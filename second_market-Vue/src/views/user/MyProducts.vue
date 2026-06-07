<template>
  <div class="my-products-page">
    <div class="page-header">
      <h2 class="page-title">{{ $t('nav.myProducts') }}</h2>
    </div>

    <el-row :gutter="20" class="equal-height-row">
      <!-- 左侧主内容区 -->
      <el-col :xs="24" :sm="24" :md="18" :lg="18" class="left-main-col">
        <!-- 搜索和筛选 -->
        <el-card class="filter-card" shadow="never">
          <div class="filter-section">
            <div class="filter-left">
              <el-input v-model="searchKeyword" :placeholder="$t('common.search')" style="width: 280px" clearable
                @clear="handleSearch" @keyup.enter="handleSearch">
                <template #prefix>
                  <el-icon>
                    <Search />
                  </el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="handleSearch">{{ $t('common.search') }}</el-button>
            </div>
            <div class="filter-right">
              <el-select v-model="statusFilter" :placeholder="$t('common.status')" style="width: 140px"
                @change="handleSearch">
                <el-option :label="$t('common.all')" :value="null" />
                <el-option :label="$t('order.draft')" :value="0" />
                <el-option :label="$t('order.pendingReview')" :value="1" />
                <el-option :label="$t('order.onSale')" :value="2" />
                <el-option :label="$t('order.offSale')" :value="3" />
                <el-option :label="$t('order.completed')" :value="4" />
              </el-select>
            </div>
          </div>
        </el-card>

        <!-- 商品表格 -->
        <el-card class="table-card fixed-height-card" shadow="never">
          <el-table :data="products" v-loading="loading" stripe style="width: 100%" :row-class-name="tableRowClassName">
            <el-table-column :label="$t('product.productImage')" width="100">
              <template #default="{ row }">
                <el-image :src="getProductImage(row.images)" fit="cover" class="product-thumb"
                  @click="handleView(row.id)">
                  <template #error>
                    <div class="image-error">
                      <el-icon>
                        <Picture />
                      </el-icon>
                    </div>
                  </template>
                </el-image>
              </template>
            </el-table-column>

            <el-table-column :label="$t('product.title')" min-width="200" show-overflow-tooltip>
              <template #default="{ row }">
                <div class="product-title-cell" @click="handleView(row.id)">
                  {{ locale === 'ko' && row.titleKo ? row.titleKo : row.title }}
                </div>
              </template>
            </el-table-column>

            <el-table-column :label="$t('product.category')" width="120">
              <template #default="{ row }">
                <el-tag size="small">{{ getCategoryName(row.category) }}</el-tag>
              </template>
            </el-table-column>

            <el-table-column :label="$t('product.price')" width="120">
              <template #default="{ row }">
                <span class="price-text">{{ formatPrice(row.price) }}</span>
              </template>
            </el-table-column>

            <el-table-column :label="$t('common.status')" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column :label="$t('product.viewCount')" width="102">
              <template #default="{ row }">
                <span class="meta-text">
                  <el-icon>
                    <View />
                  </el-icon>
                  {{ row.viewCount }}
                </span>
              </template>
            </el-table-column>

            <el-table-column :label="$t('profile.favoriteCount')" width="100">
              <template #default="{ row }">
                <span class="meta-text">
                  <el-icon>
                    <Star />
                  </el-icon>
                  {{ row.favoriteCount }}
                </span>
              </template>
            </el-table-column>

            <el-table-column :label="$t('product.publishTime')" width="120">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>

            <el-table-column :label="$t('common.actions')" width="200" fixed="right">
              <template #default="{ row }">
                <div class="action-button-grid">
                  <el-button type="primary" size="small" @click="handleView(row.id)">
                    {{ $t('common.view') }}
                  </el-button>
                  <el-button type="warning" size="small" @click="handleEdit(row.id)" :disabled="row.status === 4">
                    {{ $t('common.edit') }}
                  </el-button>
                  <el-button v-if="row.status === 3" type="success" size="small" @click="handleToggleStatus(row, 2)">
                    {{ $t('common.list') }}
                  </el-button>
                  <el-button v-else-if="row.status === 2" type="info" size="small" @click="handleToggleStatus(row, 3)">
                    {{ $t('common.unlist') }}
                  </el-button>
                  <el-button type="danger" size="small" @click="handleDelete(row)" :disabled="row.status === 4">
                    {{ $t('common.delete') }}
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <!--  自定义纯中文分页 -->
          <div class="pagination-wrapper" v-if="total > 0">
            <div class="custom-pagination">
              <span class="pagination-text">{{ $t('pagination.total') }} {{ total }} {{ $t('pagination.items') }}</span>

              <el-select v-model="pageSize" @change="handleSizeChange" size="default" class="page-size-select">
                <el-option :label="`10${$t('pagination.perPage')}`" value="10" />
                <el-option :label="`20${$t('pagination.perPage')}`" value="20" />
                <el-option :label="`50${$t('pagination.perPage')}`" value="50" />
              </el-select>

              <el-button :disabled="pageNum === 1" @click="prevPage">{{ $t('pagination.prev') }}</el-button>

              <el-pagination v-model:current-page="pageNum" :total="total" :page-size="pageSize" layout="pager"
                @current-change="handlePageChange" />

              <el-button :disabled="pageNum >= totalPages" @click="nextPage">{{ $t('pagination.next') }}</el-button>

              <span class="pagination-text">{{ $t('pagination.jumpTo') }}</span>
              <el-input v-model="jumpPage" @keyup.enter="handleJump" size="default" class="jump-input" />
              <span class="pagination-text">{{ $t('pagination.page') }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧统计栏 -->
      <el-col :xs="24" :sm="24" :md="6" :lg="6" class="right-side-col">
        <el-card class="stats-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>{{ $t('profile.tradeStats') }}</span>
              <el-icon>
                <DataAnalysis />
              </el-icon>
            </div>
          </template>

          <div class="stat-item" v-for="stat in stats" :key="stat.key">
            <div class="stat-icon-wrapper" :style="{ backgroundColor: `${stat.color}20`, color: stat.color }">
              <el-icon :size="24">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">{{ $t('profile.' + stat.key) }}</div>
              <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
            </div>
          </div>
        </el-card>

        <el-card class="quick-actions-card" shadow="never" style="margin-top: 10px;">
          <template #header>
            <div class="card-header">
              <span>{{ $t('order.quickActions') }}</span>
              <el-icon>
                <Operation />
              </el-icon>
            </div>
          </template>

          <div class="btn-group-wrapper">
            <div class="btn-item">
              <el-button type="primary" plain @click="router.push('/user/publish')" style="width:100%;height:36px;">
                <el-icon>
                  <Plus />
                </el-icon>{{ $t('product.postProduct') }}
              </el-button>
            </div>
            <div class="btn-item">
              <el-button plain @click="handleRefresh" style="width:100%;height:36px;">
                <el-icon>
                  <Refresh />
                </el-icon>{{ $t('product.refresh') }}
              </el-button>
            </div>
            <div class="btn-item">
              <el-button plain @click="handleExport" style="width:100%;height:36px;">
                <el-icon>
                  <Download />
                </el-icon>{{ $t('product.export') }}
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import {
  Search,
  View,
  Star,
  Picture,
  Box,
  ShoppingBag,
  CircleCheck,
  Clock,
  DataAnalysis,
  Operation,
  Plus,
  Refresh,
  Download
} from '@element-plus/icons-vue'
import { productApi } from '@/api/product'
import { formatImageUrl } from '@/utils/url'

const router = useRouter()
const { t, locale } = useI18n()

const loading = ref(false)
const products = ref([])
const searchKeyword = ref('')
const statusFilter = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 自定义分页变量
const jumpPage = ref('')
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const stats = ref([
  { key: 'publishCount', value: 0, icon: Box, color: '#409eff' },
  { key: 'onSaleCount', value: 0, icon: ShoppingBag, color: '#67c23a' },
  { key: 'soldCount', value: 0, icon: CircleCheck, color: '#909399' },
  { key: 'pendingReview', value: 0, icon: Clock, color: '#e6a23c' }
])

// 自定义分页方法
const prevPage = () => {
  if (pageNum.value > 1) {
    pageNum.value--
    handlePageChange(pageNum.value)
  }
}

const nextPage = () => {
  if (pageNum.value < totalPages.value) {
    pageNum.value++
    handlePageChange(pageNum.value)
  }
}

const handleJump = () => {
  const page = parseInt(jumpPage.value)
  if (!page || page < 1 || page > totalPages.value) {
    ElMessage.warning(t('pagination.invalidPage'))
    return
  }
  pageNum.value = page
  handlePageChange(page)
  jumpPage.value = ''
}

const getProductImage = (images) => {
  if (!images) return 'https://picsum.photos/300/300?random=default'
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
    } else {
      return 'https://picsum.photos/300/300?random=default'
    }

    const firstImage = imageArray[0]
    if (!firstImage) return 'https://picsum.photos/300/300?random=default'

    if (firstImage.startsWith('http://') || firstImage.startsWith('https://')) {
      return firstImage
    }

    return formatImageUrl(firstImage)
  } catch (error) {
    console.error('解析商品图片失败:', error)
    return 'https://picsum.photos/300/300?random=default'
  }
}

const getCategoryName = (cnName) => {
  const cnToKey = {
    "电子产品": "electronics",
    "家用电器": "appliances",
    "服装配饰": "clothing",
    "图书音像": "books",
    "运动户外": "sports",
    "美妆护肤": "beauty",
    "母婴玩具": "toys",
    "其他": "others"
  }
  const key = cnToKey[cnName]
  return key ? t(`categories.${key}`) : cnName
}

const formatPrice = (rmb) => {
  if (!rmb || isNaN(rmb)) return '0.00'
  const RATE = { en: 0.14, ko: 190 }
  const price = Number(rmb)
  switch (locale.value) {
    case 'zh': return `¥${price.toFixed(2)}`
    case 'en': return `$${(price * RATE.en).toFixed(2)}`
    case 'ko': return `${Math.round(price * RATE.ko)}₩`
    default: return `¥${price.toFixed(2)}`
  }
}

const getStatusType = (status) => {
  const typeMap = { 0: 'info', 1: 'warning', 2: 'success', 3: 'info', 4: 'primary' }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    0: t('order.draft'),
    1: t('order.pendingReview'),
    2: t('order.onSale'),
    3: t('order.offSale'),
    4: t('order.sold'),
  }
  return textMap[status] || t('common.unknown')
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit'
  })
}

const tableRowClassName = ({ row }) => {
  if (row.status === 4) return 'sold-out-row'
  return ''
}

const loadProducts = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (statusFilter.value !== null) params.status = statusFilter.value
    const res = await productApi.getMyProducts(params)
    if (res.code === '200') {
      products.value = res.data.records || []
      total.value = res.data.total || 0
      updateStats()
    }
  } catch (error) {
    ElMessage.error(error.message || t('common.fetchFailed'))
  } finally { loading.value = false }
}

const updateStats = async () => {
  try {
    const all = await productApi.getMyProducts({ pageNum: 1, pageSize: 1 })
    stats.value[0].value = all.data.total || 0
    const onSale = await productApi.getMyProducts({ pageNum: 1, pageSize: 1, status: 2 })
    stats.value[1].value = onSale.data.total || 0
    const sold = await productApi.getMyProducts({ pageNum: 1, pageSize: 1, status: 4 })
    stats.value[2].value = sold.data.total || 0
    const pending = await productApi.getMyProducts({ pageNum: 1, pageSize: 1, status: 1 })
    stats.value[3].value = pending.data.total || 0
  } catch (e) { }
}

const handleSearch = () => { pageNum.value = 1; loadProducts() }
const handlePageChange = (page) => { pageNum.value = page; loadProducts() }
const handleSizeChange = (size) => { pageSize.value = size; pageNum.value = 1; loadProducts() }

const handleView = (id) => router.push(`/user/product/${id}`)
const handleEdit = (id) => { ElMessage.info(t('common.featureInDev')) }

const handleToggleStatus = async (row, newStatus) => {
  const txt = newStatus === 2 ? t('common.list') : t('common.unlist')
  try {
    await ElMessageBox.confirm(
      t('myProducts.confirmToggle', { action: txt }),
      t('myProducts.confirmTitle', { action: txt }),
      { confirmButtonText: t('common.confirm'), cancelButtonText: t('common.cancel'), type: 'info' }
    )
    const res = await productApi.updateProductStatus(row.id, newStatus)
    if (res.code === '200') { ElMessage.success(t('myProducts.toggleSuccess', { action: txt })); loadProducts() }
  } catch (e) { if (e !== 'cancel') ElMessage.error(t('myProducts.toggleFail', { action: txt })) }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      t('myProducts.deleteConfirm'),
      t('myProducts.deleteTitle'),
      { confirmButtonText: t('common.confirm'), cancelButtonText: t('common.cancel'), type: 'warning' }
    )
    const res = await productApi.deleteProduct(row.id)
    if (res.code === '200') {
      ElMessage.success(t('myProducts.deleteSuccess'))
      if (products.value.length === 1 && pageNum.value > 1) pageNum.value--
      loadProducts()
    }
  } catch (e) { if (e !== 'cancel') ElMessage.error(t('myProducts.deleteFail')) }
}

const handleRefresh = () => { loadProducts(); updateStats(); ElMessage.success(t('common.refreshSuccess')) }
const handleExport = () => { ElMessage.info(t('common.featureInDev')) }

onMounted(() => { loadProducts(); updateStats() })
</script>

<style scoped>
/* 1. 强制 Row 容器等高对其 */
.equal-height-row {
  display: flex;
  align-items: stretch;
  /* 核心：让左右 Col 高度一致 */
  flex-wrap: wrap;
  /* 保证移动端能正常换行 */
}

/* 2. 左侧 Col 处理 */
.left-main-col {
  display: flex;
  flex-direction: column;
}

/* 2. 锁定左侧卡片高度 */
.table-card.fixed-height-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  margin-bottom: 0;
}

.table-card.fixed-height-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  /* 允许内部收缩，防止切换语言时长文字撑破框 */
  padding: 20px;
}

.table-card.fixed-height-card :deep(.el-table) {
  flex: 1;
  min-height: 0;
}

/* 5. 右侧 Col 处理 */
.right-side-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
  /* 两个卡片之间的间距 */
}

.right-side-col .el-card {
  margin-bottom: 0;
}

:deep(.sold-out-row) {
  background-color: #f5f7fa;
  opacity: 0.7;
}

.my-products-page {
  min-height: calc(100vh - 110px);
  background: #f0f2f5;
  padding: 24px;
  /* 👇 只加这两行，让页面宽度和其他页面统一 */
  max-width: 1400px;
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

.filter-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.filter-card :deep(.el-card__body) {
  padding: 16px;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-card {
  border-radius: 8px;
}

.table-card :deep(.el-card__body) {
  padding: 20px;
  /*  只加这一行，防止表格撑破页面 */
  overflow-x: auto;
}

.stats-card {
  border-radius: 8px;
  position: sticky;
  top: 84px;
}

.stats-card :deep(.el-card__body) {
  padding: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  color: #303133;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
}

.quick-actions-card {
  border-radius: 8px;
  position: sticky;
  top: calc(84px + 280px + 20px);
}

.quick-actions-card :deep(.el-card__body) {
  padding: 16px;
}

.btn-group-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-item {
  width: 100%;
}

.product-thumb {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  cursor: pointer;
  transition: transform 0.3s;
}

.product-thumb:hover {
  transform: scale(1.05);
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

.product-title-cell {
  cursor: pointer;
  color: #409eff;
  transition: color 0.3s;
}

.product-title-cell:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.price-text {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

.meta-text {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 14px;
}

/*  按钮两行两列布局 */
.action-button-grid {
  display: grid !important;
  grid-template-columns: 1fr 1fr !important;
  gap: 6px !important;
  width: 100% !important;
}

.action-button-grid .el-button {
  width: 100% !important;
  box-sizing: border-box !important;
  margin: 0 !important;
  /* 清除Element Plus默认的margin */
  padding: 4px 8px !important;
}

:deep(.sold-out-row) {
  background-color: #f5f7fa;
  opacity: 0.7;
}

/*  自定义分页样式 */
.pagination-wrapper {
  margin-top: 20px;
  flex-shrink: 0;
  /* 确保分页不被压缩 */
  padding-top: 10px;
  border-top: 1px solid #f5f7fa;
}

.custom-pagination {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.pagination-text {
  font-size: 14px;
  color: #606266;
}

.page-size-select {
  width: 110px;
}

.jump-input {
  width: 60px;
}

@media (max-width: 768px) {
  .my-products-page {
    padding: 16px;
  }

  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-left,
  .filter-right {
    width: 100%;
    flex-direction: column;
  }

  .filter-left .el-input,
  .filter-right .el-select {
    width: 100% !important;
  }
}
</style>
