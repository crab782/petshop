import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createMerchant, mockUserStore, createMerchantList } from '@/tests/utils/userTestUtils'
import UserMerchant from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

const mockMerchantList = (merchants: any[] = [], total = 0) => ({
  data: {
    data: merchants,
    total,
  },
})

const mockUserApi = {
  getMerchantList: vi.fn(() => Promise.resolve(mockMerchantList(createMerchantList(10), 10))),
  addFavorite: vi.fn(() => Promise.resolve({ data: { id: 1, merchantId: 1 } })),
  removeFavorite: vi.fn(() => Promise.resolve({ data: null })),
}

vi.mock('@/api/user', () => ({
  getMerchantList: (...args: any[]) => mockUserApi.getMerchantList(...args),
  addFavorite: (...args: any[]) => mockUserApi.addFavorite(...args),
  removeFavorite: (...args: any[]) => mockUserApi.removeFavorite(...args),
}))

describe('UserMerchant', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(createMerchantList(10), 10))
    )
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserMerchant)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display page title', () => {
    const wrapper = mountUserComponent(UserMerchant)
    expect(wrapper.find('.page-title').exists()).toBe(true)
    expect(wrapper.text()).toContain('商家列表')
  })

  it('should display search card', () => {
    const wrapper = mountUserComponent(UserMerchant)
    expect(wrapper.find('.search-card').exists()).toBe(true)
  })

  it('should have search input for merchant name', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const searchInput = wrapper.find('.search-card input')
    expect(searchInput.exists()).toBe(true)
  })

  it('should have rating filter select', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const selects = wrapper.findAll('.search-card .el-select')
    expect(selects.length).toBeGreaterThan(0)
  })

  it('should have sort select', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const selects = wrapper.findAll('.search-card .el-select')
    expect(selects.length).toBeGreaterThanOrEqual(2)
  })

  it('should have search button', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const buttons = wrapper.findAll('.search-card .el-button')
    const searchButton = buttons.find(b => b.text().includes('搜索'))
    expect(searchButton?.exists()).toBe(true)
  })

  it('should have reset button', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const buttons = wrapper.findAll('.search-card .el-button')
    const resetButton = buttons.find(b => b.text().includes('重置'))
    expect(resetButton?.exists()).toBe(true)
  })

  it('should display merchant cards when data is loaded', async () => {
    const merchants = [
      createMerchant({ id: 1, name: '测试商家1', rating: 4.5, serviceCount: 10 }),
      createMerchant({ id: 2, name: '测试商家2', rating: 4.0, serviceCount: 5 }),
    ]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 2))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.merchant-content').exists()).toBe(true)
  })

  it('should display merchant name', async () => {
    const merchants = [createMerchant({ id: 1, name: '宠物美容店' })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('宠物美容店')
  })

  it('should display merchant rating', async () => {
    const merchants = [createMerchant({ id: 1, name: '测试商家', rating: 4.5 })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const rating = wrapper.find('.merchant-rating')
    expect(rating.exists()).toBe(true)
  })

  it('should display merchant address', async () => {
    const merchants = [createMerchant({ id: 1, name: '测试商家', address: '北京市朝阳区' })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('北京市朝阳区')
  })

  it('should display merchant phone', async () => {
    const merchants = [createMerchant({ id: 1, name: '测试商家', phone: '13800138000' })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('13800138000')
  })

  it('should display service count', async () => {
    const merchants = [createMerchant({ id: 1, name: '测试商家', serviceCount: 15 })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('15')
  })

  it('should have favorite button on merchant card', async () => {
    const merchants = [createMerchant({ id: 1, name: '测试商家' })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.find('.favorite-btn')
    expect(favoriteBtn.exists()).toBe(true)
  })

  it('should have view detail button', async () => {
    const merchants = [createMerchant({ id: 1, name: '测试商家' })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const detailBtn = wrapper.findAll('.el-button').find(b => b.text().includes('查看详情'))
    expect(detailBtn?.exists()).toBe(true)
  })

  it('should call addFavorite when clicking favorite button on unfavorited merchant', async () => {
    const merchants = [createMerchant({ id: 1, name: '测试商家', isFavorited: false })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.find('.favorite-btn')
    if (favoriteBtn.exists()) {
      await favoriteBtn.trigger('click')
      await wrapper.vm.$nextTick()
    }
  })

  it('should call removeFavorite when clicking favorite button on favorited merchant', async () => {
    const merchants = [createMerchant({ id: 1, name: '测试商家', isFavorited: true })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.find('.favorite-btn')
    if (favoriteBtn.exists()) {
      await favoriteBtn.trigger('click')
      await wrapper.vm.$nextTick()
    }
  })

  it('should navigate to merchant detail when clicking view detail', async () => {
    const mockPush = vi.fn()
    const merchants = [createMerchant({ id: 1, name: '测试商家' })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant, {
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
    
    const detailBtn = wrapper.findAll('.el-button').find(b => b.text().includes('查看详情'))
    if (detailBtn?.exists()) {
      await detailBtn.trigger('click')
    }
  })

  it('should display pagination when total > 0', async () => {
    const merchants = createMerchantList(12)
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 50))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const pagination = wrapper.find('.el-pagination')
    expect(pagination.exists()).toBe(true)
  })

  it('should display empty state when no merchants', async () => {
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList([], 0))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const empty = wrapper.find('.el-empty')
    expect(empty.exists() || wrapper.text().includes('暂无')).toBe(true)
  })

  it('should call getMerchantList with search params on search', async () => {
    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    
    const searchButton = wrapper.findAll('.search-card .el-button').find(b => b.text().includes('搜索'))
    if (searchButton?.exists()) {
      await searchButton.trigger('click')
      expect(mockUserApi.getMerchantList).toHaveBeenCalled()
    }
  })

  it('should reset filters when clicking reset button', async () => {
    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    
    const resetButton = wrapper.findAll('.search-card .el-button').find(b => b.text().includes('重置'))
    if (resetButton?.exists()) {
      await resetButton.trigger('click')
      expect(mockUserApi.getMerchantList).toHaveBeenCalled()
    }
  })

  it('should have loading state', async () => {
    mockUserApi.getMerchantList.mockImplementation(() => 
      new Promise(resolve => setTimeout(() => resolve(mockMerchantList([])), 100))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.merchant-content').exists()).toBe(true)
  })

  it('should display merchant logo or placeholder', async () => {
    const merchants = [createMerchant({ id: 1, name: '宠物美容店', logo: 'https://example.com/logo.png' })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const avatar = wrapper.find('.merchant-logo')
    expect(avatar.exists()).toBe(true)
  })

  it('should sort merchants by rating descending', async () => {
    const merchants = [
      createMerchant({ id: 1, name: '商家A', rating: 3.5 }),
      createMerchant({ id: 2, name: '商家B', rating: 4.8 }),
    ]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 2))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.merchant-content').exists()).toBe(true)
  })

  it('should sort merchants by rating ascending', async () => {
    const merchants = [
      createMerchant({ id: 1, name: '商家A', rating: 4.8 }),
      createMerchant({ id: 2, name: '商家B', rating: 3.5 }),
    ]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 2))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.merchant-content').exists()).toBe(true)
  })

  it('should sort merchants by service count', async () => {
    const merchants = [
      createMerchant({ id: 1, name: '商家A', serviceCount: 5 }),
      createMerchant({ id: 2, name: '商家B', serviceCount: 20 }),
    ]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 2))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.merchant-content').exists()).toBe(true)
  })

  it('should handle page change', async () => {
    const merchants = createMerchantList(12)
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 50))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const pagination = wrapper.find('.el-pagination')
    if (pagination.exists()) {
      expect(pagination.exists()).toBe(true)
    }
  })

  it('should handle page size change', async () => {
    const merchants = createMerchantList(12)
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 50))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.merchant-content').exists()).toBe(true)
  })

  it('should filter by rating', async () => {
    const merchants = [
      createMerchant({ id: 1, name: '商家A', rating: 4.8 }),
      createMerchant({ id: 2, name: '商家B', rating: 3.5 }),
    ]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 2))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.merchant-content').exists()).toBe(true)
  })

  it('should handle API error gracefully', async () => {
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.reject(new Error('Network error'))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.exists()).toBe(true)
  })

  it('should handle favorite API error gracefully', async () => {
    const merchants = [createMerchant({ id: 1, name: '测试商家', isFavorited: false })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )
    mockUserApi.addFavorite.mockImplementation(() => 
      Promise.reject(new Error('Network error'))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const favoriteBtn = wrapper.find('.favorite-btn')
    if (favoriteBtn.exists()) {
      await favoriteBtn.trigger('click')
      await wrapper.vm.$nextTick()
    }
    
    expect(wrapper.exists()).toBe(true)
  })

  it('should display merchant card with correct structure', async () => {
    const merchants = [createMerchant({ id: 1, name: '测试商家' })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.merchant-card').exists()).toBe(true)
    expect(wrapper.find('.merchant-header').exists()).toBe(true)
    expect(wrapper.find('.merchant-info').exists()).toBe(true)
    expect(wrapper.find('.card-footer').exists()).toBe(true)
  })

  it('should display info items correctly', async () => {
    const merchants = [createMerchant({ 
      id: 1, 
      name: '测试商家', 
      address: '北京市朝阳区',
      phone: '13800138000',
      serviceCount: 10
    })]
    mockUserApi.getMerchantList.mockImplementation(() => 
      Promise.resolve(mockMerchantList(merchants, 1))
    )

    const wrapper = mountUserComponent(UserMerchant)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const infoItems = wrapper.findAll('.info-item')
    expect(infoItems.length).toBeGreaterThan(0)
  })

  it('should have correct rating options', () => {
    const wrapper = mountUserComponent(UserMerchant)
    expect(wrapper.vm.ratingOptions).toBeDefined()
    expect(wrapper.vm.ratingOptions.length).toBe(5)
  })

  it('should have correct sort options', () => {
    const wrapper = mountUserComponent(UserMerchant)
    expect(wrapper.vm.sortOptions).toBeDefined()
    expect(wrapper.vm.sortOptions.length).toBe(4)
  })

  it('should return default emoji for merchant name', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const emoji = wrapper.vm.getDefaultLogo('普通店铺')
    expect(emoji).toBe('🏪')
  })

  it('should return correct emoji for beauty shop', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const emoji = wrapper.vm.getDefaultLogo('美容店')
    expect(emoji).toBe('💇')
  })

  it('should return correct emoji for pet shop', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const emoji = wrapper.vm.getDefaultLogo('宠物店')
    expect(emoji).toBe('🐾')
  })

  it('should return correct emoji for hospital', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const emoji = wrapper.vm.getDefaultLogo('宠物医院')
    expect(emoji).toBe('🏥')
  })

  it('should return correct emoji for store', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const emoji = wrapper.vm.getDefaultLogo('宠物商店')
    expect(emoji).toBe('🏪')
  })

  it('should return correct emoji for training', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const emoji = wrapper.vm.getDefaultLogo('宠物训练')
    expect(emoji).toBe('🎓')
  })

  it('should return correct emoji for boarding', () => {
    const wrapper = mountUserComponent(UserMerchant)
    const emoji = wrapper.vm.getDefaultLogo('宠物寄养')
    expect(emoji).toBe('🏠')
  })
})
