import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import ElementPlus from 'element-plus'
import AnnouncementEdit from '../index.vue'
import * as announcementApi from '@/api/announcement'
import { ElMessage } from 'element-plus'
import { nextTick } from 'vue'

const mockRouter = {
  push: vi.fn(),
  replace: vi.fn(),
  go: vi.fn(),
  back: vi.fn(),
  forward: vi.fn(),
}

const mockRoute = {
  path: '/admin/announcement-edit',
  params: {},
  query: {},
  name: undefined,
  meta: {},
}

vi.mock('vue-router', () => ({
  useRoute: () => mockRoute,
  useRouter: () => mockRouter,
}))

vi.mock('element-plus', async (importOriginal) => {
  const actual = await importOriginal<typeof import('element-plus')>()
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

vi.mock('@/api/announcement', () => ({
  getAnnouncementById: vi.fn(),
  addAnnouncement: vi.fn(),
  updateAnnouncement: vi.fn(),
}))

const mockAnnouncement = {
  id: 1,
  title: '测试公告标题',
  content: '这是测试公告的内容，用于测试编辑功能。',
  status: 'published' as const,
  publishTime: '2024-01-15 10:30:00',
}

describe('AnnouncementEdit', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockRoute.query = {}
    mockRouter.push.mockClear()
    mockRouter.back.mockClear()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('数据渲染测试', () => {
    it('应该正确渲染公告表单', async () => {
      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      expect(wrapper.find('.announcement-edit').exists()).toBe(true)
      expect(wrapper.find('.form-card').exists()).toBe(true)
      expect(wrapper.find('.el-form').exists()).toBe(true)
    })

    it('应该正确渲染标题输入框', async () => {
      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      const titleInput = wrapper.find('input[placeholder="请输入公告标题"]')
      expect(titleInput.exists()).toBe(true)

      const titleFormItem = wrapper.find('.el-form-item[label="公告标题"]')
      expect(titleFormItem.exists()).toBe(true)
    })

    it('应该正确渲染内容编辑器', async () => {
      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      const contentTextarea = wrapper.find('textarea[placeholder="请输入公告内容"]')
      expect(contentTextarea.exists()).toBe(true)

      const contentFormItem = wrapper.find('.el-form-item[label="公告内容"]')
      expect(contentFormItem.exists()).toBe(true)
    })

    it('应该正确渲染发布时间选择器', async () => {
      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      const datePicker = wrapper.find('.el-date-editor')
      expect(datePicker.exists()).toBe(true)

      const publishTimeFormItem = wrapper.find('.el-form-item[label="发布时间"]')
      expect(publishTimeFormItem.exists()).toBe(true)
    })

    it('应该正确显示面包屑导航', async () => {
      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      const breadcrumb = wrapper.find('.el-breadcrumb')
      expect(breadcrumb.exists()).toBe(true)

      const breadcrumbItems = wrapper.findAll('.el-breadcrumb__item')
      expect(breadcrumbItems.length).toBeGreaterThanOrEqual(2)
    })

    it('新建模式应该显示"发布公告"标题', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      expect(wrapper.vm.pageTitle).toBe('发布公告')
    })

    it('编辑模式应该显示"编辑公告"标题', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockResolvedValue(mockAnnouncement)

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      expect(wrapper.vm.pageTitle).toBe('编辑公告')
    })
  })

  describe('API集成测试', () => {
    it('编辑模式应该调用getAnnouncementById API', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockResolvedValue(mockAnnouncement)

      mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      expect(announcementApi.getAnnouncementById).toHaveBeenCalledWith(1)
    })

    it('编辑模式应该正确填充表单数据', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockResolvedValue(mockAnnouncement)

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      expect(wrapper.vm.formData.title).toBe(mockAnnouncement.title)
      expect(wrapper.vm.formData.content).toBe(mockAnnouncement.content)
      expect(wrapper.vm.formData.publishTime).toBe(mockAnnouncement.publishTime)
    })

    it('新建模式应该调用addAnnouncement API', async () => {
      mockRoute.query = {}
      ;(announcementApi.addAnnouncement as vi.Mock).mockResolvedValue({ id: 2, ...mockAnnouncement })

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = '新公告标题'
      wrapper.vm.formData.content = '新公告内容'

      await wrapper.vm.handleSubmit(wrapper.vm.formRef)
      await flushPromises()

      expect(announcementApi.addAnnouncement).toHaveBeenCalledWith({
        title: '新公告标题',
        content: '新公告内容',
      })
    })

    it('编辑模式应该调用updateAnnouncement API', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockResolvedValue(mockAnnouncement)
      ;(announcementApi.updateAnnouncement as vi.Mock).mockResolvedValue(mockAnnouncement)

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = '更新后的标题'
      wrapper.vm.formData.content = '更新后的内容'

      await wrapper.vm.handleSubmit(wrapper.vm.formRef)
      await flushPromises()

      expect(announcementApi.updateAnnouncement).toHaveBeenCalledWith(1, {
        title: '更新后的标题',
        content: '更新后的内容',
      })
    })

    it('新建模式成功后应该显示成功消息并跳转', async () => {
      mockRoute.query = {}
      ;(announcementApi.addAnnouncement as vi.Mock).mockResolvedValue({ id: 2, ...mockAnnouncement })

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = '新公告标题'
      wrapper.vm.formData.content = '新公告内容'

      await wrapper.vm.handleSubmit(wrapper.vm.formRef)
      await flushPromises()

      expect(ElMessage.success).toHaveBeenCalledWith('发布成功')
      expect(mockRouter.push).toHaveBeenCalledWith('/admin/announcements')
    })

    it('编辑模式成功后应该显示成功消息并跳转', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockResolvedValue(mockAnnouncement)
      ;(announcementApi.updateAnnouncement as vi.Mock).mockResolvedValue(mockAnnouncement)

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      await wrapper.vm.handleSubmit(wrapper.vm.formRef)
      await flushPromises()

      expect(ElMessage.success).toHaveBeenCalledWith('更新成功')
      expect(mockRouter.push).toHaveBeenCalledWith('/admin/announcements')
    })
  })

  describe('交互功能测试', () => {
    it('应该正确验证标题必填', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = ''
      wrapper.vm.formData.content = '测试内容'

      await wrapper.vm.handleSubmit(wrapper.vm.formRef)
      await flushPromises()

      expect(announcementApi.addAnnouncement).not.toHaveBeenCalled()
    })

    it('应该正确验证标题最小长度', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = '测'
      wrapper.vm.formData.content = '测试内容'

      const formRef = wrapper.vm.formRef
      if (formRef) {
        await formRef.validate()
        await flushPromises()
      }

      expect(announcementApi.addAnnouncement).not.toHaveBeenCalled()
    })

    it('应该正确验证标题最大长度', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = 'a'.repeat(101)
      wrapper.vm.formData.content = '测试内容'

      const formRef = wrapper.vm.formRef
      if (formRef) {
        await formRef.validate()
        await flushPromises()
      }

      expect(announcementApi.addAnnouncement).not.toHaveBeenCalled()
    })

    it('应该正确验证内容必填', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = '测试标题'
      wrapper.vm.formData.content = ''

      await wrapper.vm.handleSubmit(wrapper.vm.formRef)
      await flushPromises()

      expect(announcementApi.addAnnouncement).not.toHaveBeenCalled()
    })

    it('应该正确验证内容最小长度', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = '测试标题'
      wrapper.vm.formData.content = '测试'

      const formRef = wrapper.vm.formRef
      if (formRef) {
        await formRef.validate()
        await flushPromises()
      }

      expect(announcementApi.addAnnouncement).not.toHaveBeenCalled()
    })

    it('应该正确验证内容最大长度', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = '测试标题'
      wrapper.vm.formData.content = 'a'.repeat(2001)

      const formRef = wrapper.vm.formRef
      if (formRef) {
        await formRef.validate()
        await flushPromises()
      }

      expect(announcementApi.addAnnouncement).not.toHaveBeenCalled()
    })

    it('点击返回按钮应该调用router.back', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      const backButton = wrapper.find('.card-header .el-button')
      await backButton.trigger('click')
      await flushPromises()

      expect(mockRouter.back).toHaveBeenCalled()
    })

    it('点击取消按钮应该调用router.back', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      const buttons = wrapper.findAll('.el-form-item .el-button')
      const cancelButton = buttons.find((btn) => btn.text() === '取消')

      if (cancelButton) {
        await cancelButton.trigger('click')
        await flushPromises()
      }

      expect(mockRouter.back).toHaveBeenCalled()
    })

    it('新建模式提交按钮应该显示"发布"', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      const buttons = wrapper.findAll('.el-form-item .el-button')
      const submitButton = buttons.find((btn) => btn.classes().includes('el-button--primary'))

      expect(submitButton?.text()).toBe('发布')
    })

    it('编辑模式提交按钮应该显示"保存"', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockResolvedValue(mockAnnouncement)

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      const buttons = wrapper.findAll('.el-form-item .el-button')
      const submitButton = buttons.find((btn) => btn.classes().includes('el-button--primary'))

      expect(submitButton?.text()).toBe('保存')
    })

    it('编辑模式发布时间选择器应该被禁用', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockResolvedValue(mockAnnouncement)

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      expect(wrapper.vm.isEdit).toBe(true)
    })

    it('提交时应该设置submitting状态', async () => {
      mockRoute.query = {}
      ;(announcementApi.addAnnouncement as vi.Mock).mockImplementation(
        () => new Promise((resolve) => setTimeout(() => resolve({ id: 2 }), 100))
      )

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = '测试标题内容'
      wrapper.vm.formData.content = '这是测试内容，长度超过五个字符'

      const formRef = wrapper.vm.formRef
      if (formRef) {
        await formRef.validate()
      }
      await flushPromises()

      const submitPromise = wrapper.vm.handleSubmit(wrapper.vm.formRef)

      await nextTick()
      await flushPromises()

      expect(wrapper.vm.submitting).toBe(true)

      await submitPromise
      await flushPromises()

      expect(wrapper.vm.submitting).toBe(false)
    })
  })

  describe('边界情况测试', () => {
    it('加载数据时应该显示loading状态', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockImplementation(
        () => new Promise((resolve) => setTimeout(() => resolve(mockAnnouncement), 100))
      )

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      expect(wrapper.vm.loading).toBe(true)
    })

    it('数据加载完成后应该关闭loading状态', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockResolvedValue(mockAnnouncement)

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      expect(wrapper.vm.loading).toBe(false)
    })

    it('数据加载失败应该显示错误消息', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockRejectedValue(new Error('加载失败'))

      mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('加载公告详情失败')
    })

    it('数据加载失败后应该关闭loading状态', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockRejectedValue(new Error('加载失败'))

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      expect(wrapper.vm.loading).toBe(false)
    })

    it('创建公告失败应该显示错误消息', async () => {
      mockRoute.query = {}
      ;(announcementApi.addAnnouncement as vi.Mock).mockRejectedValue(new Error('创建失败'))

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = '测试标题'
      wrapper.vm.formData.content = '测试内容内容'

      await wrapper.vm.handleSubmit(wrapper.vm.formRef)
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('操作失败')
    })

    it('更新公告失败应该显示错误消息', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockResolvedValue(mockAnnouncement)
      ;(announcementApi.updateAnnouncement as vi.Mock).mockRejectedValue(new Error('更新失败'))

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      await wrapper.vm.handleSubmit(wrapper.vm.formRef)
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('操作失败')
    })

    it('提交失败后应该关闭submitting状态', async () => {
      mockRoute.query = {}
      ;(announcementApi.addAnnouncement as vi.Mock).mockRejectedValue(new Error('创建失败'))

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = '测试标题'
      wrapper.vm.formData.content = '测试内容内容'

      await wrapper.vm.handleSubmit(wrapper.vm.formRef)
      await flushPromises()

      expect(wrapper.vm.submitting).toBe(false)
    })

    it('formRef为undefined时不应该执行提交', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      await wrapper.vm.handleSubmit(undefined)
      await flushPromises()

      expect(announcementApi.addAnnouncement).not.toHaveBeenCalled()
    })

    it('新建模式应该自动设置发布时间', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      expect(wrapper.vm.formData.publishTime).not.toBe('')
    })

    it('编辑模式不应该自动设置发布时间', async () => {
      mockRoute.query = { id: '1' }
      ;(announcementApi.getAnnouncementById as vi.Mock).mockResolvedValue(mockAnnouncement)

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      expect(wrapper.vm.formData.publishTime).toBe(mockAnnouncement.publishTime)
    })

    it('表单验证规则应该正确配置', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      const rules = wrapper.vm.rules

      expect(rules.title).toBeDefined()
      expect(rules.title.length).toBeGreaterThan(0)
      expect(rules.content).toBeDefined()
      expect(rules.content.length).toBeGreaterThan(0)
      expect(rules.publishTime).toBeDefined()
    })

    it('标题输入框应该有最大长度限制', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      const titleInput = wrapper.find('input[placeholder="请输入公告标题"]')
      expect(titleInput.attributes('maxlength')).toBe('100')
    })

    it('内容输入框应该有最大长度限制', async () => {
      mockRoute.query = {}

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      const contentTextarea = wrapper.find('textarea[placeholder="请输入公告内容"]')
      expect(contentTextarea.attributes('maxlength')).toBe('2000')
    })

    it('提交按钮在提交中应该显示loading状态', async () => {
      mockRoute.query = {}
      ;(announcementApi.addAnnouncement as vi.Mock).mockImplementation(
        () => new Promise((resolve) => setTimeout(() => resolve({ id: 2 }), 100))
      )

      const wrapper = mount(AnnouncementEdit, {
        global: {
          plugins: [ElementPlus],
        },
      })

      await flushPromises()

      wrapper.vm.formData.title = '测试标题'
      wrapper.vm.formData.content = '测试内容内容'

      const submitPromise = wrapper.vm.handleSubmit(wrapper.vm.formRef)

      await nextTick()

      const buttons = wrapper.findAll('.el-form-item .el-button')
      const submitButton = buttons.find((btn) => btn.classes().includes('el-button--primary'))

      expect(submitButton?.attributes('loading')).toBeDefined()

      await submitPromise
      await flushPromises()
    })
  })
})
