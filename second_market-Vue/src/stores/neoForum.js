import { defineStore } from 'pinia'
import { computed, ref, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { useNeoSchoolKey } from '@/neo/utils/neoSchoolKey'

const readJson = (key, fallback) => {
  try {
    const raw = localStorage.getItem(key)
    if (!raw) return fallback
    return JSON.parse(raw)
  } catch {
    return fallback
  }
}

const writeJson = (key, value) => {
  localStorage.setItem(key, JSON.stringify(value))
}

const uid = () => `${Date.now()}${Math.floor(Math.random() * 1000)}`.padStart(16, '0')

const toUserSnapshot = (user) => {
  if (!user) return { id: 'unknown', nickname: '-', avatar: '' }
  return {
    id: user.id || user.userId || user.uid || user.username || 'unknown',
    nickname: user.nickname || user.username || user.uid || '-',
    avatar: user.avatar || ''
  }
}

export const useNeoForumStore = defineStore('neoForum', () => {
  const userStore = useUserStore()
  const { key: schoolKey } = useNeoSchoolKey()

  const storageKey = computed(() => `neo:forum:${schoolKey.value}`)
  const topics = ref([])

  const hydrate = () => {
    const data = readJson(storageKey.value, [])
    topics.value = Array.isArray(data) ? data : []
  }

  watch(storageKey, () => hydrate(), { immediate: true })
  watch(
    topics,
    (next) => {
      writeJson(storageKey.value, next || [])
    },
    { deep: true }
  )

  const sorted = computed(() => {
    const arr = topics.value.slice()
    arr.sort((a, b) => Number(b.updatedAt || b.createdAt || 0) - Number(a.updatedAt || a.createdAt || 0))
    return arr
  })

  const byId = (id) => topics.value.find((x) => String(x.id) === String(id))

  const create = (payload) => {
    const now = Date.now()
    const author = toUserSnapshot(userStore.user)
    const topic = {
      id: uid(),
      schoolId: schoolKey.value,
      category: String(payload?.category || 'campus'),
      title: String(payload?.title || '').trim(),
      content: String(payload?.content || '').trim(),
      author,
      likes: [],
      comments: [],
      createdAt: now,
      updatedAt: now
    }
    topics.value.unshift(topic)
    return topic
  }

  const toggleLike = (id) => {
    const it = byId(id)
    if (!it) return false
    const me = toUserSnapshot(userStore.user)
    if (!String(me.id)) return false
    it.likes = Array.isArray(it.likes) ? it.likes : []
    const idx = it.likes.findIndex((x) => String(x) === String(me.id))
    if (idx >= 0) it.likes.splice(idx, 1)
    else it.likes.push(me.id)
    it.updatedAt = Date.now()
    return true
  }

  const addComment = (id, content) => {
    const it = byId(id)
    if (!it) return false
    const text = String(content || '').trim()
    if (!text) return false
    const me = toUserSnapshot(userStore.user)
    if (!String(me.id)) return false
    it.comments = Array.isArray(it.comments) ? it.comments : []
    it.comments.push({ id: uid(), user: me, content: text, createdAt: Date.now() })
    it.updatedAt = Date.now()
    return true
  }

  const seedIfEmpty = () => {
    if (topics.value.length) return
    const now = Date.now()
    topics.value = [
      {
        id: uid(),
        schoolId: schoolKey.value,
        category: 'campus',
        title: '新生求助：校园附近二手群推荐？',
        content: '刚来韩国读书，想问下大家常用的二手群/面交地点有哪些？',
        author: { id: 'demo', nickname: 'Demo', avatar: '' },
        likes: [],
        comments: [],
        createdAt: now - 1000 * 60 * 60 * 6,
        updatedAt: now - 1000 * 60 * 60 * 6
      },
      {
        id: uid(),
        schoolId: schoolKey.value,
        category: 'trade',
        title: '避坑帖：面交如何验货更稳？',
        content: '分享一下我常用的验货清单：外观、序列号、配件、现场测试…',
        author: { id: 'demo2', nickname: 'Mina', avatar: '' },
        likes: [],
        comments: [],
        createdAt: now - 1000 * 60 * 60 * 3,
        updatedAt: now - 1000 * 60 * 60 * 3
      }
    ]
  }

  return {
    topics,
    sorted,
    byId,
    create,
    toggleLike,
    addComment,
    seedIfEmpty
  }
})

