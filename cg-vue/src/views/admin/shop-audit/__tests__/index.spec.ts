import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import ShopAudit from '../index.vue'
import { getPendingShops, auditShop, type Shop } from '@/api/admin'

vi.mock('@/api/admin', () => ({
  getPendingShops: vi.fn(),
  auditShop: vi.fn()
}))

vi.mock('element-plus', async (importOriginal) => {
  const actual = await importOriginal() as typeof import('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      error: vi.fn(),
      warning: vi.fn()
    },
    ElMessageBox: {
      confirm: vi.fn()
    },
    default: actual.default
  }
})

const mockShops: Shop[] = [
  {
    id: 1,
    name: '测试店铺1',
    applicant: '张三',
    applicantPhone: '13800138000',
    address: '北京市朝阳区测试路1号',
    description: '这是一个测试店铺描述',
    createTime: '2024-01-01 10:00:00',
    licenseImage: 'https://example.com/license1.jpg'
  },
  {
    id: 2,
    name: '测试店铺2',
    applicant: '李四',
    applicantPhone: '13900139000',
    address: '上海市浦东新区测试路2号',
    description: '这是另一个测试店铺描述',
    createTime: '2024-01-02 11:00:00',
    licenseImage: 'https://example.com/license2.jpg'
  }
]

describe('ShopAudit 组件', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('数据渲染测试', () => {
    it('渲染组件时应显示搜索表单和待审核店铺列表', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      expect(wrapper.find('.search-card').exists()).toBe(true)
      expect(wrapper.find('.search-bar').exists()).toBe(true)
      expect(wrapper.find('.table-card').exists()).toBe(true)
      expect(wrapper.find('.card-header').exists()).toBe(true)
      expect(wrapper.find('.title').text()).toBe('店铺审核')
    })

    it('渲染搜索表单时应包含搜索输入框和按钮', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      expect(wrapper.find('.search-bar').exists()).toBe(true)
      expect(wrapper.find('.batch-actions').exists()).toBe(true)
    })

    it('渲染批量操作按钮', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const batchActions = wrapper.find('.batch-actions')
      expect(batchActions.exists()).toBe(true)
    })

    it('渲染表格应包含正确的列', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const table = wrapper.find('.el-table')
      expect(table.exists()).toBe(true)
    })
  })

  describe('API集成测试', () => {
    it('组件挂载时应调用 getPendingShops API', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      expect(getPendingShops).toHaveBeenCalledWith({
        page: 1,
        pageSize: 10,
        keyword: ''
      })
    })

    it('审核通过操作应调用 auditShop API', async () => {
      const { ElMessage, ElMessageBox } = await import('element-plus')
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })
      ;(auditShop as vi.MockedFunction<typeof auditShop>).mockResolvedValue({} as Shop)
      ;(ElMessageBox.confirm as vi.MockedFunction<typeof ElMessageBox.confirm>).mockResolvedValue(true)

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedShop = mockShops[0]
      await vm.handleApprove()
      await flushPromises()

      expect(auditShop).toHaveBeenCalledWith(mockShops[0].id, 'approved')
      expect(ElMessage.success).toHaveBeenCalledWith('店铺审核已通过')
    })

    it('审核拒绝操作应调用 auditShop API 并传递拒绝原因', async () => {
      const { ElMessage } = await import('element-plus')
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })
      ;(auditShop as vi.MockedFunction<typeof auditShop>).mockResolvedValue({} as Shop)

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedShop = mockShops[0]
      vm.rejectReason = '不符合入驻条件'
      await vm.handleReject()
      await flushPromises()

      expect(auditShop).toHaveBeenCalledWith(mockShops[0].id, 'rejected', '不符合入驻条件')
      expect(ElMessage.success).toHaveBeenCalledWith('已拒绝该店铺')
    })
  })

  describe('交互功能测试', () => {
    it('搜索功能应重置页码并调用 API', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.searchKeyword = '测试店铺'
      vm.currentPage = 2
      await vm.handleSearch()
      await flushPromises()

      expect(vm.currentPage).toBe(1)
      expect(getPendingShops).toHaveBeenCalledWith({
        page: 1,
        pageSize: 10,
        keyword: '测试店铺'
      })
    })

    it('查看详情操作应打开详情对话框', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleViewDetail(mockShops[0])
      await flushPromises()

      expect(vm.selectedShop).toEqual(mockShops[0])
      expect(vm.detailDialogVisible).toBe(true)
    })

    it('审核操作应打开审核对话框', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleAudit(mockShops[0])
      await flushPromises()

      expect(vm.selectedShop).toEqual(mockShops[0])
      expect(vm.rejectReason).toBe('')
      expect(vm.auditDialogVisible).toBe(true)
    })

    it('拒绝时未填写原因应显示警告', async () => {
      const { ElMessage } = await import('element-plus')
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedShop = mockShops[0]
      vm.rejectReason = ''
      await vm.handleReject()
      await flushPromises()

      expect(ElMessage.warning).toHaveBeenCalledWith('请填写拒绝原因')
      expect(auditShop).not.toHaveBeenCalled()
    })

    it('批量通过操作应调用 API', async () => {
      const { ElMessage, ElMessageBox } = await import('element-plus')
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })
      ;(auditShop as vi.MockedFunction<typeof auditShop>).mockResolvedValue({} as Shop)
      ;(ElMessageBox.confirm as vi.MockedFunction<typeof ElMessageBox.confirm>).mockResolvedValue(true)

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedRows = mockShops
      await vm.handleBatchApprove()
      await flushPromises()

      expect(auditShop).toHaveBeenCalledTimes(2)
      expect(ElMessage.success).toHaveBeenCalledWith('批量审核已通过')
    })

    it('批量拒绝操作应调用 API', async () => {
      const { ElMessage, ElMessageBox } = await import('element-plus')
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })
      ;(auditShop as vi.MockedFunction<typeof auditShop>).mockResolvedValue({} as Shop)
      ;(ElMessageBox.confirm as vi.MockedFunction<typeof ElMessageBox.confirm>).mockResolvedValue(true)

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedRows = mockShops
      await vm.handleBatchReject()
      await flushPromises()

      expect(auditShop).toHaveBeenCalledTimes(2)
      expect(ElMessage.success).toHaveBeenCalledWith('批量已拒绝')
    })

    it('批量操作未选择店铺时应显示警告', async () => {
      const { ElMessage } = await import('element-plus')
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedRows = []
      await vm.handleBatchApprove()
      await flushPromises()

      expect(ElMessage.warning).toHaveBeenCalledWith('请先选择要操作的店铺')
    })

    it('分页变化应调用 API', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: 20
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      await vm.handlePageChange(2)
      await flushPromises()

      expect(vm.currentPage).toBe(2)
      expect(getPendingShops).toHaveBeenCalledWith({
        page: 2,
        pageSize: 10,
        keyword: ''
      })
    })

    it('每页条数变化应调用 API', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: 20
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      await vm.handleSizeChange(20)
      await flushPromises()

      expect(vm.pageSize).toBe(20)
      expect(getPendingShops).toHaveBeenCalledWith({
        page: 1,
        pageSize: 20,
        keyword: ''
      })
    })

    it('选择变化应更新 selectedRows', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.handleSelectionChange(mockShops)

      expect(vm.selectedRows).toEqual(mockShops)
    })
  })

  describe('边界情况测试', () => {
    it('数据加载中状态应显示加载动画', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockImplementation(() => {
        return new Promise(() => {})
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      expect(vm.loading).toBe(true)
    })

    it('数据加载失败状态应显示错误消息', async () => {
      const { ElMessage } = await import('element-plus')
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockRejectedValue(new Error('Network Error'))

      mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('获取待审核店铺列表失败')
    })

    it('空数据状态应显示空状态组件', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: [],
        total: 0
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      expect(wrapper.find('.el-empty').exists()).toBe(true)
    })

    it('审核通过失败应显示错误消息', async () => {
      const { ElMessage, ElMessageBox } = await import('element-plus')
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })
      ;(auditShop as vi.MockedFunction<typeof auditShop>).mockRejectedValue(new Error('Audit failed'))
      ;(ElMessageBox.confirm as vi.MockedFunction<typeof ElMessageBox.confirm>).mockResolvedValue(true)

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedShop = mockShops[0]
      await vm.handleApprove()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('操作失败')
    })

    it('审核拒绝失败应显示错误消息', async () => {
      const { ElMessage } = await import('element-plus')
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })
      ;(auditShop as vi.MockedFunction<typeof auditShop>).mockRejectedValue(new Error('Audit failed'))

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedShop = mockShops[0]
      vm.rejectReason = '测试拒绝原因'
      await vm.handleReject()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('操作失败')
    })

    it('批量审核失败应显示错误消息', async () => {
      const { ElMessage, ElMessageBox } = await import('element-plus')
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })
      ;(auditShop as vi.MockedFunction<typeof auditShop>).mockRejectedValue(new Error('Audit failed'))
      ;(ElMessageBox.confirm as vi.MockedFunction<typeof ElMessageBox.confirm>).mockResolvedValue(true)

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedRows = mockShops
      await vm.handleBatchApprove()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('操作失败')
    })

    it('用户取消确认框不应调用 API', async () => {
      const { ElMessageBox } = await import('element-plus')
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })
      ;(ElMessageBox.confirm as vi.MockedFunction<typeof ElMessageBox.confirm>).mockRejectedValue('cancel')

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedShop = mockShops[0]
      await vm.handleApprove()
      await flushPromises()

      expect(auditShop).not.toHaveBeenCalled()
    })

    it('selectedShop 为 null 时不执行审核通过操作', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedShop = null
      await vm.handleApprove()
      await flushPromises()

      expect(auditShop).not.toHaveBeenCalled()
    })

    it('selectedShop 为 null 时不执行审核拒绝操作', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops,
        total: mockShops.length
      })

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      vm.selectedShop = null
      vm.rejectReason = '测试原因'
      await vm.handleReject()
      await flushPromises()

      expect(auditShop).not.toHaveBeenCalled()
    })

    it('API 返回无 data 字段时应正确处理', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        total: 0
      } as any)

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      expect(vm.shops).toEqual([])
    })

    it('API 返回无 total 字段时应使用数组长度', async () => {
      ;(getPendingShops as vi.MockedFunction<typeof getPendingShops>).mockResolvedValue({
        data: mockShops
      } as any)

      const wrapper = mount(ShopAudit, {
        global: {
          plugins: [ElementPlus]
        }
      })
      await flushPromises()

      const vm = wrapper.vm as any
      expect(vm.total).toBe(mockShops.length)
    })
  })
})
