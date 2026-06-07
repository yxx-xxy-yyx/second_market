# 骨架屏加载组件

骨架屏是一种改善加载体验的技术，在内容加载时显示占位符，提供即时的视觉反馈。

## 为什么使用骨架屏？

- ⚡ **即时反馈** - 减少用户等待的感知时间
- 🎨 **视觉一致** - 保持页面结构稳定，避免布局跳动
- 🌍 **更好的体验** - 告诉用户"内容正在加载中"
- ♿ **无障碍友好** - 屏幕阅读器可以识别加载状态

## 组件列表

### ProductCardSkeleton 产品卡片骨架屏

用于商品列表的加载状态。

```vue
<template>
  <div class="product-grid">
    <ProductCardSkeleton 
      v-for="i in 6" 
      :key="i" 
      :animated="true" 
    />
  </div>
</template>

<script setup>
import { ProductCardSkeleton } from '@/components'
</script>
```

**Props:**

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| animated | 是否显示动画 | `boolean` | `true` |

### ListSkeleton 列表骨架屏

用于列表项的加载状态。

```vue
<template>
  <ListSkeleton 
    :count="5"
    :show-avatar="true"
    :show-action="false"
    :animated="true"
  />
</template>

<script setup>
import { ListSkeleton } from '@/components'
</script>
```

**Props:**

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| count | 显示的骨架项数量 | `number` | `3` |
| animated | 是否显示动画 | `boolean` | `true` |
| showAvatar | 是否显示头像占位 | `boolean` | `true` |
| showAction | 是否显示操作按钮占位 | `boolean` | `false` |

### TableSkeleton 表格骨架屏

用于表格的加载状态。

```vue
<template>
  <TableSkeleton 
    :column-count="4"
    :row-count="5"
    :animated="true"
  />
</template>

<script setup>
import { TableSkeleton } from '@/components'
</script>
```

**Props:**

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| columnCount | 列数 | `number` | `4` |
| rowCount | 行数 | `number` | `5` |
| animated | 是否显示动画 | `boolean` | `true` |

## 使用示例

### 组合使用

```vue
<template>
  <div class="product-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="product-grid">
      <ProductCardSkeleton 
        v-for="i in 6" 
        :key="i" 
        :animated="true"
      />
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      <el-empty description="加载失败">
        <el-button type="primary" @click="reload">重试</el-button>
      </el-empty>
    </div>

    <!-- 数据展示 -->
    <div v-else class="product-grid">
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ProductCard, ProductCardSkeleton } from '@/components'
import { useAsyncSkeleton } from '@/composables/useSkeleton'

const fetchProducts = async () => {
  const response = await fetch('/api/products')
  return response.json()
}

const { data: products, isLoading: loading, error, fetch, reload } = useAsyncSkeleton(fetchProducts)

// 初始加载
fetch()
</script>
```

### 响应式骨架屏

```vue
<template>
  <div class="grid" :class="{ 'is-mobile': isMobile }">
    <template v-if="loading">
      <ProductCardSkeleton 
        v-for="i in skeletonCount" 
        :key="i"
      />
    </template>
    <template v-else>
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
      />
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ProductCard, ProductCardSkeleton } from '@/components'
import { useDeviceType } from '@/composables/useDevice'

const { isMobile } = useDeviceType()

const skeletonCount = computed(() => isMobile.value ? 4 : 6)
</script>
```

### 下拉刷新骨架屏

```vue
<template>
  <div class="content">
    <el-pull-refresh 
      v-model="refreshing"
      @refresh="onRefresh"
    >
      <div v-if="loading && !refreshing" class="skeleton-grid">
        <ProductCardSkeleton v-for="i in 6" :key="i" />
      </div>
      
      <div v-else class="product-grid">
        <ProductCard
          v-for="product in products"
          :key="product.id"
          :product="product"
        />
      </div>
    </el-pull-refresh>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ProductCard, ProductCardSkeleton } from '@/components'

const refreshing = ref(false)
const loading = ref(true)
const products = ref([])

const onRefresh = async () => {
  await fetchProducts()
  refreshing.value = false
}
</script>
```

## CSS 变量

你可以通过 CSS 变量自定义骨架屏的颜色：

```css
:root {
  /* 骨架屏基础颜色 */
  --skeleton-base: #f0f0f0;
  
  /* 骨架屏高亮颜色 */
  --skeleton-highlight: #e0e0e0;
}

/* 暗黑模式 */
html.dark {
  --skeleton-base: #2a2a2a;
  --skeleton-highlight: #3a3a3a;
}
```

## 最佳实践

### 1. 匹配真实布局

骨架屏应该尽可能接近真实内容的布局：

```vue
<!-- 不好的示例：骨架与真实内容差距太大 -->
<ProductCardSkeleton /> <!-- 固定布局 -->
<ProductCard :product="largeProduct" /> <!-- 变化布局 -->

<!-- 好的示例：保持一致 -->
<ProductCardSkeleton />
<ProductCard :product="product" />
```

### 2. 控制动画时长

避免过长的动画造成视觉疲劳：

```css
/* 推荐：1.5s - 2s */
animation-duration: 1.5s;

/* 避免：过长的动画 */
animation-duration: 5s;
```

### 3. 减少动画偏好

尊重用户的减少动画偏好：

```javascript
import { prefersReducedMotion } from '@/composables/useAccessibility'

// 在加载组件时检查
const shouldAnimate = !prefersReducedMotion()
```

### 4. 渐进式加载

对于大量数据，使用分页或无限滚动：

```vue
<template>
  <div class="infinite-list">
    <!-- 已加载的内容 -->
    <div v-if="!loading || products.length > 0" class="product-grid">
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
      />
    </div>

    <!-- 加载更多时的骨架屏 -->
    <div v-if="loading && products.length > 0" class="loading-more">
      <ProductCardSkeleton v-for="i in 3" :key="i" />
    </div>
  </div>
</template>
```

## 无障碍考虑

骨架屏组件已经内置了无障碍支持：

- `role="status"` - 告诉屏幕阅读器这是加载状态
- `aria-label` - 提供描述性的标签
- 尊重用户的减少动画偏好

但你仍然应该：

1. 确保主要加载操作有清晰的说明
2. 提供跳过加载内容的方式
3. 错误状态应该易于理解和处理

```vue
<template>
  <div>
    <p class="sr-only" aria-live="polite">
      {{ loading ? '正在加载商品列表...' : '商品列表加载完成' }}
    </p>
    
    <ProductCardSkeleton v-if="loading" />
    <ProductCard v-else v-for="p in products" :key="p.id" :product="p" />
  </div>
</template>
```
