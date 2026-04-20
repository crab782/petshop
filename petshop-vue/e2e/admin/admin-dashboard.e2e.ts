import { test, expect } from '@playwright/test'

test('admin dashboard loads correctly', async ({ page }) => {
  await page.goto('/admin/dashboard')
  await expect(page).toHaveTitle(/Admin Dashboard/)
})
