import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { nextTick } from 'vue'
import MerchantProducts from '../index.vue'
import {
  ElButton,
  ElTable,
  ElTableColumn,
  ElImage,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElSwitch,
  ElPagination,
  ElSelect,
  ElOption,
  ElInputNumber,
  ElTag
} from 'element-plus'
import { getMerchantProducts, updateProduct, deleteProduct, updateProductStatus, batchUpdateProductStatus, batchDeleteProducts } from '@/api/merchant'
import { createProductList, createSuccessResponse, createProduct } from '@/tests/fixtures/merchantData'

vi.mock('@/api/merchant', () => ({
  getMerchantProducts: vi.fn(),
  updateProduct: vi.fn(),
  deleteProduct: vi.fn(),
  updateProductStatus: vi.fn(),
  batchUpdateProductStatus: vi.fn(),
  batchDeleteProducts: vi.fn()
}))

vi.mock('element-plus', () => ({
  ElButton: {
    name: 'ElButton',
    template: '<button><slot /></button>'
  },
  ElTable: {
    name: 'ElTable',
    template: '<table><slot /></table>',
    props: ['data', 'loading'],
    emits: ['selection-change']
  },
  ElTableColumn: {
    name: 'ElTableColumn',
    template: '<td><slot /></td>',
    props: ['prop', 'label', 'width', 'type']
  },
  ElImage: {
    name: 'ElImage',
    template: '<img :src="src" />',
    props: ['src', 'fit']
  },
  ElDialog: {
    name: 'ElDialog',
    template: '<div v-if="modelValue"><slot /></div>',
    props: ['modelValue', 'title', 'width']
  },
  ElForm: {
    name: 'ElForm',
    template: '<form><slot /></form>',
    props: ['model', 'rules', 'inline']
  },
  ElFormItem: {
    name: 'ElFormItem',
    template: '<div><slot /></div>',
    props: ['label', 'prop']
  },
  ElInput: {
    name: 'ElInput',
    template: '<input v-model="modelValue" />',
    props: ['modelValue', 'placeholder', 'type', 'clearable']
  },
  ElMessage: {
    success: vi.fn(),
    warning: vi.fn(),
    error: vi.fn(),
    info: vi.fn()
  },
  ElMessageBox: {
    confirm: vi.fn()
  },
  ElSwitch: {
    name: 'ElSwitch',
    template: '<input type="checkbox" :checked="modelValue === activeValue" @change="$emit(\'update:modelValue\', $event.target.checked ? activeValue : inactiveValue)" />',
    props: ['modelValue', 'activeValue', 'inactiveValue'],
    emits: ['update:modelValue', 'change']
  },
  ElPagination: {
    name: 'ElPagination',
    template: '<div class="el-pagination"></div>',
    props: ['current-page', 'page-size', 'page-sizes', 'total']
  },
  ElSelect: {
    name: 'ElSelect',
    template: '<select v-model="modelValue"><slot /></select>',
    props: ['modelValue']
  },
  ElOption: {
    name: 'ElOption',
    template: '<option :value="value">{{ label }}</option>',
    props: ['label', 'value']
  },
  ElInputNumber: {
    name: 'ElInputNumber',
    template: '<input type="number" :value="modelValue" />',
    props: ['modelValue', 'min', 'precision']
  },
  ElTag: {
    name: 'ElTag',
    template: '<span><slot /></span>',
    props: ['type']
  }
}))

describe('MerchantProducts', () => {
  let wrapper: any
  const mockProducts = createProductList(15)

  const mountComponent = async (products: any[] = mockProducts) => {
    ;(getMerchantProducts as any).mockResolvedValue({
      data: products
    })

    wrapper = mount(MerchantProducts, {
      global: {
        components: {
          ElButton,
          ElTable,
          ElTableColumn,
          ElImage,
          ElDialog,
          ElForm,
          ElFormItem,
          ElInput,
          ElSwitch,
          ElPagination,
          ElSelect,
          ElOption,
          ElInputNumber,
          ElTag
        },
        stubs: {
          'el-button': true,
          'el-table': true,
          'el-table-column': true,
          'el-image': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-switch': true,
          'el-pagination': true,
          'el-select': true,
          'el-option': true,
          'el-input-number': true,
          'el-tag': true
        }
      }
    })

    await flushPromises()
    await nextTick()
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  describe('商品列表渲染', () => {
    it('应该正确渲染商品列表', async () => {
      await mountComponent()

      expect(getMerchantProducts).toHaveBeenCalled()
      expect(wrapper.vm.products.length).toBe(15)
    })

    it('应该正确显示商品名称', async () => {
      await mountComponent()

      expect(wrapper.vm.products[0].name).toBe('测试商品1')
      expect(wrapper.vm.products[4].name).toBe('测试商品5')
    })

    it('应该正确显示商品价格', async () => {
      await mountComponent()

      const formattedPrice = wrapper.vm.formatPrice(199.99)
      expect(formattedPrice).toBe('¥199.99')
    })

    it('应该正确显示商品状态', async () => {
      const productsWithStatus = [
        createProduct({ id: 1, name: '启用商品', status: 'enabled' }),
        createProduct({ id: 2, name: '禁用商品', status: 'disabled' })
      ]

      await mountComponent(productsWithStatus)

      expect(wrapper.vm.products[0].status).toBe('enabled')
      expect(wrapper.vm.products[1].status).toBe('disabled')
    })

    it('应该正确显示商品图片', async () => {
      const productsWithImages = [
        createProduct({ id: 1, name: '有图片商品', image: 'https://example.com/image.jpg' }),
        createProduct({ id: 2, name: '无图片商品', image: '' })
      ]

      await mountComponent(productsWithImages)

      expect(wrapper.vm.products[0].image).toBe('https://example.com/image.jpg')
      expect(wrapper.vm.products[1].image).toBe('')
    })
  })

  describe('搜索功能', () => {
    it('应该能够按商品名称搜索', async () => {
      await mountComponent()

      wrapper.vm.queryParams.name = '商品1'
      await nextTick()

      const filtered = wrapper.vm.filteredProducts
      expect(filtered.length).toBe(7)
      expect(filtered.every((p: any) => p.name.includes('商品1'))).toBe(true)
    })

    it('应该能够按价格区间筛选', async () => {
      const productsWithPrices = [
        createProduct({ id: 1, name: '低价商品', price: 50 }),
        createProduct({ id: 2, name: '中价商品', price: 150 }),
        createProduct({ id: 3, name: '高价商品', price: 300 })
      ]

      await mountComponent(productsWithPrices)

      wrapper.vm.queryParams.minPrice = 100
      wrapper.vm.queryParams.maxPrice = 200
      await nextTick()

      const filtered = wrapper.vm.filteredProducts
      expect(filtered.length).toBe(1)
      expect(filtered[0].name).toBe('中价商品')
    })

    it('应该能够按库存状态筛选', async () => {
      const productsWithStock = [
        createProduct({ id: 1, name: '有货商品', stock: 100 }),
        createProduct({ id: 2, name: '缺货商品', stock: 0 })
      ]

      await mountComponent(productsWithStock)

      wrapper.vm.queryParams.stockStatus = 'in_stock'
      await nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].name).toBe('有货商品')

      wrapper.vm.queryParams.stockStatus = 'out_of_stock'
      await nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].name).toBe('缺货商品')
    })

    it('应该能够按状态筛选', async () => {
      const productsWithStatus = [
        createProduct({ id: 1, name: '启用商品', status: 'enabled' }),
        createProduct({ id: 2, name: '禁用商品', status: 'disabled' })
      ]

      await mountComponent(productsWithStatus)

      wrapper.vm.queryParams.status = 'enabled'
      await nextTick()

      expect(wrapper.vm.filteredProducts.length).toBe(1)
      expect(wrapper.vm.filteredProducts[0].status).toBe('enabled')
    })

    it('应该能够重置搜索条件', async () => {
      await mountComponent()

      wrapper.vm.queryParams.name = '测试'
      wrapper.vm.queryParams.minPrice = 100
      wrapper.vm.queryParams.maxPrice = 500
      wrapper.vm.queryParams.stockStatus = 'in_stock'
      wrapper.vm.queryParams.status = 'enabled'

      wrapper.vm.handleReset()
      await nextTick()

      expect(wrapper.vm.queryParams.name).toBe('')
      expect(wrapper.vm.queryParams.minPrice).toBeUndefined()
      expect(wrapper.vm.queryParams.maxPrice).toBeUndefined()
      expect(wrapper.vm.queryParams.stockStatus).toBe('all')
      expect(wrapper.vm.queryParams.status).toBe('all')
    })

    it('搜索时应该重置页码', async () => {
      await mountComponent()

      wrapper.vm.pagination.page = 5
      wrapper.vm.handleSearch()
      await nextTick()

      expect(wrapper.vm.pagination.page).toBe(1)
    })
  })

  describe('库存预警功能', () => {
    it('应该正确识别低库存商品', async () => {
      await mountComponent()

      expect(wrapper.vm.isStockLow(5)).toBe(true)
      expect(wrapper.vm.isStockLow(10)).toBe(true)
      expect(wrapper.vm.isStockLow(11)).toBe(false)
      expect(wrapper.vm.isStockLow(0)).toBe(false)
    })

    it('应该正确识别缺货商品', async () => {
      await mountComponent()

      expect(wrapper.vm.isStockOut(0)).toBe(true)
      expect(wrapper.vm.isStockOut(-1)).toBe(true)
      expect(wrapper.vm.isStockOut(1)).toBe(false)
    })

    it('库存预警阈值应该为10', async () => {
      await mountComponent()

      expect(wrapper.vm.STORAGE_WARNING_THRESHOLD).toBe(10)
    })
  })

  describe('分页功能', () => {
    it('应该正确计算分页数据', async () => {
      await mountComponent()

      wrapper.vm.pagination.page = 1
      wrapper.vm.pagination.pageSize = 10
      await nextTick()

      expect(wrapper.vm.paginatedProducts.length).toBe(10)
      expect(wrapper.vm.pagination.total).toBe(15)
    })

    it('应该正确处理页码变化', async () => {
      await mountComponent()

      wrapper.vm.handlePageChange(2)
      await nextTick()

      expect(wrapper.vm.pagination.page).toBe(2)
    })

    it('应该正确处理每页条数变化', async () => {
      await mountComponent()

      wrapper.vm.handleSizeChange(20)
      await nextTick()

      expect(wrapper.vm.pagination.pageSize).toBe(20)
      expect(wrapper.vm.pagination.page).toBe(1)
    })

    it('应该正确显示第二页数据', async () => {
      await mountComponent()

      wrapper.vm.pagination.page = 2
      wrapper.vm.pagination.pageSize = 10
      await nextTick()

      expect(wrapper.vm.paginatedProducts.length).toBe(5)
    })
  })

  describe('批量操作功能', () => {
    it('没有选择商品时批量操作应该提示', async () => {
      await mountComponent()

      wrapper.vm.selectedRows = []
      wrapper.vm.handleBatchEnable()

      expect(ElMessage.warning).toHaveBeenCalledWith('请先选择商品')
    })

    it('应该能够批量启用商品', async () => {
      ;(batchUpdateProductStatus as any).mockResolvedValue(createSuccessResponse([]))

      await mountComponent()

      wrapper.vm.selectedRows = [
        createProduct({ id: 1 }),
        createProduct({ id: 2 })
      ]

      await wrapper.vm.handleBatchEnable()
      await flushPromises()

      expect(batchUpdateProductStatus).toHaveBeenCalledWith([1, 2], 'enabled')
      expect(ElMessage.success).toHaveBeenCalledWith('批量启用成功')
    })

    it('应该能够批量禁用商品', async () => {
      ;(batchUpdateProductStatus as any).mockResolvedValue(createSuccessResponse([]))

      await mountComponent()

      wrapper.vm.selectedRows = [
        createProduct({ id: 1 }),
        createProduct({ id: 2 })
      ]

      await wrapper.vm.handleBatchDisable()
      await flushPromises()

      expect(batchUpdateProductStatus).toHaveBeenCalledWith([1, 2], 'disabled')
      expect(ElMessage.success).toHaveBeenCalledWith('批量禁用成功')
    })

    it('应该能够批量删除商品', async () => {
      ;(ElMessageBox.confirm as any).mockResolvedValue({})
      ;(batchDeleteProducts as any).mockResolvedValue(createSuccessResponse(null))

      await mountComponent()

      wrapper.vm.selectedRows = [
        createProduct({ id: 1 }),
        createProduct({ id: 2 })
      ]

      await wrapper.vm.handleBatchDelete()
      await flushPromises()

      expect(batchDeleteProducts).toHaveBeenCalledWith([1, 2])
      expect(ElMessage.success).toHaveBeenCalledWith('批量删除成功')
    })

    it('批量删除取消时不应该执行删除', async () => {
      ;(ElMessageBox.confirm as any).mockRejectedValue('cancel')

      await mountComponent()

      wrapper.vm.selectedRows = [
        createProduct({ id: 1 })
      ]

      await wrapper.vm.handleBatchDelete()
      await flushPromises()

      expect(batchDeleteProducts).not.toHaveBeenCalled()
    })

    it('应该正确更新选择状态', async () => {
      await mountComponent()

      const selectedProducts = [
        createProduct({ id: 1 }),
        createProduct({ id: 2 })
      ]

      wrapper.vm.handleSelectionChange(selectedProducts)

      expect(wrapper.vm.selectedRows).toEqual(selectedProducts)
    })
  })

  describe('商品编辑功能', () => {
    it('应该能够打开编辑对话框', async () => {
      await mountComponent()

      const product = createProduct({ id: 1, name: '测试商品' })
      wrapper.vm.handleEdit(product)
      await nextTick()

      expect(wrapper.vm.dialogVisible).toBe(true)
      expect(wrapper.vm.isEdit).toBe(true)
      expect(wrapper.vm.dialogTitle).toBe('编辑商品')
      expect(wrapper.vm.formData.id).toBe(1)
      expect(wrapper.vm.formData.name).toBe('测试商品')
    })

    it('编辑商品时应该正确更新', async () => {
      ;(updateProduct as any).mockResolvedValue(createSuccessResponse(createProduct()))

      await mountComponent()

      wrapper.vm.isEdit = true
      wrapper.vm.formData = {
        id: 1,
        name: '更新商品',
        description: '描述',
        price: 99.99,
        stock: 50,
        image: '',
        status: 'enabled'
      }
      wrapper.vm.formRef = { validate: (cb: any) => cb(true) }

      await wrapper.vm.submitForm()
      await flushPromises()

      expect(updateProduct).toHaveBeenCalled()
      expect(ElMessage.success).toHaveBeenCalledWith('更新成功')
    })
  })

  describe('商品删除功能', () => {
    it('应该能够删除商品', async () => {
      ;(ElMessageBox.confirm as any).mockResolvedValue({})
      ;(deleteProduct as any).mockResolvedValue(createSuccessResponse(null))

      await mountComponent()

      const product = createProduct({ id: 1 })
      await wrapper.vm.handleDelete(product)
      await flushPromises()

      expect(deleteProduct).toHaveBeenCalledWith(1)
      expect(ElMessage.success).toHaveBeenCalledWith('删除成功')
    })

    it('取消删除时不应该执行删除', async () => {
      ;(ElMessageBox.confirm as any).mockRejectedValue('cancel')

      await mountComponent()

      const product = createProduct({ id: 1 })
      await wrapper.vm.handleDelete(product)
      await flushPromises()

      expect(deleteProduct).not.toHaveBeenCalled()
    })

    it('删除失败时应该显示错误信息', async () => {
      ;(ElMessageBox.confirm as any).mockResolvedValue({})
      ;(deleteProduct as any).mockRejectedValue(new Error('删除失败'))

      await mountComponent()

      const product = createProduct({ id: 1 })
      await wrapper.vm.handleDelete(product)
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('删除失败')
    })
  })

  describe('状态切换功能', () => {
    it('应该能够切换商品状态为启用', async () => {
      ;(updateProductStatus as any).mockResolvedValue(createSuccessResponse(createProduct({ status: 'enabled' })))

      await mountComponent()

      const product = createProduct({ id: 1, status: 'disabled' })
      product.status = 'enabled'

      await wrapper.vm.handleStatusChange(product)
      await flushPromises()

      expect(updateProductStatus).toHaveBeenCalledWith(1, 'enabled')
      expect(ElMessage.success).toHaveBeenCalledWith('启用成功')
    })

    it('应该能够切换商品状态为禁用', async () => {
      ;(updateProductStatus as any).mockResolvedValue(createSuccessResponse(createProduct({ status: 'disabled' })))

      await mountComponent()

      const product = createProduct({ id: 1, status: 'enabled' })
      product.status = 'disabled'

      await wrapper.vm.handleStatusChange(product)
      await flushPromises()

      expect(updateProductStatus).toHaveBeenCalledWith(1, 'disabled')
      expect(ElMessage.success).toHaveBeenCalledWith('禁用成功')
    })

    it('状态切换失败时应该恢复原状态', async () => {
      ;(updateProductStatus as any).mockRejectedValue(new Error('更新失败'))

      await mountComponent()

      const product = createProduct({ id: 1, status: 'enabled' })
      product.status = 'disabled'

      await wrapper.vm.handleStatusChange(product)
      await flushPromises()

      expect(product.status).toBe('enabled')
      expect(ElMessage.error).toHaveBeenCalledWith('状态更新失败')
    })
  })

  describe('日期格式化', () => {
    it('应该正确格式化日期', async () => {
      await mountComponent()

      const date = '2024-04-20T10:00:00.000Z'
      const formatted = wrapper.vm.formatDate(date)

      expect(formatted).toBeTruthy()
    })

    it('空日期应该返回横线', async () => {
      await mountComponent()

      expect(wrapper.vm.formatDate(undefined)).toBe('-')
      expect(wrapper.vm.formatDate('')).toBe('-')
    })
  })

  describe('添加商品功能', () => {
    it('应该能够跳转到商品编辑页面', async () => {
      const mockLocation = {
        href: ''
      }
      Object.defineProperty(window, 'location', {
        value: mockLocation,
        writable: true
      })

      await mountComponent()

      wrapper.vm.goToProductEdit()

      expect(window.location.href).toBe('/merchant/product-edit')
    })
  })

  describe('API 错误处理', () => {
    it('获取商品列表失败时应该显示错误信息', async () => {
      ;(getMerchantProducts as any).mockRejectedValue(new Error('获取失败'))

      wrapper = mount(MerchantProducts, {
        global: {
          components: {
            ElButton,
            ElTable,
            ElTableColumn,
            ElImage,
            ElDialog,
            ElForm,
            ElFormItem,
            ElInput,
            ElSwitch,
            ElPagination,
            ElSelect,
            ElOption,
            ElInputNumber,
            ElTag
          },
          stubs: {
            'el-button': true,
            'el-table': true,
            'el-table-column': true,
            'el-image': true,
            'el-dialog': true,
            'el-form': true,
            'el-form-item': true,
            'el-input': true,
            'el-switch': true,
            'el-pagination': true,
            'el-select': true,
            'el-option': true,
            'el-input-number': true,
            'el-tag': true
          }
        }
      })

      await flushPromises()
      await nextTick()

      expect(ElMessage.error).toHaveBeenCalledWith('获取商品列表失败')
    })

    it('批量启用失败时应该显示错误信息', async () => {
      ;(batchUpdateProductStatus as any).mockRejectedValue(new Error('操作失败'))

      await mountComponent()

      wrapper.vm.selectedRows = [createProduct({ id: 1 })]

      await wrapper.vm.handleBatchEnable()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('批量启用失败')
    })

    it('批量禁用失败时应该显示错误信息', async () => {
      ;(batchUpdateProductStatus as any).mockRejectedValue(new Error('操作失败'))

      await mountComponent()

      wrapper.vm.selectedRows = [createProduct({ id: 1 })]

      await wrapper.vm.handleBatchDisable()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('批量禁用失败')
    })

    it('批量删除失败时应该显示错误信息', async () => {
      ;(ElMessageBox.confirm as any).mockResolvedValue({})
      ;(batchDeleteProducts as any).mockRejectedValue(new Error('删除失败'))

      await mountComponent()

      wrapper.vm.selectedRows = [createProduct({ id: 1 })]

      await wrapper.vm.handleBatchDelete()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('批量删除失败')
    })
  })
})
