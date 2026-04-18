import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AdminActivities from '../index.vue'

describe('AdminActivities', () => {
  it('renders properly', () => {
    const wrapper = mount(AdminActivities)
    expect(wrapper.exists()).toBe(true)
  })
})
