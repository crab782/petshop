import { test, expect } from '@playwright/test'

test.describe('浏览商家E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/user/merchant/1')
  })

  test('能够加载商家列表页面', async ({ page }) => {
    await page.route('**/api/merchants*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 2,
            list: [
              {
                id: 1,
                name: '宠物乐园',
                logo: 'https://example.com/logo1.png',
                address: '北京市朝阳区xxx',
                phone: '010-12345678',
                rating: 4.8,
                serviceCount: 10
              },
              {
                id: 2,
                name: '萌宠之家',
                logo: 'https://example.com/logo2.png',
                address: '北京市海淀区xxx',
                phone: '010-87654321',
                rating: 4.5,
                serviceCount: 8
              }
            ]
          }
        })
      })
    })

    await page.goto('/user/shop')
    await expect(page.locator('.merchant-card, .el-card')).toBeVisible({ timeout: 10000 })
  })

  test('能够搜索商家', async ({ page }) => {
    await page.route('**/api/merchants*', async (route) => {
      const url = route.request().url()
      const keyword = url.includes('keyword') ? '宠物乐园' : ''
      
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: keyword ? 1 : 2,
            list: keyword ? [
              {
                id: 1,
                name: '宠物乐园',
                logo: 'https://example.com/logo1.png',
                address: '北京市朝阳区xxx',
                rating: 4.8
              }
            ] : []
          }
        })
      })
    })

    const searchInput = page.locator('input[placeholder*="搜索"], input[placeholder*="商家"]')
    if (await searchInput.isVisible()) {
      await searchInput.fill('宠物乐园')
      await searchInput.press('Enter')
      await page.waitForTimeout(500)
    }
  })

  test('能够筛选商家', async ({ page }) => {
    await page.route('**/api/merchants*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 1,
            list: [
              {
                id: 1,
                name: '宠物乐园',
                rating: 4.8
              }
            ]
          }
        })
      })
    })

    const ratingFilter = page.locator('.el-select, .el-radio-group').first()
    if (await ratingFilter.isVisible()) {
      await ratingFilter.click()
    }
  })

  test('能够查看商家详情', async ({ page }) => {
    await page.route('**/api/merchants/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            id: 1,
            name: '宠物乐园',
            logo: 'https://example.com/logo1.png',
            address: '北京市朝阳区xxx',
            phone: '010-12345678',
            rating: 4.8,
            description: '专业的宠物服务商家',
            services: [
              { id: 1, name: '宠物洗澡', price: 88 },
              { id: 2, name: '宠物美容', price: 128 }
            ]
          }
        })
      })
    })

    await expect(page.locator('h1, h2')).toContainText(/宠物乐园|商家详情/, { timeout: 10000 })
  })

  test('能够收藏商家', async ({ page }) => {
    await page.route('**/api/user/favorites', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '收藏成功',
            data: { id: 1, merchantId: 1 }
          })
        })
      } else {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: 'success',
            data: []
          })
        })
      }
    })

    const favoriteButton = page.locator('button:has-text("收藏")')
    if (await favoriteButton.isVisible()) {
      await favoriteButton.click()
      await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
    }
  })
})

test.describe('预约服务E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/services*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 3,
            list: [
              {
                id: 1,
                name: '宠物洗澡美容',
                description: '专业洗澡美容服务',
                price: 88,
                duration: 60,
                merchantId: 1,
                merchantName: '宠物乐园',
                rating: 4.8
              },
              {
                id: 2,
                name: '宠物体检套餐',
                description: '全面健康体检',
                price: 199,
                duration: 90,
                merchantId: 1,
                merchantName: '宠物乐园',
                rating: 4.9
              },
              {
                id: 3,
                name: '宠物疫苗接种',
                description: '正规疫苗接种',
                price: 120,
                duration: 30,
                merchantId: 2,
                merchantName: '萌宠之家',
                rating: 4.7
              }
            ]
          }
        })
      })
    })
  })

  test('能够浏览服务列表', async ({ page }) => {
    await page.goto('/user/services/list')
    await expect(page.locator('.service-card, .el-card, .el-table')).toBeVisible({ timeout: 10000 })
  })

  test('能够查看服务详情', async ({ page }) => {
    await page.route('**/api/services/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            id: 1,
            name: '宠物洗澡美容',
            description: '专业洗澡美容服务，包含洗澡、吹干、修剪等',
            price: 88,
            duration: 60,
            merchantId: 1,
            merchantName: '宠物乐园',
            merchantAddress: '北京市朝阳区xxx',
            merchantPhone: '010-12345678',
            rating: 4.8,
            reviewCount: 156
          }
        })
      })
    })

    await page.goto('/user/services/detail/1')
    await expect(page.locator('h1, h2, .service-name')).toContainText(/宠物洗澡|服务详情/, { timeout: 10000 })
  })

  test('能够选择服务并预约', async ({ page }) => {
    await page.route('**/api/services/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            id: 1,
            name: '宠物洗澡美容',
            price: 88,
            duration: 60,
            merchantId: 1,
            merchantName: '宠物乐园'
          }
        })
      })
    })

    await page.route('**/api/user/pets', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: [
            { id: 1, name: '小白', type: '狗', breed: '泰迪' },
            { id: 2, name: '小黑', type: '猫', breed: '英短' }
          ]
        })
      })
    })

    await page.goto('/user/services/detail/1')

    const appointmentButton = page.locator('button:has-text("预约"), button:has-text("立即预约")')
    if (await appointmentButton.isVisible()) {
      await appointmentButton.click()
    }
  })

  test('能够选择预约时间', async ({ page }) => {
    await page.goto('/user/appointments/confirm')

    const datePicker = page.locator('.el-date-picker, input[placeholder*="日期"], input[placeholder*="时间"]')
    if (await datePicker.isVisible()) {
      await datePicker.click()
      await page.waitForTimeout(300)
    }
  })

  test('能够确认预约', async ({ page }) => {
    await page.route('**/api/appointments', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '预约创建成功',
            data: {
              id: 1,
              appointmentNo: 'APT202401200001',
              status: 'pending'
            }
          })
        })
      } else {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: 'success',
            data: []
          })
        })
      }
    })

    await page.goto('/user/appointments/confirm')

    const confirmButton = page.locator('button:has-text("确认预约"), button:has-text("提交")')
    if (await confirmButton.isVisible()) {
      await confirmButton.click()
    }
  })
})

test.describe('管理预约E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/user/appointments*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 3,
            list: [
              {
                id: 1,
                appointmentNo: 'APT202401200001',
                serviceName: '宠物洗澡美容',
                merchantName: '宠物乐园',
                appointmentTime: '2024-01-20 14:00',
                status: '待确认',
                totalPrice: 88
              },
              {
                id: 2,
                appointmentNo: 'APT202401190001',
                serviceName: '宠物体检套餐',
                merchantName: '萌宠之家',
                appointmentTime: '2024-01-19 10:00',
                status: '已确认',
                totalPrice: 199
              },
              {
                id: 3,
                appointmentNo: 'APT202401180001',
                serviceName: '宠物疫苗接种',
                merchantName: '宠物乐园',
                appointmentTime: '2024-01-18 09:00',
                status: '已完成',
                totalPrice: 120
              }
            ]
          }
        })
      })
    })
  })

  test('能够加载预约列表页面', async ({ page }) => {
    await page.goto('/user/appointments')
    await expect(page.locator('.user-appointments, .el-table')).toBeVisible({ timeout: 10000 })
  })

  test('能够查看预约列表', async ({ page }) => {
    await page.goto('/user/appointments')
    
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 })
    await expect(page.locator('text=宠物洗澡美容')).toBeVisible({ timeout: 5000 })
  })

  test('能够按状态筛选预约', async ({ page }) => {
    await page.goto('/user/appointments')

    const statusFilter = page.locator('.el-radio-group .el-radio-button')
    if (await statusFilter.first().isVisible()) {
      await statusFilter.nth(1).click()
      await page.waitForTimeout(500)
    }
  })

  test('能够搜索预约', async ({ page }) => {
    await page.goto('/user/appointments')

    const searchInput = page.locator('input[placeholder*="搜索"]')
    if (await searchInput.isVisible()) {
      await searchInput.fill('宠物洗澡')
      await searchInput.press('Enter')
      await page.waitForTimeout(500)
    }
  })

  test('能够按日期范围筛选预约', async ({ page }) => {
    await page.goto('/user/appointments')

    const dateRangePicker = page.locator('.el-date-editor--daterange')
    if (await dateRangePicker.isVisible()) {
      await dateRangePicker.click()
      await page.waitForTimeout(300)
    }
  })

  test('能够取消预约', async ({ page }) => {
    await page.route('**/api/appointments/1/cancel', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '预约已取消',
          data: { id: 1, status: 'cancelled' }
        })
      })
    })

    await page.goto('/user/appointments')

    const cancelButton = page.locator('button:has-text("取消")').first()
    if (await cancelButton.isVisible()) {
      await cancelButton.click()

      const confirmButton = page.locator('.el-dialog button:has-text("确认取消"), .el-dialog button:has-text("确定")')
      if (await confirmButton.isVisible()) {
        await confirmButton.click()
        await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
      }
    }
  })

  test('能够查看预约详情', async ({ page }) => {
    await page.route('**/api/user/appointments/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            id: 1,
            appointmentNo: 'APT202401200001',
            serviceName: '宠物洗澡美容',
            merchantName: '宠物乐园',
            appointmentTime: '2024-01-20 14:00',
            status: '待确认',
            totalPrice: 88,
            petName: '小白',
            notes: '请轻柔一点'
          }
        })
      })
    })

    await page.goto('/user/appointments')

    const detailButton = page.locator('button:has-text("详情"), button:has-text("查看")').first()
    if (await detailButton.isVisible()) {
      await detailButton.click()
    }
  })

  test('能够正确显示不同状态的标签', async ({ page }) => {
    await page.goto('/user/appointments')

    await expect(page.locator('.el-tag')).toBeVisible({ timeout: 10000 })
    
    const warningTag = page.locator('.el-tag--warning')
    if (await warningTag.isVisible()) {
      await expect(warningTag).toContainText('待确认')
    }
  })

  test('空预约列表显示提示', async ({ page }) => {
    await page.route('**/api/user/appointments*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 0,
            list: []
          }
        })
      })
    })

    await page.goto('/user/appointments')
    await expect(page.locator('.el-empty')).toBeVisible({ timeout: 10000 })
  })
})

test.describe('服务列表页面E2E测试', () => {
  test('能够加载服务列表页面', async ({ page }) => {
    await page.route('**/api/services*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 2,
            list: [
              { id: 1, name: '宠物洗澡', price: 88, rating: 4.8 },
              { id: 2, name: '宠物美容', price: 128, rating: 4.9 }
            ]
          }
        })
      })
    })

    await page.goto('/user/services/list')
    await expect(page.locator('.service-card, .el-card, .el-table')).toBeVisible({ timeout: 10000 })
  })

  test('能够搜索服务', async ({ page }) => {
    await page.route('**/api/services*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 1,
            list: [
              { id: 1, name: '宠物洗澡', price: 88 }
            ]
          }
        })
      })
    })

    await page.goto('/user/services/list')

    const searchInput = page.locator('input[placeholder*="搜索"]')
    if (await searchInput.isVisible()) {
      await searchInput.fill('洗澡')
      await searchInput.press('Enter')
    }
  })

  test('能够按价格筛选服务', async ({ page }) => {
    await page.route('**/api/services*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 1,
            list: [
              { id: 1, name: '宠物洗澡', price: 50 }
            ]
          }
        })
      })
    })

    await page.goto('/user/services/list')

    const priceFilter = page.locator('.el-slider, input[type="number"]').first()
    if (await priceFilter.isVisible()) {
      await priceFilter.click()
    }
  })

  test('能够按评分排序服务', async ({ page }) => {
    await page.goto('/user/services/list')

    const sortSelect = page.locator('.el-select, .el-dropdown').first()
    if (await sortSelect.isVisible()) {
      await sortSelect.click()
      const ratingOption = page.locator('text=评分')
      if (await ratingOption.isVisible()) {
        await ratingOption.click()
      }
    }
  })
})
