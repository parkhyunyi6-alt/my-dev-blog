# 개발 진행 상황

> Claude Code 세션 시작 시 이 파일을 읽어서 현재 상태를 파악할 것.
> 기능 완료 시 [x]로 체크, 작업 중인 항목은 → 표시.

## Phase 1: 기반 설계
- [x] ERD 확정 (`docs/erd.md`)
- [ ] API 명세 초안 (`docs/api-spec.md`)
- [x] 백엔드 프로젝트 초기 세팅 (Spring Boot 3.5.14 / Java 21)
- [ ] 프론트엔드 프로젝트 초기 세팅 (Vite)
- [x] DB 연결 설정 (PostgreSQL datasource, JPA ddl-auto: validate)
- [x] Flyway 마이그레이션 설정 + `V1__init_schema.sql` 작성
- [x] Entity 전체 생성 (User, CategoryGroup, Category, Post, Tag, PostTag, PostLike)
- [x] Repository 전체 생성 (7개 — 위 Entity에 대응)
- [x] build.gradle 의존성 추가 (Security, OAuth2, JPA, Flyway, JJWT, Validation, PostgreSQL)

## Phase 2: 백엔드 핵심 기능
- → User 엔티티 + Google OAuth 로그인
  - [x] User 엔티티 + UserRepository + UserService / UserServiceImpl
  - [x] JwtTokenProvider (JWT 생성 / 검증 유틸)
  - [ ] SecurityConfig (Spring Security 필터 체인)
  - [ ] OAuth2UserService (Google 로그인 콜백 처리)
  - [ ] JWT 인증 필터 (OncePerRequestFilter)
  - [ ] AuthController (`/api/auth/me` 등)
- → CategoryGroup + Category CRUD
  - [x] CategoryService / CategoryServiceImpl (CRUD 로직 완료)
  - [x] DTO 완료 (CategoryGroupCreateRequest/UpdateRequest, CategoryCreateRequest/UpdateRequest, CategoryGroupResponse, CategoryResponse)
  - [ ] CategoryController
- [ ] Tag CRUD (Service / Controller 미작성)
- → Post CRUD
  - [x] PostService / PostServiceImpl (기본 CRUD + 페이징 + 이웃 포스트 조회)
  - [x] DTO 완료 (PostCreateRequest/UpdateRequest, PostDetailResponse, PostListItemResponse, PostSummaryResponse, PostNeighborsResponse, PagedPostResponse, AuthorResponse)
  - [ ] PostController
- [x] Post-Tag 연관 관계 (PostTag 엔티티 + 연관 처리 — PostServiceImpl 내 포함)
- [x] Post 목록 조회 (카테고리 필터 + 페이징 — PostService 인터페이스 정의 완료)
- [ ] 조회수 카운트
- [ ] 좋아요 기능 (PostLike 엔티티 + Repository 완료, Service / Controller 미작성)
- [x] 커스텀 예외 (NotFoundException, ForbiddenException, ConflictException)

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
- 백엔드 Service 레이어 완료, Controller 레이어 미작성

## 다음 우선순위
1. SecurityConfig + OAuth2UserService + JWT 인증 필터 구현 (Google OAuth 로그인 완성)
2. AuthController 작성 (`/api/auth/me`)
3. CategoryController 작성
4. PostController 작성
5. TagService + TagController 작성
