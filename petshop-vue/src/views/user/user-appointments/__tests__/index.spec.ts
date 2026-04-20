import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createAppointment, mockUserStore, mockAppointmentApi } from '@/tests/utils/userTestUtils'
import UserAppointments from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/appointment', () => mockAppointmentApi)

describe('UserAppointments', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserAppointments)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display appointment list', () => {
    const appointments = [
      createAppointment({ id: 1, status: 'pending' }),
      createAppointment({ id: 2, status: 'confirmed' }),
    ]
    const wrapper = mountUserComponent(UserAppointments, {
      global: {
        mocks: {
          appointments,
        },
      },
    })
    expect(wrapper.text()).toContain('预约') || expect(wrapper.find('.appointment-list, .el-table').exists()).toBe(true)
  })

  it('should display empty state when no appointments', () => {
    const wrapper = mountUserComponent(UserAppointments, {
      global: {
        mocks: {
          appointments: [],
        },
      },
    })
    expect(wrapper.text()).toContain('暂无预约') || expect(wrapper.find('.empty-state').exists()).toBe(true)
  })

  it('should display status badges correctly', () => {
    const appointments = [
      createAppointment({ status: 'pending' }),
      createAppointment({ status: 'confirmed' }),
      createAppointment({ status: 'completed' }),
      createAppointment({ status: 'cancelled' }),
    ]
    const wrapper = mountUserComponent(UserAppointments, {
      global: {
        mocks: {
          appointments,
        },
      },
    })
    const badges = wrapper.findAll('.status-badge, .el-tag')
    expect(badges.length).toBeGreaterThan(0)
  })

  it('should have filter options', () => {
    const wrapper = mountUserComponent(UserAppointments)
    const filterElements = wrapper.findAll('.filter, .el-select, .el-date-picker')
    expect(filterElements.length).toBeGreaterThan(0)
  })

  it('should have cancel appointment functionality', () => {
    const appointments = [createAppointment({ status: 'pending' })]
    const wrapper = mountUserComponent(UserAppointments, {
      global: {
        mocks: {
          appointments,
        },
      },
    })
    const cancelButton = wrapper.find('[data-testid="cancel-appointment"], .cancel-button')
    expect(cancelButton.exists() || wrapper.text().includes('取消')).toBe(true)
  })

  it('should display appointment details', () => {
    const appointment = createAppointment({
      totalPrice: 100,
      notes: '请准时到达',
    })
    const wrapper = mountUserComponent(UserAppointments, {
      global: {
        mocks: {
          appointments: [appointment],
        },
      },
    })
    expect(wrapper.text()).toContain('100') || expect(wrapper.find('.appointment-detail').exists()).toBe(true)
  })
})
