import request from './index'

export const tagApi = {
  // 获取标签列表
  getTagList() {
    return request({
      url: '/tag/list',
      method: 'get'
    })
  }
}