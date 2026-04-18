import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import ReviewAudit from '../index.vue'
import type { Review } from '@/api/admin'

vi.mock('element-plus', async (importOriginal) => {
  const actual = await importOriginal()
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      error: vi.fn(),
      warning: vi.fn(),
      info: vi.fn()
    },
    ElMessageBox: {
      confirm: vi.fn()
    },
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

import { ElMessage, ElMessageBox } from 'element-plus'

const mockedGetReviewsForAudit = vi.mocked(getReviewsForAudit)
const mockedApproveReview = vi.mocked(approveReview)
const mockedMarkReviewViolation = vi.mocked(markReviewViolation)
const mockedDeleteReview = vi.mocked(deleteReview)

const mockElMessage = vi.mocked(ElMessage)
const mockElMessageBox = vi.mocked(ElMessageBox)

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
    it('渲染组件时应显示容器', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])
      const wrapper = await mountComponent()

      expect(wrapper.find('.review-audit-container').exists()).toBe(true)
    })

    it('渲染组件时应显示筛选卡片', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])
      const wrapper = await mountComponent()

      expect(wrapper.find('.filter-card').exists()).toBe(true)
      expect(wrapper.find('.filter-row').exists()).toBe(true)
    })

    it('渲染组件时应显示表格卡片', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      const wrapper = await mountComponent()

      expect(wrapper.find('.table-card').exists()).toBe(true)
    })

    it('组件应包含正确的文本内容', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])
      const wrapper = await mountComponent()

      expect(wrapper.text()).toContain('首页')
      expect(wrapper.text()).toContain('评价审核')
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

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleApprove(vm.reviewList[0])
        await flushPromises()

        expect(mockedApproveReview).toHaveBeenCalled()
      }
    })

    it('标记违规操作应打开对话框', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedMarkReviewViolation.mockResolvedValue(undefined)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleViolation(vm.reviewList[0])
        await flushPromises()

        expect(vm.violationDialogVisible).toBe(true)
      }
    })

    it('删除评价操作应调用 ElMessageBox.confirm', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedDeleteReview.mockResolvedValue(undefined)
      mockElMessageBox.confirm.mockResolvedValue(true)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleDelete(vm.reviewList[0])
        await flushPromises()

        expect(mockElMessageBox.confirm).toHaveBeenCalled()
      }
    })
  })

  describe('交互功能测试', () => {
    it('审核通过成功后应显示成功消息', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedApproveReview.mockResolvedValue(undefined)
      mockElMessageBox.confirm.mockResolvedValue(true)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleApprove(vm.reviewList[0])
        await flushPromises()

        expect(mockElMessage.success).toHaveBeenCalledWith('评价已通过')
      }
    })

    it('审核通过取消后不应调用 API', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockElMessageBox.confirm.mockRejectedValue('cancel')

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleApprove(vm.reviewList[0])
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

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleApprove(vm.reviewList[0])
        await flushPromises()

        expect(mockElMessage.error).toHaveBeenCalledWith('操作失败')
      }
    })

    it('违规处理未选择原因时应显示警告', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleViolation(vm.reviewList[0])
        await flushPromises()

        await vm.confirmViolation()
        await flushPromises()

        expect(mockElMessage.warning).toHaveBeenCalledWith('请选择违规原因')
      }
    })

    it('违规处理成功后应显示成功消息', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedMarkReviewViolation.mockResolvedValue(undefined)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleViolation(vm.reviewList[0])
        await flushPromises()

        vm.violationReason = '含有敏感词汇'
        await vm.confirmViolation()
        await flushPromises()

        expect(mockElMessage.success).toHaveBeenCalledWith('已标记为违规')
      }
    })

    it('违规处理失败后应显示错误消息', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedMarkReviewViolation.mockRejectedValue(new Error('API Error'))

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleViolation(vm.reviewList[0])
        await flushPromises()

        vm.violationReason = '含有敏感词汇'
        await vm.confirmViolation()
        await flushPromises()

        expect(mockElMessage.error).toHaveBeenCalledWith('操作失败')
      }
    })

    it('删除评价成功后应显示成功消息', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedDeleteReview.mockResolvedValue(undefined)
      mockElMessageBox.confirm.mockResolvedValue(true)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleDelete(vm.reviewList[0])
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

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleDelete(vm.reviewList[0])
        await flushPromises()

        expect(mockElMessage.error).toHaveBeenCalledWith('删除失败，请重试')
      }
    })

    it('删除评价取消后不应调用 API', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockElMessageBox.confirm.mockRejectedValue('cancel')

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleDelete(vm.reviewList[0])
        await flushPromises()

        expect(mockedDeleteReview).not.toHaveBeenCalled()
      }
    })
  })

  describe('边界情况测试', () => {
    it('数据加载失败状态应显示错误消息', async () => {
      mockedGetReviewsForAudit.mockRejectedValue(new Error('Network Error'))

      await mountComponent()
      await flushPromises()

      expect(mockElMessage.error).toHaveBeenCalledWith('加载评价列表失败')
    })

    it('空数据状态应正确处理', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any
      expect(vm.reviewList).toEqual([])
    })

    it('API 返回 null 时应处理为空数组', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(null as unknown as Review[])

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any
      expect(vm.reviewList).toEqual([])
    })

    it('操作成功后应重新加载评价列表', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)
      mockedApproveReview.mockResolvedValue(undefined)
      mockElMessageBox.confirm.mockResolvedValue(true)

      const wrapper = await mountComponent()
      await flushPromises()

      const initialCallCount = mockedGetReviewsForAudit.mock.calls.length

      const vm = wrapper.vm as any
      if (vm.reviewList && vm.reviewList.length > 0) {
        await vm.handleApprove(vm.reviewList[0])
        await flushPromises()

        expect(mockedGetReviewsForAudit.mock.calls.length).toBeGreaterThan(initialCallCount)
      }
    })
  })

  describe('状态筛选功能测试', () => {
    it('状态筛选应正确过滤列表', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any

      vm.statusFilter = 'pending'
      await flushPromises()
      expect(vm.filteredList.every((r: Review) => r.status === 'pending')).toBe(true)

      vm.statusFilter = 'approved'
      await flushPromises()
      expect(vm.filteredList.every((r: Review) => r.status === 'approved')).toBe(true)

      vm.statusFilter = 'violation'
      await flushPromises()
      expect(vm.filteredList.every((r: Review) => r.status === 'violation')).toBe(true)

      vm.statusFilter = 'all'
      await flushPromises()
      expect(vm.filteredList.length).toBe(mockReviews.length)
    })
  })

  describe('搜索功能测试', () => {
    it('搜索应正确过滤列表', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any

      vm.searchQuery = '张三'
      await flushPromises()
      expect(vm.filteredList.every((r: Review) =>
        r.userName.includes('张三') ||
        r.serviceName.includes('张三') ||
        r.merchantName.includes('张三') ||
        r.comment.includes('张三')
      )).toBe(true)

      vm.searchQuery = '宠物美容'
      await flushPromises()
      expect(vm.filteredList.every((r: Review) =>
        r.userName.toLowerCase().includes('宠物美容') ||
        r.serviceName.toLowerCase().includes('宠物美容') ||
        r.merchantName.toLowerCase().includes('宠物美容') ||
        r.comment.toLowerCase().includes('宠物美容')
      )).toBe(true)
    })

    it('搜索无结果时应返回空列表', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any

      vm.searchQuery = '不存在的关键词12345'
      await flushPromises()
      expect(vm.filteredList.length).toBe(0)
    })
  })

  describe('评分类型测试', () => {
    it('getRatingType 应返回正确的类型', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any

      expect(vm.getRatingType(5)).toBe('success')
      expect(vm.getRatingType(4)).toBe('success')
      expect(vm.getRatingType(3)).toBe('warning')
      expect(vm.getRatingType(2)).toBe('danger')
      expect(vm.getRatingType(1)).toBe('danger')
    })
  })

  describe('违规原因选项测试', () => {
    it('应包含所有预设违规原因', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any

      expect(vm.violationReasons).toContain('含有敏感词汇')
      expect(vm.violationReasons).toContain('存在广告内容')
      expect(vm.violationReasons).toContain('恶意评价')
      expect(vm.violationReasons).toContain('内容不实')
      expect(vm.violationReasons).toContain('涉及隐私信息')
      expect(vm.violationReasons).toContain('其他违规原因')
    })
  })

  describe('状态映射测试', () => {
    it('statusMap 应包含正确的状态映射', async () => {
      mockedGetReviewsForAudit.mockResolvedValue(mockReviews)

      const wrapper = await mountComponent()
      await flushPromises()

      const vm = wrapper.vm as any

      expect(vm.statusMap.pending).toEqual({ label: '待审核', type: 'warning' })
      expect(vm.statusMap.approved).toEqual({ label: '通过', type: 'success' })
      expect(vm.statusMap.violation).toEqual({ label: '违规', type: 'danger' })
    })
  })

  describe('组件初始化测试', () => {
    it('组件初始化时 loading 应为 false', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])

      const wrapper = await mountComponent()
      const vm = wrapper.vm as any

      expect(vm.loading).toBe(false)
    })

    it('组件初始化时 searchQuery 应为空', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])

      const wrapper = await mountComponent()
      const vm = wrapper.vm as any

      expect(vm.searchQuery).toBe('')
    })

    it('组件初始化时 statusFilter 应为 all', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])

      const wrapper = await mountComponent()
      const vm = wrapper.vm as any

      expect(vm.statusFilter).toBe('all')
    })

    it('组件初始化时 violationDialogVisible 应为 false', async () => {
      mockedGetReviewsForAudit.mockResolvedValue([])

      const wrapper = await mountComponent()
      const vm = wrapper.vm as any

      expect(vm.violationDialogVisible).toBe(false)
    })
  })
})
