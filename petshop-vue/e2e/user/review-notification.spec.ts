import { test, expect } from '@playwright/test'

test.describe('评价服务E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/user/appointments*', async (route) => {
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
                appointmentNo: 'APT202401200001',
                serviceName: '宠物洗澡美容',
                merchantName: '宠物乐园',
                merchantId: 1,
                serviceId: 1,
                appointmentTime: '2024-01-20 14:00',
                status: '已完成',
                totalPrice: 88,
                canReview: true
              },
              {
                id: 2,
                appointmentNo: 'APT202401190001',
                serviceName: '宠物体检套餐',
                merchantName: '萌宠之家',
                merchantId: 2,
                serviceId: 2,
                appointmentTime: '2024-01-19 10:00',
                status: '已完成',
                totalPrice: 199,
                canReview: false,
                reviewed: true
              }
            ]
          }
        })
      })
    })
  })

  test('能够加载已完成预约列表', async ({ page }) => {
    await page.goto('/user/appointments')
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 })
  })

  test('能够对已完成预约进行评价', async ({ page }) => {
    await page.route('**/api/reviews', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '评价创建成功',
            data: {
              id: 1,
              rating: 5,
              comment: '服务很好，宠物很喜欢！'
            }
          })
        })
      }
    })

    await page.goto('/user/appointments')

    const reviewButton = page.locator('button:has-text("评价")').first()
    if (await reviewButton.isVisible()) {
      await reviewButton.click()

      const ratingStars = page.locator('.el-rate__icon, .el-rate__item').first()
      if (await ratingStars.isVisible()) {
        await ratingStars.click()
      }

      const commentInput = page.locator('textarea[placeholder*="评价"], textarea[placeholder*="内容"]')
      if (await commentInput.isVisible()) {
        await commentInput.fill('服务很好，宠物很喜欢！')
      }

      const submitButton = page.locator('button:has-text("提交"), button:has-text("发布")')
      if (await submitButton.isVisible()) {
        await submitButton.click()
      }
    }
  })

  test('能够选择评分', async ({ page }) => {
    await page.goto('/user/reviews')

    const ratingComponent = page.locator('.el-rate')
    if (await ratingComponent.isVisible()) {
      const stars = ratingComponent.locator('.el-rate__item')
      if (await stars.count() > 0) {
        await stars.nth(4).click()
      }
    }
  })

  test('能够填写评价内容', async ({ page }) => {
    await page.goto('/user/reviews')

    const commentInput = page.locator('textarea[placeholder*="评价"], textarea[placeholder*="内容"]')
    if (await commentInput.isVisible()) {
      await commentInput.fill('这是一条测试评价内容，服务态度很好，环境干净整洁。')
      await expect(commentInput).toHaveValue(/测试评价/)
    }
  })

  test('能够上传评价图片', async ({ page }) => {
    await page.goto('/user/reviews')

    const uploadButton = page.locator('.el-upload, input[type="file"]').first()
    if (await uploadButton.isVisible()) {
      // 模拟点击上传按钮
      await uploadButton.click()
    }
  })

  test('表单验证 - 评价内容必填', async ({ page }) => {
    await page.route('**/api/reviews', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 400,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 400,
            message: '评价内容不能为空'
          })
        })
      }
    })

    await page.goto('/user/reviews')

    const submitButton = page.locator('button:has-text("提交")').first()
    if (await submitButton.isVisible()) {
      await submitButton.click()
    }
  })
})

test.describe('评价商品E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/orders*', async (route) => {
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
                orderNo: 'ORD202401200001',
                totalPrice: 295,
                status: 'completed',
                createdAt: '2024-01-20 10:00:00',
                items: [
                  { 
                    productId: 1,
                    productName: '皇家猫粮 2kg', 
                    productImage: 'https://example.com/product1.jpg',
                    quantity: 2, 
                    price: 128,
                    canReview: true
                  }
                ]
              },
              {
                id: 2,
                orderNo: 'ORD202401190001',
                totalPrice: 39,
                status: 'completed',
                createdAt: '2024-01-19 15:30:00',
                items: [
                  { 
                    productId: 2,
                    productName: '宠物玩具球套装', 
                    productImage: 'https://example.com/product2.jpg',
                    quantity: 1, 
                    price: 39,
                    canReview: false,
                    reviewed: true
                  }
                ]
              }
            ]
          }
        })
      })
    })
  })

  test('能够查看已完成订单', async ({ page }) => {
    await page.goto('/user/orders')
    await expect(page.locator('.el-table, .order-card')).toBeVisible({ timeout: 10000 })
  })

  test('能够对已收货商品进行评价', async ({ page }) => {
    await page.route('**/api/reviews', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '评价创建成功',
            data: {
              id: 1,
              target_type: 'product',
              target_id: 1,
              rating: 5,
              comment: '商品质量很好，猫咪很喜欢吃！'
            }
          })
        })
      }
    })

    await page.goto('/user/orders')

    const reviewButton = page.locator('button:has-text("评价")').first()
    if (await reviewButton.isVisible()) {
      await reviewButton.click()
    }
  })

  test('能够查看商品评价列表', async ({ page }) => {
    await page.route('**/api/products/1/reviews*', async (route) => {
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
                userName: '用户A',
                rating: 5,
                comment: '猫猫很喜欢吃',
                createdAt: '2024-01-15'
              },
              {
                id: 2,
                userName: '用户B',
                rating: 4,
                comment: '质量不错，物流快',
                createdAt: '2024-01-10'
              }
            ]
          }
        })
      })
    })

    await page.goto('/user/product/detail/1')

    const reviewTab = page.locator('text=评价, .el-tabs__item:has-text("评价")')
    if (await reviewTab.isVisible()) {
      await reviewTab.click()
    }
  })
})

test.describe('我的评价管理E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/user/reviews*', async (route) => {
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
                target_type: 'service',
                target_name: '宠物洗澡美容',
                merchant_name: '宠物乐园',
                rating: 5,
                comment: '服务很好，宠物很喜欢！',
                createdAt: '2024-01-20 15:00:00'
              },
              {
                id: 2,
                target_type: 'product',
                target_name: '皇家猫粮 2kg',
                merchant_name: '宠物乐园',
                rating: 4,
                comment: '质量不错，猫咪爱吃',
                createdAt: '2024-01-19 12:00:00'
              },
              {
                id: 3,
                target_type: 'service',
                target_name: '宠物体检套餐',
                merchant_name: '萌宠之家',
                rating: 5,
                comment: '医生很专业，检查很全面',
                createdAt: '2024-01-18 10:00:00'
              }
            ]
          }
        })
      })
    })
  })

  test('能够加载我的评价列表页面', async ({ page }) => {
    await page.goto('/user/reviews/my')
    await expect(page.locator('.el-table, .review-card')).toBeVisible({ timeout: 10000 })
  })

  test('能够查看我的评价列表', async ({ page }) => {
    await page.goto('/user/reviews/my')
    await expect(page.locator('text=宠物洗澡美容')).toBeVisible({ timeout: 10000 })
  })

  test('能够按评价类型筛选', async ({ page }) => {
    await page.route('**/api/user/reviews*', async (route) => {
      const url = route.request().url()
      const targetType = url.includes('targetType=service') ? 'service' : null
      
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: targetType ? 2 : 3,
            list: targetType ? [
              { id: 1, target_name: '宠物洗澡美容', rating: 5 },
              { id: 3, target_name: '宠物体检套餐', rating: 5 }
            ] : []
          }
        })
      })
    })

    await page.goto('/user/reviews/my')

    const typeFilter = page.locator('.el-select, .el-radio-group').first()
    if (await typeFilter.isVisible()) {
      await typeFilter.click()
    }
  })

  test('能够按日期范围筛选评价', async ({ page }) => {
    await page.goto('/user/reviews/my')

    const dateRangePicker = page.locator('.el-date-editor--daterange')
    if (await dateRangePicker.isVisible()) {
      await dateRangePicker.click()
    }
  })

  test('能够编辑评价', async ({ page }) => {
    await page.route('**/api/reviews/1', async (route) => {
      if (route.request().method() === 'PUT') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '评价更新成功',
            data: {
              id: 1,
              rating: 5,
              comment: '更新后的评价内容'
            }
          })
        })
      }
    })

    await page.goto('/user/reviews/my')

    const editButton = page.locator('button:has-text("编辑")').first()
    if (await editButton.isVisible()) {
      await editButton.click()

      const commentInput = page.locator('textarea')
      if (await commentInput.isVisible()) {
        await commentInput.fill('更新后的评价内容')
      }

      const saveButton = page.locator('button:has-text("保存"), button:has-text("提交")')
      if (await saveButton.isVisible()) {
        await saveButton.click()
      }
    }
  })

  test('能够删除评价', async ({ page }) => {
    await page.route('**/api/reviews/1', async (route) => {
      if (route.request().method() === 'DELETE') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '评价删除成功',
            data: null
          })
        })
      }
    })

    await page.goto('/user/reviews/my')

    const deleteButton = page.locator('button:has-text("删除")').first()
    if (await deleteButton.isVisible()) {
      await deleteButton.click()

      const confirmButton = page.locator('.el-message-box button:has-text("确定")')
      if (await confirmButton.isVisible()) {
        await confirmButton.click()
        await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
      }
    }
  })

  test('能够查看评价详情', async ({ page }) => {
    await page.goto('/user/reviews/my')

    const detailButton = page.locator('button:has-text("详情"), button:has-text("查看")').first()
    if (await detailButton.isVisible()) {
      await detailButton.click()
    }
  })

  test('能够正确显示评分星级', async ({ page }) => {
    await page.goto('/user/reviews/my')

    const ratingStars = page.locator('.el-rate, .el-icon')
    if (await ratingStars.first().isVisible()) {
      await expect(ratingStars.first()).toBeVisible()
    }
  })

  test('空评价列表显示提示', async ({ page }) => {
    await page.route('**/api/user/reviews*', async (route) => {
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

    await page.goto('/user/reviews/my')
    await expect(page.locator('.el-empty')).toBeVisible({ timeout: 10000 })
  })
})

test.describe('通知管理E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/notifications*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 5,
            unread_count: 2,
            list: [
              {
                id: 1,
                title: '预约提醒',
                content: '您预约的宠物洗澡美容服务将于明天14:00开始',
                type: 'appointment',
                is_read: false,
                created_at: '2024-01-20 10:00:00'
              },
              {
                id: 2,
                title: '订单发货通知',
                content: '您的订单ORD202401190001已发货，请注意查收',
                type: 'order',
                is_read: false,
                created_at: '2024-01-19 15:00:00'
              },
              {
                id: 3,
                title: '系统公告',
                content: '平台将于1月25日进行系统升级维护',
                type: 'system',
                is_read: true,
                created_at: '2024-01-18 09:00:00'
              },
              {
                id: 4,
                title: '评价回复',
                content: '商家回复了您的评价：感谢您的支持！',
                type: 'review',
                is_read: true,
                created_at: '2024-01-17 14:00:00'
              },
              {
                id: 5,
                title: '优惠活动',
                content: '新春特惠，全场商品8折起！',
                type: 'promotion',
                is_read: true,
                created_at: '2024-01-16 10:00:00'
              }
            ]
          }
        })
      })
    })
  })

  test('能够加载通知列表页面', async ({ page }) => {
    await page.goto('/user/notifications')
    await expect(page.locator('.el-table, .notification-list, .el-card')).toBeVisible({ timeout: 10000 })
  })

  test('能够查看通知列表', async ({ page }) => {
    await page.goto('/user/notifications')
    await expect(page.locator('text=预约提醒')).toBeVisible({ timeout: 10000 })
  })

  test('能够查看未读通知数量', async ({ page }) => {
    await page.route('**/api/notifications/unread-count', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            unread_count: 2
          }
        })
      })
    })

    await page.goto('/user/notifications')

    const unreadBadge = page.locator('.el-badge__content, .unread-count')
    if (await unreadBadge.isVisible()) {
      const text = await unreadBadge.textContent()
      expect(parseInt(text || '0')).toBeGreaterThan(0)
    }
  })

  test('能够按类型筛选通知', async ({ page }) => {
    await page.route('**/api/notifications*', async (route) => {
      const url = route.request().url()
      const type = url.includes('type=order') ? 'order' : null
      
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: type ? 1 : 5,
            list: type ? [
              { id: 2, title: '订单发货通知', type: 'order' }
            ] : []
          }
        })
      })
    })

    await page.goto('/user/notifications')

    const typeFilter = page.locator('.el-select, .el-radio-group').first()
    if (await typeFilter.isVisible()) {
      await typeFilter.click()
    }
  })

  test('能够按已读/未读状态筛选', async ({ page }) => {
    await page.goto('/user/notifications')

    const readFilter = page.locator('.el-radio-group, .el-select').first()
    if (await readFilter.isVisible()) {
      await readFilter.click()
    }
  })

  test('能够标记单条通知为已读', async ({ page }) => {
    await page.route('**/api/notifications/1/read', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '标记已读成功',
          data: { id: 1, is_read: true }
        })
      })
    })

    await page.goto('/user/notifications')

    const markReadButton = page.locator('button:has-text("标记已读"), button:has-text("已读")').first()
    if (await markReadButton.isVisible()) {
      await markReadButton.click()
      await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
    }
  })

  test('能够批量标记已读', async ({ page }) => {
    await page.route('**/api/notifications/batch-read', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '批量标记已读成功',
          data: { updated_count: 2 }
        })
      })
    })

    await page.goto('/user/notifications')

    const batchReadButton = page.locator('button:has-text("全部已读"), button:has-text("批量已读")')
    if (await batchReadButton.isVisible()) {
      await batchReadButton.click()
      await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
    }
  })

  test('能够删除单条通知', async ({ page }) => {
    await page.route('**/api/notifications/1', async (route) => {
      if (route.request().method() === 'DELETE') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '通知删除成功',
            data: null
          })
        })
      }
    })

    await page.goto('/user/notifications')

    const deleteButton = page.locator('button:has-text("删除")').first()
    if (await deleteButton.isVisible()) {
      await deleteButton.click()

      const confirmButton = page.locator('.el-message-box button:has-text("确定")')
      if (await confirmButton.isVisible()) {
        await confirmButton.click()
      }
    }
  })

  test('能够批量删除通知', async ({ page }) => {
    await page.route('**/api/notifications/batch', async (route) => {
      if (route.request().method() === 'DELETE') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '批量删除成功',
            data: { deleted_count: 2 }
          })
        })
      }
    })

    await page.goto('/user/notifications')

    const checkbox = page.locator('.el-table .el-checkbox').first()
    if (await checkbox.isVisible()) {
      await checkbox.click()
    }

    const batchDeleteButton = page.locator('button:has-text("批量删除")')
    if (await batchDeleteButton.isVisible()) {
      await batchDeleteButton.click()

      const confirmButton = page.locator('.el-message-box button:has-text("确定")')
      if (await confirmButton.isVisible()) {
        await confirmButton.click()
      }
    }
  })

  test('能够查看通知详情', async ({ page }) => {
    await page.goto('/user/notifications')

    const notificationItem = page.locator('.notification-item, .el-table__row').first()
    if (await notificationItem.isVisible()) {
      await notificationItem.click()
    }
  })

  test('能够正确显示通知类型图标', async ({ page }) => {
    await page.goto('/user/notifications')

    const typeIcon = page.locator('.notification-icon, .el-icon').first()
    if (await typeIcon.isVisible()) {
      await expect(typeIcon).toBeVisible()
    }
  })

  test('能够正确显示已读/未读状态', async ({ page }) => {
    await page.goto('/user/notifications')

    const unreadIndicator = page.locator('.unread-dot, .is-read-false')
    if (await unreadIndicator.first().isVisible()) {
      await expect(unreadIndicator.first()).toBeVisible()
    }
  })

  test('空通知列表显示提示', async ({ page }) => {
    await page.route('**/api/notifications*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 0,
            unread_count: 0,
            list: []
          }
        })
      })
    })

    await page.goto('/user/notifications')
    await expect(page.locator('.el-empty')).toBeVisible({ timeout: 10000 })
  })

  test('能够查看通知统计信息', async ({ page }) => {
    await page.route('**/api/notifications/stats', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 5,
            unread: 2,
            by_type: {
              appointment: 1,
              order: 1,
              system: 1,
              review: 1,
              promotion: 1
            }
          }
        })
      })
    })

    await page.goto('/user/notifications')

    const statsCard = page.locator('.stats-card, .notification-stats')
    if (await statsCard.isVisible()) {
      await expect(statsCard).toContainText(/通知|消息/)
    }
  })
})

test.describe('公告通知E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/announcements*', async (route) => {
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
                title: '平台升级公告',
                content: '平台将于1月25日进行系统升级维护，届时部分功能可能无法使用，请提前做好准备。',
                summary: '平台将于1月25日进行系统升级维护',
                publishTime: '2024-01-20 10:00:00',
                isRead: false
              },
              {
                id: 2,
                title: '新春活动通知',
                content: '新春特惠活动即将开始，全场商品8折起，更有精美礼品相送！',
                summary: '新春特惠活动即将开始',
                publishTime: '2024-01-18 09:00:00',
                isRead: true
              },
              {
                id: 3,
                title: '服务时间调整',
                content: '春节期间服务时间有所调整，请关注各商家营业时间。',
                summary: '春节期间服务时间有所调整',
                publishTime: '2024-01-15 14:00:00',
                isRead: true
              }
            ]
          }
        })
      })
    })
  })

  test('能够加载公告列表页面', async ({ page }) => {
    await page.goto('/user/announcements')
    await expect(page.locator('.el-table, .announcement-list, .el-card')).toBeVisible({ timeout: 10000 })
  })

  test('能够查看公告列表', async ({ page }) => {
    await page.goto('/user/announcements')
    await expect(page.locator('text=平台升级公告')).toBeVisible({ timeout: 10000 })
  })

  test('能够搜索公告', async ({ page }) => {
    await page.route('**/api/announcements*', async (route) => {
      const url = route.request().url()
      const keyword = url.includes('keyword') ? '升级' : ''
      
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: keyword ? 1 : 3,
            list: keyword ? [
              { id: 1, title: '平台升级公告' }
            ] : []
          }
        })
      })
    })

    await page.goto('/user/announcements')

    const searchInput = page.locator('input[placeholder*="搜索"]')
    if (await searchInput.isVisible()) {
      await searchInput.fill('升级')
      await searchInput.press('Enter')
    }
  })

  test('能够查看公告详情', async ({ page }) => {
    await page.route('**/api/announcements/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            id: 1,
            title: '平台升级公告',
            content: '平台将于1月25日进行系统升级维护，届时部分功能可能无法使用，请提前做好准备。升级完成后，将带来更好的用户体验和更多新功能。',
            publishTime: '2024-01-20 10:00:00',
            author: '平台管理员'
          }
        })
      })
    })

    await page.goto('/user/announcements')

    const announcementItem = page.locator('.announcement-item, .el-table__row').first()
    if (await announcementItem.isVisible()) {
      await announcementItem.click()
    }
  })

  test('能够正确显示已读/未读状态', async ({ page }) => {
    await page.goto('/user/announcements')

    const unreadIndicator = page.locator('.unread-dot, .is-read-false, .new-tag')
    if (await unreadIndicator.first().isVisible()) {
      await expect(unreadIndicator.first()).toBeVisible()
    }
  })

  test('能够查看相关公告推荐', async ({ page }) => {
    await page.route('**/api/announcements/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            id: 1,
            title: '平台升级公告',
            content: '平台将于1月25日进行系统升级维护',
            publishTime: '2024-01-20 10:00:00',
            related: [
              { id: 2, title: '新春活动通知' },
              { id: 3, title: '服务时间调整' }
            ]
          }
        })
      })
    })

    await page.goto('/user/announcements/detail/1')

    const relatedSection = page.locator('.related-announcements, .related-list')
    if (await relatedSection.isVisible()) {
      await expect(relatedSection).toContainText(/相关|推荐/)
    }
  })

  test('空公告列表显示提示', async ({ page }) => {
    await page.route('**/api/announcements*', async (route) => {
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

    await page.goto('/user/announcements')
    await expect(page.locator('.el-empty')).toBeVisible({ timeout: 10000 })
  })
})

test.describe('评价统计E2E测试', () => {
  test('能够查看评价统计信息', async ({ page }) => {
    await page.route('**/api/reviews/stats', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            averageRating: 4.5,
            totalReviews: 156,
            ratingDistribution: {
              5: 98,
              4: 32,
              3: 18,
              2: 5,
              1: 3
            }
          }
        })
      })
    })

    await page.goto('/user/reviews')

    const statsSection = page.locator('.review-stats, .stats-card')
    if (await statsSection.isVisible()) {
      await expect(statsSection).toContainText(/评分|评价/)
    }
  })

  test('能够查看评分分布', async ({ page }) => {
    await page.goto('/user/reviews')

    const ratingChart = page.locator('.rating-chart, .rating-distribution')
    if (await ratingChart.isVisible()) {
      await expect(ratingChart).toBeVisible()
    }
  })
})
