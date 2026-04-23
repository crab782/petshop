import { vi } from 'vitest'

export interface MockPet {
  id: number
  name: string
  type: string
  breed?: string
  age?: number
  gender?: string
  avatar?: string
  description?: string
  weight?: number
  bodyType?: string
  furColor?: string
  personality?: string
  userId: number
}

export interface MockAppointment {
  id: number
  userId: number
  serviceId: number
  merchantId: number
  serviceName: string
  merchantName: string
  appointmentTime: string
  status: string
  notes?: string
  totalPrice?: number
  petId?: number
  petName?: string
  createdAt?: string
  updatedAt?: string
}

export interface MockService {
  id: number
  name: string
  description?: string
  price: number
  duration?: number
  image?: string
  merchantId: number
  merchantName: string
  category?: string
  rating?: number
  reviewCount?: number
  status?: 'enabled' | 'disabled'
  createdAt?: string
  updatedAt?: string
}

export interface MockProduct {
  id: number
  name: string
  description?: string
  price: number
  stock: number
  merchantId: number
  merchantName?: string
  image?: string
  category?: string
  sales?: number
  rating?: number
  status?: 'enabled' | 'disabled'
  createdAt?: string
}

export interface MockMerchant {
  id: number
  name: string
  logo?: string
  contact?: string
  contactPerson?: string
  phone?: string
  email?: string
  address?: string
  description?: string
  rating?: number
  reviewCount?: number
  serviceCount?: number
  productCount?: number
  status?: 'pending' | 'approved' | 'rejected' | 'disabled'
  isFavorited?: boolean
  createdAt?: string
  updatedAt?: string
}

export interface MockReview {
  id: number
  userId: number
  merchantId: number
  serviceId: number
  appointmentId: number
  rating: number
  comment: string
  createdAt?: string
}

export interface MockAddress {
  id?: number
  userId?: number
  contactName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: boolean
}

export interface MockCartItem {
  id: number
  productId: number
  productName: string
  productImage?: string
  price: number
  quantity: number
  merchantId: number
  merchantName: string
}

export interface MockProductOrder {
  id: number
  productName: string
  merchantName: string
  quantity: number
  totalPrice: number
  status: string
  orderTime: string
}

export interface MockOrderDetail {
  id: number
  orderNo: string
  status: string
  createTime: string
  payTime?: string
  shipTime?: string
  completeTime?: string
  cancelTime?: string
  totalPrice: number
  freight: number
  payMethod?: string
  remark?: string
  items: MockOrderItem[]
  address: MockOrderAddress
  timeline?: MockOrderTimelineItem[]
  logisticsCompany?: string
  trackingNumber?: string
}

export interface MockOrderItem {
  id: number
  productId: number
  productName: string
  productImage?: string
  price: number
  quantity: number
  subtotal: number
}

export interface MockOrderAddress {
  name: string
  phone: string
  address: string
}

export interface MockOrderTimelineItem {
  status: string
  time: string
  description: string
}

export interface MockFavoriteMerchant {
  id: number
  merchantId: number
  merchantName: string
  merchantLogo: string
  merchantAddress: string
  merchantPhone: string
  createdAt: string
}

export interface MockFavoriteService {
  id: number
  serviceId: number
  serviceName: string
  serviceImage: string
  servicePrice: number
  serviceDuration: number
  merchantId: number
  merchantName: string
  createdAt: string
}

export interface MockNotification {
  id: number
  type: 'system' | 'order' | 'appointment' | 'review'
  title: string
  summary: string
  content: string
  isRead: boolean
  createTime: string
}

export const defaultPet: MockPet = {
  id: 1,
  name: '小白',
  type: 'dog',
  breed: '金毛',
  age: 2,
  gender: 'male',
  avatar: 'https://picsum.photos/100/100?random=10',
  description: '一只可爱的金毛犬',
  weight: 25,
  bodyType: 'medium',
  furColor: 'golden',
  personality: 'friendly',
  userId: 1,
}

export const defaultService: MockService = {
  id: 1,
  name: '宠物洗澡',
  description: '专业宠物洗澡服务',
  price: 99.99,
  duration: 60,
  image: 'https://picsum.photos/200/200?random=11',
  merchantId: 1,
  merchantName: '测试商家',
  category: '美容',
  rating: 4.5,
  reviewCount: 100,
  status: 'enabled',
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
}

export const defaultProduct: MockProduct = {
  id: 1,
  name: '优质狗粮',
  description: '天然有机狗粮',
  price: 199.99,
  stock: 100,
  merchantId: 1,
  merchantName: '测试商家',
  image: 'https://picsum.photos/200/200?random=12',
  category: '食品',
  sales: 500,
  rating: 4.8,
  status: 'enabled',
  createdAt: '2024-01-01T00:00:00.000Z',
}

export const defaultMerchant: MockMerchant = {
  id: 1,
  name: '测试商家',
  logo: 'https://picsum.photos/100/100?random=13',
  contact: '张先生',
  contactPerson: '张先生',
  phone: '13900139000',
  email: 'merchant@example.com',
  address: '北京市朝阳区测试路1号',
  description: '专业宠物服务商家',
  rating: 4.5,
  reviewCount: 100,
  serviceCount: 10,
  productCount: 20,
  status: 'approved',
  isFavorited: false,
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
}

export const defaultAppointment: MockAppointment = {
  id: 1,
  userId: 1,
  serviceId: 1,
  merchantId: 1,
  serviceName: '宠物洗澡',
  merchantName: '测试商家',
  appointmentTime: '2024-05-01T14:00:00.000Z',
  status: 'pending',
  notes: '请准时',
  totalPrice: 99.99,
  petId: 1,
  petName: '小白',
  createdAt: '2024-04-20T10:00:00.000Z',
  updatedAt: '2024-04-20T10:00:00.000Z',
}

export const defaultReview: MockReview = {
  id: 1,
  userId: 1,
  merchantId: 1,
  serviceId: 1,
  appointmentId: 1,
  rating: 5,
  comment: '服务非常好，推荐！',
  createdAt: '2024-04-21T10:00:00.000Z',
}

export const defaultAddress: MockAddress = {
  id: 1,
  userId: 1,
  contactName: '张三',
  phone: '13800138000',
  province: '北京市',
  city: '北京市',
  district: '朝阳区',
  detailAddress: '测试路1号',
  isDefault: true,
}

export const defaultCartItem: MockCartItem = {
  id: 1,
  productId: 1,
  productName: '优质狗粮',
  productImage: 'https://picsum.photos/100/100?random=14',
  price: 199.99,
  quantity: 2,
  merchantId: 1,
  merchantName: '测试商家',
}

export const defaultProductOrder: MockProductOrder = {
  id: 1,
  productName: '优质狗粮',
  merchantName: '测试商家',
  quantity: 2,
  totalPrice: 399.98,
  status: 'pending',
  orderTime: '2024-04-20T10:00:00.000Z',
}

export const defaultOrderDetail: MockOrderDetail = {
  id: 1,
  orderNo: 'ORD202404200001',
  status: 'pending',
  createTime: '2024-04-20T10:00:00.000Z',
  totalPrice: 399.98,
  freight: 10,
  payMethod: 'wechat',
  remark: '请尽快发货',
  items: [
    {
      id: 1,
      productId: 1,
      productName: '优质狗粮',
      productImage: 'https://picsum.photos/100/100?random=15',
      price: 199.99,
      quantity: 2,
      subtotal: 399.98,
    },
  ],
  address: {
    name: '张三',
    phone: '13800138000',
    address: '北京市朝阳区测试路1号',
  },
  timeline: [
    {
      status: 'created',
      time: '2024-04-20T10:00:00.000Z',
      description: '订单创建',
    },
  ],
}

export const defaultFavoriteMerchant: MockFavoriteMerchant = {
  id: 1,
  merchantId: 1,
  merchantName: '测试商家',
  merchantLogo: 'https://picsum.photos/100/100?random=16',
  merchantAddress: '北京市朝阳区测试路1号',
  merchantPhone: '13900139000',
  createdAt: '2024-04-20T10:00:00.000Z',
}

export const defaultFavoriteService: MockFavoriteService = {
  id: 1,
  serviceId: 1,
  serviceName: '宠物洗澡',
  serviceImage: 'https://picsum.photos/100/100?random=17',
  servicePrice: 99.99,
  serviceDuration: 60,
  merchantId: 1,
  merchantName: '测试商家',
  createdAt: '2024-04-20T10:00:00.000Z',
}

export const defaultNotification: MockNotification = {
  id: 1,
  type: 'system',
  title: '系统通知',
  summary: '欢迎使用宠物服务平台',
  content: '欢迎使用宠物服务平台，祝您使用愉快！',
  isRead: false,
  createTime: '2024-04-20T10:00:00.000Z',
}

export const createPet = (overrides: Partial<MockPet> = {}): MockPet => ({
  ...defaultPet,
  ...overrides,
})

export const createService = (overrides: Partial<MockService> = {}): MockService => ({
  ...defaultService,
  ...overrides,
})

export const createProduct = (overrides: Partial<MockProduct> = {}): MockProduct => ({
  ...defaultProduct,
  ...overrides,
})

export const createMerchant = (overrides: Partial<MockMerchant> = {}): MockMerchant => ({
  ...defaultMerchant,
  ...overrides,
})

export const createAppointment = (overrides: Partial<MockAppointment> = {}): MockAppointment => ({
  ...defaultAppointment,
  ...overrides,
})

export const createReview = (overrides: Partial<MockReview> = {}): MockReview => ({
  ...defaultReview,
  ...overrides,
})

export const createAddress = (overrides: Partial<MockAddress> = {}): MockAddress => ({
  ...defaultAddress,
  ...overrides,
})

export const createCartItem = (overrides: Partial<MockCartItem> = {}): MockCartItem => ({
  ...defaultCartItem,
  ...overrides,
})

export const createProductOrder = (overrides: Partial<MockProductOrder> = {}): MockProductOrder => ({
  ...defaultProductOrder,
  ...overrides,
})

export const createOrderDetail = (overrides: Partial<MockOrderDetail> = {}): MockOrderDetail => ({
  ...defaultOrderDetail,
  ...overrides,
})

export const createFavoriteMerchant = (overrides: Partial<MockFavoriteMerchant> = {}): MockFavoriteMerchant => ({
  ...defaultFavoriteMerchant,
  ...overrides,
})

export const createFavoriteService = (overrides: Partial<MockFavoriteService> = {}): MockFavoriteService => ({
  ...defaultFavoriteService,
  ...overrides,
})

export const createNotification = (overrides: Partial<MockNotification> = {}): MockNotification => ({
  ...defaultNotification,
  ...overrides,
})

export const createPetList = (count: number): MockPet[] =>
  Array.from({ length: count }, (_, i) => createPet({ id: i + 1, name: `宠物${i + 1}` }))

export const createServiceList = (count: number): MockService[] =>
  Array.from({ length: count }, (_, i) => createService({ id: i + 1, name: `服务${i + 1}` }))

export const createProductList = (count: number): MockProduct[] =>
  Array.from({ length: count }, (_, i) => createProduct({ id: i + 1, name: `商品${i + 1}` }))

export const createMerchantList = (count: number): MockMerchant[] =>
  Array.from({ length: count }, (_, i) => createMerchant({ id: i + 1, name: `商家${i + 1}` }))

export const createAppointmentList = (count: number): MockAppointment[] =>
  Array.from({ length: count }, (_, i) => createAppointment({ id: i + 1 }))

export const createReviewList = (count: number): MockReview[] =>
  Array.from({ length: count }, (_, i) => createReview({ id: i + 1, rating: Math.min(5, Math.floor(Math.random() * 5) + 1) }))

export const createAddressList = (count: number): MockAddress[] =>
  Array.from({ length: count }, (_, i) => createAddress({ id: i + 1, isDefault: i === 0 }))

export const createCartItemList = (count: number): MockCartItem[] =>
  Array.from({ length: count }, (_, i) => createCartItem({ id: i + 1, productId: i + 1 }))

export const createProductOrderList = (count: number): MockProductOrder[] =>
  Array.from({ length: count }, (_, i) => createProductOrder({ id: i + 1 }))

export const createNotificationList = (count: number): MockNotification[] =>
  Array.from({ length: count }, (_, i) => createNotification({ id: i + 1, isRead: i > 0 }))

export const mockUserApi = {
  getUserPets: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createPetList(3),
    })
  ),

  getPetById: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createPet({ id }),
    })
  ),

  addPet: vi.fn((data: Omit<MockPet, 'id'>) =>
    Promise.resolve({
      code: 200,
      message: '添加成功',
      data: createPet({ ...data, id: Date.now() }),
    })
  ),

  updatePet: vi.fn((id: number, data: Partial<MockPet>) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createPet({ id, ...data }),
    })
  ),

  deletePet: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  getUserAppointments: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        total: 20,
        data: createAppointmentList(10),
        totalPages: 2,
        pageSize: 10,
        page: 1,
      },
    })
  ),

  cancelAppointment: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '取消成功',
      data: null,
    })
  ),

  createAppointment: vi.fn((data: any) =>
    Promise.resolve({
      code: 200,
      message: '预约成功',
      data: createAppointment({ id: Date.now(), ...data }),
    })
  ),

  getServices: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createServiceList(10),
    })
  ),

  getServiceById: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createService({ id }),
    })
  ),

  getMerchantInfo: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createMerchant({ id }),
    })
  ),

  getMerchantServices: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createServiceList(5),
    })
  ),

  getMerchantReviews: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createReviewList(5),
    })
  ),

  addFavorite: vi.fn((merchantId: number) =>
    Promise.resolve({
      code: 200,
      message: '收藏成功',
      data: { id: Date.now(), userId: 1, merchantId, createTime: new Date().toISOString() },
    })
  ),

  getFavorites: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: [createFavoriteMerchant()],
    })
  ),

  removeFavorite: vi.fn((merchantId: number) =>
    Promise.resolve({
      code: 200,
      message: '取消收藏成功',
      data: null,
    })
  ),

  addServiceFavorite: vi.fn((serviceId: number) =>
    Promise.resolve({
      code: 200,
      message: '收藏成功',
      data: null,
    })
  ),

  getServiceFavorites: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: [createFavoriteService()],
    })
  ),

  removeServiceFavorite: vi.fn((serviceId: number) =>
    Promise.resolve({
      code: 200,
      message: '取消收藏成功',
      data: null,
    })
  ),

  getProducts: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createProductList(10),
    })
  ),

  getProductById: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createProduct({ id }),
    })
  ),

  getProductReviews: vi.fn((productId: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createReviewList(5),
    })
  ),

  addProductFavorite: vi.fn((productId: number) =>
    Promise.resolve({
      code: 200,
      message: '收藏成功',
      data: null,
    })
  ),

  removeProductFavorite: vi.fn((productId: number) =>
    Promise.resolve({
      code: 200,
      message: '取消收藏成功',
      data: null,
    })
  ),

  checkProductFavorite: vi.fn((productId: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: { isFavorited: false },
    })
  ),

  addToCart: vi.fn((data: { productId: number; quantity: number }) =>
    Promise.resolve({
      code: 200,
      message: '添加成功',
      data: null,
    })
  ),

  getCart: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createCartItemList(3),
    })
  ),

  updateCartItem: vi.fn((cartId: number, quantity: number) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: null,
    })
  ),

  removeFromCart: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  batchRemoveFromCart: vi.fn((ids: number[]) =>
    Promise.resolve({
      code: 200,
      message: '批量删除成功',
      data: null,
    })
  ),

  getAddresses: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAddressList(2),
    })
  ),

  addAddress: vi.fn((data: Omit<MockAddress, 'id'>) =>
    Promise.resolve({
      code: 200,
      message: '添加成功',
      data: createAddress({ ...data, id: Date.now() }),
    })
  ),

  updateAddress: vi.fn((id: number, data: Partial<MockAddress>) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createAddress({ id, ...data }),
    })
  ),

  deleteAddress: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  setDefaultAddress: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '设置成功',
      data: null,
    })
  ),

  addReview: vi.fn((data: MockReview) =>
    Promise.resolve({
      code: 200,
      message: '评价成功',
      data: createReview({ id: Date.now(), ...data }),
    })
  ),

  getUserReviews: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createReviewList(5),
    })
  ),

  updateReview: vi.fn((id: number, data: { rating: number; comment: string }) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createReview({ id, ...data }),
    })
  ),

  deleteReview: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  getProductOrders: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        total: 20,
        data: createProductOrderList(10),
        totalPages: 2,
        pageSize: 10,
        page: 1,
      },
    })
  ),

  getOrderById: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createOrderDetail({ id }),
    })
  ),

  cancelOrder: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '取消成功',
      data: null,
    })
  ),

  refundOrder: vi.fn((id: number, reason: string) =>
    Promise.resolve({
      code: 200,
      message: '退款申请已提交',
      data: null,
    })
  ),

  confirmReceiveOrder: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '确认收货成功',
      data: null,
    })
  ),

  deleteOrder: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  batchCancelOrders: vi.fn((ids: number[]) =>
    Promise.resolve({
      code: 200,
      message: '批量取消成功',
      data: null,
    })
  ),

  batchDeleteOrders: vi.fn((ids: number[]) =>
    Promise.resolve({
      code: 200,
      message: '批量删除成功',
      data: null,
    })
  ),

  payOrder: vi.fn((orderId: number, payMethod: string) =>
    Promise.resolve({
      code: 200,
      message: '支付成功',
      data: null,
    })
  ),

  createOrder: vi.fn((data: { productId: number; addressId: number; quantity: number }) =>
    Promise.resolve({
      code: 200,
      message: '下单成功',
      data: { orderId: Date.now() },
    })
  ),

  purchaseProduct: vi.fn((data: { productId: number; addressId: number; quantity: number }) =>
    Promise.resolve({
      code: 200,
      message: '下单成功',
      data: { orderId: Date.now() },
    })
  ),

  previewOrder: vi.fn((items: { productId: number; quantity: number }[]) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        items: items.map((item, i) => ({
          productId: item.productId,
          productName: `商品${item.productId}`,
          productImage: `https://picsum.photos/100/100?random=${i}`,
          price: 99.99,
          quantity: item.quantity,
          subtotal: 99.99 * item.quantity,
        })),
        productTotal: items.reduce((sum, item) => sum + 99.99 * item.quantity, 0),
        shippingFee: 10,
        discount: 0,
        totalAmount: items.reduce((sum, item) => sum + 99.99 * item.quantity, 0) + 10,
      },
    })
  ),

  getPayStatus: vi.fn((orderId: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        orderId,
        payStatus: 'success',
        payTime: new Date().toISOString(),
        transactionId: 'TXN' + Date.now(),
      },
    })
  ),

  getMerchantProducts: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createProductList(5),
    })
  ),

  getAvailableSlots: vi.fn((merchantId: number, date: string) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        date,
        slots: [
          { time: '09:00', available: true },
          { time: '10:00', available: true },
          { time: '11:00', available: false },
          { time: '14:00', available: true },
          { time: '15:00', available: true },
          { time: '16:00', available: false },
        ],
        workingHours: { start: '09:00', end: '18:00' },
      },
    })
  ),

  getMerchantList: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        data: createMerchantList(10),
        total: 50,
      },
    })
  ),

  getUserReviewsList: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        data: createReviewList(10),
        total: 20,
      },
    })
  ),

  getHomeStats: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        petCount: 3,
        pendingAppointments: 2,
        reviewCount: 5,
      },
    })
  ),

  getRecentActivities: vi.fn((limit?: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: [
        { id: 1, type: 'appointment', title: '预约了宠物洗澡', time: '2024-04-20T10:00:00.000Z', status: 'pending', statusColor: 'warning' },
        { id: 2, type: 'review', title: '评价了服务', time: '2024-04-19T10:00:00.000Z', status: 'completed', statusColor: 'success' },
        { id: 3, type: 'order', title: '购买了商品', time: '2024-04-18T10:00:00.000Z', status: 'completed', statusColor: 'success' },
      ],
    })
  ),

  getRecommendedServices: vi.fn((limit?: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createServiceList(5),
    })
  ),

  getAppointmentById: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        id,
        serviceId: 1,
        serviceName: '宠物洗澡',
        servicePrice: 99.99,
        serviceDuration: 60,
        merchantId: 1,
        merchantName: '测试商家',
        merchantPhone: '13900139000',
        merchantAddress: '北京市朝阳区测试路1号',
        petId: 1,
        petName: '小白',
        petType: 'dog',
        appointmentTime: '2024-05-01T14:00:00.000Z',
        status: 'pending',
        totalPrice: 99.99,
        remark: '请准时',
        createdAt: '2024-04-20T10:00:00.000Z',
        updatedAt: '2024-04-20T10:00:00.000Z',
      },
    })
  ),

  getAppointmentStats: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        total: 20,
        pending: 5,
        confirmed: 10,
        completed: 3,
        cancelled: 2,
      },
    })
  ),

  getUserPurchasedServices: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        data: [
          {
            id: 1,
            name: '宠物洗澡套餐',
            merchant: '测试商家',
            merchantId: 1,
            price: 199.99,
            purchaseDate: '2024-04-01T00:00:00.000Z',
            expiryDate: '2024-07-01T00:00:00.000Z',
            status: 'active',
            category: '美容',
            serviceId: 1,
          },
        ],
        total: 1,
      },
    })
  ),

  searchMerchants: vi.fn((keyword: string) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createMerchantList(5),
    })
  ),
}

export const mockUserResponses = {
  petList: {
    code: 200,
    message: 'success',
    data: [defaultPet],
  },
  serviceList: {
    code: 200,
    message: 'success',
    data: [defaultService],
  },
  productList: {
    code: 200,
    message: 'success',
    data: [defaultProduct],
  },
  merchantList: {
    code: 200,
    message: 'success',
    data: [defaultMerchant],
  },
  appointmentList: {
    code: 200,
    message: 'success',
    data: {
      total: 20,
      data: [defaultAppointment],
      totalPages: 2,
      pageSize: 10,
      page: 1,
    },
  },
  reviewList: {
    code: 200,
    message: 'success',
    data: [defaultReview],
  },
  addressList: {
    code: 200,
    message: 'success',
    data: [defaultAddress],
  },
  cartList: {
    code: 200,
    message: 'success',
    data: [defaultCartItem],
  },
  orderList: {
    code: 200,
    message: 'success',
    data: {
      total: 20,
      data: [defaultProductOrder],
      totalPages: 2,
      pageSize: 10,
      page: 1,
    },
  },
  orderDetail: {
    code: 200,
    message: 'success',
    data: defaultOrderDetail,
  },
  notificationList: {
    code: 200,
    message: 'success',
    data: [defaultNotification],
  },
  homeStats: {
    code: 200,
    message: 'success',
    data: {
      petCount: 3,
      pendingAppointments: 2,
      reviewCount: 5,
    },
  },
}
