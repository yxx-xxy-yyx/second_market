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
  }
}


