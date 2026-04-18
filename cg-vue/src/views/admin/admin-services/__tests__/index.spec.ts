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
