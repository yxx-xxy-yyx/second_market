/**
 * 微交互动画工具 - Micro-interactions Utilities
 * 提供常用的微交互动画效果
 */

// 预设动画
export const animations = {
  // 淡入
  fadeIn: {
    from: { opacity: 0 },
    to: { opacity: 1 }
  },
  
  // 淡出
  fadeOut: {
    from: { opacity: 1 },
    to: { opacity: 0 }
  },
  
  // 向上滑入
  slideUp: {
    from: { opacity: 0, transform: 'translateY(20px)' },
    to: { opacity: 1, transform: 'translateY(0)' }
  },
  
  // 向下滑入
  slideDown: {
    from: { opacity: 0, transform: 'translateY(-20px)' },
    to: { opacity: 1, transform: 'translateY(0)' }
  },
  
  // 向左滑入
  slideLeft: {
    from: { opacity: 0, transform: 'translateX(20px)' },
    to: { opacity: 1, transform: 'translateX(0)' }
  },
  
  // 向右滑入
  slideRight: {
    from: { opacity: 0, transform: 'translateX(-20px)' },
    to: { opacity: 1, transform: 'translateX(0)' }
  },
  
  // 缩放弹出
  scalePop: {
    from: { opacity: 0, transform: 'scale(0.8)' },
    to: { opacity: 1, transform: 'scale(1)' }
  },
  
  // 弹跳效果
  bounce: {
    from: { transform: 'scale(0)' },
    to: { transform: 'scale(1)' }
  }
}

// 预设缓动函数
export const easings = {
  // 标准
  ease: 'cubic-bezier(0.4, 0, 0.2, 1)',
  
  // 加速
  easeIn: 'cubic-bezier(0.4, 0, 1, 1)',
  
  // 减速
  easeOut: 'cubic-bezier(0, 0, 0.2, 1)',
  
  // 弹性
  spring: 'cubic-bezier(0.175, 0.885, 0.32, 1.275)',
  
  // 弹跳
  bounce: 'cubic-bezier(0.68, -0.55, 0.265, 1.55)',
  
  // 平滑
  smooth: 'cubic-bezier(0.4, 0, 0.2, 1)'
}

// 预设时长
export const durations = {
  instant: '0ms',
  fast: '150ms',
  normal: '300ms',
  slow: '500ms',
  slower: '800ms'
}

// 动画类名
export const animationClasses = {
  // 悬停效果
  hover: {
    scale: 'hover-scale',
    lift: 'hover-lift',
    glow: 'hover-glow',
    brighten: 'hover-brighten'
  },
  
  // 点击效果
  click: {
    pulse: 'click-pulse',
    ripple: 'click-ripple',
    shrink: 'click-shrink'
  },
  
  // 加载效果
  loading: {
    spin: 'loading-spin',
    pulse: 'loading-pulse',
    bounce: 'loading-bounce'
  }
}

// 创建动画配置
export const createAnimation = (name, options = {}) => {
  const animation = animations[name]
  if (!animation) return null
  
  return {
    ...animation,
    duration: options.duration || durations.normal,
    easing: options.easing || easings.ease,
    delay: options.delay || '0ms',
    iterations: options.iterations || 1,
    direction: options.direction || 'normal',
    fill: options.fill || 'both'
  }
}

// 应用 CSS 动画
export const applyAnimation = (element, animationName, options = {}) => {
  if (!element) return
  
  const animation = createAnimation(animationName, options)
  if (!animation) return
  
  const { from, to, duration, easing, delay, iterations, direction, fill } = animation
  
  // 构建关键帧
  const keyframes = [
    { offset: 0, ...from },
    { offset: 1, ...to }
  ]
  
  // 应用动画
  element.animate(keyframes, {
    duration: parseDuration(duration),
    easing,
    delay: parseDuration(delay),
    iterations: iterations === 'infinite' ? Infinity : iterations,
    direction,
    fill
  })
}

// 解析时长字符串
const parseDuration = (duration) => {
  if (typeof duration === 'number') return duration
  return parseInt(duration) || 300
}

// 涟漪效果
export const createRipple = (event, element, options = {}) => {
  const {
    color = 'currentColor',
    duration = 600,
    scale = 2.5
  } = options
  
  const rect = element.getBoundingClientRect()
  const size = Math.max(rect.width, rect.height)
  const x = event.clientX - rect.left - size / 2
  const y = event.clientY - rect.top - size / 2
  
  const ripple = document.createElement('span')
  ripple.className = 'ripple-effect'
  ripple.style.cssText = `
    position: absolute;
    width: ${size}px;
    height: ${size}px;
    left: ${x}px;
    top: ${y}px;
    background: ${color};
    border-radius: 50%;
    opacity: 0.3;
    transform: scale(0);
    animation: ripple-effect ${duration}ms cubic-bezier(0.4, 0, 0.2, 1) forwards;
    pointer-events: none;
  `
  
  element.style.position = 'relative'
  element.style.overflow = 'hidden'
  element.appendChild(ripple)
  
  setTimeout(() => {
    ripple.remove()
  }, duration)
  
  return ripple
}

// 打字机效果
export const typewriterEffect = (element, text, options = {}) => {
  const {
    speed = 50,
    delay = 0,
    cursor = true,
    cursorChar = '|',
    onComplete = () => {}
  } = options
  
  return new Promise((resolve) => {
    setTimeout(() => {
      let index = 0
      element.textContent = ''
      
      if (cursor) {
        element.innerHTML = `<span class="typewriter-text"></span><span class="typewriter-cursor">${cursorChar}</span>`
      }
      
      const textSpan = element.querySelector('.typewriter-text')
      
      const interval = setInterval(() => {
        if (index < text.length) {
          textSpan.textContent += text[index]
          index++
        } else {
          clearInterval(interval)
          if (cursor) {
            const cursorSpan = element.querySelector('.typewriter-cursor')
            cursorSpan.style.animation = 'blink 1s step-end infinite'
          }
          onComplete()
          resolve()
        }
      }, speed)
    }, delay)
  })
}

// 数字滚动效果
export const animateNumber = (element, from, to, options = {}) => {
  const {
    duration = 1000,
    easing = easings.easeOut,
    format = (num) => num.toFixed(0),
    onUpdate = () => {},
    onComplete = () => {}
  } = options
  
  const startTime = performance.now()
  const diff = to - from
  
  const tick = (currentTime) => {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    
    // 应用缓动
    const easedProgress = ease(progress, easing)
    
    const current = from + diff * easedProgress
    element.textContent = format(current)
    onUpdate(current)
    
    if (progress < 1) {
      requestAnimationFrame(tick)
    } else {
      onComplete()
    }
  }
  
  requestAnimationFrame(tick)
}

// 简化的缓动函数
const ease = (t, easingString) => {
  // 简单的线性插值，可根据需要扩展
  return t
}

// 滑动列表动画
export const animateList = (container, options = {}) => {
  const {
    stagger = 50,
    animation = 'slideUp',
    duration = 300
  } = options
  
  const items = container.children
  Array.from(items).forEach((item, index) => {
    item.style.opacity = '0'
    item.style.transform = 'translateY(20px)'
    
    setTimeout(() => {
      item.style.transition = `all ${duration}ms cubic-bezier(0.4, 0, 0.2, 1)`
      item.style.opacity = '1'
      item.style.transform = 'translateY(0)'
    }, index * stagger)
  })
}

// 页面过渡效果
export const pageTransition = {
  fade: {
    enter: { opacity: 0 },
    enterTo: { opacity: 1 },
    leave: { opacity: 1 },
    leaveTo: { opacity: 0 }
  },
  
  slideLeft: {
    enter: { opacity: 0, transform: 'translateX(30px)' },
    enterTo: { opacity: 1, transform: 'translateX(0)' },
    leave: { opacity: 1, transform: 'translateX(0)' },
    leaveTo: { opacity: 0, transform: 'translateX(-30px)' }
  },
  
  slideRight: {
    enter: { opacity: 0, transform: 'translateX(-30px)' },
    enterTo: { opacity: 1, transform: 'translateX(0)' },
    leave: { opacity: 1, transform: 'translateX(0)' },
    leaveTo: { opacity: 0, transform: 'translateX(30px)' }
  },
  
  scale: {
    enter: { opacity: 0, transform: 'scale(0.95)' },
    enterTo: { opacity: 1, transform: 'scale(1)' },
    leave: { opacity: 1, transform: 'scale(1)' },
    leaveTo: { opacity: 0, transform: 'scale(0.95)' }
  }
}

// Vue 指令：涟漪效果
export const vRipple = {
  mounted(el, binding) {
    el.addEventListener('click', (event) => {
      createRipple(event, el, binding.value || {})
    })
  },
  unmounted(el) {
    el.removeEventListener('click', createRipple)
  }
}

// Vue 指令：打字机效果
export const vTypewriter = {
  mounted(el, binding) {
    const { text, ...options } = binding.value
    typewriterEffect(el, text, options)
  },
  updated(el, binding) {
    if (binding.value.text !== binding.oldValue?.text) {
      const { text, ...options } = binding.value
      typewriterEffect(el, text, options)
    }
  }
}

// 导出 CSS 动画关键帧
export const keyframes = `
@keyframes ripple-effect {
  to {
    transform: scale(2.5);
    opacity: 0;
  }
}

@keyframes blink {
  50% {
    opacity: 0;
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* 悬停效果类 */
.hover-scale {
  transition: transform var(--duration-fast) var(--ease-default);
}
.hover-scale:hover {
  transform: scale(1.05);
}

.hover-lift {
  transition: transform var(--duration-fast) var(--ease-default),
              box-shadow var(--duration-fast) var(--ease-default);
}
.hover-lift:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.hover-glow {
  transition: box-shadow var(--duration-fast) var(--ease-default);
}
.hover-glow:hover {
  box-shadow: 0 0 20px rgba(54, 179, 194, 0.3);
}

.hover-brighten {
  transition: filter var(--duration-fast) var(--ease-default);
}
.hover-brighten:hover {
  filter: brightness(1.1);
}

/* 点击效果类 */
.click-pulse {
  transition: transform var(--duration-instant);
}
.click-pulse:active {
  transform: scale(0.95);
}

.click-shrink {
  transition: transform var(--duration-instant);
}
.click-shrink:active {
  transform: scale(0.98);
}

/* 加载效果类 */
.loading-spin {
  animation: spin 1s linear infinite;
}

.loading-pulse {
  animation: pulse 1.5s ease-in-out infinite;
}

.loading-bounce {
  animation: bounce 1s ease-in-out infinite;
}
`

export default {
  animations,
  easings,
  durations,
  animationClasses,
  createAnimation,
  applyAnimation,
  createRipple,
  typewriterEffect,
  animateNumber,
  animateList,
  pageTransition,
  vRipple,
  vTypewriter,
  keyframes
}
