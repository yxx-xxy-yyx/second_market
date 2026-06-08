import { defineStore } from 'pinia'
import { computed, ref, watch } from 'vue'
import { useNeoUserKey } from '@/neo/utils/neoUserKey'

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

export const useNeoAddressStore = defineStore('neoAddress', () => {
  const { key: userKey } = useNeoUserKey()
  const storageKey = computed(() => `neo:address:${userKey.value}`)

  const list = ref([])

  const hydrate = () => {
    list.value = readJson(storageKey.value, [])
  }

  watch(storageKey, () => hydrate(), { immediate: true })
  watch(
    list,
    (next) => {
      writeJson(storageKey.value, next || [])
    },
    { deep: true }
  )

  const defaultAddress = computed(() => list.value.find((x) => x.isDefault) || list.value[0] || null)

  const upsert = (data) => {
    const next = normalize(data)
    if (!next) return
    if (!next.id) next.id = uid()
    const idx = list.value.findIndex((x) => x.id === next.id)
    if (idx >= 0) list.value.splice(idx, 1, next)
    else list.value.unshift(next)
    if (next.isDefault) setDefault(next.id)
    if (!list.value.some((x) => x.isDefault)) setDefault(next.id)
  }

  const remove = (id) => {
    const target = list.value.find((x) => x.id === id)
    list.value = list.value.filter((x) => x.id !== id)
    if (target?.isDefault && list.value.length) setDefault(list.value[0].id)
  }

  const setDefault = (id) => {
    list.value = list.value.map((x) => ({ ...x, isDefault: x.id === id }))
  }

  const normalize = (data) => {
    if (!data) return null
    const next = { ...data }
    next.country = next.country || 'KR'
    next.name = (next.name || '').trim()
    next.phone = (next.phone || '').trim()
    next.detail = (next.detail || '').trim()
    next.zipcode = (next.zipcode || '').trim()
    next.province = (next.province || '').trim()
    next.city = (next.city || '').trim()
    next.district = (next.district || '').trim()
    next.updatedAt = Date.now()
    next.isDefault = !!next.isDefault
    return next
  }

  return {
    list,
    defaultAddress,
    upsert,
    remove,
    setDefault
  }
})

