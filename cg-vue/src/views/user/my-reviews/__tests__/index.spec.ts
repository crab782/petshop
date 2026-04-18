import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, mockUserStore } from '@/tests/utils/userTestUtils'
import MyReviews from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/user', () => ({
  getUserReviews: vi.fn(() => Promise.resolve({ data: [] })),
  updateReview: vi.fn(() => Promise.resolve({ data: { success: true } })),
  deleteReview: vi.fn(() => Promise.resolve({ data: { success: true } })),
}))

describe('MyReviews', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(MyReviews)
    expect(wrapper.exists()).toBe(true)
  })

  it('should have loading state', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(vm.loading).toBeDefined()
  })

  it('should have reviews state', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(vm.reviews).toBeDefined()
  })

  it('should have currentPage state', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(vm.currentPage).toBeDefined()
  })

  it('should have pageSize state', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(vm.pageSize).toBeDefined()
  })

  it('should have total state', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(vm.total).toBeDefined()
  })

  it('should have filterType state', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(vm.filterType).toBeDefined()
  })

  it('should have dateRange state', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(vm.dateRange).toBeDefined()
  })

  it('should have editDialogVisible state', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(vm.editDialogVisible).toBeDefined()
  })

  it('should have editForm state', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(vm.editForm).toBeDefined()
  })

  it('should have fetchReviews method', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(typeof vm.fetchReviews).toBe('function')
  })

  it('should have handleFilter method', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(typeof vm.handleFilter).toBe('function')
  })

  it('should have handleReset method', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(typeof vm.handleReset).toBe('function')
  })

  it('should have handleEdit method', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(typeof vm.handleEdit).toBe('function')
  })

  it('should have handleEditSubmit method', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(typeof vm.handleEditSubmit).toBe('function')
  })

  it('should have handleDelete method', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(typeof vm.handleDelete).toBe('function')
  })

  it('should have handlePageChange method', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(typeof vm.handlePageChange).toBe('function')
  })

  it('should have handleSizeChange method', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(typeof vm.handleSizeChange).toBe('function')
  })

  it('should have replyTagType method', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(typeof vm.replyTagType).toBe('function')
    expect(vm.replyTagType('已回复')).toBe('success')
    expect(vm.replyTagType('待回复')).toBe('warning')
  })

  it('should have paginatedReviews computed', () => {
    const wrapper = mountUserComponent(MyReviews)
    const vm = wrapper.vm as any
    expect(vm.paginatedReviews).toBeDefined()
  })
})
