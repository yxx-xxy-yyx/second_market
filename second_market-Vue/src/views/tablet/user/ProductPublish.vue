<template>
  <div class="px-4 py-5">
    <div class="mx-auto max-w-[980px]">
      <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-5">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-lg font-semibold text-gray-900">{{ $t('publish.title') }}</div>
            <div class="text-sm text-gray-500 mt-1">{{ $t('publish.uploadTip') }}</div>
          </div>
          <button class="text-sm sm-text-primary" @click="router.back()">{{ $t('common.cancel') }}</button>
        </div>
      </div>

      <div class="mt-4 grid grid-cols-2 gap-4">
        <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-5">
          <div class="flex items-center justify-between">
            <div class="text-sm font-semibold text-gray-900">{{ $t('publish.uploadTitle') }}</div>
            <button
              class="text-sm sm-text-primary"
              :disabled="imageUrls.length === 0 || analyzing"
              @click="handleAnalyze"
            >
              {{ analyzing ? $t('publish.analyzing') : $t('publish.aiBtn') }}
            </button>
          </div>

          <el-upload
            class="mt-3"
            ref="uploadRef"
            :action="uploadAction"
            :headers="uploadHeaders"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :before-upload="beforeUpload"
            v-model:file-list="fileList"
            :limit="9"
            accept="image/*"
            multiple
          >
            <div class="flex flex-col items-center justify-center">
              <el-icon :size="22" class="sm-text-primary"><Plus /></el-icon>
              <div class="text-xs text-gray-600 mt-1">{{ $t('publish.uploadBtn') }}</div>
            </div>
          </el-upload>

          <div class="mt-2 text-xs text-gray-500 flex items-center justify-between">
            <span>{{ $t('publish.uploadHintCover') }}</span>
            <span>{{ imageUrls.length }}/9</span>
          </div>
        </div>

        <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-5">
          <div class="flex items-center justify-between">
            <div class="text-sm font-semibold text-gray-900">{{ $t('publish.aiResult') }}</div>
            <button class="text-sm sm-text-primary" :disabled="!analysisResult" @click="handleAutoFill">
              {{ $t('publish.autoFill') }}
            </button>
          </div>

          <div v-if="analysisResult" class="mt-3">
            <div class="grid grid-cols-2 gap-3">
              <div class="rounded-2xl bg-gray-50 border border-gray-100 p-3">
                <div class="text-[11px] text-gray-500">{{ $t('publish.productTitle') }}</div>
                <div class="text-sm font-semibold text-gray-900 mt-1 line-clamp-2">{{ analysisResult.productName || '-' }}</div>
              </div>
              <div class="rounded-2xl bg-gray-50 border border-gray-100 p-3">
                <div class="text-[11px] text-gray-500">{{ $t('publish.conditionScore') }}</div>
                <div class="text-sm font-semibold sm-text-primary mt-1">{{ analysisResult.conditionScore || 0 }}{{ $t('publish.scoreUnit') }}</div>
              </div>
            </div>
            <div class="mt-3 text-sm text-gray-600 leading-relaxed line-clamp-4">
              {{ analysisResult.description || '' }}
            </div>
          </div>

          <div v-else class="mt-10 text-sm text-gray-400 text-center">
            {{ $t('publish.aiAnalyzingHint') }}
          </div>
        </div>
      </div>

      <div class="mt-4 rounded-2xl bg-white border border-gray-100 shadow-sm p-5">
        <div class="text-sm font-semibold text-gray-900 mb-4">{{ $t('publish.step3') }}</div>

        <el-form ref="formRef" :model="form" :rules="formRules" label-position="top">
          <div class="grid grid-cols-2 gap-4">
            <el-form-item :label="$t('publish.form.title')" prop="title" class="col-span-2">
              <el-input v-model="form.title" :placeholder="$t('publish.form.titlePlaceholder')" />
            </el-form-item>

            <el-form-item :label="$t('publish.form.category')" prop="categoryId">
              <el-select v-model="form.categoryId" :placeholder="$t('publish.form.categoryPlaceholder')" style="width: 100%">
                <el-option v-for="c in categories" :key="c.id" :label="$t(`categories.${c.id}`)" :value="c.id" />
              </el-select>
            </el-form-item>

            <el-form-item :label="$t('publish.form.school')" prop="schoolId">
              <el-select v-model="form.schoolId" :placeholder="$t('publish.form.schoolPlaceholder')" style="width: 100%">
                <el-option v-for="s in schoolStore.schoolList" :key="s.value" :label="s.label" :value="s.value" />
              </el-select>
            </el-form-item>

            <el-form-item :label="$t('publish.form.price')" prop="price">
              <el-input-number v-model="form.price" :min="0.01" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item :label="$t('publish.form.originalPrice')" prop="originalPrice">
              <el-input-number v-model="form.originalPrice" :min="0" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>

            <el-form-item :label="$t('publish.conditionScore')" prop="conditionScore" class="col-span-2">
              <el-slider v-model="form.conditionScore" :min="1" :max="10" :step="1" />
            </el-form-item>

            <el-form-item :label="$t('publish.form.desc')" prop="description" class="col-span-2">
              <el-input v-model="form.description" type="textarea" :rows="4" :placeholder="$t('publish.form.descPlaceholder')" />
            </el-form-item>
          </div>
        </el-form>

        <div class="mt-2">
          <el-button
            type="primary"
            class="w-full"
            :loading="publishing"
            @click="handlePublish"
            style="background: var(--primary-color); border-color: var(--primary-color);"
          >
            {{ $t('publish.publishNow') }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { Plus } from '@element-plus/icons-vue'
import { aiApi } from '@/api/ai'
import { productApi } from '@/api/product'
import { useSchoolStore } from '@/stores/school'
import { useUserStore } from '@/stores/user'
import { CATEGORIES, categoryIdToDbValue } from '@/utils/categories'

const router = useRouter()
const { t, locale } = useI18n()
const schoolStore = useSchoolStore()
const userStore = useUserStore()

const categories = CATEGORIES

const uploadRef = ref()
const formRef = ref()

const apiBaseUrl = computed(() => (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/+$/, ''))
const uploadAction = computed(() => `${apiBaseUrl.value}/file/upload`)
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return { Authorization: token ? `Bearer ${token}` : '' }
})

const fileList = ref([])
const imageUrls = ref([])
const analyzing = ref(false)
const analysisResult = ref(null)
const publishing = ref(false)

const form = reactive({
  title: '',
  categoryId: '',
  schoolId: '',
  price: null,
  originalPrice: null,
  conditionScore: 8,
  description: ''
})

const formRules = {
  title: [
    { required: true, message: t('publish.validate.titleRequired'), trigger: 'blur' },
    { min: 5, max: 100, message: t('publish.validate.titleLength'), trigger: 'blur' }
  ],
  categoryId: [{ required: true, message: t('publish.validate.categoryRequired'), trigger: 'change' }],
  schoolId: [{ required: true, message: t('publish.validate.schoolRequired'), trigger: 'change' }],
  price: [
    { required: true, message: t('publish.validate.priceRequired'), trigger: 'blur' },
    { type: 'number', min: 0.01, message: t('publish.validate.priceMin'), trigger: 'blur' }
  ],
  description: [
    { required: true, message: t('publish.validate.descRequired'), trigger: 'blur' },
    { min: 10, message: t('publish.validate.descMin'), trigger: 'blur' }
  ]
}

onMounted(() => {
  if (!schoolStore.schoolList.length) {
    schoolStore.getSchoolList()
  }
  const fallbackSchoolId = schoolStore.selectedSchool || userStore.user?.schoolId || ''
  if (fallbackSchoolId && !form.schoolId) {
    form.schoolId = String(fallbackSchoolId)
  }
})

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error(t('publish.upload.onlyImage'))
    return false
  }
  if (!isLt5M) {
    ElMessage.error(t('publish.upload.sizeLimit'))
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  if (response.code === '200' || response.success) {
    const fileUrl = response.data?.fileUrl || response.data?.filePath || response.data
    if (fileUrl) {
      imageUrls.value.push(fileUrl)
      ElMessage.success(t('publish.upload.success'))
      return
    }
  }
  ElMessage.error(response.message || t('publish.upload.fail'))
}

const handleRemove = (file) => {
  const index = fileList.value.findIndex((f) => f.uid === file.uid)
  if (index > -1) {
    imageUrls.value.splice(index, 1)
  }
}

const handleAnalyze = async () => {
  if (!imageUrls.value.length) {
    ElMessage.warning(t('publish.validate.uploadImageFirst'))
    return
  }
  analyzing.value = true
  try {
    const res = await aiApi.analyzeProductImage({
      imageUrls: imageUrls.value,
      additionalInfo: '',
      language: locale.value
    })
    if (res.code === '200') {
      analysisResult.value = res.data
      ElMessage.success(t('publish.ai.analyzeSuccess'))
    } else {
      ElMessage.error(res.message || t('publish.ai.analyzeFail'))
    }
  } finally {
    analyzing.value = false
  }
}

const handleAutoFill = () => {
  if (!analysisResult.value) return
  form.title = analysisResult.value.productName || form.title
  form.description = analysisResult.value.description || form.description
  form.conditionScore = analysisResult.value.conditionScore || form.conditionScore
  if (analysisResult.value.category) {
    const matched = categories.find((c) => categoryIdToDbValue(c.id) === analysisResult.value.category)
    if (matched) form.categoryId = matched.id
  }
  if (analysisResult.value.minPrice && analysisResult.value.maxPrice) {
    const avg = Math.floor((analysisResult.value.minPrice + analysisResult.value.maxPrice) / 2)
    form.price = form.price || avg
    form.originalPrice = form.originalPrice || analysisResult.value.maxPrice
  } else if (analysisResult.value.suggestedPrice) {
    form.price = form.price || analysisResult.value.suggestedPrice
  }
  ElMessage.success(t('publish.autoFillSuccess'))
}

const handlePublish = async () => {
  try {
    await formRef.value.validate()
  } catch {
    ElMessage.warning(t('publish.validate.completeInfo'))
    return
  }
  if (!imageUrls.value.length) {
    ElMessage.warning(t('publish.validate.uploadImageFirst'))
    return
  }
  try {
    await ElMessageBox.confirm(t('publish.confirmPublishMsg'), t('publish.confirmPublish'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'info'
    })
  } catch {
    return
  }

  publishing.value = true
  try {
    const payload = {
      title: form.title,
      category: categoryIdToDbValue(form.categoryId),
      schoolId: form.schoolId ? Number(form.schoolId) : null,
      price: form.price,
      originalPrice: form.originalPrice,
      conditionScore: form.conditionScore,
      description: form.description,
      images: JSON.stringify(imageUrls.value),
      aiAnalyzed: analysisResult.value ? 1 : 0,
      aiSuggestions: analysisResult.value ? JSON.stringify(analysisResult.value) : null,
      status: 1
    }
    const res = await productApi.addProduct(payload)
    if (res.code === '200') {
      ElMessage.success(t('publish.publishSuccess'))
      router.push('/user/dashboard')
    } else {
      ElMessage.error(res.message || t('publish.publishFail'))
    }
  } finally {
    publishing.value = false
  }
}
</script>

<style scoped>
:deep(.el-upload--picture-card) {
  border-radius: 16px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  border-radius: 16px;
}

:deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-weight: 600;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner),
:deep(.el-select__wrapper) {
  border-radius: 16px;
}
</style>
