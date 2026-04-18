import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ProductManage from '../index.vue'

describe('ProductManage', () => {
  it('renders properly', () => {
    const wrapper = mount(ProductManage)
    expect(wrapper.exists()).toBe(true)
  })
})
