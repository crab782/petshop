import { test, expect, Page } from '@playwright/test'

const TEST_USER = {
  username: 'test_merchant',
  password: 'test123456',
  role: 'merchant'
}

const TEST_SERVICE = {
  name: '测试服务E2E',
  description: '这是一个E2E测试服务描述',
  price: 99.99,
  duration: 60,
  category: '美容',
  image: 'https://via.placeholder.com/200'
}

const UPDATED_SERVICE = {
  name: '更新后的服务E2E',
  description: '这是更新后的服务描述',
  price: 199.99,
  duration: 90
}

test.describe('商家服务管理流程测试', () => {
  test.beforeEach(async ({ page }) => {
    await loginAsMerchant(page)
  })

  test('应该能够正确查看服务列表', async ({ page }) => {
    await page.goto('/merchant/services')
    
    await expect(page.locator('.page-title')).toHaveText('服务管理')
    
    await expect(page.locator('.el-table')).toBeVisible()
    
    await expect(page.getByRole('button', { name: /添加服务/ })).toBeVisible()
    
    await expect(page.locator('.search-card')).toBeVisible()
  })

  test('应该能够成功新增服务', async ({ page }) => {
    await page.goto('/merchant/services')
    
    await page.getByRole('button', { name: /添加服务/ }).click()
    
    await expect(page.locator('.el-dialog')).toBeVisible()
    await expect(page.locator('.el-dialog__title')).toHaveText('添加服务')
    
    await page.locator('.el-dialog input[placeholder="请输入服务名称"]').fill(TEST_SERVICE.name)
    
    await page.locator('.el-dialog input[placeholder="请输入服务价格"]').fill(String(TEST_SERVICE.price))
    
    await page.locator('.el-dialog input[placeholder="请输入服务时长（分钟）"]').fill(String(TEST_SERVICE.duration))
    
    await page.locator('.el-dialog .el-select').click()
    await page.locator('.el-select-dropdown__item').first().click()
    
    await page.locator('.el-dialog textarea[placeholder="请输入服务描述"]').fill(TEST_SERVICE.description)
    
    await page.locator('.el-dialog input[placeholder="请输入图片URL"]').fill(TEST_SERVICE.image)
    
    await page.locator('.el-dialog').getByRole('button', { name: '确定' }).click()
    
    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
  })

  test('应该能够成功编辑服务', async ({ page }) => {
    await page.goto('/merchant/services')
    
    await page.waitForSelector('.el-table__body-wrapper tr', { timeout: 5000 })
    
    const firstEditButton = page.locator('.el-table__body-wrapper tr').first().getByRole('button', { name: '编辑' })
    await firstEditButton.click()
    
    await expect(page.locator('.el-dialog')).toBeVisible()
    await expect(page.locator('.el-dialog__title')).toHaveText('编辑服务')
    
    const nameInput = page.locator('.el-dialog input[placeholder="请输入服务名称"]')
    await nameInput.clear()
    await nameInput.fill(UPDATED_SERVICE.name)
    
    const priceInput = page.locator('.el-dialog input[placeholder="请输入服务价格"]')
    await priceInput.clear()
    await priceInput.fill(String(UPDATED_SERVICE.price))
    
    const durationInput = page.locator('.el-dialog input[placeholder="请输入服务时长（分钟）"]')
    await durationInput.clear()
    await durationInput.fill(String(UPDATED_SERVICE.duration))
    
    await page.locator('.el-dialog').getByRole('button', { name: '确定' }).click()
    
    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
  })

  test('应该能够成功删除服务', async ({ page }) => {
    await page.goto('/merchant/services')
    
    await page.waitForSelector('.el-table__body-wrapper tr', { timeout: 5000 })
    
    const rowCount = await page.locator('.el-table__body-wrapper tr').count()
    
    page.on('dialog', async dialog => {
      expect(dialog.message()).toContain('确定要删除服务')
      await dialog.accept()
    })
    
    const firstDeleteButton = page.locator('.el-table__body-wrapper tr').first().getByRole('button', { name: '删除' })
    await firstDeleteButton.click()
    
    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
    
    await page.waitForTimeout(500)
    const newRowCount = await page.locator('.el-table__body-wrapper tr').count()
    expect(newRowCount).toBeLessThanOrEqual(rowCount)
  })

  test('应该能够正确处理新增服务时的表单验证', async ({ page }) => {
    await page.goto('/merchant/services')
    
    await page.getByRole('button', { name: /添加服务/ }).click()
    
    await expect(page.locator('.el-dialog')).toBeVisible()
    
    await page.locator('.el-dialog').getByRole('button', { name: '确定' }).click()
    
    await expect(page.locator('.el-form-item__error')).toContainText('请输入服务名称')
  })

  test('应该能够正确处理搜索和筛选功能', async ({ page }) => {
    await page.goto('/merchant/services')
    
    await page.locator('input[placeholder="请输入服务名称"]').fill('测试')
    await page.getByRole('button', { name: '搜索' }).click()
    
    await page.waitForTimeout(500)
    
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('应该能够正确处理重置搜索功能', async ({ page }) => {
    await page.goto('/merchant/services')
    
    await page.locator('input[placeholder="请输入服务名称"]').fill('测试')
    await page.getByRole('button', { name: '重置' }).click()
    
    await expect(page.locator('input[placeholder="请输入服务名称"]')).toHaveValue('')
  })

  test('应该能够正确处理批量操作', async ({ page }) => {
    await page.goto('/merchant/services')
    
    await page.waitForSelector('.el-table__body-wrapper tr', { timeout: 5000 })
    
    const firstCheckbox = page.locator('.el-table__body-wrapper .el-checkbox').first()
    await firstCheckbox.click()
    
    await expect(page.locator('.batch-actions')).toBeVisible()
    await expect(page.locator('.selected-info')).toContainText('已选择')
  })

  test('应该能够正确处理分页功能', async ({ page }) => {
    await page.goto('/merchant/services')
    
    await expect(page.locator('.el-pagination')).toBeVisible()
    
    const totalText = await page.locator('.el-pagination__total').textContent()
    expect(totalText).toContain('共')
  })
})

test.describe('服务管理错误处理测试', () => {
  test('应该能够正确处理网络错误', async ({ page, context }) => {
    await context.route('**/api/merchant/services', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ message: '服务器错误' })
      })
    })
    
    await loginAsMerchant(page)
    await page.goto('/merchant/services')
    
    await expect(page.locator('.el-message--error')).toBeVisible({ timeout: 5000 })
  })

  test('应该能够正确处理空数据状态', async ({ page, context }) => {
    await context.route('**/api/merchant/services*', route => {
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          data: {
            list: [],
            total: 0
          }
        })
      })
    })
    
    await loginAsMerchant(page)
    await page.goto('/merchant/services')
    
    await expect(page.locator('.el-table__empty-text')).toBeVisible({ timeout: 5000 })
  })

  test('应该能够正确处理删除确认取消', async ({ page }) => {
    await loginAsMerchant(page)
    await page.goto('/merchant/services')
    
    await page.waitForSelector('.el-table__body-wrapper tr', { timeout: 5000 })
    
    page.on('dialog', async dialog => {
      await dialog.dismiss()
    })
    
    const firstDeleteButton = page.locator('.el-table__body-wrapper tr').first().getByRole('button', { name: '删除' })
    await firstDeleteButton.click()
    
    await page.waitForTimeout(500)
    await expect(page.locator('.el-message--success')).not.toBeVisible()
  })
})

test.describe('服务管理页面导航测试', () => {
  test('应该能够从商家首页导航到服务管理页面', async ({ page }) => {
    await loginAsMerchant(page)
    await page.goto('/merchant/home')
    
    await page.getByRole('link', { name: /服务管理/ }).or(
      page.getByRole('menuitem', { name: /服务管理/ })
    ).click()
    
    await expect(page).toHaveURL(/\/merchant\/services/)
    await expect(page.locator('.page-title')).toHaveText('服务管理')
  })

  test('应该能够从服务列表导航到服务编辑页面', async ({ page }) => {
    await loginAsMerchant(page)
    await page.goto('/merchant/services')
    
    await page.getByRole('button', { name: /添加服务/ }).click()
    
    await expect(page.locator('.el-dialog')).toBeVisible()
  })
})

async function loginAsMerchant(page: Page) {
  await page.goto('/login')
  
  await page.getByRole('radio', { name: '商家' }).click()
  
  await page.locator('input[placeholder="请输入邮箱或用户名"]').fill(TEST_USER.username)
  await page.locator('input[placeholder="请输入密码"]').fill(TEST_USER.password)
  
  await page.getByRole('button', { name: '登录' }).click()
  
  await page.waitForURL('**/merchant/**', { timeout: 5000 })
}
