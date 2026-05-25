import { createRouter, createWebHistory } from 'vue-router'
import UserDashboard from '@/views/user/Dashboard.vue'
import UserProfile from '@/views/user/Profile.vue'
import Categories from '@/views/user/Categories.vue'

// 路由配置中心
// 引入页面组件
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
const AdminDashboard = () => import('@/views/admin/Dashboard.vue')
const AdminUsers = () => import('@/views/admin/Users.vue')
const AdminProfile = () => import('@/views/admin/Profile.vue')
const AdminProductReview = () => import('@/views/admin/ProductReview.vue')
const AdminReportManage = () => import('@/views/admin/ReportManage.vue')
const AdminNoticeManage = () => import('@/views/admin/NoticeManage.vue')

// 新增移动端功能页面
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

// 引入 Neo 模块路由
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
          title: 'AI智能助手',
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
    } else {
      return { top: 0 }
    }
  }
})

router.onError((error) => {
  if (error?.message?.includes("Cannot destructure property 'type' of 'vnode' as it is null")) return
  if (error?.message?.includes("Cannot read properties of null (reading 'parentNode')")) return
  console.error('Router error:', error)
})

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - 智能二手商城`
  }

  if (to.path === '/') return next('/login')

  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  let user = null
  try {
    user = userStr ? JSON.parse(userStr) : null
  } catch (e) {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }
  const isLoggedIn = !!(token && user)

  const requiresAuth = to.matched.some((r) => r.meta && r.meta.requiresAuth)
  const hideForAuth = to.matched.some((r) => r.meta && r.meta.hideForAuth)
  const roleSet = new Set()
  to.matched.forEach((r) => {
    const roles = r.meta && r.meta.roles
    if (Array.isArray(roles)) roles.forEach((x) => roleSet.add(x))
  })
  const requiredRoles = Array.from(roleSet)

  if (hideForAuth && isLoggedIn) {
    const role = user?.role
    return next(role === 'admin' ? '/admin/dashboard' : '/user/dashboard')
  }

  if (requiresAuth && !isLoggedIn) {
    return next('/login')
  }

  if (requiredRoles && requiredRoles.length > 0) {
    const userRole = user?.role
    if (!requiredRoles.includes(userRole)) {
      return next(userRole === 'admin' ? '/admin/dashboard' : '/user/dashboard')
    }
  }

  return next()
})

export default router
