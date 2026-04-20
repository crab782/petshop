import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import * as adminApi from '@/api/admin'
import AdminServices from '../index.vue'

// 模拟路由
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/admin/service-edit',
      name: 'ServiceEdit',
      component: {}
    }
  ]
})

// 模拟服务数据
const mockServices = [
  {
    id: 1,
    name: '服务1',
    merchantId: 1,
    merchantName: '商家1',
    price: 100,
    duration: 60,
    category: '类别1',
    status: 'enabled',
    createdAt: '2023-01-01T00:00:00Z'
  },
  {
    id: 2,
    name: '服务2',
    merchantId: 2,
    merchantName: '商家2',
    price: 200,
    duration: 120,
    category: '类别2',
    status: 'disabled',
    createdAt: '2023-01-02T00:00:00Z'
  }
]

// 模拟商家数据
const mockMerchants = [
  { id: 1, name: '商家1' },
  { id: 2, name: '商家2' }
]

// 模拟 API 调用
vi.mock('@/api/admin', () => {
  return {
    getAllServices: vi.fn(() => Promise.resolve(mockServices)),
    deleteService: vi.fn(() => Promise.resolve({})),
    updateServiceStatus: vi.fn(() => Promise.resolve({})),
    batchUpdateServiceStatus: vi.fn(() => Promise.resolve({})),
    batchDeleteServices: vi.fn(() => Promise.resolve({})),
    getAllMerchants: vi.fn(() => Promise.resolve(mockMerchants))
  }
})

// 模拟 Element Plus 组件
vi.mock('element-plus', () => {
  return {
    ElMessage: {
      success: vi.fn(),
      error: vi.fn(),
      warning: vi.fn()
    },
    ElMessageBox: {
      confirm: vi.fn(() => Promise.resolve({}))
    },
    ElIcon: {
      Search: vi.fn(),
      Refresh: vi.fn(),
      Check: vi.fn(),
      Close: vi.fn(),
      Edit: vi.fn(),
      Delete: vi.fn()
    }
  }
})

describe('AdminServices 组件', () => {
  let wrapper: any

  beforeEach(() => {
    // 挂载组件
    wrapper = mount(AdminServices, {
      global: {
        plugins: [router],
        stubs: {
          ElIcon: true,
          ElButton: true,
          ElInput: true,
          ElSelect: true,
          ElOption: true,
          ElTable: true,
          ElTableColumn: true,
          ElPagination: true,
          ElCard: true,
          ElForm: true,
          ElFormItem: true,
          ElSwitch: true,
          ElTag: true
        }
      }
    })
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  // 数据渲染测试
  describe('数据渲染测试', () => {
    it('测试服务表格渲染', async () => {
      await wrapper.vm.$nextTick()
      
      // 检查表格是否存在
      expect(wrapper.find('.table-card').exists()).toBe(true)
    })

    it('测试搜索和筛选表单渲染', () => {
      // 检查搜索表单是否存在
      expect(wrapper.find('.filter-card').exists()).toBe(true)
    })

    it('测试批量操作按钮渲染', async () => {
      // 模拟选择服务
      await wrapper.vm.$nextTick()
      await wrapper.vm.handleSelectionChange(mockServices)
      await wrapper.vm.$nextTick()
      
      // 检查批量操作卡片是否显示
      expect(wrapper.find('.batch-actions').exists()).toBe(true)
    })
  })

  // API 集成测试
  describe('API 集成测试', () => {
    it('模拟服务删除 API 响应', async () => {
      await wrapper.vm.$nextTick()
      
      // 调用删除方法
      await wrapper.vm.handleDelete(mockServices[0])
      
      // 检查 API 是否被调用
      expect(adminApi.deleteService).toHaveBeenCalledWith(mockServices[0].id)
    })

    it('模拟批量操作 API 响应', async () => {
      await wrapper.vm.$nextTick()
      
      // 选择服务
      await wrapper.vm.handleSelectionChange(mockServices)
      
      // 调用批量启用
      await wrapper.vm.handleBatchEnable()
      
      // 检查 API 是否被调用
      expect(adminApi.batchUpdateServiceStatus).toHaveBeenCalledWith(
        [1, 2],
        'enabled'
      )
    })
  })

  // 交互功能测试
  describe('交互功能测试', () => {
    it('测试批量操作功能', async () => {
      await wrapper.vm.$nextTick()
      
      // 选择服务
      await wrapper.vm.handleSelectionChange(mockServices)
      
      // 测试批量启用
      await wrapper.vm.handleBatchEnable()
      expect(adminApi.batchUpdateServiceStatus).toHaveBeenCalledWith(
        [1, 2],
        'enabled'
      )
      
      // 测试批量禁用
      await wrapper.vm.handleBatchDisable()
      expect(adminApi.batchUpdateServiceStatus).toHaveBeenCalledWith(
        [1, 2],
        'disabled'
      )
      
      // 测试批量删除
      await wrapper.vm.handleBatchDelete()
      expect(adminApi.batchDeleteServices).toHaveBeenCalledWith([1, 2])
    })

    it('测试单个服务操作', async () => {
      await wrapper.vm.$nextTick()
      
      // 测试删除操作
      await wrapper.vm.handleDelete(mockServices[0])
      expect(adminApi.deleteService).toHaveBeenCalledWith(mockServices[0].id)
      
      // 测试状态变更
      await wrapper.vm.handleStatusChange(mockServices[0], false)
      expect(adminApi.updateServiceStatus).toHaveBeenCalledWith(
        mockServices[0].id,
        'disabled'
      )
    })
  })

  // 边界情况测试
  describe('边界情况测试', () => {
    it('测试数据加载失败状态', async () => {
      // 重新模拟 API 失败
      vi.mock('@/api/admin', () => {
        return {
          getAllServices: vi.fn(() => Promise.reject(new Error('加载失败'))),
          deleteService: vi.fn(() => Promise.resolve({})),
          updateServiceStatus: vi.fn(() => Promise.resolve({})),
          batchUpdateServiceStatus: vi.fn(() => Promise.resolve({})),
          batchDeleteServices: vi.fn(() => Promise.resolve({})),
          getAllMerchants: vi.fn(() => Promise.resolve(mockMerchants))
        }
      })
      
      // 调用加载方法
      await wrapper.vm.loadServices()
      
      // 检查加载状态
      expect(wrapper.vm.loading).toBe(false)
    })

    it('测试空数据状态', async () => {
      // 重新模拟空数据
      vi.mock('@/api/admin', () => {
        return {
          getAllServices: vi.fn(() => Promise.resolve([])),
          deleteService: vi.fn(() => Promise.resolve({})),
          updateServiceStatus: vi.fn(() => Promise.resolve({})),
          batchUpdateServiceStatus: vi.fn(() => Promise.resolve({})),
          batchDeleteServices: vi.fn(() => Promise.resolve({})),
          getAllMerchants: vi.fn(() => Promise.resolve(mockMerchants))
        }
      })
      
      // 调用加载方法
      await wrapper.vm.loadServices()
      await wrapper.vm.$nextTick()
      
      // 检查空数据状态
      expect(wrapper.vm.serviceList.length).toBe(0)
      expect(wrapper.vm.filteredList.length).toBe(0)
      expect(wrapper.vm.pagination.total).toBe(0)
    })
  })
})
