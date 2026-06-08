<template>
  <div class="admin-dashboard">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <div class="header-icon">
            <el-icon :size="24"><DataAnalysis /></el-icon>
          </div>
          <div>
            <h2 class="page-title">{{ $t('Dashboard.title') }}</h2>
            <p class="page-subtitle">{{ $t('Dashboard.welcome') }}</p>
          </div>
        </div>
        <div class="header-time">
          {{ currentTime }}
        </div>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="12" :md="6" v-for="(stat, index) in statsCards" :key="index">
          <div class="stat-card" :style="{ background: stat.gradient }">
            <div class="stat-icon-wrapper" :style="{ background: stat.iconBg }">
              <el-icon :size="28" :style="{ color: stat.iconColor }">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ $t(stat.labelKey) }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <el-row :gutter="20">
      <!-- 左侧主要内容区域 -->
      <el-col :xs="24" :sm="24" :md="17" :lg="17">
        <div class="main-content-area">
          <!-- 图表区域 - 2x2网格 -->
          <el-row :gutter="15">
            <el-col :xs="24" :sm="24" :md="12" :lg="12">
              <div class="glass-card">
                <div class="glass-card-header">
                  <div class="header-left">
                    <span class="card-title">{{ $t('Dashboard.userTrend') }}</span>
                    <span class="card-subtitle">{{ $t('Dashboard.last30Days') }}</span>
                  </div>
                  <div class="header-badge">
                    <span class="badge-dot"></span>
                    <span class="badge-text">Live</span>
                  </div>
                </div>
                <div ref="userTrendChartRef" class="chart-container"></div>
              </div>
            </el-col>

            <el-col :xs="24" :sm="24" :md="12" :lg="12">
              <div class="glass-card">
                <div class="glass-card-header">
                  <div class="header-left">
                    <span class="card-title">{{ $t('Dashboard.categoryDistribution') }}</span>
                  </div>
                </div>
                <div ref="categoryChartRef" class="chart-container"></div>
              </div>
            </el-col>

            <el-col :xs="24" :sm="24" :md="12" :lg="12">
              <div class="glass-card">
                <div class="glass-card-header">
                  <div class="header-left">
                    <span class="card-title">{{ $t('Dashboard.orderStatusDistribution') }}</span>
                  </div>
                </div>
                <div ref="orderStatusChartRef" class="chart-container"></div>
              </div>
            </el-col>

            <el-col :xs="24" :sm="24" :md="12" :lg="12">
              <div class="glass-card">
                <div class="glass-card-header">
                  <div class="header-left">
                    <span class="card-title">{{ $t('Dashboard.tradeTrend') }}</span>
                    <span class="card-subtitle">{{ $t('Dashboard.last30Days') }}</span>
                  </div>
                </div>
                <div ref="tradeTrendChartRef" class="chart-container"></div>
              </div>
            </el-col>
          </el-row>

          <!-- 表格区域 -->
          <el-row :gutter="15" class="table-section">
            <el-col :xs="24" :sm="24" :md="12" :lg="12">
              <div class="glass-card">
                <div class="glass-card-header">
                  <span class="card-title">{{ $t('Dashboard.hotProductsTop10') }}</span>
                </div>
                <div class="table-wrapper">
                  <el-table :data="hotProducts" stripe class="glass-table">
                    <el-table-column :label="$t('Dashboard.rank')" width="60" align="center">
                      <template #default="{ $index }">
                        <div class="rank-cell">
                          <el-icon v-if="$index === 0" color="#FFD700" :size="18"><Medal /></el-icon>
                          <el-icon v-else-if="$index === 1" color="#C0C0C0" :size="18"><Medal /></el-icon>
                          <el-icon v-else-if="$index === 2" color="#CD7F32" :size="18"><Medal /></el-icon>
                          <span v-else class="rank-number">{{ $index + 1 }}</span>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column :label="$t('Dashboard.productName')" prop="title" show-overflow-tooltip />
                    <el-table-column :label="$t('Dashboard.viewCount')" prop="viewCount" width="100" align="center" />
                  </el-table>
                </div>
              </div>
            </el-col>

            <el-col :xs="24" :sm="24" :md="12" :lg="12">
              <div class="glass-card">
                <div class="glass-card-header">
                  <span class="card-title">{{ $t('Dashboard.activeUsersTop10') }}</span>
                </div>
                <div class="table-wrapper">
                  <el-table :data="activeUsers" stripe class="glass-table">
                    <el-table-column :label="$t('Dashboard.rank')" width="60" align="center">
                      <template #default="{ $index }">
                        <div class="rank-cell">
                          <el-icon v-if="$index === 0" color="#FFD700" :size="18"><Medal /></el-icon>
                          <el-icon v-else-if="$index === 1" color="#C0C0C0" :size="18"><Medal /></el-icon>
                          <el-icon v-else-if="$index === 2" color="#CD7F32" :size="18"><Medal /></el-icon>
                          <span v-else class="rank-number">{{ $index + 1 }}</span>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column :label="$t('Dashboard.username')" prop="username" show-overflow-tooltip />
                    <el-table-column :label="$t('Dashboard.activityScore')" prop="activityScore" width="100" align="center" />
                  </el-table>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-col>

      <!-- 右侧侧边栏 -->
      <el-col :xs="24" :sm="24" :md="7" :lg="7">
        <div class="sidebar-area">
          <!-- 快速操作区域 -->
          <div class="glass-card">
            <div class="glass-card-header">
              <span class="card-title">{{ $t('Dashboard.quickActions') }}</span>
            </div>
            <div class="quick-actions">
              <el-button type="primary" class="action-btn" @click="handleAction('addUser')">
                <el-icon><UserPlus /></el-icon>
                {{ $t('Dashboard.addUser') }}
              </el-button>
              <el-button type="success" class="action-btn" @click="handleAction('audit')">
                <el-icon><CircleCheck /></el-icon>
                {{ $t('Dashboard.auditProduct') }}
              </el-button>
              <el-button type="warning" class="action-btn" @click="handleAction('report')">
                <el-icon><Warning /></el-icon>
                {{ $t('Dashboard.handleReport') }}
              </el-button>
              <el-button type="info" class="action-btn" @click="handleAction('log')">
                <el-icon><Document /></el-icon>
                {{ $t('Dashboard.systemLog') }}
              </el-button>
            </div>
          </div>

          <!-- 系统状态监控 -->
          <div class="glass-card">
            <div class="glass-card-header">
              <span class="card-title">{{ $t('Dashboard.systemStatus') }}</span>
            </div>
            <div class="system-status">
              <div class="status-item">
                <div class="status-indicator online"></div>
                <span class="status-text">{{ $t('Dashboard.serverNormal') }}</span>
              </div>
              <div class="status-item">
                <div class="status-indicator online"></div>
                <span class="status-text">{{ $t('Dashboard.databaseNormal') }}</span>
              </div>
              <div class="status-item">
                <div class="status-indicator warning"></div>
                <span class="status-text">{{ $t('Dashboard.storageUsage', { percent: 78 }) }}</span>
              </div>
            </div>
          </div>

          <!-- 最近活动 -->
          <div class="glass-card">
            <div class="glass-card-header">
              <span class="card-title">{{ $t('Dashboard.recentActivity') }}</span>
            </div>
            <div class="activity-list">
              <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
                <div class="activity-avatar">
                  <el-avatar :size="32" :src="activity.avatar" />
                </div>
                <div class="activity-content">
                  <div class="activity-text">{{ activity.text }}</div>
                  <div class="activity-time">{{ activity.time }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import {
  User,
  ShoppingBag,
  ShoppingCart,
  Money,
  Medal,
  UserPlus,
  CircleCheck,
  Warning,
  Document,
  DataAnalysis
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { adminStatisticsApi } from '@/api/admin'

const { t } = useI18n()

const userTrendChartRef = ref(null)
const categoryChartRef = ref(null)
const orderStatusChartRef = ref(null)
const tradeTrendChartRef = ref(null)

let userTrendChart = null
let categoryChart = null
let orderStatusChart = null
let tradeTrendChart = null

const currentTime = computed(() => {
  const now = new Date()
  return now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
})

const statsCards = ref([
  { labelKey: 'Dashboard.totalUsers', value: 0, icon: User, gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', iconBg: 'rgba(255,255,255,0.2)', iconColor: '#fff' },
  { labelKey: 'Dashboard.totalProducts', value: 0, icon: ShoppingBag, gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', iconBg: 'rgba(255,255,255,0.2)', iconColor: '#fff' },
  { labelKey: 'Dashboard.totalOrders', value: 0, icon: ShoppingCart, gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', iconBg: 'rgba(255,255,255,0.2)', iconColor: '#fff' },
  { labelKey: 'Dashboard.totalAmount', value: '¥0', icon: Money, gradient: 'linear-gradient(135deg, #06b6d4 0%, #14b8a6 100%)', iconBg: 'rgba(255,255,255,0.2)', iconColor: '#fff' }
])

const hotProducts = ref([])
const activeUsers = ref([])

const recentActivities = ref([
  { id: 1, avatar: 'https://i.pravatar.cc/100?img=1', text: '新用户注册', time: '2分钟前' },
  { id: 2, avatar: 'https://i.pravatar.cc/100?img=2', text: '商品上架审核通过', time: '5分钟前' },
  { id: 3, avatar: 'https://i.pravatar.cc/100?img=3', text: '收到违规举报', time: '10分钟前' },
  { id: 4, avatar: 'https://i.pravatar.cc/100?img=4', text: '订单完成', time: '15分钟前' }
])

const handleAction = (type) => {
  ElMessage.info(`操作: ${type}`)
}

const loadOverviewData = async () => {
  try {
    const res = await adminStatisticsApi.getOverview()
    
    if (res.success) {
      const data = res.data
      statsCards.value[0].value = data.totalUsers || 0
      statsCards.value[1].value = data.totalProducts || 0
      statsCards.value[2].value = data.totalOrders || 0
      statsCards.value[3].value = `¥${(data.totalAmount || 0).toLocaleString()}`
    }
  } catch (error) {
    console.error('Failed to fetch overview data:', error)
  }
}

const initUserTrendChart = () => {
  if (!userTrendChartRef.value) return

  if (userTrendChart) {
    userTrendChart.dispose()
  }

  userTrendChart = echarts.init(userTrendChartRef.value)

  const dates = []
  const values = []
  for (let i = 29; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
    values.push(Math.floor(Math.random() * 50) + 10)
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: 'rgba(102, 126, 234, 0.3)',
      textStyle: { color: '#333' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
      axisLabel: { color: 'rgba(255,255,255,0.7)' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } },
      axisLabel: { color: 'rgba(255,255,255,0.7)' }
    },
    series: [
      {
        name: t('Dashboard.newUsers'),
        type: 'line',
        smooth: true,
        data: values,
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
              { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
            ]
          }
        },
        lineStyle: { color: '#667eea', width: 3 },
        itemStyle: { color: '#667eea' }
      }
    ]
  }

  userTrendChart.setOption(option)
}

const initCategoryChart = () => {
  if (!categoryChartRef.value) return

  if (categoryChart) {
    categoryChart.dispose()
  }

  categoryChart = echarts.init(categoryChartRef.value)

  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: 'rgba(102, 126, 234, 0.3)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { color: 'rgba(255,255,255,0.8)' }
    },
    series: [
      {
        name: t('Dashboard.productCategory'),
        type: 'pie',
        radius: ['45%', '75%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: 'rgba(255,255,255,0.1)',
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 16, fontWeight: 'bold', color: '#fff' }
        },
        labelLine: { show: false },
        data: [
          { value: 335, name: t('Dashboard.category.digital'), itemStyle: { color: '#667eea' } },
          { value: 310, name: t('Dashboard.category.books'), itemStyle: { color: '#f093fb' } },
          { value: 234, name: t('Dashboard.category.living'), itemStyle: { color: '#4facfe' } },
          { value: 135, name: t('Dashboard.category.clothing'), itemStyle: { color: '#f5576c' } },
          { value: 148, name: t('Dashboard.category.other'), itemStyle: { color: '#06b6d4' } }
        ]
      }
    ]
  }

  categoryChart.setOption(option)
}

const initOrderStatusChart = () => {
  if (!orderStatusChartRef.value) return

  if (orderStatusChart) {
    orderStatusChart.dispose()
  }

  orderStatusChart = echarts.init(orderStatusChartRef.value)

  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: 'rgba(102, 126, 234, 0.3)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { color: 'rgba(255,255,255,0.8)' }
    },
    series: [
      {
        name: t('Dashboard.orderStatus'),
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: 'rgba(255,255,255,0.1)',
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 18, fontWeight: 'bold', color: '#fff' }
        },
        labelLine: { show: false },
        data: [
          { value: 120, name: t('Dashboard.status.pendingPayment'), itemStyle: { color: '#f093fb' } },
          { value: 280, name: t('Dashboard.status.paid'), itemStyle: { color: '#4facfe' } },
          { value: 450, name: t('Dashboard.status.completed'), itemStyle: { color: '#667eea' } },
          { value: 50, name: t('Dashboard.status.cancelled'), itemStyle: { color: '#909399' } }
        ]
      }
    ]
  }

  orderStatusChart.setOption(option)
}

const initTradeTrendChart = () => {
  if (!tradeTrendChartRef.value) return

  if (tradeTrendChart) {
    tradeTrendChart.dispose()
  }

  tradeTrendChart = echarts.init(tradeTrendChartRef.value)

  const dates = []
  const volumes = []
  const amounts = []
  
  for (let i = 29; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
    volumes.push(Math.floor(Math.random() * 30) + 10)
    amounts.push(Math.floor(Math.random() * 5000) + 1000)
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: 'rgba(102, 126, 234, 0.3)'
    },
    legend: {
      data: [t('Dashboard.tradeVolume'), t('Dashboard.tradeAmount')],
      textStyle: { color: 'rgba(255,255,255,0.8)' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
      axisLabel: { color: 'rgba(255,255,255,0.7)' }
    },
    yAxis: [
      {
        type: 'value',
        name: t('Dashboard.tradeVolume'),
        position: 'left',
        axisLine: { show: false },
        splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } },
        axisLabel: { color: 'rgba(255,255,255,0.7)' }
      },
      {
        type: 'value',
        name: t('Dashboard.tradeAmountYuan'),
        position: 'right',
        axisLine: { show: false },
        splitLine: { show: false },
        axisLabel: { color: 'rgba(255,255,255,0.7)' }
      }
    ],
    series: [
      {
        name: t('Dashboard.tradeVolume'),
        type: 'bar',
        yAxisIndex: 0,
        data: volumes,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ]
          },
          borderRadius: [4, 4, 0, 0]
        }
      },
      {
        name: t('Dashboard.tradeAmount'),
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: amounts,
        lineStyle: { color: '#f5576c', width: 3 },
        itemStyle: { color: '#f5576c' }
      }
    ]
  }

  tradeTrendChart.setOption(option)
}

const loadHotProducts = async () => {
  try {
    hotProducts.value = [
      { title: '二手iPhone 13 Pro', viewCount: 1234 },
      { title: '大学物理教材', viewCount: 987 },
      { title: '自行车', viewCount: 856 },
      { title: '台式电脑主机', viewCount: 745 },
      { title: '小米手环6', viewCount: 678 },
      { title: '羽毛球拍', viewCount: 567 },
      { title: '电风扇', viewCount: 456 },
      { title: '书架', viewCount: 345 },
      { title: '吉他', viewCount: 234 },
      { title: '数据结构书籍', viewCount: 123 }
    ]
  } catch (error) {
    console.error('Failed to fetch hot products:', error)
  }
}

const loadActiveUsers = async () => {
  try {
    activeUsers.value = [
      { username: 'user001', activityScore: 950 },
      { username: 'user002', activityScore: 890 },
      { username: 'user003', activityScore: 845 },
      { username: 'user004', activityScore: 789 },
      { username: 'user005', activityScore: 756 },
      { username: 'user006', activityScore: 678 },
      { username: 'user007', activityScore: 567 },
      { username: 'user008', activityScore: 456 },
      { username: 'user009', activityScore: 345 },
      { username: 'user010', activityScore: 234 }
    ]
  } catch (error) {
    console.error('Failed to fetch active users:', error)
  }
}

const handleResize = () => {
  userTrendChart?.resize()
  categoryChart?.resize()
  orderStatusChart?.resize()
  tradeTrendChart?.resize()
}

onMounted(() => {
  loadOverviewData()
  loadHotProducts()
  loadActiveUsers()

  nextTick(() => {
    initUserTrendChart()
    initCategoryChart()
    initOrderStatusChart()
    initTradeTrendChart()
  })

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  userTrendChart?.dispose()
  categoryChart?.dispose()
  orderStatusChart?.dispose()
  tradeTrendChart?.dispose()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(135deg, #0c0c1e 0%, #1a1a3e 50%, #0d1b2a 100%);
}

/* 页面头部 */
.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(40px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.header-title {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.page-title {
  font-size: 28px;
  font-weight: 900;
  color: white;
  margin: 0 0 4px 0;
  background: linear-gradient(135deg, #667eea 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

.header-time {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

/* 统计卡片区域 */
.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px 20px;
  border-radius: 20px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.stat-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 900;
  color: white;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 4px;
}

/* 玻璃态卡片 */
.glass-card {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(40px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  padding: 24px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.glass-card:hover {
  background: rgba(255, 255, 255, 0.12);
  transform: translateY(-2px);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
}

.glass-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: white;
}

.card-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.header-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(103, 194, 58, 0.2);
  border-radius: 20px;
  border: 1px solid rgba(103, 194, 58, 0.3);
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: #67c23a;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.badge-text {
  font-size: 11px;
  font-weight: 600;
  color: #67c23a;
}

/* 图表容器 */
.chart-container {
  width: 100%;
  height: 280px;
}

/* 表格区域 */
.table-section {
  margin-top: 8px;
}

.table-wrapper {
  margin-top: 16px;
}

/* 玻璃态表格 */
.glass-table {
  background: transparent !important;
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(255, 255, 255, 0.05);
  --el-table-row-hover-bg-color: rgba(255, 255, 255, 0.08);
  --el-table-border-color: rgba(255, 255, 255, 0.1);
  --el-table-text-color: rgba(255, 255, 255, 0.9);
  --el-table-header-text-color: rgba(255, 255, 255, 0.7);
}

.glass-table :deep(.el-table__header th) {
  font-weight: 600;
  font-size: 13px;
}

.glass-table :deep(.el-table__row) {
  transition: all 0.2s ease;
}

.glass-table :deep(.el-table__row:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
}

.rank-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.rank-number {
  font-weight: 700;
  color: rgba(255, 255, 255, 0.6);
}

/* 侧边栏 */
.sidebar-area {
  position: sticky;
  top: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 快速操作 */
.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 16px;
}

.action-btn {
  width: 100%;
  height: 44px;
  border-radius: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  border: none;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

/* 系统状态 */
.system-status {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-indicator.online {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  box-shadow: 0 0 12px rgba(103, 194, 58, 0.5);
}

.status-indicator.warning {
  background: linear-gradient(135deg, #e6a23c 0%, #f5c23c 100%);
  box-shadow: 0 0 12px rgba(230, 162, 60, 0.5);
}

.status-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

/* 最近活动 */
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 14px;
  transition: all 0.2s ease;
}

.activity-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.activity-content {
  flex: 1;
}

.activity-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.activity-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 4px;
}

/* 响应式 */
@media (max-width: 768px) {
  .admin-dashboard {
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 20px;
  }
  
  .header-time {
    align-self: flex-start;
  }
  
  .stat-card {
    padding: 20px 16px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .glass-card {
    padding: 20px;
  }
  
  .chart-container {
    height: 220px;
  }
  
  .sidebar-area {
    position: relative;
    top: 0;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>
