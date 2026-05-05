import request from './request'

export const productApi = {
  getProductList(data) {
    return request({
      url: '/product/list',
      method: 'post',
      data
    })
  },

  getProductById(id) {
    return request({
      url: `/product/${id}`,
      method: 'get'
    })
  },

  viewProduct(id) {
    return request({
      url: `/product/view/${id}`,
      method: 'get'
    })
  },

  addProduct(data) {
    return request({
      url: '/product/add',
      method: 'post',
      data
    })
  },

  updateProduct(data) {
    return request({
      url: '/product/update',
      method: 'put',
      data
    })
  },

  deleteProduct(id) {
    return request({
      url: `/product/delete/${id}`,
      method: 'delete'
    })
  },

  updateProductStatus(id, status) {
    return request({
      url: `/product/status/${id}`,
      method: 'put',
      params: { status }
    })
  },

  getMyProducts(data) {
    return request({
      url: '/product/my',
      method: 'post',
      data
    })
  }
}

