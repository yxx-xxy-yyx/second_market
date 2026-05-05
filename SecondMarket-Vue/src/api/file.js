import request from './request'

// 文件上传
export const uploadFileApi = (file, onProgress) => {
    const formData = new FormData()
    formData.append('file', file)

    return request({
        url: '/file/upload',
        method: 'POST',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: onProgress
    })
}

// 头像上传
export const uploadAvatarApi = (file, onProgress) => {
    const formData = new FormData()
    formData.append('file', file)

    return request({
        url: '/file/upload/avatar',
        method: 'POST',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: onProgress
    })
}

// 获取文件信息
export const getFileInfoApi = (fileId) => {
    return request({
        url: `/file/${fileId}`,
        method: 'GET'
    })
}

// 获取文件列表
export const getFileListApi = (params) => {
    return request({
        url: '/file/page',
        method: 'GET',
        params
    })
}

// 删除文件
export const deleteFileApi = (fileId) => {
    return request({
        url: `/file/${fileId}`,
        method: 'DELETE'
    })
}

// 获取文件下载链接
export const getDownloadUrlApi = (fileId) => {
    return request({
        url: `/file/${fileId}/download-url`,
        method: 'GET'
    })
}

// 下载文件
export const downloadFileApi = (fileName) => {
    return request({
        url: `/file/download/${fileName}`,
        method: 'GET',
        responseType: 'blob'
    })
}

// 管理员获取文件列表
export const getAdminFileListApi = (params) => {
    return request({
        url: '/admin/files',
        method: 'GET',
        params
    })
}

// 管理员删除文件
export const deleteAdminFileApi = (fileId) => {
    return request({
        url: `/admin/files/${fileId}`,
        method: 'DELETE'
    })
}
