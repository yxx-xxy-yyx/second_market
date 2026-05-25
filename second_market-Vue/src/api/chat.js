import request from './request'

export const chatApi = {
  // 获取最近聊天列表
  getChatList: () => {
    return request({
      url: '/chat/list',
      method: 'get',
      _silent: true
    })
  },

  // 获取与特定用户的聊天记录
  getChatHistory: (targetUserId) => {
    return request({
      url: `/chat/history/${targetUserId}`,
      method: 'get'
    })
  },

  // 发送消息
  sendMessage: (data) => {
    return request({
      url: '/chat/send',
      method: 'post',
      data
    })
  },

  // 标记消息已读
  markAsRead: (senderId) => {
    return request({
      url: `/chat/read/${senderId}`,
      method: 'post'
    })
  },

  // 获取未读聊天消息总数
  getUnreadCount: () => {
    return request({
      url: '/chat/unread/count',
      method: 'get',
      _silent: true
    })
  }
}
