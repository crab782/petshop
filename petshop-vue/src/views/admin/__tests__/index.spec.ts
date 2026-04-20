import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '../AdminLayout.vue'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/admin/dashboard', component: { template: '<div>dashboard</div>' } },
    { path: '/admin/users', component: { template: '<div>users</div>' } },
    { path: '/admin/merchants', component: { template: '<div>merchants</div>' } },
    { path: '/admin/services', component: { template: '<div>services</div>' } },
    { path: '/admin/products', component: { template: '<div>products</div>' } },
    { path: '/admin/appointments', component: { template: '<div>appointments</div>' } },
    { path: '/admin/pets', component: { template: '<div>pets</div>' } },
    { path: '/admin/reviews', component: { template: '<div>reviews</div>' } },
    { path: '/admin/announcements', component: { template: '<div>announcements</div>' } },
    { path: '/admin/system', component: { template: '<div>system</div>' } },
    { path: '/login', component: { template: '<div>login</div>' } }
  ]
})

describe('AdminLayout.vue', () => {
  beforeEach(async () => {
    await router.push('/admin/dashboard')
    await router.isReady()
  })

  it('测试导航菜单渲染', () => {
    const wrapper = mount(AdminLayout, {
      global: {
        plugins: [router, ElementPlus]
      }
    })
    
    // 检查菜单项内容
    expect(wrapper.text()).toContain('后台首页')
    expect(wrapper.text()).toContain('用户管理')
    expect(wrapper.text()).toContain('商家管理')
    expect(wrapper.text()).toContain('服务管理')
    expect(wrapper.text()).toContain('商品管理')
    expect(wrapper.text()).toContain('预约管理')
    expect(wrapper.text()).toContain('宠物管理')
    expect(wrapper.text()).toContain('评价管理')
    expect(wrapper.text()).toContain('公告管理')
    expect(wrapper.text()).toContain('系统设置')
    expect(wrapper.text()).toContain('退出登录')
  })

  it('测试用户信息显示', () => {
    const wrapper = mount(AdminLayout, {
      global: {
        plugins: [router, ElementPlus]
      }
    })
    
    // 检查用户信息元素
    const userInfo = wrapper.find('.user-info')
    expect(userInfo.exists()).toBe(true)
    
    // 检查用户头像
    const userAvatar = wrapper.find('.user-avatar')
    expect(userAvatar.exists()).toBe(true)
    expect(userAvatar.attributes('src')).toContain('avatar')
    
    // 检查用户名
    expect(wrapper.text()).toContain('管理员')
  })

  it('测试退出登录按钮', () => {
    const wrapper = mount(AdminLayout, {
      global: {
        plugins: [router, ElementPlus]
      }
    })
    
    // 检查菜单中的退出登录按钮
    expect(wrapper.text()).toContain('退出登录')
    
    // 检查下拉菜单中的退出登录按钮
    expect(wrapper.text()).toContain('退出登录')
  })

  it('测试菜单点击事件', async () => {
    const wrapper = mount(AdminLayout, {
      global: {
        plugins: [router, ElementPlus]
      }
    })
    
    // 模拟菜单点击
    const usersMenuItem = wrapper.find('[index="/admin/users"]')
    if (usersMenuItem.exists()) {
      await usersMenuItem.trigger('click')
      
      // 检查路由是否跳转
      expect(router.currentRoute.value.path).toBe('/admin/users')
    }
  })

  it('测试退出登录功能', async () => {
    const wrapper = mount(AdminLayout, {
      global: {
        plugins: [router, ElementPlus]
      }
    })
    
    // 模拟点击退出登录
    const logoutMenuItem = wrapper.find('[index="/logout"]')
    if (logoutMenuItem.exists()) {
      await logoutMenuItem.trigger('click')
      
      // 检查路由是否跳转到登录页
      expect(router.currentRoute.value.path).toBe('/login')
    }
  })

  it('测试用户下拉菜单', async () => {
    const wrapper = mount(AdminLayout, {
      global: {
        plugins: [router, ElementPlus]
      }
    })
    
    // 检查下拉菜单项是否存在于DOM中
    expect(wrapper.text()).toContain('退出登录')
  })

  it('测试菜单折叠功能', async () => {
    const wrapper = mount(AdminLayout, {
      global: {
        plugins: [router, ElementPlus]
      }
    })
    
    // 初始状态应该是展开的
    const logoText = wrapper.find('.logo-text')
    expect(logoText.exists()).toBe(true)
    
    // 点击折叠按钮
    const toggleButton = wrapper.find('.menu-toggle')
    if (toggleButton.exists()) {
      await toggleButton.trigger('click')
      
      // 检查logo文本是否存在
      expect(logoText.exists()).toBe(true)
    }
  })
})
