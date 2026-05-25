<template>
  <footer class="user-footer">
    <div class="footer-container">
      <div class="footer-content">
        <div class="footer-brand">
          <div class="logo">
            <img src="@/assets/logo.svg" alt="Logo" class="logo-img" v-if="hasLogo" />
            <span class="logo-text">{{ $t('footer.logo') }}</span>
          </div>
          <p class="brand-desc">
            {{ $t('footer.desc') }}
          </p>
        </div>
        
        <div class="footer-links">
          <div class="link-group">
            <h4 class="group-title">{{ $t('common.mall') }}</h4>
            <router-link to="/user/dashboard">{{ $t('nav.home') }}</router-link>
            <router-link to="/user/products">{{ $t('nav.products') }}</router-link>
            <router-link to="/user/publish">{{ $t('common.publish') }}</router-link>
          </div>
          
          <div class="link-group">
            <h4 class="group-title">{{ $t('common.mine') }}</h4>
            <router-link to="/user/profile">{{ $t('nav.myAccount') }}</router-link>
            <router-link to="/user/orders">{{ $t('nav.myOrders') }}</router-link>
            <router-link to="/user/favorites">{{ $t('dashboard.stats.favorites') }}</router-link>
          </div>
          
          <div class="link-group">
            <h4 class="group-title">{{ $t('userFooter.helpCenter') }}</h4>
            <a href="#">{{ $t('userFooter.faq') }}</a>
            <a href="#">{{ $t('userFooter.userAgreement') }}</a>
            <a href="#">{{ $t('userFooter.privacyPolicy') }}</a>
          </div>
        </div>
      </div>
      
      <el-divider />
      
      <div class="footer-bottom">
        <div class="copyright">
          {{ $t('footer.copyright') }}
        </div>
        <div class="footer-extra">
          <span>{{ $t('footer.made') }}</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()
const hasLogo = ref(false)

onMounted(() => {
  // 初始化语言（和登录注册页完全同步）
  const savedLang = localStorage.getItem('language')
  if (savedLang) {
    locale.value = savedLang
  }

  // 检查logo
  const img = new Image()
  img.src = '/src/assets/logo.svg'
  img.onload = () => { hasLogo.value = true }
})
</script>

<style scoped>
.user-footer {
  background: #fff;
  border-top: 1px solid #ebeef5;
  padding: 40px 0 20px;
  margin-top: auto;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  gap: 40px;
  margin-bottom: 30px;
}

.footer-brand {
  flex: 1;
  max-width: 300px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.logo-img {
  width: 32px;
  height: 32px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.brand-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.footer-links {
  display: flex;
  gap: 60px;
}

.link-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.group-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.link-group a {
  font-size: 14px;
  color: #606266;
  text-decoration: none;
  transition: color 0.3s;
}

.link-group a:hover {
  color: #409eff;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
}

.copyright {
  font-size: 13px;
  color: #909399;
}

.footer-extra {
  font-size: 13px;
  color: #c0c4cc;
}

@media screen and (max-width: 768px) {
  .user-footer {
    display: none;
  }
}
</style>