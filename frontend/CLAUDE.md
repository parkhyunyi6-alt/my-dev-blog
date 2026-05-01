# Frontend — React Context

## Project Structure
```
frontend/src/
├── components/
│   ├── layout/      # 레이아웃 (Sidebar, Header 등)
│   ├── post/        # 포스트 관련 컴포넌트
│   ├── category/    # 카테고리 관련
│   └── common/      # 공통 UI (Button, Modal 등)
├── pages/           # 라우트별 페이지 컴포넌트
├── hooks/           # 커스텀 훅
├── api/             # axios API 호출 함수
├── store/           # 전역 상태 (Context API 또는 Zustand)
└── utils/           # 유틸 함수
```

## 레이아웃 구조 (요구사항 기반)
```
┌──────────────────────────────────────────┐
│  [프로필]            [Login/Logout 버튼]   │ ← Header
├──────────┬───────────────────────────────┤
│ 프로필   │                               │
│ [글쓰기] │     포스트 내용 / 목록         │
│          │     (중앙 콘텐츠 영역)         │
│ 카테고리 │                               │
│ 그룹 목록│                               │
│  └ 카테고리                              │
└──────────┴───────────────────────────────┘
           ← Sidebar (left)
```

## Naming Rules
- 컴포넌트 파일: PascalCase (`PostCard.jsx`, `CategoryList.jsx`)
- 훅 파일: camelCase + use 접두사 (`usePost.js`, `useAuth.js`)
- API 파일: camelCase (`postApi.js`, `categoryApi.js`)
- CSS 클래스명: kebab-case (`post-card`, `sidebar-nav`)

## API 호출 규칙
- 모든 API 호출은 `src/api/` 내 함수를 통해서만
- axios 인스턴스는 `src/api/axiosInstance.js`에 중앙화
- 인터셉터에서 JWT 토큰 자동 첨부

## 주요 Rules
- props drilling 3단계 이상이면 Context 또는 상태관리 도입 검토
- 컴포넌트 하나의 역할은 하나로 제한 (SRP)
- 인증이 필요한 라우트는 `PrivateRoute`로 감싸기
- 이미지/아이콘은 `src/assets/`에 저장

## Dev Server
```bash
cd frontend
npm install     # 최초 1회
npm run dev     # 개발 서버 실행 (기본 포트: 5173)
npm run build   # 프로덕션 빌드
```

## 백엔드 API Base URL
- 개발: `http://localhost:8080/api`
- 환경변수: `VITE_API_BASE_URL` (`.env.local`에 정의)
