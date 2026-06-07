<template>
  <div class="cart-container">
    <template v-if="isMobileScreen">
      <div class="min-h-screen bg-gray-50 pb-24">
        <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 px-4 py-3 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <el-icon :size="20" class="text-gray-700" @click="router.back()"><ArrowLeft /></el-icon>
            <div class="text-base font-bold text-gray-900">{{ $t('cart.title') }}</div>
          </div>
          <button v-if="cart.items.length" class="text-sm text-primary" @click="toggleEdit">
            {{ editing ? $t('cart.done') : $t('cart.manage') }}
          </button>
        </div>

        <div v-if="cart.items.length" class="px-3 py-4 space-y-3">
          <div
            v-for="i in cart.items"
            :key="i.productId"
            class="bg-white rounded-2xl border border-gray-100 shadow-sm p-3 flex gap-3"
          >
            <div class="pt-1">
              <van-checkbox :model-value="i.selected" @update:model-value="() => cart.toggleSelect(i.productId)" />
            </div>
            <div class="w-20 h-20 rounded-xl bg-gray-50 overflow-hidden flex-shrink-0 cursor-pointer" @click="router.push(`/user/product/${i.productId}`)">
              <img :src="i.image" class="w-full h-full object-cover" />
            </div>
            <div class="flex-1 min-w-0">
              <div class="text-sm font-semibold text-gray-900 line-clamp-2">{{ i.title }}</div>
              <div class="mt-2 flex items-center justify-between">
                <div class="text-base font-black text-primary">¥{{ i.price }}</div>
                <button v-if="editing" class="text-xs text-red-500" @click="cart.removeItem(i.productId)">{{ $t('cart.remove') }}</button>
              </div>
              <div v-if="i.conditionScore" class="mt-1 text-[11px] text-gray-400">{{ $t('product.condition') }} {{ i.conditionScore }}</div>
            </div>
          </div>
        </div>

        <el-empty v-else :description="$t('cart.empty')" :image-size="110">
          <el-button type="primary" round @click="router.push('/user/products')">{{ $t('cart.browse') }}</el-button>
        </el-empty>

        <div v-if="cart.items.length" class="fixed left-0 right-0 bottom-0 bg-white border-t border-gray-100 px-4 py-3 flex items-center gap-3 z-50">
          <van-checkbox :model-value="allSelected" @update:model-value="cart.selectAll">{{ $t('cart.selectAll') }}</van-checkbox>
          <div class="flex-1 text-right">
            <div class="text-xs text-gray-500">{{ $t('cart.total') }}</div>
            <div class="text-lg font-black text-primary">¥{{ cart.selectedTotal.toFixed(2) }}</div>
          </div>
          <el-button type="primary" class="!rounded-full !h-10 !px-5" :disabled="cart.selectedCount === 0" @click="goCheckout">
            {{ $t('cart.checkout') }}({{ cart.selectedCount }})
          </el-button>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="p-6 max-w-5xl mx-auto pb-24">
        <div class="flex items-center justify-between mb-6">
          <h1 class="text-2xl font-bold">{{ $t('cart.title') }}</h1>
          <div class="text-sm text-gray-500">{{ $t('cart.selected', { count: cart.selectedCount }) }}</div>
        </div>

        <el-card v-if="cart.items.length" shadow="never">
          <div class="space-y-4">
            <div v-for="i in cart.items" :key="i.productId" class="flex items-center gap-4">
              <van-checkbox :model-value="i.selected" @update:model-value="() => cart.toggleSelect(i.productId)" />
              <el-image :src="i.image" fit="cover" class="w-20 h-20 rounded-lg cursor-pointer" @click="router.push(`/user/product/${i.productId}`)" />
              <div class="flex-1 min-w-0">
                <div class="font-semibold truncate">{{ i.title }}</div>
                <div class="mt-2 text-primary font-bold">¥{{ i.price }}</div>
              </div>
              <el-button type="danger" plain @click="cart.removeItem(i.productId)">{{ $t('cart.remove') }}</el-button>
            </div>
          </div>

          <div class="mt-6 flex items-center justify-between border-t border-gray-100 pt-4">
            <van-checkbox :model-value="allSelected" @update:model-value="cart.selectAll">{{ $t('cart.selectAll') }}</van-checkbox>
            <div class="flex items-center gap-4">
              <div class="text-right">
                <div class="text-xs text-gray-500">{{ $t('cart.total') }}</div>
                <div class="text-xl font-black text-primary">¥{{ cart.selectedTotal.toFixed(2) }}</div>
              </div>
              <el-button type="primary" :disabled="cart.selectedCount === 0" @click="goCheckout">{{ $t('cart.goCheckout') }}</el-button>
            </div>
          </div>
        </el-card>

        <el-empty v-else :description="$t('cart.empty')" />
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { useCartStore } from '@/stores/cart'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const { isMobileScreen } = useDeviceType()
const cart = useCartStore()

const editing = ref(false)
const toggleEdit = () => (editing.value = !editing.value)

const allSelected = computed(() => cart.items.length > 0 && cart.selectedCount === cart.items.length)

const goCheckout = () => {
  router.push('/user/checkout')
}
</script>

