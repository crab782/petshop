import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString } from '../utils/random'

const ProductStatus = ['active', 'inactive']

const generateProducts = (count = 30) => {
  const products = []
  for (let i = 0; i < count; i++) {
    products.push({
      id: i + 1,
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

let products = generateProducts(50)

const extractIdFromUrl = (url) => {
  const match = url.match(/\/api\/admin\/products\/(\d+)/)
  return match ? parseInt(match[1]) : null
}

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
    data: {
      list: filtered.slice(start, end),
      total: filtered.length,
      page: parseInt(page),
      pageSize: parseInt(pageSize)
    }
  }
})

Mock.mock(/\/api\/admin\/products\/\d+$/, 'get', (options) => {
  const id = extractIdFromUrl(options.url)
  const product = products.find(p => p.id === id)

  if (!product) {
    return {
      code: 404,
      message: '商品不存在',
      data: null
    }
  }

  return {
    code: 200,
    message: 'success',
    data: product
  }
})

Mock.mock(/\/api\/admin\/products\/\d+$/, 'put', (options) => {
  const id = extractIdFromUrl(options.url)
  const index = products.findIndex(p => p.id === id)

  if (index === -1) {
    return {
      code: 404,
      message: '商品不存在',
      data: null
    }
  }

  const updateData = JSON.parse(options.body)
  products[index] = { ...products[index], ...updateData, updatedAt: new Date().toISOString() }

  return {
    code: 200,
    message: '更新成功',
    data: products[index]
  }
})

Mock.mock(/\/api\/admin\/products\/\d+\/status$/, 'put', (options) => {
  const urlWithoutStatus = options.url.replace('/status', '')
  const id = extractIdFromUrl(urlWithoutStatus)
  const index = products.findIndex(p => p.id === id)

  if (index === -1) {
    return {
      code: 404,
      message: '商品不存在',
      data: null
    }
  }

  const { status } = JSON.parse(options.body)
  products[index].status = status
  products[index].updatedAt = new Date().toISOString()

  return {
    code: 200,
    message: '状态更新成功',
    data: products[index]
  }
})

Mock.mock(/\/api\/admin\/products\/\d+$/, 'delete', (options) => {
  const id = extractIdFromUrl(options.url)
  const index = products.findIndex(p => p.id === id)

  if (index === -1) {
    return {
      code: 404,
      message: '商品不存在',
      data: null
    }
  }

  products.splice(index, 1)

  return {
    code: 200,
    message: '删除成功',
    data: { success: true }
  }
})

Mock.mock('/api/admin/products/batch/status', 'put', (options) => {
  const { ids, status } = JSON.parse(options.body)
  let affected = 0

  ids.forEach(id => {
    const index = products.findIndex(p => p.id === id)
    if (index !== -1) {
      products[index].status = status
      products[index].updatedAt = new Date().toISOString()
      affected++
    }
  })

  return {
    code: 200,
    message: '批量更新状态成功',
    data: { affected, status }
  }
})

Mock.mock('/api/admin/products/batch', 'delete', (options) => {
  const { ids } = JSON.parse(options.body)
  let affected = 0

  ids.forEach(id => {
    const index = products.findIndex(p => p.id === id)
    if (index !== -1) {
      products.splice(index, 1)
      affected++
    }
  })

  return {
    code: 200,
    message: '批量删除成功',
    data: { affected }
  }
})
