<template>
  <div class="register-page flex flex-col lg:flex-row">
    <!-- 右上角语言切换 -->
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
            <div class="feature-icon-box">{{ $t('register.feature1Desc') }}</div>
            <span>{{ $t('register.feature1') }}</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon-box">{{ $t('register.feature2Desc') }}</div>
            <span>{{ $t('register.feature2') }}</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon-box">{{ $t('register.feature3Desc') }}</div>
            <span>{{ $t('register.feature3') }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="form-section flex-1">
      <div class="decorative-circle circle-1"></div>
      <div class="decorative-circle circle-2"></div>

      <div class="form-content">
        <div class="form-header">
          <h2 class="form-title">{{ $t('register.title') }}</h2>
          <p class="form-subtitle">{{ $t('register.subtitle') }}</p>
        </div>

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
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item :label="$t('common.pleaseInputPassword')" prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              :placeholder="$t('common.pleaseInputPassword')"
              show-password
              clearable
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
              @keyup.enter="handleRegister"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item :label="$t('common.school')" prop="schoolId">
            <el-select
              v-model="registerForm.schoolId"
              filterable
              class="register-input"
              :placeholder="$t('nav.selectSchool')"
              @change="schoolStore.setSchool"
            >
              <el-option
                v-for="item in schoolStore.schoolList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="register-btn"
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
</template>

<script setup>
import { ref, reactive , onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Lock, Message, Avatar, ArrowDown } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { useSchoolStore } from '@/stores/school'
import LangSwitcher from '@/components/LangSwitcher.vue'

const schoolStore = useSchoolStore()
const { t, locale } = useI18n()
const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  uid: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
  schoolId: ''
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
  password: [
    { required: true, message: t('common.pleaseInputPassword'), trigger: 'blur' },
    { min: 6, max: 20, message: t('register.passwordLength'), trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  schoolId: [
    { required: true, message: t('nav.selectSchool'), trigger: 'change' }
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
onMounted(() => {
  if (!schoolStore.schoolList.length) {
    schoolStore.getSchoolList()
  }
})
</script>

<style scoped>
/* 语言切换样式 */
.lang-switch {
  position: fixed;
  top: 20px;
  right: 24px;
  z-index: 999;
}

.register-page {
  min-height: 100vh;
  display: flex;
  background: #FFFFFF;
  overflow: hidden;
  position: relative;
}

.brand-section {
  flex: 0 0 60%;
  background: linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%);
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
  background: radial-gradient(1200px 600px at 70% 20%, rgba(46, 125, 50, 0.22) 0%, rgba(255, 255, 255, 0) 60%),
    radial-gradient(900px 500px at 20% 70%, rgba(46, 125, 50, 0.16) 0%, rgba(255, 255, 255, 0) 55%);
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
  color: #388E3C;
  margin: 20px 0 10px 0;
  letter-spacing: 2px;
}

.brand-slogan {
  font-size: 20px;
  color: #1B5E20;
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
  background: linear-gradient(135deg, #4CAF50 0%, #388E3C 100%);
  opacity: 0.15;
  top: -100px;
  right: -100px;
}

.circle-2 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #FF9800 0%, #F57C00 100%);
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
  background: linear-gradient(135deg, #4CAF50 0%, #388E3C 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 8px 0;
}

.form-subtitle {
  color: #757575;
  font-size: 14px;
  margin: 0;
}

.register-form {
  margin-bottom: 24px;
}

.register-btn {
  width: 100%;
  height: 48px;
  margin-top: 8px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #4CAF50 0%, #388E3C 100%);
  border: none;
}

.register-btn:hover {
  background: linear-gradient(135deg, #388E3C 0%, #2E7D32 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
}

.form-footer {
  text-align: center;
  color: #757575;
  font-size: 14px;
  padding: 16px 0;
}

.form-footer .el-link {
  margin-left: 8px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .register-page {
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

  .register-input .el-input__wrapper {
    border-radius: 8px;
    height: 40px;
  }

  .lang-wrapper {
    display: flex;
    justify-content: center;
    margin-bottom: 12px;
  }
}
</style>
