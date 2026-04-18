import Mock from 'mockjs'

Mock.mock('/api/merchant/info', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: {
      id: 1,
      name: '宠物乐园',
      contact_person: '张三',
      phone: '13800138000',
      email: 'merchant@example.com',
      address: '北京市朝阳区建国路88号',
      logo: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20shop%20logo%20colorful%20friendly&image_size=square',
      status: 'approved',
      created_at: '2023-01-01 00:00:00',
      updated_at: '2023-01-01 00:00:00'
    }
  }
})

Mock.mock('/api/merchant/logout', 'post', () => {
  return {
    code: 200,
    message: 'success',
    data: null
  }
})

Mock.mock('/api/merchant/register', 'post', (req: { body: string }) => {
  const { email } = JSON.parse(req.body)
  if (email === 'existing@example.com') {
    return {
      code: 400,
      message: '邮箱已被注册',
      data: null
    }
  }
  return {
    code: 200,
    message: '注册成功，等待审核',
    data: {
      id: Mock.Random.integer(1000, 9999),
      status: 'pending'
    }
  }
})

Mock.mock('/api/merchant/dashboard/stats', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: {
      today_orders: 15,
      pending_appointments: 5,
      today_revenue: 2500.50,
      average_rating: 4.5
    }
  }
})

Mock.mock('/api/merchant/dashboard/recent-orders', 'get', () => {
  const list = Mock.mock({
    'list|5': [
      {
        id: '@integer(1, 1000)',
        user: {
          username: '@cname()'
        },
        service: {
          name: '@ctitle(2, 10)'
        },
        total_price: '@float(10, 1000, 2, 2)',
        appointment_time: '@datetime("yyyy-MM-dd HH:mm:ss")',
        status: '@pick(["pending", "confirmed", "completed", "cancelled"])'
      }
    ]
  }).list
  return {
    code: 200,
    message: 'success',
    data: list
  }
})

Mock.mock('/api/merchant/dashboard/recent-reviews', 'get', () => {
  const list = Mock.mock({
    'list|5': [
      {
        id: '@integer(1, 1000)',
        user: {
          username: '@cname()',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20friendly&image_size=square'
        },
        rating: '@integer(1, 5)',
        comment: '@cparagraph(1, 2)',
        created_at: '@datetime("yyyy-MM-dd HH:mm:ss")'
      }
    ]
  }).list
  return {
    code: 200,
    message: 'success',
    data: list
  }
})

export function setupMerchantMock() {
  console.log('[Mock] 商家模块已加载')
}
