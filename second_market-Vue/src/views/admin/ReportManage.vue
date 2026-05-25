<template>
  <div class="report-manage">
    <div class="page-header">
      <h2 class="page-title">{{ $t('ReportManage.title') }}</h2>
    </div>

    <el-row :gutter="20">
      <!-- 宸︿晶涓诲唴瀹瑰尯 70% -->
      <el-col :xs="24" :sm="24" :md="17" :lg="17">
        <el-card class="filter-card" shadow="hover">
          <div class="status-filter">
            <el-radio-group v-model="statusFilter" @change="handleStatusChange">
              <el-radio-button label="">{{ $t('ReportManage.filter.all') }}</el-radio-button>
              <el-radio-button label="pending">{{ $t('ReportManage.status.pending') }}</el-radio-button>
              <el-radio-button label="handled">{{ $t('ReportManage.status.handled') }}</el-radio-button>
              <el-radio-button label="rejected">{{ $t('ReportManage.status.rejected') }}</el-radio-button>
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
            <el-table-column prop="id" :label="$t('ReportManage.id')" width="80" />
            
            <el-table-column :label="$t('ReportManage.reportType')" width="120">
              <template #default="{ row }">
                <div class="type-cell">
                  <el-icon :size="18" :color="row.type === 'product' ? '#409eff' : '#f56c6c'">
                    <component :is="row.type === 'product' ? 'ShoppingBag' : 'User'" />
                  </el-icon>
                  <span>{{ $t(getTypeTextKey(row.type)) }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column :label="$t('ReportManage.target')" width="200" show-overflow-tooltip>
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

            <el-table-column :label="$t('ReportManage.reporter')" width="120">
              <template #default="{ row }">
                {{ row.reporterName }}
              </template>
            </el-table-column>

            <el-table-column :label="$t('ReportManage.reason')" min-width="200" show-overflow-tooltip>
              <template #default="{ row }">
                {{ row.reason }}
              </template>
            </el-table-column>

            <el-table-column :label="$t('ReportManage.reportTime')" width="160">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>

            <el-table-column :label="$t('ReportManage.status')" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ $t(getStatusTextKey(row.status)) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column :label="$t('ReportManage.actions')" width="260" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleViewDetail(row)" :icon="View">
                  {{ $t('ReportManage.detail') }}
                </el-button>
                <el-button 
                  v-if="row.status === 'pending'"
                  type="success" 
                  size="small" 
                  @click="handleProcess(row)" 
                  :icon="Select"
                >
                  {{ $t('ReportManage.process') }}
                </el-button>
                <el-button 
                  v-if="row.status === 'pending'"
                  type="warning" 
                  size="small" 
                  @click="handleReject(row)" 
                  :icon="Close"
                >
                  {{ $t('ReportManage.reject') }}
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

      <!-- 鍙充晶缁熻鍗＄墖鍖?30% -->
      <el-col :xs="24" :sm="24" :md="7" :lg="7">
        <div class="stats-sidebar">
          <!-- 涓炬姤缁熻鍗＄墖 -->
          <el-card class="stat-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><TrendCharts /></el-icon>
                <span>{{ $t('ReportManage.statistics') }}</span>
              </div>
            </template>
            <div class="stat-item">
              <div class="stat-label">{{ $t('ReportManage.stats.pending') }}</div>
              <div class="stat-value pending">{{ stats.pending || 0 }}</div>
            </div>
            <el-divider />
            <div class="stat-item">
              <div class="stat-label">{{ $t('ReportManage.stats.handled') }}</div>
              <div class="stat-value success">{{ stats.handled || 0 }}</div>
            </div>
            <el-divider />
            <div class="stat-item">
              <div class="stat-label">{{ $t('ReportManage.stats.rejected') }}</div>
              <div class="stat-value danger">{{ stats.rejected || 0 }}</div>
            </div>
            <el-divider />
            <div class="stat-item">
              <div class="stat-label">{{ $t('ReportManage.stats.total') }}</div>
              <div class="stat-value info">{{ stats.total || 0 }}</div>
            </div>
          </el-card>

          <!-- 涓炬姤绫诲瀷鍒嗗竷 -->
          <el-card class="type-card" shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <el-icon><PieChart /></el-icon>
                <span>{{ $t('ReportManage.typeDistribution') }}</span>
              </div>
            </template>
            <div class="type-stats">
              <div class="type-item">
                <el-icon :size="24" color="#409eff"><ShoppingBag /></el-icon>
                <div class="type-info">
                  <div class="type-label">{{ $t('ReportManage.productReport') }}</div>
                  <div class="type-value">{{ stats.productReports || 0 }}</div>
                </div>
              </div>
              <el-divider />
              <div class="type-item">
                <el-icon :size="24" color="#f56c6c"><User /></el-icon>
                <div class="type-info">
                  <div class="type-label">{{ $t('ReportManage.userReport') }}</div>
                  <div class="type-value">{{ stats.userReports || 0 }}</div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 蹇€熸搷浣滃崱鐗?-->
          <el-card class="quick-actions-card" shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <el-icon><Operation /></el-icon>
                <span>{{ $t('ReportManage.quickActions') }}</span>
              </div>
            </template>
            <el-button type="primary" plain style="width: 100%; margin-bottom: 12px;" @click="statusFilter = 'pending'; handleStatusChange()">
              <el-icon><Warning /></el-icon>
              {{ $t('ReportManage.quick.viewPending') }}
            </el-button>
            <el-button type="success" plain style="width: 100%; margin-bottom: 12px;" @click="statusFilter = 'handled'; handleStatusChange()">
              <el-icon><CircleCheck /></el-icon>
              {{ $t('ReportManage.quick.viewHandled') }}
            </el-button>
            <el-button plain style="width: 100%;" @click="statusFilter = ''; handleStatusChange()">
              <el-icon><Refresh /></el-icon>
              {{ $t('ReportManage.quick.refresh') }}
            </el-button>
          </el-card>
        </div>
      </el-col>
    </el-row>

    <!-- 涓炬姤璇︽儏瀵硅瘽妗?-->
    <el-dialog
      v-model="detailVisible"
      :title="$t('ReportManage.detailTitle')"
      width="700px"
    >
      <div class="report-detail" v-if="currentReport">
        <div class="detail-section">
          <h4>{{ $t('ReportManage.detailInfo') }}</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item :label="$t('ReportManage.id')">{{ currentReport.id }}</el-descriptions-item>
            <el-descriptions-item :label="$t('ReportManage.reportType')">
              <el-tag :type="currentReport.type === 'product' ? 'primary' : 'danger'" size="small">
                {{ $t(getTypeTextKey(currentReport.type)) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('ReportManage.reporter')">{{ currentReport.reporterName }}</el-descriptions-item>
            <el-descriptions-item :label="$t('ReportManage.reportTime')">{{ formatDate(currentReport.createTime) }}</el-descriptions-item>
            <el-descriptions-item :label="$t('ReportManage.status')" :span="2">
              <el-tag :type="getStatusType(currentReport.status)" size="small">
                {{ $t(getStatusTextKey(currentReport.status)) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('ReportManage.reason')" :span="2">
              <div class="reason-text">{{ currentReport.reason }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h4>{{ $t('ReportManage.targetInfo') }}</h4>
          <el-card class="target-card">
            <div class="target-info">
              <div class="target-image-container">
                <!-- 鍟嗗搧涓炬姤鏄剧ず鍟嗗搧鍥剧墖 -->
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
                <!-- 鐢ㄦ埛涓炬姤鏄剧ず鐢ㄦ埛澶村儚 -->
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
                <!-- 榛樿鍥炬爣鍗犱綅 -->
                <div v-else class="target-icon">
                  <el-icon :size="32" :color="currentReport.type === 'product' ? '#409eff' : '#f56c6c'">
                    <component :is="currentReport.type === 'product' ? 'ShoppingBag' : 'User'" />
                  </el-icon>
                </div>
              </div>
              <div class="target-content">
                <div class="target-name">{{ currentReport.targetName }}</div>
                <div class="target-desc">{{ currentReport.targetDesc || $t('ReportManage.noDescription') }}</div>
                <div class="target-type">
                  <el-tag :type="currentReport.type === 'product' ? 'primary' : 'danger'" size="small">
                    {{ $t(getTypeTextKey(currentReport.type)) }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <div class="detail-section" v-if="currentReport.handleResult">
          <h4>{{ $t('ReportManage.processResult') }}</h4>
          <el-card class="result-card">
            <div class="result-text">{{ currentReport.handleResult }}</div>
            <div class="result-time">{{ $t('ReportManage.processTime') }}锛歿{ formatDate(currentReport.handleTime) }}</div>
          </el-card>
        </div>
      </div>
    </el-dialog>

    <!-- 澶勭悊涓炬姤瀵硅瘽妗?-->
    <el-dialog
      v-model="processVisible"
      :title="$t('ReportManage.processTitle')"
      width="500px"
    >
      <el-form :model="processForm" :rules="processRules" ref="processFormRef" :label-width="$t('ReportManage.labelWidth')">
        <el-form-item :label="$t('ReportManage.processResult')" prop="result">
          <el-input
            v-model="processForm.result"
            type="textarea"
            :rows="5"
            :placeholder="$t('ReportManage.placeholder.processResult')"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processVisible = false">{{ $t('ReportManage.cancel') }}</el-button>
        <el-button type="primary" @click="confirmProcess" :loading="processing">
          {{ $t('ReportManage.confirm') }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 椹冲洖涓炬姤瀵硅瘽妗?-->
    <el-dialog
      v-model="rejectVisible"
      :title="$t('ReportManage.rejectTitle')"
      width="500px"
    >
      <el-form :model="rejectForm" :rules="rejectRules" ref="rejectFormRef" :label-width="$t('ReportManage.labelWidth')">
        <el-form-item :label="$t('ReportManage.rejectReason')" prop="reason">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="5"
            :placeholder="$t('ReportManage.placeholder.rejectReason')"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">{{ $t('ReportManage.cancel') }}</el-button>
        <el-button type="warning" @click="confirmReject" :loading="rejecting">
          {{ $t('ReportManage.confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
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
import { formatAvatarUrl, formatImageUrl } from '@/utils/url'

const { t } = useI18n()

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

// 琛ㄥ崟鏍￠獙瑙勫垯锛堝搷搴斿紡锛?
const processRules = computed(() => ({
  result: [
    { required: true, message: t('ReportManage.rules.resultRequired'), trigger: 'blur' },
    { min: 10, message: t('ReportManage.rules.resultMinLength'), trigger: 'blur' }
  ]
}))

const rejectRules = computed(() => ({
  reason: [
    { required: true, message: t('ReportManage.rules.reasonRequired'), trigger: 'blur' },
    { min: 10, message: t('ReportManage.rules.reasonMinLength'), trigger: 'blur' }
  ]
}))

// 绫诲瀷缈昏瘧閿槧灏?
const getTypeTextKey = (type) => {
  const map = {
    product: 'ReportManage.type.product',
    user: 'ReportManage.type.user'
  }
  return map[type] || type
}

// 鐘舵€佹爣绛剧被鍨?
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    handled: 'success',
    rejected: 'info'
  }
  return typeMap[status] || 'info'
}

// 鐘舵€佺炕璇戦敭鏄犲皠
const getStatusTextKey = (status) => {
  const map = {
    pending: 'ReportManage.status.pending',
    handled: 'ReportManage.status.handled',
    rejected: 'ReportManage.status.rejected'
  }
  return map[status] || status
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
        targetName: '浜屾墜iPhone 13 Pro',
        targetDesc: '128GB 石墨色',
        targetImage: '',
        reporterName: 'user001',
        reason: '商品与描述不符，疑似欺诈行为',
        status: 'pending',
        createTime: new Date(Date.now() - 2 * 60 * 60 * 1000)
      },
      {
        id: 2,
        type: 'user',
        targetName: 'user002',
        targetDesc: '用户昵称：小明',
        targetAvatar: '',
        reporterName: 'user003',
        reason: '璇ョ敤鎴峰彂甯冭櫄鍋囦俊鎭紝楠氭壈浠栦汉',
        status: 'pending',
        createTime: new Date(Date.now() - 5 * 60 * 60 * 1000)
      },
      {
        id: 3,
        type: 'product',
        targetName: '澶у鐗╃悊鏁欐潗',
        targetDesc: '第七版',
        targetImage: '',
        reporterName: 'user004',
        reason: '鍟嗗搧璐ㄩ噺鏈夐棶棰橈紝鐮存崯涓ラ噸',
        status: 'handled',
        handleResult: '宸叉牳瀹炴儏鍐碉紝鍟嗗搧纭疄瀛樺湪璐ㄩ噺闂锛屽凡瑕佹眰鍗栧閫€娆惧苟涓嬫灦鍟嗗搧',
        handleTime: new Date(Date.now() - 24 * 60 * 60 * 1000),
        createTime: new Date(Date.now() - 48 * 60 * 60 * 1000)
      },
      {
        id: 4,
        type: 'user',
        targetName: 'user005',
        targetDesc: '用户昵称：小红',
        targetAvatar: '',
        reporterName: 'user006',
        reason: '恶意差评',
        status: 'rejected',
        handleResult: '经调查，举报不属实',
        handleTime: new Date(Date.now() - 72 * 60 * 60 * 1000),
        createTime: new Date(Date.now() - 96 * 60 * 60 * 1000)
      },
      {
        id: 5,
        type: 'product',
        targetName: '二手山地车',
        targetDesc: '成色一般，支持自提',
        targetImage: '',
        reporterName: 'user007',
        reason: '商品实物与图片差距很大',
        status: 'pending',
        createTime: new Date(Date.now() - 1 * 60 * 60 * 1000)
      },
      {
        id: 6,
        type: 'product',
        targetName: '笔记本电脑',
        targetDesc: 'ThinkPad X1',
        targetImage: '',
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

    // 搴旂敤鐘舵€佽繃婊?
    if (statusFilter.value) {
      reportList.value = allReports.filter(item => item.status === statusFilter.value)
    } else {
      reportList.value = allReports
    }

    pagination.total = reportList.value.length

    // 鏇存柊缁熻鏁版嵁
    stats.value = {
      pending: allReports.filter(item => item.status === 'pending').length,
      handled: allReports.filter(item => item.status === 'handled').length,
      rejected: allReports.filter(item => item.status === 'rejected').length,
      total: allReports.length,
      productReports: allReports.filter(item => item.type === 'product').length,
      userReports: allReports.filter(item => item.type === 'user').length
    }
  } catch (error) {
    console.error('鑾峰彇涓炬姤鍒楄〃澶辫触:', error)
    ElMessage.error(t('ReportManage.fetchFail'))
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
    
    ElMessage.success(t('ReportManage.processSuccess'))
    processVisible.value = false
    
    const index = reportList.value.findIndex(item => item.id === currentReport.value.id)
    if (index !== -1) {
      reportList.value[index].status = 'handled'
      reportList.value[index].handleResult = processForm.result
      reportList.value[index].handleTime = new Date()
    }
  } catch (error) {
    if (error !== false) {
      console.error('澶勭悊澶辫触:', error)
      ElMessage.error(t('ReportManage.processFail'))
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
    
    ElMessage.success(t('ReportManage.rejectSuccess'))
    rejectVisible.value = false
    
    const index = reportList.value.findIndex(item => item.id === currentReport.value.id)
    if (index !== -1) {
      reportList.value[index].status = 'rejected'
      reportList.value[index].handleResult = rejectForm.reason
      reportList.value[index].handleTime = new Date()
    }
  } catch (error) {
    if (error !== false) {
      console.error('椹冲洖澶辫触:', error)
      ElMessage.error(t('ReportManage.rejectFail'))
    }
  } finally {
    rejecting.value = false
  }
}

const handleImageError = (event) => {
  event.target.src = formatAvatarUrl('')
}

// 鏍煎紡鍖栬涓炬姤瀵硅薄鐨勫浘鐗囪矾寰?
const formatTargetImage = (imagePath) => {
  if (!imagePath) return ''
  
  // 濡傛灉鏄畬鏁碪RL锛岀洿鎺ヤ娇鐢?
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath
  }
  
  // 濡傛灉鏄浉瀵硅矾寰勶紝浣跨敤formatImageUrl澶勭悊
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

/* 渚ц竟鏍忔牱寮?*/
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


