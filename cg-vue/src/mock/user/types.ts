export interface MockUser {
  id: number
  username: string
  email: string
  phone: string
  avatar: string
  createdAt: string
  updatedAt: string
}

export interface MockPet {
  id: number
  userId: number
  name: string
  type: string
  breed: string
  age: number
  gender: string
  avatar: string
  description: string
  weight?: number
  bodyType?: string
  furColor?: string
  personality?: string
  createdAt: string
  updatedAt: string
}

export interface MockService {
  id: number
  merchantId: number
  merchantName: string
  name: string
  description: string
  price: number
  duration: number
  image: string
  category: string
  status: 'enabled' | 'disabled'
  rating: number
  reviewCount: number
  createdAt: string
  updatedAt: string
}

export interface MockProduct {
  id: number
  merchantId: number
  merchantName: string
  name: string
  description: string
  price: number
  stock: number
  image: string
  category: string
  status: 'enabled' | 'disabled'
  sales: number
  rating: number
  reviewCount: number
  createdAt: string
  updatedAt: string
}

export interface MockMerchant {
  id: number
  name: string
  logo: string
  contactPerson: string
  phone: string
  email: string
  address: string
  description: string
  rating: number
  reviewCount: number
  serviceCount: number
  productCount: number
  status: 'pending' | 'approved' | 'rejected'
  createdAt: string
  updatedAt: string
}

export interface MockAppointment {
  id: number
  userId: number
  userName: string
  merchantId: number
  merchantName: string
  serviceId: number
  serviceName: string
  petId: number
  petName: string
  appointmentTime: string
  totalPrice: number
  status: 'pending' | 'confirmed' | 'completed' | 'cancelled'
  notes: string
  createdAt: string
  updatedAt: string
}

export interface MockProductOrder {
  id: number
  orderNo: string
  userId: number
  userName: string
  merchantId: number
  merchantName: string
  totalPrice: number
  freight: number
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'
  payMethod: string
  payTime: string | null
  shipTime: string | null
  completeTime: string | null
  cancelTime: string | null
  remark: string
  shippingAddress: MockOrderAddress
  items: MockOrderItem[]
  createdAt: string
  updatedAt: string
}

export interface MockOrderItem {
  id: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
  subtotal: number
}

export interface MockOrderAddress {
  name: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
}

export interface MockReview {
  id: number
  userId: number
  userName: string
  userAvatar: string
  merchantId: number
  merchantName: string
  serviceId: number
  serviceName: string
  appointmentId: number
  rating: number
  comment: string
  images: string[]
  reply: string | null
  replyTime: string | null
  createdAt: string
}

export interface MockAddress {
  id: number
  userId: number
  contactName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: boolean
  createdAt: string
  updatedAt: string
}

export interface MockCartItem {
  id: number
  userId: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
  merchantId: number
  merchantName: string
  selected: boolean
  createdAt: string
}

export interface MockFavorite {
  id: number
  userId: number
  merchantId: number
  merchantName: string
  merchantLogo: string
  merchantAddress: string
  merchantPhone: string
  merchantRating: number
  createdAt: string
}

export interface MockNotification {
  id: number
  userId: number
  title: string
  content: string
  type: 'system' | 'order' | 'appointment' | 'review'
  isRead: boolean
  createdAt: string
}

export interface MockAnnouncement {
  id: number
  title: string
  content: string
  summary: string
  author: string
  status: 'published' | 'draft'
  isRead: boolean
  publishedAt: string
  createdAt: string
}

export interface MockDashboardStats {
  petCount: number
  pendingAppointmentCount: number
  reviewCount: number
  favoriteCount: number
}

export interface MockRecentActivity {
  id: number
  type: 'appointment' | 'order' | 'review'
  title: string
  description: string
  time: string
}

export interface PaginatedResponse<T> {
  code: number
  message: string
  data: {
    data: T[]
    total: number
    page: number
    pageSize: number
  }
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export type MockHandler = () => void

export interface MockModule {
  name: string
  handlers: MockHandler[]
}
