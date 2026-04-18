import { randomId, randomDate, randomEnum } from '../../utils/random'

export type AnnouncementCategory = 'system' | 'activity' | 'notice' | 'update'

export interface Announcement {
  id: number
  title: string
  content: string
  summary: string
  category: AnnouncementCategory
  author: string
  status: 'published' | 'draft'
  is_read: boolean
  published_at: string
  created_at: string
  updated_at: string
}

const categoryTitles: Record<AnnouncementCategory, string[]> = {
  system: [
    '系统维护通知',
    '平台服务升级公告',
    '账户安全提醒',
    '系统功能调整通知',
    '平台运营规则更新'
  ],
  activity: [
    '春季宠物摄影大赛开始报名',
    '宠物领养日活动预告',
    '会员专属优惠活动',
    '新用户注册福利',
    '周年庆活动公告'
  ],
  notice: [
    '节假日服务时间调整',
    '平台隐私政策更新',
    '支付方式升级通知',
    '客服服务时间调整',
    '平台合作协议更新'
  ],
  update: [
    'APP新版本发布通知',
    '宠物档案功能上线',
    '在线预约功能升级',
    '评价系统优化更新',
    '消息通知功能改进'
  ]
}

const categoryContents: Record<AnnouncementCategory, { content: string; summary: string }[]> = {
  system: [
    {
      content: '尊敬的用户，为了给您提供更好的服务体验，我们将于2024年4月25日凌晨2:00-6:00进行系统维护升级。届时，平台所有服务将暂停使用，请您提前做好相关安排。给您带来的不便，敬请谅解。',
      summary: '系统将于4月25日凌晨进行维护升级，届时服务暂停'
    },
    {
      content: '我们很高兴地通知您，平台已完成服务升级！新增了宠物健康档案管理、在线问诊预约等功能，优化了用户界面和操作流程。欢迎体验新功能，如有任何问题请联系客服。',
      summary: '平台服务已完成升级，新增多项功能'
    },
    {
      content: '尊敬的用户，我们检测到您的账户安全设置需要更新。建议您定期修改密码，开启双重验证功能，以确保账户安全。如发现异常登录，请及时联系客服处理。',
      summary: '请及时更新账户安全设置，保障账户安全'
    },
    {
      content: '为提升用户体验，我们对部分系统功能进行了调整：1. 优化了搜索算法；2. 调整了订单超时时间；3. 更新了退款流程。详细说明请查看帮助中心。',
      summary: '部分系统功能已调整，详情请查看帮助中心'
    },
    {
      content: '根据最新法规要求，我们对平台运营规则进行了更新。主要变更包括：商家入驻资质要求、服务标准规范、用户权益保障等方面。请仔细阅读并遵守新规则。',
      summary: '平台运营规则已更新，请仔细阅读'
    }
  ],
  activity: [
    {
      content: '春暖花开，是时候记录您与爱宠的美好时光了！我们将于4月20日-5月10日举办春季宠物摄影大赛，参与即有机会获得丰厚奖品。一等奖：价值2000元宠物用品大礼包；二等奖：价值1000元服务代金券；三等奖：精美宠物礼品。快来参与吧！',
      summary: '4月20日-5月10日举办宠物摄影大赛，丰厚奖品等你来拿'
    },
    {
      content: '每月第一个周六是我们的宠物领养日！本次活动将于5月4日在全市各合作门店同步举行。现场将有众多可爱的待领养宠物等待有缘人，同时还有宠物护理知识讲座、免费体检等福利。让我们一起给流浪动物一个温暖的家！',
      summary: '5月4日宠物领养日活动，现场有众多待领养宠物'
    },
    {
      content: '尊敬的会员用户，为感谢您的支持，我们特别推出会员专属优惠：1. 服务预约享9折优惠；2. 商品购买满200减30；3. 首次使用新功能送50积分。活动时间：4月15日-4月30日。',
      summary: '会员专属优惠活动，服务预约享9折'
    },
    {
      content: '新用户注册福利来袭！即日起，新注册用户即可获得：1. 100元新人礼包；2. 首单立减20元；3. 免费宠物健康咨询一次。快来注册体验吧！',
      summary: '新用户注册即送100元新人礼包'
    },
    {
      content: '平台成立三周年庆典活动即将开始！4月28日-5月5日期间，全场服务8折起，精选商品买一送一，更有神秘大奖等你抽取。感谢三年来有您的陪伴！',
      summary: '三周年庆典活动，全场服务8折起'
    }
  ],
  notice: [
    {
      content: '尊敬的用户，五一劳动节假期期间（5月1日-5月5日），部分商家服务时间将有所调整，具体以商家页面显示为准。请您提前预约，合理安排时间。祝您假期愉快！',
      summary: '五一假期服务时间调整，请提前预约'
    },
    {
      content: '我们已更新平台隐私政策，主要变更包括：个人信息收集范围、信息使用目的、用户权利保障等内容。请您仔细阅读更新后的隐私政策，继续使用平台服务即表示您同意相关政策。',
      summary: '平台隐私政策已更新，请仔细阅读'
    },
    {
      content: '为提供更便捷的支付体验，我们新增了数字人民币支付方式。目前支持：微信支付、支付宝、银行卡、数字人民币等多种支付方式。如有问题请联系客服。',
      summary: '新增数字人民币支付方式'
    },
    {
      content: '即日起，我们的客服服务时间调整为：工作日 9:00-21:00，周末及节假日 10:00-18:00。在线客服24小时可留言，我们将尽快回复。感谢您的理解与支持！',
      summary: '客服服务时间调整，在线客服24小时可留言'
    },
    {
      content: '我们更新了平台合作协议，主要涉及商家入驻条件、服务标准、违规处理等方面。请各合作商家登录商家后台查看详情，如有疑问请联系商务合作部门。',
      summary: '平台合作协议已更新，请商家查看详情'
    }
  ],
  update: [
    {
      content: 'APP V3.0版本已正式发布！新版本亮点：1. 全新UI设计，操作更流畅；2. 新增宠物健康档案功能；3. 优化预约流程，支持一键预约；4. 修复已知问题，提升稳定性。请及时更新体验！',
      summary: 'APP V3.0发布，全新UI设计和宠物健康档案功能'
    },
    {
      content: '宠物档案功能正式上线！您现在可以为您的爱宠建立完整的健康档案，包括：疫苗接种记录、体检报告、用药记录、体重追踪等。科学养宠，从档案管理开始！',
      summary: '宠物档案功能上线，可记录疫苗、体检等信息'
    },
    {
      content: '在线预约功能全面升级！新功能包括：1. 支持多时段选择；2. 实时显示商家可预约时间；3. 预约提醒推送；4. 一键改约/取消。让预约更便捷，服务更贴心！',
      summary: '在线预约功能升级，支持多时段选择和实时显示'
    },
    {
      content: '评价系统优化更新：1. 新增图片评价功能；2. 支持视频评价；3. 评价标签化展示；4. 商家回复实时通知。您的真实评价是我们改进服务的动力！',
      summary: '评价系统优化，支持图片和视频评价'
    },
    {
      content: '消息通知功能改进：1. 新增消息分类管理；2. 支持消息免打扰设置；3. 重要消息置顶显示；4. 消息批量操作。让您不错过任何重要信息！',
      summary: '消息通知功能改进，新增分类管理和免打扰设置'
    }
  ]
}

const authors = ['平台管理员', '运营团队', '客服中心', '产品团队', '技术部门']

const generateAnnouncements = (count = 10): Announcement[] => {
  const announcements: Announcement[] = []
  const categories: AnnouncementCategory[] = ['system', 'activity', 'notice', 'update']

  for (let i = 0; i < count; i++) {
    const category = randomEnum(categories)
    const titles = categoryTitles[category]
    const contents = categoryContents[category]
    const titleIndex = Math.floor(Math.random() * titles.length)

    announcements.push({
      id: randomId(),
      title: titles[titleIndex],
      content: contents[titleIndex].content,
      summary: contents[titleIndex].summary,
      category: category,
      author: randomEnum(authors),
      status: Math.random() > 0.2 ? 'published' : 'draft',
      is_read: Math.random() > 0.7,
      published_at: randomDate('2024-01-01'),
      created_at: randomDate('2024-01-01'),
      updated_at: randomDate('2024-01-01')
    })
  }

  return announcements.sort((a, b) =>
    new Date(b.published_at).getTime() - new Date(a.published_at).getTime()
  )
}

export const announcementsData = generateAnnouncements(10)

export const announcementStats = {
  total: 10,
  unread: 3,
  by_category: {
    system: 3,
    activity: 2,
    notice: 3,
    update: 2
  }
}
