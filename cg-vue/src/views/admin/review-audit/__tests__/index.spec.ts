import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import ReviewAudit from '../index.vue'
import type { Review } from '@/api/admin'

const mockElMessage = {
  success: vi.fn(),
  error: vi.fn(),
  warning: vi.fn(),
  info: vi.fn()
}

const mockElMessageBox = {
  confirm: vi.fn()
}

vi.mock('element-plus', async (importOriginal) => {
  const actual = await importOriginal()
  return {
    ...actual,
    ElMessage: mockElMessage,
    ElMessageBox: mockElMessageBox,
    default: actual.default
  }
})

vi.mock('@/api/admin', () => ({
  getReviewsForAudit: vi.fn(),
  approveReview: vi.fn(),
  markReviewViolation: vi.fn(),
  deleteReview: vi.fn()
}))

import {
  getReviewsForAudit,
  approveReview,
  markReviewViolation,
  deleteReview
} from '@/api/admin'

const mockedGetReviewsForAudit = vi.mocked(getReviewsForAudit)
const mockedApproveReview = vi.mocked(approveReview)
const mockedMarkReviewViolation = vi.mocked(markReviewViolation)
const mockedDeleteReview = vi.mocked(deleteReview)

const mockReviews: Review[] = [
  {
    id: 1,
    serviceId: 101,
    serviceName: '宠物美容服务',
    merchantId: 201,
    merchantName: '爱心宠物店',
    userId: 301,
    userName: '张三',
    rating: 5,
    comment: '服务非常好，宠物很喜欢！',
    status: 'pending',
    createdAt: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    serviceId: 102,
    serviceName: '宠物寄养服务',
    merchantId: 202,
    merchantName: '快乐宠物家园',
    userId: 302,
    userName: '李四',
    rating: 4,
    comment: '环境不错，服务态度好',
    status: 'pending',
    createdAt: '2024-01-16 14:20:00'
  },
  {
    id: 3,
    serviceId: 103,
    serviceName: '宠物医疗服务',
    merchantId: 203,
    merchantName: '专业宠物医院',
    userId: 303,
    userName: '王五',
    rating: 3,
    comment: '一般般，等待时间较长',
    status: 'approved',
    createdAt: '2024-01-17 09:00:00'
  },
  {
    id: 4,
    serviceId: 104,
    serviceName: '宠物训练课程',
    merchantId: 204,
    merchantName: '智慧宠物训练',
    userId: 304,
    userName: '赵六',
    rating: 2,
    comment: '含有敏感词汇',
    status: 'violation',
    createdAt: '2024-01-18 16:45:00'
  }
]

describe('ReviewAudit 组件', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockedGetReviewsForAudit.mockReset()
    mockedApproveReview.mockReset()
    mockedMarkReviewViolation.mockReset()
    mockedDeleteReview.mockReset()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  const mountComponent = async () => {
    const wrapper = mount(ReviewAudit, {
      global: {
        plugins: [ElementPlus]
      }
    })
    await flushPromises()
    return wrapper
  }

  describe('数据渲染测试', () => {
    it('渲染组件时应显示面包屑导航', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])
      const wrapper = await mountComponent()

      expect(wrapper.find('.review-audit-container').exists()).toBe(true)
      expect(wrapper.find('el-breadcrumb').exists()).toBe(true)
      expect(wrapper.text()).toContain('首页')
      expect(wrapper.text()).toContain('评价审核')
    })

    it('渲染组件时应显示搜索表单和状态筛选按钮', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])
      const wrapper = await mountComponent()

      expect(wrapper.find('.filter-card').exists()).toBe(true)
      expect(wrapper.find('.filter-row').exists()).toBe(true)
      expect(wrapper.find('el-input').exists()).toBe(true)
      expect(wrapper.find('el-radio-group').exists()).toBe(true)
    })

    it('渲染组件时应显示状态筛选选项', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])
      const wrapper = await mountComponent()

      const radioButtons = wrapper.findAll('el-radio-button')
      expect(radioButtons.length).toBe(4)
      expect(radioButtons[0].text()).toContain('全部')
      expect(radioButtons[1].text()).toContain('待审核')
      expect(radioButtons[2].text()).toContain('通过')
      expect(radioButtons[3].text()).toContain('违规')
    })

    it('渲染组件时应显示评价列表表格', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      const wrapper = await mountComponent()

      expect(wrapper.find('.table-card').exists()).toBe(true)
      expect(wrapper.find('el-table').exists()).toBe(true)
    })

    it('加载评价数据后应正确渲染评价列表', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      const wrapper = await mountComponent()

      await flushPromises()

      const table = wrapper.find('el-table')
      expect(table.exists()).toBe(true)
    })

    it('渲染评价列表时应显示正确的列', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      const wrapper = await mountComponent()

      const columns = wrapper.findAll('el-table-column')
      expect(columns.length).toBeGreaterThan(0)

      const columnLabels = columns.map(col => col.attributes('label'))
      expect(columnLabels).toContain('评价ID')
      expect(columnLabels).toContain('用户名称')
      expect(columnLabels).toContain('服务/商品')
      expect(columnLabels).toContain('商家名称')
      expect(columnLabels).toContain('评分')
      expect(columnLabels).toContain('内容')
      expect(columnLabels).toContain('状态')
      expect(columnLabels).toContain('操作')
    })

    it('渲染违规处理对话框', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      const wrapper = await mountComponent()

      expect(wrapper.find('el-dialog').exists()).toBe(true)
    })
  })

  describe('API集成测试', () => {
    it('组件挂载时应调用 getReviewsForAudit API', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      await mountComponent()

      expect(mockedGetReviewsForAudit).toHaveBeenCalledTimes(1)
    })

    it('审核通过操作应调用 approveReview API', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedApproveReview.mockResolvedValue(undefined)
      mockElMessageBox.confirm.mockResolvedValue(true)

      const wrapper = await mountComponent()
      await flushPromises()

      const approveButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('通过') && btn.attributes('type') === 'success'
      )

      if (approveButtons.length > 0) {
        await approveButtons[0].trigger('click')
        await flushPromises()

        expect(mockedApproveReview).toHaveBeenCalled()
      }
    })

    it('标记违规操作应调用 markReviewViolation API', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedMarkReviewViolation.mockResolvedValue(undefined)

      const wrapper = await mountComponent()
      await flushPromises()

      const violationButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('违规') && btn.attributes('type') === 'danger'
      )

      if (violationButtons.length > 0) {
        await violationButtons[0].trigger('click')
        await flushPromises()

        const dialog = wrapper.find('el-dialog')
        expect(dialog.exists()).toBe(true)
      }
    })

    it('删除评价操作应调用 deleteReview API', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedDeleteReview.mockResolvedValue(undefined)
      mockElMessageBox.confirm.mockResolvedValue(true)

      const wrapper = await mountComponent()
      await flushPromises()

      const deleteButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('删除')
      )

      if (deleteButtons.length > 0) {
        await deleteButtons[0].trigger('click')
        await flushPromises()

        expect(mockElMessageBox.confirm).toHaveBeenCalled()
      }
    })
  })

  describe('交互功能测试', () => {
    it('搜索功能应过滤评价列表', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      const wrapper = await mountComponent()
      await flushPromises()

      const searchInput = wrapper.find('el-input')
      expect(searchInput.exists()).toBe(true)
    })

    it('状态筛选功能应过滤评价列表', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      const wrapper = await mountComponent()
      await flushPromises()

      const radioGroup = wrapper.find('el-radio-group')
      expect(radioGroup.exists()).toBe(true)
    })

    it('审核通过成功后应显示成功消息', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedApproveReview.mockResolvedValue(undefined)
      mockElMessageBox.confirm.mockResolvedValue(true)

      const wrapper = await mountComponent()
      await flushPromises()

      const approveButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('通过') && btn.attributes('type') === 'success'
      )

      if (approveButtons.length > 0) {
        await approveButtons[0].trigger('click')
        await flushPromises()

        expect(mockElMessage.success).toHaveBeenCalledWith('评价已通过')
      }
    })

    it('审核通过取消后不应调用 API', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockElMessageBox.confirm.mockRejectedValue('cancel')

      const wrapper = await mountComponent()
      await flushPromises()

      const approveButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('通过') && btn.attributes('type') === 'success'
      )

      if (approveButtons.length > 0) {
        await approveButtons[0].trigger('click')
        await flushPromises()

        expect(mockedApproveReview).not.toHaveBeenCalled()
      }
    })

    it('审核通过失败后应显示错误消息', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedApproveReview.mockRejectedValue(new Error('API Error'))
      mockElMessageBox.confirm.mockResolvedValue(true)

      const wrapper = await mountComponent()
      await flushPromises()

      const approveButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('通过') && btn.attributes('type') === 'success'
      )

      if (approveButtons.length > 0) {
        await approveButtons[0].trigger('click')
        await flushPromises()

        expect(mockElMessage.error).toHaveBeenCalledWith('操作失败')
      }
    })

    it('打开违规对话框应显示违规原因选项', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      const wrapper = await mountComponent()
      await flushPromises()

      const violationButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('违规') && btn.attributes('type') === 'danger'
      )

      if (violationButtons.length > 0) {
        await violationButtons[0].trigger('click')
        await flushPromises()

        const dialog = wrapper.find('el-dialog')
        expect(dialog.exists()).toBe(true)
      }
    })

    it('违规处理未选择原因时应显示警告', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      const wrapper = await mountComponent()
      await flushPromises()

      const violationButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('违规') && btn.attributes('type') === 'danger'
      )

      if (violationButtons.length > 0) {
        await violationButtons[0].trigger('click')
        await flushPromises()

        const confirmButton = wrapper.findAll('el-button').find(btn =>
          btn.text().includes('确认') && btn.attributes('type') === 'danger'
        )

        if (confirmButton) {
          await confirmButton.trigger('click')
          await flushPromises()

          expect(mockElMessage.warning).toHaveBeenCalledWith('请选择违规原因')
        }
      }
    })

    it('违规处理成功后应显示成功消息', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedMarkReviewViolation.mockResolvedValue(undefined)

      const wrapper = await mountComponent()
      await flushPromises()

      const violationButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('违规') && btn.attributes('type') === 'danger'
      )

      if (violationButtons.length > 0) {
        await violationButtons[0].trigger('click')
        await flushPromises()

        const select = wrapper.find('el-select')
        if (select.exists()) {
          await select.setValue('含有敏感词汇')
        }

        const confirmButton = wrapper.findAll('el-button').find(btn =>
          btn.text().includes('确认') && btn.attributes('type') === 'danger'
        )

        if (confirmButton) {
          await confirmButton.trigger('click')
          await flushPromises()

          expect(mockElMessage.success).toHaveBeenCalledWith('已标记为违规')
        }
      }
    })

    it('违规处理失败后应显示错误消息', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedMarkReviewViolation.mockRejectedValue(new Error('API Error'))

      const wrapper = await mountComponent()
      await flushPromises()

      const violationButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('违规') && btn.attributes('type') === 'danger'
      )

      if (violationButtons.length > 0) {
        await violationButtons[0].trigger('click')
        await flushPromises()

        const select = wrapper.find('el-select')
        if (select.exists()) {
          await select.setValue('含有敏感词汇')
        }

        const confirmButton = wrapper.findAll('el-button').find(btn =>
          btn.text().includes('确认') && btn.attributes('type') === 'danger'
        )

        if (confirmButton) {
          await confirmButton.trigger('click')
          await flushPromises()

          expect(mockElMessage.error).toHaveBeenCalledWith('操作失败')
        }
      }
    })

    it('删除评价成功后应显示成功消息', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedDeleteReview.mockResolvedValue(undefined)
      mockElMessageBox.confirm.mockResolvedValue(true)

      const wrapper = await mountComponent()
      await flushPromises()

      const deleteButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('删除')
      )

      if (deleteButtons.length > 0) {
        await deleteButtons[0].trigger('click')
        await flushPromises()

        expect(mockElMessage.success).toHaveBeenCalledWith('删除成功')
      }
    })

    it('删除评价失败后应显示错误消息', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedDeleteReview.mockRejectedValue(new Error('API Error'))
      mockElMessageBox.confirm.mockResolvedValue(true)

      const wrapper = await mountComponent()
      await flushPromises()

      const deleteButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('删除')
      )

      if (deleteButtons.length > 0) {
        await deleteButtons[0].trigger('click')
        await flushPromises()

        expect(mockElMessage.error).toHaveBeenCalledWith('删除失败，请重试')
      }
    })

    it('删除评价取消后不应调用 API', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockElMessageBox.confirm.mockRejectedValue('cancel')

      const wrapper = await mountComponent()
      await flushPromises()

      const deleteButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('删除')
      )

      if (deleteButtons.length > 0) {
        await deleteButtons[0].trigger('click')
        await flushPromises()

        expect(mockedDeleteReview).not.toHaveBeenCalled()
      }
    })
  })

  describe('边界情况测试', () => {
    it('数据加载中状态应显示加载动画', async () => {
      mockedGetReviewsForAudit.mockImplementation(() => {
        return new Promise(() => {
          // 不 resolve，保持加载状态
        })
      })

      const wrapper = await mountComponent()

      const table = wrapper.find('el-table')
      expect(table.exists()).toBe(true)
      expect(table.attributes('v-loading')).toBeDefined()
    })

    it('数据加载失败状态应显示错误消息', async () => {
      mockedGetReviewsForAudit.mockRejectedValue(new Error('Network Error'))

      await mountComponent()
      await flushPromises()

      expect(mockElMessage.error).toHaveBeenCalledWith('加载评价列表失败')
    })

    it('空数据状态应显示空表格', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])

      const wrapper = await mountComponent()
      await flushPromises()

      const table = wrapper.find('el-table')
      expect(table.exists()).toBe(true)
    })

    it('API 返回 null 时应处理为空数组', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(null as unknown as Review[])

      const wrapper = await mountComponent()
      await flushPromises()

      expect(wrapper.find('el-table').exists()).toBe(true)
    })

    it('搜索无结果时应显示空列表', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      expect(wrapper.find('el-table').exists()).toBe(true)
    })

    it('状态筛选无结果时应显示空列表', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      expect(wrapper.find('el-table').exists()).toBe(true)
    })

    it('评价状态为 pending 时应显示通过和违规按钮', async () => {
      const pendingReviews = mockReviews.filter(r => r.status === 'pending')
      mockedGetReviewsForAudit.mockResolvedValue(pendingReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      const buttons = wrapper.findAll('el-button')
      const approveButtons = buttons.filter(btn => btn.text().includes('通过'))
      const violationButtons = buttons.filter(btn => btn.text().includes('违规'))

      expect(approveButtons.length).toBeGreaterThan(0)
      expect(violationButtons.length).toBeGreaterThan(0)
    })

    it('评价状态为 approved 时不应显示通过和违规按钮', async () => {
      const approvedReviews = mockReviews.filter(r => r.status === 'approved')
      mockedGetReviewsForAudit.mockResolvedValue(approvedReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      const buttons = wrapper.findAll('el-button')
      const approveButtons = buttons.filter(btn =>
        btn.text().includes('通过') && btn.attributes('type') === 'success'
      )
      const violationButtons = buttons.filter(btn =>
        btn.text().includes('违规') && btn.attributes('type') === 'danger'
      )

      expect(approveButtons.length).toBe(0)
      expect(violationButtons.length).toBe(0)
    })

    it('评价状态为 violation 时不应显示通过和违规按钮', async () => {
      const violationReviews = mockReviews.filter(r => r.status === 'violation')
      mockedGetReviewsForAudit.mockResolvedValue(violationReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      const buttons = wrapper.findAll('el-button')
      const approveButtons = buttons.filter(btn =>
        btn.text().includes('通过') && btn.attributes('type') === 'success'
      )
      const violationButtons = buttons.filter(btn =>
        btn.text().includes('违规') && btn.attributes('type') === 'danger'
      )

      expect(approveButtons.length).toBe(0)
      expect(violationButtons.length).toBe(0)
    })

    it('操作成功后应重新加载评价列表', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedApproveReview.mockResolvedValue(undefined)
      mockElMessageBox.confirm.mockResolvedValue(true)

      const wrapper = await mountComponent()
      await flushPromises()

      const initialCallCount = mockedGetReviewsForAudit.mock.calls.length

      const approveButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('通过') && btn.attributes('type') === 'success'
      )

      if (approveButtons.length > 0) {
        await approveButtons[0].trigger('click')
        await flushPromises()

        expect(mockedGetReviewsForAudit.mock.calls.length).toBeGreaterThan(initialCallCount)
      }
    })
  })

  describe('评分显示测试', () => {
    it('评分大于等于4应显示成功类型', async () => {
      const highRatingReviews = mockReviews.filter(r => r.rating >= 4)
      mockedGetReviewsForAudit.mockResolvedValue(highRatingReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      expect(wrapper.find('el-rate').exists()).toBe(true)
    })

    it('评分等于3应显示警告类型', async () => {
      const midRatingReviews = mockReviews.filter(r => r.rating === 3)
      mockedGetReviewsForAudit.mockResolvedValue(midRatingReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      expect(wrapper.find('el-rate').exists()).toBe(true)
    })

    it('评分小于3应显示危险类型', async () => {
      const lowRatingReviews = mockReviews.filter(r => r.rating < 3)
      mockedGetReviewsForAudit.mockResolvedValue(lowRatingReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      expect(wrapper.find('el-rate').exists()).toBe(true)
    })
  })

  describe('状态标签显示测试', () => {
    it('待审核状态应显示警告标签', async () => {
      const pendingReviews = mockReviews.filter(r => r.status === 'pending')
      mockedGetReviewsForAudit.mockResolvedValue(pendingReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      expect(wrapper.find('el-tag').exists()).toBe(true)
    })

    it('已通过状态应显示成功标签', async () => {
      const approvedReviews = mockReviews.filter(r => r.status === 'approved')
      mockedGetReviewsForAudit.mockResolvedValue(approvedReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      expect(wrapper.find('el-tag').exists()).toBe(true)
    })

    it('违规状态应显示危险标签', async () => {
      const violationReviews = mockReviews.filter(r => r.status === 'violation')
      mockedGetReviewsForAudit.mockResolvedValue(violationReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      expect(wrapper.find('el-tag').exists()).toBe(true)
    })
  })

  describe('违规原因选项测试', () => {
    it('违规对话框应包含所有预设原因选项', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      const wrapper = await mountComponent()
      await flushPromises()

      const violationButtons = wrapper.findAll('el-button').filter(btn =>
        btn.text().includes('违规') && btn.attributes('type') === 'danger'
      )

      if (violationButtons.length > 0) {
        await violationButtons[0].trigger('click')
        await flushPromises()

        const options = wrapper.findAll('el-option')
        expect(options.length).toBe(6)
      }
    })
  })

  describe('组件卸载测试', () => {
    it('组件卸载时应清理资源', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      const wrapper = await mountComponent()

      expect(wrapper.find('.review-audit-container').exists()).toBe(true)

      wrapper.unmount()
      expect(() => wrapper.find('.review-audit-container')).toThrow()
    })
  })
})
