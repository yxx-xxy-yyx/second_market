<template>
  <div class="user-statistics-page">
    <!-- 顶部标题区域 -->
    <div class="stats-header">
      <h1 class="title">我的数据</h1>
      <p class="subtitle">实时追踪您的交易动态与信用表现</p>
    </div>

    <!-- 数据概览卡片 -->
    <div class="overview-section">
      <div class="stat-card" v-for="item in overviewData" :key="item.label">
        <div class="stat-icon" :style="{ background: item.color }">
          <component :is="item.icon" />
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ item.value }}</span>
          <span class="stat-label">{{ item.label }}</span>
          <span class="stat-trend" :class="item.trend > 0 ? 'up' : 'down'">
            {{ item.trend > 0 ? '+' : '' }}{{ item.trend }}%
          </span>
        </div>
      </div>
    </div>

    <!-- 交易趋势图表 -->
    <div class="chart-section">
      <div class="chart-card">
        <div class="chart-header">
          <h3>收入趋势</h3>
          <div class="chart-tabs">
            <button 
              v-for="day in [7, 14, 30]" 
              :key="day"
              :class="{ active: incomeDays === day }"
              @click="loadIncomeData(day)"
            >
              近{{ day }}天
            </button>
          </div>
        </div>
        <div class="chart-body">
          <div class="simple-chart income-chart">
            <div 
              class="chart-bar" 
              v-for="(item, index) in incomeData" 
              :key="index"
              :style="{ height: getBarHeight(item.amount, maxIncome) + '%' }"
            >
              <span class="bar-value">¥{{ item.amount }}</span>
              <span class="bar-label">{{ item.date }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <h3>支出趋势</h3>
          <div class="chart-tabs">
            <button 
              v-for="day in [7, 14, 30]" 
              :key="day"
              :class="{ active: expenseDays === day }"
              @click="loadExpenseData(day)"
            >
              近{{ day }}天
            </button>
          </div>
        </div>
        <div class="chart-body">
          <div class="simple-chart expense-chart">
            <div 
              class="chart-bar expense" 
              v-for="(item, index) in expenseData" 
              :key="index"
              :style="{ height: getBarHeight(item.amount, maxExpense) + '%' }"
            >
              <span class="bar-value">¥{{ item.amount }}</span>
              <span class="bar-label">{{ item.date }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品浏览趋势 -->
    <div class="product-trend-section">
      <div class="section-header">
        <h3>我的商品浏览趋势</h3>
        <span class="section-desc">展示您发布的商品浏览量排行</span>
      </div>
      <div class="product-list">
        <div class="product-item" v-for="item in productViewTrend" :key="item.productId">
          <img :src="item.productImage || defaultImage" class="product-image" />
          <div class="product-info">
            <span class="product-name">{{ item.productName }}</span>
            <span class="product-price">¥{{ item.productPrice }}</span>
          </div>
          <div class="view-count">
            <EyeOutlined />
            <span>{{ item.viewCount }}次浏览</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 信用评分 -->
    <div class="credit-section">
      <div class="section-header">
        <h3>我的信用评分</h3>
      </div>
      <div class="credit-card">
        <div class="credit-score">
          <div class="score-circle" :style="{ '--score': ratingData.score || 100 }">
            <span class="score-number">{{ ratingData.score || 100 }}</span>
            <span class="score-label">信用分</span>
          </div>
        </div>
        <div class="credit-details">
          <div class="detail-item">
            <span class="detail-label">交易次数</span>
            <span class="detail-value">{{ ratingData.tradeCount || 0 }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">好评率</span>
            <span class="detail-value">{{ ratingData.positiveRate || '100%' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">平均评分</span>
            <span class="detail-value">{{ ratingData.avgRating || 5.0 }}分</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">信用等级</span>
            <span class="detail-value level-tag" :class="getCreditLevel(ratingData.score)">
              {{ getCreditLevelText(ratingData.score) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div class="loading-overlay" v-if="loading">
      <div class="loading-spinner"></div>
      <span>数据加载中...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { userStatisticsApi } from '@/api/statistics'
import { EyeOutlined, ShoppingOutlined, DollarOutlined, StarOutlined, TrophyOutlined } from '@ant-design/icons-vue'

const defaultImage = 'https://via.placeholder.com/80x80?text=商品'

const loading = ref(false)

// 概览数据
const overviewData = ref([
  { label: '已发布商品', value: 0, trend: 0, color: '#4F46E5', icon: 'ShoppingOutlined' },
  { label: '完成交易', value: 0, trend: 0, color: '#10B981', icon: 'DollarOutlined' },
  { label: '收到评价', value: 0, trend: 0, color: '#F59E0B', icon: 'StarOutlined' },
  { label: '信用评分', value: 100, trend: 0, color: '#EF4444', icon: 'TrophyOutlined' }
])

// 收入数据
const incomeDays = ref(30)
const incomeData = ref([])
const maxIncome = computed(() => {
  return Math.max(...incomeData.value.map(i => i.amount), 1)
})

// 支出数据
const expenseDays = ref(30)
const expenseData = ref([])
const maxExpense = computed(() => {
  return Math.max(...expenseData.value.map(i => i.amount), 1)
})

// 商品浏览趋势
const productViewTrend = ref([])

// 信用评分数据
const ratingData = ref({
  score: 100,
  tradeCount: 0,
  positiveRate: '100%',
  avgRating: 5.0
})

// 计算柱状图高度
const getBarHeight = (value, max) => {
  if (!max || max === 0) return 10
  return Math.max(10, Math.min(100, (value / max) * 100))
}

// 获取信用等级
const getCreditLevel = (score) => {
  if (score >= 90) return 'excellent'
  if (score >= 80) return 'good'
  if (score >= 60) return 'normal'
  return 'warning'
}

const getCreditLevelText = (score) => {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 60) return '一般'
  return '待提升'
}

// 加载概览数据
const loadOverview = async () => {
  try {
    const res = await userStatisticsApi.getOverview()
    if (res.code === '200' && res.data) {
      overviewData.value[0].value = res.data.publishedProducts || 0
      overviewData.value[1].value = res.data.completedTrades || 0
      overviewData.value[2].value = res.data.receivedReviews || 0
      overviewData.value[3].value = res.data.creditScore || 100
    }
  } catch (e) {
    console.error('加载概览数据失败', e)
  }
}

// 加载收入数据
const loadIncomeData = async (days) => {
  incomeDays.value = days
  try {
    const res = await userStatisticsApi.getTradeIncome(days)
    if (res.code === '200' && res.data) {
      incomeData.value = res.data.map(item => ({
        date: formatDate(item.date),
        amount: item.amount || 0
      }))
    }
  } catch (e) {
    console.error('加载收入数据失败', e)
  }
}

// 加载支出数据
const loadExpenseData = async (days) => {
  expenseDays.value = days
  try {
    const res = await userStatisticsApi.getTradeExpense(days)
    if (res.code === '200' && res.data) {
      expenseData.value = res.data.map(item => ({
        date: formatDate(item.date),
        amount: item.amount || 0
      }))
    }
  } catch (e) {
    console.error('加载支出数据失败', e)
  }
}

// 加载商品浏览趋势
const loadProductViewTrend = async () => {
  try {
    const res = await userStatisticsApi.getProductViewTrend(10)
    if (res.code === '200' && res.data) {
      productViewTrend.value = res.data
    }
  } catch (e) {
    console.error('加载商品浏览趋势失败', e)
  }
}

// 加载信用评分
const loadRating = async () => {
  try {
    const res = await userStatisticsApi.getRating()
    if (res.code === '200' && res.data) {
      ratingData.value = res.data
    }
  } catch (e) {
    console.error('加载信用评分失败', e)
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

// 初始化加载
onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([
      loadOverview(),
      loadIncomeData(30),
      loadExpenseData(30),
      loadProductViewTrend(),
      loadRating()
    ])
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.user-statistics-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  padding: 24px;
  position: relative;
}

.stats-header {
  text-align: center;
  margin-bottom: 32px;
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 14px;
  color: #6b7280;
}

/* 概览卡片 */
.overview-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  display: block;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
  display: block;
  margin-top: 4px;
}

.stat-trend {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
  margin-top: 8px;
  display: inline-block;
}

.stat-trend.up {
  color: #10b981;
  background: #d1fae5;
}

.stat-trend.down {
  color: #ef4444;
  background: #fee2e2;
}

/* 图表区域 */
.chart-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.chart-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}

.chart-tabs {
  display: flex;
  gap: 8px;
}

.chart-tabs button {
  padding: 6px 12px;
  border-radius: 6px;
  border: none;
  background: #f3f4f6;
  color: #6b7280;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.chart-tabs button.active {
  background: #4f46e5;
  color: white;
}

.chart-body {
  height: 200px;
}

.simple-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 100%;
  padding: 10px 0;
}

.chart-bar {
  flex: 1;
  max-width: 40px;
  background: linear-gradient(180deg, #4f46e5 0%, #818cf8 100%);
  border-radius: 4px 4px 0 0;
  margin: 0 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  min-height: 20px;
  position: relative;
  transition: height 0.5s ease;
}

.chart-bar.expense {
  background: linear-gradient(180deg, #ef4444 0%, #f87171 100%);
}

.bar-value {
  position: absolute;
  top: -24px;
  font-size: 11px;
  color: #6b7280;
  white-space: nowrap;
}

.bar-label {
  position: absolute;
  bottom: -20px;
  font-size: 10px;
  color: #9ca3af;
}

/* 商品浏览趋势 */
.product-trend-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 32px;
}

.section-header {
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}

.section-desc {
  font-size: 13px;
  color: #6b7280;
  margin-top: 4px;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 12px;
  transition: background 0.2s;
}

.product-item:hover {
  background: #f3f4f6;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
  display: block;
}

.product-price {
  font-size: 13px;
  color: #4f46e5;
  font-weight: 600;
  margin-top: 4px;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6b7280;
  font-size: 13px;
}

/* 信用评分 */
.credit-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.credit-card {
  display: flex;
  gap: 40px;
  align-items: center;
}

.credit-score {
  flex-shrink: 0;
}

.score-circle {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: conic-gradient(
    #4f46e5 calc(var(--score) * 1%),
    #e5e7eb calc(var(--score) * 1%)
  );
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.score-circle::before {
  content: '';
  width: 110px;
  height: 110px;
  background: white;
  border-radius: 50%;
  position: absolute;
}

.score-number {
  position: relative;
  z-index: 1;
  font-size: 36px;
  font-weight: 700;
  color: #1a1a2e;
}

.score-label {
  position: relative;
  z-index: 1;
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.credit-details {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-label {
  font-size: 13px;
  color: #6b7280;
}

.detail-value {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  margin-top: 4px;
}

.level-tag {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 14px !important;
}

.level-tag.excellent {
  background: #d1fae5;
  color: #10b981;
}

.level-tag.good {
  background: #dbeafe;
  color: #3b82f6;
}

.level-tag.normal {
  background: #fef3c7;
  color: #f59e0b;
}

.level-tag.warning {
  background: #fee2e2;
  color: #ef4444;
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #4f46e5;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 响应式 */
@media (max-width: 1024px) {
  .overview-section {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .chart-section {
    grid-template-columns: 1fr;
  }
  
  .credit-card {
    flex-direction: column;
    text-align: center;
  }
  
  .credit-details {
    width: 100%;
  }
}

@media (max-width: 640px) {
  .user-statistics-page {
    padding: 16px;
  }
  
  .overview-section {
    grid-template-columns: 1fr;
  }
  
  .title {
    font-size: 22px;
  }
}
</style>