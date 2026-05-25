/**
 * URL处理工具
 * 用于统一处理图片资源路径
 */

/**
 * 获取静态资源基础URL
 * 关键：必须保持 /api 前缀，因为uploads也需要通过/api代理访问
 */
export const getStaticBaseUrl = () => {
    return import.meta.env.VITE_API_BASE_URL || '/api'
}

/**
 * 格式化图片URL
 * 支持两种格式：
 * 1. 相对路径：/uploads/2026/10/06/xxx.jpg（本地上传）
 * 2. 完整URL：https://example.com/xxx.png（网络链接）
 * 
 * @param {string} url - 原始URL
 * @returns {string} - 格式化后的URL
 */
export const formatImageUrl = (url) => {
    if (!url) return ''

    // 如果是完整URL（http或https开头），直接使用
    if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
    }

    // 如果是相对路径，拼接基础URL
    // 相对路径格式：/uploads/2026/10/06/xxx.jpg
    // 拼接后格式：http://localhost:8080/api/uploads/2026/10/06/xxx.jpg
    const baseUrl = getStaticBaseUrl()
    return `${baseUrl}${url}`
}

export const DEFAULT_PRODUCT_IMAGE =
    "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='400' height='400' viewBox='0 0 400 400'><defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'><stop offset='0' stop-color='%23f3f4f6'/><stop offset='1' stop-color='%23e5e7eb'/></linearGradient></defs><rect width='400' height='400' rx='28' fill='url(%23g)'/><path d='M120 160h160a20 20 0 0 1 20 20v120a20 20 0 0 1-20 20H120a20 20 0 0 1-20-20V180a20 20 0 0 1 20-20z' fill='%23ffffff' opacity='0.9'/><path d='M140 200h120' stroke='%239ca3af' stroke-width='10' stroke-linecap='round' opacity='0.8'/><path d='M140 235h88' stroke='%239ca3af' stroke-width='10' stroke-linecap='round' opacity='0.6'/><circle cx='280' cy='230' r='22' fill='%23c7d2fe' opacity='0.9'/><path d='M155 292l45-52 40 40 36-30 54 62H155z' fill='%23a7f3d0' opacity='0.75'/></svg>"

export const formatProductImageUrl = (url) => {
    const formatted = formatImageUrl(url)
    return formatted || DEFAULT_PRODUCT_IMAGE
}

/**
 * 格式化头像URL
 * 支持默认头像和路径处理
 * 
 * @param {string} avatarUrl - 头像URL
 * @returns {string} - 格式化后的头像URL
 */
export const formatAvatarUrl = (avatarUrl) => {
    const DEFAULT_AVATAR =
        "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='128' height='128' viewBox='0 0 128 128'><defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'><stop offset='0' stop-color='%23409eff'/><stop offset='1' stop-color='%2396c8ff'/></linearGradient></defs><rect width='128' height='128' rx='28' fill='url(%23g)'/><circle cx='64' cy='52' r='22' fill='white' opacity='0.95'/><path d='M24 112c6-22 24-34 40-34s34 12 40 34' fill='white' opacity='0.95'/></svg>"

    if (!avatarUrl) {
        return DEFAULT_AVATAR
    }

    // 如果是完整URL（http或https开头），直接使用
    if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) {
        return avatarUrl
    }

    // 如果是相对路径，拼接基础URL
    const baseUrl = getStaticBaseUrl()
    return `${baseUrl}${avatarUrl.startsWith('/') ? '' : '/'}${avatarUrl}`
}

