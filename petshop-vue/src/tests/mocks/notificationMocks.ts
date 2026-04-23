import { vi } from 'vitest'

export interface MockNotification {
  id: number
  type: 'system' | 'order' | 'appointment' | 'review'
  title: string
  summary: string
  content: string
  isRead: boolean
  createTime: string
}

export const defaultNotification: MockNotification = {
  id: 1,
  type: 'system',
  title: '系统通知',
  summary: '欢迎使用宠物服务平台',
  content: '欢迎使用宠物服务平台，祝您使用愉快！如有任何问题，请联系客服。',
  isRead: false,
  createTime: '2024-04-20T10:00:00.000Z',
}

export const createNotification = (overrides: Partial<MockNotification> = {}): MockNotification => ({
  ...defaultNotification,
  ...overrides,
})

export const createNotificationList = (count: number): MockNotification[] =>
  Array.from({ length: count }, (_, i) =>
    createNotification({
      id: i + 1,
      title: `通知${i + 1}`,
      isRead: i > 2,
      type: ['system', 'order', 'appointment', 'review'][i % 4] as any,
    })
  )

export const mockNotificationApi = {
  getNotifications: vi.fn((params?: { type?: string; isRead?: boolean }) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createNotificationList(10),
    })
  ),

  markAsRead: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '标记成功',
      data: null,
    })
  ),

  markAllAsRead: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: '全部标记已读成功',
      data: null,
    })
  ),

  markBatchAsRead: vi.fn((ids: number[]) =>
    Promise.resolve({
      code: 200,
      message: '批量标记已读成功',
      data: null,
    })
  ),

  deleteNotification: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  deleteBatchNotifications: vi.fn((ids: number[]) =>
    Promise.resolve({
      code: 200,
      message: '批量删除成功',
      data: null,
    })
  ),

  getUnreadCount: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: { count: 5 },
    })
  ),
}

export const mockNotificationResponses = {
  notificationList: {
    code: 200,
    message: 'success',
    data: [defaultNotification],
  },
  unreadCount: {
    code: 200,
    message: 'success',
    data: { count: 5 },
  },
}
