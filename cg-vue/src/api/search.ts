import request from './request'
import { Service, Product, MerchantInfo } from './user'

export interface SearchSuggestions {
  services: Service[]
  products: Product[]
  merchants: MerchantInfo[]
}

export const getSearchSuggestions = (keyword: string) => {
  return request.get<SearchSuggestions>('/api/search/suggestions', { params: { keyword } })
}

export interface HotKeyword {
  keyword: string
  count: number
}

export const getHotKeywords = (limit?: number) => {
  return request.get<HotKeyword[]>('/api/search/hot-keywords', { params: { limit } })
}

export const saveSearchHistory = (keyword: string) => {
  return request.post('/api/user/search-history', { keyword })
}

export const getSearchHistory = (limit?: number) => {
  return request.get<string[]>('/api/user/search-history', { params: { limit } })
}

export const clearSearchHistory = () => {
  return request.delete('/api/user/search-history')
}
