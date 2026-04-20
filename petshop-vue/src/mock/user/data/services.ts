import { randomId, randomDate, randomEnum, randomString } from '../../utils/random'
import { merchants } from './merchants'

export interface Service {
  id: number
  name: string
  description: string
  price: number
  duration: number
  image: string
  merchantId: number
  merchantName: string
  rating: number
  reviewCount: number
  category: string
  status: 'enabled' | 'disabled'
  createdAt: string
  updatedAt: string
}

const ServiceCategories = [
  '宠物美容',
  '宠物寄养',
  '宠物医疗',
  '宠物训练',
  '宠物摄影',
  '宠物接送',
  '宠物SPA',
  '宠物体检'
]

const ServiceNames: Record<string, string[]> = {
  '宠物美容': ['精致洗护套餐', '造型修剪', '毛发护理', '指甲修剪', '耳道清洁'],
  '宠物寄养': ['日间寄养', '短期寄养', '长期寄养', '豪华套房寄养', 'VIP寄养'],
  '宠物医疗': ['常规体检', '疫苗接种', '驱虫服务', '健康咨询', '急诊服务'],
  '宠物训练': ['基础训练', '行为矫正', '技能培训', '社会化训练', '一对一私教'],
  '宠物摄影': ['写真拍摄', '亲子照', '节日主题', '户外拍摄', '视频制作'],
  '宠物接送': ['机场接送', '医院接送', '日常接送', '跨城托运', '专车服务'],
  '宠物SPA': ['舒缓SPA', '药浴SPA', '香薰SPA', '按摩护理', '深层清洁'],
  '宠物体检': ['基础体检', '全面体检', '老年体检', '幼宠体检', '专项检查']
}

const ServiceImages = [
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20grooming%20professional%20clean&image_size=square',
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20spa%20relaxing%20warm&image_size=square',
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20training%20outdoor%20happy&image_size=square',
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20medical%20care%20professional&image_size=square',
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20boarding%20comfortable%20cozy&image_size=square',
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20photography%20cute%20beautiful&image_size=square'
]

const ServiceDescriptions: Record<string, string[]> = {
  '宠物美容': [
    '专业宠物美容师，为您的爱宠打造完美造型',
    '使用进口洗护产品，温和不刺激',
    '包含洗澡、吹干、修剪、造型全套服务'
  ],
  '宠物寄养': [
    '宽敞舒适的寄养环境，24小时专人看护',
    '提供独立空间，保证宠物隐私和安全',
    '每日定时遛弯，保证宠物运动量'
  ],
  '宠物医疗': [
    '资深兽医团队，先进医疗设备',
    '提供全方位健康检查和治疗服务',
    '预约制服务，减少等待时间'
  ],
  '宠物训练': [
    '专业训犬师一对一指导',
    '科学训练方法，效果显著',
    '根据宠物性格定制训练方案'
  ],
  '宠物摄影': [
    '专业宠物摄影师，捕捉精彩瞬间',
    '提供多套服装道具选择',
    '精修照片，永久保存美好回忆'
  ],
  '宠物接送': [
    '专车接送，安全准时',
    '配备专业宠物运输笼',
    '全程GPS定位，实时追踪'
  ],
  '宠物SPA': [
    '舒缓放松，缓解宠物压力',
    '使用天然植物精油，温和护理',
    '改善毛发质量，促进血液循环'
  ],
  '宠物体检': [
    '全面健康检查，及早发现问题',
    '专业医疗设备，检测精准',
    '提供详细体检报告和健康建议'
  ]
}

export const generateServices = (count = 20): Service[] => {
  const services: Service[] = []
  let serviceId = 1

  merchants.forEach(merchant => {
    const serviceCount = Math.floor(Math.random() * 4) + 2
    const usedCategories = new Set<string>()

    for (let i = 0; i < serviceCount && serviceId <= count; i++) {
      let category = randomEnum(ServiceCategories)
      let attempts = 0
      while (usedCategories.has(category) && attempts < 10) {
        category = randomEnum(ServiceCategories)
        attempts++
      }
      usedCategories.add(category)

      const names = ServiceNames[category] || [category + '服务']
      const name = randomEnum(names)
      const descriptions = ServiceDescriptions[category] || ['专业服务']
      const description = randomEnum(descriptions)

      const basePrice = {
        '宠物美容': 100,
        '宠物寄养': 80,
        '宠物医疗': 150,
        '宠物训练': 200,
        '宠物摄影': 300,
        '宠物接送': 50,
        '宠物SPA': 180,
        '宠物体检': 120
      }[category] || 100

      const createdAt = randomDate('2023-01-01', '2024-12-31')

      services.push({
        id: serviceId,
        name,
        description,
        price: Math.round((basePrice + Math.random() * basePrice * 0.5) * 100) / 100,
        duration: randomEnum([30, 45, 60, 90, 120, 180]),
        image: randomEnum(ServiceImages),
        merchantId: merchant.id,
        merchantName: merchant.name,
        rating: Math.round((3.5 + Math.random() * 1.5) * 10) / 10,
        reviewCount: Math.floor(Math.random() * 200) + 10,
        category,
        status: 'enabled',
        createdAt,
        updatedAt: randomDate(createdAt.split(' ')[0], '2025-04-19')
      })

      serviceId++
    }
  })

  while (services.length < count) {
    const merchant = randomEnum(merchants)
    const category = randomEnum(ServiceCategories)
    const names = ServiceNames[category] || [category + '服务']
    const name = randomEnum(names)
    const descriptions = ServiceDescriptions[category] || ['专业服务']
    const description = randomEnum(descriptions)

    const basePrice = {
      '宠物美容': 100,
      '宠物寄养': 80,
      '宠物医疗': 150,
      '宠物训练': 200,
      '宠物摄影': 300,
      '宠物接送': 50,
      '宠物SPA': 180,
      '宠物体检': 120
    }[category] || 100

    const createdAt = randomDate('2023-01-01', '2024-12-31')

    services.push({
      id: serviceId,
      name,
      description,
      price: Math.round((basePrice + Math.random() * basePrice * 0.5) * 100) / 100,
      duration: randomEnum([30, 45, 60, 90, 120, 180]),
      image: randomEnum(ServiceImages),
      merchantId: merchant.id,
      merchantName: merchant.name,
      rating: Math.round((3.5 + Math.random() * 1.5) * 10) / 10,
      reviewCount: Math.floor(Math.random() * 200) + 10,
      category,
      status: 'enabled',
      createdAt,
      updatedAt: randomDate(createdAt.split(' ')[0], '2025-04-19')
    })

    serviceId++
  }

  return services
}

export const services: Service[] = generateServices(25)

export const getServiceById = (id: number): Service | undefined => {
  return services.find(s => s.id === id)
}

export const getServicesByMerchantId = (merchantId: number): Service[] => {
  return services.filter(s => s.merchantId === merchantId)
}

export const getServicesByIds = (ids: number[]): Service[] => {
  return services.filter(s => ids.includes(s.id))
}
