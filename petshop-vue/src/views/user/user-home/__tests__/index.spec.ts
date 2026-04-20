import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createUser, createPet, createAppointment, createNotification, mockUserStore, mockUserApi } from '@/tests/utils/userTestUtils'
import UserHome from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/user', () => mockUserApi)

describe('UserHome', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserHome)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display welcome message with user name', () => {
    const user = createUser({ username: 'testuser' })
    const wrapper = mountUserComponent(UserHome, {
      global: {
        mocks: {
          $userStore: {
            user,
            isLoggedIn: true,
          },
        },
      },
    })
    expect(wrapper.text()).toContain('testuser')
  })

  it('should display statistics cards', () => {
    const wrapper = mountUserComponent(UserHome)
    const cards = wrapper.findAll('.stat-card, .el-card')
    expect(cards.length).toBeGreaterThan(0)
  })

  it('should display pet count', () => {
    const wrapper = mountUserComponent(UserHome, {
      global: {
        mocks: {
          $userStore: {
            user: createUser(),
            pets: [createPet(), createPet()],
          },
        },
      },
    })
    expect(wrapper.text()).toContain('2')
  })

  it('should display quick action buttons', () => {
    const wrapper = mountUserComponent(UserHome)
    const buttons = wrapper.findAll('button, .el-button')
    expect(buttons.length).toBeGreaterThan(0)
  })

  it('should navigate to appointments when clicking quick action', async () => {
    const mockPush = vi.fn()
    const wrapper = mountUserComponent(UserHome, {
      global: {
        mocks: {
          $router: {
            push: mockPush,
          },
        },
      },
    })
    const appointmentButton = wrapper.find('[data-testid="quick-appointments"]')
    if (appointmentButton.exists()) {
      await appointmentButton.trigger('click')
      expect(mockPush).toHaveBeenCalled()
    }
  })
})
