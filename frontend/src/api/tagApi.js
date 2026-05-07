import axiosInstance from './axiosInstance.js'

export const getTags = () =>
  axiosInstance.get('/tags').then((r) => r.data.data)

export const getPostsByTag = (tagId, params) =>
  axiosInstance.get(`/tags/${tagId}/posts`, { params }).then((r) => r.data.data)
