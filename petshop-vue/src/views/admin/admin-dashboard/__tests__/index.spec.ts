import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import AdminDashboard from '../index.vue'
import { createTestRouter, createTestPinia, flushPromises } from '@/tests/utils/testUtils'

global.fetch = vi.fn()

describe('AdminDashboard', () => {
  let router: any
  let pinia: any

  const mockDashboardStats = {
    userCount: 2580,
    merchantCount: 150,
    orderCount: 320,
  }

  const mockRecentUsers = {
    code: 200,
    data: {
      data: [
        {
          id: 1,
          username: 'testuser1',
          phone: '13800138001',
          email: 'test1@example.com',
          avatar: 'https://example.com/avatar1.jpg',
          createdAt: '2024-01-15T10:00:00.000Z',
        },
        {
          id: 2,
          username: 'testuser2',
          phone: '13800138002',
          email: 'test2@example.com',
          avatar: 'https://example.com/avatar2.jpg',
          createdAt: '2024-01-14T15:30:00.000Z',
        },
      ],
    },
  }

  const mockPendingMerchants = {
    code: 200,
    data: {
      data: [
        {
          id: 1,
          name: '测试宠物店',
          contactPerson: '张三',
          phone: '13800138003',
          email: 'merchant1@example.com',
          address: '北京市朝阳区',
          createdAt: '2024-01-15T10:00:00.000Z',
        },
      ],
    },
  }

  const mockAnnouncements = {
    code: 200,
    data: {
      data: [
        {
          id: 1,
          title: '平台服务升级通知',
          content: '平台将于本周日凌晨2:00-6:00进行系统升级',
          createdAt: '2024-01-15T08:00:00.000Z',
        },
      ],
    },
  }

  beforeEach(() => {
    vi.clearAllMocks()
    router = createTestRouter()
    pinia = createTestPinia()

    vi.mocked(fetch).mockImplementation((url: string) => {
      if (url.includes('/dashboard/recent-users')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockRecentUsers),
        } as any)
      } else if (url.includes('/dashboard/pending-merchants')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockPendingMerchants),
        } as any)
      } else if (url.includes('/dashboard/announcements')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockAnnouncements),
        } as any)
      } else {
        return Promise.resolve({
          ok: true,
          json: () =>
            Promise.resolve({
              code: 200,
              data: mockDashboardStats,
            }),
        } as any)
      }
    })
  })

  const mountComponent = (options = {}) => {
    return mount(AdminDashboard, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-button': true,
          'el-table': true,
          'el-table-column': true,
          'el-empty': true,
          AdminCard: {
            template: '<div class="admin-card-stub"><slot /></div>',
            props: ['title', 'value', 'subtitle', 'icon', 'iconColor'],
          },
        },
        ...options,
      },
    })
  }

  describe('欢迎区域', () => {
    it('应该显示欢迎标题', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.welcome-title').text()).toBe('欢迎回来，管理员！')
    })

    it('应该显示欢迎文本和当前日期', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.welcome-text').text()).toContain('今天是')
    })
  })

  describe('统计卡片', () => {
    it('应该显示四个统计卡片', () => {
      const wrapper = mountComponent()
      const statCards = wrapper.findAllComponents({ name: 'AdminCard' })
      expect(statCards.length).toBe(4)
    })

    it('第一个统计卡片应该显示总用户数', () => {
      const wrapper = mountComponent()
      const statCards = wrapper.findAllComponents({ name: 'AdminCard' })
      expect(statCards[0].props('title')).toBe('总用户数')
      expect(statCards[0].props('icon')).toBe('fa fa-users')
    })

    it('第二个统计卡片应该显示总商家数', () => {
      const wrapper = mountComponent()
      const statCards = wrapper.findAllComponents({ name: 'AdminCard' })
      expect(statCards[1].props('title')).toBe('总商家数')
      expect(statCards[1].props('icon')).toBe('fa fa-building')
    })

    it('第三个统计卡片应该显示本月营收', () => {
      const wrapper = mountComponent()
      const statCards = wrapper.findAllComponents({ name: 'AdminCard' })
      expect(statCards[2].props('title')).toBe('本月营收')
      expect(statCards[2].props('icon')).toBe('fa fa-rmb')
    })

    it('第四个统计卡片应该显示今日订单', () => {
      const wrapper = mountComponent()
      const statCards = wrapper.findAllComponents({ name: 'AdminCard' })
      expect(statCards[3].props('title')).toBe('今日订单')
      expect(statCards[3].props('icon')).toBe('fa fa-shopping-cart')
    })
  })

  describe('最近注册用户', () => {
    it('应该显示最近注册用户标题', () => {
      const wrapper = mountComponent()
      const titles = wrapper.findAll('.section-title')
      const userTitle = titles.find(t => t.text() === '最近注册用户')
      expect(userTitle).toBeDefined()
    })

    it('应该调用获取最近用户API', async () => {
      mountComponent()
      await flushPromises()

      expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/dashboard/recent-users'))
    })

    it('应该显示用户表格', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.users-table').exists()).toBe(true)
    })
  })

  describe('待审核商家', () => {
    it('应该显示待审核商家标题', () => {
      const wrapper = mountComponent()
      const titles = wrapper.findAll('.section-title')
      const merchantTitle = titles.find(t => t.text() === '待审核商家')
      expect(merchantTitle).toBeDefined()
    })

    it('应该调用获取待审核商家API', async () => {
      mountComponent()
      await flushPromises()

      expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/dashboard/pending-merchants'))
    })

    it('应该显示商家表格', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.merchants-table').exists()).toBe(true)
    })
  })

  describe('快捷操作', () => {
    it('应该显示四个快捷操作项', () => {
      const wrapper = mountComponent()
      const quickActions = wrapper.findAll('.quick-action-item')
      expect(quickActions.length).toBe(4)
    })

    it('第一个快捷操作应该是用户管理', () => {
      const wrapper = mountComponent()
      const quickActions = wrapper.findAll('.quick-action-item')
      expect(quickActions[0].find('.action-title').text()).toBe('用户管理')
    })

    it('点击快捷操作应该触发handleQuickAction', async () => {
      const wrapper = mountComponent()
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {})

      const firstAction = wrapper.find('.quick-action-item')
      await firstAction.trigger('click')

      expect(consoleSpy).toHaveBeenCalledWith('快捷操作:', expect.any(Object))
      consoleSpy.mockRestore()
    })
  })

  describe('系统公告', () => {
    it('应该显示系统公告标题', () => {
      const wrapper = mountComponent()
      const titles = wrapper.findAll('.section-title')
      const announcementTitle = titles.find(t => t.text() === '系统公告')
      expect(announcementTitle).toBeDefined()
    })

    it('应该调用获取公告API', async () => {
      mountComponent()
      await flushPromises()

      expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/dashboard/announcements'))
    })

    it('点击公告应该触发handleAnnouncementClick', async () => {
      const wrapper = mountComponent()
      await flushPromises()

      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {})
      const announcementItem = wrapper.find('.announcement-item')

      if (announcementItem.exists()) {
        await announcementItem.trigger('click')
        expect(consoleSpy).toHaveBeenCalledWith('查看公告:', expect.any(Object))
      }

      consoleSpy.mockRestore()
    })
  })

  describe('数据加载', () => {
    it('组件挂载时应该调用所有API', () => {
      mountComponent()

      expect(fetch).toHaveBeenCalledWith('/api/admin/dashboard')
      expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/dashboard/recent-users'))
      expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/dashboard/pending-merchants'))
      expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/dashboard/announcements'))
    })

    it('应该设置当前日期', () => {
      const wrapper = mountComponent()
      expect(wrapper.vm.currentDate).toBe(new Date().toLocaleDateString('zh-CN'))
    })
  })

  describe('错误处理', () => {
    it('API调用失败时应该显示错误信息', async () => {
      vi.mocked(fetch).mockRejectedValueOnce(new Error('API Error'))
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

      mountComponent()
      await flushPromises()

      expect(consoleSpy).toHaveBeenCalled()
      consoleSpy.mockRestore()
    })

    it('API返回错误状态码时应该处理错误', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({
        ok: false,
      } as any)

      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

      mountComponent()
      await flushPromises()

      expect(consoleSpy).toHaveBeenCalled()
      consoleSpy.mockRestore()
    })
  })

  describe('空数据处理', () => {
    it('用户数据为空时应该显示空状态', async () => {
      vi.mocked(fetch).mockImplementation((url: string) => {
        if (url.includes('/dashboard/recent-users')) {
          return Promise.resolve({
            ok: true,
            json: () =>
              Promise.resolve({
                code: 200,
                data: { data: [] },
              }),
          } as any)
        }
        return Promise.resolve({
          ok: true,
          json: () =>
            Promise.resolve({
              code: 200,
              data: mockDashboardStats,
            }),
        } as any)
      })

      const wrapper = mountComponent()
      await flushPromises()

      const emptyComponent = wrapper.findComponent({ name: 'el-empty' })
      expect(emptyComponent.exists()).toBe(true)
    })
  })

  describe('响应式布局', () => {
    it('应该使用网格布局', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.stats-grid').exists()).toBe(true)
      expect(wrapper.find('.main-content').exists()).toBe(true)
    })

    it('应该有左右内容区域', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.left-content').exists()).toBe(true)
      expect(wrapper.find('.right-content').exists()).toBe(true)
    })
  })

  describe('方法调用', () => {
    it('handleUserClick应该记录日志', () => {
      const wrapper = mountComponent()
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {})

      wrapper.vm.handleUserClick({ id: 1, username: '测试用户' })

      expect(consoleSpy).toHaveBeenCalledWith('查看用户:', { id: 1, username: '测试用户' })
      consoleSpy.mockRestore()
    })

    it('handleMerchantClick应该记录日志', () => {
      const wrapper = mountComponent()
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {})

      wrapper.vm.handleMerchantClick({ id: 1, name: '测试商家' })

      expect(consoleSpy).toHaveBeenCalledWith('审核商家:', { id: 1, name: '测试商家' })
      consoleSpy.mockRestore()
    })

    it('handleAnnouncementClick应该记录日志', () => {
      const wrapper = mountComponent()
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {})

      wrapper.vm.handleAnnouncementClick({ id: 1, title: '测试公告' })

      expect(consoleSpy).toHaveBeenCalledWith('查看公告:', { id: 1, title: '测试公告' })
      consoleSpy.mockRestore()
    })

    it('handleQuickAction应该记录日志', () => {
      const wrapper = mountComponent()
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {})

      wrapper.vm.handleQuickAction({ title: '测试操作', path: '/test' })

      expect(consoleSpy).toHaveBeenCalledWith('快捷操作:', { title: '测试操作', path: '/test' })
      consoleSpy.mockRestore()
    })
  })
})
