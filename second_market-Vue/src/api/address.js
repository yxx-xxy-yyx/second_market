import request from '@/utils/request'

export const addressApi = {
  // 获取地址列表
  getList() {
    return request.get('/address/list')
  },

  // 获取单个地址
  getById(id) {
    return request.get(`/address/${id}`)
  },

  // 添加地址
  add(data) {
    return request.post('/address', data)
  },

  // 更新地址
  update(id, data) {
    return request.put(`/address/${id}`, data)
  },

  // 删除地址
  delete(id) {
    return request.delete(`/address/${id}`)
  },

  // 设置默认地址
  setDefault(id) {
    return request.put(`/address/${id}/default`)
  }
}
