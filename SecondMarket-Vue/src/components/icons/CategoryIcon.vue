<template>
  <component
    :is="resolved"
    :style="{ fontSize: typeof size === 'number' ? `${size}px` : size }"
  />
</template>

<script setup>
import { computed } from 'vue'
import { CATEGORY_ICONS } from '@/components/icons/categories'

const props = defineProps({
  categoryId: {
    type: String,
    default: ''
  },
  size: {
    type: [Number, String],
    default: 22
  }
})

const resolved = computed(() => CATEGORY_ICONS[props.categoryId] || CATEGORY_ICONS.others)
</script>
