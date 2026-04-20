export type ErrorCode = 400 | 401 | 403 | 404 | 500 | 502 | 503 | 504

export type ErrorScenario =
  | 'bad_request'
  | 'unauthorized'
  | 'forbidden'
  | 'not_found'
  | 'internal_error'
  | 'bad_gateway'
  | 'service_unavailable'
  | 'gateway_timeout'

export type EmptyScenario =
  | 'empty_list'
  | 'empty_detail'
  | 'no_search_results'
  | 'no_data'

export type BoundaryScenario =
  | 'first_page'
  | 'last_page'
  | 'out_of_range_page'
  | 'max_value'
  | 'min_value'
  | 'zero_value'
  | 'empty_string'
  | 'long_string'
  | 'special_chars'

export type DelayScenario =
  | 'none'
  | 'slow'
  | 'very_slow'
  | 'random'
  | 'timeout'

export interface ErrorConfig {
  code: ErrorCode
  message: string
  details?: string
  errors?: Record<string, string[]>
}

export interface DelayConfig {
  enabled: boolean
  minMs: number
  maxMs: number
  timeout: boolean
  timeoutMs: number
}

export interface EmptyDataConfig {
  returnEmptyArray: boolean
  returnNullObject: boolean
  returnZeroCount: boolean
}

export interface BoundaryConfig {
  page: number
  pageSize: number
  maxValue: number
  minValue: number
  stringLength: number
}

export interface ScenarioConfig {
  name: string
  description: string
  enabled: boolean
  error?: ErrorConfig
  delay?: DelayConfig
  empty?: EmptyDataConfig
  boundary?: BoundaryConfig
}

export interface ScenarioPreset {
  id: string
  name: string
  description: string
  scenarios: ScenarioConfig[]
}

export interface ErrorResponse {
  code: ErrorCode
  message: string
  data: null
  errors?: Record<string, string[]>
  timestamp: string
  path: string
}

export interface ScenarioState {
  currentScenario: string
  errorEnabled: boolean
  delayEnabled: boolean
  emptyEnabled: boolean
  boundaryEnabled: boolean
  activeErrors: Map<string, ErrorScenario>
  activeDelays: Map<string, DelayScenario>
  activeEmptyScenarios: Map<string, EmptyScenario>
  activeBoundaryScenarios: Map<string, BoundaryScenario>
}

export type ResourceType =
  | 'user'
  | 'pet'
  | 'service'
  | 'product'
  | 'merchant'
  | 'appointment'
  | 'order'
  | 'review'
  | 'cart'
  | 'favorite'
  | 'notification'
  | 'address'

export interface ScenarioTarget {
  resource: ResourceType
  action: 'list' | 'detail' | 'create' | 'update' | 'delete' | 'search'
  scenario: ErrorScenario | EmptyScenario | BoundaryScenario | DelayScenario
}

export const ERROR_SCENARIOS: Record<ErrorScenario, ErrorConfig> = {
  bad_request: {
    code: 400,
    message: '请求参数错误',
    details: '提交的数据格式不正确或缺少必要字段',
    errors: {
      field: ['该字段不能为空', '格式不正确']
    }
  },
  unauthorized: {
    code: 401,
    message: '未授权访问',
    details: '请先登录后再访问此资源'
  },
  forbidden: {
    code: 403,
    message: '禁止访问',
    details: '您没有权限访问此资源'
  },
  not_found: {
    code: 404,
    message: '资源不存在',
    details: '请求的资源已被删除或不存在'
  },
  internal_error: {
    code: 500,
    message: '服务器内部错误',
    details: '服务器处理请求时发生错误，请稍后重试'
  },
  bad_gateway: {
    code: 502,
    message: '网关错误',
    details: '上游服务器返回无效响应'
  },
  service_unavailable: {
    code: 503,
    message: '服务不可用',
    details: '服务器暂时无法处理请求，请稍后重试'
  },
  gateway_timeout: {
    code: 504,
    message: '网关超时',
    details: '上游服务器响应超时'
  }
}

export const DELAY_SCENARIOS: Record<DelayScenario, DelayConfig> = {
  none: {
    enabled: false,
    minMs: 0,
    maxMs: 0,
    timeout: false,
    timeoutMs: 0
  },
  slow: {
    enabled: true,
    minMs: 1000,
    maxMs: 2000,
    timeout: false,
    timeoutMs: 0
  },
  very_slow: {
    enabled: true,
    minMs: 3000,
    maxMs: 5000,
    timeout: false,
    timeoutMs: 0
  },
  random: {
    enabled: true,
    minMs: 100,
    maxMs: 3000,
    timeout: false,
    timeoutMs: 0
  },
  timeout: {
    enabled: true,
    minMs: 30000,
    maxMs: 60000,
    timeout: true,
    timeoutMs: 30000
  }
}

export const EMPTY_SCENARIOS: Record<EmptyScenario, EmptyDataConfig> = {
  empty_list: {
    returnEmptyArray: true,
    returnNullObject: false,
    returnZeroCount: true
  },
  empty_detail: {
    returnEmptyArray: false,
    returnNullObject: true,
    returnZeroCount: false
  },
  no_search_results: {
    returnEmptyArray: true,
    returnNullObject: false,
    returnZeroCount: true
  },
  no_data: {
    returnEmptyArray: true,
    returnNullObject: true,
    returnZeroCount: true
  }
}

export const BOUNDARY_SCENARIOS: Record<BoundaryScenario, BoundaryConfig> = {
  first_page: {
    page: 1,
    pageSize: 10,
    maxValue: 100,
    minValue: 0,
    stringLength: 10
  },
  last_page: {
    page: 10,
    pageSize: 10,
    maxValue: 100,
    minValue: 0,
    stringLength: 10
  },
  out_of_range_page: {
    page: 9999,
    pageSize: 10,
    maxValue: 100,
    minValue: 0,
    stringLength: 10
  },
  max_value: {
    page: 1,
    pageSize: 100,
    maxValue: Number.MAX_SAFE_INTEGER,
    minValue: 0,
    stringLength: 1000
  },
  min_value: {
    page: 1,
    pageSize: 1,
    maxValue: 100,
    minValue: Number.MIN_SAFE_INTEGER,
    stringLength: 1
  },
  zero_value: {
    page: 1,
    pageSize: 0,
    maxValue: 0,
    minValue: 0,
    stringLength: 0
  },
  empty_string: {
    page: 1,
    pageSize: 10,
    maxValue: 100,
    minValue: 0,
    stringLength: 0
  },
  long_string: {
    page: 1,
    pageSize: 10,
    maxValue: 100,
    minValue: 0,
    stringLength: 10000
  },
  special_chars: {
    page: 1,
    pageSize: 10,
    maxValue: 100,
    minValue: 0,
    stringLength: 100
  }
}
