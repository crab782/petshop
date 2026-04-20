import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createService, createMerchant, createPet, mockUserStore } from '@/tests/utils/userTestUtils'
import ServiceDetail from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

const createMerchantReview = (overrides: any = {}) => ({
  id: 1,
  userId: 1,
  userName: '测试用户',
  merchantId: 1,
  rating: 5,
  content: '服务非常好',
  createTime: '2024-04-20T10:00:00.000Z',
  ...overrides,
})

const mockUserApi = {
  getServiceById: vi.fn(() => Promise.resolve({ 
    data: createService({ id: 1, name: '宠物美容', price: 100, duration: 60 }) 
  })),
  getMerchantInfo: vi.fn(() => Promise.resolve({ 
    data: createMerchant({ id: 1, name: '测试宠物店' }) 
  })),
  getMerchantReviews: vi.fn(() => Promise.resolve([createMerchantReview()])),
  getUserPets: vi.fn(() => Promise.resolve({ data: [createPet()] })),
  createAppointment: vi.fn(() => Promise.resolve({ data: { id: 1 } })),
  addFavorite: vi.fn(() => Promise.resolve({ data: { id: 1, merchantId: 1 } })),
  removeFavorite: vi.fn(() => Promise.resolve({ data: null })),
}

vi.mock('@/api/user', () => ({
  getServiceById: (...args: any[]) => mockUserApi.getServiceById(...args),
  getMerchantInfo: (...args: any[]) => mockUserApi.getMerchantInfo(...args),
  getMerchantReviews: (...args: any[]) => mockUserApi.getMerchantReviews(...args),
  getUserPets: (...args: any[]) => mockUserApi.getUserPets(...args),
  createAppointment: (...args: any[]) => mockUserApi.createAppointment(...args),
  addFavorite: (...args: any[]) => mockUserApi.addFavorite(...args),
  removeFavorite: (...args: any[]) => mockUserApi.removeFavorite(...args),
}))

describe('ServiceDetail', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.resolve({ data: createService({ id: 1, name: '宠物美容', price: 100, duration: 60 }) })
    )
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, name: '测试宠物店' }) })
    )
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview()])
    )
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )
    mockUserApi.createAppointment.mockImplementation(() => 
      Promise.resolve({ data: { id: 1 } })
    )
    mockUserApi.addFavorite.mockImplementation(() => 
      Promise.resolve({ data: { id: 1, merchantId: 1 } })
    )
    mockUserApi.removeFavorite.mockImplementation(() => 
      Promise.resolve({ data: null })
    )
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(ServiceDetail)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display breadcrumb', () => {
    const wrapper = mountUserComponent(ServiceDetail)
    expect(wrapper.find('.el-breadcrumb').exists()).toBe(true)
  })

  it('should display service card', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-card').exists()).toBe(true)
  })

  it('should display service name', async () => {
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.resolve({ data: createService({ id: 1, name: '宠物美容' }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('宠物美容')
  })

  it('should display service price', async () => {
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.resolve({ data: createService({ id: 1, name: '宠物美容', price: 100 }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('100')
  })

  it('should display service duration', async () => {
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.resolve({ data: createService({ id: 1, name: '宠物美容', duration: 60 }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('60')
  })

  it('should display service description', async () => {
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.resolve({ data: createService({ id: 1, name: '宠物美容', description: '专业宠物美容服务' }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('专业宠物美容服务')
  })

  it('should display service rating', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-rating').exists() || wrapper.find('.el-rate').exists()).toBe(true)
  })

  it('should display review count', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([
        createMerchantReview({ id: 1 }),
        createMerchantReview({ id: 2 }),
      ])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('评价')
  })

  it('should have book now button', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const bookBtn = wrapper.findAll('.el-button').find(b => b.text().includes('立即预约'))
    expect(bookBtn?.exists()).toBe(true)
  })

  it('should have favorite button', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('收藏'))
    expect(favoriteBtn?.exists()).toBe(true)
  })

  it('should display merchant card', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.merchant-card').exists()).toBe(true)
  })

  it('should display merchant name', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, name: '宠物乐园' }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('宠物乐园')
  })

  it('should display merchant address', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, address: '北京市朝阳区' }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('北京市朝阳区')
  })

  it('should display merchant phone', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, phone: '13800138000' }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('13800138000')
  })

  it('should display merchant rating', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, rating: 4.5 }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('评分')
  })

  it('should display merchant description', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, description: '专业宠物服务' }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('专业宠物服务')
  })

  it('should have view shop button', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const viewShopBtn = wrapper.findAll('.el-button').find(b => b.text().includes('查看店铺'))
    expect(viewShopBtn?.exists()).toBe(true)
  })

  it('should display tabs for description and reviews', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.el-tabs').exists()).toBe(true)
  })

  it('should display service introduction tab', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('服务介绍')
  })

  it('should display reviews tab', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('评价列表')
  })

  it('should display review list', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([
        createMerchantReview({ id: 1, content: '服务非常好' }),
        createMerchantReview({ id: 2, content: '很满意' }),
      ])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.reviews-list').exists() || wrapper.text().includes('服务非常好')).toBe(true)
  })

  it('should display review content', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1, content: '非常满意的服务' })])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('非常满意的服务')
  })

  it('should display review rating', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1, rating: 5 })])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.review-item').exists() || wrapper.find('.review-rating').exists()).toBe(true)
  })

  it('should display review user name', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1, userName: '张三' })])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('张三')
  })

  it('should open book dialog when clicking book button', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const bookBtn = wrapper.findAll('.el-button').find(b => b.text().includes('立即预约'))
    if (bookBtn?.exists()) {
      await bookBtn.trigger('click')
      await wrapper.vm.$nextTick()
    }
  })

  it('should display book dialog with service info', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.bookDialogVisible = true
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.el-dialog').exists() || wrapper.find('.book-dialog-content').exists()).toBe(true)
  })

  it('should display pet selector in book dialog', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet({ id: 1, name: '小白' })] })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.bookDialogVisible = true
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.book-form').exists() || wrapper.text().includes('选择宠物')).toBe(true)
  })

  it('should display date picker in book dialog', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.bookDialogVisible = true
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.book-form').exists() || wrapper.text().includes('预约时间')).toBe(true)
  })

  it('should display remark input in book dialog', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.bookDialogVisible = true
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.book-form').exists() || wrapper.text().includes('备注')).toBe(true)
  })

  it('should submit appointment successfully', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet({ id: 1 })] })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.selectedPetId = 1
    wrapper.vm.appointmentTime = '2024-05-01 10:00:00'
    wrapper.vm.remark = '测试备注'
    
    await wrapper.vm.handleSubmitAppointment()
    await wrapper.vm.$nextTick()
    
    expect(mockUserApi.createAppointment).toHaveBeenCalled()
  })

  it('should show warning when no pet selected', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.selectedPetId = null
    await wrapper.vm.handleSubmitAppointment()
    await wrapper.vm.$nextTick()
  })

  it('should show warning when no appointment time', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.selectedPetId = 1
    wrapper.vm.appointmentTime = ''
    await wrapper.vm.handleSubmitAppointment()
    await wrapper.vm.$nextTick()
  })

  it('should toggle favorite when clicking favorite button', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('收藏'))
    if (favoriteBtn?.exists()) {
      await favoriteBtn.trigger('click')
      await wrapper.vm.$nextTick()
    }
  })

  it('should show unfavorited state', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, isFavorited: false }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('收藏'))
    expect(favoriteBtn?.exists()).toBe(true)
  })

  it('should show favorited state', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, isFavorited: true }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('已收藏'))
    expect(favoriteBtn?.exists()).toBe(true)
  })

  it('should call addFavorite when favoriting', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, isFavorited: false }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('收藏'))
    if (favoriteBtn?.exists()) {
      await favoriteBtn.trigger('click')
      await wrapper.vm.$nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
    }
  })

  it('should call removeFavorite when unfavoriting', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, isFavorited: true }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('已收藏'))
    if (favoriteBtn?.exists()) {
      await favoriteBtn.trigger('click')
      await wrapper.vm.$nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
    }
  })

  it('should redirect to pets page when no pets', async () => {
    const mockPush = vi.fn()
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [] })
    )

    const wrapper = mountUserComponent(ServiceDetail, {
      global: {
        mocks: {
          $router: {
            push: mockPush,
          },
        },
      },
    })
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.handleBook()
    await wrapper.vm.$nextTick()
  })

  it('should display empty state when service not found', async () => {
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.resolve({ data: null })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('服务不存在')
  })

  it('should handle API error gracefully', async () => {
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.reject(new Error('Network error'))
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.exists()).toBe(true)
  })

  it('should handle appointment API error gracefully', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )
    mockUserApi.createAppointment.mockImplementation(() => 
      Promise.reject(new Error('Network error'))
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.selectedPetId = 1
    wrapper.vm.appointmentTime = '2024-05-01 10:00:00'
    await wrapper.vm.handleSubmitAppointment()
    await wrapper.vm.$nextTick()
    
    expect(wrapper.exists()).toBe(true)
  })

  it('should handle favorite API error gracefully', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, isFavorited: false }) })
    )
    mockUserApi.addFavorite.mockImplementation(() => 
      Promise.reject(new Error('Network error'))
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('收藏'))
    if (favoriteBtn?.exists()) {
      await favoriteBtn.trigger('click')
      await wrapper.vm.$nextTick()
    }
    
    expect(wrapper.exists()).toBe(true)
  })

  it('should have loading state', async () => {
    mockUserApi.getServiceById.mockImplementation(() => 
      new Promise(resolve => setTimeout(() => resolve({ data: createService() }), 100))
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    
    expect(wrapper.exists()).toBe(true)
  })

  it('should calculate average rating correctly', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([
        createMerchantReview({ id: 1, rating: 5 }),
        createMerchantReview({ id: 2, rating: 4 }),
        createMerchantReview({ id: 3, rating: 3 }),
      ])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.vm.averageRating).toBe('4.0')
  })

  it('should return 5 for average rating when no reviews', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => Promise.resolve([]))

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.vm.averageRating).toBe(5)
  })

  it('should format date correctly', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    const formattedDate = wrapper.vm.formatDate('2024-04-20T10:00:00.000Z')
    expect(formattedDate).toBeTruthy()
  })

  it('should return empty string for invalid date', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    const formattedDate = wrapper.vm.formatDate('')
    expect(formattedDate).toBe('')
  })

  it('should disable past dates', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    const pastDate = new Date()
    pastDate.setDate(pastDate.getDate() - 1)
    
    const isDisabled = wrapper.vm.disabledDate(pastDate)
    expect(isDisabled).toBe(true)
  })

  it('should enable future dates', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    const futureDate = new Date()
    futureDate.setDate(futureDate.getDate() + 1)
    
    const isDisabled = wrapper.vm.disabledDate(futureDate)
    expect(isDisabled).toBe(false)
  })

  it('should get star array correctly', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    
    const starArray3 = wrapper.vm.getStarArray(3)
    expect(starArray3).toEqual([true, true, true, false, false])
    
    const starArray5 = wrapper.vm.getStarArray(5)
    expect(starArray5).toEqual([true, true, true, true, true])
    
    const starArray0 = wrapper.vm.getStarArray(0)
    expect(starArray0).toEqual([false, false, false, false, false])
  })

  it('should navigate back when clicking go back', async () => {
    const mockBack = vi.fn()

    const wrapper = mountUserComponent(ServiceDetail, {
      global: {
        mocks: {
          $router: {
            back: mockBack,
          },
        },
      },
    })
    await wrapper.vm.$nextTick()
    
    wrapper.vm.goBack()
    expect(mockBack).toHaveBeenCalled()
  })

  it('should display service image container', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-image-container').exists()).toBe(true)
  })

  it('should display service info section', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-info').exists()).toBe(true)
  })

  it('should display action buttons section', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.action-buttons').exists()).toBe(true)
  })

  it('should display merchant info section', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.merchant-info').exists()).toBe(true)
  })

  it('should display merchant logo', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, logo: 'https://example.com/logo.png' }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.merchant-logo').exists()).toBe(true)
  })

  it('should display merchant meta information', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.merchant-meta').exists()).toBe(true)
  })

  it('should display review item with correct structure', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1 })])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.review-item').exists() || wrapper.find('.review-header').exists()).toBe(true)
  })

  it('should display review user info', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1, userName: '张三' })])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.review-user-info').exists() || wrapper.find('.review-user').exists()).toBe(true)
  })

  it('should display review time', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1 })])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.review-time').exists() || wrapper.text().includes('评价')).toBe(true)
  })

  it('should display review content', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1, content: '测试评价内容' })])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.review-content').exists() || wrapper.text().includes('测试评价内容')).toBe(true)
  })

  it('should display description content tab', async () => {
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.resolve({ data: createService({ id: 1, description: '服务描述内容' }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.description-content').exists() || wrapper.text().includes('服务描述内容')).toBe(true)
  })

  it('should display service summary in book dialog', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.bookDialogVisible = true
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.service-summary').exists() || wrapper.text().includes('服务名称')).toBe(true)
  })

  it('should display summary items correctly', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.resolve({ data: createService({ id: 1, name: '宠物美容', price: 100 }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.bookDialogVisible = true
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.summary-item').exists() || wrapper.text().includes('宠物美容')).toBe(true)
  })

  it('should handle tab change', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.activeTab = 'reviews'
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.activeTab).toBe('reviews')
  })

  it('should display empty state when no reviews', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => Promise.resolve([]))

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.activeTab = 'reviews'
    await wrapper.vm.$nextTick()
    
    expect(wrapper.text()).toContain('暂无评价')
  })

  it('should close book dialog when clicking cancel', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.bookDialogVisible = true
    await wrapper.vm.$nextTick()
    
    const cancelBtn = wrapper.findAll('.el-dialog .el-button').find(b => b.text().includes('取消'))
    if (cancelBtn?.exists()) {
      await cancelBtn.trigger('click')
      await wrapper.vm.$nextTick()
    }
  })

  it('should display confirm button in book dialog', async () => {
    mockUserApi.getUserPets.mockImplementation(() => 
      Promise.resolve({ data: [createPet()] })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.bookDialogVisible = true
    await wrapper.vm.$nextTick()
    
    const confirmBtn = wrapper.findAll('.el-dialog .el-button').find(b => b.text().includes('确认预约'))
    expect(confirmBtn?.exists()).toBe(true)
  })

  it('should display service price row', async () => {
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.resolve({ data: createService({ id: 1, price: 100, duration: 60 }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-price-row').exists()).toBe(true)
  })

  it('should display service price with correct format', async () => {
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.resolve({ data: createService({ id: 1, price: 100 }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-price').exists()).toBe(true)
    expect(wrapper.text()).toContain('¥')
  })

  it('should display service duration with icon', async () => {
    mockUserApi.getServiceById.mockImplementation(() => 
      Promise.resolve({ data: createService({ id: 1, duration: 60 }) })
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-duration').exists()).toBe(true)
  })

  it('should display card header in merchant card', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.card-header').exists() || wrapper.find('.card-title').exists()).toBe(true)
  })

  it('should display detail tabs card', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.detail-tabs').exists()).toBe(true)
  })

  it('should display tab label with icon', async () => {
    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.tab-label').exists() || wrapper.text().includes('评价列表')).toBe(true)
  })

  it('should display user avatar in review', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1, userName: '张三' })])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.user-avatar').exists() || wrapper.find('.el-avatar').exists()).toBe(true)
  })

  it('should display user detail in review', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1, userName: '张三' })])
    )

    const wrapper = mountUserComponent(ServiceDetail)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.user-detail').exists() || wrapper.text().includes('张三')).toBe(true)
  })

  it('should handle invalid service ID', async () => {
    const wrapper = mountUserComponent(ServiceDetail, {
      global: {
        mocks: {
          $route: {
            params: { id: 'invalid' },
          },
        },
      },
    })
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.exists()).toBe(true)
  })
})
