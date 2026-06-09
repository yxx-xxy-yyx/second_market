<template>
  <div class="checkout-container">
    <!-- 移动端视图 -->
    <div v-if="isMobileScreen" class="checkout-mobile min-h-screen bg-gray-50 pb-28">
      <!-- 顶部导航 -->
      <div class="sticky top-0 z-50 bg-white px-4 py-3 flex items-center justify-between border-b border-gray-100 shadow-sm">
        <div class="h-10 w-10 rounded-full bg-gray-100 flex items-center justify-center cursor-pointer hover:bg-gray-200 transition" @click="router.back()">
          <el-icon :size="20"><ArrowLeft /></el-icon>
        </div>
        <span class="text-base font-bold text-gray-900">确认订单</span>
        <div class="w-10"></div>
      </div>

      <!-- 商品信息 -->
      <div class="p-4 space-y-4">
        <!-- 商品卡片 -->
        <div class="bg-white rounded-2xl p-4 shadow-sm">
          <div class="flex gap-4">
            <img :src="productImage" class="w-24 h-24 rounded-xl object-cover" />
            <div class="flex-1">
              <h3 class="text-base font-bold text-gray-900 line-clamp-2">{{ product.title }}</h3>
              <p class="text-sm text-gray-500 mt-1">{{ product.category }}</p>
              <div class="flex items-center justify-between mt-2">
                <span class="text-xl font-black text-primary">¥{{ product.price }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 收货地址 -->
        <div class="bg-white rounded-2xl p-4 shadow-sm" @click="goToAddress">
          <div v-if="selectedAddress" class="flex items-start gap-3">
            <el-icon :size="24" class="text-primary mt-1"><Location /></el-icon>
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-1">
                <span class="font-bold text-gray-900">{{ selectedAddress.receiverName }}</span>
                <span class="text-sm text-gray-500">{{ selectedAddress.receiverPhone }}</span>
              </div>
              <p class="text-sm text-gray-600">{{ selectedAddress.province }} {{ selectedAddress.city }} {{ selectedAddress.district }} {{ selectedAddress.detailAddress }}</p>
            </div>
            <el-icon :size="20" class="text-gray-400"><ArrowRight /></el-icon>
          </div>
          <div v-else class="flex items-center justify-center py-4 text-gray-500">
            <el-icon class="mr-2"><Location /></el-icon>
            <span>请添加收货地址</span>
            <el-icon class="ml-2"><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 订单备注 -->
        <div class="bg-white rounded-2xl p-4 shadow-sm">
          <h4 class="font-bold text-gray-900 mb-3">订单备注</h4>
          <el-input v-model="remark" type="textarea" :rows="2" placeholder="选填，可备注特殊要求" />
        </div>

        <!-- 价格明细 -->
        <div class="bg-white rounded-2xl p-4 shadow-sm space-y-2">
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">商品价格</span>
            <span class="text-gray-900">¥{{ product.price }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">运费</span>
            <span class="text-gray-900">¥0.00</span>
          </div>
          <div class="flex justify-between pt-2 border-t border-gray-100">
            <span class="font-bold text-gray-900">合计</span>
            <span class="text-xl font-black text-primary">¥{{ product.price }}</span>
          </div>
        </div>
      </div>

      <!-- 底部提交栏 -->
      <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-100 px-4 py-4 flex items-center gap-4">
        <div class="flex-1">
          <span class="text-gray-500 text-sm">实付款</span>
          <span class="text-2xl font-black text-primary ml-1">¥{{ product.price }}</span>
        </div>
        <el-button type="primary" class="!h-12 !px-8 !rounded-full !font-bold" :loading="submitting" @click="handleSubmit">提交订单</el-button>
      </div>
    </div>

    <!-- 桌面端视图 -->
    <div v-else class="checkout-desktop bg-gradient-to-br from-gray-50 to-gray-100 min-h-screen pb-24">
      <div class="max-w-5xl mx-auto p-8 space-y-6">
        <h2 class="text-2xl font-bold text-gray-900">确认订单</h2>

        <!-- 收货地址 -->
        <div class="bg-white rounded-3xl shadow-xl p-6">
          <h3 class="text-lg font-bold text-gray-900 mb-4 flex items-center">
            <el-icon class="mr-2 text-primary"><Location /></el-icon>收货地址
          </h3>
          <div v-if="addresses.length === 0" class="text-center py-8">
            <p class="text-gray-500 mb-4">暂无收货地址</p>
            <el-button type="primary" @click="goToAddress">添加地址</el-button>
          </div>
          <div v-else class="grid grid-cols-2 gap-4">
            <div v-for="addr in addresses" :key="addr.id"
                 class="border-2 rounded-2xl p-4 cursor-pointer transition-all"
                 :class="selectedAddressId === addr.id ? 'border-primary bg-primary/5' : 'border-gray-100 hover:border-gray-200'"
                 @click="selectedAddressId = addr.id">
              <div class="flex items-center gap-2 mb-2">
                <span class="font-bold text-gray-900">{{ addr.receiverName }}</span>
                <span class="text-sm text-gray-500">{{ addr.receiverPhone }}</span>
              </div>
              <p class="text-sm text-gray-600">{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}</p>
            </div>
            <div class="border-2 border-dashed border-gray-200 rounded-2xl p-4 flex items-center justify-center cursor-pointer hover:border-primary transition-all" @click="goToAddress">
              <el-icon class="mr-2"><Plus /></el-icon>
              <span class="text-gray-500">添加新地址</span>
            </div>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="bg-white rounded-3xl shadow-xl p-6">
          <h3 class="text-lg font-bold text-gray-900 mb-4 flex items-center">
            <el-icon class="mr-2 text-primary"><Goods /></el-icon>商品信息
          </h3>
          <div class="flex gap-6 p-4 bg-gray-50 rounded-2xl">
            <img :src="productImage" class="w-32 h-32 rounded-xl object-cover" />
            <div class="flex-1">
              <h4 class="text-lg font-bold text-gray-900">{{ product.title }}</h4>
              <p class="text-sm text-gray-500 mt-2">{{ product.category }}</p>
              <div class="flex items-center justify-between mt-4">
                <span class="text-2xl font-black text-primary">¥{{ product.price }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 订单备注 -->
        <div class="bg-white rounded-3xl shadow-xl p-6">
          <h3 class="text-lg font-bold text-gray-900 mb-4 flex items-center">
            <el-icon class="mr-2 text-primary"><Document /></el-icon>订单备注
          </h3>
          <el-input v-model="remark" type="textarea" :rows="3" placeholder="选填，可备注特殊要求" />
        </div>

        <!-- 价格明细 -->
        <div class="bg-white rounded-3xl shadow-xl p-6">
          <div class="space-y-3">
            <div class="flex justify-between text-base">
              <span class="text-gray-500">商品价格</span>
              <span class="text-gray-900">¥{{ product.price }}</span>
            </div>
            <div class="flex justify-between text-base">
              <span class="text-gray-500">运费</span>
              <span class="text-gray-900">¥0.00</span>
            </div>
            <div class="flex justify-between pt-4 border-t border-gray-100">
              <span class="text-xl font-bold text-gray-900">合计</span>
              <span class="text-2xl font-black text-primary">¥{{ product.price }}</span>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="bg-white rounded-3xl shadow-xl p-6">
          <div class="flex items-center justify-between">
            <div>
              <span class="text-gray-500">实付款</span>
              <span class="text-3xl font-black text-primary ml-2">¥{{ product.price }}</span>
            </div>
            <el-button type="primary" size="large" class="!h-14 !px-12 !rounded-full !text-lg !font-bold" :loading="submitting" @click="handleSubmit">
              提交订单
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '@/api/product'
import { orderApi } from '@/api/order'
import { addressApi } from '@/api/address'
import { formatProductImageUrl } from '@/utils/url'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight, Location, Plus, Goods, Document } from '@element-plus/icons-vue'
import { useDeviceType } from '@/utils/device'

const route = useRoute()
const router = useRouter()
const { isMobileScreen } = useDeviceType()

const product = ref({})
const addresses = ref([])
const selectedAddressId = ref(null)
const remark = ref('')
const submitting = ref(false)

const productImage = computed(() => {
  if (!product.value?.images) return ''
  try {
    const arr = typeof product.value.images === 'string' ? JSON.parse(product.value.images) : product.value.images
    return formatProductImageUrl(Array.isArray(arr) ? arr[0] : arr)
  } catch {
    return formatProductImageUrl(product.value.images)
  }
})

const selectedAddress = computed(() => {
  return addresses.value.find(a => a.id === selectedAddressId.value)
})

const fetchProduct = async () => {
  try {
    const res = await productApi.getProductById(route.query.productId)
    if (res.code === '200') {
      product.value = res.data
    }
  } catch (e) {
    ElMessage.error('获取商品信息失败')
  }
}

const fetchAddresses = async () => {
  try {
    const res = await addressApi.getList()
    if (res.code === '200') {
      addresses.value = res.data || []
      // 优先选择默认地址
      const defaultAddr = addresses.value.find(a => a.isDefault)
      if (defaultAddr) {
        selectedAddressId.value = defaultAddr.id
      } else if (addresses.value.length > 0) {
        selectedAddressId.value = addresses.value[0].id
      }
    }
  } catch (e) {
    ElMessage.error('获取地址列表失败')
  }
}

const goToAddress = () => {
  router.push({
    path: '/neo/addresses',
    query: { returnTo: '/neo/checkout', addressId: selectedAddressId.value, productId: route.query.productId }
  })
}

const handleSubmit = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  submitting.value = true
  try {
    const res = await orderApi.createOrder({
      productId: route.query.productId,
      addressId: selectedAddressId.value,
      remark: remark.value
    })
    if (res.code === '200' && res.data?.id) {
      router.push({
        path: '/neo/pay-center',
        query: { orderId: res.data.id, amount: product.value.price }
      })
    } else {
      ElMessage.error(res.message || '创建订单失败')
    }
  } catch (e) {
    ElMessage.error('创建订单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (!route.query.productId) {
    ElMessage.error('商品信息不存在')
    router.back()
    return
  }
  fetchProduct()
  fetchAddresses()
  
  // 监听地址选择返回
  if (route.query.addressId) {
    selectedAddressId.value = Number(route.query.addressId)
  }
})
</script>

<style scoped>
.checkout-container :deep(.el-textarea__inner) {
  border: none;
  background: #f5f5f5;
  border-radius: 12px;
}
</style>
