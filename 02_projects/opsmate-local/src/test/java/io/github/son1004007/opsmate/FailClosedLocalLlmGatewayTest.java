package io.github.son1004007.opsmate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import io.github.son1004007.opsmate.agent.DraftPrompt;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.infrastructure.llm.FailClosedLocalLlmGateway;
import org.junit.jupiter.api.Test;

class FailClosedLocalLlmGatewayTest {

    @Test
    void disabledGatewayNeverCreatesAProposal() {
        FailClosedLocalLlmGateway gateway = new FailClosedLocalLlmGateway();

        assertThatThrownBy(() -> gateway.propose(new DraftPrompt("노트북 구매", List.of())))
                .isInstanceOf(OpsMateException.class)
                .extracting(exception -> ((OpsMateException) exception).getCode())
                .isEqualTo(ErrorCode.MODEL_UNAVAILABLE);
    }
}
