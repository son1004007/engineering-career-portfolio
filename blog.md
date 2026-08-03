---
title: "실무 사례와 설계 기록"
page_description: "Java/Spring 실무 사례와 AI Agent 프로젝트의 문제, 선택, 검증 상태를 기록합니다."
permalink: /blog/
---

<header class="page-hero shell">
  <p class="eyebrow">Engineering notes</p>
  <h1>실무 사례와 설계 기록</h1>
  <p>완료된 것처럼 보이게 만드는 글보다, 현재 근거와 다음 검증 단계를 분명하게 보여주는 글을 남깁니다.</p>
  <div class="status-legend" aria-label="상태 배지 설명">
    {% include status-badge.html status="planned" %}
    {% include status-badge.html status="source-reviewed" %}
    {% include status-badge.html status="tested-component" %}
    {% include status-badge.html status="sample-verified" %}
  </div>
</header>

<section class="section section--soft" aria-labelledby="cases-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Reviewed cases</p>
        <h2 id="cases-title">비식별 실무 사례</h2>
      </div>
      <p>비공개 원본에서 확인한 개인 기여 범위만 서술합니다. 각 글의 한계와 공개 재현 계획도 함께 표시합니다.</p>
    </div>
    <div class="card-grid">
      {% include case-card.html id="CS-JAVA-01" status="sample-verified" title="Spring Security 인증 통합" description="DB 로그인과 issuer·audience 바인딩 SSO를 로컬 RBAC·세션·CSRF 정책으로 통합하고 24개 테스트로 검증한 독립 사례" href="/cases/spring-security-auth-bridge/" %}
      {% include case-card.html id="CS-JAVA-02" status="source-reviewed" title="MyBatis 조회의 정합성과 성능" description="복잡한 조건의 결과 정합성과 인덱스 사용성을 함께 다룬 사례" href="/cases/mybatis-query-correctness/" %}
      {% include case-card.html id="CS-JAVA-03" status="source-reviewed" title="WAR 서비스의 배포 이식성" description="Context path와 환경 차이를 profile·health·rollback 관점에서 정리한 사례" href="/cases/war-deployment-portability/" %}
      {% include case-card.html id="CS-JAVA-06" status="source-reviewed" title="분산된 업무 규칙 정합화" description="Controller·Service·Mapper에 흩어진 식별 규칙을 한 흐름으로 정리한 사례" href="/cases/business-rule-consistency/" %}
      {% include case-card.html id="CS-JAVA-11" status="source-reviewed" title="통계 품질 분석 화면" description="CSV·Excel 측정값과 Xbar-R 시각화를 Java/Spring 화면에 연결한 사례" href="/cases/statistical-analysis-ui/" %}
      {% include case-card.html id="CS-AI-01" status="source-reviewed" title="검증 가능한 Text2SQL" description="생성보다 SELECT 제한·schema 검증·실패 분리에 초점을 둔 AI 응용 사례" href="/cases/text2sql-validation/" %}
      {% include case-card.html id="CS-AI-02" status="source-reviewed" title="Agent Runtime 산출물 추적" description="Workspace 격리와 artifact·manifest·provenance를 다룬 구성요소 사례" href="/cases/agent-runtime-artifact-provenance/" %}
    </div>
  </div>
</section>

<section class="section shell" aria-labelledby="posts-title">
  <div class="section-heading">
    <div>
      <p class="eyebrow">Notes</p>
      <h2 id="posts-title">설계와 검증 기록</h2>
    </div>
    <p>포트폴리오를 구성한 이유와 상태를 올리는 기준을 설명합니다.</p>
  </div>
  <div class="post-list">
    {% for post in site.posts %}
      {% include post-card.html post=post %}
    {% else %}
      <p>아직 공개된 기록이 없습니다.</p>
    {% endfor %}
  </div>
</section>
