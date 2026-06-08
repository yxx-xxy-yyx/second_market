import { computed } from 'vue'
import { useWindowSize } from '@vueuse/core'

export const BREAKPOINTS = {
    mobileMax: 767,
    tabletMin: 768,
    tabletMax: 1023,
    desktopMin: 1024
}

/**
 * 响应式设备检测 Hook
 * @returns {Object} 设备状态 - 同时提供 isMobile/isMobileScreen 等多组别名，兼容新旧调用方
 */
export function useDeviceType() {
    const { width, height } = useWindowSize()

    const isMobileScreen = computed(() => width.value < BREAKPOINTS.tabletMin)
    const isTabletScreen = computed(() => width.value >= BREAKPOINTS.tabletMin && width.value < BREAKPOINTS.desktopMin)
    const isDesktopScreen = computed(() => width.value >= BREAKPOINTS.desktopMin)

    // 兼容别名（@/composables/useDevice 调用方式）
    const isMobile = isMobileScreen
    const isTablet = isTabletScreen
    const isDesktop = isDesktopScreen

    const screenWidth = width
    const screenHeight = height

    return {
        width,
        height,
        // 主别名（UserLayout / App.vue 使用）
        isMobileScreen,
        isTabletScreen,
        isDesktopScreen,
        // 兼容别名（ResponsiveLayout / AppTabs 使用）
        isMobile,
        isTablet,
        isDesktop,
        screenWidth,
        screenHeight
    }
}

/**
 * 同步函数版本（非响应式，适合一次性判断）
 */
export function isMobileWidth() {
    return window.innerWidth < BREAKPOINTS.tabletMin
}

// 兼容旧调用
export const isMobile = isMobileWidth
