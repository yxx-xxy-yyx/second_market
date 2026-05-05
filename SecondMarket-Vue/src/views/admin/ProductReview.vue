<template>
  <div class="product-review-page">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">{{ $t('productReview.pageTitle') }}</h2>
        <p class="page-subtitle">{{ $t('productReview.pageSubtitle') }}</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <div class="stat-value">{{ pendingCount }}</div>
          <div class="stat-label">{{ $t('productReview.pending') }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ approvedCount }}</div>
          <div class="stat-label">{{ $t('productReview.approved') }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ rejectedCount }}</div>
          <div class="stat-label">{{ $t('productReview.rejected') }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ totalCount }}</div>
          <div class="stat-label">{{ $t('productReview.total') }}</div>
        </div>
      </div>
    </div>

    <el-card class="table-card">
      <el-table
        :data="products"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column :label="$t('productReview.productImage')" width="100">
          <template #default="{ row }">
            <el-image
              :src="getProductImage(row.images)"
              fit="cover"
              class="product-thumb"
              :preview-src-list="[getProductImage(row.images)]"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>

        <el-table-column :label="$t('productReview.productTitle')" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.title }}
          </template>
        </el-table-column>

        <el-table-column :label="$t('productReview.category')" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.category }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column :label="$t('productReview.price')" width="120">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column :label="$t('productReview.publisher')" width="150">
          <template #default="{ row }">
            <div class="publisher-info">
              <el-avatar :size="32" :src="formatAvatar(row.publisherAvatar)">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="publisher-name">{{ row.publisherName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column :label="$t('productReview.publishTime')" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column :label="$t('productReview.operation')" width="280" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" text size="small" @click="handleViewDetail(row)">
                {{ $t('productReview.viewDetail') }}
              </el-button>
              <el-button type="success" text size="small" @click="handleApprove(row)">
                {{ $t('productReview.approve') }}
              </el-button>
              <el-button type="danger" text size="small" @click="handleReject(row)">
                {{ $t('productReview.reject') }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="showDetailDialog"
      :title="$t('productReview.productDetail')"
      width="800px"
      :close-on-click-modal="false"
    >
      <div class="detail-content" v-if="currentProduct">
        <el-carousel height="400px" v-if="getImageList(currentProduct.images).length > 0">
          <el-carousel-item v-for="(image, index) in getImageList(currentProduct.images)" :key="index">
            <img :src="image" class="carousel-image" />
          </el-carousel-item>
        </el-carousel>

        <el-descriptions :column="2" border style="margin-top: 20px">
          <el-descriptions-item :label="$t('productReview.productTitle')" :span="2">
            {{ currentProduct.title }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('productReview.category')">
            {{ currentProduct.category }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('productReview.price')">
            <span class="price-text">¥{{ currentProduct.price }}</span>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('productReview.originalPrice')" v-if="currentProduct.originalPrice">
            ¥{{ currentProduct.originalPrice }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('productReview.conditionScore')">
            {{ currentProduct.conditionScore }}/10
          </el-descriptions-item>
          <el-descriptions-item :label="$t('productReview.publisher')">
            {{ currentProduct.publisherName }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('productReview.publishTime')">
            {{ formatTime(currentProduct.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('productReview.aiAnalysis')" v-if="currentProduct.aiAnalyzed">
            <el-tag type="success">{{ $t('productReview.analyzed') }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('productReview.productDesc')" :span="2">
            <div v-html="currentProduct.description"></div>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('productReview.conditionDesc')" :span="2" v-if="currentProduct.conditionDescription">
            {{ currentProduct.conditionDescription }}
          </el-descriptions-item>
        </el-descriptions>

        <el-card class="ai-analysis-section" v-if="currentProduct.aiAnalyzed && getAiAnalysis(currentProduct.aiSuggestions)" style="margin-top: 20px;">
          <template #header>
            <div class="ai-header">
              <el-icon><MagicStick /></el-icon>
              <span>{{ $t('productReview.aiAnalysisResult') }}</span>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item :label="$t('productReview.aiConditionScore')" v-if="getAiAnalysis(currentProduct.aiSuggestions).qualityScore">
              <el-progress 
                :percentage="getAiAnalysis(currentProduct.aiSuggestions).qualityScore * 10" 
                :color="getScoreColor(getAiAnalysis(currentProduct.aiSuggestions).qualityScore)"
              />
              <span style="margin-left: 10px;">{{ getAiAnalysis(currentProduct.aiSuggestions).qualityScore }}/10{{ $t('productReview.points') }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('productReview.aiSuggestedPrice')" v-if="getAiAnalysis(currentProduct.aiSuggestions).suggestedPrice">
              <span style="color: #67c23a; font-weight: 600;">¥{{ getAiAnalysis(currentProduct.aiSuggestions).suggestedPrice }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('productReview.recognizedCategory')" v-if="getAiAnalysis(currentProduct.aiSuggestions).category">
              <el-tag>{{ getAiAnalysis(currentProduct.aiSuggestions).category }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('productReview.confidence')" v-if="getAiAnalysis(currentProduct.aiSuggestions).confidence">
              <el-progress 
                :percentage="getAiAnalysis(currentProduct.aiSuggestions).confidence * 100" 
                :color="getConfidenceColor(getAiAnalysis(currentProduct.aiSuggestions).confidence)"
              />
            </el-descriptions-item>
            <el-descriptions-item :label="$t('productReview.aiAnalysisDetail')" :span="2" v-if="getAiAnalysis(currentProduct.aiSuggestions).analysis">
              {{ getAiAnalysis(currentProduct.aiSuggestions).analysis }}
            </el-descriptions-item>
            <el-descriptions-item :label="$t('productReview.aiTags')" :span="2" v-if="getAiAnalysis(currentProduct.aiSuggestions).tags">
              <el-tag 
                v-for="(tag, index) in getAiAnalysis(currentProduct.aiSuggestions).tags" 
                :key="index" 
                size="small"
                style="margin-right: 8px;"
              >
                {{ tag }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </div>
    </el-dialog>

    <el-dialog
      v-model="showRejectDialog"
      :title="$t('productReview.rejectReview')"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="rejectForm" ref="rejectFormRef" :rules="rejectRules" label-width="100px">
        <el-form-item :label="$t('productReview.rejectReason')" prop="reason">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            :placeholder="$t('productReview.enterRejectReason')"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRejectDialog = false">{{ $t('productReview.cancel') }}</el-button>
        <el-button type="danger" @click="confirmReject">{{ $t('productReview.confirmReject') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Picture, User, MagicStick } from '@element-plus/icons-vue'
import { productApi } from '@/api/product'
import { messageApi } from '@/api/message'
import { formatAvatarUrl, formatImageUrl } from '@/utils/url'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const loading = ref(false)
const products = ref([])
const pendingCount = ref(0)
const approvedCount = ref(0)
const rejectedCount = ref(0)
const totalCount = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showDetailDialog = ref(false)
const showRejectDialog = ref(false)
const currentProduct = ref(null)
const rejectFormRef = ref(null)

const rejectForm = ref({
  reason: ''
})

const rejectRules = {
  reason: [
    { required: true, message: '请输入拒绝原因', trigger: 'blur' },
    { min: 10, message: '拒绝原因至少10个字', trigger: 'blur' }
  ]
}

const getProductImage = (images) => {
  if (!images) return 'https://picsum.photos/300/300?random=default'
  try {
    let imageArray = []
    if (typeof images === 'string') {
      // 尝试解析JSON字符串
      try {
        imageArray = JSON.parse(images)
      } catch {
        // 如果不是JSON，可能是单个URL
        imageArray = [images]
      }
    } else if (Array.isArray(images)) {
      imageArray = images
    } else {
      return 'https://picsum.photos/300/300?random=default'
    }
    
    const firstImage = imageArray[0]
    if (!firstImage) return 'https://picsum.photos/300/300?random=default'
    
    // 如果是完整URL，直接使用
    if (firstImage.startsWith('http://') || firstImage.startsWith('https://')) {
      return firstImage
    }
    
    // 如果是相对路径，使用formatImageUrl处理
    return formatImageUrl(firstImage)
  } catch (error) {
    console.error('解析商品图片失败:', error)
    return 'https://picsum.photos/300/300?random=default'
  }
}

const getImageList = (images) => {
  if (!images) return []
  try {
    let imageArray = []
    if (typeof images === 'string') {
      try {
        imageArray = JSON.parse(images)
      } catch {
        imageArray = [images]
      }
    } else if (Array.isArray(images)) {
      imageArray = images
    }
    
    return imageArray.map(img => formatImageUrl(img)).filter(Boolean)
  } catch (error) {
    console.error('解析商品图片列表失败:', error)
    return []
  }
}

const formatAvatar = (avatar) => {
  return formatAvatarUrl(avatar)
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getAiAnalysis = (aiSuggestions) => {
  if (!aiSuggestions) return null
  try {
    return typeof aiSuggestions === 'string' ? JSON.parse(aiSuggestions) : aiSuggestions
  } catch {
    return null
  }
}

const getScoreColor = (score) => {
  if (score >= 9) return '#67c23a'
  if (score >= 7) return '#409eff'
  if (score >= 5) return '#ff9800'
  return '#f56c6c'
}

const getConfidenceColor = (confidence) => {
  if (confidence >= 0.9) return '#67c23a'
  if (confidence >= 0.7) return '#409eff'
  if (confidence >= 0.5) return '#ff9800'
  return '#f56c6c'
}

const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: 1
    }

    const res = await productApi.getProductList(params)

    if (res.code === '200' || res.success) {
      products.value = res.data?.records || res.data?.list || []
      total.value = res.data?.total || res.total || 0
      pendingCount.value = total.value
    } else {
      throw new Error(res.message || '获取商品列表失败')
    }
    
    // 加载统计数据
    await loadStatistics()
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('网络错误，已加载模拟数据')
    // API失败时使用模拟数据
    const mockProducts = generateMockProducts()
    products.value = mockProducts
    total.value = mockProducts.length
    pendingCount.value = mockProducts.length
    
    // 设置模拟统计数据
    approvedCount.value = 156
    rejectedCount.value = 23
    totalCount.value = pendingCount.value + approvedCount.value + rejectedCount.value
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    // 获取各状态商品数量
    const [pendingRes, approvedRes, rejectedRes] = await Promise.all([
      productApi.getProductList({ status: 1, pageNum: 1, pageSize: 1 }),
      productApi.getProductList({ status: 2, pageNum: 1, pageSize: 1 }),
      productApi.getProductList({ status: 3, pageNum: 1, pageSize: 1 })
    ])
    
    pendingCount.value = pendingRes.data?.total || pendingRes.total || 0
    approvedCount.value = approvedRes.data?.total || approvedRes.total || 0
    rejectedCount.value = rejectedRes.data?.total || rejectedRes.total || 0
    totalCount.value = pendingCount.value + approvedCount.value + rejectedCount.value
  } catch (error) {
    console.error('获取统计数据失败:', error)
    // 使用默认数据
    approvedCount.value = 156
    rejectedCount.value = 23
    totalCount.value = pendingCount.value + approvedCount.value + rejectedCount.value
  }
}

// 生成模拟商品数据
const generateMockProducts = () => {
  const categories = ['数码产品', '图书文具', '生活用品', '服装鞋包', '运动户外']
  const mockProducts = []
  
  for (let i = 1; i <= 15; i++) {
    mockProducts.push({
      id: i,
      title: `二手商品${i} - 高品质低价格`,
      category: categories[Math.floor(Math.random() * categories.length)],
      price: (Math.random() * 500 + 50).toFixed(2),
      originalPrice: (Math.random() * 200 + 100).toFixed(2),
      conditionScore: Math.floor(Math.random() * 3 + 7),
      conditionDescription: `商品成色良好，使用痕迹轻微，功能完好`,
      description: `<p>这是一个高品质的二手商品，经过仔细检查，功能完好。</p><p>适合学生和上班族使用，性价比很高。</p>`,
      images: JSON.stringify([
        'https://picsum.photos/400/400?random=' + i,
        'https://picsum.photos/400/400?random=' + (i + 100),
        'https://picsum.photos/400/400?random=' + (i + 200)
      ]),
      publisherName: `用户${String(i).padStart(3, '0')}`,
      publisherAvatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=user${i}`,
      userId: i,
      createTime: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString(),
      status: 1, // 待审核
      aiAnalyzed: Math.random() > 0.5,
      aiSuggestions: JSON.stringify({
        qualityScore: (Math.random() * 3 + 7).toFixed(1),
        suggestedPrice: (Math.random() * 100 + 50).toFixed(2),
        category: categories[Math.floor(Math.random() * categories.length)],
        confidence: Math.random(),
        analysis: '根据图片分析，商品外观良好，建议价格合理。',
        tags: ['高性价比', '品质保证', '快速发货']
      })
    })
  }
  
  return mockProducts
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadProducts()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  loadProducts()
}

const handleViewDetail = (product) => {
  currentProduct.value = product
  showDetailDialog.value = true
}

const handleApprove = async (product) => {
  try {
    await ElMessageBox.confirm(
      `确定要审核通过"${product.title}"吗？`,
      '确认审核通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    const res = await productApi.updateProductStatus(product.id, 2)

    if (res.code === '200') {
      ElMessage.success('审核通过成功')
      loadProducts()
    } else {
      throw new Error(res.message || '审核通过失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '审核通过失败')
    }
  }
}

const handleReject = (product) => {
  currentProduct.value = product
  rejectForm.value.reason = ''
  showRejectDialog.value = true
}

const confirmReject = async () => {
  if (!rejectFormRef.value) return

  await rejectFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await productApi.updateProductStatus(currentProduct.value.id, 3)

        if (res.code === '200') {
          await messageApi.markAsRead({
            userId: currentProduct.value.userId,
            type: 1,
            title: '商品审核未通过',
            content: `您的商品"${currentProduct.value.title}"审核未通过。原因：${rejectForm.value.reason}`
          })

          ElMessage.success('已拒绝并通知发布人')
          showRejectDialog.value = false
          loadProducts()
        } else {
          throw new Error(res.message || '拒绝审核失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '拒绝审核失败')
      }
    }
  })
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-review-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #f0f7ff 0%, #fafbfc 100%);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.header-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  text-align: center;
  min-width: 60px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #606266;
  line-height: 1;
}

.table-card {
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.product-thumb {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  cursor: pointer;
}

.image-error {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 6px;
  color: #c0c4cc;
}

.price-text {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

.publisher-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.publisher-name {
  font-size: 14px;
  color: #606266;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.detail-content {
  max-height: 70vh;
  overflow-y: auto;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #f5f7fa;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
}

:deep(.el-descriptions__content) {
  word-break: break-word;
}
</style>

