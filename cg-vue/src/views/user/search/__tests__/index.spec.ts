import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, mockUserStore } from '@/tests/utils/userTestUtils'
import SearchPage from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/user', () => ({
  searchProducts: vi.fn(() => Promise.resolve({ data: [] })),
  searchServices: vi.fn(() => Promise.resolve({ data: [] })),
  getMerchantInfo: vi.fn(() => Promise.resolve({ data: null })),
}))

describe('SearchPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(SearchPage)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display page title', () => {
    const wrapper = mountUserComponent(SearchPage)
    expect(wrapper.text()).toContain('搜索')
  })

  it('should have search bar', () => {
    const wrapper = mountUserComponent(SearchPage)
    expect(wrapper.find('.search-bar').exists()).toBe(true)
  })

  it('should have keyword state', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.keyword).toBeDefined()
  })

  it('should have activeTab state', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.activeTab).toBeDefined()
  })

  it('should have products state', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.products).toBeDefined()
  })

  it('should have services state', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.services).toBeDefined()
  })

  it('should have merchants state', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.merchants).toBeDefined()
  })

  it('should have searchHistory state', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.searchHistory).toBeDefined()
  })

  it('should have hotKeywords state', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.hotKeywords).toBeDefined()
    expect(vm.hotKeywords.length).toBeGreaterThan(0)
  })

  it('should have sortField state', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.sortField).toBeDefined()
  })

  it('should have sortOrder state', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.sortOrder).toBeDefined()
  })

  it('should have hasSearched state', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.hasSearched).toBeDefined()
  })

  it('should have handleSearch method', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(typeof vm.handleSearch).toBe('function')
  })

  it('should have handleHistoryClick method', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(typeof vm.handleHistoryClick).toBe('function')
  })

  it('should have handleHotClick method', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(typeof vm.handleHotClick).toBe('function')
  })

  it('should have clearSearchHistory method', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(typeof vm.clearSearchHistory).toBe('function')
  })

  it('should have handleTabChange method', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(typeof vm.handleTabChange).toBe('function')
  })

  it('should have goToProductDetail method', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(typeof vm.goToProductDetail).toBe('function')
  })

  it('should have goToServiceDetail method', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(typeof vm.goToServiceDetail).toBe('function')
  })

  it('should have goToMerchantDetail method', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(typeof vm.goToMerchantDetail).toBe('function')
  })

  it('should have filteredProducts computed', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.filteredProducts).toBeDefined()
  })

  it('should have filteredServices computed', () => {
    const wrapper = mountUserComponent(SearchPage)
    const vm = wrapper.vm as any
    expect(vm.filteredServices).toBeDefined()
  })
})
