import { randomId, randomDate, randomEnum } from '../../utils/random'

export type NotificationType = 'system' | 'order' | 'appointment' | 'review'

export interface Notification {
  id: number
  userId: number
  title: string
  content: string
  type: NotificationType
  isRead: boolean
  createdAt: string
}

const notificationTitles: Record<NotificationType, string[]> = {
  system: [
    '系统维护通知',
    '新功能上线通知',
    '账户安全提醒',
    '平台公告',
    '服务升级通知'
  ],
  order: [
    '订单支付成功',
    '订单已发货',
    '订单已完成',
    '订单取消通知',
    '退款成功通知'
  ],
  appointment: [
    '预约确认通知',
    '预约提醒',
    '预约取消通知',
    '服务完成通知',
    '预约时间变更'
  ],
  review: [
    '商家回复了您的评价',
    '评价审核通过',
    '收到新的评价',
    '评价被点赞',
    '评价被回复'
  ]
}

const notificationContents: Record<NotificationType, string[]> = {
  system: [
    '系统将于今晚22:00-24:00进行维护升级，届时服务将暂停，请提前做好准备。',
    '新版本已上线，新增宠物档案管理功能，快来体验吧！',
    '检测到您的账户在新设备登录，如非本人操作请及时修改密码。',
    '平台将于下周举办宠物摄影大赛，欢迎各位宠物爱好者参与！',
    '我们的服务已全面升级，现在支持更多支付方式，体验更便捷。'
  ],
  order: [
    '您的订单 #12345 已支付成功，商家正在准备发货，请耐心等待。',
    '您的订单 #12346 已发货，快递单号：SF1234567890，请注意查收。',
    '您的订单 #12347 已完成，感谢您的购买，欢迎再次光临！',
    '您的订单 #12348 已取消，退款将在3-5个工作日内原路返回。',
    '您的订单 #12349 退款已成功，金额￥199.00已退回您的账户。'
  ],
  appointment: [
    '您预约的宠物美容服务已确认，时间：2024年4月20日 14:00，请准时到店。',
    '温馨提醒：您预约的宠物洗澡服务将于明天上午10:00开始，请提前准备。',
    '您预约的服务已取消，如有疑问请联系商家。',
    '您的宠物寄养服务已完成，感谢您的信任，欢迎下次再来！',
    '您预约的服务时间已调整为2024年4月21日 15:00，请确认。'
  ],
  review: [
    '商家"宠物乐园"回复了您的评价："感谢您的好评，我们会继续努力！"',
    '您发布的评价已通过审核，现在可以在详情页查看。',
    '您收到了一条新的评价回复，点击查看详情。',
    '您的评价获得了10个点赞，继续保持！',
    '用户"爱宠达人"回复了您的评价："同感，这家店确实不错！"'
  ]
}

const generateNotifications = (count = 10): Notification[] => {
  const notifications: Notification[] = []
  const types: NotificationType[] = ['system', 'order', 'appointment', 'review']

  for (let i = 0; i < count; i++) {
    const type = randomEnum(types)
    const titles = notificationTitles[type]
    const contents = notificationContents[type]

    notifications.push({
      id: randomId(),
      userId: randomId(1, 100),
      title: randomEnum(titles),
      content: randomEnum(contents),
      type: type,
      isRead: Math.random() > 0.5,
      createdAt: randomDate('2024-01-01')
    })
  }
  return notifications
}

export const notificationsData = generateNotifications(12)

export const notificationStats = {
  total: 12,
  unread: 5,
  by_type: {
    system: 3,
    order: 4,
    appointment: 3,
    review: 2
  }
}
