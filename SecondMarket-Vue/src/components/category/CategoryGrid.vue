<template>
  <div class="sm-tap">
    <div
      class="grid"
      :style="{ gridTemplateColumns: `repeat(${columns}, minmax(0, 1fr))`, gap: `${gap}px` }"
    >
      <button
        v-if="showAll"
        type="button"
        class="rounded-2xl border transition active:scale-[0.99]"
        :class="itemClass(modelValue === allValue)"
        @click="select(allValue)"
      >
        <div class="h-11 w-11 rounded-2xl sm-bg-primary-soft sm-text-primary flex items-center justify-center mx-auto">
          <CategoryIcon category-id="others" :size="iconSize" />
        </div>
        <div class="mt-2 text-xs text-gray-800 leading-snug break-words text-center">
          {{ allLabel || $t('common.all') }}
        </div>
      </button>

      <button
        v-for="c in items"
        :key="c.id"
        type="button"
        class="rounded-2xl border transition active:scale-[0.99]"
        :class="itemClass(modelValue === c.id)"
        @click="select(c.id)"
      >
        <div class="h-11 w-11 rounded-2xl sm-bg-primary-soft sm-text-primary flex items-center justify-center mx-auto">
          <CategoryIcon :category-id="c.id" :size="iconSize" />
        </div>
        <div class="mt-2 text-xs text-gray-800 leading-snug break-words text-center">
          {{ labelOf(c.id) }}
        </div>
        <div v-if="showSubtitle" class="mt-1 text-[11px] text-gray-500 text-center">
          {{ subtitleOf(c.id) }}
        </div>
      </button>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
import { CATEGORIES } from '@/utils/categories'
import CategoryIcon from '@/components/icons/CategoryIcon.vue'

const props = defineProps({
  items: {
    type: Array,
    default: () => CATEGORIES
  },
  modelValue: {
    type: String,
    default: ''
  },
  columns: {
    type: Number,
    default: 4
  },
  gap: {
    type: Number,
    default: 12
  },
  iconSize: {
    type: Number,
    default: 22
  },
  showAll: {
    type: Boolean,
    default: false
  },
  allValue: {
    type: String,
    default: ''
  },
  allLabel: {
    type: String,
    default: ''
  },
  showSubtitle: {
    type: Boolean,
    default: false
  },
  subtitleMap: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue', 'select'])

const { t } = useI18n()

const labelOf = (id) => t(`categories.${id}`)
const subtitleOf = (id) => props.subtitleMap[id] || ''

const itemClass = (selected) => {
  return selected
    ? 'border-[color:rgba(6,182,212,0.35)] bg-white sm-shadow-card'
    : 'border-gray-100 bg-gradient-to-b from-white to-gray-50 hover:shadow-sm'
}

const select = (id) => {
  emit('update:modelValue', id)
  emit('select', id)
}
</script>
