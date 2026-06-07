# 微交互动画指南

微交互是指用户与应用进行交互时产生的细微动画效果，能够提升用户体验，增加界面的生动性和反馈感。

## 为什么微交互重要？

- 🎯 **即时反馈** - 告诉用户操作已被接收
- ✨ **增强体验** - 让界面更加生动有趣
- 📱 **引导操作** - 通过动画引导用户行为
- 💫 **减少认知负担** - 清晰的视觉反馈减少疑惑

## 设计原则

### 1. 快速响应

微交互应该快速，不应延迟用户操作：

```css
/* 推荐：150-300ms */
transition-duration: var(--duration-fast); /* 150ms */

/* 避免：过长的动画 */
transition-duration: 2s;
```

### 2. 自然流畅

使用合理的缓动函数，模拟物理世界的感觉：

```css
/* 标准缓动 - 推荐 */
transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);

/* 弹性效果 - 用于特殊强调 */
transition-timing-function: cubic-bezier(0.175, 0.885, 0.32, 1.275);
```

### 3. 保持一致

整个应用使用统一的动画风格：

```css
/* 在 design-tokens.css 中定义 */
:root {
  --duration-fast: 150ms;
  --duration-normal: 300ms;
  --ease-default: cubic-bezier(0.4, 0, 0.2, 1);
}
```

### 4. 尊重用户偏好

支持减少动画偏好：

```javascript
import { prefersReducedMotion } from '@/composables/useAccessibility'

// 检测用户偏好
if (prefersReducedMotion()) {
  // 禁用或简化动画
}
```

## 常用微交互动画

### 1. 按钮交互

```vue
<template>
  <button class="btn-primary" @click="handleClick">
    点击我
  </button>
</template>

<style scoped>
.btn-primary {
  padding: var(--space-3) var(--space-6);
  background: var(--primary-500);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-weight: var(--font-medium);
  cursor: pointer;
  
  /* 微交互：悬停效果 */
  transition: all var(--duration-fast) var(--ease-default);
}

.btn-primary:hover {
  background: var(--primary-600);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn-primary:active {
  transform: translateY(0);
  box-shadow: var(--shadow-sm);
}

/* 涟漪效果 */
.btn-primary {
  position: relative;
  overflow: hidden;
}
</style>
```

### 2. 卡片悬停

```vue
<template>
  <div class="product-card hover-lift">
    <!-- 卡片内容 -->
  </div>
</template>

<style scoped>
.product-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  
  /* 悬停动画 */
  transition: transform var(--duration-normal) var(--ease-spring),
              box-shadow var(--duration-normal) var(--ease-default);
}

.product-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: var(--shadow-xl);
}
</style>
```

### 3. 点赞动画

```vue
<template>
  <button 
    class="like-btn"
    :class="{ 'is-liked': isLiked }"
    @click="toggleLike"
  >
    <span class="like-icon">❤️</span>
    <span class="like-count">{{ likeCount }}</span>
  </button>
</template>

<style scoped>
.like-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: transparent;
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-default);
}

.like-btn:hover {
  border-color: var(--error-300);
}

.like-icon {
  font-size: 18px;
  transition: transform var(--duration-fast) var(--ease-spring);
}

.like-btn.is-liked .like-icon {
  animation: like-pop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes like-pop {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.3);
  }
  100% {
    transform: scale(1);
  }
}

.like-count {
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}
</style>
```

### 4. 输入框聚焦

```vue
<template>
  <div class="input-wrapper">
    <input 
      class="input-field"
      :class="{ 'is-focused': isFocused }"
      @focus="isFocused = true"
      @blur="isFocused = false"
    />
    <span class="input-label">邮箱</span>
  </div>
</template>

<style scoped>
.input-wrapper {
  position: relative;
}

.input-field {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-size: var(--font-md);
  transition: border-color var(--duration-fast) var(--ease-default),
              box-shadow var(--duration-fast) var(--ease-default);
}

.input-field:focus {
  outline: none;
  border-color: var(--primary-500);
  box-shadow: 0 0 0 3px var(--primary-100);
}

.input-label {
  position: absolute;
  left: var(--space-4);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-secondary);
  transition: all var(--duration-fast) var(--ease-default);
  pointer-events: none;
}

.input-field:focus + .input-label,
.input-field:not(:placeholder-shown) + .input-label {
  top: 0;
  transform: translateY(-50%);
  font-size: var(--font-sm);
  color: var(--primary-500);
  background: white;
  padding: 0 var(--space-2);
}
</style>
```

### 5. 数字滚动

```vue
<template>
  <div ref="numberEl" class="stat-number">0</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { animateNumber } from '@/composables/useAnimations'

const numberEl = ref(null)

onMounted(() => {
  animateNumber(numberEl.value, 0, 1234, {
    duration: 1500,
    format: (num) => Math.round(num).toLocaleString()
  })
})
</script>
```

### 6. 列表进入动画

```vue
<template>
  <div ref="listEl" class="product-list">
    <div v-for="product in products" :key="product.id" class="product-item">
      {{ product.title }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { animateList } from '@/composables/useAnimations'

const listEl = ref(null)
const products = ref([])

onMounted(() => {
  // 初始加载
  products.value = getProducts()
  
  // 添加动画
  setTimeout(() => {
    animateList(listEl.value, {
      stagger: 100,
      animation: 'slideUp',
      duration: 400
    })
  }, 100)
})
</script>

<style scoped>
.product-item {
  padding: var(--space-4);
  margin-bottom: var(--space-3);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
}
</style>
```

### 7. 页面过渡

```vue
<template>
  <router-view v-slot="{ Component, route }">
    <transition :name="transitionName" mode="out-in">
      <component :is="Component" :key="route.path" />
    </transition>
  </router-view>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const transitionName = computed(() => route.meta.transition || 'fade')
</script>

<style>
/* 淡入淡出 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--duration-normal) var(--ease-default);
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 滑动 */
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all var(--duration-normal) var(--ease-default);
}
.slide-left-enter-from {
  opacity: 0;
  transform: translateX(30px);
}
.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style>
```

### 8. 涟漪点击效果

```vue
<template>
  <button v-ripple class="ripple-btn">
    点击我
  </button>
</template>

<script setup>
import { vRipple } from '@/composables/useAnimations'
</script>

<style scoped>
.ripple-btn {
  position: relative;
  overflow: hidden;
  padding: var(--space-4) var(--space-8);
  background: var(--primary-500);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
}

:global(.ripple-effect) {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: scale(0);
  animation: ripple-effect 0.6s linear;
  pointer-events: none;
}

@keyframes ripple-effect {
  to {
    transform: scale(2.5);
    opacity: 0;
  }
}
</style>
```

## 使用工具函数

### 创建涟漪效果

```javascript
import { createRipple } from '@/composables/useAnimations'

const handleClick = (event) => {
  createRipple(event, event.currentTarget, {
    color: 'var(--primary-200)',
    duration: 600,
    scale: 2.5
  })
}
```

### 数字动画

```javascript
import { animateNumber } from '@/composables/useAnimations'

animateNumber(element, 0, 1000, {
  duration: 2000,
  easing: 'cubic-bezier(0.4, 0, 0.2, 1)',
  format: (num) => `¥${num.toFixed(2)}`,
  onUpdate: (current) => console.log('Updating:', current),
  onComplete: () => console.log('Animation complete')
})
```

## 最佳实践

### 1. 控制动画数量

避免同一时间有过多动画：

```javascript
// 不好：多个元素同时动画
elements.forEach(el => animate(el))

// 好：错开动画时间
elements.forEach((el, i) => {
  setTimeout(() => animate(el), i * 50)
})
```

### 2. 性能优化

使用 transform 和 opacity 动画：

```css
/* 推荐：使用 transform 和 opacity */
.element {
  transition: transform 0.3s, opacity 0.3s;
}

/* 避免：动画性能差的属性 */
.element {
  transition: width 0.3s, height 0.3s, margin 0.3s;
}
```

### 3. 键盘可访问

确保动画不干扰键盘导航：

```css
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
```

## 常见场景

### 加载状态

- 使用脉冲动画表示加载中
- 避免阻塞用户操作

### 成功反馈

- 使用绿色 + 动画表示成功
- 短暂显示后自动消失

### 错误提示

- 使用红色 + 震动动画表示错误
- 提供清晰的错误信息

### 空状态

- 使用友好的插图和文字
- 提供明确的操作指引

## 总结

微交互是提升用户体验的重要手段，但要注意：

1. ✅ 保持动画快速（150-300ms）
2. ✅ 使用合理的缓动函数
3. ✅ 保持应用内一致性
4. ✅ 尊重用户偏好
5. ✅ 优先使用 GPU 加速属性
6. ✅ 避免过度使用动画
