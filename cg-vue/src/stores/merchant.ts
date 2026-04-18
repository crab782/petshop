import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export interface MerchantInfo {
  id: number
  merchantName: string
  businessLicense?: string
  contactPhone?: string
  address?: string
  avatar?: string
  [key: string]: any
}

export interface MerchantState {
  token: string
  merchantInfo: MerchantInfo | null
  isLoggedIn: boolean
}

const TOKEN_KEY = 'merchant_token'

export const useMerchantStore = defineStore('merchant', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const merchantInfo = ref<MerchantInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem(TOKEN_KEY, newToken)
  }

  function setMerchantInfo(info: MerchantInfo) {
    merchantInfo.value = info
  }

  function logout() {
    token.value = ''
    merchantInfo.value = null
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
    merchantInfo,
    isLoggedIn,
    setToken,
    setMerchantInfo,
    logout,
    checkLogin
  }
})
