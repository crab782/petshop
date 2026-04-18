<script setup lang="ts">
defineProps({
  type: {
    type: String,
    default: 'primary',
    validator: (value: string) => ['primary', 'secondary', 'outline', 'text'].includes(value)
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value: string) => ['small', 'medium', 'large'].includes(value)
  },
  round: {
    type: Boolean,
    default: false
  },
  fullWidth: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

const handleClick = (event: MouseEvent) => {
  emit('click', event)
}
</script>

<template>
  <button
    :class="[
      'btn',
      `btn-${type}`,
      `btn-${size}`,
      { 'btn-round': round },
      { 'btn-full-width': fullWidth }
    ]"
    @click="handleClick"
  >
    <slot></slot>
  </button>
</template>

<style scoped>
.btn {
  font-family: Arial, sans-serif;
  font-weight: medium;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

/* 按钮类型 */
.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-primary:hover {
  background-color: #45a049;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(76, 175, 80, 0.3);
}

.btn-secondary {
  background-color: #FF9800;
  color: white;
}

.btn-secondary:hover {
  background-color: #f57c00;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(255, 152, 0, 0.3);
}

.btn-outline {
  background-color: transparent;
  color: #4CAF50;
  border: 2px solid #4CAF50;
}

.btn-outline:hover {
  background-color: #4CAF50;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(76, 175, 80, 0.3);
}

.btn-text {
  background-color: transparent;
  color: #4CAF50;
  border: none;
}

.btn-text:hover {
  background-color: rgba(76, 175, 80, 0.1);
  transform: translateY(-1px);
}

/* 按钮大小 */
.btn-small {
  padding: 6px 12px;
  font-size: 14px;
}

.btn-medium {
  padding: 8px 16px;
  font-size: 16px;
}

.btn-large {
  padding: 12px 24px;
  font-size: 18px;
}

/* 圆角按钮 */
.btn-round {
  border-radius: 9999px;
}

/* 全宽按钮 */
.btn-full-width {
  width: 100%;
}
</style>