# ProductCard 产品卡片

ProductCard 是一个专门用于展示商品信息的卡片组件，包含图片、价格、卖家信息等。

## 基本用法

::: demo 基础产品卡片

```vue
<template>
  <ProductCard :product="product" />
</template>

<script setup>
import { ref } from 'vue'
import { ProductCard } from '@/components'

const product = ref({
  id: 1,
  title: 'MacBook Pro 2023 14寸',
  description: '几乎全新，电池健康100%',
  price: 12999,
  originalPrice: 14999,
  image: 'https://example.com/macbook.jpg',
  sellerName: '小明同学',
  viewCount: 256,
  likeCount: 42
})
</script>
```

:::

## 带标签

::: demo 带有各种标签的产品卡片

```vue
<template>
  <ProductCard :product="product" />
</template>

<script setup>
const product = {
  title: 'iPad Air 5',
  price: 5599,
  image: 'https://example.com/ipad.jpg',
  sellerName: '李华',
  isVerified: true,    // 正品标签
  isUrgent: true,      // 急售标签
  tags: ['平板', '学习']  // 自定义标签
}
</script>
```

:::

## 悬停效果

::: demo 带点击交互的卡片

```vue
<template>
  <ProductCard
    :product="product"
    @click="handleProductClick"
  />
</template>

<script setup>
const product = {
  title: 'Sony WH-1000XM4',
  price: 1699,
  image: 'https://example.com/headphones.jpg'
}

const handleProductClick = (product) => {
  console.log('查看商品详情:', product.id)
}
</script>
```

:::

## 产品数据格式

```typescript
interface Product {
  // 必需字段
  id: number | string
  title: string
  price: number

  // 可选字段
  description?: string           // 商品描述
  originalPrice?: number         // 原价（用于显示折扣）
  image?: string                 // 商品图片URL
  sellerName?: string            // 卖家名称
  sellerAvatar?: string          // 卖家头像
  viewCount?: number             // 浏览次数
  likeCount?: number             // 点赞数
  isVerified?: boolean           // 是否正品认证
  isUrgent?: boolean             // 是否急售
  tags?: string[]               // 商品标签（最多显示2个）
}
```

## Props

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| product | 商品数据对象 | `Product` | `{}` (必需) |

## Events

| 事件名 | 说明 | 参数 |
|--------|------|------|
| click | 点击卡片时触发 | `(product: Product)` |

## 使用示例

### 商品列表

```vue
<template>
  <div class="product-grid">
    <ProductCard
      v-for="item in products"
      :key="item.id"
      :product="item"
      @click="goToDetail"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ProductCard } from '@/components'
import { useRouter } from 'vue-router'

const router = useRouter()
const products = ref([
  { id: 1, title: '商品1', price: 100, image: '...' },
  { id: 2, title: '商品2', price: 200, image: '...' },
  { id: 3, title: '商品3', price: 300, image: '...' }
])

const goToDetail = (product) => {
  router.push(`/product/${product.id}`)
}
</script>

<style scoped>
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--space-4);
}
</style>
```

### 响应式列表

```vue
<template>
  <div class="product-list" :class="{ 'is-mobile': isMobile }">
    <ProductCard
      v-for="item in products"
      :key="item.id"
      :product="item"
      layout="horizontal"
    />
  </div>
</template>

<script setup>
import { useDeviceType } from '@/composables/useDevice'
const { isMobile } = useDeviceType()
</script>
```

## 最佳实践

1. **图片优化**：始终为商品提供高质量图片
2. **价格展示**：有折扣时显示原价，增加转化率
3. **标签使用**：限制标签数量，最多显示2-3个
4. **加载状态**：列表加载时使用骨架屏

```vue
<template>
  <!-- 加载状态 -->
  <div v-if="loading" class="product-grid">
    <ProductCardSkeleton v-for="i in 6" :key="i" />
  </div>

  <!-- 实际数据 -->
  <div v-else class="product-grid">
    <ProductCard
      v-for="item in products"
      :key="item.id"
      :product="item"
    />
  </div>
</template>
```
