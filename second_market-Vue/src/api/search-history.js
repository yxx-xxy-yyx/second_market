import request from './request'

export const searchHistoryApi = {
  getHistory(limit = 10) {
    return request({
      url: '/search-history/list',
      method: 'get',
      params: { limit },
      noLoading: true
    })
  },

  record(keyword) {
    return request({
      url: '/search-history/record',
      method: 'post',
      params: { keyword },
      noLoading: true
    })
  },

  clear() {
    return request({
      url: '/search-history/clear',
      method: 'delete',
      noLoading: true
    })
  }
}
