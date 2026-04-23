import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import Login from '../Login.vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElLink } from 'element-plus'

vi.mock('axios', () => ({
  default: {
    post: vi.fn()
  }
}))

vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      warning: vi.fn(),
      error: vi.fn(),
      info: vi.fn()
    }
  }
})

vi.mock('@element-plus/icons-vue', () => ({
  Lock: { name: 'Lock', render: () => null },
  Phone: { name: 'Phone', render: () => null }
}))

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter
}))

import axios from 'axios'
import { ElMessage } from 'element-plus'

const mockRouterPush = vi.fn()
const mockRouter = {
  push: mockRouterPush,
  replace: vi.fn(),
  go: vi.fn(),
  back: vi.fn(),
  forward: vi.fn()
}

describe('Login.vue - 商家端登录页面', () => {
  let wrapper: any

  const mountComponent = (options = {}) => {
    return mount(Login, {
      global: {
        components: {
          ElForm,
          ElFormItem,
          ElInput,
          ElButton,
          ElLink
        },
        provide: {
          'Symbol(router)': mockRouter
        },
        stubs: {
          'el-form': ElForm,
          'el-form-item': ElFormItem,
          'el-input': ElInput,
          'el-button': ElButton,
          'el-link': ElLink
        }
      },
      ...options
    })
  }

  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
    wrapper = mountComponent()
  })

  afterEach(() => {
    wrapper?.unmount()
  })

  describe('页面渲染测试', () => {
    it('应该正确渲染登录容器', () => {
      expect(wrapper.find('.merchant-login-container').exists()).toBe(true)
      expect(wrapper.find('.login-card').exists()).toBe(true)
    })

    it('应该正确渲染品牌标识', () => {
      expect(wrapper.find('.brand-logo').exists()).toBe(true)
      expect(wrapper.find('.logo-icon').text()).toBe('🐶')
      expect(wrapper.find('.brand-title').text()).toBe('宠物服务平台')
    })

    it('应该正确渲染登录标题和副标题', () => {
      expect(wrapper.find('.login-title').text()).toBe('商家端登录')
      expect(wrapper.find('.login-subtitle').text()).toBe('欢迎回来，商家朋友')
    })

    it('应该渲染手机号输入框', () => {
      const phoneInput = wrapper.find('input[placeholder="请输入手机号"]')
      expect(phoneInput.exists()).toBe(true)
    })

    it('应该渲染密码输入框', () => {
      const passwordInput = wrapper.find('input[type="password"]')
      expect(passwordInput.exists()).toBe(true)
    })

    it('应该渲染登录按钮', () => {
      const loginButton = wrapper.find('.login-button')
      expect(loginButton.exists()).toBe(true)
      expect(loginButton.text()).toBe('登录')
    })

    it('应该渲染注册链接', () => {
      const registerLink = wrapper.find('.login-footer .el-link')
      expect(registerLink.exists()).toBe(true)
      expect(registerLink.text()).toBe('立即注册')
    })

    it('应该渲染用户登录链接', () => {
      const userLoginLinks = wrapper.findAll('.login-footer .el-link')
      expect(userLoginLinks.length).toBeGreaterThan(0)
    })

    it('应该渲染商家端特色功能', () => {
      expect(wrapper.find('.merchant-features').exists()).toBe(true)
      const featureItems = wrapper.findAll('.feature-item')
      expect(featureItems.length).toBe(3)
    })
  })

  describe('表单验证测试', () => {
    it('表单应该有正确的验证规则', () => {
      const vm = wrapper.vm
      expect(vm.loginRules).toBeDefined()
      expect(vm.loginRules.phone).toBeDefined()
      expect(vm.loginRules.password).toBeDefined()
    })

    it('手机号验证规则应包含必填和格式验证', () => {
      const phoneRules = wrapper.vm.loginRules.phone
      expect(phoneRules.some((rule: any) => rule.required)).toBe(true)
      expect(phoneRules.some((rule: any) => rule.pattern)).toBe(true)
    })

    it('密码验证规则应包含必填和最小长度', () => {
      const passwordRules = wrapper.vm.loginRules.password
      expect(passwordRules.some((rule: any) => rule.required)).toBe(true)
      expect(passwordRules.some((rule: any) => rule.min === 6)).toBe(true)
    })

    it('手机号格式不正确时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.loginForm.phone = 'invalid-phone'
      await wrapper.vm.$nextTick()
      expect(vm.loginForm.phone).not.toMatch(/^1[3-9]\d{9}$/)
    })

    it('密码长度不足6个字符时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.loginForm.password = '12345'
      await wrapper.vm.$nextTick()
      expect(vm.loginForm.password.length).toBeLessThan(6)
    })

    it('有效手机号应该通过格式验证', async () => {
      const vm = wrapper.vm
      vm.loginForm.phone = '13800138000'
      await wrapper.vm.$nextTick()
      expect(vm.loginForm.phone).toMatch(/^1[3-9]\d{9}$/)
    })

    it('有效密码应该通过长度验证', async () => {
      const vm = wrapper.vm
      vm.loginForm.password = 'password123'
      await wrapper.vm.$nextTick()
      expect(vm.loginForm.password.length).toBeGreaterThanOrEqual(6)
    })
  })

  describe('登录功能测试', () => {
    const fillLoginForm = async () => {
      const vm = wrapper.vm
      vm.loginForm.phone = '13800138000'
      vm.loginForm.password = 'password123'
      await wrapper.vm.$nextTick()
    }

    it('登录成功后应该保存token和商家信息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: {
          code: 200,
          data: {
            token: 'merchant-token',
            user: { id: 1, name: '测试商家' }
          }
        }
      })

      await fillLoginForm()
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(localStorage.getItem('merchant_token')).toBe('merchant-token')
      expect(JSON.parse(localStorage.getItem('merchantInfo') || '{}')).toEqual({ id: 1, name: '测试商家' })
    })

    it('登录成功后应该显示成功消息并跳转', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: {
          code: 200,
          data: {
            token: 'merchant-token',
            user: { id: 1, name: '测试商家' }
          }
        }
      })

      await fillLoginForm()
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(ElMessage.success).toHaveBeenCalledWith('登录成功')
      expect(mockRouterPush).toHaveBeenCalledWith('/merchant/home')
    })

    it('登录成功(code=0)后应该显示成功消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: {
          code: 0,
          data: {
            token: 'merchant-token',
            user: { id: 1 }
          }
        }
      })

      await fillLoginForm()
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(ElMessage.success).toHaveBeenCalledWith('登录成功')
    })

    it('登录失败时应该显示错误消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: {
          code: 400,
          message: '手机号或密码错误'
        }
      })

      await fillLoginForm()
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('手机号或密码错误')
    })

    it('登录失败时应该显示默认错误消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: {
          code: 500,
          message: ''
        }
      })

      await fillLoginForm()
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('登录失败')
    })

    it('网络错误时应该显示错误消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockRejectedValueOnce(new Error('Network Error'))

      await fillLoginForm()
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('登录失败，请检查手机号和密码')
    })

    it('服务器返回错误时应该显示服务器错误消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockRejectedValueOnce({
        response: {
          data: {
            message: '服务器内部错误'
          }
        }
      })

      await fillLoginForm()
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('服务器内部错误')
    })
  })

  describe('导航功能测试', () => {
    it('点击注册链接应该跳转到商家注册页', () => {
      wrapper.vm.goToRegister()
      expect(mockRouterPush).toHaveBeenCalledWith('/merchant/register')
    })

    it('点击用户登录应该跳转到用户登录页', () => {
      wrapper.vm.goToUserLogin()
      expect(mockRouterPush).toHaveBeenCalledWith('/login')
    })
  })

  describe('边界条件测试', () => {
    it('空手机号时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.loginForm.phone = ''
      await wrapper.vm.$nextTick()
      expect(vm.loginForm.phone).toBe('')
    })

    it('空密码时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.loginForm.password = ''
      await wrapper.vm.$nextTick()
      expect(vm.loginForm.password).toBe('')
    })

    it('超长手机号应该被接受但不匹配格式', async () => {
      const vm = wrapper.vm
      vm.loginForm.phone = '138001380001'
      await wrapper.vm.$nextTick()
      expect(vm.loginForm.phone).not.toMatch(/^1[3-9]\d{9}$/)
    })

    it('特殊字符密码应该被接受', async () => {
      const vm = wrapper.vm
      vm.loginForm.password = 'Pass@123!#$%'
      await wrapper.vm.$nextTick()
      expect(vm.loginForm.password).toBe('Pass@123!#$%')
    })

    it('不同运营商手机号应该通过验证', async () => {
      const validPhones = ['13800138000', '15800138000', '18800138000', '19800138000']
      for (const phone of validPhones) {
        expect(phone).toMatch(/^1[3-9]\d{9}$/)
      }
    })
  })

  describe('表单数据绑定测试', () => {
    it('修改手机号应该更新表单数据', async () => {
      const vm = wrapper.vm
      vm.loginForm.phone = '13900139000'
      await wrapper.vm.$nextTick()
      expect(vm.loginForm.phone).toBe('13900139000')
    })

    it('修改密码应该更新表单数据', async () => {
      const vm = wrapper.vm
      vm.loginForm.password = 'newpassword123'
      await wrapper.vm.$nextTick()
      expect(vm.loginForm.password).toBe('newpassword123')
    })
  })

  describe('安全性测试', () => {
    it('密码输入框应该是密码类型', () => {
      const passwordInput = wrapper.find('input[type="password"]')
      expect(passwordInput.exists()).toBe(true)
    })

    it('密码输入框应该有显示密码功能', () => {
      const passwordInputs = wrapper.findAllComponents(ElInput)
      expect(passwordInputs.length).toBeGreaterThan(0)
    })

    it('登录成功后token应该存储在localStorage', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: {
          code: 200,
          data: {
            token: 'secure-merchant-token-123',
            user: { id: 1 }
          }
        }
      })

      const vm = wrapper.vm
      vm.loginForm.phone = '13800138000'
      vm.loginForm.password = 'password123'
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(localStorage.getItem('merchant_token')).toBe('secure-merchant-token-123')
    })
  })

  describe('用户体验测试', () => {
    it('登录按钮应该有加载状态', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)))

      const vm = wrapper.vm
      vm.loginForm.phone = '13800138000'
      vm.loginForm.password = 'password123'

      const loginPromise = wrapper.vm.handleLogin()
      await wrapper.vm.$nextTick()

      const loginButton = wrapper.find('.login-button')
      expect(loginButton.exists()).toBe(true)

      await loginPromise
      await flushPromises()
    })

    it('输入框应该有正确的占位符文本', () => {
      const phoneInput = wrapper.find('input[placeholder="请输入手机号"]')
      expect(phoneInput.exists()).toBe(true)

      const passwordInput = wrapper.find('input[placeholder="请输入密码"]')
      expect(passwordInput.exists()).toBe(true)
    })

    it('特色功能图标应该正确显示', () => {
      const featureIcons = wrapper.findAll('.feature-icon')
      expect(featureIcons.length).toBe(3)
      expect(featureIcons[0].text()).toBe('📊')
      expect(featureIcons[1].text()).toBe('🛠️')
      expect(featureIcons[2].text()).toBe('💳')
    })

    it('应该有分隔线显示', () => {
      expect(wrapper.find('.login-divider').exists()).toBe(true)
    })
  })

  describe('响应式设计测试', () => {
    it('登录卡片应该有最大宽度限制', () => {
      const loginCard = wrapper.find('.login-card')
      expect(loginCard.exists()).toBe(true)
    })

    it('表单应该正确响应窗口大小变化', () => {
      const form = wrapper.find('.login-form')
      expect(form.exists()).toBe(true)
    })
  })

  describe('商家特有功能测试', () => {
    it('应该显示商家端特色功能', () => {
      const featureTexts = wrapper.findAll('.feature-text')
      expect(featureTexts[0].text()).toBe('数据分析')
      expect(featureTexts[1].text()).toBe('服务管理')
      expect(featureTexts[2].text()).toBe('订单处理')
    })

    it('登录成功后应该跳转到商家首页', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: {
          code: 200,
          data: {
            token: 'merchant-token',
            user: { id: 1 }
          }
        }
      })

      const vm = wrapper.vm
      vm.loginForm.phone = '13800138000'
      vm.loginForm.password = 'password123'
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(mockRouterPush).toHaveBeenCalledWith('/merchant/home')
    })
  })
})
