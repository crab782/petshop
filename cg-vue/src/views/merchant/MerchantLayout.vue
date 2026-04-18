<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { HomeFilled, ShoppingBag, Calendar, Cubes, Star, SignOut, Bell, ArrowDown, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

const menuItems = [
  { path: '/merchant/home', name: '后台首页', icon: 'HomeFilled' },
  { path: '/merchant/shop/edit', name: '店铺管理', icon: 'ShoppingBag' },
  { path: '/merchant/appointments', name: '预约订单列表', icon: 'Calendar' },
  { path: '/merchant/services', name: '服务管理', icon: 'Cubes' },
  { path: '/merchant/reviews', name: '服务评价列表', icon: 'Star' }
]

const handleMenuSelect = (path: string) => {
  router.push(path)
}

const handleLogout = () => {
  router.push('/login')
}
</script>

<template>
  <el-container class="merchant-layout">
    <el-header class="header">
      <div class="header-left">
        <el-button text @click="isCollapse = !isCollapse" class="menu-toggle">
          <el-icon size="20"><ArrowDown /></el-icon>
        </el-button>
        <div class="logo-area">
          <i class="fa fa-paw text-white text-2xl mr-2"></i>
          <span class="merchant-name">宠物家园</span>
        </div>
      </div>
      <div class="header-right">
        <div class="relative mr-4">
          <el-button text class="notification-btn">
            <el-icon><Bell /></el-icon>
            <span class="notification-badge">5</span>
          </el-button>
        </div>
        <el-dropdown trigger="click">
          <div class="user-info">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20merchant%20avatar%2C%20business%20person&image_size=square" alt="商家头像" class="user-avatar">
            <span class="username">商家名称</span>
            <i class="fa fa-caret-down text-white ml-1"></i>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>
                <el-icon><Setting /></el-icon>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item @click="handleLogout">
                <el-icon><SignOut /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container class="main-container">
      <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
        <el-menu
          :default-active="route.path"
          :collapse="isCollapse"
          class="menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/merchant/home">
            <el-icon><HomeFilled /></el-icon>
            <template #title>后台首页</template>
          </el-menu-item>
          <el-menu-item index="/merchant/profile">
            <el-icon><ShoppingBag /></el-icon>
            <template #title>店铺管理</template>
          </el-menu-item>
          <el-menu-item index="/merchant/appointments">
            <el-icon><Calendar /></el-icon>
            <template #title>预约订单列表</template>
          </el-menu-item>
          <el-menu-item index="/merchant/services">
            <el-icon><Cubes /></el-icon>
            <template #title>服务管理</template>
          </el-menu-item>
          <el-menu-item index="/merchant/reviews">
            <el-icon><Star /></el-icon>
            <template #title>服务评价列表</template>
          </el-menu-item>
          <el-menu-item index="/logout" @click="handleLogout">
            <el-icon><SignOut /></el-icon>
            <template #title>退出登录</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.merchant-layout {
  height: 100vh;
  width: 100%;
  font-family: Arial, sans-serif;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #4CAF50;
  color: white;
  padding: 0 24px;
  height: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.menu-toggle {
  color: white;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 8px;
}

.merchant-name {
  font-size: 20px;
  font-weight: bold;
  color: white;
}

.header-right {
  display: flex;
  align-items: center;
}

.notification-btn {
  color: white;
  position: relative;
}

.notification-badge {
  position: absolute;
  top: -1px;
  right: -1px;
  width: 16px;
  height: 16px;
  background-color: #f56c6c;
  border-radius: 50%;
  color: white;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.user-info:hover .user-avatar {
  transform: scale(1.05);
}

.username {
  color: white;
  font-size: 14px;
  font-weight: medium;
}

.main-container {
  height: calc(100vh - 60px);
  display: flex;
}

.aside {
  background-color: white;
  border-right: 1px solid #e0e0e0;
  transition: width 0.3s;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.menu {
  border-right: none;
  background-color: white;
  height: 100%;
}

:deep(.el-menu) {
  background-color: white;
  border-right: none;
}

:deep(.el-menu-item) {
  color: #333333;
  margin: 8px 12px;
  border-radius: 8px;
  height: 48px;
  line-height: 48px;
  transition: all 0.3s ease;
}

:deep(.el-menu-item:hover),
:deep(.el-menu-item.is-active) {
  background-color: #4CAF50;
  color: white;
  transform: translateX(4px);
}

.main {
  flex: 1;
  background: #f5f5f5;
  padding: 24px;
  overflow-y: auto;
  min-height: calc(100vh - 60px);
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
    top: 60px;
    z-index: 1000;
    height: calc(100vh - 60px);
  }

  .main {
    padding: 16px;
  }

  .header-right {
    gap: 12px;
  }

  .user-info span:not(.user-avatar) {
    display: none;
  }

  .merchant-name {
    font-size: 18px;
  }
}
</style>
