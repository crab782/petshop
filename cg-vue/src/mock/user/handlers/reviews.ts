import Mock from 'mockjs'
import { reviewsData, getTargetName, getMerchantName, reviewStats, type Review, type TargetType } from '../data/reviews'
import { randomId, randomDate } from '../../utils/random'

const reviewsList = [...reviewsData]

const parseQuery = (url: string) => {
  const queryStr = url.split('?')[1]
  if (!queryStr) return {}
  return queryStr.split('&').reduce((acc, param) => {
    const [key, value] = param.split('=')
    acc[key] = decodeURIComponent(value)
    return acc
  }, {} as Record<string, string>)
}

Mock.mock(/\/api\/reviews(\?.*)?$/, 'get', (options: { url: string }) => {
  const { page = '1', size = '10', rating, startDate, endDate, targetType, keyword } = parseQuery(options.url)

  let filtered = [...reviewsList]

  if (rating) {
    filtered = filtered.filter(r => r.rating === parseInt(rating))
  }
  if (targetType) {
    filtered = filtered.filter(r => r.target_type === targetType)
  }
  if (startDate) {
    filtered = filtered.filter(r => r.created_at >= startDate)
  }
  if (endDate) {
    filtered = filtered.filter(r => r.created_at <= endDate + ' 23:59:59')
  }
  if (keyword) {
    filtered = filtered.filter(r => r.comment.includes(keyword))
  }

  filtered.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())

  const pageNum = parseInt(page)
  const pageSize = parseInt(size)
  const start = (pageNum - 1) * pageSize
  const end = start + pageSize
  const paginatedList = filtered.slice(start, end)

  const resultList = paginatedList.map(item => ({
    ...item,
    target_name: getTargetName(item.target_type, item.target_id),
    merchant_name: getMerchantName(item.merchant_id),
    user: {
      username: Mock.Random.cname(),
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=user${item.user_id}`
    }
  }))

  return {
    code: 200,
    message: 'success',
    data: {
      total: filtered.length,
      page: pageNum,
      size: pageSize,
      list: resultList
    }
  }
})

Mock.mock('/api/reviews', 'post', (options: { body: string }) => {
  const body = JSON.parse(options.body) as {
    target_id: number
    target_type: TargetType
    merchant_id: number
    rating: number
    comment: string
    images?: string[]
  }

  const newReview: Review = {
    id: randomId(1000, 9999),
    user_id: 1,
    target_id: body.target_id,
    target_type: body.target_type,
    merchant_id: body.merchant_id,
    rating: body.rating,
    comment: body.comment,
    images: body.images || [],
    created_at: new Date().toISOString().replace('T', ' ').substring(0, 19),
    updated_at: new Date().toISOString().replace('T', ' ').substring(0, 19)
  }

  reviewsList.unshift(newReview)

  return {
    code: 200,
    message: '评价创建成功',
    data: newReview
  }
})

Mock.mock(/\/api\/reviews\/\d+$/, 'put', (options: { url: string; body: string }) => {
  const id = parseInt(options.url.match(/\/api\/reviews\/(\d+)/)?.[1] || '0')
  const body = JSON.parse(options.body) as Partial<Review>

  const index = reviewsList.findIndex(r => r.id === id)
  if (index === -1) {
    return {
      code: 404,
      message: '评价不存在',
      data: null
    }
  }

  reviewsList[index] = {
    ...reviewsList[index],
    ...body,
    updated_at: new Date().toISOString().replace('T', ' ').substring(0, 19)
  }

  return {
    code: 200,
    message: '评价更新成功',
    data: reviewsList[index]
  }
})

Mock.mock(/\/api\/reviews\/\d+$/, 'delete', (options: { url: string }) => {
  const id = parseInt(options.url.match(/\/api\/reviews\/(\d+)/)?.[1] || '0')
  const index = reviewsList.findIndex(r => r.id === id)

  if (index === -1) {
    return {
      code: 404,
      message: '评价不存在',
      data: null
    }
  }

  reviewsList.splice(index, 1)

  return {
    code: 200,
    message: '评价删除成功',
    data: null
  }
})

Mock.mock(/\/api\/user\/reviews(\?.*)?$/, 'get', (options: { url: string }) => {
  const { page = '1', size = '10', targetType, startDate, endDate } = parseQuery(options.url)

  let filtered = reviewsList.filter(r => r.user_id === 1)

  if (targetType) {
    filtered = filtered.filter(r => r.target_type === targetType)
  }
  if (startDate) {
    filtered = filtered.filter(r => r.created_at >= startDate)
  }
  if (endDate) {
    filtered = filtered.filter(r => r.created_at <= endDate + ' 23:59:59')
  }

  filtered.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())

  const pageNum = parseInt(page)
  const pageSize = parseInt(size)
  const start = (pageNum - 1) * pageSize
  const end = start + pageSize
  const paginatedList = filtered.slice(start, end)

  const resultList = paginatedList.map(item => ({
    ...item,
    target_name: getTargetName(item.target_type, item.target_id),
    merchant_name: getMerchantName(item.merchant_id)
  }))

  return {
    code: 200,
    message: 'success',
    data: {
      total: filtered.length,
      page: pageNum,
      size: pageSize,
      list: resultList
    }
  }
})

Mock.mock('/api/reviews/stats', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: reviewStats
  }
})

export { reviewsList }
