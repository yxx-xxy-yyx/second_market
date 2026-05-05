<script setup>
import { useI18n } from 'vue-i18n'
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useSchoolStore } from '@/stores/school'

const { locale, t } = useI18n()
const schoolStore = useSchoolStore()

const savedLang = localStorage.getItem('language')
if (savedLang) {
  locale.value = savedLang
}

// 当前语言名称
const currentLangName = computed(() => {
  return {
    zh: t('lang.zh'),
    en: t('lang.en'),
    ko: t('lang.ko')
  }[locale.value]
})

// 当前语言图标
const currentLangIcon = computed(() => {
  const flagMap = {
    zh: 'fi fi-cn',  // 中国
    en: 'fi fi-gb',  // 英国
    ko: 'fi fi-kr'   // 韩国
  }
  return flagMap[locale.value] || 'fi fi-cn'
})

// 切换语言
const handleLangCommand = (lang) => {
  locale.value = lang
  localStorage.setItem('language', lang)
  schoolStore.setLanguage(lang)
  ElMessage.success(t('common.languageSwitched'))
}
</script>

<template>
    <el-dropdown @command="handleLangCommand" trigger="click" class="lang-dropdown">
    <div class="lang-btn">
      <div class="lang-icon-wrapper">
        <span :class="['lang-icon-text', currentLangIcon]"></span>
      </div>
      <span class="lang-text">{{ currentLangName }}</span>
      <el-icon class="lang-arrow"><ArrowDown /></el-icon>
    </div>

    <template #dropdown>
      <el-dropdown-menu class="lang-menu">
        <el-dropdown-item command="zh" :class="{ active: locale === 'zh' }">
          <span class="lang-item-icon fi fi-cn"></span> {{ $t('lang.zhWithEn') }}
        </el-dropdown-item>
        <el-dropdown-item command="ko" :class="{ active: locale === 'ko' }">
          <span class="lang-item-icon fi fi-kr"></span> {{ $t('lang.koWithEn') }}
        </el-dropdown-item>
        <el-dropdown-item command="en" :class="{ active: locale === 'en' }">
          <span class="lang-item-icon fi fi-gb"></span> {{ $t('lang.enWithEn') }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<style scoped>
/* 语言切换样式 */
.lang-btn {
  font-size: 14px;
  color: #333;
}
.lang-dropdown {
  margin-right: 5px;
  cursor: pointer;
  display: inline-block;
  vertical-align: middle;
}

.lang-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #303133;
  padding: 4px 10px;
  height: 32px;
  border-radius: 20px;
  background: rgba(245, 247, 250, 0.8);
  border: 1px solid #ebeef5;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.lang-btn:hover {
  background: #fff;
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.lang-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  font-size: 16px;
}

.lang-text {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
}

.lang-arrow {
  font-size: 12px;
  color: #909399;
  transition: transform 0.3s;
}

.lang-dropdown:hover .lang-arrow {
  transform: rotate(180deg);
  color: #409eff;
}

.lang-menu .el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 16px;
  font-size: 14px;
}

.lang-menu .el-dropdown-menu__item.active {
  color: #409eff;
  background-color: #ecf5ff;
}

.lang-item-icon {
  font-size: 18px;
  margin-right: 6px;
}
</style>
