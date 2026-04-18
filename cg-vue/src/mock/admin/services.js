import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString } from '../utils/random'

const ServiceStatus = ['active', 'inactive']
const ServiceCategories = ['美容', '寄养', '体检', '饮食', '训练', '接送']

const generateServices = (count = 30) => {
  const services = []
  for (let i = 0; i < count; i++) {
    services.push({
      id: randomId(),
      name: randomEnum(ServiceCategories) + '服务',
      merchantId: randomId(1, 20),
      merchantName: randomString(6) + '宠物店',
      category: randomEnum(ServiceCategories),
      price: randomId(50, 500),
      duration: randomEnum([30, 60, 90, 120]),
      description: randomString(30),
      status: randomEnum(ServiceStatus),
      createdAt: randomDate('2024-01-01')
    })
  }
  return services
}

const services = generateServices(50)

Mock.mock('/api/admin/services', 'get', (options) => {
  const params = options.url.split('?')[1]?.split('&').reduce((acc, param) => {
    const [key, value] = param.split('=')
    acc[key] = value
    return acc
  }, {}) || {}

  const { page = 1, pageSize = 10, keyword, merchantId, minPrice, maxPrice, status } = params

  let filtered = [...services]

  if (keyword) {
    filtered = filtered.filter(s => s.name.includes(keyword))
  }
  if (merchantId) {
    filtered = filtered.filter(s => s.merchantId === parseInt(merchantId))
  }
  if (minPrice) {
    filtered = filtered.filter(s => s.price >= parseInt(minPrice))
  }
  if (maxPrice) {
    filtered = filtered.filter(s => s.price <= parseInt(maxPrice))
  }
  if (status) {
    filtered = filtered.filter(s => s.status === status)
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

Mock.mock(/\/api\/admin\/services\/\d+/, 'delete', () => ({
  code: 200,
  message: '删除成功',
  data: { success: true }
}))

Mock.mock('/api/admin/services/batch', 'post', (options) => {
  const { action, ids } = JSON.parse(options.body)
  return {
    code: 200,
    message: '批量操作成功',
    data: { affected: ids.length, action }
  }
})