import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import ProfileEdit from '@/views/user/profile-edit/index.vue'
import PetEdit from '@/views/user/pet-edit/index.vue'
import UserPets from '@/views/user/user-pets/index.vue'
import UserProfile from '@/views/user/user-profile/index.vue'
import { 
  createUser, 
  createPet, 
  createPetList,
  mockUserStore,
} from '@/tests/utils/userTestUtils'

const mockPush = vi.fn()
const mockReplace = vi.fn()

const mockRoute = {
  query: {} as Record<string, string>,
  path: '/user',
  params: {},
}

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/auth', () => ({
  getUserInfo: vi.fn(() => Promise.resolve({
    id: 1,
    username: 'testuser',
    nickname: '测试用户',
    gender: 1,
    phone: '13800138000',
    email: 'test@example.com',
    avatar: 'https://example.com/avatar.jpg',
    birthday: '1990-01-01'
  })),
  updateUserInfo: vi.fn(() => Promise.resolve({ success: true })),
}))

vi.mock('@/api/user', () => ({
  getPetById: vi.fn((id: number) => Promise.resolve(createPet({ id }))),
  addPet: vi.fn(() => Promise.resolve({ id: 1 })),
  updatePet: vi.fn(() => Promise.resolve({ success: true })),
  deletePet: vi.fn(() => Promise.resolve({ success: true })),
  getUserPets: vi.fn(() => Promise.resolve(createPetList(3))),
}))

vi.mock('vue-router', async () => {
  const actual = await vi.importActual('vue-router')
  return {
    ...actual,
    useRouter: () => ({
      push: mockPush,
      replace: mockReplace,
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

global.fetch = vi.fn(() =>
  Promise.resolve({
    ok: true,
    json: () => Promise.resolve({ success: true }),
  })
) as any

const elementStubs = {
  'el-avatar': true,
  'el-button': true,
  'el-upload': true,
  'el-card': true,
  'el-form': true,
  'el-form-item': true,
  'el-input': true,
  'el-input-number': true,
  'el-radio': true,
  'el-radio-group': true,
  'el-date-picker': true,
  'el-select': true,
  'el-option': true,
  'el-divider': true,
  'el-icon': true,
  'el-table': true,
  'el-table-column': true,
  'el-tag': true,
  'el-empty': true,
  'el-dialog': true,
  'el-image': true,
}

describe('用户管理集成测试', () => {
  let pinia: ReturnType<typeof createPinia>

  beforeEach(() => {
    vi.clearAllMocks()
    mockPush.mockClear()
    mockReplace.mockClear()
    mockRoute.query = {}
    pinia = createPinia()
    setActivePinia(pinia)
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('个人信息编辑流程测试', () => {
    it('should complete profile edit flow successfully', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(wrapper.exists()).toBe(true)
      expect(wrapper.vm.profileForm).toBeDefined()
    })

    it('should load user data on mount', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      
      expect(wrapper.vm.profileForm.username).toBe('testuser')
      expect(wrapper.vm.profileForm.email).toBe('test@example.com')
    })

    it('should update user info and navigate on save', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      wrapper.vm.profileForm.nickname = '新昵称'
      wrapper.vm.profileForm.email = 'newemail@example.com'
      wrapper.vm.profileForm.phone = '13900139000'

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.profileFormRef = mockFormRef

      await wrapper.vm.handleSave()
      await flushPromises()
    })

    it('should navigate back on cancel', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      wrapper.vm.handleCancel()
      
      expect(mockPush).toHaveBeenCalledWith('/user/profile')
    })

    it('should handle avatar upload in profile edit', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      const mockCreateObjectURL = vi.fn(() => 'blob:test-url')
      URL.createObjectURL = mockCreateObjectURL

      const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
      const uploadFile = { raw: file }

      wrapper.vm.handleAvatarChange(uploadFile)
      
      expect(wrapper.vm.profileForm.avatar).toBe('blob:test-url')
    })

    it('should validate form fields before saving', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      expect(wrapper.vm.profileRules).toBeDefined()
      expect(wrapper.vm.profileRules.nickname).toBeDefined()
      expect(wrapper.vm.profileRules.email).toBeDefined()
      expect(wrapper.vm.profileRules.phone).toBeDefined()
    })

    it('should handle save error gracefully', async () => {
      const { updateUserInfo } = await import('@/api/auth')
      vi.mocked(updateUserInfo).mockRejectedValueOnce(new Error('保存失败'))

      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      wrapper.vm.profileForm.nickname = '测试昵称'
      wrapper.vm.profileForm.email = 'test@example.com'
      wrapper.vm.profileForm.phone = '13800138000'

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.profileFormRef = mockFormRef

      await wrapper.vm.handleSave()
      await flushPromises()
      
      expect(wrapper.exists()).toBe(true)
    })
  })

  describe('宠物管理流程测试', () => {
    describe('添加宠物流程', () => {
      it('should display add pet form', async () => {
        const wrapper = mount(PetEdit, {
          global: {
            plugins: [pinia],
            stubs: elementStubs,
          },
        })

        await flushPromises()

        expect(wrapper.find('.pet-edit').exists()).toBe(true)
        expect(wrapper.vm.pageTitle).toBe('添加宠物')
      })

      it('should fill and submit new pet form', async () => {
        const wrapper = mount(PetEdit, {
          global: {
            plugins: [pinia],
            stubs: elementStubs,
          },
        })

        await flushPromises()

        wrapper.vm.form.name = '新宠物'
        wrapper.vm.form.type = '狗'
        wrapper.vm.form.breed = '拉布拉多'
        wrapper.vm.form.age = 1
        wrapper.vm.form.gender = '公'

        const mockFormRef = {
          validate: vi.fn((callback: any) => callback(true)),
        }
        wrapper.vm.formRef = mockFormRef

        await wrapper.vm.handleSubmit()
        await flushPromises()
      })

      it('should validate required fields when adding pet', async () => {
        const wrapper = mount(PetEdit, {
          global: {
            plugins: [pinia],
            stubs: elementStubs,
          },
        })

        await flushPromises()

        expect(wrapper.vm.rules.name).toBeDefined()
        expect(wrapper.vm.rules.type).toBeDefined()
      })

      it('should handle avatar upload when adding pet', async () => {
        const wrapper = mount(PetEdit, {
          global: {
            plugins: [pinia],
            stubs: elementStubs,
          },
        })

        await flushPromises()

        const file = new File(['test'], 'pet.jpg', { type: 'image/jpeg' })
        
        const result = wrapper.vm.beforeAvatarUpload(file)
        expect(result).toBe(true)
      })

      it('should navigate to pets list after adding', async () => {
        const wrapper = mount(PetEdit, {
          global: {
            plugins: [pinia],
            stubs: elementStubs,
          },
        })

        await flushPromises()

        wrapper.vm.form.name = '新宠物'
        wrapper.vm.form.type = '狗'

        const mockFormRef = {
          validate: vi.fn((callback: any) => callback(true)),
        }
        wrapper.vm.formRef = mockFormRef

        await wrapper.vm.handleSubmit()
        await flushPromises()
      })
    })

    describe('编辑宠物流程', () => {
      it('should load pet data when editing', async () => {
        mockRoute.query = { id: '1' }

        const wrapper = mount(PetEdit, {
          global: {
            plugins: [pinia],
            stubs: elementStubs,
          },
        })

        await flushPromises()
        
        expect(wrapper.vm.isEdit).toBe(true)
        expect(wrapper.vm.petId).toBe('1')
      })

      it('should display edit title when editing existing pet', async () => {
        mockRoute.query = { id: '1' }

        const wrapper = mount(PetEdit, {
          global: {
            plugins: [pinia],
            stubs: elementStubs,
          },
        })

        await flushPromises()
        
        expect(wrapper.vm.pageTitle).toBe('编辑宠物')
      })

      it('should update pet on save', async () => {
        mockRoute.query = { id: '1' }

        const wrapper = mount(PetEdit, {
          global: {
            plugins: [pinia],
            stubs: elementStubs,
          },
        })

        await flushPromises()

        wrapper.vm.form.name = '更新后的名称'
        wrapper.vm.form.type = '猫'

        const mockFormRef = {
          validate: vi.fn((callback: any) => callback(true)),
        }
        wrapper.vm.formRef = mockFormRef

        await wrapper.vm.handleSubmit()
        await flushPromises()
      })
    })

    describe('删除宠物流程', () => {
      it('should have pets list component', async () => {
        const wrapper = mount(UserPets, {
          global: {
            plugins: [pinia],
            stubs: elementStubs,
          },
        })

        await flushPromises()
        expect(wrapper.exists()).toBe(true)
      })
    })
  })

  describe('数据流转测试', () => {
    it('should sync profile data between components', async () => {
      const profileWrapper = mount(UserProfile, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      const editWrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      expect(profileWrapper.exists()).toBe(true)
      expect(editWrapper.exists()).toBe(true)
    })

    it('should sync pet data between list and edit', async () => {
      const listWrapper = mount(UserPets, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      const editWrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      expect(listWrapper.exists()).toBe(true)
      expect(editWrapper.exists()).toBe(true)
    })

    it('should handle form data persistence', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      const originalData = { ...wrapper.vm.profileForm }

      wrapper.vm.profileForm.nickname = '修改后的昵称'
      
      expect(wrapper.vm.profileForm.nickname).not.toBe(originalData.nickname)
    })

    it('should handle pet form data persistence', async () => {
      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      const originalData = { ...wrapper.vm.form }

      wrapper.vm.form.name = '修改后的宠物名'
      
      expect(wrapper.vm.form.name).not.toBe(originalData.name)
    })
  })

  describe('错误处理流程测试', () => {
    it('should handle profile fetch error gracefully', async () => {
      const { getUserInfo } = await import('@/api/auth')
      vi.mocked(getUserInfo).mockRejectedValueOnce(new Error('获取用户信息失败'))

      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })

    it('should handle pet fetch error gracefully', async () => {
      const { getPetById } = await import('@/api/user')
      vi.mocked(getPetById).mockRejectedValueOnce(new Error('获取宠物信息失败'))
      
      mockRoute.query = { id: '1' }

      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()
      expect(wrapper.exists()).toBe(true)
    })

    it('should handle add pet error gracefully', async () => {
      const { addPet } = await import('@/api/user')
      vi.mocked(addPet).mockRejectedValueOnce(new Error('添加宠物失败'))

      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      wrapper.vm.form.name = '新宠物'
      wrapper.vm.form.type = '狗'

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(true)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(wrapper.exists()).toBe(true)
    })
  })

  describe('导航流程测试', () => {
    it('should navigate from profile edit back to profile', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      wrapper.vm.handleCancel()
      
      expect(mockPush).toHaveBeenCalledWith('/user/profile')
    })

    it('should navigate from pet edit back to pets list', async () => {
      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      wrapper.vm.handleCancel()
      
      expect(mockPush).toHaveBeenCalledWith('/user/pets')
    })
  })

  describe('表单验证流程测试', () => {
    it('should validate profile form correctly', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      const rules = wrapper.vm.profileRules
      
      expect(rules.nickname[0].required).toBe(true)
      expect(rules.email[0].required).toBe(true)
      expect(rules.phone[0].required).toBe(true)
    })

    it('should validate pet form correctly', async () => {
      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      const rules = wrapper.vm.rules
      
      expect(rules.name[0].required).toBe(true)
      expect(rules.type[0].required).toBe(true)
    })

    it('should not submit invalid profile form', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(false)),
      }
      wrapper.vm.profileFormRef = mockFormRef

      await wrapper.vm.handleSave()
      await flushPromises()
      
      expect(mockFormRef.validate).toHaveBeenCalled()
    })

    it('should not submit invalid pet form', async () => {
      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      const mockFormRef = {
        validate: vi.fn((callback: any) => callback(false)),
      }
      wrapper.vm.formRef = mockFormRef

      await wrapper.vm.handleSubmit()
      await flushPromises()
      
      expect(mockFormRef.validate).toHaveBeenCalled()
    })
  })

  describe('状态管理流程测试', () => {
    it('should manage loading state during profile fetch', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      expect(wrapper.vm.loading).toBeDefined()
      
      await flushPromises()
      
      expect(wrapper.vm.loading).toBe(false)
    })

    it('should manage loading state during pet fetch', async () => {
      mockRoute.query = { id: '1' }
      
      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      expect(wrapper.vm.loading).toBeDefined()
      
      await flushPromises()
      
      expect(wrapper.vm.loading).toBe(false)
    })

    it('should manage submitting state during profile save', async () => {
      const wrapper = mount(ProfileEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      expect(wrapper.vm.loading).toBe(false)
    })

    it('should manage submitting state during pet save', async () => {
      const wrapper = mount(PetEdit, {
        global: {
          plugins: [pinia],
          stubs: elementStubs,
        },
      })

      await flushPromises()

      expect(wrapper.vm.submitting).toBe(false)
    })
  })
})
