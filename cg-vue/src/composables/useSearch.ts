import { ref, watch, type Ref } from 'vue'

interface UseSearchReturn {
  keyword: Ref<string>
  filters: Ref<Record<string, any>>
  debouncedKeyword: Ref<string>
  setKeyword: (value: string) => void
  setFilter: (key: string, value: any) => void
  removeFilter: (key: string) => void
  clearFilters: () => void
  reset: () => void
}

export function useSearch(debounceMs = 300): UseSearchReturn {
  const keyword = ref<string>('')
  const filters = ref<Record<string, any>>({})
  const debouncedKeyword = ref<string>('')

  let timeoutId: ReturnType<typeof setTimeout> | null = null

  watch(keyword, (newValue) => {
    if (timeoutId) {
      clearTimeout(timeoutId)
    }
    timeoutId = setTimeout(() => {
      debouncedKeyword.value = newValue
    }, debounceMs)
  })

  const setKeyword = (value: string): void => {
    keyword.value = value
  }

  const setFilter = (key: string, value: any): void => {
    filters.value = {
      ...filters.value,
      [key]: value
    }
  }

  const removeFilter = (key: string): void => {
    const newFilters = { ...filters.value }
    delete newFilters[key]
    filters.value = newFilters
  }

  const clearFilters = (): void => {
    filters.value = {}
  }

  const reset = (): void => {
    keyword.value = ''
    debouncedKeyword.value = ''
    filters.value = {}
    if (timeoutId) {
      clearTimeout(timeoutId)
      timeoutId = null
    }
  }

  return {
    keyword,
    filters,
    debouncedKeyword,
    setKeyword,
    setFilter,
    removeFilter,
    clearFilters,
    reset
  }
}
