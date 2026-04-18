import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AnnouncementEdit from '../index.vue'

describe('AnnouncementEdit', () => {
  it('renders properly', () => {
    const wrapper = mount(AnnouncementEdit)
    expect(wrapper.exists()).toBe(true)
  })
})
