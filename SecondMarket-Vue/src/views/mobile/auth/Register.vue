<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <!-- Header -->
    <div class="px-5 pt-12 pb-4 bg-white">
      <div class="flex items-start justify-between gap-3">
        <div>
          <h1 class="text-2xl font-bold">{{ $t('register.title') }}</h1>
          <p class="text-sm text-gray-500 mt-1">{{ $t('register.subtitle') }}</p>
        </div>
        <LangSwitcher />
      </div>
    </div>

    <!-- Form -->
    <div class="flex-1 px-5 py-6 space-y-4">
      <div class="relative">
        <input 
          v-model="form.uid" 
          :placeholder="$t('common.pleaseInputUid')"
          class="w-full p-4 pl-10 rounded-2xl border border-gray-200 focus:ring-2 focus:ring-orange-400 outline-none"
          @blur="validateUid"
        />
        <span class="absolute left-3 top-4 text-gray-400">👤</span>
        <div v-if="uidError" class="text-red-400 text-xs mt-1 pl-10">{{ uidError }}</div>
      </div>

      <div class="relative">
        <input 
          v-model="form.nickname" 
          :placeholder="$t('common.pleaseInputNickname')"
          class="w-full p-4 pl-10 rounded-2xl border border-gray-200 focus:ring-2 focus:ring-orange-400 outline-none"
          @blur="validateNickname"
        />
        <span class="absolute left-3 top-4 text-gray-400">🪪</span>
        <div v-if="nicknameError" class="text-red-400 text-xs mt-1 pl-10">{{ nicknameError }}</div>
      </div>

      <div class="relative">
        <input 
          v-model="form.email" 
          :placeholder="$t('common.pleaseInputEmail')"
          class="w-full p-4 pl-10 rounded-2xl border border-gray-200 focus:ring-2 focus:ring-orange-400 outline-none"
          @blur="validateEmail"
        />
        <span class="absolute left-3 top-4 text-gray-400">📧</span>
        <div v-if="emailError" class="text-red-400 text-xs mt-1 pl-10">{{ emailError }}</div>
      </div>

      <div class="relative">
        <input 
          v-model="form.password" 
          type="password" 
          :placeholder="$t('common.pleaseInputPassword')"
          class="w-full p-4 pl-10 rounded-2xl border border-gray-200 focus:ring-2 focus:ring-orange-400 outline-none"
          @blur="validatePassword"
        />
        <span class="absolute left-3 top-4 text-gray-400">🔒</span>
        <div v-if="passwordError" class="text-red-400 text-xs mt-1 pl-10">{{ passwordError }}</div>
      </div>

      <div class="relative">
        <input 
          v-model="form.confirmPassword" 
          type="password" 
          :placeholder="$t('register.confirmPasswordPlaceholder')"
          class="w-full p-4 pl-10 rounded-2xl border border-gray-200 focus:ring-2 focus:ring-orange-400 outline-none"
          @blur="validateConfirmPassword"
        />
        <span class="absolute left-3 top-4 text-gray-400">🔒</span>
        <div v-if="confirmPasswordError" class="text-red-400 text-xs mt-1 pl-10">{{ confirmPasswordError }}</div>
      </div>

      <div class="relative">
        <select 
          v-model="form.schoolId"
          class="w-full p-4 pl-10 rounded-2xl border border-gray-200 focus:ring-2 focus:ring-orange-400 outline-none appearance-none bg-white"
          @change="validateSchool"
        >
          <option value="">{{ $t('nav.selectSchool') }}</option>
          <option v-for="item in schoolList" :key="item.value" :value="item.value">
            {{ item.label }}
          </option>
        </select>
        <span class="absolute left-3 top-4 text-gray-400">🏫</span>
        <div v-if="schoolError" class="text-red-400 text-xs mt-1 pl-10">{{ schoolError }}</div>
      </div>

      <button
        :disabled="loading || !canSubmit"
        @click="register"
        class="w-full py-4 rounded-2xl text-white font-semibold transition"
        :class="canSubmit ? 'bg-orange-500 active:scale-95' : 'bg-gray-300'"
      >
        <span v-if="!loading">{{ $t('common.register') }}</span>
        <span v-else>{{ $t('register.registering') }}</span>
      </button>

      <div v-if="error" class="text-red-500 text-sm text-center">
        {{ error }}
      </div>

      <div class="text-center text-sm text-gray-500 mt-4">
        {{ $t('register.hasAccount') }}
        <span class="text-orange-500" @click="goLogin">{{ $t('common.login') }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@/stores/user'
import { useSchoolStore } from '@/stores/school'
import LangSwitcher from '@/components/LangSwitcher.vue'

export default {
  components: { LangSwitcher },
  data() {
    return {
      form: {
        uid: '',
        nickname: '',
        email: '',
        password: '',
        confirmPassword: '',
        schoolId: ''
      },
      loading: false,
      error: '',
      uidError: '',
      nicknameError: '',
      emailError: '',
      passwordError: '',
      confirmPasswordError: '',
      schoolError: '',
      schoolList: []
    }
  },
  computed: {
    canSubmit() {
      // 所有字段非空且无校验错误
      return (
        this.form.uid && this.form.nickname && this.form.email &&
        this.form.password && this.form.confirmPassword && this.form.schoolId &&
        !this.uidError && !this.nicknameError && !this.emailError &&
        !this.passwordError && !this.confirmPasswordError && !this.schoolError
      )
    }
  },
  async mounted() {
    // 加载学校列表（对齐Web端）
    const schoolStore = useSchoolStore()
    if (!schoolStore.schoolList.length) {
      await schoolStore.getSchoolList()
    }
    this.schoolList = schoolStore.schoolList
  },
  methods: {
    // 用户名校验
    validateUid() {
      if (!this.form.uid) {
        this.uidError = this.$t('common.pleaseInputUid')
      } else if (this.form.uid.length < 3 || this.form.uid.length > 20) {
        this.uidError = this.$t('register.uidLength')
      } else {
        this.uidError = ''
      }
    },
    // 昵称校验
    validateNickname() {
      this.nicknameError = !this.form.nickname ? this.$t('common.pleaseInputNickname') : ''
    },
    // 邮箱校验
    validateEmail() {
      if (!this.form.email) {
        this.emailError = this.$t('common.pleaseInputEmail')
      } else {
        const reg = /^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
        this.emailError = reg.test(this.form.email) ? '' : this.$t('register.emailValid')
      }
    },
    // 密码校验
    validatePassword() {
      if (!this.form.password) {
        this.passwordError = this.$t('common.pleaseInputPassword')
      } else if (this.form.password.length < 6 || this.form.password.length > 20) {
        this.passwordError = this.$t('register.passwordLength')
      } else {
        this.passwordError = ''
        // 密码变化时重新校验确认密码
        this.validateConfirmPassword()
      }
    },
    // 确认密码校验
    validateConfirmPassword() {
      if (!this.form.confirmPassword) {
        this.confirmPasswordError = this.$t('register.confirmPasswordRequired')
      } else if (this.form.confirmPassword !== this.form.password) {
        this.confirmPasswordError = this.$t('register.passwordNotMatch')
      } else {
        this.confirmPasswordError = ''
      }
    },
    // 学校校验
    validateSchool() {
      this.schoolError = !this.form.schoolId ? this.$t('nav.selectSchool') : ''
    },
    async register() {
      // 前置全量校验
      this.validateUid()
      this.validateNickname()
      this.validateEmail()
      this.validatePassword()
      this.validateConfirmPassword()
      this.validateSchool()
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
          password: this.form.password,
          schoolId: this.form.schoolId
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
