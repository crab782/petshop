import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createProduct, createMerchant, mockUserStore, mockProductApi, mockMerchantApi } from '@/tests/utils/userTestUtils'
import ProductDetail from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/product', () => mockProductApi)

vi.mock('@/api/merchant', () => mockMerchantApi)

describe('ProductDetail', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(ProductDetail)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display product information', () => {
    const product = createProduct({
      name: '宠物狗粮',
      description: '优质宠物狗粮',
      price: 99,
    })
    const wrapper = mountUserComponent(ProductDetail, {
      global: {
        mocks: {
          product,
        },
      },
    })
    expect(wrapper.text()).toContain('宠物狗粮') || expect(wrapper.find('.product-name').exists()).toBe(true)
  })

  it('should display product price', () => {
    const product = createProduct({ price: 99 })
    const wrapper = mountUserComponent(ProductDetail, {
      global: {
        mocks: {
          product,
        },
      },
    })
    expect(wrapper.text()).toContain('99') || expect(wrapper.find('.price').exists()).toBe(true)
  })

  it('should display product stock', () => {
    const product = createProduct({ stock: 100 })
    const wrapper = mountUserComponent(ProductDetail, {
      global: {
        mocks: {
          product,
        },
      },
    })
    expect(wrapper.text()).toContain('100') || expect(wrapper.find('.stock').exists()).toBe(true)
  })

  it('should display merchant information', () => {
    const merchant = createMerchant({ name: '测试宠物店' })
    const wrapper = mountUserComponent(ProductDetail, {
      global: {
        mocks: {
          merchant,
        },
      },
    })
    expect(wrapper.text()).toContain('测试宠物店') || expect(wrapper.find('.merchant-info').exists()).toBe(true)
  })

  it('should have quantity selector', () => {
    const wrapper = mountUserComponent(ProductDetail)
    const quantitySelector = wrapper.find('.quantity-selector, .el-input-number')
    expect(quantitySelector.exists() || wrapper.find('.quantity-section').exists()).toBe(true)
  })

  it('should have add to cart button', () => {
    const wrapper = mountUserComponent(ProductDetail)
    const addToCartButton = wrapper.find('[data-testid="add-to-cart"], .add-to-cart-button')
    expect(addToCartButton.exists() || wrapper.text().includes('加入购物车')).toBe(true)
  })

  it('should have buy now button', () => {
    const wrapper = mountUserComponent(ProductDetail)
    const buyNowButton = wrapper.find('[data-testid="buy-now"], .buy-now-button')
    expect(buyNowButton.exists() || wrapper.text().includes('立即购买')).toBe(true)
  })

  it('should have favorite button', () => {
    const wrapper = mountUserComponent(ProductDetail)
    const favoriteButton = wrapper.find('[data-testid="favorite"], .favorite-button')
    expect(favoriteButton.exists() || wrapper.text().includes('收藏')).toBe(true)
  })

  it('should display product reviews', () => {
    const wrapper = mountUserComponent(ProductDetail)
    const reviewsSection = wrapper.find('.reviews-section, .product-reviews')
    expect(reviewsSection.exists() || wrapper.text().includes('评价')).toBe(true)
  })
})
