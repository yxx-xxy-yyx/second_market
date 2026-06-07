# ResponsiveLayout 响应式布局

ResponsiveLayout 是一个强大的响应式布局组件，可以根据设备类型自动切换不同的布局内容。

## 基本用法

::: demo 根据设备类型显示不同内容

```vue
<template>
  <ResponsiveLayout>
    <template #desktop>
      <DesktopNavigation />
    </template>
    <template #tablet>
      <TabletNavigation />
    </template>
    <template #mobile>
      <MobileNavigation />
    </template>
  </ResponsiveLayout>
</template>

<script setup>
import { ResponsiveLayout } from '@/components'
</script>
```

:::

## 设备断点

| 设备 | 断点范围 | 说明 |
|------|----------|------|
| Mobile | < 768px | 手机屏幕 |
| Tablet | 768px - 991px | 平板屏幕 |
| Desktop | ≥ 992px | 桌面屏幕 |

## 使用示例

### 条件渲染

```vue
<template>
  <ResponsiveLayout>
    <template #desktop>
      <div class="desktop-layout">
        <DesktopHeader />
        <DesktopSidebar />
        <main class="main-content">
          <slot />
        </main>
      </div>
    </template>

    <template #mobile>
      <div class="mobile-layout">
        <MobileHeader />
        <main class="mobile-content">
          <slot />
        </main>
        <MobileTabbar />
      </div>
    </template>
  </ResponsiveLayout>
</template>
```

### 传递布局属性

你可以在插槽中访问布局属性：

```vue
<template>
  <ResponsiveLayout>
    <template #desktop="{ props }">
      <div class="desktop" :style="{ width: props.screenWidth + 'px' }">
        <p>屏幕宽度: {{ props.screenWidth }}</p>
        <p>设备类型: {{ props.isDesktop ? '桌面' : '其他' }}</p>
      </div>
    </template>
  </ResponsiveLayout>
</template>
```

可用的布局属性：

| 属性 | 类型 | 说明 |
|------|------|------|
| isMobile | `Ref<boolean>` | 是否是移动端 |
| isTablet | `Ref<boolean>` | 是否是平板端 |
| isDesktop | `Ref<boolean>` | 是否是桌面端 |
| screenWidth | `Ref<number>` | 屏幕宽度 |
| screenHeight | `Ref<number>` | 屏幕高度 |

## Props

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| - | - | - | - |

## Slots

| 插槽名 | 说明 | 作用域数据 |
|--------|------|------------|
| default | 默认内容（当没有设备特定插槽时显示） | layoutProps |
| desktop | 桌面端布局 | layoutProps |
| tablet | 平板端布局 | layoutProps |
| mobile | 移动端布局 | layoutProps |

## 组合使用

### 与其他组件配合

```vue
<template>
  <ResponsiveLayout>
    <template #desktop="{ props }">
      <el-container class="desktop-layout">
        <el-aside width="200px">
          <Sidebar />
        </el-aside>
        <el-container>
          <el-header>
            <DesktopHeader />
          </el-header>
          <el-main>
            <UnifiedCard>
              <slot />
            </UnifiedCard>
          </el-main>
        </el-container>
      </el-container>
    </template>

    <template #mobile>
      <div class="mobile-layout">
        <MobileHeader />
        <main class="mobile-content">
          <UnifiedCard size="sm">
            <slot />
          </UnifiedCard>
        </main>
        <MobileTabbar />
      </div>
    </template>
  </ResponsiveLayout>
</template>
```

### 自适应栅格

```vue
<template>
  <ResponsiveLayout>
    <template #desktop="{ props }">
      <div class="product-grid" style="grid-template-columns: repeat(4, 1fr)">
        <ProductCard v-for="p in products" :key="p.id" :product="p" />
      </div>
    </template>

    <template #tablet="{ props }">
      <div class="product-grid" style="grid-template-columns: repeat(2, 1fr)">
        <ProductCard v-for="p in products" :key="p.id" :product="p" />
      </div>
    </template>

    <template #mobile>
      <div class="product-grid" style="grid-template-columns: repeat(2, 1fr)">
        <ProductCard v-for="p in products" :key="p.id" :product="p" />
      </div>
    </template>
  </ResponsiveLayout>
</template>
```

## 注意事项

1. **优先级**：desktop > tablet > mobile
2. **性能**：设备切换时会重新渲染组件，注意性能影响
3. **状态保持**：切换设备可能导致组件状态丢失，考虑使用 keep-alive

## 最佳实践

```vue
<template>
  <!-- 使用 key 确保组件在设备切换时重新渲染 -->
  <ResponsiveLayout>
    <template #desktop="{ props }">
      <DesktopView :key="`desktop-${currentRoute}`" />
    </template>
    <template #mobile>
      <MobileView :key="`mobile-${currentRoute}`" />
    </template>
  </ResponsiveLayout>
</template>
```
