<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElTable, ElTableColumn, ElButton, ElTag, ElDialog, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElMessage, ElMessageBox, ElCard, ElPagination, ElDatePicker, ElEmpty } from 'element-plus'
import { Check, Close, Search, Refresh } from '@element-plus/icons-vue'
import { getMerchantOrders, updateOrderStatus, type Order } from '@/api/merchant'
import { useAsync } from '@/composables/useAsync'
import { useSearch } from '@/composables/useSearch'
import { usePagination } from '@/composables/usePagination'

// 使用 composables
const { keyword, filters, debouncedKeyword, setKeyword, setFilter, clearFilters } = useSearch(300)
const { page, pageSize, total, setTotal, setPageSize, setPage, reset: resetPagination } = usePagination(1, 10)

// 订单数据
const orders = ref<Order[]>([])
const dialogVisible = ref(false)
const rejectReason = ref('')
const currentOrder = ref<Order | null>(null)
const dateRange = ref<[Date, Date] | null>(null)

// 使用 useAsync 处理获取订单
const { loading: fetchLoading, error: fetchError, execute: executeFetchOrders } = useAsync(async () => {
  try {
    const data = await getMerchantOrders()
    orders.value = data
    return data
  } catch (err) {
    ElMessage.error('获取订单列表失败')
    return []
  }
})

// 使用 useAsync 处理状态更新
const { loading: updateLoading, execute: executeUpdateStatus } = useAsync(async (orderId: number, status: string, rejectReason?: string) => {
  return await updateOrderStatus(orderId, status, rejectReason)
})

// 状态选项
const statusOptions = [
  { value: 'all', label: '全部' },
  { value: 'pending', label: '待处理' },
  { value: 'confirmed', label: '已确认' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' }
]

// 获取当前状态筛选值
const filterStatus = computed({
  get: () => filters.value.status || 'all',
  set: (value: string) => setFilter('status', value === 'all' ? undefined : value)
})

// 根据筛选条件过滤订单
const filteredOrders = computed(() => {
  let result = orders.value
  
  // 按状态筛选
  if (filters.value.status && filters.value.status !== 'all') {
    result = result.filter(order => order.status === filters.value.status)
  }
  
  // 按关键词搜索
  if (debouncedKeyword.value) {
    const kw = debouncedKeyword.value.toLowerCase()
    result = result.filter(order =>
      order.id.toString().includes(kw) ||
      order.userName.toLowerCase().includes(kw) ||
      order.serviceName.toLowerCase().includes(kw)
    )
  }
  
  // 按日期范围筛选
  if (dateRange.value && dateRange.value[0] && dateRange.value[1]) {
    const [start, end] = dateRange.value
    const endDate = new Date(end)
    endDate.setHours(23, 59, 59, 999)
    result = result.filter(order => {
      const orderDate = new Date(order.appointmentTime)
      return orderDate >= start && orderDate <= endDate
    })
  }
  
  return result
})

// 分页后的订单数据
const paginatedOrders = computed(() => {
  const start = (page.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredOrders.value.slice(start, end)
})

// 监听筛选条件变化，更新总数并重置页码
watch(filteredOrders, (newFiltered) => {
  setTotal(newFiltered.length)
  if (page.value > Math.ceil(newFiltered.length / pageSize.value)) {
    setPage(1)
  }
}, { immediate: true })

// 获取状态标签类型
const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    confirmed: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待处理',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

// 获取订单列表
const fetchOrders = async () => {
  await executeFetchOrders()
  if (fetchError.value) {
    ElMessage.error(fetchError.value.message || '获取订单列表失败')
  }
}

// 查看详情
const handleViewDetail = (row: Order) => {
  currentOrder.value = row
  dialogVisible.value = true
}

// 确认订单（带确认对话框）
const handleConfirm = async (row: Order) => {
  try {
    await ElMessageBox.confirm(
      '确定要确认该订单吗？确认后将通知用户。',
      '确认订单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    const result = await executeUpdateStatus(row.id, 'confirmed')
    if (result) {
      ElMessage.success('订单确认成功')
      row.status = 'confirmed'
    } else {
      ElMessage.error('订单确认失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败，请重试')
    }
  }
}

// 完成订单（带确认对话框）
const handleComplete = async (row: Order) => {
  try {
    await ElMessageBox.confirm(
      '确定要将该订单标记为已完成吗？',
      '完成订单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    
    const result = await executeUpdateStatus(row.id, 'completed')
    if (result) {
      ElMessage.success('订单已完成')
      row.status = 'completed'
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败，请重试')
    }
  }
}

// 取消订单（带确认对话框）
const handleCancel = async (row: Order) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      '请输入取消原因（可选）',
      '取消订单',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '返回',
        type: 'warning',
        inputPlaceholder: '请输入取消原因'
      }
    )
    
    const result = await executeUpdateStatus(row.id, 'cancelled')
    if (result) {
      ElMessage.success('订单已取消')
      row.status = 'cancelled'
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败，请重试')
    }
  }
}

// 处理订单（打开详情弹窗）
const handleProcess = (row: Order) => {
  currentOrder.value = row
  dialogVisible.value = true
}

// 接单（在详情弹窗中）
const handleAccept = async () => {
  if (!currentOrder.value) return
  
  try {
    const result = await executeUpdateStatus(currentOrder.value.id, 'confirmed')
    if (result) {
      ElMessage.success('接单成功')
      currentOrder.value.status = 'confirmed'
      dialogVisible.value = false
    } else {
      ElMessage.error('接单失败')
    }
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

// 拒单（在详情弹窗中）
const handleReject = async () => {
  if (!currentOrder.value) return
  
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写拒单原因')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要拒绝该订单吗？原因：${rejectReason.value}`,
      '拒单确认',
      {
        confirmButtonText: '确定拒单',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const result = await executeUpdateStatus(currentOrder.value.id, 'cancelled', rejectReason.value)
    if (result) {
      ElMessage.success('拒单成功')
      currentOrder.value.status = 'cancelled'
      dialogVisible.value = false
      rejectReason.value = ''
    } else {
      ElMessage.error('拒单失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败，请重试')
    }
  }
}

// 关闭弹窗
const closeDialog = () => {
  dialogVisible.value = false
  rejectReason.value = ''
  currentOrder.value = null
}

// 搜索
const handleSearch = () => {
  setPage(1)
}

// 重置筛选
const handleReset = () => {
  keyword.value = ''
  clearFilters()
  dateRange.value = null
  resetPagination()
}

// 分页变化
const handlePageChange = (newPage: number) => {
  setPage(newPage)
}

// 每页条数变化
const handleSizeChange = (newSize: number) => {
  setPageSize(newSize)
}

// 初始化
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
              v-model="keyword"
              placeholder="搜索订单号/用户/服务"
              style="width: 200px"
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
              style="width: 240px"
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
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          </div>
        </div>
      </template>

      <el-table 
        :data="paginatedOrders" 
        v-loading="fetchLoading" 
        style="width: 100%"
        :empty-text="fetchError ? '加载失败，请重试' : '暂无订单数据'"
      >
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
              :loading="updateLoading"
              @click="handleConfirm(row)"
            >
              确认
            </el-button>
            <el-button
              v-if="row.status === 'confirmed'"
              type="warning"
              size="small"
              link
              :loading="updateLoading"
              @click="handleComplete(row)"
            >
              完成
            </el-button>
            <el-button
              v-if="row.status === 'pending' || row.status === 'confirmed'"
              type="danger"
              size="small"
              link
              :loading="updateLoading"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
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
          <el-button type="success" :icon="Check" :loading="updateLoading" @click="handleAccept">
            确认接单
          </el-button>
          <el-button type="danger" :icon="Close" :loading="updateLoading" @click="handleReject">
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
