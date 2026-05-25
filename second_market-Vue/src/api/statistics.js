import request from './request'

export const userStatisticsApi = {
  getOverview() {
    return request({
      url: '/user/statistics/overview',
      method: 'get'
    })
  },

  getTradeIncome(days = 30) {
    return request({
      url: '/user/statistics/trade/income',
      method: 'get',
      params: { days }
    })
  },

  getTradeExpense(days = 30) {
    return request({
      url: '/user/statistics/trade/expense',
      method: 'get',
      params: { days }
    })
  },

  getProductViewTrend(limit = 10) {
    return request({
      url: '/user/statistics/product/view',
      method: 'get',
      params: { limit }
    })
  },

  getRating() {
    return request({
      url: '/user/statistics/rating',
      method: 'get'
    })
  }
}

