<template>
  <div class="product-publish-container">
    <template v-if="isMobileScreen">
      <div class="product-publish-mobile bg-gray-50 min-h-screen pb-24">
        <div class="sticky top-0 z-50 bg-white px-4 py-3 border-b border-gray-100 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <el-icon :size="20" @click="router.back()"><ArrowLeft /></el-icon>
            <h1 class="text-lg font-bold">发布商品</h1>
          </div>
          <el-button type="primary" size="small" :loading="submitting" @click="handleSubmit">发布</el-button>
        </div>

        <div class="p-4 space-y-4">
          <!-- 图片上传 -->
          <div class="bg-white rounded-2xl p-4 shadow-sm border border-gray-100">
            <div class="flex items-center justify-between mb-3">
              <span class="text-sm font-bold">商品图片</span>
              <el-button v-if="imageUrls.length > 0" type="primary" link size="small" :loading="analyzing" @click="handleAnalyze">AI 评估</el-button>
            </div>
            <el-upload v-model:file-list="fileList" :action="uploadAction" :headers="uploadHeaders" list-type="picture-card" :on-success="handleUploadSuccess" :on-remove="handleRemove" :limit="9">
              <el-icon><Plus /></el-icon>
            </el-upload>
            <p class="text-[10px] text-gray-400 mt-2">最多上传 9 张，第一张默认为封面</p>
          </div>

          <!-- 表单信息 -->
          <div class="bg-white rounded-2xl p-4 shadow-sm border border-gray-100">
            <el-form :model="form" label-position="top">
              <el-form-item label="标题">
                <el-input v-model="form.title" placeholder="品牌、型号、品类" />
              </el-form-item>
              <el-form-item label="描述">
                <el-input v-model="form.description" type="textarea" :rows="4" placeholder="描述一下宝贝的转手原因、成色、规格..." />
              </el-form-item>
              <div class="grid grid-cols-2 gap-4">
                <el-form-item label="价格">
                  <el-input-number v-model="form.price" :precision="2" :step="1" :min="0" controls-position="right" class="!w-full" />
                </el-form-item>
                <el-form-item label="成色 (1-10)">
                  <el-input-number v-model="form.conditionScore" :min="1" :max="10" controls-position="right" class="!w-full" />
                </el-form-item>
              </div>
              <el-form-item label="分类">
                <el-select v-model="form.categoryId" placeholder="选择分类" class="w-full">
                  <el-option v-for="c in CATEGORIES" :key="c.id" :label="$t(`categories.${c.id}`)" :value="c.id" />
                </el-select>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="product-publish-page p-6 max-w-4xl mx-auto pb-24">
        <el-card>
          <template #header><h1 class="text-xl font-bold">{{ form.id ? '编辑商品' : '发布新商品' }}</h1></template>
          <el-form :model="form" label-width="100px" class="mt-4">
            <el-form-item label="商品图片">
              <el-upload v-model:file-list="fileList" :action="uploadAction" :headers="uploadHeaders" list-type="picture-card" :on-success="handleUploadSuccess" :on-remove="handleRemove" :limit="9">
                <el-icon><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item label="商品标题">
              <el-input v-model="form.title" placeholder="请输入商品标题" />
            </el-form-item>
            <el-form-item label="商品描述">
              <el-input v-model="form.description" type="textarea" :rows="6" placeholder="详细描述商品信息" />
            </el-form-item>
            <div class="grid grid-cols-2 gap-6">
              <el-form-item label="价格">
                <el-input-number v-model="form.price" :precision="2" :min="0" class="!w-full" />
              </el-form-item>
              <el-form-item label="成色评分">
                <el-input-number v-model="form.conditionScore" :min="1" :max="10" class="!w-full" />
              </el-form-item>
            </div>
            <el-form-item label="所属分类">
              <el-select v-model="form.categoryId" placeholder="请选择分类" class="w-full">
                <el-option v-for="c in CATEGORIES" :key="c.id" :label="$t(`categories.${c.id}`)" :value="c.id" />
              </el-select>
            </el-form-item>
            <el-form-item class="mt-8">
              <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">立即发布</el-button>
              <el-button size="large" @click="router.back()">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { productApi } from '@/api/product'
import { CATEGORIES } from '@/utils/categories'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { isMobileScreen } = useDeviceType()

const submitting = ref(false)
const analyzing = ref(false)
const fileList = ref([])
const imageUrls = ref([])

const form = ref({
  id: null,
  title: '',
  description: '',
  price: 0,
  conditionScore: 9,
  categoryId: '',
  images: ''
})

const apiBaseUrl = computed(() => (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/+$/, ''))
const uploadAction = computed(() => `${apiBaseUrl.value}/file/upload`)
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token') || userStore.token
  return { Authorization: token ? `Bearer ${token}` : '' }
})

const handleUploadSuccess = (res) => {
  if (res.code === '200') {
    const url = res?.data?.fileUrl || res?.data?.url
    if (!url) return
    imageUrls.value.push(url)
    form.value.images = JSON.stringify(imageUrls.value)
  }
}

const handleRemove = (file) => {
  const url = file.response?.data?.fileUrl || file.response?.data?.url || file.url
  imageUrls.value = imageUrls.value.filter(u => u !== url)
  form.value.images = JSON.stringify(imageUrls.value)
}

const handleAnalyze = async () => {
  if (imageUrls.value.length === 0) return
  analyzing.value = true
  try {
    const res = await productApi.analyzeImage(imageUrls.value[0])
    if (res.code === '200') {
      const data = res.data
      form.value.title = data.productName || form.value.title
      form.value.description = data.description || form.value.description
      form.value.conditionScore = data.conditionScore || form.value.conditionScore
      ElMessage.success('AI 评估完成')
    }
  } catch (e) {
  } finally {
    analyzing.value = false
  }
}

const handleSubmit = async () => {
  if (!form.value.title || imageUrls.value.length === 0) {
    return ElMessage.warning('请填写标题并上传图片')
  }
  submitting.value = true
  try {
    const res = form.value.id 
      ? await productApi.updateProduct(form.value)
      : await productApi.createProduct(form.value)
    if (res.code === '200') {
      ElMessage.success(form.value.id ? '更新成功' : '发布成功')
      router.push('/user/my-products')
    }
  } catch (e) {
  } finally {
    submitting.value = false
  }
}

const fetchProduct = async (id) => {
  try {
    const res = await productApi.getProductById(id)
    if (res.code === '200') {
      const data = res.data
      form.value = { ...data }
      try {
        const imgs = JSON.parse(data.images)
        imageUrls.value = Array.isArray(imgs) ? imgs : [imgs]
        fileList.value = imageUrls.value.map(url => ({ url }))
      } catch (e) {}
    }
  } catch (e) {}
}

onMounted(() => {
  if (route.query.id) {
    form.value.id = route.query.id
    fetchProduct(route.query.id)
  }
})
</script>
