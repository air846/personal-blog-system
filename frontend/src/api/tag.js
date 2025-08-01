import request from './index'

// 标签相关API
export const tagApi = {
  // 获取标签列表
  getTagList() {
    return request.get('/tag/list')
  },

  // 获取标签详情
  getTagDetail(id) {
    return request.get(`/tag/${id}`)
  }
}