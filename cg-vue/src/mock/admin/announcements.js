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
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '创建成功',
    data: { id: randomId(), ...data }
  }
})

Mock.mock(/\/api\/admin\/announcements\/\d+/, 'put', () => ({
  code: 200,
  message: '更新成功',
  data: { success: true }
}))

Mock.mock(/\/api\/admin\/announcements\/\d+/, 'delete', () => ({
  code: 200,
  message: '删除成功',
  data: { success: true }
}))

Mock.mock(/\/api\/admin\/announcements\/\d+\/publish/, 'post', () => ({
  code: 200,
  message: '发布成功',
  data: { success: true }
}))

Mock.mock(/\/api\/admin\/announcements\/\d+\/unpublish/, 'post', () => ({
  code: 200,
  message: '下架成功',
  data: { success: true }
}))