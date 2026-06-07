<template>
  <div class="h-[100dvh] bg-gray-50">
    <div ref="scrollerRef" class="h-full w-full flex overflow-x-auto snap-x snap-mandatory scroll-smooth swipe-scroller"
      style="scrollbar-width: none;" @scroll="handleScrollerScroll">
      <!-- 第一个页面 -->
      <section class="w-full flex-none h-full snap-start relative swipe-section">
        <div class="absolute inset-0 hero-bg z-0" aria-hidden="true"></div>
        <div class="absolute inset-0 hero-iso z-0" aria-hidden="true"></div>

        <div class="absolute top-0 left-0 right-0 px-6 z-20"
          :style="{ paddingTop: 'calc(env(safe-area-inset-top) + 16px)' }">
          <div class="flex items-center justify-end">
            <LangSwitcher />
          </div>
        </div>

        <div class="h-full flex items-center justify-center px-10 relative z-10">
          <div class="w-full max-w-[1080px] grid grid-cols-12 gap-8 items-center">
            <div class="col-span-7">
              <div class="text-white">
                <div class="text-xl font-extrabold tracking-wide">{{ $t('common.projectName') }}</div>
                <div class="mt-1 text-xs text-white/80 tracking-widest">CAMPUS SECOND-HAND MARKETPLACE</div>
              </div>
              <div class="mt-6">
                <div class="text-white/85 text-lg font-semibold">{{ $t('login.appSlogan') }}</div>
                <div class="mt-2 text-white/70 text-sm leading-relaxed max-w-[520px]">
                  {{ $t('login.subtitle') }}
                </div>
              </div>
            </div>

            <div class="col-span-5">
              <div
                class="rounded-3xl bg-[rgba(15,23,42,0.24)] border border-[rgba(255,255,255,0.20)] shadow-sm p-6 backdrop-blur">
                <div class="text-white text-xl font-extrabold">{{ $t('common.login') }}</div>
                <div class="mt-5 space-y-3">
                  <div
                    class="rounded-2xl bg-[rgba(255,255,255,0.12)] border border-[rgba(255,255,255,0.16)] px-4 py-4 text-white/70">
                    {{ $t('common.pleaseInputUid') }}
                  </div>
                  <div
                    class="rounded-2xl bg-[rgba(255,255,255,0.12)] border border-[rgba(255,255,255,0.16)] px-4 py-4 text-white/70">
                    {{ $t('common.pleaseInputPassword') }}
                  </div>
                </div>
                <div class="mt-4 flex items-center justify-between">
                  <div class="flex items-center gap-2 text-white/70 text-sm">
                    <span class="h-4 w-4 rounded border border-white/40 inline-block"></span>
                    <span>{{ $t('common.rememberMe') }}</span>
                  </div>
                  <div class="text-white/70 text-sm">{{ $t('common.forgotPassword') }}</div>
                </div>
                <button class="mt-6 w-full py-3.5 rounded-2xl text-white font-semibold active:scale-[0.99] transition"
                  style="background: linear-gradient(135deg, rgba(6,182,212,0.95) 0%, rgba(139,92,246,0.92) 100%);"
                  @click="goNext">
                  {{ $t('common.goLogin') }}
                </button>

                <div class="mt-6 flex items-center justify-center gap-2">
                  <span class="h-1.5 rounded-full transition-all"
                    :class="activeIndex === 0 ? 'w-6 bg-white' : 'w-2 bg-white/60'"></span>
                  <span class="h-1.5 rounded-full transition-all"
                    :class="activeIndex === 1 ? 'w-6 bg-white' : 'w-2 bg-white/60'"></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 第二个页面（添加了底部背景） -->
      <section class="w-full flex-none h-full snap-start bg-white swipe-section relative">
        <!-- 新增：第一个页面的背景元素 -->
        <div class="absolute inset-0 hero-bg z-0 opacity-20" aria-hidden="true"></div>
        <div class="absolute inset-0 hero-iso z-0 opacity-20" aria-hidden="true"></div>

        <div class="absolute -left-16 bottom-10 w-56 h-56 rounded-full bg-[rgba(6,182,212,0.16)] blur-2xl"
          aria-hidden="true"></div>
        <div class="absolute right-8 top-28 w-44 h-44 rounded-full bg-[rgba(255,166,0,0.16)] blur-2xl"
          aria-hidden="true"></div>
        <div class="h-full flex flex-col relative z-10"> <!-- 新增 z-10 确保内容在背景上层 -->
          <div class="px-8" :style="{ paddingTop: 'calc(env(safe-area-inset-top) + 18px)', paddingBottom: '14px' }">
            <div class="flex items-start justify-between gap-3">
              <div>
                <h1 class="text-2xl font-bold text-gray-900">{{ $t('login.title') }}</h1>
                <p class="text-sm text-gray-500 mt-1">{{ $t('login.subtitle') }}</p>
              </div>
              <button class="text-sm text-gray-500" @click="goPrev">{{ $t('common.cancel') }}</button>
            </div>
          </div>

          <div class="flex-1 overflow-y-auto px-8 py-6">
            <div class="mx-auto max-w-[1080px] rounded-3xl bg-white border border-gray-100 shadow-sm overflow-hidden">
              <div class="grid grid-cols-12 min-h-[560px]">
                <div class="col-span-5 p-8">
                  <div class="space-y-4">
                    <div class="relative">
                      <input v-model="form.uid" :placeholder="$t('common.pleaseInputUid')"
                        class="w-full p-4 pl-11 rounded-2xl border border-gray-200 focus:ring-2 outline-none bg-white"
                        :style="{ '--tw-ring-color': 'rgba(6,182,212,0.45)' }" inputmode="text" autocomplete="username"
                        @blur="validateUid" @keyup.enter="handleLogin" />
                      <span class="absolute left-4 top-4 text-gray-400">👤</span>
                      <div v-if="uidError" class="text-red-500 text-xs mt-1 pl-11">{{ uidError }}</div>
                    </div>

                    <div class="relative">
                      <input v-model="form.password" type="password" :placeholder="$t('common.pleaseInputPassword')"
                        class="w-full p-4 pl-11 rounded-2xl border border-gray-200 focus:ring-2 outline-none bg-white"
                        :style="{ '--tw-ring-color': 'rgba(6,182,212,0.45)' }" autocomplete="current-password"
                        @blur="validatePassword" @keyup.enter="handleLogin" />
                      <span class="absolute left-4 top-4 text-gray-400">🔒</span>
                      <div v-if="passwordError" class="text-red-500 text-xs mt-1 pl-11">{{ passwordError }}</div>
                    </div>

                    <button :disabled="loading || !canSubmit"
                      class="w-full py-4 rounded-2xl text-white font-semibold transition active:scale-[0.99]"
                      :class="canSubmit ? 'sm-bg-primary' : 'bg-gray-300'" @click="handleLogin">
                      <span v-if="!loading">{{ $t('common.login') }}</span>
                      <span v-else>{{ $t('login.loggingIn') }}</span>
                    </button>

                    <div v-if="error" class="text-red-500 text-sm text-center">
                      {{ error }}
                    </div>

                    <div class="text-center text-sm text-gray-500 mt-2">
                      {{ $t('login.noAccount') }}
                      <span class="text-orange-500" @click="router.push('/register')">{{ $t('common.register') }}</span>
                    </div>
                  </div>
                </div>

                <div class="col-span-7 relative">
                  <div class="absolute inset-0 form-iso" aria-hidden="true"></div>
                  <div class="absolute inset-0 bg-gradient-to-br from-[rgba(2,132,199,0.88)] to-[rgba(79,70,229,0.78)]"
                    aria-hidden="true"></div>
                  <div class="relative h-full p-10 text-white">
                    <div class="text-xl font-extrabold tracking-wide">{{ $t('common.projectName') }}</div>
                    <div class="mt-2 text-white/85 text-sm max-w-[520px] leading-relaxed">
                      {{ $t('login.appSlogan') }}
                    </div>
                    <div class="mt-10 grid grid-cols-2 gap-4 max-w-[620px]">
                      <div class="rounded-2xl bg-white/10 border border-white/15 p-5">
                        <div class="text-sm font-semibold">{{ $t('dashboard.aiTitle') }}</div>
                        <div class="mt-2 text-xs text-white/80 leading-relaxed">{{ $t('dashboard.aiDesc') }}</div>
                      </div>
                      <div class="rounded-2xl bg-white/10 border border-white/15 p-5">
                        <div class="text-sm font-semibold">{{ $t('common.school') }}</div>
                        <div class="mt-2 text-xs text-white/80 leading-relaxed">{{ $t('nav.selectSchool') }}</div>
                      </div>
                    </div>
                    <div class="absolute bottom-8 left-10 text-xs text-white/60">Copyright © {{ new Date().getFullYear()
                      }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="h-6" :style="{ paddingBottom: 'env(safe-area-inset-bottom)' }"></div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useI18n } from 'vue-i18n'
import { useSchoolStore } from '@/stores/school'
import LangSwitcher from '@/components/LangSwitcher.vue'

const { t } = useI18n()
const router = useRouter()
const userStore = useUserStore()
const schoolStore = useSchoolStore()

const selectedSchool = computed({
  get: () => schoolStore.selectedSchool,
  set: (value) => schoolStore.setSchool(value)
})

onMounted(() => {
  if (!schoolStore.schoolList.length) {
    schoolStore.getSchoolList()
  }
})

const scrollerRef = ref()
const activeIndex = ref(0)

const handleScrollerScroll = () => {
  const el = scrollerRef.value
  if (!el) return
  const width = el.clientWidth || 1
  const idx = Math.round(el.scrollLeft / width)
  activeIndex.value = Math.max(0, Math.min(1, idx))
}

const form = reactive({
  uid: '',
  password: ''
})

const loading = ref(false)
const error = ref('')
const uidError = ref('')
const passwordError = ref('')

const canSubmit = computed(() => {
  return !!form.uid && !!form.password && !uidError.value && !passwordError.value
})

const validateUid = () => {
  if (!form.uid) {
    uidError.value = t('common.pleaseInputUid')
  } else if (form.uid.length < 3 || form.uid.length > 20) {
    uidError.value = t('register.uidLength')
  } else {
    uidError.value = ''
  }
}

const validatePassword = () => {
  if (!form.password) {
    passwordError.value = t('common.pleaseInputPassword')
  } else if (form.password.length < 6 || form.password.length > 20) {
    passwordError.value = t('register.passwordLength')
  } else {
    passwordError.value = ''
  }
}

const goNext = () => {
  const el = scrollerRef.value
  if (!el) return
  el.scrollTo({ left: el.clientWidth, behavior: 'smooth' })
  activeIndex.value = 1
}

const goPrev = () => {
  const el = scrollerRef.value
  if (!el) return
  el.scrollTo({ left: 0, behavior: 'smooth' })
  activeIndex.value = 0
}

const handleLogin = async () => {
  validateUid()
  validatePassword()
  if (!canSubmit.value) return

  loading.value = true
  error.value = ''
  try {
    const res = await userStore.login({
      uid: form.uid,
      password: form.password
    })

    if (!res.success) {
      error.value = res.message || t('user.login.fail')
      return
    }

    await userStore.getUserInfo()
    router.replace('/user/dashboard')
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

.form-iso {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='1200' height='900' viewBox='0 0 1200 900'%3E%3Cdefs%3E%3ClinearGradient id='b' x1='0' y1='0' x2='1' y2='1'%3E%3Cstop offset='0' stop-color='%230EA5E9' stop-opacity='0.22'/%3E%3Cstop offset='1' stop-color='%234F46E5' stop-opacity='0.20'/%3E%3C/linearGradient%3E%3C/defs%3E%3Crect width='1200' height='900' fill='none'/%3E%3Cg fill='url(%23b)'%3E%3Cpath d='M220 610l180-90 180 90-180 90z'/%3E%3Cpath d='M640 520l220-110 220 110-220 110z'/%3E%3Cpath d='M460 410l140-70 140 70-140 70z'/%3E%3C/g%3E%3Cg fill='rgba(255,255,255,0.12)'%3E%3Ccircle cx='980' cy='200' r='4'/%3E%3Ccircle cx='1040' cy='250' r='3'/%3E%3Ccircle cx='920' cy='290' r='5'/%3E%3C/g%3E%3C/svg%3E");
  background-size: cover;
  background-position: center;
  opacity: 1;
}
</style>