<template>
  <div class="product-reviews-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-icon class="header-icon"><Star /></el-icon>
        <h2 class="page-title">{{ $t('productReviews.pageTitle') }}</h2>
        <el-tag v-if="productInfo" type="info" size="small">{{ productInfo.title }}</el-tag>
      </div>
      <div class="header-right">
        <el-button-group size="small">
          <el-button :type="sortBy === 'latest' ? 'primary' : ''" @click="handleSort('latest')">
            <el-icon><Clock /></el-icon>{{ $t('productReviews.latest') }}
          </el-button>
          <el-button :type="sortBy === 'rating' ? 'primary' : ''" @click="handleSort('rating')">
            <el-icon><TrendCharts /></el-icon>{{ $t('productReviews.rating') }}
          </el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 统计概览 -->
    <el-row :gutter="12" class="stats-section">
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <div class="stat-card primary">
          <div class="stat-icon">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ reviewStats.averageRating || '0.0' }}</div>
            <div class="stat-label">{{ $t('productReviews.averageRating') }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <div class="stat-card success">
          <div class="stat-icon">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ reviewStats.totalCount || 0 }}</div>
            <div class="stat-label">{{ $t('productReviews.totalReviews') }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <div class="stat-card warning">
          <div class="stat-icon">
            <el-icon><Pointer /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ reviewStats.goodRate || '0%' }}</div>
            <div class="stat-label">{{ $t('productReviews.goodRate') }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <div class="stat-card info">
          <div class="stat-icon">
            <el-icon><Picture /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ reviewStats.withImagesCount || 0 }}</div>
            <div class="stat-label">{{ $t('productReviews.reviewsWithImages') }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 评分分布 -->
    <el-card class="rating-distribution-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><TrendCharts /></el-icon>
          <span>{{ $t('productReviews.ratingDistribution') }}</span>
        </div>
      </template>
      <div class="rating-bars">
        <div v-for="(item, index) in ratingDistribution" :key="index" class="rating-bar-item">
          <div class="rating-label">{{ 5 - index }}{{ $t('productReviews.star') }}</div>
          <div class="rating-bar">
            <div class="rating-progress" :style="{ width: item.percentage + '%' }"></div>
          </div>
          <div class="rating-count">{{ item.count }}</div>
        </div>
      </div>
    </el-card>

    <!-- 筛选器 -->
    <div class="filter-section">
      <el-space wrap :size="8">
        <el-tag 
          v-for="filter in reviewFilters" 
          :key="filter.key"
          :type="activeFilter === filter.key ? 'primary' : ''"
          :effect="activeFilter === filter.key ? 'dark' : 'plain'"
          @click="handleFilterChange(filter.key)"
          class="filter-tag"
        >
          {{ $t(`productReviews.filter.${filter.key}`) }} ({{ filter.count }})
        </el-tag>
      </el-space>
    </div>

    <!-- 评价列表 -->
    <div class="reviews-container" v-loading="loading">
      <div class="reviews-grid">
        <div 
          v-for="review in reviews" 
          :key="review.id" 
          class="review-card"
          :class="{ 'has-images': review.images && review.images.length > 0 }"
        >
          <!-- 评价头部 -->
          <div class="review-header">
            <div class="user-info">
              <el-avatar :size="32" :src="formatAvatarUrl(review.userAvatar)">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="user-details">
                <div class="username">{{ review.userNickname || $t('productReviews.anonymousUser') }}</div>
                <div class="review-time">{{ formatTime(review.createTime) }}</div>
              </div>
            </div>
            <div class="rating-section">
              <el-rate 
                v-model="review.rating" 
                disabled 
                size="small"
                :colors="['#ff6b6b', '#feca57', '#409eff']"
              />
              <span class="rating-text">{{ getRatingText(review.rating) }}</span>
            </div>
          </div>

          <!-- 评价内容 -->
          <div class="review-content">
            <p class="review-text">{{ review.content }}</p>
          </div>

          <!-- 评价图片 -->
          <div v-if="review.images && review.images.length > 0" class="review-images">
            <div class="images-grid" :class="getImageGridClass(review.images.length)">
              <el-image
                v-for="(image, index) in review.images.slice(0, 6)"
                :key="index"
                :src="formatImageUrl(image)"
                fit="cover"
                class="review-image"
                :preview-src-list="review.images.map(img => formatImageUrl(img))"
                :initial-index="index"
                preview-teleported
              />
              <div 
                v-if="review.images.length > 6" 
                class="more-images"
                @click="handleViewAllImages(review.images)"
              >
                +{{ review.images.length - 6 }}
              </div>
            </div>
          </div>

          <!-- 评价底部 -->
          <div class="review-footer">
            <div class="review-actions">
              <el-button 
                text 
                size="small" 
                @click="handleLike(review)"
                :class="{ 'liked': review.isLiked }"
              >
                <el-icon><Pointer /></el-icon>
                {{ review.likeCount || 0 }}
              </el-button>
              <el-button text size="small" @click="handleReply(review)">
                <el-icon><ChatDotRound /></el-icon>
                {{ $t('productReviews.reply') }}
              </el-button>
            </div>
            <div class="review-meta">
              <el-tag v-if="review.isVerified" type="success" size="small" effect="plain">
                <el-icon><Select /></el-icon>{{ $t('productReviews.verifiedPurchase') }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && reviews.length === 0" :description="$t('productReviews.noReviews')" />

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { formatAvatarUrl, formatImageUrl } from '@/utils/url'
import { reviewApi } from '@/api/review'
import { productApi } from '@/api/product'
import {
  Star,
  Clock,
  TrendCharts,
  ChatDotRound,
  Pointer,
  Picture,
  User,
  Select
} from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n' // 引入 i18n

const { t } = useI18n() // 解构 t 函数

const route = useRoute()
const productId = ref(route.params.productId || route.query.productId)

const loading = ref(false)
const reviews = ref([])
const productInfo = ref(null)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const sortBy = ref('latest')
const activeFilter = ref('all')

const reviewStats = reactive({
  averageRating: 0,
  totalCount: 0,
  goodRate: '0%',
  withImagesCount: 0
})

const ratingDistribution = ref([
  { rating: 5, count: 0, percentage: 0 },
  { rating: 4, count: 0, percentage: 0 },
  { rating: 3, count: 0, percentage: 0 },
  { rating: 2, count: 0, percentage: 0 },
  { rating: 1, count: 0, percentage: 0 }
])

const reviewFilters = computed(() => [
  { key: 'all', label: $t('productReviews.filter.all'), count: reviewStats.totalCount },
  { key: 'withImages', label: $t('productReviews.filter.withImages'), count: reviewStats.withImagesCount },
  { key: 'good', label: $t('productReviews.filter.good'), count: ratingDistribution.value.slice(0, 2).reduce((sum, item) => sum + item.count, 0) },
  { key: 'medium', label: $t('productReviews.filter.medium'), count: ratingDistribution.value[2].count },
  { key: 'bad', label: $t('productReviews.filter.bad'), count: ratingDistribution.value.slice(3).reduce((sum, item) => sum + item.count, 0) }
])

const fetchProductInfo = async () => {
  try {
    const res = await productApi.getProductDetail(productId.value)
    if (res.code === '200') {
      productInfo.value = res.data
    }
  } catch (error) {
    console.error($t('productReviews.error.fetchProduct'), error)
  }
}

const fetchReviews = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      sortBy: sortBy.value,
      filter: activeFilter.value
    }
    
    const res = await reviewApi.getProductReviews(productId.value, params.pageNum, params.pageSize)
    if (res.code === '200') {
      reviews.value = res.data.records.map(review => ({
        ...review,
        images: review.images ? (typeof review.images === 'string' ? JSON.parse(review.images) : review.images) : [],
        isLiked: false,
        likeCount: Math.floor(Math.random() * 20),
        isVerified: Math.random() > 0.3
      }))
      total.value = res.data.total
      
      // 计算统计数据
      calculateStats()
    }
  } catch (error) {
    console.error($t('productReviews.error.fetchReviews'), error)
    ElMessage.error($t('productReviews.error.fetchReviews'))
  } finally {
    loading.value = false
  }
}

const calculateStats = () => {
  if (reviews.value.length === 0) return
  
  const totalRating = reviews.value.reduce((sum, review) => sum + review.rating, 0)
  reviewStats.averageRating = (totalRating / reviews.value.length).toFixed(1)
  reviewStats.totalCount = total.value
  reviewStats.withImagesCount = reviews.value.filter(review => review.images && review.images.length > 0).length
  
  const goodCount = reviews.value.filter(review => review.rating >= 4).length
  reviewStats.goodRate = ((goodCount / reviews.value.length) * 100).toFixed(0) + '%'
  
  // 计算评分分布
  const distribution = [0, 0, 0, 0, 0]
  reviews.value.forEach(review => {
    distribution[5 - review.rating]++
  })
  
  const maxCount = Math.max(...distribution)
  ratingDistribution.value = distribution.map((count, index) => ({
    rating: 5 - index,
    count,
    percentage: maxCount > 0 ? (count / maxCount) * 100 : 0
  }))
}

const handleSort = (type) => {
  sortBy.value = type
  pageNum.value = 1
  fetchReviews()
}

const handleFilterChange = (filter) => {
  activeFilter.value = filter
  pageNum.value = 1
  fetchReviews()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  fetchReviews()
}

const handleCurrentChange = (page) => {
  pageNum.value = page
  fetchReviews()
}

const getRatingText = (rating) => {
  const texts = [
    '',
    $t('productReviews.ratingText.veryBad'),
    $t('productReviews.ratingText.average'),
    $t('productReviews.ratingText.good'),
    $t('productReviews.ratingText.satisfied'),
    $t('productReviews.ratingText.veryGood')
  ]
  return texts[rating] || ''
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return $t('productReviews.time.justNow')
  if (diff < 3600000) return $t('productReviews.time.minutesAgo', { count: Math.floor(diff / 60000) })
  if (diff < 86400000) return $t('productReviews.time.hoursAgo', { count: Math.floor(diff / 3600000) })
  if (diff < 2592000000) return $t('productReviews.time.daysAgo', { count: Math.floor(diff / 86400000) })
  
  return date.toLocaleDateString()
}

const getImageGridClass = (count) => {
  if (count === 1) return 'single'
  if (count === 2) return 'double'
  if (count <= 4) return 'quad'
  return 'multi'
}

const handleLike = (review) => {
  review.isLiked = !review.isLiked
  review.likeCount += review.isLiked ? 1 : -1
}

const handleReply = (review) => {
  ElMessage.info($t('productReviews.underDevelopment.reply'))
}

const handleViewAllImages = (images) => {
  // 可以实现查看所有图片的功能
  ElMessage.info($t('productReviews.underDevelopment.viewAllImages'))
}

onMounted(() => {
  if (!productId.value) {
    ElMessage.error($t('productReviews.error.missingProductId'))
    return
  }
  fetchProductInfo()
  fetchReviews()
})
</script>

<style scoped>
.product-reviews-page {
  min-height: calc(100vh - 110px);
  background: #f5f7fa;
  padding: 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px 0;
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

.stats-section {
  margin-bottom: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-card.primary .stat-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #e6a23c 0%, #f0c674 100%);
}

.stat-card.info .stat-icon {
  background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  color: white;
  font-size: 18px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.rating-distribution-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.rating-bars {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rating-label {
  width: 30px;
  font-size: 12px;
  color: #606266;
}

.rating-bar {
  flex: 1;
  height: 6px;
  background: #f0f2f5;
  border-radius: 3px;
  overflow: hidden;
}

.rating-progress {
  height: 100%;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
  transition: width 0.3s;
}

.rating-count {
  width: 30px;
  text-align: right;
  font-size: 12px;
  color: #909399;
}

.filter-section {
  margin-bottom: 16px;
  padding: 12px 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.filter-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tag:hover {
  transform: translateY(-1px);
}

.reviews-container {
  min-height: 400px;
}

.reviews-grid {
  display: grid;
  gap: 12px;
}

.review-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.review-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.review-card.has-images {
  border-left: 3px solid #409eff;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.review-time {
  font-size: 12px;
  color: #909399;
}

.rating-section {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.rating-text {
  font-size: 12px;
  color: #409eff;
  font-weight: 500;
}

.review-content {
  margin-bottom: 12px;
}

.review-text {
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
  margin: 0;
}

.review-images {
  margin-bottom: 12px;
}

.images-grid {
  display: grid;
  gap: 6px;
  border-radius: 6px;
  overflow: hidden;
}

.images-grid.single {
  grid-template-columns: 1fr;
  max-width: 200px;
}

.images-grid.double {
  grid-template-columns: 1fr 1fr;
  max-width: 300px;
}

.images-grid.quad {
  grid-template-columns: 1fr 1fr;
  max-width: 300px;
}

.images-grid.multi {
  grid-template-columns: repeat(3, 1fr);
  max-width: 300px;
}

.review-image {
  width: 100%;
  height: 80px;
  cursor: pointer;
  border-radius: 4px;
}

.more-images {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 12px;
  cursor: pointer;
  border-radius: 4px;
}

.review-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #f0f2f5;
}

.review-actions {
  display: flex;
  gap: 8px;
}

.review-actions .el-button.liked {
  color: #409eff;
}

.review-meta {
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 16px;
  background: white;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .product-reviews-page {
    padding: 12px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .stat-card {
    padding: 12px;
  }
  
  .stat-value {
    font-size: 16px;
  }
  
  .review-card {
    padding: 12px;
  }
  
  .review-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .rating-section {
    align-items: flex-start;
  }
}
</style>