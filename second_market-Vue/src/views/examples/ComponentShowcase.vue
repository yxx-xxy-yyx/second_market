<template>
  <div class="component-showcase">
    <div class="container">
      <h1 class="page-title">组件库展示</h1>
      <p class="page-subtitle">使用我们的设计系统创建统一的UI体验</p>

      <!-- 响应式布局示例 -->
      <section class="showcase-section">
        <h2 class="section-title">响应式布局</h2>
        <ResponsiveLayout>
          <template #desktop>
            <div class="device-demo desktop-demo">
              <h3>桌面端布局</h3>
              <p>宽屏显示，使用更丰富的内容布局</p>
            </div>
          </template>
          <template #tablet>
            <div class="device-demo tablet-demo">
              <h3>平板端布局</h3>
              <p>中等屏幕，平衡布局</p>
            </div>
          </template>
          <template #mobile>
            <div class="device-demo mobile-demo">
              <h3>移动端布局</h3>
              <p>小屏幕，简洁高效</p>
            </div>
          </template>
        </ResponsiveLayout>
      </section>

      <!-- 产品卡片示例 -->
      <section class="showcase-section">
        <h2 class="section-title">产品卡片</h2>
        <div class="product-grid">
          <ProductCard
            v-for="product in sampleProducts"
            :key="product.id"
            :product="product"
            @click="handleProductClick"
          />
        </div>
      </section>

      <!-- 设计令牌展示 -->
      <section class="showcase-section">
        <h2 class="section-title">设计令牌</h2>

        <div class="token-group">
          <h3>颜色系统</h3>
          <div class="color-grid">
            <div
              v-for="color in colors"
              :key="color.name"
              class="color-item"
              :style="{ background: color.value }"
            >
              <span class="color-name">{{ color.name }}</span>
              <span class="color-value">{{ color.value }}</span>
            </div>
          </div>
        </div>

        <div class="token-group">
          <h3>间距系统</h3>
          <div class="spacing-grid">
            <div
              v-for="space in spaces"
              :key="space.name"
              class="space-item"
              :style="{ width: space.value, height: space.value }"
            >
              <span class="space-label">{{ space.name }}</span>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ResponsiveLayout, ProductCard, UnifiedCard } from '@/components'

const sampleProducts = ref([
  {
    id: 1,
    title: 'MacBook Pro 2023 14寸',
    description: '几乎全新，电池健康100%，原装充电器',
    price: 12999,
    originalPrice: 14999,
    image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400',
    sellerName: '小明同学',
    sellerAvatar: '',
    viewCount: 256,
    likeCount: 42,
    isVerified: true,
    isUrgent: false,
    tags: ['电脑', '苹果']
  },
  {
    id: 2,
    title: 'iPad Air 5 256G',
    description: '深空灰色，带Apple Pencil二代',
    price: 5599,
    image: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400',
    sellerName: '李华',
    viewCount: 189,
    likeCount: 28,
    isVerified: true,
    isUrgent: true,
    tags: ['平板', '学习']
  },
  {
    id: 3,
    title: 'Sony WH-1000XM4 耳机',
    description: '顶级降噪，音质优秀',
    price: 1699,
    originalPrice: 2299,
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400',
    sellerName: '技术宅',
    viewCount: 312,
    likeCount: 56,
    tags: ['耳机', '索尼']
  }
])

const colors = ref([
  { name: 'Primary 500', value: 'var(--primary-500)' },
  { name: 'Success 500', value: 'var(--success-500)' },
  { name: 'Warning 500', value: 'var(--warning-500)' },
  { name: 'Error 500', value: 'var(--error-500)' },
  { name: 'Neutral 900', value: 'var(--neutral-900)' },
  { name: 'Neutral 50', value: 'var(--neutral-50)' }
])

const spaces = ref([
  { name: 'xs (4px)', value: '4px' },
  { name: 'sm (8px)', value: '8px' },
  { name: 'md (16px)', value: '16px' },
  { name: 'lg (24px)', value: '24px' },
  { name: 'xl (32px)', value: '32px' }
])

const handleProductClick = (product) => {
  console.log('Product clicked:', product)
}
</script>

<style scoped>
.component-showcase {
  min-height: 100vh;
  background: var(--bg-primary);
  padding: var(--space-8) 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-4);
}

.page-title {
  font-size: var(--font-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.page-subtitle {
  font-size: var(--font-lg);
  color: var(--text-secondary);
  margin: 0 0 var(--space-8) 0;
}

.showcase-section {
  margin-bottom: var(--space-8);
}

.section-title {
  font-size: var(--font-2xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-4) 0;
}

.device-demo {
  padding: var(--space-6);
  border-radius: var(--radius-lg);
  text-align: center;
}

.desktop-demo {
  background: linear-gradient(135deg, var(--primary-100), var(--primary-50));
}

.tablet-demo {
  background: linear-gradient(135deg, var(--secondary-100), var(--secondary-50));
}

.mobile-demo {
  background: linear-gradient(135deg, var(--accent-100), var(--accent-50));
}

.device-demo h3 {
  font-size: var(--font-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.device-demo p {
  font-size: var(--font-md);
  color: var(--text-secondary);
  margin: 0;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--space-4);
}

.token-group {
  margin-bottom: var(--space-6);
}

.token-group h3 {
  font-size: var(--font-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
}

.color-grid {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
}

.color-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  width: 120px;
  height: 120px;
  border-radius: var(--radius-lg);
  padding: var(--space-3);
  box-shadow: var(--shadow-md);
  transition: transform var(--duration-normal) var(--ease-default);
}

.color-item:hover {
  transform: translateY(-4px);
}

.color-name {
  font-size: var(--font-sm);
  font-weight: var(--font-semibold);
  color: white;
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
  margin-bottom: var(--space-1);
}

.color-value {
  font-size: var(--font-xs);
  color: rgba(255,255,255,0.9);
}

.spacing-grid {
  display: flex;
  align-items: flex-end;
  gap: var(--space-3);
}

.space-item {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: var(--space-2);
  background: var(--primary-100);
  border-radius: var(--radius-md);
  transition: all var(--duration-normal) var(--ease-default);
}

.space-item:hover {
  background: var(--primary-200);
}

.space-label {
  font-size: var(--font-xs);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}
</style>
