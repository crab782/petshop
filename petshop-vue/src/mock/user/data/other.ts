import type { MockReview, MockCartItem, MockFavorite, MockNotification, MockAnnouncement, MockDashboardStats, MockRecentActivity } from '../types'
import {
  generateNumericId,
  generateDate,
  randomRating,
  randomChineseName,
  randomAvatar,
  randomImage,
  randomEnum,
  randomBoolean
} from '../utils/generators'
import { mockUsers } from './user'
import { mockMerchants, mockServices, mockProducts } from './merchant'

export const mockReviews: MockReview[] = [
  {
    id: 1,
    userId: 1,
    userName: 'testuser',
    userAvatar: randomAvatar(),
    merchantId: 1,
    merchantName: '宠物乐园1号店',
    serviceId: 1,
    serviceName: '宠物洗澡',
    appointmentId: 1,
    rating: 5,
    comment: '服务非常好，狗狗洗得很干净，工作人员很专业！',
    images: [randomImage(200, 200), randomImage(200, 200)],
    reply: '感谢您的好评，欢迎下次光临！',
    replyTime: '2024-12-16 10:00:00',
    createdAt: '2024-12-15 18:00:00'
  },
  {
    id: 2,
    userId: 1,
    userName: 'testuser',
    userAvatar: randomAvatar(),
    merchantId: 2,
    merchantName: '爱宠之家旗舰店',
    serviceId: 4,
    serviceName: '宠物SPA',
    appointmentId: 3,
    rating: 4,
    comment: 'SPA体验不错，环境很舒适，就是价格有点贵。',
    images: [],
    reply: null,
    replyTime: null,
    createdAt: '2024-12-15 20:00:00'
  },
  {
    id: 3,
    userId: 2,
    userName: 'petlover',
    userAvatar: randomAvatar(),
    merchantId: 1,
    merchantName: '宠物乐园1号店',
    serviceId: 2,
    serviceName: '宠物美容',
    appointmentId: 2,
    rating: 5,
    comment: '美容师技术很好，造型很满意！',
    images: [randomImage(200, 200)],
    reply: '谢谢您的认可，我们会继续努力！',
    replyTime: '2024-12-17 09:00:00',
    createdAt: '2024-12-16 15:00:00'
  }
]

export const mockCartItems: MockCartItem[] = [
  {
    id: 1,
    userId: 1,
    productId: 1,
    productName: '优质狗粮',
    productImage: randomImage(100, 100),
    price: 128,
    quantity: 2,
    merchantId: 1,
    merchantName: '宠物乐园1号店',
    selected: true,
    createdAt: '2024-12-18 10:00:00'
  },
  {
    id: 2,
    userId: 1,
    productId: 3,
    productName: '宠物玩具球',
    productImage: randomImage(100, 100),
    price: 28,
    quantity: 3,
    merchantId: 1,
    merchantName: '宠物乐园1号店',
    selected: true,
    createdAt: '2024-12-19 14:00:00'
  },
  {
    id: 3,
    userId: 1,
    productId: 6,
    productName: '智能宠物喂食器',
    productImage: randomImage(100, 100),
    price: 298,
    quantity: 1,
    merchantId: 3,
    merchantName: '萌宠世界体验店',
    selected: false,
    createdAt: '2024-12-20 09:00:00'
  }
]

export const mockFavorites: MockFavorite[] = [
  {
    id: 1,
    userId: 1,
    merchantId: 1,
    merchantName: '宠物乐园1号店',
    merchantLogo: randomImage(100, 100),
    merchantAddress: '北京市朝阳区建国路88号',
    merchantPhone: '13800138001',
    merchantRating: 4.8,
    createdAt: '2024-12-01 10:00:00'
  },
  {
    id: 2,
    userId: 1,
    merchantId: 2,
    merchantName: '爱宠之家旗舰店',
    merchantLogo: randomImage(100, 100),
    merchantAddress: '上海市浦东新区南京路100号',
    merchantPhone: '13800138002',
    merchantRating: 4.6,
    createdAt: '2024-12-05 14:00:00'
  },
  {
    id: 3,
    userId: 2,
    merchantId: 3,
    merchantName: '萌宠世界体验店',
    merchantLogo: randomImage(100, 100),
    merchantAddress: '广东省深圳市南山区科技园路50号',
    merchantPhone: '13800138003',
    merchantRating: 4.9,
    createdAt: '2024-12-10 09:00:00'
  }
]

export const mockNotifications: MockNotification[] = [
  {
    id: 1,
    userId: 1,
    title: '预约确认通知',
    content: '您预约的"宠物洗澡"服务已确认，请准时到店。',
    type: 'appointment',
    isRead: false,
    createdAt: '2024-12-18 14:30:00'
  },
  {
    id: 2,
    userId: 1,
    title: '订单发货通知',
    content: '您的订单已发货，快递单号：SF1234567890',
    type: 'order',
    isRead: true,
    createdAt: '2024-12-19 16:00:00'
  },
  {
    id: 3,
    userId: 1,
    title: '系统维护通知',
    content: '系统将于今晚22:00-23:00进行维护升级，届时服务可能受影响。',
    type: 'system',
    isRead: false,
    createdAt: '2024-12-20 09:00:00'
  },
  {
    id: 4,
    userId: 1,
    title: '评价回复通知',
    content: '商家已回复您的评价，点击查看详情。',
    type: 'review',
    isRead: true,
    createdAt: '2024-12-16 10:00:00'
  }
]

export const mockAnnouncements: MockAnnouncement[] = [
  {
    id: 1,
    title: '平台春节放假通知',
    content: '尊敬的用户，春节期间（2025年1月28日-2月4日）平台将暂停服务，请提前安排您的预约和订单。祝您新春快乐！',
    summary: '春节期间平台暂停服务通知',
    author: '平台管理员',
    status: 'published',
    isRead: false,
    publishedAt: '2024-12-15 10:00:00',
    createdAt: '2024-12-15 10:00:00'
  },
  {
    id: 2,
    title: '新功能上线：在线支付',
    content: '我们很高兴地宣布，平台现已支持微信支付和支付宝支付，让您的支付更加便捷安全。',
    summary: '平台支持在线支付功能',
    author: '平台管理员',
    status: 'published',
    isRead: true,
    publishedAt: '2024-12-10 14:00:00',
    createdAt: '2024-12-10 14:00:00'
  },
  {
    id: 3,
    title: '双十二活动：全场8折',
    content: '双十二活动期间（12月10日-12月12日），全场服务享受8折优惠，快来预约吧！',
    summary: '双十二活动优惠通知',
    author: '平台管理员',
    status: 'published',
    isRead: true,
    publishedAt: '2024-12-08 09:00:00',
    createdAt: '2024-12-08 09:00:00'
  }
]

export const mockDashboardStats: MockDashboardStats = {
  petCount: 3,
  pendingAppointmentCount: 2,
  reviewCount: 2,
  favoriteCount: 2
}

export const mockRecentActivities: MockRecentActivity[] = [
  {
    id: 1,
    type: 'appointment',
    title: '预约已确认',
    description: '您预约的"宠物洗澡"服务已确认',
    time: '2024-12-18 14:30:00'
  },
  {
    id: 2,
    type: 'order',
    title: '订单已发货',
    description: '订单ORD202412190001已发货',
    time: '2024-12-19 16:00:00'
  },
  {
    id: 3,
    type: 'review',
    title: '评价已回复',
    description: '商家回复了您的评价',
    time: '2024-12-16 10:00:00'
  }
]

export const generateMockReview = (userId: number): MockReview => {
  const user = mockUsers.find(u => u.id === userId) || mockUsers[0]
  const merchant = mockMerchants[Math.floor(Math.random() * mockMerchants.length)]
  const service = mockServices.filter(s => s.merchantId === merchant.id)[0] || mockServices[0]
  
  return {
    id: generateNumericId(),
    userId,
    userName: user.username,
    userAvatar: user.avatar,
    merchantId: merchant.id,
    merchantName: merchant.name,
    serviceId: service.id,
    serviceName: service.name,
    appointmentId: generateNumericId(),
    rating: randomRating(),
    comment: '服务很好，推荐！',
    images: randomBoolean(0.3) ? [randomImage(200, 200)] : [],
    reply: null,
    replyTime: null,
    createdAt: generateDate()
  }
}

export const generateMockCartItem = (userId: number): MockCartItem => {
  const product = mockProducts[Math.floor(Math.random() * mockProducts.length)]
  const merchant = mockMerchants.find(m => m.id === product.merchantId) || mockMerchants[0]
  
  return {
    id: generateNumericId(),
    userId,
    productId: product.id,
    productName: product.name,
    productImage: product.image,
    price: product.price,
    quantity: generateNumericId(1, 5),
    merchantId: merchant.id,
    merchantName: merchant.name,
    selected: randomBoolean(),
    createdAt: generateDate()
  }
}

export const generateMockFavorite = (userId: number): MockFavorite => {
  const merchant = mockMerchants[Math.floor(Math.random() * mockMerchants.length)]
  
  return {
    id: generateNumericId(),
    userId,
    merchantId: merchant.id,
    merchantName: merchant.name,
    merchantLogo: merchant.logo,
    merchantAddress: merchant.address,
    merchantPhone: merchant.phone,
    merchantRating: merchant.rating,
    createdAt: generateDate()
  }
}

export const generateMockNotification = (userId: number): MockNotification => {
  const types: MockNotification['type'][] = ['system', 'order', 'appointment', 'review']
  const type = randomEnum(types)
  
  const titles: Record<MockNotification['type'], string> = {
    system: '系统通知',
    order: '订单通知',
    appointment: '预约通知',
    review: '评价通知'
  }
  
  return {
    id: generateNumericId(),
    userId,
    title: titles[type],
    content: '这是一条通知消息',
    type,
    isRead: randomBoolean(),
    createdAt: generateDate()
  }
}
