import Mock from 'mockjs'

// 商家端API Mock实现

// 通用响应结构
const createResponse = (data: any, code: number = 200, message: string = 'success') => {
  return {
    code,
    message,
    data
  }
}

// 商家信息
Mock.mock('/api/merchant/info', 'get', () => {
  return createResponse({
    id: 1,
    name: '宠物乐园',
    contact_person: '张三',
    phone: '13800138000',
    email: 'merchant@example.com',
    address: '北京市朝阳区建国路88号',
    logo: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20shop%20logo%20colorful%20friendly&image_size=square',
    description: '专业的宠物服务机构，提供美容、寄养、训练等服务',
    status: 'approved',
    created_at: '2023-01-01 00:00:00'
  })
})

Mock.mock('/api/merchant/info', 'put', () => {
  return createResponse({
    id: 1,
    name: '宠物乐园',
    contact_person: '张三',
    phone: '13800138000',
    email: 'merchant@example.com',
    address: '北京市朝阳区建国路88号',
    logo: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20shop%20logo%20colorful%20friendly&image_size=square',
    description: '专业的宠物服务机构，提供美容、寄养、训练等服务',
    status: 'approved',
    created_at: '2023-01-01 00:00:00'
  })
})

// 服务管理
Mock.mock('/api/merchant/services', 'get', () => {
  return createResponse(Mock.mock({
    'list|10': [
      {
        id: '@integer(1, 1000)',
        name: '@ctitle(2, 10)',
        description: '@cparagraph(1, 3)',
        price: '@float(10, 1000, 2, 2)',
        duration: '@integer(30, 180)',
        category: '@ctitle(2, 5)',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20service%20professional&image_size=square',
        status: '@pick(["enabled", "disabled"])',
        createdAt: '@datetime("yyyy-MM-dd HH:mm:ss")'
      }
    ]
  }).list)
})

Mock.mock('/api/merchant/services', 'post', () => {
  return createResponse({
    id: Mock.Random.integer(1000, 9999),
    name: '新服务',
    description: '服务描述',
    price: 100.00,
    duration: 60,
    category: '美容',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20service%20professional&image_size=square',
    status: 'enabled',
    createdAt: new Date().toISOString()
  })
})

Mock.mock(/\/api\/merchant\/services\/\d+$/, 'get', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/services\/(\d+)/)?.[1] || '0')
  return createResponse({
    id,
    name: Mock.Random.ctitle(2, 10),
    description: Mock.Random.cparagraph(1, 3),
    price: Mock.Random.float(10, 1000, 2, 2),
    duration: Mock.Random.integer(30, 180),
    category: Mock.Random.ctitle(2, 5),
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20service%20professional&image_size=square',
    status: 'enabled',
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
  })
})

Mock.mock(/\/api\/merchant\/services\/\d+$/, 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/services\/(\d+)/)?.[1] || '0')
  return createResponse({
    id,
    name: Mock.Random.ctitle(2, 10),
    description: Mock.Random.cparagraph(1, 3),
    price: Mock.Random.float(10, 1000, 2, 2),
    duration: Mock.Random.integer(30, 180),
    category: Mock.Random.ctitle(2, 5),
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20service%20professional&image_size=square',
    status: 'enabled',
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
  })
})

Mock.mock(/\/api\/merchant\/services\/\d+$/, 'delete', () => {
  return createResponse(null)
})

Mock.mock('/api/merchant/services/batch/status', 'put', () => {
  return createResponse(Mock.mock({
    'list|3': [
      {
        id: '@integer(1, 1000)',
        status: 'enabled'
      }
    ]
  }).list)
})

Mock.mock('/api/merchant/services/batch', 'delete', () => {
  return createResponse(null)
})

// 商品管理
Mock.mock('/api/merchant/products', 'get', () => {
  return createResponse(Mock.mock({
    'list|10': [
      {
        id: '@integer(1, 1000)',
        name: '@ctitle(2, 10)',
        description: '@cparagraph(1, 3)',
        price: '@float(10, 500, 2, 2)',
        stock: '@integer(0, 100)',
        lowStockThreshold: 10,
        merchantId: 1,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20product%20cute&image_size=square',
        category: '@ctitle(2, 5)',
        status: '@pick(["enabled", "disabled"])',
        createdAt: '@datetime("yyyy-MM-dd HH:mm:ss")',
        updatedAt: '@datetime("yyyy-MM-dd HH:mm:ss")'
      }
    ]
  }).list)
})

Mock.mock('/api/merchant/products/paged', 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page') || '1')
  const pageSize = parseInt(url.searchParams.get('pageSize') || '10')
  
  return createResponse({
    list: Mock.mock({
      [`list|${pageSize}`]: [
        {
          id: '@integer(1, 1000)',
          name: '@ctitle(2, 10)',
          description: '@cparagraph(1, 3)',
          price: '@float(10, 500, 2, 2)',
          stock: '@integer(0, 100)',
          lowStockThreshold: 10,
          merchantId: 1,
          image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20product%20cute&image_size=square',
          category: '@ctitle(2, 5)',
          status: '@pick(["enabled", "disabled"])',
          createdAt: '@datetime("yyyy-MM-dd HH:mm:ss")',
          updatedAt: '@datetime("yyyy-MM-dd HH:mm:ss")'
        }
      ]
    }).list,
    total: 50,
    page,
    pageSize
  })
})

Mock.mock('/api/merchant/products', 'post', () => {
  return createResponse({
    id: Mock.Random.integer(1000, 9999),
    name: '新商品',
    description: '商品描述',
    price: 50.00,
    stock: 50,
    lowStockThreshold: 10,
    merchantId: 1,
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20product%20cute&image_size=square',
    category: '食品',
    status: 'enabled',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  })
})

Mock.mock(/\/api\/merchant\/products\/\d+$/, 'get', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/products\/(\d+)/)?.[1] || '0')
  return createResponse({
    id,
    name: Mock.Random.ctitle(2, 10),
    description: Mock.Random.cparagraph(1, 3),
    price: Mock.Random.float(10, 500, 2, 2),
    stock: Mock.Random.integer(0, 100),
    lowStockThreshold: 10,
    merchantId: 1,
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20product%20cute&image_size=square',
    category: Mock.Random.ctitle(2, 5),
    status: 'enabled',
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    updatedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
  })
})

Mock.mock(/\/api\/merchant\/products\/\d+$/, 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/products\/(\d+)/)?.[1] || '0')
  return createResponse({
    id,
    name: Mock.Random.ctitle(2, 10),
    description: Mock.Random.cparagraph(1, 3),
    price: Mock.Random.float(10, 500, 2, 2),
    stock: Mock.Random.integer(0, 100),
    lowStockThreshold: 10,
    merchantId: 1,
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20product%20cute&image_size=square',
    category: Mock.Random.ctitle(2, 5),
    status: 'enabled',
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    updatedAt: new Date().toISOString()
  })
})

Mock.mock(/\/api\/merchant\/products\/\d+$/, 'delete', () => {
  return createResponse(null)
})

Mock.mock(/\/api\/merchant\/products\/\d+\/status/, 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/products\/(\d+)\/status/)?.[1] || '0')
  return createResponse({
    id,
    status: 'enabled'
  })
})

Mock.mock('/api/merchant/products/batch/status', 'put', () => {
  return createResponse(Mock.mock({
    'list|3': [
      {
        id: '@integer(1, 1000)',
        status: 'enabled'
      }
    ]
  }).list)
})

Mock.mock('/api/merchant/products/batch', 'delete', () => {
  return createResponse(null)
})

// 订单管理
Mock.mock('/api/merchant/orders', 'get', () => {
  return createResponse(Mock.mock({
    'list|10': [
      {
        id: '@integer(1, 1000)',
        userId: '@integer(1, 1000)',
        userName: '@cname()',
        serviceId: '@integer(1, 1000)',
        serviceName: '@ctitle(2, 10)',
        merchantId: 1,
        appointmentTime: '@datetime("yyyy-MM-dd HH:mm:ss")',
        status: '@pick(["pending", "confirmed", "completed", "cancelled"])',
        remark: '@cparagraph(1, 2)',
        totalPrice: '@float(10, 1000, 2, 2)'
      }
    ]
  }).list)
})

Mock.mock(/\/api\/merchant\/orders\/\d+\/status/, 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/orders\/(\d+)\/status/)?.[1] || '0')
  return createResponse({
    id,
    status: 'completed'
  })
})

// 商品订单
Mock.mock('/api/merchant/product-orders', 'get', () => {
  return createResponse(Mock.mock({
    'list|10': [
      {
        id: '@integer(1, 1000)',
        userId: '@integer(1, 1000)',
        userName: '@cname()',
        productId: '@integer(1, 1000)',
        productName: '@ctitle(2, 10)',
        quantity: '@integer(1, 10)',
        totalPrice: '@float(10, 1000, 2, 2)',
        status: '@pick(["pending", "paid", "shipped", "completed", "cancelled"])',
        address: '@caddress()',
        phone: '@phone()',
        receiverName: '@cname()',
        createTime: '@datetime("yyyy-MM-dd HH:mm:ss")',
        trackingNumber: '@string("upper", 12)',
        logisticsCompany: '@ctitle(2, 5)'
      }
    ]
  }).list)
})

Mock.mock(/\/api\/merchant\/product-orders\/\d+\/status/, 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/product-orders\/(\d+)\/status/)?.[1] || '0')
  return createResponse({
    id,
    status: 'shipped'
  })
})

Mock.mock(/\/api\/merchant\/product-orders\/\d+\/logistics/, 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/product-orders\/(\d+)\/logistics/)?.[1] || '0')
  return createResponse({
    id,
    logisticsCompany: '顺丰速运',
    trackingNumber: Mock.Random.string('upper', 12)
  })
})

// 预约管理
Mock.mock('/api/merchant/appointments', 'get', () => {
  return createResponse(Mock.mock({
    'list|10': [
      {
        id: '@integer(1, 1000)',
        userId: '@integer(1, 1000)',
        userName: '@cname()',
        serviceId: '@integer(1, 1000)',
        serviceName: '@ctitle(2, 10)',
        merchantId: 1,
        petName: '@cname()',
        appointmentTime: '@datetime("yyyy-MM-dd HH:mm:ss")',
        status: '@pick(["pending", "confirmed", "completed", "cancelled"])'
      }
    ]
  }).list)
})

Mock.mock(/\/api\/merchant\/appointments\/\d+\/status/, 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/appointments\/(\d+)\/status/)?.[1] || '0')
  return createResponse({
    id,
    status: 'confirmed'
  })
})

// 评价管理
Mock.mock('/api/merchant/reviews', 'get', (req: { url: string }) => {
  return createResponse({
    list: Mock.mock({
      'list|10': [
        {
          id: '@integer(1, 1000)',
          userId: '@integer(1, 1000)',
          userName: '@cname()',
          userAvatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20friendly&image_size=square',
          serviceId: '@integer(1, 1000)',
          serviceName: '@ctitle(2, 10)',
          orderId: '@integer(1, 1000)',
          rating: '@integer(1, 5)',
          content: '@cparagraph(1, 3)',
          reviewTime: '@datetime("yyyy-MM-dd HH:mm:ss")',
          replyStatus: '@pick(["replied", "pending"])',
          replyContent: '@cparagraph(1, 2)',
          replyTime: '@datetime("yyyy-MM-dd HH:mm:ss")'
        }
      ]
    }).list,
    total: 50,
    page: 1,
    pageSize: 10,
    ratingDistribution: {
      five: 30,
      four: 15,
      three: 3,
      two: 1,
      one: 1
    }
  })
})

Mock.mock(/\/api\/merchant\/reviews\/\d+\/reply/, 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/reviews\/(\d+)\/reply/)?.[1] || '0')
  return createResponse({
    id,
    replyStatus: 'replied',
    replyContent: '感谢您的评价！',
    replyTime: new Date().toISOString()
  })
})

Mock.mock(/\/api\/merchant\/reviews\/\d+$/, 'delete', () => {
  return createResponse(null)
})

Mock.mock('/api/merchant/reviews/recent', 'get', () => {
  return createResponse(Mock.mock({
    'list|5': [
      {
        id: '@integer(1, 1000)',
        userName: '@cname()',
        userAvatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20friendly&image_size=square',
        rating: '@integer(1, 5)',
        content: '@cparagraph(1, 2)',
        serviceName: '@ctitle(2, 10)',
        reviewTime: '@datetime("yyyy-MM-dd HH:mm:ss")'
      }
    ]
  }).list)
})

// 分类管理
Mock.mock('/api/merchant/categories', 'get', () => {
  return createResponse(Mock.mock({
    'list|10': [
      {
        id: '@integer(1, 1000)',
        name: '@ctitle(2, 5)',
        icon: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=category%20icon%20simple&image_size=square',
        description: '@cparagraph(1, 2)',
        sort: '@integer(1, 100)',
        status: '@pick(["enabled", "disabled"])'
      }
    ]
  }).list)
})

Mock.mock('/api/merchant/categories', 'post', () => {
  return createResponse({
    id: Mock.Random.integer(1000, 9999),
    name: '新分类',
    icon: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=category%20icon%20simple&image_size=square',
    description: '分类描述',
    sort: 1,
    status: 'enabled'
  })
})

Mock.mock(/\/api\/merchant\/categories\/\d+$/, 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/categories\/(\d+)/)?.[1] || '0')
  return createResponse({
    id,
    name: Mock.Random.ctitle(2, 5),
    icon: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=category%20icon%20simple&image_size=square',
    description: Mock.Random.cparagraph(1, 2),
    sort: Mock.Random.integer(1, 100),
    status: 'enabled'
  })
})

Mock.mock(/\/api\/merchant\/categories\/\d+$/, 'delete', () => {
  return createResponse(null)
})

Mock.mock(/\/api\/merchant\/categories\/\d+\/status/, 'put', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/merchant\/categories\/(\d+)\/status/)?.[1] || '0')
  return createResponse({
    id,
    status: 'enabled'
  })
})

Mock.mock('/api/merchant/categories/batch/status', 'put', () => {
  return createResponse(Mock.mock({
    'list|3': [
      {
        id: '@integer(1, 1000)',
        status: 'enabled'
      }
    ]
  }).list)
})

Mock.mock('/api/merchant/categories/batch', 'delete', () => {
  return createResponse(null)
})

// 统计分析
Mock.mock('/api/merchant/dashboard', 'get', () => {
  return createResponse({
    todayOrders: 15,
    pendingAppointments: 5,
    todayRevenue: 2500.50,
    avgRating: 4.5,
    orderGrowth: 12.5,
    revenueGrowth: 8.2,
    ratingCount: 120
  })
})

Mock.mock('/api/merchant/appointments/recent', 'get', () => {
  return createResponse(Mock.mock({
    'list|5': [
      {
        id: '@integer(1, 1000)',
        customerName: '@cname()',
        serviceName: '@ctitle(2, 10)',
        status: '@pick(["pending", "confirmed", "completed", "cancelled"])'
      }
    ]
  }).list)
})

Mock.mock('/api/merchant/revenue-stats', 'get', () => {
  return createResponse({
    totalRevenue: 50000.00,
    orderCount: 200,
    avgOrderValue: 250.00,
    lastPeriodRevenue: 45000.00,
    growthRate: 11.1,
    serviceRevenue: 30000.00,
    productRevenue: 20000.00,
    serviceOrderCount: 120,
    productOrderCount: 80,
    orderList: Mock.mock({
      'list|7': [
        {
          id: '@integer(1, 1000)',
          date: '@date("yyyy-MM-dd")',
          serviceAmount: '@float(1000, 5000, 2, 2)',
          productAmount: '@float(500, 2000, 2, 2)',
          totalAmount: '@float(1500, 7000, 2, 2)'
        }
      ]
    }).list,
    topServices: Mock.mock({
      'list|5': [
        {
          id: '@integer(1, 1000)',
          name: '@ctitle(2, 10)',
          orderCount: '@integer(10, 50)',
          revenue: '@float(1000, 10000, 2, 2)'
        }
      ]
    }).list,
    topProducts: Mock.mock({
      'list|5': [
        {
          id: '@integer(1, 1000)',
          name: '@ctitle(2, 10)',
          orderCount: '@integer(10, 50)',
          revenue: '@float(1000, 10000, 2, 2)'
        }
      ]
    }).list
  })
})

Mock.mock('/api/merchant/appointment-stats', 'get', () => {
  return createResponse({
    totalCount: 100,
    pendingCount: 10,
    completedCount: 80,
    cancelledCount: 10,
    growthRate: 15.5,
    trendData: Mock.mock({
      'list|7': [
        {
          date: '@date("yyyy-MM-dd")',
          count: '@integer(5, 20)'
        }
      ]
    }).list,
    sourceData: [
      { name: '线上', value: 70 },
      { name: '线下', value: 30 }
    ],
    hotServices: Mock.mock({
      'list|5': [
        {
          name: '@ctitle(2, 10)',
          count: '@integer(10, 50)'
        }
      ]
    }).list
  })
})

// 店铺设置
Mock.mock('/api/merchant/settings', 'get', () => {
  return createResponse({
    businessDays: [1, 2, 3, 4, 5, 6],
    startTime: '09:00',
    endTime: '18:00',
    legalHolidayRest: false,
    customRestDays: [],
    maxAppointments: 20,
    advanceBookingHours: 24,
    isOpen: true,
    notificationSettings: {
      appointmentReminder: true,
      orderReminder: true,
      reviewReminder: true,
      notifyViaSms: true,
      notifyViaEmail: true
    }
  })
})

Mock.mock('/api/merchant/settings', 'put', () => {
  return createResponse({
    businessDays: [1, 2, 3, 4, 5, 6],
    startTime: '09:00',
    endTime: '18:00',
    legalHolidayRest: false,
    customRestDays: [],
    maxAppointments: 20,
    advanceBookingHours: 24,
    isOpen: true,
    notificationSettings: {
      appointmentReminder: true,
      orderReminder: true,
      reviewReminder: true,
      notifyViaSms: true,
      notifyViaEmail: true
    }
  })
})

// 账号管理
Mock.mock('/api/merchant/change-password', 'post', () => {
  return createResponse(null)
})

Mock.mock('/api/merchant/bind-phone', 'post', () => {
  return createResponse(null)
})

Mock.mock('/api/merchant/bind-email', 'post', () => {
  return createResponse(null)
})

Mock.mock('/api/merchant/send-verify-code', 'post', () => {
  return createResponse(null)
})

// 退出登录
Mock.mock('/api/merchant/logout', 'post', () => {
  return createResponse(null)
})

export function setupMerchantApiMock() {
  console.log('[Mock] 商家端API Mock已加载')
}
