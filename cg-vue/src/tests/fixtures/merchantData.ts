export interface Merchant {
  id: number
  name: string
  logo: string
  address: string
  rating: number
  serviceCount: number
  phone: string
  email: string
  description: string
  contactPerson: string
  status: 'approved' | 'pending' | 'rejected' | 'disabled'
  createdAt: string
  updatedAt: string
}

export interface Service {
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

export interface Product {
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

export interface User {
  id: number
  username: string
  email: string
  phone: string
  avatar: string
  status: 'active' | 'disabled'
  createdAt: string
  updatedAt: string
}

export interface Pet {
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

export interface Appointment {
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

export interface ProductOrder {
  id: number
  userId: number
  merchantId: number
  totalPrice: number
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'
  shippingAddress: string
  createdAt: string
  updatedAt: string
}

export interface ProductOrderItem {
  id: number
  orderId: number
  productId: number
  quantity: number
  price: number
}

export interface Review {
  id: number
  userId: number
  merchantId: number
  serviceId: number | null
  productId: number | null
  appointmentId: number | null
  rating: number
  comment: string
  createdAt: string
}

export interface Announcement {
  id: number
  title: string
  content: string
  status: 'published' | 'draft'
  createdAt: string
  updatedAt: string
}

export interface Notification {
  id: number
  userId: number
  title: string
  content: string
  type: 'system' | 'order' | 'appointment' | 'review'
  isRead: boolean
  createdAt: string
}

export interface Address {
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

export interface Category {
  id: number
  merchantId: number
  name: string
  icon: string
  sort: number
  status: 'enabled' | 'disabled'
  createdAt: string
  updatedAt: string
}

export interface Admin {
  id: number
  username: string
  email: string
  avatar: string
  role: string
  createdAt: string
  updatedAt: string
}

export interface Log {
  id: number
  adminId: number
  action: string
  targetType: string
  targetId: number
  detail: string
  ipAddress: string
  createdAt: string
}

export interface Role {
  id: number
  name: string
  description: string
  permissions: string[]
  createdAt: string
  updatedAt: string
}

export const defaultMerchant: Merchant = {
  id: 1,
  name: '测试商家',
  logo: 'https://picsum.photos/100/100?random=1',
  address: '北京市朝阳区测试路1号',
  rating: 4.5,
  serviceCount: 10,
  phone: '13800138000',
  email: 'test@merchant.com',
  description: '这是一个测试商家',
  contactPerson: '张先生',
  status: 'approved',
  createdAt: '2024-01-01 10:00:00',
  updatedAt: '2024-04-01 10:00:00',
}

export const defaultService: Service = {
  id: 1,
  merchantId: 1,
  name: '宠物洗澡',
  description: '专业宠物洗澡服务',
  price: 99.99,
  duration: 60,
  image: 'https://picsum.photos/200/200?random=1',
  status: 'enabled',
  createdAt: '2024-01-01 10:00:00',
  updatedAt: '2024-04-01 10:00:00',
}

export const defaultProduct: Product = {
  id: 1,
  merchantId: 1,
  name: '优质狗粮',
  description: '天然有机狗粮',
  price: 199.99,
  stock: 100,
  image: 'https://picsum.photos/200/200?random=2',
  status: 'enabled',
  createdAt: '2024-01-01 10:00:00',
  updatedAt: '2024-04-01 10:00:00',
}

export const defaultUser: User = {
  id: 1,
  username: '测试用户',
  email: 'test@user.com',
  phone: '13900139000',
  avatar: 'https://picsum.photos/100/100?random=3',
  status: 'active',
  createdAt: '2024-01-01 10:00:00',
  updatedAt: '2024-04-01 10:00:00',
}

export const defaultPet: Pet = {
  id: 1,
  userId: 1,
  name: '小白',
  type: '狗',
  breed: '金毛',
  age: 2,
  gender: 'male',
  avatar: 'https://picsum.photos/100/100?random=4',
  description: '一只可爱的金毛',
  createdAt: '2024-01-01 10:00:00',
  updatedAt: '2024-04-01 10:00:00',
}

export const defaultAppointment: Appointment = {
  id: 1,
  userId: 1,
  merchantId: 1,
  serviceId: 1,
  petId: 1,
  appointmentTime: '2024-05-01 14:00:00',
  status: 'pending',
  totalPrice: 99.99,
  notes: '请准时',
  createdAt: '2024-04-20 10:00:00',
  updatedAt: '2024-04-20 10:00:00',
}

export const defaultProductOrder: ProductOrder = {
  id: 1,
  userId: 1,
  merchantId: 1,
  totalPrice: 199.99,
  status: 'pending',
  shippingAddress: '北京市朝阳区测试路1号',
  createdAt: '2024-04-20 10:00:00',
  updatedAt: '2024-04-20 10:00:00',
}

export const defaultProductOrderItem: ProductOrderItem = {
  id: 1,
  orderId: 1,
  productId: 1,
  quantity: 1,
  price: 199.99,
}

export const defaultReview: Review = {
  id: 1,
  userId: 1,
  merchantId: 1,
  serviceId: 1,
  productId: null,
  appointmentId: 1,
  rating: 5,
  comment: '服务非常好',
  createdAt: '2024-04-21 10:00:00',
}

export const defaultAnnouncement: Announcement = {
  id: 1,
  title: '测试公告',
  content: '这是一条测试公告内容',
  status: 'published',
  createdAt: '2024-04-01 10:00:00',
  updatedAt: '2024-04-01 10:00:00',
}

export const defaultNotification: Notification = {
  id: 1,
  userId: 1,
  title: '测试通知',
  content: '这是一条测试通知内容',
  type: 'system',
  isRead: false,
  createdAt: '2024-04-20 10:00:00',
}

export const defaultAddress: Address = {
  id: 1,
  userId: 1,
  receiverName: '张三',
  phone: '13800138000',
  province: '北京市',
  city: '北京市',
  district: '朝阳区',
  detailAddress: '测试路1号',
  isDefault: true,
  createdAt: '2024-01-01 10:00:00',
  updatedAt: '2024-04-01 10:00:00',
}

export const defaultCategory: Category = {
  id: 1,
  merchantId: 1,
  name: '宠物食品',
  icon: 'food',
  sort: 1,
  status: 'enabled',
  createdAt: '2024-01-01 10:00:00',
  updatedAt: '2024-04-01 10:00:00',
}

export const defaultAdmin: Admin = {
  id: 1,
  username: 'admin',
  email: 'admin@test.com',
  avatar: 'https://picsum.photos/100/100?random=5',
  role: 'super_admin',
  createdAt: '2024-01-01 10:00:00',
  updatedAt: '2024-04-01 10:00:00',
}

export const defaultLog: Log = {
  id: 1,
  adminId: 1,
  action: 'create',
  targetType: 'user',
  targetId: 1,
  detail: '创建用户',
  ipAddress: '127.0.0.1',
  createdAt: '2024-04-20 10:00:00',
}

export const defaultRole: Role = {
  id: 1,
  name: '管理员',
  description: '系统管理员角色',
  permissions: ['user:read', 'user:write', 'merchant:read', 'merchant:write'],
  createdAt: '2024-01-01 10:00:00',
  updatedAt: '2024-04-01 10:00:00',
}

export const createMerchant = (overrides: Partial<Merchant> = {}): Merchant => ({
  ...defaultMerchant,
  ...overrides,
})

export const createService = (overrides: Partial<Service> = {}): Service => ({
  ...defaultService,
  ...overrides,
})

export const createProduct = (overrides: Partial<Product> = {}): Product => ({
  ...defaultProduct,
  ...overrides,
})

export const createUser = (overrides: Partial<User> = {}): User => ({
  ...defaultUser,
  ...overrides,
})

export const createPet = (overrides: Partial<Pet> = {}): Pet => ({
  ...defaultPet,
  ...overrides,
})

export const createAppointment = (overrides: Partial<Appointment> = {}): Appointment => ({
  ...defaultAppointment,
  ...overrides,
})

export const createProductOrder = (overrides: Partial<ProductOrder> = {}): ProductOrder => ({
  ...defaultProductOrder,
  ...overrides,
})

export const createProductOrderItem = (overrides: Partial<ProductOrderItem> = {}): ProductOrderItem => ({
  ...defaultProductOrderItem,
  ...overrides,
})

export const createReview = (overrides: Partial<Review> = {}): Review => ({
  ...defaultReview,
  ...overrides,
})

export const createAnnouncement = (overrides: Partial<Announcement> = {}): Announcement => ({
  ...defaultAnnouncement,
  ...overrides,
})

export const createNotification = (overrides: Partial<Notification> = {}): Notification => ({
  ...defaultNotification,
  ...overrides,
})

export const createAddress = (overrides: Partial<Address> = {}): Address => ({
  ...defaultAddress,
  ...overrides,
})

export const createCategory = (overrides: Partial<Category> = {}): Category => ({
  ...defaultCategory,
  ...overrides,
})

export const createAdmin = (overrides: Partial<Admin> = {}): Admin => ({
  ...defaultAdmin,
  ...overrides,
})

export const createLog = (overrides: Partial<Log> = {}): Log => ({
  ...defaultLog,
  ...overrides,
})

export const createRole = (overrides: Partial<Role> = {}): Role => ({
  ...defaultRole,
  ...overrides,
})

export const createMerchantList = (count: number, overrides: Partial<Merchant> = {}): Merchant[] => {
  return Array.from({ length: count }, (_, index) =>
    createMerchant({
      id: index + 1,
      name: `测试商家${index + 1}`,
      ...overrides,
    })
  )
}

export const createServiceList = (count: number, overrides: Partial<Service> = {}): Service[] => {
  return Array.from({ length: count }, (_, index) =>
    createService({
      id: index + 1,
      name: `测试服务${index + 1}`,
      ...overrides,
    })
  )
}

export const createProductList = (count: number, overrides: Partial<Product> = {}): Product[] => {
  return Array.from({ length: count }, (_, index) =>
    createProduct({
      id: index + 1,
      name: `测试商品${index + 1}`,
      ...overrides,
    })
  )
}

export const createUserList = (count: number, overrides: Partial<User> = {}): User[] => {
  return Array.from({ length: count }, (_, index) =>
    createUser({
      id: index + 1,
      username: `测试用户${index + 1}`,
      ...overrides,
    })
  )
}

export const createAppointmentList = (count: number, overrides: Partial<Appointment> = {}): Appointment[] => {
  return Array.from({ length: count }, (_, index) =>
    createAppointment({
      id: index + 1,
      ...overrides,
    })
  )
}

export const createReviewList = (count: number, overrides: Partial<Review> = {}): Review[] => {
  return Array.from({ length: count }, (_, index) =>
    createReview({
      id: index + 1,
      comment: `测试评价${index + 1}`,
      ...overrides,
    })
  )
}

export const createAnnouncementList = (count: number, overrides: Partial<Announcement> = {}): Announcement[] => {
  return Array.from({ length: count }, (_, index) =>
    createAnnouncement({
      id: index + 1,
      title: `测试公告${index + 1}`,
      ...overrides,
    })
  )
}

export const createPaginatedResponse = <T>(
  data: T[],
  page = 1,
  pageSize = 10
): { data: T[]; total: number; page: number; pageSize: number } => {
  const start = (page - 1) * pageSize
  const end = start + pageSize
  return {
    data: data.slice(start, end),
    total: data.length,
    page,
    pageSize,
  }
}

export const createSuccessResponse = <T>(data: T, message = 'success') => ({
  code: 200,
  message,
  data,
})

export const createErrorResponse = (message: string, code = 400) => ({
  code,
  message,
  data: null,
})
