import Mock from 'mockjs'
import './admin/dashboard'
import './admin/users'
import './admin/merchants'
import './admin/shop-audit'
import './admin/services'
import './admin/products'
import './admin/reviews'
import './admin/announcements'
import './admin/roles'
import './admin/logs'
import './admin/activities'
import './admin/tasks'
import './user/handlers/appointments'
import './user/handlers/reviews'
import './user/handlers/notifications'

// 设置全局响应时间 100-500ms
Mock.setup({
  timeout: '100-500'
})

export function setupMock() {
  console.log('Mock服务已配置')
}

export default Mock
