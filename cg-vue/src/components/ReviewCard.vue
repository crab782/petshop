<script setup lang="ts">
defineProps({
  name: {
    type: String,
    required: true
  },
  avatar: {
    type: String,
    default: ''
  },
  rating: {
    type: Number,
    default: 5
  },
  date: {
    type: String,
    required: true
  },
  content: {
    type: String,
    required: true
  },
  serviceType: {
    type: String,
    required: true
  }
})
</script>

<template>
  <div class="review-card border border-gray-200 rounded-lg p-4">
    <div class="flex items-start justify-between mb-3">
      <div class="flex items-center">
        <img :src="avatar" alt="客户头像" class="w-10 h-10 rounded-full mr-3">
        <div>
          <h4 class="font-semibold text-dark">{{ name }}</h4>
          <div class="text-yellow-400">
            <i v-for="i in 5" :key="i" class="fa" :class="i <= rating ? 'fa-star' : 'fa-star-o'"></i>
          </div>
        </div>
      </div>
      <span class="text-gray-500 text-sm">{{ date }}</span>
    </div>
    <p class="text-gray-600 mb-3">{{ content }}</p>
    <div class="text-sm text-gray-500">
      服务类型：{{ serviceType }}
    </div>
  </div>
</template>

<style scoped>
.review-card {
  transition: all 0.3s ease;
}

.review-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}
</style>