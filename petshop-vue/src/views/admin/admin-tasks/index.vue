<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTasks, addTask, updateTask, deleteTask, executeTask, type Task } from '@/api/admin'
import { Plus, Edit, Delete, VideoPlay, Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const taskList = ref<Task[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加任务')
const isEdit = ref(false)
const currentTaskId = ref<number | null>(null)

const filterName = ref('')
const filterType = ref('')
const filterStatus = ref('')
const dateRange = ref<[Date, Date] | null>(null)

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const formData = ref({
  name: '',
  type: '',
  description: '',
  cronExpression: '',
  executeTime: ''
})

const formRef = ref()

const taskTypes = [
  { value: 'scheduled', label: '定时任务' },
  { value: 'one-time', label: '一次性任务' },
  { value: 'recurring', label: '循环任务' }
]

const taskStatuses = [
  { value: '', label: '全部' },
  { value: 'pending', label: '待执行' },
  { value: 'running', label: '执行中' },
  { value: 'completed', label: '已完成' },
  { value: 'failed', label: '失败' }
]

const rules = {
  name: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择任务类型', trigger: 'change' }]
}

const getStatusLabel = (status: string) => {
  const found = taskStatuses.find(item => item.value === status)
  return found ? found.label : status
}

const getStatusClass = (status: string) => {
  switch (status) {
    case 'pending':
      return 'status-pending'
    case 'running':
      return 'status-running'
    case 'completed':
      return 'status-completed'
    case 'failed':
      return 'status-failed'
    default:
      return 'status-default'
  }
}

const getTypeLabel = (type: string) => {
  const found = taskTypes.find(item => item.value === type)
  return found ? found.label : type
}

const loadTasks = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.value.current,
      pageSize: pagination.value.pageSize
    }
    if (filterName.value) {
      params.name = filterName.value
    }
    if (filterType.value) {
      params.type = filterType.value
    }
    if (filterStatus.value) {
      params.status = filterStatus.value
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0].toISOString()
      params.endDate = dateRange.value[1].toISOString()
    }

    const data = await getTasks(params)
    taskList.value = data || []
    pagination.value.total = taskList.value.length
  } catch (error) {
    ElMessage.error('加载任务列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  loadTasks()
}

const handleRefresh = () => {
  filterName.value = ''
  filterType.value = ''
  filterStatus.value = ''
  dateRange.value = null
  pagination.value.current = 1
  loadTasks()
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  loadTasks()
}

const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size
  loadTasks()
}

const handleAdd = () => {
  dialogTitle.value = '添加任务'
  isEdit.value = false
  currentTaskId.value = null
  formData.value = {
    name: '',
    type: '',
    description: '',
    cronExpression: '',
    executeTime: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: Task) => {
  dialogTitle.value = '编辑任务'
  isEdit.value = true
  currentTaskId.value = row.id
  formData.value = {
    name: row.name,
    type: row.type,
    description: row.description || '',
    cronExpression: row.cronExpression || '',
    executeTime: row.executeTime || ''
  }
  dialogVisible.value = true
}

const handleDelete = async (row: Task) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除任务 "${row.name}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deleteTask(row.id)
    ElMessage.success('删除成功')
    loadTasks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleExecute = async (row: Task) => {
  try {
    await ElMessageBox.confirm(
      `确定要立即执行任务 "${row.name}" 吗？`,
      '执行确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await executeTask(row.id)
    ElMessage.success('任务已触发执行')
    loadTasks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('执行失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    try {
      if (isEdit.value && currentTaskId.value) {
        await updateTask(currentTaskId.value, formData.value)
        ElMessage.success('更新成功')
      } else {
        await addTask(formData.value)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadTasks()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    }
  })
}

const formatDate = (date: string) => {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleString('zh-CN')
}

onMounted(() => {
  loadTasks()
})
</script>

<template>
  <div class="task-management">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>任务管理</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="filter-card">
      <div class="filter-bar">
        <div class="filter-item">
          <span class="filter-label">任务名称：</span>
          <el-input
            v-model="filterName"
            placeholder="请输入任务名称"
            clearable
            style="width: 180px;"
          />
        </div>

        <div class="filter-item">
          <span class="filter-label">任务类型：</span>
          <el-select v-model="filterType" placeholder="请选择" clearable style="width: 150px;">
            <el-option
              v-for="item in taskTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>

        <div class="filter-item">
          <span class="filter-label">任务状态：</span>
          <el-select v-model="filterStatus" placeholder="请选择" clearable style="width: 150px;">
            <el-option
              v-for="item in taskStatuses"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>

        <div class="filter-item">
          <span class="filter-label">时间范围：</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px;"
          />
        </div>

        <div class="filter-actions">
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleRefresh">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="hover" class="table-card">
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          添加任务
        </el-button>
      </div>

      <el-table
        :data="taskList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="任务ID" width="100" />
        <el-table-column prop="name" label="任务名称" min-width="150" />
        <el-table-column prop="type" label="任务类型" width="120">
          <template #default="{ row }">
            {{ getTypeLabel(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="executeTime" label="执行时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.executeTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span :class="['status-tag', getStatusClass(row.status)]">
              {{ getStatusLabel(row.status) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="result" label="执行结果" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.result || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :icon="VideoPlay"
              @click="handleExecute(row)"
              :disabled="row.status === 'running'"
            >
              执行
            </el-button>
            <el-button
              type="primary"
              size="small"
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="任务名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入任务名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="任务类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择任务类型" style="width: 100%;">
            <el-option
              v-for="item in taskTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            placeholder="请输入任务描述"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="Cron表达式" v-if="formData.type === 'scheduled'">
          <el-input
            v-model="formData.cronExpression"
            placeholder="请输入Cron表达式，如 0 0 * * *"
            clearable
          />
        </el-form-item>
        <el-form-item label="执行时间">
          <el-date-picker
            v-model="formData.executeTime"
            type="datetime"
            placeholder="请选择执行时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.task-management {
  padding: 0;
}

.filter-card {
  margin-top: 20px;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.filter-actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.table-card {
  margin-top: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.status-running {
  background-color: #ecf5ff;
  color: #409eff;
}

.status-completed {
  background-color: #e1f3d8;
  color: #67c23a;
}

.status-failed {
  background-color: #fef0f0;
  color: #f56c6c;
}

.status-default {
  background-color: #f4f4f5;
  color: #909399;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-actions {
    margin-left: 0;
    width: 100%;
    justify-content: flex-end;
  }
}
</style>