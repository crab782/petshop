import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import MerchantDetail from '../index.vue'

describe('MerchantDetail', () => {
  it('renders properly', () => {
    const wrapper = mount(MerchantDetail)
    expect(wrapper.exists()).toBe(true)
  })
})
