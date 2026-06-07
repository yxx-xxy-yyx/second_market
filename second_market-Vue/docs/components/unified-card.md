# UnifiedCard 统一卡片

UnifiedCard 是一个灵活且可定制的卡片组件，适用于各种场景。

## 基本用法

::: demo 使用默认配置的卡片

```vue
<template>
  <UnifiedCard title="卡片标题">
    <p>卡片内容区域</p>
  </UnifiedCard>
</template>

<script setup>
import { UnifiedCard } from '@/components'
</script>
```

:::

## 多种变体

::: demo 提供四种卡片变体

```vue
<template>
  <div class="card-grid">
    <UnifiedCard title="默认卡片" variant="default">
      <p>带有阴影的标准卡片</p>
    </UnifiedCard>

    <UnifiedCard title="带边框" variant="bordered">
      <p>使用边框的卡片</p>
    </UnifiedCard>

    <UnifiedCard title="扁平风格" variant="flat">
      <p>没有阴影的扁平卡片</p>
    </UnifiedCard>

    <UnifiedCard title="渐变背景" variant="gradient">
      <p>带有渐变背景的卡片</p>
    </UnifiedCard>
  </div>
</template>
```

:::

## 不同尺寸

::: demo 三种尺寸选项

```vue
<template>
  <div class="card-grid">
    <UnifiedCard title="小卡片" size="sm">
      <p>紧凑的卡片内容</p>
    </UnifiedCard>

    <UnifiedCard title="中卡片" size="md">
      <p>标准尺寸的卡片内容</p>
    </UnifiedCard>

    <UnifiedCard title="大卡片" size="lg">
      <p>更宽松的大卡片内容</p>
    </UnifiedCard>
  </div>
</template>
```

:::

## 带图标

::: demo 带有图标的卡片

```vue
<template>
  <UnifiedCard
    title="销售统计"
    sub-header="本月数据"
    :icon="TrendCharts"
    icon-bg="var(--success-100)"
  >
    <p>销售额：¥12,345</p>
  </UnifiedCard>
</template>

<script setup>
import { TrendCharts } from '@element-plus/icons-vue'
</script>
```

:::

## 可点击卡片

::: demo 带悬停效果的卡片

```vue
<template>
  <UnifiedCard
    title="点击查看详情"
    :hover-effect="true"
    :clickable="true"
    @click="handleClick"
  >
    <p>悬停时会有上浮效果</p>
  </UnifiedCard>
</template>

<script setup>
const handleClick = () => {
  console.log('卡片被点击')
}
</script>
```

:::

## Props

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| title | 卡片标题 | `string` | - |
| subHeader | 副标题 | `string` | - |
| icon | 图标组件 | `Component` | - |
| iconBg | 图标背景色 | `string` | `var(--primary-100)` |
| size | 尺寸 | `'sm' \| 'md' \| 'lg'` | `'md'` |
| variant | 变体样式 | `'default' \| 'bordered' \| 'flat' \| 'gradient'` | `'default'` |
| bordered | 是否有边框 | `boolean` | `false` |
| shadow | 是否有阴影 | `boolean` | `true` |
| clickable | 是否可点击 | `boolean` | `false` |
| hoverEffect | 是否显示悬停效果 | `boolean` | `false` |
| noPadding | 内容区域是否无内边距 | `boolean` | `false` |

## Slots

| 插槽名 | 说明 |
|--------|------|
| default | 默认内容插槽 |
| header | 自定义头部内容 |
| header-extra | 头部右侧额外内容 |
| footer | 底部内容插槽 |

## Events

| 事件名 | 说明 | 参数 |
|--------|------|------|
| click | 点击卡片时触发 | `(event: MouseEvent)` |

## 样式变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| --unified-card-bg | 卡片背景色 | `var(--bg-card)` |
| --unified-card-radius | 卡片圆角 | `var(--radius-lg)` |
| --unified-card-shadow | 卡片阴影 | `var(--shadow-md)` |

## 使用示例

### 组合使用

```vue
<template>
  <UnifiedCard
    title="商品信息"
    :icon="Goods"
    icon-bg="var(--primary-100)"
    :hover-effect="true"
  >
    <template #header-extra>
      <el-tag type="success">在售</el-tag>
    </template>

    <div class="product-info">
      <h3>{{ product.title }}</h3>
      <p class="price">¥{{ product.price }}</p>
    </div>

    <template #footer>
      <div class="actions">
        <el-button type="primary" size="small">立即购买</el-button>
        <el-button size="small">收藏</el-button>
      </div>
    </template>
  </UnifiedCard>
</template>

<script setup>
import { UnifiedCard } from '@/components'
import { Goods } from '@element-plus/icons-vue'

const product = {
  title: 'MacBook Pro 2023',
  price: 12999
}
</script>
```

### 响应式设计

```vue
<template>
  <UnifiedCard
    :size="isMobile ? 'sm' : 'md'"
    :shadow="!isMobile"
  >
    <p>卡片内容</p>
  </UnifiedCard>
</template>

<script setup>
import { useDeviceType } from '@/composables/useDevice'
const { isMobile } = useDeviceType()
</script>
```
