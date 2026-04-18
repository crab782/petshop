import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString } from '../utils/random'

const ProductStatus = ['active', 'inactive']

const generateProducts = (count = 30) => {
  const products = []
  for (let i = 0; i < count; i++) {
    products.push({
      id: randomId(),
      name: randomString(6) + '狗粮/猫粮',
      merchantId: randomId(1, 20),
      merchantName: randomString(6) + '宠物店',
      description: randomString(40),
      price: randomId(20, 300),
      stock: randomId(0, 100),
      sales: randomId(0, 500),
      image: `https://picsum.photos/200/200?random=${randomId()}`,
      status: randomEnum(ProductStatus),
      createdAt: randomDate('2024-01-01')
    })
  }
  return products
}

const products = generateProducts(50)

Mock.mock('/api/admin/products', 'get', (options) => {
  const params = options.url.split('?')[1]?.split('&').reduce((acc, param) => {
    const [key, value] = param.split('=')
    acc[key] = value
    return acc
  }, {}) || {}

  const { page = 1, pageSize = 10, keyword, merchantId, minPrice, maxPrice, stockStatus, status } = params

  let filtered = [...products]

  if (keyword) {
    filtered = filtered.filter(p => p.name.includes(keyword) || p.merchantName.includes(keyword))
  }
  if (merchantId) {
    filtered = filtered.filter(p => p.merchantId === parseInt(merchantId))
  }
  if (minPrice) {
    filtered = filtered.filter(p => p.price >= parseInt(minPrice))
  }
  if (maxPrice) {
    filtered = filtered.filter(p => p.price <= parseInt(maxPrice))
  }
  if (stockStatus === 'outOfStock') {
    filtered = filtered.filter(p => p.stock === 0)
  } else if (stockStatus === 'hasStock') {
    filtered = filtered.filter(p => p.stock > 0)
  }
  if (status) {
    filtered = filtered.filter(p => p.status === status)
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

Mock.mock(/\/api\/admin\/products\/\d+/, 'get', () => ({
  code: 200,
  message: 'success',
  data: products[randomId(0, products.length - 1)]
}))

Mock.mock(/\/api\/admin\/products\/\d+/, 'put', () => ({
  code: 200,
  message: '更新成功',
  data: { success: true }
}))

Mock.mock(/\/api\/admin\/products\/\d+/, 'delete', () => ({
  code: 200,
  message: '删除成功',
  data: { success: true }
}))

Mock.mock('/api/admin/products/batch', 'post', (options) => {
  const { action, ids } = JSON.parse(options.body)
  return {
    code: 200,
    message: '批量操作成功',
    data: { affected: ids.length, action }
  }
})