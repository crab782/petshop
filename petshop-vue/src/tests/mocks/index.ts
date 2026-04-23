export * from './merchantMocks'
export * from './authMocks'
export * from './userMocks'
export * from './merchantApiMocks'
export * from './adminMocks'
export * from './announcementMocks'
export * from './notificationMocks'
export * from './searchMocks'

import { vi } from 'vitest'
import { mockAuthApi } from './authMocks'
import { mockUserApi } from './userMocks'
import { mockMerchantApi } from './merchantApiMocks'
import { mockAdminApi } from './adminMocks'
import { mockAnnouncementApi } from './announcementMocks'
import { mockNotificationApi } from './notificationMocks'
import { mockSearchApi } from './searchMocks'

export const mockAllApis = () => ({
  auth: mockAuthApi,
  user: mockUserApi,
  merchant: mockMerchantApi,
  admin: mockAdminApi,
  announcement: mockAnnouncementApi,
  notification: mockNotificationApi,
  search: mockSearchApi,
})

export const resetAllMocks = () => {
  vi.clearAllMocks()
}

export const createSuccessResponse = <T>(data: T, message = 'success') => ({
  code: 200,
  message,
  data,
})

export const createErrorResponse = (message: string, code = 400) => ({
  code,
  message,
  data: null,
})

export const createPaginatedResponse = <T>(
  data: T[],
  page = 1,
  pageSize = 10
): { data: T[]; total: number; page: number; pageSize: number; totalPages: number } => {
  const start = (page - 1) * pageSize
  const end = start + pageSize
  return {
    data: data.slice(start, end),
    total: data.length,
    page,
    pageSize,
    totalPages: Math.ceil(data.length / pageSize),
  }
}
