import { createRouter, createWebHistory } from 'vue-router'

// 引入页面组件
const Login = () => import('@/views/auth/LoginEntry.vue')
const Register = () => import('@/views/auth/RegisterEntry.vue')
const UserLayout = () => import('@/layouts/UserLayout.vue')
const UserDashboard = () => import('@/views/user/DashboardEntry.vue')
const UserProfile = () => import('@/views/user/ProfileEntry.vue')
const ProductList = () => import('@/views/user/ProductListEntry.vue')
const Categories = () => import('@/views/user/CategoriesEntry.vue')
const ProductPublish = () => import('@/views/user/ProductPublishEntry.vue')
const ProductDetail = () => import('@/views/user/ProductDetail.vue')
const MyProducts = () => import('@/views/user/MyProductsEntry.vue')
const MyOrders = () => import('@/views/user/MyOrdersEntry.vue')
const Favorites = () => import('@/views/user/FavoritesEntry.vue')
const Messages = () => import('@/views/user/Messages.vue')
const WriteReview = () => import('@/views/user/WriteReview.vue')
const Notice = () => import('@/views/user/Notice.vue')
const AdminLayout = () => import('@/layouts/AdminLayout.vue')
const AdminDashboard = () => import('@/views/admin/Dashboard.vue')
const AdminUsers = () => import('@/views/admin/Users.vue')
const AdminProfile = () => import('@/views/admin/Profile.vue')
const AdminProductReview = () => import('@/views/admin/ProductReview.vue')
const AdminReportManage = () => import('@/views/admin/ReportManage.vue')
const AdminNoticeManage = () => import('@/views/admin/NoticeManage.vue')

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
        name: 'user-products',
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
          title: '分类',
          requiresAuth: true
        }
      },
      {
        path: 'publish',
        name: 'user-publish',
        component: ProductPublish,
        meta: {
          title: '发布商品',
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
