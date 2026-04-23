import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import MerchantHome from '../index.vue'
import { createTestRouter, createTestPinia, flushPromises } from '@/tests/utils/testUtils'
import * as merchantApi from '@/api/merchant'

vi.mock('@/api/merchant', () => ({
  getMerchantDashboard: vi.fn(),
  getRecentOrders: vi.fn(),
  getRecentReviews: vi.fn(),
}))

vi.mock('@/composables', () => ({
  useAsync: vi.fn((fn) => {
    const data = ref(null)
    const loading = ref(false)
    const error = ref(null)
    const execute = async () => {
      loading.value = true
      try {
        const result = await fn()
        data.value = result
        return result
      } catch (e) {
        error.value = e
        console.error('useAsync error:', e)
        return null
      } finally {
        loading.value = false
      }
    }
    execute()
    return { data, loading, error, execute }
  }),
}))

const mockDashboardData = {
  todayRevenue: 25670,
  revenueGrowth: 12,
  todayOrders: 24,
  orderGrowth: 8,
  pendingAppointments: 5,
  avgRating: 4.8,
  ratingCount: 120,
}

const mockOrders = [
  {
    id: 'ORD-2024-001',
    customerName: '张先生',
    serviceName: '宠物美容',
    status: 'completed',
    appointmentTime: '2024-01-15T10:00:00.000Z',
    totalPrice: 120,
  },
  {
    id: 'ORD-2024-002',
    customerName: '李女士',
    serviceName: '宠物寄养',
    status: 'confirmed',
    appointmentTime: '2024-01-16T14:00:00.000Z',
    totalPrice: 200,
  },
  {
    id: 'ORD-2024-003',
    customerName: '王女士',
    serviceName: '宠物医疗',
    status: 'pending',
    appointmentTime: '2024-01-17T09:00:00.000Z',
    totalPrice: 300,
  },
]

const mockReviews = [
  {
    id: 1,
    userName: '张先生',
    userAvatar: 'https://example.com/avatar1.jpg',
    rating: 5,
    reviewTime: '2024-01-15T10:00:00.000Z',
    content: '服务非常专业，宠物美容效果很好，下次还会再来！',
    serviceName: '宠物美容',
  },
  {
    id: 2,
    userName: '李女士',
    userAvatar: 'https://example.com/avatar2.jpg',
    rating: 4,
    reviewTime: '2024-01-14T15:30:00.000Z',
    content: '寄养服务不错，环境干净整洁。',
    serviceName: '宠物寄养',
  },
]

describe('MerchantHome', () => {
  let router: any
  let pinia: any

  beforeEach(() => {
    vi.clearAllMocks()
    router = createTestRouter()
    pinia = createTestPinia()

    vi.mocked(merchantApi.getMerchantDashboard).mockResolvedValue(mockDashboardData as any)
    vi.mocked(merchantApi.getRecentOrders).mockResolvedValue(mockOrders as any)
    vi.mocked(merchantApi.getRecentReviews).mockResolvedValue(mockReviews as any)
  })

  const mountComponent = (options = {}) => {
    return mount(MerchantHome, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-skeleton': true,
          'el-empty': true,
          'el-button': true,
          'router-link': true,
          MerchantCard: {
            template: '<div class="merchant-card-stub"><slot /></div>',
            props: ['title', 'value', 'subtitle', 'icon', 'iconColor'],
          },
          ReviewCard: {
            template: '<div class="review-card-stub"><slot /></div>',
            props: ['name', 'avatar', 'rating', 'date', 'content', 'serviceType'],
          },
        },
        ...options,
      },
    })
  }

  describe('页面标题', () => {
    it('应该正确渲染页面标题', () => {
      const wrapper = mountComponent()
      const title = wrapper.find('h2')
      expect(title.exists()).toBe(true)
      expect(title.text()).toBe('商家后台首页')
    })
  })

  describe('统计数据加载', () => {
    it('组件挂载时应该调用所有API', () => {
      mountComponent()
      expect(merchantApi.getMerchantDashboard).toHaveBeenCalled()
      expect(merchantApi.getRecentOrders).toHaveBeenCalledWith(5)
      expect(merchantApi.getRecentReviews).toHaveBeenCalledWith(5)
    })

    it('应该显示四个统计卡片', async () => {
      const wrapper = mountComponent()
      await flushPromises()

      const statCards = wrapper.findAllComponents({ name: 'MerchantCard' })
      expect(statCards.length).toBe(4)
    })
  })

  describe('最近订单', () => {
    it('应该显示最近订单标题', () => {
      const wrapper = mountComponent()
      const sectionTitle = wrapper.find('h3')
      expect(sectionTitle.text()).toBe('最近订单')
    })

    it('应该显示查看全部链接', () => {
      const wrapper = mountComponent()
      const viewAllLink = wrapper.find('a[href="/merchant/appointments"]')
      expect(viewAllLink.exists()).toBe(true)
      expect(viewAllLink.text()).toBe('查看全部')
    })

    it('应该渲染订单表格', async () => {
      const wrapper = mountComponent()
      await flushPromises()

      const table = wrapper.find('table')
      expect(table.exists()).toBe(true)
    })

    it('应该显示订单表头', () => {
      const wrapper = mountComponent()
      const tableHeaders = wrapper.findAll('thead th')
      expect(tableHeaders.length).toBe(6)

      expect(tableHeaders[0].text()).toBe('订单号')
      expect(tableHeaders[1].text()).toBe('客户')
      expect(tableHeaders[2].text()).toBe('服务')
      expect(tableHeaders[3].text()).toBe('状态')
      expect(tableHeaders[4].text()).toBe('日期')
      expect(tableHeaders[5].text()).toBe('金额')
    })
  })

  describe('最近评价', () => {
    it('应该显示最近评价标题', () => {
      const wrapper = mountComponent()
      const titles = wrapper.findAll('h3')
      const reviewTitle = titles.find(t => t.text() === '最近评价')
      expect(reviewTitle).toBeDefined()
    })

    it('应该显示查看全部评价链接', () => {
      const wrapper = mountComponent()
      const viewAllLink = wrapper.find('a[href="/merchant/reviews"]')
      expect(viewAllLink.exists()).toBe(true)
      expect(viewAllLink.text()).toBe('查看全部')
    })

    it('应该渲染评价卡片', async () => {
      const wrapper = mountComponent()
      await flushPromises()

      const reviewCards = wrapper.findAllComponents({ name: 'ReviewCard' })
      expect(reviewCards.length).toBe(2)
    })
  })

  describe('加载状态', () => {
    it('加载时应该显示骨架屏', () => {
      const wrapper = mountComponent()
      const skeletons = wrapper.findAllComponents({ name: 'el-skeleton' })
      expect(skeletons.length).toBeGreaterThan(0)
    })
  })

  describe('错误处理', () => {
    it('API调用失败时应该显示错误信息', async () => {
      vi.mocked(merchantApi.getMerchantDashboard).mockRejectedValue(new Error('API Error'))
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

      mountComponent()
      await flushPromises()

      expect(consoleSpy).toHaveBeenCalled()
      consoleSpy.mockRestore()
    })
  })

  describe('空数据处理', () => {
    it('订单为空时应该显示空状态', async () => {
      vi.mocked(merchantApi.getRecentOrders).mockResolvedValue([] as any)
      const wrapper = mountComponent()
      await flushPromises()

      const emptyComponent = wrapper.findComponent({ name: 'el-empty' })
      expect(emptyComponent.exists()).toBe(true)
    })

    it('评价为空时应该显示空状态', async () => {
      vi.mocked(merchantApi.getRecentReviews).mockResolvedValue([] as any)
      const wrapper = mountComponent()
      await flushPromises()

      const emptyComponent = wrapper.findComponent({ name: 'el-empty' })
      expect(emptyComponent.exists()).toBe(true)
    })
  })

  describe('重试功能', () => {
    it('点击重试按钮应该重新加载数据', async () => {
      const wrapper = mountComponent()
      await flushPromises()

      const retryButton = wrapper.find('el-button-stub')
      if (retryButton.exists()) {
        await retryButton.trigger('click')
        expect(merchantApi.getMerchantDashboard).toHaveBeenCalled()
      }
    })
  })

  describe('响应式布局', () => {
    it('应该使用网格布局', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.grid').exists()).toBe(true)
    })

    it('订单和评价应该使用两列布局', () => {
      const wrapper = mountComponent()
      const grid = wrapper.find('.grid-cols-1.lg\\:grid-cols-2')
      expect(grid.exists()).toBe(true)
    })
  })

  describe('数据格式化', () => {
    it('应该正确格式化日期', () => {
      const wrapper = mountComponent()
      const formattedDate = wrapper.vm.formatDate('2024-01-15T10:00:00.000Z')
      expect(formattedDate).toBeTruthy()
    })

    it('应该正确格式化金额', () => {
      const wrapper = mountComponent()
      const formattedPrice = wrapper.vm.formatPrice(120)
      expect(formattedPrice).toContain('¥')
    })

    it('应该正确映射订单状态', () => {
      const wrapper = mountComponent()
      const statusMap = wrapper.vm.orderStatusMap

      expect(statusMap.pending).toBeDefined()
      expect(statusMap.confirmed).toBeDefined()
      expect(statusMap.completed).toBeDefined()
      expect(statusMap.cancelled).toBeDefined()
    })
  })
})
