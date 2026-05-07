import { Link } from 'react-router-dom'
import { useAuth } from '../../store/AuthContext.jsx'

export default function Header({ onMenuToggle }) {
  const { user, logout } = useAuth()

  function handleLogin() {
    const base = import.meta.env.VITE_OAUTH_BASE_URL ?? ''
    window.location.href = `${base}/oauth2/authorization/google`
  }

  return (
    <header className="header">
      <div className="header-left">
        <button className="header-menu-btn" onClick={onMenuToggle} aria-label="메뉴">
          ☰
        </button>
        <Link to="/" className="header-title">My Dev Blog</Link>
      </div>
      <div className="header-actions">
        {user ? (
          <div className="header-user">
            <span className="header-user-name">{user.name}</span>
            <button className="header-btn" onClick={logout}>로그아웃</button>
          </div>
        ) : (
          <button className="header-btn header-btn--primary" onClick={handleLogin}>
            Google 로그인
          </button>
        )}
      </div>
    </header>
  )
}
