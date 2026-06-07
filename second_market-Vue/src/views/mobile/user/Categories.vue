<template>
  <div class="min-h-[calc(100dvh-140px)]">
    <div class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden">
      <div class="px-5 py-4">
        <div class="text-lg font-semibold text-gray-900">{{ $t('nav.categories') }}</div>
        <div class="text-sm text-gray-500 mt-1">{{ $t('common.mall') }}</div>
      </div>

      <div class="px-5 pb-6">
        <CategoryGrid :items="categories" :columns="2" :gap="12" :icon-size="22" @select="goCategory" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { CATEGORIES } from '@/utils/categories'
import CategoryGrid from '@/components/category/CategoryGrid.vue'

const router = useRouter()
const categories = CATEGORIES

const goCategory = (categoryId) => {
  router.push({
    path: '/user/products',
    query: {
      category: categoryId
    }
  })
}
</script>
