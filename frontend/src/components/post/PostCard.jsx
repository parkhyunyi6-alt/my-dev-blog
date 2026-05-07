import { Link } from 'react-router-dom'
import { formatDate } from '../../utils/formatDate.js'
import './post.css'

export default function PostCard({ post }) {
  return (
    <article className="post-card">
      {post.category && (
        <div>
          <Link to={`/categories/${post.category.id}`} className="post-card-category">
            {post.category.name}
          </Link>
        </div>
      )}
      <h2 className="post-card-title">
        <Link to={`/posts/${post.id}`}>{post.title}</Link>
      </h2>
      {post.tags.length > 0 && (
        <div className="post-card-tags">
          {post.tags.map(tag => (
            <Link key={tag.id} to={`/tags/${tag.id}`} className="tag-badge">
              #{tag.name}
            </Link>
          ))}
        </div>
      )}
      <div className="post-card-footer">
        <span>{formatDate(post.createdAt)}</span>
        <span className="post-card-divider">·</span>
        <span>조회 {post.viewCount}</span>
        <span className="post-card-divider">·</span>
        <span>♡ {post.heartCount}</span>
      </div>
    </article>
  )
}
