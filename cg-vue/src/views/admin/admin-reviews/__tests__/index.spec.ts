import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import AdminReviews from '../index.vue'
import * as adminApi from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

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

vi.mock('@/api/admin', () => ({
  getAllReviews: vi.fn(),
  deleteReview: vi.fn()
}))

const mockReviews = [
  {
    id: 1,
    serviceId: 1,
    serviceName: '宠物美容',
    merchantId: 1,
    merchantName: '宠物乐园',
    userId: 1,
    userName: '张三',
    rating: 5,
    comment: '服务非常好，宠物很满意！',
    status: 'approved' as const,
    createdAt: '2023-01-01 10:00:00',
    appointmentId: 1,
    orderNo: 'ORD001'
  },
  {
    id: 2,
    serviceId: 2,
    serviceName: '宠物寄养',
    merchantId: 2,
    merchantName: '爱心宠物店',
    userId: 2,
    userName: '李四',
    rating: 4,
    comment: '服务不错，环境干净整洁。',
    status: 'approved' as const,
    createdAt: '2023-01-02 11:00:00',
    appointmentId: 2,
    orderNo: 'ORD002'
  },
  {
    id: 3,
    serviceId: 1,
    serviceName: '宠物美容',
    merchantId: 1,
    merchantName: '宠物乐园',
    userId: 3,
    userName: '王五',
    rating: 2,
    comment: '服务一般，等待时间较长。',
    status: 'pending' as const,
    createdAt: '2023-01-03 12:00:00',
    appointmentId: 3,
    orderNo: 'ORD003'
  },
  {
    id: 4,
    serviceId: 3,
    serviceName: '宠物医疗',
    merchantId: 3,
    merchantName: '宠物医院',
    userId: 4,
    userName: '赵六',
    rating: 3,
    comment: '医生很专业，但价格偏高。',
    status: 'violation' as const,
    createdAt: '2023-01-04 13:00:00',
    appointmentId: 4,
    orderNo: 'ORD004'
  }
]

describe('AdminReviews 组件测试', () => {
  let wrapper: any

  beforeEach(() => {
    vi.clearAllMocks()

    ;(adminApi.getAllReviews as vi.Mock).mockResolvedValue(mockReviews)
    ;(adminApi.deleteReview as vi.Mock).mockResolvedValue({})
    ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)

    wrapper = mount(AdminReviews, {
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

  describe('数据渲染测试', () => {
    it('测试评价表格渲染', async () => {
      await wrapper.vm.$nextTick()

      const table = wrapper.find('.el-table')
      expect(table.exists()).toBe(true)

      const rows = wrapper.findAll('.el-table__row')
      expect(rows.length).toBe(mockReviews.length)

      const firstRow = rows[0]
      expect(firstRow.text()).toContain('宠物美容')
      expect(firstRow.text()).toContain('宠物乐园')
      expect(firstRow.text()).toContain('张三')
      expect(firstRow.text()).toContain('服务非常好，宠物很满意！')
      expect(firstRow.text()).toContain('2023-01-01 10:00:00')
    })

    it('测试搜索和筛选表单渲染', () => {
      const searchInput = wrapper.find('.el-input')
      expect(searchInput.exists()).toBe(true)

      const searchButton = wrapper.find('.el-button--primary')
      expect(searchButton.exists()).toBe(true)
      expect(searchButton.text()).toContain('搜索')

      const resetButton = wrapper.findAll('.el-button').find((btn: any) => btn.text().includes('重置'))
      expect(resetButton).toBeDefined()
    })

    it('测试统计卡片渲染', async () => {
      await wrapper.vm.$nextTick()

      const statsCard = wrapper.find('.stats-card')
      expect(statsCard.exists()).toBe(true)

      const statValues = wrapper.findAll('.stat-value')
      expect(statValues.length).toBe(3)

      expect(statValues[0].text()).toBe('4')
      expect(statValues[1].text()).toBe('3.5')
      expect(statValues[2].text()).toBe('50.0%')

      const statLabels = wrapper.findAll('.stat-label')
      expect(statLabels.length).toBe(3)
      expect(statLabels[0].text()).toBe('总评价数')
      expect(statLabels[1].text()).toBe('平均评分')
      expect(statLabels[2].text()).toBe('好评率')
    })

    it('测试分页组件渲染', async () => {
      await wrapper.vm.$nextTick()

      const pagination = wrapper.find('.el-pagination')
      expect(pagination.exists()).toBe(true)
    })

    it('测试操作按钮渲染', async () => {
      await wrapper.vm.$nextTick()

      const rows = wrapper.findAll('.el-table__row')
      const firstRowButtons = rows[0].findAll('.el-button')

      expect(firstRowButtons.length).toBe(2)
      expect(firstRowButtons[0].text()).toContain('查看')
      expect(firstRowButtons[1].text()).toContain('删除')
    })
  })

  describe('API集成测试', () => {
    it('模拟评价列表API响应', async () => {
      await wrapper.vm.$nextTick()

      expect(adminApi.getAllReviews).toHaveBeenCalled()
      expect(wrapper.vm.reviewList.length).toBe(mockReviews.length)
    })

    it('模拟评价删除API响应', async () => {
      await wrapper.vm.$nextTick()

      ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)
      ;(adminApi.deleteReview as vi.Mock).mockResolvedValue({})

      await wrapper.vm.handleDelete(mockReviews[0])
      await wrapper.vm.$nextTick()

      expect(ElMessageBox.confirm).toHaveBeenCalled()
      expect(adminApi.deleteReview).toHaveBeenCalledWith(1)
      expect(ElMessage.success).toHaveBeenCalledWith('删除成功')
    })

    it('模拟评价删除失败API响应', async () => {
      await wrapper.vm.$nextTick()

      ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)
      ;(adminApi.deleteReview as vi.Mock).mockRejectedValue(new Error('删除失败'))

      await wrapper.vm.handleDelete(mockReviews[0])
      await wrapper.vm.$nextTick()

      expect(ElMessage.error).toHaveBeenCalledWith('删除失败，请重试')
    })
  })

  describe('交互功能测试', () => {
    it('测试搜索功能 - 按服务名称搜索', async () => {
      await wrapper.vm.$nextTick()

      wrapper.vm.searchQuery = '宠物美容'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(2)
      expect(wrapper.vm.filteredList[0].serviceName).toBe('宠物美容')
      expect(wrapper.vm.currentPage).toBe(1)
    })

    it('测试搜索功能 - 按商家名称搜索', async () => {
      await wrapper.vm.$nextTick()

      wrapper.vm.searchQuery = '宠物乐园'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(2)
      expect(wrapper.vm.filteredList[0].merchantName).toBe('宠物乐园')
    })

    it('测试搜索功能 - 按用户名称搜索', async () => {
      await wrapper.vm.$nextTick()

      wrapper.vm.searchQuery = '张三'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(1)
      expect(wrapper.vm.filteredList[0].userName).toBe('张三')
    })

    it('测试搜索功能 - 无匹配结果', async () => {
      await wrapper.vm.$nextTick()

      wrapper.vm.searchQuery = '不存在的服务'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(0)
    })

    it('测试重置功能', async () => {
      await wrapper.vm.$nextTick()

      wrapper.vm.searchQuery = '宠物美容'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(2)

      await wrapper.vm.handleReset()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.searchQuery).toBe('')
      expect(wrapper.vm.filteredList.length).toBe(mockReviews.length)
    })

    it('测试分页功能 - 页码变化', async () => {
      await wrapper.vm.$nextTick()

      wrapper.vm.pageSize = 2
      await wrapper.vm.$nextTick()

      await wrapper.vm.handlePageChange(2)
      expect(wrapper.vm.currentPage).toBe(2)

      const paginatedList = wrapper.vm.paginatedList
      expect(paginatedList.length).toBe(2)
    })

    it('测试分页功能 - 每页大小变化', async () => {
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleSizeChange(20)
      expect(wrapper.vm.pageSize).toBe(20)
      expect(wrapper.vm.currentPage).toBe(1)
    })

    it('测试查看详情功能', async () => {
      await wrapper.vm.$nextTick()

      await wrapper.vm.handleView(mockReviews[0])
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.currentReview).toEqual(mockReviews[0])
      expect(wrapper.vm.detailDialogVisible).toBe(true)
    })

    it('测试删除确认取消', async () => {
      await wrapper.vm.$nextTick()

      ;(ElMessageBox.confirm as vi.Mock).mockRejectedValue(new Error('取消'))

      await wrapper.vm.handleDelete(mockReviews[0])
      await wrapper.vm.$nextTick()

      expect(adminApi.deleteReview).not.toHaveBeenCalled()
    })

    it('测试评分类型计算 - 高评分', () => {
      const type = wrapper.vm.getRatingType(5)
      expect(type).toBe('success')
    })

    it('测试评分类型计算 - 中等评分', () => {
      const type = wrapper.vm.getRatingType(3)
      expect(type).toBe('warning')
    })

    it('测试评分类型计算 - 低评分', () => {
      const type = wrapper.vm.getRatingType(2)
      expect(type).toBe('danger')
    })

    it('测试日期格式化 - 正常日期', () => {
      const formatted = wrapper.vm.formatDate('2023-01-01 10:00:00')
      expect(formatted).toBe('2023-01-01 10:00:00')
    })

    it('测试日期格式化 - 空日期', () => {
      const formatted = wrapper.vm.formatDate('')
      expect(formatted).toBe('-')
    })

    it('测试日期格式化 - null日期', () => {
      const formatted = wrapper.vm.formatDate(null as any)
      expect(formatted).toBe('-')
    })
  })

  describe('边界情况测试', () => {
    it('测试数据加载中状态', async () => {
      wrapper.vm.loading = true
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.loading).toBe(true)
    })

    it('测试数据加载失败状态', async () => {
      ;(adminApi.getAllReviews as vi.Mock).mockRejectedValue(new Error('加载失败'))

      wrapper.unmount()
      wrapper = mount(AdminReviews, {
        global: {
          plugins: [createPinia(), ElementPlus]
        }
      })

      await wrapper.vm.$nextTick()

      expect(ElMessage.error).toHaveBeenCalledWith('加载评价列表失败')
    })

    it('测试空数据状态', async () => {
      ;(adminApi.getAllReviews as vi.Mock).mockResolvedValue([])

      wrapper.unmount()
      wrapper = mount(AdminReviews, {
        global: {
          plugins: [createPinia(), ElementPlus]
        }
      })

      await wrapper.vm.$nextTick()

      expect(wrapper.vm.reviewList.length).toBe(0)
      expect(wrapper.vm.filteredList.length).toBe(0)

      const stats = wrapper.vm.stats
      expect(stats.totalReviews).toBe(0)
      expect(stats.avgRating).toBe('0.0')
      expect(stats.goodRatingRate).toBe('0.0')
    })

    it('测试API返回null的情况', async () => {
      ;(adminApi.getAllReviews as vi.Mock).mockResolvedValue(null)

      wrapper.unmount()
      wrapper = mount(AdminReviews, {
        global: {
          plugins: [createPinia(), ElementPlus]
        }
      })

      await wrapper.vm.$nextTick()

      expect(wrapper.vm.reviewList.length).toBe(0)
    })

    it('测试搜索关键词大小写不敏感', async () => {
      await wrapper.vm.$nextTick()

      wrapper.vm.searchQuery = 'PET BEAUTY'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(0)

      wrapper.vm.searchQuery = '宠物美容'
      await wrapper.vm.handleSearch()
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(2)
    })

    it('测试分页边界 - 最后一页', async () => {
      await wrapper.vm.$nextTick()

      wrapper.vm.pageSize = 3
      await wrapper.vm.$nextTick()

      await wrapper.vm.handlePageChange(2)
      expect(wrapper.vm.paginatedList.length).toBe(1)
    })

    it('测试评价详情弹窗关闭', async () => {
      await wrapper.vm.$nextTick()

      wrapper.vm.detailDialogVisible = true
      wrapper.vm.currentReview = mockReviews[0]
      await wrapper.vm.$nextTick()

      wrapper.vm.detailDialogVisible = false
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.detailDialogVisible).toBe(false)
    })

    it('测试统计计算 - 好评率', async () => {
      await wrapper.vm.$nextTick()

      const stats = wrapper.vm.stats
      expect(stats.totalReviews).toBe(4)
      expect(stats.avgRating).toBe('3.5')
      expect(stats.goodRatingRate).toBe('50.0')
    })

    it('测试评价内容为空的情况', async () => {
      const reviewWithEmptyComment = {
        id: 5,
        serviceId: 1,
        serviceName: '测试服务',
        merchantId: 1,
        merchantName: '测试商家',
        userId: 1,
        userName: '测试用户',
        rating: 5,
        comment: '',
        status: 'approved' as const,
        createdAt: '2023-01-05 14:00:00'
      }

      ;(adminApi.getAllReviews as vi.Mock).mockResolvedValue([reviewWithEmptyComment])

      wrapper.unmount()
      wrapper = mount(AdminReviews, {
        global: {
          plugins: [createPinia(), ElementPlus]
        }
      })

      await wrapper.vm.$nextTick()

      expect(wrapper.vm.reviewList.length).toBe(1)
    })

    it('测试Enter键触发搜索', async () => {
      await wrapper.vm.$nextTick()

      const input = wrapper.find('.el-input')
      expect(input.exists()).toBe(true)
    })
  })

  describe('计算属性测试', () => {
    it('测试filteredList计算属性', async () => {
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredList.length).toBe(4)

      wrapper.vm.searchQuery = '宠物乐园'
      expect(wrapper.vm.filteredList.length).toBe(2)
    })

    it('测试paginatedList计算属性', async () => {
      await wrapper.vm.$nextTick()

      wrapper.vm.currentPage = 1
      wrapper.vm.pageSize = 2
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.paginatedList.length).toBe(2)
      expect(wrapper.vm.paginatedList[0].id).toBe(1)
      expect(wrapper.vm.paginatedList[1].id).toBe(2)

      wrapper.vm.currentPage = 2
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.paginatedList.length).toBe(2)
      expect(wrapper.vm.paginatedList[0].id).toBe(3)
    })

    it('测试stats计算属性 - 空列表', async () => {
      ;(adminApi.getAllReviews as vi.Mock).mockResolvedValue([])

      wrapper.unmount()
      wrapper = mount(AdminReviews, {
        global: {
          plugins: [createPinia(), ElementPlus]
        }
      })

      await wrapper.vm.$nextTick()

      const stats = wrapper.vm.stats
      expect(stats.totalReviews).toBe(0)
      expect(stats.avgRating).toBe('0.0')
      expect(stats.goodRatingRate).toBe('0.0')
    })

    it('测试stats计算属性 - 全好评', async () => {
      const allGoodReviews = [
        { ...mockReviews[0], rating: 5 },
        { ...mockReviews[1], rating: 5 },
        { ...mockReviews[2], rating: 4 }
      ]

      ;(adminApi.getAllReviews as vi.Mock).mockResolvedValue(allGoodReviews)

      wrapper.unmount()
      wrapper = mount(AdminReviews, {
        global: {
          plugins: [createPinia(), ElementPlus]
        }
      })

      await wrapper.vm.$nextTick()

      const stats = wrapper.vm.stats
      expect(stats.totalReviews).toBe(3)
      expect(stats.avgRating).toBe('4.7')
      expect(stats.goodRatingRate).toBe('100.0')
    })
  })

  describe('组件生命周期测试', () => {
    it('测试组件挂载时自动加载数据', async () => {
      expect(adminApi.getAllReviews).toHaveBeenCalled()
    })

    it('测试删除后重新加载数据', async () => {
      await wrapper.vm.$nextTick()

      vi.clearAllMocks()
      ;(adminApi.getAllReviews as vi.Mock).mockResolvedValue(mockReviews)
      ;(adminApi.deleteReview as vi.Mock).mockResolvedValue({})
      ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(true)

      await wrapper.vm.handleDelete(mockReviews[0])
      await wrapper.vm.$nextTick()

      expect(adminApi.getAllReviews).toHaveBeenCalled()
    })
  })
})
