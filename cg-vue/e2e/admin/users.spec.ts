import { test, expect } from '@playwright/test'

test('admin users page loads correctly', async ({ page }) => {
  await page.goto('/admin/users')
  await expect(page).toHaveTitle(/用户管理/)
})

test('users list loads successfully', async ({ page }) => {
  await page.goto('/admin/users')
  
  // 检查用户列表表格
  const userTable = page.locator('.table-card el-table')
  await expect(userTable).toBeVisible()
  
  // 检查表格列
  const tableHeaders = page.locator('el-table__header th')
  await expect(tableHeaders).toHaveCount(8) // 选择框 + 7列
  
  // 检查表格数据加载
  const userRows = page.locator('el-table__row')
  await expect(userRows).toBeVisible()
  
  // 检查分页组件
  const pagination = page.locator('.pagination-wrapper el-pagination')
  await expect(pagination).toBeVisible()
})

test('search and filter functionality', async ({ page }) => {
  await page.goto('/admin/users')
  
  // 输入搜索关键词
  const searchInput = page.locator('el-input input[placeholder="请输入用户名或邮箱"]')
  await searchInput.fill('admin')
  
  // 点击搜索按钮
  const searchButton = page.locator('el-button:has-text("搜索")')
  await searchButton.click()
  
  // 检查搜索结果
  const userRows = page.locator('el-table__row')
  // 搜索结果应该包含admin用户
  const adminUser = page.locator('el-table__row:has-text("admin")')
  await expect(adminUser).toBeVisible()
  
  // 点击重置按钮
  const resetButton = page.locator('el-button:has-text("重置")')
  await resetButton.click()
  
  // 检查搜索框是否清空
  await expect(searchInput).toHaveValue('')
})

test('pagination functionality', async ({ page }) => {
  await page.goto('/admin/users')
  
  // 检查分页组件
  const pagination = page.locator('.pagination-wrapper el-pagination')
  await expect(pagination).toBeVisible()
  
  // 检查当前页码
  const currentPage = page.locator('.el-pager li.is-active')
  await expect(currentPage).toContainText('1')
  
  // 检查每页显示条数
  const pageSizeSelector = page.locator('.el-pagination__sizes select')
  await expect(pageSizeSelector).toHaveValue('10')
  
  // 尝试切换到第二页
  const nextPageButton = page.locator('.el-pagination__btn--next')
  await nextPageButton.click()
  
  // 检查是否切换到第二页
  await expect(currentPage).toContainText('2')
})

test('user operations', async ({ page }) => {
  await page.goto('/admin/users')
  
  // 等待用户列表加载
  await page.waitForSelector('el-table__row')
  
  // 获取第一个用户行
  const firstUserRow = page.locator('el-table__row').nth(0)
  
  // 检查详情按钮
  const viewButton = firstUserRow.locator('el-button:has-text("详情")')
  await expect(viewButton).toBeVisible()
  
  // 检查状态按钮
  const statusButton = firstUserRow.locator('el-button:has-text("禁用")').first()
  await expect(statusButton).toBeVisible()
  
  // 检查删除按钮
  const deleteButton = firstUserRow.locator('el-button:has-text("删除")')
  await expect(deleteButton).toBeVisible()
  
  // 测试详情按钮点击
  await viewButton.click()
  
  // 检查是否跳转到详情页面
  await expect(page).toHaveURL(/admin\/user\/detail/)
  
  // 返回用户列表页面
  await page.goBack()
  
  // 等待页面加载
  await page.waitForSelector('el-table__row')
  
  // 测试批量操作按钮
  const batchEnableButton = page.locator('el-button:has-text("批量启用")')
  const batchDisableButton = page.locator('el-button:has-text("批量禁用")')
  const batchDeleteButton = page.locator('el-button:has-text("批量删除")')
  
  await expect(batchEnableButton).toBeVisible()
  await expect(batchDisableButton).toBeVisible()
  await expect(batchDeleteButton).toBeVisible()
  
  // 检查批量操作按钮是否禁用（未选择用户）
  await expect(batchEnableButton).toBeDisabled()
  await expect(batchDisableButton).toBeDisabled()
  await expect(batchDeleteButton).toBeDisabled()
  
  // 选择第一个用户
  const checkbox = firstUserRow.locator('input[type="checkbox"]')
  await checkbox.click()
  
  // 检查批量操作按钮是否启用
  await expect(batchEnableButton).not.toBeDisabled()
  await expect(batchDisableButton).not.toBeDisabled()
  await expect(batchDeleteButton).not.toBeDisabled()
})
