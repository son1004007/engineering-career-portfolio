# Architecture

## Directory Structure
```text
ai-rag-api/
├─ README.md
├─ requirements.txt
├─ .env.example
└─ app/
   ├─ main.py
   ├─ api/
   │  └─ routes.py
   ├─ service/
   │  └─ rag_service.py
   ├─ repository/
   │  └─ vector_store.py
   └─ core/
      ├─ config.py
      └─ logging.py
```

## Layer Responsibilities
- `api`: 요청/응답 처리
- `service`: 문서 분할, 검색 결과 조합, LLM 호출
- `repository`: embedding 생성, FAISS 저장/검색
- `core`: 환경설정 및 로깅

## Request Flow
1. 사용자가 문서를 등록합니다.
2. 서비스 레이어가 문서를 chunk로 분할합니다.
3. repository 레이어가 각 chunk를 embedding으로 변환하고 FAISS 인덱스에 저장합니다.
4. 사용자가 질문을 입력합니다.
5. repository 레이어가 Top-K 문서를 검색합니다.
6. service 레이어가 검색 결과를 context로 구성합니다.
7. LLM이 context 기반 응답을 생성합니다.
8. API 레이어가 응답을 반환합니다.
