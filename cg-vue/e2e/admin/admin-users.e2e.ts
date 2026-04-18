import { test, expect } from '@playwright/test'

test('admin users page loads correctly', async ({ page }) => {
  await page.goto('/admin/users')
  await expect(page).toHaveTitle(/Admin Users/)
})
