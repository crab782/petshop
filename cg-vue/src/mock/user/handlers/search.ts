import Mock from 'mockjs'
import { products } from '../data/products'
import { services } from '../data/services'
import { merchants } from '../data/merchants'
import {
  hotSearchKeywords,
  getHotSearchKeywords,
  getSearchHistories,
  addSearchHistory,
  clearSearchHistories
} from '../data/search'

export const setupSearchHandlers = () => {
  Mock.mock(/\/api\/search\?/, 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const keyword = url.searchParams.get('keyword') || ''
    const type = url.searchParams.get('type') || 'all'
    const page = parseInt(url.searchParams.get('page') || '1')
    const size = parseInt(url.searchParams.get('size') || '10')
    const category = url.searchParams.get('category') || ''
    const minPrice = url.searchParams.get('minPrice')
    const maxPrice = url.searchParams.get('maxPrice')
    const minRating = url.searchParams.get('minRating')
    const sortBy = url.searchParams.get('sortBy') || 'relevance'
    const sortOrder = url.searchParams.get('sortOrder') || 'desc'

    if (keyword) {
      addSearchHistory(keyword)
    }

    let filteredProducts = [...products]
    let filteredServices = [...services]
    let filteredMerchants = [...merchants]

    if (keyword) {
      const lowerKeyword = keyword.toLowerCase()
      filteredProducts = filteredProducts.filter(p =>
        p.name.toLowerCase().includes(lowerKeyword) ||
        p.description.toLowerCase().includes(lowerKeyword) ||
        p.category.toLowerCase().includes(lowerKeyword) ||
        p.merchantName.toLowerCase().includes(lowerKeyword)
      )
      filteredServices = filteredServices.filter(s =>
        s.name.toLowerCase().includes(lowerKeyword) ||
        s.description.toLowerCase().includes(lowerKeyword) ||
        s.category.toLowerCase().includes(lowerKeyword) ||
        s.merchantName.toLowerCase().includes(lowerKeyword)
      )
      filteredMerchants = filteredMerchants.filter(m =>
        m.name.toLowerCase().includes(lowerKeyword) ||
        m.description.toLowerCase().includes(lowerKeyword) ||
        m.address.toLowerCase().includes(lowerKeyword)
      )
    }

    if (category) {
      filteredProducts = filteredProducts.filter(p => p.category === category)
      filteredServices = filteredServices.filter(s => s.category === category)
    }

    if (minPrice) {
      filteredProducts = filteredProducts.filter(p => p.price >= parseFloat(minPrice))
      filteredServices = filteredServices.filter(s => s.price >= parseFloat(minPrice))
    }
    if (maxPrice) {
      filteredProducts = filteredProducts.filter(p => p.price <= parseFloat(maxPrice))
      filteredServices = filteredServices.filter(s => s.price <= parseFloat(maxPrice))
    }

    if (minRating) {
      filteredProducts = filteredProducts.filter(p => p.rating >= parseFloat(minRating))
      filteredServices = filteredServices.filter(s => s.rating >= parseFloat(minRating))
      filteredMerchants = filteredMerchants.filter(m => m.rating >= parseFloat(minRating))
    }

    const sortFn = (a: any, b: any, field: string) => {
      let comparison = 0
      switch (field) {
        case 'price':
          comparison = (a.price || 0) - (b.price || 0)
          break
        case 'rating':
          comparison = (a.rating || 0) - (b.rating || 0)
          break
        case 'sales':
          comparison = (a.sales || a.reviewCount || 0) - (b.sales || b.reviewCount || 0)
          break
        case 'relevance':
        default:
          if (keyword) {
            const aScore = (a.name.toLowerCase().includes(keyword.toLowerCase()) ? 10 : 0) +
                          (a.description?.toLowerCase().includes(keyword.toLowerCase()) ? 5 : 0)
            const bScore = (b.name.toLowerCase().includes(keyword.toLowerCase()) ? 10 : 0) +
                          (b.description?.toLowerCase().includes(keyword.toLowerCase()) ? 5 : 0)
            comparison = aScore - bScore
          }
      }
      return sortOrder === 'desc' ? -comparison : comparison
    }

    filteredProducts.sort((a, b) => sortFn(a, b, sortBy))
    filteredServices.sort((a, b) => sortFn(a, b, sortBy))
    filteredMerchants.sort((a, b) => sortFn(a, b, sortBy === 'sales' ? 'rating' : sortBy))

    const productResults = filteredProducts.map(p => ({
      id: p.id,
      type: 'product',
      name: p.name,
      image: p.image,
      price: p.price,
      rating: p.rating,
      sales: p.sales,
      merchantName: p.merchantName,
      merchantId: p.merchantId,
      category: p.category,
      description: p.description.substring(0, 50) + '...'
    }))

    const serviceResults = filteredServices.map(s => ({
      id: s.id,
      type: 'service',
      name: s.name,
      image: s.image,
      price: s.price,
      rating: s.rating,
      sales: s.reviewCount,
      merchantName: s.merchantName,
      merchantId: s.merchantId,
      category: s.category,
      duration: s.duration,
      description: s.description.substring(0, 50) + '...'
    }))

    const merchantResults = filteredMerchants.map(m => ({
      id: m.id,
      type: 'merchant',
      name: m.name,
      image: m.logo,
      rating: m.rating,
      serviceCount: m.serviceCount,
      address: m.address,
      description: m.description
    }))

    let allResults: any[] = []
    if (type === 'all') {
      allResults = [...productResults, ...serviceResults, ...merchantResults]
    } else if (type === 'product') {
      allResults = productResults
    } else if (type === 'service') {
      allResults = serviceResults
    } else if (type === 'merchant') {
      allResults = merchantResults
    }

    const total = allResults.length
    const start = (page - 1) * size
    const end = start + size
    const list = allResults.slice(start, end)

    return {
      code: 200,
      message: 'success',
      data: {
        list,
        total,
        page,
        size,
        totalPages: Math.ceil(total / size),
        summary: {
          productCount: productResults.length,
          serviceCount: serviceResults.length,
          merchantCount: merchantResults.length
        }
      }
    }
  })

  Mock.mock('/api/hot-searches', 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const limit = parseInt(url.searchParams.get('limit') || '10')

    return {
      code: 200,
      message: 'success',
      data: getHotSearchKeywords(limit)
    }
  })

  Mock.mock('/api/search/histories', 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const userId = parseInt(url.searchParams.get('userId') || '1')
    const limit = parseInt(url.searchParams.get('limit') || '10')

    return {
      code: 200,
      message: 'success',
      data: getSearchHistories(userId, limit)
    }
  })

  Mock.mock('/api/search/histories/clear', 'delete', () => {
    clearSearchHistories()
    return {
      code: 200,
      message: 'success',
      data: null
    }
  })

  Mock.mock('/api/search/suggestions', 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const keyword = url.searchParams.get('keyword') || ''
    const limit = parseInt(url.searchParams.get('limit') || '5')

    if (!keyword) {
      return {
        code: 200,
        message: 'success',
        data: []
      }
    }

    const lowerKeyword = keyword.toLowerCase()

    const productSuggestions = products
      .filter(p => p.name.toLowerCase().includes(lowerKeyword))
      .slice(0, limit)
      .map(p => ({ type: 'product', name: p.name, id: p.id }))

    const serviceSuggestions = services
      .filter(s => s.name.toLowerCase().includes(lowerKeyword))
      .slice(0, limit)
      .map(s => ({ type: 'service', name: s.name, id: s.id }))

    const merchantSuggestions = merchants
      .filter(m => m.name.toLowerCase().includes(lowerKeyword))
      .slice(0, limit)
      .map(m => ({ type: 'merchant', name: m.name, id: m.id }))

    const allSuggestions = [...productSuggestions, ...serviceSuggestions, ...merchantSuggestions]
      .slice(0, limit)

    return {
      code: 200,
      message: 'success',
      data: allSuggestions
    }
  })
}
