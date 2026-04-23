import { vi } from 'vitest'
import { createService, createProduct, createMerchant } from './userMocks'

export interface MockSearchSuggestions {
  services: any[]
  products: any[]
  merchants: any[]
}

export interface MockHotKeyword {
  keyword: string
  count: number
}

export const defaultHotKeywords: MockHotKeyword[] = [
  { keyword: '宠物洗澡', count: 1500 },
  { keyword: '狗粮', count: 1200 },
  { keyword: '宠物美容', count: 980 },
  { keyword: '猫粮', count: 850 },
  { keyword: '宠物寄养', count: 720 },
  { keyword: '宠物疫苗', count: 650 },
  { keyword: '宠物玩具', count: 580 },
  { keyword: '宠物医疗', count: 520 },
  { keyword: '宠物用品', count: 480 },
  { keyword: '宠物训练', count: 420 },
]

export const createHotKeywords = (count: number = 10): MockHotKeyword[] =>
  defaultHotKeywords.slice(0, count)

export const mockSearchApi = {
  getSearchSuggestions: vi.fn((keyword: string) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        services: [createService({ name: `${keyword}服务` })],
        products: [createProduct({ name: `${keyword}商品` })],
        merchants: [createMerchant({ name: `${keyword}商家` })],
      },
    })
  ),

  getHotKeywords: vi.fn((limit?: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: createHotKeywords(limit || 10),
    })
  ),

  saveSearchHistory: vi.fn((keyword: string) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: null,
    })
  ),

  getSearchHistory: vi.fn((limit?: number) =>
    Promise.resolve({
      code: 200,
      message: 'success',
      data: ['宠物洗澡', '狗粮', '宠物美容', '猫粮', '宠物寄养'].slice(0, limit || 10),
    })
  ),

  clearSearchHistory: vi.fn(() =>
    Promise.resolve({
      code: 200,
      message: '清空成功',
      data: null,
    })
  ),
}

export const mockSearchResponses = {
  suggestions: {
    code: 200,
    message: 'success',
    data: {
      services: [createService()],
      products: [createProduct()],
      merchants: [createMerchant()],
    },
  },
  hotKeywords: {
    code: 200,
    message: 'success',
    data: defaultHotKeywords,
  },
  searchHistory: {
    code: 200,
    message: 'success',
    data: ['宠物洗澡', '狗粮', '宠物美容'],
  },
}
