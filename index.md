---
title: "Software / Backend / Platform Engineer"
page_description: "업무 문제를 시스템으로 구조화하고 AI와 적절한 기술을 활용해 구현·검증·운영까지 연결하는 손기석의 엔지니어 포트폴리오"
permalink: /
---

<section class="hero" aria-labelledby="hero-title">
  <div class="shell hero__grid">
    <div>
      <p class="eyebrow">Software · Backend · Data · AI · Security · Operations</p>
      <h1 id="hero-title"><em>문제를 실제로 작동하는 시스템으로</em> 만듭니다.</h1>
      <p class="hero__lead">업무에서 필요한 기능을 문제와 제약조건으로 정리하고, 적절한 기술과 AI를 활용해 구현한 뒤 테스트·보안·운영까지 확인합니다. Java/Spring, Python/FastAPI, SQL, Docker와 LLM은 이 일을 하기 위해 사용하는 도구입니다.</p>
      <div class="button-row">
        <a class="button" href="{{ '/#capabilities' | relative_url }}">제가 잘하는 일 보기</a>
        <a class="button button--secondary" href="{{ '/HOW_I_ENGINEER/' | relative_url }}">개발 방식 보기</a>
      </div>
    </div>

    <aside class="hero__panel" aria-label="핵심 엔지니어링 역량">
      <p>핵심 엔지니어링 역량</p>
      <dl>
        <div>
          <dt>Problem to system</dt>
          <dd>요구사항을 경계·상태·데이터·완료조건으로 구조화</dd>
        </div>
        <div>
          <dt>AI with control</dt>
          <dd>AI가 도와줄 일과 서버·사람이 책임질 일을 분리</dd>
        </div>
        <div>
          <dt>Verified delivery</dt>
          <dd>테스트·실행 결과·배포와 복구 증거로 완료를 확인</dd>
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
        <h2 id="capabilities-title">기술 이름보다 문제를 해결하는 방식을 먼저 보여드립니다.</h2>
      </div>
      <p>HR은 쉽게 이해하고, 엔지니어는 아래 기술 세부와 evidence까지 확인할 수 있게 두 단계로 설명합니다.</p>
    </div>

    <div class="link-grid">
      <article class="link-card">
        <span class="link-card__label">Problem framing</span>
        <h3>모호한 요청을 구현 가능한 문제로 정리합니다.</h3>
        <p>누가 사용하는지, 무엇이 성공인지, 어떤 실패를 막아야 하는지 먼저 정의합니다. 기술적으로는 system boundary, state, transaction과 acceptance criteria를 다룹니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Backend & data</span>
        <h3>업무 규칙과 데이터를 실제 기능으로 연결합니다.</h3>
        <p>화면, API, DB와 분석 결과가 하나의 사용자 흐름으로 이어지게 만듭니다. Spring Boot, FastAPI, SQL, Oracle과 PostgreSQL을 사용해 왔습니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">AI integration</span>
        <h3>AI를 쓰되 중요한 결정은 시스템이 통제합니다.</h3>
        <p>LLM은 초안·검색·질의를 돕고, 권한·상태·업무 규칙은 서버가 최종 확인합니다. Text2SQL/NL2SQL, RAG, Agent와 structured output 검증을 경험했습니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Verification</span>
        <h3>만들었다는 설명보다 실제 동작을 확인합니다.</h3>
        <p>정상 상황뿐 아니라 실패, 권한, 중복, 잘못된 입력과 외부 시스템 장애를 테스트하고 가능한 경우 실제 실행 환경에서 E2E로 다시 확인합니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Security</span>
        <h3>잘못된 사용자와 잘못된 결과가 중요한 처리로 이어지지 않게 합니다.</h3>
        <p>인증·권한, 세션 경계, 감사 기록, 최소 권한과 fail-closed 같은 원칙을 개발 문제로 함께 다룹니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Operations</span>
        <h3>배포하고 닫고 다시 열 수 있는 상태까지 고려합니다.</h3>
        <p>Linux, Docker, Tomcat, Nginx와 CI/CD 환경에서 health check, rollback, network boundary와 복구 절차를 확인합니다.</p>
      </article>
    </div>
  </div>
</section>

<section class="section" id="evidence" aria-labelledby="evidence-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">숫자로 확인된 공개 증거</p>
        <h2 id="evidence-title">확인한 범위만 숫자로 말합니다.</h2>
      </div>
      <p>검증하지 않은 대규모 운영, SLA나 실제 회사 성능은 추정해서 쓰지 않습니다.</p>
    </div>

    <div class="card-grid">
      <article class="case-card">
        <div class="case-card__topline"><span class="status-badge status-badge--published">실제 모델 E2E</span></div>
        <h3>OpsMate Local: 9/9</h3>
        <p>실제 Ollama `gemma3:12b`를 사용한 합성 구매 요청 E2E 9건이 모두 성공했습니다.</p>
        <p class="case-card__focus"><strong>관측:</strong> p95 21,076ms, 프로젝트 gate &lt;= 30,000ms</p>
      </article>
      <article class="case-card">
        <div class="case-card__topline"><span class="status-badge status-badge--published">Public boundary</span></div>
        <h3>Internet HTTPS 경계 검증</h3>
        <p>외부 두 session의 격리, rate limit, DB/model 비노출, app egress 차단과 close/reopen lifecycle을 실제 Internet 경로에서 확인했습니다.</p>
        <p class="case-card__focus"><strong>범위:</strong> bounded E2E, 24x7 SLA와 장기 부하는 미검증</p>
      </article>
      <article class="case-card">
        <div class="case-card__topline"><span class="status-badge status-badge--published">Case studies</span></div>
        <h3>24 + 12 + 10 + 11 tests</h3>
        <p>로그인·권한, 데이터 정합성, 배포·복구, 업무 규칙 일관성 문제를 회사 코드와 독립된 합성 샘플로 재현하고 자동 테스트로 검증했습니다.</p>
        <p class="case-card__focus"><strong>원칙:</strong> 실제 회사 시스템 전체 성과로 확대하지 않음</p>
      </article>
    </div>
  </div>
</section>

<section class="section section--soft" id="flagship" aria-labelledby="flagship-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">대표 프로젝트</p>
        <h2 id="flagship-title">AI는 도와주고, 시스템은 중요한 규칙을 지킵니다.</h2>
      </div>
      <p>`OpsMate Local`은 구매 요청부터 승인·발주까지의 업무 흐름에 AI 초안 생성을 연결한 프로젝트입니다.</p>
    </div>

    <article class="project-panel">
      <div class="project-panel__main">
        <p class="eyebrow">Controlled AI Integration</p>
        <h3>AI가 업무를 대신 결정하지 않도록 경계를 설계했습니다.</h3>
        <p>AI는 요청을 이해하고 초안을 제안하지만, 사용자 권한, 상태 변경, 중복 방지와 발주는 서버가 최종 검증합니다. 모델이 잘못된 결과를 반환하거나 사용할 수 없으면 잘못 처리하기보다 안전하게 중단합니다.</p>
        <p><strong>기술적으로:</strong> Spring Boot, Spring Security, JPA/PostgreSQL, local LLM adapter, RBAC, idempotency, fail-closed, audit event, isolated session workspace를 적용했습니다.</p>
        <div class="button-row">
          <a class="button" href="{{ '/02_projects/opsmate-local/' | relative_url }}">프로젝트와 코드 보기</a>
          <a class="text-link" href="{{ '/blog/2026/08/03/llm-transaction-boundaries/' | relative_url }}">설계 판단 읽기 <span aria-hidden="true">→</span></a>
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
        <p class="status-note"><strong>검증 범위:</strong> 실제 모델 E2E, Synology 내부 배포·네트워크·보안·수명주기, Internet HTTPS의 session 격리·rate limit·비노출·close/reopen을 bounded E2E로 확인했습니다. 장기 production SLA는 주장하지 않습니다.</p>
      </div>
    </article>
  </div>
</section>

<section class="section" id="case-studies" aria-labelledby="cases-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">실무 문제 해결 사례</p>
        <h2 id="cases-title">프레임워크보다 해결한 문제를 먼저 설명합니다.</h2>
      </div>
      <p>회사 코드와 데이터를 공개하지 않고 직접 담당한 문제를 일반화해 독립 샘플과 테스트로 재현했습니다.</p>
    </div>

    <div class="card-grid">
      {% include case-card.html id="CS-JAVA-01" status="published" title="사용자 로그인과 권한을 안전하게 통합" description="서로 다른 인증 경로에서도 사용자와 권한이 잘못 연결되지 않도록 통합하고, 독립 샘플의 24개 테스트와 Pages 배포로 확인했습니다." focus="기술: Spring Security, RBAC, session/CSRF, assertion 검증" href="/cases/spring-security-auth-bridge/" %}

      {% include case-card.html id="CS-JAVA-02" status="published" title="복잡한 기간 조회에서 데이터 정합성 유지" description="여러 연도와 월을 함께 조회할 때 조건이 겹치거나 빠지지 않도록 SQL 구조를 재현하고 12개 테스트로 확인했습니다." focus="기술: Spring Boot, MyBatis, SQL, H2" href="/cases/mybatis-query-correctness/" %}

      {% include case-card.html id="CS-JAVA-03" status="published" title="환경이 달라도 배포하고 복구할 수 있는 구조" description="WAR 애플리케이션의 context path, 외부 설정, health check와 rollback 경계를 독립 샘플로 재현하고 10개 테스트로 확인했습니다." focus="기술: Spring Boot WAR, Tomcat, profile, rollback" href="/cases/war-deployment-portability/" %}

      {% include case-card.html id="CS-JAVA-06" status="published" title="사용자 식별과 업무 기준을 여러 계층에서 일관되게 유지" description="session identity와 최신 기준이 화면·service·data access에서 서로 다르게 적용되지 않도록 재현하고 11개 테스트로 확인했습니다." focus="기술: Spring Boot, MockMvc, service/mapper boundary" href="/cases/business-rule-consistency/" %}
    </div>
  </div>
</section>

<section class="section section--soft" id="ai-engineering" aria-labelledby="ai-engineering-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">AI를 활용해 개발하는 방식</p>
        <h2 id="ai-engineering-title">AI가 빨라져도 완료 기준은 검증입니다.</h2>
      </div>
      <p>AI를 코드 자동완성 도구로만 쓰지 않고 조사, 계획, 구현과 리뷰에 활용합니다. 결과는 다시 테스트하고 실제 실행으로 확인합니다.</p>
    </div>

    <article class="project-panel">
      <div class="project-panel__main">
        <h3>사람이 목표와 제약을 유지하고, AI는 탐색과 구현을 가속합니다.</h3>
        <p>다른 AI나 개발자가 작업을 이어받아도 현재 상태, 제한과 검증 기준을 파악할 수 있도록 repository 규칙과 evidence를 함께 관리합니다.</p>
        <p><strong>쉽게 말하면:</strong> AI가 바뀌어도 프로젝트의 기억을 잃지 않게 하고, AI가 만든 결과도 다시 확인할 수 있게 만듭니다.</p>
        <div class="button-row">
          <a class="button button--secondary" href="{{ '/HOW_I_ENGINEER/' | relative_url }}">개발 방식 자세히 보기</a>
        </div>
      </div>
      <div class="project-panel__flow">
        <h4>기본 workflow</h4>
        <ol class="flow-list">
          <li>문제와 제약 정의</li>
          <li>구현 계획</li>
          <li>AI를 활용한 탐색·구현</li>
          <li>자동 테스트와 리뷰</li>
          <li>실제 실행 환경 검증</li>
          <li>evidence와 문서 갱신</li>
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
        <h2 id="technology-title">기술은 목적에 맞춰 선택합니다.</h2>
      </div>
      <p>언어와 프레임워크는 상위 정체성이 아니라 시스템을 만들기 위한 도구로 설명합니다.</p>
    </div>
    <div class="link-grid">
      <article class="link-card">
        <span class="link-card__label">Backend</span>
        <h3>업무 서비스와 API</h3>
        <p>Java, Spring Boot, Spring Security, JPA, MyBatis, Python, FastAPI</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Data & AI</span>
        <h3>데이터와 AI 기능 연결</h3>
        <p>Oracle, PostgreSQL, SQL, Text2SQL/NL2SQL, RAG, local LLM, Agent workflow</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Platform & Operations</span>
        <h3>실행, 배포와 검증</h3>
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
      <p>실무 사례에는 회사 코드, 고객 데이터와 내부 식별자를 포함하지 않습니다. 독립 재현 샘플과 검증된 공개 evidence를 구분하며, 확인하지 않은 장기 운영·성능·팀 전체 성과는 주장하지 않습니다.</p>
      <div class="button-row">
        <a class="button button--secondary" href="https://github.com/{{ site.repository }}">GitHub에서 전체 소스 보기</a>
        <a class="text-link" href="{{ '/03_portfolio/evidence-index/' | relative_url }}">구현과 테스트 기록 <span aria-hidden="true">→</span></a>
      </div>
    </div>
  </div>
</section>
