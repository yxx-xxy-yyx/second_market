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
  },

  // 管理员获取所有帖子
  adminGetAllPosts(params) {
    return request({
      url: '/forum/admin/all-posts',
      method: 'get',
      params
    })
  },

  // 管理员获取所有评论
  adminGetAllComments(params) {
    return request({
      url: '/forum/admin/all-comments',
      method: 'get',
      params
    })
  },

  // 管理员删除帖子
  adminDeletePost(id) {
    return request({
      url: `/forum/admin/delete-post/${id}`,
      method: 'delete'
    })
  },

  // 管理员删除评论
  adminDeleteComment(id) {
    return request({
      url: `/forum/admin/delete-comment/${id}`,
      method: 'delete'
    })
  }
}

// AI推荐API
export const aiRecommendApi = {
  // 获取AI推荐商品
  getRecommendations(data) {
    return request({
      url: '/ai/recommendations',
      method: 'post',
      data
    })
  }
}
