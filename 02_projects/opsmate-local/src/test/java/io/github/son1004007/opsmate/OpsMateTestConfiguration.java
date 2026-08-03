package io.github.son1004007.opsmate;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.github.son1004007.opsmate.agent.DraftPrompt;
import io.github.son1004007.opsmate.agent.DraftProposal;
import io.github.son1004007.opsmate.agent.LocalOpenWeightLlmGateway;
import io.github.son1004007.opsmate.application.OrderPostPersistHook;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseCategory;
import io.github.son1004007.opsmate.domain.PurchaseOrder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class OpsMateTestConfiguration {

    @Bean
    @Primary
    StubLocalLlmGateway stubLocalLlmGateway() {
        return new StubLocalLlmGateway();
    }

    @Bean
    @Primary
    ControllableOrderPostPersistHook controllableOrderPostPersistHook() {
        return new ControllableOrderPostPersistHook();
    }

    static final class StubLocalLlmGateway implements LocalOpenWeightLlmGateway {

        private final AtomicBoolean unavailable = new AtomicBoolean();
        private final AtomicReference<DraftProposal> proposal = new AtomicReference<>();

        StubLocalLlmGateway() {
            reset();
        }

        @Override
        public DraftProposal propose(DraftPrompt prompt) {
            if (unavailable.get()) {
                throw new OpsMateException(ErrorCode.MODEL_UNAVAILABLE, "Synthetic model outage");
            }
            return proposal.get();
        }

        void failModel() {
            unavailable.set(true);
        }

        void returnProposal(DraftProposal next) {
            proposal.set(next);
        }

        void reset() {
            unavailable.set(false);
            proposal.set(new DraftProposal(
                    "개발용 노트북 구매",
                    "개발 환경 개선",
                    new BigDecimal("2500000"),
                    "KRW",
                    PurchaseCategory.IT_EQUIPMENT,
                    List.of("POL-IT-001")));
        }
    }

    static final class ControllableOrderPostPersistHook implements OrderPostPersistHook {

        private final AtomicBoolean fail = new AtomicBoolean();

        @Override
        public void afterPersist(PurchaseOrder order) {
            if (fail.get()) {
                throw new OpsMateException(
                        ErrorCode.ORDER_FINALIZATION_FAILED,
                        "Synthetic post-persist failure");
            }
        }

        void failNext() {
            fail.set(true);
        }

        void reset() {
            fail.set(false);
        }
    }
}
