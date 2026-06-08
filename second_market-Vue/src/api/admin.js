import request from './request'

// 用户管理
export const adminUserApi = {
  getUserList(data) {
    return request({
      url: '/admin/user/list',
      method: 'post',
      data
    })
  },

  banUser(id) {
    return request({
      url: `/admin/user/ban/${id}`,
      method: 'put'
    })
  },

  unbanUser(id) {
    return request({
      url: `/admin/user/unban/${id}`,
      method: 'put'
    })
  }
}

// 统计数据
export const adminStatisticsApi = {
  getOverview() {
    return request({
      url: '/admin/statistics/overview',
      method: 'get'
    })
  },

  getUserTrend(days = 7) {
    return request({
      url: '/admin/statistics/user/trend',
      method: 'get',
      params: { days }
    })
  },

  getCategoryStatistics() {
    return request({
      url: '/admin/statistics/product/category',
      method: 'get'
    })
  },

  getOrderStatusStatistics() {
    return request({
      url: '/admin/statistics/order/status',
      method: 'get'
    })
  },

  getTradeTrend(days = 30) {
    return request({
      url: '/admin/statistics/trade/trend',
      method: 'get',
      params: { days }
    })
  },

  getHotProducts(limit = 10) {
    return request({
      url: '/admin/statistics/product/hot',
      method: 'get',
      params: { limit }
    })
  },

  getActiveUsers(limit = 10) {
    return request({
      url: '/admin/statistics/user/active',
      method: 'get',
      params: { limit }
    })
  }
}

// 公告管理
export const noticeApi = {
  getList(pageNum = 1, pageSize = 10, status) {
    return request({
      url: '/notice/list',
      method: 'post',
      params: { pageNum, pageSize, status }
    })
  },

  getById(id) {
    return request({
      url: `/notice/${id}`,
      method: 'get'
    })
  },

  add(data) {
    return request({
      url: '/notice/add',
      method: 'post',
      data
    })
  },

  update(data) {
    return request({
      url: '/notice/update',
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/notice/delete/${id}`,
      method: 'delete'
    })
  }
}

// 举报管理
export const reportApi = {
  getList(pageNum = 1, pageSize = 10, status) {
    return request({
      url: '/report/list',
      method: 'post',
      params: { pageNum, pageSize, status }
    })
  },

  add(data) {
    return request({
      url: '/report/add',
      method: 'post',
      data
    })
  },

  handle(id, data) {
    return request({
      url: `/report/handle/${id}`,
      method: 'put',
      data
    })
  }
}

// 系统管理
export const adminSystemApi = {
  // 获取系统信息
  getSystemInfo() {
    return request({
      url: '/admin/system/info',
      method: 'get'
    })
  },

  // 获取管理后台Dashboard数据
  getDashboard() {
    return request({
      url: '/admin/dashboard',
      method: 'get'
    })
  },

  // 批量删除用户
  batchDeleteUsers(userIds) {
    return request({
      url: '/admin/users/batch',
      method: 'delete',
      data: userIds
    })
  },

  // 重置用户密码
  resetUserPassword(userId, newPassword) {
    return request({
      url: `/admin/users/${userId}/reset-password`,
      method: 'put',
      params: { newPassword }
    })
  }
}

// 管理员文件管理
export const adminFileApi = {
  // 获取文件列表
  getFileList(params) {
    return request({
      url: '/admin/files',
      method: 'get',
      params
    })
  },

  // 删除文件
  deleteFile(fileId) {
    return request({
      url: `/admin/files/${fileId}`,
      method: 'delete'
    })
  }
}

// 学校管理（后台）
export const adminSchoolApi = {
  // 新增学校
  addSchool(data) {
    return request({
      url: '/school/add',
      method: 'post',
      data
    })
  },

  // 更新学校
  updateSchool(data) {
    return request({
      url: '/school/update',
      method: 'put',
      data
    })
  },

  // 删除学校
  deleteSchool(id) {
    return request({
      url: `/school/delete/${id}`,
      method: 'delete'
    })
  },

  // 学校分页列表
  getSchoolPage(params) {
    return request({
      url: '/school/page',
      method: 'get',
      params
    })
  },

  // 获取学校详情
  getSchoolById(id) {
    return request({
      url: `/school/${id}`,
      method: 'get'
    })
  }
}

