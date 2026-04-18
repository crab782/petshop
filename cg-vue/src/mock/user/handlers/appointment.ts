import Mock from 'mockjs'
import type { MockAppointment } from '../types'
import { mockAppointments, mockProductOrders } from '../data'
import { createSuccessResponse, createErrorResponse, generateNumericId, generateDate } from '../utils/generators'

const currentUserId = 1

export const setupAppointmentHandlers = () => {
  Mock.mock('/api/user/appointments', 'get', () => {
    const appointments = mockAppointments.filter(a => a.userId === currentUserId)
    return createSuccessResponse(appointments)
  })

  Mock.mock('/api/user/appointments', 'post', (options: { body: string }) => {
    const data = JSON.parse(options.body) as {
      serviceId: number
      petId: number
      appointmentTime: string
      remark?: string
    }

    const newAppointment: MockAppointment = {
      id: generateNumericId(100, 9999),
      userId: currentUserId,
      userName: 'testuser',
      merchantId: 1,
      merchantName: '宠物乐园1号店',
      serviceId: data.serviceId,
      serviceName: '宠物洗澡',
      petId: data.petId,
      petName: '小白',
      appointmentTime: data.appointmentTime,
      totalPrice: 68,
      status: 'pending',
      notes: data.remark || '',
      createdAt: generateDate(),
      updatedAt: generateDate()
    }

    mockAppointments.push(newAppointment)
    return createSuccessResponse(newAppointment, '预约成功')
  })

  Mock.mock(/\/api\/user\/appointments\/\d+\/cancel/, 'put', (options: { url: string }) => {
    const id = parseInt(options.url.split('/')[4])
    const appointment = mockAppointments.find(a => a.id === id && a.userId === currentUserId)
    if (appointment) {
      if (appointment.status === 'completed' || appointment.status === 'cancelled') {
        return createErrorResponse('该预约无法取消', 400)
      }
      appointment.status = 'cancelled'
      appointment.updatedAt = generateDate()
      return createSuccessResponse(null, '取消成功')
    }
    return createErrorResponse('预约不存在', 404)
  })

  Mock.mock('/api/user/orders', 'get', () => {
    const orders = mockProductOrders.filter(o => o.userId === currentUserId)
    return createSuccessResponse(orders)
  })

  Mock.mock(/\/api\/user\/orders\/\d+/, 'get', (options: { url: string }) => {
    const id = parseInt(options.url.split('/').pop() || '0')
    const order = mockProductOrders.find(o => o.id === id && o.userId === currentUserId)
    if (order) {
      return createSuccessResponse(order)
    }
    return createErrorResponse('订单不存在', 404)
  })

  Mock.mock(/\/api\/user\/orders\/\d+\/cancel/, 'put', (options: { url: string }) => {
    const id = parseInt(options.url.split('/')[4])
    const order = mockProductOrders.find(o => o.id === id && o.userId === currentUserId)
    if (order) {
      if (order.status !== 'pending') {
        return createErrorResponse('该订单无法取消', 400)
      }
      order.status = 'cancelled'
      order.cancelTime = generateDate()
      order.updatedAt = generateDate()
      return createSuccessResponse(null, '取消成功')
    }
    return createErrorResponse('订单不存在', 404)
  })

  Mock.mock(/\/api\/user\/orders\/\d+\/confirm/, 'put', (options: { url: string }) => {
    const id = parseInt(options.url.split('/')[4])
    const order = mockProductOrders.find(o => o.id === id && o.userId === currentUserId)
    if (order) {
      if (order.status !== 'shipped') {
        return createErrorResponse('该订单无法确认收货', 400)
      }
      order.status = 'completed'
      order.completeTime = generateDate()
      order.updatedAt = generateDate()
      return createSuccessResponse(null, '确认收货成功')
    }
    return createErrorResponse('订单不存在', 404)
  })

  Mock.mock(/\/api\/user\/orders\/\d+\/pay/, 'post', (options: { url: string; body: string }) => {
    const id = parseInt(options.url.split('/')[4])
    const { payMethod } = JSON.parse(options.body)
    const order = mockProductOrders.find(o => o.id === id && o.userId === currentUserId)
    if (order) {
      if (order.status !== 'pending') {
        return createErrorResponse('该订单无法支付', 400)
      }
      order.status = 'paid'
      order.payMethod = payMethod
      order.payTime = generateDate()
      order.updatedAt = generateDate()
      return createSuccessResponse(null, '支付成功')
    }
    return createErrorResponse('订单不存在', 404)
  })

  Mock.mock('/api/user/orders/batch-cancel', 'put', (options: { body: string }) => {
    const { ids } = JSON.parse(options.body) as { ids: number[] }
    ids.forEach(id => {
      const order = mockProductOrders.find(o => o.id === id && o.userId === currentUserId && o.status === 'pending')
      if (order) {
        order.status = 'cancelled'
        order.cancelTime = generateDate()
        order.updatedAt = generateDate()
      }
    })
    return createSuccessResponse(null, '批量取消成功')
  })
}

export default setupAppointmentHandlers
