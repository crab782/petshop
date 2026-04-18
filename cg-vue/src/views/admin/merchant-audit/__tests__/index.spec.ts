import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import MerchantAudit from '../index.vue'
import { getPendingMerchants, auditMerchant, type Merchant } from '@/api/admin'

// 模拟 API 调用
vi.mock('@/api/admin', () => ({
  getPendingMerchants: vi.fn(),
  auditMerchant: vi.fn()
}))

// 模拟 Element Plus 的消息和对话框组件
vi.mock('element-plus', async (importOriginal) => {
  const actual = await importOriginal()
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

// 导入需要模拟的组件
const ElMessage = vi.importMock('element-plus').ElMessage
const ElMessageBox = vi.importMock('element-plus').ElMessageBox

const mockMerchants: Merchant[] = [
  {
    id: 1,
    name: '商家1',
    contactPerson: '张三',
    phone: '13800138000',
    address: '北京市朝阳区',
    description: '这是一个测试商家',
    createTime: '2024-01-01 10:00:00',
    licenseImage: 'https://example.com/license1.jpg',
    status: 'pending'
  },
  {
    id: 2,
    name: '商家2',
    contactPerson: '李四',
    phone: '13900139000',
    address: '上海市浦东新区',
    description: '这是另一个测试商家',
    createTime: '2024-01-02 11:00:00',
    licenseImage: 'https://example.com/license2.jpg',
    status: 'pending'
  }
]

describe('MerchantAudit 组件', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('渲染组件时应显示搜索表单和待审核商家列表', async () => {
    // 模拟 API 响应
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockResolvedValue({
      data: mockMerchants,
      total: mockMerchants.length
    })

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 检查搜索表单是否渲染
    expect(wrapper.find('.search-card').exists()).toBe(true)
    expect(wrapper.find('el-input').exists()).toBe(true)
    expect(wrapper.find('el-button[type="primary"]').exists()).toBe(true)

    // 检查批量操作按钮是否渲染
    expect(wrapper.find('.batch-actions').exists()).toBe(true)
    expect(wrapper.find('el-button[type="success"]').exists()).toBe(true)
    expect(wrapper.find('el-button[type="danger"]').exists()).toBe(true)

    // 检查表格是否渲染
    expect(wrapper.find('.table-card').exists()).toBe(true)
    expect(wrapper.find('el-table').exists()).toBe(true)
  })

  it('加载中状态应显示加载动画', async () => {
    // 模拟 API 响应延迟
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockImplementation(() => {
      return new Promise(() => {
        // 不 resolve，保持加载状态
      })
    })

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 检查加载状态
    const table = wrapper.find('el-table')
    expect(table.attributes('v-loading')).toBe('true')
  })

  it('加载失败状态应显示错误消息', async () => {
    // 模拟 API 失败
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockRejectedValue(new Error('Failed'))

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 检查错误消息是否被调用
    expect(ElMessage.error).toHaveBeenCalledWith('获取待审核商家列表失败')
  })

  it('空数据状态应显示空状态组件', async () => {
    // 模拟 API 返回空数据
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockResolvedValue({
      data: [],
      total: 0
    })

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 检查空状态组件是否渲染
    expect(wrapper.find('el-empty').exists()).toBe(true)
  })

  it('搜索功能应调用 API 并传递搜索关键词', async () => {
    // 模拟 API 响应
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockResolvedValue({
      data: mockMerchants,
      total: mockMerchants.length
    })

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 输入搜索关键词
    const input = wrapper.find('el-input')
    await input.setValue('商家1')

    // 点击搜索按钮
    const searchButton = wrapper.find('el-button[type="primary"]')
    await searchButton.trigger('click')

    // 检查 API 是否被调用，并且传递了正确的参数
    expect(getPendingMerchants).toHaveBeenCalledWith({
      page: 1,
      pageSize: 10,
      keyword: '商家1'
    })
  })

  it('审核通过操作应调用 API 并显示成功消息', async () => {
    // 模拟 API 响应
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockResolvedValue({
      data: mockMerchants,
      total: mockMerchants.length
    })
    ;(auditMerchant as vi.MockedFunction<typeof auditMerchant>).mockResolvedValue({})
    ;(ElMessageBox.confirm as vi.MockedFunction<typeof ElMessageBox.confirm>).mockResolvedValue(true)

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 点击审核通过按钮
    const approveButton = wrapper.find('el-button[type="success"]')
    await approveButton.trigger('click')

    // 检查 API 是否被调用
    expect(auditMerchant).toHaveBeenCalledWith(mockMerchants[0].id, 'approved')
    // 检查成功消息是否被调用
    expect(ElMessage.success).toHaveBeenCalledWith('商家审核已通过')
  })

  it('审核拒绝操作应调用 API 并显示成功消息', async () => {
    // 模拟 API 响应
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockResolvedValue({
      data: mockMerchants,
      total: mockMerchants.length
    })
    ;(auditMerchant as vi.MockedFunction<typeof auditMerchant>).mockResolvedValue({})

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 点击审核拒绝按钮
    const rejectButton = wrapper.find('el-button[type="danger"]')
    await rejectButton.trigger('click')

    // 输入拒绝原因
    const rejectReasonInput = wrapper.find('textarea')
    await rejectReasonInput.setValue('不符合要求')

    // 点击拒绝按钮
    const dialogRejectButton = wrapper.find('.dialog-footer el-button[type="danger"]')
    await dialogRejectButton.trigger('click')

    // 检查 API 是否被调用
    expect(auditMerchant).toHaveBeenCalledWith(mockMerchants[0].id, 'rejected', '不符合要求')
    // 检查成功消息是否被调用
    expect(ElMessage.success).toHaveBeenCalledWith('已拒绝该商家')
  })

  it('拒绝时未填写原因应显示警告消息', async () => {
    // 模拟 API 响应
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockResolvedValue({
      data: mockMerchants,
      total: mockMerchants.length
    })

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 点击审核拒绝按钮
    const rejectButton = wrapper.find('el-button[type="danger"]')
    await rejectButton.trigger('click')

    // 不输入拒绝原因，直接点击拒绝按钮
    const dialogRejectButton = wrapper.find('.dialog-footer el-button[type="danger"]')
    await dialogRejectButton.trigger('click')

    // 检查警告消息是否被调用
    expect(ElMessage.warning).toHaveBeenCalledWith('请填写拒绝原因')
  })

  it('批量通过操作应调用 API 并显示成功消息', async () => {
    // 模拟 API 响应
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockResolvedValue({
      data: mockMerchants,
      total: mockMerchants.length
    })
    ;(auditMerchant as vi.MockedFunction<typeof auditMerchant>).mockResolvedValue({})
    ;(ElMessageBox.confirm as vi.MockedFunction<typeof ElMessageBox.confirm>).mockResolvedValue(true)

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 模拟选择行
    const selectCheckbox = wrapper.find('el-table-column[type="selection"] input[type="checkbox"]')
    await selectCheckbox.setChecked(true)

    // 点击批量通过按钮
    const batchApproveButton = wrapper.find('.batch-actions el-button[type="success"]')
    await batchApproveButton.trigger('click')

    // 检查 API 是否被调用
    expect(auditMerchant).toHaveBeenCalledWith(mockMerchants[0].id, 'approved')
    // 检查成功消息是否被调用
    expect(ElMessage.success).toHaveBeenCalledWith('批量审核已通过')
  })

  it('批量拒绝操作应调用 API 并显示成功消息', async () => {
    // 模拟 API 响应
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockResolvedValue({
      data: mockMerchants,
      total: mockMerchants.length
    })
    ;(auditMerchant as vi.MockedFunction<typeof auditMerchant>).mockResolvedValue({})
    ;(ElMessageBox.confirm as vi.MockedFunction<typeof ElMessageBox.confirm>).mockResolvedValue(true)

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 模拟选择行
    const selectCheckbox = wrapper.find('el-table-column[type="selection"] input[type="checkbox"]')
    await selectCheckbox.setChecked(true)

    // 点击批量拒绝按钮
    const batchRejectButton = wrapper.find('.batch-actions el-button[type="danger"]')
    await batchRejectButton.trigger('click')

    // 检查 API 是否被调用
    expect(auditMerchant).toHaveBeenCalledWith(mockMerchants[0].id, 'rejected')
    // 检查成功消息是否被调用
    expect(ElMessage.success).toHaveBeenCalledWith('批量已拒绝')
  })

  it('查看详情操作应显示详情对话框', async () => {
    // 模拟 API 响应
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockResolvedValue({
      data: mockMerchants,
      total: mockMerchants.length
    })

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 点击查看详情按钮
    const viewButton = wrapper.find('el-button[type="primary"]')
    await viewButton.trigger('click')

    // 检查详情对话框是否显示
    const detailDialog = wrapper.find('el-dialog')
    expect(detailDialog.exists()).toBe(true)
  })

  it('分页功能应调用 API 并传递正确的页码', async () => {
    // 模拟 API 响应
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockResolvedValue({
      data: mockMerchants,
      total: 20
    })

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 模拟页码变化
    const pagination = wrapper.find('el-pagination')
    await pagination.trigger('current-change', 2)

    // 检查 API 是否被调用，并且传递了正确的参数
    expect(getPendingMerchants).toHaveBeenCalledWith({
      page: 2,
      pageSize: 10,
      keyword: ''
    })
  })

  it('每页条数变化应调用 API 并传递正确的页大小', async () => {
    // 模拟 API 响应
    ;(getPendingMerchants as vi.MockedFunction<typeof getPendingMerchants>).mockResolvedValue({
      data: mockMerchants,
      total: 20
    })

    const wrapper = mount(MerchantAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await wrapper.vm.$nextTick()

    // 模拟每页条数变化
    const pagination = wrapper.find('el-pagination')
    await pagination.trigger('size-change', 20)

    // 检查 API 是否被调用，并且传递了正确的参数
    expect(getPendingMerchants).toHaveBeenCalledWith({
      page: 1,
      pageSize: 20,
      keyword: ''
    })
  })
})
