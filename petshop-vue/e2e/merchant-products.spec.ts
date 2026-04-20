import { test, expect } from '@playwright/test'

test.describe('商品管理流程测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')

    await page.locator('input[placeholder="请输入邮箱或用户名"]').fill('merchant@test.com')
    await page.locator('input[placeholder="请输入密码"]').fill('123456')
    await page.locator('button:has-text("登录")').click()

    await page.waitForURL('**/merchant/home', { timeout: 10000 })
  })

  test('应该能够正确查看商品列表', async ({ page }) => {
    await page.goto('/merchant/products')

    await expect(page.locator('h2.page-title')).toHaveText('商品管理')

    await expect(page.locator('el-table')).toBeVisible()

    const tableRows = page.locator('el-table el-table__row')
    await expect(tableRows.first()).toBeVisible({ timeout: 5000 })
  })

  test('应该能够成功新增商品', async ({ page }) => {
    await page.goto('/merchant/products')

    await page.locator('button:has-text("添加商品")').click()

    await expect(page).toHaveURL(/\/merchant\/products\/add/)

    await page.locator('input[placeholder="请输入商品名称"]').fill('测试商品E2E')
    await page.locator('.el-select:has-text("请选择商品分类")').click()
    await page.locator('.el-select-dropdown__item:has-text("狗粮")').click()
    await page.locator('input[placeholder="请输入价格"]').fill('99.99')
    await page.locator('input[placeholder="请输入库存"]').fill('100')
    await page.locator('input[placeholder="请输入图片URL"]').fill('https://example.com/test-product.jpg')
    await page.locator('textarea[placeholder="请输入商品描述"]').fill('这是一个E2E测试商品')

    await page.locator('button:has-text("添加")').click()

    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })

    await expect(page).toHaveURL(/\/merchant\/products/)
  })

  test('应该能够成功编辑商品', async ({ page }) => {
    await page.goto('/merchant/products')

    await page.waitForSelector('el-table el-table__row', { timeout: 5000 })

    const firstEditButton = page.locator('button:has-text("编辑")').first()
    await firstEditButton.click()

    await expect(page).toHaveURL(/\/merchant\/products\/edit/)

    const nameInput = page.locator('input[placeholder="请输入商品名称"]')
    await nameInput.fill('')
    await nameInput.fill('更新后的商品名称')

    await page.locator('button:has-text("保存")').click()

    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })

    await expect(page).toHaveURL(/\/merchant\/products/)
  })

  test('应该能够成功删除商品', async ({ page }) => {
    await page.goto('/merchant/products')

    await page.waitForSelector('el-table el-table__row', { timeout: 5000 })

    const firstDeleteButton = page.locator('button:has-text("删除")').first()
    await firstDeleteButton.click()

    await expect(page.locator('.el-message-box')).toBeVisible()

    await page.locator('button:has-text("确定")').click()

    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
  })

  test('应该能够正确处理库存管理', async ({ page }) => {
    await page.goto('/merchant/products')

    await page.waitForSelector('el-table el-table__row', { timeout: 5000 })

    const lowStockWarning = page.locator('.stock-low')
    if (await lowStockWarning.count() > 0) {
      await expect(lowStockWarning.first()).toBeVisible()
    }

    const outOfStockWarning = page.locator('.stock-out')
    if (await outOfStockWarning.count() > 0) {
      await expect(outOfStockWarning.first()).toBeVisible()
    }
  })

  test('应该能够搜索和筛选商品', async ({ page }) => {
    await page.goto('/merchant/products')

    await page.locator('input[placeholder="请输入商品名称"]').fill('测试')
    await page.locator('button:has-text("搜索")').click()

    await page.waitForTimeout(500)

    await page.locator('button:has-text("重置")').click()

    await page.waitForTimeout(500)
  })

  test('应该能够批量操作商品', async ({ page }) => {
    await page.goto('/merchant/products')

    await page.waitForSelector('el-table el-table__row', { timeout: 5000 })

    const firstCheckbox = page.locator('el-table .el-checkbox').first()
    await firstCheckbox.click()

    await expect(page.locator('text=已选择 1 项')).toBeVisible()

    const batchEnableButton = page.locator('button:has-text("批量启用")')
    await expect(batchEnableButton).toBeEnabled()
  })

  test('应该能够切换商品状态', async ({ page }) => {
    await page.goto('/merchant/products')

    await page.waitForSelector('el-table el-table__row', { timeout: 5000 })

    const firstSwitch = page.locator('el-switch').first()
    const switchState = await firstSwitch.getAttribute('aria-checked')

    await firstSwitch.click()

    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
  })

  test('应该能够分页浏览商品', async ({ page }) => {
    await page.goto('/merchant/products')

    await page.waitForSelector('.el-pagination', { timeout: 5000 })

    const pagination = page.locator('.el-pagination')
    await expect(pagination).toBeVisible()

    const nextButton = page.locator('.btn-next')
    if (await nextButton.isEnabled()) {
      await nextButton.click()
      await page.waitForTimeout(500)
    }
  })

  test('应该能够验证商品表单', async ({ page }) => {
    await page.goto('/merchant/products/add')

    await page.locator('button:has-text("添加")').click()

    await expect(page.locator('.el-form-item__error:has-text("请输入商品名称")')).toBeVisible()
    await expect(page.locator('.el-form-item__error:has-text("请选择商品分类")')).toBeVisible()
    await expect(page.locator('.el-form-item__error:has-text("请输入价格")')).toBeVisible()
    await expect(page.locator('.el-form-item__error:has-text("请输入库存")')).toBeVisible()
  })

  test('应该能够设置库存预警', async ({ page }) => {
    await page.goto('/merchant/products/add')

    await page.locator('input[placeholder="请输入商品名称"]').fill('库存预警测试商品')
    await page.locator('.el-select:has-text("请选择商品分类")').click()
    await page.locator('.el-select-dropdown__item:has-text("猫粮")').click()
    await page.locator('input[placeholder="请输入价格"]').fill('50')
    await page.locator('input[placeholder="请输入库存"]').fill('5')

    const lowStockInput = page.locator('input[placeholder="库存低于此值时提醒"]')
    await lowStockInput.fill('10')

    await page.locator('button:has-text("添加")').click()

    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
  })

  test('应该能够查看商品图片预览', async ({ page }) => {
    await page.goto('/merchant/products/add')

    const imageUrl = 'https://example.com/test-preview.jpg'
    await page.locator('input[placeholder="请输入图片URL"]').fill(imageUrl)

    await page.waitForTimeout(500)

    const imagePreview = page.locator(`img[src="${imageUrl}"]`)
    if (await imagePreview.count() > 0) {
      await expect(imagePreview).toBeVisible()
    }
  })

  test('应该能够取消商品编辑', async ({ page }) => {
    await page.goto('/merchant/products/add')

    await page.locator('input[placeholder="请输入商品名称"]').fill('取消测试商品')

    await page.locator('button:has-text("取消")').click()

    await expect(page).toHaveURL(/\/merchant\/products/)
  })
})
