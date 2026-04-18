import Mock from 'mockjs'
import type { MockModule } from './types'
import {
  setupUserHandlers,
  setupAppointmentHandlers,
  setupCartHandlers,
  setupFavoriteHandlers,
  setupReviewHandlers,
  setupServiceHandlers,
  setupMerchantHandlers,
  setupDashboardHandlers,
  setupAnnouncementHandlers,
  setupAddressHandlers,
  setupSearchHandlers,
  setupStatsHandlers,
  setupUserHomeHandlers,
  setupUserServicesHandlers,
  setupAuthHandlers
} from './handlers'

export * from './types'
export * from './data'
export * from './utils/generators'

export interface MockConfig {
  enabled: boolean
  delay?: number
  debug?: boolean
}

const defaultConfig: MockConfig = {
  enabled: import.meta.env.DEV,
  delay: 0,
  debug: true
}

let isInitialized = false

const mockModules: MockModule[] = [
  {
    name: 'user',
    handlers: [setupUserHandlers]
  },
  {
    name: 'appointment',
    handlers: [setupAppointmentHandlers]
  },
  {
    name: 'cart',
    handlers: [setupCartHandlers, setupFavoriteHandlers, setupReviewHandlers]
  },
  {
    name: 'service',
    handlers: [setupServiceHandlers, setupMerchantHandlers, setupDashboardHandlers]
  },
  {
    name: 'search',
    handlers: [setupSearchHandlers]
  },
  {
    name: 'stats',
    handlers: [setupStatsHandlers]
  },
  {
    name: 'announcement',
    handlers: [setupAnnouncementHandlers]
  },
  {
    name: 'address',
    handlers: [setupAddressHandlers]
  },
  {
    name: 'user-home',
    handlers: [setupUserHomeHandlers]
  },
  {
    name: 'user-services',
    handlers: [setupUserServicesHandlers]
  },
  {
    name: 'auth',
    handlers: [setupAuthHandlers]
  }
]

export const setupUserMock = (config: Partial<MockConfig> = {}) => {
  const finalConfig = { ...defaultConfig, ...config }

  if (!finalConfig.enabled) {
    if (finalConfig.debug) {
      console.log('[User Mock] Mock服务已禁用')
    }
    return
  }

  if (isInitialized) {
    if (finalConfig.debug) {
      console.log('[User Mock] Mock服务已初始化，跳过重复初始化')
    }
    return
  }

  Mock.setup({
    timeout: finalConfig.delay || '200-500'
  })

  mockModules.forEach(module => {
    module.handlers.forEach(handler => {
      try {
        handler()
        if (finalConfig.debug) {
          console.log(`[User Mock] 模块 ${module.name} 加载成功`)
        }
      } catch (error) {
        console.error(`[User Mock] 模块 ${module.name} 加载失败:`, error)
      }
    })
  })

  isInitialized = true

  if (finalConfig.debug) {
    console.log('[User Mock] 用户端Mock服务初始化完成')
  }
}

export const resetUserMock = () => {
  Mock.mock(new RegExp('.*'), 'get', () => ({}))
  Mock.mock(new RegExp('.*'), 'post', () => ({}))
  Mock.mock(new RegExp('.*'), 'put', () => ({}))
  Mock.mock(new RegExp('.*'), 'delete', () => ({}))
  isInitialized = false
  console.log('[User Mock] Mock服务已重置')
}

export const enableModule = (moduleName: string) => {
  const module = mockModules.find(m => m.name === moduleName)
  if (module) {
    module.handlers.forEach(handler => handler())
    console.log(`[User Mock] 模块 ${moduleName} 已启用`)
  } else {
    console.warn(`[User Mock] 模块 ${moduleName} 不存在`)
  }
}

export const getMockStatus = () => ({
  initialized: isInitialized,
  modules: mockModules.map(m => m.name)
})

export default {
  setup: setupUserMock,
  reset: resetUserMock,
  enableModule,
  getStatus: getMockStatus
}
