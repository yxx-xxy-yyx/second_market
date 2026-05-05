<template>
  <div class="user-dashboard">
    <div class="dashboard-container">
      <!-- 平台公告轮播条 - APP风格 -->
      <div class="notice-bar-wrapper">
        <el-icon class="notice-icon"><Bell /></el-icon>
        <el-carousel 
          height="44px" 
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

      <!-- 顶部区域：轮播图 + 统计卡片 + 分类快捷入口 - APP风格 -->
      <el-row :gutter="10" class="top-section">
        <!-- 左侧轮播图 - APP沉浸式 -->
        <el-col :xs="24" :sm="24" :md="14" :lg="14">
          <div class="carousel-wrapper">
            <el-carousel height="180px" :interval="4000" arrow="never" indicator-position="bottom">
              <el-carousel-item v-for="(item, index) in banners" :key="index">
                <img :src="item.image" :alt="locale === 'ko' ? item.titleKo : item.title" class="carousel-image" />
                <div class="carousel-overlay">
                  <h3 class="carousel-title">{{ locale === 'ko' ? item.titleKo : item.title }}</h3>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
        </el-col>

        <!-- 右侧：上部统计 + 下部分类 - APP卡片风格 -->
        <el-col :xs="24" :sm="24" :md="10" :lg="10">
          <!-- 统计卡片 - APP风格 -->
          <el-row :gutter="8" class="stats-wrapper">
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

          <!-- 分类快捷入口 - APP网格风格 -->
          <div class="quick-categories">
            <el-row :gutter="4">
              <el-col :span="6" v-for="category in categories.slice(0, 8)" :key="category.id">
                <div class="quick-category-item" @click="handleCategoryClick(category.id)">
                  <div class="category-icon-wrapper">
                    <CategoryIcon :category-id="category.id" :size="28" />
                  </div>
                  <span class="category-name-mini">{{ $t(`categories.${category.id}`) }}</span>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>

      <!-- 中部区域：AI推荐 + 热门商品 + 实时交易 - APP风格 -->
      <el-row :gutter="10" class="middle-section">
        <!-- AI 智能推荐栏 - APP风格 -->
        <el-col :span="24" style="margin-bottom: 10px;">
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

        <!-- 热门商品横向滚动 - APP风格 -->
        <el-col :xs="24" :sm="24" :md="17" :lg="17">
          <div class="hot-products-section">
            <div class="section-header">
              <div class="header-left">
                <el-icon class="title-icon" color="#ff6b6b" :size="20"><TrendCharts /></el-icon>
                <h2 class="section-title">{{ $t('dashboard.hotProducts') }}</h2>
              </div>
              <el-link type="primary" @click="handleViewMore('hot')" class="view-more-link">
                {{ $t('dashboard.viewMore') }} <el-icon size="12"><ArrowRight /></el-icon>
              </el-link>
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
                      <el-icon :size="12"><View /></el-icon>
                      <span>{{ product.viewCount }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 实时交易动态 - APP风格 -->
        <el-col :xs="24" :sm="24" :md="7" :lg="7">
          <div class="trade-dynamic-section">
            <div class="section-header">
              <div class="header-left">
                <el-icon class="title-icon" color="#67c23a" :size="18"><Clock /></el-icon>
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
                      <el-avatar :size="28" :src="formatAvatarUrl(trade.avatar)" class="trade-avatar">
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

      <!-- 底部区域：推荐商品网格 - APP风格 -->
      <el-row :gutter="8" class="bottom-section">
        <!-- 推荐商品网格 -->
        <el-col :span="24">
          <div class="recommend-section">
            <div class="section-header">
              <div class="header-left">
                <el-icon class="title-icon" color="#409eff" :size="20"><Goods /></el-icon>
                <h2 class="section-title">{{ $t('dashboard.recommend') }}</h2>
              </div>
              <el-link type="primary" @click="handleViewMore('new')" class="view-more-link">
                {{ $t('dashboard.viewMore') }} <el-icon size="12"><ArrowRight /></el-icon>
              </el-link>
            </div>
            <el-row :gutter="8" v-loading="loading">
              <el-col 
                v-for="product in recommendProducts.slice(0, 12)" 
                :key="product.id"
                :xs="6" 
                :sm="6" 
                :md="6" 
                :lg="6"
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

    <!-- 公告详情抽屉 - APP风格 -->
    <el-drawer
      v-model="showNoticeDrawer"
      :title="$t('dashboard.notice')"
      direction="rtl"
      size="85%"
      :withHeader="true"
      class="app-drawer"
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

    <!-- 公告详情对话框 - APP风格 -->
    <el-dialog
      v-model="showNoticeDetail"
      :title="selectedNotice?.title"
      width="90%"
      class="app-dialog"
      :border-radius="16"
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '@/api/product'
import { favoriteApi } from '@/api/favorite'
import { userStatisticsApi } from '@/api/statistics'
import { noticeApi } from '@/api/admin'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { formatAvatarUrl, formatImageUrl } from '@/utils/url'
import { CATEGORIES } from '@/utils/categories'
import CategoryIcon from '@/components/icons/CategoryIcon.vue'
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
    const res = await productApi.getProductList({
      pageNum: 1,
      pageSize: 20,
      status: 2
    })
    if (res.code === '200') {
      hotProducts.value = res.data.records.sort((a, b) => b.viewCount - a.viewCount)
    }
  } catch (error) {
    console.error('获取热门商品失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchRecommendProducts = async () => {
  loading.value = true
  try {
    const res = await productApi.getProductList({
      pageNum: 1,
      pageSize: 12,
      status: 2
    })
    if (res.code === '200') {
      recommendProducts.value = res.data.records
    }
  } catch (error) {
    console.error('获取推荐商品失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchUserStatistics = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await userStatisticsApi.getOverview()
    if (res.code === '200') {
      const data = res.data
      stats.value[0].value = data.publishedProductCount || 0
      stats.value[1].value = data.purchaseOrderCount || 0
      stats.value[2].value = data.favoriteCount || 0
      stats.value[3].value = '0' // 浏览记录暂时设为0，后续可添加相应字段
    }
  } catch (error) {
    console.error('获取用户统计数据失败:', error)
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
/* 基础样式 - APP风格重置 */
.user-dashboard {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 0;
  margin: 0;
}

.dashboard-container {
  max-width: 100%;
  margin: 0 auto;
  padding: 0;
}

/* 公告栏 - APP风格 */
.notice-bar-wrapper {
  background: #fff;
  height: 44px;
  margin-bottom: 8px;
  border-radius: 0;
  display: flex;
  align-items: center;
  padding: 0 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  position: sticky;
  top: 0;
  z-index: 10;
}

.notice-icon {
  color: #ff672b;
  font-size: 18px;
  margin-right: 8px;
  flex-shrink: 0;
}

.notice-carousel {
  flex: 1;
  min-width: 0;
}

.notice-bar-item {
  height: 44px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: color 0.2s;
}

.notice-tag {
  flex-shrink: 0;
  border-radius: 4px;
}

.notice-text {
  flex: 1;
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-date {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
  margin-left: 8px;
}

.notice-more-btn {
  font-size: 12px;
  color: #666;
  margin-left: 8px;
  flex-shrink: 0;
}

/* AI推荐栏 - APP风格 */
.ai-recommend-bar {
  background: #fff;
  border: none;
  border-radius: 12px;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin: 0 12px 12px;
}

.ai-recommend-bar:hover {
  transform: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border-color: transparent;
}

.ai-bar-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-bar-icon {
  font-size: 24px;
  color: #ff672b;
  background: #fff;
  padding: 8px;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(255, 103, 43, 0.15);
}

.ai-bar-text {
  display: flex;
  flex-direction: column;
}

.ai-bar-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.ai-bar-desc {
  font-size: 12px;
  color: #999;
}

.ai-bar-btn {
  background: #ff672b;
  border: none;
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 13px;
}

/* 顶部区域 - APP风格 */
.top-section {
  margin-bottom: 8px;
  padding: 0 12px;
}

.carousel-wrapper {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: none;
  height: 180px;
  position: relative;
  margin-bottom: 8px;
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
  padding: 16px;
  background: linear-gradient(to top, rgba(0,0,0,0.6), transparent);
}

.carousel-title {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  text-shadow: 1px 1px 3px rgba(0,0,0,0.5);
}

/* 统计卡片 - APP风格 */
.stats-wrapper {
  margin-bottom: 8px;
  display: flex;
  flex-wrap: wrap;
}

.stat-card-mini {
  background: #fff;
  border-radius: 12px;
  padding: 16px 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  height: 80px;
  margin-bottom: 8px;
}

.stat-card-mini:hover {
  transform: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-icon-mini {
  width: 44px;
  height: 44px;
  border-radius: 50%;
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
  font-size: 22px;
  font-weight: 700;
  color: #333;
  line-height: 1;
  margin-bottom: 2px;
}

.stat-label-mini {
  font-size: 12px;
  color: #999;
}

/* 快速分类 - APP风格 */
.quick-categories {
  background: #fff;
  border-radius: 12px;
  padding: 16px 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  height: auto;
}

.quick-category-title {
  display: none;
}

.quick-category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 4px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-category-item:hover {
  background: #f8f8f8;
  color: #ff672b;
}

.category-icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.category-name-mini {
  font-size: 12px;
  text-align: center;
  line-height: 1.2;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  color: #666;
}

/* 中部区域 - APP风格 */
.middle-section {
  margin-bottom: 8px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  padding: 0 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 6px;
}

.section-title {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.section-title-small {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.title-icon {
  flex-shrink: 0;
}

.view-more-link {
  font-size: 12px;
  color: #999;
}

/* 热门商品横向滚动 - APP风格 */
.hot-products-section {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  margin: 0 12px 8px;
}

.horizontal-scroll-container {
  position: relative;
  min-height: 200px;
}

.horizontal-scroll-content {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 8px;
  scrollbar-width: none;
  padding-left: 4px;
}

.horizontal-scroll-content::-webkit-scrollbar {
  display: none;
}

.horizontal-product-card {
  flex-shrink: 0;
  width: 140px;
  background: #fff;
  border: none;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.horizontal-product-card:hover {
  transform: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: transparent;
}

.horizontal-product-image {
  position: relative;
  width: 100%;
  height: 140px;
  overflow: hidden;
  border-radius: 8px 8px 0 0;
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

.horizontal-product-info {
  padding: 8px;
}

.horizontal-product-title {
  font-size: 13px;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 32px;
  line-height: 16px;
}

.horizontal-product-price {
  font-size: 15px;
  color: #ff672b;
  font-weight: 600;
  margin-bottom: 4px;
}

.horizontal-product-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #999;
}

/* 实时交易动态 - APP风格 */
.trade-dynamic-section {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  height: 100%;
  margin: 0 12px 8px;
}

.trade-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 260px;
  overflow-y: auto;
  scrollbar-width: none;
}

.trade-list::-webkit-scrollbar {
  display: none;
}

.trade-item {
  padding: 8px;
  background: #f8f8f8;
  border-radius: 8px;
  transition: all 0.2s;
}

.trade-item:hover {
  background: #f0f0f0;
}

.trade-content {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.trade-avatar {
  border-radius: 50%;
}

.trade-info {
  flex: 1;
  min-width: 0;
}

.trade-text {
  font-size: 11px;
  color: #999;
  line-height: 1.4;
}

.trade-product {
  font-size: 12px;
  color: #333;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin: 1px 0;
}

.trade-time {
  font-size: 10px;
  color: #ccc;
}

/* 底部区域 - APP风格 */
.bottom-section {
  margin-bottom: 20px;
  padding: 0 12px;
}

.recommend-section {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.product-card-compact {
  background: #fff;
  border: none;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.product-card-compact:hover {
  transform: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: transparent;
}

.product-image-wrapper-compact {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
  background: #f8f8f8;
  border-radius: 8px 8px 0 0;
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
  transition: all 0.2s;
  z-index: 2;
}

.product-info-compact {
  padding: 8px;
}

.product-title-compact {
  font-size: 12px;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 30px;
  line-height: 15px;
}

.product-price-compact {
  font-size: 14px;
  color: #ff672b;
  font-weight: 600;
}

/* 公告抽屉 - APP风格 */
.app-drawer {
  --el-drawer-border-radius: 20px 20px 0 0;
  --el-drawer-background-color: #fff;
}

.notice-drawer-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
}

.notice-drawer-item {
  padding: 12px;
  background: #f8f8f8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.notice-drawer-item:hover {
  background: #f0f0f0;
  box-shadow: none;
}

.notice-drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.notice-drawer-date {
  font-size: 11px;
  color: #999;
}

.notice-drawer-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.notice-drawer-summary {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

/* 公告详情对话框 - APP风格 */
.app-dialog {
  --el-dialog-border-radius: 16px;
  --el-dialog-background-color: #fff;
}

.notice-detail-content {
  padding: 8px 0;
}

.notice-detail-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.notice-detail-date {
  font-size: 12px;
  color: #999;
}

.notice-detail-text {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
}

/* 响应式适配 - APP优先 */
@media (max-width: 992px) {
  .dashboard-container {
    padding: 0;
  }
  
  .top-section,
  .middle-section,
  .bottom-section {
    margin-bottom: 8px;
  }

  .carousel-wrapper {
    height: 180px;
  }

  .stats-wrapper {
    margin-top: 0;
  }

  .quick-categories {
    margin-top: 0;
  }
}

@media (max-width: 768px) {
  .carousel-title {
    font-size: 16px;
  }

  .section-title {
    font-size: 16px;
  }

  .horizontal-product-card {
    width: 120px;
  }

  .horizontal-product-image {
    height: 120px;
  }
}

/* 底部安全区适配 */
@media (max-width: 768px) {
  .bottom-section {
    padding-bottom: env(safe-area-inset-bottom);
  }
}
</style>
