import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import Roles from '../index.vue'

describe('Roles', () => {
  it('renders properly', () => {
    const wrapper = mount(Roles)
    expect(wrapper.exists()).toBe(true)
  })
})
