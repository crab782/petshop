import Mock from 'mockjs'
import { userData, UserInfo } from './user/data/user'
import { petsData, Pet } from './user/data/pets'

let currentUser: UserInfo = { ...userData }
let pets: Pet[] = [...petsData]
let nextPetId = 6

Mock.mock('/api/user/profile', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: currentUser
  }
})

Mock.mock('/api/user/profile', 'put', (req: { body: string }) => {
  const updateData = JSON.parse(req.body) as Partial<UserInfo>
  currentUser = {
    ...currentUser,
    ...updateData,
    updated_at: new Date().toISOString().replace('T', ' ').substring(0, 19)
  }
  return {
    code: 200,
    message: '更新成功',
    data: currentUser
  }
})

Mock.mock(RegExp('/api/user/pets.*'), 'get', (req: { url: string }) => {
  const url = new URL(req.url, 'http://localhost')
  const type = url.searchParams.get('type')
  const keyword = url.searchParams.get('keyword')

  let filteredPets = pets.filter(p => p.user_id === currentUser.id)

  if (type) {
    filteredPets = filteredPets.filter(p => p.type === type)
  }
  if (keyword) {
    filteredPets = filteredPets.filter(p =>
      p.name.includes(keyword) ||
      p.breed.includes(keyword)
    )
  }

  return {
    code: 200,
    message: 'success',
    data: filteredPets
  }
})

Mock.mock('/api/user/pets', 'post', (req: { body: string }) => {
  const newPet = JSON.parse(req.body) as Omit<Pet, 'id' | 'user_id' | 'created_at' | 'updated_at'>
  const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
  const pet: Pet = {
    ...newPet,
    id: nextPetId++,
    user_id: currentUser.id,
    created_at: now,
    updated_at: now
  }
  pets.push(pet)
  return {
    code: 200,
    message: '创建成功',
    data: pet
  }
})

Mock.mock(RegExp('/api/user/pets/\\d+$'), 'get', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/user\/pets\/(\d+)/)?.[1] || '0')
  const pet = pets.find(p => p.id === id && p.user_id === currentUser.id)
  if (!pet) {
    return {
      code: 404,
      message: '宠物不存在',
      data: null
    }
  }
  return {
    code: 200,
    message: 'success',
    data: pet
  }
})

Mock.mock(RegExp('/api/user/pets/\\d+$'), 'put', (req: { url: string; body: string }) => {
  const id = parseInt(req.url.match(/\/api\/user\/pets\/(\d+)/)?.[1] || '0')
  const updateData = JSON.parse(req.body) as Partial<Pet>
  const index = pets.findIndex(p => p.id === id && p.user_id === currentUser.id)

  if (index === -1) {
    return {
      code: 404,
      message: '宠物不存在',
      data: null
    }
  }

  pets[index] = {
    ...pets[index],
    ...updateData,
    updated_at: new Date().toISOString().replace('T', ' ').substring(0, 19)
  }

  return {
    code: 200,
    message: '更新成功',
    data: pets[index]
  }
})

Mock.mock(RegExp('/api/user/pets/\\d+$'), 'delete', (req: { url: string }) => {
  const id = parseInt(req.url.match(/\/api\/user\/pets\/(\d+)/)?.[1] || '0')
  const index = pets.findIndex(p => p.id === id && p.user_id === currentUser.id)

  if (index === -1) {
    return {
      code: 404,
      message: '宠物不存在',
      data: null
    }
  }

  pets.splice(index, 1)

  return {
    code: 200,
    message: '删除成功',
    data: null
  }
})

export function setupUserMock() {
  console.log('用户Mock服务已配置')
}

export { currentUser, pets }
