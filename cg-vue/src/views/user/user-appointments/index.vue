<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Calendar } from '@element-plus/icons-vue'
import { getUserAppointments, cancelAppointment, type Appointment } from '@/api/user'



const router = useRouter()
const loading = ref(false)
const appointments = ref<Appointment[]>([])
const statusFilter = ref<string>('全部')
const searchKeyword = ref('')
const dateRange = ref<[Date, Date] | null>(null)
const cancelDialogVisible = ref(false)
const currentCancelId = ref<number | null>(null)

const statusOptions = [
  { label: '全部', value: '全部' },
  { label: '待确认', value: '待确认' },
  { label: '已确认', value: '已确认' },
  { label: '已完成', value: '已完成' },
  { label: '已取消', value: '已取消' }
]

const statusTagType = (status: string) => {
  const map: Record<string, string> = {
    '待确认': 'warning',
    '已确认': 'primary',
    '已完成': 'success',
    '已取消': 'info'
  }
  return map[status] || 'info'
}

const filteredAppointments = computed(() => {
  let filtered = [...appointments.value]

  // 状态筛选
  if (statusFilter.value !== '全部') {
    filtered = filtered.filter(item => item.status === statusFilter.value)
  }

  // 搜索筛选
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(item =>
      item.id.toString().includes(keyword) ||
      item.serviceName.toLowerCase().includes(keyword) ||
      item.merchantName.toLowerCase().includes(keyword)
    )
  }

  // 日期筛选
  if (dateRange.value) {
    const [start, end] = dateRange.value
    filtered = filtered.filter(item => {
      const appointmentDate = new Date(item.appointmentTime)
      return appointmentDate >= start && appointmentDate <= end
    })
  }

  return filtered
})

const fetchAppointments = async () => {
  loading.value = true
  try {
    // 使用真实API
    const res = await getUserAppointments()
    appointments.value = res.data || res || []
  } catch {
    ElMessage.error('获取预约列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  // 搜索逻辑已在computed中处理
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

const handleDetail = (row: Appointment) => {
  // 跳转到预约详情页
  router.push(`/user/appointments/detail/${row.id}`)
}

const canCancel = (status: string) => {
  return status === '待确认'
}

onMounted(() => {
  fetchAppointments()
})
</script>

<template>
  <div class="user-appointments">
    <div class="page-header">
      <h1 class="page-title">我的预约</h1>
    </div>

    <div class="appointments-content">
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
              <template #append>
                <el-button :icon="Search" @click="handleSearch" />
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
                class="filter-button"
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
          <el-table-column prop="id" label="预约编号" width="120" align="center" />
          <el-table-column label="服务名称" min-width="180">
            <template #default="{ row }">
              <div class="service-name">{{ row.serviceName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="商家名称" min-width="150">
            <template #default="{ row }">
              <div class="merchant-name">{{ row.merchantName }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="appointmentTime" label="预约时间" width="200" align="center" />
          <el-table-column label="状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)" size="small" class="status-tag">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                round
                @click="handleDetail(row)"
                class="detail-button"
              >
                查看详情
              </el-button>
              <el-button
                v-if="canCancel(row.status)"
                type="danger"
                size="small"
                round
                @click="handleCancel(row)"
                class="cancel-button"
              >
                取消
              </el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无预约记录" />
          </template>
        </el-table>
      </el-card>
    </div>

    <el-dialog
      v-model="cancelDialogVisible"
      title="确认取消预约"
      width="400px"
    >
      <span>确定要取消该预约吗？此操作不可恢复。</span>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmCancel">确认取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-appointments {
  min-height: 100vh;
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #333333;
  margin: 0;
}

.appointments-content {
  max-width: 1200px;
  margin: 0 auto;
}

.filter-card {
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-section {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  padding: 16px 0;
}

.search-box {
  flex: 1;
  min-width: 300px;
}

.search-input {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.date-filter {
  display: flex;
  align-items: center;
}

.date-picker {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.status-filter {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  color: #666666;
  font-weight: medium;
}

.filter-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-button {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.detail-button {
  border-radius: 9999px;
  transition: all 0.3s ease;
  margin-right: 8px;
}

.detail-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(76, 175, 80, 0.3);
}

.appointments-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.appointments-table {
  border-radius: 8px;
  overflow: hidden;
}

.service-name {
  font-weight: medium;
  color: #333333;
}

.merchant-name {
  color: #666666;
  font-size: 14px;
}

.status-tag {
  border-radius: 12px;
  padding: 2px 12px;
}

.cancel-button {
  border-radius: 9999px;
  transition: all 0.3s ease;
}

.cancel-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(245, 108, 108, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-appointments {
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

  .filter-buttons {
    width: 100%;
  }

  .filter-button {
    flex: 1;
    min-width: 80px;
  }

  .el-table {
    font-size: 14px;
  }

  .el-table-column {
    min-width: 100px;
  }
}
</style>
