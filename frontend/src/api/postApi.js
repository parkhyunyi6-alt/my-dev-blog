import axiosInstance from './axiosInstance.js'

export const getPosts = (params) =>
  axiosInstance.get('/posts', { params }).then((r) => r.data.data)

export const getLatestPost = () =>
  axiosInstance.get('/posts/latest').then((r) => r.data.data)

export const getPost = (id) =>
  axiosInstance.get(`/posts/${id}`).then((r) => r.data.data)

export const getNeighborPosts = (id) =>
  axiosInstance.get(`/posts/${id}/neighbors`).then((r) => r.data.data)

export const createPost = (data) =>
  axiosInstance.post('/posts', data).then((r) => r.data.data)

export const updatePost = (id, data) =>
  axiosInstance.put(`/posts/${id}`, data).then((r) => r.data.data)

export const deletePost = (id) =>
  axiosInstance.delete(`/posts/${id}`).then((r) => r.data)

export const incrementViewCount = (id) =>
  axiosInstance.post(`/posts/${id}/views`).then((r) => r.data)

export const addLike = (id, deviceId) =>
  axiosInstance.post(`/posts/${id}/likes`, { deviceId }).then((r) => r.data)

export const removeLike = (id, deviceId) =>
  axiosInstance.delete(`/posts/${id}/likes`, { data: { deviceId } }).then((r) => r.data)

export const getLikeStatus = (id, deviceId) =>
  axiosInstance
    .get(`/posts/${id}/likes/status`, { params: { deviceId } })
    .then((r) => r.data.data)
