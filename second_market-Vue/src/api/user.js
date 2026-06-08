import request from './request'

// 用户登录
export const loginApi = (data) => {
  return request({
    url: '/auth/login',
    method: 'POST',
    data
  })
}

// 用户注册
export const registerApi = (data) => {
  return request({
    url: '/auth/register',
    method: 'POST',
    data
  })
}

// 获取用户信息
export const getUserInfoApi = () => {
  return request({
    url: '/user/info',
    method: 'GET'
  })
}

// 获取当前登录用户的可信信息（从 token 解析）
export const getMeApi = () => {
  return request({
    url: '/user/me',
    method: 'GET'
  })
}

// 更新用户信息
export const updateUserInfoApi = (data) => {
  return request({
    url: '/user/profile',
    method: 'PUT',
    data
  })
}

// 更新用户头像
export const updateAvatarApi = (avatarUrl) => {
  return request({
    url: '/user/avatar',
    method: 'PUT',
    data: { avatarUrl }
  })
}

// 修改密码
export const changePasswordApi = (oldPassword, newPassword) => {
  return request({
    url: '/user/password',
    method: 'PUT',
    params: { oldPassword, newPassword }
  })
}

// 用户列表（管理员）
export const getUserListApi = (params) => {
  return request({
    url: '/admin/users',
    method: 'GET',
    params
  })
}

// 删除用户（管理员）
export const deleteUserApi = (userId) => {
  return request({
    url: `/admin/users/${userId}`,
    method: 'DELETE'
  })
}

// 创建用户（管理员）
export const createUserApi = (data) => {
  return request({
    url: '/admin/users',
    method: 'POST',
    data
  })
}

// 更新用户（管理员）
export const updateUserApi = (userId, data) => {
  return request({
    url: `/admin/users/${userId}`,
    method: 'PUT',
    data
  })
}

// 更新用户状态（管理员）
export const updateUserStatusApi = (userId, status) => {
  return request({
    url: `/admin/users/${userId}/status`,
    method: 'PUT',
    params: { status }
  })
}

// 重置用户密码（管理员）
export const resetUserPasswordApi = (userId, newPassword) => {
  return request({
    url: `/admin/users/${userId}/reset-password`,
    method: 'PUT',
    params: { newPassword }
  })
}

// 获取系统概览（管理员）
export const getDashboardApi = () => {
  return request({
    url: '/admin/dashboard',
    method: 'GET'
  })
}

export const userApi = {
  login: loginApi,
  register: registerApi,
  getCurrentUser: getUserInfoApi,
  getMe: getMeApi,
  updateUserInfo: updateUserInfoApi,
  updateAvatar: updateAvatarApi,
  changePassword: changePasswordApi,
  getUserList: getUserListApi,
  deleteUser: deleteUserApi,
  createUser: createUserApi,
  updateUser: updateUserApi,
  updateUserStatus: updateUserStatusApi,
  resetUserPassword: resetUserPasswordApi,
  getDashboard: getDashboardApi
}