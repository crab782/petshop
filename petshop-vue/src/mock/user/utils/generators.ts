import Mock from 'mockjs'

export type IDGenerator = (prefix?: string) => string
export type DateRange = { start?: string; end?: string }
export type WeightedOption<T> = { value: T; weight: number }

export interface IDGeneratorOptions {
  prefix?: string
  length?: number
  type?: 'numeric' | 'alphanumeric' | 'uuid' | 'timestamp'
}

export interface DateGeneratorOptions {
  start?: string
  end?: string
  format?: 'datetime' | 'date' | 'time' | 'timestamp'
  includeTime?: boolean
}

export interface CityInfo {
  province: string
  city: string
  districts: string[]
}

export interface MerchantData {
  id: number
  name: string
  logo: string
  contactPerson: string
  phone: string
  email: string
  address: string
  description: string
  rating: number
  reviewCount: number
  serviceCount: number
  productCount: number
  status: 'pending' | 'approved' | 'rejected'
  createdAt: string
  updatedAt: string
}

export interface ServiceData {
  id: number
  merchantId: number
  merchantName: string
  name: string
  description: string
  price: number
  duration: number
  image: string
  category: string
  status: 'enabled' | 'disabled'
  rating: number
  reviewCount: number
  createdAt: string
  updatedAt: string
}

export interface ProductData {
  id: number
  merchantId: number
  merchantName: string
  name: string
  description: string
  price: number
  stock: number
  image: string
  category: string
  status: 'enabled' | 'disabled'
  sales: number
  rating: number
  reviewCount: number
  createdAt: string
  updatedAt: string
}

export interface AppointmentData {
  id: number
  userId: number
  userName: string
  merchantId: number
  merchantName: string
  serviceId: number
  serviceName: string
  petId: number
  petName: string
  appointmentTime: string
  totalPrice: number
  status: 'pending' | 'confirmed' | 'completed' | 'cancelled'
  notes: string
  createdAt: string
  updatedAt: string
}

export interface OrderData {
  id: number
  orderNo: string
  userId: number
  userName: string
  merchantId: number
  merchantName: string
  totalPrice: number
  freight: number
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'
  payMethod: string
  payTime: string | null
  shipTime: string | null
  completeTime: string | null
  cancelTime: string | null
  remark: string
  shippingAddress: {
    name: string
    phone: string
    province: string
    city: string
    district: string
    detailAddress: string
  }
  items: Array<{
    id: number
    productId: number
    productName: string
    productImage: string
    price: number
    quantity: number
    subtotal: number
  }>
  createdAt: string
  updatedAt: string
}

export interface UserData {
  id: number
  username: string
  email: string
  phone: string
  avatar: string
  createdAt: string
  updatedAt: string
}

export interface PetData {
  id: number
  userId: number
  name: string
  type: string
  breed: string
  age: number
  gender: string
  avatar: string
  description: string
  weight?: number
  bodyType?: string
  furColor?: string
  personality?: string
  createdAt: string
  updatedAt: string
}

export interface ReviewData {
  id: number
  userId: number
  userName: string
  userAvatar: string
  merchantId: number
  merchantName: string
  serviceId: number
  serviceName: string
  appointmentId: number
  rating: number
  comment: string
  images: string[]
  reply: string | null
  replyTime: string | null
  createdAt: string
}

const CHINESE_SURNAMES = [
  '王', '李', '张', '刘', '陈', '杨', '黄', '赵', '周', '吴',
  '徐', '孙', '马', '朱', '胡', '郭', '何', '高', '林', '罗',
  '郑', '梁', '谢', '宋', '唐', '许', '韩', '冯', '邓', '曹',
  '彭', '曾', '萧', '田', '董', '袁', '潘', '于', '蒋', '蔡',
  '余', '杜', '叶', '程', '苏', '魏', '吕', '丁', '任', '沈'
]

const CHINESE_GIVEN_NAMES_MALE = [
  '伟', '强', '磊', '洋', '勇', '军', '杰', '涛', '明', '辉',
  '鹏', '华', '飞', '刚', '波', '斌', '宇', '浩', '凯', '俊',
  '峰', '龙', '威', '志', '亮', '健', '林', '斌', '建', '国'
]

const CHINESE_GIVEN_NAMES_FEMALE = [
  '芳', '娜', '敏', '静', '丽', '秀', '娟', '英', '华', '慧',
  '婷', '琳', '玲', '艳', '萍', '红', '梅', '霞', '燕', '洁',
  '雪', '莉', '倩', '颖', '欣', '雅', '蕾', '薇', '莹', '璐'
]

const CHINESE_CITIES: CityInfo[] = [
  {
    province: '北京市',
    city: '北京市',
    districts: ['东城区', '西城区', '朝阳区', '海淀区', '丰台区', '石景山区', '通州区', '顺义区']
  },
  {
    province: '上海市',
    city: '上海市',
    districts: ['黄浦区', '徐汇区', '长宁区', '静安区', '普陀区', '虹口区', '杨浦区', '浦东新区']
  },
  {
    province: '广东省',
    city: '广州市',
    districts: ['越秀区', '海珠区', '荔湾区', '天河区', '白云区', '黄埔区', '番禺区', '花都区']
  },
  {
    province: '广东省',
    city: '深圳市',
    districts: ['罗湖区', '福田区', '南山区', '宝安区', '龙岗区', '盐田区', '龙华区', '坪山区']
  },
  {
    province: '浙江省',
    city: '杭州市',
    districts: ['上城区', '下城区', '江干区', '拱墅区', '西湖区', '滨江区', '萧山区', '余杭区']
  },
  {
    province: '四川省',
    city: '成都市',
    districts: ['锦江区', '青羊区', '金牛区', '武侯区', '成华区', '龙泉驿区', '青白江区', '新都区']
  },
  {
    province: '江苏省',
    city: '南京市',
    districts: ['玄武区', '秦淮区', '建邺区', '鼓楼区', '浦口区', '栖霞区', '雨花台区', '江宁区']
  },
  {
    province: '湖北省',
    city: '武汉市',
    districts: ['江岸区', '江汉区', '硚口区', '汉阳区', '武昌区', '青山区', '洪山区', '东西湖区']
  },
  {
    province: '陕西省',
    city: '西安市',
    districts: ['新城区', '碑林区', '莲湖区', '灞桥区', '未央区', '雁塔区', '阎良区', '临潼区']
  },
  {
    province: '重庆市',
    city: '重庆市',
    districts: ['渝中区', '大渡口区', '江北区', '沙坪坝区', '九龙坡区', '南岸区', '北碚区', '渝北区']
  }
]

const STREETS = [
  '人民路', '中山路', '解放路', '建设路', '和平路', '胜利路', '长安街', '建国路',
  '复兴路', '朝阳路', '海淀路', '中关村大街', '南京路', '淮海路', '深南大道',
  '天府大道', '汉江路', '高新路', '解放碑步行街', '观音桥步行街'
]

const SERVICE_DESCRIPTIONS: Record<string, string[]> = {
  '宠物美容': [
    '专业宠物美容师团队，为您的爱宠打造完美造型。使用进口洗护产品，温和不刺激，让宠物享受SPA级护理体验。',
    '提供全方位美容服务，包括洗澡、修剪、造型、指甲修剪、耳道清洁等。让您的爱宠焕发光彩，成为街头最靓的仔。',
    '采用国际标准美容流程，专业设备配合温和手法，确保宠物在舒适的环境中完成美容护理。'
  ],
  '宠物寄养': [
    '宽敞舒适的寄养环境，24小时专人看护。独立空间保证宠物隐私和安全，每日定时遛弯，保证宠物运动量。',
    '豪华套房寄养服务，配备空调、监控、自动喂食器。专业宠物护理师全程照料，让您安心出行。',
    '温馨如家的寄养体验，提供个性化护理方案。定期发送宠物视频，让您随时了解爱宠动态。'
  ],
  '宠物医疗': [
    '资深兽医团队，先进医疗设备。提供全方位健康检查和治疗服务，预约制服务减少等待时间。',
    '24小时急诊服务，随时守护爱宠健康。配备专业手术室和ICU病房，为重症宠物提供最佳治疗方案。',
    '预防医学专家，提供疫苗接种、驱虫、体检等常规医疗服务。建立宠物健康档案，追踪健康状况。'
  ],
  '宠物训练': [
    '专业训犬师一对一指导，科学训练方法效果显著。根据宠物性格定制训练方案，让您的爱宠更乖巧听话。',
    '基础服从训练、行为矫正、技能培训、社会化训练等多种课程可选。小班教学，保证训练质量。',
    '采用正向强化训练法，不使用任何暴力手段。让宠物在快乐中学习，建立良好的行为习惯。'
  ],
  '宠物摄影': [
    '专业宠物摄影师，捕捉精彩瞬间。提供多套服装道具选择，精修照片永久保存美好回忆。',
    '写真拍摄、亲子照、节日主题、户外拍摄等多种风格可选。专业灯光和布景，打造明星级大片。',
    '上门拍摄服务，在宠物熟悉的环境中拍摄，捕捉最自然的状态。提供精修照片和精美相册。'
  ],
  '宠物接送': [
    '专车接送服务，安全准时。配备专业宠物运输笼，全程GPS定位实时追踪，让您随时了解行程。',
    '机场接送、医院接送、日常接送、跨城托运等多种服务。专业司机熟悉宠物习性，确保运输安全。',
    '24小时预约服务，随叫随到。车内配备空调和饮水设施，让宠物在舒适的环境中完成旅程。'
  ],
  '宠物SPA': [
    '舒缓SPA护理，缓解宠物压力。使用天然植物精油温和护理，改善毛发质量促进血液循环。',
    '药浴SPA针对皮肤问题，香薰SPA放松身心。专业按摩手法，让宠物享受极致放松体验。',
    '深层清洁护理，去除死毛和皮屑。配合营养护毛素，让毛发更加柔顺光亮。'
  ],
  '宠物体检': [
    '全面健康检查，及早发现问题。专业医疗设备检测精准，提供详细体检报告和健康建议。',
    '基础体检、全面体检、老年体检、幼宠体检等多种套餐可选。建立健康档案，追踪健康变化。',
    '专项检查服务，包括心脏检查、骨科检查、眼科检查等。专家会诊，提供专业诊疗方案。'
  ]
}

const PRODUCT_DESCRIPTIONS: Record<string, string[]> = {
  '宠物食品': [
    '精选优质原料，科学营养配方。富含优质蛋白质和必需营养素，帮助维持宠物健康体态和亮丽毛发。',
    '进口原料，无添加防腐剂。营养均衡易消化，适合各年龄段宠物食用。',
    '专业兽医推荐配方，满足宠物日常营养需求。美味可口，挑食宠物也爱吃。'
  ],
  '宠物用品': [
    '环保材质，安全无毒。人性化设计，使用方便，让养宠生活更加轻松。',
    '高品质材料制作，耐用持久。精工细作，每一个细节都经过严格把控。',
    '创新设计，解决养宠难题。实用性强，性价比高，深受宠物主人喜爱。'
  ],
  '宠物玩具': [
    '安全材质，耐咬耐磨。互动性强，增进主人与宠物的感情，让宠物快乐玩耍。',
    '益智玩具，锻炼宠物智力。多种玩法，保持宠物新鲜感，避免无聊破坏。',
    '户外运动玩具，增强宠物体质。轻便易携带，随时随地享受互动乐趣。'
  ],
  '宠物服饰': [
    '柔软舒适面料，不伤皮肤。时尚设计，让宠物成为街头焦点。',
    '四季款式齐全，保暖透气。精细做工，穿着舒适不束缚活动。',
    '多种尺码可选，适合不同体型宠物。易穿脱设计，方便日常更换。'
  ],
  '宠物保健': [
    '高纯度营养成分，科学配比。帮助宠物补充日常所需营养，增强免疫力。',
    '天然植物提取，无副作用。改善宠物常见健康问题，呵护宠物健康。',
    '专业兽医研发配方，安全有效。日常保健必备，预防胜于治疗。'
  ]
}

const REVIEW_COMMENTS: Record<number, string[]> = {
  5: [
    '非常满意！服务态度超级好，宠物也很喜欢。环境干净整洁，价格也很合理。强烈推荐！',
    '体验非常棒！工作人员很专业，对宠物很有爱心。下次还会再来！',
    '超出预期！服务细致周到，宠物回来后状态很好。五星好评！',
    '非常专业的服务，价格实惠。环境舒适，工作人员很有耐心。一定会推荐给朋友！'
  ],
  4: [
    '整体体验不错，服务态度很好。就是等待时间稍微有点长，希望可以改进。',
    '服务还可以，价格合理。环境干净，工作人员态度友善。',
    '基本满意，服务专业。建议增加更多优惠活动。',
    '体验不错，宠物很喜欢。下次会继续选择这家店。'
  ],
  3: [
    '服务一般，价格偏贵。希望可以提供更多优惠。',
    '环境还可以，但服务态度有待提高。',
    '中规中矩，没有特别惊艳也没有特别差。',
    '基本满足需求，但细节方面还有提升空间。'
  ],
  2: [
    '体验不太好，等待时间太长。服务态度一般。',
    '价格偏贵，服务质量与价格不符。',
    '环境一般，希望可以改善。不太满意这次服务。'
  ],
  1: [
    '非常不满意，服务态度差。不会再来了。',
    '体验很差，不推荐。希望店家能够改进。'
  ]
}

const MERCHANT_NAMES = [
  '萌宠乐园', '爱宠之家', '宠物天地', '毛孩子宠物店', '快乐宠物馆',
  '宠物世界', '宠爱一生', '小动物之家', '宠物驿站', '爱心宠物店',
  '宠物小镇', '萌宠工坊', '宠物之家', '宠爱有家', '宠物乐园',
  '毛茸茸宠物馆', '萌宠世界', '爱宠天地', '宠物王国', '萌宠之家'
]

const SERVICE_CATEGORIES = [
  '宠物美容', '宠物寄养', '宠物医疗', '宠物训练', '宠物摄影', '宠物接送', '宠物SPA', '宠物体检'
]

const SERVICE_NAMES: Record<string, string[]> = {
  '宠物美容': ['精致洗护套餐', '造型修剪', '毛发护理', '指甲修剪', '耳道清洁', '全身SPA'],
  '宠物寄养': ['日间寄养', '短期寄养', '长期寄养', '豪华套房寄养', 'VIP寄养', '节假日寄养'],
  '宠物医疗': ['常规体检', '疫苗接种', '驱虫服务', '健康咨询', '急诊服务', '专科诊疗'],
  '宠物训练': ['基础训练', '行为矫正', '技能培训', '社会化训练', '一对一私教', '团体课程'],
  '宠物摄影': ['写真拍摄', '亲子照', '节日主题', '户外拍摄', '视频制作', '证件照'],
  '宠物接送': ['机场接送', '医院接送', '日常接送', '跨城托运', '专车服务', '紧急接送'],
  '宠物SPA': ['舒缓SPA', '药浴SPA', '香薰SPA', '按摩护理', '深层清洁', '美毛护理'],
  '宠物体检': ['基础体检', '全面体检', '老年体检', '幼宠体检', '专项检查', '年度体检']
]

const PRODUCT_CATEGORIES = ['宠物食品', '宠物用品', '宠物玩具', '宠物服饰', '宠物保健']

const PRODUCT_NAMES: Record<string, string[]> = {
  '宠物食品': [
    '皇家猫粮 成猫通用型', '渴望狗粮 无谷鸡肉配方', '猫罐头 金枪鱼湿粮',
    '宠物狗咬胶 磨牙棒', '营养膏 猫咪专用', '冻干零食 鸡肉粒'
  ],
  '宠物用品': [
    '猫抓板 瓦楞纸猫咪磨爪器', '宠物自动喂食器 定时投喂', '猫砂盆 封闭式防臭',
    '狗狗牵引绳 自动伸缩', '宠物洗澡刷 按摩梳毛一体', '宠物饮水机 智能恒温'
  ],
  '宠物玩具': [
    '猫咪逗猫棒 羽毛铃铛套装', '狗狗飞盘 户外互动玩具', '宠物毛绒玩具 耐咬',
    '益智漏食球 狗猫通用', '激光笔 宠物互动', '猫隧道 组合玩具'
  ],
  '宠物服饰': [
    '宠物保暖衣服 小型犬冬季棉服', '猫咪蝴蝶结领结衣服', '宠物雨衣 防水透气',
    '狗狗四脚衣服 春秋款', '宠物鞋子 防滑耐磨', '宠物帽子 可爱造型'
  ],
  '宠物保健': [
    '宠物钙片 营养补充剂', '宠物益生菌 肠道调理', '宠物维生素 综合营养',
    '宠物美毛粉 卵磷脂', '宠物关节宝 软骨素', '宠物驱虫药 体内外同驱'
  ]
}

const PET_TYPES = ['狗', '猫', '兔子', '仓鼠', '鸟', '鱼', '龟', '其他']

const PET_BREEDS: Record<string, string[]> = {
  '狗': ['金毛', '拉布拉多', '柯基', '泰迪', '哈士奇', '边境牧羊犬', '萨摩耶', '德国牧羊犬', '比熊', '雪纳瑞'],
  '猫': ['英短', '美短', '布偶猫', '暹罗猫', '波斯猫', '橘猫', '蓝猫', '缅因猫', '加菲猫', '无毛猫'],
  '兔子': ['荷兰垂耳兔', '安哥拉兔', '荷兰侏儒兔', '狮子头兔', '巨型花明兔'],
  '仓鼠': ['金丝熊', '布丁仓鼠', '银狐仓鼠', '三线仓鼠', '老公公仓鼠'],
  '鸟': ['虎皮鹦鹉', '金丝雀', '文鸟', '画眉', '八哥', '鹩哥'],
  '鱼': ['金鱼', '锦鲤', '龙鱼', '孔雀鱼', '斗鱼', '神仙鱼'],
  '龟': ['巴西龟', '草龟', '金钱龟', '陆龟', '鳄龟'],
  '其他': ['未知品种']
}

const PET_NAMES = [
  '小白', '小黑', '花花', '豆豆', '球球', '毛毛', '咪咪', '旺财',
  '大黄', '小黄', '巧克力', '布丁', '奶茶', '可乐', '雪球', '团子',
  '馒头', '包子', '饺子', '汤圆', '年糕', '芝麻', '花生', '核桃'
]

const PERSONALITIES = [
  '活泼好动', '温顺乖巧', '独立高冷', '粘人可爱', '聪明伶俐',
  '胆小怕生', '调皮捣蛋', '安静沉稳', '友善亲人', '好奇宝宝'
]

const LOGISTICS_COMPANIES = ['顺丰速运', '中通快递', '圆通速递', '韵达快递', '申通快递', '百世快递', '极兔速递']

const PAY_METHODS = ['微信支付', '支付宝', '银行卡']

let idCounter = 0
const idCounters: Record<string, number> = {}

export const generateId = (prefix = ''): string => {
  const key = prefix || 'default'
  if (!idCounters[key]) {
    idCounters[key] = 1000
  }
  idCounters[key]++
  return prefix ? `${prefix}${idCounters[key]}` : String(idCounters[key])
}

export const generateNumericId = (min = 1, max = 10000): number => {
  return Mock.Random.integer(min, max)
}

export const generateUUID = (): string => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

export const generateTimestampId = (prefix = ''): string => {
  const timestamp = Date.now()
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return prefix ? `${prefix}${timestamp}${random}` : `${timestamp}${random}`
}

export const generateOrderId = (prefix = 'ORD'): string => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `${prefix}${year}${month}${day}${random}`
}

export const generateAppointmentNo = (prefix = 'APT'): string => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `${prefix}${year}${month}${day}${random}`
}

export const generateAdvancedId = (options: IDGeneratorOptions = {}): string => {
  const { prefix = '', length = 8, type = 'numeric' } = options

  switch (type) {
    case 'uuid':
      return prefix + generateUUID()
    case 'timestamp':
      return generateTimestampId(prefix)
    case 'alphanumeric': {
      const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
      let result = ''
      for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length))
      }
      return prefix + result
    }
    case 'numeric':
    default: {
      const num = Math.floor(Math.random() * Math.pow(10, length))
      return prefix + num.toString().padStart(length, '0')
    }
  }
}

export const generateDate = (range?: DateRange): string => {
  const start = range?.start ? new Date(range.start) : new Date('2024-01-01')
  const end = range?.end ? new Date(range.end) : new Date()
  const timestamp = start.getTime() + Math.random() * (end.getTime() - start.getTime())
  return new Date(timestamp).toISOString().replace('T', ' ').substring(0, 19)
}

export const generateFutureDate = (daysFromNow = 7, includeTime = true): string => {
  const now = new Date()
  const future = new Date(now.getTime() + Math.random() * daysFromNow * 24 * 60 * 60 * 1000)
  if (includeTime) {
    future.setHours(9 + Math.floor(Math.random() * 10), 0, 0, 0)
    return future.toISOString().replace('T', ' ').substring(0, 19)
  }
  return future.toISOString().substring(0, 10)
}

export const generatePastDate = (daysAgo = 30, includeTime = true): string => {
  const now = new Date()
  const past = new Date(now.getTime() - Math.random() * daysAgo * 24 * 60 * 60 * 1000)
  if (includeTime) {
    return past.toISOString().replace('T', ' ').substring(0, 19)
  }
  return past.toISOString().substring(0, 10)
}

export const generateTodayDate = (includeTime = true): string => {
  const now = new Date()
  if (includeTime) {
    return now.toISOString().replace('T', ' ').substring(0, 19)
  }
  return now.toISOString().substring(0, 10)
}

export const generateTimeSlot = (startHour = 9, endHour = 18): string => {
  const hour = startHour + Math.floor(Math.random() * (endHour - startHour))
  return `${hour.toString().padStart(2, '0')}:00`
}

export const generateAdvancedDate = (options: DateGeneratorOptions = {}): string => {
  const { start, end, format = 'datetime', includeTime = true } = options
  const startDate = start ? new Date(start) : new Date('2024-01-01')
  const endDate = end ? new Date(end) : new Date()
  const timestamp = startDate.getTime() + Math.random() * (endDate.getTime() - startDate.getTime())
  const date = new Date(timestamp)

  switch (format) {
    case 'date':
      return date.toISOString().substring(0, 10)
    case 'time':
      return date.toISOString().substring(11, 19)
    case 'timestamp':
      return String(date.getTime())
    case 'datetime':
    default:
      if (includeTime) {
        return date.toISOString().replace('T', ' ').substring(0, 19)
      }
      return date.toISOString().substring(0, 10)
  }
}

export const randomEnum = <T>(arr: T[]): T => {
  return arr[Math.floor(Math.random() * arr.length)]
}

export const randomWeighted = <T>(options: WeightedOption<T>[]): T => {
  const totalWeight = options.reduce((sum, opt) => sum + opt.weight, 0)
  let random = Math.random() * totalWeight

  for (const option of options) {
    random -= option.weight
    if (random <= 0) {
      return option.value
    }
  }

  return options[options.length - 1].value
}

export const randomBoolean = (probability = 0.5): boolean => {
  return Math.random() < probability
}

export const randomString = (length = 10): string => {
  return Mock.Random.string('lower', length)
}

export const randomChineseName = (gender?: 'male' | 'female'): string => {
  const surname = randomEnum(CHINESE_SURNAMES)
  const nameGender = gender || (randomBoolean() ? 'male' : 'female')
  const givenNames = nameGender === 'male' ? CHINESE_GIVEN_NAMES_MALE : CHINESE_GIVEN_NAMES_FEMALE
  const givenName = randomEnum(givenNames)
  const secondGiven = randomBoolean(0.3) ? randomEnum(givenNames) : ''
  return surname + givenName + secondGiven
}

export const randomCity = (): CityInfo => {
  return randomEnum(CHINESE_CITIES)
}

export const randomFullAddress = (): string => {
  const city = randomCity()
  const district = randomEnum(city.districts)
  const street = randomEnum(STREETS)
  const number = Math.floor(Math.random() * 500) + 1
  const building = Math.floor(Math.random() * 20) + 1
  const room = Math.floor(Math.random() * 2000) + 101
  return `${city.province}${city.city}${district}${street}${number}号${building}栋${room}室`
}

export const randomSimpleAddress = (): string => {
  const city = randomCity()
  const district = randomEnum(city.districts)
  const street = randomEnum(STREETS)
  const number = Math.floor(Math.random() * 500) + 1
  return `${city.province}${city.city}${district}${street}${number}号`
}

export const randomPhone = (): string => {
  const prefixes = ['138', '139', '150', '151', '152', '158', '159', '186', '187', '188', '177', '153', '180', '181', '189']
  const prefix = randomEnum(prefixes)
  const suffix = Math.floor(Math.random() * 100000000).toString().padStart(8, '0')
  return prefix + suffix
}

export const randomEmail = (): string => {
  const domains = ['qq.com', '163.com', 'gmail.com', 'outlook.com', 'sina.com', '126.com', 'hotmail.com']
  const username = Mock.Random.string('lower', 8)
  return `${username}@${randomEnum(domains)}`
}

export const randomPrice = (min = 10, max = 1000): number => {
  return Number((Math.random() * (max - min) + min).toFixed(2))
}

export const randomRating = (weighted = false): number => {
  if (weighted) {
    return randomWeighted([
      { value: 5, weight: 40 },
      { value: 4, weight: 35 },
      { value: 3, weight: 15 },
      { value: 2, weight: 7 },
      { value: 1, weight: 3 }
    ])
  }
  return Mock.Random.integer(1, 5)
}

export const randomQuantity = (min = 1, max = 100): number => {
  return Mock.Random.integer(min, max)
}

export const randomDuration = (): number => {
  const durations = [30, 45, 60, 90, 120, 180]
  return randomEnum(durations)
}

export const randomPetType = (): string => {
  return randomEnum(PET_TYPES)
}

export const randomPetBreed = (type?: string): string => {
  const petType = type || randomPetType()
  return randomEnum(PET_BREEDS[petType] || PET_BREEDS['其他'])
}

export const randomPetName = (): string => {
  return randomEnum(PET_NAMES)
}

export const randomPetPersonality = (): string => {
  return randomEnum(PERSONALITIES)
}

export const randomGender = (): string => {
  return randomEnum(['male', 'female'])
}

export const randomOrderStatus = (): string => {
  return randomEnum(['pending', 'paid', 'shipped', 'completed', 'cancelled'])
}

export const randomAppointmentStatus = (): string => {
  return randomEnum(['pending', 'confirmed', 'completed', 'cancelled'])
}

export const randomMerchantStatus = (): string => {
  return randomWeighted([
    { value: 'approved', weight: 70 },
    { value: 'pending', weight: 20 },
    { value: 'rejected', weight: 10 }
  ])
}

export const randomServiceCategory = (): string => {
  return randomEnum(SERVICE_CATEGORIES)
}

export const randomServiceName = (category?: string): string => {
  const cat = category || randomServiceCategory()
  return randomEnum(SERVICE_NAMES[cat] || [cat + '服务'])
}

export const randomServiceDescription = (category?: string): string => {
  const cat = category || randomServiceCategory()
  return randomEnum(SERVICE_DESCRIPTIONS[cat] || ['专业服务，品质保证。'])
}

export const randomProductCategory = (): string => {
  return randomEnum(PRODUCT_CATEGORIES)
}

export const randomProductName = (category?: string): string => {
  const cat = category || randomProductCategory()
  return randomEnum(PRODUCT_NAMES[cat] || [cat + '商品'])
}

export const randomProductDescription = (category?: string): string => {
  const cat = category || randomProductCategory()
  return randomEnum(PRODUCT_DESCRIPTIONS[cat] || ['优质商品，品质保证。'])
}

export const randomMerchantName = (): string => {
  return randomEnum(MERCHANT_NAMES) + (randomBoolean(0.7) ? `${Math.floor(Math.random() * 99) + 1}号店` : '')
}

export const randomMerchantDescription = (): string => {
  const descriptions = [
    '专业宠物服务，用心呵护每一位毛孩子。我们提供全方位的宠物护理服务，让您的爱宠享受最优质的照顾。',
    '温馨舒适的宠物之家，为您的爱宠提供最贴心的服务。专业团队，品质保障，让宠物开心，让主人放心。',
    '多年宠物服务经验，深受客户好评。我们坚持以宠物为中心，提供个性化服务方案。',
    '一站式宠物服务中心，涵盖美容、寄养、医疗、训练等多种服务。专业设备，舒适环境。',
    '爱心宠物店，用心服务每一位宠物主人。价格实惠，服务周到，欢迎光临！'
  ]
  return randomEnum(descriptions)
}

export const randomReviewComment = (rating?: number): string => {
  const r = rating || randomRating(true)
  return randomEnum(REVIEW_COMMENTS[r] || REVIEW_COMMENTS[3])
}

export const randomAvatar = (): string => {
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${Mock.Random.string('lower', 8)}`
}

export const randomImage = (width = 200, height = 200): string => {
  return `https://picsum.photos/${width}/${height}?random=${Mock.Random.integer(1, 1000)}`
}

export const randomImages = (count = 3, width = 200, height = 200): string[] => {
  return Array.from({ length: count }, () => randomImage(width, height))
}

export const generateList = <T>(generator: () => T, count: number): T[] => {
  return Array.from({ length: count }, generator)
}

export const paginateList = <T>(list: T[], page = 1, pageSize = 10): { data: T[]; total: number; page: number; pageSize: number } => {
  const start = (page - 1) * pageSize
  const end = start + pageSize
  return {
    data: list.slice(start, end),
    total: list.length,
    page,
    pageSize
  }
}

export const delay = (ms: number): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms))
}

export const createSuccessResponse = <T>(data: T, message = 'success') => {
  return {
    code: 200,
    message,
    data
  }
}

export const createErrorResponse = (message: string, code = 400) => {
  return {
    code,
    message,
    data: null
  }
}

export const generateMerchant = (id: number, overrides: Partial<MerchantData> = {}): MerchantData => {
  const createdAt = generatePastDate(365)
  const updatedAt = generateDate({ start: createdAt.substring(0, 10), end: '2026-04-19' })

  return {
    id,
    name: randomMerchantName(),
    logo: randomImage(200, 200),
    contactPerson: randomChineseName(),
    phone: randomPhone(),
    email: randomEmail(),
    address: randomSimpleAddress(),
    description: randomMerchantDescription(),
    rating: Number((3.5 + Math.random() * 1.5).toFixed(1)),
    reviewCount: Math.floor(Math.random() * 500) + 10,
    serviceCount: Math.floor(Math.random() * 15) + 3,
    productCount: Math.floor(Math.random() * 20) + 5,
    status: 'approved',
    createdAt,
    updatedAt,
    ...overrides
  }
}

export const generateService = (id: number, merchant: MerchantData, overrides: Partial<ServiceData> = {}): ServiceData => {
  const category = randomServiceCategory()
  const createdAt = generatePastDate(300)
  const updatedAt = generateDate({ start: createdAt.substring(0, 10), end: '2026-04-19' })

  const basePrice: Record<string, number> = {
    '宠物美容': 100,
    '宠物寄养': 80,
    '宠物医疗': 150,
    '宠物训练': 200,
    '宠物摄影': 300,
    '宠物接送': 50,
    '宠物SPA': 180,
    '宠物体检': 120
  }

  const basePriceValue = basePrice[category] || 100

  return {
    id,
    merchantId: merchant.id,
    merchantName: merchant.name,
    name: randomServiceName(category),
    description: randomServiceDescription(category),
    price: Number((basePriceValue + Math.random() * basePriceValue * 0.5).toFixed(2)),
    duration: randomDuration(),
    image: randomImage(400, 300),
    category,
    status: 'enabled',
    rating: Number((3.5 + Math.random() * 1.5).toFixed(1)),
    reviewCount: Math.floor(Math.random() * 200) + 10,
    createdAt,
    updatedAt,
    ...overrides
  }
}

export const generateProduct = (id: number, merchant: MerchantData, overrides: Partial<ProductData> = {}): ProductData => {
  const category = randomProductCategory()
  const createdAt = generatePastDate(300)
  const updatedAt = generateDate({ start: createdAt.substring(0, 10), end: '2026-04-19' })

  const basePrice: Record<string, number> = {
    '宠物食品': 50,
    '宠物用品': 30,
    '宠物玩具': 20,
    '宠物服饰': 40,
    '宠物保健': 60
  }

  const basePriceValue = basePrice[category] || 50

  return {
    id,
    merchantId: merchant.id,
    merchantName: merchant.name,
    name: randomProductName(category),
    description: randomProductDescription(category),
    price: Number((basePriceValue + Math.random() * basePriceValue * 2).toFixed(2)),
    stock: Math.floor(Math.random() * 300),
    image: randomImage(400, 400),
    category,
    status: 'enabled',
    sales: Math.floor(Math.random() * 5000),
    rating: Number((3.5 + Math.random() * 1.5).toFixed(1)),
    reviewCount: Math.floor(Math.random() * 300) + 10,
    createdAt,
    updatedAt,
    ...overrides
  }
}

export const generateUser = (id: number, overrides: Partial<UserData> = {}): UserData => {
  const createdAt = generatePastDate(500)
  const updatedAt = generateDate({ start: createdAt.substring(0, 10), end: '2026-04-19' })

  return {
    id,
    username: randomChineseName(),
    email: randomEmail(),
    phone: randomPhone(),
    avatar: randomAvatar(),
    createdAt,
    updatedAt,
    ...overrides
  }
}

export const generatePet = (id: number, userId: number, overrides: Partial<PetData> = {}): PetData => {
  const type = randomPetType()
  const createdAt = generatePastDate(400)
  const updatedAt = generateDate({ start: createdAt.substring(0, 10), end: '2026-04-19' })

  return {
    id,
    userId,
    name: randomPetName(),
    type,
    breed: randomPetBreed(type),
    age: Math.floor(Math.random() * 10) + 1,
    gender: randomGender(),
    avatar: randomImage(200, 200),
    description: `${randomPetPersonality()}的小可爱`,
    weight: Number((Math.random() * 20 + 2).toFixed(1)),
    personality: randomPetPersonality(),
    createdAt,
    updatedAt,
    ...overrides
  }
}

export const generateAppointment = (
  id: number,
  user: UserData,
  pet: PetData,
  merchant: MerchantData,
  service: ServiceData,
  overrides: Partial<AppointmentData> = {}
): AppointmentData => {
  const isPast = randomBoolean(0.5)
  const appointmentTime = isPast ? generatePastDate(30) : generateFutureDate(30)
  const createdAt = generateDate({ end: appointmentTime.substring(0, 10) })
  const updatedAt = generateDate({ start: createdAt.substring(0, 10), end: '2026-04-19' })

  let status: 'pending' | 'confirmed' | 'completed' | 'cancelled'
  if (isPast) {
    status = randomWeighted([
      { value: 'completed', weight: 70 },
      { value: 'cancelled', weight: 30 }
    ])
  } else {
    status = randomWeighted([
      { value: 'pending', weight: 40 },
      { value: 'confirmed', weight: 60 }
    ])
  }

  return {
    id,
    userId: user.id,
    userName: user.username,
    merchantId: merchant.id,
    merchantName: merchant.name,
    serviceId: service.id,
    serviceName: service.name,
    petId: pet.id,
    petName: pet.name,
    appointmentTime,
    totalPrice: service.price,
    status,
    notes: randomBoolean(0.3) ? '请轻柔对待我的宠物' : '',
    createdAt,
    updatedAt,
    ...overrides
  }
}

export const generateOrder = (
  id: number,
  user: UserData,
  merchant: MerchantData,
  products: ProductData[],
  overrides: Partial<OrderData> = {}
): OrderData => {
  const itemCount = Math.floor(Math.random() * 3) + 1
  const selectedProducts = products.sort(() => Math.random() - 0.5).slice(0, itemCount)

  const items = selectedProducts.map((product, index) => {
    const quantity = Math.floor(Math.random() * 3) + 1
    return {
      id: index + 1,
      productId: product.id,
      productName: product.name,
      productImage: product.image,
      price: product.price,
      quantity,
      subtotal: Number((product.price * quantity).toFixed(2))
    }
  })

  const totalPrice = Number(items.reduce((sum, item) => sum + item.subtotal, 0).toFixed(2))
  const freight = totalPrice > 99 ? 0 : 10

  const status = randomWeighted([
    { value: 'completed', weight: 40 },
    { value: 'shipped', weight: 20 },
    { value: 'paid', weight: 15 },
    { value: 'pending', weight: 15 },
    { value: 'cancelled', weight: 10 }
  ])

  const createdAt = generatePastDate(60)
  const updatedAt = generateDate({ start: createdAt.substring(0, 10), end: '2026-04-19' })

  const city = randomCity()

  let payTime: string | null = null
  let shipTime: string | null = null
  let completeTime: string | null = null
  let cancelTime: string | null = null

  if (status !== 'pending' && status !== 'cancelled') {
    payTime = generateDate({ start: createdAt.substring(0, 10), end: '2026-04-19' })
  }
  if (status === 'shipped' || status === 'completed') {
    shipTime = generateDate({ start: payTime?.substring(0, 10) || createdAt.substring(0, 10), end: '2026-04-19' })
  }
  if (status === 'completed') {
    completeTime = generateDate({ start: shipTime?.substring(0, 10) || createdAt.substring(0, 10), end: '2026-04-19' })
  }
  if (status === 'cancelled') {
    cancelTime = generateDate({ start: createdAt.substring(0, 10), end: '2026-04-19' })
  }

  return {
    id,
    orderNo: generateOrderId(),
    userId: user.id,
    userName: user.username,
    merchantId: merchant.id,
    merchantName: merchant.name,
    totalPrice,
    freight,
    status,
    payMethod: randomEnum(PAY_METHODS),
    payTime,
    shipTime,
    completeTime,
    cancelTime,
    remark: randomBoolean(0.3) ? '请尽快发货，谢谢！' : '',
    shippingAddress: {
      name: user.username,
      phone: user.phone,
      province: city.province,
      city: city.city,
      district: randomEnum(city.districts),
      detailAddress: randomEnum(STREETS) + (Math.floor(Math.random() * 500) + 1) + '号'
    },
    items,
    createdAt,
    updatedAt,
    ...overrides
  }
}

export const generateReview = (
  id: number,
  user: UserData,
  merchant: MerchantData,
  service: ServiceData,
  appointmentId: number,
  overrides: Partial<ReviewData> = {}
): ReviewData => {
  const rating = randomRating(true)
  const createdAt = generatePastDate(90)

  return {
    id,
    userId: user.id,
    userName: user.username,
    userAvatar: user.avatar,
    merchantId: merchant.id,
    merchantName: merchant.name,
    serviceId: service.id,
    serviceName: service.name,
    appointmentId,
    rating,
    comment: randomReviewComment(rating),
    images: randomBoolean(0.4) ? randomImages(Math.floor(Math.random() * 3) + 1) : [],
    reply: randomBoolean(0.3) ? '感谢您的好评，我们会继续努力！' : null,
    replyTime: randomBoolean(0.3) ? generateDate({ start: createdAt.substring(0, 10), end: '2026-04-19' }) : null,
    createdAt,
    ...overrides
  }
}

export const generateMerchants = (count = 10): MerchantData[] => {
  const merchants: MerchantData[] = []
  const usedNames = new Set<string>()

  for (let i = 1; i <= count; i++) {
    let name = randomMerchantName()
    let attempts = 0
    while (usedNames.has(name) && attempts < 10) {
      name = randomMerchantName()
      attempts++
    }
    usedNames.add(name)

    merchants.push(generateMerchant(i, { name }))
  }

  return merchants
}

export const generateServices = (merchants: MerchantData[], servicesPerMerchant = 3): ServiceData[] => {
  const services: ServiceData[] = []
  let serviceId = 1

  merchants.forEach(merchant => {
    const count = Math.floor(Math.random() * 3) + servicesPerMerchant
    const usedCategories = new Set<string>()

    for (let i = 0; i < count; i++) {
      let category = randomServiceCategory()
      let attempts = 0
      while (usedCategories.has(category) && attempts < 10) {
        category = randomServiceCategory()
        attempts++
      }
      usedCategories.add(category)

      services.push(generateService(serviceId, merchant, { category }))
      serviceId++
    }
  })

  return services
}

export const generateProducts = (merchants: MerchantData[], productsPerMerchant = 5): ProductData[] => {
  const products: ProductData[] = []
  let productId = 1

  merchants.forEach(merchant => {
    const count = Math.floor(Math.random() * 5) + productsPerMerchant

    for (let i = 0; i < count; i++) {
      products.push(generateProduct(productId, merchant))
      productId++
    }
  })

  return products
}

export const generateUsers = (count = 10): UserData[] => {
  return Array.from({ length: count }, (_, i) => generateUser(i + 1))
}

export const generatePets = (users: UserData[], petsPerUser = 2): PetData[] => {
  const pets: PetData[] = []
  let petId = 1

  users.forEach(user => {
    const count = Math.floor(Math.random() * 3) + 1

    for (let i = 0; i < count; i++) {
      pets.push(generatePet(petId, user.id))
      petId++
    }
  })

  return pets
}

export const generateAppointments = (
  users: UserData[],
  pets: PetData[],
  merchants: MerchantData[],
  services: ServiceData[],
  appointmentsPerUser = 3
): AppointmentData[] => {
  const appointments: AppointmentData[] = []
  let appointmentId = 1

  users.forEach(user => {
    const userPets = pets.filter(p => p.userId === user.id)
    const count = Math.floor(Math.random() * 4) + appointmentsPerUser

    for (let i = 0; i < count; i++) {
      const pet = userPets.length > 0 ? randomEnum(userPets) : pets[0]
      const merchant = randomEnum(merchants)
      const merchantServices = services.filter(s => s.merchantId === merchant.id)
      const service = merchantServices.length > 0 ? randomEnum(merchantServices) : services[0]

      appointments.push(generateAppointment(appointmentId, user, pet, merchant, service))
      appointmentId++
    }
  })

  return appointments
}

export const generateOrders = (
  users: UserData[],
  merchants: MerchantData[],
  products: ProductData[],
  ordersPerUser = 3
): OrderData[] => {
  const orders: OrderData[] = []
  let orderId = 1

  users.forEach(user => {
    const count = Math.floor(Math.random() * 4) + ordersPerUser

    for (let i = 0; i < count; i++) {
      const merchant = randomEnum(merchants)
      const merchantProducts = products.filter(p => p.merchantId === merchant.id)

      if (merchantProducts.length > 0) {
        orders.push(generateOrder(orderId, user, merchant, merchantProducts))
        orderId++
      }
    }
  })

  return orders
}

export const generateReviews = (
  users: UserData[],
  merchants: MerchantData[],
  services: ServiceData[],
  appointments: AppointmentData[],
  reviewsPerUser = 2
): ReviewData[] => {
  const reviews: ReviewData[] = []
  let reviewId = 1

  users.forEach(user => {
    const userAppointments = appointments.filter(a => a.userId === user.id && a.status === 'completed')
    const count = Math.min(Math.floor(Math.random() * 3) + reviewsPerUser, userAppointments.length)

    for (let i = 0; i < count; i++) {
      const appointment = userAppointments[i]
      if (appointment) {
        const merchant = merchants.find(m => m.id === appointment.merchantId)!
        const service = services.find(s => s.id === appointment.serviceId)!

        reviews.push(generateReview(reviewId, user, merchant, service, appointment.id))
        reviewId++
      }
    }
  })

  return reviews
}

export interface GeneratedData {
  users: UserData[]
  pets: PetData[]
  merchants: MerchantData[]
  services: ServiceData[]
  products: ProductData[]
  appointments: AppointmentData[]
  orders: OrderData[]
  reviews: ReviewData[]
}

export const generateAllData = (options: {
  userCount?: number
  merchantCount?: number
  servicesPerMerchant?: number
  productsPerMerchant?: number
  petsPerUser?: number
  appointmentsPerUser?: number
  ordersPerUser?: number
  reviewsPerUser?: number
} = {}): GeneratedData => {
  const {
    userCount = 10,
    merchantCount = 15,
    servicesPerMerchant = 3,
    productsPerMerchant = 5,
    petsPerUser = 2,
    appointmentsPerUser = 3,
    ordersPerUser = 3,
    reviewsPerUser = 2
  } = options

  const users = generateUsers(userCount)
  const merchants = generateMerchants(merchantCount)
  const services = generateServices(merchants, servicesPerMerchant)
  const products = generateProducts(merchants, productsPerMerchant)
  const pets = generatePets(users, petsPerUser)
  const appointments = generateAppointments(users, pets, merchants, services, appointmentsPerUser)
  const orders = generateOrders(users, merchants, products, ordersPerUser)
  const reviews = generateReviews(users, merchants, services, appointments, reviewsPerUser)

  return {
    users,
    pets,
    merchants,
    services,
    products,
    appointments,
    orders,
    reviews
  }
}
