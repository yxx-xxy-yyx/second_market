<template>
  <div class="profile-page">
    <div class="page-container">
      <div class="page-header">
        <h2 class="page-title">{{ $t('profile.personalCenter') }}</h2>
      </div>

      <div class="profile-content">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="24" :md="8" :lg="8">
            <el-card class="info-card">
              <div class="avatar-section">
                <div class="avatar-wrapper" @click="handleAvatarClick">
                  <el-avatar :size="120" :src="avatarUrl">
                    <el-icon :size="60"><User /></el-icon>
                  </el-avatar>
                  <div class="avatar-mask">
                    <el-icon><Camera /></el-icon>
                    <div class="mask-text">{{ $t('profile.changeAvatar') }}</div>
                  </div>
                </div>
                <div class="user-name">{{ userInfo.nickname || userInfo.username }}</div>
              </div>

              <div class="info-list">
                <div class="info-item">
                  <div class="info-label">{{ $t('profile.phone') }}</div>
                  <div class="info-value">{{ userInfo.phone || $t('profile.notSet') }}</div>
                </div>
                <div class="info-item">
                  <div class="info-label">{{ $t('profile.address') }}</div>
                  <div class="info-value">{{ userInfo.address || $t('profile.notSet') }}</div>
                </div>
                <div class="info-item">
                  <div class="info-label">{{ $t('profile.registerTime') }}</div>
                  <div class="info-value">{{ formatTime(userInfo.createTime) }}</div>
                </div>
              </div>

              <el-button type="primary" plain class="edit-btn" @click="showEditDialog = true">
                <el-icon><Edit /></el-icon>
                {{ $t('profile.editInfo') }}
              </el-button>
              <el-button plain class="qrcode-btn" @click="showQrcodeDrawer = true">
                <el-icon><User /></el-icon>
                {{ $t('profile.myBusinessCard') }}
              </el-button>
            </el-card>
          </el-col>

          <el-col :xs="24" :sm="24" :md="16" :lg="16">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12" :md="12" :lg="12">
                <el-card class="credit-card">
                  <template #header>
                    <div class="card-header">
                      <span>{{ $t('profile.myCredit') }}</span>
                    </div>
                  </template>
                  <div class="credit-content">
                    <div class="credit-score">{{ statistics.creditScore || 0 }}</div>
                    <el-rate
                      v-model="creditStars"
                      disabled
                      show-score
                      text-color="#ff9900"
                      score-template="{value}"
                      size="large"
                    />
                    <div class="credit-desc">{{ $t('profile.basedOn') }} {{ statistics.reviewCount || 0 }} {{ $t('profile.reviews') }}</div>
                  </div>
                </el-card>
              </el-col>

              <el-col :xs="24" :sm="12" :md="12" :lg="12">
                <el-card class="stats-card">
                  <template #header>
                    <div class="card-header">
                      <span>{{ $t('profile.tradeStats') }}</span>
                    </div>
                  </template>
                  <div class="stats-grid">
                    <!-- 我发布的 -->
                    <div class="stat-item" @click="goToMyPublished">
                      <div class="stat-icon" style="background: #409eff; cursor: pointer">
                        <el-icon :size="24"><Box /></el-icon>
                      </div>
                      <div class="stat-info">
                        <div class="stat-value">{{ statistics.productCount || 0 }}</div>
                        <div class="stat-label">{{ $t('profile.publishCount') }}</div>
                      </div>
                    </div>

                    <!-- 我卖出的 -->
                    <div class="stat-item" @click="goToMySold">
                      <div class="stat-icon" style="background: #67c23a; cursor: pointer">
                        <el-icon :size="24"><CircleCheck /></el-icon>
                      </div>
                      <div class="stat-info">
                        <div class="stat-value">{{ statistics.soldCount || 0 }}</div>
                        <div class="stat-label">{{ $t('profile.soldCount') }}</div>
                      </div>
                    </div>

                    <!-- 我买到的 -->
                    <div class="stat-item" @click="goToMyBought">
                      <div class="stat-icon" style="background: #e6a23c; cursor: pointer">
                        <el-icon :size="24"><ShoppingBag /></el-icon>
                      </div>
                      <div class="stat-info">
                        <div class="stat-value">{{ statistics.buyCount || 0 }}</div>
                        <div class="stat-label">{{ $t('profile.buyCount') }}</div>
                      </div>
                    </div>

                    <!-- 我的收藏 -->
                    <div class="stat-item" @click="goToMyFavorite">
                      <div class="stat-icon" style="background: #f56c6c; cursor: pointer">
                        <el-icon :size="24"><Star /></el-icon>
                      </div>
                      <div class="stat-info">
                        <div class="stat-value">{{ statistics.favoriteCount || 0 }}</div>
                        <div class="stat-label">{{ $t('profile.favoriteCount') }}</div>
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <el-card class="chart-card" style="margin-top: 20px">
              <template #header>
                <div class="card-header">
                  <span>{{ $t('profile.tradeTrend') }}</span>
                  <span class="sub-title">{{ $t('profile.last30Days') }}</span>
                </div>
              </template>
              <div ref="chartRef" class="chart-container"></div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <el-dialog
      v-model="showEditDialog"
      :title="$t('profile.editInfo')"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="80px">
        <el-form-item :label="$t('profile.nickname')" prop="nickname">
          <el-input v-model="editForm.nickname" :placeholder="$t('profile.nickname')" />
        </el-form-item>
        <el-form-item :label="$t('profile.phone')" prop="phone">
          <el-input v-model="editForm.phone" :placeholder="$t('profile.phone')" />
        </el-form-item>
        <el-form-item :label="$t('profile.address')" prop="address">
          <el-input
            v-model="editForm.address"
            type="textarea"
            :rows="3"
            :placeholder="$t('profile.address')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSaveEdit">{{ $t('common.save') }}</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="showAvatarDialog"
      :title="$t('profile.changeAvatar')"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="avatar-upload-dialog">
        <AvatarUpload
          :model-value="avatarUrl"
          :size="200"
          @success="handleAvatarSuccess"
        />
      </div>
      <template #footer>
        <el-button @click="showAvatarDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  Edit,
  Camera,
  Box,
  CircleCheck,
  ShoppingBag,
  Star,
  Download,
  Share
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import { userStatisticsApi } from '@/api/statistics'
import { formatAvatarUrl } from '@/utils/url'
import AvatarUpload from '@/components/AvatarUpload.vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'

// 路由
const router = useRouter()
const { t } = useI18n()
const userStore = useUserStore()

const isOk = (res) => res && (res.code === '200' || res.success)

const chartRef = ref(null)
let chartInstance = null

const userInfo = ref({})
const statistics = ref({})
const trendData = ref({ dates: [], income: [], expense: [] })

const showEditDialog = ref(false)
const showAvatarDialog = ref(false)
const showQrcodeDrawer = ref(false)
const editFormRef = ref(null)

const editForm = ref({
  nickname: '',
  phone: '',
  address: ''
})

// 跳转到我发布的商品
const goToMyPublished = () => {
  router.push('/user/my-products')
}

// 跳转到我卖出的
const goToMySold = () => {
  router.push({
    path: '/user/orders',
    query: {
      tab: 'seller'
    }
  })
}

// 跳转到我买到的
const goToMyBought = () => {
  router.push({
    path: '/user/orders',
    query: {
      tab: 'buyer'
    }
  })
}

// 跳转到我的收藏
const goToMyFavorite = () => {
  router.push('/user/favorites')
}

const editRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在2-20个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const avatarUrl = computed(() => {
  return formatAvatarUrl(userInfo.value?.avatar)
})

const creditStars = computed(() => {
  const score = statistics.value.creditScore || 0
  return Math.min(5, Math.round(score / 20))
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const loadUserInfo = async () => {
  try {
    const ok = await userStore.getUserInfo()
    if (ok.success) {
      const data = userStore.user || {}
      userInfo.value = data
      editForm.value = {
        nickname: data.nickname || '',
        phone: data.phone || '',
        address: data.address || ''
      }
      return
    }

    const res = await userApi.getCurrentUser()
    if (isOk(res)) {
      userInfo.value = res.data
      userStore.setUser(res.data)
      editForm.value = {
        nickname: res.data.nickname || '',
        phone: res.data.phone || '',
        address: res.data.address || ''
      }
    }
  } catch {
  }
}

const loadStatistics = async () => {
  try {
    const res = await userStatisticsApi.getOverview()
    if (isOk(res)) {
      statistics.value = res.data
    }
  } catch {
  }
}

const loadTrendData = async () => {
  try {
    const [incomeRes, expenseRes] = await Promise.all([
      userStatisticsApi.getTradeIncome(30),
      userStatisticsApi.getTradeExpense(30)
    ])
    
    if (isOk(incomeRes) && isOk(expenseRes)) {
      const incomeData = incomeRes.data || []
      const expenseData = expenseRes.data || []
      
      // 如果没有数据，生成默认的30天数据
      if (incomeData.length === 0 && expenseData.length === 0) {
        const dates = []
        const income = []
        const expense = []
        
        for (let i = 29; i >= 0; i--) {
          const date = new Date()
          date.setDate(date.getDate() - i)
          dates.push(date.toISOString().split('T')[0])
          income.push(0)
          expense.push(0)
        }
        
        trendData.value = { dates, income, expense }
      } else {
        // 合并日期数据，确保收入和支出数据对齐
        const allDates = new Set([
          ...incomeData.map(item => item.date),
          ...expenseData.map(item => item.date)
        ])
        const sortedDates = Array.from(allDates).sort()
        
        const incomeMap = new Map(incomeData.map(item => [item.date, item.amount]))
        const expenseMap = new Map(expenseData.map(item => [item.date, item.amount]))
        
        const dates = sortedDates
        const income = sortedDates.map(date => incomeMap.get(date) || 0)
        const expense = sortedDates.map(date => expenseMap.get(date) || 0)
        
        trendData.value = { dates, income, expense }
      }
      
      nextTick(() => {
        initChart()
      })
    }
  } catch {
    // 设置默认数据
    const dates = []
    const income = []
    const expense = []
    
    for (let i = 29; i >= 0; i--) {
      const date = new Date()
      date.setDate(date.getDate() - i)
      dates.push(date.toISOString().split('T')[0])
      income.push(0)
      expense.push(0)
    }
    
    trendData.value = { dates, income, expense }
    nextTick(() => {
      initChart()
    })
  }
}

const initChart = () => {
  if (!chartRef.value) return

  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(chartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['收入', '支出'],
      top: 0
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
      data: trendData.value.dates || []
    },
    yAxis: [
      {
        type: 'value',
        name: t('chart.incomeUnit'),
        position: 'left',
        axisLabel: {
          formatter: '{value}'
        }
      },
      {
        type: 'value',
        name: t('chart.expendUnit'),
        position: 'right',
        axisLabel: {
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: t('chart.income'),
        type: 'line',
        smooth: true,
        yAxisIndex: 0,
        data: trendData.value.income || [],
        itemStyle: {
          color: '#67c23a'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
              { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
            ]
          }
        }
      },
      {
        name: t('chart.expend'),
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: trendData.value.expense || [],
        itemStyle: {
          color: '#f56c6c'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
              { offset: 1, color: 'rgba(245, 108, 108, 0.05)' }
            ]
          }
        }
      }
    ]
  }

  chartInstance.setOption(option)

  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
}

const handleAvatarClick = () => {
  showAvatarDialog.value = true
}

const handleAvatarSuccess = async (data) => {
  showAvatarDialog.value = false
  await loadUserInfo()
  if (userInfo.value) {
    userStore.setUser(userInfo.value)
  }
}

const handleSaveEdit = async () => {
  if (!editFormRef.value) return

  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await userApi.updateUserInfo(editForm.value)
        if (res.code === '200') {
          ElMessage.success(t('profile.saveSuccess'))
          showEditDialog.value = false
          await loadUserInfo()
          userStore.updateUser(userInfo.value)
        } else {
          throw new Error(res.message || t('profile.saveFail'))
        }
      } catch (error) {
        ElMessage.error(error.message || t('profile.saveFail'))
      }
    }
  })
}

onMounted(() => {
  loadUserInfo()
  loadStatistics()
  loadTrendData()
})
</script>

<style scoped>
.stat-item {
  cursor: pointer;
  transition: all 0.2s ease;
}
.stat-item:hover {
  transform: translateY(-2px);
  opacity: 0.85;
}

.profile-page {
  min-height: calc(100vh - 110px);
  background: #f0f2f5;
  padding: 20px;
}

.page-container {
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

.profile-content {
  width: 100%;
}

.info-card {
  height: 100%;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  margin-bottom: 16px;
}

.avatar-wrapper:hover .avatar-mask {
  opacity: 1;
}

.avatar-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-mask .el-icon {
  font-size: 32px;
  margin-bottom: 4px;
}

.mask-text {
  font-size: 12px;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  text-align: center;
}

.info-list {
  padding: 20px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px solid #e4e7ed;
  gap: 12px;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  color: #909399;
  font-size: 14px;
  flex: 0 0 90px;
}

.info-value {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
  flex: 1 1 auto;
  text-align: right;
  word-break: break-word;
}

@media (max-width: 767px) {
  .page-header {
    padding: 14px 14px;
    border-radius: 14px;
  }
  .page-title {
    font-size: 18px;
  }
  .info-item {
    padding: 10px 0;
  }
  .info-label {
    flex-basis: 78px;
    font-size: 13px;
  }
  .info-value {
    font-size: 13px;
  }
}

.edit-btn,
.qrcode-btn {
  width: 100%;
  margin: 10px auto;
}

/* 二维码名片抽屉 */
.qrcode-card-content {
  padding: 10px 0;
}

.qrcode-header {
  display: flex;
  gap: 16px;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #f0f7ff 0%, #e6f0ff 100%);
  border-radius: 10px;
  margin-bottom: 20px;
}

.qrcode-user-info {
  flex: 1;
}

.qrcode-username {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.qrcode-credit {
  display: flex;
  align-items: center;
  gap: 8px;
}

.credit-text {
  font-size: 13px;
  color: #606266;
}

.qrcode-stats {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
}

.qrcode-stat-item {
  text-align: center;
}

.qrcode-stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 6px;
}

.qrcode-stat-label {
  font-size: 13px;
  color: #909399;
}

.qrcode-section {
  text-align: center;
  padding: 20px;
}

.qrcode-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
}

.qrcode-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: #f8f9fa;
  border-radius: 10px;
  border: 2px dashed #dcdfe6;
}

.qrcode-placeholder p {
  margin-top: 10px;
  font-size: 14px;
  color: #909399;
}

.qrcode-actions {
  display: flex;
  gap: 10px;
  padding: 0 10px;
}

.qrcode-actions .el-button {
  flex: 1;
}

.credit-card,
.stats-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.sub-title {
  font-size: 14px;
  color: #909399;
  font-weight: normal;
}

.credit-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.credit-score {
  font-size: 60px;
  font-weight: 700;
  color: #e6a23c;
  margin-bottom: 16px;
}

.credit-desc {
  color: #909399;
  font-size: 14px;
  margin-top: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 12px 0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.chart-card {
  margin-top: 20px;
}

.chart-container {
  width: 100%;
  height: 400px;
}

.avatar-upload-dialog {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .profile-page {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .chart-container {
    height: 300px;
  }
}
</style>
