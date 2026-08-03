package io.github.son1004007.opsmate.agent;

import java.util.List;

public interface PolicyEvidenceTool {

    String TOOL_NAME = "policy.search";

    List<PolicyEvidence> search(PolicySearchQuery query);
}
