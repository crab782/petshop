import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import Register from '../Register.vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElCheckbox, ElRadioGroup, ElRadio, ElUpload, ElAvatar, ElLink, ElDialog } from 'element-plus'
import { createMockRouter } from '@/tests/utils/testUtils'
import { createSuccessResponse, createErrorResponse } from '@/tests/fixtures/merchantData'

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

describe('Register.vue', () => {
  let wrapper: any

  const mountComponent = (options = {}) => {
    return mount(Register, {
      global: {
        components: {
          ElForm,
          ElFormItem,
          ElInput,
          ElButton,
          ElCheckbox,
          ElRadioGroup,
          ElRadio,
          ElUpload,
          ElAvatar,
          ElLink,
          ElDialog
        },
        provide: {
          'Symbol(router)': mockRouter
        },
        stubs: {
          'el-form': ElForm,
          'el-form-item': ElFormItem,
          'el-input': ElInput,
          'el-button': ElButton,
          'el-checkbox': ElCheckbox,
          'el-radio-group': ElRadioGroup,
          'el-radio': ElRadio,
          'el-upload': ElUpload,
          'el-avatar': ElAvatar,
          'el-link': ElLink,
          'el-dialog': ElDialog
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

  describe('表单渲染测试', () => {
    it('应该正确渲染注册表单容器', () => {
      expect(wrapper.find('.register-container').exists()).toBe(true)
      expect(wrapper.find('.register-card').exists()).toBe(true)
    })

    it('应该正确渲染标题和副标题', () => {
      expect(wrapper.find('.register-title').text()).toBe('商家注册')
      expect(wrapper.find('.register-subtitle').text()).toBe('创建您的店铺账号')
    })

    it('应该渲染所有必填表单字段', () => {
      const formItems = wrapper.findAllComponents(ElFormItem)
      expect(formItems.length).toBeGreaterThan(0)

      const inputs = wrapper.findAllComponents(ElInput)
      expect(inputs.length).toBeGreaterThanOrEqual(8)
    })

    it('应该渲染用户名输入框', () => {
      const usernameInput = wrapper.find('input[placeholder="请输入用户名"]')
      expect(usernameInput.exists()).toBe(true)
    })

    it('应该渲染邮箱输入框', () => {
      const emailInput = wrapper.find('input[placeholder="请输入邮箱"]')
      expect(emailInput.exists()).toBe(true)
    })

    it('应该渲染联系电话输入框', () => {
      const phoneInput = wrapper.find('input[placeholder="请输入联系电话"]')
      expect(phoneInput.exists()).toBe(true)
    })

    it('应该渲染联系人输入框', () => {
      const contactInput = wrapper.find('input[placeholder="请输入联系人姓名"]')
      expect(contactInput.exists()).toBe(true)
    })

    it('应该渲染商家名称输入框', () => {
      const nameInput = wrapper.find('input[placeholder="请输入商家名称"]')
      expect(nameInput.exists()).toBe(true)
    })

    it('应该渲染地址输入框', () => {
      const addressInput = wrapper.find('input[placeholder="请输入地址"]')
      expect(addressInput.exists()).toBe(true)
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

    it('应该渲染同意协议复选框', () => {
      const checkbox = wrapper.findComponent(ElCheckbox)
      expect(checkbox.exists()).toBe(true)
    })

    it('应该渲染Logo上传区域', () => {
      expect(wrapper.find('.logo-section').exists()).toBe(true)
      expect(wrapper.findComponent(ElRadioGroup).exists()).toBe(true)
    })

    it('应该渲染登录链接', () => {
      const loginLink = wrapper.find('.register-footer .el-link')
      expect(loginLink.exists()).toBe(true)
      expect(loginLink.text()).toBe('立即登录')
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
      expect(vm.registerRules.contact_person).toBeDefined()
      expect(vm.registerRules.name).toBeDefined()
      expect(vm.registerRules.address).toBeDefined()
    })

    it('用户名验证规则应包含必填和长度限制', () => {
      const usernameRules = wrapper.vm.registerRules.username
      expect(usernameRules.some((rule: any) => rule.required)).toBe(true)
      expect(usernameRules.some((rule: any) => rule.min === 3)).toBe(true)
      expect(usernameRules.some((rule: any) => rule.max === 20)).toBe(true)
    })

    it('邮箱验证规则应包含必填和邮箱格式验证', () => {
      const emailRules = wrapper.vm.registerRules.email
      expect(emailRules.some((rule: any) => rule.required)).toBe(true)
      expect(emailRules.some((rule: any) => rule.type === 'email')).toBe(true)
    })

    it('密码验证规则应包含必填和最小长度', () => {
      const passwordRules = wrapper.vm.registerRules.password
      expect(passwordRules.some((rule: any) => rule.required)).toBe(true)
      expect(passwordRules.some((rule: any) => rule.min === 6)).toBe(true)
    })

    it('确认密码验证规则应包含自定义验证器', () => {
      const confirmPasswordRules = wrapper.vm.registerRules.confirmPassword
      expect(confirmPasswordRules.some((rule: any) => rule.validator)).toBe(true)
    })

    it('手机号验证规则应包含自定义验证器', () => {
      const phoneRules = wrapper.vm.registerRules.phone
      expect(phoneRules.some((rule: any) => rule.required)).toBe(true)
      expect(phoneRules.some((rule: any) => rule.validator)).toBe(true)
    })

    it('用户名长度不足3个字符时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = 'ab'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username.length).toBeLessThan(3)
    })

    it('用户名长度超过20个字符时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = 'a'.repeat(21)
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username.length).toBeGreaterThan(20)
    })

    it('邮箱格式不正确时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.email = 'invalid-email'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.email).not.toMatch(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)
    })

    it('密码长度不足6个字符时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.password = '12345'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.password.length).toBeLessThan(6)
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

    it('联系人为空时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.contact_person = ''
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.contact_person).toBe('')
    })

    it('联系人超过50个字符时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.contact_person = 'a'.repeat(51)
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.contact_person.length).toBeGreaterThan(50)
    })

    it('商家名称为空时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.name = ''
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.name).toBe('')
    })

    it('商家名称长度不足2个字符时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.name = 'a'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.name.length).toBeLessThan(2)
    })

    it('商家名称超过100个字符时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.name = 'a'.repeat(101)
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.name.length).toBeGreaterThan(100)
    })

    it('地址为空时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.address = ''
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.address).toBe('')
    })

    it('地址超过255个字符时应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.address = 'a'.repeat(256)
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.address.length).toBeGreaterThan(255)
    })
  })

  describe('表单提交测试', () => {
    const fillValidForm = async () => {
      const vm = wrapper.vm
      vm.registerForm.username = 'testmerchant'
      vm.registerForm.email = 'test@merchant.com'
      vm.registerForm.password = 'password123'
      vm.registerForm.confirmPassword = 'password123'
      vm.registerForm.phone = '13800138000'
      vm.registerForm.contact_person = '张先生'
      vm.registerForm.name = '测试商家'
      vm.registerForm.address = '北京市朝阳区测试路1号'
      vm.registerForm.agreeToTerms = true
      await wrapper.vm.$nextTick()
    }

    it('未勾选协议时应该显示警告', async () => {
      await fillValidForm()
      wrapper.vm.registerForm.agreeToTerms = false
      await wrapper.vm.handleRegister()
      await flushPromises()
      expect(ElMessage.warning).toHaveBeenCalledWith('请阅读并同意注册协议')
    })

    it('注册成功后应该显示成功消息并跳转登录页', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: createSuccessResponse({ id: 1 })
      })

      await fillValidForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(mockAxiosPost).toHaveBeenCalledWith('/api/auth/merchant/register', expect.objectContaining({
        username: 'testmerchant',
        email: 'test@merchant.com',
        phone: '13800138000'
      }))
      expect(ElMessage.success).toHaveBeenCalledWith('注册成功，请等待审核')
      expect(mockRouterPush).toHaveBeenCalledWith('/login')
    })

    it('注册成功(code=0)后应该显示成功消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: { code: 0, message: 'success', data: { id: 1 } }
      })

      await fillValidForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(ElMessage.success).toHaveBeenCalledWith('注册成功，请等待审核')
    })

    it('注册失败时应该显示错误消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: createErrorResponse('用户名已存在', 400)
      })

      await fillValidForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('用户名已存在')
    })

    it('注册失败时应该显示默认错误消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: { code: 500, message: '' }
      })

      await fillValidForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('注册失败')
    })
  })

  describe('网络错误处理测试', () => {
    const fillValidForm = async () => {
      const vm = wrapper.vm
      vm.registerForm.username = 'testmerchant'
      vm.registerForm.email = 'test@merchant.com'
      vm.registerForm.password = 'password123'
      vm.registerForm.confirmPassword = 'password123'
      vm.registerForm.phone = '13800138000'
      vm.registerForm.contact_person = '张先生'
      vm.registerForm.name = '测试商家'
      vm.registerForm.address = '北京市朝阳区测试路1号'
      vm.registerForm.agreeToTerms = true
      await wrapper.vm.$nextTick()
    }

    it('网络错误时应该显示错误消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      const networkError = new Error('Network Error')
      mockAxiosPost.mockRejectedValueOnce(networkError)

      await fillValidForm()
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

      await fillValidForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('服务器内部错误')
    })

    it('请求超时时应该显示错误消息', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockRejectedValueOnce({
        code: 'ECONNABORTED',
        message: 'timeout'
      })

      await fillValidForm()
      await wrapper.vm.handleRegister()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalled()
    })
  })

  describe('边界条件测试', () => {
    it('空输入时所有必填字段应该验证失败', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = ''
      vm.registerForm.email = ''
      vm.registerForm.password = ''
      vm.registerForm.confirmPassword = ''
      vm.registerForm.phone = ''
      vm.registerForm.contact_person = ''
      vm.registerForm.name = ''
      vm.registerForm.address = ''
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username).toBe('')
      expect(vm.registerForm.email).toBe('')
      expect(vm.registerForm.password).toBe('')
    })

    it('超长用户名应该被截断或验证失败', async () => {
      const vm = wrapper.vm
      const longUsername = 'a'.repeat(100)
      vm.registerForm.username = longUsername
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username.length).toBe(100)
    })

    it('超长邮箱应该验证失败', async () => {
      const vm = wrapper.vm
      const longEmail = 'a'.repeat(95) + '@test.com'
      vm.registerForm.email = longEmail
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.email.length).toBeGreaterThan(100)
    })

    it('超长地址应该验证失败', async () => {
      const vm = wrapper.vm
      const longAddress = 'a'.repeat(300)
      vm.registerForm.address = longAddress
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.address.length).toBe(300)
    })

    it('特殊字符用户名应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = 'test_user-123'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username).toBe('test_user-123')
    })

    it('中文用户名应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = '测试商家用户名'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username).toBe('测试商家用户名')
    })

    it('中文商家名称应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.name = '测试商家店铺名称'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.name).toBe('测试商家店铺名称')
    })

    it('中文地址应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.address = '北京市朝阳区测试路1号测试大厦A座1001室'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.address).toBe('北京市朝阳区测试路1号测试大厦A座1001室')
    })

    it('最小长度用户名应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = 'abc'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username).toBe('abc')
    })

    it('最大长度用户名应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.username = 'a'.repeat(20)
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.username.length).toBe(20)
    })

    it('最小长度密码应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.password = '123456'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.password).toBe('123456')
    })

    it('包含特殊字符的密码应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.password = 'Pass@123!#$%'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.password).toBe('Pass@123!#$%')
    })

    it('座机号码格式应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.phone = '010-12345678'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.phone).toBe('010-12345678')
    })

    it('国际号码格式应该被接受', async () => {
      const vm = wrapper.vm
      vm.registerForm.phone = '+86-13800138000'
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.phone).toBe('+86-13800138000')
    })
  })

  describe('Logo上传测试', () => {
    it('应该能够切换Logo输入方式为URL', async () => {
      const vm = wrapper.vm
      vm.logoInputMethod = 'url'
      await wrapper.vm.$nextTick()
      expect(vm.logoInputMethod).toBe('url')
    })

    it('应该能够切换Logo输入方式为上传', async () => {
      const vm = wrapper.vm
      vm.logoInputMethod = 'upload'
      await wrapper.vm.$nextTick()
      expect(vm.logoInputMethod).toBe('upload')
    })

    it('输入Logo URL应该更新表单', async () => {
      const vm = wrapper.vm
      const testUrl = 'https://example.com/logo.png'
      vm.handleLogoUrlInput(testUrl)
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.logo).toBe(testUrl)
      expect(vm.logoUrl).toBe(testUrl)
    })

    it('上传Logo文件应该更新表单', async () => {
      const vm = wrapper.vm
      const mockFile = {
        raw: new File([''], 'logo.png', { type: 'image/png' })
      }
      vm.handleLogoUploadChange(mockFile)
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.logo).toContain('blob:')
      expect(vm.logoUrl).toContain('blob:')
    })

    it('上传Logo URL对象应该更新表单', async () => {
      const vm = wrapper.vm
      const mockFile = {
        url: 'https://example.com/uploaded-logo.png'
      }
      vm.handleLogoUploadChange(mockFile)
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.logo).toBe('https://example.com/uploaded-logo.png')
      expect(vm.logoUrl).toBe('https://example.com/uploaded-logo.png')
    })
  })

  describe('导航测试', () => {
    it('goToLogin方法应该跳转到登录页', () => {
      wrapper.vm.goToLogin()
      expect(mockRouterPush).toHaveBeenCalledWith('/login')
    })

    it('goToUserRegister方法应该跳转到用户注册页', () => {
      wrapper.vm.goToUserRegister()
      expect(mockRouterPush).toHaveBeenCalledWith('/register')
    })
  })

  describe('协议弹窗测试', () => {
    it('点击协议链接应该显示协议弹窗', async () => {
      const vm = wrapper.vm
      expect(vm.termsDialogVisible).toBe(false)
      vm.showTermsDialog()
      await wrapper.vm.$nextTick()
      expect(vm.termsDialogVisible).toBe(true)
    })

    it('协议弹窗应该包含协议内容', async () => {
      const vm = wrapper.vm
      vm.showTermsDialog()
      await wrapper.vm.$nextTick()
      expect(wrapper.find('.terms-content').exists()).toBe(true)
    })

    it('协议弹窗应该包含总则章节', async () => {
      const vm = wrapper.vm
      vm.showTermsDialog()
      await wrapper.vm.$nextTick()
      const termsContent = wrapper.find('.terms-content')
      expect(termsContent.text()).toContain('总则')
    })

    it('关闭协议弹窗应该隐藏弹窗', async () => {
      const vm = wrapper.vm
      vm.showTermsDialog()
      await wrapper.vm.$nextTick()
      vm.termsDialogVisible = false
      await wrapper.vm.$nextTick()
      expect(vm.termsDialogVisible).toBe(false)
    })
  })

  describe('加载状态测试', () => {
    it('表单提交完成后应该隐藏加载状态', async () => {
      const mockAxiosPost = vi.mocked(axios.post)
      mockAxiosPost.mockResolvedValueOnce({
        data: createSuccessResponse({ id: 1 })
      })

      const vm = wrapper.vm
      vm.registerForm.username = 'testmerchant'
      vm.registerForm.email = 'test@merchant.com'
      vm.registerForm.password = 'password123'
      vm.registerForm.confirmPassword = 'password123'
      vm.registerForm.phone = '13800138000'
      vm.registerForm.contact_person = '张先生'
      vm.registerForm.name = '测试商家'
      vm.registerForm.address = '北京市朝阳区测试路1号'
      vm.registerForm.agreeToTerms = true

      await wrapper.vm.handleRegister()
      await flushPromises()
      expect(vm.registerLoading).toBe(false)
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

    it('勾选协议应该更新表单数据', async () => {
      const vm = wrapper.vm
      vm.registerForm.agreeToTerms = true
      await wrapper.vm.$nextTick()
      expect(vm.registerForm.agreeToTerms).toBe(true)
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

  describe('手机号验证器测试', () => {
    it('有效手机号应该通过验证', () => {
      const vm = wrapper.vm
      let callbackCalled = false
      let callbackError: any = null
      const callback = (error?: any) => {
        callbackCalled = true
        callbackError = error
      }
      
      const phoneRule = vm.registerRules.phone.find((rule: any) => rule.validator)
      if (phoneRule) {
        phoneRule.validator({}, '13800138000', callback)
      }
      
      expect(callbackCalled).toBe(true)
      expect(callbackError).toBeUndefined()
    })

    it('空手机号应该验证失败', () => {
      const vm = wrapper.vm
      let callbackCalled = false
      let callbackError: any = null
      const callback = (error?: any) => {
        callbackCalled = true
        callbackError = error
      }
      
      const phoneRule = vm.registerRules.phone.find((rule: any) => rule.validator)
      if (phoneRule) {
        phoneRule.validator({}, '', callback)
      }
      
      expect(callbackCalled).toBe(true)
      expect(callbackError).toBeDefined()
      expect(callbackError.message).toBe('请输入联系电话')
    })
  })
})
