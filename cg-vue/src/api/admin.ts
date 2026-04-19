import request from './request'

export interface DashboardData {
  userCount: number
  merchantCount: number
  orderCount: number
  serviceCount: number
}

export interface User {
  id: number
  username: string
  email?: string
  phone?: string
  role: string
  status: string
  createTime: string
}

export interface Merchant {
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

export interface AdminService {
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

export interface Review {
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

export interface Product {
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

export interface RecentOrder {
  id: number
  orderNo: string
  serviceName: string
  totalPrice: number
  status: string
  createTime: string
}

export interface RecentAppointment {
  id: number
  serviceName: string
  merchantName: string
  appointmentTime: string
  status: string
}

export interface UserReview {
  id: number
  serviceName: string
  merchantName: string
  rating: number
  comment: string
  createdAt: string
}

export interface UserDetail extends User {
  recentOrders: RecentOrder[]
  recentAppointments: RecentAppointment[]
  reviews: UserReview[]
}

export const getAdminDashboard = () => {
  return request.get<DashboardData>('/api/admin/dashboard')
}

export const getAllUsers = () => {
  return request.get<User[]>('/api/admin/users')
}

export const updateUserStatus = (id: number, status: string) => {
  return request.put<User>(`/api/admin/users/${id}/status`, { status })
}

export const deleteUser = (id: number) => {
  return request.delete(`/api/admin/users/${id}`)
}

export const batchUpdateUserStatus = (ids: number[], status: string) => {
  return request.put('/api/admin/users/batch/status', { ids, status })
}

export const batchDeleteUsers = (ids: number[]) => {
  return request.delete('/api/admin/users/batch', { data: { ids } })
}

export const getUserDetailById = (id: number) => {
  return request.get<UserDetail>(`/api/admin/users/${id}`)
}

export const getAllMerchants = () => {
  return request.get<Merchant[]>('/api/admin/merchants')
}

export const updateMerchantStatus = (id: number, status: string) => {
  return request.put<Merchant>(`/api/admin/merchants/${id}/status`, { status })
}

export const getMerchantDetailById = (id: number) => {
  return request.get<MerchantDetail>(`/api/admin/merchants/${id}`)
}

export interface PaginationParams {
  page?: number
  pageSize?: number
  keyword?: string
}

export const getPendingMerchants = (params?: PaginationParams) => {
  return request.get<Merchant[]>('/api/admin/merchants/pending', { params })
}

export const auditMerchant = (id: number, status: string, reason?: string) => {
  return request.put<Merchant>(`/api/admin/merchants/${id}/audit`, { status, reason })
}

export const deleteMerchant = (id: number) => {
  return request.delete(`/api/admin/merchants/${id}`)
}

export const getAllServices = () => {
  return request.get<AdminService[]>('/api/admin/services')
}

export const updateServiceStatus = (id: number, status: string) => {
  return request.put<AdminService>(`/api/admin/services/${id}/status`, { status })
}

export const batchUpdateServiceStatus = (ids: number[], status: string) => {
  return request.put('/api/admin/services/batch/status', { ids, status })
}

export const batchDeleteServices = (ids: number[]) => {
  return request.delete('/api/admin/services/batch', { data: { ids } })
}

export interface SystemConfig {
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

export const getSystemConfig = () => {
  return request.get<SystemConfig>('/api/admin/system/config')
}

export const updateSystemConfig = (data: SystemConfig) => {
  return request.put<SystemConfig>('/api/admin/system/config', data)
}

export const deleteService = (id: number) => {
  return request.delete(`/api/admin/services/${id}`)
}

export const batchUpdateProductStatus = (ids: number[], status: string) => {
  return request.put('/api/admin/products/batch/status', { ids, status })
}

export const batchDeleteProducts = (ids: number[]) => {
  return request.delete('/api/admin/products/batch', { data: { ids } })
}

export const getAllReviews = () => {
  return request.get<Review[]>('/api/admin/reviews')
}

export const deleteReview = (id: number) => {
  return request.delete(`/api/admin/reviews/${id}`)
}

export const getAllProducts = () => {
  return request.get<Product[]>('/api/admin/products')
}

export const getAllProductsForAdmin = () => {
  return request.get<Product[]>('/api/admin/products')
}

export const updateProductStatus = (id: number, status: string) => {
  return request.put<Product>(`/api/admin/products/${id}/status`, { status })
}

export const deleteProduct = (id: number) => {
  return request.delete(`/api/admin/products/${id}`)
}

export interface Shop {
  id: number
  name: string
  applicant?: string
  applicantPhone?: string
  address?: string
  description?: string
  createTime: string
  licenseImage?: string
}

export const getPendingShops = (params?: PaginationParams) => {
  return request.get<Shop[]>('/api/admin/shops/pending', { params })
}

export const auditShop = (id: number, status: string, reason?: string) => {
  return request.put<Shop>(`/api/admin/shops/${id}/audit`, { status, reason })
}

export interface OperationLog {
  id: number
  operator: string
  operationType: string
  description: string
  ipAddress: string
  operationTime: string
}

export const getOperationLogs = (params?: any) => {
  return request.get<OperationLog[]>('/api/admin/operation-logs', { params })
}

export const deleteLog = (id: number) => {
  return request.delete(`/api/admin/operation-logs/${id}`)
}

export const clearLogs = () => {
  return request.delete('/api/admin/operation-logs')
}

export interface Role {
  id: number
  name: string
  description?: string
  permissions?: number[]
  permissionCount?: number
}

export const getRoles = () => {
  return request.get<Role[]>('/api/admin/roles')
}

export const addRole = (data: Partial<Role>) => {
  return request.post<Role>('/api/admin/roles', data)
}

export const updateRole = (id: number, data: Partial<Role>) => {
  return request.put<Role>(`/api/admin/roles/${id}`, data)
}

export const deleteRole = (id: number) => {
  return request.delete(`/api/admin/roles/${id}`)
}

export interface Permission {
  id: number
  name: string
  code?: string
  children?: Permission[]
}

export const getPermissions = () => {
  return request.get<Permission[]>('/api/admin/permissions')
}

export interface Activity {
  id: number
  name: string
  type: string
  description?: string
  startTime: string
  endTime: string
  status: 'pending' | 'active' | 'ended' | 'disabled'
  createTime?: string
}

export const getActivities = (params?: any) => {
  return request.get<Activity[]>('/api/admin/activities', { params })
}

export const addActivity = (data: Partial<Activity>) => {
  return request.post<Activity>('/api/admin/activities', data)
}

export const updateActivity = (id: number, data: Partial<Activity>) => {
  return request.put<Activity>(`/api/admin/activities/${id}`, data)
}

export const deleteActivity = (id: number) => {
  return request.delete(`/api/admin/activities/${id}`)
}

export const toggleActivityStatus = (id: number, status: string) => {
  return request.put<Activity>(`/api/admin/activities/${id}/status`, { status })
}

export interface Task {
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

export const getTasks = (params?: any) => {
  return request.get<Task[]>('/api/admin/tasks', { params })
}

export const addTask = (data: Partial<Task>) => {
  return request.post<Task>('/api/admin/tasks', data)
}

export const updateTask = (id: number, data: Partial<Task>) => {
  return request.put<Task>(`/api/admin/tasks/${id}`, data)
}

export const deleteTask = (id: number) => {
  return request.delete(`/api/admin/tasks/${id}`)
}

export const executeTask = (id: number) => {
  return request.post<Task>(`/api/admin/tasks/${id}/execute`)
}

export const getReviewsForAudit = () => {
  return request.get<Review[]>('/api/admin/reviews/audit')
}

export const approveReview = (id: number) => {
  return request.put<Review>(`/api/admin/reviews/${id}/approve`)
}

export const markReviewViolation = (id: number) => {
  return request.put<Review>(`/api/admin/reviews/${id}/violation`)
}
