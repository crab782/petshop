import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, mockUserStore } from '@/tests/utils/userTestUtils'
import AppointmentConfirm from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/user', () => ({
  getUserPets: vi.fn(() => Promise.resolve({ data: [] })),
  createAppointment: vi.fn(() => Promise.resolve({ data: { id: 1 } })),
  getMerchantInfo: vi.fn(() => Promise.resolve({ data: null })),
}))

describe('AppointmentConfirm', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    expect(wrapper.exists()).toBe(true)
  })

  it('should have loading state', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(vm.loading).toBeDefined()
  })

  it('should have submitting state', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(vm.submitting).toBeDefined()
  })

  it('should have service state', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(vm.service).toBeDefined()
  })

  it('should have merchant state', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(vm.merchant).toBeDefined()
  })

  it('should have pets state', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(vm.pets).toBeDefined()
  })

  it('should have selectedPetId state', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(vm.selectedPetId).toBeDefined()
  })

  it('should have appointmentTime state', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(vm.appointmentTime).toBeDefined()
  })

  it('should have remark state', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(vm.remark).toBeDefined()
  })

  it('should have formRules defined', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(vm.formRules).toBeDefined()
  })

  it('should have fetchServiceInfo method', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(typeof vm.fetchServiceInfo).toBe('function')
  })

  it('should have fetchMerchantInfo method', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(typeof vm.fetchMerchantInfo).toBe('function')
  })

  it('should have fetchPets method', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(typeof vm.fetchPets).toBe('function')
  })

  it('should have handleSubmit method', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(typeof vm.handleSubmit).toBe('function')
  })

  it('should have handleCancel method', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(typeof vm.handleCancel).toBe('function')
  })

  it('should have formatDuration method', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(typeof vm.formatDuration).toBe('function')
  })

  it('should format duration correctly', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(vm.formatDuration(60)).toBe('1小时')
    expect(vm.formatDuration(90)).toBe('1小时30分钟')
    expect(vm.formatDuration(30)).toBe('30分钟')
    expect(vm.formatDuration(undefined)).toBe('60分钟')
  })

  it('should have disabledDate method', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(typeof vm.disabledDate).toBe('function')
  })

  it('should have disabledHours method', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(typeof vm.disabledHours).toBe('function')
  })

  it('should have formData computed', () => {
    const wrapper = mountUserComponent(AppointmentConfirm)
    const vm = wrapper.vm as any
    expect(vm.formData).toBeDefined()
  })
})
