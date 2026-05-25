import { defineStore } from 'pinia'
import { ref } from 'vue'

const STORAGE_KEY = 'sm_forum_fav_v1'

const safeParse = (v) => {
  try {
    return JSON.parse(v)
  } catch {
    return null
  }
}

export const useForumFavoriteStore = defineStore('forumFavorite', () => {
  const favorites = ref([])

  const load = () => {
    const raw = localStorage.getItem(STORAGE_KEY)
    const parsed = raw ? safeParse(raw) : null
    favorites.value = Array.isArray(parsed) ? parsed : []
  }

  const persist = () => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(favorites.value))
  }

  const has = (postId) => favorites.value.some((p) => String(p.id) === String(postId))

  const add = (post) => {
    if (!post?.id) return
    if (has(post.id)) return
    favorites.value.unshift({
      id: post.id,
      title: post.title || '',
      images: post.images || '',
      userNickname: post.userNickname || '',
      userAvatar: post.userAvatar || '',
      commentCount: post.commentCount || 0,
      createTime: post.createTime || '',
      savedAt: Date.now()
    })
    persist()
  }

  const remove = (postId) => {
    favorites.value = favorites.value.filter((p) => String(p.id) !== String(postId))
    persist()
  }

  const toggle = (post) => {
    if (!post?.id) return false
    if (has(post.id)) {
      remove(post.id)
      return false
    }
    add(post)
    return true
  }

  const clear = () => {
    favorites.value = []
    persist()
  }

  load()

  return {
    favorites,
    has,
    add,
    remove,
    toggle,
    clear
  }
})

