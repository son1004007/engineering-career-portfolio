---
title: "Java/Spring에서 AI Agent까지"
page_description: "손기석의 Java/Spring 실무 사례와 안전한 AI Agent 통합 프로젝트를 근거 상태와 함께 확인하는 포트폴리오"
permalink: /
---

<section class="hero" aria-labelledby="hero-title">
  <div class="shell hero__grid">
    <div>
      <p class="eyebrow">Backend · Platform · AI Agent</p>
      <h1 id="hero-title">업무 시스템에 <em>AI를 안전하게 연결</em>합니다.</h1>
      <p class="hero__lead">Java/Spring 엔터프라이즈 백엔드 경험을 중심에 두고, 오픈웨이트 LLM과 AI Agent를 기존 트랜잭션에 통제 가능하게 통합합니다.</p>
      <div class="button-row">
        <a class="button" href="{{ '/blog/' | relative_url }}">실무 사례 보기</a>
        <a class="button button--secondary" href="{{ '/03_portfolio/evidence-index.html' | relative_url }}">검증 근거 확인</a>
      </div>
    </div>

    <aside class="hero__panel" aria-label="포트폴리오 핵심 방향">
      <p>Current positioning</p>
      <dl>
        <div>
          <dt>Core</dt>
          <dd>Java · Spring · SQL · 운영</dd>
        </div>
        <div>
          <dt>Extension</dt>
          <dd>Open-weight LLM · Agent · RAG</dd>
        </div>
        <div>
          <dt>Proof</dt>
          <dd>상태가 구분된 코드·테스트·근거</dd>
        </div>
      </dl>
    </aside>
  </div>
</section>

<section class="section" id="about" aria-labelledby="about-title">
  <div class="shell about-grid">
    <div class="about-copy">
      <p class="eyebrow">About</p>
      <h2 id="about-title">모델 연구보다<br><strong>업무 적용의 완성도</strong>에 집중합니다.</h2>
      <p>AI가 DB나 승인 상태를 직접 바꾸게 하지 않습니다. 인증, 권한, 상태 전이, 실패 처리와 감사 가능성을 Spring 서비스의 책임으로 두는 방향을 지향합니다.</p>
    </div>
    <ol class="principles" aria-label="엔지니어링 원칙">
      <li data-index="01"><strong>근거가 먼저</strong><span>계획과 구현, 원본 검토와 공개 샘플 검증을 구분합니다.</span></li>
      <li data-index="02"><strong>업무 규칙이 최종 권한</strong><span>LLM 출력보다 서버 측 권한과 상태 전이를 우선합니다.</span></li>
      <li data-index="03"><strong>실패 시 안전하게 중단</strong><span>모델이 없거나 출력이 잘못되면 쓰기 경로를 닫습니다.</span></li>
    </ol>
  </div>
</section>

<section class="section section--soft" id="case-studies" aria-labelledby="cases-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Work cases</p>
        <h2 id="cases-title">코드에서 확인한 실무 사례</h2>
      </div>
      <p>비공개 원본과 본인 귀속을 확인한 사례입니다. 회사 코드나 내부 식별자는 공개하지 않으며, 독립 재현 샘플이 검증되기 전까지 <code>source-reviewed</code>로 표시합니다.</p>
    </div>

    <div class="card-grid">
      {% include case-card.html id="CS-JAVA-01" status="sample-verified" title="SSO와 DB 인증을 Spring Security로 통합" description="서로 다른 인증 경로를 하나의 권한·세션 흐름으로 다루고 24개 테스트로 검증한 독립 Java/Spring 사례입니다." focus="Spring Security, RBAC, 세션·CSRF, 실패 처리" href="/cases/spring-security-auth-bridge/" %}
      {% include case-card.html id="CS-JAVA-02" status="source-reviewed" title="MyBatis·Oracle 조회 정합성과 성능 개선" description="업무 조회 결과의 정합성을 지키면서 인덱스 사용성과 쿼리 구조를 함께 검토한 사례입니다." focus="MyBatis, Oracle, 회귀 조건, 실행계획" href="/cases/mybatis-query-correctness/" %}
      {% include case-card.html id="CS-JAVA-03" status="source-reviewed" title="WAR 서비스를 환경 독립적으로 배포" description="Context path와 프로필 차이를 통제해 Spring MVC/JSP 서비스를 일관되게 배포하는 사례입니다." focus="Tomcat, WAR, profile, health, rollback" href="/cases/war-deployment-portability/" %}
      {% include case-card.html id="CS-JAVA-11" status="source-reviewed" title="통계 품질 분석 화면과 Xbar-R 시각화 연결" description="Java/Spring 업무 화면에서 CSV·Excel 데이터와 통계 품질 분석 시각화를 연결한 사례입니다." focus="Java/Spring, CSV·Excel, Xbar-R, 시각화" href="/cases/statistical-analysis-ui/" %}
    </div>

    <div class="button-row">
      <a class="button button--secondary" href="{{ '/blog/' | relative_url }}">사례 기록 전체 보기</a>
      <a class="text-link" href="{{ '/03_portfolio/case-study-index.html' | relative_url }}">후보와 검수 상태 확인 <span aria-hidden="true">→</span></a>
    </div>
  </div>
</section>

<section class="section" id="flagship" aria-labelledby="flagship-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Flagship project</p>
        <h2 id="flagship-title">OpsMate Local</h2>
      </div>
      <p>로컬 오픈웨이트 LLM을 구매 업무에 연결하되, 승인되지 않은 쓰기와 모델 장애를 Spring 서비스에서 차단하는 신규 대표 프로젝트입니다.</p>
    </div>

    <article class="project-panel">
      <div class="project-panel__main">
        {% include status-badge.html status="tested-component" %}
        <h3>AI가 제안하고,<br>업무 시스템이 결정합니다.</h3>
        <p>첫 수직 범위는 구매 요청, 정책 검색, 초안, 사람 승인과 발주 생성입니다. 19개 테스트가 성공한 <code>tested-component</code> 상태이며 실제 로컬 모델 서버 E2E와 운영 배포는 아직 검증하지 않았습니다.</p>
        <div class="button-row">
          <a class="button" href="{{ '/03_portfolio/portfolio-strategy.html#track-a-flagship' | relative_url }}">확정된 설계 원칙 보기</a>
          <a class="text-link" href="{{ '/02_projects/opsmate-local/' | relative_url }}">코드와 검증 결과 <span aria-hidden="true">→</span></a>
        </div>
      </div>
      <div class="project-panel__flow">
        <h4>First vertical slice</h4>
        <ol class="flow-list">
          <li>자연어 구매 요청</li>
          <li>정책 근거 검색</li>
          <li>구매요청 초안</li>
          <li>사람 승인 또는 반려</li>
          <li>승인된 요청만 발주</li>
          <li>감사 이벤트 기록</li>
        </ol>
        <p class="status-note"><strong>Fail-closed:</strong> 모델 미연결·타임아웃·잘못된 출력에서는 쓰기 작업을 중단하며 외부 유료 API로 자동 우회하지 않습니다.</p>
      </div>
    </article>
  </div>
</section>

<section class="section section--soft" id="evidence" aria-labelledby="evidence-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Evidence map</p>
        <h2 id="evidence-title">주장을 검증하는 경로</h2>
      </div>
      <p>채용 담당자와 AI가 같은 기준으로 현재 상태를 확인할 수 있도록 계획, 작업, 검수와 근거 문서를 연결했습니다.</p>
    </div>
    <div class="link-grid">
      <article class="link-card">
        <span class="link-card__label">Plan & work</span>
        <h3><a href="{{ '/WORKS.html' | relative_url }}">단계별 작업 현황</a></h3>
        <p>각 산출물의 목표, 선행 조건과 완료 기준을 확인합니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Review</span>
        <h3><a href="{{ '/03_portfolio/review-checklist.html' | relative_url }}">공개 전 검수표</a></h3>
        <p>귀속, 비식별화, 코드·설명 정합성과 테스트 결과를 확인합니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Evidence</span>
        <h3><a href="{{ '/03_portfolio/evidence-index.html' | relative_url }}">근거·상태 인덱스</a></h3>
        <p>무엇이 계획이고 구현이며 최근 검증됐는지 구분해 확인합니다.</p>
      </article>
    </div>
  </div>
</section>

{% if site.posts.size > 0 %}
<section class="section" aria-labelledby="recent-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Recent notes</p>
        <h2 id="recent-title">최근 기록</h2>
      </div>
      <p>결과만 나열하지 않고 문제, 제약, 선택과 확인하지 못한 한계까지 함께 기록합니다.</p>
    </div>
    <div class="card-grid">
      {% for post in site.posts limit: 2 %}
        {% include post-card.html post=post %}
      {% endfor %}
    </div>
  </div>
</section>
{% endif %}
