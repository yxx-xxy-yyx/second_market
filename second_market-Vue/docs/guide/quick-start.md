# 快速开始

本文将帮助你快速上手使用 EchoOfMemories UI 组件库。

## 环境要求

- Node.js >= 18.0.0
- Vue >= 3.3.0
- npm / yarn / pnpm

## 📦 安装

### 使用 npm

```bash
npm install @echoofmemories/ui
```

### 使用 yarn

```bash
yarn add @echoofmemories/ui
```

### 使用 pnpm

```bash
pnpm add @echoofmemories/ui
```

## 🚀 快速使用

### 1. 引入样式

在你的入口文件中引入样式文件：

```javascript
import '@echoofmemories/ui/dist/style.css'
```

### 2. 使用组件

```vue
<template>
  <div>
    <UnifiedCard title="产品卡片" :hover-effect="true">
      <p>这是一个统一卡片组件</p>
    </UnifiedCard>

    <ProductCard :product="productData" @click="handleClick" />
  </div>
</template>

<script setup>
import { UnifiedCard, ProductCard } from '@echoofmemories/ui'

const productData = {
  title: 'MacBook Pro 2023',
  price: 12999,
  image: 'https://example.com/image.jpg'
}

const handleClick = (product) => {
  console.log('产品被点击:', product)
}
</script>
```

### 3. 使用设计令牌

你可以在任何地方使用设计系统中的 CSS 变量：

```css
.my-component {
  color: var(--primary-500);
  background: var(--bg-card);
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
}
```

## 📱 响应式布局

使用 ResponsiveLayout 组件实现设备自适应：

```vue
<template>
  <ResponsiveLayout>
    <template #desktop>
      <DesktopView />
    </template>
    <template #tablet>
      <TabletView />
    </template>
    <template #mobile>
      <MobileView />
    </template>
  </ResponsiveLayout>
</template>

<script setup>
import { ResponsiveLayout } from '@echoofmemories/ui'
</script>
```

## 🎨 主题定制

### 暗黑模式

```javascript
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()

// 切换到暗黑模式
themeStore.toggleTheme()
```

### 自定义主题色

在 CSS 中覆盖 CSS 变量：

```css
:root {
  --primary-500: #1890ff;
}
```

## 🛠️ 开发模式

如果你想参与组件库的开发或进行本地调试：

```bash
# 克隆仓库
git clone https://github.com/echoofmemories/second-market.git

# 安装依赖
npm install

# 启动文档
npm run docs:dev

# 启动示例项目
npm run dev
```

## 📖 下一步

- 查看 [组件文档](/components/)
- 了解 [设计系统](/design/)
- 阅读 [最佳实践](/guide/best-practices)

## ❓ 获取帮助

如果你在使用过程中遇到问题：

1. 查看 [常见问题 FAQ](/guide/faq)
2. 提交 [GitHub Issue](https://github.com/echoofmemories/second-market/issues)
3. 加入我们的社区讨论
