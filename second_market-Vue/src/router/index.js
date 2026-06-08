import { createRouter, createWebHistory } from 'vue-router'
import UserDashboard from '@/views/user/Dashboard.vue'
import UserProfile from '@/views/user/Profile.vue'
import Categories from '@/views/user/Categories.vue'

// 路由配置中心
const Login = () => import('@/views/auth/Login.vue')
const Register = () => import('@/views/auth/Register.vue')
const UserLayout = () => import('@/layouts/UserLayout.vue')
const UserProfileEdit = () => import('@/views/mobile/user/ProfileEditView.vue')
const UserAddressEdit = () => import('@/views/mobile/user/AddressView.vue')
const ProductList = () => import('@/views/user/ProductList.vue')
const ProductPublish = () => import('@/views/user/ProductPublish.vue')
const UserChat = () => import('@/views/mobile/user/ChatView.vue')
const UserMessageChat = () => import('@/views/mobile/user/MessageView.vue')
const SystemMsgList = () => import('@/views/mobile/user/SystemMsgList.vue')
const SystemMsgDetail = () => import('@/views/mobile/user/SystemMsgDetail.vue')
const AiChat = () => import('@/views/user/AiChatView.vue')
const ProductDetail = () => import('@/views/user/ProductDetail.vue')
const MyProducts = () => import('@/views/user/MyProducts.vue')
const MyOrders = () => import('@/views/user/MyOrders.vue')
const Favorites = () => import('@/views/user/Favorites.vue')
const Messages = () => import('@/views/user/Messages.vue')
const WriteReview = () => import('@/views/user/WriteReview.vue')
const Notice = () => import('@/views/user/Notice.vue')
const SearchView = () => import('@/views/user/SearchView.vue')
const Cart = () => import('@/views/user/Cart.vue')
const Checkout = () => import('@/views/user/Checkout.vue')
const MyPublish = () => import('@/views/user/MyPublish.vue')
const Settings = () => import('@/views/user/Settings.vue')
const AgreementPrivacy = () => import('@/views/user/AgreementPrivacy.vue')
const AgreementUser = () => import('@/views/user/AgreementUser.vue')
const Feedback = () => import('@/views/user/Feedback.vue')
const Verify = () => import('@/views/user/Verify.vue')
const Credit = () => import('@/views/user/Credit.vue')
const AdminLayout = () => import('@/layouts/AdminLayout.vue')

// AI 功能页面
const AiAuthenticate = () => import('@/views/user/AiAuthenticate.vue')
const AiMarketTrend = () => import('@/views/user/AiMarketTrend.vue')
const AiTrust = () => import('@/views/user/AiTrust.vue')
const UserStatistics = () => import('@/views/user/UserStatistics.vue')
const AdminDashboard = () => import('@/views/admin/Dashboard.vue')
const AdminUsers = () => import('@/views/admin/Users.vue')
const AdminProfile = () => import('@/views/admin/Profile.vue')
const AdminProductReview = () => import('@/views/admin/ProductReview.vue')
const AdminReportManage = () => import('@/views/admin/ReportManage.vue')
const AdminNoticeManage = () => import('@/views/admin/NoticeManage.vue')
const AdminForumManage = () => import('@/views/admin/ForumManage.vue')
const AdminRoleManage = () => import('@/views/admin/RoleManage.vue')
const AdminFileManage = () => import('@/views/admin/FileManage.vue')

// 移动端新增功能页面
const RecentlyViewed = () => import('@/views/user/RecentlyViewed.vue')
const PopularDigital = () => import('@/views/user/PopularDigital.vue')
const CampusNearby = () => import('@/views/user/CampusNearby.vue')
const ErrandHome = () => import('@/views/user/ErrandHome.vue')
const ErrandPublish = () => import('@/views/user/ErrandPublish.vue')
const ErrandOrders = () => import('@/views/user/ErrandOrders.vue')
const ErrandDetail = () => import('@/views/user/ErrandDetail.vue')
const ForumView = () => import('@/views/user/ForumView.vue')
const ForumDetail = () => import('@/views/user/ForumDetail.vue')
const ForumPublish = () => import('@/views/user/ForumPublish.vue')

// Neo 模块路由
import { neoRoutes } from '@/neo/neo.routes.js'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  ...neoRoutes,
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: {
      title: '登录',
      requiresAuth: false,
      hideForAuth: true
    }
  },
  {
    path: '/register',
    name: 'register',
    component: Register,
    meta: {
      title: '注册',
      requiresAuth: false,
      hideForAuth: true
    }
  },
  {
    path: '/user',
    name: 'user',
    component: UserLayout,
    redirect: '/user/dashboard',
    meta: {
      requiresAuth: true,
      roles: ['user']
    },
    children: [
      {
        path: 'dashboard',
        name: 'user-dashboard',
        component: UserDashboard,
        meta: {
          title: '用户首页',
          requiresAuth: true
        }
      },
      {
        path: 'profileEdit',
        name: 'user-profile-edit',
        component: UserProfileEdit,
        meta: {
          title: '个人资料修改',
          requiresAuth: true
        }
      },
      {
        path: 'address',
        name: 'user-address-edit',
        component: UserAddressEdit,
        meta: {
          title: '个人地址修改',
          requiresAuth: true
        }
      },
      {
        path: 'profile',
        name: 'user-profile',
        component: UserProfile,
        meta: {
          title: '个人资料',
          requiresAuth: true
        }
      },
      {
        path: 'products',
        name: 'user-productList',
        component: ProductList,
        meta: {
          title: '商品列表',
          requiresAuth: true
        }
      },
      {
        path: 'categories',
        name: 'user-categories',
        component: Categories,
        meta: {
          title: '校园',
          requiresAuth: true
        }
      },
      {
        path: 'publish',
        name: 'user-publish',
        component: ProductPublish,
        meta: {
          title: '发布商品',
          requiresAuth: true,
          hideNav: true
        }
      },
      {
        path: 'messageChat',
        name: 'user-message-chat',
        component: UserMessageChat,
        meta: {
          title: '消息列表',
          requiresAuth: true
        }
      },
      {
        path: 'chat/:id',
        name: 'user-chat',
        component: UserChat,
        meta: {
          title: '消息窗口',
          requiresAuth: true,
          hideNav: true
        }
      },
      {
        path: 'system-messages',
        name: 'SystemMsgList',
        component: SystemMsgList,
        meta: {
          title: '系统消息',
          requiresAuth: true
        }
      },
      {
        path: 'system-msg-detail/:id',
        name: 'SystemMsgDetail',
        component: SystemMsgDetail,
        meta: {
          title: '消息详情',
          requiresAuth: true
        }
      },
      {
        path: 'ai-chat',
        name: 'AiChat',
        component: AiChat,
        meta: {
          title: 'AI 智能助手',
          requiresAuth: true
        }
      },
      {
        path: 'product/:id',
        name: 'user-product-detail',
        component: ProductDetail,
        meta: {
          title: '商品详情',
          requiresAuth: true
        }
      },
      {
        path: 'my-products',
        name: 'user-my-products',
        component: MyProducts,
        meta: {
          title: '我的商品',
          requiresAuth: true
        }
      },
      {
        path: 'orders',
        name: 'user-orders',
        component: MyOrders,
        meta: {
          title: '我的订单',
          requiresAuth: true
        }
      },
      {
        path: 'favorites',
        name: 'user-favorites',
        component: Favorites,
        meta: {
          title: '我的收藏',
          requiresAuth: true
        }
      },
      {
        path: 'my-publish',
        name: 'user-my-publish',
        component: MyPublish,
        meta: {
          title: '我的发布',
          requiresAuth: true
        }
      },
      {
        path: 'settings',
        name: 'user-settings',
        component: Settings,
        meta: {
          title: '设置',
          requiresAuth: true
        }
      },
      {
        path: 'privacy',
        name: 'user-privacy',
        component: AgreementPrivacy,
        meta: {
          title: '隐私协议',
          requiresAuth: false,
          hideNav: true
        }
      },
      {
        path: 'terms',
        name: 'user-terms',
        component: AgreementUser,
        meta: {
          title: '用户协议',
          requiresAuth: false,
          hideNav: true
        }
      },
      {
        path: 'feedback',
        name: 'user-feedback',
        component: Feedback,
        meta: {
          title: '反馈',
          requiresAuth: true,
          hideNav: true
        }
      },
      {
        path: 'verify',
        name: 'user-verify',
        component: Verify,
        meta: {
          title: '实名认证',
          requiresAuth: true
        }
      },
      {
        path: 'credit',
        name: 'user-credit',
        component: Credit,
        meta: {
          title: '信用分',
          requiresAuth: true
        }
      },
      {
        path: 'cart',
        name: 'user-cart',
        component: Cart,
        meta: {
          title: '购物车',
          requiresAuth: true
        }
      },
      {
        path: 'checkout',
        name: 'user-checkout',
        component: Checkout,
        meta: {
          title: '确认订单',
          requiresAuth: true,
          hideNav: true
        }
      },
      {
        path: 'recently-viewed',
        name: 'recently-viewed',
        component: RecentlyViewed,
        meta: { title: '我常看的', requiresAuth: true }
      },
      {
        path: 'popular-digital',
        name: 'popular-digital',
        component: PopularDigital,
        meta: { title: '热门数码', requiresAuth: true }
      },
      {
        path: 'campus-nearby',
        name: 'campus-nearby',
        component: CampusNearby,
        meta: { title: '校园周边', requiresAuth: true }
      },
      {
        path: 'errand',
        name: 'user-errand',
        component: ErrandHome,
        meta: { title: '跑腿', requiresAuth: true }
      },
      {
        path: 'errand/publish',
        name: 'user-errand-publish',
        component: ErrandPublish,
        meta: { title: '发跑腿单', requiresAuth: true, hideNav: true }
      },
      {
        path: 'errand/orders',
        name: 'user-errand-orders',
        component: ErrandOrders,
        meta: { title: '跑腿订单', requiresAuth: true, hideNav: true }
      },
      {
        path: 'errand/detail/:id',
        name: 'user-errand-detail',
        component: ErrandDetail,
        meta: { title: '跑腿详情', requiresAuth: true, hideNav: true }
      },
      {
        path: 'forum',
        name: 'user-forum',
        component: ForumView,
        meta: {
          title: '校园论坛',
          requiresAuth: false
        }
      },
      {
        path: 'forum/detail/:id',
        name: 'user-forum-detail',
        component: ForumDetail,
        meta: {
          title: '帖子详情',
          requiresAuth: false,
          hideNav: true
        }
      },
      {
        path: 'forum/publish',
        name: 'user-forum-publish',
        component: ForumPublish,
        meta: {
          title: '发布帖子',
          requiresAuth: true
        }
      },
      {
        path: 'messages',
        name: 'user-messages',
        component: Messages,
        meta: {
          title: '消息通知',
          requiresAuth: true
        }
      },
      {
        path: 'write-review',
        name: 'user-write-review',
        component: WriteReview,
        meta: {
          title: '发表评价',
          requiresAuth: true
        }
      },
      {
        path: 'review',
        name: 'ProductReview',
        component: () => import('@/views/user/ProductReview.vue'),
        meta: {
          title: '商品评价',
          requiresAuth: true
        }
      },
      {
        path: 'product/:productId/reviews',
        name: 'ProductReviews',
        component: () => import('@/views/user/ProductReviews.vue'),
        meta: {
          title: '商品评价列表',
          requiresAuth: false
        }
      },
      {
        path: 'notices',
        name: 'user-notices',
        component: Notice,
        meta: {
          title: '平台公告',
          requiresAuth: true
        }
      },
      {
        path: 'search',
        name: 'user-search',
        component: SearchView,
        meta: {
          title: '搜索',
          requiresAuth: true
        }
      },
      {
        path: 'ai-authenticate',
        name: 'ai-authenticate',
        component: AiAuthenticate,
        meta: {
          title: 'AI 鉴定质检',
          requiresAuth: true
        }
      },
      {
        path: 'ai-market',
        name: 'ai-market',
        component: AiMarketTrend,
        meta: {
          title: 'AI 行情参考',
          requiresAuth: true
        }
      },
      {
        path: 'ai-trust',
        name: 'ai-trust',
        component: AiTrust,
        meta: {
          title: 'AI 智能托管',
          requiresAuth: true
        }
      },
      {
        path: 'statistics',
        name: 'user-statistics',
        component: UserStatistics,
        meta: {
          title: '我的数据',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    meta: {
      requiresAuth: true,
      roles: ['admin']
    },
    children: [
      {
        path: 'dashboard',
        name: 'admin-dashboard',
        component: AdminDashboard,
        meta: {
          title: '管理首页',
          requiresAuth: true,
          roles: ['admin']
        }
      },
      {
        path: 'users',
        name: 'admin-users',
        component: AdminUsers,
        meta: {
          title: '用户管理',
          requiresAuth: true,
          roles: ['admin']
        }
      },
      {
        path: 'profile',
        name: 'admin-profile',
        component: AdminProfile,
        meta: {
          title: '个人资料',
          requiresAuth: true,
          roles: ['admin']
        }
      },
      {
        path: 'product-review',
        name: 'admin-product-review',
        component: AdminProductReview,
        meta: {
          title: '商品审核',
          requiresAuth: true,
          roles: ['admin']
        }
      },
      {
        path: 'reports',
        name: 'admin-reports',
        component: AdminReportManage,
        meta: {
          title: '举报管理',
          requiresAuth: true,
          roles: ['admin']
        }
      },
      {
        path: 'notices',
        name: 'admin-notices',
        component: AdminNoticeManage,
        meta: {
          title: '公告管理',
          requiresAuth: true,
          roles: ['admin']
        }
      },
      {
        path: 'forum',
        name: 'admin-forum',
        component: AdminForumManage,
        meta: {
          title: '论坛管理',
          requiresAuth: true,
          roles: ['admin']
        }
      },
      {
        path: 'roles',
        name: 'admin-roles',
        component: AdminRoleManage,
        meta: {
          title: '角色权限',
          requiresAuth: true,
          roles: ['admin']
        }
      },
      {
        path: 'files',
        name: 'admin-files',
        component: AdminFileManage,
        meta: {
          title: '文件管理',
          requiresAuth: true,
          roles: ['admin']
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: {
      title: '页面未找到',
      requiresAuth: false
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  }
})

router.onError((error) => {
  if (error?.message?.includes("Cannot destructure property 'type' of 'vnode' as it is null")) return
  if (error?.message?.includes("Cannot read properties of null (reading 'parentNode')")) return
  console.error('Router error:', error)
})

// ========== 路由守卫 ==========
// 从 localStorage 读取当前用户信息（仅作为"快速判断是否登录"的缓存，不可信）
// 角色权限判断必须通过 Pinia store 的 isAdmin/isUser（需经过后端验证）
const getCurrentUser = () => {
  try {
    const raw = localStorage.getItem('user')
    return raw ? JSON.parse(raw) : null
  } catch (_) {
    return null
  }
}

// 从 localStorage 取缓存 role，仅用于"优化跳转方向"，不作为放行依据
const getCachedRoleHint = () => {
  const user = getCurrentUser()
  return user?.role || null
}

router.beforeEach(async (to, from, next) => {
  // 1. 更新页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - 智能二手商城`
  }

  const token = localStorage.getItem('token')
  const cachedUser = getCurrentUser()
  const hasCachedUser = !!cachedUser
  const cachedRole = getCachedRoleHint()

  // 2. 根路径 => 按登录状态跳（用缓存 role 仅做跳转方向优化）
  if (to.path === '/') {
    if (token && hasCachedUser) {
      // 这里的 role 来自 localStorage，仅用于"猜一个首页"，真正的 admin 页面仍需后端鉴权
      return next(cachedRole === 'admin' ? '/admin/dashboard' : '/user/dashboard')
    }
    return next('/login')
  }

  // 3. 收集 meta 信息
  const requiresAuth = to.matched.some((r) => r.meta && r.meta.requiresAuth)
  const hideForAuth = to.matched.some((r) => r.meta && r.meta.hideForAuth)
  const roleSet = new Set()
  to.matched.forEach((r) => {
    const roles = r.meta && r.meta.roles
    if (Array.isArray(roles)) roles.forEach((x) => roleSet.add(x))
  })
  const requiredRoles = Array.from(roleSet)

  // 4. 需要登录但无 token => 直接跳到登录
  if (requiresAuth && !token) {
    return next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  }

  // 5. 有 token 时引入 Pinia store（延迟 import，确保 Pinia 已初始化）
  // 注意：import 必须放在路由守卫函数内部，因为 router 在 main.js 中先于 Pinia 初始化
  const { useUserStore } = await import('@/stores/user')
  const userStore = useUserStore()

  // 6. 已登录但 Pinia 中 userVerified 为 false，且要进入需要鉴权/角色的页面
  //    => 尝试从 /user/me 获取可信 user 信息（页面刷新后场景）
  if (token && !userStore.userVerified && (requiresAuth || requiredRoles.length > 0)) {
    try {
      await userStore.fetchCurrentUser()
    } catch (e) {
      // /user/me 失败不拦截，让后续逻辑继续：页面内会自行判断或由后端 401 拦截
      console.warn('[router] fetchCurrentUser failed, continue anyway', e?.message || e)
    }
  }

  // 7. 已登录访问登录/注册等页 => 跳到对应首页
  if (hideForAuth && token) {
    const isAdminNow = userStore.isAdmin || cachedRole === 'admin'
    return next(isAdminNow ? '/admin/dashboard' : '/user/dashboard')
  }

  // 8. 角色不匹配时：
  //    - 如果 store 已验证（userVerified），可信地重定向
  //    - 如果 store 未验证（仅靠 localStorage 缓存），则仅做"优化跳转"
  //      让页面进入后由 Pinia / 后端 API 真正拦截，而不是在路由层强制放行
  if (requiredRoles && requiredRoles.length > 0) {
    // 优先使用 store 中可信的 isAdmin/isUser 判断
    if (userStore.userVerified) {
      const isAdmin = userStore.isAdmin
      const isUser = userStore.isUser
      const userRole = isAdmin ? 'admin' : (isUser ? 'user' : null)
      if (!userRole || !requiredRoles.includes(userRole)) {
        return next(userRole === 'admin' ? '/admin/dashboard' : '/user/dashboard')
      }
    } else {
      // store 未验证：用缓存 role 做"优化跳转"，但不阻止进入页面
      // 进入页面后，由页面组件内的 Pinia 检查 或 后端 API 401/403 来处理
      // 这样即使 localStorage 被篡改 role='admin'，进入 /admin 页面后后端 API 也会 403
      if (cachedRole && !requiredRoles.includes(cachedRole)) {
        return next(cachedRole === 'admin' ? '/admin/dashboard' : '/user/dashboard')
      }
    }
  }

  next()
})

// 登录成功后解析 redirect 跳转的辅助函数
export function resolveRedirectRoute(route) {
  const redirect = route.query?.redirect
  if (typeof redirect === 'string' && redirect && redirect !== '/login') {
    return redirect
  }
  const user = getCurrentUser()
  return user?.role === 'admin' ? '/admin/dashboard' : '/user/dashboard'
}

export default router
