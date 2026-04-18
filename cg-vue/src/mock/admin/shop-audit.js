import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString, randomPhone } from '../utils/random'

const ShopStatus = ['pending', 'approved', 'rejected']

const generateShops = (count = 15) => {
  const shops = []
  for (let i = 0; i < count; i++) {
    shops.push({
      id: randomId(),
      merchantId: randomId(1, 20),
      merchantName: randomString(8) + '宠物店',
      shopName: randomString(6) + '宠物生活馆',
      contactPerson: randomString(4) + '先生',
      phone: randomPhone(),
      address: randomString(10) + '路',
      businessLicense: randomString(18),
      status: randomEnum(ShopStatus),
      appliedAt: randomDate('2024-01-01'),
      description: randomString(50)
    })
  }
  return shops
}

const shops = generateShops(20)

// GET /api/admin/shops/pending - 待审核店铺
Mock.mock('/api/admin/shops/pending', 'get', (options) => {
  const { page = 1, pageSize = 10, keyword } =
    options.url.split('?')[1]?.split('&').reduce((acc, param) => {
      const [key, value] = param.split('=')
      acc[key] = value
      return acc
    }, {}) || {}

  let filtered = shops.filter(s => s.status === 'pending')

  if (keyword) {
    filtered = filtered.filter(s => s.shopName.includes(keyword) || s.merchantName.includes(keyword))
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

// POST /api/admin/shops/:id/audit - 店铺审核
Mock.mock(/\/api\/admin\/shops\/\d+\/audit/, 'post', (options) => {
  const { action, reason } = JSON.parse(options.body)
  return {
    code: 200,
    message: action === 'approve' ? '审核通过' : '审核拒绝',
    data: { success: true, action, reason }
  }
})
