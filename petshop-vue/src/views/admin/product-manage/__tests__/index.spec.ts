import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import ProductManage from '../index.vue'
import * as adminApi from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

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

vi.mock('vue-router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn()
  }))
}))

vi.mock('@/api/admin', () => ({
  getAllProductsForAdmin: vi.fn(),
  updateProductStatus: vi.fn(),
  deleteProduct: vi.fn(),
  getAllMerchants: vi.fn()
}))

const mockProducts = [
  {
    id: 1,
    name: '测试商品1',
    merchantId: 1,
    merchantName: '测试商家1',
    price: 99.99,
    stock: 50,
    sales: 10,
    status: 'active' as const,
    description: '这是一个测试商品描述',
    image: 'https://example.com/image1.jpg',
    createTime: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    name: '测试商品2',
    merchantId: 2,
    merchantName: '测试商家2',
    price: 199.99,
    stock: 5,
    sales: 20,
    status: 'inactive' as const,
    description: '这是另一个测试商品描述',
    image: 'https://example.com/image2.jpg',
    createTime: '2024-01-02 11:00:00'
  },
  {
    id: 3,
    name: '测试商品3',
    merchantId: 1,
    merchantName: '测试商家1',
    price: 299.99,
    stock: 0,
    sales: 30,
    status: 'active' as const,
    description: '缺货商品',
    image: 'https://example.com/image3.jpg',
    createTime: '2024-01-03 12:00:00'
  }
]

const mockMerchants = [
  { id: 1, name: '测试商家1', status: 'approved' as const, createTime: '2024-01-01' },
  { id: 2, name: '测试商家2', status: 'approved' as const, createTime: '2024-01-02' }
]

describe('ProductManage 组件测试', () => {
  let wrapper: any

  beforeEach(() => {
    vi.clearAllMocks()

    ;(adminApi.getAllProductsForAdmin as vi.Mock).mockResolvedValue(mockProducts)
    ;(adminApi.getAllMerchants as vi.Mock).mockResolvedValue(mockMerchants)
    ;(adminApi.updateProductStatus as vi.Mock).mockResolvedValue({})
    ;(adminApi.deleteProduct as vi.Mock).mockResolvedValue({})
    ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  const mountComponent = async () => {
    wrapper = mount(ProductManage, {
      global: {
        plugins: [createPinia(), ElementPlus]
      }
    })
    await flushPromises()
    return wrapper
  }

  describe('数据渲染测试', () => {
    it('测试商品详情显示', async () => {
      await mountComponent()

      expect(wrapper.vm.productList.length).toBe(mockProducts.length)
      expect(wrapper.vm.filteredProducts.length).toBe(mockProducts.length)
    })

    it('测试商家信息显示', async () => {
      await mountComponent()

      expect(wrapper.vm.merchantList.length).toBe(mockMerchants.length)
      expect(wrapper.vm.merchantList[0].name).toBe('测试商家1')
      expect(wrapper.vm.merchantList[1].name).toBe('测试商家2')
    })

    it('测试商品表格渲染', async () => {
      await mountComponent()

      const table = wrapper.find('.el-table')
      expect(table.exists()).toBe(true)

      const rows = wrapper.findAll('.el-table__row')
      expect(rows.length).toBe(mockProducts.length)

      const firstRow = rows[0]
      expect(firstRow.text()).toContain('测试商品1')
      expect(firstRow.text()).toContain('测试商家1')
      expect(firstRow.text()).toContain('99.99')
    })

    it('测试价格格式化显示', async () => {
      await mountComponent()

      const priceElements = wrapper.findAll('.price')
      expect(priceElements.length).toBeGreaterThan(0)
      expect(priceElements[0].text()).toContain('¥')
      expect(priceElements[0].text()).toContain('99.99')
    })

    it('测试库存状态样式', async () => {
      await mountComponent()

      const stockElements = wrapper.findAll('.el-table__row .el-table__cell')

      const outOfStockRow = wrapper.findAll('.el-table__row')[2]
      expect(outOfStockRow.text()).toContain('0')

      const lowStockRow = wrapper.findAll('.el-table__row')[1]
      expect(lowStockRow.text()).toContain('5')
    })

    it('测试商品详情弹窗显示', async () => {
      await mountComponent()

      await wrapper.vm.handleView(mockProducts[0])
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.detailDialogVisible).toBe(true)
      expect(wrapper.vm.currentProduct).toEqual(mockProducts[0])
    })

    it('测试商品详情弹窗内容', async () => {
      await mountComponent()

      wrapper.vm.currentProduct = mockProducts[0]
      wrapper.vm.detailDialogVisible = true
      await wrapper.vm.$nextTick()

      const dialog = wrapper.find('.el-dialog')
      if (dialog.exists()) {
        expect(dialog.text()).toContain('测试商品1')
        expect(dialog.text()).toContain('测试商家1')
        expect(dialog.text()).toContain('99.99')
        expect(dialog.text()).toContain('50')
        expect(dialog.text()).toContain('这是一个测试商品描述')
      }
    })
  })

  describe('API集成测试', () => {
    it('模拟商品详情API响应', async () => {
      await mountComponent()

      expect(adminApi.getAllProductsForAdmin).toHaveBeenCalled()
      expect(wrapper.vm.productList).toEqual(mockProducts)
    })

    it('模拟商家列表API响应', async () => {
      await mountComponent()

      expect(adminApi.getAllMerchants).toHaveBeenCalled()
      expect(wrapper.vm.merchantList).toEqual(mockMerchants)
    })

    it('模拟商品状态更新API响应', async () => {
      await mountComponent()

      await wrapper.vm.handleStatusChange(mockProducts[0], false)
      await wrapper.vm.$nextTick()

      expect(adminApi.updateProductStatus).toHaveBeenCalledWith(1, 'inactive')
      expect(ElMessage.success).toHaveBeenCalledWith('商品已下架')
    })

    it('模拟商品删除API响应', async () => {
      await mountComponent()

      await wrapper.vm.handleDelete(mockProducts[0])
      await flushPromises()

      expect(ElMessageBox.confirm).toHaveBeenCalled()
      expect(adminApi.deleteProduct).toHaveBeenCalledWith(1)
      expect(ElMessage.success).toHaveBeenCalledWith('删除成功')
    })

    it('模拟API调用失败处理', async () => {
      ;(adminApi.getAllProductsForAdmin as vi.Mock).mockRejectedValue(new Error('API Error'))

      await mountComponent()

      expect(ElMessage.error).toHaveBeenCalledWith('获取商品列表失败')
    })
  })

  describe('交互功能测试', () => {
    it('测试编辑商品功能', async () => {
      await mountComponent()

      await wrapper.vm.handleEdit(mockProducts[0])
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.editDialogVisible).toBe(true)
      expect(wrapper.vm.currentProduct).toEqual(mockProducts[0])
      expect(wrapper.vm.editForm.name).toBe('测试商品1')
      expect(wrapper.vm.editForm.price).toBe(99.99)
      expect(wrapper.vm.editForm.stock).toBe(50)
      expect(wrapper.vm.editForm.description).toBe('这是一个测试商品描述')
    })

    it('测试状态切换功能 - 上架', async () => {
      await mountComponent()

      await wrapper.vm.handleStatusChange(mockProducts[1], true)
      await wrapper.vm.$nextTick()

      expect(adminApi.updateProductStatus).toHaveBeenCalledWith(2, 'active')
      expect(ElMessage.success).toHaveBeenCalledWith('商品已上架')
    })

    it('测试状态切换功能 - 下架', async () => {
      await mountComponent()

      await wrapper.vm.handleStatusChange(mockProducts[0], false)
      await wrapper.vm.$nextTick()

      expect(adminApi.updateProductStatus).toHaveBeenCalledWith(1, 'inactive')
      expect(ElMessage.success).toHaveBeenCalledWith('商品已下架')
    })

    it('测试状态切换失败处理', async () => {
      ;(adminApi.updateProductStatus as vi.Mock).mockRejectedValue(new Error('Update failed'))

      await mountComponent()

      await wrapper.vm.handleStatusChange(mockProducts[0], false)
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('更新状态失败')
    })

    it('测试删除商品功能 - 确认删除', async () => {
      await mountComponent()

      await wrapper.vm.handleDelete(mockProducts[0])
      await flushPromises()

      expect(ElMessageBox.confirm).toHaveBeenCalledWith(
        '确定要删除该商品吗？此操作不可恢复！',
        '提示',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      expect(adminApi.deleteProduct).toHaveBeenCalledWith(1)
      expect(ElMessage.success).toHaveBeenCalledWith('删除成功')
    })

    it('测试删除商品功能 - 取消删除', async () => {
      ;(ElMessageBox.confirm as vi.Mock).mockRejectedValue('cancel')

      await mountComponent()

      await wrapper.vm.handleDelete(mockProducts[0])
      await flushPromises()

      expect(adminApi.deleteProduct).not.toHaveBeenCalled()
      expect(ElMessage.success).not.toHaveBeenCalled()
    })

    it('测试删除商品功能 - 删除失败', async () => {
      ;(adminApi.deleteProduct as vi.Mock).mockRejectedValue(new Error('Delete failed'))

      await mountComponent()

      await wrapper.vm.handleDelete(mockProducts[0])
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('删除失败')
    })

    it('测试搜索功能', async () => {
      await mountComponent()

      wrapper.vm.searchForm.keyword = '商品1'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.pagination.current).toBe(1)
      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].name).toBe('测试商品1')
    })

    it('测试按商家筛选功能', async () => {
      await mountComponent()

      wrapper.vm.searchForm.merchantId = 2
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].merchantId).toBe(2)
    })

    it('测试按价格区间筛选功能', async () => {
      await mountComponent()

      wrapper.vm.searchForm.minPrice = '100'
      wrapper.vm.searchForm.maxPrice = '200'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].price).toBe(199.99)
    })

    it('测试按库存状态筛选 - 有货', async () => {
      await mountComponent()

      wrapper.vm.searchForm.stockStatus = 'in_stock'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      const result = wrapper.vm.filteredProducts
      expect(result.every((p: any) => p.stock > 0)).toBe(true)
    })

    it('测试按库存状态筛选 - 缺货', async () => {
      await mountComponent()

      wrapper.vm.searchForm.stockStatus = 'out_of_stock'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      const result = wrapper.vm.filteredProducts
      expect(result.every((p: any) => p.stock <= 0)).toBe(true)
    })

    it('测试按状态筛选功能', async () => {
      await mountComponent()

      wrapper.vm.searchForm.status = 'inactive'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].status).toBe('inactive')
    })

    it('测试重置搜索功能', async () => {
      await mountComponent()

      wrapper.vm.searchForm.keyword = '测试'
      wrapper.vm.searchForm.merchantId = 1
      wrapper.vm.searchForm.minPrice = '100'
      wrapper.vm.searchForm.maxPrice = '200'
      wrapper.vm.searchForm.stockStatus = 'in_stock'
      wrapper.vm.searchForm.status = 'active'

      wrapper.vm.handleReset()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.searchForm.keyword).toBe('')
      expect(wrapper.vm.searchForm.merchantId).toBeNull()
      expect(wrapper.vm.searchForm.minPrice).toBe('')
      expect(wrapper.vm.searchForm.maxPrice).toBe('')
      expect(wrapper.vm.searchForm.stockStatus).toBe('')
      expect(wrapper.vm.searchForm.status).toBe('')
      expect(wrapper.vm.pagination.current).toBe(1)
    })

    it('测试分页功能 - 页码变化', async () => {
      await mountComponent()

      wrapper.vm.handlePageChange(2)
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.pagination.current).toBe(2)
    })

    it('测试分页功能 - 每页大小变化', async () => {
      await mountComponent()

      wrapper.vm.handleSizeChange(20)
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.pagination.pageSize).toBe(20)
      expect(wrapper.vm.pagination.current).toBe(1)
    })

    it('测试关闭详情弹窗', async () => {
      await mountComponent()

      wrapper.vm.detailDialogVisible = true
      await wrapper.vm.$nextTick()

      wrapper.vm.detailDialogVisible = false
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.detailDialogVisible).toBe(false)
    })

    it('测试关闭编辑弹窗', async () => {
      await mountComponent()

      wrapper.vm.editDialogVisible = true
      await wrapper.vm.$nextTick()

      wrapper.vm.editDialogVisible = false
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.editDialogVisible).toBe(false)
    })
  })

  describe('边界情况测试', () => {
    it('测试数据加载中状态', async () => {
      let resolveFn: () => void
      ;(adminApi.getAllProductsForAdmin as vi.Mock).mockImplementation(() => {
        return new Promise((resolve) => {
          resolveFn = () => resolve(mockProducts)
        })
      })

      const mountPromise = mountComponent()

      expect(wrapper.vm.loading).toBe(true)

      resolveFn!()
      await mountPromise

      expect(wrapper.vm.loading).toBe(false)
    })

    it('测试数据加载失败状态', async () => {
      ;(adminApi.getAllProductsForAdmin as vi.Mock).mockRejectedValue(new Error('Network Error'))

      await mountComponent()

      expect(wrapper.vm.productList).toEqual([])
      expect(ElMessage.error).toHaveBeenCalledWith('获取商品列表失败')
    })

    it('测试商家列表加载失败', async () => {
      ;(adminApi.getAllMerchants as vi.Mock).mockRejectedValue(new Error('Network Error'))

      await mountComponent()

      expect(wrapper.vm.merchantList).toEqual([])
    })

    it('测试空数据状态', async () => {
      ;(adminApi.getAllProductsForAdmin as vi.Mock).mockResolvedValue([])

      await mountComponent()

      expect(wrapper.vm.productList.length).toBe(0)
      expect(wrapper.vm.filteredProducts.length).toBe(0)
      expect(wrapper.vm.pagination.total).toBe(0)
    })

    it('测试商品无图片时的显示', async () => {
      const productWithoutImage = {
        ...mockProducts[0],
        image: undefined
      }
      ;(adminApi.getAllProductsForAdmin as vi.Mock).mockResolvedValue([productWithoutImage])

      await mountComponent()

      wrapper.vm.currentProduct = productWithoutImage
      wrapper.vm.detailDialogVisible = true
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.currentProduct.image).toBeUndefined()
    })

    it('测试商品无描述时的显示', async () => {
      const productWithoutDescription = {
        ...mockProducts[0],
        description: undefined
      }
      ;(adminApi.getAllProductsForAdmin as vi.Mock).mockResolvedValue([productWithoutDescription])

      await mountComponent()

      wrapper.vm.currentProduct = productWithoutDescription
      wrapper.vm.detailDialogVisible = true
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.currentProduct.description).toBeUndefined()
    })

    it('测试搜索无结果状态', async () => {
      await mountComponent()

      wrapper.vm.searchForm.keyword = '不存在的商品'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(0)
    })

    it('测试价格边界值筛选', async () => {
      await mountComponent()

      wrapper.vm.searchForm.minPrice = '99.99'
      wrapper.vm.searchForm.maxPrice = '99.99'
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].price).toBe(99.99)
    })

    it('测试分页边界情况', async () => {
      await mountComponent()

      wrapper.vm.pagination.pageSize = 1
      wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)

      wrapper.vm.handlePageChange(100)
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.pagination.current).toBe(100)
    })

    it('测试编辑表单初始值', async () => {
      await mountComponent()

      expect(wrapper.vm.editForm.name).toBe('')
      expect(wrapper.vm.editForm.price).toBe(0)
      expect(wrapper.vm.editForm.stock).toBe(0)
      expect(wrapper.vm.editForm.description).toBe('')
    })

    it('测试搜索表单初始值', async () => {
      await mountComponent()

      expect(wrapper.vm.searchForm.keyword).toBe('')
      expect(wrapper.vm.searchForm.merchantId).toBeNull()
      expect(wrapper.vm.searchForm.minPrice).toBe('')
      expect(wrapper.vm.searchForm.maxPrice).toBe('')
      expect(wrapper.vm.searchForm.stockStatus).toBe('')
      expect(wrapper.vm.searchForm.status).toBe('')
    })

    it('测试分页初始值', async () => {
      await mountComponent()

      expect(wrapper.vm.pagination.current).toBe(1)
      expect(wrapper.vm.pagination.pageSize).toBe(10)
      expect(wrapper.vm.pagination.total).toBe(mockProducts.length)
    })
  })
})
