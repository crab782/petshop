import type { DelayScenario, DelayConfig, ResourceType } from './types'
import { DELAY_SCENARIOS } from './types'

export interface DelayOptions {
  minMs?: number
  maxMs?: number
  timeout?: boolean
  timeoutMs?: number
}

export class DelaySimulator {
  private static instance: DelaySimulator
  private activeDelays: Map<string, DelayScenario> = new Map()
  private globalDelayEnabled: boolean = false
  private globalDelayScenario: DelayScenario = 'none'
  private customDelays: Map<string, DelayOptions> = new Map()

  static getInstance(): DelaySimulator {
    if (!DelaySimulator.instance) {
      DelaySimulator.instance = new DelaySimulator()
    }
    return DelaySimulator.instance
  }

  enableGlobalDelay(scenario: DelayScenario): void {
    this.globalDelayEnabled = true
    this.globalDelayScenario = scenario
  }

  disableGlobalDelay(): void {
    this.globalDelayEnabled = false
    this.globalDelayScenario = 'none'
  }

  setDelay(resource: ResourceType, action: string, scenario: DelayScenario): void {
    const key = `${resource}:${action}`
    this.activeDelays.set(key, scenario)
  }

  setCustomDelay(resource: ResourceType, action: string, options: DelayOptions): void {
    const key = `${resource}:${action}`
    this.customDelays.set(key, options)
  }

  clearDelay(resource: ResourceType, action: string): void {
    const key = `${resource}:${action}`
    this.activeDelays.delete(key)
    this.customDelays.delete(key)
  }

  clearAllDelays(): void {
    this.activeDelays.clear()
    this.customDelays.clear()
    this.globalDelayEnabled = false
    this.globalDelayScenario = 'none'
  }

  shouldApplyDelay(resource: ResourceType, action: string): boolean {
    if (this.globalDelayEnabled && this.globalDelayScenario !== 'none') {
      return true
    }
    const key = `${resource}:${action}`
    return this.activeDelays.has(key) || this.customDelays.has(key)
  }

  getDelayConfig(resource: ResourceType, action: string): DelayConfig | null {
    if (this.globalDelayEnabled && this.globalDelayScenario !== 'none') {
      return DELAY_SCENARIOS[this.globalDelayScenario]
    }

    const key = `${resource}:${action}`

    const customOptions = this.customDelays.get(key)
    if (customOptions) {
      return {
        enabled: true,
        minMs: customOptions.minMs || 0,
        maxMs: customOptions.maxMs || customOptions.minMs || 0,
        timeout: customOptions.timeout || false,
        timeoutMs: customOptions.timeoutMs || 30000
      }
    }

    const scenario = this.activeDelays.get(key)
    if (scenario) {
      return DELAY_SCENARIOS[scenario]
    }

    return null
  }

  async applyDelay(resource: ResourceType, action: string): Promise<void> {
    const config = this.getDelayConfig(resource, action)

    if (!config || !config.enabled) {
      return
    }

    if (config.timeout) {
      await this.simulateTimeout(config.timeoutMs)
      return
    }

    const delayMs = this.getRandomDelay(config.minMs, config.maxMs)
    await this.sleep(delayMs)
  }

  private sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms))
  }

  private getRandomDelay(min: number, max: number): number {
    if (min === max) return min
    return Math.floor(Math.random() * (max - min + 1)) + min
  }

  private async simulateTimeout(timeoutMs: number): Promise<never> {
    await this.sleep(timeoutMs)
    throw new Error('Request timeout')
  }

  getDelayInfo(resource: ResourceType, action: string): {
    willDelay: boolean
    estimatedMs: number
    willTimeout: boolean
  } {
    const config = this.getDelayConfig(resource, action)

    if (!config || !config.enabled) {
      return {
        willDelay: false,
        estimatedMs: 0,
        willTimeout: false
      }
    }

    return {
      willDelay: true,
      estimatedMs: Math.floor((config.minMs + config.maxMs) / 2),
      willTimeout: config.timeout
    }
  }
}

export const delaySimulator = DelaySimulator.getInstance()

export const applyDelay = async (resource: ResourceType, action: string): Promise<void> => {
  return delaySimulator.applyDelay(resource, action)
}

export const setDelay = (
  resource: ResourceType,
  action: string,
  scenario: DelayScenario
): void => {
  delaySimulator.setDelay(resource, action, scenario)
}

export const setCustomDelay = (
  resource: ResourceType,
  action: string,
  options: DelayOptions
): void => {
  delaySimulator.setCustomDelay(resource, action, options)
}

export const DELAY_PRESETS: Record<string, DelayOptions> = {
  instant: {
    minMs: 0,
    maxMs: 50
  },
  fast: {
    minMs: 100,
    maxMs: 300
  },
  normal: {
    minMs: 300,
    maxMs: 800
  },
  slow: {
    minMs: 1000,
    maxMs: 2000
  },
  verySlow: {
    minMs: 3000,
    maxMs: 5000
  },
  extremelySlow: {
    minMs: 8000,
    maxMs: 15000
  },
  timeout: {
    minMs: 30000,
    maxMs: 60000,
    timeout: true,
    timeoutMs: 30000
  },
  random: {
    minMs: 100,
    maxMs: 5000
  },
  unstable: {
    minMs: 100,
    maxMs: 10000
  }
}

export const withDelay = async <T>(
  resource: ResourceType,
  action: string,
  callback: () => T | Promise<T>
): Promise<T> => {
  await applyDelay(resource, action)
  return callback()
}

export const simulateNetworkConditions = {
  offline: (): void => {
    delaySimulator.enableGlobalDelay('timeout')
  },

  slow3g: (): void => {
    delaySimulator.setCustomDelay('service' as ResourceType, 'list', {
      minMs: 2000,
      maxMs: 4000
    })
    delaySimulator.setCustomDelay('product' as ResourceType, 'list', {
      minMs: 2000,
      maxMs: 4000
    })
  },

  fast3g: (): void => {
    delaySimulator.setCustomDelay('service' as ResourceType, 'list', {
      minMs: 500,
      maxMs: 1500
    })
    delaySimulator.setCustomDelay('product' as ResourceType, 'list', {
      minMs: 500,
      maxMs: 1500
    })
  },

  wifi: (): void => {
    delaySimulator.setCustomDelay('service' as ResourceType, 'list', {
      minMs: 50,
      maxMs: 200
    })
    delaySimulator.setCustomDelay('product' as ResourceType, 'list', {
      minMs: 50,
      maxMs: 200
    })
  },

  reset: (): void => {
    delaySimulator.clearAllDelays()
  }
}
