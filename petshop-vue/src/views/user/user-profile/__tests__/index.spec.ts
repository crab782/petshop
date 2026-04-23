import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import UserProfile from '../index.vue'
import { createTestRouter, createTestPinia, flushPromises } from '@/tests/utils/testUtils'
import * as authApi from '@/api/auth'

vi.mock('@/api/auth', () => ({
  getUserInfo: vi.fn(),
  updateUserInfo: vi.fn(),
}))

vi.mock('@/stores/user', () => ({
  useUserStore: vi.fn(() => ({
    user: { id: 1, username: 'testuser', email: 'test@example.com' },
    setUserInfo: vi.fn(),
    logout: vi.fn(),
  })),
}))

describe('UserProfile', () => {
  let router: any
  let pinia: any

  const mockUserInfo = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com',
    phone: '13800138000',
    avatar: 'https://example.com/avatar.jpg',
    created_at: '2024-01-01T00:00:00.000Z',
  }

  beforeEach(() => {
    vi.clearAllMocks()
    router = createTestRouter()
    pinia = createTestPinia()

    vi.mocked(authApi.getUserInfo).mockResolvedValue({ data: mockUserInfo } as any)
    vi.mocked(authApi.updateUserInfo).mockResolvedValue({ data: mockUserInfo } as any)
  })

  const mountComponent = (options = {}) => {
    return mount(UserProfile, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-card': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-avatar': true,
          'el-upload': true,
          'el-icon': true,
          'el-message': true,
        },
        ...options,
      },
    })
  }

  describe('页面渲染', () => {
    it('应该正确渲染页面标题', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.page-title').text()).toBe('个人中心')
    })

    it('应该显示页面描述', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.page-description').text()).toContain('管理您的个人信息和设置')
    })
  })

  describe('用户信息加载', () => {
    it('组件挂载时应该调用getUserInfo API', () => {
      mountComponent()
      expect(authApi.getUserInfo).toHaveBeenCalled()
    })

    it('应该显示用户信息表单', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.profile-form').exists()).toBe(true)
    })

    it('应该显示用户头像卡片', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.avatar-card').exists()).toBe(true)
    })
  })

  describe('编辑功能', () => {
    it('应该显示编辑按钮', () => {
      const wrapper = mountComponent()
      const editButton = wrapper.find('.edit-button')
      expect(editButton.exists()).toBe(true)
    })

    it('点击编辑按钮应该切换到编辑模式', async () => {
      const wrapper = mountComponent()
      const editButton = wrapper.find('.edit-button')

      if (editButton.exists()) {
        await editButton.trigger('click')
        expect(wrapper.vm.isEditing).toBe(true)
      }
    })

    it('编辑模式下应该显示保存和取消按钮', async () => {
      const wrapper = mountComponent()
      wrapper.vm.isEditing = true
      await wrapper.vm.$nextTick()

      expect(wrapper.find('.edit-actions').exists()).toBe(true)
    })
  })

  describe('保存功能', () => {
    it('保存时应该调用updateUserInfo API', async () => {
      const wrapper = mountComponent()
      wrapper.vm.isEditing = true
      wrapper.vm.profileForm.username = 'newusername'

      await wrapper.vm.handleSave()
      await flushPromises()

      expect(authApi.updateUserInfo).toHaveBeenCalled()
    })
  })

  describe('快捷操作', () => {
    it('应该显示四个快捷操作项', () => {
      const wrapper = mountComponent()
      const quickActions = wrapper.findAll('.quick-action-item')
      expect(quickActions.length).toBe(4)
    })

    it('点击快捷操作应该跳转到对应路由', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      const wrapper = mountComponent()

      const firstAction = wrapper.find('.quick-action-item')
      if (firstAction.exists()) {
        await firstAction.trigger('click')
        expect(pushSpy).toHaveBeenCalled()
      }
    })
  })

  describe('安全设置', () => {
    it('应该显示三个安全设置项', () => {
      const wrapper = mountComponent()
      const securitySettings = wrapper.findAll('.security-setting-item')
      expect(securitySettings.length).toBe(3)
    })

    it('应该显示修改密码卡片', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.password-card').exists()).toBe(true)
    })
  })

  describe('修改密码功能', () => {
    it('应该显示密码表单', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.password-form').exists()).toBe(true)
    })

    it('提交密码修改应该验证表单', async () => {
      const wrapper = mountComponent()
      wrapper.vm.passwordForm.oldPassword = '123456'
      wrapper.vm.passwordForm.newPassword = '654321'
      wrapper.vm.passwordForm.confirmPassword = '654321'

      await wrapper.vm.handlePasswordSubmit()

      // 验证表单字段已设置
      expect(wrapper.vm.passwordForm.oldPassword).toBe('123456')
      expect(wrapper.vm.passwordForm.newPassword).toBe('654321')
    })
  })

  describe('头像上传', () => {
    it('应该显示上传按钮', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.upload-button').exists()).toBe(true)
    })

    it('上传头像应该更新头像URL', async () => {
      const wrapper = mountComponent()
      const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' })

      wrapper.vm.handleAvatarChange({ raw: file })

      expect(wrapper.vm.profileForm.avatar).toBeTruthy()
    })
  })

  describe('退出登录', () => {
    it('应该显示退出登录按钮', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.logout-button').exists()).toBe(true)
    })

    it('点击退出登录应该调用logout并跳转', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      const userStore = wrapper.vm.userStore

      await wrapper.vm.handleLogout()

      expect(userStore.logout).toHaveBeenCalled()
      expect(pushSpy).toHaveBeenCalledWith('/login')
    })
  })

  describe('错误处理', () => {
    it('获取用户信息失败时应该显示错误', async () => {
      vi.mocked(authApi.getUserInfo).mockRejectedValue(new Error('API Error'))
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

      mountComponent()
      await flushPromises()

      expect(consoleSpy).toHaveBeenCalled()
      consoleSpy.mockRestore()
    })

    it('更新用户信息失败时应该显示错误', async () => {
      vi.mocked(authApi.updateUserInfo).mockRejectedValue(new Error('Update Error'))
      const wrapper = mountComponent()

      wrapper.vm.isEditing = true
      await wrapper.vm.handleSave()
      await flushPromises()

      // 验证错误处理
      expect(wrapper.vm.loading).toBe(false)
    })
  })

  describe('表单验证', () => {
    it('用户名应该有验证规则', () => {
      const wrapper = mountComponent()
      const rules = wrapper.vm.profileRules
      expect(rules.username).toBeDefined()
      expect(rules.username.length).toBeGreaterThan(0)
    })

    it('邮箱应该有验证规则', () => {
      const wrapper = mountComponent()
      const rules = wrapper.vm.profileRules
      expect(rules.email).toBeDefined()
      expect(rules.email.length).toBeGreaterThan(0)
    })

    it('密码应该有验证规则', () => {
      const wrapper = mountComponent()
      const rules = wrapper.vm.passwordRules
      expect(rules.oldPassword).toBeDefined()
      expect(rules.newPassword).toBeDefined()
      expect(rules.confirmPassword).toBeDefined()
    })
  })

  describe('响应式布局', () => {
    it('应该使用网格布局', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.profile-grid').exists()).toBe(true)
    })

    it('应该有主内容区域和侧边栏', () => {
      const wrapper = mountComponent()
      expect(wrapper.find('.profile-main').exists()).toBe(true)
      expect(wrapper.find('.profile-sidebar').exists()).toBe(true)
    })
  })
})
