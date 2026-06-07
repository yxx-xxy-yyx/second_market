<template>
  <div 
    class="image-lazy" 
    :class="{ 'is-loaded': isLoaded, 'is-error': hasError }"
    :style="{ paddingTop: aspectRatio }"
  >
    <img
      v-if="shouldLoad"
      :src="src"
      :alt="alt"
      class="lazy-img"
      :loading="eager ? 'eager' : 'lazy'"
      @load="handleLoad"
      @error="handleError"
    />
    
    <!-- 加载中占位 -->
    <div v-if="!isLoaded && !hasError" class="lazy-placeholder">
      <div class="placeholder-shimmer"></div>
      <el-icon v-if="placeholderIcon" :size="placeholderSize" class="placeholder-icon">
        <Picture />
      </el-icon>
    </div>
    
    <!-- 错误占位 -->
    <div v-if="hasError" class="lazy-error">
      <el-icon :size="placeholderSize" class="error-icon">
        <PictureFilled />
      </el-icon>
      <span v-if="errorText" class="error-text">{{ errorText }}</span>
    </div>
    
    <!-- 加载完成渐变 -->
    <transition name="fade">
      <div v-if="isLoaded" class="lazy-loaded"></div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Picture, PictureFilled } from '@element-plus/icons-vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: ''
  },
  aspectRatio: {
    type: String,
    default: '100%'
  },
  placeholderIcon: {
    type: Boolean,
    default: true
  },
  placeholderSize: {
    type: [Number, String],
    default: 48
  },
  errorText: {
    type: String,
    default: ''
  },
  eager: {
    type: Boolean,
    default: false
  },
  rootMargin: {
    type: String,
    default: '50px'
  },
  threshold: {
    type: Number,
    default: 0
  }
})

const isLoaded = ref(false)
const hasError = ref(false)
const shouldLoad = ref(props.eager)

let observer = null

const handleLoad = () => {
  isLoaded.value = true
  hasError.value = false
}

const handleError = () => {
  hasError.value = true
  isLoaded.value = false
}

onMounted(() => {
  if (!props.eager && 'IntersectionObserver' in window) {
    observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            shouldLoad.value = true
            observer?.disconnect()
          }
        })
      },
      {
        rootMargin: props.rootMargin,
        threshold: props.threshold
      }
    )
    
    // 观察当前元素
    const el = document.querySelector('.image-lazy')
    if (el) {
      observer.observe(el)
    }
  } else {
    shouldLoad.value = true
  }
})

onUnmounted(() => {
  observer?.disconnect()
})
</script>

<style scoped>
.image-lazy {
  position: relative;
  overflow: hidden;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
}

.lazy-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity var(--duration-normal) var(--ease-default);
}

.is-loaded .lazy-img {
  opacity: 1;
}

.lazy-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-shimmer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    var(--bg-secondary) 0%,
    var(--bg-tertiary) 50%,
    var(--bg-secondary) 100%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.placeholder-icon,
.error-icon {
  position: relative;
  z-index: 1;
  color: var(--neutral-400);
}

.lazy-error {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  background: var(--bg-secondary);
}

.error-icon {
  color: var(--neutral-400);
}

.error-text {
  font-size: var(--font-sm);
  color: var(--text-secondary);
}

.lazy-loaded {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: transparent;
  pointer-events: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--duration-normal) var(--ease-default);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
