<template>
  <div class="ai-authenticate-page">
    <!-- 移动端视图 -->
    <template v-if="isMobileScreen">
      <div class="mobile-container">
        <div class="mobile-header">
          <el-icon class="back-icon" @click="router.back()">
            <ArrowLeft />
          </el-icon>
          <span class="header-title">AI鉴定质检</span>
        </div>

        <div class="mobile-content">
          <!-- 图片上传区域 -->
          <div class="upload-section">
            <div class="section-title">上传商品图片</div>
            <el-upload
              v-model:file-list="fileList"
              :action="uploadAction"
              :headers="uploadHeaders"
              list-type="picture-card"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              :limit="5"
              class="upload-area"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">建议上传多角度清晰照片，AI将自动识别商品真伪和成色</div>
          </div>

          <!-- 商品信息补充 -->
          <div class="info-section">
            <div class="section-title">补充商品信息 (可选)</div>
            <el-form :model="form" label-position="top">
              <el-form-item label="商品名称">
                <el-input v-model="form.productName" placeholder="请输入商品名称" />
              </el-form-item>
              <el-form-item label="品牌">
                <el-input v-model="form.brand" placeholder="请输入品牌" />
              </el-form-item>
              <el-form-item label="型号">
                <el-input v-model="form.model" placeholder="请输入型号" />
              </el-form-item>
              <el-form-item label="分类">
                <el-select v-model="form.category" placeholder="请选择分类" class="w-full">
                  <el-option label="电子产品" value="electronics" />
                  <el-option label="服装配饰" value="clothing" />
                  <el-option label="书籍" value="books" />
                  <el-option label="其他" value="others" />
                </el-select>
              </el-form-item>
              <el-form-item label="补充说明">
                <el-input
                  v-model="form.additionalInfo"
                  type="textarea"
                  :rows="3"
                  placeholder="描述商品购买渠道、使用情况等"
                />
              </el-form-item>
            </el-form>
          </div>

          <!-- 鉴定按钮 -->
          <el-button
            type="primary"
            size="large"
            :loading="analyzing"
            :disabled="imageUrls.length === 0"
            class="analyze-btn"
            @click="handleAnalyze"
          >
            {{ analyzing ? 'AI鉴定中...' : '开始AI鉴定' }}
          </el-button>

          <!-- 鉴定结果 -->
          <div v-if="result" class="result-section">
            <div class="result-card">
              <div class="result-header">
                <div class="authenticity-badge" :class="result.isAuthentic ? 'authentic' : 'fake'">
                  <el-icon v-if="result.isAuthentic"><CircleCheck /></el-icon>
                  <el-icon v-else><CircleClose /></el-icon>
                  <span>{{ result.isAuthentic ? '鉴定为正品' : '存疑' }}</span>
                </div>
                <div class="score-display">
                  <span class="score-label">真伪评分</span>
                  <span class="score-value">{{ (result.authenticityScore * 100).toFixed(0) }}%</span>
                </div>
              </div>

              <div class="condition-section">
                <div class="condition-grade">{{ result.conditionGrade }}</div>
                <div class="condition-score">成色评分: {{ result.conditionScore }}/10</div>
                <div class="condition-details">
                  <div v-for="(detail, index) in result.conditionDetails" :key="index" class="detail-item">
                    <el-icon><Check /></el-icon>
                    <span>{{ detail }}</span>
                  </div>
                </div>
              </div>

              <div class="report-summary">
                <div class="summary-title">鉴定报告摘要</div>
                <p>{{ result.reportSummary }}</p>
              </div>

              <div class="recommendations">
                <div class="recommend-title">AI建议</div>
                <div v-for="(rec, index) in result.recommendations" :key="index" class="recommend-item">
                  {{ rec }}
                </div>
              </div>

              <div class="report-footer">
                <span class="report-id">报告编号: {{ result.reportId }}</span>
                <span class="report-time">{{ formatTime(result.reportTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 桌面端视图 -->
    <template v-else>
      <div class="desktop-container">
        <el-card class="main-card">
          <template #header>
            <div class="card-header">
              <h2>AI鉴定质检</h2>
              <p>上传商品图片，AI将为您鉴定真伪并评估成色</p>
            </div>
          </template>

          <el-row :gutter="24">
            <el-col :span="12">
              <div class="section-title">上传商品图片</div>
              <el-upload
                v-model:file-list="fileList"
                :action="uploadAction"
                :headers="uploadHeaders"
                list-type="picture-card"
                :on-success="handleUploadSuccess"
                :on-remove="handleRemove"
                :limit="5"
                class="upload-area-large"
              >
                <el-icon><Plus /></el-icon>
              </el-upload>
              <div class="upload-tip">建议上传多角度清晰照片，AI将自动识别商品真伪和成色</div>

              <div class="form-section">
                <el-form :model="form" label-position="top">
                  <el-row :gutter="16">
                    <el-col :span="12">
                      <el-form-item label="商品名称">
                        <el-input v-model="form.productName" placeholder="请输入商品名称" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="品牌">
                        <el-input v-model="form.brand" placeholder="请输入品牌" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row :gutter="16">
                    <el-col :span="12">
                      <el-form-item label="型号">
                        <el-input v-model="form.model" placeholder="请输入型号" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="分类">
                        <el-select v-model="form.category" placeholder="请选择分类" class="w-full">
                          <el-option label="电子产品" value="electronics" />
                          <el-option label="服装配饰" value="clothing" />
                          <el-option label="书籍" value="books" />
                          <el-option label="其他" value="others" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-form-item label="补充说明">
                    <el-input
                      v-model="form.additionalInfo"
                      type="textarea"
                      :rows="3"
                      placeholder="描述商品购买渠道、使用情况等"
                    />
                  </el-form-item>
                </el-form>
              </div>

              <el-button
                type="primary"
                size="large"
                :loading="analyzing"
                :disabled="imageUrls.length === 0"
                class="analyze-btn-large"
                @click="handleAnalyze"
              >
                {{ analyzing ? 'AI鉴定中...' : '开始AI鉴定' }}
              </el-button>
            </el-col>

            <el-col :span="12">
              <div v-if="result" class="result-desktop">
                <div class="result-card-large">
                  <div class="result-header">
                    <div class="authenticity-badge" :class="result.isAuthentic ? 'authentic' : 'fake'">
                      <el-icon v-if="result.isAuthentic"><CircleCheck /></el-icon>
                      <el-icon v-else><CircleClose /></el-icon>
                      <span>{{ result.isAuthentic ? '鉴定为正品' : '存疑' }}</span>
                    </div>
                    <div class="score-display">
                      <span class="score-label">真伪评分</span>
                      <span class="score-value">{{ (result.authenticityScore * 100).toFixed(0) }}%</span>
                    </div>
                  </div>

                  <div class="condition-section">
                    <div class="condition-grade">{{ result.conditionGrade }}</div>
                    <div class="condition-score">成色评分: {{ result.conditionScore }}/10</div>
                    <div class="condition-details">
                      <div v-for="(detail, index) in result.conditionDetails" :key="index" class="detail-item">
                        <el-icon><Check /></el-icon>
                        <span>{{ detail }}</span>
                      </div>
                    </div>
                  </div>

                  <div class="report-summary">
                    <div class="summary-title">鉴定报告摘要</div>
                    <p>{{ result.reportSummary }}</p>
                  </div>

                  <div class="recommendations">
                    <div class="recommend-title">AI建议</div>
                    <div v-for="(rec, index) in result.recommendations" :key="index" class="recommend-item">
                      {{ rec }}
                    </div>
                  </div>

                  <div class="report-footer">
                    <span class="report-id">报告编号: {{ result.reportId }}</span>
                    <span class="report-time">{{ formatTime(result.reportTime) }}</span>
                  </div>
                </div>
              </div>
              <div v-else class="empty-state">
                <el-empty description="鉴定结果将显示在这里" />
              </div>
            </el-col>
          </el-row>
        </el-card>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { useUserStore } from '@/stores/user'
import { aiApi } from '@/api/ai'
import { ArrowLeft, Plus, CircleCheck, CircleClose, Check } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const { isMobileScreen } = useDeviceType()

const analyzing = ref(false)
const fileList = ref([])
const imageUrls = ref([])
const result = ref(null)

const form = ref({
  productName: '',
  brand: '',
  model: '',
  category: '',
  additionalInfo: ''
})

const apiBaseUrl = computed(() => (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/+$/, ''))
const uploadAction = computed(() => `${apiBaseUrl.value}/file/upload`)
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token') || userStore.token
  return { Authorization: token ? `Bearer ${token}` : '' }
})

const handleUploadSuccess = (res) => {
  if (res.code === '200') {
    const url = res?.data?.fileUrl || res?.data?.url
    if (url) {
      imageUrls.value.push(url)
    }
  }
}

const handleRemove = (file) => {
  const url = file.response?.data?.fileUrl || file.response?.data?.url || file.url
  imageUrls.value = imageUrls.value.filter(u => u !== url)
}

const handleAnalyze = async () => {
  if (imageUrls.value.length === 0) {
    ElMessage.warning('请先上传商品图片')
    return
  }

  analyzing.value = true
  try {
    const res = await aiApi.authenticateProduct({
      imageUrls: imageUrls.value,
      ...form.value
    })

    if (res.code === '200' || res.success) {
      result.value = res.data
      ElMessage.success('鉴定完成')
    } else {
      ElMessage.error(res.message || '鉴定失败')
    }
  } catch (error) {
    console.error('AI鉴定失败:', error)
    ElMessage.error('鉴定失败，请重试')
  } finally {
    analyzing.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}
</script>

<style scoped>
.ai-authenticate-page {
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

.upload-section,
.info-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: var(--card-shadow);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.upload-area {
  width: 100%;
}

.upload-tip {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 12px;
}

.w-full {
  width: 100%;
}

.analyze-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.result-section {
  margin-top: 20px;
}

.result-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--card-shadow);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.authenticity-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

.authenticity-badge.authentic {
  background: #d1fae5;
  color: #065f46;
}

.authenticity-badge.fake {
  background: #fee2e2;
  color: #991b1b;
}

.score-display {
  text-align: right;
}

.score-label {
  display: block;
  font-size: 12px;
  color: var(--text-secondary);
}

.score-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary-color);
}

.condition-section {
  text-align: center;
  padding: 20px 0;
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 20px;
}

.condition-grade {
  font-size: 32px;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 8px;
}

.condition-score {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.condition-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-regular);
}

.report-summary,
.recommendations {
  margin-bottom: 20px;
}

.summary-title,
.recommend-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--text-primary);
}

.recommend-item {
  font-size: 14px;
  color: var(--text-regular);
  padding: 8px 0;
}

.report-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-secondary);
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

/* 桌面端样式 */
.desktop-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
}

.main-card {
  border-radius: 16px;
  box-shadow: var(--card-shadow);
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
}

.card-header p {
  margin: 0;
  color: var(--text-secondary);
}

.upload-area-large {
  width: 100%;
}

.form-section {
  margin-top: 24px;
}

.analyze-btn-large {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 24px;
}

.result-desktop {
  height: 100%;
}

.result-card-large {
  background: linear-gradient(135deg, #f0fdfa 0%, #f0f9ff 100%);
  border-radius: 16px;
  padding: 32px;
  height: 100%;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 400px;
}
</style>
