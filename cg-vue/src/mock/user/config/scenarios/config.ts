import type {
  ScenarioConfig,
  ScenarioPreset,
  ErrorScenario,
  DelayScenario,
  EmptyScenario,
  BoundaryScenario,
  ResourceType
} from './types'
import { errorGenerator } from './errors'
import { delaySimulator } from './delay'
import { emptyDataHandler } from './empty'
import { boundaryHandler } from './boundary'

export class ScenarioConfigManager {
  private static instance: ScenarioConfigManager
  private presets: Map<string, ScenarioPreset> = new Map()
  private activePreset: string | null = null
  private customScenarios: Map<string, ScenarioConfig> = new Map()

  static getInstance(): ScenarioConfigManager {
    if (!ScenarioConfigManager.instance) {
      ScenarioConfigManager.instance = new ScenarioConfigManager()
      ScenarioConfigManager.instance.initializePresets()
    }
    return ScenarioConfigManager.instance
  }

  private initializePresets(): void {
    this.registerPreset({
      id: 'normal',
      name: '正常模式',
      description: '所有功能正常运行，无异常',
      scenarios: []
    })

    this.registerPreset({
      id: 'error-all',
      name: '全局错误模式',
      description: '所有请求返回500错误',
      scenarios: [
        {
          name: 'global-error',
          description: '全局服务器错误',
          enabled: true,
          error: {
            code: 500,
            message: '服务器内部错误'
          }
        }
      ]
    })

    this.registerPreset({
      id: 'slow-network',
      name: '慢速网络',
      description: '模拟慢速网络环境',
      scenarios: [
        {
          name: 'slow-delay',
          description: '响应延迟2-5秒',
          enabled: true,
          delay: {
            enabled: true,
            minMs: 2000,
            maxMs: 5000,
            timeout: false,
            timeoutMs: 0
          }
        }
      ]
    })

    this.registerPreset({
      id: 'timeout',
      name: '超时模式',
      description: '模拟请求超时',
      scenarios: [
        {
          name: 'timeout-scenario',
          description: '请求超时',
          enabled: true,
          delay: {
            enabled: true,
            minMs: 30000,
            maxMs: 60000,
            timeout: true,
            timeoutMs: 30000
          }
        }
      ]
    })

    this.registerPreset({
      id: 'empty-data',
      name: '空数据模式',
      description: '所有列表返回空数据',
      scenarios: [
        {
          name: 'empty-lists',
          description: '空列表场景',
          enabled: true,
          empty: {
            returnEmptyArray: true,
            returnNullObject: true,
            returnZeroCount: true
          }
        }
      ]
    })

    this.registerPreset({
      id: 'unauthorized',
      name: '未授权模式',
      description: '模拟未登录状态',
      scenarios: [
        {
          name: 'unauthorized-access',
          description: '未授权访问',
          enabled: true,
          error: {
            code: 401,
            message: '未授权访问，请先登录'
          }
        }
      ]
    })

    this.registerPreset({
      id: 'mixed-errors',
      name: '混合错误模式',
      description: '不同资源返回不同错误',
      scenarios: [
        {
          name: 'service-error',
          description: '服务模块500错误',
          enabled: true,
          error: {
            code: 500,
            message: '服务模块异常'
          }
        },
        {
          name: 'order-not-found',
          description: '订单404错误',
          enabled: true,
          error: {
            code: 404,
            message: '订单不存在'
          }
        }
      ]
    })

    this.registerPreset({
      id: 'boundary-test',
      name: '边界测试模式',
      description: '测试边界情况',
      scenarios: [
        {
          name: 'max-values',
          description: '最大值边界',
          enabled: true,
          boundary: {
            page: 9999,
            pageSize: 100,
            maxValue: Number.MAX_SAFE_INTEGER,
            minValue: 0,
            stringLength: 10000
          }
        }
      ]
    })

    this.registerPreset({
      id: 'unstable',
      name: '不稳定网络',
      description: '随机延迟和错误',
      scenarios: [
        {
          name: 'random-delay',
          description: '随机延迟100ms-5s',
          enabled: true,
          delay: {
            enabled: true,
            minMs: 100,
            maxMs: 5000,
            timeout: false,
            timeoutMs: 0
          }
        }
      ]
    })

    this.registerPreset({
      id: 'stress-test',
      name: '压力测试模式',
      description: '极端条件测试',
      scenarios: [
        {
          name: 'extreme-delay',
          description: '极慢响应',
          enabled: true,
          delay: {
            enabled: true,
            minMs: 5000,
            maxMs: 10000,
            timeout: false,
            timeoutMs: 0
          }
        },
        {
          name: 'large-data',
          description: '大数据量',
          enabled: true,
          boundary: {
            page: 1,
            pageSize: 100,
            maxValue: 10000,
            minValue: 0,
            stringLength: 10000
          }
        }
      ]
    })
  }

  registerPreset(preset: ScenarioPreset): void {
    this.presets.set(preset.id, preset)
  }

  unregisterPreset(presetId: string): void {
    this.presets.delete(presetId)
  }

  getPreset(presetId: string): ScenarioPreset | undefined {
    return this.presets.get(presetId)
  }

  getAllPresets(): ScenarioPreset[] {
    return Array.from(this.presets.values())
  }

  applyPreset(presetId: string): boolean {
    const preset = this.presets.get(presetId)
    if (!preset) return false

    this.resetAllScenarios()

    preset.scenarios.forEach(scenario => {
      if (!scenario.enabled) return

      if (scenario.error) {
        errorGenerator.enableGlobalError('internal_error')
      }

      if (scenario.delay) {
        delaySimulator.enableGlobalDelay('slow')
      }

      if (scenario.empty) {
        emptyDataHandler.enableGlobalEmpty('empty_list')
      }

      if (scenario.boundary) {
        boundaryHandler.enableGlobalBoundary('max_value')
      }
    })

    this.activePreset = presetId
    return true
  }

  applyScenarioToResource(
    resource: ResourceType,
    action: string,
    scenario: ScenarioConfig
  ): void {
    const key = `${resource}:${action}`
    this.customScenarios.set(key, scenario)

    if (scenario.error) {
      const errorScenario: ErrorScenario = this.getErrorScenario(scenario.error.code)
      errorGenerator.setError(resource, action, errorScenario)
    }

    if (scenario.delay && scenario.delay.enabled) {
      const delayScenario: DelayScenario = this.getDelayScenario(scenario.delay.minMs)
      delaySimulator.setDelay(resource, action, delayScenario)
    }

    if (scenario.empty) {
      const emptyScenario: EmptyScenario = 'empty_list'
      emptyDataHandler.setEmpty(resource, action, emptyScenario)
    }

    if (scenario.boundary) {
      const boundaryScenario: BoundaryScenario = this.getBoundaryScenario(scenario.boundary)
      boundaryHandler.setBoundary(resource, action, boundaryScenario)
    }
  }

  private getErrorScenario(code: number): ErrorScenario {
    const mapping: Record<number, ErrorScenario> = {
      400: 'bad_request',
      401: 'unauthorized',
      403: 'forbidden',
      404: 'not_found',
      500: 'internal_error',
      502: 'bad_gateway',
      503: 'service_unavailable',
      504: 'gateway_timeout'
    }
    return mapping[code] || 'internal_error'
  }

  private getDelayScenario(minMs: number): DelayScenario {
    if (minMs === 0) return 'none'
    if (minMs <= 100) return 'slow'
    if (minMs <= 1000) return 'slow'
    if (minMs <= 3000) return 'very_slow'
    return 'timeout'
  }

  private getBoundaryScenario(config: NonNullable<ScenarioConfig['boundary']>): BoundaryScenario {
    if (config.page === 1) return 'first_page'
    if (config.page >= 9999) return 'out_of_range_page'
    if (config.pageSize === 0) return 'zero_value'
    if (config.maxValue === Number.MAX_SAFE_INTEGER) return 'max_value'
    if (config.minValue === Number.MIN_SAFE_INTEGER) return 'min_value'
    return 'first_page'
  }

  resetAllScenarios(): void {
    errorGenerator.clearAllErrors()
    delaySimulator.clearAllDelays()
    emptyDataHandler.clearAllEmpty()
    boundaryHandler.clearAllBoundaries()
    this.customScenarios.clear()
    this.activePreset = null
  }

  getActivePreset(): string | null {
    return this.activePreset
  }

  getActiveScenarios(): Map<string, ScenarioConfig> {
    return new Map(this.customScenarios)
  }

  exportConfig(): {
    activePreset: string | null
    customScenarios: Record<string, ScenarioConfig>
  } {
    return {
      activePreset: this.activePreset,
      customScenarios: Object.fromEntries(this.customScenarios)
    }
  }

  importConfig(config: {
    activePreset?: string | null
    customScenarios?: Record<string, ScenarioConfig>
  }): void {
    if (config.activePreset) {
      this.applyPreset(config.activePreset)
    }

    if (config.customScenarios) {
      Object.entries(config.customScenarios).forEach(([key, scenario]) => {
        const [resource, action] = key.split(':') as [ResourceType, string]
        this.applyScenarioToResource(resource, action, scenario)
      })
    }
  }
}

export const scenarioConfigManager = ScenarioConfigManager.getInstance()

export const applyPreset = (presetId: string): boolean => {
  return scenarioConfigManager.applyPreset(presetId)
}

export const resetScenarios = (): void => {
  scenarioConfigManager.resetAllScenarios()
}

export const getPresets = (): ScenarioPreset[] => {
  return scenarioConfigManager.getAllPresets()
}

export const PRESET_DESCRIPTIONS: Record<string, { name: string; description: string }> = {
  normal: {
    name: '正常模式',
    description: '所有功能正常运行，无异常'
  },
  'error-all': {
    name: '全局错误模式',
    description: '所有请求返回500错误'
  },
  'slow-network': {
    name: '慢速网络',
    description: '模拟慢速网络环境，响应延迟2-5秒'
  },
  timeout: {
    name: '超时模式',
    description: '模拟请求超时'
  },
  'empty-data': {
    name: '空数据模式',
    description: '所有列表返回空数据'
  },
  unauthorized: {
    name: '未授权模式',
    description: '模拟未登录状态'
  },
  'mixed-errors': {
    name: '混合错误模式',
    description: '不同资源返回不同错误'
  },
  'boundary-test': {
    name: '边界测试模式',
    description: '测试边界情况'
  },
  unstable: {
    name: '不稳定网络',
    description: '随机延迟和错误'
  },
  'stress-test': {
    name: '压力测试模式',
    description: '极端条件测试'
  }
}
