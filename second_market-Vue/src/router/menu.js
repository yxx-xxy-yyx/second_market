export const MENU_ITEMS = {
  user: [
    {
      path: '/user/dashboard',
      name: 'user-dashboard',
      title: '首页',
      icon: 'Home'
    },
    {
      path: '/user/products',
      name: 'user-productList',
      title: '商品',
      icon: 'Appstore'
    },
    {
      path: '/user/categories',
      name: 'user-categories',
      title: '分类',
      icon: 'Grid'
    },
    {
      path: '/user/forum',
      name: 'user-forum',
      title: '论坛',
      icon: 'ChatLineSquare'
    },
    {
      path: '/user/orders',
      name: 'user-orders',
      title: '订单',
      icon: 'Document'
    },
    {
      path: '/user/my-products',
      name: 'user-my-products',
      title: '我的商品',
      icon: 'Shop'
    },
    {
      path: '/user/favorites',
      name: 'user-favorites',
      title: '收藏',
      icon: 'Heart'
    },
    {
      path: '/user/messages',
      name: 'user-messages',
      title: '消息',
      icon: 'Bell'
    },
    {
      path: '/user/statistics',
      name: 'user-statistics',
      title: '数据',
      icon: 'DataAnalysis'
    },
    {
      path: '/user/ai-chat',
      name: 'AiChat',
      title: 'AI助手',
      icon: 'Service'
    },
    {
      path: '/user/profile',
      name: 'user-profile',
      title: '个人中心',
      icon: 'User'
    },
    {
      path: '/user/settings',
      name: 'user-settings',
      title: '设置',
      icon: 'Setting'
    }
  ],

  admin: [
    {
      path: '/admin/dashboard',
      name: 'admin-dashboard',
      title: '管理首页',
      icon: 'Home'
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      title: '用户管理',
      icon: 'User'
    },
    {
      path: '/admin/product-review',
      name: 'admin-product-review',
      title: '商品审核',
      icon: 'Check'
    },
    {
      path: '/admin/reports',
      name: 'admin-reports',
      title: '举报管理',
      icon: 'Warning'
    },
    {
      path: '/admin/notices',
      name: 'admin-notices',
      title: '公告管理',
      icon: 'Megaphone'
    },
    {
      path: '/admin/forum',
      name: 'admin-forum',
      title: '论坛管理',
      icon: 'ChatDotRound'
    },
    {
      path: '/admin/roles',
      name: 'admin-roles',
      title: '角色权限',
      icon: 'Key'
    },
    {
      path: '/admin/files',
      name: 'admin-files',
      title: '文件管理',
      icon: 'Folder'
    }
  ]
}

export const QUICK_NAV = [
  {
    path: '/user/publish',
    name: 'user-publish',
    title: '发布商品',
    icon: 'Plus',
    type: 'primary'
  },
  {
    path: '/user/cart',
    name: 'user-cart',
    title: '购物车',
    icon: 'ShoppingCart'
  },
  {
    path: '/user/errand',
    name: 'user-errand',
    title: '跑腿',
    icon: 'Van'
  }
]

export const AI_NAV = [
  {
    path: '/user/ai-chat',
    name: 'AiChat',
    title: 'AI助手',
    icon: 'Service'
  },
  {
    path: '/user/ai-authenticate',
    name: 'ai-authenticate',
    title: 'AI鉴定',
    icon: 'TakeawayBox'
  },
  {
    path: '/user/ai-market',
    name: 'ai-market',
    title: 'AI行情',
    icon: 'TrendCharts'
  },
  {
    path: '/user/ai-trust',
    name: 'ai-trust',
    title: 'AI托管',
    icon: 'Odometer'
  }
]

export const CAMPUS_SERVICE_NAV = [
  {
    path: '/user/campus-nearby',
    name: 'campus-nearby',
    title: '校园周边',
    icon: 'MapLocation'
  },
  {
    path: '/user/errand',
    name: 'user-errand',
    title: '跑腿服务',
    icon: 'Van'
  },
  {
    path: '/user/recently-viewed',
    name: 'recently-viewed',
    title: '我常看的',
    icon: 'Clock'
  },
  {
    path: '/user/popular-digital',
    name: 'popular-digital',
    title: '热门数码',
    icon: 'Iphone'
  }
]

export const ACCOUNT_NAV = [
  {
    path: '/user/profile',
    name: 'user-profile',
    title: '个人资料',
    icon: 'User'
  },
  {
    path: '/user/verify',
    name: 'user-verify',
    title: '实名认证',
    icon: 'Postcard'
  },
  {
    path: '/user/credit',
    name: 'user-credit',
    title: '信用评分',
    icon: 'Trophy'
  },
  {
    path: '/user/address',
    name: 'user-address-edit',
    title: '收货地址',
    icon: 'Location'
  },
  {
    path: '/user/settings',
    name: 'user-settings',
    title: '设置',
    icon: 'Setting'
  }
]

export const HELP_NAV = [
  {
    path: '/user/terms',
    name: 'user-terms',
    title: '用户协议',
    icon: 'Document'
  },
  {
    path: '/user/privacy',
    name: 'user-privacy',
    title: '隐私协议',
    icon: 'Lock'
  },
  {
    path: '/user/feedback',
    name: 'user-feedback',
    title: '意见反馈',
    icon: 'ChatLineSquare'
  }
]

export default {
  MENU_ITEMS,
  QUICK_NAV,
  AI_NAV,
  CAMPUS_SERVICE_NAV,
  ACCOUNT_NAV,
  HELP_NAV
}

