import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('../views/AdminLogin.vue')
  },
  {
    path: '/user/dashboard',
    name: 'UserDashboard',
    component: () => import('../views/UserDashboard.vue')
  },
  {
    path: '/merchant/dashboard',
    name: 'MerchantDashboard',
    component: () => import('../views/MerchantDashboard.vue')
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('../views/AdminDashboard.vue')
  },
  {
    path: '/service/boarding',
    name: 'ServiceBoarding',
    component: () => import('../views/ServiceBoarding.vue')
  },
  {
    path: '/service/grooming',
    name: 'ServiceGrooming',
    component: () => import('../views/ServiceGrooming.vue')
  },
  {
    path: '/service/training',
    name: 'ServiceTraining',
    component: () => import('../views/ServiceTraining.vue')
  },
  {
    path: '/service/health',
    name: 'ServiceHealth',
    component: () => import('../views/ServiceHealth.vue')
  },
  {
    path: '/service/transport',
    name: 'ServiceTransport',
    component: () => import('../views/ServiceTransport.vue')
  },
  {
    path: '/service/food',
    name: 'ServiceFood',
    component: () => import('../views/ServiceFood.vue')
  },
  {
    path: '/user/add-pet',
    name: 'UserAddPet',
    component: () => import('../views/UserAddPet.vue')
  },
  {
    path: '/merchant/add-service',
    name: 'MerchantAddService',
    component: () => import('../views/MerchantAddService.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router