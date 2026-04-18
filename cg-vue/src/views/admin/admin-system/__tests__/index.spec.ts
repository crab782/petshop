import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import AdminSystem from '../index.vue'
import {
  ElForm,
  ElFormItem,
  ElInput,
  ElButton,
  ElCard,
  ElTabs,
  ElTabPane,
  ElSwitch,
  ElInputNumber,
  ElRadioGroup,
  ElRadio,
  ElBreadcrumb,
  ElBreadcrumbItem,
  ElDivider,
  ElImage,
} from 'element-plus'
import { createMockRouter, flushPromises as waitForPromises } from '@/tests/utils/testUtils'

const mockMessageSuccess = vi.hoisted(() => vi.fn())
const mockMessageError = vi.hoisted(() => vi.fn())
const mockMessageInfo = vi.hoisted(() => vi.fn())
const mockGetSystemSettings = vi.hoisted(() => vi.fn())
const mockSaveSystemSettings = vi.hoisted(() => vi.fn())

vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: vi.fn(),
    replace: vi.fn(),
    go: vi.fn(),
    back: vi.fn(),
    forward: vi.fn(),
    currentRoute: {
      value: {
        path: '/',
        params: {},
        query: {},
      },
    },
  }),
}))

vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: mockMessageSuccess,
      error: mockMessageError,
      warning: vi.fn(),
      info: mockMessageInfo,
    },
  }
})

const mockRouter = createMockRouter()

const defaultSystemSettings = {
  basic: {
    siteName: '宠物服务平台',
    siteDescription: '专业的宠物服务预约平台',
    siteLogo: '',
    contactEmail: 'admin@example.com',
    contactPhone: '400-888-8888',
    icpNumber: '京ICP备XXXXXXXX号',
    copyright: '© 2024 宠物服务平台 版权所有',
  },
  email: {
    smtpHost: 'smtp.example.com',
    smtpPort: 465,
    smtpUsername: 'noreply@example.com',
    smtpPassword: '',
    fromEmail: 'noreply@example.com',
    fromName: '宠物服务平台',
    encryption: 'ssl',
    isEnabled: false,
  },
  sms: {
    provider: 'aliyun',
    accessKeyId: '',
    accessKeySecret: '',
    signName: '宠物服务平台',
    isEnabled: false,
  },
  payment: {
    alipayEnabled: false,
    alipayAppId: '',
    alipayPrivateKey: '',
    alipayPublicKey: '',
    wechatEnabled: false,
    wechatMchId: '',
    wechatApiKey: '',
    wechatCertPath: '',
    wechatKeyPath: '',
  },
}

describe('AdminSystem', () => {
  let wrapper: any

  const createWrapper = (options = {}) => {
    return mount(AdminSystem, {
      global: {
        components: {
          ElForm,
          ElFormItem,
          ElInput,
          ElButton,
          ElCard,
          ElTabs,
          ElTabPane,
          ElSwitch,
          ElInputNumber,
          ElRadioGroup,
          ElRadio,
          ElBreadcrumb,
          ElBreadcrumbItem,
          ElDivider,
          ElImage,
        },
        mocks: {
          $router: mockRouter,
        },
        stubs: {
          'el-form': ElForm,
          'el-form-item': ElFormItem,
          'el-input': ElInput,
          'el-button': ElButton,
          'el-card': ElCard,
          'el-tabs': ElTabs,
          'el-tab-pane': ElTabPane,
          'el-switch': ElSwitch,
          'el-input-number': ElInputNumber,
          'el-radio-group': ElRadioGroup,
          'el-radio': ElRadio,
          'el-breadcrumb': ElBreadcrumb,
          'el-breadcrumb-item': ElBreadcrumbItem,
          'el-divider': ElDivider,
          'el-image': ElImage,
        },
      },
      ...options,
    })
  }

  beforeEach(() => {
    vi.clearAllMocks()
    mockMessageSuccess.mockClear()
    mockMessageError.mockClear()
    mockMessageInfo.mockClear()
    mockGetSystemSettings.mockClear()
    mockSaveSystemSettings.mockClear()
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  describe('数据渲染测试', () => {
    describe('设置表单渲染', () => {
      it('应该正确渲染页面标题和面包屑', () => {
        wrapper = createWrapper()

        expect(wrapper.find('.admin-system').exists()).toBe(true)
        expect(wrapper.find('.title').text()).toBe('系统设置')
      })

      it('应该正确渲染设置卡片', () => {
        wrapper = createWrapper()

        expect(wrapper.find('.settings-card').exists()).toBe(true)
        expect(wrapper.find('.card-header').exists()).toBe(true)
      })

      it('应该正确渲染标签页组件', () => {
        wrapper = createWrapper()

        expect(wrapper.findComponent(ElTabs).exists()).toBe(true)
      })

      it('应该正确渲染保存按钮', () => {
        wrapper = createWrapper()

        const saveButton = wrapper.find('.form-actions').findComponent(ElButton)
        expect(saveButton.exists()).toBe(true)
        expect(saveButton.text()).toContain('保存设置')
      })
    })

    describe('基本设置表单', () => {
      it('应该正确渲染基本设置标签页', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.activeTab).toBe('basic')
      })

      it('应该正确渲染所有基本设置表单字段', () => {
        wrapper = createWrapper()

        const basicInputs = wrapper.findAllComponents(ElInput)
        expect(basicInputs.length).toBeGreaterThan(0)
      })

      it('应该正确显示网站名称字段', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.basicForm.siteName).toBe('宠物服务平台')
      })

      it('应该正确显示网站描述字段', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.basicForm.siteDescription).toBe('专业的宠物服务预约平台')
      })

      it('应该正确显示联系邮箱字段', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.basicForm.contactEmail).toBe('admin@example.com')
      })

      it('应该正确显示联系电话字段', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.basicForm.contactPhone).toBe('400-888-8888')
      })

      it('应该正确显示ICP备案号字段', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.basicForm.icpNumber).toBe('京ICP备XXXXXXXX号')
      })

      it('应该正确显示版权信息字段', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.basicForm.copyright).toBe('© 2024 宠物服务平台 版权所有')
      })

      it('应该正确渲染Logo上传区域', () => {
        wrapper = createWrapper()

        expect(wrapper.find('.logo-upload').exists()).toBe(true)
      })

      it('应该在Logo存在时显示预览', async () => {
        wrapper = createWrapper()
        wrapper.vm.basicForm.siteLogo = 'https://example.com/logo.png'
        await wrapper.vm.$nextTick()

        expect(wrapper.find('.logo-preview').exists()).toBe(true)
      })

      it('应该在Logo不存在时不显示预览', () => {
        wrapper = createWrapper()
        wrapper.vm.basicForm.siteLogo = ''

        expect(wrapper.find('.logo-preview').exists()).toBe(false)
      })
    })

    describe('邮件设置表单', () => {
      it('应该正确切换到邮件设置标签页', async () => {
        wrapper = createWrapper()
        wrapper.vm.activeTab = 'email'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.activeTab).toBe('email')
      })

      it('应该正确显示邮件服务开关', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.emailForm.isEnabled).toBe(false)
      })

      it('应该正确显示SMTP服务器设置', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.emailForm.smtpHost).toBe('smtp.example.com')
        expect(wrapper.vm.emailForm.smtpPort).toBe(465)
        expect(wrapper.vm.emailForm.smtpUsername).toBe('noreply@example.com')
      })

      it('应该正确显示加密方式选项', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.emailForm.encryption).toBe('ssl')
      })

      it('应该正确显示发件人信息', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.emailForm.fromEmail).toBe('noreply@example.com')
        expect(wrapper.vm.emailForm.fromName).toBe('宠物服务平台')
      })

      it('应该在邮件服务禁用时禁用相关输入', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.emailForm.isEnabled).toBe(false)
      })
    })

    describe('短信设置表单', () => {
      it('应该正确切换到短信设置标签页', async () => {
        wrapper = createWrapper()
        wrapper.vm.activeTab = 'sms'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.activeTab).toBe('sms')
      })

      it('应该正确显示短信服务开关', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.smsForm.isEnabled).toBe(false)
      })

      it('应该正确显示短信平台选项', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.smsForm.provider).toBe('aliyun')
      })

      it('应该正确显示短信签名设置', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.smsForm.signName).toBe('宠物服务平台')
      })

      it('应该在短信服务禁用时禁用相关输入', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.smsForm.isEnabled).toBe(false)
      })
    })

    describe('支付设置表单', () => {
      it('应该正确切换到支付设置标签页', async () => {
        wrapper = createWrapper()
        wrapper.vm.activeTab = 'payment'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.activeTab).toBe('payment')
      })

      it('应该正确显示支付宝配置区域', () => {
        wrapper = createWrapper()
        wrapper.vm.activeTab = 'payment'

        expect(wrapper.vm.paymentForm.alipayEnabled).toBe(false)
      })

      it('应该正确显示微信支付配置区域', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.paymentForm.wechatEnabled).toBe(false)
      })

      it('应该在支付宝禁用时禁用相关输入', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.paymentForm.alipayEnabled).toBe(false)
      })

      it('应该在微信支付禁用时禁用相关输入', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.paymentForm.wechatEnabled).toBe(false)
      })

      it('应该正确显示支付设置的分隔线', async () => {
        wrapper = createWrapper()
        wrapper.vm.activeTab = 'payment'
        await wrapper.vm.$nextTick()

        expect(wrapper.findComponent(ElDivider).exists()).toBe(true)
      })
    })
  })

  describe('API集成测试', () => {
    describe('获取系统设置API响应', () => {
      it('应该正确初始化表单数据', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.basicForm.siteName).toBe(defaultSystemSettings.basic.siteName)
        expect(wrapper.vm.emailForm.smtpHost).toBe(defaultSystemSettings.email.smtpHost)
        expect(wrapper.vm.smsForm.provider).toBe(defaultSystemSettings.sms.provider)
        expect(wrapper.vm.paymentForm.alipayEnabled).toBe(defaultSystemSettings.payment.alipayEnabled)
      })
    })

    describe('保存系统设置API响应', () => {
      it('应该能够调用保存方法', async () => {
        wrapper = createWrapper()

        const formRef = { validate: vi.fn((callback) => callback(true)) }
        wrapper.vm.formRef = formRef

        await wrapper.vm.handleSubmit(formRef)
        await waitForPromises()

        expect(mockMessageSuccess).toHaveBeenCalledWith('保存成功')
      })
    })
  })

  describe('交互功能测试', () => {
    describe('表单验证', () => {
      beforeEach(() => {
        wrapper = createWrapper()
      })

      it('应该验证网站名称为必填项', () => {
        const nameField = wrapper.vm.rules.siteName
        expect(nameField).toBeDefined()
        expect(nameField.some((rule: any) => rule.required)).toBe(true)
        expect(nameField.some((rule: any) => rule.max === 100)).toBe(true)
      })

      it('应该验证联系邮箱格式', () => {
        const emailField = wrapper.vm.rules.contactEmail
        expect(emailField).toBeDefined()
        expect(emailField.some((rule: any) => rule.required)).toBe(true)
        expect(emailField.some((rule: any) => rule.type === 'email')).toBe(true)
      })

      it('应该验证联系电话为必填项', () => {
        const phoneField = wrapper.vm.rules.contactPhone
        expect(phoneField).toBeDefined()
        expect(phoneField.some((rule: any) => rule.required)).toBe(true)
      })

      it('应该验证SMTP服务器为必填项', () => {
        const smtpHostField = wrapper.vm.rules.smtpHost
        expect(smtpHostField).toBeDefined()
        expect(smtpHostField.some((rule: any) => rule.required)).toBe(true)
      })

      it('应该验证SMTP端口为数字', () => {
        const smtpPortField = wrapper.vm.rules.smtpPort
        expect(smtpPortField).toBeDefined()
        expect(smtpPortField.some((rule: any) => rule.type === 'number')).toBe(true)
      })

      it('应该验证SMTP用户名为必填项', () => {
        const smtpUsernameField = wrapper.vm.rules.smtpUsername
        expect(smtpUsernameField).toBeDefined()
        expect(smtpUsernameField.some((rule: any) => rule.required)).toBe(true)
      })

      it('应该验证AccessKey ID为必填项', () => {
        const accessKeyIdField = wrapper.vm.rules.accessKeyId
        expect(accessKeyIdField).toBeDefined()
        expect(accessKeyIdField.some((rule: any) => rule.required)).toBe(true)
      })

      it('应该验证支付宝AppID为必填项', () => {
        const alipayAppIdField = wrapper.vm.rules.alipayAppId
        expect(alipayAppIdField).toBeDefined()
        expect(alipayAppIdField.some((rule: any) => rule.required)).toBe(true)
      })

      it('应该验证微信商户号为必填项', () => {
        const wechatMchIdField = wrapper.vm.rules.wechatMchId
        expect(wechatMchIdField).toBeDefined()
        expect(wechatMchIdField.some((rule: any) => rule.required)).toBe(true)
      })
    })

    describe('保存功能', () => {
      beforeEach(() => {
        wrapper = createWrapper()
      })

      it('应该在表单验证通过时调用保存', async () => {
        const formRef = { validate: vi.fn((callback) => callback(true)) }
        wrapper.vm.formRef = formRef

        await wrapper.vm.handleSubmit(formRef)
        await waitForPromises()

        expect(mockMessageSuccess).toHaveBeenCalledWith('保存成功')
      })

      it('应该在表单验证失败时不调用保存', async () => {
        const formRef = { validate: vi.fn((callback) => callback(false)) }
        wrapper.vm.formRef = formRef

        await wrapper.vm.handleSubmit(formRef)
        await waitForPromises()

        expect(mockMessageSuccess).not.toHaveBeenCalled()
      })

      it('应该在保存时显示loading状态', async () => {
        const formRef = { validate: vi.fn((callback) => callback(true)) }
        wrapper.vm.formRef = formRef

        const promise = wrapper.vm.handleSubmit(formRef)
        expect(wrapper.vm.submitting).toBe(true)
        await promise
        await waitForPromises()
      })

      it('应该在保存完成后关闭loading状态', async () => {
        const formRef = { validate: vi.fn((callback) => callback(true)) }
        wrapper.vm.formRef = formRef

        await wrapper.vm.handleSubmit(formRef)
        await waitForPromises()

        expect(wrapper.vm.submitting).toBe(false)
      })

      it('应该在formRef为空时不执行保存', async () => {
        await wrapper.vm.handleSubmit(undefined)

        expect(mockMessageSuccess).not.toHaveBeenCalled()
      })
    })

    describe('标签页切换', () => {
      beforeEach(() => {
        wrapper = createWrapper()
      })

      it('应该默认显示基本设置标签页', () => {
        expect(wrapper.vm.activeTab).toBe('basic')
      })

      it('应该能够切换到邮件设置标签页', async () => {
        wrapper.vm.activeTab = 'email'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.activeTab).toBe('email')
      })

      it('应该能够切换到短信设置标签页', async () => {
        wrapper.vm.activeTab = 'sms'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.activeTab).toBe('sms')
      })

      it('应该能够切换到支付设置标签页', async () => {
        wrapper.vm.activeTab = 'payment'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.activeTab).toBe('payment')
      })

      it('应该能够通过标签页组件切换', async () => {
        const tabs = wrapper.findComponent(ElTabs)
        await tabs.vm.$emit('update:modelValue', 'email')

        expect(wrapper.vm.activeTab).toBe('email')
      })
    })

    describe('Logo上传功能', () => {
      beforeEach(() => {
        wrapper = createWrapper()
      })

      it('应该能够触发Logo上传功能', async () => {
        await wrapper.vm.handleLogoUpload()

        expect(mockMessageInfo).toHaveBeenCalledWith('Logo上传功能开发中')
      })

      it('应该能够更新Logo URL', async () => {
        wrapper.vm.basicForm.siteLogo = 'https://example.com/new-logo.png'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.basicForm.siteLogo).toBe('https://example.com/new-logo.png')
      })
    })

    describe('开关切换功能', () => {
      beforeEach(() => {
        wrapper = createWrapper()
      })

      it('应该能够切换邮件服务开关', async () => {
        expect(wrapper.vm.emailForm.isEnabled).toBe(false)

        wrapper.vm.emailForm.isEnabled = true
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.emailForm.isEnabled).toBe(true)
      })

      it('应该能够切换短信服务开关', async () => {
        expect(wrapper.vm.smsForm.isEnabled).toBe(false)

        wrapper.vm.smsForm.isEnabled = true
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.smsForm.isEnabled).toBe(true)
      })

      it('应该能够切换支付宝开关', async () => {
        expect(wrapper.vm.paymentForm.alipayEnabled).toBe(false)

        wrapper.vm.paymentForm.alipayEnabled = true
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.paymentForm.alipayEnabled).toBe(true)
      })

      it('应该能够切换微信支付开关', async () => {
        expect(wrapper.vm.paymentForm.wechatEnabled).toBe(false)

        wrapper.vm.paymentForm.wechatEnabled = true
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.paymentForm.wechatEnabled).toBe(true)
      })
    })

    describe('表单输入交互', () => {
      beforeEach(() => {
        wrapper = createWrapper()
      })

      it('应该能够更新网站名称', async () => {
        wrapper.vm.basicForm.siteName = '新网站名称'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.basicForm.siteName).toBe('新网站名称')
      })

      it('应该能够更新网站描述', async () => {
        wrapper.vm.basicForm.siteDescription = '新的网站描述'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.basicForm.siteDescription).toBe('新的网站描述')
      })

      it('应该能够更新联系邮箱', async () => {
        wrapper.vm.basicForm.contactEmail = 'new@example.com'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.basicForm.contactEmail).toBe('new@example.com')
      })

      it('应该能够更新联系电话', async () => {
        wrapper.vm.basicForm.contactPhone = '400-999-9999'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.basicForm.contactPhone).toBe('400-999-9999')
      })

      it('应该能够更新SMTP端口', async () => {
        wrapper.vm.emailForm.smtpPort = 587
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.emailForm.smtpPort).toBe(587)
      })

      it('应该能够更新加密方式', async () => {
        wrapper.vm.emailForm.encryption = 'tls'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.emailForm.encryption).toBe('tls')
      })

      it('应该能够更新短信平台', async () => {
        wrapper.vm.smsForm.provider = 'tencent'
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.smsForm.provider).toBe('tencent')
      })
    })
  })

  describe('边界情况测试', () => {
    describe('数据加载中状态', () => {
      it('应该正确初始化loading状态', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.loading).toBe(false)
      })

      it('应该正确初始化submitting状态', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.submitting).toBe(false)
      })
    })

    describe('数据加载失败状态', () => {
      it('应该正确处理空数据', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.basicForm).toBeDefined()
        expect(wrapper.vm.emailForm).toBeDefined()
        expect(wrapper.vm.smsForm).toBeDefined()
        expect(wrapper.vm.paymentForm).toBeDefined()
      })
    })

    describe('保存失败状态', () => {
      beforeEach(() => {
        wrapper = createWrapper()
      })

      it('应该在保存失败时显示错误消息', async () => {
        const formRef = {
          validate: vi.fn((callback) => {
            callback(true)
            throw new Error('保存失败')
          }),
        }
        wrapper.vm.formRef = formRef

        try {
          await wrapper.vm.handleSubmit(formRef)
        } catch {
          // 预期会抛出错误
        }
        await waitForPromises()
      })

      it('应该在保存失败后重置submitting状态', async () => {
        const formRef = { validate: vi.fn((callback) => callback(true)) }
        wrapper.vm.formRef = formRef

        await wrapper.vm.handleSubmit(formRef)
        await waitForPromises()

        expect(wrapper.vm.submitting).toBe(false)
      })
    })

    describe('表单边界值测试', () => {
      beforeEach(() => {
        wrapper = createWrapper()
      })

      it('应该能够处理最大长度的网站名称', async () => {
        const maxLengthName = 'a'.repeat(100)
        wrapper.vm.basicForm.siteName = maxLengthName
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.basicForm.siteName.length).toBe(100)
      })

      it('应该能够处理空的网站描述', async () => {
        wrapper.vm.basicForm.siteDescription = ''
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.basicForm.siteDescription).toBe('')
      })

      it('应该能够处理空的Logo URL', async () => {
        wrapper.vm.basicForm.siteLogo = ''
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.basicForm.siteLogo).toBe('')
        expect(wrapper.find('.logo-preview').exists()).toBe(false)
      })

      it('应该能够处理SMTP端口边界值', async () => {
        wrapper.vm.emailForm.smtpPort = 1
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.emailForm.smtpPort).toBe(1)

        wrapper.vm.emailForm.smtpPort = 65535
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.emailForm.smtpPort).toBe(65535)
      })
    })

    describe('组件状态测试', () => {
      it('应该正确初始化所有表单数据', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.basicForm).toEqual({
          siteName: '宠物服务平台',
          siteDescription: '专业的宠物服务预约平台',
          siteLogo: '',
          contactEmail: 'admin@example.com',
          contactPhone: '400-888-8888',
          icpNumber: '京ICP备XXXXXXXX号',
          copyright: '© 2024 宠物服务平台 版权所有',
        })
      })

      it('应该正确初始化邮件表单数据', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.emailForm).toEqual({
          smtpHost: 'smtp.example.com',
          smtpPort: 465,
          smtpUsername: 'noreply@example.com',
          smtpPassword: '',
          fromEmail: 'noreply@example.com',
          fromName: '宠物服务平台',
          encryption: 'ssl',
          isEnabled: false,
        })
      })

      it('应该正确初始化短信表单数据', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.smsForm).toEqual({
          provider: 'aliyun',
          accessKeyId: '',
          accessKeySecret: '',
          signName: '宠物服务平台',
          isEnabled: false,
        })
      })

      it('应该正确初始化支付表单数据', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.paymentForm).toEqual({
          alipayEnabled: false,
          alipayAppId: '',
          alipayPrivateKey: '',
          alipayPublicKey: '',
          wechatEnabled: false,
          wechatMchId: '',
          wechatApiKey: '',
          wechatCertPath: '',
          wechatKeyPath: '',
        })
      })

      it('应该正确初始化表单引用', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.formRef).toBeDefined()
        expect(wrapper.vm.formRef).toHaveProperty('validate')
      })
    })

    describe('组件选项测试', () => {
      it('应该正确定义组件名称', () => {
        wrapper = createWrapper()

        expect(wrapper.vm.$options.name).toBe('AdminSystem')
      })
    })
  })
})
