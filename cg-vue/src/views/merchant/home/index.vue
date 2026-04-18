<script setup lang="ts">
import { ref } from 'vue'
import MerchantCard from '@/components/MerchantCard.vue'
import ReviewCard from '@/components/ReviewCard.vue'

// 模拟数据
const stats = [
  {
    title: '本月销售额',
    value: '¥25,670',
    subtitle: '较上月增长 12%',
    icon: 'fa fa-line-chart',
    iconColor: 'primary'
  },
  {
    title: '新客户',
    value: '24',
    subtitle: '较上月增长 8%',
    icon: 'fa fa-user-plus',
    iconColor: 'secondary'
  },
  {
    title: '服务完成率',
    value: '96%',
    subtitle: '较上月增长 2%',
    icon: 'fa fa-check-circle',
    iconColor: 'green-500'
  },
  {
    title: '平均评分',
    value: '4.8',
    subtitle: '基于 120 条评价',
    icon: 'fa fa-star',
    iconColor: 'blue-500'
  }
]

const recentOrders = [
  {
    id: 'ORD-2024-001',
    customer: '张先生',
    service: '宠物美容',
    status: '已完成',
    date: '2024-01-15',
    amount: '¥120'
  },
  {
    id: 'ORD-2024-002',
    customer: '李女士',
    service: '宠物寄养',
    status: '进行中',
    date: '2024-01-14',
    amount: '¥200'
  },
  {
    id: 'ORD-2024-003',
    customer: '王女士',
    service: '宠物医疗',
    status: '已完成',
    date: '2024-01-13',
    amount: '¥350'
  },
  {
    id: 'ORD-2024-004',
    customer: '赵先生',
    service: '宠物训练',
    status: '待处理',
    date: '2024-01-12',
    amount: '¥180'
  }
]

const recentReviews = [
  {
    name: '张先生',
    avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=male%20customer%20avatar%2C%20friendly%20face&image_size=square',
    rating: 5,
    date: '2024-01-15',
    content: '服务非常专业，宠物美容效果很好，下次还会再来！',
    serviceType: '宠物美容'
  },
  {
    name: '李女士',
    avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20customer%20avatar%2C%20friendly%20face&image_size=square',
    rating: 4,
    date: '2024-01-14',
    content: '宠物寄养服务很贴心，环境干净整洁，工作人员也很负责。',
    serviceType: '宠物寄养'
  },
  {
    name: '王女士',
    avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20customer%20avatar%2C%20professional%20look&image_size=square',
    rating: 5,
    date: '2024-01-13',
    content: '宠物医疗服务专业可靠，医生很有经验，治疗效果很好。',
    serviceType: '宠物医疗'
  }
]
</script>

<template>
  <div class="merchant-dashboard">
    <h2 class="text-2xl font-bold text-gray-800 mb-6">商家后台首页</h2>
    
    <!-- 数据统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <MerchantCard
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
          <a href="/merchant/appointments" class="text-primary hover:underline">查看全部</a>
        </div>
        <div class="overflow-x-auto">
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
              <tr v-for="order in recentOrders" :key="order.id" class="border-b border-gray-100 hover:bg-gray-50 transition">
                <td class="py-3 px-4 text-sm text-gray-700">{{ order.id }}</td>
                <td class="py-3 px-4 text-sm text-gray-700">{{ order.customer }}</td>
                <td class="py-3 px-4 text-sm text-gray-700">{{ order.service }}</td>
                <td class="py-3 px-4">
                  <span 
                    class="px-2 py-1 rounded text-xs font-medium"
                    :class="{
                      'bg-green-100 text-green-800': order.status === '已完成',
                      'bg-blue-100 text-blue-800': order.status === '进行中',
                      'bg-yellow-100 text-yellow-800': order.status === '待处理'
                    }"
                  >
                    {{ order.status }}
                  </span>
                </td>
                <td class="py-3 px-4 text-sm text-gray-700">{{ order.date }}</td>
                <td class="py-3 px-4 text-sm font-medium text-gray-900">{{ order.amount }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      
      <!-- 最近评价 -->
      <div class="bg-white rounded-lg shadow-md p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-700">最近评价</h3>
          <a href="/merchant/reviews" class="text-primary hover:underline">查看全部</a>
        </div>
        <div class="space-y-4">
          <ReviewCard
            v-for="(review, index) in recentReviews"
            :key="index"
            :name="review.name"
            :avatar="review.avatar"
            :rating="review.rating"
            :date="review.date"
            :content="review.content"
            :service-type="review.serviceType"
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

.space-y-4 > * + * {
  margin-top: 1rem;
}
</style>