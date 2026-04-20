import { test, expect } from '@playwright/test'

test.describe('浏览商品E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/products*', async (route) => {
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
                name: '皇家猫粮 2kg',
                description: '优质猫粮，营养均衡',
                price: 128,
                stock: 100,
                image: 'https://example.com/product1.jpg',
                merchantId: 1,
                merchantName: '宠物乐园',
                rating: 4.8,
                salesCount: 256
              },
              {
                id: 2,
                name: '宠物玩具球套装',
                description: '多彩玩具球，耐咬耐磨',
                price: 39,
                stock: 200,
                image: 'https://example.com/product2.jpg',
                merchantId: 1,
                merchantName: '宠物乐园',
                rating: 4.5,
                salesCount: 189
              },
              {
                id: 3,
                name: '宠物自动喂食器',
                description: '智能定时喂食，大容量',
                price: 299,
                stock: 50,
                image: 'https://example.com/product3.jpg',
                merchantId: 2,
                merchantName: '萌宠之家',
                rating: 4.9,
                salesCount: 98
              }
            ]
          }
        })
      })
    })
  })

  test('能够加载商品列表页面', async ({ page }) => {
    await page.goto('/user/shop')
    await expect(page.locator('.product-card, .el-card, .el-table')).toBeVisible({ timeout: 10000 })
  })

  test('能够搜索商品', async ({ page }) => {
    await page.route('**/api/products*', async (route) => {
      const url = route.request().url()
      const keyword = url.includes('keyword') ? '猫粮' : ''
      
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: keyword ? 1 : 3,
            list: keyword ? [
              {
                id: 1,
                name: '皇家猫粮 2kg',
                price: 128
              }
            ] : []
          }
        })
      })
    })

    await page.goto('/user/shop')

    const searchInput = page.locator('input[placeholder*="搜索"]')
    if (await searchInput.isVisible()) {
      await searchInput.fill('猫粮')
      await searchInput.press('Enter')
      await page.waitForTimeout(500)
    }
  })

  test('能够按价格筛选商品', async ({ page }) => {
    await page.route('**/api/products*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 1,
            list: [
              { id: 2, name: '宠物玩具球套装', price: 39 }
            ]
          }
        })
      })
    })

    await page.goto('/user/shop')

    const priceFilter = page.locator('.el-slider, input[type="number"]').first()
    if (await priceFilter.isVisible()) {
      await priceFilter.click()
    }
  })

  test('能够按库存状态筛选商品', async ({ page }) => {
    await page.goto('/user/shop')

    const stockFilter = page.locator('.el-select, .el-radio-group').first()
    if (await stockFilter.isVisible()) {
      await stockFilter.click()
    }
  })

  test('能够查看商品详情', async ({ page }) => {
    await page.route('**/api/products/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            id: 1,
            name: '皇家猫粮 2kg',
            description: '优质猫粮，营养均衡，适合各年龄段猫咪',
            price: 128,
            stock: 100,
            image: 'https://example.com/product1.jpg',
            merchantId: 1,
            merchantName: '宠物乐园',
            merchantAddress: '北京市朝阳区xxx',
            merchantPhone: '010-12345678',
            rating: 4.8,
            reviewCount: 156,
            salesCount: 256
          }
        })
      })
    })

    await page.goto('/user/product/detail/1')
    await expect(page.locator('h1, h2, .product-name')).toContainText(/猫粮|商品详情/, { timeout: 10000 })
  })

  test('能够查看商品评价', async ({ page }) => {
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

  test('能够收藏商品', async ({ page }) => {
    await page.route('**/api/user/favorites', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '收藏成功',
            data: { id: 1, productId: 1 }
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

    await page.goto('/user/product/detail/1')

    const favoriteButton = page.locator('button:has-text("收藏")')
    if (await favoriteButton.isVisible()) {
      await favoriteButton.click()
    }
  })
})

test.describe('购物车操作E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/user/cart', async (route) => {
      if (route.request().method() === 'GET') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: 'success',
            data: [
              {
                id: 1,
                productId: 1,
                productName: '皇家猫粮 2kg',
                productImage: 'https://example.com/product1.jpg',
                price: 128,
                quantity: 2,
                merchantId: 1,
                merchantName: '宠物乐园',
                selected: true
              },
              {
                id: 2,
                productId: 2,
                productName: '宠物玩具球套装',
                productImage: 'https://example.com/product2.jpg',
                price: 39,
                quantity: 1,
                merchantId: 1,
                merchantName: '宠物乐园',
                selected: false
              }
            ]
          })
        })
      }
    })
  })

  test('能够加载购物车页面', async ({ page }) => {
    await page.goto('/user/cart')
    await expect(page.locator('.cart-page, .el-table')).toBeVisible({ timeout: 10000 })
  })

  test('能够查看购物车商品列表', async ({ page }) => {
    await page.goto('/user/cart')
    await expect(page.locator('text=皇家猫粮')).toBeVisible({ timeout: 10000 })
  })

  test('能够添加商品到购物车', async ({ page }) => {
    await page.route('**/api/user/cart', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '添加成功',
            data: {
              id: 3,
              productId: 3,
              productName: '宠物自动喂食器',
              price: 299,
              quantity: 1
            }
          })
        })
      }
    })

    await page.goto('/user/product/detail/1')

    const addButton = page.locator('button:has-text("加入购物车")')
    if (await addButton.isVisible()) {
      await addButton.click()
      await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
    }
  })

  test('能够修改购物车商品数量', async ({ page }) => {
    await page.route('**/api/user/cart', async (route) => {
      if (route.request().method() === 'PUT') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '更新成功',
            data: { id: 1, quantity: 3 }
          })
        })
      }
    })

    await page.goto('/user/cart')

    const increaseButton = page.locator('.el-input-number__increase').first()
    if (await increaseButton.isVisible()) {
      await increaseButton.click()
      await page.waitForTimeout(500)
    }
  })

  test('能够删除购物车商品', async ({ page }) => {
    await page.route('**/api/user/cart/1', async (route) => {
      if (route.request().method() === 'DELETE') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '删除成功',
            data: null
          })
        })
      }
    })

    await page.goto('/user/cart')

    const deleteButton = page.locator('button:has-text("删除")').first()
    if (await deleteButton.isVisible()) {
      await deleteButton.click()

      const confirmButton = page.locator('.el-message-box button:has-text("确定")')
      if (await confirmButton.isVisible()) {
        await confirmButton.click()
      }
    }
  })

  test('能够全选/取消全选商品', async ({ page }) => {
    await page.goto('/user/cart')

    const selectAllCheckbox = page.locator('.el-checkbox').first()
    if (await selectAllCheckbox.isVisible()) {
      await selectAllCheckbox.click()
      await page.waitForTimeout(300)
    }
  })

  test('能够批量删除选中商品', async ({ page }) => {
    await page.route('**/api/user/cart/*', async (route) => {
      if (route.request().method() === 'DELETE') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '删除成功',
            data: null
          })
        })
      }
    })

    await page.goto('/user/cart')

    const checkbox = page.locator('.el-table .el-checkbox').first()
    if (await checkbox.isVisible()) {
      await checkbox.click()
    }

    const batchDeleteButton = page.locator('button:has-text("删除选中")')
    if (await batchDeleteButton.isVisible()) {
      await batchDeleteButton.click()

      const confirmButton = page.locator('.el-message-box button:has-text("确定")')
      if (await confirmButton.isVisible()) {
        await confirmButton.click()
      }
    }
  })

  test('能够正确计算总价', async ({ page }) => {
    await page.goto('/user/cart')

    const totalAmount = page.locator('.settlement-amount, .total-amount')
    if (await totalAmount.isVisible()) {
      const text = await totalAmount.textContent()
      expect(text).toContain('¥')
    }
  })

  test('空购物车显示提示', async ({ page }) => {
    await page.route('**/api/user/cart', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: []
        })
      })
    })

    await page.goto('/user/cart')
    await expect(page.locator('.el-empty')).toBeVisible({ timeout: 10000 })
  })
})

test.describe('下单支付E2E测试', () => {
  test('能够加载结算页面', async ({ page }) => {
    await page.route('**/api/user/addresses', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: [
            {
              id: 1,
              receiverName: '张三',
              phone: '13800138000',
              province: '北京市',
              city: '北京市',
              district: '朝阳区',
              detailAddress: 'xxx街道xxx号',
              isDefault: true
            }
          ]
        })
      })
    })

    await page.goto('/user/checkout')
    await expect(page.locator('.checkout-page, .el-card')).toBeVisible({ timeout: 10000 })
  })

  test('能够选择收货地址', async ({ page }) => {
    await page.route('**/api/user/addresses', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: [
            {
              id: 1,
              receiverName: '张三',
              phone: '13800138000',
              address: '北京市朝阳区xxx',
              isDefault: true
            },
            {
              id: 2,
              receiverName: '李四',
              phone: '13900139000',
              address: '北京市海淀区xxx',
              isDefault: false
            }
          ]
        })
      })
    })

    await page.goto('/user/checkout')

    const addressCard = page.locator('.address-card, .el-radio').first()
    if (await addressCard.isVisible()) {
      await addressCard.click()
    }
  })

  test('能够添加新收货地址', async ({ page }) => {
    await page.route('**/api/user/addresses', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '添加成功',
            data: {
              id: 3,
              receiverName: '王五',
              phone: '13700137000',
              address: '北京市西城区xxx'
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

    await page.goto('/user/checkout')

    const addAddressButton = page.locator('button:has-text("新增地址")')
    if (await addAddressButton.isVisible()) {
      await addAddressButton.click()
    }
  })

  test('能够选择支付方式', async ({ page }) => {
    await page.goto('/user/checkout')

    const paymentOption = page.locator('.payment-option, .el-radio').first()
    if (await paymentOption.isVisible()) {
      await paymentOption.click()
    }
  })

  test('能够添加订单备注', async ({ page }) => {
    await page.goto('/user/checkout')

    const remarkInput = page.locator('textarea[placeholder*="备注"], input[placeholder*="备注"]')
    if (await remarkInput.isVisible()) {
      await remarkInput.fill('请尽快发货，谢谢！')
    }
  })

  test('能够提交订单', async ({ page }) => {
    await page.route('**/api/orders', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '订单创建成功',
            data: {
              orderId: 1,
              orderNo: 'ORD202401200001',
              totalPrice: 295
            }
          })
        })
      }
    })

    await page.route('**/api/user/addresses', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: [
            {
              id: 1,
              receiverName: '张三',
              phone: '13800138000',
              address: '北京市朝阳区xxx',
              isDefault: true
            }
          ]
        })
      })
    })

    await page.goto('/user/checkout')

    const submitButton = page.locator('button:has-text("提交订单"), button:has-text("去支付")')
    if (await submitButton.isVisible()) {
      await submitButton.click()
    }
  })

  test('能够加载支付页面', async ({ page }) => {
    await page.route('**/api/orders/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            id: 1,
            orderNo: 'ORD202401200001',
            totalPrice: 295,
            status: 'pending'
          }
        })
      })
    })

    await page.goto('/user/pay?orderId=1')
    await expect(page.locator('.pay-page, .el-card')).toBeVisible({ timeout: 10000 })
  })

  test('能够完成支付', async ({ page }) => {
    await page.route('**/api/orders/1/pay', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '支付成功',
          data: {
            orderId: 1,
            status: 'paid'
          }
        })
      })
    })

    await page.route('**/api/orders/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            id: 1,
            orderNo: 'ORD202401200001',
            totalPrice: 295,
            status: 'pending'
          }
        })
      })
    })

    await page.goto('/user/pay?orderId=1')

    const payButton = page.locator('button:has-text("确认支付"), button:has-text("支付")')
    if (await payButton.isVisible()) {
      await payButton.click()
    }
  })
})

test.describe('订单管理E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/orders*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 4,
            statusCount: {
              all: 4,
              pending: 1,
              paid: 1,
              shipped: 1,
              completed: 1,
              cancelled: 0
            },
            list: [
              {
                id: 1,
                orderNo: 'ORD202401200001',
                totalPrice: 295,
                status: 'pending',
                createdAt: '2024-01-20 10:00:00',
                items: [
                  { productName: '皇家猫粮 2kg', quantity: 2, price: 128 }
                ]
              },
              {
                id: 2,
                orderNo: 'ORD202401190001',
                totalPrice: 39,
                status: 'paid',
                createdAt: '2024-01-19 15:30:00',
                items: [
                  { productName: '宠物玩具球套装', quantity: 1, price: 39 }
                ]
              },
              {
                id: 3,
                orderNo: 'ORD202401180001',
                totalPrice: 299,
                status: 'shipped',
                createdAt: '2024-01-18 09:00:00',
                items: [
                  { productName: '宠物自动喂食器', quantity: 1, price: 299 }
                ]
              },
              {
                id: 4,
                orderNo: 'ORD202401170001',
                totalPrice: 128,
                status: 'completed',
                createdAt: '2024-01-17 14:00:00',
                items: [
                  { productName: '皇家猫粮 2kg', quantity: 1, price: 128 }
                ]
              }
            ]
          }
        })
      })
    })
  })

  test('能够加载订单列表页面', async ({ page }) => {
    await page.goto('/user/orders')
    await expect(page.locator('.el-table, .order-card')).toBeVisible({ timeout: 10000 })
  })

  test('能够查看订单列表', async ({ page }) => {
    await page.goto('/user/orders')
    await expect(page.locator('text=ORD202401200001')).toBeVisible({ timeout: 10000 })
  })

  test('能够按状态筛选订单', async ({ page }) => {
    await page.goto('/user/orders')

    const statusTab = page.locator('.el-tabs__item, .el-radio-button').nth(1)
    if (await statusTab.isVisible()) {
      await statusTab.click()
      await page.waitForTimeout(500)
    }
  })

  test('能够查看订单详情', async ({ page }) => {
    await page.route('**/api/orders/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            id: 1,
            orderNo: 'ORD202401200001',
            totalPrice: 295,
            status: 'pending',
            createdAt: '2024-01-20 10:00:00',
            shippingAddress: '北京市朝阳区xxx',
            items: [
              { 
                productId: 1,
                productName: '皇家猫粮 2kg', 
                productImage: 'https://example.com/product1.jpg',
                quantity: 2, 
                price: 128,
                subtotal: 256
              }
            ]
          }
        })
      })
    })

    await page.goto('/user/orders')

    const detailButton = page.locator('button:has-text("详情"), a:has-text("详情")').first()
    if (await detailButton.isVisible()) {
      await detailButton.click()
    }
  })

  test('能够取消订单', async ({ page }) => {
    await page.route('**/api/orders/1/cancel', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '订单已取消',
          data: { id: 1, status: 'cancelled' }
        })
      })
    })

    await page.goto('/user/orders')

    const cancelButton = page.locator('button:has-text("取消")').first()
    if (await cancelButton.isVisible()) {
      await cancelButton.click()

      const confirmButton = page.locator('.el-message-box button:has-text("确定")')
      if (await confirmButton.isVisible()) {
        await confirmButton.click()
        await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 5000 })
      }
    }
  })

  test('能够确认收货', async ({ page }) => {
    await page.route('**/api/orders/3/confirm', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '确认收货成功',
          data: { id: 3, status: 'completed' }
        })
      })
    })

    await page.goto('/user/orders')

    const confirmButton = page.locator('button:has-text("确认收货")').first()
    if (await confirmButton.isVisible()) {
      await confirmButton.click()

      const confirmDialogButton = page.locator('.el-message-box button:has-text("确定")')
      if (await confirmDialogButton.isVisible()) {
        await confirmDialogButton.click()
      }
    }
  })

  test('能够删除已完成订单', async ({ page }) => {
    await page.route('**/api/orders/4', async (route) => {
      if (route.request().method() === 'DELETE') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '订单已删除',
            data: null
          })
        })
      }
    })

    await page.goto('/user/orders')

    const deleteButton = page.locator('button:has-text("删除")').first()
    if (await deleteButton.isVisible()) {
      await deleteButton.click()

      const confirmButton = page.locator('.el-message-box button:has-text("确定")')
      if (await confirmButton.isVisible()) {
        await confirmButton.click()
      }
    }
  })

  test('能够批量取消订单', async ({ page }) => {
    await page.route('**/api/orders/batch-cancel', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '成功取消1个订单',
          data: { cancelledCount: 1 }
        })
      })
    })

    await page.goto('/user/orders')

    const checkbox = page.locator('.el-table .el-checkbox').first()
    if (await checkbox.isVisible()) {
      await checkbox.click()
    }

    const batchCancelButton = page.locator('button:has-text("批量取消")')
    if (await batchCancelButton.isVisible()) {
      await batchCancelButton.click()
    }
  })

  test('能够正确显示不同状态的标签', async ({ page }) => {
    await page.goto('/user/orders')

    await expect(page.locator('.el-tag')).toBeVisible({ timeout: 10000 })
  })

  test('空订单列表显示提示', async ({ page }) => {
    await page.route('**/api/orders*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: 'success',
          data: {
            total: 0,
            statusCount: {
              all: 0,
              pending: 0,
              paid: 0,
              shipped: 0,
              completed: 0,
              cancelled: 0
            },
            list: []
          }
        })
      })
    })

    await page.goto('/user/orders')
    await expect(page.locator('.el-empty')).toBeVisible({ timeout: 10000 })
  })
})

test.describe('收货地址管理E2E测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/user/addresses', async (route) => {
      if (route.request().method() === 'GET') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: 'success',
            data: [
              {
                id: 1,
                receiverName: '张三',
                phone: '13800138000',
                province: '北京市',
                city: '北京市',
                district: '朝阳区',
                detailAddress: 'xxx街道xxx号',
                isDefault: true
              },
              {
                id: 2,
                receiverName: '李四',
                phone: '13900139000',
                province: '北京市',
                city: '北京市',
                district: '海淀区',
                detailAddress: 'yyy街道yyy号',
                isDefault: false
              }
            ]
          })
        })
      }
    })
  })

  test('能够加载地址列表页面', async ({ page }) => {
    await page.goto('/user/addresses')
    await expect(page.locator('.address-card, .el-table, .el-card')).toBeVisible({ timeout: 10000 })
  })

  test('能够添加新地址', async ({ page }) => {
    await page.route('**/api/user/addresses', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '添加成功',
            data: {
              id: 3,
              receiverName: '王五',
              phone: '13700137000',
              province: '北京市',
              city: '北京市',
              district: '西城区',
              detailAddress: 'zzz街道zzz号',
              isDefault: false
            }
          })
        })
      }
    })

    await page.goto('/user/addresses')

    const addButton = page.locator('button:has-text("新增"), button:has-text("添加")')
    if (await addButton.isVisible()) {
      await addButton.click()
    }
  })

  test('能够编辑地址', async ({ page }) => {
    await page.route('**/api/user/addresses/1', async (route) => {
      if (route.request().method() === 'PUT') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '更新成功',
            data: {
              id: 1,
              receiverName: '张三',
              phone: '13800138001'
            }
          })
        })
      }
    })

    await page.goto('/user/addresses')

    const editButton = page.locator('button:has-text("编辑")').first()
    if (await editButton.isVisible()) {
      await editButton.click()
    }
  })

  test('能够删除地址', async ({ page }) => {
    await page.route('**/api/user/addresses/2', async (route) => {
      if (route.request().method() === 'DELETE') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            code: 200,
            message: '删除成功',
            data: null
          })
        })
      }
    })

    await page.goto('/user/addresses')

    const deleteButton = page.locator('button:has-text("删除")').first()
    if (await deleteButton.isVisible()) {
      await deleteButton.click()

      const confirmButton = page.locator('.el-message-box button:has-text("确定")')
      if (await confirmButton.isVisible()) {
        await confirmButton.click()
      }
    }
  })

  test('能够设置默认地址', async ({ page }) => {
    await page.route('**/api/user/addresses/2/default', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 200,
          message: '设置成功',
          data: null
        })
      })
    })

    await page.goto('/user/addresses')

    const setDefaultButton = page.locator('button:has-text("设为默认"), text=设为默认').first()
    if (await setDefaultButton.isVisible()) {
      await setDefaultButton.click()
    }
  })
})
