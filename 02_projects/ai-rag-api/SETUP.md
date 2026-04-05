# Setup Guide

## 1. Install dependencies
```bash
pip install -r requirements.txt
```

## 2. Configure environment variables
`.env.example` 파일을 참고해 환경변수를 설정합니다.

예시:
```bash
export OPENAI_API_KEY=your_api_key_here
export OPENAI_CHAT_MODEL=gpt-4.1-mini
export OPENAI_EMBEDDING_MODEL=text-embedding-3-small
```

## 3. Run the server
```bash
uvicorn app.main:app --reload
```

## 4. Example requests
### Add document
```bash
curl -X POST http://127.0.0.1:8000/documents \
-H "Content-Type: application/json" \
-d '{"content": "Spring Boot는 Java 기반 백엔드 프레임워크입니다."}'
```

### Query
```bash
curl -X POST http://127.0.0.1:8000/query \
-H "Content-Type: application/json" \
-d '{"question": "Spring Boot가 무엇인가요?"}'
```
