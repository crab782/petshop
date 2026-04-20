import Mock from 'mockjs'

Mock.mock(RegExp('/api/categories\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page') || '1')
  const size = parseInt(url.searchParams.get('size') || '10')
  const status = url.searchParams.get('status')

  const total = 20
  const list = Mock.mock({
    [`list|${size}`]: [
      {
        id: '@integer(1, 100)',
        merchant_id: 1,
        name: '@ctitle(2, 10)',
        icon: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=category%20icon%20simple&image_size=square',
        sort: '@integer(0, 100)',
        status: '@pick(["enabled", "disabled"])',
        product_count: '@integer(0, 50)'
      }
    ]
  }).list

  let filteredList = list
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

Mock.mock('/api/categories', 'post', () => {
  return {
    code: 200,
    message: '添加成功',
    data: {
      id: Mock.Random.integer(1000, 9999)
    }
  }
})

Mock.mock(RegExp('/api/categories/\\d+$'), 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/categories\/(\d+)/)?.[1] || '0')
  return {
    code: 200,
    message: '编辑成功',
    data: {
      id
    }
  }
})

Mock.mock(RegExp('/api/categories/\\d+$'), 'delete', () => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  }
})

Mock.mock('/api/categories/batch', 'post', () => {
  return {
    code: 200,
    message: '操作成功',
    data: null
  }
})

export function setupCategoriesMock() {
  console.log('[Mock] 分类模块已加载')
}
