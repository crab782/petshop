<script setup lang="ts">
import { ref } from 'vue'
import ReviewCard from '@/components/ReviewCard.vue'

// 模拟评价数据
const reviews = [
  {
    id: 'REV-2024-001',
    name: '张先生',
    avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=male%20customer%20avatar%2C%20friendly%20face&image_size=square',
    rating: 5,
    date: '2024-01-15',
    content: '服务非常专业，宠物美容效果很好，下次还会再来！',
    serviceType: '宠物美容',
    response: '感谢您的好评，我们会继续努力为您的宠物提供更好的服务！'
  },
  {
    id: 'REV-2024-002',
    name: '李女士',
    avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20customer%20avatar%2C%20friendly%20face&image_size=square',
    rating: 4,
    date: '2024-01-14',
    content: '宠物寄养服务很贴心，环境干净整洁，工作人员也很负责。',
    serviceType: '宠物寄养',
    response: '谢谢反馈，我们会继续保持服务质量！'
  },
  {
    id: 'REV-2024-003',
    name: '王女士',
    avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20customer%20avatar%2C%20professional%20look&image_size=square',
    rating: 5,
    date: '2024-01-13',
    content: '宠物医疗服务专业可靠，医生很有经验，治疗效果很好。',
    serviceType: '宠物医疗',
    response: '非常感谢您的认可，我们会继续为宠物健康保驾护航！'
  },
  {
    id: 'REV-2024-004',
    name: '赵先生',
    avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=male%20customer%20avatar%2C%20casual%20look&image_size=square',
    rating: 3,
    date: '2024-01-12',
    content: '宠物训练服务还可以，希望教练能更有耐心一些。',
    serviceType: '宠物训练',
    response: '感谢您的反馈，我们会加强教练的培训，提高服务质量。'
  },
  {
    id: 'REV-2024-005',
    name: '钱女士',
    avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20customer%20avatar%2C%20smiling&image_size=square',
    rating: 5,
    date: '2024-01-11',
    content: '宠物美容服务非常满意，造型很好看，宠物也很开心。',
    serviceType: '宠物美容',
    response: '感谢您的好评，我们会继续为您的宠物打造美丽造型！'
  }
]

const ratingFilter = ref(0)
const serviceFilter = ref('')

const serviceOptions = [
  { value: '', label: '全部服务' },
  { value: '宠物美容', label: '宠物美容' },
  { value: '宠物寄养', label: '宠物寄养' },
  { value: '宠物医疗', label: '宠物医疗' },
  { value: '宠物训练', label: '宠物训练' }
]

const filteredReviews = computed(() => {
  return reviews.filter(review => {
    const matchesRating = ratingFilter.value ? review.rating >= ratingFilter.value : true
    const matchesService = serviceFilter.value ? review.serviceType === serviceFilter.value : true
    return matchesRating && matchesService
  })
})

// 导入computed
import { computed } from 'vue'
</script>

<template>
  <div class="merchant-reviews">
    <h2 class="text-2xl font-bold text-gray-800 mb-6">服务评价列表</h2>
    
    <!-- 筛选器 -->
    <div class="flex flex-col md:flex-row gap-4 mb-6">
      <div class="flex items-center gap-2">
        <span class="text-gray-700">评分筛选：</span>
        <div class="flex gap-2">
          <button 
            v-for="rating in [0, 5, 4, 3, 2, 1]" 
            :key="rating"
            @click="ratingFilter = rating"
            class="px-3 py-1 rounded-full text-sm transition"
            :class="ratingFilter === rating ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
          >
            {{ rating === 0 ? '全部' : `${rating}星及以上` }}
          </button>
        </div>
      </div>
      <div class="w-full md:w-48">
        <select
          v-model="serviceFilter"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
        >
          <option v-for="option in serviceOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
      </div>
    </div>
    
    <!-- 评价列表 -->
    <div class="space-y-6">
      <div v-for="review in filteredReviews" :key="review.id" class="bg-white rounded-lg shadow-md p-6">
        <div class="flex items-start gap-4">
          <img :src="review.avatar" alt="客户头像" class="w-16 h-16 rounded-full">
          <div class="flex-1">
            <div class="flex items-center justify-between mb-2">
              <h4 class="font-semibold text-lg text-gray-800">{{ review.name }}</h4>
              <span class="text-sm text-gray-500">{{ review.date }}</span>
            </div>
            <div class="flex items-center mb-3">
              <div class="text-yellow-400 mr-2">
                <i v-for="i in 5" :key="i" class="fa" :class="i <= review.rating ? 'fa-star' : 'fa-star-o'"></i>
              </div>
              <span class="text-gray-600 text-sm">{{ review.serviceType }}</span>
            </div>
            <p class="text-gray-700 mb-4">{{ review.content }}</p>
            <div v-if="review.response" class="bg-gray-50 p-3 rounded-lg">
              <div class="flex items-start gap-2">
                <div class="w-8 h-8 bg-primary rounded-full flex items-center justify-center flex-shrink-0">
                  <i class="fa fa-store text-white"></i>
                </div>
                <div>
                  <p class="font-medium text-gray-800 mb-1">商家回复</p>
                  <p class="text-gray-700">{{ review.response }}</p>
                </div>
              </div>
            </div>
            <div v-else class="mt-4">
              <textarea 
                placeholder="输入回复..."
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                rows="2"
              ></textarea>
              <div class="mt-2 flex justify-end">
                <button class="bg-primary text-white px-4 py-2 rounded-lg hover:bg-opacity-90 transition">
                  回复
                </button>
              </div>
            </div>
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
</template>

<style scoped>
.merchant-reviews {
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

.items-center {
  align-items: center;
}

.gap-2 {
  gap: 0.5rem;
}

.text-gray-700 {
  color: #374151;
}

.px-3 {
  padding-left: 0.75rem;
  padding-right: 0.75rem;
}

.py-1 {
  padding-top: 0.25rem;
  padding-bottom: 0.25rem;
}

.rounded-full {
  border-radius: 9999px;
}

.text-sm {
  font-size: 0.875rem;
  line-height: 1.25rem;
}

.transition {
  transition: all 0.3s ease;
}

.bg-primary {
  background-color: #4CAF50;
}

.text-white {
  color: #ffffff;
}

.bg-gray-100 {
  background-color: #f3f4f6;
}

.hover\:bg-gray-200:hover {
  background-color: #e5e7eb;
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

.space-y-6 > * + * {
  margin-top: 1.5rem;
}

.bg-white {
  background-color: #ffffff;
}

.shadow-md {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.p-6 {
  padding: 1.5rem;
}

.flex-start {
  align-items: flex-start;
}

.w-16 {
  width: 4rem;
}

.h-16 {
  height: 4rem;
}

.rounded-full {
  border-radius: 9999px;
}

.flex-1 {
  flex: 1;
}

.justify-between {
  justify-content: space-between;
}

.mb-2 {
  margin-bottom: 0.5rem;
}

.font-semibold {
  font-weight: 600;
}

.text-lg {
  font-size: 1.125rem;
  line-height: 1.75rem;
}

.text-gray-800 {
  color: #1f2937;
}

.text-gray-500 {
  color: #6b7280;
}

.mb-3 {
  margin-bottom: 0.75rem;
}

.text-yellow-400 {
  color: #fbbf24;
}

.mr-2 {
  margin-right: 0.5rem;
}

.text-gray-600 {
  color: #4b5563;
}

.mb-4 {
  margin-bottom: 1rem;
}

.bg-gray-50 {
  background-color: #f9fafb;
}

.p-3 {
  padding: 0.75rem;
}

.w-8 {
  width: 2rem;
}

.h-8 {
  height: 2rem;
}

.flex-shrink-0 {
  flex-shrink: 0;
}

.mb-1 {
  margin-bottom: 0.25rem;
}

.font-medium {
  font-weight: 500;
}

.mt-4 {
  margin-top: 1rem;
}

.rows-2 {
  rows: 2;
}

.mt-2 {
  margin-top: 0.5rem;
}

.justify-end {
  justify-content: flex-end;
}

.hover\:bg-opacity-90:hover {
  background-color: rgba(76, 175, 80, 0.9);
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