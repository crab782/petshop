import { vi } from 'vitest'
import { render, type RenderOptions } from '@testing-library/vue'
import { mount, type VueWrapper } from '@vue/test-utils'
import { createRouter, createWebHistory, type Router } from 'vue-router'
import { createPinia, setActivePinia, type Pinia } from 'pinia'
import type { Component } from 'vue'

export interface UserTestData {
  id: number
  username: string
  email: string
  phone: string
  avatar: string
  createdAt: string
  updatedAt: string
}

export interface PetTestData {
  id: number
  userId: number
  name: string
  type: string
  breed: string
  age: number
  gender: 'male' | 'female'
  avatar: string
  description: string
  createdAt: string
  updatedAt: string
}

export interface AddressTestData {
  id: number
  userId: number
  receiverName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: boolean
  createdAt: string
  updatedAt: string
}

export interface AppointmentTestData {
  id: number
  userId: number
  merchantId: number
  serviceId: number
  petId: number
  appointmentTime: string
  status: 'pending' | 'confirmed' | 'completed' | 'cancelled'
  totalPrice: number
  notes: string
  createdAt: string
  updatedAt: string
}

export interface ProductOrderTestData {
  id: number
  userId: number
  merchantId: number
  totalPrice: number
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'
  shippingAddress: string
  createdAt: string
  updatedAt: string
}

export interface ServiceTestData {
  id: number
  merchantId: number
  name: string
  description: string
  price: number
  duration: number
  image: string
  status: 'enabled' | 'disabled'
  createdAt: string
  updatedAt: string
}

export interface ProductTestData {
  id: number
  merchantId: number
  name: string
  description: string
  price: number
  stock: number
  image: string
  status: 'enabled' | 'disabled'
  createdAt: string
  updatedAt: string
}

export interface MerchantTestData {
  id: number
  name: string
  contactPerson: string
  phone: string
  email: string
  address: string
  logo: string
  status: 'pending' | 'approved' | 'rejected'
  rating: number
  createdAt: string
  updatedAt: string
}

export interface ReviewTestData {
  id: number
  userId: number
  merchantId: number
  serviceId: number
  appointmentId: number
  rating: number
  comment: string
  createdAt: string
}

export interface NotificationTestData {
  id: number
  userId: number
  title: string
  content: string
  type: string
  isRead: boolean
  createdAt: string
}

export interface AnnouncementTestData {
  id: number
  title: string
  content: string
  status: 'published' | 'draft'
  createdAt: string
  updatedAt: string
}

export const createUser = (overrides: Partial<UserTestData> = {}): UserTestData => ({
  id: 1,
  username: 'testuser',
  email: 'test@example.com',
  phone: '13800138000',
  avatar: 'https://picsum.photos/100/100?random=1',
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

export const createPet = (overrides: Partial<PetTestData> = {}): PetTestData => ({
  id: 1,
  userId: 1,
  name: '小白',
  type: 'dog',
  breed: '金毛',
  age: 2,
  gender: 'male',
  avatar: 'https://picsum.photos/100/100?random=2',
  description: '一只可爱的金毛犬',
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

export const createAddress = (overrides: Partial<AddressTestData> = {}): AddressTestData => ({
  id: 1,
  userId: 1,
  receiverName: '张三',
  phone: '13800138001',
  province: '北京市',
  city: '北京市',
  district: '朝阳区',
  detailAddress: '某某街道某某小区1号楼101室',
  isDefault: true,
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

export const createAppointment = (overrides: Partial<AppointmentTestData> = {}): AppointmentTestData => ({
  id: 1,
  userId: 1,
  merchantId: 1,
  serviceId: 1,
  petId: 1,
  appointmentTime: '2024-04-20T10:00:00.000Z',
  status: 'pending',
  totalPrice: 100,
  notes: '请准时到达',
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

export const createProductOrder = (overrides: Partial<ProductOrderTestData> = {}): ProductOrderTestData => ({
  id: 1,
  userId: 1,
  merchantId: 1,
  totalPrice: 299,
  status: 'pending',
  shippingAddress: '北京市朝阳区某某街道某某小区1号楼101室',
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

export const createService = (overrides: Partial<ServiceTestData> = {}): ServiceTestData => ({
  id: 1,
  merchantId: 1,
  name: '宠物美容',
  description: '专业宠物美容服务',
  price: 100,
  duration: 60,
  image: 'https://picsum.photos/300/200?random=3',
  status: 'enabled',
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

export const createProduct = (overrides: Partial<ProductTestData> = {}): ProductTestData => ({
  id: 1,
  merchantId: 1,
  name: '宠物狗粮',
  description: '优质宠物狗粮',
  price: 99,
  stock: 100,
  image: 'https://picsum.photos/300/200?random=4',
  status: 'enabled',
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

export const createMerchant = (overrides: Partial<MerchantTestData> = {}): MerchantTestData => ({
  id: 1,
  name: '测试宠物店',
  contactPerson: '李四',
  phone: '13800138002',
  email: 'merchant@example.com',
  address: '北京市朝阳区某某街道',
  logo: 'https://picsum.photos/200/200?random=5',
  status: 'approved',
  rating: 4.5,
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

export const createReview = (overrides: Partial<ReviewTestData> = {}): ReviewTestData => ({
  id: 1,
  userId: 1,
  merchantId: 1,
  serviceId: 1,
  appointmentId: 1,
  rating: 5,
  comment: '服务非常好，推荐！',
  createdAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

export const createNotification = (overrides: Partial<NotificationTestData> = {}): NotificationTestData => ({
  id: 1,
  userId: 1,
  title: '预约提醒',
  content: '您有一个预约即将开始',
  type: 'appointment',
  isRead: false,
  createdAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

export const createAnnouncement = (overrides: Partial<AnnouncementTestData> = {}): AnnouncementTestData => ({
  id: 1,
  title: '系统公告',
  content: '欢迎使用宠物服务平台',
  status: 'published',
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
  ...overrides,
})

export const createUserList = (count: number): UserTestData[] =>
  Array.from({ length: count }, (_, i) => createUser({ id: i + 1, username: `user${i + 1}` }))

export const createPetList = (count: number): PetTestData[] =>
  Array.from({ length: count }, (_, i) => createPet({ id: i + 1, name: `宠物${i + 1}` }))

export const createAddressList = (count: number): AddressTestData[] =>
  Array.from({ length: count }, (_, i) => createAddress({ id: i + 1, isDefault: i === 0 }))

export const createAppointmentList = (count: number): AppointmentTestData[] =>
  Array.from({ length: count }, (_, i) => createAppointment({ id: i + 1 }))

export const createProductOrderList = (count: number): ProductOrderTestData[] =>
  Array.from({ length: count }, (_, i) => createProductOrder({ id: i + 1 }))

export const createServiceList = (count: number): ServiceTestData[] =>
  Array.from({ length: count }, (_, i) => createService({ id: i + 1, name: `服务${i + 1}` }))

export const createProductList = (count: number): ProductTestData[] =>
  Array.from({ length: count }, (_, i) => createProduct({ id: i + 1, name: `商品${i + 1}` }))

export const createMerchantList = (count: number): MerchantTestData[] =>
  Array.from({ length: count }, (_, i) => createMerchant({ id: i + 1, name: `商家${i + 1}` }))

export const createReviewList = (count: number): ReviewTestData[] =>
  Array.from({ length: count }, (_, i) => createReview({ id: i + 1, rating: Math.min(5, Math.floor(Math.random() * 5) + 1) }))

export const createNotificationList = (count: number): NotificationTestData[] =>
  Array.from({ length: count }, (_, i) => createNotification({ id: i + 1, isRead: i > 0 }))

export const createAnnouncementList = (count: number): AnnouncementTestData[] =>
  Array.from({ length: count }, (_, i) => createAnnouncement({ id: i + 1, title: `公告${i + 1}` }))

export const createTestRouter = (routes: any[] = []): Router => {
  return createRouter({
    history: createWebHistory(),
    routes: [
      { path: '/', name: 'home', component: { template: '<div>Home</div>' } },
      { path: '/login', name: 'login', component: { template: '<div>Login</div>' } },
      { path: '/user', name: 'user', component: { template: '<div>User</div>' } },
      ...routes,
    ],
  })
}

export const createTestPinia = (): Pinia => {
  const pinia = createPinia()
  setActivePinia(pinia)
  return pinia
}

export interface UserTestMountOptions {
  router?: Router
  pinia?: Pinia
  global?: {
    components?: Record<string, any>
    plugins?: any[]
    mocks?: Record<string, any>
    provide?: Record<string, any>
    stubs?: Record<string, any>
  }
  props?: Record<string, any>
  slots?: Record<string, any>
}

export const mountUserComponent = <T extends Component>(
  component: T,
  options: UserTestMountOptions = {}
): VueWrapper<T> => {
  const { router, pinia, global = {}, ...restOptions } = options
  const testPinia = pinia || createTestPinia()
  const testRouter = router || createTestRouter()

  return mount(component, {
    ...restOptions,
    shallow: true,
    global: {
      ...global,
      plugins: [
        ...(global.plugins || []),
        testPinia,
        testRouter,
      ],
      stubs: {
        RouterLink: true,
        RouterView: true,
        ElTable: true,
        ElTableColumn: true,
        ElInput: true,
        ElSelect: true,
        ElOption: true,
        ElButton: true,
        ElTag: true,
        ElCard: true,
        ElDialog: true,
        ElPagination: true,
        ElDatePicker: true,
        ElEmpty: true,
        ElForm: true,
        ElFormItem: true,
        ElRadio: true,
        ElRadioGroup: true,
        ElRadioButton: true,
        ElTabs: true,
        ElTabPane: true,
        ElRate: true,
        ElDescriptions: true,
        ElDescriptionsItem: true,
        ElDivider: true,
        ElAlert: true,
        ElResult: true,
        ElImage: true,
        ElBreadcrumb: true,
        ElBreadcrumbItem: true,
        ElAvatar: true,
        ElIcon: true,
        ElRow: true,
        ElCol: true,
        ElText: true,
        ElBadge: true,
        ElInputNumber: true,
        ElTooltip: true,
        ...global.stubs,
      },
    },
  })
}

export const renderUserComponent = <T extends Component>(
  component: T,
  options: UserTestMountOptions = {}
) => {
  const { router, pinia, global = {}, ...restOptions } = options
  const testPinia = pinia || createTestPinia()
  const testRouter = router || createTestRouter()

  return render(component, {
    ...restOptions,
    global: {
      ...global,
      plugins: [
        ...(global.plugins || []),
        testPinia,
        testRouter,
      ],
      stubs: {
        RouterLink: true,
        RouterView: true,
        ...global.stubs,
      },
    } as RenderOptions,
  })
}

export const mockUserStore = () => ({
  user: createUser(),
  isLoggedIn: true,
  setUser: vi.fn(),
  clearUser: vi.fn(),
  updateUser: vi.fn(),
  login: vi.fn(),
  logout: vi.fn(),
  fetchUser: vi.fn(),
})

export const mockUserApi = {
  getUser: vi.fn(() => Promise.resolve({ data: createUser() })),
  getUsers: vi.fn(() => Promise.resolve({ data: createUserList(10) })),
  updateUser: vi.fn(() => Promise.resolve({ data: createUser() })),
  getPets: vi.fn(() => Promise.resolve({ data: createPetList(3) })),
  getAddresses: vi.fn(() => Promise.resolve({ data: createAddressList(2) })),
  getAppointments: vi.fn(() => Promise.resolve({ data: createAppointmentList(5) })),
  getOrders: vi.fn(() => Promise.resolve({ data: createProductOrderList(5) })),
  getReviews: vi.fn(() => Promise.resolve({ data: createReviewList(5) })),
  getFavorites: vi.fn(() => Promise.resolve({ data: createMerchantList(3) })),
  getNotifications: vi.fn(() => Promise.resolve({ data: createNotificationList(5) })),
}

export const mockServiceApi = {
  getServices: vi.fn(() => Promise.resolve(createServiceList(10))),
  getService: vi.fn(() => Promise.resolve(createService())),
  getMerchantServices: vi.fn(() => Promise.resolve(createServiceList(5))),
}

export const mockProductApi = {
  getProducts: vi.fn(() => Promise.resolve(createProductList(10))),
  getProduct: vi.fn(() => Promise.resolve(createProduct())),
  getMerchantProducts: vi.fn(() => Promise.resolve(createProductList(5))),
}

export const mockMerchantApi = {
  getMerchants: vi.fn(() => Promise.resolve(createMerchantList(10))),
  getMerchant: vi.fn(() => Promise.resolve(createMerchant())),
}

export const mockAppointmentApi = {
  createAppointment: vi.fn(() => Promise.resolve(createAppointment())),
  getAppointments: vi.fn(() => Promise.resolve(createAppointmentList(5))),
  cancelAppointment: vi.fn(() => Promise.resolve({ ...createAppointment(), status: 'cancelled' })),
}

export const mockOrderApi = {
  createOrder: vi.fn(() => Promise.resolve(createProductOrder())),
  getOrders: vi.fn(() => Promise.resolve(createProductOrderList(5))),
  cancelOrder: vi.fn(() => Promise.resolve({ ...createProductOrder(), status: 'cancelled' })),
}

export const mockReviewApi = {
  createReview: vi.fn(() => Promise.resolve(createReview())),
  getReviews: vi.fn(() => Promise.resolve(createReviewList(5))),
}

export const mockAnnouncementApi = {
  getAnnouncements: vi.fn(() => Promise.resolve(createAnnouncementList(10))),
  getAnnouncement: vi.fn(() => Promise.resolve(createAnnouncement())),
}

export const mockNotificationApi = {
  getNotifications: vi.fn(() => Promise.resolve(createNotificationList(5))),
  markAsRead: vi.fn(() => Promise.resolve({ ...createNotification(), isRead: true })),
  markAllAsRead: vi.fn(() => Promise.resolve(null)),
}

export const mockCartStore = () => ({
  items: [] as Array<{ productId: number; quantity: number; price: number }>,
  totalPrice: 0,
  addItem: vi.fn(),
  removeItem: vi.fn(),
  updateQuantity: vi.fn(),
  clearCart: vi.fn(),
  getTotalPrice: vi.fn(() => 0),
})

export const mockFavoriteStore = () => ({
  favorites: [] as number[],
  addFavorite: vi.fn(),
  removeFavorite: vi.fn(),
  isFavorite: vi.fn(() => false),
})

export const waitFor = async (ms: number): Promise<void> => {
  return new Promise((resolve) => {
    const timer = setTimeout(resolve, ms)
    return () => clearTimeout(timer)
  })
}

export const flushPromises = async (): Promise<void> => {
  return new Promise((resolve) => {
    const timer = setTimeout(resolve, 0)
    return () => clearTimeout(timer)
  })
}

export const waitForCondition = async (
  condition: () => boolean,
  options: { timeout?: number; interval?: number } = {}
): Promise<void> => {
  const { timeout = 5000, interval = 50 } = options
  const startTime = Date.now()

  while (!condition()) {
    if (Date.now() - startTime > timeout) {
      throw new Error('Condition not met within timeout')
    }
    await new Promise((resolve) => {
      const timer = setTimeout(resolve, interval)
      return () => clearTimeout(timer)
    })
  }
}

export const mockLoginState = (isLoggedIn: boolean = true) => {
  if (isLoggedIn) {
    localStorage.setItem('token', 'test-token')
    localStorage.setItem('user', JSON.stringify(createUser()))
  } else {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
}

export const mockRouterPush = () => vi.fn()

export const createSuccessResponse = <T>(data: T) => ({
  code: 200,
  message: 'success',
  data,
})

export const createErrorResponse = (message: string, code = 400) => ({
  code,
  message,
  data: null,
})

export const createPaginatedResponse = <T>(list: T[], page = 1, pageSize = 10) => ({
  code: 200,
  message: 'success',
  data: {
    list,
    total: list.length,
    page,
    pageSize,
    totalPages: Math.ceil(list.length / pageSize),
  },
})
