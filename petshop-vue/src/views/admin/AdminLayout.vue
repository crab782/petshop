<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  HomeFilled,
  User,
  Shop,
  Service,
  Calendar,
  Grid,
  Star,
  Notification,
  Setting,
  List,
  Bell,
  Goods,
  Document,
  DataLine,
  TrendCharts,
  FolderOpened,
  Checked,
  Timer,
  Fold,
  Expand,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const isCollapse = ref(false)

// 静态菜单数据，避免每次渲染重新创建
const menuItems = [
  { key: '/admin/dashboard', icon: HomeFilled, label: '后台首页' },
  { key: '/admin/users', icon: User, label: '用户管理' },
  { key: '/admin/merchants', icon: Shop, label: '商家管理' },
  { key: '/admin/merchants/audit', icon: Checked, label: '商家审核' },
  { key: '/admin/services', icon: Service, label: '服务管理' },
  { key: '/admin/products', icon: Goods, label: '商品管理' },
  { key: '/admin/reviews', icon: Star, label: '评价管理' },
  { key: '/admin/reviews/audit', icon: Checked, label: '评价审核' },
  { key: '/admin/announcements', icon: Notification, label: '公告管理' },
  { key: '/admin/shop/audit', icon: Checked, label: '店铺审核' },
  { key: '/admin/activities', icon: TrendCharts, label: '活动管理' },
  { key: '/admin/tasks', icon: Timer, label: '任务管理' },
  { key: '/admin/system', icon: Setting, label: '系统设置' },
  { key: '/admin/system/roles', icon: User, label: '角色管理' },
  { key: '/admin/system/logs', icon: Document, label: '操作日志' }
]

// 优化：监听路由变化，避免不必要的重渲染
const routePath = ref(route.path)
watch(() => route.path, (newPath) => {
  routePath.value = newPath
}, { deep: false })

const handleMenuSelect = (path: string) => {
  router.push(path)
}

const handleLogout = () => {
  localStorage.removeItem('admin_token')
  sessionStorage.removeItem('admin_token')
  localStorage.removeItem('adminInfo')
  sessionStorage.removeItem('adminInfo')
  router.push('/admin/login')
}

// 优化：确保组件销毁时清理资源
onUnmounted(() => {
  // 清理逻辑
})
</script>

<template>
  <el-container class="admin-layout">
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
        <el-menu-item index="/logout" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <template #title>退出登录</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-button text @click="isCollapse = !isCollapse" class="menu-toggle">
            <el-icon :size="20"><component :is="isCollapse ? Expand : Fold" /></el-icon>
          </el-button>
        </div>
        <div class="header-right">
          <div class="relative mr-4">
            <el-button text class="notification-btn">
              <el-icon><Bell /></el-icon>
              <span class="notification-badge">8</span>
            </el-button>
          </div>
          <el-dropdown trigger="click">
            <span class="user-info">
              <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20admin%20avatar%2C%20business%20professional&image_size=square" alt="管理员头像" class="user-avatar">
              <span>管理员</span>
              <i class="fa fa-caret-down text-white ml-1"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <el-icon><Setting /></el-icon>
                  个人设置
                </el-dropdown-item>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><List /></el-icon>
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
.admin-layout {
  height: 100vh;
  font-family: Arial, sans-serif;
}

.aside {
  background-color: white;
  border-right: 1px solid #e0e0e0;
  transition: width 0.3s;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
  background-color: white;
  height: calc(100vh - 60px);
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
}

.menu-toggle {
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
  color: white;
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

.main {
  background-color: #f5f5f5;
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
    top: 0;
    z-index: 1000;
    height: 100vh;
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
}
</style>
