package io.github.son1004007.opsmate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.sql.DriverManager;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import io.github.son1004007.opsmate.OpsMateTestConfiguration.StubLocalLlmGateway;
import io.github.son1004007.opsmate.agent.DraftAgentResult;
import io.github.son1004007.opsmate.agent.PurchaseDraftAgent;
import io.github.son1004007.opsmate.application.AuditQueryService;
import io.github.son1004007.opsmate.application.DraftPersistenceService;
import io.github.son1004007.opsmate.application.PurchaseOrderService;
import io.github.son1004007.opsmate.application.PurchaseRequestService;
import io.github.son1004007.opsmate.demo.DemoPersona;
import io.github.son1004007.opsmate.demo.DemoPrincipal;
import io.github.son1004007.opsmate.demo.DemoWorkspace;
import io.github.son1004007.opsmate.demo.DemoWorkspaceRepository;
import io.github.son1004007.opsmate.demo.DemoWorkspaceService;
import io.github.son1004007.opsmate.domain.ApprovalDecision;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseOrder;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import io.github.son1004007.opsmate.infrastructure.persistence.AuditEventRepository;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseOrderRepository;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseRequestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

/**
 * 배포와 같은 PostgreSQL 역할 분리·Flyway schema에서 실제 업무 CRUD와 cascade를 확인한다.
 * H2 컴포넌트 테스트만 통과하고 PostgreSQL 제약에서 실패하는 회귀를 별도 경계로 막는다.
 */
@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=validate",
        "opsmate.security.basic-enabled=false",
        "opsmate.demo.enabled=true",
        "opsmate.demo.start-enabled=true",
        "opsmate.demo.max-active-workspaces=10",
        "opsmate.llm.enabled=false"
})
@Testcontainers(disabledWithoutDocker = true)
@Import(OpsMateTestConfiguration.class)
class PostgreSqlMigrationIntegrationTest {

    private static final String LAPTOP_REQUEST = "개발용 노트북 1대를 2500000원에 구매하고 싶습니다.";
    private static final String DATABASE = "opsmate";
    private static final String ADMIN_USER = "opsmate_admin";
    private static final String ADMIN_PASSWORD = "test-only-admin-password-0001";
    private static final String MIGRATION_USER = "opsmate_migration";
    private static final String MIGRATION_PASSWORD = "test-only-migration-password-0001";
    private static final String APP_USER = "opsmate_app";
    private static final String APP_PASSWORD = "test-only-runtime-password-0001";

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16.8-alpine")
            .withDatabaseName(DATABASE)
            .withUsername(ADMIN_USER)
            .withPassword(ADMIN_PASSWORD)
            .withEnv("OPSMATE_DB_MIGRATION_USER", MIGRATION_USER)
            .withEnv("OPSMATE_DB_MIGRATION_PASSWORD", MIGRATION_PASSWORD)
            .withEnv("OPSMATE_DB_APP_USER", APP_USER)
            .withEnv("OPSMATE_DB_APP_PASSWORD", APP_PASSWORD)
            .withCopyFileToContainer(
                    MountableFile.forHostPath(
                            Path.of("deploy/postgres/init-roles.sh").toAbsolutePath(),
                            0755),
                    "/docker-entrypoint-initdb.d/10-init-opsmate-roles.sh");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        // 배포 컨테이너와 같은 one-shot command로 먼저 migration을 끝낸 뒤,
        // 장기 실행 Spring context에는 runtime credential만 전달한다.
        MigrationCommand.run(migrationEnvironment());
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", () -> APP_USER);
        registry.add("spring.datasource.password", () -> APP_PASSWORD);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DemoWorkspaceRepository workspaceRepository;

    @Autowired
    PurchaseRequestRepository requestRepository;

    @Autowired
    PurchaseOrderRepository orderRepository;

    @Autowired
    AuditEventRepository auditRepository;

    @Autowired
    DemoWorkspaceService workspaceService;

    @Autowired
    PurchaseRequestService requestService;

    @Autowired
    PurchaseOrderService orderService;

    @Autowired
    AuditQueryService auditQueryService;

    @Autowired
    StubLocalLlmGateway llmGateway;

    @Autowired
    PurchaseDraftAgent draftAgent;

    @Autowired
    DraftPersistenceService draftPersistenceService;

    @BeforeEach
    void clearSyntheticRows() {
        llmGateway.reset();
        orderRepository.deleteAll();
        auditRepository.deleteAll();
        requestRepository.deleteAll();
        workspaceRepository.deleteAll();
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void migrationRoleOwnsSchemaWhileRuntimeRoleHasOnlyBusinessDml() throws Exception {
        // 이미 최신 schema에서도 같은 command를 다시 실행할 수 있어야 재배포가 안전하다.
        MigrationCommand.run(migrationEnvironment());
        try (var connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), MIGRATION_USER, MIGRATION_PASSWORD);
                var statement = connection.createStatement();
                var result = statement.executeQuery(
                        "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true")) {
            assertThat(result.next()).isTrue();
            assertThat(result.getInt(1)).isEqualTo(2);
        }

        assertThat(jdbcTemplate.queryForObject("SELECT current_user", String.class)).isEqualTo(APP_USER);
        DemoWorkspace workspace = workspaceRepository.saveAndFlush(DemoWorkspace.active(
                Instant.parse("2026-08-04T00:00:00Z"),
                Duration.ofMinutes(30)));
        assertThat(workspaceRepository.findById(workspace.getId())).isPresent();

        Integer workspaceColumns = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns "
                        + "WHERE table_schema = 'public' AND table_name = 'purchase_requests' "
                        + "AND column_name = 'workspace_id'",
                Integer.class);
        assertThat(workspaceColumns).isEqualTo(1);
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history", Integer.class))
                .isInstanceOf(DataAccessException.class);
        assertThatThrownBy(() -> jdbcTemplate.execute("CREATE TABLE forbidden_runtime_ddl(id integer)"))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void requesterToAuditorFlowAndWorkspaceCascadeRunOnPostgreSql() {
        DemoWorkspace workspace = workspaceService.start();
        authenticate(workspace, DemoPersona.REQUESTER);
        PurchaseRequest request = requestService.createDraft(
                "postgres-draft-001",
                LAPTOP_REQUEST);
        requestService.submit(request.getId());

        authenticate(workspace, DemoPersona.APPROVER);
        requestService.decide(request.getId(), ApprovalDecision.APPROVE, null);
        authenticate(workspace, DemoPersona.BUYER);
        PurchaseOrder order = orderService.createOrder(request.getId(), "postgres-order-001");
        authenticate(workspace, DemoPersona.AUDITOR);

        assertThat(order.getPurchaseRequestId()).isEqualTo(request.getId());
        assertThat(auditQueryService.findAll()).hasSize(4);
        assertThat(requestRepository.countByWorkspaceId(workspace.getId())).isEqualTo(1);
        assertThat(orderRepository.countByWorkspaceId(workspace.getId())).isEqualTo(1);

        // 애플리케이션 명시 삭제와 별도로 PostgreSQL FK의 ON DELETE CASCADE 자체를 검증한다.
        workspaceRepository.deleteById(workspace.getId());
        workspaceRepository.flush();
        assertThat(requestRepository.countByWorkspaceId(workspace.getId())).isZero();
        assertThat(orderRepository.countByWorkspaceId(workspace.getId())).isZero();
        assertThat(auditRepository.countByWorkspaceId(workspace.getId())).isZero();
    }

    @Test
    void databaseRejectsOrderThatReferencesAnotherWorkspaceRequest() {
        DemoWorkspace firstWorkspace = workspaceService.start();
        DemoWorkspace secondWorkspace = workspaceService.start();
        PurchaseRequest secondRequest = requestRepository.saveAndFlush(PurchaseRequest.draft(
                secondWorkspace.getId(),
                LAPTOP_REQUEST,
                "cross-workspace-fingerprint",
                "교차 workspace 방어 테스트",
                "데이터 격리 제약 검증",
                new java.math.BigDecimal("2500000"),
                "KRW",
                io.github.son1004007.opsmate.domain.PurchaseCategory.IT_EQUIPMENT,
                java.util.Set.of("POL-IT-001"),
                "requester-cross-workspace",
                "cross-workspace-request-key",
                Instant.now()));

        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO purchase_orders "
                        + "(id, workspace_id, order_number, purchase_request_id, created_by, "
                        + "idempotency_key, request_fingerprint, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
                java.util.UUID.randomUUID(),
                firstWorkspace.getId(),
                "PO-CROSS-BOUNDARY",
                secondRequest.getId(),
                "buyer-cross-workspace",
                "cross-workspace-order-key",
                "cross-workspace-fingerprint"))
                .isInstanceOf(DataAccessException.class);
        assertThat(orderRepository.countByWorkspaceId(firstWorkspace.getId())).isZero();
    }

    @Test
    void writeBoundaryRejectsWorkspaceClosedAfterModelResultWasProduced() {
        DemoWorkspace workspace = workspaceService.start();
        DraftAgentResult result = draftAgent.createDraft(LAPTOP_REQUEST);
        workspace.close();
        workspaceRepository.saveAndFlush(workspace);

        assertThatThrownBy(() -> draftPersistenceService.persistDraft(
                workspace.getId(),
                "requester-expired-after-model",
                "expired-after-model-key",
                LAPTOP_REQUEST,
                "expired-after-model-fingerprint",
                result))
                .isInstanceOfSatisfying(OpsMateException.class, exception ->
                        assertThat(exception.getCode()).isEqualTo(ErrorCode.SESSION_EXPIRED));
        assertThat(requestRepository.countByWorkspaceId(workspace.getId())).isZero();
        assertThat(auditRepository.countByWorkspaceId(workspace.getId())).isZero();
    }

    private void authenticate(DemoWorkspace workspace, DemoPersona persona) {
        DemoPrincipal principal = new DemoPrincipal(workspace.getId(), persona);
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        principal,
                        null,
                        principal.authorities()));
    }

    private static Map<String, String> migrationEnvironment() {
        return Map.of(
                "OPSMATE_DB_URL", POSTGRES.getJdbcUrl(),
                "OPSMATE_FLYWAY_USERNAME", MIGRATION_USER,
                "OPSMATE_FLYWAY_PASSWORD", MIGRATION_PASSWORD,
                "OPSMATE_FLYWAY_APP_ROLE", APP_USER);
    }
}
