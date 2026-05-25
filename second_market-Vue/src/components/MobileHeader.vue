<template>
    <div :class="['user-header', { scrolled, glass: isGlass }]">
        <div class="header-container">
            <div class="header-left">
                <div class="logo" @click="router.push('/user/dashboard')">
                    <BrandLogo :title="t('common.projectName')" class="brand" />
                </div>

                <div class="mobile-left-text">
                    {{ leftTitle }}
                </div>

                <div class="search-box hidden lg:block" @click="router.push('/user/search')" style="cursor: pointer;">
                    <div class="search-input-placeholder">
                        <el-icon>
                            <Search />
                        </el-icon>
                        <span class="text-gray-400 text-sm">{{ t('nav.searchProducts') }}</span>
                    </div>
                </div>
            </div>

            <div class="header-nav hidden lg:flex">
                <div v-for="item in navMenu" :key="item.path"
                    :class="['nav-item', { active: isActive(item.path) }, currentLang]" @click="router.push(item.path)">
                    {{ item.label }}
                </div>
            </div>

            <div class="header-right">
                <template v-if="currentTab === 'home'">
                    <LangSwitcher :glass="isGlass" />
                    <div class="icon-btn" @click="goNotices">
                        <el-icon :size="20">
                            <Bell />
                        </el-icon>
                    </div>
                    <div class="icon-btn" @click="router.push('/user/search')">
                        <el-icon :size="20">
                            <Search />
                        </el-icon>
                    </div>
                </template>

                <template v-else-if="currentTab === 'categories'">
                    <LangSwitcher :glass="isGlass" />
                    <el-select v-model="selectedSchool" :placeholder="t('nav.selectSchool')" class="school-select"
                        filterable clearable
                        :popper-class="isGlass ? 'school-mobile-glass-popper' : 'school-mobile-white-popper'"
                        @change="handleSchoolChange">
                        <el-option v-for="item in schoolStore.schoolList" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                    <div class="icon-btn" @click="goNotices">
                        <el-icon :size="20">
                            <Bell />
                        </el-icon>
                    </div>
                </template>

                <template v-else-if="currentTab === 'message'">
                    <LangSwitcher :glass="isGlass" />
                </template>

                <template v-else>
                    <div class="profile-controls"
                        style="display:flex; flex-direction:row; gap:12px; align-items:center;">
                        <LangSwitcher :glass="isGlass" />
                        <el-dropdown @command="handleUserCommand" trigger="click"
                            :popper-class="isGlass ? 'user-dropdown-glass' : 'user-dropdown-white'">
                            <div class="user-info">
                                <el-avatar :size="32" :src="avatarUrl">
                                    <el-icon>
                                        <User />
                                    </el-icon>
                                </el-avatar>
                                <span class="username">{{ userStore.user?.nickname || userStore.user?.username }}</span>
                                <el-icon class="dropdown-icon" :size="14">
                                    <ArrowDown />
                                </el-icon>
                            </div>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item command="profile">
                                        <el-icon>
                                            <User />
                                        </el-icon>
                                        {{ t('common.profile') }}
                                    </el-dropdown-item>
                                    <el-dropdown-item divided command="logout">
                                        <el-icon>
                                            <SwitchButton />
                                        </el-icon>
                                        {{ t('common.logout') }}
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </template>
            </div>
        </div>

        <el-drawer title="" v-model="drawerVisible" direction="rtl" size="280px">
            <!-- 占位抽屉内容，后续可填充 -->
        </el-drawer>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { formatAvatarUrl } from '@/utils/url'
import { messageApi } from '@/api/message'
import { chatApi } from '@/api/chat'
import { useI18n } from 'vue-i18n'
import { useSchoolStore } from '@/stores/school'
import { useDeviceType } from '@/utils/device'
import LangSwitcher from '@/components/LangSwitcher.vue'
import BrandLogo from '@/components/BrandLogo.vue'
import {
    Search,
    Bell,
    User,
    SwitchButton,
    Plus,
    Setting
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { t, locale } = useI18n()
const currentLang = locale.value
const schoolStore = useSchoolStore()
const { isMobileScreen } = useDeviceType()

const unreadCount = ref(0)

const drawerVisible = ref(false)
const scrolled = ref(false)

const currentTab = computed(() => {
    const p = route.path
    if (p === '/user/dashboard' || p.startsWith('/user/dashboard')) return 'home'
    if (p === '/user/categories' || p.startsWith('/user/categories')) return 'categories'
    if (p === '/user/publish') return 'publish'
    if (p === '/user/messageChat' || p.startsWith('/user/message')) return 'message'
    if (p === '/user/profile' || p.startsWith('/user/profile')) return 'profile'
    return 'home'
})

const leftTitle = computed(() => {
    switch (currentTab.value) {
        case 'home': return t('nav.home')
        case 'categories': return schoolStore.selectedSchool ? schoolStore.getSchoolName(schoolStore.selectedSchool) : (t('nav.campus') || '校园')
        case 'message': return t('nav.chat')
        case 'profile': return t('nav.myAccount')
        default: return t('nav.home')
    }
})

const isGlass = computed(() => isMobileScreen.value && scrolled.value)

const navMenu = computed(() => [
    { label: t('nav.home'), path: '/user/dashboard' },
    { label: t('nav.products'), path: '/user/products' },
    { label: t('nav.myProducts'), path: '/user/my-products' },
    { label: t('nav.myOrders'), path: '/user/orders' },
    { label: t('nav.myAccount'), path: '/user/profile' }
])

const selectedSchool = computed({
    get: () => schoolStore.selectedSchool,
    set: (value) => schoolStore.setSchool(value)
})

const handleSchoolChange = (value) => {
    schoolStore.setSchool(value)
}

onMounted(() => {
    if (!schoolStore.schoolList.length) {
        schoolStore.getSchoolList()
    }
})

watch(locale, async () => {
    await schoolStore.getSchoolList()
})

const avatarUrl = computed(() => formatAvatarUrl(userStore.user?.avatar))

const isActive = (path) => {
    return route.path === path || route.path.startsWith(path + '/')
}

const handleUserCommand = async (command) => {
    switch (command) {
        case 'profile':
            router.push('/user/profile')
            break
        case 'logout':
            userStore.logout()
            router.replace('/login')
            break
    }
}

const fetchUnreadCount = async () => {
    if (!userStore.isLoggedIn) return
    try {
        const [msgRes, chatRes] = await Promise.all([
            messageApi.getUnreadCount(),
            chatApi.getUnreadCount()
        ])

        let total = 0
        if (msgRes.code === '200') total += (msgRes.data || 0)
        if (chatRes.code === '200') total += (chatRes.data || 0)

        unreadCount.value = total
    } catch (error) {
        unreadCount.value = 0
    }
}

onMounted(() => {
    fetchUnreadCount()
    setInterval(fetchUnreadCount, 60000)
})

onMounted(() => {
    window.addEventListener('scroll', onScroll, { passive: true })
})

onUnmounted(() => {
    window.removeEventListener('scroll', onScroll)
})

const onScroll = () => {
    scrolled.value = window.scrollY > 8
}

const goNotices = () => {
    router.push('/user/notices')
}
</script>

<style scoped>
.user-header {
    height: calc(65px + env(safe-area-inset-top));
    padding-top: env(safe-area-inset-top);
    background: var(--app-header-gradient);
    box-shadow: 0 10px 26px rgba(15, 23, 42, 0.16);
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1200;
    border-bottom: none;
    transition: background 0.25s ease, box-shadow 0.25s ease, backdrop-filter 0.25s ease;
}

.user-header.glass {
    background: linear-gradient(135deg, rgba(54, 179, 194, 0.80) 0%, rgba(38, 143, 156, 0.80) 100%);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    box-shadow: 0 12px 30px rgba(15, 23, 42, 0.18);
}

.header-container {
    height: 100%;
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20px;
    flex-wrap: nowrap;
    overflow: hidden;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 20px;
    flex-shrink: 0;
    min-width: 0;
}

.logo {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    transition: opacity 0.3s;
    color: var(--primary-color);
}

.logo:hover {
    opacity: 0.8;
}

.mobile-left-text {
    display: none;
    font-size: 16px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.95);
}

.brand {
    display: inline-flex;
    align-items: center;
    min-width: 0;
}

.search-box {
    width: 200px;
}

.search-input-placeholder {
    display: flex;
    align-items: center;
    gap: 6px;
    height: 32px;
    padding: 0 12px;
    border-radius: 20px;
    background-color: #f5f7fa;
    border: 1px solid transparent;
    transition: all 0.3s;
}

.search-input-placeholder:hover {
    border-color: #c0c4cc;
    background-color: #fff;
}

.header-nav {
    display: flex;
    align-items: center;
    gap: 16px;
    flex: 0 1 auto;
    justify-content: center;
    min-width: 0;
    overflow-x: auto;
    scrollbar-width: none;
}

.header-nav::-webkit-scrollbar {
    display: none;
}

.nav-item {
    font-size: 14px;
    color: #606266;
    cursor: pointer;
    padding: 8px 8px;
    border-radius: 8px;
    transition: all 0.3s;
    position: relative;
    white-space: nowrap;
}

.nav-item.en {
    font-size: 11.4px;
    padding: 8px 3px;
}

.nav-item.ko {
    padding: 8px 13px;
}

.nav-item.zh {
    padding: 8px 8px;
}

.nav-item:hover {
    color: #409eff;
    background-color: #f0f7ff;
}

.nav-item.active {
    color: #409eff;
    font-weight: 500;
}

.nav-item.active::after {
    content: '';
    position: absolute;
    bottom: -8px;
    left: 50%;
    transform: translateX(-50%);
    width: 40px;
    height: 3px;
    background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
    border-radius: 2px;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-shrink: 0;
    flex-direction: row-reverse;
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

.publish-btn {
    border-radius: 20px;
    padding: 10px 5px;
    font-weight: 500;
}

.message-badge {
    cursor: pointer;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 6px 12px;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s;
}

.user-info:hover {
    background-color: #f0f7ff;
}

.user-header.glass .user-info {
    background: rgba(255, 255, 255, 0.14);
    border: 1px solid rgba(255, 255, 255, 0.18);
}

.user-header.glass .user-info:hover {
    background: rgba(255, 255, 255, 0.22);
}

:global(.user-dropdown-white.el-popper) {
    border: none;
    box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
    border-radius: 18px;
    overflow: hidden;
    background: transparent;
}

:global(.user-dropdown-white .el-dropdown-menu) {
    border: none;
    border-radius: 18px;
    background: #ffffff;
    box-shadow: none;
    padding: 8px 0;
    overflow: hidden;
}

:global(.user-dropdown-glass.el-popper) {
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 18px;
    overflow: hidden;
    background: rgba(77, 86, 151, 0.42);
    box-shadow: 0 18px 40px rgba(59, 60, 118, 0.28), inset 0 1px 0 rgba(255, 255, 255, 0.24);
    backdrop-filter: blur(18px);
    -webkit-backdrop-filter: blur(18px);
}

:global(.user-dropdown-glass .el-dropdown-menu) {
    border: none;
    border-radius: 18px;
    background: transparent;
    box-shadow: none;
    padding: 8px 0;
    overflow: hidden;
}

:global(.user-dropdown-glass .el-dropdown-menu__item) {
    color: white;
    /* 设置字体和图标颜色为白色 */
}

:global(.user-dropdown-glass .el-dropdown-menu__item:not(.is-disabled):focus),
:global(.user-dropdown-glass .el-dropdown-menu__item:not(.is-disabled):hover) {
    background: rgba(255, 255, 255, 0.14);
    /* 调整悬停背景色 */
    color: white;
    /* 确保悬停时字体颜色仍为白色 */
}

:global(.user-dropdown-glass .el-dropdown-menu__item.active) {
    background: rgba(255, 255, 255, 0.18);
    /* 调整选中项背景色 */
    color: white;
    /* 确保选中时字体颜色仍为白色 */
}

.user-header.glass .user-info .username,
.user-header.glass .user-info .dropdown-icon {
    color: #ffffff;
}

.username {
    font-size: 14px;
    color: #606266;
    font-weight: 500;
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.dropdown-icon {
    color: #909399;
    transition: transform 0.3s;
}

.user-info:hover .dropdown-icon {
    transform: rotate(180deg);
}

@media (max-width: 1200px) {
    .header-container {
        gap: 16px;
    }

    .header-left {
        gap: 16px;
    }

    .header-nav {
        gap: 12px;
    }

    .search-box {
        width: 220px;
    }

    .nav-item {
        font-size: 14px;
        padding: 6px 12px;
    }
}

@media (max-width: 992px) {
    .header-container {
        gap: 12px;
    }

    .header-nav {
        gap: 8px;
        max-width: 300px;
    }

    .nav-item {
        font-size: 13px;
        padding: 6px 10px;
    }

    .search-box {
        width: 180px;
    }

    .publish-btn {
        padding: 8px 16px;
    }
}

@media (max-width: 768px) {
    .header-container {
        padding: 0 12px;
        gap: 8px;
    }

    .logo-text {
        display: none;
    }

    .logo {
        display: none;
    }

    .mobile-left-text {
        display: block;
        margin-left: 6px;
        font-size: 18px;
        line-height: calc(65px + env(safe-area-inset-top));
    }

    .header-nav {
        display: none;
    }

    .search-box {
        flex: 1;
        min-width: 120px;
        max-width: 200px;
    }

    .username {
        display: none;
    }

    .publish-btn {
        padding: 8px 12px;
        min-width: auto;
    }

    .publish-btn .el-icon {
        margin-right: 0;
    }
}

@media (max-width: 576px) {
    .header-container {
        padding: 0 10px;
        gap: 6px;
    }

    .search-box {
        max-width: 150px;
    }

    .header-right {
        gap: 8px;
    }

    .user-info {
        padding: 6px 8px;
    }
}

@media screen and (max-width: 768px) {
    .header-nav {
        display: none;
    }

    .publish-btn {
        display: none;
    }

    .username {
        display: none;
    }

    .icon-btn {
        width: 40px;
        height: 38px;
        border-radius: 12px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        color: #303133;
        cursor: pointer;
        transition: all 0.25s ease;
    }

    .icon-btn:hover {
        background: #f5f7fa;
    }

    .user-header.glass .icon-btn {
        background: rgba(77, 86, 151, 0.18);
        color: white;
        border: 1px solid rgba(255, 255, 255, 0.12);
        backdrop-filter: blur(12px);
    }

    .user-header.glass .icon-btn:hover {
        background: rgba(77, 86, 151, 0.22);
    }

    .school-select {
        width: 120px;
    }

    .school-select :deep(.el-input__wrapper) {
        min-height: 38px;
        padding: 10px 16px;
        /* Adjusted to match LangSwitcher button padding */
        border-radius: 18px;
        background: rgba(245, 247, 250, 0.95) !important;
        border: 1px solid #ebeef5;
        box-shadow: none;
        transition: all 0.3s;
    }

    .school-select :deep(.el-input__inner) {
        font-size: 13px !important;
        font-weight: 600;
        color: #606266 !important;
        padding: 0 6px;
    }

    /* 确保在白底模式下，学校选择器箭头颜色正常 */
    .school-select :deep(.el-input__suffix-inner .el-icon) {
        color: #909399;
    }

    .user-header.glass .school-select :deep(.el-input__wrapper) {
        background: rgba(77, 86, 151, 0.28) !important;
        border: 1px solid rgba(255, 255, 255, 0.2) !important;
        backdrop-filter: blur(14px);
        -webkit-backdrop-filter: blur(14px);
        box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 10px 24px rgba(59, 60, 118, 0.18);
        border-radius: 18px !important; /* 确保在毛玻璃模式下也应用圆角 */
    }

    .user-header.glass .school-select :deep(.el-input__inner) {
        color: #ffffff !important;
    }

    .user-header.glass .school-select :deep(.el-input__suffix .el-icon) {
        color: #ffffff !important;
    }
}

/* --- 手机端专用学校下拉框全局样式 (不影响 Web) --- */

/* 白底模式 */
:global(.school-mobile-white-popper.el-popper) {
    border: none !important;
    background: transparent !important;
    box-shadow: none !important;
}

:global(.school-mobile-white-popper .el-select-dropdown) {
    border: 1px solid #ebeef5 !important;
    border-radius: 18px !important;
    background: #ffffff !important;
    box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08) !important;
    padding: 8px 0 !important;
}

:global(.school-mobile-white-popper .el-select-dropdown__item) {
    font-size: 14px !important;
    padding: 5px 16px !important;
    color: #303133 !important;
}

:global(.school-mobile-white-popper .el-select-dropdown__item.selected) {
    background: #ecf5ff !important;
    color: #409eff !important;
}

/* 毛玻璃模式 */
:global(.school-mobile-glass-popper.el-popper) {
    border: none !important;
    background: transparent !important;
    box-shadow: none !important;
}

:global(.school-mobile-glass-popper .el-select-dropdown) {
    border: 1px solid rgba(255, 255, 255, 0.2) !important;
    border-radius: 18px !important;
    background: rgba(77, 86, 151, 0.45) !important;
    backdrop-filter: blur(18px) !important;
    -webkit-backdrop-filter: blur(18px) !important;
    box-shadow: 0 20px 40px rgba(15, 23, 42, 0.12) !important;
    padding: 8px 0 !important;
}

:global(.school-mobile-glass-popper .el-select-dropdown__item) {
    color: rgba(255, 255, 255, 0.86) !important;
    font-size: 14px !important;
    padding: 5px 16px !important;
}

:global(.school-mobile-glass-popper .el-select-dropdown__item.hover),
:global(.school-mobile-glass-popper .el-select-dropdown__item:hover) {
    background: rgba(255, 255, 255, 0.14) !important;
    color: #fff !important;
}

:global(.school-mobile-glass-popper .el-select-dropdown__item.selected) {
    background: rgba(255, 255, 255, 0.18) !important; /* 恢复为与语言选择器一致的选中颜色 */
    /* 修复：在毛玻璃背景下，选中文字改用蓝色以提高辨识度 */
    color: #409eff !important;
    font-weight: 600 !important;
}

/* 选中项边缘圆角对齐 (填满框) */
:global(.school-mobile-white-popper .el-select-dropdown__item.selected:first-child),
:global(.school-mobile-glass-popper .el-select-dropdown__item.selected:first-child) {
    border-top-left-radius: 18px !important;
    border-top-right-radius: 18px !important;
}

:global(.school-mobile-white-popper .el-select-dropdown__item.selected:last-child),
:global(.school-mobile-glass-popper .el-select-dropdown__item.selected:last-child) {
    border-bottom-left-radius: 18px !important;
    border-bottom-right-radius: 18px !important;
}
</style>
