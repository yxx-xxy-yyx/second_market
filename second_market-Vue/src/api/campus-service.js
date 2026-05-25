import request from './request'

export const campusServiceApi = {
  // 获取服务列表
  getList(params) {
    return request({
      url: '/campus-service/list',
      method: 'get',
      params
    })
  },

  // 获取详情
  getById(id) {
    return request({
      url: `/campus-service/${id}`,
      method: 'get'
    })
  },

  // 发布服务
  publish(data) {
    return request({
      url: '/campus-service/publish',
      method: 'post',
      data
    })
  },

  // 接单/加入
  accept(id) {
    return request({
      url: `/campus-service/accept/${id}`,
      method: 'post'
    })
  },

  // 完成
  complete(id) {
    return request({
      url: `/campus-service/complete/${id}`,
      method: 'post'
    })
  },

  // 取消
  cancel(id) {
    return request({
      url: `/campus-service/cancel/${id}`,
      method: 'post'
    })
  }
}
