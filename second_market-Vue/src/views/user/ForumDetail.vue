<template>
  <div class="forum-detail min-h-screen bg-[#f8f8fa] pb-20">
    <!-- Header -->
    <div class="sticky top-0 z-50 bg-primary text-white px-4 py-4 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <el-icon class="text-xl cursor-pointer" @click="router.back()"><ArrowLeft /></el-icon>
        <h1 class="text-lg font-bold">{{ $t('forum.detailTitle') }}</h1>
      </div>
      <div class="flex items-center gap-4">
        <el-dropdown v-if="isAuthor" trigger="click" @command="handleCommand">
          <el-icon class="text-xl cursor-pointer text-white"><MoreFilled /></el-icon>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="edit">{{ $t('common.edit') }}</el-dropdown-item>
              <el-dropdown-item command="delete" style="color: #f56c6c">{{ $t('common.delete') }}</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-icon class="text-xl cursor-pointer" :class="isLiked ? 'text-white' : 'text-white/80'" @click="toggleLike"><Pointer /></el-icon>
        <el-icon class="text-xl cursor-pointer" @click="toggleFavorite">
          <component :is="isFavorited ? StarFilled : Star" />
        </el-icon>
        <el-icon class="text-xl cursor-pointer" @click="handleReport"><Warning /></el-icon>
        <el-icon class="text-xl cursor-pointer"><Share /></el-icon>
      </div>
    </div>

    <!-- Skeleton Loading -->
    <div v-if="loading" class="p-4 space-y-4">
      <el-skeleton :rows="5" animated />
    </div>

    <template v-else-if="post && post.id">
      <!-- 作者信息栏 -->
      <div class="px-4 py-4 bg-white border-b border-gray-50">
        <div class="flex items-center gap-3">
          <el-avatar :size="48" :src="formatAvatarUrl(post.userAvatar)" class="border border-gray-100" />
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2">
              <h4 class="text-base font-bold text-gray-900 truncate">{{ post.userNickname }}</h4>
              <el-tag v-if="isAuthor" size="small" effect="plain" class="rounded-lg scale-90">{{ $t('forum.author') }}</el-tag>
              <el-tag v-if="post.isAnonymous" size="small" type="info" effect="plain" class="rounded-lg scale-90">{{ $t('forum.anonymous') }}</el-tag>
            </div>
            <p class="text-xs text-gray-400 mt-0.5">{{ formatTime(post.createTime) }} · {{ post.schoolName || $t('forum.alumni') }}</p>
          </div>
          <div class="flex items-center gap-3">
            <button v-if="!isAuthor && !post.isAnonymous" 
              class="px-4 py-1.5 bg-primary text-white text-xs font-bold rounded-full shadow-lg shadow-cyan-soft active:scale-95 transition-all"
              @click="handleContact">
              {{ $t('forum.chat') }}
            </button>
            <span class="px-2.5 py-1 rounded-full bg-primary/10 text-primary text-[10px] font-bold">
              {{ post.category }}
            </span>
          </div>
        </div>
      </div>

      <!-- 帖子内容 -->
      <div class="bg-white px-4 py-6 shadow-sm min-h-[200px]">
        <h2 class="text-xl font-bold text-gray-900 mb-4 leading-tight">{{ post.title }}</h2>
        <div class="text-gray-700 text-base leading-relaxed whitespace-pre-wrap mb-6">
          {{ post.content }}
        </div>

        <div v-if="post.imagesList && post.imagesList.length > 0" class="space-y-3 mb-6">
          <div v-for="(img, idx) in post.imagesList" :key="idx" class="rounded-2xl overflow-hidden bg-gray-50 border border-gray-100">
            <el-image 
              :src="formatImageUrl(img)" 
              fit="cover" 
              class="w-full" 
              :preview-src-list="post.imagesList.map(i => formatImageUrl(i))"
              :initial-index="idx"
            />
          </div>
        </div>

        <div class="flex items-center gap-6 py-4 border-t border-gray-50">
          <div class="flex items-center gap-2">
            <div class="p-2 rounded-full">
              <el-icon class="text-xl text-gray-400"><ChatDotRound /></el-icon>
            </div>
            <span class="text-sm font-medium text-gray-500">{{ post.commentCount }} {{ $t('forum.comment') }}</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="p-2 rounded-full">
              <el-icon class="text-xl text-gray-400"><View /></el-icon>
            </div>
            <span class="text-sm font-medium text-gray-500">{{ post.viewCount }} {{ $t('forum.view') }}</span>
          </div>
          <button class="flex items-center gap-2" @click="toggleLike">
            <div class="p-2 rounded-full">
              <el-icon class="text-xl" :class="isLiked ? 'text-primary' : 'text-gray-400'"><Pointer /></el-icon>
            </div>
            <span class="text-sm font-medium" :class="isLiked ? 'text-primary' : 'text-gray-500'">{{ post.likeCount || 0 }} {{ $t('forum.like') }}</span>
          </button>
          <button class="flex items-center gap-2" @click="toggleFavorite">
            <div class="p-2 rounded-full">
              <el-icon class="text-xl" :class="isFavorited ? 'text-primary' : 'text-gray-400'"><Star /></el-icon>
            </div>
            <span class="text-sm font-medium" :class="isFavorited ? 'text-primary' : 'text-gray-500'">{{ post.favoriteCount || 0 }} {{ $t('forum.favorite') }}</span>
          </button>
        </div>
      </div>

      <!-- Comments Section -->
      <div class="mt-2 bg-white px-4 py-6 mb-24">
        <h3 class="text-base font-bold text-gray-900 mb-6 flex items-center gap-2">
          {{ $t('forum.comments') }}
          <span class="text-sm font-normal text-gray-400">{{ post.commentCount }}</span>
        </h3>

        <div v-if="comments.length === 0" class="py-10 text-center">
          <el-empty :description="$t('forum.noComments')" :image-size="100" />
        </div>

        <div v-else class="space-y-8">
          <div v-for="comment in comments" :key="comment.id" class="flex gap-3">
            <el-avatar :size="36" :src="formatAvatarUrl(comment.userAvatar)" class="border border-gray-100" />
            <div class="flex-1 min-w-0">
              <div class="flex items-center justify-between mb-1">
                <div class="flex items-center gap-2">
                  <span class="text-sm font-bold text-gray-900 truncate">{{ comment.userNickname }}</span>
                  <el-tag v-if="comment.userId === post.userId" size="small" effect="plain" class="rounded-lg scale-75 origin-left">作者</el-tag>
                  <el-tag v-if="comment.isAnonymous" size="small" type="info" effect="plain" class="rounded-lg scale-75 origin-left">匿名</el-tag>
                </div>
                <div class="flex items-center gap-3">
                  <button v-if="userStore.user?.id && comment.userId !== userStore.user.id && !comment.isAnonymous"
                    class="text-[10px] text-primary font-bold"
                    @click="handleChat(comment)">
                    私聊
                  </button>
                  <span class="text-[10px] text-gray-400">{{ formatTime(comment.createTime) }}</span>
                  <el-icon 
                    v-if="userStore.user?.id && (comment.userId === userStore.user.id || post.userId === userStore.user.id)"
                    class="text-gray-300 active:text-red-400"
                    @click="deleteComment(comment.id)"
                  >
                    <Delete />
                  </el-icon>
                </div>
              </div>
              <p class="text-sm text-gray-600 mt-1 leading-relaxed">{{ comment.content }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Bottom Comment Bar -->
      <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-100 px-4 py-3 z-[100] safe-area-bottom shadow-[0_-4px_10px_rgba(0,0,0,0.08)]">
        <div v-if="userStore.user?.id" class="flex items-center gap-3">
          <div class="flex-1 flex items-center gap-2 bg-gray-50 rounded-full px-4 py-2 border border-gray-100">
            <el-icon class="text-gray-400"><EditPen /></el-icon>
            <input 
              v-model="newComment" 
              type="text" 
              :placeholder="$t('forum.commentPlaceholder')" 
              class="flex-1 bg-transparent border-none outline-none text-sm"
              @keyup.enter="submitComment"
            >
            <div class="flex items-center gap-1 px-2 border-l border-gray-200 cursor-pointer" @click="isAnonymous = !isAnonymous">
              <el-icon :class="isAnonymous ? 'text-primary' : 'text-gray-300'"><User /></el-icon>
              <span class="text-[10px]" :class="isAnonymous ? 'text-primary' : 'text-gray-400'">{{ $t('forum.anonymous') }}</span>
            </div>
          </div>
          <button 
            class="text-sm font-bold transition-colors"
            :class="newComment.trim() ? 'text-primary' : 'text-gray-300'"
            :disabled="!newComment.trim() || submitting"
            @click="submitComment"
          >
            {{ submitting ? $t('forum.publishing') : $t('forum.send') }}
          </button>
        </div>
        <div v-else class="flex items-center justify-center py-2">
          <button class="w-full bg-gray-900 text-white py-2.5 rounded-full text-sm font-bold active:scale-95 transition-all"
            @click="router.push('/login')">
            {{ $t('forum.loginToComment') }}
          </button>
        </div>
      </div>
    </template>

    <div v-else-if="!loading" class="flex flex-col items-center justify-center py-20">
      <el-empty :description="$t('forum.notFound')" />
      <el-button type="primary" round @click="router.back()">{{ $t('common.back') }}</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  ArrowLeft, 
  Share, 
  ChatDotRound, 
  View, 
  EditPen,
  MoreFilled,
  Delete,
  User,
  Star,
  StarFilled,
  Pointer,
  Warning
} from '@element-plus/icons-vue'
import { forumApi } from '@/api/forum'
import { reportApi } from '@/api/report'
import { formatAvatarUrl, formatImageUrl } from '@/utils/url'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useContentHistoryStore } from '@/stores/contentHistory'
import { useI18n } from 'vue-i18n'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'
import 'dayjs/locale/en'
import 'dayjs/locale/ko'

dayjs.extend(relativeTime)
const { t, locale } = useI18n()

const applyDayjsLocale = (v) => {
  if (v === 'en') dayjs.locale('en')
  else if (v === 'ko') dayjs.locale('ko')
  else dayjs.locale('zh-cn')
}

applyDayjsLocale(locale.value)
watch(locale, (v) => applyDayjsLocale(v))

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const contentHistory = useContentHistoryStore()
const postId = route.params.id

const loading = ref(true)
const post = ref({})
const comments = ref([])
const newComment = ref('')
const submitting = ref(false)
const isAnonymous = ref(false)

const isAuthor = computed(() => {
  return userStore.user?.id && post.value.userId === userStore.user.id
})

const isFavorited = computed(() => {
  if (!post.value?.id) return false
  return !!post.value.favorited
})

const isLiked = computed(() => {
  if (!post.value?.id) return false
  return !!post.value.liked
})

const toggleFavorite = async () => {
  if (!userStore.user?.id) {
    router.push('/login')
    return
  }
  if (!post.value?.id) return
  try {
    const res = await forumApi.toggleFavorite(post.value.id)
    if (res.code == 200 || res.code === '200' || res.success) {
      const d = res.data || {}
      post.value.favorited = d.favorited ? 1 : 0
      post.value.favoriteCount = d.favoriteCount
    }
  } catch {}
}

const toggleLike = async () => {
  if (!userStore.user?.id) {
    router.push('/login')
    return
  }
  if (!post.value?.id) return
  try {
    const res = await forumApi.toggleLike(post.value.id)
    if (res.code == 200 || res.code === '200' || res.success) {
      const d = res.data || {}
      post.value.liked = d.liked ? 1 : 0
      post.value.likeCount = d.likeCount
    }
  } catch {}
}

const handleReport = async () => {
  if (!userStore.user?.id) {
    router.push('/login')
    return
  }
  if (!post.value?.id) return
  try {
    const { value } = await ElMessageBox.prompt(t('forum.reportPlaceholder'), t('forum.reportTitle'), {
      confirmButtonText: t('common.submit'),
      cancelButtonText: t('common.cancel'),
      inputPlaceholder: t('forum.reportExample')
    })
    const reason = (value || '').trim()
    if (!reason) return
    const res = await reportApi.add({ targetType: 2, targetId: post.value.id, reason })
    if (res.code === '200') ElMessage.success(t('forum.reportSuccess'))
    else ElMessage.error(res.message || t('forum.reportFail'))
  } catch {}
}

const handleCommand = (command) => {
  if (command === 'edit') {
    router.push(`/user/forum/publish?id=${postId}`)
  } else if (command === 'delete') {
    handleDelete()
  }
}

const handleContact = () => {
  if (!userStore.user?.id) {
    ElMessage.warning(t('common.pleaseLogin'))
    router.push('/login')
    return
  }
  router.push({
    path: `/user/chat/${post.value.userId}`,
    query: {
      nickname: post.value.userNickname,
      avatar: post.value.userAvatar
    }
  })
}

const handleChat = (comment) => {
  if (!userStore.user?.id) {
    ElMessage.warning(t('common.pleaseLogin'))
    router.push('/login')
    return
  }
  router.push({
    path: `/user/chat/${comment.userId}`,
    query: {
      nickname: comment.userNickname,
      avatar: comment.userAvatar
    }
  })
}

const handleDelete = () => {
  ElMessageBox.confirm(t('forum.deletePostConfirm'), t('forum.deletePostTitle'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  }).then(async () => {
    try {
      const res = await forumApi.delete(postId)
      if (res.code == 200) {
        ElMessage.success(t('forum.deleteSuccess'))
        router.back()
      } else {
        ElMessage.error(res.message || t('forum.deleteFail'))
      }
    } catch (e) {
      console.error(e)
      ElMessage.error(t('forum.deleteFail'))
    }
  })
}

const formatTime = (time) => {
  if (!time) return ''
  return dayjs(time).fromNow()
}

const fetchDetail = async () => {
  try {
    const res = await forumApi.getDetail(postId)
    if (res.code == 200 || res.success) {
      const data = res.data
      if (data.images) {
        try {
          data.imagesList = JSON.parse(data.images)
        } catch (e) {
          data.imagesList = []
        }
      } else {
        data.imagesList = []
      }
      post.value = data
      contentHistory.recordPost(data)
    }
  } catch (e) {
    console.error(e)
    ElMessage.error(t('forum.fetchFail'))
  } finally {
    loading.value = false
  }
}

const fetchComments = async () => {
  try {
    const res = await forumApi.getComments(postId)
    if (res.code == 200 || res.success) {
      comments.value = res.data
    }
  } catch (e) {
    console.error(e)
  }
}

const deleteComment = async (id) => {
  try {
    const res = await forumApi.deleteComment(id)
    if (res.code == 200) {
      ElMessage.success(t('forum.deleteSuccess'))
      fetchComments()
      post.value.commentCount--
    }
  } catch (e) {
    console.error(e)
  }
}

const submitComment = async () => {
  if (!newComment.value.trim() || submitting.value) return
  
  submitting.value = true
  try {
    const res = await forumApi.addComment({
      postId: postId,
      content: newComment.value.trim(),
      isAnonymous: isAnonymous.value ? 1 : 0
    })
    if (res.code == 200 || res.success) {
      ElMessage.success(t('forum.commentSuccess'))
      newComment.value = ''
      post.value.commentCount++
      fetchComments()
    } else if (res.code == 401) {
      ElMessage.warning(t('common.pleaseLogin'))
      router.push('/login')
    }
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchDetail()
  fetchComments()
})
</script>

<style scoped>
.safe-area-bottom {
  padding-bottom: calc(0.75rem + env(safe-area-inset-bottom));
}

:deep(.el-image-viewer__wrapper) {
  z-index: 2000;
}
</style>
