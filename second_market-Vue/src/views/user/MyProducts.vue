<template>
  <div class="my-products-container">
    <template v-if="isMobileScreen">
      <div class="my-products-mobile bg-gray-50 min-h-screen pb-24">
        <div class="sticky top-0 z-50 bg-white px-4 py-3 flex items-center gap-3 border-b border-gray-100">
          <el-icon :size="20" class="text-gray-600" @click="router.back()"><ArrowLeft /></el-icon>
          <h1 class="text-lg font-bold text-gray-900 flex-1">我发布的</h1>
          <LangSwitcher />
        </div>

        <div class="p-4 space-y-4">
          <div v-if="loading && products.length === 0" class="text-center py-10 text-gray-400 text-sm">加载中...</div>
          <div v-else class="space-y-3">
            <div v-for="product in products" :key="product.id" class="bg-white rounded-2xl p-4 shadow-sm border border-gray-50 flex gap-4 active:scale-[0.98] transition-all" @click="router.push(`/user/product/${product.id}`)">
              <div class="w-24 h-24 rounded-xl overflow-hidden bg-gray-50 flex-shrink-0">
                <img :src="getProductImage(product.images)" class="w-full h-full object-cover" />
              </div>
              <div class="flex-1 min-w-0 flex flex-col justify-between py-0.5">
                <div>
                  <div class="flex items-center justify-between gap-2">
                    <h3 class="text-sm font-bold text-gray-900 truncate">{{ product.title }}</h3>
                    <el-tag :type="getStatusType(product.status)" size="small">{{ getStatusText(product.status) }}</el-tag>
                  </div>
                  <div class="mt-1 text-[10px] text-gray-400">浏览 {{ product.viewCount || 0 }} · {{ new Date(product.createTime).toLocaleDateString() }}</div>
                </div>
                <div class="flex items-end justify-between">
                  <div class="text-lg font-bold text-primary">¥{{ product.price }}</div>
                  <div class="flex gap-2" @click.stop>
                    <el-button size="small" @click="handleEdit(product.id)">编辑</el-button>
                    <el-button size="small" type="danger" plain @click="handleDelete(product.id)">删除</el-button>
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-if="products.length === 0 && !loading" description="暂无发布" />
          </div>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="my-products-page p-6 max-w-7xl mx-auto pb-24">
        <div class="flex items-center justify-between mb-6">
          <h1 class="text-2xl font-bold">我发布的商品</h1>
          <el-button type="primary" @click="router.push('/user/publish')">发布新商品</el-button>
        </div>

        <el-table v-loading="loading" :data="products" style="width: 100%">
          <el-table-column label="商品信息" min-width="400">
            <template #default="{ row }">
              <div class="flex items-center gap-4">
                <el-image :src="getProductImage(row.images)" class="w-20 h-20 rounded" fit="cover" />
                <div class="min-w-0">
                  <div class="font-bold truncate">{{ row.title }}</div>
                  <div class="text-xs text-gray-500 mt-1 line-clamp-1">{{ row.description }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="价格" width="120">
            <template #default="{ row }">
              <span class="font-bold text-primary">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="浏览" width="100" prop="viewCount" />
          <el-table-column label="发布时间" width="180">
            <template #default="{ row }">
              {{ new Date(row.createTime).toLocaleString() }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="handleEdit(row.id)">编辑</el-button>
              <el-button size="small" type="danger" plain @click="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { productApi } from '@/api/product'
import { formatProductImageUrl } from '@/utils/url'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'

const router = useRouter()
const { isMobileScreen } = useDeviceType()
const products = ref([])
const loading = ref(false)

const getProductImage = (images) => {
  if (!images) return ''
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return formatProductImageUrl(Array.isArray(arr) ? arr[0] : arr)
  } catch {
    return formatProductImageUrl(images)
  }
}

const getStatusType = (status) => {
  const types = { 1: 'info', 2: 'success', 3: 'warning', 4: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 1: '审核中', 2: '在售中', 3: '已下架', 4: '已售出' }
  return texts[status] || '未知'
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await productApi.getMyProducts()
    if (res.code === '200') products.value = res.data || []
  } catch (e) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleEdit = (id) => {
  router.push(`/user/publish?id=${id}`)
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
    const res = await productApi.deleteProduct(id)
    if (res.code === '200') {
      ElMessage.success('删除成功')
      fetchProducts()
    }
  } catch (e) {}
}

onMounted(() => fetchProducts())
</script>