import { computed } from 'vue'
import { useWindowSize } from '@vueuse/core'

export const BREAKPOINTS = {
    mobileMax: 767,
    tabletMin: 768,
    tabletMax: 1023,
    desktopMin: 1024
}

export function useDeviceType() {
    const { width } = useWindowSize()

    const isMobileScreen = computed(() => width.value < BREAKPOINTS.tabletMin)
    const isTabletScreen = computed(() => width.value >= BREAKPOINTS.tabletMin && width.value < BREAKPOINTS.desktopMin)
    const isDesktopScreen = computed(() => width.value >= BREAKPOINTS.desktopMin)

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
