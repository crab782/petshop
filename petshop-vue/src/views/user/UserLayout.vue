<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  User,
  HomeFilled,
  Goods,
  Calendar,
  Setting,
  ScaleToOriginal,
  SwitchButton,
  Fold,
  Expand,
  Search,
  Bell,
  Star,
  Ticket,
  Shop,
  ShoppingCart,
  List,
  Document,
  Edit,
  Notification,
  Location,
  ChatDotRound,
  Reading,
  House,
  UserFilled
} from '@element-plus/icons-vue'
import { getCart } from '@/api/user'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)
const searchQuery = ref('')
const userInfo = ref<any>(null)
const cartCount = ref(0)

// 静态菜单数据，避免每次渲染重新创建
const menuItems = [
  { key: '/user/home', icon: Shop, label: '商店浏览' },
  { key: '/user/services', icon: ScaleToOriginal, label: '服务浏览' },
  { key: '/user/services/list', icon: List, label: '服务列表' },
  { key: '/user/appointments/book', icon: Calendar, label: '预约服务' },
  { key: '/user/pets', icon: Goods, label: '宠物管理' },
  { key: '/user/appointments', icon: Ticket, label: '预约管理' },
  { key: '/user/orders', icon: Document, label: '订单管理' },
  { key: '/user/favorites', icon: Star, label: '收藏评价' },
  { key: '/user/profile', icon: Setting, label: '个人中心' },
  { key: '/user/profile/edit', icon: Edit, label: '编辑资料' },
  { key: '/user/announcements', icon: Reading, label: '公告列表' },
  { key: '/user/shop', icon: House, label: '店铺详情' },
  { key: '/user/cart', icon: ShoppingCart, label: '购物车' },
  { key: '/user/merchant/1', icon: Shop, label: '商家详情' },
  { key: '/user/reviews', icon: ChatDotRound, label: '服务评价' },
  { key: '/user/reviews/my', icon: Star, label: '我的评价' },
  { key: '/user/search', icon: Search, label: '搜索页' },
  { key: '/user/notifications', icon: Notification, label: '消息通知' },
  { key: '/user/addresses', icon: Location, label: '收货地址' }
]

const username = computed(() => {
  if (userInfo.value?.username) {
    return userInfo.value.username
  }
  if (userInfo.value?.phone) {
    return userInfo.value.phone
  }
  return '用户'
})

const handleCommand = (command: string) => {
  if (command === 'logout') {
    localStorage.removeItem('user_token')
    localStorage.removeItem('userInfo')
    sessionStorage.removeItem('user_token')
    sessionStorage.removeItem('userInfo')
    router.push('/login')
  }
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ path: '/user/search', query: { keyword: searchQuery.value } })
  }
}

const loadUserInfo = () => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    try {
      userInfo.value = JSON.parse(stored)
    } catch (e) {
      console.error('Failed to parse userInfo:', e)
    }
  }
}

const loadCartCount = async () => {
  try {
    const data = await getCart()
    cartCount.value = data.length
  } catch (e) {
    console.error('Failed to load cart count:', e)
  }
}

// 优化：只在需要时加载数据
onMounted(() => {
  loadUserInfo()
  loadCartCount()
})

// 优化：监听路由变化，避免不必要的重渲染
const routePath = ref(route.path)
watch(() => route.path, (newPath) => {
  routePath.value = newPath
}, { deep: false })

// 优化：确保组件销毁时清理资源
onUnmounted(() => {
  // 清理逻辑
})
</script>

<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
      <div class="logo-area">
        <i class="fa fa-paw text-primary text-2xl mr-2"></i>
        <span v-if="!isCollapse" class="logo-text">宠物家园</span>
      </div>
      <el-menu
        :default-active="routePath"
        :collapse="isCollapse"
        :collapse-transition="false"
        class="menu"
        router
      >
        <el-menu-item 
          v-for="item in menuItems" 
          :key="item.key" 
          :index="item.key"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.label }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-button text @click="isCollapse = !isCollapse" class="menu-toggle">
            <el-icon :size="20"><component :is="isCollapse ? Expand : Fold" /></el-icon>
          </el-button>
          <div class="search-container">
            <el-input
              v-model="searchQuery"
              placeholder="搜索服务、商品、商家"
              prefix-icon="Search"
              @keyup.enter="handleSearch"
              class="search-input"
            >
              <template #append>
                <el-button @click="handleSearch" type="primary" :icon="Search" />
              </template>
            </el-input>
          </div>
        </div>
        <div class="header-right">
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
            <el-button text @click="router.push('/user/cart')" class="cart-btn">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </el-button>
          </el-badge>
          <el-badge value="3" class="notification-badge">
            <el-button text @click="router.push('/user/notifications')" class="notification-btn">
              <el-icon :size="20"><Bell /></el-icon>
            </el-button>
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  min-height: 100vh;
  height: 100%;
  font-family: Arial, sans-serif;
}

.aside {
  background-color: #f5f5f5;
  border-right: 1px solid #e0e0e0;
  transition: width 0.3s;
  overflow: hidden;
}

.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #4CAF50;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #e0e0e0;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo-text {
  white-space: nowrap;
}

.menu {
  border-right: none;
  background-color: #f5f5f5;
  height: 100%;
  min-height: calc(100vh - 60px);
}

.menu :deep(.el-menu-item) {
  color: #333333;
  margin: 8px 12px;
  border-radius: 8px;
  height: 48px;
  line-height: 48px;
  transition: all 0.3s ease;
}

.menu :deep(.el-menu-item:hover),
.menu :deep(.el-menu-item.is-active) {
  background-color: #4CAF50;
  color: white;
  transform: translateX(4px);
}

.header {
  background-color: #4CAF50;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
}

.menu-toggle {
  color: white;
}

.search-container {
  flex: 1;
  max-width: 500px;
}

.search-input {
  background-color: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 20px;
  color: white;
}

.search-input :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 20px;
}

.search-input :deep(.el-input__placeholder) {
  color: rgba(255, 255, 255, 0.7);
}

.search-input :deep(.el-input__inner) {
  color: white;
}

.search-input :deep(.el-input__prefix-inner) {
  color: rgba(255, 255, 255, 0.7);
}

.notification-badge {
  margin-right: 16px;
}

.cart-badge {
  margin-right: 16px;
}

.cart-btn {
  color: white;
  transition: all 0.3s ease;
}

.cart-btn:hover {
  color: #f5f5f5;
  transform: scale(1.1);
}

.notification-btn {
  color: white;
  transition: all 0.3s ease;
}

.notification-btn:hover {
  color: #f5f5f5;
  transform: scale(1.1);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: white;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.username {
  color: white;
  font-size: 14px;
  font-weight: medium;
}

.main {
  background-color: #f5f5f5;
  padding: 20px;
  overflow-y: auto;
  flex: 1;
  min-height: 0;
}

:deep(.el-avatar) {
  background-color: rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.user-info:hover :deep(.el-avatar) {
  transform: scale(1.05);
}

:deep(.el-dropdown-menu) {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border: none;
}

:deep(.el-dropdown-item) {
  padding: 8px 16px;
  transition: all 0.3s ease;
}

:deep(.el-dropdown-item:hover) {
  background-color: #f5f5f5;
  transform: translateX(4px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .aside {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1000;
    height: 100vh;
  }

  .main {
    padding: 16px;
  }
}
</style>
