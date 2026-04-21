import request from './request'

export interface Pet {
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

export interface Appointment {
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

export interface Service {
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

export interface MerchantReview {
  id: number
  userId: number
  userName: string
  merchantId: number
  rating: number
  content: string
  createTime: string
}

export interface Favorite {
  id: number
  userId: number
  merchantId: number
  createTime: string
}

export interface Product {
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

export interface MerchantInfo {
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

export const getUserPets = () => {
  return request.get<Pet[]>('/api/user/pets')
}

export const getPetById = (id: number) => {
  return request.get<Pet>(`/api/user/pets/${id}`)
}

export const addPet = (data: Omit<Pet, 'id'>) => {
  return request.post<Pet>('/api/user/pets', data)
}

export const updatePet = (id: number, data: Partial<Pet>) => {
  return request.put<Pet>(`/api/user/pets/${id}`, data)
}

export const deletePet = (id: number) => {
  return request.delete(`/api/user/pets/${id}`)
}

export const getUserAppointments = (params?: {
  status?: string
  keyword?: string
  startDate?: string
  endDate?: string
  page?: number
  pageSize?: number
}) => {
  return request.get<{
    total: number
    data: Appointment[]
    totalPages: number
    pageSize: number
    page: number
  }>('/api/user/appointments', { params })
}

export const cancelAppointment = (id: number) => {
  return request.put(`/api/user/appointments/${id}/cancel`)
}

export const createAppointment = (data: {
  serviceId: number
  petId: number
  appointmentTime: string
  remark?: string
}) => {
  return request.post('/api/user/appointments', data)
}

export const getServices = (params?: { type?: string }) => {
  return request.get<Service[]>('/api/services', { params })
}

export const getServiceById = (id: number) => {
  return request.get<Service>(`/api/services/${id}`)
}

export const getServicesByKeyword = (keyword: string) => {
  return request.get<Service[]>('/api/services/search', { params: { keyword } })
}

export const getMerchantInfo = (id: number) => {
  return request.get<MerchantInfo>(`/api/merchant/${id}`)
}

export const getMerchantServices = (id: number) => {
  return request.get<Service[]>(`/api/merchant/${id}/services`)
}

export const getMerchantReviews = (id: number) => {
  return request.get<MerchantReview[]>(`/api/merchant/${id}/reviews`)
}

export const addFavorite = (merchantId: number) => {
  return request.post<Favorite>('/api/user/favorites', { merchantId })
}

export interface FavoriteMerchant {
  id: number
  merchantId: number
  merchantName: string
  merchantLogo: string
  merchantAddress: string
  merchantPhone: string
  createdAt: string
}

export const getFavorites = () => {
  return request.get<FavoriteMerchant[]>('/api/user/favorites')
}

export const removeFavorite = (merchantId: number) => {
  return request.delete(`/api/user/favorites/${merchantId}`)
}

export const addServiceFavorite = (serviceId: number) => {
  return request.post('/api/user/favorites/services', { serviceId })
}

export interface FavoriteService {
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

export const getServiceFavorites = () => {
  return request.get<FavoriteService[]>('/api/user/favorites/services')
}

export const removeServiceFavorite = (serviceId: number) => {
  return request.delete(`/api/user/favorites/services/${serviceId}`)
}

export const searchProducts = (keyword: string) => {
  return request.get<Product[]>('/api/products/search', { params: { keyword } })
}

export const searchServices = (keyword: string) => {
  return request.get<Service[]>('/api/services/search', { params: { keyword } })
}

export const getProducts = (params?: { name?: string; categoryId?: string; page?: number; pageSize?: number }) => {
  return request.get<Product[]>('/api/products', { params })
}

export const getProductById = (id: number) => {
  return request.get<Product>(`/api/products/${id}`)
}

export const getProductReviews = (productId: number) => {
  return request.get<Review[]>(`/api/products/${productId}/reviews`)
}

export const addProductFavorite = (productId: number) => {
  return request.post(`/api/user/favorites/products`, { productId })
}

export const removeProductFavorite = (productId: number) => {
  return request.delete(`/api/user/favorites/products/${productId}`)
}

export const checkProductFavorite = (productId: number) => {
  return request.get<{ isFavorited: boolean }>(`/api/user/favorites/products/${productId}/check`)
}

export const addToCart = (data: { productId: number; quantity: number }) => {
  return request.post('/api/user/cart', data)
}

export const createOrder = (data: { productId: number; addressId: number; quantity: number }) => {
  return request.post('/api/user/orders', data)
}

export const purchaseProduct = (data: { productId: number; addressId: number; quantity: number }) => {
  return request.post('/api/user/orders', data)
}

export interface Address {
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

export const getAddresses = () => {
  return request.get<Address[]>('/api/user/addresses')
}

export const addAddress = (data: Omit<Address, 'id'>) => {
  return request.post<Address>('/api/user/addresses', data)
}

export const updateAddress = (id: number, data: Partial<Address>) => {
  return request.put<Address>(`/api/user/addresses/${id}`, data)
}

export const deleteAddress = (id: number) => {
  return request.delete(`/api/user/addresses/${id}`)
}

export const setDefaultAddress = (id: number) => {
  return request.put(`/api/user/addresses/${id}/default`)
}

export interface Review {
  id?: number
  appointmentId: number
  merchantId: number
  serviceId: number
  rating: number
  comment: string
}

export const addReview = (data: Review) => {
  return request.post('/api/user/reviews', data)
}

export const getUserReviews = (params?: { type?: string; startDate?: string; endDate?: string }) => {
  return request.get<Review[]>('/api/user/reviews', { params })
}

export const updateReview = (id: number, data: { rating: number; comment: string }) => {
  return request.put(`/api/user/reviews/${id}`, data)
}

export const deleteReview = (id: number) => {
  return request.delete(`/api/user/reviews/${id}`)
}

export interface CartItem {
  id: number
  productId: number
  productName: string
  productImage?: string
  price: number
  quantity: number
  merchantId: number
  merchantName: string
}

export const getCart = () => {
  return request.get<CartItem[]>('/api/user/cart')
}

export const updateCartItem = (cartId: number, quantity: number) => {
  return request.put('/api/user/cart', { cartId, quantity })
}

export const removeFromCart = (id: number) => {
  return request.delete(`/api/user/cart/${id}`)
}

export interface ProductOrder {
  id: number
  productName: string
  merchantName: string
  quantity: number
  totalPrice: number
  status: string
  orderTime: string
}

export const getProductOrders = (params?: {
  status?: string
  keyword?: string
  startDate?: string
  endDate?: string
  page?: number
  pageSize?: number
}) => {
  return request.get<{
    total: number
    data: ProductOrder[]
    totalPages: number
    pageSize: number
    page: number
  }>('/api/user/orders', { params })
}

export const payOrder = (orderId: number, payMethod: string) => {
  return request.post('/api/user/orders/' + orderId + '/pay', { payMethod })
}

export interface OrderDetail {
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
  items: OrderItem[]
  address: OrderAddress
  timeline?: OrderTimelineItem[]
  logisticsCompany?: string
  trackingNumber?: string
}

export interface OrderItem {
  id: number
  productId: number
  productName: string
  productImage?: string
  price: number
  quantity: number
  subtotal: number
}

export interface OrderAddress {
  name: string
  phone: string
  address: string
}

export interface OrderTimelineItem {
  status: string
  time: string
  description: string
}

export const getOrderById = (id: number) => {
  return request.get<OrderDetail>(`/api/user/orders/${id}`)
}

export const cancelOrder = (id: number) => {
  return request.put(`/api/user/orders/${id}/cancel`)
}

export const refundOrder = (id: number, reason: string) => {
  return request.post(`/api/user/orders/${id}/refund`, { reason })
}

export const confirmReceiveOrder = (id: number) => {
  return request.put(`/api/user/orders/${id}/confirm`)
}

export const deleteOrder = (id: number) => {
  return request.delete(`/api/user/orders/${id}`)
}

export const batchCancelOrders = (ids: number[]) => {
  return request.put('/api/user/orders/batch-cancel', { ids })
}

export const batchDeleteOrders = (ids: number[]) => {
  return request.delete('/api/user/orders/batch-delete', { data: { ids } })
}

export interface MerchantListItem {
  id: number
  name: string
  logo?: string
  address?: string
  phone?: string
  rating?: number
  serviceCount: number
  isFavorited?: boolean
}

export const getMerchantList = (params?: {
  name?: string
  minRating?: number
  page?: number
  pageSize?: number
}) => {
  return request.get<{ data: MerchantListItem[]; total: number }>('/api/merchants', { params })
}

export interface UserReview {
  id: number
  appointmentId: number
  serviceName: string
  merchantName: string
  merchantId: number
  serviceId: number
  rating: number
  comment: string
  createdAt: string
}

export const getUserReviewsList = (params?: {
  rating?: number
  startDate?: string
  endDate?: string
  page?: number
  size?: number
}) => {
  return request.get<{ data: UserReview[]; total: number }>('/api/user/reviews', { params })
}

export interface HomeStats {
  petCount: number
  pendingAppointments: number
  reviewCount: number
}

export const getHomeStats = () => {
  return request.get<HomeStats>('/api/user/home/stats')
}

export interface Activity {
  id: number
  type: 'appointment' | 'review' | 'order'
  title: string
  time: string
  status: string
  statusColor: string
  relatedId?: number
}

export const getRecentActivities = (limit?: number) => {
  return request.get<Activity[]>('/api/user/home/activities', { params: { limit } })
}

export const getRecommendedServices = (limit?: number) => {
  return request.get<Service[]>('/api/services/recommended', { params: { limit } })
}

export interface AppointmentDetail {
  id: number
  serviceId: number
  serviceName: string
  servicePrice: number
  serviceDuration: number
  merchantId: number
  merchantName: string
  merchantPhone: string
  merchantAddress: string
  petId: number
  petName: string
  petType: string
  appointmentTime: string
  status: string
  totalPrice: number
  remark?: string
  createdAt: string
  updatedAt: string
}

export const getAppointmentById = (id: number) => {
  return request.get<AppointmentDetail>(`/api/user/appointments/${id}`)
}

export interface AppointmentStats {
  total: number
  pending: number
  confirmed: number
  completed: number
  cancelled: number
}

export const getAppointmentStats = () => {
  return request.get<AppointmentStats>('/api/user/appointments/stats')
}

export interface UserPurchasedService {
  id: number
  name: string
  merchant: string
  merchantId: number
  price: number
  purchaseDate: string
  expiryDate: string
  status: 'active' | 'used' | 'expired'
  category: string
  serviceId: number
}

export const getUserPurchasedServices = (params?: {
  keyword?: string
  status?: string
  page?: number
  pageSize?: number
}) => {
  return request.get<{ data: UserPurchasedService[]; total: number }>('/api/user/services', { params })
}

export const batchRemoveFromCart = (ids: number[]) => {
  return request.delete('/api/user/cart/batch', { data: { ids } })
}

export interface OrderPreview {
  items: {
    productId: number
    productName: string
    productImage: string
    price: number
    quantity: number
    subtotal: number
  }[]
  productTotal: number
  shippingFee: number
  discount: number
  totalAmount: number
}

export const previewOrder = (items: { productId: number; quantity: number }[]) => {
  return request.post<OrderPreview>('/api/user/orders/preview', { items })
}

export interface PayStatusResponse {
  orderId: number
  payStatus: 'pending' | 'paying' | 'success' | 'failed'
  payTime?: string
  transactionId?: string
}

export const getPayStatus = (orderId: number) => {
  return request.get<PayStatusResponse>(`/api/user/orders/${orderId}/pay/status`)
}

export const getMerchantProducts = (id: number) => {
  return request.get<Product[]>(`/api/merchant/${id}/products`)
}

export interface AvailableSlot {
  time: string
  available: boolean
}

export interface AvailableSlotsResponse {
  date: string
  slots: AvailableSlot[]
  workingHours: {
    start: string
    end: string
  }
}

export const getAvailableSlots = (merchantId: number, date: string) => {
  return request.get<AvailableSlotsResponse>(`/api/merchant/${merchantId}/available-slots`, { params: { date } })
}

export const searchMerchants = (keyword: string) => {
  return request.get<MerchantInfo[]>('/api/merchants/search', { params: { keyword } })
}
