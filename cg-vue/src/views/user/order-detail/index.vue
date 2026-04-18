<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCheck, Clock, Close, InfoFilled, Van, Location, Phone, User } from '@element-plus/icons-vue'
import {
  getOrderById,
  cancelOrder,
  refundOrder,
  confirmReceiveOrder,
  type OrderDetail
} from '@/api/user'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const orderDetail = ref<OrderDetail | null>(null)
const cancelDialogVisible = ref(false)
const refundDialogVisible = ref(false)
const confirmDialogVisible = ref(false)
const refundReason = ref('')

const orderId = computed(() => Number(route.params.id))

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    'pending': 'warning',
    'paid': 'primary',
    'shipped': 'info',
    'completed': 'success',
    'cancelled': 'danger',
    'refunding': 'warning',
    'refunded': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'pending': '待支付',
    'paid': '已支付',
    'shipped': '已发货',
    'completed': '已完成',
    'cancelled': '已取消',
    'refunding': '退款中',
    'refunded': '已退款'
  }
  return map[status] || status
}

const getStatusColor = (status: string) => {
  const map: Record<string, string> = {
    'pending': '#E6A23C',
    'paid': '#409EFF',
    'shipped': '#909399',
    'completed': '#67C23A',
    'cancelled': '#F56C6C',
    'refunding': '#E6A23C',
    'refunded': '#909399'
  }
  return map[status] || '#909399'
}

const canCancel = computed(() => {
  return orderDetail.value?.status === 'pending'
})

const canRefund = computed(() => {
  return orderDetail.value?.status === 'paid' || orderDetail.value?.status === 'shipped'
})

const canConfirm = computed(() => {
  return orderDetail.value?.status === 'shipped'
})

const canPay = computed(() => {
  return orderDetail.value?.status === 'pending'
})

const fetchOrderDetail = async () => {
  if (!orderId.value) return
  loading.value = true
  try {
    const res = await getOrderById(orderId.value)
    orderDetail.value = res.data
  } catch {
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  cancelDialogVisible.value = true
}

const confirmCancel = async () => {
  try {
    await cancelOrder(orderId.value)
    ElMessage.success('订单已取消')
    cancelDialogVisible.value = false
    fetchOrderDetail()
  } catch {
    ElMessage.error('取消订单失败')
  }
}

const handleRefund = () => {
  refundDialogVisible.value = true
}

const confirmRefund = async () => {
  if (!refundReason.value.trim()) {
    ElMessage.warning('请输入退款原因')
    return
  }
  try {
    await refundOrder(orderId.value, refundReason.value)
    ElMessage.success('退款申请已提交')
    refundDialogVisible.value = false
    refundReason.value = ''
    fetchOrderDetail()
  } catch {
    ElMessage.error('退款申请失败')
  }
}

const handleConfirm = () => {
  confirmDialogVisible.value = true
}

const confirmReceive = async () => {
  try {
    await confirmReceiveOrder(orderId.value)
    ElMessage.success('已确认收货')
    confirmDialogVisible.value = false
    fetchOrderDetail()
  } catch {
    ElMessage.error('确认收货失败')
  }
}

const handlePay = () => {
  router.push({ path: '/user/pay', query: { orderId: orderId.value } })
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<template>
  <div class="order-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="title">订单详情</span>
          <el-button @click="router.back()">返回</el-button>
        </div>
      </template>

      <div v-if="orderDetail">
        <el-card class="status-card" :style="{ borderColor: getStatusColor(orderDetail.status) }">
          <div class="status-content">
            <div class="status-icon" :style="{ backgroundColor: getStatusColor(orderDetail.status) }">
              <el-icon v-if="orderDetail.status === 'completed'" :size="32"><CircleCheck /></el-icon>
              <el-icon v-else-if="orderDetail.status === 'pending'" :size="32"><Clock /></el-icon>
              <el-icon v-else-if="orderDetail.status === 'cancelled'" :size="32"><Close /></el-icon>
              <el-icon v-else-if="orderDetail.status === 'shipped'" :size="32"><Van /></el-icon>
              <el-icon v-else :size="32"><InfoFilled /></el-icon>
            </div>
            <div class="status-info">
              <div class="status-text">{{ getStatusText(orderDetail.status) }}</div>
              <div class="status-desc">
                <span v-if="orderDetail.status === 'pending'">请尽快完成支付</span>
                <span v-else-if="orderDetail.status === 'paid'">商家将尽快发货</span>
                <span v-else-if="orderDetail.status === 'shipped'">商品正在配送中</span>
                <span v-else-if="orderDetail.status === 'completed'">感谢您的购买</span>
                <span v-else-if="orderDetail.status === 'cancelled'">订单已取消</span>
                <span v-else-if="orderDetail.status === 'refunding'">退款申请处理中</span>
                <span v-else-if="orderDetail.status === 'refunded'">退款已完成</span>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="info-card">
          <template #header>
            <span class="section-title">订单信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单编号">{{ orderDetail.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ formatDate(orderDetail.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="支付方式">{{ orderDetail.payMethod || '-' }}</el-descriptions-item>
            <el-descriptions-item label="订单备注">{{ orderDetail.remark || '-' }}</el-descriptions-item>
            <el-descriptions-item label="支付时间" v-if="orderDetail.payTime">{{ formatDate(orderDetail.payTime) }}</el-descriptions-item>
            <el-descriptions-item label="发货时间" v-if="orderDetail.shipTime">{{ formatDate(orderDetail.shipTime) }}</el-descriptions-item>
            <el-descriptions-item label="完成时间" v-if="orderDetail.completeTime">{{ formatDate(orderDetail.completeTime) }}</el-descriptions-item>
            <el-descriptions-item label="取消时间" v-if="orderDetail.cancelTime">{{ formatDate(orderDetail.cancelTime) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card class="info-card">
          <template #header>
            <span class="section-title">商品信息</span>
          </template>
          <div class="product-list">
            <div v-for="item in orderDetail.items" :key="item.id" class="product-item">
              <el-image
                :src="item.productImage || 'https://via.placeholder.com/80x80'"
                fit="cover"
                class="product-image"
              >
                <template #error>
                  <div class="image-placeholder">
                    <el-icon :size="32"><InfoFilled /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="product-info">
                <div class="product-name">{{ item.productName }}</div>
                <div class="product-price">¥{{ item.price.toFixed(2) }} x {{ item.quantity }}</div>
              </div>
              <div class="product-subtotal">¥{{ item.subtotal.toFixed(2) }}</div>
            </div>
          </div>
          <div class="total-section">
            <div class="total-row">
              <span>商品总价</span>
              <span>¥{{ (orderDetail.totalPrice - orderDetail.freight).toFixed(2) }}</span>
            </div>
            <div class="total-row">
              <span>运费</span>
              <span>¥{{ orderDetail.freight.toFixed(2) }}</span>
            </div>
            <el-divider />
            <div class="total-row final-total">
              <span>实付金额</span>
              <span class="price">¥{{ orderDetail.totalPrice.toFixed(2) }}</span>
            </div>
          </div>
        </el-card>

        <el-card class="info-card">
          <template #header>
            <span class="section-title">收货信息</span>
          </template>
          <div class="address-info">
            <div class="address-contact">
              <el-icon><User /></el-icon>
              <span class="name">{{ orderDetail.address.name }}</span>
              <el-icon style="margin-left: 16px;"><Phone /></el-icon>
              <span class="phone">{{ orderDetail.address.phone }}</span>
            </div>
            <div class="address-detail">
              <el-icon><Location /></el-icon>
              <span>{{ orderDetail.address.address }}</span>
            </div>
          </div>
        </el-card>

        <el-card class="info-card" v-if="orderDetail.timeline && orderDetail.timeline.length > 0">
          <template #header>
            <span class="section-title">物流跟踪</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in orderDetail.timeline"
              :key="index"
              :timestamp="item.time"
              :color="index === 0 ? getStatusColor(orderDetail.status) : '#909399'"
              :hollow="index !== 0"
            >
              <div class="timeline-content">
                <div class="timeline-status">{{ item.status }}</div>
                <div class="timeline-desc">{{ item.description }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>

        <div class="action-section">
          <el-button v-if="canPay" type="primary" size="large" @click="handlePay">去支付</el-button>
          <el-button v-if="canConfirm" type="success" size="large" @click="handleConfirm">确认收货</el-button>
          <el-button v-if="canCancel" type="danger" size="large" plain @click="handleCancel">取消订单</el-button>
          <el-button v-if="canRefund" type="warning" size="large" plain @click="handleRefund">申请退款</el-button>
        </div>
      </div>

      <el-empty v-else description="订单不存在" />
    </el-card>

    <el-dialog v-model="cancelDialogVisible" title="确认取消订单" width="400px">
      <span>确定要取消该订单吗？此操作不可恢复。</span>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmCancel">确认取消</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="refundDialogVisible" title="申请退款" width="500px">
      <el-form label-width="80px">
        <el-form-item label="退款原因" required>
          <el-input
            v-model="refundReason"
            type="textarea"
            :rows="4"
            placeholder="请输入退款原因"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRefund">提交申请</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="confirmDialogVisible" title="确认收货" width="400px">
      <span>确认已收到商品吗？确认后订单将完成。</span>
      <template #footer>
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="success" @click="confirmReceive">确认收货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.order-detail {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.status-card {
  margin-bottom: 20px;
  border-left: 4px solid;
}

.status-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.status-info {
  flex: 1;
}

.status-text {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.status-desc {
  font-size: 14px;
  color: #909399;
}

.info-card {
  margin-bottom: 20px;
}

.section-title {
  font-weight: bold;
  font-size: 16px;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.product-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  flex-shrink: 0;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
}

.product-info {
  flex: 1;
  margin-left: 16px;
}

.product-name {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 4px;
  color: #303133;
}

.product-price {
  font-size: 14px;
  color: #909399;
}

.product-subtotal {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}

.total-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.total-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
  color: #606266;
}

.final-total {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.price {
  font-size: 20px;
  color: #f56c6c;
}

.address-info {
  padding: 8px 0;
}

.address-contact {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  color: #606266;
}

.address-contact .el-icon {
  margin-right: 4px;
  color: #909399;
}

.address-contact .name {
  font-weight: bold;
  color: #303133;
}

.address-detail {
  display: flex;
  align-items: flex-start;
  color: #606266;
  font-size: 14px;
}

.address-detail .el-icon {
  margin-right: 4px;
  color: #909399;
  margin-top: 2px;
}

.timeline-content {
  padding: 4px 0;
}

.timeline-status {
  font-weight: bold;
  margin-bottom: 4px;
  color: #303133;
}

.timeline-desc {
  font-size: 14px;
  color: #909399;
}

.action-section {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .order-detail {
    padding: 10px;
  }

  .action-section {
    flex-wrap: wrap;
  }

  .action-section .el-button {
    flex: 1;
    min-width: 120px;
  }
}
</style>
