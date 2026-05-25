import request from './request'

export const browseApi = {
  // 记录浏览历史
  add(productId) {
    return request({
      url: `/browse/add/${productId}`,
      method: 'post'
    })
  },

  // 获取浏览记录列表
  getList(params) {
    return request({
      url: '/browse/list',
      method: 'get',
      params
    })
  },

  // 删除单条浏览记录
  delete(id) {
    return request({
      url: `/browse/delete/${id}`,
      method: 'delete'
    })
  },

  // 清空浏览记录
  clear() {
    return request({
      url: '/browse/clear',
      method: 'delete'
    })
  }
}
