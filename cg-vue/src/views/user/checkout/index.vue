<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElDialog } from 'element-plus'
import { getAddresses, purchaseProduct, type Product } from '@/api/user'

interface Address {
  id: number
  name: string
  phone: string
  address: string
}

interface CartItem {
  product: Product
  quantity: number
}

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const submitting = ref(false)
const addresses = ref<Address[]>([])
const selectedAddressId = ref<number | null>(null)
const remark = ref('')
const selectedPaymentMethod = ref('wechat')
const addressDialogVisible = ref(false)

const cartItems = ref<CartItem[]>([])

const paymentMethods = [
  { value: 'wechat', label: '微信支付', icon: '💳' },
  { value: 'alipay', label: '支付宝', icon: '💵' },
  { value: 'bank', label: '银行卡', icon: '🏦' }
]

const productTotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.product.price * item.quantity, 0)
})

const shippingFee = computed(() => {
  return productTotal.value > 0 ? 10 : 0
})

const orderTotal = computed(() => {
  return productTotal.value + shippingFee.value
})

const fetchAddresses = async () => {
  loading.value = true
  try {
    const res = await getAddresses()
    addresses.value = res.data || []
    if (addresses.value.length > 0 && !selectedAddressId.value) {
      selectedAddressId.value = addresses.value[0].id
    }
  } catch {
    ElMessage.error('获取收货地址失败')
  } finally {
    loading.value = false
  }
}

const handleAddAddress = () => {
  // 跳转到地址管理页面添加新地址
  router.push('/user/addresses')
}

const handleSubmitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  submitting.value = true
  try {
    // 模拟创建订单
    const orderId = Math.floor(Math.random() * 1000000)
    // 跳转到支付页面
    router.push({
      path: '/user/pay',
      query: {
        orderId: orderId.toString(),
        amount: orderTotal.value.toString(),
        paymentMethod: selectedPaymentMethod.value
      }
    })
  } catch {
    ElMessage.error('订单提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchAddresses()
  const cartData = route.query.cart
  if (cartData) {
    try {
      cartItems.value = JSON.parse(cartData as string)
    } catch {
      cartItems.value = []
    }
  } else {
    const productData = route.query.product
    const quantity = parseInt(route.query.quantity as string) || 1
    if (productData) {
      try {
        const product = JSON.parse(productData as string)
        cartItems.value = [{ product, quantity }]
      } catch {
        cartItems.value = []
      }
    }
  }
})
</script>

<template>
  <div class="checkout-page">
    <el-card>
      <template #header>
        <h2>确认订单</h2>
      </template>

      <div v-loading="loading">
        <el-card shadow="never" class="address-section">
          <template #header>
            <div class="section-title">
              <span>收货地址</span>
              <el-button type="primary" link @click="handleAddAddress">添加新地址</el-button>
            </div>
          </template>
          <el-radio-group v-model="selectedAddressId" class="address-list">
            <el-radio
              v-for="addr in addresses"
              :key="addr.id"
              :value="addr.id"
              class="address-item"
            >
              <div class="address-info">
                <span class="address-name">{{ addr.name }}</span>
                <span class="address-phone">{{ addr.phone }}</span>
                <span class="address-detail">{{ addr.address }}</span>
              </div>
            </el-radio>
          </el-radio-group>
          <el-empty v-if="addresses.length === 0" description="暂无收货地址，请添加" />
        </el-card>

        <el-card shadow="never" class="payment-section">
          <template #header>
            <span class="section-title">支付方式</span>
          </template>
          <el-radio-group v-model="selectedPaymentMethod" class="payment-list">
            <el-radio
              v-for="method in paymentMethods"
              :key="method.value"
              :value="method.value"
              class="payment-item"
            >
              <div class="payment-info">
                <span class="payment-icon">{{ method.icon }}</span>
                <span class="payment-label">{{ method.label }}</span>
              </div>
            </el-radio>
          </el-radio-group>
        </el-card>

        <el-card shadow="never" class="product-section">
          <template #header>
            <span class="section-title">商品清单</span>
          </template>
          <div v-for="item in cartItems" :key="item.product.id" class="product-item">
            <el-image
              :src="item.product.image || 'https://placeholder.com/80x80'"
              fit="cover"
              class="product-image"
            />
            <div class="product-detail">
              <h4 class="product-name">{{ item.product.name }}</h4>
              <p class="product-desc">{{ item.product.description || '暂无描述' }}</p>
            </div>
            <div class="product-quantity">x{{ item.quantity }}</div>
            <div class="product-price">
              <span class="unit-price">¥{{ item.product.price }}</span>
              <span class="subtotal">小计: ¥{{ (item.product.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
          <el-empty v-if="cartItems.length === 0" description="购物车为空" />
        </el-card>

        <el-card shadow="never" class="fee-section">
          <template #header>
            <span class="section-title">费用明细</span>
          </template>
          <div class="fee-detail">
            <div class="fee-row">
              <span>商品总价</span>
              <span>¥{{ productTotal.toFixed(2) }}</span>
            </div>
            <div class="fee-row">
              <span>运费</span>
              <span>¥{{ shippingFee.toFixed(2) }}</span>
            </div>
            <el-divider />
            <div class="fee-row total-row">
              <span>总计</span>
              <span class="total-price">¥{{ orderTotal.toFixed(2) }}</span>
            </div>
          </div>
        </el-card>

        <el-card shadow="never" class="remark-section">
          <template #header>
            <span class="section-title">备注</span>
          </template>
          <el-input
            v-model="remark"
            type="textarea"
            :rows="3"
            placeholder="请输入订单备注（可选）"
            maxlength="200"
            show-word-limit
          />
        </el-card>

        <div class="submit-section">
          <div class="submit-info">
            <span>共 {{ cartItems.length }} 件商品，合计：</span>
            <span class="submit-total">¥{{ orderTotal.toFixed(2) }}</span>
          </div>
          <el-button
            type="primary"
            size="large"
            :loading="submitting"
            :disabled="!selectedAddressId || cartItems.length === 0"
            @click="handleSubmitOrder"
          >
            提交订单
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.checkout-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.checkout-page h2 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.address-section,
.product-section,
.fee-section,
.remark-section,
.payment-section {
  margin-bottom: 20px;
}

.payment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-item {
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.payment-item:hover {
  border-color: #4CAF50;
  box-shadow: 0 2px 4px rgba(76, 175, 80, 0.1);
}

.payment-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.payment-icon {
  font-size: 24px;
}

.payment-label {
  font-size: 14px;
  color: #303133;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-item {
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.address-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.address-name {
  font-weight: bold;
}

.address-phone {
  color: #909399;
  font-size: 14px;
}

.address-detail {
  color: #606266;
  font-size: 14px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #ebeef5;
}

.product-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
}

.product-detail {
  flex: 1;
  margin-left: 16px;
}

.product-name {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: bold;
}

.product-desc {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.product-quantity {
  margin: 0 24px;
  color: #909399;
  font-size: 14px;
}

.product-price {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.unit-price {
  color: #606266;
  font-size: 14px;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
  font-size: 14px;
}

.fee-detail {
  padding: 0 8px;
}

.fee-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
  color: #606266;
}

.total-row {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.total-price {
  color: #f56c6c;
  font-size: 20px;
}

.submit-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-top: 20px;
}

.submit-info {
  font-size: 14px;
  color: #606266;
}

.submit-total {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
  margin-left: 8px;
}
</style>
