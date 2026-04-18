import Mock from 'mockjs'
import { merchants, getMerchantsByIds, Merchant } from '../data/merchants'
import { services, getServicesByIds, Service } from '../data/services'

interface FavoriteMerchant {
  id: number
  userId: number
  merchantId: number
  merchant: Merchant
  createdAt: string
}

interface FavoriteService {
  id: number
  userId: number
  serviceId: number
  service: Service
  createdAt: string
}

let favoriteMerchants: FavoriteMerchant[] = []
let favoriteServices: FavoriteService[] = []
let favoriteMerchantId = 1
let favoriteServiceId = 1

const currentUserId = 1

export const setupFavoriteHandlers = () => {
  Mock.mock(/\/api\/favorites\/merchants\?/, 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const page = parseInt(url.searchParams.get('page') || '1')
    const size = parseInt(url.searchParams.get('size') || '10')

    const userFavorites = favoriteMerchants.filter(f => f.userId === currentUserId)
    const total = userFavorites.length
    const start = (page - 1) * size
    const end = start + size
    const list = userFavorites.slice(start, end)

    return {
      code: 200,
      message: 'success',
      data: {
        list,
        total,
        page,
        size,
        totalPages: Math.ceil(total / size)
      }
    }
  })

  Mock.mock('/api/favorites/merchants', 'post', (options) => {
    const body = JSON.parse(options.body)
    const { merchantId } = body

    const merchant = merchants.find(m => m.id === merchantId)
    if (!merchant) {
      return {
        code: 404,
        message: '商家不存在',
        data: null
      }
    }

    const existing = favoriteMerchants.find(
      f => f.userId === currentUserId && f.merchantId === merchantId
    )
    if (existing) {
      return {
        code: 400,
        message: '已收藏该商家',
        data: null
      }
    }

    const favorite: FavoriteMerchant = {
      id: favoriteMerchantId++,
      userId: currentUserId,
      merchantId,
      merchant,
      createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
    }
    favoriteMerchants.push(favorite)

    return {
      code: 200,
      message: '收藏成功',
      data: favorite
    }
  })

  Mock.mock(/\/api\/favorites\/merchants\/\d+$/, 'delete', (options) => {
    const match = options.url.match(/\/api\/favorites\/merchants\/(\d+)/)
    if (!match) {
      return {
        code: 400,
        message: '无效的收藏ID',
        data: null
      }
    }

    const merchantId = parseInt(match[1])
    const index = favoriteMerchants.findIndex(
      f => f.userId === currentUserId && f.merchantId === merchantId
    )

    if (index === -1) {
      return {
        code: 404,
        message: '收藏记录不存在',
        data: null
      }
    }

    favoriteMerchants.splice(index, 1)

    return {
      code: 200,
      message: '取消收藏成功',
      data: null
    }
  })

  Mock.mock(/\/api\/favorites\/merchants\/check\/\d+$/, 'get', (options) => {
    const match = options.url.match(/\/api\/favorites\/merchants\/check\/(\d+)/)
    if (!match) {
      return {
        code: 400,
        message: '无效的商家ID',
        data: null
      }
    }

    const merchantId = parseInt(match[1])
    const isFavorited = favoriteMerchants.some(
      f => f.userId === currentUserId && f.merchantId === merchantId
    )

    return {
      code: 200,
      message: 'success',
      data: { isFavorited }
    }
  })

  Mock.mock(/\/api\/favorites\/services\?/, 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const page = parseInt(url.searchParams.get('page') || '1')
    const size = parseInt(url.searchParams.get('size') || '10')

    const userFavorites = favoriteServices.filter(f => f.userId === currentUserId)
    const total = userFavorites.length
    const start = (page - 1) * size
    const end = start + size
    const list = userFavorites.slice(start, end)

    return {
      code: 200,
      message: 'success',
      data: {
        list,
        total,
        page,
        size,
        totalPages: Math.ceil(total / size)
      }
    }
  })

  Mock.mock('/api/favorites/services', 'post', (options) => {
    const body = JSON.parse(options.body)
    const { serviceId } = body

    const service = services.find(s => s.id === serviceId)
    if (!service) {
      return {
        code: 404,
        message: '服务不存在',
        data: null
      }
    }

    const existing = favoriteServices.find(
      f => f.userId === currentUserId && f.serviceId === serviceId
    )
    if (existing) {
      return {
        code: 400,
        message: '已收藏该服务',
        data: null
      }
    }

    const favorite: FavoriteService = {
      id: favoriteServiceId++,
      userId: currentUserId,
      serviceId,
      service,
      createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
    }
    favoriteServices.push(favorite)

    return {
      code: 200,
      message: '收藏成功',
      data: favorite
    }
  })

  Mock.mock(/\/api\/favorites\/services\/\d+$/, 'delete', (options) => {
    const match = options.url.match(/\/api\/favorites\/services\/(\d+)/)
    if (!match) {
      return {
        code: 400,
        message: '无效的收藏ID',
        data: null
      }
    }

    const serviceId = parseInt(match[1])
    const index = favoriteServices.findIndex(
      f => f.userId === currentUserId && f.serviceId === serviceId
    )

    if (index === -1) {
      return {
        code: 404,
        message: '收藏记录不存在',
        data: null
      }
    }

    favoriteServices.splice(index, 1)

    return {
      code: 200,
      message: '取消收藏成功',
      data: null
    }
  })

  Mock.mock(/\/api\/favorites\/services\/check\/\d+$/, 'get', (options) => {
    const match = options.url.match(/\/api\/favorites\/services\/check\/(\d+)/)
    if (!match) {
      return {
        code: 400,
        message: '无效的服务ID',
        data: null
      }
    }

    const serviceId = parseInt(match[1])
    const isFavorited = favoriteServices.some(
      f => f.userId === currentUserId && f.serviceId === serviceId
    )

    return {
      code: 200,
      message: 'success',
      data: { isFavorited }
    }
  })

  Mock.mock('/api/favorites/count', 'get', () => {
    return {
      code: 200,
      message: 'success',
      data: {
        merchantCount: favoriteMerchants.filter(f => f.userId === currentUserId).length,
        serviceCount: favoriteServices.filter(f => f.userId === currentUserId).length
      }
    }
  })
}
