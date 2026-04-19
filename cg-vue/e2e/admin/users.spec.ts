import { test, expect } from '@playwright/test'

test.describe('平台端用户管理页面测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/admin/users')
  })

  test('用户管理页面加载正确', async ({ page }) => {
    await expect(page.locator('.user-management')).toBeVisible()
    await expect(page.locator('.search-card')).toBeVisible()
    await expect(page.locator('.table-card')).toBeVisible()
  })

  test('用户列表渲染正确', async ({ page }) => {
    const table = page.locator('.table-card .el-table')
    await expect(table).toBeVisible()

    const tableHeaders = page.locator('.el-table__header-wrapper .el-table__cell')
    await expect(tableHeaders.first()).toBeVisible()

    const userRows = page.locator('.el-table__body-wrapper .el-table__row')
    await expect(userRows.first()).toBeVisible()

    const pagination = page.locator('.pagination-wrapper .el-pagination')
    await expect(pagination).toBeVisible()
  })

  test('搜索功能正常工作', async ({ page }) => {
    const searchInput = page.locator('.search-card input[placeholder="请输入用户名或邮箱"]')
    await expect(searchInput).toBeVisible()

    await searchInput.fill('admin')

    const searchButton = page.locator('.search-card .el-button:has-text("搜索")')
    await searchButton.click()

    await page.waitForTimeout(500)

    const userRows = page.locator('.el-table__body-wrapper .el-table__row')
    await expect(userRows.first()).toBeVisible()
  })

  test('重置功能正常工作', async ({ page }) => {
    const searchInput = page.locator('.search-card input[placeholder="请输入用户名或邮箱"]')
    await searchInput.fill('test')

    const resetButton = page.locator('.search-card .el-button:has-text("重置")')
    await resetButton.click()

    await expect(searchInput).toHaveValue('')
  })

  test('分页功能正常工作', async ({ page }) => {
    const pagination = page.locator('.pagination-wrapper .el-pagination')
    await expect(pagination).toBeVisible()

    const currentPage = page.locator('.el-pager .is-active')
    await expect(currentPage).toBeVisible()

    const nextButton = page.locator('.btn-next')
    const totalText = page.locator('.el-pagination__total')

    await expect(totalText).toBeVisible()
  })

  test('用户详情按钮可用', async ({ page }) => {
    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstUserRow = page.locator('.el-table__body-wrapper .el-table__row').first()
    await expect(firstUserRow).toBeVisible()

    const viewButton = firstUserRow.locator('.el-button:has-text("详情")')
    await expect(viewButton).toBeVisible()
  })

  test('用户状态切换按钮可用', async ({ page }) => {
    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstUserRow = page.locator('.el-table__body-wrapper .el-table__row').first()

    const disableButton = firstUserRow.locator('.el-button:has-text("禁用")')
    const enableButton = firstUserRow.locator('.el-button:has-text("启用")')

    const hasDisableButton = await disableButton.count() > 0
    const hasEnableButton = await enableButton.count() > 0

    expect(hasDisableButton || hasEnableButton).toBeTruthy()
  })

  test('用户删除按钮可用', async ({ page }) => {
    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstUserRow = page.locator('.el-table__body-wrapper .el-table__row').first()

    const deleteButton = firstUserRow.locator('.el-button:has-text("删除")')
    await expect(deleteButton).toBeVisible()
  })

  test('批量操作按钮初始状态', async ({ page }) => {
    const batchEnableButton = page.locator('.batch-actions .el-button:has-text("批量启用")')
    const batchDisableButton = page.locator('.batch-actions .el-button:has-text("批量禁用")')
    const batchDeleteButton = page.locator('.batch-actions .el-button:has-text("批量删除")')

    await expect(batchEnableButton).toBeVisible()
    await expect(batchDisableButton).toBeVisible()
    await expect(batchDeleteButton).toBeVisible()

    await expect(batchEnableButton).toBeDisabled()
    await expect(batchDisableButton).toBeDisabled()
    await expect(batchDeleteButton).toBeDisabled()
  })

  test('选择用户后批量操作按钮启用', async ({ page }) => {
    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstCheckbox = page.locator('.el-table__body-wrapper .el-checkbox').first()
    await firstCheckbox.click()

    const batchEnableButton = page.locator('.batch-actions .el-button:has-text("批量启用")')
    const batchDisableButton = page.locator('.batch-actions .el-button:has-text("批量禁用")')
    const batchDeleteButton = page.locator('.batch-actions .el-button:has-text("批量删除")')

    await expect(batchEnableButton).not.toBeDisabled()
    await expect(batchDisableButton).not.toBeDisabled()
    await expect(batchDeleteButton).not.toBeDisabled()
  })

  test('已选择数量显示正确', async ({ page }) => {
    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const selectedInfo = page.locator('.selected-info')
    await expect(selectedInfo).toContainText('已选择 0 项')

    const firstCheckbox = page.locator('.el-table__body-wrapper .el-checkbox').first()
    await firstCheckbox.click()

    await expect(selectedInfo).toContainText('已选择 1 项')
  })

  test('面包屑导航显示正确', async ({ page }) => {
    const breadcrumb = page.locator('.el-breadcrumb')
    await expect(breadcrumb).toBeVisible()

    const homeLink = page.locator('.el-breadcrumb-item:has-text("首页")')
    await expect(homeLink).toBeVisible()

    const currentLink = page.locator('.el-breadcrumb-item:has-text("用户管理")')
    await expect(currentLink).toBeVisible()
  })

  test('点击详情跳转到用户详情页', async ({ page }) => {
    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstUserRow = page.locator('.el-table__body-wrapper .el-table__row').first()
    const viewButton = firstUserRow.locator('.el-button:has-text("详情")')
    await viewButton.click()

    await expect(page).toHaveURL(/admin\/user\/detail/)
  })

  test('每页显示条数选择器可用', async ({ page }) => {
    const pageSizeSelector = page.locator('.el-pagination__sizes')
    await expect(pageSizeSelector).toBeVisible()
  })

  test('跳转页码输入框可用', async ({ page }) => {
    const jumper = page.locator('.el-pagination__jump')
    await expect(jumper).toBeVisible()
  })
})
