import { useEffect, useState } from 'react'
import { useParams, useSearchParams } from 'react-router-dom'
import PostCard from '../components/post/PostCard.jsx'
import Pagination from '../components/common/Pagination.jsx'
import { getPosts } from '../api/postApi.js'

export default function HomePage() {
  const { categoryId } = useParams()
  const [searchParams, setSearchParams] = useSearchParams()
  const page = Number(searchParams.get('page') ?? 1)

  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  useEffect(() => {
    setLoading(true)
    setError(false)
    getPosts({ page, size: 10, categoryId: categoryId ?? undefined })
      .then(setData)
      .catch(() => setError(true))
      .finally(() => setLoading(false))
  }, [page, categoryId])

  function handlePageChange(newPage) {
    setSearchParams({ page: newPage })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  if (loading) return <p className="page-status">불러오는 중...</p>
  if (error)   return <p className="page-status">불러오기 실패. 잠시 후 다시 시도해주세요.</p>
  if (!data || data.posts.length === 0) return <p className="page-status">아직 작성된 글이 없습니다.</p>

  return (
    <div>
      <h1 className="page-title">
        전체 글 <span className="page-count">{data.totalElements}</span>
      </h1>
      <div className="post-list">
        {data.posts.map(post => (
          <PostCard key={post.id} post={post} />
        ))}
      </div>
      <Pagination
        currentPage={data.currentPage}
        totalPages={data.totalPages}
        onPageChange={handlePageChange}
      />
    </div>
  )
}
