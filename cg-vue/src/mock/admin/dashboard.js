import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString, randomPhone } from '../utils/random'

Mock.mock('/api/admin/dashboard/stats', 'get', () => ({
  code: 200,
  message: 'success',
  data: {
    totalUsers: 2580,
    totalMerchants: 128,
    todayOrders: 156,
    monthlyRevenue: 156800
  }
}))

Mock.mock('/api/admin/dashboard/recent-users', 'get', () => {
  const users = []
  for (let i = 0; i < 10; i++) {
    users.push({
      id: randomId(),
      username: randomString(6),
      phone: randomPhone(),
      email: randomString(8) + '@mail.com',
      createdAt: randomDate('2024-01-01')
    })
  }
  return {
    code: 200,
    message: 'success',
    data: users
  }
})

Mock.mock('/api/admin/dashboard/pending-merchants', 'get', () => {
  const merchants = []
  for (let i = 0; i < 5; i++) {
    merchants.push({
      id: randomId(),
      name: randomString(8) + '宠物店',
      contactPerson: randomString(4) + '先生',
      phone: randomPhone(),
      address: randomString(10) + '路',
      createdAt: randomDate('2024-01-01')
    })
  }
  return {
    code: 200,
    message: 'success',
    data: merchants
  }
})

Mock.mock('/api/admin/dashboard/announcements', 'get', () => {
  const announcements = []
  for (let i = 0; i < 5; i++) {
    announcements.push({
      id: randomId(),
      title: randomString(10) + '公告',
      content: randomString(30),
      author: randomString(4),
      publishedAt: randomDate('2024-01-01')
    })
  }
  return {
    code: 200,
    message: 'success',
    data: announcements
  }
})

Mock.mock('/api/admin/system/settings', 'get', () => ({
  code: 200,
  message: 'success',
  data: {
    basic: {
      siteName: '宠物家园',
      siteDescription: '专业的宠物服务平台',
      contactEmail: 'contact@pet.com',
      contactPhone: '400-123-4567'
    },
    email: {
      enabled: true,
      smtpHost: 'smtp.example.com',
      smtpPort: 465,
      encryption: 'SSL',
      username: 'noreply@example.com'
    },
    sms: {
      enabled: false,
      provider: 'aliyun',
      accessKeyId: '',
      accessKeySecret: '',
      signName: '宠物家园'
    },
    payment: {
      alipay: {
        enabled: true,
        appId: '2021xxxxx',
        privateKey: ''
      },
      wechat: {
        enabled: true,
        mchId: '1xxxxxxx',
        apiKey: ''
      }
    }
  }
}))

Mock.mock('/api/admin/system/settings', 'put', () => ({
  code: 200,
  message: '保存成功',
  data: { success: true }
}))