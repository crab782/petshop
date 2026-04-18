import { test, expect } from '@playwright/test'

test('admin dashboard loads correctly', async ({ page }) => {
  await page.goto('/admin/dashboard')
  await expect(page).toHaveTitle(/Admin Dashboard/)
})

test('dashboard displays statistics', async ({ page }) => {
  await page.goto('/admin/dashboard')
  
  // 检查统计数据卡片
  const statsCards = page.locator('.stats-grid .stat-item')
  await expect(statsCards).toHaveCount(4)
  
  // 检查总用户数
  const userStat = page.locator('.stats-grid .stat-item').nth(0)
  await expect(userStat).toContainText('总用户数')
  await expect(userStat).toContainText('2,580')
  
  // 检查总商家数
  const merchantStat = page.locator('.stats-grid .stat-item').nth(1)
  await expect(merchantStat).toContainText('总商家数')
  await expect(merchantStat).toContainText('128')
  
  // 检查本月营收
  const revenueStat = page.locator('.stats-grid .stat-item').nth(2)
  await expect(revenueStat).toContainText('本月营收')
  await expect(revenueStat).toContainText('¥ 156,800')
  
  // 检查今日订单
  const orderStat = page.locator('.stats-grid .stat-item').nth(3)
  await expect(orderStat).toContainText('今日订单')
  await expect(orderStat).toContainText('156')
})

test('dashboard displays recent users list', async ({ page }) => {
  await page.goto('/admin/dashboard')
  
  // 检查最近用户列表
  const usersTable = page.locator('.users-table el-table')
  await expect(usersTable).toBeVisible()
  
  // 检查用户数据
  const userRows = page.locator('.users-table el-table__row')
  await expect(userRows).toHaveCount(5)
  
  // 检查第一个用户数据
  const firstUser = userRows.nth(0)
  await expect(firstUser).toContainText('2580')
  await expect(firstUser).toContainText('张先生')
  await expect(firstUser).toContainText('138****1234')
  await expect(firstUser).toContainText('zhangsan@email.com')
  await expect(firstUser).toContainText('2026-04-18 10:30')
  
  // 检查操作按钮
  const viewButton = firstUser.locator('el-button:has-text("详情")')
  await expect(viewButton).toBeVisible()
})

test('dashboard displays pending merchants list', async ({ page }) => {
  await page.goto('/admin/dashboard')
  
  // 检查待审核商家列表
  const merchantsTable = page.locator('.merchants-table el-table')
  await expect(merchantsTable).toBeVisible()
  
  // 检查商家数据
  const merchantRows = page.locator('.merchants-table el-table__row')
  await expect(merchantRows).toHaveCount(3)
  
  // 检查第一个商家数据
  const firstMerchant = merchantRows.nth(0)
  await expect(firstMerchant).toContainText('128')
  await expect(firstMerchant).toContainText('宠物乐园')
  await expect(firstMerchant).toContainText('陈老板')
  await expect(firstMerchant).toContainText('138****1234')
  await expect(firstMerchant).toContainText('北京市朝阳区建国路88号')
  await expect(firstMerchant).toContainText('2026-04-17 15:30')
  
  // 检查操作按钮
  const passButton = firstMerchant.locator('el-button:has-text("通过")')
  const rejectButton = firstMerchant.locator('el-button:has-text("拒绝")')
  await expect(passButton).toBeVisible()
  await expect(rejectButton).toBeVisible()
})

test('dashboard displays system announcements', async ({ page }) => {
  await page.goto('/admin/dashboard')
  
  // 检查系统公告列表
  const announcementsList = page.locator('.announcements-list')
  await expect(announcementsList).toBeVisible()
  
  // 检查公告数据
  const announcementItems = page.locator('.announcement-item')
  await expect(announcementItems).toHaveCount(3)
  
  // 检查第一个公告数据
  const firstAnnouncement = announcementItems.nth(0)
  await expect(firstAnnouncement).toContainText('平台服务升级通知')
  await expect(firstAnnouncement).toContainText('平台将于本周日凌晨2:00-6:00进行系统升级')
  await expect(firstAnnouncement).toContainText('2026-04-18 08:00')
  await expect(firstAnnouncement).toContainText('系统管理员')
})
