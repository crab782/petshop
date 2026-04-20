import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString, randomPhone, randomEmail } from '../utils/random'

const UserStatus = ['active', 'disabled']

const generateUsers = (count = 20) => {
  const users = []
  for (let i = 0; i < count; i++) {
    users.push({
      id: randomId(),
      username: randomString(6),
      email: randomEmail(),
      phone: randomPhone(),
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${randomString(8)}`,
      status: randomEnum(UserStatus),
      createdAt: randomDate('2024-01-01'),
      updatedAt: randomDate('2024-01-01')
    })
  }
  return users
}

const users = generateUsers(50)

Mock.mock(/\/api\/admin\/users/, 'get', (options) => {
  const { page = 1, pageSize = 10, keyword, status } = options.url.split('?')[1]?.split('&').reduce((acc, param) => {
    const [key, value] = param.split('=')
    acc[key] = value
    return acc
  }, {}) || {}

  let filtered = [...users]

  if (keyword) {
    filtered = filtered.filter(u => u.username.includes(keyword) || u.email.includes(keyword))
  }
  if (status) {
    filtered = filtered.filter(u => u.status === status)
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

Mock.mock(/\/api\/admin\/users\/\d+/, 'get', () => {
  const user = users[randomId(0, users.length - 1)]
  return {
    code: 200,
    message: 'success',
    data: {
      ...user,
      pets: [
        { id: randomId(), name: '宠物1', species: '狗', breed: '金毛' },
        { id: randomId(), name: '宠物2', species: '猫', breed: '英短' }
      ],
      serviceOrders: [
        { id: randomId(), serviceName: '宠物美容', amount: 200, status: 'completed', createdAt: randomDate() }
      ],
      productOrders: [
        { id: randomId(), productName: '狗粮', amount: 150, status: 'completed', createdAt: randomDate() }
      ],
      reviews: [
        { id: randomId(), content: '服务很好', rating: 5, createdAt: randomDate() }
      ]
    }
  }
})

Mock.mock(/\/api\/admin\/users\/\d+$/, 'put', (options) => {
  const id = parseInt(options.url.match(/\/api\/admin\/users\/(\d+)/)[1])
  const body = JSON.parse(options.body)
  const userIndex = users.findIndex(u => u.id === id)
  if (userIndex > -1) {
    users[userIndex] = { ...users[userIndex], ...body, updatedAt: new Date().toISOString() }
  }
  return {
    code: 200,
    message: '更新成功',
    data: users[userIndex] || null
  }
})

Mock.mock(/\/api\/admin\/users\/\d+\/status$/, 'put', (options) => {
  const id = parseInt(options.url.match(/\/api\/admin\/users\/(\d+)\/status/)[1])
  const { status } = JSON.parse(options.body)
  const userIndex = users.findIndex(u => u.id === id)
  if (userIndex > -1) {
    users[userIndex].status = status
    users[userIndex].updatedAt = new Date().toISOString()
  }
  return {
    code: 200,
    message: '状态更新成功',
    data: { id, status }
  }
})

Mock.mock(/\/api\/admin\/users\/\d+$/, 'delete', (options) => {
  const id = parseInt(options.url.match(/\/api\/admin\/users\/(\d+)/)[1])
  const index = users.findIndex(u => u.id === id)
  if (index > -1) {
    users.splice(index, 1)
  }
  return {
    code: 200,
    message: '删除成功',
    data: { id }
  }
})

Mock.mock('/api/admin/users/batch/status', 'put', (options) => {
  const { ids, status } = JSON.parse(options.body)
  ids.forEach(id => {
    const userIndex = users.findIndex(u => u.id === id)
    if (userIndex > -1) {
      users[userIndex].status = status
      users[userIndex].updatedAt = new Date().toISOString()
    }
  })
  return {
    code: 200,
    message: '批量状态更新成功',
    data: { affected: ids.length, status }
  }
})

Mock.mock('/api/admin/users/batch', 'delete', (options) => {
  const { ids } = JSON.parse(options.body)
  ids.forEach(id => {
    const index = users.findIndex(u => u.id === id)
    if (index > -1) {
      users.splice(index, 1)
    }
  })
  return {
    code: 200,
    message: '批量删除成功',
    data: { affected: ids.length }
  }
})