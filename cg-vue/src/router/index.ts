import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

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
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/login/Login.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/login/Register.vue'),
    },
    {
      path: '/merchant/register',
      name: 'merchant-register',
      component: () => import('../views/merchant/Register.vue'),
    },
    {
      path: '/merchant/login',
      name: 'merchant-login',
      component: () => import('../views/merchant/Login.vue'),
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
        },
        {
          path: 'services',
          name: 'user-services',
          component: () => import('../views/user/user-services/index.vue'),
        },
        {
          path: 'services/list',
          name: 'service-list',
          component: () => import('../views/user/service-list/index.vue'),
        },
        {
          path: 'services/detail/:id',
          name: 'service-detail',
          component: () => import('../views/user/service-detail/index.vue'),
        },
        {
          path: 'pets',
          name: 'user-pets',
          component: () => import('../views/user/user-pets/index.vue'),
        },
        {
          path: 'pets/add',
          name: 'pet-add',
          component: () => import('../views/user/pet-edit/index.vue'),
        },
        {
          path: 'pets/edit/:id',
          name: 'pet-edit',
          component: () => import('../views/user/pet-edit/index.vue'),
        },
        {
          path: 'appointments',
          name: 'user-appointments',
          component: () => import('../views/user/user-appointments/index.vue'),
        },
        {
          path: 'appointments/book',
          name: 'user-book',
          component: () => import('../views/user/user-book/index.vue'),
        },
        {
          path: 'appointments/confirm',
          name: 'appointment-confirm',
          component: () => import('../views/user/appointment-confirm/index.vue'),
        },
        {
          path: 'profile',
          name: 'user-profile',
          component: () => import('../views/user/user-profile/index.vue'),
        },
        {
          path: 'profile/edit',
          name: 'profile-edit',
          component: () => import('../views/user/profile-edit/index.vue'),
        },
        {
          path: 'announcements',
          name: 'user-announcements',
          component: () => import('../views/user/user-announcements/index.vue'),
        },
        {
          path: 'announcements/detail/:id',
          name: 'announcement-detail',
          component: () => import('../views/user/announcement-detail/index.vue'),
        },
        {
          path: 'shop',
          name: 'user-shop',
          component: () => import('../views/user/user-shop/index.vue'),
        },
        {
          path: 'cart',
          name: 'user-cart',
          component: () => import('../views/user/user-cart/index.vue'),
        },
        {
          path: 'checkout',
          name: 'checkout',
          component: () => import('../views/user/checkout/index.vue'),
        },
        {
          path: 'pay',
          name: 'pay',
          component: () => import('../views/user/pay/index.vue'),
        },
        {
          path: 'merchant/:id',
          name: 'user-merchant',
          component: () => import('../views/user/user-merchant/index.vue'),
        },
        {
          path: 'favorites',
          name: 'user-favorites',
          component: () => import('../views/user/user-favorites/index.vue'),
        },
        {
          path: 'reviews',
          name: 'user-reviews',
          component: () => import('../views/user/user-reviews/index.vue'),
        },
        {
          path: 'reviews/my',
          name: 'my-reviews',
          component: () => import('../views/user/my-reviews/index.vue'),
        },
        {
          path: 'orders',
          name: 'user-orders',
          component: () => import('../views/user/user-orders/index.vue'),
        },
        {
          path: 'orders/detail/:id',
          name: 'order-detail',
          component: () => import('../views/user/order-detail/index.vue'),
        },
        {
          path: 'search',
          name: 'search',
          component: () => import('../views/user/search/index.vue'),
        },
        {
          path: 'notifications',
          name: 'notifications',
          component: () => import('../views/user/notifications/index.vue'),
        },
        {
          path: 'addresses',
          name: 'addresses',
          component: () => import('../views/user/addresses/index.vue'),
        },
        {
          path: 'product/detail/:id',
          name: 'product-detail',
          component: () => import('../views/user/product-detail/index.vue'),
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
        },
        {
          path: 'services',
          name: 'merchant-services',
          component: () => import('../views/merchant/merchant-services/index.vue'),
        },
        {
          path: 'services/add',
          name: 'service-add',
          component: () => import('../views/merchant/service-edit/index.vue'),
        },
        {
          path: 'services/edit/:id',
          name: 'service-edit',
          component: () => import('../views/merchant/service-edit/index.vue'),
        },
        {
          path: 'orders',
          name: 'merchant-orders',
          component: () => import('../views/merchant/merchant-orders/index.vue'),
        },
        {
          path: 'products',
          name: 'merchant-products',
          component: () => import('../views/merchant/merchant-products/index.vue'),
        },
        {
          path: 'products/add',
          name: 'product-add',
          component: () => import('../views/merchant/product-edit/index.vue'),
        },
        {
          path: 'products/edit/:id',
          name: 'product-edit',
          component: () => import('../views/merchant/product-edit/index.vue'),
        },
        {
          path: 'appointments',
          name: 'merchant-appointments',
          component: () => import('../views/merchant/merchant-appointments/index.vue'),
        },
        {
          path: 'product-orders',
          name: 'merchant-product-orders',
          component: () => import('../views/merchant/merchant-product-orders/index.vue'),
        },
        {
          path: 'reviews',
          name: 'merchant-reviews',
          component: () => import('../views/merchant/merchant-reviews/index.vue'),
        },
        {
          path: 'categories',
          name: 'merchant-categories',
          component: () => import('../views/merchant/categories/index.vue'),
        },
        {
          path: 'shop/edit',
          name: 'shop-edit',
          component: () => import('../views/merchant/shop-edit/index.vue'),
        },
        {
          path: 'shop/settings',
          name: 'shop-settings',
          component: () => import('../views/merchant/shop-settings/index.vue'),
        },
        {
          path: 'stats/appointments',
          name: 'stats-appointments',
          component: () => import('../views/merchant/stats-appointments/index.vue'),
        },
        {
          path: 'stats/revenue',
          name: 'stats-revenue',
          component: () => import('../views/merchant/stats-revenue/index.vue'),
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
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('../views/admin/admin-users/index.vue'),
        },
        {
          path: 'users/:id',
          name: 'admin-user-detail',
          component: () => import('../views/admin/user-detail/index.vue'),
        },
        {
          path: 'merchants',
          name: 'admin-merchants',
          component: () => import('../views/admin/admin-merchants/index.vue'),
        },
        {
          path: 'merchants/:id',
          name: 'merchant-detail',
          component: () => import('../views/admin/merchant-detail/index.vue'),
        },
        {
          path: 'merchants/audit',
          name: 'merchant-audit',
          component: () => import('../views/admin/merchant-audit/index.vue'),
        },
        {
          path: 'services',
          name: 'admin-services',
          component: () => import('../views/admin/admin-services/index.vue'),
        },
        {
          path: 'products',
          name: 'admin-products',
          component: () => import('../views/admin/admin-products/index.vue'),
        },
        {
          path: 'products/manage',
          name: 'product-manage',
          component: () => import('../views/admin/product-manage/index.vue'),
        },
        {
          path: 'announcements',
          name: 'admin-announcements',
          component: () => import('../views/admin/admin-announcements/index.vue'),
        },
        {
          path: 'announcements/edit',
          name: 'announcement-edit',
          component: () => import('../views/admin/announcement-edit/index.vue'),
        },
        {
          path: 'announcements/edit/:id',
          name: 'announcement-edit-id',
          component: () => import('../views/admin/announcement-edit/index.vue'),
        },
        {
          path: 'system',
          name: 'admin-system',
          component: () => import('../views/admin/admin-system/index.vue'),
        },
        {
          path: 'system/roles',
          name: 'roles',
          component: () => import('../views/admin/roles/index.vue'),
        },
        {
          path: 'system/logs',
          name: 'logs',
          component: () => import('../views/admin/logs/index.vue'),
        },
        {
          path: 'reviews',
          name: 'admin-reviews',
          component: () => import('../views/admin/admin-reviews/index.vue'),
        },
        {
          path: 'reviews/audit',
          name: 'review-audit',
          component: () => import('../views/admin/review-audit/index.vue'),
        },
        {
          path: 'shop/audit',
          name: 'shop-audit',
          component: () => import('../views/admin/shop-audit/index.vue'),
        },
        {
          path: 'activities',
          name: 'admin-activities',
          component: () => import('../views/admin/admin-activities/index.vue'),
        },
        {
          path: 'tasks',
          name: 'admin-tasks',
          component: () => import('../views/admin/admin-tasks/index.vue'),
        },
      ],
    },
  ],
})

// 路由守卫（已禁用登录验证，仅用于调试）
router.beforeEach((to, from, next) => {
  // 临时禁用所有登录验证，方便直接访问页面进行测试
  next();
});

export default router
