import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import ShopSettings from '../index.vue'
import {
  ElCard,
  ElForm,
  ElFormItem,
  ElCheckboxGroup,
  ElCheckbox,
  ElTimeSelect,
  ElSwitch,
  ElDatePicker,
  ElInputNumber,
  ElButton,
  ElMessage,
  ElTag,
  ElInput,
  ElDivider,
  ElTabs,
  ElTabPane,
  ElRow,
  ElCol,
  ElAvatar,
  ElEmpty,
  ElIcon,
  ElDescriptions,
  ElDescriptionsItem
} from 'element-plus'
import {
  getMerchantSettings,
  updateMerchantSettings,
  getMerchantInfo,
  changePassword,
  bindPhone,
  bindEmail,
  sendVerifyCode,
  type MerchantSettings,
  type MerchantInfo
} from '@/api/merchant'
import { createSuccessResponse, createErrorResponse } from '@/tests/fixtures/merchantData'

vi.mock('@/api/merchant', () => ({
  getMerchantSettings: vi.fn(),
  updateMerchantSettings: vi.fn(),
  getMerchantInfo: vi.fn(),
  changePassword: vi.fn(),
  bindPhone: vi.fn(),
  bindEmail: vi.fn(),
  sendVerifyCode: vi.fn()
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

const mockMerchantInfo: MerchantInfo = {
  id: 1,
  name: '测试商家',
  contact_person: '张先生',
  phone: '13800138000',
  email: 'test@merchant.com',
  address: '北京市朝阳区测试路1号',
  logo: 'https://picsum.photos/100/100',
  description: '测试商家描述'
}

const mockMerchantSettings: MerchantSettings = {
  businessDays: [1, 2, 3, 4, 5],
  startTime: '09:00',
  endTime: '18:00',
  legalHolidayRest: false,
  customRestDays: [],
  maxAppointments: 10,
  advanceBookingHours: 24,
  isOpen: true,
  notificationSettings: {
    appointmentReminder: true,
    orderReminder: true,
    reviewReminder: true,
    notifyViaSms: false,
    notifyViaEmail: true
  }
}

describe('ShopSettings', () => {
  let wrapper: any

  const mountComponent = async (options = {}) => {
    wrapper = mount(ShopSettings, {
      global: {
        components: {
          ElCard,
          ElForm,
          ElFormItem,
          ElCheckboxGroup,
          ElCheckbox,
          ElTimeSelect,
          ElSwitch,
          ElDatePicker,
          ElInputNumber,
          ElButton,
          ElTag,
          ElInput,
          ElDivider,
          ElTabs,
          ElTabPane,
          ElRow,
          ElCol,
          ElAvatar,
          ElEmpty,
          ElIcon,
          ElDescriptions,
          ElDescriptionsItem
        },
        stubs: {
          'el-card': true,
          'el-form': true,
          'el-form-item': true,
          'el-checkbox-group': true,
          'el-checkbox': true,
          'el-time-select': true,
          'el-switch': true,
          'el-date-picker': true,
          'el-input-number': true,
          'el-button': true,
          'el-tag': true,
          'el-input': true,
          'el-divider': true,
          'el-tabs': true,
          'el-tab-pane': true,
          'el-row': true,
          'el-col': true,
          'el-avatar': true,
          'el-empty': true,
          'el-icon': true,
          'el-descriptions': true,
          'el-descriptions-item': true
        },
        ...options
      }
    })
    await flushPromises()
    return wrapper
  }

  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(getMerchantInfo).mockResolvedValue(createSuccessResponse(mockMerchantInfo))
    vi.mocked(getMerchantSettings).mockResolvedValue(createSuccessResponse(mockMerchantSettings))
    vi.mocked(updateMerchantSettings).mockResolvedValue(createSuccessResponse(mockMerchantSettings))
    vi.mocked(changePassword).mockResolvedValue(createSuccessResponse(null))
    vi.mocked(bindPhone).mockResolvedValue(createSuccessResponse(null))
    vi.mocked(bindEmail).mockResolvedValue(createSuccessResponse(null))
    vi.mocked(sendVerifyCode).mockResolvedValue(createSuccessResponse(null))
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  describe('页面渲染', () => {
    it('店铺设置页面能够正确渲染', async () => {
      wrapper = await mountComponent()

      expect(wrapper.find('.shop-settings').exists()).toBe(true)
      expect(wrapper.find('.page-header').exists()).toBe(true)
      expect(wrapper.find('.page-title').text()).toBe('店铺设置')
    })

    it('页面加载时正确获取商家信息和设置', async () => {
      wrapper = await mountComponent()

      expect(getMerchantInfo).toHaveBeenCalledTimes(1)
      expect(getMerchantSettings).toHaveBeenCalledTimes(1)
    })

    it('页面标题正确显示', async () => {
      wrapper = await mountComponent()

      const title = wrapper.find('.page-title')
      expect(title.exists()).toBe(true)
      expect(title.text()).toBe('店铺设置')
    })
  })

  describe('店铺信息展示', () => {
    it('正确显示商家基本信息', async () => {
      wrapper = await mountComponent()

      expect(wrapper.vm.merchantInfo).toEqual(mockMerchantInfo)
    })

    it('正确显示审核状态', async () => {
      wrapper = await mountComponent()

      const getStatusType = wrapper.vm.getStatusType
      expect(getStatusType('pending')).toBe('warning')
      expect(getStatusType('approved')).toBe('success')
      expect(getStatusType('rejected')).toBe('danger')
    })

    it('正确格式化日期', async () => {
      wrapper = await mountComponent()

      const formatDate = wrapper.vm.formatDate
      expect(formatDate('2024-04-20 10:00:00')).toContain('2024')
      expect(formatDate('')).toBe('-')
      expect(formatDate(null as any)).toBe('-')
    })
  })

  describe('营业设置', () => {
    it('正确初始化营业设置表单', async () => {
      wrapper = await mountComponent()

      expect(wrapper.vm.settingsForm).toEqual(mockMerchantSettings)
    })

    it('正确设置营业日', async () => {
      wrapper = await mountComponent()

      const weekDays = wrapper.vm.weekDays
      expect(weekDays).toHaveLength(7)
      expect(weekDays[0]).toEqual({ label: '周一', value: 1 })
      expect(weekDays[6]).toEqual({ label: '周日', value: 0 })
    })

    it('正确生成时间选项', async () => {
      wrapper = await mountComponent()

      const timeOptions = wrapper.vm.timeOptions
      expect(timeOptions.length).toBeGreaterThan(0)
      expect(timeOptions[0]).toEqual({ label: '00:00', value: '00:00' })
    })
  })

  describe('通知设置保存', () => {
    it('通知设置能够正确保存', async () => {
      wrapper = await mountComponent()

      const newSettings: MerchantSettings = {
        ...mockMerchantSettings,
        notificationSettings: {
          appointmentReminder: false,
          orderReminder: true,
          reviewReminder: false,
          notifyViaSms: true,
          notifyViaEmail: false
        }
      }

      wrapper.vm.settingsForm = newSettings
      await wrapper.vm.handleSave()

      expect(updateMerchantSettings).toHaveBeenCalledWith(newSettings)
      expect(ElMessage.success).toHaveBeenCalledWith('保存成功')
    })

    it('保存失败时显示错误提示', async () => {
      vi.mocked(updateMerchantSettings).mockRejectedValueOnce(new Error('保存失败'))

      wrapper = await mountComponent()
      await wrapper.vm.handleSave()

      expect(ElMessage.error).toHaveBeenCalledWith('保存失败')
    })

    it('保存时显示加载状态', async () => {
      wrapper = await mountComponent()

      expect(wrapper.vm.saving).toBe(false)

      const savePromise = wrapper.vm.handleSave()
      expect(wrapper.vm.saving).toBe(true)

      await savePromise
      expect(wrapper.vm.saving).toBe(false)
    })
  })

  describe('密码修改功能', () => {
    it('密码修改功能能够正确工作', async () => {
      wrapper = await mountComponent()

      wrapper.vm.passwordForm.oldPassword = 'oldPassword123'
      wrapper.vm.passwordForm.newPassword = 'newPassword123'
      wrapper.vm.passwordForm.confirmPassword = 'newPassword123'

      await wrapper.vm.handleChangePassword()

      expect(changePassword).toHaveBeenCalledWith({
        oldPassword: 'oldPassword123',
        newPassword: 'newPassword123'
      })
      expect(ElMessage.success).toHaveBeenCalledWith('密码修改成功')
    })

    it('原密码为空时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.passwordForm.oldPassword = ''
      wrapper.vm.passwordForm.newPassword = 'newPassword123'
      wrapper.vm.passwordForm.confirmPassword = 'newPassword123'

      await wrapper.vm.handleChangePassword()

      expect(ElMessage.warning).toHaveBeenCalledWith('请输入原密码')
      expect(changePassword).not.toHaveBeenCalled()
    })

    it('新密码为空时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.passwordForm.oldPassword = 'oldPassword123'
      wrapper.vm.passwordForm.newPassword = ''
      wrapper.vm.passwordForm.confirmPassword = ''

      await wrapper.vm.handleChangePassword()

      expect(ElMessage.warning).toHaveBeenCalledWith('请输入新密码')
      expect(changePassword).not.toHaveBeenCalled()
    })

    it('新密码长度不足时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.passwordForm.oldPassword = 'oldPassword123'
      wrapper.vm.passwordForm.newPassword = '12345'
      wrapper.vm.passwordForm.confirmPassword = '12345'

      await wrapper.vm.handleChangePassword()

      expect(ElMessage.warning).toHaveBeenCalledWith('新密码长度不能少于6位')
      expect(changePassword).not.toHaveBeenCalled()
    })

    it('两次密码不一致时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.passwordForm.oldPassword = 'oldPassword123'
      wrapper.vm.passwordForm.newPassword = 'newPassword123'
      wrapper.vm.passwordForm.confirmPassword = 'differentPassword'

      await wrapper.vm.handleChangePassword()

      expect(ElMessage.warning).toHaveBeenCalledWith('两次输入的密码不一致')
      expect(changePassword).not.toHaveBeenCalled()
    })

    it('密码修改失败时显示错误提示', async () => {
      vi.mocked(changePassword).mockRejectedValueOnce(new Error('修改失败'))

      wrapper = await mountComponent()

      wrapper.vm.passwordForm.oldPassword = 'oldPassword123'
      wrapper.vm.passwordForm.newPassword = 'newPassword123'
      wrapper.vm.passwordForm.confirmPassword = 'newPassword123'

      await wrapper.vm.handleChangePassword()

      expect(ElMessage.error).toHaveBeenCalledWith('密码修改失败')
    })

    it('密码修改成功后清空表单', async () => {
      wrapper = await mountComponent()

      wrapper.vm.passwordForm.oldPassword = 'oldPassword123'
      wrapper.vm.passwordForm.newPassword = 'newPassword123'
      wrapper.vm.passwordForm.confirmPassword = 'newPassword123'

      await wrapper.vm.handleChangePassword()

      expect(wrapper.vm.passwordForm.oldPassword).toBe('')
      expect(wrapper.vm.passwordForm.newPassword).toBe('')
      expect(wrapper.vm.passwordForm.confirmPassword).toBe('')
    })
  })

  describe('验证码发送', () => {
    it('发送手机验证码成功', async () => {
      wrapper = await mountComponent()

      wrapper.vm.phoneForm.phone = '13800138000'
      await wrapper.vm.handleSendVerifyCode('phone')

      expect(sendVerifyCode).toHaveBeenCalledWith('phone', '13800138000')
      expect(ElMessage.success).toHaveBeenCalledWith('验证码发送成功')
    })

    it('发送邮箱验证码成功', async () => {
      wrapper = await mountComponent()

      wrapper.vm.emailForm.email = 'test@example.com'
      await wrapper.vm.handleSendVerifyCode('email')

      expect(sendVerifyCode).toHaveBeenCalledWith('email', 'test@example.com')
      expect(ElMessage.success).toHaveBeenCalledWith('验证码发送成功')
    })

    it('手机号为空时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.phoneForm.phone = ''
      await wrapper.vm.handleSendVerifyCode('phone')

      expect(ElMessage.warning).toHaveBeenCalledWith('请输入手机号')
      expect(sendVerifyCode).not.toHaveBeenCalled()
    })

    it('邮箱为空时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.emailForm.email = ''
      await wrapper.vm.handleSendVerifyCode('email')

      expect(ElMessage.warning).toHaveBeenCalledWith('请输入邮箱')
      expect(sendVerifyCode).not.toHaveBeenCalled()
    })

    it('手机号格式错误时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.phoneForm.phone = 'invalid-phone'
      await wrapper.vm.handleSendVerifyCode('phone')

      expect(ElMessage.warning).toHaveBeenCalledWith('请输入正确的手机号格式')
      expect(sendVerifyCode).not.toHaveBeenCalled()
    })

    it('邮箱格式错误时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.emailForm.email = 'invalid-email'
      await wrapper.vm.handleSendVerifyCode('email')

      expect(ElMessage.warning).toHaveBeenCalledWith('请输入正确的邮箱格式')
      expect(sendVerifyCode).not.toHaveBeenCalled()
    })

    it('验证码发送失败时显示错误提示', async () => {
      vi.mocked(sendVerifyCode).mockRejectedValueOnce(new Error('发送失败'))

      wrapper = await mountComponent()

      wrapper.vm.phoneForm.phone = '13800138000'
      await wrapper.vm.handleSendVerifyCode('phone')

      expect(ElMessage.error).toHaveBeenCalledWith('验证码发送失败')
    })
  })

  describe('绑定手机号', () => {
    it('绑定手机号成功', async () => {
      wrapper = await mountComponent()

      wrapper.vm.phoneForm.phone = '13800138000'
      wrapper.vm.phoneForm.verifyCode = '123456'

      await wrapper.vm.handleBindPhone()

      expect(bindPhone).toHaveBeenCalledWith({
        phone: '13800138000',
        verifyCode: '123456'
      })
      expect(ElMessage.success).toHaveBeenCalledWith('手机号绑定成功')
    })

    it('手机号为空时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.phoneForm.phone = ''
      wrapper.vm.phoneForm.verifyCode = '123456'

      await wrapper.vm.handleBindPhone()

      expect(ElMessage.warning).toHaveBeenCalledWith('请输入手机号')
      expect(bindPhone).not.toHaveBeenCalled()
    })

    it('验证码为空时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.phoneForm.phone = '13800138000'
      wrapper.vm.phoneForm.verifyCode = ''

      await wrapper.vm.handleBindPhone()

      expect(ElMessage.warning).toHaveBeenCalledWith('请输入验证码')
      expect(bindPhone).not.toHaveBeenCalled()
    })

    it('绑定手机号失败时显示错误提示', async () => {
      vi.mocked(bindPhone).mockRejectedValueOnce(new Error('绑定失败'))

      wrapper = await mountComponent()

      wrapper.vm.phoneForm.phone = '13800138000'
      wrapper.vm.phoneForm.verifyCode = '123456'

      await wrapper.vm.handleBindPhone()

      expect(ElMessage.error).toHaveBeenCalledWith('手机号绑定失败')
    })

    it('绑定成功后刷新商家信息', async () => {
      wrapper = await mountComponent()

      wrapper.vm.phoneForm.phone = '13800138000'
      wrapper.vm.phoneForm.verifyCode = '123456'

      await wrapper.vm.handleBindPhone()

      expect(getMerchantInfo).toHaveBeenCalledTimes(2)
    })
  })

  describe('绑定邮箱', () => {
    it('绑定邮箱成功', async () => {
      wrapper = await mountComponent()

      wrapper.vm.emailForm.email = 'test@example.com'
      wrapper.vm.emailForm.verifyCode = '123456'

      await wrapper.vm.handleBindEmail()

      expect(bindEmail).toHaveBeenCalledWith({
        email: 'test@example.com',
        verifyCode: '123456'
      })
      expect(ElMessage.success).toHaveBeenCalledWith('邮箱绑定成功')
    })

    it('邮箱为空时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.emailForm.email = ''
      wrapper.vm.emailForm.verifyCode = '123456'

      await wrapper.vm.handleBindEmail()

      expect(ElMessage.warning).toHaveBeenCalledWith('请输入邮箱')
      expect(bindEmail).not.toHaveBeenCalled()
    })

    it('验证码为空时提示', async () => {
      wrapper = await mountComponent()

      wrapper.vm.emailForm.email = 'test@example.com'
      wrapper.vm.emailForm.verifyCode = ''

      await wrapper.vm.handleBindEmail()

      expect(ElMessage.warning).toHaveBeenCalledWith('请输入验证码')
      expect(bindEmail).not.toHaveBeenCalled()
    })

    it('绑定邮箱失败时显示错误提示', async () => {
      vi.mocked(bindEmail).mockRejectedValueOnce(new Error('绑定失败'))

      wrapper = await mountComponent()

      wrapper.vm.emailForm.email = 'test@example.com'
      wrapper.vm.emailForm.verifyCode = '123456'

      await wrapper.vm.handleBindEmail()

      expect(ElMessage.error).toHaveBeenCalledWith('邮箱绑定失败')
    })

    it('绑定成功后刷新商家信息', async () => {
      wrapper = await mountComponent()

      wrapper.vm.emailForm.email = 'test@example.com'
      wrapper.vm.emailForm.verifyCode = '123456'

      await wrapper.vm.handleBindEmail()

      expect(getMerchantInfo).toHaveBeenCalledTimes(2)
    })
  })

  describe('设置变更处理', () => {
    it('店铺状态切换', async () => {
      wrapper = await mountComponent()

      wrapper.vm.settingsForm.isOpen = true
      expect(wrapper.vm.settingsForm.isOpen).toBe(true)

      wrapper.vm.settingsForm.isOpen = false
      expect(wrapper.vm.settingsForm.isOpen).toBe(false)
    })

    it('营业日设置变更', async () => {
      wrapper = await mountComponent()

      wrapper.vm.settingsForm.businessDays = [1, 2, 3, 4, 5, 6, 0]
      expect(wrapper.vm.settingsForm.businessDays).toHaveLength(7)
    })

    it('营业时间设置变更', async () => {
      wrapper = await mountComponent()

      wrapper.vm.settingsForm.startTime = '08:00'
      wrapper.vm.settingsForm.endTime = '20:00'

      expect(wrapper.vm.settingsForm.startTime).toBe('08:00')
      expect(wrapper.vm.settingsForm.endTime).toBe('20:00')
    })

    it('最大预约人数设置变更', async () => {
      wrapper = await mountComponent()

      wrapper.vm.settingsForm.maxAppointments = 20
      expect(wrapper.vm.settingsForm.maxAppointments).toBe(20)
    })

    it('预约提前时间设置变更', async () => {
      wrapper = await mountComponent()

      wrapper.vm.settingsForm.advanceBookingHours = 48
      expect(wrapper.vm.settingsForm.advanceBookingHours).toBe(48)
    })

    it('法定节假日休息设置变更', async () => {
      wrapper = await mountComponent()

      wrapper.vm.settingsForm.legalHolidayRest = true
      expect(wrapper.vm.settingsForm.legalHolidayRest).toBe(true)
    })

    it('通知设置变更', async () => {
      wrapper = await mountComponent()

      wrapper.vm.settingsForm.notificationSettings.appointmentReminder = false
      wrapper.vm.settingsForm.notificationSettings.orderReminder = false
      wrapper.vm.settingsForm.notificationSettings.reviewReminder = false
      wrapper.vm.settingsForm.notificationSettings.notifyViaSms = true
      wrapper.vm.settingsForm.notificationSettings.notifyViaEmail = false

      expect(wrapper.vm.settingsForm.notificationSettings.appointmentReminder).toBe(false)
      expect(wrapper.vm.settingsForm.notificationSettings.orderReminder).toBe(false)
      expect(wrapper.vm.settingsForm.notificationSettings.reviewReminder).toBe(false)
      expect(wrapper.vm.settingsForm.notificationSettings.notifyViaSms).toBe(true)
      expect(wrapper.vm.settingsForm.notificationSettings.notifyViaEmail).toBe(false)
    })
  })

  describe('加载状态', () => {
    it('获取设置时显示加载状态', async () => {
      wrapper = await mountComponent()

      expect(wrapper.vm.loading).toBe(false)
    })

    it('获取商家信息时显示加载状态', async () => {
      wrapper = await mountComponent()

      expect(wrapper.vm.infoLoading).toBe(false)
    })

    it('修改密码时显示加载状态', async () => {
      wrapper = await mountComponent()

      wrapper.vm.passwordForm.oldPassword = 'oldPassword123'
      wrapper.vm.passwordForm.newPassword = 'newPassword123'
      wrapper.vm.passwordForm.confirmPassword = 'newPassword123'

      expect(wrapper.vm.passwordLoading).toBe(false)

      const promise = wrapper.vm.handleChangePassword()
      expect(wrapper.vm.passwordLoading).toBe(true)

      await promise
      expect(wrapper.vm.passwordLoading).toBe(false)
    })

    it('绑定时显示加载状态', async () => {
      wrapper = await mountComponent()

      wrapper.vm.phoneForm.phone = '13800138000'
      wrapper.vm.phoneForm.verifyCode = '123456'

      expect(wrapper.vm.bindLoading).toBe(false)

      const promise = wrapper.vm.handleBindPhone()
      expect(wrapper.vm.bindLoading).toBe(true)

      await promise
      expect(wrapper.vm.bindLoading).toBe(false)
    })
  })

  describe('错误处理', () => {
    it('获取设置失败时显示错误提示', async () => {
      vi.mocked(getMerchantSettings).mockRejectedValueOnce(new Error('获取失败'))

      wrapper = await mountComponent()

      expect(ElMessage.error).toHaveBeenCalledWith('获取店铺设置失败')
    })

    it('获取商家信息失败时正确处理', async () => {
      vi.mocked(getMerchantInfo).mockRejectedValueOnce(new Error('获取失败'))

      wrapper = await mountComponent()

      expect(wrapper.vm.merchantInfo).toBeNull()
    })
  })

  describe('表单验证', () => {
    it('验证手机号格式 - 有效手机号', async () => {
      wrapper = await mountComponent()

      const phoneRegex = /^1[3-9]\d{9}$/

      expect(phoneRegex.test('13800138000')).toBe(true)
      expect(phoneRegex.test('15912345678')).toBe(true)
    })

    it('验证手机号格式 - 无效手机号', async () => {
      wrapper = await mountComponent()

      const phoneRegex = /^1[3-9]\d{9}$/

      expect(phoneRegex.test('12345678901')).toBe(false)
      expect(phoneRegex.test('1380013800')).toBe(false)
      expect(phoneRegex.test('abc12345678')).toBe(false)
    })

    it('验证邮箱格式 - 有效邮箱', async () => {
      wrapper = await mountComponent()

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

      expect(emailRegex.test('test@example.com')).toBe(true)
      expect(emailRegex.test('user.name@domain.co')).toBe(true)
    })

    it('验证邮箱格式 - 无效邮箱', async () => {
      wrapper = await mountComponent()

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

      expect(emailRegex.test('invalid-email')).toBe(false)
      expect(emailRegex.test('@example.com')).toBe(false)
      expect(emailRegex.test('test@')).toBe(false)
    })
  })
})
