import { test, expect } from '@playwright/test'

test.describe('平台端仪表盘页面测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/admin/dashboard')
  })

  test('仪表盘页面加载正确', async ({ page }) => {
    await expect(page.locator('.admin-dashboard')).toBeVisible()
    await expect(page.locator('.welcome-section')).toBeVisible()
    await expect(page.locator('.welcome-title')).toContainText('欢迎回来')
  })

  test('统计数据显示正确', async ({ page }) => {
    const statsGrid = page.locator('.stats-grid')
    await expect(statsGrid).toBeVisible()

    const statItems = page.locator('.stats-grid .stat-item')
    await expect(statItems).toHaveCount(4)

    const userStat = statItems.nth(0)
    await expect(userStat).toBeVisible()

    const merchantStat = statItems.nth(1)
    await expect(merchantStat).toBeVisible()

    const revenueStat = statItems.nth(2)
    await expect(revenueStat).toBeVisible()

    const orderStat = statItems.nth(3)
    await expect(orderStat).toBeVisible()
  })

  test('最近注册用户列表显示正确', async ({ page }) => {
    const usersSection = page.locator('.users-section')
    await expect(usersSection).toBeVisible()

    const sectionTitle = usersSection.locator('.section-title')
    await expect(sectionTitle).toContainText('最近注册用户')

    const usersTable = page.locator('.users-table .el-table')
    await expect(usersTable).toBeVisible()

    const userRows = page.locator('.users-table .el-table__body-wrapper .el-table__row')
    await expect(userRows.first()).toBeVisible()

    const firstUser = userRows.first()
    const viewButton = firstUser.locator('.el-button:has-text("详情")')
    await expect(viewButton).toBeVisible()
  })

  test('待审核商家列表显示正确', async ({ page }) => {
    const merchantsSection = page.locator('.merchants-section')
    await expect(merchantsSection).toBeVisible()

    const sectionTitle = merchantsSection.locator('.section-title')
    await expect(sectionTitle).toContainText('待审核商家')

    const merchantsTable = page.locator('.merchants-table .el-table')
    await expect(merchantsTable).toBeVisible()

    const merchantRows = page.locator('.merchants-table .el-table__body-wrapper .el-table__row')
    await expect(merchantRows.first()).toBeVisible()

    const firstMerchant = merchantRows.first()
    const passButton = firstMerchant.locator('.el-button:has-text("通过")')
    const rejectButton = firstMerchant.locator('.el-button:has-text("拒绝")')
    await expect(passButton).toBeVisible()
    await expect(rejectButton).toBeVisible()
  })

  test('系统公告列表显示正确', async ({ page }) => {
    const announcementsSection = page.locator('.announcements-section')
    await expect(announcementsSection).toBeVisible()

    const sectionTitle = announcementsSection.locator('.section-title')
    await expect(sectionTitle).toContainText('系统公告')

    const announcementsList = page.locator('.announcements-list')
    await expect(announcementsList).toBeVisible()

    const announcementItems = page.locator('.announcement-item')
    await expect(announcementItems.first()).toBeVisible()

    const firstAnnouncement = announcementItems.first()
    const announcementTitle = firstAnnouncement.locator('.announcement-title')
    await expect(announcementTitle).toBeVisible()
  })

  test('快捷操作区域显示正确', async ({ page }) => {
    const quickActionsSection = page.locator('.quick-actions-section')
    await expect(quickActionsSection).toBeVisible()

    const sectionTitle = quickActionsSection.locator('.section-title')
    await expect(sectionTitle).toContainText('快捷操作')

    const quickActions = page.locator('.quick-action-item')
    await expect(quickActions).toHaveCount(4)

    const firstAction = quickActions.first()
    const actionTitle = firstAction.locator('.action-title')
    await expect(actionTitle).toBeVisible()
  })

  test('查看全部用户链接跳转', async ({ page }) => {
    const usersSection = page.locator('.users-section')
    const viewAllButton = usersSection.locator('.el-button:has-text("查看全部")')
    await expect(viewAllButton).toBeVisible()
    await viewAllButton.click()
    await expect(page).toHaveURL(/admin\/users/)
  })

  test('查看全部商家链接跳转', async ({ page }) => {
    const merchantsSection = page.locator('.merchants-section')
    const viewAllButton = merchantsSection.locator('.el-button:has-text("查看全部")')
    await expect(viewAllButton).toBeVisible()
    await viewAllButton.click()
    await expect(page).toHaveURL(/admin\/merchants\/audit/)
  })
})
