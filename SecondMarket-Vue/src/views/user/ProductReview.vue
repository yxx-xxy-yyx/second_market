<template>
  <div class="product-review-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <el-icon class="header-icon"><EditPen /></el-icon>
          <h2 class="page-title">{{ $t('productReview.pageTitle') }}</h2>
        </div>
        <div class="header-right">
          <el-steps :active="1" finish-status="success" simple>
            <el-step :title="$t('productReview.steps.selectProduct')" />
            <el-step :title="$t('productReview.steps.fillReview')" />
            <el-step :title="$t('productReview.steps.complete')" />
          </el-steps>
        </div>
      </div>
    </div>

    <div class="review-container" v-loading="loading">
      <template v-if="orderInfo">
        <!-- 左侧：商品信息卡片 -->
        <div class="left-section">
          <el-card class="product-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Box /></el-icon>
                <span>{{ $t('productReview.productInfo') }}</span>
                <el-tag type="success" size="small">{{ $t('productReview.completed') }}</el-tag>
              </div>
            </template>
            
            <div class="product-preview">
              <div class="product-image-wrapper">
                <el-image 
                  :src="getImageUrl(orderInfo.productImages || orderInfo.productImage)" 
                  fit="cover" 
                  class="product-image"
                />
                <div class="image-overlay">
                  <el-icon><View /></el-icon>
                </div>
              </div>
              
              <div class="product-details">
                <h4 class="product-title">{{ orderInfo.productTitle }}</h4>
                <div class="product-meta">
                  <div class="price-info">
                    <span class="price-label">{{ $t('productReview.transactionPrice') }}</span>
                    <span class="product-price">¥{{ orderInfo.productPrice || orderInfo.totalAmount }}</span>
                  </div>
                  <div class="order-info">
                    <span class="order-label">{{ $t('productReview.orderNumber') }}</span>
                    <span class="order-number">{{ orderInfo.orderNo || orderId }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 订单状态时间线 -->
            <div class="order-timeline">
              <el-timeline>
                <el-timeline-item
                  :timestamp="$t('productReview.orderStatus.ordered')"
                  placement="top"
                  type="success"
                  icon="SuccessFilled"
                />
                <el-timeline-item
                  :timestamp="$t('productReview.orderStatus.paid')"
                  placement="top"
                  type="success"
                  icon="SuccessFilled"
                />
                <el-timeline-item
                  :timestamp="$t('productReview.orderStatus.completed')"
                  placement="top"
                  type="success"
                  icon="SuccessFilled"
                />
              </el-timeline>
            </div>
          </el-card>

          <!-- 评价指南卡片 -->
          <el-card class="guide-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><QuestionFilled /></el-icon>
                <span>{{ $t('productReview.reviewGuide') }}</span>
              </div>
            </template>
            <div class="guide-content">
              <div class="guide-item">
                <el-icon class="guide-icon"><Star /></el-icon>
                <span>{{ $t('productReview.guide.item1') }}</span>
              </div>
              <div class="guide-item">
                <el-icon class="guide-icon"><Picture /></el-icon>
                <span>{{ $t('productReview.guide.item2') }}</span>
              </div>
              <div class="guide-item">
                <el-icon class="guide-icon"><ChatDotRound /></el-icon>
                <span>{{ $t('productReview.guide.item3') }}</span>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 右侧：评价表单 -->
        <div class="right-section">
          <el-card class="review-form-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Edit /></el-icon>
                <span>{{ $t('productReview.fillReview') }}</span>
                <div class="form-progress">
                  <span class="progress-text">{{ getFormProgress() }}% {{ $t('productReview.complete') }}</span>
                  <el-progress :percentage="getFormProgress()" :stroke-width="4" />
                </div>
              </div>
            </template>

            <el-form
              ref="reviewFormRef"
              :model="reviewForm"
              :rules="reviewRules"
              class="review-form"
              label-position="top"
            >
              <!-- 评分区域 -->
              <div class="rating-section">
                <el-form-item :label="$t('productReview.productRating')" prop="rating" class="rating-form-item">
                  <div class="rating-wrapper">
                    <el-rate
                      v-model="reviewForm.rating"
                      :texts="ratingTexts.map(text => $t(text))"
                      show-text
                      :colors="['#f56c6c', '#ff9800', '#409eff']"
                      size="large"
                      class="rating-stars"
                    />
                    <div class="rating-desc">{{ getRatingDescription(reviewForm.rating) }}</div>
                  </div>
                </el-form-item>
              </div>

              <!-- 评价内容 -->
              <el-form-item :label="$t('productReview.reviewContent')" prop="content" class="content-form-item">
                <el-input
                  v-model="reviewForm.content"
                  type="textarea"
                  :rows="5"
                  :placeholder="$t('productReview.contentPlaceholder')"
                  maxlength="500"
                  show-word-limit
                  class="review-textarea"
                />
                <div class="content-tips">
                  <el-tag size="small" type="info">{{ $t('productReview.contentTips') }}</el-tag>
                </div>
              </el-form-item>

              <!-- 图片上传 -->
              <el-form-item :label="$t('productReview.uploadImages')" class="upload-form-item">
                <div class="upload-section">
                  <el-upload
                    v-model:file-list="reviewForm.images"
                    :action="uploadAction"
                    list-type="picture-card"
                    :on-success="handleUploadSuccess"
                    :headers="uploadHeaders"
                    :limit="6"
                    accept="image/*"
                    class="review-upload"
                  >
                    <div class="upload-trigger">
                      <el-icon><Plus /></el-icon>
                      <div class="upload-text">{{ $t('productReview.uploadImage') }}</div>
                    </div>
                  </el-upload>
                  <div class="upload-tips">
                    <div class="tip-item">
                      <el-icon><InfoFilled /></el-icon>
                      <span>{{ $t('productReview.uploadTips.item1') }}</span>
                    </div>
                    <div class="tip-item">
                      <el-icon><WarnTriangleFilled /></el-icon>
                      <span>{{ $t('productReview.uploadTips.item2') }}</span>
                    </div>
                  </div>
                </div>
              </el-form-item>

              <!-- 提交按钮 -->
              <div class="submit-section">
                <el-button 
                  type="primary" 
                  size="large"
                  @click="handleSubmit" 
                  :loading="submitting"
                  class="submit-btn"
                >
                  <el-icon><Check /></el-icon>
                  {{ submitting ? $t('productReview.submitting') : $t('productReview.publishReview') }}
                </el-button>
                <el-button size="large" @click="handleCancel" class="cancel-btn">
                  <el-icon><Close /></el-icon>
                  {{ $t('productReview.cancel') }}
                </el-button>
              </div>
            </el-form>
          </el-card>
        </div>
      </template>

      <el-empty v-else-if="!loading" :description="$t('productReview.orderNotFound')" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  EditPen,
  Plus,
  Check,
  Close,
  Box,
  View,
  QuestionFilled,
  Star,
  Picture,
  ChatDotRound,
  Edit,
  InfoFilled,
  WarnTriangleFilled
} from '@element-plus/icons-vue'
import { reviewApi } from '@/api/review'
import { orderApi } from '@/api/order'
import { formatImageUrl } from '@/utils/url'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const orderId = ref(route.query.orderId)
const loading = ref(false)
const submitting = ref(false)
const orderInfo = ref(null)
const reviewFormRef = ref()

const apiBaseUrl = computed(() => (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/+$/, ''))
const uploadAction = computed(() => `${apiBaseUrl.value}/file/upload`)
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return { Authorization: token ? `Bearer ${token}` : '' }
})

const reviewForm = reactive({
  orderId: orderId.value,
  rating: 5,
  content: '',
  images: []
})

const reviewRules = {
  rating: [
    { required: true, message: t('productReview.rules.ratingRequired'), trigger: 'change' }
  ],
  content: [
    { required: true, message: t('productReview.rules.contentRequired'), trigger: 'blur' },
    { min: 10, message: t('productReview.rules.contentMinLength'), trigger: 'blur' }
  ]
}

const ratingTexts = [
  'productReview.ratingTexts.veryBad',
  'productReview.ratingTexts.bad',
  'productReview.ratingTexts.average',
  'productReview.ratingTexts.satisfied',
  'productReview.ratingTexts.verySatisfied'
]

const getRatingDescription = (rating) => {
  const descriptions = [
    '',
    t('productReview.ratingDesc.1'),
    t('productReview.ratingDesc.2'),
    t('productReview.ratingDesc.3'),
    t('productReview.ratingDesc.4'),
    t('productReview.ratingDesc.5')
  ]
  return descriptions[rating] || ''
}

const getFormProgress = () => {
  let progress = 0
  if (reviewForm.rating > 0) progress += 30
  if (reviewForm.content && reviewForm.content.length >= 10) progress += 50
  if (reviewForm.images && reviewForm.images.length > 0) progress += 20
  return Math.min(progress, 100)
}

const getImageUrl = (images) => {
  if (!images) return ''
  try {
    const imageArray = typeof images === 'string' ? JSON.parse(images) : images
    const firstImage = imageArray[0] || images
    return formatImageUrl(firstImage)
  } catch {
    return formatImageUrl(images)
  }
}

const handleUploadSuccess = (response) => {
  if (response.code === '200') {
    const imageUrl = response.data?.fileUrl || response.data?.filePath || response.data
    reviewForm.images.push(imageUrl)
  }
}

const fetchOrderInfo = async () => {
  loading.value = true
  try {
    const res = await orderApi.getOrderDetail(orderId.value)
    if (res.code === '200') {
      orderInfo.value = res.data
    } else {
      ElMessage.error(t('productReview.error.fetchOrder'))
    }
  } catch (error) {
    console.error(t('productReview.error.fetchOrder'), error)
    ElMessage.error(t('productReview.error.fetchOrder'))
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!reviewFormRef.value) return
  
  try {
    await reviewFormRef.value.validate()
    
    submitting.value = true
    const submitData = {
      orderId: reviewForm.orderId,
      rating: reviewForm.rating,
      content: reviewForm.content,
      images: reviewForm.images.map(img => img.url || img).join(',')
    }
    
    const res = await reviewApi.createReview(submitData)
    
    if (res.code === '200') {
      ElMessage.success(t('productReview.success.submit'))
      router.push('/user/orders')
    } else {
      ElMessage.error(res.message || t('productReview.error.submit'))
    }
  } catch (error) {
    console.error($t('productReview.error.submit'), error)
    if (error !== 'cancel') {
      ElMessage.error(t('productReview.error.submit'))
    }
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  router.back()
}

onMounted(() => {
  if (!orderId.value) {
    ElMessage.error(t('productReview.error.missingOrderId'))
    router.push('/user/orders')
    return
  }
  fetchOrderInfo()
})
</script>

<style scoped>
.product-review-page {
  min-height: calc(100vh - 110px);
  background: #f5f7fa;
  padding: 16px;
}

/* 页面头部 */
.page-header {
  margin-bottom: 20px;
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  color: #409eff;
  font-size: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.header-right {
  flex: 1;
  max-width: 300px;
}

/* 主要容器 */
.review-container {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 20px;
  align-items: start;
}

/* 左侧区域 */
.left-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-card,
.guide-card,
.review-form-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.card-header .el-icon {
  color: #409eff;
}

/* 商品预览 */
.product-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
}

.product-image {
  width: 100%;
  height: 100%;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  opacity: 0;
  transition: opacity 0.3s;
}

.product-image-wrapper:hover .image-overlay {
  opacity: 1;
}

.product-details {
  padding: 0 4px;
}

.product-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.product-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.price-info,
.order-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-label,
.order-label {
  font-size: 12px;
  color: #909399;
}

.product-price {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.order-number {
  font-size: 12px;
  color: #606266;
  font-family: monospace;
}

/* 订单时间线 */
.order-timeline {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f2f5;
}

/* 评价指南 */
.guide-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.guide-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 6px;
  font-size: 13px;
  color: #606266;
}

.guide-icon {
  color: #409eff;
  font-size: 16px;
}

/* 右侧表单区域 */
.right-section {
  min-height: 600px;
}

.form-progress {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-left: auto;
  width: 120px;
}

.progress-text {
  font-size: 12px;
  color: #606266;
  text-align: right;
}

/* 评分区域 */
.rating-section {
  background: linear-gradient(135deg, #fef9f0 0%, #fff8f0 100%);
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.rating-form-item {
  margin-bottom: 0;
}

.rating-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.rating-stars {
  font-size: 24px;
}

.rating-desc {
  font-size: 14px;
  color: #409eff;
  font-weight: 500;
  text-align: center;
}

/* 评价内容 */
.content-form-item {
  margin-bottom: 20px;
}

.review-textarea {
  border-radius: 8px;
}

.content-tips {
  margin-top: 8px;
}

/* 图片上传 */
.upload-form-item {
  margin-bottom: 30px;
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-upload {
  width: 100%;
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s;
  cursor: pointer;
}

.upload-trigger:hover {
  border-color: #409eff;
  background: #fef9f0;
}

.upload-text {
  font-size: 14px;
  color: #606266;
}

.upload-tips {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #909399;
}

.tip-item .el-icon {
  font-size: 14px;
}

/* 提交区域 */
.submit-section {
  display: flex;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #f0f2f5;
}

.submit-btn {
  flex: 1;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.cancel-btn {
  width: 100px;
  height: 48px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .review-container {
    grid-template-columns: 320px 1fr;
    gap: 16px;
  }
}

@media (max-width: 992px) {
  .review-container {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .left-section {
    order: 2;
  }
  
  .right-section {
    order: 1;
  }
  
  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .header-right {
    width: 100%;
    max-width: none;
  }
}

@media (max-width: 768px) {
  .product-review-page {
    padding: 12px;
  }
  
  .page-header {
    padding: 12px 16px;
  }
  
  .page-title {
    font-size: 18px;
  }
  
  .product-image-wrapper {
    height: 150px;
  }
  
  .rating-section {
    padding: 16px;
  }
  
  .submit-section {
    flex-direction: column;
  }
  
  .cancel-btn {
    width: 100%;
  }
}
</style>
