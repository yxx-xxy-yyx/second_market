<template>
  <div class="notice-manage">
    <div class="page-header">
      <h2 class="page-title">公告管理</h2>
      <el-button type="primary" @click="handleAdd" :icon="Plus">
        发布公告
      </el-button>
    </div>

    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="noticeList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column label="标题" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.title }}
          </template>
        </el-table-column>

        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <div class="type-cell">
              <el-icon :size="18" :color="getTypeColor(row.type)">
                <component :is="getTypeIcon(row.type)" />
              </el-icon>
              <span>{{ getTypeText(row.type) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="发布时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.publishTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)" :icon="View">
              查看
            </el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)" :icon="Edit">
              编辑
            </el-button>
            <el-button 
              v-if="row.status === 'draft' || row.status === 'offline'"
              type="success" 
              size="small" 
              @click="handlePublish(row)"
              :icon="Upload"
            >
              发布
            </el-button>
            <el-button 
              v-if="row.status === 'published'"
              type="info" 
              size="small" 
              @click="handleOffline(row)"
              :icon="Download"
            >
              下架
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" :icon="Delete">
              删除
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

    <el-dialog
      v-model="viewVisible"
      title="查看公告"
      width="800px"
    >
      <div class="notice-view" v-if="currentNotice">
        <h3 class="notice-title">{{ currentNotice.title }}</h3>
        <div class="notice-meta">
          <el-tag :type="getTypeColor(currentNotice.type)" size="small">
            {{ getTypeText(currentNotice.type) }}
          </el-tag>
          <span class="notice-time">发布时间：{{ formatDate(currentNotice.publishTime) }}</span>
        </div>
        <div class="notice-content" v-html="currentNotice.content"></div>
      </div>
    </el-dialog>

    <el-dialog
      v-model="formVisible"
      :title="formTitle"
      width="900px"
      @closed="handleDialogClosed"
    >
      <el-form :model="noticeForm" :rules="noticeRules" ref="noticeFormRef" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input 
            v-model="noticeForm.title" 
            placeholder="请输入公告标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="noticeForm.type" placeholder="请选择公告类型" style="width: 200px">
            <el-option label="系统公告" value="system">
              <div class="type-option">
                <el-icon color="#409eff"><Bell /></el-icon>
                <span>系统公告</span>
              </div>
            </el-option>
            <el-option label="活动公告" value="activity">
              <div class="type-option">
                <el-icon color="#67c23a"><Flag /></el-icon>
                <span>活动公告</span>
              </div>
            </el-option>
            <el-option label="维护通知" value="maintenance">
              <div class="type-option">
                <el-icon color="#e6a23c"><Tools /></el-icon>
                <span>维护通知</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="公告内容" prop="content">
          <Editor 
            v-model:editorValue="noticeForm.content"
            :editorValue="noticeForm.content"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button @click="handleSaveDraft" :loading="saving">
          保存草稿
        </el-button>
        <el-button type="primary" @click="handleSaveAndPublish" :loading="saving">
          立即发布
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  View,
  Edit,
  Delete,
  Upload,
  Download,
  Bell,
  Flag,
  Tools
} from '@element-plus/icons-vue'
import Editor from '@/components/Editor/index.vue'

const loading = ref(false)
const saving = ref(false)
const viewVisible = ref(false)
const formVisible = ref(false)
const isAdd = ref(false)
const currentNotice = ref(null)
const noticeList = ref([])
const noticeFormRef = ref()

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const noticeForm = reactive({
  id: '',
  title: '',
  type: 'system',
  content: ''
})

const noticeRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 5, message: '公告标题不能少于5个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择公告类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' }
  ]
}

const formTitle = computed(() => isAdd.value ? '发布公告' : '编辑公告')

const getTypeIcon = (type) => {
  const iconMap = {
    system: 'Bell',
    activity: 'Flag',
    maintenance: 'Tools'
  }
  return iconMap[type] || 'Bell'
}

const getTypeColor = (type) => {
  const colorMap = {
    system: '#409eff',
    activity: '#67c23a',
    maintenance: '#e6a23c'
  }
  return colorMap[type] || '#409eff'
}

const getTypeText = (type) => {
  const textMap = {
    system: '系统公告',
    activity: '活动公告',
    maintenance: '维护通知'
  }
  return textMap[type] || type
}

const getStatusType = (status) => {
  const typeMap = {
    draft: 'info',
    published: 'success',
    offline: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    draft: '草稿',
    published: '已发布',
    offline: '已下架'
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

const getNoticeList = async () => {
  try {
    loading.value = true
    
    noticeList.value = [
      {
        id: 1,
        title: '平台用户协议更新通知',
        type: 'system',
        content: '<p>尊敬的用户，我们对平台用户协议进行了更新，请仔细阅读...</p>',
        status: 'published',
        publishTime: new Date(Date.now() - 24 * 60 * 60 * 1000)
      },
      {
        id: 2,
        title: '新春促销活动开始啦',
        type: 'activity',
        content: '<p>新春佳节，平台推出多款优惠商品，欢迎选购...</p>',
        status: 'published',
        publishTime: new Date(Date.now() - 48 * 60 * 60 * 1000)
      },
      {
        id: 3,
        title: '系统维护通知',
        type: 'maintenance',
        content: '<p>系统将于本周六凌晨2:00-6:00进行维护升级...</p>',
        status: 'draft',
        publishTime: null
      },
      {
        id: 4,
        title: '功能升级公告',
        type: 'system',
        content: '<p>平台即将上线新功能，敬请期待...</p>',
        status: 'offline',
        publishTime: new Date(Date.now() - 72 * 60 * 60 * 1000)
      }
    ]

    pagination.total = noticeList.value.length
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  getNoticeList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  getNoticeList()
}

const handleAdd = () => {
  isAdd.value = true
  formVisible.value = true
  resetForm()
}

const handleView = (row) => {
  currentNotice.value = { ...row }
  viewVisible.value = true
}

const handleEdit = (row) => {
  isAdd.value = false
  formVisible.value = true
  Object.assign(noticeForm, {
    id: row.id,
    title: row.title,
    type: row.type,
    content: row.content
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除公告 "${row.title}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('删除成功')
    const index = noticeList.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      noticeList.value.splice(index, 1)
      pagination.total--
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除公告失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要发布公告 "${row.title}" 吗？`,
      '确认发布',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    
    ElMessage.success('发布成功')
    row.status = 'published'
    row.publishTime = new Date()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布公告失败:', error)
      ElMessage.error('发布失败')
    }
  }
}

const handleOffline = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要下架公告 "${row.title}" 吗？`,
      '确认下架',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('下架成功')
    row.status = 'offline'
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架公告失败:', error)
      ElMessage.error('下架失败')
    }
  }
}

const handleSaveDraft = async () => {
  try {
    await noticeFormRef.value.validate()
    saving.value = true
    
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (isAdd.value) {
      noticeList.value.unshift({
        id: Date.now(),
        title: noticeForm.title,
        type: noticeForm.type,
        content: noticeForm.content,
        status: 'draft',
        publishTime: null
      })
      pagination.total++
    } else {
      const index = noticeList.value.findIndex(item => item.id === noticeForm.id)
      if (index !== -1) {
        noticeList.value[index] = {
          ...noticeList.value[index],
          title: noticeForm.title,
          type: noticeForm.type,
          content: noticeForm.content
        }
      }
    }
    
    ElMessage.success('保存草稿成功')
    formVisible.value = false
  } catch (error) {
    if (error !== false) {
      console.error('保存草稿失败:', error)
      ElMessage.error('保存草稿失败')
    }
  } finally {
    saving.value = false
  }
}

const handleSaveAndPublish = async () => {
  try {
    await noticeFormRef.value.validate()
    saving.value = true
    
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (isAdd.value) {
      noticeList.value.unshift({
        id: Date.now(),
        title: noticeForm.title,
        type: noticeForm.type,
        content: noticeForm.content,
        status: 'published',
        publishTime: new Date()
      })
      pagination.total++
    } else {
      const index = noticeList.value.findIndex(item => item.id === noticeForm.id)
      if (index !== -1) {
        noticeList.value[index] = {
          ...noticeList.value[index],
          title: noticeForm.title,
          type: noticeForm.type,
          content: noticeForm.content,
          status: 'published',
          publishTime: new Date()
        }
      }
    }
    
    ElMessage.success('发布成功')
    formVisible.value = false
  } catch (error) {
    if (error !== false) {
      console.error('发布失败:', error)
      ElMessage.error('发布失败')
    }
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  Object.assign(noticeForm, {
    id: '',
    title: '',
    type: 'system',
    content: ''
  })
}

const handleDialogClosed = () => {
  resetForm()
  noticeFormRef.value?.clearValidate()
}

onMounted(() => {
  getNoticeList()
})
</script>

<style scoped>
.notice-manage {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.table-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.type-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}

.notice-view {
  padding: 20px 0;
}

.notice-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.notice-time {
  font-size: 14px;
  color: #909399;
}

.notice-content {
  line-height: 1.8;
  color: #2c3e50;
}

.notice-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
}

.notice-content :deep(p) {
  margin: 12px 0;
}

@media (max-width: 768px) {
  .notice-manage {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>

