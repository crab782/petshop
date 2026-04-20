import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import MerchantOrders from '../index.vue'
import { ElCard, ElTable, ElTableColumn, ElButton, ElTag, ElDialog, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElPagination, ElDatePicker, ElMessage } from 'element-plus'
import { getMerchantOrders, updateOrderStatus, type Order } from '@/api/merchant'

vi.mock('@/api/merchant', () => ({
  getMerchantOrders: vi.fn(),
  updateOrderStatus: vi.fn(),
}))

vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      warning: vi.fn(),
      error: vi.fn(),
      info: vi.fn(),
    },
  }
})

const mockOrders: Order[] = [
  {
    id: 1,
    userId: 1,
    userName: '张三',
    serviceId: 1,
    serviceName: '宠物洗澡',
    merchantId: 1,
    appointmentTime: '2024-05-01 14:00:00',
    status: 'pending',
    totalPrice: 99.99,
    remark: '请准时到达',
  },
  {
    id: 2,
    userId: 2,
    userName: '李四',
    serviceId: 2,
    serviceName: '宠物美容',
    merchantId: 1,
    appointmentTime: '2024-05-02 10:00:00',
    status: 'confirmed',
    totalPrice: 199.99,
    remark: '需要修剪毛发',
  },
  {
    id: 3,
    userId: 3,
    userName: '王五',
    serviceId: 3,
    serviceName: '宠物寄养',
    merchantId: 1,
    appointmentTime: '2024-05-03 09:00:00',
    status: 'completed',
    totalPrice: 299.99,
  },
  {
    id: 4,
    userId: 4,
    userName: '赵六',
    serviceId: 1,
    serviceName: '宠物洗澡',
    merchantId: 1,
    appointmentTime: '2024-05-04 15:00:00',
    status: 'cancelled',
    totalPrice: 99.99,
  },
  {
    id: 5,
    userId: 1,
    userName: '张三',
    serviceId: 2,
    serviceName: '宠物美容',
    merchantId: 1,
    appointmentTime: '2024-05-05 11:00:00',
    status: 'pending',
    totalPrice: 199.99,
  },
]

const createWrapper = (options = {}) => {
  return mount(MerchantOrders, {
    global: {
      components: {
        ElCard,
        ElTable,
        ElTableColumn,
        ElButton,
        ElTag,
        ElDialog,
        ElForm,
        ElFormItem,
        ElInput,
        ElSelect,
        ElOption,
        ElPagination,
        ElDatePicker,
      },
      stubs: {
        'el-icon': true,
      },
    },
    ...options,
  })
}

describe('MerchantOrders', () => {
  let wrapper: ReturnType<typeof createWrapper>

  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(getMerchantOrders).mockResolvedValue(mockOrders)
  })

  afterEach(() => {
    wrapper?.unmount()
  })

  describe('订单列表渲染', () => {
    it('组件挂载时应该调用 getMerchantOrders API', async () => {
      wrapper = createWrapper()
      await flushPromises()

      expect(getMerchantOrders).toHaveBeenCalledTimes(1)
    })

    it('应该正确渲染订单列表', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const table = wrapper.findComponent(ElTable)
      expect(table.exists()).toBe(true)

      const tableData = table.props('data')
      expect(tableData.length).toBe(5)
    })

    it('应该正确显示订单编号', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const table = wrapper.findComponent(ElTable)
      const tableData = table.props('data')

      expect(tableData[0].id).toBe(1)
      expect(tableData[1].id).toBe(2)
      expect(tableData[2].id).toBe(3)
    })

    it('应该正确显示用户名称', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const table = wrapper.findComponent(ElTable)
      const tableData = table.props('data')

      expect(tableData[0].userName).toBe('张三')
      expect(tableData[1].userName).toBe('李四')
      expect(tableData[2].userName).toBe('王五')
    })

    it('应该正确显示服务名称', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const table = wrapper.findComponent(ElTable)
      const tableData = table.props('data')

      expect(tableData[0].serviceName).toBe('宠物洗澡')
      expect(tableData[1].serviceName).toBe('宠物美容')
      expect(tableData[2].serviceName).toBe('宠物寄养')
    })

    it('应该正确显示订单总价', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const table = wrapper.findComponent(ElTable)
      const tableData = table.props('data')

      expect(tableData[0].totalPrice).toBe(99.99)
      expect(tableData[1].totalPrice).toBe(199.99)
      expect(tableData[2].totalPrice).toBe(299.99)
    })

    it('应该正确显示不同状态的订单', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const table = wrapper.findComponent(ElTable)
      const tableData = table.props('data')

      const pendingOrders = tableData.filter((o: Order) => o.status === 'pending')
      const confirmedOrders = tableData.filter((o: Order) => o.status === 'confirmed')
      const completedOrders = tableData.filter((o: Order) => o.status === 'completed')
      const cancelledOrders = tableData.filter((o: Order) => o.status === 'cancelled')

      expect(pendingOrders.length).toBe(2)
      expect(confirmedOrders.length).toBe(1)
      expect(completedOrders.length).toBe(1)
      expect(cancelledOrders.length).toBe(1)
    })

    it('loading 状态应该正确显示', async () => {
      vi.mocked(getMerchantOrders).mockImplementation(() => new Promise(() => {}))

      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.vm.loading).toBe(true)
    })
  })

  describe('状态流转功能', () => {
    it('pending 状态订单应该显示确认按钮', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      const pendingOrder = mockOrders.find(o => o.status === 'pending')

      expect(vm.getStatusType(pendingOrder!.status)).toBe('warning')
      expect(vm.getStatusText(pendingOrder!.status)).toBe('待处理')
    })

    it('点击确认按钮应该调用 updateOrderStatus 并更新状态为 confirmed', async () => {
      vi.mocked(updateOrderStatus).mockResolvedValue({
        id: 1,
        userId: 1,
        userName: '张三',
        serviceId: 1,
        serviceName: '宠物洗澡',
        merchantId: 1,
        appointmentTime: '2024-05-01 14:00:00',
        status: 'confirmed',
        totalPrice: 99.99,
      })

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      await vm.handleConfirm(mockOrders[0])
      await flushPromises()

      expect(updateOrderStatus).toHaveBeenCalledWith(1, 'confirmed')
      expect(ElMessage.success).toHaveBeenCalledWith('确认成功')
      expect(getMerchantOrders).toHaveBeenCalledTimes(2)
    })

    it('confirmed 状态订单应该显示完成按钮', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      const confirmedOrder = mockOrders.find(o => o.status === 'confirmed')

      expect(vm.getStatusType(confirmedOrder!.status)).toBe('primary')
      expect(vm.getStatusText(confirmedOrder!.status)).toBe('已确认')
    })

    it('点击完成按钮应该调用 updateOrderStatus 并更新状态为 completed', async () => {
      vi.mocked(updateOrderStatus).mockResolvedValue({
        id: 2,
        userId: 2,
        userName: '李四',
        serviceId: 2,
        serviceName: '宠物美容',
        merchantId: 1,
        appointmentTime: '2024-05-02 10:00:00',
        status: 'completed',
        totalPrice: 199.99,
      })

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      await vm.handleComplete(mockOrders[1])
      await flushPromises()

      expect(updateOrderStatus).toHaveBeenCalledWith(2, 'completed')
      expect(ElMessage.success).toHaveBeenCalledWith('完成成功')
    })

    it('completed 状态订单应该显示已完成标签', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      const completedOrder = mockOrders.find(o => o.status === 'completed')

      expect(vm.getStatusType(completedOrder!.status)).toBe('success')
      expect(vm.getStatusText(completedOrder!.status)).toBe('已完成')
    })

    it('cancelled 状态订单应该显示已取消标签', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      const cancelledOrder = mockOrders.find(o => o.status === 'cancelled')

      expect(vm.getStatusType(cancelledOrder!.status)).toBe('info')
      expect(vm.getStatusText(cancelledOrder!.status)).toBe('已取消')
    })

    it('确认订单失败时应该显示错误消息', async () => {
      vi.mocked(updateOrderStatus).mockRejectedValue(new Error('确认失败'))

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      await vm.handleConfirm(mockOrders[0])
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('确认失败')
    })

    it('完成订单失败时应该显示错误消息', async () => {
      vi.mocked(updateOrderStatus).mockRejectedValue(new Error('操作失败'))

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      await vm.handleComplete(mockOrders[1])
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('操作失败')
    })
  })

  describe('搜索和筛选功能', () => {
    it('按订单编号搜索应该正确过滤订单', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.searchKeyword = '2'
      vm.handleSearch()

      expect(vm.filteredOrders.length).toBe(1)
      expect(vm.filteredOrders[0].id).toBe(2)
    })

    it('按用户名称搜索应该正确过滤订单', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.searchKeyword = '张三'
      vm.handleSearch()

      expect(vm.filteredOrders.length).toBe(2)
      expect(vm.filteredOrders.every((o: Order) => o.userName === '张三')).toBe(true)
    })

    it('按服务名称搜索应该正确过滤订单', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.searchKeyword = '宠物洗澡'
      vm.handleSearch()

      expect(vm.filteredOrders.length).toBe(2)
      expect(vm.filteredOrders.every((o: Order) => o.serviceName === '宠物洗澡')).toBe(true)
    })

    it('搜索不区分大小写', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.searchKeyword = 'ZHANG'
      vm.handleSearch()

      expect(vm.filteredOrders.length).toBe(0)
    })

    it('按状态筛选应该正确过滤订单', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.filterStatus = 'pending'
      vm.handleSearch()

      expect(vm.filteredOrders.length).toBe(2)
      expect(vm.filteredOrders.every((o: Order) => o.status === 'pending')).toBe(true)
    })

    it('按 confirmed 状态筛选应该正确过滤订单', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.filterStatus = 'confirmed'
      vm.handleSearch()

      expect(vm.filteredOrders.length).toBe(1)
      expect(vm.filteredOrders[0].status).toBe('confirmed')
    })

    it('按 completed 状态筛选应该正确过滤订单', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.filterStatus = 'completed'
      vm.handleSearch()

      expect(vm.filteredOrders.length).toBe(1)
      expect(vm.filteredOrders[0].status).toBe('completed')
    })

    it('按 cancelled 状态筛选应该正确过滤订单', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.filterStatus = 'cancelled'
      vm.handleSearch()

      expect(vm.filteredOrders.length).toBe(1)
      expect(vm.filteredOrders[0].status).toBe('cancelled')
    })

    it('状态选择 "全部" 应该显示所有订单', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.filterStatus = 'all'
      vm.handleSearch()

      expect(vm.filteredOrders.length).toBe(5)
    })

    it('按日期范围筛选应该正确过滤订单', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      const startDate = new Date('2024-05-01T00:00:00')
      const endDate = new Date('2024-05-03T23:59:59')
      vm.dateRange = [startDate, endDate]

      const filtered = vm.filteredOrders
      expect(filtered.length).toBe(3)
    })

    it('组合筛选应该正确工作', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.filterStatus = 'pending'
      vm.searchKeyword = '张三'
      vm.handleSearch()

      expect(vm.filteredOrders.length).toBe(2)
      expect(vm.filteredOrders.every((o: Order) => o.userName === '张三')).toBe(true)
      expect(vm.filteredOrders.every((o: Order) => o.status === 'pending')).toBe(true)
    })

    it('搜索后应该重置页码为1', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.currentPage = 3
      vm.searchKeyword = '张三'
      vm.handleSearch()

      expect(vm.currentPage).toBe(1)
    })
  })

  describe('订单详情功能', () => {
    it('点击查看详情应该打开弹窗并显示订单信息', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleViewDetail(mockOrders[0])
      await flushPromises()

      expect(vm.dialogVisible).toBe(true)
      expect(vm.currentOrder).toEqual(mockOrders[0])
    })

    it('弹窗应该显示订单编号', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleViewDetail(mockOrders[0])
      await flushPromises()

      expect(vm.currentOrder.id).toBe(1)
    })

    it('弹窗应该显示用户名称', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleViewDetail(mockOrders[0])
      await flushPromises()

      expect(vm.currentOrder.userName).toBe('张三')
    })

    it('弹窗应该显示服务名称', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleViewDetail(mockOrders[0])
      await flushPromises()

      expect(vm.currentOrder.serviceName).toBe('宠物洗澡')
    })

    it('弹窗应该显示订单总价', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleViewDetail(mockOrders[0])
      await flushPromises()

      expect(vm.currentOrder.totalPrice).toBe(99.99)
    })

    it('弹窗应该显示订单状态', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleViewDetail(mockOrders[0])
      await flushPromises()

      expect(vm.currentOrder.status).toBe('pending')
    })

    it('弹窗应该显示备注信息', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleViewDetail(mockOrders[0])
      await flushPromises()

      expect(vm.currentOrder.remark).toBe('请准时到达')
    })

    it('关闭弹窗应该清空当前订单和拒单原因', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleViewDetail(mockOrders[0])
      vm.rejectReason = '测试原因'
      await flushPromises()

      vm.closeDialog()

      expect(vm.dialogVisible).toBe(false)
      expect(vm.currentOrder).toBeNull()
      expect(vm.rejectReason).toBe('')
    })

    it('pending 状态订单在弹窗中应该显示拒单原因输入框', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleViewDetail(mockOrders[0])
      await flushPromises()

      expect(vm.currentOrder.status).toBe('pending')
    })

    it('非 pending 状态订单在弹窗中不应该显示拒单原因输入框', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleViewDetail(mockOrders[2])
      await flushPromises()

      expect(vm.currentOrder.status).toBe('completed')
    })
  })

  describe('订单取消功能', () => {
    it('pending 状态订单应该显示取消按钮', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      const pendingOrder = mockOrders.find(o => o.status === 'pending')

      expect(['pending', 'confirmed']).toContain(pendingOrder!.status)
    })

    it('confirmed 状态订单应该显示取消按钮', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      const confirmedOrder = mockOrders.find(o => o.status === 'confirmed')

      expect(['pending', 'confirmed']).toContain(confirmedOrder!.status)
    })

    it('点击取消按钮应该调用 updateOrderStatus 并更新状态为 cancelled', async () => {
      vi.mocked(updateOrderStatus).mockResolvedValue({
        id: 1,
        userId: 1,
        userName: '张三',
        serviceId: 1,
        serviceName: '宠物洗澡',
        merchantId: 1,
        appointmentTime: '2024-05-01 14:00:00',
        status: 'cancelled',
        totalPrice: 99.99,
      })

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      await vm.handleCancel(mockOrders[0])
      await flushPromises()

      expect(updateOrderStatus).toHaveBeenCalledWith(1, 'cancelled')
      expect(ElMessage.success).toHaveBeenCalledWith('取消成功')
    })

    it('取消订单失败时应该显示错误消息', async () => {
      vi.mocked(updateOrderStatus).mockRejectedValue(new Error('操作失败'))

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      await vm.handleCancel(mockOrders[0])
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('操作失败')
    })
  })

  describe('拒单功能', () => {
    it('拒单时必须填写拒单原因', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.currentOrder = mockOrders[0]
      vm.rejectReason = ''
      await vm.handleReject()
      await flushPromises()

      expect(ElMessage.warning).toHaveBeenCalledWith('请填写拒单原因')
      expect(updateOrderStatus).not.toHaveBeenCalled()
    })

    it('填写拒单原因后应该成功拒单', async () => {
      vi.mocked(updateOrderStatus).mockResolvedValue({
        id: 1,
        userId: 1,
        userName: '张三',
        serviceId: 1,
        serviceName: '宠物洗澡',
        merchantId: 1,
        appointmentTime: '2024-05-01 14:00:00',
        status: 'cancelled',
        totalPrice: 99.99,
      })

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.currentOrder = mockOrders[0]
      vm.rejectReason = '时间冲突'
      await vm.handleReject()
      await flushPromises()

      expect(updateOrderStatus).toHaveBeenCalledWith(1, 'cancelled')
      expect(ElMessage.success).toHaveBeenCalledWith('拒单成功')
      expect(vm.dialogVisible).toBe(false)
      expect(vm.rejectReason).toBe('')
    })

    it('拒单失败时应该显示错误消息', async () => {
      vi.mocked(updateOrderStatus).mockRejectedValue(new Error('拒单失败'))

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.currentOrder = mockOrders[0]
      vm.rejectReason = '时间冲突'
      await vm.handleReject()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('拒单失败')
    })
  })

  describe('确认接单功能', () => {
    it('点击确认接单应该更新订单状态为 confirmed', async () => {
      vi.mocked(updateOrderStatus).mockResolvedValue({
        id: 1,
        userId: 1,
        userName: '张三',
        serviceId: 1,
        serviceName: '宠物洗澡',
        merchantId: 1,
        appointmentTime: '2024-05-01 14:00:00',
        status: 'confirmed',
        totalPrice: 99.99,
      })

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.currentOrder = mockOrders[0]
      await vm.handleAccept()
      await flushPromises()

      expect(updateOrderStatus).toHaveBeenCalledWith(1, 'confirmed')
      expect(ElMessage.success).toHaveBeenCalledWith('确认成功')
      expect(vm.dialogVisible).toBe(false)
    })

    it('确认接单失败时应该显示错误消息', async () => {
      vi.mocked(updateOrderStatus).mockRejectedValue(new Error('确认失败'))

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.currentOrder = mockOrders[0]
      await vm.handleAccept()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('确认失败')
    })
  })

  describe('分页功能', () => {
    it('应该正确计算分页数据', async () => {
      const manyOrders = Array.from({ length: 25 }, (_, i) => ({
        id: i + 1,
        userId: 1,
        userName: `用户${i + 1}`,
        serviceId: 1,
        serviceName: '服务',
        merchantId: 1,
        appointmentTime: '2024-05-01 14:00:00',
        status: 'pending',
        totalPrice: 99.99,
      }))

      vi.mocked(getMerchantOrders).mockResolvedValue(manyOrders)

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      expect(vm.filteredOrders.length).toBe(25)
      expect(vm.paginatedOrders.length).toBe(10)
    })

    it('切换页码应该正确更新当前页', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handlePageChange(2)

      expect(vm.currentPage).toBe(2)
    })

    it('切换每页显示数量应该重置页码为1', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.currentPage = 3
      vm.handleSizeChange(20)

      expect(vm.pageSize).toBe(20)
      expect(vm.currentPage).toBe(1)
    })

    it('分页组件应该正确显示总数', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const pagination = wrapper.findComponent(ElPagination)
      expect(pagination.props('total')).toBe(5)
    })
  })

  describe('状态类型和文本映射', () => {
    it('getStatusType 应该返回正确的类型', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any

      expect(vm.getStatusType('pending')).toBe('warning')
      expect(vm.getStatusType('confirmed')).toBe('primary')
      expect(vm.getStatusType('completed')).toBe('success')
      expect(vm.getStatusType('cancelled')).toBe('info')
      expect(vm.getStatusType('unknown')).toBe('info')
    })

    it('getStatusText 应该返回正确的文本', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any

      expect(vm.getStatusText('pending')).toBe('待处理')
      expect(vm.getStatusText('confirmed')).toBe('已确认')
      expect(vm.getStatusText('completed')).toBe('已完成')
      expect(vm.getStatusText('cancelled')).toBe('已取消')
      expect(vm.getStatusText('unknown')).toBe('unknown')
    })
  })

  describe('API 错误处理', () => {
    it('获取订单列表失败时应该显示错误消息', async () => {
      vi.mocked(getMerchantOrders).mockRejectedValue(new Error('获取失败'))

      wrapper = createWrapper()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('获取订单列表失败')
    })
  })

  describe('handleProcess 方法', () => {
    it('应该打开弹窗并设置当前订单', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleProcess(mockOrders[0])
      await flushPromises()

      expect(vm.dialogVisible).toBe(true)
      expect(vm.currentOrder).toEqual(mockOrders[0])
    })
  })
})
