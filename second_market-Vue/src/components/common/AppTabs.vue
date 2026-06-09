<template>
  <nav class="app-tabs" role="tablist" :aria-label="label">
    <div class="tabs-container">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        :ref="el => { if (el) tabRefs[tab.value] = el }"
        role="tab"
        :aria-selected="modelValue === tab.value"
        :aria-controls="`tab-panel-${tab.value}`"
        :tabindex="modelValue === tab.value ? 0 : -1"
        class="tab-item"
        :class="{ 'is-active': modelValue === tab.value }"
        @click="handleClick(tab.value)"
        @keydown="handleKeydown"
      >
        <el-icon v-if="tab.icon" class="tab-icon">
          <component :is="tab.icon" />
        </el-icon>
        <span class="tab-label">{{ tab.label }}</span>
        <span v-if="tab.count !== undefined" class="tab-count">{{ tab.count }}</span>
      </button>
      
      <!-- 活动指示器 -->
      <div 
        class="tab-indicator"
        :style="indicatorStyle"
      ></div>
    </div>
  </nav>
  
  <!-- Tab Panels -->
  <div class="tab-panels">
    <slot />
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { useDeviceType } from '@/utils/device'

const props = defineProps({
  modelValue: {
    type: [String, Number],
    required: true
  },
  tabs: {
    type: Array,
    required: true,
    default: () => []
  },
  label: {
    type: String,
    default: '标签页'
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const { isMobile } = useDeviceType()
const tabRefs = ref({})
const indicatorStyle = ref({})

const handleClick = (value) => {
  emit('update:modelValue', value)
  emit('change', value)
  updateIndicator(value)
}

const handleKeydown = (e) => {
  const tabValues = props.tabs.map(t => t.value)
  const currentIndex = tabValues.indexOf(props.modelValue)
  
  if (e.key === 'ArrowRight' && currentIndex < tabValues.length - 1) {
    e.preventDefault()
    handleClick(tabValues[currentIndex + 1])
  } else if (e.key === 'ArrowLeft' && currentIndex > 0) {
    e.preventDefault()
    handleClick(tabValues[currentIndex - 1])
  } else if (e.key === 'Home') {
    e.preventDefault()
    handleClick(tabValues[0])
  } else if (e.key === 'End') {
    e.preventDefault()
    handleClick(tabValues[tabValues.length - 1])
  }
}

const updateIndicator = async (activeValue) => {
  await nextTick()
  const activeTab = tabRefs.value[activeValue]
  if (activeTab) {
    indicatorStyle.value = {
      width: `${activeTab.offsetWidth}px`,
      transform: `translateX(${activeTab.offsetLeft}px)`
    }
  }
}

watch(() => props.modelValue, (newValue) => {
  updateIndicator(newValue)
}, { immediate: true })
</script>

<style scoped>
.app-tabs {
  width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}

.app-tabs::-webkit-scrollbar {
  display: none;
}

.tabs-container {
  position: relative;
  display: flex;
  gap: var(--space-1);
  padding: var(--space-1);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  min-width: max-content;
}

.tab-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  background: transparent;
  border: none;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: var(--font-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-default);
  white-space: nowrap;
}

.tab-item:hover {
  color: var(--text-primary);
  background: var(--bg-card);
}

.tab-item.is-active {
  color: var(--primary-500);
  background: var(--bg-card);
  box-shadow: var(--shadow-sm);
}

.tab-icon {
  font-size: var(--font-md);
}

.tab-label {
  line-height: 1;
}

.tab-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 var(--space-1);
  background: var(--neutral-200);
  border-radius: var(--radius-full);
  font-size: var(--font-xs);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
}

.tab-item.is-active .tab-count {
  background: var(--primary-100);
  color: var(--primary-600);
}

.tab-indicator {
  display: none;
}

.tab-panels {
  margin-top: var(--space-4);
}

/* 移动端样式 */
@media (max-width: 768px) {
  .tabs-container {
    gap: var(--space-2);
  }
  
  .tab-item {
    padding: var(--space-2) var(--space-3);
  }
}
</style>
