import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mountUserComponent, mockUserStore } from '@/tests/utils/userTestUtils'
import PayPage from '../index.vue'

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore(),
}))

vi.mock('@/api/user', () => ({
  payOrder: vi.fn(() => Promise.resolve({ data: { success: true } })),
  getOrderById: vi.fn(() => Promise.resolve({ 
    data: { 
      id: 1, 
      orderNo: 'ORD001', 
      totalPrice: 100, 
      freight: 10,
      status: 'pending',
      createTime: new Date().toISOString(),
      address: { name: '张三', phone: '13800138000', address: '测试地址' },
      items: []
    } 
  })),
}))

describe('PayPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mountUserComponent(PayPage)
    expect(wrapper.exists()).toBe(true)
  })

  it('should have loading state', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(vm.loading).toBeDefined()
  })

  it('should have paying state', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(vm.paying).toBeDefined()
  })

  it('should have order state', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(vm.order).toBeDefined()
  })

  it('should have payMethod state', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(vm.payMethod).toBeDefined()
  })

  it('should have payStatus state', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(vm.payStatus).toBeDefined()
  })

  it('should have showQRCode state', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(vm.showQRCode).toBeDefined()
  })

  it('should have countdown state', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(vm.countdown).toBeDefined()
  })

  it('should have payMethodOptions defined', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(vm.payMethodOptions).toBeDefined()
    expect(vm.payMethodOptions.length).toBeGreaterThan(0)
  })

  it('should have fetchOrderInfo method', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(typeof vm.fetchOrderInfo).toBe('function')
  })

  it('should have handlePay method', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(typeof vm.handlePay).toBe('function')
  })

  it('should have handleCancelPay method', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(typeof vm.handleCancelPay).toBe('function')
  })

  it('should have handleRetry method', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(typeof vm.handleRetry).toBe('function')
  })

  it('should have handleBack method', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(typeof vm.handleBack).toBe('function')
  })

  it('should have generateQRCodeURL method', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(typeof vm.generateQRCodeURL).toBe('function')
  })

  it('should have currentStatus computed', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(vm.currentStatus).toBeDefined()
  })

  it('should have formatCountdown computed', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(vm.formatCountdown).toBeDefined()
  })

  it('should have isExpired computed', () => {
    const wrapper = mountUserComponent(PayPage)
    const vm = wrapper.vm as any
    expect(vm.isExpired).toBeDefined()
  })
})
