import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import AdminProducts from '../index.vue'
import * as adminApi from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

// 模拟 Element Plus 组件
vi.mock('element-plus', async (importOriginal) => {
  const actual = await importOriginal<any>()
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      error: vi.fn(),
      warning: vi.fn()
    },
    ElMessageBox: {
      confirm: vi.fn()
    }
  }
})

// 模拟路由
vi.mock('vue-router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn()
  }))
}))

// 模拟 API 调用
vi.mock('@/api/admin', () => ({
  getAllProducts: vi.fn(),
  updateProductStatus: vi.fn(),
  deleteProduct: vi.fn(),
  batchUpdateProductStatus: vi.fn(),
  batchDeleteProducts: vi.fn(),
  getAllMerchants: vi.fn()
}))

// 测试数据
const mockProducts = [
  {
    id: 1,
    name: '商品1',
    merchantId: 1,
    merchantName: '商家1',
    price: 100,
    stock: 50,
    sales: 10,
    status: 'active' as const,
    createTime: '2023-01-01 10:00:00'
  },
  {
    id: 2,
    name: '商品2',
    merchantId: 2,
    merchantName: '商家2',
    price: 200,
    stock: 5,
    sales: 20,
    status: 'inactive' as const,
    createTime: '2023-01-02 11:00:00'
  },
  {
    id: 3,
    name: '商品3',
    merchantId: 1,
    merchantName: '商家1',
    price: 300,
    stock: 0,
    sales: 30,
    status: 'active' as const,
    createTime: '2023-01-03 12:00:00'
  }
]

const mockMerchants = [
  { id: 1, name: '商家1', status: 'active' as const, createTime: '2023-01-01' },
  { id: 2, name: '商家2', status: 'active' as const, createTime: '2023-01-02' }
]

describe('AdminProducts 组件测试', () => {
  let wrapper: any
  let router: any

  beforeEach(() => {
    // 重置所有模拟
    vi.clearAllMocks()

    // 模拟路由
    router = {
      push: vi.fn()
    }
    ;(useRouter as vi.Mock).mockReturnValue(router)

    // 模拟 API 响应
    ;(adminApi.getAllProducts as vi.Mock).mockResolvedValue(mockProducts)
    ;(adminApi.getAllMerchants as vi.Mock).mockResolvedValue(mockMerchants)
    ;(adminApi.updateProductStatus as vi.Mock).mockResolvedValue({})
    ;(adminApi.deleteProduct as vi.Mock).mockResolvedValue({})
    ;(adminApi.batchUpdateProductStatus as vi.Mock).mockResolvedValue({})
    ;(adminApi.batchDeleteProducts as vi.Mock).mockResolvedValue({})

    // 模拟 ElMessageBox.confirm
    ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)

    // 挂载组件
    wrapper = mount(AdminProducts, {
      global: {
        plugins: [createPinia(), ElementPlus]
      }
    })
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  // 数据渲染测试
  describe('数据渲染测试', () => {
    it('测试商品表格渲染', async () => {
      await wrapper.vm.$nextTick()
      
      // 检查表格是否存在
      const table = wrapper.find('.el-table')
      expect(table.exists()).toBe(true)

      // 检查表格行数量
      const rows = wrapper.findAll('.el-table__row')
      expect(rows.length).toBe(mockProducts.length)

      // 检查第一行数据
      const firstRow = rows[0]
      expect(firstRow.text()).toContain('商品1')
      expect(firstRow.text()).toContain('商家1')
      expect(firstRow.text()).toContain('¥100.00')
      expect(firstRow.text()).toContain('50')
      expect(firstRow.text()).toContain('10')
      expect(firstRow.text()).toContain('上架')
    })

    it('测试搜索和筛选表单渲染', () => {
      // 检查搜索输入框
      const searchInput = wrapper.find('.el-input__inner')
      expect(searchInput.exists()).toBe(true)

      // 检查商家选择器
      const merchantSelect = wrapper.find('.el-select')
      expect(merchantSelect.exists()).toBe(true)

      // 检查库存状态选择器
      const stockStatusSelect = wrapper.find('.el-select')
      expect(stockStatusSelect.exists()).toBe(true)

      // 检查状态选择器
      const statusSelect = wrapper.find('.el-select')
      expect(statusSelect.exists()).toBe(true)

      // 检查搜索和重置按钮
      const searchButton = wrapper.find('.el-button--primary')
      expect(searchButton.exists()).toBe(true)
      expect(searchButton.text()).toContain('搜索')

      const resetButton = wrapper.find('.el-button:not(.el-button--primary)')
      expect(resetButton.exists()).toBe(true)
      expect(resetButton.text()).toContain('重置')
    })

    it('测试批量操作按钮渲染', async () => {
      await wrapper.vm.$nextTick()

      // 直接设置selectedProducts来模拟选择商品
      wrapper.vm.selectedProducts = [mockProducts[0]]
      await wrapper.vm.$nextTick()

      // 检查批量操作按钮是否显示
      const batchActions = wrapper.find('.batch-actions')
      expect(batchActions.exists()).toBe(true)

      // 检查批量操作按钮
      const batchButtons = batchActions.findAll('.el-button')
      expect(batchButtons.length).toBe(3)
      expect(batchButtons[0].text()).toContain('批量上架')
      expect(batchButtons[1].text()).toContain('批量下架')
      expect(batchButtons[2].text()).toContain('批量删除')
    })

    it('测试库存预警样式', async () => {
      await wrapper.vm.$nextTick()

      // 检查有库存的商品
      const stockElements = wrapper.findAll('.el-table__row .el-table__cell:nth-child(6) span')
      expect(stockElements.length).toBe(mockProducts.length)

      // 检查库存为0的商品（应该有 low-stock 类）
      const outOfStockElement = stockElements[2]
      expect(outOfStockElement.classes()).toContain('low-stock')

      // 检查库存低于10的商品（应该有 stock-warning 类）
      const lowStockElement = stockElements[1]
      expect(lowStockElement.classes()).toContain('stock-warning')

      // 检查库存正常的商品（不应该有特殊类）
      const normalStockElement = stockElements[0]
      expect(normalStockElement.classes()).not.toContain('low-stock')
      expect(normalStockElement.classes()).not.toContain('stock-warning')
    })
  })

  // API集成测试
  describe('API集成测试', () => {
    it('模拟商品列表API响应', async () => {
      await wrapper.vm.$nextTick()

      // 验证 API 调用
      expect(adminApi.getAllProducts).toHaveBeenCalled()
      expect(wrapper.vm.productList.length).toBe(mockProducts.length)
    })

    it('模拟商品删除API响应', async () => {
      await wrapper.vm.$nextTick()

      // 模拟删除成功
      ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)
      ;(adminApi.deleteProduct as vi.Mock).mockResolvedValue({})

      // 直接调用handleDelete方法
      await wrapper.vm.handleDelete(mockProducts[0])
      await wrapper.vm.$nextTick()

      // 验证 API 调用
      expect(ElMessageBox.confirm).toHaveBeenCalled()
      expect(adminApi.deleteProduct).toHaveBeenCalledWith(1)
      expect(ElMessage.success).toHaveBeenCalledWith('删除成功')
    })

    it('模拟批量操作API响应', async () => {
      await wrapper.vm.$nextTick()

      // 直接设置selectedProducts来模拟选择商品
      wrapper.vm.selectedProducts = [mockProducts[0]]
      await wrapper.vm.$nextTick()

      // 模拟批量上架
      ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)
      ;(adminApi.batchUpdateProductStatus as vi.Mock).mockResolvedValue({})

      // 直接调用handleBatchEnable方法
      await wrapper.vm.handleBatchEnable()
      await wrapper.vm.$nextTick()

      // 验证 API 调用
      expect(ElMessageBox.confirm).toHaveBeenCalled()
      expect(adminApi.batchUpdateProductStatus).toHaveBeenCalledWith([1], 'active')
      expect(ElMessage.success).toHaveBeenCalledWith('批量上架成功')
    })
  })

  // 交互功能测试
  describe('交互功能测试', () => {
    it('测试搜索功能', async () => {
      await wrapper.vm.$nextTick()

      // 直接设置搜索关键词
      wrapper.vm.searchForm.keyword = '商品1'
      await wrapper.vm.$nextTick()

      // 直接调用handleSearch方法
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      // 验证筛选结果
      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].name).toBe('商品1')
    })

    it('测试筛选功能', async () => {
      await wrapper.vm.$nextTick()

      // 测试按商家筛选
      wrapper.vm.searchForm.merchantId = 2
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].merchantId).toBe(2)

      // 测试按价格区间筛选
      wrapper.vm.searchForm.merchantId = null
      wrapper.vm.searchForm.minPrice = '150'
      wrapper.vm.searchForm.maxPrice = '250'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].price).toBe(200)

      // 测试按库存状态筛选
      wrapper.vm.searchForm.minPrice = ''
      wrapper.vm.searchForm.maxPrice = ''
      wrapper.vm.searchForm.stockStatus = 'out_of_stock'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].stock).toBe(0)

      // 测试按状态筛选
      wrapper.vm.searchForm.stockStatus = ''
      wrapper.vm.searchForm.status = 'inactive'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].status).toBe('inactive')
    })

    it('测试分页功能', async () => {
      await wrapper.vm.$nextTick()

      // 测试页码变化
      await wrapper.vm.handlePageChange(2)
      expect(wrapper.vm.pagination.current).toBe(2)

      // 测试每页大小变化
      await wrapper.vm.handleSizeChange(20)
      expect(wrapper.vm.pagination.pageSize).toBe(20)
      expect(wrapper.vm.pagination.current).toBe(1)
    })

    it('测试批量操作功能', async () => {
      await wrapper.vm.$nextTick()

      // 直接设置selectedProducts来模拟选择商品
      wrapper.vm.selectedProducts = [mockProducts[0]]
      await wrapper.vm.$nextTick()

      // 测试批量上架
      ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)
      ;(adminApi.batchUpdateProductStatus as vi.Mock).mockResolvedValue({})
      await wrapper.vm.handleBatchEnable()
      expect(ElMessage.success).toHaveBeenCalledWith('批量上架成功')

      // 测试批量下架
      ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)
      ;(adminApi.batchUpdateProductStatus as vi.Mock).mockResolvedValue({})
      await wrapper.vm.handleBatchDisable()
      expect(ElMessage.success).toHaveBeenCalledWith('批量下架成功')

      // 测试批量删除
      ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)
      ;(adminApi.batchDeleteProducts as vi.Mock).mockResolvedValue({})
      await wrapper.vm.handleBatchDelete()
      expect(ElMessage.success).toHaveBeenCalledWith('批量删除成功')
    })

    it('测试单个商品操作', async () => {
      await wrapper.vm.$nextTick()

      // 测试编辑操作
      await wrapper.vm.handleEdit(mockProducts[0])
      expect(router.push).toHaveBeenCalledWith({ path: '/admin/product-edit', query: { id: 1 } })

      // 测试删除操作
      ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)
      ;(adminApi.deleteProduct as vi.Mock).mockResolvedValue({})
      await wrapper.vm.handleDelete(mockProducts[0])
      expect(ElMessageBox.confirm).toHaveBeenCalled()
      expect(adminApi.deleteProduct).toHaveBeenCalledWith(1)
    })
  })

  // 边界情况测试
  describe('边界情况测试', () => {
    it('测试数据加载中状态', async () => {
      // 模拟加载中
      wrapper.vm.loading = true
      await wrapper.vm.$nextTick()

      // 检查加载状态
      const loadingElement = wrapper.find('.el-loading-mask')
      expect(loadingElement.exists()).toBe(true)
    })

    it('测试数据加载失败状态', async () => {
      // 模拟加载失败
      ;(adminApi.getAllProducts as vi.Mock).mockRejectedValue(new Error('加载失败'))
      
      // 重新挂载组件
      wrapper.unmount()
      wrapper = mount(AdminProducts, {
        global: {
          plugins: [createPinia(), ElementPlus]
        }
      })

      await wrapper.vm.$nextTick()

      // 验证错误消息
      expect(ElMessage.error).toHaveBeenCalledWith('加载商品列表失败')
    })

    it('测试空数据状态', async () => {
      // 模拟空数据
      ;(adminApi.getAllProducts as vi.Mock).mockResolvedValue([])
      
      // 重新挂载组件
      wrapper.unmount()
      wrapper = mount(AdminProducts, {
        global: {
          plugins: [createPinia(), ElementPlus]
        }
      })

      await wrapper.vm.$nextTick()

      // 验证空数据状态
      expect(wrapper.vm.productList.length).toBe(0)
      expect(wrapper.vm.filteredProducts.length).toBe(0)
    })
  })
})
