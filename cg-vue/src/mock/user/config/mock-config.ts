import type {
  ResourceType,
  ErrorScenario,
  DelayScenario,
  EmptyScenario,
  BoundaryScenario
} from './scenarios/types'
import type { GeneratedData } from '../utils/generators'

export interface MockDataConfig {
  enabled: boolean
  debug: boolean
  defaultDelay: number
  persistConfig: boolean
  storageKey: string
}

export interface DataOverrideConfig {
  users?: Partial<GeneratedData['users'][0]>[]
  pets?: Partial<GeneratedData['pets'][0]>[]
  merchants?: Partial<GeneratedData['merchants'][0]>[]
  services?: Partial<GeneratedData['services'][0]>[]
  products?: Partial<GeneratedData['products'][0]>[]
  appointments?: Partial<GeneratedData['appointments'][0]>[]
  orders?: Partial<GeneratedData['orders'][0]>[]
  reviews?: Partial<GeneratedData['reviews'][0]>[]
}

export interface DataGeneratorConfig {
  userCount: number
  merchantCount: number
  servicesPerMerchant: number
  productsPerMerchant: number
  petsPerUser: number
  appointmentsPerUser: number
  ordersPerUser: number
  reviewsPerUser: number
}

export interface ResourceScenarioConfig {
  error?: ErrorScenario
  delay?: DelayScenario | { minMs: number; maxMs: number }
  empty?: EmptyScenario
  boundary?: BoundaryScenario
}

export type ResourceScenariosMap = Partial<Record<ResourceType, Partial<Record<string, ResourceScenarioConfig>>>>

export interface ScenarioProfile {
  id: string
  name: string
  description: string
  config: ResourceScenariosMap
}

export interface MockConfig {
  data: MockDataConfig
  generator: DataGeneratorConfig
  overrides: DataOverrideConfig
  scenarios: ResourceScenariosMap
  profiles: ScenarioProfile[]
}

export const DEFAULT_MOCK_CONFIG: MockConfig = {
  data: {
    enabled: true,
    debug: true,
    defaultDelay: 200,
    persistConfig: true,
    storageKey: 'user-mock-config'
  },
  generator: {
    userCount: 10,
    merchantCount: 15,
    servicesPerMerchant: 3,
    productsPerMerchant: 5,
    petsPerUser: 2,
    appointmentsPerUser: 3,
    ordersPerUser: 3,
    reviewsPerUser: 2
  },
  overrides: {},
  scenarios: {},
  profiles: []
}

export const BUILTIN_PROFILES: ScenarioProfile[] = [
  {
    id: 'normal',
    name: '正常模式',
    description: '所有功能正常运行，无异常',
    config: {}
  },
  {
    id: 'slow-network',
    name: '慢速网络',
    description: '模拟慢速网络环境，所有请求延迟2-3秒',
    config: {
      user: { list: { delay: 'slow' }, detail: { delay: 'slow' } },
      pet: { list: { delay: 'slow' }, detail: { delay: 'slow' } },
      service: { list: { delay: 'slow' }, detail: { delay: 'slow' } },
      product: { list: { delay: 'slow' }, detail: { delay: 'slow' } },
      merchant: { list: { delay: 'slow' }, detail: { delay: 'slow' } },
      appointment: { list: { delay: 'slow' }, detail: { delay: 'slow' } },
      order: { list: { delay: 'slow' }, detail: { delay: 'slow' } },
      review: { list: { delay: 'slow' }, detail: { delay: 'slow' } }
    }
  },
  {
    id: 'error-500',
    name: '服务器错误',
    description: '所有请求返回500错误',
    config: {
      user: { list: { error: 'internal_error' } },
      pet: { list: { error: 'internal_error' } },
      service: { list: { error: 'internal_error' } },
      product: { list: { error: 'internal_error' } },
      merchant: { list: { error: 'internal_error' } },
      appointment: { list: { error: 'internal_error' } },
      order: { list: { error: 'internal_error' } },
      review: { list: { error: 'internal_error' } }
    }
  },
  {
    id: 'unauthorized',
    name: '未授权模式',
    description: '模拟未登录状态，返回401错误',
    config: {
      user: { detail: { error: 'unauthorized' } },
      pet: { list: { error: 'unauthorized' } },
      appointment: { list: { error: 'unauthorized' } },
      order: { list: { error: 'unauthorized' } },
      review: { list: { error: 'unauthorized' } }
    }
  },
  {
    id: 'empty-data',
    name: '空数据模式',
    description: '所有列表返回空数据',
    config: {
      user: { list: { empty: 'empty_list' } },
      pet: { list: { empty: 'empty_list' } },
      service: { list: { empty: 'empty_list' } },
      product: { list: { empty: 'empty_list' } },
      merchant: { list: { empty: 'empty_list' } },
      appointment: { list: { empty: 'empty_list' } },
      order: { list: { empty: 'empty_list' } },
      review: { list: { empty: 'empty_list' } }
    }
  },
  {
    id: 'timeout',
    name: '超时模式',
    description: '所有请求超时',
    config: {
      user: { list: { delay: 'timeout' } },
      pet: { list: { delay: 'timeout' } },
      service: { list: { delay: 'timeout' } },
      product: { list: { delay: 'timeout' } },
      merchant: { list: { delay: 'timeout' } },
      appointment: { list: { delay: 'timeout' } },
      order: { list: { delay: 'timeout' } },
      review: { list: { delay: 'timeout' } }
    }
  },
  {
    id: 'mixed-scenarios',
    name: '混合场景',
    description: '不同资源返回不同状态，用于综合测试',
    config: {
      service: { list: { delay: 'slow' } },
      product: { list: { empty: 'empty_list' } },
      appointment: { list: { error: 'not_found' } },
      order: { detail: { error: 'internal_error' } }
    }
  },
  {
    id: 'stress-test',
    name: '压力测试',
    description: '大量数据和慢速响应',
    config: {
      user: { list: { delay: 'very_slow' } },
      pet: { list: { delay: 'very_slow' } },
      service: { list: { delay: 'very_slow' } },
      product: { list: { delay: 'very_slow' } }
    }
  },
  {
    id: 'network-unstable',
    name: '网络不稳定',
    description: '随机延迟，模拟网络波动',
    config: {
      user: { list: { delay: 'random' } },
      pet: { list: { delay: 'random' } },
      service: { list: { delay: 'random' } },
      product: { list: { delay: 'random' } },
      merchant: { list: { delay: 'random' } },
      appointment: { list: { delay: 'random' } },
      order: { list: { delay: 'random' } },
      review: { list: { delay: 'random' } }
    }
  },
  {
    id: 'service-unavailable',
    name: '服务不可用',
    description: '模拟服务维护状态',
    config: {
      user: { list: { error: 'service_unavailable' } },
      pet: { list: { error: 'service_unavailable' } },
      service: { list: { error: 'service_unavailable' } },
      product: { list: { error: 'service_unavailable' } },
      merchant: { list: { error: 'service_unavailable' } },
      appointment: { list: { error: 'service_unavailable' } },
      order: { list: { error: 'service_unavailable' } },
      review: { list: { error: 'service_unavailable' } }
    }
  }
]

export const QUICK_SCENARIOS: Record<string, ResourceScenariosMap> = {
  serviceError: {
    service: { list: { error: 'internal_error' }, detail: { error: 'internal_error' } }
  },
  productEmpty: {
    product: { list: { empty: 'empty_list' } }
  },
  orderNotFound: {
    order: { detail: { error: 'not_found' } }
  },
  appointmentSlow: {
    appointment: { list: { delay: 'slow' }, detail: { delay: 'slow' } }
  },
  merchantTimeout: {
    merchant: { list: { delay: 'timeout' } }
  },
  reviewForbidden: {
    review: { list: { error: 'forbidden' }, create: { error: 'forbidden' } }
  },
  userUnauthorized: {
    user: { detail: { error: 'unauthorized' } }
  },
  petEmpty: {
    pet: { list: { empty: 'empty_list' } }
  }
}

export type MockConfigKey = keyof MockConfig
export type DataConfigKey = keyof MockConfig['data']
export type GeneratorConfigKey = keyof MockConfig['generator']

export function createMockConfig(overrides: Partial<MockConfig> = {}): MockConfig {
  return {
    ...DEFAULT_MOCK_CONFIG,
    ...overrides,
    data: { ...DEFAULT_MOCK_CONFIG.data, ...overrides.data },
    generator: { ...DEFAULT_MOCK_CONFIG.generator, ...overrides.generator },
    overrides: { ...DEFAULT_MOCK_CONFIG.overrides, ...overrides.overrides },
    scenarios: { ...DEFAULT_MOCK_CONFIG.scenarios, ...overrides.scenarios },
    profiles: [...DEFAULT_MOCK_CONFIG.profiles, ...(overrides.profiles || [])]
  }
}

export function validateMockConfig(config: unknown): config is MockConfig {
  if (typeof config !== 'object' || config === null) return false
  const c = config as Record<string, unknown>
  return (
    typeof c.data === 'object' &&
    typeof c.generator === 'object' &&
    typeof c.overrides === 'object' &&
    typeof c.scenarios === 'object' &&
    Array.isArray(c.profiles)
  )
}
