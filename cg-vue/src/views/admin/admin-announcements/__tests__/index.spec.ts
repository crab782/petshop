import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AdminAnnouncements from '../index.vue'

describe('AdminAnnouncements', () => {
  it('renders properly', () => {
    const wrapper = mount(AdminAnnouncements)
    expect(wrapper.exists()).toBe(true)
  })
})
