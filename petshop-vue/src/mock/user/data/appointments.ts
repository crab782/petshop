import { randomId, randomDate, randomEnum } from '../../utils/random'

const AppointmentStatus = ['pending', 'confirmed', 'completed', 'cancelled']

export interface Appointment {
  id: number
  appointmentNo: string
  userId: number
  serviceId: number
  merchantId: number
  petId: number
  appointmentTime: string
  status: string
  totalPrice: number
  notes: string
  createdAt: string
  updatedAt: string
}

export interface AppointmentDetail extends Appointment {
  user: {
    id: number
    username: string
    avatar: string
    phone: string
  }
  service: {
    id: number
    name: string
    price: number
    duration: number
    image: string
  }
  merchant: {
    id: number
    name: string
    address: string
    phone: string
    logo: string
  }
  pet: {
    id: number
    name: string
    type: string
    breed: string
    avatar: string
  }
}

const services = [
  { id: 1, name: '宠物洗澡', price: 88, duration: 60 },
  { id: 2, name: '宠物美容', price: 168, duration: 120 },
  { id: 3, name: '宠物寄养', price: 100, duration: 1440 },
  { id: 4, name: '宠物医疗', price: 200, duration: 30 },
  { id: 5, name: '宠物训练', price: 300, duration: 90 }
]

const merchants = [
  { id: 1, name: '宠物乐园', address: '北京市朝阳区建国路88号', phone: '13800138000' },
  { id: 2, name: '萌宠之家', address: '北京市海淀区中关村大街66号', phone: '13900139000' },
  { id: 3, name: '爱宠服务中心', address: '北京市西城区金融街10号', phone: '13700137000' }
]

const pets = [
  { id: 1, name: '小白', type: '狗', breed: '萨摩耶', userId: 1 },
  { id: 2, name: '小花', type: '猫', breed: '英短', userId: 1 },
  { id: 3, name: '大黄', type: '狗', breed: '金毛', userId: 1 },
  { id: 4, name: '咪咪', type: '猫', breed: '美短', userId: 1 }
]

const users = [
  { id: 1, username: '张三', phone: '13800138001', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=user1' },
  { id: 2, username: '李四', phone: '13800138002', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=user2' }
]

const generateAppointmentNo = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `APT${year}${month}${day}${random}`
}

const generateAppointments = (count = 10): Appointment[] => {
  const appointments: Appointment[] = []
  const now = new Date()
  
  const timeSlots = [
    { label: 'past', days: -7 },
    { label: 'past', days: -3 },
    { label: 'past', days: -1 },
    { label: 'today', days: 0 },
    { label: 'today', days: 0 },
    { label: 'future', days: 1 },
    { label: 'future', days: 3 },
    { label: 'future', days: 7 },
    { label: 'future', days: 14 },
    { label: 'future', days: 30 }
  ]

  const statusByTime = {
    past: ['completed', 'cancelled'],
    today: ['pending', 'confirmed', 'completed'],
    future: ['pending', 'confirmed']
  }

  for (let i = 0; i < count; i++) {
    const slot = timeSlots[i % timeSlots.length]
    const service = services[i % services.length]
    const merchant = merchants[i % merchants.length]
    const pet = pets[i % pets.length]
    
    const appointmentDate = new Date(now)
    appointmentDate.setDate(appointmentDate.getDate() + slot.days)
    appointmentDate.setHours(9 + (i % 9), 0, 0, 0)
    
    const statusOptions = statusByTime[slot.label]
    const status = statusOptions[i % statusOptions.length]

    appointments.push({
      id: i + 1,
      appointmentNo: generateAppointmentNo(),
      userId: pet.userId,
      serviceId: service.id,
      merchantId: merchant.id,
      petId: pet.id,
      appointmentTime: appointmentDate.toISOString().replace('T', ' ').substring(0, 19),
      status,
      totalPrice: service.price,
      notes: i % 3 === 0 ? '请轻柔对待我的宠物' : '',
      createdAt: new Date(appointmentDate.getTime() - 86400000 * 3).toISOString().replace('T', ' ').substring(0, 19),
      updatedAt: appointmentDate.toISOString().replace('T', ' ').substring(0, 19)
    })
  }

  return appointments
}

export const appointments = generateAppointments(10)

export const getAppointmentDetail = (id: number): AppointmentDetail | null => {
  const appointment = appointments.find(a => a.id === id)
  if (!appointment) return null

  const service = services.find(s => s.id === appointment.serviceId)!
  const merchant = merchants.find(m => m.id === appointment.merchantId)!
  const pet = pets.find(p => p.id === appointment.petId)!
  const user = users.find(u => u.id === appointment.userId)!

  return {
    ...appointment,
    user: {
      id: user.id,
      username: user.username,
      avatar: user.avatar,
      phone: user.phone
    },
    service: {
      id: service.id,
      name: service.name,
      price: service.price,
      duration: service.duration,
      image: `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20service%20${service.name}&image_size=square`
    },
    merchant: {
      id: merchant.id,
      name: merchant.name,
      address: merchant.address,
      phone: merchant.phone,
      logo: `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20shop%20${merchant.name}&image_size=square`
    },
    pet: {
      id: pet.id,
      name: pet.name,
      type: pet.type,
      breed: pet.breed,
      avatar: `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20${pet.type}%20${pet.breed}&image_size=square`
    }
  }
}

export const getUserAppointments = (userId: number) => {
  return appointments.filter(a => a.userId === userId)
}

export const createAppointment = (data: Partial<Appointment>): Appointment => {
  const newId = Math.max(...appointments.map(a => a.id)) + 1
  const newAppointment: Appointment = {
    id: newId,
    appointmentNo: generateAppointmentNo(),
    userId: data.userId || 1,
    serviceId: data.serviceId || 1,
    merchantId: data.merchantId || 1,
    petId: data.petId || 1,
    appointmentTime: data.appointmentTime || '',
    status: 'pending',
    totalPrice: data.totalPrice || 0,
    notes: data.notes || '',
    createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
    updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
  }
  appointments.unshift(newAppointment)
  return newAppointment
}

export const updateAppointmentStatus = (id: number, status: string): Appointment | null => {
  const appointment = appointments.find(a => a.id === id)
  if (!appointment) return null
  
  appointment.status = status
  appointment.updatedAt = new Date().toISOString().replace('T', ' ').substring(0, 19)
  return appointment
}
