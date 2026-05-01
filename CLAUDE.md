# Blog Project — Root Context

## Project Overview
개인 기술 블로그. 개발 경험과 소프트웨어 여정을 기록하는 플랫폼.
Owner-only 글쓰기, Google OAuth 로그인, 카테고리/태그 기반 분류.

## Monorepo Structure
```
blog-project/
├── backend/         # Java 17 + Spring Boot 3 + JPA + PostgreSQL
├── frontend/        # React + Vite + JavaScript
├── docs/            # 설계 문서 (ERD, API 명세 등)
└── CLAUDE.md        # ← 지금 이 파일
```

## Tech Stack
- **Backend**: Java 17, Spring Boot 3.x, Spring Security, JPA/Hibernate
- **Frontend**: React 18, Vite, JavaScript (TypeScript 아님)
- **DB**: PostgreSQL 15
- **Auth**: Google OAuth2 (Spring Security OAuth2 Client)
- **Design Pattern**: MVC + Service Layer

## Coding Conventions (전체 공통)
- 변수/함수명: camelCase
- 클래스명: PascalCase
- 파일명: 백엔드는 PascalCase, 프론트는 PascalCase (컴포넌트) / camelCase (유틸)
- 주석은 한국어로 작성

## Git Commit Convention
```
feat: 새 기능
fix: 버그 수정
refactor: 리팩토링
chore: 설정/빌드 변경
```
예시: `feat: Post 목록 조회 API 추가`

## Dev Rules (반드시 지켜야 할 규칙)
- DB 스키마 변경 (컬럼 추가/삭제/타입 변경) 전에 반드시 확인 요청
- 기존 파일 삭제 금지 — 이름 변경도 먼저 물어볼 것
- 한 번에 하나의 기능 단위만 작업
- 새 의존성(라이브러리) 추가 전에 반드시 확인 요청
- 확인 없이 포트, 환경변수, 설정 파일 변경 금지

## Reference Docs
상세 설계는 아래 파일 참조 (필요할 때만 로드):
- `docs/erd.md` — 전체 DB 스키마 및 ERD
- `docs/api-spec.md` — REST API 엔드포인트 명세
- `docs/progress.md` — 현재 진행 상황 및 체크리스트
