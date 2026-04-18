import Mock from 'mockjs'
import {
  appointments,
  getAppointmentDetail,
  getUserAppointments,
  createAppointment,
  updateAppointmentStatus
} from '../data/appointments'

const parseQuery = (url: string) => {
  const queryStr = url.split('?')[1]
  if (!queryStr) return {}
  
  return queryStr.split('&').reduce((acc, param) => {
    const [key, value] = param.split('=')
    acc[key] = decodeURIComponent(value)
    return acc
  }, {} as Record<string, string>)
}

Mock.mock(/\/api\/appointments(\?.*)?$/, 'get', (options: { url: string }) => {
  const query = parseQuery(options.url)
  const { page = '1', size = '10', status, startDate, endDate, keyword } = query
  
  let filtered = [...appointments]
  
  if (status) {
    filtered = filtered.filter(a => a.status === status)
  }
  
  if (startDate) {
    filtered = filtered.filter(a => a.appointmentTime >= startDate)
  }
  
  if (endDate) {
    filtered = filtered.filter(a => a.appointmentTime <= endDate + ' 23:59:59')
  }
  
  if (keyword) {
    filtered = filtered.filter(a => 
      a.appointmentNo.includes(keyword) ||
      a.notes?.includes(keyword)
    )
  }
  
  const pageNum = parseInt(page)
  const pageSize = parseInt(size)
  const start = (pageNum - 1) * pageSize
  const end = start + pageSize
  
  const list = filtered.slice(start, end).map(a => getAppointmentDetail(a.id))
  
  return {
    code: 200,
    message: 'success',
    data: {
      total: filtered.length,
      page: pageNum,
      size: pageSize,
      list
    }
  }
})

Mock.mock(/\/api\/appointments\/(\d+)$/, 'get', (options: { url: string }) => {
  const match = options.url.match(/\/api\/appointments\/(\d+)$/)
  const id = match ? parseInt(match[1]) : 0
  
  const detail = getAppointmentDetail(id)
  
  if (!detail) {
    return {
      code: 404,
      message: '预约不存在',
      data: null
    }
  }
  
  return {
    code: 200,
    message: 'success',
    data: detail
  }
})

Mock.mock('/api/appointments', 'post', (options: { body: string }) => {
  const body = JSON.parse(options.body)
  
  const newAppointment = createAppointment(body)
  const detail = getAppointmentDetail(newAppointment.id)
  
  return {
    code: 200,
    message: '预约创建成功',
    data: detail
  }
})

Mock.mock(/\/api\/appointments\/(\d+)\/cancel$/, 'put', (options: { url: string }) => {
  const match = options.url.match(/\/api\/appointments\/(\d+)\/cancel$/)
  const id = match ? parseInt(match[1]) : 0
  
  const appointment = appointments.find(a => a.id === id)
  
  if (!appointment) {
    return {
      code: 404,
      message: '预约不存在',
      data: null
    }
  }
  
  if (appointment.status === 'completed') {
    return {
      code: 400,
      message: '已完成的预约无法取消',
      data: null
    }
  }
  
  if (appointment.status === 'cancelled') {
    return {
      code: 400,
      message: '预约已取消',
      data: null
    }
  }
  
  const updated = updateAppointmentStatus(id, 'cancelled')
  const detail = updated ? getAppointmentDetail(updated.id) : null
  
  return {
    code: 200,
    message: '预约已取消',
    data: detail
  }
})

Mock.mock(/\/api\/appointments\/(\d+)\/confirm$/, 'put', (options: { url: string }) => {
  const match = options.url.match(/\/api\/appointments\/(\d+)\/confirm$/)
  const id = match ? parseInt(match[1]) : 0
  
  const appointment = appointments.find(a => a.id === id)
  
  if (!appointment) {
    return {
      code: 404,
      message: '预约不存在',
      data: null
    }
  }
  
  if (appointment.status !== 'pending') {
    return {
      code: 400,
      message: '只有待处理的预约可以确认',
      data: null
    }
  }
  
  const updated = updateAppointmentStatus(id, 'confirmed')
  const detail = updated ? getAppointmentDetail(updated.id) : null
  
  return {
    code: 200,
    message: '预约已确认',
    data: detail
  }
})

Mock.mock(/\/api\/appointments\/(\d+)\/complete$/, 'put', (options: { url: string }) => {
  const match = options.url.match(/\/api\/appointments\/(\d+)\/complete$/)
  const id = match ? parseInt(match[1]) : 0
  
  const appointment = appointments.find(a => a.id === id)
  
  if (!appointment) {
    return {
      code: 404,
      message: '预约不存在',
      data: null
    }
  }
  
  if (appointment.status !== 'confirmed') {
    return {
      code: 400,
      message: '只有已确认的预约可以完成',
      data: null
    }
  }
  
  const updated = updateAppointmentStatus(id, 'completed')
  const detail = updated ? getAppointmentDetail(updated.id) : null
  
  return {
    code: 200,
    message: '预约已完成',
    data: detail
  }
})

Mock.mock(/\/api\/user\/appointments(\?.*)?$/, 'get', (options: { url: string }) => {
  const query = parseQuery(options.url)
  const { page = '1', size = '10', status, startDate, endDate } = query
  
  const userId = 1
  let filtered = getUserAppointments(userId)
  
  if (status) {
    filtered = filtered.filter(a => a.status === status)
  }
  
  if (startDate) {
    filtered = filtered.filter(a => a.appointmentTime >= startDate)
  }
  
  if (endDate) {
    filtered = filtered.filter(a => a.appointmentTime <= endDate + ' 23:59:59')
  }
  
  const pageNum = parseInt(page)
  const pageSize = parseInt(size)
  const start = (pageNum - 1) * pageSize
  const end = start + pageSize
  
  const list = filtered.slice(start, end).map(a => getAppointmentDetail(a.id))
  
  return {
    code: 200,
    message: 'success',
    data: {
      total: filtered.length,
      page: pageNum,
      size: pageSize,
      list
    }
  }
})

Mock.mock(/\/api\/user\/appointments\/(\d+)$/, 'get', (options: { url: string }) => {
  const match = options.url.match(/\/api\/user\/appointments\/(\d+)$/)
  const id = match ? parseInt(match[1]) : 0
  
  const detail = getAppointmentDetail(id)
  
  if (!detail) {
    return {
      code: 404,
      message: '预约不存在',
      data: null
    }
  }
  
  return {
    code: 200,
    message: 'success',
    data: detail
  }
})

export default Mock
