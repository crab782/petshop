import { describe, it, expect, beforeEach, vi } from 'vitest'
import {
  createWrapper,
  waitFor,
  flushPromises,
  mockLocalStorage,
  mockConsole,
  setupTestEnvironment,
  createDataProvider,
} from '../utils/testUtils'
import {
  createMerchant,
  createService,
  createProduct,
  createUser,
  createMerchantList,
  createServiceList,
  createSuccessResponse,
  createErrorResponse,
  createPaginatedResponse,
  type Merchant,
  type Service,
} from '../fixtures/merchantData'
import {
  mockRouter,
  mockPinia,
  mockMerchantStore,
  mockMerchantApi,
  mockElementPlus,
  resetAllMocks,
} from '../mocks/merchantMocks'

describe('Test Infrastructure', () => {
  describe('testUtils', () => {
    it('should create wrapper with options', () => {
      const mockComponent = {
        template: '<div class="test-component">Test</div>',
      }
      const wrapper = createWrapper(mockComponent)
      expect(wrapper.find('.test-component').exists()).toBe(true)
    })

    it('should wait for specified time', async () => {
      const start = Date.now()
      await waitFor(100)
      const elapsed = Date.now() - start
      expect(elapsed).toBeGreaterThanOrEqual(90)
    })

    it('should flush promises', async () => {
      let resolved = false
      Promise.resolve().then(() => {
        resolved = true
      })
      await flushPromises()
      expect(resolved).toBe(true)
    })

    it('should mock localStorage', () => {
      const storage = mockLocalStorage()
      storage.setItem('test', 'value')
      expect(storage.getItem('test')).toBe('value')
      storage.removeItem('test')
      expect(storage.getItem('test')).toBeNull()
    })

    it('should mock console', () => {
      const consoleMock = mockConsole()
      console.log('test')
      expect(consoleMock.log).toHaveBeenCalledWith('test')
      consoleMock.restore()
    })

    it('should setup test environment', () => {
      const env = setupTestEnvironment()
      expect(env.localStorage).toBeDefined()
      expect(env.sessionStorage).toBeDefined()
      expect(env.console).toBeDefined()
      env.cleanup()
    })

    it('should create data provider', () => {
      const provider = createDataProvider<Merchant>()
      const merchant = createMerchant()
      provider.add(merchant)
      expect(provider.getAll()).toHaveLength(1)
      expect(provider.findBy('id', 1)).toEqual(merchant)
      provider.clear()
      expect(provider.getAll()).toHaveLength(0)
    })
  })

  describe('merchantData fixtures', () => {
    it('should create default merchant', () => {
      const merchant = createMerchant()
      expect(merchant.id).toBe(1)
      expect(merchant.name).toBe('测试商家')
      expect(merchant.status).toBe('approved')
    })

    it('should create merchant with overrides', () => {
      const merchant = createMerchant({ name: '自定义商家', status: 'pending' })
      expect(merchant.name).toBe('自定义商家')
      expect(merchant.status).toBe('pending')
    })

    it('should create service with overrides', () => {
      const service = createService({ name: '自定义服务', price: 199.99 })
      expect(service.name).toBe('自定义服务')
      expect(service.price).toBe(199.99)
    })

    it('should create product with overrides', () => {
      const product = createProduct({ name: '自定义商品', stock: 50 })
      expect(product.name).toBe('自定义商品')
      expect(product.stock).toBe(50)
    })

    it('should create user with overrides', () => {
      const user = createUser({ username: '自定义用户' })
      expect(user.username).toBe('自定义用户')
    })

    it('should create merchant list', () => {
      const merchants = createMerchantList(5)
      expect(merchants).toHaveLength(5)
      expect(merchants[0].name).toBe('测试商家1')
      expect(merchants[4].name).toBe('测试商家5')
    })

    it('should create service list', () => {
      const services = createServiceList(3)
      expect(services).toHaveLength(3)
      expect(services[0].name).toBe('测试服务1')
    })

    it('should create success response', () => {
      const response = createSuccessResponse({ id: 1 }, '操作成功')
      expect(response.code).toBe(200)
      expect(response.message).toBe('操作成功')
      expect(response.data.id).toBe(1)
    })

    it('should create error response', () => {
      const response = createErrorResponse('操作失败', 400)
      expect(response.code).toBe(400)
      expect(response.message).toBe('操作失败')
      expect(response.data).toBeNull()
    })

    it('should create paginated response', () => {
      const merchants = createMerchantList(25)
      const paginated = createPaginatedResponse(merchants, 2, 10)
      expect(paginated.data).toHaveLength(10)
      expect(paginated.total).toBe(25)
      expect(paginated.page).toBe(2)
      expect(paginated.pageSize).toBe(10)
    })
  })

  describe('merchantMocks', () => {
    beforeEach(() => {
      resetAllMocks()
    })

    it('should create mock router', () => {
      const router = mockRouter()
      expect(router.push).toBeDefined()
      expect(router.replace).toBeDefined()
      expect(router.currentRoute.value.path).toBe('/')
    })

    it('should create mock pinia', () => {
      const pinia = mockPinia()
      expect(pinia).toBeDefined()
    })

    it('should create mock merchant store', () => {
      const store = mockMerchantStore()
      expect(store.merchant).toBeDefined()
      expect(store.setMerchant).toBeDefined()
      expect(store.isLoggedIn).toBe(true)
    })

    it('should create mock merchant api', async () => {
      const api = mockMerchantApi
      const response = await api.getMerchant()
      expect(response.code).toBe(200)
      expect(response.data.id).toBe(1)
    })

    it('should create mock element plus', () => {
      const el = mockElementPlus()
      expect(el.ElMessage.success).toBeDefined()
      expect(el.ElMessageBox.confirm).toBeDefined()
    })
  })
})
