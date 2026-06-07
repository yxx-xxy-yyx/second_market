import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useWindowSize } from '@vueuse/core'

export const BREAKPOINTS = {
    mobileMax: 767,
    tabletMin: 768,
    tabletMax: 1023,
    desktopMin: 1024
}

export function useDeviceType() {
    const { width: vuesUseWidth } = useWindowSize()
    // 创建一个本地的响应式宽度，确保能捕捉到所有 resize 事件
    const width = ref(typeof window !== 'undefined' ? window.innerWidth : 0)

    const isMobileScreen = computed(() => width.value < BREAKPOINTS.tabletMin)
    const isTabletScreen = computed(() => width.value >= BREAKPOINTS.tabletMin && width.value < BREAKPOINTS.desktopMin)
    const isDesktopScreen = computed(() => width.value >= BREAKPOINTS.desktopMin)

    // 强制更新宽度，处理 vueuse 可能失效的情况
    const handleResize = () => {
        const newWidth = window.innerWidth
        if (width.value !== newWidth) {
            width.value = newWidth
            if (import.meta.env.DEV) {
                console.log('[useDeviceType] Width updated:', newWidth, 'isMobile:', isMobileScreen.value)
            }
        }
    }

    let rafId = null
    let mediaChangeHandler = null

    onMounted(() => {
        // 初始化宽度
        width.value = window.innerWidth
        if (import.meta.env.DEV) {
            console.log('[useDeviceType] Initialized width:', width.value)
        }

        // 同时监听 resize 和 orientationchange 事件
        window.addEventListener('resize', handleResize)
        window.addEventListener('orientationchange', handleResize)

        // 为浏览器开发者工具响应式模式变化添加额外监听
        // 在下一帧检查宽度是否变化
        const checkWidthChange = () => {
            handleResize()
            rafId = requestAnimationFrame(checkWidthChange)
        }
        rafId = requestAnimationFrame(checkWidthChange)

        // 监听媒体查询变化（备选方案）
        const mobileMedia = window.matchMedia(`(max-width: ${BREAKPOINTS.mobileMax}px)`)
        const tabletMedia = window.matchMedia(`(min-width: ${BREAKPOINTS.tabletMin}px) and (max-width: ${BREAKPOINTS.tabletMax}px)`)
        const desktopMedia = window.matchMedia(`(min-width: ${BREAKPOINTS.desktopMin}px)`)

        mediaChangeHandler = () => {
            handleResize()
        }

        if (mobileMedia.addEventListener) {
            mobileMedia.addEventListener('change', mediaChangeHandler)
            tabletMedia.addEventListener('change', mediaChangeHandler)
            desktopMedia.addEventListener('change', mediaChangeHandler)
        }
    })

    onUnmounted(() => {
        if (rafId) {
            cancelAnimationFrame(rafId)
        }
        window.removeEventListener('resize', handleResize)
        window.removeEventListener('orientationchange', handleResize)

        // 移除媒体查询监听
        const mobileMedia = window.matchMedia(`(max-width: ${BREAKPOINTS.mobileMax}px)`)
        const tabletMedia = window.matchMedia(`(min-width: ${BREAKPOINTS.tabletMin}px) and (max-width: ${BREAKPOINTS.tabletMax}px)`)
        const desktopMedia = window.matchMedia(`(min-width: ${BREAKPOINTS.desktopMin}px)`)

        if (mediaChangeHandler && mobileMedia.removeEventListener) {
            mobileMedia.removeEventListener('change', mediaChangeHandler)
            tabletMedia.removeEventListener('change', mediaChangeHandler)
            desktopMedia.removeEventListener('change', mediaChangeHandler)
        }
    })

    return {
        width,
        isMobileScreen,
        isTabletScreen,
        isDesktopScreen
    }
}

export function isMobile() {
    return window.innerWidth < BREAKPOINTS.tabletMin
}
