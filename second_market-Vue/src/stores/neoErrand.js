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

export const useNeoErrandStore = defineStore('neoErrand', () => {
  const userStore = useUserStore()
  const { key: schoolKey } = useNeoSchoolKey()

  const storageKey = computed(() => `neo:errand:${schoolKey.value}`)

  const list = ref([])

  const hydrate = () => {
    const data = readJson(storageKey.value, [])
    list.value = Array.isArray(data) ? data : []
  }

  watch(storageKey, () => hydrate(), { immediate: true })
  watch(
    list,
    (next) => {
      writeJson(storageKey.value, next || [])
    },
    { deep: true }
  )

  const sorted = computed(() => {
    const arr = list.value.slice()
    arr.sort((a, b) => Number(b.updatedAt || b.createdAt || 0) - Number(a.updatedAt || a.createdAt || 0))
    return arr
  })

  const byId = (id) => list.value.find((x) => String(x.id) === String(id))

  const create = (payload) => {
    const now = Date.now()
    const owner = toUserSnapshot(userStore.user)
    const item = {
      id: uid(),
      schoolId: schoolKey.value,
      title: String(payload?.title || '').trim(),
      desc: String(payload?.desc || '').trim(),
      location: String(payload?.location || '').trim(),
      deadline: payload?.deadline || '',
      fee: Number(payload?.fee || 0),
      tags: Array.isArray(payload?.tags) ? payload.tags.slice(0, 6) : [],
      status: 'open',
      owner,
      runner: null,
      comments: [],
      createdAt: now,
      updatedAt: now
    }
    list.value.unshift(item)
    return item
  }

  const accept = (id) => {
    const it = byId(id)
    if (!it) return false
    if (it.status !== 'open') return false
    const runner = toUserSnapshot(userStore.user)
    if (String(runner.id) === String(it.owner?.id)) return false
    it.status = 'accepted'
    it.runner = runner
    it.updatedAt = Date.now()
    return true
  }

  const cancel = (id) => {
    const it = byId(id)
    if (!it) return false
    const me = toUserSnapshot(userStore.user)
    if (String(me.id) !== String(it.owner?.id)) return false
    if (it.status !== 'open') return false
    it.status = 'cancelled'
    it.updatedAt = Date.now()
    return true
  }

  const finish = (id) => {
    const it = byId(id)
    if (!it) return false
    const me = toUserSnapshot(userStore.user)
    const isOwner = String(me.id) === String(it.owner?.id)
    const isRunner = String(me.id) === String(it.runner?.id)
    if (!isOwner && !isRunner) return false
    if (it.status !== 'accepted') return false
    it.status = 'done'
    it.updatedAt = Date.now()
    return true
  }

  const addComment = (id, content) => {
    const it = byId(id)
    if (!it) return false
    const text = String(content || '').trim()
    if (!text) return false
    const me = toUserSnapshot(userStore.user)
    it.comments = Array.isArray(it.comments) ? it.comments : []
    it.comments.push({
      id: uid(),
      user: me,
      content: text,
      createdAt: Date.now()
    })
    it.updatedAt = Date.now()
    return true
  }

  const seedIfEmpty = () => {
    if (list.value.length) return
    const now = Date.now()
    list.value = [
      {
        id: uid(),
        schoolId: schoolKey.value,
        title: '代取快递（校园驿站）',
        desc: '晚上 9 点前帮我从驿站取一下快递，东西不重。',
        location: '校门口驿站',
        deadline: '',
        fee: 8,
        tags: ['快递', '当天'],
        status: 'open',
        owner: { id: 'demo', nickname: 'Demo', avatar: '' },
        runner: null,
        comments: [],
        createdAt: now - 1000 * 60 * 60 * 5,
        updatedAt: now - 1000 * 60 * 60 * 5
      },
      {
        id: uid(),
        schoolId: schoolKey.value,
        title: '代买便利店（饮料+面包）',
        desc: '帮忙带一瓶水和一个面包，备注口味即可。',
        location: '宿舍楼下 CU',
        deadline: '',
        fee: 5,
        tags: ['代买'],
        status: 'open',
        owner: { id: 'demo2', nickname: 'Mina', avatar: '' },
        runner: null,
        comments: [],
        createdAt: now - 1000 * 60 * 60 * 2,
        updatedAt: now - 1000 * 60 * 60 * 2
      }
    ]
  }

  return {
    list,
    sorted,
    byId,
    create,
    accept,
    cancel,
    finish,
    addComment,
    seedIfEmpty
  }
})

