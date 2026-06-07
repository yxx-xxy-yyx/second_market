import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'EchoOfMemories UI',
  description: '校园二手交易平台组件库文档',
  base: '/docs/',
  
  themeConfig: {
    logo: '/logo.svg',
    
    nav: [
      { text: '指南', link: '/guide/introduction' },
      { text: '组件', link: '/components/' },
      { text: '设计系统', link: '/design/' },
      { text: 'GitHub', link: 'https://github.com/echoofmemories/second-market' }
    ],

    sidebar: {
      '/guide/': [
        {
          text: '指南',
          items: [
            { text: '介绍', link: '/guide/introduction' },
            { text: '快速开始', link: '/guide/quick-start' },
            { text: '安装', link: '/guide/installation' },
            { text: '使用', link: '/guide/usage' }
          ]
        }
      ],
      '/components/': [
        {
          text: '基础组件',
          items: [
            { text: 'UnifiedCard 统一卡片', link: '/components/unified-card' },
            { text: 'ProductCard 产品卡片', link: '/components/product-card' },
            { text: 'ResponsiveLayout 响应式布局', link: '/components/responsive-layout' }
          ]
        },
        {
          text: '表单组件',
          items: [
            { text: '按钮', link: '/components/button' },
            { text: '输入框', link: '/components/input' },
            { text: '选择器', link: '/components/select' }
          ]
        },
        {
          text: '反馈组件',
          items: [
            { text: '骨架屏', link: '/components/skeleton' },
            { text: '消息提示', link: '/components/message' },
            { text: '对话框', link: '/components/dialog' }
          ]
        },
        {
          text: '导航组件',
          items: [
            { text: '标签页', link: '/components/tabs' },
            { text: '面包屑', link: '/components/breadcrumb' }
          ]
        }
      ],
      '/design/': [
        {
          text: '设计系统',
          items: [
            { text: '色彩系统', link: '/design/colors' },
            { text: '字体系统', link: '/design/typography' },
            { text: '间距系统', link: '/design/spacing' },
            { text: '阴影系统', link: '/design/shadows' }
          ]
        }
      ]
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/echoofmemories/second-market' }
    ],

    footer: {
      message: '基于 MIT 许可证发布',
      copyright: 'Copyright © 2024 EchoOfMemories Team'
    },

    search: {
      provider: 'local'
    }
  },

  head: [
    ['link', { rel: 'icon', type: 'image/svg+xml', href: '/logo.svg' }],
    ['meta', { name: 'theme-color', content: '#36b3c2' }]
  ],

  markdown: {
    theme: {
      light: 'github-light',
      dark: 'github-dark'
    },
    lineNumbers: true
  }
})
