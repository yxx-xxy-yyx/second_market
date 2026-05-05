<template>
  <div class="user-dashboard">
    <div class="dashboard-container">
      <!-- 平台公告轮播条 -->
      <div class="notice-bar-wrapper">
        <el-icon class="notice-icon"><Bell /></el-icon>
        <el-carousel 
          height="40px" 
          direction="vertical" 
          :autoplay="true" 
          :interval="3000" 
          indicator-position="none"
          arrow="never"
          class="notice-carousel"
        >
          <el-carousel-item v-for="(notice, index) in allNotices" :key="index">
            <div class="notice-bar-item" @click="handleNoticeClick(notice)">
              <el-tag size="small" :type="notice.type === 'system' ? 'primary' : 'warning'" class="notice-tag">
                {{ getNoticeTypeLabel(notice.type) }}
              </el-tag>
              <span class="notice-text">{{ locale === 'ko' && notice.titleKo ? notice.titleKo : notice.title }}</span>
              <span class="notice-date">{{ formatNoticeDate(notice.publishTime || notice.date) }}</span>
            </div>
          </el-carousel-item>
        </el-carousel>
        <el-button link class="notice-more-btn" @click="showNoticeDrawer = true">
          {{ $t('common.more') }} <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <!-- 顶部区域：轮播图 + 统计卡片 + 分类快捷入口 -->
      <el-row :gutter="15" class="top-section">
        <!-- 左侧轮播图 60% -->
        <el-col :xs="24" :sm="24" :md="14" :lg="14">
          <div class="carousel-wrapper">
            <el-carousel height="360px" :interval="4000" arrow="hover">
              <el-carousel-item v-for="(item, index) in banners" :key="index">
                <img
                  :src="item.image"
                  :alt="locale === 'ko' ? item.titleKo : item.title"
                  class="carousel-image"
                  @error="handleBannerImgError"
                />
                <div class="carousel-overlay">
                  <h3 class="carousel-title">{{ locale === 'ko' ? item.titleKo : item.title }}</h3>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
        </el-col>

        <!-- 右侧 40%：上部统计 + 下部分类 -->
        <el-col :xs="24" :sm="24" :md="10" :lg="10">
          <!-- 统计卡片 -->
          <el-row :gutter="10" class="stats-wrapper">
            <el-col :span="12" v-for="(stat, index) in stats" :key="index">
              <div class="stat-card-mini" @click="handleStatClick(stat.route)">
                <div class="stat-icon-mini" :style="{ background: stat.color }">
                  <el-icon :size="20"><component :is="stat.icon" /></el-icon>
                </div>
                <div class="stat-info-mini">
                  <div class="stat-value-mini">{{ stat.value }}</div>
                  <div class="stat-label-mini">{{ stat.label }}</div>
                </div>
              </div>
            </el-col>
          </el-row>

          <!-- 分类快捷入口 -->
          <div class="quick-categories">
            <div class="quick-category-title">{{ $t('dashboard.quickCategory') }}</div>
            <CategoryGrid :items="categories" :columns="4" :gap="10" :icon-size="22" @select="handleCategoryClick" />
          </div>
        </el-col>
      </el-row>

      <!-- 中部区域：热门商品横向滚动 + 实时交易动态 -->
      <el-row :gutter="15" class="middle-section">
        <!-- AI 智能推荐栏 -->
        <el-col :span="24" style="margin-bottom: 20px;">
          <div class="ai-recommend-bar" @click="toggleAiAssistant">
            <div class="ai-bar-content">
              <el-icon class="ai-bar-icon"><MagicStick /></el-icon>
              <div class="ai-bar-text">
                <span class="ai-bar-title">{{ $t('dashboard.aiTitle') }}</span>
                <span class="ai-bar-desc">{{ $t('dashboard.aiDesc') }}</span>
              </div>
            </div>
            <el-button type="primary" size="small" round class="ai-bar-btn">{{ $t('dashboard.aiBtn') }}</el-button>
          </div>
        </el-col>

        <!-- 热门商品横向滚动 70% -->
        <el-col :xs="24" :sm="24" :md="17" :lg="17">
          <div class="hot-products-section">
            <div class="section-header">
              <div class="header-left">
                <el-icon class="title-icon" color="#ff6b6b" :size="24"><TrendCharts /></el-icon>
                <h2 class="section-title">{{ $t('dashboard.hotProducts') }}</h2>
              </div>
              <el-link type="primary" @click="handleViewMore('hot')">{{ $t('dashboard.viewMore') }}</el-link>
            </div>
            <div class="horizontal-scroll-container" v-loading="loading">
              <div class="horizontal-scroll-content">
                <div 
                  v-for="product in hotProducts.slice(0, 10)" 
                  :key="product.id"
                  class="horizontal-product-card"
                  @click="handleProductClick(product.id)"
                >
                  <div class="horizontal-product-image">
                    <el-image :src="getProductImage(product.images)" fit="cover" />
                    <div class="horizontal-product-badge" v-if="product.aiAnalyzed">
                      <el-tag size="small" type="success" effect="dark">AI</el-tag>
                    </div>
                  </div>
                  <div class="horizontal-product-info">
                    <div class="horizontal-product-title">{{ locale === 'ko' && product.titleKo ? product.titleKo : product.title }}</div>
                    <div class="horizontal-product-price">¥{{ product.price }}</div>
                    <div class="horizontal-product-meta">
                      <el-icon :size="14"><View /></el-icon>
                      <span>{{ product.viewCount }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 实时交易动态 30% -->
        <el-col :xs="24" :sm="24" :md="7" :lg="7">
          <div class="trade-dynamic-section">
            <div class="section-header">
              <div class="header-left">
                <el-icon class="title-icon" color="#67c23a" :size="20"><Clock /></el-icon>
                <h3 class="section-title-small">{{ $t('dashboard.tradeDynamic') }}</h3>
              </div>
            </div>
            <div class="trade-list">
              <div 
                v-for="(trade, index) in recentTrades" 
                :key="index"
                class="trade-item"
              >
                <el-popover
                  placement="left"
                  :width="200"
                  trigger="hover"
                >
                  <template #reference>
                    <div class="trade-content">
                      <el-avatar :size="32" :src="formatAvatarUrl(trade.avatar)">
                        <el-icon><User /></el-icon>
                      </el-avatar>
                      <div class="trade-info">
                        <div class="trade-text">{{ trade.username }} {{ locale === 'zh' ? '购买了' : '구매했습니다' }}</div>
                        <div class="trade-product">{{ trade.productName }}</div>
                        <div class="trade-time">{{ trade.time }}</div>
                      </div>
                    </div>
                  </template>
                  <div class="trade-popover-content">
                    <div class="popover-title">{{ $t('order.orderDetail') }}</div>
                    <div class="popover-item">
                      <span class="popover-label">{{ $t('order.productInfo') }}：</span>
                      <span class="popover-value">{{ trade.productName }}</span>
                    </div>
                    <div class="popover-item">
                      <span class="popover-label">{{ $t('product.price') }}：</span>
                      <span class="popover-value price">¥{{ trade.price }}</span>
                    </div>
                    <div class="popover-item">
                      <span class="popover-label">{{ $t('order.orderTime') }}：</span>
                      <span class="popover-value">{{ trade.time }}</span>
                    </div>
                  </div>
                </el-popover>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 底部区域：推荐商品网格 -->
      <el-row :gutter="15" class="bottom-section">
        <!-- 推荐商品网格 100% (占据全部宽度) -->
        <el-col :span="24">
          <div class="recommend-section">
            <div class="section-header">
              <div class="header-left">
                <el-icon class="title-icon" color="#409eff" :size="24"><Goods /></el-icon>
                <h2 class="section-title">{{ $t('dashboard.recommend') }}</h2>
              </div>
              <el-link type="primary" @click="handleViewMore('new')">{{ $t('dashboard.viewMore') }}</el-link>
            </div>
            <el-row :gutter="12" v-loading="loading">
              <el-col 
                v-for="product in recommendProducts.slice(0, 12)" 
                :key="product.id"
                :xs="12" 
                :sm="8" 
                :md="6" 
                :lg="4"
              >
                <div class="product-card-compact" @click="handleProductClick(product.id)">
                  <div class="product-image-wrapper-compact">
                    <el-image :src="getProductImage(product.images)" fit="cover" class="product-image-compact" />
                    <div class="product-badge-compact" v-if="product.aiAnalyzed">
                      <el-tag size="small" type="success">AI</el-tag>
                    </div>
                    <div class="favorite-btn-compact" @click.stop="handleFavorite(product.id)">
                      <el-icon :size="16" color="#f56c6c"><Star /></el-icon>
                    </div>
                  </div>
                  <div class="product-info-compact">
                    <div class="product-title-compact">{{ locale === 'ko' && product.titleKo ? product.titleKo : product.title }}</div>
                    <div class="product-price-compact">¥{{ product.price }}</div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 公告详情抽屉 -->
    <el-drawer
      v-model="showNoticeDrawer"
      :title="$t('dashboard.notice')"
      direction="rtl"
      size="450px"
    >
      <div class="notice-drawer-content">
        <div 
          v-for="(notice, index) in allNotices" 
          :key="index"
          class="notice-drawer-item"
          @click="handleNoticeDetailClick(notice)"
        >
          <div class="notice-drawer-header">
            <el-tag :type="notice.type === 'system' ? 'primary' : notice.type === 'activity' ? 'success' : 'warning'" size="small">
              {{ notice.typeLabel }}
            </el-tag>
            <span class="notice-drawer-date">{{ notice.date }}</span>
          </div>
          <div class="notice-drawer-title">{{ notice.title }}</div>
          <div class="notice-drawer-summary">{{ notice.summary }}</div>
        </div>
      </div>
    </el-drawer>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="showNoticeDetail"
      :title="selectedNotice?.title"
      width="600px"
    >
      <div class="notice-detail-content" v-if="selectedNotice">
        <div class="notice-detail-meta">
          <el-tag :type="selectedNotice.type === 'system' ? 'primary' : selectedNotice.type === 'activity' ? 'success' : 'warning'">
            {{ selectedNotice.typeLabel }}
          </el-tag>
          <span class="notice-detail-date">{{ selectedNotice.date }}</span>
        </div>
        <div class="notice-detail-text">{{ selectedNotice.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '@/api/product'
import { favoriteApi } from '@/api/favorite'
import { userStatisticsApi } from '@/api/statistics'
import { noticeApi } from '@/api/admin'
import { useUserStore } from '@/stores/user'
import { useSchoolStore } from '@/stores/school'
import { ElMessage } from 'element-plus'
import { formatAvatarUrl, formatImageUrl } from '@/utils/url'
import { CATEGORIES } from '@/utils/categories'
import CategoryGrid from '@/components/category/CategoryGrid.vue'
import { useI18n } from 'vue-i18n'
import {
  Star,
  View,
  TrendCharts,
  Goods,
  ShoppingBag,
  ShoppingCart,
  User,
  Clock,
  Bell,
  MagicStick,
  ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const schoolStore = useSchoolStore()
const { t, locale } = useI18n()
const loading = ref(false)
const showNoticeDrawer = ref(false)
const showNoticeDetail = ref(false)
const selectedNotice = ref(null)

const getNoticeTypeLabel = (type) => {
  const labels = {
    system: locale.value === 'ko' ? '시스템' : locale.value === 'en' ? 'System' : '系统',
    activity: locale.value === 'ko' ? '이벤트' : locale.value === 'en' ? 'Activity' : '活动',
    maintenance: locale.value === 'ko' ? '점검' : locale.value === 'en' ? 'Maintenance' : '维护'
  }
  return labels[type] || type
}

const formatNoticeDate = (date) => {
  if (!date) return ''
  if (typeof date === 'string' && date.includes('前')) {
    if (locale.value === 'ko') {
      return date.replace('小时前', '시간 전').replace('分钟前', '분 전').replace('天前', '일 전')
    }
    return date
  }
  // 如果是日期对象或ISO字符串
  const d = new Date(date)
  if (isNaN(d.getTime())) return date
  
  const now = new Date()
  const diff = now - d
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 60) return locale.value === 'ko' ? `${minutes}분 전` : `${minutes}分钟前`
  if (hours < 24) return locale.value === 'ko' ? `${hours}시간 전` : `${hours}小时前`
  if (days < 7) return locale.value === 'ko' ? `${days}일 전` : `${days}天前`

  return d.toLocaleDateString(locale.value === 'ko' ? 'ko-KR' : 'zh-CN')
}

const toggleAiAssistant = () => {
  window.dispatchEvent(new CustomEvent('toggle-ai-assistant'))
}

const BANNER_FALLBACK =
  "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='1200' height='400'><defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'><stop offset='0' stop-color='%2306b6d4'/><stop offset='1' stop-color='%238b5cf6'/></linearGradient></defs><rect width='1200' height='400' fill='url(%23g)'/><text x='60' y='210' fill='white' font-family='Arial' font-size='44' font-weight='700'>SecondMarket</text><text x='60' y='270' fill='white' font-family='Arial' font-size='22' opacity='0.9'>Campus second-hand marketplace</text></svg>"

const handleBannerImgError = (e) => {
  if (e?.target) e.target.src = BANNER_FALLBACK
}

const banners = [
  {
    image: BANNER_FALLBACK,
    title: 'AI智能识别 让交易更放心',
    titleKo: 'AI 스마트 인식으로 더 안심할 수 있는 거래'
  },
  {
    image: BANNER_FALLBACK,
    title: '优质二手商品 物超所值',
    titleKo: '가성비 최고의 프리미엄 중고 상품'
  },
  {
    image: BANNER_FALLBACK,
    title: '安全交易保障 售后无忧',
    titleKo: '안전 거래 보장 및 확실한 사후 관리'
  }
]

const stats = ref([
  { label: t('common.publish'), value: '0', icon: ShoppingBag, color: 'linear-gradient(135deg, #409eff 0%, #66b1ff 100%)', route: '/user/publish' },
  { label: t('common.myOrders'), value: '0', icon: ShoppingCart, color: 'linear-gradient(135deg, #ff9800 0%, #ffc107 100%)', route: '/user/orders' },
  { label: t('common.myFavorites'), value: '0', icon: Star, color: 'linear-gradient(135deg, #67c23a 0%, #85ce61 100%)', route: '/user/favorites' },
  { label: t('common.browseHistory'), value: '0', icon: View, color: 'linear-gradient(135deg, #4facfe 0%, #00d4ff 100%)', route: '/user/history' }
])

const categories = CATEGORIES

const recentTrades = ref([
  { username: '张同学', productName: 'iPhone 13 Pro', price: 4999, time: '2分钟前', avatar: '' },
  { username: '李同学', productName: '小米手环7', price: 199, time: '5分钟前', avatar: '' },
  { username: '王同学', productName: '机械键盘', price: 299, time: '8分钟前', avatar: '' },
  { username: '赵同学', productName: '运动鞋', price: 399, time: '12分钟前', avatar: '' },
  { username: '刘同学', productName: '专业相机', price: 2999, time: '15分钟前', avatar: '' },
  { username: '陈同学', productName: '蓝牙耳机', price: 399, time: '20分钟前', avatar: '' }
])

const notices = ref([
  { type: 'system', typeLabel: t('common.system'), title: '平台用户协议更新通知', date: '2小时前', summary: '为了更好地保护您的权益...', content: '尊敬的用户，我们对《用户服务协议》进行了更新...' },
  { type: 'activity', typeLabel: t('common.activity'), title: '新春促销活动火热进行中', date: '5小时前', summary: '全场商品9折起，满减优惠...', content: '亲爱的用户，新春佳节来临之际...' },
  { type: 'maintenance', typeLabel: t('common.maintenance'), title: '系统维护升级通知', date: '10小时前', summary: '系统将于凌晨2:00-6:00维护...', content: '为了提升系统性能和用户体验...' }
])

const allNotices = ref([
  { type: 'system', typeLabel: '系统', title: '平台用户协议更新通知', date: '2小时前', summary: '为了更好地保护您的权益...', content: '尊敬的用户，我们对《用户服务协议》进行了更新，主要更新内容包括：优化了用户隐私保护条款、明确了交易纠纷处理流程、新增了平台责任免责声明、更新了违规行为处理规则。' },
  { type: 'activity', typeLabel: '活动', title: '新春促销活动火热进行中', date: '5小时前', summary: '全场商品9折起，满减优惠...', content: '亲爱的用户，新春佳节来临之际，平台推出多款优惠商品！活动时间：1月20日-2月10日。全场商品9折起，满100减20，满200减50，每日限时秒杀低至5折！' },
  { type: 'maintenance', typeLabel: '维护', title: '系统维护升级通知', date: '10小时前', summary: '系统将于凌晨2:00-6:00维护...', content: '为了提升系统性能和用户体验，我们将对平台进行维护升级。维护时间：1月13日凌晨2:00-6:00。维护期间平台将暂停服务，给您带来不便，敬请谅解。' },
  { type: 'system', typeLabel: '系统', title: '平台功能升级完成', date: '1天前', summary: 'AI智能推荐等新功能上线...', content: '平台功能升级已顺利完成！新增功能：AI智能推荐、商品评价系统、消息通知中心、收藏夹功能。期待您体验全新的平台功能！' },
  { type: 'system', typeLabel: '系统', title: '诚信交易倡议书', date: '2天前', summary: '共建诚信交易环境...', content: '为营造诚信、和谐的交易环境，我们倡议：如实描述商品信息、按时发货保证质量、文明交流礼貌待人、遵守平台规则、理性解决纠纷。' }
])

const hotProducts = ref([])
const recommendProducts = ref([])

const getProductImage = (images) => {
  if (!images) return 'https://via.placeholder.com/300x200?text=No+Image'
  try {
    const imageArray = typeof images === 'string' ? JSON.parse(images) : images
    const firstImage = imageArray[0]
    if (!firstImage) return 'https://via.placeholder.com/300x200?text=No+Image'
    
    // 使用formatImageUrl处理图片路径
    return formatImageUrl(firstImage)
  } catch {
    return 'https://via.placeholder.com/300x200?text=No+Image'
  }
}

const fetchHotProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: 1,
      pageSize: 20,
      status: 2
    }
    if (schoolStore.selectedSchool) {
      params.schoolId = Number(schoolStore.selectedSchool)
    }
    const res = await productApi.getProductList(params)
    if (res.code === '200' || res.success) {
      hotProducts.value = res.data.records.sort((a, b) => b.viewCount - a.viewCount)
    }
  } catch {
  } finally {
    loading.value = false
  }
}

const fetchRecommendProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: 1,
      pageSize: 12,
      status: 2
    }
    if (schoolStore.selectedSchool) {
      params.schoolId = Number(schoolStore.selectedSchool)
    }
    const res = await productApi.getProductList(params)
    if (res.code === '200' || res.success) {
      recommendProducts.value = res.data.records
    }
  } catch {
  } finally {
    loading.value = false
  }
}

watch(
  () => schoolStore.selectedSchool,
  () => {
    fetchHotProducts()
    fetchRecommendProducts()
  }
)

const fetchUserStatistics = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await userStatisticsApi.getOverview()
    if (res.code === '200' || res.success) {
      const data = res.data
      stats.value[0].value = data.publishedProductCount || 0
      stats.value[1].value = data.purchaseOrderCount || 0
      stats.value[2].value = data.favoriteCount || 0
      stats.value[3].value = '0' // 浏览记录暂时设为0，后续可添加相应字段
    }
  } catch {
  }
}

const handleStatClick = (route) => {
  if (route) {
    router.push(route)
  }
}

const handleCategoryClick = (categoryId) => {
  router.push({
    path: '/user/products',
    query: { category: categoryId }
  })
}

const handleProductClick = (productId) => {
  router.push(`/user/product/${productId}`)
}

const handleFavorite = async (productId) => {
  try {
    const res = await favoriteApi.addFavorite(productId)
    if (res.code === '200') {
      ElMessage.success(t('common.favoriteSuccess'))
    }
  } catch (error) {
    ElMessage.error(error.message || t('common.favoriteFail'))
  }
}

const handleViewMore = (type) => {
  router.push({
    path: '/user/products',
    query: { sort: type }
  })
}

const handleNoticeClick = (notice) => {
  selectedNotice.value = notice
  showNoticeDetail.value = true
}

const handleNoticeDetailClick = (notice) => {
  selectedNotice.value = notice
  showNoticeDrawer.value = false
  showNoticeDetail.value = true
}

const fetchNotices = async () => {
  try {
    const res = await noticeApi.getList(1, 10, 1) // 状态为1表示已发布
    if (res.code === '200') {
      const records = res.data.records || []
      if (records.length > 0) {
        allNotices.value = records.map(notice => ({
          type: notice.type === 0 ? 'system' : notice.type === 1 ? 'activity' : 'maintenance',
          typeLabel: notice.type === 0 ? (locale.value === 'zh' ? '系统' : '시스템') : 
                     notice.type === 1 ? (locale.value === 'zh' ? '活动' : '활동') : 
                     (locale.value === 'zh' ? '维护' : '유지보수'),
          title: locale.value === 'ko' && notice.titleKo ? notice.titleKo : notice.title,
          content: locale.value === 'ko' && notice.contentKo ? notice.contentKo : notice.content,
          date: formatTime(notice.publishTime || notice.createTime),
          summary: (locale.value === 'ko' && notice.contentKo ? notice.contentKo : notice.content).replace(/<[^>]+>/g, '').substring(0, 50) + '...'
        }))
      }
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) {
    return locale.value === 'zh' ? '刚刚' : '방금 전'
  } else if (diff < 3600000) {
    return locale.value === 'zh' ? `${Math.floor(diff / 60000)}分钟前` : `${Math.floor(diff / 60000)}분 전`
  } else if (diff < 86400000) {
    return locale.value === 'zh' ? `${Math.floor(diff / 3600000)}小时前` : `${Math.floor(diff / 3600000)}시간 전`
  } else if (diff < 604800000) {
    return locale.value === 'zh' ? `${Math.floor(diff / 86400000)}天前` : `${Math.floor(diff / 86400000)}일 전`
  } else {
    return date.toLocaleDateString(locale.value === 'zh' ? 'zh-CN' : 'ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  }
}

onMounted(() => {
  fetchHotProducts()
  fetchRecommendProducts()
  fetchUserStatistics()
  fetchNotices()
})
</script>

<style scoped>
.user-dashboard {
  min-height: calc(100vh - 110px);
  background: #f0f2f5;
  padding: 15px 0;
}

.dashboard-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 15px;
}

.notice-bar-wrapper {
  background: #fff;
  height: 40px;
  margin-bottom: 15px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.notice-icon {
  color: #ff9800;
  font-size: 18px;
  margin-right: 10px;
  flex-shrink: 0;
}

.notice-carousel {
  flex: 1;
  min-width: 0;
}

.notice-bar-item {
  height: 40px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: color 0.3s;
}

.notice-bar-item:hover .notice-text {
  color: #409eff;
}

.notice-tag {
  flex-shrink: 0;
}

.notice-text {
  flex: 1;
  font-size: 13px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-date {
  font-size: 11px;
  color: #909399;
  flex-shrink: 0;
  margin-left: 10px;
}

.notice-more-btn {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
  flex-shrink: 0;
}

.ai-recommend-bar {
  background: linear-gradient(90deg, #ecf5ff 0%, #ffffff 100%);
  border: 1px solid #d9ecff;
  border-radius: 12px;
  padding: 15px 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
}

.ai-recommend-bar:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.2);
  border-color: #409eff;
}

.ai-bar-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.ai-bar-icon {
  font-size: 28px;
  color: #409eff;
  background: #fff;
  padding: 8px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.ai-bar-text {
  display: flex;
  flex-direction: column;
}

.ai-bar-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 2px;
}

.ai-bar-desc {
  font-size: 13px;
  color: #606266;
}

@media screen and (max-width: 768px) {
  .ai-bar-desc {
    display: none;
  }
  .ai-bar-title {
    font-size: 14px;
  }
}

/* 顶部区域 */
.top-section {
  margin-bottom: 15px;
}

.carousel-wrapper {
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  height: 360px;
  position: relative;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(to top, rgba(0,0,0,0.7), transparent);
}

.carousel-title {
  color: #fff;
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
}

@media screen and (max-width: 768px) {
  .carousel-wrapper {
    height: 240px;
  }

  .carousel-overlay {
    padding: 14px;
  }

  .carousel-title {
    font-size: 16px;
  }
}

/* 统计卡片紧凑版 */
.stats-wrapper {
  margin-bottom: 10px;
}

.stat-card-mini {
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  height: 70px;
  margin-bottom: 10px; /* 调整上下间距 */
}

.stat-card-mini:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon-mini {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info-mini {
  flex: 1;
  min-width: 0;
}

.stat-value-mini {
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label-mini {
  font-size: 12px;
  color: #909399;
}

/* 快速分类 */
.quick-categories {
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  height: 190px;
}

.quick-category-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 10px;
}

.quick-category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 10px 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-category-item:hover {
  background: #f0f7ff;
  color: #409eff;
}

.category-name-mini {
  font-size: 11px;
  text-align: center;
  line-height: 1.2;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.category-zh {
  font-weight: 500;
  color: #303133;
}

.category-ko {
  font-size: 9px;
  color: #909399;
  transform: scale(0.85);
  margin-top: -2px;
}

/* 中部区域 */
.middle-section {
  margin-bottom: 15px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding: 0 4px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.section-title-small {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.title-icon {
  flex-shrink: 0;
}

/* 热门商品横向滚动 */
.hot-products-section {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.horizontal-scroll-container {
  position: relative;
  min-height: 220px;
}

.horizontal-scroll-content {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 10px;
  scrollbar-width: thin;
}

.horizontal-scroll-content::-webkit-scrollbar {
  height: 6px;
}

.horizontal-scroll-content::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

.horizontal-product-card {
  flex-shrink: 0;
  width: 160px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.horizontal-product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  border-color: #409eff;
}

.horizontal-product-image {
  position: relative;
  width: 100%;
  height: 160px;
  overflow: hidden;
}

.horizontal-product-image .el-image {
  width: 100%;
  height: 100%;
}

.horizontal-product-badge {
  position: absolute;
  top: 6px;
  left: 6px;
  z-index: 2;
}

.product-badge-compact {
  position: absolute;
  top: 6px;
  left: 6px;
  z-index: 2;
}

.horizontal-product-info {
  padding: 10px;
}

.horizontal-product-title {
  font-size: 13px;
  color: #303133;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 36px;
  line-height: 18px;
}

.horizontal-product-price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: 600;
  margin-bottom: 6px;
}

.horizontal-product-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

/* 实时交易动态 */
.trade-dynamic-section {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  height: 100%;
}

.trade-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 275px;
  overflow-y: auto;
  scrollbar-width: thin;
}

.trade-item {
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
  transition: all 0.3s;
}

.trade-item:hover {
  background: #e6f0ff;
}

.trade-content {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.trade-info {
  flex: 1;
  min-width: 0;
}

.trade-text {
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.trade-product {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin: 2px 0;
}

.trade-time {
  font-size: 11px;
  color: #909399;
}

.trade-popover-content {
  padding: 4px 0;
}

.popover-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.popover-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 13px;
}

.popover-label {
  color: #909399;
}

.popover-value {
  color: #303133;
  font-weight: 500;
}

.popover-value.price {
  color: #f56c6c;
  font-weight: 600;
}

/* 底部区域 */
.bottom-section {
  margin-bottom: 15px;
}

.recommend-section {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.product-card-compact {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 12px;
}

.product-card-compact:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  border-color: #409eff;
}

.product-image-wrapper-compact {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
  background: #f5f7fa;
}

.product-image-compact {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.favorite-btn-compact {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 28px;
  height: 28px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 2;
}

.favorite-btn-compact:hover {
  background: #fff;
  transform: scale(1.1);
}

.product-info-compact {
  padding: 10px;
}

.product-title-compact {
  font-size: 13px;
  color: #303133;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 36px;
  line-height: 18px;
}

.product-price-compact {
  font-size: 16px;
  color: #f56c6c;
  font-weight: 600;
}

/* 公告抽屉 */
.notice-drawer-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-drawer-item {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.notice-drawer-item:hover {
  background: #e6f0ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.notice-drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notice-drawer-date {
  font-size: 12px;
  color: #909399;
}

.notice-drawer-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.notice-drawer-summary {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

/* 公告详情对话框 */
.notice-detail-content {
  padding: 10px 0;
}

.notice-detail-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.notice-detail-date {
  font-size: 13px;
  color: #909399;
}

.notice-detail-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}

/* 响应式 */
@media (max-width: 992px) {
  .dashboard-container {
    padding: 0 12px;
  }
  
  .top-section,
  .middle-section,
  .bottom-section {
    margin-bottom: 12px;
  }

  .carousel-wrapper {
    height: 300px;
  }

  .stats-wrapper {
    margin-top: 12px;
  }

  .quick-categories {
    margin-top: 10px;
  }

  .trade-dynamic-section,
  .notice-sidebar {
    margin-top: 12px;
  }
}

@media (max-width: 768px) {
  .carousel-title {
    font-size: 18px;
  }

  .section-title {
    font-size: 18px;
  }

  .horizontal-product-card {
    width: 140px;
  }

  .horizontal-product-image {
    height: 140px;
  }
}
</style>
