import request from './request'

export interface Notification {
  id: number
  type: 'system' | 'order' | 'appointment' | 'review'
  title: string
  summary: string
  content: string
  isRead: boolean
  createTime: string
}

export const getNotifications = (params?: { type?: string; isRead?: boolean }) => {
  return request.get<Notification[]>('/api/user/notifications', { params })
}

export const markAsRead = (id: number) => {
  return request.put(`/api/user/notifications/${id}/read`)
}

export const markAllAsRead = () => {
  return request.put('/api/user/notifications/read-all')
}

export const markBatchAsRead = (ids: number[]) => {
  return request.put('/api/user/notifications/batch-read', { ids })
}

export const deleteNotification = (id: number) => {
  return request.delete(`/api/user/notifications/${id}`)
}

export const deleteBatchNotifications = (ids: number[]) => {
  return request.delete('/api/user/notifications/batch', { data: { ids } })
}
