---
title: "기술 사례"
page_description: "Java/Spring 백엔드와 AI 기능 통합 과정에서 마주한 문제, 설계 선택과 구현 결과를 정리한 기술 사례"
permalink: /blog/
---

<header class="page-hero shell">
  <p class="eyebrow">Engineering cases</p>
  <h1>기술 사례</h1>
  <p>실무와 개인 프로젝트에서 마주한 문제, 선택한 설계와 구현 결과를 정리했습니다. 회사 코드와 데이터는 포함하지 않습니다.</p>
</header>

<section class="section section--soft" aria-labelledby="implemented-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Code & tests</p>
        <h2 id="implemented-title">코드와 테스트로 확인할 수 있는 작업</h2>
      </div>
      <p>독립적으로 구현한 코드와 최근 자동화 테스트 결과를 함께 공개합니다.</p>
    </div>
    <div class="card-grid">
      {% include case-card.html id="CS-JAVA-01" status="sample-verified" title="DB 로그인과 레거시 SSO 권한 통합" description="두 인증 경로를 하나의 사용자·권한·세션 정책으로 통합하고 24개 테스트로 확인한 Spring Security 사례" focus="Spring Security, RBAC, 세션·CSRF, assertion 검증" href="/cases/spring-security-auth-bridge/" %}
      <article class="case-card">
        <div class="case-card__topline">{% include status-badge.html status="tested-component" %}</div>
        <h3><a href="{{ '/02_projects/opsmate-local/' | relative_url }}">OpsMate Local</a></h3>
        <p>구매 요청, 정책 조회, 승인·반려와 발주 흐름에 AI 초안 생성을 연결한 Spring Boot 프로젝트</p>
        <p class="case-card__focus"><strong>핵심:</strong> 권한, 상태 전이, 멱등성, 모델 오류 처리 · 19개 테스트</p>
        <a class="text-link" href="{{ '/02_projects/opsmate-local/' | relative_url }}">프로젝트 보기 <span aria-hidden="true">→</span></a>
      </article>
    </div>
  </div>
</section>

<section class="section shell" aria-labelledby="experience-cases-title">
  <div class="section-heading">
    <div>
      <p class="eyebrow">Work experience</p>
      <h2 id="experience-cases-title">실무에서 다룬 문제</h2>
    </div>
    <p>직접 담당한 범위에서 문제와 판단을 일반화한 기록입니다. 회사 원본 코드는 공개하지 않습니다.</p>
  </div>
  <div class="card-grid">
    {% include case-card.html id="CS-JAVA-02" status="source-reviewed" title="MyBatis 조회의 정합성과 성능" description="복잡한 조회 조건에서 결과 누락·중복을 막으면서 인덱스 사용성을 함께 검토한 사례" href="/cases/mybatis-query-correctness/" %}
    {% include case-card.html id="CS-JAVA-03" status="source-reviewed" title="WAR 서비스의 배포 이식성" description="Context path와 환경 설정 차이를 통제하고 health 확인과 rollback 흐름을 정리한 사례" href="/cases/war-deployment-portability/" %}
    {% include case-card.html id="CS-AI-01" status="source-reviewed" title="검증 가능한 Text2SQL" description="SQL 생성과 정책 검증, DB 실행과 정답 판단을 분리해 로컬 모델을 비교한 사례" href="/cases/text2sql-validation/" %}
    {% include case-card.html id="CS-AI-02" status="source-reviewed" title="Agent Runtime 산출물 추적" description="작업 공간을 격리하고 artifact·manifest·provenance를 연결한 백엔드 구성요소 사례" href="/cases/agent-runtime-artifact-provenance/" %}
  </div>
</section>

{% if site.posts.size > 0 %}
<section class="section section--soft" aria-labelledby="notes-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Technical notes</p>
        <h2 id="notes-title">기술 메모</h2>
      </div>
      <p>구현 과정에서 선택한 책임 경계와 trade-off를 설명합니다.</p>
    </div>
    <div class="post-list">
      {% for post in site.posts %}
        {% include post-card.html post=post %}
      {% endfor %}
    </div>
  </div>
</section>
{% endif %}
