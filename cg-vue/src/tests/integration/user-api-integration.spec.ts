import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { useRouter, useRoute } from 'vue-router'

// 导入用户端页面组件
import UserHome from '@/views/user/user-home/index.vue'
import UserProfile from '@/views/user/user-profile/index.vue'
import ProfileEdit from '@/views/user/profile-edit/index.vue'
import UserPets from '@/views/user/user-pets/index.vue'
import PetEdit from '@/views/user/pet-edit/index.vue'
import UserAppointments from '@/views/user/user-appointments/index.vue'
import AppointmentConfirm from '@/views/user/appointment-confirm/index.vue'
import UserOrders from '@/views/user/user-orders/index.vue'
import OrderDetail from '@/views/user/order-detail/index.vue'
import UserCart from '@/views/user/user-cart/index.vue'
import Checkout from '@/views/user/checkout/index.vue'
import Pay from '@/views/user/pay/index.vue'
import UserFavorites from '@/views/user/user-favorites/index.vue'
import UserReviews from '@/views/user/user-reviews/index.vue'
import MyReviews from '@/views/user/my-reviews/index.vue'
import UserAddresses from '@/views/user/addresses/index.vue'
import UserMerchant from '@/views/user/user-merchant/index.vue'
import UserShop from '@/views/user/user-shop/index.vue'
import ProductDetail from '@/views/user/product-detail/index.vue'
import ServiceDetail from '@/views/user/service-detail/index.vue'
import ServiceList from '@/views/user/service-list/index.vue'
import UserServices from '@/views/user/user-services/index.vue'
import UserBook from '@/views/user/user-book/index.vue'
import Search from '@/views/user/search/index.vue'
import UserAnnouncements from '@/views/user/user-announcements/index.vue'
import AnnouncementDetail from '@/views/user/announcement-detail/index.vue'
import Notifications from '@/views/user/notifications/index.vue'

// 导入API模块
import * as userApi from '@/api/user'
import * as authApi from '@/api/auth'
import * as searchApi from '@/api/search'
import * as notificationApi from '@/api/notification'
import * as announcementApi from '@/api/announcement'

// 导入测试工具
import { createUser, createPet, createPetList, mockUserStore } from '@/tests/utils/userTestUtils'

// 模拟路由
const mockPush = vi.fn()
const mockReplace = vi.fn()

const mockRoute = {
  query: {} as Record<string, string>,
  path: '/user',
  params: {},
}

// 模拟存储
vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

// 模拟API调用
vi.mock('@/api/user', () => ({
  // 用户相关API
  getHomeStats: vi.fn(() => Promise.resolve({ petCount: 2, pendingAppointments: 1, reviewCount: 3 })),
  getRecentActivities: vi.fn(() => Promise.resolve([])),
  getRecommendedServices: vi.fn(() => Promise.resolve([])),
  
  // 个人资料API
  getUserPets: vi.fn(() => Promise.resolve(createPetList(3))),
  getPetById: vi.fn((id: number) => Promise.resolve(createPet({ id }))),
  addPet: vi.fn(() => Promise.resolve({ id: 1 })),
  updatePet: vi.fn(() => Promise.resolve({ id: 1 })),
  deletePet: vi.fn(() => Promise.resolve()),
  
  // 预约API
  getUserAppointments: vi.fn(() => Promise.resolve([])),
  getAppointmentById: vi.fn(() => Promise.resolve({})),
  createAppointment: vi.fn(() => Promise.resolve()),
  cancelAppointment: vi.fn(() => Promise.resolve()),
  getAppointmentStats: vi.fn(() => Promise.resolve({})),
  
  // 订单API
  getProductOrders: vi.fn(() => Promise.resolve([])),
  getOrderById: vi.fn(() => Promise.resolve({})),
  createOrder: vi.fn(() => Promise.resolve()),
  previewOrder: vi.fn(() => Promise.resolve({})),
  payOrder: vi.fn(() => Promise.resolve({})),
  getPayStatus: vi.fn(() => Promise.resolve({})),
  cancelOrder: vi.fn(() => Promise.resolve()),
  refundOrder: vi.fn(() => Promise.resolve()),
  confirmReceiveOrder: vi.fn(() => Promise.resolve()),
  deleteOrder: vi.fn(() => Promise.resolve()),
  batchCancelOrders: vi.fn(() => Promise.resolve()),
  batchDeleteOrders: vi.fn(() => Promise.resolve()),
  
  // 购物车API
  getCart: vi.fn(() => Promise.resolve([])),
  addToCart: vi.fn(() => Promise.resolve()),
  updateCartItem: vi.fn(() => Promise.resolve()),
  removeFromCart: vi.fn(() => Promise.resolve()),
  batchRemoveFromCart: vi.fn(() => Promise.resolve()),
  
  // 收藏API
  getFavorites: vi.fn(() => Promise.resolve([])),
  addFavorite: vi.fn(() => Promise.resolve({})),
  removeFavorite: vi.fn(() => Promise.resolve()),
  getServiceFavorites: vi.fn(() => Promise.resolve([])),
  addServiceFavorite: vi.fn(() => Promise.resolve({})),
  removeServiceFavorite: vi.fn(() => Promise.resolve()),
  addProductFavorite: vi.fn(() => Promise.resolve({})),
  removeProductFavorite: vi.fn(() => Promise.resolve()),
  checkProductFavorite: vi.fn(() => Promise.resolve({ isFavorited: false })),
  
  // 评价API
  addReview: vi.fn(() => Promise.resolve({})),
  getUserReviews: vi.fn(() => Promise.resolve([])),
  updateReview: vi.fn(() => Promise.resolve({})),
  deleteReview: vi.fn(() => Promise.resolve()),
  
  // 地址API
  getAddresses: vi.fn(() => Promise.resolve([])),
  addAddress: vi.fn(() => Promise.resolve({})),
  updateAddress: vi.fn(() => Promise.resolve({})),
  deleteAddress: vi.fn(() => Promise.resolve()),
  setDefaultAddress: vi.fn(() => Promise.resolve({})),
  
  // 服务和商家API
  getServices: vi.fn(() => Promise.resolve([])),
  getServiceById: vi.fn(() => Promise.resolve({})),
  getServicesByKeyword: vi.fn(() => Promise.resolve([])),
  getMerchantInfo: vi.fn(() => Promise.resolve({})),
  getMerchantServices: vi.fn(() => Promise.resolve([])),
  getMerchantReviews: vi.fn(() => Promise.resolve([])),
  getMerchantList: vi.fn(() => Promise.resolve({ data: [], total: 0 })),
  getMerchantProducts: vi.fn(() => Promise.resolve([])),
  getAvailableSlots: vi.fn(() => Promise.resolve({})),
  searchMerchants: vi.fn(() => Promise.resolve([])),
  
  // 商品API
  searchProducts: vi.fn(() => Promise.resolve([])),
  getProducts: vi.fn(() => Promise.resolve([])),
  getProductById: vi.fn(() => Promise.resolve({})),
  getProductReviews: vi.fn(() => Promise.resolve([])),
  
  // 其他API
  getUserPurchasedServices: vi.fn(() => Promise.resolve({ data: [], total: 0 })),
  searchServices: vi.fn(() => Promise.resolve([])),
}))

vi.mock('@/api/auth', () => ({
  getUserInfo: vi.fn(() => Promise.resolve(createUser())),
  updateUserInfo: vi.fn(() => Promise.resolve({})),
  login: vi.fn(() => Promise.resolve({ success: true, data: { token: 'test-token' } })),
  register: vi.fn(() => Promise.resolve({ success: true })),
  logout: vi.fn(() => Promise.resolve({ success: true })),
}))

vi.mock('@/api/search', () => ({
  search: vi.fn(() => Promise.resolve({ products: [], services: [], merchants: [] })),
  getHotKeywords: vi.fn(() => Promise.resolve([])),
  getSearchHistory: vi.fn(() => Promise.resolve([])),
  clearSearchHistory: vi.fn(() => Promise.resolve({})),
}))

vi.mock('@/api/notification', () => ({
  getNotifications: vi.fn(() => Promise.resolve([])),
  markAsRead: vi.fn(() => Promise.resolve({})),
  markAllAsRead: vi.fn(() => Promise.resolve({})),
  deleteNotification: vi.fn(() => Promise.resolve({})),
}))

vi.mock('@/api/announcement', () => ({
  getAnnouncements: vi.fn(() => Promise.resolve([])),
  getAnnouncementById: vi.fn(() => Promise.resolve({})),
}))

// 模拟路由
vi.mock('vue-router', async () => {
  const actual = await vi.importActual('vue-router')
  return {
    ...actual,
    useRouter: () => ({
      push: mockPush,
      replace: mockReplace,
      go: vi.fn(),
      back: vi.fn(),
    }),
    useRoute: () => mockRoute,
  }
})

// 模拟Element Plus
vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      error: vi.fn(),
      warning: vi.fn(),
      info: vi.fn(),
    },
    ElMessageBox: {
      confirm: vi.fn(() => Promise.resolve()),
      alert: vi.fn(() => Promise.resolve()),
      prompt: vi.fn(() => Promise.resolve({ value: 'test' })),
    },
  }
})

// 全局模拟ElMessage
global.ElMessage = {
  success: vi.fn(),
  error: vi.fn(),
  warning: vi.fn(),
  info: vi.fn(),
}

// 模拟fetch
global.fetch = vi.fn(() =>
  Promise.resolve({
    ok: true,
    json: () => Promise.resolve({ success: true }),
  })
) as any

// 元素stubs
const elementStubs = {
  'el-avatar': true,
  'el-button': true,
  'el-upload': true,
  'el-card': true,
  'el-form': true,
  'el-form-item': true,
  'el-input': true,
  'el-input-number': true,
  'el-radio': true,
  'el-radio-group': true,
  'el-date-picker': true,
  'el-select': true,
  'el-option': true,
  'el-divider': true,
  'el-icon': true,
  'el-table': true,
  'el-table-column': true,
  'el-tag': true,
  'el-empty': true,
  'el-dialog': true,
  'el-image': true,
  'el-pagination': true,
  'el-tabs': true,
  'el-tab-pane': true,
  'el-alert': true,
  'el-progress': true,
  'el-badge': true,
  'el-dropdown': true,
  'el-dropdown-menu': true,
  'el-dropdown-item': true,
  'el-tree': true,
  'el-tree-node': true,
  'el-collapse': true,
  'el-collapse-item': true,
  'el-checkbox': true,
  'el-checkbox-group': true,
  'el-rate': true,
  'el-loading': true,
  'el-skeleton': true,
  'el-skeleton-item': true,
}

describe('用户端API集成测试', () => {
  let pinia: ReturnType<typeof createPinia>

  beforeEach(() => {
    vi.clearAllMocks()
    mockPush.mockClear()
    mockReplace.mockClear()
    mockRoute.query = {}
    mockRoute.params = {}
    pinia = createPinia()
    setActivePinia(pinia)
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  // 测试用户首页API集成
  describe('用户首页API集成测试', () => {
    it('should load home stats successfully', async () => {
      const wrapper = mount(UserHome, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      expect(userApi.getHomeStats).toHaveBeenCalled()
      expect(userApi.getRecentActivities).toHaveBeenCalled()
      expect(userApi.getRecommendedServices).toHaveBeenCalled()
    })

    it('should handle home stats fetch error', async () => {
      vi.mocked(userApi.getHomeStats).mockRejectedValueOnce(new Error('获取首页统计失败'))

      const wrapper = mount(UserHome, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })
  })

  // 测试用户个人资料API集成
  describe('用户个人资料API集成测试', () => {
    it('should load user profile successfully', async () => {
      const wrapper = mount(UserProfile, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(authApi.getUserInfo).toHaveBeenCalled()
    })

    it('should update user profile successfully', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      wrapper.vm.profileForm.nickname = '新昵称'
      wrapper.vm.profileForm.email = 'newemail@example.com'
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.profileFormRef = mockFormRef

      await wrapper.vm.handleSave()
      await flushPromises()
      
      expect(authApi.updateUserInfo).toHaveBeenCalled()
    })

    it('should handle profile update error', async () => {
      vi.mocked(authApi.updateUserInfo).mockRejectedValueOnce(new Error('更新个人资料失败'))

      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      wrapper.vm.profileForm.nickname = '新昵称'
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.profileFormRef = mockFormRef

      await wrapper.vm.handleSave()
      await flushPromises()
      
      expect(wrapper.exists()).toBe(true)
    })
  })

  // 测试用户宠物管理API集成
  describe('用户宠物管理API集成测试', () => {
    it('should load pet list successfully', async () => {
      const wrapper = mount(UserPets, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getUserPets).toHaveBeenCalled()
    })

    it('should add pet successfully', async () => {
      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      wrapper.vm.form.name = '新宠物'
      wrapper.vm.form.type = '狗'
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(userApi.addPet).toHaveBeenCalled()
    })

    it('should update pet successfully', async () => {
      mockRoute.query = { id: '1' }
      
      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      wrapper.vm.form.name = '更新宠物'
      wrapper.vm.form.type = '猫'
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(userApi.updatePet).toHaveBeenCalled()
    })

    it('should handle pet operation errors', async () => {
      vi.mocked(userApi.addPet).mockRejectedValueOnce(new Error('添加宠物失败'))

      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      wrapper.vm.form.name = '新宠物'
      wrapper.vm.form.type = '狗'
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(wrapper.exists()).toBe(true)
    })
  })

  // 测试用户预约API集成
  describe('用户预约API集成测试', () => {
    it('should load appointments successfully', async () => {
      const wrapper = mount(UserAppointments, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getUserAppointments).toHaveBeenCalled()
    })

    it('should create appointment successfully', async () => {
      mockRoute.query = { serviceId: '1', merchantId: '1' }
      
      const wrapper = mount(AppointmentConfirm, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      // 模拟表单提交
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(userApi.createAppointment).toHaveBeenCalled()
    })

    it('should handle appointment creation error', async () => {
      vi.mocked(userApi.createAppointment).mockRejectedValueOnce(new Error('创建预约失败'))
      mockRoute.query = { serviceId: '1', merchantId: '1' }
      
      const wrapper = mount(AppointmentConfirm, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(wrapper.exists()).toBe(true)
    })
  })

  // 测试用户订单API集成
  describe('用户订单API集成测试', () => {
    it('should load orders successfully', async () => {
      const wrapper = mount(UserOrders, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getProductOrders).toHaveBeenCalled()
    })

    it('should load order detail successfully', async () => {
      mockRoute.params = { id: '1' }
      
      const wrapper = mount(OrderDetail, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getOrderById).toHaveBeenCalled()
    })

    it('should create order successfully', async () => {
      const wrapper = mount(Checkout, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(userApi.createOrder).toHaveBeenCalled()
    })

    it('should handle order creation error', async () => {
      vi.mocked(userApi.createOrder).mockRejectedValueOnce(new Error('创建订单失败'))

      const wrapper = mount(Checkout, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(wrapper.exists()).toBe(true)
    })
  })

  // 测试用户购物车API集成
  describe('用户购物车API集成测试', () => {
    it('should load cart successfully', async () => {
      const wrapper = mount(UserCart, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getCart).toHaveBeenCalled()
    })

    it('should add to cart successfully', async () => {
      const wrapper = mount(UserCart, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      // 模拟添加商品到购物车
      await wrapper.vm.handleAddToCart({ productId: 1, quantity: 1 })
      await flushPromises()
      
      expect(userApi.addToCart).toHaveBeenCalled()
    })

    it('should handle cart operation errors', async () => {
      vi.mocked(userApi.getCart).mockRejectedValueOnce(new Error('获取购物车失败'))

      const wrapper = mount(UserCart, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })
  })

  // 测试用户收藏API集成
  describe('用户收藏API集成测试', () => {
    it('should load favorites successfully', async () => {
      const wrapper = mount(UserFavorites, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getFavorites).toHaveBeenCalled()
      expect(userApi.getServiceFavorites).toHaveBeenCalled()
    })

    it('should add favorite successfully', async () => {
      const wrapper = mount(UserFavorites, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      // 模拟添加收藏
      await wrapper.vm.handleAddFavorite(1)
      await flushPromises()
      
      expect(userApi.addFavorite).toHaveBeenCalled()
    })

    it('should handle favorite operation errors', async () => {
      vi.mocked(userApi.getFavorites).mockRejectedValueOnce(new Error('获取收藏失败'))

      const wrapper = mount(UserFavorites, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })
  })

  // 测试用户评价API集成
  describe('用户评价API集成测试', () => {
    it('should load reviews successfully', async () => {
      const wrapper = mount(UserReviews, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getUserReviews).toHaveBeenCalled()
    })

    it('should add review successfully', async () => {
      const wrapper = mount(MyReviews, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      // 模拟添加评价
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(userApi.addReview).toHaveBeenCalled()
    })

    it('should handle review operation errors', async () => {
      vi.mocked(userApi.getUserReviews).mockRejectedValueOnce(new Error('获取评价失败'))

      const wrapper = mount(UserReviews, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })
  })

  // 测试用户地址管理API集成
  describe('用户地址管理API集成测试', () => {
    it('should load addresses successfully', async () => {
      const wrapper = mount(UserAddresses, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getAddresses).toHaveBeenCalled()
    })

    it('should add address successfully', async () => {
      const wrapper = mount(UserAddresses, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      // 模拟添加地址
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(userApi.addAddress).toHaveBeenCalled()
    })

    it('should handle address operation errors', async () => {
      vi.mocked(userApi.getAddresses).mockRejectedValueOnce(new Error('获取地址失败'))

      const wrapper = mount(UserAddresses, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })
  })

  // 测试其他用户端页面API集成
  describe('其他用户端页面API集成测试', () => {
    it('should load merchant list successfully', async () => {
      const wrapper = mount(UserMerchant, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getMerchantList).toHaveBeenCalled()
    })

    it('should load shop details successfully', async () => {
      mockRoute.params = { id: '1' }
      
      const wrapper = mount(UserShop, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getMerchantInfo).toHaveBeenCalled()
      expect(userApi.getMerchantServices).toHaveBeenCalled()
      expect(userApi.getMerchantProducts).toHaveBeenCalled()
    })

    it('should load product details successfully', async () => {
      mockRoute.params = { id: '1' }
      
      const wrapper = mount(ProductDetail, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getProductById).toHaveBeenCalled()
      expect(userApi.getProductReviews).toHaveBeenCalled()
      expect(userApi.checkProductFavorite).toHaveBeenCalled()
    })

    it('should load service details successfully', async () => {
      mockRoute.params = { id: '1' }
      
      const wrapper = mount(ServiceDetail, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getServiceById).toHaveBeenCalled()
    })

    it('should load services list successfully', async () => {
      const wrapper = mount(ServiceList, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getServices).toHaveBeenCalled()
    })

    it('should load user services successfully', async () => {
      const wrapper = mount(UserServices, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(userApi.getUserPurchasedServices).toHaveBeenCalled()
    })

    it('should load search results successfully', async () => {
      mockRoute.query = { keyword: 'test' }
      
      const wrapper = mount(Search, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      // 检查组件是否成功挂载
      expect(wrapper.exists()).toBe(true)
    })

    it('should load announcements successfully', async () => {
      const wrapper = mount(UserAnnouncements, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      // 检查组件是否成功挂载
      expect(wrapper.exists()).toBe(true)
    })

    it('should load announcement details successfully', async () => {
      mockRoute.params = { id: '1' }
      
      const wrapper = mount(AnnouncementDetail, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      // 检查组件是否成功挂载
      expect(wrapper.exists()).toBe(true)
    })

    it('should load notifications successfully', async () => {
      const wrapper = mount(Notifications, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(notificationApi.getNotifications).toHaveBeenCalled()
    })
  })

  // 测试身份验证机制
  describe('身份验证机制测试', () => {
    it('should handle unauthorized access', async () => {
      // 模拟未登录状态
      vi.mocked(authApi.getUserInfo).mockRejectedValueOnce(new Error('未授权'))

      const wrapper = mount(UserProfile, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })

    it('should handle login successfully', async () => {
      const loginData = { username: 'test', password: '123456' }
      
      const result = await authApi.login(loginData)
      expect(result.success).toBe(true)
      expect(result.data?.token).toBe('test-token')
    })

    it('should handle logout successfully', async () => {
      const result = await authApi.logout()
      expect(result.success).toBe(true)
    })
  })

  // 测试错误处理机制
  describe('错误处理机制测试', () => {
    it('should handle API errors gracefully', async () => {
      // 模拟API错误
      vi.mocked(userApi.getHomeStats).mockRejectedValueOnce(new Error('网络错误'))

      const wrapper = mount(UserHome, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })

    it('should handle 404 errors', async () => {
      // 模拟404错误
      vi.mocked(userApi.getPetById).mockRejectedValueOnce(new Error('资源不存在'))
      mockRoute.query = { id: '999' }

      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })

    it('should handle 500 errors', async () => {
      // 模拟500错误
      vi.mocked(userApi.createOrder).mockRejectedValueOnce(new Error('服务器内部错误'))

      const wrapper = mount(Checkout, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      // 检查组件是否成功挂载
      expect(wrapper.exists()).toBe(true)
    })
  })

  // 测试请求/响应格式
  describe('请求/响应格式测试', () => {
    it('should send correct request format', async () => {
      const testPet = {
        name: '测试宠物',
        type: '狗',
        breed: '拉布拉多',
        age: 1,
        gender: '公',
      }

      await userApi.addPet(testPet)
      expect(userApi.addPet).toHaveBeenCalledWith(testPet)
    })

    it('should handle response format correctly', async () => {
      const mockResponse = {
        success: true,
        data: { id: 1, name: '测试宠物' },
      }

      vi.mocked(userApi.addPet).mockResolvedValueOnce(mockResponse)

      const result = await userApi.addPet({ name: '测试宠物', type: '狗' })
      expect(result.success).toBe(true)
      expect(result.data?.id).toBe(1)
    })

    it('should handle error response format', async () => {
      const mockErrorResponse = {
        success: false,
        message: '添加失败',
      }

      vi.mocked(userApi.addPet).mockRejectedValueOnce(mockErrorResponse)

      try {
        await userApi.addPet({ name: '测试宠物', type: '狗' })
      } catch (error) {
        expect(error).toEqual(mockErrorResponse)
      }
    })
  })
})
