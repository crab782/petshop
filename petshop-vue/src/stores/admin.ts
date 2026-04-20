import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export interface AdminInfo {
  id: number
  username: string
  email?: string
  role?: string
  avatar?: string
  [key: string]: any
}

export interface AdminState {
  token: string
  adminInfo: AdminInfo | null
  isLoggedIn: boolean
}

const TOKEN_KEY = 'admin_token'

export const useAdminStore = defineStore('admin', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const adminInfo = ref<AdminInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem(TOKEN_KEY, newToken)
  }

  function setAdminInfo(info: AdminInfo) {
    adminInfo.value = info
  }

  function logout() {
    token.value = ''
    adminInfo.value = null
    localStorage.removeItem(TOKEN_KEY)
  }

  function checkLogin() {
    const storedToken = localStorage.getItem(TOKEN_KEY)
    if (storedToken) {
      token.value = storedToken
      return true
    }
    return false
  }

  return {
    token,
    adminInfo,
    isLoggedIn,
    setToken,
    setAdminInfo,
    logout,
    checkLogin
  }
})
