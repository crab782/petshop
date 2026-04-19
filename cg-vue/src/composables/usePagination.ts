import { ref, computed, type Ref } from 'vue'

interface UsePaginationReturn {
  page: Ref<number>
  pageSize: Ref<number>
  total: Ref<number>
  totalPages: ReturnType<typeof computed<number>>
  hasNextPage: ReturnType<typeof computed<boolean>>
  hasPrevPage: ReturnType<typeof computed<boolean>>
  setPage: (newPage: number) => void
  setPageSize: (newSize: number) => void
  setTotal: (newTotal: number) => void
  nextPage: () => void
  prevPage: () => void
  firstPage: () => void
  lastPage: () => void
  reset: () => void
}

export function usePagination(defaultPage = 1, defaultPageSize = 10): UsePaginationReturn {
  const page = ref<number>(defaultPage)
  const pageSize = ref<number>(defaultPageSize)
  const total = ref<number>(0)

  const totalPages = computed<number>(() => {
    return Math.max(1, Math.ceil(total.value / pageSize.value))
  })

  const hasNextPage = computed<boolean>(() => {
    return page.value < totalPages.value
  })

  const hasPrevPage = computed<boolean>(() => {
    return page.value > 1
  })

  const setPage = (newPage: number): void => {
    const validPage = Math.max(1, Math.min(newPage, totalPages.value))
    page.value = validPage
  }

  const setPageSize = (newSize: number): void => {
    const validSize = Math.max(1, newSize)
    pageSize.value = validSize
    page.value = 1
  }

  const setTotal = (newTotal: number): void => {
    total.value = Math.max(0, newTotal)
    if (page.value > totalPages.value) {
      page.value = totalPages.value
    }
  }

  const nextPage = (): void => {
    if (hasNextPage.value) {
      page.value++
    }
  }

  const prevPage = (): void => {
    if (hasPrevPage.value) {
      page.value--
    }
  }

  const firstPage = (): void => {
    page.value = 1
  }

  const lastPage = (): void => {
    page.value = totalPages.value
  }

  const reset = (): void => {
    page.value = defaultPage
    pageSize.value = defaultPageSize
    total.value = 0
  }

  return {
    page,
    pageSize,
    total,
    totalPages,
    hasNextPage,
    hasPrevPage,
    setPage,
    setPageSize,
    setTotal,
    nextPage,
    prevPage,
    firstPage,
    lastPage,
    reset
  }
}
