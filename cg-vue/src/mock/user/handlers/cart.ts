import Mock from 'mockjs'
import type { MockCartItem, MockFavorite, MockReview } from '../types'
import { mockCartItems, mockFavorites, mockReviews, mockMerchants, mockServices, mockProducts } from '../data'
import { createSuccessResponse, createErrorResponse, generateNumericId, generateDate } from '../utils/generators'

const currentUserId = 1

export const setupCartHandlers = () => {
  Mock.mock('/api/user/cart', 'get', () => {
    const items = mockCartItems.filter(c => c.userId === currentUserId)
    return createSuccessResponse(items)
  })

  Mock.mock('/api/user/cart', 'post', (options: { body: string }) => {
    const { productId, quantity } = JSON.parse(options.body) as { productId: number; quantity: number }
    const product = mockProducts.find(p => p.id === productId)
    if (!product) {
      return createErrorResponse('商品不存在', 404)
    }

    const existingItem = mockCartItems.find(c => c.userId === currentUserId && c.productId === productId)
    if (existingItem) {
      existingItem.quantity += quantity
      existingItem.selected = true
      return createSuccessResponse(existingItem, '已添加到购物车')
    }

    const merchant = mockMerchants.find(m => m.id === product.merchantId)
    const newItem: MockCartItem = {
      id: generateNumericId(100, 9999),
      userId: currentUserId,
      productId: product.id,
      productName: product.name,
      productImage: product.image,
      price: product.price,
      quantity,
      merchantId: product.merchantId,
      merchantName: merchant?.name || '',
      selected: true,
      createdAt: generateDate()
    }
    mockCartItems.push(newItem)
    return createSuccessResponse(newItem, '添加成功')
  })

  Mock.mock('/api/user/cart', 'put', (options: { body: string }) => {
    const { productId, quantity } = JSON.parse(options.body) as { productId: number; quantity: number }
    const item = mockCartItems.find(c => c.userId === currentUserId && c.productId === productId)
    if (item) {
      if (quantity <= 0) {
        const index = mockCartItems.indexOf(item)
        mockCartItems.splice(index, 1)
        return createSuccessResponse(null, '已从购物车移除')
      }
      item.quantity = quantity
      return createSuccessResponse(item, '更新成功')
    }
    return createErrorResponse('购物车商品不存在', 404)
  })

  Mock.mock(/\/api\/user\/cart\/\d+/, 'delete', (options: { url: string }) => {
    const productId = parseInt(options.url.split('/').pop() || '0')
    const index = mockCartItems.findIndex(c => c.userId === currentUserId && c.productId === productId)
    if (index > -1) {
      mockCartItems.splice(index, 1)
      return createSuccessResponse(null, '删除成功')
    }
    return createErrorResponse('购物车商品不存在', 404)
  })
}

export const setupFavoriteHandlers = () => {
  Mock.mock('/api/user/favorites', 'get', () => {
    const favorites = mockFavorites.filter(f => f.userId === currentUserId)
    return createSuccessResponse(favorites)
  })

  Mock.mock('/api/user/favorites', 'post', (options: { body: string }) => {
    const { merchantId } = JSON.parse(options.body) as { merchantId: number }
    const existing = mockFavorites.find(f => f.userId === currentUserId && f.merchantId === merchantId)
    if (existing) {
      return createErrorResponse('已收藏该商家', 400)
    }

    const merchant = mockMerchants.find(m => m.id === merchantId)
    if (!merchant) {
      return createErrorResponse('商家不存在', 404)
    }

    const newFavorite: MockFavorite = {
      id: generateNumericId(100, 9999),
      userId: currentUserId,
      merchantId: merchant.id,
      merchantName: merchant.name,
      merchantLogo: merchant.logo,
      merchantAddress: merchant.address,
      merchantPhone: merchant.phone,
      merchantRating: merchant.rating,
      createdAt: generateDate()
    }
    mockFavorites.push(newFavorite)
    return createSuccessResponse(newFavorite, '收藏成功')
  })

  Mock.mock(/\/api\/user\/favorites\/\d+/, 'delete', (options: { url: string }) => {
    const merchantId = parseInt(options.url.split('/').pop() || '0')
    const index = mockFavorites.findIndex(f => f.userId === currentUserId && f.merchantId === merchantId)
    if (index > -1) {
      mockFavorites.splice(index, 1)
      return createSuccessResponse(null, '取消收藏成功')
    }
    return createErrorResponse('收藏不存在', 404)
  })
}

export const setupReviewHandlers = () => {
  Mock.mock('/api/user/reviews', 'get', () => {
    const reviews = mockReviews.filter(r => r.userId === currentUserId)
    return createSuccessResponse(reviews)
  })

  Mock.mock('/api/user/reviews', 'post', (options: { body: string }) => {
    const data = JSON.parse(options.body) as {
      appointmentId: number
      merchantId: number
      serviceId: number
      rating: number
      comment: string
    }

    const merchant = mockMerchants.find(m => m.id === data.merchantId)
    const service = mockServices.find(s => s.id === data.serviceId)

    const newReview: MockReview = {
      id: generateNumericId(100, 9999),
      userId: currentUserId,
      userName: 'testuser',
      userAvatar: '',
      merchantId: data.merchantId,
      merchantName: merchant?.name || '',
      serviceId: data.serviceId,
      serviceName: service?.name || '',
      appointmentId: data.appointmentId,
      rating: data.rating,
      comment: data.comment,
      images: [],
      reply: null,
      replyTime: null,
      createdAt: generateDate()
    }
    mockReviews.push(newReview)
    return createSuccessResponse(newReview, '评价成功')
  })

  Mock.mock(/\/api\/user\/reviews\/\d+/, 'put', (options: { url: string; body: string }) => {
    const id = parseInt(options.url.split('/').pop() || '0')
    const { rating, comment } = JSON.parse(options.body) as { rating: number; comment: string }
    const review = mockReviews.find(r => r.id === id && r.userId === currentUserId)
    if (review) {
      review.rating = rating
      review.comment = comment
      return createSuccessResponse(review, '更新成功')
    }
    return createErrorResponse('评价不存在', 404)
  })

  Mock.mock(/\/api\/user\/reviews\/\d+/, 'delete', (options: { url: string }) => {
    const id = parseInt(options.url.split('/').pop() || '0')
    const index = mockReviews.findIndex(r => r.id === id && r.userId === currentUserId)
    if (index > -1) {
      mockReviews.splice(index, 1)
      return createSuccessResponse(null, '删除成功')
    }
    return createErrorResponse('评价不存在', 404)
  })
}

export default setupCartHandlers
