import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, mockUserStore, mockServiceApi } from '@/tests/utils/userTestUtils'
import UserServices from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/service', () => mockServiceApi)

describe('UserServices', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserServices)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display page title', () => {
    const wrapper = mountUserComponent(UserServices)
    expect(wrapper.text()).toContain('我的服务')
  })

  it('should display page description', () => {
    const wrapper = mountUserComponent(UserServices)
    expect(wrapper.text()).toContain('管理您已购买的宠物服务')
  })

  it('should have search section', () => {
    const wrapper = mountUserComponent(UserServices)
    expect(wrapper.find('.search-section').exists()).toBe(true)
  })

  it('should have services content section', () => {
    const wrapper = mountUserComponent(UserServices)
    expect(wrapper.find('.services-content').exists()).toBe(true)
  })

  it('should have status options defined', () => {
    const wrapper = mountUserComponent(UserServices)
    const vm = wrapper.vm as any
    expect(vm.statusOptions).toBeDefined()
    expect(vm.statusOptions.length).toBeGreaterThan(0)
  })

  it('should have myServices data', () => {
    const wrapper = mountUserComponent(UserServices)
    const vm = wrapper.vm as any
    expect(vm.myServices).toBeDefined()
    expect(vm.myServices.length).toBeGreaterThan(0)
  })

  it('should have filterServices method', () => {
    const wrapper = mountUserComponent(UserServices)
    const vm = wrapper.vm as any
    expect(typeof vm.filterServices).toBe('function')
  })

  it('should have handleSearch method', () => {
    const wrapper = mountUserComponent(UserServices)
    const vm = wrapper.vm as any
    expect(typeof vm.handleSearch).toBe('function')
  })

  it('should have handleStatusChange method', () => {
    const wrapper = mountUserComponent(UserServices)
    const vm = wrapper.vm as any
    expect(typeof vm.handleStatusChange).toBe('function')
  })

  it('should have handlePageChange method', () => {
    const wrapper = mountUserComponent(UserServices)
    const vm = wrapper.vm as any
    expect(typeof vm.handlePageChange).toBe('function')
  })

  it('should have handleDetail method', () => {
    const wrapper = mountUserComponent(UserServices)
    const vm = wrapper.vm as any
    expect(typeof vm.handleDetail).toBe('function')
  })

  it('should have getStatusLabel method', () => {
    const wrapper = mountUserComponent(UserServices)
    const vm = wrapper.vm as any
    expect(typeof vm.getStatusLabel).toBe('function')
    expect(vm.getStatusLabel('active')).toBe('待使用')
    expect(vm.getStatusLabel('used')).toBe('已使用')
    expect(vm.getStatusLabel('expired')).toBe('已过期')
  })

  it('should have getStatusType method', () => {
    const wrapper = mountUserComponent(UserServices)
    const vm = wrapper.vm as any
    expect(typeof vm.getStatusType).toBe('function')
    expect(vm.getStatusType('active')).toBe('success')
    expect(vm.getStatusType('used')).toBe('info')
    expect(vm.getStatusType('expired')).toBe('danger')
  })

  it('should have loading state', () => {
    const wrapper = mountUserComponent(UserServices)
    const vm = wrapper.vm as any
    expect(vm.loading).toBeDefined()
  })

  it('should have pagination state', () => {
    const wrapper = mountUserComponent(UserServices)
    const vm = wrapper.vm as any
    expect(vm.currentPage).toBeDefined()
    expect(vm.pageSize).toBeDefined()
    expect(vm.total).toBeDefined()
  })
})
