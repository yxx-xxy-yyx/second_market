<template>
  <div class="stat-card">
    <div class="stat-header">
      <div class="stat-icon-wrapper" :style="{ background: iconBg }">
        <el-icon :size="24" :style="{ color: iconColor }">
          <component :is="icon" />
        </el-icon>
      </div>
      <el-tag v-if="trend" :type="trendType" size="small" class="stat-trend">
        <el-icon v-if="trendValue">{{ trendValue > 0 ? '+' : '' }}{{ trendValue }}%</el-icon>
      </el-tag>
    </div>
    
    <div class="stat-content">
      <div class="stat-value" :style="{ color: valueColor }">
        <span v-if="prefix" class="stat-prefix">{{ prefix }}</span>
        <span ref="valueEl">{{ displayValue }}</span>
        <span v-if="suffix" class="stat-suffix">{{ suffix }}</span>
      </div>
      <div class="stat-label">{{ label }}</div>
    </div>
    
    <div v-if="chartData?.length" class="stat-chart">
      <div 
        class="chart-line"
        :style="{ 
          background: `linear-gradient(to right, ${valueColor}33, ${valueColor})`,
          height: `${Math.min(100, maxChartValue)}%` 
        }"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'

const props = defineProps({
  value: {
    type: [Number, String],
    required: true
  },
  label: {
    type: String,
    required: true
  },
  icon: {
    type: [String, Object],
    default: null
  },
  iconBg: {
    type: String,
    default: 'var(--primary-100)'
  },
  iconColor: {
    type: String,
    default: 'var(--primary-600)'
  },
  valueColor: {
    type: String,
    default: 'var(--text-primary)'
  },
  prefix: {
    type: String,
    default: ''
  },
  suffix: {
    type: String,
    default: ''
  },
  trend: {
    type: Number,
    default: null
  },
  trendValue: {
    type: Number,
    default: null
  },
  animate: {
    type: Boolean,
    default: true
  },
  chartData: {
    type: Array,
    default: () => []
  }
})

const displayValue = ref(props.animate ? 0 : props.value)
const valueEl = ref(null)

const trendType = computed(() => {
  if (!props.trend) return 'info'
  return props.trend > 0 ? 'success' : 'danger'
})

const maxChartValue = computed(() => {
  if (!props.chartData?.length) return 0
  return Math.max(...props.chartData) * 1.2
})

const animateValue = (from, to, duration = 1500) => {
  const startTime = performance.now()
  const diff = typeof to === 'number' ? to - from : 0
  
  const tick = (currentTime) => {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    
    // 使用缓动函数
    const eased = 1 - Math.pow(1 - progress, 3)
    
    if (typeof to === 'number') {
      displayValue.value = Math.round(from + diff * eased)
    } else {
      displayValue.value = to
    }
    
    if (progress < 1) {
      requestAnimationFrame(tick)
    }
  }
  
  requestAnimationFrame(tick)
}

onMounted(() => {
  if (props.animate && typeof props.value === 'number') {
    setTimeout(() => {
      animateValue(0, props.value)
    }, 100)
  } else {
    displayValue.value = props.value
  }
})

watch(() => props.value, (newValue) => {
  if (props.animate && typeof newValue === 'number') {
    animateValue(displayValue.value, newValue)
  } else {
    displayValue.value = newValue
  }
})
</script>

<style scoped>
.stat-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  box-shadow: var(--shadow-sm);
  transition: all var(--duration-normal) var(--ease-default);
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.stat-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}

.stat-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
}

.stat-trend {
  font-size: var(--font-xs);
}

.stat-content {
  margin-bottom: var(--space-3);
}

.stat-value {
  display: flex;
  align-items: baseline;
  gap: var(--space-1);
  font-size: var(--font-3xl);
  font-weight: var(--font-bold);
  line-height: 1.2;
  margin-bottom: var(--space-1);
}

.stat-prefix {
  font-size: var(--font-lg);
  font-weight: var(--font-medium);
}

.stat-suffix {
  font-size: var(--font-md);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

.stat-label {
  font-size: var(--font-sm);
  color: var(--text-secondary);
}

.stat-chart {
  height: 40px;
  display: flex;
  align-items: flex-end;
}

.chart-line {
  width: 100%;
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
  min-height: 4px;
}
</style>
