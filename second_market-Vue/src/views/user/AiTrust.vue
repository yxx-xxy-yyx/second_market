<template>
  <div class="ai-trust-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <button class="back-btn" @click="router.back()">
        <ArrowLeftOutlined />
      </button>
      <h1 class="title">AI智能托管</h1>
      <div class="header-badge">
        <span class="badge-text">智能服务</span>
      </div>
    </div>

    <!-- 功能介绍 -->
    <div class="intro-section">
      <div class="intro-card">
        <div class="intro-icon">
          <RobotOutlined />
        </div>
        <h2>让AI帮您管理商品</h2>
        <p>开启智能托管后，AI将自动帮您：</p>
        <ul class="feature-list">
          <li><CheckOutlined /> 自动回复买家咨询</li>
          <li><CheckOutlined /> 智能议价与调价</li>
          <li><CheckOutlined /> 推荐最佳交易时机</li>
          <li><CheckOutlined /> 风险预警与提醒</li>
        </ul>
      </div>
    </div>

    <!-- 商品选择 -->
    <div class="product-section">
      <div class="section-header">
        <h3>选择要托管的商品</h3>
        <span class="product-count">共 {{ myProducts.length }} 个商品</span>
      </div>
      <div class="product-list">
        <div 
          class="product-item" 
          v-for="product in myProducts" 
          :key="product.id"
          :class="{ selected: selectedProducts.includes(product.id) }"
          @click="toggleProduct(product.id)"
        >
          <img :src="product.images?.[0] || defaultImage" class="product-image" />
          <div class="product-info">
            <span class="product-name">{{ product.name }}</span>
            <span class="product-price">¥{{ product.price }}</span>
            <span class="product-status" :class="getStatusClass(product.status)">
              {{ getStatusText(product.status) }}
            </span>
          </div>
          <div class="select-checkbox">
            <CheckOutlined v-if="selectedProducts.includes(product.id)" />
          </div>
        </div>
      </div>
    </div>

    <!-- 托管设置 -->
    <div class="settings-section" v-if="selectedProducts.length > 0">
      <div class="section-header">
        <h3>托管设置</h3>
      </div>
      <div class="settings-card">
        <div class="setting-item">
          <div class="setting-label">
            <span class="label-text">自动回复</span>
            <span class="label-desc">AI自动回复买家咨询</span>
          </div>
          <div class="setting-control">
            <input type="checkbox" v-model="trustSettings.autoReply" class="toggle-checkbox" />
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-label">
            <span class="label-text">智能议价</span>
            <span class="label-desc">在价格范围内自动议价</span>
          </div>
          <div class="setting-control">
            <input type="checkbox" v-model="trustSettings.smartBargain" class="toggle-checkbox" />
          </div>
        </div>
        <div class="setting-item" v-if="trustSettings.smartBargain">
          <div class="setting-label">
            <span class="label-text">最低价格</span>
            <span class="label-desc">议价最低可接受价格</span>
          </div>
          <div class="setting-control">
            <input type="number" v-model="trustSettings.minPrice" class="price-input" placeholder="最低价格" />
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-label">
            <span class="label-text">自动调价</span>
            <span class="label-desc">根据市场行情自动调整价格</span>
          </div>
          <div class="setting-control">
            <input type="checkbox" v-model="trustSettings.autoAdjustPrice" class="toggle-checkbox" />
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-label">
            <span class="label-text">风险提醒</span>
            <span class="label-desc">检测可疑买家并提醒</span>
          </div>
          <div class="setting-control">
            <input type="checkbox" v-model="trustSettings.riskAlert" class="toggle-checkbox" />
          </div>
        </div>
      </div>
    </div>

    <!-- 开启托管按钮 -->
    <div class="action-section">
      <button 
        class="start-trust-btn" 
        :disabled="selectedProducts.length === 0 || loading"
        @click="startTrust"
      >
        <RobotOutlined />
        {{ loading ? '正在开启...' : '开启智能托管' }}
      </button>
      <p class="action-tip">已选择 {{ selectedProducts.length }} 个商品</p>
    </div>

    <!-- 托管状态展示 -->
    <div class="trust-status-section" v-if="trustResult">
      <div class="status-card success" v-if="trustResult.success">
        <div class="status-icon">
          <CheckCircleOutlined />
        </div>
        <h3>智能托管已开启</h3>
        <p>{{ trustResult.message }}</p>
        <div class="trust-details">
          <div class="detail-item">
            <span class="detail-label">托管商品</span>
            <span class="detail-value">{{ trustResult.productCount }} 个</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">托管时长</span>
            <span class="detail-value">{{ trustResult.duration }} 天</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">AI响应速度</span>
            <span class="detail-value">{{ trustResult.responseSpeed }}</span>
          </div>
        </div>
      </div>
      <div class="status-card error" v-else>
        <div class="status-icon">
          <CloseCircleOutlined />
        </div>
        <h3>开启失败</h3>
        <p>{{ trustResult.message }}</p>
      </div>
    </div>

    <!-- 加载状态 -->
    <div class="loading-overlay" v-if="loading">
      <div class="loading-spinner"></div>
      <span>正在配置智能托管...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ArrowLeftOutlined, RobotOutlined, CheckOutlined, 
  CheckCircleOutlined, CloseCircleOutlined
} from '@ant-design/icons-vue'
import { productApi } from '@/api/product'
import { aiApi } from '@/api/ai'
import { message } from 'ant-design-vue'

const router = useRouter()
const defaultImage = 'https://via.placeholder.com/80x80?text=商品'

const loading = ref(false)
const myProducts = ref([])
const selectedProducts = ref([])
const trustSettings = ref({
  autoReply: true,
  smartBargain: true,
  minPrice: 0,
  autoAdjustPrice: false,
  riskAlert: true
})
const trustResult = ref(null)

// 获取状态样式类
const getStatusClass = (status) => {
  const classes = {
    1: 'on-sale',
    2: 'sold',
    3: 'off-shelf'
  }
  return classes[status] || 'unknown'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    1: '在售',
    2: '已售',
    3: '下架'
  }
  return texts[status] || '未知'
}

// 切换商品选择
const toggleProduct = (productId) => {
  const index = selectedProducts.value.indexOf(productId)
  if (index > -1) {
    selectedProducts.value.splice(index, 1)
  } else {
    selectedProducts.value.push(productId)
  }
}

// 加载我的商品
const loadMyProducts = async () => {
  try {
    const res = await productApi.getMyProducts({ pageNum: 1, pageSize: 20 })
    if (res.code === '200' && res.data) {
      myProducts.value = res.data.records?.filter(p => p.status === 1) || []
    }
  } catch (e) {
    console.error('加载商品失败', e)
  }
}

// 开启智能托管
const startTrust = async () => {
  if (selectedProducts.value.length === 0) {
    message.warning('请选择要托管的商品')
    return
  }

  loading.value = true
  trustResult.value = null
  try {
    const res = await aiApi.intelligentTrust({
      productIds: selectedProducts.value,
      settings: trustSettings.value
    })
    if (res.code === '200' && res.data) {
      trustResult.value = {
        success: true,
        message: '智能托管服务已成功开启',
        productCount: selectedProducts.value.length,
        duration: 30,
        responseSpeed: '< 5秒',
        ...res.data
      }
      message.success('智能托管开启成功')
    } else {
      trustResult.value = {
        success: false,
        message: res.message || '开启失败，请稍后重试'
      }
      message.error('开启失败')
    }
  } catch (e) {
    trustResult.value = {
      success: false,
      message: '服务异常，请稍后重试'
    }
    message.error('服务异常')
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  loadMyProducts()
})
</script>

<style scoped>
.ai-trust-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0f4ff 0%, #e8f0fe 100%);
  padding: 24px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.back-btn {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  border: none;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #374151;
}

.title {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a2e;
}

.header-badge {
  padding: 6px 12px;
  background: linear-gradient(135deg, #4f46e5 0%, #818cf8 100%);
  border-radius: 8px;
}

.badge-text {
  font-size: 12px;
  color: white;
  font-weight: 500;
}

/* 功能介绍 */
.intro-section {
  margin-bottom: 24px;
}

.intro-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
}

.intro-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #4f46e5 0%, #818cf8 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 28px;
  color: white;
}

.intro-card h2 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.intro-card p {
  font-size: 14px;
  color: #6b7280;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin-top: 16px;
}

.feature-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  font-size: 14px;
  color: #374151;
}

.feature-list li .anticon {
  color: #10b981;
}

/* 商品选择 */
.product-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.product-count {
  font-size: 13px;
  color: #6b7280;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;
}

.product-item:hover {
  background: #f3f4f6;
}

.product-item.selected {
  border-color: #4f46e5;
  background: #eff6ff;
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
}

.product-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  margin-left: 8px;
}

.product-status.on-sale {
  background: #d1fae5;
  color: #10b981;
}

.product-status.sold {
  background: #dbeafe;
  color: #3b82f6;
}

.product-status.off-shelf {
  background: #f3f4f6;
  color: #6b7280;
}

.select-checkbox {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  background: #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-item.selected .select-checkbox {
  background: #4f46e5;
  color: white;
}

/* 托管设置 */
.settings-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
}

.settings-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e5e7eb;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-label {
  flex: 1;
}

.label-text {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
  display: block;
}

.label-desc {
  font-size: 12px;
  color: #6b7280;
}

.toggle-checkbox {
  width: 48px;
  height: 24px;
  appearance: none;
  background: #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
}

.toggle-checkbox:checked {
  background: #4f46e5;
}

.toggle-checkbox::before {
  content: '';
  width: 20px;
  height: 20px;
  background: white;
  border-radius: 10px;
  position: absolute;
  top: 2px;
  left: 2px;
  transition: left 0.2s;
}

.toggle-checkbox:checked::before {
  left: 26px;
}

.price-input {
  width: 120px;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
}

/* 操作按钮 */
.action-section {
  text-align: center;
  margin-bottom: 24px;
}

.start-trust-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 32px;
  background: linear-gradient(135deg, #4f46e5 0%, #818cf8 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
}

.start-trust-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(79, 70, 229, 0.3);
}

.start-trust-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.action-tip {
  font-size: 13px;
  color: #6b7280;
  margin-top: 12px;
}

/* 托管状态 */
.trust-status-section {
  margin-bottom: 24px;
}

.status-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
}

.status-card.success {
  border: 2px solid #10b981;
}

.status-card.error {
  border: 2px solid #ef4444;
}

.status-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 32px;
}

.status-card.success .status-icon {
  background: #d1fae5;
  color: #10b981;
}

.status-card.error .status-icon {
  background: #fee2e2;
  color: #ef4444;
}

.status-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}

.status-card p {
  font-size: 14px;
  color: #6b7280;
}

.trust-details {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 24px;
}

.detail-item {
  text-align: center;
}

.detail-label {
  font-size: 12px;
  color: #6b7280;
}

.detail-value {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
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

@media (max-width: 640px) {
  .ai-trust-page {
    padding: 16px;
  }
  
  .trust-details {
    grid-template-columns: 1fr;
  }
}
</style>