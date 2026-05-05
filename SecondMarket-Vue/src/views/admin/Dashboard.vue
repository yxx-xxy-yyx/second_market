<template>
  <div class="admin-dashboard">
    <div class="page-header">
      <h2 class="page-title">数据统计</h2>
    </div>

      <el-row :gutter="20">
      <!-- 左侧主要内容区域 70% -->
      <el-col :xs="24" :sm="24" :md="17" :lg="17">
        <div class="main-content-area">
          <!-- 图表区域 - 2x2网格 -->
          <el-row :gutter="15">
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">用户增长趋势</span>
                <span class="chart-subtitle">最近30天</span>
              </div>
            </template>
            <div ref="userTrendChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">商品分类分布</span>
              </div>
            </template>
            <div ref="categoryChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">订单状态分布</span>
              </div>
            </template>
            <div ref="orderStatusChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">交易趋势</span>
                <span class="chart-subtitle">最近30天</span>
              </div>
            </template>
            <div ref="tradeTrendChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

          <!-- 表格区域 - 1x2网格 -->
          <el-row :gutter="15" style="margin-top: 15px;">
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="table-card">
            <template #header>
              <div class="table-header">
                <span class="table-title">热门商品 Top10</span>
              </div>
            </template>
                <el-table :data="hotProducts" stripe style="width: 100%" max-height="400">
              <el-table-column label="排名" width="80">
                <template #default="{ $index }">
                  <div class="rank-cell">
                    <el-icon v-if="$index === 0" color="#FFD700" :size="20"><Medal /></el-icon>
                    <el-icon v-else-if="$index === 1" color="#C0C0C0" :size="20"><Medal /></el-icon>
                    <el-icon v-else-if="$index === 2" color="#CD7F32" :size="20"><Medal /></el-icon>
                    <span v-else>{{ $index + 1 }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="商品名称" prop="title" show-overflow-tooltip />
              <el-table-column label="浏览量" prop="viewCount" width="100" />
            </el-table>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="table-card">
            <template #header>
              <div class="table-header">
                <span class="table-title">活跃用户 Top10</span>
              </div>
            </template>
                <el-table :data="activeUsers" stripe style="width: 100%" max-height="400">
              <el-table-column label="排名" width="80">
                <template #default="{ $index }">
                  <div class="rank-cell">
                    <el-icon v-if="$index === 0" color="#FFD700" :size="20"><Medal /></el-icon>
                    <el-icon v-else-if="$index === 1" color="#C0C0C0" :size="20"><Medal /></el-icon>
                    <el-icon v-else-if="$index === 2" color="#CD7F32" :size="20"><Medal /></el-icon>
                    <span v-else>{{ $index + 1 }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="用户名" prop="username" show-overflow-tooltip />
              <el-table-column label="活跃度" prop="activityScore" width="100" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
      </el-col>

      <!-- 右侧侧边栏 30% - 数据指标卡片网格 -->
      <el-col :xs="24" :sm="24" :md="7" :lg="7">
        <div class="sidebar-area">
          <!-- 主要指标卡片 2x2 网格 -->
          <div class="stats-grid">
            <div class="stat-card-grid" v-for="stat in statsCards" :key="stat.label">
              <div class="stat-info-grid">
                <div class="stat-value-grid">{{ stat.value }}</div>
                <div class="stat-label-grid">{{ stat.label }}</div>
              </div>
            </div>
          </div>

          <!-- 快速操作区域 -->
          <el-card class="quick-actions-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-title">快速操作</span>
              </div>
            </template>
            <div class="quick-actions">
              <el-button type="primary" size="small" class="action-btn">
                <el-icon><Plus /></el-icon>
                新增用户
              </el-button>
              <el-button type="success" size="small" class="action-btn">
                <el-icon><Check /></el-icon>
                审核商品
              </el-button>
              <el-button type="warning" size="small" class="action-btn">
                <el-icon><Warning /></el-icon>
                处理举报
              </el-button>
              <el-button type="info" size="small" class="action-btn">
                <el-icon><Document /></el-icon>
                系统日志
              </el-button>
            </div>
          </el-card>

          <!-- 系统状态监控 -->
          <el-card class="system-status-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-title">系统状态</span>
              </div>
            </template>
            <div class="system-status">
              <div class="status-item">
                <div class="status-indicator online"></div>
                <span class="status-text">服务器运行正常</span>
              </div>
              <div class="status-item">
                <div class="status-indicator online"></div>
                <span class="status-text">数据库连接正常</span>
              </div>
              <div class="status-item">
                <div class="status-indicator warning"></div>
                <span class="status-text">存储空间 78%</span>
              </div>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  ShoppingBag,
  ShoppingCart,
  Money,
  Medal,
  Plus,
  Check,
  Warning,
  Document
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { adminStatisticsApi } from '@/api/admin'

const userTrendChartRef = ref(null)
const categoryChartRef = ref(null)
const orderStatusChartRef = ref(null)
const tradeTrendChartRef = ref(null)

let userTrendChart = null
let categoryChart = null
let orderStatusChart = null
let tradeTrendChart = null

const statsCards = ref([
  { label: '总用户数', value: 0, icon: User, gradient: 'linear-gradient(135deg, #e1f0ff 0%, #f0f8ff 100%)' },
  { label: '总商品数', value: 0, icon: ShoppingBag, gradient: 'linear-gradient(135deg, #ffe1e1 0%, #fff0e6 100%)' },
  { label: '总订单数', value: 0, icon: ShoppingCart, gradient: 'linear-gradient(135deg, #e6f4ff 0%, #f0faff 100%)' },
  { label: '总交易额', value: '¥0', icon: Money, gradient: 'linear-gradient(135deg, #e8f5e8 0%, #f0faf0 100%)' }
])

const hotProducts = ref([])
const activeUsers = ref([])

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
    console.error('获取概览数据失败:', error)
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
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增用户',
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
              { offset: 0, color: 'rgba(64, 158, 255, 0.6)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ]
          }
        },
        itemStyle: {
          color: '#409eff'
        }
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
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '10%',
      top: 'center'
    },
    series: [
      {
        name: '商品分类',
        type: 'pie',
        radius: '70%',
        center: ['35%', '50%'],
        data: [
          { value: 335, name: '数码产品' },
          { value: 310, name: '图书文具' },
          { value: 234, name: '生活用品' },
          { value: 135, name: '服装鞋包' },
          { value: 148, name: '其他' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
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
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '10%',
      top: 'center'
    },
    series: [
      {
        name: '订单状态',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 24,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 120, name: '待支付', itemStyle: { color: '#e6a23c' } },
          { value: 280, name: '已支付', itemStyle: { color: '#409eff' } },
          { value: 450, name: '已完成', itemStyle: { color: '#67c23a' } },
          { value: 50, name: '已取消', itemStyle: { color: '#909399' } }
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
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['交易量', '交易额']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: [
      {
        type: 'value',
        name: '交易量',
        position: 'left'
      },
      {
        type: 'value',
        name: '交易额（元）',
        position: 'right'
      }
    ],
    series: [
      {
        name: '交易量',
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
              { offset: 0, color: '#409eff' },
              { offset: 1, color: '#67c23a' }
            ]
          }
        }
      },
      {
        name: '交易额',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: amounts,
        itemStyle: {
          color: '#f5576c'
        }
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
    console.error('获取热门商品失败:', error)
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
    console.error('获取活跃用户失败:', error)
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

.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  color: #fff;
  transition: transform 0.3s;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.charts-section {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.chart-subtitle {
  font-size: 14px;
  color: #909399;
}

.chart-container {
  width: 100%;
  height: 350px;
}

.tables-section {
  margin-bottom: 20px;
}

.table-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.table-header {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.rank-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
}

/* 侧边栏样式 */
.sidebar-area {
  position: sticky;
  top: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 指标网格样式 */
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.stat-card-grid {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 12px;
  border-radius: 10px;
  background: #ffffff;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  cursor: pointer;
  text-align: center;
  min-height: 100px;
}

.stat-card-grid:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-color: #409eff;
}

.stat-info-grid {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.stat-value-grid {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 6px;
  line-height: 1.2;
  color: #2c3e50;
}

.stat-label-grid {
  font-size: 12px;
  color: #606266;
  line-height: 1.2;
}

/* 快速操作卡片 */
.quick-actions-card {
  margin-bottom: 16px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.action-btn {
  width: 100%;
  font-size: 12px;
  padding: 8px 12px;
}

/* 系统状态卡片 */
.system-status-card {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.system-status {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-indicator.online {
  background-color: #67c23a;
  box-shadow: 0 0 6px rgba(103, 194, 58, 0.4);
}

.status-indicator.warning {
  background-color: #e6a23c;
  box-shadow: 0 0 6px rgba(230, 162, 60, 0.4);
}

.status-text {
  font-size: 12px;
  color: #606266;
}

.main-content-area {
  min-height: 600px;
}

@media (max-width: 768px) {
  .admin-dashboard {
    padding: 16px;
  }
  
  .sidebar-area {
    position: relative;
    top: 0;
    margin-top: 20px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr 1fr;
    gap: 8px;
  }
  
  .stat-card-grid {
    padding: 12px 8px;
    min-height: 80px;
  }

  .stat-value-grid {
    font-size: 16px;
  }

  .stat-label-grid {
    font-size: 10px;
  }

  .quick-actions {
    grid-template-columns: 1fr;
    gap: 6px;
  }

  .action-btn {
    font-size: 11px;
    padding: 6px 10px;
  }

  .chart-container {
    height: 300px;
  }
}
</style>

