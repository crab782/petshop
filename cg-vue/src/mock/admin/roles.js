import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString } from '../utils/random'

const generateRoles = (count = 10) => {
  const roles = []
  for (let i = 0; i < count; i++) {
    roles.push({
      id: randomId(),
      name: randomEnum(['超级管理员', '运营管理员', '客服', '商家']),
      description: randomString(20),
      permissions: [randomId(1, 10), randomId(11, 20), randomId(21, 30)],
      permissionCount: randomId(3, 15),
      createdAt: randomDate('2024-01-01'),
      updatedAt: randomDate('2024-01-01')
    })
  }
  return roles
}

const roles = generateRoles(10)

const permissions = [
  { id: 1, name: '用户管理', code: 'user:read', children: [
    { id: 11, name: '查看用户', code: 'user:view' },
    { id: 12, name: '编辑用户', code: 'user:edit' }
  ]},
  { id: 2, name: '商家管理', code: 'merchant:read', children: [
    { id: 21, name: '查看商家', code: 'merchant:view' },
    { id: 22, name: '审核商家', code: 'merchant:audit' }
  ]},
  { id: 3, name: '订单管理', code: 'order:read', children: [] },
  { id: 4, name: '系统设置', code: 'system:config', children: [] }
]

Mock.mock('/api/admin/roles', 'get', (options) => {
  const { page = 1, pageSize = 10, keyword } =
    options.url.split('?')[1]?.split('&').reduce((acc, param) => {
      const [key, value] = param.split('=')
      acc[key] = value
      return acc
    }, {}) || {}

  let filtered = [...roles]

  if (keyword) {
    filtered = filtered.filter(r => r.name.includes(keyword))
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

Mock.mock('/api/admin/roles', 'post', (options) => {
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '创建成功',
    data: { id: randomId(), ...data, createdAt: new Date().toISOString() }
  }
})

Mock.mock(/\/api\/admin\/roles\/\d+/, 'put', () => ({
  code: 200,
  message: '更新成功',
  data: { success: true }
}))

Mock.mock(/\/api\/admin\/roles\/\d+/, 'delete', () => ({
  code: 200,
  message: '删除成功',
  data: { success: true }
}))

Mock.mock('/api/admin/permissions', 'get', () => ({
  code: 200,
  message: 'success',
  data: permissions
}))