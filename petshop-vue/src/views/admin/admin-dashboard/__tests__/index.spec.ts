import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import AdminDashboard from '../index.vue'
import AdminCard from '@/components/AdminCard.vue'
import { ElCard, ElButton, ElTable, ElTableColumn } from 'element-plus'

// 模拟 console.log
vi.spyOn(console, 'log').mockImplementation(() => {})

describe('AdminDashboard', () => {
  let wrapper: any

  beforeEach(() => {
    wrapper = mount(AdminDashboard, {
      global: {
        components: {
          AdminCard,
          ElCard,
          ElButton,
          ElTable,
          ElTableColumn
        }
      }
    })
  })

  it('renders welcome section with current date', () => {
    expect(wrapper.find('.welcome-title').text()).toBe('欢迎回来，管理员！')
    expect(wrapper.find('.welcome-text').text()).toContain('今天是')
  })

  it('renders stats cards correctly', () => {
    const statCards = wrapper.findAllComponents(AdminCard)
    expect(statCards.length).toBe(4)

    // 检查第一个统计卡片
    expect(statCards[0].props('title')).toBe('总用户数')
    expect(statCards[0].props('value')).toBe('2,580')
    expect(statCards[0].props('subtitle')).toBe('位注册用户')
    expect(statCards[0].props('icon')).toBe('fa fa-users')
    expect(statCards[0].props('iconColor')).toBe('primary')
  })

  it('renders recent users table correctly', () => {
    const usersTable = wrapper.find('.users-table')
    expect(usersTable.exists()).toBe(true)
  })

  it('renders pending merchants table correctly', () => {
    const merchantsTable = wrapper.find('.merchants-table')
    expect(merchantsTable.exists()).toBe(true)
  })

  it('renders announcements list correctly', () => {
    const announcements = wrapper.findAll('.announcement-item')
    expect(announcements.length).toBe(3)

    // 检查第一个公告
    const firstAnnouncement = announcements[0]
    expect(firstAnnouncement.find('.announcement-title').text()).toBe('平台服务升级通知')
    expect(firstAnnouncement.find('.announcement-time').text()).toBe('2026-04-18 08:00')
    expect(firstAnnouncement.find('.announcement-content').text()).toBe('平台将于本周日凌晨2:00-6:00进行系统升级，届时部分功能将暂停使用，请提前做好准备。')
    expect(firstAnnouncement.find('.announcement-publisher').text()).toBe('发布人：系统管理员')
  })

  it('renders quick actions correctly', () => {
    const quickActions = wrapper.findAll('.quick-action-item')
    expect(quickActions.length).toBe(4)

    // 检查第一个快捷操作
    const firstAction = quickActions[0]
    expect(firstAction.find('.action-title').text()).toBe('用户管理')
    expect(firstAction.find('.action-desc').text()).toBe('管理注册用户')
  })

  it('triggers handleAnnouncementClick when announcement is clicked', async () => {
    const announcementItem = wrapper.find('.announcement-item')
    await announcementItem.trigger('click')
    expect(console.log).toHaveBeenCalledWith('查看公告:', expect.any(Object))
  })

  it('triggers handleQuickAction when quick action is clicked', async () => {
    const quickActionItem = wrapper.find('.quick-action-item')
    await quickActionItem.trigger('click')
    expect(console.log).toHaveBeenCalledWith('快捷操作:', expect.any(Object))
  })

  it('updates currentDate when component is mounted', () => {
    expect(wrapper.vm.currentDate).toBe(new Date().toLocaleDateString('zh-CN'))
  })

  // 测试方法调用
  it('calls handleUserClick method', () => {
    const mockUser = { id: 1, username: '测试用户' }
    wrapper.vm.handleUserClick(mockUser)
    expect(console.log).toHaveBeenCalledWith('查看用户:', mockUser)
  })

  it('calls handleMerchantClick method', () => {
    const mockMerchant = { id: 1, name: '测试商家' }
    wrapper.vm.handleMerchantClick(mockMerchant)
    expect(console.log).toHaveBeenCalledWith('审核商家:', mockMerchant)
  })

  it('calls handleAnnouncementClick method', () => {
    const mockAnnouncement = { id: 1, title: '测试公告' }
    wrapper.vm.handleAnnouncementClick(mockAnnouncement)
    expect(console.log).toHaveBeenCalledWith('查看公告:', mockAnnouncement)
  })

  it('calls handleQuickAction method', () => {
    const mockAction = { title: '测试操作', path: '/test' }
    wrapper.vm.handleQuickAction(mockAction)
    expect(console.log).toHaveBeenCalledWith('快捷操作:', mockAction)
  })
})
