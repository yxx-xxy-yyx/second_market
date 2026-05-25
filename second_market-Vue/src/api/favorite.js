import request from './request'

export const favoriteApi = {
  getMyFavorites(data) {
    return request({
      url: '/favorite/my',
      method: 'post',
      data
    })
  },

  addFavorite(productId) {
    return request({
      url: '/favorite/add',
      method: 'post',
      params: { productId }
    })
  },

  deleteFavorite(productId) {
    return request({
      url: `/favorite/delete/${productId}`,
      method: 'delete'
    })
  },

  removeFavorite(productId) {
    return request({
      url: `/favorite/delete/${productId}`,
      method: 'delete'
    })
  },

  checkFavorite(productId) {
    return request({
      url: `/favorite/check/${productId}`,
      method: 'get'
    })
  }
}
