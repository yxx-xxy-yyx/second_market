<template>
  <div class="addresses-container">
    <!-- 移动端视图 -->
    <div v-if="isMobileScreen" class="addresses-mobile min-h-screen bg-gray-50">
      <!-- 顶部导航 -->
      <div class="sticky top-0 z-50 bg-white px-4 py-3 flex items-center justify-between border-b border-gray-100 shadow-sm">
        <div class="h-10 w-10 rounded-full bg-gray-100 flex items-center justify-center cursor-pointer hover:bg-gray-200 transition" @click="router.back()">
          <el-icon :size="20"><ArrowLeft /></el-icon>
        </div>
        <span class="text-base font-bold text-gray-900">收货地址</span>
        <div class="h-10 w-10 rounded-full bg-primary flex items-center justify-center cursor-pointer hover:bg-primary-dark transition" @click="showAddDialog = true">
          <el-icon :size="20" class="text-white"><Plus /></el-icon>
        </div>
      </div>

      <!-- 地址列表 -->
      <div class="p-4 space-y-3 pb-24">
        <div v-if="addresses.length === 0" class="text-center py-12">
          <el-icon :size="64" class="text-gray-300 mb-4"><Location /></el-icon>
          <p class="text-gray-500">暂无收货地址</p>
          <el-button type="primary" class="mt-4" @click="showAddDialog = true">添加地址</el-button>
        </div>

        <div v-for="addr in addresses" :key="addr.id" 
             class="bg-white rounded-2xl p-4 shadow-sm border-2 transition-all"
             :class="selectedId === addr.id ? 'border-primary' : 'border-transparent hover:border-gray-200'"
             @click="handleSelect(addr)">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <span class="text-lg font-bold text-gray-900">{{ addr.receiverName }}</span>
                <span class="text-sm text-gray-500">{{ addr.receiverPhone }}</span>
                <el-tag v-if="addr.isDefault" size="small" type="primary" class="rounded-full">默认</el-tag>
              </div>
              <p class="text-sm text-gray-600 leading-relaxed">{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}</p>
            </div>
            <div class="flex items-center gap-2">
              <div class="h-8 w-8 rounded-full bg-gray-100 flex items-center justify-center cursor-pointer hover:bg-gray-200 transition" @click.stop="handleEdit(addr)">
                <el-icon :size="16"><Edit /></el-icon>
              </div>
              <div class="h-8 w-8 rounded-full bg-red-50 flex items-center justify-center cursor-pointer hover:bg-red-100 transition" @click.stop="handleDelete(addr.id)">
                <el-icon :size="16" class="text-red-500"><Delete /></el-icon>
              </div>
            </div>
          </div>
          <div v-if="selectedId === addr.id" class="mt-3 pt-3 border-t border-gray-100">
            <el-button type="primary" class="w-full" @click.stop="confirmSelection">确认选择此地址</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 桌面端视图 -->
    <div v-else class="addresses-desktop bg-gradient-to-br from-gray-50 to-gray-100 min-h-screen">
      <div class="max-w-4xl mx-auto p-8">
        <div class="bg-white rounded-3xl shadow-xl p-6">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-2xl font-bold text-gray-900">收货地址管理</h2>
            <el-button type="primary" @click="showAddDialog = true">
              <el-icon class="mr-1"><Plus /></el-icon>添加地址
            </el-button>
          </div>

          <el-empty v-if="addresses.length === 0" description="暂无收货地址">
            <el-button type="primary" @click="showAddDialog = true">添加地址</el-button>
          </el-empty>

          <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div v-for="addr in addresses" :key="addr.id"
                 class="border-2 rounded-2xl p-5 cursor-pointer transition-all hover:shadow-lg"
                 :class="selectedId === addr.id ? 'border-primary bg-primary/5' : 'border-gray-100 hover:border-gray-200'"
                 @click="handleSelect(addr)">
              <div class="flex items-start justify-between mb-3">
                <div>
                  <span class="text-lg font-bold text-gray-900">{{ addr.receiverName }}</span>
                  <span class="text-sm text-gray-500 ml-3">{{ addr.receiverPhone }}</span>
                </div>
                <el-tag v-if="addr.isDefault" size="small" type="primary">默认</el-tag>
              </div>
              <p class="text-sm text-gray-600">{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}</p>
              <div class="flex items-center gap-2 mt-4 pt-4 border-t border-gray-100">
                <el-button size="small" @click="handleEdit(addr)">编辑</el-button>
                <el-button size="small" type="danger" plain @click="handleDelete(addr.id)">删除</el-button>
                <el-button v-if="!addr.isDefault" size="small" @click="setDefault(addr.id)">设为默认</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑地址弹窗 -->
    <el-dialog v-model="showAddDialog" :title="isEditing ? '编辑地址' : '添加地址'" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="form.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { addressApi } from '@/api/address'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus, Edit, Delete, Location } from '@element-plus/icons-vue'
import { useDeviceType } from '@/utils/device'

const route = useRoute()
const router = useRouter()
const { isMobileScreen } = useDeviceType()

const addresses = ref([])
const selectedId = ref(null)
const showAddDialog = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const formRef = ref(null)

const form = ref({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

const rules = {
  receiverName: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const fetchAddresses = async () => {
  try {
    const res = await addressApi.getList()
    if (res.code === '200') {
      addresses.value = res.data || []
      const defaultAddr = addresses.value.find(a => a.isDefault)
      if (defaultAddr) selectedId.value = defaultAddr.id
    }
  } catch (e) {
    ElMessage.error('获取地址列表失败')
  }
}

const handleSelect = (addr) => {
  selectedId.value = addr.id
  if (route.query.returnTo) {
    router.push({
      path: route.query.returnTo,
      query: { addressId: addr.id }
    })
  }
}

const confirmSelection = () => {
  if (route.query.returnTo) {
    router.push({
      path: route.query.returnTo,
      query: { addressId: selectedId.value }
    })
  }
}

const handleEdit = (addr) => {
  isEditing.value = true
  editingId.value = addr.id
  form.value = { ...addr }
  showAddDialog.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除此地址吗？', '提示', { type: 'warning' })
    const res = await addressApi.delete(id)
    if (res.code === '200') {
      ElMessage.success('删除成功')
      fetchAddresses()
    }
  } catch {}
}

const setDefault = async (id) => {
  try {
    const res = await addressApi.setDefault(id)
    if (res.code === '200') {
      ElMessage.success('设置成功')
      fetchAddresses()
    }
  } catch {}
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    let res
    if (isEditing.value) {
      res = await addressApi.update(editingId.value, form.value)
    } else {
      res = await addressApi.add(form.value)
    }
    if (res.code === '200') {
      ElMessage.success(isEditing.value ? '修改成功' : '添加成功')
      showAddDialog.value = false
      resetForm()
      fetchAddresses()
    }
  } catch {}
}

const resetForm = () => {
  form.value = {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false
  }
  isEditing.value = false
  editingId.value = null
}

onMounted(() => {
  fetchAddresses()
  if (route.query.addressId) {
    selectedId.value = Number(route.query.addressId)
  }
})
</script>

<style scoped>
.addresses-container :deep(.el-dialog__body) {
  padding: 20px;
}
</style>
