import { defineStore } from 'pinia'
import { computed, ref, watch } from 'vue'
import { useNeoUserKey } from '@/neo/utils/neoUserKey'

const readJson = (key, fallback) => {
  try {
    const raw = sessionStorage.getItem(key)
    if (!raw) return fallback
    return JSON.parse(raw)
  } catch {
    return fallback
  }
}

const writeJson = (key, value) => {
  sessionStorage.setItem(key, JSON.stringify(value))
}

export const useNeoCheckoutStore = defineStore('neoCheckout', () => {
  const { key: userKey } = useNeoUserKey()
  const storageKey = computed(() => `neo:checkout:${userKey.value}`)

  const draft = ref({
    items: [],
    addressId: '',
    remark: '',
    orderIds: [],
    paymentMethod: ''
  })

  const hydrate = () => {
    draft.value = readJson(storageKey.value, draft.value)
  }

  watch(storageKey, () => hydrate(), { immediate: true })
  watch(
    draft,
    (next) => {
      writeJson(storageKey.value, next)
    },
    { deep: true }
  )

  const amount = computed(() =>
    (draft.value.items || []).reduce((acc, x) => acc + Number(x.qty || 0) * Number(x.price || 0), 0)
  )

  const setItems = (items) => {
    draft.value.items = Array.isArray(items) ? items : []
  }

  const setAddressId = (id) => {
    draft.value.addressId = id || ''
  }

  const setRemark = (remark) => {
    draft.value.remark = remark || ''
  }

  const setOrderIds = (ids) => {
    draft.value.orderIds = Array.isArray(ids) ? ids : []
  }

  const setPaymentMethod = (method) => {
    draft.value.paymentMethod = method || ''
  }

  const reset = () => {
    draft.value = { items: [], addressId: '', remark: '', orderIds: [], paymentMethod: '' }
  }

  return {
    draft,
    amount,
    setItems,
    setAddressId,
    setRemark,
    setOrderIds,
    setPaymentMethod,
    reset
  }
})

