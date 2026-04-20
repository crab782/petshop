import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import AdminMerchants from '../index.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllMerchants, updateMerchantStatus, deleteMerchant, auditMerchant, type Merchant } from '@/api/admin'

// 模拟 API 函数
vi.mock('@/api/admin', () => ({
  getAllMerchants: vi.fn(),
  updateMerchantStatus: vi.fn(),
  deleteMerchant: vi.fn(),
  auditMerchant: vi.fn()
}))

// 模拟 Element Plus 组件
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn()
  },
  ElMessageBox: {
    confirm: vi.fn()
  }
}))

// 模拟图标
vi.mock('@element-plus/icons-vue', () => ({
  Search: vi.fn(() => 'SearchIcon'),
  Refresh: vi.fn(() => 'RefreshIcon'),
  Check: vi.fn(() => 'CheckIcon'),
  Close: vi.fn(() => 'CloseIcon'),
  View: vi.fn(() => 'ViewIcon'),
  Delete: vi.fn(() => 'DeleteIcon')
}))

// 模拟数据
const mockMerchants: Merchant[] = [
  {
    id: 1,
    name: '测试商家1',
    contactPerson: '张三',
    phone: '13800138001',
    address: '北京市朝阳区',
    status: 'pending',
    createTime: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    name: '测试商家2',
    contactPerson: '李四',
    phone: '13900139002',
    address: '上海市浦东新区',
    status: 'approved',
    createTime: '2024-01-02 11:00:00'
  },
  {
    id: 3,
    name: '测试商家3',
    contactPerson: '王五',
    phone: '13700137003',
    address: '广州市天河区',
    status: 'rejected',
    createTime: '2024-01-03 12:00:00'
  }
]

describe('AdminMerchants 组件', () => {
  let wrapper: any

  beforeEach(() => {
    // 重置所有模拟
    vi.clearAllMocks()
    
    // 模拟 API 响应
    ;(getAllMerchants as any).mockResolvedValue({ data: mockMerchants })
    ;(updateMerchantStatus as any).mockResolvedValue({})
    ;(deleteMerchant as any).mockResolvedValue({})
    ;(auditMerchant as any).mockResolvedValue({})
    
    // 模拟 MessageBox.confirm
    ;(ElMessageBox.confirm as any).mockResolvedValue(true)
  })

  const createWrapper = () => {
    return mount(AdminMerchants, {
      global: {
        stubs: {
          'el-breadcrumb': true,
          'el-breadcrumb-item': true,
          'el-card': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-select': true,
          'el-option': true,
          'el-button': true,
          'el-table': true,
          'el-table-column': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-icon': true
        }
      }
    })
  }

  // 数据渲染测试
  describe('数据渲染测试', () => {
    it('测试商家表格渲染', async () => {
      wrapper = createWrapper()
      
      // 等待组件挂载和数据加载
      await wrapper.vm.$nextTick()
      
      // 验证商家数据是否正确加载
      expect(wrapper.vm.merchants).toEqual(mockMerchants)
      expect(wrapper.vm.filteredMerchants).toEqual(mockMerchants)
    })

    it('测试搜索表单渲染', async () => {
      wrapper = createWrapper()
      
      // 等待组件挂载
      await wrapper.vm.$nextTick()
      
      // 验证搜索表单初始值
      expect(wrapper.vm.searchKeyword).toBe('')
      expect(wrapper.vm.statusFilter).toBe('')
    })

    it('测试批量操作按钮渲染', async () => {
      wrapper = createWrapper()
      
      // 等待组件挂载
      await wrapper.vm.$nextTick()
      
      // 验证批量操作按钮初始状态
      expect(wrapper.vm.selectedIds).toEqual([])
    })
  })

  // API集成测试
  describe('API集成测试', () => {
    it('模拟商家列表API响应', async () => {
      wrapper = createWrapper()
      
      // 等待组件挂载和数据加载
      await wrapper.vm.$nextTick()
      
      // 验证API调用
      expect(getAllMerchants).toHaveBeenCalledTimes(1)
      expect(wrapper.vm.merchants).toEqual(mockMerchants)
    })

    it('模拟商家审核API响应', async () => {
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      // 模拟审核操作
      await wrapper.vm.handleApprove(mockMerchants[0])
      
      // 验证API调用
      expect(auditMerchant).toHaveBeenCalledWith(1, 'approved')
      expect(ElMessage.success).toHaveBeenCalledWith('商家审核已通过')
    })

    it('模拟商家删除API响应', async () => {
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      // 模拟删除操作
      await wrapper.vm.handleDelete(mockMerchants[0])
      
      // 验证API调用
      expect(deleteMerchant).toHaveBeenCalledWith(1)
      expect(ElMessage.success).toHaveBeenCalledWith('删除成功')
    })

    it('模拟批量操作API响应', async () => {
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      // 设置选中的商家
      wrapper.vm.selectedIds = [1, 2]
      
      // 模拟批量审核操作
      await wrapper.vm.handleBatchAudit()
      
      // 验证API调用
      expect(auditMerchant).toHaveBeenCalledWith(1, 'approved')
      expect(ElMessage.success).toHaveBeenCalledWith('批量审核成功')
    })
  })

  // 交互功能测试
  describe('交互功能测试', () => {
    it('测试搜索功能', async () => {
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      // 设置搜索关键词
      wrapper.vm.searchKeyword = '测试商家1'
      await wrapper.vm.handleSearch()
      
      // 验证搜索结果
      expect(wrapper.vm.filteredMerchants).toHaveLength(1)
      expect(wrapper.vm.filteredMerchants[0].name).toBe('测试商家1')
    })

    it('测试筛选功能', async () => {
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      // 设置状态筛选
      wrapper.vm.statusFilter = 'approved'
      await wrapper.vm.handleSearch()
      
      // 验证筛选结果
      expect(wrapper.vm.filteredMerchants).toHaveLength(1)
      expect(wrapper.vm.filteredMerchants[0].status).toBe('approved')
    })

    it('测试批量操作功能', async () => {
      // 测试批量启用
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      wrapper.vm.selectedIds = [1, 2]
      await wrapper.vm.handleBatchEnable()
      expect(updateMerchantStatus).toHaveBeenCalledWith(1, 'approved')
      expect(updateMerchantStatus).toHaveBeenCalledWith(2, 'approved')
      
      // 重置 mock
      vi.clearAllMocks()
      ;(updateMerchantStatus as any).mockResolvedValue({})
      ;(ElMessageBox.confirm as any).mockResolvedValue(true)
      
      // 测试批量禁用
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      wrapper.vm.selectedIds = [1, 2]
      await wrapper.vm.handleBatchDisable()
      expect(updateMerchantStatus).toHaveBeenCalledWith(1, 'disabled')
      expect(updateMerchantStatus).toHaveBeenCalledWith(2, 'disabled')
      
      // 重置 mock
      vi.clearAllMocks()
      ;(deleteMerchant as any).mockResolvedValue({})
      ;(ElMessageBox.confirm as any).mockResolvedValue(true)
      
      // 测试批量删除
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      wrapper.vm.selectedIds = [1, 2]
      await wrapper.vm.handleBatchDelete()
      expect(deleteMerchant).toHaveBeenCalledWith(1)
      expect(deleteMerchant).toHaveBeenCalledWith(2)
    })

    it('测试单个商家操作', async () => {
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      // 测试查看详情
      await wrapper.vm.handleViewDetail(mockMerchants[0])
      expect(wrapper.vm.selectedMerchant).toEqual(mockMerchants[0])
      expect(wrapper.vm.dialogVisible).toBe(true)
      
      // 测试审核通过
      await wrapper.vm.handleApprove(mockMerchants[0])
      expect(auditMerchant).toHaveBeenCalledWith(1, 'approved')
      
      // 测试拒绝
      await wrapper.vm.handleReject(mockMerchants[0])
      expect(wrapper.vm.rejectDialogVisible).toBe(true)
      
      // 测试确认拒绝
      wrapper.vm.rejectReason = '拒绝原因'
      await wrapper.vm.confirmReject()
      expect(auditMerchant).toHaveBeenCalledWith(1, 'rejected', '拒绝原因')
      
      // 测试删除
      await wrapper.vm.handleDelete(mockMerchants[0])
      expect(deleteMerchant).toHaveBeenCalledWith(1)
    })
  })

  // 边界情况测试
  describe('边界情况测试', () => {
    it('测试数据加载中状态', async () => {
      // 模拟加载状态
      ;(getAllMerchants as any).mockImplementation(() => {
        return new Promise(() => {
          // 模拟延迟
        })
      })
      
      wrapper = createWrapper()
      
      // 等待组件挂载
      await wrapper.vm.$nextTick()
      
      // 验证加载状态
      expect(wrapper.vm.loading).toBe(true)
    })

    it('测试数据加载失败状态', async () => {
      // 模拟加载失败
      ;(getAllMerchants as any).mockRejectedValue(new Error('加载失败'))
      
      wrapper = createWrapper()
      
      // 等待组件挂载和错误处理
      await wrapper.vm.$nextTick()
      
      // 验证错误处理
      expect(ElMessage.error).toHaveBeenCalledWith('获取商家列表失败')
      expect(wrapper.vm.loading).toBe(false)
    })

    it('测试空数据状态', async () => {
      // 模拟空数据
      ;(getAllMerchants as any).mockResolvedValue({ data: [] })
      
      wrapper = createWrapper()
      
      // 等待组件挂载
      await wrapper.vm.$nextTick()
      
      // 验证空数据状态
      expect(wrapper.vm.merchants).toEqual([])
      expect(wrapper.vm.filteredMerchants).toEqual([])
      expect(wrapper.vm.pagination.total).toBe(0)
    })

    it('测试批量操作无选择时的提示', async () => {
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      // 测试批量审核无选择
      await wrapper.vm.handleBatchAudit()
      expect(ElMessage.warning).toHaveBeenCalledWith('请先选择要审核的商家')
      
      // 测试批量启用无选择
      await wrapper.vm.handleBatchEnable()
      expect(ElMessage.warning).toHaveBeenCalledWith('请先选择要启用的商家')
      
      // 测试批量禁用无选择
      await wrapper.vm.handleBatchDisable()
      expect(ElMessage.warning).toHaveBeenCalledWith('请先选择要禁用的商家')
      
      // 测试批量删除无选择
      await wrapper.vm.handleBatchDelete()
      expect(ElMessage.warning).toHaveBeenCalledWith('请先选择要删除的商家')
    })

    it('测试批量审核无待审核商家时的提示', async () => {
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      // 设置选中的商家（无待审核状态）
      wrapper.vm.selectedIds = [2, 3] // 已通过和已拒绝
      
      // 测试批量审核
      await wrapper.vm.handleBatchAudit()
      expect(ElMessage.warning).toHaveBeenCalledWith('选中的商家中没有待审核的')
    })

    it('测试拒绝时未填写原因的提示', async () => {
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      // 设置选中的商家
      wrapper.vm.selectedMerchant = mockMerchants[0]
      wrapper.vm.rejectReason = ''
      
      // 测试确认拒绝
      await wrapper.vm.confirmReject()
      expect(ElMessage.warning).toHaveBeenCalledWith('请填写拒绝原因')
    })

    it('测试MessageBox取消操作', async () => {
      // 模拟取消操作
      ;(ElMessageBox.confirm as any).mockRejectedValue('cancel')
      
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      // 测试删除取消
      await wrapper.vm.handleDelete(mockMerchants[0])
      expect(deleteMerchant).not.toHaveBeenCalled()
      expect(ElMessage.error).not.toHaveBeenCalled()
    })
  })
})
