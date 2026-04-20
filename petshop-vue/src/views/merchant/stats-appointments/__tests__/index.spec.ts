import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import StatsAppointments from '../index.vue'
import {
  ElCard,
  ElRow,
  ElCol,
  ElStatistic,
  ElSelect,
  ElOption,
  ElTable,
  ElTableColumn,
  ElTag,
  ElProgress,
  ElButton,
  ElButtonGroup,
  ElDatePicker,
  ElMessage
} from 'element-plus'
import { getMerchantAppointmentStats, type AppointmentStats } from '@/api/merchant'

vi.mock('@/api/merchant', () => ({
  getMerchantAppointmentStats: vi.fn()
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

const mockAppointmentStats: AppointmentStats = {
  totalCount: 150,
  pendingCount: 20,
  completedCount: 110,
  cancelledCount: 20,
  growthRate: 15.5,
  trendData: [
    { date: '2024-04-14', count: 18 },
    { date: '2024-04-15', count: 22 },
    { date: '2024-04-16', count: 25 },
    { date: '2024-04-17', count: 20 },
    { date: '2024-04-18', count: 30 },
    { date: '2024-04-19', count: 15 },
    { date: '2024-04-20', count: 20 }
  ],
  sourceData: [
    { name: '线上预约', value: 80 },
    { name: '电话预约', value: 40 },
    { name: '到店预约', value: 30 }
  ],
  hotServices: [
    { name: '宠物洗澡', count: 60 },
    { name: '宠物美容', count: 45 },
    { name: '宠物寄养', count: 25 },
    { name: '宠物医疗', count: 20 }
  ],
  dailyStats: {
    '2024-04-14': { completedCount: 15, cancelledCount: 3, totalRevenue: 1800 },
    '2024-04-15': { completedCount: 18, cancelledCount: 4, totalRevenue: 2200 },
    '2024-04-16': { completedCount: 20, cancelledCount: 5, totalRevenue: 2500 },
    '2024-04-17': { completedCount: 16, cancelledCount: 4, totalRevenue: 2000 },
    '2024-04-18': { completedCount: 25, cancelledCount: 5, totalRevenue: 3000 },
    '2024-04-19': { completedCount: 12, cancelledCount: 3, totalRevenue: 1500 },
    '2024-04-20': { completedCount: 16, cancelledCount: 4, totalRevenue: 2000 }
  },
  hourData: [
    { hour: 8, count: 5 },
    { hour: 9, count: 15 },
    { hour: 10, count: 25 },
    { hour: 11, count: 20 },
    { hour: 12, count: 10 },
    { hour: 13, count: 15 },
    { hour: 14, count: 30 },
    { hour: 15, count: 25 },
    { hour: 16, count: 20 },
    { hour: 17, count: 10 },
    { hour: 18, count: 5 }
  ]
}

const createWrapper = (options = {}) => {
  return mount(StatsAppointments, {
    global: {
      components: {
        ElCard,
        ElRow,
        ElCol,
        ElStatistic,
        ElSelect,
        ElOption,
        ElTable,
        ElTableColumn,
        ElTag,
        ElProgress,
        ElButton,
        ElButtonGroup,
        ElDatePicker
      },
      stubs: {
        'el-icon': true
      }
    },
    ...options
  })
}

describe('StatsAppointments', () => {
  let wrapper: ReturnType<typeof createWrapper>

  beforeEach(() => {
    vi.mocked(getMerchantAppointmentStats).mockResolvedValue(mockAppointmentStats)
  })

  afterEach(() => {
    vi.clearAllMocks()
    if (wrapper) {
      wrapper.unmount()
    }
  })

  describe('组件渲染', () => {
    it('应该正确渲染页面标题', async () => {
      wrapper = createWrapper()
      await flushPromises()
      expect(wrapper.find('.page-title').text()).toBe('预约统计')
    })

    it('应该正确渲染时间选择按钮组', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const buttons = wrapper.findAllComponents(ElButton)
      const timeButtons = buttons.filter(btn => {
        const text = btn.text()
        return ['今日', '本周', '本月', '本年', '自定义'].includes(text)
      })
      expect(timeButtons.length).toBe(5)
    })

    it('应该正确渲染导出按钮', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const exportButton = wrapper.findAllComponents(ElButton).find(btn => btn.text().includes('导出数据'))
      expect(exportButton?.exists()).toBe(true)
    })
  })

  describe('统计概览卡片', () => {
    it('应该正确显示总预约数', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const statistics = wrapper.findAllComponents(ElStatistic)
      const totalStat = statistics.find(stat => stat.props('title') === '总预约数')
      expect(totalStat?.props('value')).toBe(150)
    })

    it('应该正确显示待处理数', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const statistics = wrapper.findAllComponents(ElStatistic)
      const pendingStat = statistics.find(stat => stat.props('title') === '待处理数')
      expect(pendingStat?.props('value')).toBe(20)
    })

    it('应该正确显示已完成数', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const statistics = wrapper.findAllComponents(ElStatistic)
      const completedStat = statistics.find(stat => stat.props('title') === '已完成数')
      expect(completedStat?.props('value')).toBe(110)
    })

    it('应该正确显示取消数', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const statistics = wrapper.findAllComponents(ElStatistic)
      const cancelledStat = statistics.find(stat => stat.props('title') === '取消数')
      expect(cancelledStat?.props('value')).toBe(20)
    })

    it('应该正确计算完成率', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const statistics = wrapper.findAllComponents(ElStatistic)
      const completionRateStat = statistics.find(stat => stat.props('title') === '完成率')
      expect(completionRateStat?.props('value')).toBe(73)
      expect(completionRateStat?.props('suffix')).toBe('%')
    })

    it('应该正确计算取消率', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const statistics = wrapper.findAllComponents(ElStatistic)
      const cancellationRateStat = statistics.find(stat => stat.props('title') === '取消率')
      expect(cancellationRateStat?.props('value')).toBe(13)
      expect(cancellationRateStat?.props('suffix')).toBe('%')
    })

    it('应该正确显示环比增长率', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const statistics = wrapper.findAllComponents(ElStatistic)
      const growthRateStat = statistics.find(stat => stat.props('title') === '环比增长率')
      expect(growthRateStat?.props('value')).toBe(15.5)
      expect(growthRateStat?.props('suffix')).toBe('%')
    })
  })

  describe('趋势数据表格', () => {
    it('应该正确渲染预约趋势表格', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const tables = wrapper.findAllComponents(ElTable)
      const trendTable = tables.find(table => {
        const columns = table.findAllComponents(ElTableColumn)
        return columns.some(col => col.props('prop') === 'date')
      })
      expect(trendTable?.exists()).toBe(true)
    })

    it('应该正确显示趋势数据', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const vm = wrapper.vm as any
      expect(vm.statsData?.trendData).toHaveLength(7)
      expect(vm.statsData?.trendData[0].date).toBe('2024-04-14')
      expect(vm.statsData?.trendData[0].count).toBe(18)
    })

    it('应该正确渲染热门服务表格', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const tables = wrapper.findAllComponents(ElTable)
      expect(tables.length).toBeGreaterThanOrEqual(2)
    })
  })

  describe('预约来源分析', () => {
    it('应该正确显示来源数据', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const sourceItems = wrapper.findAll('.source-item')
      expect(sourceItems.length).toBe(3)
    })

    it('应该正确显示来源名称', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const sourceNames = wrapper.findAll('.source-name')
      expect(sourceNames[0].text()).toBe('线上预约')
      expect(sourceNames[1].text()).toBe('电话预约')
      expect(sourceNames[2].text()).toBe('到店预约')
    })

    it('应该正确显示来源数量', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const sourceCounts = wrapper.findAll('.source-count')
      expect(sourceCounts[0].text()).toBe('80')
      expect(sourceCounts[1].text()).toBe('40')
      expect(sourceCounts[2].text()).toBe('30')
    })
  })

  describe('时段统计', () => {
    it('应该正确显示高峰时段提示', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const peakHourTip = wrapper.find('.peak-hour-tip')
      expect(peakHourTip.exists()).toBe(true)
      expect(peakHourTip.text()).toContain('预约高峰时段')
    })

    it('应该正确显示时段柱状图', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const hourBars = wrapper.findAll('.hour-bar-item')
      expect(hourBars.length).toBe(11)
    })

    it('应该正确计算高峰时段', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const vm = wrapper.vm as any
      expect(vm.peakHour?.hour).toBe(14)
      expect(vm.peakHour?.count).toBe(30)
    })
  })

  describe('服务类型统计', () => {
    it('应该正确显示服务类型列表', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const serviceItems = wrapper.findAll('.service-item')
      expect(serviceItems.length).toBe(3)
    })

    it('应该正确显示服务名称', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const serviceNames = wrapper.findAll('.service-name')
      expect(serviceNames[0].text()).toBe('线上预约')
    })
  })

  describe('数据摘要', () => {
    it('应该正确显示数据摘要卡片', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const summaryCard = wrapper.find('.summary-card')
      expect(summaryCard.exists()).toBe(true)
    })

    it('应该正确显示最热服务', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const hotServiceName = wrapper.find('.hot-service-name')
      expect(hotServiceName.text()).toBe('宠物洗澡')
    })

    it('应该正确显示高峰时段', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const peakTime = wrapper.find('.peak-time')
      expect(peakTime.text()).toBe('14:00 - 15:00')
    })
  })

  describe('时间范围选择', () => {
    it('应该默认选择今日', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const vm = wrapper.vm as any
      expect(vm.timeType).toBe('today')
    })

    it('点击时间按钮应该切换时间类型', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const vm = wrapper.vm as any
      const buttons = wrapper.findAllComponents(ElButton)
      const weekButton = buttons.find(btn => btn.text() === '本周')
      await weekButton?.trigger('click')
      expect(vm.timeType).toBe('week')
    })

    it('选择自定义时间类型应该显示日期选择器', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const vm = wrapper.vm as any
      vm.timeType = 'custom'
      await wrapper.vm.$nextTick()
      const datePicker = wrapper.findComponent(ElDatePicker)
      expect(datePicker.exists()).toBe(true)
    })

    it('切换时间类型应该重新获取数据', async () => {
      wrapper = createWrapper()
      await flushPromises()
      vi.clearAllMocks()
      vi.mocked(getMerchantAppointmentStats).mockResolvedValue(mockAppointmentStats)
      const buttons = wrapper.findAllComponents(ElButton)
      const monthButton = buttons.find(btn => btn.text() === '本月')
      await monthButton?.trigger('click')
      await flushPromises()
      expect(getMerchantAppointmentStats).toHaveBeenCalledWith('month', undefined, undefined)
    })
  })

  describe('导出功能', () => {
    it('点击导出按钮应该触发导出功能', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const buttons = wrapper.findAllComponents(ElButton)
      const exportButton = buttons.find(btn => btn.text().includes('导出数据'))
      expect(exportButton?.exists()).toBe(true)
      await exportButton?.trigger('click')
      expect(ElMessage.success).toHaveBeenCalled()
    })

    it('导出功能应该创建下载链接', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const createElementSpy = vi.spyOn(document, 'createElement')
      const vm = wrapper.vm as any
      vm.exportToCSV()
      expect(createElementSpy).toHaveBeenCalledWith('a')
      createElementSpy.mockRestore()
    })

    it('没有数据时导出应该显示警告', async () => {
      vi.mocked(getMerchantAppointmentStats).mockRejectedValue(new Error('API Error'))
      wrapper = createWrapper()
      await flushPromises()
      const vm = wrapper.vm as any
      vm.exportToCSV()
      expect(ElMessage.warning).toHaveBeenCalledWith('暂无数据可导出')
    })
  })

  describe('API调用', () => {
    it('组件挂载时应该调用API获取数据', async () => {
      wrapper = createWrapper()
      await flushPromises()
      expect(getMerchantAppointmentStats).toHaveBeenCalled()
    })

    it('API调用失败应该显示错误消息', async () => {
      vi.mocked(getMerchantAppointmentStats).mockRejectedValue(new Error('API Error'))
      wrapper = createWrapper()
      await flushPromises()
      expect(ElMessage.error).toHaveBeenCalledWith('获取统计数据失败')
    })
  })

  describe('计算属性', () => {
    it('没有数据时应该返回默认值', async () => {
      vi.mocked(getMerchantAppointmentStats).mockResolvedValue({
        totalCount: 0,
        pendingCount: 0,
        completedCount: 0,
        cancelledCount: 0,
        trendData: [],
        sourceData: [],
        hotServices: []
      })
      wrapper = createWrapper()
      await flushPromises()
      const vm = wrapper.vm as any
      expect(vm.totalAppointments).toBe(0)
      expect(vm.pendingAppointments).toBe(0)
      expect(vm.completedAppointments).toBe(0)
      expect(vm.cancelledAppointments).toBe(0)
      expect(vm.completionRate).toBe(0)
      expect(vm.cancellationRate).toBe(0)
    })

    it('应该正确计算完成率百分比', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const vm = wrapper.vm as any
      expect(vm.completionRate).toBe(Math.round((110 / 150) * 100))
    })

    it('应该正确计算取消率百分比', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const vm = wrapper.vm as any
      expect(vm.cancellationRate).toBe(Math.round((20 / 150) * 100))
    })
  })

  describe('加载状态', () => {
    it('加载数据时应该显示loading状态', async () => {
      vi.mocked(getMerchantAppointmentStats).mockImplementation(() =>
        new Promise(resolve => setTimeout(() => resolve(mockAppointmentStats), 100))
      )
      wrapper = createWrapper()
      const vm = wrapper.vm as any
      expect(vm.loading).toBe(true)
      await flushPromises()
    })

    it('数据加载完成后应该关闭loading状态', async () => {
      wrapper = createWrapper()
      await flushPromises()
      const vm = wrapper.vm as any
      expect(vm.loading).toBe(false)
    })
  })
})
