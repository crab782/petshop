import type {
  ErrorScenario,
  ErrorResponse,
  ErrorConfig,
  ErrorCode,
  ResourceType
} from './types'
import { ERROR_SCENARIOS } from './types'

export class ErrorGenerator {
  private static instance: ErrorGenerator
  private activeErrors: Map<string, ErrorScenario> = new Map()
  private globalErrorEnabled: boolean = false
  private globalErrorScenario: ErrorScenario | null = null

  static getInstance(): ErrorGenerator {
    if (!ErrorGenerator.instance) {
      ErrorGenerator.instance = new ErrorGenerator()
    }
    return ErrorGenerator.instance
  }

  enableGlobalError(scenario: ErrorScenario): void {
    this.globalErrorEnabled = true
    this.globalErrorScenario = scenario
  }

  disableGlobalError(): void {
    this.globalErrorEnabled = false
    this.globalErrorScenario = null
  }

  setError(resource: ResourceType, action: string, scenario: ErrorScenario): void {
    const key = `${resource}:${action}`
    this.activeErrors.set(key, scenario)
  }

  clearError(resource: ResourceType, action: string): void {
    const key = `${resource}:${action}`
    this.activeErrors.delete(key)
  }

  clearAllErrors(): void {
    this.activeErrors.clear()
    this.globalErrorEnabled = false
    this.globalErrorScenario = null
  }

  shouldReturnError(resource: ResourceType, action: string): boolean {
    if (this.globalErrorEnabled && this.globalErrorScenario) {
      return true
    }
    const key = `${resource}:${action}`
    return this.activeErrors.has(key)
  }

  getErrorScenario(resource: ResourceType, action: string): ErrorScenario | null {
    if (this.globalErrorEnabled && this.globalErrorScenario) {
      return this.globalErrorScenario
    }
    const key = `${resource}:${action}`
    return this.activeErrors.get(key) || null
  }

  generateError(
    scenario: ErrorScenario,
    path: string,
    customMessage?: string
  ): ErrorResponse {
    const config = ERROR_SCENARIOS[scenario]
    return {
      code: config.code,
      message: customMessage || config.message,
      data: null,
      errors: config.errors,
      timestamp: new Date().toISOString(),
      path
    }
  }

  generateCustomError(
    code: ErrorCode,
    message: string,
    path: string,
    errors?: Record<string, string[]>
  ): ErrorResponse {
    return {
      code,
      message,
      data: null,
      errors,
      timestamp: new Date().toISOString(),
      path
    }
  }

  generateValidationError(
    path: string,
    errors: Record<string, string[]>
  ): ErrorResponse {
    return {
      code: 400,
      message: '数据验证失败',
      data: null,
      errors,
      timestamp: new Date().toISOString(),
      path
    }
  }

  generateAuthError(path: string, isTokenExpired: boolean = false): ErrorResponse {
    return {
      code: 401,
      message: isTokenExpired ? '登录已过期，请重新登录' : '未授权访问',
      data: null,
      timestamp: new Date().toISOString(),
      path
    }
  }

  generatePermissionError(path: string, resource?: string): ErrorResponse {
    return {
      code: 403,
      message: resource
        ? `您没有权限访问${resource}`
        : '您没有权限执行此操作',
      data: null,
      timestamp: new Date().toISOString(),
      path
    }
  }

  generateNotFoundError(path: string, resource?: string): ErrorResponse {
    return {
      code: 404,
      message: resource
        ? `${resource}不存在或已被删除`
        : '请求的资源不存在',
      data: null,
      timestamp: new Date().toISOString(),
      path
    }
  }

  generateServerError(
    path: string,
    details?: string,
    includeStack: boolean = false
  ): ErrorResponse {
    const response: ErrorResponse = {
      code: 500,
      message: '服务器内部错误',
      data: null,
      timestamp: new Date().toISOString(),
      path
    }

    if (details) {
      response.errors = {
        details: [details]
      }
    }

    if (includeStack && import.meta.env.DEV) {
      response.errors = {
        ...response.errors,
        stack: ['Error at line 1: ...']
      }
    }

    return response
  }

  generateRateLimitError(path: string, retryAfter: number = 60): ErrorResponse {
    return {
      code: 429,
      message: '请求过于频繁，请稍后重试',
      data: null,
      errors: {
        retryAfter: [`${retryAfter}秒后可重试`]
      },
      timestamp: new Date().toISOString(),
      path
    }
  }

  generateMaintenanceError(path: string, estimatedTime?: string): ErrorResponse {
    return {
      code: 503,
      message: '系统维护中',
      data: null,
      errors: estimatedTime ? {
        estimatedTime: [`预计恢复时间: ${estimatedTime}`]
      } : undefined,
      timestamp: new Date().toISOString(),
      path
    }
  }
}

export const errorGenerator = ErrorGenerator.getInstance()

export const createErrorResponse = (
  scenario: ErrorScenario,
  path: string,
  customMessage?: string
): ErrorResponse => {
  return errorGenerator.generateError(scenario, path, customMessage)
}

export const createValidationError = (
  path: string,
  errors: Record<string, string[]>
): ErrorResponse => {
  return errorGenerator.generateValidationError(path, errors)
}

export const createNotFoundError = (path: string, resource?: string): ErrorResponse => {
  return errorGenerator.generateNotFoundError(path, resource)
}

export const createAuthError = (path: string, isTokenExpired?: boolean): ErrorResponse => {
  return errorGenerator.generateAuthError(path, isTokenExpired)
}

export const createServerError = (path: string, details?: string): ErrorResponse => {
  return errorGenerator.generateServerError(path, details)
}

export const ERROR_PRESETS: Record<string, ErrorConfig> = {
  invalidId: {
    code: 400,
    message: '无效的ID格式',
    errors: {
      id: ['ID必须是正整数']
    }
  },
  invalidEmail: {
    code: 400,
    message: '邮箱格式不正确',
    errors: {
      email: ['请输入有效的邮箱地址']
    }
  },
  invalidPhone: {
    code: 400,
    message: '手机号格式不正确',
    errors: {
      phone: ['请输入有效的手机号码']
    }
  },
  passwordTooShort: {
    code: 400,
    message: '密码长度不足',
    errors: {
      password: ['密码长度至少为6个字符']
    }
  },
  duplicateEmail: {
    code: 409,
    message: '邮箱已被注册',
    errors: {
      email: ['该邮箱已被使用，请更换或直接登录']
    }
  },
  outOfStock: {
    code: 400,
    message: '商品库存不足',
    errors: {
      stock: ['商品库存不足，无法购买']
    }
  },
  appointmentConflict: {
    code: 409,
    message: '预约时间冲突',
    errors: {
      appointmentTime: ['该时间段已被预约，请选择其他时间']
    }
  },
  orderAlreadyPaid: {
    code: 400,
    message: '订单已支付',
    errors: {
      status: ['订单已完成支付，请勿重复操作']
    }
  },
  orderCancelled: {
    code: 400,
    message: '订单已取消',
    errors: {
      status: ['订单已取消，无法执行此操作']
    }
  },
  serviceDisabled: {
    code: 400,
    message: '服务已下架',
    errors: {
      status: ['该服务已下架，无法预约']
    }
  }
}
