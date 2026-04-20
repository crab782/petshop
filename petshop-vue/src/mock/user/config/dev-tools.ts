import type {
  ResourceType,
  ErrorScenario,
  DelayScenario,
  EmptyScenario,
  BoundaryScenario
} from './scenarios/types'
import type { GeneratedData } from '../utils/generators'
import { mockDataManager, getData, getResource, getItem, setItem, addItem, removeItem, queryData, resetData, regenerateData, getDataStatistics } from './data-manager'
import {
  scenarioController,
  getProfiles,
  applyProfile,
  setScenario,
  clearScenario,
  resetScenarios,
  getScenarioState,
  createSnapshot,
  restoreSnapshot,
  getSnapshots
} from './scenario-controller'
import type { MockConfig, DataGeneratorConfig, ScenarioProfile } from './mock-config'
import { DEFAULT_MOCK_CONFIG, createMockConfig } from './mock-config'

export interface DevToolsAPI {
  data: {
    get: () => GeneratedData
    getResource: <K extends keyof GeneratedData>(resource: K) => GeneratedData[K]
    getItem: <K extends keyof GeneratedData>(resource: K, id: number) => GeneratedData[K][number] | undefined
    set: <K extends keyof GeneratedData>(resource: K, id: number, updates: Partial<GeneratedData[K][number]>) => boolean
    add: <K extends keyof GeneratedData>(resource: K, item: GeneratedData[K][number]) => GeneratedData[K][number]
    remove: <K extends keyof GeneratedData>(resource: K, id: number) => boolean
    query: <K extends keyof GeneratedData>(resource: K, options?: { page?: number; pageSize?: number; filters?: Record<string, unknown> }) => { data: GeneratedData[K]; total: number; page: number; pageSize: number }
    reset: () => void
    regenerate: (config?: DataGeneratorConfig) => GeneratedData
    stats: () => ReturnType<typeof getDataStatistics>
  }
  scenarios: {
    profiles: {
      list: () => ScenarioProfile[]
      apply: (id: string) => boolean
      get: (id: string) => ScenarioProfile | undefined
    }
    set: (resource: ResourceType, action: string, type: 'error' | 'delay' | 'empty' | 'boundary', scenario: ErrorScenario | DelayScenario | EmptyScenario | BoundaryScenario) => void
    clear: (resource: ResourceType, action: string, type?: 'error' | 'delay' | 'empty' | 'boundary') => void
    reset: () => void
    state: () => ReturnType<typeof getScenarioState>
    snapshots: {
      create: (name: string) => string
      restore: (id: string) => boolean
      list: () => ReturnType<typeof getSnapshots>
      delete: (id: string) => void
    }
  }
  config: {
    get: () => MockConfig
    set: (config: Partial<MockConfig>) => void
    reset: () => void
    export: () => string
    import: (json: string) => boolean
  }
  utils: {
    log: (message: string, data?: unknown) => void
    table: <T>(data: T[]) => void
    time: (label: string) => void
    timeEnd: (label: string) => void
    profile: (name: string, fn: () => void) => void
  }
}

class MockDevTools {
  private config: MockConfig
  private initialized: boolean = false

  constructor() {
    this.config = createMockConfig()
  }

  initialize(config?: Partial<MockConfig>): void {
    if (this.initialized) {
      console.warn('[MockDevTools] Already initialized')
      return
    }

    this.config = createMockConfig(config)
    mockDataManager.initialize(this.config.generator)
    this.initialized = true

    if (this.config.data.debug) {
      console.log('[MockDevTools] Initialized with config:', this.config)
    }

    this.exposeGlobals()
  }

  private exposeGlobals(): void {
    if (typeof window !== 'undefined' && import.meta.env.DEV) {
      (window as unknown as Record<string, unknown>).__MOCK_DEV_TOOLS__ = this.getAPI()
      (window as unknown as Record<string, unknown>).mockDevTools = this.getAPI()
    }
  }

  getAPI(): DevToolsAPI {
    return {
      data: {
        get: getData,
        getResource: <K extends keyof GeneratedData>(resource: K) => getResource(resource),
        getItem: <K extends keyof GeneratedData>(resource: K, id: number) => getItem(resource, id),
        set: <K extends keyof GeneratedData>(resource: K, id: number, updates: Partial<GeneratedData[K][number]>) => setItem(resource, id, updates),
        add: <K extends keyof GeneratedData>(resource: K, item: GeneratedData[K][number]) => addItem(resource, item),
        remove: <K extends keyof GeneratedData>(resource: K, id: number) => removeItem(resource, id),
        query: <K extends keyof GeneratedData>(resource: K, options?: { page?: number; pageSize?: number; filters?: Record<string, unknown> }) => queryData(resource, options),
        reset: resetData,
        regenerate: (config?: DataGeneratorConfig) => regenerateData(config),
        stats: getDataStatistics
      },
      scenarios: {
        profiles: {
          list: getProfiles,
          apply: applyProfile,
          get: (id: string) => scenarioController.getProfile(id)
        },
        set: setScenario,
        clear: clearScenario,
        reset: resetScenarios,
        state: getScenarioState,
        snapshots: {
          create: createSnapshot,
          restore: restoreSnapshot,
          list: getSnapshots,
          delete: (id: string) => scenarioController.deleteSnapshot(id)
        }
      },
      config: {
        get: () => this.config,
        set: (config: Partial<MockConfig>) => {
          this.config = createMockConfig({ ...this.config, ...config })
          if (config.generator) {
            regenerateData(config.generator)
          }
        },
        reset: () => {
          this.config = createMockConfig()
          resetData()
          resetScenarios()
        },
        export: () => JSON.stringify({
          config: this.config,
          scenarioState: scenarioController.exportState()
        }),
        import: (json: string) => {
          try {
            const data = JSON.parse(json)
            if (data.config) {
              this.config = createMockConfig(data.config)
            }
            if (data.scenarioState) {
              scenarioController.importState(data.scenarioState)
            }
            return true
          } catch {
            return false
          }
        }
      },
      utils: {
        log: (message: string, data?: unknown) => {
          console.log(`[MockDevTools] ${message}`, data !== undefined ? data : '')
        },
        table: <T>(data: T[]) => {
          console.table(data)
        },
        time: (label: string) => {
          console.time(label)
        },
        timeEnd: (label: string) => {
          console.timeEnd(label)
        },
        profile: (name: string, fn: () => void) => {
          console.profile(name)
          fn()
          console.profileEnd(name)
        }
      }
    }
  }

  quickSetup(preset: 'normal' | 'slow' | 'error' | 'empty' | 'timeout' | 'mixed'): void {
    const presetMap: Record<string, string> = {
      normal: 'normal',
      slow: 'slow-network',
      error: 'error-500',
      empty: 'empty-data',
      timeout: 'timeout',
      mixed: 'mixed-scenarios'
    }

    const profileId = presetMap[preset]
    if (profileId) {
      applyProfile(profileId)
      console.log(`[MockDevTools] Applied preset: ${preset}`)
    }
  }

  enableError(resource: ResourceType, action: string, errorType: ErrorScenario = 'internal_error'): void {
    setScenario(resource, action, 'error', errorType)
    console.log(`[MockDevTools] Enabled ${errorType} error for ${resource}:${action}`)
  }

  enableDelay(resource: ResourceType, action: string, delayType: DelayScenario = 'slow'): void {
    setScenario(resource, action, 'delay', delayType)
    console.log(`[MockDevTools] Enabled ${delayType} delay for ${resource}:${action}`)
  }

  enableEmpty(resource: ResourceType, action: string): void {
    setScenario(resource, action, 'empty', 'empty_list')
    console.log(`[MockDevTools] Enabled empty data for ${resource}:${action}`)
  }

  createTestData(options: {
    users?: number
    merchants?: number
    services?: number
    products?: number
    appointments?: number
    orders?: number
  }): void {
    const config: DataGeneratorConfig = {
      userCount: options.users || 10,
      merchantCount: options.merchants || 15,
      servicesPerMerchant: Math.ceil((options.services || 45) / (options.merchants || 15)),
      productsPerMerchant: Math.ceil((options.products || 75) / (options.merchants || 15)),
      petsPerUser: 2,
      appointmentsPerUser: Math.ceil((options.appointments || 30) / (options.users || 10)),
      ordersPerUser: Math.ceil((options.orders || 30) / (options.users || 10)),
      reviewsPerUser: 2
    }

    regenerateData(config)
    console.log('[MockDevTools] Created test data with config:', config)
  }

  debug(): void {
    console.group('MockDevTools Debug Info')
    console.log('Config:', this.config)
    console.log('Data Stats:', getDataStatistics())
    console.log('Scenario State:', getScenarioState())
    console.log('Available Profiles:', getProfiles())
    console.groupEnd()
  }

  help(): void {
    console.log(`
MockDevTools Help
================

Data Operations:
  mockDevTools.data.get()                    - Get all mock data
  mockDevTools.data.getResource('users')     - Get specific resource data
  mockDevTools.data.getItem('users', 1)      - Get specific item by ID
  mockDevTools.data.set('users', 1, {...})   - Update item
  mockDevTools.data.add('users', {...})      - Add new item
  mockDevTools.data.remove('users', 1)       - Remove item
  mockDevTools.data.query('users', {...})    - Query with pagination/filters
  mockDevTools.data.reset()                  - Reset all data
  mockDevTools.data.regenerate({...})        - Regenerate with new config
  mockDevTools.data.stats()                  - Get data statistics

Scenario Operations:
  mockDevTools.scenarios.profiles.list()     - List all profiles
  mockDevTools.scenarios.profiles.apply(id)  - Apply a profile
  mockDevTools.scenarios.set(resource, action, type, scenario)  - Set scenario
  mockDevTools.scenarios.clear(resource, action, type)          - Clear scenario
  mockDevTools.scenarios.reset()             - Reset all scenarios
  mockDevTools.scenarios.state()             - Get current state
  mockDevTools.scenarios.snapshots.create(name)  - Create snapshot
  mockDevTools.scenarios.snapshots.restore(id)   - Restore snapshot
  mockDevTools.scenarios.snapshots.list()        - List snapshots

Config Operations:
  mockDevTools.config.get()                  - Get current config
  mockDevTools.config.set({...})             - Update config
  mockDevTools.config.reset()                - Reset to defaults
  mockDevTools.config.export()               - Export config as JSON
  mockDevTools.config.import(json)           - Import config

Utility Functions:
  mockDevTools.utils.log(message, data)      - Log with prefix
  mockDevTools.utils.table(data)             - Display as table
  mockDevTools.utils.time(label)             - Start timer
  mockDevTools.utils.timeEnd(label)          - End timer

Quick Setup:
  mockDevTools.quickSetup('normal')          - Normal mode
  mockDevTools.quickSetup('slow')            - Slow network
  mockDevTools.quickSetup('error')           - Server errors
  mockDevTools.quickSetup('empty')           - Empty data
  mockDevTools.quickSetup('timeout')         - Timeout mode
  mockDevTools.quickSetup('mixed')           - Mixed scenarios

Debug:
  mockDevTools.debug()                       - Show debug info
  mockDevTools.help()                        - Show this help
    `)
  }
}

const mockDevToolsInstance = new MockDevTools()

export const initMockDevTools = (config?: Partial<MockConfig>): void => {
  mockDevToolsInstance.initialize(config)
}

export const getMockDevTools = (): DevToolsAPI => {
  return mockDevToolsInstance.getAPI()
}

export const mockDevTools = mockDevToolsInstance

export default mockDevToolsInstance
