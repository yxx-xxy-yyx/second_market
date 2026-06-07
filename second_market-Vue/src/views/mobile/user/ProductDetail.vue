<template>
  <div class="product-detail-page">
    <div class="detail-container" v-loading="loading">
      <template v-if="product">
        <!-- 主内容区：商品图片 + 详细信息 -->
        <el-row :gutter="15" class="detail-main">
          <!-- 左侧：商品图片区域 -->
          <el-col :xs="24" :sm="24" :md="8" :lg="8" class="left-image-col">
            <!-- 搜索栏 -->
            <div class="search-bar-wrapper">
              <el-input v-model="searchKeyword" :placeholder="$t('common.search')" size="default" clearable
                @keyup.enter="handleSearch" class="search-input">
                <template #prefix>
                  <el-icon class="cursor-pointer" :size="18" @click="router.back()">
                    <ArrowLeft />
                  </el-icon>
                </template>
                <template #suffix>
                  <el-icon class="cursor-pointer" :size="18" style="color: #409eff;" @click="handleSearch">
                    <Search />
                  </el-icon>
                </template>
              </el-input>
            </div>

            <!-- 卖家信息条 -->
            <div class="seller-info-bar" @click="showSellerDrawer = true">
              <el-avatar :size="36" :src="formatAvatar(product.userAvatar)">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="seller-info-bar-detail">
                <span class="seller-info-bar-name">{{ product.userNickname || $t('product.anonymousUser') }}</span>
                <el-rate v-model="sellerRating.averageRating" disabled size="small" show-score text-color="#ff9800" />
              </div>
              <el-icon :size="16" color="#c0c4cc"><ArrowRight /></el-icon>
            </div>

            <el-card class="image-card fixed-height-card" shadow="never" :body-style="{ padding: '12px' }">
              <div class="image-section">
                <div class="main-image-wrapper">
                  <el-image :src="currentImage" :preview-src-list="imageList" fit="contain" class="main-image"
                    :initial-index="currentImageIndex">
                    <template #error>
                      <div class="image-error">
                        <el-icon>
                          <Picture />
                        </el-icon>
                        <span>{{ $t('product.noImage') }}</span>
                      </div>
                    </template>
                  </el-image>
                  <div v-if="product.status === 4" class="sold-out-mask">
                    <div class="sold-out-text">{{ $t('product.soldOut') }}</div>
                  </div>
                </div>

                <div class="thumbnail-list">
                  <div v-for="(image, index) in imageList" :key="index"
                    :class="['thumbnail-item', { active: currentImageIndex === index }]"
                    @click="currentImageIndex = index">
                    <el-image :src="image" fit="cover" class="thumbnail-img" />
                  </div>
                </div>

                <!-- 商品标题和标签 -->
                <h1 class="product-title">{{ locale === 'ko' && product.titleKo ? product.titleKo : product.title }}
                </h1>
                <div class="product-tags">
                  <el-tag type="success" effect="dark" v-if="product.aiAnalyzed || aiAnalysisData" size="small">
                    <el-icon>
                      <MagicStick />
                    </el-icon>
                    {{ $t('product.aiAnalyzed') }}
                  </el-tag>
                  <el-tag type="primary" size="small">{{ getCategoryName(product.category) }}</el-tag>
                  <el-tag type="info" size="small" v-if="product.status === 4">{{ $t('product.soldOut') }}</el-tag>
                </div>

                <!-- 价格和统计信息 -->
                <div class="price-section-header" style="margin-top: 2px;">
                  <div class="current-price-header">
                    <span class="price-symbol-header">¥</span>
                    <span class="price-value-header">{{ product.price }}</span>
                  </div>
                  <div class="original-price-header" v-if="product.originalPrice">
                    <span class="original-text">{{ $t('product.originalPrice') }}</span>
                    <span class="original-value">¥{{ product.originalPrice }}</span>
                    <el-tag type="danger" size="mini" class="discount-tag-header">
                      {{ $t('product.save') }}¥{{ (product.originalPrice - product.price).toFixed(2) }}
                    </el-tag>
                  </div>
                </div>

                <div class="header-stats" style="margin-top: -5px;">
                  <div class="header-stat-item">
                    <el-icon color="#909399">
                      <View />
                    </el-icon>
                    <span class="stat-label">{{ $t('product.viewCount') }}</span>
                    <span class="stat-value">{{ product.viewCount }}</span>
                  </div>
                  <div class="header-stat-item">
                    <el-icon color="#f56c6c">
                      <Star />
                    </el-icon>
                    <span class="stat-label">{{ $t('dashboard.stats.favorites') }}</span>
                    <span class="stat-value">{{ product.favoriteCount || 0 }}</span>
                  </div>
                </div>

                <!-- 商品成色和描述 -->
                <div class="product-info-section" style="margin-top: 12px;">
                  <!-- 商品成色 -->
                  <div class="info-item" style="margin-bottom: 12px;">
                    <div class="info-value">{{ product.conditionDesc || $t('product.noConditionDesc') }}</div>
                  </div>
                  <!-- 商品描述 -->
                  <div class="info-item">
                    <div class="info-value">{{ product.description || $t('product.noDesc') }}</div>
                  </div>
                  <!-- 成色评分 -->
                  <div class="info-item" style="margin-top: 8px;">
                    <div class="info-value" style="display: flex; align-items: center; gap: 6px;">
                      <el-icon color="#409eff"><Stamp /></el-icon>
                      <span class="stat-label">{{ $t('product.condition') }}</span>
                      <el-rate v-model="product.conditionScore" disabled show-score :max="10" text-color="#ff9800" size="small" />
                    </div>
                  </div>
                  <!-- 交易须知 -->
                  <el-divider style="margin: 12px 0;" />
                  <div class="info-item" style="margin-top: 12px;">
                    <div class="info-label" style="display: flex; align-items: center; gap: 6px; font-size: 12px;">
                      <el-icon color="#409eff"><InfoFilled /></el-icon>
                      <span>{{ $t('product.buyNotice') }}</span>
                    </div>
                    <ul class="tips-list" style="margin: 6px 0 0 0; padding-left: 18px; font-size: 11px; color: #909399; line-height: 1.8;">
                      <li>{{ $t('product.notice1') }}</li>
                      <li>{{ $t('product.notice2') }}</li>
                      <li>{{ $t('product.notice3') }}</li>
                      <li>{{ $t('product.notice4') }}</li>
                    </ul>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 中间：商品信息和AI评估 -->
          <el-col :xs="24" :sm="24" :md="8" :lg="8" class="middle-info-col">
            <div class="middle-info-wrapper">
              <!-- AI评估卡片 -->
              <el-card class="ai-analysis-card" shadow="never" :body-style="{ padding: '12px' }">
                <template #header>
                  <div class="card-header ai-header">
                    <div class="header-left-content">
                      <el-icon>
                        <MagicStick />
                      </el-icon>
                      <span>{{ $t('publish.ai.aiEvaluation') }}</span>
                      <el-tag type="success" size="small" effect="dark" v-if="product.aiAnalyzed || aiAnalysisData">{{
                        $t('publish.ai.analyzed') }}</el-tag>
                      <el-tag type="info" size="small" v-else>{{ $t('publish.ai.notAnalyzed') }}</el-tag>
                    </div>

                  </div>
                </template>

                <!-- 有AI数据时显示 -->
                <div class="ai-analysis-content" v-if="aiAnalysisData">
                  <div class="ai-item">
                    <div class="ai-label">
                      <el-icon>
                        <TrendCharts />
                      </el-icon>
                      {{ $t('publish.ai.conditionScore') }}
                    </div>
                    <div class="ai-value">
                      <el-progress :percentage="(aiAnalysisData.conditionScore || 0) * 10"
                        :color="getScoreColor(aiAnalysisData.conditionScore || 0)" :stroke-width="6" />
                      <span class="score-text">{{ aiAnalysisData.conditionScore || 0 }}/10{{ $t('product.point')
                      }}</span>
                    </div>
                  </div>

                  <div class="ai-item"
                    v-if="aiAnalysisData.conditionDescription || aiAnalysisData.conditionExplanation">
                    <div class="ai-label">
                      <el-icon>
                        <Stamp />
                      </el-icon>
                      {{ $t('publish.ai.conditionAnalysis') }}
                    </div>
                    <div class="ai-value analysis-text">
                      {{ aiAnalysisData.conditionDescription || aiAnalysisData.conditionExplanation }}
                    </div>
                  </div>

                  <div class="ai-item" v-if="aiAnalysisData.priceMin || aiAnalysisData.suggestedPrice">
                    <div class="ai-label">
                      <el-icon>
                        <Money />
                      </el-icon>
                      {{ $t('publish.ai.suggestedPrice') }}
                    </div>
                    <div class="ai-value price-suggestion">
                      <span v-if="aiAnalysisData.priceMin && aiAnalysisData.priceMax">
                        ¥{{ aiAnalysisData.priceMin }} - ¥{{ aiAnalysisData.priceMax }}
                      </span>
                      <span v-else-if="aiAnalysisData.suggestedPrice">
                        ¥{{ aiAnalysisData.suggestedPrice }}
                      </span>
                    </div>
                  </div>

                  <div class="ai-item" v-if="aiAnalysisData.category || aiAnalysisData.brand || aiAnalysisData.model">
                    <div class="ai-label">
                      <el-icon>
                        <Box />
                      </el-icon>
                      {{ $t('publish.ai.productInfo') }}
                    </div>
                    <div class="ai-value">
                      <div v-if="aiAnalysisData.category" style="margin-bottom: 4px;">
                        <el-tag type="primary" size="small">{{ aiAnalysisData.category }}</el-tag>
                      </div>
                      <div v-if="aiAnalysisData.brand || aiAnalysisData.model" style="font-size: 12px; color: #909399;">
                        <span v-if="aiAnalysisData.brand">{{ aiAnalysisData.brand }}</span>
                        <span v-if="aiAnalysisData.brand && aiAnalysisData.model"> / </span>
                        <span v-if="aiAnalysisData.model">{{ aiAnalysisData.model }}</span>
                      </div>
                    </div>
                  </div>

                  <div class="ai-item" v-if="aiAnalysisData.description">
                    <div class="ai-label">{{ $t('publish.ai.productDesc') }}</div>
                    <div class="ai-value analysis-text">
                      {{ aiAnalysisData.description }}
                    </div>
                  </div>
                </div>

                <!-- 无AI数据时显示 -->
                <div class="no-ai-data" v-else>
                  <div class="no-ai-content">
                    <el-icon :size="48" color="#c0c4cc">
                      <MagicStick />
                    </el-icon>
                    <p class="no-ai-text">{{ $t('product.noAiAnalysis') }}</p>
                    <el-button type="primary" size="default" :loading="aiAnalyzing" @click="handleRequestAiAnalysis"
                      v-if="product.images && userStore.isLoggedIn" class="ai-analyze-btn">
                      <el-icon>
                        <MagicStick />
                      </el-icon>
                      {{ aiAnalyzing ? $t('product.aiAnalyzing') : $t('product.aiEvaluateNow') }}
                    </el-button>
                    <p class="no-ai-tip" v-if="!product.images">{{ $t('product.needImageForAi') }}</p>
                    <p class="no-ai-tip" v-if="!userStore.isLoggedIn">{{ $t('product.loginForAi') }}</p>
                  </div>
                </div>
              </el-card>

            </div>
          </el-col>

          <!-- 右侧：操作区域 -->
          <el-col :xs="24" :sm="24" :md="8" :lg="8" class="right-action-col">
            <div class="action-section">
              <!-- 操作按钮（桌面端显示） -->
              <el-card class="seller-action-card mobile-hide" shadow="hover">
                <div class="desktop-actions">
                  <!-- 按钮1：查看卖家 -->
                  <div class="btn-wrapper">
                    <el-button type="primary" plain class="view-seller-btn" @click="showSellerDrawer = true">
                      <el-icon>
                        <User />
                      </el-icon>
                      {{ $t('seller.viewProfile') }}
                    </el-button>
                  </div>

                  <!-- 按钮2：联系卖家 -->
                  <div class="btn-wrapper">
                    <el-button class="contact-btn" @click="handleContact">
                      <el-icon>
                        <ChatDotRound />
                      </el-icon>
                      {{ $t('seller.contactSeller') }}
                    </el-button>
                  </div>

                  <el-divider />

                  <!-- 立即购买 -->
                  <div class="btn-wrapper">
                    <el-button type="warning" size="large" :disabled="product.status === 4 || isOwner" @click="handleBuy"
                      class="action-btn buy-btn">
                      <el-icon>
                        <ShoppingCart />
                      </el-icon>
                      {{ $t('seller.buynow') }}
                    </el-button>
                  </div>

                  <div class="secondary-actions">
                    <!-- 收藏 -->
                    <div class="btn-wrapper">
                      <el-button size="large" :type="isFavorited ? 'danger' : 'default'" @click="handleFavorite"
                        class="action-btn">
                        <el-icon v-if="isFavorited">
                          <StarFilled />
                        </el-icon>
                        <el-icon v-else>
                          <Star />
                        </el-icon>
                        {{ $t('seller.favorite') }}
                      </el-button>
                    </div>

                    <!-- 分享 -->
                    <div class="btn-wrapper">
                      <el-popover placement="bottom" :width="220" trigger="click">
                        <template #reference>
                          <el-button size="large" class="action-btn">
                            <el-icon>
                              <Share />
                            </el-icon>
                            {{ $t('seller.share') }}
                          </el-button>
                        </template>
                        <div class="share-popover">
                          <div class="share-title">{{ $t('product.shareToFriend') }}</div>
                          <div class="share-methods">
                            <div class="share-method" @click="handleShare('link')">
                              <el-icon :size="28" color="#409eff">
                                <Link />
                              </el-icon>
                              <span>{{ $t('product.copyLink') }}</span>
                            </div>
                            <div class="share-method" @click="handleShare('qrcode')">
                              <el-icon :size="28" color="#67c23a">
                                <Picture />
                              </el-icon>
                              <span>{{ $t('product.generateQrcode') }}</span>
                            </div>
                            <div class="share-method" @click="handleShare('poster')">
                              <el-icon :size="28" color="#ff9800">
                                <Picture />
                              </el-icon>
                              <span>{{ $t('product.sharePoster') }}</span>
                            </div>
                          </div>
                        </div>
                      </el-popover>
                    </div>
                  </div>
                </div>
              </el-card>


            </div>
          </el-col>
        </el-row>


      </template>

      <el-empty v-else-if="!loading" :description="$t('product.productNotExist')" :image-size="200" />
    </div>

    <!-- 卖家详情抽屉 -->
    <el-drawer v-model="showSellerDrawer" :title="undefined" direction="rtl" size="450px">
      <div class="seller-drawer-content" v-if="product">
        <!-- 卖家头部信息 -->
        <div class="seller-drawer-header">
          <div class="seller-drawer-avatar">
            <el-avatar :size="80" :src="formatAvatar(product.userAvatar)">
              <el-icon>
                <User />
              </el-icon>
            </el-avatar>
          </div>
          <div class="seller-drawer-info">
            <div class="seller-drawer-name">{{ product.userNickname || $t('product.anonymousUser') }}</div>
            <div class="seller-drawer-rating">
              <el-rate v-model="sellerRating.averageRating" disabled show-score text-color="#ff9800" />
            </div>
            <div class="seller-drawer-stats">
              <span class="stat">{{ sellerRating.totalCount }} {{ $t('product.reviewCount') }}</span>
              <el-divider direction="vertical" />
              <span class="stat">{{ $t('product.onSale') }} {{ sellerProducts.length }} {{ $t('product.item') }}</span>
            </div>
          </div>
        </div>

        <!-- 卖家统计 -->
        <div class="seller-stats-section">
          <div class="stat-card-mini">
            <el-icon :size="28" color="#409eff">
              <Goods />
            </el-icon>
            <div class="stat-info">
              <div class="stat-label">{{ $t('product.totalSales') }}</div>
              <div class="stat-value">{{ sellerStats.totalSales }}</div>
            </div>
          </div>
          <div class="stat-card-mini">
            <el-icon :size="28" color="#67c23a">
              <TrendCharts />
            </el-icon>
            <div class="stat-info">
              <div class="stat-label">{{ $t('product.goodRate') }}</div>
              <div class="stat-value">{{ sellerStats.goodRate }}%</div>
            </div>
          </div>
        </div>

        <!-- 卖家在售商品 -->
        <div class="seller-products-section">
          <div class="section-title">
            <el-icon>
              <ShoppingBag />
            </el-icon>
            <span>{{ $t('product.onSaleProduct') }}</span>
          </div>
          <div class="seller-products-list">
            <div v-for="item in sellerProducts" :key="item.id" class="seller-product-item"
              @click="goToProduct(item.id)">
              <el-image :src="getProductImage(item.images)" fit="cover" class="seller-product-image" />
              <div class="seller-product-info">
                <div class="seller-product-title">{{ item.title }}</div>
                <div class="seller-product-price">¥{{ item.price }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 联系卖家按钮 -->
        <div class="seller-drawer-actions">
          <el-button type="primary" size="large" @click="handleContact" class="contact-seller-btn">
            <el-icon>
              <ChatDotRound />
            </el-icon>
            {{ $t('seller.contactSeller') }}
          </el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 移动端固定底部操作栏 -->
    <div class="mobile-bottom-actions" v-if="product">
      <div class="mobile-action-item" @click="handleShare('link')">
        <el-icon :size="20"><Share /></el-icon>
        <span>{{ $t('seller.share') }}</span>
      </div>
      <div class="mobile-action-item" @click="handleFavorite">
        <el-icon :size="20">
          <StarFilled v-if="isFavorited" />
          <Star v-else />
        </el-icon>
        <span>{{ $t('seller.favorite') }}</span>
      </div>
      <div class="mobile-action-item" @click="handleContact">
        <el-icon :size="20"><ChatDotRound /></el-icon>
        <span>{{ $t('seller.contactSeller') }}</span>
      </div>
      <div class="mobile-action-item mobile-buy-btn" @click="handleBuy">
        <el-icon :size="20"><ShoppingCart /></el-icon>
        <span>{{ $t('seller.buynow') }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '@/api/product'
import { favoriteApi } from '@/api/favorite'
import { reviewApi } from '@/api/review'
import { orderApi } from '@/api/order'
import { aiApi } from '@/api/ai'
import { useUserStore } from '@/stores/user'
import { formatAvatarUrl, formatImageUrl } from '@/utils/url'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElNotification, ElMessageBox } from 'element-plus'
import {
  Picture,
  View,
  Star,
  StarFilled,
  ShoppingCart,
  User,
  ChatDotRound,
  Share,
  Link,
  Clock,
  Document,
  MagicStick,
  Stamp,
  InfoFilled,
  Goods,
  TrendCharts,
  ShoppingBag,
  Money,
  Box,
  Search,
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { t, locale } = useI18n()

const searchKeyword = ref('')

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/user/products', query: { keyword: searchKeyword.value.trim() } })
  }
}

const loading = ref(false)
const product = ref(null)
const currentImageIndex = ref(0)
const isFavorited = ref(false)
const activeTab = ref('description')
const reviews = ref([])
const showSellerDrawer = ref(false)
const sellerProducts = ref([])
const aiAnalyzing = ref(false)
const aiAnalysisResult = ref(null)

const sellerRating = ref({
  averageRating: 4.5,
  totalCount: 0
})

const sellerStats = ref({
  totalSales: 0,
  goodRate: 95
})

const currentImage = computed(() => {
  return imageList.value[currentImageIndex.value] || ''
})

const imageList = computed(() => {
  if (!product.value || !product.value.images) return []
  try {
    let images = typeof product.value.images === 'string'
      ? JSON.parse(product.value.images)
      : product.value.images

    if (!Array.isArray(images)) {
      images = [images]
    }

    // 使用formatImageUrl处理每个图片URL
    return images.map(img => {
      if (!img) return 'https://picsum.photos/400/400?random=default'

      // 如果是完整URL，直接使用
      if (img.startsWith('http://') || img.startsWith('https://')) {
        return img
      }

      // 如果是相对路径，使用formatImageUrl处理
      return formatImageUrl(img)
    })
  } catch {
    return ['https://picsum.photos/400/400?random=default']
  }
})

const isOwner = computed(() => {
  return userStore.isLoggedIn && product.value && product.value.userId === userStore.user?.id
})

const aiAnalysisData = computed(() => {
  if (aiAnalysisResult.value) {
    return aiAnalysisResult.value
  }

  if (!product.value || !product.value.aiSuggestions) {
    return null
  }

  try {
    const suggestions = typeof product.value.aiSuggestions === 'string'
      ? JSON.parse(product.value.aiSuggestions)
      : product.value.aiSuggestions

    return {
      productName: suggestions.productName,
      brand: suggestions.brand,
      model: suggestions.model,
      category: suggestions.category,
      conditionScore: suggestions.conditionScore,
      conditionDescription: suggestions.conditionDescription || suggestions.conditionExplanation,
      conditionExplanation: suggestions.conditionDescription || suggestions.conditionExplanation,
      priceMin: suggestions.minPrice || suggestions.priceMin,
      priceMax: suggestions.maxPrice || suggestions.priceMax,
      suggestedPrice: suggestions.suggestedPrice,
      description: suggestions.description,
      analysis: suggestions.conditionDescription || suggestions.analysis || suggestions.description,
      summary: suggestions.description || suggestions.summary,
      tags: suggestions.tags || []
    }
  } catch (error) {
    console.error('解析AI建议失败:', error)
    return null
  }
})

const getCategoryName = (cnName) => {
  // 中文 => 英文key 映射表
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

const handleRequestAiAnalysis = async () => {
  if (!product.value || !product.value.images) {
    ElMessage.warning(t('product.noImageForAi'))
    return
  }

  const imageUrls = imageList.value
  if (!imageUrls || imageUrls.length === 0) {
    ElMessage.warning(t('product.invalidImageUrl'))
    return
  }

  aiAnalyzing.value = true
  try {
    const additionalInfo = `${t('product.productTitle')}：${product.value.title || ''}\n${t('product.productDesc')}：${product.value.description || ''}`

    const res = await aiApi.analyzeProductImage({
      imageUrls: imageUrls,
      additionalInfo: additionalInfo
    })

    if (res.code === '200') {
      aiAnalysisResult.value = res.data
      ElMessage.success(t('product.aiAnalysisSuccess'))

      const updateRes = await productApi.updateProduct({
        id: product.value.id,
        aiAnalyzed: 1,
        aiSuggestions: JSON.stringify(res.data)
      })

      if (updateRes.code === '200') {
        await fetchProductDetail()
      }
    } else {
      ElMessage.error(res.message || t('product.aiAnalysisFail'))
    }
  } catch (error) {
    console.error('AI分析失败:', error)
    ElMessage.error(error.message || t('product.aiAnalysisRetry'))
  } finally {
    aiAnalyzing.value = false
  }
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

const formatAvatar = (avatar) => {
  if (!avatar) return ''

  // 如果是完整URL（http或https开头），直接使用
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }

  // 如果是相对路径，拼接基础URL
  // 相对路径格式：/uploads/2025/10/06/xxx.jpg
  // 拼接后格式：http://localhost:8080/api/uploads/2025/10/06/xxx.jpg
  return `/api${avatar.startsWith('/') ? '' : '/'}${avatar}`
}

const formatTime = (time) => {
  if (!time) return ''
  try {
    return new Date(time).toLocaleString(locale.value === 'zh' ? 'zh-CN' : locale.value === 'en' ? 'en-US' : 'ko-KR')
  } catch {
    return time
  }
}

const getConditionColor = (score) => {
  if (score >= 9) return '#67c23a'
  if (score >= 7) return '#409eff'
  if (score >= 5) return '#ff9800'
  return '#f56c6c'
}

const getScoreColor = (score) => {
  if (score >= 9) return '#67c23a'
  if (score >= 7) return '#409eff'
  if (score >= 5) return '#ff9800'
  return '#f56c6c'
}

const getConditionText = (score) => {
  if (score >= 9) return t('product.almostNew')
  if (score >= 7) return t('product.goodCondition')
  if (score >= 5) return t('product.normalUse')
  return t('product.obviousUseTraces')
}

const fetchProductDetail = async () => {
  loading.value = true
  try {
    const res = await productApi.viewProduct(route.params.id)
    if (res.code === '200') {
      product.value = res.data
      checkFavoriteStatus()
      fetchReviews()
      fetchSellerProducts()
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error(t('product.getProductFail'))
  } finally {
    loading.value = false
  }
}

const checkFavoriteStatus = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await favoriteApi.checkFavorite(product.value.id)
    if (res.code === '200') {
      isFavorited.value = res.data
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

const fetchReviews = async () => {
  try {
    const res = await reviewApi.getReviewsByProductId(route.params.id, 1, 50)
    if (res.code === '200') {
      if (res.data && res.data.records) {
        reviews.value = res.data.records || []
      } else if (Array.isArray(res.data)) {
        reviews.value = res.data
      } else {
        reviews.value = []
      }
    }
  } catch (error) {
    console.error('获取评价失败:', error)
    reviews.value = []
  }
}

const fetchSellerProducts = async () => {
  if (!product.value) return
  try {
    const res = await productApi.getProductList({
      userId: product.value.userId,
      status: 2,
      pageNum: 1,
      pageSize: 6
    })
    if (res.code === '200') {
      sellerProducts.value = (res.data.records || []).filter(p => p.id !== product.value.id)
      sellerStats.value.totalSales = res.data.total || 0
    }
  } catch (error) {
    console.error('获取卖家商品失败:', error)
  }
}

const handleBuy = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning(t('product.pleaseLogin'))
    router.push('/login')
    return
  }

  if (isOwner.value) {
    ElMessage.warning(t('product.cannotBuyOwn'))
    return
  }

  try {
    await ElMessageBox.confirm(
      `${t('product.confirmBuy')}"${product.value.title}"？`,
      t('product.confirmPurchase'),
      {
        confirmButtonText: t('product.confirm'),
        cancelButtonText: t('product.cancel'),
        type: 'warning'
      }
    )

    const res = await orderApi.createOrder({
      productId: product.value.id
    })
    if (res.code === '200') {
      ElNotification({
        title: t('product.orderSuccess'),
        message: t('product.orderCreated'),
        type: 'success',
        duration: 3000
      })
      router.push('/user/orders')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || t('product.orderFail'))
    }
  }
}

const handleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning(t('product.pleaseLogin'))
    router.push('/login')
    return
  }

  try {
    if (isFavorited.value) {
      const res = await favoriteApi.removeFavorite(product.value.id)
      if (res.code === '200') {
        isFavorited.value = false
        ElNotification({
          title: t('product.cancelFavorite'),
          message: t('product.removedFromFavorite'),
          type: 'info'
        })
      }
    } else {
      const res = await favoriteApi.addFavorite(product.value.id)
      if (res.code === '200') {
        isFavorited.value = true
        ElNotification({
          title: t('product.favoriteSuccess'),
          message: t('product.addedToFavorite'),
          type: 'success'
        })
      }
    }
  } catch (error) {
    ElMessage.error(error.message || t('product.operationFail'))
  }
}

const handleContact = () => {

  if (!userStore.isLoggedIn) {
    ElMessage.warning(t('product.pleaseLogin'))
    router.push('/login')
    return
  }

  if (!product.value?.userId) {
    console.error('[Chat Error] 卖家 ID (userId) 缺失:', product.value)
    ElMessage.error('无法获取卖家信息，请刷新重试')
    return
  }

  // 兼容不同的用户 ID 存储方式
  const myId = userStore.user?.id || userStore.userInfo?.id
  if (String(product.value.userId) === String(myId)) {
    ElMessage.warning(t('product.cannotContactSelf'))
    return
  }

  const targetPath = `/user/chat/${product.value.userId}`
  console.log('[Navigation] 正在尝试跳转到:', targetPath)

  router.push({
    path: targetPath,
    query: {
      goodsId: product.value.id,
      name: product.value.userNickname || product.value.username
    }
  }).catch(err => {
    console.error('[Navigation Error] 跳转失败:', err)
    // 如果 404，尝试去掉 /user 前缀（视你的路由配置而定）
    if (err.name === 'NavigationDuplicated') return

    router.push({
      path: `/chat/${product.value.userId}`,
      query: { goodsId: product.value.id }
    })
  });
}

const handleShare = (type) => {
  const url = window.location.href

  if (type === 'link') {
    navigator.clipboard.writeText(url).then(() => {
      ElNotification({
        title: t('product.copySuccess'),
        message: t('product.linkCopied'),
        type: 'success'
      })
    }).catch(() => {
      ElMessage.error(t('product.copyFail'))
    })
  } else if (type === 'qrcode') {
    ElMessage.info(t('product.qrcodeDeveloping'))
  } else if (type === 'poster') {
    ElMessage.info(t('product.posterDeveloping'))
  }
}

const goToReviews = () => {
  router.push(`/user/product/${product.value.id}/reviews`)
}

const goToProduct = (productId) => {
  showSellerDrawer.value = false
  router.push(`/user/product/${productId}`)
}

onMounted(() => {
  fetchProductDetail()
})
</script>

<style scoped>
.product-detail-page {
  min-height: calc(100vh - 110px);
  background: #fff;
  padding: 15px 0;
}

.detail-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 15px;
}

/* 搜索栏 */
.search-bar-wrapper {
  margin-bottom: 12px;
  display: flex;
  justify-content: center;
  width: 100%;
}

.seller-info-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  margin-bottom: 12px;
  background: #fff;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.seller-info-bar:active {
  background: #f5f5f5;
  transform: scale(0.98);
}

.seller-info-bar-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.seller-info-bar-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.seller-info-bar .el-rate {
  height: 16px;
}

.seller-info-bar .el-rate__icon {
  font-size: 12px !important;
}

.seller-info-bar .el-rate__text {
  font-size: 11px !important;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  background-color: #fff;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

.search-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

/* 三栏主体布局 */
.detail-main {
  margin-top: 15px;
  margin-bottom: 15px;
  display: flex;
  align-items: stretch;
}

/* 2. 强制左侧 Col 和 Card 填充全部高度 */
.left-image-col {
  display: flex;
}

/* 中间列wrapper */
.middle-info-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

.middle-info-wrapper .ai-analysis-card {
  width: 100%;
}

.middle-info-wrapper .detail-tabs {
  width: 100%;
  flex: 1;
}

/* 左侧卡片锁定高度 */
.fixed-height-card {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
}

/* 去掉 Element Card 默认的内边距，手动控制布局 */
.fixed-height-card :deep(.el-card__body) {
  padding: 12px;
  height: 100%;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

/* 左侧图片区域 */
.image-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

/* 图片容器样式，控制内边距 */
.main-image-wrapper {
  flex: 1;
  /* 占据剩余所有空间 */
  position: relative;
  /* 为绝对定位的图片提供参考 */
  background: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
  min-height: 0;
  /* 确保它可以被 flex 压缩 */
}

.main-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 48px;
  gap: 10px;
}

.sold-out-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.sold-out-text {
  font-size: 48px;
  color: #fff;
  font-weight: 700;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.thumbnail-list {
  height: 80px;
  flex-shrink: 0;
  /* 禁止收缩，确保高度固定 */
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 2px;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  border-radius: 6px;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.thumbnail-item:hover,
.thumbnail-item.active {
  border-color: #409eff;
}

.thumbnail-img {
  width: 100%;
  height: 100%;
}

/* 中间信息区域 */
.info-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-header {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 页面Header样式 */
.page-header-card {
  border-radius: 10px;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border: 1px solid #e4e7ed;
}

.page-header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.header-left {
  flex: 1;
  min-width: 0;
}

.header-right {
  flex-shrink: 0;
  text-align: right;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-title {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 6px 0;
  line-height: 1.3;
  word-break: break-word;
}

.product-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  align-items: center;
}

.header-ai-trigger-btn {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  border: none;
  font-weight: 500;
  padding: 4px 10px !important;
  height: auto !important;
  font-size: 12px;
}

.header-ai-trigger-btn:hover {
  background: linear-gradient(135deg, #5daf34 0%, #67c23a 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
}

.price-section-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.current-price-header {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
  gap: 2px;
}

.price-symbol-header {
  font-size: 20px;
  color: #f56c6c;
  font-weight: 600;
}

.price-value-header {
  font-size: 32px;
  color: #f56c6c;
  font-weight: 700;
}

.original-price-header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
  font-size: 12px;
}

.original-text {
  color: #909399;
}

.original-value {
  color: #909399;
  text-decoration: line-through;
}

.discount-tag-header {
  margin-left: 2px;
}

.header-stats {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
  justify-content: flex-end;
}

.header-stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 2px 6px;
  border-radius: 4px;
  transition: all 0.3s;
  line-height: 1;
}

.header-stat-item:hover {
  transform: translateY(-1px);
}

.header-stat-item .el-icon {
  font-size: 16px;
  display: flex;
  align-items: center;
}

.header-stat-item .stat-label {
  font-size: 11px;
  color: #909399;
  line-height: 16px;
  vertical-align: middle;
}

.header-stat-item .stat-value {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  line-height: 16px;
  vertical-align: middle;
}

.header-stat-item .el-rate {
  display: flex;
  align-items: center;
  gap: 2px;
}

.header-stat-item .el-rate__text {
  font-size: 11px;
}

.stat-label {
  color: #606266;
  font-size: 13px;
}

.stat-value {
  color: #303133;
  font-weight: 600;
  font-size: 14px;
}

@media (max-width: 992px) {
  .page-header-content {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .detail-main {
    flex-direction: column;
  }

  .main-image-wrapper {
    height: 400px;
    /* 移动端给个固定高度 */
    flex: none;
  }

  .header-right {
    width: 100%;
    text-align: left;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }

  .current-price-header {
    justify-content: flex-start;
  }

  .original-price-header {
    justify-content: flex-start;
  }

  .header-stats {
    flex-wrap: nowrap;
    gap: 8px;
    justify-content: flex-start;
  }

  .product-title {
    font-size: 16px;
  }
}

@media (max-width: 768px) {
  .detail-container {
    display: flex;
    flex-direction: column;
  }

  .detail-main {
    display: contents !important;
  }

  .page-header-card,
  .left-image-col,
  .middle-info-col,
  .right-action-col,
  .detail-tabs {
    width: calc(100% - 15px);
    box-sizing: border-box;
    margin-left: auto;
    margin-right: auto;
  }

  .left-image-col,
  .middle-info-col,
  .right-action-col {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }

  .left-image-col {
    order: 1;
    display: block;
    margin-bottom: 12px;
  }

  .page-header-card {
    order: 2;
  }

  .middle-info-col {
    order: 3;
  }

  .right-action-col {
    order: 4;
  }

  .detail-tabs {
    order: 5;
  }

  .image-card.fixed-height-card {
    height: auto;
  }

  .fixed-height-card :deep(.el-card__body) {
    height: auto;
  }

  .image-section {
    height: auto;
  }

  .main-image-wrapper {
    width: 100%;
    height: auto;
    aspect-ratio: 1 / 1;
    margin-bottom: 10px;
    flex: none;
  }

  .thumbnail-list {
    height: 64px;
  }

  .thumbnail-item {
    width: 64px;
    height: 64px;
  }

  .page-header-content {
    gap: 12px;
  }

  .header-right {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .price-section-header {
    flex-direction: row;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
  }

  .current-price-header,
  .original-price-header {
    justify-content: flex-start;
  }

  .header-stats {
    display: flex;
    flex-wrap: nowrap;
    gap: 8px;
    width: 100%;
  }

  .header-stat-item {
    min-width: 0;
  }

  .product-title {
    font-size: 18px;
  }

  .price-value-header {
    font-size: 28px;
  }

  .header-stat-item {
    padding: 3px 6px;
  }
}

@media (max-width: 480px) {
  .price-section-header {
    gap: 8px;
  }

  .original-price-header {
    min-width: 0;
  }

  .discount-tag-header {
    max-width: 140px;
  }

  .header-stat-item .el-rate {
    min-width: 0;
  }
}

.price-section {
  background: linear-gradient(135deg, #fff5f5 0%, #ffffff 100%);
  border: 1px solid #ffd0d0;
  border-radius: 10px;
  padding: 20px;
}

.current-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.price-symbol {
  font-size: 24px;
  color: #f56c6c;
  font-weight: 600;
  margin-right: 4px;
}

.price-value {
  font-size: 36px;
  color: #f56c6c;
  font-weight: 700;
}

.original-price {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
  display: flex;
  align-items: center;
  gap: 8px;
}

.discount-tag {
  margin-left: 8px;
}

/* 快速属性展示 */
.quick-attrs {
  background: #f5f7fa;
  border-radius: 10px;
  padding: 15px;
  margin-top: 15px;
}

.quick-attr-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
}

.quick-attr-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  min-width: 40px;
}

.quick-attr-value {
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.attr-card {
  border-radius: 10px;
}

.attr-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.attr-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.attr-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  flex: 1;
  margin-left: 20px;
}

.progress-text {
  font-size: 12px;
  font-weight: 600;
}

.desc-preview-card {
  border-radius: 10px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
}

.desc-preview-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  max-height: 150px;
  overflow-y: auto;
}

/* AI评估卡片样式 */
.ai-analysis-card {
  border-radius: 10px;
  border: 2px solid #67c23a;
  background: linear-gradient(135deg, #f0f9ff 0%, #ffffff 100%);
}

.ai-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.header-left-content {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #409eff;
}

.header-ai-btn {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border: none;
  font-weight: 500;
}

.header-ai-btn:hover {
  background: linear-gradient(135deg, #3a8ee6 0%, #409eff 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.ai-analysis-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ai-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.ai-label {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 6px;
}

.ai-value {
  font-size: 13px;
  color: #303133;
}

.score-text {
  margin-left: 8px;
  font-weight: 600;
  color: #409eff;
  font-size: 13px;
}

.price-suggestion {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

.analysis-text {
  line-height: 1.5;
  padding: 8px 10px;
  background: #f5f7fa;
  border-radius: 4px;
  border-left: 3px solid #409eff;
  font-size: 12px;
}

.no-ai-data {
  padding: 30px 20px;
  text-align: center;
}

.no-ai-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 20px 0;
}

.ai-analyze-btn {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border: none;
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.25);
}

.ai-analyze-btn:hover {
  background: linear-gradient(135deg, #3a8ee6 0%, #409eff 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.35);
}

.no-ai-text {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.no-ai-tip {
  color: #c0c4cc;
  font-size: 12px;
  margin: 0;
}

/* 商品详情卡片 */
.desc-card {
  border-radius: 10px;
  min-height: 320px;
}

.desc-content {
  padding: 4px 0;
}

.desc-section h4 {
  font-size: 14px;
  color: #303133;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.desc-section p {
  font-size: 13px;
  color: #606266;
  line-height: 1.7;
  margin: 0;
}

/* 右侧操作区域 */
.action-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
  position: sticky;
  top: 20px;
}

.seller-card,
.action-card,
.seller-action-card,
.tips-card {
  border-radius: 10px;
}

.seller-action-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid #e4e7ed;
}

.seller-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid #e4e7ed;
}

.seller-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  padding: 12px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.seller-info:hover {
  background: #f5f7fa;
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.seller-details {
  flex: 1;
}

.seller-name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.seller-rating {
  margin-bottom: 6px;
}

.seller-stats {
  font-size: 12px;
  color: #909399;
}

.stat-item {
  margin-right: 12px;
}

/* ========== 按钮统一样式：宽高 + 间距 全部锁死 ========== */
.btn-wrapper {
  width: 100%;
  height: 44px;
  margin-bottom: 8px !important;
  /* 强制所有按钮上下间距为 8px */
}

.btn-wrapper:last-child {
  margin-bottom: 0 !important;
  /* 最后一个按钮取消底部间距 */
}

.btn-wrapper :deep(.el-button) {
  width: 100% !important;
  height: 100% !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  padding: 0 !important;
  margin: 0 !important;
}

/* 立即购买按钮可以稍高一点，但间距保持一致 */
.buy-btn {
  height: 50px !important;
  font-size: 18px !important;
  font-weight: bold !important;
}

/* 收藏和分享按钮的间距 */
.secondary-actions {
  display: flex;
  flex-direction: column;
  gap: 1px !important;
  /* 强制收藏和分享按钮间距为 8px */
}

/* 卖家卡片内的按钮间距 */
.seller-card .btn-wrapper {
  margin-bottom: 8px !important;
}

/* ===================================================== */

.view-seller-btn,
.contact-btn {
  width: 100%;
  font-weight: 500;
}

.contact-btn {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border: none;
  color: #fff;
}

.contact-btn:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
}

.action-btn {
  width: 100%;
}

.buy-btn {
  background: linear-gradient(135deg, #ff9800 0%, #ff6b00 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
  transition: all 0.3s;
}

.buy-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 152, 0, 0.4);
}

.buy-btn:disabled {
  background: #c0c4cc;
  box-shadow: none;
}

/* 分享Popover */
.share-popover {
  padding: 10px 0;
}

.share-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
  text-align: center;
}

.share-methods {
  display: flex;
  justify-content: space-around;
  gap: 10px;
}

.share-method {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  flex: 1;
}

.share-method:hover {
  background: #f0f7ff;
}

.share-method span {
  font-size: 12px;
  color: #606266;
}

/* 购买须知 */
.tips-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  font-size: 13px;
  color: #606266;
  line-height: 2;
}

/* 详情标签页 */
.detail-tabs {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.tab-content {
  margin-top: 15px;
  border-radius: 8px;
}

.description-content h3 {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 12px 0;
}

.description-content p {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  margin-bottom: 24px;
}

.reviews-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.review-user {
  flex: 1;
}

.review-username {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.review-time {
  font-size: 12px;
  color: #909399;
}

.review-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.more-reviews {
  text-align: center;
  padding: 16px 0;
  border-top: 1px solid #f0f2f5;
  margin-top: 16px;
}

/* 卖家详情抽屉 */
.seller-drawer-content {
  padding: 10px 3px 10px 20px;
}

.seller-drawer-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 24px 0px;
  background: linear-gradient(135deg, #f0f7ff 0%, #e6f0ff 100%);
  border-radius: 12px;
  margin-bottom: 16px;
}

.seller-drawer-avatar {
  display: flex;
  justify-content: center;
}

.seller-drawer-info {
  text-align: center;
  width: 100%;
}

.seller-drawer-name {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.seller-drawer-rating {
  margin-bottom: 8px;
  display: flex;
  justify-content: center;
}

.seller-drawer-stats {
  font-size: 13px;
  color: #606266;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
}

.seller-stats-section {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.stat-card-mini {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px 12px;
  background: #f8f9fa;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  text-align: center;
  transition: all 0.3s;
  min-height: 100px;
}

.stat-card-mini:hover {
  background: #e6f0ff;
  transform: translateY(-2px);
}

.stat-info {
  text-align: center;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
}

.seller-products-section {
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f2f5;
}

.seller-products-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.seller-product-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.seller-product-item:hover {
  background: #e6f0ff;
  transform: translateX(4px);
}

.seller-product-image {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  flex-shrink: 0;
}

.seller-product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.seller-product-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.seller-product-price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: 600;
}

.seller-drawer-actions {
  padding: 0;
}

.seller-drawer-actions .contact-seller-btn {
  width: 100%;
  border-radius: 8px;
}

/* 响应式 */
@media (max-width: 992px) {
  .detail-container {
    padding: 0 12px;
  }

  .image-section,
  .action-section {
    position: static;
  }

  .main-image-wrapper {
    height: 350px;
  }

  .detail-main>.el-col {
    margin-bottom: 15px;
  }
}

@media (max-width: 768px) {
  .main-image-wrapper {
    height: auto;
    aspect-ratio: 1 / 1;
  }

  .product-title {
    font-size: 18px;
  }

  .price-value {
    font-size: 28px;
  }

  .thumbnail-item {
    width: 64px;
    height: 64px;
  }
}

/* 价格范围样式 */
.price-range {
  color: #67c23a;
}

/* 移动端固定底部操作栏 - 默认隐藏 */
.mobile-bottom-actions {
  display: none;
}

/* 移动端显示固定底部栏，隐藏桌面端按钮 */
@media (max-width: 768px) {
  .mobile-hide {
    display: none;
  }

  .desktop-actions {
    display: none;
  }

  .mobile-bottom-actions {
    display: flex;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 56px;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
    z-index: 999;
    justify-content: space-around;
    align-items: center;
    padding: 0 8px;
    padding-bottom: env(safe-area-inset-bottom);
  }

  .mobile-action-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    cursor: pointer;
    padding: 4px 12px;
    border-radius: 8px;
    transition: all 0.2s;
    color: #606266;
    font-size: 11px;
  }

  .mobile-action-item:active {
    background: #f0f0f0;
    transform: scale(0.95);
  }

  .mobile-action-item span {
    line-height: 1;
  }

  .mobile-buy-btn {
    color: #fff;
    background: linear-gradient(135deg, #ff9800, #f57c00);
    border-radius: 20px;
    padding: 8px 20px;
    flex-direction: row;
    gap: 4px;
    font-size: 13px;
    font-weight: 600;
    box-shadow: 0 2px 8px rgba(255, 152, 0, 0.3);
  }

  .mobile-buy-btn:active {
    background: linear-gradient(135deg, #f57c00, #e65100);
    transform: scale(0.95);
  }

  .product-detail-page {
    padding-bottom: calc(56px + env(safe-area-inset-bottom));
  }
}
</style>
