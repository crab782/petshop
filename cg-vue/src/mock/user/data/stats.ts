export interface UserStats {
  petCount: number
  pendingAppointments: number
  pendingOrders: number
  favoriteCount: number
  reviewCount: number
  totalSpent: number
  memberDays: number
  level: string
  points: number
}

export interface Recommendation {
  id: number
  type: 'service' | 'product'
  name: string
  image: string
  price: number
  rating: number
  sales: number
  merchantName: string
  merchantId: number
  category: string
  reason: string
}

export interface ActivityItem {
  id: number
  type: 'appointment' | 'order' | 'review' | 'favorite'
  title: string
  description: string
  time: string
  status?: string
  amount?: number
}

export const userStats: UserStats = {
  petCount: 5,
  pendingAppointments: 2,
  pendingOrders: 1,
  favoriteCount: 8,
  reviewCount: 12,
  totalSpent: 3580.00,
  memberDays: 365,
  level: '黄金会员',
  points: 2580
}

export const recommendations: Recommendation[] = [
  {
    id: 1,
    type: 'service',
    name: '精致洗护套餐',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20grooming%20professional%20clean&image_size=square',
    price: 128.00,
    rating: 4.9,
    sales: 156,
    merchantName: '宠物乐园',
    merchantId: 1,
    category: '宠物美容',
    reason: '好评如潮'
  },
  {
    id: 2,
    type: 'service',
    name: '宠物SPA护理',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20spa%20relaxing%20warm&image_size=square',
    price: 198.00,
    rating: 4.8,
    sales: 89,
    merchantName: '萌宠之家',
    merchantId: 2,
    category: '宠物SPA',
    reason: '本周热门'
  },
  {
    id: 3,
    type: 'product',
    name: '皇家猫粮 成猫通用型 2kg',
    image: 'https://picsum.photos/400/400?random=1',
    price: 128.00,
    rating: 4.8,
    sales: 2341,
    merchantName: '宠物乐园',
    merchantId: 1,
    category: '宠物食品',
    reason: '销量冠军'
  },
  {
    id: 4,
    type: 'service',
    name: '宠物训练课程',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20training%20outdoor%20happy&image_size=square',
    price: 299.00,
    rating: 4.7,
    sales: 67,
    merchantName: '爱宠世界',
    merchantId: 3,
    category: '宠物训练',
    reason: '专业认证'
  },
  {
    id: 5,
    type: 'product',
    name: '宠物益生菌 肠道调理 30包装',
    image: 'https://picsum.photos/400/400?random=10',
    price: 128.00,
    rating: 4.9,
    sales: 1876,
    merchantName: '宠物乐园',
    merchantId: 1,
    category: '宠物保健',
    reason: '好评推荐'
  },
  {
    id: 6,
    type: 'service',
    name: '全面健康体检',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20medical%20care%20professional&image_size=square',
    price: 258.00,
    rating: 4.9,
    sales: 234,
    merchantName: '宠物乐园',
    merchantId: 1,
    category: '宠物体检',
    reason: '健康首选'
  },
  {
    id: 7,
    type: 'product',
    name: '宠物自动喂食器 定时投喂 5L',
    image: 'https://picsum.photos/400/400?random=4',
    price: 299.00,
    rating: 4.7,
    sales: 1234,
    merchantName: '萌宠之家',
    merchantId: 2,
    category: '宠物用品',
    reason: '智能便捷'
  },
  {
    id: 8,
    type: 'service',
    name: '温馨寄养服务',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20boarding%20comfortable%20cozy&image_size=square',
    price: 88.00,
    rating: 4.6,
    sales: 178,
    merchantName: '萌宠之家',
    merchantId: 2,
    category: '宠物寄养',
    reason: '安心之选'
  }
]

export const recentActivities: ActivityItem[] = [
  {
    id: 1,
    type: 'appointment',
    title: '预约服务',
    description: '宠物美容 - 宠物乐园',
    time: '2025-04-19 10:00',
    status: 'confirmed',
    amount: 168.00
  },
  {
    id: 2,
    type: 'order',
    title: '商品订单',
    description: '猫粮、猫砂已发货',
    time: '2025-04-18 15:30',
    status: 'shipped',
    amount: 226.00
  },
  {
    id: 3,
    type: 'review',
    title: '发表评价',
    description: '对"宠物洗澡"服务进行了评价',
    time: '2025-04-17 16:45',
    status: 'completed'
  },
  {
    id: 4,
    type: 'favorite',
    title: '收藏商家',
    description: '收藏了"爱宠世界"',
    time: '2025-04-16 14:20'
  },
  {
    id: 5,
    type: 'appointment',
    title: '预约完成',
    description: '宠物体检 - 萌宠之家',
    time: '2025-04-15 09:00',
    status: 'completed',
    amount: 258.00
  }
]

export const getUserStats = (userId: number): UserStats => {
  return userStats
}

export const getRecommendations = (limit: number = 6, type?: 'service' | 'product'): Recommendation[] => {
  let filtered = [...recommendations]
  if (type) {
    filtered = filtered.filter(r => r.type === type)
  }
  return filtered
    .sort((a, b) => b.rating * b.sales - a.rating * a.sales)
    .slice(0, limit)
}

export const getRecentActivities = (userId: number, limit: number = 5): ActivityItem[] => {
  return recentActivities.slice(0, limit)
}
