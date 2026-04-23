import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from '../request'
import {
  login,
  register,
  merchantRegister,
  adminRegister,
  logout,
  getUserInfo,
  updateUserInfo,
  sendVerifyCode,
  resetPassword,
  changePassword,
} from '../auth'

vi.mock('../request', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
    put: vi.fn(),
  },
}))

describe('auth API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('login', () => {
    it('should call POST /api/auth/login with correct data', async () => {
      const mockResponse = { token: 'test-token' }
      ;(request.post as any).mockResolvedValue(mockResponse)

      const result = await login({
        loginIdentifier: 'test@example.com',
        password: 'password123',
      })

      expect(request.post).toHaveBeenCalledWith('/api/auth/login', {
        loginIdentifier: 'test@example.com',
        password: 'password123',
      })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('register', () => {
    it('should call POST /api/auth/register with correct data', async () => {
      const mockResponse = { token: 'test-token' }
      ;(request.post as any).mockResolvedValue(mockResponse)

      const result = await register({
        username: 'testuser',
        password: 'password123',
        email: 'test@example.com',
        phone: '13800138000',
      })

      expect(request.post).toHaveBeenCalledWith('/api/auth/register', {
        username: 'testuser',
        password: 'password123',
        email: 'test@example.com',
        phone: '13800138000',
      })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('merchantRegister', () => {
    it('should call POST /api/auth/merchant/register with correct data', async () => {
      const mockResponse = { message: 'Registration successful' }
      ;(request.post as any).mockResolvedValue(mockResponse)

      const result = await merchantRegister({
        password: 'password123',
        email: 'merchant@example.com',
        phone: '13800138000',
        contactPerson: 'John Doe',
        name: 'Test Shop',
        logo: 'https://example.com/logo.png',
        address: '123 Test Street',
      })

      expect(request.post).toHaveBeenCalledWith('/api/auth/merchant/register', {
        password: 'password123',
        email: 'merchant@example.com',
        phone: '13800138000',
        contactPerson: 'John Doe',
        name: 'Test Shop',
        logo: 'https://example.com/logo.png',
        address: '123 Test Street',
      })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('adminRegister', () => {
    it('should call POST /api/auth/admin/register with correct data', async () => {
      const mockResponse = { message: 'Registration successful' }
      ;(request.post as any).mockResolvedValue(mockResponse)

      const result = await adminRegister({
        password: 'password123',
        email: 'admin@example.com',
        phone: '13800138000',
        username: 'adminuser',
      })

      expect(request.post).toHaveBeenCalledWith('/api/auth/admin/register', {
        password: 'password123',
        email: 'admin@example.com',
        phone: '13800138000',
        username: 'adminuser',
      })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('logout', () => {
    it('should call POST /api/auth/logout', async () => {
      ;(request.post as any).mockResolvedValue(undefined)

      await logout()

      expect(request.post).toHaveBeenCalledWith('/api/auth/logout')
    })
  })

  describe('getUserInfo', () => {
    it('should call GET /api/auth/userinfo', async () => {
      const mockResponse = {
        id: 1,
        username: 'testuser',
        email: 'test@example.com',
        phone: '13800138000',
        avatar: 'https://example.com/avatar.png',
        role: 'user',
      }
      ;(request.get as any).mockResolvedValue(mockResponse)

      const result = await getUserInfo()

      expect(request.get).toHaveBeenCalledWith('/api/auth/userinfo')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('updateUserInfo', () => {
    it('should call PUT /api/auth/userinfo with correct data', async () => {
      const mockResponse = {
        id: 1,
        username: 'newusername',
        email: 'new@example.com',
        phone: '13900139000',
        avatar: 'https://example.com/new-avatar.png',
        role: 'user',
      }
      ;(request.put as any).mockResolvedValue(mockResponse)

      const result = await updateUserInfo({
        username: 'newusername',
        email: 'new@example.com',
        phone: '13900139000',
        avatar: 'https://example.com/new-avatar.png',
      })

      expect(request.put).toHaveBeenCalledWith('/api/auth/userinfo', {
        username: 'newusername',
        email: 'new@example.com',
        phone: '13900139000',
        avatar: 'https://example.com/new-avatar.png',
      })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('sendVerifyCode', () => {
    it('should call POST /api/auth/sendVerifyCode with email', async () => {
      ;(request.post as any).mockResolvedValue(undefined)

      await sendVerifyCode('test@example.com')

      expect(request.post).toHaveBeenCalledWith('/api/auth/sendVerifyCode', {
        email: 'test@example.com',
      })
    })
  })

  describe('resetPassword', () => {
    it('should call POST /api/auth/resetPassword with correct data', async () => {
      ;(request.post as any).mockResolvedValue(undefined)

      await resetPassword({
        email: 'test@example.com',
        verifyCode: '123456',
        password: 'newpassword123',
      })

      expect(request.post).toHaveBeenCalledWith('/api/auth/resetPassword', {
        email: 'test@example.com',
        verifyCode: '123456',
        password: 'newpassword123',
      })
    })
  })

  describe('changePassword', () => {
    it('should call PUT /api/auth/password with correct data', async () => {
      const mockResponse = { success: true, message: 'Password changed successfully' }
      ;(request.put as any).mockResolvedValue(mockResponse)

      const result = await changePassword({
        oldPassword: 'oldpassword123',
        newPassword: 'newpassword123',
      })

      expect(request.put).toHaveBeenCalledWith('/api/auth/password', {
        oldPassword: 'oldpassword123',
        newPassword: 'newpassword123',
      })
      expect(result).toEqual(mockResponse)
    })
  })
})
