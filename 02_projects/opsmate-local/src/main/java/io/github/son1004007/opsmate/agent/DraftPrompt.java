package io.github.son1004007.opsmate.agent;

import java.util.List;

public record DraftPrompt(String requestText, List<PolicyEvidence> policyEvidence) {

    public DraftPrompt {
        policyEvidence = List.copyOf(policyEvidence);
    }
}
