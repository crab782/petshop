import { MockMethod } from 'vite-plugin-mock'
import { products, getProductById } from '../data/products'

const productFavorites: number[] = []

const productReviews = [
  {
    id: 1,
    productId: 1,
    userId: 1,
    username: '爱猫人士',
    avatar: 'https://picsum.photos/100/100?random=101',
    rating: 5,
    comment: '猫咪很喜欢吃，毛色变得更亮了，性价比很高！',
    createdAt: '2024-01-20 14:30:00'
  },
  {
    id: 2,
    productId: 1,
    userId: 2,
    username: '铲屎官小王',
    avatar: 'https://picsum.photos/100/100?random=102',
    rating: 4,
    comment: '品质不错，就是价格有点贵，希望能多搞活动。',
    createdAt: '2024-01-21 16:45:00'
  },
  {
    id: 3,
    productId: 1,
    userId: 3,
    username: '猫咪主人',
    avatar: 'https://picsum.photos/100/100?random=103',
    rating: 5,
    comment: '回购第三次了，猫咪一直吃这个牌子，很健康！',
    createdAt: '2024-01-22 10:15:00'
  },
  {
    id: 4,
    productId: 2,
    userId: 4,
    username: '狗狗爸爸',
    avatar: 'https://picsum.photos/100/100?random=104',
    rating: 5,
    comment: '狗狗吃了不拉肚子，营养很好，强烈推荐！',
    createdAt: '2024-01-23 11:20:00'
  },
  {
    id: 5,
    productId: 2,
    userId: 5,
    username: '养狗达人',
    avatar: 'https://picsum.photos/100/100?random=105',
    rating: 4,
    comment: '无谷配方很好，狗狗肠胃敏感也能吃。',
    createdAt: '2024-01-24 09:30:00'
  }
]

const productHandlers: MockMethod[] = [
  {
    url: '/api/products/:id',
    method: 'get',
    response: (req) => {
      const { id } = req.params
      const product = getProductById(parseInt(id))
      
      if (!product) {
        return {
          code: 404,
          message: '商品不存在',
          data: null
        }
      }

      const isFavorited = productFavorites.includes(product.id)
      
      return {
        code: 200,
        message: 'success',
        data: {
          ...product,
          isFavorited
        }
      }
    }
  },
  {
    url: '/api/products/:id/reviews',
    method: 'get',
    response: (req) => {
      const { id } = req.params
      const { page = 1, size = 10, rating } = req.query
      
      let filteredReviews = productReviews.filter(r => r.productId === parseInt(id))
      
      if (rating) {
        filteredReviews = filteredReviews.filter(r => r.rating === parseInt(rating))
      }
      
      const total = filteredReviews.length
      const start = (parseInt(page) - 1) * parseInt(size)
      const end = start + parseInt(size)
      const list = filteredReviews.slice(start, end)
      
      const avgRating = filteredReviews.length > 0 
        ? (filteredReviews.reduce((sum, r) => sum + r.rating, 0) / filteredReviews.length).toFixed(1)
        : '0.0'
      
      const ratingDistribution = {
        5: filteredReviews.filter(r => r.rating === 5).length,
        4: filteredReviews.filter(r => r.rating === 4).length,
        3: filteredReviews.filter(r => r.rating === 3).length,
        2: filteredReviews.filter(r => r.rating === 2).length,
        1: filteredReviews.filter(r => r.rating === 1).length
      }
      
      return {
        code: 200,
        message: 'success',
        data: {
          total,
          page: parseInt(page),
          size: parseInt(size),
          list,
          avgRating,
          ratingDistribution
        }
      }
    }
  },
  {
    url: '/api/user/favorites/products',
    method: 'post',
    response: (req) => {
      const { productId } = req.body
      
      if (!productId) {
        return {
          code: 400,
          message: '商品ID不能为空',
          data: null
        }
      }
      
      const product = getProductById(productId)
      if (!product) {
        return {
          code: 404,
          message: '商品不存在',
          data: null
        }
      }
      
      if (productFavorites.includes(productId)) {
        return {
          code: 400,
          message: '已收藏该商品',
          data: null
        }
      }
      
      productFavorites.push(productId)
      
      return {
        code: 200,
        message: '收藏成功',
        data: {
          productId
        }
      }
    }
  },
  {
    url: '/api/user/favorites/products/:id',
    method: 'delete',
    response: (req) => {
      const { id } = req.params
      const productId = parseInt(id)
      
      const index = productFavorites.indexOf(productId)
      if (index === -1) {
        return {
          code: 404,
          message: '未收藏该商品',
          data: null
        }
      }
      
      productFavorites.splice(index, 1)
      
      return {
        code: 200,
        message: '取消收藏成功',
        data: null
      }
    }
  },
  {
    url: '/api/user/favorites/products',
    method: 'get',
    response: () => {
      const favoriteProducts = productFavorites.map(id => getProductById(id)).filter(p => p)
      
      return {
        code: 200,
        message: 'success',
        data: favoriteProducts
      }
    }
  },
  {
    url: '/api/user/favorites/products/:id/check',
    method: 'get',
    response: (req) => {
      const { id } = req.params
      const productId = parseInt(id)
      
      const isFavorited = productFavorites.includes(productId)
      
      return {
        code: 200,
        message: 'success',
        data: { isFavorited }
      }
    }
  }
]

export default productHandlers
