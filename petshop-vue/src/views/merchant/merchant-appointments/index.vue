<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  ElTable,
  ElTableColumn,
  ElButton,
  ElTag,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElOption,
  ElMessage,
  ElCard,
  ElPagination,
  ElDatePicker,
  ElEmpty
} from 'element-plus'
import { Check, Close, Download, Refresh } from '@element-plus/icons-vue'
import { getMerchantAppointments, updateAppointmentStatus, type Appointment } from '@/api/merchant'
import { usePagination } from '@/composables/usePagination'
import dayjs from 'dayjs'

const appointments = ref<Appointment[]>([])
const loading = ref(false)
const filterStatus = ref('all')
const dialogVisible = ref(false)
const rejectReason = ref('')
const currentAppointment = ref<Appointment | null>(null)
const rejectDialogVisible = ref(false)

const searchKeyword = ref('')
const searchUserName = ref('')
const dateRange = ref<[string, string] | null>(null)

const { page, pageSize, total, setTotal, setPage, setPageSize } = usePagination(1, 10)

const statusOptions = [
  { value: 'all', label: '全部' },
  { value: 'pending', label: '待确认' },
  { value: 'confirmed', label: '已确认' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' }
]

const filteredAppointments = computed(() => {
  let result = appointments.value

  if (filterStatus.value !== 'all') {
    result = result.filter(item => item.status === filterStatus.value)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(item => item.id.toString().includes(keyword))
  }

  if (searchUserName.value) {
    const userName = searchUserName.value.toLowerCase()
    result = result.filter(item => item.userName.toLowerCase().includes(userName))
  }

  if (dateRange.value && dateRange.value.length === 2) {
    const [startDate, endDate] = dateRange.value
    const start = dayjs(startDate).startOf('day')
    const end = dayjs(endDate).endOf('day')
    result = result.filter(item => {
      const appointmentDate = dayjs(item.appointmentTime)
      return appointmentDate.isAfter(start) && appointmentDate.isBefore(end)
    })
  }

  return result
})

const paginatedAppointments = computed(() => {
  const start = (page.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredAppointments.value.slice(start, end)
})

const totalCount = computed(() => filteredAppointments.value.length)

const getStatusType = (status: string): 'primary' | 'danger' | 'success' | 'warning' | 'info' => {
  const map: Record<string, 'primary' | 'danger' | 'success' | 'warning' | 'info'> = {
    pending: 'warning',
    confirmed: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const fetchAppointments = async () => {
  loading.value = true
  try {
    const res = await getMerchantAppointments()
    appointments.value = res.data || []
    setTotal(appointments.value.length)
  } catch (error) {
    console.error('获取预约列表失败:', error)
    ElMessage.error('获取预约列表失败，请稍后重试')
    appointments.value = []
  } finally {
    loading.value = false
  }
}

const handleViewDetail = (row: Appointment) => {
  currentAppointment.value = row
  dialogVisible.value = true
}

const handleProcess = (row: Appointment) => {
  currentAppointment.value = row
  dialogVisible.value = true
}

const handleAccept = async () => {
  if (!currentAppointment.value) return
  try {
    await updateAppointmentStatus(currentAppointment.value.id, 'confirmed')
    ElMessage.success('接单成功')
    dialogVisible.value = false
    fetchAppointments()
  } catch (error) {
    console.error('接单失败:', error)
    ElMessage.error('接单失败，请稍后重试')
  }
}

const handleComplete = async (row: Appointment) => {
  try {
    await updateAppointmentStatus(row.id, 'completed')
    ElMessage.success('已完成')
    fetchAppointments()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const handleReject = () => {
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!currentAppointment.value) return
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写拒单原因')
    return
  }
  try {
    await updateAppointmentStatus(currentAppointment.value.id, 'cancelled', rejectReason.value)
    ElMessage.success('拒单成功')
    dialogVisible.value = false
    rejectDialogVisible.value = false
    rejectReason.value = ''
    fetchAppointments()
  } catch (error) {
    console.error('拒单失败:', error)
    ElMessage.error('拒单失败，请稍后重试')
  }
}

const handleCancel = async (row: Appointment) => {
  try {
    await updateAppointmentStatus(row.id, 'cancelled')
    ElMessage.success('已取消')
    fetchAppointments()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const closeDialog = () => {
  dialogVisible.value = false
  rejectReason.value = ''
  currentAppointment.value = null
}

const closeRejectDialog = () => {
  rejectDialogVisible.value = false
  rejectReason.value = ''
}

const handlePageChange = (newPage: number) => {
  setPage(newPage)
}

const handleSizeChange = (newSize: number) => {
  setPageSize(newSize)
}

const resetFilters = () => {
  searchKeyword.value = ''
  searchUserName.value = ''
  dateRange.value = null
  filterStatus.value = 'all'
  setPage(1)
}

const exportAppointments = () => {
  const data = filteredAppointments.value
  if (data.length === 0) {
    ElMessage.warning('没有可导出的数据')
    return
  }

  const headers = ['预约编号', '用户名称', '宠物名称', '服务名称', '预约时间', '总价', '状态', '备注']
  const rows = data.map(item => [
    item.id,
    item.userName,
    item.petName,
    item.serviceName,
    dayjs(item.appointmentTime).format('YYYY-MM-DD HH:mm:ss'),
    `¥${item.totalPrice.toFixed(2)}`,
    getStatusText(item.status),
    item.remark || '-'
  ])

  const csvContent = [headers, ...rows]
    .map(row => row.map(cell => `"${cell}"`).join(','))
    .join('\n')

  const BOM = '\uFEFF'
  const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `预约订单_${dayjs().format('YYYY-MM-DD_HH-mm')}.csv`
  link.click()
  URL.revokeObjectURL(link.href)
  ElMessage.success('导出成功')
}

onMounted(() => {
  fetchAppointments()
})
</script>

<template>
  <div class="merchant-appointments">
    <el-card shadow="hover" class="appointments-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">服务预约订单</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Refresh" @click="fetchAppointments">刷新</el-button>
            <el-button type="success" :icon="Download" @click="exportAppointments">
              导出
            </el-button>
          </div>
        </div>
      </template>

      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索预约编号"
          style="width: 160px"
          clearable
          @keyup.enter="setPage(1)"
        />
        <el-input
          v-model="searchUserName"
          placeholder="搜索用户名称"
          style="width: 160px"
          clearable
          @keyup.enter="setPage(1)"
        />
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 260px"
          @change="setPage(1)"
        />
        <el-select v-model="filterStatus" style="width: 140px" @change="setPage(1)">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-button @click="resetFilters">重置</el-button>
      </div>

      <el-table :data="paginatedAppointments" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="预约编号" width="100" />
        <el-table-column prop="userName" label="用户名称" width="120" />
        <el-table-column prop="serviceName" label="服务名称" min-width="150" />
        <el-table-column prop="petName" label="宠物名称" width="100" />
        <el-table-column label="预约时间" width="180">
          <template #default="{ row }">
            {{ dayjs(row.appointmentTime).format('YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="总价" width="100">
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
        <el-table-column label="备注" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="primary"
              size="small"
              link
              @click="handleProcess(row)"
            >
              处理
            </el-button>
            <el-button
              v-if="row.status === 'confirmed'"
              type="success"
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
        <template #empty>
          <el-empty description="暂无预约数据" />
        </template>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="totalCount"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="预约详情"
      width="500px"
      @close="closeDialog"
    >
      <el-form v-if="currentAppointment" label-width="100px">
        <el-form-item label="预约编号">
          <span>{{ currentAppointment.id }}</span>
        </el-form-item>
        <el-form-item label="用户名称">
          <span>{{ currentAppointment.userName }}</span>
        </el-form-item>
        <el-form-item label="服务名称">
          <span>{{ currentAppointment.serviceName }}</span>
        </el-form-item>
        <el-form-item label="宠物名称">
          <span>{{ currentAppointment.petName }}</span>
        </el-form-item>
        <el-form-item label="预约时间">
          <span>{{ new Date(currentAppointment.appointmentTime).toLocaleString() }}</span>
        </el-form-item>
        <el-form-item label="总价">
          <span>¥{{ currentAppointment.totalPrice.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="getStatusType(currentAppointment.status)" size="small">
            {{ getStatusText(currentAppointment.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item v-if="currentAppointment.remark" label="备注">
          <span>{{ currentAppointment.remark }}</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <div v-if="currentAppointment && currentAppointment.status === 'pending'">
          <el-button type="success" :icon="Check" @click="handleAccept">
            接单
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

    <el-dialog
      v-model="rejectDialogVisible"
      title="拒单原因"
      width="400px"
      @close="closeRejectDialog"
    >
      <el-form>
        <el-form-item label="请填写拒单原因">
          <el-input
            v-model="rejectReason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒单原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeRejectDialog">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.merchant-appointments {
  padding: 0;
}

.appointments-card {
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

.header-actions {
  display: flex;
  gap: 8px;
}

.search-bar {
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
</style>
