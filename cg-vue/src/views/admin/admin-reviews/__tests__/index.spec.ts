import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AdminReviews from '../index.vue'

describe('AdminReviews', () => {
  it('renders properly', () => {
    const wrapper = mount(AdminReviews)
    expect(wrapper.exists()).toBe(true)
  })
})
