<template>
  <div class="login-page flex flex-col lg:flex-row">
    <!-- 右上角语言切换下拉 -->
    <div class="lang-switch">
      <LangSwitcher />
    </div>

    <div class="brand-section hidden lg:flex">
      <div class="brand-content">
        <div class="logo-container">
          <div class="logo">
            <img src="https://img.icons8.com/fluency/200/shop.png" alt="智能二手商城" />
          </div>
        </div>
        <h1 class="brand-title">{{ $t('login.appName') }}</h1>
        <p class="brand-slogan">{{ $t('login.appSlogan') }}</p>
        <div class="brand-features">
          <div class="feature-item">
            <div class="feature-icon-box">{{ $t('login.feature1Desc') }}</div>
            <span>{{ $t('login.feature1') }}</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon-box">{{ $t('login.feature2Desc') }}</div>
            <span>{{ $t('login.feature2') }}</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon-box">{{ $t('login.feature3Desc') }}</div>
            <span>{{ $t('login.feature3') }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="form-section flex-1">
      <div class="decorative-circle circle-1"></div>
      <div class="decorative-circle circle-2"></div>

      <div class="form-content">
        <div class="form-header">
          <h2 class="form-title">{{ $t('login.title') }}</h2>
          <p class="form-subtitle">{{ $t('login.subtitle') }}</p>
        </div>

        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form" label-position="top"
          size="large">
          <el-form-item :label="$t('common.pleaseInputUid')" prop="uid">
            <el-input v-model="loginForm.uid" :placeholder="$t('common.pleaseInputUid')" clearable
              @keyup.enter="handleLogin">
              <template #prefix>
                <el-icon>
                  <User />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item :label="$t('common.pleaseInputPassword')" prop="password">
            <el-input v-model="loginForm.password" type="password" :placeholder="$t('common.pleaseInputPassword')"
              show-password clearable @keyup.enter="handleLogin">
              <template #prefix>
                <el-icon>
                  <Lock />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
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

        <!--<div class="demo-accounts">  隐藏测试账户
          <el-divider>{{ $t('login.testAccount') }}</el-divider>
          <p class="demo-text">{{ $t('login.admin') }}：admin / EchoOfMemories11</p>
          <p class="demo-text">{{ $t('login.user') }}：user01 / EchoOfMemories11</p>
        </div>-->
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Lock } from '@element-plus/icons-vue'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const router = useRouter()
const userStore = useUserStore()

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
/* 语言切换样式 */
.lang-switch {
  position: fixed;
  top: 20px;
  right: 24px;
  z-index: 999;
}

.login-page {
  min-height: 100vh;
  display: flex;
  background: #FFFFFF;
  overflow: hidden;
  position: relative;
}

.brand-section {
  flex: 0 0 60%;
  background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  overflow: hidden;
}

.brand-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(1200px 600px at 70% 20%, rgba(25, 118, 210, 0.22) 0%, rgba(255, 255, 255, 0) 60%),
    radial-gradient(900px 500px at 20% 70%, rgba(25, 118, 210, 0.16) 0%, rgba(255, 255, 255, 0) 55%);
  z-index: 0;
}

.brand-content {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 500px;
}

.logo-container {
  margin-bottom: 30px;
}

.logo {
  width: 200px;
  height: 200px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.logo img {
  width: 150px;
  height: 150px;
  object-fit: contain;
}

.brand-title {
  font-size: 42px;
  font-weight: 700;
  color: #1976D2;
  margin: 20px 0 10px 0;
  letter-spacing: 2px;
}

.brand-slogan {
  font-size: 20px;
  color: #0D47A1;
  margin: 0 0 40px 0;
}

.brand-features {
  display: flex;
  justify-content: center;
  gap: 30px;
  flex-wrap: wrap;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  min-width: 120px;
}

.feature-icon-box {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #42a5f5 0%, #478ed1 100%);
  color: white;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.feature-item span:last-child {
  font-size: 14px;
  color: #424242;
  font-weight: 500;
}

.form-section {
  flex: 0 0 40%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  position: relative;
  background: #FFFFFF;
}

.decorative-circle {
  position: absolute;
  border-radius: 50%;
  z-index: 0;
}

.circle-1 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #FF9800 0%, #FF5722 100%);
  opacity: 0.15;
  top: -100px;
  right: -100px;
}

.circle-2 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
  opacity: 0.15;
  bottom: -50px;
  left: -50px;
}

.form-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 400px;
  background: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-title {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.form-subtitle {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}

.login-form {
  margin-bottom: 24px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
}

.form-footer {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-bottom: 24px;
}

.demo-accounts {
  text-align: center;
  font-size: 12px;
  color: #999;
}

.demo-text {
  margin: 4px 0;
}

@media (max-width: 768px) {
  .login-page {
    flex-direction: column;
  }

  .brand-section {
    flex: 0 0 auto;
    min-height: 300px;
    padding: 40px 20px;
  }

  .logo {
    width: 120px;
    height: 120px;
  }

  .logo img {
    width: 80px;
    height: 80px;
  }

  .brand-title {
    font-size: 28px;
  }

  .brand-slogan {
    font-size: 16px;
  }

  .form-section {
    flex: 1;
    padding: 40px 20px;
  }

  .form-content {
    padding: 30px 20px;
  }

  .form-title {
    font-size: 26px;
  }

  .lang-wrapper {
    display: flex;
    justify-content: center;
    margin-bottom: 12px;
  }
}
</style>
