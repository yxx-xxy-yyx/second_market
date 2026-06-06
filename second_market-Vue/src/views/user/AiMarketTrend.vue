<template>
  <div class="ai-market-page">
    <!-- 移动端视图 -->
    <template v-if="isMobileScreen">
      <div class="mobile-container">
        <div class="mobile-header">
          <el-icon class="back-icon" @click="router.back()">
            <ArrowLeft />
          </el-icon>
          <span class="header-title">AI行情参考</span>
        </div>

        <div class="mobile-content">
          <!-- 搜索区域 -->
          <div class="search-section">
            <el-form :model="form" label-position="top">
              <el-form-item label="商品分类">
                <el-select v-model="form.category" placeholder="请选择分类" class="w-full">
                  <el-option label="全部" value="" />
                  <el-option label="电子产品" value="electronics" />
                  <el-option label="服装配饰" value="clothing" />
                  <el-option label="书籍" value="books" />
                  <el-option label="其他" value="others" />
                </el-select>
              </el-form-item>
              <el-form-item label="品牌">
                <el-input v-model="form.brand" placeholder="请输入品牌 (可选)" />
              </el-form-item>
              <el-form-item label="型号">
                <el-input v-model="form.model" placeholder="请输入型号 (可选)" />
              </el-form-item>
              <el-form-item label="商品名称">
                <el-input v-model="form.productName" placeholder="请输入商品名称 (可选)" />
              </el-form-item>
              <el-form-item label="时间范围">
                <el-select v-model="form.days" placeholder="请选择时间范围" class="w-full">
                  <el-option label="最近7天" :value="7" />
                  <el-option label="最近30天" :value="30" />
                  <el-option label="最近90天" :value="90" />
                </el-select>
              </el-form-item>
            </el-form>

            <el-button type="primary" size="large" :loading="loading" class="search-btn" @click="handleSearch">
              {{ loading ? '分析中...' : '查看行情' }}
            </el-button>
          </div>

          <!-- 行情结果 -->
          <div v-if="result" class="result-section">
            <!-- 价格概览 -->
            <div class="price-overview">
              <div class="overview-card">
                <div class="overview-label">平均价格</div>
                <div class="overview-value">{{ formatPrice(result.averagePrice) }}</div>
              </div>
              <div class="overview-card">
                <div class="overview-label">最低价格</div>
                <div class="overview-value lowest">{{ formatPrice(result.lowestPrice) }}</div>
              </div>
              <div class="overview-card">
                <div class="overview-label">最高价格</div>
                <div class="overview-value highest">{{ formatPrice(result.highestPrice) }}</div>
              </div>
              <div class="overview-card">
                <div class="overview-label">成交量</div>
                <div class="overview-value">{{ result.totalSold }}件</div>
              </div>
            </div>

            <!-- 价格趋势图占位 -->
            <div class="chart-section">
              <div class="section-title">价格趋势</div>
              <div class="chart-placeholder">
                <el-icon class="chart-icon"><TrendCharts /></el-icon>
                <div class="chart-tip">价格趋势图表</div>
                <div class="trend-data">
                  <div v-for="(item, index) in result.priceTrend.slice(-5)" :key="index" class="trend-item">
                    <span class="trend-date">{{ item.date }}</span>
                    <span class="trend-price">{{ formatPrice(item.price) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 相似商品 -->
            <div class="similar-section">
              <div class="section-title">相似商品参考</div>
              <div v-for="(product, index) in result.similarProducts" :key="index" class="similar-card">
                <div class="similar-name">{{ product.name }}</div>
                <div class="similar-price">{{ formatPrice(product.price) }}</div>
                <div class="similar-condition">成色: {{ product.condition }}/10</div>
              </div>
            </div>

            <!-- AI建议 -->
            <div class="ai-suggestion">
              <div class="suggestion-title">
                <el-icon><MagicStick /></el-icon>
                <span>AI智能建议</span>
              </div>
              <div class="suggestion-card">
                <div class="suggestion-item">
                  <div class="suggestion-label">定价建议</div>
                  <div class="suggestion-content">{{ result.priceSuggestion }}</div>
                </div>
                <div class="suggestion-item">
                  <div class="suggestion-label">交易建议</div>
                  <div class="suggestion-content">{{ result.recommendation }}</div>
                </div>
                <div class="suggestion-item">
                  <div class="suggestion-label">市场展望</div>
                  <div class="suggestion-content">{{ result.marketOutlook }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 桌面端视图 -->
    <template v-else>
      <div class="desktop-container">
        <el-row :gutter="24">
          <!-- 左侧搜索区 -->
          <el-col :span="6">
            <el-card class="search-card">
              <template #header>
                <div class="card-header">
                  <h3>搜索条件</h3>
                </div>
              </template>
              <el-form :model="form" label-position="top">
                <el-form-item label="商品分类">
                  <el-select v-model="form.category" placeholder="请选择分类" class="w-full">
                    <el-option label="全部" value="" />
                    <el-option label="电子产品" value="electronics" />
                    <el-option label="服装配饰" value="clothing" />
                    <el-option label="书籍" value="books" />
                    <el-option label="其他" value="others" />
                  </el-select>
                </el-form-item>
                <el-form-item label="品牌">
                  <el-input v-model="form.brand" placeholder="请输入品牌 (可选)" />
                </el-form-item>
                <el-form-item label="型号">
                  <el-input v-model="form.model" placeholder="请输入型号 (可选)" />
                </el-form-item>
                <el-form-item label="商品名称">
                  <el-input v-model="form.productName" placeholder="请输入商品名称 (可选)" />
                </el-form-item>
                <el-form-item label="时间范围">
                  <el-select v-model="form.days" placeholder="请选择时间范围" class="w-full">
                    <el-option label="最近7天" :value="7" />
                    <el-option label="最近30天" :value="30" />
                    <el-option label="最近90天" :value="90" />
                  </el-select>
                </el-form-item>
              </el-form>
              <el-button type="primary" size="large" :loading="loading" class="search-btn-full" @click="handleSearch">
                {{ loading ? '分析中...' : '查看行情' }}
              </el-button>
            </el-card>
          </el-col>

          <!-- 右侧结果区 -->
          <el-col :span="18">
            <div v-if="result" class="result-desktop">
              <!-- 价格概览 -->
              <el-row :gutter="16" class="price-overview-row">
                <el-col :span="6">
                  <div class="overview-card-large">
                    <div class="overview-label">平均价格</div>
                    <div class="overview-value">{{ formatPrice(result.averagePrice) }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="overview-card-large">
                    <div class="overview-label">最低价格</div>
                    <div class="overview-value lowest">{{ formatPrice(result.lowestPrice) }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="overview-card-large">
                    <div class="overview-label">最高价格</div>
                    <div class="overview-value highest">{{ formatPrice(result.highestPrice) }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="overview-card-large">
                    <div class="overview-label">成交量</div>
                    <div class="overview-value">{{ result.totalSold }}件</div>
                  </div>
                </el-col>
              </el-row>

              <el-row :gutter="24" class="content-row">
                <!-- 价格趋势 -->
                <el-col :span="16">
                  <el-card class="chart-card">
                    <template #header>
                      <div class="card-header">
                        <h3>价格趋势</h3>
                      </div>
                    </template>
                    <div class="trend-table">
                      <el-table :data="result.priceTrend" style="width: 100%">
                        <el-table-column prop="date" label="日期" width="120" />
                        <el-table-column prop="price" label="价格">
                          <template #default="scope">
                            {{ formatPrice(scope.row.price) }}
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                  </el-card>
                </el-col>

                <!-- AI建议 -->
                <el-col :span="8">
                  <el-card class="suggestion-card">
                    <template #header>
                      <div class="card-header">
                        <h3>
                          <el-icon class="ai-icon"><MagicStick /></el-icon>
                          AI智能建议
                        </h3>
                      </div>
                    </template>
                    <div class="suggestion-content">
                      <div class="suggestion-item">
                        <div class="suggestion-label">定价建议</div>
                        <div class="suggestion-text">{{ result.priceSuggestion }}</div>
                      </div>
                      <div class="suggestion-item">
                        <div class="suggestion-label">交易建议</div>
                        <div class="suggestion-text">{{ result.recommendation }}</div>
                      </div>
                      <div class="suggestion-item">
                        <div class="suggestion-label">市场展望</div>
                        <div class="suggestion-text">{{ result.marketOutlook }}</div>
                      </div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>

              <!-- 相似商品 -->
              <el-card class="similar-card">
                <template #header>
                  <div class="card-header">
                    <h3>相似商品参考</h3>
                  </div>
                </template>
                <el-row :gutter="16">
                  <el-col :span="8" v-for="(product, index) in result.similarProducts" :key="index">
                    <div class="similar-product-card">
                      <div class="product-name">{{ product.name }}</div>
                      <div class="product-price">{{ formatPrice(product.price) }}</div>
                      <div class="product-condition">成色: {{ product.condition }}/10</div>
                    </div>
                  </el-col>
                </el-row>
              </el-card>
            </div>

            <div v-else class="empty-state">
              <el-empty description="输入搜索条件查看市场行情" />
            </div>
          </el-col>
        </el-row>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { aiApi } from '@/api/ai'
import { ArrowLeft, TrendCharts, MagicStick } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const { isMobileScreen } = useDeviceType()

const loading = ref(false)
const result = ref(null)

const form = ref({
  category: '',
  brand: '',
  model: '',
  productName: '',
  days: 7
})

const handleSearch = async () => {
  loading.value = true
  try {
    const res = await aiApi.getMarketTrend(form.value)

    if (res.code === '200' || res.success) {
      result.value = res.data
      ElMessage.success('获取行情成功')
    } else {
      ElMessage.error(res.message || '获取行情失败')
    }
  } catch (error) {
    console.error('获取行情失败:', error)
    ElMessage.error('获取行情失败，请重试')
  } finally {
    loading.value = false
  }
}

const formatPrice = (price) => {
  if (price === null || price === undefined) return '-'
  return `¥${price.toFixed(2)}`
}
</script>

<style scoped>
.ai-market-page {
  min-height: 100vh;
  background: var(--bg-color);
}

/* 移动端样式 */
.mobile-container {
  padding-bottom: 80px;
}

.mobile-header {
  display: flex;
  align-items: center;
  padding: 16px;
  background: white;
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-icon {
  font-size: 20px;
  margin-right: 12px;
  cursor: pointer;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
}

.mobile-content {
  padding: 16px;
}

.search-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: var(--card-shadow);
}

.w-full {
  width: 100%;
}

.search-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 8px;
}

.result-section {
  margin-top: 16px;
}

.price-overview {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.overview-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  box-shadow: var(--card-shadow);
}

.overview-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.overview-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-color);
}

.overview-value.lowest {
  color: #10b981;
}

.overview-value.highest {
  color: #ef4444;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--text-primary);
}

.chart-section,
.similar-section,
.ai-suggestion {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: var(--card-shadow);
}

.chart-placeholder {
  background: linear-gradient(135deg, #f0f9ff 0%, #f0fdfa 100%);
  border-radius: 12px;
  padding: 32px;
  text-align: center;
}

.chart-icon {
  font-size: 48px;
  color: var(--primary-color);
  margin-bottom: 12px;
}

.chart-tip {
  font-size: 14px;
  color: var(--text-secondary);
}

.trend-data {
  margin-top: 20px;
}

.trend-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid var(--border-light);
}

.trend-date {
  font-size: 14px;
  color: var(--text-secondary);
}

.trend-price {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.similar-card {
  padding: 16px;
  background: var(--bg-color);
  border-radius: 12px;
  margin-bottom: 12px;
}

.similar-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.similar-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 4px;
}

.similar-condition {
  font-size: 12px;
  color: var(--text-secondary);
}

.suggestion-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.suggestion-card {
  background: linear-gradient(135deg, #fef3c7 0%, #fce7f3 100%);
  border-radius: 12px;
  padding: 20px;
}

.suggestion-item {
  margin-bottom: 16px;
}

.suggestion-item:last-child {
  margin-bottom: 0;
}

.suggestion-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.suggestion-content {
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.6;
}

/* 桌面端样式 */
.desktop-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 24px;
}

.search-card {
  border-radius: 16px;
  box-shadow: var(--card-shadow);
}

.search-btn-full {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 16px;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.result-desktop {
  width: 100%;
}

.price-overview-row {
  margin-bottom: 24px;
}

.overview-card-large {
  background: linear-gradient(135deg, #f0fdfa 0%, #f0f9ff 100%);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  box-shadow: var(--card-shadow);
}

.content-row {
  margin-bottom: 24px;
}

.chart-card,
.suggestion-card,
.similar-card {
  border-radius: 16px;
  box-shadow: var(--card-shadow);
  height: 100%;
}

.ai-icon {
  color: var(--primary-color);
  margin-right: 8px;
}

.suggestion-content {
  padding: 8px 0;
}

.suggestion-item {
  margin-bottom: 20px;
}

.suggestion-item:last-child {
  margin-bottom: 0;
}

.suggestion-text {
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.8;
}

.similar-product-card {
  background: var(--bg-color);
  border-radius: 12px;
  padding: 16px;
  text-align: center;
}

.product-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.product-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 4px;
}

.product-condition {
  font-size: 12px;
  color: var(--text-secondary);
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 500px;
}
</style>
