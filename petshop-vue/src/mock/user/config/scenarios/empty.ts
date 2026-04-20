import type {
  EmptyScenario,
  EmptyDataConfig,
  ResourceType,
  ApiResponse,
  PaginatedResponse
} from './types'
import { EMPTY_SCENARIOS } from './types'

export class EmptyDataHandler {
  private static instance: EmptyDataHandler
  private activeEmptyScenarios: Map<string, EmptyScenario> = new Map()
  private globalEmptyEnabled: boolean = false
  private globalEmptyScenario: EmptyScenario | null = null

  static getInstance(): EmptyDataHandler {
    if (!EmptyDataHandler.instance) {
      EmptyDataHandler.instance = new EmptyDataHandler()
    }
    return EmptyDataHandler.instance
  }

  enableGlobalEmpty(scenario: EmptyScenario): void {
    this.globalEmptyEnabled = true
    this.globalEmptyScenario = scenario
  }

  disableGlobalEmpty(): void {
    this.globalEmptyEnabled = false
    this.globalEmptyScenario = null
  }

  setEmpty(resource: ResourceType, action: string, scenario: EmptyScenario): void {
    const key = `${resource}:${action}`
    this.activeEmptyScenarios.set(key, scenario)
  }

  clearEmpty(resource: ResourceType, action: string): void {
    const key = `${resource}:${action}`
    this.activeEmptyScenarios.delete(key)
  }

  clearAllEmpty(): void {
    this.activeEmptyScenarios.clear()
    this.globalEmptyEnabled = false
    this.globalEmptyScenario = null
  }

  shouldReturnEmpty(resource: ResourceType, action: string): boolean {
    if (this.globalEmptyEnabled && this.globalEmptyScenario) {
      return true
    }
    const key = `${resource}:${action}`
    return this.activeEmptyScenarios.has(key)
  }

  getEmptyConfig(resource: ResourceType, action: string): EmptyDataConfig | null {
    if (this.globalEmptyEnabled && this.globalEmptyScenario) {
      return EMPTY_SCENARIOS[this.globalEmptyScenario]
    }
    const key = `${resource}:${action}`
    const scenario = this.activeEmptyScenarios.get(key)
    return scenario ? EMPTY_SCENARIOS[scenario] : null
  }

  generateEmptyListResponse<T>(): PaginatedResponse<T> {
    return {
      code: 200,
      message: 'success',
      data: {
        data: [],
        total: 0,
        page: 1,
        pageSize: 10
      }
    }
  }

  generateEmptyDetailResponse<T>(): ApiResponse<T | null> {
    return {
      code: 200,
      message: 'success',
      data: null
    }
  }

  generateNoSearchResultsResponse<T>(keyword?: string): PaginatedResponse<T> {
    return {
      code: 200,
      message: keyword
        ? `未找到与"${keyword}"相关的结果`
        : '未找到相关结果',
      data: {
        data: [],
        total: 0,
        page: 1,
        pageSize: 10
      }
    }
  }

  generateEmptyState<T>(
    resource: ResourceType,
    action: string,
    originalData?: T
  ): ApiResponse<T | null> | PaginatedResponse<T extends Array<any> ? T[0] : T> {
    const config = this.getEmptyConfig(resource, action)

    if (!config) {
      return {
        code: 200,
        message: 'success',
        data: originalData as any
      }
    }

    if (action === 'list' || action === 'search') {
      if (config.returnEmptyArray) {
        return this.generateEmptyListResponse()
      }
    }

    if (action === 'detail') {
      if (config.returnNullObject) {
        return this.generateEmptyDetailResponse()
      }
    }

    return {
      code: 200,
      message: 'success',
      data: null
    }
  }
}

export const emptyDataHandler = EmptyDataHandler.getInstance()

export const setEmptyScenario = (
  resource: ResourceType,
  action: string,
  scenario: EmptyScenario
): void => {
  emptyDataHandler.setEmpty(resource, action, scenario)
}

export const clearEmptyScenario = (resource: ResourceType, action: string): void => {
  emptyDataHandler.clearEmpty(resource, action)
}

export const generateEmptyList = <T>(): PaginatedResponse<T> => {
  return emptyDataHandler.generateEmptyListResponse()
}

export const generateEmptyDetail = <T>(): ApiResponse<T | null> => {
  return emptyDataHandler.generateEmptyDetailResponse()
}

export const generateNoResults = <T>(keyword?: string): PaginatedResponse<T> => {
  return emptyDataHandler.generateNoSearchResultsResponse(keyword)
}

export const EMPTY_STATE_MESSAGES: Record<ResourceType, Record<string, string>> = {
  user: {
    list: '暂无用户数据',
    detail: '用户不存在',
    search: '未找到匹配的用户'
  },
  pet: {
    list: '您还没有添加宠物',
    detail: '宠物信息不存在',
    search: '未找到匹配的宠物'
  },
  service: {
    list: '暂无可用服务',
    detail: '服务不存在或已下架',
    search: '未找到匹配的服务'
  },
  product: {
    list: '暂无商品',
    detail: '商品不存在或已下架',
    search: '未找到匹配的商品'
  },
  merchant: {
    list: '暂无商家',
    detail: '商家不存在',
    search: '未找到匹配的商家'
  },
  appointment: {
    list: '暂无预约记录',
    detail: '预约记录不存在',
    search: '未找到匹配的预约'
  },
  order: {
    list: '暂无订单记录',
    detail: '订单不存在',
    search: '未找到匹配的订单'
  },
  review: {
    list: '暂无评价记录',
    detail: '评价不存在',
    search: '未找到匹配的评价'
  },
  cart: {
    list: '购物车是空的',
    detail: '商品不存在',
    search: '购物车中没有找到该商品'
  },
  favorite: {
    list: '暂无收藏',
    detail: '收藏不存在',
    search: '未找到匹配的收藏'
  },
  notification: {
    list: '暂无消息通知',
    detail: '通知不存在',
    search: '未找到匹配的通知'
  },
  address: {
    list: '暂无收货地址',
    detail: '地址不存在',
    search: '未找到匹配的地址'
  }
}

export const getEmptyStateMessage = (
  resource: ResourceType,
  action: string
): string => {
  return EMPTY_STATE_MESSAGES[resource]?.[action] || '暂无数据'
}

export const createEmptyResponseWithMessage = <T>(
  resource: ResourceType,
  action: string
): PaginatedResponse<T> => {
  return {
    code: 200,
    message: 'success',
    data: {
      data: [],
      total: 0,
      page: 1,
      pageSize: 10
    }
  }
}
