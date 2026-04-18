import Mock from 'mockjs'
import { appointments } from '../data/appointments'
import { services } from '../data/services'
import { merchants } from '../data/merchants'

const currentUserId = 1

export const setupUserServicesHandlers = () => {
  Mock.mock('/api/user/services', 'get', (options) => {
    const url = new URL(options.url, 'http://localhost')
    const status = url.searchParams.get('status') || ''
    const page = parseInt(url.searchParams.get('page') || '1')
    const size = parseInt(url.searchParams.get('size') || '10')

    const userAppointments = appointments.filter(a => a.userId === currentUserId)
    
    let filtered = userAppointments
    if (status) {
      filtered = filtered.filter(a => a.status === status)
    }

    const purchasedServices = filtered.map(apt => {
      const service = services.find(s => s.id === apt.serviceId)
      const merchant = merchants.find(m => m.id === apt.merchantId)
      
      return {
        id: apt.id,
        serviceId: apt.serviceId,
        serviceName: service?.name || '未知服务',
        serviceImage: service?.image || '',
        merchantId: apt.merchantId,
        merchantName: merchant?.name || apt.merchantName,
        price: apt.totalPrice,
        status: apt.status,
        appointmentTime: apt.appointmentTime,
        createdAt: apt.createdAt
      }
    })

    const start = (page - 1) * size
    const end = start + size
    const paginatedList = purchasedServices.slice(start, end)

    return {
      code: 200,
      message: 'success',
      data: {
        total: purchasedServices.length,
        page,
        size,
        list: paginatedList
      }
    }
  })
}

export default setupUserServicesHandlers
