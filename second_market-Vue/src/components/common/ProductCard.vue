<template>
  <UnifiedCard
    :bordered="false"
    :hover-effect="true"
    :clickable="true"
    class="product-card"
    @click="handleClick"
  >
    <template #header>
      <div class="product-image-wrapper">
        <img
          v-if="product.image"
          :src="product.image"
          :alt="product.title"
          class="product-image"
          loading="lazy"
        />
        <div v-else class="product-image-placeholder">
          <el-icon :size="48" color="var(--neutral-400)"><Picture /></el-icon>
        </div>
        <!-- 标签 -->
        <div v-if="product.tags?.length || product.isVerified || product.isUrgent" class="product-badges">
          <el-tag v-if="product.isVerified" type="success" size="small" class="badge">
            <el-icon><CircleCheckFilled /></el-icon> 正品
          </el-tag>
          <el-tag v-if="product.isUrgent" type="danger" size="small" class="badge">
            急售
          </el-tag>
          <el-tag v-for="tag in product.tags?.slice(0, 2)" :key="tag" size="small" class="badge">
            {{ tag }}
          </el-tag>
        </div>
      </div>
    </template>

    <div class="product-content">
      <h3 class="product-title">{{ product.title }}</h3>
      <p v-if="product.description" class="product-desc">{{ product.description }}</p>

      <div class="product-meta">
        <div class="product-price">
          <span class="currency">¥</span>
          <span class="amount">{{ formatPrice(product.price) }}</span>
          <span v-if="product.originalPrice" class="original-price">¥{{ formatPrice(product.originalPrice) }}</span>
        </div>

        <div class="product-footer">
          <div class="seller-info">
            <el-avatar :size="20" :src="product.sellerAvatar" />
            <span class="seller-name">{{ product.sellerName }}</span>
          </div>
          <div class="product-stats">
            <span v-if="product.viewCount" class="stat">
              <el-icon><View /></el-icon>
              {{ product.viewCount }}
            </span>
            <span v-if="product.likeCount" class="stat">
              <el-icon><Heart /></el-icon>
              {{ product.likeCount }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </UnifiedCard>
</template>

<script setup>
import { computed } from 'vue'
import UnifiedCard from './UnifiedCard.vue'
import { Picture, CircleCheckFilled, View, Heart } from '@element-plus/icons-vue'

const props = defineProps({
  product: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

const emit = defineEmits(['click'])

const formatPrice = (price) => {
  if (!price && price !== 0) return ''
  return Number(price).toFixed(2)
}

const handleClick = () => {
  emit('click', props.product)
}
</script>

<style scoped>
.product-card {
  overflow: hidden;
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 100%;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--bg-secondary);
}

.product-image,
.product-image-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-image-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
}

.product-badges {
  position: absolute;
  top: var(--space-2);
  left: var(--space-2);
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-1);
}

.badge {
  backdrop-filter: blur(8px);
  background: rgba(255, 255, 255, 0.9);
}

html.dark .badge {
  background: rgba(0, 0, 0, 0.6);
}

.product-content {
  margin-top: var(--space-3);
}

.product-title {
  font-size: var(--font-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
  line-height: var(--leading-snug);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-desc {
  font-size: var(--font-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-3) 0;
  line-height: var(--leading-normal);
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: var(--space-2);
}

.currency {
  font-size: var(--font-md);
  font-weight: var(--font-semibold);
  color: var(--error-500);
}

.amount {
  font-size: var(--font-2xl);
  font-weight: var(--font-bold);
  color: var(--error-500);
}

.original-price {
  font-size: var(--font-sm);
  color: var(--neutral-400);
  text-decoration: line-through;
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: var(--space-3);
  padding-top: var(--space-3);
  border-top: 1px solid var(--border-primary);
}

.seller-info {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.seller-name {
  font-size: var(--font-sm);
  color: var(--text-secondary);
}

.product-stats {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.stat {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--font-sm);
  color: var(--text-secondary);
}

.stat .el-icon {
  font-size: var(--font-md);
}
</style>
