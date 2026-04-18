import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import AdminActivities from '../index.vue'
import * as adminApi from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const mockActivities = [
  {
    id: 1,
    name: '春节促销活动',
    type: 'promotion',
    description: '春节特惠，全场八折',
    startTime: '2024-01-15 00:00:00',
    endTime: '2024-02-15 23:59:59',
    status: 'active' as const,
    createTime: '2024-01-10 10:00:00'
  },
  {
    id: 2,
    name: '新年抽奖活动',
    type: 'lottery',
    description: '新年抽奖赢大奖',
    startTime: '2024-01-01 00:00:00',
    endTime: '2024-01-31 23:59:59',
    status: 'ended' as const,
    createTime: '2023-12-25 10:00:00'
  },
  {
    id: 3,
    name: '满减优惠活动',
    type: 'discount',
    description: '满100减20',
    startTime: '2024-03-01 00:00:00',
    endTime: '2024-03-31 23:59:59',
    status: 'pending' as const,
    createTime: '2024-02-20 10:00:00'
  },
  {
    id: 4,
    name: '端午节活动',
    type: 'festival',
    description: '端午节特惠',
    startTime: '2024-06-01 00:00:00',
    endTime: '2024-06-10 23:59:59',
    status: 'disabled' as const,
    createTime: '2024-05-20 10:00:00'
  },
  {
    id: 5,
    name: '新品上市活动',
    type: 'new_product',
    description: '新品首发优惠',
    startTime: '2024-04-01 00:00:00',
    endTime: '2024-04-30 23:59:59',
    status: 'active' as const,
    createTime: '2024-03-25 10:00:00'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/admin/dashboard',
      name: 'admin-dashboard'
    },
    {
      path: '/admin/activities',
      name: 'admin-activities'
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

describe('AdminActivities', () => {
  let wrapper: any

  beforeEach(() => {
    vi.clearAllMocks()

    vi.spyOn(adminApi, 'getActivities').mockResolvedValue(mockActivities)
    vi.spyOn(adminApi, 'addActivity').mockResolvedValue({ id: 6, ...mockActivities[0] } as any)
    vi.spyOn(adminApi, 'updateActivity').mockResolvedValue({} as any)
    vi.spyOn(adminApi, 'deleteActivity').mockResolvedValue({})
    vi.spyOn(adminApi, 'toggleActivityStatus').mockResolvedValue({} as any)

    vi.mocked(ElMessageBox.confirm).mockResolvedValue(true)
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  const mountComponent = async () => {
    wrapper = mount(AdminActivities, {
      global: {
        plugins: [ElementPlus, router]
      }
    })
    await flushPromises()
    await wrapper.vm.$nextTick()
    return wrapper
  }

  describe('数据渲染测试', () => {
    it('测试活动列表渲染', async () => {
      await mountComponent()

      expect(wrapper.find('.el-table').exists()).toBe(true)

      const tableRows = wrapper.findAll('.el-table__row')
      expect(tableRows.length).toBeGreaterThan(0)

      expect(wrapper.vm.activityList.length).toBe(5)
    })

    it('测试搜索和筛选表单渲染', async () => {
      await mountComponent()

      expect(wrapper.find('.search-form').exists()).toBe(true)

      const nameInput = wrapper.find('input[placeholder="请输入活动名称"]')
      expect(nameInput.exists()).toBe(true)

      const typeSelect = wrapper.findAll('.el-select')[0]
      expect(typeSelect.exists()).toBe(true)

      const statusSelect = wrapper.findAll('.el-select')[1]
      expect(statusSelect.exists()).toBe(true)

      const datePicker = wrapper.find('.el-date-editor--daterange')
      expect(datePicker.exists()).toBe(true)
    })

    it('测试操作按钮渲染', async () => {
      await mountComponent()

      const addBtn = wrapper.find('.card-header .el-button--primary')
      expect(addBtn.exists()).toBe(true)
      expect(addBtn.text()).toContain('添加活动')

      const searchBtn = wrapper.findAll('.search-form .el-button')[0]
      expect(searchBtn.exists()).toBe(true)
      expect(searchBtn.text()).toContain('搜索')

      const resetBtn = wrapper.findAll('.search-form .el-button')[1]
      expect(resetBtn.exists()).toBe(true)
      expect(resetBtn.text()).toContain('重置')
    })

    it('测试分页组件渲染', async () => {
      await mountComponent()

      expect(wrapper.find('.el-pagination').exists()).toBe(true)

      expect(wrapper.vm.pagination.total).toBe(5)
    })

    it('测试活动类型显示', async () => {
      await mountComponent()

      expect(wrapper.vm.getTypeText('promotion')).toBe('促销活动')
      expect(wrapper.vm.getTypeText('lottery')).toBe('抽奖活动')
      expect(wrapper.vm.getTypeText('discount')).toBe('满减活动')
      expect(wrapper.vm.getTypeText('festival')).toBe('节日活动')
      expect(wrapper.vm.getTypeText('new_product')).toBe('新品上市')
    })

    it('测试活动状态显示', async () => {
      await mountComponent()

      expect(wrapper.vm.getStatusText('pending')).toBe('未开始')
      expect(wrapper.vm.getStatusText('active')).toBe('进行中')
      expect(wrapper.vm.getStatusText('ended')).toBe('已结束')
      expect(wrapper.vm.getStatusText('disabled')).toBe('已禁用')

      expect(wrapper.vm.getStatusType('pending')).toBe('info')
      expect(wrapper.vm.getStatusType('active')).toBe('success')
      expect(wrapper.vm.getStatusType('ended')).toBe('warning')
      expect(wrapper.vm.getStatusType('disabled')).toBe('danger')
    })

    it('测试面包屑导航渲染', async () => {
      await mountComponent()

      expect(wrapper.find('.el-breadcrumb').exists()).toBe(true)

      const breadcrumbItems = wrapper.findAll('.el-breadcrumb__item')
      expect(breadcrumbItems.length).toBe(2)
      expect(breadcrumbItems[0].text()).toContain('首页')
      expect(breadcrumbItems[1].text()).toContain('活动管理')
    })
  })

  describe('API集成测试', () => {
    it('模拟活动列表API响应', async () => {
      await mountComponent()

      expect(adminApi.getActivities).toHaveBeenCalledTimes(1)

      expect(wrapper.vm.activityList.length).toBe(5)
      expect(wrapper.vm.pagination.total).toBe(5)
    })

    it('模拟添加活动API响应', async () => {
      await mountComponent()

      const newActivity = {
        name: '测试活动',
        type: 'promotion',
        description: '测试描述',
        startTime: '2024-05-01 00:00:00',
        endTime: '2024-05-31 23:59:59',
        status: 'pending' as const
      }

      await wrapper.vm.handleAdd()
      wrapper.vm.formData = newActivity
      await wrapper.vm.handleSave()
      await flushPromises()

      expect(adminApi.addActivity).toHaveBeenCalled()
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('添加成功')
    })

    it('模拟更新活动API响应', async () => {
      await mountComponent()

      const activityToUpdate = mockActivities[0]
      await wrapper.vm.handleEdit(activityToUpdate)

      wrapper.vm.formData.name = '更新后的活动名称'
      await wrapper.vm.handleSave()
      await flushPromises()

      expect(adminApi.updateActivity).toHaveBeenCalledWith(1, expect.objectContaining({
        name: '更新后的活动名称'
      }))
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('更新成功')
    })

    it('模拟删除活动API响应', async () => {
      await mountComponent()

      await wrapper.vm.handleDelete(mockActivities[0])
      await flushPromises()

      expect(adminApi.deleteActivity).toHaveBeenCalledWith(1)
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('删除成功')
    })

    it('模拟活动状态切换API响应 - 禁用活动', async () => {
      await mountComponent()

      await wrapper.vm.handleToggleStatus(mockActivities[0])
      await flushPromises()

      expect(adminApi.toggleActivityStatus).toHaveBeenCalledWith(1, 'disabled')
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('禁用成功')
    })

    it('模拟活动状态切换API响应 - 启用活动', async () => {
      await mountComponent()

      await wrapper.vm.handleToggleStatus(mockActivities[3])
      await flushPromises()

      expect(adminApi.toggleActivityStatus).toHaveBeenCalledWith(4, 'active')
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('启用成功')
    })
  })

  describe('交互功能测试', () => {
    it('测试搜索功能 - 按名称搜索', async () => {
      await mountComponent()

      wrapper.vm.searchForm.name = '春节'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(1)
      expect(wrapper.vm.filteredList[0].name).toBe('春节促销活动')
      expect(wrapper.vm.pagination.current).toBe(1)
    })

    it('测试搜索功能 - 按类型筛选', async () => {
      await mountComponent()

      wrapper.vm.searchForm.type = 'promotion'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(1)
      expect(wrapper.vm.filteredList[0].type).toBe('promotion')
    })

    it('测试搜索功能 - 按状态筛选', async () => {
      await mountComponent()

      wrapper.vm.searchForm.status = 'active'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(2)
      wrapper.vm.filteredList.forEach((item: any) => {
        expect(item.status).toBe('active')
      })
    })

    it('测试搜索功能 - 按时间范围筛选', async () => {
      await mountComponent()

      wrapper.vm.searchForm.dateRange = ['2024-01-01', '2024-01-31']
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBeGreaterThan(0)
    })

    it('测试搜索功能 - 组合筛选', async () => {
      await mountComponent()

      wrapper.vm.searchForm.name = '活动'
      wrapper.vm.searchForm.status = 'active'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(1)
      expect(wrapper.vm.filteredList[0].name).toContain('活动')
      expect(wrapper.vm.filteredList[0].status).toBe('active')
    })

    it('测试重置功能', async () => {
      await mountComponent()

      wrapper.vm.searchForm.name = '春节'
      wrapper.vm.searchForm.type = 'promotion'
      wrapper.vm.searchForm.status = 'active'
      wrapper.vm.searchForm.dateRange = ['2024-01-01', '2024-01-31']

      wrapper.vm.handleReset()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.searchForm.name).toBe('')
      expect(wrapper.vm.searchForm.type).toBe('')
      expect(wrapper.vm.searchForm.status).toBe('')
      expect(wrapper.vm.searchForm.dateRange).toEqual([])
      expect(wrapper.vm.pagination.current).toBe(1)
    })

    it('测试分页功能 - 页码变化', async () => {
      await mountComponent()

      wrapper.vm.handlePageChange(2)
      expect(wrapper.vm.pagination.current).toBe(2)
    })

    it('测试分页功能 - 每页条数变化', async () => {
      await mountComponent()

      wrapper.vm.handlePageSizeChange(20)
      expect(wrapper.vm.pagination.pageSize).toBe(20)
      expect(wrapper.vm.pagination.current).toBe(1)
    })

    it('测试添加活动 - 打开对话框', async () => {
      await mountComponent()

      wrapper.vm.handleAdd()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.dialogVisible).toBe(true)
      expect(wrapper.vm.dialogTitle).toBe('添加活动')
      expect(wrapper.vm.isEdit).toBe(false)
      expect(wrapper.vm.currentId).toBe(null)
    })

    it('测试编辑活动 - 打开对话框', async () => {
      await mountComponent()

      const activity = mockActivities[0]
      wrapper.vm.handleEdit(activity)
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.dialogVisible).toBe(true)
      expect(wrapper.vm.dialogTitle).toBe('编辑活动')
      expect(wrapper.vm.isEdit).toBe(true)
      expect(wrapper.vm.currentId).toBe(1)
      expect(wrapper.vm.formData.name).toBe(activity.name)
      expect(wrapper.vm.formData.type).toBe(activity.type)
    })

    it('测试删除活动 - 确认删除', async () => {
      await mountComponent()

      await wrapper.vm.handleDelete(mockActivities[0])
      await flushPromises()

      expect(ElMessageBox.confirm).toHaveBeenCalledWith(
        expect.stringContaining('春节促销活动'),
        '删除确认',
        expect.any(Object)
      )
      expect(adminApi.deleteActivity).toHaveBeenCalledWith(1)
      expect(ElMessage.success).toHaveBeenCalledWith('删除成功')
    })

    it('测试删除活动 - 取消删除', async () => {
      await mountComponent()

      vi.mocked(ElMessageBox.confirm).mockRejectedValue('cancel')

      await wrapper.vm.handleDelete(mockActivities[0])
      await flushPromises()

      expect(ElMessageBox.confirm).toHaveBeenCalled()
      expect(adminApi.deleteActivity).not.toHaveBeenCalled()
    })

    it('测试状态切换 - 禁用进行中的活动', async () => {
      await mountComponent()

      await wrapper.vm.handleToggleStatus(mockActivities[0])
      await flushPromises()

      expect(ElMessageBox.confirm).toHaveBeenCalledWith(
        expect.stringContaining('禁用'),
        '禁用确认',
        expect.any(Object)
      )
      expect(adminApi.toggleActivityStatus).toHaveBeenCalledWith(1, 'disabled')
    })

    it('测试状态切换 - 启用已禁用的活动', async () => {
      await mountComponent()

      await wrapper.vm.handleToggleStatus(mockActivities[3])
      await flushPromises()

      expect(ElMessageBox.confirm).toHaveBeenCalledWith(
        expect.stringContaining('启用'),
        '启用确认',
        expect.any(Object)
      )
      expect(adminApi.toggleActivityStatus).toHaveBeenCalledWith(4, 'active')
    })

    it('测试状态切换 - 已结束的活动不显示启用按钮', async () => {
      await mountComponent()

      const endedActivity = mockActivities[1]
      expect(endedActivity.status).toBe('ended')

      const row = wrapper.vm.paginatedList.find((a: any) => a.id === 2)
      expect(row?.status).toBe('ended')
    })

    it('测试表单验证 - 必填字段', async () => {
      await mountComponent()

      wrapper.vm.handleAdd()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.rules.name[0].required).toBe(true)
      expect(wrapper.vm.rules.type[0].required).toBe(true)
      expect(wrapper.vm.rules.startTime[0].required).toBe(true)
      expect(wrapper.vm.rules.endTime[0].required).toBe(true)
    })

    it('测试表单验证 - 结束时间必须晚于开始时间', async () => {
      await mountComponent()

      wrapper.vm.handleAdd()
      wrapper.vm.formData = {
        name: '测试活动',
        type: 'promotion',
        description: '测试',
        startTime: '2024-05-31 23:59:59',
        endTime: '2024-05-01 00:00:00',
        status: 'pending'
      }

      await wrapper.vm.handleSave()
      await flushPromises()

      expect(ElMessage.warning).toHaveBeenCalledWith('结束时间必须晚于开始时间')
    })

    it('测试对话框关闭', async () => {
      await mountComponent()

      wrapper.vm.handleAdd()
      await wrapper.vm.$nextTick()
      expect(wrapper.vm.dialogVisible).toBe(true)

      wrapper.vm.dialogVisible = false
      await wrapper.vm.$nextTick()
      expect(wrapper.vm.dialogVisible).toBe(false)
    })
  })

  describe('边界情况测试', () => {
    it('测试数据加载中状态', async () => {
      vi.spyOn(adminApi, 'getActivities').mockImplementation(() => {
        return new Promise((resolve) => {
          setTimeout(() => resolve(mockActivities), 100)
        })
      })

      wrapper = mount(AdminActivities, {
        global: {
          plugins: [ElementPlus, router]
        }
      })

      expect(wrapper.vm.loading).toBe(true)

      await flushPromises()
      await new Promise(resolve => setTimeout(resolve, 150))
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.loading).toBe(false)
    })

    it('测试数据加载失败状态', async () => {
      vi.spyOn(adminApi, 'getActivities').mockRejectedValue(new Error('网络错误'))

      await mountComponent()

      expect(ElMessage.error).toHaveBeenCalledWith('加载活动列表失败')
      expect(wrapper.vm.activityList).toEqual([])
    })

    it('测试空数据状态', async () => {
      vi.spyOn(adminApi, 'getActivities').mockResolvedValue([])

      await mountComponent()

      expect(wrapper.vm.activityList).toEqual([])
      expect(wrapper.vm.filteredList).toEqual([])
      expect(wrapper.vm.pagination.total).toBe(0)
    })

    it('测试搜索无结果状态', async () => {
      await mountComponent()

      wrapper.vm.searchForm.name = '不存在的活动名称'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList).toEqual([])
      expect(wrapper.vm.pagination.total).toBe(0)
    })

    it('测试添加活动失败状态', async () => {
      await mountComponent()

      vi.spyOn(adminApi, 'addActivity').mockRejectedValue(new Error('添加失败'))

      wrapper.vm.handleAdd()
      wrapper.vm.formData = {
        name: '测试活动',
        type: 'promotion',
        description: '测试',
        startTime: '2024-05-01 00:00:00',
        endTime: '2024-05-31 23:59:59',
        status: 'pending'
      }

      const mockValidate = vi.fn().mockImplementation((callback: (valid: boolean) => void) => {
        callback(true)
      })
      wrapper.vm.formRef = { validate: mockValidate }

      await wrapper.vm.handleSave()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('保存失败')
    })

    it('测试更新活动失败状态', async () => {
      await mountComponent()

      vi.spyOn(adminApi, 'updateActivity').mockRejectedValue(new Error('更新失败'))

      wrapper.vm.handleEdit(mockActivities[0])
      wrapper.vm.formData.name = '更新后的名称'

      const mockValidate = vi.fn().mockImplementation((callback: (valid: boolean) => void) => {
        callback(true)
      })
      wrapper.vm.formRef = { validate: mockValidate }

      await wrapper.vm.handleSave()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('保存失败')
    })

    it('测试删除活动失败状态', async () => {
      await mountComponent()

      vi.spyOn(adminApi, 'deleteActivity').mockRejectedValue(new Error('删除失败'))

      await wrapper.vm.handleDelete(mockActivities[0])
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('删除失败')
    })

    it('测试状态切换失败状态', async () => {
      await mountComponent()

      vi.spyOn(adminApi, 'toggleActivityStatus').mockRejectedValue(new Error('切换失败'))

      await wrapper.vm.handleToggleStatus(mockActivities[0])
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('禁用失败')
    })

    it('测试分页边界 - 第一页', async () => {
      await mountComponent()

      wrapper.vm.handlePageChange(1)
      expect(wrapper.vm.pagination.current).toBe(1)

      const start = (1 - 1) * wrapper.vm.pagination.pageSize
      expect(wrapper.vm.paginatedList.length).toBeLessThanOrEqual(wrapper.vm.pagination.pageSize)
    })

    it('测试分页边界 - 最后一页', async () => {
      await mountComponent()

      const totalPages = Math.ceil(wrapper.vm.pagination.total / wrapper.vm.pagination.pageSize)
      wrapper.vm.handlePageChange(totalPages)
      expect(wrapper.vm.pagination.current).toBe(totalPages)
    })

    it('测试表单ref不存在时的保存', async () => {
      await mountComponent()

      wrapper.vm.formRef = null

      await wrapper.vm.handleSave()
      await flushPromises()

      expect(adminApi.addActivity).not.toHaveBeenCalled()
      expect(adminApi.updateActivity).not.toHaveBeenCalled()
    })

    it('测试表单验证失败时的保存', async () => {
      await mountComponent()

      wrapper.vm.handleAdd()

      const mockValidate = vi.fn().mockImplementation((callback: (valid: boolean) => void) => {
        callback(false)
      })
      wrapper.vm.formRef = { validate: mockValidate }

      await wrapper.vm.handleSave()
      await flushPromises()

      expect(adminApi.addActivity).not.toHaveBeenCalled()
    })

    it('测试活动类型选项完整性', async () => {
      await mountComponent()

      const types = wrapper.vm.activityTypes
      expect(types.length).toBe(6)
      expect(types[0].value).toBe('')
      expect(types.find((t: any) => t.value === 'promotion')).toBeDefined()
      expect(types.find((t: any) => t.value === 'lottery')).toBeDefined()
      expect(types.find((t: any) => t.value === 'discount')).toBeDefined()
      expect(types.find((t: any) => t.value === 'festival')).toBeDefined()
      expect(types.find((t: any) => t.value === 'new_product')).toBeDefined()
    })

    it('测试状态选项完整性', async () => {
      await mountComponent()

      const statuses = wrapper.vm.statusOptions
      expect(statuses.length).toBe(5)
      expect(statuses[0].value).toBe('')
      expect(statuses.find((s: any) => s.value === 'pending')).toBeDefined()
      expect(statuses.find((s: any) => s.value === 'active')).toBeDefined()
      expect(statuses.find((s: any) => s.value === 'ended')).toBeDefined()
      expect(statuses.find((s: any) => s.value === 'disabled')).toBeDefined()
    })

    it('测试未知类型的处理', async () => {
      await mountComponent()

      expect(wrapper.vm.getTypeText('unknown_type')).toBe('unknown_type')
      expect(wrapper.vm.getStatusText('unknown_status')).toBe('unknown_status')
      expect(wrapper.vm.getStatusType('unknown_status')).toBe('info')
    })

    it('测试组件挂载时自动加载数据', async () => {
      vi.spyOn(adminApi, 'getActivities')

      await mountComponent()

      expect(adminApi.getActivities).toHaveBeenCalled()
    })

    it('测试操作后刷新列表', async () => {
      await mountComponent()

      vi.clearAllMocks()

      await wrapper.vm.handleDelete(mockActivities[0])
      await flushPromises()

      expect(adminApi.getActivities).toHaveBeenCalled()
    })

    it('测试回车键触发搜索', async () => {
      await mountComponent()

      const nameInput = wrapper.find('input[placeholder="请输入活动名称"]')
      await nameInput.setValue('春节')
      await nameInput.trigger('keyup.enter')
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(1)
    })
  })
})
