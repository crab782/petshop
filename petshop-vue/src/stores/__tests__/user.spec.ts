import { describe, it, expect, beforeEach, afterEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../user'

describe('user store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
  })

  afterEach(() => {
    localStorage.clear()
  })

  describe('initial state', () => {
    it('should have empty token initially', () => {
      const store = useUserStore()
      expect(store.token).toBe('')
    })

    it('should have null userInfo initially', () => {
      const store = useUserStore()
      expect(store.userInfo).toBeNull()
    })

    it('should not be logged in initially', () => {
      const store = useUserStore()
      expect(store.isLoggedIn).toBe(false)
    })

    it('should load token from localStorage if present', () => {
      localStorage.setItem('user_token', 'existing-token')
      setActivePinia(createPinia())
      const store = useUserStore()
      expect(store.token).toBe('existing-token')
      expect(store.isLoggedIn).toBe(true)
    })
  })

  describe('setToken', () => {
    it('should set token value', () => {
      const store = useUserStore()
      store.setToken('new-token')
      expect(store.token).toBe('new-token')
    })

    it('should save token to localStorage', () => {
      const store = useUserStore()
      store.setToken('new-token')
      expect(localStorage.getItem('user_token')).toBe('new-token')
    })

    it('should update isLoggedIn computed', () => {
      const store = useUserStore()
      expect(store.isLoggedIn).toBe(false)
      store.setToken('new-token')
      expect(store.isLoggedIn).toBe(true)
    })
  })

  describe('setUserInfo', () => {
    it('should set userInfo', () => {
      const store = useUserStore()
      const userInfo = {
        id: 1,
        username: 'testuser',
        email: 'test@example.com',
        phone: '13800138000',
        avatar: 'https://example.com/avatar.png',
      }
      store.setUserInfo(userInfo)
      expect(store.userInfo).toEqual(userInfo)
    })
  })

  describe('logout', () => {
    it('should clear token', () => {
      const store = useUserStore()
      store.setToken('test-token')
      store.logout()
      expect(store.token).toBe('')
    })

    it('should clear userInfo', () => {
      const store = useUserStore()
      store.setUserInfo({
        id: 1,
        username: 'testuser',
      })
      store.logout()
      expect(store.userInfo).toBeNull()
    })

    it('should remove token from localStorage', () => {
      const store = useUserStore()
      store.setToken('test-token')
      store.logout()
      expect(localStorage.getItem('user_token')).toBeNull()
    })

    it('should update isLoggedIn computed', () => {
      const store = useUserStore()
      store.setToken('test-token')
      expect(store.isLoggedIn).toBe(true)
      store.logout()
      expect(store.isLoggedIn).toBe(false)
    })
  })

  describe('checkLogin', () => {
    it('should return false if no token in localStorage', () => {
      const store = useUserStore()
      const result = store.checkLogin()
      expect(result).toBe(false)
    })

    it('should return true if token exists in localStorage', () => {
      localStorage.setItem('user_token', 'existing-token')
      const store = useUserStore()
      const result = store.checkLogin()
      expect(result).toBe(true)
    })

    it('should set token from localStorage', () => {
      localStorage.setItem('user_token', 'existing-token')
      const store = useUserStore()
      store.checkLogin()
      expect(store.token).toBe('existing-token')
    })
  })

  describe('isLoggedIn computed', () => {
    it('should return false when token is empty', () => {
      const store = useUserStore()
      expect(store.isLoggedIn).toBe(false)
    })

    it('should return true when token is set', () => {
      const store = useUserStore()
      store.setToken('test-token')
      expect(store.isLoggedIn).toBe(true)
    })

    it('should return false after logout', () => {
      const store = useUserStore()
      store.setToken('test-token')
      store.logout()
      expect(store.isLoggedIn).toBe(false)
    })
  })
})
