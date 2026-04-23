import { vi } from 'vitest'

export interface MockMerchantService {
  id: number
  name: string
  description?: string
  price: number
  duration?: number
  category?: string
  image?: string
  status?: 'enabled' | 'disabled'
  createdAt?: string
}

export interface MockMerchantProduct {
  id: number
  name: string
  description?: string
  price: number
  stock: number
  lowStockThreshold?: number
  merchantId: number
  image?: string
  category?: string
  status: 'enabled' | 'disabled'
  createdAt?: string
  updatedAt?: string
}

export interface MockMerchantAppointment {
  id: number
  userId: number
  userName: string
  serviceId: number
  serviceName: string
  merchantId: number
  petName: string
  appointmentTime: string
  status: 'pending' | 'confirmed' | 'completed' | 'cancelled'
  remark?: string
  totalPrice: number
}

export interface MockMerchantReview {
  id: number
  userId: number
  userName: string
  userAvatar?: string
  serviceId: number
  serviceName: string
  orderId: number
  rating: number
  content: string
  reviewTime: string
  replyStatus: 'replied' | 'pending'
  replyContent?: string
  replyTime?: string
}

export interface MockMerchantCategory {
  id: number
  name: string
  icon?: string
  description?: string
  sort: number
  status: 'enabled' | 'disabled'
  productCount: number
  createdAt?: string
}

export interface MockMerchantProductOrder {
  id: number
  userId: number
  userName: string
  productId: number
  productName: string
  quantity: number
  totalPrice: number
  status: string
  address?: string
  phone?: string
  receiverName?: string
  createTime: string
  trackingNumber?: string
  logisticsCompany?: string
}

export interface MockMerchantInfo {
  id: number
  name: string
  contactPerson?: string
  phone?: string
  email?: string
  address?: string
  logo?: string
  description?: string
  status?: 'pending' | 'approved' | 'rejected'
  createdAt?: string
}

export interface MockMerchantSettings {
  businessDays: number[]
  startTime: string
  endTime: string
  legalHolidayRest: boolean
  customRestDays: string[]
  maxAppointments: number
  advanceBookingHours: number
  isOpen: boolean
  notificationSettings: {
    appointmentReminder: boolean
    orderReminder: boolean
    reviewReminder: boolean
    notifyViaSms: boolean
    notifyViaEmail: boolean
  }
}

export interface MockDashboardStats {
  todayOrders: number
  pendingAppointments: number
  todayRevenue: number
  avgRating: number
  orderGrowth: number
  revenueGrowth: number
  ratingCount: number
}

export interface MockRecentOrder {
  id: number
  customerName: string
  serviceName: string
  status: 'pending' | 'confirmed' | 'completed' | 'cancelled'
  appointmentTime: string
  totalPrice: number
}

export interface MockRecentReview {
  id: number
  userName: string
  userAvatar?: string
  rating: number
  content: string
  serviceName: string
  reviewTime: string
}

export interface MockRevenueStats {
  totalRevenue: number
  orderCount: number
  avgOrderValue: number
  lastPeriodRevenue: number
  growthRate: number
  serviceRevenue: number
  productRevenue: number
  serviceOrderCount: number
  productOrderCount: number
  orderList: MockRevenueOrderItem[]
  topServices: MockTopServiceItem[]
  topProducts: MockTopProductItem[]
}

export interface MockRevenueOrderItem {
  id: number
  date: string
  serviceAmount: number
  productAmount: number
  totalAmount: number
}

export interface MockTopServiceItem {
  id: number
  name: string
  orderCount: number
  revenue: number
}

export interface MockTopProductItem {
  id: number
  name: string
  orderCount: number
  revenue: number
}

export interface MockAppointmentStats {
  totalCount: number
  pendingCount: number
  completedCount: number
  cancelledCount: number
  growthRate?: number
  trendData: { date: string; count: number }[]
  sourceData: { name: string; value: number }[]
  hotServices: { name: string; count: number }[]
  dailyStats?: Record<string, { completedCount: number; cancelledCount: number; totalRevenue: number }>
  hourData?: { hour: number; count: number }[]
}

export const defaultMerchantService: MockMerchantService = {
  id: 1,
  name: '宠物洗澡',
  description: '专业宠物洗澡服务',
  price: 99.99,
  duration: 60,
  category: '美容',
  image: 'https://picsum.photos/200/200?random=20',
  status: 'enabled',
  createdAt: '2024-01-01T00:00:00.000Z',
}

export const defaultMerchantProduct: MockMerchantProduct = {
  id: 1,
  name: '优质狗粮',
  description: '天然有机狗粮',
  price: 199.99,
  stock: 100,
  lowStockThreshold: 10,
  merchantId: 1,
  image: 'https://picsum.photos/200/200?random=21',
  category: '食品',
  status: 'enabled',
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
}

export const defaultMerchantAppointment: MockMerchantAppointment = {
  id: 1,
  userId: 1,
  userName: '测试用户',
  serviceId: 1,
  serviceName: '宠物洗澡',
  merchantId: 1,
  petName: '小白',
  appointmentTime: '2024-05-01T14:00:00.000Z',
  status: 'pending',
  remark: '请准时',
  totalPrice: 99.99,
}

export const defaultMerchantReview: MockMerchantReview = {
  id: 1,
  userId: 1,
  userName: '测试用户',
  userAvatar: 'https://picsum.photos/100/100?random=22',
  serviceId: 1,
  serviceName: '宠物洗澡',
  orderId: 1,
  rating: 5,
  content: '服务非常好，推荐！',
  reviewTime: '2024-04-21T10:00:00.000Z',
  replyStatus: 'pending',
}

export const defaultMerchantCategory: MockMerchantCategory = {
  id: 1,
  name: '宠物食品',
  icon: 'food',
  description: '各类宠物食品',
  sort: 1,
  status: 'enabled',
  productCount: 10,
  createdAt: '2024-01-01T00:00:00.000Z',
}

export const defaultMerchantProductOrder: MockMerchantProductOrder = {
  id: 1,
  userId: 1,
  userName: '测试用户',
  productId: 1,
  productName: '优质狗粮',
  quantity: 2,
  totalPrice: 399.98,
  status: 'pending',
  address: '北京市朝阳区测试路1号',
  phone: '13800138000',
  receiverName: '张三',
  createTime: '2024-04-20T10:00:00.000Z',
}

export const defaultMerchantInfo: MockMerchantInfo = {
  id: 1,
  name: '测试商家',
  contactPerson: '张先生',
  phone: '13900139000',
  email: 'merchant@example.com',
  address: '北京市朝阳区测试路1号',
  logo: 'https://picsum.photos/100/100?random=23',
  description: '专业宠物服务商家',
  status: 'approved',
  createdAt: '2024-01-01T00:00:00.000Z',
}

export const defaultMerchantSettings: MockMerchantSettings = {
  businessDays: [1, 2, 3, 4, 5],
  startTime: '09:00',
  endTime: '18:00',
  legalHolidayRest: true,
  customRestDays: [],
  maxAppointments: 10,
  advanceBookingHours: 24,
  isOpen: true,
  notificationSettings: {
    appointmentReminder: true,
    orderReminder: true,
    reviewReminder: true,
    notifyViaSms: false,
    notifyViaEmail: true,
  },
}

export const defaultDashboardStats: MockDashboardStats = {
  todayOrders: 15,
  pendingAppointments: 5,
  todayRevenue: 2500.00,
  avgRating: 4.8,
  orderGrowth: 12.5,
  revenueGrowth: 8.3,
  ratingCount: 120,
}

export const defaultRecentOrder: MockRecentOrder = {
  id: 1,
  customerName: '测试用户',
  serviceName: '宠物洗澡',
  status: 'pending',
  appointmentTime: '2024-05-01T14:00:00.000Z',
  totalPrice: 99.99,
}

export const defaultRecentReview: MockRecentReview = {
  id: 1,
  userName: '测试用户',
  userAvatar: 'https://picsum.photos/100/100?random=24',
  rating: 5,
  content: '服务非常好！',
  serviceName: '宠物洗澡',
  reviewTime: '2024-04-21T10:00:00.000Z',
}

export const defaultRevenueStats: MockRevenueStats = {
  totalRevenue: 50000,
  orderCount: 200,
  avgOrderValue: 250,
  lastPeriodRevenue: 45000,
  growthRate: 11.1,
  serviceRevenue: 30000,
  productRevenue: 20000,
  serviceOrderCount: 150,
  productOrderCount: 50,
  orderList: [
    { id: 1, date: '2024-04-20', serviceAmount: 1500, productAmount: 500, totalAmount: 2000 },
    { id: 2, date: '2024-04-19', serviceAmount: 1800, productAmount: 700, totalAmount: 2500 },
  ],
  topServices: [
    { id: 1, name: '宠物洗澡', orderCount: 50, revenue: 5000 },
    { id: 2, name: '宠物美容', orderCount: 30, revenue: 4500 },
  ],
  topProducts: [
    { id: 1, name: '优质狗粮', orderCount: 100, revenue: 10000 },
    { id: 2, name: '宠物玩具', orderCount: 80, revenue: 4000 },
  ],
}

export const defaultAppointmentStats: MockAppointmentStats = {
  totalCount: 200,
  pendingCount: 20,
  completedCount: 150,
  cancelledCount: 30,
  growthRate: 15.5,
  trendData: [
    { date: '2024-04-20', count: 15 },
    { date: '2024-04-19', count: 12 },
    { date: '2024-04-18', count: 18 },
  ],
  sourceData: [
    { name: '小程序', value: 60 },
    { name: 'APP', value: 30 },
    { name: '网页', value: 10 },
  ],
  hotServices: [
    { name: '宠物洗澡', count: 50 },
    { name: '宠物美容', count: 40 },
    { name: '宠物寄养', count: 30 },
  ],
  hourData: [
    { hour: 9, count: 5 },
    { hour: 10, count: 8 },
    { hour: 11, count: 10 },
    { hour: 14, count: 12 },
    { hour: 15, count: 15 },
    { hour: 16, count: 10 },
  ],
}

export const createMerchantService = (overrides: Partial<MockMerchantService> = {}): MockMerchantService => ({
  ...defaultMerchantService,
  ...overrides,
})

export const createMerchantProduct = (overrides: Partial<MockMerchantProduct> = {}): MockMerchantProduct => ({
  ...defaultMerchantProduct,
  ...overrides,
})

export const createMerchantAppointment = (overrides: Partial<MockMerchantAppointment> = {}): MockMerchantAppointment => ({
  ...defaultMerchantAppointment,
  ...overrides,
})

export const createMerchantReview = (overrides: Partial<MockMerchantReview> = {}): MockMerchantReview => ({
  ...defaultMerchantReview,
  ...overrides,
})

export const createMerchantCategory = (overrides: Partial<MockMerchantCategory> = {}): MockMerchantCategory => ({
  ...defaultMerchantCategory,
  ...overrides,
})

export const createMerchantProductOrder = (overrides: Partial<MockMerchantProductOrder> = {}): MockMerchantProductOrder => ({
  ...defaultMerchantProductOrder,
  ...overrides,
})

export const createMerchantInfo = (overrides: Partial<MockMerchantInfo> = {}): MockMerchantInfo => ({
  ...defaultMerchantInfo,
  ...overrides,
})

export const createMerchantSettings = (overrides: Partial<MockMerchantSettings> = {}): MockMerchantSettings => ({
  ...defaultMerchantSettings,
  ...overrides,
})

export const createDashboardStats = (overrides: Partial<MockDashboardStats> = {}): MockDashboardStats => ({
  ...defaultDashboardStats,
  ...overrides,
})

export const createRecentOrder = (overrides: Partial<MockRecentOrder> = {}): MockRecentOrder => ({
  ...defaultRecentOrder,
  ...overrides,
})

export const createRecentReview = (overrides: Partial<MockRecentReview> = {}): MockRecentReview => ({
  ...defaultRecentReview,
  ...overrides,
})

export const createRevenueStats = (overrides: Partial<MockRevenueStats> = {}): MockRevenueStats => ({
  ...defaultRevenueStats,
  ...overrides,
})

export const createAppointmentStats = (overrides: Partial<MockAppointmentStats> = {}): MockAppointmentStats => ({
  ...defaultAppointmentStats,
  ...overrides,
})

export const createMerchantServiceList = (count: number): MockMerchantService[] =>
  Array.from({ length: count }, (_, i) => createMerchantService({ id: i + 1, name: `服务${i + 1}` }))

export const createMerchantProductList = (count: number): MockMerchantProduct[] =>
  Array.from({ length: count }, (_, i) => createMerchantProduct({ id: i + 1, name: `商品${i + 1}` }))

export const createMerchantAppointmentList = (count: number): MockMerchantAppointment[] =>
  Array.from({ length: count }, (_, i) => createMerchantAppointment({ id: i + 1 }))

export const createMerchantReviewList = (count: number): MockMerchantReview[] =>
  Array.from({ length: count }, (_, i) => createMerchantReview({ id: i + 1, rating: Math.min(5, Math.floor(Math.random() * 5) + 1) }))

export const createMerchantCategoryList = (count: number): MockMerchantCategory[] =>
  Array.from({ length: count }, (_, i) => createMerchantCategory({ id: i + 1, name: `分类${i + 1}` }))

export const createMerchantProductOrderList = (count: number): MockMerchantProductOrder[] =>
  Array.from({ length: count }, (_, i) => createMerchantProductOrder({ id: i + 1 }))

export const mockMerchantApi = {
  getMerchantInfo: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createMerchantInfo(),
    })
  ),

  updateMerchantInfo: vi.fn((data: Partial<MockMerchantInfo>) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createMerchantInfo(data),
    })
  ),

  getMerchantServices: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createMerchantServiceList(10),
    })
  ),

  addService: vi.fn((data: Omit<MockMerchantService, 'id'>) =>
    Promise.resolve({
      code: 200,
      message: '添加成功',
      data: createMerchantService({ id: Date.now(), ...data }),
    })
  ),

  updateService: vi.fn((id: number, data: Partial<MockMerchantService>) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createMerchantService({ id, ...data }),
    })
  ),

  deleteService: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  getServiceById: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createMerchantService({ id }),
    })
  ),

  batchUpdateServiceStatus: vi.fn((ids: number[], status: 'enabled' | 'disabled') =>
    Promise.resolve({
      code: 200,
      message: '批量更新成功',
      data: createMerchantServiceList(ids.length).map((s, i) => ({ ...s, id: ids[i], status })),
    })
  ),

  batchDeleteServices: vi.fn((ids: number[]) =>
    Promise.resolve({
      code: 200,
      message: '批量删除成功',
      data: null,
    })
  ),

  getMerchantProducts: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createMerchantProductList(10),
    })
  ),

  getMerchantProductsPaged: vi.fn((query: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        list: createMerchantProductList(10),
        total: 50,
        page: query.page || 1,
        pageSize: query.pageSize || 10,
      },
    })
  ),

  addProduct: vi.fn((data: Omit<MockMerchantProduct, 'id'>) =>
    Promise.resolve({
      code: 200,
      message: '添加成功',
      data: createMerchantProduct({ id: Date.now(), ...data }),
    })
  ),

  updateProduct: vi.fn((id: number, data: Partial<MockMerchantProduct>) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createMerchantProduct({ id, ...data }),
    })
  ),

  deleteProduct: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  getProductById: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createMerchantProduct({ id }),
    })
  ),

  updateProductStatus: vi.fn((id: number, status: 'enabled' | 'disabled') =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createMerchantProduct({ id, status }),
    })
  ),

  batchUpdateProductStatus: vi.fn((ids: number[], status: 'enabled' | 'disabled') =>
    Promise.resolve({
      code: 200,
      message: '批量更新成功',
      data: createMerchantProductList(ids.length).map((p, i) => ({ ...p, id: ids[i], status })),
    })
  ),

  batchDeleteProducts: vi.fn((ids: number[]) =>
    Promise.resolve({
      code: 200,
      message: '批量删除成功',
      data: null,
    })
  ),

  getMerchantAppointments: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createMerchantAppointmentList(10),
    })
  ),

  updateAppointmentStatus: vi.fn((id: number, status: string, rejectReason?: string) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createMerchantAppointment({ id, status: status as any }),
    })
  ),

  getMerchantProductOrders: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createMerchantProductOrderList(10),
    })
  ),

  updateProductOrderStatus: vi.fn((id: number, status: string) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createMerchantProductOrder({ id, status }),
    })
  ),

  updateProductOrderLogistics: vi.fn((id: number, logisticsCompany: string, trackingNumber: string) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createMerchantProductOrder({ id, logisticsCompany, trackingNumber }),
    })
  ),

  getMerchantReviews: vi.fn((query?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        list: createMerchantReviewList(10),
        total: 50,
        page: query?.page || 1,
        pageSize: query?.pageSize || 10,
        ratingDistribution: {
          five: 30,
          four: 25,
          three: 20,
          two: 15,
          one: 10,
        },
      },
    })
  ),

  replyReview: vi.fn((id: number, replyContent: string) =>
    Promise.resolve({
      code: 200,
      message: '回复成功',
      data: createMerchantReview({ id, replyStatus: 'replied', replyContent, replyTime: new Date().toISOString() }),
    })
  ),

  deleteReview: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  getCategories: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createMerchantCategoryList(5),
    })
  ),

  addCategory: vi.fn((data: Omit<MockMerchantCategory, 'id' | 'productCount' | 'createdAt'>) =>
    Promise.resolve({
      code: 200,
      message: '添加成功',
      data: createMerchantCategory({ id: Date.now(), ...data, productCount: 0 }),
    })
  ),

  updateCategory: vi.fn((id: number, data: Partial<MockMerchantCategory>) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createMerchantCategory({ id, ...data }),
    })
  ),

  deleteCategory: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  updateCategoryStatus: vi.fn((id: number, status: 'enabled' | 'disabled') =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createMerchantCategory({ id, status }),
    })
  ),

  batchUpdateCategoryStatus: vi.fn((ids: number[], status: 'enabled' | 'disabled') =>
    Promise.resolve({
      code: 200,
      message: '批量更新成功',
      data: createMerchantCategoryList(ids.length).map((c, i) => ({ ...c, id: ids[i], status })),
    })
  ),

  batchDeleteCategories: vi.fn((ids: number[]) =>
    Promise.resolve({
      code: 200,
      message: '批量删除成功',
      data: null,
    })
  ),

  getMerchantRevenueStats: vi.fn((query: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createRevenueStats(),
    })
  ),

  exportRevenueStats: vi.fn((query: any) =>
    Promise.resolve(new Blob(['mock excel data'], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }))
  ),

  getMerchantAppointmentStats: vi.fn((timeRange: string, startDate?: string, endDate?: string) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAppointmentStats(),
    })
  ),

  exportAppointmentStats: vi.fn((timeRange: string, startDate?: string, endDate?: string) =>
    Promise.resolve(new Blob(['mock excel data'], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }))
  ),

  getMerchantSettings: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createMerchantSettings(),
    })
  ),

  updateMerchantSettings: vi.fn((data: MockMerchantSettings) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createMerchantSettings(data),
    })
  ),

  changePassword: vi.fn((data: { oldPassword: string; newPassword: string }) => {
    if (data.oldPassword === 'wrongpassword') {
      return Promise.resolve({
        code: 400,
        message: '原密码错误',
        data: null,
      })
    }
    return Promise.resolve({
      code: 200,
      message: '密码修改成功',
      data: null,
    })
  }),

  bindPhone: vi.fn((data: { phone: string; verifyCode: string }) => {
    if (data.verifyCode === '123456') {
      return Promise.resolve({
        code: 200,
        message: '绑定成功',
        data: null,
      })
    }
    return Promise.resolve({
      code: 400,
      message: '验证码错误',
      data: null,
    })
  }),

  bindEmail: vi.fn((data: { email: string; verifyCode: string }) => {
    if (data.verifyCode === '123456') {
      return Promise.resolve({
        code: 200,
        message: '绑定成功',
        data: null,
      })
    }
    return Promise.resolve({
      code: 400,
      message: '验证码错误',
      data: null,
    })
  }),

  sendVerifyCode: vi.fn((type: 'phone' | 'email', target: string) =>
    Promise.resolve({
      code: 200,
      message: '验证码已发送',
      data: null,
    })
  ),

  getMerchantDashboard: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createDashboardStats(),
    })
  ),

  getRecentOrders: vi.fn((limit: number = 5) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: Array.from({ length: limit }, (_, i) => createRecentOrder({ id: i + 1 })),
    })
  ),

  getRecentReviews: vi.fn((limit: number = 5) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: Array.from({ length: limit }, (_, i) => createRecentReview({ id: i + 1 })),
    })
  ),

  getReviewStatistics: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        averageRating: 4.5,
        totalCount: 100,
        ratingDistribution: {
          five: 50,
          four: 25,
          three: 15,
          two: 7,
          one: 3,
        },
      },
    })
  ),

  toggleShopStatus: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: '切换成功',
      data: { isOpen: true },
    })
  ),
}

export const mockMerchantResponses = {
  serviceList: {
    code: 200,
    message: 'success',
    data: [defaultMerchantService],
  },
  productList: {
    code: 200,
    message: 'success',
    data: [defaultMerchantProduct],
  },
  appointmentList: {
    code: 200,
    message: 'success',
    data: [defaultMerchantAppointment],
  },
  reviewList: {
    code: 200,
    message: 'success',
    data: {
      list: [defaultMerchantReview],
      total: 50,
      page: 1,
      pageSize: 10,
      ratingDistribution: {
        five: 30,
        four: 25,
        three: 20,
        two: 15,
        one: 10,
      },
    },
  },
  categoryList: {
    code: 200,
    message: 'success',
    data: [defaultMerchantCategory],
  },
  productOrderList: {
    code: 200,
    message: 'success',
    data: [defaultMerchantProductOrder],
  },
  merchantInfo: {
    code: 200,
    message: 'success',
    data: defaultMerchantInfo,
  },
  merchantSettings: {
    code: 200,
    message: 'success',
    data: defaultMerchantSettings,
  },
  dashboardStats: {
    code: 200,
    message: 'success',
    data: defaultDashboardStats,
  },
  revenueStats: {
    code: 200,
    message: 'success',
    data: defaultRevenueStats,
  },
  appointmentStats: {
    code: 200,
    message: 'success',
    data: defaultAppointmentStats,
  },
}
