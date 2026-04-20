import Mock from 'mockjs'
import { addressesData, generateAddress, type Address } from '../data/addresses'
import { randomId } from '../../utils/random'

const addressesList = [...addressesData]
const currentUserId = 1

const getCurrentTime = () => {
  return new Date().toISOString().replace('T', ' ').substring(0, 19)
}

export const setupAddressHandlers = () => {
  Mock.mock('/api/addresses', 'get', () => {
    const addresses = addressesList.filter(a => a.user_id === currentUserId)
    return {
      code: 200,
      message: 'success',
      data: addresses
    }
  })

  Mock.mock(/\/api\/addresses\/(\d+)$/, 'get', (options: { url: string }) => {
    const id = parseInt(options.url.match(/\/api\/addresses\/(\d+)/)?.[1] || '0')
    const address = addressesList.find(a => a.id === id && a.user_id === currentUserId)

    if (!address) {
      return {
        code: 404,
        message: '地址不存在',
        data: null
      }
    }

    return {
      code: 200,
      message: 'success',
      data: address
    }
  })

  Mock.mock('/api/addresses', 'post', (options: { body: string }) => {
    const data = JSON.parse(options.body) as Omit<Address, 'id' | 'user_id' | 'created_at' | 'updated_at'>

    if (data.is_default) {
      addressesList.forEach(a => {
        if (a.user_id === currentUserId) {
          a.is_default = false
        }
      })
    }

    const newAddress: Address = {
      ...data,
      id: randomId(100, 9999),
      user_id: currentUserId,
      created_at: getCurrentTime(),
      updated_at: getCurrentTime()
    }

    addressesList.push(newAddress)

    return {
      code: 200,
      message: '添加成功',
      data: newAddress
    }
  })

  Mock.mock(/\/api\/addresses\/(\d+)$/, 'put', (options: { url: string; body: string }) => {
    const id = parseInt(options.url.match(/\/api\/addresses\/(\d+)/)?.[1] || '0')
    const index = addressesList.findIndex(a => a.id === id && a.user_id === currentUserId)

    if (index === -1) {
      return {
        code: 404,
        message: '地址不存在',
        data: null
      }
    }

    const data = JSON.parse(options.body) as Partial<Address>

    if (data.is_default) {
      addressesList.forEach(a => {
        if (a.user_id === currentUserId) {
          a.is_default = false
        }
      })
    }

    addressesList[index] = {
      ...addressesList[index],
      ...data,
      updated_at: getCurrentTime()
    }

    return {
      code: 200,
      message: '更新成功',
      data: addressesList[index]
    }
  })

  Mock.mock(/\/api\/addresses\/(\d+)$/, 'delete', (options: { url: string }) => {
    const id = parseInt(options.url.match(/\/api\/addresses\/(\d+)/)?.[1] || '0')
    const index = addressesList.findIndex(a => a.id === id && a.user_id === currentUserId)

    if (index === -1) {
      return {
        code: 404,
        message: '地址不存在',
        data: null
      }
    }

    const wasDefault = addressesList[index].is_default
    addressesList.splice(index, 1)

    if (wasDefault) {
      const userAddresses = addressesList.filter(a => a.user_id === currentUserId)
      if (userAddresses.length > 0) {
        userAddresses[0].is_default = true
      }
    }

    return {
      code: 200,
      message: '删除成功',
      data: null
    }
  })

  Mock.mock(/\/api\/addresses\/(\d+)\/default$/, 'put', (options: { url: string }) => {
    const id = parseInt(options.url.match(/\/api\/addresses\/(\d+)\/default/)?.[1] || '0')
    const address = addressesList.find(a => a.id === id && a.user_id === currentUserId)

    if (!address) {
      return {
        code: 404,
        message: '地址不存在',
        data: null
      }
    }

    addressesList.forEach(a => {
      if (a.user_id === currentUserId) {
        a.is_default = a.id === id
      }
      if (a.id === id) {
        a.updated_at = getCurrentTime()
      }
    })

    return {
      code: 200,
      message: '设置默认地址成功',
      data: { id, is_default: true }
    }
  })

  Mock.mock('/api/addresses/default', 'get', () => {
    const defaultAddress = addressesList.find(a => a.user_id === currentUserId && a.is_default)

    if (!defaultAddress) {
      const userAddresses = addressesList.filter(a => a.user_id === currentUserId)
      if (userAddresses.length > 0) {
        return {
          code: 200,
          message: 'success',
          data: userAddresses[0]
        }
      }
      return {
        code: 404,
        message: '暂无收货地址',
        data: null
      }
    }

    return {
      code: 200,
      message: 'success',
      data: defaultAddress
    }
  })
}

export default setupAddressHandlers
