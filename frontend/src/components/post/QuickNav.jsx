import { useState, useEffect, useRef } from 'react'
import { Link, useNavigate } from 'react-router-dom'

export default function QuickNav({ currentPostId, posts }) {
  const [open, setOpen] = useState(false)
  const panelRef = useRef(null)
  const navigate = useNavigate()

  // 패널 외부 클릭 시 닫기
  useEffect(() => {
    if (!open) return
    function handleClick(e) {
      if (panelRef.current && !panelRef.current.contains(e.target)) {
        setOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClick)
    return () => document.removeEventListener('mousedown', handleClick)
  }, [open])

  if (!posts || posts.length === 0) return null

  return (
    <div className="quick-nav" ref={panelRef}>
      <button
        className={`quick-nav-toggle${open ? ' quick-nav-toggle--open' : ''}`}
        onClick={() => setOpen((o) => !o)}
        aria-label="빠른 내비게이션"
        title="빠른 내비게이션"
      >
        ≡
      </button>

      {open && (
        <div className="quick-nav-panel">
          <p className="quick-nav-heading">이 시리즈의 글</p>
          <ul className="quick-nav-list">
            {posts.map((post) => {
              const isCurrent = post.id === currentPostId
              return (
                <li key={post.id} className={`quick-nav-item${isCurrent ? ' quick-nav-item--current' : ''}`}>
                  {isCurrent ? (
                    <span className="quick-nav-link quick-nav-link--current">{post.title}</span>
                  ) : (
                    <Link
                      to={`/posts/${post.id}`}
                      className="quick-nav-link"
                      onClick={() => setOpen(false)}
                    >
                      {post.title}
                    </Link>
                  )}
                </li>
              )
            })}
          </ul>
        </div>
      )}
    </div>
  )
}
