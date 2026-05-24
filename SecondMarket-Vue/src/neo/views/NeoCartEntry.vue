<template>
  <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
    <div class="px-5 py-5 bg-gray-50 flex items-center justify-between">
      <div class="text-[16px] md:text-[18px] font-semibold text-gray-900">{{ $t('neo.cart.title') }}</div>
      <div class="flex items-center gap-2">
        <el-button text class="!text-gray-500" :disabled="cart.items.length === 0" @click="handleClearSelected">
          {{ $t('neo.cart.clearSelected') }}
        </el-button>
        <el-button text class="!text-rose-600" :disabled="cart.items.length === 0" @click="handleClearAll">
          {{ $t('neo.cart.clearAll') }}
        </el-button>
      </div>
    </div>

    <div v-if="cart.items.length === 0" class="px-6 py-10">
      <el-empty :description="$t('neo.cart.empty')" :image-size="140">
        <el-button type="primary" @click="router.push('/user/products')">
          {{ $t('neo.cart.goShopping') }}
        </el-button>
      </el-empty>
    </div>

    <div v-else class="px-5 py-5">
      <div class="flex items-center justify-between gap-3">
        <div class="flex items-center gap-3">
          <el-checkbox v-model="allSelected" @change="toggleAll">
            {{ $t('neo.cart.selectAll') }}
          </el-checkbox>
          <div class="text-[12px] text-gray-500">
            {{ $t('neo.cart.selectedCount', { n: cart.selectedCount }) }}
          </div>
        </div>
        <el-button text class="!text-gray-500" @click="router.push('/user/products')">
          {{ $t('neo.cart.continue') }}
        </el-button>
      </div>

      <div class="mt-4 space-y-3">
        <div
          v-for="it in cart.items"
          :key="it.productId"
          class="rounded-2xl border border-gray-100 hover:border-[rgba(var(--primary-rgb),0.35)] transition bg-white"
        >
          <div class="p-4 flex gap-4 items-start">
            <el-checkbox :model-value="it.selected" @change="(v) => cart.setSelected(it.productId, v)" class="mt-1" />
            <button class="h-[76px] w-[76px] rounded-2xl overflow-hidden bg-gray-50 shrink-0" @click="goDetail(it.productId)">
              <img :src="getProductImage(it.snapshot?.images)" class="h-full w-full object-cover" @error="handleImgError" />
            </button>
            <div class="flex-1 min-w-0">
              <div class="flex items-start justify-between gap-3">
                <button class="text-left min-w-0" @click="goDetail(it.productId)">
                  <div class="text-[14px] font-semibold text-gray-900 line-clamp-2">
                    {{ locale === 'ko' && it.snapshot?.titleKo ? it.snapshot?.titleKo : it.snapshot?.title }}
                  </div>
                  <div class="mt-1 text-[12px] text-gray-500 truncate">
                    {{ it.snapshot?.userNickname || '-' }}
                  </div>
                </button>
                <div class="text-right shrink-0">
                  <div class="text-[14px] font-bold text-[color:var(--primary-color)]">
                    {{ currency }}{{ it.price }}
                  </div>
                  <el-button text class="!text-rose-600 !px-0" @click="cart.remove(it.productId)">
                    {{ $t('neo.cart.remove') }}
                  </el-button>
                </div>
              </div>

              <div class="mt-3 flex items-center justify-between gap-3">
                <div class="text-[12px] text-gray-500">
                  {{ $t('neo.cart.subtotal') }}:
                  <span class="font-semibold text-gray-900">{{ currency }}{{ (Number(it.price) * Number(it.qty)).toFixed(2) }}</span>
                </div>
                <el-input-number
                  :model-value="it.qty"
                  :min="1"
                  :max="99"
                  controls-position="right"
                  size="small"
                  @update:model-value="(v) => cart.setQty(it.productId, v)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="cart.items.length" class="border-t border-gray-100 bg-white">
      <div class="px-5 py-4 flex items-center justify-between gap-4">
        <div class="min-w-0">
          <div class="text-[12px] text-gray-500">{{ $t('neo.cart.total') }}</div>
          <div class="text-[18px] font-extrabold text-[color:var(--primary-color)] truncate">
            {{ currency }}{{ cart.totalPrice.toFixed(2) }}
          </div>
        </div>
        <el-button type="primary" class="min-w-[140px]" :disabled="cart.selectedItems.length === 0" @click="goCheckout">
          {{ $t('neo.cart.checkout') }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessageBox } from 'element-plus'
import { useNeoCartStore } from '@/neo/stores/neoCart'
import { useNeoCheckoutStore } from '@/neo/stores/neoCheckout'

const router = useRouter()
const { t, locale } = useI18n()

const cart = useNeoCartStore()
const checkout = useNeoCheckoutStore()

const currency = t('common.currency')

const allSelected = computed({
  get: () => cart.allSelected,
  set: (v) => cart.setAllSelected(v)
})

const toggleAll = (v) => {
  cart.setAllSelected(v)
}

const goCheckout = () => {
  checkout.setItems(cart.selectedItems.map((x) => ({ ...x })))
  checkout.setOrderIds([])
  checkout.setPaymentMethod('')
  router.push('/neo/checkout')
}

const goDetail = (id) => {
  router.push(`/user/product/${id}`)
}

const handleClearSelected = async () => {
  if (!cart.selectedItems.length) return
  await ElMessageBox.confirm(t('neo.cart.confirmClearSelected'), t('common.tip'), {
    type: 'warning',
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel')
  })
  cart.clearSelected()
}

const handleClearAll = async () => {
  if (!cart.items.length) return
  await ElMessageBox.confirm(t('neo.cart.confirmClearAll'), t('common.tip'), {
    type: 'warning',
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel')
  })
  cart.clear()
}

const DEFAULT_IMAGE = 'https://via.placeholder.com/300x300?text=No+Image'
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

