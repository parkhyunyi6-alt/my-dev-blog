import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import ReactMarkdown from 'react-markdown'
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { oneLight } from 'react-syntax-highlighter/dist/esm/styles/prism'
import { getPost, getNeighborPosts, incrementViewCount } from '../api/postApi.js'
import { formatDate } from '../utils/formatDate.js'
import { useAuth } from '../store/AuthContext.jsx'
import LikeButton from '../components/post/LikeButton.jsx'
import PostNavigation from '../components/post/PostNavigation.jsx'
import QuickNav from '../components/post/QuickNav.jsx'
import '../components/post/post.css'

const markdownComponents = {
  code({ node, inline, className, children, ...props }) {
    const match = /language-(\w+)/.exec(className || '')
    return !inline && match ? (
      <SyntaxHighlighter style={oneLight} language={match[1]} PreTag="div" {...props}>
        {String(children).replace(/\n$/, '')}
      </SyntaxHighlighter>
    ) : (
      <code className={className} {...props}>
        {children}
      </code>
    )
  },
}

function extractPrevNext(neighborData, currentId) {
  const posts = neighborData?.posts ?? []
  const idx = posts.findIndex((p) => p.id === Number(currentId))
  return {
    prev: idx > 0 ? posts[idx - 1] : null,
    next: idx >= 0 && idx < posts.length - 1 ? posts[idx + 1] : null,
  }
}

export default function PostDetailPage() {
  const { id } = useParams()
  const { isOwner } = useAuth()
  const [post, setPost] = useState(null)
  const [neighborData, setNeighborData] = useState(null)
  const [status, setStatus] = useState('loading')

  useEffect(() => {
    setStatus('loading')
    setPost(null)
    setNeighborData(null)

    Promise.all([
      getPost(id),
      getNeighborPosts(id).catch(() => null),
    ])
      .then(([postData, nd]) => {
        setPost(postData)
        setNeighborData(nd)
        setStatus('ok')
        incrementViewCount(id).catch(() => {})
      })
      .catch(() => setStatus('error'))
  }, [id])

  if (status === 'loading') return <p className="page-status">불러오는 중...</p>
  if (status === 'error') return <p className="page-status">포스트를 불러올 수 없습니다.</p>
  if (!post) return null

  const { prev, next } = extractPrevNext(neighborData, id)

  return (
    <article className="post-detail">
      {/* 헤더 메타 */}
      <header className="post-detail-header">
        <div className="post-detail-header-top">
          {post.category && (
            <Link to={`/categories/${post.category.id}`} className="post-card-category">
              {post.category.name}
            </Link>
          )}
          {isOwner && (
            <Link to={`/posts/${post.id}/edit`} className="post-detail-edit-btn">
              수정
            </Link>
          )}
        </div>
        <h1 className="post-detail-title">{post.title}</h1>
        <div className="post-detail-meta">
          <span>{formatDate(post.createdAt)}</span>
          <span className="post-card-divider">·</span>
          <span>조회 {post.viewCount}</span>
        </div>
        {post.tags?.length > 0 && (
          <div className="post-card-tags">
            {post.tags.map((tag) => (
              <Link key={tag.id} to={`/tags/${tag.id}`} className="tag-badge">
                #{tag.name}
              </Link>
            ))}
          </div>
        )}
      </header>

      {/* 본문 */}
      <div className="post-detail-body">
        <ReactMarkdown components={markdownComponents}>{post.content}</ReactMarkdown>
      </div>

      {/* 좋아요 */}
      <div className="post-detail-like">
        <LikeButton postId={post.id} initialCount={post.heartCount} />
      </div>

      {/* 이전/다음 포스트 */}
      <PostNavigation prev={prev} next={next} />

      {/* 빠른 내비게이션 (floating) */}
      <QuickNav currentPostId={post.id} posts={neighborData?.posts} />
    </article>
  )
}
