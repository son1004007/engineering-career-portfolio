---
title: Text2SQL을 생성이 아니라 검증 가능한 기능으로 만들기
description: SELECT 제한, schema 검증, 실행 결과와 실패 유형을 분리한 LLM 응용서비스 사례
permalink: /cases/text2sql-validation/
status: source-reviewed
---

# Text2SQL을 생성이 아니라 검증 가능한 기능으로 만들기

> 상태: `source-reviewed`, `tested-component` · 전체 서비스 운영 검증을 뜻하지 않음

## 확인된 범위

비공개 업무 근거에서 FastAPI 기반 Text2SQL/NL2SQL 호출 구조, SQL 검증·선택적 실행, 결과 기록과 다중 로컬 모델 비교에 대한 본인 구현 및 구성요소 테스트를 확인했습니다.

## 문제

LLM이 SQL 문자열을 생성했다는 사실만으로는 업무 기능이 되지 않습니다. 허용 schema를 벗어나는 열, 쓰기 문장, 실행 오류와 의미가 틀린 결과를 서로 다른 실패로 분류해야 합니다.

## 설계 판단

- 모델 호출과 SQL policy validation을 분리합니다.
- 읽기 전용 문장만 허용하고 허용된 schema 범위를 검사합니다.
- 생성 성공, 구문·정책 통과, DB 실행 성공과 정답 여부를 다른 지표로 기록합니다.
- 모델 adapter를 교체 가능하게 두되 평가 조건은 동일하게 유지합니다.

## 검증 근거

13개 업무 질문과 4개 로컬 모델의 52회 비교, 별도의 29개 질문 validation set 기록을 확인했습니다. 실행 성공률을 정답 정확도로 바꾸어 표현하지 않습니다.

## 공개 재현 계획과 한계

합성 schema와 읽기 전용 DB로 policy test와 공개 benchmark를 다시 만들 계획입니다. 현재 공개 저장소에는 원본 질문, 데이터, SQL과 모델 응답을 포함하지 않습니다.
