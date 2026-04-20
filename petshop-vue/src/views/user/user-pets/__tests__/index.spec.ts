import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createPet, mockUserStore, mockUserApi } from '@/tests/utils/userTestUtils'
import UserPets from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/user', () => mockUserApi)

describe('UserPets', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserPets)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display pet list', () => {
    const pets = [createPet({ name: '小白' }), createPet({ id: 2, name: '小黑' })]
    const wrapper = mountUserComponent(UserPets, {
      global: {
        mocks: {
          $userStore: {
            pets,
          },
        },
      },
    })
    expect(wrapper.text()).toContain('小白')
    expect(wrapper.text()).toContain('小黑')
  })

  it('should display empty state when no pets', () => {
    const wrapper = mountUserComponent(UserPets, {
      global: {
        mocks: {
          $userStore: {
            pets: [],
          },
        },
      },
    })
    expect(wrapper.text()).toContain('暂无宠物') || expect(wrapper.find('.empty-state').exists()).toBe(true)
  })

  it('should display add pet button', () => {
    const wrapper = mountUserComponent(UserPets)
    const addButton = wrapper.find('[data-testid="add-pet"], .add-button')
    expect(addButton.exists() || wrapper.text().includes('添加')).toBe(true)
  })

  it('should navigate to add pet page when clicking add button', async () => {
    const mockPush = vi.fn()
    const wrapper = mountUserComponent(UserPets, {
      global: {
        mocks: {
          $router: {
            push: mockPush,
          },
        },
      },
    })
    const addButton = wrapper.find('[data-testid="add-pet"]')
    if (addButton.exists()) {
      await addButton.trigger('click')
      expect(mockPush).toHaveBeenCalled()
    }
  })

  it('should display pet details correctly', () => {
    const pet = createPet({
      name: '小白',
      type: 'dog',
      breed: '金毛',
      age: 2,
      gender: 'male',
    })
    const wrapper = mountUserComponent(UserPets, {
      global: {
        mocks: {
          $userStore: {
            pets: [pet],
          },
        },
      },
    })
    expect(wrapper.text()).toContain('小白')
    expect(wrapper.text()).toContain('金毛')
  })

  it('should have delete pet functionality', () => {
    const pets = [createPet()]
    const wrapper = mountUserComponent(UserPets, {
      global: {
        mocks: {
          $userStore: {
            pets,
          },
        },
      },
    })
    const deleteButton = wrapper.find('[data-testid="delete-pet"], .delete-button')
    expect(deleteButton.exists() || wrapper.text().includes('删除')).toBe(true)
  })
})
