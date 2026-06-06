import request from './request'

export const aiApi = {
  analyzeProductImage(data) {
    return request({
      url: '/ai/analyze/image',
      method: 'post',
      data,
      timeout: 180000
    })
  },

  generateContent(data) {
    return request({
      url: '/ai/generate',
      method: 'post',
      data
    })
  },

  generateSummary(data) {
    return request({
      url: '/ai/generate/summary',
      method: 'post',
      data
    })
  },

  generateTags(data) {
    return request({
      url: '/ai/generate/tags',
      method: 'post',
      data
    })
  },

  getAiStatus() {
    return request({
      url: '/ai/status',
      method: 'get'
    })
  },

  testConnection() {
    return request({
      url: '/ai/test',
      method: 'post'
    })
  },

  // 智能托管
  intelligentTrust(data) {
    return request({
      url: '/ai/trust',
      method: 'post',
      data
    })
  },

  // 鉴定质检
  authenticateProduct(data) {
    return request({
      url: '/ai/authenticate',
      method: 'post',
      data,
      timeout: 180000
    })
  },

  // 市场行情
  getMarketTrend(data) {
    return request({
      url: '/ai/market',
      method: 'post',
      data
    })
  },

  // 智能搜索
  smartSearch(data) {
    return request({
      url: '/ai/search',
      method: 'post',
      data
    })
  },

  // 校园匹配
  campusMatch(data) {
    return request({
      url: '/ai/match',
      method: 'post',
      data
    })
  },

  // 纠纷仲裁
  resolveDispute(data) {
    return request({
      url: '/ai/dispute',
      method: 'post',
      data
    })
  },

  // 校园专属服务
  campusService(data) {
    return request({
      url: '/ai/campus-service',
      method: 'post',
      data
    })
  }
}


