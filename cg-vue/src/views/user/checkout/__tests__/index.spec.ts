import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, mockUserStore } from '@/tests/utils/userTestUtils'
import CheckoutPage from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/user', () => ({
  getAddresses: vi.fn(() => Promise.resolve({ data: [] })),
  purchaseProduct: vi.fn(() => Promise.resolve({ data: { id: 1 } })),
}))

describe('CheckoutPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    expect(wrapper.exists()).toBe(true)
  })

  it('should have loading state', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(vm.loading).toBeDefined()
  })

  it('should have submitting state', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(vm.submitting).toBeDefined()
  })

  it('should have addresses state', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(vm.addresses).toBeDefined()
  })

  it('should have selectedAddressId state', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(vm.selectedAddressId).toBeDefined()
  })

  it('should have remark state', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(vm.remark).toBeDefined()
  })

  it('should have selectedPaymentMethod state', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(vm.selectedPaymentMethod).toBeDefined()
  })

  it('should have cartItems state', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(vm.cartItems).toBeDefined()
  })

  it('should have paymentMethods defined', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(vm.paymentMethods).toBeDefined()
    expect(vm.paymentMethods.length).toBeGreaterThan(0)
  })

  it('should have fetchAddresses method', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(typeof vm.fetchAddresses).toBe('function')
  })

  it('should have handleAddAddress method', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(typeof vm.handleAddAddress).toBe('function')
  })

  it('should have handleSubmitOrder method', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(typeof vm.handleSubmitOrder).toBe('function')
  })

  it('should have productTotal computed', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(vm.productTotal).toBeDefined()
  })

  it('should have shippingFee computed', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(vm.shippingFee).toBeDefined()
  })

  it('should have orderTotal computed', () => {
    const wrapper = mountUserComponent(CheckoutPage)
    const vm = wrapper.vm as any
    expect(vm.orderTotal).toBeDefined()
  })
})
