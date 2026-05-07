import { useEffect, useState } from 'react'
import { useParams, useSearchParams } from 'react-router-dom'
import PostCard from '../components/post/PostCard.jsx'
import Pagination from '../components/common/Pagination.jsx'
import { getPostsByTag } from '../api/tagApi.js'

export default function TagPage() {
  const { tagId } = useParams()
  const [searchParams, setSearchParams] = useSearchParams()
  const page = Number(searchParams.get('page') ?? 1)

  const [data, setData] = useState(null)
  const [status, setStatus] = useState('loading')

  useEffect(() => {
    setStatus('loading')
    setData(null)

    getPostsByTag(tagId, { page, size: 10 })
      .then((postData) => {
        setData(postData)
        setStatus('ok')
      })
      .catch(() => setStatus('error'))
  }, [page, tagId])

  function handlePageChange(newPage) {
    setSearchParams({ page: newPage })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  if (status === 'loading') return <p className="page-status">불러오는 중...</p>
  if (status === 'error') return <p className="page-status">불러오기 실패. 잠시 후 다시 시도해주세요.</p>

  return (
    <div>
      <h1 className="page-title">
        #{data?.tag?.name ?? tagId} <span className="page-count">{data?.totalElements ?? 0}</span>
      </h1>
      {!data || data.posts.length === 0 ? (
        <p className="page-status">이 태그로 작성된 글이 없습니다.</p>
      ) : (
        <>
          <div className="post-list">
            {data.posts.map((post) => (
              <PostCard key={post.id} post={post} />
            ))}
          </div>
          <Pagination
            currentPage={data.currentPage}
            totalPages={data.totalPages}
            onPageChange={handlePageChange}
          />
        </>
      )}
    </div>
  )
}
