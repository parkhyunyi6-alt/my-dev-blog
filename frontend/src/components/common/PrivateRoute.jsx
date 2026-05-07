import { Navigate } from 'react-router-dom'
import { useAuth } from '../../store/AuthContext.jsx'

export default function PrivateRoute({ children }) {
  const { isOwner, loading } = useAuth()

  if (loading) return <p className="page-status">불러오는 중...</p>
  if (!isOwner) return <Navigate to="/" replace />

  return children
}
