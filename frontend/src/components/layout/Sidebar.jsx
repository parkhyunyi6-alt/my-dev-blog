import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../store/AuthContext.jsx'
import { getCategoryGroups } from '../../api/categoryApi.js'

export default function Sidebar({ open, onClose }) {
  const { user, isOwner } = useAuth()
  const navigate = useNavigate()
  const [categoryGroups, setCategoryGroups] = useState([])
  const [expandedGroups, setExpandedGroups] = useState({})

  useEffect(() => {
    getCategoryGroups()
      .then(setCategoryGroups)
      .catch(() => {})
  }, [])

  function toggleGroup(id) {
    setExpandedGroups(prev => ({ ...prev, [id]: !prev[id] }))
  }

  function handleNav(path) {
    navigate(path)
    onClose()
  }

  return (
    <aside className={`sidebar${open ? ' sidebar--open' : ''}`}>
      <div className="sidebar-profile">
        {user ? (
          <>
            <div className="sidebar-avatar">{user.name?.[0]?.toUpperCase()}</div>
            <div className="sidebar-username">{user.name}</div>
          </>
        ) : (
          <div className="sidebar-avatar sidebar-avatar--guest">?</div>
        )}
      </div>

      {isOwner && (
        <button className="sidebar-write-btn" onClick={() => handleNav('/write')}>
          + 새 글 쓰기
        </button>
      )}

      <nav className="sidebar-nav">
        <Link to="/" className="sidebar-nav-all" onClick={onClose}>전체 글</Link>
        {categoryGroups.map(group => (
          <div key={group.id} className="sidebar-group">
            <button
              className="sidebar-group-title"
              onClick={() => toggleGroup(group.id)}
            >
              {expandedGroups[group.id] ? '▾' : '▸'} {group.name}
            </button>
            {expandedGroups[group.id] && (
              <ul className="sidebar-category-list">
                {group.categories?.map(cat => (
                  <li key={cat.id}>
                    <Link
                      to={`/categories/${cat.id}`}
                      className="sidebar-category-link"
                      onClick={onClose}
                    >
                      {cat.name}
                    </Link>
                  </li>
                ))}
              </ul>
            )}
          </div>
        ))}
      </nav>
    </aside>
  )
}
