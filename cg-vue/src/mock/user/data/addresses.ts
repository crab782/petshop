import { randomId, randomDate } from '../../utils/random'

export interface Address {
  id: number
  userId: number
  contactName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: boolean
  createdAt: string
  updatedAt: string
}

export const addressesData: Address[] = [
  {
    id: 1,
    userId: 1,
    contactName: '张三',
    phone: '13800138001',
    province: '北京市',
    city: '北京市',
    district: '朝阳区',
    detailAddress: '建国路88号SOHO现代城A座1201室',
    isDefault: true,
    createdAt: '2024-01-15 10:30:00',
    updatedAt: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    userId: 1,
    contactName: '李四',
    phone: '13900139002',
    province: '上海市',
    city: '上海市',
    district: '浦东新区',
    detailAddress: '陆家嘴环路1000号恒生银行大厦25楼',
    isDefault: false,
    createdAt: '2024-02-20 14:15:00',
    updatedAt: '2024-02-20 14:15:00'
  },
  {
    id: 3,
    userId: 1,
    contactName: '王五',
    phone: '13700137003',
    province: '广东省',
    city: '深圳市',
    district: '南山区',
    detailAddress: '科技园南区高新南一道飞亚达大厦3楼',
    isDefault: false,
    createdAt: '2024-03-10 09:45:00',
    updatedAt: '2024-03-10 09:45:00'
  },
  {
    id: 4,
    userId: 2,
    contactName: '赵六',
    phone: '13600136004',
    province: '浙江省',
    city: '杭州市',
    district: '西湖区',
    detailAddress: '文三路398号东信大厦12楼1205室',
    isDefault: true,
    createdAt: '2024-01-20 16:20:00',
    updatedAt: '2024-01-20 16:20:00'
  },
  {
    id: 5,
    userId: 2,
    contactName: '孙七',
    phone: '13500135005',
    province: '江苏省',
    city: '南京市',
    district: '鼓楼区',
    detailAddress: '中山北路30号城市名人酒店8楼',
    isDefault: false,
    createdAt: '2024-02-28 11:00:00',
    updatedAt: '2024-02-28 11:00:00'
  }
]

export const generateAddress = (userId: number): Address => ({
  id: randomId(),
  userId: userId,
  contactName: '新收货人',
  phone: '13800000000',
  province: '北京市',
  city: '北京市',
  district: '海淀区',
  detailAddress: '新建地址',
  isDefault: false,
  createdAt: randomDate('2024-01-01'),
  updatedAt: randomDate('2024-01-01')
})
