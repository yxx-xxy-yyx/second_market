<template>
  <el-footer class="footer">
    <div class="footer-container">
      <div class="footer-content">
        <div class="footer-left">
          <div class="copyright">
            © 2026 VueSpringBase. 毕业设计项目演示
          </div>
        </div>
        
        <div class="footer-right">
          <div class="links">
            <a href="#" class="link-item">关于我们</a>
            <a href="#" class="link-item">联系方式</a>
            <a href="#" class="link-item">使用条款</a>
            <a href="#" class="link-item">隐私政策</a>
          </div>

        </div>
      </div>
    </div>
  </el-footer>
</template>

<script setup>
import { ref } from 'vue'

// 获取构建时间（实际项目中可以从环境变量获取）
const buildTime = ref(new Date().toLocaleDateString('zh-CN'))
</script>

<style scoped>
.footer {
  background: #303133;
  color: #e4e7ed;
  padding: 30px 0 20px;
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
  align-items: flex-start;
  margin-bottom: 20px;
}

.footer-left .copyright {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #f0f2f5;
}

.footer-left .tech-stack {
  font-size: 14px;
  color: #909399;
}

.footer-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.links {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.link-item {
  color: #c0c4cc;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.link-item:hover {
  color: #409eff;
}

.social-links {
  display: flex;
  gap: 12px;
}

.social-icon {
  cursor: pointer;
  color: #909399;
  transition: color 0.3s;
}

.social-icon:hover {
  color: #409eff;
}

.footer-divider {
  height: 1px;
  background: #4c4d4f;
  margin: 20px 0;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.beian-link {
  color: #909399;
  text-decoration: none;
  transition: color 0.3s;
}

.beian-link:hover {
  color: #c0c4cc;
}

/* 响应式设计 */
@media (max-width: 767px) {
  .footer-content {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .footer-right {
    align-items: center;
  }
  
  .footer-bottom {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .links {
    flex-wrap: wrap;
    justify-content: center;
  }
}

@media (min-width: 768px) and (max-width: 1023px) {
  .footer-container {
    padding: 0 18px;
  }
}

@media (max-width: 480px) {
  .footer {
    padding: 20px 0 15px;
  }
  
  .footer-container {
    padding: 0 15px;
  }
  
  .links {
    gap: 15px;
  }
  
  .footer-left .copyright {
    font-size: 14px;
  }
}
</style>
