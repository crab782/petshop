<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantAppointments, updateAppointmentStatus, type Appointment } from '@/api/merchant'
import { useAsync } from '@/composables/useAsync'
import { useSearch } from '@/composables/useSearch'
import { usePagination } from '@/composables/usePagination'

// 预约数据
const appointments = ref<Appointment[]>([])

// 使用 composables
const { data, loading, error, execute: fetchAppointments } = useAsync<Appointment[]>(getMerchantAppointments)
const { keyword, filters, debouncedKeyword, setKeyword, setFilter, clearFilters } = useSearch(300)
const { page, pageSize, total, setTotal, setPageSize, setPage } = usePagination(1, 10)

// 状态选项
const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'pending', label: '待处理' },
  { value: 'confirmed', label: '已确认' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' }
]

// 日期范围筛选
const dateRange = ref<[Date, Date] | null>(null)

// 获取预约列表
const loadAppointments = async () => {
  await fetchAppointments()
  if (data.value) {
    appointments.value = data.value
    setTotal(data.value.length)
  }
}

// 筛选后的预约列表
const filteredAppointments = ref<Appointment[]>([])

// 应用筛选
const applyFilters = () => {
  let result = [...appointments.value]

  // 关键字搜索
  if (debouncedKeyword.value) {
    const searchLower = debouncedKeyword.value.toLowerCase()
    result = result.filter(apt => 
      apt.id.toString().includes(searchLower) ||
      apt.userName.toLowerCase().includes(searchLower) ||
      apt.serviceName.toLowerCase().includes(searchLower) ||
      apt.petName.toLowerCase().includes(searchLower)
    )
  }

  // 状态筛选
  if (filters.value.status) {
    result = result.filter(apt => apt.status === filters.value.status)
  }

  // 日期范围筛选
  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0])
    const endDate = new Date(dateRange.value[1])
    endDate.setHours(23, 59, 59, 999)
    
    result = result.filter(apt => {
      const aptDate = new Date(apt.appointmentTime)
      return aptDate >= startDate && aptDate <= endDate
    })
  }

  filteredAppointments.value = result
  setTotal(result.length)
}

// 监听筛选条件变化
watch([debouncedKeyword, filters, dateRange], applyFilters, { deep: true })

// 监听数据加载完成
watch(data, (newData) => {
  if (newData) {
    appointments.value = newData
    applyFilters()
  }
})

// 监听错误
watch(error, (newError) => {
  if (newError) {
    ElMessage.error(`加载预约列表失败: ${newError.message}`)
  }
})

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化金额
const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}

// 获取状态标签类型
const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: 'warning',
    confirmed: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待处理',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    pending: '#E6A23C',
    confirmed: '#409EFF',
    completed: '#67C23A',
    cancelled: '#909399'
  }
  return colorMap[status] || '#909399'
}

// 更新预约状态
const handleUpdateStatus = async (appointment: Appointment, newStatus: string) => {
  const statusText = getStatusText(newStatus)
  
  try {
    await ElMessageBox.confirm(
      `确定要将预约 #${appointment.id} 的状态更改为"${statusText}"吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 如果是取消操作，询问取消原因
    let rejectReason = ''
    if (newStatus === 'cancelled') {
      const { value } = await ElMessageBox.prompt(
        '请输入取消原因',
        '取消预约',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPattern: /\S+/,
          inputErrorMessage: '请输入取消原因'
        }
      )
      rejectReason = value
    }

    await updateAppointmentStatus(appointment.id, newStatus, rejectReason)
    ElMessage.success('状态更新成功')
    
    // 重新加载列表
    await loadAppointments()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('状态更新失败')
    }
  }
}

// 查看详情
const handleViewDetail = (appointment: Appointment) => {
  ElMessageBox.alert(
    `
    <div style="line-height: 2;">
      <p><strong>预约编号：</strong>${appointment.id}</p>
      <p><strong>用户名称：</strong>${appointment.userName}</p>
      <p><strong>宠物名称：</strong>${appointment.petName}</p>
      <p><strong>服务名称：</strong>${appointment.serviceName}</p>
      <p><strong>预约时间：</strong>${formatDateTime(appointment.appointmentTime)}</p>
      <p><strong>预约金额：</strong>${formatPrice(appointment.totalPrice)}</p>
      <p><strong>预约状态：</strong>${getStatusText(appointment.status)}</p>
      ${appointment.remark ? `<p><strong>备注：</strong>${appointment.remark}</p>` : ''}
    </div>
    `,
    '预约详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

// 获取可执行的状态操作
const getAvailableActions = (status: string) => {
  const actions: Array<{ status: string; text: string; type: string }> = []
  
  if (status === 'pending') {
    actions.push({ status: 'confirmed', text: '确认', type: 'primary' })
    actions.push({ status: 'cancelled', text: '取消', type: 'danger' })
  } else if (status === 'confirmed') {
    actions.push({ status: 'completed', text: '完成', type: 'success' })
    actions.push({ status: 'cancelled', text: '取消', type: 'danger' })
  }
  
  return actions
}

// 重置筛选
const handleReset = () => {
  setKeyword('')
  clearFilters()
  dateRange.value = null
}

// 组件挂载时加载数据
onMounted(() => {
  loadAppointments()
})
</script>

<template>
  <div class="appointments-container">
    <div class="header">
      <h2>预约订单列表</h2>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="filter-section">
      <div class="filter-row">
        <el-input
          v-model="keyword"
          placeholder="搜索预约编号、用户、服务或宠物"
          clearable
          class="search-input"
          @clear="setKeyword('')"
        >
          <template #prefix>
            <el-icon><i-ep-search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="filters.status"
          placeholder="选择状态"
          clearable
          class="status-select"
          @change="(val: string) => setFilter('status', val)"
        >
          <el-option
            v-for="option in statusOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>

        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          class="date-picker"
          value-format="YYYY-MM-DD"
        />

        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        :data="filteredAppointments.slice((page - 1) * pageSize, page * pageSize)"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="预约编号" width="100" align="center" />
        
        <el-table-column label="用户信息" width="150">
          <template #default="{ row }">
            <div class="user-info">
              <div class="user-name">{{ row.userName }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="petName" label="宠物名称" width="120" />

        <el-table-column prop="serviceName" label="服务名称" min-width="150" />

        <el-table-column label="预约时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.appointmentTime) }}
          </template>
        </el-table-column>

        <el-table-column label="金额" width="100" align="right">
          <template #default="{ row }">
            <span class="price">{{ formatPrice(row.totalPrice) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />

        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                size="small"
                link
                @click="handleViewDetail(row)"
              >
                查看
              </el-button>
              
              <template v-for="action in getAvailableActions(row.status)" :key="action.status">
                <el-button
                  :type="action.type as any"
                  size="small"
                  link
                  @click="handleUpdateStatus(row, action.status)"
                >
                  {{ action.text }}
                </el-button>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="setPageSize"
          @current-change="setPage"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.appointments-container {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-row {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  width: 300px;
}

.status-select {
  width: 150px;
}

.date-picker {
  width: 260px;
}

.table-container {
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  color: #303133;
}

.price {
  font-weight: 600;
  color: #F56C6C;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.pagination-container {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  background: #fff;
  border-top: 1px solid #EBEEF5;
}
</style>
