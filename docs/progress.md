# 개발 진행 상황

> Claude Code 세션 시작 시 이 파일을 읽어서 현재 상태를 파악할 것.
> 기능 완료 시 [x]로 체크, 작업 중인 항목은 → 표시.

## Phase 1: 기반 설계
- [x] ERD 확정 (`docs/erd.md`)
- [ ] API 명세 초안 (`docs/api-spec.md`)
- [x] 백엔드 프로젝트 초기 세팅 (Spring Initializr)
- [ ] 프론트엔드 프로젝트 초기 세팅 (Vite)
- [ ] DB 연결 및 Entity 생성 (Entity 클래스 완료, DB 연결 설정 미완)

## Phase 2: 백엔드 핵심 기능
- [ ] User 엔티티 + Google OAuth 로그인 (User 엔티티 완료, OAuth 미완)
- [ ] CategoryGroup + Category CRUD
- [ ] Tag CRUD
- [ ] Post CRUD (기본)
- [ ] Post-Tag 연관 관계
- [ ] Post 목록 조회 (카테고리 필터, 태그 필터)
- [ ] 조회수 카운트
- [ ] 좋아요 기능 (중복 방지)

## Phase 3: 프론트엔드
- [ ] 레이아웃 (Sidebar + Header + 중앙 콘텐츠)
- [ ] Google 로그인 버튼 + 세션 유지
- [ ] 메인 페이지 (최신 포스트)
- [ ] 포스트 상세 뷰
- [ ] 카테고리별 포스트 목록
- [ ] 태그별 포스트 목록
- [ ] 포스트 작성/수정 에디터 (Markdown)
- [ ] 빠른 내비게이션 토글 메뉴
- [ ] 좋아요 버튼 UI

## Phase 4: 마무리
- [ ] 반응형 (모바일 대응)
- [ ] 배포 설정

---

## 현재 작업 중
- Entity 클래스 전체 완료 (User, CategoryGroup, Category, Post, Tag, PostTag, PostLike)

## 다음 우선순위
1. DB 연결 설정 (application.yml — PostgreSQL datasource, JPA ddl-auto)
2. Google OAuth 로그인 구현
