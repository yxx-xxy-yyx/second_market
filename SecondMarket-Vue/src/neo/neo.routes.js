const NeoLayout = () => import('@/neo/NeoLayout.vue')
const NeoHome = () => import('@/neo/views/NeoHomeEntry.vue')
const NeoPlaceholder = () => import('@/neo/views/NeoPlaceholderEntry.vue')
const NeoCartEntry = () => import('@/neo/views/NeoCartEntry.vue')
const NeoCheckoutEntry = () => import('@/neo/views/NeoCheckoutEntry.vue')
const NeoAddressesEntry = () => import('@/neo/views/NeoAddressesEntry.vue')
const NeoOrdersEntry = () => import('@/neo/views/orders/NeoOrdersEntry.vue')
const NeoPayCenterSelect = () => import('@/neo/views/pay/NeoPayCenterSelect.vue')
const NeoPayCenterCountdown = () => import('@/neo/views/pay/NeoPayCenterCountdown.vue')
const NeoPayCenterSuccess = () => import('@/neo/views/pay/NeoPayCenterSuccess.vue')
const NeoPayCenterFail = () => import('@/neo/views/pay/NeoPayCenterFail.vue')

export const neoRoutes = [
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
        meta: {
          title: 'AI智能聚合首页',
          requiresAuth: true
        }
      },
      {
        path: 'campus-categories',
        name: 'neo-campus-categories',
        component: NeoPlaceholder,
        meta: { title: '校园专属分类', requiresAuth: true }
      },
      {
        path: 'cart',
        name: 'neo-cart',
        component: NeoCartEntry,
        meta: { title: '购物车', requiresAuth: true }
      },
      {
        path: 'checkout',
        name: 'neo-checkout',
        component: NeoCheckoutEntry,
        meta: { title: '确认订单', requiresAuth: true }
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
        meta: { title: '支付倒计时', requiresAuth: true }
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
      }
    ]
  }
]
