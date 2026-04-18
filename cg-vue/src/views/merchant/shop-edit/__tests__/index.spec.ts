import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import ShopEdit from '../index.vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElCard, ElRadioGroup, ElRadio, ElUpload, ElAvatar } from 'element-plus'
import { createMockRouter, flushPromises as waitForPromises } from '@/tests/utils/testUtils'
import { createMerchant, createSuccessResponse, createErrorResponse } from '@/tests/fixtures/merchantData'

const mockPush = vi.hoisted(() => vi.fn())
const mockGetMerchant = vi.hoisted(() => vi.fn())
const mockUpdateMerchant = vi.hoisted(() => vi.fn())
const mockMessageSuccess = vi.hoisted(() => vi.fn())
const mockMessageError = vi.hoisted(() => vi.fn())

vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: mockPush,
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

vi.mock('@/api/merchant', () => ({
  getMerchantInfo: () => mockGetMerchant(),
  updateMerchantInfo: (data: any) => mockUpdateMerchant(data),
}))

vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: mockMessageSuccess,
      error: mockMessageError,
      warning: vi.fn(),
      info: vi.fn(),
    },
  }
})

const mockRouter = createMockRouter()

describe('ShopEdit', () => {
  let wrapper: any

  const createWrapper = (options = {}) => {
    return mount(ShopEdit, {
      global: {
        components: {
          ElForm,
          ElFormItem,
          ElInput,
          ElButton,
          ElCard,
          ElRadioGroup,
          ElRadio,
          ElUpload,
          ElAvatar,
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
          'el-radio-group': ElRadioGroup,
          'el-radio': ElRadio,
          'el-upload': ElUpload,
          'el-avatar': ElAvatar,
        },
      },
      ...options,
    })
  }

  beforeEach(() => {
    vi.clearAllMocks()
    mockGetMerchant.mockClear()
    mockUpdateMerchant.mockClear()
    mockPush.mockClear()
    mockMessageSuccess.mockClear()
    mockMessageError.mockClear()
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  describe('表单渲染', () => {
    it('应该正确渲染页面标题', async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)

      wrapper = createWrapper()
      await waitForPromises()

      expect(wrapper.find('.page-title').text()).toBe('店铺信息')
    })

    it('应该正确渲染所有表单字段', async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)

      wrapper = createWrapper()
      await waitForPromises()

      const formItems = wrapper.findAllComponents(ElFormItem)
      expect(formItems.length).toBe(7)

      expect(wrapper.find('input[placeholder="请输入店铺名称"]').exists()).toBe(true)
      expect(wrapper.find('input[placeholder="请输入联系人姓名"]').exists()).toBe(true)
      expect(wrapper.find('input[placeholder="请输入联系电话"]').exists()).toBe(true)
      expect(wrapper.find('input[placeholder="请输入邮箱"]').exists()).toBe(true)
      expect(wrapper.find('textarea[placeholder="请输入店铺地址"]').exists()).toBe(true)
    })

    it('应该正确渲染Logo上传区域', async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)

      wrapper = createWrapper()
      await waitForPromises()

      expect(wrapper.findComponent(ElRadioGroup).exists()).toBe(true)
      expect(wrapper.find('.logo-section').exists()).toBe(true)
      expect(wrapper.findComponent(ElAvatar).exists()).toBe(true)
    })

    it('应该正确渲染保存和返回按钮', async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)

      wrapper = createWrapper()
      await waitForPromises()

      const buttons = wrapper.findAllComponents(ElButton)
      expect(buttons.length).toBe(2)
      expect(buttons[0].text()).toBe('保存')
      expect(buttons[1].text()).toBe('返回设置')
    })

    it('应该在加载时显示loading状态', async () => {
      mockGetMerchant.mockImplementation(() => new Promise(() => {}))

      wrapper = createWrapper()

      expect(wrapper.vm.loading).toBe(true)
    })
  })

  describe('数据加载', () => {
    it('应该在挂载时调用getMerchantInfo API', async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)

      wrapper = createWrapper()
      await waitForPromises()

      expect(mockGetMerchant).toHaveBeenCalled()
    })

    it('应该正确加载并显示商家信息', async () => {
      const merchantData = createMerchant({
        name: '测试宠物店',
        contact_person: '张三',
        phone: '13800138000',
        email: 'test@example.com',
        address: '北京市朝阳区测试路123号',
        logo: 'https://example.com/logo.png',
      })
      mockGetMerchant.mockResolvedValue(merchantData)

      wrapper = createWrapper()
      await waitForPromises()

      expect(wrapper.vm.shopForm.name).toBe('测试宠物店')
      expect(wrapper.vm.shopForm.contact_person).toBe('张三')
      expect(wrapper.vm.shopForm.phone).toBe('13800138000')
      expect(wrapper.vm.shopForm.email).toBe('test@example.com')
      expect(wrapper.vm.shopForm.address).toBe('北京市朝阳区测试路123号')
      expect(wrapper.vm.shopForm.logo).toBe('https://example.com/logo.png')
    })

    it('应该在获取商家信息失败时显示错误消息', async () => {
      mockGetMerchant.mockRejectedValue(new Error('获取店铺信息失败'))

      wrapper = createWrapper()
      await waitForPromises()

      expect(mockMessageError).toHaveBeenCalledWith('获取店铺信息失败')
    })
  })

  describe('表单验证', () => {
    beforeEach(async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)
      wrapper = createWrapper()
      await waitForPromises()
    })

    it('应该验证店铺名称为必填项', async () => {
      const nameField = wrapper.vm.shopRules.name
      expect(nameField).toBeDefined()
      expect(nameField.some((rule: any) => rule.required)).toBe(true)
      expect(nameField.some((rule: any) => rule.min === 2 && rule.max === 100)).toBe(true)
    })

    it('应该验证联系人为必填项', async () => {
      const contactField = wrapper.vm.shopRules.contact_person
      expect(contactField).toBeDefined()
      expect(contactField.some((rule: any) => rule.required)).toBe(true)
      expect(contactField.some((rule: any) => rule.max === 50)).toBe(true)
    })

    it('应该验证联系电话为必填项', async () => {
      const phoneField = wrapper.vm.shopRules.phone
      expect(phoneField).toBeDefined()
      expect(phoneField.some((rule: any) => rule.required)).toBe(true)
      expect(phoneField.some((rule: any) => rule.max === 20)).toBe(true)
    })

    it('应该验证邮箱格式', async () => {
      const emailField = wrapper.vm.shopRules.email
      expect(emailField).toBeDefined()
      expect(emailField.some((rule: any) => rule.required)).toBe(true)
      expect(emailField.some((rule: any) => rule.type === 'email')).toBe(true)
      expect(emailField.some((rule: any) => rule.max === 100)).toBe(true)
    })

    it('应该验证店铺地址为必填项', async () => {
      const addressField = wrapper.vm.shopRules.address
      expect(addressField).toBeDefined()
      expect(addressField.some((rule: any) => rule.required)).toBe(true)
      expect(addressField.some((rule: any) => rule.max === 255)).toBe(true)
    })

    it('应该在表单验证失败时不调用保存API', async () => {
      const formRef = { validate: vi.fn((callback) => callback(false)) }
      wrapper.vm.formRef = formRef

      await wrapper.vm.handleSave()
      await waitForPromises()

      expect(mockUpdateMerchant).not.toHaveBeenCalled()
    })
  })

  describe('Logo上传功能', () => {
    beforeEach(async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)
      wrapper = createWrapper()
      await waitForPromises()
    })

    it('应该能够切换Logo输入方式', async () => {
      expect(wrapper.vm.logoInputMethod).toBe('url')

      const radioGroup = wrapper.findComponent(ElRadioGroup)
      await radioGroup.setValue('upload')
      expect(wrapper.vm.logoInputMethod).toBe('upload')
    })

    it('应该能够通过URL输入Logo', async () => {
      const logoUrl = 'https://example.com/new-logo.png'
      wrapper.vm.handleLogoUrlInput(logoUrl)

      expect(wrapper.vm.logoUrl).toBe(logoUrl)
      expect(wrapper.vm.shopForm.logo).toBe(logoUrl)
    })

    it('应该能够处理文件上传', async () => {
      const mockFile = {
        raw: new File([''], 'test.png', { type: 'image/png' }),
      }

      global.URL.createObjectURL = vi.fn(() => 'blob:test-url')

      wrapper.vm.handleLogoUploadChange(mockFile)

      expect(wrapper.vm.logoUrl).toBe('blob:test-url')
      expect(wrapper.vm.shopForm.logo).toBe('blob:test-url')
    })

    it('应该能够处理带有url的上传对象', async () => {
      const mockFile = {
        url: 'https://example.com/uploaded-logo.png',
      }

      wrapper.vm.handleLogoUploadChange(mockFile)

      expect(wrapper.vm.logoUrl).toBe('https://example.com/uploaded-logo.png')
      expect(wrapper.vm.shopForm.logo).toBe('https://example.com/uploaded-logo.png')
    })

    it('应该显示Logo预览', async () => {
      const logoUrl = 'https://example.com/logo.png'
      wrapper.vm.logoUrl = logoUrl

      await wrapper.vm.$nextTick()

      const avatar = wrapper.findComponent(ElAvatar)
      expect(avatar.exists()).toBe(true)
    })
  })

  describe('保存功能', () => {
    beforeEach(async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)
      wrapper = createWrapper()
      await waitForPromises()
    })

    it('应该在保存成功时调用updateMerchantInfo API', async () => {
      const updateData = {
        name: '更新后的店铺名称',
        logo: 'https://example.com/new-logo.png',
        contact_person: '李四',
        phone: '13900139000',
        email: 'new@example.com',
        address: '上海市浦东新区测试路456号',
      }

      wrapper.vm.shopForm.name = updateData.name
      wrapper.vm.shopForm.logo = updateData.logo
      wrapper.vm.shopForm.contact_person = updateData.contact_person
      wrapper.vm.shopForm.phone = updateData.phone
      wrapper.vm.shopForm.email = updateData.email
      wrapper.vm.shopForm.address = updateData.address

      mockUpdateMerchant.mockResolvedValue(createSuccessResponse({}))

      await wrapper.vm.handleSave()
      await waitForPromises()

      expect(mockUpdateMerchant).toHaveBeenCalledWith({
        name: updateData.name,
        logo: updateData.logo,
        contact_person: updateData.contact_person,
        phone: updateData.phone,
        email: updateData.email,
        address: updateData.address,
      })
    })

    it('应该在保存成功时显示成功消息', async () => {
      mockUpdateMerchant.mockResolvedValue(createSuccessResponse({}))

      await wrapper.vm.handleSave()
      await waitForPromises()

      expect(mockMessageSuccess).toHaveBeenCalledWith('保存成功')
    })

    it('应该在保存时显示loading状态', async () => {
      const formRef = { validate: vi.fn((callback) => callback(true)) }
      wrapper.vm.formRef = formRef

      mockUpdateMerchant.mockImplementation(() => new Promise(() => {}))

      wrapper.vm.handleSave()

      expect(wrapper.vm.loading).toBe(true)
    })

    it('应该在保存完成后关闭loading状态', async () => {
      mockUpdateMerchant.mockResolvedValue(createSuccessResponse({}))

      await wrapper.vm.handleSave()
      await waitForPromises()

      expect(wrapper.vm.loading).toBe(false)
    })
  })

  describe('保存失败处理', () => {
    beforeEach(async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)
      wrapper = createWrapper()
      await waitForPromises()
    })

    it('应该在保存失败时显示错误消息', async () => {
      mockUpdateMerchant.mockRejectedValue(new Error('保存失败'))

      await wrapper.vm.handleSave()
      await waitForPromises()

      expect(mockMessageError).toHaveBeenCalledWith('保存失败')
    })

    it('应该在保存失败时关闭loading状态', async () => {
      mockUpdateMerchant.mockRejectedValue(new Error('保存失败'))

      await wrapper.vm.handleSave()
      await waitForPromises()

      expect(wrapper.vm.loading).toBe(false)
    })

    it('应该处理没有错误消息的失败情况', async () => {
      mockUpdateMerchant.mockRejectedValue(new Error())

      await wrapper.vm.handleSave()
      await waitForPromises()

      expect(mockMessageError).toHaveBeenCalledWith('保存失败')
    })

    it('应该处理网络错误', async () => {
      mockUpdateMerchant.mockRejectedValue(new Error('Network Error'))

      await wrapper.vm.handleSave()
      await waitForPromises()

      expect(mockMessageError).toHaveBeenCalledWith('Network Error')
    })
  })

  describe('返回功能', () => {
    beforeEach(async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)
      wrapper = createWrapper()
      await waitForPromises()
    })

    it('应该点击返回按钮时跳转到店铺设置页面', async () => {
      const backButton = wrapper.findAllComponents(ElButton)[1]
      await backButton.trigger('click')

      expect(mockPush).toHaveBeenCalledWith('/merchant/shop-settings')
    })

    it('应该调用handleBack方法', async () => {
      wrapper.vm.handleBack()

      expect(mockPush).toHaveBeenCalledWith('/merchant/shop-settings')
    })
  })

  describe('表单交互', () => {
    beforeEach(async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)
      wrapper = createWrapper()
      await waitForPromises()
    })

    it('应该能够更新店铺名称', async () => {
      const input = wrapper.find('input[placeholder="请输入店铺名称"]')
      await input.setValue('新店铺名称')

      expect(wrapper.vm.shopForm.name).toBe('新店铺名称')
    })

    it('应该能够更新联系人', async () => {
      const input = wrapper.find('input[placeholder="请输入联系人姓名"]')
      await input.setValue('王五')

      expect(wrapper.vm.shopForm.contact_person).toBe('王五')
    })

    it('应该能够更新联系电话', async () => {
      const input = wrapper.find('input[placeholder="请输入联系电话"]')
      await input.setValue('13700137000')

      expect(wrapper.vm.shopForm.phone).toBe('13700137000')
    })

    it('应该能够更新邮箱', async () => {
      const input = wrapper.find('input[placeholder="请输入邮箱"]')
      await input.setValue('newemail@example.com')

      expect(wrapper.vm.shopForm.email).toBe('newemail@example.com')
    })

    it('应该能够更新店铺地址', async () => {
      const textarea = wrapper.find('textarea[placeholder="请输入店铺地址"]')
      await textarea.setValue('广州市天河区测试路789号')

      expect(wrapper.vm.shopForm.address).toBe('广州市天河区测试路789号')
    })
  })

  describe('组件状态', () => {
    it('应该正确初始化loading状态', async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)

      wrapper = createWrapper()

      expect(wrapper.vm.loading).toBe(true)

      await waitForPromises()

      expect(wrapper.vm.loading).toBe(false)
    })

    it('应该正确初始化表单数据', async () => {
      const merchantData = {
        id: 1,
        name: '测试商家',
        logo: 'https://picsum.photos/100/100?random=1',
        contact_person: '张先生',
        phone: '13800138000',
        email: 'test@merchant.com',
        address: '北京市朝阳区测试路1号',
      }
      mockGetMerchant.mockResolvedValue(merchantData)

      wrapper = createWrapper()
      await waitForPromises()

      expect(wrapper.vm.shopForm).toEqual({
        id: merchantData.id,
        name: merchantData.name,
        logo: merchantData.logo,
        contact_person: merchantData.contact_person,
        phone: merchantData.phone,
        email: merchantData.email,
        address: merchantData.address,
      })
    })

    it('应该正确初始化Logo输入方式', async () => {
      const merchantData = createMerchant()
      mockGetMerchant.mockResolvedValue(merchantData)

      wrapper = createWrapper()
      await waitForPromises()

      expect(wrapper.vm.logoInputMethod).toBe('url')
    })
  })
})
