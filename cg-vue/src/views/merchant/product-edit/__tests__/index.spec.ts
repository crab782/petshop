import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { ElMessage } from 'element-plus'
import ProductEdit from '../index.vue'
import {
  createProduct,
  createSuccessResponse,
  createErrorResponse,
} from '@/tests/fixtures/merchantData'

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

vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn(),
  },
}))

vi.mock('@/api/merchant', () => ({
  addProduct: vi.fn(),
  updateProduct: vi.fn(),
  getProductById: vi.fn(),
}))

import { addProduct, updateProduct, getProductById } from '@/api/merchant'

const mockAddProduct = vi.mocked(addProduct)
const mockUpdateProduct = vi.mocked(updateProduct)
const mockGetProductById = vi.mocked(getProductById)

describe('ProductEdit', () => {
  let wrapper: any

  beforeEach(() => {
    vi.clearAllMocks()
    mockRoute.query = {}
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  const mountComponent = (options = {}) => {
    return mount(ProductEdit, {
      global: {
        ...options,
      },
    })
  }

  describe('表单渲染', () => {
    it('应该正确渲染页面', () => {
      wrapper = mountComponent()

      expect(wrapper.find('.product-edit').exists()).toBe(true)
      expect(wrapper.find('.product-form').exists()).toBe(true)
    })

    it('应该在添加模式下显示正确的标题', () => {
      wrapper = mountComponent()

      expect(wrapper.vm.pageTitle).toBe('添加商品')
    })

    it('应该在编辑模式下显示正确的标题', () => {
      mockRoute.query = { id: '1' }
      wrapper = mountComponent()

      expect(wrapper.vm.pageTitle).toBe('编辑商品')
    })

    it('应该有正确的表单初始值', () => {
      wrapper = mountComponent()

      expect(wrapper.vm.form.name).toBe('')
      expect(wrapper.vm.form.category).toBe('')
      expect(wrapper.vm.form.price).toBe(0)
      expect(wrapper.vm.form.stock).toBe(0)
      expect(wrapper.vm.form.lowStockThreshold).toBe(10)
      expect(wrapper.vm.form.image).toBe('')
      expect(wrapper.vm.form.description).toBe('')
    })

    it('应该有正确的分类选项', () => {
      wrapper = mountComponent()

      const options = wrapper.vm.categoryOptions
      expect(options.length).toBe(7)
      expect(options[0].label).toBe('狗粮')
      expect(options[1].label).toBe('猫粮')
      expect(options[2].label).toBe('宠物零食')
      expect(options[3].label).toBe('宠物玩具')
      expect(options[4].label).toBe('宠物用品')
      expect(options[5].label).toBe('宠物美容')
      expect(options[6].label).toBe('其他')
    })
  })

  describe('表单验证', () => {
    it('应该有正确的验证规则', () => {
      wrapper = mountComponent()

      const rules = wrapper.vm.rules
      expect(rules.name).toBeDefined()
      expect(rules.category).toBeDefined()
      expect(rules.price).toBeDefined()
      expect(rules.stock).toBeDefined()
    })

    it('商品名称应该有必填验证', () => {
      wrapper = mountComponent()

      const nameRules = wrapper.vm.rules.name
      const requiredRule = nameRules.find((rule: any) => rule.required === true)
      expect(requiredRule).toBeDefined()
      expect(requiredRule.message).toBe('请输入商品名称')
    })

    it('商品名称应该有长度验证', () => {
      wrapper = mountComponent()

      const nameRules = wrapper.vm.rules.name
      const lengthRule = nameRules.find((rule: any) => rule.min && rule.max)
      expect(lengthRule).toBeDefined()
      expect(lengthRule.min).toBe(1)
      expect(lengthRule.max).toBe(100)
    })

    it('分类应该有必填验证', () => {
      wrapper = mountComponent()

      const categoryRules = wrapper.vm.rules.category
      const requiredRule = categoryRules.find((rule: any) => rule.required === true)
      expect(requiredRule).toBeDefined()
      expect(requiredRule.message).toBe('请选择商品分类')
    })

    it('价格应该有必填验证', () => {
      wrapper = mountComponent()

      const priceRules = wrapper.vm.rules.price
      const requiredRule = priceRules.find((rule: any) => rule.required === true)
      expect(requiredRule).toBeDefined()
      expect(requiredRule.message).toBe('请输入价格')
    })

    it('价格应该有最小值验证', () => {
      wrapper = mountComponent()

      const priceRules = wrapper.vm.rules.price
      const minRule = priceRules.find((rule: any) => rule.type === 'number' && rule.min !== undefined)
      expect(minRule).toBeDefined()
      expect(minRule.min).toBe(0)
    })

    it('库存应该有必填验证', () => {
      wrapper = mountComponent()

      const stockRules = wrapper.vm.rules.stock
      const requiredRule = stockRules.find((rule: any) => rule.required === true)
      expect(requiredRule).toBeDefined()
      expect(requiredRule.message).toBe('请输入库存')
    })

    it('库存应该有最小值验证', () => {
      wrapper = mountComponent()

      const stockRules = wrapper.vm.rules.stock
      const minRule = stockRules.find((rule: any) => rule.type === 'number' && rule.min !== undefined)
      expect(minRule).toBeDefined()
      expect(minRule.min).toBe(0)
    })
  })

  describe('图片上传功能', () => {
    it('应该有图片字段', () => {
      wrapper = mountComponent()

      expect(wrapper.vm.form.image).toBeDefined()
      expect(wrapper.vm.form.image).toBe('')
    })

    it('当有图片URL时应该显示图片预览', async () => {
      wrapper = mountComponent()

      wrapper.vm.form.image = 'https://example.com/image.jpg'
      await wrapper.vm.$nextTick()

      expect(wrapper.find('.image-preview').exists()).toBe(true)
    })

    it('当没有图片URL时不应该显示图片预览', () => {
      wrapper = mountComponent()

      wrapper.vm.form.image = ''
      expect(wrapper.find('.image-preview').exists()).toBe(false)
    })

    it('图片URL应该可以修改', async () => {
      wrapper = mountComponent()

      wrapper.vm.form.image = 'https://example.com/new-image.jpg'
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.form.image).toBe('https://example.com/new-image.jpg')
    })
  })

  describe('库存管理功能', () => {
    it('应该有库存字段', () => {
      wrapper = mountComponent()

      expect(wrapper.vm.form.stock).toBeDefined()
      expect(wrapper.vm.form.stock).toBe(0)
    })

    it('应该有库存预警阈值字段', () => {
      wrapper = mountComponent()

      expect(wrapper.vm.form.lowStockThreshold).toBeDefined()
      expect(wrapper.vm.form.lowStockThreshold).toBe(10)
    })

    it('库存预警阈值应该可以修改', async () => {
      wrapper = mountComponent()

      wrapper.vm.form.lowStockThreshold = 20
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.form.lowStockThreshold).toBe(20)
    })

    it('库存应该可以修改', async () => {
      wrapper = mountComponent()

      wrapper.vm.form.stock = 100
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.form.stock).toBe(100)
    })
  })

  describe('添加商品功能', () => {
    it('应该能够成功添加商品', async () => {
      const mockProduct = createProduct()
      mockAddProduct.mockResolvedValue(createSuccessResponse(mockProduct))

      wrapper = mountComponent()

      wrapper.vm.form.name = '测试商品'
      wrapper.vm.form.category = '狗粮'
      wrapper.vm.form.price = 100
      wrapper.vm.form.stock = 50

      const formRef = {
        validate: vi.fn((callback: any) => {
          if (callback) callback(true)
          return Promise.resolve(true)
        }),
      }
      wrapper.vm.formRef = formRef

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(mockAddProduct).toHaveBeenCalled()
      expect(ElMessage.success).toHaveBeenCalledWith('添加成功')
      expect(mockPush).toHaveBeenCalledWith('/merchant/products')
    })

    it('添加商品失败时应该显示错误消息', async () => {
      mockAddProduct.mockRejectedValue(new Error('添加失败'))

      wrapper = mountComponent()

      wrapper.vm.form.name = '测试商品'
      wrapper.vm.form.category = '狗粮'
      wrapper.vm.form.price = 100
      wrapper.vm.form.stock = 50

      const formRef = {
        validate: vi.fn((callback: any) => {
          if (callback) callback(true)
          return Promise.resolve(true)
        }),
      }
      wrapper.vm.formRef = formRef

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('添加失败')
    })

    it('提交时应该包含所有必要字段', async () => {
      const mockProduct = createProduct()
      mockAddProduct.mockResolvedValue(createSuccessResponse(mockProduct))

      wrapper = mountComponent()

      wrapper.vm.form.name = '测试商品'
      wrapper.vm.form.category = '狗粮'
      wrapper.vm.form.price = 100
      wrapper.vm.form.stock = 50
      wrapper.vm.form.lowStockThreshold = 10
      wrapper.vm.form.image = 'https://example.com/image.jpg'
      wrapper.vm.form.description = '测试描述'

      const formRef = {
        validate: vi.fn((callback: any) => {
          if (callback) callback(true)
          return Promise.resolve(true)
        }),
      }
      wrapper.vm.formRef = formRef

      await wrapper.vm.handleSubmit()
      await flushPromises()

      const callArgs = mockAddProduct.mock.calls[0][0]
      expect(callArgs.name).toBe('测试商品')
      expect(callArgs.category).toBe('狗粮')
      expect(callArgs.price).toBe(100)
      expect(callArgs.stock).toBe(50)
      expect(callArgs.lowStockThreshold).toBe(10)
      expect(callArgs.image).toBe('https://example.com/image.jpg')
      expect(callArgs.description).toBe('测试描述')
    })

    it('提交时应该设置提交状态', async () => {
      const mockProduct = createProduct()
      let resolvePromise: any
      mockAddProduct.mockImplementation(() => {
        return new Promise((resolve) => {
          resolvePromise = resolve
        })
      })

      wrapper = mountComponent()

      wrapper.vm.form.name = '测试商品'
      wrapper.vm.form.category = '狗粮'
      wrapper.vm.form.price = 100
      wrapper.vm.form.stock = 50

      const formRef = {
        validate: vi.fn((callback: any) => {
          if (callback) callback(true)
          return Promise.resolve(true)
        }),
      }
      wrapper.vm.formRef = formRef

      const submitPromise = wrapper.vm.handleSubmit()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.submitting).toBe(true)

      resolvePromise(createSuccessResponse(mockProduct))
      await submitPromise
      await flushPromises()

      expect(wrapper.vm.submitting).toBe(false)
    })

    it('表单验证失败时不应该提交', async () => {
      wrapper = mountComponent()

      const formRef = {
        validate: vi.fn((callback: any) => {
          if (callback) callback(false)
          return Promise.resolve(false)
        }),
      }
      wrapper.vm.formRef = formRef

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(mockAddProduct).not.toHaveBeenCalled()
    })
  })

  describe('编辑商品功能', () => {
    beforeEach(() => {
      mockRoute.query = { id: '1' }
    })

    it('编辑模式下应该加载商品数据', async () => {
      const mockProduct = createProduct({
        id: 1,
        name: '测试商品',
        category: '狗粮',
        price: 100,
        stock: 50,
        lowStockThreshold: 10,
        image: 'https://example.com/image.jpg',
        description: '测试描述',
      })

      mockGetProductById.mockResolvedValue(createSuccessResponse(mockProduct))

      wrapper = mountComponent()

      await flushPromises()

      expect(mockGetProductById).toHaveBeenCalledWith(1)
      expect(wrapper.vm.form.name).toBe('测试商品')
      expect(wrapper.vm.form.category).toBe('狗粮')
      expect(wrapper.vm.form.price).toBe(100)
      expect(wrapper.vm.form.stock).toBe(50)
      expect(wrapper.vm.form.lowStockThreshold).toBe(10)
      expect(wrapper.vm.form.image).toBe('https://example.com/image.jpg')
      expect(wrapper.vm.form.description).toBe('测试描述')
    })

    it('加载商品数据失败时应该显示错误消息并跳转', async () => {
      mockGetProductById.mockRejectedValue(new Error('获取失败'))

      wrapper = mountComponent()

      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('获取商品信息失败')
      expect(mockPush).toHaveBeenCalledWith('/merchant/products')
    })

    it('应该能够成功更新商品', async () => {
      const mockProduct = createProduct()
      mockGetProductById.mockResolvedValue(createSuccessResponse(mockProduct))
      mockUpdateProduct.mockResolvedValue(createSuccessResponse(mockProduct))

      wrapper = mountComponent()

      await flushPromises()

      wrapper.vm.form.name = '更新后的商品'
      wrapper.vm.form.category = '猫粮'
      wrapper.vm.form.price = 200
      wrapper.vm.form.stock = 100

      const formRef = {
        validate: vi.fn((callback: any) => {
          if (callback) callback(true)
          return Promise.resolve(true)
        }),
      }
      wrapper.vm.formRef = formRef

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(mockUpdateProduct).toHaveBeenCalledWith(1, expect.any(Object))
      expect(ElMessage.success).toHaveBeenCalledWith('更新成功')
      expect(mockPush).toHaveBeenCalledWith('/merchant/products')
    })

    it('更新商品失败时应该显示错误消息', async () => {
      const mockProduct = createProduct()
      mockGetProductById.mockResolvedValue(createSuccessResponse(mockProduct))
      mockUpdateProduct.mockRejectedValue(new Error('更新失败'))

      wrapper = mountComponent()

      await flushPromises()

      wrapper.vm.form.name = '更新后的商品'

      const formRef = {
        validate: vi.fn((callback: any) => {
          if (callback) callback(true)
          return Promise.resolve(true)
        }),
      }
      wrapper.vm.formRef = formRef

      await wrapper.vm.handleSubmit()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('更新失败')
    })
  })

  describe('取消功能', () => {
    it('点击取消按钮应该跳转到商品列表页', async () => {
      wrapper = mountComponent()

      await wrapper.vm.handleCancel()

      expect(mockPush).toHaveBeenCalledWith('/merchant/products')
    })
  })

  describe('加载状态', () => {
    it('编辑模式下应该显示加载状态', async () => {
      mockRoute.query = { id: '1' }

      const mockProduct = createProduct()
      let resolvePromise: any
      mockGetProductById.mockImplementation(() => {
        return new Promise((resolve) => {
          resolvePromise = resolve
        })
      })

      wrapper = mountComponent()

      expect(wrapper.vm.loading).toBe(true)

      resolvePromise(createSuccessResponse(mockProduct))
      await flushPromises()

      expect(wrapper.vm.loading).toBe(false)
    })
  })

  describe('计算属性', () => {
    it('productId 应该从路由查询参数获取', () => {
      mockRoute.query = { id: '123' }
      wrapper = mountComponent()

      expect(wrapper.vm.productId).toBe('123')
    })

    it('isEdit 应该根据 productId 判断', () => {
      mockRoute.query = { id: '123' }
      wrapper = mountComponent()

      expect(wrapper.vm.isEdit).toBe(true)
    })

    it('没有 id 时 isEdit 应该为 false', () => {
      mockRoute.query = {}
      wrapper = mountComponent()

      expect(wrapper.vm.isEdit).toBe(false)
    })

    it('pageTitle 应该根据编辑模式显示不同标题', () => {
      mockRoute.query = {}
      wrapper = mountComponent()
      expect(wrapper.vm.pageTitle).toBe('添加商品')

      mockRoute.query = { id: '1' }
      wrapper = mountComponent()
      expect(wrapper.vm.pageTitle).toBe('编辑商品')
    })
  })

  describe('表单字段修改', () => {
    it('应该能够修改商品名称', async () => {
      wrapper = mountComponent()

      wrapper.vm.form.name = '新商品名称'
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.form.name).toBe('新商品名称')
    })

    it('应该能够修改商品分类', async () => {
      wrapper = mountComponent()

      wrapper.vm.form.category = '猫粮'
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.form.category).toBe('猫粮')
    })

    it('应该能够修改商品价格', async () => {
      wrapper = mountComponent()

      wrapper.vm.form.price = 199.99
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.form.price).toBe(199.99)
    })

    it('应该能够修改商品描述', async () => {
      wrapper = mountComponent()

      wrapper.vm.form.description = '这是商品描述'
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.form.description).toBe('这是商品描述')
    })
  })

  describe('数据清理', () => {
    it('组件卸载时应该清理状态', () => {
      wrapper = mountComponent()

      wrapper.vm.form.name = '测试商品'
      wrapper.unmount()

      expect(wrapper.vm.form.name).toBe('测试商品')
    })
  })
})
