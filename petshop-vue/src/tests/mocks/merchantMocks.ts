import { vi } from 'vitest'
import type { Router } from 'vue-router'
import type { Pinia } from 'pinia'
import { createPinia, setActivePinia } from 'pinia'
import {
  createMerchant,
  createService,
  createProduct,
  createUser,
  createPet,
  createAppointment,
  createProductOrder,
  createReview,
  createAnnouncement,
  createNotification,
  createAddress,
  createCategory,
  createAdmin,
  createLog,
  createRole,
  createSuccessResponse,
  createErrorResponse,
  createPaginatedResponse,
  createMerchantList,
  createServiceList,
  createProductList,
  createUserList,
  createAppointmentList,
  createReviewList,
  createAnnouncementList,
  type Merchant,
  type Service,
  type Product,
  type User,
  type Pet,
  type Appointment,
  type ProductOrder,
  type Review,
  type Announcement,
  type Notification,
  type Address,
  type Category,
  type Admin,
  type Log,
  type Role,
} from '../fixtures/merchantData'

export const mockRouter = (): Router => {
  return {
    push: vi.fn(),
    replace: vi.fn(),
    go: vi.fn(),
    back: vi.fn(),
    forward: vi.fn(),
    beforeEach: vi.fn(),
    beforeResolve: vi.fn(),
    afterEach: vi.fn(),
    getRoutes: vi.fn(() => []),
    hasRoute: vi.fn(() => false),
    resolve: vi.fn(),
    currentRoute: {
      value: {
        path: '/',
        name: undefined,
        params: {},
        query: {},
        hash: '',
        fullPath: '/',
        matched: [],
        redirectedFrom: undefined,
        meta: {},
      },
    },
    options: {
      history: {} as any,
      routes: [],
    },
    install: vi.fn(),
  } as unknown as Router
}

export const mockPinia = (): Pinia => {
  const pinia = createPinia()
  setActivePinia(pinia)
  return pinia
}

export const mockMerchantStore = () => ({
  merchant: createMerchant(),
  setMerchant: vi.fn(),
  clearMerchant: vi.fn(),
  updateMerchant: vi.fn(),
  isLoggedIn: true,
  login: vi.fn(),
  logout: vi.fn(),
})

export const mockUserStore = () => ({
  user: createUser(),
  setUser: vi.fn(),
  clearUser: vi.fn(),
  updateUser: vi.fn(),
  isLoggedIn: true,
  login: vi.fn(),
  logout: vi.fn(),
})

export const mockAdminStore = () => ({
  admin: createAdmin(),
  setAdmin: vi.fn(),
  clearAdmin: vi.fn(),
  updateAdmin: vi.fn(),
  isLoggedIn: true,
  login: vi.fn(),
  logout: vi.fn(),
})

export const mockMerchantApi = {
  getMerchant: vi.fn(() => Promise.resolve(createSuccessResponse(createMerchant()))),
  getMerchants: vi.fn(() => Promise.resolve(createSuccessResponse(createMerchantList(10)))),
  createMerchant: vi.fn(() => Promise.resolve(createSuccessResponse(createMerchant()))),
  updateMerchant: vi.fn(() => Promise.resolve(createSuccessResponse(createMerchant()))),
  deleteMerchant: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
  approveMerchant: vi.fn(() => Promise.resolve(createSuccessResponse(createMerchant({ status: 'approved' })))),
  rejectMerchant: vi.fn(() => Promise.resolve(createSuccessResponse(createMerchant({ status: 'rejected' })))),
  getMerchantServices: vi.fn(() => Promise.resolve(createSuccessResponse(createServiceList(5)))),
  getMerchantProducts: vi.fn(() => Promise.resolve(createSuccessResponse(createProductList(5)))),
  getMerchantAppointments: vi.fn(() => Promise.resolve(createSuccessResponse(createAppointmentList(5)))),
  getMerchantOrders: vi.fn(() => Promise.resolve(createSuccessResponse([]))),
  getMerchantReviews: vi.fn(() => Promise.resolve(createSuccessResponse(createReviewList(5)))),
}

export const mockServiceApi = {
  getService: vi.fn(() => Promise.resolve(createSuccessResponse(createService()))),
  getServices: vi.fn(() => Promise.resolve(createSuccessResponse(createServiceList(10)))),
  createService: vi.fn(() => Promise.resolve(createSuccessResponse(createService()))),
  updateService: vi.fn(() => Promise.resolve(createSuccessResponse(createService()))),
  deleteService: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
  enableService: vi.fn(() => Promise.resolve(createSuccessResponse(createService({ status: 'enabled' })))),
  disableService: vi.fn(() => Promise.resolve(createSuccessResponse(createService({ status: 'disabled' })))),
}

export const mockProductApi = {
  getProduct: vi.fn(() => Promise.resolve(createSuccessResponse(createProduct()))),
  getProducts: vi.fn(() => Promise.resolve(createSuccessResponse(createProductList(10)))),
  createProduct: vi.fn(() => Promise.resolve(createSuccessResponse(createProduct()))),
  updateProduct: vi.fn(() => Promise.resolve(createSuccessResponse(createProduct()))),
  deleteProduct: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
  enableProduct: vi.fn(() => Promise.resolve(createSuccessResponse(createProduct({ status: 'enabled' })))),
  disableProduct: vi.fn(() => Promise.resolve(createSuccessResponse(createProduct({ status: 'disabled' })))),
  updateStock: vi.fn(() => Promise.resolve(createSuccessResponse(createProduct()))),
}

export const mockUserApi = {
  getUser: vi.fn(() => Promise.resolve(createSuccessResponse(createUser()))),
  getUsers: vi.fn(() => Promise.resolve(createSuccessResponse(createUserList(10)))),
  createUser: vi.fn(() => Promise.resolve(createSuccessResponse(createUser()))),
  updateUser: vi.fn(() => Promise.resolve(createSuccessResponse(createUser()))),
  deleteUser: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
  enableUser: vi.fn(() => Promise.resolve(createSuccessResponse(createUser({ status: 'active' })))),
  disableUser: vi.fn(() => Promise.resolve(createSuccessResponse(createUser({ status: 'disabled' })))),
  getUserPets: vi.fn(() => Promise.resolve(createSuccessResponse([createPet()]))),
  getUserAppointments: vi.fn(() => Promise.resolve(createSuccessResponse(createAppointmentList(5)))),
  getUserOrders: vi.fn(() => Promise.resolve(createSuccessResponse([]))),
  getUserReviews: vi.fn(() => Promise.resolve(createSuccessResponse(createReviewList(5)))),
}

export const mockAppointmentApi = {
  getAppointment: vi.fn(() => Promise.resolve(createSuccessResponse(createAppointment()))),
  getAppointments: vi.fn(() => Promise.resolve(createSuccessResponse(createAppointmentList(10)))),
  createAppointment: vi.fn(() => Promise.resolve(createSuccessResponse(createAppointment()))),
  updateAppointment: vi.fn(() => Promise.resolve(createSuccessResponse(createAppointment()))),
  cancelAppointment: vi.fn(() => Promise.resolve(createSuccessResponse(createAppointment({ status: 'cancelled' })))),
  confirmAppointment: vi.fn(() => Promise.resolve(createSuccessResponse(createAppointment({ status: 'confirmed' })))),
  completeAppointment: vi.fn(() => Promise.resolve(createSuccessResponse(createAppointment({ status: 'completed' })))),
}

export const mockOrderApi = {
  getOrder: vi.fn(() => Promise.resolve(createSuccessResponse(createProductOrder()))),
  getOrders: vi.fn(() => Promise.resolve(createSuccessResponse([]))),
  createOrder: vi.fn(() => Promise.resolve(createSuccessResponse(createProductOrder()))),
  updateOrder: vi.fn(() => Promise.resolve(createSuccessResponse(createProductOrder()))),
  cancelOrder: vi.fn(() => Promise.resolve(createSuccessResponse(createProductOrder({ status: 'cancelled' })))),
  payOrder: vi.fn(() => Promise.resolve(createSuccessResponse(createProductOrder({ status: 'paid' })))),
  shipOrder: vi.fn(() => Promise.resolve(createSuccessResponse(createProductOrder({ status: 'shipped' })))),
  completeOrder: vi.fn(() => Promise.resolve(createSuccessResponse(createProductOrder({ status: 'completed' })))),
}

export const mockReviewApi = {
  getReview: vi.fn(() => Promise.resolve(createSuccessResponse(createReview()))),
  getReviews: vi.fn(() => Promise.resolve(createSuccessResponse(createReviewList(10)))),
  createReview: vi.fn(() => Promise.resolve(createSuccessResponse(createReview()))),
  updateReview: vi.fn(() => Promise.resolve(createSuccessResponse(createReview()))),
  deleteReview: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
  approveReview: vi.fn(() => Promise.resolve(createSuccessResponse(createReview()))),
  rejectReview: vi.fn(() => Promise.resolve(createSuccessResponse(createReview()))),
}

export const mockAnnouncementApi = {
  getAnnouncement: vi.fn(() => Promise.resolve(createSuccessResponse(createAnnouncement()))),
  getAnnouncements: vi.fn(() => Promise.resolve(createSuccessResponse(createAnnouncementList(10)))),
  createAnnouncement: vi.fn(() => Promise.resolve(createSuccessResponse(createAnnouncement()))),
  updateAnnouncement: vi.fn(() => Promise.resolve(createSuccessResponse(createAnnouncement()))),
  deleteAnnouncement: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
  publishAnnouncement: vi.fn(() => Promise.resolve(createSuccessResponse(createAnnouncement({ status: 'published' })))),
  unpublishAnnouncement: vi.fn(() => Promise.resolve(createSuccessResponse(createAnnouncement({ status: 'draft' })))),
}

export const mockNotificationApi = {
  getNotification: vi.fn(() => Promise.resolve(createSuccessResponse(createNotification()))),
  getNotifications: vi.fn(() => Promise.resolve(createSuccessResponse([createNotification()]))),
  markAsRead: vi.fn(() => Promise.resolve(createSuccessResponse(createNotification({ isRead: true })))),
  markAllAsRead: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
  deleteNotification: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
}

export const mockAddressApi = {
  getAddress: vi.fn(() => Promise.resolve(createSuccessResponse(createAddress()))),
  getAddresses: vi.fn(() => Promise.resolve(createSuccessResponse([createAddress()]))),
  createAddress: vi.fn(() => Promise.resolve(createSuccessResponse(createAddress()))),
  updateAddress: vi.fn(() => Promise.resolve(createSuccessResponse(createAddress()))),
  deleteAddress: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
  setDefaultAddress: vi.fn(() => Promise.resolve(createSuccessResponse(createAddress({ isDefault: true })))),
}

export const mockCategoryApi = {
  getCategory: vi.fn(() => Promise.resolve(createSuccessResponse(createCategory()))),
  getCategories: vi.fn(() => Promise.resolve(createSuccessResponse([createCategory()]))),
  createCategory: vi.fn(() => Promise.resolve(createSuccessResponse(createCategory()))),
  updateCategory: vi.fn(() => Promise.resolve(createSuccessResponse(createCategory()))),
  deleteCategory: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
  enableCategory: vi.fn(() => Promise.resolve(createSuccessResponse(createCategory({ status: 'enabled' })))),
  disableCategory: vi.fn(() => Promise.resolve(createSuccessResponse(createCategory({ status: 'disabled' })))),
}

export const mockAdminApi = {
  getAdmin: vi.fn(() => Promise.resolve(createSuccessResponse(createAdmin()))),
  getAdmins: vi.fn(() => Promise.resolve(createSuccessResponse([createAdmin()]))),
  createAdmin: vi.fn(() => Promise.resolve(createSuccessResponse(createAdmin()))),
  updateAdmin: vi.fn(() => Promise.resolve(createSuccessResponse(createAdmin()))),
  deleteAdmin: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
}

export const mockLogApi = {
  getLog: vi.fn(() => Promise.resolve(createSuccessResponse(createLog()))),
  getLogs: vi.fn(() => Promise.resolve(createSuccessResponse([createLog()]))),
  createLog: vi.fn(() => Promise.resolve(createSuccessResponse(createLog()))),
}

export const mockRoleApi = {
  getRole: vi.fn(() => Promise.resolve(createSuccessResponse(createRole()))),
  getRoles: vi.fn(() => Promise.resolve(createSuccessResponse([createRole()]))),
  createRole: vi.fn(() => Promise.resolve(createSuccessResponse(createRole()))),
  updateRole: vi.fn(() => Promise.resolve(createSuccessResponse(createRole()))),
  deleteRole: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
}

export const mockAuthApi = {
  login: vi.fn(() => Promise.resolve(createSuccessResponse({ token: 'test-token', user: createUser() }))),
  logout: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
  register: vi.fn(() => Promise.resolve(createSuccessResponse(createUser()))),
  refreshToken: vi.fn(() => Promise.resolve(createSuccessResponse({ token: 'new-token' }))),
  getCurrentUser: vi.fn(() => Promise.resolve(createSuccessResponse(createUser()))),
  forgotPassword: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
  resetPassword: vi.fn(() => Promise.resolve(createSuccessResponse(null))),
}

export const mockUploadApi = {
  uploadImage: vi.fn(() => Promise.resolve(createSuccessResponse({ url: 'https://picsum.photos/200/200?random=1' }))),
  uploadFile: vi.fn(() => Promise.resolve(createSuccessResponse({ url: 'https://example.com/file.pdf' }))),
}

export const mockStatsApi = {
  getDashboardStats: vi.fn(() =>
    Promise.resolve(
      createSuccessResponse({
        totalUsers: 2580,
        totalMerchants: 150,
        todayOrders: 89,
        monthRevenue: 125680,
      })
    )
  ),
  getAppointmentStats: vi.fn(() =>
    Promise.resolve(
      createSuccessResponse({
        total: 100,
        completed: 80,
        cancelled: 10,
        pending: 10,
      })
    )
  ),
  getRevenueStats: vi.fn(() =>
    Promise.resolve(
      createSuccessResponse({
        serviceRevenue: 50000,
        productRevenue: 30000,
        totalRevenue: 80000,
      })
    )
  ),
}

export const resetAllMocks = () => {
  vi.clearAllMocks()
}

export const mockAllApis = () => ({
  merchant: mockMerchantApi,
  service: mockServiceApi,
  product: mockProductApi,
  user: mockUserApi,
  appointment: mockAppointmentApi,
  order: mockOrderApi,
  review: mockReviewApi,
  announcement: mockAnnouncementApi,
  notification: mockNotificationApi,
  address: mockAddressApi,
  category: mockCategoryApi,
  admin: mockAdminApi,
  log: mockLogApi,
  role: mockRoleApi,
  auth: mockAuthApi,
  upload: mockUploadApi,
  stats: mockStatsApi,
})

export const mockElementPlus = () => ({
  ElMessage: {
    success: vi.fn(),
    warning: vi.fn(),
    info: vi.fn(),
    error: vi.fn(),
  },
  ElMessageBox: {
    confirm: vi.fn(() => Promise.resolve()),
    alert: vi.fn(() => Promise.resolve()),
    prompt: vi.fn(() => Promise.resolve({ value: 'test' })),
  },
  ElNotification: {
    success: vi.fn(),
    warning: vi.fn(),
    info: vi.fn(),
    error: vi.fn(),
  },
  ElLoading: {
    service: vi.fn(() => ({
      close: vi.fn(),
    })),
  },
})

export const mockI18n = () => ({
  t: vi.fn((key: string) => key),
  locale: 'zh-CN',
  availableLocales: ['zh-CN', 'en-US'],
})

export const mockDate = () => {
  const now = new Date('2024-04-20T10:00:00.000Z')
  vi.useFakeTimers()
  vi.setSystemTime(now)
  return {
    now,
    restore: () => vi.useRealTimers(),
  }
}

export const mockFetch = (response: any, ok = true, status = 200) => {
  global.fetch = vi.fn(() =>
    Promise.resolve({
      ok,
      status,
      json: () => Promise.resolve(response),
      text: () => Promise.resolve(JSON.stringify(response)),
      blob: () => Promise.resolve(new Blob([JSON.stringify(response)])),
    } as Response)
  )
  return global.fetch
}

export const mockAxios = () => {
  const axios = {
    get: vi.fn(() => Promise.resolve({ data: {} })),
    post: vi.fn(() => Promise.resolve({ data: {} })),
    put: vi.fn(() => Promise.resolve({ data: {} })),
    delete: vi.fn(() => Promise.resolve({ data: {} })),
    patch: vi.fn(() => Promise.resolve({ data: {} })),
    request: vi.fn(() => Promise.resolve({ data: {} })),
    interceptors: {
      request: {
        use: vi.fn(),
        eject: vi.fn(),
      },
      response: {
        use: vi.fn(),
        eject: vi.fn(),
      },
    },
    defaults: {
      baseURL: '',
      headers: {},
    },
  }
  return axios
}

export const mockWebSocket = () => {
  const events: Record<string, Array<(data: any) => void>> = {}
  const ws = {
    send: vi.fn(),
    close: vi.fn(),
    onopen: null as (() => void) | null,
    onclose: null as (() => void) | null,
    onmessage: null as ((event: { data: string }) => void) | null,
    onerror: null as ((error: any) => void) | null,
    addEventListener: vi.fn((event: string, callback: (data: any) => void) => {
      if (!events[event]) events[event] = []
      events[event].push(callback)
    }),
    removeEventListener: vi.fn((event: string, callback: (data: any) => void) => {
      if (events[event]) {
        const index = events[event].indexOf(callback)
        if (index > -1) events[event].splice(index, 1)
      }
    }),
    dispatchEvent: vi.fn(),
    readyState: 1,
    CONNECTING: 0,
    OPEN: 1,
    CLOSING: 2,
    CLOSED: 3,
    trigger: (event: string, data: any) => {
      if (events[event]) {
        events[event].forEach((callback) => callback(data))
      }
    },
  }
  return ws
}

export {
  createMerchant,
  createService,
  createProduct,
  createUser,
  createPet,
  createAppointment,
  createProductOrder,
  createReview,
  createAnnouncement,
  createNotification,
  createAddress,
  createCategory,
  createAdmin,
  createLog,
  createRole,
  createSuccessResponse,
  createErrorResponse,
  createPaginatedResponse,
  createMerchantList,
  createServiceList,
  createProductList,
  createUserList,
  createAppointmentList,
  createReviewList,
  createAnnouncementList,
}
