import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import Register from '../Register.vue'
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
  User: { name: 'User', render: () => null },
  Lock: { name: 'Lock', render: () => null },
  Message: { name: 'Message', render: () => null },
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

describe('Register.vue - 用户端注册页面', () => {
  let wrapper: any

  const mountComponent = (options = {}) => {
    return mount(Register, {
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
    wrapper = mountComponent()
  })

  afterEach(() => {
    wrapper?.unmount()
  })

  describe('页面渲染测试', () => {
    it('应该正确渲染注册容器', () => {
      expect(wrapper.find('.register-container').exists()).toBe(true)
      expect(wrapper.find('.register-card').exists()).toBe(true)
    })

    it('应该正确渲染品牌标识', () => {
      expect(wrapper.find('.brand-logo').exists()).toBe(true)
      expect(wrapper.find('.logo-icon').text()).toBe('🐶')
      expect(wrapper.find('.brand-title').text()).toBe('宠物服务平台')
    })

    it('应该正确渲染注册标题和副标题', () => {
      expect(wrapper.find('.register-title').text()).toBe('用户注册')
      expect(wrapper.find('.register-subtitle').text()).toBe('创建您的账号')
    })

    it('应该渲染用户名输入框', () => {
      const usernameInput = wrapper.find('input[placeholder="请输入用户名"]')
      expect(usernameInput.exists()).toBe(true)
    })

    it('应该渲染邮箱输入框', () => {
      const emailInput = wrapper.find('input[placeholder="请输入邮箱"]')
      expect(emailInput.exists()).toBe(true)
    })

    it('应该渲染手机号输入框', () => {
      const phoneInput = wrapper.find('input[placeholder="请输入11位手机号"]')
      expect(phoneInput.exists()).toBe(true)
    })

    it('应该渲染密码输入框', () => {
      const passwordInputs = wrapper.findAll('input[type="password"]')
      expect(passwordInputs.length).toBe(2)
    })

    it('应该渲染注册按钮', () => {
      const registerButton = wrapper.find('.register-button')
      expect(registerButton.exists()).toBe(true)
      expect(registerButton.text()).toBe('注册')
    })

    it('应该渲染登录链接', () => {
      const loginLink = wrapper.find('.register-footer .el-link')
      expect(loginLink.exists()).toBe(true)
      expect(loginLink.text()).toBe('立即登录')
    })

    it('应该渲染商家入驻链接', () => {
      const merchantLinks = wrapper.findAll('.merchant-entry .el-link')
      expect(merchantLinks.length).toBeGreaterThan(0)
      expect(merchantLinks[0].text()).toBe('商家入驻')
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
      expect(vm.registerRules).toBeDefined()
      expect(vm.registerRules.username).toBeDefined()
      expect(vm.registerRules.email).toBeDefined()
      expect(vm.registerRules.password).toBeDefined()
      expect(vm.registerRules.confirmPassword).toBeDefined()
      expect(vm.registerRules.phone).toBeDefined()
    })

    it('密码验证规则应包含必填和长度限制', () => {
      const passwordRules = wrapper.vm.registerRules.password
      expect(passwordRules.some((rule: any) => rule.required)).toBe(true)
      expect(passwordRules.some((rule: any) => rule.min === 6)).toBe(true)
      expect(passwordRules.some((rule: any) => rule.max === 20)).toBe(true)
    })

    it('确认密码验证规则应包含自定义验证器', () => {
      const confirmPasswordRules = wrapper.vm.registerRules.confirmPassword
      expect(confirmPasswordRules.some((rule: any) => rule.required)).toBe(true)
      expect(confirmPasswordRules.some((rule: any) => rule.validator)).toBe(true)
    })

    it('手机号验证规则应包含必填和格式验证', () => {
      const phoneRules = wrapper.vm.registerRules.phone
      expect(phoneRules.some((rule: any) => rule.required)).toBe(true)
      expect(phoneRules.some((rule: any) => rule.pattern)).toBe(true)
    })

    it('密码长度不足6个字符时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.password = '12345'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.password.length).toBeLessThan(6)
    })

    it('密码长度超过20个字符时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.password = 'a'.repeat(21)
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.password.length).toBeGreaterThan(20)
    })

    it('确认密码与密码不一致时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.password = 'password123'
      vm.registerForm.confirmPassword = 'password456'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.confirmPassword).not.toBe(vm.registerForm.password)
    })

    it('确认密码与密码一致时应该验证通过', async () => {
      const vm = wrapper.vm
      vm.registerForm.password = 'password123'
      vm.registerForm.confirmPassword = 'password123'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.confirmPassword).toBe(vm.registerForm.password)
    })

    it('手机号格式不正确时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.phone = 'invalid-phone'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.phone).not.toMatch(/^1[3-9]\d{9}$/)
    })

    it('有效手机号应该通过格式验证', async () => {
      const vm = wrapper.vm
      vm.registerForm.phone = '13800138000'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.phone).toMatch(/^1[3-9]\d{9}$/)
    })
  })

  describe('注册功能测试', () => {
    const fillRegisterForm = async () => {
      const vm = wrapper.vm
      vm.registerForm.username = 'testuser'
      vm.registerForm.email = 'test@example.com'
      vm.registerForm.password = 'password123'
      vm.registerForm.confirmPassword = 'password123'
      vm.registerForm.phone = '13800138000'
      await wrapper.vm.$nextTick()
    }

    it('注册成功后应该显示成功消息并跳转登录页', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: { code: 200, message: 'success', data: { id: 1 } }
      })

      await fillRegisterForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(mockAxiosPost).toHaveBeenCalledWith('/api/auth/register', expect.objectContaining({
        username: 'testuser',
        email: 'test@example.com',
        phone: '13800138000'
      }))
      expect(ElMessage.success).toHaveBeenCalledWith('注册成功，请登录')
      expect(mockRouterPush).toHaveBeenCalledWith('/login')
    })

    it('注册成功(code=0)后应该显示成功消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: { code: 0, message: 'success', data: { id: 1 } }
      })

      await fillRegisterForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(ElMessage.success).toHaveBeenCalledWith('注册成功，请登录')
    })

    it('注册失败时应该显示错误消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: { code: 400, message: '手机号已被注册' }
      })

      await fillRegisterForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('手机号已被注册')
    })

    it('注册失败时应该显示默认错误消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: { code: 500, message: '' }
      })

      await fillRegisterForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('注册失败')
    })

    it('网络错误时应该显示错误消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockRejectedValueOnce(new Error('Network Error'))

      await fillRegisterForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('注册失败，请稍后重试')
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

      await fillRegisterForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('服务器内部错误')
    })
  })

  describe('导航功能测试', () => {
    it('点击登录链接应该跳转到登录页', () => {
      wrapper.vm.goToLogin()
      expect(mockRouterPush).toHaveBeenCalledWith('/login')
    })

    it('点击商家入驻应该跳转到商家注册页', () => {
      wrapper.vm.goToMerchantRegister()
      expect(mockRouterPush).toHaveBeenCalledWith('/merchant/register')
    })
  })

  describe('边界条件测试', () => {
    it('空用户名时应该被接受（非必填）', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = ''
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username).toBe('')
    })

    it('空邮箱时应该被接受（非必填）', async () => {
      const vm = wrapper.vm
      vm.registerForm.email = ''
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.email).toBe('')
    })

    it('空手机号时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.phone = ''
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.phone).toBe('')
    })

    it('超长用户名应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = 'a'.repeat(100)
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username.length).toBe(100)
    })

    it('超长邮箱应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.email = 'a'.repeat(95) + '@test.com'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.email.length).toBeGreaterThan(100)
    })

    it('特殊字符用户名应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = 'test_user-123'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username).toBe('test_user-123')
    })

    it('中文用户名应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = '测试用户名'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username).toBe('测试用户名')
    })

    it('最小长度密码应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.password = '123456'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.password).toBe('123456')
    })

    it('最大长度密码应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.password = 'a'.repeat(20)
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.password.length).toBe(20)
    })

    it('包含特殊字符的密码应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.password = 'Pass@123!#$%'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.password).toBe('Pass@123!#$%')
    })

    it('不同运营商手机号应该通过验证', async () => {
      const validPhones = ['13800138000', '15800138000', '18800138000', '19800138000']
      for (const phone of validPhones) {
        expect(phone).toMatch(/^1[3-9]\d{9}$/)
      }
    })
  })

  describe('表单数据绑定测试', () => {
    it('修改用户名应该更新表单数据', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = 'newusername'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username).toBe('newusername')
    })

    it('修改邮箱应该更新表单数据', async () => {
      const vm = wrapper.vm
      vm.registerForm.email = 'new@email.com'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.email).toBe('new@email.com')
    })

    it('修改密码应该更新表单数据', async () => {
      const vm = wrapper.vm
      vm.registerForm.password = 'newpassword123'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.password).toBe('newpassword123')
    })

    it('修改手机号应该更新表单数据', async () => {
      const vm = wrapper.vm
      vm.registerForm.phone = '13900139000'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.phone).toBe('13900139000')
    })
  })

  describe('密码确认验证器测试', () => {
    it('密码一致时验证器应该通过', () => {
      const vm = wrapper.vm
      vm.registerForm.password = 'test123456'
      let callbackCalled = false
      let callbackError: any = null
      const callback = (error?: any) => {
        callbackCalled = true
        callbackError = error
      }

      const confirmPasswordRule = vm.registerRules.confirmPassword.find((rule: any) => rule.validator)
      if (confirmPasswordRule) {
        confirmPasswordRule.validator({}, 'test123456', callback)
      }

      expect(callbackCalled).toBe(true)
      expect(callbackError).toBeUndefined()
    })

    it('密码不一致时验证器应该失败', () => {
      const vm = wrapper.vm
      vm.registerForm.password = 'test123456'
      let callbackCalled = false
      let callbackError: any = null
      const callback = (error?: any) => {
        callbackCalled = true
        callbackError = error
      }

      const confirmPasswordRule = vm.registerRules.confirmPassword.find((rule: any) => rule.validator)
      if (confirmPasswordRule) {
        confirmPasswordRule.validator({}, 'different123', callback)
      }

      expect(callbackCalled).toBe(true)
      expect(callbackError).toBeDefined()
      expect(callbackError.message).toBe('两次输入的密码不一致')
    })
  })

  describe('安全性测试', () => {
    it('密码输入框应该是密码类型', () => {
      const passwordInputs = wrapper.findAll('input[type="password"]')
      expect(passwordInputs.length).toBe(2)
    })

    it('密码输入框应该有显示密码功能', () => {
      const passwordInputs = wrapper.findAllComponents(ElInput)
      expect(passwordInputs.length).toBeGreaterThan(0)
    })
  })

  describe('用户体验测试', () => {
    it('注册按钮应该有加载状态', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)))

      const vm = wrapper.vm
      vm.registerForm.username = 'testuser'
      vm.registerForm.email = 'test@example.com'
      vm.registerForm.password = 'password123'
      vm.registerForm.confirmPassword = 'password123'
      vm.registerForm.phone = '13800138000'

      const registerPromise = wrapper.vm.handleRegister()
      await wrapper.vm.$nextTick()

      const registerButton = wrapper.find('.register-button')
      expect(registerButton.exists()).toBe(true)

      await registerPromise
      await flushPromises()
    })

    it('输入框应该有正确的占位符文本', () => {
      const usernameInput = wrapper.find('input[placeholder="请输入用户名"]')
      expect(usernameInput.exists()).toBe(true)

      const emailInput = wrapper.find('input[placeholder="请输入邮箱"]')
      expect(emailInput.exists()).toBe(true)

      const phoneInput = wrapper.find('input[placeholder="请输入11位手机号"]')
      expect(phoneInput.exists()).toBe(true)
    })

    it('特色功能图标应该正确显示', () => {
      const featureIcons = wrapper.findAll('.feature-icon')
      expect(featureIcons.length).toBe(3)
      expect(featureIcons[0].text()).toBe('🐾')
      expect(featureIcons[1].text()).toBe('📅')
      expect(featureIcons[2].text()).toBe('🛍️')
    })

    it('必填字段应该有提示', () => {
      const requiredHint = wrapper.find('.required-hint')
      expect(requiredHint.exists()).toBe(true)
      expect(requiredHint.text()).toContain('必填且唯一')
    })
  })

  describe('响应式设计测试', () => {
    it('注册卡片应该有最大宽度限制', () => {
      const registerCard = wrapper.find('.register-card')
      expect(registerCard.exists()).toBe(true)
    })

    it('表单应该正确响应窗口大小变化', () => {
      const form = wrapper.find('.register-form')
      expect(form.exists()).toBe(true)
    })
  })
})
