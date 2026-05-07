import axiosInstance from './axiosInstance.js'

export const getCategoryGroups = () =>
  axiosInstance.get('/category-groups').then((r) => r.data.data)

export const createCategoryGroup = (data) =>
  axiosInstance.post('/category-groups', data).then((r) => r.data.data)

export const updateCategoryGroup = (id, data) =>
  axiosInstance.put(`/category-groups/${id}`, data).then((r) => r.data.data)

export const deleteCategoryGroup = (id) =>
  axiosInstance.delete(`/category-groups/${id}`).then((r) => r.data)

export const createCategory = (data) =>
  axiosInstance.post('/categories', data).then((r) => r.data.data)

export const updateCategory = (id, data) =>
  axiosInstance.put(`/categories/${id}`, data).then((r) => r.data.data)

export const deleteCategory = (id) =>
  axiosInstance.delete(`/categories/${id}`).then((r) => r.data)
