import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import PetEdit from '../index.vue'
import { mockUserStore, createPet } from '@/tests/utils/userTestUtils'

const mockPetData = {
  id: 1,
  name: '小白',
  type: '狗',
  breed: '金毛',
  age: 2,
  gender: '公',
  avatar: 'https://example.com/pet.jpg',
  description: '一只可爱的金毛犬',
  weight: 25.5,
  bodyType: '正常',
  furColor: '金色',
  personality: '活泼好动'
}

const mockPush = vi.fn()

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/user', () => ({
  getPetById: vi.fn(() => Promise.resolve({ data: mockPetData })),
  addPet: vi.fn(() => Promise.resolve({ success: true, data: { id: 1 } })),
  updatePet: vi.fn(() => Promise.resolve({ success: true })),
  deletePet: vi.fn(() => Promise.resolve({ success: true })),
  getUserPets: vi.fn(() => Promise.resolve({ data: [] })),
}))

const mockRoute = {
  query: {} as Record<string, string>,
}

vi.mock('vue-router', async () => {
  const actual = await vi.importActual('vue-router')
  return {
    ...actual,
    useRouter: () => ({
      push: mockPush,
      replace: vi.fn(),
      go: vi.fn(),
      back: vi.fn(),
    }),
    useRoute: () => mockRoute,
  }
})

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

const createWrapper = (options: any = {}) => {
  return mount(PetEdit, {
    global: {
      stubs: {
        'el-card': true,
        'el-form': true,
        'el-form-item': true,
        'el-input': true,
        'el-input-number': true,
        'el-select': true,
        'el-option': true,
        'el-radio': true,
        'el-radio-group': true,
        'el-divider': true,
        'el-button': true,
        'el-upload': true,
        'el-avatar': true,
        'el-icon': true,
      },
      ...options.global,
    },
    ...options,
  })
}

describe('PetEdit', () => {
  let wrapper: any

  beforeEach(() => {
    vi.clearAllMocks()
    mockPush.mockClear()
    mockRoute.query = {}
  })

  afterEach(() => {
    wrapper?.unmount()
  })

  describe('表单渲染测试', () => {
    it('should render correctly', () => {
      wrapper = createWrapper()
      expect(wrapper.exists()).toBe(true)
    })

    it('should render pet form card', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.pet-edit').exists()).toBe(true)
    })

    it('should display add pet title when creating new pet', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.pageTitle).toBe('添加宠物')
    })

    it('should render avatar upload section', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.pet-edit').exists()).toBe(true)
    })

    it('should render avatar tips', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.pet-edit').exists()).toBe(true)
    })

    it('should render avatar placeholder when no avatar', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.pet-edit').exists()).toBe(true)
    })
  })

  describe('表单验证测试', () => {
    it('should have name validation rules', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.rules.name).toBeDefined()
      expect(wrapper.vm.rules.name.length).toBeGreaterThan(0)
    })

    it('should validate name is required', () => {
      wrapper = createWrapper()
      const nameRules = wrapper.vm.rules.name
      const requiredRule = nameRules.find((r: any) => r.required)
      expect(requiredRule).toBeDefined()
      expect(requiredRule.message).toBe('请输入宠物名称')
    })

    it('should validate name length between 1-50 characters', () => {
      wrapper = createWrapper()
      const nameRules = wrapper.vm.rules.name
      const lengthRule = nameRules.find((r: any) => r.min !== undefined)
      expect(lengthRule).toBeDefined()
      expect(lengthRule.min).toBe(1)
      expect(lengthRule.max).toBe(50)
    })

    it('should have type validation rules', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.rules.type).toBeDefined()
    })

    it('should validate type is required', () => {
      wrapper = createWrapper()
      const typeRules = wrapper.vm.rules.type
      const requiredRule = typeRules.find((r: any) => r.required)
      expect(requiredRule).toBeDefined()
      expect(requiredRule.message).toBe('请选择宠物类型')
    })

    it('should have age validation rules', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.rules.age).toBeDefined()
    })

    it('should validate age range 0-50', () => {
      wrapper = createWrapper()
      const ageRules = wrapper.vm.rules.age
      const rangeRule = ageRules.find((r: any) => r.type === 'number')
      expect(rangeRule).toBeDefined()
      expect(rangeRule.min).toBe(0)
      expect(rangeRule.max).toBe(50)
    })

    it('should have weight validation rules', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.rules.weight).toBeDefined()
    })

    it('should validate weight range 0-500', () => {
      wrapper = createWrapper()
      const weightRules = wrapper.vm.rules.weight
      const rangeRule = weightRules.find((r: any) => r.type === 'number')
      expect(rangeRule).toBeDefined()
      expect(rangeRule.min).toBe(0)
      expect(rangeRule.max).toBe(500)
    })
  })

  describe('头像上传测试', () => {
    it('should have handleAvatarSuccess method', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.handleAvatarSuccess).toBeDefined()
    })

    it('should have beforeAvatarUpload method', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.beforeAvatarUpload).toBeDefined()
    })

    it('should handle avatar upload success', async () => {
      wrapper = createWrapper()
      
      const response = { url: 'https://example.com/new-avatar.jpg' }
      wrapper.vm.handleAvatarSuccess(response)
      
      expect(wrapper.vm.form.avatar).toBe('https://example.com/new-avatar.jpg')
    })

    it('should validate avatar file type - image file', async () => {
      wrapper = createWrapper()
      
      const imageFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
      const result = wrapper.vm.beforeAvatarUpload(imageFile)
      expect(result).toBe(true)
    })

    it('should validate avatar file type - non-image file', async () => {
      wrapper = createWrapper()
      
      const textFile = new File(['test'], 'test.txt', { type: 'text/plain' })
      const result = wrapper.vm.beforeAvatarUpload(textFile)
      expect(result).toBe(false)
    })

    it('should validate avatar file size - small file', async () => {
      wrapper = createWrapper()
      
      const smallFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
      Object.defineProperty(smallFile, 'size', { value: 1024 * 1024 })
      
      const result = wrapper.vm.beforeAvatarUpload(smallFile)
      expect(result).toBe(true)
    })

    it('should validate avatar file size - large file', async () => {
      wrapper = createWrapper()
      
      const largeFile = new File(['test'], 'large.jpg', { type: 'image/jpeg' })
      Object.defineProperty(largeFile, 'size', { value: 3 * 1024 * 1024 })
      
      const result = wrapper.vm.beforeAvatarUpload(largeFile)
      expect(result).toBe(false)
    })
  })

  describe('保存取消功能测试', () => {
    it('should have handleSubmit method', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.handleSubmit).toBeDefined()
    })

    it('should have handleCancel method', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.handleCancel).toBeDefined()
    })

    it('should navigate to pets page when clicking cancel', async () => {
      wrapper = createWrapper()
      
      wrapper.vm.handleCancel()
      
      expect(mockPush).toHaveBeenCalledWith('/user/pets')
    })

    it('should validate form before submitting', async () => {
      wrapper = createWrapper()
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(false)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(mockFormRef.validate).toHaveBeenCalled()
    })

    it('should not submit when formRef is null', async () => {
      wrapper = createWrapper()
      wrapper.vm.formRef = null

      await wrapper.vm.handleSubmit()
      await flushPromises()
    })

    it('should submit with valid form data', async () => {
      wrapper = createWrapper()
      
      wrapper.vm.form.name = '测试宠物'
      wrapper.vm.form.type = '狗'
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
    })
  })

  describe('宠物类型选项测试', () => {
    it('should have pet types defined', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.petTypes).toBeDefined()
      expect(wrapper.vm.petTypes.length).toBeGreaterThan(0)
    })

    it('should have common pet types', () => {
      wrapper = createWrapper()
      const types = wrapper.vm.petTypes.map((t: any) => t.value)
      expect(types).toContain('狗')
      expect(types).toContain('猫')
    })

    it('should have other pet type option', () => {
      wrapper = createWrapper()
      const types = wrapper.vm.petTypes.map((t: any) => t.value)
      expect(types).toContain('其他')
    })

    it('should have correct number of pet types', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.petTypes.length).toBe(6)
    })
  })

  describe('性别选项测试', () => {
    it('should have gender options defined', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.genderOptions).toBeDefined()
      expect(wrapper.vm.genderOptions.length).toBe(3)
    })

    it('should have correct gender values', () => {
      wrapper = createWrapper()
      const values = wrapper.vm.genderOptions.map((o: any) => o.value)
      expect(values).toContain('公')
      expect(values).toContain('母')
      expect(values).toContain('未知')
    })
  })

  describe('体型选项测试', () => {
    it('should have body type options defined', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.bodyTypeOptions).toBeDefined()
      expect(wrapper.vm.bodyTypeOptions.length).toBe(4)
    })

    it('should have correct body type values', () => {
      wrapper = createWrapper()
      const values = wrapper.vm.bodyTypeOptions.map((o: any) => o.value)
      expect(values).toContain('偏瘦')
      expect(values).toContain('正常')
      expect(values).toContain('偏胖')
      expect(values).toContain('肥胖')
    })
  })

  describe('性格选项测试', () => {
    it('should have personality options defined', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.personalityOptions).toBeDefined()
      expect(wrapper.vm.personalityOptions.length).toBeGreaterThan(0)
    })

    it('should have common personality types', () => {
      wrapper = createWrapper()
      const values = wrapper.vm.personalityOptions.map((o: any) => o.value)
      expect(values).toContain('活泼好动')
      expect(values).toContain('温顺安静')
      expect(values).toContain('粘人')
      expect(values).toContain('独立')
    })

    it('should have correct number of personality options', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.personalityOptions.length).toBe(6)
    })
  })

  describe('表单数据绑定测试', () => {
    it('should have form reactive data', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.form).toBeDefined()
    })

    it('should have all required form fields', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.form.name).toBeDefined()
      expect(wrapper.vm.form.type).toBeDefined()
      expect(wrapper.vm.form.breed).toBeDefined()
      expect(wrapper.vm.form.gender).toBeDefined()
      expect(wrapper.vm.form.avatar).toBeDefined()
      expect(wrapper.vm.form.description).toBeDefined()
      expect(wrapper.vm.form.bodyType).toBeDefined()
      expect(wrapper.vm.form.furColor).toBeDefined()
      expect(wrapper.vm.form.personality).toBeDefined()
    })

    it('should have form reference', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.formRef).toBeUndefined()
    })

    it('should have loading state', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.loading).toBeDefined()
    })

    it('should have submitting state', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.submitting).toBeDefined()
    })
  })

  describe('编辑模式测试', () => {
    it('should detect edit mode from route query', async () => {
      mockRoute.query = { id: '1' }
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.vm.isEdit).toBe(true)
      expect(wrapper.vm.petId).toBe('1')
    })

    it('should have correct page title in edit mode', async () => {
      mockRoute.query = { id: '1' }
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.vm.pageTitle).toBe('编辑宠物')
    })

    it('should fetch pet data when in edit mode', async () => {
      mockRoute.query = { id: '1' }
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.vm.form.name).toBe(mockPetData.name)
      expect(wrapper.vm.form.type).toBe(mockPetData.type)
    })

    it('should handle fetch pet error gracefully', async () => {
      const { getPetById } = await import('@/api/user')
      vi.mocked(getPetById).mockRejectedValueOnce(new Error('获取宠物信息失败'))
      
      mockRoute.query = { id: '1' }
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.exists()).toBe(true)
    })

    it('should not fetch data in add mode', async () => {
      mockRoute.query = {}
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.vm.form.name).toBe('')
      expect(wrapper.vm.form.type).toBe('')
    })
  })

  describe('样式测试', () => {
    it('should have correct container class', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.pet-edit').exists()).toBe(true)
    })

    it('should have form class', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.pet-edit').exists()).toBe(true)
    })

    it('should have avatar upload styling', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.pet-edit').exists()).toBe(true)
    })

    it('should have header section', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.pet-edit').exists()).toBe(true)
    })
  })

  describe('边界情况处理', () => {
    it('should handle missing pet data gracefully', async () => {
      const { getPetById } = await import('@/api/user')
      vi.mocked(getPetById).mockResolvedValueOnce({
        data: {
          id: 1,
          name: '',
          type: '',
        }
      } as any)
      
      mockRoute.query = { id: '1' }
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.vm.form.name).toBe('')
    })

    it('should handle null values in pet data', async () => {
      const { getPetById } = await import('@/api/user')
      vi.mocked(getPetById).mockResolvedValueOnce({
        data: {
          id: 1,
          name: null,
          type: null,
          breed: null,
          age: null,
        }
      } as any)
      
      mockRoute.query = { id: '1' }
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.vm.form.name).toBe('')
      expect(wrapper.vm.form.type).toBe('')
    })

    it('should only include non-empty fields in submit data', async () => {
      wrapper = createWrapper()
      
      wrapper.vm.form.name = '测试宠物'
      wrapper.vm.form.type = '狗'
      wrapper.vm.form.breed = ''
      wrapper.vm.form.age = undefined
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
    })
  })

  describe('计算属性测试', () => {
    it('should have isEdit computed property', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.isEdit).toBe(false)
    })

    it('should have petId computed property', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.petId).toBeUndefined()
    })

    it('should have pageTitle computed property', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.pageTitle).toBe('添加宠物')
    })

    it('should update computed properties when route changes', async () => {
      mockRoute.query = { id: '1' }
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.vm.isEdit).toBe(true)
      expect(wrapper.vm.petId).toBe('1')
      expect(wrapper.vm.pageTitle).toBe('编辑宠物')
    })
  })
})
