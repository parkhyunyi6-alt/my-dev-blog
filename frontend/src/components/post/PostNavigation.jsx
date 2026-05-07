import { Link } from 'react-router-dom'

export default function PostNavigation({ prev, next }) {
  if (!prev && !next) return null

  return (
    <nav className="post-navigation">
      <div className="post-nav-item post-nav-prev">
        {prev ? (
          <Link to={`/posts/${prev.id}`}>
            <span className="post-nav-label">← 이전 글</span>
            <span className="post-nav-title">{prev.title}</span>
          </Link>
        ) : (
          <span className="post-nav-empty" />
        )}
      </div>
      <div className="post-nav-item post-nav-next">
        {next ? (
          <Link to={`/posts/${next.id}`}>
            <span className="post-nav-label">다음 글 →</span>
            <span className="post-nav-title">{next.title}</span>
          </Link>
        ) : (
          <span className="post-nav-empty" />
        )}
      </div>
    </nav>
  )
}
