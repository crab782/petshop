import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString, randomPhone } from '../utils/random'

Mock.mock('/api/admin/dashboard', 'get', () => ({
  code: 200,
  message: 'success',
  data: {
    userCount: Mock.Random.integer(2000, 3000),
    merchantCount: Mock.Random.integer(100, 200),
    orderCount: Mock.Random.integer(100, 300),
    serviceCount: Mock.Random.integer(50, 150)
  }
}))

Mock.mock('/api/admin/dashboard/recent-users', 'get', () => {
  const users = []
  for (let i = 0; i < 10; i++) {
    users.push({
      id: randomId(),
      username: Mock.Random.cname(),
      phone: randomPhone(),
      email: Mock.Random.email(),
      avatar: Mock.Random.image('100x100', Mock.Random.color()),
      registerTime: randomDate('2024-01-01')
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
      name: Mock.Random.cword(4, 8) + '宠物店',
      contact: Mock.Random.cname(),
      phone: randomPhone(),
      email: Mock.Random.email(),
      address: Mock.Random.county(true),
      registerTime: randomDate('2024-01-01')
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
      title: Mock.Random.ctitle(5, 15),
      content: Mock.Random.cparagraph(1, 3),
      publishTime: randomDate('2024-01-01'),
      publisher: Mock.Random.cname()
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