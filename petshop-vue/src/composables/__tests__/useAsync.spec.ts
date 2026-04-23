import { describe, it, expect } from 'vitest'
import { nextTick } from 'vue'
import { useAsync } from '../useAsync'

describe('useAsync', () => {
  describe('initial state', () => {
    it('should have null data initially', () => {
      const { data } = useAsync(async () => 'test')
      expect(data.value).toBeNull()
    })

    it('should have false loading initially', () => {
      const { loading } = useAsync(async () => 'test')
      expect(loading.value).toBe(false)
    })

    it('should have null error initially', () => {
      const { error } = useAsync(async () => 'test')
      expect(error.value).toBeNull()
    })

    it('should use initial value if provided', () => {
      const { data } = useAsync(async () => 'test', 'initial')
      expect(data.value).toBe('initial')
    })
  })

  describe('execute', () => {
    it('should set loading to true during execution', async () => {
      const { loading, execute } = useAsync(async () => {
        await new Promise(resolve => setTimeout(resolve, 100))
        return 'test'
      })

      const promise = execute()
      expect(loading.value).toBe(true)
      await promise
      expect(loading.value).toBe(false)
    })

    it('should set data on successful execution', async () => {
      const { data, execute } = useAsync(async () => 'test result')

      await execute()

      expect(data.value).toBe('test result')
    })

    it('should return result on successful execution', async () => {
      const { execute } = useAsync(async () => 'test result')

      const result = await execute()

      expect(result).toBe('test result')
    })

    it('should set error on failed execution', async () => {
      const { error, execute } = useAsync(async () => {
        throw new Error('Test error')
      })

      await execute()

      expect(error.value).toBeInstanceOf(Error)
      expect(error.value?.message).toBe('Test error')
    })

    it('should return null on failed execution', async () => {
      const { execute } = useAsync(async () => {
        throw new Error('Test error')
      })

      const result = await execute()

      expect(result).toBeNull()
    })

    it('should set loading to false after failed execution', async () => {
      const { loading, execute } = useAsync(async () => {
        throw new Error('Test error')
      })

      await execute()

      expect(loading.value).toBe(false)
    })

    it('should pass arguments to async function', async () => {
      const mockFn = async (a: number, b: number) => a + b
      const { execute } = useAsync(mockFn)

      const result = await execute(2, 3)

      expect(result).toBe(5)
    })

    it('should clear error on successful execution after failure', async () => {
      let shouldFail = true
      const { error, execute } = useAsync(async () => {
        if (shouldFail) {
          throw new Error('Test error')
        }
        return 'success'
      })

      await execute()
      expect(error.value).toBeInstanceOf(Error)

      shouldFail = false
      await execute()
      expect(error.value).toBeNull()
    })

    it('should convert non-Error throws to Error', async () => {
      const { error, execute } = useAsync(async () => {
        throw 'string error'
      })

      await execute()

      expect(error.value).toBeInstanceOf(Error)
      expect(error.value?.message).toBe('string error')
    })
  })

  describe('reset', () => {
    it('should reset data to null', async () => {
      const { data, execute, reset } = useAsync(async () => 'test')

      await execute()
      expect(data.value).toBe('test')

      reset()
      expect(data.value).toBeNull()
    })

    it('should reset data to initial value', async () => {
      const { data, execute, reset } = useAsync(async () => 'test', 'initial')

      await execute()
      expect(data.value).toBe('test')

      reset()
      expect(data.value).toBe('initial')
    })

    it('should reset loading to false', async () => {
      const { loading, reset } = useAsync(async () => 'test')

      loading.value = true
      reset()
      expect(loading.value).toBe(false)
    })

    it('should reset error to null', async () => {
      const { error, execute, reset } = useAsync(async () => {
        throw new Error('Test error')
      })

      await execute()
      expect(error.value).toBeInstanceOf(Error)

      reset()
      expect(error.value).toBeNull()
    })
  })

  describe('reactivity', () => {
    it('should update data reactively', async () => {
      const { data, execute } = useAsync(async () => 'test')

      expect(data.value).toBeNull()

      await execute()
      await nextTick()

      expect(data.value).toBe('test')
    })

    it('should update loading reactively', async () => {
      const { loading, execute } = useAsync(async () => {
        await new Promise(resolve => setTimeout(resolve, 10))
        return 'test'
      })

      expect(loading.value).toBe(false)

      const promise = execute()
      await nextTick()
      expect(loading.value).toBe(true)

      await promise
      await nextTick()
      expect(loading.value).toBe(false)
    })
  })
})
