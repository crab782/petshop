import Mock from 'mockjs'

Mock.mock('/api/shop/info', 'get', () => {
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
      created_at: '2023-01-01 00:00:00'
    }
  }
})

Mock.mock('/api/shop/info', 'put', () => {
  return {
    code: 200,
    message: '保存成功',
    data: null
  }
})

Mock.mock('/api/shop/settings', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: {
      business_status: 'open',
      notifications: {
        appointment_reminder: true,
        order_reminder: true,
        review_reminder: true,
        notification_method: ['sms', 'email']
      }
    }
  }
})

Mock.mock('/api/shop/settings', 'put', () => {
  return {
    code: 200,
    message: '更新成功',
    data: null
  }
})

Mock.mock('/api/shop/password', 'put', () => {
  return {
    code: 200,
    message: '密码修改成功',
    data: null
  }
})

Mock.mock('/api/shop/export', 'get', () => {
  return {
    code: 200,
    message: '导出成功',
    data: {
      url: 'https://example.com/export/shop-data.xlsx'
    }
  }
})

export function setupShopMock() {
  console.log('[Mock] 店铺模块已加载')
}
