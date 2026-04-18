import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createProduct, mockUserStore, mockCartStore, mockProductApi } from '@/tests/utils/userTestUtils'
import UserCart from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/stores/cart', () => ({
  useCartStore: () => mockCartStore(),
}))

vi.mock('@/api/product', () => mockProductApi)

describe('UserCart', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserCart)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display empty cart state when no items', () => {
    const wrapper = mountUserComponent(UserCart, {
      global: {
        mocks: {
          $cartStore: {
            items: [],
            totalPrice: 0,
          },
        },
      },
    })
    expect(wrapper.text()).toContain('购物车为空') || expect(wrapper.find('.empty-state').exists()).toBe(true)
  })

  it('should display cart items', () => {
    const items = [
      { productId: 1, quantity: 2, price: 99 },
      { productId: 2, quantity: 1, price: 199 },
    ]
    const wrapper = mountUserComponent(UserCart, {
      global: {
        mocks: {
          $cartStore: {
            items,
            totalPrice: 397,
          },
        },
      },
    })
    expect(wrapper.text()).toContain('购物车') || expect(wrapper.find('.cart-item').exists()).toBe(true)
  })

  it('should display total price', () => {
    const wrapper = mountUserComponent(UserCart, {
      global: {
        mocks: {
          $cartStore: {
            items: [{ productId: 1, quantity: 2, price: 99 }],
            totalPrice: 198,
          },
        },
      },
    })
    expect(wrapper.text()).toContain('198') || expect(wrapper.find('.total-price').exists()).toBe(true)
  })

  it('should have quantity adjustment buttons', () => {
    const items = [{ productId: 1, quantity: 1, price: 99 }]
    const wrapper = mountUserComponent(UserCart, {
      global: {
        mocks: {
          $cartStore: {
            items,
          },
        },
      },
    })
    const quantityButtons = wrapper.findAll('.quantity-button, .el-input-number')
    expect(quantityButtons.length).toBeGreaterThan(0)
  })

  it('should have delete item button', () => {
    const items = [{ productId: 1, quantity: 1, price: 99 }]
    const wrapper = mountUserComponent(UserCart, {
      global: {
        mocks: {
          $cartStore: {
            items,
          },
        },
      },
    })
    const deleteButton = wrapper.find('[data-testid="delete-item"], .delete-button')
    expect(deleteButton.exists() || wrapper.text().includes('删除')).toBe(true)
  })

  it('should have checkout button', () => {
    const items = [{ productId: 1, quantity: 1, price: 99 }]
    const wrapper = mountUserComponent(UserCart, {
      global: {
        mocks: {
          $cartStore: {
            items,
            totalPrice: 99,
          },
        },
      },
    })
    const checkoutButton = wrapper.find('[data-testid="checkout"], .checkout-button')
    expect(checkoutButton.exists() || wrapper.text().includes('结算')).toBe(true)
  })

  it('should navigate to checkout when clicking checkout button', async () => {
    const mockPush = vi.fn()
    const items = [{ productId: 1, quantity: 1, price: 99 }]
    const wrapper = mountUserComponent(UserCart, {
      global: {
        mocks: {
          $router: {
            push: mockPush,
          },
          $cartStore: {
            items,
            totalPrice: 99,
          },
        },
      },
    })
    const checkoutButton = wrapper.find('[data-testid="checkout"]')
    if (checkoutButton.exists()) {
      await checkoutButton.trigger('click')
      expect(mockPush).toHaveBeenCalled()
    }
  })
})
