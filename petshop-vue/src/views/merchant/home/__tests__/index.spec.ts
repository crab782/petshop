import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import { defineComponent } from 'vue'
import MerchantHome from '../index.vue'
import { flushPromises } from '@/tests/utils/testUtils'

vi.spyOn(console, 'log').mockImplementation(() => {})

const MerchantCardStub = defineComponent({
  name: 'MerchantCard',
  props: ['title', 'value', 'subtitle', 'icon', 'iconColor', 'buttonText', 'buttonLink'],
  template: `
    <div class="merchant-card-stub">
      <h3>{{ title }}</h3>
      <p class="value">{{ value }}</p>
      <p class="subtitle">{{ subtitle }}</p>
    </div>
  `
})

const ReviewCardStub = defineComponent({
  name: 'ReviewCard',
  props: ['name', 'avatar', 'rating', 'date', 'content', 'serviceType'],
  template: `
    <div class="review-card-stub">
      <h4>{{ name }}</h4>
      <span class="rating">{{ rating }}</span>
      <span class="date">{{ date }}</span>
      <p class="content">{{ content }}</p>
      <span class="service-type">{{ serviceType }}</span>
    </div>
  `
})

describe('MerchantHome', () => {
  let wrapper: VueWrapper<any>

  const mountComponent = (options = {}) => {
    return mount(MerchantHome, {
      global: {
        stubs: {
          MerchantCard: MerchantCardStub,
          ReviewCard: ReviewCardStub,
          'router-link': {
            template: '<a :href="to"><slot /></a>',
            props: ['to'],
          },
        },
      },
      ...options,
    })
  }

  beforeEach(() => {
    wrapper = mountComponent()
  })

  describe('页面标题', () => {
    it('应该正确渲染页面标题', () => {
      const title = wrapper.find('h2')
      expect(title.exists()).toBe(true)
      expect(title.text()).toBe('商家后台首页')
      expect(title.classes()).toContain('text-2xl')
      expect(title.classes()).toContain('font-bold')
    })
  })

  describe('统计卡片', () => {
    it('应该渲染4个统计卡片', () => {
      const statCards = wrapper.findAllComponents(MerchantCardStub)
      expect(statCards.length).toBe(4)
    })

    it('第一个统计卡片应该显示本月销售额', () => {
      const statCards = wrapper.findAllComponents(MerchantCardStub)
      const firstCard = statCards[0]
      
      expect(firstCard.props('title')).toBe('本月销售额')
      expect(firstCard.props('value')).toBe('¥25,670')
      expect(firstCard.props('subtitle')).toBe('较上月增长 12%')
      expect(firstCard.props('icon')).toBe('fa fa-line-chart')
      expect(firstCard.props('iconColor')).toBe('primary')
    })

    it('第二个统计卡片应该显示新客户数', () => {
      const statCards = wrapper.findAllComponents(MerchantCardStub)
      const secondCard = statCards[1]
      
      expect(secondCard.props('title')).toBe('新客户')
      expect(secondCard.props('value')).toBe('24')
      expect(secondCard.props('subtitle')).toBe('较上月增长 8%')
      expect(secondCard.props('icon')).toBe('fa fa-user-plus')
      expect(secondCard.props('iconColor')).toBe('secondary')
    })

    it('第三个统计卡片应该显示服务完成率', () => {
      const statCards = wrapper.findAllComponents(MerchantCardStub)
      const thirdCard = statCards[2]
      
      expect(thirdCard.props('title')).toBe('服务完成率')
      expect(thirdCard.props('value')).toBe('96%')
      expect(thirdCard.props('subtitle')).toBe('较上月增长 2%')
      expect(thirdCard.props('icon')).toBe('fa fa-check-circle')
      expect(thirdCard.props('iconColor')).toBe('green-500')
    })

    it('第四个统计卡片应该显示平均评分', () => {
      const statCards = wrapper.findAllComponents(MerchantCardStub)
      const fourthCard = statCards[3]
      
      expect(fourthCard.props('title')).toBe('平均评分')
      expect(fourthCard.props('value')).toBe('4.8')
      expect(fourthCard.props('subtitle')).toBe('基于 120 条评价')
      expect(fourthCard.props('icon')).toBe('fa fa-star')
      expect(fourthCard.props('iconColor')).toBe('blue-500')
    })

    it('统计卡片应该使用响应式网格布局', () => {
      const gridContainer = wrapper.find('.grid-cols-1.md\\:grid-cols-2.lg\\:grid-cols-4')
      expect(gridContainer.exists()).toBe(true)
    })
  })

  describe('最近订单列表', () => {
    it('应该正确渲染订单表格标题', () => {
      const orderSection = wrapper.find('.bg-white.rounded-lg.shadow-md.p-6')
      expect(orderSection.exists()).toBe(true)
      
      const sectionTitle = orderSection.find('h3')
      expect(sectionTitle.text()).toBe('最近订单')
    })

    it('应该显示查看全部链接', () => {
      const viewAllLink = wrapper.find('a[href="/merchant/appointments"]')
      expect(viewAllLink.exists()).toBe(true)
      expect(viewAllLink.text()).toBe('查看全部')
    })

    it('应该渲染订单表格表头', () => {
      const tableHeaders = wrapper.findAll('thead th')
      expect(tableHeaders.length).toBe(6)
      
      expect(tableHeaders[0].text()).toBe('订单号')
      expect(tableHeaders[1].text()).toBe('客户')
      expect(tableHeaders[2].text()).toBe('服务')
      expect(tableHeaders[3].text()).toBe('状态')
      expect(tableHeaders[4].text()).toBe('日期')
      expect(tableHeaders[5].text()).toBe('金额')
    })

    it('应该渲染4条订单数据', () => {
      const orderRows = wrapper.findAll('tbody tr')
      expect(orderRows.length).toBe(4)
    })

    it('应该正确显示第一条订单数据', () => {
      const firstRow = wrapper.find('tbody tr')
      const cells = firstRow.findAll('td')
      
      expect(cells[0].text()).toBe('ORD-2024-001')
      expect(cells[1].text()).toBe('张先生')
      expect(cells[2].text()).toBe('宠物美容')
      expect(cells[3].text()).toBe('已完成')
      expect(cells[4].text()).toBe('2024-01-15')
      expect(cells[5].text()).toBe('¥120')
    })

    it('已完成状态应该显示绿色标签', () => {
      const statusSpans = wrapper.findAll('tbody tr td span.px-2')
      const completedStatus = statusSpans.find(span => span.text() === '已完成')
      
      expect(completedStatus).toBeDefined()
      expect(completedStatus!.classes()).toContain('bg-green-100')
      expect(completedStatus!.classes()).toContain('text-green-800')
    })

    it('进行中状态应该显示蓝色标签', () => {
      const statusSpans = wrapper.findAll('tbody tr td span.px-2')
      const inProgressStatus = statusSpans.find(span => span.text() === '进行中')
      
      expect(inProgressStatus).toBeDefined()
      expect(inProgressStatus!.classes()).toContain('bg-blue-100')
      expect(inProgressStatus!.classes()).toContain('text-blue-800')
    })

    it('待处理状态应该显示黄色标签', () => {
      const statusSpans = wrapper.findAll('tbody tr td span.px-2')
      const pendingStatus = statusSpans.find(span => span.text() === '待处理')
      
      expect(pendingStatus).toBeDefined()
      expect(pendingStatus!.classes()).toContain('bg-yellow-100')
      expect(pendingStatus!.classes()).toContain('text-yellow-800')
    })

    it('订单行应该有悬停效果', () => {
      const orderRows = wrapper.findAll('tbody tr')
      orderRows.forEach(row => {
        expect(row.classes()).toContain('hover:bg-gray-50')
        expect(row.classes()).toContain('transition')
      })
    })
  })

  describe('最近评价列表', () => {
    it('应该正确渲染评价区域标题', () => {
      const reviewSections = wrapper.findAll('.bg-white.rounded-lg.shadow-md.p-6')
      const reviewSection = reviewSections[1]
      
      const sectionTitle = reviewSection.find('h3')
      expect(sectionTitle.text()).toBe('最近评价')
    })

    it('应该显示查看全部评价链接', () => {
      const viewAllLink = wrapper.find('a[href="/merchant/reviews"]')
      expect(viewAllLink.exists()).toBe(true)
      expect(viewAllLink.text()).toBe('查看全部')
    })

    it('应该渲染3条评价数据', () => {
      const reviewCards = wrapper.findAllComponents(ReviewCardStub)
      expect(reviewCards.length).toBe(3)
    })

    it('第一条评价应该正确传递属性', () => {
      const reviewCards = wrapper.findAllComponents(ReviewCardStub)
      const firstReview = reviewCards[0]
      
      expect(firstReview.props('name')).toBe('张先生')
      expect(firstReview.props('rating')).toBe(5)
      expect(firstReview.props('date')).toBe('2024-01-15')
      expect(firstReview.props('content')).toBe('服务非常专业，宠物美容效果很好，下次还会再来！')
      expect(firstReview.props('serviceType')).toBe('宠物美容')
    })

    it('第二条评价应该有正确的评分', () => {
      const reviewCards = wrapper.findAllComponents(ReviewCardStub)
      const secondReview = reviewCards[1]
      
      expect(secondReview.props('name')).toBe('李女士')
      expect(secondReview.props('rating')).toBe(4)
      expect(secondReview.props('serviceType')).toBe('宠物寄养')
    })

    it('第三条评价应该有正确的服务类型', () => {
      const reviewCards = wrapper.findAllComponents(ReviewCardStub)
      const thirdReview = reviewCards[2]
      
      expect(thirdReview.props('name')).toBe('王女士')
      expect(thirdReview.props('rating')).toBe(5)
      expect(thirdReview.props('serviceType')).toBe('宠物医疗')
    })

    it('评价卡片应该在垂直空间中排列', () => {
      const reviewContainer = wrapper.find('.space-y-4')
      expect(reviewContainer.exists()).toBe(true)
    })
  })

  describe('快捷操作入口', () => {
    it('订单查看全部链接应该正确跳转', () => {
      const orderLink = wrapper.find('a[href="/merchant/appointments"]')
      expect(orderLink.exists()).toBe(true)
      expect(orderLink.attributes('href')).toBe('/merchant/appointments')
    })

    it('评价查看全部链接应该正确跳转', () => {
      const reviewLink = wrapper.find('a[href="/merchant/reviews"]')
      expect(reviewLink.exists()).toBe(true)
      expect(reviewLink.attributes('href')).toBe('/merchant/reviews')
    })

    it('链接应该有悬停下划线效果', () => {
      const links = wrapper.findAll('a.text-primary')
      links.forEach(link => {
        expect(link.classes()).toContain('hover:underline')
      })
    })
  })

  describe('布局结构', () => {
    it('应该使用两列网格布局显示订单和评价', () => {
      const twoColumnGrid = wrapper.find('.grid-cols-1.lg\\:grid-cols-2')
      expect(twoColumnGrid.exists()).toBe(true)
    })

    it('订单和评价区域应该有白色背景和阴影', () => {
      const sections = wrapper.findAll('.bg-white.rounded-lg.shadow-md.p-6')
      expect(sections.length).toBe(2)
      
      sections.forEach(section => {
        expect(section.classes()).toContain('bg-white')
        expect(section.classes()).toContain('rounded-lg')
        expect(section.classes()).toContain('shadow-md')
      })
    })

    it('表格应该支持横向滚动', () => {
      const tableContainer = wrapper.find('.overflow-x-auto')
      expect(tableContainer.exists()).toBe(true)
    })
  })

  describe('空数据处理', () => {
    it('当订单数据为空时表格应该显示空状态', async () => {
      const emptyWrapper = mount(MerchantHome, {
        global: {
          stubs: {
            MerchantCard: MerchantCardStub,
            ReviewCard: ReviewCardStub,
            'router-link': true,
          },
        },
        setup() {
          const stats = [
            {
              title: '本月销售额',
              value: '¥0',
              subtitle: '暂无数据',
              icon: 'fa fa-line-chart',
              iconColor: 'primary'
            }
          ]
          const recentOrders: any[] = []
          const recentReviews: any[] = []
          return { stats, recentOrders, recentReviews }
        }
      })

      const orderRows = emptyWrapper.findAll('tbody tr')
      expect(orderRows.length).toBe(0)
    })

    it('当评价数据为空时应该不显示评价卡片', async () => {
      const emptyWrapper = mount(MerchantHome, {
        global: {
          stubs: {
            MerchantCard: MerchantCardStub,
            ReviewCard: ReviewCardStub,
            'router-link': true,
          },
        },
        setup() {
          const stats: any[] = []
          const recentOrders: any[] = []
          const recentReviews: any[] = []
          return { stats, recentOrders, recentReviews }
        }
      })

      const reviewCards = emptyWrapper.findAllComponents(ReviewCardStub)
      expect(reviewCards.length).toBe(0)
    })

    it('当统计数据为空时应该不显示统计卡片', async () => {
      const emptyWrapper = mount(MerchantHome, {
        global: {
          stubs: {
            MerchantCard: MerchantCardStub,
            ReviewCard: ReviewCardStub,
            'router-link': true,
          },
        },
        setup() {
          const stats: any[] = []
          const recentOrders: any[] = []
          const recentReviews: any[] = []
          return { stats, recentOrders, recentReviews }
        }
      })

      const statCards = emptyWrapper.findAllComponents(MerchantCardStub)
      expect(statCards.length).toBe(0)
    })
  })

  describe('响应式设计', () => {
    it('统计卡片区域应该有响应式类', () => {
      const statsGrid = wrapper.find('.grid-cols-1.md\\:grid-cols-2.lg\\:grid-cols-4')
      expect(statsGrid.classes()).toContain('grid-cols-1')
      expect(statsGrid.classes()).toContain('md:grid-cols-2')
      expect(statsGrid.classes()).toContain('lg:grid-cols-4')
    })

    it('订单和评价区域应该有响应式类', () => {
      const contentGrid = wrapper.find('.grid-cols-1.lg\\:grid-cols-2')
      expect(contentGrid.classes()).toContain('grid-cols-1')
      expect(contentGrid.classes()).toContain('lg:grid-cols-2')
    })
  })

  describe('样式验证', () => {
    it('页面容器应该有正确的字体设置', () => {
      const container = wrapper.find('.merchant-dashboard')
      expect(container.exists()).toBe(true)
    })

    it('统计卡片之间应该有正确的间距', () => {
      const statsGrid = wrapper.find('.grid-cols-1.md\\:grid-cols-2.lg\\:grid-cols-4')
      expect(statsGrid.classes()).toContain('gap-6')
      expect(statsGrid.classes()).toContain('mb-8')
    })

    it('订单表格单元格应该有正确的内边距', () => {
      const tableCells = wrapper.findAll('tbody td')
      tableCells.forEach(cell => {
        expect(cell.classes()).toContain('py-3')
        expect(cell.classes()).toContain('px-4')
      })
    })

    it('状态标签应该有正确的圆角和内边距', () => {
      const statusSpans = wrapper.findAll('tbody tr td span.px-2')
      statusSpans.forEach(span => {
        expect(span.classes()).toContain('px-2')
        expect(span.classes()).toContain('py-1')
        expect(span.classes()).toContain('rounded')
        expect(span.classes()).toContain('text-xs')
        expect(span.classes()).toContain('font-medium')
      })
    })
  })

  describe('数据完整性', () => {
    it('所有订单应该有完整的数据字段', () => {
      const orderRows = wrapper.findAll('tbody tr')
      
      orderRows.forEach(row => {
        const cells = row.findAll('td')
        expect(cells.length).toBe(6)
        
        cells.forEach(cell => {
          expect(cell.text().trim()).not.toBe('')
        })
      })
    })

    it('所有评价应该有完整的数据', () => {
      const reviewCards = wrapper.findAllComponents(ReviewCardStub)
      
      reviewCards.forEach(card => {
        expect(card.props('name')).toBeTruthy()
        expect(card.props('rating')).toBeGreaterThanOrEqual(1)
        expect(card.props('rating')).toBeLessThanOrEqual(5)
        expect(card.props('date')).toBeTruthy()
        expect(card.props('content')).toBeTruthy()
        expect(card.props('serviceType')).toBeTruthy()
      })
    })

    it('所有统计卡片应该有完整的数据', () => {
      const statCards = wrapper.findAllComponents(MerchantCardStub)
      
      statCards.forEach(card => {
        expect(card.props('title')).toBeTruthy()
        expect(card.props('value')).toBeTruthy()
        expect(card.props('icon')).toBeTruthy()
      })
    })
  })
})
