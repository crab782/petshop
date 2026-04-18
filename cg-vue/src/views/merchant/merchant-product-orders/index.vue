<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElTable, ElTableColumn, ElButton, ElTag, ElDialog, ElForm, ElFormItem, ElMessage, ElCard, ElInput, ElSelect, ElOption, ElPagination, ElDivider } from 'element-plus'
import { View, Check, Close, Delete, Search } from '@element-plus/icons-vue'
import { getMerchantProductOrders, updateProductOrderStatus, updateProductOrderLogistics, type ProductOrder } from '@/api/merchant'

const orders = ref<ProductOrder[]>([])
const loading = ref(false)
const filterStatus = ref('all')
const searchKeyword = ref('')
const dialogVisible = ref(false)
const currentOrder = ref<ProductOrder | null>(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const logisticsCompany = ref('')
const trackingNumber = ref('')
const logisticsDialogVisible = ref(false)

const logisticsCompanies = [
  { value: '顺丰速运', label: '顺丰速运' },
  { value: '圆通快递', label: '圆通快递' },
  { value: '中通快递', label: '中通快递' },
  { value: '韵达快递', label: '韵达快递' },
  { value: '申通快递', label: '申通快递' },
  { value: 'EMS', label: 'EMS' },
  { value: '京东物流', label: '京东物流' },
  { value: '菜鸟裹裹', label: '菜鸟裹裹' }
]

const statusOptions = [
  { value: 'all', label: '全部' },
  { value: 'pending', label: '待支付' },
  { value: 'paid', label: '已支付' },
  { value: 'shipped', label: '已发货' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' }
]

const filteredOrders = computed(() => {
  let result = orders.value

  if (filterStatus.value !== 'all') {
    result = result.filter(order => order.status === filterStatus.value)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(order =>
      order.id.toString().includes(keyword) ||
      order.userName.toLowerCase().includes(keyword)
    )
  }

  return result
})

const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  total.value = filteredOrders.value.length
  return filteredOrders.value.slice(start, end)
})

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    paid: 'primary',
    shipped: 'info',
    completed: 'success',
    cancelled: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待支付',
    paid: '已支付',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const data = await getMerchantProductOrders()
    orders.value = data
  } catch (error) {
    ElMessage.error('获取商品订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleViewDetail = (row: ProductOrder) => {
  currentOrder.value = { ...row }
  dialogVisible.value = true
}

const handleShip = (row: ProductOrder) => {
  currentOrder.value = { ...row }
  logisticsCompany.value = row.logisticsCompany || ''
  trackingNumber.value = row.trackingNumber || ''
  logisticsDialogVisible.value = true
}

const handleConfirmShip = async () => {
  if (!currentOrder.value) return
  if (!logisticsCompany.value) {
    ElMessage.warning('请选择物流公司')
    return
  }
  if (!trackingNumber.value) {
    ElMessage.warning('请输入物流单号')
    return
  }
  try {
    await updateProductOrderLogistics(currentOrder.value.id, logisticsCompany.value, trackingNumber.value)
    await updateProductOrderStatus(currentOrder.value.id, 'shipped')
    ElMessage.success('发货成功')
    logisticsDialogVisible.value = false
    dialogVisible.value = false
    fetchOrders()
  } catch (error) {
    ElMessage.error('发货失败')
  }
}

const handleComplete = async (row: ProductOrder) => {
  try {
    await updateProductOrderStatus(row.id, 'completed')
    ElMessage.success('订单已完成')
    fetchOrders()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleCancel = async (row: ProductOrder) => {
  try {
    await updateProductOrderStatus(row.id, 'cancelled')
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const closeDialog = () => {
  dialogVisible.value = false
  currentOrder.value = null
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
}

const handleSearch = () => {
  currentPage.value = 1
}

onMounted(() => {
  fetchOrders()
})
</script>

<template>
  <div class="merchant-product-orders">
    <el-card shadow="hover" class="orders-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">商品订单</span>
        </div>
      </template>

      <div class="filter-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索订单编号/用户名称"
          style="width: 200px"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="filterStatus" style="width: 140px" @change="handleSearch">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>

      <el-table :data="paginatedOrders" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="订单编号" width="100" />
        <el-table-column prop="userName" label="用户名称" width="120" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="总价（元）" width="100">
          <template #default="{ row }">
            ¥{{ row.totalPrice.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" width="180">
          <template #default="{ row }">
            {{ new Date(row.createTime).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'paid'"
              type="primary"
              size="small"
              link
              @click="handleShip(row)"
            >
              发货
            </el-button>
            <el-button
              v-if="row.status === 'shipped'"
              type="success"
              size="small"
              link
              @click="handleComplete(row)"
            >
              完成
            </el-button>
            <el-button
              v-if="row.status === 'pending' || row.status === 'paid'"
              type="danger"
              size="small"
              link
              @click="handleCancel(row)"
            >
              取消
            </el-button>
            <el-button
              type="info"
              size="small"
              link
              @click="handleViewDetail(row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="订单详情"
      width="600px"
      @close="closeDialog"
    >
      <el-form v-if="currentOrder" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ currentOrder.id }}</span>
        </el-form-item>
        <el-form-item label="用户名称">
          <span>{{ currentOrder.userName }}</span>
        </el-form-item>
        <el-form-item label="商品名称">
          <span>{{ currentOrder.productName }}</span>
        </el-form-item>
        <el-form-item label="商品数量">
          <span>{{ currentOrder.quantity }}</span>
        </el-form-item>
        <el-form-item label="总价">
          <span>¥{{ currentOrder.totalPrice.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)" size="small">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-form-item>
        <el-divider v-if="currentOrder.logisticsCompany || currentOrder.trackingNumber" />
        <el-form-item v-if="currentOrder.logisticsCompany" label="物流公司">
          <span>{{ currentOrder.logisticsCompany }}</span>
        </el-form-item>
        <el-form-item v-if="currentOrder.trackingNumber" label="物流单号">
          <span>{{ currentOrder.trackingNumber }}</span>
        </el-form-item>
        <el-divider v-if="currentOrder.receiverName || currentOrder.phone || currentOrder.address" />
        <el-form-item v-if="currentOrder.receiverName" label="收货人">
          <span>{{ currentOrder.receiverName }}</span>
        </el-form-item>
        <el-form-item v-if="currentOrder.phone" label="联系电话">
          <span>{{ currentOrder.phone }}</span>
        </el-form-item>
        <el-form-item v-if="currentOrder.address" label="收货地址">
          <span>{{ currentOrder.address }}</span>
        </el-form-item>
        <el-divider />
        <el-form-item label="下单时间">
          <span>{{ new Date(currentOrder.createTime).toLocaleString() }}</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <div v-if="currentOrder && currentOrder.status === 'paid'">
          <el-button type="primary" @click="handleShip(currentOrder)">
            去发货
          </el-button>
        </div>
        <el-button @click="closeDialog">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="logisticsDialogVisible"
      title="发货"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="物流公司" required>
          <el-select v-model="logisticsCompany" placeholder="请选择物流公司" style="width: 100%">
            <el-option
              v-for="item in logisticsCompanies"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" required>
          <el-input v-model="trackingNumber" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="logisticsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.merchant-product-orders {
  padding: 0;
}

.orders-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>