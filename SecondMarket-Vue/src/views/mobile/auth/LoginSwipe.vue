<template>
  <div class="h-[100dvh] bg-black">
    <div
      ref="scrollerRef"
      class="h-full w-full flex overflow-x-auto snap-x snap-mandatory scroll-smooth swipe-scroller"
      style="scrollbar-width: none;"
      @scroll="handleScrollerScroll"
    >
      <section class="w-full flex-none h-full snap-start relative swipe-section">
        <img
          class="absolute inset-0 h-full w-full object-cover hero-cover"
          :src="coverImage"
          alt="cover"
          draggable="false"
        />
        <div class="absolute inset-0 bg-gradient-to-b from-black/35 via-black/20 to-black/70 hero-overlay"></div>

        <div class="absolute top-0 left-0 right-0 px-4" :style="{ paddingTop: 'calc(env(safe-area-inset-top) + 12px)' }">
          <div class="flex items-center justify-end">
            <LangSwitcher />
          </div>
        </div>

        <div class="absolute left-0 right-0 bottom-0 px-5 pb-7 z-10" :style="{ paddingBottom: 'calc(env(safe-area-inset-bottom) + 40px)' }">
          <div class="text-white text-2xl font-semibold tracking-tight">
            {{ $t('login.appName') }}
          </div>
          <div class="text-white/80 text-sm mt-2">
            {{ $t('login.appSlogan') }}
          </div>

          <button
            class="mt-6 w-full rounded-2xl bg-white text-gray-900 py-4 font-semibold active:scale-[0.99] transition"
            @click="goNext"
          >
            {{ $t('common.goLogin') }}
          </button>

          <div class="mt-4 flex items-center justify-center gap-2">
            <span class="h-1.5 rounded-full transition-all" :class="activeIndex === 0 ? 'w-6 bg-white' : 'w-2 bg-white/50'"></span>
            <span class="h-1.5 rounded-full transition-all" :class="activeIndex === 1 ? 'w-6 bg-white' : 'w-2 bg-white/50'"></span>
          </div>
        </div>
      </section>

      <section class="w-full flex-none h-full snap-start bg-gray-50 swipe-section">
        <div class="h-full flex flex-col">
          <div class="px-5 bg-white" :style="{ paddingTop: 'calc(env(safe-area-inset-top) + 18px)', paddingBottom: '16px' }">
            <div class="flex items-start justify-between gap-3">
              <div>
                <h1 class="text-2xl font-bold text-gray-900">{{ $t('login.title') }}</h1>
                <p class="text-sm text-gray-500 mt-1">{{ $t('login.subtitle') }}</p>
              </div>
              <button class="text-sm text-gray-500" @click="goPrev">{{ $t('common.cancel') }}</button>
            </div>
          </div>

          <div class="flex-1 overflow-y-auto px-5 py-6 space-y-4">
            <div class="relative">
              <input
                v-model="form.uid"
                :placeholder="$t('common.pleaseInputUid')"
                class="w-full p-4 pl-10 rounded-2xl border border-gray-200 focus:ring-2 outline-none bg-white"
                :style="{ '--tw-ring-color': 'rgba(6,182,212,0.45)' }"
                inputmode="text"
                autocomplete="username"
                @blur="validateUid"
                @keyup.enter="handleLogin"
              />
              <span class="absolute left-3 top-4 text-gray-400">👤</span>
              <div v-if="uidError" class="text-red-500 text-xs mt-1 pl-10">{{ uidError }}</div>
            </div>

            <div class="relative">
              <input
                v-model="form.password"
                type="password"
                :placeholder="$t('common.pleaseInputPassword')"
                class="w-full p-4 pl-10 rounded-2xl border border-gray-200 focus:ring-2 outline-none bg-white"
                :style="{ '--tw-ring-color': 'rgba(6,182,212,0.45)' }"
                autocomplete="current-password"
                @blur="validatePassword"
                @keyup.enter="handleLogin"
              />
              <span class="absolute left-3 top-4 text-gray-400">🔒</span>
              <div v-if="passwordError" class="text-red-500 text-xs mt-1 pl-10">{{ passwordError }}</div>
            </div>

            <div class="space-y-2">
              <div class="text-xs font-semibold text-gray-600">{{ $t('common.school') }}</div>
              <el-select
                v-model="selectedSchool"
                filterable
                :placeholder="$t('nav.selectSchool')"
                class="w-full"
              >
                <el-option
                  v-for="item in schoolStore.schoolList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </div>

            <button
              :disabled="loading || !canSubmit"
              class="w-full py-4 rounded-2xl text-white font-semibold transition active:scale-[0.99]"
              :class="canSubmit ? 'sm-bg-primary' : 'bg-gray-300'"
              @click="handleLogin"
            >
              <span v-if="!loading">{{ $t('common.login') }}</span>
              <span v-else>{{ $t('login.loggingIn') }}</span>
            </button>

            <div v-if="error" class="text-red-500 text-sm text-center">
              {{ error }}
            </div>

            <div class="text-center text-sm text-gray-500 mt-4">
              {{ $t('login.noAccount') }}
              <span class="text-orange-500" @click="router.push('/register')">{{ $t('common.register') }}</span>
            </div>
          </div>

          <div class="h-6" :style="{ paddingBottom: 'env(safe-area-inset-bottom)' }"></div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'
import { useSchoolStore } from '@/stores/school'

const { t } = useI18n()
const router = useRouter()
const userStore = useUserStore()
const schoolStore = useSchoolStore()

const coverImage =
  "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='1400' height='2000'><defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'><stop offset='0' stop-color='%23111'/><stop offset='1' stop-color='%2306b6d4'/></linearGradient></defs><rect width='1400' height='2000' fill='url(%23g)'/><circle cx='1100' cy='420' r='360' fill='%23ffffff' opacity='0.08'/><circle cx='260' cy='1640' r='520' fill='%23ffffff' opacity='0.06'/><text x='90' y='1120' fill='white' font-family='Arial' font-size='56' font-weight='700' opacity='0.95'>SecondMarket</text><text x='90' y='1200' fill='white' font-family='Arial' font-size='26' opacity='0.85'>Campus second-hand marketplace</text></svg>"

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
    const result = await userStore.login({
      uid: form.uid,
      password: form.password
    })
    if (result.success) {
      if (userStore.user?.role === 'admin') {
        await router.replace('/admin/dashboard')
      } else {
        await router.replace('/user/dashboard')
      }
    } else {
      error.value = result.message || t('user.login.fail')
    }
  } catch (e) {
    error.value = e?.message || t('user.login.fail')
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

.hero-cover,
.hero-overlay {
  pointer-events: none;
  user-select: none;
}
</style>
