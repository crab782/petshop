import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ReviewAudit from '../index.vue'

describe('ReviewAudit', () => {
  it('renders properly', () => {
    const wrapper = mount(ReviewAudit)
    expect(wrapper.exists()).toBe(true)
  })
})
