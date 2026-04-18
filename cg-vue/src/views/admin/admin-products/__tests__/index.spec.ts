import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AdminProducts from '../index.vue'

describe('AdminProducts', () => {
  it('renders properly', () => {
    const wrapper = mount(AdminProducts)
    expect(wrapper.exists()).toBe(true)
  })
})
