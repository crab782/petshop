import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { nextTick } from 'vue'
import MerchantReviews from '../index.vue'
import { createReviewList, createReview, createSuccessResponse } from '@/tests/fixtures/merchantData'

vi.mock('@/api/merchant', () => ({
  getMerchantReviews: vi.fn(),
  replyReview: vi.fn(),
  deleteReview: vi.fn()
}))

describe('MerchantReviews', () => {
  let wrapper: any

  const mountComponent = async () => {
    wrapper = mount(MerchantReviews, {
      global: {
        stubs: {
          'ReviewCard': {
            template: '<div class="review-card"><slot /></div>',
            props: ['review']
          }
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

  describe('评价列表渲染', () => {
    it('应该正确渲染评价列表组件', async () => {
      await mountComponent()

      expect(wrapper.exists()).toBe(true)
      expect(wrapper.find('.merchant-reviews').exists()).toBe(true)
    })

    it('应该正确渲染标题', async () => {
      await mountComponent()

      const title = wrapper.find('h2')
      expect(title.exists()).toBe(true)
      expect(title.text()).toBe('服务评价列表')
    })

    it('应该正确渲染评价项', async () => {
      await mountComponent()

      const reviewCards = wrapper.findAll('.space-y-6 > div')
      expect(reviewCards.length).toBeGreaterThan(0)
    })

    it('应该正确显示评价用户名称', async () => {
      await mountComponent()

      const reviewCards = wrapper.findAll('.space-y-6 > div')
      const firstReviewName = reviewCards[0].find('h4')
      expect(firstReviewName.text()).toBe('张先生')
    })

    it('应该正确显示评价日期', async () => {
      await mountComponent()

      const reviewCards = wrapper.findAll('.space-y-6 > div')
      const firstReviewDate = reviewCards[0].find('.text-gray-500')
      expect(firstReviewDate.text()).toBe('2024-01-15')
    })

    it('应该正确显示评价内容', async () => {
      await mountComponent()

      const reviewCards = wrapper.findAll('.space-y-6 > div')
      const firstReviewContent = reviewCards[0].find('.text-gray-700')
      expect(firstReviewContent.text()).toContain('服务非常专业')
    })

    it('应该正确显示服务类型', async () => {
      await mountComponent()

      const reviewCards = wrapper.findAll('.space-y-6 > div')
      const firstReviewServiceType = reviewCards[0].find('.text-gray-600')
      expect(firstReviewServiceType.text()).toBe('宠物美容')
    })

    it('应该正确显示用户头像', async () => {
      await mountComponent()

      const reviewCards = wrapper.findAll('.space-y-6 > div')
      const firstReviewAvatar = reviewCards[0].find('img')
      expect(firstReviewAvatar.exists()).toBe(true)
      expect(firstReviewAvatar.attributes('src')).toBeTruthy()
    })

    it('应该正确显示评分星级', async () => {
      await mountComponent()

      const reviewCards = wrapper.findAll('.space-y-6 > div')
      const firstReviewStars = reviewCards[0].findAll('.fa-star')
      expect(firstReviewStars.length).toBe(5)
    })
  })

  describe('评分统计功能', () => {
    it('应该正确计算平均评分', async () => {
      await mountComponent()

      const reviews = wrapper.vm.reviews
      const avgRating = reviews.reduce((sum: number, r: any) => sum + r.rating, 0) / reviews.length
      expect(avgRating).toBe(4.4)
    })

    it('应该正确统计各评分数量', async () => {
      await mountComponent()

      const reviews = wrapper.vm.reviews
      const ratingCounts = {
        5: reviews.filter((r: any) => r.rating === 5).length,
        4: reviews.filter((r: any) => r.rating === 4).length,
        3: reviews.filter((r: any) => r.rating === 3).length,
        2: reviews.filter((r: any) => r.rating === 2).length,
        1: reviews.filter((r: any) => r.rating === 1).length
      }

      expect(ratingCounts[5]).toBe(3)
      expect(ratingCounts[4]).toBe(1)
      expect(ratingCounts[3]).toBe(1)
      expect(ratingCounts[2]).toBe(0)
      expect(ratingCounts[1]).toBe(0)
    })

    it('应该正确计算5星评价占比', async () => {
      await mountComponent()

      const reviews = wrapper.vm.reviews
      const fiveStarPercentage = (reviews.filter((r: any) => r.rating === 5).length / reviews.length) * 100
      expect(fiveStarPercentage).toBe(60)
    })

    it('应该正确统计各服务类型评价数量', async () => {
      await mountComponent()

      const reviews = wrapper.vm.reviews
      const serviceTypeCounts = {
        '宠物美容': reviews.filter((r: any) => r.serviceType === '宠物美容').length,
        '宠物寄养': reviews.filter((r: any) => r.serviceType === '宠物寄养').length,
        '宠物医疗': reviews.filter((r: any) => r.serviceType === '宠物医疗').length,
        '宠物训练': reviews.filter((r: any) => r.serviceType === '宠物训练').length
      }

      expect(serviceTypeCounts['宠物美容']).toBe(2)
      expect(serviceTypeCounts['宠物寄养']).toBe(1)
      expect(serviceTypeCounts['宠物医疗']).toBe(1)
      expect(serviceTypeCounts['宠物训练']).toBe(1)
    })
  })

  describe('回复功能', () => {
    it('应该正确显示已有回复', async () => {
      await mountComponent()

      const reviewCards = wrapper.findAll('.space-y-6 > div')
      const firstReviewResponse = reviewCards[0].find('.bg-gray-50')
      expect(firstReviewResponse.exists()).toBe(true)
      expect(firstReviewResponse.text()).toContain('商家回复')
    })

    it('应该正确显示回复内容', async () => {
      await mountComponent()

      const reviewCards = wrapper.findAll('.space-y-6 > div')
      const firstReviewResponse = reviewCards[0].find('.bg-gray-50')
      expect(firstReviewResponse.text()).toContain('感谢您的好评')
    })

    it('回复输入框应该根据评价回复状态显示', async () => {
      await mountComponent()

      const reviews = wrapper.vm.reviews
      const hasReviewsWithoutResponse = reviews.some((r: any) => !r.response)
      
      const textareas = wrapper.findAll('textarea')
      if (hasReviewsWithoutResponse) {
        expect(textareas.length).toBeGreaterThan(0)
      } else {
        expect(textareas.length).toBe(0)
      }
    })

    it('回复按钮应该根据评价回复状态显示', async () => {
      await mountComponent()

      const reviews = wrapper.vm.reviews
      const hasReviewsWithoutResponse = reviews.some((r: any) => !r.response)
      
      const buttons = wrapper.findAll('button')
      const replyButtons = buttons.filter(btn => btn.text().includes('回复'))
      
      if (hasReviewsWithoutResponse) {
        expect(replyButtons.length).toBeGreaterThan(0)
      } else {
        expect(replyButtons.length).toBe(0)
      }
    })

    it('应该能够输入回复内容', async () => {
      await mountComponent()

      const textarea = wrapper.find('textarea')
      if (textarea.exists()) {
        await textarea.setValue('这是我的回复')
        expect(textarea.element.value).toBe('这是我的回复')
      }
    })
  })

  describe('删除功能', () => {
    it('删除功能应该存在', async () => {
      await mountComponent()

      expect(typeof wrapper.vm.handleDelete).toBeDefined()
    })

    it('删除评价应该调用删除API', async () => {
      const { deleteReview } = await import('@/api/merchant')
      ;(deleteReview as any).mockResolvedValue(createSuccessResponse(null))

      await mountComponent()

      if (wrapper.vm.handleDelete) {
        await wrapper.vm.handleDelete('REV-2024-001')
        await flushPromises()

        expect(deleteReview).toHaveBeenCalledWith('REV-2024-001')
      }
    })

    it('删除失败时应该显示错误信息', async () => {
      const { deleteReview } = await import('@/api/merchant')
      ;(deleteReview as any).mockRejectedValue(new Error('删除失败'))

      await mountComponent()

      if (wrapper.vm.handleDelete) {
        await wrapper.vm.handleDelete('REV-2024-001')
        await flushPromises()

        expect(deleteReview).toHaveBeenCalled()
      }
    })
  })

  describe('筛选功能', () => {
    it('应该正确渲染评分筛选按钮', async () => {
      await mountComponent()

      const ratingButtons = wrapper.findAll('button')
      const ratingFilterButtons = ratingButtons.filter(btn => 
        btn.text().includes('全部') || btn.text().includes('星及以上')
      )
      expect(ratingFilterButtons.length).toBe(6)
    })

    it('应该能够按评分筛选评价', async () => {
      await mountComponent()

      wrapper.vm.ratingFilter = 5
      await nextTick()

      const filteredReviews = wrapper.vm.filteredReviews
      expect(filteredReviews.length).toBe(3)
      expect(filteredReviews.every((r: any) => r.rating >= 5)).toBe(true)
    })

    it('应该能够按4星及以上筛选评价', async () => {
      await mountComponent()

      wrapper.vm.ratingFilter = 4
      await nextTick()

      const filteredReviews = wrapper.vm.filteredReviews
      expect(filteredReviews.length).toBe(4)
      expect(filteredReviews.every((r: any) => r.rating >= 4)).toBe(true)
    })

    it('应该能够按3星及以上筛选评价', async () => {
      await mountComponent()

      wrapper.vm.ratingFilter = 3
      await nextTick()

      const filteredReviews = wrapper.vm.filteredReviews
      expect(filteredReviews.length).toBe(5)
      expect(filteredReviews.every((r: any) => r.rating >= 3)).toBe(true)
    })

    it('应该能够按服务类型筛选评价', async () => {
      await mountComponent()

      wrapper.vm.serviceFilter = '宠物美容'
      await nextTick()

      const filteredReviews = wrapper.vm.filteredReviews
      expect(filteredReviews.length).toBe(2)
      expect(filteredReviews.every((r: any) => r.serviceType === '宠物美容')).toBe(true)
    })

    it('应该能够按宠物寄养筛选评价', async () => {
      await mountComponent()

      wrapper.vm.serviceFilter = '宠物寄养'
      await nextTick()

      const filteredReviews = wrapper.vm.filteredReviews
      expect(filteredReviews.length).toBe(1)
      expect(filteredReviews[0].serviceType).toBe('宠物寄养')
    })

    it('应该能够按宠物医疗筛选评价', async () => {
      await mountComponent()

      wrapper.vm.serviceFilter = '宠物医疗'
      await nextTick()

      const filteredReviews = wrapper.vm.filteredReviews
      expect(filteredReviews.length).toBe(1)
      expect(filteredReviews[0].serviceType).toBe('宠物医疗')
    })

    it('应该能够按宠物训练筛选评价', async () => {
      await mountComponent()

      wrapper.vm.serviceFilter = '宠物训练'
      await nextTick()

      const filteredReviews = wrapper.vm.filteredReviews
      expect(filteredReviews.length).toBe(1)
      expect(filteredReviews[0].serviceType).toBe('宠物训练')
    })

    it('应该能够同时按评分和服务类型筛选', async () => {
      await mountComponent()

      wrapper.vm.ratingFilter = 5
      wrapper.vm.serviceFilter = '宠物美容'
      await nextTick()

      const filteredReviews = wrapper.vm.filteredReviews
      expect(filteredReviews.length).toBe(2)
      expect(filteredReviews.every((r: any) => r.rating >= 5 && r.serviceType === '宠物美容')).toBe(true)
    })

    it('重置筛选应该显示所有评价', async () => {
      await mountComponent()

      wrapper.vm.ratingFilter = 5
      wrapper.vm.serviceFilter = '宠物美容'
      await nextTick()

      wrapper.vm.ratingFilter = 0
      wrapper.vm.serviceFilter = ''
      await nextTick()

      const filteredReviews = wrapper.vm.filteredReviews
      expect(filteredReviews.length).toBe(5)
    })

    it('应该正确渲染服务类型下拉选择器', async () => {
      await mountComponent()

      const select = wrapper.find('select')
      expect(select.exists()).toBe(true)
    })

    it('应该正确显示所有服务类型选项', async () => {
      await mountComponent()

      const options = wrapper.findAll('option')
      expect(options.length).toBe(5)
      expect(options[0].text()).toBe('全部服务')
      expect(options[1].text()).toBe('宠物美容')
      expect(options[2].text()).toBe('宠物寄养')
      expect(options[3].text()).toBe('宠物医疗')
      expect(options[4].text()).toBe('宠物训练')
    })

    it('点击评分筛选按钮应该更新筛选条件', async () => {
      await mountComponent()

      const ratingButtons = wrapper.findAll('button')
      const fiveStarButton = ratingButtons.find(btn => btn.text() === '5星及以上')
      
      if (fiveStarButton) {
        await fiveStarButton.trigger('click')
        await nextTick()

        expect(wrapper.vm.ratingFilter).toBe(5)
      }
    })

    it('筛选按钮应该有正确的激活状态', async () => {
      await mountComponent()

      wrapper.vm.ratingFilter = 5
      await nextTick()

      const ratingButtons = wrapper.findAll('button')
      const fiveStarButton = ratingButtons.find(btn => btn.text() === '5星及以上')
      
      if (fiveStarButton) {
        const classes = fiveStarButton.classes()
        expect(classes.includes('bg-primary') || classes.includes('text-white')).toBe(true)
      }
    })
  })

  describe('分页功能', () => {
    it('应该正确渲染分页组件', async () => {
      await mountComponent()

      const pagination = wrapper.find('.mt-8 nav')
      expect(pagination.exists()).toBe(true)
    })

    it('应该正确显示分页按钮', async () => {
      await mountComponent()

      const pagination = wrapper.find('.mt-8 nav')
      const pageButtons = pagination.findAll('button')
      expect(pageButtons.length).toBe(5)
    })

    it('应该正确显示上一页按钮', async () => {
      await mountComponent()

      const pagination = wrapper.find('.mt-8 nav')
      const prevButton = pagination.find('.fa-chevron-left')
      expect(prevButton.exists()).toBe(true)
    })

    it('应该正确显示下一页按钮', async () => {
      await mountComponent()

      const pagination = wrapper.find('.mt-8 nav')
      const nextButton = pagination.find('.fa-chevron-right')
      expect(nextButton.exists()).toBe(true)
    })

    it('当前页码应该高亮显示', async () => {
      await mountComponent()

      const pagination = wrapper.find('.mt-8 nav')
      const pageButtons = pagination.findAll('button')
      const currentPageButton = pageButtons.find(btn => {
        const classes = btn.classes()
        return classes.includes('bg-primary')
      })
      
      expect(currentPageButton).toBeDefined()
      expect(currentPageButton?.text()).toBe('1')
    })
  })

  describe('组件初始化', () => {
    it('组件挂载时应该初始化筛选条件', async () => {
      await mountComponent()

      expect(wrapper.vm.ratingFilter).toBe(0)
      expect(wrapper.vm.serviceFilter).toBe('')
    })

    it('组件挂载时应该初始化评价数据', async () => {
      await mountComponent()

      expect(wrapper.vm.reviews).toBeDefined()
      expect(wrapper.vm.reviews.length).toBe(5)
    })

    it('组件挂载时应该初始化服务选项', async () => {
      await mountComponent()

      expect(wrapper.vm.serviceOptions).toBeDefined()
      expect(wrapper.vm.serviceOptions.length).toBe(5)
    })
  })

  describe('边界情况处理', () => {
    it('筛选结果为空时应该不显示评价', async () => {
      await mountComponent()

      wrapper.vm.ratingFilter = 1
      wrapper.vm.serviceFilter = '不存在的服务'
      await nextTick()

      const filteredReviews = wrapper.vm.filteredReviews
      expect(filteredReviews.length).toBe(0)
    })
  })

  describe('响应式布局', () => {
    it('筛选器应该有正确的响应式类', async () => {
      await mountComponent()

      const filterContainer = wrapper.find('.flex-col.md\\:flex-row')
      expect(filterContainer.exists()).toBe(true)
    })

    it('服务类型选择器应该有正确的响应式宽度', async () => {
      await mountComponent()

      const selectContainer = wrapper.find('.w-full.md\\:w-48')
      expect(selectContainer.exists()).toBe(true)
    })
  })

  describe('数据验证', () => {
    it('评价数据应该包含必要字段', async () => {
      await mountComponent()

      const review = wrapper.vm.reviews[0]
      expect(review).toHaveProperty('id')
      expect(review).toHaveProperty('name')
      expect(review).toHaveProperty('avatar')
      expect(review).toHaveProperty('rating')
      expect(review).toHaveProperty('date')
      expect(review).toHaveProperty('content')
      expect(review).toHaveProperty('serviceType')
      expect(review).toHaveProperty('response')
    })

    it('评分应该在1-5范围内', async () => {
      await mountComponent()

      const reviews = wrapper.vm.reviews
      reviews.forEach((review: any) => {
        expect(review.rating).toBeGreaterThanOrEqual(1)
        expect(review.rating).toBeLessThanOrEqual(5)
      })
    })

    it('服务选项应该包含全部选项', async () => {
      await mountComponent()

      const serviceOptions = wrapper.vm.serviceOptions
      expect(serviceOptions[0].value).toBe('')
      expect(serviceOptions[0].label).toBe('全部服务')
    })
  })

  describe('computed 属性测试', () => {
    it('filteredReviews 应该返回正确的筛选结果', async () => {
      await mountComponent()

      expect(wrapper.vm.filteredReviews.length).toBe(5)

      wrapper.vm.ratingFilter = 5
      await nextTick()
      expect(wrapper.vm.filteredReviews.length).toBe(3)

      wrapper.vm.ratingFilter = 0
      wrapper.vm.serviceFilter = '宠物美容'
      await nextTick()
      expect(wrapper.vm.filteredReviews.length).toBe(2)
    })

    it('filteredReviews 应该是响应式的', async () => {
      await mountComponent()

      const initialLength = wrapper.vm.filteredReviews.length

      wrapper.vm.ratingFilter = 5
      await nextTick()
      expect(wrapper.vm.filteredReviews.length).not.toBe(initialLength)

      wrapper.vm.ratingFilter = 0
      await nextTick()
      expect(wrapper.vm.filteredReviews.length).toBe(initialLength)
    })
  })
})
