# 无障碍设计指南

本文档介绍如何为 EchoOfMemories UI 应用添加无障碍支持。

## 为什么无障碍很重要？

- **法律要求**：许多国家/地区有相关法规要求数字产品必须具备无障碍功能
- **用户群体**：全球约 15% 的人口有某种形式的残疾
- **用户体验**：无障碍设计对所有人都有好处（情境限制、临时障碍等）
- **SEO 优化**：搜索引擎可以更好地理解页面内容

## 核心原则

我们遵循 **WCAG 2.1** 的四项原则（POUR）：

1. **可感知性 (Perceivable)** - 信息必须是可以被感知到的
2. **可操作性 (Operable)** - 界面必须是可操作的
3. **可理解性 (Understandable)** - 信息和界面操作必须是可理解的
4. **健壮性 (Robust)** - 内容必须足够健壮，能被各种用户代理可靠地解析

## 可感知性

### 1. 替代文本

所有图像都应该有替代文本：

```vue
<!-- 好的做法 -->
<img 
  src="/product.jpg" 
  alt="MacBook Pro 2023 14寸笔记本电脑" 
/>

<!-- 装饰性图像 -->
<img 
  src="/decoration.svg" 
  alt="" 
  aria-hidden="true" 
/>
```

### 2. 颜色对比度

确保文本与背景有足够的对比度：

```css
/* 满足 WCAG AA 标准 */
.text-primary {
  color: var(--primary-700); /* 对比度 >= 4.5:1 */
}

/* 大文本（18px+ 或粗体14px+）可以稍低 */
.heading-large {
  color: var(--primary-500); /* 对比度 >= 3:1 */
}
```

### 3. 音频和视频

提供字幕或文本替代：

```vue
<video controls>
  <track kind="captions" src="/captions.vtt" />
</video>
```

## 可操作性

### 1. 键盘可访问

所有交互元素都应该可以通过键盘操作：

```vue
<!-- 使用语义化元素 -->
<button @click="handleSubmit">提交</button>

<!-- 如果必须使用 div -->
<div 
  role="button" 
  tabindex="0" 
  @click="handleClick" 
  @keydown.enter="handleClick"
  @keydown.space="handleClick"
>
  点击我
</div>
```

### 2. 焦点管理

确保焦点可见且逻辑清晰：

```vue
<!-- 焦点样式 -->
:focus-visible {
  outline: 2px solid var(--primary-500);
  outline-offset: 2px;
}

/* 移除默认焦点环（可选） */
:focus:not(:focus-visible) {
  outline: none;
}
```

### 3. 跳过导航

为重复的导航提供跳过链接：

```vue
<a href="#main-content" class="skip-link">
  跳转到主要内容
</a>

<nav aria-label="主导航">
  <!-- 导航内容 -->
</nav>

<main id="main-content">
  <!-- 主要内容 -->
</main>
```

### 4. 焦点陷阱

模态框和对话框应该锁定焦点：

```javascript
import { focusManagement } from '@/composables/useAccessibility'

const Modal = {
  setup() {
    const modalRef = ref(null)
    let cleanupFn = null
    
    const openModal = () => {
      // 将焦点移到模态框
      cleanupFn = focusManagement.trapFocus(modalRef.value)
      focusManagement.focusFirst(modalRef.value)
    }
    
    const closeModal = () => {
      cleanupFn?.()
      // 将焦点返回到触发元素
    }
    
    return { modalRef }
  }
}
```

## 可理解性

### 1. 表单标签

所有表单输入都应该有关联的标签：

```vue
<!-- 推荐：明确关联 -->
<label for="email">邮箱地址</label>
<input id="email" type="email" v-model="email" />

<!-- 错误：仅依赖占位符 -->
<input type="email" placeholder="请输入邮箱" />
```

### 2. 错误消息

表单验证错误应该清晰明确：

```vue
<template>
  <div>
    <label for="username">用户名</label>
    <input 
      id="username"
      type="text"
      v-model="username"
      :aria-invalid="!!errors.username"
      :aria-describedby="errors.username ? 'username-error' : undefined"
    />
    <span 
      v-if="errors.username" 
      id="username-error" 
      role="alert"
    >
      {{ errors.username }}
    </span>
  </div>
</template>
```

### 3. 一致性

保持导航和交互的一致性：

```vue
<!-- 导航结构一致 -->
<nav aria-label="主导航">
  <ul>
    <li><a href="/" aria-current="page">首页</a></li>
    <li><a href="/products">商品</a></li>
  </ul>
</nav>
```

## 健壮性

### 1. 语义化 HTML

使用正确的 HTML 元素：

```vue
<!-- 好的做法 -->
<article>
  <header>
    <h1>文章标题</h1>
  </header>
  <main>
    <section>
      <h2>章节标题</h2>
      <p>内容...</p>
    </section>
  </main>
  <footer>
    <nav aria-label="文章导航">
      <!-- 相关文章链接 -->
    </nav>
  </footer>
</article>

<!-- 避免使用无意义的 div -->
<div class="button">提交</div>
```

### 2. ARIA 正确使用

ARIA 应该是最后手段，优先使用语义化 HTML：

```vue
<!-- 优先使用语义化元素 -->
<nav>主导航</nav>

<!-- ARIA 作为补充 -->
<div role="navigation" aria-label="主导航">主导航</div>
```

### 3. 动态内容

动态更新内容应该通知屏幕阅读器：

```vue
<template>
  <div aria-live="polite" aria-atomic="true">
    <p v-if="message">{{ message }}</p>
  </div>
</template>

<script>
const message = ref('')

const showMessage = (text) => {
  message.value = text
  // 屏幕阅读器会自动读取
}
</script>
```

## 实用技巧

### 1. 使用我们的工具函数

```vue
<script setup>
import { 
  generateId, 
  keyboardNavigation,
  focusManagement 
} from '@/composables/useAccessibility'

const inputId = generateId('input')
const handleKeydown = keyboardNavigation.onEnter(() => {
  submit()
})
</script>
```

### 2. 减少动画偏好

尊重用户的减少动画偏好：

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

```javascript
import { prefersReducedMotion } from '@/composables/useAccessibility'

if (prefersReducedMotion()) {
  // 禁用动画
}
```

### 3. 移动端触摸目标

确保触摸目标足够大：

```css
/* 最小触摸目标 44x44px */
.touch-target {
  min-width: 44px;
  min-height: 44px;
  padding: 12px;
}
```

## 测试工具

- [WAVE](https://wave.webaim.org/) - 网页无障碍评估工具
- [axe DevTools](https://www.deque.com/axe/devtools/) - 浏览器扩展
- [NVDA](https://www.nvaccess.org/) - Windows 屏幕阅读器
- [VoiceOver](https://www.apple.com/cn/accessibility/mac/vision/) - macOS/iOS 屏幕阅读器
- [Accessibility Insights](https://accessibilityinsights.io/) - 微软无障碍检查工具

## 检查清单

- [ ] 所有图像都有替代文本
- [ ] 表单有关联的标签
- [ ] 颜色对比度符合 WCAG AA 标准
- [ ] 所有交互元素可通过键盘操作
- [ ] 焦点状态清晰可见
- [ ] 错误消息清晰且有帮助
- [ ] 动态内容有适当的 ARIA 通知
- [ ] 页面结构使用语义化 HTML
- [ ] 模态框锁定焦点
- [ ] 支持减少动画偏好
