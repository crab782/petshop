import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { ElMessage } from 'element-plus'
import MerchantProductOrders from '../index.vue'
import {
  getMerchantProductOrders,
  updateProductOrderStatus,
  updateProductOrderLogistics,
  type ProductOrder
} from '@/api/merchant'

vi.mock('@/api/merchant', () => ({
  getMerchantProductOrders: vi.fn(),
  updateProductOrderStatus: vi.fn(),
  updateProductOrderLogistics: vi.fn()
}))

vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      warning: vi.fn(),
      error: vi.fn(),
      info: vi.fn()
    }
  }
})

const createMockProductOrder = (overrides: Partial<ProductOrder> = {}): ProductOrder => ({
  id: 1,
  userId: 1,
  userName: '测试用户',
  productId: 1,
  productName: '测试商品',
  quantity: 2,
  totalPrice: 199.99,
  status: 'pending',
  createTime: '2024-04-20 10:00:00',
  ...overrides
})

const createMockProductOrderList = (count: number): ProductOrder[] => {
  const statuses: Array<'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'> = ['pending', 'paid', 'shipped', 'completed', 'cancelled']
  return Array.from({ length: count }, (_, index) =>
    createMockProductOrder({
      id: index + 1,
      userName: `用户${index + 1}`,
      productName: `商品${index + 1}`,
      quantity: (index % 3) + 1,
      totalPrice: (index + 1) * 100,
      status: statuses[index % 5],
      createTime: `2024-04-${20 + index} 10:00:00`
    })
  )
}

describe('MerchantProductOrders', () => {
  let wrapper: any

  const defaultOrders = createMockProductOrderList(15)

  const mountComponent = async (options = {}) => {
    vi.mocked(getMerchantProductOrders).mockResolvedValue(defaultOrders)

    wrapper = mount(MerchantProductOrders, {
      global: {
        stubs: {
          'el-card': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true,
          'el-tag': true,
          'el-input': true,
          'el-select': true,
          'el-option': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-divider': true,
          'el-icon': true
        }
      },
      ...options
    })

    await flushPromises()
    return wrapper
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  describe('组件渲染', () => {
    it('应该正确渲染商品订单列表', async () => {
      wrapper = await mountComponent()

      expect(getMerchantProductOrders).toHaveBeenCalled()
      expect(wrapper.find('.merchant-product-orders').exists()).toBe(true)
    })

    it('应该显示筛选栏', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.filterStatus).toBeDefined()
      expect(vm.searchKeyword).toBeDefined()
    })

    it('应该显示订单表格', async () => {
      wrapper = await mountComponent()

      expect(wrapper.find('.orders-card').exists()).toBe(true)
    })

    it('应该显示分页组件', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.currentPage).toBeDefined()
      expect(vm.pageSize).toBeDefined()
    })
  })

  describe('订单数据加载', () => {
    it('组件挂载时应该加载订单数据', async () => {
      wrapper = await mountComponent()

      expect(getMerchantProductOrders).toHaveBeenCalledTimes(1)
    })

    it('加载订单失败时应该显示错误消息', async () => {
      vi.mocked(getMerchantProductOrders).mockRejectedValue(new Error('加载失败'))

      wrapper = mount(MerchantProductOrders, {
        global: {
          stubs: {
            'el-card': true,
            'el-table': true,
            'el-table-column': true,
            'el-button': true,
            'el-tag': true,
            'el-input': true,
            'el-select': true,
            'el-option': true,
            'el-pagination': true,
            'el-dialog': true,
            'el-form': true,
            'el-form-item': true,
            'el-divider': true,
            'el-icon': true
          }
        }
      })

      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('获取商品订单列表失败')
    })

    it('加载状态应该正确管理', async () => {
      vi.mocked(getMerchantProductOrders).mockImplementation(() =>
        new Promise(resolve => setTimeout(() => resolve([]), 100))
      )

      wrapper = mount(MerchantProductOrders, {
        global: {
          stubs: {
            'el-card': true,
            'el-table': true,
            'el-table-column': true,
            'el-button': true,
            'el-tag': true,
            'el-input': true,
            'el-select': true,
            'el-option': true,
            'el-pagination': true,
            'el-icon': true
          }
        }
      })

      expect(wrapper.vm.loading).toBe(true)
    })
  })

  describe('订单状态处理', () => {
    it('应该正确显示待支付状态', async () => {
      const pendingOrder = createMockProductOrder({ status: 'pending' })
      vi.mocked(getMerchantProductOrders).mockResolvedValue([pendingOrder])

      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.getStatusText('pending')).toBe('待支付')
      expect(vm.getStatusType('pending')).toBe('warning')
    })

    it('应该正确显示已支付状态', async () => {
      const paidOrder = createMockProductOrder({ status: 'paid' })
      vi.mocked(getMerchantProductOrders).mockResolvedValue([paidOrder])

      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.getStatusText('paid')).toBe('已支付')
      expect(vm.getStatusType('paid')).toBe('primary')
    })

    it('应该正确显示已发货状态', async () => {
      const shippedOrder = createMockProductOrder({ status: 'shipped' })
      vi.mocked(getMerchantProductOrders).mockResolvedValue([shippedOrder])

      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.getStatusText('shipped')).toBe('已发货')
      expect(vm.getStatusType('shipped')).toBe('info')
    })

    it('应该正确显示已完成状态', async () => {
      const completedOrder = createMockProductOrder({ status: 'completed' })
      vi.mocked(getMerchantProductOrders).mockResolvedValue([completedOrder])

      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.getStatusText('completed')).toBe('已完成')
      expect(vm.getStatusType('completed')).toBe('success')
    })

    it('应该正确显示已取消状态', async () => {
      const cancelledOrder = createMockProductOrder({ status: 'cancelled' })
      vi.mocked(getMerchantProductOrders).mockResolvedValue([cancelledOrder])

      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.getStatusText('cancelled')).toBe('已取消')
      expect(vm.getStatusType('cancelled')).toBe('danger')
    })

    it('未知状态应该返回原状态值', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.getStatusText('unknown')).toBe('unknown')
      expect(vm.getStatusType('unknown')).toBe('info')
    })
  })

  describe('搜索和筛选功能', () => {
    it('应该能够按状态筛选订单', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.filterStatus = 'paid'
      await vm.$nextTick()

      expect(vm.filteredOrders.every((order: ProductOrder) => order.status === 'paid')).toBe(true)
    })

    it('应该能够按关键词搜索订单编号', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.searchKeyword = '1'
      await vm.$nextTick()

      expect(vm.filteredOrders.some((order: ProductOrder) =>
        order.id.toString().includes('1')
      )).toBe(true)
    })

    it('应该能够按关键词搜索用户名称', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.searchKeyword = '用户1'
      await vm.$nextTick()

      expect(vm.filteredOrders.some((order: ProductOrder) =>
        order.userName.toLowerCase().includes('用户1'.toLowerCase())
      )).toBe(true)
    })

    it('搜索时应该重置页码', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.currentPage = 3
      vm.handleSearch()

      expect(vm.currentPage).toBe(1)
    })

    it('全部状态时应该显示所有订单', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.filterStatus = 'all'
      vm.searchKeyword = ''
      await vm.$nextTick()

      expect(vm.filteredOrders.length).toBe(defaultOrders.length)
    })
  })

  describe('分页功能', () => {
    it('应该正确计算分页数据', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.currentPage = 1
      vm.pageSize = 10
      await vm.$nextTick()

      expect(vm.paginatedOrders.length).toBeLessThanOrEqual(10)
    })

    it('切换页码应该正确更新', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.handleCurrentChange(2)

      expect(vm.currentPage).toBe(2)
    })

    it('改变每页条数应该重置页码', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.currentPage = 3
      vm.handleSizeChange(20)

      expect(vm.currentPage).toBe(1)
      expect(vm.pageSize).toBe(20)
    })

    it('应该正确计算总数', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.filterStatus = 'all'
      vm.searchKeyword = ''
      await vm.$nextTick()

      const _ = vm.paginatedOrders

      expect(vm.total).toBe(defaultOrders.length)
    })

    it('分页数据应该正确切片', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.currentPage = 2
      vm.pageSize = 5
      await vm.$nextTick()

      const start = (2 - 1) * 5
      const expectedSlice = vm.filteredOrders.slice(start, start + 5)
      expect(vm.paginatedOrders).toEqual(expectedSlice)
    })
  })

  describe('订单详情弹窗', () => {
    it('点击查看详情应该打开弹窗', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      const order = createMockProductOrder()
      vm.handleViewDetail(order)

      expect(vm.dialogVisible).toBe(true)
      expect(vm.currentOrder).toEqual(order)
    })

    it('关闭弹窗应该清空当前订单', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.handleViewDetail(createMockProductOrder())
      vm.closeDialog()

      expect(vm.dialogVisible).toBe(false)
      expect(vm.currentOrder).toBeNull()
    })

    it('详情弹窗应该显示完整订单信息', async () => {
      const order = createMockProductOrder({
        receiverName: '张三',
        phone: '13800138000',
        address: '北京市朝阳区测试路1号',
        logisticsCompany: '顺丰速运',
        trackingNumber: 'SF1234567890'
      })
      vi.mocked(getMerchantProductOrders).mockResolvedValue([order])

      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.handleViewDetail(order)
      await vm.$nextTick()

      expect(vm.currentOrder.receiverName).toBe('张三')
      expect(vm.currentOrder.phone).toBe('13800138000')
      expect(vm.currentOrder.address).toBe('北京市朝阳区测试路1号')
      expect(vm.currentOrder.logisticsCompany).toBe('顺丰速运')
      expect(vm.currentOrder.trackingNumber).toBe('SF1234567890')
    })

    it('详情弹窗应该正确复制订单对象', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      const order = createMockProductOrder()
      vm.handleViewDetail(order)

      expect(vm.currentOrder).not.toBe(order)
      expect(vm.currentOrder).toEqual(order)
    })
  })

  describe('发货功能', () => {
    it('点击发货应该打开物流弹窗', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      const order = createMockProductOrder({ status: 'paid' })
      vm.handleShip(order)

      expect(vm.logisticsDialogVisible).toBe(true)
      expect(vm.currentOrder).toEqual(order)
    })

    it('发货时应该验证物流公司', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.handleShip(createMockProductOrder({ status: 'paid' }))
      vm.logisticsCompany = ''
      vm.trackingNumber = 'SF123456'
      await vm.handleConfirmShip()

      expect(ElMessage.warning).toHaveBeenCalledWith('请选择物流公司')
    })

    it('发货时应该验证物流单号', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.handleShip(createMockProductOrder({ status: 'paid' }))
      vm.logisticsCompany = '顺丰速运'
      vm.trackingNumber = ''
      await vm.handleConfirmShip()

      expect(ElMessage.warning).toHaveBeenCalledWith('请输入物流单号')
    })

    it('发货成功应该更新订单状态', async () => {
      vi.mocked(updateProductOrderLogistics).mockResolvedValue(createMockProductOrder())
      vi.mocked(updateProductOrderStatus).mockResolvedValue(createMockProductOrder({ status: 'shipped' }))

      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.handleShip(createMockProductOrder({ id: 1, status: 'paid' }))
      vm.logisticsCompany = '顺丰速运'
      vm.trackingNumber = 'SF1234567890'
      await vm.handleConfirmShip()
      await flushPromises()

      expect(updateProductOrderLogistics).toHaveBeenCalledWith(1, '顺丰速运', 'SF1234567890')
      expect(updateProductOrderStatus).toHaveBeenCalledWith(1, 'shipped')
      expect(ElMessage.success).toHaveBeenCalledWith('发货成功')
      expect(vm.logisticsDialogVisible).toBe(false)
    })

    it('发货失败应该显示错误消息', async () => {
      vi.mocked(updateProductOrderLogistics).mockRejectedValue(new Error('发货失败'))

      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.handleShip(createMockProductOrder({ status: 'paid' }))
      vm.logisticsCompany = '顺丰速运'
      vm.trackingNumber = 'SF1234567890'
      await vm.handleConfirmShip()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('发货失败')
    })

    it('发货成功后应该重新获取订单列表', async () => {
      vi.mocked(updateProductOrderLogistics).mockResolvedValue(createMockProductOrder())
      vi.mocked(updateProductOrderStatus).mockResolvedValue(createMockProductOrder({ status: 'shipped' }))

      wrapper = await mountComponent()

      const vm = wrapper.vm
      vi.clearAllMocks()

      vm.handleShip(createMockProductOrder({ id: 1, status: 'paid' }))
      vm.logisticsCompany = '顺丰速运'
      vm.trackingNumber = 'SF1234567890'
      await vm.handleConfirmShip()
      await flushPromises()

      expect(getMerchantProductOrders).toHaveBeenCalled()
    })
  })

  describe('物流信息显示', () => {
    it('应该显示物流公司列表', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.logisticsCompanies.length).toBe(8)
      expect(vm.logisticsCompanies[0].value).toBe('顺丰速运')
      expect(vm.logisticsCompanies[1].value).toBe('圆通快递')
      expect(vm.logisticsCompanies[2].value).toBe('中通快递')
    })

    it('已有物流信息时应该预填充', async () => {
      const order = createMockProductOrder({
        status: 'shipped',
        logisticsCompany: '圆通快递',
        trackingNumber: 'YT123456'
      })
      vi.mocked(getMerchantProductOrders).mockResolvedValue([order])

      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.handleShip(order)

      expect(vm.logisticsCompany).toBe('圆通快递')
      expect(vm.trackingNumber).toBe('YT123456')
    })

    it('详情弹窗应该显示物流信息', async () => {
      const order = createMockProductOrder({
        logisticsCompany: '顺丰速运',
        trackingNumber: 'SF123456'
      })
      vi.mocked(getMerchantProductOrders).mockResolvedValue([order])

      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.handleViewDetail(order)

      expect(vm.currentOrder.logisticsCompany).toBe('顺丰速运')
      expect(vm.currentOrder.trackingNumber).toBe('SF123456')
    })

    it('物流公司列表应该包含常用快递', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      const companyValues = vm.logisticsCompanies.map((c: any) => c.value)

      expect(companyValues).toContain('顺丰速运')
      expect(companyValues).toContain('圆通快递')
      expect(companyValues).toContain('中通快递')
      expect(companyValues).toContain('韵达快递')
      expect(companyValues).toContain('申通快递')
      expect(companyValues).toContain('EMS')
      expect(companyValues).toContain('京东物流')
      expect(companyValues).toContain('菜鸟裹裹')
    })
  })

  describe('订单操作', () => {
    it('完成订单应该成功', async () => {
      vi.mocked(updateProductOrderStatus).mockResolvedValue(createMockProductOrder({ status: 'completed' }))

      wrapper = await mountComponent()

      const vm = wrapper.vm
      const order = createMockProductOrder({ status: 'shipped' })
      await vm.handleComplete(order)
      await flushPromises()

      expect(updateProductOrderStatus).toHaveBeenCalledWith(order.id, 'completed')
      expect(ElMessage.success).toHaveBeenCalledWith('订单已完成')
    })

    it('取消订单应该成功', async () => {
      vi.mocked(updateProductOrderStatus).mockResolvedValue(createMockProductOrder({ status: 'cancelled' }))

      wrapper = await mountComponent()

      const vm = wrapper.vm
      const order = createMockProductOrder({ status: 'pending' })
      await vm.handleCancel(order)
      await flushPromises()

      expect(updateProductOrderStatus).toHaveBeenCalledWith(order.id, 'cancelled')
      expect(ElMessage.success).toHaveBeenCalledWith('订单已取消')
    })

    it('操作失败应该显示错误消息', async () => {
      vi.mocked(updateProductOrderStatus).mockRejectedValue(new Error('操作失败'))

      wrapper = await mountComponent()

      const vm = wrapper.vm
      await vm.handleComplete(createMockProductOrder())
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('操作失败')
    })

    it('取消订单失败应该显示错误消息', async () => {
      vi.mocked(updateProductOrderStatus).mockRejectedValue(new Error('取消失败'))

      wrapper = await mountComponent()

      const vm = wrapper.vm
      await vm.handleCancel(createMockProductOrder())
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('操作失败')
    })

    it('操作成功后应该重新获取订单列表', async () => {
      vi.mocked(updateProductOrderStatus).mockResolvedValue(createMockProductOrder({ status: 'completed' }))

      wrapper = await mountComponent()

      const vm = wrapper.vm
      vi.clearAllMocks()

      await vm.handleComplete(createMockProductOrder({ status: 'shipped' }))
      await flushPromises()

      expect(getMerchantProductOrders).toHaveBeenCalled()
    })
  })

  describe('价格格式化', () => {
    it('应该正确格式化价格', async () => {
      const order = createMockProductOrder({ totalPrice: 1234.56 })
      vi.mocked(getMerchantProductOrders).mockResolvedValue([order])

      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.handleViewDetail(order)

      expect(vm.currentOrder.totalPrice.toFixed(2)).toBe('1234.56')
    })

    it('应该正确处理整数价格', async () => {
      const order = createMockProductOrder({ totalPrice: 100 })
      vi.mocked(getMerchantProductOrders).mockResolvedValue([order])

      wrapper = await mountComponent()

      const vm = wrapper.vm
      vm.handleViewDetail(order)

      expect(vm.currentOrder.totalPrice.toFixed(2)).toBe('100.00')
    })
  })

  describe('日期格式化', () => {
    it('应该正确格式化日期', async () => {
      const order = createMockProductOrder({ createTime: '2024-04-20 10:30:00' })
      vi.mocked(getMerchantProductOrders).mockResolvedValue([order])

      wrapper = await mountComponent()

      const date = new Date(order.createTime)
      expect(date.toLocaleString()).toBeTruthy()
    })
  })

  describe('状态选项', () => {
    it('应该包含所有状态选项', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.statusOptions.length).toBe(6)
      expect(vm.statusOptions.map((s: any) => s.value)).toContain('all')
      expect(vm.statusOptions.map((s: any) => s.value)).toContain('pending')
      expect(vm.statusOptions.map((s: any) => s.value)).toContain('paid')
      expect(vm.statusOptions.map((s: any) => s.value)).toContain('shipped')
      expect(vm.statusOptions.map((s: any) => s.value)).toContain('completed')
      expect(vm.statusOptions.map((s: any) => s.value)).toContain('cancelled')
    })

    it('状态选项应该有正确的标签', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      const statusMap: Record<string, string> = {
        all: '全部',
        pending: '待支付',
        paid: '已支付',
        shipped: '已发货',
        completed: '已完成',
        cancelled: '已取消'
      }

      vm.statusOptions.forEach((option: any) => {
        expect(option.label).toBe(statusMap[option.value])
      })
    })
  })

  describe('数据初始化', () => {
    it('初始状态应该正确', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.orders).toEqual(defaultOrders)
      expect(vm.loading).toBe(false)
      expect(vm.filterStatus).toBe('all')
      expect(vm.searchKeyword).toBe('')
      expect(vm.dialogVisible).toBe(false)
      expect(vm.currentOrder).toBeNull()
      expect(vm.currentPage).toBe(1)
      expect(vm.pageSize).toBe(10)

      const _ = vm.paginatedOrders
      expect(vm.total).toBe(defaultOrders.length)
    })

    it('物流弹窗初始状态应该正确', async () => {
      wrapper = await mountComponent()

      const vm = wrapper.vm
      expect(vm.logisticsCompany).toBe('')
      expect(vm.trackingNumber).toBe('')
      expect(vm.logisticsDialogVisible).toBe(false)
    })
  })
})
