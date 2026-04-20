import type {
  BoundaryScenario,
  BoundaryConfig,
  ResourceType,
  PaginatedResponse
} from './types'
import { BOUNDARY_SCENARIOS } from './types'

export interface PaginationParams {
  page: number
  pageSize: number
  total: number
}

export interface ValueBoundary {
  min: number
  max: number
  current: number
}

export interface StringBoundary {
  minLength: number
  maxLength: number
  currentValue: string
}

export class BoundaryHandler {
  private static instance: BoundaryHandler
  private activeBoundaryScenarios: Map<string, BoundaryScenario> = new Map()
  private globalBoundaryEnabled: boolean = false
  private globalBoundaryScenario: BoundaryScenario | null = null

  static getInstance(): BoundaryHandler {
    if (!BoundaryHandler.instance) {
      BoundaryHandler.instance = new BoundaryHandler()
    }
    return BoundaryHandler.instance
  }

  enableGlobalBoundary(scenario: BoundaryScenario): void {
    this.globalBoundaryEnabled = true
    this.globalBoundaryScenario = scenario
  }

  disableGlobalBoundary(): void {
    this.globalBoundaryEnabled = false
    this.globalBoundaryScenario = null
  }

  setBoundary(resource: ResourceType, action: string, scenario: BoundaryScenario): void {
    const key = `${resource}:${action}`
    this.activeBoundaryScenarios.set(key, scenario)
  }

  clearBoundary(resource: ResourceType, action: string): void {
    const key = `${resource}:${action}`
    this.activeBoundaryScenarios.delete(key)
  }

  clearAllBoundaries(): void {
    this.activeBoundaryScenarios.clear()
    this.globalBoundaryEnabled = false
    this.globalBoundaryScenario = null
  }

  shouldApplyBoundary(resource: ResourceType, action: string): boolean {
    if (this.globalBoundaryEnabled && this.globalBoundaryScenario) {
      return true
    }
    const key = `${resource}:${action}`
    return this.activeBoundaryScenarios.has(key)
  }

  getBoundaryConfig(resource: ResourceType, action: string): BoundaryConfig | null {
    if (this.globalBoundaryEnabled && this.globalBoundaryScenario) {
      return BOUNDARY_SCENARIOS[this.globalBoundaryScenario]
    }
    const key = `${resource}:${action}`
    const scenario = this.activeBoundaryScenarios.get(key)
    return scenario ? BOUNDARY_SCENARIOS[scenario] : null
  }

  handlePaginationBoundary<T>(
    params: PaginationParams,
    data: T[]
  ): PaginatedResponse<T> {
    const { page, pageSize, total } = params
    const totalPages = Math.ceil(total / pageSize) || 1

    let actualPage = page
    let actualData = data

    if (page < 1) {
      actualPage = 1
    } else if (page > totalPages) {
      actualPage = totalPages
      actualData = []
    }

    return {
      code: 200,
      message: 'success',
      data: {
        data: actualData,
        total,
        page: actualPage,
        pageSize
      }
    }
  }

  generateFirstPageResponse<T>(data: T[], total: number, pageSize: number): PaginatedResponse<T> {
    return {
      code: 200,
      message: 'success',
      data: {
        data: data.slice(0, pageSize),
        total,
        page: 1,
        pageSize
      }
    }
  }

  generateLastPageResponse<T>(data: T[], total: number, pageSize: number): PaginatedResponse<T> {
    const totalPages = Math.ceil(total / pageSize)
    const startIndex = (totalPages - 1) * pageSize
    return {
      code: 200,
      message: 'success',
      data: {
        data: data.slice(startIndex),
        total,
        page: totalPages,
        pageSize
      }
    }
  }

  generateOutOfRangePageResponse<T>(total: number, pageSize: number, requestedPage: number): PaginatedResponse<T> {
    return {
      code: 200,
      message: 'success',
      data: {
        data: [],
        total,
        page: requestedPage,
        pageSize
      }
    }
  }

  generateMaxValueResponse<T>(data: T, maxValue: number): { data: T; maxValue: number } {
    return {
      data,
      maxValue
    }
  }

  generateMinValueResponse<T>(data: T, minValue: number): { data: T; minValue: number } {
    return {
      data,
      minValue
    }
  }

  generateZeroValueResponse<T>(): { data: T | null; value: number } {
    return {
      data: null,
      value: 0
    }
  }

  generateEmptyStringResponse(): string {
    return ''
  }

  generateLongStringResponse(length: number = 10000): string {
    return 'a'.repeat(length)
  }

  generateSpecialCharsString(): string {
    const specialChars = '!@#$%^&*()_+-=[]{}|;:\'",.<>?/~`\\n\\r\\t'
    return specialChars.repeat(10)
  }

  generateUnicodeString(): string {
    const unicodeChars = '你好世界🎉🔥💖日本語한국어العربيةעברית'
    return unicodeChars.repeat(10)
  }

  generateXSSString(): string {
    return '<script>alert("XSS")</script><img src=x onerror=alert("XSS")>'
  }

  generateSQLInjectionString(): string {
    return "'; DROP TABLE users; -- OR '1'='1"
  }

  validatePagination(page: number, pageSize: number, total: number): {
    valid: boolean
    error?: string
    correctedPage?: number
  } {
    const totalPages = Math.ceil(total / pageSize) || 1

    if (page < 1) {
      return {
        valid: false,
        error: '页码不能小于1',
        correctedPage: 1
      }
    }

    if (page > totalPages) {
      return {
        valid: false,
        error: `页码超出范围，最大页码为${totalPages}`,
        correctedPage: totalPages
      }
    }

    return { valid: true }
  }

  validateValue(value: number, min: number, max: number): {
    valid: boolean
    error?: string
  } {
    if (value < min) {
      return {
        valid: false,
        error: `值不能小于${min}`
      }
    }

    if (value > max) {
      return {
        valid: false,
        error: `值不能大于${max}`
      }
    }

    return { valid: true }
  }

  validateStringLength(value: string, minLength: number, maxLength: number): {
    valid: boolean
    error?: string
  } {
    if (value.length < minLength) {
      return {
        valid: false,
        error: `长度不能小于${minLength}个字符`
      }
    }

    if (value.length > maxLength) {
      return {
        valid: false,
        error: `长度不能超过${maxLength}个字符`
      }
    }

    return { valid: true }
  }
}

export const boundaryHandler = BoundaryHandler.getInstance()

export const setBoundaryScenario = (
  resource: ResourceType,
  action: string,
  scenario: BoundaryScenario
): void => {
  boundaryHandler.setBoundary(resource, action, scenario)
}

export const clearBoundaryScenario = (resource: ResourceType, action: string): void => {
  boundaryHandler.clearBoundary(resource, action)
}

export const handlePagination = <T>(
  params: PaginationParams,
  data: T[]
): PaginatedResponse<T> => {
  return boundaryHandler.handlePaginationBoundary(params, data)
}

export const generateLongString = (length?: number): string => {
  return boundaryHandler.generateLongStringResponse(length)
}

export const generateSpecialChars = (): string => {
  return boundaryHandler.generateSpecialCharsString()
}

export const BOUNDARY_TEST_DATA = {
  maxInt: Number.MAX_SAFE_INTEGER,
  minInt: Number.MIN_SAFE_INTEGER,
  maxFloat: Number.MAX_VALUE,
  minFloat: Number.MIN_VALUE,
  infinity: Infinity,
  negInfinity: -Infinity,
  nan: NaN,
  zero: 0,
  negZero: -0,
  emptyString: '',
  longString: 'a'.repeat(10000),
  unicodeString: '🎉🔥💖你好世界',
  specialChars: '!@#$%^&*()',
  xssPayload: '<script>alert("XSS")</script>',
  sqlInjection: "'; DROP TABLE users; --"
}

export const PAGINATION_BOUNDARIES = {
  firstPage: { page: 1, pageSize: 10 },
  lastPage: { page: 10, pageSize: 10 },
  outOfRange: { page: 9999, pageSize: 10 },
  singleItem: { page: 1, pageSize: 1 },
  maxPageSize: { page: 1, pageSize: 100 },
  zeroPageSize: { page: 1, pageSize: 0 },
  negativePage: { page: -1, pageSize: 10 },
  negativePageSize: { page: 1, pageSize: -10 }
}
