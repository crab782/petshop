import { vi } from 'vitest'

export interface MockAdminUser {
  id: number
  username: string
  email?: string
  phone?: string
  role: string
  status: string
  createTime: string
}

export interface MockAdminMerchant {
  id: number
  name: string
  description?: string
  address?: string
  phone?: string
  status: string
  rating?: number
  createTime: string
  licenseImage?: string
  contactPerson?: string
}

export interface MockAdminService {
  id: number
  name: string
  description?: string
  price: number
  merchantId: number
  merchantName: string
  category?: string
  status?: 'enabled' | 'disabled'
  duration?: number
  image?: string
  createdAt?: string
}

export interface MockAdminProduct {
  id: number
  name: string
  merchantId: number
  merchantName: string
  price: number
  stock: number
  sales: number
  status: 'active' | 'inactive'
  description?: string
  image?: string
  createTime: string
}

export interface MockAdminReview {
  id: number
  serviceId: number
  serviceName: string
  merchantId: number
  merchantName: string
  userId: number
  userName: string
  rating: number
  comment: string
  status: 'pending' | 'approved' | 'violation'
  createdAt: string
  appointmentId?: number
  orderNo?: string
}

export interface MockAdminAnnouncement {
  id: number
  title: string
  content: string
  status: 'published' | 'draft'
  publishTime: string
}

export interface MockAdminShop {
  id: number
  name: string
  applicant?: string
  applicantPhone?: string
  address?: string
  description?: string
  createTime: string
  licenseImage?: string
}

export interface MockAdminRole {
  id: number
  name: string
  description?: string
  permissions?: number[]
  permissionCount?: number
}

export interface MockAdminPermission {
  id: number
  name: string
  code?: string
  children?: MockAdminPermission[]
}

export interface MockAdminActivity {
  id: number
  name: string
  type: string
  description?: string
  startTime: string
  endTime: string
  status: 'pending' | 'active' | 'ended' | 'disabled'
  createTime?: string
}

export interface MockAdminTask {
  id: number
  name: string
  type: string
  description?: string
  cronExpression?: string
  executeTime?: string
  status: 'pending' | 'running' | 'completed' | 'failed'
  result?: string
  createdAt: string
  updatedAt: string
}

export interface MockAdminLog {
  id: number
  operator: string
  operationType: string
  description: string
  ipAddress: string
  operationTime: string
}

export interface MockAdminDashboardData {
  userCount: number
  merchantCount: number
  orderCount: number
  serviceCount: number
}

export interface MockMerchantDetail extends MockAdminMerchant {
  services: MockAdminService[]
  products: MockAdminProduct[]
  orders: any[]
  reviews: MockAdminReview[]
}

export interface MockUserDetail extends MockAdminUser {
  recentOrders: MockRecentOrder[]
  recentAppointments: MockRecentAppointment[]
  reviews: MockUserReview[]
}

export interface MockRecentOrder {
  id: number
  orderNo: string
  serviceName: string
  totalPrice: number
  status: string
  createTime: string
}

export interface MockRecentAppointment {
  id: number
  serviceName: string
  merchantName: string
  appointmentTime: string
  status: string
}

export interface MockUserReview {
  id: number
  serviceName: string
  merchantName: string
  rating: number
  comment: string
  createdAt: string
}

export interface MockSystemConfig {
  websiteName: string
  websiteLogo: string
  contactPhone: string
  contactEmail: string
  copyright: string
  appointmentOpenTime: string
  minAdvanceHours: number
  maxAdvanceDays: number
  enableReview: boolean
  reviewCharLimit: number
}

export const defaultAdminUser: MockAdminUser = {
  id: 1,
  username: 'testuser',
  email: 'test@example.com',
  phone: '13800138000',
  role: 'user',
  status: 'active',
  createTime: '2024-01-01T00:00:00.000Z',
}

export const defaultAdminMerchant: MockAdminMerchant = {
  id: 1,
  name: '测试商家',
  description: '专业宠物服务商家',
  address: '北京市朝阳区测试路1号',
  phone: '13900139000',
  status: 'approved',
  rating: 4.5,
  createTime: '2024-01-01T00:00:00.000Z',
  licenseImage: 'https://picsum.photos/200/200?random=30',
  contactPerson: '张先生',
}

export const defaultAdminService: MockAdminService = {
  id: 1,
  name: '宠物洗澡',
  description: '专业宠物洗澡服务',
  price: 99.99,
  merchantId: 1,
  merchantName: '测试商家',
  category: '美容',
  status: 'enabled',
  duration: 60,
  image: 'https://picsum.photos/200/200?random=31',
  createdAt: '2024-01-01T00:00:00.000Z',
}

export const defaultAdminProduct: MockAdminProduct = {
  id: 1,
  name: '优质狗粮',
  merchantId: 1,
  merchantName: '测试商家',
  price: 199.99,
  stock: 100,
  sales: 500,
  status: 'active',
  description: '天然有机狗粮',
  image: 'https://picsum.photos/200/200?random=32',
  createTime: '2024-01-01T00:00:00.000Z',
}

export const defaultAdminReview: MockAdminReview = {
  id: 1,
  serviceId: 1,
  serviceName: '宠物洗澡',
  merchantId: 1,
  merchantName: '测试商家',
  userId: 1,
  userName: '测试用户',
  rating: 5,
  comment: '服务非常好，推荐！',
  status: 'pending',
  createdAt: '2024-04-21T10:00:00.000Z',
  appointmentId: 1,
  orderNo: 'ORD202404200001',
}

export const defaultAdminAnnouncement: MockAdminAnnouncement = {
  id: 1,
  title: '系统公告',
  content: '欢迎使用宠物服务平台',
  status: 'published',
  publishTime: '2024-04-01T00:00:00.000Z',
}

export const defaultAdminShop: MockAdminShop = {
  id: 1,
  name: '新入驻商家',
  applicant: '李先生',
  applicantPhone: '13800138001',
  address: '北京市海淀区测试路2号',
  description: '新开宠物店',
  createTime: '2024-04-20T00:00:00.000Z',
  licenseImage: 'https://picsum.photos/200/200?random=33',
}

export const defaultAdminRole: MockAdminRole = {
  id: 1,
  name: '管理员',
  description: '系统管理员角色',
  permissions: [1, 2, 3, 4],
  permissionCount: 4,
}

export const defaultAdminPermission: MockAdminPermission = {
  id: 1,
  name: '用户管理',
  code: 'user:manage',
  children: [
    { id: 2, name: '查看用户', code: 'user:read' },
    { id: 3, name: '编辑用户', code: 'user:write' },
  ],
}

export const defaultAdminActivity: MockAdminActivity = {
  id: 1,
  name: '新用户优惠活动',
  type: 'discount',
  description: '新用户首次下单享8折优惠',
  startTime: '2024-05-01T00:00:00.000Z',
  endTime: '2024-05-31T23:59:59.000Z',
  status: 'active',
  createTime: '2024-04-20T00:00:00.000Z',
}

export const defaultAdminTask: MockAdminTask = {
  id: 1,
  name: '清理过期订单',
  type: 'cleanup',
  description: '每天凌晨清理过期未支付订单',
  cronExpression: '0 0 0 * * ?',
  executeTime: '2024-04-21T00:00:00.000Z',
  status: 'completed',
  result: '清理了10个过期订单',
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-04-21T00:00:00.000Z',
}

export const defaultAdminLog: MockAdminLog = {
  id: 1,
  operator: 'admin',
  operationType: 'create',
  description: '创建用户 testuser',
  ipAddress: '127.0.0.1',
  operationTime: '2024-04-20T10:00:00.000Z',
}

export const defaultAdminDashboardData: MockAdminDashboardData = {
  userCount: 2580,
  merchantCount: 150,
  orderCount: 1250,
  serviceCount: 320,
}

export const defaultSystemConfig: MockSystemConfig = {
  websiteName: '宠物服务平台',
  websiteLogo: 'https://picsum.photos/100/100?random=34',
  contactPhone: '400-123-4567',
  contactEmail: 'support@petshop.com',
  copyright: '© 2024 宠物服务平台',
  appointmentOpenTime: '09:00',
  minAdvanceHours: 2,
  maxAdvanceDays: 30,
  enableReview: true,
  reviewCharLimit: 500,
}

export const createAdminUser = (overrides: Partial<MockAdminUser> = {}): MockAdminUser => ({
  ...defaultAdminUser,
  ...overrides,
})

export const createAdminMerchant = (overrides: Partial<MockAdminMerchant> = {}): MockAdminMerchant => ({
  ...defaultAdminMerchant,
  ...overrides,
})

export const createAdminService = (overrides: Partial<MockAdminService> = {}): MockAdminService => ({
  ...defaultAdminService,
  ...overrides,
})

export const createAdminProduct = (overrides: Partial<MockAdminProduct> = {}): MockAdminProduct => ({
  ...defaultAdminProduct,
  ...overrides,
})

export const createAdminReview = (overrides: Partial<MockAdminReview> = {}): MockAdminReview => ({
  ...defaultAdminReview,
  ...overrides,
})

export const createAdminAnnouncement = (overrides: Partial<MockAdminAnnouncement> = {}): MockAdminAnnouncement => ({
  ...defaultAdminAnnouncement,
  ...overrides,
})

export const createAdminShop = (overrides: Partial<MockAdminShop> = {}): MockAdminShop => ({
  ...defaultAdminShop,
  ...overrides,
})

export const createAdminRole = (overrides: Partial<MockAdminRole> = {}): MockAdminRole => ({
  ...defaultAdminRole,
  ...overrides,
})

export const createAdminPermission = (overrides: Partial<MockAdminPermission> = {}): MockAdminPermission => ({
  ...defaultAdminPermission,
  ...overrides,
})

export const createAdminActivity = (overrides: Partial<MockAdminActivity> = {}): MockAdminActivity => ({
  ...defaultAdminActivity,
  ...overrides,
})

export const createAdminTask = (overrides: Partial<MockAdminTask> = {}): MockAdminTask => ({
  ...defaultAdminTask,
  ...overrides,
})

export const createAdminLog = (overrides: Partial<MockAdminLog> = {}): MockAdminLog => ({
  ...defaultAdminLog,
  ...overrides,
})

export const createAdminUserList = (count: number): MockAdminUser[] =>
  Array.from({ length: count }, (_, i) => createAdminUser({ id: i + 1, username: `user${i + 1}` }))

export const createAdminMerchantList = (count: number): MockAdminMerchant[] =>
  Array.from({ length: count }, (_, i) => createAdminMerchant({ id: i + 1, name: `商家${i + 1}` }))

export const createAdminServiceList = (count: number): MockAdminService[] =>
  Array.from({ length: count }, (_, i) => createAdminService({ id: i + 1, name: `服务${i + 1}` }))

export const createAdminProductList = (count: number): MockAdminProduct[] =>
  Array.from({ length: count }, (_, i) => createAdminProduct({ id: i + 1, name: `商品${i + 1}` }))

export const createAdminReviewList = (count: number): MockAdminReview[] =>
  Array.from({ length: count }, (_, i) => createAdminReview({ id: i + 1, rating: Math.min(5, Math.floor(Math.random() * 5) + 1) }))

export const createAdminAnnouncementList = (count: number): MockAdminAnnouncement[] =>
  Array.from({ length: count }, (_, i) => createAdminAnnouncement({ id: i + 1, title: `公告${i + 1}` }))

export const createAdminShopList = (count: number): MockAdminShop[] =>
  Array.from({ length: count }, (_, i) => createAdminShop({ id: i + 1, name: `店铺${i + 1}` }))

export const createAdminRoleList = (count: number): MockAdminRole[] =>
  Array.from({ length: count }, (_, i) => createAdminRole({ id: i + 1, name: `角色${i + 1}` }))

export const createAdminActivityList = (count: number): MockAdminActivity[] =>
  Array.from({ length: count }, (_, i) => createAdminActivity({ id: i + 1, name: `活动${i + 1}` }))

export const createAdminTaskList = (count: number): MockAdminTask[] =>
  Array.from({ length: count }, (_, i) => createAdminTask({ id: i + 1, name: `任务${i + 1}` }))

export const createAdminLogList = (count: number): MockAdminLog[] =>
  Array.from({ length: count }, (_, i) => createAdminLog({ id: i + 1, description: `操作${i + 1}` }))

export const mockAdminApi = {
  getAdminDashboard: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: defaultAdminDashboardData,
    })
  ),

  getAllUsers: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminUserList(20),
    })
  ),

  updateUserStatus: vi.fn((id: number, status: string) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createAdminUser({ id, status }),
    })
  ),

  deleteUser: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  batchUpdateUserStatus: vi.fn((ids: number[], status: string) =>
    Promise.resolve({
      code: 200,
      message: '批量更新成功',
      data: null,
    })
  ),

  batchDeleteUsers: vi.fn((ids: number[]) =>
    Promise.resolve({
      code: 200,
      message: '批量删除成功',
      data: null,
    })
  ),

  getUserDetailById: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        ...createAdminUser({ id }),
        recentOrders: [
          { id: 1, orderNo: 'ORD001', serviceName: '宠物洗澡', totalPrice: 99.99, status: 'completed', createTime: '2024-04-20T10:00:00.000Z' },
        ],
        recentAppointments: [
          { id: 1, serviceName: '宠物洗澡', merchantName: '测试商家', appointmentTime: '2024-05-01T14:00:00.000Z', status: 'pending' },
        ],
        reviews: [
          { id: 1, serviceName: '宠物洗澡', merchantName: '测试商家', rating: 5, comment: '服务很好', createdAt: '2024-04-21T10:00:00.000Z' },
        ],
      },
    })
  ),

  getAllMerchants: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminMerchantList(20),
    })
  ),

  updateMerchantStatus: vi.fn((id: number, status: string) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createAdminMerchant({ id, status }),
    })
  ),

  getMerchantDetailById: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        ...createAdminMerchant({ id }),
        services: createAdminServiceList(5),
        products: createAdminProductList(5),
        orders: [],
        reviews: createAdminReviewList(5),
      },
    })
  ),

  getPendingMerchants: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminMerchantList(5).map(m => ({ ...m, status: 'pending' })),
    })
  ),

  auditMerchant: vi.fn((id: number, status: string, reason?: string) =>
    Promise.resolve({
      code: 200,
      message: '审核成功',
      data: createAdminMerchant({ id, status }),
    })
  ),

  deleteMerchant: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  getAllServices: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminServiceList(20),
    })
  ),

  updateServiceStatus: vi.fn((id: number, status: string) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createAdminService({ id, status: status as any }),
    })
  ),

  batchUpdateServiceStatus: vi.fn((ids: number[], status: string) =>
    Promise.resolve({
      code: 200,
      message: '批量更新成功',
      data: null,
    })
  ),

  batchDeleteServices: vi.fn((ids: number[]) =>
    Promise.resolve({
      code: 200,
      message: '批量删除成功',
      data: null,
    })
  ),

  deleteService: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  getAllProducts: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminProductList(20),
    })
  ),

  getAllProductsForAdmin: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminProductList(20),
    })
  ),

  updateProductStatus: vi.fn((id: number, status: string) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createAdminProduct({ id, status: status as any }),
    })
  ),

  deleteProduct: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  batchUpdateProductStatus: vi.fn((ids: number[], status: string) =>
    Promise.resolve({
      code: 200,
      message: '批量更新成功',
      data: null,
    })
  ),

  batchDeleteProducts: vi.fn((ids: number[]) =>
    Promise.resolve({
      code: 200,
      message: '批量删除成功',
      data: null,
    })
  ),

  getAllReviews: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminReviewList(20),
    })
  ),

  deleteReview: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  getReviewsForAudit: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminReviewList(10).map(r => ({ ...r, status: 'pending' })),
    })
  ),

  approveReview: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '审核通过',
      data: createAdminReview({ id, status: 'approved' }),
    })
  ),

  markReviewViolation: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '已标记违规',
      data: createAdminReview({ id, status: 'violation' }),
    })
  ),

  getPendingShops: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminShopList(5),
    })
  ),

  auditShop: vi.fn((id: number, status: string, reason?: string) =>
    Promise.resolve({
      code: 200,
      message: '审核成功',
      data: createAdminShop({ id }),
    })
  ),

  getOperationLogs: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminLogList(20),
    })
  ),

  deleteLog: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  clearLogs: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: '清空成功',
      data: null,
    })
  ),

  getRoles: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminRoleList(5),
    })
  ),

  addRole: vi.fn((data: Partial<MockAdminRole>) =>
    Promise.resolve({
      code: 200,
      message: '添加成功',
      data: createAdminRole({ id: Date.now(), ...data }),
    })
  ),

  updateRole: vi.fn((id: number, data: Partial<MockAdminRole>) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createAdminRole({ id, ...data }),
    })
  ),

  deleteRole: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  getPermissions: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: [
        {
          id: 1,
          name: '用户管理',
          code: 'user',
          children: [
            { id: 11, name: '查看用户', code: 'user:read' },
            { id: 12, name: '编辑用户', code: 'user:write' },
            { id: 13, name: '删除用户', code: 'user:delete' },
          ],
        },
        {
          id: 2,
          name: '商家管理',
          code: 'merchant',
          children: [
            { id: 21, name: '查看商家', code: 'merchant:read' },
            { id: 22, name: '编辑商家', code: 'merchant:write' },
            { id: 23, name: '删除商家', code: 'merchant:delete' },
          ],
        },
      ],
    })
  ),

  getActivities: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminActivityList(10),
    })
  ),

  addActivity: vi.fn((data: Partial<MockAdminActivity>) =>
    Promise.resolve({
      code: 200,
      message: '添加成功',
      data: createAdminActivity({ id: Date.now(), ...data }),
    })
  ),

  updateActivity: vi.fn((id: number, data: Partial<MockAdminActivity>) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createAdminActivity({ id, ...data }),
    })
  ),

  deleteActivity: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  toggleActivityStatus: vi.fn((id: number, status: string) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createAdminActivity({ id, status: status as any }),
    })
  ),

  getTasks: vi.fn((params?: any) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createAdminTaskList(10),
    })
  ),

  addTask: vi.fn((data: Partial<MockAdminTask>) =>
    Promise.resolve({
      code: 200,
      message: '添加成功',
      data: createAdminTask({ id: Date.now(), ...data }),
    })
  ),

  updateTask: vi.fn((id: number, data: Partial<MockAdminTask>) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createAdminTask({ id, ...data }),
    })
  ),

  deleteTask: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '删除成功',
      data: null,
    })
  ),

  executeTask: vi.fn((id: number) =>
    Promise.resolve({
      code: 200,
      message: '执行成功',
      data: createAdminTask({ id, status: 'completed', result: '执行完成' }),
    })
  ),

  getSystemConfig: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: defaultSystemConfig,
    })
  ),

  updateSystemConfig: vi.fn((data: MockSystemConfig) =>
    Promise.resolve({
      code: 200,
      message: '更新成功',
      data: { ...defaultSystemConfig, ...data },
    })
  ),
}

export const mockAdminResponses = {
  dashboard: {
    code: 200,
    message: 'success',
    data: defaultAdminDashboardData,
  },
  userList: {
    code: 200,
    message: 'success',
    data: [defaultAdminUser],
  },
  merchantList: {
    code: 200,
    message: 'success',
    data: [defaultAdminMerchant],
  },
  serviceList: {
    code: 200,
    message: 'success',
    data: [defaultAdminService],
  },
  productList: {
    code: 200,
    message: 'success',
    data: [defaultAdminProduct],
  },
  reviewList: {
    code: 200,
    message: 'success',
    data: [defaultAdminReview],
  },
  announcementList: {
    code: 200,
    message: 'success',
    data: [defaultAdminAnnouncement],
  },
  shopList: {
    code: 200,
    message: 'success',
    data: [defaultAdminShop],
  },
  roleList: {
    code: 200,
    message: 'success',
    data: [defaultAdminRole],
  },
  activityList: {
    code: 200,
    message: 'success',
    data: [defaultAdminActivity],
  },
  taskList: {
    code: 200,
    message: 'success',
    data: [defaultAdminTask],
  },
  logList: {
    code: 200,
    message: 'success',
    data: [defaultAdminLog],
  },
  systemConfig: {
    code: 200,
    message: 'success',
    data: defaultSystemConfig,
  },
}
