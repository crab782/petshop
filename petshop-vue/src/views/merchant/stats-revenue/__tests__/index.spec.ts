import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import StatsRevenue from '../index.vue'
import { ElCard, ElButton, ElButtonGroup, ElDatePicker, ElTable, ElTableColumn, ElRow, ElCol, ElProgress, ElTag, ElEmpty, ElMessage } from 'element-plus'
import * as merchantApi from '@/api/merchant'

vi.mock('@/api/merchant', () => ({
  getMerchantRevenueStats: vi.fn(),
  exportRevenueStats: vi.fn(),
}))

vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      warning: vi.fn(),
      error: vi.fn(),
      info: vi.fn(),
    },
  }
})

const mockRevenueStats: merchantApi.RevenueStats = {
  totalRevenue: 88888.88,
  orderCount: 156,
  avgOrderValue: 569.8,
  lastPeriodRevenue: 75000,
  growthRate: 18.5,
  serviceRevenue: 55555.55,
  productRevenue: 33333.33,
  serviceOrderCount: 98,
  productOrderCount: 58,
  orderList: [
    { id: 1, date: '2024-04-13', serviceAmount: 1500.5, productAmount: 800.25, totalAmount: 2300.75 },
    { id: 2, date: '2024-04-14', serviceAmount: 2200.0, productAmount: 1200.5, totalAmount: 3400.5 },
    { id: 3, date: '2024-04-15', serviceAmount: 1800.0, productAmount: 900.0, totalAmount: 2700.0 },
    { id: 4, date: '2024-04-16', serviceAmount: 2500.0, productAmount: 1500.0, totalAmount: 4000.0 },
    { id: 5, date: '2024-04-17', serviceAmount: 2000.0, productAmount: 1100.0, totalAmount: 3100.0 },
    { id: 6, date: '2024-04-18', serviceAmount: 2800.0, productAmount: 1600.0, totalAmount: 4400.0 },
    { id: 7, date: '2024-04-19', serviceAmount: 3000.0, productAmount: 1800.0, totalAmount: 4800.0 },
  ],
  topServices: [
    { id: 1, name: '宠物洗澡', orderCount: 45, revenue: 4500.0 },
    { id: 2, name: '宠物美容', orderCount: 32, revenue: 6400.0 },
    { id: 3, name: '宠物寄养', orderCount: 21, revenue: 8400.0 },
    { id: 4, name: '宠物医疗', orderCount: 15, revenue: 7500.0 },
    { id: 5, name: '宠物训练', orderCount: 10, revenue: 5000.0 },
  ],
  topProducts: [
    { id: 1, name: '优质狗粮', orderCount: 58, revenue: 11600.0 },
    { id: 2, name: '宠物玩具', orderCount: 42, revenue: 4200.0 },
    { id: 3, name: '宠物零食', orderCount: 35, revenue: 3500.0 },
    { id: 4, name: '宠物保健品', orderCount: 28, revenue: 8400.0 },
    { id: 5, name: '宠物用品', orderCount: 22, revenue: 2200.0 },
  ],
}

const createWrapper = () => {
  return mount(StatsRevenue, {
    global: {
      components: {
        ElCard,
        ElButton,
        ElButtonGroup,
        ElDatePicker,
        ElTable,
        ElTableColumn,
        ElRow,
        ElCol,
        ElProgress,
        ElTag,
        ElEmpty,
      },
      stubs: {
        Download: true,
        TrendCharts: true,
        Money: true,
        ShoppingCart: true,
        Goods: true,
        ArrowUp: true,
        ArrowDown: true,
        Top: true,
        Star: true,
      },
      directives: {
        loading: () => {},
      },
    },
  })
}

describe('StatsRevenue', () => {
  let wrapper: ReturnType<typeof createWrapper>

  beforeEach(() => {
    vi.mocked(merchantApi.getMerchantRevenueStats).mockResolvedValue(mockRevenueStats)
    vi.mocked(merchantApi.exportRevenueStats).mockResolvedValue(new Blob(['test']))
  })

  afterEach(() => {
    vi.clearAllMocks()
    if (wrapper) {
      wrapper.unmount()
    }
  })

  describe('组件渲染', () => {
    it('正确渲染页面标题和导出按钮', async () => {
      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.find('.page-title').text()).toBe('营业额统计')
      expect(wrapper.find('.title-card .el-button').exists()).toBe(true)
      expect(wrapper.find('.title-card .el-button').text()).toContain('导出Excel')
    })

    it('正确渲染时间筛选按钮组', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const buttons = wrapper.findAll('.filter-card .el-button-group .el-button')
      expect(buttons.length).toBe(5)
      expect(buttons[0].text()).toBe('今日')
      expect(buttons[1].text()).toBe('本周')
      expect(buttons[2].text()).toBe('本月')
      expect(buttons[3].text()).toBe('本年')
      expect(buttons[4].text()).toBe('自定义')
    })
  })

  describe('营收概览卡片', () => {
    it('正确显示本月总收入', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const statCards = wrapper.findAll('.stat-card')
      expect(statCards.length).toBe(4)

      const revenueCard = statCards[0]
      expect(revenueCard.find('.stat-label').text()).toBe('本月总收入')
      expect(revenueCard.find('.stat-value').text()).toBe(`¥${mockRevenueStats.totalRevenue.toFixed(2)}`)
    })

    it('正确显示环比增长率', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const statCards = wrapper.findAll('.stat-card')
      const growthCard = statCards[3]

      expect(growthCard.find('.stat-label').text()).toBe('环比增长率')
      expect(growthCard.find('.stat-value').classes()).toContain('positive')
      expect(growthCard.find('.stat-value').text()).toContain('+18.5%')
    })

    it('正确显示本月订单数', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const statCards = wrapper.findAll('.stat-card')
      const orderCard = statCards[1]

      expect(orderCard.find('.stat-label').text()).toBe('本月订单数')
      expect(orderCard.find('.stat-value').text()).toBe('156')
      expect(orderCard.find('.stat-sub').text()).toContain('服务订单 98')
      expect(orderCard.find('.stat-sub').text()).toContain('商品订单 58')
    })

    it('正确显示平均客单价', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const statCards = wrapper.findAll('.stat-card')
      const avgCard = statCards[2]

      expect(avgCard.find('.stat-label').text()).toBe('平均客单价')
      expect(avgCard.find('.stat-value').text()).toBe(`¥${mockRevenueStats.avgOrderValue.toFixed(2)}`)
    })

    it('正确显示负增长率', async () => {
      const negativeGrowthStats = {
        ...mockRevenueStats,
        growthRate: -10.5,
      }
      vi.mocked(merchantApi.getMerchantRevenueStats).mockResolvedValue(negativeGrowthStats)

      wrapper = createWrapper()
      await flushPromises()

      const statCards = wrapper.findAll('.stat-card')
      const growthCard = statCards[3]

      expect(growthCard.find('.stat-value').classes()).toContain('negative')
      expect(growthCard.find('.stat-value').text()).toContain('-10.5%')
    })

    it('正确显示较上期对比信息', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const statCards = wrapper.findAll('.stat-card')
      const revenueCard = statCards[0]

      expect(revenueCard.find('.stat-compare').exists()).toBe(true)
      expect(revenueCard.find('.stat-compare .positive').exists()).toBe(true)
      expect(revenueCard.find('.compare-text').text()).toBe('较上期')
    })
  })

  describe('趋势图渲染', () => {
    it('正确渲染趋势图区域', async () => {
      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.find('.trend-area').exists()).toBe(true)
      expect(wrapper.find('.trend-chart').exists()).toBe(true)
    })

    it('正确显示趋势图标签', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const labels = wrapper.find('.chart-labels')
      expect(labels.find('.service-label').text()).toBe('服务收入')
      expect(labels.find('.product-label').text()).toBe('商品收入')
    })

    it('正确渲染趋势柱状图', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const trendBars = wrapper.findAll('.trend-bar-item')
      expect(trendBars.length).toBe(7)

      const firstBar = trendBars[0]
      expect(firstBar.find('.bar-label').exists()).toBe(true)
      expect(firstBar.find('.bar-value').exists()).toBe(true)
      expect(firstBar.find('.service-bar').exists()).toBe(true)
      expect(firstBar.find('.product-bar').exists()).toBe(true)
    })

    it('正确显示趋势图数值', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const trendBars = wrapper.findAll('.trend-bar-item')
      const lastBar = trendBars[trendBars.length - 1]

      expect(lastBar.find('.bar-value').text()).toBe(`¥${mockRevenueStats.orderList[6].totalAmount.toFixed(0)}`)
    })

    it('无数据时显示空状态', async () => {
      vi.mocked(merchantApi.getMerchantRevenueStats).mockResolvedValue({
        ...mockRevenueStats,
        orderList: [],
      })

      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.find('.trend-chart').exists()).toBe(false)
      expect(wrapper.find('.trend-area').findComponent(ElEmpty).exists()).toBe(true)
    })
  })

  describe('营收构成饼图', () => {
    it('正确渲染营收构成区域', async () => {
      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.find('.composition-area').exists()).toBe(true)
      expect(wrapper.find('.composition-chart').exists()).toBe(true)
    })

    it('正确显示饼图容器', async () => {
      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.find('.pie-container').exists()).toBe(true)
    })

    it('正确显示饼图图例', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const legendItems = wrapper.findAll('.legend-item')
      expect(legendItems.length).toBe(2)

      expect(legendItems[0].find('.legend-name').text()).toBe('服务收入')
      expect(legendItems[1].find('.legend-name').text()).toBe('商品收入')
    })

    it('正确显示营收构成百分比', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const legendItems = wrapper.findAll('.legend-item')
      const servicePercent = Math.round((mockRevenueStats.serviceRevenue / mockRevenueStats.totalRevenue) * 100)
      const productPercent = Math.round((mockRevenueStats.productRevenue / mockRevenueStats.totalRevenue) * 100)

      expect(legendItems[0].find('.legend-percent').text()).toBe(`${servicePercent}%`)
      expect(legendItems[1].find('.legend-percent').text()).toBe(`${productPercent}%`)
    })

    it('正确显示营收构成汇总', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const summaryItems = wrapper.findAll('.summary-item')
      expect(summaryItems.length).toBe(2)

      expect(summaryItems[0].find('.summary-label').text()).toBe('服务收入')
      expect(summaryItems[1].find('.summary-label').text()).toBe('商品收入')
    })

    it('无数据时显示空状态', async () => {
      vi.mocked(merchantApi.getMerchantRevenueStats).mockResolvedValue({
        ...mockRevenueStats,
        serviceRevenue: 0,
        productRevenue: 0,
        totalRevenue: 0,
      })

      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.find('.composition-chart').exists()).toBe(false)
    })
  })

  describe('排行榜显示', () => {
    it('正确渲染Top服务排行表格', async () => {
      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.find('.rank-card').exists()).toBe(true)
      const rankCards = wrapper.findAll('.rank-card')
      expect(rankCards.length).toBe(2)
    })

    it('正确显示Top服务排行标题', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const rankCards = wrapper.findAll('.rank-card')
      expect(rankCards[0].find('.card-title').text()).toContain('Top服务排行')
    })

    it('正确显示Top商品排行标题', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const rankCards = wrapper.findAll('.rank-card')
      expect(rankCards[1].find('.card-title').text()).toContain('Top商品排行')
    })

    it('正确显示服务排行数据', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const rankCards = wrapper.findAll('.rank-card')
      const serviceTable = rankCards[0].findComponent(ElTable)
      expect(serviceTable.exists()).toBe(true)
    })

    it('正确显示商品排行数据', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const rankCards = wrapper.findAll('.rank-card')
      const productTable = rankCards[1].findComponent(ElTable)
      expect(productTable.exists()).toBe(true)
    })

    it('无服务数据时显示空状态', async () => {
      vi.mocked(merchantApi.getMerchantRevenueStats).mockResolvedValue({
        ...mockRevenueStats,
        topServices: [],
      })

      wrapper = createWrapper()
      await flushPromises()

      const rankCards = wrapper.findAll('.rank-card')
      expect(rankCards[0].findComponent(ElEmpty).exists()).toBe(true)
    })

    it('无商品数据时显示空状态', async () => {
      vi.mocked(merchantApi.getMerchantRevenueStats).mockResolvedValue({
        ...mockRevenueStats,
        topProducts: [],
      })

      wrapper = createWrapper()
      await flushPromises()

      const rankCards = wrapper.findAll('.rank-card')
      expect(rankCards[1].findComponent(ElEmpty).exists()).toBe(true)
    })
  })

  describe('营收统计明细表格', () => {
    it('正确渲染明细表格', async () => {
      wrapper = createWrapper()
      await flushPromises()

      expect(wrapper.find('.table-card').exists()).toBe(true)
      expect(wrapper.find('.table-card .card-title').text()).toContain('营收统计明细')
    })

    it('正确显示明细表格', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const tableCard = wrapper.find('.table-card')
      const table = tableCard.findComponent(ElTable)
      expect(table.exists()).toBe(true)
    })
  })

  describe('时间筛选功能', () => {
    it('点击时间按钮触发数据请求', async () => {
      wrapper = createWrapper()
      await flushPromises()

      vi.clearAllMocks()
      vi.mocked(merchantApi.getMerchantRevenueStats).mockResolvedValue(mockRevenueStats)

      const buttons = wrapper.findAll('.filter-card .el-button-group .el-button')
      await buttons[0].trigger('click')
      await flushPromises()

      expect(merchantApi.getMerchantRevenueStats).toHaveBeenCalledWith({ type: 'today' })
    })

    it('切换到自定义时间显示日期选择器', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const buttons = wrapper.findAll('.filter-card .el-button-group .el-button')
      await buttons[4].trigger('click')
      await flushPromises()

      expect(wrapper.findComponent(ElDatePicker).exists()).toBe(true)
    })

    it('默认选中本月', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const buttons = wrapper.findAll('.filter-card .el-button-group .el-button')
      expect(buttons[2].classes()).toContain('el-button--primary')
    })
  })

  describe('导出功能', () => {
    it('点击导出按钮触发导出', async () => {
      const mockBlob = new Blob(['test'], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
      vi.mocked(merchantApi.exportRevenueStats).mockResolvedValue(mockBlob)

      const originalCreateObjectURL = window.URL.createObjectURL
      const originalRevokeObjectURL = window.URL.revokeObjectURL
      const mockCreateObjectURL = vi.fn(() => 'blob:test')
      const mockRevokeObjectURL = vi.fn()
      window.URL.createObjectURL = mockCreateObjectURL
      window.URL.revokeObjectURL = mockRevokeObjectURL

      const originalCreateElement = document.createElement.bind(document)
      const originalAppendChild = document.body.appendChild.bind(document.body)
      const originalRemoveChild = document.body.removeChild.bind(document.body)

      const mockClick = vi.fn()
      const mockAnchor = {
        click: mockClick,
        setAttribute: vi.fn(),
        href: '',
        download: '',
      } as unknown as HTMLAnchorElement

      vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
        if (tagName === 'a') {
          return mockAnchor
        }
        return originalCreateElement(tagName)
      })
      vi.spyOn(document.body, 'appendChild').mockImplementation(() => mockAnchor)
      vi.spyOn(document.body, 'removeChild').mockImplementation(() => mockAnchor)

      wrapper = createWrapper()
      await flushPromises()

      const exportButton = wrapper.find('.title-card .el-button')
      await exportButton.trigger('click')
      await flushPromises()

      expect(merchantApi.exportRevenueStats).toHaveBeenCalled()
      expect(ElMessage.success).toHaveBeenCalledWith('导出成功')

      window.URL.createObjectURL = originalCreateObjectURL
      window.URL.revokeObjectURL = originalRevokeObjectURL
      document.createElement = originalCreateElement
      document.body.appendChild = originalAppendChild
      document.body.removeChild = originalRemoveChild
    })

    it('无数据时导出显示警告', async () => {
      vi.mocked(merchantApi.getMerchantRevenueStats).mockResolvedValue({
        ...mockRevenueStats,
        orderList: [],
      })

      wrapper = createWrapper()
      await flushPromises()

      const exportButton = wrapper.find('.title-card .el-button')
      await exportButton.trigger('click')
      await flushPromises()

      expect(ElMessage.warning).toHaveBeenCalledWith('没有可导出的数据')
      expect(merchantApi.exportRevenueStats).not.toHaveBeenCalled()
    })

    it('导出失败显示错误消息', async () => {
      vi.mocked(merchantApi.exportRevenueStats).mockRejectedValue(new Error('导出失败'))

      wrapper = createWrapper()
      await flushPromises()

      const exportButton = wrapper.find('.title-card .el-button')
      await exportButton.trigger('click')
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('导出失败，请稍后重试')
    })
  })

  describe('数据加载', () => {
    it('组件挂载时自动加载数据', async () => {
      vi.mocked(merchantApi.getMerchantRevenueStats).mockClear()
      vi.mocked(merchantApi.getMerchantRevenueStats).mockResolvedValue(mockRevenueStats)

      wrapper = createWrapper()
      await flushPromises()

      expect(merchantApi.getMerchantRevenueStats).toHaveBeenCalled()
    })

    it('数据加载失败显示错误消息', async () => {
      vi.mocked(merchantApi.getMerchantRevenueStats).mockRejectedValue(new Error('加载失败'))

      wrapper = createWrapper()
      await flushPromises()

      expect(ElMessage.error).toHaveBeenCalledWith('获取营业额统计数据失败')
    })
  })

  describe('计算属性', () => {
    it('正确计算趋势项数据', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      expect(vm.trendItems.length).toBe(7)
      expect(vm.trendItems[0].label).toBeDefined()
      expect(vm.trendItems[0].serviceValue).toBeDefined()
      expect(vm.trendItems[0].productValue).toBeDefined()
      expect(vm.trendItems[0].totalValue).toBeDefined()
    })

    it('正确计算最大趋势值', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      const maxExpected = Math.max(...mockRevenueStats.orderList.slice(-7).map(item => item.totalAmount))
      expect(vm.maxTrendValue).toBe(maxExpected)
    })

    it('正确计算营收构成数据', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      expect(vm.compositionData.length).toBe(2)
      expect(vm.compositionData[0].name).toBe('服务收入')
      expect(vm.compositionData[1].name).toBe('商品收入')
    })

    it('正确计算营收构成百分比', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      const servicePercent = Math.round((mockRevenueStats.serviceRevenue / mockRevenueStats.totalRevenue) * 100)
      const productPercent = Math.round((mockRevenueStats.productRevenue / mockRevenueStats.totalRevenue) * 100)

      expect(vm.compositionPercentages.service).toBe(servicePercent)
      expect(vm.compositionPercentages.product).toBe(productPercent)
    })

    it('正确计算增长率显示', async () => {
      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      expect(vm.growthRateDisplay.value).toBe(18.5)
      expect(vm.growthRateDisplay.isPositive).toBe(true)
    })

    it('正确计算负增长率显示', async () => {
      vi.mocked(merchantApi.getMerchantRevenueStats).mockResolvedValue({
        ...mockRevenueStats,
        growthRate: -15.5,
      })

      wrapper = createWrapper()
      await flushPromises()

      const vm = wrapper.vm as any
      expect(vm.growthRateDisplay.value).toBe(15.5)
      expect(vm.growthRateDisplay.isPositive).toBe(false)
    })
  })
})
