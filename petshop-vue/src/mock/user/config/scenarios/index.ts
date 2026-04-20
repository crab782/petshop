import type {
  ErrorScenario,
  DelayScenario,
  EmptyScenario,
  BoundaryScenario,
  ResourceType,
  ScenarioConfig,
  ScenarioPreset,
  ErrorResponse,
  DelayOptions,
  BoundaryConfig
} from './types'

export * from './types'
export * from './errors'
export * from './delay'
export * from './empty'
export * from './boundary'
export * from './config'

import { errorGenerator, createErrorResponse } from './errors'
import { delaySimulator, applyDelay, DELAY_PRESETS, simulateNetworkConditions } from './delay'
import { emptyDataHandler, generateEmptyList, generateEmptyDetail } from './empty'
import { boundaryHandler, handlePagination, BOUNDARY_TEST_DATA } from './boundary'
import { scenarioConfigManager, applyPreset, resetScenarios, getPresets } from './config'

class ScenarioService {
  private static instance: ScenarioService

  static getInstance(): ScenarioService {
    if (!ScenarioService.instance) {
      ScenarioService.instance = new ScenarioService()
    }
    return ScenarioService.instance
  }

  enableError(scenario: ErrorScenario, resource?: ResourceType, action?: string): void {
    if (resource && action) {
      errorGenerator.setError(resource, action, scenario)
    } else {
      errorGenerator.enableGlobalError(scenario)
    }
  }

  disableError(resource?: ResourceType, action?: string): void {
    if (resource && action) {
      errorGenerator.clearError(resource, action)
    } else {
      errorGenerator.disableGlobalError()
    }
  }

  enableDelay(scenario: DelayScenario, resource?: ResourceType, action?: string): void {
    if (resource && action) {
      delaySimulator.setDelay(resource, action, scenario)
    } else {
      delaySimulator.enableGlobalDelay(scenario)
    }
  }

  setCustomDelay(resource: ResourceType, action: string, options: DelayOptions): void {
    delaySimulator.setCustomDelay(resource, action, options)
  }

  disableDelay(resource?: ResourceType, action?: string): void {
    if (resource && action) {
      delaySimulator.clearDelay(resource, action)
    } else {
      delaySimulator.disableGlobalDelay()
    }
  }

  enableEmptyData(scenario: EmptyScenario, resource?: ResourceType, action?: string): void {
    if (resource && action) {
      emptyDataHandler.setEmpty(resource, action, scenario)
    } else {
      emptyDataHandler.enableGlobalEmpty(scenario)
    }
  }

  disableEmptyData(resource?: ResourceType, action?: string): void {
    if (resource && action) {
      emptyDataHandler.clearEmpty(resource, action)
    } else {
      emptyDataHandler.disableGlobalEmpty()
    }
  }

  enableBoundary(scenario: BoundaryScenario, resource?: ResourceType, action?: string): void {
    if (resource && action) {
      boundaryHandler.setBoundary(resource, action, scenario)
    } else {
      boundaryHandler.enableGlobalBoundary(scenario)
    }
  }

  disableBoundary(resource?: ResourceType, action?: string): void {
    if (resource && action) {
      boundaryHandler.clearBoundary(resource, action)
    } else {
      boundaryHandler.disableGlobalBoundary()
    }
  }

  applyPreset(presetId: string): boolean {
    return scenarioConfigManager.applyPreset(presetId)
  }

  reset(): void {
    scenarioConfigManager.resetAllScenarios()
  }

  getPresets(): ScenarioPreset[] {
    return scenarioConfigManager.getAllPresets()
  }

  shouldReturnError(resource: ResourceType, action: string): boolean {
    return errorGenerator.shouldReturnError(resource, action)
  }

  getErrorResponse(resource: ResourceType, action: string, path: string): ErrorResponse | null {
    const scenario = errorGenerator.getErrorScenario(resource, action)
    if (scenario) {
      return errorGenerator.generateError(scenario, path)
    }
    return null
  }

  async applyDelayIfEnabled(resource: ResourceType, action: string): Promise<void> {
    await applyDelay(resource, action)
  }

  shouldReturnEmpty(resource: ResourceType, action: string): boolean {
    return emptyDataHandler.shouldReturnEmpty(resource, action)
  }

  shouldApplyBoundary(resource: ResourceType, action: string): boolean {
    return boundaryHandler.shouldApplyBoundary(resource, action)
  }

  exportConfig(): ReturnType<typeof scenarioConfigManager.exportConfig> {
    return scenarioConfigManager.exportConfig()
  }

  importConfig(config: Parameters<typeof scenarioConfigManager.importConfig>[0]): void {
    scenarioConfigManager.importConfig(config)
  }
}

export const scenarioService = ScenarioService.getInstance()

export const enableError = (scenario: ErrorScenario, resource?: ResourceType, action?: string): void => {
  scenarioService.enableError(scenario, resource, action)
}

export const disableError = (resource?: ResourceType, action?: string): void => {
  scenarioService.disableError(resource, action)
}

export const enableDelay = (scenario: DelayScenario, resource?: ResourceType, action?: string): void => {
  scenarioService.enableDelay(scenario, resource, action)
}

export const disableDelay = (resource?: ResourceType, action?: string): void => {
  scenarioService.disableDelay(resource, action)
}

export const enableEmptyData = (scenario: EmptyScenario, resource?: ResourceType, action?: string): void => {
  scenarioService.enableEmptyData(scenario, resource, action)
}

export const disableEmptyData = (resource?: ResourceType, action?: string): void => {
  scenarioService.disableEmptyData(resource, action)
}

export const enableBoundary = (scenario: BoundaryScenario, resource?: ResourceType, action?: string): void => {
  scenarioService.enableBoundary(scenario, resource, action)
}

export const disableBoundary = (resource?: ResourceType, action?: string): void => {
  scenarioService.disableBoundary(resource, action)
}

export {
  applyPreset,
  resetScenarios,
  getPresets,
  createErrorResponse,
  applyDelay,
  generateEmptyList,
  generateEmptyDetail,
  handlePagination,
  DELAY_PRESETS,
  simulateNetworkConditions,
  BOUNDARY_TEST_DATA
}

export default scenarioService
