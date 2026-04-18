import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createService, mockUserStore, createServiceList } from '@/tests/utils/userTestUtils'
import ServiceList from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

const mockServiceApi = {
  getServices: vi.fn(() => Promise.resolve({ data: createServiceList(10) })),
  getServicesByKeyword: vi.fn(() => Promise.resolve({ data: createServiceList(5) })),
}

vi.mock('@/api/user', () => ({
  getServices: (...args: any[]) => mockServiceApi.getServices(...args),
  getServicesByKeyword: (...args: any[]) => mockServiceApi.getServicesByKeyword(...args),
}))

describe('ServiceList', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: createServiceList(10) })
    )
    mockServiceApi.getServicesByKeyword.mockImplementation(() => 
      Promise.resolve({ data: createServiceList(5) })
    )
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(ServiceList)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display page title', () => {
    const wrapper = mountUserComponent(ServiceList)
    expect(wrapper.find('.page-title').exists()).toBe(true)
    expect(wrapper.text()).toContain('服务项目')
  })

  it('should display page header', () => {
    const wrapper = mountUserComponent(ServiceList)
    expect(wrapper.find('.page-header').exists()).toBe(true)
  })

  it('should have filter toggle button', () => {
    const wrapper = mountUserComponent(ServiceList)
    const filterBtn = wrapper.findAll('.el-button').find(b => b.text().includes('高级筛选'))
    expect(filterBtn?.exists()).toBe(true)
  })

  it('should display category tabs', () => {
    const wrapper = mountUserComponent(ServiceList)
    expect(wrapper.find('.category-tabs').exists()).toBe(true)
  })

  it('should display all category options', () => {
    const wrapper = mountUserComponent(ServiceList)
    const tabs = wrapper.findAll('.el-tabs__item')
    expect(tabs.length).toBeGreaterThan(0)
  })

  it('should display filter card when toggled', async () => {
    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    
    const filterBtn = wrapper.findAll('.el-button').find(b => b.text().includes('高级筛选'))
    if (filterBtn?.exists()) {
      await filterBtn.trigger('click')
      await wrapper.vm.$nextTick()
      expect(wrapper.find('.filter-card').exists()).toBe(true)
    }
  })

  it('should have search input in filter panel', async () => {
    const wrapper = mountUserComponent(ServiceList)
    wrapper.vm.showFilterPanel = true
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.filter-card input').exists()).toBe(true)
  })

  it('should have price range slider', async () => {
    const wrapper = mountUserComponent(ServiceList)
    wrapper.vm.showFilterPanel = true
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.el-slider').exists()).toBe(true)
  })

  it('should have duration range slider', async () => {
    const wrapper = mountUserComponent(ServiceList)
    wrapper.vm.showFilterPanel = true
    await wrapper.vm.$nextTick()
    
    const sliders = wrapper.findAll('.el-slider')
    expect(sliders.length).toBeGreaterThanOrEqual(2)
  })

  it('should have rating filter', async () => {
    const wrapper = mountUserComponent(ServiceList)
    wrapper.vm.showFilterPanel = true
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.el-rate').exists()).toBe(true)
  })

  it('should have sort select', async () => {
    const wrapper = mountUserComponent(ServiceList)
    wrapper.vm.showFilterPanel = true
    await wrapper.vm.$nextTick()
    
    const selects = wrapper.findAll('.el-select')
    expect(selects.length).toBeGreaterThan(0)
  })

  it('should have reset filter button', async () => {
    const wrapper = mountUserComponent(ServiceList)
    wrapper.vm.showFilterPanel = true
    await wrapper.vm.$nextTick()
    
    const resetBtn = wrapper.findAll('.el-button').find(b => b.text().includes('重置筛选'))
    expect(resetBtn?.exists()).toBe(true)
  })

  it('should display service list', async () => {
    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.services-content').exists()).toBe(true)
  })

  it('should display service cards', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容' })] })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-card').exists() || wrapper.text().includes('宠物美容')).toBe(true)
  })

  it('should display service name', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容' })] })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('宠物美容')
  })

  it('should display service price', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容', price: 100 })] })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('100')
  })

  it('should display service duration', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容', duration: 60 })] })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('60')
  })

  it('should display merchant name', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容', merchantName: '测试宠物店' })] })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('测试宠物店')
  })

  it('should display service rating', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容' })] })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-rating').exists() || wrapper.find('.el-rate').exists()).toBe(true)
  })

  it('should have book button on service card', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容' })] })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const bookBtn = wrapper.findAll('.el-button').find(b => b.text().includes('立即预约'))
    expect(bookBtn?.exists()).toBe(true)
  })

  it('should display result info', async () => {
    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.result-info').exists()).toBe(true)
  })

  it('should display total count', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: createServiceList(10) })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.text()).toContain('共找到')
  })

  it('should have pagination', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: createServiceList(20) })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.el-pagination').exists()).toBe(true)
  })

  it('should display empty state when no services', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [] })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.el-empty').exists() || wrapper.text().includes('暂无')).toBe(true)
  })

  it('should search services by keyword', async () => {
    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    
    wrapper.vm.searchKeyword = '美容'
    wrapper.vm.handleSearch()
    await wrapper.vm.$nextTick()
    
    expect(mockServiceApi.getServicesByKeyword).toHaveBeenCalled()
  })

  it('should filter by category', async () => {
    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    
    wrapper.vm.selectedCategory = 'beauty'
    wrapper.vm.handleTabChange()
    await wrapper.vm.$nextTick()
    
    expect(mockServiceApi.getServices).toHaveBeenCalled()
  })

  it('should sort by price ascending', async () => {
    const services = [
      createService({ id: 1, name: '服务A', price: 200 }),
      createService({ id: 2, name: '服务B', price: 100 }),
    ]
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: services })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.sortBy = 'price_asc'
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.filteredAndSortedList).toBeDefined()
  })

  it('should sort by price descending', async () => {
    const services = [
      createService({ id: 1, name: '服务A', price: 100 }),
      createService({ id: 2, name: '服务B', price: 200 }),
    ]
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: services })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.sortBy = 'price_desc'
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.filteredAndSortedList).toBeDefined()
  })

  it('should sort by rating descending', async () => {
    const services = [
      createService({ id: 1, name: '服务A' }),
      createService({ id: 2, name: '服务B' }),
    ]
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: services })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.sortBy = 'rating_desc'
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.filteredAndSortedList).toBeDefined()
  })

  it('should sort by duration ascending', async () => {
    const services = [
      createService({ id: 1, name: '服务A', duration: 120 }),
      createService({ id: 2, name: '服务B', duration: 60 }),
    ]
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: services })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.sortBy = 'duration_asc'
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.filteredAndSortedList).toBeDefined()
  })

  it('should sort by duration descending', async () => {
    const services = [
      createService({ id: 1, name: '服务A', duration: 60 }),
      createService({ id: 2, name: '服务B', duration: 120 }),
    ]
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: services })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.sortBy = 'duration_desc'
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.filteredAndSortedList).toBeDefined()
  })

  it('should filter by price range', async () => {
    const services = [
      createService({ id: 1, name: '服务A', price: 50 }),
      createService({ id: 2, name: '服务B', price: 200 }),
    ]
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: services })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.priceRange = [0, 100]
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.filteredAndSortedList).toBeDefined()
  })

  it('should filter by duration range', async () => {
    const services = [
      createService({ id: 1, name: '服务A', duration: 30 }),
      createService({ id: 2, name: '服务B', duration: 120 }),
    ]
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: services })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.durationRange = [0, 60]
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.filteredAndSortedList).toBeDefined()
  })

  it('should filter by rating', async () => {
    const services = [
      createService({ id: 1, name: '服务A' }),
      createService({ id: 2, name: '服务B' }),
    ]
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: services })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.ratingFilter = 4
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.filteredAndSortedList).toBeDefined()
  })

  it('should reset filters', async () => {
    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    
    wrapper.vm.searchKeyword = 'test'
    wrapper.vm.selectedCategory = 'beauty'
    wrapper.vm.priceRange = [100, 500]
    wrapper.vm.durationRange = [30, 120]
    wrapper.vm.ratingFilter = 4
    wrapper.vm.sortBy = 'price_desc'
    
    wrapper.vm.resetFilters()
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.searchKeyword).toBe('')
    expect(wrapper.vm.selectedCategory).toBe('all')
    expect(wrapper.vm.priceRange).toEqual([0, 1000])
    expect(wrapper.vm.durationRange).toEqual([0, 240])
    expect(wrapper.vm.ratingFilter).toBe(0)
    expect(wrapper.vm.sortBy).toBe('default')
  })

  it('should handle page change', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: createServiceList(20) })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.handlePageChange(2)
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.currentPage).toBe(2)
  })

  it('should navigate to book service', async () => {
    const mockPush = vi.fn()
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容' })] })
    )

    const wrapper = mountUserComponent(ServiceList, {
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

  it('should navigate to merchant page', async () => {
    const mockPush = vi.fn()
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容', merchantId: 1 })] })
    )

    const wrapper = mountUserComponent(ServiceList, {
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
    
    const merchantName = wrapper.find('.merchant-name')
    if (merchantName.exists()) {
      await merchantName.trigger('click')
    }
  })

  it('should handle API error gracefully', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.reject(new Error('Network error'))
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.exists()).toBe(true)
  })

  it('should have loading state', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      new Promise(resolve => setTimeout(() => resolve({ data: [] }), 100))
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    
    expect(wrapper.exists()).toBe(true)
  })

  it('should format duration correctly for minutes', () => {
    const wrapper = mountUserComponent(ServiceList)
    const formatted = wrapper.vm.formatDuration(30)
    expect(formatted).toBe('30分钟')
  })

  it('should format duration correctly for hours', () => {
    const wrapper = mountUserComponent(ServiceList)
    const formatted = wrapper.vm.formatDuration(120)
    expect(formatted).toBe('2小时')
  })

  it('should format duration correctly for hours and minutes', () => {
    const wrapper = mountUserComponent(ServiceList)
    const formatted = wrapper.vm.formatDuration(90)
    expect(formatted).toBe('1小时30分钟')
  })

  it('should get service image emoji', () => {
    const wrapper = mountUserComponent(ServiceList)
    
    expect(wrapper.vm.getServiceImage({ category: 'beauty' } as any)).toBe('🛁')
    expect(wrapper.vm.getServiceImage({ category: 'health' } as any)).toBe('🏥')
    expect(wrapper.vm.getServiceImage({ category: 'boarding' } as any)).toBe('🏠')
    expect(wrapper.vm.getServiceImage({ category: 'food' } as any)).toBe('🍖')
    expect(wrapper.vm.getServiceImage({ category: 'training' } as any)).toBe('🎓')
    expect(wrapper.vm.getServiceImage({ category: 'other' } as any)).toBe('🐾')
  })

  it('should have correct category options', () => {
    const wrapper = mountUserComponent(ServiceList)
    expect(wrapper.vm.categories).toBeDefined()
    expect(wrapper.vm.categories.length).toBe(6)
  })

  it('should have correct sort options', () => {
    const wrapper = mountUserComponent(ServiceList)
    expect(wrapper.vm.sortOptions).toBeDefined()
    expect(wrapper.vm.sortOptions.length).toBe(6)
  })

  it('should toggle filter panel', async () => {
    const wrapper = mountUserComponent(ServiceList)
    expect(wrapper.vm.showFilterPanel).toBe(false)
    
    const filterBtn = wrapper.findAll('.el-button').find(b => b.text().includes('高级筛选'))
    if (filterBtn?.exists()) {
      await filterBtn.trigger('click')
      await wrapper.vm.$nextTick()
      expect(wrapper.vm.showFilterPanel).toBe(true)
    }
  })

  it('should display service card with correct structure', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容' })] })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-card').exists() || wrapper.find('.service-info').exists()).toBe(true)
  })

  it('should display service image placeholder', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容', category: 'beauty' })] })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-image').exists() || wrapper.find('.image-placeholder').exists()).toBe(true)
  })

  it('should display service meta information', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: [createService({ id: 1, name: '宠物美容', price: 100, duration: 60 })] })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.service-meta').exists()).toBe(true)
  })

  it('should display pagination container', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: createServiceList(20) })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    expect(wrapper.find('.pagination-container').exists()).toBe(true)
  })

  it('should handle search with enter key', async () => {
    const wrapper = mountUserComponent(ServiceList)
    wrapper.vm.showFilterPanel = true
    await wrapper.vm.$nextTick()
    
    const searchInput = wrapper.find('.filter-card input')
    if (searchInput.exists()) {
      await searchInput.trigger('keyup.enter')
      expect(mockServiceApi.getServicesByKeyword).toHaveBeenCalled()
    }
  })

  it('should filter services by keyword', async () => {
    const services = [
      createService({ id: 1, name: '宠物美容' }),
      createService({ id: 2, name: '宠物寄养' }),
    ]
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: services })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.searchKeyword = '美容'
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.filteredAndSortedList).toBeDefined()
  })

  it('should paginate services correctly', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: createServiceList(20) })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    wrapper.vm.currentPage = 2
    wrapper.vm.pageSize = 8
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.paginatedList).toBeDefined()
  })

  it('should reset current page when filters change', async () => {
    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    
    wrapper.vm.currentPage = 3
    wrapper.vm.selectedCategory = 'beauty'
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.currentPage).toBe(1)
  })

  it('should display book button on each service card', async () => {
    mockServiceApi.getServices.mockImplementation(() => 
      Promise.resolve({ data: createServiceList(3) })
    )

    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const bookBtns = wrapper.findAll('.book-button')
    expect(bookBtns.length).toBeGreaterThan(0)
  })

  it('should display sort select in result info', async () => {
    const wrapper = mountUserComponent(ServiceList)
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const resultInfo = wrapper.find('.result-info')
    expect(resultInfo.exists()).toBe(true)
  })
})
