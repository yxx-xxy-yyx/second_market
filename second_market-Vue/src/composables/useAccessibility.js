/**
 * 可访问性工具函数 - Accessibility Utilities
 * 提供增强应用可访问性的工具函数
 */

// 生成唯一 ID
let idCounter = 0
export const generateId = (prefix = 'a11y') => {
  return `${prefix}-${++idCounter}`
}

//ARIA 属性
export const ariaAttributes = {
  // 标签关联
  label: (id, text) => ({
    'aria-label': text,
    id
  }),
  
  // 描述关联
  describedBy: (id, describedById) => ({
    'aria-describedby': describedById,
    id
  }),
  
  // 角色
  role: (role) => ({
    'role': role
  }),
  
  // 展开/折叠状态
  expanded: (expanded) => ({
    'aria-expanded': !!expanded
  }),
  
  // 选中状态
  checked: (checked) => ({
    'aria-checked': !!checked
  }),
  
  // 禁用状态
  disabled: (disabled) => ({
    'aria-disabled': !!disabled
  }),
  
  // 隐藏
  hidden: (hidden) => ({
    'aria-hidden': !!hidden
  }),
  
  // 只读
  readonly: (readonly) => ({
    'aria-readonly': !!readonly
  }),
  
  // 必需
  required: (required) => ({
    'aria-required': !!required
  }),
  
  // 无效状态
  invalid: (invalid, message) => ({
    'aria-invalid': !!invalid,
    'aria-errormessage': message
  }),
  
  // 悬停
  hover: (hover) => ({
    'aria-hover': hover
  }),
  
  // 按下
  pressed: (pressed) => ({
    'aria-pressed': pressed
  }),
  
  // 选择
  selected: (selected) => ({
    'aria-selected': !!selected
  }),
  
  // 当前
  current: (current) => ({
    'aria-current': current
  }),
  
  // 级别（用于标题、网格等）
  level: (level) => ({
    'aria-level': level
  }),
  
  // 列表大小和位置
  setSize: (size) => ({
    'aria-setsize': size
  }),
  posInSet: (pos) => ({
    'aria-posinset': pos
  }),
  
  // 控件类型
  controls: (id) => ({
    'aria-controls': id
  }),
  
  // 拥有者
  owns: (id) => ({
    'aria-owns': id
  }),
  
  // 流量方向
  flowTo: (id) => ({
    'aria-flowto': id
  })
}

// 焦点管理
export const focusManagement = {
  // 聚焦到元素
  focus: (element) => {
    if (element && typeof element.focus === 'function') {
      element.focus()
    }
  },
  
  // 聚焦到第一个可聚焦元素
  focusFirst: (container) => {
    const focusable = container?.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    )
    if (focusable?.length > 0) {
      focusable[0].focus()
    }
  },
  
  // 聚焦到最后一个可聚焦元素
  focusLast: (container) => {
    const focusable = container?.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    )
    if (focusable?.length > 0) {
      focusable[focusable.length - 1].focus()
    }
  },
  
  // 聚焦到下一个可聚焦元素
  focusNext: (currentElement) => {
    const focusable = document.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    )
    const currentIndex = Array.from(focusable).indexOf(currentElement)
    if (currentIndex !== -1 && currentIndex < focusable.length - 1) {
      focusable[currentIndex + 1].focus()
    }
  },
  
  // 聚焦到上一个可聚焦元素
  focusPrev: (currentElement) => {
    const focusable = document.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    )
    const currentIndex = Array.from(focusable).indexOf(currentElement)
    if (currentIndex > 0) {
      focusable[currentIndex - 1].focus()
    }
  },
  
  // 将焦点锁在容器内
  trapFocus: (container) => {
    const focusable = container?.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    )
    if (!focusable?.length) return () => {}
    
    const firstElement = focusable[0]
    const lastElement = focusable[focusable.length - 1]
    
    const handleKeyDown = (e) => {
      if (e.key === 'Tab') {
        if (e.shiftKey && document.activeElement === firstElement) {
          e.preventDefault()
          lastElement.focus()
        } else if (!e.shiftKey && document.activeElement === lastElement) {
          e.preventDefault()
          firstElement.focus()
        }
      }
    }
    
    container.addEventListener('keydown', handleKeyDown)
    
    // 返回清理函数
    return () => {
      container.removeEventListener('keydown', handleKeyDown)
    }
  }
}

// 键盘导航
export const keyboardNavigation = {
  // 回车键
  onEnter: (callback) => (e) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      callback(e)
    }
  },
  
  // 空格键
  onSpace: (callback) => (e) => {
    if (e.key === ' ') {
      e.preventDefault()
      callback(e)
    }
  },
  
  // Escape 键
  onEscape: (callback) => (e) => {
    if (e.key === 'Escape') {
      e.preventDefault()
      callback(e)
    }
  },
  
  // 方向键
  onArrowUp: (callback) => (e) => {
    if (e.key === 'ArrowUp') {
      e.preventDefault()
      callback(e)
    }
  },
  
  onArrowDown: (callback) => (e) => {
    if (e.key === 'ArrowDown') {
      e.preventDefault()
      callback(e)
    }
  },
  
  onArrowLeft: (callback) => (e) => {
    if (e.key === 'ArrowLeft') {
      e.preventDefault()
      callback(e)
    }
  },
  
  onArrowRight: (callback) => (e) => {
    if (e.key === 'ArrowRight') {
      e.preventDefault()
      callback(e)
    }
  },
  
  // Home 和 End 键
  onHome: (callback) => (e) => {
    if (e.key === 'Home') {
      e.preventDefault()
      callback(e)
    }
  },
  
  onEnd: (callback) => (e) => {
    if (e.key === 'End') {
      e.preventDefault()
      callback(e)
    }
  }
}

// 屏幕阅读器文本
export const srText = {
  // 可见但对屏幕阅读器隐藏
  visuallyHidden: {
    position: 'absolute',
    width: '1px',
    height: '1px',
    padding: '0',
    margin: '-1px',
    overflow: 'hidden',
    clip: 'rect(0, 0, 0, 0)',
    whiteSpace: 'nowrap',
    border: '0'
  },
  
  // 渲染屏幕阅读器文本
  render: (text) => ({
    class: 'sr-only',
    'aria-live': 'polite'
  }, text)
}

// 实时区域（用于动态内容更新）
export const liveRegions = {
  // 创建实时区域
  create: (container, politeness = 'polite') => {
    const region = document.createElement('div')
    region.setAttribute('role', 'status')
    region.setAttribute('aria-live', politeness)
    region.setAttribute('aria-atomic', 'true')
    region.className = 'sr-only'
    container.appendChild(region)
    return region
  },
  
  // 宣布消息
  announce: (region, message, politeness = 'polite') => {
    region.setAttribute('aria-live', politeness)
    region.textContent = ''
    // 使用 setTimeout 确保屏幕阅读器能捕捉到变化
    setTimeout(() => {
      region.textContent = message
    }, 100)
  }
}

// 跳过链接
export const skipLinks = {
  // 创建跳过链接
  create: (targetId, text = '跳转到主要内容') => {
    const skipLink = document.createElement('a')
    skipLink.href = `#${targetId}`
    skipLink.textContent = text
    skipLink.className = 'skip-link'
    skipLink.style.cssText = `
      position: absolute;
      top: -40px;
      left: 0;
      background: var(--primary-500);
      color: white;
      padding: 8px;
      z-index: 10000;
      transition: top 0.3s;
    `
    
    skipLink.addEventListener('focus', () => {
      skipLink.style.top = '0'
    })
    
    skipLink.addEventListener('blur', () => {
      skipLink.style.top = '-40px'
    })
    
    document.body.insertBefore(skipLink, document.body.firstChild)
    return skipLink
  }
}

// 表单验证
export const formValidation = {
  // 获取错误消息
  getErrorMessage: (field, error) => {
    if (error.required) return `${field}是必填项`
    if (error.email) return '请输入有效的邮箱地址'
    if (error.minlength) return `${field}至少需要${error.minlength.requiredLength}个字符`
    if (error.maxlength) return `${field}不能超过${error.maxlength.requiredLength}个字符`
    if (error.min) return `${field}不能小于${error.min}`
    if (error.max) return `${field}不能大于${error.max}`
    if (error.pattern) return `${field}格式不正确`
    return '输入无效'
  },
  
  // 设置字段错误
  setFieldError: (input, message) => {
    input.setAttribute('aria-invalid', 'true')
    input.setAttribute('aria-describedby', `${input.id}-error`)
    
    // 创建或更新错误元素
    let errorEl = document.getElementById(`${input.id}-error`)
    if (!errorEl) {
      errorEl = document.createElement('span')
      errorEl.id = `${input.id}-error`
      errorEl.className = 'field-error'
      errorEl.style.cssText = 'color: var(--error-500); font-size: 12px; margin-top: 4px;'
      input.parentNode.appendChild(errorEl)
    }
    errorEl.textContent = message
  },
  
  // 清除字段错误
  clearFieldError: (input) => {
    input.removeAttribute('aria-invalid')
    input.removeAttribute('aria-describedby')
    
    const errorEl = document.getElementById(`${input.id}-error`)
    if (errorEl) {
      errorEl.remove()
    }
  }
}

// 减少动画偏好检测
export const prefersReducedMotion = () => {
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches
}

// 颜色对比度计算
export const contrastRatio = {
  // 获取相对亮度
  getLuminance: (color) => {
    const rgb = color.match(/\d+/g).map(Number)
    const [r, g, b] = rgb.map(c => {
      c = c / 255
      return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4)
    })
    return 0.2126 * r + 0.7152 * g + 0.0722 * b
  },
  
  // 计算对比度
  calculate: (color1, color2) => {
    const l1 = contrastRatio.getLuminance(color1)
    const l2 = contrastRatio.getLuminance(color2)
    const lighter = Math.max(l1, l2)
    const darker = Math.min(l1, l2)
    return (lighter + 0.05) / (darker + 0.05)
  },
  
  // 检查是否符合 WCAG AA 标准
  isAA: (foreground, background, isLargeText = false) => {
    const ratio = contrastRatio.calculate(foreground, background)
    return isLargeText ? ratio >= 3 : ratio >= 4.5
  },
  
  // 检查是否符合 WCAG AAA 标准
  isAAA: (foreground, background, isLargeText = false) => {
    const ratio = contrastRatio.calculate(foreground, background)
    return isLargeText ? ratio >= 4.5 : ratio >= 7
  }
}

export default {
  generateId,
  ariaAttributes,
  focusManagement,
  keyboardNavigation,
  srText,
  liveRegions,
  skipLinks,
  formValidation,
  prefersReducedMotion,
  contrastRatio
}
