import Mock from 'mockjs'

Mock.mock('/api/stats/appointments/overview', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: {
      total_appointments: 120,
      completed_appointments: 100,
      cancelled_appointments: 20,
      cancellation_rate: 0.1667,
      growth_rate: 0.2
    }
  }
})

Mock.mock(RegExp('/api/stats/appointments/trend\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const period = url.searchParams.get('period') || 'day'
  const days = period === 'day' ? 30 : period === 'week' ? 8 : 12
  const list = []
  for (let i = 0; i < days; i++) {
    list.push({
      date: Mock.Random.date('yyyy-MM-dd'),
      appointments: Mock.Random.integer(1, 10),
      completed: Mock.Random.integer(0, 10),
      cancelled: Mock.Random.integer(0, 3)
    })
  }
  return {
    code: 200,
    message: 'success',
    data: list
  }
})

Mock.mock('/api/stats/appointments/services', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: [
      { name: '洗澡', value: 30 },
      { name: '美容', value: 25 },
      { name: '寄养', value: 20 },
      { name: '医疗', value: 15 },
      { name: '其他', value: 10 }
    ]
  }
})

Mock.mock('/api/stats/appointments/time', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: [
      { hour: '09:00', count: 5 },
      { hour: '10:00', count: 8 },
      { hour: '11:00', count: 12 },
      { hour: '14:00', count: 15 },
      { hour: '15:00', count: 10 },
      { hour: '16:00', count: 7 },
      { hour: '17:00', count: 4 }
    ]
  }
})

Mock.mock('/api/stats/appointments/export', 'get', () => {
  return {
    code: 200,
    message: '导出成功',
    data: {
      url: 'https://example.com/export/appointment-stats.xlsx'
    }
  }
})

Mock.mock('/api/stats/revenue/overview', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: {
      total_revenue: 15000,
      total_orders: 120,
      average_order_value: 125,
      growth_rate: 0.25
    }
  }
})

Mock.mock(RegExp('/api/stats/revenue/trend\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const period = url.searchParams.get('period') || 'day'
  const days = period === 'day' ? 30 : period === 'week' ? 8 : 12
  const list = []
  for (let i = 0; i < days; i++) {
    list.push({
      date: Mock.Random.date('yyyy-MM-dd'),
      service_orders: Mock.Random.integer(1, 10),
      service_revenue: Mock.Random.float(100, 1000, 2, 2),
      product_orders: Mock.Random.integer(1, 10),
      product_revenue: Mock.Random.float(100, 1000, 2, 2)
    })
  }
  return {
    code: 200,
    message: 'success',
    data: list
  }
})

Mock.mock('/api/stats/revenue/composition', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: [
      { name: '服务收入', value: 10000 },
      { name: '商品收入', value: 5000 }
    ]
  }
})

Mock.mock(RegExp('/api/stats/revenue/top\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const type = url.searchParams.get('type') || 'service'
  const list = Mock.mock({
    [`list|10`]: [
      {
        id: '@integer(1, 100)',
        name: '@ctitle(2, 10)',
        quantity: '@integer(1, 50)',
        revenue: '@float(100, 1000, 2, 2)'
      }
    ]
  }).list
  return {
    code: 200,
    message: 'success',
    data: list
  }
})

Mock.mock('/api/stats/revenue/export', 'get', () => {
  return {
    code: 200,
    message: '导出成功',
    data: {
      url: 'https://example.com/export/revenue-stats.xlsx'
    }
  }
})

export function setupStatsMock() {
  console.log('[Mock] 统计模块已加载')
}
