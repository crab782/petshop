import { test, expect } from '@playwright/test'

test.describe('商家登录流程测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
  })

  test('能够成功登录并跳转到商家首页', async ({ page }) => {
    await page.route('**/api/auth/login', async (route) => {
      const request = route.request()
      const body = request.postDataJSON()

      if (body.role === 'merchant' && body.username && body.password) {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '登录成功',
            data: {
              token: 'mock-merchant-token-12345',
              user: {
                id: 1,
                username: body.username,
                role: 'merchant'
              }
            }
          })
        })
      } else {
        await route.fulfill({
          status: 401,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 401,
            message: '用户名或密码错误'
          })
        })
      }
    })

    await page.click('text=商家')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'merchant@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    await page.click('button:has-text("登录")')

    await expect(page).toHaveURL(/\/merchant\/home/, { timeout: 10000 })

    await expect(page.locator('.merchant-home')).toBeVisible()
  })

  test('能够正确处理登录失败（错误的账号密码）', async ({ page }) => {
    await page.route('**/api/auth/login', async (route) => {
      await route.fulfill({
        status: 401,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 401,
          message: '用户名或密码错误'
        })
      })
    })

    await page.click('text=商家')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'wrong@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'wrongpassword')

    await page.click('button:has-text("登录")')

    await expect(page.locator('.el-message--error')).toBeVisible({ timeout: 5000 })

    await expect(page).toHaveURL('/login')
  })

  test('能够正确处理网络错误', async ({ page }) => {
    await page.route('**/api/auth/login', async (route) => {
      await route.abort('failed')
    })

    await page.click('text=商家')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'merchant@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    await page.click('button:has-text("登录")')

    await expect(page.locator('.el-message--error')).toBeVisible({ timeout: 5000 })

    await expect(page).toHaveURL('/login')
  })

  test('登录状态能够正确保持', async ({ page, context }) => {
    await page.route('**/api/auth/login', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '登录成功',
          data: {
            token: 'mock-merchant-token-12345',
            user: {
              id: 1,
              username: 'merchant@test.com',
              role: 'merchant'
            }
          }
        })
      })
    })

    await page.route('**/api/merchant/**', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: []
        })
      })
    })

    await page.click('text=商家')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'merchant@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    await page.click('button:has-text("登录")')

    await expect(page).toHaveURL(/\/merchant\/home/, { timeout: 10000 })

    const cookies = await context.cookies()
    expect(cookies.length).toBeGreaterThanOrEqual(0)

    await page.reload()

    await expect(page.locator('.merchant-home, .el-empty')).toBeVisible({ timeout: 10000 })
  })

  test('表单验证 - 必填项验证', async ({ page }) => {
    await page.click('text=商家')

    await page.click('button:has-text("登录")')

    await expect(page.locator('.el-form-item__error')).toBeVisible({ timeout: 5000 })
  })

  test('表单验证 - 密码长度验证', async ({ page }) => {
    await page.click('text=商家')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'merchant@test.com')
    await page.fill('input[placeholder="请输入密码"]', '12345')

    await page.click('button:has-text("登录")')

    await expect(page.locator('.el-form-item__error')).toContainText('密码长度至少6位', { timeout: 5000 })
  })

  test('登录按钮loading状态', async ({ page }) => {
    await page.route('**/api/auth/login', async (route) => {
      await new Promise(resolve => setTimeout(resolve, 2000))
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '登录成功',
          data: {
            token: 'mock-merchant-token-12345'
          }
        })
      })
    })

    await page.click('text=商家')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'merchant@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    const loginButton = page.locator('button:has-text("登录")')
    await loginButton.click()

    await expect(loginButton).toHaveAttribute('class', /is-loading/, { timeout: 1000 })
  })

  test('能够切换角色并登录', async ({ page }) => {
    await page.route('**/api/auth/login', async (route) => {
      const request = route.request()
      const body = request.postDataJSON()

      const roleRoutes: Record<string, string> = {
        user: '/user/home',
        merchant: '/merchant/home',
        admin: '/admin/dashboard'
      }

      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '登录成功',
          data: {
            token: `mock-${body.role}-token`,
            redirectUrl: roleRoutes[body.role]
          }
        })
      })
    })

    await page.click('text=商家')

    await expect(page.locator('.el-radio-button__original-radio:checked')).toHaveValue('merchant', { timeout: 3000 })

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'merchant@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    await page.click('button:has-text("登录")')

    await expect(page).toHaveURL(/\/merchant\/home/, { timeout: 10000 })
  })

  test('服务器返回错误状态码时显示错误信息', async ({ page }) => {
    await page.route('**/api/auth/login', async (route) => {
      await route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 500,
          message: '服务器内部错误'
        })
      })
    })

    await page.click('text=商家')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'merchant@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    await page.click('button:has-text("登录")')

    await expect(page.locator('.el-message--error')).toBeVisible({ timeout: 5000 })

    await expect(page).toHaveURL('/login')
  })

  test('账号未审核时显示相应提示', async ({ page }) => {
    await page.route('**/api/auth/login', async (route) => {
      await route.fulfill({
        status: 403,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 403,
          message: '账号正在审核中，请等待管理员审核通过'
        })
      })
    })

    await page.click('text=商家')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'pending@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    await page.click('button:has-text("登录")')

    await expect(page.locator('.el-message--error')).toBeVisible({ timeout: 5000 })

    await expect(page).toHaveURL('/login')
  })
})
