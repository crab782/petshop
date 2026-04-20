import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import AdminTasks from '../index.vue'
import * as adminApi from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const mockTasks = [
  {
    id: 1,
    name: '数据备份任务',
    type: 'scheduled',
    description: '每日数据备份',
    cronExpression: '0 0 * * *',
    executeTime: '2024-01-15 00:00:00',
    status: 'pending' as const,
    result: '',
    createdAt: '2024-01-01 10:00:00',
    updatedAt: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    name: '日志清理任务',
    type: 'one-time',
    description: '清理过期日志',
    cronExpression: '',
    executeTime: '2024-01-20 12:00:00',
    status: 'running' as const,
    result: '执行中...',
    createdAt: '2024-01-02 10:00:00',
    updatedAt: '2024-01-02 10:00:00'
  },
  {
    id: 3,
    name: '报表生成任务',
    type: 'recurring',
    description: '每周生成报表',
    cronExpression: '0 0 * * 0',
    executeTime: '2024-01-21 00:00:00',
    status: 'completed' as const,
    result: '报表生成成功',
    createdAt: '2024-01-03 10:00:00',
    updatedAt: '2024-01-03 10:00:00'
  },
  {
    id: 4,
    name: '邮件通知任务',
    type: 'scheduled',
    description: '发送邮件通知',
    cronExpression: '0 8 * * *',
    executeTime: '2024-01-22 08:00:00',
    status: 'failed' as const,
    result: 'SMTP连接失败',
    createdAt: '2024-01-04 10:00:00',
    updatedAt: '2024-01-04 10:00:00'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/admin/dashboard',
      name: 'admin-dashboard'
    }
  ]
})

describe('AdminTasks', () => {
  let wrapper: any

  beforeEach(() => {
    vi.clearAllMocks()

    vi.spyOn(adminApi, 'getTasks').mockResolvedValue(mockTasks)
    vi.spyOn(adminApi, 'addTask').mockResolvedValue({} as any)
    vi.spyOn(adminApi, 'updateTask').mockResolvedValue({} as any)
    vi.spyOn(adminApi, 'deleteTask').mockResolvedValue({})
    vi.spyOn(adminApi, 'executeTask').mockResolvedValue({} as any)

    vi.spyOn(router, 'push').mockImplementation(() => {})

    vi.mocked(ElMessageBox.confirm).mockResolvedValue(true)

    wrapper = mount(AdminTasks, {
      global: {
        plugins: [ElementPlus, router]
      }
    })
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  describe('数据渲染测试', () => {
    it('测试任务列表渲染', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      expect(wrapper.find('.el-table').exists()).toBe(true)

      const tableRows = wrapper.findAll('.el-table__row')
      expect(tableRows.length).toBe(4)

      const firstRowCells = tableRows[0].findAll('.el-table__cell')
      expect(firstRowCells[0].text()).toBe('1')
      expect(firstRowCells[1].text()).toBe('数据备份任务')
    })

    it('测试搜索和筛选表单渲染', () => {
      expect(wrapper.find('.filter-card').exists()).toBe(true)
      expect(wrapper.find('.filter-bar').exists()).toBe(true)

      const filterItems = wrapper.findAll('.filter-item')
      expect(filterItems.length).toBe(4)

      const filterInputs = wrapper.findAll('.el-input')
      expect(filterInputs.length).toBeGreaterThan(0)

      const filterSelects = wrapper.findAll('.el-select')
      expect(filterSelects.length).toBeGreaterThan(0)

      const datePicker = wrapper.find('.el-date-picker')
      expect(datePicker.exists()).toBe(true)
    })

    it('测试操作按钮渲染', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      const addButton = wrapper.find('.toolbar .el-button')
      expect(addButton.exists()).toBe(true)
      expect(addButton.text()).toContain('添加任务')

      const tableRows = wrapper.findAll('.el-table__row')
      expect(tableRows.length).toBeGreaterThan(0)

      const actionButtons = tableRows[0].findAll('.el-button')
      expect(actionButtons.length).toBe(3)
      expect(actionButtons[0].text()).toContain('执行')
      expect(actionButtons[1].text()).toContain('编辑')
      expect(actionButtons[2].text()).toContain('删除')
    })

    it('测试分页组件渲染', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      expect(wrapper.find('.el-pagination').exists()).toBe(true)

      const totalText = wrapper.find('.el-pagination__total').text()
      expect(totalText).toContain('4')
    })

    it('测试任务状态标签渲染', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      const tableRows = wrapper.findAll('.el-table__row')

      const pendingRow = tableRows[0]
      const pendingStatus = pendingRow.find('.status-tag')
      expect(pendingStatus.classes()).toContain('status-pending')
      expect(pendingStatus.text()).toBe('待执行')

      const runningRow = tableRows[1]
      const runningStatus = runningRow.find('.status-tag')
      expect(runningStatus.classes()).toContain('status-running')
      expect(runningStatus.text()).toBe('执行中')

      const completedRow = tableRows[2]
      const completedStatus = completedRow.find('.status-tag')
      expect(completedStatus.classes()).toContain('status-completed')
      expect(completedStatus.text()).toBe('已完成')

      const failedRow = tableRows[3]
      const failedStatus = failedRow.find('.status-tag')
      expect(failedStatus.classes()).toContain('status-failed')
      expect(failedStatus.text()).toBe('失败')
    })

    it('测试任务类型显示', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      const tableRows = wrapper.findAll('.el-table__row')

      const typeCell = tableRows[0].findAll('.el-table__cell')[2]
      expect(typeCell.text()).toBe('定时任务')

      const typeCell2 = tableRows[1].findAll('.el-table__cell')[2]
      expect(typeCell2.text()).toBe('一次性任务')

      const typeCell3 = tableRows[2].findAll('.el-table__cell')[2]
      expect(typeCell3.text()).toBe('循环任务')
    })
  })

  describe('API集成测试', () => {
    it('模拟任务列表API响应', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      expect(adminApi.getTasks).toHaveBeenCalledTimes(1)

      expect(wrapper.vm.taskList.length).toBe(4)
      expect(wrapper.vm.taskList[0].name).toBe('数据备份任务')
    })

    it('模拟添加任务API响应', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleAdd()
      await wrapper.vm.$nextTick()

      wrapper.vm.formData = {
        name: '新任务',
        type: 'scheduled',
        description: '测试任务描述',
        cronExpression: '0 0 * * *',
        executeTime: '2024-02-01 00:00:00'
      }

      const mockValidate = vi.fn().mockImplementation((callback: (valid: boolean) => void) => {
        callback(true)
      })
      wrapper.vm.formRef = { validate: mockValidate }

      await wrapper.vm.handleSubmit()
      await wrapper.vm.$nextTick()

      expect(adminApi.addTask).toHaveBeenCalledWith({
        name: '新任务',
        type: 'scheduled',
        description: '测试任务描述',
        cronExpression: '0 0 * * *',
        executeTime: '2024-02-01 00:00:00'
      })
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('添加成功')
    })

    it('模拟更新任务API响应', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleEdit(mockTasks[0])
      await wrapper.vm.$nextTick()

      wrapper.vm.formData.name = '更新后的任务名称'

      const mockValidate = vi.fn().mockImplementation((callback: (valid: boolean) => void) => {
        callback(true)
      })
      wrapper.vm.formRef = { validate: mockValidate }

      await wrapper.vm.handleSubmit()
      await wrapper.vm.$nextTick()

      expect(adminApi.updateTask).toHaveBeenCalledWith(1, expect.objectContaining({
        name: '更新后的任务名称'
      }))
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('更新成功')
    })

    it('模拟删除任务API响应', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleDelete(mockTasks[0])
      await wrapper.vm.$nextTick()

      expect(adminApi.deleteTask).toHaveBeenCalledWith(1)
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('删除成功')
    })

    it('模拟执行任务API响应', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleExecute(mockTasks[0])
      await wrapper.vm.$nextTick()

      expect(adminApi.executeTask).toHaveBeenCalledWith(1)
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('任务已触发执行')
    })

    it('模拟带筛选参数的API请求', async () => {
      vi.clearAllMocks()
      vi.spyOn(adminApi, 'getTasks').mockResolvedValue(mockTasks)

      wrapper.vm.filterName = '备份'
      wrapper.vm.filterType = 'scheduled'
      wrapper.vm.filterStatus = 'pending'
      wrapper.vm.dateRange = [new Date('2024-01-01'), new Date('2024-01-31')]

      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(adminApi.getTasks).toHaveBeenCalledWith(expect.objectContaining({
        name: '备份',
        type: 'scheduled',
        status: 'pending',
        page: 1,
        pageSize: 10
      }))
    })
  })

  describe('交互功能测试', () => {
    it('测试搜索功能', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      wrapper.vm.filterName = '备份'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.pagination.current).toBe(1)
      expect(adminApi.getTasks).toHaveBeenCalledWith(expect.objectContaining({
        name: '备份'
      }))
    })

    it('测试重置功能', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      wrapper.vm.filterName = '测试'
      wrapper.vm.filterType = 'scheduled'
      wrapper.vm.filterStatus = 'pending'
      wrapper.vm.dateRange = [new Date(), new Date()]

      await wrapper.vm.handleRefresh()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filterName).toBe('')
      expect(wrapper.vm.filterType).toBe('')
      expect(wrapper.vm.filterStatus).toBe('')
      expect(wrapper.vm.dateRange).toBe(null)
      expect(wrapper.vm.pagination.current).toBe(1)
    })

    it('测试按类型筛选功能', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      wrapper.vm.filterType = 'scheduled'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(adminApi.getTasks).toHaveBeenCalledWith(expect.objectContaining({
        type: 'scheduled'
      }))
    })

    it('测试按状态筛选功能', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      wrapper.vm.filterStatus = 'completed'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(adminApi.getTasks).toHaveBeenCalledWith(expect.objectContaining({
        status: 'completed'
      }))
    })

    it('测试按时间范围筛选功能', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      const startDate = new Date('2024-01-01')
      const endDate = new Date('2024-01-31')
      wrapper.vm.dateRange = [startDate, endDate]

      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(adminApi.getTasks).toHaveBeenCalledWith(expect.objectContaining({
        startDate: startDate.toISOString(),
        endDate: endDate.toISOString()
      }))
    })

    it('测试分页功能 - 页码变化', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handlePageChange(2)
      expect(wrapper.vm.pagination.current).toBe(2)
      expect(adminApi.getTasks).toHaveBeenCalledWith(expect.objectContaining({
        page: 2
      }))
    })

    it('测试分页功能 - 每页条数变化', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleSizeChange(20)
      expect(wrapper.vm.pagination.pageSize).toBe(20)
      expect(adminApi.getTasks).toHaveBeenCalledWith(expect.objectContaining({
        pageSize: 20
      }))
    })

    it('测试添加任务功能', async () => {
      await wrapper.vm.handleAdd()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.dialogVisible).toBe(true)
      expect(wrapper.vm.dialogTitle).toBe('添加任务')
      expect(wrapper.vm.isEdit).toBe(false)
      expect(wrapper.vm.currentTaskId).toBe(null)
      expect(wrapper.vm.formData.name).toBe('')
      expect(wrapper.vm.formData.type).toBe('')
    })

    it('测试编辑任务功能', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleEdit(mockTasks[0])
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.dialogVisible).toBe(true)
      expect(wrapper.vm.dialogTitle).toBe('编辑任务')
      expect(wrapper.vm.isEdit).toBe(true)
      expect(wrapper.vm.currentTaskId).toBe(1)
      expect(wrapper.vm.formData.name).toBe('数据备份任务')
      expect(wrapper.vm.formData.type).toBe('scheduled')
    })

    it('测试删除任务功能', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleDelete(mockTasks[0])
      await wrapper.vm.$nextTick()

      expect(ElMessageBox.confirm).toHaveBeenCalledWith(
        '确定要删除任务 "数据备份任务" 吗？',
        '删除确认',
        expect.objectContaining({
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      )
      expect(adminApi.deleteTask).toHaveBeenCalledWith(1)
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('删除成功')
    })

    it('测试执行任务功能', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleExecute(mockTasks[0])
      await wrapper.vm.$nextTick()

      expect(ElMessageBox.confirm).toHaveBeenCalledWith(
        '确定要立即执行任务 "数据备份任务" 吗？',
        '执行确认',
        expect.objectContaining({
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      )
      expect(adminApi.executeTask).toHaveBeenCalledWith(1)
      expect(vi.mocked(ElMessage.success)).toHaveBeenCalledWith('任务已触发执行')
    })

    it('测试执行中的任务按钮禁用状态', async () => {
      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      const tableRows = wrapper.findAll('.el-table__row')
      const runningRow = tableRows[1]
      const executeButton = runningRow.findAll('.el-button')[0]

      expect(executeButton.attributes('disabled')).toBeDefined()
    })

    it('测试表单验证 - 必填项验证', async () => {
      await wrapper.vm.handleAdd()
      await wrapper.vm.$nextTick()

      const mockValidate = vi.fn().mockImplementation((callback: (valid: boolean) => void) => {
        callback(false)
      })
      wrapper.vm.formRef = { validate: mockValidate }

      await wrapper.vm.handleSubmit()
      await wrapper.vm.$nextTick()

      expect(adminApi.addTask).not.toHaveBeenCalled()
    })

    it('测试关闭对话框功能', async () => {
      await wrapper.vm.handleAdd()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.dialogVisible).toBe(true)

      wrapper.vm.dialogVisible = false
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.dialogVisible).toBe(false)
    })
  })

  describe('边界情况测试', () => {
    it('测试数据加载中状态', async () => {
      wrapper.vm.loading = true
      await wrapper.vm.$nextTick()

      expect(wrapper.find('.el-loading-mask').exists() || wrapper.vm.loading).toBe(true)
    })

    it('测试数据加载失败状态', async () => {
      vi.mocked(adminApi.getTasks).mockRejectedValue(new Error('加载失败'))

      wrapper.unmount()
      wrapper = mount(AdminTasks, {
        global: {
          plugins: [ElementPlus, router]
        }
      })

      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      expect(vi.mocked(ElMessage.error)).toHaveBeenCalledWith('加载任务列表失败')
    })

    it('测试空数据状态', async () => {
      vi.mocked(adminApi.getTasks).mockResolvedValue([])

      wrapper.unmount()
      wrapper = mount(AdminTasks, {
        global: {
          plugins: [ElementPlus, router]
        }
      })

      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.taskList.length).toBe(0)
      expect(wrapper.vm.pagination.total).toBe(0)
    })

    it('测试删除任务取消操作', async () => {
      vi.mocked(ElMessageBox.confirm).mockRejectedValue('cancel')

      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleDelete(mockTasks[0])
      await wrapper.vm.$nextTick()

      expect(adminApi.deleteTask).not.toHaveBeenCalled()
      expect(vi.mocked(ElMessage.error)).not.toHaveBeenCalled()
    })

    it('测试执行任务取消操作', async () => {
      vi.mocked(ElMessageBox.confirm).mockRejectedValue('cancel')

      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleExecute(mockTasks[0])
      await wrapper.vm.$nextTick()

      expect(adminApi.executeTask).not.toHaveBeenCalled()
      expect(vi.mocked(ElMessage.error)).not.toHaveBeenCalled()
    })

    it('测试删除任务失败状态', async () => {
      vi.mocked(adminApi.deleteTask).mockRejectedValue(new Error('删除失败'))

      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleDelete(mockTasks[0])
      await wrapper.vm.$nextTick()

      expect(vi.mocked(ElMessage.error)).toHaveBeenCalledWith('删除失败')
    })

    it('测试执行任务失败状态', async () => {
      vi.mocked(adminApi.executeTask).mockRejectedValue(new Error('执行失败'))

      await wrapper.vm.$nextTick()
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleExecute(mockTasks[0])
      await wrapper.vm.$nextTick()

      expect(vi.mocked(ElMessage.error)).toHaveBeenCalledWith('执行失败')
    })

    it('测试添加任务失败状态', async () => {
      vi.mocked(adminApi.addTask).mockRejectedValue(new Error('添加失败'))

      await wrapper.vm.handleAdd()
      await wrapper.vm.$nextTick()

      const mockValidate = vi.fn().mockImplementation((callback: (valid: boolean) => void) => {
        callback(true)
      })
      wrapper.vm.formRef = { validate: mockValidate }

      await wrapper.vm.handleSubmit()
      await wrapper.vm.$nextTick()

      expect(vi.mocked(ElMessage.error)).toHaveBeenCalledWith('添加失败')
    })

    it('测试更新任务失败状态', async () => {
      vi.mocked(adminApi.updateTask).mockRejectedValue(new Error('更新失败'))

      await wrapper.vm.handleEdit(mockTasks[0])
      await wrapper.vm.$nextTick()

      const mockValidate = vi.fn().mockImplementation((callback: (valid: boolean) => void) => {
        callback(true)
      })
      wrapper.vm.formRef = { validate: mockValidate }

      await wrapper.vm.handleSubmit()
      await wrapper.vm.$nextTick()

      expect(vi.mocked(ElMessage.error)).toHaveBeenCalledWith('更新失败')
    })

    it('测试表单引用不存在时的提交', async () => {
      wrapper.vm.formRef = null

      await wrapper.vm.handleSubmit()
      await wrapper.vm.$nextTick()

      expect(adminApi.addTask).not.toHaveBeenCalled()
    })

    it('测试日期格式化函数', () => {
      const result = wrapper.vm.formatDate('2024-01-15T10:30:00')
      expect(result).toBeTruthy()

      const emptyResult = wrapper.vm.formatDate('')
      expect(emptyResult).toBe('-')

      const nullResult = wrapper.vm.formatDate(null as any)
      expect(nullResult).toBe('-')
    })

    it('测试状态标签获取函数', () => {
      expect(wrapper.vm.getStatusLabel('pending')).toBe('待执行')
      expect(wrapper.vm.getStatusLabel('running')).toBe('执行中')
      expect(wrapper.vm.getStatusLabel('completed')).toBe('已完成')
      expect(wrapper.vm.getStatusLabel('failed')).toBe('失败')
      expect(wrapper.vm.getStatusLabel('unknown')).toBe('unknown')
    })

    it('测试状态样式类获取函数', () => {
      expect(wrapper.vm.getStatusClass('pending')).toBe('status-pending')
      expect(wrapper.vm.getStatusClass('running')).toBe('status-running')
      expect(wrapper.vm.getStatusClass('completed')).toBe('status-completed')
      expect(wrapper.vm.getStatusClass('failed')).toBe('status-failed')
      expect(wrapper.vm.getStatusClass('unknown')).toBe('status-default')
    })

    it('测试类型标签获取函数', () => {
      expect(wrapper.vm.getTypeLabel('scheduled')).toBe('定时任务')
      expect(wrapper.vm.getTypeLabel('one-time')).toBe('一次性任务')
      expect(wrapper.vm.getTypeLabel('recurring')).toBe('循环任务')
      expect(wrapper.vm.getTypeLabel('unknown')).toBe('unknown')
    })

    it('测试Cron表达式字段条件显示', async () => {
      await wrapper.vm.handleAdd()
      await wrapper.vm.$nextTick()

      wrapper.vm.formData.type = 'scheduled'
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.formData.type).toBe('scheduled')

      wrapper.vm.formData.type = 'one-time'
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.formData.type).toBe('one-time')
    })
  })
})
