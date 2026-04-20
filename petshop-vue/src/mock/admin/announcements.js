import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString } from '../utils/random'

const AnnouncementStatus = ['published', 'draft']

const generateAnnouncements = (count = 20) => {
  const announcements = []
  for (let i = 0; i < count; i++) {
    announcements.push({
      id: randomId(),
      title: randomString(10) + '公告',
      content: randomString(100),
      author: randomString(4),
      status: randomEnum(AnnouncementStatus),
      publishedAt: randomDate('2024-01-01'),
      createdAt: randomDate('2024-01-01'),
      updatedAt: randomDate('2024-01-01')
    })
  }
  return announcements
}

const announcements = generateAnnouncements(30)

Mock.mock('/api/admin/announcements', 'get', (options) => {
  const { page = 1, pageSize = 10, keyword, status } =
    options.url.split('?')[1]?.split('&').reduce((acc, param) => {
      const [key, value] = param.split('=')
      acc[key] = value
      return acc
    }, {}) || {}

  let filtered = [...announcements]

  if (keyword) {
    filtered = filtered.filter(a => a.title.includes(keyword))
  }
  if (status) {
    filtered = filtered.filter(a => a.status === status)
  }

  const start = (page - 1) * pageSize
  const end = start + parseInt(pageSize)

  return {
    code: 200,
    message: 'success',
    data: filtered.slice(start, end),
    total: filtered.length
  }
})

Mock.mock(/\/api\/admin\/announcements\/\d+/, 'get', () => ({
  code: 200,
  message: 'success',
  data: announcements[randomId(0, announcements.length - 1)]
}))

Mock.mock('/api/admin/announcements', 'post', (options) => {
  const body = JSON.parse(options.body)
  const newAnnouncement = {
    id: randomId(),
    ...body,
    status: body.status || 'draft',
    publishedAt: body.status === 'published' ? new Date().toISOString() : null,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  }
  announcements.unshift(newAnnouncement)
  return {
    code: 200,
    message: '创建成功',
    data: newAnnouncement
  }
})

Mock.mock(/\/api\/admin\/announcements\/\d+/, 'put', (options) => {
  const id = parseInt(options.url.match(/\/api\/admin\/announcements\/(\d+)/)[1])
  const body = JSON.parse(options.body)
  const announcementIndex = announcements.findIndex(a => a.id === id)
  if (announcementIndex > -1) {
    announcements[announcementIndex] = { 
      ...announcements[announcementIndex], 
      ...body, 
      updatedAt: new Date().toISOString() 
    }
  }
  return {
    code: 200,
    message: '更新成功',
    data: announcements[announcementIndex] || null
  }
})

Mock.mock(/\/api\/admin\/announcements\/\d+/, 'delete', (options) => {
  const id = parseInt(options.url.match(/\/api\/admin\/announcements\/(\d+)/)[1])
  const index = announcements.findIndex(a => a.id === id)
  if (index > -1) {
    announcements.splice(index, 1)
  }
  return {
    code: 200,
    message: '删除成功',
    data: { id }
  }
})

Mock.mock(/\/api\/admin\/announcements\/\d+\/publish/, 'put', (options) => {
  const id = parseInt(options.url.match(/\/api\/admin\/announcements\/(\d+)\/publish/)[1])
  const announcementIndex = announcements.findIndex(a => a.id === id)
  if (announcementIndex > -1) {
    announcements[announcementIndex].status = 'published'
    announcements[announcementIndex].publishedAt = new Date().toISOString()
    announcements[announcementIndex].updatedAt = new Date().toISOString()
  }
  return {
    code: 200,
    message: '发布成功',
    data: { id, status: 'published' }
  }
})

Mock.mock(/\/api\/admin\/announcements\/\d+\/unpublish/, 'put', (options) => {
  const id = parseInt(options.url.match(/\/api\/admin\/announcements\/(\d+)\/unpublish/)[1])
  const announcementIndex = announcements.findIndex(a => a.id === id)
  if (announcementIndex > -1) {
    announcements[announcementIndex].status = 'draft'
    announcements[announcementIndex].updatedAt = new Date().toISOString()
  }
  return {
    code: 200,
    message: '下架成功',
    data: { id, status: 'draft' }
  }
})

Mock.mock('/api/admin/announcements/batch/publish', 'put', (options) => {
  const { ids } = JSON.parse(options.body)
  ids.forEach(id => {
    const announcementIndex = announcements.findIndex(a => a.id === id)
    if (announcementIndex > -1) {
      announcements[announcementIndex].status = 'published'
      announcements[announcementIndex].publishedAt = new Date().toISOString()
      announcements[announcementIndex].updatedAt = new Date().toISOString()
    }
  })
  return {
    code: 200,
    message: '批量发布成功',
    data: { affected: ids.length }
  }
})

Mock.mock('/api/admin/announcements/batch', 'delete', (options) => {
  const { ids } = JSON.parse(options.body)
  ids.forEach(id => {
    const index = announcements.findIndex(a => a.id === id)
    if (index > -1) {
      announcements.splice(index, 1)
    }
  })
  return {
    code: 200,
    message: '批量删除成功',
    data: { affected: ids.length }
  }
})