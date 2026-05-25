<template>
  <div class="write-review-page">
    <div class="page-container">
      <div class="page-header">
        <h2 class="page-title">{{ $t('writeReview.pageTitle') }}</h2>
      </div>

      <el-card class="review-card" v-loading="loading">
        <div class="order-info" v-if="orderInfo">
          <div class="product-image">
            <img :src="getProductImage(orderInfo.productImages)" :alt="orderInfo.productTitle" />
          </div>
          <div class="product-details">
            <div class="product-title">{{ orderInfo.productTitle }}</div>
            <div class="product-price">楼{{ orderInfo.amount }}</div>
          </div>
        </div>

        <el-divider />

        <el-form
          ref="reviewFormRef"
          :model="reviewForm"
          :rules="reviewRules"
          label-width="100px"
          label-position="top"
        >
          <el-form-item :label="$t('writeReview.ratingLabel')" prop="rating">
            <el-rate
              v-model="reviewForm.rating"
              :size="32"
              show-text
              :texts="[$t('writeReview.ratingText1'), $t('writeReview.ratingText2'), $t('writeReview.ratingText3'), $t('writeReview.ratingText4'), $t('writeReview.ratingText5')]"
            />
          </el-form-item>

          <el-form-item :label="$t('writeReview.contentLabel')" prop="content">
            <el-input
              v-model="reviewForm.content"
              type="textarea"
              :rows="6"
              :placeholder="$t('writeReview.contentPlaceholder')"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button type="warning" size="large" class="submit-btn" @click="handleSubmit">
              {{ $t('writeReview.submitBtn') }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'
import { reviewApi } from '@/api/review'
import { useI18n } from 'vue-i18n' // 寮曞叆 i18n
import { DEFAULT_PRODUCT_IMAGE, formatProductImageUrl } from '@/utils/url'

const { t } = useI18n() // 瑙ｆ瀯 t 鍑芥暟
const route = useRoute()
const router = useRouter()

const loading = ref(false)
const orderInfo = ref(null)
const reviewFormRef = ref(null)

const reviewForm = ref({
  rating: 5,
  content: ''
})

const reviewRules = {
  rating: [
    { required: true, message: t('writeReview.ratingRequired'), trigger: 'change' }
  ],
  content: [
    { required: true, message: t('writeReview.contentRequired'), trigger: 'blur' },
    { min: 10, message: t('writeReview.contentMin'), trigger: 'blur' },
    { max: 500, message: t('writeReview.contentMax'), trigger: 'blur' }
  ]
}

const getProductImage = (images) => {
  if (!images) return DEFAULT_PRODUCT_IMAGE
  try {
    const imageArray = typeof images === 'string' ? JSON.parse(images) : images
    return formatProductImageUrl(imageArray[0]) || DEFAULT_PRODUCT_IMAGE
  } catch {
    return DEFAULT_PRODUCT_IMAGE
  }
}

const loadOrderInfo = async () => {
  loading.value = true
  try {
    const orderId = route.query.orderId

    if (!orderId) {
      ElMessage.error(t('writeReview.lackOrderInfo'))
      router.push('/user/orders')
      return
    }

    const checkRes = await reviewApi.checkReviewed(orderId)
    if (checkRes.code === '200' && checkRes.data) {
      ElMessage.warning(t('writeReview.orderReviewed'))
      router.push('/user/orders')
      return
    }

    const res = await orderApi.getOrderById(orderId)

    if (res.code === '200') {
      orderInfo.value = res.data
    } else {
      throw new Error(res.message || t('writeReview.getOrderFail'))
    }
  } catch (error) {
    ElMessage.error(error.message || t('writeReview.getOrderFail'))
    router.push('/user/orders')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!reviewFormRef.value) return

  await reviewFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await ElMessageBox.confirm(
          t('writeReview.confirmSubmit'),
          t('writeReview.confirmTitle'),
          {
            confirmButtonText: t('writeReview.confirmBtn'),
            cancelButtonText: t('writeReview.cancelBtn'),
            type: 'info'
          }
        )

        const orderId = route.query.orderId

        const data = {
          orderId: orderId,
          productId: orderInfo.value.productId,
          rating: reviewForm.value.rating,
          content: reviewForm.value.content
        }

        const res = await reviewApi.createReview(data)

        if (res.code === '200') {
          ElMessage.success(t('writeReview.submitSuccess'))
          router.push('/user/orders')
        } else {
          throw new Error(res.message || t('writeReview.submitFail'))
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.message || t('writeReview.submitFail'))
        }
      }
    }
  })
}

onMounted(() => {
  loadOrderInfo()
})
</script>

<style scoped>
.write-review-page {
  min-height: calc(100vh - 110px);
  background: #f0f2f5;
  padding: 20px;
}

.page-container {
  max-width: 600px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f0f7ff 0%, #fafbfc 100%);
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.review-card {
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.order-info {
  display: flex;
  gap: 16px;
  padding: 20px 0;
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  line-height: 1.5;
}

.product-price {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  font-size: 16px;
  color: #2c3e50;
}

:deep(.el-rate) {
  height: 40px;
}

:deep(.el-rate__text) {
  font-size: 16px;
  color: #606266;
}

.submit-btn {
  width: 100%;
  margin-top: 16px;
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  border: none;
  font-size: 16px;
  font-weight: 600;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #f97316 0%, #f59e0b 100%);
}

@media (max-width: 768px) {
  .write-review-page {
    padding: 16px;
  }

  .page-container {
    max-width: 100%;
  }

  .order-info {
    flex-direction: column;
  }

  .product-image {
    width: 100%;
    height: 200px;
  }
}
</style>

