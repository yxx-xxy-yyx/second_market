<template>
  <div class="h-[100dvh] bg-black relative overflow-hidden">
    <div class="absolute inset-0 hero-bg z-0" aria-hidden="true"></div>
    <div class="absolute inset-0 hero-iso z-0" aria-hidden="true"></div>

    <div class="h-full flex flex-col items-center justify-center relative z-10 px-6 overflow-y-auto py-16">
      <div class="text-center mb-6">
        <h1 class="text-4xl font-bold text-white">{{ $t('register.title') }}</h1>
        <p class="mt-2 text-white/80 text-sm">{{ $t('register.subtitle') }}</p>
      </div>

      <div class="register-glass w-full max-w-[360px]">
        <div class="px-4 text-white text-3xl font-extrabold mb-4">{{ $t('common.register') }}</div>
        <div class="space-y-4">
          <div class="relative">
            <input v-model="form.uid" :placeholder="uidError || $t('common.pleaseInputUid')"
              class="glass-input w-full px-6 py-4 rounded-[24px] border outline-none"
              :class="{ 'error-placeholder': uidError && !form.uid }" @blur="validateUid" @keyup.enter="register" />
          </div>

          <div class="relative">
            <input v-model="form.nickname" :placeholder="nicknameError || $t('common.pleaseInputNickname')"
              class="glass-input w-full px-6 py-4 rounded-[24px] border outline-none"
              :class="{ 'error-placeholder': nicknameError && !form.nickname }" @blur="validateNickname"
              @keyup.enter="register" />
          </div>

          <div class="relative">
            <input v-model="form.email" :placeholder="emailError || $t('common.pleaseInputEmail')"
              class="glass-input w-full px-6 py-4 rounded-[24px] border outline-none"
              :class="{ 'error-placeholder': emailError && !form.email }" @blur="validateEmail"
              @keyup.enter="register" />
          </div>

          <div class="relative">
            <input v-model="form.password" :type="showPassword ? 'text' : 'password'"
              :placeholder="passwordError || $t('common.pleaseInputPassword')"
              class="glass-input w-full px-6 py-4 pr-14 rounded-[24px] border outline-none"
              :class="{ 'error-placeholder': passwordError && !form.password }" @blur="validatePassword"
              @keyup.enter="register" />
            <button v-if="form.password" type="button" class="password-eye" @click="showPassword = !showPassword">
              <el-icon>
                <Hide v-if="showPassword" />
                <View v-else />
              </el-icon>
            </button>
          </div>

          <div class="relative">
            <input v-model="form.confirmPassword" :type="showConfirmPassword ? 'text' : 'password'"
              :placeholder="confirmPasswordError || $t('register.confirmPasswordPlaceholder')"
              class="glass-input w-full px-6 py-4 pr-14 rounded-[24px] border outline-none"
              :class="{ 'error-placeholder': confirmPasswordError && !form.confirmPassword }"
              @blur="validateConfirmPassword" @keyup.enter="register" />
            <button v-if="form.confirmPassword" type="button" class="password-eye"
              @click="showConfirmPassword = !showConfirmPassword">
              <el-icon>
                <Hide v-if="showConfirmPassword" />
                <View v-else />
              </el-icon>
            </button>
          </div>

          <button :disabled="loading || !canSubmit" @click="register"
            class="register-action w-full py-4 rounded-[24px] text-white text-xl font-bold transition active:scale-[0.99]"
            :class="{ disabled: !canSubmit }">
            <span v-if="!loading">{{ $t('common.register') }}</span>
            <span v-else>{{ $t('register.registering') }}</span>
          </button>

          <div v-if="error" class="text-red-100 text-sm text-center">
            {{ error }}
          </div>

          <div class="text-center text-base text-white/75 pt-1">
            {{ $t('register.hasAccount') }}
            <span class="text-white font-semibold" @click="goLogin">{{ $t('common.login') }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@/stores/user'
import { View, Hide } from '@element-plus/icons-vue'

export default {
  components: { View, Hide },
  data() {
    return {
      form: {
        uid: '',
        nickname: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      loading: false,
      error: '',
      uidError: '',
      nicknameError: '',
      emailError: '',
      passwordError: '',
      confirmPasswordError: '',
      showPassword: false,
      showConfirmPassword: false
    }
  },
  computed: {
    canSubmit() {
      // 所有字段非空且无校验错误
      return (
        this.form.uid && this.form.nickname && this.form.email &&
        this.form.password && this.form.confirmPassword &&
        !this.uidError && !this.nicknameError && !this.emailError &&
        !this.passwordError && !this.confirmPasswordError
      )
    }
  },
  methods: {
    // 用户名校验
    validateUid() {
      if (!this.form.uid) {
        this.uidError = '! ' + this.$t('common.pleaseInputUid')
      } else if (this.form.uid.length < 3 || this.form.uid.length > 20) {
        this.uidError = '! ' + this.$t('register.uidLength')
      } else {
        this.uidError = ''
      }
    },
    // 昵称校验
    validateNickname() {
      this.nicknameError = !this.form.nickname ? '! ' + this.$t('common.pleaseInputNickname') : ''
    },
    // 邮箱校验
    validateEmail() {
      if (!this.form.email) {
        this.emailError = '! ' + this.$t('common.pleaseInputEmail')
      } else {
        const reg = /^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
        this.emailError = reg.test(this.form.email) ? '' : '! ' + this.$t('register.emailValid')
      }
    },
    // 密码校验
    validatePassword() {
      if (!this.form.password) {
        this.passwordError = '! ' + this.$t('common.pleaseInputPassword')
      } else if (this.form.password.length < 6 || this.form.password.length > 20) {
        this.passwordError = '! ' + this.$t('register.passwordLength')
      } else {
        this.passwordError = ''
        // 密码变化时重新校验确认密码
        this.validateConfirmPassword()
      }
    },
    // 确认密码校验
    validateConfirmPassword() {
      if (!this.form.confirmPassword) {
        this.confirmPasswordError = '! ' + this.$t('register.confirmPasswordRequired')
      } else if (this.form.confirmPassword !== this.form.password) {
        this.confirmPasswordError = '! ' + this.$t('register.passwordNotMatch')
      } else {
        this.confirmPasswordError = ''
      }
    },
    async register() {
      // 前置全量校验
      this.validateUid()
      this.validateNickname()
      this.validateEmail()
      this.validatePassword()
      this.validateConfirmPassword()
      if (!this.canSubmit) return

      this.loading = true
      this.error = ''

      try {
        // 对齐Web端：使用userStore的注册方法
        const userStore = useUserStore()
        const result = await userStore.register({
          uid: this.form.uid,
          nickname: this.form.nickname,
          email: this.form.email,
          password: this.form.password
        })

        if (result.success) {
          this.$router.push('/login')
        } else {
          this.error = result.message || this.$t('user.register.fail')
        }
      } catch (e) {
        this.error = e?.message || this.$t('user.register.fail')
      } finally {
        this.loading = false
      }
    },
    goLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.hero-bg {
  background:
    radial-gradient(1200px 600px at 20% 10%, rgba(255, 255, 255, 0.55), rgba(255, 255, 255, 0) 60%),
    radial-gradient(900px 520px at 85% 70%, rgba(255, 166, 0, 0.18), rgba(255, 255, 255, 0) 60%),
    linear-gradient(135deg, rgba(186, 230, 253, 1) 0%, rgba(147, 197, 253, 1) 45%, rgba(167, 139, 250, 1) 100%);
}

.hero-iso {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='1400' height='900' viewBox='0 0 1400 900'%3E%3Cdefs%3E%3ClinearGradient id='g' x1='0' y1='0' x2='1' y2='1'%3E%3Cstop offset='0' stop-color='%2306B6D4' stop-opacity='0.20'/%3E%3Cstop offset='1' stop-color='%238B5CF6' stop-opacity='0.18'/%3E%3C/linearGradient%3E%3C/defs%3E%3Crect width='1400' height='900' fill='none'/%3E%3Cg fill='url(%23g)'%3E%3Cpath d='M190 520l180-90 180 90-180 90z'/%3E%3Cpath d='M540 390l140-70 140 70-140 70z'/%3E%3Cpath d='M360 610l120-60 120 60-120 60z'/%3E%3Cpath d='M760 560l220-110 220 110-220 110z'/%3E%3C/g%3E%3Cg fill='rgba(255,255,255,0.12)'%3E%3Ccircle cx='1060' cy='210' r='5'/%3E%3Ccircle cx='1120' cy='260' r='3'/%3E%3Ccircle cx='980' cy='300' r='4'/%3E%3C/g%3E%3Cg stroke='rgba(255,255,255,0.14)' stroke-width='2' fill='none'%3E%3Cpath d='M120 780c120-90 260-110 420-60 110 35 210 20 320-30 120-55 240-65 380-25'/%3E%3C/g%3E%3C/svg%3E");
  background-size: cover;
  background-position: center;
  opacity: 1;
}

.register-glass {
  padding: 24px 24px 22px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 28px;
  background: rgba(77, 86, 151, 0.34);
  box-shadow: 0 18px 40px rgba(59, 60, 118, 0.28), inset 0 1px 0 rgba(255, 255, 255, 0.24);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
}

.glass-input {
  min-height: 56px;
  border-color: rgba(255, 255, 255, 0.24);
  background: rgba(255, 255, 255, 0.12);
  color: white;
  font-size: 18px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.16);
}

.glass-input::placeholder {
  color: white;
  transition: color 0.3s ease;
}

.glass-input.error-placeholder::placeholder {
  color: #ff4d4f;
}

.glass-input:focus {
  border-color: rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.18);
}

.password-eye {
  position: absolute;
  top: 28px;
  right: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  color: rgba(255, 255, 255, 0.72);
  font-size: 20px;
  transform: translateY(-50%);
}

.password-eye:active {
  color: white;
}

.register-action {
  background: linear-gradient(135deg, #19bfd3 0%, #36a8e8 50%, #7c5df1 100%);
  box-shadow: 0 14px 28px rgba(56, 110, 230, 0.28);
}

.register-action.disabled {
  background: rgba(255, 255, 255, 0.24);
  box-shadow: none;
}
</style>
