export function formatAvatarUrl(avatarUrl) {
  const DEFAULT_AVATAR =
    "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='128' height='128' viewBox='0 0 128 128'><defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'><stop offset='0' stop-color='%23409eff'/><stop offset='1' stop-color='%2396c8ff'/></linearGradient></defs><rect width='128' height='128' rx='28' fill='url(%23g)'/><circle cx='64' cy='52' r='22' fill='white' opacity='0.95'/><path d='M24 112c6-22 24-34 40-34s34 12 40 34' fill='white' opacity='0.95'/></svg>"
  if (!avatarUrl) {
    return DEFAULT_AVATAR
  }

  if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) {
    return avatarUrl
  }

  return `/api${avatarUrl.startsWith('/') ? '' : '/'}${avatarUrl}`
}

export function formatImageUrl(imageUrl) {
  if (!imageUrl) {
    return ''
  }

  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }

  return `/api${imageUrl.startsWith('/') ? '' : '/'}${imageUrl}`
}

export function formatPrice(price) {
  if (price === null || price === undefined) {
    return '0.00'
  }
  return Number(price).toFixed(2)
}

export function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

