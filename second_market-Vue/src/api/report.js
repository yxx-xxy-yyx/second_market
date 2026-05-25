import request from './request'

export const reportApi = {
  add(data) {
    return request({
      url: '/report/add',
      method: 'post',
      data
    })
  }
}

