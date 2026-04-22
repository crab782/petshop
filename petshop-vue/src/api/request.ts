import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { AxiosInstance, AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios'

const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
})

request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    let token = ''
    const path = window.location.pathname
    
    if (path.startsWith('/merchant/')) {
      // 商家端
      token = localStorage.getItem('merchant_token') || sessionStorage.getItem('merchant_token')
    } else if (path.startsWith('/admin/')) {
      // 平台端
      token = localStorage.getItem('admin_token') || sessionStorage.getItem('admin_token')
    } else {
      // 用户端
      token = localStorage.getItem('user_token') || sessionStorage.getItem('user_token')
    }
    
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response: AxiosResponse) => {
    const data = response.data
    // 处理后端API返回的格式
    if (data && typeof data === 'object') {
      if (data.code === 200 || data.code === 0) {
        // 成功响应，返回data字段
        return data.data
      } else {
        // 失败响应，抛出错误
        ElMessage.error(data.message || '请求失败')
        return Promise.reject(new Error(data.message || '请求失败'))
      }
    }
    return data
  },
  (error: AxiosError) => {
    if (error.response) {
      const status = error.response.status
      switch (status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          // 清除所有token
          localStorage.removeItem('user_token')
          localStorage.removeItem('merchant_token')
          localStorage.removeItem('admin_token')
          sessionStorage.removeItem('user_token')
          sessionStorage.removeItem('merchant_token')
          sessionStorage.removeItem('admin_token')
          // 根据当前路径跳转到对应的登录页
          const path = window.location.pathname
          if (path.startsWith('/merchant/')) {
            window.location.href = '/merchant/login'
          } else if (path.startsWith('/admin/')) {
            window.location.href = '/admin/login'
          } else {
            window.location.href = '/login'
          }
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error('请求失败')
      }
    } else if (error.request) {
      ElMessage.error('网络连接失败')
    } else {
      ElMessage.error('请求配置错误')
    }
    return Promise.reject(error)
  }
)

export default request
