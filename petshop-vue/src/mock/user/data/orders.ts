export interface OrderItem {
  productId: number
  productName: string
  productImage: string
  quantity: number
  price: number
  subtotal: number
}

export interface ShippingAddress {
  receiverName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
}

export interface Order {
  id: number
  orderNo: string
  userId: number
  merchantId: number
  merchantName: string
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'
  totalPrice: number
  items: OrderItem[]
  shippingAddress: ShippingAddress
  remark: string
  logisticsCompany?: string
  trackingNumber?: string
  createdAt: string
  paidAt?: string
  shippedAt?: string
  completedAt?: string
  cancelledAt?: string
}

export const orders: Order[] = [
  {
    id: 1,
    orderNo: 'ORD202401150001',
    userId: 1,
    merchantId: 1,
    merchantName: '宠物乐园',
    status: 'completed',
    totalPrice: 226.00,
    items: [
      {
        productId: 1,
        productName: '皇家猫粮 成猫通用型 2kg',
        productImage: 'https://picsum.photos/400/400?random=1',
        quantity: 1,
        price: 128.00,
        subtotal: 128.00
      },
      {
        productId: 10,
        productName: '宠物益生菌 肠道调理 30包装',
        productImage: 'https://picsum.photos/400/400?random=10',
        quantity: 1,
        price: 98.00,
        subtotal: 98.00
      }
    ],
    shippingAddress: {
      receiverName: '张三',
      phone: '13800138001',
      province: '北京市',
      city: '北京市',
      district: '朝阳区',
      detailAddress: '建国路88号SOHO现代城A座1001室'
    },
    remark: '请尽快发货，谢谢！',
    logisticsCompany: '顺丰速运',
    trackingNumber: 'SF1234567890',
    createdAt: '2024-01-15 10:30:00',
    paidAt: '2024-01-15 10:35:00',
    shippedAt: '2024-01-15 14:20:00',
    completedAt: '2024-01-17 16:45:00'
  },
  {
    id: 2,
    orderNo: 'ORD202401160002',
    userId: 1,
    merchantId: 2,
    merchantName: '萌宠之家',
    status: 'shipped',
    totalPrice: 398.00,
    items: [
      {
        productId: 4,
        productName: '宠物自动喂食器 定时投喂 5L大容量',
        productImage: 'https://picsum.photos/400/400?random=4',
        quantity: 1,
        price: 299.00,
        subtotal: 299.00
      },
      {
        productId: 3,
        productName: '猫抓板 瓦楞纸猫咪磨爪器',
        productImage: 'https://picsum.photos/400/400?random=3',
        quantity: 2,
        price: 29.90,
        subtotal: 59.80
      },
      {
        productId: 14,
        productName: '猫罐头 金枪鱼湿粮 80g*12罐',
        productImage: 'https://picsum.photos/400/400?random=14',
        quantity: 1,
        price: 39.20,
        subtotal: 39.20
      }
    ],
    shippingAddress: {
      receiverName: '张三',
      phone: '13800138001',
      province: '北京市',
      city: '北京市',
      district: '朝阳区',
      detailAddress: '建国路88号SOHO现代城A座1001室'
    },
    remark: '',
    logisticsCompany: '中通快递',
    trackingNumber: 'ZT9876543210',
    createdAt: '2024-01-16 14:20:00',
    paidAt: '2024-01-16 14:25:00',
    shippedAt: '2024-01-17 09:15:00'
  },
  {
    id: 3,
    orderNo: 'ORD202401170003',
    userId: 1,
    merchantId: 3,
    merchantName: '爱宠世界',
    status: 'paid',
    totalPrice: 114.00,
    items: [
      {
        productId: 6,
        productName: '狗狗飞盘 户外互动玩具 橡胶材质',
        productImage: 'https://picsum.photos/400/400?random=6',
        quantity: 2,
        price: 35.00,
        subtotal: 70.00
      },
      {
        productId: 12,
        productName: '狗狗牵引绳 自动伸缩牵引绳 5米',
        productImage: 'https://picsum.photos/400/400?random=12',
        quantity: 1,
        price: 44.00,
        subtotal: 44.00
      }
    ],
    shippingAddress: {
      receiverName: '李四',
      phone: '13800138002',
      province: '上海市',
      city: '上海市',
      district: '浦东新区',
      detailAddress: '陆家嘴环路1000号恒生银行大厦25楼'
    },
    remark: '周末在家，请周末配送',
    createdAt: '2024-01-17 09:15:00',
    paidAt: '2024-01-17 09:20:00'
  },
  {
    id: 4,
    orderNo: 'ORD202401180004',
    userId: 1,
    merchantId: 1,
    merchantName: '宠物乐园',
    status: 'pending',
    totalPrice: 358.00,
    items: [
      {
        productId: 2,
        productName: '渴望狗粮 无谷鸡肉配方 5kg',
        productImage: 'https://picsum.photos/400/400?random=2',
        quantity: 1,
        price: 358.00,
        subtotal: 358.00
      }
    ],
    shippingAddress: {
      receiverName: '张三',
      phone: '13800138001',
      province: '北京市',
      city: '北京市',
      district: '朝阳区',
      detailAddress: '建国路88号SOHO现代城A座1001室'
    },
    remark: '第一次购买，希望能满意',
    createdAt: '2024-01-18 16:45:00'
  },
  {
    id: 5,
    orderNo: 'ORD202401190005',
    userId: 1,
    merchantId: 2,
    merchantName: '萌宠之家',
    status: 'cancelled',
    totalPrice: 159.00,
    items: [
      {
        productId: 11,
        productName: '猫砂盆 封闭式防臭猫厕所 大号',
        productImage: 'https://picsum.photos/400/400?random=11',
        quantity: 1,
        price: 159.00,
        subtotal: 159.00
      }
    ],
    shippingAddress: {
      receiverName: '王五',
      phone: '13800138003',
      province: '广东省',
      city: '深圳市',
      district: '南山区',
      detailAddress: '科技园南区深南大道9966号威盛科技大厦'
    },
    remark: '取消原因：买错了，需要重新下单',
    createdAt: '2024-01-19 11:30:00',
    cancelledAt: '2024-01-19 12:00:00'
  }
]

export const getOrderById = (id: number): Order | undefined => {
  return orders.find(o => o.id === id)
}

export const getOrderByOrderNo = (orderNo: string): Order | undefined => {
  return orders.find(o => o.orderNo === orderNo)
}

export const getOrdersByStatus = (status: string): Order[] => {
  return orders.filter(o => o.status === status)
}

export const getOrdersByUserId = (userId: number): Order[] => {
  return orders.filter(o => o.userId === userId)
}
