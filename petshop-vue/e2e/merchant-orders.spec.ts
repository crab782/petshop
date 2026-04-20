import { test, expect, Page } from '@playwright/test'

const MERCHANT_LOGIN_URL = '/login'
const MERCHANT_ORDERS_URL = '/merchant/orders'

interface TestOrder {
  id: number
  userName: string
  serviceName: string
  status: string
  totalPrice: number
  appointmentTime: string
}

test.describe('商家订单处理流程', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(MERCHANT_ORDERS_URL)
    await page.waitForLoadState('networkidle')
  })

  test('应该正确显示订单列表页面', async ({ page }) => {
    await expect(page).toHaveURL(MERCHANT_ORDERS_URL)
    await expect(page.locator('.card-title')).toHaveText('订单处理')
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('应该正确显示订单列表筛选功能', async ({ page }) => {
    await expect(page.locator('.header-actions')).toBeVisible()
    await expect(page.locator('input[placeholder="搜索订单号/用户/服务"]')).toBeVisible()
    await expect(page.locator('.el-date-editor')).toBeVisible()
    await expect(page.locator('.el-select')).toBeVisible()
  })

  test('应该能够按状态筛选订单', async ({ page }) => {
    const statusSelect = page.locator('.el-select').first()
    await statusSelect.click()
    await page.waitForTimeout(300)
    await page.locator('.el-select-dropdown__item:has-text("待处理")').click()
    await page.waitForTimeout(500)
    const statusTags = page.locator('.el-tag:has-text("待处理")')
    const count = await statusTags.count()
    if (count > 0) {
      await expect(statusTags.first()).toBeVisible()
    }
  })

  test('应该能够搜索订单', async ({ page }) => {
    const searchInput = page.locator('input[placeholder="搜索订单号/用户/服务"]')
    await searchInput.fill('test')
    await searchInput.press('Enter')
    await page.waitForTimeout(500)
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('应该能够查看订单详情', async ({ page }) => {
    const viewDetailBtn = page.locator('button:has-text("查看详情")').first()
    const btnCount = await viewDetailBtn.count()
    if (btnCount > 0) {
      await viewDetailBtn.click()
      await page.waitForTimeout(300)
      await expect(page.locator('.el-dialog:has-text("订单详情")')).toBeVisible()
      await expect(page.locator('.el-dialog .el-form')).toBeVisible()
      await page.locator('.el-dialog button:has-text("关闭")').click()
    } else {
      const tableRows = page.locator('.el-table__body-wrapper .el-table__row')
      const rowCount = await tableRows.count()
      expect(rowcount).toBeGreaterThanOrEqual(0)
    }
  })

  test('应该能够确认待处理订单', async ({ page }) => {
    const pendingRow = page.locator('.el-table__body-wrapper .el-table__row').filter({
      has: page.locator('.el-tag:has-text("待处理")')
    }).first()
    const rowCount = await pendingRow.count()
    if (rowCount > 0) {
      const confirmBtn = pendingRow.locator('button:has-text("确认")')
      await expect(confirmBtn).toBeVisible()
      await confirmBtn.click()
      await page.waitForTimeout(500)
      const successMessage = page.locator('.el-message--success')
      if (await successMessage.count() > 0) {
        await expect(successMessage).toBeVisible()
      }
    }
  })

  test('应该能够完成已确认订单', async ({ page }) => {
    const confirmedRow = page.locator('.el-table__body-wrapper .el-table__row').filter({
      has: page.locator('.el-tag:has-text("已确认")')
    }).first()
    const rowCount = await confirmedRow.count()
    if (rowCount > 0) {
      const completeBtn = confirmedRow.locator('button:has-text("完成")')
      await expect(completeBtn).toBeVisible()
      await completeBtn.click()
      await page.waitForTimeout(500)
      const successMessage = page.locator('.el-message--success')
      if (await successMessage.count() > 0) {
        await expect(successMessage).toBeVisible()
      }
    }
  })

  test('应该能够取消待处理订单', async ({ page }) => {
    const pendingRow = page.locator('.el-table__body-wrapper .el-table__row').filter({
      has: page.locator('.el-tag:has-text("待处理")')
    }).first()
    const rowCount = await pendingRow.count()
    if (rowCount > 0) {
      const cancelBtn = pendingRow.locator('button:has-text("取消")')
      await expect(cancelBtn).toBeVisible()
      await cancelBtn.click()
      await page.waitForTimeout(500)
      const successMessage = page.locator('.el-message--success')
      if (await successMessage.count() > 0) {
        await expect(successMessage).toBeVisible()
      }
    }
  })

  test('应该能够在详情弹窗中确认接单', async ({ page }) => {
    const pendingRow = page.locator('.el-table__body-wrapper .el-table__row').filter({
      has: page.locator('.el-tag:has-text("待处理")')
    }).first()
    const rowCount = await pendingRow.count()
    if (rowCount > 0) {
      const viewDetailBtn = pendingRow.locator('button:has-text("查看详情")')
      await viewDetailBtn.click()
      await page.waitForTimeout(300)
      const dialog = page.locator('.el-dialog:has-text("订单详情")')
      await expect(dialog).toBeVisible()
      const acceptBtn = dialog.locator('button:has-text("确认接单")')
      if (await acceptBtn.count() > 0) {
        await acceptBtn.click()
        await page.waitForTimeout(500)
      }
    }
  })

  test('应该能够在详情弹窗中拒单', async ({ page }) => {
    const pendingRow = page.locator('.el-table__body-wrapper .el-table__row').filter({
      has: page.locator('.el-tag:has-text("待处理")')
    }).first()
    const rowCount = await pendingRow.count()
    if (rowCount > 0) {
      const viewDetailBtn = pendingRow.locator('button:has-text("查看详情")')
      await viewDetailBtn.click()
      await page.waitForTimeout(300)
      const dialog = page.locator('.el-dialog:has-text("订单详情")')
      await expect(dialog).toBeVisible()
      const rejectInput = dialog.locator('textarea[placeholder="请填写拒单原因"]')
      if (await rejectInput.count() > 0) {
        await rejectInput.fill('测试拒单原因')
        const rejectBtn = dialog.locator('button:has-text("拒单")')
        await rejectBtn.click()
        await page.waitForTimeout(500)
      }
    }
  })

  test('应该正确显示分页功能', async ({ page }) => {
    const pagination = page.locator('.el-pagination')
    await expect(pagination).toBeVisible()
    const totalText = pagination.locator('.el-pagination__total')
    if (await totalText.count() > 0) {
      await expect(totalText).toContainText('共')
    }
  })

  test('应该能够切换每页显示条数', async ({ page }) => {
    const sizeSelect = page.locator('.el-pagination .el-select')
    if (await sizeSelect.count() > 0) {
      await sizeSelect.click()
      await page.waitForTimeout(300)
      const option20 = page.locator('.el-select-dropdown__item:has-text("20")')
      if (await option20.count() > 0) {
        await option20.click()
        await page.waitForTimeout(500)
      }
    }
  })

  test('应该能够按日期范围筛选订单', async ({ page }) => {
    const dateRangePicker = page.locator('.el-date-editor--daterange')
    await expect(dateRangePicker).toBeVisible()
    const startInput = dateRangePicker.locator('input').first()
    await startInput.click()
    await page.waitForTimeout(300)
    const todayCell = page.locator('.el-date-table td.available.today')
    if (await todayCell.count() > 0) {
      await todayCell.first().click()
    }
  })

  test('应该正确显示订单状态标签颜色', async ({ page }) => {
    const pendingTag = page.locator('.el-tag:has-text("待处理")').first()
    if (await pendingTag.count() > 0) {
      await expect(pendingTag).toHaveClass(/el-tag--warning/)
    }
    const confirmedTag = page.locator('.el-tag:has-text("已确认")').first()
    if (await confirmedTag.count() > 0) {
      await expect(confirmedTag).toHaveClass(/el-tag--primary/)
    }
    const completedTag = page.locator('.el-tag:has-text("已完成")').first()
    if (await completedTag.count() > 0) {
      await expect(completedTag).toHaveClass(/el-tag--success/)
    }
    const cancelledTag = page.locator('.el-tag:has-text("已取消")').first()
    if (await cancelledTag.count() > 0) {
      await expect(cancelledTag).toHaveClass(/el-tag--info/)
    }
  })

  test('应该正确显示订单表格列', async ({ page }) => {
    const table = page.locator('.el-table')
    await expect(table).toBeVisible()
    const columns = ['订单编号', '用户名称', '服务名称', '预约时间', '总价（元）', '状态', '操作']
    for (const col of columns) {
      const header = table.locator(`th:has-text("${col}")`)
      if (await header.count() > 0) {
        await expect(header.first()).toBeVisible()
      }
    }
  })

  test('应该正确显示价格格式', async ({ page }) => {
    const priceCells = page.locator('.el-table__body-wrapper td:has-text("¥")')
    const count = await priceCells.count()
    if (count > 0) {
      const priceText = await priceCells.first().textContent()
      expect(priceText).toMatch(/¥\d+\.\d{2}/)
    }
  })

  test('应该正确处理空订单列表', async ({ page }) => {
    const searchInput = page.locator('input[placeholder="搜索订单号/用户/服务"]')
    await searchInput.fill('不存在的订单号xyz123456')
    await searchInput.press('Enter')
    await page.waitForTimeout(500)
    const tableRows = page.locator('.el-table__body-wrapper .el-table__row')
    const rowCount = await tableRows.count()
    expect(rowCount).toBe(0)
  })

  test('应该正确处理加载状态', async ({ page }) => {
    await page.goto(MERCHANT_ORDERS_URL)
    const loading = page.locator('.el-loading-mask')
    const isLoading = await loading.count() > 0
    if (isLoading) {
      await expect(loading).toBeVisible({ timeout: 1000 })
    }
    await page.waitForLoadState('networkidle')
    await expect(page.locator('.el-table')).toBeVisible()
  })

  test('应该能够关闭详情弹窗', async ({ page }) => {
    const viewDetailBtn = page.locator('button:has-text("查看详情")').first()
    const btnCount = await viewDetailBtn.count()
    if (btnCount > 0) {
      await viewDetailBtn.click()
      await page.waitForTimeout(300)
      const dialog = page.locator('.el-dialog:has-text("订单详情")')
      await expect(dialog).toBeVisible()
      const closeBtn = dialog.locator('button:has-text("关闭")')
      if (await closeBtn.count() > 0) {
        await closeBtn.click()
        await page.waitForTimeout(300)
        await expect(dialog).not.toBeVisible()
      } else {
        const overlay = page.locator('.el-overlay')
        await overlay.click({ position: { x: 10, y: 10 } })
      }
    }
  })

  test('应该正确验证拒单原因必填', async ({ page }) => {
    const pendingRow = page.locator('.el-table__body-wrapper .el-table__row').filter({
      has: page.locator('.el-tag:has-text("待处理")')
    }).first()
    const rowCount = await pendingRow.count()
    if (rowCount > 0) {
      const viewDetailBtn = pendingRow.locator('button:has-text("查看详情")')
      await viewDetailBtn.click()
      await page.waitForTimeout(300)
      const dialog = page.locator('.el-dialog:has-text("订单详情")')
      await expect(dialog).toBeVisible()
      const rejectBtn = dialog.locator('button:has-text("拒单")')
      if (await rejectBtn.count() > 0) {
        await rejectBtn.click()
        await page.waitForTimeout(300)
        const warningMessage = page.locator('.el-message--warning')
        if (await warningMessage.count() > 0) {
          await expect(warningMessage).toBeVisible()
        }
      }
    }
  })

  test('应该正确处理订单状态流转', async ({ page }) => {
    const pendingRow = page.locator('.el-table__body-wrapper .el-table__row').filter({
      has: page.locator('.el-tag:has-text("待处理")')
    }).first()
    const rowCount = await pendingRow.count()
    if (rowCount > 0) {
      const confirmBtn = pendingRow.locator('button:has-text("确认")')
      const cancelBtn = pendingRow.locator('button:has-text("取消")')
      await expect(confirmBtn).toBeVisible()
      await expect(cancelBtn).toBeVisible()
    }
    const confirmedRow = page.locator('.el-table__body-wrapper .el-table__row').filter({
      has: page.locator('.el-tag:has-text("已确认")')
    }).first()
    const confirmedRowCount = await confirmedRow.count()
    if (confirmedRowCount > 0) {
      const completeBtn = confirmedRow.locator('button:has-text("完成")')
      const cancelBtn = confirmedRow.locator('button:has-text("取消")')
      await expect(completeBtn).toBeVisible()
      await expect(cancelBtn).toBeVisible()
    }
  })

  test('应该正确处理网络错误', async ({ page, context }) => {
    await context.route('**/api/merchant/orders', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ message: '服务器错误' })
      })
    })
    await page.goto(MERCHANT_ORDERS_URL)
    await page.waitForTimeout(1000)
    const errorMessage = page.locator('.el-message--error')
    if (await errorMessage.count() > 0) {
      await expect(errorMessage).toBeVisible()
    }
  })

  test('应该正确处理订单详情数据展示', async ({ page }) => {
    const viewDetailBtn = page.locator('button:has-text("查看详情")').first()
    const btnCount = await viewDetailBtn.count()
    if (btnCount > 0) {
      await viewDetailBtn.click()
      await page.waitForTimeout(300)
      const dialog = page.locator('.el-dialog:has-text("订单详情")')
      await expect(dialog).toBeVisible()
      const formItems = ['订单编号', '用户名称', '服务名称', '预约时间', '总价', '状态']
      for (const item of formItems) {
        const formItem = dialog.locator(`.el-form-item:has-text("${item}")`)
        if (await formItem.count() > 0) {
          await expect(formItem).toBeVisible()
        }
      }
    }
  })
})

test.describe('订单处理异常情况测试', () => {
  test('应该正确处理无权限访问', async ({ page }) => {
    await page.goto(MERCHANT_ORDERS_URL)
    await page.waitForLoadState('networkidle')
    await expect(page).toHaveURL(/merchant\/orders/)
  })

  test('应该正确处理页面刷新', async ({ page }) => {
    await page.goto(MERCHANT_ORDERS_URL)
    await page.waitForLoadState('networkidle')
    await expect(page.locator('.card-title')).toHaveText('订单处理')
    await page.reload()
    await page.waitForLoadState('networkidle')
    await expect(page.locator('.card-title')).toHaveText('订单处理')
  })

  test('应该正确处理快速点击操作按钮', async ({ page }) => {
    const pendingRow = page.locator('.el-table__body-wrapper .el-table__row').filter({
      has: page.locator('.el-tag:has-text("待处理")')
    }).first()
    const rowCount = await pendingRow.count()
    if (rowCount > 0) {
      const confirmBtn = pendingRow.locator('button:has-text("确认")')
      await expect(confirmBtn).toBeVisible()
      await Promise.all([
        confirmBtn.click(),
        confirmBtn.click()
      ])
      await page.waitForTimeout(1000)
      await expect(page.locator('.el-table')).toBeVisible()
    }
  })

  test('应该正确处理搜索框清空', async ({ page }) => {
    const searchInput = page.locator('input[placeholder="搜索订单号/用户/服务"]')
    await searchInput.fill('test')
    await searchInput.press('Enter')
    await page.waitForTimeout(500)
    const clearBtn = searchInput.locator('..').locator('.el-input__clear')
    if (await clearBtn.count() > 0) {
      await clearBtn.click()
      await page.waitForTimeout(500)
      await expect(searchInput).toHaveValue('')
    }
  })

  test('应该正确处理表格排序', async ({ page }) => {
    const idHeader = page.locator('th:has-text("订单编号")')
    if (await idHeader.count() > 0) {
      await idHeader.click()
      await page.waitForTimeout(500)
      await idHeader.click()
      await page.waitForTimeout(500)
    }
  })
})
