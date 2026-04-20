import Mock from 'mockjs'
import { getUserStats, getRecommendations, getRecentActivities, recommendations } from '../data/stats'
import { services } from '../data/services'
import { products } from '../data/products'
import { merchants } from '../data/merchants'
import { petsData } from '../data/pets'
import { appointments } from '../data/appointments'
import { orders } from '../data/orders'

export const setupStatsHandlers = () => {
  Mock.mock('/api/services/recommended', 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const limit = parseInt(url.searchParams.get('limit') || '6')

    const recommendedServices = [...services]
      .sort((a, b) => b.rating * b.reviewCount - a.rating * a.reviewCount)
      .slice(0, limit)
      .map(s => ({
        id: s.id,
        name: s.name,
        image: s.image,
        price: s.price,
        rating: s.rating,
        reviewCount: s.reviewCount,
        merchantName: s.merchantName,
        merchantId: s.merchantId,
        category: s.category,
        duration: s.duration
      }))

    return {
      code: 200,
      message: 'success',
      data: recommendedServices
    }
  })

  Mock.mock('/api/user/stats', 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const userId = parseInt(url.searchParams.get('userId') || '1')

    const userPets = petsData.filter(p => p.user_id === userId)
    const pendingAppointments = appointments.filter(a => a.userId === userId && a.status === 'pending')
    const pendingOrders = orders.filter(o => o.userId === userId && o.status === 'pending')
    const completedOrders = orders.filter(o => o.userId === userId && o.status === 'completed')
    const totalSpent = completedOrders.reduce((sum, o) => sum + o.totalPrice, 0)

    const stats = {
      petCount: userPets.length,
      pendingAppointments: pendingAppointments.length,
      pendingOrders: pendingOrders.length,
      favoriteCount: Math.floor(Math.random() * 10) + 5,
      reviewCount: Math.floor(Math.random() * 20) + 5,
      totalSpent: Math.round(totalSpent * 100) / 100,
      memberDays: Math.floor(Math.random() * 500) + 100,
      level: ['普通会员', '白银会员', '黄金会员', '钻石会员'][Math.floor(Math.random() * 4)],
      points: Math.floor(Math.random() * 5000) + 500
    }

    return {
      code: 200,
      message: 'success',
      data: stats
    }
  })

  Mock.mock('/api/recommendations', 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const limit = parseInt(url.searchParams.get('limit') || '6')
    const type = url.searchParams.get('type') as 'service' | 'product' | undefined

    const result = getRecommendations(limit, type)

    return {
      code: 200,
      message: 'success',
      data: result
    }
  })

  Mock.mock('/api/user/activities', 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const userId = parseInt(url.searchParams.get('userId') || '1')
    const limit = parseInt(url.searchParams.get('limit') || '5')

    const activities = getRecentActivities(userId, limit)

    return {
      code: 200,
      message: 'success',
      data: activities
    }
  })

  Mock.mock('/api/user/dashboard', 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const userId = parseInt(url.searchParams.get('userId') || '1')

    const userPets = petsData.filter(p => p.user_id === userId)
    const pendingAppointments = appointments.filter(a => a.userId === userId && a.status === 'pending')
    const pendingOrders = orders.filter(o => o.userId === userId && o.status === 'pending')
    const completedOrders = orders.filter(o => o.userId === userId && o.status === 'completed')
    const totalSpent = completedOrders.reduce((sum, o) => sum + o.totalPrice, 0)

    const topServices = [...services]
      .sort((a, b) => b.rating * b.reviewCount - a.rating * a.reviewCount)
      .slice(0, 4)
      .map(s => ({
        id: s.id,
        name: s.name,
        image: s.image,
        price: s.price,
        rating: s.rating,
        merchantName: s.merchantName,
        category: s.category
      }))

    const topProducts = [...products]
      .sort((a, b) => b.rating * b.sales - a.rating * a.sales)
      .slice(0, 4)
      .map(p => ({
        id: p.id,
        name: p.name,
        image: p.image,
        price: p.price,
        rating: p.rating,
        merchantName: p.merchantName,
        category: p.category
      }))

    const nearbyMerchants = [...merchants]
      .sort((a, b) => b.rating - a.rating)
      .slice(0, 3)
      .map(m => ({
        id: m.id,
        name: m.name,
        logo: m.logo,
        rating: m.rating,
        address: m.address,
        serviceCount: m.serviceCount
      }))

    return {
      code: 200,
      message: 'success',
      data: {
        stats: {
          petCount: userPets.length,
          pendingAppointments: pendingAppointments.length,
          pendingOrders: pendingOrders.length,
          favoriteCount: Math.floor(Math.random() * 10) + 5,
          reviewCount: Math.floor(Math.random() * 20) + 5,
          totalSpent: Math.round(totalSpent * 100) / 100
        },
        pets: userPets.map(p => ({
          id: p.id,
          name: p.name,
          type: p.type,
          breed: p.breed,
          avatar: p.avatar
        })),
        recentActivities: getRecentActivities(userId, 5),
        recommendations: {
          services: topServices,
          products: topProducts
        },
        nearbyMerchants
      }
    }
  })

  Mock.mock('/api/user/quick-actions', 'get', () => {
    return {
      code: 200,
      message: 'success',
      data: [
        { id: 1, icon: 'calendar', title: '预约服务', desc: '预约宠物服务', link: '/user/services' },
        { id: 2, icon: 'pet', title: '我的宠物', desc: '管理宠物信息', link: '/user/pets' },
        { id: 3, icon: 'order', title: '我的订单', desc: '查看订单状态', link: '/user/orders' },
        { id: 4, icon: 'review', title: '我的评价', desc: '查看已评价', link: '/user/reviews' }
      ]
    }
  })

  Mock.mock('/api/trending', 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const type = url.searchParams.get('type') || 'all'
    const limit = parseInt(url.searchParams.get('limit') || '8')

    let trendingItems: any[] = []

    if (type === 'all' || type === 'service') {
      const trendingServices = [...services]
        .sort((a, b) => b.reviewCount - a.reviewCount)
        .slice(0, limit)
        .map(s => ({
          id: s.id,
          type: 'service',
          name: s.name,
          image: s.image,
          price: s.price,
          rating: s.rating,
          sales: s.reviewCount,
          merchantName: s.merchantName,
          category: s.category
        }))
      trendingItems = [...trendingItems, ...trendingServices]
    }

    if (type === 'all' || type === 'product') {
      const trendingProducts = [...products]
        .sort((a, b) => b.sales - a.sales)
        .slice(0, limit)
        .map(p => ({
          id: p.id,
          type: 'product',
          name: p.name,
          image: p.image,
          price: p.price,
          rating: p.rating,
          sales: p.sales,
          merchantName: p.merchantName,
          category: p.category
        }))
      trendingItems = [...trendingItems, ...trendingProducts]
    }

    return {
      code: 200,
      message: 'success',
      data: trendingItems.slice(0, limit)
    }
  })
}
