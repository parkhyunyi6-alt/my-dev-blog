import './common.css'

export default function Pagination({ currentPage, totalPages, onPageChange }) {
  if (totalPages <= 1) return null

  const pages = Array.from({ length: totalPages }, (_, i) => i + 1)

  return (
    <nav className="pagination">
      <button
        className="pagination-btn"
        disabled={currentPage <= 1}
        onClick={() => onPageChange(currentPage - 1)}
      >
        이전
      </button>
      {pages.map(page => (
        <button
          key={page}
          className={`pagination-btn${page === currentPage ? ' pagination-btn--active' : ''}`}
          onClick={() => onPageChange(page)}
        >
          {page}
        </button>
      ))}
      <button
        className="pagination-btn"
        disabled={currentPage >= totalPages}
        onClick={() => onPageChange(currentPage + 1)}
      >
        다음
      </button>
    </nav>
  )
}
