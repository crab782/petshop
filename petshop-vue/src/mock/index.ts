import Mock from 'mockjs'
import './user'
import './user/shopping'
import './merchant'
import './merchant-api'
import './products'
import './services'
import './appointments'
import './orders'
import './reviews'
import './categories'
import './shop'
import './stats'

// Admin模块Mock
import './admin/dashboard'
import './admin/users'
import './admin/merchants'
import './admin/services'
import './admin/products'
import './admin/reviews'
import './admin/announcements'
import './admin/roles'
import './admin/logs'
import './admin/activities'
import './admin/tasks'
import './admin/shop-audit'

export interface MockConfig {
  enabled: boolean
  delay?: number
  debug?: boolean
}

const defaultConfig: MockConfig = {
  enabled: import.meta.env.VITE_USE_MOCK === 'true' || import.meta.env.DEV,
  delay: 200,
  debug: true
}

let isInitialized = false

export function setupMock(config: Partial<MockConfig> = {}) {
  const finalConfig = { ...defaultConfig, ...config }

  if (!finalConfig.enabled) {
    if (finalConfig.debug) {
      console.log('[Mock] Mock服务已禁用')
    }
    return
  }

  if (isInitialized) {
    if (finalConfig.debug) {
      console.log('[Mock] Mock服务已初始化，跳过重复初始化')
    }
    return
  }

  Mock.setup({
    timeout: finalConfig.delay || '200-500'
  })

  isInitialized = true

  if (finalConfig.debug) {
    console.log('[Mock] Mock服务初始化完成')
    console.log('[Mock] 已加载模块：')
    console.log('  - 用户模块 (user, pets, profile)')
    console.log('  - 购物模块 (cart, orders, favorites)')
    console.log('  - 商家模块 (merchant, dashboard)')
    console.log('  - 商品模块 (products)')
    console.log('  - 服务模块 (services)')
    console.log('  - 预约模块 (appointments)')
    console.log('  - 订单模块 (orders)')
    console.log('  - 评价模块 (reviews)')
    console.log('  - 分类模块 (categories)')
    console.log('  - 店铺模块 (shop)')
    console.log('  - 统计模块 (stats)')
    console.log('  - 平台端管理模块 (dashboard, users, merchants, services, products, reviews, announcements, roles, logs, activities, tasks, shop-audit)')
  }
}

export function resetMock() {
  Mock.mock(new RegExp('.*'), 'get', () => ({}))
  Mock.mock(new RegExp('.*'), 'post', () => ({}))
  Mock.mock(new RegExp('.*'), 'put', () => ({}))
  Mock.mock(new RegExp('.*'), 'delete', () => ({}))
  isInitialized = false
  console.log('[Mock] Mock服务已重置')
}

export function getMockStatus() {
  return {
    initialized: isInitialized,
    enabled: defaultConfig.enabled
  }
}

export default {
  setup: setupMock,
  reset: resetMock,
  getStatus: getMockStatus
}
