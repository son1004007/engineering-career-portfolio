# Security Audit Log

## Overview
`security-audit-log`는 사용자 행위와 시스템 이벤트를 감사 가능한 형태로 기록하기 위한 예제 프로젝트입니다.

이 프로젝트의 목적은 단순 로그 출력이 아니라, **누가 / 언제 / 무엇을 했는지**를 구조적으로 남기고 조회할 수 있는 기본 구조를 보여주는 것입니다.

---

## What this project demonstrates
- 보안/감사 관점의 로그 모델 설계
- FastAPI 기반 API 구조
- 로그 저장 로직 분리
- 역할 기반 조회 예제
- 테스트 가능한 구조

---

## Core Features
- 감사 로그 생성
- 감사 로그 목록 조회
- 행위 유형(action) 기록
- 행위 주체(actor) 기록
- 대상(resource) 기록
- timestamp 자동 부여

---

## Example Audit Event
```json
{
  "actor": "admin-user",
  "action": "DELETE_USER",
  "resource": "user:1234",
  "detail": "deleted inactive user account"
}
```

---

## Why this project matters
이 프로젝트는 손기석이 단순 웹 기능 구현을 넘어서,
**보안, 통제, 감사 가능성**을 고려한 서비스 구조를 이해하고 있다는 점을 보여주기 위한 포트폴리오 산출물입니다.
