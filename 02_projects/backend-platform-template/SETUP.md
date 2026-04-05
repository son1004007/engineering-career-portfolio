# Setup Guide

## 1. 목표
이 템플릿은 운영 가능한 백엔드 서비스의 기본 구조를 빠르게 시작하기 위한 예시입니다.

## 2. 기본 구성 요소
- 환경변수 기반 설정 분리
- 공통 로깅 구조
- 공통 예외 처리
- Docker 기반 실행 환경
- 추후 인증/인가, 캐시, 모니터링 확장 가능

## 3. 실행 예시
### FastAPI 기반일 경우
```bash
uvicorn app.main:app --reload
```

### Spring Boot 기반일 경우
```bash
./gradlew bootRun
```

## 4. Docker 실행 예시
```bash
docker build -t backend-platform-template .
docker run -p 8000:8000 backend-platform-template
```

## 5. 환경 변수 예시
```bash
APP_ENV=local
APP_PORT=8000
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sample
DB_USER=user
DB_PASSWORD=password
```

## 6. 확장 방향
- Redis 추가
- 인증/인가 모듈 추가
- Prometheus/Grafana 연동
- GitHub Actions 또는 Jenkins 배포 연결
