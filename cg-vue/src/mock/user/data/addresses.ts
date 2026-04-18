import { randomId, randomDate } from '../../utils/random'

export interface Address {
  id: number
  user_id: number
  receiver_name: string
  phone: string
  province: string
  city: string
  district: string
  detail_address: string
  is_default: boolean
  created_at: string
  updated_at: string
}

export const addressesData: Address[] = [
  {
    id: 1,
    user_id: 1,
    receiver_name: '张三',
    phone: '13800138001',
    province: '北京市',
    city: '北京市',
    district: '朝阳区',
    detail_address: '建国路88号SOHO现代城A座1201室',
    is_default: true,
    created_at: '2024-01-15 10:30:00',
    updated_at: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    user_id: 1,
    receiver_name: '李四',
    phone: '13900139002',
    province: '上海市',
    city: '上海市',
    district: '浦东新区',
    detail_address: '陆家嘴环路1000号恒生银行大厦25楼',
    is_default: false,
    created_at: '2024-02-20 14:15:00',
    updated_at: '2024-02-20 14:15:00'
  },
  {
    id: 3,
    user_id: 1,
    receiver_name: '王五',
    phone: '13700137003',
    province: '广东省',
    city: '深圳市',
    district: '南山区',
    detail_address: '科技园南区高新南一道飞亚达大厦3楼',
    is_default: false,
    created_at: '2024-03-10 09:45:00',
    updated_at: '2024-03-10 09:45:00'
  },
  {
    id: 4,
    user_id: 2,
    receiver_name: '赵六',
    phone: '13600136004',
    province: '浙江省',
    city: '杭州市',
    district: '西湖区',
    detail_address: '文三路398号东信大厦12楼1205室',
    is_default: true,
    created_at: '2024-01-20 16:20:00',
    updated_at: '2024-01-20 16:20:00'
  },
  {
    id: 5,
    user_id: 2,
    receiver_name: '孙七',
    phone: '13500135005',
    province: '江苏省',
    city: '南京市',
    district: '鼓楼区',
    detail_address: '中山北路30号城市名人酒店8楼',
    is_default: false,
    created_at: '2024-02-28 11:00:00',
    updated_at: '2024-02-28 11:00:00'
  }
]

export const generateAddress = (userId: number): Address => ({
  id: randomId(),
  user_id: userId,
  receiver_name: '新收货人',
  phone: '13800000000',
  province: '北京市',
  city: '北京市',
  district: '海淀区',
  detail_address: '新建地址',
  is_default: false,
  created_at: randomDate('2024-01-01'),
  updated_at: randomDate('2024-01-01')
})
