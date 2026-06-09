import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const STORAGE_KEY = 'sm_cart_v1'

const safeParse = (v) => {
  try {
    return JSON.parse(v)
  } catch {
    return null
  }
}

export const useCartStore = defineStore('cart', () => {
  const items = ref([])

  const load = () => {
    const raw = localStorage.getItem(STORAGE_KEY)
    const parsed = raw ? safeParse(raw) : null
    items.value = Array.isArray(parsed) ? parsed : []
  }

  const persist = () => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(items.value))
  }

  const count = computed(() => items.value.length)
  const selectedItems = computed(() => items.value.filter((i) => i.selected))
  const selectedCount = computed(() => selectedItems.value.length)
  const selectedTotal = computed(() =>
    selectedItems.value.reduce((sum, i) => sum + Number(i.price || 0), 0)
  )

  const has = (productId) => items.value.some((i) => String(i.productId) === String(productId))

  const addItem = (item) => {
    if (!item?.productId) return
    if (has(item.productId)) {
      items.value = items.value.map((i) =>
        String(i.productId) === String(item.productId) ? { ...i, selected: true } : i
      )
      persist()
      return
    }
    items.value.unshift({
      productId: item.productId,
      title: item.title || '',
      price: item.price || 0,
      image: item.image || '',
      sellerId: item.sellerId,
      schoolId: item.schoolId,
      conditionScore: item.conditionScore,
      conditionDesc: item.conditionDesc || '',
      selected: true,
      addedAt: Date.now()
    })
    persist()
  }

  const removeItem = (productId) => {
    items.value = items.value.filter((i) => String(i.productId) !== String(productId))
    persist()
  }

  const clear = () => {
    items.value = []
    persist()
  }

  const toggleSelect = (productId) => {
    items.value = items.value.map((i) =>
      String(i.productId) === String(productId) ? { ...i, selected: !i.selected } : i
    )
    persist()
  }

  const selectAll = (v) => {
    items.value = items.value.map((i) => ({ ...i, selected: !!v }))
    persist()
  }

  load()

  return {
    items,
    count,
    selectedItems,
    selectedCount,
    selectedTotal,
    addItem,
    removeItem,
    clear,
    toggleSelect,
    selectAll
  }
})

