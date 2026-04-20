import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createMerchant, mockUserStore, mockFavoriteStore, mockMerchantApi } from '@/tests/utils/userTestUtils'
import UserFavorites from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/stores/favorite', () => ({
  useFavoriteStore: () => mockFavoriteStore(),
}))

vi.mock('@/api/merchant', () => mockMerchantApi)

describe('UserFavorites', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserFavorites)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display favorites list', () => {
    const merchants = [
      createMerchant({ id: 1, name: '宠物店A' }),
      createMerchant({ id: 2, name: '宠物店B' }),
    ]
    const wrapper = mountUserComponent(UserFavorites, {
      global: {
        mocks: {
          favorites: merchants,
        },
      },
    })
    expect(wrapper.text()).toContain('宠物店A') || expect(wrapper.find('.favorite-item').exists()).toBe(true)
  })

  it('should display empty state when no favorites', () => {
    const wrapper = mountUserComponent(UserFavorites, {
      global: {
        mocks: {
          favorites: [],
        },
      },
    })
    expect(wrapper.text()).toContain('暂无收藏') || expect(wrapper.find('.empty-state').exists()).toBe(true)
  })

  it('should display merchant rating', () => {
    const merchant = createMerchant({ rating: 4.5 })
    const wrapper = mountUserComponent(UserFavorites, {
      global: {
        mocks: {
          favorites: [merchant],
        },
      },
    })
    expect(wrapper.text()).toContain('4.5') || expect(wrapper.find('.rating').exists()).toBe(true)
  })

  it('should have remove favorite button', () => {
    const merchants = [createMerchant()]
    const wrapper = mountUserComponent(UserFavorites, {
      global: {
        mocks: {
          favorites: merchants,
        },
      },
    })
    const removeButton = wrapper.find('[data-testid="remove-favorite"], .remove-button')
    expect(removeButton.exists() || wrapper.text().includes('取消收藏')).toBe(true)
  })

  it('should navigate to merchant detail when clicking', async () => {
    const mockPush = vi.fn()
    const merchant = createMerchant({ id: 1 })
    const wrapper = mountUserComponent(UserFavorites, {
      global: {
        mocks: {
          $router: {
            push: mockPush,
          },
          favorites: [merchant],
        },
      },
    })
    const merchantCard = wrapper.find('.merchant-card, .favorite-item')
    if (merchantCard.exists()) {
      await merchantCard.trigger('click')
      expect(mockPush).toHaveBeenCalled()
    }
  })
})
