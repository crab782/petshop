export interface Pet {
  id: number
  user_id: number
  name: string
  type: string
  breed: string
  age: number
  gender: 'male' | 'female'
  weight: number
  bodyType: string
  furColor: string
  personality: string
  avatar: string
  description: string
  created_at: string
  updated_at: string
}

export const petsData: Pet[] = [
  {
    id: 1,
    user_id: 1,
    name: '豆豆',
    type: '狗',
    breed: '金毛寻回犬',
    age: 3,
    gender: 'male',
    weight: 28.5,
    bodyType: '大型',
    furColor: '金色',
    personality: '活泼友善、聪明温顺',
    avatar: 'https://api.dicebear.com/7.x/thumbs/svg?seed=golden&backgroundColor=ffdfbf',
    description: '豆豆是一只非常活泼的金毛，喜欢在公园里奔跑和玩飞盘。对人非常友善，特别喜欢小孩子。',
    created_at: '2024-01-20 09:00:00',
    updated_at: '2024-03-15 16:30:00'
  },
  {
    id: 2,
    user_id: 1,
    name: '咪咪',
    type: '猫',
    breed: '英国短毛猫',
    age: 2,
    gender: 'female',
    weight: 4.2,
    bodyType: '中型',
    furColor: '蓝灰色',
    personality: '安静优雅、独立高冷',
    avatar: 'https://api.dicebear.com/7.x/thumbs/svg?seed=british&backgroundColor=c0aede',
    description: '咪咪是一只优雅的英短，喜欢在窗台上晒太阳。性格比较独立，但偶尔也会撒娇求抱抱。',
    created_at: '2024-02-10 14:20:00',
    updated_at: '2024-04-01 11:00:00'
  },
  {
    id: 3,
    user_id: 1,
    name: '小白',
    type: '狗',
    breed: '萨摩耶',
    age: 1,
    gender: 'male',
    weight: 22.0,
    bodyType: '中型',
    furColor: '纯白色',
    personality: '微笑天使、调皮捣蛋',
    avatar: 'https://api.dicebear.com/7.x/thumbs/svg?seed=samoyed&backgroundColor=ffffff',
    description: '小白是家里的开心果，总是带着标志性的微笑。精力充沛，喜欢到处跑，是个十足的捣蛋鬼。',
    created_at: '2024-03-05 10:15:00',
    updated_at: '2024-04-12 09:45:00'
  },
  {
    id: 4,
    user_id: 1,
    name: '橘子',
    type: '猫',
    breed: '橘猫',
    age: 4,
    gender: 'male',
    weight: 6.8,
    bodyType: '中型',
    furColor: '橘色',
    personality: '贪吃懒散、亲人粘人',
    avatar: 'https://api.dicebear.com/7.x/thumbs/svg?seed=orange&backgroundColor=ffd5dc',
    description: '橘子是一只典型的橘猫，以吃和睡为人生两大目标。虽然胖乎乎的，但非常亲人，喜欢被撸。',
    created_at: '2023-12-01 08:30:00',
    updated_at: '2024-02-28 17:00:00'
  },
  {
    id: 5,
    user_id: 1,
    name: '球球',
    type: '狗',
    breed: '柯基',
    age: 2,
    gender: 'female',
    weight: 12.5,
    bodyType: '小型',
    furColor: '黄白相间',
    personality: '机灵活泼、短腿萌宠',
    avatar: 'https://api.dicebear.com/7.x/thumbs/svg?seed=corgi&backgroundColor=d1d4f9',
    description: '球球是一只可爱的柯基，小短腿走起路来一扭一扭的特别萌。性格活泼，喜欢追着自己的尾巴转圈。',
    created_at: '2024-01-08 11:45:00',
    updated_at: '2024-03-20 15:30:00'
  }
]

export default petsData
