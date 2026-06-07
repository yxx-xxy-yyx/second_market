<template>
  <div class="my-orders-page">
    <div class="page-container">
      <div class="page-header">
        <h2 class="page-title">{{ $t('nav.myOrders') }}</h2>
      </div>

      <!-- 顶部统计条 -->
      <el-row :gutter="15" class="stats-bar">
        <el-col :span="6" v-for="(stat, index) in stats" :key="index">
          <div class="stat-card" :style="{ borderLeft: `4px solid ${stat.color}` }">
            <div class="stat-icon" :style="{ background: stat.bgColor }">
              <el-icon :size="24" :color="stat.color">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ $t('order.' + stat.key) }}</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="orders-tabs">
        <el-tab-pane :label="$t('order.myBuy')" name="buyer">
          <el-row :gutter="15">
            <!-- 左侧订单列表 65% -->
            <el-col :xs="24" :sm="24" :md="16" :lg="16">
              <!-- 状态筛选 -->
              <div class="status-filters">
                <div v-for="status in statusList" :key="status.value"
                  :class="['status-filter-item', { active: currentStatus === status.value }]"
                  @click="handleStatusChange(status.value)">
                  <el-icon>
                    <component :is="status.icon" />
                  </el-icon>
                  <span>{{ $t('order.' + status.key) }}</span>
                  <el-badge v-if="status.count > 0" :value="status.count" class="filter-badge" />
                </div>
              </div>

              <!-- 订单列表 -->
              <div class="orders-list" v-loading="loading">
                <el-empty v-if="!loading && orders.length === 0" :description="$t('common.noData')" :image-size="160" />

                <div v-for="order in orders" :key="order.id" class="order-card" @click="showOrderDetail(order)">
                  <div class="order-header">
                    <div class="order-number">
                      <el-icon>
                        <Tickets />
                      </el-icon>
                      <span class="value">{{ order.orderNumber || order.orderNo || order.id }}</span>
                    </div>
                    <el-tag :type="getStatusType(order.status)" size="default">
                      {{ getStatusText(order.status) }}
                    </el-tag>
                  </div>

                  <div class="order-content">
                    <div class="product-info" @click.stop="handleViewProduct(order.productId)">
                      <el-image :src="getProductImage(order.productImages)" fit="cover" class="product-image">
                        <template #error>
                          <div class="image-error">
                            <el-icon>
                              <Picture />
                            </el-icon>
                          </div>
                        </template>
                      </el-image>
                      <div class="product-details">
                        <div class="product-title">{{ locale === 'ko' && order.productTitleKo ? order.productTitleKo :
                          order.productTitle }}
                        </div>
                        <div class="product-meta">
                          <span class="order-time">{{ formatTime(order.createTime) }}</span>
                        </div>
                      </div>
                    </div>
                    <div class="amount-info">
                      <div class="amount-label">{{ $t('order.orderAmount') }}</div>
                      <div class="amount-value">¥{{ order.amount }}</div>
                    </div>
                  </div>

                  <div class="order-footer">
                    <div class="seller-info">
                      <el-avatar :size="28" :src="formatAvatar(order.sellerAvatar)">
                        <el-icon>
                          <User />
                        </el-icon>
                      </el-avatar>
                      <span class="seller-name">{{ order.sellerName }}</span>
                    </div>

                    <div class="order-actions" @click.stop>
                      <el-button v-if="order.status === 0" type="warning" size="default" @click="handlePay(order)">
                        <el-icon>
                          <Wallet />
                        </el-icon>
                        {{ $t('common.pay') }}
                      </el-button>
                      <el-button v-if="order.status === 1" type="success" size="default" @click="handleConfirm(order)">
                        <el-icon><Select /></el-icon>
                        {{ $t('common.confirmReceipt') }}
                      </el-button>
                      <el-button v-if="order.status === 2" size="default" @click="handleReview(order)">
                        <el-icon>
                          <Edit />
                        </el-icon>
                        {{ $t('common.review') }}
                      </el-button>
                      <el-button v-if="order.status === 0" size="default" @click="handleCancel(order)">
                        <el-icon>
                          <Close />
                        </el-icon>
                        {{ $t('common.cancel') }}
                      </el-button>
                      <el-button size="default" @click.stop="showOrderDetail(order)">
                        <el-icon>
                          <View />
                        </el-icon>
                        {{ $t('common.view') }}
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 自定义纯中文分页 -->
              <div class="pagination" v-if="total > 0">
                <div class="custom-pagination">
                  <span class="pagination-text">{{ $t('pagination.total') }} {{ total }} {{ $t('pagination.items')
                  }}</span>
                  <el-button :disabled="pageNum === 1" @click="prevPage">{{ $t('pagination.prev') }}</el-button>
                  <el-pagination v-model:current-page="pageNum" :total="total" :page-size="pageSize" layout="pager"
                    @current-change="handlePageChange" />
                  <el-button :disabled="pageNum >= totalPages" @click="nextPage">{{ $t('pagination.next') }}</el-button>
                </div>
              </div>
            </el-col>

            <!-- 右侧交易统计 35% -->
            <el-col :xs="24" :sm="24" :md="8" :lg="8">
              <div class="trade-stats-panel">
                <!-- 本月交易统计 -->
                <el-card class="stats-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <el-icon color="#67c23a">
                        <TrendCharts />
                      </el-icon>
                      <span>{{ $t('order.monthStats') }}</span>
                    </div>
                  </template>
                  <div class="month-stats">
                    <div class="month-stat-item">
                      <div class="stat-item-label">{{ $t('order.myBuy') }}</div>
                      <div class="stat-item-value" style="color: #409eff;">{{ monthStats.buyCount }}</div>
                    </div>
                    <div class="month-stat-item">
                      <div class="stat-item-label">{{ $t('order.accumulatedExpense') }}</div>
                      <div class="stat-item-value" style="color: #ff9800;">¥{{ monthStats.buyAmount }}</div>
                    </div>
                    <div class="month-stat-item">
                      <div class="stat-item-label">{{ $t('order.avgOrderAmount') }}</div>
                      <div class="stat-item-value" style="color: #67c23a;">¥{{ monthStats.avgAmount }}</div>
                    </div>
                  </div>
                </el-card>

                <!-- 快捷操作 -->
                <el-card class="quick-actions-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <el-icon color="#409eff">
                        <Operation />
                      </el-icon>
                      <span>{{ $t('order.quickActions') }}</span>
                    </div>
                  </template>
                  <div class="quick-actions">
                    <div class="quick-action-item" @click="handleStatusChange(0)">
                      <el-icon :size="32" color="#ff9800">
                        <Clock />
                      </el-icon>
                      <div class="action-label">{{ $t('order.pendingPay') }}</div>
                      <div class="action-count">{{ orderCounts.pending }}</div>
                    </div>
                    <div class="quick-action-item" @click="handleStatusChange(1)">
                      <el-icon :size="32" color="#409eff">
                        <Box />
                      </el-icon>
                      <div class="action-label">{{ $t('order.pendingReceive') }}</div>
                      <div class="action-count">{{ orderCounts.shipping }}</div>
                    </div>
                    <div class="quick-action-item" @click="handleStatusChange(2)">
                      <el-icon :size="32" color="#67c23a">
                        <Comment />
                      </el-icon>
                      <div class="action-label">{{ $t('order.pendingEvaluation') }}</div>
                      <div class="action-count">{{ orderCounts.evaluate }}</div>
                    </div>
                  </div>
                </el-card>

                <!-- 最近交易动态 -->
                <el-card class="recent-trades-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <el-icon color="#ff9800">
                        <Bell />
                      </el-icon>
                      <span>{{ $t('order.recentTrades') }}</span>
                    </div>
                  </template>
                  <div class="recent-trades">
                    <div v-for="(trade, index) in recentTrades" :key="index" class="trade-item">
                      <div class="trade-dot" :style="{ background: trade.color }"></div>
                      <div class="trade-content">
                        <div class="trade-title">{{ trade.title }}</div>
                        <div class="trade-time">{{ trade.time }}</div>
                      </div>
                    </div>
                  </div>
                </el-card>
              </div>
            </el-col>
          </el-row>
        </el-tab-pane>

        <el-tab-pane :label="$t('order.mySell')" name="seller">
          <el-row :gutter="15">
            <!-- 左侧订单列表 65% -->
            <el-col :xs="24" :sm="24" :md="16" :lg="16">
              <div class="status-filters">
                <div v-for="status in statusList" :key="status.value"
                  :class="['status-filter-item', { active: currentStatus === status.value }]"
                  @click="handleStatusChange(status.value)">
                  <el-icon>
                    <component :is="status.icon" />
                  </el-icon>
                  <span>{{ $t('order.' + status.key) }}</span>
                </div>
              </div>

              <div class="orders-list" v-loading="loading">
                <el-empty v-if="!loading && orders.length === 0" :description="$t('common.noData')" :image-size="160" />

                <div v-for="order in orders" :key="order.id" class="order-card" @click="showOrderDetail(order)">
                  <div class="order-header">
                    <div class="order-number">
                      <el-icon>
                        <Tickets />
                      </el-icon>
                      <span class="value">{{ order.orderNumber || order.orderNo || order.id }}</span>
                    </div>
                    <el-tag :type="getStatusType(order.status)" size="default">
                      {{ getStatusText(order.status) }}
                    </el-tag>
                  </div>

                  <div class="order-content">
                    <div class="product-info" @click.stop="handleViewProduct(order.productId)">
                      <el-image :src="getProductImage(order.productImages)" fit="cover" class="product-image">
                        <template #error>
                          <div class="image-error">
                            <el-icon>
                              <Picture />
                            </el-icon>
                          </div>
                        </template>
                      </el-image>
                      <div class="product-details">
                        <div class="product-title">{{ locale === 'ko' && order.productTitleKo ? order.productTitleKo :
                          order.productTitle }}</div>
                        <div class="product-meta">
                          <el-tag size="small">{{ order.productCategory ? $t('categories.' + order.productCategory) :
                            $t('categories.others') }}</el-tag>
                          <span class="order-time">{{ formatTime(order.createTime) }}</span>
                        </div>
                      </div>
                    </div>
                    <div class="amount-info">
                      <div class="amount-label">{{ $t('order.orderAmount') }}</div>
                      <div class="amount-value">¥{{ order.amount }}</div>
                    </div>
                  </div>

                  <div class="order-footer">
                    <div class="buyer-info">
                      <el-avatar :size="28" :src="formatAvatar(order.buyerAvatar)">
                        <el-icon>
                          <User />
                        </el-icon>
                      </el-avatar>
                      <span class="buyer-name">{{ order.buyerName }}</span>
                    </div>

                    <div class="order-actions" @click.stop>
                      <el-button size="default" @click.stop="showOrderDetail(order)">
                        <el-icon>
                          <View />
                        </el-icon>
                        {{ $t('common.view') }}
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 自定义纯中文分页 -->
              <div class="pagination" v-if="total > 0">
                <div class="custom-pagination">
                  <span class="pagination-text">{{ $t('pagination.total') }} {{ total }} {{ $t('pagination.items')
                  }}</span>
                  <el-button :disabled="pageNum === 1" @click="prevPage">{{ $t('pagination.prev') }}</el-button>
                  <el-pagination v-model:current-page="pageNum" :total="total" :page-size="pageSize" layout="pager"
                    @current-change="handlePageChange" />
                  <el-button :disabled="pageNum >= totalPages" @click="nextPage">{{ $t('pagination.next') }}</el-button>
                </div>
              </div>
            </el-col>

            <!-- 右侧销售统计 35% -->
            <el-col :xs="24" :sm="24" :md="8" :lg="8">
              <div class="trade-stats-panel">
                <el-card class="stats-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <el-icon color="#67c23a">
                        <TrendCharts />
                      </el-icon>
                      <span>{{ $t('order.monthSellStats') }}</span>
                    </div>
                  </template>
                  <div class="month-stats">
                    <div class="month-stat-item">
                      <div class="stat-item-label">{{ $t('order.mySell') }}</div>
                      <div class="stat-item-value" style="color: #409eff;">{{ monthStats.sellCount }}</div>
                    </div>
                    <div class="month-stat-item">
                      <div class="stat-item-label">{{ $t('order.accumulatedIncome') }}</div>
                      <div class="stat-item-value" style="color: #67c23a;">¥{{ monthStats.sellAmount }}</div>
                    </div>
                    <div class="month-stat-item">
                      <div class="stat-item-label">{{ $t('order.avgOrderAmount') }}</div>
                      <div class="stat-item-value" style="color: #ff9800;">¥{{ monthStats.avgSellAmount }}</div>
                    </div>
                  </div>
                </el-card>

                <el-card class="quick-actions-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <el-icon color="#409eff">
                        <DataAnalysis />
                      </el-icon>
                      <span>{{ $t('order.sellOverview') }}</span>
                    </div>
                  </template>
                  <div class="quick-actions">
                    <div class="quick-action-item">
                      <el-icon :size="32" color="#67c23a">
                        <Goods />
                      </el-icon>
                      <div class="action-label">{{ $t('order.soldItems') }}</div>
                      <div class="action-count">{{ orderCounts.completed }}</div>
                    </div>
                    <div class="quick-action-item">
                      <el-icon :size="32" color="#ff9800">
                        <Money />
                      </el-icon>
                      <div class="action-label">{{ $t('order.totalIncome') }}</div>
                      <div class="action-count">¥{{ totalIncome }}</div>
                    </div>
                  </div>
                </el-card>
              </div>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 订单详情抽屉 -->
    <el-drawer v-model="showDetailDrawer" :title="$t('order.orderDetail')" direction="rtl" size="500px">
      <div class="order-detail" v-if="selectedOrder">
        <!-- 订单状态 -->
        <div class="detail-section">
          <div class="detail-status">
            <el-result :icon="getOrderStatusIcon(selectedOrder.status)" :title="getStatusText(selectedOrder.status)"
              :sub-title="`${$t('order.orderNo')}: ${selectedOrder.orderNumber || selectedOrder.orderNo || selectedOrder.id}`" />
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="detail-section">
          <div class="section-title">
            <el-icon>
              <Goods />
            </el-icon>
            <span>{{ $t('order.productInfo') }}</span>
          </div>
          <div class="detail-product">
            <el-image :src="getProductImage(selectedOrder.productImages)" fit="cover" class="detail-product-image">
            </el-image>
            <div class="detail-product-info">
              <div class="detail-product-title">{{ locale === 'ko' && selectedOrder.productTitleKo ?
                selectedOrder.productTitleKo : selectedOrder.productTitle }}</div>
              <div class="detail-product-price">¥{{ selectedOrder.amount }}</div>
            </div>
          </div>
        </div>

        <!-- 交易信息 -->
        <div class="detail-section">
          <div class="section-title">
            <el-icon>
              <InfoFilled />
            </el-icon>
            <span>{{ $t('order.tradeInfo') }}</span>
          </div>
          <div class="detail-info-list">
            <div class="info-item">
              <span class="info-label">{{ $t('order.orderNo') }}</span>
              <span class="info-value">{{ selectedOrder.orderNumber || selectedOrder.orderNo || selectedOrder.id
              }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">{{ $t('order.orderTime') }}</span>
              <span class="info-value">{{ formatTime(selectedOrder.createTime) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">{{ $t('common.status') }}</span>
              <el-tag :type="getStatusType(selectedOrder.status)">
                {{ getStatusText(selectedOrder.status) }}
              </el-tag>
            </div>
            <div class="info-item" v-if="activeTab === 'buyer'">
              <span class="info-label">{{ $t('product.seller') }}</span>
              <div class="info-value">
                <el-avatar :size="24" :src="formatAvatar(selectedOrder.sellerAvatar)">
                  <el-icon>
                    <User />
                  </el-icon>
                </el-avatar>
                <span style="margin-left: 8px;">{{ selectedOrder.sellerName }}</span>
              </div>
            </div>
            <div class="info-item" v-else>
              <span class="info-label">{{ $t('order.buyer') }}</span>
              <div class="info-value">
                <el-avatar :size="24" :src="formatAvatar(selectedOrder.buyerAvatar)">
                  <el-icon>
                    <User />
                  </el-icon>
                </el-avatar>
                <span style="margin-left: 8px;">{{ selectedOrder.buyerName }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 订单操作 -->
        <div class="detail-actions">
          <el-button v-if="selectedOrder.status === 0 && activeTab === 'buyer'" type="warning" size="large"
            @click="handlePay(selectedOrder)">
            <el-icon>
              <Wallet />
            </el-icon>
            {{ $t('common.pay') }}
          </el-button>
          <el-button v-if="selectedOrder.status === 1 && activeTab === 'buyer'" type="success" size="large"
            @click="handleConfirm(selectedOrder)">
            <el-icon><Select /></el-icon>
            {{ $t('common.confirmReceipt') }}
          </el-button>
          <el-button v-if="selectedOrder.status === 2 && activeTab === 'buyer'" type="primary" size="large"
            @click="handleReview(selectedOrder)">
            <el-icon>
              <Edit />
            </el-icon>
            {{ $t('common.review') }}
          </el-button>
          <el-button v-if="selectedOrder.status === 0 && activeTab === 'buyer'" size="large"
            @click="handleCancel(selectedOrder)">
            <el-icon>
              <Close />
            </el-icon>
            {{ $t('common.cancel') }}
          </el-button>
          <el-button size="large" @click="handleViewProduct(selectedOrder.productId)">
            <el-icon>
              <View />
            </el-icon>
            {{ $t('common.view') }}
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { orderApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Picture,
  User,
  View,
  ShoppingBag,
  ShoppingCart,
  Wallet,
  TrendCharts,
  Clock,
  Box,
  Comment,
  Operation,
  Bell,
  Tickets,
  Goods,
  InfoFilled,
  Select,
  Edit,
  Close,
  DataAnalysis,
  Money,
  List,
  Document,
  SuccessFilled
} from '@element-plus/icons-vue'


const router = useRouter()
const route = useRoute()
const { t, locale } = useI18n()
const activeTab = ref('buyer')
const currentStatus = ref(null)
const loading = ref(false)
const orders = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const showDetailDrawer = ref(false)
const selectedOrder = ref(null)

// 自定义分页计算
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const stats = ref([
  {
    key: 'totalOrders',
    value: '0',
    icon: ShoppingBag,
    color: '#409eff',
    bgColor: '#e6f0ff'
  },
  {
    key: 'pendingPay',
    value: '0',
    icon: Clock,
    color: '#ff9800',
    bgColor: '#fff3e0'
  },
  {
    key: 'pendingReceive',
    value: '0',
    icon: Box,
    color: '#67c23a',
    bgColor: '#e8f5e9'
  },
  {
    key: 'completed',
    value: '0',
    icon: SuccessFilled,
    color: '#5ac761',
    bgColor: '#f0f9ff'
  }
])

const statusList = [
  { key: 'all', value: null, icon: List, count: 0 },
  { key: 'pendingPay', value: 0, icon: Clock, count: 0 },
  { key: 'pendingReceive', value: 1, icon: Box, count: 0 },
  { key: 'pendingEvaluation', value: 2, icon: Comment, count: 0 },
  { key: 'completed', value: 3, icon: Document, count: 0 },
  { key: 'cancelled', value: 4, icon: Close, count: 0 }
]

const monthStats = ref({
  buyCount: 0,
  buyAmount: 0,
  avgAmount: 0,
  sellCount: 0,
  sellAmount: 0,
  avgSellAmount: 0
})

const orderCounts = ref({
  pending: 0,
  shipping: 0,
  evaluate: 0,
  completed: 0
})

const recentTrades = ref([
  { title: '订单ORD20260112001已支付', time: '2小时前', color: '#409eff' },
  { title: '订单ORD20260111003已确认收货', time: '5小时前', color: '#67c23a' },
  { title: '订单ORD20260110005已取消', time: '1天前', color: '#f56c6c' }
])

const totalIncome = computed(() => {
  return orders.value
    .filter(order => order.status === 3)
    .reduce((sum, order) => sum + order.amount, 0)
    .toFixed(2)
})

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

const getProductImage = (images) => {
  if (!images) return 'https://via.placeholder.com/300x200?text=No+Image'
  try {
    const imageArray = typeof images === 'string' ? JSON.parse(images) : images
    return imageArray[0] || 'https://via.placeholder.com/300x200?text=No+Image'
  } catch {
    return 'https://via.placeholder.com/300x200?text=No+Image'
  }
}

const formatAvatar = (avatar) => {
  return avatar || ''
}

const formatTime = (time) => {
  if (!time) return ''
  try {
    return new Date(time).toLocaleString('zh-CN')
  } catch {
    return time
  }
}

const getStatusText = (status) => {
  const statusMap = {
    0: t('order.pendingPay'),
    1: t('order.pendingReceive'),
    2: t('order.pendingEvaluation'),
    3: t('order.completed'),
    4: t('order.cancelled')
  }
  return statusMap[status] || t('common.noData')
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'info',
    4: 'info'
  }
  return typeMap[status] || 'info'
}

const getOrderStatusIcon = (status) => {
  if (status === 3) return 'success'
  if (status === 4) return 'error'
  return 'warning'
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      type: activeTab.value === 'buyer' ? 0 : 1
    }

    if (currentStatus.value !== null) {
      params.status = currentStatus.value
    }

    const res = await orderApi.getOrderList(params)
    if (res.code === '200') {
      orders.value = res.data.records || []
      total.value = res.data.total || 0
      updateStats()
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error(t('myOrders.fetchFailed'))
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  const allOrders = orders.value
  stats.value[0].value = String(total.value)
  stats.value[1].value = String(allOrders.filter(o => o.status === 0).length)
  stats.value[2].value = String(allOrders.filter(o => o.status === 1).length)
  stats.value[3].value = String(allOrders.filter(o => o.status === 3).length)

  orderCounts.value.pending = allOrders.filter(o => o.status === 0).length
  orderCounts.value.shipping = allOrders.filter(o => o.status === 1).length
  orderCounts.value.evaluate = allOrders.filter(o => o.status === 2).length
  orderCounts.value.completed = allOrders.filter(o => o.status === 3).length

  if (activeTab.value === 'buyer') {
    monthStats.value.buyCount = allOrders.filter(o => o.status !== 4).length
    monthStats.value.buyAmount = allOrders
      .filter(o => o.status !== 4)
      .reduce((sum, o) => sum + o.amount, 0)
      .toFixed(2)
    monthStats.value.avgAmount = monthStats.value.buyCount > 0
      ? (monthStats.value.buyAmount / monthStats.value.buyCount).toFixed(2)
      : '0.00'
  } else {
    monthStats.value.sellCount = allOrders.filter(o => o.status !== 4).length
    monthStats.value.sellAmount = allOrders
      .filter(o => o.status !== 4)
      .reduce((sum, o) => sum + o.amount, 0)
      .toFixed(2)
    monthStats.value.avgSellAmount = monthStats.value.sellCount > 0
      ? (monthStats.value.sellAmount / monthStats.value.sellCount).toFixed(2)
      : '0.00'
  }
}

const handleTabChange = () => {
  currentStatus.value = null
  pageNum.value = 1
  fetchOrders()
}

const handleStatusChange = (status) => {
  currentStatus.value = status
  pageNum.value = 1
  fetchOrders()
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchOrders()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleViewProduct = (productId) => {
  router.push(`/user/product/${productId}`)
}

const showOrderDetail = (order) => {
  selectedOrder.value = order
  showDetailDrawer.value = true
}

const handlePay = async (order) => {
  try {
    await ElMessageBox.confirm(t('order.confirmPay'), t('common.tip'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })

    const res = await orderApi.payOrder(order.id)
    if (res.code === '200') {
      ElMessage.success(t('common.save'))
      fetchOrders()
      showDetailDrawer.value = false
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || t('common.noData'))
    }
  }
}

const handleConfirm = async (order) => {
  try {
    await ElMessageBox.confirm(t('order.confirmReceived'), t('common.tip'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })

    const res = await orderApi.confirmOrder(order.id)
    if (res.code === '200') {
      ElMessage.success(t('common.save'))
      fetchOrders()
      showDetailDrawer.value = false
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || t('common.noData'))
    }
  }
}

const handleReview = (order) => {
  router.push({
    path: '/user/review',
    query: { orderId: order.id }
  })
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm(t('order.confirmCancel'), t('common.tip'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })

    const res = await orderApi.cancelOrder(order.id)
    if (res.code === '200') {
      ElMessage.success(t('common.save'))
      fetchOrders()
      showDetailDrawer.value = false
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || t('common.noData'))
    }
  }
}

onMounted(() => {
  const tabFromUrl = route.query.tab
  if (tabFromUrl === 'buyer' || tabFromUrl === 'seller') {
    activeTab.value = tabFromUrl
  }
  fetchOrders()
})
</script>

<style scoped>
.my-orders-page {
  min-height: calc(100vh - 110px);
  background: #f0f2f5;
  padding: 15px 0;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 15px;
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

/* 顶部统计条 */
.stats-bar {
  margin-bottom: 15px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
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
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 6px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

/* Tabs */
.orders-tabs {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 状态筛选 */
.status-filters {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.status-filter-item {
  padding: 10px 20px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
  position: relative;
}

.status-filter-item:hover {
  background: #e6f0ff;
  color: #409eff;
}

.status-filter-item.active {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

.filter-badge {
  margin-left: 4px;
}

/* 订单列表 */
.orders-list {
  min-height: 400px;
}

.order-card {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 12px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
  cursor: pointer;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #409eff;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f2f5;
}

.order-number {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.order-number .value {
  font-weight: 500;
  color: #303133;
}

.order-content {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 16px;
}

.product-info {
  display: flex;
  gap: 12px;
  flex: 1;
  cursor: pointer;
  transition: all 0.3s;
  padding: 8px;
  border-radius: 6px;
}

.product-info:hover {
  background: #f8f9fa;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  flex-shrink: 0;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
  font-size: 24px;
}

.product-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.product-title {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: #909399;
}

.amount-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  flex-shrink: 0;
}

.amount-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.amount-value {
  font-size: 20px;
  color: #f56c6c;
  font-weight: 700;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f2f5;
}

.seller-info,
.buyer-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.seller-name,
.buyer-name {
  font-weight: 500;
  color: #303133;
}

.order-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* 自定义分页样式 */
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
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

/* 右侧统计面板 */
.trade-stats-panel {
  position: sticky;
  top: 80px;
}

.stats-card,
.quick-actions-card,
.recent-trades-card {
  margin-bottom: 15px;
}

.stats-card:last-child,
.quick-actions-card:last-child,
.recent-trades-card:last-child {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
}

.month-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.month-stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item-label {
  font-size: 14px;
  color: #606266;
}

.stat-item-value {
  font-size: 20px;
  font-weight: 700;
}

.quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.quick-action-item {
  flex: 1;
  min-width: calc(50% - 6px);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 8px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-action-item:hover {
  background: #e6f0ff;
  transform: translateY(-2px);
}

.action-label {
  font-size: 13px;
  color: #606266;
}

.action-count {
  font-size: 18px;
  font-weight: 700;
  color: #409eff;
}

.recent-trades {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.trade-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}

.trade-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 6px;
}

.trade-content {
  flex: 1;
}

.trade-title {
  font-size: 13px;
  color: #303133;
  margin-bottom: 4px;
}

.trade-time {
  font-size: 12px;
  color: #909399;
}

/* 订单详情抽屉 */
.order-detail {
  padding: 10px 0;
}

.detail-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f2f5;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 16px;
}

.detail-status {
  text-align: center;
}

.detail-product {
  display: flex;
  gap: 16px;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
}

.detail-product-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  flex-shrink: 0;
}

.detail-product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.detail-product-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
}

.detail-product-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: 700;
}

.detail-info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-size: 14px;
  color: #909399;
}

.info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.detail-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 响应式 */
@media (max-width: 992px) {
  .page-container {
    padding: 0 12px;
  }

  .stats-bar {
    display: flex;
    flex-wrap: wrap;
  }

  .stats-bar .el-col {
    width: 50% !important;
  }

  .trade-stats-panel {
    position: static;
    margin-top: 15px;
  }
}

@media (max-width: 768px) {
  .stats-bar .el-col {
    width: 100% !important;
  }

  .status-filters {
    overflow-x: auto;
    flex-wrap: nowrap;
  }

  .order-content {
    flex-direction: column;
  }

  .amount-info {
    align-items: flex-start;
  }

  .order-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .order-actions {
    width: 100%;
    justify-content: stretch;
  }

  .order-actions .el-button {
    flex: 1;
  }
}
</style>
