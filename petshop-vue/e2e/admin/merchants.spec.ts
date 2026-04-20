import { test, expect } from '@playwright/test'

test.describe('平台端商家管理页面测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/admin/merchants')
  })

  test('商家管理页面加载正确', async ({ page }) => {
    await expect(page.locator('.merchants-page')).toBeVisible()
    await expect(page.locator('.search-card')).toBeVisible()
    await expect(page.locator('.table-card')).toBeVisible()
  })

  test('商家列表渲染正确', async ({ page }) => {
    const table = page.locator('.table-card .el-table')
    await expect(table).toBeVisible()

    const tableHeaders = page.locator('.el-table__header-wrapper .el-table__cell')
    await expect(tableHeaders.first()).toBeVisible()

    const merchantRows = page.locator('.el-table__body-wrapper .el-table__row')
    await expect(merchantRows.first()).toBeVisible()

    const pagination = page.locator('.pagination-wrapper .el-pagination')
    await expect(pagination).toBeVisible()
  })

  test('搜索功能正常工作', async ({ page }) => {
    const searchInput = page.locator('.search-card input[placeholder="请输入商家名称/联系人/手机号"]')
    await expect(searchInput).toBeVisible()

    await searchInput.fill('宠物')

    const searchButton = page.locator('.search-card .el-button:has-text("搜索")')
    await searchButton.click()

    await page.waitForTimeout(500)

    const merchantRows = page.locator('.el-table__body-wrapper .el-table__row')
    await expect(merchantRows.first()).toBeVisible()
  })

  test('重置功能正常工作', async ({ page }) => {
    const searchInput = page.locator('.search-card input[placeholder="请输入商家名称/联系人/手机号"]')
    await searchInput.fill('test')

    const statusFilter = page.locator('.search-card .el-select')
    await statusFilter.click()
    await page.locator('.el-select-dropdown__item:has-text("待审核")').click()

    const resetButton = page.locator('.search-card .el-button:has-text("重置")')
    await resetButton.click()

    await expect(searchInput).toHaveValue('')
  })

  test('状态筛选功能正常工作', async ({ page }) => {
    const statusFilter = page.locator('.search-card .el-select')
    await statusFilter.click()

    const pendingOption = page.locator('.el-select-dropdown__item:has-text("待审核")')
    await expect(pendingOption).toBeVisible()
    await pendingOption.click()

    const searchButton = page.locator('.search-card .el-button:has-text("搜索")')
    await searchButton.click()

    await page.waitForTimeout(500)

    const merchantRows = page.locator('.el-table__body-wrapper .el-table__row')
    await expect(merchantRows.first()).toBeVisible()
  })

  test('商家详情按钮可用', async ({ page }) => {
    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstMerchantRow = page.locator('.el-table__body-wrapper .el-table__row').first()
    await expect(firstMerchantRow).toBeVisible()

    const viewButton = firstMerchantRow.locator('.el-button:has-text("详情")')
    await expect(viewButton).toBeVisible()
  })

  test('商家详情对话框显示正确', async ({ page }) => {
    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstMerchantRow = page.locator('.el-table__body-wrapper .el-table__row').first()
    const viewButton = firstMerchantRow.locator('.el-button:has-text("详情")')
    await viewButton.click()

    const dialog = page.locator('.el-dialog:has-text("商家详情")')
    await expect(dialog).toBeVisible()

    const closeButton = dialog.locator('.el-button:has-text("关闭")')
    await expect(closeButton).toBeVisible()
    await closeButton.click()

    await expect(dialog).not.toBeVisible()
  })

  test('审核通过按钮可用', async ({ page }) => {
    const statusFilter = page.locator('.search-card .el-select')
    await statusFilter.click()
    await page.locator('.el-select-dropdown__item:has-text("待审核")').click()

    const searchButton = page.locator('.search-card .el-button:has-text("搜索")')
    await searchButton.click()

    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstMerchantRow = page.locator('.el-table__body-wrapper .el-table__row').first()
    const approveButton = firstMerchantRow.locator('.el-button:has-text("通过")')
    const count = await approveButton.count()
    
    if (count > 0) {
      await expect(approveButton).toBeVisible()
    }
  })

  test('审核拒绝按钮可用', async ({ page }) => {
    const statusFilter = page.locator('.search-card .el-select')
    await statusFilter.click()
    await page.locator('.el-select-dropdown__item:has-text("待审核")').click()

    const searchButton = page.locator('.search-card .el-button:has-text("搜索")')
    await searchButton.click()

    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstMerchantRow = page.locator('.el-table__body-wrapper .el-table__row').first()
    const rejectButton = firstMerchantRow.locator('.el-button:has-text("拒绝")')
    const count = await rejectButton.count()
    
    if (count > 0) {
      await expect(rejectButton).toBeVisible()
    }
  })

  test('商家删除按钮可用', async ({ page }) => {
    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstMerchantRow = page.locator('.el-table__body-wrapper .el-table__row').first()

    const deleteButton = firstMerchantRow.locator('.el-button:has-text("删除")')
    await expect(deleteButton).toBeVisible()
  })

  test('批量操作按钮初始状态', async ({ page }) => {
    const batchAuditButton = page.locator('.batch-actions .el-button:has-text("批量审核")')
    const batchEnableButton = page.locator('.batch-actions .el-button:has-text("批量启用")')
    const batchDisableButton = page.locator('.batch-actions .el-button:has-text("批量禁用")')
    const batchDeleteButton = page.locator('.batch-actions .el-button:has-text("批量删除")')

    await expect(batchAuditButton).toBeVisible()
    await expect(batchEnableButton).toBeVisible()
    await expect(batchDisableButton).toBeVisible()
    await expect(batchDeleteButton).toBeVisible()

    await expect(batchAuditButton).toBeDisabled()
    await expect(batchEnableButton).toBeDisabled()
    await expect(batchDisableButton).toBeDisabled()
    await expect(batchDeleteButton).toBeDisabled()
  })

  test('选择商家后批量操作按钮启用', async ({ page }) => {
    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstCheckbox = page.locator('.el-table__body-wrapper .el-checkbox').first()
    await firstCheckbox.click()

    const batchAuditButton = page.locator('.batch-actions .el-button:has-text("批量审核")')
    const batchEnableButton = page.locator('.batch-actions .el-button:has-text("批量启用")')
    const batchDisableButton = page.locator('.batch-actions .el-button:has-text("批量禁用")')
    const batchDeleteButton = page.locator('.batch-actions .el-button:has-text("批量删除")')

    await expect(batchAuditButton).not.toBeDisabled()
    await expect(batchEnableButton).not.toBeDisabled()
    await expect(batchDisableButton).not.toBeDisabled()
    await expect(batchDeleteButton).not.toBeDisabled()
  })

  test('分页功能正常工作', async ({ page }) => {
    const pagination = page.locator('.pagination-wrapper .el-pagination')
    await expect(pagination).toBeVisible()

    const currentPage = page.locator('.el-pager .is-active')
    await expect(currentPage).toBeVisible()

    const totalText = page.locator('.el-pagination__total')
    await expect(totalText).toBeVisible()
  })

  test('面包屑导航显示正确', async ({ page }) => {
    const breadcrumb = page.locator('.el-breadcrumb')
    await expect(breadcrumb).toBeVisible()

    const homeLink = page.locator('.el-breadcrumb-item:has-text("首页")')
    await expect(homeLink).toBeVisible()

    const currentLink = page.locator('.el-breadcrumb-item:has-text("商家管理")')
    await expect(currentLink).toBeVisible()
  })

  test('商家状态标签显示正确', async ({ page }) => {
    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const statusTag = page.locator('.el-table__body-wrapper .el-tag').first()
    await expect(statusTag).toBeVisible()
  })

  test('拒绝原因对话框功能', async ({ page }) => {
    const statusFilter = page.locator('.search-card .el-select')
    await statusFilter.click()
    await page.locator('.el-select-dropdown__item:has-text("待审核")').click()

    const searchButton = page.locator('.search-card .el-button:has-text("搜索")')
    await searchButton.click()

    await page.waitForSelector('.el-table__body-wrapper .el-table__row')

    const firstMerchantRow = page.locator('.el-table__body-wrapper .el-table__row').first()
    const rejectButton = firstMerchantRow.locator('.el-button:has-text("拒绝")')
    
    if (await rejectButton.count() > 0) {
      await rejectButton.click()

      const rejectDialog = page.locator('.el-dialog:has-text("拒绝原因")')
      await expect(rejectDialog).toBeVisible()

      const cancelButton = rejectDialog.locator('.el-button:has-text("取消")')
      await cancelButton.click()

      await expect(rejectDialog).not.toBeVisible()
    }
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
