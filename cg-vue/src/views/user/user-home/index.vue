<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Calendar, Goods, ArrowRight, StarFilled, Message, List } from '@element-plus/icons-vue'

const router = useRouter()

const currentDate = computed(() => {
  const now = new Date()
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  }
  return now.toLocaleDateString('zh-CN', options)
})

const stats = ref([
  { title: '我的宠物', value: '3', icon: Goods, color: '#409eff', route: '/user/pets' },
  { title: '待处理预约', value: '2', icon: Calendar, color: '#e6a23c', route: '/user/appointments' },
  { title: '服务评价', value: '5', icon: StarFilled, color: '#67c23a', route: '/user/reviews' }
])

const recentActivities = ref([
  {
    id: 1,
    type: 'appointment',
    title: '预约宠物洗澡美容',
    time: '2024-01-20 14:00',
    status: '已确认',
    statusColor: 'success'
  },
  {
    id: 2,
    type: 'review',
    title: '评价宠物寄养服务',
    time: '2024-01-19 10:30',
    status: '已完成',
    statusColor: 'info'
  },
  {
    id: 3,
    type: 'appointment',
    title: '预约宠物体检',
    time: '2024-01-18 09:00',
    status: '待处理',
    statusColor: 'warning'
  }
])

const quickActions = [
  { title: '我的宠物', icon: Goods, color: '#409eff', route: '/user/pets' },
  { title: '我的预约', icon: Calendar, color: '#67c23a', route: '/user/appointments' },
  { title: '浏览服务', icon: Location, color: '#e6a23c', route: '/user/services' },
  { title: '我的订单', icon: List, color: '#f56c6c', route: '/user/orders' }
]

const recommendedServices = ref([
  { id: 1, name: '宠物洗澡美容', price: '88', score: 4.8, image: '🛁' },
  { id: 2, name: '宠物体检套餐', price: '199', score: 4.9, image: '🏥' },
  { id: 3, name: '宠物疫苗接种', price: '120', score: 4.7, image: '💉' },
  { id: 4, name: '宠物寄养服务', price: '150', score: 4.6, image: '🏠' }
])

const goToQuickAction = (route: string) => {
  router.push(route)
}
</script>

<template>
  <div class="user-home">
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-header">
          <h1 class="welcome-title">您好，欢迎来到宠物服务平台！</h1>
          <span class="current-date">{{ currentDate }}</span>
        </div>
        <p class="welcome-subtitle">为您提供专业、贴心的宠物服务</p>
      </div>
    </el-card>

    <div class="section">
      <h2 class="section-title">统计概览</h2>
      <el-row :gutter="16">
        <el-col v-for="stat in stats" :key="stat.title" :span="8">
          <el-card class="stat-card" :style="{ '--card-color': stat.color }">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon :size="32"><component :is="stat.icon" /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ stat.value }}</span>
                <span class="stat-title">{{ stat.title }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="section">
      <h2 class="section-title">最近活动</h2>
      <el-card class="activities-card">
        <el-timeline>
          <el-timeline-item
            v-for="activity in recentActivities"
            :key="activity.id"
            :timestamp="activity.time"
            :type="activity.statusColor"
            :icon="activity.type === 'appointment' ? Calendar : StarFilled"
          >
            <div class="activity-content">
              <span class="activity-title">{{ activity.title }}</span>
              <el-tag :type="activity.statusColor" size="small">{{ activity.status }}</el-tag>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

    <div class="section">
      <h2 class="section-title">快捷操作</h2>
      <el-row :gutter="16" class="quick-actions">
        <el-col v-for="action in quickActions" :key="action.title" :span="6">
          <el-card
            class="action-card"
            :style="{ '--card-color': action.color }"
            @click="goToQuickAction(action.route)"
          >
            <div class="action-content">
              <div class="action-icon">
                <el-icon :size="32"><component :is="action.icon" /></el-icon>
              </div>
              <div class="action-text">
                <span class="action-title">{{ action.title }}</span>
                <el-icon class="action-arrow"><ArrowRight /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="section">
      <h2 class="section-title">推荐服务</h2>
      <el-row :gutter="16">
        <el-col v-for="service in recommendedServices" :key="service.id" :span="6">
          <el-card class="service-card" shadow="hover">
            <div class="service-image">{{ service.image }}</div>
            <div class="service-info">
              <h3 class="service-name">{{ service.name }}</h3>
              <div class="service-meta">
                <span class="service-score">
                  <el-icon color="#f5a623"><StarFilled /></el-icon>
                  {{ service.score }}
                </span>
                <span class="service-price">¥{{ service.price }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>
.user-home {
  max-width: 1200px;
  margin: 0 auto;
  font-family: Arial, sans-serif;
}

.welcome-card {
  background: #4CAF50;
  color: #fff;
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.welcome-card :deep(.el-card__body) {
  padding: 32px;
}

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.welcome-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.current-date {
  font-size: 14px;
  opacity: 0.9;
  background: rgba(255, 255, 255, 0.2);
  padding: 6px 12px;
  border-radius: 16px;
}

.welcome-subtitle {
  font-size: 14px;
  margin: 0;
  opacity: 0.9;
}

.stat-card {
  transition: transform 0.2s, box-shadow 0.2s;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background-color: #4CAF50;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #333333;
  margin-bottom: 4px;
}

.stat-title {
  font-size: 14px;
  color: #666666;
}

.activities-card {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.activity-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.activity-title {
  font-size: 14px;
  color: #333333;
  font-weight: 500;
}

:deep(.el-timeline-item__timestamp) {
  font-size: 12px;
  color: #999999;
}

.section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333333;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #4CAF50;
  display: inline-block;
}

.quick-actions {
  margin-bottom: 8px;
}

.action-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.action-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background-color: #4CAF50;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.action-text {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.action-title {
  font-size: 16px;
  font-weight: 500;
  color: #333333;
}

.action-arrow {
  color: #4CAF50;
}

.service-card {
  transition: transform 0.2s, box-shadow 0.2s;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.service-image {
  font-size: 48px;
  text-align: center;
  padding: 16px 0;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 12px;
}

.service-info {
  padding: 0 4px;
}

.service-name {
  font-size: 14px;
  font-weight: 500;
  color: #333333;
  margin: 0 0 8px 0;
  text-align: center;
}

.service-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.service-score {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666666;
}

.service-price {
  font-size: 16px;
  font-weight: bold;
  color: #4CAF50;
}

:deep(.el-card) {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>
