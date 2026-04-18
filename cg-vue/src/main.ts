import './assets/main.css'

// Mock模拟服务 - 在开发环境启用
if (import.meta.env.DEV) {
  import('./mock').then(({ setupMock }) => {
    setupMock()
    console.log('Mock服务已启动')
  }).catch(err => {
    console.warn('Mock服务启动失败:', err)
  })
}

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
