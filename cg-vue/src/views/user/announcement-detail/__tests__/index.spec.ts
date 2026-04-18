import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, mockUserStore, mockAnnouncementApi } from '@/tests/utils/userTestUtils'
import AnnouncementDetail from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/announcement', () => mockAnnouncementApi)

describe('AnnouncementDetail', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    expect(wrapper.exists()).toBe(true)
  })

  it('should have loading state', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    const vm = wrapper.vm as any
    expect(vm.loading).toBeDefined()
  })

  it('should have announcement state', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    const vm = wrapper.vm as any
    expect(vm.announcement).toBeDefined()
  })

  it('should have allAnnouncements state', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    const vm = wrapper.vm as any
    expect(vm.allAnnouncements).toBeDefined()
  })

  it('should have fetchAnnouncement method', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    const vm = wrapper.vm as any
    expect(typeof vm.fetchAnnouncement).toBe('function')
  })

  it('should have fetchAllAnnouncements method', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    const vm = wrapper.vm as any
    expect(typeof vm.fetchAllAnnouncements).toBe('function')
  })

  it('should have handleBack method', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    const vm = wrapper.vm as any
    expect(typeof vm.handleBack).toBe('function')
  })

  it('should have handleViewRelated method', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    const vm = wrapper.vm as any
    expect(typeof vm.handleViewRelated).toBe('function')
  })

  it('should have formatDate method', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    const vm = wrapper.vm as any
    expect(typeof vm.formatDate).toBe('function')
  })

  it('should have relatedAnnouncements computed', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    const vm = wrapper.vm as any
    expect(vm.relatedAnnouncements).toBeDefined()
  })

  it('should format date correctly', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    const vm = wrapper.vm as any
    const formatted = vm.formatDate('2024-01-15')
    expect(formatted).toBeTruthy()
  })

  it('should return empty string for invalid date', () => {
    const wrapper = mountUserComponent(AnnouncementDetail)
    const vm = wrapper.vm as any
    const formatted = vm.formatDate('')
    expect(formatted).toBe('')
  })
})
