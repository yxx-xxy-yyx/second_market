<template>
  <div class="grid grid-cols-1 lg:grid-cols-[1fr_360px] gap-5">
    <section class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
      <div class="px-5 py-5 bg-gray-50 flex items-start justify-between gap-4">
        <div>
          <div class="text-[16px] md:text-[18px] font-semibold text-gray-900">{{ $t('neo.checkout.title') }}</div>
          <div class="mt-1 text-[12px] text-gray-500">{{ $t('neo.checkout.subtitle') }}</div>
        </div>
        <el-button text class="!text-gray-500" @click="router.push('/neo/cart')">
          {{ $t('neo.checkout.backCart') }}
        </el-button>
      </div>

      <div class="px-5 py-5 space-y-4">
        <div class="rounded-2xl border border-gray-100 bg-white overflow-hidden">
          <div class="px-4 py-4 flex items-center justify-between bg-gray-50">
            <div class="text-[13px] font-semibold text-gray-900">{{ $t('neo.checkout.address') }}</div>
            <el-button text class="!text-[color:var(--primary-color)]" @click="router.push({ path: '/neo/addresses', query: { pick: 1 } })">
              {{ $t('neo.checkout.choose') }}
            </el-button>
          </div>
          <div class="px-4 py-4">
            <div v-if="pickedAddress" class="space-y-1">
              <div class="flex items-center gap-2 flex-wrap">
                <div class="text-[14px] font-semibold text-gray-900">{{ pickedAddress.name }}</div>
                <div class="text-[12px] text-gray-500">{{ pickedAddress.phone }}</div>
                <el-tag v-if="pickedAddress.isDefault" size="small" type="success">{{ $t('neo.address.default') }}</el-tag>
              </div>
              <div class="text-[13px] text-gray-700 leading-relaxed">{{ formatAddress(pickedAddress) }}</div>
            </div>
            <div v-else class="rounded-2xl border border-dashed border-gray-200 bg-white px-4 py-5 text-center">
              <div class="text-[13px] font-semibold text-gray-900">{{ $t('neo.checkout.noAddress') }}</div>
              <div class="mt-1 text-[12px] text-gray-500">{{ $t('neo.checkout.noAddressHint') }}</div>
              <el-button class="mt-4" type="primary" @click="router.push('/neo/addresses')">
                {{ $t('neo.checkout.addAddress') }}
              </el-button>
            </div>
          </div>
        </div>

        <div class="rounded-2xl border border-gray-100 overflow-hidden">
          <div class="px-4 py-4 bg-gray-50 text-[13px] font-semibold text-gray-900">{{ $t('neo.checkout.items') }}</div>
          <div class="px-4 py-4 space-y-3">
            <div
              v-for="it in checkout.draft.items"
              :key="it.productId"
              class="flex items-start gap-4 rounded-2xl border border-gray-100 bg-white p-4"
            >
              <button class="h-[70px] w-[70px] rounded-2xl overflow-hidden bg-gray-50 shrink-0" @click="goDetail(it.productId)">
                <img :src="getProductImage(it.snapshot?.images)" class="h-full w-full object-cover" @error="handleImgError" />
              </button>
              <div class="flex-1 min-w-0">
                <div class="text-[14px] font-semibold text-gray-900 line-clamp-2">
                  {{ locale === 'ko' && it.snapshot?.titleKo ? it.snapshot?.titleKo : it.snapshot?.title }}
                </div>
                <div class="mt-1 text-[12px] text-gray-500">
                  {{ currency }}{{ it.price }} × {{ it.qty }}
                </div>
              </div>
              <div class="text-right text-[14px] font-bold text-[color:var(--primary-color)]">
                {{ currency }}{{ (Number(it.price) * Number(it.qty)).toFixed(2) }}
              </div>
            </div>
          </div>
        </div>

        <div class="rounded-2xl border border-gray-100 overflow-hidden">
          <div class="px-4 py-4 bg-gray-50 text-[13px] font-semibold text-gray-900">{{ $t('neo.checkout.remark') }}</div>
          <div class="px-4 py-4">
            <el-input
              v-model="remark"
              type="textarea"
              :rows="3"
              :placeholder="$t('neo.checkout.remarkPlaceholder')"
            />
          </div>
        </div>
      </div>
    </section>

    <aside class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden h-fit">
      <div class="px-5 py-5 bg-gray-50">
        <div class="text-[13px] font-semibold text-gray-900">{{ $t('neo.checkout.summary') }}</div>
      </div>
      <div class="px-5 py-5 space-y-3">
        <div class="flex items-center justify-between text-[13px] text-gray-600">
          <span>{{ $t('neo.checkout.itemCount') }}</span>
          <span class="font-semibold text-gray-900">{{ checkout.draft.items.length }}</span>
        </div>
        <div class="flex items-center justify-between text-[13px] text-gray-600">
          <span>{{ $t('neo.checkout.amount') }}</span>
          <span class="font-extrabold text-[16px] text-[color:var(--primary-color)]">{{ currency }}{{ checkout.amount.toFixed(2) }}</span>
        </div>
        <el-button type="primary" class="w-full" :disabled="!canSubmit" :loading="submitting" @click="submit">
          {{ $t('neo.checkout.submit') }}
        </el-button>
        <div class="text-[12px] text-gray-400 leading-relaxed">
          {{ $t('neo.checkout.hint') }}
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { orderApi } from '@/api/order'
import { useNeoCartStore } from '@/neo/stores/neoCart'
import { useNeoAddressStore } from '@/neo/stores/neoAddress'
import { useNeoCheckoutStore } from '@/neo/stores/neoCheckout'
import { DEFAULT_PRODUCT_IMAGE } from '@/utils/url'

const router = useRouter()
const { t, locale } = useI18n()

const cart = useNeoCartStore()
const addr = useNeoAddressStore()
const checkout = useNeoCheckoutStore()

const currency = t('common.currency')

const remark = ref(checkout.draft.remark || '')
watch(remark, (v) => checkout.setRemark(v))

const pickedAddress = computed(() => {
  if (checkout.draft.addressId) return addr.list.find((x) => x.id === checkout.draft.addressId) || null
  return addr.defaultAddress
})

watch(
  () => pickedAddress.value?.id,
  (id) => {
    if (id && !checkout.draft.addressId) checkout.setAddressId(id)
  },
  { immediate: true }
)

const canSubmit = computed(() => checkout.draft.items.length > 0 && !!pickedAddress.value)
const submitting = ref(false)

const submit = async () => {
  if (!canSubmit.value) return
  submitting.value = true
  try {
    const ids = []
    for (const it of checkout.draft.items) {
      const res = await orderApi.createOrder({ productId: it.productId, remark: checkout.draft.remark || '' })
      if (res.code !== '200') {
        throw new Error(res.message || t('neo.checkout.createFail'))
      }
      ids.push(res.data?.id)
    }
    checkout.setOrderIds(ids)
    cart.clearSelected()
    router.push({ path: '/neo/pay-center', query: { orderIds: ids.join(',') } })
  } catch (e) {
    ElMessage.error(e?.message || t('neo.checkout.createFail'))
  } finally {
    submitting.value = false
  }
}

const goDetail = (id) => {
  router.push(`/user/product/${id}`)
}

const formatAddress = (it) => {
  if (!it) return ''
  if (it.country === 'CN') {
    return `${it.province || ''}${it.city || ''}${it.district || ''}${it.detail || ''} ${it.zipcode || ''}`.trim()
  }
  return `${it.detail || ''} ${it.detail2 || ''} ${it.zipcode || ''}`.trim()
}

const DEFAULT_IMAGE = DEFAULT_PRODUCT_IMAGE
const getProductImage = (images) => {
  if (!images) return DEFAULT_IMAGE
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return arr?.[0] || DEFAULT_IMAGE
  } catch {
    return DEFAULT_IMAGE
  }
}

const handleImgError = (e) => {
  if (e?.target) e.target.src = DEFAULT_IMAGE
}
</script>

