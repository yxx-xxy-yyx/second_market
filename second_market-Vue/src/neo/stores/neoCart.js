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

export const useNeoCartStore = defineStore('neoCart', () => {
  const { key: userKey } = useNeoUserKey()
  const storageKey = computed(() => `neo:cart:${userKey.value}`)

  const items = ref([])

  const hydrate = () => {
    items.value = readJson(storageKey.value, [])
  }

  watch(storageKey, () => hydrate(), { immediate: true })
  watch(
    items,
    (next) => {
      writeJson(storageKey.value, next || [])
    },
    { deep: true }
  )

  const selectedItems = computed(() => items.value.filter((x) => x.selected))
  const selectedCount = computed(() => selectedItems.value.reduce((acc, x) => acc + Number(x.qty || 0), 0))
  const totalPrice = computed(() =>
    selectedItems.value.reduce((acc, x) => acc + Number(x.qty || 0) * Number(x.price || 0), 0)
  )
  const allSelected = computed(() => items.value.length > 0 && items.value.every((x) => x.selected))

  const add = (product, qty = 1) => {
    if (!product || product.id == null) return
    const nextQty = Math.max(1, Number(qty || 1))
    const idx = items.value.findIndex((x) => x.productId === product.id)
    if (idx >= 0) {
      items.value[idx].qty = Math.min(99, Number(items.value[idx].qty || 1) + nextQty)
      items.value[idx].selected = true
      items.value[idx].snapshot = buildSnapshot(product)
      return
    }
    items.value.unshift({
      productId: product.id,
      qty: Math.min(99, nextQty),
      selected: true,
      price: Number(product.price || 0),
      snapshot: buildSnapshot(product)
    })
  }

  const buildSnapshot = (product) => {
    const snap = {
      id: product.id,
      title: product.title,
      titleKo: product.titleKo,
      price: Number(product.price || 0),
      images: product.images,
      status: product.status,
      userId: product.userId,
      userNickname: product.userNickname || product.username,
      userAvatar: product.userAvatar,
      schoolId: product.schoolId
    }
    return snap
  }

  const remove = (productId) => {
    items.value = items.value.filter((x) => x.productId !== productId)
  }

  const clear = () => {
    items.value = []
  }

  const clearSelected = () => {
    items.value = items.value.filter((x) => !x.selected)
  }

  const setSelected = (productId, selected) => {
    const it = items.value.find((x) => x.productId === productId)
    if (!it) return
    it.selected = !!selected
  }

  const setAllSelected = (selected) => {
    const val = !!selected
    items.value = items.value.map((x) => ({ ...x, selected: val }))
  }

  const setQty = (productId, qty) => {
    const it = items.value.find((x) => x.productId === productId)
    if (!it) return
    const next = Math.max(1, Math.min(99, Number(qty || 1)))
    it.qty = next
  }

  const mergeProductSnapshot = (productId, product) => {
    const it = items.value.find((x) => x.productId === productId)
    if (!it) return
    it.price = Number(product?.price || it.price || 0)
    it.snapshot = buildSnapshot({ ...(it.snapshot || {}), ...(product || {}) })
  }

  return {
    items,
    selectedItems,
    selectedCount,
    totalPrice,
    allSelected,
    add,
    remove,
    clear,
    clearSelected,
    setSelected,
    setAllSelected,
    setQty,
    mergeProductSnapshot
  }
})

