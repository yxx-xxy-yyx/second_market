import { defineStore } from 'pinia'
import { ref } from 'vue'

const STORAGE_KEY = 'sm_content_history_v1'

const safeParse = (v) => {
  try {
    return JSON.parse(v)
  } catch {
    return null
  }
}

const normalizeList = (list) => (Array.isArray(list) ? list : [])

export const useContentHistoryStore = defineStore('contentHistory', () => {
  const postHistory = ref([])
  const serviceHistory = ref([])

  const load = () => {
    const raw = localStorage.getItem(STORAGE_KEY)
    const parsed = raw ? safeParse(raw) : null
    postHistory.value = normalizeList(parsed?.postHistory)
    serviceHistory.value = normalizeList(parsed?.serviceHistory)
  }

  const persist = () => {
    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify({
        postHistory: postHistory.value,
        serviceHistory: serviceHistory.value
      })
    )
  }

  const recordPost = (post) => {
    if (!post?.id) return
    postHistory.value = [
      {
        id: post.id,
        title: post.title || '',
        images: post.images || '',
        userNickname: post.userNickname || '',
        userAvatar: post.userAvatar || '',
        commentCount: post.commentCount || 0,
        createTime: post.createTime || '',
        viewedAt: Date.now()
      },
      ...postHistory.value.filter((p) => String(p.id) !== String(post.id))
    ].slice(0, 200)
    persist()
  }

  const removePost = (postId) => {
    postHistory.value = postHistory.value.filter((p) => String(p.id) !== String(postId))
    persist()
  }

  const clearPost = () => {
    postHistory.value = []
    persist()
  }

  const recordService = (service) => {
    if (!service?.id) return
    serviceHistory.value = [
      {
        id: service.id,
        title: service.title || '',
        content: service.content || '',
        type: service.type,
        location: service.location || '',
        reward: service.reward || 0,
        status: service.status,
        userId: service.userId,
        accepterId: service.accepterId,
        createTime: service.createTime || '',
        viewedAt: Date.now()
      },
      ...serviceHistory.value.filter((s) => String(s.id) !== String(service.id))
    ].slice(0, 200)
    persist()
  }

  const removeService = (serviceId) => {
    serviceHistory.value = serviceHistory.value.filter((s) => String(s.id) !== String(serviceId))
    persist()
  }

  const clearService = () => {
    serviceHistory.value = []
    persist()
  }

  load()

  return {
    postHistory,
    serviceHistory,
    recordPost,
    removePost,
    clearPost,
    recordService,
    removeService,
    clearService
  }
})

