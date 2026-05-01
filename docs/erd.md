# my-dev-blog ERD

## 테이블 목록

- users
- category_groups
- categories
- posts
- tags
- post_tags
- post_likes

---

## users

| 컬럼              | 타입         | 제약                      | 설명                 |
| ----------------- | ------------ | ------------------------- | -------------------- |
| id                | BIGINT       | PK, AUTO_INCREMENT        |                      |
| google_id         | VARCHAR(255) | UNIQUE, NOT NULL          | Google OAuth 식별자  |
| email             | VARCHAR(255) | UNIQUE, NOT NULL          |                      |
| name              | VARCHAR(100) | NOT NULL                  |                      |
| profile_image_url | VARCHAR(500) |                           | Google 프로필 이미지 |
| role              | VARCHAR(20)  | NOT NULL, DEFAULT 'GUEST' | OWNER / GUEST        |
| created_at        | TIMESTAMP    | NOT NULL                  |                      |

> role 값: `OWNER`(블로그 주인), `GUEST`(일반 로그인 사용자)
> 현재는 OWNER 1명만 글쓰기 가능. 나중에 일반 회원 확장 시 role 추가.

---

## category_groups

| 컬럼          | 타입         | 제약                | 설명                        |
| ------------- | ------------ | ------------------- | --------------------------- |
| id            | BIGINT       | PK, AUTO_INCREMENT  |                             |
| name          | VARCHAR(100) | NOT NULL            | 그룹명 (예: "개발", "일상") |
| display_order | INT          | NOT NULL, DEFAULT 0 | 사이드바 표시 순서          |
| created_at    | TIMESTAMP    | NOT NULL            |                             |

---

## categories

| 컬럼              | 타입         | 제약                    | 설명              |
| ----------------- | ------------ | ----------------------- | ----------------- |
| id                | BIGINT       | PK, AUTO_INCREMENT      |                   |
| category_group_id | BIGINT       | FK → category_groups.id |                   |
| name              | VARCHAR(100) | NOT NULL                | 카테고리명        |
| display_order     | INT          | NOT NULL, DEFAULT 0     | 그룹 내 표시 순서 |
| created_at        | TIMESTAMP    | NOT NULL                |                   |

---

## posts

| 컬럼        | 타입         | 제약                         | 설명                               |
| ----------- | ------------ | ---------------------------- | ---------------------------------- |
| id          | BIGINT       | PK, AUTO_INCREMENT           |                                    |
| user_id     | BIGINT       | FK → users.id, NOT NULL      | 작성자                             |
| category_id | BIGINT       | FK → categories.id, NOT NULL |                                    |
| title       | VARCHAR(255) | NOT NULL                     |                                    |
| content     | TEXT         | NOT NULL                     | Markdown 원문                      |
| view_count  | INT          | NOT NULL, DEFAULT 0          | 조회수                             |
| heart_count | INT          | NOT NULL, DEFAULT 0          | 좋아요 수 (post_likes 수와 동기화) |
| created_at  | TIMESTAMP    | NOT NULL                     |                                    |
| updated_at  | TIMESTAMP    | NOT NULL                     |                                    |

> heart_count는 post_likes 조회 대신 캐싱 목적으로 posts에 직접 보관.

---

## tags

| 컬럼       | 타입        | 제약               | 설명   |
| ---------- | ----------- | ------------------ | ------ |
| id         | BIGINT      | PK, AUTO_INCREMENT |        |
| name       | VARCHAR(50) | UNIQUE, NOT NULL   | 태그명 |
| created_at | TIMESTAMP   | NOT NULL           |        |

---

## post_tags

| 컬럼    | 타입   | 제약                    | 설명 |
| ------- | ------ | ----------------------- | ---- |
| id      | BIGINT | PK, AUTO_INCREMENT      |      |
| post_id | BIGINT | FK → posts.id, NOT NULL |      |
| tag_id  | BIGINT | FK → tags.id, NOT NULL  |      |

UNIQUE 제약: `(post_id, tag_id)`

> 포스트 1개당 태그 최대 10개는 애플리케이션 레벨에서 검증.

---

## post_likes

| 컬럼       | 타입         | 제약                     | 설명                      |
| ---------- | ------------ | ------------------------ | ------------------------- |
| id         | BIGINT       | PK, AUTO_INCREMENT       |                           |
| post_id    | BIGINT       | FK → posts.id, NOT NULL  |                           |
| user_id    | BIGINT       | FK → users.id, NULL 허용 | 로그인 사용자면 저장      |
| device_id  | VARCHAR(255) | NULL 허용                | 비로그인 사용자 기기 식별 |
| created_at | TIMESTAMP    | NOT NULL                 |                           |

UNIQUE 제약:

- `(post_id, user_id)` — 로그인 사용자 중복 방지
- `(post_id, device_id)` — 비로그인 사용자 중복 방지

> user_id와 device_id 둘 다 NULL이면 안 됨 → 애플리케이션 레벨에서 검증.
> 로그인 사용자가 좋아요 시 device_id도 함께 저장하면 로그아웃 후 중복 방지 가능 (선택).

---

## 관계 요약

```
category_groups 1 ── N categories
categories      1 ── N posts
users           1 ── N posts
posts           1 ── N post_tags ── N tags
posts           1 ── N post_likes
users           1 ── N post_likes (user_id nullable)
```
