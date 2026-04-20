import request from './request'

export interface MerchantService {
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

export interface Order {
  id: number
  userId: number
  userName: string
  serviceId: number
  serviceName: string
  merchantId: number
  appointmentTime: string
  status: string
  remark?: string
  totalPrice: number
}

export interface Product {
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

export interface Appointment {
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

export interface Review {
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

export interface ReviewQuery {
  page: number
  pageSize: number
  rating?: number
  startDate?: string
  endDate?: string
  keyword?: string
}

export interface ReviewListResponse {
  list: Review[]
  total: number
  page: number
  pageSize: number
  ratingDistribution: {
    five: number
    four: number
    three: number
    two: number
    one: number
  }
}

export const getMerchantReviews = (query?: ReviewQuery) => {
  const params = {
    page: query?.page || 0,
    size: query?.pageSize || 10,
    sortBy: 'createdAt',
    sortDir: 'desc',
    rating: query?.rating,
    keyword: query?.keyword
  }
  return request.get<any>('/api/merchant/reviews', { params })
}

export const replyReview = (id: number, replyContent: string) => {
  return request.put<Review>(`/api/merchant/reviews/${id}/reply`, { reply: replyContent })
}

export const deleteReview = (id: number) => {
  return request.delete(`/api/merchant/reviews/${id}`)
}

export const getMerchantInfo = () => {
  return request.get<MerchantInfo>('/api/merchant/info')
}

export const updateMerchantInfo = (data: Partial<MerchantInfo>) => {
  return request.put<MerchantInfo>('/api/merchant/info', data)
}

export const getMerchantServices = () => {
  return request.get<MerchantService[]>('/api/merchant/services')
}

export const addService = (data: Omit<MerchantService, 'id'>) => {
  return request.post<MerchantService>('/api/merchant/services', data)
}

export const updateService = (id: number, data: Partial<MerchantService>) => {
  return request.put<MerchantService>(`/api/merchant/services/${id}`, data)
}

export const deleteService = (id: number) => {
  return request.delete(`/api/merchant/services/${id}`)
}

export const getServiceById = (id: number) => {
  return request.get<MerchantService>(`/api/merchant/services/${id}`)
}

export const batchUpdateServiceStatus = (ids: number[], status: 'enabled' | 'disabled') => {
  return request.put<MerchantService[]>('/api/merchant/services/batch/status', { ids, status })
}

export const batchDeleteServices = (ids: number[]) => {
  return request.delete('/api/merchant/services/batch', { data: { ids } })
}

export const getMerchantOrders = () => {
  return request.get<Order[]>('/api/merchant/appointments')
}

export const updateOrderStatus = (id: number, status: string, rejectReason?: string) => {
  return request.put<Order>(`/api/merchant/appointments/${id}/status`, { status, rejectReason })
}

export const getMerchantProducts = () => {
  return request.get<Product[]>('/api/merchant/products')
}

export const addProduct = (data: Omit<Product, 'id'>) => {
  return request.post<Product>('/api/merchant/products', data)
}

export const updateProduct = (id: number, data: Partial<Product>) => {
  return request.put<Product>(`/api/merchant/products/${id}`, data)
}

export const deleteProduct = (id: number) => {
  return request.delete(`/api/merchant/products/${id}`)
}

export const getProductById = (id: number) => {
  return request.get<Product>(`/api/merchant/products/${id}`)
}

export interface ProductQuery {
  page: number
  pageSize: number
  name?: string
  minPrice?: number
  maxPrice?: number
  stockStatus?: 'all' | 'in_stock' | 'out_of_stock'
  status?: 'all' | 'enabled' | 'disabled'
}

export interface ProductListResponse {
  list: Product[]
  total: number
  page: number
  pageSize: number
}

export const getMerchantProductsPaged = (query: any) => {
  return request.get<any>('/api/merchant/products/paged', { params: query })
}

export const updateProductStatus = (id: number, status: 'enabled' | 'disabled') => {
  return request.put<Product>(`/api/merchant/products/${id}/status`, { status })
}

export const batchUpdateProductStatus = (ids: number[], status: 'enabled' | 'disabled') => {
  return request.put<Product[]>('/api/merchant/products/batch/status', { ids, status })
}

export const batchDeleteProducts = (ids: number[]) => {
  return request.delete('/api/merchant/products/batch', { data: { ids } })
}

export const getMerchantAppointments = () => {
  return request.get<Appointment[]>('/api/merchant/appointments')
}

export const updateAppointmentStatus = (id: number, status: string, rejectReason?: string) => {
  return request.put<Appointment>(`/api/merchant/appointments/${id}/status`, { status, rejectReason })
}

export interface ProductOrder {
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

export interface Category {
  id: number
  name: string
  icon?: string
  description?: string
  sort: number
  status: 'enabled' | 'disabled'
  productCount: number
  createdAt?: string
}

export const getCategories = () => {
  return request.get<Category[]>('/api/merchant/categories')
}

export const addCategory = (data: Omit<Category, 'id' | 'productCount' | 'createdAt'>) => {
  return request.post<Category>('/api/merchant/categories', data)
}

export const updateCategory = (id: number, data: Partial<Category>) => {
  return request.put<Category>(`/api/merchant/categories/${id}`, data)
}

export const deleteCategory = (id: number) => {
  return request.delete(`/api/merchant/categories/${id}`)
}

export const updateCategoryStatus = (id: number, status: 'enabled' | 'disabled') => {
  return request.put<Category>(`/api/merchant/categories/${id}/status`, { status })
}

export const batchUpdateCategoryStatus = (ids: number[], status: 'enabled' | 'disabled') => {
  return request.put<Category[]>('/api/merchant/categories/batch/status', { ids, status })
}

export const batchDeleteCategories = (ids: number[]) => {
  return request.delete('/api/merchant/categories/batch', { data: { ids } })
}

export const getMerchantProductOrders = () => {
  return request.get<ProductOrder[]>('/api/merchant/product-orders')
}

export const updateProductOrderStatus = (id: number, status: string) => {
  return request.put<ProductOrder>(`/api/merchant/product-orders/${id}/status`, { status })
}
export const updateProductOrderLogistics = (id: number, logisticsCompany: string, trackingNumber: string) => {
  return request.put<ProductOrder>(`/api/merchant/product-orders/${id}/logistics`, {
    logisticsCompany,
    trackingNumber
  })
}

export interface RevenueStats {
  totalRevenue: number
  orderCount: number
  avgOrderValue: number
  lastPeriodRevenue: number
  growthRate: number
  serviceRevenue: number
  productRevenue: number
  serviceOrderCount: number
  productOrderCount: number
  orderList: RevenueOrderItem[]
  topServices: TopServiceItem[]
  topProducts: TopProductItem[]
}

export interface RevenueOrderItem {
  id: number
  date: string
  serviceAmount: number
  productAmount: number
  totalAmount: number
}

export interface TopServiceItem {
  id: number
  name: string
  orderCount: number
  revenue: number
}

export interface TopProductItem {
  id: number
  name: string
  orderCount: number
  revenue: number
}

export interface RevenueQuery {
  type: 'today' | 'week' | 'month' | 'year' | 'custom'
  startDate?: string
  endDate?: string
}

export const getMerchantRevenueStats = (query: RevenueQuery) => {
  // 根据 query 计算日期范围
  let startDate = query.startDate;
  let endDate = query.endDate;
  
  if (!startDate || !endDate) {
    const now = new Date();
    switch (query.type) {
      case 'today':
        startDate = now.toISOString().split('T')[0];
        endDate = now.toISOString().split('T')[0];
        break;
      case 'week':
        const weekStart = new Date(now);
        weekStart.setDate(now.getDate() - now.getDay());
        startDate = weekStart.toISOString().split('T')[0];
        endDate = now.toISOString().split('T')[0];
        break;
      case 'month':
        const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);
        startDate = monthStart.toISOString().split('T')[0];
        endDate = now.toISOString().split('T')[0];
        break;
      case 'year':
        const yearStart = new Date(now.getFullYear(), 0, 1);
        startDate = yearStart.toISOString().split('T')[0];
        endDate = now.toISOString().split('T')[0];
        break;
    }
  }
  
  return request.get<RevenueStats>('/api/merchant/revenue-stats', { params: { startDate, endDate } })
}

export const exportRevenueStats = (query: RevenueQuery) => {
  // 根据 query 计算日期范围
  let startDate = query.startDate;
  let endDate = query.endDate;
  
  if (!startDate || !endDate) {
    const now = new Date();
    switch (query.type) {
      case 'today':
        startDate = now.toISOString().split('T')[0];
        endDate = now.toISOString().split('T')[0];
        break;
      case 'week':
        const weekStart = new Date(now);
        weekStart.setDate(now.getDate() - now.getDay());
        startDate = weekStart.toISOString().split('T')[0];
        endDate = now.toISOString().split('T')[0];
        break;
      case 'month':
        const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);
        startDate = monthStart.toISOString().split('T')[0];
        endDate = now.toISOString().split('T')[0];
        break;
      case 'year':
        const yearStart = new Date(now.getFullYear(), 0, 1);
        startDate = yearStart.toISOString().split('T')[0];
        endDate = now.toISOString().split('T')[0];
        break;
    }
  }
  
  return request.get<Blob>('/api/merchant/revenue-stats/export', {
    params: { startDate, endDate },
    responseType: 'blob'
  })
}

export interface AppointmentStats {
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

export const getMerchantAppointmentStats = (timeRange: string, startDate?: string, endDate?: string) => {
  // 根据 timeRange 计算日期范围
  let actualStartDate = startDate;
  let actualEndDate = endDate;
  
  if (!startDate || !endDate) {
    const now = new Date();
    switch (timeRange) {
      case 'today':
        actualStartDate = now.toISOString().split('T')[0];
        actualEndDate = now.toISOString().split('T')[0];
        break;
      case 'week':
        const weekStart = new Date(now);
        weekStart.setDate(now.getDate() - now.getDay());
        actualStartDate = weekStart.toISOString().split('T')[0];
        actualEndDate = now.toISOString().split('T')[0];
        break;
      case 'month':
        const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);
        actualStartDate = monthStart.toISOString().split('T')[0];
        actualEndDate = now.toISOString().split('T')[0];
        break;
      case 'year':
        const yearStart = new Date(now.getFullYear(), 0, 1);
        actualStartDate = yearStart.toISOString().split('T')[0];
        actualEndDate = now.toISOString().split('T')[0];
        break;
    }
  }
  
  return request.get<AppointmentStats>('/api/merchant/appointment-stats', { params: { startDate: actualStartDate, endDate: actualEndDate } })
}

export const exportAppointmentStats = (timeRange: string, startDate?: string, endDate?: string) => {
  // 根据 timeRange 计算日期范围
  let actualStartDate = startDate;
  let actualEndDate = endDate;
  
  if (!startDate || !endDate) {
    const now = new Date();
    switch (timeRange) {
      case 'today':
        actualStartDate = now.toISOString().split('T')[0];
        actualEndDate = now.toISOString().split('T')[0];
        break;
      case 'week':
        const weekStart = new Date(now);
        weekStart.setDate(now.getDate() - now.getDay());
        actualStartDate = weekStart.toISOString().split('T')[0];
        actualEndDate = now.toISOString().split('T')[0];
        break;
      case 'month':
        const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);
        actualStartDate = monthStart.toISOString().split('T')[0];
        actualEndDate = now.toISOString().split('T')[0];
        break;
      case 'year':
        const yearStart = new Date(now.getFullYear(), 0, 1);
        actualStartDate = yearStart.toISOString().split('T')[0];
        actualEndDate = now.toISOString().split('T')[0];
        break;
    }
  }
  
  return request.get<Blob>('/api/merchant/appointment-stats/export', {
    params: { startDate: actualStartDate, endDate: actualEndDate },
    responseType: 'blob'
  })
}

export interface MerchantSettings {
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

export const getMerchantSettings = () => {
  return request.get<MerchantSettings>('/api/merchant/settings')
}

export const updateMerchantSettings = (data: MerchantSettings) => {
  return request.put<MerchantSettings>('/api/merchant/settings', data)
}

export interface MerchantInfo {
  id: number
  name: string
  contact_person?: string
  phone?: string
  email?: string
  address?: string
  logo?: string
  description?: string
  status?: 'pending' | 'approved' | 'rejected'
  created_at?: string
}

export interface ChangePasswordData {
  oldPassword: string
  newPassword: string
}

export const changePassword = (data: ChangePasswordData) => {
  return request.post('/api/merchant/change-password', data)
}

export interface BindPhoneData {
  phone: string
  verifyCode: string
}

export const bindPhone = (data: BindPhoneData) => {
  return request.post('/api/merchant/bind-phone', data)
}

export interface BindEmailData {
  email: string
  verifyCode: string
}

export const bindEmail = (data: BindEmailData) => {
  return request.post('/api/merchant/bind-email', data)
}

export const sendVerifyCode = (type: 'phone' | 'email', value: string) => {
  return request.post('/api/merchant/send-verify-code', { type, value })
}

export interface DashboardStats {
  todayOrders: number
  pendingAppointments: number
  todayRevenue: number
  avgRating: number
  orderGrowth: number
  revenueGrowth: number
  ratingCount: number
}

export interface RecentOrder {
  id: number
  customerName: string
  serviceName: string
  status: 'pending' | 'confirmed' | 'completed' | 'cancelled'
  appointmentTime: string
  totalPrice: number
}

export interface RecentReview {
  id: number
  userName: string
  userAvatar?: string
  rating: number
  content: string
  serviceName: string
  reviewTime: string
}

export const getMerchantDashboard = () => {
  return request.get<DashboardStats>('/api/merchant/dashboard')
}

export const getRecentOrders = (limit: number = 5) => {
  return request.get<RecentOrder[]>('/api/merchant/appointments/recent', { params: { limit } })
}

export const getRecentReviews = (limit: number = 5) => {
  return request.get<RecentReview[]>('/api/merchant/reviews/recent', { params: { limit } })
}
