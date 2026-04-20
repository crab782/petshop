<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, View } from '@element-plus/icons-vue'
import {
  getActivities,
  addActivity,
  updateActivity,
  deleteActivity,
  toggleActivityStatus
} from '@/api/admin'
import type { Activity } from '@/api/admin'

const loading = ref(false)
const activityList = ref<Activity[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const searchForm = ref({
  name: '',
  type: '',
  status: '',
  dateRange: [] as string[]
})

const activityTypes = [
  { label: '全部', value: '' },
  { label: '促销活动', value: 'promotion' },
  { label: '抽奖活动', value: 'lottery' },
  { label: '满减活动', value: 'discount' },
  { label: '节日活动', value: 'festival' },
  { label: '新品上市', value: 'new_product' }
]

const statusOptions = [
  { label: '全部', value: '' },
  { label: '未开始', value: 'pending' },
  { label: '进行中', value: 'active' },
  { label: '已结束', value: 'ended' },
  { label: '已禁用', value: 'disabled' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('添加活动')
const isEdit = ref(false)
const currentId = ref<number | null>(null)
const formRef = ref()

const formData = ref({
  name: '',
  type: 'promotion',
  description: '',
  startTime: '',
  endTime: '',
  status: 'pending'
})

const rules = {
  name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const filteredList = computed(() => {
  let result = activityList.value

  if (searchForm.value.name) {
    result = result.filter(item =>
      item.name.toLowerCase().includes(searchForm.value.name.toLowerCase())
    )
  }

  if (searchForm.value.type) {
    result = result.filter(item => item.type === searchForm.value.type)
  }

  if (searchForm.value.status) {
    result = result.filter(item => item.status === searchForm.value.status)
  }

  if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
    const [startDate, endDate] = searchForm.value.dateRange
    result = result.filter(item => {
      const itemDate = new Date(item.startTime).getTime()
      return itemDate >= new Date(startDate).getTime() && itemDate <= new Date(endDate).getTime()
    })
  }

  return result
})

const paginatedList = computed(() => {
  const start = (pagination.value.current - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  return filteredList.value.slice(start, end)
})

const handleSearch = () => {
  pagination.value.current = 1
  pagination.value.total = filteredList.value.length
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    type: '',
    status: '',
    dateRange: []
  }
  handleSearch()
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
}

const handlePageSizeChange = (size: number) => {
  pagination.value.pageSize = size
  pagination.value.current = 1
}

const loadActivities = async () => {
  loading.value = true
  try {
    const data = await getActivities()
    activityList.value = data
    pagination.value.total = data.length
  } catch (error) {
    ElMessage.error('加载活动列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加活动'
  isEdit.value = false
  currentId.value = null
  formData.value = {
    name: '',
    type: 'promotion',
    description: '',
    startTime: '',
    endTime: '',
    status: 'pending'
  }
  dialogVisible.value = true
}

const handleEdit = (row: Activity) => {
  dialogTitle.value = '编辑活动'
  isEdit.value = true
  currentId.value = row.id
  formData.value = {
    name: row.name,
    type: row.type,
    description: row.description || '',
    startTime: row.startTime,
    endTime: row.endTime,
    status: row.status
  }
  dialogVisible.value = true
}

const handleDelete = async (row: Activity) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除活动 "${row.name}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteActivity(row.id)
    ElMessage.success('删除成功')
    loadActivities()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleToggleStatus = async (row: Activity) => {
  const action = row.status === 'active' ? '禁用' : '启用'
  const newStatus = row.status === 'active' ? 'disabled' : 'active'

  try {
    await ElMessageBox.confirm(
      `确定要${action}活动 "${row.name}" 吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await toggleActivityStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadActivities()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleSave = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    if (new Date(formData.value.endTime) <= new Date(formData.value.startTime)) {
      ElMessage.warning('结束时间必须晚于开始时间')
      return
    }

    try {
      if (isEdit.value && currentId.value !== null) {
        await updateActivity(currentId.value, formData.value)
        ElMessage.success('更新成功')
      } else {
        await addActivity(formData.value)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadActivities()
    } catch (error) {
      ElMessage.error('保存失败')
    }
  })
}

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: 'info',
    active: 'success',
    ended: 'warning',
    disabled: 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: '未开始',
    active: '进行中',
    ended: '已结束',
    disabled: '已禁用'
  }
  return textMap[status] || status
}

const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    promotion: '促销活动',
    lottery: '抽奖活动',
    discount: '满减活动',
    festival: '节日活动',
    new_product: '新品上市'
  }
  return typeMap[type] || type
}

onMounted(() => {
  loadActivities()
})
</script>

<template>
  <div class="activity-management">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>活动管理</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="活动名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入活动名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 150px">
            <el-option
              v-for="item in activityTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="活动状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 280px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <span>活动列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 添加活动
          </el-button>
        </div>
      </template>

      <el-table
        :data="paginatedList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="活动ID" width="100" />
        <el-table-column prop="name" label="活动名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="type" label="活动类型" width="120">
          <template #default="{ row }">
            {{ getTypeText(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ row.startTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">
            {{ row.endTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" :icon="View" @click="handleEdit(row)">
              详情
            </el-button>
            <el-button type="warning" size="small" :icon="Edit" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              v-if="row.status === 'active'"
              type="danger"
              size="small"
              @click="handleToggleStatus(row)"
            >
              禁用
            </el-button>
            <el-button
              v-else-if="row.status !== 'ended'"
              type="success"
              size="small"
              @click="handleToggleStatus(row)"
            >
              启用
            </el-button>
            <el-button type="danger" size="small" :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="活动名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入活动名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择活动类型" style="width: 100%">
            <el-option
              v-for="item in activityTypes.filter(t => t.value !== '')"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            placeholder="请输入活动描述"
            :rows="4"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="请选择开始时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="请选择结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.activity-management {
  padding: 0;
}

.search-card {
  margin-top: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>