import Mock from 'mockjs'
import { createSuccessResponse, createErrorResponse } from '../utils/generators'

export const setupAuthHandlers = () => {
  Mock.mock('/api/auth/password', 'put', (options: { body: string }) => {
    const { oldPassword, newPassword, confirmPassword } = JSON.parse(options.body) as {
      oldPassword: string
      newPassword: string
      confirmPassword: string
    }

    if (!oldPassword || !newPassword || !confirmPassword) {
      return createErrorResponse('请填写完整信息', 400)
    }

    if (oldPassword !== '123456') {
      return createErrorResponse('原密码错误', 400)
    }

    if (newPassword.length < 6) {
      return createErrorResponse('新密码长度不能少于6位', 400)
    }

    if (newPassword !== confirmPassword) {
      return createErrorResponse('两次输入的密码不一致', 400)
    }

    if (oldPassword === newPassword) {
      return createErrorResponse('新密码不能与原密码相同', 400)
    }

    return createSuccessResponse(null, '密码修改成功')
  })
}

export default setupAuthHandlers
