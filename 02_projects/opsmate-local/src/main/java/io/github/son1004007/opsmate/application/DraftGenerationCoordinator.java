package io.github.son1004007.opsmate.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import org.springframework.stereotype.Component;

/**
 * 공개 데모의 제한된 GPU를 보호하면서 동일 최초 요청을 한 번만 실행한다.
 *
 * <p><strong>single-flight:</strong> 한 애플리케이션 인스턴스 안에서 같은
 * workspace·actor·멱등키의 동시 요청은 하나의 future를 공유한다.
 *
 * <p><strong>자원 제한:</strong> workspace별 시간 창 호출 한도와 전체 semaphore를
 * 모델 호출 전에 적용한다. 다중 인스턴스에서는 이 in-memory 통제가 충분하지 않으므로
 * PostgreSQL 또는 별도 조정 저장소로 교체하기 전에는 수평 확장하지 않는다.
 */
@Component
public class DraftGenerationCoordinator {

    private final ConcurrentHashMap<FlightKey, Flight> flights =
            new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, WindowCounter> workspaceCounters = new ConcurrentHashMap<>();
    private final ModelGuardProperties properties;
    private final Clock clock;
    private final Semaphore modelPermits;
    private final WindowCounter globalCounter;

    public DraftGenerationCoordinator(ModelGuardProperties properties, Clock clock) {
        properties.validate();
        this.properties = properties;
        this.clock = clock;
        this.modelPermits = new Semaphore(properties.getMaxConcurrent(), true);
        this.globalCounter = new WindowCounter(clock.instant().plus(properties.getWorkspaceWindow()));
    }

    /**
     * 같은 key의 동시 호출은 소유자 한 건만 실행하고 나머지는 그 결과를 기다린다.
     * 완료된 flight는 즉시 제거하므로 이후 재시도는 DB 멱등 조회가 처리한다.
     */
    public PurchaseRequest execute(
            UUID workspaceId,
            String actor,
            String idempotencyKey,
            Supplier<PurchaseRequest> action) {
        FlightKey key = new FlightKey(workspaceId, actor, idempotencyKey);
        Flight mine = new Flight(properties.getMaxFollowersPerFlight());
        Flight active = flights.putIfAbsent(key, mine);
        if (active != null) {
            return await(active);
        }

        try {
            PurchaseRequest result = invokeWithinLimits(workspaceId, action);
            mine.result.complete(result);
            return result;
        } catch (RuntimeException exception) {
            mine.result.completeExceptionally(exception);
            throw exception;
        } finally {
            flights.remove(key, mine);
        }
    }

    private PurchaseRequest invokeWithinLimits(UUID workspaceId, Supplier<PurchaseRequest> action) {
        requireWorkspaceQuota(workspaceId);
        // 새 workspace를 계속 만들어도 GPU 사용량이 무한 초기화되지 않도록 process 전체 한도를 별도로 차감한다.
        requireQuota(globalCounter, properties.getMaxRequestsGlobal(), "The public demo reached its model limit");
        boolean acquired;
        try {
            acquired = modelPermits.tryAcquire(
                    properties.getQueueWait().toMillis(),
                    TimeUnit.MILLISECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new OpsMateException(ErrorCode.MODEL_BUSY, "Model queue wait was interrupted", exception);
        }
        if (!acquired) {
            throw new OpsMateException(ErrorCode.MODEL_BUSY, "The local model is busy; retry later");
        }
        try {
            return action.get();
        } finally {
            modelPermits.release();
        }
    }

    private void requireWorkspaceQuota(UUID workspaceId) {
        Instant now = clock.instant();
        WindowCounter counter = workspaceCounters.computeIfAbsent(
                workspaceId,
                ignored -> new WindowCounter(now.plus(properties.getWorkspaceWindow())));
        requireQuota(counter, properties.getMaxRequestsPerWorkspace(),
                "The demo workspace reached its model request limit");
    }

    private void requireQuota(WindowCounter counter, int maximum, String message) {
        Instant now = clock.instant();
        synchronized (counter) {
            if (!now.isBefore(counter.windowEndsAt)) {
                counter.windowEndsAt = now.plus(properties.getWorkspaceWindow());
                counter.count = 0;
            }
            if (counter.count >= maximum) {
                throw new OpsMateException(ErrorCode.RATE_LIMITED, message);
            }
            counter.count++;
        }
    }

    private PurchaseRequest await(Flight active) {
        if (!active.followerPermits.tryAcquire()) {
            throw new OpsMateException(ErrorCode.MODEL_BUSY, "Too many requests are waiting for the same result");
        }
        try {
            return active.result.get(properties.getFlightWait().toMillis(), TimeUnit.MILLISECONDS);
        } catch (ExecutionException | CompletionException exception) {
            if (exception.getCause() instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new OpsMateException(ErrorCode.MODEL_UNAVAILABLE, "Model flight failed", exception);
        } catch (TimeoutException exception) {
            throw new OpsMateException(ErrorCode.MODEL_BUSY, "Waiting for the model result timed out", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new OpsMateException(ErrorCode.MODEL_BUSY, "Waiting for the model result was interrupted", exception);
        } finally {
            active.followerPermits.release();
        }
    }

    int activeFlightCount() {
        return flights.size();
    }

    int availableModelPermits() {
        return modelPermits.availablePermits();
    }

    /** workspace 삭제 뒤 장시간 프로세스에 호출 한도 상태가 누적되지 않게 정리한다. */
    public void forgetWorkspace(UUID workspaceId) {
        workspaceCounters.remove(workspaceId);
    }

    private record FlightKey(UUID workspaceId, String actor, String idempotencyKey) {
    }

    private static final class WindowCounter {
        private Instant windowEndsAt;
        private int count;

        private WindowCounter(Instant windowEndsAt) {
            this.windowEndsAt = windowEndsAt;
        }
    }

    private static final class Flight {
        private final CompletableFuture<PurchaseRequest> result = new CompletableFuture<>();
        private final Semaphore followerPermits;

        private Flight(int maximumFollowers) {
            this.followerPermits = new Semaphore(maximumFollowers);
        }
    }
}
