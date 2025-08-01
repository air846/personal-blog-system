import request from './index'

// 分类相关API
export const categoryApi = {
  // 获取分类列表
  getCategoryList() {
    return request.get('/category/list')
  },

  // 获取分类详情
  getCategoryDetail(id) {
    return request.get(`/category/${id}`)
  }
}