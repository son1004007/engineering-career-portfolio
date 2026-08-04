---
title: "Java/Spring 백엔드 엔지니어"
page_description: "Java/Spring 업무 시스템 개발 경험과 AI 기능을 기존 서비스에 연결한 프로젝트를 소개하는 손기석의 포트폴리오"
permalink: /
---

<section class="hero" aria-labelledby="hero-title">
  <div class="shell hero__grid">
    <div>
      <p class="eyebrow">Java · Spring · Python · AI Integration</p>
      <h1 id="hero-title"><em>운영까지 이어지는 백엔드</em>를 만듭니다.</h1>
      <p class="hero__lead">Java/Spring 기반 웹 서비스와 API, DB 연계, 인증·권한, 배포·운영을 다뤄왔습니다. Python/FastAPI 기반 Text2SQL/NL2SQL API와 로컬 모델 비교를 경험했고, 최근에는 Spring Boot에서 LLM 초안을 기존 업무 흐름에 연결하는 프로젝트를 진행하고 있습니다.</p>
      <div class="button-row">
        <a class="button" href="{{ '/#flagship' | relative_url }}">대표 프로젝트 보기</a>
        <a class="button button--secondary" href="{{ '/cases/spring-security-auth-bridge/' | relative_url }}">Spring Security 사례 보기</a>
      </div>
    </div>

    <aside class="hero__panel" aria-label="주요 기술 경험">
      <p>주요 기술 경험</p>
      <dl>
        <div>
          <dt>Enterprise backend</dt>
          <dd>Java · Spring · Oracle · MyBatis</dd>
        </div>
        <div>
          <dt>Operations</dt>
          <dd>Linux · Tomcat · Nginx · Docker · Jenkins</dd>
        </div>
        <div>
          <dt>AI integration</dt>
          <dd>Python · FastAPI · Text2SQL · LLM adapter</dd>
        </div>
      </dl>
    </aside>
  </div>
</section>

<section class="section section--soft" id="flagship" aria-labelledby="flagship-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">대표 프로젝트</p>
        <h2 id="flagship-title">OpsMate Local</h2>
      </div>
      <p>구매 요청부터 승인·발주까지의 업무 흐름에 AI 초안 생성을 결합한 Spring Boot 프로젝트입니다.</p>
    </div>

    <article class="project-panel">
      <div class="project-panel__main">
        <p class="eyebrow">Spring Boot · Security · JPA · Local LLM</p>
        <h3>모델은 초안을 제안하고, 서버는 업무 규칙을 지킵니다.</h3>
        <p>서버가 정책을 먼저 조회한 뒤 사용자 요청과 근거를 모델에 전달합니다. 권한, 상태 전이, 중복 방지와 발주는 Spring 서비스와 데이터베이스가 최종 확인합니다.</p>
        <div class="button-row">
          <a class="button" href="{{ '/02_projects/opsmate-local/' | relative_url }}">프로젝트와 코드 보기</a>
          <a class="text-link" href="{{ '/blog/2026/08/03/llm-transaction-boundaries/' | relative_url }}">설계 판단 읽기 <span aria-hidden="true">→</span></a>
        </div>
      </div>
      <div class="project-panel__flow">
        <h4>구현한 업무 흐름</h4>
        <ol class="flow-list">
          <li>자연어 구매 요청</li>
          <li>서버 주도 정책 조회</li>
          <li>구매요청 초안 생성</li>
          <li>사람의 승인 또는 반려</li>
          <li>승인된 요청만 발주</li>
          <li>감사 이벤트 기록</li>
        </ol>
        <p class="status-note"><strong>현재 범위:</strong> 공개 session UI, workspace 격리, PostgreSQL 역할 분리와 model guard는 54개 컴포넌트 테스트로 확인했습니다. 닫기·다시 열기 자산은 구현했지만 승인된 실제 모델 E2E와 외부 배포 rehearsal은 아직 미검증입니다.</p>
      </div>
    </article>
  </div>
</section>

<section class="section" id="case-studies" aria-labelledby="cases-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Java/Spring 실무 사례</p>
        <h2 id="cases-title">문제와 선택이 드러나는 사례를 정리했습니다.</h2>
      </div>
      <p>회사 코드와 데이터를 사용하지 않고, 직접 담당한 문제를 공개 가능한 범위에서 일반화했습니다.</p>
    </div>

    <div class="card-grid">
      {% include case-card.html id="CS-JAVA-01" status="sample-verified" title="DB 로그인과 레거시 SSO 권한 통합" description="서로 다른 인증 경로를 하나의 사용자·권한·세션 정책으로 통합하고, 독립 샘플의 24개 테스트로 확인했습니다." focus="Spring Security, RBAC, 세션·CSRF, assertion 검증" href="/cases/spring-security-auth-bridge/" %}

      <article class="case-card">
        <div class="case-card__topline"><span class="status-badge status-badge--source-reviewed">실무 경험을 일반화한 사례</span></div>
        <h3><a href="{{ '/blog/' | relative_url }}">Java/Spring에서 다룬 문제들</a></h3>
        <p>MyBatis·Oracle 조회 정합성, WAR 배포 환경 차이, 업무 규칙 정리와 통계 품질 화면 개발 경험을 문제 해결 관점으로 정리했습니다.</p>
        <p class="case-card__focus"><strong>주요 영역:</strong> SQL, 배포, 데이터 처리, 운영</p>
        <a class="text-link" href="{{ '/blog/' | relative_url }}">기술 사례 보기 <span aria-hidden="true">→</span></a>
      </article>
    </div>
  </div>
</section>

<section class="section section--soft" id="experience" aria-labelledby="experience-title">
  <div class="shell">
    <div class="section-heading">
      <div>
        <p class="eyebrow">기술과 역할</p>
        <h2 id="experience-title">기술을 실제 사용 맥락과 함께 설명합니다.</h2>
      </div>
      <p>프레임워크 목록보다 어떤 책임을 맡고 어디까지 구현했는지를 중요하게 생각합니다.</p>
    </div>
    <div class="link-grid">
      <article class="link-card">
        <span class="link-card__label">Backend</span>
        <h3>업무 서비스와 API</h3>
        <p>Java/Spring 웹 서비스, REST API, 인증·권한과 Oracle/MyBatis 기반 데이터 조회를 다뤘습니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">Operations</span>
        <h3>배포와 운영</h3>
        <p>Linux, Tomcat, Nginx, Docker와 Jenkins 환경에서 설정 분리, 배포와 장애 원인 분석을 경험했습니다.</p>
      </article>
      <article class="link-card">
        <span class="link-card__label">AI integration</span>
        <h3>AI 기능 연동 프로젝트</h3>
        <p>Python/FastAPI 기반 Text2SQL과 로컬 모델 adapter, 작업 산출물 추적 구성요소를 구현했습니다.</p>
      </article>
    </div>
  </div>
</section>

<section class="section" id="about" aria-labelledby="about-title">
  <div class="shell about-grid">
    <div class="about-copy">
      <p class="eyebrow">설계 원칙</p>
      <h2 id="about-title">AI 기능도 기존 시스템의 규칙 안에서 동작해야 합니다.</h2>
      <p>모델은 필요한 초안을 제안하고, 권한·상태 전이·트랜잭션은 애플리케이션이 검증하도록 설계합니다. 모델 장애와 잘못된 입력도 정상적인 실패 시나리오로 다룹니다.</p>
    </div>
    <ol class="principles" aria-label="엔지니어링 원칙">
      <li data-index="01"><strong>명확한 책임 경계</strong><span>모델과 업무 서비스가 담당할 일을 나눕니다.</span></li>
      <li data-index="02"><strong>실패를 고려한 설계</strong><span>권한 오류, 중복 요청과 외부 서비스 장애를 테스트합니다.</span></li>
      <li data-index="03"><strong>운영을 고려한 구현</strong><span>인증, 데이터베이스, 배포와 감사 흐름을 함께 고려합니다.</span></li>
    </ol>
  </div>
</section>

<section class="section section--soft" id="scope" aria-labelledby="scope-title">
  <div class="shell section-heading">
    <div>
      <p class="eyebrow">공개 범위</p>
      <h2 id="scope-title">확인한 사실과 공개 가능한 코드만 담았습니다.</h2>
    </div>
    <div>
      <p>실무 사례에는 회사 코드, 고객 데이터와 내부 식별자를 포함하지 않습니다. 아직 확인하지 않은 모델 E2E나 운영 성능은 각 프로젝트에서 별도로 밝힙니다.</p>
      <div class="button-row">
        <a class="button button--secondary" href="https://github.com/{{ site.repository }}">GitHub에서 전체 소스 보기</a>
        <a class="text-link" href="{{ '/03_portfolio/evidence-index/' | relative_url }}">구현과 테스트 기록 <span aria-hidden="true">→</span></a>
      </div>
    </div>
  </div>
</section>
