<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import {
  ElTable,
  ElTableColumn,
  ElButton,
  ElTag,
  ElDialog,
  ElForm,
  ElFormItem,
  ElMessage,
  ElMessageBox,
  ElCard,
  ElInput,
  ElSelect,
  ElOption,
  ElPagination,
  ElDivider,
  ElDatePicker,
  ElEmpty
} from 'element-plus'
import { View, Check, Close, Search, Van } from '@element-plus/icons-vue'
import {
  getMerchantProductOrders,
  updateProductOrderStatus,
  updateProductOrderLogistics,
  type ProductOrder
} from '@/api/merchant'
import { useAsync } from '@/composables/useAsync'
import { useSearch } from '@/composables/useSearch'
import { usePagination } from '@/composables/usePagination'

const {
  keyword,
  filters,
  debouncedKeyword,
  setKeyword,
  setFilter,
  removeFilter,
  clearFilters
} = useSearch(300)

const {
  page,
  pageSize,
  total,
  setTotal,
  setPageSize,
  setPage
} = usePagination(1, 10)

const {
  data: orders,
  loading,
  error,
  execute: fetchOrders
} = useAsync<ProductOrder[]>(getMerchantProductOrders, [])

const dialogVisible = ref(false)
const currentOrder = ref<ProductOrder | null>(null)
const logisticsCompany = ref('')
const trackingNumber = ref('')
const logisticsDialogVisible = ref(false)
const dateRange = ref<[Date, Date] | null>(null)

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
  if (!orders.value) return []

  let result = orders.value

  const statusFilter = filters.value.status
  if (statusFilter && statusFilter !== 'all') {
    result = result.filter(order => order.status === statusFilter)
  }

  if (debouncedKeyword.value) {
    const kw = debouncedKeyword.value.toLowerCase()
    result = result.filter(order =>
      order.id.toString().includes(kw) ||
      order.userName.toLowerCase().includes(kw) ||
      order.productName.toLowerCase().includes(kw)
    )
  }

  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0])
    startDate.setHours(0, 0, 0, 0)
    const endDate = new Date(dateRange.value[1])
    endDate.setHours(23, 59, 59, 999)

    result = result.filter(order => {
      const orderDate = new Date(order.createTime)
      return orderDate >= startDate && orderDate <= endDate
    })
  }

  return result
})

const paginatedOrders = computed(() => {
  const start = (page.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredOrders.value.slice(start, end)
})

watch(filteredOrders, (newVal) => {
  setTotal(newVal.length)
}, { immediate: true })

watch([debouncedKeyword, filters, dateRange], () => {
  setPage(1)
}, { deep: true })

watch(error, (newError) => {
  if (newError) {
    ElMessage.error(`获取商品订单列表失败: ${newError.message}`)
  }
})

const getStatusType = (status: string) => {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
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
    await ElMessageBox.confirm(
      `确认发货？物流公司：${logisticsCompany.value}，物流单号：${trackingNumber.value}`,
      '发货确认',
      {
        confirmButtonText: '确认发货',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await updateProductOrderLogistics(
      currentOrder.value.id,
      logisticsCompany.value,
      trackingNumber.value
    )
    await updateProductOrderStatus(currentOrder.value.id, 'shipped')

    ElMessage.success('发货成功')
    logisticsDialogVisible.value = false
    dialogVisible.value = false
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发货失败，请重试')
    }
  }
}

const handleComplete = async (row: ProductOrder) => {
  try {
    await ElMessageBox.confirm(
      '确认将该订单标记为已完成？',
      '完成确认',
      {
        confirmButtonText: '确认完成',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await updateProductOrderStatus(row.id, 'completed')
    ElMessage.success('订单已完成')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败，请重试')
    }
  }
}

const handleCancel = async (row: ProductOrder) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      '请输入取消原因',
      '取消订单',
      {
        confirmButtonText: '确认取消',
        cancelButtonText: '返回',
        inputPattern: /\S+/,
        inputErrorMessage: '请输入取消原因',
        type: 'warning'
      }
    )

    await updateProductOrderStatus(row.id, 'cancelled')
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败，请重试')
    }
  }
}

const closeDialog = () => {
  dialogVisible.value = false
  currentOrder.value = null
}

const handleSizeChange = (val: number) => {
  setPageSize(val)
}

const handleCurrentChange = (val: number) => {
  setPage(val)
}

const handleSearch = () => {
  setPage(1)
}

const handleStatusChange = (val: string) => {
  if (val === 'all') {
    removeFilter('status')
  } else {
    setFilter('status', val)
  }
}

const handleDateChange = () => {
  setPage(1)
}

const handleReset = () => {
  setKeyword('')
  clearFilters()
  dateRange.value = null
  setPage(1)
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

const formatPrice = (price: number) => {
  return `¥${(price || 0).toFixed(2)}`
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
          <span class="card-title">商品订单管理</span>
          <el-button type="primary" size="small" @click="handleReset">
            重置筛选
          </el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索订单编号/用户名称/商品名称"
          style="width: 260px"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          :model-value="filters.status || 'all'"
          style="width: 140px"
          placeholder="订单状态"
          @change="handleStatusChange"
        >
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>

        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 280px"
          @change="handleDateChange"
        />
      </div>

      <el-table
        :data="paginatedOrders"
        v-loading="loading"
        style="width: 100%"
        stripe
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column prop="id" label="订单编号" width="100" align="center" />
        <el-table-column prop="userName" label="用户名称" width="120" />
        <el-table-column prop="productName" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column label="总价" width="100" align="right">
          <template #default="{ row }">
            <span class="price">{{ formatPrice(row.totalPrice) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'paid'"
              type="primary"
              size="small"
              link
              @click="handleShip(row)"
            >
              <el-icon><Van /></el-icon>
              发货
            </el-button>
            <el-button
              v-if="row.status === 'shipped'"
              type="success"
              size="small"
              link
              @click="handleComplete(row)"
            >
              <el-icon><Check /></el-icon>
              完成
            </el-button>
            <el-button
              v-if="row.status === 'pending' || row.status === 'paid'"
              type="danger"
              size="small"
              link
              @click="handleCancel(row)"
            >
              <el-icon><Close /></el-icon>
              取消
            </el-button>
            <el-button
              type="info"
              size="small"
              link
              @click="handleViewDetail(row)"
            >
              <el-icon><View /></el-icon>
              详情
            </el-button>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无订单数据" />
        </template>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
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
      width="650px"
      :close-on-click-modal="false"
      @close="closeDialog"
    >
      <el-form v-if="currentOrder" label-width="100px" class="detail-form">
        <el-divider content-position="left">基本信息</el-divider>
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
        <el-form-item label="订单总价">
          <span class="price">{{ formatPrice(currentOrder.totalPrice) }}</span>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)" size="small">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="下单时间">
          <span>{{ formatDate(currentOrder.createTime) }}</span>
        </el-form-item>

        <el-divider
          v-if="currentOrder.logisticsCompany || currentOrder.trackingNumber"
          content-position="left"
        >
          物流信息
        </el-divider>
        <el-form-item v-if="currentOrder.logisticsCompany" label="物流公司">
          <span>{{ currentOrder.logisticsCompany }}</span>
        </el-form-item>
        <el-form-item v-if="currentOrder.trackingNumber" label="物流单号">
          <span>{{ currentOrder.trackingNumber }}</span>
        </el-form-item>

        <el-divider
          v-if="currentOrder.receiverName || currentOrder.phone || currentOrder.address"
          content-position="left"
        >
          收货信息
        </el-divider>
        <el-form-item v-if="currentOrder.receiverName" label="收货人">
          <span>{{ currentOrder.receiverName }}</span>
        </el-form-item>
        <el-form-item v-if="currentOrder.phone" label="联系电话">
          <span>{{ currentOrder.phone }}</span>
        </el-form-item>
        <el-form-item v-if="currentOrder.address" label="收货地址">
          <span>{{ currentOrder.address }}</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button
            v-if="currentOrder && currentOrder.status === 'paid'"
            type="primary"
            @click="handleShip(currentOrder)"
          >
            <el-icon><Van /></el-icon>
            去发货
          </el-button>
          <el-button @click="closeDialog">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="logisticsDialogVisible"
      title="填写物流信息"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form label-width="100px" class="logistics-form">
        <el-form-item label="订单编号">
          <span v-if="currentOrder">{{ currentOrder.id }}</span>
        </el-form-item>
        <el-form-item label="商品名称">
          <span v-if="currentOrder">{{ currentOrder.productName }}</span>
        </el-form-item>
        <el-form-item label="物流公司" required>
          <el-select
            v-model="logisticsCompany"
            placeholder="请选择物流公司"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="item in logisticsCompanies"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" required>
          <el-input
            v-model="trackingNumber"
            placeholder="请输入物流单号"
            clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="logisticsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmShip">
          <el-icon><Van /></el-icon>
          确认发货
        </el-button>
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
  flex-wrap: wrap;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.price {
  color: #f56c6c;
  font-weight: 600;
}

.detail-form {
  padding: 10px 20px;
}

.logistics-form {
  padding: 10px 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-divider__text) {
  font-weight: 600;
  color: #409eff;
}
</style>
