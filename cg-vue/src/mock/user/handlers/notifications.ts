import Mock from 'mockjs'
import { notificationsData, notificationStats, type Notification, type NotificationType } from '../data/notifications'
import { randomId } from '../../utils/random'

const notificationsList = [...notificationsData]

const parseQuery = (url: string) => {
  const queryStr = url.split('?')[1]
  if (!queryStr) return {}
  return queryStr.split('&').reduce((acc, param) => {
    const [key, value] = param.split('=')
    acc[key] = decodeURIComponent(value)
    return acc
  }, {} as Record<string, string>)
}

Mock.mock(/\/api\/notifications(\?.*)?$/, 'get', (options: { url: string }) => {
  const { page = '1', size = '10', type, isRead } = parseQuery(options.url)

  let filtered = [...notificationsList]

  if (type) {
    filtered = filtered.filter(n => n.type === type)
  }
  if (isRead !== undefined && isRead !== '') {
    const readStatus = isRead === 'true'
    filtered = filtered.filter(n => n.is_read === readStatus)
  }

  filtered.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())

  const pageNum = parseInt(page)
  const pageSize = parseInt(size)
  const start = (pageNum - 1) * pageSize
  const end = start + pageSize
  const paginatedList = filtered.slice(start, end)

  const unreadCount = filtered.filter(n => !n.is_read).length

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

Mock.mock(/\/api\/notifications\/(\d+)\/read$/, 'put', (options: { url: string }) => {
  const id = parseInt(options.url.match(/\/api\/notifications\/(\d+)\/read/)?.[1] || '0')
  const index = notificationsList.findIndex(n => n.id === id)

  if (index === -1) {
    return {
      code: 404,
      message: '通知不存在',
      data: null
    }
  }

  notificationsList[index].is_read = true

  return {
    code: 200,
    message: '标记已读成功',
    data: { id, is_read: true }
  }
})

Mock.mock('/api/notifications/batch-read', 'put', (options: { body: string }) => {
  const body = JSON.parse(options.body) as { ids?: number[]; type?: NotificationType }

  let updatedCount = 0

  if (body.ids && body.ids.length > 0) {
    body.ids.forEach(id => {
      const index = notificationsList.findIndex(n => n.id === id)
      if (index !== -1 && !notificationsList[index].is_read) {
        notificationsList[index].is_read = true
        updatedCount++
      }
    })
  } else if (body.type) {
    notificationsList.forEach(n => {
      if (n.type === body.type && !n.is_read) {
        n.is_read = true
        updatedCount++
      }
    })
  } else {
    notificationsList.forEach(n => {
      if (!n.is_read) {
        n.is_read = true
        updatedCount++
      }
    })
  }

  return {
    code: 200,
    message: '批量标记已读成功',
    data: { updated_count: updatedCount }
  }
})

Mock.mock(/\/api\/notifications\/\d+$/, 'delete', (options: { url: string }) => {
  const id = parseInt(options.url.match(/\/api\/notifications\/(\d+)/)?.[1] || '0')
  const index = notificationsList.findIndex(n => n.id === id)

  if (index === -1) {
    return {
      code: 404,
      message: '通知不存在',
      data: null
    }
  }

  notificationsList.splice(index, 1)

  return {
    code: 200,
    message: '通知删除成功',
    data: null
  }
})

Mock.mock('/api/notifications/batch', 'delete', (options: { body: string }) => {
  const body = JSON.parse(options.body) as { ids?: number[]; type?: NotificationType; isRead?: boolean }

  let deletedCount = 0

  if (body.ids && body.ids.length > 0) {
    body.ids.forEach(id => {
      const index = notificationsList.findIndex(n => n.id === id)
      if (index !== -1) {
        notificationsList.splice(index, 1)
        deletedCount++
      }
    })
  } else if (body.type) {
    for (let i = notificationsList.length - 1; i >= 0; i--) {
      if (notificationsList[i].type === body.type) {
        notificationsList.splice(i, 1)
        deletedCount++
      }
    }
  } else if (body.isRead !== undefined) {
    const readStatus = body.isRead
    for (let i = notificationsList.length - 1; i >= 0; i--) {
      if (notificationsList[i].is_read === readStatus) {
        notificationsList.splice(i, 1)
        deletedCount++
      }
    }
  }

  return {
    code: 200,
    message: '批量删除成功',
    data: { deleted_count: deletedCount }
  }
})

Mock.mock('/api/notifications/stats', 'get', () => {
  const unreadCount = notificationsList.filter(n => !n.is_read).length
  const byType = notificationsList.reduce((acc, n) => {
    acc[n.type] = (acc[n.type] || 0) + 1
    return acc
  }, {} as Record<NotificationType, number>)

  return {
    code: 200,
    message: 'success',
    data: {
      total: notificationsList.length,
      unread: unreadCount,
      by_type: byType
    }
  }
})

Mock.mock('/api/notifications/unread-count', 'get', () => {
  const unreadCount = notificationsList.filter(n => !n.is_read).length

  return {
    code: 200,
    message: 'success',
    data: {
      unread_count: unreadCount
    }
  }
})

export { notificationsList }
