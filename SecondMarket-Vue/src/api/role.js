import request from './request'

// 获取角色列表
export const getRoleListApi = (params) => {
  return request({
    url: '/admin/roles',
    method: 'GET',
    params
  })
}

// 新增角色
export const createRoleApi = (data) => {
  return request({
    url: '/admin/roles',
    method: 'POST',
    data
  })
}

// 更新角色
export const updateRoleApi = (roleId, data) => {
  return request({
    url: `/admin/roles/${roleId}`,
    method: 'PUT',
    data
  })
}

// 删除角色
export const deleteRoleApi = (roleId) => {
  return request({
    url: `/admin/roles/${roleId}`,
    method: 'DELETE'
  })
}

// 获取角色权限
export const getRolePermissionsApi = (roleId) => {
  return request({
    url: `/admin/roles/${roleId}/permissions`,
    method: 'GET'
  })
}

// 更新角色权限
export const updateRolePermissionsApi = (roleId, permissions) => {
  return request({
    url: `/admin/roles/${roleId}/permissions`,
    method: 'PUT',
    data: { permissions }
  })
}

// 获取权限树
export const getPermissionTreeApi = () => {
  return request({
    url: '/admin/permissions/tree',
    method: 'GET'
  })
}
