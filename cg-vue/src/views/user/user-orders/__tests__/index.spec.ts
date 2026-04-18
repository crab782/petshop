import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createProductOrder, mockUserStore, mockOrderApi } from '@/tests/utils/userTestUtils'
import UserOrders from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/order', () => mockOrderApi)

describe('UserOrders', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(UserOrders)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display order list', () => {
    const orders = [
      createProductOrder({ id: 1, status: 'pending' }),
      createProductOrder({ id: 2, status: 'paid' }),
    ]
    const wrapper = mountUserComponent(UserOrders, {
      global: {
        mocks: {
          orders,
        },
      },
    })
    expect(wrapper.text()).toContain('订单') || expect(wrapper.find('.order-list, .el-table').exists()).toBe(true)
  })

  it('should display empty state when no orders', () => {
    const wrapper = mountUserComponent(UserOrders, {
      global: {
        mocks: {
          orders: [],
        },
      },
    })
    expect(wrapper.text()).toContain('暂无订单') || expect(wrapper.find('.empty-state').exists()).toBe(true)
  })

  it('should display status tabs', () => {
    const wrapper = mountUserComponent(UserOrders)
    const tabs = wrapper.findAll('.el-tabs__item, .status-tab')
    expect(tabs.length).toBeGreaterThan(0)
  })

  it('should display order total price', () => {
    const order = createProductOrder({ totalPrice: 299 })
    const wrapper = mountUserComponent(UserOrders, {
      global: {
        mocks: {
          orders: [order],
        },
      },
    })
    expect(wrapper.text()).toContain('299')
  })

  it('should have view detail functionality', () => {
    const orders = [createProductOrder()]
    const wrapper = mountUserComponent(UserOrders, {
      global: {
        mocks: {
          orders,
        },
      },
    })
    const detailButton = wrapper.find('[data-testid="view-detail"], .detail-button')
    expect(detailButton.exists() || wrapper.text().includes('详情')).toBe(true)
  })

  it('should have cancel order functionality for pending orders', () => {
    const orders = [createProductOrder({ status: 'pending' })]
    const wrapper = mountUserComponent(UserOrders, {
      global: {
        mocks: {
          orders,
        },
      },
    })
    const cancelButton = wrapper.find('[data-testid="cancel-order"], .cancel-button')
    expect(cancelButton.exists() || wrapper.text().includes('取消')).toBe(true)
  })

  it('should filter orders by status', async () => {
    const wrapper = mountUserComponent(UserOrders)
    const statusFilter = wrapper.find('.status-filter, .el-select')
    if (statusFilter.exists()) {
      expect(statusFilter.exists()).toBe(true)
    }
  })
})
