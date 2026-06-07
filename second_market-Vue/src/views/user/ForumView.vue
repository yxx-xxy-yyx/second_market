<template>
  <div class="forum-page min-h-screen bg-[#f8f8fa]">
    <!-- Header -->
    <div class="sticky top-0 z-50 bg-primary text-white px-4 py-3 flex items-center justify-between shadow-sm">
      <div class="flex items-center gap-3">
        <el-icon class="text-xl cursor-pointer active:scale-90 transition-transform" @click="router.back()"><ArrowLeft /></el-icon>
        <h1 class="text-lg font-bold">{{ $t('dashboard.campusForum') }}</h1>
      </div>
      <div class="flex items-center gap-2">
        <LangSwitcher glass />
        <div class="flex items-center gap-1 text-[10px] bg-white/20 px-3 py-1.5 rounded-full cursor-pointer active:scale-95 transition-transform"
          @click="openSchoolPicker">
          <el-icon><Location /></el-icon>
          <span>{{ currentSchool }}</span>
        </div>
      </div>
    </div>

    <!-- Category Tabs (Campus Style) -->
    <div class="px-4 py-3 bg-white sticky top-[52px] z-40 shadow-sm">
      <div class="flex bg-gray-100 p-1 rounded-2xl">
        <div 
          @click="forumScope = 'all'"
          class="flex-1 py-2 text-center text-sm font-bold rounded-xl transition-all cursor-pointer"
          :class="forumScope === 'all' ? 'bg-white text-primary shadow-sm' : 'text-gray-500'"
        >
          总论坛
        </div>
        <div 
          @click="forumScope = 'school'"
          class="flex-1 py-2 text-center text-sm font-bold rounded-xl transition-all cursor-pointer"
          :class="forumScope === 'school' ? 'bg-white text-primary shadow-sm' : 'text-gray-500'"
        >
          本校论坛
        </div>
      </div>
      <div class="mt-2 flex items-center justify-between">
        <div class="flex bg-gray-50 p-1 rounded-xl">
          <button class="px-3 h-8 rounded-lg text-xs font-bold transition" :class="sortBy === 'hot' ? 'bg-white text-primary shadow-sm' : 'text-gray-500'" @click="sortBy = 'hot'">热门</button>
          <button class="px-3 h-8 rounded-lg text-xs font-bold transition" :class="sortBy !== 'hot' ? 'bg-white text-primary shadow-sm' : 'text-gray-500'" @click="sortBy = ''">最新</button>
        </div>
        <div class="text-[11px] text-gray-400">{{ forumScope === 'school' ? '同校优先' : '全站精选' }}</div>
      </div>
    </div>

    <!-- Sub Categories (Campus Style) -->
    <div class="bg-white px-4 py-2 flex gap-2 overflow-x-auto no-scrollbar border-b border-gray-50">
      <div 
        v-for="cat in categories" 
        :key="cat"
        @click="activeCategory = cat"
        class="flex-shrink-0 px-4 py-1.5 rounded-xl text-xs font-bold transition-all active:scale-95"
        :class="activeCategory === cat ? 'bg-primary/10 text-primary' : 'text-gray-400'"
      >
        {{ cat }}
      </div>
    </div>

    <!-- Announcement -->
      <div v-if="activeCategory === '全部'" class="px-4 py-1">
      <div class="bg-primary/10 border border-primary/15 rounded-2xl px-4 py-3 flex items-center gap-3">
        <div class="bg-primary p-1.5 rounded-lg text-white">
          <el-icon><Microphone /></el-icon>
        </div>
        <p class="text-sm text-primary font-medium truncate">关于举办“校内二手市集”活动的通知</p>
      </div>
    </div>

    <!-- Pull to Refresh & List -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" head-height="80">
      <template #pulling>
        <div class="flex flex-col items-center py-4">
          <div class="w-8 h-8 border-2 border-primary/20 border-t-primary rounded-full animate-spin"></div>
          <span class="text-xs text-primary mt-2 font-bold">下拉刷新社区</span>
        </div>
      </template>
      <template #loosing>
        <div class="flex flex-col items-center py-4">
          <div class="w-8 h-8 border-2 border-primary rounded-full"></div>
          <span class="text-xs text-primary mt-2 font-bold">松手更新</span>
        </div>
      </template>
      
      <van-list
        v-model:loading="loading"
        :finished="finished"
        :finished-text="forumPosts.length > 0 ? '没有更多帖子了' : ''"
        :immediate-check="false"
        @load="onLoad"
        class="px-4 pb-24 pt-2"
      >
        <!-- Empty State -->
        <div v-if="!loading && forumPosts.length === 0" class="py-20 text-center">
          <el-empty description="还没有帖子，快来发第一篇吧" :image-size="120">
            <el-button type="primary" round class="publish-btn" @click="goPublish">去发布</el-button>
          </el-empty>
        </div>

    <!-- Waterfall Grid -->
    <div class="px-4 pb-24">
      <div class="grid grid-cols-2 gap-3">
        <!-- Left Column -->
        <div class="flex flex-col gap-3">
          <div 
            v-for="post in leftColumnPosts" 
            :key="post.id" 
            @click.stop="goDetail(post)"
            class="bg-white rounded-2xl overflow-hidden shadow-sm border border-gray-50 active:scale-[0.98] transition-all cursor-pointer"
          >
            <!-- Post Image -->
            <div v-if="post.imagesList && post.imagesList.length > 0" class="relative aspect-[3/4] overflow-hidden bg-gray-50">
              <el-image :src="formatImageUrl(post.imagesList[0])" fit="cover" class="w-full h-full" lazy />
              <div v-if="post.imagesList.length > 1" class="absolute top-2 right-2 bg-black/40 backdrop-blur-md rounded-lg px-1.5 py-0.5 flex items-center gap-1">
                <el-icon class="text-white text-[10px]"><Picture /></el-icon>
                <span class="text-white text-[10px] font-bold">{{ post.imagesList.length }}</span>
              </div>
            </div>

            <!-- Post Content -->
            <div class="p-3">
              <h3 class="text-sm font-bold text-gray-800 mb-1.5 line-clamp-2 leading-snug">{{ post.title }}</h3>
              <div class="flex items-center gap-2">
                <el-avatar :size="18" :src="formatAvatarUrl(post.userAvatar)" />
                <span class="text-[10px] text-gray-400 truncate flex-1">{{ post.userNickname }}</span>
                <el-tag v-if="post.isAnonymous" size="small" type="info" effect="plain" class="rounded-lg scale-75 origin-left">匿名</el-tag>
                <div class="flex items-center gap-0.5 text-gray-300">
                  <el-icon class="text-xs"><ChatDotRound /></el-icon>
                  <span class="text-[10px]">{{ post.commentCount }}</span>
                </div>
                <button class="flex items-center gap-0.5" :class="post.liked ? 'text-primary' : 'text-gray-300'" @click.stop="toggleLike(post)">
                  <el-icon class="text-xs"><Pointer /></el-icon>
                  <span class="text-[10px]">{{ post.likeCount || 0 }}</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Column -->
        <div class="flex flex-col gap-3">
          <div 
            v-for="post in rightColumnPosts" 
            :key="post.id" 
            @click.stop="goDetail(post)"
            class="bg-white rounded-2xl overflow-hidden shadow-sm border border-gray-50 active:scale-[0.98] transition-all cursor-pointer"
          >
            <!-- Post Image -->
            <div v-if="post.imagesList && post.imagesList.length > 0" class="relative aspect-[3/4] overflow-hidden bg-gray-50">
              <el-image :src="formatImageUrl(post.imagesList[0])" fit="cover" class="w-full h-full" lazy />
              <div v-if="post.imagesList.length > 1" class="absolute top-2 right-2 bg-black/40 backdrop-blur-md rounded-lg px-1.5 py-0.5 flex items-center gap-1">
                <el-icon class="text-white text-[10px]"><Picture /></el-icon>
                <span class="text-white text-[10px] font-bold">{{ post.imagesList.length }}</span>
              </div>
            </div>

            <!-- Post Content -->
            <div class="p-3">
              <h3 class="text-sm font-bold text-gray-800 mb-1.5 line-clamp-2 leading-snug">{{ post.title }}</h3>
              <div class="flex items-center gap-2">
                <el-avatar :size="18" :src="formatAvatarUrl(post.userAvatar)" />
                <span class="text-[10px] text-gray-400 truncate flex-1">{{ post.userNickname }}</span>
                <div class="flex items-center gap-0.5 text-gray-300">
                  <el-icon class="text-xs"><ChatDotRound /></el-icon>
                  <span class="text-[10px]">{{ post.commentCount }}</span>
                </div>
                <button class="flex items-center gap-0.5" :class="post.liked ? 'text-primary' : 'text-gray-300'" @click.stop="toggleLike(post)">
                  <el-icon class="text-xs"><Pointer /></el-icon>
                  <span class="text-[10px]">{{ post.likeCount || 0 }}</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
      </van-list>
    </van-pull-refresh>

    <!-- Floating Action Button -->
    <div 
      @click="goPublish"
      class="fixed bottom-24 right-6 w-14 h-14 bg-primary rounded-full shadow-lg shadow-cyan-soft flex items-center justify-center text-white text-2xl active:scale-90 active:rotate-12 transition-all z-50 cursor-pointer border-4 border-white"
    >
      <el-icon><Plus /></el-icon>
    </div>

    <!-- School Picker Drawer (Campus Style) -->
    <Teleport to="body">
      <Transition name="fade">
        <div v-if="showSchoolPicker" class="fixed inset-0 z-[100] flex items-end justify-center px-4 pb-10">
          <div class="fixed inset-0 bg-black/40 backdrop-blur-sm" @click="showSchoolPicker = false"></div>
          
          <Transition name="slide-up">
            <div class="bg-white w-full max-w-[500px] max-h-[70vh] flex flex-col z-10 overflow-hidden rounded-[32px] shadow-2xl border border-gray-100">
              <div class="flex items-center justify-between p-6 pb-2">
                <span class="text-gray-900 text-xl font-extrabold">选择学校</span>
                <div class="w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center cursor-pointer active:scale-90 transition-transform" @click="showSchoolPicker = false">
                  <el-icon class="text-gray-500"><Close /></el-icon>
                </div>
              </div>

              <!-- Search School -->
              <div class="px-6 py-4">
                <div class="relative">
                  <input 
                    v-model="schoolSearchKeyword" 
                    placeholder="搜索学校..."
                    class="w-full bg-gray-50 px-11 py-3.5 rounded-2xl border-none outline-none text-base h-14 focus:ring-2 focus:ring-primary/20 transition-all"
                  />
                  <el-icon class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 text-xl"><Search /></el-icon>
                </div>
              </div>
              
              <div class="flex-1 overflow-y-auto px-6 pb-8 space-y-3 custom-scrollbar">
                <div 
                  v-for="school in filteredSchools" 
                  :key="school.id"
                  class="px-5 py-4 rounded-2xl transition-all active:scale-[0.98] border cursor-pointer flex items-center justify-between"
                  :class="schoolStore.selectedSchool === String(school.id) ? 'bg-primary/10 border-primary/20 shadow-sm' : 'bg-gray-50 border-transparent hover:bg-gray-100'"
                  @click="selectSchool(school)"
                >
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 bg-white rounded-xl shadow-sm flex items-center justify-center overflow-hidden border border-gray-50">
                      <img v-if="school.logoUrl" :src="school.logoUrl" class="w-full h-full object-contain" />
                      <el-icon v-else class="text-gray-300 text-xl"><Location /></el-icon>
                    </div>
                    <span class="text-gray-800 text-base font-bold" :class="{ 'text-primary': schoolStore.selectedSchool === String(school.id) }">{{ school.label }}</span>
                  </div>
                  <div v-if="schoolStore.selectedSchool === String(school.id)" class="w-5 h-5 rounded-full bg-primary flex items-center justify-center">
                    <el-icon class="text-white text-xs"><Check /></el-icon>
                  </div>
                </div>
              </div>
            </div>
          </Transition>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ArrowLeft, 
  EditPen, 
  Microphone, 
  ChatDotRound, 
  Plus,
  ArrowRight,
  Picture,
  Location,
  Search,
  Close,
  Check,
  Pointer
} from '@element-plus/icons-vue'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { forumApi } from '@/api/forum'
import { schoolApi } from '@/api/school'
import { formatAvatarUrl, formatImageUrl } from '@/utils/url'
import { useSchoolStore } from '@/stores/school'
import { useUserStore } from '@/stores/user'
import { useContentHistoryStore } from '@/stores/contentHistory'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const router = useRouter()
const schoolStore = useSchoolStore()
const userStore = useUserStore()
const contentHistory = useContentHistoryStore()

const forumScope = ref('all') // 'all' or 'school'
const categories = ['全部', '二手专区', '校园闲聊', '跑腿互助']
const activeCategory = ref('全部')
const sortBy = ref('')

const forumPosts = ref([])
const leftColumnPosts = computed(() => forumPosts.value.filter((_, i) => i % 2 === 0))
const rightColumnPosts = computed(() => forumPosts.value.filter((_, i) => i % 2 !== 0))
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const currentPage = ref(1)
const pageSize = 10

// School Picker State
const showSchoolPicker = ref(false)
const schools = ref([])
const schoolSearchKeyword = ref('')

const currentSchool = computed(() => {
  if (schoolStore.selectedSchool) {
    return schoolStore.getSchoolName(schoolStore.selectedSchool)
  }
  return '选择学校'
})

const filteredSchools = computed(() => {
  if (!schoolSearchKeyword.value) return schools.value
  const kw = schoolSearchKeyword.value.toLowerCase()
  return schools.value.filter(s => 
    (s.label && s.label.toLowerCase().includes(kw)) ||
    (s.name && s.name.toLowerCase().includes(kw)) ||
    (s.nameZh && s.nameZh.toLowerCase().includes(kw)) ||
    (s.name_zh && s.name_zh.toLowerCase().includes(kw)) ||
    (s.nameKo && s.nameKo.toLowerCase().includes(kw)) ||
    (s.nameEn && s.nameEn.toLowerCase().includes(kw))
  )
})

const openSchoolPicker = async () => {
  showSchoolPicker.value = true
  if (schools.value.length === 0) {
    try {
      const res = await schoolApi.getList()
      if (res.code == 200 || res.success) {
        schools.value = (res.data || []).map(item => ({
          ...item,
          label: schoolStore.getLocalizedSchoolName(item) || 'Unknown School'
        }))
      }
    } catch (e) {
      console.error(e)
    }
  }
}

const selectSchool = (school) => {
  schoolStore.setSchool(String(school.id))
  showSchoolPicker.value = false
  onRefresh()
}

onMounted(() => {
  if (!schoolStore.selectedSchool && userStore.user?.schoolId) {
    schoolStore.setSchool(String(userStore.user.schoolId))
  }
  if (!schoolStore.schoolList.length) {
    schoolStore.getSchoolList()
  }
  onRefresh()
})

const formatTime = (time) => {
  return dayjs(time).fromNow()
}

const fetchPosts = async (isRefresh = false) => {
  if (loading.value && !isRefresh) return
  loading.value = true
  try {
    const page = isRefresh ? 1 : currentPage.value
    const params = {
      current: page,
      size: pageSize,
      category: activeCategory.value === '全部' ? null : activeCategory.value,
      sortBy: sortBy.value || null
    }

    // 如果是本校论坛，强制带上 schoolId
    if (forumScope.value === 'school') {
      params.schoolId = schoolStore.selectedSchool || null
    }

    const res = await forumApi.getList(params)
    if (res.code == 200 || res.success) {
      const { records = [], current = page, pages = 0 } = res.data || {}
      
      // 处理图片JSON
      const processedRecords = records.map(post => {
        try {
          const parsedImages = typeof post.images === 'string' ? JSON.parse(post.images) : post.images
          post.imagesList = Array.isArray(parsedImages) ? parsedImages : []
        } catch (e) {
          post.imagesList = []
        }
        return post
      })

      if (isRefresh) {
        forumPosts.value = processedRecords
        currentPage.value = 2
      } else {
        forumPosts.value.push(...processedRecords)
        currentPage.value++
      }

      if (current >= pages) {
        finished.value = true
      }
    } else {
      if (isRefresh) {
        forumPosts.value = []
      }
      finished.value = true
      ElMessage.error(res.message || '帖子加载失败')
    }
  } catch (e) {
    console.error(e)
    finished.value = true
    if (isRefresh) {
      forumPosts.value = []
    }
    ElMessage.error('帖子加载失败')
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const onLoad = () => {
  fetchPosts(false)
}

const onRefresh = () => {
  refreshing.value = true
  finished.value = false
  currentPage.value = 1
  fetchPosts(true)
}

const handleCategoryChange = (cat) => {
  activeCategory.value = cat
}

// 监听切换
watch([forumScope, activeCategory, () => schoolStore.selectedSchool], () => {
  onRefresh()
})
watch(sortBy, () => onRefresh())

const goDetail = (post) => {
  if (!post?.id) return
  contentHistory.recordPost(post)
  router.push({ name: 'user-forum-detail', params: { id: post.id } })
}

const goPublish = () => {
  router.push({ name: 'user-forum-publish' })
}

const toggleLike = async (post) => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  try {
    const res = await forumApi.toggleLike(post.id)
    if (res.code == 200 || res.code === '200' || res.success) {
      const d = res.data || {}
      post.liked = d.liked ? 1 : 0
      post.likeCount = d.likeCount
    }
  } catch (e) {}
}
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.publish-btn {
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.publish-btn:active {
  background: var(--primary-color-dark);
  border-color: var(--primary-color-dark);
}

:deep(.van-pull-refresh__track) {
  min-height: calc(100vh - 120px);
}
</style>
