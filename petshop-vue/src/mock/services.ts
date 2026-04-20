import Mock from 'mockjs'

Mock.mock(RegExp('/api/services\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page') || '1')
  const size = parseInt(url.searchParams.get('size') || '10')
  const name = url.searchParams.get('name')
  const minPrice = url.searchParams.get('minPrice')
  const maxPrice = url.searchParams.get('maxPrice')
  const status = url.searchParams.get('status')

  const total = 50
  const list = Mock.mock({
    [`list|${size}`]: [
      {
        id: '@integer(1, 1000)',
        merchant_id: 1,
        name: '@ctitle(2, 10)',
        description: '@cparagraph(1, 3)',
        price: '@float(10, 1000, 2, 2)',
        duration: '@integer(30, 180)',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20service%20professional%20clean&image_size=square',
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

Mock.mock('/api/services/batch', 'post', () => {
  return {
    code: 200,
    message: '操作成功',
    data: null
  }
})

Mock.mock(RegExp('/api/services/\\d+$'), 'get', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/services\/(\d+)/)?.[1] || '0')
  return {
    code: 200,
    message: 'success',
    data: {
      id,
      merchant_id: 1,
      name: Mock.Random.ctitle(2, 10),
      description: Mock.Random.cparagraph(1, 3),
      price: Mock.Random.float(10, 1000, 2, 2),
      duration: Mock.Random.integer(30, 180),
      image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20service%20professional%20clean&image_size=square',
      status: 'enabled'
    }
  }
})

Mock.mock('/api/services', 'post', () => {
  return {
    code: 200,
    message: '保存成功',
    data: {
      id: Mock.Random.integer(1000, 9999)
    }
  }
})

Mock.mock(RegExp('/api/services/\\d+$'), 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/services\/(\d+)/)?.[1] || '0')
  return {
    code: 200,
    message: '更新成功',
    data: {
      id
    }
  }
})

Mock.mock(RegExp('/api/services/\\d+$'), 'delete', () => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  }
})

export function setupServicesMock() {
  console.log('[Mock] 服务模块已加载')
}
