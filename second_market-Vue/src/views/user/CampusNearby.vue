<template>
  <div class="campus-nearby-page min-h-screen bg-[#f8f8fa]">
    <!-- Header -->
    <div class="sticky top-0 z-50 bg-primary text-white px-4 py-3 flex items-center justify-between shadow-sm">
      <div class="flex items-center gap-3">
        <el-icon class="text-xl cursor-pointer" @click="router.back()"><ArrowLeft /></el-icon>
        <h1 class="text-lg font-bold">{{ t('campus.nearby') }}</h1>
      </div>
      <div class="flex items-center gap-2">
        <LangSwitcher glass />
        <div class="flex items-center gap-1 text-[10px] bg-white/20 px-3 py-1.5 rounded-full cursor-pointer active:scale-95 transition-transform"
          @click="openSchoolPicker">
          <el-icon><Location /></el-icon>
          <span>{{ currentSchool }}</span>
        </div>
      </div>
    </div>

    <!-- Category Tabs -->
    <div class="px-4 py-3 bg-white sticky top-[52px] z-40 shadow-sm">
      <div class="flex bg-gray-100 p-1 rounded-2xl">
        <div v-for="tab in tabs" :key="tab.value" 
          class="flex-1 py-2 text-center text-sm font-bold rounded-xl transition-all cursor-pointer"
          :class="activeTab === tab.value ? 'bg-white text-primary shadow-sm' : 'text-gray-500'"
          @click="activeTab = tab.value">
          {{ tab.label }}
        </div>
      </div>
    </div>

    <!-- Content List -->
    <div class="px-4 py-4 space-y-4 pb-24">
      <div v-if="loading" class="space-y-4">
        <div v-for="i in 3" :key="i" class="bg-white p-4 rounded-3xl animate-pulse h-40"></div>
      </div>

      <div v-else-if="services.length > 0" class="space-y-4">
        <div v-for="service in services" :key="service.id" 
          class="bg-white rounded-3xl p-5 shadow-sm border border-gray-50 active:scale-[0.99] transition-all duration-200">
          
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-2">
              <el-avatar :size="32" :src="formatAvatarUrl(service.userAvatar)" />
              <div>
                <h4 class="text-xs font-bold text-gray-900">{{ service.userNickname || '校友' }}</h4>
                <p class="text-[10px] text-gray-400">{{ formatDate(service.createTime) }}</p>
              </div>
            </div>
            <div :class="getStatusClass(service.status)" class="text-[10px] px-2 py-0.5 rounded-full font-medium">
              {{ getStatusLabel(service.status) }}
            </div>
          </div>

          <h3 class="text-base font-bold text-gray-800 mb-2">{{ service.title }}</h3>
          <p class="text-sm text-gray-600 mb-4 line-clamp-2">{{ service.content }}</p>

          <div class="flex flex-wrap gap-2 mb-4">
            <div v-if="service.location" class="flex items-center gap-1 text-[10px] bg-blue-50 text-blue-600 px-2 py-1 rounded-lg">
              <el-icon><Location /></el-icon>
              {{ service.location }}
            </div>
            <div v-if="service.type === 2 && service.limitCount" class="flex items-center gap-1 text-[10px] bg-green-50 text-green-600 px-2 py-1 rounded-lg">
              <el-icon><User /></el-icon>
              {{ service.currentCount }}/{{ service.limitCount }} 人
            </div>
            <div v-if="service.reward > 0" class="flex items-center gap-1 text-[10px] bg-primary/10 text-primary px-2 py-1 rounded-lg font-bold">
              <el-icon><Coin /></el-icon>
              ¥ {{ service.reward }}
            </div>
          </div>

          <div class="flex justify-end gap-3 pt-4 border-t border-gray-50">
            <template v-if="service.userId === userStore.user?.id">
              <el-button v-if="service.status === 2" size="small" round class="!bg-green-500 !text-white !border-none" @click="handleComplete(service.id)">确认完成</el-button>
              <el-button v-if="service.status === 1" size="small" round plain @click="handleCancel(service.id)">取消任务</el-button>
            </template>
            <template v-else>
              <el-button v-if="service.status === 1" size="small" round class="!bg-primary !text-white !border-none" @click="handleAccept(service.id)">
                {{ service.type === 2 ? '加入拼单' : (service.type === 3 ? '申请借用' : '立即接单') }}
              </el-button>
              <el-button v-else-if="service.status === 2 && service.accepterId === userStore.user?.id" size="small" round disabled>进行中</el-button>
            </template>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无相关服务" :image-size="100" />
    </div>

    <!-- Floating Action Button -->
    <div class="fixed bottom-24 right-6 w-14 h-14 bg-primary rounded-full shadow-xl flex items-center justify-center text-white text-2xl active:scale-90 transition-all z-50 cursor-pointer border-4 border-white"
      @click="openPublishDialog">
      <el-icon><Plus /></el-icon>
    </div>

    <!-- Publish Dialog -->
    <el-dialog v-model="showPublishDialog" title="发布校园周边任务" width="90%" border-radius="24px" class="rounded-3xl custom-dialog">
      <el-form :model="publishForm" label-position="top" class="mt-2">
        <el-form-item label="任务类型">
          <el-radio-group v-model="publishForm.type" class="w-full flex">
            <el-radio-button :label="1" class="flex-1">跑腿互助</el-radio-button>
            <el-radio-button :label="2" class="flex-1">拼单拼车</el-radio-button>
            <el-radio-button :label="3" class="flex-1">资源共享</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="所属学校" required>
          <el-select v-model="publishForm.schoolId" placeholder="请选择学校" class="w-full">
            <el-option
              v-for="item in schoolStore.schoolList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="标题">
          <el-input v-model="publishForm.title" placeholder="请输入任务标题" maxlength="50" show-word-limit />
        </el-form-item>

        <el-form-item label="详细内容">
          <el-input v-model="publishForm.content" type="textarea" :rows="3" placeholder="请描述具体需求..." />
        </el-form-item>

        <el-form-item label="地点">
          <el-input v-model="publishForm.location" placeholder="例如：西门菜鸟驿站、图书馆" />
        </el-form-item>

        <div class="grid grid-cols-2 gap-4">
          <el-form-item v-if="publishForm.type === 1" label="报酬 (¥)">
            <el-input-number v-model="publishForm.reward" :min="0" :precision="2" class="!w-full" />
          </el-form-item>
          <el-form-item v-if="publishForm.type === 2" label="人数上限">
            <el-input-number v-model="publishForm.limitCount" :min="2" :max="10" class="!w-full" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <div class="flex gap-3 px-2 pb-2">
          <el-button class="flex-1 !rounded-xl" @click="showPublishDialog = false">取消</el-button>
          <el-button class="flex-1 !rounded-xl !bg-primary !text-white !border-none" :loading="publishing" @click="handlePublish">立即发布</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- School Picker Drawer -->
    <Teleport to="body">
      <Transition name="fade">
        <div v-if="showSchoolPicker" class="fixed inset-0 z-[100] flex items-end justify-center px-4 pb-10">
          <div class="fixed inset-0 bg-black/40 backdrop-blur-sm" @click="showSchoolPicker = false"></div>
          
          <Transition name="slide-up">
            <div class="bg-white w-full max-w-[500px] max-h-[70vh] flex flex-col z-10 overflow-hidden rounded-[32px] shadow-2xl border border-gray-100">
              <div class="flex items-center justify-between p-6 pb-2">
                <span class="text-gray-900 text-xl font-extrabold">选择学校</span>
                <div class="w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center cursor-pointer active:scale-90 transition-transform" @click="showSchoolPicker = false">
                  <el-icon class="text-gray-500"><Close /></el-icon>
                </div>
              </div>

              <!-- Search School -->
              <div class="px-6 py-4">
                <div class="relative">
                  <input 
                    v-model="schoolSearchKeyword" 
                    placeholder="搜索学校..."
                    class="w-full bg-gray-50 px-11 py-3.5 rounded-2xl border-none outline-none text-base h-14 focus:ring-2 focus:ring-primary/20 transition-all"
                  />
                  <el-icon class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 text-xl"><Search /></el-icon>
                </div>
              </div>
              
              <div class="flex-1 overflow-y-auto px-6 pb-8 space-y-3 custom-scrollbar">
                <div 
                  v-for="school in filteredSchools" 
                  :key="school.value"
                  class="px-5 py-4 rounded-2xl transition-all active:scale-[0.98] border cursor-pointer flex items-center justify-between"
                  :class="schoolStore.selectedSchool === school.value ? 'bg-primary/10 border-primary/20 shadow-sm' : 'bg-gray-50 border-transparent hover:bg-gray-100'"
                  @click="selectSchool(school)"
                >
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 bg-white rounded-xl shadow-sm flex items-center justify-center overflow-hidden border border-gray-50">
                      <img v-if="school.logoUrl" :src="school.logoUrl" class="w-full h-full object-contain" />
                      <el-icon v-else class="text-gray-300 text-xl"><Location /></el-icon>
                    </div>
                    <span class="text-gray-800 text-base font-bold" :class="{ 'text-primary': schoolStore.selectedSchool === school.value }">{{ school.label }}</span>
                  </div>
                  <div v-if="schoolStore.selectedSchool === school.value" class="w-5 h-5 rounded-full bg-primary flex items-center justify-center">
                    <el-icon class="text-white text-xs"><Check /></el-icon>
                  </div>
                </div>
              </div>
            </div>
          </Transition>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Location, User, Coin, Plus, Search, Close, Check } from '@element-plus/icons-vue'
import { campusServiceApi } from '@/api/campus-service'
import { schoolApi } from '@/api/school'
import { useUserStore } from '@/stores/user'
import { useSchoolStore } from '@/stores/school'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'
import dayjs from 'dayjs'
import { formatAvatarUrl } from '@/utils/url'

const router = useRouter()
const userStore = useUserStore()
const schoolStore = useSchoolStore()
const { locale, t } = useI18n()

const loading = ref(false)
const publishing = ref(false)
const services = ref([])
const activeTab = ref(1)
const showPublishDialog = ref(false)

// School Picker State
const showSchoolPicker = ref(false)
const schoolSearchKeyword = ref('')

const tabs = [
  { label: '跑腿互助', value: 1 },
  { label: '拼单拼车', value: 2 },
  { label: '资源共享', value: 3 }
]

const publishForm = ref({
  type: 1,
  title: '',
  content: '',
  location: '',
  reward: 0,
  limitCount: 2,
  schoolId: schoolStore.selectedSchool || userStore.user?.schoolId || ''
})

const currentSchool = computed(() => {
  return schoolStore.currentSchoolName || '选择学校'
})

const filteredSchools = computed(() => {
  if (!schoolSearchKeyword.value) return schoolStore.schoolList
  const kw = schoolSearchKeyword.value.toLowerCase()
  return schoolStore.schoolList.filter(s => 
    (s.label && s.label.toLowerCase().includes(kw))
  )
})

const openSchoolPicker = async () => {
  showSchoolPicker.value = true
  if (schoolStore.schoolList.length === 0) {
    await schoolStore.getSchoolList()
  }
}

const openPublishDialog = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (schoolStore.schoolList.length === 0) {
    await schoolStore.getSchoolList()
  }
  publishForm.value.schoolId = schoolStore.selectedSchool || userStore.user?.schoolId || ''
  showPublishDialog.value = true
}

const selectSchool = (school) => {
  schoolStore.setSchool(school.value)
  showSchoolPicker.value = false
  fetchServices()
}

const fetchServices = async () => {
  loading.value = true
  try {
    const params = {
      type: activeTab.value,
      pageNum: 1,
      pageSize: 50
    }
    
    // 只有当有选中的学校时才添加 schoolId 参数，且确保它是数字或 null
    if (schoolStore.selectedSchool && schoolStore.selectedSchool !== '') {
      params.schoolId = Number(schoolStore.selectedSchool)
    }

    const res = await campusServiceApi.getList(params)
    if (res.success) {
      // 过滤掉已完成和已取消的
      const records = res.data?.records || res.data || []
      services.value = records.filter(s => s.status === 1 || s.status === 2)
    }
  } catch (error) {
    console.error('Fetch services failed:', error)
  } finally {
    loading.value = false
  }
}

const handlePublish = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!publishForm.value.title || !publishForm.value.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }

  if (!publishForm.value.schoolId) {
    ElMessage.warning('请选择学校')
    return
  }

  publishing.value = true
  try {
    // 确保数值类型正确，且 schoolId 不为空字符串
    const data = {
      ...publishForm.value,
      reward: Number(publishForm.value.reward) || 0,
      limitCount: Number(publishForm.value.limitCount) || 0,
      schoolId: Number(publishForm.value.schoolId),
      status: 1 // 初始状态为待接单
    }
    
    const res = await campusServiceApi.publish(data)
    if (res.success) {
      ElMessage.success('发布成功')
      showPublishDialog.value = false
      // 重置表单
      publishForm.value = {
        type: activeTab.value,
        title: '',
        content: '',
        location: '',
        reward: 0,
        limitCount: 2,
        schoolId: schoolStore.selectedSchool || userStore.user?.schoolId || ''
      }
      fetchServices()
    } else {
      ElMessage.error(res.message || '发布失败')
    }
  } catch (error) {
    console.error('Publish failed:', error)
    ElMessage.error('发布失败，请重试')
  } finally {
    publishing.value = false
  }
}

const handleAccept = async (id) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    await ElMessageBox.confirm('确定要接单/加入吗？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info',
      roundButton: true
    })
    
    const res = await campusServiceApi.accept(id)
    if (res.success) {
      ElMessage.success('操作成功')
      fetchServices()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch {
    // User cancelled
  }
}

const handleComplete = async (id) => {
  try {
    await ElMessageBox.confirm('确认该任务已完成吗？', '确认', {
      confirmButtonText: '完成',
      cancelButtonText: '取消',
      type: 'success',
      roundButton: true
    })
    
    const res = await campusServiceApi.complete(id)
    if (res.success) {
      ElMessage.success('任务已完成')
      fetchServices()
    }
  } catch {}
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该任务吗？', '确认', {
      confirmButtonText: '取消任务',
      cancelButtonText: '返回',
      type: 'warning',
      roundButton: true
    })
    
    const res = await campusServiceApi.cancel(id)
    if (res.success) {
      ElMessage.success('任务已取消')
      fetchServices()
    }
  } catch {}
}

const getStatusLabel = (status) => {
  const labels = {
    1: activeTab.value === 2 ? '招募中' : '待接单',
    2: '进行中',
    3: '已完成',
    4: '已取消'
  }
  return labels[status] || '未知'
}

const getStatusClass = (status) => {
  const classes = {
    1: 'bg-orange-50 text-orange-500',
    2: 'bg-blue-50 text-blue-500',
    3: 'bg-green-50 text-green-500',
    4: 'bg-gray-50 text-gray-500'
  }
  return classes[status] || 'bg-gray-50 text-gray-500'
}

const formatDate = (date) => {
  return dayjs(date).format('MM-DD HH:mm')
}

watch(activeTab, () => {
  fetchServices()
})

watch(locale, () => {
  schoolStore.getSchoolList()
  fetchServices()
})

onMounted(async () => {
  await schoolStore.getSchoolList()
  fetchServices()
})
</script>

<style scoped>
.custom-dialog :deep(.el-dialog) {
  border-radius: 24px;
  overflow: hidden;
}

.custom-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px 20px 10px;
}

.custom-dialog :deep(.el-dialog__title) {
  font-weight: 800;
  color: #1f2937;
}

:deep(.el-radio-button__inner) {
  border-radius: 12px !important;
  margin: 0 4px;
  border: 1px solid #e5e7eb !important;
}

:deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-left: 1px solid #e5e7eb !important;
}

:deep(.el-radio-button__orig-radio:checked + .el-radio-button__inner) {
  background-color: #ec4899 !important;
  border-color: #ec4899 !important;
  color: white !important;
  box-shadow: none !important;
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.4s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.4s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  transform: translateY(100%);
  opacity: 0;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 10px;
}
</style>
