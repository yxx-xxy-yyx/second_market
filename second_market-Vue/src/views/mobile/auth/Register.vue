<template>
  <div class="mobile-register-page">
    <!-- 语言切换 -->
    <div class="lang-switch">
      <LangSwitcher />
    </div>

    <!-- 背景装饰 -->
    <div class="mobile-background">
      <div class="floating-circle circle-1"></div>
      <div class="floating-circle circle-2"></div>
      <div class="floating-circle circle-3"></div>
    </div>

    <div class="mobile-content">
      <!-- 品牌区 -->
      <div class="mobile-brand">
        <div class="mobile-logo">
          <img src="https://img.icons8.com/fluency/200/shop.png" alt="智能二手商城" />
        </div>
        <h1 class="mobile-title">{{ $t('login.appName') }}</h1>
        <p class="mobile-subtitle">{{ $t('login.appSlogan') }}</p>
      </div>

      <!-- 表单卡片 -->
      <div class="mobile-form-wrapper">
        <div class="mobile-form-card">
          <h2 class="mobile-form-title">{{ $t('register.title') }}</h2>

          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="register-form"
            label-position="top"
            size="large"
          >
            <el-form-item :label="$t('common.pleaseInputUid')" prop="uid">
              <el-input
                v-model="registerForm.uid"
                :placeholder="$t('common.pleaseInputUid')"
                clearable
                class="mobile-input"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item :label="$t('common.pleaseInputNickname')" prop="nickname">
              <el-input
                v-model="registerForm.nickname"
                :placeholder="$t('common.pleaseInputNickname')"
                clearable
                class="mobile-input"
              >
                <template #prefix>
                  <el-icon><Avatar /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item :label="$t('common.pleaseInputEmail')" prop="email">
              <el-input
                v-model="registerForm.email"
                :placeholder="$t('common.pleaseInputEmail')"
                clearable
                class="mobile-input"
              >
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item :label="$t('common.school')" prop="schoolId">
              <el-select
                v-model="registerForm.schoolId"
                :placeholder="$t('common.pleaseSelectSchool')"
                class="mobile-select"
                clearable
                filterable
              >
                <template #prefix>
                  <el-icon><School /></el-icon>
                </template>
                <el-option
                  v-for="item in schools"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item :label="$t('common.pleaseInputPassword')" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                :placeholder="$t('common.pleaseInputPassword')"
                show-password
                clearable
                class="mobile-input"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item :label="$t('register.confirmPassword')" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                :placeholder="$t('register.confirmPasswordPlaceholder')"
                show-password
                clearable
                class="mobile-input"
                @keyup.enter="handleRegister"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                class="mobile-register-btn"
                :loading="loading"
                @click="handleRegister"
              >
                {{ loading ? $t('register.registering') : $t('common.register') }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <span>{{ $t('register.hasAccount') }}</span>
            <el-link type="primary" :underline="false" @click="$router.push('/login')">
              {{ $t('common.login') }}
            </el-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Lock, Message, Avatar, School } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { schoolApi } from '@/api/school'

const { t, locale } = useI18n()
const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref()
const loading = ref(false)
const schools = ref([])

const registerForm = reactive({
  uid: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
  schoolId: ''
})

// 获取学校列表
const fetchSchools = async () => {
  try {
    const res = await schoolApi.getSchoolList({ language: locale.value })
    if (res.code === '200' || res.code === 200) {
      schools.value = res.data
    }
  } catch (error) {
    console.error('Failed to fetch schools:', error)
  }
}

// 监听语言变化
watch(locale, () => {
  fetchSchools()
})

onMounted(() => {
  fetchSchools()
})

// 密码校验
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error(t('register.confirmPasswordRequired')))
  } else if (value !== registerForm.password) {
    callback(new Error(t('register.passwordNotMatch')))
  } else {
    callback()
  }
}

// 多语言校验规则
const registerRules = {
  uid: [
    { required: true, message: t('common.pleaseInputUid'), trigger: 'blur' },
    { min: 3, max: 20, message: t('register.uidLength'), trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: t('common.pleaseInputNickname'), trigger: 'blur' }
  ],
  email: [
    { required: true, message: t('common.pleaseInputEmail'), trigger: 'blur' },
    { type: 'email', message: t('register.emailValid'), trigger: 'blur' }
  ],
  schoolId: [
    { required: true, message: t('common.pleaseSelectSchool'), trigger: 'change' }
  ],
  password: [
    { required: true, message: t('common.pleaseInputPassword'), trigger: 'blur' },
    { min: 6, max: 20, message: t('register.passwordLength'), trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()

    loading.value = true

    const result = await userStore.register({
      uid: registerForm.uid,
      nickname: registerForm.nickname,
      email: registerForm.email,
      password: registerForm.password,
      schoolId: registerForm.schoolId
    })

    if (result.success) {
      router.push('/login')
    }
  } catch (error) {

  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 基础样式 */
* {
  box-sizing: border-box;
}

.lang-switch {
  position: fixed;
  top: 16px;
  right: 16px;
  z-index: 9999;
}

.mobile-register-page {
  min-height: 100vh;
  overflow: hidden;
  position: relative;
  background: linear-gradient(180deg, #042f3d 0%, #0a4a5e 50%, #063344 100%);
}

/* 背景装饰 */
.mobile-background {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.floating-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.3;
  animation: float 8s ease-in-out infinite;
}

.floating-circle.circle-1 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  top: -50px;
  left: -50px;
  animation-delay: 0s;
}

.floating-circle.circle-2 {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, #14b8a6 0%, #0d9488 100%);
  top: 40%;
  right: -30px;
  animation-delay: 2s;
}

.floating-circle.circle-3 {
  width: 180px;
  height: 180px;
  background: linear-gradient(135deg, #22d3ee 0%, #06b6d4 100%);
  bottom: -60px;
  left: 30%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(10deg);
  }
}

.mobile-content {
  position: relative;
  z-index: 1;
  padding: 60px 20px 40px;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 品牌区 */
.mobile-brand {
  text-align: center;
  margin-bottom: 24px;
}

.mobile-logo {
  width: 100px;
  height: 100px;
  margin: 0 auto 20px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3),
              inset 0 0 20px rgba(255, 255, 255, 0.1);
  animation: pulse-glow 3s ease-in-out infinite;
}

@keyframes pulse-glow {
  0%, 100% {
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3),
                inset 0 0 20px rgba(255, 255, 255, 0.1);
  }
  50% {
    box-shadow: 0 8px 50px rgba(6, 182, 212, 0.4),
                inset 0 0 30px rgba(255, 255, 255, 0.2);
  }
}

.mobile-logo img {
  width: 64px;
  height: 64px;
  object-fit: contain;
}

.mobile-title {
  font-size: 28px;
  font-weight: 900;
  background: linear-gradient(135deg, #06b6d4 0%, #14b8a6 50%, #22d3ee 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.mobile-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

/* 表单卡片 */
.mobile-form-wrapper {
  flex: 1;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

.mobile-form-card {
  width: 100%;
  max-width: 380px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(40px);
  border-radius: 28px;
  padding: 32px 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3),
              0 0 60px rgba(6, 182, 212, 0.1);
  animation: slide-up 0.8s ease-out;
}

@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.mobile-form-title {
  font-size: 24px;
  font-weight: 800;
  color: white;
  text-align: center;
  margin-bottom: 28px;
}

/* 输入框样式 */
.mobile-input :deep(.el-input__wrapper),
.mobile-select :deep(.el-select__wrapper) {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 16px;
  padding: 14px 16px;
  box-shadow: none;
  transition: all 0.3s ease;
}

.mobile-input :deep(.el-input__wrapper:hover),
.mobile-select :deep(.el-select__wrapper:hover) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(6, 182, 212, 0.5);
}

.mobile-input :deep(.el-input__wrapper.is-focus),
.mobile-select :deep(.el-select__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.25);
  border-color: #06b6d4;
  box-shadow: 0 0 20px rgba(6, 182, 212, 0.3);
}

.mobile-input :deep(.el-input__inner),
.mobile-input :deep(.el-input__prefix),
.mobile-input :deep(.el-input__suffix),
.mobile-select :deep(.el-input__inner) {
  color: white;
}

.mobile-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

.mobile-input :deep(.el-form-item__label),
.mobile-select :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.85);
  font-weight: 600;
  font-size: 14px;
}

/* 下拉选择器箭头颜色 */
.mobile-select :deep(.el-select__caret) {
  color: rgba(255, 255, 255, 0.7);
}

/* 注册按钮 */
.mobile-register-btn {
  width: 100%;
  height: 52px;
  font-size: 17px;
  font-weight: 700;
  border-radius: 16px;
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  border: none;
  box-shadow: 0 8px 25px rgba(6, 182, 212, 0.4);
  transition: all 0.3s ease;
  margin-top: 8px;
}

.mobile-register-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(6, 182, 212, 0.5);
}

.mobile-register-btn:active {
  transform: translateY(-1px);
}

/* 表单底部 */
.form-footer {
  text-align: center;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 20px;
}

.form-footer a {
  color: #06b6d4;
  font-weight: 700;
}
</style>
