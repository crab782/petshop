import { randomId, randomDate, randomEnum, randomString, randomPhone, randomEmail } from '../../utils/random'

export interface Merchant {
  id: number
  name: string
  logo: string
  address: string
  rating: number
  serviceCount: number
  phone: string
  email: string
  description: string
  contactPerson: string
  status: 'approved' | 'pending' | 'rejected' | 'disabled'
  createdAt: string
  updatedAt: string
}

const MerchantNames = [
  '萌宠乐园',
  '爱宠之家',
  '宠物天地',
  '毛孩子宠物店',
  '快乐宠物馆',
  '宠物世界',
  '宠爱一生',
  '小动物之家',
  '宠物驿站',
  '爱心宠物店',
  '宠物小镇',
  '萌宠工坊',
  '宠物之家',
  '宠爱有家',
  '宠物乐园'
]

const Addresses = [
  '北京市朝阳区建国路88号',
  '上海市浦东新区张江高科技园区',
  '广州市天河区珠江新城',
  '深圳市南山区科技园',
  '杭州市西湖区文三路',
  '成都市武侯区天府大道',
  '南京市鼓楼区中山路',
  '武汉市江汉区解放大道',
  '西安市雁塔区高新路',
  '重庆市渝中区解放碑'
]

const Descriptions = [
  '专业宠物美容护理，提供洗澡、修剪、造型等全方位服务',
  '温馨舒适的宠物寄养环境，让您的爱宠享受家的温暖',
  '资深兽医团队，提供专业宠物医疗体检服务',
  '优质宠物用品，进口粮、国产粮应有尽有',
  '宠物训练专家，让您的爱宠更乖巧听话',
  '24小时宠物急诊，随时守护爱宠健康',
  '宠物SPA护理，让爱宠焕发光彩',
  '宠物摄影服务，记录爱宠美好瞬间',
  '宠物接送服务，安全便捷',
  '专业宠物行为矫正，解决您的烦恼'
]

const Logos = [
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20shop%20logo%20colorful%20friendly%20cute&image_size=square',
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=dog%20cat%20logo%20cartoon%20happy&image_size=square',
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20store%20logo%20modern%20clean&image_size=square',
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=animal%20clinic%20logo%20professional&image_size=square',
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20grooming%20logo%20soft%20colors&image_size=square'
]

export const generateMerchants = (count = 10): Merchant[] => {
  const merchants: Merchant[] = []
  const usedNames = new Set<string>()

  for (let i = 0; i < count; i++) {
    let name = randomEnum(MerchantNames)
    while (usedNames.has(name)) {
      name = randomEnum(MerchantNames) + randomString(2)
    }
    usedNames.add(name)

    const createdAt = randomDate('2022-01-01', '2024-12-31')

    merchants.push({
      id: i + 1,
      name,
      logo: randomEnum(Logos),
      address: randomEnum(Addresses),
      rating: Math.round((3.5 + Math.random() * 1.5) * 10) / 10,
      serviceCount: Math.floor(Math.random() * 15) + 3,
      phone: randomPhone(),
      email: randomEmail(),
      description: randomEnum(Descriptions),
      contactPerson: randomString(3) + '先生/女士',
      status: 'approved',
      createdAt,
      updatedAt: randomDate(createdAt.split(' ')[0], '2025-04-19')
    })
  }
  return merchants
}

export const merchants: Merchant[] = generateMerchants(15)

export const getMerchantById = (id: number): Merchant | undefined => {
  return merchants.find(m => m.id === id)
}

export const getMerchantsByIds = (ids: number[]): Merchant[] => {
  return merchants.filter(m => ids.includes(m.id))
}
