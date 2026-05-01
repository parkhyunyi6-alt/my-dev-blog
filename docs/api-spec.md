# my-dev-blog API 명세

## 공통

### Base URL

```
http://localhost:8080/api
```

### 응답 형식

```json
{
  "success": true,
  "data": {},
  "message": "성공"
}
```

### 인증

- Google OAuth2 로그인 후 서버에서 JWT 발급
- 인증 필요 API는 `Authorization: Bearer {token}` 헤더 포함
- 🔒 표시 = 로그인 필요 / 🔑 표시 = OWNER만 가능

---

## Auth

| 메서드 | 경로                    | 설명                                | 인증 |
| ------ | ----------------------- | ----------------------------------- | ---- |
| GET    | `/auth/google`          | Google OAuth 로그인 시작 (redirect) | -    |
| GET    | `/auth/google/callback` | Google OAuth 콜백, JWT 발급         | -    |
| POST   | `/auth/logout`          | 로그아웃                            | 🔒   |
| GET    | `/auth/me`              | 내 정보 조회                        | 🔒   |

#### GET `/auth/me` 응답

```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@gmail.com",
    "name": "홍길동",
    "profileImageUrl": "https://...",
    "role": "OWNER"
  }
}
```

---

## Category Group

| 메서드 | 경로                    | 설명                                | 인증 |
| ------ | ----------------------- | ----------------------------------- | ---- |
| GET    | `/category-groups`      | 전체 그룹 + 하위 카테고리 목록 조회 | -    |
| POST   | `/category-groups`      | 그룹 생성                           | 🔑   |
| PUT    | `/category-groups/{id}` | 그룹 수정                           | 🔑   |
| DELETE | `/category-groups/{id}` | 그룹 삭제                           | 🔑   |

#### GET `/category-groups` 응답

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "개발",
      "displayOrder": 1,
      "categories": [
        { "id": 1, "name": "Spring", "displayOrder": 1 },
        { "id": 2, "name": "React", "displayOrder": 2 }
      ]
    }
  ]
}
```

---

## Category

| 메서드 | 경로               | 설명          | 인증 |
| ------ | ------------------ | ------------- | ---- |
| POST   | `/categories`      | 카테고리 생성 | 🔑   |
| PUT    | `/categories/{id}` | 카테고리 수정 | 🔑   |
| DELETE | `/categories/{id}` | 카테고리 삭제 | 🔑   |

---

## Tag

| 메서드 | 경로               | 설명                    | 인증 |
| ------ | ------------------ | ----------------------- | ---- |
| GET    | `/tags`            | 전체 태그 목록 조회     | -    |
| GET    | `/tags/{id}/posts` | 태그별 포스트 목록 조회 | -    |

#### GET `/tags/{id}/posts` 쿼리 파라미터

| 파라미터 | 타입 | 필수 | 설명                     |
| -------- | ---- | ---- | ------------------------ |
| page     | Int  | 선택 | 페이지 번호 (기본값: 1)  |
| size     | Int  | 선택 | 페이지 크기 (기본값: 10) |

#### GET `/tags/{id}/posts` 응답

```json
{
  "success": true,
  "data": {
    "tag": { "id": 1, "name": "java" },
    "posts": [
      {
        "id": 1,
        "title": "Spring Boot 시작하기",
        "category": { "id": 1, "name": "Spring" },
        "tags": [{ "id": 1, "name": "java" }],
        "author": { "id": 1, "name": "홍길동" },
        "viewCount": 100,
        "heartCount": 10,
        "createdAt": "2025-01-01T00:00:00"
      }
    ],
    "totalPages": 3,
    "totalElements": 25,
    "currentPage": 1
  }
}
```

---

## Post

| 메서드 | 경로                    | 설명                                    | 인증 |
| ------ | ----------------------- | --------------------------------------- | ---- |
| GET    | `/posts`                | 포스트 목록 조회 (카테고리 필터/페이지) | -    |
| GET    | `/posts/latest`         | 최신 포스트 1개 조회                    | -    |
| GET    | `/posts/{id}`           | 포스트 상세 조회                        | -    |
| GET    | `/posts/{id}/neighbors` | 빠른 내비게이션용 포스트 목록           | -    |
| POST   | `/posts`                | 포스트 작성                             | 🔑   |
| PUT    | `/posts/{id}`           | 포스트 수정                             | 🔑   |
| DELETE | `/posts/{id}`           | 포스트 삭제                             | 🔑   |

#### GET `/posts` 쿼리 파라미터

| 파라미터   | 타입 | 필수 | 설명                     |
| ---------- | ---- | ---- | ------------------------ |
| categoryId | Long | 선택 | 카테고리 필터            |
| page       | Int  | 선택 | 페이지 번호 (기본값: 1)  |
| size       | Int  | 선택 | 페이지 크기 (기본값: 10) |

#### GET `/posts` 응답

```json
{
  "success": true,
  "data": {
    "posts": [
      {
        "id": 1,
        "title": "Spring Boot 시작하기",
        "category": { "id": 1, "name": "Spring" },
        "tags": [{ "id": 1, "name": "java" }],
        "author": { "id": 1, "name": "홍길동" },
        "viewCount": 100,
        "heartCount": 10,
        "createdAt": "2025-01-01T00:00:00"
      }
    ],
    "totalPages": 5,
    "totalElements": 42,
    "currentPage": 1
  }
}
```

#### GET `/posts/{id}` 응답

```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Spring Boot 시작하기",
    "content": "## 시작하기\n...",
    "category": { "id": 1, "name": "Spring" },
    "tags": [{ "id": 1, "name": "java" }],
    "author": { "id": 1, "name": "홍길동", "profileImageUrl": "https://..." },
    "viewCount": 101,
    "heartCount": 10,
    "createdAt": "2025-01-01T00:00:00",
    "updatedAt": "2025-01-02T00:00:00"
  }
}
```

#### GET `/posts/{id}/neighbors` 응답

```json
{
  "success": true,
  "data": {
    "currentPostId": 3,
    "posts": [
      { "id": 1, "title": "이전 글 (부족분 채우기)" },
      { "id": 2, "title": "이전 글 (부족분 채우기)" },
      { "id": 3, "title": "현재 글" },
      { "id": 4, "title": "이후 글 1" },
      { "id": 5, "title": "이후 글 2" },
      { "id": 6, "title": "이후 글 3" }
    ]
  }
}
```

> 기본: 현재 글 + 이후 5개 (총 6개).
> 이후 글이 N개 미만이면 이전 글로 (5-N)개 채워서 항상 총 6개 유지.
> posts 배열은 오래된 순 정렬 (위에서 아래로 시간순).

#### POST `/posts` 요청

```json
{
  "title": "Spring Boot 시작하기",
  "content": "## 시작하기\n...",
  "categoryId": 1,
  "tagIds": [1, 2, 3]
}
```

---

## Post Like

| 메서드 | 경로                       | 설명             | 인증 |
| ------ | -------------------------- | ---------------- | ---- |
| POST   | `/posts/{id}/likes`        | 좋아요 추가      | -    |
| DELETE | `/posts/{id}/likes`        | 좋아요 취소      | -    |
| GET    | `/posts/{id}/likes/status` | 좋아요 여부 확인 | -    |

#### POST `/posts/{id}/likes` 요청

```json
{
  "deviceId": "abc123"
}
```

> 로그인 사용자면 JWT에서 user_id 추출. deviceId는 항상 포함.

#### GET `/posts/{id}/likes/status` 응답

```json
{
  "success": true,
  "data": {
    "liked": true
  }
}
```

---

## Post View Count

| 메서드 | 경로                | 설명        | 인증 |
| ------ | ------------------- | ----------- | ---- |
| POST   | `/posts/{id}/views` | 조회수 증가 | -    |

> 포스트 상세 조회(GET /posts/{id})와 분리.
> 프론트에서 상세 페이지 진입 시 별도 호출.
