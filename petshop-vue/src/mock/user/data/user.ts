import type { MockUser, MockPet, MockAddress } from '../types'
import {
  generateNumericId,
  randomChineseName,
  randomPhone,
  randomEmail,
  randomAvatar,
  generateDate,
  randomPetType,
  randomPetBreed,
  randomGender,
  randomEnum,
  randomString
} from '../utils/generators'

export const mockUsers: MockUser[] = [
  {
    id: 1,
    username: 'testuser',
    email: 'test@example.com',
    phone: '13800138000',
    avatar: randomAvatar(),
    createdAt: '2024-01-01 10:00:00',
    updatedAt: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    username: 'petlover',
    email: 'petlover@example.com',
    phone: '13900139000',
    avatar: randomAvatar(),
    createdAt: '2024-02-15 14:30:00',
    updatedAt: '2024-02-15 14:30:00'
  }
]

export const mockPets: MockPet[] = [
  {
    id: 1,
    userId: 1,
    name: '小白',
    type: '狗',
    breed: '金毛',
    age: 3,
    gender: 'male',
    avatar: randomAvatar(),
    description: '活泼可爱的金毛犬',
    weight: 28,
    bodyType: '大型',
    furColor: '金色',
    personality: '温顺友好',
    createdAt: '2024-01-15 09:00:00',
    updatedAt: '2024-01-15 09:00:00'
  },
  {
    id: 2,
    userId: 1,
    name: '咪咪',
    type: '猫',
    breed: '英短',
    age: 2,
    gender: 'female',
    avatar: randomAvatar(),
    description: '慵懒的英短蓝猫',
    weight: 5,
    bodyType: '中型',
    furColor: '蓝色',
    personality: '安静独立',
    createdAt: '2024-02-01 11:00:00',
    updatedAt: '2024-02-01 11:00:00'
  },
  {
    id: 3,
    userId: 1,
    name: '球球',
    type: '仓鼠',
    breed: '金丝熊',
    age: 1,
    gender: 'male',
    avatar: randomAvatar(),
    description: '活泼的小仓鼠',
    weight: 0.1,
    bodyType: '小型',
    furColor: '金黄色',
    personality: '活泼好动',
    createdAt: '2024-03-01 16:00:00',
    updatedAt: '2024-03-01 16:00:00'
  },
  {
    id: 4,
    userId: 2,
    name: '大黄',
    type: '狗',
    breed: '拉布拉多',
    age: 4,
    gender: 'male',
    avatar: randomAvatar(),
    description: '忠诚的拉布拉多',
    weight: 30,
    bodyType: '大型',
    furColor: '黄色',
    personality: '聪明忠诚',
    createdAt: '2024-01-20 10:00:00',
    updatedAt: '2024-01-20 10:00:00'
  }
]

export const mockAddresses: MockAddress[] = [
  {
    id: 1,
    userId: 1,
    contactName: '张三',
    phone: '13800138000',
    province: '北京市',
    city: '北京市',
    district: '朝阳区',
    detailAddress: '建国路88号',
    isDefault: true,
    createdAt: '2024-01-01 10:00:00',
    updatedAt: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    userId: 1,
    contactName: '李四',
    phone: '13900139000',
    province: '上海市',
    city: '上海市',
    district: '浦东新区',
    detailAddress: '南京路100号',
    isDefault: false,
    createdAt: '2024-02-01 11:00:00',
    updatedAt: '2024-02-01 11:00:00'
  },
  {
    id: 3,
    userId: 2,
    contactName: '王五',
    phone: '13700137000',
    province: '广东省',
    city: '深圳市',
    district: '南山区',
    detailAddress: '科技园路50号',
    isDefault: true,
    createdAt: '2024-03-01 09:00:00',
    updatedAt: '2024-03-01 09:00:00'
  }
]

export const generateMockPet = (userId: number): MockPet => ({
  id: generateNumericId(),
  userId,
  name: randomChineseName().substring(0, 2),
  type: randomPetType(),
  breed: randomPetBreed(),
  age: generateNumericId(1, 10),
  gender: randomGender(),
  avatar: randomAvatar(),
  description: `可爱的${randomPetType()}`,
  createdAt: generateDate(),
  updatedAt: generateDate()
})

export const generateMockAddress = (userId: number): MockAddress => ({
  id: generateNumericId(),
  userId,
  contactName: randomChineseName(),
  phone: randomPhone(),
  province: randomEnum(['北京市', '上海市', '广东省', '浙江省', '江苏省']),
  city: randomEnum(['北京市', '上海市', '深圳市', '杭州市', '南京市']),
  district: randomEnum(['朝阳区', '海淀区', '浦东新区', '南山区', '西湖区']),
  detailAddress: `${randomString(5)}路${generateNumericId(1, 200)}号`,
  isDefault: false,
  createdAt: generateDate(),
  updatedAt: generateDate()
})
