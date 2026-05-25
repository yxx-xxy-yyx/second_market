import request from './request'

export const forumApi = {
  // 获取帖子列表
  getList(params) {
    return request({
      url: '/forum/list',
      method: 'get',
      params
    })
  },

  // 获取帖子详情
  getDetail(id) {
    return request({
      url: `/forum/detail/${id}`,
      method: 'get'
    })
  },

  // 发布帖子
  create(data) {
    return request({
      url: '/forum/create',
      method: 'post',
      data
    })
  },

  // 发表评论
  addComment(data) {
    return request({
      url: '/forum/comment',
      method: 'post',
      data
    })
  },

  // 获取帖子评论列表
  getComments(postId) {
    return request({
      url: `/forum/comments/${postId}`,
      method: 'get'
    })
  },

  // 删除帖子
  delete(id) {
    return request({
      url: `/forum/delete/${id}`,
      method: 'delete'
    })
  },

  // 删除评论
  deleteComment(id) {
    return request({
      url: `/forum/comment/delete/${id}`,
      method: 'delete'
    })
  },

  // 更新帖子
  update(data) {
    return request({
      url: '/forum/update',
      method: 'put',
      data
    })
  },

  toggleLike(postId) {
    return request({
      url: `/forum/like/${postId}`,
      method: 'post'
    })
  },

  toggleFavorite(postId) {
    return request({
      url: `/forum/favorite/${postId}`,
      method: 'post'
    })
  },

  getMyFavorites(params) {
    return request({
      url: '/forum/favorite/my',
      method: 'get',
      params
    })
  }
}
