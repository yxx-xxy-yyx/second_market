<template>
  <div class="avatar-upload">
    <el-upload
      class="avatar-uploader"
      :action="uploadUrl"
      :headers="uploadHeaders"
      :show-file-list="false"
      :on-success="handleAvatarSuccess"
      :on-error="handleAvatarError"
      :before-upload="beforeAvatarUpload"
      :on-progress="handleProgress"
      name="file"
      accept="image/*"
    >
      <div class="avatar-container">
        <img 
          v-if="avatarUrl" 
          :src="avatarUrl" 
          class="avatar"
          :alt="$t('avatarUpload.avatarAlt')"
        />
        <div v-else class="avatar-placeholder">
          <el-icon class="avatar-icon" :size="50">
            <Plus />
          </el-icon>
          <div class="avatar-text">{{ $t('avatarUpload.clickUpload') }}</div>
        </div>
        
        <!-- 上传进度遮罩 -->
        <div v-if="uploading" class="upload-overlay">
          <el-progress 
            type="circle" 
            :percentage="uploadProgress"
            :width="60"
            :show-text="false"
          />
          <div class="upload-text">{{ $t('avatarUpload.uploading') }}</div>
        </div>
        
        <!-- 悬停遮罩 -->
        <div class="hover-overlay">
          <el-icon :size="24">
            <Camera />
          </el-icon>
          <div class="hover-text">{{ $t('avatarUpload.changeAvatar') }}</div>
        </div>
      </div>
    </el-upload>
    
    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Camera } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { formatAvatarUrl } from '@/utils/url'
import { userApi } from '@/api/user'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  size: {
    type: Number,
    default: 120
  },
  maxSize: {
    type: Number,
    default: 5 // MB
  }
})

const emit = defineEmits(['update:modelValue', 'success', 'error'])

const userStore = useUserStore()

const avatarUrl = ref(formatAvatarUrl(props.modelValue))
const uploading = ref(false)
const uploadProgress = ref(0)
const errorMessage = ref('')

// 上传URL和请求头
const uploadUrl = computed(() => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
  return `${baseUrl}/file/upload/avatar`
})

const uploadHeaders = computed(() => {
  return {
    'Authorization': `Bearer ${userStore.token}`
  }
})

// 监听props变化
watch(() => props.modelValue, (newVal) => {
  avatarUrl.value = formatAvatarUrl(newVal)
})

// 上传前验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < props.maxSize

  if (!isImage) {
    errorMessage.value = t('avatarUpload.onlyImage')
    ElMessage.error(t('avatarUpload.onlyImage'))
    return false
  }
  if (!isLt5M) {
    errorMessage.value = t('avatarUpload.exceedSize', { size: props.maxSize })
    ElMessage.error(t('avatarUpload.exceedSize', { size: props.maxSize }))
    return false
  }
  
  errorMessage.value = ''
  uploading.value = true
  uploadProgress.value = 0
  
  return true
}

// 上传进度
const handleProgress = (event) => {
  uploadProgress.value = Math.round(event.percent)
}

// 上传成功
const handleAvatarSuccess = async (response) => {
  uploading.value = false
  uploadProgress.value = 0
  
  try {
    let fileUrl = ''
    
    if (response.code === '200') {
      fileUrl = response.data?.fileUrl || response.data?.filePath || response.data
    } else if (response.success) {
      fileUrl = response.data?.fileUrl || response.data?.filePath || response.data
    } else {
      throw new Error(response.message || t('avatarUpload.uploadFail'))
    }
    
    if (!fileUrl) {
      throw new Error(t('avatarUpload.noFileUrl'))
    }
    
    const formattedUrl = formatAvatarUrl(fileUrl)
    avatarUrl.value = formattedUrl
    
    try {
      const updateRes = await userApi.updateAvatar(fileUrl)
      if (updateRes.code === '200') {
        ElMessage.success(t('avatarUpload.uploadSuccess'))
        emit('update:modelValue', fileUrl)
        emit('success', { fileUrl })
      } else {
        throw new Error(updateRes.message || t('avatarUpload.updateFail'))
      }
    } catch (updateError) {
      console.error('更新头像失败:', updateError)
      if (updateError.response?.status === 404) {
        ElMessage.error(t('avatarUpload.apiNotFound'))
      } else {
        ElMessage.warning(t('avatarUpload.uploadedButUpdateFail'))
      }
      emit('success', { fileUrl })
    }
  } catch (error) {
    handleAvatarError(error)
  }
}

// 上传失败
const handleAvatarError = (error) => {
  uploading.value = false
  uploadProgress.value = 0
  
  console.error('头像上传失败:', error)
  errorMessage.value = t('avatarUpload.uploadFailRetry')
  ElMessage.error(t('avatarUpload.uploadFailRetry'))
  emit('error', error)
}
</script>

<style scoped>
.avatar-upload {
  display: inline-block;
}

.avatar-uploader {
  display: block;
}

.avatar-container {
  position: relative;
  width: v-bind("props.size + 'px'");
  height: v-bind("props.size + 'px'");
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-container:hover {
  border-color: #409eff;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
  background: #fafafa;
}

.avatar-icon {
  margin-bottom: 8px;
}

.avatar-text {
  font-size: 12px;
  text-align: center;
  line-height: 1.2;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}

.upload-text {
  margin-top: 10px;
  font-size: 14px;
}

.hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-container:hover .hover-overlay {
  opacity: 1;
}

.hover-text {
  margin-top: 5px;
  font-size: 12px;
}

.error-message {
  margin-top: 8px;
  color: #f56c6c;
  font-size: 12px;
  text-align: center;
}

/* 隐藏Element Plus默认的上传按钮样式 */
.avatar-uploader :deep(.el-upload) {
  border: none;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader :deep(.el-upload):hover {
  border-color: #409eff;
}
</style>