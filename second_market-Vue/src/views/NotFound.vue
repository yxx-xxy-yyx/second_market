<template>
  <div class="not-found-page">
    <div class="not-found-container">
      <div class="not-found-content">
        <!-- 404图标 -->
        <div class="not-found-icon">
          <div class="number-404">
            <span>4</span>
            <span class="zero">0</span>
            <span>4</span>
          </div>
        </div>
        
        <!-- 错误信息 -->
        <div class="not-found-info">
          <h1 class="not-found-title">{{ $t('notFound.pageTitle') }}</h1>
          <p class="not-found-description">
            {{ $t('notFound.description') }}
          </p>
          <p class="not-found-suggestion">
            {{ $t('notFound.suggestion') }}
          </p>
        </div>
        
        <!-- 操作按钮 -->
        <div class="not-found-actions">
          <el-button type="primary" size="large" @click="goHome">
            <el-icon><House /></el-icon>
            {{ $t('notFound.goHome') }}
          </el-button>
          <el-button size="large" @click="goBack" class="ml-20">
            <el-icon><ArrowLeft /></el-icon>
            {{ $t('notFound.goBack') }}
          </el-button>
        </div>
        
        <!-- 推荐链接 -->
        <div class="not-found-links">
          <h3>{{ $t('notFound.interestTitle') }}</h3>
          <div class="link-grid">
            <router-link to="/" class="link-item">
              <el-icon><House /></el-icon>
              <span>{{ $t('notFound.homePage') }}</span>
            </router-link>
            <router-link to="/login" class="link-item">
              <el-icon><User /></el-icon>
              <span>{{ $t('notFound.login') }}</span>
            </router-link>
            <router-link to="/register" class="link-item">
              <el-icon><UserFilled /></el-icon>
              <span>{{ $t('notFound.register') }}</span>
            </router-link>
          </div>
        </div>
      </div>
      
      <!-- 装饰元素 -->
      <div class="decoration">
        <div class="float-element element-1"></div>
        <div class="float-element element-2"></div>
        <div class="float-element element-3"></div>
        <div class="float-element element-4"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { House, ArrowLeft, User, UserFilled } from '@element-plus/icons-vue'
import { ElLoading } from 'element-plus'

const router = useRouter()
const loading = ref(false)

// 返回首页
const goHome = async () => {
  const loadingInstance = ElLoading.service({
    lock: true,
    text: $t('notFound.jumping'), // 新增i18n动态文本
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    await router.push('/')
  } catch (error) {
    console.error('导航到首页失败:', error)
    window.location.href = '/' // 如果路由失败，使用硬刷新
  } finally {
    loadingInstance.close()
  }
}

// 返回上一页
const goBack = async () => {
  const loadingInstance = ElLoading.service({
    lock: true,
    text: $t('notFound.returning'), // 新增i18n动态文本
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    if (window.history.length > 1) {
      await router.go(-1)
    } else {
      await router.push('/')
    }
  } catch (error) {
    console.error('返回上一页失败:', error)
    window.history.back() // 如果路由失败，使用浏览器API
  } finally {
    loadingInstance.close()
  }
}
</script>

<style scoped>
.not-found-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.not-found-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 800px;
  padding: 40px 20px;
}

.not-found-content {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
}

/* 404图标 */
.not-found-icon {
  margin-bottom: 30px;
}

.number-404 {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  font-size: 120px;
  font-weight: 900;
  color: #409eff;
  text-shadow: 0 5px 15px rgba(64, 158, 255, 0.3);
}

.number-404 span {
  display: inline-block;
  animation: bounce 2s infinite;
}

.number-404 .zero {
  animation-delay: 0.2s;
}

.number-404 span:last-child {
  animation-delay: 0.4s;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-20px);
  }
  60% {
    transform: translateY(-10px);
  }
}

/* 错误信息 */
.not-found-info {
  margin-bottom: 40px;
}

.not-found-title {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 15px 0;
}

.not-found-description {
  font-size: 18px;
  color: #606266;
  margin: 0 0 10px 0;
  line-height: 1.6;
}

.not-found-suggestion {
  font-size: 14px;
  color: #909399;
  margin: 0;
  line-height: 1.5;
}

/* 操作按钮 */
.not-found-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-bottom: 40px;
}

.ml-20 {
  margin-left: 20px;
}

/* 推荐链接 */
.not-found-links {
  border-top: 1px solid #e4e7ed;
  padding-top: 30px;
}

.not-found-links h3 {
  font-size: 16px;
  font-weight: 500;
  color: #606266;
  margin: 0 0 20px 0;
}

.link-grid {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.link-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border-radius: 12px;
  background: #f8f9fa;
  color: #606266;
  text-decoration: none;
  transition: all 0.3s;
  min-width: 100px;
  cursor: pointer;
}

.link-item:hover {
  background: #409eff;
  color: white;
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(64, 158, 255, 0.3);
}

.link-item .el-icon {
  font-size: 24px;
}

.link-item span {
  font-size: 14px;
  font-weight: 500;
}

/* 装饰元素 */
.decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.float-element {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: float 6s ease-in-out infinite;
}

.element-1 {
  width: 60px;
  height: 60px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.element-2 {
  width: 80px;
  height: 80px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.element-3 {
  width: 40px;
  height: 40px;
  bottom: 30%;
  left: 20%;
  animation-delay: 4s;
}

.element-4 {
  width: 100px;
  height: 100px;
  top: 10%;
  right: 10%;
  animation-delay: 1s;
}

@keyframes float {
  0%, 100% { 
    transform: translateY(0px) rotate(0deg); 
  }
  50% { 
    transform: translateY(-20px) rotate(180deg); 
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .not-found-content {
    padding: 40px 30px;
  }
  
  .number-404 {
    font-size: 80px;
  }
  
  .not-found-title {
    font-size: 24px;
  }
  
  .not-found-description {
    font-size: 16px;
  }
  
  .not-found-actions {
    flex-direction: column;
    gap: 15px;
  }
  
  .ml-20 {
    margin-left: 0;
  }
  
  .link-grid {
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }
  
  .link-item {
    flex-direction: row;
    justify-content: center;
    width: 200px;
  }
}

@media (max-width: 480px) {
  .not-found-container {
    padding: 20px 15px;
  }
  
  .not-found-content {
    padding: 30px 20px;
  }
  
  .number-404 {
    font-size: 60px;
  }
  
  .not-found-title {
    font-size: 20px;
  }
}
</style>