import { test, expect } from '@playwright/test'

test('admin merchants page loads correctly', async ({ page }) => {
  await page.goto('/admin/merchants')
  await expect(page).toHaveTitle(/商家管理/)
})

test('merchants list loads successfully', async ({ page }) => {
  await page.goto('/admin/merchants')
  
  // 检查商家列表表格
  const merchantTable = page.locator('.table-card el-table')
  await expect(merchantTable).toBeVisible()
  
  // 检查表格列
  const tableHeaders = page.locator('el-table__header th')
  await expect(tableHeaders).toHaveCount(9) // 选择框 + 8列
  
  // 检查表格数据加载
  const merchantRows = page.locator('el-table__row')
  await expect(merchantRows).toBeVisible()
  
  // 检查分页组件
  const pagination = page.locator('.pagination-wrapper el-pagination')
  await expect(pagination).toBeVisible()
})

test('merchant audit operations', async ({ page }) => {
  await page.goto('/admin/merchants')
  
  // 等待商家列表加载
  await page.waitForSelector('el-table__row')
  
  // 筛选待审核的商家
  const statusFilter = page.locator('el-select:has-text("全部状态")')
  await statusFilter.click()
  await page.locator('el-option:has-text("待审核")').click()
  
  // 点击搜索按钮
  const searchButton = page.locator('el-button:has-text("搜索")')
  await searchButton.click()
  
  // 等待筛选结果
  await page.waitForSelector('el-table__row')
  
  // 获取第一个待审核商家
  const firstMerchantRow = page.locator('el-table__row').nth(0)
  
  // 检查通过按钮
  const approveButton = firstMerchantRow.locator('el-button:has-text("通过")')
  await expect(approveButton).toBeVisible()
  
  // 检查拒绝按钮
  const rejectButton = firstMerchantRow.locator('el-button:has-text("拒绝")')
  await expect(rejectButton).toBeVisible()
  
  // 检查详情按钮
  const viewButton = firstMerchantRow.locator('el-button:has-text("详情")')
  await expect(viewButton).toBeVisible()
  
  // 检查删除按钮
  const deleteButton = firstMerchantRow.locator('el-button:has-text("删除")')
  await expect(deleteButton).toBeVisible()
  
  // 测试详情按钮点击
  await viewButton.click()
  
  // 检查详情对话框
  const detailDialog = page.locator('el-dialog:has-text("商家详情")')
  await expect(detailDialog).toBeVisible()
  
  // 关闭详情对话框
  const closeButton = detailDialog.locator('el-button:has-text("关闭")')
  await closeButton.click()
  
  // 等待对话框关闭
  await page.waitForSelector('el-dialog:has-text("商家详情")', { state: 'hidden' })
})

test('search and filter functionality', async ({ page }) => {
  await page.goto('/admin/merchants')
  
  // 输入搜索关键词
  const searchInput = page.locator('el-input input[placeholder="请输入商家名称/联系人/手机号"]')
  await searchInput.fill('宠物')
  
  // 点击搜索按钮
  const searchButton = page.locator('el-button:has-text("搜索")')
  await searchButton.click()
  
  // 检查搜索结果
  const merchantRows = page.locator('el-table__row')
  // 搜索结果应该包含宠物相关的商家
  const petMerchant = page.locator('el-table__row:has-text("宠物")')
  await expect(petMerchant).toBeVisible()
  
  // 测试状态筛选
  const statusFilter = page.locator('el-select:has-text("全部状态")')
  await statusFilter.click()
  await page.locator('el-option:has-text("已通过")').click()
  
  // 点击搜索按钮
  await searchButton.click()
  
  // 检查筛选结果
  const approvedMerchants = page.locator('el-table__row el-tag:has-text("已通过")')
  await expect(approvedMerchants).toBeVisible()
  
  // 点击重置按钮
  const resetButton = page.locator('el-button:has-text("重置")')
  await resetButton.click()
  
  // 检查搜索框和筛选是否清空
  await expect(searchInput).toHaveValue('')
  await expect(statusFilter).toContainText('全部状态')
})

test('batch operations', async ({ page }) => {
  await page.goto('/admin/merchants')
  
  // 等待商家列表加载
  await page.waitForSelector('el-table__row')
  
  // 测试批量操作按钮
  const batchAuditButton = page.locator('el-button:has-text("批量审核")')
  const batchEnableButton = page.locator('el-button:has-text("批量启用")')
  const batchDisableButton = page.locator('el-button:has-text("批量禁用")')
  const batchDeleteButton = page.locator('el-button:has-text("批量删除")')
  
  await expect(batchAuditButton).toBeVisible()
  await expect(batchEnableButton).toBeVisible()
  await expect(batchDisableButton).toBeVisible()
  await expect(batchDeleteButton).toBeVisible()
  
  // 检查批量操作按钮是否禁用（未选择商家）
  await expect(batchAuditButton).toBeDisabled()
  await expect(batchEnableButton).toBeDisabled()
  await expect(batchDisableButton).toBeDisabled()
  await expect(batchDeleteButton).toBeDisabled()
  
  // 选择第一个商家
  const firstMerchantRow = page.locator('el-table__row').nth(0)
  const checkbox = firstMerchantRow.locator('input[type="checkbox"]')
  await checkbox.click()
  
  // 检查批量操作按钮是否启用
  await expect(batchAuditButton).not.toBeDisabled()
  await expect(batchEnableButton).not.toBeDisabled()
  await expect(batchDisableButton).not.toBeDisabled()
  await expect(batchDeleteButton).not.toBeDisabled()
})
