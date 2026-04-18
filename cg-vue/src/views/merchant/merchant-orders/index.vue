<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElTable, ElTableColumn, ElButton, ElTag, ElDialog, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElMessage, ElCard, ElPagination, ElDatePicker } from 'element-plus'
import { Check, Close, Search } from '@element-plus/icons-vue'
import { getMerchantOrders, updateOrderStatus, type Order } from '@/api/merchant'

const orders = ref<Order[]>([])
const loading = ref(false)
const filterStatus = ref('all')
const dialogVisible = ref(false)
const rejectReason = ref('')
const currentOrder = ref<Order | null>(null)
const searchKeyword = ref('')
const dateRange = ref<[Date, Date] | null>(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusOptions = [
  { value: 'all', label: '全部' },
  { value: 'pending', label: '待处理' },
  { value: 'confirmed', label: '已确认' },
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
      order.userName.toLowerCase().includes(keyword) ||
      order.serviceName.toLowerCase().includes(keyword)
    )
  }
  if (dateRange.value && dateRange.value[0] && dateRange.value[1]) {
    const [start, end] = dateRange.value
    result = result.filter(order => {
      const orderDate = new Date(order.appointmentTime)
      return orderDate >= start && orderDate <= end
    })
  }
  return result
})

const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredOrders.value.slice(start, end)
})

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    confirmed: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待处理',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const data = await getMerchantOrders()
    orders.value = data
    total.value = data.length
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleViewDetail = (row: Order) => {
  currentOrder.value = row
  dialogVisible.value = true
}

const handleConfirm = async (row: Order) => {
  try {
    await updateOrderStatus(row.id, 'confirmed')
    ElMessage.success('确认成功')
    fetchOrders()
  } catch (error) {
    ElMessage.error('确认失败')
  }
}

const handleComplete = async (row: Order) => {
  try {
    await updateOrderStatus(row.id, 'completed')
    ElMessage.success('完成成功')
    fetchOrders()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleCancel = async (row: Order) => {
  try {
    await updateOrderStatus(row.id, 'cancelled')
    ElMessage.success('取消成功')
    fetchOrders()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleProcess = (row: Order) => {
  currentOrder.value = row
  dialogVisible.value = true
}

const handleAccept = async () => {
  if (!currentOrder.value) return
  try {
    await updateOrderStatus(currentOrder.value.id, 'confirmed')
    ElMessage.success('确认成功')
    dialogVisible.value = false
    fetchOrders()
  } catch (error) {
    ElMessage.error('确认失败')
  }
}

const handleReject = async () => {
  if (!currentOrder.value) return
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写拒单原因')
    return
  }
  try {
    await updateOrderStatus(currentOrder.value.id, 'cancelled')
    ElMessage.success('拒单成功')
    dialogVisible.value = false
    rejectReason.value = ''
    fetchOrders()
  } catch (error) {
    ElMessage.error('拒单失败')
  }
}

const closeDialog = () => {
  dialogVisible.value = false
  rejectReason.value = ''
  currentOrder.value = null
}

const handleSearch = () => {
  currentPage.value = 1
}

const handlePageChange = (page: number) => {
  currentPage.value = page
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

onMounted(() => {
  fetchOrders()
})
</script>

<template>
  <div class="merchant-orders">
    <el-card shadow="hover" class="orders-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">订单处理</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索订单号/用户/服务"
              style="width: 200px; margin-right: 10px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 240px; margin-right: 10px"
              @change="handleSearch"
            />
            <el-select v-model="filterStatus" style="width: 140px" @change="handleSearch">
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="paginatedOrders" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="订单编号" width="100" />
        <el-table-column prop="userName" label="用户名称" width="120" />
        <el-table-column prop="serviceName" label="服务名称" min-width="150" />
        <el-table-column prop="petName" label="宠物名称" width="100">
          <template #default="{ row }">
            {{ (row as any).petName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="预约时间" width="180">
          <template #default="{ row }">
            {{ new Date(row.appointmentTime).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总价（元）" width="100">
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
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleViewDetail(row)"
            >
              查看详情
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              size="small"
              link
              @click="handleConfirm(row)"
            >
              确认
            </el-button>
            <el-button
              v-if="row.status === 'confirmed'"
              type="warning"
              size="small"
              link
              @click="handleComplete(row)"
            >
              完成
            </el-button>
            <el-button
              v-if="row.status === 'pending' || row.status === 'confirmed'"
              type="danger"
              size="small"
              link
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="filteredOrders.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="订单详情"
      width="500px"
      @close="closeDialog"
    >
      <el-form v-if="currentOrder" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ currentOrder.id }}</span>
        </el-form-item>
        <el-form-item label="用户名称">
          <span>{{ currentOrder.userName }}</span>
        </el-form-item>
        <el-form-item label="服务名称">
          <span>{{ currentOrder.serviceName }}</span>
        </el-form-item>
        <el-form-item label="预约时间">
          <span>{{ new Date(currentOrder.appointmentTime).toLocaleString() }}</span>
        </el-form-item>
        <el-form-item label="总价">
          <span>¥{{ currentOrder.totalPrice.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)" size="small">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item v-if="currentOrder.remark" label="备注">
          <span>{{ currentOrder.remark }}</span>
        </el-form-item>

        <el-form-item v-if="currentOrder.status === 'pending'" label="拒单原因">
          <el-input
            v-model="rejectReason"
            type="textarea"
            :rows="3"
            placeholder="请填写拒单原因"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div v-if="currentOrder && currentOrder.status === 'pending'">
          <el-button type="success" :icon="Check" @click="handleAccept">
            确认接单
          </el-button>
          <el-button type="danger" :icon="Close" @click="handleReject">
            拒单
          </el-button>
        </div>
        <div v-else>
          <el-button @click="closeDialog">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.merchant-orders {
  padding: 0;
}

.orders-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
