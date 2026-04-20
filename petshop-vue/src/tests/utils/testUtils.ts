import { mount, VueWrapper } from '@vue/test-utils'
import type { Component, ComponentOptions } from 'vue'
import { vi } from 'vitest'

export interface MountOptions {
  global?: {
    components?: Record<string, Component>
    plugins?: Array<{ install: (...args: any[]) => void }>
    mocks?: Record<string, any>
    provide?: Record<string, any>
    stubs?: Record<string, boolean | Component>
  }
  props?: Record<string, any>
  slots?: Record<string, any>
  attachTo?: string | Element
}

export const createWrapper = <T extends Component>(
  component: T,
  options: MountOptions = {}
): VueWrapper<T> => {
  return mount(component, {
    ...options,
    global: {
      ...options.global,
      stubs: options.global?.stubs || {},
    },
  })
}

export const createComponentWrapper = <T extends Component>(
  component: T,
  options: MountOptions = {}
): VueWrapper<T> => {
  return createWrapper(component, options)
}

export const waitFor = (ms: number): Promise<void> => {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

export const flushPromises = (): Promise<void> => {
  return new Promise((resolve) => setTimeout(resolve, 0))
}

export const mockRouterPush = () => {
  return vi.fn()
}

export const createMockRouter = () => ({
  push: mockRouterPush(),
  replace: vi.fn(),
  go: vi.fn(),
  back: vi.fn(),
  forward: vi.fn(),
  currentRoute: {
    value: {
      path: '/',
      params: {},
      query: {},
      name: undefined,
    },
  },
})

export const createMockStore = <T extends Record<string, any>>(initialState: T) => {
  const state = { ...initialState }
  return {
    state,
    setState: <K extends keyof T>(key: K, value: T[K]) => {
      state[key] = value
    },
    getState: <K extends keyof T>(key: K): T[K] => state[key],
    reset: () => {
      Object.assign(state, initialState)
    },
  }
}

export const mockLocalStorage = () => {
  const store: Record<string, string> = {}
  return {
    getItem: vi.fn((key: string) => store[key] || null),
    setItem: vi.fn((key: string, value: string) => {
      store[key] = value
    }),
    removeItem: vi.fn((key: string) => {
      delete store[key]
    }),
    clear: vi.fn(() => {
      Object.keys(store).forEach((key) => delete store[key])
    }),
    get store() {
      return { ...store }
    },
  }
}

export const mockSessionStorage = () => {
  const store: Record<string, string> = {}
  return {
    getItem: vi.fn((key: string) => store[key] || null),
    setItem: vi.fn((key: string, value: string) => {
      store[key] = value
    }),
    removeItem: vi.fn((key: string) => {
      delete store[key]
    }),
    clear: vi.fn(() => {
      Object.keys(store).forEach((key) => delete store[key])
    }),
    get store() {
      return { ...store }
    },
  }
}

export const mockConsole = () => {
  const originalConsole = { ...console }
  return {
    log: vi.spyOn(console, 'log').mockImplementation(() => {}),
    warn: vi.spyOn(console, 'warn').mockImplementation(() => {}),
    error: vi.spyOn(console, 'error').mockImplementation(() => {}),
    info: vi.spyOn(console, 'info').mockImplementation(() => {}),
    restore: () => {
      Object.assign(console, originalConsole)
    },
  }
}

export const mockWindowLocation = () => {
  const originalLocation = window.location
  delete (window as any).location
  window.location = {
    ...originalLocation,
    href: '',
    assign: vi.fn(),
    reload: vi.fn(),
    replace: vi.fn(),
  } as Location
  return {
    restore: () => {
      window.location = originalLocation
    },
  }
}

export const createMockApi = <T extends (...args: any[]) => any>(
  implementation?: T
) => {
  return vi.fn(implementation)
}

export const createMockResponse = <T>(data: T, delay = 0) => {
  return new Promise<T>((resolve) => {
    setTimeout(() => resolve(data), delay)
  })
}

export const createMockErrorResponse = (message: string, code = 400, delay = 0) => {
  return new Promise((_, reject) => {
    setTimeout(() => reject({ code, message }), delay)
  })
}

export const generateTestId = (prefix = 'test'): string => {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`
}

export const createTestElement = (tagName: string, attributes: Record<string, string> = {}): HTMLElement => {
  const element = document.createElement(tagName)
  Object.entries(attributes).forEach(([key, value]) => {
    element.setAttribute(key, value)
  })
  return element
}

export const triggerEvent = async (
  element: Element | VueWrapper,
  eventName: string,
  eventData?: Record<string, any>
) => {
  const el = 'element' in element ? element.element : element
  const event = new Event(eventName, { bubbles: true, cancelable: true })
  Object.assign(event, eventData || {})
  el.dispatchEvent(event)
  await flushPromises()
}

export const setInputValue = async (input: HTMLInputElement | HTMLTextAreaElement, value: string) => {
  input.value = value
  input.dispatchEvent(new Event('input', { bubbles: true }))
  input.dispatchEvent(new Event('change', { bubbles: true }))
  await flushPromises()
}

export const selectOption = async (select: HTMLSelectElement, value: string) => {
  select.value = value
  select.dispatchEvent(new Event('change', { bubbles: true }))
  await flushPromises()
}

export const checkCheckbox = async (checkbox: HTMLInputElement, checked: boolean) => {
  checkbox.checked = checked
  checkbox.dispatchEvent(new Event('change', { bubbles: true }))
  await flushPromises()
}

export const getElementBySelector = <T extends Element>(
  container: Element | Document,
  selector: string
): T | null => {
  return container.querySelector<T>(selector)
}

export const getElementsBySelector = <T extends Element>(
  container: Element | Document,
  selector: string
): NodeListOf<T> => {
  return container.querySelectorAll<T>(selector)
}

export const hasClass = (element: Element, className: string): boolean => {
  return element.classList.contains(className)
}

export const hasClasses = (element: Element, classNames: string[]): boolean => {
  return classNames.every((className) => element.classList.contains(className))
}

export const getStyle = (element: Element, property: string): string => {
  return window.getComputedStyle(element).getPropertyValue(property)
}

export const isElementVisible = (element: Element): boolean => {
  const style = window.getComputedStyle(element)
  return style.display !== 'none' && style.visibility !== 'hidden' && style.opacity !== '0'
}

export const mockIntersectionObserver = () => {
  const instances: IntersectionObserver[] = []
  const mock = vi.fn((callback: IntersectionObserverCallback, options?: IntersectionObserverInit) => {
    const instance: IntersectionObserver = {
      root: options?.root || null,
      rootMargin: options?.rootMargin || '0px',
      thresholds: Array.isArray(options?.threshold) ? options.threshold : [options?.threshold || 0],
      observe: vi.fn(),
      unobserve: vi.fn(),
      disconnect: vi.fn(),
      takeRecords: vi.fn(() => []),
    }
    instances.push(instance)
    return instance
  })
  window.IntersectionObserver = mock as any
  return { mock, instances }
}

export const mockResizeObserver = () => {
  const instances: ResizeObserver[] = []
  const mock = vi.fn((callback: ResizeObserverCallback) => {
    const instance: ResizeObserver = {
      observe: vi.fn(),
      unobserve: vi.fn(),
      disconnect: vi.fn(),
    }
    instances.push(instance)
    return instance
  })
  window.ResizeObserver = mock as any
  return { mock, instances }
}

export const mockMatchMedia = (matches = false) => {
  const listeners: Array<(e: MediaQueryListEvent) => void> = []
  const mqList: MediaQueryList = {
    matches,
    media: '',
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn((_type: string, listener: (e: MediaQueryListEvent) => void) => {
      listeners.push(listener)
    }),
    removeEventListener: vi.fn((_type: string, listener: (e: MediaQueryListEvent) => void) => {
      const index = listeners.indexOf(listener)
      if (index > -1) listeners.splice(index, 1)
    }),
    dispatchEvent: vi.fn(),
  }
  window.matchMedia = vi.fn(() => mqList)
  return {
    mqList,
    triggerChange: (newMatches: boolean) => {
      const event = { matches: newMatches } as MediaQueryListEvent
      listeners.forEach((listener) => listener(event))
    },
  }
}

export const setupTestEnvironment = () => {
  const localStorageMock = mockLocalStorage()
  const sessionStorageMock = mockSessionStorage()
  const consoleMock = mockConsole()

  Object.defineProperty(window, 'localStorage', { value: localStorageMock })
  Object.defineProperty(window, 'sessionStorage', { value: sessionStorageMock })

  return {
    localStorage: localStorageMock,
    sessionStorage: sessionStorageMock,
    console: consoleMock,
    cleanup: () => {
      localStorageMock.clear()
      sessionStorageMock.clear()
      consoleMock.restore()
    },
  }
}

export const createDataProvider = <T extends Record<string, any>>() => {
  const data: T[] = []
  return {
    add: (item: T) => data.push(item),
    getAll: () => [...data],
    clear: () => (data.length = 0),
    findBy: <K extends keyof T>(key: K, value: T[K]): T | undefined =>
      data.find((item) => item[key] === value),
    filterBy: <K extends keyof T>(key: K, value: T[K]): T[] =>
      data.filter((item) => item[key] === value),
  }
}
