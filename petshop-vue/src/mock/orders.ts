import Mock from 'mockjs'

Mock.mock(RegExp('/api/orders/service\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page') || '1')
  const size = parseInt(url.searchParams.get('size') || '10')
  const id = url.searchParams.get('id')
  const username = url.searchParams.get('username')
  const status = url.searchParams.get('status')

  const total = 50
  const list = Mock.mock({
    [`list|${size}`]: [
      {
        id: '@integer(1, 1000)',
        user_id: '@integer(1, 100)',
        merchant_id: 1,
        service_id: '@integer(1, 100)',
        appointment_time: '@datetime("yyyy-MM-dd HH:mm:ss")',
        status: '@pick(["pending", "confirmed", "completed", "cancelled"])'
      }
    ]
  }).list

  let filteredList = list
  if (id) {
    filteredList = filteredList.filter((item: { id: number }) => item.id.toString().includes(id))
  }
  if (username) {
    filteredList = filteredList.filter(() => Math.random() > 0.5)
  }
  if (status) {
    filteredList = filteredList.filter((item: { status: string }) => item.status === status)
  }

  const resultList = filteredList.map((item: { id: number; user_id: number; merchant_id: number; service_id: number; appointment_time: string; status: string }) => ({
    ...item,
    user: {
      username: Mock.Random.cname()
    },
    service: {
      name: Mock.Random.ctitle(2, 10)
    },
    total_price: Mock.Random.float(10, 1000, 2, 2)
  }))

  return {
    code: 200,
    message: 'success',
    data: {
      total,
      page,
      size,
      list: resultList
    }
  }
})

Mock.mock(RegExp('/api/orders/product\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page') || '1')
  const size = parseInt(url.searchParams.get('size') || '10')
  const id = url.searchParams.get('id')
  const username = url.searchParams.get('username')
  const status = url.searchParams.get('status')

  const total = 50
  const list = Mock.mock({
    [`list|${size}`]: [
      {
        id: '@integer(1, 1000)',
        user_id: '@integer(1, 100)',
        merchant_id: 1,
        total_price: '@float(10, 1000, 2, 2)',
        status: '@pick(["pending", "paid", "shipped", "completed", "cancelled"])'
      }
    ]
  }).list

  let filteredList = list
  if (id) {
    filteredList = filteredList.filter((item: { id: number }) => item.id.toString().includes(id))
  }
  if (username) {
    filteredList = filteredList.filter(() => Math.random() > 0.5)
  }
  if (status) {
    filteredList = filteredList.filter((item: { status: string }) => item.status === status)
  }

  const resultList = filteredList.map((item: { id: number; user_id: number; merchant_id: number; total_price: number; status: string }) => ({
    ...item,
    user: {
      username: Mock.Random.cname()
    },
    shipping_address: Mock.Random.cparagraph(1, 2),
    created_at: Mock.Random.datetime("yyyy-MM-dd HH:mm:ss")
  }))

  return {
    code: 200,
    message: 'success',
    data: {
      total,
      page,
      size,
      list: resultList
    }
  }
})

Mock.mock(RegExp('/api/orders/\\d+/status'), 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/orders\/(\d+)/)?.[1] || '0')
  return {
    code: 200,
    message: '状态更新成功',
    data: {
      id
    }
  }
})

Mock.mock(RegExp('/api/orders/\\d+$'), 'get', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/orders\/(\d+)/)?.[1] || '0')
  return {
    code: 200,
    message: 'success',
    data: {
      id,
      user_id: Mock.Random.integer(1, 100),
      merchant_id: 1,
      total_price: Mock.Random.float(10, 1000, 2, 2),
      status: Mock.Random.pick(["pending", "paid", "shipped", "completed", "cancelled"]),
      shipping_address: Mock.Random.cparagraph(1, 2),
      created_at: Mock.Random.datetime("yyyy-MM-dd HH:mm:ss"),
      user: {
        username: Mock.Random.cname(),
        phone: Mock.Random.phone()
      },
      items: Mock.mock({
        'list|1-5': [
          {
            product_id: '@integer(1, 100)',
            name: '@ctitle(2, 10)',
            quantity: '@integer(1, 10)',
            price: '@float(10, 500, 2, 2)'
          }
        ]
      }).list
    }
  }
})

Mock.mock(RegExp('/api/orders/\\d+/shipping'), 'post', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/orders\/(\d+)/)?.[1] || '0')
  return {
    code: 200,
    message: '发货成功',
    data: {
      id
    }
  }
})

export function setupOrdersMock() {
  console.log('[Mock] 订单模块已加载')
}
