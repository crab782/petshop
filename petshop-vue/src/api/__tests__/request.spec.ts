import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import axios from 'axios'
import type { AxiosInstance } from 'axios'

vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => ({
      interceptors: {
        request: {
          use: vi.fn(),
        },
        response: {
          use: vi.fn(),
        },
      },
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn(),
    })),
  },
}))

vi.mock('element-plus', () => ({
  ElMessage: {
    error: vi.fn(),
  },
}))

describe('request', () => {
  let mockAxiosInstance: any

  beforeEach(async () => {
    vi.clearAllMocks()
    mockAxiosInstance = {
      interceptors: {
        request: {
          use: vi.fn(),
        },
        response: {
          use: vi.fn(),
        },
      },
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn(),
    }
    ;(axios.create as any).mockReturnValue(mockAxiosInstance)
  })

  afterEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
    sessionStorage.clear()
  })

  it('should create axios instance with correct config', async () => {
    await import('../request')

    expect(axios.create).toHaveBeenCalledWith(
      expect.objectContaining({
        timeout: 10000,
      })
    )
  })

  it('should setup request interceptor', async () => {
    await import('../request')

    expect(mockAxiosInstance.interceptors.request.use).toHaveBeenCalled()
  })

  it('should setup response interceptor', async () => {
    await import('../request')

    expect(mockAxiosInstance.interceptors.response.use).toHaveBeenCalled()
  })

  it('should add merchant token for merchant paths', async () => {
    const originalPath = window.location.pathname
    Object.defineProperty(window, 'location', {
      value: { pathname: '/merchant/dashboard' },
      writable: true,
    })

    localStorage.setItem('merchant_token', 'test-merchant-token')

    await import('../request')

    const requestInterceptor = mockAxiosInstance.interceptors.request.use.mock.calls[0][0]
    const config = { headers: {} }
    const result = requestInterceptor(config)

    expect(result.headers.Authorization).toBe('Bearer test-merchant-token')

    Object.defineProperty(window, 'location', {
      value: { pathname: originalPath },
      writable: true,
    })
  })

  it('should add admin token for admin paths', async () => {
    const originalPath = window.location.pathname
    Object.defineProperty(window, 'location', {
      value: { pathname: '/admin/dashboard' },
      writable: true,
    })

    localStorage.setItem('admin_token', 'test-admin-token')

    await import('../request')

    const requestInterceptor = mockAxiosInstance.interceptors.request.use.mock.calls[0][0]
    const config = { headers: {} }
    const result = requestInterceptor(config)

    expect(result.headers.Authorization).toBe('Bearer test-admin-token')

    Object.defineProperty(window, 'location', {
      value: { pathname: originalPath },
      writable: true,
    })
  })

  it('should add user token for user paths', async () => {
    const originalPath = window.location.pathname
    Object.defineProperty(window, 'location', {
      value: { pathname: '/user/dashboard' },
      writable: true,
    })

    localStorage.setItem('user_token', 'test-user-token')

    await import('../request')

    const requestInterceptor = mockAxiosInstance.interceptors.request.use.mock.calls[0][0]
    const config = { headers: {} }
    const result = requestInterceptor(config)

    expect(result.headers.Authorization).toBe('Bearer test-user-token')

    Object.defineProperty(window, 'location', {
      value: { pathname: originalPath },
      writable: true,
    })
  })

  it('should not add token if not present', async () => {
    const originalPath = window.location.pathname
    Object.defineProperty(window, 'location', {
      value: { pathname: '/user/dashboard' },
      writable: true,
    })

    await import('../request')

    const requestInterceptor = mockAxiosInstance.interceptors.request.use.mock.calls[0][0]
    const config = { headers: {} }
    const result = requestInterceptor(config)

    expect(result.headers.Authorization).toBeUndefined()

    Object.defineProperty(window, 'location', {
      value: { pathname: originalPath },
      writable: true,
    })
  })

  it('should handle response with code 200', async () => {
    await import('../request')

    const responseInterceptor = mockAxiosInstance.interceptors.response.use.mock.calls[0][0]
    const response = {
      data: {
        code: 200,
        data: { id: 1, name: 'test' },
      },
    }
    const result = responseInterceptor(response)

    expect(result).toEqual({ id: 1, name: 'test' })
  })

  it('should handle response with code 0', async () => {
    await import('../request')

    const responseInterceptor = mockAxiosInstance.interceptors.response.use.mock.calls[0][0]
    const response = {
      data: {
        code: 0,
        data: { id: 1, name: 'test' },
      },
    }
    const result = responseInterceptor(response)

    expect(result).toEqual({ id: 1, name: 'test' })
  })

  it('should handle response with error code', async () => {
    const { ElMessage } = await import('element-plus')
    await import('../request')

    const responseInterceptor = mockAxiosInstance.interceptors.response.use.mock.calls[0][0]
    const response = {
      data: {
        code: 400,
        message: 'Bad Request',
      },
    }

    expect(() => responseInterceptor(response)).toThrow('Bad Request')
    expect(ElMessage.error).toHaveBeenCalledWith('Bad Request')
  })

  it('should handle response without code', async () => {
    await import('../request')

    const responseInterceptor = mockAxiosInstance.interceptors.response.use.mock.calls[0][0]
    const response = {
      data: 'plain text',
    }
    const result = responseInterceptor(response)

    expect(result).toBe('plain text')
  })
})
