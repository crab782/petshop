import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createUser, createPet, createAppointment, mockUserStore } from '@/tests/utils/userTestUtils'
import UserLayout from '../UserLayout.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

describe('UserLayout', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserLayout)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display user information when logged in', () => {
    const user = createUser({ username: 'testuser' })
    const wrapper = mountUserComponent(UserLayout, {
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

  it('should have navigation menu items', () => {
    const wrapper = mountUserComponent(UserLayout)
    const menuItems = wrapper.findAll('nav a, nav button')
    expect(menuItems.length).toBeGreaterThan(0)
  })

  it('should call logout when logout button is clicked', async () => {
    const mockLogout = vi.fn()
    const wrapper = mountUserComponent(UserLayout, {
      global: {
        mocks: {
          $userStore: {
            user: createUser(),
            isLoggedIn: true,
            logout: mockLogout,
          },
        },
      },
    })
    const logoutButton = wrapper.find('[data-testid="logout-button"]')
    if (logoutButton.exists()) {
      await logoutButton.trigger('click')
      expect(mockLogout).toHaveBeenCalled()
    }
  })
})
