# Figma Tokens 同步工具

本工具用于将 Figma 设计系统中的 Design Tokens 同步到代码中，确保设计与开发的一致性。

## 概述

Design Tokens 是设计系统中最小单位的视觉设计元素，如颜色、字体、间距等。通过同步 Figma Tokens，可以：

- 🎨 **保持一致** - 设计稿和代码使用完全相同的值
- ⚡ **提高效率** - 减少设计和开发之间的沟通成本
- 🔄 **易于更新** - 修改设计后可以快速同步到代码

## 支持的 Token 类型

### 1. 颜色 (Colors)

```json
{
  "color": {
    "primary": {
      "50": { "value": "#edfafb", "type": "color" },
      "100": { "value": "#d1f1f4", "type": "color" },
      "500": { "value": "#36b3c2", "type": "color" }
    }
  }
}
```

### 2. 字体 (Typography)

```json
{
  "typography": {
    "fontFamily": {
      "sans": { "value": "Inter, sans-serif", "type": "fontFamily" }
    },
    "fontSize": {
      "sm": { "value": "14px", "type": "fontSize" },
      "md": { "value": "16px", "type": "fontSize" }
    }
  }
}
```

### 3. 间距 (Spacing)

```json
{
  "spacing": {
    "sm": { "value": "8px", "type": "spacing" },
    "md": { "value": "16px", "type": "spacing" },
    "lg": { "value": "24px", "type": "spacing" }
  }
}
```

### 4. 圆角 (Border Radius)

```json
{
  "borderRadius": {
    "sm": { "value": "6px", "type": "borderRadius" },
    "md": { "value": "8px", "type": "borderRadius" }
  }
}
```

### 5. 阴影 (Shadows)

```json
{
  "shadow": {
    "sm": { "value": "0 1px 3px rgba(0,0,0,0.1)", "type": "boxShadow" },
    "md": { "value": "0 4px 6px rgba(0,0,0,0.1)", "type": "boxShadow" }
  }
}
```

## 同步流程

### 步骤 1: 导出 Figma Tokens

1. 在 Figma 中使用 Tokens Studio 或类似的插件
2. 导出为 JSON 格式
3. 保存到 `tokens/figma-tokens.json`

### 步骤 2: 运行同步脚本

```bash
# 同步所有 tokens
npm run sync:tokens

# 仅同步颜色
npm run sync:tokens:colors

# 仅同步字体
npm run sync:tokens:typography

# 预览更改（不实际修改文件）
npm run sync:tokens:dry-run
```

### 步骤 3: 验证更改

```bash
# 对比更改
git diff src/styles/design-tokens.css
```

## 工具配置

### 配置文件

```json
// tokens/config.json
{
  "source": "tokens/figma-tokens.json",
  "targets": [
    {
      "format": "css",
      "output": "src/styles/design-tokens.css"
    },
    {
      "format": "scss",
      "output": "src/styles/_tokens.scss"
    },
    {
      "format": "json",
      "output": "src/styles/tokens.json"
    },
    {
      "format": "js",
      "output": "src/styles/tokens.js"
    }
  ],
  "transforms": {
    "color": {
      "format": "rgb"
    },
    "spacing": {
      "unit": "px"
    }
  }
}
```

### Transform 配置

```javascript
// tokens/transforms.js
module.exports = {
  // 颜色转换
  color: (value) => {
    // 将 hex 转换为 rgba
    const hex = value.replace('#', '')
    const r = parseInt(hex.substring(0, 2), 16)
    const g = parseInt(hex.substring(2, 4), 16)
    const b = parseInt(hex.substring(4, 6), 16)
    return `rgba(${r}, ${g}, ${b}, <alpha-value>)`
  },
  
  // 字体转换
  fontFamily: (value) => {
    // 添加备选字体
    return `"${value}", -apple-system, BlinkMacSystemFont, sans-serif`
  },
  
  // 间距转换
  spacing: (value) => {
    // 提取数值
    return value.replace('px', '')
  }
}
```

## 使用示例

### 从 Figma 导出 Tokens

```json
{
  "colors": {
    "primary": {
      "500": {
        "value": "#36b3c2",
        "type": "color"
      }
    }
  }
}
```

### 同步后的 CSS

```css
:root {
  --color-primary-500: #36b3c2;
  --color-primary: var(--color-primary-500);
}
```

### 在代码中使用

```vue
<template>
  <button class="btn">按钮</button>
</template>

<style scoped>
.btn {
  background: var(--color-primary);
  color: white;
  padding: var(--spacing-4) var(--spacing-6);
  border-radius: var(--radius-md);
  font-size: var(--font-size-md);
  box-shadow: var(--shadow-md);
}
</style>
```

## 自动化集成

### GitHub Actions

```yaml
# .github/workflows/sync-tokens.yml
name: Sync Design Tokens

on:
  workflow_dispatch:
  push:
    branches:
      - main
    paths:
      - 'tokens/**'

jobs:
  sync:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          
      - name: Install dependencies
        run: npm install
        
      - name: Sync tokens
        run: npm run sync:tokens
        env:
          FIGMA_TOKEN: ${{ secrets.FIGMA_TOKEN }}
          
      - name: Create Pull Request
        uses: peter-evans/create-pull-request@v4
        with:
          title: 'chore: sync design tokens'
          commit-message: 'chore: sync design tokens from Figma'
          branch: chore/sync-tokens
```

### Git Hooks

```bash
# .husky/pre-commit
#!/bin/sh
npm run sync:tokens -- --check
```

## 最佳实践

### 1. 命名规范

使用语义化的命名：

```json
// 好
{
  "color": {
    "text": {
      "primary": { "value": "#333333" }
    }
  }
}

// 避免
{
  "color": {
    "c1": { "value": "#333333" }
  }
}
```

### 2. 分层结构

```json
{
  "primitives": {
    "colors": {
      "blue": {
        "500": { "value": "#3b82f6" }
      }
    }
  },
  "semantic": {
    "colors": {
      "primary": { "value": "{primitives.colors.blue.500}" }
    }
  }
}
```

### 3. 版本控制

始终提交 token 更改：

```bash
git add tokens/
git commit -m "chore: update design tokens from Figma"
git push
```

### 4. 文档同步

保持 Figma 和代码文档一致：

```markdown
## 颜色

| Token | 值 | 用途 |
|-------|-----|------|
| primary.500 | #36b3c2 | 主色调 |
| success.500 | #22c55e | 成功状态 |
```

## 故障排除

### 常见问题

1. **Tokens 不同步**
   - 检查 Figma 导出是否成功
   - 验证 JSON 格式是否正确
   - 确认文件路径配置

2. **格式转换失败**
   - 检查 transform 配置
   - 查看错误日志
   - 手动测试转换函数

3. **CI/CD 失败**
   - 确认 secret 配置正确
   - 检查权限设置
   - 查看 GitHub Actions 日志

## 相关资源

- [Figma Tokens 插件](https://www.figma.com/community/plugins/studio)
- [Style Dictionary](https://amzn.github.io/style-dictionary/)
- [Design Tokens W3C 规范](https://design-tokens.github.io/community-group/format/)

## 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0.0 | 2024-01-01 | 初始版本 |
| v1.1.0 | 2024-06-07 | 支持多种输出格式 |
