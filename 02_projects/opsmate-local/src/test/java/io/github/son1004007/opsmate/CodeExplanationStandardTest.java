package io.github.son1004007.opsmate;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/** 핵심 업무·보안 경계의 이유형 설명이 리팩터링 중 사라지지 않게 확인한다. */
class CodeExplanationStandardTest {

    @Test
    void criticalBusinessBoundariesKeepKoreanDesignExplanations() throws IOException {
        Map<String, List<String>> requiredTerms = Map.ofEntries(
                Map.entry("application/PurchaseRequestService.java", List.of("멱등", "single-flight", "DB 고유 제약")),
                Map.entry("application/OrderPersistenceService.java", List.of("원자적", "롤백", "outbox")),
                Map.entry("application/DraftGenerationCoordinator.java", List.of("single-flight", "동시", "다중 인스턴스")),
                Map.entry("application/PurchaseRequestReadPolicy.java", List.of("workspace", "객체 존재 여부")),
                Map.entry("application/AuditRecorder.java", List.of("transaction", "부분 성공", "credential")),
                Map.entry("agent/PurchaseDraftAgent.java", List.of("신뢰 경계", "서버가 다시 검증", "DB 무변경")),
                Map.entry("agent/SyntheticPolicyEvidenceTool.java", List.of("합성 정책", "금액 한도", "경계값")),
                Map.entry("infrastructure/llm/OllamaLocalLlmGateway.java", List.of("고정된", "unknown field", "fallback")),
                Map.entry("infrastructure/llm/FailClosedLocalLlmGateway.java", List.of("가짜 결과", "fallback", "중단")),
                Map.entry("config/SecurityConfig.java", List.of("공개 demo", "CSRF", "Basic")),
                Map.entry("demo/DemoWorkspaceService.java", List.of("workspace", "합성 데이터", "삭제")));

        Path sourceRoot = Path.of("src/main/java/io/github/son1004007/opsmate");
        for (Map.Entry<String, List<String>> entry : requiredTerms.entrySet()) {
            String source = Files.readString(sourceRoot.resolve(entry.getKey()));
            assertThat(source)
                    .as("%s should contain a type or method Javadoc", entry.getKey())
                    .contains("/**");
            assertThat(source)
                    .as("%s should explain its business or security boundary", entry.getKey())
                    .contains(entry.getValue().toArray(String[]::new));
        }
    }
}
