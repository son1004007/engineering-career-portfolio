package io.github.son1004007.opsmate.agent;

import java.util.List;

public record DraftAgentResult(DraftProposal proposal, List<PolicyEvidence> policyEvidence) {

    public DraftAgentResult {
        policyEvidence = List.copyOf(policyEvidence);
    }
}
