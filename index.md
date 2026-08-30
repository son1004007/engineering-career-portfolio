---
title: "Backend Engineer - AI Integration & Reliable Systems"
page_description: "업무 요구사항을 안전한 백엔드 시스템으로 구현하고 AI 기능을 서버 검증과 실제 실행 증거로 연결하는 손기석의 엔지니어 포트폴리오"
permalink: /
---

<section class="hero" aria-labelledby="hero-title">
  <div class="shell hero__grid">
    <div>
      <p class="eyebrow">Backend / AI Integration / Reliable Systems</p>
      <h1 id="hero-title"><em>업무 요구사항을 실제로 작동하는 백엔드 시스템으로</em> 구현합니다.</h1>
      <p class="hero__lead">데이터, 권한, 처리 상태와 실패 조건을 명확하게 설계하고, AI 기능은 서버의 검증과 사람의 승인 안에서 실제 업무와 연결합니다. Java/Spring, Python/FastAPI, SQL, Docker와 LLM은 문제에 맞게 선택하는 도구입니다.</p>
      <div class="button-row">
        <a class="button" href="{{ '/#evidence' | relative_url }}">실제 검증 보기</a>
        <a class="button button--secondary" href="{{ '/HOW_I_ENGINEER/' | relative_url }}">개발 방식 보기</a>
      </div>
    </div>

    <aside class="hero__panel" aria-label="핵심 엔지니어링 역량">
      <p>핵심 엔지니어링 역량</p>
      <dl>
        <div>
          <dt>Backend systems</dt>
          <dd>업무 규칙, 데이터와 권한을 API와 DB에 연결</dd>
        </div>
        <div>
          <dt>Controlled AI</dt>
          <dd>AI가 제안할 일과 서버 또는 사람이 책임질 일을 분리</dd>
        </div>
        <div>
          <dt>Reliable delivery</dt>
          <dd>정상 동작뿐 아니라 실패, 배포와 복구까지 실제로 검증</dd>
        </div>
      </dl>
    </aside>
  </div>
</section>

<section class="section section--soft" id="capabilities" aria-labelledby="capabilities-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">제가 잘하는 일</p>
        <h2 id="capabilities-title">백엔드 역할을 중심으로 데이터, AI, 보안과 운영을 연결합니다.</h2>
      </div>
      <p>먼저 쉽게 이해할 수 있는 결과를 설명하고, 필요한 경우 아래에 구현 기술과 검증 근거를 붙입니다.</p>
    </div>

    <div class="link-grid">
      <article class="link-card">
        <span class="link-card__label">Problem framing</span>
        <h3>모호한 요청을 구현 가능한 범위로 정리합니다.</h3>
        <p>누가 사용하는지, 어떤 처리가 필요한지, 무엇을 성공과 실패로 볼지 먼저 정리합니다. 기술적으로는 system boundary, state, transaction과 acceptance criteria를 다룹니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Backend & data</span>
        <h3>업무 규칙과 데이터를 실제 기능으로 연결합니다.</h3>
        <p>화면, API, DB와 분석 결과가 하나의 사용자 흐름으로 이어지게 만듭니다. Spring Boot, FastAPI, SQL, Oracle과 PostgreSQL을 사용해 왔습니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">AI integration</span>
        <h3>AI를 쓰되 중요한 결정은 시스템이 통제합니다.</h3>
        <p>LLM은 초안, 검색과 질의를 돕고, 권한, 상태와 업무 규칙은 서버가 최종 확인합니다. Text2SQL/NL2SQL과 structured output 검증을 적용했습니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Verification</span>
        <h3>만들었다는 설명보다 실제 동작을 확인합니다.</h3>
        <p>정상 상황뿐 아니라 실패, 권한, 중복, 잘못된 입력과 외부 시스템 장애를 테스트하고 가능한 경우 실제 실행 환경에서 E2E로 다시 확인합니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Security</span>
        <h3>잘못된 사용자와 잘못된 결과가 중요한 처리로 이어지지 않게 합니다.</h3>
        <p>인증과 권한, 사용자별 작업 분리, 감사 기록과 안전한 실패 처리를 백엔드 설계에 함께 반영합니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Operations</span>
        <h3>배포하고 상태를 확인하고 다시 복구할 수 있게 합니다.</h3>
        <p>Linux, Docker, Tomcat, Nginx와 CI/CD 환경에서 health check, rollback, network boundary와 복구 절차를 확인합니다.</p>
      </article>
    </div>
  </div>
</section>

<section class="section" id="evidence" aria-labelledby="evidence-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">공개 검증</p>
        <h2 id="evidence-title">몇 개를 테스트했는지보다 무엇을 실제로 확인했는지 보여드립니다.</h2>
      </div>
      <p>실제 모델, 실패 조건, 권한 경계, 배포와 복구처럼 채용 검토자가 다시 확인할 수 있는 범위를 우선합니다.</p>
    </div>

    <div class="card-grid">
      <article class="case-card">
        <div class="case-card__topline"><span class="status-badge status-badge--published">실제 모델 E2E</span></div>
        <h3>OpsMate Local: 실제 LLM 요청 9/9 성공</h3>
        <p>Ollama `gemma3:12b`를 사용한 합성 구매 요청을 실제 모델로 실행하고 모든 요청이 완료되는지 확인했습니다.</p>
        <p class="case-card__focus"><strong>응답시간 기준:</strong> 관측 p95 21,076ms, 프로젝트 gate &lt;= 30,000ms</p>
      </article>
      <article class="case-card">
        <div class="case-card__topline"><span class="status-badge status-badge--published">외부 접속 검증</span></div>
        <h3>사용자 작업 분리와 외부 노출 차단 확인</h3>
        <p>실제 Internet HTTPS 경로에서 서로 다른 사용자의 작업이 섞이지 않는지, 과도한 요청이 차단되는지, DB와 모델이 외부에 직접 노출되지 않는지 확인했습니다.</p>
        <p class="case-card__focus"><strong>복구:</strong> 서비스를 닫고 같은 검증 버전으로 다시 열 수 있는지까지 확인</p>
      </article>
      <article class="case-card">
        <div class="case-card__topline"><span class="status-badge status-badge--published">독립 재현 사례</span></div>
        <h3>업무에서 자주 발생하는 실패 조건을 합성 샘플로 재현</h3>
        <p>로그인과 권한, 데이터 정합성, 배포와 복구, 업무 규칙 일관성 문제를 회사 코드와 독립된 샘플로 다시 구현했습니다.</p>
        <p class="case-card__focus"><strong>검증:</strong> 정상 흐름과 함께 권한 오류, 기간 경계, 잘못된 설정과 상태 불일치를 자동 테스트</p>
      </article>
    </div>
  </div>
</section>

<section class="section section--soft" id="flagship" aria-labelledby="flagship-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">대표 프로젝트</p>
        <h2 id="flagship-title">AI는 초안을 돕고, 서버는 중요한 규칙을 지킵니다.</h2>
      </div>
      <p>`OpsMate Local`은 구매 요청부터 승인과 발주까지의 백엔드 업무 흐름에 AI 초안 생성을 연결한 프로젝트입니다.</p>
    </div>

    <article class="project-panel">
      <div class="project-panel__main">
        <p class="eyebrow">Controlled AI Integration</p>
        <h3>AI의 결과를 그대로 저장하거나 업무 결정으로 사용하지 않습니다.</h3>
        <p>AI는 요청을 이해하고 초안을 제안하지만, 사용자 권한, 상태 변경, 중복 방지와 발주는 서버가 최종 검증합니다. 모델이 잘못된 결과를 반환하거나 사용할 수 없으면 중요한 처리를 진행하지 않습니다.</p>
        <p><strong>기술적으로:</strong> Spring Boot, Spring Security, JPA/PostgreSQL, local LLM adapter, RBAC, idempotency, fail-closed, audit event, isolated session workspace를 적용했습니다.</p>
        <div class="button-row">
          <a class="button" href="{{ '/02_projects/opsmate-local/' | relative_url }}">프로젝트와 코드 보기</a>
          <a class="text-link" href="{{ '/blog/2026/08/03/llm-transaction-boundaries/' | relative_url }}">설계 판단 읽기</a>
        </div>
      </div>
      <div class="project-panel__flow">
        <h4>업무 흐름</h4>
        <ol class="flow-list">
          <li>자연어 구매 요청</li>
          <li>서버가 정책 근거 조회</li>
          <li>AI가 초안 제안</li>
          <li>사람이 승인 또는 반려</li>
          <li>서버가 승인된 요청만 발주</li>
          <li>누가 무엇을 했는지 감사 기록</li>
        </ol>
        <p class="status-note"><strong>확인한 범위:</strong> 실제 모델 E2E, 내부 배포와 네트워크 경계, Internet HTTPS의 사용자 작업 분리, rate limit, 비노출, close/reopen을 확인했습니다. 장기 production SLA는 주장하지 않습니다.</p>
      </div>
    </article>
  </div>
</section>

<section class="section" id="case-studies" aria-labelledby="cases-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">실무 문제 해결 사례</p>
        <h2 id="cases-title">프레임워크보다 해결한 문제와 실패 조건을 먼저 설명합니다.</h2>
      </div>
      <p>회사 코드와 데이터를 공개하지 않고 직접 담당한 문제를 일반화해 독립 샘플과 테스트로 재현했습니다.</p>
    </div>

    <div class="card-grid">
      {% include case-card.html id="CS-JAVA-01" status="published" title="사용자 로그인과 권한을 안전하게 통합" description="서로 다른 인증 경로에서도 사용자와 권한이 잘못 연결되지 않는지 정상 흐름과 권한 오류를 함께 검증했습니다." focus="기술: Spring Security, RBAC, session/CSRF, assertion 검증" href="/cases/spring-security-auth-bridge/" %}

      {% include case-card.html id="CS-JAVA-02" status="published" title="복잡한 기간 조회에서 데이터 정합성 유지" description="여러 연도와 월을 함께 조회할 때 조건이 겹치거나 빠지지 않는지 기간 경계와 중복 조건을 검증했습니다." focus="기술: Spring Boot, MyBatis, SQL, H2" href="/cases/mybatis-query-correctness/" %}

      {% include case-card.html id="CS-JAVA-03" status="published" title="환경이 달라도 배포하고 복구할 수 있는 구조" description="서버 설정이 달라져도 같은 애플리케이션을 배포하고 상태를 확인한 뒤 문제가 생기면 되돌릴 수 있는지 검증했습니다." focus="기술: Spring Boot WAR, Tomcat, profile, rollback" href="/cases/war-deployment-portability/" %}

      {% include case-card.html id="CS-JAVA-06" status="published" title="사용자 식별과 업무 기준을 여러 계층에서 일관되게 유지" description="사용자 식별과 최신 기준이 화면, service와 data access에서 다르게 적용되지 않는지 검증했습니다." focus="기술: Spring Boot, MockMvc, service/mapper boundary" href="/cases/business-rule-consistency/" %}
    </div>
  </div>
</section>

<section class="section section--soft" id="ai-engineering" aria-labelledby="ai-engineering-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">AI를 활용해 개발하는 방식</p>
        <h2 id="ai-engineering-title">AI가 빨라져도 완료 기준은 테스트와 실제 실행입니다.</h2>
      </div>
      <p>AI를 조사, 계획, 구현과 리뷰에 활용하지만 결과를 그대로 완료로 보지 않습니다.</p>
    </div>

    <article class="project-panel">
      <div class="project-panel__main">
        <h3>사람이 목표와 제약을 유지하고, AI는 탐색과 구현을 가속합니다.</h3>
        <p>다른 개발자나 AI가 작업을 이어받아도 현재 상태, 제한과 검증 기준을 파악할 수 있도록 작업 규칙과 검증 근거를 함께 관리합니다.</p>
        <p><strong>쉽게 말하면:</strong> AI가 만든 코드와 설명도 다시 테스트하고 실제로 실행해 확인합니다.</p>
        <div class="button-row">
          <a class="button button--secondary" href="{{ '/HOW_I_ENGINEER/' | relative_url }}">개발 방식 자세히 보기</a>
        </div>
      </div>
      <div class="project-panel__flow">
        <h4>기본 workflow</h4>
        <ol class="flow-list">
          <li>문제와 제약 정의</li>
          <li>구현 계획</li>
          <li>AI를 활용한 탐색과 구현</li>
          <li>자동 테스트와 리뷰</li>
          <li>실제 실행 환경 검증</li>
          <li>결과와 한계 기록</li>
        </ol>
      </div>
    </article>
  </div>
</section>

<section class="section" id="technology" aria-labelledby="technology-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">사용하는 기술</p>
        <h2 id="technology-title">백엔드 시스템을 만들기 위해 목적에 맞는 도구를 선택합니다.</h2>
      </div>
      <p>기술 이름은 역할 자체가 아니라 문제를 해결하기 위한 구현 도구로 설명합니다.</p>
    </div>
    <div class="link-grid">
      <article class="link-card">
        <span class="link-card__label">Backend</span>
        <h3>업무 서비스와 API</h3>
        <p>Java, Spring Boot, Spring Security, JPA, MyBatis, Python, FastAPI</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Data & AI Integration</span>
        <h3>데이터와 AI 기능 연결</h3>
        <p>Oracle, PostgreSQL, SQL, Text2SQL/NL2SQL, LLM integration, structured output validation, RAG/Agent patterns</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Operations & Delivery</span>
        <h3>실행, 배포와 복구</h3>
        <p>Linux, Docker, Tomcat, Nginx, Jenkins, GitHub Actions, health check, rollback</p>
      </article>
    </div>
  </div>
</section>

<section class="section section--soft" id="scope" aria-labelledby="scope-title">
  <div class="shell section-heading">
    <div>
      <p class="eyebrow">공개 범위</p>
      <h2 id="scope-title">확인한 사실과 공개 가능한 코드만 담았습니다.</h2>
    </div>
    <div>
      <p>실무 사례에는 회사 코드, 고객 데이터와 내부 식별자를 포함하지 않습니다. 독립 재현 샘플과 검증된 공개 evidence를 구분하며, 확인하지 않은 장기 운영, 성능과 팀 전체 성과는 주장하지 않습니다.</p>
      <div class="button-row">
        <a class="button button--secondary" href="https://github.com/{{ site.repository }}">GitHub에서 전체 소스 보기</a>
        <a class="text-link" href="{{ '/03_portfolio/evidence-index/' | relative_url }}">구현과 테스트 기록</a>
      </div>
    </div>
  </div>
</section>
