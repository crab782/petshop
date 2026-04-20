// 随机ID生成
export const randomId = (min = 1, max = 10000) => {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

// 随机日期生成
export const randomDate = (startDate, endDate) => {
  const start = startDate ? new Date(startDate) : new Date('2020-01-01')
  const end = endDate ? new Date(endDate) : new Date()
  const date = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()))
  return date.toISOString().replace('T', ' ').substring(0, 19)
}

// 随机枚举值选择
export const randomEnum = (arr) => {
  return arr[Math.floor(Math.random() * arr.length)]
}

// 随机字符串生成
export const randomString = (length = 10) => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return result
}

// 随机手机号
export const randomPhone = () => {
  return '1' + Math.floor(Math.random() * 9 + 1) + Math.floor(Math.random() * 1000000000).toString().padStart(9, '0')
}

// 随机邮箱
export const randomEmail = () => {
  const domains = ['qq.com', '163.com', 'gmail.com', 'outlook.com']
  const username = randomString(8).toLowerCase()
  return username + '@' + randomEnum(domains)
}

// 随机IP地址
export const randomIP = () => {
  return `${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}`
}