import Mock from 'mockjs'
import { announcementsData, announcementStats, type Announcement, type AnnouncementCategory } from '../data/announcements'
import { randomId } from '../../utils/random'

const announcementsList = [...announcementsData]

const parseQuery = (url: string) => {
  const queryStr = url.split('?')[1]
  if (!queryStr) return {}
  return queryStr.split('&').reduce((acc, param) => {
    const [key, value] = param.split('=')
    acc[key] = decodeURIComponent(value)
    return acc
  }, {} as Record<string, string>)
}

export const setupAnnouncementHandlers = () => {
  Mock.mock(/\/api\/announcements(\?.*)?$/, 'get', (options: { url: string }) => {
    const { page = '1', size = '10', keyword, category, isRead } = parseQuery(options.url)

    let filtered = [...announcementsList].filter(a => a.status === 'published')

    if (keyword) {
      filtered = filtered.filter(a =>
        a.title.includes(keyword) || a.content.includes(keyword)
      )
    }
    if (category) {
      filtered = filtered.filter(a => a.category === category)
    }
    if (isRead !== undefined && isRead !== '') {
      const readStatus = isRead === 'true'
      filtered = filtered.filter(a => a.is_read === readStatus)
    }

    filtered.sort((a, b) => new Date(b.published_at).getTime() - new Date(a.published_at).getTime())

    const pageNum = parseInt(page)
    const pageSize = parseInt(size)
    const start = (pageNum - 1) * pageSize
    const end = start + pageSize
    const paginatedList = filtered.slice(start, end)

    const unreadCount = filtered.filter(a => !a.is_read).length

    return {
      code: 200,
      message: 'success',
      data: {
        total: filtered.length,
        page: pageNum,
        size: pageSize,
        unread_count: unreadCount,
        list: paginatedList
      }
    }
  })

  Mock.mock(/\/api\/announcements\/(\d+)$/, 'get', (options: { url: string }) => {
    const id = parseInt(options.url.match(/\/api\/announcements\/(\d+)/)?.[1] || '0')
    const announcement = announcementsList.find(a => a.id === id)

    if (!announcement) {
      return {
        code: 404,
        message: '公告不存在',
        data: null
      }
    }

    const index = announcementsList.findIndex(a => a.id === id)
    if (index !== -1) {
      announcementsList[index].is_read = true
    }

    return {
      code: 200,
      message: 'success',
      data: announcement
    }
  })

  Mock.mock('/api/announcements/stats', 'get', () => {
    const publishedList = announcementsList.filter(a => a.status === 'published')
    const unreadCount = publishedList.filter(a => !a.is_read).length
    const byCategory = publishedList.reduce((acc, a) => {
      acc[a.category] = (acc[a.category] || 0) + 1
      return acc
    }, {} as Record<AnnouncementCategory, number>)

    return {
      code: 200,
      message: 'success',
      data: {
        total: publishedList.length,
        unread: unreadCount,
        by_category: byCategory
      }
    }
  })

  Mock.mock(/\/api\/announcements\/(\d+)\/read$/, 'put', (options: { url: string }) => {
    const id = parseInt(options.url.match(/\/api\/announcements\/(\d+)\/read/)?.[1] || '0')
    const index = announcementsList.findIndex(a => a.id === id)

    if (index === -1) {
      return {
        code: 404,
        message: '公告不存在',
        data: null
      }
    }

    announcementsList[index].is_read = true

    return {
      code: 200,
      message: '标记已读成功',
      data: { id, is_read: true }
    }
  })
}

export default setupAnnouncementHandlers
