export interface Product {
  id: number
  name: string
  description: string
  price: number
  stock: number
  image: string
  merchantId: number
  merchantName: string
  category: string
  sales: number
  rating: number
  status: 'enabled' | 'disabled'
  createdAt: string
}

const categories = ['宠物食品', '宠物用品', '宠物玩具', '宠物服饰', '宠物保健']

export const products: Product[] = [
  {
    id: 1,
    name: '皇家猫粮 成猫通用型 2kg',
    description: '专为成年猫咪设计的营养配方，富含优质蛋白质和必需营养素，帮助维持猫咪健康体态和亮丽毛发。',
    price: 128.00,
    stock: 156,
    image: 'https://picsum.photos/400/400?random=1',
    merchantId: 1,
    merchantName: '宠物乐园',
    category: '宠物食品',
    sales: 2341,
    rating: 4.8,
    status: 'enabled',
    createdAt: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    name: '渴望狗粮 无谷鸡肉配方 5kg',
    description: '采用新鲜鸡肉为主要原料，无谷物配方，适合肠胃敏感的狗狗，营养均衡易消化。',
    price: 358.00,
    stock: 89,
    image: 'https://picsum.photos/400/400?random=2',
    merchantId: 1,
    merchantName: '宠物乐园',
    category: '宠物食品',
    sales: 1892,
    rating: 4.9,
    status: 'enabled',
    createdAt: '2024-01-16 14:20:00'
  },
  {
    id: 3,
    name: '猫抓板 瓦楞纸猫咪磨爪器',
    description: '环保瓦楞纸材质，保护家具不被抓坏，附带猫薄荷吸引猫咪使用，耐磨耐用。',
    price: 29.90,
    stock: 320,
    image: 'https://picsum.photos/400/400?random=3',
    merchantId: 2,
    merchantName: '萌宠之家',
    category: '宠物用品',
    sales: 5672,
    rating: 4.6,
    status: 'enabled',
    createdAt: '2024-01-17 09:15:00'
  },
  {
    id: 4,
    name: '宠物自动喂食器 定时投喂 5L大容量',
    description: '智能定时喂食，5L大容量储粮，支持APP远程控制，解决主人外出喂食难题。',
    price: 299.00,
    stock: 67,
    image: 'https://picsum.photos/400/400?random=4',
    merchantId: 2,
    merchantName: '萌宠之家',
    category: '宠物用品',
    sales: 1234,
    rating: 4.7,
    status: 'enabled',
    createdAt: '2024-01-18 16:45:00'
  },
  {
    id: 5,
    name: '猫咪逗猫棒 羽毛铃铛玩具套装',
    description: '天然羽毛材质，带有铃铛设计，吸引猫咪注意力，增进主人与宠物的互动乐趣。',
    price: 15.90,
    stock: 0,
    image: 'https://picsum.photos/400/400?random=5',
    merchantId: 3,
    merchantName: '爱宠世界',
    category: '宠物玩具',
    sales: 8923,
    rating: 4.5,
    status: 'enabled',
    createdAt: '2024-01-19 11:30:00'
  },
  {
    id: 6,
    name: '狗狗飞盘 户外互动玩具 橡胶材质',
    description: '高弹性橡胶材质，耐咬耐磨，适合户外互动训练，增强狗狗体质和服从性。',
    price: 35.00,
    stock: 245,
    image: 'https://picsum.photos/400/400?random=6',
    merchantId: 3,
    merchantName: '爱宠世界',
    category: '宠物玩具',
    sales: 3456,
    rating: 4.4,
    status: 'enabled',
    createdAt: '2024-01-20 08:20:00'
  },
  {
    id: 7,
    name: '宠物保暖衣服 小型犬冬季棉服',
    description: '加厚棉质面料，保暖舒适，四脚设计更贴合身体，适合小型犬冬季穿着。',
    price: 68.00,
    stock: 134,
    image: 'https://picsum.photos/400/400?random=7',
    merchantId: 1,
    merchantName: '宠物乐园',
    category: '宠物服饰',
    sales: 2156,
    rating: 4.6,
    status: 'enabled',
    createdAt: '2024-01-21 13:50:00'
  },
  {
    id: 8,
    name: '猫咪蝴蝶结领结衣服 可爱公主裙',
    description: '甜美公主风设计，蝴蝶结装饰，柔软面料不伤皮肤，让猫咪更可爱迷人。',
    price: 45.00,
    stock: 89,
    image: 'https://picsum.photos/400/400?random=8',
    merchantId: 2,
    merchantName: '萌宠之家',
    category: '宠物服饰',
    sales: 1678,
    rating: 4.7,
    status: 'enabled',
    createdAt: '2024-01-22 15:10:00'
  },
  {
    id: 9,
    name: '宠物钙片 营养补充剂 200片装',
    description: '高钙配方，添加维生素D促进吸收，帮助宠物骨骼发育，预防骨质疏松。',
    price: 89.00,
    stock: 178,
    image: 'https://picsum.photos/400/400?random=9',
    merchantId: 3,
    merchantName: '爱宠世界',
    category: '宠物保健',
    sales: 2934,
    rating: 4.8,
    status: 'enabled',
    createdAt: '2024-01-23 10:25:00'
  },
  {
    id: 10,
    name: '宠物益生菌 肠道调理 30包装',
    description: '活性益生菌配方，改善宠物肠道健康，缓解腹泻便秘，增强免疫力。',
    price: 128.00,
    stock: 92,
    image: 'https://picsum.photos/400/400?random=10',
    merchantId: 1,
    merchantName: '宠物乐园',
    category: '宠物保健',
    sales: 1876,
    rating: 4.9,
    status: 'enabled',
    createdAt: '2024-01-24 12:40:00'
  },
  {
    id: 11,
    name: '猫砂盆 封闭式防臭猫厕所 大号',
    description: '封闭式设计有效防臭，大容量适合多猫家庭，易清洗材质，配带猫砂铲。',
    price: 159.00,
    stock: 56,
    image: 'https://picsum.photos/400/400?random=11',
    merchantId: 2,
    merchantName: '萌宠之家',
    category: '宠物用品',
    sales: 987,
    rating: 4.5,
    status: 'enabled',
    createdAt: '2024-01-25 09:55:00'
  },
  {
    id: 12,
    name: '狗狗牵引绳 自动伸缩牵引绳 5米',
    description: '自动伸缩设计，5米长度自由活动空间，人体工学手柄，一键锁定功能。',
    price: 79.00,
    stock: 234,
    image: 'https://picsum.photos/400/400?random=12',
    merchantId: 3,
    merchantName: '爱宠世界',
    category: '宠物用品',
    sales: 4521,
    rating: 4.6,
    status: 'enabled',
    createdAt: '2024-01-26 14:30:00'
  },
  {
    id: 13,
    name: '宠物洗澡刷 按摩梳毛一体',
    description: '柔软刷毛，洗澡梳毛二合一，按摩促进血液循环，让宠物享受洗澡时光。',
    price: 25.00,
    stock: 0,
    image: 'https://picsum.photos/400/400?random=13',
    merchantId: 1,
    merchantName: '宠物乐园',
    category: '宠物用品',
    sales: 6234,
    rating: 4.4,
    status: 'enabled',
    createdAt: '2024-01-27 16:15:00'
  },
  {
    id: 14,
    name: '猫罐头 金枪鱼湿粮 80g*12罐',
    description: '新鲜金枪鱼制作，高蛋白低脂肪，营养美味，满足猫咪挑剔口味。',
    price: 98.00,
    stock: 167,
    image: 'https://picsum.photos/400/400?random=14',
    merchantId: 2,
    merchantName: '萌宠之家',
    category: '宠物食品',
    sales: 3421,
    rating: 4.8,
    status: 'enabled',
    createdAt: '2024-01-28 11:45:00'
  },
  {
    id: 15,
    name: '宠物狗咬胶 磨牙棒 牛皮卷 10支装',
    description: '天然牛皮制作，耐咬耐磨，帮助狗狗清洁牙齿，缓解换牙期不适。',
    price: 49.00,
    stock: 289,
    image: 'https://picsum.photos/400/400?random=15',
    merchantId: 3,
    merchantName: '爱宠世界',
    category: '宠物食品',
    sales: 5678,
    rating: 4.7,
    status: 'enabled',
    createdAt: '2024-01-29 13:20:00'
  }
]

export const getProductById = (id: number): Product | undefined => {
  return products.find(p => p.id === id)
}

export const getProductsByCategory = (category: string): Product[] => {
  return products.filter(p => p.category === category)
}

export const getProductsByMerchant = (merchantId: number): Product[] => {
  return products.filter(p => p.merchantId === merchantId)
}
