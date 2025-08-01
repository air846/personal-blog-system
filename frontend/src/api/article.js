import request from './index'

export const articleApi = {
  // 获取文章列表
  getArticleList(params) {
    return request({
      url: '/article/list',
      method: 'get',
      params
    })
  },
  
  // 获取文章详情
  getArticleDetail(id) {
    return request({
      url: `/article/${id}`,
      method: 'get'
    })
  },
  
  // 创建文章
  createArticle(data) {
    return request({
      url: '/article',
      method: 'post',
      data
    })
  },
  
  // 更新文章
  updateArticle(id, data) {
    return request({
      url: `/article/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除文章
  deleteArticle(id) {
    return request({
      url: `/article/${id}`,
      method: 'delete'
    })
  },
  
  // 发布文章
  publishArticle(id) {
    return request({
      url: `/article/${id}/publish`,
      method: 'post'
    })
  },
  
  // 点赞文章
  likeArticle(id) {
    return request({
      url: `/article/${id}/like`,
      method: 'post'
    })
  },
  
  // 取消点赞
  unlikeArticle(id) {
    return request({
      url: `/article/${id}/like`,
      method: 'delete'
    })
  },
  
  // 获取热门文章
  getHotArticles(limit = 10) {
    return request({
      url: '/article/hot',
      method: 'get',
      params: { limit }
    })
  },
  
  // 获取推荐文章
  getRecommendedArticles(limit = 10) {
    return request({
      url: '/article/recommended',
      method: 'get',
      params: { limit }
    })
  },
  
  // 搜索文章
  searchArticles(params) {
    return request({
      url: '/article/search',
      method: 'get',
      params
    })
  }
}