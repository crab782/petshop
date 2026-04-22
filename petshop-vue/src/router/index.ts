import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const DEFAULT_TITLE = '宠物服务平台'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/user/home',
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
      meta: { title: '关于我们' },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/login/Login.vue'),
      meta: { title: '用户登录' },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/login/Register.vue'),
      meta: { title: '用户注册' },
    },
    {
      path: '/merchant/register',
      name: 'merchant-register',
      component: () => import('../views/merchant/Register.vue'),
      meta: { title: '商家入驻' },
    },
    {
      path: '/merchant/login',
      name: 'merchant-login',
      component: () => import('../views/merchant/Login.vue'),
      meta: { title: '商家登录' },
    },
    {
      path: '/admin/register',
      name: 'admin-register',
      component: () => import('../views/admin/Register.vue'),
      meta: { title: '平台注册' },
    },
    {
      path: '/admin/login',
      name: 'admin-login',
      component: () => import('../views/admin/Login.vue'),
      meta: { title: '平台登录' },
    },
    {
      path: '/user',
      component: () => import('../views/user/UserLayout.vue'),
      meta: { requiresAuth: true, role: 'user' },
      children: [
        {
          path: '',
          redirect: '/user/home',
        },
        {
          path: 'home',
          name: 'user-home',
          component: () => import('../views/user/user-home/index.vue'),
          meta: { title: '首页' },
        },
        {
          path: 'services',
          name: 'user-services',
          component: () => import('../views/user/user-services/index.vue'),
          meta: { title: '我的服务' },
        },
        {
          path: 'services/list',
          name: 'service-list',
          component: () => import('../views/user/service-list/index.vue'),
          meta: { title: '服务列表' },
        },
        {
          path: 'services/detail/:id',
          name: 'service-detail',
          component: () => import('../views/user/service-detail/index.vue'),
          meta: { title: '服务详情' },
        },
        {
          path: 'services/detail',
          redirect: '/user/services/detail/1',
        },
        {
          path: 'pets',
          name: 'user-pets',
          component: () => import('../views/user/user-pets/index.vue'),
          meta: { title: '我的宠物' },
        },
        {
          path: 'pets/add',
          name: 'pet-add',
          component: () => import('../views/user/pet-edit/index.vue'),
          meta: { title: '添加宠物' },
        },
        {
          path: 'pets/edit/:id',
          name: 'pet-edit',
          component: () => import('../views/user/pet-edit/index.vue'),
          meta: { title: '编辑宠物' },
        },
        {
          path: 'pets/edit',
          redirect: '/user/pets/add',
        },
        {
          path: 'appointments',
          name: 'user-appointments',
          component: () => import('../views/user/user-appointments/index.vue'),
          meta: { title: '我的预约' },
        },
        {
          path: 'appointments/book',
          name: 'user-book',
          component: () => import('../views/user/user-book/index.vue'),
          meta: { title: '预约服务' },
        },
        {
          path: 'appointments/confirm',
          name: 'appointment-confirm',
          component: () => import('../views/user/appointment-confirm/index.vue'),
          meta: { title: '确认预约' },
        },
        {
          path: 'profile',
          name: 'user-profile',
          component: () => import('../views/user/user-profile/index.vue'),
          meta: { title: '个人中心' },
        },
        {
          path: 'profile/edit',
          name: 'profile-edit',
          component: () => import('../views/user/profile-edit/index.vue'),
          meta: { title: '编辑资料' },
        },
        {
          path: 'announcements',
          name: 'user-announcements',
          component: () => import('../views/user/user-announcements/index.vue'),
          meta: { title: '公告列表' },
        },
        {
          path: 'announcements/detail/:id',
          name: 'announcement-detail',
          component: () => import('../views/user/announcement-detail/index.vue'),
          meta: { title: '公告详情' },
        },
        {
          path: 'announcements/detail',
          redirect: '/user/announcements/detail/1',
        },
        {
          path: 'shop/:id',
          name: 'user-shop',
          component: () => import('../views/user/user-shop/index.vue'),
          meta: { title: '店铺详情' },
        },
        {
          path: 'cart',
          name: 'user-cart',
          component: () => import('../views/user/user-cart/index.vue'),
          meta: { title: '购物车' },
        },
        {
          path: 'checkout',
          name: 'checkout',
          component: () => import('../views/user/checkout/index.vue'),
          meta: { title: '确认订单' },
        },
        {
          path: 'pay',
          name: 'pay',
          component: () => import('../views/user/pay/index.vue'),
          meta: { title: '支付页' },
        },
        {
          path: 'merchant/:id',
          name: 'user-merchant',
          component: () => import('../views/user/user-merchant/index.vue'),
          meta: { title: '商家详情' },
        },
        {
          path: 'merchant/',
          redirect: '/user/home',
        },
        {
          path: 'favorites',
          name: 'user-favorites',
          component: () => import('../views/user/user-favorites/index.vue'),
          meta: { title: '我的收藏' },
        },
        {
          path: 'reviews',
          name: 'user-reviews',
          component: () => import('../views/user/user-reviews/index.vue'),
          meta: { title: '服务评价' },
        },
        {
          path: 'reviews/my',
          name: 'my-reviews',
          component: () => import('../views/user/my-reviews/index.vue'),
          meta: { title: '我的评价' },
        },
        {
          path: 'orders',
          name: 'user-orders',
          component: () => import('../views/user/user-orders/index.vue'),
          meta: { title: '我的订单' },
        },
        {
          path: 'orders/detail/:id',
          name: 'order-detail',
          component: () => import('../views/user/order-detail/index.vue'),
          meta: { title: '订单详情' },
        },
        {
          path: 'orders/detail/',
          redirect: '/user/orders/detail/1',
        },
        {
          path: 'search',
          name: 'search',
          component: () => import('../views/user/search/index.vue'),
          meta: { title: '搜索页' },
        },
        {
          path: 'notifications',
          name: 'notifications',
          component: () => import('../views/user/notifications/index.vue'),
          meta: { title: '消息通知' },
        },
        {
          path: 'addresses',
          name: 'addresses',
          component: () => import('../views/user/addresses/index.vue'),
          meta: { title: '收货地址' },
        },
        {
          path: 'product/detail/:id',
          name: 'product-detail',
          component: () => import('../views/user/product-detail/index.vue'),
          meta: { title: '商品详情' },
        },
      ],
    },
    {
      path: '/merchant',
      component: () => import('../views/merchant/MerchantLayout.vue'),
      meta: { requiresAuth: true, role: 'merchant' },
      children: [
        {
          path: '',
          redirect: '/merchant/home',
        },
        {
          path: 'home',
          name: 'merchant-home',
          component: () => import('../views/merchant/merchant-home/index.vue'),
          meta: { title: '商家首页' },
        },
        {
          path: 'services',
          name: 'merchant-services',
          component: () => import('../views/merchant/merchant-services/index.vue'),
          meta: { title: '服务管理' },
        },
        {
          path: 'services/add',
          name: 'service-add',
          component: () => import('../views/merchant/service-edit/index.vue'),
          meta: { title: '添加服务' },
        },
        {
          path: 'services/edit/:id',
          name: 'service-edit',
          component: () => import('../views/merchant/service-edit/index.vue'),
          meta: { title: '编辑服务' },
        },
        {
          path: 'orders',
          name: 'merchant-orders',
          component: () => import('../views/merchant/merchant-orders/index.vue'),
          meta: { title: '服务订单' },
        },
        {
          path: 'products',
          name: 'merchant-products',
          component: () => import('../views/merchant/merchant-products/index.vue'),
          meta: { title: '商品管理' },
        },
        {
          path: 'products/add',
          name: 'product-add',
          component: () => import('../views/merchant/product-edit/index.vue'),
          meta: { title: '添加商品' },
        },
        {
          path: 'products/edit/:id',
          name: 'product-edit',
          component: () => import('../views/merchant/product-edit/index.vue'),
          meta: { title: '编辑商品' },
        },
        {
          path: 'appointments',
          name: 'merchant-appointments',
          component: () => import('../views/merchant/merchant-appointments/index.vue'),
          meta: { title: '预约管理' },
        },
        {
          path: 'product-orders',
          name: 'merchant-product-orders',
          component: () => import('../views/merchant/merchant-product-orders/index.vue'),
          meta: { title: '商品订单' },
        },
        {
          path: 'reviews',
          name: 'merchant-reviews',
          component: () => import('../views/merchant/merchant-reviews/index.vue'),
          meta: { title: '评价管理' },
        },
        {
          path: 'categories',
          name: 'merchant-categories',
          component: () => import('../views/merchant/categories/index.vue'),
          meta: { title: '分类管理' },
        },
        {
          path: 'shop/edit',
          name: 'shop-edit',
          component: () => import('../views/merchant/shop-edit/index.vue'),
          meta: { title: '店铺信息' },
        },
        {
          path: 'shop/settings',
          name: 'shop-settings',
          component: () => import('../views/merchant/shop-settings/index.vue'),
          meta: { title: '店铺设置' },
        },
        {
          path: 'stats/appointments',
          name: 'stats-appointments',
          component: () => import('../views/merchant/stats-appointments/index.vue'),
          meta: { title: '预约统计' },
        },
        {
          path: 'stats/revenue',
          name: 'stats-revenue',
          component: () => import('../views/merchant/stats-revenue/index.vue'),
          meta: { title: '营收统计' },
        },
      ],
    },
    {
      path: '/admin',
      component: () => import('../views/admin/AdminLayout.vue'),
      meta: { requiresAuth: true, role: 'admin' },
      children: [
        {
          path: '',
          redirect: '/admin/dashboard',
        },
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: () => import('../views/admin/admin-dashboard/index.vue'),
          meta: { title: '管理后台' },
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('../views/admin/admin-users/index.vue'),
          meta: { title: '用户管理' },
        },
        {
          path: 'users/:id',
          name: 'admin-user-detail',
          component: () => import('../views/admin/user-detail/index.vue'),
          meta: { title: '用户详情' },
        },
        {
          path: 'merchants',
          name: 'admin-merchants',
          component: () => import('../views/admin/admin-merchants/index.vue'),
          meta: { title: '商家管理' },
        },
        {
          path: 'merchants/:id',
          name: 'merchant-detail',
          component: () => import('../views/admin/merchant-detail/index.vue'),
          meta: { title: '商家详情' },
        },
        {
          path: 'merchants/audit',
          name: 'merchant-audit',
          component: () => import('../views/admin/merchant-audit/index.vue'),
          meta: { title: '商家审核' },
        },
        {
          path: 'services',
          name: 'admin-services',
          component: () => import('../views/admin/admin-services/index.vue'),
          meta: { title: '服务管理' },
        },
        {
          path: 'products',
          name: 'admin-products',
          component: () => import('../views/admin/admin-products/index.vue'),
          meta: { title: '商品管理' },
        },
        {
          path: 'products/manage',
          name: 'product-manage',
          component: () => import('../views/admin/product-manage/index.vue'),
          meta: { title: '商品详情' },
        },
        {
          path: 'announcements',
          name: 'admin-announcements',
          component: () => import('../views/admin/admin-announcements/index.vue'),
          meta: { title: '公告管理' },
        },
        {
          path: 'announcements/edit',
          name: 'announcement-edit',
          component: () => import('../views/admin/announcement-edit/index.vue'),
          meta: { title: '编辑公告' },
        },
        {
          path: 'announcements/edit/:id',
          name: 'announcement-edit-id',
          component: () => import('../views/admin/announcement-edit/index.vue'),
          meta: { title: '编辑公告' },
        },
        {
          path: 'system',
          name: 'admin-system',
          component: () => import('../views/admin/admin-system/index.vue'),
          meta: { title: '系统设置' },
        },
        {
          path: 'system/roles',
          name: 'roles',
          component: () => import('../views/admin/roles/index.vue'),
          meta: { title: '角色管理' },
        },
        {
          path: 'system/logs',
          name: 'logs',
          component: () => import('../views/admin/logs/index.vue'),
          meta: { title: '操作日志' },
        },
        {
          path: 'reviews',
          name: 'admin-reviews',
          component: () => import('../views/admin/admin-reviews/index.vue'),
          meta: { title: '评价管理' },
        },
        {
          path: 'reviews/audit',
          name: 'review-audit',
          component: () => import('../views/admin/review-audit/index.vue'),
          meta: { title: '评价审核' },
        },
        {
          path: 'shop/audit',
          name: 'shop-audit',
          component: () => import('../views/admin/shop-audit/index.vue'),
          meta: { title: '店铺审核' },
        },
        {
          path: 'activities',
          name: 'admin-activities',
          component: () => import('../views/admin/admin-activities/index.vue'),
          meta: { title: '活动管理' },
        },
        {
          path: 'tasks',
          name: 'admin-tasks',
          component: () => import('../views/admin/admin-tasks/index.vue'),
          meta: { title: '任务管理' },
        },
      ],
    },
  ],
})

router.beforeEach((to, from, next) => {
  const title = to.meta.title as string
  if (title) {
    document.title = `${title} - ${DEFAULT_TITLE}`
  } else {
    document.title = DEFAULT_TITLE
  }
  next()
})

export default router
