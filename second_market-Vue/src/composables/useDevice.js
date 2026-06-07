/**
 * 设备检测Hook - Device Detection Composable
 * 用于检测当前设备的屏幕尺寸和类型
 */
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useWindowSize } from '@vueuse/core'

// 响应式断点配置
const BREAKPOINTS = {
  xs: 0,
  sm: 576,
  md: 768,
  lg: 992,
  xl: 1200,
  xxl: 1400
}

// 设备类型
const DEVICE_TYPES = {
  MOBILE: 'mobile',
  TABLET: 'tablet',
  DESKTOP: 'desktop'
}

export function useDeviceType() {
  const { width } = useWindowSize()
  
  // 计算当前设备类型
  const deviceType = computed(() => {
    if (width.value < BREAKPOINTS.md) {
      return DEVICE_TYPES.MOBILE
    } else if (width.value < BREAKPOINTS.lg) {
      return DEVICE_TYPES.TABLET
    } else {
      return DEVICE_TYPES.DESKTOP
    }
  })
  
  // 是否是移动端
  const isMobile = computed(() => deviceType.value === DEVICE_TYPES.MOBILE)
  
  // 是否是平板
  const isTablet = computed(() => deviceType.value === DEVICE_TYPES.TABLET)
  
  // 是否是桌面端
  const isDesktop = computed(() => deviceType.value === DEVICE_TYPES.DESKTOP)
  
  // 是否是移动端或平板（需要移动端布局）
  const isMobileScreen = isMobile
  const isTabletScreen = isTablet
  
  // 当前断点名称
  const currentBreakpoint = computed(() => {
    if (width.value < BREAKPOINTS.sm) return 'xs'
    if (width.value < BREAKPOINTS.md) return 'sm'
    if (width.value < BREAKPOINTS.lg) return 'md'
    if (width.value < BREAKPOINTS.xl) return 'lg'
    if (width.value < BREAKPOINTS.xxl) return 'xl'
    return 'xxl'
  })
  
  // 是否在某个断点以上
  const isAbove = (breakpoint) => {
    return computed(() => width.value >= BREAKPOINTS[breakpoint])
  }
  
  // 是否在某个断点以下
  const isBelow = (breakpoint) => {
    return computed(() => width.value < BREAKPOINTS[breakpoint])
  }
  
  // 屏幕宽度
  const screenWidth = width
  
  // 屏幕高度
  const { height: screenHeight } = useWindowSize()
  
  return {
    // 设备类型
    deviceType,
    isMobile,
    isTablet,
    isDesktop,
    
    // 响应式布局专用
    isMobileScreen,
    isTabletScreen,
    
    // 当前断点
    currentBreakpoint,
    
    // 辅助函数
    isAbove,
    isBelow,
    
    // 屏幕尺寸
    screenWidth,
    screenHeight,
    
    // 常量
    BREAKPOINTS,
    DEVICE_TYPES
  }
}

// 响应式值Hook - 用于在模板中直接使用响应式断点
export function useResponsiveValue(mobileValue, tabletValue, desktopValue) {
  const { isMobile, isTablet } = useDeviceType()
  
  return computed(() => {
    if (isMobile.value) return mobileValue
    if (isTablet.value) return tabletValue
    return desktopValue
  })
}

// 栅格系统Hook
export function useGrid(columns = 12, gap = 16) {
  const { deviceType, screenWidth } = useDeviceType()
  
  // 根据设备类型返回合适的列数
  const responsiveColumns = computed(() => {
    switch (deviceType.value) {
      case DEVICE_TYPES.MOBILE:
        return 1
      case DEVICE_TYPES.TABLET:
        return Math.min(columns, 6)
      case DEVICE_TYPES.DESKTOP:
      default:
        return columns
    }
  })
  
  // 根据设备类型返回合适的间距
  const responsiveGap = computed(() => {
    switch (deviceType.value) {
      case DEVICE_TYPES.MOBILE:
        return gap * 0.75
      case DEVICE_TYPES.TABLET:
        return gap
      case DEVICE_TYPES.DESKTOP:
      default:
        return gap * 1.25
    }
  })
  
  return {
    columns: responsiveColumns,
    gap: responsiveGap,
    deviceType
  }
}

// 横竖屏检测
export function useOrientation() {
  const orientation = ref('landscape')
  const isLandscape = computed(() => orientation.value === 'landscape')
  const isPortrait = computed(() => orientation.value === 'portrait')
  
  const updateOrientation = () => {
    orientation.value = window.innerHeight > window.innerWidth ? 'portrait' : 'landscape'
  }
  
  onMounted(() => {
    updateOrientation()
    window.addEventListener('resize', updateOrientation)
  })
  
  onUnmounted(() => {
    window.removeEventListener('resize', updateOrientation)
  })
  
  return {
    orientation,
    isLandscape,
    isPortrait
  }
}

// 触摸设备检测
export function useTouchDevice() {
  const isTouchDevice = ref(false)
  
  onMounted(() => {
    isTouchDevice.value = 'ontouchstart' in window || navigator.maxTouchPoints > 0
  })
  
  return {
    isTouchDevice
  }
}

// 高DPI屏幕检测
export function useHighDPI() {
  const isHighDPI = computed(() => {
    if (typeof window === 'undefined') return false
    return window.devicePixelRatio && window.devicePixelRatio >= 2
  })
  
  const pixelRatio = computed(() => {
    if (typeof window === 'undefined') return 1
    return window.devicePixelRatio || 1
  })
  
  return {
    isHighDPI,
    pixelRatio
  }
}
