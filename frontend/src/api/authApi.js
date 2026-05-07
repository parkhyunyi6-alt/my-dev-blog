import axiosInstance from './axiosInstance.js'

export const getMe = () =>
  axiosInstance.get('/auth/me').then((r) => r.data.data)

export const logout = () =>
  axiosInstance.post('/auth/logout').then((r) => r.data)
