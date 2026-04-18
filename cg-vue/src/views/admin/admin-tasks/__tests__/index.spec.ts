import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AdminTasks from '../index.vue'

describe('AdminTasks', () => {
  it('renders properly', () => {
    const wrapper = mount(AdminTasks)
    expect(wrapper.exists()).toBe(true)
  })
})
