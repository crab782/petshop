import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ShopAudit from '../index.vue'

describe('ShopAudit', () => {
  it('renders properly', () => {
    const wrapper = mount(ShopAudit)
    expect(wrapper.exists()).toBe(true)
  })
})
