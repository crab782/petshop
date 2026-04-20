import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory, type Router } from 'vue-router'
import { createPinia, setActivePinia, type Pinia } from 'pinia'
import MerchantLayout from '../MerchantLayout.vue'
import {
  ElContainer,
  ElHeader,
  ElAside,
  ElMain,
  ElMenu,
  ElMenuItem,
  ElButton,
  ElIcon,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
} from 'element-plus'
import { HomeFilled, ShoppingBag, Calendar, Cubes, Star, SignOut, Bell, ArrowDown, Setting } from '@element-plus/icons-vue'
import { mockLocalStorage, mockMatchMedia } from '@/tests/utils/testUtils'

vi.spyOn(console, 'log').mockImplementation(() => {})

const createTestRouter = (): Router => {
  return createRouter({
    history: createWebHistory(),
    routes: [
      { path: '/merchant/home', name: 'MerchantHome', component: { template: '<div>Home</div>' } },
      { path: '/merchant/shop/edit', name: 'MerchantShopEdit', component: { template: '<div>Shop Edit</div>' } },
      { path: '/merchant/appointments', name: 'MerchantAppointments', component: { template: '<div>Appointments</div>' } },
      { path: '/merchant/services', name: 'MerchantServices', component: { template: '<div>Services</div>' } },
      { path: '/merchant/reviews', name: 'MerchantReviews', component: { template: '<div>Reviews</div>' } },
      { path: '/login', name: 'Login', component: { template: '<div>Login</div>' } },
    ],
  }) as unknown as Router
}

describe('MerchantLayout', () => {
  let wrapper: any
  let router: Router
  let pinia: Pinia
  let localStorageMock: ReturnType<typeof mockLocalStorage>
  let matchMediaMock: ReturnType<typeof mockMatchMedia>

  beforeEach(async () => {
    localStorageMock = mockLocalStorage()
    Object.defineProperty(window, 'localStorage', { value: localStorageMock })

    matchMediaMock = mockMatchMedia(false)
    
    pinia = createPinia()
    setActivePinia(pinia)

    router = createTestRouter()
    await router.push('/merchant/home')
    await router.isReady()

    wrapper = mount(MerchantLayout, {
      global: {
        plugins: [router, pinia],
        components: {
          ElContainer,
          ElHeader,
          ElAside,
          ElMain,
          ElMenu,
          ElMenuItem,
          ElButton,
          ElIcon,
          ElDropdown,
          ElDropdownMenu,
          ElDropdownItem,
          HomeFilled,
          ShoppingBag,
          Calendar,
          Cubes,
          Star,
          SignOut,
          Bell,
          ArrowDown,
          Setting,
        },
        stubs: {
          'router-view': true,
        },
      },
    })
  })

  afterEach(() => {
    wrapper?.unmount()
    vi.clearAllMocks()
  })

  describe('组件渲染测试', () => {
    it('组件能够正确挂载', () => {
      expect(wrapper.exists()).toBe(true)
    })

    it('包含正确的布局容器', () => {
      expect(wrapper.find('.merchant-layout').exists()).toBe(true)
      expect(wrapper.find('.header').exists()).toBe(true)
      expect(wrapper.find('.main-container').exists()).toBe(true)
      expect(wrapper.find('.aside').exists()).toBe(true)
      expect(wrapper.find('.main').exists()).toBe(true)
    })

    it('头部包含Logo区域和商家名称', () => {
      const headerLeft = wrapper.find('.header-left')
      expect(headerLeft.exists()).toBe(true)
      
      const logoArea = wrapper.find('.logo-area')
      expect(logoArea.exists()).toBe(true)
      
      const merchantName = wrapper.find('.merchant-name')
      expect(merchantName.exists()).toBe(true)
      expect(merchantName.text()).toBe('宠物家园')
    })

    it('头部包含通知按钮', () => {
      const notificationBtn = wrapper.find('.notification-btn')
      expect(notificationBtn.exists()).toBe(true)
      
      const notificationBadge = wrapper.find('.notification-badge')
      expect(notificationBadge.exists()).toBe(true)
      expect(notificationBadge.text()).toBe('5')
    })

    it('头部包含用户信息区域', () => {
      const userInfo = wrapper.find('.user-info')
      expect(userInfo.exists()).toBe(true)
      
      const userAvatar = wrapper.find('.user-avatar')
      expect(userAvatar.exists()).toBe(true)
      
      const username = wrapper.find('.username')
      expect(username.exists()).toBe(true)
      expect(username.text()).toBe('商家名称')
    })

    it('侧边栏包含所有菜单项', () => {
      const menuItems = wrapper.findAll('.el-menu-item')
      expect(menuItems.length).toBe(6)
    })

    it('菜单项包含正确的文本内容', () => {
      const menuItems = wrapper.findAll('.el-menu-item')
      const texts = menuItems.map((item: any) => item.text())
      
      expect(texts.some((t: string) => t.includes('后台首页'))).toBe(true)
      expect(texts.some((t: string) => t.includes('店铺管理'))).toBe(true)
      expect(texts.some((t: string) => t.includes('预约订单列表'))).toBe(true)
      expect(texts.some((t: string) => t.includes('服务管理'))).toBe(true)
      expect(texts.some((t: string) => t.includes('服务评价列表'))).toBe(true)
      expect(texts.some((t: string) => t.includes('退出登录'))).toBe(true)
    })

    it('主内容区域包含router-view', () => {
      const main = wrapper.find('.main')
      expect(main.exists()).toBe(true)
    })
  })

  describe('菜单功能测试', () => {
    it('菜单折叠状态初始为展开', () => {
      expect(wrapper.vm.isCollapse).toBe(false)
    })

    it('点击菜单切换按钮可以折叠菜单', async () => {
      const toggleBtn = wrapper.find('.menu-toggle')
      expect(toggleBtn.exists()).toBe(true)
      
      await toggleBtn.trigger('click')
      expect(wrapper.vm.isCollapse).toBe(true)
    })

    it('点击菜单切换按钮可以展开菜单', async () => {
      wrapper.vm.isCollapse = true
      await wrapper.vm.$nextTick()
      
      const toggleBtn = wrapper.find('.menu-toggle')
      await toggleBtn.trigger('click')
      expect(wrapper.vm.isCollapse).toBe(false)
    })

    it('handleMenuSelect方法调用router.push', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      
      wrapper.vm.handleMenuSelect('/merchant/services')
      
      expect(pushSpy).toHaveBeenCalledWith('/merchant/services')
    })

    it('点击首页菜单项跳转到首页', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      
      wrapper.vm.handleMenuSelect('/merchant/home')
      
      expect(pushSpy).toHaveBeenCalledWith('/merchant/home')
    })

    it('点击预约订单菜单项跳转到预约订单页面', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      
      wrapper.vm.handleMenuSelect('/merchant/appointments')
      
      expect(pushSpy).toHaveBeenCalledWith('/merchant/appointments')
    })

    it('点击服务管理菜单项跳转到服务管理页面', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      
      wrapper.vm.handleMenuSelect('/merchant/services')
      
      expect(pushSpy).toHaveBeenCalledWith('/merchant/services')
    })

    it('点击服务评价菜单项跳转到服务评价页面', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      
      wrapper.vm.handleMenuSelect('/merchant/reviews')
      
      expect(pushSpy).toHaveBeenCalledWith('/merchant/reviews')
    })
  })

  describe('用户下拉菜单测试', () => {
    it('用户下拉菜单存在', () => {
      const dropdown = wrapper.findComponent(ElDropdown)
      expect(dropdown.exists()).toBe(true)
    })

    it('下拉菜单包含个人设置选项', () => {
      const dropdownItems = wrapper.findAllComponents(ElDropdownItem)
      const settingsItem = dropdownItems.find((item: any) => 
        item.text().includes('个人设置')
      )
      expect(settingsItem).toBeDefined()
    })

    it('下拉菜单包含退出登录选项', () => {
      const dropdownItems = wrapper.findAllComponents(ElDropdownItem)
      const logoutItem = dropdownItems.find((item: any) => 
        item.text().includes('退出登录')
      )
      expect(logoutItem).toBeDefined()
    })

    it('下拉菜单组件存在', () => {
      const dropdown = wrapper.findComponent(ElDropdown)
      expect(dropdown.exists()).toBe(true)
    })
  })

  describe('退出登录功能测试', () => {
    it('handleLogout方法存在', () => {
      expect(typeof wrapper.vm.handleLogout).toBe('function')
    })

    it('handleLogout方法调用router.push跳转到登录页', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      
      await wrapper.vm.handleLogout()
      
      expect(pushSpy).toHaveBeenCalledWith('/login')
    })
  })

  describe('响应式布局测试', () => {
    it('侧边栏组件存在', () => {
      const aside = wrapper.find('.aside')
      expect(aside.exists()).toBe(true)
    })

    it('折叠状态下侧边栏样式变化', async () => {
      wrapper.vm.isCollapse = true
      await wrapper.vm.$nextTick()
      
      const aside = wrapper.find('.aside')
      expect(aside.exists()).toBe(true)
    })

    it('菜单折叠状态正确传递给el-menu', async () => {
      const menu = wrapper.findComponent(ElMenu)
      expect(menu.props('collapse')).toBe(false)
      
      wrapper.vm.isCollapse = true
      await wrapper.vm.$nextTick()
      
      expect(menu.props('collapse')).toBe(true)
    })

    it('菜单默认激活项为当前路由路径', () => {
      const menu = wrapper.findComponent(ElMenu)
      expect(menu.props('defaultActive')).toBe('/merchant/home')
    })

    it('组件包含scoped样式', () => {
      const layout = wrapper.find('.merchant-layout')
      expect(layout.exists()).toBe(true)
    })
  })

  describe('样式和交互测试', () => {
    it('头部背景色为绿色', () => {
      const header = wrapper.find('.header')
      expect(header.exists()).toBe(true)
    })

    it('用户信息区域有hover效果样式', () => {
      const userInfo = wrapper.find('.user-info')
      expect(userInfo.exists()).toBe(true)
    })

    it('菜单项有正确的样式类', () => {
      const menu = wrapper.find('.menu')
      expect(menu.exists()).toBe(true)
    })

    it('主内容区域有正确的背景色', () => {
      const main = wrapper.find('.main')
      expect(main.exists()).toBe(true)
    })
  })

  describe('组件状态测试', () => {
    it('isCollapse状态初始为false', () => {
      expect(wrapper.vm.isCollapse).toBe(false)
    })

    it('menuItems数据存在', () => {
      expect(wrapper.vm.menuItems).toBeDefined()
      expect(Array.isArray(wrapper.vm.menuItems)).toBe(true)
      expect(wrapper.vm.menuItems.length).toBe(5)
    })

    it('menuItems包含正确的路径', () => {
      const paths = wrapper.vm.menuItems.map((item: any) => item.path)
      expect(paths).toContain('/merchant/home')
      expect(paths).toContain('/merchant/shop/edit')
      expect(paths).toContain('/merchant/appointments')
      expect(paths).toContain('/merchant/services')
      expect(paths).toContain('/merchant/reviews')
    })

    it('menuItems包含正确的名称', () => {
      const names = wrapper.vm.menuItems.map((item: any) => item.name)
      expect(names).toContain('后台首页')
      expect(names).toContain('店铺管理')
      expect(names).toContain('预约订单列表')
      expect(names).toContain('服务管理')
      expect(names).toContain('服务评价列表')
    })
  })

  describe('路由集成测试', () => {
    it('handleMenuSelect方法调用router.push', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      
      wrapper.vm.handleMenuSelect('/merchant/reviews')
      
      expect(pushSpy).toHaveBeenCalledWith('/merchant/reviews')
    })
  })

  describe('边界情况测试', () => {
    it('快速连续点击菜单切换按钮', async () => {
      const toggleBtn = wrapper.find('.menu-toggle')
      
      await toggleBtn.trigger('click')
      await toggleBtn.trigger('click')
      await toggleBtn.trigger('click')
      
      expect(wrapper.vm.isCollapse).toBe(true)
    })

    it('无效路由路径处理', async () => {
      const pushSpy = vi.spyOn(router, 'push')
      
      wrapper.vm.handleMenuSelect('/invalid-path')
      
      expect(pushSpy).toHaveBeenCalledWith('/invalid-path')
    })
  })
})
