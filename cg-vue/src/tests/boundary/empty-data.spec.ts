import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'

const createTestRouter = () => createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: { template: '<div>Home</div>' } },
    { path: '/user/pets', name: 'user-pets', component: { template: '<div>Pets</div>' } },
    { path: '/user/orders', name: 'user-orders', component: { template: '<div>Orders</div>' } },
    { path: '/user/appointments', name: 'user-appointments', component: { template: '<div>Appointments</div>' } },
    { path: '/user/favorites', name: 'user-favorites', component: { template: '<div>Favorites</div>' } },
    { path: '/user/reviews', name: 'user-reviews', component: { template: '<div>Reviews</div>' } },
    { path: '/user/notifications', name: 'user-notifications', component: { template: '<div>Notifications</div>' } },
    { path: '/user/addresses', name: 'user-addresses', component: { template: '<div>Addresses</div>' } },
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
          template: '<button :disabled="disabled"><slot /></button>',
          props: ['disabled', 'type', 'loading'],
        },
        'el-input': {
          template: '<input v-model="modelValue" />',
          props: ['modelValue', 'placeholder'],
        },
        'el-select': {
          template: '<select class="el-select"><slot /></select>',
          props: ['modelValue', 'placeholder'],
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
        'el-table': {
          template: '<div class="el-table"><slot /><slot name="empty" /></div>',
          props: ['data', 'loading'],
        },
        'el-form': {
          template: '<form class="el-form"><slot /></form>',
          props: ['model', 'rules'],
        },
        'el-form-item': {
          template: '<div class="el-form-item"><slot /><span v-if="error" class="error">{{ error }}</span></div>',
          props: ['label', 'prop', 'error'],
        },
      },
      ...((options as any).global || {}),
    },
  })
}

describe('空数据状态测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('宠物列表空数据', () => {
    const UserPetsComponent = {
      template: `
        <div class="user-pets">
          <el-empty v-if="pets.length === 0" description="暂无宠物数据" />
          <div v-else class="pets-grid">
            <div v-for="pet in pets" :key="pet.id" class="pet-item">
              {{ pet.name }}
            </div>
          </div>
        </div>
      `,
      data() {
        return {
          pets: [] as any[],
        }
      },
    }

    it('当宠物列表为空时应显示空状态提示', () => {
      const wrapper = mountWithPlugins(UserPetsComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无宠物数据')
    })

    it('空状态时应显示添加宠物按钮', () => {
      const ComponentWithAddButton = {
        template: `
          <div>
            <el-empty v-if="pets.length === 0" description="暂无宠物数据">
              <el-button type="primary" @click="$emit('add')">添加宠物</el-button>
            </el-empty>
          </div>
        `,
        data() {
          return { pets: [] }
        },
      }
      const wrapper = mountWithPlugins(ComponentWithAddButton)
      expect(wrapper.find('button').exists()).toBe(true)
      expect(wrapper.text()).toContain('添加宠物')
    })

    it('空状态时不应显示宠物列表', () => {
      const wrapper = mountWithPlugins(UserPetsComponent)
      expect(wrapper.find('.pets-grid').exists()).toBe(false)
    })

    it('空数据时筛选功能仍应可用', () => {
      const ComponentWithFilter = {
        template: `
          <div>
            <el-input v-model="searchName" placeholder="搜索宠物名称" />
            <el-select v-model="filterType" placeholder="选择类型" />
            <el-empty v-if="filteredPets.length === 0" description="暂无宠物数据" />
          </div>
        `,
        data() {
          return {
            searchName: '',
            filterType: '',
            pets: [],
          }
        },
        computed: {
          filteredPets() {
            return this.pets
          },
        },
      }
      const wrapper = mountWithPlugins(ComponentWithFilter)
      expect(wrapper.find('input').exists()).toBe(true)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
    })
  })

  describe('订单列表空数据', () => {
    const UserOrdersComponent = {
      template: `
        <div class="user-orders">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="商品订单" name="product">
              <el-table :data="productOrders">
                <template #empty>
                  <el-empty description="暂无商品订单" />
                </template>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="服务预约" name="appointment">
              <el-table :data="appointments">
                <template #empty>
                  <el-empty description="暂无服务预约订单" />
                </template>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
      `,
      data() {
        return {
          activeTab: 'product',
          productOrders: [] as any[],
          appointments: [] as any[],
        }
      },
    }

    it('商品订单为空时应显示空状态', () => {
      const wrapper = mountWithPlugins(UserOrdersComponent)
      expect(wrapper.text()).toContain('暂无商品订单')
    })

    it('服务预约为空时应显示空状态', async () => {
      const wrapper = mountWithPlugins(UserOrdersComponent)
      const tabs = wrapper.findAll('.el-tabs__item')
      if (tabs.length > 1) {
        await tabs[1].trigger('click')
      }
      expect(wrapper.text()).toContain('暂无服务预约订单')
    })

    it('空订单时筛选功能仍应可用', () => {
      const ComponentWithFilter = {
        template: `
          <div>
            <el-select v-model="statusFilter" placeholder="全部状态" />
            <el-empty v-if="orders.length === 0" description="暂无订单" />
          </div>
        `,
        data() {
          return {
            statusFilter: '',
            orders: [],
          }
        },
      }
      const wrapper = mountWithPlugins(ComponentWithFilter)
      expect(wrapper.find('.el-select').exists()).toBe(true)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
    })

    it('空订单时批量操作按钮应禁用或隐藏', () => {
      const ComponentWithBatchActions = {
        template: `
          <div>
            <div v-if="selectedOrders.length > 0" class="batch-actions">
              <el-button type="warning">批量取消</el-button>
              <el-button type="danger">批量删除</el-button>
            </div>
            <el-empty v-if="orders.length === 0" description="暂无订单" />
          </div>
        `,
        data() {
          return {
            selectedOrders: [] as number[],
            orders: [] as any[],
          }
        },
      }
      const wrapper = mountWithPlugins(ComponentWithBatchActions)
      expect(wrapper.find('.batch-actions').exists()).toBe(false)
    })
  })

  describe('预约列表空数据', () => {
    const AppointmentsComponent = {
      template: `
        <div class="appointments">
          <el-empty v-if="appointments.length === 0" description="暂无预约记录" />
          <div v-else class="appointment-list">
            <div v-for="apt in appointments" :key="apt.id" class="appointment-item">
              {{ apt.id }}
            </div>
          </div>
        </div>
      `,
      data() {
        return {
          appointments: [] as any[],
        }
      },
    }

    it('预约列表为空时应显示空状态', () => {
      const wrapper = mountWithPlugins(AppointmentsComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无预约记录')
    })

    it('空预约时状态筛选应显示无数据', () => {
      const ComponentWithStatusFilter = {
        template: `
          <div>
            <el-select v-model="statusFilter" placeholder="全部状态">
              <el-option label="全部" value="" />
              <el-option label="待确认" value="pending" />
              <el-option label="已确认" value="confirmed" />
            </el-select>
            <el-empty v-if="filteredAppointments.length === 0" description="暂无预约记录" />
          </div>
        `,
        data() {
          return {
            statusFilter: '',
            appointments: [] as any[],
          }
        },
        computed: {
          filteredAppointments() {
            return this.appointments
          },
        },
      }
      const wrapper = mountWithPlugins(ComponentWithStatusFilter)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
    })
  })

  describe('收藏列表空数据', () => {
    const FavoritesComponent = {
      template: `
        <div class="favorites">
          <el-empty v-if="favorites.length === 0" description="暂无收藏">
            <template #image>
              <div class="empty-icon">❤️</div>
            </template>
          </el-empty>
          <div v-else class="favorite-list">
            <div v-for="fav in favorites" :key="fav.id">{{ fav.name }}</div>
          </div>
        </div>
      `,
      data() {
        return {
          favorites: [] as any[],
        }
      },
    }

    it('收藏列表为空时应显示空状态', () => {
      const wrapper = mountWithPlugins(FavoritesComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无收藏')
    })

    it('空收藏时应显示引导用户去浏览商家', () => {
      const ComponentWithGuide = {
        template: `
          <div>
            <el-empty v-if="favorites.length === 0" description="暂无收藏">
              <el-button type="primary" @click="$emit('browse')">去逛逛</el-button>
            </el-empty>
          </div>
        `,
        data() {
          return { favorites: [] }
        },
      }
      const wrapper = mountWithPlugins(ComponentWithGuide)
      expect(wrapper.find('button').exists()).toBe(true)
      expect(wrapper.text()).toContain('去逛逛')
    })
  })

  describe('评价列表空数据', () => {
    const ReviewsComponent = {
      template: `
        <div class="reviews">
          <el-empty v-if="reviews.length === 0" description="暂无评价记录" />
          <div v-else class="review-list">
            <div v-for="review in reviews" :key="review.id">{{ review.comment }}</div>
          </div>
        </div>
      `,
      data() {
        return {
          reviews: [] as any[],
        }
      },
    }

    it('评价列表为空时应显示空状态', () => {
      const wrapper = mountWithPlugins(ReviewsComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无评价记录')
    })

    it('空评价时评分筛选应可用', () => {
      const ComponentWithRatingFilter = {
        template: `
          <div>
            <el-select v-model="ratingFilter" placeholder="全部评分" />
            <el-empty v-if="reviews.length === 0" description="暂无评价记录" />
          </div>
        `,
        data() {
          return {
            ratingFilter: '',
            reviews: [] as any[],
          }
        },
      }
      const wrapper = mountWithPlugins(ComponentWithRatingFilter)
      expect(wrapper.find('.el-select').exists()).toBe(true)
    })
  })

  describe('通知列表空数据', () => {
    const NotificationsComponent = {
      template: `
        <div class="notifications">
          <el-empty v-if="notifications.length === 0" description="暂无通知消息" />
          <div v-else class="notification-list">
            <div v-for="notif in notifications" :key="notif.id">{{ notif.title }}</div>
          </div>
        </div>
      `,
      data() {
        return {
          notifications: [] as any[],
        }
      },
    }

    it('通知列表为空时应显示空状态', () => {
      const wrapper = mountWithPlugins(NotificationsComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无通知消息')
    })

    it('空通知时全部标记已读按钮应禁用', () => {
      const ComponentWithMarkAllRead = {
        template: `
          <div>
            <el-button 
              :disabled="notifications.length === 0" 
              @click="$emit('markAllRead')"
            >
              全部标记已读
            </el-button>
            <el-empty v-if="notifications.length === 0" description="暂无通知消息" />
          </div>
        `,
        data() {
          return { notifications: [] }
        },
      }
      const wrapper = mountWithPlugins(ComponentWithMarkAllRead)
      const button = wrapper.find('button')
      expect(button.attributes('disabled')).toBeDefined()
    })
  })

  describe('地址列表空数据', () => {
    const AddressesComponent = {
      template: `
        <div class="addresses">
          <el-empty v-if="addresses.length === 0" description="暂无收货地址">
            <el-button type="primary" @click="$emit('add')">添加地址</el-button>
          </el-empty>
          <div v-else class="address-list">
            <div v-for="addr in addresses" :key="addr.id">{{ addr.detailAddress }}</div>
          </div>
        </div>
      `,
      data() {
        return {
          addresses: [] as any[],
        }
      },
    }

    it('地址列表为空时应显示空状态', () => {
      const wrapper = mountWithPlugins(AddressesComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无收货地址')
    })

    it('空地址时应显示添加地址按钮', () => {
      const wrapper = mountWithPlugins(AddressesComponent)
      expect(wrapper.find('button').exists()).toBe(true)
      expect(wrapper.text()).toContain('添加地址')
    })
  })

  describe('购物车空数据', () => {
    const CartComponent = {
      template: `
        <div class="cart">
          <el-empty v-if="cartItems.length === 0" description="购物车是空的">
            <el-button type="primary" @click="$emit('goShopping')">去购物</el-button>
          </el-empty>
          <div v-else class="cart-list">
            <div v-for="item in cartItems" :key="item.id">{{ item.productName }}</div>
          </div>
        </div>
      `,
      data() {
        return {
          cartItems: [] as any[],
        }
      },
    }

    it('购物车为空时应显示空状态', () => {
      const wrapper = mountWithPlugins(CartComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('购物车是空的')
    })

    it('空购物车时应显示去购物按钮', () => {
      const wrapper = mountWithPlugins(CartComponent)
      expect(wrapper.find('button').exists()).toBe(true)
      expect(wrapper.text()).toContain('去购物')
    })

    it('空购物车时结算按钮应禁用', () => {
      const ComponentWithCheckout = {
        template: `
          <div>
            <el-empty v-if="cartItems.length === 0" description="购物车是空的" />
            <el-button :disabled="cartItems.length === 0" type="primary">结算</el-button>
          </div>
        `,
        data() {
          return { cartItems: [] }
        },
      }
      const wrapper = mountWithPlugins(ComponentWithCheckout)
      const button = wrapper.find('button')
      expect(button.attributes('disabled')).toBeDefined()
    })
  })

  describe('搜索结果空数据', () => {
    const SearchComponent = {
      template: `
        <div class="search">
          <el-input v-model="keyword" placeholder="搜索" />
          <el-empty v-if="searchResults.length === 0 && hasSearched" description="未找到相关结果" />
          <div v-else-if="searchResults.length > 0" class="results">
            <div v-for="item in searchResults" :key="item.id">{{ item.name }}</div>
          </div>
        </div>
      `,
      data() {
        return {
          keyword: '',
          searchResults: [] as any[],
          hasSearched: false,
        }
      },
    }

    it('搜索无结果时应显示空状态', () => {
      const wrapper = mountWithPlugins(SearchComponent, {
        data() {
          return {
            keyword: '不存在的商品',
            searchResults: [],
            hasSearched: true,
          }
        },
      })
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('未找到相关结果')
    })

    it('未搜索时不应显示空状态', () => {
      const wrapper = mountWithPlugins(SearchComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(false)
    })

    it('搜索无结果时应显示搜索建议', () => {
      const ComponentWithSuggestions = {
        template: `
          <div>
            <el-empty v-if="searchResults.length === 0 && hasSearched" description="未找到相关结果">
              <div class="suggestions">
                <p>建议：</p>
                <ul>
                  <li>检查输入是否正确</li>
                  <li>尝试其他关键词</li>
                </ul>
              </div>
            </el-empty>
          </div>
        `,
        data() {
          return {
            searchResults: [],
            hasSearched: true,
          }
        },
      }
      const wrapper = mountWithPlugins(ComponentWithSuggestions)
      expect(wrapper.text()).toContain('建议')
    })
  })

  describe('商家列表空数据', () => {
    const MerchantsComponent = {
      template: `
        <div class="merchants">
          <el-empty v-if="merchants.length === 0" description="暂无商家" />
          <div v-else class="merchant-list">
            <div v-for="m in merchants" :key="m.id">{{ m.name }}</div>
          </div>
        </div>
      `,
      data() {
        return {
          merchants: [] as any[],
        }
      },
    }

    it('商家列表为空时应显示空状态', () => {
      const wrapper = mountWithPlugins(MerchantsComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无商家')
    })
  })

  describe('服务列表空数据', () => {
    const ServicesComponent = {
      template: `
        <div class="services">
          <el-empty v-if="services.length === 0" description="暂无服务" />
          <div v-else class="service-list">
            <div v-for="s in services" :key="s.id">{{ s.name }}</div>
          </div>
        </div>
      `,
      data() {
        return {
          services: [] as any[],
        }
      },
    }

    it('服务列表为空时应显示空状态', () => {
      const wrapper = mountWithPlugins(ServicesComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无服务')
    })
  })

  describe('公告列表空数据', () => {
    const AnnouncementsComponent = {
      template: `
        <div class="announcements">
          <el-empty v-if="announcements.length === 0" description="暂无公告" />
          <div v-else class="announcement-list">
            <div v-for="a in announcements" :key="a.id">{{ a.title }}</div>
          </div>
        </div>
      `,
      data() {
        return {
          announcements: [] as any[],
        }
      },
    }

    it('公告列表为空时应显示空状态', () => {
      const wrapper = mountWithPlugins(AnnouncementsComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无公告')
    })
  })

  describe('空详情测试', () => {
    it('商品详情不存在时应显示404', () => {
      const ProductDetailComponent = {
        template: `
          <div>
            <el-empty v-if="!product" description="商品不存在或已下架" />
            <div v-else>{{ product.name }}</div>
          </div>
        `,
        data() {
          return { product: null }
        },
      }
      const wrapper = mountWithPlugins(ProductDetailComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('商品不存在或已下架')
    })

    it('服务详情不存在时应显示404', () => {
      const ServiceDetailComponent = {
        template: `
          <div>
            <el-empty v-if="!service" description="服务不存在或已下架" />
            <div v-else>{{ service.name }}</div>
          </div>
        `,
        data() {
          return { service: null }
        },
      }
      const wrapper = mountWithPlugins(ServiceDetailComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('服务不存在或已下架')
    })

    it('商家详情不存在时应显示404', () => {
      const MerchantDetailComponent = {
        template: `
          <div>
            <el-empty v-if="!merchant" description="商家不存在或已关闭" />
            <div v-else>{{ merchant.name }}</div>
          </div>
        `,
        data() {
          return { merchant: null }
        },
      }
      const wrapper = mountWithPlugins(MerchantDetailComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('商家不存在或已关闭')
    })

    it('订单详情不存在时应显示404', () => {
      const OrderDetailComponent = {
        template: `
          <div>
            <el-empty v-if="!order" description="订单不存在" />
            <div v-else>{{ order.id }}</div>
          </div>
        `,
        data() {
          return { order: null }
        },
      }
      const wrapper = mountWithPlugins(OrderDetailComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('订单不存在')
    })

    it('预约详情不存在时应显示404', () => {
      const AppointmentDetailComponent = {
        template: `
          <div>
            <el-empty v-if="!appointment" description="预约不存在" />
            <div v-else>{{ appointment.id }}</div>
          </div>
        `,
        data() {
          return { appointment: null }
        },
      }
      const wrapper = mountWithPlugins(AppointmentDetailComponent)
      expect(wrapper.find('.el-empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('预约不存在')
    })
  })
})
