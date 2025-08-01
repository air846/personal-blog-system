import request from './index'

export const categoryApi = {
  // 获取分类列表
  getCategoryList() {
    return request({
      url: '/category/list',
      method: 'get'
    })
  }
}