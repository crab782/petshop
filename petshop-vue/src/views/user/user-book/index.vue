<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Calendar, Clock, View, Close } from '@element-plus/icons-vue'
import { getUserAppointments, cancelAppointment, type Appointment } from '@/api/user'

const router = useRouter()
const loading = ref(false)
const appointments = ref<Appointment[]>([])
const statusFilter = ref<string>('全部')
const searchKeyword = ref('')
const dateRange = ref<[Date, Date] | null>(null)
const cancelDialogVisible = ref(false)
const currentCancelId = ref<number | null>(null)
const detailDialogVisible = ref(false)
const currentAppointment = ref<Appointment | null>(null)

const statusOptions = [
  { label: '全部', value: '全部' },
  { label: '待处理', value: 'pending' },
  { label: '已确认', value: 'confirmed' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' }
]

const statusMap: Record<string, string> = {
  'pending': '待处理',
  'confirmed': '已确认',
  'completed': '已完成',
  'cancelled': '已取消'
}

const statusTagType = (status: string) => {
  const map: Record<string, string> = {
    'pending': 'warning',
    'confirmed': 'primary',
    'completed': 'success',
    'cancelled': 'info'
  }
  return map[status] || 'info'
}

const filteredAppointments = computed(() => {
  let filtered = [...appointments.value]

  if (statusFilter.value !== '全部') {
    filtered = filtered.filter(item => item.status === statusFilter.value)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(item =>
      item.id.toString().includes(keyword) ||
      item.serviceName?.toLowerCase().includes(keyword) ||
      item.merchantName?.toLowerCase().includes(keyword)
    )
  }

  if (dateRange.value && dateRange.value[0] && dateRange.value[1]) {
    const [start, end] = dateRange.value
    const endDate = new Date(end)
    endDate.setHours(23, 59, 59, 999)
    filtered = filtered.filter(item => {
      const appointmentDate = new Date(item.appointmentTime)
      return appointmentDate >= start && appointmentDate <= endDate
    })
  }

  return filtered.sort((a, b) => {
    return new Date(b.appointmentTime).getTime() - new Date(a.appointmentTime).getTime()
  })
})

const fetchAppointments = async () => {
  loading.value = true
  try {
    const res = await getUserAppointments()
    appointments.value = res.data || res || []
  } catch {
    ElMessage.error('获取预约列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
}

const handleDetail = (row: Appointment) => {
  currentAppointment.value = row
  detailDialogVisible.value = true
}

const handleCancel = (row: Appointment) => {
  currentCancelId.value = row.id
  cancelDialogVisible.value = true
}

const confirmCancel = async () => {
  if (!currentCancelId.value) return
  try {
    await cancelAppointment(currentCancelId.value)
    ElMessage.success('取消预约成功')
    cancelDialogVisible.value = false
    fetchAppointments()
  } catch {
    ElMessage.error('取消预约失败')
  }
}

const canCancel = (status: string) => {
  return status === 'pending' || status === 'confirmed'
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchAppointments()
})
</script>

<template>
  <div class="user-book">
    <div class="page-header">
      <h1 class="page-title">我的预约记录</h1>
      <p class="page-desc">查看和管理您的服务预约</p>
    </div>

    <div class="book-content">
      <el-card class="filter-card">
        <div class="filter-section">
          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索预约编号、服务名称或商家"
              clearable
              @keyup.enter="handleSearch"
              class="search-input"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="date-filter">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              class="date-picker"
              :shortcuts="[
                {
                  text: '最近一周',
                  value: () => {
                    const end = new Date()
                    const start = new Date()
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
                    return [start, end]
                  }
                },
                {
                  text: '最近一个月',
                  value: () => {
                    const end = new Date()
                    const start = new Date()
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
                    return [start, end]
                  }
                }
              ]"
            >
              <template #prefix>
                <el-icon><Calendar /></el-icon>
              </template>
            </el-date-picker>
          </div>
          <div class="status-filter">
            <span class="filter-label">状态：</span>
            <el-radio-group v-model="statusFilter" size="default" class="filter-buttons">
              <el-radio-button
                v-for="option in statusOptions"
                :key="option.value"
                :value="option.value"
              >
                {{ option.label }}
              </el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </el-card>

      <el-card class="appointments-card">
        <el-table
          v-loading="loading"
          :data="filteredAppointments"
          style="width: 100%"
          class="appointments-table"
        >
          <el-table-column prop="id" label="预约编号" width="100" align="center">
            <template #default="{ row }">
              <span class="appointment-id">#{{ row.id }}</span>
            </template>
          </el-table-column>
          <el-table-column label="服务名称" min-width="150">
            <template #default="{ row }">
              <div class="service-name">{{ row.serviceName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="商家名称" min-width="120">
            <template #default="{ row }">
              <div class="merchant-name">{{ row.merchantName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="预约时间" width="180" align="center">
            <template #default="{ row }">
              <div class="appointment-time">
                <el-icon><Clock /></el-icon>
                {{ formatDate(row.appointmentTime) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)" size="small" class="status-tag">
                {{ statusMap[row.status] || row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                text
                @click="handleDetail(row)"
              >
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <el-button
                v-if="canCancel(row.status)"
                type="danger"
                size="small"
                text
                @click="handleCancel(row)"
              >
                <el-icon><Close /></el-icon>
                取消
              </el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无预约记录" />
          </template>
        </el-table>
      </el-card>

      <div class="stats-section">
        <el-row :gutter="16">
          <el-col :xs="12" :sm="6">
            <el-card class="stat-card">
              <div class="stat-value">{{ Array.isArray(appointments.value) ? appointments.value.length : 0 }}</div>
              <div class="stat-label">总预约数</div>
            </el-card>
          </el-col>
          <el-col :xs="12" :sm="6">
            <el-card class="stat-card pending">
              <div class="stat-value">{{ Array.isArray(appointments.value) ? appointments.value.filter(a => a.status === 'pending').length : 0 }}</div>
              <div class="stat-label">待处理</div>
            </el-card>
          </el-col>
          <el-col :xs="12" :sm="6">
            <el-card class="stat-card confirmed">
              <div class="stat-value">{{ Array.isArray(appointments.value) ? appointments.value.filter(a => a.status === 'confirmed').length : 0 }}</div>
              <div class="stat-label">已确认</div>
            </el-card>
          </el-col>
          <el-col :xs="12" :sm="6">
            <el-card class="stat-card completed">
              <div class="stat-value">{{ Array.isArray(appointments.value) ? appointments.value.filter(a => a.status === 'completed').length : 0 }}</div>
              <div class="stat-label">已完成</div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <el-dialog
      v-model="cancelDialogVisible"
      title="确认取消预约"
      width="400px"
    >
      <div class="cancel-dialog-content">
        <el-icon class="warning-icon" color="#e6a23c" :size="48"><Close /></el-icon>
        <p>确定要取消该预约吗？</p>
        <p class="warning-text">此操作不可恢复</p>
      </div>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmCancel">确认取消</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailDialogVisible"
      title="预约详情"
      width="500px"
    >
      <div v-if="currentAppointment" class="detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="预约编号">
            #{{ currentAppointment.id }}
          </el-descriptions-item>
          <el-descriptions-item label="服务名称">
            {{ currentAppointment.serviceName }}
          </el-descriptions-item>
          <el-descriptions-item label="商家名称">
            {{ currentAppointment.merchantName }}
          </el-descriptions-item>
          <el-descriptions-item label="预约时间">
            {{ formatDate(currentAppointment.appointmentTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="预约状态">
            <el-tag :type="statusTagType(currentAppointment.status)">
              {{ statusMap[currentAppointment.status] || currentAppointment.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注" v-if="currentAppointment.remark">
            {{ currentAppointment.remark }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentAppointment && canCancel(currentAppointment.status)"
          type="danger"
          @click="detailDialogVisible = false; handleCancel(currentAppointment)"
        >
          取消预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-book {
  min-height: 100vh;
  padding: 20px;
  background-color: #f5f7fa;
}

.page-header {
  margin-bottom: 24px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-desc {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.book-content {
  max-width: 1200px;
  margin: 0 auto;
}

.filter-card {
  margin-bottom: 24px;
  border-radius: 8px;
}

.filter-section {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  padding: 8px 0;
}

.search-box {
  flex: 1;
  min-width: 200px;
}

.search-input {
  border-radius: 8px;
}

.date-filter {
  display: flex;
  align-items: center;
}

.date-picker {
  border-radius: 8px;
}

.status-filter {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.filter-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.appointments-card {
  border-radius: 8px;
  margin-bottom: 24px;
}

.appointments-table {
  border-radius: 8px;
}

.appointment-id {
  font-weight: 500;
  color: #409eff;
}

.service-name {
  font-weight: 500;
  color: #303133;
}

.merchant-name {
  color: #606266;
  font-size: 14px;
}

.appointment-time {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 14px;
  color: #606266;
}

.status-tag {
  border-radius: 12px;
  padding: 2px 12px;
}

.stats-section {
  margin-top: 24px;
}

.stat-card {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.stat-card.pending .stat-value {
  color: #e6a23c;
}

.stat-card.confirmed .stat-value {
  color: #409eff;
}

.stat-card.completed .stat-value {
  color: #67c23a;
}

.cancel-dialog-content {
  text-align: center;
  padding: 20px 0;
}

.warning-icon {
  margin-bottom: 16px;
}

.cancel-dialog-content p {
  margin: 8px 0;
  font-size: 16px;
  color: #303133;
}

.warning-text {
  color: #909399;
  font-size: 14px !important;
}

.detail-content {
  padding: 10px 0;
}

@media (max-width: 768px) {
  .user-book {
    padding: 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .filter-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .search-box {
    width: 100%;
  }

  .date-filter {
    width: 100%;
  }

  .date-picker {
    width: 100%;
  }

  .status-filter {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-buttons {
    width: 100%;
  }

  .stat-card {
    margin-bottom: 16px;
  }
}
</style>
