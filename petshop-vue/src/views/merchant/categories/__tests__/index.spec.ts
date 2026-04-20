import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import Categories from '../index.vue'
import {
  ElButton,
  ElTable,
  ElTableColumn,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElSwitch,
  ElPagination,
  ElInputNumber,
  ElImage,
  ElSelect,
  ElOption
} from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh } from '@element-plus/icons-vue'
import {
  getCategories,
  addCategory,
  updateCategory,
  deleteCategory,
  updateCategoryStatus,
  batchUpdateCategoryStatus,
  batchDeleteCategories,
  type Category
} from '@/api/merchant'

vi.mock('@/api/merchant', () => ({
  getCategories: vi.fn(),
  addCategory: vi.fn(),
  updateCategory: vi.fn(),
  deleteCategory: vi.fn(),
  updateCategoryStatus: vi.fn(),
  batchUpdateCategoryStatus: vi.fn(),
  batchDeleteCategories: vi.fn()
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
    },
    ElMessageBox: {
      confirm: vi.fn()
    }
  }
})

const mockCategories: Category[] = [
  {
    id: 1,
    name: '宠物食品',
    icon: 'https://picsum.photos/40/40?random=1',
    description: '各类宠物食品',
    sort: 10,
    status: 'enabled',
    productCount: 25,
    createdAt: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    name: '宠物用品',
    icon: 'https://picsum.photos/40/40?random=2',
    description: '各类宠物用品',
    sort: 8,
    status: 'enabled',
    productCount: 18,
    createdAt: '2024-01-02 10:00:00'
  },
  {
    id: 3,
    name: '宠物玩具',
    icon: 'https://picsum.photos/40/40?random=3',
    description: '各类宠物玩具',
    sort: 5,
    status: 'disabled',
    productCount: 12,
    createdAt: '2024-01-03 10:00:00'
  }
]

const createWrapper = () => {
  return mount(Categories, {
    global: {
      components: {
        ElButton,
        ElTable,
        ElTableColumn,
        ElDialog,
        ElForm,
        ElFormItem,
        ElInput,
        ElSwitch,
        ElPagination,
        ElInputNumber,
        ElImage,
        ElSelect,
        ElOption
      },
      stubs: {
        'el-icon': true
      }
    }
  })
}

describe('Categories', () => {
  let wrapper: ReturnType<typeof createWrapper>

  beforeEach(() => {
    vi.clearAllMocks()
    ;(getCategories as any).mockResolvedValue({ data: mockCategories })
  })

  afterEach(() => {
    wrapper?.unmount()
  })

  describe('分类列表渲染', () => {
    it('组件挂载时应该调用 getCategories API', async () => {
      wrapper = createWrapper()
      await flushPromises()
      expect(getCategories).toHaveBeenCalled()
    })

    it('应该正确渲染分类列表', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const table = wrapper.findComponent(ElTable)
      expect(table.exists()).toBe(true)

      const tableData = table.props('data')
      expect(tableData.length).toBe(3)
    })

    it('应该按排序值降序显示分类', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const table = wrapper.findComponent(ElTable)
      const tableData = table.props('data')

      expect(tableData[0].sort).toBe(10)
      expect(tableData[1].sort).toBe(8)
      expect(tableData[2].sort).toBe(5)
    })

    it('应该正确显示分类名称', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const table = wrapper.findComponent(ElTable)
      const tableData = table.props('data')

      expect(tableData[0].name).toBe('宠物食品')
      expect(tableData[1].name).toBe('宠物用品')
      expect(tableData[2].name).toBe('宠物玩具')
    })

    it('应该正确显示分类状态', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const table = wrapper.findComponent(ElTable)
      const tableData = table.props('data')

      expect(tableData[0].status).toBe('enabled')
      expect(tableData[2].status).toBe('disabled')
    })

    it('应该正确显示商品数量', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const table = wrapper.findComponent(ElTable)
      const tableData = table.props('data')

      expect(tableData[0].productCount).toBe(25)
      expect(tableData[1].productCount).toBe(18)
      expect(tableData[2].productCount).toBe(12)
    })

    it('加载状态应该正确显示', async () => {
      ;(getCategories as any).mockImplementation(() => new Promise(() => {}))

      wrapper = createWrapper()

      expect(wrapper.vm.loading).toBe(true)
    })
  })

  describe('添加分类功能', () => {
    it('点击添加按钮应该打开对话框', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const addButton = wrapper.findAllComponents(ElButton)[0]
      await addButton.trigger('click')
      await flushPromises()

      const dialog = wrapper.findComponent(ElDialog)
      expect(dialog.props('modelValue')).toBe(true)
      expect(wrapper.vm.dialogTitle).toBe('添加分类')
      expect(wrapper.vm.isEdit).toBe(false)
    })

    it('添加分类时应该重置表单数据', async () => {
      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.formData = {
        id: 99,
        name: '测试分类',
        icon: 'test-icon',
        description: '测试描述',
        sort: 100,
        status: 'disabled'
      }

      const addButton = wrapper.findAllComponents(ElButton)[0]
      await addButton.trigger('click')
      await flushPromises()

      expect(wrapper.vm.formData.id).toBe(0)
      expect(wrapper.vm.formData.name).toBe('')
      expect(wrapper.vm.formData.icon).toBe('')
      expect(wrapper.vm.formData.description).toBe('')
      expect(wrapper.vm.formData.sort).toBe(0)
      expect(wrapper.vm.formData.status).toBe('enabled')
    })

    it('提交添加分类应该调用 addCategory API', async () => {
      ;(addCategory as any).mockResolvedValue({ data: { id: 4, name: '新分类', sort: 0, status: 'enabled', productCount: 0 } })

      wrapper = createWrapper()
      await flushPromises()

      const mockValidate = vi.fn((cb: (valid: boolean) => void) => cb(true))
      wrapper.vm.formRef = { validate: mockValidate }

      wrapper.vm.formData = {
        id: 0,
        name: '新分类',
        icon: 'new-icon',
        description: '新分类描述',
        sort: 0,
        status: 'enabled'
      }

      await wrapper.vm.submitForm()
      await flushPromises()

      expect(mockValidate).toHaveBeenCalled()
      expect(addCategory).toHaveBeenCalledWith({
        name: '新分类',
        icon: 'new-icon',
        description: '新分类描述',
        sort: 0,
        status: 'enabled'
      })
      expect(ElMessage.success).toHaveBeenCalledWith('添加成功')
    })

    it('添加分类成功后应该关闭对话框并刷新列表', async () => {
      ;(addCategory as any).mockResolvedValue({ data: { id: 4, name: '新分类', sort: 0, status: 'enabled', productCount: 0 } })

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.dialogVisible = true
      wrapper.vm.formData = {
        id: 0,
        name: '新分类',
        icon: '',
        description: '',
        sort: 0,
        status: 'enabled'
      }

      wrapper.vm.formRef = { validate: (cb: (valid: boolean) => void) => cb(true) }
      await wrapper.vm.submitForm()
      await flushPromises()

      expect(wrapper.vm.dialogVisible).toBe(false)
      expect(getCategories).toHaveBeenCalledTimes(2)
    })

    it('添加分类失败应该显示错误消息', async () => {
      ;(addCategory as any).mockRejectedValue(new Error('添加失败'))

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.formData = {
        id: 0,
        name: '新分类',
        icon: '',
        description: '',
        sort: 0,
        status: 'enabled'
      }

      wrapper.vm.formRef = { validate: (cb: (valid: boolean) => void) => cb(true) }
      await wrapper.vm.submitForm()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('添加失败')
    })
  })

  describe('编辑分类功能', () => {
    it('点击编辑按钮应该打开对话框并填充数据', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const category = mockCategories[0]
      wrapper.vm.handleEdit(category)
      await flushPromises()

      expect(wrapper.vm.dialogVisible).toBe(true)
      expect(wrapper.vm.dialogTitle).toBe('编辑分类')
      expect(wrapper.vm.isEdit).toBe(true)
      expect(wrapper.vm.formData.id).toBe(category.id)
      expect(wrapper.vm.formData.name).toBe(category.name)
    })

    it('提交编辑分类应该调用 updateCategory API', async () => {
      ;(updateCategory as any).mockResolvedValue({ data: mockCategories[0] })

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.isEdit = true
      wrapper.vm.formData = {
        id: 1,
        name: '更新后的分类',
        icon: 'updated-icon',
        description: '更新后的描述',
        sort: 15,
        status: 'enabled'
      }

      wrapper.vm.formRef = { validate: (cb: (valid: boolean) => void) => cb(true) }
      await wrapper.vm.submitForm()
      await flushPromises()

      expect(updateCategory).toHaveBeenCalledWith(1, {
        name: '更新后的分类',
        icon: 'updated-icon',
        description: '更新后的描述',
        sort: 15,
        status: 'enabled'
      })
      expect(ElMessage.success).toHaveBeenCalledWith('更新成功')
    })

    it('编辑分类成功后应该关闭对话框并刷新列表', async () => {
      ;(updateCategory as any).mockResolvedValue({ data: mockCategories[0] })

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.dialogVisible = true
      wrapper.vm.isEdit = true
      wrapper.vm.formData = {
        id: 1,
        name: '更新后的分类',
        icon: '',
        description: '',
        sort: 10,
        status: 'enabled'
      }

      wrapper.vm.formRef = { validate: (cb: (valid: boolean) => void) => cb(true) }
      await wrapper.vm.submitForm()
      await flushPromises()

      expect(wrapper.vm.dialogVisible).toBe(false)
      expect(getCategories).toHaveBeenCalledTimes(2)
    })

    it('编辑分类失败应该显示错误消息', async () => {
      ;(updateCategory as any).mockRejectedValue(new Error('更新失败'))

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.isEdit = true
      wrapper.vm.formData = {
        id: 1,
        name: '更新后的分类',
        icon: '',
        description: '',
        sort: 10,
        status: 'enabled'
      }

      wrapper.vm.formRef = { validate: (cb: (valid: boolean) => void) => cb(true) }
      await wrapper.vm.submitForm()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('更新失败')
    })
  })

  describe('删除分类功能', () => {
    it('确认删除应该调用 deleteCategory API', async () => {
      ;(ElMessageBox.confirm as any).mockResolvedValue('confirm')
      ;(deleteCategory as any).mockResolvedValue({})

      wrapper = createWrapper()
      await flushPromises()

      await wrapper.vm.handleDelete(mockCategories[0])
      await flushPromises()

      expect(deleteCategory).toHaveBeenCalledWith(1)
      expect(ElMessage.success).toHaveBeenCalledWith('删除成功')
    })

    it('删除成功后应该刷新列表', async () => {
      ;(ElMessageBox.confirm as any).mockResolvedValue('confirm')
      ;(deleteCategory as any).mockResolvedValue({})

      wrapper = createWrapper()
      await flushPromises()

      vi.clearAllMocks()
      ;(getCategories as any).mockResolvedValue({ data: mockCategories.slice(1) })

      await wrapper.vm.handleDelete(mockCategories[0])
      await flushPromises()

      expect(getCategories).toHaveBeenCalled()
    })

    it('取消删除不应该调用 deleteCategory API', async () => {
      ;(ElMessageBox.confirm as any).mockRejectedValue('cancel')

      wrapper = createWrapper()
      await flushPromises()

      await wrapper.vm.handleDelete(mockCategories[0])
      await flushPromises()

      expect(deleteCategory).not.toHaveBeenCalled()
    })

    it('删除失败应该显示错误消息', async () => {
      ;(ElMessageBox.confirm as any).mockResolvedValue('confirm')
      ;(deleteCategory as any).mockRejectedValue(new Error('删除失败'))

      wrapper = createWrapper()
      await flushPromises()

      await wrapper.vm.handleDelete(mockCategories[0])
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('删除失败')
    })
  })

  describe('排序功能', () => {
    it('点击增加排序按钮应该调用 updateCategory API', async () => {
      ;(updateCategory as any).mockResolvedValue({ data: { ...mockCategories[0], sort: 11 } })

      wrapper = createWrapper()
      await flushPromises()

      await wrapper.vm.handleSortChange(mockCategories[0], 1)
      await flushPromises()

      expect(updateCategory).toHaveBeenCalledWith(1, { sort: 11 })
      expect(ElMessage.success).toHaveBeenCalledWith('排序更新成功')
    })

    it('点击减少排序按钮应该调用 updateCategory API', async () => {
      ;(updateCategory as any).mockResolvedValue({ data: { ...mockCategories[0], sort: 9 } })

      wrapper = createWrapper()
      await flushPromises()

      await wrapper.vm.handleSortChange(mockCategories[0], -1)
      await flushPromises()

      expect(updateCategory).toHaveBeenCalledWith(1, { sort: 9 })
      expect(ElMessage.success).toHaveBeenCalledWith('排序更新成功')
    })

    it('排序更新成功后应该刷新列表', async () => {
      ;(updateCategory as any).mockResolvedValue({ data: { ...mockCategories[0], sort: 11 } })

      wrapper = createWrapper()
      await flushPromises()

      vi.clearAllMocks()
      ;(getCategories as any).mockResolvedValue({ data: mockCategories })

      await wrapper.vm.handleSortChange(mockCategories[0], 1)
      await flushPromises()

      expect(getCategories).toHaveBeenCalled()
    })

    it('排序更新失败应该显示错误消息', async () => {
      ;(updateCategory as any).mockRejectedValue(new Error('排序更新失败'))

      wrapper = createWrapper()
      await flushPromises()

      await wrapper.vm.handleSortChange(mockCategories[0], 1)
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('排序更新失败')
    })
  })

  describe('状态切换功能', () => {
    it('切换状态应该调用 updateCategoryStatus API', async () => {
      ;(updateCategoryStatus as any).mockResolvedValue({ data: { ...mockCategories[0], status: 'disabled' } })

      wrapper = createWrapper()
      await flushPromises()

      const category = { ...mockCategories[0], status: 'disabled' as const }
      await wrapper.vm.handleStatusChange(category)
      await flushPromises()

      expect(updateCategoryStatus).toHaveBeenCalledWith(1, 'disabled')
      expect(ElMessage.success).toHaveBeenCalledWith('禁用成功')
    })

    it('启用状态切换应该显示启用成功消息', async () => {
      ;(updateCategoryStatus as any).mockResolvedValue({ data: { ...mockCategories[2], status: 'enabled' } })

      wrapper = createWrapper()
      await flushPromises()

      const category = { ...mockCategories[2], status: 'enabled' as const }
      await wrapper.vm.handleStatusChange(category)
      await flushPromises()

      expect(ElMessage.success).toHaveBeenCalledWith('启用成功')
    })

    it('状态切换失败应该恢复原状态并显示错误消息', async () => {
      ;(updateCategoryStatus as any).mockRejectedValue(new Error('状态更新失败'))

      wrapper = createWrapper()
      await flushPromises()

      const category = { ...mockCategories[0], status: 'disabled' as const }
      await wrapper.vm.handleStatusChange(category)
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('状态更新失败')
      expect(category.status).toBe('enabled')
    })
  })

  describe('搜索和筛选功能', () => {
    it('按名称搜索应该正确过滤分类', async () => {
      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.queryParams.name = '食品'
      wrapper.vm.handleSearch()
      await flushPromises()

      const filtered = wrapper.vm.filteredCategories
      expect(filtered.length).toBe(1)
      expect(filtered[0].name).toBe('宠物食品')
    })

    it('按状态筛选应该正确过滤分类', async () => {
      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.queryParams.status = 'enabled'
      wrapper.vm.handleSearch()
      await flushPromises()

      const filtered = wrapper.vm.filteredCategories
      expect(filtered.length).toBe(2)
      expect(filtered.every((c: Category) => c.status === 'enabled')).toBe(true)
    })

    it('组合搜索和筛选应该正确过滤分类', async () => {
      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.queryParams.name = '宠物'
      wrapper.vm.queryParams.status = 'enabled'
      wrapper.vm.handleSearch()
      await flushPromises()

      const filtered = wrapper.vm.filteredCategories
      expect(filtered.length).toBe(2)
      expect(filtered.every((c: Category) => c.status === 'enabled' && c.name.includes('宠物'))).toBe(true)
    })

    it('重置搜索应该清空搜索条件', async () => {
      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.queryParams.name = '测试'
      wrapper.vm.queryParams.status = 'enabled'

      wrapper.vm.handleReset()
      await flushPromises()

      expect(wrapper.vm.queryParams.name).toBe('')
      expect(wrapper.vm.queryParams.status).toBe('all')
    })

    it('搜索后应该重置页码为1', async () => {
      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.pagination.page = 5
      wrapper.vm.handleSearch()
      await flushPromises()

      expect(wrapper.vm.pagination.page).toBe(1)
    })
  })

  describe('分页功能', () => {
    it('应该正确计算分页数据', async () => {
      const manyCategories = Array.from({ length: 25 }, (_, i) => ({
        id: i + 1,
        name: `分类${i + 1}`,
        sort: 25 - i,
        status: 'enabled' as const,
        productCount: 0
      }))

      ;(getCategories as any).mockResolvedValue({ data: manyCategories })

      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.vm.pagination.total).toBe(25)
      expect(wrapper.vm.paginatedCategories.length).toBe(10)
    })

    it('切换页码应该正确更新分页数据', async () => {
      const manyCategories = Array.from({ length: 25 }, (_, i) => ({
        id: i + 1,
        name: `分类${i + 1}`,
        sort: 25 - i,
        status: 'enabled' as const,
        productCount: 0
      }))

      ;(getCategories as any).mockResolvedValue({ data: manyCategories })

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.handlePageChange(2)
      await flushPromises()

      expect(wrapper.vm.pagination.page).toBe(2)
      expect(wrapper.vm.paginatedCategories[0].id).toBe(11)
    })

    it('改变每页显示数量应该重置页码', async () => {
      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.pagination.page = 3
      wrapper.vm.handleSizeChange(20)
      await flushPromises()

      expect(wrapper.vm.pagination.pageSize).toBe(20)
      expect(wrapper.vm.pagination.page).toBe(1)
    })
  })

  describe('批量操作功能', () => {
    it('批量启用应该调用 batchUpdateCategoryStatus API', async () => {
      ;(batchUpdateCategoryStatus as any).mockResolvedValue({ data: [] })

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.selectedRows = [mockCategories[0], mockCategories[1]]
      await wrapper.vm.handleBatchEnable()
      await flushPromises()

      expect(batchUpdateCategoryStatus).toHaveBeenCalledWith([1, 2], 'enabled')
      expect(ElMessage.success).toHaveBeenCalledWith('批量启用成功')
    })

    it('批量禁用应该调用 batchUpdateCategoryStatus API', async () => {
      ;(batchUpdateCategoryStatus as any).mockResolvedValue({ data: [] })

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.selectedRows = [mockCategories[0], mockCategories[1]]
      await wrapper.vm.handleBatchDisable()
      await flushPromises()

      expect(batchUpdateCategoryStatus).toHaveBeenCalledWith([1, 2], 'disabled')
      expect(ElMessage.success).toHaveBeenCalledWith('批量禁用成功')
    })

    it('批量删除应该调用 batchDeleteCategories API', async () => {
      ;(ElMessageBox.confirm as any).mockResolvedValue('confirm')
      ;(batchDeleteCategories as any).mockResolvedValue({})

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.selectedRows = [mockCategories[0], mockCategories[1]]
      await wrapper.vm.handleBatchDelete()
      await flushPromises()

      expect(batchDeleteCategories).toHaveBeenCalledWith([1, 2])
      expect(ElMessage.success).toHaveBeenCalledWith('批量删除成功')
    })

    it('未选择分类时批量操作应该显示警告', async () => {
      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.selectedRows = []
      await wrapper.vm.handleBatchEnable()
      await flushPromises()

      expect(ElMessage.warning).toHaveBeenCalledWith('请先选择分类')
    })

    it('取消批量删除不应该调用 API', async () => {
      ;(ElMessageBox.confirm as any).mockRejectedValue('cancel')

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.selectedRows = [mockCategories[0]]
      await wrapper.vm.handleBatchDelete()
      await flushPromises()

      expect(batchDeleteCategories).not.toHaveBeenCalled()
    })

    it('批量操作成功后应该刷新列表', async () => {
      ;(batchUpdateCategoryStatus as any).mockResolvedValue({ data: [] })

      wrapper = createWrapper()
      await flushPromises()

      vi.clearAllMocks()
      ;(getCategories as any).mockResolvedValue({ data: mockCategories })

      wrapper.vm.selectedRows = [mockCategories[0]]
      await wrapper.vm.handleBatchEnable()
      await flushPromises()

      expect(getCategories).toHaveBeenCalled()
    })
  })

  describe('表单验证', () => {
    it('分类名称为必填项', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const rules = wrapper.vm.rules
      expect(rules.name).toBeDefined()
      expect(rules.name[0].required).toBe(true)
      expect(rules.name[0].message).toBe('请输入分类名称')
    })
  })

  describe('日期格式化', () => {
    it('应该正确格式化日期', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const formatted = wrapper.vm.formatDate('2024-01-15 10:30:00')
      expect(formatted).toMatch(/2024/)
    })

    it('日期为空时应该返回横线', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const formatted = wrapper.vm.formatDate(undefined)
      expect(formatted).toBe('-')
    })
  })

  describe('选择功能', () => {
    it('应该正确更新选中的行', async () => {
      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.handleSelectionChange([mockCategories[0], mockCategories[1]])
      expect(wrapper.vm.selectedRows.length).toBe(2)
      expect(wrapper.vm.selectedRows[0].id).toBe(1)
      expect(wrapper.vm.selectedRows[1].id).toBe(2)
    })

    it('清空选择应该正确更新', async () => {
      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.selectedRows = [mockCategories[0]]
      wrapper.vm.handleSelectionChange([])
      expect(wrapper.vm.selectedRows.length).toBe(0)
    })
  })

  describe('对话框功能', () => {
    it('点击取消按钮应该关闭对话框', async () => {
      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.dialogVisible = true

      const dialog = wrapper.findComponent(ElDialog)
      await dialog.vm.$emit('update:modelValue', false)
      await flushPromises()

      expect(wrapper.vm.dialogVisible).toBe(false)
    })
  })

  describe('错误处理', () => {
    it('获取分类列表失败应该显示错误消息', async () => {
      ;(getCategories as any).mockRejectedValue(new Error('获取失败'))

      wrapper = createWrapper()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('获取分类列表失败')
    })

    it('批量启用失败应该显示错误消息', async () => {
      ;(batchUpdateCategoryStatus as any).mockRejectedValue(new Error('批量启用失败'))

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.selectedRows = [mockCategories[0]]
      await wrapper.vm.handleBatchEnable()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('批量启用失败')
    })

    it('批量禁用失败应该显示错误消息', async () => {
      ;(batchUpdateCategoryStatus as any).mockRejectedValue(new Error('批量禁用失败'))

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.selectedRows = [mockCategories[0]]
      await wrapper.vm.handleBatchDisable()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('批量禁用失败')
    })

    it('批量删除失败应该显示错误消息', async () => {
      ;(ElMessageBox.confirm as any).mockResolvedValue('confirm')
      ;(batchDeleteCategories as any).mockRejectedValue(new Error('批量删除失败'))

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.selectedRows = [mockCategories[0]]
      await wrapper.vm.handleBatchDelete()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('批量删除失败')
    })
  })
})
