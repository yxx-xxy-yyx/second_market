<template>
  <div class="breadcrumb-nav" role="navigation" aria-label="面包屑导航">
    <ol class="breadcrumb-list">
      <li 
        v-for="(item, index) in items" 
        :key="index"
        class="breadcrumb-item"
      >
        <router-link 
          v-if="item.path && index < items.length - 1"
          :to="item.path"
          class="breadcrumb-link"
        >
          <el-icon v-if="item.icon" class="breadcrumb-icon">
            <component :is="item.icon" />
          </el-icon>
          {{ item.label }}
        </router-link>
        
        <span 
          v-else
          class="breadcrumb-current"
          :aria-current="index === items.length - 1 ? 'page' : undefined"
        >
          <el-icon v-if="item.icon" class="breadcrumb-icon">
            <component :is="item.icon" />
          </el-icon>
          {{ item.label }}
        </span>
        
        <el-icon 
          v-if="index < items.length - 1"
          class="breadcrumb-separator"
        >
          <ArrowRight />
        </el-icon>
      </li>
    </ol>
  </div>
</template>

<script setup>
import { ArrowRight } from '@element-plus/icons-vue'

defineProps({
  items: {
    type: Array,
    required: true,
    default: () => []
  }
})
</script>

<style scoped>
.breadcrumb-nav {
  padding: var(--space-3) 0;
}

.breadcrumb-list {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: var(--space-2);
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.breadcrumb-link {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--text-secondary);
  text-decoration: none;
  font-size: var(--font-sm);
  transition: color var(--duration-fast) var(--ease-default);
}

.breadcrumb-link:hover {
  color: var(--primary-500);
  text-decoration: underline;
}

.breadcrumb-current {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--text-primary);
  font-size: var(--font-sm);
  font-weight: var(--font-medium);
}

.breadcrumb-icon {
  font-size: var(--font-sm);
}

.breadcrumb-separator {
  font-size: var(--font-xs);
  color: var(--neutral-400);
}
</style>
