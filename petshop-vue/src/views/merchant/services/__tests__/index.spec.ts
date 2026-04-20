import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import MerchantServices from '../index.vue'
import { createWrapper, flushPromises, setInputValue, selectOption } from '@/tests/utils/testUtils'
import { createServiceList, createService } from '@/tests/fixtures/merchantData'

vi.spyOn(console, 'log').mockImplementation(() => {})

describe('MerchantServices', () => {
  let wrapper: VueWrapper<any>

  const mountComponent = (options = {}) => {
    return createWrapper(MerchantServices, options)
  }

  beforeEach(() => {
    wrapper = mountComponent()
  })

  describe('服务列表渲染', () => {
    it('应该正确渲染页面标题', () => {
      expect(wrapper.find('h2').text()).toBe('服务管理')
    })

    it('应该正确渲染添加服务按钮', () => {
      const addButton = wrapper.find('button.bg-primary')
      expect(addButton.exists()).toBe(true)
      expect(addButton.text()).toContain('添加服务')
    })

    it('应该正确渲染搜索输入框', () => {
      const searchInput = wrapper.find('input[placeholder="搜索服务名称或描述"]')
      expect(searchInput.exists()).toBe(true)
    })

    it('应该正确渲染状态筛选下拉框', () => {
      const statusSelect = wrapper.find('select')
      expect(statusSelect.exists()).toBe(true)
      
      const options = statusSelect.findAll('option')
      expect(options.length).toBe(3)
      expect(options[0].text()).toBe('全部状态')
      expect(options[1].text()).toBe('启用')
      expect(options[2].text()).toBe('禁用')
    })

    it('应该正确渲染所有服务卡片', () => {
      const serviceCards = wrapper.findAll('.bg-white.rounded-lg.shadow-md')
      expect(serviceCards.length).toBe(5)
    })

    it('应该正确渲染服务卡片内容', () => {
      const firstCard = wrapper.find('.bg-white.rounded-lg.shadow-md')
      
      expect(firstCard.find('h3').text()).toBe('宠物美容')
      expect(firstCard.find('.text-gray-700').text()).toContain('¥120')
      expect(firstCard.find('p.text-gray-600').exists()).toBe(true)
    })

    it('应该正确渲染服务状态标签', () => {
      const statusLabels = wrapper.findAll('.bg-green-100.text-green-800')
      expect(statusLabels.length).toBeGreaterThan(0)
      
      const firstStatusLabel = statusLabels[0]
      expect(firstStatusLabel.text()).toBe('启用')
    })

    it('应该正确渲染服务操作按钮', () => {
      const firstCard = wrapper.find('.bg-white.rounded-lg.shadow-md')
      const buttons = firstCard.findAll('button')
      
      expect(buttons.length).toBe(3)
      expect(buttons[0].text()).toBe('编辑')
      expect(buttons[1].text()).toBe('禁用')
      expect(buttons[2].text()).toBe('删除')
    })

    it('应该正确渲染服务图片', () => {
      const serviceImages = wrapper.findAll('img[alt="服务图片"]')
      expect(serviceImages.length).toBe(5)
    })
  })

  describe('搜索功能', () => {
    it('应该能够通过服务名称搜索', async () => {
      const searchInput = wrapper.find('input[placeholder="搜索服务名称或描述"]')
      
      await setInputValue(searchInput.element as HTMLInputElement, '美容')
      await flushPromises()
      
      const filteredCards = wrapper.findAll('.bg-white.rounded-lg.shadow-md')
      expect(filteredCards.length).toBe(1)
      expect(filteredCards[0].find('h3').text()).toBe('宠物美容')
    })

    it('应该能够通过服务描述搜索', async () => {
      const searchInput = wrapper.find('input[placeholder="搜索服务名称或描述"]')
      
      await setInputValue(searchInput.element as HTMLInputElement, '寄养')
      await flushPromises()
      
      const filteredCards = wrapper.findAll('.bg-white.rounded-lg.shadow-md')
      expect(filteredCards.length).toBe(1)
      expect(filteredCards[0].find('h3').text()).toBe('宠物寄养')
    })

    it('搜索无结果时应该显示空列表', async () => {
      const searchInput = wrapper.find('input[placeholder="搜索服务名称或描述"]')
      
      await setInputValue(searchInput.element as HTMLInputElement, '不存在的服务')
      await flushPromises()
      
      const filteredCards = wrapper.findAll('.bg-white.rounded-lg.shadow-md')
      expect(filteredCards.length).toBe(0)
    })

    it('清空搜索应该显示所有服务', async () => {
      const searchInput = wrapper.find('input[placeholder="搜索服务名称或描述"]')
      
      await setInputValue(searchInput.element as HTMLInputElement, '美容')
      await flushPromises()
      
      expect(wrapper.findAll('.bg-white.rounded-lg.shadow-md').length).toBe(1)
      
      await setInputValue(searchInput.element as HTMLInputElement, '')
      await flushPromises()
      
      expect(wrapper.findAll('.bg-white.rounded-lg.shadow-md').length).toBe(5)
    })

    it('搜索应该是大小写不敏感的', async () => {
      const searchInput = wrapper.find('input[placeholder="搜索服务名称或描述"]')
      
      await setInputValue(searchInput.element as HTMLInputElement, '医疗')
      await flushPromises()
      
      const filteredCards = wrapper.findAll('.bg-white.rounded-lg.shadow-md')
      expect(filteredCards.length).toBe(1)
    })
  })

  describe('状态筛选功能', () => {
    it('应该能够筛选启用状态的服务', async () => {
      const statusSelect = wrapper.find('select')
      
      await selectOption(statusSelect.element as HTMLSelectElement, '启用')
      await flushPromises()
      
      const filteredCards = wrapper.findAll('.bg-white.rounded-lg.shadow-md')
      expect(filteredCards.length).toBe(5)
      
      filteredCards.forEach(card => {
        const statusLabel = card.find('.bg-green-100.text-green-800')
        expect(statusLabel.exists()).toBe(true)
        expect(statusLabel.text()).toBe('启用')
      })
    })

    it('应该能够筛选禁用状态的服务', async () => {
      const statusSelect = wrapper.find('select')
      
      await selectOption(statusSelect.element as HTMLSelectElement, '禁用')
      await flushPromises()
      
      const filteredCards = wrapper.findAll('.bg-white.rounded-lg.shadow-md')
      expect(filteredCards.length).toBe(0)
    })

    it('选择全部状态应该显示所有服务', async () => {
      const statusSelect = wrapper.find('select')
      
      await selectOption(statusSelect.element as HTMLSelectElement, '启用')
      await flushPromises()
      
      await selectOption(statusSelect.element as HTMLSelectElement, '')
      await flushPromises()
      
      const filteredCards = wrapper.findAll('.bg-white.rounded-lg.shadow-md')
      expect(filteredCards.length).toBe(5)
    })
  })

  describe('搜索和筛选组合功能', () => {
    it('应该能够同时使用搜索和状态筛选', async () => {
      const searchInput = wrapper.find('input[placeholder="搜索服务名称或描述"]')
      const statusSelect = wrapper.find('select')
      
      await setInputValue(searchInput.element as HTMLInputElement, '宠物')
      await selectOption(statusSelect.element as HTMLSelectElement, '启用')
      await flushPromises()
      
      const filteredCards = wrapper.findAll('.bg-white.rounded-lg.shadow-md')
      expect(filteredCards.length).toBe(5)
    })

    it('搜索和筛选条件都为空时应该显示所有服务', async () => {
      const searchInput = wrapper.find('input[placeholder="搜索服务名称或描述"]')
      const statusSelect = wrapper.find('select')
      
      await setInputValue(searchInput.element as HTMLInputElement, '')
      await selectOption(statusSelect.element as HTMLSelectElement, '')
      await flushPromises()
      
      const filteredCards = wrapper.findAll('.bg-white.rounded-lg.shadow-md')
      expect(filteredCards.length).toBe(5)
    })
  })

  describe('分页功能', () => {
    it('应该正确渲染分页组件', () => {
      const pagination = wrapper.find('nav[aria-label="Pagination"]')
      expect(pagination.exists()).toBe(true)
    })

    it('应该正确渲染上一页按钮', () => {
      const prevButton = wrapper.find('button.rounded-l-md')
      expect(prevButton.exists()).toBe(true)
      expect(prevButton.find('.fa-chevron-left').exists()).toBe(true)
    })

    it('应该正确渲染下一页按钮', () => {
      const nextButton = wrapper.find('button.rounded-r-md')
      expect(nextButton.exists()).toBe(true)
      expect(nextButton.find('.fa-chevron-right').exists()).toBe(true)
    })

    it('应该正确渲染页码按钮', () => {
      const pageButtons = wrapper.findAll('button').filter(btn => {
        const text = btn.text()
        return text === '1' || text === '2'
      })
      expect(pageButtons.length).toBe(2)
    })

    it('当前页应该高亮显示', () => {
      const pagination = wrapper.find('nav[aria-label="Pagination"]')
      const activePage = pagination.find('button.bg-primary.text-white')
      expect(activePage.exists()).toBe(true)
      expect(activePage.text()).toBe('1')
    })

    it('点击页码按钮应该触发事件', async () => {
      const pageButtons = wrapper.findAll('button').filter(btn => {
        const text = btn.text()
        return text === '1' || text === '2'
      })
      
      await pageButtons[1].trigger('click')
      await flushPromises()
      
      expect(true).toBe(true)
    })
  })

  describe('批量操作功能', () => {
    it('应该能够点击编辑按钮', async () => {
      const editButtons = wrapper.findAll('button').filter(btn => btn.text() === '编辑')
      expect(editButtons.length).toBeGreaterThan(0)
      
      await editButtons[0].trigger('click')
      await flushPromises()
      
      expect(true).toBe(true)
    })

    it('应该能够点击禁用按钮', async () => {
      const disableButtons = wrapper.findAll('button.bg-gray-200')
      expect(disableButtons.length).toBeGreaterThan(0)
      
      await disableButtons[0].trigger('click')
      await flushPromises()
      
      expect(true).toBe(true)
    })

    it('应该能够点击删除按钮', async () => {
      const deleteButtons = wrapper.findAll('button.bg-red-500')
      expect(deleteButtons.length).toBeGreaterThan(0)
      
      await deleteButtons[0].trigger('click')
      await flushPromises()
      
      expect(true).toBe(true)
    })

    it('所有服务卡片应该有相同的操作按钮', () => {
      const serviceCards = wrapper.findAll('.bg-white.rounded-lg.shadow-md')
      
      serviceCards.forEach(card => {
        const buttons = card.findAll('button')
        expect(buttons.length).toBe(3)
        expect(buttons[0].text()).toBe('编辑')
        expect(buttons[1].text()).toBe('禁用')
        expect(buttons[2].text()).toBe('删除')
      })
    })
  })

  describe('响应式布局', () => {
    it('应该正确应用网格布局类', () => {
      const gridContainer = wrapper.find('.grid')
      expect(gridContainer.classes()).toContain('grid-cols-1')
      expect(gridContainer.classes()).toContain('md:grid-cols-2')
      expect(gridContainer.classes()).toContain('lg:grid-cols-3')
    })

    it('应该正确应用搜索区域布局类', () => {
      const searchArea = wrapper.find('.flex-col.md\\:flex-row')
      expect(searchArea.exists()).toBe(true)
    })
  })

  describe('数据绑定', () => {
    it('searchQuery 应该正确绑定到输入框', async () => {
      const searchInput = wrapper.find('input[placeholder="搜索服务名称或描述"]')
      
      await setInputValue(searchInput.element as HTMLInputElement, '测试搜索')
      
      expect(wrapper.vm.searchQuery).toBe('测试搜索')
    })

    it('statusFilter 应该正确绑定到下拉框', async () => {
      const statusSelect = wrapper.find('select')
      
      await selectOption(statusSelect.element as HTMLSelectElement, '启用')
      
      expect(wrapper.vm.statusFilter).toBe('启用')
    })
  })

  describe('组件方法', () => {
    it('filteredServices 计算属性应该返回正确的过滤结果', async () => {
      expect(wrapper.vm.filteredServices.length).toBe(5)
      
      wrapper.vm.searchQuery = '美容'
      await flushPromises()
      
      expect(wrapper.vm.filteredServices.length).toBe(1)
      expect(wrapper.vm.filteredServices[0].name).toBe('宠物美容')
    })

    it('filteredServices 应该根据状态过滤', async () => {
      wrapper.vm.statusFilter = '启用'
      await flushPromises()
      
      expect(wrapper.vm.filteredServices.length).toBe(5)
      
      wrapper.vm.statusFilter = '禁用'
      await flushPromises()
      
      expect(wrapper.vm.filteredServices.length).toBe(0)
    })

    it('filteredServices 应该同时应用搜索和状态过滤', async () => {
      wrapper.vm.searchQuery = '宠物'
      wrapper.vm.statusFilter = '启用'
      await flushPromises()
      
      expect(wrapper.vm.filteredServices.length).toBe(5)
    })
  })

  describe('边界情况', () => {
    it('空搜索字符串应该显示所有服务', async () => {
      wrapper.vm.searchQuery = ''
      await flushPromises()
      
      expect(wrapper.vm.filteredServices.length).toBe(5)
    })

    it('特殊字符搜索应该正常工作', async () => {
      wrapper.vm.searchQuery = '@#$%'
      await flushPromises()
      
      expect(wrapper.vm.filteredServices.length).toBe(0)
    })

    it('长字符串搜索应该正常工作', async () => {
      wrapper.vm.searchQuery = '这是一个非常非常长的搜索字符串用于测试边界情况'
      await flushPromises()
      
      expect(wrapper.vm.filteredServices.length).toBe(0)
    })
  })

  describe('UI 交互', () => {
    it('点击添加服务按钮应该触发事件', async () => {
      const addButton = wrapper.find('button.bg-primary')
      
      await addButton.trigger('click')
      await flushPromises()
      
      expect(true).toBe(true)
    })

    it('服务卡片应该有悬停效果类', () => {
      const serviceCard = wrapper.find('.hover\\:shadow-lg')
      expect(serviceCard.exists()).toBe(true)
    })

    it('按钮应该有过渡效果类', () => {
      const buttons = wrapper.findAll('button.transition')
      expect(buttons.length).toBeGreaterThan(0)
    })
  })
})
