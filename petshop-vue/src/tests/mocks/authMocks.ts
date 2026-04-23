import { vi } from 'vitest'

export interface MockUserInfo {
  id: number
  username: string
  email?: string
  phone?: string
  avatar?: string
  role: string
}

export interface MockMerchantInfo {
  id: number
  name: string
  contactPerson?: string
  phone?: string
  email?: string
  address?: string
  logo?: string
  description?: string
  status?: 'pending' | 'approved' | 'rejected'
  createdAt?: string
}

export interface MockAdminInfo {
  id: number
  username: string
  email?: string
  phone?: string
  avatar?: string
  role: string
}

export const defaultUserInfo: MockUserInfo = {
  id: 1,
  username: 'testuser',
  email: 'test@example.com',
  phone: '13800138000',
  avatar: 'https://picsum.photos/100/100?random=1',
  role: 'user',
}

export const defaultMerchantInfo: MockMerchantInfo = {
  id: 1,
  name: '测试商家',
  contactPerson: '张先生',
  phone: '13900139000',
  email: 'merchant@example.com',
  address: '北京市朝阳区测试路1号',
  logo: 'https://picsum.photos/100/100?random=2',
  description: '专业宠物服务商家',
  status: 'approved',
  createdAt: '2024-01-01T00:00:00.000Z',
}

export const defaultAdminInfo: MockAdminInfo = {
  id: 1,
  username: 'admin',
  email: 'admin@example.com',
  phone: '13700137000',
  avatar: 'https://picsum.photos/100/100?random=3',
  role: 'admin',
}

export const createUserInfo = (overrides: Partial<MockUserInfo> = {}): MockUserInfo => ({
  ...defaultUserInfo,
  ...overrides,
})

export const createMerchantInfo = (overrides: Partial<MockMerchantInfo> = {}): MockMerchantInfo => ({
  ...defaultMerchantInfo,
  ...overrides,
})

export const createAdminInfo = (overrides: Partial<MockAdminInfo> = {}): MockAdminInfo => ({
  ...defaultAdminInfo,
  ...overrides,
})

export const mockAuthApi = {
  login: vi.fn((loginIdentifier: string, password: string) => {
    if (loginIdentifier === 'user@test.com' && password === 'password123') {
      return Promise.resolve({
        code: 200,
        message: '登录成功',
        data: { token: 'mock-user-token-12345' },
      })
    }
    if (loginIdentifier === 'merchant@test.com' && password === 'password123') {
      return Promise.resolve({
        code: 200,
        message: '登录成功',
        data: { token: 'mock-merchant-token-12345' },
      })
    }
    if (loginIdentifier === 'admin@test.com' && password === 'password123') {
      return Promise.resolve({
        code: 200,
        message: '登录成功',
        data: { token: 'mock-admin-token-12345' },
      })
    }
    return Promise.resolve({
      code: 401,
      message: '用户名或密码错误',
      data: null,
    })
  }),

  register: vi.fn((data: { username: string; password: string; email?: string; phone: string }) => {
    if (data.phone === '13800138000') {
      return Promise.resolve({
        code: 400,
        message: '该手机号已注册',
        data: null,
      })
    }
    return Promise.resolve({
      code: 200,
      message: '注册成功',
      data: { token: 'mock-new-user-token-12345' },
    })
  }),

  merchantRegister: vi.fn((data: { name: string; phone: string; password: string }) => {
    return Promise.resolve({
      code: 200,
      message: '注册申请已提交，请等待审核',
      data: { message: '注册申请已提交，请等待审核' },
    })
  }),

  adminRegister: vi.fn((data: { username: string; phone: string; password: string }) => {
    return Promise.resolve({
      code: 200,
      message: '管理员注册成功',
      data: { message: '管理员注册成功' },
    })
  }),

  logout: vi.fn(() => {
    return Promise.resolve({
      code: 200,
      message: '退出成功',
      data: null,
    })
  }),

  getUserInfo: vi.fn(() => {
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: createUserInfo(),
    })
  }),

  updateUserInfo: vi.fn((data: { username?: string; email?: string; phone?: string; avatar?: string }) => {
    return Promise.resolve({
      code: 200,
      message: '更新成功',
      data: createUserInfo(data),
    })
  }),

  sendVerifyCode: vi.fn((email: string) => {
    return Promise.resolve({
      code: 200,
      message: '验证码已发送',
      data: null,
    })
  }),

  resetPassword: vi.fn((data: { email: string; verifyCode: string; password: string }) => {
    if (data.verifyCode === '123456') {
      return Promise.resolve({
        code: 200,
        message: '密码重置成功',
        data: null,
      })
    }
    return Promise.resolve({
      code: 400,
      message: '验证码错误',
      data: null,
    })
  }),

  changePassword: vi.fn((data: { oldPassword: string; newPassword: string }) => {
    if (data.oldPassword === 'wrongpassword') {
      return Promise.resolve({
        code: 400,
        message: '原密码错误',
        data: { success: false, message: '原密码错误' },
      })
    }
    return Promise.resolve({
      code: 200,
      message: '密码修改成功',
      data: { success: true, message: '密码修改成功' },
    })
  }),
}

export const mockAuthResponses = {
  loginSuccess: {
    code: 200,
    message: '登录成功',
    data: { token: 'mock-token-12345' },
  },
  loginFailed: {
    code: 401,
    message: '用户名或密码错误',
    data: null,
  },
  registerSuccess: {
    code: 200,
    message: '注册成功',
    data: { token: 'mock-token-12345' },
  },
  registerFailed: {
    code: 400,
    message: '该手机号已注册',
    data: null,
  },
  logoutSuccess: {
    code: 200,
    message: '退出成功',
    data: null,
  },
  userInfoSuccess: {
    code: 200,
    message: 'success',
    data: defaultUserInfo,
  },
  merchantInfoSuccess: {
    code: 200,
    message: 'success',
    data: defaultMerchantInfo,
  },
  adminInfoSuccess: {
    code: 200,
    message: 'success',
    data: defaultAdminInfo,
  },
}
