import { randomId, randomDate, randomEnum } from '../../utils/random'

export type TargetType = 'service' | 'product'

export interface Review {
  id: number
  user_id: number
  target_id: number
  target_type: TargetType
  merchant_id: number
  rating: number
  comment: string
  images: string[]
  reply?: string
  reply_at?: string
  created_at: string
  updated_at: string
}

const serviceNames = [
  '宠物美容',
  '宠物洗澡',
  '宠物寄养',
  '宠物医疗',
  '宠物训练',
  '宠物SPA'
]

const productNames = [
  '皇家狗粮',
  '猫砂盆',
  '宠物玩具',
  '宠物牵引绳',
  '宠物窝',
  '宠物零食'
]

const merchantNames = [
  '宠物乐园',
  '萌宠之家',
  '爱宠天地',
  '宠物世界',
  '毛孩子宠物店'
]

const comments = [
  '服务非常好，店员很专业，宠物也很喜欢！',
  '环境干净整洁，服务态度很好，下次还会再来。',
  '价格合理，服务周到，推荐给大家！',
  '一般般吧，没有想象中那么好。',
  '服务态度有待提高，但整体还可以。',
  '非常满意！店员很细心，宠物洗完澡香香的。',
  '性价比很高，服务也很专业。',
  '环境很好，服务也很到位，宠物很喜欢这里。',
  '体验不错，下次还会选择这家店。',
  '服务一般，价格偏贵，不太推荐。',
  '店员很热情，服务也很专业，好评！',
  '宠物在这里很开心，服务也很好。'
]

const generateReviews = (count = 15): Review[] => {
  const reviews: Review[] = []
  const targetTypes: TargetType[] = ['service', 'product']

  for (let i = 0; i < count; i++) {
    const targetType = randomEnum(targetTypes)
    const hasReply = Math.random() > 0.5
    const hasImages = Math.random() > 0.6

    reviews.push({
      id: randomId(),
      user_id: randomId(1, 100),
      target_id: randomId(1, 50),
      target_type: targetType,
      merchant_id: randomId(1, 10),
      rating: randomEnum([1, 2, 3, 4, 5]),
      comment: randomEnum(comments),
      images: hasImages
        ? [
            `https://api.dicebear.com/7.x/shapes/svg?seed=review${i}a`,
            `https://api.dicebear.com/7.x/shapes/svg?seed=review${i}b`
          ]
        : [],
      reply: hasReply ? '感谢您的好评，我们会继续努力！' : undefined,
      reply_at: hasReply ? randomDate('2024-01-01') : undefined,
      created_at: randomDate('2024-01-01'),
      updated_at: randomDate('2024-01-01')
    })
  }
  return reviews
}

export const reviewsData = generateReviews(15)

export const getTargetName = (targetType: TargetType, targetId: number): string => {
  if (targetType === 'service') {
    return serviceNames[targetId % serviceNames.length]
  }
  return productNames[targetId % productNames.length]
}

export const getMerchantName = (merchantId: number): string => {
  return merchantNames[merchantId % merchantNames.length]
}

export const reviewStats = {
  average_rating: 4.2,
  total_reviews: 156,
  rating_distribution: {
    5: 68,
    4: 45,
    3: 25,
    2: 12,
    1: 6
  }
}
