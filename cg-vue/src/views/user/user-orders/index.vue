<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, Refresh } from '@element-plus/icons-vue'
import {
  getProductOrders,
  getUserAppointments,
  cancelAppointment,
  cancelOrder,
  deleteOrder,
  batchCancelOrders,
  batchDeleteOrders,
  type ProductOrder,
  type Appointment
} from '@/api/user'

const router = useRouter()

const activeTab = ref('product')
const productLoading = ref(false)
const appointmentLoading = ref(false)
const productOrders = ref<ProductOrder[]>([])
const appointments = ref<Appointment[]>([])

const productStatusFilter = ref('')
const appointmentStatusFilter = ref('')
const dateRange = ref<[Date, Date] | null>(null)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const selectedProductOrders = ref<number[]>([])
const selectedAppointments = ref<number[]>([])

const productStatusOptions = [
  { label: '全部状态', value: '' },
  { label: '待支付', value: 'pending' },
  { label: '已支付', value: 'paid' },
  { label: '已发货', value: 'shipped' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' }
]

const appointmentStatusOptions = [
  { label: '全部状态', value: '' },
  { label: '待确认', value: 'pending' },
  { label: '已确认', value: 'confirmed' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' }
]

const productStatusTagType = (status: string) => {
  const map: Record<string, string> = {
    'pending': 'warning',
    'paid': 'primary',
    'shipped': 'info',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return map[status] || 'info'
}

const productStatusText = (status: string) => {
  const map: Record<string, string> = {
    'pending': '待支付',
    'paid': '已支付',
    'shipped': '已发货',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return map[status] || status
}

const appointmentStatusTagType = (status: string) => {
  const map: Record<string, string> = {
    'pending': 'warning',
    'confirmed': 'primary',
    'completed': 'success',
    'cancelled': 'info'
  }
  return map[status] || 'info'
}

const appointmentStatusText = (status: string) => {
  const map: Record<string, string> = {
    'pending': '待确认',
    'confirmed': '已确认',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return map[status] || status
}

const filteredProductOrders = computed(() => {
  let list = [...productOrders.value]
  
  if (productStatusFilter.value) {
    list = list.filter(item => item.status === productStatusFilter.value)
  }
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(item => 
      item.productName?.toLowerCase().includes(keyword) ||
      item.merchantName?.toLowerCase().includes(keyword)
    )
  }
  
  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0])
    const endDate = new Date(dateRange.value[1])
    endDate.setHours(23, 59, 59, 999)
    list = list.filter(item => {
      const orderDate = new Date(item.orderTime)
      return orderDate >= startDate && orderDate <= endDate
    })
  }
  
  return list
})

const filteredAppointments = computed(() => {
  let list = [...appointments.value]
  
  if (appointmentStatusFilter.value) {
    list = list.filter(item => item.status === appointmentStatusFilter.value)
  }
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(item => 
      item.serviceName?.toLowerCase().includes(keyword) ||
      item.merchantName?.toLowerCase().includes(keyword)
    )
  }
  
  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0])
    const endDate = new Date(dateRange.value[1])
    endDate.setHours(23, 59, 59, 999)
    list = list.filter(item => {
      const appointmentDate = new Date(item.appointmentTime)
      return appointmentDate >= startDate && appointmentDate <= endDate
    })
  }
  
  return list
})

const fetchProductOrders = async () => {
  productLoading.value = true
  try {
    const res = await getProductOrders()
    productOrders.value = res.data || []
    total.value = productOrders.value.length
  } catch {
    ElMessage.error('获取商品订单失败')
  } finally {
    productLoading.value = false
  }
}

const fetchAppointments = async () => {
  appointmentLoading.value = true
  try {
    const res = await getUserAppointments()
    appointments.value = res.data || []
  } catch {
    ElMessage.error('获取服务预约订单失败')
  } finally {
    appointmentLoading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleReset = () => {
  productStatusFilter.value = ''
  appointmentStatusFilter.value = ''
  dateRange.value = null
  searchKeyword.value = ''
  currentPage.value = 1
}

const handleProductSelectionChange = (selection: ProductOrder[]) => {
  selectedProductOrders.value = selection.map(item => item.id)
}

const handleAppointmentSelectionChange = (selection: Appointment[]) => {
  selectedAppointments.value = selection.map(item => item.id)
}

const handleViewProductOrder = (orderId: number) => {
  router.push(`/user/order/${orderId}`)
}

const handleViewAppointment = (appointmentId: number) => {
  router.push(`/user/appointment/${appointmentId}`)
}

const handleCancelProductOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelOrder(orderId)
    ElMessage.success('订单已取消')
    fetchProductOrders()
  } catch (error: unknown) {
    const err = error as { message?: string }
    if (err.message !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

const handleCancelAppointment = async (appointmentId: number) => {
  try {
    await ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelAppointment(appointmentId)
    ElMessage.success('预约已取消')
    fetchAppointments()
  } catch (error: unknown) {
    const err = error as { message?: string }
    if (err.message !== 'cancel') {
      ElMessage.error('取消预约失败')
    }
  }
}

const handleDeleteProductOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteOrder(orderId)
    ElMessage.success('订单已删除')
    fetchProductOrders()
  } catch (error: unknown) {
    const err = error as { message?: string }
    if (err.message !== 'cancel') {
      ElMessage.error('删除订单失败')
    }
  }
}

const handleBatchCancel = async () => {
  const ids = activeTab.value === 'product' ? selectedProductOrders.value : selectedAppointments.value
  if (ids.length === 0) {
    ElMessage.warning('请选择要取消的订单')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要取消选中的 ${ids.length} 个订单吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    if (activeTab.value === 'product') {
      await batchCancelOrders(ids)
      selectedProductOrders.value = []
      fetchProductOrders()
    } else {
      for (const id of ids) {
        await cancelAppointment(id)
      }
      selectedAppointments.value = []
      fetchAppointments()
    }
    ElMessage.success('批量取消成功')
  } catch (error: unknown) {
    const err = error as { message?: string }
    if (err.message !== 'cancel') {
      ElMessage.error('批量取消失败')
    }
  }
}

const handleBatchDelete = async () => {
  const ids = activeTab.value === 'product' ? selectedProductOrders.value : selectedAppointments.value
  if (ids.length === 0) {
    ElMessage.warning('请选择要删除的订单')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 个订单吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    if (activeTab.value === 'product') {
      await batchDeleteOrders(ids)
      selectedProductOrders.value = []
      fetchProductOrders()
    } else {
      ElMessage.warning('预约订单暂不支持批量删除')
      return
    }
    ElMessage.success('批量删除成功')
  } catch (error: unknown) {
    const err = error as { message?: string }
    if (err.message !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const canCancelProductOrder = (status: string) => {
  return status === 'pending'
}

const canCancelAppointment = (status: string) => {
  return status === 'pending'
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
  fetchProductOrders()
  fetchAppointments()
})
</script>

<template>
  <div class="user-orders">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">我的订单</span>
        </div>
      </template>

      <div class="filter-section">
        <el-form inline>
          <el-form-item label="关键词">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索商品/服务/商家"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 260px"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-tabs v-model="activeTab" class="order-tabs">
        <el-tab-pane label="商品订单" name="product">
          <div class="batch-actions" v-if="selectedProductOrders.length > 0">
            <span class="selected-count">已选择 {{ selectedProductOrders.length }} 项</span>
            <el-button type="warning" plain :icon="Refresh" @click="handleBatchCancel">
              批量取消
            </el-button>
            <el-button type="danger" plain :icon="Delete" @click="handleBatchDelete">
              批量删除
            </el-button>
          </div>
          
          <el-form inline style="margin-bottom: 16px;">
            <el-form-item label="订单状态">
              <el-select v-model="productStatusFilter" placeholder="全部状态" style="width: 120px">
                <el-option
                  v-for="item in productStatusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-form>

          <el-table
            v-loading="productLoading"
            :data="filteredProductOrders"
            style="width: 100%"
            stripe
            @selection-change="handleProductSelectionChange"
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column prop="id" label="订单编号" width="100" align="center" />
            <el-table-column prop="productName" label="商品名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="merchantName" label="商家名称" min-width="120" align="center" show-overflow-tooltip />
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column prop="totalPrice" label="总金额" width="100" align="center">
              <template #default="{ row }">
                <span class="price">¥{{ row.totalPrice?.toFixed(2) || '0.00' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="订单状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="productStatusTagType(row.status)" size="small">
                  {{ productStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="下单时间" width="160" align="center">
              <template #default="{ row }">
                {{ formatDate(row.orderTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" plain @click="handleViewProductOrder(row.id)">
                  详情
                </el-button>
                <el-button
                  v-if="canCancelProductOrder(row.status)"
                  type="warning"
                  size="small"
                  plain
                  @click="handleCancelProductOrder(row.id)"
                >
                  取消
                </el-button>
                <el-button
                  v-if="row.status === 'cancelled' || row.status === 'completed'"
                  type="danger"
                  size="small"
                  plain
                  @click="handleDeleteProductOrder(row.id)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无商品订单" />
            </template>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="服务预约" name="appointment">
          <div class="batch-actions" v-if="selectedAppointments.length > 0">
            <span class="selected-count">已选择 {{ selectedAppointments.length }} 项</span>
            <el-button type="warning" plain :icon="Refresh" @click="handleBatchCancel">
              批量取消
            </el-button>
          </div>
          
          <el-form inline style="margin-bottom: 16px;">
            <el-form-item label="预约状态">
              <el-select v-model="appointmentStatusFilter" placeholder="全部状态" style="width: 120px">
                <el-option
                  v-for="item in appointmentStatusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-form>

          <el-table
            v-loading="appointmentLoading"
            :data="filteredAppointments"
            style="width: 100%"
            stripe
            @selection-change="handleAppointmentSelectionChange"
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column prop="id" label="预约编号" width="100" align="center" />
            <el-table-column prop="serviceName" label="服务名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="merchantName" label="商家名称" min-width="120" align="center" show-overflow-tooltip />
            <el-table-column label="预约时间" width="160" align="center">
              <template #default="{ row }">
                {{ formatDate(row.appointmentTime) }}
              </template>
            </el-table-column>
            <el-table-column label="总金额" width="100" align="center">
              <template #default="{ row }">
                <span class="price">¥{{ row.totalPrice?.toFixed(2) || '0.00' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="appointmentStatusTagType(row.status)" size="small">
                  {{ appointmentStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" plain @click="handleViewAppointment(row.id)">
                  详情
                </el-button>
                <el-button
                  v-if="canCancelAppointment(row.status)"
                  type="warning"
                  size="small"
                  plain
                  @click="handleCancelAppointment(row.id)"
                >
                  取消
                </el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无服务预约订单" />
            </template>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.user-orders {
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

.filter-section {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.filter-section :deep(.el-form-item) {
  margin-bottom: 0;
}

.batch-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.selected-count {
  font-size: 14px;
  color: #606266;
}

.order-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

@media (max-width: 768px) {
  .user-orders {
    padding: 10px;
  }

  .filter-section :deep(.el-form-item) {
    width: 100%;
    margin-right: 0;
    margin-bottom: 12px;
  }

  .filter-section :deep(.el-input),
  .filter-section :deep(.el-date-editor) {
    width: 100% !important;
  }
}
</style>
