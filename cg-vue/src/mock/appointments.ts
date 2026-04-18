import Mock from 'mockjs'

Mock.mock(RegExp('/api/appointments\\?.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page') || '1')
  const size = parseInt(url.searchParams.get('size') || '10')
  const id = url.searchParams.get('id')
  const username = url.searchParams.get('username')
  const startDate = url.searchParams.get('startDate')
  const endDate = url.searchParams.get('endDate')
  const status = url.searchParams.get('status')

  const total = 50
  const list = Mock.mock({
    [`list|${size}`]: [
      {
        id: '@integer(1, 1000)',
        user_id: '@integer(1, 100)',
        merchant_id: 1,
        service_id: '@integer(1, 100)',
        pet_id: '@integer(1, 100)',
        appointment_time: '@datetime("yyyy-MM-dd HH:mm:ss")',
        status: '@pick(["pending", "confirmed", "completed", "cancelled"])'
      }
    ]
  }).list

  let filteredList = list
  if (id) {
    filteredList = filteredList.filter((item: { id: number }) => item.id.toString().includes(id))
  }
  if (username) {
    filteredList = filteredList.filter(() => Math.random() > 0.5)
  }
  if (startDate) {
    filteredList = filteredList.filter((item: { appointment_time: string }) => item.appointment_time >= startDate)
  }
  if (endDate) {
    filteredList = filteredList.filter((item: { appointment_time: string }) => item.appointment_time <= endDate)
  }
  if (status) {
    filteredList = filteredList.filter((item: { status: string }) => item.status === status)
  }

  const resultList = filteredList.map((item: { id: number; user_id: number; merchant_id: number; service_id: number; pet_id: number; appointment_time: string; status: string }) => ({
    ...item,
    user: {
      username: Mock.Random.cname()
    },
    pet: {
      name: Mock.Random.ctitle(2, 5)
    },
    service: {
      name: Mock.Random.ctitle(2, 10)
    },
    total_price: Mock.Random.float(10, 1000, 2, 2),
    notes: Mock.Random.cparagraph(1, 2)
  }))

  return {
    code: 200,
    message: 'success',
    data: {
      total,
      page,
      size,
      list: resultList
    }
  }
})

Mock.mock(RegExp('/api/appointments/\\d+/status'), 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/appointments\/(\d+)/)?.[1] || '0')
  return {
    code: 200,
    message: '状态更新成功',
    data: {
      id
    }
  }
})

Mock.mock('/api/appointments/export', 'get', () => {
  return {
    code: 200,
    message: '导出成功',
    data: {
      url: 'https://example.com/export/appointments.xlsx'
    }
  }
})

export function setupAppointmentsMock() {
  console.log('[Mock] 预约模块已加载')
}
