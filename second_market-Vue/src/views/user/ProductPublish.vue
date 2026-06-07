<template>
  <div class="product-publish-page">
    <el-row :gutter="20">
      <!-- 左侧主内容区 70% -->
      <el-col :xs="24" :sm="24" :md="17" :lg="17">
        <div class="main-publish-area">
          <div class="page-title">
            <el-icon :size="28">
              <Box />
            </el-icon>
            {{ $t('publish.title') }}
          </div>

          <el-steps :active="currentStep" align-center finish-status="success" class="publish-steps">
            <el-step :title="$t('publish.step1')" />
            <el-step :title="$t('publish.step2')" />
            <el-step :title="$t('publish.step3')" />
          </el-steps>

          <div class="step-content">
            <!-- 步骤1：上传图片 -->
            <div v-show="currentStep === 0" class="step-card">
              <div class="step-title">{{ $t('publish.uploadTitle') }}</div>
              <div class="step-desc">{{ $t('publish.uploadTip') }}</div>

              <el-upload ref="uploadRef" :action="uploadAction" :headers="uploadHeaders" list-type="picture-card"
                :on-success="handleUploadSuccess" :on-remove="handleRemove" :before-upload="beforeUpload"
                :file-list="fileList" :limit="9" accept="image/*" multiple>
                <div class="upload-trigger">
                  <el-icon class="upload-icon">
                    <Plus />
                  </el-icon>
                  <div class="upload-trigger-text">{{ $t('publish.uploadBtn') }}</div>
                  <div class="upload-trigger-sub">{{ $t('publish.uploadMultiTip') }}</div>
                </div>
              </el-upload>

              <div class="upload-hints">
                <div class="upload-hint-item">{{ $t('publish.uploadHintCover') }}</div>
                <div class="upload-hint-item">{{ $t('publish.uploadHintLimit') }}</div>
              </div>

              <div class="button-group">
                <el-button size="large" :disabled="imageUrls.length === 0 || analyzing" :loading="analyzing"
                  @click="handleAnalyze" class="analyze-btn">
                  <el-icon class="btn-icon">
                    <MagicStick />
                  </el-icon>
                  {{ analyzing ? $t('publish.analyzing') : $t('publish.aiBtn') }}
                </el-button>
                <el-button size="large" v-if="imageUrls.length > 0" @click="currentStep = 2" type="primary" plain>
                  <el-icon class="btn-icon">
                    <ArrowRight />
                  </el-icon>
                  {{ $t('publish.skipAI') }}
                </el-button>
              </div>
            </div>

            <!-- 步骤2：AI分析 -->
            <div v-show="currentStep === 1" class="step-card">
              <div v-if="analyzing" class="analyzing-container">
                <el-icon class="analyzing-icon" :size="80">
                  <Loading />
                </el-icon>
                <div class="analyzing-text">{{ $t('publish.aiAnalyzing') }}</div>
                <div class="analyzing-hint">{{ $t('publish.aiAnalyzingHint') }}</div>
              </div>

              <div v-else-if="analysisResult" class="analysis-result">
                <div class="result-header">
                  <el-icon class="result-icon" color="#67c23a">
                    <CircleCheck />
                  </el-icon>
                  <span class="result-title">{{ $t('publish.aiResult') }}</span>
                </div>

                <!-- AI结果卡片网格布局 -->
                <el-row :gutter="15" class="result-cards-grid">
                  <!-- 主要信息卡片（左侧大卡片） -->
                  <el-col :xs="24" :sm="24" :md="14" :lg="14">
                    <el-card class="result-main-card" shadow="hover">
                      <template #header>
                        <div class="card-header">
                          <el-icon color="#409eff">
                            <Box />
                          </el-icon>
                          <span>{{ $t('publish.productInfo') }}</span>
                        </div>
                      </template>
                      <div class="main-card-content">
                        <div class="product-name-display">{{ analysisResult.productName }}</div>
                        <div class="brand-model" v-if="analysisResult.brandModel">{{ analysisResult.brandModel }}</div>
                        <el-divider />
                        <div class="description-preview">{{ analysisResult.description }}</div>
                      </div>
                    </el-card>
                  </el-col>

                  <!-- 右侧小卡片（3个堆叠） -->
                  <el-col :xs="24" :sm="24" :md="10" :lg="10">
                    <!-- 成色卡片 -->
                    <el-card class="result-mini-card condition-card" shadow="hover">
                      <div class="mini-card-icon"
                        style="background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);">
                        <el-icon :size="24">
                          <Stamp />
                        </el-icon>
                      </div>
                      <div class="mini-card-content">
                        <div class="mini-card-label">{{ $t('publish.conditionScore') }}</div>
                        <div class="mini-card-value">{{ analysisResult.conditionScore }}<span class="value-unit">{{
                          $t('publish.scoreUnit') }}</span></div>
                        <el-progress :percentage="analysisResult.conditionScore * 10" :stroke-width="6"
                          :show-text="false" :color="getConditionColor(analysisResult.conditionScore)" />
                        <div class="mini-card-desc">{{ analysisResult.conditionDescription ||
                          analysisResult.conditionExplanation }}</div>
                      </div>
                    </el-card>

                    <!-- 价格建议卡片 -->
                    <el-card class="result-mini-card price-card" shadow="hover">
                      <div class="mini-card-icon"
                        style="background: linear-gradient(135deg, #ff9800 0%, #ffc107 100%);">
                        <el-icon :size="24">
                          <Money />
                        </el-icon>
                      </div>
                      <div class="mini-card-content">
                        <div class="mini-card-label">{{ $t('publish.priceSuggest') }}</div>
                        <div class="mini-card-value price-value">¥{{ analysisResult.minPrice }} - ¥{{
                          analysisResult.maxPrice }}</div>
                        <div class="mini-card-desc">{{ $t('publish.marketAnalysis') }}</div>
                      </div>
                    </el-card>

                    <!-- AI建议卡片 -->
                    <el-card class="result-mini-card suggest-card" shadow="hover">
                      <div class="mini-card-icon"
                        style="background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);">
                        <el-icon :size="24">
                          <MagicStick />
                        </el-icon>
                      </div>
                      <div class="mini-card-content">
                        <div class="mini-card-label">{{ $t('publish.aiSuggest') }}</div>
                        <div class="mini-card-desc">{{ $t('publish.aiSuggestDesc') }}</div>
                      </div>
                    </el-card>
                  </el-col>
                </el-row>

                <div class="button-group">
                  <el-button size="large" @click="currentStep = 0">
                    <el-icon class="btn-icon">
                      <ArrowLeft />
                    </el-icon>
                    {{ $t('publish.reAnalyze') }}
                  </el-button>
                  <el-button size="large" @click="handleAutoFill" class="autofill-btn">
                    <el-icon class="btn-icon">
                      <Operation />
                    </el-icon>
                    {{ $t('publish.autoFill') }}
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 步骤3：完善信息（左右分栏：表单 + 实时预览） -->
            <div v-show="currentStep === 2" class="step-card">
              <div class="step-title">{{ $t('publish.completeInfo') }}</div>

              <el-row :gutter="20" class="form-preview-row">
                <!-- 左侧表单 60% -->
                <el-col :xs="24" :sm="24" :md="14" :lg="14">
                  <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px" class="product-form">
                    <el-form-item :label="$t('publish.form.title')" prop="title">
                      <el-input v-model="form.title" :placeholder="$t('publish.form.titlePlaceholder')" maxlength="100"
                        show-word-limit />
                    </el-form-item>

                    <el-form-item :label="$t('publish.form.titleKo')" prop="titleKo">
                      <el-input v-model="form.titleKo" :placeholder="$t('publish.form.titleKoPlaceholder')"
                        maxlength="100" show-word-limit />
                    </el-form-item>

                    <el-form-item :label="$t('publish.form.category')" prop="category">
                      <el-select v-model="form.category" :placeholder="$t('publish.form.categoryPlaceholder')"
                        style="width: 100%">
                        <el-option v-for="cat in categories" :key="cat.id"
                          :label="locale === 'zh' ? cat.name : $t('categories.' + cat.id)" :value="cat.name" />
                      </el-select>
                    </el-form-item>

                    <el-form-item :label="$t('publish.form.school')" prop="schoolId">
                      <el-select v-model="form.schoolId" :placeholder="$t('publish.form.schoolPlaceholder')"
                        style="width: 100%">
                        <el-option v-for="s in schoolStore.schoolList" :key="s.value" :label="s.label"
                          :value="s.value" />
                      </el-select>
                    </el-form-item>

                    <el-row :gutter="15">
                      <el-col :span="12">
                        <el-form-item :label="$t('publish.form.price')" prop="price">
                          <el-input v-model.number="form.price" :placeholder="$t('publish.form.pricePlaceholder')"
                            type="number" step="0.01">
                            <template #prepend>¥</template>
                          </el-input>
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item :label="$t('publish.form.originalPrice')" prop="originalPrice">
                          <el-input v-model.number="form.originalPrice"
                            :placeholder="$t('publish.form.originalPricePlaceholder')" type="number" step="0.01">
                            <template #prepend>¥</template>
                          </el-input>
                        </el-form-item>
                      </el-col>
                    </el-row>

                    <el-form-item :label="$t('publish.form.conditionScore')" prop="conditionScore">
                      <el-slider v-model="form.conditionScore" :min="1" :max="10"
                        :marks="{ 1: $t('publish.score1'), 5: $t('publish.score5'), 10: $t('publish.score10') }"
                        show-stops />
                    </el-form-item>

                    <el-form-item :label="$t('publish.form.desc')" prop="description">
                      <el-input v-model="form.description" type="textarea" :rows="4"
                        :placeholder="$t('publish.form.descPlaceholder')" maxlength="500" show-word-limit />
                    </el-form-item>

                    <el-form-item :label="$t('publish.form.descKo')" prop="descriptionKo">
                      <el-input v-model="form.descriptionKo" type="textarea" :rows="4"
                        :placeholder="$t('publish.form.descKoPlaceholder')" maxlength="500" show-word-limit />
                    </el-form-item>

                    <el-form-item :label="$t('publish.form.conditionDesc')" prop="conditionDesc">
                      <el-input v-model="form.conditionDesc" type="textarea" :rows="3"
                        :placeholder="$t('publish.form.conditionDescPlaceholder')" maxlength="200" show-word-limit />
                    </el-form-item>
                  </el-form>
                </el-col>

                <!-- 右侧实时预览 40% -->
                <el-col :xs="24" :sm="24" :md="10" :lg="10">
                  <div class="preview-panel">
                    <div class="preview-title-header">
                      <el-icon>
                        <View />
                      </el-icon>
                      <span>{{ $t('publish.preview') }}</span>
                    </div>
                    <el-card class="preview-card" shadow="hover">
                      <div class="preview-image-area">
                        <el-image v-if="imageUrls.length > 0" :src="imageUrls[0]" fit="cover"
                          class="preview-main-image" />
                        <div v-else class="preview-placeholder">
                          <el-icon :size="48">
                            <Picture />
                          </el-icon>
                          <span>{{ $t('publish.noImage') }}</span>
                        </div>
                      </div>
                      <div class="preview-content">
                        <div class="preview-product-title">{{ form.title || $t('publish.productTitle') }}</div>
                        <div class="preview-price-row">
                          <span class="preview-price">¥{{ form.price || 0 }}</span>
                          <span class="preview-original-price" v-if="form.originalPrice">¥{{ form.originalPrice
                          }}</span>
                        </div>
                        <div class="preview-meta-row">
                          <el-tag size="small" v-if="form.category">{{ form.category }}</el-tag>
                          <el-tag size="small" type="info" v-if="form.schoolId">
                            {{ schoolStore.getSchoolName(String(form.schoolId)) }}
                          </el-tag>
                          <span class="preview-condition">{{ form.conditionScore }}{{ $t('publish.scoreUnit') }}</span>
                        </div>
                        <div class="preview-description">{{ form.description || $t('publish.noDesc') }}</div>
                      </div>
                    </el-card>
                    <div class="preview-hint">
                      <el-icon>
                        <InfoFilled />
                      </el-icon>
                      <span>{{ $t('publish.previewHint') }}</span>
                    </div>
                  </div>
                </el-col>
              </el-row>

              <div class="button-group">
                <el-button size="large" @click="currentStep = 1">
                  <el-icon class="btn-icon">
                    <ArrowLeft />
                  </el-icon>
                  {{ $t('publish.prevStep') }}
                </el-button>
                <el-button size="large" :loading="publishing" @click="handlePublish" class="publish-btn">
                  <el-icon class="btn-icon">
                    <Check />
                  </el-icon>
                  {{ publishing ? $t('publish.publishing') : $t('publish.publishNow') }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 右侧辅助信息区 30% -->
      <el-col :xs="24" :sm="24" :md="7" :lg="7">
        <div class="sidebar-area">
          <!-- 发布指南卡片 -->
          <el-card class="guide-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon>
                  <InfoFilled />
                </el-icon>
                <span>{{ $t('publish.guideTitle') }}</span>
              </div>
            </template>
            <el-timeline>
              <el-timeline-item :timestamp="$t('publish.step1Text')" :hollow="currentStep !== 0"
                :type="currentStep > 0 ? 'success' : 'primary'">
                {{ $t('publish.guideStep1Title') }}
                <div class="timeline-desc">{{ $t('publish.guideStep1Desc') }}</div>
              </el-timeline-item>
              <el-timeline-item :timestamp="$t('publish.step2Text')" :hollow="currentStep !== 1"
                :type="currentStep > 1 ? 'success' : 'primary'">
                {{ $t('publish.guideStep2Title') }}
                <div class="timeline-desc">{{ $t('publish.guideStep2Desc') }}</div>
              </el-timeline-item>
              <el-timeline-item :timestamp="$t('publish.step3Text')" :hollow="currentStep !== 2"
                :type="currentStep > 2 ? 'success' : 'primary'">
                {{ $t('publish.guideStep3Title') }}
                <div class="timeline-desc">{{ $t('publish.guideStep3Desc') }}</div>
              </el-timeline-item>
            </el-timeline>
          </el-card>

          <!-- 发布统计卡片 -->
          <el-card class="stats-card" shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <el-icon>
                  <TrendCharts />
                </el-icon>
                <span>{{ $t('publish.myPublishTitle') }}</span>
              </div>
            </template>
            <div class="stat-item">
              <span class="stat-label">{{ $t('publish.totalCount') }}</span>
              <span class="stat-value">0</span>
            </div>
            <el-divider />
            <div class="stat-item">
              <span class="stat-label">{{ $t('publish.reviewing') }}</span>
              <span class="stat-value pending">0</span>
            </div>
            <el-divider />
            <div class="stat-item">
              <span class="stat-label">{{ $t('publish.published') }}</span>
              <span class="stat-value success">0</span>
            </div>
          </el-card>

          <!-- AI分析结果卡片 -->
          <el-card class="ai-result-card" shadow="hover" style="margin-top: 20px;" v-if="analysisResult">
            <template #header>
              <div class="card-header">
                <el-icon>
                  <MagicStick />
                </el-icon>
                <span>{{ $t('publish.aiResult') }}</span>
                <el-tag type="success" size="small">{{ $t('publish.smartSuggest') }}</el-tag>
              </div>
            </template>
            <div class="ai-result-item">
              <div class="result-label">{{ $t('publish.recognizedProduct') }}</div>
              <div class="result-value">{{ analysisResult.productName }}</div>
            </div>
            <el-divider />
            <div class="ai-result-item">
              <div class="result-label">{{ $t('publish.conditionScore') }}</div>
              <div class="result-value">
                <el-rate v-model="analysisResult.conditionScore" disabled show-score :max="10" />
              </div>
            </div>
            <el-divider />
            <div class="ai-result-item">
              <div class="result-label">{{ $t('publish.priceSuggest') }}</div>
              <div class="result-value price">
                ¥{{ analysisResult.minPrice }} - ¥{{ analysisResult.maxPrice }}
              </div>
            </div>
          </el-card>

          <!-- 温馨提示卡片 -->
          <el-card class="tips-card" shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <el-icon>
                  <Warning />
                </el-icon>
                <span>{{ $t('publish.tipsTitle') }}</span>
              </div>
            </template>
            <ul class="tips-list">
              <li>{{ $t('publish.tip1') }}</li>
              <li>{{ $t('publish.tip2') }}</li>
              <li>{{ $t('publish.tip3') }}</li>
              <li>{{ $t('publish.tip4') }}</li>
            </ul>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import {
  Plus,
  MagicStick,
  Loading,
  CircleCheck,
  Operation,
  Check,
  ArrowLeft,
  ArrowRight,
  Box,
  Stamp,
  Money,
  View,
  Picture,
  InfoFilled,
  TrendCharts,
  Warning
} from '@element-plus/icons-vue'
import { aiApi } from '@/api/ai'
import { productApi } from '@/api/product'
import { useSchoolStore } from '@/stores/school'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const { t, locale } = useI18n()
const schoolStore = useSchoolStore()
const userStore = useUserStore()

const categories = [
  { id: 'electronics', name: '电子产品' },
  { id: 'appliances', name: '家用电器' },
  { id: 'clothing', name: '服装配饰' },
  { id: 'books', name: '图书音像' },
  { id: 'sports', name: '运动户外' },
  { id: 'beauty', name: '美妆护肤' },
  { id: 'toys', name: '母婴玩具' },
  { id: 'others', name: '其他' }
]

const currentStep = ref(0)
const uploadRef = ref()
const formRef = ref()

const apiBaseUrl = computed(() => (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/+$/, ''))
const uploadAction = computed(() => `${apiBaseUrl.value}/file/upload`)
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return {
    'Authorization': token ? `Bearer ${token}` : ''
  }
})

const fileList = ref([])
const imageUrls = ref([])
const analyzing = ref(false)
const analysisResult = ref(null)
const publishing = ref(false)

const form = reactive({
  title: '',
  titleKo: '',
  category: '',
  schoolId: '',
  price: null,
  originalPrice: null,
  conditionScore: 8,
  description: '',
  descriptionKo: '',
  conditionDesc: '',
  images: []
})

const formRules = {
  title: [
    { required: true, message: t('publish.validate.titleRequired'), trigger: 'blur' },
    { min: 5, max: 100, message: t('publish.validate.titleLength'), trigger: 'blur' }
  ],
  category: [
    { required: true, message: t('publish.validate.categoryRequired'), trigger: 'change' }
  ],
  schoolId: [
    { required: true, message: t('publish.validate.schoolRequired'), trigger: 'change' }
  ],
  price: [
    { required: true, message: t('publish.validate.priceRequired'), trigger: 'blur' },
    { type: 'number', min: 0.01, message: t('publish.validate.priceMin'), trigger: 'blur' }
  ],
  description: [
    { required: true, message: t('publish.validate.descRequired'), trigger: 'blur' },
    { min: 10, message: t('publish.validate.descMin'), trigger: 'blur' }
  ]
}

const getConditionColor = (score) => {
  if (score >= 9) return '#67c23a'
  if (score >= 7) return '#409eff'
  if (score >= 5) return '#e6a23c'
  return '#f56c6c'
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

const handleUploadSuccess = (response, file, files) => {
  try {
    if (response.code === '200' || response.success) {
      const fileUrl = response.data?.fileUrl || response.data?.filePath || response.data
      if (fileUrl) {
        imageUrls.value.push(fileUrl)
        form.images = imageUrls.value

        const targetFile = fileList.value.find(f => f.uid === file.uid)
        if (targetFile) {
          if (fileUrl.startsWith('http://') || fileUrl.startsWith('https://')) {
            targetFile.url = fileUrl
          } else {
            targetFile.url = `${apiBaseUrl.value}${fileUrl}`
          }
        }

        ElMessage.success(t('publish.upload.success'))
      } else {
        throw new Error('未获取到文件URL')
      }
    } else {
      throw new Error(response.message || '上传失败')
    }
  } catch (error) {
    console.error('上传处理失败:', error)
    ElMessage.error(error.message || t('publish.upload.fail'))
    const index = fileList.value.findIndex(f => f.uid === file.uid)
    if (index > -1) {
      fileList.value.splice(index, 1)
    }
  }
}

const handleRemove = (file, files) => {
  const index = fileList.value.findIndex(f => f.uid === file.uid)
  if (index > -1) {
    imageUrls.value.splice(index, 1)
    form.images = imageUrls.value
  }
}

const handleAnalyze = async () => {
  if (imageUrls.value.length === 0) {
    ElMessage.warning(t('publish.validate.uploadImageFirst'))
    return
  }

  analyzing.value = true
  currentStep.value = 1

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
      throw new Error(res.message || t('publish.ai.analyzeFail'))
    }
  } catch (error) {
    ElMessage.error(error.message || t('publish.ai.analyzeFail'))
    currentStep.value = 0
  } finally {
    analyzing.value = false
  }
}

const handleAutoFill = () => {
  if (!analysisResult.value) {
    ElMessage.warning(t('publish.validate.analyzeFirst'))
    return
  }

  currentStep.value = 2

  form.title = analysisResult.value.productName || form.title
  form.description = analysisResult.value.description || form.description
  form.conditionScore = analysisResult.value.conditionScore || form.conditionScore || 8
  form.conditionDesc = analysisResult.value.conditionDescription || analysisResult.value.conditionExplanation || form.conditionDesc

  if (analysisResult.value.category) {
    form.category = analysisResult.value.category
  }

  if (analysisResult.value.minPrice && analysisResult.value.maxPrice) {
    const avgPrice = Math.floor((analysisResult.value.minPrice + analysisResult.value.maxPrice) / 2)
    form.price = form.price || avgPrice
    form.originalPrice = form.originalPrice || analysisResult.value.maxPrice
  } else if (analysisResult.value.suggestedPrice) {
    form.price = form.price || analysisResult.value.suggestedPrice
  }

  ElMessage.success(t('publish.autoFillSuccess'))
}

const handlePublish = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    ElMessage.warning(t('publish.validate.completeInfo'))
    return
  }

  if (imageUrls.value.length === 0) {
    ElMessage.warning(t('publish.validate.uploadImageFirst'))
    currentStep.value = 0
    return
  }

  try {
    await ElMessageBox.confirm(
      t('publish.confirmPublishMsg'),
      t('publish.confirmPublish'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'info'
      }
    )
  } catch {
    return
  }

  publishing.value = true

  try {
    const publishData = {
      ...form,
      schoolId: form.schoolId ? Number(form.schoolId) : null,
      images: JSON.stringify(imageUrls.value),
      aiAnalyzed: analysisResult.value ? 1 : 0,
      aiSuggestions: analysisResult.value ? JSON.stringify(analysisResult.value) : null,
      status: 1
    }

    const res = await productApi.addProduct(publishData)

    if (res.code === '200') {
      ElMessage.success(t('publish.publishSuccess'))
      setTimeout(() => {
        router.push('/user/dashboard')
      }, 1500)
    } else {
      throw new Error(res.message || t('publish.publishFail'))
    }
  } catch (error) {
    ElMessage.error(error.message || t('publish.publishFail'))
  } finally {
    publishing.value = false
  }
}
</script>

<style scoped>
/* 你的原有样式完全不变，我没动 */
.product-publish-page {
  min-height: calc(100vh - 110px);
  background: #f0f2f5;
  padding: 20px;
}

.publish-container {
  max-width: 900px;
  margin: 0 auto;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  text-align: center;
  margin-bottom: 30px;
}

.publish-steps {
  margin-bottom: 24px;
  padding: 0 20px;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.step-content {
  min-height: 400px;
}

.step-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
}

.step-card:hover {
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.12);
  border-color: #c6e2ff;
}

.step-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.step-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 20px;
  line-height: 1.6;
}

:deep(.el-upload-list--picture-card) {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

:deep(.el-upload--picture-card) {
  width: 140px;
  height: 140px;
  border-radius: 10px;
  border: 2px dashed #dcdfe6;
  transition: all 0.3s ease;
  background: #fafafa;
}

:deep(.el-upload--picture-card:hover) {
  border-color: #409eff;
  background: #ecf5ff;
  transform: translateY(-2px);
}

:deep(.el-upload-list__item) {
  width: 140px;
  height: 140px;
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
}

:deep(.el-upload-list__item:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  border-color: #409eff;
}

.upload-icon {
  font-size: 32px;
  color: #8c939d;
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  text-align: center;
}

.upload-trigger-text {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.upload-trigger-sub {
  font-size: 11px;
  color: #909399;
  line-height: 1.2;
}

.upload-hints {
  margin-top: 12px;
  display: grid;
  gap: 6px;
  color: #909399;
  font-size: 12px;
}

.upload-hint-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.analyzing-container {
  text-align: center;
  padding: 80px 0;
}

.analyzing-icon {
  color: #409eff;
  animation: rotating 2s linear infinite;
  margin-bottom: 24px;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.analyzing-text {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 12px;
  font-weight: 500;
}

.analyzing-hint {
  font-size: 14px;
  color: #909399;
}

.analysis-result {
  padding: 20px 0;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f2f5;
}

.result-icon {
  font-size: 28px;
}

.result-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

.result-cards-grid {
  margin-bottom: 20px;
}

.result-main-card {
  height: 100%;
  transition: all 0.3s;
}

.result-main-card:hover {
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.main-card-content {
  padding: 10px 0;
}

.product-name-display {
  font-size: 22px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 8px;
}

.brand-model {
  font-size: 15px;
  color: #606266;
  margin-bottom: 12px;
}

.description-preview {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  max-height: 120px;
  overflow-y: auto;
}

.result-mini-card {
  margin-bottom: 12px;
  transition: all 0.3s;
  border: 1px solid #ebeef5;
}

.result-mini-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.12);
  border-color: #c6e2ff;
}

.result-mini-card:last-child {
  margin-bottom: 0;
}

.mini-card-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-bottom: 12px;
}

.mini-card-content {
  padding: 4px 0;
}

.mini-card-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.mini-card-value {
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.value-unit {
  font-size: 14px;
  color: #909399;
  font-weight: 400;
  margin-left: 2px;
}

.price-value {
  color: #ff9800;
  font-size: 18px;
}

.mini-card-desc {
  font-size: 12px;
  color: #606266;
  line-height: 1.5;
  margin-top: 8px;
}

.form-preview-row {
  margin-top: 16px;
}

.product-form {
  margin-top: 0;
}

.preview-panel {
  position: sticky;
  top: 80px;
}

.preview-title-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding: 10px 14px;
  background: linear-gradient(135deg, #e6f0ff 0%, #f0f7ff 100%);
  border-radius: 8px;
  border: 1px solid #c6e2ff;
}

.preview-card {
  margin-bottom: 12px;
}

.preview-image-area {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #ebeef5;
}

.preview-main-image {
  width: 100%;
  height: 100%;
}

.preview-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #c0c4cc;
}

.preview-content {
  padding: 14px 0 0 0;
}

.preview-product-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  min-height: 42px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

.preview-price-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.preview-price {
  font-size: 22px;
  color: #f56c6c;
  font-weight: 700;
}

.preview-original-price {
  font-size: 16px;
  color: #909399;
  text-decoration: line-through;
}

.preview-meta-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.preview-condition {
  font-size: 13px;
  color: #606266;
}

.preview-description {
  font-size: 12px;
  color: #606266;
  line-height: 1.6;
  max-height: 72px;
  overflow-y: auto;
}

.preview-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #909399;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.button-group .el-button {
  transition: all 0.3s ease;
}

.analyze-btn {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border: none;
  padding: 12px 32px;
  font-size: 15px;
  font-weight: 500;
}

.analyze-btn:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.35);
}

.autofill-btn {
  background: linear-gradient(135deg, #ff9800 0%, #ffb74d 100%);
  border: none;
  padding: 12px 32px;
  color: #fff;
  font-size: 15px;
  font-weight: 500;
}

.autofill-btn:hover {
  background: linear-gradient(135deg, #f57c00 0%, #ff9800 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.35);
}

.publish-btn {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  border: none;
  padding: 12px 32px;
  font-size: 15px;
  font-weight: 500;
}

.publish-btn:hover {
  background: linear-gradient(135deg, #5daf34 0%, #67c23a 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.35);
}

.btn-icon {
  margin-right: 6px;
}

@media (max-width: 768px) {
  .publish-container {
    max-width: 100%;
  }

  .step-card {
    padding: 24px;
  }

  .publish-steps {
    padding: 0 20px;
  }

  :deep(.el-upload--picture-card),
  :deep(.el-upload-list__item) {
    width: 120px;
    height: 120px;
  }

  .button-group {
    flex-direction: column;
  }

  .button-group .el-button {
    width: 100%;
  }

  .sidebar-area {
    position: relative;
    top: 0;
    margin-top: 20px;
  }
}

.sidebar-area {
  position: sticky;
  top: 20px;
}

.guide-card,
.stats-card,
.ai-result-card,
.tips-card {
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.guide-card:hover,
.stats-card:hover,
.ai-result-card:hover,
.tips-card:hover {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.12);
  border-color: #c6e2ff;
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.timeline-desc {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.5;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.stat-label {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.stat-value.pending {
  color: #ff9800;
}

.stat-value.success {
  color: #67c23a;
}

.ai-result-item {
  margin: 10px 0;
}

.result-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
  font-weight: 500;
}

.result-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  line-height: 1.6;
}

.result-value.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 600;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  position: relative;
  padding-left: 18px;
  margin-bottom: 10px;
  font-size: 12px;
  color: #606266;
  line-height: 1.6;
}

.tips-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #409eff;
  font-weight: bold;
  font-size: 14px;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

.main-publish-area .page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff !important;
  padding: 16px 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.25);
  margin-bottom: 24px;
}
</style>
