<script setup lang="ts">
defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: String,
    default: ''
  },
  subtitle: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: ''
  },
  iconColor: {
    type: String,
    default: 'primary'
  },
  buttonText: {
    type: String,
    default: ''
  },
  buttonLink: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['button-click'])

const handleButtonClick = () => {
  emit('button-click')
}
</script>

<template>
  <div class="merchant-card card-hover bg-white rounded-lg shadow-md p-6">
    <!-- 标题和图标 -->
    <div class="flex items-center justify-between mb-4">
      <h3 class="text-lg font-semibold text-gray-700">{{ title }}</h3>
      <div class="w-12 h-12" :class="`bg-${iconColor}/20 rounded-full flex items-center justify-center`">
        <i :class="[icon, `text-${iconColor}`, 'text-2xl']"></i>
      </div>
    </div>

    <!-- 数值和副标题 -->
    <p v-if="value" class="text-3xl font-bold text-dark">{{ value }}</p>
    <p v-if="subtitle" class="text-gray-600 mt-2">{{ subtitle }}</p>

    <!-- 按钮 -->
    <button
      v-if="buttonText"
      :href="buttonLink"
      class="mt-4 bg-primary text-white px-4 py-2 rounded-lg hover:bg-opacity-90 transition"
      @click="handleButtonClick"
    >
      {{ buttonText }}
    </button>
  </div>
</template>

<style scoped>
.merchant-card {
  border-radius: 8px;
  transition: all 0.3s ease;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.merchant-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.bg-primary\/20 {
  background-color: rgba(76, 175, 80, 0.2);
}

.text-primary {
  color: #4CAF50;
}

.bg-secondary\/20 {
  background-color: rgba(255, 152, 0, 0.2);
}

.text-secondary {
  color: #FF9800;
}

.bg-green-100 {
  background-color: #f0f9ff;
}

.text-green-500 {
  color: #10b981;
}

.bg-blue-100 {
  background-color: #dbeafe;
}

.text-blue-500 {
  color: #3b82f6;
}

.hover\:bg-opacity-90:hover {
  background-color: rgba(76, 175, 80, 0.9);
}

.transition {
  transition: all 0.3s ease;
}
</style>