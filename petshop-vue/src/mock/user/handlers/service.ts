import Mock from 'mockjs'
import { mockMerchants, mockServices, mockProducts, mockDashboardStats, mockRecentActivities } from '../data'
import { createSuccessResponse, createErrorResponse, paginateList } from '../utils/generators'

export const setupServiceHandlers = () => {
  Mock.mock('/api/services', 'get', (options: { body?: string }) => {
    let params = {}
    if (options.body) {
      params = JSON.parse(options.body)
    }
    let services = [...mockServices]

    if (params && 'type' in params && params.type) {
      services = services.filter(s => s.category === params.type)
    }

    return createSuccessResponse(services)
  })

  Mock.mock(/\/api\/services\/\d+/, 'get', (options: { url: string }) => {
    const id = parseInt(options.url.split('/').pop() || '0')
    const service = mockServices.find(s => s.id === id)
    if (service) {
      return createSuccessResponse(service)
    }
    return createErrorResponse('服务不存在', 404)
  })

  Mock.mock('/api/services/search', 'get', (options: { url: string }) => {
    const url = new URL(options.url, 'http://localhost')
    const keyword = url.searchParams.get('keyword') || ''
    const services = mockServices.filter(s =>
      s.name.includes(keyword) || s.description.includes(keyword)
    )
    return createSuccessResponse(services)
  })

  Mock.mock('/api/products', 'get', (options: { url: string }) => {
    const url = new URL(options.url, 'http://localhost')
    const keyword = url.searchParams.get('keyword') || ''
    const page = parseInt(url.searchParams.get('page') || '1')
    const pageSize = parseInt(url.searchParams.get('pageSize') || '10')

    let products = [...mockProducts]
    if (keyword) {
      products = products.filter(p =>
        p.name.includes(keyword) || p.description.includes(keyword)
      )
    }

    const result = paginateList(products, page, pageSize)
    return createSuccessResponse(result)
  })

  Mock.mock(/\/api\/products\/\d+/, 'get', (options: { url: string }) => {
    const id = parseInt(options.url.split('/').pop() || '0')
    const product = mockProducts.find(p => p.id === id)
    if (product) {
      return createSuccessResponse(product)
    }
    return createErrorResponse('商品不存在', 404)
  })

  Mock.mock('/api/products/search', 'get', (options: { url: string }) => {
    const url = new URL(options.url, 'http://localhost')
    const keyword = url.searchParams.get('keyword') || ''
    const products = mockProducts.filter(p =>
      p.name.includes(keyword) || p.description.includes(keyword)
    )
    return createSuccessResponse(products)
  })
}

export const setupMerchantHandlers = () => {
  Mock.mock('/api/merchants', 'get', (options: { url: string }) => {
    const url = new URL(options.url, 'http://localhost')
    const keyword = url.searchParams.get('keyword') || ''
    const page = parseInt(url.searchParams.get('page') || '1')
    const pageSize = parseInt(url.searchParams.get('pageSize') || '10')

    let merchants = mockMerchants.map(m => ({
      id: m.id,
      name: m.name,
      logo: m.logo,
      address: m.address,
      phone: m.phone,
      rating: m.rating,
      serviceCount: m.serviceCount,
      isFavorited: false
    }))

    if (keyword) {
      merchants = merchants.filter(m =>
        m.name.includes(keyword) || m.address.includes(keyword)
      )
    }

    const result = paginateList(merchants, page, pageSize)
    return createSuccessResponse(result)
  })

  Mock.mock(/\/api\/merchant\/\d+$/, 'get', (options: { url: string }) => {
    const id = parseInt(options.url.split('/').pop() || '0')
    const merchant = mockMerchants.find(m => m.id === id)
    if (merchant) {
      return createSuccessResponse(merchant)
    }
    return createErrorResponse('商家不存在', 404)
  })

  Mock.mock(/\/api\/merchant\/\d+\/services/, 'get', (options: { url: string }) => {
    const id = parseInt(options.url.split('/')[3])
    const services = mockServices.filter(s => s.merchantId === id)
    return createSuccessResponse(services)
  })

  Mock.mock(/\/api\/merchant\/\d+\/reviews/, 'get', (options: { url: string }) => {
    const id = parseInt(options.url.split('/')[3])
    const reviews = mockMerchants.find(m => m.id === id)
    return createSuccessResponse([])
  })
}

export const setupDashboardHandlers = () => {
  Mock.mock('/api/user/dashboard/stats', 'get', () => {
    return createSuccessResponse(mockDashboardStats)
  })

  Mock.mock('/api/user/dashboard/activities', 'get', () => {
    return createSuccessResponse(mockRecentActivities)
  })
}

export default setupServiceHandlers
