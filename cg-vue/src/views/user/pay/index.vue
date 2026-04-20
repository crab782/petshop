<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Money, Timer, CreditCard, Check, Close, Loading, Picture } from '@element-plus/icons-vue'
import { payOrder, getOrderById, type OrderDetail } from '@/api/user'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const paying = ref(false)
const order = ref<OrderDetail | null>(null)
const payMethod = ref('wechat')
const payStatus = ref<'pending' | 'paying' | 'success' | 'failed'>('pending')
const showQRCode = ref(false)
const countdown = ref(0)
const countdownTimer = ref<ReturnType<typeof setInterval> | null>(null)
const payTimer = ref<ReturnType<typeof setInterval> | null>(null)

const payMethodOptions = [
  {
    label: '微信支付',
    value: 'wechat',
    icon: 'Wechat',
    color: '#07c160',
    description: '使用微信扫码支付'
  },
  {
    label: '支付宝',
    value: 'alipay',
    icon: 'AliPay',
    color: '#1677ff',
    description: '使用支付宝扫码支付'
  },
  {
    label: '银行卡支付',
    value: 'bank',
    icon: 'CreditCard',
    color: '#ff6b6b',
    description: '使用银行卡在线支付'
  }
]

const statusConfig = {
  pending: {
    text: '待支付',
    type: 'warning',
    icon: Timer,
    color: '#e6a23c'
  },
  paying: {
    text: '支付中',
    type: 'primary',
    icon: Loading,
    color: '#409eff'
  },
  success: {
    text: '支付成功',
    type: 'success',
    icon: Check,
    color: '#67c23a'
  },
  failed: {
    text: '支付失败',
    type: 'danger',
    icon: Close,
    color: '#f56c6c'
  }
}

const currentStatus = computed(() => statusConfig[payStatus.value])

const formatCountdown = computed(() => {
  const minutes = Math.floor(countdown.value / 60)
  const seconds = countdown.value % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

const isExpired = computed(() => countdown.value <= 0)

// 硬编码测试数据 - 仅在开发环境使用
const mockOrder: OrderDetail = {
  id: 1,
  orderNo: 'PS202401200001',
  userId: 1,
  merchantId: 4,
  totalPrice: 258.9,
  freight: 10,
  status: 'pending',
  createTime: new Date().toISOString(),
  address: {
    id: 1,
    name: '张三',
    phone: '13800138000',
    province: '北京市',
    city: '北京市',
    district: '朝阳区',
    address: '建国路88号SOHO现代城A座2301室'
  },
  items: [
    {
      id: 1,
      productId: 1,
      productName: '宠物粮食 成犬专用',
      productImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20food%20bag%20for%20adult%20dogs&image_size=landscape_4_3',
      price: 129.9,
      quantity: 1,
      subtotal: 129.9
    },
    {
      id: 2,
      productId: 2,
      productName: '宠物玩具 发声球',
      productImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20toy%20squeaky%20ball&image_size=landscape_4_3',
      price: 39.5,
      quantity: 2,
      subtotal: 79
    },
    {
      id: 3,
      productId: 3,
      productName: '宠物牵引绳',
      productImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20leash%20for%20dogs&image_size=landscape_4_3',
      price: 49.9,
      quantity: 1,
      subtotal: 49.9
    }
  ]
}

const fetchOrderInfo = async () => {
  loading.value = true
  try {
    // 开发环境使用模拟数据
    if (import.meta.env.DEV) {
      order.value = mockOrder
      
      const createTime = new Date(order.value.createTime).getTime()
      const expireTime = createTime + 30 * 60 * 1000
      countdown.value = Math.max(0, Math.floor((expireTime - Date.now()) / 1000))

      if (countdown.value > 0) {
        startCountdown()
      }
      return
    }
    
    const orderId = Number(route.query.orderId)
    if (!orderId) {
      ElMessage.error('订单参数缺失')
      router.push('/user/orders')
      return
    }

    const res = await getOrderById(orderId)
    order.value = res.data

    if (order.value.status !== 'pending') {
      ElMessage.warning('该订单已支付或已取消')
      router.push('/user/orders')
      return
    }

    const createTime = new Date(order.value.createTime).getTime()
    const expireTime = createTime + 30 * 60 * 1000
    countdown.value = Math.max(0, Math.floor((expireTime - Date.now()) / 1000))

    if (countdown.value > 0) {
      startCountdown()
    }
  } catch {
    ElMessage.error('获取订单信息失败')
    router.push('/user/orders')
  } finally {
    loading.value = false
  }
}

const startCountdown = () => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }

  countdownTimer.value = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      stopCountdown()
      ElMessage.warning('订单已超时，请重新下单')
      router.push('/user/orders')
    }
  }, 1000)
}

const stopCountdown = () => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
    countdownTimer.value = null
  }
}

const handlePay = async () => {
  if (!order.value || isExpired.value) {
    ElMessage.error('订单已超时，无法支付')
    return
  }

  const selectedMethod = payMethodOptions.find(m => m.value === payMethod.value)

  try {
    await ElMessageBox.confirm(
      `确定使用${selectedMethod?.label}支付 ¥${order.value.totalPrice.toFixed(2)}？`,
      '确认支付',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    if (payMethod.value === 'bank') {
      await handleBankPay()
    } else {
      await handleQRCodePay()
    }
  } catch {
    // 用户取消
  }
}

const handleQRCodePay = async () => {
  if (!order.value) return

  payStatus.value = 'paying'
  showQRCode.value = true

  try {
    await payOrder(order.value.id, payMethod.value)

    startPayPolling()
  } catch {
    payStatus.value = 'failed'
    showQRCode.value = false
    ElMessage.error('发起支付失败，请稍后重试')
  }
}

const handleBankPay = async () => {
  if (!order.value) return

  payStatus.value = 'paying'

  try {
    await payOrder(order.value.id, payMethod.value)

    await simulatePayment()

    payStatus.value = 'success'
    ElMessage.success('支付成功')

    setTimeout(() => {
      router.push({
        path: '/user/orders',
        query: { payResult: 'success', orderId: order.value?.id }
      })
    }, 1500)
  } catch {
    payStatus.value = 'failed'
    ElMessage.error('支付失败，请稍后重试')
  }
}

const startPayPolling = () => {
  let pollCount = 0
  const maxPolls = 60

  payTimer.value = setInterval(async () => {
    pollCount++

    if (pollCount >= maxPolls) {
      stopPayPolling()
      payStatus.value = 'failed'
      showQRCode.value = false
      ElMessage.error('支付超时，请重新支付')
      return
    }

    if (Math.random() > 0.7) {
      await simulatePayment()
      stopPayPolling()
      payStatus.value = 'success'
      showQRCode.value = false
      ElMessage.success('支付成功')

      setTimeout(() => {
        router.push({
          path: '/user/orders',
          query: { payResult: 'success', orderId: order.value?.id }
        })
      }, 1500)
    }
  }, 2000)
}

const stopPayPolling = () => {
  if (payTimer.value) {
    clearInterval(payTimer.value)
    payTimer.value = null
  }
}

const simulatePayment = () => {
  return new Promise((resolve) => {
    setTimeout(resolve, 1500)
  })
}

const handleCancelPay = () => {
  stopPayPolling()
  showQRCode.value = false
  payStatus.value = 'pending'
}

const handleRetry = () => {
  payStatus.value = 'pending'
  showQRCode.value = false
}

const handleBack = () => {
  router.push('/user/orders')
}

const generateQRCodeURL = () => {
  const baseURL = 'https://api.qrserver.com/v1/create-qr-code/'
  const size = '200x200'
  const data = `petshop://pay?orderId=${order.value?.id}&amount=${order.value?.totalPrice}&method=${payMethod.value}`
  return `${baseURL}?size=${size}&data=${encodeURIComponent(data)}`
}

onMounted(() => {
  fetchOrderInfo()
})

onUnmounted(() => {
  stopCountdown()
  stopPayPolling()
})
</script>

<template>
  <div class="pay-page">
    <el-card v-loading="loading" class="pay-card">
      <template #header>
        <div class="card-header">
          <span class="title">订单支付</span>
          <el-tag
            :type="currentStatus.type"
            :icon="currentStatus.icon"
            effect="dark"
          >
            {{ currentStatus.text }}
          </el-tag>
        </div>
      </template>

      <div v-if="order" class="pay-content">
        <!-- 订单信息 -->
        <el-alert
          type="info"
          :closable="false"
          show-icon
          class="order-alert"
        >
          <template #title>
            <div class="order-info">
              <div class="info-row">
                <span class="info-label">订单编号：</span>
                <span class="info-value">{{ order.orderNo }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">下单时间：</span>
                <span class="info-value">{{ order.createTime }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">收货地址：</span>
                <span class="info-value">{{ order.address.name }} {{ order.address.phone }} {{ order.address.address }}</span>
              </div>
            </div>
          </template>
        </el-alert>

        <!-- 支付倒计时 -->
        <div v-if="!isExpired && payStatus === 'pending'" class="countdown-section">
          <el-icon class="countdown-icon"><Timer /></el-icon>
          <span class="countdown-text">支付剩余时间：</span>
          <span class="countdown-time">{{ formatCountdown }}</span>
        </div>

        <div v-if="isExpired" class="expired-section">
          <el-result
            icon="warning"
            title="订单已超时"
            sub-title="该订单已超过支付时限，请重新下单"
          >
            <template #extra>
              <el-button type="primary" @click="handleBack">返回订单列表</el-button>
            </template>
          </el-result>
        </div>

        <!-- 商品列表 -->
        <div v-if="!isExpired && !showQRCode" class="products-section">
          <h3 class="section-title">商品清单</h3>
          <div class="product-list">
            <div
              v-for="item in order.items"
              :key="item.id"
              class="product-item"
            >
              <el-image
                :src="item.productImage || 'https://via.placeholder.com/60'"
                fit="cover"
                class="product-image"
              >
                <template #error>
                  <div class="image-placeholder">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="product-info">
                <div class="product-name">{{ item.productName }}</div>
                <div class="product-meta">
                  <span>¥{{ item.price.toFixed(2) }}</span>
                  <span>x{{ item.quantity }}</span>
                </div>
              </div>
              <div class="product-subtotal">
                ¥{{ item.subtotal.toFixed(2) }}
              </div>
            </div>
          </div>
        </div>

        <el-divider v-if="!isExpired && !showQRCode" />

        <!-- 支付金额 -->
        <div v-if="!isExpired && !showQRCode" class="amount-section">
          <div class="amount-row">
            <span>商品总额：</span>
            <span>¥{{ order.totalPrice.toFixed(2) }}</span>
          </div>
          <div class="amount-row">
            <span>运费：</span>
            <span>¥{{ order.freight.toFixed(2) }}</span>
          </div>
          <el-divider />
          <div class="amount-row total">
            <span>应付金额：</span>
            <span class="total-amount">
              <span class="currency">¥</span>
              <span class="money">{{ (order.totalPrice + order.freight).toFixed(2) }}</span>
            </span>
          </div>
        </div>

        <el-divider v-if="!isExpired && !showQRCode" />

        <!-- 支付方式选择 -->
        <div v-if="!isExpired && !showQRCode" class="pay-method-section">
          <h3 class="section-title">选择支付方式</h3>
          <el-radio-group v-model="payMethod" class="pay-method-group">
            <el-radio
              v-for="method in payMethodOptions"
              :key="method.value"
              :value="method.value"
              class="pay-method-item"
            >
              <div class="pay-method-content">
                <div
                  class="pay-icon"
                  :style="{ backgroundColor: method.color }"
                >
                  <el-icon v-if="method.icon === 'CreditCard'"><CreditCard /></el-icon>
                  <span v-else>{{ method.label.charAt(0) }}</span>
                </div>
                <div class="pay-method-info">
                  <div class="pay-method-name">{{ method.label }}</div>
                  <div class="pay-method-desc">{{ method.description }}</div>
                </div>
              </div>
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 扫码支付 -->
        <div v-if="showQRCode && payStatus === 'paying'" class="qrcode-section">
          <div class="qrcode-header">
            <h3>{{ payMethodOptions.find(m => m.value === payMethod)?.label }}扫码支付</h3>
            <p>请使用{{ payMethodOptions.find(m => m.value === payMethod)?.label }}扫描下方二维码完成支付</p>
          </div>

          <div class="qrcode-container">
            <img :src="generateQRCodeURL()" alt="支付二维码" class="qrcode-image" />
          </div>

          <div class="qrcode-amount">
            <span>支付金额：</span>
            <span class="amount">¥{{ order.totalPrice.toFixed(2) }}</span>
          </div>

          <div class="qrcode-status">
            <el-icon class="loading-icon"><Loading /></el-icon>
            <span>等待支付中...</span>
          </div>

          <el-button
            type="text"
            class="cancel-btn"
            @click="handleCancelPay"
          >
            取消支付
          </el-button>
        </div>

        <!-- 支付成功 -->
        <div v-if="payStatus === 'success'" class="result-section">
          <el-result
            icon="success"
            title="支付成功"
            sub-title="您的订单已支付成功，即将跳转到订单列表"
          />
        </div>

        <!-- 支付失败 -->
        <div v-if="payStatus === 'failed'" class="result-section">
          <el-result
            icon="error"
            title="支付失败"
            sub-title="支付过程中出现问题，请重试或选择其他支付方式"
          >
            <template #extra>
              <el-button type="primary" @click="handleRetry">重新支付</el-button>
              <el-button @click="handleBack">返回订单</el-button>
            </template>
          </el-result>
        </div>

        <!-- 操作按钮 -->
        <div v-if="!isExpired && !showQRCode && payStatus === 'pending'" class="submit-section">
          <el-button size="large" @click="handleBack">
            返回订单
          </el-button>
          <el-button
            type="primary"
            size="large"
            :loading="paying"
            class="pay-button"
            @click="handlePay"
          >
            立即支付 ¥{{ (order.totalPrice + order.freight).toFixed(2) }}
          </el-button>
        </div>
      </div>

      <el-empty v-else-if="!loading" description="订单信息不存在" />
    </el-card>
  </div>
</template>

<style scoped>
.pay-page {
  max-width: 700px;
  margin: 0 auto;
  padding: 20px;
}

.pay-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.pay-content {
  padding: 10px 0;
}

.order-alert {
  margin-bottom: 20px;
}

.order-info {
  padding: 5px 0;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.info-label {
  color: #909399;
  width: 100px;
  flex-shrink: 0;
}

.info-value {
  color: #303133;
  font-weight: 500;
  flex: 1;
}

.countdown-section {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #fff5f5 0%, #fff 100%);
  border-radius: 8px;
  margin-bottom: 20px;
}

.countdown-icon {
  font-size: 20px;
  color: #f56c6c;
  margin-right: 8px;
}

.countdown-text {
  color: #606266;
  margin-right: 8px;
}

.countdown-time {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
  font-family: 'Courier New', monospace;
}

.expired-section {
  padding: 20px 0;
}

.products-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  margin-right: 12px;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e4e7ed;
  color: #909399;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.product-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.product-subtotal {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.amount-section {
  padding: 10px 0;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  color: #606266;
}

.amount-row.total {
  padding-top: 16px;
}

.total-amount {
  color: #f56c6c;
}

.currency {
  font-size: 18px;
  vertical-align: top;
}

.money {
  font-size: 28px;
  font-weight: bold;
}

.pay-method-section {
  padding: 10px 0;
}

.pay-method-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.pay-method-item {
  height: auto;
  min-height: 70px;
  margin: 0;
  padding: 16px;
  border: 2px solid #dcdfe6;
  border-radius: 12px;
  transition: all 0.3s;
}

.pay-method-item:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.pay-method-item.is-checked {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.pay-method-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.pay-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  flex-shrink: 0;
}

.pay-method-info {
  flex: 1;
}

.pay-method-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.pay-method-desc {
  font-size: 12px;
  color: #909399;
}

.qrcode-section {
  text-align: center;
  padding: 30px 0;
}

.qrcode-header h3 {
  font-size: 20px;
  color: #303133;
  margin: 0 0 8px 0;
}

.qrcode-header p {
  color: #909399;
  margin: 0 0 24px 0;
}

.qrcode-container {
  display: inline-block;
  padding: 20px;
  background: #fff;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  margin-bottom: 20px;
}

.qrcode-image {
  width: 200px;
  height: 200px;
}

.qrcode-amount {
  font-size: 16px;
  color: #606266;
  margin-bottom: 20px;
}

.qrcode-amount .amount {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}

.qrcode-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #409eff;
  margin-bottom: 16px;
}

.loading-icon {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.cancel-btn {
  color: #909399;
}

.result-section {
  padding: 20px 0;
}

.submit-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.pay-button {
  min-width: 200px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .pay-page {
    padding: 10px;
  }

  .pay-method-content {
    gap: 12px;
  }

  .pay-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }

  .pay-method-name {
    font-size: 14px;
  }

  .pay-method-desc {
    font-size: 11px;
  }

  .submit-section {
    flex-direction: column;
    gap: 12px;
  }

  .pay-button {
    width: 100%;
  }
}
</style>
