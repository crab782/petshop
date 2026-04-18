import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import UserDetail from '../index.vue'

describe('UserDetail', () => {
  it('renders properly', () => {
    const wrapper = mount(UserDetail)
    expect(wrapper.exists()).toBe(true)
  })
})
