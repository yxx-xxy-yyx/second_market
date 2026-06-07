# 色彩系统

EchoOfMemories UI 提供了一套完整的色彩系统，包含主色、辅助色、功能色等多个维度。

## 主色 - 青色系

主色用于强调关键操作和品牌标识。

::: demo 主色色阶

```vue
<template>
  <div class="color-row">
    <div
      v-for="shade in shades"
      :key="shade"
      class="color-block"
      :style="{ background: `var(--primary-${shade})` }"
    >
      <span>{{ shade }}</span>
    </div>
  </div>
</template>

<script setup>
const shades = ['50', '100', '200', '300', '400', '500', '600', '700', '800', '900', '950']
</script>

<style scoped>
.color-row {
  display: flex;
  gap: 4px;
}
.color-block {
  flex: 1;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-size: 12px;
  color: white;
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
}
</style>
```

:::

## 辅助色

辅助色用于次要信息和辅助功能。

### 蓝色系 (Secondary)

```css
--secondary-500: #3b6391;  /* 默认辅助色 */
```

### 紫色系 (Accent)

```css
--accent-500: #cf33ff;     /* 强调色 */
```

## 功能色

功能色用于传达特定含义。

::: demo 功能色

```vue
<template>
  <div class="functional-colors">
    <div class="color-item success">
      <span class="label">Success</span>
      <span class="value">#22c55e</span>
    </div>
    <div class="color-item warning">
      <span class="label">Warning</span>
      <span class="value">#f59e0b</span>
    </div>
    <div class="color-item error">
      <span class="label">Error</span>
      <span class="value">#ef4444</span>
    </div>
    <div class="color-item info">
      <span class="label">Info</span>
      <span class="value">#64748b</span>
    </div>
  </div>
</template>
```

:::

## 中性色

中性色用于文字、边框、背景等。

```css
--neutral-50:  #f8fafc;   /* 最浅背景 */
--neutral-100: #f1f5f9;   /* 浅背景 */
--neutral-200: #e2e8f0;   /* 边框 */
--neutral-300: #cbd5e1;   /* 禁用状态 */
--neutral-400: #94a3b8;   /* 占位符 */
--neutral-500: #64748b;   /* 次要文字 */
--neutral-600: #475569;   /* 深次要文字 */
--neutral-700: #334155;   /* 主要文字 */
--neutral-800: #1e293b;   /* 深色背景 */
--neutral-900: #0f172a;   /* 最深文字 */
--neutral-950: #020617;   /* 纯深色背景 */
```

## 使用方法

### 在 CSS 中使用

```css
.my-component {
  color: var(--primary-500);
  background-color: var(--primary-50);
  border-color: var(--primary-200);
}
```

### 在类名中使用

```vue
<div class="bg-primary-500 text-white">
  蓝色背景
</div>
```

### 语义化颜色

我们还提供语义化的颜色别名：

```css
color: var(--primary);           /* 主色 */
color: var(--primary-hover);     /* 主色悬停 */
color: var(--success);           /* 成功色 */
color: var(--warning);           /* 警告色 */
color: var(--error);             /* 错误色 */
color: var(--text-primary);      /* 主要文字 */
color: var(--text-secondary);    /* 次要文字 */
color: var(--bg-card);           /* 卡片背景 */
```

## 暗黑模式

暗黑模式下，颜色会自动调整：

```css
html.dark {
  --primary: var(--primary-400);
  --bg-card: var(--neutral-900);
  --text-primary: var(--neutral-50);
}
```

## 无障碍建议

确保文字与背景的颜色对比度符合 WCAG 标准：

- 普通文字：至少 4.5:1
- 大字（18px+ 或粗体14px+）：至少 3:1
- UI组件和图形：至少 3:1

```css
/* ✅ 推荐：对比度足够 */
color: var(--primary-700);
background: var(--neutral-50);

/* ⚠️ 注意：对比度可能不足 */
color: var(--primary-300);
background: white;
```

## 工具函数

```javascript
// 获取颜色变量
const color = getComputedStyle(document.documentElement)
  .getPropertyValue('--primary-500')
  .trim()

// 动态设置主题色
document.documentElement.style.setProperty('--primary-500', '#1890ff')
```
