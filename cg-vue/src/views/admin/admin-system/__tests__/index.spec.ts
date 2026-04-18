import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AdminSystem from '../index.vue'

describe('AdminSystem', () => {
  it('renders properly', () => {
    const wrapper = mount(AdminSystem)
    expect(wrapper.exists()).toBe(true)
  })
})
