import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, mockUserStore, mockAnnouncementApi } from '@/tests/utils/userTestUtils'
import UserAnnouncements from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/announcement', () => mockAnnouncementApi)

describe('UserAnnouncements', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display page title', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    expect(wrapper.text()).toContain('平台公告')
  })

  it('should have filter card', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    expect(wrapper.find('.filter-card').exists()).toBe(true)
  })

  it('should have list card', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    expect(wrapper.find('.list-card').exists()).toBe(true)
  })

  it('should have categories defined', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(vm.categories).toBeDefined()
    expect(vm.categories.length).toBeGreaterThan(0)
  })

  it('should have searchKeyword state', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(vm.searchKeyword).toBeDefined()
  })

  it('should have selectedCategory state', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(vm.selectedCategory).toBeDefined()
  })

  it('should have announcementList state', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(vm.announcementList).toBeDefined()
  })

  it('should have loading state', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(vm.loading).toBeDefined()
  })

  it('should have dialogVisible state', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(vm.dialogVisible).toBeDefined()
  })

  it('should have handleViewDetail method', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(typeof vm.handleViewDetail).toBe('function')
  })

  it('should have handleSearch method', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(typeof vm.handleSearch).toBe('function')
  })

  it('should have handleCategoryChange method', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(typeof vm.handleCategoryChange).toBe('function')
  })

  it('should have isRead method', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(typeof vm.isRead).toBe('function')
  })

  it('should have getCategoryLabel method', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(typeof vm.getCategoryLabel).toBe('function')
  })

  it('should have getCategoryType method', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(typeof vm.getCategoryType).toBe('function')
  })

  it('should have filteredList computed', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(vm.filteredList).toBeDefined()
  })

  it('should have fetchAnnouncements method', () => {
    const wrapper = mountUserComponent(UserAnnouncements)
    const vm = wrapper.vm as any
    expect(typeof vm.fetchAnnouncements).toBe('function')
  })
})
