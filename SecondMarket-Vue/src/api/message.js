import request from './request'

export const messageApi = {
  getMyMessages(data) {
    return request({
      url: '/message/my',
      method: 'post',
      data
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
      method: 'get'
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
