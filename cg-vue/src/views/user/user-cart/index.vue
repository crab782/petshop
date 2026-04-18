<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, ShoppingTrolley } from '@element-plus/icons-vue'
import { getCart, updateCartItem, removeFromCart, type CartItem } from '@/api/user'

const router = useRouter()

const loading = ref(false)
const cartItems = ref<CartItem[]>([])
const selectedItems = ref<number[]>([])

const allSelected = computed({
  get: () => cartItems.value.length > 0 && selectedItems.value.length === cartItems.value.length,
  set: (val: boolean) => {
    selectedItems.value = val ? cartItems.value.map((item) => item.id) : []
  }
})

const isIndeterminate = computed(() => {
  const len = selectedItems.value.length
  return len > 0 && len < cartItems.value.length
})

const totalQuantity = computed(() => {
  return cartItems.value
    .filter((item) => selectedItems.value.includes(item.id))
    .reduce((sum, item) => sum + item.quantity, 0)
})

const totalAmount = computed(() => {
  return cartItems.value
    .filter((item) => selectedItems.value.includes(item.id))
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const subtotal = (item: CartItem) => {
  return item.price * item.quantity
}

const fetchCart = async () => {
  loading.value = true
  try {
    const res = await getCart()
    cartItems.value = res.data || []
    selectedItems.value = []
  } catch {
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

const handleQuantityChange = async (item: CartItem, quantity: number) => {
  if (quantity < 1) return
  try {
    await updateCartItem(item.productId, quantity)
    item.quantity = quantity
  } catch {
    ElMessage.error('更新数量失败')
    fetchCart()
  }
}

const handleRemove = async (item: CartItem) => {
  try {
    await ElMessageBox.confirm('确定要从购物车中移除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFromCart(item.productId)
    ElMessage.success('已移除')
    fetchCart()
  } catch {
    // 用户取消
  }
}

const handleBatchRemove = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedItems.value.length} 件商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    for (const id of selectedItems.value) {
      const item = cartItems.value.find((i) => i.id === id)
      if (item) {
        await removeFromCart(item.productId)
      }
    }
    ElMessage.success('已删除选中商品')
    fetchCart()
  } catch {
    // 用户取消
  }
}

const handleSelectAll = (val: boolean) => {
  selectedItems.value = val ? cartItems.value.map((item) => item.id) : []
}

const handleSettle = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  const selectedCartItems = cartItems.value.filter((item) =>
    selectedItems.value.includes(item.id)
  )
  router.push({
    path: '/user/checkout',
    query: {
      items: JSON.stringify(
        selectedCartItems.map((item) => ({
          productId: item.productId,
          productName: item.productName,
          productImage: item.productImage,
          price: item.price,
          quantity: item.quantity,
          merchantId: item.merchantId,
          merchantName: item.merchantName
        }))
      )
    }
  })
}

onMounted(() => {
  fetchCart()
})
</script>

<template>
  <div class="cart-page">
    <el-card>
      <template #header>
        <div class="header">
          <h2>
            <el-icon><ShoppingTrolley /></el-icon>
            购物车
            <span v-if="cartItems.length > 0" class="cart-count">({{ cartItems.length }})</span>
          </h2>
          <el-button
            v-if="selectedItems.length > 0"
            type="danger"
            plain
            @click="handleBatchRemove"
          >
            删除选中 ({{ selectedItems.length }})
          </el-button>
        </div>
      </template>

      <div v-loading="loading">
        <el-empty v-if="cartItems.length === 0" description="购物车是空的">
          <el-button type="primary" @click="$router.push('/user/shop')">去逛逛</el-button>
        </el-empty>

        <div v-else class="cart-container">
          <div class="cart-main">
            <el-table :data="cartItems" style="width: 100%" row-key="id">
              <el-table-column width="60" align="center">
                <template #header>
                  <el-checkbox
                    :model-value="allSelected"
                    :indeterminate="isIndeterminate"
                    @change="handleSelectAll"
                  />
                </template>
                <template #default="{ row }">
                  <el-checkbox
                    v-model="selectedItems"
                    :value="row.id"
                    :checked="selectedItems.includes(row.id)"
                  />
                </template>
              </el-table-column>

              <el-table-column label="商品" min-width="300">
                <template #default="{ row }">
                  <div class="product-cell">
                    <el-image
                      :src="row.productImage || 'https://placeholder.com/80x80'"
                      fit="cover"
                      class="product-image"
                    >
                      <template #error>
                        <div class="image-error">
                          <span>🐾</span>
                        </div>
                      </template>
                    </el-image>
                    <div class="product-info">
                      <span class="product-name">{{ row.productName }}</span>
                      <span class="product-merchant">{{ row.merchantName }}</span>
                    </div>
                  </div>
                </template>
              </el-table-column>

              <el-table-column prop="price" label="单价" width="120" align="center">
                <template #default="{ row }">
                  <span class="price">¥{{ row.price.toFixed(2) }}</span>
                </template>
              </el-table-column>

              <el-table-column label="数量" width="180" align="center">
                <template #default="{ row }">
                  <el-input-number
                    :model-value="row.quantity"
                    :min="1"
                    :max="99"
                    size="small"
                    @change="(val: number) => handleQuantityChange(row, val)"
                  />
                </template>
              </el-table-column>

              <el-table-column label="小计" width="120" align="center">
                <template #default="{ row }">
                  <span class="subtotal">¥{{ subtotal(row).toFixed(2) }}</span>
                </template>
              </el-table-column>

              <el-table-column label="操作" width="100" align="center">
                <template #default="{ row }">
                  <el-button
                    type="danger"
                    :icon="Delete"
                    text
                    @click="handleRemove(row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div class="cart-sidebar">
            <el-card shadow="hover" class="settlement-card">
              <h3 class="settlement-title">结算信息</h3>

              <div class="settlement-row">
                <span class="settlement-label">已选商品</span>
                <span class="settlement-value">{{ selectedItems.length }} 种</span>
              </div>

              <div class="settlement-row">
                <span class="settlement-label">商品总数</span>
                <span class="settlement-value">{{ totalQuantity }} 件</span>
              </div>

              <el-divider style="margin: 16px 0" />

              <div class="settlement-row total-row">
                <span class="settlement-label">应付金额</span>
                <span class="settlement-amount">¥{{ totalAmount.toFixed(2) }}</span>
              </div>

              <el-button
                type="primary"
                class="settle-btn"
                :disabled="selectedItems.length === 0"
                @click="handleSettle"
              >
                去结算 ({{ selectedItems.length }})
              </el-button>

              <div class="select-all-row">
                <el-checkbox
                  :model-value="allSelected"
                  :indeterminate="isIndeterminate"
                  @change="handleSelectAll"
                >
                  全选
                </el-checkbox>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
}

.cart-count {
  font-size: 14px;
  font-weight: normal;
  color: #909399;
}

.cart-container {
  display: flex;
  gap: 20px;
}

.cart-main {
  flex: 1;
  min-width: 0;
}

.cart-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  flex-shrink: 0;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  font-size: 32px;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-merchant {
  font-size: 12px;
  color: #909399;
}

.price {
  color: #606266;
  font-weight: 500;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
  font-size: 14px;
}

.settlement-card {
  position: sticky;
  top: 20px;
}

.settlement-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.settlement-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.settlement-label {
  color: #606266;
  font-size: 14px;
}

.settlement-value {
  color: #303133;
  font-size: 14px;
}

.total-row {
  margin-bottom: 0;
}

.settlement-amount {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}

.settle-btn {
  width: 100%;
  margin-top: 20px;
  height: 44px;
  font-size: 16px;
}

.select-all-row {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  text-align: center;
}

@media (max-width: 768px) {
  .cart-container {
    flex-direction: column;
  }

  .cart-sidebar {
    width: 100%;
  }

  .settlement-card {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    border-radius: 16px 16px 0 0;
    z-index: 100;
    box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.1);
  }
}
</style>
