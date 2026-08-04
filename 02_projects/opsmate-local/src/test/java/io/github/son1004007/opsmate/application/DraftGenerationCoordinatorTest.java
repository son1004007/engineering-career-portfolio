package io.github.son1004007.opsmate.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseCategory;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import org.junit.jupiter.api.Test;

class DraftGenerationCoordinatorTest {

    private static final Instant NOW = Instant.parse("2026-08-04T00:00:00Z");

    @Test
    void sameInitialKeyUsesOneModelCallForTwentyConcurrentRequests() throws Exception {
        ModelGuardProperties properties = properties(100, Duration.ofSeconds(1));
        DraftGenerationCoordinator coordinator = coordinator(properties);
        UUID workspaceId = UUID.randomUUID();
        AtomicInteger invocations = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch ownerEntered = new CountDownLatch(1);
        CountDownLatch releaseOwner = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(20);
        try {
            List<CompletableFuture<PurchaseRequest>> calls = new ArrayList<>();
            for (int index = 0; index < 20; index++) {
                calls.add(CompletableFuture.supplyAsync(() -> {
                    await(start);
                    return coordinator.execute(workspaceId, "requester", "same-key-001", () -> {
                        invocations.incrementAndGet();
                        ownerEntered.countDown();
                        await(releaseOwner);
                        return request(workspaceId, "same-key-001");
                    });
                }, executor));
            }

            start.countDown();
            assertThat(ownerEntered.await(2, TimeUnit.SECONDS)).isTrue();
            Thread.sleep(100);
            releaseOwner.countDown();

            List<PurchaseRequest> results = calls.stream().map(CompletableFuture::join).toList();
            assertThat(invocations).hasValue(1);
            assertThat(results).extracting(PurchaseRequest::getId).containsOnly(results.getFirst().getId());
            assertThat(coordinator.activeFlightCount()).isZero();
            assertThat(coordinator.availableModelPermits()).isEqualTo(1);
        } finally {
            releaseOwner.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    void workspaceRateLimitStopsBeforeAnotherModelCall() {
        DraftGenerationCoordinator coordinator = coordinator(properties(2, Duration.ofSeconds(1)));
        UUID workspaceId = UUID.randomUUID();

        coordinator.execute(workspaceId, "requester", "rate-key-001", () -> request(workspaceId, "rate-key-001"));
        coordinator.execute(workspaceId, "requester", "rate-key-002", () -> request(workspaceId, "rate-key-002"));

        assertThatThrownBy(() -> coordinator.execute(
                workspaceId,
                "requester",
                "rate-key-003",
                () -> request(workspaceId, "rate-key-003")))
                .isInstanceOf(OpsMateException.class)
                .satisfies(exception -> assertThat(((OpsMateException) exception).getCode())
                        .isEqualTo(ErrorCode.RATE_LIMITED));
    }

    @Test
    void globalRateLimitSurvivesWorkspaceResetAndStopsUnboundedGpuUse() {
        ModelGuardProperties properties = properties(10, Duration.ofSeconds(1));
        properties.setMaxRequestsGlobal(2);
        DraftGenerationCoordinator coordinator = coordinator(properties);
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID third = UUID.randomUUID();

        coordinator.execute(first, "requester", "global-key-001", () -> request(first, "global-key-001"));
        coordinator.forgetWorkspace(first);
        coordinator.execute(second, "requester", "global-key-002", () -> request(second, "global-key-002"));
        coordinator.forgetWorkspace(second);

        assertThatThrownBy(() -> coordinator.execute(
                third,
                "requester",
                "global-key-003",
                () -> request(third, "global-key-003")))
                .isInstanceOf(OpsMateException.class)
                .satisfies(exception -> assertThat(((OpsMateException) exception).getCode())
                        .isEqualTo(ErrorCode.RATE_LIMITED));
    }

    @Test
    void nonPositiveDurationIsRejectedAtStartup() {
        ModelGuardProperties properties = properties(10, Duration.ZERO);

        assertThatThrownBy(() -> coordinator(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("positive");
    }

    @Test
    void differentRequestGetsBusyWhenGlobalPermitCannotBeAcquired() throws Exception {
        DraftGenerationCoordinator coordinator = coordinator(properties(100, Duration.ofMillis(50)));
        UUID workspaceId = UUID.randomUUID();
        CountDownLatch ownerEntered = new CountDownLatch(1);
        CountDownLatch releaseOwner = new CountDownLatch(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            CompletableFuture<PurchaseRequest> owner = CompletableFuture.supplyAsync(() -> coordinator.execute(
                    workspaceId,
                    "requester",
                    "busy-key-001",
                    () -> {
                        ownerEntered.countDown();
                        await(releaseOwner);
                        return request(workspaceId, "busy-key-001");
                    }), executor);
            assertThat(ownerEntered.await(2, TimeUnit.SECONDS)).isTrue();

            assertThatThrownBy(() -> coordinator.execute(
                    workspaceId,
                    "requester",
                    "busy-key-002",
                    () -> request(workspaceId, "busy-key-002")))
                    .isInstanceOf(OpsMateException.class)
                    .satisfies(exception -> assertThat(((OpsMateException) exception).getCode())
                            .isEqualTo(ErrorCode.MODEL_BUSY));

            releaseOwner.countDown();
            assertThat(owner.get(2, TimeUnit.SECONDS)).isNotNull();
        } finally {
            releaseOwner.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    void excessFollowersAreRejectedInsteadOfBlockingWorkerThreads() throws Exception {
        ModelGuardProperties properties = properties(100, Duration.ofSeconds(1));
        properties.setMaxFollowersPerFlight(1);
        DraftGenerationCoordinator coordinator = coordinator(properties);
        UUID workspaceId = UUID.randomUUID();
        CountDownLatch ownerEntered = new CountDownLatch(1);
        CountDownLatch releaseOwner = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            CompletableFuture<PurchaseRequest> owner = CompletableFuture.supplyAsync(() -> coordinator.execute(
                    workspaceId,
                    "requester",
                    "follower-key-001",
                    () -> {
                        ownerEntered.countDown();
                        await(releaseOwner);
                        return request(workspaceId, "follower-key-001");
                    }), executor);
            assertThat(ownerEntered.await(2, TimeUnit.SECONDS)).isTrue();
            CompletableFuture<PurchaseRequest> follower = CompletableFuture.supplyAsync(() -> coordinator.execute(
                    workspaceId,
                    "requester",
                    "follower-key-001",
                    () -> request(workspaceId, "follower-key-001")), executor);
            Thread.sleep(50);

            assertThatThrownBy(() -> coordinator.execute(
                    workspaceId,
                    "requester",
                    "follower-key-001",
                    () -> request(workspaceId, "follower-key-001")))
                    .isInstanceOf(OpsMateException.class)
                    .satisfies(exception -> assertThat(((OpsMateException) exception).getCode())
                            .isEqualTo(ErrorCode.MODEL_BUSY));

            releaseOwner.countDown();
            assertThat(owner.get(2, TimeUnit.SECONDS)).isNotNull();
            assertThat(follower.get(2, TimeUnit.SECONDS)).isNotNull();
        } finally {
            releaseOwner.countDown();
            executor.shutdownNow();
        }
    }

    private DraftGenerationCoordinator coordinator(ModelGuardProperties properties) {
        return new DraftGenerationCoordinator(
                properties,
                Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private ModelGuardProperties properties(int workspaceLimit, Duration queueWait) {
        ModelGuardProperties properties = new ModelGuardProperties();
        properties.setMaxConcurrent(1);
        properties.setMaxRequestsPerWorkspace(workspaceLimit);
        properties.setMaxRequestsGlobal(100);
        properties.setMaxFollowersPerFlight(25);
        properties.setFlightWait(Duration.ofSeconds(3));
        properties.setWorkspaceWindow(Duration.ofHours(1));
        properties.setQueueWait(queueWait);
        return properties;
    }

    private PurchaseRequest request(UUID workspaceId, String key) {
        return PurchaseRequest.draft(
                workspaceId,
                "개발용 노트북 1대 구매",
                "fingerprint",
                "개발용 노트북 구매",
                "개발 환경 개선",
                new BigDecimal("2500000"),
                "KRW",
                PurchaseCategory.IT_EQUIPMENT,
                Set.of("POL-IT-001"),
                "requester",
                key,
                NOW);
    }

    private void await(CountDownLatch latch) {
        try {
            if (!latch.await(3, TimeUnit.SECONDS)) {
                throw new AssertionError("Timed out while coordinating the concurrency test");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError(exception);
        }
    }
}
