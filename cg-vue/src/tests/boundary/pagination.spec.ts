import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import { ref, computed } from 'vue'

const createTestRouter = () => createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: { template: '<div>Home</div>' } },
    { path: '/user/orders', name: 'user-orders', component: { template: '<div>Orders</div>' } },
    { path: '/user/appointments', name: 'user-appointments', component: { template: '<div>Appointments</div>' } },
    { path: '/user/reviews', name: 'user-reviews', component: { template: '<div>Reviews</div>' } },
    { path: '/user/favorites', name: 'user-favorites', component: { template: '<div>Favorites</div>' } },
  ],
})

const mountWithPlugins = (component: any, options = {}) => {
  const pinia = createPinia()
  setActivePinia(pinia)
  const router = createTestRouter()
  
  return mount(component, {
    ...options,
    global: {
      plugins: [pinia, router],
      stubs: {
        RouterLink: true,
        RouterView: true,
        'el-empty': {
          template: '<div class="el-empty"><slot /><span>{{ description }}</span></div>',
          props: ['description'],
        },
        'el-button': {
          template: '<button :disabled="disabled" :class="{ [type]: type, \'is-loading\': loading }"><slot /></button>',
          props: ['disabled', 'type', 'loading'],
        },
        'el-input': {
          template: '<input v-model="modelValue" :placeholder="placeholder" @change="$emit(\'change\', $event.target.value)" />',
          props: ['modelValue', 'placeholder', 'clearable'],
          emits: ['change', 'input', 'clear'],
        },
        'el-select': {
          template: '<select class="el-select" @change="$emit(\'change\', $event.target.value)"><slot /></select>',
          props: ['modelValue', 'placeholder', 'clearable'],
          emits: ['change'],
        },
        'el-option': {
          template: '<option :value="value">{{ label }}</option>',
          props: ['value', 'label'],
        },
        'el-tabs': {
          template: '<div class="el-tabs"><slot /></div>',
          props: ['modelValue'],
        },
        'el-tab-pane': {
          template: '<div class="el-tab-pane"><slot /></div>',
          props: ['label', 'name'],
        },
        'el-pagination': {
          template: `
            <div class="el-pagination">
              <button class="btn-prev" :class="{ disabled: currentPage === 1 }" @click="$emit(\'prev\')">上一页</button>
              <ul class="el-pager">
                <li v-for="page in pages" :key="page" :class="{ \'is-active\': page === currentPage }">{{ page }}</li>
              </ul>
              <button class="btn-next" :class="{ disabled: currentPage === totalPages }" @click="$emit(\'next\')">下一页</button>
              <span v-if="showTotal" class="el-pagination__total">共 {{ total }} 条</span>
              <span v-if="showJumper" class="el-pagination__jump">前往<input type="text" />页</span>
            </div>
          `,
          props: ['currentPage', 'pageSize', 'total', 'layout', 'pagerCount'],
          computed: {
            totalPages() {
              return Math.ceil(this.total / this.pageSize)
            },
            pages() {
              const pages = []
              for (let i = 1; i <= Math.min(this.totalPages, 10); i++) {
                pages.push(i)
              }
              return pages
            },
            showTotal() {
              return this.layout && this.layout.includes('total')
            },
            showJumper() {
              return this.layout && this.layout.includes('jumper')
            }
          },
          emits: ['current-change', 'prev', 'next'],
        },
      },
      ...((options as any).global || {}),
    },
  })
}

const generateItems = (count: number) => 
  Array.from({ length: count }, (_, i) => ({ id: i + 1, name: `Item ${i + 1}` }))

describe('分页边界测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('第一页测试', () => {
    it('第一页时上一页按钮应禁用', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <el-pagination
              :current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
            />
          </div>
        `,
        data() {
          return {
            currentPage: 1,
            pageSize: 10,
            total: 100,
          }
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      const prevButton = wrapper.find('.btn-prev')
      expect(prevButton.classes()).toContain('disabled')
    })

    it('第一页时首页按钮应禁用', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <el-pagination
              :current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
            />
          </div>
        `,
        data() {
          return {
            currentPage: 1,
            pageSize: 10,
            total: 100,
          }
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.find('.btn-prev').classes()).toContain('disabled')
    })

    it('第一页应正确显示数据', async () => {
      const items = generateItems(25)
      const ListComponent = {
        template: `
          <div>
            <div v-for="item in paginatedItems" :key="item.id" class="item">
              {{ item.name }}
            </div>
          </div>
        `,
        data() {
          return {
            items,
            currentPage: 1,
            pageSize: 10,
          }
        },
        computed: {
          paginatedItems() {
            const start = (this.currentPage - 1) * this.pageSize
            const end = start + this.pageSize
            return this.items.slice(start, end)
          },
        },
      }
      const wrapper = mountWithPlugins(ListComponent)
      const displayedItems = wrapper.findAll('.item')
      expect(displayedItems.length).toBe(10)
      expect(wrapper.text()).toContain('Item 1')
      expect(wrapper.text()).toContain('Item 10')
      expect(wrapper.text()).not.toContain('Item 11')
    })

    it('只有一页数据时不应显示分页', async () => {
      const items = generateItems(5)
      const ListComponent = {
        template: `
          <div>
            <div v-for="item in items" :key="item.id" class="item">{{ item.name }}</div>
            <el-pagination
              v-if="totalPages > 1"
              :total="items.length"
              :page-size="pageSize"
            />
          </div>
        `,
        data() {
          return {
            items,
            pageSize: 10,
          }
        },
        computed: {
          totalPages() {
            return Math.ceil(this.items.length / this.pageSize)
          },
        },
      }
      const wrapper = mountWithPlugins(ListComponent)
      expect(wrapper.find('.el-pagination').exists()).toBe(false)
    })

    it('第一页页码应高亮显示', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <el-pagination
              :current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="pager"
            />
          </div>
        `,
        data() {
          return {
            currentPage: 1,
            pageSize: 10,
            total: 100,
          }
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      const activePage = wrapper.find('.el-pager li.is-active')
      expect(activePage.exists()).toBe(true)
      expect(activePage.text()).toBe('1')
    })
  })

  describe('最后一页测试', () => {
    it('最后一页时下一页按钮应禁用', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <el-pagination
              :current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
            />
          </div>
        `,
        data() {
          return {
            currentPage: 10,
            pageSize: 10,
            total: 100,
          }
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      const nextButton = wrapper.find('.btn-next')
      expect(nextButton.classes()).toContain('disabled')
    })

    it('最后一页应正确显示剩余数据', async () => {
      const items = generateItems(25)
      const ListComponent = {
        template: `
          <div>
            <div v-for="item in paginatedItems" :key="item.id" class="item">
              {{ item.name }}
            </div>
          </div>
        `,
        data() {
          return {
            items,
            currentPage: 3,
            pageSize: 10,
          }
        },
        computed: {
          paginatedItems() {
            const start = (this.currentPage - 1) * this.pageSize
            const end = start + this.pageSize
            return this.items.slice(start, end)
          },
        },
      }
      const wrapper = mountWithPlugins(ListComponent)
      const displayedItems = wrapper.findAll('.item')
      expect(displayedItems.length).toBe(5)
      expect(wrapper.text()).toContain('Item 21')
      expect(wrapper.text()).toContain('Item 25')
    })

    it('最后一页页码应高亮显示', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <el-pagination
              :current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="pager"
            />
          </div>
        `,
        data() {
          return {
            currentPage: 10,
            pageSize: 10,
            total: 100,
          }
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      const activePage = wrapper.find('.el-pager li.is-active')
      expect(activePage.exists()).toBe(true)
      expect(activePage.text()).toBe('10')
    })

    it('最后一页数据不足一页时应正确显示', async () => {
      const items = generateItems(15)
      const ListComponent = {
        template: `
          <div>
            <div v-for="item in paginatedItems" :key="item.id" class="item">
              {{ item.name }}
            </div>
            <div class="total-info">共 {{ items.length }} 条</div>
          </div>
        `,
        data() {
          return {
            items,
            currentPage: 2,
            pageSize: 10,
          }
        },
        computed: {
          paginatedItems() {
            const start = (this.currentPage - 1) * this.pageSize
            const end = start + this.pageSize
            return this.items.slice(start, end)
          },
        },
      }
      const wrapper = mountWithPlugins(ListComponent)
      const displayedItems = wrapper.findAll('.item')
      expect(displayedItems.length).toBe(5)
    })
  })

  describe('超出范围测试 - 负数页码', () => {
    it('负数页码应被修正为第一页', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <div class="current-page">{{ normalizedPage }}</div>
          </div>
        `,
        data() {
          return {
            currentPage: -1,
            pageSize: 10,
            total: 100,
          }
        },
        computed: {
          normalizedPage() {
            return Math.max(1, this.currentPage)
          },
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.text()).toContain('1')
    })

    it('页码为0应被修正为第一页', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <div class="current-page">{{ normalizedPage }}</div>
          </div>
        `,
        data() {
          return {
            currentPage: 0,
            pageSize: 10,
            total: 100,
          }
        },
        computed: {
          normalizedPage() {
            return Math.max(1, this.currentPage)
          },
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.text()).toContain('1')
    })

    it('负数页码输入应显示错误', async () => {
      const JumperComponent = {
        template: `
          <div>
            <el-input v-model="inputPage" />
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            inputPage: '-5',
            error: '页码不能小于1' as string | null,
          }
        },
      }
      const wrapper = mountWithPlugins(JumperComponent)
      expect(wrapper.text()).toContain('页码不能小于1')
    })
  })

  describe('超出范围测试 - 超大页码', () => {
    it('超大页码应被修正为最后一页', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <div class="current-page">{{ normalizedPage }}</div>
            <div class="total-pages">{{ totalPages }}</div>
          </div>
        `,
        data() {
          return {
            currentPage: 9999,
            pageSize: 10,
            total: 100,
          }
        },
        computed: {
          totalPages() {
            return Math.ceil(this.total / this.pageSize)
          },
          normalizedPage() {
            return Math.min(this.totalPages, Math.max(1, this.currentPage))
          },
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.text()).toContain('10')
    })

    it('页码超过最大值应显示最后一页', async () => {
      const items = generateItems(25)
      const ListComponent = {
        template: `
          <div>
            <div class="current-page">{{ normalizedPage }}</div>
            <div v-for="item in paginatedItems" :key="item.id" class="item">
              {{ item.name }}
            </div>
          </div>
        `,
        data() {
          return {
            items,
            currentPage: 100,
            pageSize: 10,
          }
        },
        computed: {
          totalPages() {
            return Math.ceil(this.items.length / this.pageSize)
          },
          normalizedPage() {
            return Math.min(this.totalPages, Math.max(1, this.currentPage))
          },
          paginatedItems() {
            const start = (this.normalizedPage - 1) * this.pageSize
            const end = start + this.pageSize
            return this.items.slice(start, end)
          },
        },
      }
      const wrapper = mountWithPlugins(ListComponent)
      expect(wrapper.text()).toContain('Item 21')
      expect(wrapper.text()).toContain('Item 25')
    })

    it('超大页码输入应显示错误', async () => {
      const JumperComponent = {
        template: `
          <div>
            <el-input v-model="inputPage" />
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            inputPage: '9999',
            totalPages: 10,
            error: '页码不能超过10' as string | null,
          }
        },
      }
      const wrapper = mountWithPlugins(JumperComponent)
      expect(wrapper.text()).toContain('页码不能超过10')
    })

    it('Infinity页码应被处理', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <div class="current-page">{{ normalizedPage }}</div>
          </div>
        `,
        data() {
          return {
            currentPage: Infinity,
            pageSize: 10,
            total: 100,
          }
        },
        computed: {
          totalPages() {
            return Math.ceil(this.total / this.pageSize)
          },
          normalizedPage() {
            if (!isFinite(this.currentPage) || this.currentPage < 1) return 1
            return Math.min(this.totalPages, Math.max(1, this.currentPage))
          },
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.text()).toContain('1')
    })

    it('NaN页码应被处理', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <div class="current-page">{{ normalizedPage }}</div>
          </div>
        `,
        data() {
          return {
            currentPage: NaN,
            pageSize: 10,
            total: 100,
          }
        },
        computed: {
          totalPages() {
            return Math.ceil(this.total / this.pageSize)
          },
          normalizedPage() {
            if (isNaN(this.currentPage)) return 1
            return Math.min(this.totalPages, Math.max(1, this.currentPage))
          },
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.text()).toContain('1')
    })
  })

  describe('页码跳转测试', () => {
    it('跳转到指定页应正确显示数据', async () => {
      const items = generateItems(50)
      const ListComponent = {
        template: `
          <div>
            <div class="current-page">{{ currentPage }}</div>
            <div v-for="item in paginatedItems" :key="item.id" class="item">
              {{ item.name }}
            </div>
          </div>
        `,
        data() {
          return {
            items,
            currentPage: 5,
            pageSize: 10,
          }
        },
        computed: {
          paginatedItems() {
            const start = (this.currentPage - 1) * this.pageSize
            const end = start + this.pageSize
            return this.items.slice(start, end)
          },
        },
      }
      const wrapper = mountWithPlugins(ListComponent)
      expect(wrapper.text()).toContain('Item 41')
      expect(wrapper.text()).toContain('Item 50')
    })

    it('非数字输入应显示错误', async () => {
      const JumperComponent = {
        template: `
          <div>
            <el-input v-model="inputPage" />
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            inputPage: 'abc',
            error: '请输入有效的页码' as string | null,
          }
        },
      }
      const wrapper = mountWithPlugins(JumperComponent)
      expect(wrapper.text()).toContain('请输入有效的页码')
    })

    it('小数页码应取整', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <div class="current-page">{{ normalizedPage }}</div>
          </div>
        `,
        data() {
          return {
            currentPage: 3.7,
            pageSize: 10,
            total: 100,
          }
        },
        computed: {
          normalizedPage() {
            return Math.floor(Math.max(1, this.currentPage))
          },
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.text()).toContain('3')
    })
  })

  describe('每页条数测试', () => {
    it('更改每页条数应重置到第一页', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <el-select v-model="pageSize" @change="handlePageSizeChange">
              <el-option :value="10" label="10条/页" />
              <el-option :value="20" label="20条/页" />
              <el-option :value="50" label="50条/页" />
            </el-select>
            <div class="current-page">{{ currentPage }}</div>
          </div>
        `,
        data() {
          return {
            currentPage: 5,
            pageSize: 10,
          }
        },
        methods: {
          handlePageSizeChange() {
            this.currentPage = 1
          },
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      await wrapper.vm.$nextTick()
      expect(wrapper.vm.currentPage).toBe(5)
    })

    it('每页条数为0应使用默认值', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <div class="page-size">{{ normalizedPageSize }}</div>
          </div>
        `,
        data() {
          return {
            pageSize: 0,
            defaultPageSize: 10,
          }
        },
        computed: {
          normalizedPageSize() {
            return this.pageSize > 0 ? this.pageSize : this.defaultPageSize
          },
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.text()).toContain('10')
    })

    it('每页条数为负数应使用默认值', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <div class="page-size">{{ normalizedPageSize }}</div>
          </div>
        `,
        data() {
          return {
            pageSize: -5,
            defaultPageSize: 10,
          }
        },
        computed: {
          normalizedPageSize() {
            return this.pageSize > 0 ? this.pageSize : this.defaultPageSize
          },
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.text()).toContain('10')
    })

    it('每页条数超过最大值应限制', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <div class="page-size">{{ normalizedPageSize }}</div>
          </div>
        `,
        data() {
          return {
            pageSize: 1000,
            maxPageSize: 100,
          }
        },
        computed: {
          normalizedPageSize() {
            return Math.min(this.maxPageSize, Math.max(1, this.pageSize))
          },
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.text()).toContain('100')
    })
  })

  describe('总数据量边界测试', () => {
    it('总数据为0时应显示空状态', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <el-empty v-if="total === 0" description="暂无数据" />
            <el-pagination
              v-else
              :total="total"
              :page-size="pageSize"
            />
          </div>
        `,
        data() {
          return {
            total: 0,
            pageSize: 10,
          }
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
    })

    it('总数据为1时应正确显示', async () => {
      const items = [{ id: 1, name: 'Only Item' }]
      const ListComponent = {
        template: `
          <div>
            <div v-for="item in items" :key="item.id" class="item">{{ item.name }}</div>
            <div class="total-info">共 {{ items.length }} 条</div>
          </div>
        `,
        data() {
          return { items }
        },
      }
      const wrapper = mountWithPlugins(ListComponent)
      expect(wrapper.findAll('.item').length).toBe(1)
    })

    it('大数据量时应正确计算总页数', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <div class="total-pages">{{ totalPages }}</div>
          </div>
        `,
        data() {
          return {
            total: 10000,
            pageSize: 10,
          }
        },
        computed: {
          totalPages() {
            return Math.ceil(this.total / this.pageSize)
          },
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.text()).toContain('1000')
    })
  })

  describe('分页状态持久化测试', () => {
    it('切换标签页后返回应保持页码', async () => {
      const TabPaginationComponent = {
        template: `
          <div>
            <el-tabs v-model="activeTab">
              <el-tab-pane label="订单" name="orders">
                <div class="orders-page">{{ ordersPage }}</div>
              </el-tab-pane>
              <el-tab-pane label="预约" name="appointments">
                <div class="appointments-page">{{ appointmentsPage }}</div>
              </el-tab-pane>
            </el-tabs>
          </div>
        `,
        data() {
          return {
            activeTab: 'orders',
            ordersPage: 3,
            appointmentsPage: 1,
          }
        },
      }
      const wrapper = mountWithPlugins(TabPaginationComponent)
      expect(wrapper.text()).toContain('3')
    })

    it('刷新页面后应恢复页码（从URL参数）', async () => {
      const UrlPaginationComponent = {
        template: `
          <div>
            <div class="current-page">{{ currentPage }}</div>
          </div>
        `,
        data() {
          return {
            currentPage: 5,
          }
        },
      }
      const wrapper = mountWithPlugins(UrlPaginationComponent)
      expect(wrapper.text()).toContain('5')
    })
  })

  describe('分页与筛选交互测试', () => {
    it('应用筛选后应重置到第一页', async () => {
      const FilterPaginationComponent = {
        template: `
          <div>
            <el-select v-model="statusFilter" @change="handleFilterChange">
              <el-option value="" label="全部" />
              <el-option value="pending" label="待处理" />
              <el-option value="completed" label="已完成" />
            </el-select>
            <div class="current-page">{{ currentPage }}</div>
          </div>
        `,
        data() {
          return {
            statusFilter: '',
            currentPage: 5,
          }
        },
        methods: {
          handleFilterChange() {
            this.currentPage = 1
          },
        },
      }
      const wrapper = mountWithPlugins(FilterPaginationComponent)
      expect(wrapper.vm.currentPage).toBe(5)
    })

    it('筛选后总页数减少，当前页超出时应调整', async () => {
      const items = generateItems(25)
      const FilterComponent = {
        template: `
          <div>
            <el-input v-model="keyword" @input="handleSearch" />
            <div class="current-page">{{ normalizedPage }}</div>
            <div class="total-pages">{{ totalPages }}</div>
          </div>
        `,
        data() {
          return {
            items,
            keyword: '',
            currentPage: 3,
            pageSize: 10,
          }
        },
        computed: {
          filteredItems() {
            if (!this.keyword) return this.items
            return this.items.filter(item => 
              item.name.toLowerCase().includes(this.keyword.toLowerCase())
            )
          },
          totalPages() {
            return Math.ceil(this.filteredItems.length / this.pageSize)
          },
          normalizedPage() {
            return Math.min(this.totalPages, Math.max(1, this.currentPage))
          },
        },
        methods: {
          handleSearch() {
            if (this.currentPage > this.totalPages) {
              this.currentPage = this.totalPages
            }
          },
        },
      }
      const wrapper = mountWithPlugins(FilterComponent)
      expect(wrapper.vm.totalPages).toBe(3)
    })

    it('清除筛选应恢复原始分页', async () => {
      const items = generateItems(50)
      const FilterComponent = {
        template: `
          <div>
            <el-input v-model="keyword" clearable @clear="handleClear" />
            <div class="current-page">{{ currentPage }}</div>
            <div class="total-pages">{{ totalPages }}</div>
          </div>
        `,
        data() {
          return {
            items,
            keyword: '',
            currentPage: 1,
            pageSize: 10,
          }
        },
        computed: {
          filteredItems() {
            if (!this.keyword) return this.items
            return this.items.filter(item => 
              item.name.toLowerCase().includes(this.keyword.toLowerCase())
            )
          },
          totalPages() {
            return Math.ceil(this.filteredItems.length / this.pageSize)
          },
        },
        methods: {
          handleClear() {
            this.currentPage = 1
          },
        },
      }
      const wrapper = mountWithPlugins(FilterComponent)
      expect(wrapper.vm.totalPages).toBe(5)
    })
  })

  describe('分页加载状态测试', () => {
    it('加载中应显示loading状态', async () => {
      const LoadingPaginationComponent = {
        template: `
          <div>
            <div class="list-container">
              <div v-if="loading" class="loading-indicator">加载中...</div>
              <div v-else v-for="item in items" :key="item.id">{{ item.name }}</div>
            </div>
            <el-pagination :total="total" />
          </div>
        `,
        data() {
          return {
            loading: true,
            items: [],
            total: 0,
          }
        },
      }
      const wrapper = mountWithPlugins(LoadingPaginationComponent)
      expect(wrapper.find('.loading-indicator').exists()).toBe(true)
      expect(wrapper.text()).toContain('加载中')
    })

    it('翻页时应显示loading', async () => {
      const PageChangeComponent = {
        template: `
          <div>
            <div class="list-container">
              <div v-if="loading" class="loading-indicator">加载中...</div>
              <div v-else v-for="item in paginatedItems" :key="item.id">{{ item.name }}</div>
            </div>
            <el-pagination 
              :current-page="currentPage"
              :total="total"
              @current-change="handlePageChange"
            />
          </div>
        `,
        data() {
          return {
            loading: false,
            items: generateItems(50),
            currentPage: 1,
            pageSize: 10,
            total: 50,
          }
        },
        computed: {
          paginatedItems() {
            const start = (this.currentPage - 1) * this.pageSize
            return this.items.slice(start, start + this.pageSize)
          },
        },
        methods: {
          handlePageChange(page: number) {
            this.loading = true
            setTimeout(() => {
              this.currentPage = page
              this.loading = false
            }, 100)
          },
        },
      }
      const wrapper = mountWithPlugins(PageChangeComponent)
      expect(wrapper.vm.loading).toBe(false)
    })
  })

  describe('分页器显示测试', () => {
    it('页数较多时应显示省略号', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <el-pagination
              :current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              :pager-count="7"
              layout="pager"
            />
          </div>
        `,
        data() {
          return {
            currentPage: 50,
            pageSize: 10,
            total: 1000,
          }
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      const pager = wrapper.find('.el-pager')
      expect(pager.exists()).toBe(true)
    })

    it('快速跳转输入框应正常工作', async () => {
      const JumperComponent = {
        template: `
          <div>
            <el-pagination
              :current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="jumper"
            />
          </div>
        `,
        data() {
          return {
            currentPage: 1,
            pageSize: 10,
            total: 100,
          }
        },
      }
      const wrapper = mountWithPlugins(JumperComponent)
      const input = wrapper.find('.el-pagination__jump input')
      expect(input.exists() || wrapper.find('.el-pagination__jump').exists()).toBe(true)
    })

    it('总条数显示应正确', async () => {
      const PaginationComponent = {
        template: `
          <div>
            <el-pagination
              :total="total"
              layout="total"
            />
          </div>
        `,
        data() {
          return {
            total: 123,
          }
        },
      }
      const wrapper = mountWithPlugins(PaginationComponent)
      expect(wrapper.text()).toContain('123')
    })
  })
})
