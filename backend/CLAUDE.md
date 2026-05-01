# Backend — Spring Boot Context

## Package Structure
```
backend/src/main/java/com/blog/
├── domain/          # JPA Entity 클래스
├── repository/      # JPA Repository 인터페이스
├── service/         # 비즈니스 로직
├── controller/      # REST API 컨트롤러
├── dto/
│   ├── request/     # 요청 DTO
│   └── response/    # 응답 DTO
├── config/          # Security, CORS 등 설정
└── exception/       # 커스텀 예외 클래스
```

## Entity 목록
- `User` — Google OAuth 사용자 정보
- `Post` — 블로그 포스트
- `Category` — 카테고리 (CategoryGroup에 속함)
- `CategoryGroup` — 카테고리 묶음
- `Tag` — 태그 (Post와 다대다)
- `PostTag` — Post-Tag 중간 테이블
- `PostLike` — 좋아요 (계정 or 디바이스 기준 중복 방지)

## Naming Rules
- Controller: `PostController`, `CategoryController`
- Service: `PostService`, `CategoryService`
- Repository: `PostRepository`, `CategoryRepository`
- DTO: `PostCreateRequest`, `PostDetailResponse`, `PostListResponse`
- Entity: 단수형 (`Post`, `User`, `Tag`)

## API Response 형식
모든 API는 아래 형식으로 응답:
```json
{
  "success": true,
  "data": { ... },
  "message": "성공"
}
```
공통 wrapper 클래스: `ApiResponse<T>`

## 주요 Rules
- Entity에 Lombok `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` 사용
- 양방향 연관관계에서 편의 메서드는 연관관계의 주인(FK 있는 쪽)에 작성
- `@Transactional`은 Service 레이어에만
- Repository에서 쿼리 메서드 이름이 길어지면 `@Query` JPQL 사용
- 비밀번호/토큰 정보는 절대 로그에 출력 금지

## Build & Run
```bash
cd backend
./gradlew bootRun          # 서버 실행
./gradlew test             # 테스트 실행
./gradlew build            # 빌드
```

## DB 연결 정보 위치
`backend/src/main/resources/application.yml`
(로컬 개발용 민감 정보는 `application-local.yml`에 분리, gitignore 처리)
