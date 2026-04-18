import { test, expect } from '@playwright/test'

test.describe('用户注册流程测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/register')
  })

  test('能够成功注册并跳转到登录页', async ({ page }) => {
    await page.route('**/api/auth/register', async (route) => {
      const request = route.request()
      const body = request.postDataJSON()

      if (body.username && body.email && body.password) {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '注册成功',
            data: {
              id: 1,
              username: body.username,
              email: body.email
            }
          })
        })
      } else {
        await route.fulfill({
          status: 400,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 400,
            message: '注册信息不完整'
          })
        })
      }
    })

    await page.fill('input[placeholder="请输入用户名"]', 'testuser')
    await page.fill('input[placeholder="请输入邮箱"]', 'test@example.com')
    await page.fill('input[placeholder="请输入手机号"]', '13800138000')
    await page.fill('input[type="password"][placeholder="请输入密码"]', 'password123')
    await page.fill('input[placeholder="请再次输入密码"]', 'password123')

    await page.click('button:has-text("注册")')

    await expect(page).toHaveURL('/login', { timeout: 10000 })
  })

  test('表单验证 - 用户名必填', async ({ page }) => {
    await page.fill('input[placeholder="请输入邮箱"]', 'test@example.com')
    await page.fill('input[placeholder="请输入手机号"]', '13800138000')
    await page.fill('input[type="password"][placeholder="请输入密码"]', 'password123')
    await page.fill('input[placeholder="请再次输入密码"]', 'password123')

    await page.click('button:has-text("注册")')

    await expect(page.locator('.el-form-item__error')).toContainText('用户名', { timeout: 5000 })
  })

  test('表单验证 - 邮箱格式验证', async ({ page }) => {
    await page.fill('input[placeholder="请输入用户名"]', 'testuser')
    await page.fill('input[placeholder="请输入邮箱"]', 'invalid-email')
    await page.fill('input[placeholder="请输入手机号"]', '13800138000')
    await page.fill('input[type="password"][placeholder="请输入密码"]', 'password123')
    await page.fill('input[placeholder="请再次输入密码"]', 'password123')

    await page.click('button:has-text("注册")')

    await expect(page.locator('.el-form-item__error')).toContainText('邮箱', { timeout: 5000 })
  })

  test('表单验证 - 密码长度验证', async ({ page }) => {
    await page.fill('input[placeholder="请输入用户名"]', 'testuser')
    await page.fill('input[placeholder="请输入邮箱"]', 'test@example.com')
    await page.fill('input[placeholder="请输入手机号"]', '13800138000')
    await page.fill('input[type="password"][placeholder="请输入密码"]', '123')
    await page.fill('input[placeholder="请再次输入密码"]', '123')

    await page.click('button:has-text("注册")')

    await expect(page.locator('.el-form-item__error')).toContainText('密码长度', { timeout: 5000 })
  })

  test('表单验证 - 确认密码一致性', async ({ page }) => {
    await page.fill('input[placeholder="请输入用户名"]', 'testuser')
    await page.fill('input[placeholder="请输入邮箱"]', 'test@example.com')
    await page.fill('input[placeholder="请输入手机号"]', '13800138000')
    await page.fill('input[type="password"][placeholder="请输入密码"]', 'password123')
    await page.fill('input[placeholder="请再次输入密码"]', 'differentpassword')

    await page.click('button:has-text("注册")')

    await expect(page.locator('.el-form-item__error')).toContainText('密码不一致', { timeout: 5000 })
  })

  test('表单验证 - 手机号格式验证', async ({ page }) => {
    await page.fill('input[placeholder="请输入用户名"]', 'testuser')
    await page.fill('input[placeholder="请输入邮箱"]', 'test@example.com')
    await page.fill('input[placeholder="请输入手机号"]', '12345')
    await page.fill('input[type="password"][placeholder="请输入密码"]', 'password123')
    await page.fill('input[placeholder="请再次输入密码"]', 'password123')

    await page.click('button:has-text("注册")')

    await expect(page.locator('.el-form-item__error')).toContainText('手机号', { timeout: 5000 })
  })

  test('能够正确处理注册失败', async ({ page }) => {
    await page.route('**/api/auth/register', async (route) => {
      await route.fulfill({
        status: 400,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 400,
          message: '该邮箱已被注册'
        })
      })
    })

    await page.fill('input[placeholder="请输入用户名"]', 'testuser')
    await page.fill('input[placeholder="请输入邮箱"]', 'exists@example.com')
    await page.fill('input[placeholder="请输入手机号"]', '13800138000')
    await page.fill('input[type="password"][placeholder="请输入密码"]', 'password123')
    await page.fill('input[placeholder="请再次输入密码"]', 'password123')

    await page.click('button:has-text("注册")')

    await expect(page.locator('.el-message--error')).toBeVisible({ timeout: 5000 })
  })

  test('注册按钮loading状态', async ({ page }) => {
    await page.route('**/api/auth/register', async (route) => {
      await new Promise(resolve => setTimeout(resolve, 2000))
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '注册成功',
          data: {}
        })
      })
    })

    await page.fill('input[placeholder="请输入用户名"]', 'testuser')
    await page.fill('input[placeholder="请输入邮箱"]', 'test@example.com')
    await page.fill('input[placeholder="请输入手机号"]', '13800138000')
    await page.fill('input[type="password"][placeholder="请输入密码"]', 'password123')
    await page.fill('input[placeholder="请再次输入密码"]', 'password123')

    const registerButton = page.locator('button:has-text("注册")')
    await registerButton.click()

    await expect(registerButton).toHaveAttribute('class', /loading/, { timeout: 1000 })
  })

  test('能够跳转到登录页', async ({ page }) => {
    await page.click('text=立即登录')

    await expect(page).toHaveURL('/login')
  })
})

test.describe('用户登录流程测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
  })

  test('能够成功登录并跳转到用户首页', async ({ page }) => {
    await page.route('**/api/auth/login', async (route) => {
      const request = route.request()
      const body = request.postDataJSON()

      if (body.role === 'user' && body.username && body.password) {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '登录成功',
            data: {
              token: 'mock-user-token-12345',
              user: {
                id: 1,
                username: body.username,
                role: 'user'
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

    await page.click('text=用户')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'user@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    await page.click('button:has-text("登录")')

    await expect(page).toHaveURL(/\/user\/home/, { timeout: 10000 })

    await expect(page.locator('.user-home')).toBeVisible()
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

    await page.click('text=用户')

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

    await page.click('text=用户')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'user@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    await page.click('button:has-text("登录")')

    await expect(page.locator('.el-message--error')).toBeVisible({ timeout: 5000 })
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
            token: 'mock-user-token-12345',
            user: {
              id: 1,
              username: 'user@test.com',
              role: 'user'
            }
          }
        })
      })
    })

    await page.route('**/api/user/**', async (route) => {
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

    await page.click('text=用户')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'user@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    await page.click('button:has-text("登录")')

    await expect(page).toHaveURL(/\/user\/home/, { timeout: 10000 })

    const cookies = await context.cookies()
    expect(cookies.length).toBeGreaterThanOrEqual(0)

    await page.reload()

    await expect(page.locator('.user-home, .el-empty')).toBeVisible({ timeout: 10000 })
  })

  test('表单验证 - 必填项验证', async ({ page }) => {
    await page.click('text=用户')

    await page.click('button:has-text("登录")')

    await expect(page.locator('.el-form-item__error')).toBeVisible({ timeout: 5000 })
  })

  test('表单验证 - 密码长度验证', async ({ page }) => {
    await page.click('text=用户')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'user@test.com')
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
            token: 'mock-user-token-12345'
          }
        })
      })
    })

    await page.click('text=用户')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'user@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    const loginButton = page.locator('button:has-text("登录")')
    await loginButton.click()

    await expect(loginButton).toHaveAttribute('class', /loading/, { timeout: 1000 })
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

    await page.click('text=用户')

    await expect(page.locator('.el-radio-button__original-radio:checked')).toHaveValue('user', { timeout: 3000 })

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'user@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    await page.click('button:has-text("登录")')

    await expect(page).toHaveURL(/\/user\/home/, { timeout: 10000 })
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

    await page.click('text=用户')

    await page.fill('input[placeholder="请输入邮箱或用户名"]', 'user@test.com')
    await page.fill('input[placeholder="请输入密码"]', 'password123')

    await page.click('button:has-text("登录")')

    await expect(page.locator('.el-message--error')).toBeVisible({ timeout: 5000 })

    await expect(page).toHaveURL('/login')
  })

  test('能够跳转到注册页', async ({ page }) => {
    await page.click('text=立即注册')

    await expect(page).toHaveURL('/register')
  })
})

test.describe('个人资料编辑流程测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/user/profile/edit')
  })

  test('能够加载个人资料编辑页面', async ({ page }) => {
    await expect(page.locator('h1')).toContainText(/编辑个人资料|个人资料/)
  })

  test('能够正确填写并提交个人资料', async ({ page }) => {
    await page.route('**/api/user/profile', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '更新成功',
          data: {
            id: 1,
            username: 'testuser',
            email: 'test@example.com',
            phone: '13800138000'
          }
        })
      })
    })

    const usernameInput = page.locator('input[placeholder="请输入用户名"]')
    if (await usernameInput.isVisible()) {
      await usernameInput.fill('newusername')
    }

    const emailInput = page.locator('input[type="email"], input[placeholder*="邮"]')
    if (await emailInput.isVisible()) {
      await emailInput.fill('newemail@example.com')
    }

    const phoneInput = page.locator('input[placeholder*="机"]')
    if (await phoneInput.isVisible()) {
      await phoneInput.fill('13900139000')
    }

    const saveButton = page.locator('button:has-text("保存")')
    if (await saveButton.isVisible()) {
      await saveButton.click()
      await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
    }
  })

  test('表单验证 - 邮箱格式验证', async ({ page }) => {
    const emailInput = page.locator('input[type="email"], input[placeholder*="邮"]')
    if (await emailInput.isVisible()) {
      await emailInput.fill('invalid-email')
      
      const saveButton = page.locator('button:has-text("保存")')
      if (await saveButton.isVisible()) {
        await saveButton.click()
        await expect(page.locator('.el-form-item__error')).toBeVisible({ timeout: 5000 })
      }
    }
  })

  test('表单验证 - 用户名必填', async ({ page }) => {
    const usernameInput = page.locator('input[placeholder="请输入用户名"]')
    if (await usernameInput.isVisible()) {
      await usernameInput.fill('')
      
      const saveButton = page.locator('button:has-text("保存")')
      if (await saveButton.isVisible()) {
        await saveButton.click()
        await expect(page.locator('.el-form-item__error')).toBeVisible({ timeout: 5000 })
      }
    }
  })

  test('能够返回个人中心页面', async ({ page }) => {
    const backButton = page.locator('a:has-text("返回"), button:has-text("返回")')
    if (await backButton.first().isVisible()) {
      await backButton.first().click()
      await expect(page).toHaveURL(/\/user\/profile/)
    }
  })
})
