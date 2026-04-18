<script setup lang="ts">
import { ref } from 'vue'

// 模拟订单数据
const orders = [
  {
    id: 'ORD-2024-001',
    customer: '张先生',
    phone: '138****1234',
    service: '宠物美容',
    date: '2024-01-15',
    time: '10:00',
    status: '已完成',
    amount: '¥120',
    notes: '需要修剪指甲'
  },
  {
    id: 'ORD-2024-002',
    customer: '李女士',
    phone: '139****5678',
    service: '宠物寄养',
    date: '2024-01-14',
    time: '09:30',
    status: '进行中',
    amount: '¥200',
    notes: '需要每天遛狗两次'
  },
  {
    id: 'ORD-2024-003',
    customer: '王女士',
    phone: '137****9012',
    service: '宠物医疗',
    date: '2024-01-13',
    time: '14:00',
    status: '已完成',
    amount: '¥350',
    notes: '宠物有点咳嗽'
  },
  {
    id: 'ORD-2024-004',
    customer: '赵先生',
    phone: '136****3456',
    service: '宠物训练',
    date: '2024-01-12',
    time: '16:00',
    status: '待处理',
    amount: '¥180',
    notes: '宠物过于活泼'
  },
  {
    id: 'ORD-2024-005',
    customer: '钱女士',
    phone: '135****7890',
    service: '宠物美容',
    date: '2024-01-11',
    time: '11:00',
    status: '已完成',
    amount: '¥150',
    notes: '需要洗澡和剪毛'
  }
]

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: '待处理', label: '待处理' },
  { value: '进行中', label: '进行中' },
  { value: '已完成', label: '已完成' },
  { value: '已取消', label: '已取消' }
]

const searchQuery = ref('')
const statusFilter = ref('')

const filteredOrders = computed(() => {
  return orders.filter(order => {
    const matchesSearch = order.id.includes(searchQuery.value) || 
                         order.customer.includes(searchQuery.value) ||
                         order.service.includes(searchQuery.value)
    const matchesStatus = statusFilter.value ? order.status === statusFilter.value : true
    return matchesSearch && matchesStatus
  })
})

// 导入computed
import { computed } from 'vue'
</script>

<template>
  <div class="merchant-orders">
    <h2 class="text-2xl font-bold text-gray-800 mb-6">预约订单列表</h2>
    
    <!-- 搜索和筛选 -->
    <div class="flex flex-col md:flex-row gap-4 mb-6">
      <div class="flex-1">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索订单号、客户或服务"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
        />
      </div>
      <div class="w-full md:w-48">
        <select
          v-model="statusFilter"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
        >
          <option v-for="option in statusOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
      </div>
    </div>
    
    <!-- 订单表格 -->
    <div class="bg-white rounded-lg shadow-md overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full">
          <thead>
            <tr class="bg-gray-50 border-b border-gray-200">
              <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">订单号</th>
              <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">客户信息</th>
              <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">服务类型</th>
              <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">预约时间</th>
              <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">状态</th>
              <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">金额</th>
              <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">备注</th>
              <th class="py-3 px-4 text-left text-sm font-medium text-gray-600">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in filteredOrders" :key="order.id" class="border-b border-gray-100 hover:bg-gray-50 transition">
              <td class="py-3 px-4 text-sm text-gray-700">{{ order.id }}</td>
              <td class="py-3 px-4 text-sm text-gray-700">
                <div>{{ order.customer }}</div>
                <div class="text-gray-500 text-xs">{{ order.phone }}</div>
              </td>
              <td class="py-3 px-4 text-sm text-gray-700">{{ order.service }}</td>
              <td class="py-3 px-4 text-sm text-gray-700">
                <div>{{ order.date }}</div>
                <div class="text-gray-500 text-xs">{{ order.time }}</div>
              </td>
              <td class="py-3 px-4">
                <span 
                  class="px-2 py-1 rounded text-xs font-medium"
                  :class="{
                    'bg-green-100 text-green-800': order.status === '已完成',
                    'bg-blue-100 text-blue-800': order.status === '进行中',
                    'bg-yellow-100 text-yellow-800': order.status === '待处理',
                    'bg-red-100 text-red-800': order.status === '已取消'
                  }"
                >
                  {{ order.status }}
                </span>
              </td>
              <td class="py-3 px-4 text-sm font-medium text-gray-900">{{ order.amount }}</td>
              <td class="py-3 px-4 text-sm text-gray-700">{{ order.notes }}</td>
              <td class="py-3 px-4 text-sm">
                <div class="flex gap-2">
                  <button class="text-primary hover:underline">查看</button>
                  <button class="text-blue-600 hover:underline">编辑</button>
                  <button class="text-red-600 hover:underline">取消</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- 分页 -->
      <div class="px-4 py-3 flex items-center justify-between border-t border-gray-200 bg-gray-50">
        <div class="flex-1 flex justify-between sm:hidden">
          <button class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            上一页
          </button>
          <button class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            下一页
          </button>
        </div>
        <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <p class="text-sm text-gray-700">
              显示 <span class="font-medium">{{ filteredOrders.length }}</span> 条记录
            </p>
          </div>
          <div>
            <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
              <button class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                <span class="sr-only">上一页</span>
                <i class="fa fa-chevron-left"></i>
              </button>
              <button class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-primary text-sm font-medium text-white">
                1
              </button>
              <button class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                2
              </button>
              <button class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                3
              </button>
              <button class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                <span class="sr-only">下一页</span>
                <i class="fa fa-chevron-right"></i>
              </button>
            </nav>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.merchant-orders {
  font-family: Arial, sans-serif;
}

.flex {
  display: flex;
}

.flex-col {
  flex-direction: column;
}

.md\:flex-row {
  flex-direction: row;
}

.gap-4 {
  gap: 1rem;
}

.mb-6 {
  margin-bottom: 1.5rem;
}

.flex-1 {
  flex: 1;
}

.w-full {
  width: 100%;
}

.md\:w-48 {
  width: 12rem;
}

.px-4 {
  padding-left: 1rem;
  padding-right: 1rem;
}

.py-2 {
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
}

.border {
  border: 1px solid #e5e7eb;
}

.border-gray-300 {
  border-color: #d1d5db;
}

.rounded-lg {
  border-radius: 0.5rem;
}

.focus\:outline-none:focus {
  outline: 2px solid transparent;
  outline-offset: 2px;
}

.focus\:ring-2:focus {
  ring-width: 2px;
}

.focus\:ring-primary:focus {
  ring-color: #4CAF50;
}

.overflow-hidden {
  overflow: hidden;
}

.overflow-x-auto {
  overflow-x: auto;
}

.min-w-full {
  min-width: 100%;
}

.bg-gray-50 {
  background-color: #f9fafb;
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

.text-gray-700 {
  color: #374151;
}

.text-gray-500 {
  color: #6b7280;
}

.text-xs {
  font-size: 0.75rem;
  line-height: 1rem;
}

.hover\:bg-gray-50:hover {
  background-color: #f9fafb;
}

.transition {
  transition: all 0.3s ease;
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

.bg-red-100 {
  background-color: #fee2e2;
}

.text-red-800 {
  color: #991b1b;
}

.text-gray-900 {
  color: #111827;
}

.gap-2 {
  gap: 0.5rem;
}

.text-primary {
  color: #4CAF50;
}

.hover\:underline:hover {
  text-decoration: underline;
}

.text-blue-600 {
  color: #2563eb;
}

.text-red-600 {
  color: #dc2626;
}

.border-t {
  border-top: 1px solid #e5e7eb;
}

.justify-between {
  justify-content: space-between;
}

.items-center {
  align-items: center;
}

.sm\:hidden {
  display: none;
}

.sm\:flex {
  display: flex;
}

.sm\:justify-between {
  justify-content: space-between;
}

.relative {
  position: relative;
}

.inline-flex {
  display: inline-flex;
}

.border-gray-300 {
  border-color: #d1d5db;
}

.bg-white {
  background-color: #ffffff;
}

.rounded-l-md {
  border-top-left-radius: 0.375rem;
  border-bottom-left-radius: 0.375rem;
}

.rounded-r-md {
  border-top-right-radius: 0.375rem;
  border-bottom-right-radius: 0.375rem;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

.font-medium {
  font-weight: 500;
}

.bg-primary {
  background-color: #4CAF50;
}

.text-white {
  color: #ffffff;
}

.shadow-sm {
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.-space-x-px > * + * {
  margin-left: -1px;
}

.aria-label {
  aria-label: Pagination;
}
</style>