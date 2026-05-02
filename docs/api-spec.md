# my-dev-blog API 명세

## 공통

### Base URL
```
http://localhost:8082/api
```

### 성공 응답 형식
```json
{
  "success": true,
  "data": { },
  "message": "성공"
}
```

### 에러 응답 형식
```json
{
  "success": false,
  "data": null,
  "message": "포스트를 찾을 수 없습니다."
}
```

| HTTP 상태코드 | 설명 |
|---|---|
| 400 | 잘못된 요청 (유효성 검사 실패 등) |
| 401 | 인증 필요 (토큰 없음 또는 만료) |
| 403 | 권한 없음 (OWNER 아닌 사용자) |
| 404 | 리소스를 찾을 수 없음 |
| 500 | 서버 내부 오류 |

### 인증
- Google OAuth2 로그인 후 서버에서 JWT 발급
- 인증 필요 API는 `Authorization: Bearer {token}` 헤더 포함
- 🔒 표시 = 로그인 필요 / 🔑 표시 = OWNER만 가능

### 페이지네이션
- `page` 파라미터는 1-based (1, 2, 3...)
- 서버 내부에서 Spring Pageable의 0-based로 변환 처리

---

## Auth

> Spring Security OAuth2 Client 기본 경로 사용.
> 프론트에서 로그인 버튼 클릭 시 `/oauth2/authorization/google`로 redirect.

| 메서드 | 경로 | 설명 | 인증 |
|---|---|---|---|
| GET | `/oauth2/authorization/google` | Google OAuth 로그인 시작 (Spring Security 기본 경로) | - |
| GET | `/login/oauth2/code/google` | Google OAuth 콜백, JWT 발급 (Spring Security 기본 경로) | - |
| POST | `/auth/logout` | 로그아웃 | 🔒 |
| GET | `/auth/me` | 내 정보 조회 | 🔒 |

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

| 메서드 | 경로 | 설명 | 인증 |
|---|---|---|---|
| GET | `/category-groups` | 전체 그룹 + 하위 카테고리 목록 조회 | - |
| POST | `/category-groups` | 그룹 생성 | 🔑 |
| PUT | `/category-groups/{id}` | 그룹 수정 | 🔑 |
| DELETE | `/category-groups/{id}` | 그룹 삭제 | 🔑 |

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

> 하위 카테고리가 존재하는 그룹은 삭제 불가 (400 반환). 프론트에서 alert 표시.

#### POST `/category-groups` 요청
```json
{
  "name": "개발",
  "displayOrder": 1
}
```

#### PUT `/category-groups/{id}` 요청
```json
{
  "name": "개발",
  "displayOrder": 1
}
```

---

## Category

| 메서드 | 경로 | 설명 | 인증 |
|---|---|---|---|
| POST | `/categories` | 카테고리 생성 | 🔑 |
| PUT | `/categories/{id}` | 카테고리 수정 | 🔑 |
| DELETE | `/categories/{id}` | 카테고리 삭제 | 🔑 |

> 포스트가 존재하는 카테고리는 삭제 불가 (400 반환). 프론트에서 alert 표시.

#### POST `/categories` 요청
```json
{
  "categoryGroupId": 1,
  "name": "Spring",
  "displayOrder": 1
}
```

#### PUT `/categories/{id}` 요청
```json
{
  "categoryGroupId": 1,
  "name": "Spring",
  "displayOrder": 1
}
```
> `categoryGroupId`는 선택 — 생략 시 기존 그룹 유지.

---

## Tag

| 메서드 | 경로 | 설명 | 인증 |
|---|---|---|---|
| GET | `/tags` | 전체 태그 목록 조회 | - |

#### GET `/tags` 응답
```json
{
  "success": true,
  "data": [
    { "id": 1, "name": "java" },
    { "id": 2, "name": "spring" },
    { "id": 3, "name": "react" }
  ]
}
```

> 태그 생성은 별도 API 없음.
> POST /posts 또는 PUT /posts/{id} 요청 시 `tags` 문자열 배열로 전달하면 서버에서 없는 태그 자동 생성.

---

## Tag — 태그별 포스트 목록

| 메서드 | 경로 | 설명 | 인증 |
|---|---|---|---|
| GET | `/tags/{id}/posts` | 태그별 포스트 목록 조회 | - |

#### GET `/tags/{id}/posts` 쿼리 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| page | Int | 선택 | 페이지 번호 (기본값: 1) |
| size | Int | 선택 | 페이지 크기 (기본값: 10) |

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

| 메서드 | 경로 | 설명 | 인증 |
|---|---|---|---|
| GET | `/posts` | 포스트 목록 조회 (카테고리 필터/페이지) | - |
| GET | `/posts/latest` | 최신 포스트 1개 조회 | - |
| GET | `/posts/{id}` | 포스트 상세 조회 | - |
| GET | `/posts/{id}/neighbors` | 빠른 내비게이션용 포스트 목록 | - |
| POST | `/posts` | 포스트 작성 | 🔑 |
| PUT | `/posts/{id}` | 포스트 수정 | 🔑 |
| DELETE | `/posts/{id}` | 포스트 삭제 | 🔑 |

#### GET `/posts` 쿼리 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| categoryId | Long | 선택 | 카테고리 필터 |
| page | Int | 선택 | 페이지 번호 (기본값: 1) |
| size | Int | 선택 | 페이지 크기 (기본값: 10) |

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

#### GET `/posts/latest` 응답
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
> /posts/{id} 응답과 동일한 구조.

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
  "tags": ["java", "spring", "backend"]
}
```
> `tags`는 문자열 배열. 없는 태그는 서버에서 자동 생성. 최대 10개.

#### PUT `/posts/{id}` 요청
```json
{
  "title": "Spring Boot 시작하기 (수정)",
  "content": "## 시작하기\n...",
  "categoryId": 1,
  "tags": ["java", "spring"]
}
```

---

## Post Like

| 메서드 | 경로 | 설명 | 인증 |
|---|---|---|---|
| POST | `/posts/{id}/likes` | 좋아요 추가 | - |
| DELETE | `/posts/{id}/likes` | 좋아요 취소 | - |
| GET | `/posts/{id}/likes/status` | 좋아요 여부 확인 | - |

#### POST `/posts/{id}/likes` 요청
```json
{
  "deviceId": "abc123"
}
```
> 로그인 사용자면 JWT에서 user_id 추출. deviceId는 항상 포함.

#### DELETE `/posts/{id}/likes` 요청
```json
{
  "deviceId": "abc123"
}
```
> 로그인 사용자면 JWT에서 user_id로 취소. 비로그인이면 deviceId로 취소.

#### GET `/posts/{id}/likes/status` 응답
```json
{
  "success": true,
  "data": {
    "liked": true
  }
}
```
> 로그인 사용자면 user_id 기준, 비로그인이면 쿼리 파라미터 `?deviceId=abc123` 기준.

---

## Post View Count

| 메서드 | 경로 | 설명 | 인증 |
|---|---|---|---|
| POST | `/posts/{id}/views` | 조회수 증가 | - |

> 포스트 상세 조회(GET /posts/{id})와 분리.
> 프론트에서 상세 페이지 진입 시 1회만 호출. 중복 방지는 프론트 책임.
