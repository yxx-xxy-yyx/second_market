<template>
  <div class="rounded-2xl bg-white border border-gray-100 shadow-sm">
    <div class="px-5 py-4 flex items-center justify-between">
      <div class="text-lg font-semibold text-gray-900">{{ $t('nav.categories') }}</div>
      <el-button type="primary" plain @click="router.push('/user/products')">
        {{ $t('nav.products') }}
      </el-button>
    </div>

    <div class="px-5 pb-6">
      <div class="hidden lg:block">
        <CategoryGrid :items="categories" :columns="4" :gap="16" :icon-size="22" @select="goCategory" />
      </div>
      <div class="hidden md:block lg:hidden">
        <CategoryGrid :items="categories" :columns="3" :gap="16" :icon-size="22" @select="goCategory" />
      </div>
      <div class="md:hidden">
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
