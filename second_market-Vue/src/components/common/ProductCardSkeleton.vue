<template>
  <div 
    class="product-card-skeleton"
    :class="{ 'animate': animated }"
    role="status"
    :aria-label="animated ? '正在加载商品卡片' : undefined"
  >
    <!-- 图片骨架 -->
    <div class="skeleton-image"></div>
    
    <!-- 内容区域 -->
    <div class="skeleton-content">
      <!-- 标题骨架 -->
      <div class="skeleton-title"></div>
      <div class="skeleton-title-short"></div>
      
      <!-- 描述骨架 -->
      <div class="skeleton-desc"></div>
      
      <!-- 价格骨架 -->
      <div class="skeleton-price">
        <div class="skeleton-price-main"></div>
        <div class="skeleton-price-original"></div>
      </div>
      
      <!-- 底部信息骨架 -->
      <div class="skeleton-footer">
        <div class="skeleton-avatar"></div>
        <div class="skeleton-name"></div>
        <div class="skeleton-stats">
          <div class="skeleton-stat"></div>
          <div class="skeleton-stat"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  animated: {
    type: Boolean,
    default: true
  }
})
</script>

<style scoped>
.product-card-skeleton {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.product-card-skeleton .skeleton-image,
.product-card-skeleton .skeleton-title,
.product-card-skeleton .skeleton-title-short,
.product-card-skeleton .skeleton-desc,
.product-card-skeleton .skeleton-price-main,
.product-card-skeleton .skeleton-price-original,
.product-card-skeleton .skeleton-avatar,
.product-card-skeleton .skeleton-name,
.product-card-skeleton .skeleton-stat {
  background: var(--skeleton-base);
  border-radius: var(--radius-md);
}

.product-card-skeleton .skeleton-image {
  width: 100%;
  padding-top: 100%;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.product-card-skeleton .skeleton-content {
  padding: var(--space-4);
}

.product-card-skeleton .skeleton-title {
  height: 20px;
  width: 90%;
  margin-bottom: var(--space-2);
}

.product-card-skeleton .skeleton-title-short {
  height: 20px;
  width: 60%;
  margin-bottom: var(--space-3);
}

.product-card-skeleton .skeleton-desc {
  height: 14px;
  width: 80%;
  margin-bottom: var(--space-4);
}

.product-card-skeleton .skeleton-price {
  display: flex;
  align-items: baseline;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
}

.product-card-skeleton .skeleton-price-main {
  height: 24px;
  width: 80px;
}

.product-card-skeleton .skeleton-price-original {
  height: 14px;
  width: 60px;
}

.product-card-skeleton .skeleton-footer {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding-top: var(--space-3);
  border-top: 1px solid var(--border-primary);
}

.product-card-skeleton .skeleton-avatar {
  width: 20px;
  height: 20px;
  border-radius: var(--radius-full);
  flex-shrink: 0;
}

.product-card-skeleton .skeleton-name {
  height: 14px;
  width: 60px;
  flex-grow: 1;
}

.product-card-skeleton .skeleton-stats {
  display: flex;
  gap: var(--space-3);
}

.product-card-skeleton .skeleton-stat {
  width: 40px;
  height: 14px;
}

/* 动画 */
.animate .skeleton-image,
.animate .skeleton-title,
.animate .skeleton-title-short,
.animate .skeleton-desc,
.animate .skeleton-price-main,
.animate .skeleton-price-original,
.animate .skeleton-avatar,
.animate .skeleton-name,
.animate .skeleton-stat {
  background: linear-gradient(
    90deg,
    var(--skeleton-base) 0%,
    var(--skeleton-highlight) 50%,
    var(--skeleton-base) 100%
  );
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s ease-in-out infinite;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* 暗黑模式 */
html.dark .product-card-skeleton .skeleton-image,
html.dark .product-card-skeleton .skeleton-title,
html.dark .product-card-skeleton .skeleton-title-short,
html.dark .product-card-skeleton .skeleton-desc,
html.dark .product-card-skeleton .skeleton-price-main,
html.dark .product-card-skeleton .skeleton-price-original,
html.dark .product-card-skeleton .skeleton-avatar,
html.dark .product-card-skeleton .skeleton-name,
html.dark .product-card-skeleton .skeleton-stat {
  background: linear-gradient(
    90deg,
    var(--skeleton-base) 0%,
    var(--skeleton-highlight) 50%,
    var(--skeleton-base) 100%
  );
  background-size: 200% 100%;
}
</style>
