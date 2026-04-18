export interface HotSearchKeyword {
  id: number
  keyword: string
  searchCount: number
  trend: 'up' | 'down' | 'stable'
  category: 'service' | 'product' | 'merchant' | 'general'
}

export interface SearchHistory {
  id: number
  keyword: string
  searchedAt: string
}

export interface SearchResult {
  products: any[]
  services: any[]
  merchants: any[]
  total: number
}

export const hotSearchKeywords: HotSearchKeyword[] = [
  { id: 1, keyword: '宠物美容', searchCount: 15680, trend: 'up', category: 'service' },
  { id: 2, keyword: '猫粮', searchCount: 12350, trend: 'up', category: 'product' },
  { id: 3, keyword: '宠物寄养', searchCount: 9870, trend: 'stable', category: 'service' },
  { id: 4, keyword: '狗粮', searchCount: 8920, trend: 'down', category: 'product' },
  { id: 5, keyword: '宠物洗澡', searchCount: 7650, trend: 'up', category: 'service' },
  { id: 6, keyword: '宠物医院', searchCount: 6540, trend: 'stable', category: 'merchant' },
  { id: 7, keyword: '猫砂', searchCount: 5890, trend: 'up', category: 'product' },
  { id: 8, keyword: '宠物疫苗', searchCount: 5230, trend: 'up', category: 'service' },
  { id: 9, keyword: '宠物玩具', searchCount: 4560, trend: 'stable', category: 'product' },
  { id: 10, keyword: '宠物训练', searchCount: 4120, trend: 'up', category: 'service' },
  { id: 11, keyword: '宠物SPA', searchCount: 3890, trend: 'up', category: 'service' },
  { id: 12, keyword: '宠物服饰', searchCount: 3450, trend: 'stable', category: 'product' },
  { id: 13, keyword: '宠物体检', searchCount: 3120, trend: 'up', category: 'service' },
  { id: 14, keyword: '宠物用品', searchCount: 2890, trend: 'stable', category: 'general' },
  { id: 15, keyword: '宠物摄影', searchCount: 2560, trend: 'up', category: 'service' }
]

export const searchHistories: SearchHistory[] = [
  { id: 1, keyword: '宠物美容', searchedAt: '2025-04-18 14:30:00' },
  { id: 2, keyword: '猫粮', searchedAt: '2025-04-18 10:15:00' },
  { id: 3, keyword: '宠物寄养', searchedAt: '2025-04-17 16:45:00' },
  { id: 4, keyword: '宠物洗澡', searchedAt: '2025-04-17 09:20:00' },
  { id: 5, keyword: '宠物医院', searchedAt: '2025-04-16 15:30:00' }
]

export const getHotSearchKeywords = (limit: number = 10): HotSearchKeyword[] => {
  return [...hotSearchKeywords]
    .sort((a, b) => b.searchCount - a.searchCount)
    .slice(0, limit)
}

export const getSearchHistories = (userId: number, limit: number = 10): SearchHistory[] => {
  return searchHistories.slice(0, limit)
}

export const addSearchHistory = (keyword: string): SearchHistory => {
  const newHistory: SearchHistory = {
    id: searchHistories.length + 1,
    keyword,
    searchedAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
  }
  searchHistories.unshift(newHistory)
  return newHistory
}

export const clearSearchHistories = (): void => {
  searchHistories.length = 0
}
