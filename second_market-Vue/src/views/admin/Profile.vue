<template>
  <div class="admin-profile">
    <div class="page-header">
      <h1 class="page-title">{{ $t('Users.profileTitle') }}</h1>
      <p class="page-subtitle">{{ $t('Users.profileSubtitle') }}</p>
    </div>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card" :header="$t('Users.avatarSettings')">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <AvatarUpload v-model="profileForm.avatar" :size="120" @success="handleAvatarSuccess"
                @error="handleAvatarError" />
            </div>
            <div class="avatar-info">
              <div class="username">{{ profileForm.username }}</div>
              <div class="user-role">
                <el-tag type="danger" size="small">
                  {{ $t('Users.role.admin') }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="stats-card" :header="$t('Users.accountStats')" style="margin-top: 20px;">
          <div class="stats-list">
            <div class="stats-item">
              <span class="stats-label">{{ $t('Users.registerTime') }}</span>
              <span class="stats-value">{{ profileForm.createTime }}</span>
            </div>
            <div class="stats-item">
              <span class="stats-label">{{ $t('Users.accountStatus') }}</span>
              <span class="stats-value">
                <el-tag :type="profileForm.status === 1 ? 'success' : 'danger'" size="small">
                  {{ profileForm.status === 1 ? $t('Users.status.normal') : $t('Users.status.disabled') }}
                </el-tag>
              </span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="form-card" :header="$t('Users.basicInfo')">
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px" size="large">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item :label="$t('Users.username')" prop="username">
                  <el-input v-model="profileForm.username" disabled :placeholder="$t('Users.usernameDisabled')" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item :label="$t('Users.nickname')" prop="nickname">
                  <el-input v-model="profileForm.nickname" :placeholder="$t('Users.nicknamePlaceholder')" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item :label="$t('Users.email')" prop="email">
                  <el-input v-model="profileForm.email" :placeholder="$t('Users.emailPlaceholder')" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item :label="$t('Users.phone')" prop="phone">
                  <el-input v-model="profileForm.phone" :placeholder="$t('Users.phonePlaceholder')" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item :label="$t('Users.genders')" prop="gender">
                  <el-select v-model="profileForm.gender" :placeholder="$t('Users.genderPlaceholder')"
                    style="width: 100%">
                    <el-option :label="$t('Users.gender.male')" value="male" />
                    <el-option :label="$t('Users.gender.female')" value="female" />
                    <el-option :label="$t('Users.gender.secret')" value="secret" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item :label="$t('Users.birthday')" prop="birthday">
                  <el-date-picker v-model="profileForm.birthday" type="date"
                    :placeholder="$t('Users.birthdayPlaceholder')" format="YYYY-MM-DD" value-format="YYYY-MM-DD"
                    style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item>
              <el-button type="primary" @click="handleSave" :loading="saving" class="save-btn">
                {{ saving ? $t('Users.saving') : $t('Users.saveInfo') }}
              </el-button>
              <el-button @click="handleReset" class="ml-10">
                {{ $t('Users.reset') }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="password-card" :header="$t('Users.changePassword')" style="margin-top: 20px;">
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px" size="large">
            <el-form-item :label="$t('Users.currentPassword')" prop="currentPassword">
              <el-input v-model="passwordForm.currentPassword" type="password"
                :placeholder="$t('Users.currentPasswordPlaceholder')" show-password clearable />
            </el-form-item>

            <el-form-item :label="$t('Users.newPassword')" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password"
                :placeholder="$t('Users.newPasswordPlaceholder')" show-password clearable />
            </el-form-item>

            <el-form-item :label="$t('Users.confirmPassword')" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password"
                :placeholder="$t('Users.confirmPasswordPlaceholder')" show-password clearable />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handlePasswordChange" :loading="passwordSaving">
                {{ passwordSaving ? $t('Users.changing') : $t('Users.changePasswordBtn') }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import AvatarUpload from '@/components/AvatarUpload.vue'
import { changePasswordApi, updateAvatarApi, updateUserInfoApi, getUserInfoApi } from '@/api/user'

const userStore = useUserStore()

const profileFormRef = ref()
const passwordFormRef = ref()
const saving = ref(false)
const passwordSaving = ref(false)

const profileForm = reactive({
  id: '',
  username: '',
  nickname: '',
  email: '',
  phone: '',
  gender: '',
  birthday: '',
  avatar: '',
  role: '',
  status: 1,
  createTime: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  nickname: [
    { max: 20, message: '昵称不能超过20个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ]
}

const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleSave = async () => {
  try {
    await profileFormRef.value.validate()

    saving.value = true

    const updateData = {
      nickname: profileForm.nickname,
      email: profileForm.email,
      phone: profileForm.phone,
      gender: profileForm.gender,
      birthday: profileForm.birthday
    }

    const response = await updateUserInfoApi(updateData)
    if (response.success) {
      await loadUserInfo()
      userStore.setUser({ ...userStore.user, ...updateData })
      ElMessage.success('个人信息保存成功')
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

const handleReset = () => {
  ElMessageBox.confirm('确定要重置所有修改吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loadUserInfo()
    ElMessage.success('已重置')
  }).catch(() => { })
}

const handlePasswordChange = async () => {
  try {
    await passwordFormRef.value.validate()

    passwordSaving.value = true

    const response = await changePasswordApi(
      passwordForm.currentPassword,
      passwordForm.newPassword
    )

    if (response.success) {
      Object.keys(passwordForm).forEach(key => {
        passwordForm[key] = ''
      })
      ElMessage.success('密码修改成功')
    } else {
      ElMessage.error(response.message || '密码修改失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改密码失败，请稍后重试')
  } finally {
    passwordSaving.value = false
  }
}

const handleAvatarSuccess = async (fileInfo) => {
  try {
    const response = await updateAvatarApi(fileInfo.fileUrl)
    if (response.success) {
      await loadUserInfo()
      ElMessage.success('头像更新成功')
    } else {
      ElMessage.error(response.message || '头像更新失败')
    }
  } catch (error) {
    console.error('头像更新失败:', error)
    ElMessage.error('头像更新失败，请稍后重试')
  }
}

const handleAvatarError = (error) => {
  console.error('头像上传失败:', error)
}

const loadUserInfo = async () => {
  try {
    const response = await getUserInfoApi()
    if (response.success && response.data) {
      const user = response.data
      const genderMap = { M: 'male', F: 'female', P: 'secret' };
      Object.assign(profileForm, {
        id: user.id,
        username: user.username,
        nickname: user.nickname || '',
        email: user.email || '',
        phone: user.phone || '',
        gender: genderMap[user.gender] || user.gender || '',
        birthday: user.birthday || '',
        avatar: user.avatar || '',
        role: user.role,
        status: user.status,
        createTime: user.createTime ? new Date(user.createTime).toLocaleString('zh-CN') : ''
      })
      userStore.setUser(user)
    } else {
      // API失败时使用默认数据
      const defaultUser = {
        id: 1,
        username: 'admin',
        nickname: '系统管理员',
        email: 'admin@example.com',
        phone: '13800138000',
        gender: 'secret',
        birthday: '1990-01-01',
        avatar: '',
        role: 'admin',
        status: 1,
        createTime: new Date().toLocaleString('zh-CN')
      }
      Object.assign(profileForm, defaultUser)
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    // API失败时使用默认数据
    const defaultUser = {
      id: 1,
      username: 'admin',
      nickname: '系统管理员',
      email: 'admin@example.com',
      phone: '13800138000',
      gender: 'secret',
      birthday: '1990-01-01',
      avatar: '',
      role: 'admin',
      status: 1,
      createTime: new Date().toLocaleString('zh-CN')
    }
    Object.assign(profileForm, defaultUser)
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.admin-profile {
  padding: 0;
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

.page-subtitle {
  color: #909399;
  margin: 0;
}

.profile-card,
.form-card,
.stats-card,
.password-card {
  border: none;
  border-radius: 12px;
}

.profile-card :deep(.el-card__header),
.form-card :deep(.el-card__header),
.stats-card :deep(.el-card__header),
.password-card :deep(.el-card__header) {
  border-bottom: 1px solid #f0f2f5;
  font-weight: 600;
  color: #303133;
}

.avatar-section {
  text-align: center;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.avatar-info .username {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.avatar-info .user-role {
  display: flex;
  justify-content: center;
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.stats-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f2f5;
}

.stats-item:last-child {
  border-bottom: none;
}

.stats-label {
  color: #606266;
  font-size: 14px;
}

.stats-value {
  color: #303133;
  font-weight: 500;
  font-size: 14px;
}

.save-btn {
  padding: 12px 30px;
}

.ml-10 {
  margin-left: 10px;
}

@media (max-width: 992px) {
  .admin-profile .el-col {
    margin-bottom: 20px;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 20px;
  }
}
</style>
