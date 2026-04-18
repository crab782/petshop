import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createNotification, mockUserStore, mockNotificationApi } from '@/tests/utils/userTestUtils'
import Notifications from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/notification', () => mockNotificationApi)

describe('Notifications', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(Notifications)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display notification list', () => {
    const notifications = [
      createNotification({ id: 1, title: '预约提醒', content: '您有一个预约即将开始' }),
      createNotification({ id: 2, title: '订单通知', content: '您的订单已发货' }),
    ]
    const wrapper = mountUserComponent(Notifications, {
      global: {
        mocks: {
          notifications,
        },
      },
    })
    expect(wrapper.text()).toContain('预约提醒') || expect(wrapper.find('.notification-item').exists()).toBe(true)
  })

  it('should display empty state when no notifications', () => {
    const wrapper = mountUserComponent(Notifications, {
      global: {
        mocks: {
          notifications: [],
        },
      },
    })
    expect(wrapper.text()).toContain('暂无通知') || expect(wrapper.find('.empty-state').exists()).toBe(true)
  })

  it('should display unread badge', () => {
    const notifications = [createNotification({ isRead: false })]
    const wrapper = mountUserComponent(Notifications, {
      global: {
        mocks: {
          notifications,
        },
      },
    })
    const unreadBadge = wrapper.find('.unread-badge, .el-badge')
    expect(unreadBadge.exists() || wrapper.find('.unread').exists()).toBe(true)
  })

  it('should have mark as read functionality', () => {
    const notifications = [createNotification({ isRead: false })]
    const wrapper = mountUserComponent(Notifications, {
      global: {
        mocks: {
          notifications,
        },
      },
    })
    const markReadButton = wrapper.find('[data-testid="mark-read"], .mark-read-button')
    expect(markReadButton.exists() || wrapper.text().includes('已读')).toBe(true)
  })

  it('should have mark all as read button', () => {
    const wrapper = mountUserComponent(Notifications)
    const markAllButton = wrapper.find('[data-testid="mark-all-read"], .mark-all-button')
    expect(markAllButton.exists() || wrapper.text().includes('全部已读')).toBe(true)
  })

  it('should have filter by type option', () => {
    const wrapper = mountUserComponent(Notifications)
    const typeFilter = wrapper.find('.type-filter, .el-select')
    expect(typeFilter.exists() || wrapper.find('.filter-section').exists()).toBe(true)
  })

  it('should display notification time', () => {
    const notification = createNotification({ createdAt: '2024-01-01T10:00:00.000Z' })
    const wrapper = mountUserComponent(Notifications, {
      global: {
        mocks: {
          notifications: [notification],
        },
      },
    })
    expect(wrapper.text()).toContain('2024') || expect(wrapper.find('.notification-time').exists()).toBe(true)
  })
})
