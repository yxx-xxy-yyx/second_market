import request from './request'

export const orderApi = {
  getOrderList(params) {
    const { type, ...otherParams } = params
    const url = type === 1 ? '/orders/my/seller' : '/orders/my/buyer'
    return request({
      url,
      method: 'post',
      data: otherParams
    })
  },

  getBuyerOrders(data) {
    return request({
      url: '/orders/my/buyer',
      method: 'post',
      data
    })
  },

  getSellerOrders(data) {
    return request({
      url: '/orders/my/seller',
      method: 'post',
      data
    })
  },

  getOrderById(id) {
    return request({
      url: `/orders/${id}`,
      method: 'get'
    })
  },

  createOrder(data) {
    return request({
      url: '/orders/create',
      method: 'post',
      data
    })
  },

  payOrder(orderId) {
    return request({
      url: `/orders/pay/${orderId}`,
      method: 'put'
    })
  },

  confirmOrder(orderId) {
    return request({
      url: `/orders/finish/${orderId}`,
      method: 'put'
    })
  },

  finishOrder(orderId) {
    return request({
      url: `/orders/finish/${orderId}`,
      method: 'put'
    })
  },

  cancelOrder(orderId) {
    return request({
      url: `/orders/cancel/${orderId}`,
      method: 'put'
    })
  },

  getOrderDetail(orderId) {
    return request({
      url: `/orders/${orderId}`,
      method: 'get'
    })
  }
}

