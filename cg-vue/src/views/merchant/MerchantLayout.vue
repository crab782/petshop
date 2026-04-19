<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { HomeFilled, ShoppingBag, Calendar, Grid, Star, SwitchButton, Bell, ArrowDown, Setting } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getMerchantInfo, type MerchantInfo } from '@/api/merchant'
import { useAsync } from '@/composables'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

// 商家信息
const merchantInfo = ref<MerchantInfo | null>(null)

// 使用 useAsync 处理 API 调用
const { loading, error, execute: fetchMerchantInfo } = useAsync(getMerchantInfo)

// 检查会话状态
const checkSession = () => {
  const token = localStorage.getItem('merchant_token') || sessionStorage.getItem('merchant_token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/merchant/login')
    return false
  }
  return true
}

// 获取商家信息
const loadMerchantInfo = async () => {
  if (!checkSession()) return
  
  const result = await fetchMerchantInfo()
  if (result) {
    merchantInfo.value = result
  } else if (error.value) {
    ElMessage.error(error.value.message || '获取商家信息失败')
  }
}

// 登出功能
const handleLogout = () => {
  // 清除登录状态
  localStorage.removeItem('merchant_token')
  sessionStorage.removeItem('merchant_token')
  localStorage.removeItem('merchant_info')
  sessionStorage.removeItem('merchant_info')
  
  ElMessage.success('已退出登录')
  router.push('/merchant/login')
}

// 菜单选择处理
const handleMenuSelect = (path: string) => {
  if (path === '/logout') {
    handleLogout()
  } else {
    router.push(path)
  }
}

// 组件挂载时获取商家信息
onMounted(() => {
  loadMerchantInfo()
})
</script>

<template>
  <el-container class="merchant-layout" v-loading="loading" element-loading-text="加载中...">
    <el-header class="header">
      <div class="header-left">
        <el-button text @click="isCollapse = !isCollapse" class="menu-toggle">
          <el-icon size="20"><ArrowDown /></el-icon>
        </el-button>
        <div class="logo-area">
          <i class="fa fa-paw text-white text-2xl mr-2"></i>
          <span class="merchant-name">{{ merchantInfo?.name || '商家后台' }}</span>
        </div>
      </div>
      <div class="header-right">
        <div class="relative mr-4">
          <el-button text class="notification-btn">
            <el-icon><Bell /></el-icon>
          </el-button>
        </div>
        <el-dropdown trigger="click">
          <div class="user-info">
            <img 
              :src="merchantInfo?.logo || 'https://via.placeholder.com/32'" 
              alt="商家头像" 
              class="user-avatar"
            >
            <span class="username">{{ merchantInfo?.name || '商家' }}</span>
            <i class="fa fa-caret-down text-white ml-1"></i>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/merchant/profile')">
                <el-icon><Setting /></el-icon>
                店铺设置
              </el-dropdown-item>
              <el-dropdown-item @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>
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
            <el-icon><Grid /></el-icon>
            <template #title>服务管理</template>
          </el-menu-item>
          <el-menu-item index="/merchant/reviews">
            <el-icon><Star /></el-icon>
            <template #title>服务评价列表</template>
          </el-menu-item>
          <el-menu-item index="/logout">
            <el-icon><SwitchButton /></el-icon>
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
  object-fit: cover;
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
