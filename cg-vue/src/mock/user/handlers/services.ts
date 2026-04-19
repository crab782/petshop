import Mock from 'mockjs'
import { services, getServiceById, getServicesByMerchantId } from '../data/services'
import { getMerchantById } from '../data/merchants'

export const setupServiceHandlers = () => {
  Mock.mock(/\/api\/services\?/, 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const page = parseInt(url.searchParams.get('page') || '1')
    const size = parseInt(url.searchParams.get('size') || '10')
    const keyword = url.searchParams.get('keyword') || ''
    const category = url.searchParams.get('category') || ''
    const merchantId = url.searchParams.get('merchantId')
    const minPrice = url.searchParams.get('minPrice')
    const maxPrice = url.searchParams.get('maxPrice')
    const minRating = url.searchParams.get('minRating')
    const maxRating = url.searchParams.get('maxRating')
    const minDuration = url.searchParams.get('minDuration')
    const maxDuration = url.searchParams.get('maxDuration')
    const sortBy = url.searchParams.get('sortBy') || 'rating'
    const sortOrder = url.searchParams.get('sortOrder') || 'desc'

    let filtered = [...services]

    if (keyword) {
      filtered = filtered.filter(s =>
        s.name.includes(keyword) ||
        s.description.includes(keyword) ||
        s.merchantName.includes(keyword)
      )
    }

    if (category) {
      filtered = filtered.filter(s => s.category === category)
    }

    if (merchantId) {
      filtered = filtered.filter(s => s.merchantId === parseInt(merchantId))
    }

    if (minPrice) {
      filtered = filtered.filter(s => s.price >= parseFloat(minPrice))
    }
    if (maxPrice) {
      filtered = filtered.filter(s => s.price <= parseFloat(maxPrice))
    }

    if (minRating) {
      filtered = filtered.filter(s => s.rating >= parseFloat(minRating))
    }
    if (maxRating) {
      filtered = filtered.filter(s => s.rating <= parseFloat(maxRating))
    }

    if (minDuration) {
      filtered = filtered.filter(s => s.duration >= parseInt(minDuration))
    }
    if (maxDuration) {
      filtered = filtered.filter(s => s.duration <= parseInt(maxDuration))
    }

    filtered.sort((a, b) => {
      let comparison = 0
      switch (sortBy) {
        case 'rating':
          comparison = a.rating - b.rating
          break
        case 'price':
          comparison = a.price - b.price
          break
        case 'duration':
          comparison = a.duration - b.duration
          break
        case 'reviewCount':
          comparison = a.reviewCount - b.reviewCount
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

  Mock.mock(/\/api\/services\/\d+$/, 'get', (options) => {
    const match = options.url.match(/\/api\/services\/(\d+)/)
    if (!match) {
      return {
        code: 400,
        message: '无效的服务ID',
        data: null
      }
    }

    const id = parseInt(match[1])
    const service = getServiceById(id)

    if (!service) {
      return {
        code: 404,
        message: '服务不存在',
        data: null
      }
    }

    const merchant = getMerchantById(service.merchantId)

    const reviews = Mock.mock({
      'list|5': [
        {
          id: '@integer(1, 1000)',
          userId: '@integer(1, 100)',
          username: '@cname()',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20friendly&image_size=square',
          rating: '@integer(3, 5)',
          comment: '@cparagraph(1, 2)',
          createdAt: '@datetime("yyyy-MM-dd HH:mm:ss")'
        }
      ]
    }).list

    return {
      code: 200,
      message: 'success',
      data: {
        ...service,
        merchant: merchant ? {
          id: merchant.id,
          name: merchant.name,
          logo: merchant.logo,
          address: merchant.address,
          phone: merchant.phone,
          rating: merchant.rating
        } : null,
        reviews,
        reviewStats: {
          average: service.rating,
          total: service.reviewCount,
          distribution: {
            5: Math.floor(service.reviewCount * 0.5),
            4: Math.floor(service.reviewCount * 0.25),
            3: Math.floor(service.reviewCount * 0.15),
            2: Math.floor(service.reviewCount * 0.07),
            1: Math.floor(service.reviewCount * 0.03)
          }
        },
        availableSlots: [
          { time: '09:00', available: true },
          { time: '10:00', available: true },
          { time: '11:00', available: false },
          { time: '14:00', available: true },
          { time: '15:00', available: true },
          { time: '16:00', available: false },
          { time: '17:00', available: true }
        ]
      }
    }
  })

  Mock.mock('/api/services/categories', 'get', () => {
    const categories = [
      { name: '宠物美容', count: services.filter(s => s.category === '宠物美容').length },
      { name: '宠物寄养', count: services.filter(s => s.category === '宠物寄养').length },
      { name: '宠物医疗', count: services.filter(s => s.category === '宠物医疗').length },
      { name: '宠物训练', count: services.filter(s => s.category === '宠物训练').length },
      { name: '宠物摄影', count: services.filter(s => s.category === '宠物摄影').length },
      { name: '宠物接送', count: services.filter(s => s.category === '宠物接送').length },
      { name: '宠物SPA', count: services.filter(s => s.category === '宠物SPA').length },
      { name: '宠物体检', count: services.filter(s => s.category === '宠物体检').length }
    ]

    return {
      code: 200,
      message: 'success',
      data: categories
    }
  })

  Mock.mock('/api/services/hot', 'get', () => {
    const hotServices = [...services]
      .sort((a, b) => b.reviewCount - a.reviewCount)
      .slice(0, 8)

    return {
      code: 200,
      message: 'success',
      data: hotServices
    }
  })

  Mock.mock('/api/services/recommended', 'get', () => {
    const recommendServices = [...services]
      .sort((a, b) => b.rating - a.rating)
      .slice(0, 6)

    return {
      code: 200,
      message: 'success',
      data: recommendServices
    }
  })

  Mock.mock(/\/api\/merchant\/\d+\/services/, 'get', (options) => {
    const match = options.url.match(/\/api\/merchant\/(\d+)\/services/)
    if (!match) {
      return {
        code: 400,
        message: '无效的商家ID',
        data: null
      }
    }

    const merchantId = parseInt(match[1])
    const merchantServices = getServicesByMerchantId(merchantId)

    return {
      code: 200,
      message: 'success',
      data: merchantServices
    }
  })
}
