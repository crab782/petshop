import request from './request'

export interface LoginData {
  username: string
  password: string
}

export interface RegisterData {
  username: string
  password: string
  email?: string
  phone?: string
}

export interface MerchantRegisterData {
  username: string
  password: string
  email: string
  phone: string
  contact_person: string
  name: string
  logo?: string
  address: string
}

export interface UserInfo {
  id: number
  username: string
  email?: string
  phone?: string
  avatar?: string
  role: string
}

export const login = (data: LoginData) => {
  return request.post<{ token: string }>('/api/auth/login', data)
}

export const register = (data: RegisterData) => {
  return request.post<{ token: string }>('/api/auth/register', data)
}

export const merchantRegister = (data: MerchantRegisterData) => {
  return request.post<{ message: string }>('/api/auth/merchant/register', data)
}

export const logout = () => {
  return request.post('/api/auth/logout')
}

export const getUserInfo = () => {
  return request.get<UserInfo>('/api/auth/userinfo')
}

export interface UpdateUserInfoData {
  username?: string
  email?: string
  phone?: string
  avatar?: string
  password?: string
}

export const updateUserInfo = (data: UpdateUserInfoData) => {
  return request.put('/api/auth/userinfo', data)
}

export interface SendVerifyCodeData {
  email: string
}

export const sendVerifyCode = (email: string) => {
  return request.post('/api/auth/sendVerifyCode', { email })
}

export interface ResetPasswordData {
  email: string
  verifyCode: string
  password: string
}

export const resetPassword = (data: ResetPasswordData) => {
  return request.post('/api/auth/resetPassword', data)
}

export interface ChangePasswordData {
  oldPassword: string
  newPassword: string
}

export const changePassword = (data: ChangePasswordData) => {
  return request.put<{ success: boolean; message: string }>('/api/auth/password', data)
}
