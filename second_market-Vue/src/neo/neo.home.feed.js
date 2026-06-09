import { ref, watch, onMounted } from 'vue'
import { productApi } from '@/api/product'
import { useSchoolStore } from '@/stores/school'

export function useNeoHomeFeed() {
  const schoolStore = useSchoolStore()

  const loading = ref(false)
  const aiRecommended = ref([])
  const hotProducts = ref([])
  const campusProducts = ref([])
  const cityProducts = ref([])

  const isOk = (res) => res && (res.code === '200' || res.success)

  const fetchList = async (params) => {
    const res = await productApi.getProductList(params)
    if (!isOk(res)) return []
    const records = res.data?.records || res.data || []
    return Array.isArray(records) ? records : []
  }

  const buildAiRecommended = (candidates) => {
    const scored = (candidates || []).map((p) => {
      const view = Number(p.viewCount || 0)
      const fav = Number(p.favoriteCount || 0)
      const ai = p.aiAnalyzed === 1 || p.aiAnalyzed === true ? 1 : 0
      const score = view * 1.2 + fav * 2.5 + ai * 40
      return { p, score }
    })
    return scored
      .sort((a, b) => b.score - a.score)
      .slice(0, 10)
      .map((x) => x.p)
  }

  const refresh = async () => {
    loading.value = true
    try {
      const base = { status: 2 }
      const schoolId = schoolStore.selectedSchool ? Number(schoolStore.selectedSchool) : null

      const [hot, campus, city] = await Promise.all([
        fetchList({ ...base, pageNum: 1, pageSize: 10 }),
        fetchList({ ...base, pageNum: 1, pageSize: 12, sortBy: 'createTime', ...(schoolId ? { schoolId } : {}) }),
        fetchList({ ...base, pageNum: 1, pageSize: 12, sortBy: 'createTime' })
      ])

      hotProducts.value = (hot || []).slice().sort((a, b) => Number(b.viewCount || 0) - Number(a.viewCount || 0))
      campusProducts.value = campus || []
      cityProducts.value = city || []

      const candidates = [...hotProducts.value, ...campusProducts.value, ...cityProducts.value]
      const uniq = new Map()
      candidates.forEach((p) => {
        if (p && p.id != null && !uniq.has(p.id)) uniq.set(p.id, p)
      })
      aiRecommended.value = buildAiRecommended(Array.from(uniq.values()))
    } finally {
      loading.value = false
    }
  }

  watch(
    () => schoolStore.selectedSchool,
    () => refresh()
  )

  onMounted(() => refresh())

  return {
    loading,
    aiRecommended,
    hotProducts,
    campusProducts,
    cityProducts,
    refresh
  }
}

