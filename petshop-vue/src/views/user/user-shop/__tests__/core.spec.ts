import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import UserShop from '../index.vue'

const mockMerchantInfo = {
  id: 1,
  name: '测试宠物店',
  logo: 'https://example.com/logo.png',
  address: '北京市朝阳区',
  phone: '13800138000',
  description: '专业宠物服务',
  rating: 4.5,
}

const mockServices = [
  { id: 1, name: '宠物美容', price: 100, duration: 60, description: '专业美容' },
  { id: 2, name: '宠物寄养', price: 200, duration: 120, description: '专业寄养' },
]

const mockProducts = [
  { id: 1, name: '宠物狗粮', price: 99, stock: 100, description: '优质狗粮' },
  { id: 2, name: '宠物猫粮', price: 88, stock: 50, description: '优质猫粮' },
]

const mockReviews = [
  { id: 1, userName: '张三', rating: 5, content: '非常满意的服务', createTime: '2024-01-01' },
  { id: 2, userName: '李四', rating: 4, content: '服务不错', createTime: '2024-01-02' },
]

vi.mock('@/api/user', () => ({
  getMerchantInfo: vi.fn(() => Promise.resolve(mockMerchantInfo)),
  getMerchantServices: vi.fn(() => Promise.resolve(mockServices)),
  getMerchantProducts: vi.fn(() => Promise.resolve(mockProducts)),
  getMerchantReviews: vi.fn(() => Promise.resolve(mockReviews)),
  getFavorites: vi.fn(() => Promise.resolve([])),
  addFavorite: vi.fn(() => Promise.resolve({ id: 1, merchantId: 1 })),
  removeFavorite: vi.fn(() => Promise.resolve(null)),
  addToCart: vi.fn(() => Promise.resolve({ id: 1 })),
}))

vi.mock('@/stores/user', () => ({
  useUserStore: () => ({
    user: { id: 1, username: 'testuser' },
    isLoggedIn: true,
  }),
}))

describe('UserShop - Core Functions', () => {
  let router: any
  let pinia: any

  beforeEach(async () => {
    vi.clearAllMocks()

    pinia = createPinia()
    setActivePinia(pinia)

    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/', component: { template: '<div>Home</div>' } },
        { path: '/user/shop/:id', component: UserShop },
      ],
    })

    await router.push('/user/shop/1')
    await router.isReady()
  })

  it('should mount successfully', async () => {
    const wrapper = mount(UserShop, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-avatar': true,
          'el-rate': true,
          'el-button': true,
          'el-tabs': true,
          'el-tab-pane': true,
          'el-empty': true,
          'el-row': true,
          'el-col': true,
          'el-image': true,
          'el-icon': true,
        },
      },
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('should have correct merchantId computed', async () => {
    const wrapper = mount(UserShop, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-avatar': true,
          'el-rate': true,
          'el-button': true,
          'el-tabs': true,
          'el-tab-pane': true,
          'el-empty': true,
          'el-row': true,
          'el-col': true,
          'el-image': true,
          'el-icon': true,
        },
      },
    })

    expect(wrapper.vm.merchantId).toBe(1)
  })

  it('should calculate avgRating correctly', async () => {
    const wrapper = mount(UserShop, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-avatar': true,
          'el-rate': true,
          'el-button': true,
          'el-tabs': true,
          'el-tab-pane': true,
          'el-empty': true,
          'el-row': true,
          'el-col': true,
          'el-image': true,
          'el-icon': true,
        },
      },
    })

    await wrapper.vm.$nextTick()

    wrapper.vm.reviews = mockReviews
    await wrapper.vm.$nextTick()

    const avgRating = wrapper.vm.avgRating
    expect(parseFloat(avgRating)).toBeCloseTo(4.5, 1)
  })

  it('should format price correctly', async () => {
    const wrapper = mount(UserShop, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-avatar': true,
          'el-rate': true,
          'el-button': true,
          'el-tabs': true,
          'el-tab-pane': true,
          'el-empty': true,
          'el-row': true,
          'el-col': true,
          'el-image': true,
          'el-icon': true,
        },
      },
    })

    const formattedPrice = wrapper.vm.formatPrice(99.5)
    expect(formattedPrice).toBe('¥99.50')
  })

  it('should format date correctly', async () => {
    const wrapper = mount(UserShop, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-avatar': true,
          'el-rate': true,
          'el-button': true,
          'el-tabs': true,
          'el-tab-pane': true,
          'el-empty': true,
          'el-row': true,
          'el-col': true,
          'el-image': true,
          'el-icon': true,
        },
      },
    })

    const formattedDate = wrapper.vm.formatDate('2024-01-15T10:00:00.000Z')
    expect(formattedDate).toBeTruthy()
  })

  it('should handle empty date', async () => {
    const wrapper = mount(UserShop, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-avatar': true,
          'el-rate': true,
          'el-button': true,
          'el-tabs': true,
          'el-tab-pane': true,
          'el-empty': true,
          'el-row': true,
          'el-col': true,
          'el-image': true,
          'el-icon': true,
        },
      },
    })

    const formattedDate = wrapper.vm.formatDate('')
    expect(formattedDate).toBe('')
  })

  it('should have initial loading state as false', async () => {
    const wrapper = mount(UserShop, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-avatar': true,
          'el-rate': true,
          'el-button': true,
          'el-tabs': true,
          'el-tab-pane': true,
          'el-empty': true,
          'el-row': true,
          'el-col': true,
          'el-image': true,
          'el-icon': true,
        },
      },
    })

    expect(wrapper.vm.loading).toBe(false)
  })

  it('should have initial error state as false', async () => {
    const wrapper = mount(UserShop, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-avatar': true,
          'el-rate': true,
          'el-button': true,
          'el-tabs': true,
          'el-tab-pane': true,
          'el-empty': true,
          'el-row': true,
          'el-col': true,
          'el-image': true,
          'el-icon': true,
        },
      },
    })

    expect(wrapper.vm.error).toBe(false)
  })

  it('should have initial activeTab as services', async () => {
    const wrapper = mount(UserShop, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-avatar': true,
          'el-rate': true,
          'el-button': true,
          'el-tabs': true,
          'el-tab-pane': true,
          'el-empty': true,
          'el-row': true,
          'el-col': true,
          'el-image': true,
          'el-icon': true,
        },
      },
    })

    expect(wrapper.vm.activeTab).toBe('services')
  })
})
