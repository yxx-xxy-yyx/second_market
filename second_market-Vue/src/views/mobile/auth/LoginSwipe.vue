<template>
  <div class="h-[100dvh] bg-black">
    <div ref="scrollerRef" class="h-full w-full flex overflow-x-auto snap-x snap-mandatory scroll-smooth swipe-scroller"
      style="scrollbar-width: none;" @scroll="handleScrollerScroll">

      <!-- 第一页 -->
      <section class="w-full flex-none h-full snap-start relative swipe-section">
        <div class="absolute inset-0 hero-bg z-0" aria-hidden="true"></div>
        <div class="absolute inset-0 hero-iso z-0" aria-hidden="true"></div>
        <div class="absolute inset-0 bg-gradient-to-b from-black/20 via-black/10 to-transparent hero-overlay"></div>

        <div v-if="activeIndex === 0" class="brand-mark absolute left-0 right-0 z-10 pointer-events-none">
          <div class="text-center text-white">
            <div class="text-4xl font-extrabold tracking-wide">{{ $t('common.projectName') }}</div>
            <div class="mt-2 text-sm text-white/80 tracking-widest">CAMPUS SECOND-HAND MARKETPLACE</div>
          </div>
        </div>

        <div class="absolute left-0 right-0 px-5 z-10 thumb-zone">
          <div class="mt-6 pl-[28px]">
            <div class="text-white/85 text-lg font-semibold">{{ $t('login.appSlogan') }}</div>
            <div class="mt-2 text-white/70 text-sm leading-relaxed max-w-[520px]">
              {{ $t('login.subtitle') }}
            </div>
          </div>
          <button
            class="mt-6 w-full rounded-[28px] bg-white text-gray-900 py-4 font-semibold active:scale-[0.99] transition"
            @click="goNext">
            {{ $t('common.goLogin') }}
          </button>
          <div class="mt-4 flex items-center justify-center gap-2">
            <span class="h-1.5 rounded-full transition-all"
              :class="activeIndex === 0 ? 'w-6 bg-white' : 'w-2 bg-white/50'"></span>
            <span class="h-1.5 rounded-full transition-all"
              :class="activeIndex === 1 ? 'w-6 bg-white' : 'w-2 bg-white/50'"></span>
          </div>
        </div>
      </section>

      <!-- 第二页：登录页（你要的全部效果） -->
      <section class="w-full flex-none h-full snap-start relative swipe-section">

        <!-- 背景：全屏铺满 不是半截！！！ -->
        <div class="absolute inset-0 hero-bg z-0" aria-hidden="true"></div>
        <div class="absolute inset-0 hero-iso z-0" aria-hidden="true"></div>

        <div class="h-full flex flex-col items-center justify-center relative z-10 px-6 py-16">

          <!-- 1. 欢迎回来 + 副标题 -->
          <div class="text-center mb-6">
            <h1 class="text-4xl font-bold text-white">{{ $t('login.title') }}</h1>
            <p class="mt-2 text-white/80 text-sm">{{ $t('login.subtitle') }}</p>
          </div>

          <!-- 2. 毛玻璃登录面板 -->
          <div class="login-glass w-full max-w-[360px]">
            <div class="px-4 text-white text-3xl font-extrabold mb-6">{{ $t('common.login') }}</div>
            <div class="space-y-6">
              <div class="relative">
                <input v-model="form.uid" :placeholder="uidError || $t('common.pleaseInputUid')"
                  class="glass-input w-full px-6 py-5 rounded-[28px] border outline-none"
                  :class="{ 'error-placeholder': uidError && !form.uid }" @blur="validateUid"
                  @keyup.enter="handleLogin" />
              </div>

              <div class="relative">
                <input v-model="form.password" :type="showPassword ? 'text' : 'password'"
                  :placeholder="passwordError || $t('common.pleaseInputPassword')"
                  class="glass-input w-full px-6 py-5 pr-14 rounded-[28px] border outline-none"
                  :class="{ 'error-placeholder': passwordError && !form.password }" @blur="validatePassword"
                  @keyup.enter="handleLogin" />
                <button v-if="form.password" type="button" class="password-eye" @click="showPassword = !showPassword">
                  <el-icon>
                    <Hide v-if="showPassword" />
                    <View v-else />
                  </el-icon>
                </button>
              </div>

              <div class="flex items-center justify-between px-4 text-white/80 text-base">
                <label class="flex items-center gap-3">
                  <input v-model="rememberAccount" type="checkbox" class="sr-only" />
                  <span class="remember-box" :class="{ checked: rememberAccount }"></span>
                  <span>记住账号</span>
                </label>
                <button type="button" class="text-white/80 active:text-white">
                  忘记密码?
                </button>
              </div>

              <button :disabled="loading || !canSubmit"
                class="login-action w-full py-5 rounded-[28px] text-white text-xl font-bold transition active:scale-[0.99]"
                :class="{ disabled: !canSubmit }" @click="handleLogin">
                <span v-if="!loading">{{ $t('common.login') }}</span>
                <span v-else>{{ $t('login.loggingIn') }}</span>
              </button>

              <div v-if="error" class="text-red-100 text-sm text-center">
                {{ error }}
              </div>

              <div class="text-center text-base text-white/75 pt-1">
                {{ $t('login.noAccount') }}
                <span class="text-white font-semibold" @click="router.push('/register')">{{ $t('common.register')
                  }}</span>
              </div>
            </div>
          </div>

          <!-- 返回按钮 -->
          <button class="cancel-glass absolute top-8 right-6 text-white" @click="goPrev">
            {{ $t('common.cancel') }}
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useI18n } from 'vue-i18n'
import { View, Hide } from '@element-plus/icons-vue'

const { t } = useI18n()
const router = useRouter()
const userStore = useUserStore()

const scrollerRef = ref()
const activeIndex = ref(0)

const handleScrollerScroll = () => {
  const el = scrollerRef.value
  if (!el) return
  const w = el.clientWidth || 1
  activeIndex.value = Math.round(el.scrollLeft / w)
}

const form = reactive({ uid: '', password: '' })
const loading = ref(false)
const error = ref('')
const uidError = ref('')
const passwordError = ref('')
const rememberAccount = ref(false)
const showPassword = ref(false)

const canSubmit = computed(() =>
  !!form.uid && !!form.password && !uidError.value && !passwordError.value
)

const validateUid = () => {
  if (!form.uid) uidError.value = '! ' + t('common.pleaseInputUid')
  else if (form.uid.length < 3 || form.uid.length > 20) uidError.value = '! ' + t('register.uidLength')
  else uidError.value = ''
}

const validatePassword = () => {
  if (!form.password) passwordError.value = '! ' + t('common.pleaseInputPassword')
  else if (form.password.length < 6 || form.password.length > 20) passwordError.value = '! ' + t('register.passwordLength')
  else passwordError.value = ''
}

const goNext = () => {
  scrollerRef.value?.scrollTo({ left: scrollerRef.value.clientWidth, behavior: 'smooth' })
  activeIndex.value = 1
}

const goPrev = () => {
  scrollerRef.value?.scrollTo({ left: 0, behavior: 'smooth' })
  activeIndex.value = 0
}

const handleLogin = async () => {
  validateUid()
  validatePassword()
  if (!canSubmit.value) return
  loading.value = true
  error.value = ''
  try {
    const res = await userStore.login({ uid: form.uid, password: form.password })
    if (res.success) {
      userStore.user?.role === 'admin'
        ? router.replace('/admin/dashboard')
        : router.replace('/user/dashboard')
    } else {
      error.value = res.message || t('user.login.fail')
    }
  } catch (e) {
    error.value = e?.message || t('user.login.error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
div::-webkit-scrollbar {
  display: none;
}

.swipe-scroller {
  -webkit-overflow-scrolling: touch;
  overscroll-behavior-x: contain;
  touch-action: pan-x;
}

.swipe-section {
  scroll-snap-stop: always;
}

.thumb-zone {
  bottom: calc(env(safe-area-inset-bottom) + 18dvh);
}

.brand-mark {
  top: 0;
  bottom: calc(env(safe-area-inset-bottom) + 18dvh + 78px);
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-height: 700px) {
  .thumb-zone {
    bottom: calc(env(safe-area-inset-bottom) + 14dvh);
  }

  .brand-mark {
    bottom: calc(env(safe-area-inset-bottom) + 14dvh + 78px);
  }
}

@media (min-height: 820px) {
  .thumb-zone {
    bottom: calc(env(safe-area-inset-bottom) + 20dvh);
  }

  .brand-mark {
    bottom: calc(env(safe-area-inset-bottom) + 20dvh + 78px);
  }
}

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

.login-glass {
  padding: 28px 24px 26px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 28px;
  background: rgba(77, 86, 151, 0.34);
  box-shadow: 0 18px 40px rgba(59, 60, 118, 0.28), inset 0 1px 0 rgba(255, 255, 255, 0.24);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
}

.glass-input {
  min-height: 66px;
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
  top: 33px;
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

.remember-box {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.52);
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.08);
  transition: background 0.2s ease, border-color 0.2s ease;
}

.remember-box.checked {
  border-color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.9);
}

.login-action {
  background: linear-gradient(135deg, #19bfd3 0%, #36a8e8 50%, #7c5df1 100%);
  box-shadow: 0 14px 28px rgba(56, 110, 230, 0.28);
}

.login-action.disabled {
  background: rgba(255, 255, 255, 0.24);
  box-shadow: none;
}

.cancel-glass {
  padding: 10px 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 18px;
  background: rgba(77, 86, 151, 0.28);
  font-size: 18px;
  line-height: 1;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 10px 24px rgba(59, 60, 118, 0.18);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}
</style>
