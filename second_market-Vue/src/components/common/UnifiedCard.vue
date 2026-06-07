/**
 * 统一卡片组件 - Unified Card Component
 * 提供一致的卡片样式和行为
 */
<template>
  <component
    :is="tag"
    :class="[
      'unified-card',
      `unified-card--${variant}`,
      `unified-card--${size}`,
      {
        'unified-card--clickable': clickable || $attrs.onClick,
        'unified-card--bordered': bordered,
        'unified-card--shadow': shadow,
        'unified-card--hover': hoverEffect
      }
    ]"
    @click="handleClick"
  >
    <!-- 卡片头部 -->
    <div v-if="$slots.header || header" class="unified-card__header">
      <slot name="header">
        <div class="unified-card__header-content">
          <div v-if="icon" class="unified-card__icon" :style="{ background: iconBg }">
            <el-icon :size="iconSize"><component :is="icon" /></el-icon>
          </div>
          <div class="unified-card__header-text">
            <h3 class="unified-card__title">{{ header }}</h3>
            <p v-if="subHeader" class="unified-card__sub-header">{{ subHeader }}</p>
          </div>
        </div>
      </slot>
      <div v-if="$slots['header-extra']" class="unified-card__header-extra">
        <slot name="header-extra" />
      </div>
    </div>

    <!-- 卡片内容 -->
    <div class="unified-card__body" :class="{ 'unified-card__body--no-padding': noPadding }">
      <slot />
    </div>

    <!-- 卡片底部 -->
    <div v-if="$slots.footer" class="unified-card__footer">
      <slot name="footer" />
    </div>
  </component>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  // HTML标签
  tag: {
    type: String,
    default: 'div'
  },
  // 卡片标题
  header: {
    type: String,
    default: ''
  },
  // 副标题
  subHeader: {
    type: String,
    default: ''
  },
  // 图标
  icon: {
    type: [String, Object],
    default: null
  },
  // 图标背景色
  iconBg: {
    type: String,
    default: 'var(--primary-100)'
  },
  // 图标大小
  iconSize: {
    type: Number,
    default: 18
  },
  // 尺寸
  size: {
    type: String,
    default: 'md',
    validator: (v) => ['sm', 'md', 'lg'].includes(v)
  },
  // 变体
  variant: {
    type: String,
    default: 'default',
    validator: (v) => ['default', 'bordered', 'flat', 'gradient'].includes(v)
  },
  // 是否有边框
  bordered: {
    type: Boolean,
    default: false
  },
  // 是否有阴影
  shadow: {
    type: Boolean,
    default: true
  },
  // 是否可点击
  clickable: {
    type: Boolean,
    default: false
  },
  // 悬停效果
  hoverEffect: {
    type: Boolean,
    default: false
  },
  // 内容区域无内边距
  noPadding: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

const handleClick = (e) => {
  if (props.clickable || props.hoverEffect) {
    emit('click', e)
  }
}
</script>

<style scoped>
.unified-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  transition: all var(--duration-normal) var(--ease-default);
  position: relative;
  overflow: hidden;
}

/* 尺寸 */
.unified-card--sm {
  padding: var(--space-3);
  border-radius: var(--radius-md);
}

.unified-card--md {
  padding: var(--space-4);
}

.unified-card--lg {
  padding: var(--space-6);
}

/* 变体 */
.unified-card--default {
  box-shadow: var(--shadow-sm);
}

.unified-card--bordered {
  border: 1px solid var(--border-primary);
  box-shadow: none;
}

.unified-card--flat {
  box-shadow: none;
  background: var(--bg-secondary);
}

.unified-card--gradient {
  background: linear-gradient(135deg, var(--primary-50) 0%, var(--bg-card) 100%);
}

/* 可点击状态 */
.unified-card--clickable {
  cursor: pointer;
}

.unified-card--clickable:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

/* 悬停效果 */
.unified-card--hover:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

/* 头部 */
.unified-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}

.unified-card__header-content {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.unified-card__icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-600);
  flex-shrink: 0;
}

.unified-card--sm .unified-card__icon {
  width: 32px;
  height: 32px;
}

.unified-card--lg .unified-card__icon {
  width: 48px;
  height: 48px;
}

.unified-card__header-text {
  min-width: 0;
}

.unified-card__title {
  font-size: var(--font-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
  line-height: var(--leading-snug);
}

.unified-card--sm .unified-card__title {
  font-size: var(--font-md);
}

.unified-card--lg .unified-card__title {
  font-size: var(--font-xl);
}

.unified-card__sub-header {
  font-size: var(--font-sm);
  color: var(--text-secondary);
  margin: var(--space-1) 0 0 0;
  line-height: var(--leading-normal);
}

.unified-card__header-extra {
  flex-shrink: 0;
}

/* 内容区域 */
.unified-card__body {
  color: var(--text-primary);
}

.unified-card__body--no-padding {
  margin: calc(var(--space-4) * -1);
}

/* 底部 */
.unified-card__footer {
  margin-top: var(--space-4);
  padding-top: var(--space-4);
  border-top: 1px solid var(--border-primary);
}

/* 暗黑模式 */
html.dark .unified-card--default {
  background: var(--bg-card);
}

html.dark .unified-card--flat {
  background: var(--bg-secondary);
}

html.dark .unified-card--gradient {
  background: linear-gradient(135deg, var(--primary-900) 0%, var(--bg-card) 100%);
}
</style>
