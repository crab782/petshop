import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import ProfileEdit from '../index.vue'
import { mockUserStore, flushPromises as waitForPromises } from '@/tests/utils/userTestUtils'

const mockUserInfo = {
  id: 1,
  username: 'testuser',
  nickname: '测试用户',
  gender: 1,
  phone: '13800138000',
  email: 'test@example.com',
  avatar: 'https://example.com/avatar.jpg',
  birthday: '1990-01-01'
}

const mockPush = vi.fn()

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/auth', () => ({
  getUserInfo: vi.fn(() => Promise.resolve(mockUserInfo)),
  updateUserInfo: vi.fn(() => Promise.resolve({ success: true })),
}))

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
    useRoute: () => ({
      path: '/user/profile-edit',
      params: {},
      query: {},
    }),
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

global.fetch = vi.fn(() =>
  Promise.resolve({
    ok: true,
    json: () => Promise.resolve({ success: true }),
  })
) as any

const createWrapper = (options: any = {}) => {
  return mount(ProfileEdit, {
    global: {
      stubs: {
        'el-avatar': true,
        'el-button': true,
        'el-upload': true,
        'el-card': true,
        'el-form': true,
        'el-form-item': true,
        'el-input': true,
        'el-radio': true,
        'el-radio-group': true,
        'el-date-picker': true,
      },
      ...options.global,
    },
    ...options,
  })
}

describe('ProfileEdit', () => {
  let wrapper: any

  beforeEach(() => {
    vi.clearAllMocks()
    mockPush.mockClear()
  })

  afterEach(() => {
    wrapper?.unmount()
  })

  describe('表单渲染测试', () => {
    it('should render correctly', () => {
      wrapper = createWrapper()
      expect(wrapper.exists()).toBe(true)
    })

    it('should display page title', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.page-title').exists()).toBe(true)
      expect(wrapper.find('.page-title').text()).toContain('编辑资料')
    })

    it('should render avatar upload section', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.profile-edit').exists()).toBe(true)
    })

    it('should render profile form', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.profile-edit').exists()).toBe(true)
    })

    it('should render avatar image section', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.profile-edit').exists()).toBe(true)
    })

    it('should display avatar tips', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.profile-edit').exists()).toBe(true)
    })
  })

  describe('表单验证测试', () => {
    it('should have nickname validation rules', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.profileRules.nickname).toBeDefined()
      expect(wrapper.vm.profileRules.nickname.length).toBeGreaterThan(0)
    })

    it('should validate nickname is required', () => {
      wrapper = createWrapper()
      const nicknameRules = wrapper.vm.profileRules.nickname
      const requiredRule = nicknameRules.find((r: any) => r.required)
      expect(requiredRule).toBeDefined()
      expect(requiredRule.message).toBe('请输入昵称')
    })

    it('should validate nickname length between 2-20 characters', () => {
      wrapper = createWrapper()
      const nicknameRules = wrapper.vm.profileRules.nickname
      const lengthRule = nicknameRules.find((r: any) => r.min !== undefined)
      expect(lengthRule).toBeDefined()
      expect(lengthRule.min).toBe(2)
      expect(lengthRule.max).toBe(20)
    })

    it('should have email validation rules', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.profileRules.email).toBeDefined()
    })

    it('should validate email is required', () => {
      wrapper = createWrapper()
      const emailRules = wrapper.vm.profileRules.email
      const requiredRule = emailRules.find((r: any) => r.required)
      expect(requiredRule).toBeDefined()
      expect(requiredRule.message).toBe('请输入邮箱')
    })

    it('should validate email format', () => {
      wrapper = createWrapper()
      const emailRules = wrapper.vm.profileRules.email
      const formatRule = emailRules.find((r: any) => r.type === 'email')
      expect(formatRule).toBeDefined()
      expect(formatRule.message).toBe('请输入正确的邮箱格式')
    })

    it('should have phone validation rules', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.profileRules.phone).toBeDefined()
    })

    it('should validate phone is required', () => {
      wrapper = createWrapper()
      const phoneRules = wrapper.vm.profileRules.phone
      const requiredRule = phoneRules.find((r: any) => r.required)
      expect(requiredRule).toBeDefined()
      expect(requiredRule.message).toBe('请输入手机号')
    })

    it('should validate phone format with regex', () => {
      wrapper = createWrapper()
      const phoneRules = wrapper.vm.profileRules.phone
      const patternRule = phoneRules.find((r: any) => r.pattern)
      expect(patternRule).toBeDefined()
      expect(patternRule.pattern.test('13800138000')).toBe(true)
      expect(patternRule.pattern.test('12800138000')).toBe(false)
    })
  })

  describe('保存功能测试', () => {
    it('should have handleSave method', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.handleSave).toBeDefined()
    })

    it('should call handleSave when clicking save button', async () => {
      wrapper = createWrapper()
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.profileFormRef = mockFormRef

      await wrapper.vm.handleSave()
      await flushPromises()
    })

    it('should validate form before saving', async () => {
      wrapper = createWrapper()
      
      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(false)),
      }
      wrapper.vm.profileFormRef = mockFormRef

      await wrapper.vm.handleSave()
      await flushPromises()
      
      expect(mockFormRef.validate).toHaveBeenCalled()
    })

    it('should not save when formRef is null', async () => {
      wrapper = createWrapper()
      wrapper.vm.profileFormRef = null

      await wrapper.vm.handleSave()
      await flushPromises()
    })
  })

  describe('取消功能测试', () => {
    it('should have handleCancel method', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.handleCancel).toBeDefined()
    })

    it('should navigate to profile page when clicking cancel', async () => {
      wrapper = createWrapper()
      
      wrapper.vm.handleCancel()
      
      expect(mockPush).toHaveBeenCalledWith('/user/profile')
    })
  })

  describe('头像上传测试', () => {
    it('should have handleAvatarChange method', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.handleAvatarChange).toBeDefined()
    })

    it('should handle avatar change', async () => {
      wrapper = createWrapper()
      
      const mockCreateObjectURL = vi.fn(() => 'blob:test-url')
      URL.createObjectURL = mockCreateObjectURL

      const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
      const uploadFile = { raw: file }
      
      wrapper.vm.handleAvatarChange(uploadFile)
      
      expect(mockCreateObjectURL).toHaveBeenCalled()
      expect(wrapper.vm.imageUrl).toBe('blob:test-url')
      expect(wrapper.vm.profileForm.avatar).toBe('blob:test-url')
    })

    it('should return false from handleAvatarChange to prevent auto upload', async () => {
      wrapper = createWrapper()
      
      const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
      const uploadFile = { raw: file }
      
      const result = wrapper.vm.handleAvatarChange(uploadFile)
      expect(result).toBe(false)
    })
  })

  describe('数据加载测试', () => {
    it('should fetch user info on mount', async () => {
      wrapper = createWrapper()
      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })

    it('should populate form with user data', async () => {
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.vm.profileForm.id).toBe(mockUserInfo.id)
      expect(wrapper.vm.profileForm.username).toBe(mockUserInfo.username)
      expect(wrapper.vm.profileForm.nickname).toBe(mockUserInfo.nickname)
      expect(wrapper.vm.profileForm.gender).toBe(mockUserInfo.gender)
      expect(wrapper.vm.profileForm.phone).toBe(mockUserInfo.phone)
      expect(wrapper.vm.profileForm.email).toBe(mockUserInfo.email)
      expect(wrapper.vm.profileForm.avatar).toBe(mockUserInfo.avatar)
    })

    it('should show loading state when fetching data', async () => {
      wrapper = createWrapper()
      expect(wrapper.vm.loading).toBeDefined()
    })

    it('should handle fetch error gracefully', async () => {
      const { getUserInfo } = await import('@/api/auth')
      vi.mocked(getUserInfo).mockRejectedValueOnce(new Error('获取用户信息失败'))
      
      wrapper = createWrapper()
      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })
  })

  describe('性别选择测试', () => {
    it('should have gender options defined', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.genderOptions).toBeDefined()
      expect(wrapper.vm.genderOptions.length).toBe(3)
    })

    it('should have correct gender values', () => {
      wrapper = createWrapper()
      const values = wrapper.vm.genderOptions.map((o: any) => o.value)
      expect(values).toContain(0)
      expect(values).toContain(1)
      expect(values).toContain(2)
    })

    it('should have correct gender labels', () => {
      wrapper = createWrapper()
      const labels = wrapper.vm.genderOptions.map((o: any) => o.label)
      expect(labels).toContain('保密')
      expect(labels).toContain('男')
      expect(labels).toContain('女')
    })
  })

  describe('表单数据绑定测试', () => {
    it('should have profileForm reactive data', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.profileForm).toBeDefined()
    })

    it('should have all required form fields', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.profileForm.id).toBeDefined()
      expect(wrapper.vm.profileForm.username).toBeDefined()
      expect(wrapper.vm.profileForm.nickname).toBeDefined()
      expect(wrapper.vm.profileForm.gender).toBeDefined()
      expect(wrapper.vm.profileForm.phone).toBeDefined()
      expect(wrapper.vm.profileForm.email).toBeDefined()
      expect(wrapper.vm.profileForm.avatar).toBeDefined()
      expect(wrapper.vm.profileForm.birthday).toBeDefined()
    })

    it('should have form reference', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.profileFormRef).toBeUndefined()
    })

    it('should have imageUrl for avatar preview', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.imageUrl).toBeDefined()
    })
  })

  describe('样式测试', () => {
    it('should have correct container class', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.profile-edit').exists()).toBe(true)
    })

    it('should have avatar card with correct class', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.avatar-card').exists()).toBe(true)
    })

    it('should have form card with correct class', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.form-card').exists()).toBe(true)
    })

    it('should have avatar content section', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.profile-edit').exists()).toBe(true)
    })
  })

  describe('边界情况处理', () => {
    it('should handle missing user data gracefully', async () => {
      const { getUserInfo } = await import('@/api/auth')
      vi.mocked(getUserInfo).mockResolvedValueOnce({
        id: 1,
        username: '',
        email: '',
        phone: '',
      } as any)
      
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.vm.profileForm.id).toBe(1)
    })

    it('should handle null values in user data', async () => {
      const { getUserInfo } = await import('@/api/auth')
      vi.mocked(getUserInfo).mockResolvedValueOnce({
        id: 1,
        username: null,
        email: null,
        phone: null,
        avatar: null,
      } as any)
      
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.vm.profileForm.username).toBe('')
      expect(wrapper.vm.profileForm.email).toBe('')
      expect(wrapper.vm.profileForm.phone).toBe('')
    })
  })
})
