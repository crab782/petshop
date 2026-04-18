import Mock from 'mockjs'
import { randomId, randomDate, randomEnum, randomString } from '../utils/random'

const TaskStatus = ['pending', 'running', 'completed', 'failed']
const TaskTypes = ['scheduled', 'onetime', 'recurring']

const generateTasks = (count = 15) => {
  const tasks = []
  for (let i = 0; i < count; i++) {
    tasks.push({
      id: randomId(),
      name: randomString(8) + '任务',
      type: randomEnum(TaskTypes),
      description: randomString(40),
      cronExpression: randomEnum(['0 0 * * *', '0 0 1 * *', '*/5 * * * *']),
      executeTime: randomDate('2024-01-01'),
      status: randomEnum(TaskStatus),
      result: randomEnum(['执行成功', '执行失败', null]),
      createdAt: randomDate('2024-01-01'),
      updatedAt: randomDate('2024-01-01')
    })
  }
  return tasks
}

const tasks = generateTasks(20)

Mock.mock('/api/admin/tasks', 'get', (options) => {
  const { page = 1, pageSize = 10, keyword, type, startDate, endDate, status } =
    options.url.split('?')[1]?.split('&').reduce((acc, param) => {
      const [key, value] = param.split('=')
      acc[key] = value
      return acc
    }, {}) || {}

  let filtered = [...tasks]

  if (keyword) {
    filtered = filtered.filter(t => t.name.includes(keyword))
  }
  if (type) {
    filtered = filtered.filter(t => t.type === type)
  }
  if (status) {
    filtered = filtered.filter(t => t.status === status)
  }

  const start = (page - 1) * pageSize
  const end = start + parseInt(pageSize)

  return {
    code: 200,
    message: 'success',
    data: filtered.slice(start, end),
    total: filtered.length
  }
})

Mock.mock('/api/admin/tasks', 'post', (options) => {
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '创建成功',
    data: { id: randomId(), ...data, createdAt: new Date().toISOString() }
  }
})

Mock.mock(/\/api\/admin\/tasks\/\d+/, 'put', () => ({
  code: 200,
  message: '更新成功',
  data: { success: true }
}))

Mock.mock(/\/api\/admin\/tasks\/\d+/, 'delete', () => ({
  code: 200,
  message: '删除成功',
  data: { success: true }
}))

Mock.mock(/\/api\/admin\/tasks\/\d+\/execute/, 'post', () => ({
  code: 200,
  message: '任务执行中',
  data: { success: true }
}))