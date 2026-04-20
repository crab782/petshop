import Mock from 'mockjs'
import { petsData } from '../data/pets'
import { appointments } from '../data/appointments'
import { reviewsData } from '../data/reviews'
import { services } from '../data/services'
import { orders } from '../data/orders'

const currentUserId = 1

export const setupUserHomeHandlers = () => {
  Mock.mock('/api/user/home/stats', 'get', () => {
    const userPets = petsData.filter(p => p.user_id === currentUserId)
    const pendingAppointments = appointments.filter(
      a => a.userId === currentUserId && a.status === 'pending'
    )
    const userReviews = reviewsData.filter(r => r.user_id === currentUserId)

    return {
      code: 200,
      message: 'success',
      data: {
        petCount: userPets.length,
        pendingAppointments: pendingAppointments.length,
        reviewCount: userReviews.length
      }
    }
  })

  Mock.mock('/api/user/home/activities', 'get', () => {
    const activities: any[] = []

    const recentAppointments = appointments
      .filter(a => a.userId === currentUserId)
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 3)

    recentAppointments.forEach(apt => {
      const service = services.find(s => s.id === apt.serviceId)
      const statusMap: Record<string, { status: string; statusColor: string }> = {
        pending: { status: '待确认', statusColor: 'warning' },
        confirmed: { status: '已确认', statusColor: 'primary' },
        completed: { status: '已完成', statusColor: 'success' },
        cancelled: { status: '已取消', statusColor: 'info' }
      }
      const statusInfo = statusMap[apt.status] || { status: apt.status, statusColor: 'default' }

      activities.push({
        id: apt.id,
        type: 'appointment',
        title: `预约了${service?.name || '服务'}`,
        time: apt.appointmentTime,
        status: statusInfo.status,
        statusColor: statusInfo.statusColor,
        relatedId: apt.id
      })
    })

    const recentOrders = orders
      .filter(o => o.userId === currentUserId)
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 2)

    recentOrders.forEach(order => {
      const statusMap: Record<string, { status: string; statusColor: string }> = {
        pending: { status: '待支付', statusColor: 'warning' },
        paid: { status: '已支付', statusColor: 'primary' },
        shipped: { status: '已发货', statusColor: 'info' },
        completed: { status: '已完成', statusColor: 'success' },
        cancelled: { status: '已取消', statusColor: 'default' }
      }
      const statusInfo = statusMap[order.status] || { status: order.status, statusColor: 'default' }

      activities.push({
        id: order.id,
        type: 'order',
        title: `购买了商品`,
        time: order.createdAt,
        status: statusInfo.status,
        statusColor: statusInfo.statusColor,
        relatedId: order.id
      })
    })

    const recentReviews = reviewsData
      .filter(r => r.user_id === currentUserId)
      .sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
      .slice(0, 2)

    recentReviews.forEach(review => {
      const targetName = review.target_type === 'service' 
        ? services.find(s => s.id === review.target_id)?.name || '服务'
        : '商品'
      
      activities.push({
        id: review.id,
        type: 'review',
        title: `评价了${targetName}`,
        time: review.created_at,
        status: '已评价',
        statusColor: 'success',
        relatedId: review.id
      })
    })

    activities.sort((a, b) => new Date(b.time).getTime() - new Date(a.time).getTime())

    return {
      code: 200,
      message: 'success',
      data: activities.slice(0, 10)
    }
  })
}

export default setupUserHomeHandlers
