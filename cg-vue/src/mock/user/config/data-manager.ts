import type {
  GeneratedData,
  UserData,
  PetData,
  MerchantData,
  ServiceData,
  ProductData,
  AppointmentData,
  OrderData,
  ReviewData
} from '../utils/generators'
import {
  generateAllData,
  generateUsers,
  generatePets,
  generateMerchants,
  generateServices,
  generateProducts,
  generateAppointments,
  generateOrders,
  generateReviews
} from '../utils/generators'
import type { DataOverrideConfig, DataGeneratorConfig } from './mock-config'

type DataKey = keyof GeneratedData
type DataItem<K extends DataKey> = GeneratedData[K] extends (infer T)[] ? T : never

export interface DataManagerState {
  data: GeneratedData | null
  overrides: Map<string, Map<number, Partial<DataItem<DataKey>>>>
  additions: Map<string, DataItem<DataKey>[]>
  deletions: Map<string, Set<number>>
  isDirty: boolean
}

export interface DataQueryOptions {
  page?: number
  pageSize?: number
  filters?: Record<string, unknown>
  sort?: { field: string; order: 'asc' | 'desc' }
}

export interface DataQueryResult<T> {
  data: T[]
  total: number
  page: number
  pageSize: number
}

class MockDataManager {
  private static instance: MockDataManager
  private state: DataManagerState = {
    data: null,
    overrides: new Map(),
    additions: new Map(),
    deletions: new Map(),
    isDirty: false
  }
  private listeners: Set<(state: DataManagerState) => void> = new Set()

  static getInstance(): MockDataManager {
    if (!MockDataManager.instance) {
      MockDataManager.instance = new MockDataManager()
    }
    return MockDataManager.instance
  }

  initialize(config: DataGeneratorConfig): GeneratedData {
    if (this.state.data && !this.state.isDirty) {
      return this.state.data
    }

    this.state.data = generateAllData(config)
    this.state.isDirty = false
    this.notifyListeners()

    return this.state.data
  }

  getData(): GeneratedData {
    if (!this.state.data) {
      this.state.data = generateAllData({})
    }
    return this.applyModifications(this.state.data)
  }

  getResource<K extends DataKey>(resource: K): GeneratedData[K] {
    const data = this.getData()
    let result = [...data[resource]] as GeneratedData[K]

    const deletions = this.state.deletions.get(resource)
    if (deletions) {
      result = result.filter(item => !deletions.has((item as { id: number }).id)) as GeneratedData[K]
    }

    const additions = this.state.additions.get(resource)
    if (additions) {
      result = [...result, ...additions] as GeneratedData[K]
    }

    return result
  }

  getItem<K extends DataKey>(resource: K, id: number): DataItem<K> | undefined {
    const items = this.getResource(resource)
    return items.find(item => (item as { id: number }).id === id) as DataItem<K> | undefined
  }

  setItem<K extends DataKey>(resource: K, id: number, updates: Partial<DataItem<K>>): boolean {
    if (!this.state.overrides.has(resource)) {
      this.state.overrides.set(resource, new Map())
    }

    const resourceOverrides = this.state.overrides.get(resource)!
    const existingOverrides = resourceOverrides.get(id) || {}
    resourceOverrides.set(id, { ...existingOverrides, ...updates })

    this.state.isDirty = true
    this.notifyListeners()

    return true
  }

  addItem<K extends DataKey>(resource: K, item: DataItem<K>): DataItem<K> {
    if (!this.state.additions.has(resource)) {
      this.state.additions.set(resource, [])
    }

    const additions = this.state.additions.get(resource)!
    const maxId = this.getMaxId(resource)
    const newItem = { ...item, id: maxId + 1 } as DataItem<K>

    additions.push(newItem)
    this.state.isDirty = true
    this.notifyListeners()

    return newItem
  }

  removeItem<K extends DataKey>(resource: K, id: number): boolean {
    if (!this.state.deletions.has(resource)) {
      this.state.deletions.set(resource, new Set())
    }

    const deletions = this.state.deletions.get(resource)!
    deletions.add(id)

    this.state.isDirty = true
    this.notifyListeners()

    return true
  }

  restoreItem<K extends DataKey>(resource: K, id: number): boolean {
    const deletions = this.state.deletions.get(resource)
    if (deletions) {
      deletions.delete(id)
      this.state.isDirty = true
      this.notifyListeners()
    }
    return true
  }

  applyOverrides(overrides: DataOverrideConfig): void {
    Object.entries(overrides).forEach(([resource, items]) => {
      if (Array.isArray(items)) {
        items.forEach((item, index) => {
          if (item && typeof item === 'object' && 'id' in item) {
            this.setItem(resource as DataKey, (item as { id: number }).id, item as Partial<DataItem<DataKey>>)
          } else if (item) {
            const data = this.getResource(resource as DataKey)
            const existingId = data[index]?.id
            if (existingId) {
              this.setItem(resource as DataKey, existingId, item as Partial<DataItem<DataKey>>)
            }
          }
        })
      }
    })
  }

  query<K extends DataKey>(
    resource: K,
    options: DataQueryOptions = {}
  ): DataQueryResult<DataItem<K>> {
    const { page = 1, pageSize = 10, filters, sort } = options
    let data = this.getResource(resource) as DataItem<K>[]

    if (filters) {
      data = this.applyFilters(data, filters)
    }

    if (sort) {
      data = this.applySort(data, sort)
    }

    const total = data.length
    const start = (page - 1) * pageSize
    const end = start + pageSize

    return {
      data: data.slice(start, end),
      total,
      page,
      pageSize
    }
  }

  private applyFilters<T>(data: T[], filters: Record<string, unknown>): T[] {
    return data.filter(item => {
      return Object.entries(filters).every(([key, value]) => {
        if (value === undefined || value === null || value === '') {
          return true
        }

        const itemValue = (item as Record<string, unknown>)[key]

        if (Array.isArray(value)) {
          return value.includes(itemValue)
        }

        if (typeof value === 'string' && typeof itemValue === 'string') {
          return itemValue.toLowerCase().includes(value.toLowerCase())
        }

        return itemValue === value
      })
    })
  }

  private applySort<T>(data: T[], sort: { field: string; order: 'asc' | 'desc' }): T[] {
    return [...data].sort((a, b) => {
      const aVal = (a as Record<string, unknown>)[sort.field]
      const bVal = (b as Record<string, unknown>)[sort.field]

      if (aVal === bVal) return 0
      if (aVal === undefined || aVal === null) return 1
      if (bVal === undefined || bVal === null) return -1

      const comparison = aVal < bVal ? -1 : 1
      return sort.order === 'asc' ? comparison : -comparison
    })
  }

  private applyModifications(data: GeneratedData): GeneratedData {
    const result = { ...data }

    this.state.overrides.forEach((resourceOverrides, resource) => {
      const key = resource as DataKey
      if (result[key]) {
        result[key] = result[key].map(item => {
          const id = (item as { id: number }).id
          const overrides = resourceOverrides.get(id)
          return overrides ? { ...item, ...overrides } : item
        }) as GeneratedData[DataKey]
      }
    })

    this.state.deletions.forEach((ids, resource) => {
      const key = resource as DataKey
      if (result[key]) {
        result[key] = result[key].filter(
          item => !ids.has((item as { id: number }).id)
        ) as GeneratedData[DataKey]
      }
    })

    this.state.additions.forEach((items, resource) => {
      const key = resource as DataKey
      if (result[key]) {
        result[key] = [...result[key], ...items] as GeneratedData[DataKey]
      }
    })

    return result
  }

  private getMaxId<K extends DataKey>(resource: K): number {
    const data = this.getResource(resource)
    let maxId = 0

    data.forEach(item => {
      const id = (item as { id: number }).id
      if (id > maxId) maxId = id
    })

    return maxId
  }

  reset(): void {
    this.state = {
      data: null,
      overrides: new Map(),
      additions: new Map(),
      deletions: new Map(),
      isDirty: false
    }
    this.notifyListeners()
  }

  regenerate(config?: DataGeneratorConfig): GeneratedData {
    this.state.data = generateAllData(config || {})
    this.state.overrides.clear()
    this.state.additions.clear()
    this.state.deletions.clear()
    this.state.isDirty = false
    this.notifyListeners()
    return this.state.data
  }

  subscribe(listener: (state: DataManagerState) => void): () => void {
    this.listeners.add(listener)
    return () => this.listeners.delete(listener)
  }

  private notifyListeners(): void {
    this.listeners.forEach(listener => listener({ ...this.state }))
  }

  exportData(): GeneratedData {
    return JSON.parse(JSON.stringify(this.getData()))
  }

  importData(data: Partial<GeneratedData>): void {
    if (!this.state.data) {
      this.state.data = generateAllData({})
    }

    Object.entries(data).forEach(([key, value]) => {
      if (key in this.state.data && Array.isArray(value)) {
        (this.state.data as Record<string, unknown>)[key] = value
      }
    })

    this.state.isDirty = true
    this.notifyListeners()
  }

  getStatistics(): Record<DataKey, { total: number; modified: number; added: number; deleted: number }> {
    const stats: Record<string, { total: number; modified: number; added: number; deleted: number }> = {}

    const resources: DataKey[] = ['users', 'pets', 'merchants', 'services', 'products', 'appointments', 'orders', 'reviews']

    resources.forEach(resource => {
      const data = this.getResource(resource)
      stats[resource] = {
        total: data.length,
        modified: this.state.overrides.get(resource)?.size || 0,
        added: this.state.additions.get(resource)?.length || 0,
        deleted: this.state.deletions.get(resource)?.size || 0
      }
    })

    return stats as Record<DataKey, { total: number; modified: number; added: number; deleted: number }>
  }

  createUser(overrides: Partial<UserData> = {}): UserData {
    const id = this.getMaxId('users') + 1
    const user: UserData = {
      id,
      username: `用户${id}`,
      email: `user${id}@example.com`,
      phone: `13800138000`,
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${id}`,
      createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      ...overrides
    }
    return this.addItem('users', user)
  }

  createPet(userId: number, overrides: Partial<PetData> = {}): PetData {
    const id = this.getMaxId('pets') + 1
    const pet: PetData = {
      id,
      userId,
      name: `宠物${id}`,
      type: '狗',
      breed: '金毛',
      age: 1,
      gender: 'male',
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=pet${id}`,
      description: '可爱的小宠物',
      createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      ...overrides
    }
    return this.addItem('pets', pet)
  }

  createAppointment(
    userId: number,
    petId: number,
    merchantId: number,
    serviceId: number,
    overrides: Partial<AppointmentData> = {}
  ): AppointmentData {
    const id = this.getMaxId('appointments') + 1
    const user = this.getItem('users', userId)
    const pet = this.getItem('pets', petId)
    const merchant = this.getItem('merchants', merchantId)
    const service = this.getItem('services', serviceId)

    const appointment: AppointmentData = {
      id,
      userId,
      userName: user?.username || '',
      merchantId,
      merchantName: merchant?.name || '',
      serviceId,
      serviceName: service?.name || '',
      petId,
      petName: pet?.name || '',
      appointmentTime: new Date(Date.now() + 86400000).toISOString().replace('T', ' ').substring(0, 19),
      totalPrice: service?.price || 0,
      status: 'pending',
      notes: '',
      createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      ...overrides
    }
    return this.addItem('appointments', appointment)
  }

  createOrder(
    userId: number,
    merchantId: number,
    items: Array<{ productId: number; quantity: number }>,
    overrides: Partial<OrderData> = {}
  ): OrderData {
    const id = this.getMaxId('orders') + 1
    const user = this.getItem('users', userId)
    const merchant = this.getItem('merchants', merchantId)

    const orderItems = items.map((item, index) => {
      const product = this.getItem('products', item.productId)
      return {
        id: index + 1,
        productId: item.productId,
        productName: product?.name || '',
        productImage: product?.image || '',
        price: product?.price || 0,
        quantity: item.quantity,
        subtotal: Number(((product?.price || 0) * item.quantity).toFixed(2))
      }
    })

    const totalPrice = Number(orderItems.reduce((sum, item) => sum + item.subtotal, 0).toFixed(2))

    const order: OrderData = {
      id,
      orderNo: `ORD${Date.now()}`,
      userId,
      userName: user?.username || '',
      merchantId,
      merchantName: merchant?.name || '',
      totalPrice,
      freight: totalPrice > 99 ? 0 : 10,
      status: 'pending',
      payMethod: '微信支付',
      payTime: null,
      shipTime: null,
      completeTime: null,
      cancelTime: null,
      remark: '',
      shippingAddress: {
        name: user?.username || '',
        phone: user?.phone || '',
        province: '北京市',
        city: '北京市',
        district: '朝阳区',
        detailAddress: '测试地址123号'
      },
      items: orderItems,
      createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      ...overrides
    }
    return this.addItem('orders', order)
  }

  createReview(
    userId: number,
    merchantId: number,
    serviceId: number,
    appointmentId: number,
    rating: number,
    comment: string,
    overrides: Partial<ReviewData> = {}
  ): ReviewData {
    const id = this.getMaxId('reviews') + 1
    const user = this.getItem('users', userId)
    const merchant = this.getItem('merchants', merchantId)
    const service = this.getItem('services', serviceId)

    const review: ReviewData = {
      id,
      userId,
      userName: user?.username || '',
      userAvatar: user?.avatar || '',
      merchantId,
      merchantName: merchant?.name || '',
      serviceId,
      serviceName: service?.name || '',
      appointmentId,
      rating,
      comment,
      images: [],
      reply: null,
      replyTime: null,
      createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      ...overrides
    }
    return this.addItem('reviews', review)
  }
}

export const mockDataManager = MockDataManager.getInstance()

export const initializeData = (config?: DataGeneratorConfig): GeneratedData => {
  return mockDataManager.initialize(config || {})
}

export const getData = (): GeneratedData => mockDataManager.getData()

export const getResource = <K extends DataKey>(resource: K): GeneratedData[K] => {
  return mockDataManager.getResource(resource)
}

export const getItem = <K extends DataKey>(resource: K, id: number): DataItem<K> | undefined => {
  return mockDataManager.getItem(resource, id)
}

export const setItem = <K extends DataKey>(
  resource: K,
  id: number,
  updates: Partial<DataItem<K>>
): boolean => {
  return mockDataManager.setItem(resource, id, updates)
}

export const addItem = <K extends DataKey>(resource: K, item: DataItem<K>): DataItem<K> => {
  return mockDataManager.addItem(resource, item)
}

export const removeItem = <K extends DataKey>(resource: K, id: number): boolean => {
  return mockDataManager.removeItem(resource, id)
}

export const queryData = <K extends DataKey>(
  resource: K,
  options?: DataQueryOptions
): DataQueryResult<DataItem<K>> => {
  return mockDataManager.query(resource, options)
}

export const resetData = (): void => mockDataManager.reset()

export const regenerateData = (config?: DataGeneratorConfig): GeneratedData => {
  return mockDataManager.regenerate(config)
}

export const getDataStatistics = () => mockDataManager.getStatistics()

export default mockDataManager
