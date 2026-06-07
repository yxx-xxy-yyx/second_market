<template>
  <div class="forum-publish min-h-screen bg-white">
    <!-- Header -->
    <div class="sticky top-0 z-50 text-white px-4 py-4 flex items-center justify-between" :style="{ background: 'var(--app-header-gradient)' }">
      <div class="flex items-center gap-3">
        <el-icon class="text-xl cursor-pointer" @click="router.back()"><ArrowLeft /></el-icon>
        <h1 class="text-lg font-bold">{{ $t('forum.publishTitle') }}</h1>
      </div>
      <button 
        class="text-sm font-bold bg-white/20 px-4 py-1.5 rounded-full active:scale-95 transition-transform"
        @click="handlePublish"
        :disabled="submitting"
      >
        {{ submitting ? $t('forum.publishing') : $t('forum.publish') }}
      </button>
    </div>

    <div class="p-4 space-y-6">
      <!-- Title Input -->
      <div class="border-b border-gray-50">
        <input 
          v-model="form.title" 
          type="text" 
          :placeholder="$t('forum.titlePlaceholder')" 
          class="w-full text-lg font-bold placeholder:text-gray-300 outline-none py-2"
        >
      </div>

      <!-- Content Input -->
      <div class="min-h-[200px]">
        <textarea 
          ref="contentEl"
          v-model="form.content" 
          :placeholder="$t('forum.contentPlaceholder')" 
          class="w-full h-full min-h-[200px] text-base placeholder:text-gray-300 outline-none resize-none leading-relaxed"
        ></textarea>
      </div>

      <div class="flex items-center justify-between gap-3">
        <div class="flex flex-wrap items-center gap-2 min-w-0">
          <span
            v-for="t in topics"
            :key="t"
            class="text-[11px] px-2 py-1 rounded-full bg-primary/10 text-primary font-bold truncate"
          >#{{ t }}</span>
        </div>
        <div class="flex items-center gap-2 flex-shrink-0">
          <button
            class="h-9 px-3 rounded-xl bg-primary/10 text-primary text-xs font-bold active:scale-95 transition-transform"
            type="button"
            @click="handleMention"
          >{{ $t('forum.mentionFriend') }}</button>
          <button
            class="h-9 px-3 rounded-xl bg-primary text-white text-xs font-bold active:scale-95 transition-transform"
            type="button"
            @click="handleTopic"
          >{{ $t('forum.topic') }}</button>
        </div>
      </div>

      <!-- Image Upload -->
      <div class="space-y-3">
        <div class="flex items-center justify-between">
          <span class="text-sm font-bold text-gray-900">{{ $t('forum.addImage') }}</span>
          <span class="text-xs text-gray-400">{{ fileList.length }}/9</span>
        </div>
        <el-upload
          v-model:file-list="fileList"
          :action="uploadAction"
          :headers="uploadHeaders"
          list-type="picture-card"
          :on-success="handleUploadSuccess"
          :on-remove="handleRemove"
          :before-upload="beforeUpload"
          :limit="9"
          accept="image/*"
          multiple
          class="forum-uploader"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
      </div>

      <!-- Category & School & Visibility -->
      <div class="space-y-4 pt-4">
        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-2xl border border-gray-100">
          <div class="flex items-center gap-2">
            <el-icon class="text-primary"><CollectionTag /></el-icon>
            <span class="text-sm font-medium text-gray-700">{{ $t('forum.selectCategory') }}</span>
          </div>
          <el-select v-model="form.category" :placeholder="$t('forum.choosePlaceholder')" size="small" class="category-select">
            <el-option :label="$t('forum.categories.secondHand')" value="二手专区" />
            <el-option :label="$t('forum.categories.campusChat')" value="校园闲聊" />
            <el-option :label="$t('forum.categories.errand')" value="跑腿互助" />
          </el-select>
        </div>

        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-2xl border border-gray-100">
          <div class="flex items-center gap-2">
            <el-icon class="text-primary"><School /></el-icon>
            <span class="text-sm font-medium text-gray-700">{{ $t('forum.publishToSchool') }}</span>
          </div>
          <el-select v-model="form.schoolId" :placeholder="$t('forum.chooseSchoolPlaceholder')" size="small" class="school-select">
            <el-option v-for="s in schoolStore.schoolList" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </div>

        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-2xl border border-gray-100">
          <div class="flex items-center gap-2">
            <el-icon class="text-primary"><Lock /></el-icon>
            <span class="text-sm font-medium text-gray-700">{{ $t('forum.schoolOnly') }}</span>
          </div>
          <el-switch v-model="form.isSchoolOnly" active-color="#36B3C2" />
        </div>

        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-2xl border border-gray-100">
          <div class="flex items-center gap-2">
            <el-icon class="text-primary"><User /></el-icon>
            <span class="text-sm font-medium text-gray-700">{{ $t('forum.anonymousPost') }}</span>
          </div>
          <el-switch v-model="form.isAnonymous" active-color="#36B3C2" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Plus, CollectionTag, School, Lock, User } from '@element-plus/icons-vue'
import { useSchoolStore } from '@/stores/school'
import { useUserStore } from '@/stores/user'
import { forumApi } from '@/api/forum'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatImageUrl } from '@/utils/url'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const route = useRoute()
const schoolStore = useSchoolStore()
const userStore = useUserStore()
const { t } = useI18n()

const postId = computed(() => route.query.id)
const isEdit = computed(() => !!postId.value)

const apiBaseUrl = computed(() => (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/+$/, ''))
const uploadAction = computed(() => `${apiBaseUrl.value}/file/upload`)
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return { Authorization: token ? `Bearer ${token}` : '' }
})

const fileList = ref([])
const imageUrls = ref([])
const submitting = ref(false)
const contentEl = ref(null)
const topics = ref([])

const form = reactive({
  title: '',
  content: '',
  category: '校园闲聊',
  schoolId: '',
  isSchoolOnly: false,
  isAnonymous: false
})

const parseTitleTopics = (rawTitle) => {
  const topics = []
  let title = String(rawTitle || '').trim()
  const re = /^#([^#]{1,30})#\s*/
  while (true) {
    const m = title.match(re)
    if (!m) break
    topics.push(m[1])
    title = title.replace(re, '').trimStart()
  }
  return { topics, title }
}

const buildTitleWithTopics = (title, list) => {
  const base = String(title || '').trim()
  const tps = Array.from(new Set((list || []).map((v) => String(v || '').trim()).filter(Boolean))).slice(0, 3)
  if (!tps.length) return base
  return `${tps.map((t) => `#${t}#`).join('')} ${base}`.trim()
}

const insertToContent = async (text) => {
  const el = contentEl.value
  if (!el) {
    form.content = `${form.content || ''}${text}`
    return
  }
  const start = typeof el.selectionStart === 'number' ? el.selectionStart : String(form.content || '').length
  const end = typeof el.selectionEnd === 'number' ? el.selectionEnd : start
  const current = String(form.content || '')
  form.content = current.slice(0, start) + text + current.slice(end)
  await nextTick()
  el.focus()
  el.setSelectionRange(start + text.length, start + text.length)
}

const handleMention = async () => {
  try {
    const { value } = await ElMessageBox.prompt(t('forum.mentionPromptTip'), t('forum.mentionPromptTitle'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      inputPlaceholder: t('forum.mentionPromptPlaceholder'),
      inputValidator: (v) => (String(v || '').trim().length >= 2 ? true : t('common.pleaseInputNickname'))
    })
    const name = String(value || '').trim()
    if (!name) return
    await insertToContent(`@${name} `)
  } catch {}
}

const handleTopic = async () => {
  try {
    const { value } = await ElMessageBox.prompt(t('forum.topicPromptTip'), t('forum.topicPromptTitle'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      inputPlaceholder: t('forum.topicPromptPlaceholder'),
      inputValidator: (v) => (String(v || '').trim().length >= 2 ? true : t('common.pleaseEnterKeyword'))
    })
    const t = String(value || '').trim()
    if (!t) return
    if (topics.value.includes(t)) return
    topics.value = [...topics.value, t].slice(0, 3)
  } catch {}
}

onMounted(async () => {
  if (!schoolStore.schoolList.length) {
    await schoolStore.getSchoolList()
  }
  
  if (isEdit.value) {
    fetchPostDetail()
  } else {
    form.schoolId = userStore.user?.schoolId || schoolStore.selectedSchool || ''
  }
})

const fetchPostDetail = async () => {
  try {
    const res = await forumApi.getDetail(postId.value)
    if (res.code == 200) {
      const data = res.data
      const parsed = parseTitleTopics(data.title)
      topics.value = parsed.topics
      form.title = parsed.title
      form.content = data.content
      form.category = data.category
      form.schoolId = data.schoolId
      form.isSchoolOnly = data.isSchoolOnly
      form.isAnonymous = data.isAnonymous || false
      
      if (data.images) {
        try {
          const images = JSON.parse(data.images)
          imageUrls.value = images
          fileList.value = images.map(url => ({
            name: url.split('/').pop(),
            url: formatImageUrl(url),
            response: { data: url }
          }))
        } catch (e) {
          console.error('解析图片失败', e)
        }
      }
    }
  } catch (e) {
    console.error(e)
    ElMessage.error(t('forum.fetchDetailFail'))
  }
}

const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp'
  const isLt2M = file.size / 1024 / 1024 < 5

  if (!isJPG) {
    ElMessage.error('上传图片只能是 JPG/PNG/WEBP 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 5MB!')
  }
  return isJPG && isLt2M
}

const handleUploadSuccess = (response) => {
  if (response.code == 200) {
    imageUrls.value.push(response.data)
  } else {
    ElMessage.error(t('forum.uploadFail'))
  }
}

const handleRemove = (file) => {
  const url = file.response ? file.response.data : file.url
  const index = imageUrls.value.indexOf(url)
  if (index > -1) {
    imageUrls.value.splice(index, 1)
  }
}

const handlePublish = async () => {
  if (!form.title.trim()) return ElMessage.warning(t('forum.needTitle'))
  if (!form.content.trim()) return ElMessage.warning(t('forum.needContent'))
  if (!form.schoolId) return ElMessage.warning(t('forum.needSchool'))

  submitting.value = true
  try {
    const data = {
      ...form,
      title: buildTitleWithTopics(form.title, topics.value),
      images: JSON.stringify(imageUrls.value)
    }
    
    let res
    if (isEdit.value) {
      data.id = postId.value
      res = await forumApi.update(data)
    } else {
      res = await forumApi.create(data)
    }

    if (res.code == 200 || res.success) {
      ElMessage.success(isEdit.value ? t('forum.updateSuccess') : t('forum.publishSuccess'))
      if (isEdit.value) {
        router.back()
      } else {
        router.replace({ name: 'user-forum', query: { refresh: Date.now() } })
      }
    } else if (res.code == 401) {
      ElMessage.warning(t('common.pleaseLogin'))
      router.push('/login')
    } else {
      ElMessage.error(res.message || t('forum.operateFail'))
    }
  } catch (e) {
    console.error(e)
    ElMessage.error(t('forum.operateFail'))
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.forum-uploader :deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  border-radius: 16px;
  background-color: #f9fafb;
  border: 1px dashed #e5e7eb;
}

.forum-uploader :deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
  border-radius: 16px;
}

.category-select :deep(.el-input__wrapper),
.school-select :deep(.el-input__wrapper) {
  background-color: transparent;
  box-shadow: none !important;
  padding-right: 0;
}

.category-select :deep(.el-input__inner),
.school-select :deep(.el-input__inner) {
  font-weight: 600;
  color: var(--primary-color);
  text-align: right;
}

.school-select :deep(.el-input__inner) {
  color: var(--primary-color);
}
</style>
