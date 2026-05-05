<template>
  <div class="user-bottom-nav">
    <div
      v-for="item in navMenu"
      :key="item.path"
      :class="['nav-item', { active: isActive(item.path), 'publish-center': item.isPublish }]"
      @click="router.push(item.path)"
    >
      <div class="nav-icon-container">
        <el-icon :size="item.isPublish ? 32 : 24">
          <component :is="item.icon" />
        </el-icon>
      </div>
      <span class="nav-label" v-if="!item.isPublish">{{ $t('nav.' + item.id) }}</span>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import IconHome from '@/components/icons/IconHome.vue'
import IconCategory from '@/components/icons/IconCategory.vue'
import IconPlus from '@/components/icons/IconPlus.vue'
import IconUser from '@/components/icons/IconUser.vue'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const navMenu = [
  { id: 'home', label: '首页', path: '/user/dashboard', icon: IconHome },
  { id: 'categories', label: '分类', path: '/user/categories', icon: IconCategory },
  { id: 'publish', label: '发布', path: '/user/publish', icon: IconPlus, isPublish: true },
  { id: 'myAccount', label: '我的', path: '/user/profile', icon: IconUser }
]

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}
</script>

<style scoped>
.user-bottom-nav {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: calc(65px + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 -2px 15px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  justify-content: space-around;
  align-items: center;
  padding-bottom: 0;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  flex: 1;
  height: 100%;
  cursor: pointer;
  transition: color 0.18s ease, transform 0.06s ease;
  position: relative;
}

.nav-item:active {
  transform: scale(0.99);
}

.nav-item.active {
  color: var(--primary-color);
}

.nav-icon-container {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.18s ease, background-color 0.18s ease;
}

.nav-item:not(.publish-center) .nav-icon-container {
  width: 36px;
  height: 36px;
  border-radius: 12px;
}

.nav-item.active .nav-icon-container {
  transform: translateY(-1px);
}

.nav-item.active:not(.publish-center) .nav-icon-container {
  background: rgba(6, 182, 212, 0.12);
}

.nav-label {
  font-size: 11px;
  margin-top: 4px;
  font-weight: 500;
  line-height: 1;
  transition: color 0.18s ease;
}

.nav-item.active .nav-label {
  color: var(--primary-color);
  font-weight: 700;
}

/* 中间发布按钮特别样式 */
.publish-center {
  flex: 1.2;
}

.publish-center .nav-icon-container {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  color: white;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(6, 182, 212, 0.30);
  margin-top: -35px;
  border: 4px solid #fff;
}

.publish-center.active .nav-icon-container {
  box-shadow: 0 6px 16px rgba(6, 182, 212, 0.55);
  transform: translateY(-5px) scale(1.05);
}

@media screen and (max-width: 767px) {
  .user-bottom-nav {
    display: flex;
  }
}
</style>
