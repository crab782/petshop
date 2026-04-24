<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Calendar, Goods, ArrowRight, StarFilled, Message, List, Loading } from '@element-plus/icons-vue'
import { getHomeStats, getRecentActivities, getRecommendedServices, getMerchantList, type HomeStats, type Activity, type Service, type MerchantListItem } from '@/api/user'

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

const loading = ref(false)
const stats = ref<Array<{ title: string; value: string; icon: any; color: string; route: string }>>([])

const recentActivities = ref<Activity[]>([])

const quickActions = ref<Array<{ title: string; icon: any; color: string; route: string }>>([])

const recommendedServices = ref<Service[]>([])

const merchants = ref<MerchantListItem[]>([])
const merchantsLoading = ref(false)

const goToQuickAction = (route: string) => {
  router.push(route)
}

const goToMerchant = (id: number) => {
  router.push('/user/shop/' + id)
}

const loadHomeData = async () => {
  loading.value = true
  merchantsLoading.value = true
  try {
    const statsData = await getHomeStats()
    if (statsData) {
      stats.value = [
        { title: '我的宠物', value: (statsData.petCount || 0).toString(), icon: Goods, color: '#409eff', route: '/user/pets' },
        { title: '待处理预约', value: (statsData.pendingAppointments || 0).toString(), icon: Calendar, color: '#e6a23c', route: '/user/appointments' },
        { title: '服务评价', value: (statsData.reviewCount || 0).toString(), icon: StarFilled, color: '#67c23a', route: '/user/reviews' }
      ]
    }

    const activitiesData = await getRecentActivities(5)
    if (activitiesData) {
      recentActivities.value = activitiesData
    }

    const servicesData = await getRecommendedServices(4)
    if (servicesData) {
      recommendedServices.value = servicesData
    }

    // 设置快捷操作
    quickActions.value = [
      { title: '我的宠物', icon: Goods, color: '#409eff', route: '/user/pets' },
      { title: '我的预约', icon: Calendar, color: '#67c23a', route: '/user/appointments' },
      { title: '浏览服务', icon: Location, color: '#e6a23c', route: '/user/services' },
      { title: '我的订单', icon: List, color: '#f56c6c', route: '/user/orders' }
    ]
  } catch (error) {
    console.error('加载首页数据失败:', error)
  } finally {
    loading.value = false
  }

  try {
    const merchantResult = await getMerchantList({ page: 1, pageSize: 8 })
    const merchantData = merchantResult.data || merchantResult
    if (Array.isArray(merchantData)) {
      merchants.value = merchantData
    } else if (merchantData && Array.isArray(merchantData.data)) {
      merchants.value = merchantData.data
    }
  } catch (error) {
    console.error('加载商家列表失败:', error)
  } finally {
    merchantsLoading.value = false
  }
}

onMounted(() => {
  loadHomeData()
})
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
      <div class="section-header">
        <h2 class="section-title">商店浏览</h2>
        <router-link to="/user/services/list" class="view-all-link">查看全部 <el-icon><ArrowRight /></el-icon></router-link>
      </div>
      <el-row :gutter="16" v-loading="merchantsLoading">
        <el-col v-for="merchant in merchants" :key="merchant.id" :xs="24" :sm="12" :md="6">
          <el-card class="merchant-card" shadow="hover" @click="goToMerchant(merchant.id)">
            <div class="merchant-card-content">
              <el-avatar :size="64" :src="merchant.logo" class="merchant-avatar">
                {{ merchant.name?.charAt(0) }}
              </el-avatar>
              <div class="merchant-info">
                <h3 class="merchant-name">{{ merchant.name }}</h3>
                <div class="merchant-address" v-if="merchant.address">
                  <el-icon><Location /></el-icon>
                  <span>{{ merchant.address }}</span>
                </div>
                <div class="merchant-rating">
                  <el-rate :model-value="merchant.rating || 0" disabled :size="12" />
                </div>
              </div>
              <el-badge :value="(merchant.serviceCount || 0) + '项服务'" class="merchant-service-badge" type="success" />
            </div>
          </el-card>
        </el-col>
        <el-col v-if="merchants.length === 0 && !merchantsLoading" :span="24">
          <el-empty description="暂无商家" />
        </el-col>
      </el-row>
    </div>

    <div class="section">
      <h2 class="section-title">统计概览</h2>
      <el-row :gutter="16" v-loading="loading">
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
      <el-card class="activities-card" v-loading="loading">
        <el-timeline v-if="recentActivities.length > 0">
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
        <el-empty v-else description="暂无活动记录" />
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
      <el-row :gutter="16" v-loading="loading">
        <el-col v-for="service in recommendedServices" :key="service.id" :span="6">
          <el-card class="service-card" shadow="hover">
            <div class="service-image">{{ service.image || '🐾' }}</div>
            <div class="service-info">
              <h3 class="service-name">{{ service.name }}</h3>
              <div class="service-meta">
                <span class="service-score">
                  <el-icon color="#f5a623"><StarFilled /></el-icon>
                  {{ service.rating || 0 }}
                </span>
                <span class="service-price">¥{{ service.price || 0 }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col v-if="recommendedServices.length === 0" :span="24">
          <el-empty description="暂无推荐服务" />
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
  padding: 0;
  box-sizing: border-box;
}

.welcome-card {
  background: #4CAF50;
  color: #fff;
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.welcome-card :deep(.el-card__body) {
  padding: 32px;
  margin: 0;
  box-sizing: border-box;
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
  overflow: hidden;
  min-height: 120px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  box-sizing: border-box;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background-color: var(--card-color, #4CAF50);
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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header .section-title {
  margin-bottom: 0;
}

.view-all-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #4CAF50;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.2s;
}

.view-all-link:hover {
  color: #388E3C;
}

.merchant-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  overflow: hidden;
  margin-bottom: 16px;
}

.merchant-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.merchant-card :deep(.el-card__body) {
  padding: 16px;
}

.merchant-card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  position: relative;
}

.merchant-avatar {
  background-color: #4CAF50;
  color: #fff;
  font-size: 24px;
  font-weight: bold;
  flex-shrink: 0;
}

.merchant-info {
  text-align: center;
  width: 100%;
}

.merchant-name {
  font-size: 15px;
  font-weight: bold;
  color: #333333;
  margin: 0 0 6px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.merchant-address {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 12px;
  color: #999999;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.merchant-address span {
  overflow: hidden;
  text-overflow: ellipsis;
}

.merchant-rating {
  display: flex;
  justify-content: center;
}

.merchant-service-badge {
  position: absolute;
  top: 0;
  right: 0;
}

.quick-actions {
  margin-bottom: 8px;
}

.action-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  overflow: hidden;
  min-height: 120px;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.action-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  box-sizing: border-box;
}

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background-color: var(--card-color, #4CAF50);
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
  overflow: hidden;
  min-height: 200px;
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
  border-radius: 8px 8px 0 0;
  margin: 0;
  box-sizing: border-box;
}

.service-info {
  padding: 16px;
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
  box-sizing: border-box;
}

:deep(.el-card__body) {
  padding: 20px;
  box-sizing: border-box;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .user-home {
    max-width: 100%;
    padding: 0 16px;
  }
}

@media (max-width: 768px) {
  .welcome-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .welcome-card :deep(.el-card__body) {
    padding: 20px;
  }

  .stat-content,
  .action-content {
    padding: 12px;
    gap: 12px;
  }

  .stat-icon,
  .action-icon {
    width: 48px;
    height: 48px;
  }

  .service-image {
    font-size: 36px;
    padding: 12px 0;
  }

  .merchant-avatar {
    width: 48px !important;
    height: 48px !important;
  }

  .merchant-name {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .main {
    padding: 12px;
  }

  .welcome-title {
    font-size: 20px;
  }

  .section-title {
    font-size: 16px;
  }

  .stat-value {
    font-size: 20px;
  }

  .service-name {
    font-size: 12px;
  }

  .service-price {
    font-size: 14px;
  }
}
</style>
