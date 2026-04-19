<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useAsync } from '@/composables'
import {
  getMerchantDashboard,
  getRecentOrders,
  getRecentReviews,
  type DashboardStats,
  type RecentOrder,
  type RecentReview
} from '@/api/merchant'
import MerchantCard from '@/components/MerchantCard.vue'
import ReviewCard from '@/components/ReviewCard.vue'

// 统计数据
const {
  data: dashboardData,
  loading: statsLoading,
  error: statsError,
  execute: fetchDashboard
} = useAsync<DashboardStats>(getMerchantDashboard)

// 最近订单
const {
  data: ordersData,
  loading: ordersLoading,
  error: ordersError,
  execute: fetchOrders
} = useAsync<RecentOrder[]>(() => getRecentOrders(5))

// 最近评价
const {
  data: reviewsData,
  loading: reviewsLoading,
  error: reviewsError,
  execute: fetchReviews
} = useAsync<RecentReview[]>(() => getRecentReviews(5))

// 计算统计卡片数据
const stats = computed(() => {
  if (!dashboardData.value) return []
  
  const data = dashboardData.value
  return [
    {
      title: '今日销售额',
      value: `¥${data.todayRevenue.toLocaleString()}`,
      subtitle: `较上月增长 ${data.revenueGrowth}%`,
      icon: 'fa fa-line-chart',
      iconColor: 'primary'
    },
    {
      title: '今日订单数',
      value: data.todayOrders.toString(),
      subtitle: `较上月增长 ${data.orderGrowth}%`,
      icon: 'fa fa-shopping-cart',
      iconColor: 'secondary'
    },
    {
      title: '待处理预约',
      value: data.pendingAppointments.toString(),
      subtitle: '待确认处理',
      icon: 'fa fa-clock-o',
      iconColor: 'orange-500'
    },
    {
      title: '平均评分',
      value: data.avgRating.toFixed(1),
      subtitle: `基于 ${data.ratingCount} 条评价`,
      icon: 'fa fa-star',
      iconColor: 'blue-500'
    }
  ]
})

// 订单状态映射
const orderStatusMap: Record<string, { text: string; class: string }> = {
  pending: { text: '待处理', class: 'bg-yellow-100 text-yellow-800' },
  confirmed: { text: '已确认', class: 'bg-blue-100 text-blue-800' },
  completed: { text: '已完成', class: 'bg-green-100 text-green-800' },
  cancelled: { text: '已取消', class: 'bg-gray-100 text-gray-800' }
}

// 获取订单状态显示
const getOrderStatus = (status: string) => {
  return orderStatusMap[status] || { text: status, class: 'bg-gray-100 text-gray-800' }
}

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 格式化金额
const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}

// 加载所有数据
const loadAllData = async () => {
  await Promise.all([
    fetchDashboard(),
    fetchOrders(),
    fetchReviews()
  ])
}

// 重试加载统计数据
const retryStats = () => {
  fetchDashboard()
}

// 重试加载订单数据
const retryOrders = () => {
  fetchOrders()
}

// 重试加载评价数据
const retryReviews = () => {
  fetchReviews()
}

// 显示错误提示
const showError = (message: string) => {
  ElMessage.error(message)
}

// 监听错误并显示提示
onMounted(() => {
  loadAllData()
})
</script>

<template>
  <div class="merchant-dashboard">
    <h2 class="text-2xl font-bold text-gray-800 mb-6">商家后台首页</h2>
    
    <!-- 数据统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <!-- 加载状态 -->
      <template v-if="statsLoading">
        <div v-for="i in 4" :key="i" class="bg-white rounded-lg shadow-md p-6">
          <el-skeleton :rows="2" animated />
        </div>
      </template>
      
      <!-- 错误状态 -->
      <div v-else-if="statsError" class="col-span-full bg-white rounded-lg shadow-md p-6">
        <div class="text-center text-gray-500">
          <i class="fa fa-exclamation-circle text-4xl mb-2"></i>
          <p class="mb-2">加载统计数据失败</p>
          <el-button type="primary" size="small" @click="retryStats">重试</el-button>
        </div>
      </div>
      
      <!-- 正常显示 -->
      <MerchantCard
        v-else
        v-for="(stat, index) in stats"
        :key="index"
        :title="stat.title"
        :value="stat.value"
        :subtitle="stat.subtitle"
        :icon="stat.icon"
        :icon-color="stat.iconColor"
      />
    </div>
    
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 最近订单 -->
      <div class="bg-white rounded-lg shadow-md p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-700">最近订单</h3>
          <router-link to="/merchant/appointments" class="text-primary hover:underline">查看全部</router-link>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="ordersLoading" class="space-y-3">
          <el-skeleton v-for="i in 3" :key="i" :rows="1" animated />
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="ordersError" class="text-center py-8">
          <i class="fa fa-exclamation-circle text-4xl text-gray-400 mb-2"></i>
          <p class="text-gray-500 mb-2">加载订单失败</p>
          <el-button type="primary" size="small" @click="retryOrders">重试</el-button>
        </div>
        
        <!-- 空状态 -->
        <el-empty v-else-if="!ordersData || ordersData.length === 0" description="暂无订单数据" />
        
        <!-- 正常显示 -->
        <div v-else class="overflow-x-auto">
          <table class="min-w-full">
            <thead>
              <tr class="border-b border-gray-200">
                <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">订单号</th>
                <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">客户</th>
                <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">服务</th>
                <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">状态</th>
                <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">日期</th>
                <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">金额</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in ordersData" :key="order.id" class="border-b border-gray-100 hover:bg-gray-50 transition">
                <td class="py-3 px-4 text-sm text-gray-700">{{ order.id }}</td>
                <td class="py-3 px-4 text-sm text-gray-700">{{ order.customerName }}</td>
                <td class="py-3 px-4 text-sm text-gray-700">{{ order.serviceName }}</td>
                <td class="py-3 px-4">
                  <span 
                    class="px-2 py-1 rounded text-xs font-medium"
                    :class="getOrderStatus(order.status).class"
                  >
                    {{ getOrderStatus(order.status).text }}
                  </span>
                </td>
                <td class="py-3 px-4 text-sm text-gray-700">{{ formatDate(order.appointmentTime) }}</td>
                <td class="py-3 px-4 text-sm font-medium text-gray-900">{{ formatPrice(order.totalPrice) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      
      <!-- 最近评价 -->
      <div class="bg-white rounded-lg shadow-md p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-700">最近评价</h3>
          <router-link to="/merchant/reviews" class="text-primary hover:underline">查看全部</router-link>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="reviewsLoading" class="space-y-3">
          <el-skeleton v-for="i in 3" :key="i" :rows="2" animated />
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="reviewsError" class="text-center py-8">
          <i class="fa fa-exclamation-circle text-4xl text-gray-400 mb-2"></i>
          <p class="text-gray-500 mb-2">加载评价失败</p>
          <el-button type="primary" size="small" @click="retryReviews">重试</el-button>
        </div>
        
        <!-- 空状态 -->
        <el-empty v-else-if="!reviewsData || reviewsData.length === 0" description="暂无评价数据" />
        
        <!-- 正常显示 -->
        <div v-else class="space-y-4">
          <ReviewCard
            v-for="review in reviewsData"
            :key="review.id"
            :name="review.userName"
            :avatar="review.userAvatar"
            :rating="review.rating"
            :date="formatDate(review.reviewTime)"
            :content="review.content"
            :service-type="review.serviceName"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.merchant-dashboard {
  font-family: Arial, sans-serif;
}

.grid {
  display: grid;
  gap: 1.5rem;
}

.grid-cols-1 {
  grid-template-columns: repeat(1, minmax(0, 1fr));
}

@media (min-width: 768px) {
  .md\:grid-cols-2 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (min-width: 1024px) {
  .lg\:grid-cols-2 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  
  .lg\:grid-cols-4 {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

.gap-6 {
  gap: 1.5rem;
}

.mb-6 {
  margin-bottom: 1.5rem;
}

.mb-8 {
  margin-bottom: 2rem;
}

.text-2xl {
  font-size: 1.5rem;
  line-height: 2rem;
}

.font-bold {
  font-weight: 700;
}

.text-gray-800 {
  color: #1f2937;
}

.text-lg {
  font-size: 1.125rem;
  line-height: 1.75rem;
}

.font-semibold {
  font-weight: 600;
}

.text-gray-700 {
  color: #374151;
}

.text-primary {
  color: #4CAF50;
}

.hover\:underline:hover {
  text-decoration: underline;
}

.overflow-x-auto {
  overflow-x: auto;
}

.min-w-full {
  min-width: 100%;
}

.border-b {
  border-bottom: 1px solid #e5e7eb;
}

.border-gray-200 {
  border-color: #e5e7eb;
}

.py-3 {
  padding-top: 0.75rem;
  padding-bottom: 0.75rem;
}

.px-4 {
  padding-left: 1rem;
  padding-right: 1rem;
}

.text-left {
  text-align: left;
}

.text-sm {
  font-size: 0.875rem;
  line-height: 1.25rem;
}

.font-medium {
  font-weight: 500;
}

.text-gray-600 {
  color: #4b5563;
}

.border-gray-100 {
  border-color: #f3f4f6;
}

.hover\:bg-gray-50:hover {
  background-color: #f9fafb;
}

.transition {
  transition: all 0.3s ease;
}

.text-gray-900 {
  color: #111827;
}

.px-2 {
  padding-left: 0.5rem;
  padding-right: 0.5rem;
}

.py-1 {
  padding-top: 0.25rem;
  padding-bottom: 0.25rem;
}

.rounded {
  border-radius: 0.25rem;
}

.text-xs {
  font-size: 0.75rem;
  line-height: 1rem;
}

.bg-green-100 {
  background-color: #d1fae5;
}

.text-green-800 {
  color: #065f46;
}

.bg-blue-100 {
  background-color: #dbeafe;
}

.text-blue-800 {
  color: #1e40af;
}

.bg-yellow-100 {
  background-color: #fef3c7;
}

.text-yellow-800 {
  color: #92400e;
}

.bg-gray-100 {
  background-color: #f3f4f6;
}

.text-gray-800 {
  color: #1f2937;
}

.space-y-4 > * + * {
  margin-top: 1rem;
}

.space-y-3 > * + * {
  margin-top: 0.75rem;
}

.text-center {
  text-align: center;
}

.text-gray-500 {
  color: #6b7280;
}

.text-gray-400 {
  color: #9ca3af;
}

.text-4xl {
  font-size: 2.25rem;
  line-height: 2.5rem;
}

.mb-2 {
  margin-bottom: 0.5rem;
}

.py-8 {
  padding-top: 2rem;
  padding-bottom: 2rem;
}

.col-span-full {
  grid-column: 1 / -1;
}
</style>
