import { useState, useEffect } from 'react'
import { addLike, removeLike, getLikeStatus } from '../../api/postApi.js'
import { getDeviceId } from '../../utils/deviceId.js'

export default function LikeButton({ postId, initialCount }) {
  const [liked, setLiked] = useState(false)
  const [count, setCount] = useState(initialCount ?? 0)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const deviceId = getDeviceId()
    getLikeStatus(postId, deviceId)
      .then((data) => setLiked(data.liked))
      .catch(() => {})
  }, [postId])

  async function toggle() {
    if (loading) return
    setLoading(true)
    const deviceId = getDeviceId()
    try {
      if (liked) {
        await removeLike(postId, deviceId)
        setLiked(false)
        setCount((c) => c - 1)
      } else {
        await addLike(postId, deviceId)
        setLiked(true)
        setCount((c) => c + 1)
      }
    } catch {
      // 실패 시 상태 그대로 유지
    } finally {
      setLoading(false)
    }
  }

  return (
    <button
      className={`like-button${liked ? ' like-button--active' : ''}`}
      onClick={toggle}
      disabled={loading}
      aria-label={liked ? '좋아요 취소' : '좋아요'}
    >
      <span className="like-button-icon">{liked ? '♥' : '♡'}</span>
      <span className="like-button-count">{count}</span>
    </button>
  )
}
