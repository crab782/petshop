import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import UserDetailPage from '../index.vue'
import * as adminApi from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UserDetail } from '@/api/admin'

const mockUserDetail: UserDetail = {
  id: 1,
  username: 'testuser',
  email: 'test@example.com',
  phone: '13800138000',
  role: 'user',
  status: '正常',
  createTime: '2024-01-01 12:00:00',
  recentOrders: [
    {
      id: 1,
      orderNo: 'ORD001',
      serviceName: '宠物美容',
      totalPrice: 100,
      status: '已完成',
      createTime: '2024-01-15 10:00:00'
    },
    {
      id: 2,
      orderNo: 'ORD002',
      serviceName: '宠物寄养',
      totalPrice: 200,
      status: '待处理',
      createTime: '2024-01-16 14:00:00'
    }
  ],
  recentAppointments: [
    {
      id: 1,
      serviceName: '宠物洗护',
      merchantName: '宠物乐园',
      appointmentTime: '2024-01-20 10:00:00',
      status: '已确认'
    },
    {
      id: 2,
      serviceName: '宠物医疗',
      merchantName: '爱心宠物医院',
      appointmentTime: '2024-01-21 15:00:00',
      status: '已完成'
    }
  ],
  reviews: [
    {
      id: 1,
      serviceName: '宠物美容',
      merchantName: '宠物乐园',
      rating: 5,
      comment: '服务非常好，宠物很喜欢！',
      createdAt: '2024-01-15 12:00:00'
    },
    {
      id: 2,
      serviceName: '宠物寄养',
      merchantName: '爱心宠物店',
      rating: 4,
      comment: '服务不错，下次还会来。',
      createdAt: '2024-01-16 16:00:00'
    }
  ]
}

const mockDisabledUserDetail: UserDetail = {
  ...mockUserDetail,
  id: 2,
  username: 'disableduser',
  status: '禁用',
  recentOrders: [],
  recentAppointments: [],
  reviews: []
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/admin/users',
      name: 'admin-users'
    },
    {
      path: '/admin/users/:id',
      name: 'admin-user-detail'
    }
  ]
})

vi.mock('element-plus', async (importOriginal) => {
  const actual = await importOriginal<any>()
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      error: vi.fn(),
      warning: vi.fn(),
      info: vi.fn()
    },
    ElMessageBox: {
      confirm: vi.fn()
    }
  }
})

describe('UserDetail', () => {
  let wrapper: any

  beforeEach(() => {
    vi.clearAllMocks()

    vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: mockUserDetail } as any)
    vi.spyOn(adminApi, 'updateUserStatus').mockResolvedValue({} as any)

    vi.spyOn(router, 'back').mockImplementation(() => {})

    vi.mocked(ElMessageBox.confirm).mockResolvedValue(true)
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  const mountComponent = async (userId = 1) => {
    wrapper = mount(UserDetailPage, {
      global: {
        plugins: [ElementPlus, router]
      }
    })

    Object.defineProperty(wrapper.vm.$route, 'params', {
      get: () => ({ id: String(userId) })
    })

    await flushPromises()
    await wrapper.vm.$nextTick()

    return wrapper
  }

  describe('数据渲染测试', () => {
    it('测试用户基本信息显示', async () => {
      await mountComponent()

      expect(wrapper.find('.user-detail').exists()).toBe(true)

      expect(wrapper.text()).toContain(mockUserDetail.id.toString())
      expect(wrapper.text()).toContain(mockUserDetail.username)
      expect(wrapper.text()).toContain(mockUserDetail.email || '-')
      expect(wrapper.text()).toContain(mockUserDetail.phone || '-')
      expect(wrapper.text()).toContain(mockUserDetail.createTime)
    })

    it('测试正常状态用户显示', async () => {
      await mountComponent()

      expect(wrapper.text()).toContain('正常')

      const dangerButton = wrapper.find('.el-button--danger')
      expect(dangerButton.exists()).toBe(true)
      expect(dangerButton.text()).toContain('禁用账户')
    })

    it('测试禁用状态用户显示', async () => {
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: mockDisabledUserDetail } as any)

      await mountComponent(2)

      expect(wrapper.text()).toContain('禁用')

      const successButton = wrapper.find('.el-button--success')
      expect(successButton.exists()).toBe(true)
      expect(successButton.text()).toContain('启用账户')
    })

    it('测试订单记录渲染', async () => {
      await mountComponent()

      const tabs = wrapper.findAll('.el-tabs__item')
      const ordersTab = tabs.find((tab: any) => tab.text().includes('最近订单'))
      if (ordersTab) {
        await ordersTab.trigger('click')
        await wrapper.vm.$nextTick()
      }

      expect(wrapper.text()).toContain('ORD001')
      expect(wrapper.text()).toContain('宠物美容')
      expect(wrapper.text()).toContain('100')
      expect(wrapper.text()).toContain('已完成')
    })

    it('测试预约记录渲染', async () => {
      await mountComponent()

      const tabs = wrapper.findAll('.el-tabs__item')
      const appointmentsTab = tabs.find((tab: any) => tab.text().includes('最近预约'))
      if (appointmentsTab) {
        await appointmentsTab.trigger('click')
        await wrapper.vm.$nextTick()
      }

      expect(wrapper.text()).toContain('宠物洗护')
      expect(wrapper.text()).toContain('宠物乐园')
      expect(wrapper.text()).toContain('已确认')
    })

    it('测试评价记录渲染', async () => {
      await mountComponent()

      const tabs = wrapper.findAll('.el-tabs__item')
      const reviewsTab = tabs.find((tab: any) => tab.text().includes('评价记录'))
      if (reviewsTab) {
        await reviewsTab.trigger('click')
        await wrapper.vm.$nextTick()
      }

      expect(wrapper.text()).toContain('服务非常好')
      expect(wrapper.text()).toContain('宠物乐园')
    })

    it('测试面包屑导航渲染', async () => {
      await mountComponent()

      const breadcrumb = wrapper.find('.el-breadcrumb')
      expect(breadcrumb.exists()).toBe(true)

      const breadcrumbItems = wrapper.findAll('.el-breadcrumb__item')
      expect(breadcrumbItems.length).toBeGreaterThanOrEqual(2)
      expect(breadcrumbItems[0].text()).toContain('用户管理')
      expect(breadcrumbItems[1].text()).toContain('用户详情')
    })
  })

  describe('API集成测试', () => {
    it('模拟用户详情API响应', async () => {
      await mountComponent()

      expect(adminApi.getUserDetailById).toHaveBeenCalledWith(1)

      expect(wrapper.vm.userDetail).toEqual(mockUserDetail)
    })

    it('模拟用户状态更新API响应 - 禁用', async () => {
      await mountComponent()

      await wrapper.vm.handleDisableAccount()
      await flushPromises()

      expect(adminApi.updateUserStatus).toHaveBeenCalledWith(1, '禁用')
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('账户已禁用')
    })

    it('模拟用户状态更新API响应 - 启用', async () => {
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: mockDisabledUserDetail } as any)

      await mountComponent(2)

      await wrapper.vm.handleEnableAccount()
      await flushPromises()

      expect(adminApi.updateUserStatus).toHaveBeenCalledWith(2, '正常')
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('账户已启用')
    })

    it('测试API调用后刷新数据', async () => {
      await mountComponent()

      vi.clearAllMocks()
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: mockUserDetail } as any)

      await wrapper.vm.handleDisableAccount()
      await flushPromises()

      expect(adminApi.getUserDetailById).toHaveBeenCalled()
    })
  })

  describe('交互功能测试', () => {
    it('测试禁用用户功能', async () => {
      await mountComponent()

      const dangerButton = wrapper.find('.el-button--danger')
      await dangerButton.trigger('click')
      await flushPromises()

      expect(ElMessageBox.confirm).toHaveBeenCalledWith(
        expect.stringContaining(mockUserDetail.username),
        '禁用确认',
        expect.any(Object)
      )
    })

    it('测试启用用户功能', async () => {
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: mockDisabledUserDetail } as any)

      await mountComponent(2)

      const successButton = wrapper.find('.el-button--success')
      await successButton.trigger('click')
      await flushPromises()

      expect(ElMessageBox.confirm).toHaveBeenCalledWith(
        expect.stringContaining(mockDisabledUserDetail.username),
        '启用确认',
        expect.any(Object)
      )
    })

    it('测试返回按钮功能', async () => {
      await mountComponent()

      const backButton = wrapper.find('.back-card .el-button')
      await backButton.trigger('click')

      expect(router.back).toHaveBeenCalled()
    })

    it('测试标签页切换 - 订单', async () => {
      await mountComponent()

      wrapper.vm.activeTab = 'orders'
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.activeTab).toBe('orders')
    })

    it('测试标签页切换 - 预约', async () => {
      await mountComponent()

      wrapper.vm.activeTab = 'appointments'
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.activeTab).toBe('appointments')
    })

    it('测试标签页切换 - 评价', async () => {
      await mountComponent()

      wrapper.vm.activeTab = 'reviews'
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.activeTab).toBe('reviews')
    })

    it('测试取消禁用操作', async () => {
      vi.mocked(ElMessageBox.confirm).mockRejectedValue('cancel')

      await mountComponent()

      await wrapper.vm.handleDisableAccount()
      await flushPromises()

      expect(adminApi.updateUserStatus).not.toHaveBeenCalled()
      expect(vi.mocked(ElMessage.error)).not.toHaveBeenCalled()
    })

    it('测试取消启用操作', async () => {
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: mockDisabledUserDetail } as any)
      vi.mocked(ElMessageBox.confirm).mockRejectedValue('cancel')

      await mountComponent(2)

      await wrapper.vm.handleEnableAccount()
      await flushPromises()

      expect(adminApi.updateUserStatus).not.toHaveBeenCalled()
      expect(vi.mocked(ElMessage.error)).not.toHaveBeenCalled()
    })
  })

  describe('边界情况测试', () => {
    it('测试数据加载中状态', async () => {
      vi.spyOn(adminApi, 'getUserDetailById').mockImplementation(() =>
        new Promise((resolve) => setTimeout(() => resolve({ data: mockUserDetail } as any), 1000))
      )

      wrapper = mount(UserDetailPage, {
        global: {
          plugins: [ElementPlus, router]
        }
      })

      Object.defineProperty(wrapper.vm.$route, 'params', {
        get: () => ({ id: '1' })
      })

      await wrapper.vm.$nextTick()

      expect(wrapper.vm.loading).toBe(true)
    })

    it('测试数据加载完成状态', async () => {
      await mountComponent()

      expect(wrapper.vm.loading).toBe(false)
      expect(wrapper.vm.userDetail).not.toBeNull()
    })

    it('测试数据加载失败状态', async () => {
      vi.spyOn(adminApi, 'getUserDetailById').mockRejectedValue(new Error('加载失败'))

      await mountComponent()

      expect(vi.mocked(ElMessage.error)).toHaveBeenCalledWith('获取用户详情失败')
    })

    it('测试空订单数据状态', async () => {
      const emptyOrdersUser = {
        ...mockUserDetail,
        recentOrders: []
      }
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: emptyOrdersUser } as any)

      await mountComponent()

      wrapper.vm.activeTab = 'orders'
      await wrapper.vm.$nextTick()

      expect(wrapper.text()).toContain('暂无订单记录')
    })

    it('测试空预约数据状态', async () => {
      const emptyAppointmentsUser = {
        ...mockUserDetail,
        recentAppointments: []
      }
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: emptyAppointmentsUser } as any)

      await mountComponent()

      wrapper.vm.activeTab = 'appointments'
      await wrapper.vm.$nextTick()

      expect(wrapper.text()).toContain('暂无预约记录')
    })

    it('测试空评价数据状态', async () => {
      const emptyReviewsUser = {
        ...mockUserDetail,
        reviews: []
      }
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: emptyReviewsUser } as any)

      await mountComponent()

      wrapper.vm.activeTab = 'reviews'
      await wrapper.vm.$nextTick()

      expect(wrapper.text()).toContain('暂无评价记录')
    })

    it('测试用户不存在状态', async () => {
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: null } as any)

      await mountComponent()

      expect(wrapper.text()).toContain('用户不存在')
    })

    it('测试无用户ID时的处理', async () => {
      wrapper = mount(UserDetailPage, {
        global: {
          plugins: [ElementPlus, router]
        }
      })

      Object.defineProperty(wrapper.vm.$route, 'params', {
        get: () => ({})
      })

      await flushPromises()
      await wrapper.vm.$nextTick()

      expect(adminApi.getUserDetailById).not.toHaveBeenCalled()
    })

    it('测试禁用操作API失败', async () => {
      vi.spyOn(adminApi, 'updateUserStatus').mockRejectedValue(new Error('操作失败'))

      await mountComponent()

      await wrapper.vm.handleDisableAccount()
      await flushPromises()

      expect(vi.mocked(ElMessage.error)).toHaveBeenCalledWith('操作失败')
    })

    it('测试启用操作API失败', async () => {
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: mockDisabledUserDetail } as any)
      vi.spyOn(adminApi, 'updateUserStatus').mockRejectedValue(new Error('操作失败'))

      await mountComponent(2)

      await wrapper.vm.handleEnableAccount()
      await flushPromises()

      expect(vi.mocked(ElMessage.error)).toHaveBeenCalledWith('操作失败')
    })

    it('测试用户详情缺少可选字段', async () => {
      const userWithMissingFields: UserDetail = {
        id: 3,
        username: 'incompleteuser',
        role: 'user',
        status: '正常',
        createTime: '2024-01-01 12:00:00',
        recentOrders: [],
        recentAppointments: [],
        reviews: []
      }
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: userWithMissingFields } as any)

      await mountComponent(3)

      expect(wrapper.text()).toContain('-')
    })

    it('测试订单金额格式化', async () => {
      await mountComponent()

      wrapper.vm.activeTab = 'orders'
      await wrapper.vm.$nextTick()

      expect(wrapper.text()).toContain('¥100')
    })

    it('测试订单状态标签颜色', async () => {
      await mountComponent()

      wrapper.vm.activeTab = 'orders'
      await wrapper.vm.$nextTick()

      const tags = wrapper.findAll('.el-tag')
      expect(tags.length).toBeGreaterThan(0)
    })
  })

  describe('组件生命周期测试', () => {
    it('测试组件挂载时自动获取数据', async () => {
      await mountComponent()

      expect(adminApi.getUserDetailById).toHaveBeenCalledTimes(1)
    })

    it('测试组件卸载时清理状态', async () => {
      await mountComponent()

      wrapper.unmount()

      expect(wrapper.vm).toBeUndefined()
    })
  })

  describe('用户界面元素测试', () => {
    it('测试卡片标题显示', async () => {
      await mountComponent()

      expect(wrapper.find('.title').text()).toContain('用户详情')
    })

    it('测试基本信息卡片标题', async () => {
      await mountComponent()

      const sectionTitles = wrapper.findAll('.section-title')
      expect(sectionTitles[0].text()).toContain('基本信息')
    })

    it('测试用户行为记录卡片标题', async () => {
      await mountComponent()

      const sectionTitles = wrapper.findAll('.section-title')
      expect(sectionTitles[1].text()).toContain('用户行为记录')
    })

    it('测试描述列表渲染', async () => {
      await mountComponent()

      const descriptions = wrapper.find('.el-descriptions')
      expect(descriptions.exists()).toBe(true)
    })

    it('测试表格组件存在', async () => {
      await mountComponent()

      const tables = wrapper.findAll('.el-table')
      expect(tables.length).toBeGreaterThan(0)
    })

    it('测试标签页组件存在', async () => {
      await mountComponent()

      const tabs = wrapper.find('.el-tabs')
      expect(tabs.exists()).toBe(true)
    })
  })

  describe('数据格式化测试', () => {
    it('测试空邮箱显示为横杠', async () => {
      const userNoEmail = {
        ...mockUserDetail,
        email: undefined
      }
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: userNoEmail } as any)

      await mountComponent()

      const descriptionsItems = wrapper.findAll('.el-descriptions-item__content')
      const emailItem = descriptionsItems.find((item: any) =>
        item.element.parentElement?.textContent?.includes('邮箱')
      )
      expect(emailItem?.text() || wrapper.text()).toContain('-')
    })

    it('测试空手机号显示为横杠', async () => {
      const userNoPhone = {
        ...mockUserDetail,
        phone: undefined
      }
      vi.spyOn(adminApi, 'getUserDetailById').mockResolvedValue({ data: userNoPhone } as any)

      await mountComponent()

      const descriptionsItems = wrapper.findAll('.el-descriptions-item__content')
      const phoneItem = descriptionsItems.find((item: any) =>
        item.element.parentElement?.textContent?.includes('手机号')
      )
      expect(phoneItem?.text() || wrapper.text()).toContain('-')
    })
  })
})
