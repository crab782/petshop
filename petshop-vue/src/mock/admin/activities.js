import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString } from '../utils/random'

const ActivityStatus = ['pending', 'active', 'ended', 'disabled']
const ActivityTypes = ['促销活动', '抽奖活动', '满减活动', '节日活动', '新品上市']

const generateActivities = (count = 15) => {
  const activities = []
  for (let i = 0; i < count; i++) {
    activities.push({
      id: randomId(),
      name: randomEnum(ActivityTypes) + randomId(1, 100),
      type: randomEnum(ActivityTypes),
      description: randomString(50),
      startTime: randomDate('2024-01-01'),
      endTime: randomDate('2024-06-01'),
      status: randomEnum(ActivityStatus),
      createdAt: randomDate('2024-01-01')
    })
  }
  return activities
}

const activities = generateActivities(20)

Mock.mock('/api/admin/activities', 'get', (options) => {
  const { page = 1, pageSize = 10, keyword, type, startDate, endDate, status } =
    options.url.split('?')[1]?.split('&').reduce((acc, param) => {
      const [key, value] = param.split('=')
      acc[key] = value
      return acc
    }, {}) || {}

  let filtered = [...activities]

  if (keyword) {
    filtered = filtered.filter(a => a.name.includes(keyword))
  }
  if (type) {
    filtered = filtered.filter(a => a.type === type)
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

Mock.mock('/api/admin/activities', 'post', (options) => {
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '创建成功',
    data: { id: randomId(), ...data, createdAt: new Date().toISOString() }
  }
})

Mock.mock(/\/api\/admin\/activities\/\d+/, 'put', () => ({
  code: 200,
  message: '更新成功',
  data: { success: true }
}))

Mock.mock(/\/api\/admin\/activities\/\d+/, 'delete', () => ({
  code: 200,
  message: '删除成功',
  data: { success: true }
}))

Mock.mock(/\/api\/admin\/activities\/\d+\/status/, 'put', () => ({
  code: 200,
  message: '状态切换成功',
  data: { success: true }
}))