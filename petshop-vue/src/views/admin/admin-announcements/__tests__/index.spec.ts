import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import ElementPlus from 'element-plus'
import AdminAnnouncements from '../index.vue'
import * as announcementApi from '@/api/announcement'
import { Announcement } from '@/api/announcement'
import { ElMessage, ElMessageBox } from 'element-plus'

// 模拟 Element Plus 组件
vi.mock('element-plus', async (importOriginal) => {
  const actual = await importOriginal<typeof import('element-plus')>();
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      error: vi.fn(),
      warning: vi.fn(),
      info: vi.fn()
    },
    ElMessageBox: {
      confirm: vi.fn()
    }
  };
});

// 模拟 API 调用
vi.mock('@/api/announcement', () => ({
  getAnnouncements: vi.fn(),
  addAnnouncement: vi.fn(),
  updateAnnouncement: vi.fn(),
  deleteAnnouncement: vi.fn(),
  publishAnnouncement: vi.fn(),
  unpublishAnnouncement: vi.fn()
}))

const mockAnnouncements: Announcement[] = [
  {
    id: 1,
    title: '测试公告1',
    content: '这是测试公告1的内容',
    status: 'published',
    publishTime: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    title: '测试公告2',
    content: '这是测试公告2的内容',
    status: 'draft',
    publishTime: ''
  },
  {
    id: 3,
    title: '测试公告3',
    content: '这是测试公告3的内容',
    status: 'published',
    publishTime: '2024-01-02 10:00:00'
  }
]

describe('AdminAnnouncements', () => {
  beforeEach(() => {
    // 重置所有模拟
    vi.clearAllMocks()
    
    // 模拟 getAnnouncements 返回数据
    ;(announcementApi.getAnnouncements as vi.Mock).mockResolvedValue(mockAnnouncements)
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('should render announcement list correctly', async () => {
    const wrapper = mount(AdminAnnouncements, {
      global: {
        plugins: [ElementPlus]
      }
    })

    await flushPromises()

    // 检查表格是否渲染
    const table = wrapper.find('.el-table')
    expect(table.exists()).toBe(true)

    // 检查表格行数（不包括表头）
    const tableRows = wrapper.findAll('.el-table__row')
    expect(tableRows.length).toBe(3)

    // 检查搜索和筛选表单
    const searchInput = wrapper.find('input[placeholder="请输入公告标题"]')
    expect(searchInput.exists()).toBe(true)

    const statusSelect = wrapper.find('.el-select')
    expect(statusSelect.exists()).toBe(true)

    // 检查批量操作按钮
    const batchPublishBtn = wrapper.find('.el-button--success')
    expect(batchPublishBtn.exists()).toBe(true)
    expect(batchPublishBtn.text()).toContain('批量发布')

    const batchUnpublishBtn = wrapper.find('.el-button--warning')
    expect(batchUnpublishBtn.exists()).toBe(true)
    expect(batchUnpublishBtn.text()).toContain('批量下架')
  })

  it('should mock API responses correctly', async () => {
    const wrapper = mount(AdminAnnouncements, {
      global: {
        plugins: [ElementPlus]
      }
    })

    await flushPromises()

    // 验证 getAnnouncements 被调用
    expect(announcementApi.getAnnouncements).toHaveBeenCalled()

    // 模拟 deleteAnnouncement 返回成功
    ;(announcementApi.deleteAnnouncement as vi.Mock).mockResolvedValue(undefined)
    ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(undefined)

    // 点击删除按钮
    const deleteBtn = wrapper.find('.el-button--danger')
    await deleteBtn.trigger('click')
    await flushPromises()

    // 验证 deleteAnnouncement 被调用
    expect(announcementApi.deleteAnnouncement).toHaveBeenCalledWith(1)
  })

  it('should test search functionality', async () => {
    const wrapper = mount(AdminAnnouncements, {
      global: {
        plugins: [ElementPlus]
      }
    })

    await flushPromises()

    // 输入搜索关键词
    const searchInput = wrapper.find('input[placeholder="请输入公告标题"]')
    await searchInput.setValue('测试公告1')

    // 点击搜索按钮
    const searchBtn = wrapper.find('.el-button--primary')
    await searchBtn.trigger('click')

    // 检查是否只显示匹配的公告
    const tableRows = wrapper.findAll('.el-table__row')
    expect(tableRows.length).toBe(1)
  })

  it('should test filter functionality', async () => {
    const wrapper = mount(AdminAnnouncements, {
      global: {
        plugins: [ElementPlus]
      }
    })

    await flushPromises()

    // 直接设置 statusFilter 的值
    wrapper.vm.statusFilter = 'published'
    
    // 点击搜索按钮
    const searchBtn = wrapper.find('.el-button--primary')
    await searchBtn.trigger('click')

    // 检查是否只显示已发布的公告
    const tableRows = wrapper.findAll('.el-table__row')
    expect(tableRows.length).toBe(2)
  })

  it('should test pagination functionality', async () => {
    // 创建更多模拟数据以测试分页
    const moreAnnouncements = Array.from({ length: 15 }, (_, i) => ({
      id: i + 1,
      title: `测试公告${i + 1}`,
      content: `这是测试公告${i + 1}的内容`,
      status: i % 2 === 0 ? 'published' : 'draft',
      publishTime: i % 2 === 0 ? '2024-01-01 10:00:00' : ''
    }))

    ;(announcementApi.getAnnouncements as vi.Mock).mockResolvedValue(moreAnnouncements)

    const wrapper = mount(AdminAnnouncements, {
      global: {
        plugins: [ElementPlus]
      }
    })

    await flushPromises()

    // 检查第一页显示10条数据
    let tableRows = wrapper.findAll('.el-table__row')
    expect(tableRows.length).toBe(10)

    // 直接设置当前页码
    wrapper.vm.pagination.current = 2
    await wrapper.vm.$nextTick()

    // 检查第二页显示剩余5条数据
    tableRows = wrapper.findAll('.el-table__row')
    expect(tableRows.length).toBe(5)
  })

  it('should test batch operations', async () => {
    const wrapper = mount(AdminAnnouncements, {
      global: {
        plugins: [ElementPlus]
      }
    })

    await flushPromises()

    // 模拟 confirm 对话框
    ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(undefined)
    ;(announcementApi.publishAnnouncement as vi.Mock).mockResolvedValue(undefined)
    ;(announcementApi.unpublishAnnouncement as vi.Mock).mockResolvedValue(undefined)
    ;(announcementApi.deleteAnnouncement as vi.Mock).mockResolvedValue(undefined)

    // 直接设置 selectedRows
    wrapper.vm.selectedRows = [mockAnnouncements[1]] // 选择草稿状态的公告
    await wrapper.vm.$nextTick()

    // 测试批量发布
    const batchPublishBtn = wrapper.find('.el-button--success')
    await batchPublishBtn.trigger('click')
    await flushPromises()

    // 验证 publishAnnouncement 被调用
    expect(announcementApi.publishAnnouncement).toHaveBeenCalled()

    // 直接设置 selectedRows
    wrapper.vm.selectedRows = [mockAnnouncements[0]] // 选择已发布状态的公告
    await wrapper.vm.$nextTick()

    // 测试批量下架
    const batchUnpublishBtn = wrapper.find('.el-button--warning')
    await batchUnpublishBtn.trigger('click')
    await flushPromises()

    // 验证 unpublishAnnouncement 被调用
    expect(announcementApi.unpublishAnnouncement).toHaveBeenCalled()
  })

  it('should test single announcement operations', async () => {
    const wrapper = mount(AdminAnnouncements, {
      global: {
        plugins: [ElementPlus]
      }
    })

    await flushPromises()

    // 模拟 confirm 对话框
    ;(ElMessageBox.confirm as vi.Mock).mockResolvedValue(undefined)
    ;(announcementApi.deleteAnnouncement as vi.Mock).mockResolvedValue(undefined)
    ;(announcementApi.publishAnnouncement as vi.Mock).mockResolvedValue(undefined)
    ;(announcementApi.unpublishAnnouncement as vi.Mock).mockResolvedValue(undefined)

    // 测试删除按钮
    const deleteBtn = wrapper.find('.el-button--danger')
    await deleteBtn.trigger('click')
    await flushPromises()

    // 验证 deleteAnnouncement 被调用
    expect(announcementApi.deleteAnnouncement).toHaveBeenCalled()

    // 直接调用 handlePublish 方法来测试发布功能
    await wrapper.vm.handlePublish(mockAnnouncements[1])
    await flushPromises()

    // 验证 publishAnnouncement 被调用
    expect(announcementApi.publishAnnouncement).toHaveBeenCalledWith(2)
  })

  it('should test loading state', async () => {
    // 模拟加载延迟
    const mockPromise = new Promise(resolve => {
      setTimeout(() => resolve(mockAnnouncements), 100)
    })
    ;(announcementApi.getAnnouncements as vi.Mock).mockReturnValue(mockPromise)

    const wrapper = mount(AdminAnnouncements, {
      global: {
        plugins: [ElementPlus]
      }
    })

    // 检查 loading 变量是否为 true
    expect(wrapper.vm.loading).toBe(true)

    // 等待 promise 完成
    await mockPromise
    await wrapper.vm.$nextTick()

    // 检查 loading 变量是否为 false
    expect(wrapper.vm.loading).toBe(false)
  })

  it('should test error state', async () => {
    // 模拟 API 错误
    ;(announcementApi.getAnnouncements as vi.Mock).mockRejectedValue(new Error('Failed to load'))

    const wrapper = mount(AdminAnnouncements, {
      global: {
        plugins: [ElementPlus]
      }
    })

    await flushPromises()

    // 验证错误消息
    expect(ElMessage.error).toHaveBeenCalledWith('加载公告列表失败')
  })

  it('should test empty state', async () => {
    // 模拟空数据
    ;(announcementApi.getAnnouncements as vi.Mock).mockResolvedValue([])

    const wrapper = mount(AdminAnnouncements, {
      global: {
        plugins: [ElementPlus]
      }
    })

    await flushPromises()

    // 检查是否显示空状态
    const emptyText = wrapper.find('.el-table__empty-text')
    expect(emptyText.exists()).toBe(true)
  })
})
