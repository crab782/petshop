import request from './request'

export interface Announcement {
  id: number
  title: string
  content: string
  status: 'published' | 'draft'
  publishTime: string
}

export const getAnnouncements = () => {
  return request.get<Announcement[]>('/api/announcements')
}

export const getAnnouncementById = (id: number) => {
  return request.get<Announcement>(`/api/announcements/${id}`)
}

export const addAnnouncement = (data: { title: string; content: string }) => {
  return request.post<Announcement>('/api/announcements', data)
}

export const updateAnnouncement = (id: number, data: { title: string; content: string }) => {
  return request.put<Announcement>(`/api/announcements/${id}`, data)
}

export const deleteAnnouncement = (id: number) => {
  return request.delete<void>(`/api/announcements/${id}`)
}

export const publishAnnouncement = (id: number) => {
  return request.put<Announcement>(`/api/announcements/${id}/publish`, {})
}

export const unpublishAnnouncement = (id: number) => {
  return request.put<Announcement>(`/api/announcements/${id}/unpublish`, {})
}
