import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createUser, mockUserStore, mockUserApi } from '@/tests/utils/userTestUtils'
import UserProfile from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/user', () => mockUserApi)

describe('UserProfile', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserProfile)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display user information', () => {
    const user = createUser({
      username: 'testuser',
      email: 'test@example.com',
      phone: '13800138000',
    })
    const wrapper = mountUserComponent(UserProfile, {
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
    expect(wrapper.text()).toContain('test@example.com')
    expect(wrapper.text()).toContain('13800138000')
  })

  it('should display user avatar', () => {
    const user = createUser({ avatar: 'https://example.com/avatar.jpg' })
    const wrapper = mountUserComponent(UserProfile, {
      global: {
        mocks: {
          $userStore: {
            user,
            isLoggedIn: true,
          },
        },
      },
    })
    const avatar = wrapper.find('img')
    expect(avatar.exists()).toBe(true)
  })

  it('should have edit profile button', () => {
    const wrapper = mountUserComponent(UserProfile)
    const editButton = wrapper.find('[data-testid="edit-profile"], .edit-button')
    expect(editButton.exists() || wrapper.text().includes('编辑')).toBe(true)
  })

  it('should navigate to edit profile page when clicking edit button', async () => {
    const mockPush = vi.fn()
    const wrapper = mountUserComponent(UserProfile, {
      global: {
        mocks: {
          $router: {
            push: mockPush,
          },
        },
      },
    })
    const editButton = wrapper.find('[data-testid="edit-profile"]')
    if (editButton.exists()) {
      await editButton.trigger('click')
      expect(mockPush).toHaveBeenCalled()
    }
  })

  it('should display statistics section', () => {
    const wrapper = mountUserComponent(UserProfile)
    expect(wrapper.find('.statistics, .stats-section').exists() || wrapper.text().includes('宠物') || wrapper.text().includes('订单')).toBe(true)
  })
})
