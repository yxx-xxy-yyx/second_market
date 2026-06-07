<template>
  <div class="favorites-page">
    <div class="page-container">
      <div class="page-header">
        <h2 class="page-title">{{ $t('favorites.pageTitle') }}</h2>
      </div>

      <div class="favorites-content" v-loading="loading">
        <el-empty v-if="!loading && products.length === 0" :description="$t('favorites.noFavorites')">
          <el-button type="primary" @click="handleGoShopping">{{ $t('favorites.goShopping') }}</el-button>
        </el-empty>

        <el-row :gutter="15" v-if="products.length > 0">
          <el-col v-for="product in products" :key="product.id" :xs="24" :sm="12" :md="8" :lg="6">
            <div class="product-card" @click="handleViewDetail(product.id)">
              <div class="product-image-wrapper">
                <img :src="getProductImage(product.images)" :alt="product.title" class="product-image" />
                <div class="product-badge" v-if="product.aiAnalyzed">
                  <el-tag size="small" type="success">{{ $t('favorites.aiAnalyzed') }}</el-tag>
                </div>
                <div class="sold-out-mask" v-if="product.status === 4">
                  <span>{{ $t('favorites.soldOut') }}</span>
                </div>
              </div>
              <div class="product-info">
                <div class="product-title">{{ product.title }}</div>
                <div class="product-price-row">
                  <span class="product-price">{{ $t('common.currency') }}{{ product.price }}</span>
                  <span class="product-original-price" v-if="product.originalPrice">
                    {{ $t('common.currency') }}{{ product.originalPrice }}
                  </span>
                </div>
                <div class="product-meta">
                  <div class="product-condition">
                    <span class="meta-label">{{ $t('favorites.condition') }}</span>
                    <el-progress :percentage="product.conditionScore * 10" :stroke-width="6" :show-text="false"
                      :color="getConditionColor(product.conditionScore)" />
                  </div>
                  <div class="product-views">
                    <el-icon>
                      <View />
                    </el-icon>
                    <span>{{ product.viewCount }}</span>
                  </div>
                </div>
                <div class="product-actions">
                  <el-button type="primary" text size="small" @click.stop="handleViewDetail(product.id)">
                    {{ $t('favorites.viewDetail') }}
                  </el-button>
                  <el-button type="danger" text size="small" @click.stop="handleRemoveFavorite(product)">
                    {{ $t('favorites.removeFavorite') }}
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[12, 24, 36]"
            :total="total" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange"
            @current-change="handlePageChange" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View } from '@element-plus/icons-vue'
import { favoriteApi } from '@/api/favorite'
import { formatImageUrl } from '@/utils/url'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t } = useI18n()

const loading = ref(false)
const products = ref([])
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)

const getProductImage = (images) => {
  if (!images) return ''
  try {
    const imageArray = typeof images === 'string' ? JSON.parse(images) : images
    const firstImage = Array.isArray(imageArray) ? imageArray[0] : imageArray
    return formatImageUrl(firstImage) || ''
  } catch {
    return ''
  }
}

const getConditionColor = (score) => {
  if (score >= 9) return '#67c23a'
  if (score >= 7) return '#409eff'
  if (score >= 5) return '#e6a23c'
  return '#f56c6c'
}

const loadFavorites = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }

    const res = await favoriteApi.getMyFavorites(params)

    if (res.code === '200' || res.success) {
      const favorites = res.data.records || []
      products.value = favorites.map(favorite => ({
        id: favorite.productId,
        title: favorite.productTitle || t('favorites.unknownProduct'),
        images: favorite.productImages,
        price: favorite.productPrice,
        originalPrice: favorite.productOriginalPrice,
        status: favorite.productStatus,
        conditionScore: favorite.productConditionScore || 5,
        viewCount: favorite.productViewCount || 0,
        favoriteCount: favorite.productFavoriteCount || 0,
        aiAnalyzed: favorite.productAiAnalyzed || 0,
        category: favorite.productCategory,
        createTime: favorite.createTime
      }))
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || t('favorites.getFavoritesFailed'))
    }
  } catch (error) {
    ElMessage.error(error.message || t('favorites.getFavoritesFailed'))
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadFavorites()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  loadFavorites()
}

const handleViewDetail = (productId) => {
  router.push(`/user/product/${productId}`)
}

const handleRemoveFavorite = async (product) => {
  try {
    await ElMessageBox.confirm(
      t('favorites.confirmRemoveFavorite', { title: product.title }),
      t('favorites.removeFavoriteTitle'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )

    const res = await favoriteApi.deleteFavorite(product.id)

    if (res.code === '200' || res.success) {
      ElMessage.success(t('favorites.removeFavoriteSuccess'))
      if (products.value.length === 1 && pageNum.value > 1) {
        pageNum.value--
      }
      loadFavorites()
    } else {
      ElMessage.error(res.message || t('favorites.removeFavoriteFailed'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || t('favorites.removeFavoriteFailed'))
    }
  }
}

const handleGoShopping = () => {
  router.push('/user/products')
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-page {
  min-height: calc(100vh - 110px);
  background: #f0f2f5;
  padding: 20px;
}

.page-container {
  max-width: 1200px;
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

.favorites-content {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  min-height: 400px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e4e7ed;
  margin-bottom: 15px;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
  background: #f5f7fa;
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 2;
}

.sold-out-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 3;
}

.sold-out-mask span {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
}

.product-info {
  padding: 12px;
}

.product-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  height: 40px;
  line-height: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-price-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
}

.product-original-price {
  font-size: 12px;
  color: #909399;
  text-decoration: line-through;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-top: 1px solid #e4e7ed;
  margin-bottom: 8px;
}

.product-condition {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-right: 12px;
}

.meta-label {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.product-condition :deep(.el-progress) {
  flex: 1;
}

.product-views {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.product-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #e4e7ed;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

@media (max-width: 768px) {
  .favorites-page {
    padding: 16px;
  }

  .product-card {
    margin-bottom: 16px;
  }
}
</style>
