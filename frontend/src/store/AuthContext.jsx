import { createContext, useContext, useEffect, useState } from 'react'
import { getMe, logout as apiLogout } from '../api/authApi.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // OAuth 리다이렉트 후 URL에 포함된 토큰 처리
    const params = new URLSearchParams(window.location.search)
    const urlToken = params.get('token')
    if (urlToken) {
      localStorage.setItem('token', urlToken)
      window.history.replaceState({}, '', window.location.pathname)
    }

    const token = localStorage.getItem('token')
    if (!token) {
      setLoading(false)
      return
    }
    getMe()
      .then(setUser)
      .catch(() => localStorage.removeItem('token'))
      .finally(() => setLoading(false))
  }, [])

  // OAuth 로그인 성공 후 URL에서 토큰을 받아 저장하는 함수
  function saveToken(token) {
    localStorage.setItem('token', token)
    getMe().then(setUser)
  }

  function logout() {
    apiLogout().finally(() => {
      localStorage.removeItem('token')
      setUser(null)
    })
  }

  const isOwner = user?.role === 'OWNER'

  return (
    <AuthContext.Provider value={{ user, loading, isOwner, saveToken, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
