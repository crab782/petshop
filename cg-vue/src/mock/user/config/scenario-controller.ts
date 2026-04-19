import type {
  ResourceType,
  ErrorScenario,
  DelayScenario,
  EmptyScenario,
  BoundaryScenario
} from './scenarios/types'
import { scenarioService } from './scenarios/index'
import type {
  ScenarioProfile,
  ResourceScenariosMap,
  ResourceScenarioConfig,
  BUILTIN_PROFILES,
  QUICK_SCENARIOS
} from './mock-config'
import { DEFAULT_MOCK_CONFIG } from './mock-config'

export interface ScenarioState {
  activeProfile: string | null
  activeScenarios: Map<string, ResourceScenarioConfig>
  history: Array<{
    timestamp: number
    action: string
    profile?: string
    scenarios?: ResourceScenariosMap
  }>
}

export interface ScenarioSnapshot {
  id: string
  name: string
  timestamp: number
  state: ScenarioState
}

class ScenarioController {
  private static instance: ScenarioController
  private state: ScenarioState = {
    activeProfile: null,
    activeScenarios: new Map(),
    history: []
  }
  private snapshots: Map<string, ScenarioSnapshot> = new Map()
  private listeners: Set<(state: ScenarioState) => void> = new Set()
  private profiles: Map<string, ScenarioProfile> = new Map()

  static getInstance(): ScenarioController {
    if (!ScenarioController.instance) {
      ScenarioController.instance = new ScenarioController()
      ScenarioController.instance.initializeProfiles()
    }
    return ScenarioController.instance
  }

  private initializeProfiles(): void {
    const builtinProfiles: ScenarioProfile[] = [
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
        config: this.createSlowNetworkConfig()
      },
      {
        id: 'error-500',
        name: '服务器错误',
        description: '所有请求返回500错误',
        config: this.createError500Config()
      },
      {
        id: 'unauthorized',
        name: '未授权模式',
        description: '模拟未登录状态，返回401错误',
        config: this.createUnauthorizedConfig()
      },
      {
        id: 'empty-data',
        name: '空数据模式',
        description: '所有列表返回空数据',
        config: this.createEmptyDataConfig()
      },
      {
        id: 'timeout',
        name: '超时模式',
        description: '所有请求超时',
        config: this.createTimeoutConfig()
      },
      {
        id: 'mixed-scenarios',
        name: '混合场景',
        description: '不同资源返回不同状态，用于综合测试',
        config: this.createMixedConfig()
      },
      {
        id: 'stress-test',
        name: '压力测试',
        description: '大量数据和慢速响应',
        config: this.createStressTestConfig()
      },
      {
        id: 'network-unstable',
        name: '网络不稳定',
        description: '随机延迟，模拟网络波动',
        config: this.createUnstableConfig()
      },
      {
        id: 'service-unavailable',
        name: '服务不可用',
        description: '模拟服务维护状态',
        config: this.createServiceUnavailableConfig()
      }
    ]

    builtinProfiles.forEach(profile => {
      this.profiles.set(profile.id, profile)
    })
  }

  private createSlowNetworkConfig(): ResourceScenariosMap {
    const resources: ResourceType[] = ['user', 'pet', 'service', 'product', 'merchant', 'appointment', 'order', 'review']
    const config: ResourceScenariosMap = {}
    resources.forEach(resource => {
      config[resource] = {
        list: { delay: 'slow' },
        detail: { delay: 'slow' }
      }
    })
    return config
  }

  private createError500Config(): ResourceScenariosMap {
    const resources: ResourceType[] = ['user', 'pet', 'service', 'product', 'merchant', 'appointment', 'order', 'review']
    const config: ResourceScenariosMap = {}
    resources.forEach(resource => {
      config[resource] = { list: { error: 'internal_error' } }
    })
    return config
  }

  private createUnauthorizedConfig(): ResourceScenariosMap {
    return {
      user: { detail: { error: 'unauthorized' } },
      pet: { list: { error: 'unauthorized' } },
      appointment: { list: { error: 'unauthorized' } },
      order: { list: { error: 'unauthorized' } },
      review: { list: { error: 'unauthorized' } }
    }
  }

  private createEmptyDataConfig(): ResourceScenariosMap {
    const resources: ResourceType[] = ['user', 'pet', 'service', 'product', 'merchant', 'appointment', 'order', 'review']
    const config: ResourceScenariosMap = {}
    resources.forEach(resource => {
      config[resource] = { list: { empty: 'empty_list' } }
    })
    return config
  }

  private createTimeoutConfig(): ResourceScenariosMap {
    const resources: ResourceType[] = ['user', 'pet', 'service', 'product', 'merchant', 'appointment', 'order', 'review']
    const config: ResourceScenariosMap = {}
    resources.forEach(resource => {
      config[resource] = { list: { delay: 'timeout' } }
    })
    return config
  }

  private createMixedConfig(): ResourceScenariosMap {
    return {
      service: { list: { delay: 'slow' } },
      product: { list: { empty: 'empty_list' } },
      appointment: { list: { error: 'not_found' } },
      order: { detail: { error: 'internal_error' } }
    }
  }

  private createStressTestConfig(): ResourceScenariosMap {
    return {
      user: { list: { delay: 'very_slow' } },
      pet: { list: { delay: 'very_slow' } },
      service: { list: { delay: 'very_slow' } },
      product: { list: { delay: 'very_slow' } }
    }
  }

  private createUnstableConfig(): ResourceScenariosMap {
    const resources: ResourceType[] = ['user', 'pet', 'service', 'product', 'merchant', 'appointment', 'order', 'review']
    const config: ResourceScenariosMap = {}
    resources.forEach(resource => {
      config[resource] = { list: { delay: 'random' } }
    })
    return config
  }

  private createServiceUnavailableConfig(): ResourceScenariosMap {
    const resources: ResourceType[] = ['user', 'pet', 'service', 'product', 'merchant', 'appointment', 'order', 'review']
    const config: ResourceScenariosMap = {}
    resources.forEach(resource => {
      config[resource] = { list: { error: 'service_unavailable' } }
    })
    return config
  }

  getProfiles(): ScenarioProfile[] {
    return Array.from(this.profiles.values())
  }

  getProfile(id: string): ScenarioProfile | undefined {
    return this.profiles.get(id)
  }

  registerProfile(profile: ScenarioProfile): void {
    this.profiles.set(profile.id, profile)
    this.notifyListeners()
  }

  unregisterProfile(id: string): void {
    this.profiles.delete(id)
    this.notifyListeners()
  }

  applyProfile(profileId: string): boolean {
    const profile = this.profiles.get(profileId)
    if (!profile) return false

    this.reset()

    Object.entries(profile.config).forEach(([resource, actions]) => {
      Object.entries(actions || {}).forEach(([action, config]) => {
        this.applyScenarioConfig(resource as ResourceType, action, config)
      })
    })

    this.state.activeProfile = profileId
    this.addToHistory(`Applied profile: ${profileId}`, profileId)

    this.notifyListeners()
    return true
  }

  applyScenarioConfig(
    resource: ResourceType,
    action: string,
    config: ResourceScenarioConfig
  ): void {
    const key = `${resource}:${action}`
    this.state.activeScenarios.set(key, config)

    if (config.error) {
      scenarioService.enableError(config.error, resource, action)
    }

    if (config.delay) {
      if (typeof config.delay === 'string') {
        scenarioService.enableDelay(config.delay, resource, action)
      } else {
        scenarioService.setCustomDelay(resource, action, config.delay)
      }
    }

    if (config.empty) {
      scenarioService.enableEmptyData(config.empty, resource, action)
    }

    if (config.boundary) {
      scenarioService.enableBoundary(config.boundary, resource, action)
    }

    this.notifyListeners()
  }

  setScenario(
    resource: ResourceType,
    action: string,
    type: 'error' | 'delay' | 'empty' | 'boundary',
    scenario: ErrorScenario | DelayScenario | EmptyScenario | BoundaryScenario
  ): void {
    const key = `${resource}:${action}`
    const existing = this.state.activeScenarios.get(key) || {}
    existing[type] = scenario as never
    this.applyScenarioConfig(resource, action, existing)
    this.addToHistory(`Set ${type} scenario for ${resource}:${action}`)
  }

  clearScenario(resource: ResourceType, action: string, type?: 'error' | 'delay' | 'empty' | 'boundary'): void {
    const key = `${resource}:${action}`

    if (type) {
      const existing = this.state.activeScenarios.get(key)
      if (existing) {
        delete existing[type]
        if (Object.keys(existing).length === 0) {
          this.state.activeScenarios.delete(key)
        }
      }

      switch (type) {
        case 'error':
          scenarioService.disableError(resource, action)
          break
        case 'delay':
          scenarioService.disableDelay(resource, action)
          break
        case 'empty':
          scenarioService.disableEmptyData(resource, action)
          break
        case 'boundary':
          scenarioService.disableBoundary(resource, action)
          break
      }
    } else {
      this.state.activeScenarios.delete(key)
      scenarioService.disableError(resource, action)
      scenarioService.disableDelay(resource, action)
      scenarioService.disableEmptyData(resource, action)
      scenarioService.disableBoundary(resource, action)
    }

    this.addToHistory(`Cleared scenario for ${resource}:${action}`)
    this.notifyListeners()
  }

  applyQuickScenario(name: keyof typeof QUICK_SCENARIOS): void {
    const quickScenarios: Record<string, ResourceScenariosMap> = {
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

    const config = quickScenarios[name]
    if (config) {
      Object.entries(config).forEach(([resource, actions]) => {
        Object.entries(actions || {}).forEach(([action, scenarioConfig]) => {
          this.applyScenarioConfig(resource as ResourceType, action, scenarioConfig)
        })
      })
      this.addToHistory(`Applied quick scenario: ${name}`)
      this.notifyListeners()
    }
  }

  reset(): void {
    scenarioService.reset()
    this.state.activeProfile = null
    this.state.activeScenarios.clear()
    this.addToHistory('Reset all scenarios')
    this.notifyListeners()
  }

  getState(): ScenarioState {
    return { ...this.state }
  }

  getActiveProfile(): string | null {
    return this.state.activeProfile
  }

  getActiveScenarios(): Map<string, ResourceScenarioConfig> {
    return new Map(this.state.activeScenarios)
  }

  createSnapshot(name: string): string {
    const id = `snapshot-${Date.now()}`
    const snapshot: ScenarioSnapshot = {
      id,
      name,
      timestamp: Date.now(),
      state: {
        activeProfile: this.state.activeProfile,
        activeScenarios: new Map(this.state.activeScenarios),
        history: [...this.state.history]
      }
    }
    this.snapshots.set(id, snapshot)
    return id
  }

  restoreSnapshot(id: string): boolean {
    const snapshot = this.snapshots.get(id)
    if (!snapshot) return false

    this.reset()

    snapshot.state.activeScenarios.forEach((config, key) => {
      const [resource, action] = key.split(':') as [ResourceType, string]
      this.applyScenarioConfig(resource, action, config)
    })

    this.state.activeProfile = snapshot.state.activeProfile
    this.state.history = [...snapshot.state.history]
    this.addToHistory(`Restored snapshot: ${snapshot.name}`)

    this.notifyListeners()
    return true
  }

  getSnapshots(): ScenarioSnapshot[] {
    return Array.from(this.snapshots.values())
  }

  deleteSnapshot(id: string): void {
    this.snapshots.delete(id)
  }

  getHistory(): ScenarioState['history'] {
    return [...this.state.history]
  }

  clearHistory(): void {
    this.state.history = []
    this.notifyListeners()
  }

  private addToHistory(action: string, profile?: string): void {
    this.state.history.push({
      timestamp: Date.now(),
      action,
      profile
    })

    if (this.state.history.length > 100) {
      this.state.history = this.state.history.slice(-100)
    }
  }

  subscribe(listener: (state: ScenarioState) => void): () => void {
    this.listeners.add(listener)
    return () => this.listeners.delete(listener)
  }

  private notifyListeners(): void {
    this.listeners.forEach(listener => listener({ ...this.state }))
  }

  exportState(): string {
    return JSON.stringify({
      activeProfile: this.state.activeProfile,
      activeScenarios: Object.fromEntries(this.state.activeScenarios),
      history: this.state.history
    })
  }

  importState(json: string): boolean {
    try {
      const data = JSON.parse(json)
      this.reset()

      if (data.activeProfile) {
        this.applyProfile(data.activeProfile)
      }

      if (data.activeScenarios) {
        Object.entries(data.activeScenarios as Record<string, ResourceScenarioConfig>).forEach(([key, config]) => {
          const [resource, action] = key.split(':') as [ResourceType, string]
          this.applyScenarioConfig(resource, action, config)
        })
      }

      this.state.history = data.history || []
      this.notifyListeners()
      return true
    } catch {
      return false
    }
  }

  enableError(resource: ResourceType, action: string, scenario: ErrorScenario): void {
    this.setScenario(resource, action, 'error', scenario)
  }

  enableDelay(resource: ResourceType, action: string, scenario: DelayScenario): void {
    this.setScenario(resource, action, 'delay', scenario)
  }

  enableEmpty(resource: ResourceType, action: string, scenario: EmptyScenario): void {
    this.setScenario(resource, action, 'empty', scenario)
  }

  enableBoundary(resource: ResourceType, action: string, scenario: BoundaryScenario): void {
    this.setScenario(resource, action, 'boundary', scenario)
  }
}

export const scenarioController = ScenarioController.getInstance()

export const getProfiles = (): ScenarioProfile[] => scenarioController.getProfiles()

export const applyProfile = (profileId: string): boolean => scenarioController.applyProfile(profileId)

export const setScenario = (
  resource: ResourceType,
  action: string,
  type: 'error' | 'delay' | 'empty' | 'boundary',
  scenario: ErrorScenario | DelayScenario | EmptyScenario | BoundaryScenario
): void => scenarioController.setScenario(resource, action, type, scenario)

export const clearScenario = (
  resource: ResourceType,
  action: string,
  type?: 'error' | 'delay' | 'empty' | 'boundary'
): void => scenarioController.clearScenario(resource, action, type)

export const resetScenarios = (): void => scenarioController.reset()

export const getScenarioState = (): ScenarioState => scenarioController.getState()

export const createSnapshot = (name: string): string => scenarioController.createSnapshot(name)

export const restoreSnapshot = (id: string): boolean => scenarioController.restoreSnapshot(id)

export const getSnapshots = (): ScenarioSnapshot[] => scenarioController.getSnapshots()

export default scenarioController
