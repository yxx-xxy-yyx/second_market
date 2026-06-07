<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <!-- Header -->
    <div class="px-5 pt-12 pb-4 bg-white">
      <div>
        <h1 class="text-2xl font-bold">{{ $t('login.title') }}</h1>
        <p class="text-sm text-gray-500 mt-1">{{ $t('login.subtitle') }}</p>
      </div>
    </div>

    <!-- Form -->
    <div class="flex-1 px-5 py-6 space-y-4">
      <div class="relative">
        <input v-model="form.uid" :placeholder="$t('common.pleaseInputUid')"
          class="w-full p-4 pl-10 rounded-2xl border border-gray-200 focus:ring-2 focus:ring-orange-400 outline-none"
          @blur="validateUid" />
        <span class="absolute left-3 top-4 text-gray-400">👤</span>
        <div v-if="uidError" class="text-red-400 text-xs mt-1 pl-10">{{ uidError }}</div>
      </div>

      <div class="relative">
        <input v-model="form.password" type="password" :placeholder="$t('common.pleaseInputPassword')"
          class="w-full p-4 pl-10 rounded-2xl border border-gray-200 focus:ring-2 focus:ring-orange-400 outline-none"
          @blur="validatePassword" />
        <span class="absolute left-3 top-4 text-gray-400">🔒</span>
        <div v-if="passwordError" class="text-red-400 text-xs mt-1 pl-10">{{ passwordError }}</div>
      </div>

      <button :disabled="loading || !canSubmit" @click="login"
        class="w-full py-4 rounded-2xl text-white font-semibold transition"
        :class="canSubmit ? 'bg-orange-500 active:scale-95' : 'bg-gray-300'">
        <span v-if="!loading">{{ $t('common.login') }}</span>
        <span v-else>{{ $t('login.loggingIn') }}</span>
      </button>

      <div v-if="error" class="text-red-500 text-sm text-center">
        {{ error }}
      </div>

      <div class="text-center text-sm text-gray-500 mt-4">
        {{ $t('login.noAccount') }}
        <span class="text-orange-500" @click="goRegister">{{ $t('common.register') }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@/stores/user'
export default {
  data() {
    return {
      form: {
        uid: '',
        password: ''
      },
      loading: false,
      error: '',
      uidError: '',
      passwordError: ''
    }
  },
  computed: {
    canSubmit() {
      // 仅当无表单错误且内容不为空时可提交
      return this.form.uid && this.form.password && !this.uidError && !this.passwordError
    }
  },
  methods: {
    // 用户名校验（对齐Web端规则）
    validateUid() {
      if (!this.form.uid) {
        this.uidError = this.$t('common.pleaseInputUid')
      } else if (this.form.uid.length < 3 || this.form.uid.length > 20) {
        this.uidError = this.$t('register.uidLength')
      } else {
        this.uidError = ''
      }
    },
    // 密码校验（对齐Web端规则）
    validatePassword() {
      if (!this.form.password) {
        this.passwordError = this.$t('common.pleaseInputPassword')
      } else if (this.form.password.length < 6 || this.form.password.length > 20) {
        this.passwordError = this.$t('register.passwordLength')
      } else {
        this.passwordError = ''
      }
    },
    async login() {
      // 前置校验
      this.validateUid()
      this.validatePassword()
      if (!this.canSubmit) return

      this.loading = true
      this.error = ''

      try {
        // 对齐Web端：使用userStore的登录方法（而非直接调$api）
        const userStore = useUserStore()
        const result = await userStore.login({
          uid: this.form.uid,
          password: this.form.password
        })

        if (result.success) {
          // 对齐Web端路由逻辑：区分管理员/普通用户
          if (userStore.user?.role === 'admin') {
            await this.$router.replace('/admin/dashboard')
          } else {
            await this.$router.replace('/user/dashboard')
          }
        } else {
          this.error = result.message || this.$t('user.login.fail')
        }
      } catch (e) {

        this.error = e.message || this.$t('user.login.fail')
      } finally {
        this.loading = false
      }
    },
    goRegister() {
      this.$router.push('/register')
    }
  }
}
</script>
