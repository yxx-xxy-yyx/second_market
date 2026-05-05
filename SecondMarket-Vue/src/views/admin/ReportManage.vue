<template>
  <div class="report-manage">
    <div class="page-header">
      <h2 class="page-title">举报管理</h2>
    </div>

    <el-row :gutter="20">
      <!-- 左侧主内容区 70% -->
      <el-col :xs="24" :sm="24" :md="17" :lg="17">
        <el-card class="filter-card" shadow="hover">
          <div class="status-filter">
            <el-radio-group v-model="statusFilter" @change="handleStatusChange">
              <el-radio-button label="">全部</el-radio-button>
              <el-radio-button label="pending">待处理</el-radio-button>
              <el-radio-button label="handled">已处理</el-radio-button>
              <el-radio-button label="rejected">已驳回</el-radio-button>
            </el-radio-group>
          </div>
        </el-card>

        <el-card class="table-card" shadow="hover" style="margin-top: 20px;">
      <el-table
        v-loading="loading"
        :data="reportList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column label="举报类型" width="120">
          <template #default="{ row }">
            <div class="type-cell">
              <el-icon :size="18" :color="row.type === 'product' ? '#409eff' : '#f56c6c'">
                <component :is="row.type === 'product' ? 'ShoppingBag' : 'User'" />
              </el-icon>
              <span>{{ row.type === 'product' ? '商品举报' : '用户举报' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="举报对象" width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="target-cell">
              <img 
                v-if="row.type === 'product' && row.targetImage" 
                :src="row.targetImage" 
                class="target-image"
                @error="handleImageError"
              />
              <div class="target-info">
                <div class="target-name">{{ row.targetName }}</div>
                <div class="target-desc" v-if="row.targetDesc">{{ row.targetDesc }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="举报人" width="120">
          <template #default="{ row }">
            {{ row.reporterName }}
          </template>
        </el-table-column>

        <el-table-column label="举报原因" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.reason }}
          </template>
        </el-table-column>

        <el-table-column label="举报时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)" :icon="View">
              详情
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="success" 
              size="small" 
              @click="handleProcess(row)" 
              :icon="Select"
            >
              处理
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="warning" 
              size="small" 
              @click="handleReject(row)" 
              :icon="Close"
            >
              驳回
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
      </el-col>

      <!-- 右侧统计卡片区 30% -->
      <el-col :xs="24" :sm="24" :md="7" :lg="7">
        <div class="stats-sidebar">
          <!-- 举报统计卡片 -->
          <el-card class="stat-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><TrendCharts /></el-icon>
                <span>举报统计</span>
              </div>
            </template>
            <div class="stat-item">
              <div class="stat-label">待处理</div>
              <div class="stat-value pending">{{ stats.pending || 0 }}</div>
            </div>
            <el-divider />
            <div class="stat-item">
              <div class="stat-label">已处理</div>
              <div class="stat-value success">{{ stats.handled || 0 }}</div>
            </div>
            <el-divider />
            <div class="stat-item">
              <div class="stat-label">已驳回</div>
              <div class="stat-value danger">{{ stats.rejected || 0 }}</div>
            </div>
            <el-divider />
            <div class="stat-item">
              <div class="stat-label">总举报数</div>
              <div class="stat-value info">{{ stats.total || 0 }}</div>
            </div>
          </el-card>

          <!-- 举报类型分布 -->
          <el-card class="type-card" shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <el-icon><PieChart /></el-icon>
                <span>类型分布</span>
              </div>
            </template>
            <div class="type-stats">
              <div class="type-item">
                <el-icon :size="24" color="#409eff"><ShoppingBag /></el-icon>
                <div class="type-info">
                  <div class="type-label">商品举报</div>
                  <div class="type-value">{{ stats.productReports || 0 }}</div>
                </div>
              </div>
              <el-divider />
              <div class="type-item">
                <el-icon :size="24" color="#f56c6c"><User /></el-icon>
                <div class="type-info">
                  <div class="type-label">用户举报</div>
                  <div class="type-value">{{ stats.userReports || 0 }}</div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 快速操作卡片 -->
          <el-card class="quick-actions-card" shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <el-icon><Operation /></el-icon>
                <span>快速操作</span>
              </div>
            </template>
            <el-button type="primary" plain style="width: 100%; margin-bottom: 12px;" @click="statusFilter = 'pending'; handleStatusChange()">
              <el-icon><Warning /></el-icon>
              查看待处理
            </el-button>
            <el-button type="success" plain style="width: 100%; margin-bottom: 12px;" @click="statusFilter = 'handled'; handleStatusChange()">
              <el-icon><CircleCheck /></el-icon>
              查看已处理
            </el-button>
            <el-button plain style="width: 100%;" @click="statusFilter = ''; handleStatusChange()">
              <el-icon><Refresh /></el-icon>
              刷新列表
            </el-button>
          </el-card>
        </div>
      </el-col>
    </el-row>

    <el-dialog
      v-model="detailVisible"
      title="举报详情"
      width="700px"
    >
      <div class="report-detail" v-if="currentReport">
        <div class="detail-section">
          <h4>举报信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="举报ID">{{ currentReport.id }}</el-descriptions-item>
            <el-descriptions-item label="举报类型">
              <el-tag :type="currentReport.type === 'product' ? 'primary' : 'danger'" size="small">
                {{ currentReport.type === 'product' ? '商品举报' : '用户举报' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="举报人">{{ currentReport.reporterName }}</el-descriptions-item>
            <el-descriptions-item label="举报时间">{{ formatDate(currentReport.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="状态" :span="2">
              <el-tag :type="getStatusType(currentReport.status)" size="small">
                {{ getStatusText(currentReport.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="举报原因" :span="2">
              <div class="reason-text">{{ currentReport.reason }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h4>被举报对象信息</h4>
          <el-card class="target-card">
            <div class="target-info">
              <div class="target-image-container">
                <!-- 商品举报显示商品图片 -->
                <el-image 
                  v-if="currentReport.type === 'product' && currentReport.targetImage"
                  :src="formatTargetImage(currentReport.targetImage)"
                  fit="cover"
                  class="target-image"
                  :preview-src-list="[formatTargetImage(currentReport.targetImage)]"
                >
                  <template #error>
                    <div class="target-icon">
                      <el-icon :size="32" color="#409eff">
                        <ShoppingBag />
                      </el-icon>
                    </div>
                  </template>
                </el-image>
                <!-- 用户举报显示用户头像 -->
                <el-avatar 
                  v-else-if="currentReport.type === 'user' && currentReport.targetAvatar"
                  :src="formatTargetImage(currentReport.targetAvatar)"
                  :size="60"
                  class="target-avatar"
                >
                  <el-icon :size="24" color="#f56c6c">
                    <User />
                  </el-icon>
                </el-avatar>
                <!-- 默认图标占位 -->
                <div v-else class="target-icon">
                  <el-icon :size="32" :color="currentReport.type === 'product' ? '#409eff' : '#f56c6c'">
                    <component :is="currentReport.type === 'product' ? 'ShoppingBag' : 'User'" />
                  </el-icon>
                </div>
              </div>
              <div class="target-content">
                <div class="target-name">{{ currentReport.targetName }}</div>
                <div class="target-desc">{{ currentReport.targetDesc || '暂无描述' }}</div>
                <div class="target-type">
                  <el-tag :type="currentReport.type === 'product' ? 'primary' : 'danger'" size="small">
                    {{ currentReport.type === 'product' ? '商品举报' : '用户举报' }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <div class="detail-section" v-if="currentReport.handleResult">
          <h4>处理结果</h4>
          <el-card class="result-card">
            <div class="result-text">{{ currentReport.handleResult }}</div>
            <div class="result-time">处理时间：{{ formatDate(currentReport.handleTime) }}</div>
          </el-card>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      v-model="processVisible"
      title="处理举报"
      width="500px"
    >
      <el-form :model="processForm" :rules="processRules" ref="processFormRef" label-width="100px">
        <el-form-item label="处理结果" prop="result">
          <el-input
            v-model="processForm.result"
            type="textarea"
            :rows="5"
            placeholder="请输入处理结果"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmProcess" :loading="processing">
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="rejectVisible"
      title="驳回举报"
      width="500px"
    >
      <el-form :model="rejectForm" :rules="rejectRules" ref="rejectFormRef" label-width="100px">
        <el-form-item label="驳回原因" prop="reason">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="5"
            placeholder="请输入驳回原因"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="warning" @click="confirmReject" :loading="rejecting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  View,
  Select,
  Close,
  ShoppingBag,
  User,
  TrendCharts,
  PieChart,
  Operation,
  Warning,
  CircleCheck,
  Refresh
} from '@element-plus/icons-vue'
import { formatImageUrl } from '@/utils/url'

const loading = ref(false)
const processing = ref(false)
const rejecting = ref(false)
const detailVisible = ref(false)
const processVisible = ref(false)
const rejectVisible = ref(false)
const currentReport = ref(null)
const statusFilter = ref('')
const reportList = ref([])
const processFormRef = ref()
const rejectFormRef = ref()

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const stats = ref({
  pending: 0,
  handled: 0,
  rejected: 0,
  total: 0,
  productReports: 0,
  userReports: 0
})

const processForm = reactive({
  result: ''
})

const rejectForm = reactive({
  reason: ''
})

const processRules = {
  result: [
    { required: true, message: '请输入处理结果', trigger: 'blur' },
    { min: 10, message: '处理结果不能少于10个字符', trigger: 'blur' }
  ]
}

const rejectRules = {
  reason: [
    { required: true, message: '请输入驳回原因', trigger: 'blur' },
    { min: 10, message: '驳回原因不能少于10个字符', trigger: 'blur' }
  ]
}

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    handled: 'success',
    rejected: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待处理',
    handled: '已处理',
    rejected: '已驳回'
  }
  return textMap[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getReportList = async () => {
  try {
    loading.value = true
    
    const allReports = [
      {
        id: 1,
        type: 'product',
        targetName: '二手iPhone 13 Pro',
        targetDesc: '128GB 石墨色',
        targetImage: 'https://picsum.photos/400/400?random=1',
        reporterName: 'user001',
        reason: '商品与描述不符，存在欺诈行为，要求退款',
        status: 'pending',
        createTime: new Date(Date.now() - 2 * 60 * 60 * 1000)
      },
      {
        id: 2,
        type: 'user',
        targetName: 'user002',
        targetDesc: '用户昵称：小明',
        targetAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=user002',
        reporterName: 'user003',
        reason: '该用户发布虚假信息，骚扰他人',
        status: 'pending',
        createTime: new Date(Date.now() - 5 * 60 * 60 * 1000)
      },
      {
        id: 3,
        type: 'product',
        targetName: '大学物理教材',
        targetDesc: '第七版',
        targetImage: 'https://picsum.photos/400/400?random=2',
        reporterName: 'user004',
        reason: '商品质量有问题，破损严重',
        status: 'handled',
        handleResult: '已核实情况，商品确实存在质量问题，已要求卖家退款并下架商品',
        handleTime: new Date(Date.now() - 24 * 60 * 60 * 1000),
        createTime: new Date(Date.now() - 48 * 60 * 60 * 1000)
      },
      {
        id: 4,
        type: 'user',
        targetName: 'user005',
        targetDesc: '用户昵称：小红',
        targetAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=user005',
        reporterName: 'user006',
        reason: '该用户恶意差评',
        status: 'rejected',
        handleResult: '经调查，该举报不属实，用户评价客观真实',
        handleTime: new Date(Date.now() - 72 * 60 * 60 * 1000),
        createTime: new Date(Date.now() - 96 * 60 * 60 * 1000)
      },
      {
        id: 5,
        type: 'product',
        targetName: '自行车',
        targetDesc: '二手山地车',
        targetImage: 'https://picsum.photos/400/400?random=3',
        reporterName: 'user007',
        reason: '商品实物与照片差距很大',
        status: 'pending',
        createTime: new Date(Date.now() - 1 * 60 * 60 * 1000)
      },
      {
        id: 6,
        type: 'product',
        targetName: '笔记本电脑',
        targetDesc: 'ThinkPad X1',
        targetImage: 'https://picsum.photos/400/400?random=4',
        reporterName: 'user008',
        reason: '商品有质量问题',
        status: 'handled',
        handleResult: '已处理完成',
        handleTime: new Date(Date.now() - 12 * 60 * 60 * 1000),
        createTime: new Date(Date.now() - 24 * 60 * 60 * 1000)
      },
      {
        id: 7,
        type: 'user',
        targetName: 'user009',
        targetDesc: '用户昵称：小李',
        reporterName: 'user010',
        reason: '恶意骚扰其他用户',
        status: 'rejected',
        handleResult: '举报不成立',
        handleTime: new Date(Date.now() - 6 * 60 * 60 * 1000),
        createTime: new Date(Date.now() - 18 * 60 * 60 * 1000)
      }
    ]

    // 应用状态过滤
    if (statusFilter.value) {
      reportList.value = allReports.filter(item => item.status === statusFilter.value)
    } else {
      reportList.value = allReports
    }

    pagination.total = reportList.value.length

    // 更新统计数据
    stats.value = {
      pending: allReports.filter(item => item.status === 'pending').length,
      handled: allReports.filter(item => item.status === 'handled').length,
      rejected: allReports.filter(item => item.status === 'rejected').length,
      total: allReports.length,
      productReports: allReports.filter(item => item.type === 'product').length,
      userReports: allReports.filter(item => item.type === 'user').length
    }
  } catch (error) {
    console.error('获取举报列表失败:', error)
    ElMessage.error('获取举报列表失败')
  } finally {
    loading.value = false
  }
}

const handleStatusChange = () => {
  pagination.page = 1
  getReportList()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  getReportList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  getReportList()
}

const handleViewDetail = (row) => {
  currentReport.value = { ...row }
  detailVisible.value = true
}

const handleProcess = (row) => {
  currentReport.value = { ...row }
  processForm.result = ''
  processVisible.value = true
}

const handleReject = (row) => {
  currentReport.value = { ...row }
  rejectForm.reason = ''
  rejectVisible.value = true
}

const confirmProcess = async () => {
  try {
    await processFormRef.value.validate()
    processing.value = true
    
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('处理成功')
    processVisible.value = false
    
    const index = reportList.value.findIndex(item => item.id === currentReport.value.id)
    if (index !== -1) {
      reportList.value[index].status = 'handled'
      reportList.value[index].handleResult = processForm.result
      reportList.value[index].handleTime = new Date()
    }
  } catch (error) {
    if (error !== false) {
      console.error('处理失败:', error)
      ElMessage.error('处理失败')
    }
  } finally {
    processing.value = false
  }
}

const confirmReject = async () => {
  try {
    await rejectFormRef.value.validate()
    rejecting.value = true
    
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('驳回成功')
    rejectVisible.value = false
    
    const index = reportList.value.findIndex(item => item.id === currentReport.value.id)
    if (index !== -1) {
      reportList.value[index].status = 'rejected'
      reportList.value[index].handleResult = rejectForm.reason
      reportList.value[index].handleTime = new Date()
    }
  } catch (error) {
    if (error !== false) {
      console.error('驳回失败:', error)
      ElMessage.error('驳回失败')
    }
  } finally {
    rejecting.value = false
  }
}

const handleImageError = (event) => {
  event.target.src = 'https://picsum.photos/40/40?random=error'
}

// 格式化被举报对象的图片路径
const formatTargetImage = (imagePath) => {
  if (!imagePath) return ''
  
  // 如果是完整URL，直接使用
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath
  }
  
  // 如果是相对路径，使用formatImageUrl处理
  return formatImageUrl(imagePath)
}

onMounted(() => {
  getReportList()
})
</script>

<style scoped>
.report-manage {
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

.filter-card,
.table-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.status-filter {
  display: flex;
  justify-content: center;
}

.type-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.target-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.target-image {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
  flex-shrink: 0;
}

.target-info {
  flex: 1;
  min-width: 0;
}

.target-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.target-desc {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}

.report-detail {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.reason-text {
  line-height: 1.6;
  color: #f56c6c;
  font-weight: 500;
  background: #fef0f0;
  padding: 8px 12px;
  border-radius: 4px;
}

.target-card {
  background: #f9f9f9;
}

.target-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.target-image-container {
  flex-shrink: 0;
}

.target-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  cursor: pointer;
}

.target-avatar {
  border: 1px solid #e4e7ed;
}

.target-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  background: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.target-content {
  flex: 1;
}

.target-name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.target-desc {
  font-size: 14px;
  color: #909399;
  margin-bottom: 6px;
}

.target-type {
  margin-top: 6px;
}

.result-card {
  background: #f0f9ff;
  border: 1px solid #d9ecff;
}

.result-text {
  line-height: 1.6;
  color: #2c3e50;
  margin-bottom: 8px;
}

.result-time {
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .report-manage {
    padding: 16px;
  }

  .target-info {
    flex-direction: column;
    text-align: center;
  }
  
  .stats-sidebar {
    position: relative;
    top: 0;
    margin-top: 20px;
  }
}

/* 侧边栏样式 */
.stats-sidebar {
  position: sticky;
  top: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
}

.stat-value.pending {
  color: #ff9800;
}

.stat-value.success {
  color: #67c23a;
}

.stat-value.danger {
  color: #f56c6c;
}

.stat-value.info {
  color: #409eff;
}

.type-stats {
  padding: 8px 0;
}

.type-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
}

.type-info {
  flex: 1;
}

.type-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.type-value {
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
}
</style>

