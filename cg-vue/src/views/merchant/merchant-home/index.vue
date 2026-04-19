<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElCard, ElStatistic, ElRow, ElCol, ElTable, ElTableColumn, ElButton, ElTag, ElEmpty, ElMessage } from 'element-plus'
import { ShoppingCart, Clock, Money, TrendCharts, Plus, Edit, Document, Check, Star } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import {
  getMerchantDashboard,
  getRecentOrders,
  getRecentReviews,
  type DashboardStats,
  type RecentOrder,
  type RecentReview
} from '@/api/merchant'

const router = useRouter()

const loading = ref(false)
const dashboardStats = ref<DashboardStats | null>(null)
const recentOrders = ref<RecentOrder[]>([])
const recentReviews = ref<RecentReview[]>([])

const statusMap: Record<string, { label: string; type: string }> = {
  pending: { label: '待处理', type: 'warning' },
  confirmed: { label: '已确认', type: 'primary' },
  completed: { label: '已完成', type: 'success' },
  cancelled: { label: '已取消', type: 'info' }
}

const getStatusType = (status: string) => statusMap[status]?.type || 'info'
const getStatusLabel = (status: string) => statusMap[status]?.label || status

const formatDateTime = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}

const fetchDashboardData = async () => {
  loading.value = true
  try {
    const [dashboardRes, ordersRes, reviewsRes] = await Promise.all([
      getMerchantDashboard(),
      getRecentOrders(5),
      getRecentReviews(5)
    ])

    dashboardStats.value = dashboardRes.data
    recentOrders.value = ordersRes.data || []
    recentReviews.value = reviewsRes.data || []
  } catch (error) {
    console.error('获取首页数据失败:', error)
    ElMessage.error('获取首页数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleAction = (action: string) => {
  const routes: Record<string, string> = {
    'add-service': '/merchant/services/add',
    'add-product': '/merchant/products/add',
    'view-appointments': '/merchant/appointments',
    'view-orders': '/merchant/orders'
  }
  if (routes[action]) {
    router.push(routes[action])
  }
}

const viewAppointmentDetail = (row: RecentOrder) => {
  router.push(`/merchant/appointments?search=${row.id}`)
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<template>
  <div class="merchant-home" v-loading="loading">
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="今日订单" :value="dashboardStats?.todayOrders || 0">
            <template #prefix>
              <el-icon class="stat-icon order-icon"><ShoppingCart /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="待处理预约" :value="dashboardStats?.pendingAppointments || 0">
            <template #prefix>
              <el-icon class="stat-icon pending-icon"><Clock /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="今日收入" :value="dashboardStats?.todayRevenue || 0" :precision="2">
            <template #prefix>
              <el-icon class="stat-icon revenue-icon"><Money /></el-icon>
            </template>
            <template #suffix>元</template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="平均评分" :value="dashboardStats?.avgRating || 0">
            <template #prefix>
              <el-icon class="stat-icon rating-icon"><Star /></el-icon>
            </template>
            <template #suffix>分</template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="14">
        <el-card shadow="hover" class="table-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">最近订单</span>
              <el-button type="primary" link @click="handleAction('view-appointments')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" style="width: 100%" :show-header="true">
            <el-table-column prop="id" label="订单号" width="80" />
            <el-table-column prop="customerName" label="用户名称" width="100" />
            <el-table-column prop="serviceName" label="服务类型" width="120" />
            <el-table-column prop="totalPrice" label="金额" width="90">
              <template #default="{ row }">
                {{ formatPrice(row.totalPrice) }}
              </template>
            </el-table-column>
            <el-table-column prop="appointmentTime" label="预约时间" width="140">
              <template #default="{ row }">
                {{ formatDateTime(row.appointmentTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" link @click="viewAppointmentDetail(row)">
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="recentOrders.length === 0" description="暂无订单数据" :image-size="60" />
        </el-card>

        <el-card shadow="hover" class="table-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span class="card-title">最新评价</span>
              <el-button type="primary" link @click="router.push('/merchant/reviews')">查看全部</el-button>
            </div>
          </template>
          <div class="review-list">
            <div v-for="review in recentReviews" :key="review.id" class="review-item">
              <div class="review-header">
                <span class="review-user">{{ review.userName }}</span>
                <el-rate v-model="review.rating" disabled text-color="#ff9900" />
              </div>
              <div class="review-content">{{ review.content }}</div>
              <div class="review-footer">
                <span class="review-service">{{ review.serviceName }}</span>
                <span class="review-time">{{ formatDateTime(review.reviewTime) }}</span>
              </div>
            </div>
            <el-empty v-if="recentReviews.length === 0" description="暂无评价数据" :image-size="60" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="hover" class="quick-actions-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="action-item" @click="handleAction('add-service')">
              <div class="action-icon add-icon">
                <el-icon><Plus /></el-icon>
              </div>
              <span class="action-text">添加服务</span>
            </div>
            <div class="action-item" @click="handleAction('add-product')">
              <div class="action-icon edit-icon">
                <el-icon><Edit /></el-icon>
              </div>
              <span class="action-text">添加商品</span>
            </div>
            <div class="action-item" @click="handleAction('view-appointments')">
              <div class="action-icon order-icon">
                <el-icon><Document /></el-icon>
              </div>
              <span class="action-text">查看预约</span>
            </div>
            <div class="action-item" @click="handleAction('view-orders')">
              <div class="action-icon process-icon">
                <el-icon><Check /></el-icon>
              </div>
              <span class="action-text">处理订单</span>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="chart-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span class="card-title">数据趋势</span>
              <el-icon class="chart-icon"><TrendCharts /></el-icon>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-empty description="数据图表展示区域" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.merchant-home {
  padding: 0;
  font-family: Arial, sans-serif;
}

.statistics-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  transition: transform 0.3s, box-shadow 0.3s;
  border: 1px solid #e0e0e0;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 28px;
  margin-right: 8px;
  color: #4CAF50;
}

.order-icon {
  color: #4CAF50;
}

.pending-icon {
  color: #FF9800;
}

.revenue-icon {
  color: #4CAF50;
}

.content-row {
  margin-bottom: 20px;
}

.table-card {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 8px;
  border-bottom: 2px solid #4CAF50;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333333;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.review-user {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.review-content {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 8px;
}

.review-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.review-service {
  color: #4CAF50;
}

.rating-icon {
  color: #FF9800;
}

.quick-actions-card {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 8px 0;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 16px;
  background: #f5f5f5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e0e0e0;
}

.action-item:hover {
  background: #e8f5e8;
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  background-color: #4CAF50;
}

.add-icon {
  background-color: #4CAF50;
}

.edit-icon {
  background-color: #FF9800;
}

.order-icon {
  background-color: #2196F3;
}

.process-icon {
  background-color: #4CAF50;
}

.action-text {
  font-size: 14px;
  color: #333333;
  font-weight: 500;
}

.chart-card {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.chart-placeholder {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-icon {
  font-size: 20px;
  color: #4CAF50;
}

:deep(.el-card) {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-button--primary) {
  color: #4CAF50;
  border-color: #4CAF50;
}

:deep(.el-button--primary:hover) {
  color: #45a049;
  border-color: #45a049;
}

:deep(.el-table) {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

:deep(.el-table th) {
  background-color: #f5f5f5;
  color: #333333;
  font-weight: 600;
}

:deep(.el-table tr:hover) {
  background-color: #f5f5f5;
}

:deep(.el-tag--danger) {
  background-color: #f44336;
  border-color: #f44336;
}

:deep(.el-tag--warning) {
  background-color: #FF9800;
  border-color: #FF9800;
}

:deep(.el-tag--info) {
  background-color: #2196F3;
  border-color: #2196F3;
}
</style>
