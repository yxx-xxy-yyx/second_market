<template>
  <div class="cart-container">
    <template v-if="isMobileScreen">
      <div class="min-h-screen bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 pb-32">
        <div class="sticky top-0 z-50 bg-white/90 backdrop-blur-xl border-b border-gray-100 shadow-sm px-4 py-4 flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="h-10 w-10 rounded-2xl bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center hover:from-gray-200 hover:to-gray-300 transition-all cursor-pointer active:scale-95" @click="router.back()">
              <el-icon :size="20" class="text-gray-700"><ArrowLeft /></el-icon>
            </div>
            <div class="text-xl font-black text-gray-900">{{ $t('cart.title') }}</div>
          </div>
          <button v-if="cart.items.length" class="px-4 py-2 rounded-2xl bg-gradient-to-r from-primary to-indigo-600 text-white text-sm font-bold shadow-lg hover:shadow-xl transition-all active:scale-95" @click="toggleEdit">
            {{ editing ? $t('cart.done') : $t('cart.manage') }}
          </button>
        </div>

        <div v-if="cart.items.length" class="px-4 py-5 space-y-4">
          <div
            v-for="i in cart.items"
            :key="i.productId"
            class="bg-white/90 backdrop-blur-xl rounded-3xl border border-gray-100 shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300 p-4 flex gap-4 group"
          >
            <div class="pt-3">
              <div class="h-6 w-6">
                <van-checkbox :model-value="i.selected" @update:model-value="() => cart.toggleSelect(i.productId)" class="!rounded-xl" />
              </div>
            </div>
            <div class="w-24 h-24 rounded-2xl bg-gradient-to-br from-gray-100 to-gray-200 overflow-hidden flex-shrink-0 cursor-pointer shadow-md hover:shadow-lg transition-all" @click="router.push(`/user/product/${i.productId}`)">
              <img :src="i.image" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
            </div>
            <div class="flex-1 min-w-0 pt-1">
              <div class="text-base font-black text-gray-900 line-clamp-2 leading-snug">{{ i.title }}</div>
              <div class="mt-3 flex items-center justify-between">
                <div class="text-2xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">¥{{ i.price }}</div>
                <button v-if="editing" class="px-4 py-2 bg-gradient-to-r from-red-500 to-rose-600 text-white text-xs font-bold rounded-2xl shadow-md hover:shadow-lg transition-all active:scale-95" @click="cart.removeItem(i.productId)">
                  {{ $t('cart.remove') }}
                </button>
              </div>
              <div v-if="i.conditionScore" class="mt-2 text-xs text-gray-500 font-medium">{{ $t('product.condition') }} {{ i.conditionScore }}</div>
            </div>
          </div>
        </div>

        <div v-else class="pt-16 px-4">
          <el-empty :description="$t('cart.empty')" :image-size="130">
            <div class="mt-8">
              <el-button type="primary" round size="large" class="!px-10 !py-3 !text-base !font-bold !rounded-2xl !shadow-lg hover:!shadow-xl transition-all" @click="router.push('/user/products')">
                {{ $t('cart.browse') }}
              </el-button>
            </div>
          </el-empty>
        </div>

        <div v-if="cart.items.length" class="fixed left-0 right-0 bottom-0 bg-white/95 backdrop-blur-xl border-t border-gray-100 px-6 py-5 flex items-center gap-4 z-50 shadow-2xl">
          <div class="flex items-center gap-3">
            <van-checkbox :model-value="allSelected" @update:model-value="cart.selectAll" class="!rounded-xl">{{ $t('cart.selectAll') }}</van-checkbox>
          </div>
          <div class="flex-1 text-right">
            <div class="text-xs text-gray-500 font-medium mb-1">{{ $t('cart.total') }}</div>
            <div class="text-2xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">¥{{ cart.selectedTotal.toFixed(2) }}</div>
          </div>
          <el-button type="primary" class="!rounded-2xl !h-14 !px-8 !text-base !font-bold !shadow-lg hover:!shadow-xl transition-all" :disabled="cart.selectedCount === 0" @click="goCheckout">
            {{ $t('cart.checkout') }}({{ cart.selectedCount }})
          </el-button>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 min-h-screen pt-10 pb-24">
        <div class="px-8 max-w-6xl mx-auto">
          <div class="flex items-center justify-between mb-10">
            <div class="flex items-center gap-4">
              <div class="h-12 w-12 bg-gradient-to-br from-primary to-indigo-600 rounded-2xl flex items-center justify-center shadow-lg">
                <el-icon :size="24" class="text-white"><ShoppingCart /></el-icon>
              </div>
              <h1 class="text-4xl font-black text-gray-900">{{ $t('cart.title') }}</h1>
            </div>
            <div class="px-6 py-3 bg-white/90 backdrop-blur-xl rounded-2xl shadow-lg border border-gray-100 text-lg font-bold text-gray-700">
              {{ $t('cart.selected', { count: cart.selectedCount }) }}
            </div>
          </div>

          <div v-if="cart.items.length" class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-2xl border border-gray-100 overflow-hidden">
            <div class="p-6 space-y-4">
              <div v-for="i in cart.items" :key="i.productId" class="flex items-center gap-6 p-5 bg-gradient-to-r from-gray-50 to-white rounded-2xl border border-gray-100 hover:shadow-xl hover:-translate-y-1 transition-all duration-300 group">
                <van-checkbox :model-value="i.selected" @update:model-value="() => cart.toggleSelect(i.productId)" class="!rounded-xl" />
                <el-image :src="i.image" fit="cover" class="w-28 h-28 rounded-2xl cursor-pointer overflow-hidden shadow-md hover:shadow-lg transition-all group-hover:scale-105" @click="router.push(`/user/product/${i.productId}`)" />
                <div class="flex-1 min-w-0">
                  <div class="text-xl font-black text-gray-900 truncate">{{ i.title }}</div>
                  <div class="mt-3 text-3xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">¥{{ i.price }}</div>
                  <div v-if="i.conditionScore" class="mt-2 text-sm text-gray-500 font-medium">{{ $t('product.condition') }} {{ i.conditionScore }}</div>
                </div>
                <el-button type="danger" size="large" class="!rounded-2xl !px-6 !py-2 !font-bold !shadow-md hover:!shadow-lg transition-all" @click="cart.removeItem(i.productId)">{{ $t('cart.remove') }}</el-button>
              </div>
            </div>

            <div class="mt-6 flex items-center justify-between border-t border-gray-100 pt-8 px-6 bg-gradient-to-r from-gray-50 to-slate-50">
              <div class="flex items-center gap-4">
                <van-checkbox :model-value="allSelected" @update:model-value="cart.selectAll" class="!text-lg !font-bold">{{ $t('cart.selectAll') }}</van-checkbox>
              </div>
              <div class="flex items-center gap-8">
                <div class="text-right">
                  <div class="text-sm text-gray-500 font-medium mb-2">{{ $t('cart.total') }}</div>
                  <div class="text-4xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">¥{{ cart.selectedTotal.toFixed(2) }}</div>
                </div>
                <el-button type="primary" size="large" class="!h-16 !px-10 !text-xl !font-bold !rounded-2xl !shadow-xl hover:!shadow-2xl transition-all" :disabled="cart.selectedCount === 0" @click="goCheckout">
                  {{ $t('cart.goCheckout') }}
                </el-button>
              </div>
            </div>
          </div>

          <div v-else class="pt-16">
            <el-empty :description="$t('cart.empty')" :image-size="160" />
            <div class="flex justify-center mt-10">
              <el-button type="primary" size="large" class="!px-12 !py-4 !text-xl !font-bold !rounded-3xl !shadow-2xl hover:!shadow-3xl transition-all" @click="router.push('/user/products')">
                {{ $t('cart.browse') }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { useCartStore } from '@/stores/cart'
import { ArrowLeft, ShoppingCart } from '@element-plus/icons-vue'

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

