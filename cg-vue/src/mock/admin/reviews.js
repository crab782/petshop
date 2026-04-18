import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString } from '../utils/random'

const ReviewStatus = ['pending', 'approved', 'rejected']
const Ratings = [1, 2, 3, 4, 5]

const generateReviews = (count = 30) => {
  const reviews = []
  for (let i = 0; i < count; i++) {
    reviews.push({
      id: randomId(),
      userId: randomId(1, 100),
      userName: randomString(6),
      merchantId: randomId(1, 20),
      merchantName: randomString(6) + '宠物店',
      serviceName: randomEnum(['宠物美容', '宠物洗澡', '宠物医疗']),
      productName: randomString(6) + '狗粮',
      rating: randomEnum(Ratings),
      content: randomString(50),
      images: [
        `https://picsum.photos/100/100?random=${randomId()}`,
        `https://picsum.photos/100/100?random=${randomId()}`
      ],
      status: randomEnum(ReviewStatus),
      createdAt: randomDate('2024-01-01')
    })
  }
  return reviews
}

const reviews = generateReviews(50)

Mock.mock('/api/admin/reviews', 'get', (options) => {
  const params = options.url.split('?')[1]?.split('&').reduce((acc, param) => {
    const [key, value] = param.split('=')
    acc[key] = value
    return acc
  }, {}) || {}

  const { page = 1, pageSize = 10, keyword, rating, startDate, endDate, status } = params

  let filtered = [...reviews]

  if (status) {
    filtered = filtered.filter(r => r.status === status)
  }
  if (rating) {
    filtered = filtered.filter(r => r.rating === parseInt(rating))
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

Mock.mock('/api/admin/reviews/pending', 'get', () => {
  const pending = reviews.filter(r => r.status === 'pending')
  return {
    code: 200,
    message: 'success',
    data: pending.slice(0, 10),
    total: pending.length
  }
})

Mock.mock(/\/api\/admin\/reviews\/\d+\/audit/, 'post', () => ({
  code: 200,
  message: '审核成功',
  data: { success: true }
}))

Mock.mock(/\/api\/admin\/reviews\/\d+/, 'delete', () => ({
  code: 200,
  message: '删除成功',
  data: { success: true }
}))