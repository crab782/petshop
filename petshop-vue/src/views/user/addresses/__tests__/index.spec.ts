import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, createAddress, mockUserStore, mockAddressApi } from '@/tests/utils/userTestUtils'
import Addresses from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/address', () => mockAddressApi)

describe('Addresses', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(Addresses)
    expect(wrapper.exists()).toBe(true)
  })

  it('should display address list', () => {
    const addresses = [
      createAddress({ id: 1, receiverName: '张三', isDefault: true }),
      createAddress({ id: 2, receiverName: '李四', isDefault: false }),
    ]
    const wrapper = mountUserComponent(Addresses, {
      global: {
        mocks: {
          addresses,
        },
      },
    })
    expect(wrapper.text()).toContain('张三') || expect(wrapper.find('.address-item').exists()).toBe(true)
  })

  it('should display empty state when no addresses', () => {
    const wrapper = mountUserComponent(Addresses, {
      global: {
        mocks: {
          addresses: [],
        },
      },
    })
    expect(wrapper.text()).toContain('暂无收货地址') || expect(wrapper.find('.empty-state').exists()).toBe(true)
  })

  it('should display default address badge', () => {
    const addresses = [createAddress({ isDefault: true })]
    const wrapper = mountUserComponent(Addresses, {
      global: {
        mocks: {
          addresses,
        },
      },
    })
    expect(wrapper.text()).toContain('默认') || expect(wrapper.find('.default-badge').exists()).toBe(true)
  })

  it('should have add address button', () => {
    const wrapper = mountUserComponent(Addresses)
    const addButton = wrapper.find('[data-testid="add-address"], .add-button')
    expect(addButton.exists() || wrapper.text().includes('新增')).toBe(true)
  })

  it('should have edit address button', () => {
    const addresses = [createAddress()]
    const wrapper = mountUserComponent(Addresses, {
      global: {
        mocks: {
          addresses,
        },
      },
    })
    const editButton = wrapper.find('[data-testid="edit-address"], .edit-button')
    expect(editButton.exists() || wrapper.text().includes('编辑')).toBe(true)
  })

  it('should have delete address button', () => {
    const addresses = [createAddress()]
    const wrapper = mountUserComponent(Addresses, {
      global: {
        mocks: {
          addresses,
        },
      },
    })
    const deleteButton = wrapper.find('[data-testid="delete-address"], .delete-button')
    expect(deleteButton.exists() || wrapper.text().includes('删除')).toBe(true)
  })

  it('should have set default address functionality', () => {
    const addresses = [createAddress({ isDefault: false })]
    const wrapper = mountUserComponent(Addresses, {
      global: {
        mocks: {
          addresses,
        },
      },
    })
    const defaultButton = wrapper.find('[data-testid="set-default"], .default-button')
    expect(defaultButton.exists() || wrapper.text().includes('设为默认')).toBe(true)
  })
})
