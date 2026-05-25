<template>
  <div class="search-page">
    <!-- 搜索栏 -->
    <div class="sticky top-0 z-40 bg-white border-b border-gray-100">
      <div class="h-[52px] px-3 flex items-center gap-2" :style="{ paddingTop: 'env(safe-area-inset-top)' }">
        <el-input
          v-model="searchKey"
          :placeholder="$t('common.search')"
          size="small"
          clearable
          @keyup.enter="doSearch"
          autofocus
          class="flex-1"
        >
          <template #prefix>
            <el-icon class="cursor-pointer" :size="18" @click="router.back()"><ArrowLeft /></el-icon>
          </template>
          <template #suffix>
            <el-icon class="cursor-pointer text-cyan-600" :size="18" @click="doSearch"><Search /></el-icon>
          </template>
        </el-input>
        <LangSwitcher />
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="px-3 py-4 space-y-6">
      <!-- 历史搜索 -->
      <div v-if="searchHistory.length > 0" class="space-y-3">
        <div class="flex items-center justify-between">
          <div class="text-sm font-semibold text-gray-900">{{ $t('common.searchHistory') }}</div>
          <button class="text-xs text-gray-400 hover:text-red-500" @click="clearSearchHistory">
            {{ $t('common.clearHistory') }}
          </button>
        </div>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="(item, index) in searchHistory"
            :key="index"
            class="px-3 py-2 text-xs bg-gray-100 text-gray-700 rounded-full hover:bg-gray-200 active:scale-95 transition"
            @click="searchKey = item; doSearch()"
          >
            {{ item }}
          </button>
        </div>
      </div>

      <!-- 猜你可能想找 -->
      <div v-if="recommendations.length > 0" class="space-y-3">
        <div class="text-sm font-semibold text-gray-900">{{ $t('common.guessYouLike') }}</div>
        <div class="grid grid-cols-2 gap-3">
          <button
            v-for="(item, index) in recommendations"
            :key="index"
            class="rounded-lg bg-white border border-gray-100 p-3 text-left hover:border-blue-300 hover:shadow-sm active:scale-95 transition"
            @click="searchKey = item.keyword; doSearch()"
          >
            <div class="text-sm font-medium text-gray-900 line-clamp-2">{{ item.keyword }}</div>
            <div class="mt-1 text-xs text-gray-500">{{ item.count }}{{ $t('common.peopleSearch') }}</div>
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="searchHistory.length === 0 && recommendations.length === 0" class="py-12 text-center">
        <el-empty :description="$t('common.noData')" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { productApi } from '@/api/product'
import { ElMessage } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import {
  Search,
  ArrowLeft
} from '@element-plus/icons-vue'

const router = useRouter()
const { t } = useI18n()

const searchKey = ref('')
const searchHistory = ref([])
const recommendations = ref([])

const SEARCH_HISTORY_KEY = 'search_history'
const MAX_HISTORY = 10

// 加载搜索历史
const loadSearchHistory = () => {
  const history = localStorage.getItem(SEARCH_HISTORY_KEY)
  if (history) {
    try {
      searchHistory.value = JSON.parse(history)
    } catch {
      searchHistory.value = []
    }
  }
}

// 保存搜索历史
const saveSearchHistory = (keyword) => {
  if (!keyword.trim()) return

  let history = [...searchHistory.value]
  history = history.filter(h => h !== keyword)
  history.unshift(keyword)
  history = history.slice(0, MAX_HISTORY)

  searchHistory.value = history
  localStorage.setItem(SEARCH_HISTORY_KEY, JSON.stringify(history))
}

// 清空搜索历史
const clearSearchHistory = () => {
  searchHistory.value = []
  localStorage.removeItem(SEARCH_HISTORY_KEY)
  ElMessage.success(t('common.historyCleared'))
}

// 执行搜索
const doSearch = () => {
  const keyword = searchKey.value?.trim()
  if (!keyword) {
    ElMessage.warning(t('common.pleaseEnterKeyword'))
    return
  }

  saveSearchHistory(keyword)
  router.push({
    path: '/user/products',
    query: { keyword }
  })
}

// 获取推荐搜索
const fetchRecommendations = async () => {
  try {
    const res = await productApi.getProductList({
      pageNum: 1,
      pageSize: 8,
      status: 2,
      sortBy: 'viewCount'
    })

    if (res.code === '200' && res.data.records) {
      const keywords = new Set()
      res.data.records.forEach(product => {
        const words = product.title.split(/[\s\-_\/\\。，、]+/).filter(w => w.length > 2)
        words.forEach(w => {
          if (keywords.size < 6) {
            keywords.add(w)
          }
        })
      })

      recommendations.value = Array.from(keywords).map((keyword, index) => ({
        keyword,
        count: Math.floor(Math.random() * 100) + 10
      }))
    }
  } catch (error) {
    console.error('获取推荐搜索失败:', error)
  }
}

onMounted(() => {
  loadSearchHistory()
  fetchRecommendations()
})
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background-color: #fff;
  padding-bottom: 60px;
}
</style>
