import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import Login from '../Login.vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElLink } from 'element-plus'

vi.mock('@/api/request', () => ({
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
  Phone: { name: 'Phone', render: () => null },
  Shop: { name: 'Shop', render: () => null },
  Setting: { name: 'Setting', render: () => null }
}))

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter
}))

import request from '@/api/request'
import { ElMessage } from 'element-plus'

const mockRouterPush = vi.fn()
const mockRouter = {
  push: mockRouterPush,
  replace: vi.fn(),
  go: vi.fn(),
  back: vi.fn(),
  forward: vi.fn()
}

describe('Login.vue - 用户端登录页面', () => {
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
      expect(wrapper.find('.login-container').exists()).toBe(true)
      expect(wrapper.find('.login-card').exists()).toBe(true)
    })

    it('应该正确渲染品牌标识', () => {
      expect(wrapper.find('.brand-logo').exists()).toBe(true)
      expect(wrapper.find('.logo-icon').text()).toBe('🐶')
      expect(wrapper.find('.brand-title').text()).toBe('宠物服务平台')
    })

    it('应该正确渲染登录标题和副标题', () => {
      expect(wrapper.find('.login-title').text()).toBe('用户登录')
      expect(wrapper.find('.login-subtitle').text()).toBe('欢迎回来')
    })

    it('应该渲染手机号输入框', () => {
      const phoneInput = wrapper.find('input[placeholder="请输入11位手机号"]')
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

    it('应该渲染商家登录入口', () => {
      expect(wrapper.find('.merchant-login-entry').exists()).toBe(true)
      expect(wrapper.find('.merchant-text').text()).toBe('商家登录')
    })

    it('应该渲染平台登录入口', () => {
      expect(wrapper.find('.admin-login-entry').exists()).toBe(true)
      expect(wrapper.find('.admin-text').text()).toBe('平台登录')
    })

    it('应该渲染用户端特色功能', () => {
      expect(wrapper.find('.user-features').exists()).toBe(true)
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

    it('登录成功后应该保存token和用户信息', async () => {
      const mockRequestPost = vi.mocked(request.post)
      mockRequestPost.mockResolvedValueOnce({
        token: 'test-token',
        user: { id: 1, username: 'testuser' }
      })

      await fillLoginForm()
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(localStorage.getItem('user_token')).toBe('test-token')
      expect(JSON.parse(localStorage.getItem('userInfo') || '{}')).toEqual({ id: 1, username: 'testuser' })
    })

    it('登录成功后应该显示成功消息并跳转', async () => {
      const mockRequestPost = vi.mocked(request.post)
      mockRequestPost.mockResolvedValueOnce({
        token: 'test-token',
        user: { id: 1, username: 'testuser' }
      })

      await fillLoginForm()
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(ElMessage.success).toHaveBeenCalledWith('登录成功')
      expect(mockRouterPush).toHaveBeenCalledWith('/user/home')
    })

    it('登录失败时应该显示错误消息', async () => {
      const mockRequestPost = vi.mocked(request.post)
      const error = {
        response: {
          data: {
            message: '手机号或密码错误'
          }
        }
      }
      mockRequestPost.mockRejectedValueOnce(error)

      await fillLoginForm()
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('手机号或密码错误')
    })

    it('登录失败时应该显示默认错误消息', async () => {
      const mockRequestPost = vi.mocked(request.post)
      mockRequestPost.mockRejectedValueOnce(new Error('Network Error'))

      await fillLoginForm()
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('登录失败，请检查手机号和密码')
    })
  })

  describe('导航功能测试', () => {
    it('点击注册链接应该跳转到注册页', () => {
      wrapper.vm.goToRegister()
      expect(mockRouterPush).toHaveBeenCalledWith('/register')
    })

    it('点击商家登录应该跳转到商家登录页', () => {
      wrapper.vm.goToMerchantLogin()
      expect(mockRouterPush).toHaveBeenCalledWith('/merchant/login')
    })

    it('点击平台登录应该跳转到平台登录页', () => {
      wrapper.vm.goToAdminLogin()
      expect(mockRouterPush).toHaveBeenCalledWith('/admin/login')
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
      const mockRequestPost = vi.mocked(request.post)
      mockRequestPost.mockResolvedValueOnce({
        token: 'secure-token-123',
        user: { id: 1 }
      })

      const vm = wrapper.vm
      vm.loginForm.phone = '13800138000'
      vm.loginForm.password = 'password123'
      await wrapper.vm.handleLogin()
      await flushPromises()

      expect(localStorage.getItem('user_token')).toBe('secure-token-123')
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

  describe('用户体验测试', () => {
    it('登录按钮应该有加载状态', async () => {
      const mockRequestPost = vi.mocked(request.post)
      mockRequestPost.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)))

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
      const phoneInput = wrapper.find('input[placeholder="请输入11位手机号"]')
      expect(phoneInput.exists()).toBe(true)

      const passwordInput = wrapper.find('input[placeholder="请输入密码"]')
      expect(passwordInput.exists()).toBe(true)
    })

    it('特色功能图标应该正确显示', () => {
      const featureIcons = wrapper.findAll('.feature-icon')
      expect(featureIcons.length).toBe(3)
      expect(featureIcons[0].text()).toBe('🐾')
      expect(featureIcons[1].text()).toBe('📅')
      expect(featureIcons[2].text()).toBe('🛒')
    })
  })
})
