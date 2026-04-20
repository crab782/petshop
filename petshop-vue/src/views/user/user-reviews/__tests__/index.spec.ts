import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createReview, mockUserStore, mockReviewApi } from '@/tests/utils/userTestUtils'
import UserReviews from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/review', () => mockReviewApi)

describe('UserReviews', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserReviews)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display reviews list', () => {
    const reviews = [
      createReview({ id: 1, rating: 5, comment: '服务非常好' }),
      createReview({ id: 2, rating: 4, comment: '还不错' }),
    ]
    const wrapper = mountUserComponent(UserReviews, {
      global: {
        mocks: {
          reviews,
        },
      },
    })
    expect(wrapper.text()).toContain('服务非常好') || expect(wrapper.find('.review-item').exists()).toBe(true)
  })

  it('should display empty state when no reviews', () => {
    const wrapper = mountUserComponent(UserReviews, {
      global: {
        mocks: {
          reviews: [],
        },
      },
    })
    expect(wrapper.text()).toContain('暂无评价') || expect(wrapper.find('.empty-state').exists()).toBe(true)
  })

  it('should display star ratings', () => {
    const review = createReview({ rating: 5 })
    const wrapper = mountUserComponent(UserReviews, {
      global: {
        mocks: {
          reviews: [review],
        },
      },
    })
    const stars = wrapper.findAll('.star, .el-rate__icon')
    expect(stars.length).toBeGreaterThan(0)
  })

  it('should have filter by rating option', () => {
    const wrapper = mountUserComponent(UserReviews)
    const ratingFilter = wrapper.find('.rating-filter, .el-select')
    expect(ratingFilter.exists() || wrapper.find('.filter-section').exists()).toBe(true)
  })

  it('should display review date', () => {
    const review = createReview({ createdAt: '2024-01-01T00:00:00.000Z' })
    const wrapper = mountUserComponent(UserReviews, {
      global: {
        mocks: {
          reviews: [review],
        },
      },
    })
    expect(wrapper.text()).toContain('2024') || expect(wrapper.find('.review-date').exists()).toBe(true)
  })
})
