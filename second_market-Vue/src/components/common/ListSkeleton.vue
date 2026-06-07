<template>
  <div class="list-skeleton">
    <div 
      v-for="i in count" 
      :key="i"
      class="skeleton-item"
      :class="{ 'animate': animated }"
      role="status"
      :aria-label="animated ? `正在加载第${i}项内容` : undefined"
    >
      <div v-if="showAvatar" class="skeleton-avatar"></div>
      <div class="skeleton-content">
        <div class="skeleton-title"></div>
        <div class="skeleton-text"></div>
      </div>
      <div v-if="showAction" class="skeleton-action"></div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  count: {
    type: Number,
    default: 3
  },
  animated: {
    type: Boolean,
    default: true
  },
  showAvatar: {
    type: Boolean,
    default: true
  },
  showAction: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.list-skeleton {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.skeleton-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
}

.skeleton-avatar,
.skeleton-title,
.skeleton-text,
.skeleton-action {
  background: var(--skeleton-base);
  border-radius: var(--radius-md);
}

.skeleton-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  flex-shrink: 0;
}

.skeleton-content {
  flex: 1;
  min-width: 0;
}

.skeleton-title {
  height: 16px;
  width: 70%;
  margin-bottom: var(--space-2);
}

.skeleton-text {
  height: 12px;
  width: 90%;
}

.skeleton-action {
  width: 60px;
  height: 32px;
  flex-shrink: 0;
}

/* 动画 */
.animate .skeleton-avatar,
.animate .skeleton-title,
.animate .skeleton-text,
.animate .skeleton-action {
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
</style>
