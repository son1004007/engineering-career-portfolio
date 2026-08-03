---
title: Agent Runtime의 작업 격리와 산출물 추적
description: AI Agent 실행에서 workspace 탈출을 막고 artifact·manifest·provenance를 남긴 백엔드 구성요소 사례
permalink: /cases/agent-runtime-artifact-provenance/
status: source-reviewed
---

# Agent Runtime의 작업 격리와 산출물 추적

> 상태: `source-reviewed`, `tested-component` · 전체 Agent 플랫폼 완료를 뜻하지 않음

## 확인된 범위

비공개 업무 근거에서 작업별 workspace, 안전한 저장 경로, artifact·manifest·provenance 저장 구조와 관련 테스트의 본인 구현 범위를 확인했습니다.

## 문제

Agent가 파일을 만들 수 있으면 작업 디렉터리를 벗어나지 못하게 해야 하고, 결과 파일이 어느 job·입력·도구 실행에서 나왔는지 추적할 수 있어야 합니다. 단순 파일 저장만으로는 재현과 감사가 어렵습니다.

## 설계 판단

- job별 루트 workspace를 만들고 canonical path가 그 경계 안에 있는지 검사합니다.
- artifact 메타데이터와 실제 저장을 분리합니다.
- manifest에는 입력, 산출물, 생성 주체와 시간 정보를 연결합니다.
- 경로 탈출, 중복 식별자와 존재하지 않는 artifact 접근을 실패 테스트로 둡니다.

## 검증 근거

workspace, artifact, provenance와 안전하지 않은 경로 차단에 대한 테스트 산출물을 확인했습니다. 실제 모델 전체 연동과 장기 운영은 별도 범위입니다.

## 공개 재현 계획과 한계

합성 job과 임시 디렉터리만 사용하는 최소 storage 샘플로 재구현할 수 있습니다. 현재 글은 전체 플랫폼이 아니라 확인된 backend 구성요소만 설명합니다.
