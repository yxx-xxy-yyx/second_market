<template>
  <div class="table-skeleton">
    <div class="skeleton-header">
      <div 
        v-for="i in columnCount" 
        :key="i"
        class="skeleton-cell header-cell"
        :style="{ width: getColumnWidth(i) }"
      ></div>
    </div>
    <div 
      v-for="i in rowCount" 
      :key="i"
      class="skeleton-row"
      :class="{ 'animate': animated }"
      role="status"
      :aria-label="animated ? `正在加载第${i}行数据` : undefined"
    >
      <div 
        v-for="j in columnCount" 
        :key="j"
        class="skeleton-cell"
        :style="{ width: getColumnWidth(j) }"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  columnCount: {
    type: Number,
    default: 4
  },
  rowCount: {
    type: Number,
    default: 5
  },
  animated: {
    type: Boolean,
    default: true
  }
})

const columnWidths = computed(() => {
  // 自动生成合理的列宽
  const widths = []
  for (let i = 0; i < props.columnCount; i++) {
    if (i === 0) {
      widths.push('20%')
    } else if (i === props.columnCount - 1) {
      widths.push('10%')
    } else {
      widths.push(`${Math.floor(Math.random() * 20 + 15)}%`)
    }
  }
  return widths
})

const getColumnWidth = (index) => {
  return columnWidths.value[index - 1] || '15%'
}
</script>

<style scoped>
.table-skeleton {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.skeleton-header {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-4);
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-primary);
}

.skeleton-row {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-4);
  border-bottom: 1px solid var(--border-tertiary);
}

.skeleton-row:last-child {
  border-bottom: none;
}

.skeleton-cell {
  height: 16px;
  background: var(--skeleton-base);
  border-radius: var(--radius-sm);
}

.skeleton-header .skeleton-cell {
  height: 18px;
  background: var(--skeleton-highlight);
}

/* 动画 */
.animate .skeleton-cell {
  background: linear-gradient(
    90deg,
    var(--skeleton-base) 0%,
    var(--skeleton-highlight) 50%,
    var(--skeleton-base) 100%
  );
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s ease-in-out infinite;
}

.animate .skeleton-header .skeleton-cell {
  animation-delay: 0.1s;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>
