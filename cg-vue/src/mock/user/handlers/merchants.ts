import Mock from 'mockjs'
import { merchants, getMerchantById } from '../data/merchants'
import { getServicesByMerchantId } from '../data/services'

export const setupMerchantHandlers = () => {
  Mock.mock(/\/api\/merchants\?/, 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const page = parseInt(url.searchParams.get('page') || '1')
    const size = parseInt(url.searchParams.get('size') || '10')
    const keyword = url.searchParams.get('keyword') || ''
    const minRating = url.searchParams.get('minRating')
    const maxRating = url.searchParams.get('maxRating')
    const sortBy = url.searchParams.get('sortBy') || 'rating'
    const sortOrder = url.searchParams.get('sortOrder') || 'desc'

    let filtered = [...merchants]

    if (keyword) {
      filtered = filtered.filter(m =>
        m.name.includes(keyword) ||
        m.address.includes(keyword) ||
        m.description.includes(keyword)
      )
    }

    if (minRating) {
      filtered = filtered.filter(m => m.rating >= parseFloat(minRating))
    }
    if (maxRating) {
      filtered = filtered.filter(m => m.rating <= parseFloat(maxRating))
    }

    filtered.sort((a, b) => {
      let comparison = 0
      switch (sortBy) {
        case 'rating':
          comparison = a.rating - b.rating
          break
        case 'serviceCount':
          comparison = a.serviceCount - b.serviceCount
          break
        case 'name':
          comparison = a.name.localeCompare(b.name)
          break
        case 'createdAt':
          comparison = new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
          break
        default:
          comparison = a.rating - b.rating
      }
      return sortOrder === 'desc' ? -comparison : comparison
    })

    const total = filtered.length
    const start = (page - 1) * size
    const end = start + size
    const list = filtered.slice(start, end)

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

  Mock.mock(/\/api\/merchants\/\d+$/, 'get', (options) => {
    const match = options.url.match(/\/api\/merchants\/(\d+)/)
    if (!match) {
      return {
        code: 400,
        message: '无效的商家ID',
        data: null
      }
    }

    const id = parseInt(match[1])
    const merchant = getMerchantById(id)

    if (!merchant) {
      return {
        code: 404,
        message: '商家不存在',
        data: null
      }
    }

    const merchantServices = getServicesByMerchantId(id)

    return {
      code: 200,
      message: 'success',
      data: {
        ...merchant,
        services: merchantServices,
        products: [],
        reviews: [],
        businessHours: '09:00-21:00',
        features: ['专业服务', '环境舒适', '价格透明', '预约便捷']
      }
    }
  })

  Mock.mock('/api/merchants/nearby', 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const lat = parseFloat(url.searchParams.get('lat') || '39.9')
    const lng = parseFloat(url.searchParams.get('lng') || '116.4')
    const radius = parseFloat(url.searchParams.get('radius') || '5')

    const nearbyMerchants = merchants.slice(0, 5).map(m => ({
      ...m,
      distance: Math.round(Math.random() * radius * 10) / 10
    }))

    nearbyMerchants.sort((a, b) => a.distance - b.distance)

    return {
      code: 200,
      message: 'success',
      data: nearbyMerchants
    }
  })

  Mock.mock('/api/merchants/recommend', 'get', () => {
    const recommendMerchants = [...merchants]
      .sort((a, b) => b.rating - a.rating)
      .slice(0, 6)

    return {
      code: 200,
      message: 'success',
      data: recommendMerchants
    }
  })
}
