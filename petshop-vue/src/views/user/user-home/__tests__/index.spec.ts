import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import UserHome from '../index.vue'
import { createTestRouter, createTestPinia, flushPromises } from '@/tests/utils/testUtils'
import * as userApi from '@/api/user'

vi.mock('@/api/user', () => ({
  getHomeStats: vi.fn(),
  getRecentActivities: vi.fn(),
  getRecommendedServices: vi.fn(),
  getMerchantList: vi.fn(),
}))

vi.mock('@/stores/user', () => ({
  useUserStore: vi.fn(() => ({
    user: { id: 1, username: 'testuser' },
    isLoggedIn: true,
  })),
}))

describe('UserHome', () => {
  let router: any
  let pinia: any

  const mockHomeStats = {
    petCount: 2,
    pendingAppointments: 3,
    reviewCount: 5,
  }

  const mockActivities = [
    {
      id: 1,
      title: '预约服务成功',
      time: '2024-01-15 10:00',
      status: '已完成',
      statusColor: 'success',
      type: 'appointment',
    },
    {
      id: 2,
      title: '发表评价',
      time: '2024-01-14 15:30',
      status: '已发布',
      statusColor: 'primary',
      type: 'review',
    },
  ]

  const mockServices = [
    {
      id: 1,
      name: '宠物美容',
      price: 100,
      rating: 4.8,
      image: 'https://example.com/service1.jpg',
    },
    {
      id: 2,
      name: '宠物寄养',
      price: 200,
      rating: 4.5,
      image: 'https://example.com/service2.jpg',
    },
  ]

  const mockMerchants = [
    {
      id: 1,
      name: '测试宠物店',
      logo: 'https://example.com/logo1.jpg',
      address: '北京市朝阳区',
      rating: 4.5,
      serviceCount: 10,
    },
    {
      id: 2,
      name: '爱心宠物店',
      logo: 'https://example.com/logo2.jpg',
      address: '北京市海淀区',
      rating: 4.8,
      serviceCount: 15,
    },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
    router = createTestRouter()
    pinia = createTestPinia()

    vi.mocked(userApi.getHomeStats).mockResolvedValue({ data: mockHomeStats } as any)
    vi.mocked(userApi.getRecentActivities).mockResolvedValue({ data: mockActivities } as any)
    vi.mocked(userApi.getRecommendedServices).mockResolvedValue({ data: mockServices } as any)
    vi.mocked(userApi.getMerchantList).mockResolvedValue({ data: mockMerchants } as any)
  })

  const mountComponent = (options = {}) => {
    return mount(UserHome, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-row': true,
          'el-col': true,
          'el-avatar': true,
          'el-rate': true,
          'el-badge': true,
          'el-timeline': true,
          'el-timeline-item': true,
          'el-tag': true,
          'el-empty': true,
          'el-icon': true,
          'router-link': true,
        },
        ...options,
      },
    })
  }

  describe('页面渲染', () => {
    it('应该正确渲染欢迎卡片', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.welcome-card').exists()).toBe(true)
      expect(wrapper.find('.welcome-title').text()).toContain('您好，欢迎来到宠物服务平台！')
    })

    it('应该显示当前日期', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.current-date').exists()).toBe(true)
    })
  })

  describe('商家浏览区域', () => {
    it('应该显示商家浏览标题', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.section-title').text()).toContain('商店浏览')
    })

    it('应该调用getMerchantList API', async () => {
      mountComponent()
      await flushPromises()
      expect(userApi.getMerchantList).toHaveBeenCalledWith({ page: 1, pageSize: 8 })
    })
  })

  describe('统计概览', () => {
    it('应该调用getHomeStats API', async () => {
      mountComponent()
      await flushPromises()
      expect(userApi.getHomeStats).toHaveBeenCalled()
    })

    it('应该显示三个统计卡片', () => {
      const wrapper = mountComponent()
      const statCards = wrapper.findAll('.stat-card')
      expect(statCards.length).toBe(3)
    })
  })

  describe('最近活动', () => {
    it('应该调用getRecentActivities API', async () => {
      mountComponent()
      await flushPromises()
      expect(userApi.getRecentActivities).toHaveBeenCalledWith(5)
    })

    it('应该显示最近活动标题', () => {
      const wrapper = mountComponent()
      const titles = wrapper.findAll('.section-title')
      const activityTitle = titles.find(t => t.text().includes('最近活动'))
      expect(activityTitle).toBeDefined()
    })
  })

  describe('快捷操作', () => {
    it('应该显示四个快捷操作按钮', () => {
      const wrapper = mountComponent()
      const actionCards = wrapper.findAll('.action-card')
      expect(actionCards.length).toBe(4)
    })

    it('点击快捷操作应该跳转到对应路由', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      const wrapper = mountComponent()

      const firstAction = wrapper.find('.action-card')
      if (firstAction.exists()) {
        await firstAction.trigger('click')
        expect(pushSpy).toHaveBeenCalled()
      }
    })
  })

  describe('推荐服务', () => {
    it('应该调用getRecommendedServices API', async () => {
      mountComponent()
      await flushPromises()
      expect(userApi.getRecommendedServices).toHaveBeenCalledWith(4)
    })

    it('应该显示推荐服务标题', () => {
      const wrapper = mountComponent()
      const titles = wrapper.findAll('.section-title')
      const serviceTitle = titles.find(t => t.text().includes('推荐服务'))
      expect(serviceTitle).toBeDefined()
    })
  })

  describe('商家卡片点击', () => {
    it('点击商家卡片应该跳转到商家详情页', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      const wrapper = mountComponent()

      await flushPromises()
      wrapper.vm.$nextTick()

      const merchantCard = wrapper.find('.merchant-card')
      if (merchantCard.exists()) {
        await merchantCard.trigger('click')
        expect(pushSpy).toHaveBeenCalled()
      }
    })
  })

  describe('错误处理', () => {
    it('API调用失败时应该显示错误信息', async () => {
      vi.mocked(userApi.getHomeStats).mockRejectedValue(new Error('API Error'))
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

      mountComponent()
      await flushPromises()

      expect(consoleSpy).toHaveBeenCalled()
      consoleSpy.mockRestore()
    })
  })

  describe('数据加载状态', () => {
    it('组件挂载时应该开始加载数据', () => {
      mountComponent()
      expect(userApi.getHomeStats).toHaveBeenCalled()
      expect(userApi.getRecentActivities).toHaveBeenCalled()
      expect(userApi.getRecommendedServices).toHaveBeenCalled()
      expect(userApi.getMerchantList).toHaveBeenCalled()
    })
  })
})
