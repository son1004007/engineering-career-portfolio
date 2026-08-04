package io.github.son1004007.opsmate.infrastructure.llm;

import io.github.son1004007.opsmate.agent.DraftPrompt;
import io.github.son1004007.opsmate.agent.DraftProposal;
import io.github.son1004007.opsmate.agent.LocalOpenWeightLlmGateway;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;

/** 모델이 비활성·미구성 상태일 때 가짜 결과나 외부 API fallback 없이 즉시 중단하는 adapter. */
public class FailClosedLocalLlmGateway implements LocalOpenWeightLlmGateway {

    @Override
    public DraftProposal propose(DraftPrompt prompt) {
        throw new OpsMateException(
                ErrorCode.MODEL_UNAVAILABLE,
                "Local open-weight model gateway is disabled or unavailable");
    }
}
