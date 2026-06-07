<template>
  <article
    class="product-card"
    role="article"
    :aria-label="cardAriaLabel"
    tabindex="0"
    @click="handleClick"
    @keydown.enter="handleClick"
    @keydown.space.prevent="handleClick"
  >
    <div class="product-image-wrapper">
      <img
        v-if="product.image"
        :src="product.image"
        :alt="product.title"
        class="product-image"
        loading="lazy"
      />
      <div v-else class="product-image-placeholder" role="img" :aria-label="`${product.title || '商品'}的图片占位符`">
        <el-icon :size="48" color="var(--neutral-400)"><Picture /></el-icon>
      </div>
      
      <!-- 标签 -->
      <div v-if="hasBadges" class="product-badges" role="group" :aria-label="badgesAriaLabel">
        <el-tag v-if="product.isVerified" type="success" size="small" class="badge" role="status">
          <el-icon><CircleCheckFilled /></el-icon> 
          <span>正品认证</span>
        </el-tag>
        <el-tag v-if="product.isUrgent" type="danger" size="small" class="badge" role="status">
          <span>急售</span>
        </el-tag>
        <el-tag v-for="tag in product.tags?.slice(0, 2)" :key="tag" size="small" class="badge">
          {{ tag }}
        </el-tag>
      </div>
    </div>

    <div class="product-content">
      <h3 class="product-title">{{ product.title }}</h3>
      <p v-if="product.description" class="product-desc">{{ product.description }}</p>

      <div class="product-meta">
        <div class="product-price" role="group" aria-label="价格信息">
          <span class="currency" aria-hidden="true">¥</span>
          <span class="amount" aria-label="现价">{{ formatPrice(product.price) }}</span>
          <span v-if="product.originalPrice" class="original-price" aria-label="原价">
            ¥{{ formatPrice(product.originalPrice) }}
          </span>
        </div>

        <div class="product-footer">
          <div class="seller-info" role="group" :aria-label="`卖家: ${product.sellerName || '未知'}`">
            <el-avatar 
              :size="20" 
              :src="product.sellerAvatar"
              :alt="product.sellerName"
            />
            <span class="seller-name">{{ product.sellerName }}</span>
          </div>
          <div class="product-stats">
            <span v-if="product.viewCount" class="stat" :aria-label="`浏览量 ${product.viewCount}`">
              <el-icon aria-hidden="true"><View /></el-icon>
              <span>{{ product.viewCount }}</span>
            </span>
            <span v-if="product.likeCount" class="stat" :aria-label="`收藏数 ${product.likeCount}`">
              <el-icon aria-hidden="true"><Heart /></el-icon>
              <span>{{ product.likeCount }}</span>
            </span>
          </div>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { Picture, CircleCheckFilled, View, Heart } from '@element-plus/icons-vue'

const props = defineProps({
  product: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

const emit = defineEmits(['click'])

// 计算是否有标签
const hasBadges = computed(() => {
  return product.tags?.length || product.isVerified || product.isUrgent
})

// 计算商品卡片的可访问标签
const cardAriaLabel = computed(() => {
  const parts = []
  
  if (props.product.title) {
    parts.push(props.product.title)
  }
  
  if (props.product.price) {
    parts.push(`价格${props.product.price}元`)
  }
  
  if (props.product.originalPrice) {
    parts.push(`原价${props.product.originalPrice}元`)
  }
  
  if (props.product.isVerified) {
    parts.push('已认证')
  }
  
  if (props.product.isUrgent) {
    parts.push('急售')
  }
  
  if (props.product.sellerName) {
    parts.push(`卖家${props.product.sellerName}`)
  }
  
  parts.push('点击查看详情')
  
  return parts.join('，')
})

// 标签组的无障碍标签
const badgesAriaLabel = computed(() => {
  const badges = []
  if (props.product.isVerified) badges.push('正品认证标签')
  if (props.product.isUrgent) badges.push('急售标签')
  if (props.product.tags?.length) badges.push(`商品标签：${props.product.tags.slice(0, 2).join('、')}`)
  return badges.join('，')
})

const formatPrice = (price) => {
  if (!price && price !== 0) return ''
  return Number(price).toFixed(2)
}

const handleClick = (e) => {
  emit('click', props.product)
}
</script>

<style scoped>
.product-card {
  overflow: hidden;
  cursor: pointer;
  transition: all var(--duration-normal) var(--ease-default);
}

.product-card:focus-visible {
  outline: 2px solid var(--primary-500);
  outline-offset: 2px;
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
