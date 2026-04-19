import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString, randomPhone, randomEmail } from '../utils/random'

const MerchantStatus = ['pending', 'approved', 'rejected', 'disabled']

const generateMerchants = (count = 20) => {
  const merchants = []
  for (let i = 0; i < count; i++) {
    merchants.push({
      id: randomId(),
      name: randomString(8) + '宠物店',
      contactPerson: randomString(4) + '先生',
      phone: randomPhone(),
      email: randomEmail(),
      address: randomString(10) + '路',
      status: randomEnum(MerchantStatus),
      description: randomString(20),
      createdAt: randomDate('2024-01-01')
    })
  }
  return merchants
}

const merchants = generateMerchants(30)

Mock.mock(/\/api\/admin\/merchants(?!\/\d|\/pending)/, 'get', (options) => {
  const { page = 1, pageSize = 10, keyword, status } = options.url.split('?')[1]?.split('&').reduce((acc, param) => {
    const [key, value] = param.split('=')
    acc[key] = value
    return acc
  }, {}) || {}

  let filtered = [...merchants]

  if (keyword) {
    filtered = filtered.filter(m => m.name.includes(keyword) || m.contactPerson.includes(keyword))
  }
  if (status) {
    filtered = filtered.filter(m => m.status === status)
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

Mock.mock(/\/api\/admin\/merchants\/\d+/, 'get', () => {
  const merchant = merchants[randomId(0, merchants.length - 1)]
  return {
    code: 200,
    message: 'success',
    data: {
      ...merchant,
      services: [
        { id: randomId(), name: '宠物美容', price: 200 }
      ],
      products: [
        { id: randomId(), name: '狗粮', price: 150 }
      ],
      orders: [],
      reviews: []
    }
  }
})

Mock.mock(/\/api\/admin\/merchants\/\d+\/audit/, 'put', (options) => {
  const body = JSON.parse(options.body || '{}')
  const idMatch = options.url.match(/\/api\/admin\/merchants\/(\d+)\/audit/)
  const id = idMatch ? parseInt(idMatch[1]) : null
  const merchant = merchants.find(m => m.id === id)
  if (merchant) {
    merchant.status = body.status || 'approved'
  }
  return {
    code: 200,
    message: '审核成功',
    data: { success: true }
  }
})

Mock.mock('/api/admin/merchants/pending', 'get', () => {
  const pending = merchants.filter(m => m.status === 'pending')
  return {
    code: 200,
    message: 'success',
    data: pending.slice(0, 10),
    total: pending.length
  }
})

Mock.mock(/\/api\/admin\/merchants\/\d+\/status/, 'put', (options) => {
  const body = JSON.parse(options.body || '{}')
  const idMatch = options.url.match(/\/api\/admin\/merchants\/(\d+)\/status/)
  const id = idMatch ? parseInt(idMatch[1]) : null
  const merchant = merchants.find(m => m.id === id)
  if (merchant) {
    merchant.status = body.status
  }
  return {
    code: 200,
    message: '状态更新成功',
    data: { success: true }
  }
})

Mock.mock(/\/api\/admin\/merchants\/\d+$/, 'delete', (options) => {
  const idMatch = options.url.match(/\/api\/admin\/merchants\/(\d+)$/)
  const id = idMatch ? parseInt(idMatch[1]) : null
  const index = merchants.findIndex(m => m.id === id)
  if (index > -1) {
    merchants.splice(index, 1)
  }
  return {
    code: 200,
    message: '删除成功',
    data: { success: true }
  }
})

Mock.mock('/api/admin/merchants/batch/status', 'put', (options) => {
  const body = JSON.parse(options.body || '{}')
  const { ids, status } = body
  if (ids && Array.isArray(ids)) {
    ids.forEach(id => {
      const merchant = merchants.find(m => m.id === id)
      if (merchant) {
        merchant.status = status
      }
    })
  }
  return {
    code: 200,
    message: '批量更新状态成功',
    data: { success: true }
  }
})

Mock.mock('/api/admin/merchants/batch', 'delete', (options) => {
  const body = JSON.parse(options.body || '{}')
  const { ids } = body
  if (ids && Array.isArray(ids)) {
    ids.forEach(id => {
      const index = merchants.findIndex(m => m.id === id)
      if (index > -1) {
        merchants.splice(index, 1)
      }
    })
  }
  return {
    code: 200,
    message: '批量删除成功',
    data: { success: true }
  }
})