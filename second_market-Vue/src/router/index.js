import { createRouter, createWebHistory } from 'vue-router'
import UserDashboard from '@/views/user/Dashboard.vue'
import UserProfile from '@/views/user/Profile.vue'
import Categories from '@/views/user/Categories.vue'

// 路由配置中心
// 引入页面组件
const Login = () => import('@/views/auth/LoginEntry.vue')
const Register = () => import('@/views/auth/RegisterEntry.vue')
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
const Favorites = () => import('@/views/user/Favorites.vue')
const Messages = () => import('@/views/user/Messages.vue')
const WriteReview = () => import('@/views/user/WriteReview.vue')
const Notice = () => import('@/views/user/Notice.vue')
const SearchView = () => import('@/views/user/SearchView.vue')
const MyPublish = () => import('@/views/user/MyPublish.vue')
const Settings = () => import('@/views/user/Settings.vue')
const AgreementPrivacy = () => import('@/views/user/AgreementPrivacy.vue')
const AgreementUser = () => import('@/views/user/AgreementUser.vue')
const Feedback = () => import('@/views/user/Feedback.vue')
const Verify = () => import('@/views/user/Verify.vue')
const Credit = () => import('@/views/user/Credit.vue')
const AdminLayout = () => import('@/layouts/AdminLayout.vue')
const NeoLayout = () => import('@/layouts/NeoLayout.vue')

// 新增AI功能页面
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

// 新增移动端功能页面
const RecentlyViewed = () => import('@/views/user/RecentlyViewed.vue')
const PopularDigital = () => import('@/views/user/PopularDigital.vue')
const CampusNearby = () => import('@/views/user/CampusNearby.vue')

// Neo 模块页面
const NeoHome = () => import('@/views/user/NeoHomeEntry.vue')
const NeoPlaceholder = () => import('@/views/user/NeoPlaceholderEntry.vue')
const NeoCheckoutEntry = () => import('@/views/user/NeoCheckoutEntry.vue')
const NeoAddressesEntry = () => import('@/views/user/NeoAddressesEntry.vue')
const NeoOrdersEntry = () => import('@/views/user/orders/NeoOrdersEntry.vue')
const NeoPayCenterSelect = () => import('@/views/user/pay/NeoPayCenterSelect.vue')
const NeoPayCenterCountdown = () => import('@/views/user/pay/NeoPayCenterCountdown.vue')
const NeoPayCenterSuccess = () => import('@/views/user/pay/NeoPayCenterSuccess.vue')
const NeoPayCenterFail = () => import('@/views/user/pay/NeoPayCenterFail.vue')
const NeoErrandsListEntry = () => import('@/views/user/errand/NeoErrandsListEntry.vue')
const NeoErrandNewEntry = () => import('@/views/user/errand/NeoErrandNewEntry.vue')
const NeoErrandDetailEntry = () => import('@/views/user/errand/NeoErrandDetailEntry.vue')
const NeoForumListEntry = () => import('@/views/user/forum/NeoForumListEntry.vue')
const NeoForumNewEntry = () => import('@/views/user/forum/NeoForumNewEntry.vue')
const NeoForumDetailEntry = () => import('@/views/user/forum/NeoForumDetailEntry.vue')

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
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
  // Neo 模块路由
  {
    path: '/neo',
    name: 'neo',
    component: NeoLayout,
    redirect: '/neo/home',
    meta: {
      requiresAuth: true,
      roles: ['user']
    },
    children: [
      {
        path: 'home',
        name: 'neo-home',
        component: NeoHome,
        meta: { title: 'AI智能聚合首页', requiresAuth: true }
      },
      {
        path: 'campus-categories',
        name: 'neo-campus-categories',
        component: NeoPlaceholder,
        meta: { title: '校园专属分类', requiresAuth: true }
      },
      {
        path: 'errands',
        name: 'neo-errands',
        component: NeoErrandsListEntry,
        meta: { title: '跑腿交易', requiresAuth: true }
      },
      {
        path: 'errands/new',
        name: 'neo-errands-new',
        component: NeoErrandNewEntry,
        meta: { title: '发布跑腿', requiresAuth: true }
      },
      {
        path: 'errands/:id',
        name: 'neo-errands-detail',
        component: NeoErrandDetailEntry,
        meta: { title: '跑腿详情', requiresAuth: true }
      },
      {
        path: 'forum',
        name: 'neo-forum',
        component: NeoForumListEntry,
        meta: { title: '论坛', requiresAuth: true }
      },
      {
        path: 'forum/new',
        name: 'neo-forum-new',
        component: NeoForumNewEntry,
        meta: { title: '发布帖子', requiresAuth: true }
      },
      {
        path: 'forum/:id',
        name: 'neo-forum-detail',
        component: NeoForumDetailEntry,
        meta: { title: '帖子详情', requiresAuth: true }
      },
      {
        path: 'orders/pending-pay',
        name: 'neo-orders-pending-pay',
        component: NeoOrdersEntry,
        meta: { title: '待付款订单', requiresAuth: true }
      },
      {
        path: 'orders/pending-ship',
        name: 'neo-orders-pending-ship',
        component: NeoOrdersEntry,
        meta: { title: '待发货订单', requiresAuth: true }
      },
      {
        path: 'orders/pending-receive',
        name: 'neo-orders-pending-receive',
        component: NeoOrdersEntry,
        meta: { title: '待收货订单', requiresAuth: true }
      },
      {
        path: 'orders/completed',
        name: 'neo-orders-completed',
        component: NeoOrdersEntry,
        meta: { title: '已完成订单', requiresAuth: true }
      },
      {
        path: 'orders/cancelled',
        name: 'neo-orders-cancelled',
        component: NeoOrdersEntry,
        meta: { title: '取消订单', requiresAuth: true }
      },
      {
        path: 'seller-center',
        name: 'neo-seller-center',
        component: NeoPlaceholder,
        meta: { title: '卖家管理后台', requiresAuth: true }
      },
      {
        path: 'chat',
        name: 'neo-chat',
        component: NeoPlaceholder,
        meta: { title: '实时聊天', requiresAuth: true }
      },
      {
        path: 'favorites',
        name: 'neo-favorites',
        component: NeoPlaceholder,
        meta: { title: '商品收藏夹', requiresAuth: true }
      },
      {
        path: 'follows',
        name: 'neo-follows',
        component: NeoPlaceholder,
        meta: { title: '店铺关注列表', requiresAuth: true }
      },
      {
        path: 'review-center',
        name: 'neo-review-center',
        component: NeoPlaceholder,
        meta: { title: '商品评价中心', requiresAuth: true }
      },
      {
        path: 'campus-deals',
        name: 'neo-campus-deals',
        component: NeoPlaceholder,
        meta: { title: '校园同城交易', requiresAuth: true }
      },
      {
        path: 'notification-center',
        name: 'neo-notification-center',
        component: NeoPlaceholder,
        meta: { title: '系统消息通知中心', requiresAuth: true }
      },
      {
        path: 'after-sales/refund',
        name: 'neo-after-sales-refund',
        component: NeoPlaceholder,
        meta: { title: '退款申请', requiresAuth: true }
      },
      {
        path: 'after-sales/progress',
        name: 'neo-after-sales-progress',
        component: NeoPlaceholder,
        meta: { title: '售后进度查询', requiresAuth: true }
      },
      {
        path: 'after-sales/dispute',
        name: 'neo-after-sales-dispute',
        component: NeoPlaceholder,
        meta: { title: '交易纠纷提交', requiresAuth: true }
      },
      {
        path: 'footprints',
        name: 'neo-footprints',
        component: NeoPlaceholder,
        meta: { title: '浏览足迹', requiresAuth: true }
      },
      {
        path: 'checkout',
        name: 'neo-checkout',
        component: NeoCheckoutEntry,
        meta: { title: '确认订单', requiresAuth: true }
      },
      {
        path: 'addresses',
        name: 'neo-addresses',
        component: NeoAddressesEntry,
        meta: { title: '收货地址管理', requiresAuth: true }
      },
      {
        path: 'pay-center',
        name: 'neo-pay-center',
        component: NeoPayCenterSelect,
        meta: { title: '模拟支付中心', requiresAuth: true }
      },
      {
        path: 'pay-center/countdown',
        name: 'neo-pay-center-countdown',
        component: NeoPayCenterCountdown,
        meta: { title: '支付中', requiresAuth: true }
      },
      {
        path: 'pay-center/success',
        name: 'neo-pay-center-success',
        component: NeoPayCenterSuccess,
        meta: { title: '支付成功', requiresAuth: true }
      },
      {
        path: 'pay-center/fail',
        name: 'neo-pay-center-fail',
        component: NeoPayCenterFail,
        meta: { title: '支付失败', requiresAuth: true }
      }
    ]
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
        component: NeoOrdersEntry,
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
        component: NeoErrandsListEntry,
        meta: { title: '跑腿', requiresAuth: true }
      },
      {
        path: 'errand/publish',
        name: 'user-errand-publish',
        component: NeoErrandNewEntry,
        meta: { title: '发跑腿单', requiresAuth: true, hideNav: true }
      },
      {
        path: 'errand/orders',
        name: 'user-errand-orders',
        component: NeoOrdersEntry,
        meta: { title: '跑腿订单', requiresAuth: true, hideNav: true }
      },
      {
        path: 'errand/detail/:id',
        name: 'user-errand-detail',
        component: NeoErrandDetailEntry,
        meta: { title: '跑腿详情', requiresAuth: true, hideNav: true }
      },
      {
        path: 'forum',
        name: 'user-forum',
        component: NeoForumListEntry,
        meta: {
          title: '校园论坛',
          requiresAuth: false
        }
      },
      {
        path: 'forum/detail/:id',
        name: 'user-forum-detail',
        component: NeoForumDetailEntry,
        meta: {
          title: '帖子详情',
          requiresAuth: false,
          hideNav: true
        }
      },
      {
        path: 'forum/publish',
        name: 'user-forum-publish',
        component: NeoForumNewEntry,
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
          title: 'AI鉴定质检',
          requiresAuth: true
        }
      },
      {
        path: 'ai-market',
        name: 'ai-market',
        component: AiMarketTrend,
        meta: {
          title: 'AI行情参考',
          requiresAuth: true
        }
      },
      {
        path: 'ai-trust',
        name: 'ai-trust',
        component: AiTrust,
        meta: {
          title: 'AI智能托管',
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
