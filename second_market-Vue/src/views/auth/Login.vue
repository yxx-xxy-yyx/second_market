<template>
  <div class="login-page">
    <!-- 语言切换 -->
    <div class="lang-switch">
      <LangSwitcher />
    </div>

    <!-- 移动端/平板端设计 -->
    <div class="mobile-container lg:hidden">
      <div class="mobile-background">
        <div class="floating-circle circle-1"></div>
        <div class="floating-circle circle-2"></div>
        <div class="floating-circle circle-3"></div>
      </div>

      <div class="mobile-content">
        <div class="mobile-brand">
          <div class="mobile-logo">
            <img src="https://img.icons8.com/fluency/200/shop.png" alt="智能二手商城" />
          </div>
          <h1 class="mobile-title">{{ $t('login.appName') }}</h1>
          <p class="mobile-subtitle">{{ $t('login.appSlogan') }}</p>
        </div>

        <div class="mobile-form-wrapper">
          <div class="mobile-form-card">
            <h2 class="mobile-form-title">{{ $t('login.title') }}</h2>

            <el-form
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              class="login-form"
              label-position="top"
              size="large"
            >
              <el-form-item :label="$t('common.pleaseInputUid')" prop="uid">
                <el-input
                  v-model="loginForm.uid"
                  :placeholder="$t('common.pleaseInputUid')"
                  clearable
                  class="mobile-input"
                  @keyup.enter="handleLogin"
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item :label="$t('common.school')">
                <el-select
                  v-model="selectedSchool"
                  filterable
                  :placeholder="$t('nav.selectSchool')"
                  style="width: 100%;"
                  class="mobile-select"
                >
                  <el-option
                    v-for="item in schoolStore.schoolList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>

              <el-form-item :label="$t('common.pleaseInputPassword')" prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  :placeholder="$t('common.pleaseInputPassword')"
                  show-password
                  clearable
                  class="mobile-input"
                  @keyup.enter="handleLogin"
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  class="mobile-login-btn"
                  :loading="loading"
                  @click="handleLogin"
                >
                  {{ loading ? $t('common.loading') : $t('common.login') }}
                </el-button>
              </el-form-item>
            </el-form>

            <div class="form-footer">
              <span>{{ $t('login.noAccount') }}</span>
              <el-link type="primary" :underline="false" @click="$router.push('/register')">
                {{ $t('common.register') }}
              </el-link>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 桌面端设计 -->
    <div class="desktop-container hidden lg:block">
      <div class="desktop-background">
        <div class="bg-gradient-1"></div>
        <div class="bg-gradient-2"></div>
        <div class="bg-particles">
          <div v-for="i in 20" :key="i" class="particle" :style="getParticleStyle(i)"></div>
        </div>
      </div>

      <div class="desktop-content">
        <div class="desktop-brand">
          <div class="brand-logo-wrapper">
            <div class="brand-logo">
              <img src="https://img.icons8.com/fluency/200/shop.png" alt="智能二手商城" />
            </div>
            <div class="logo-glow"></div>
          </div>
          <h1 class="desktop-title">{{ $t('login.appName') }}</h1>
          <p class="desktop-slogan">{{ $t('login.appSlogan') }}</p>
        </div>

        <div class="desktop-form-wrapper">
          <div class="desktop-form-card">
            <div class="form-decoration">
              <div class="decoration-circle dec-1"></div>
              <div class="decoration-circle dec-2"></div>
              <div class="decoration-circle dec-3"></div>
            </div>

            <div class="form-card-content">
              <h2 class="desktop-form-title">{{ $t('login.title') }}</h2>
              <p class="desktop-form-subtitle">{{ $t('login.subtitle') }}</p>

              <el-form
                ref="loginFormRef"
                :model="loginForm"
                :rules="loginRules"
                class="login-form"
                label-position="top"
                size="large"
              >
                <el-form-item :label="$t('common.pleaseInputUid')" prop="uid">
                  <el-input
                    v-model="loginForm.uid"
                    :placeholder="$t('common.pleaseInputUid')"
                    clearable
                    class="desktop-input"
                    @keyup.enter="handleLogin"
                  >
                    <template #prefix>
                      <el-icon><User /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item :label="$t('common.school')">
                  <el-select
                    v-model="selectedSchool"
                    filterable
                    :placeholder="$t('nav.selectSchool')"
                    style="width: 100%;"
                    class="desktop-select"
                  >
                    <el-option
                      v-for="item in schoolStore.schoolList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>

                <el-form-item :label="$t('common.pleaseInputPassword')" prop="password">
                  <el-input
                    v-model="loginForm.password"
                    type="password"
                    :placeholder="$t('common.pleaseInputPassword')"
                    show-password
                    clearable
                    class="desktop-input"
                    @keyup.enter="handleLogin"
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item>
                  <el-button
                    type="primary"
                    class="desktop-login-btn"
                    :loading="loading"
                    @click="handleLogin"
                  >
                    {{ loading ? $t('common.loading') : $t('common.login') }}
                  </el-button>
                </el-form-item>
              </el-form>

              <div class="desktop-form-footer">
                <span>{{ $t('login.noAccount') }}</span>
                <el-link type="primary" :underline="false" @click="$router.push('/register')">
                  {{ $t('common.register') }}
                </el-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Lock } from '@element-plus/icons-vue'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'
import { useSchoolStore } from '@/stores/school'

const { locale, t } = useI18n()
const router = useRouter()
const userStore = useUserStore()
const schoolStore = useSchoolStore()

// 为桌面端粒子效果生成随机样式
const getParticleStyle = (i) => {
  const size = Math.random() * 60 + 20
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    width: `${size}px`,
    height: `${size}px`,
    animationDelay: `${Math.random() * 10}s`,
    animationDuration: `${Math.random() * 20 + 20}s`,
    opacity: Math.random() * 0.4 + 0.1
  }
}

const selectedSchool = computed({
  get: () => schoolStore.selectedSchool,
  set: (value) => schoolStore.setSchool(value)
})

onMounted(() => {
  if (!schoolStore.schoolList.length) {
    schoolStore.getSchoolList()
  }
})

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  uid: '',
  password: ''
})

// 多语言校验规则
const loginRules = {
  uid: [
    { required: true, message: t('common.pleaseInputUid'), trigger: 'blur' }
  ],
  password: [
    { required: true, message: t('common.pleaseInputPassword'), trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()

    loading.value = true

    const result = await userStore.login(loginForm)

    if (result.success) {
      if (userStore.user?.role === 'admin') {
        await router.replace('/admin/dashboard')
      } else {
        await router.replace('/user/dashboard')
      }
    }
  } catch (error) {

  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ===== 基础样式 ===== */
* {
  box-sizing: border-box;
}

.lang-switch {
  position: fixed;
  top: 20px;
  right: 24px;
  z-index: var(--z-overlay);
}

.login-page {
  min-height: 100vh;
  overflow: hidden;
  position: relative;
}

/* ================================================
   移动端/平板端样式 (Tailwind: lg:hidden)
   ================================================ */
.mobile-container {
  min-height: 100vh;
  background: linear-gradient(180deg, var(--primary-900) 0%, var(--primary-800) 50%, var(--secondary-900) 100%);
  position: relative;
}

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
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-700) 100%);
  top: -50px;
  left: -50px;
  animation-delay: 0s;
}

.floating-circle.circle-2 {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, var(--accent-500) 0%, var(--accent-700) 100%);
  top: 40%;
  right: -30px;
  animation-delay: 2s;
}

.floating-circle.circle-3 {
  width: 180px;
  height: 180px;
  background: linear-gradient(135deg, var(--primary-300) 0%, var(--primary-500) 100%);
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
  padding: 80px 20px 40px;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.mobile-brand {
  text-align: center;
  margin-bottom: 40px;
}

.mobile-logo {
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-2xl);
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
    box-shadow: 0 8px 50px rgba(54, 179, 194, 0.4),
                inset 0 0 30px rgba(255, 255, 255, 0.2);
  }
}

.mobile-logo img {
  width: 80px;
  height: 80px;
  object-fit: contain;
}

.mobile-title {
  font-size: 32px;
  font-weight: var(--font-bold);
  background: linear-gradient(135deg, var(--primary-300) 0%, var(--accent-400) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.mobile-subtitle {
  font-size: var(--font-md);
  color: rgba(255, 255, 255, 0.7);
  font-weight: var(--font-medium);
}

.mobile-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mobile-form-card {
  width: 100%;
  max-width: 400px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(40px);
  -webkit-backdrop-filter: blur(40px);
  border-radius: var(--radius-2xl);
  padding: 40px 28px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3),
              0 0 60px rgba(54, 179, 194, 0.1);
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
  font-size: var(--font-3xl);
  font-weight: var(--font-bold);
  color: white;
  text-align: center;
  margin-bottom: 32px;
}

/* ===== Element Plus 组件覆盖 - 移动端 ===== */
.mobile-input :deep(.el-input__wrapper),
.mobile-select :deep(.el-select__wrapper) {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: var(--radius-xl);
  padding: 12px 16px;
  box-shadow: none;
  transition: all 0.3s ease;
}

.mobile-input :deep(.el-input__wrapper:hover),
.mobile-select :deep(.el-select__wrapper:hover) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(54, 179, 194, 0.5);
}

.mobile-input :deep(.el-input__wrapper.is-focus),
.mobile-select :deep(.el-select__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.25);
  border-color: var(--primary-500);
  box-shadow: 0 0 20px rgba(54, 179, 194, 0.3);
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
  font-weight: var(--font-semibold);
}

.mobile-login-btn {
  width: 100%;
  height: 54px;
  font-size: var(--font-lg);
  font-weight: var(--font-bold);
  border-radius: var(--radius-xl);
  background: var(--gradient-primary) !important;
  border: none;
  box-shadow: 0 8px 25px rgba(54, 179, 194, 0.4);
  transition: all 0.3s ease;
}

.mobile-login-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(54, 179, 194, 0.5);
}

.mobile-login-btn:active {
  transform: translateY(-1px);
}

.form-footer {
  text-align: center;
  font-size: var(--font-md);
  color: rgba(255, 255, 255, 0.8);
}

.form-footer a {
  color: var(--primary-300);
  font-weight: var(--font-bold);
}

/* ================================================
   桌面端样式 (Tailwind: hidden lg:block)
   ================================================ */
.desktop-container {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--primary-950) 0%, var(--primary-900) 50%, var(--secondary-900) 100%);
  position: relative;
  overflow: hidden;
}

.desktop-background {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.bg-gradient-1 {
  position: absolute;
  width: 800px;
  height: 800px;
  background: radial-gradient(circle, rgba(54, 179, 194, 0.35) 0%, transparent 70%);
  top: -200px;
  left: -200px;
  animation: pulse-bg 15s ease-in-out infinite;
}

.bg-gradient-2 {
  position: absolute;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.25) 0%, transparent 70%);
  bottom: -100px;
  right: -100px;
  animation: pulse-bg 12s ease-in-out infinite reverse;
}

@keyframes pulse-bg {
  0%, 100% {
    transform: scale(1);
    opacity: 0.8;
  }
  50% {
    transform: scale(1.1);
    opacity: 1;
  }
}

.bg-particles {
  position: absolute;
  inset: 0;
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-400) 0%, var(--primary-600) 100%);
  animation: float-particle 20s linear infinite;
}

@keyframes float-particle {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) rotate(720deg);
    opacity: 0;
  }
}

.desktop-content {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: flex;
  padding: 40px 80px;
  gap: 80px;
}

.desktop-brand {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
}

.brand-logo-wrapper {
  position: relative;
  margin-bottom: 40px;
}

.brand-logo {
  width: 180px;
  height: 180px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: var(--radius-3xl);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 2;
  animation: logo-float 6s ease-in-out infinite;
}

@keyframes logo-float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
}

.brand-logo img {
  width: 120px;
  height: 120px;
  object-fit: contain;
}

.logo-glow {
  position: absolute;
  inset: -20px;
  background: radial-gradient(circle, rgba(54, 179, 194, 0.6) 0%, transparent 70%);
  z-index: 1;
  animation: glow-pulse 4s ease-in-out infinite;
  filter: blur(30px);
}

@keyframes glow-pulse {
  0%, 100% {
    opacity: 0.6;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }
}

.desktop-title {
  font-size: 64px;
  font-weight: var(--font-black);
  background: linear-gradient(135deg, var(--primary-300) 0%, var(--accent-400) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  line-height: 1.1;
  letter-spacing: -1px;
}

.desktop-slogan {
  font-size: var(--font-2xl);
  color: rgba(255, 255, 255, 0.75);
  font-weight: var(--font-medium);
  margin-bottom: 40px;
}

.desktop-form-wrapper {
  flex: 0 0 480px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.desktop-form-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(50px);
  -webkit-backdrop-filter: blur(50px);
  border-radius: var(--radius-3xl);
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.4);
  position: relative;
  overflow: hidden;
  animation: card-appear 0.8s ease-out;
}

@keyframes card-appear {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.form-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-700) 100%);
  opacity: 0.1;
}

.decoration-circle.dec-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -100px;
}

.decoration-circle.dec-2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  left: -50px;
  background: linear-gradient(135deg, var(--accent-500) 0%, var(--accent-700) 100%);
}

.decoration-circle.dec-3 {
  width: 100px;
  height: 100px;
  top: 50%;
  right: -30px;
  background: linear-gradient(135deg, var(--primary-300) 0%, var(--primary-500) 100%);
}

.form-card-content {
  position: relative;
  z-index: 1;
  padding: 56px 48px;
}

.desktop-form-title {
  font-size: 36px;
  font-weight: var(--font-bold);
  color: white;
  margin-bottom: 8px;
}

.desktop-form-subtitle {
  font-size: var(--font-md);
  color: rgba(255, 255, 255, 0.65);
  margin-bottom: 40px;
}

/* ===== Element Plus 组件覆盖 - 桌面端 ===== */
.desktop-input :deep(.el-input__wrapper),
.desktop-select :deep(.el-select__wrapper) {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-2xl);
  padding: 16px 20px;
  box-shadow: none;
  transition: all 0.4s ease;
}

.desktop-input :deep(.el-input__wrapper:hover),
.desktop-select :deep(.el-select__wrapper:hover) {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(54, 179, 194, 0.5);
}

.desktop-input :deep(.el-input__wrapper.is-focus),
.desktop-select :deep(.el-select__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.2);
  border-color: var(--primary-500);
  box-shadow: 0 0 30px rgba(54, 179, 194, 0.25);
}

.desktop-input :deep(.el-input__inner),
.desktop-input :deep(.el-input__prefix),
.desktop-input :deep(.el-input__suffix),
.desktop-select :deep(.el-input__inner) {
  color: white;
  font-size: var(--font-lg);
}

.desktop-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.45);
}

.desktop-input :deep(.el-form-item__label),
.desktop-select :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.85);
  font-weight: var(--font-semibold);
  font-size: var(--font-md);
  margin-bottom: 8px;
}

.desktop-login-btn {
  width: 100%;
  height: 60px;
  font-size: var(--font-lg);
  font-weight: var(--font-bold);
  border-radius: var(--radius-2xl);
  background: var(--gradient-primary) !important;
  border: none;
  box-shadow: 0 10px 35px rgba(54, 179, 194, 0.4);
  transition: all 0.4s ease;
  margin-top: 8px;
}

.desktop-login-btn:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 15px 45px rgba(54, 179, 194, 0.5);
}

.desktop-login-btn:active {
  transform: translateY(-2px) scale(0.99);
}

.desktop-form-footer {
  text-align: center;
  font-size: var(--font-lg);
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8px;
}

.desktop-form-footer a {
  color: var(--primary-400);
  font-weight: var(--font-bold);
}

/* ================================================
   平板端适配 (Tailwind: lg = 1024px)
   ================================================ */
@media (min-width: 1024px) and (max-width: 1280px) {
  .desktop-content {
    padding: 40px;
    gap: 40px;
  }

  .desktop-title {
    font-size: 48px;
  }

  .desktop-slogan {
    font-size: var(--font-xl);
  }

  .desktop-form-wrapper {
    flex-basis: 420px;
  }

  .form-card-content {
    padding: 48px 36px;
  }
}
</style>
