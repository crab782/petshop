import Mock from 'mockjs'

Mock.mock(RegExp('/api/reviews\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page') || '1')
  const size = parseInt(url.searchParams.get('size') || '10')
  const rating = url.searchParams.get('rating')
  const startDate = url.searchParams.get('startDate')
  const endDate = url.searchParams.get('endDate')
  const keyword = url.searchParams.get('keyword')

  const total = 50
  const list = Mock.mock({
    [`list|${size}`]: [
      {
        id: '@integer(1, 1000)',
        user_id: '@integer(1, 100)',
        merchant_id: 1,
        service_id: '@integer(1, 100)',
        appointment_id: '@integer(1, 100)',
        rating: '@integer(1, 5)',
        comment: '@cparagraph(1, 3)',
        created_at: '@datetime("yyyy-MM-dd HH:mm:ss")'
      }
    ]
  }).list

  let filteredList = list
  if (rating) {
    filteredList = filteredList.filter((item: { rating: number }) => item.rating === parseInt(rating))
  }
  if (startDate) {
    filteredList = filteredList.filter((item: { created_at: string }) => item.created_at >= startDate)
  }
  if (endDate) {
    filteredList = filteredList.filter((item: { created_at: string }) => item.created_at <= endDate)
  }
  if (keyword) {
    filteredList = filteredList.filter((item: { comment: string }) => item.comment.includes(keyword))
  }

  const resultList = filteredList.map((item: { id: number; user_id: number; merchant_id: number; service_id: number; appointment_id: number; rating: number; comment: string; created_at: string }) => ({
    ...item,
    user: {
      username: Mock.Random.cname(),
      avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20friendly&image_size=square'
    },
    service: {
      name: Mock.Random.ctitle(2, 10)
    }
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

Mock.mock('/api/reviews/stats', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: {
      average_rating: 4.5,
      rating_distribution: {
        5: 20,
        4: 15,
        3: 8,
        2: 4,
        1: 3
      }
    }
  }
})

Mock.mock(RegExp('/api/reviews/\\d+/reply'), 'post', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/reviews\/(\d+)/)?.[1] || '0')
  return {
    code: 200,
    message: '回复成功',
    data: {
      id
    }
  }
})

Mock.mock(RegExp('/api/reviews/\\d+$'), 'delete', () => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  }
})

export function setupReviewsMock() {
  console.log('[Mock] 评价模块已加载')
}
