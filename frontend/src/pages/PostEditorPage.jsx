import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import ReactMarkdown from 'react-markdown'
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { oneLight } from 'react-syntax-highlighter/dist/esm/styles/prism'
import { getPost, createPost, updatePost, deletePost } from '../api/postApi.js'
import { getCategoryGroups } from '../api/categoryApi.js'
import '../components/post/post.css'
import '../components/common/editor.css'

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

export default function PostEditorPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = Boolean(id)

  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [categoryId, setCategoryId] = useState('')
  const [tagInput, setTagInput] = useState('')
  const [categoryGroups, setCategoryGroups] = useState([])
  const [tab, setTab] = useState('edit')
  const [submitting, setSubmitting] = useState(false)
  const [loadStatus, setLoadStatus] = useState(isEdit ? 'loading' : 'ok')

  useEffect(() => {
    getCategoryGroups().then(setCategoryGroups).catch(() => {})
  }, [])

  useEffect(() => {
    if (!isEdit) return
    getPost(id)
      .then((post) => {
        setTitle(post.title)
        setContent(post.content)
        setCategoryId(post.category?.id ?? '')
        setTagInput(post.tags?.map((t) => t.name).join(', ') ?? '')
        setLoadStatus('ok')
      })
      .catch(() => setLoadStatus('error'))
  }, [id, isEdit])

  async function handleSubmit(e) {
    e.preventDefault()
    if (!title.trim() || !content.trim()) return

    const tags = tagInput
      .split(',')
      .map((t) => t.trim())
      .filter(Boolean)

    const payload = {
      title: title.trim(),
      content,
      categoryId: categoryId ? Number(categoryId) : null,
      tags,
    }

    setSubmitting(true)
    try {
      const result = isEdit
        ? await updatePost(id, payload)
        : await createPost(payload)
      navigate(`/posts/${result.id}`)
    } catch {
      alert('저장에 실패했습니다. 다시 시도해주세요.')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDelete() {
    if (!window.confirm('정말로 이 글을 삭제하시겠습니까?')) return
    try {
      await deletePost(id)
      navigate('/')
    } catch {
      alert('삭제에 실패했습니다.')
    }
  }

  if (loadStatus === 'loading') return <p className="page-status">불러오는 중...</p>
  if (loadStatus === 'error') return <p className="page-status">포스트를 불러올 수 없습니다.</p>

  return (
    <div className="editor-page">
      <div className="editor-header">
        <h1 className="page-title">{isEdit ? '글 수정' : '새 글 쓰기'}</h1>
        {isEdit && (
          <button type="button" className="editor-delete-btn" onClick={handleDelete}>
            삭제
          </button>
        )}
      </div>

      <form className="editor-form" onSubmit={handleSubmit}>
        {/* 제목 */}
        <input
          className="editor-title-input"
          type="text"
          placeholder="제목을 입력하세요"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />

        {/* 카테고리 + 태그 */}
        <div className="editor-meta-row">
          <select
            className="editor-select"
            value={categoryId}
            onChange={(e) => setCategoryId(e.target.value)}
          >
            <option value="">카테고리 없음</option>
            {categoryGroups.map((group) =>
              group.categories?.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {group.name} / {cat.name}
                </option>
              ))
            )}
          </select>

          <input
            className="editor-tag-input"
            type="text"
            placeholder="태그 (쉼표로 구분: java, spring)"
            value={tagInput}
            onChange={(e) => setTagInput(e.target.value)}
          />
        </div>

        {/* 편집 / 미리보기 탭 */}
        <div className="editor-tabs">
          <button
            type="button"
            className={`editor-tab${tab === 'edit' ? ' editor-tab--active' : ''}`}
            onClick={() => setTab('edit')}
          >
            편집
          </button>
          <button
            type="button"
            className={`editor-tab${tab === 'preview' ? ' editor-tab--active' : ''}`}
            onClick={() => setTab('preview')}
          >
            미리보기
          </button>
        </div>

        {tab === 'edit' ? (
          <textarea
            className="editor-textarea"
            placeholder="Markdown으로 내용을 작성하세요..."
            value={content}
            onChange={(e) => setContent(e.target.value)}
            required
          />
        ) : (
          <div className="editor-preview post-detail-body">
            {content.trim() ? (
              <ReactMarkdown components={markdownComponents}>{content}</ReactMarkdown>
            ) : (
              <p className="editor-preview-empty">내용을 입력하면 여기에 미리보기가 표시됩니다.</p>
            )}
          </div>
        )}

        {/* 제출 */}
        <div className="editor-actions">
          <button type="button" className="editor-cancel-btn" onClick={() => navigate(-1)}>
            취소
          </button>
          <button type="submit" className="editor-submit-btn" disabled={submitting}>
            {submitting ? '저장 중...' : isEdit ? '수정 완료' : '발행'}
          </button>
        </div>
      </form>
    </div>
  )
}
