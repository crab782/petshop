import Mock from 'mockjs'
import type { MockPet, MockAddress } from '../types'
import { mockPets, mockAddresses, generateMockPet, generateMockAddress } from '../data'
import { createSuccessResponse, createErrorResponse, generateNumericId } from '../utils/generators'

const currentUserId = 1

export const setupUserHandlers = () => {
  Mock.mock('/api/user/pets', 'get', () => {
    const pets = mockPets.filter(p => p.userId === currentUserId)
    return createSuccessResponse(pets)
  })

  Mock.mock(/\/api\/user\/pets\/\d+/, 'get', (options: { url: string }) => {
    const id = parseInt(options.url.split('/').pop() || '0')
    const pet = mockPets.find(p => p.id === id && p.userId === currentUserId)
    if (pet) {
      return createSuccessResponse(pet)
    }
    return createErrorResponse('宠物不存在', 404)
  })

  Mock.mock('/api/user/pets', 'post', (options: { body: string }) => {
    const data = JSON.parse(options.body) as Omit<MockPet, 'id' | 'userId' | 'createdAt' | 'updatedAt'>
    const newPet: MockPet = {
      ...data,
      id: generateNumericId(100, 9999),
      userId: currentUserId,
      createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
    }
    mockPets.push(newPet)
    return createSuccessResponse(newPet, '添加成功')
  })

  Mock.mock(/\/api\/user\/pets\/\d+/, 'put', (options: { url: string; body: string }) => {
    const id = parseInt(options.url.split('/').pop() || '0')
    const index = mockPets.findIndex(p => p.id === id && p.userId === currentUserId)
    if (index > -1) {
      const data = JSON.parse(options.body) as Partial<MockPet>
      mockPets[index] = {
        ...mockPets[index],
        ...data,
        updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
      }
      return createSuccessResponse(mockPets[index], '更新成功')
    }
    return createErrorResponse('宠物不存在', 404)
  })

  Mock.mock(/\/api\/user\/pets\/\d+/, 'delete', (options: { url: string }) => {
    const id = parseInt(options.url.split('/').pop() || '0')
    const index = mockPets.findIndex(p => p.id === id && p.userId === currentUserId)
    if (index > -1) {
      mockPets.splice(index, 1)
      return createSuccessResponse(null, '删除成功')
    }
    return createErrorResponse('宠物不存在', 404)
  })

  Mock.mock('/api/user/addresses', 'get', () => {
    const addresses = mockAddresses.filter(a => a.userId === currentUserId)
    return createSuccessResponse(addresses)
  })

  Mock.mock('/api/user/addresses', 'post', (options: { body: string }) => {
    const data = JSON.parse(options.body) as Omit<MockAddress, 'id' | 'userId' | 'createdAt' | 'updatedAt'>
    if (data.isDefault) {
      mockAddresses.forEach(a => {
        if (a.userId === currentUserId) {
          a.isDefault = false
        }
      })
    }
    const newAddress: MockAddress = {
      ...data,
      id: generateNumericId(100, 9999),
      userId: currentUserId,
      createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
    }
    mockAddresses.push(newAddress)
    return createSuccessResponse(newAddress, '添加成功')
  })

  Mock.mock(/\/api\/user\/addresses\/\d+/, 'put', (options: { url: string; body: string }) => {
    const id = parseInt(options.url.split('/').pop() || '0')
    const index = mockAddresses.findIndex(a => a.id === id && a.userId === currentUserId)
    if (index > -1) {
      const data = JSON.parse(options.body) as Partial<MockAddress>
      if (data.isDefault) {
        mockAddresses.forEach(a => {
          if (a.userId === currentUserId) {
            a.isDefault = false
          }
        })
      }
      mockAddresses[index] = {
        ...mockAddresses[index],
        ...data,
        updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
      }
      return createSuccessResponse(mockAddresses[index], '更新成功')
    }
    return createErrorResponse('地址不存在', 404)
  })

  Mock.mock(/\/api\/user\/addresses\/\d+/, 'delete', (options: { url: string }) => {
    const id = parseInt(options.url.split('/').pop() || '0')
    const index = mockAddresses.findIndex(a => a.id === id && a.userId === currentUserId)
    if (index > -1) {
      mockAddresses.splice(index, 1)
      return createSuccessResponse(null, '删除成功')
    }
    return createErrorResponse('地址不存在', 404)
  })

  Mock.mock(/\/api\/user\/addresses\/\d+\/default/, 'put', (options: { url: string }) => {
    const id = parseInt(options.url.split('/')[4])
    const address = mockAddresses.find(a => a.id === id && a.userId === currentUserId)
    if (address) {
      mockAddresses.forEach(a => {
        if (a.userId === currentUserId) {
          a.isDefault = a.id === id
        }
      })
      return createSuccessResponse(null, '设置成功')
    }
    return createErrorResponse('地址不存在', 404)
  })
}

export default setupUserHandlers
