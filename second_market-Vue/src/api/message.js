import request from './request'

export const messageApi = {
  getMyMessages(params) {
    return request({
      url: '/message/my',
      method: 'post',
      params
    })
  },

  getMessageById(id) {
    return request({
      url: `/message/${id}`,
      method: 'get'
    })
  },

  markAsRead(id) {
    return request({
      url: `/message/read/${id}`,
      method: 'put'
    })
  },

  markAllAsRead() {
    return request({
      url: '/message/read-all',
      method: 'post'
    })
  },

  deleteMessage(id) {
    return request({
      url: `/message/delete/${id}`,
      method: 'delete'
    })
  },

  getUnreadCount() {
    return request({
      url: '/message/unread/count',
      method: 'get',
      _silent: true
    })
  },

  sendMessage(data) {
    return request({
      url: '/message/send',
      method: 'post',
      data
    })
  }
}
