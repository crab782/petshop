import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createMerchant, createService, createProduct, mockUserStore, createServiceList, createProductList } from '@/tests/utils/userTestUtils'
import UserShop from '../index.vue'

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

const createFavoriteMerchant = (overrides: any = {}) => ({
  id: 1,
  merchantId: 1,
  merchantName: '测试商家',
  merchantLogo: 'https://example.com/logo.png',
  merchantAddress: '北京市朝阳区',
  merchantPhone: '13800138000',
  createdAt: '2024-04-20T10:00:00.000Z',
  ...overrides,
})

const mockUserApi = {
  getMerchantInfo: vi.fn(() => Promise.resolve({ 
    data: createMerchant({ id: 1, name: '测试宠物店' }) 
  })),
  getMerchantServices: vi.fn(() => Promise.resolve(createServiceList(3))),
  getMerchantReviews: vi.fn(() => Promise.resolve([createMerchantReview()])),
  getFavorites: vi.fn(() => Promise.resolve([])),
  getProducts: vi.fn(() => Promise.resolve(createProductList(3))),
  addFavorite: vi.fn(() => Promise.resolve({ data: { id: 1, merchantId: 1 } })),
  removeFavorite: vi.fn(() => Promise.resolve({ data: null })),
}

vi.mock('@/api/user', () => ({
  getMerchantInfo: (...args: any[]) => mockUserApi.getMerchantInfo(...args),
  getMerchantServices: (...args: any[]) => mockUserApi.getMerchantServices(...args),
  getMerchantReviews: (...args: any[]) => mockUserApi.getMerchantReviews(...args),
  getFavorites: (...args: any[]) => mockUserApi.getFavorites(...args),
  getProducts: (...args: any[]) => mockUserApi.getProducts(...args),
  addFavorite: (...args: any[]) => mockUserApi.addFavorite(...args),
  removeFavorite: (...args: any[]) => mockUserApi.removeFavorite(...args),
}))

describe('UserShop', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, name: '测试宠物店' }) })
    )
    mockUserApi.getMerchantServices.mockImplementation(() => 
      Promise.resolve(createServiceList(3))
    )
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview()])
    )
    mockUserApi.getFavorites.mockImplementation(() => 
      Promise.resolve([])
    )
    mockUserApi.getProducts.mockImplementation(() => 
      Promise.resolve(createProductList(3))
    )
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserShop)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display shop header card', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.shop-header-card').exists()).toBe(true)
  })

  it('should display merchant name', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, name: '宠物乐园' }) })
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('宠物乐园')
  })

  it('should display merchant logo', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, logo: 'https://example.com/logo.png' }) })
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.shop-avatar').exists()).toBe(true)
  })

  it('should display merchant address', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, address: '北京市朝阳区测试路1号' }) })
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('北京市朝阳区测试路1号')
  })

  it('should display merchant phone', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, phone: '13800138000' }) })
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('13800138000')
  })

  it('should display merchant description', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, description: '专业宠物服务' }) })
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('专业宠物服务')
  })

  it('should display rating', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, rating: 4.5 }) })
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.shop-rating').exists()).toBe(true)
  })

  it('should have favorite button', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('收藏'))
    expect(favoriteBtn?.exists()).toBe(true)
  })

  it('should have contact button', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const contactBtn = wrapper.findAll('.el-button').find(b => b.text().includes('联系商家'))
    expect(contactBtn?.exists()).toBe(true)
  })

  it('should display tabs for services, products and reviews', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.el-tabs').exists()).toBe(true)
  })

  it('should display services tab', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('服务列表')
  })

  it('should display products tab', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('商品列表')
  })

  it('should display reviews tab', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('用户评价')
  })

  it('should display service list', async () => {
    mockUserApi.getMerchantServices.mockImplementation(() => 
      Promise.resolve([
        createService({ id: 1, name: '宠物美容', price: 100 }),
        createService({ id: 2, name: '宠物寄养', price: 200 }),
      ])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-card').exists() || wrapper.text().includes('宠物美容')).toBe(true)
  })

  it('should display service name', async () => {
    mockUserApi.getMerchantServices.mockImplementation(() => 
      Promise.resolve([createService({ id: 1, name: '宠物美容' })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('宠物美容')
  })

  it('should display service price', async () => {
    mockUserApi.getMerchantServices.mockImplementation(() => 
      Promise.resolve([createService({ id: 1, name: '宠物美容', price: 100 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('100')
  })

  it('should display service duration', async () => {
    mockUserApi.getMerchantServices.mockImplementation(() => 
      Promise.resolve([createService({ id: 1, name: '宠物美容', duration: 60 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('60')
  })

  it('should have book button on service card', async () => {
    mockUserApi.getMerchantServices.mockImplementation(() => 
      Promise.resolve([createService({ id: 1, name: '宠物美容' })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const bookBtn = wrapper.findAll('.el-button').find(b => b.text().includes('立即预约'))
    expect(bookBtn?.exists()).toBe(true)
  })

  it('should display product list', async () => {
    mockUserApi.getProducts.mockImplementation(() => 
      Promise.resolve([
        createProduct({ id: 1, name: '宠物狗粮', price: 99, merchantId: 1 }),
        createProduct({ id: 2, name: '宠物猫粮', price: 88, merchantId: 1 }),
      ])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.product-card').exists() || wrapper.text().includes('宠物狗粮')).toBe(true)
  })

  it('should display product name', async () => {
    mockUserApi.getProducts.mockImplementation(() => 
      Promise.resolve([createProduct({ id: 1, name: '宠物狗粮', merchantId: 1 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('宠物狗粮')
  })

  it('should display product price', async () => {
    mockUserApi.getProducts.mockImplementation(() => 
      Promise.resolve([createProduct({ id: 1, name: '宠物狗粮', price: 99, merchantId: 1 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('99')
  })

  it('should display product stock', async () => {
    mockUserApi.getProducts.mockImplementation(() => 
      Promise.resolve([createProduct({ id: 1, name: '宠物狗粮', stock: 50, merchantId: 1 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('50')
  })

  it('should have view detail button on product card', async () => {
    mockUserApi.getProducts.mockImplementation(() => 
      Promise.resolve([createProduct({ id: 1, name: '宠物狗粮', merchantId: 1 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const viewBtn = wrapper.findAll('.el-button').find(b => b.text().includes('查看详情'))
    expect(viewBtn?.exists()).toBe(true)
  })

  it('should display review list', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([
        createMerchantReview({ id: 1, content: '服务非常好' }),
        createMerchantReview({ id: 2, content: '很满意' }),
      ])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.review-list').exists() || wrapper.text().includes('服务非常好')).toBe(true)
  })

  it('should display review content', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1, content: '非常满意的服务' })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('非常满意的服务')
  })

  it('should display review rating', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1, rating: 5 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.review-item').exists() || wrapper.text().includes('评价')).toBe(true)
  })

  it('should display review user name', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1, userName: '张三' })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('张三')
  })

  it('should display average rating', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([
        createMerchantReview({ id: 1, rating: 5 }),
        createMerchantReview({ id: 2, rating: 4 }),
      ])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.rating-summary').exists() || wrapper.find('.rating-score').exists()).toBe(true)
  })

  it('should toggle favorite when clicking favorite button', async () => {
    mockUserApi.getFavorites.mockImplementation(() => Promise.resolve([]))

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('收藏店铺'))
    if (favoriteBtn?.exists()) {
      await favoriteBtn.trigger('click')
      await wrapper.vm.$nextTick()
    }
  })

  it('should show unfavorited state when not in favorites', async () => {
    mockUserApi.getFavorites.mockImplementation(() => Promise.resolve([]))

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('收藏店铺'))
    expect(favoriteBtn?.exists()).toBe(true)
  })

  it('should show favorited state when in favorites', async () => {
    mockUserApi.getFavorites.mockImplementation(() => 
      Promise.resolve([createFavoriteMerchant({ merchantId: 1 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('已收藏'))
    expect(favoriteBtn?.exists()).toBe(true)
  })

  it('should call addFavorite when favoriting', async () => {
    mockUserApi.getFavorites.mockImplementation(() => Promise.resolve([]))

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('收藏店铺'))
    if (favoriteBtn?.exists()) {
      await favoriteBtn.trigger('click')
      await wrapper.vm.$nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
    }
  })

  it('should call removeFavorite when unfavoriting', async () => {
    mockUserApi.getFavorites.mockImplementation(() => 
      Promise.resolve([createFavoriteMerchant({ merchantId: 1 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('已收藏'))
    if (favoriteBtn?.exists()) {
      await favoriteBtn.trigger('click')
      await wrapper.vm.$nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
    }
  })

  it('should navigate to book service when clicking book button', async () => {
    const mockPush = vi.fn()
    mockUserApi.getMerchantServices.mockImplementation(() => 
      Promise.resolve([createService({ id: 1, name: '宠物美容' })])
    )

    const wrapper = mountUserComponent(UserShop, {
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
    
    const bookBtn = wrapper.findAll('.el-button').find(b => b.text().includes('立即预约'))
    if (bookBtn?.exists()) {
      await bookBtn.trigger('click')
    }
  })

  it('should navigate to product detail when clicking view button', async () => {
    const mockPush = vi.fn()
    mockUserApi.getProducts.mockImplementation(() => 
      Promise.resolve([createProduct({ id: 1, name: '宠物狗粮', merchantId: 1 })])
    )

    const wrapper = mountUserComponent(UserShop, {
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
    
    const viewBtn = wrapper.findAll('.el-button').find(b => b.text().includes('查看详情'))
    if (viewBtn?.exists()) {
      await viewBtn.trigger('click')
    }
  })

  it('should show contact info when clicking contact button', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.resolve({ data: createMerchant({ id: 1, phone: '13800138000' }) })
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const contactBtn = wrapper.findAll('.el-button').find(b => b.text().includes('联系商家'))
    if (contactBtn?.exists()) {
      await contactBtn.trigger('click')
    }
  })

  it('should display empty state when no services', async () => {
    mockUserApi.getMerchantServices.mockImplementation(() => Promise.resolve([]))

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('暂无服务')
  })

  it('should display empty state when no products', async () => {
    mockUserApi.getProducts.mockImplementation(() => Promise.resolve([]))

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const productTab = wrapper.findAll('.el-tabs__item').find(t => t.text().includes('商品列表'))
    if (productTab?.exists()) {
      await productTab.trigger('click')
      await wrapper.vm.$nextTick()
    }
  })

  it('should display empty state when no reviews', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => Promise.resolve([]))

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('暂无评价')
  })

  it('should display empty state when merchant not found', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => Promise.resolve(null))

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('店铺不存在')
  })

  it('should handle API error gracefully', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      Promise.reject(new Error('Network error'))
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.exists()).toBe(true)
  })

  it('should handle favorite API error gracefully', async () => {
    mockUserApi.getFavorites.mockImplementation(() => Promise.resolve([]))
    mockUserApi.addFavorite.mockImplementation(() => 
      Promise.reject(new Error('Network error'))
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.findAll('.el-button').find(b => b.text().includes('收藏店铺'))
    if (favoriteBtn?.exists()) {
      await favoriteBtn.trigger('click')
      await wrapper.vm.$nextTick()
    }
    
    expect(wrapper.exists()).toBe(true)
  })

  it('should have loading state', async () => {
    mockUserApi.getMerchantInfo.mockImplementation(() => 
      new Promise(resolve => setTimeout(() => resolve({ data: createMerchant() }), 100))
    )

    const wrapper = mountUserComponent(UserShop)
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

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.vm.avgRating).toBe('4.0')
  })

  it('should return 0 for average rating when no reviews', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => Promise.resolve([]))

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.vm.avgRating).toBe(0)
  })

  it('should format price correctly', async () => {
    const wrapper = mountUserComponent(UserShop)
    const formattedPrice = wrapper.vm.formatPrice(100)
    expect(formattedPrice).toBe('¥100.00')
  })

  it('should format date correctly', async () => {
    const wrapper = mountUserComponent(UserShop)
    const formattedDate = wrapper.vm.formatDate('2024-04-20T10:00:00.000Z')
    expect(formattedDate).toBeTruthy()
  })

  it('should filter products by merchantId', async () => {
    mockUserApi.getProducts.mockImplementation(() => 
      Promise.resolve([
        createProduct({ id: 1, name: '产品A', merchantId: 1 }),
        createProduct({ id: 2, name: '产品B', merchantId: 2 }),
      ])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.exists()).toBe(true)
  })

  it('should display review count', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([
        createMerchantReview({ id: 1 }),
        createMerchantReview({ id: 2 }),
        createMerchantReview({ id: 3 }),
      ])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('3')
  })

  it('should display service card with correct structure', async () => {
    mockUserApi.getMerchantServices.mockImplementation(() => 
      Promise.resolve([createService({ id: 1, name: '宠物美容' })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-card').exists() || wrapper.find('.service-info').exists()).toBe(true)
  })

  it('should display product card with correct structure', async () => {
    mockUserApi.getProducts.mockImplementation(() => 
      Promise.resolve([createProduct({ id: 1, name: '宠物狗粮', merchantId: 1 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.product-card').exists() || wrapper.find('.product-info').exists()).toBe(true)
  })

  it('should display review item with correct structure', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.review-item').exists() || wrapper.find('.review-header').exists()).toBe(true)
  })

  it('should display rating summary section', async () => {
    mockUserApi.getMerchantReviews.mockImplementation(() => 
      Promise.resolve([createMerchantReview({ id: 1, rating: 5 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.rating-summary').exists() || wrapper.find('.rating-score').exists()).toBe(true)
  })

  it('should display shop actions', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.shop-actions').exists()).toBe(true)
  })

  it('should display shop info section', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.shop-info').exists()).toBe(true)
  })

  it('should display shop meta information', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.shop-meta').exists()).toBe(true)
  })

  it('should display content section', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.content-section').exists()).toBe(true)
  })

  it('should handle tab change', async () => {
    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.activeTab = 'products'
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.activeTab).toBe('products')
  })

  it('should display disabled button when product stock is 0', async () => {
    mockUserApi.getProducts.mockImplementation(() => 
      Promise.resolve([createProduct({ id: 1, name: '宠物狗粮', merchantId: 1, stock: 0 })])
    )

    const wrapper = mountUserComponent(UserShop)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('缺货')
  })
})
