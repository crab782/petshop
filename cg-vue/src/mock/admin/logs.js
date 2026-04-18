import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString, randomIP } from '../utils/random'

const ActionTypes = ['CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'APPROVE', 'REJECT']
const Modules = ['用户管理', '商家管理', '服务管理', '商品管理', '订单管理', '系统设置']

const generateLogs = (count = 100) => {
  const logs = []
  for (let i = 0; i < count; i++) {
    logs.push({
      id: randomId(),
      username: randomString(6),
      action: randomEnum(ActionTypes),
      module: randomEnum(Modules),
      description: randomString(30),
      ipAddress: randomIP(),
      userAgent: 'Mozilla/5.0',
      createTime: randomDate('2024-01-01')
    })
  }
  return logs
}

const logs = generateLogs(200)

Mock.mock('/api/admin/operation-logs', 'get', (options) => {
  const { page = 1, pageSize = 10, action, username, startDate, endDate } =
    options.url.split('?')[1]?.split('&').reduce((acc, param) => {
      const [key, value] = param.split('=')
      acc[key] = value
      return acc
    }, {}) || {}

  let filtered = [...logs]

  if (action) {
    filtered = filtered.filter(l => l.action === action)
  }
  if (username) {
    filtered = filtered.filter(l => l.username.includes(username))
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

Mock.mock(/\/api\/admin\/operation-logs\/\d+/, 'delete', () => ({
  code: 200,
  message: '删除成功',
  data: { success: true }
}))

Mock.mock('/api/admin/operation-logs', 'delete', () => ({
  code: 200,
  message: '清空成功',
  data: { success: true }
}))