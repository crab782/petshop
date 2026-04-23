import { vi, beforeEach, afterEach, afterAll } from 'vitest'
import { config } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import ElementPlus from 'element-plus'
import { ElLoading } from 'element-plus'

vi.stubEnv('VITE_API_BASE_URL', '/api')

config.global.plugins = []

const vLoading = {
  mounted: () => {},
  updated: () => {},
  unmounted: () => {}
}

config.global.directives = {
  loading: vLoading
}

const localStorageMock = (() => {
  let store: Record<string, string> = {}
  return {
    getItem: vi.fn((key: string) => store[key] || null),
    setItem: vi.fn((key: string, value: string) => {
      store[key] = value
    }),
    removeItem: vi.fn((key: string) => {
      delete store[key]
    }),
    clear: vi.fn(() => {
      store = {}
    }),
    get length() {
      return Object.keys(store).length
    },
    key: vi.fn((index: number) => Object.keys(store)[index] || null),
  }
})()

const sessionStorageMock = (() => {
  let store: Record<string, string> = {}
  return {
    getItem: vi.fn((key: string) => store[key] || null),
    setItem: vi.fn((key: string, value: string) => {
      store[key] = value
    }),
    removeItem: vi.fn((key: string) => {
      delete store[key]
    }),
    clear: vi.fn(() => {
      store = {}
    }),
    get length() {
      return Object.keys(store).length
    },
    key: vi.fn((index: number) => Object.keys(store)[index] || null),
  }
})()

Object.defineProperty(window, 'localStorage', { value: localStorageMock })
Object.defineProperty(window, 'sessionStorage', { value: sessionStorageMock })

Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation((query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})

class ResizeObserverMock {
  observe = vi.fn()
  unobserve = vi.fn()
  disconnect = vi.fn()
}
window.ResizeObserver = ResizeObserverMock

class IntersectionObserverMock {
  observe = vi.fn()
  unobserve = vi.fn()
  disconnect = vi.fn()
  root = null
  rootMargin = ''
  thresholds = []
}
window.IntersectionObserver = IntersectionObserverMock as unknown as typeof IntersectionObserver

HTMLDialogElement.prototype.show = vi.fn()
HTMLDialogElement.prototype.showModal = vi.fn()
HTMLDialogElement.prototype.close = vi.fn()

beforeEach(() => {
  setActivePinia(createPinia())
  localStorageMock.clear()
  sessionStorageMock.clear()
  vi.clearAllMocks()
  vi.clearAllTimers()
})

afterEach(async () => {
  vi.restoreAllMocks()
  vi.useRealTimers()
  await new Promise<void>((resolve) => {
    setTimeout(resolve, 0)
  })
})

afterAll(async () => {
  vi.clearAllTimers()
  vi.useRealTimers()
  await new Promise<void>((resolve) => {
    setTimeout(resolve, 0)
  })
})

vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      warning: vi.fn(),
      info: vi.fn(),
      error: vi.fn(),
    },
    ElMessageBox: {
      confirm: vi.fn(() => Promise.resolve()),
      alert: vi.fn(() => Promise.resolve()),
      prompt: vi.fn(() => Promise.resolve({ value: 'test' })),
    },
    ElNotification: {
      success: vi.fn(),
      warning: vi.fn(),
      info: vi.fn(),
      error: vi.fn(),
    },
    ElLoading: {
      service: vi.fn(() => ({
        close: vi.fn(),
      })),
    },
  }
})

vi.mock('@element-plus/icons-vue', () => {
  const icons = [
    'Promotion', 'User', 'Goods', 'ShoppingCart', 'Document', 'Setting',
    'House', 'Calendar', 'ChatDotRound', 'Star', 'Location', 'Phone',
    'Message', 'Bell', 'Search', 'Plus', 'Edit', 'Delete', 'View',
    'ArrowLeft', 'ArrowRight', 'Check', 'Close', 'Warning', 'InfoFilled',
    'SuccessFilled', 'CircleCheck', 'CircleClose', 'More', 'Refresh',
    'Download', 'Upload', 'Filter', 'Sort', 'Grid', 'List', 'Picture',
    'VideoPlay', 'Microphone', 'Headset', 'Monitor', 'Cellphone',
    'LocationInformation', 'Timer', 'AlarmClock', 'Watch', 'Lock',
    'Unlock', 'Key', 'Fingerprint', 'IdCard', 'Postcard', 'Message',
    'ChatLineRound', 'ChatRound', 'ChatSquare', 'Service', 'PhoneFilled',
    'PhoneOutline', 'Link', 'Paperclip', 'Connection', 'Opportunity',
    'Coin', 'Wallet', 'CreditCard', 'Money', 'ShoppingBag', 'ShoppingCartFull',
    'Box', 'Files', 'DocumentCopy', 'Folder', 'FolderOpened', 'FolderAdd',
    'FolderRemove', 'FolderChecked', 'FolderDelete', 'DataLine', 'DataBoard',
    'PieChart', 'Histogram', 'TrendCharts', 'DataAnalysis', 'CirclePieChart',
    'Compass', 'Position', 'MapLocation', 'Aim', 'Guide', 'LocationFilled',
    'AddLocation', 'DeleteLocation', 'Place', 'School', 'OfficeBuilding',
    'ToiletPaper', 'CaretRight', 'CaretLeft', 'CaretTop', 'CaretBottom',
    'DCaret', 'SortDown', 'SortUp', 'Rank', 'Expand', 'Fold', 'Operation',
    'Menu', 'FullScreen', 'ExitFullScreen', 'ZoomIn', 'ZoomOut', 'FullScreenExpand',
    'FullScreenShrink', 'SwitchButton', 'TurnOff', 'Open', 'Setup',
    'Tools', 'Hide', 'Show', 'ViewHidden', 'Visible', 'Invisible',
    'Loading', 'LoadingFilled', 'CircleCheckFilled', 'CircleCloseFilled',
    'WarningFilled', 'QuestionFilled', 'InfoFilled', 'RemoveFilled',
    'AddFilled', 'PositionFilled', 'StarFilled', 'GoodsFilled', 'MessageFilled',
    'BellFilled', 'CalendarFilled', 'CameraFilled', 'CollectionFilled',
    'EditFilled', 'FlagFilled', 'HomeFilled', 'MenuFilled', 'MicFilled',
    'PictureFilled', 'PromotionFilled', 'SettingFilled', 'ShopFilled',
    'SuitcaseFilled', 'TicketFilled', 'TrendChartsFilled', 'UserFilled',
    'VideoCameraFilled', 'WalletFilled', 'WarnTriangleFilled', 'WindPower',
    'Sunny', 'Sunrise', 'Sunset', 'Moon', 'Cloudy', 'CloudyAndSun',
    'PartlyCloudy', 'SunrisePng', 'Drizzling', 'HeavyRain', 'Lightning',
    'LightningFilled', 'Mist', 'Snowflake', 'Sleet', 'Hail', 'Smog',
    'Goblet', 'GobletFull', 'GobletSquare', 'GobletSquareFull', 'Refrigerator',
    'Grape', 'Watermelon', 'Cherry', 'Apple', 'Pear', 'Orange', 'Coffee',
    'IceCream', 'IceCreamRound', 'IceCreamSquare', 'Lollipop', 'PotatoStrips',
    'Chicken', 'Drumstick', 'Burger', 'KnifeFork', 'Sugar', 'Dessert',
    'IceDrink', 'HotDrink', 'CoffeeCup', 'TakeawayBox', 'Dish', 'DishDot',
    'Food', 'FoodFilled', 'NoSmoking', 'Smoking', 'Trophy', 'Medal',
    'MedalFilled', 'FirstAidKit', 'Basketball', 'Football', 'Volleyball',
    'Tennis', 'Baseball', 'TableTennis', 'Badminton', 'Bowling', 'Bicycle',
    'Van', 'Truck', 'CarFilled', 'Car', 'Ship', 'ShipFilled', 'Plane',
    'PlaneFilled', 'Rocket', 'RocketFilled', 'RocketLaunch', 'RocketLaunchFilled',
    'BicycleFilled', 'Train', 'TrainFilled', 'Bus', 'BusFilled', 'Taxi',
    'TaxiFilled', 'Motorcycle', 'MotorcycleFilled', 'Cruiser', 'CruiserFilled',
    'Sailboat', 'SailboatFilled', 'Helicopter', 'HelicopterFilled', 'Scooter',
    'ScooterFilled', 'Ebike', 'EbikeFilled', 'ShipLine', 'ShipLineFilled',
    'TrainLine', 'TrainLineFilled', 'BusLine', 'BusLineFilled', 'TaxiLine',
    'TaxiLineFilled', 'CarLine', 'CarLineFilled', 'VanLine', 'VanLineFilled',
    'TruckLine', 'TruckLineFilled', 'BicycleLine', 'BicycleLineFilled',
    'MotorcycleLine', 'MotorcycleLineFilled', 'ShipFilledLine', 'ShipFilledLineFilled',
    'PlaneFilledLine', 'PlaneFilledLineFilled', 'RocketFilledLine', 'RocketFilledLineFilled',
    'RocketLaunchFilledLine', 'RocketLaunchFilledLineFilled', 'BicycleFilledLine',
    'BicycleFilledLineFilled', 'TrainFilledLine', 'TrainFilledLineFilled',
    'BusFilledLine', 'BusFilledLineFilled', 'TaxiFilledLine', 'TaxiFilledLineFilled',
    'MotorcycleFilledLine', 'MotorcycleFilledLineFilled', 'CruiserFilledLine',
    'CruiserFilledLineFilled', 'SailboatFilledLine', 'SailboatFilledLineFilled',
    'HelicopterFilledLine', 'HelicopterFilledLineFilled', 'ScooterFilledLine',
    'ScooterFilledLineFilled', 'EbikeFilledLine', 'EbikeFilledLineFilled',
  ]

  const mockComponents: any = {}
  icons.forEach(icon => {
    mockComponents[icon] = {
      name: icon,
      template: `<svg class="icon-${icon.toLowerCase()}" />`,
    }
  })

  return mockComponents
})

vi.mock('vue-router', async () => {
  const actual = await vi.importActual('vue-router')
  return {
    ...actual,
    useRouter: () => ({
      push: vi.fn(),
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
          meta: {},
        },
      },
    }),
    useRoute: () => ({
      path: '/',
      params: {},
      query: {},
      name: undefined,
      meta: {},
    }),
  }
})

global.ResizeObserver = ResizeObserverMock
global.IntersectionObserver = IntersectionObserverMock as unknown as typeof IntersectionObserver

export { localStorageMock, sessionStorageMock }
