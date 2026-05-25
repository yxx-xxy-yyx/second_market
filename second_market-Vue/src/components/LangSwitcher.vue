<script setup>
import { useI18n } from 'vue-i18n'
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useSchoolStore } from '@/stores/school'

const props = defineProps({
  glass: {
    type: Boolean,
    default: false
  }
})

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
  <el-dropdown @command="handleLangCommand" trigger="click" class="lang-dropdown"
    :popper-class="props.glass ? 'lang-glass-popper' : 'lang-white-popper'">
    <div class="lang-btn" :class="{ 'lang-btn-glass': props.glass }">
      <div class="lang-icon-wrapper">
        <span :class="['lang-icon-text', currentLangIcon]"></span>
      </div>
      <span class="lang-text">{{ currentLangName }}</span>
      <el-icon class="lang-arrow">
        <ArrowDown />
      </el-icon>
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
  padding: 10px 16px;
  min-height: 38px;
  border-radius: 18px;
  background: rgba(245, 247, 250, 0.95);
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

.lang-menu {
  min-width: 160px;
  border-radius: 18px;
  background: #ffffff;
  border: 1px solid #ebeef5;
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
  padding: 8px 0;
  overflow: hidden;
}

.lang-menu .el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  font-size: 13px;
  color: #303133;
  transition: background 0.2s ease, color 0.2s ease;
  border-radius: 0;
}

.lang-menu .el-dropdown-menu__item:first-child {
  border-top-left-radius: 18px;
  border-top-right-radius: 18px;
}

.lang-menu .el-dropdown-menu__item:last-child {
  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 18px;
}

.lang-menu .el-dropdown-menu__item.active {
  color: #409eff;
  background-color: #ecf5ff;
}

.lang-menu .el-dropdown-menu__item:not(.active):hover {
  background: #f5f7fa;
}

:global(.lang-white-popper.el-popper) {
  background: transparent;
  border-radius: 18px;
  overflow: hidden;
}

:global(.lang-white-popper .el-dropdown-menu) {
  border-radius: 18px;
  background: #ffffff;
  border: 1px solid #ebeef5;
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
  padding: 8px 0;
}

.lang-item-icon {
  font-size: 18px;
  margin-right: 6px;
}

.lang-btn-glass {
  min-height: 38px;
  padding: 10px 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 18px;
  background: rgba(77, 86, 151, 0.28);
  color: white;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 10px 24px rgba(59, 60, 118, 0.18);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}

.lang-btn-glass:hover {
  border-color: rgba(255, 255, 255, 0.32);
  background: rgba(77, 86, 151, 0.34);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.24), 0 10px 24px rgba(59, 60, 118, 0.2);
}

.lang-btn-glass .lang-text {
  color: white;
  font-size: 13px;
}

.lang-btn-glass .lang-arrow {
  color: rgba(255, 255, 255, 0.82);
  font-size: 12px;
}

:global(.lang-glass-popper.el-popper) {
  border: none !important;
  border-radius: 18px;
  overflow: hidden;
  background: transparent !important;
  box-shadow: none !important;
  padding: 0;
}

:global(.lang-glass-popper .el-dropdown-menu) {
  min-width: 160px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 18px;
  background: rgba(77, 86, 151, 0.45);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.12);
  padding: 8px 0;
  overflow: hidden;
}

:global(.lang-glass-popper .el-dropdown-menu__item) {
  color: rgba(255, 255, 255, 0.86);
  font-size: 14px;
  padding: 5px 16px;
  display: flex;
  align-items: center;
  gap: 0;
  border-radius: 0;
  transition: background 0.2s ease, color 0.2s ease;
}

:global(.lang-glass-popper .el-dropdown-menu__item:first-child) {
  border-top-left-radius: 18px;
  border-top-right-radius: 18px;
}

:global(.lang-glass-popper .el-dropdown-menu__item:last-child) {
  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 18px;
}

:global(.lang-glass-popper .el-dropdown-menu__item:not(.is-disabled):focus),
:global(.lang-glass-popper .el-dropdown-menu__item:not(.is-disabled):hover) {
  color: white;
  background: rgba(255, 255, 255, 0.14);
}

:global(.lang-glass-popper .el-dropdown-menu__item.active) {
  color: white;
  background: rgba(255, 255, 255, 0.18);
}

:global(.lang-glass-popper .el-dropdown-menu__item.active:first-child) {
  border-top-left-radius: 18px;
  border-top-right-radius: 18px;
}

:global(.lang-glass-popper .el-dropdown-menu__item.active:last-child) {
  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 18px;
}

:global(.lang-glass-popper .el-popper__arrow::before) {
  border-color: rgba(255, 255, 255, 0.2);
  background: rgba(77, 86, 151, 0.7);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
}
</style>
