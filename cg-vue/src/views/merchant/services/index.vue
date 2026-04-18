<script setup lang="ts">
import { ref } from 'vue'

// 模拟服务数据
const services = [
  {
    id: 'SVC-001',
    name: '宠物美容',
    price: '¥120',
    duration: '60分钟',
    description: '专业宠物美容服务，包括洗澡、剪毛、修剪指甲等',
    status: '启用',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20pet%20grooming%2C%20clean%20and%20friendly%20environment&image_size=landscape_4_3'
  },
  {
    id: 'SVC-002',
    name: '宠物寄养',
    price: '¥80/天',
    duration: '全天',
    description: '提供舒适的宠物寄养环境，每天遛狗两次，定期喂食',
    status: '启用',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20boarding%20facility%2C%20clean%20cages%2C%20play%20area&image_size=landscape_4_3'
  },
  {
    id: 'SVC-003',
    name: '宠物医疗',
    price: '¥200起',
    duration: '30分钟',
    description: '专业宠物医疗服务，包括体检、疫苗接种、疾病治疗等',
    status: '启用',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=veterinary%20clinic%2C%20professional%20equipment%2C%20clean%20environment&image_size=landscape_4_3'
  },
  {
    id: 'SVC-004',
    name: '宠物训练',
    price: '¥180/课时',
    duration: '45分钟',
    description: '专业宠物训练服务，包括基本 obedience训练、行为纠正等',
    status: '启用',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=dog%20training%20session%2C%20trainer%20working%20with%20dog&image_size=landscape_4_3'
  },
  {
    id: 'SVC-005',
    name: '宠物用品销售',
    price: '¥50起',
    duration: '不限',
    description: '销售各种宠物用品，包括食品、玩具、窝具等',
    status: '启用',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20supply%20store%2C%20various%20products%2C%20organized%20shelves&image_size=landscape_4_3'
  }
]

const searchQuery = ref('')
const statusFilter = ref('')

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: '启用', label: '启用' },
  { value: '禁用', label: '禁用' }
]

const filteredServices = computed(() => {
  return services.filter(service => {
    const matchesSearch = service.name.includes(searchQuery.value) || 
                         service.description.includes(searchQuery.value)
    const matchesStatus = statusFilter.value ? service.status === statusFilter.value : true
    return matchesSearch && matchesStatus
  })
})

// 导入computed
import { computed } from 'vue'
</script>

<template>
  <div class="merchant-services">
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-2xl font-bold text-gray-800">服务管理</h2>
      <button class="bg-primary text-white px-4 py-2 rounded-lg hover:bg-opacity-90 transition flex items-center gap-2">
        <i class="fa fa-plus"></i>
        添加服务
      </button>
    </div>
    
    <!-- 搜索和筛选 -->
    <div class="flex flex-col md:flex-row gap-4 mb-6">
      <div class="flex-1">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索服务名称或描述"
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
    
    <!-- 服务列表 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="service in filteredServices" :key="service.id" class="bg-white rounded-lg shadow-md overflow-hidden transition hover:shadow-lg">
        <img :src="service.image" alt="服务图片" class="w-full h-48 object-cover">
        <div class="p-6">
          <div class="flex items-center justify-between mb-2">
            <h3 class="text-lg font-semibold text-gray-800">{{ service.name }}</h3>
            <span 
              class="px-2 py-1 rounded text-xs font-medium"
              :class="service.status === '启用' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
            >
              {{ service.status }}
            </span>
          </div>
          <div class="mb-4">
            <div class="flex items-center mb-1">
              <i class="fa fa-money text-gray-500 mr-2"></i>
              <span class="text-gray-700">{{ service.price }}</span>
            </div>
            <div class="flex items-center mb-1">
              <i class="fa fa-clock-o text-gray-500 mr-2"></i>
              <span class="text-gray-700">{{ service.duration }}</span>
            </div>
          </div>
          <p class="text-gray-600 text-sm mb-4">{{ service.description }}</p>
          <div class="flex gap-2">
            <button class="flex-1 bg-primary text-white px-3 py-2 rounded-lg hover:bg-opacity-90 transition text-sm">
              编辑
            </button>
            <button class="flex-1 bg-gray-200 text-gray-800 px-3 py-2 rounded-lg hover:bg-gray-300 transition text-sm">
              禁用
            </button>
            <button class="bg-red-500 text-white px-3 py-2 rounded-lg hover:bg-red-600 transition text-sm">
              删除
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="mt-8 flex justify-center">
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
        <button class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
          <span class="sr-only">下一页</span>
          <i class="fa fa-chevron-right"></i>
        </button>
      </nav>
    </div>
  </div>
</template>

<style scoped>
.merchant-services {
  font-family: Arial, sans-serif;
}

.flex {
  display: flex;
}

.items-center {
  align-items: center;
}

.justify-between {
  justify-content: space-between;
}

.mb-6 {
  margin-bottom: 1.5rem;
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

.bg-primary {
  background-color: #4CAF50;
}

.text-white {
  color: #ffffff;
}

.px-4 {
  padding-left: 1rem;
  padding-right: 1rem;
}

.py-2 {
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
}

.rounded-lg {
  border-radius: 0.5rem;
}

.hover\:bg-opacity-90:hover {
  background-color: rgba(76, 175, 80, 0.9);
}

.transition {
  transition: all 0.3s ease;
}

.gap-2 {
  gap: 0.5rem;
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

.flex-1 {
  flex: 1;
}

.w-full {
  width: 100%;
}

.md\:w-48 {
  width: 12rem;
}

.border {
  border: 1px solid #e5e7eb;
}

.border-gray-300 {
  border-color: #d1d5db;
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

.grid {
  display: grid;
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
  .lg\:grid-cols-3 {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

.gap-6 {
  gap: 1.5rem;
}

.shadow-md {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.overflow-hidden {
  overflow: hidden;
}

.hover\:shadow-lg:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.object-cover {
  object-fit: cover;
}

.p-6 {
  padding: 1.5rem;
}

.text-lg {
  font-size: 1.125rem;
  line-height: 1.75rem;
}

.font-semibold {
  font-weight: 600;
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

.font-medium {
  font-weight: 500;
}

.bg-green-100 {
  background-color: #d1fae5;
}

.text-green-800 {
  color: #065f46;
}

.bg-red-100 {
  background-color: #fee2e2;
}

.text-red-800 {
  color: #991b1b;
}

.mb-4 {
  margin-bottom: 1rem;
}

.mb-1 {
  margin-bottom: 0.25rem;
}

.text-gray-500 {
  color: #6b7280;
}

.mr-2 {
  margin-right: 0.5rem;
}

.text-gray-700 {
  color: #374151;
}

.text-gray-600 {
  color: #4b5563;
}

.text-sm {
  font-size: 0.875rem;
  line-height: 1.25rem;
}

.flex-1 {
  flex: 1;
}

.px-3 {
  padding-left: 0.75rem;
  padding-right: 0.75rem;
}

.py-2 {
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
}

.bg-gray-200 {
  background-color: #e5e7eb;
}

.text-gray-800 {
  color: #1f2937;
}

.hover\:bg-gray-300:hover {
  background-color: #d1d5db;
}

.bg-red-500 {
  background-color: #ef4444;
}

.hover\:bg-red-600:hover {
  background-color: #dc2626;
}

.mt-8 {
  margin-top: 2rem;
}

.justify-center {
  justify-content: center;
}

.relative {
  position: relative;
}

.inline-flex {
  display: inline-flex;
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