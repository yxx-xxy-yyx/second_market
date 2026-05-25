import request from './request'

export const reviewApi = {
  createReview(data) {
    return request({
      url: '/review/add',
      method: 'post',
      data
    })
  },

  createErrandReview(data) {
    return request({
      url: '/review/errand/add',
      method: 'post',
      data
    })
  },

  getProductReviews(productId, pageNum = 1, pageSize = 10) {
    return request({
      url: `/review/product/${productId}`,
      method: 'get',
      params: { pageNum, pageSize }
    })
  },

  getErrandReviews(campusServiceId, pageNum = 1, pageSize = 10) {
    return request({
      url: `/review/errand/${campusServiceId}`,
      method: 'get',
      params: { pageNum, pageSize }
    })
  },

  getReviewsByProductId(productId, pageNum = 1, pageSize = 10) {
    return request({
      url: `/review/product/${productId}`,
      method: 'get',
      params: { pageNum, pageSize }
    })
  },

  getMyReviews(data) {
    return request({
      url: '/review/my',
      method: 'post',
      data
    })
  },

  deleteReview(id) {
    return request({
      url: `/review/delete/${id}`,
      method: 'delete'
    })
  },

  checkReviewed(orderId) {
    return request({
      url: `/review/check/${orderId}`,
      method: 'get'
    })
  },

  checkErrandReviewed(campusServiceId) {
    return request({
      url: `/review/errand/check/${campusServiceId}`,
      method: 'get'
    })
  },

  getUserRating(userId) {
    return request({
      url: `/review/rating/${userId}`,
      method: 'get'
    })
  }
}

