<script setup lang="ts">
defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: ''
  },
  image: {
    type: String,
    default: ''
  },
  buttonText: {
    type: String,
    default: '了解详情'
  },
  buttonLink: {
    type: String,
    default: '#'
  }
})
</script>

<template>
  <div class="card card-hover bg-white rounded-lg p-6 shadow-md">
    <!-- 图标或图片 -->
    <div v-if="icon" class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mb-4">
      <i :class="icon" class="text-primary text-2xl"></i>
    </div>
    <img v-else-if="image" :src="image" alt="" class="w-full h-48 object-cover rounded-lg mb-4">
    
    <!-- 内容 -->
    <h3 class="text-xl font-semibold mb-3">{{ title }}</h3>
    <p v-if="description" class="text-gray-600 mb-4">{{ description }}</p>
    
    <!-- 按钮 -->
    <a v-if="buttonLink" :href="buttonLink" class="inline-block bg-primary text-white px-4 py-2 rounded hover:bg-opacity-90 transition">
      {{ buttonText }}
    </a>
  </div>
</template>

<style scoped>
.card {
  border-radius: 8px;
  transition: all 0.3s ease;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.bg-primary\/20 {
  background-color: rgba(76, 175, 80, 0.2);
}

.text-primary {
  color: #4CAF50;
}

.hover\:bg-opacity-90:hover {
  background-color: rgba(76, 175, 80, 0.9);
}

.transition {
  transition: all 0.3s ease;
}
</style>