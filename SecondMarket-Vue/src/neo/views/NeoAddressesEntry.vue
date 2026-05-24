<template>
  <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
    <div class="px-5 py-5 bg-gray-50 flex items-center justify-between">
      <div>
        <div class="text-[16px] md:text-[18px] font-semibold text-gray-900">{{ $t('neo.address.title') }}</div>
        <div class="mt-1 text-[12px] text-gray-500">{{ $t('neo.address.subtitle') }}</div>
      </div>
      <el-button type="primary" @click="openCreate">
        {{ $t('neo.address.add') }}
      </el-button>
    </div>

    <div v-if="addr.list.length === 0" class="px-6 py-10">
      <el-empty :description="$t('neo.address.empty')" :image-size="140">
        <el-button type="primary" @click="openCreate">{{ $t('neo.address.add') }}</el-button>
      </el-empty>
    </div>

    <div v-else class="px-5 py-5 space-y-3">
      <div
        v-for="it in addr.list"
        :key="it.id"
        class="rounded-2xl border border-gray-100 hover:border-[rgba(var(--primary-rgb),0.35)] transition bg-white p-4"
      >
        <div class="flex items-start justify-between gap-4">
          <button class="text-left min-w-0 flex-1" @click="handlePick(it)">
            <div class="flex items-center gap-2 flex-wrap">
              <div class="text-[14px] font-semibold text-gray-900">{{ it.name || '-' }}</div>
              <div class="text-[12px] text-gray-500">{{ it.phone || '-' }}</div>
              <el-tag v-if="it.isDefault" size="small" type="success">{{ $t('neo.address.default') }}</el-tag>
              <el-tag size="small" type="info">{{ it.country === 'CN' ? $t('neo.address.cn') : $t('neo.address.kr') }}</el-tag>
            </div>
            <div class="mt-2 text-[13px] text-gray-700 leading-relaxed">
              {{ formatAddress(it) }}
            </div>
          </button>

          <div class="shrink-0 flex items-center gap-2">
            <el-button text class="!text-[color:var(--primary-color)]" @click="setDefault(it.id)">
              {{ $t('neo.address.setDefault') }}
            </el-button>
            <el-button text class="!text-gray-500" @click="openEdit(it)">
              {{ $t('common.edit') }}
            </el-button>
            <el-button text class="!text-rose-600" @click="remove(it.id)">
              {{ $t('common.delete') }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <el-dialog v-model="showForm" :title="formTitle" width="680px" :close-on-click-modal="false">
    <el-form :model="form" label-width="100px">
      <el-form-item :label="$t('neo.address.country')">
        <el-segmented v-model="form.country" :options="countryOptions" />
      </el-form-item>
      <el-form-item :label="$t('neo.address.name')">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item :label="$t('neo.address.phone')">
        <el-input v-model="form.phone" />
      </el-form-item>

      <template v-if="form.country === 'CN'">
        <el-form-item :label="$t('neo.address.province')">
          <el-input v-model="form.province" />
        </el-form-item>
        <el-form-item :label="$t('neo.address.city')">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item :label="$t('neo.address.district')">
          <el-input v-model="form.district" />
        </el-form-item>
        <el-form-item :label="$t('neo.address.detail')">
          <el-input v-model="form.detail" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item :label="$t('neo.address.zipcode')">
          <el-input v-model="form.zipcode" />
        </el-form-item>
      </template>

      <template v-else>
        <el-form-item :label="$t('neo.address.road')">
          <el-input v-model="form.detail" />
        </el-form-item>
        <el-form-item :label="$t('neo.address.detail2')">
          <el-input v-model="form.detail2" />
        </el-form-item>
        <el-form-item :label="$t('neo.address.zipcode')">
          <el-input v-model="form.zipcode" />
        </el-form-item>
      </template>

      <el-form-item>
        <el-checkbox v-model="form.isDefault">{{ $t('neo.address.setAsDefault') }}</el-checkbox>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showForm = false">{{ $t('common.cancel') }}</el-button>
      <el-button type="primary" @click="save">{{ $t('common.save') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useNeoAddressStore } from '@/neo/stores/neoAddress'
import { useNeoCheckoutStore } from '@/neo/stores/neoCheckout'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const addr = useNeoAddressStore()
const checkout = useNeoCheckoutStore()

const showForm = ref(false)
const editingId = ref('')

const countryOptions = computed(() => [
  { label: t('neo.address.kr'), value: 'KR' },
  { label: t('neo.address.cn'), value: 'CN' }
])

const form = reactive({
  id: '',
  country: 'KR',
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  detail2: '',
  zipcode: '',
  isDefault: false
})

const formTitle = computed(() => (editingId.value ? t('neo.address.edit') : t('neo.address.add')))

const openCreate = () => {
  editingId.value = ''
  Object.assign(form, {
    id: '',
    country: 'KR',
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    detail2: '',
    zipcode: '',
    isDefault: addr.list.length === 0
  })
  showForm.value = true
}

const openEdit = (it) => {
  editingId.value = it.id
  Object.assign(form, {
    id: it.id,
    country: it.country || 'KR',
    name: it.name || '',
    phone: it.phone || '',
    province: it.province || '',
    city: it.city || '',
    district: it.district || '',
    detail: it.detail || '',
    detail2: it.detail2 || '',
    zipcode: it.zipcode || '',
    isDefault: !!it.isDefault
  })
  showForm.value = true
}

const save = () => {
  if (!form.name.trim() || !form.phone.trim() || !form.detail.trim()) {
    ElMessage.warning(t('neo.address.required'))
    return
  }
  addr.upsert({ ...form })
  showForm.value = false
}

const remove = async (id) => {
  await ElMessageBox.confirm(t('neo.address.confirmRemove'), t('common.tip'), {
    type: 'warning',
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel')
  })
  addr.remove(id)
}

const setDefault = (id) => {
  addr.setDefault(id)
}

const formatAddress = (it) => {
  if (!it) return ''
  if (it.country === 'CN') {
    return `${it.province || ''}${it.city || ''}${it.district || ''}${it.detail || ''} ${it.zipcode || ''}`.trim()
  }
  return `${it.detail || ''} ${it.detail2 || ''} ${it.zipcode || ''}`.trim()
}

const handlePick = (it) => {
  const mode = route.query?.pick
  if (mode) {
    checkout.setAddressId(it.id)
    router.back()
  }
}
</script>

