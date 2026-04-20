import type { MockAppointment, MockProductOrder, MockOrderItem, MockOrderAddress } from '../types'
import {
  generateNumericId,
  generateDate,
  generateFutureDate,
  randomAppointmentStatus,
  randomOrderStatus,
  randomPrice,
  randomEnum,
  randomImage,
  randomString
} from '../utils/generators'
import { mockUsers, mockPets } from './user'
import { mockMerchants, mockServices, mockProducts } from './merchant'

export const mockAppointments: MockAppointment[] = [
  {
    id: 1,
    userId: 1,
    userName: 'testuser',
    merchantId: 1,
    merchantName: '宠物乐园1号店',
    serviceId: 1,
    serviceName: '宠物洗澡',
    petId: 1,
    petName: '小白',
    appointmentTime: '2024-12-20 10:00:00',
    totalPrice: 68,
    status: 'confirmed',
    notes: '请给狗狗多梳毛',
    createdAt: '2024-12-18 14:00:00',
    updatedAt: '2024-12-18 14:00:00'
  },
  {
    id: 2,
    userId: 1,
    userName: 'testuser',
    merchantId: 1,
    merchantName: '宠物乐园1号店',
    serviceId: 2,
    serviceName: '宠物美容',
    petId: 2,
    petName: '咪咪',
    appointmentTime: '2024-12-22 14:00:00',
    totalPrice: 128,
    status: 'pending',
    notes: '',
    createdAt: '2024-12-19 09:00:00',
    updatedAt: '2024-12-19 09:00:00'
  },
  {
    id: 3,
    userId: 1,
    userName: 'testuser',
    merchantId: 2,
    merchantName: '爱宠之家旗舰店',
    serviceId: 4,
    serviceName: '宠物SPA',
    petId: 1,
    petName: '小白',
    appointmentTime: '2024-12-15 11:00:00',
    totalPrice: 198,
    status: 'completed',
    notes: '服务很好',
    createdAt: '2024-12-14 10:00:00',
    updatedAt: '2024-12-15 13:00:00'
  },
  {
    id: 4,
    userId: 2,
    userName: 'petlover',
    merchantId: 3,
    merchantName: '萌宠世界体验店',
    serviceId: 6,
    serviceName: '宠物医疗',
    petId: 4,
    petName: '大黄',
    appointmentTime: '2024-12-25 09:00:00',
    totalPrice: 168,
    status: 'pending',
    notes: '定期体检',
    createdAt: '2024-12-20 08:00:00',
    updatedAt: '2024-12-20 08:00:00'
  }
]

const mockOrderItems: MockOrderItem[][] = [
  [
    {
      id: 1,
      productId: 1,
      productName: '优质狗粮',
      productImage: randomImage(100, 100),
      price: 128,
      quantity: 2,
      subtotal: 256
    }
  ],
  [
    {
      id: 2,
      productId: 3,
      productName: '宠物玩具球',
      productImage: randomImage(100, 100),
      price: 28,
      quantity: 3,
      subtotal: 84
    },
    {
      id: 3,
      productId: 4,
      productName: '宠物零食大礼包',
      productImage: randomImage(100, 100),
      price: 68,
      quantity: 1,
      subtotal: 68
    }
  ],
  [
    {
      id: 4,
      productId: 6,
      productName: '智能宠物喂食器',
      productImage: randomImage(100, 100),
      price: 298,
      quantity: 1,
      subtotal: 298
    }
  ]
]

const mockOrderAddresses: MockOrderAddress[] = [
  {
    name: '张三',
    phone: '13800138000',
    province: '北京市',
    city: '北京市',
    district: '朝阳区',
    detailAddress: '建国路88号'
  },
  {
    name: '李四',
    phone: '13900139000',
    province: '上海市',
    city: '上海市',
    district: '浦东新区',
    detailAddress: '南京路100号'
  }
]

export const mockProductOrders: MockProductOrder[] = [
  {
    id: 1,
    orderNo: 'ORD202412180001',
    userId: 1,
    userName: 'testuser',
    merchantId: 1,
    merchantName: '宠物乐园1号店',
    totalPrice: 256,
    freight: 0,
    status: 'completed',
    payMethod: 'alipay',
    payTime: '2024-12-18 10:05:00',
    shipTime: '2024-12-18 14:00:00',
    completeTime: '2024-12-20 15:00:00',
    cancelTime: null,
    remark: '尽快发货',
    shippingAddress: mockOrderAddresses[0],
    items: mockOrderItems[0],
    createdAt: '2024-12-18 10:00:00',
    updatedAt: '2024-12-20 15:00:00'
  },
  {
    id: 2,
    orderNo: 'ORD202412190001',
    userId: 1,
    userName: 'testuser',
    merchantId: 2,
    merchantName: '爱宠之家旗舰店',
    totalPrice: 152,
    freight: 10,
    status: 'shipped',
    payMethod: 'wechat',
    payTime: '2024-12-19 09:05:00',
    shipTime: '2024-12-19 16:00:00',
    completeTime: null,
    cancelTime: null,
    remark: '',
    shippingAddress: mockOrderAddresses[0],
    items: mockOrderItems[1],
    createdAt: '2024-12-19 09:00:00',
    updatedAt: '2024-12-19 16:00:00'
  },
  {
    id: 3,
    orderNo: 'ORD202412200001',
    userId: 1,
    userName: 'testuser',
    merchantId: 3,
    merchantName: '萌宠世界体验店',
    totalPrice: 298,
    freight: 0,
    status: 'paid',
    payMethod: 'alipay',
    payTime: '2024-12-20 11:05:00',
    shipTime: null,
    completeTime: null,
    cancelTime: null,
    remark: '送朋友的',
    shippingAddress: mockOrderAddresses[1],
    items: mockOrderItems[2],
    createdAt: '2024-12-20 11:00:00',
    updatedAt: '2024-12-20 11:05:00'
  },
  {
    id: 4,
    orderNo: 'ORD202412210001',
    userId: 1,
    userName: 'testuser',
    merchantId: 1,
    merchantName: '宠物乐园1号店',
    totalPrice: 128,
    freight: 5,
    status: 'pending',
    payMethod: '',
    payTime: null,
    shipTime: null,
    completeTime: null,
    cancelTime: null,
    remark: '',
    shippingAddress: mockOrderAddresses[0],
    items: [
      {
        id: 5,
        productId: 2,
        productName: '天然猫粮',
        productImage: randomImage(100, 100),
        price: 98,
        quantity: 1,
        subtotal: 98
      }
    ],
    createdAt: '2024-12-21 08:00:00',
    updatedAt: '2024-12-21 08:00:00'
  }
]

export const generateMockAppointment = (userId: number): MockAppointment => {
  const user = mockUsers.find(u => u.id === userId) || mockUsers[0]
  const merchant = mockMerchants[Math.floor(Math.random() * mockMerchants.length)]
  const service = mockServices.filter(s => s.merchantId === merchant.id)[0] || mockServices[0]
  const pet = mockPets.filter(p => p.userId === userId)[0] || mockPets[0]
  
  return {
    id: generateNumericId(),
    userId,
    userName: user.username,
    merchantId: merchant.id,
    merchantName: merchant.name,
    serviceId: service.id,
    serviceName: service.name,
    petId: pet.id,
    petName: pet.name,
    appointmentTime: generateFutureDate(14),
    totalPrice: service.price,
    status: randomAppointmentStatus(),
    notes: '',
    createdAt: generateDate(),
    updatedAt: generateDate()
  }
}

export const generateMockProductOrder = (userId: number): MockProductOrder => {
  const user = mockUsers.find(u => u.id === userId) || mockUsers[0]
  const merchant = mockMerchants[Math.floor(Math.random() * mockMerchants.length)]
  const products = mockProducts.filter(p => p.merchantId === merchant.id)
  const address = mockOrderAddresses[Math.floor(Math.random() * mockOrderAddresses.length)]
  
  const items: MockOrderItem[] = products.slice(0, 2).map((p, i) => ({
    id: generateNumericId(),
    productId: p.id,
    productName: p.name,
    productImage: p.image,
    price: p.price,
    quantity: generateNumericId(1, 3),
    subtotal: p.price * generateNumericId(1, 3)
  }))
  
  const totalPrice = items.reduce((sum, item) => sum + item.subtotal, 0)
  const status = randomOrderStatus()
  
  return {
    id: generateNumericId(),
    orderNo: `ORD${Date.now()}${generateNumericId(1000, 9999)}`,
    userId,
    userName: user.username,
    merchantId: merchant.id,
    merchantName: merchant.name,
    totalPrice,
    freight: totalPrice > 99 ? 0 : 10,
    status,
    payMethod: status === 'pending' ? '' : randomEnum(['alipay', 'wechat']),
    payTime: status !== 'pending' ? generateDate() : null,
    shipTime: status === 'shipped' || status === 'completed' ? generateDate() : null,
    completeTime: status === 'completed' ? generateDate() : null,
    cancelTime: status === 'cancelled' ? generateDate() : null,
    remark: '',
    shippingAddress: address,
    items,
    createdAt: generateDate(),
    updatedAt: generateDate()
  }
}
