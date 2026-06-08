<template>
  <div class="file-manage-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="title">文件管理</h1>
      <div class="header-stats">
        <div class="stat-item">
          <FileOutlined />
          <span>总文件: {{ totalFiles }}</span>
        </div>
        <div class="stat-item">
          <PictureOutlined />
          <span>图片: {{ imageCount }}</span>
        </div>
        <div class="stat-item">
          <FileTextOutlined />
          <span>文档: {{ documentCount }}</span>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="search-box">
        <SearchOutlined />
        <input type="text" v-model="searchKeyword" placeholder="搜索文件名..." />
      </div>
      <div class="filter-group">
        <select v-model="filterType">
          <option value="">全部类型</option>
          <option value="image">图片</option>
          <option value="document">文档</option>
          <option value="other">其他</option>
        </select>
        <select v-model="filterUserId">
          <option value="">全部用户</option>
          <option v-for="user in userList" :key="user.id" :value="user.id">
            {{ user.nickname || user.username }}
          </option>
        </select>
      </div>
      <button class="batch-delete-btn" @click="batchDelete" :disabled="selectedFiles.length === 0">
        <DeleteOutlined />
        批量删除 ({{ selectedFiles.length }})
      </button>
    </div>

    <!-- 文件列表 -->
    <div class="file-table">
      <table>
        <thead>
          <tr>
            <th class="checkbox-col">
              <input type="checkbox" v-model="selectAll" @change="toggleSelectAll" />
            </th>
            <th class="preview-col">预览</th>
            <th>文件名</th>
            <th>类型</th>
            <th>大小</th>
            <th>上传者</th>
            <th>上传时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="file in fileList" :key="file.id" :class="{ selected: selectedFiles.includes(file.id) }">
            <td class="checkbox-col">
              <input type="checkbox" :checked="selectedFiles.includes(file.id)" @change="toggleSelect(file.id)" />
            </td>
            <td class="preview-col">
              <div class="file-preview">
                <img v-if="file.fileType === 'image'" :src="file.url" class="preview-image" />
                <div v-else class="preview-icon">
                  <FileTextOutlined v-if="file.fileType === 'document'" />
                  <FileOutlined v-else />
                </div>
              </div>
            </td>
            <td class="name-col">
              <span class="file-name">{{ file.originalName }}</span>
              <span class="file-ext">{{ getFileExtension(file.originalName) }}</span>
            </td>
            <td>
              <span class="type-tag" :class="file.fileType">
                {{ getTypeText(file.fileType) }}
              </span>
            </td>
            <td>{{ formatFileSize(file.fileSize) }}</td>
            <td>
              <div class="user-info">
                <img :src="file.userAvatar || defaultAvatar" class="user-avatar" />
                <span>{{ file.userNickname || file.username || '未知' }}</span>
              </div>
            </td>
            <td>{{ formatDate(file.createTime) }}</td>
            <td class="action-col">
              <button class="action-btn view" @click="viewFile(file)">
                <EyeOutlined />
              </button>
              <button class="action-btn download" @click="downloadFile(file)">
                <DownloadOutlined />
              </button>
              <button class="action-btn delete" @click="deleteFile(file)">
                <DeleteOutlined />
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div class="empty-state" v-if="fileList.length === 0">
        <FolderOpenOutlined class="empty-icon" />
        <p>暂无文件数据</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <button class="prev-btn" @click="prevPage" :disabled="currentPage === 1">
        <LeftOutlined />
      </button>
      <div class="page-info">
        第 {{ currentPage }} / {{ totalPages }} 页
      </div>
      <button class="next-btn" @click="nextPage" :disabled="currentPage === totalPages">
        <RightOutlined />
      </button>
      <select v-model="pageSize" class="size-select">
        <option value="10">10条/页</option>
        <option value="20">20条/页</option>
        <option value="50">50条/页</option>
      </select>
    </div>

    <!-- 文件预览弹窗 -->
    <div class="preview-modal" v-if="showPreviewModal" @click.self="showPreviewModal = false">
      <div class="preview-content">
        <button class="close-btn" @click="showPreviewModal = false">
          <CloseOutlined />
        </button>
        <img v-if="previewFile?.fileType === 'image'" :src="previewFile?.url" class="preview-full" />
        <div v-else class="preview-placeholder">
          <FileOutlined class="placeholder-icon" />
          <p>{{ previewFile?.originalName }}</p>
          <a :href="previewFile?.url" target="_blank" class="download-link">
            点击下载查看
          </a>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div class="loading-overlay" v-if="loading">
      <div class="loading-spinner"></div>
      <span>数据加载中...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { 
  FileOutlined, FileTextOutlined, PictureOutlined, SearchOutlined,
  DeleteOutlined, EyeOutlined, DownloadOutlined, FolderOpenOutlined,
  LeftOutlined, RightOutlined, CloseOutlined
} from '@ant-design/icons-vue'
import { adminFileApi } from '@/api/admin'
import { message } from 'ant-design-vue'

const defaultAvatar = 'https://via.placeholder.com/32x32?text=U'

const loading = ref(false)
const fileList = ref([])
const userList = ref([])
const totalFiles = ref(0)
const imageCount = ref(0)
const documentCount = ref(0)

const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = computed(() => Math.ceil(totalFiles.value / pageSize.value) || 1)

const searchKeyword = ref('')
const filterType = ref('')
const filterUserId = ref('')
const selectedFiles = ref([])
const selectAll = ref(false)

const showPreviewModal = ref(false)
const previewFile = ref(null)

// 获取文件扩展名
const getFileExtension = (filename) => {
  if (!filename) return ''
  const ext = filename.split('.').pop()
  return ext ? `.${ext}` : ''
}

// 获取类型文本
const getTypeText = (type) => {
  const types = {
    image: '图片',
    document: '文档',
    other: '其他'
  }
  return types[type] || '未知'
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let size = bytes
  let unitIndex = 0
  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024
    unitIndex++
  }
  return `${size.toFixed(2)} ${units[unitIndex]}`
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 加载文件列表
const loadFileList = async () => {
  loading.value = true
  try {
    const res = await adminFileApi.getFileList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      fileType: filterType.value,
      userId: filterUserId.value
    })
    if (res.code === '200' && res.data) {
      fileList.value = res.data.records || []
      totalFiles.value = res.data.total || 0
      
      // 计算统计数据
      const allFiles = res.data.records || []
      imageCount.value = allFiles.filter(f => f.fileType === 'image').length
      documentCount.value = allFiles.filter(f => f.fileType === 'document').length
      
      // 提取用户列表
      const users = new Map()
      allFiles.forEach(f => {
        if (f.userId && !users.has(f.userId)) {
          users.set(f.userId, {
            id: f.userId,
            nickname: f.userNickname,
            username: f.username,
            avatar: f.userAvatar
          })
        }
      })
      userList.value = Array.from(users.values())
    }
  } catch (e) {
    console.error('加载文件列表失败', e)
  } finally {
    loading.value = false
  }
}

// 切换选择
const toggleSelect = (fileId) => {
  const index = selectedFiles.value.indexOf(fileId)
  if (index > -1) {
    selectedFiles.value.splice(index, 1)
  } else {
    selectedFiles.value.push(fileId)
  }
  selectAll.value = selectedFiles.value.length === fileList.value.length
}

// 全选/取消全选
const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedFiles.value = fileList.value.map(f => f.id)
  } else {
    selectedFiles.value = []
  }
}

// 查看文件
const viewFile = (file) => {
  previewFile.value = file
  showPreviewModal.value = true
}

// 下载文件
const downloadFile = (file) => {
  if (file.url) {
    window.open(file.url, '_blank')
  }
}

// 删除单个文件
const deleteFile = async (file) => {
  if (!confirm(`确定要删除文件 "${file.originalName}" 吗？`)) return
  try {
    const res = await adminFileApi.deleteFile(file.id)
    if (res.code === '200') {
      message.success('删除成功')
      await loadFileList()
    } else {
      message.error(res.message || '删除失败')
    }
  } catch (e) {
    message.error('删除失败')
  }
}

// 批量删除
const batchDelete = async () => {
  if (selectedFiles.value.length === 0) return
  if (!confirm(`确定要删除选中的 ${selectedFiles.value.length} 个文件吗？`)) return
  try {
    // 逐个删除
    for (const fileId of selectedFiles.value) {
      await adminFileApi.deleteFile(fileId)
    }
    message.success('批量删除成功')
    selectedFiles.value = []
    selectAll.value = false
    await loadFileList()
  } catch (e) {
    message.error('批量删除失败')
  }
}

// 分页操作
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    loadFileList()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    loadFileList()
  }
}

// 监听筛选条件变化
watch([filterType, filterUserId, pageSize], () => {
  currentPage.value = 1
  loadFileList()
})

// 监听搜索关键词（延迟搜索）
watch(searchKeyword, (newVal) => {
  // 可以添加延迟搜索逻辑
})

// 初始化
onMounted(() => {
  loadFileList()
})
</script>

<style scoped>
.file-manage-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
}

.header-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: white;
  border-radius: 8px;
  font-size: 14px;
  color: #374151;
}

.stat-item .anticon {
  color: #4f46e5;
}

/* 筛选区域 */
.filter-section {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 16px;
  border-radius: 12px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #f3f4f6;
  border-radius: 8px;
  flex: 1;
}

.search-box input {
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
}

.filter-group {
  display: flex;
  gap: 12px;
}

.filter-group select {
  padding: 10px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.batch-delete-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.batch-delete-btn:hover:not(:disabled) {
  background: #dc2626;
}

.batch-delete-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 文件表格 */
.file-table {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.file-table table {
  width: 100%;
  border-collapse: collapse;
}

.file-table th {
  background: #f9fafb;
  padding: 14px 16px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
}

.file-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #e5e7eb;
  font-size: 14px;
  color: #1a1a2e;
}

.file-table tr:hover {
  background: #f9fafb;
}

.file-table tr.selected {
  background: #eff6ff;
}

.checkbox-col {
  width: 40px;
}

.checkbox-col input {
  width: 18px;
  height: 18px;
  accent-color: #4f46e5;
}

.preview-col {
  width: 60px;
}

.file-preview {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-icon {
  width: 100%;
  height: 100%;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  font-size: 20px;
}

.name-col {
  max-width: 200px;
}

.file-name {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-ext {
  font-size: 12px;
  color: #6b7280;
}

.type-tag {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
}

.type-tag.image {
  background: #dbeafe;
  color: #3b82f6;
}

.type-tag.document {
  background: #fef3c7;
  color: #f59e0b;
}

.type-tag.other {
  background: #f3f4f6;
  color: #6b7280;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
}

.action-col {
  width: 120px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  transition: all 0.2s;
}

.action-btn.view {
  background: #dbeafe;
  color: #3b82f6;
}

.action-btn.view:hover {
  background: #3b82f6;
  color: white;
}

.action-btn.download {
  background: #d1fae5;
  color: #10b981;
}

.action-btn.download:hover {
  background: #10b981;
  color: white;
}

.action-btn.delete {
  background: #fee2e2;
  color: #ef4444;
}

.action-btn.delete:hover {
  background: #ef4444;
  color: white;
}

/* 空状态 */
.empty-state {
  padding: 60px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  color: #d1d5db;
}

.empty-state p {
  margin-top: 16px;
  color: #6b7280;
}

/* 分页 */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 24px;
  background: white;
  border-radius: 12px;
  margin-top: 24px;
}

.prev-btn, .next-btn {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.prev-btn:disabled, .next-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #374151;
}

.size-select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

/* 预览弹窗 */
.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.preview-content {
  max-width: 90vw;
  max-height: 90vh;
  position: relative;
}

.preview-full {
  max-width: 100%;
  max-height: 90vh;
  border-radius: 8px;
}

.preview-placeholder {
  background: white;
  padding: 60px;
  border-radius: 16px;
  text-align: center;
}

.placeholder-icon {
  font-size: 64px;
  color: #4f46e5;
}

.preview-placeholder p {
  margin-top: 16px;
  font-size: 16px;
  color: #374151;
}

.download-link {
  display: inline-block;
  margin-top: 16px;
  padding: 10px 20px;
  background: #4f46e5;
  color: white;
  border-radius: 8px;
  text-decoration: none;
}

.close-btn {
  position: absolute;
  top: -40px;
  right: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
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

@media (max-width: 768px) {
  .file-manage-page {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .filter-section {
    flex-direction: column;
  }
  
  .file-table {
    overflow-x: auto;
  }
}
</style>