import Mock from 'mockjs'
import type { ResourceType, ApiResponse, PaginatedResponse } from './types'
import { scenarioService } from './index'

const Random = Mock.Random

export interface MockRequest {
  url: string
  type: string
  body?: any
  query?: Record<string, string>
}

export interface MockResponseOptions<T> {
  resource: ResourceType
  action: 'list' | 'detail' | 'create' | 'update' | 'delete' | 'search'
  data: T | (() => T)
  path: string
}

export class ScenarioMockHandler {
  private static instance: ScenarioMockHandler
  private enabled: boolean = false

  static getInstance(): ScenarioMockHandler {
    if (!ScenarioMockHandler.instance) {
      ScenarioMockHandler.instance = new ScenarioMockHandler()
    }
    return ScenarioMockHandler.instance
  }

  enable(): void {
    this.enabled = true
  }

  disable(): void {
    this.enabled = false
  }

  isEnabled(): boolean {
    return this.enabled
  }

  async handleWithScenario<T>(
    options: MockResponseOptions<T>
  ): Promise<ApiResponse<T> | PaginatedResponse<T extends Array<any> ? T[0] : T> | any> {
    const { resource, action, data, path } = options

    if (!this.enabled) {
      return this.generateNormalResponse(data)
    }

    await scenarioService.applyDelayIfEnabled(resource, action)

    if (scenarioService.shouldReturnError(resource, action)) {
      const errorResponse = scenarioService.getErrorResponse(resource, action, path)
      if (errorResponse) {
        return errorResponse
      }
    }

    if (scenarioService.shouldReturnEmpty(resource, action)) {
      if (action === 'list' || action === 'search') {
        return generateEmptyListResponse()
      }
      return {
        code: 200,
        message: 'success',
        data: null
      }
    }

    return this.generateNormalResponse(data)
  }

  private generateNormalResponse<T>(data: T | (() => T)): ApiResponse<T> {
    const result = typeof data === 'function' ? (data as () => T)() : data
    return {
      code: 200,
      message: 'success',
      data: result
    }
  }

  parseRequest(url: string, body?: string): MockRequest {
    const [path, queryString] = url.split('?')
    const query: Record<string, string> = {}

    if (queryString) {
      queryString.split('&').forEach(param => {
        const [key, value] = param.split('=')
        query[decodeURIComponent(key)] = decodeURIComponent(value || '')
      })
    }

    let parsedBody: any = undefined
    if (body) {
      try {
        parsedBody = JSON.parse(body)
      } catch {
        parsedBody = body
      }
    }

    return {
      url: path,
      type: '',
      body: parsedBody,
      query
    }
  }

  getQueryParams(request: MockRequest): Record<string, string> {
    return request.query || {}
  }

  getPagingParams(request: MockRequest): { page: number; pageSize: number } {
    const query = request.query || {}
    return {
      page: parseInt(query.page || '1', 10),
      pageSize: parseInt(query.pageSize || '10', 10)
    }
  }
}

function generateEmptyListResponse<T>(): PaginatedResponse<T> {
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

export const scenarioMockHandler = ScenarioMockHandler.getInstance()

export const createScenarioMock = <T>(
  resource: ResourceType,
  action: MockResponseOptions<T>['action'],
  path: string,
  dataGenerator: () => T
) => {
  return async (options: { url: string; body?: string; type: string }) => {
    const request = scenarioMockHandler.parseRequest(options.url, options.body)

    return scenarioMockHandler.handleWithScenario({
      resource,
      action,
      data: dataGenerator,
      path
    })
  }
}

export const setupScenarioMockHandlers = () => {
  scenarioMockHandler.enable()

  Mock.mock(/\/api\/user\/scenarios\/error/, 'get', () => {
    scenarioService.enableError('internal_error')
    return {
      code: 200,
      message: '已启用全局错误模式',
      data: null
    }
  })

  Mock.mock(/\/api\/user\/scenarios\/delay/, 'get', () => {
    scenarioService.enableDelay('slow')
    return {
      code: 200,
      message: '已启用延迟模式',
      data: null
    }
  })

  Mock.mock(/\/api\/user\/scenarios\/empty/, 'get', () => {
    scenarioService.enableEmptyData('empty_list')
    return {
      code: 200,
      message: '已启用空数据模式',
      data: null
    }
  })

  Mock.mock(/\/api\/user\/scenarios\/reset/, 'get', () => {
    scenarioService.reset()
    return {
      code: 200,
      message: '已重置所有场景',
      data: null
    }
  })

  Mock.mock(/\/api\/user\/scenarios\/preset/, 'get', (options: { url: string }) => {
    const url = new URL(options.url, 'http://localhost')
    const presetId = url.searchParams.get('id')
    if (presetId && scenarioService.applyPreset(presetId)) {
      return {
        code: 200,
        message: `已应用预设: ${presetId}`,
        data: null
      }
    }
    return {
      code: 400,
      message: '预设不存在',
      data: null
    }
  })

  Mock.mock(/\/api\/user\/scenarios\/presets/, 'get', () => {
    return {
      code: 200,
      message: 'success',
      data: scenarioService.getPresets()
    }
  })

  Mock.mock(/\/api\/user\/scenarios\/status/, 'get', () => {
    return {
      code: 200,
      message: 'success',
      data: scenarioService.exportConfig()
    }
  })
}

export const withScenarioHandling = async <T>(
  resource: ResourceType,
  action: MockResponseOptions<T>['action'],
  path: string,
  dataGenerator: () => T
): Promise<any> => {
  return scenarioMockHandler.handleWithScenario({
    resource,
    action,
    data: dataGenerator,
    path
  })
}

export default scenarioMockHandler
