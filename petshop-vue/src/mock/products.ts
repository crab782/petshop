import Mock from 'mockjs'

Mock.mock(RegExp('/api/products\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page') || '1')
  const size = parseInt(url.searchParams.get('size') || '10')
  const name = url.searchParams.get('name')
  const minPrice = url.searchParams.get('minPrice')
  const maxPrice = url.searchParams.get('maxPrice')
  const stockStatus = url.searchParams.get('stockStatus')
  const status = url.searchParams.get('status')

  const total = 50
  const list = Mock.mock({
    [`list|${size}`]: [
      {
        id: '@integer(1, 1000)',
        merchant_id: 1,
        name: '@ctitle(2, 10)',
        description: '@cparagraph(1, 3)',
        price: '@float(10, 500, 2, 2)',
        stock: '@integer(0, 100)',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20product%20colorful%20toys&image_size=square',
        status: '@pick(["enabled", "disabled"])'
      }
    ]
  }).list

  let filteredList = list
  if (name) {
    filteredList = filteredList.filter((item: { name: string }) => item.name.includes(name))
  }
  if (minPrice) {
    filteredList = filteredList.filter((item: { price: number }) => item.price >= parseFloat(minPrice))
  }
  if (maxPrice) {
    filteredList = filteredList.filter((item: { price: number }) => item.price <= parseFloat(maxPrice))
  }
  if (stockStatus === 'inStock') {
    filteredList = filteredList.filter((item: { stock: number }) => item.stock > 0)
  } else if (stockStatus === 'outOfStock') {
    filteredList = filteredList.filter((item: { stock: number }) => item.stock === 0)
  }
  if (status) {
    filteredList = filteredList.filter((item: { status: string }) => item.status === status)
  }

  return {
    code: 200,
    message: 'success',
    data: {
      total,
      page,
      size,
      list: filteredList
    }
  }
})

Mock.mock('/api/products/batch', 'post', () => {
  return {
    code: 200,
    message: '操作成功',
    data: null
  }
})

Mock.mock(RegExp('/api/products/\\d+$'), 'get', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/products\/(\d+)/)?.[1] || '0')
  return {
    code: 200,
    message: 'success',
    data: {
      id,
      merchant_id: 1,
      name: Mock.Random.ctitle(2, 10),
      description: Mock.Random.cparagraph(1, 3),
      price: Mock.Random.float(10, 500, 2, 2),
      stock: Mock.Random.integer(0, 100),
      image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20product%20colorful%20toys&image_size=square',
      status: 'enabled'
    }
  }
})

Mock.mock('/api/products', 'post', () => {
  return {
    code: 200,
    message: '保存成功',
    data: {
      id: Mock.Random.integer(1000, 9999)
    }
  }
})

Mock.mock(RegExp('/api/products/\\d+$'), 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/products\/(\d+)/)?.[1] || '0')
  return {
    code: 200,
    message: '更新成功',
    data: {
      id
    }
  }
})

Mock.mock(RegExp('/api/products/\\d+$'), 'delete', () => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  }
})

export function setupProductsMock() {
  console.log('[Mock] 商品模块已加载')
}
