import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { nextTick } from 'vue'
import ServiceEdit from '../index.vue'
import { ElForm, ElFormItem, ElInput, ElInputNumber, ElButton, ElImage, ElDialog, ElMessage } from 'element-plus'
import { createService, createSuccessResponse, createErrorResponse } from '@/tests/fixtures/merchantData'

const mockPush = vi.fn()
const mockRoute = {
  query: {},
}

vi.mock('vue-router', () => ({
  useRoute: () => mockRoute,
  useRouter: () => ({
    push: mockPush,
  }),
}))

vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      error: vi.fn(),
      warning: vi.fn(),
      info: vi.fn(),
    },
  }
})

vi.mock('@/api/merchant', () => ({
  updateService: vi.fn(),
  addService: vi.fn(),
}))

vi.mock('@/api/request', () => ({
  default: {
    get: vi.fn(),
  },
}))

import { updateService, addService } from '@/api/merchant'
import request from '@/api/request'

describe('ServiceEdit', () => {
  let wrapper: any

  const createWrapper = (options: any = {}) => {
    return mount(ServiceEdit, {
      global: {
        components: {
          ElForm,
          ElFormItem,
          ElInput,
          ElInputNumber,
          ElButton,
          ElImage,
          ElDialog,
        },
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-button': true,
          'el-image': true,
          'el-dialog': true,
        },
      },
      ...options,
    })
  }

  beforeEach(() => {
    vi.clearAllMocks()
    mockRoute.query = {}
  })

  afterEach(() => {
    wrapper?.unmount()
  })

  describe('表单渲染', () => {
    it('应该正确渲染所有表单字段', () => {
      wrapper = createWrapper()

      expect(wrapper.find('.service-edit').exists()).toBe(true)
      expect(wrapper.find('.service-form').exists()).toBe(true)
      expect(wrapper.find('h1').text()).toBe('添加服务')
    })

    it('在添加模式下应显示"添加服务"标题', () => {
      wrapper = createWrapper()

      expect(wrapper.find('h1').text()).toBe('添加服务')
    })

    it('在编辑模式下应显示"编辑服务"标题', async () => {
      mockRoute.query = { id: '1' }
      const mockService = createService({ id: 1 })
      ;(request.get as any).mockResolvedValueOnce({
        data: mockService,
      })

      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.find('h1').text()).toBe('编辑服务')
    })

    it('应该正确初始化表单数据', () => {
      wrapper = createWrapper()

      expect(wrapper.vm.form.name).toBe('')
      expect(wrapper.vm.form.description).toBe('')
      expect(wrapper.vm.form.price).toBe(0)
      expect(wrapper.vm.form.duration).toBe(0)
      expect(wrapper.vm.form.image).toBe('')
    })
  })

  describe('表单验证', () => {
    it('应该有正确的验证规则', () => {
      wrapper = createWrapper()

      const rules = wrapper.vm.rules

      expect(rules.name).toBeDefined()
      expect(rules.name.length).toBeGreaterThan(0)
      expect(rules.name[0].required).toBe(true)
      expect(rules.name[0].message).toBe('请输入服务名称')
      expect(rules.name[1].max).toBe(100)

      expect(rules.price).toBeDefined()
      expect(rules.price[0].required).toBe(true)
      expect(rules.price[0].message).toBe('请输入价格')
      expect(rules.price[1].min).toBe(0)

      expect(rules.duration).toBeDefined()
      expect(rules.duration[0].required).toBe(true)
      expect(rules.duration[0].message).toBe('请输入时长')
      expect(rules.duration[1].min).toBe(1)
    })

    it('服务名称验证规则应包含最大长度限制', () => {
      wrapper = createWrapper()

      const nameRules = wrapper.vm.rules.name
      const maxLengthRule = nameRules.find((rule: any) => rule.max === 100)

      expect(maxLengthRule).toBeDefined()
      expect(maxLengthRule.message).toBe('服务名称最多100个字符')
    })

    it('价格验证规则应包含最小值限制', () => {
      wrapper = createWrapper()

      const priceRules = wrapper.vm.rules.price
      const minRule = priceRules.find((rule: any) => rule.type === 'number')

      expect(minRule).toBeDefined()
      expect(minRule.min).toBe(0)
    })

    it('时长验证规则应包含最小值限制', () => {
      wrapper = createWrapper()

      const durationRules = wrapper.vm.rules.duration
      const minRule = durationRules.find((rule: any) => rule.type === 'number')

      expect(minRule).toBeDefined()
      expect(minRule.min).toBe(1)
    })
  })

  describe('图片上传功能', () => {
    it('应该正确处理图片URL输入', async () => {
      wrapper = createWrapper()

      const testUrl = 'https://example.com/test-image.jpg'
      wrapper.vm.handleImageChange(testUrl)

      expect(wrapper.vm.form.image).toBe(testUrl)
    })

    it('应该正确显示图片预览按钮状态', () => {
      wrapper = createWrapper()

      expect(wrapper.vm.form.image).toBe('')
    })

    it('应该能够打开图片预览对话框', async () => {
      wrapper = createWrapper()

      wrapper.vm.form.image = 'https://example.com/test-image.jpg'
      wrapper.vm.handleImagePreview()

      expect(wrapper.vm.imagePreviewVisible).toBe(true)
      expect(wrapper.vm.imagePreviewUrl).toBe('https://example.com/test-image.jpg')
    })

    it('当没有图片时预览按钮应禁用', () => {
      wrapper = createWrapper()

      expect(wrapper.vm.form.image).toBe('')
    })
  })

  describe('保存功能', () => {
    it('添加服务时应调用addService API', async () => {
      wrapper = createWrapper()

      wrapper.vm.form.name = '测试服务'
      wrapper.vm.form.description = '测试描述'
      wrapper.vm.form.price = 99.99
      wrapper.vm.form.duration = 60
      wrapper.vm.form.image = 'https://example.com/image.jpg'

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      ;(addService as any).mockResolvedValueOnce(createSuccessResponse(createService()))

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(addService).toHaveBeenCalledWith({
        name: '测试服务',
        description: '测试描述',
        price: 99.99,
        duration: 60,
        image: 'https://example.com/image.jpg',
      })
    })

    it('编辑服务时应调用updateService API', async () => {
      mockRoute.query = { id: '1' }
      const mockService = createService({ id: 1 })
      ;(request.get as any).mockResolvedValueOnce({
        data: mockService,
      })

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.form.name = '更新后的服务'
      wrapper.vm.form.description = '更新后的描述'
      wrapper.vm.form.price = 199.99
      wrapper.vm.form.duration = 90
      wrapper.vm.form.image = 'https://example.com/new-image.jpg'

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      ;(updateService as any).mockResolvedValueOnce(createSuccessResponse(mockService))

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(updateService).toHaveBeenCalledWith(1, {
        name: '更新后的服务',
        description: '更新后的描述',
        price: 199.99,
        duration: 90,
        image: 'https://example.com/new-image.jpg',
      })
    })

    it('保存成功后应显示成功消息并跳转', async () => {
      wrapper = createWrapper()

      wrapper.vm.form.name = '测试服务'
      wrapper.vm.form.price = 99.99
      wrapper.vm.form.duration = 60

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      ;(addService as any).mockResolvedValueOnce(createSuccessResponse(createService()))

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(ElMessage.success).toHaveBeenCalledWith('添加成功')
      expect(mockPush).toHaveBeenCalledWith('/merchant/services')
    })

    it('更新成功后应显示成功消息并跳转', async () => {
      mockRoute.query = { id: '1' }
      const mockService = createService({ id: 1 })
      ;(request.get as any).mockResolvedValueOnce({
        data: mockService,
      })

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.form.name = '更新后的服务'
      wrapper.vm.form.price = 199.99
      wrapper.vm.form.duration = 90

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      ;(updateService as any).mockResolvedValueOnce(createSuccessResponse(mockService))

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(ElMessage.success).toHaveBeenCalledWith('更新成功')
      expect(mockPush).toHaveBeenCalledWith('/merchant/services')
    })

    it('表单验证失败时不应调用API', async () => {
      wrapper = createWrapper()

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(false)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(addService).not.toHaveBeenCalled()
      expect(updateService).not.toHaveBeenCalled()
    })

    it('当formRef不存在时不应执行任何操作', async () => {
      wrapper = createWrapper()
      wrapper.vm.formRef = null

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(addService).not.toHaveBeenCalled()
      expect(updateService).not.toHaveBeenCalled()
    })
  })

  describe('保存失败处理', () => {
    it('添加服务失败时应显示错误消息', async () => {
      wrapper = createWrapper()

      wrapper.vm.form.name = '测试服务'
      wrapper.vm.form.price = 99.99
      wrapper.vm.form.duration = 60

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      ;(addService as any).mockRejectedValueOnce(new Error('添加失败'))

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('保存失败')
    })

    it('更新服务失败时应显示错误消息', async () => {
      mockRoute.query = { id: '1' }
      const mockService = createService({ id: 1 })
      ;(request.get as any).mockResolvedValueOnce({
        data: mockService,
      })

      wrapper = createWrapper()
      await flushPromises()

      wrapper.vm.form.name = '更新后的服务'
      wrapper.vm.form.price = 199.99
      wrapper.vm.form.duration = 90

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      ;(updateService as any).mockRejectedValueOnce(new Error('更新失败'))

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('更新失败')
    })

    it('保存失败后不应跳转页面', async () => {
      wrapper = createWrapper()

      wrapper.vm.form.name = '测试服务'
      wrapper.vm.form.price = 99.99
      wrapper.vm.form.duration = 60

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      ;(addService as any).mockRejectedValueOnce(new Error('添加失败'))

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(mockPush).not.toHaveBeenCalled()
    })

    it('保存过程中应正确设置submitting状态', async () => {
      wrapper = createWrapper()

      wrapper.vm.form.name = '测试服务'
      wrapper.vm.form.price = 99.99
      wrapper.vm.form.duration = 60

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      let resolvePromise: any
      ;(addService as any).mockImplementation(() => {
        return new Promise((resolve) => {
          resolvePromise = resolve
        })
      })

      const submitPromise = wrapper.vm.handleSubmit()

      expect(wrapper.vm.submitting).toBe(true)

      resolvePromise(createSuccessResponse(createService()))
      await submitPromise
      await flushPromises()

      expect(wrapper.vm.submitting).toBe(false)
    })
  })

  describe('编辑模式数据加载', () => {
    it('编辑模式下应加载服务数据', async () => {
      mockRoute.query = { id: '1' }
      const mockService = createService({
        id: 1,
        name: '宠物美容',
        description: '专业宠物美容服务',
        price: 199.99,
        duration: 90,
        image: 'https://example.com/beauty.jpg',
      })
      ;(request.get as any).mockResolvedValueOnce({
        data: mockService,
      })

      wrapper = createWrapper()
      await flushPromises()

      expect(request.get).toHaveBeenCalledWith('/api/merchant/services/1')
      expect(wrapper.vm.form.name).toBe('宠物美容')
      expect(wrapper.vm.form.description).toBe('专业宠物美容服务')
      expect(wrapper.vm.form.price).toBe(199.99)
      expect(wrapper.vm.form.duration).toBe(90)
      expect(wrapper.vm.form.image).toBe('https://example.com/beauty.jpg')
    })

    it('加载服务数据失败时应显示错误消息并跳转', async () => {
      mockRoute.query = { id: '1' }
      ;(request.get as any).mockRejectedValueOnce(new Error('加载失败'))

      wrapper = createWrapper()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('获取服务信息失败')
      expect(mockPush).toHaveBeenCalledWith('/merchant/services')
    })

    it('加载服务数据时应正确设置loading状态', async () => {
      mockRoute.query = { id: '1' }
      let resolvePromise: any
      ;(request.get as any).mockImplementation(() => {
        return new Promise((resolve) => {
          resolvePromise = resolve
        })
      })

      wrapper = createWrapper()

      expect(wrapper.vm.loading).toBe(true)

      resolvePromise({ data: createService() })
      await flushPromises()

      expect(wrapper.vm.loading).toBe(false)
    })

    it('添加模式下不应加载服务数据', async () => {
      mockRoute.query = {}

      wrapper = createWrapper()
      await flushPromises()

      expect(request.get).not.toHaveBeenCalled()
    })
  })

  describe('取消功能', () => {
    it('点击取消按钮应跳转到服务列表', () => {
      wrapper = createWrapper()

      wrapper.vm.handleCancel()

      expect(mockPush).toHaveBeenCalledWith('/merchant/services')
    })
  })

  describe('计算属性', () => {
    it('serviceId应正确从路由获取', () => {
      mockRoute.query = { id: '123' }
      wrapper = createWrapper()

      expect(wrapper.vm.serviceId).toBe('123')
    })

    it('isEdit应根据serviceId判断', () => {
      mockRoute.query = {}
      wrapper = createWrapper()
      expect(wrapper.vm.isEdit).toBe(false)

      wrapper.unmount()

      mockRoute.query = { id: '1' }
      ;(request.get as any).mockResolvedValueOnce({ data: createService() })
      wrapper = createWrapper()

      expect(wrapper.vm.isEdit).toBe(true)
    })

    it('pageTitle应根据模式显示不同标题', () => {
      mockRoute.query = {}
      wrapper = createWrapper()
      expect(wrapper.vm.pageTitle).toBe('添加服务')

      wrapper.unmount()

      mockRoute.query = { id: '1' }
      ;(request.get as any).mockResolvedValueOnce({ data: createService() })
      wrapper = createWrapper()

      expect(wrapper.vm.pageTitle).toBe('编辑服务')
    })
  })

  describe('边界情况处理', () => {
    it('服务数据字段为空时应使用默认值', async () => {
      mockRoute.query = { id: '1' }
      ;(request.get as any).mockResolvedValueOnce({
        data: {},
      })

      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.vm.form.name).toBe('')
      expect(wrapper.vm.form.description).toBe('')
      expect(wrapper.vm.form.price).toBe(0)
      expect(wrapper.vm.form.duration).toBe(0)
      expect(wrapper.vm.form.image).toBe('')
    })

    it('服务数据包含null值时应正确处理', async () => {
      mockRoute.query = { id: '1' }
      ;(request.get as any).mockResolvedValueOnce({
        data: {
          name: null,
          description: null,
          price: null,
          duration: null,
          image: null,
        },
      })

      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.vm.form.name).toBe('')
      expect(wrapper.vm.form.description).toBe('')
      expect(wrapper.vm.form.price).toBe(0)
      expect(wrapper.vm.form.duration).toBe(0)
      expect(wrapper.vm.form.image).toBe('')
    })

    it('价格应为非负数', async () => {
      wrapper = createWrapper()

      const priceRules = wrapper.vm.rules.price
      const minRule = priceRules.find((rule: any) => rule.type === 'number')

      expect(minRule.min).toBeGreaterThanOrEqual(0)
    })

    it('时长应至少为1分钟', async () => {
      wrapper = createWrapper()

      const durationRules = wrapper.vm.rules.duration
      const minRule = durationRules.find((rule: any) => rule.type === 'number')

      expect(minRule.min).toBeGreaterThanOrEqual(1)
    })
  })
})
