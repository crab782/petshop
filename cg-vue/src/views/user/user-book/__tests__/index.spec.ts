import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, mockUserStore } from '@/tests/utils/userTestUtils'
import UserBook from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/user', () => ({
  getUserAppointments: vi.fn(() => Promise.resolve({ data: [] })),
  cancelAppointment: vi.fn(() => Promise.resolve({ data: { success: true } })),
}))

describe('UserBook', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserBook)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display page title', () => {
    const wrapper = mountUserComponent(UserBook)
    expect(wrapper.text()).toContain('我的预约记录')
  })

  it('should display page description', () => {
    const wrapper = mountUserComponent(UserBook)
    expect(wrapper.text()).toContain('查看和管理您的服务预约')
  })

  it('should have loading state', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(vm.loading).toBeDefined()
  })

  it('should have appointments state', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(vm.appointments).toBeDefined()
  })

  it('should have statusFilter state', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(vm.statusFilter).toBeDefined()
  })

  it('should have searchKeyword state', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(vm.searchKeyword).toBeDefined()
  })

  it('should have dateRange state', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(vm.dateRange).toBeDefined()
  })

  it('should have cancelDialogVisible state', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(vm.cancelDialogVisible).toBeDefined()
  })

  it('should have detailDialogVisible state', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(vm.detailDialogVisible).toBeDefined()
  })

  it('should have statusOptions defined', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(vm.statusOptions).toBeDefined()
    expect(vm.statusOptions.length).toBeGreaterThan(0)
  })

  it('should have fetchAppointments method', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(typeof vm.fetchAppointments).toBe('function')
  })

  it('should have handleSearch method', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(typeof vm.handleSearch).toBe('function')
  })

  it('should have handleDetail method', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(typeof vm.handleDetail).toBe('function')
  })

  it('should have handleCancel method', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(typeof vm.handleCancel).toBe('function')
  })

  it('should have confirmCancel method', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(typeof vm.confirmCancel).toBe('function')
  })

  it('should have canCancel method', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(typeof vm.canCancel).toBe('function')
    expect(vm.canCancel('pending')).toBe(true)
    expect(vm.canCancel('confirmed')).toBe(true)
    expect(vm.canCancel('completed')).toBe(false)
    expect(vm.canCancel('cancelled')).toBe(false)
  })

  it('should have formatDate method', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(typeof vm.formatDate).toBe('function')
  })

  it('should have statusMap defined', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(vm.statusMap).toBeDefined()
    expect(vm.statusMap['pending']).toBe('待处理')
    expect(vm.statusMap['confirmed']).toBe('已确认')
    expect(vm.statusMap['completed']).toBe('已完成')
    expect(vm.statusMap['cancelled']).toBe('已取消')
  })

  it('should have statusTagType method', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(typeof vm.statusTagType).toBe('function')
    expect(vm.statusTagType('pending')).toBe('warning')
    expect(vm.statusTagType('confirmed')).toBe('primary')
    expect(vm.statusTagType('completed')).toBe('success')
    expect(vm.statusTagType('cancelled')).toBe('info')
  })

  it('should have filteredAppointments computed', () => {
    const wrapper = mountUserComponent(UserBook)
    const vm = wrapper.vm as any
    expect(vm.filteredAppointments).toBeDefined()
  })
})
