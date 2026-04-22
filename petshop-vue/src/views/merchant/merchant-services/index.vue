<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  ElTable,
  ElTableColumn,
  ElButton,
  ElSwitch,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElOption,
  ElMessage,
  ElMessageBox,
  ElPagination,
  ElCard,
  ElImage,
  ElTag,
  ElEmpty
} from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh, Check, Close } from '@element-plus/icons-vue'
import {
  getMerchantServices,
  addService,
  updateService,
  deleteService,
  batchUpdateServiceStatus,
  batchDeleteServices,
  type MerchantService
} from '@/api/merchant'
import { usePagination } from '@/composables/usePagination'

const serviceList = ref<MerchantService[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加服务')
const selectedServices = ref<MerchantService[]>([])
const serviceForm = ref({
  id: 0,
  name: '',
  description: '',
  price: 0,
  duration: 0,
  category: '',
  image: ''
})

const formRules = {
  name: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入服务价格', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入服务时长', trigger: 'blur' }]
}

const serviceTypes = ['美容', '健康', '寄养', '饮食', '接送', '训练']
const formRef = ref()

const searchForm = ref({
  keyword: '',
  minPrice: '',
  maxPrice: '',
  status: ''
})

const { page, pageSize, total, setTotal, setPage, setPageSize, reset: resetPagination } = usePagination(1, 10)

const fetchServices = async () => {
  loading.value = true
  try {
    const res = await getMerchantServices()
    let data = res || []

    if (searchForm.value.keyword) {
      data = data.filter(s => s.name.toLowerCase().includes(searchForm.value.keyword.toLowerCase()))
    }
    if (searchForm.value.minPrice) {
      data = data.filter(s => s.price >= Number(searchForm.value.minPrice))
    }
    if (searchForm.value.maxPrice) {
      data = data.filter(s => s.price <= Number(searchForm.value.maxPrice))
    }
    if (searchForm.value.status) {
      data = data.filter(s => s.status === searchForm.value.status)
    }

    serviceList.value = data
    setTotal(data.length)
  } catch (error) {
    console.error('获取服务列表失败:', error)
    ElMessage.error('获取服务列表失败，请稍后重试')
    serviceList.value = []
  } finally {
    loading.value = false
  }
}

const paginatedServices = () => {
  const start = (page.value - 1) * pageSize.value
  const end = start + pageSize.value
  return serviceList.value.slice(start, end)
}

const handleSearch = () => {
  setPage(1)
  fetchServices()
}

const handleReset = () => {
  searchForm.value = {
    keyword: '',
    minPrice: '',
    maxPrice: '',
    status: ''
  }
  resetPagination()
  fetchServices()
}

const handleSelectionChange = (selection: MerchantService[]) => {
  selectedServices.value = selection
}

const handlePageChange = (newPage: number) => {
  setPage(newPage)
}

const handleSizeChange = (newSize: number) => {
  setPageSize(newSize)
}

const handleAdd = () => {
  dialogTitle.value = '添加服务'
  serviceForm.value = { id: 0, name: '', description: '', price: 0, duration: 0, category: '', image: '' }
  dialogVisible.value = true
}

const handleEdit = (row: MerchantService) => {
  dialogTitle.value = '编辑服务'
  serviceForm.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: MerchantService) => {
  try {
    await ElMessageBox.confirm(`确定要删除服务"${row.name}"吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteService(row.id)
    ElMessage.success('删除成功')
    fetchServices()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

const handleStatusChange = async (row: MerchantService, status: boolean) => {
  try {
    const newStatus = status ? 'enabled' : 'disabled'
    await updateService(row.id, { status: newStatus } as any)
    ElMessage.success(newStatus === 'enabled' ? '已启用' : '已禁用')
    fetchServices()
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败，请稍后重试')
    fetchServices()
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const data = {
          name: serviceForm.value.name,
          description: serviceForm.value.description,
          price: serviceForm.value.price,
          duration: serviceForm.value.duration,
          category: serviceForm.value.category,
          image: serviceForm.value.image
        }
        if (serviceForm.value.id === 0) {
          await addService(data)
          ElMessage.success('添加成功')
        } else {
          await updateService(serviceForm.value.id, data)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchServices()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(dialogTitle.value === '添加服务' ? '添加失败，请稍后重试' : '更新失败，请稍后重试')
      }
    }
  })
}

const handleBatchEnable = async () => {
  if (selectedServices.value.length === 0) {
    ElMessage.warning('请选择要启用的服务')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要启用选中的 ${selectedServices.value.length} 项服务吗？`, '批量启用', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const ids = selectedServices.value.map(s => s.id)
    await batchUpdateServiceStatus(ids, 'enabled')
    ElMessage.success('批量启用成功')
    fetchServices()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量启用失败:', error)
      ElMessage.error('批量启用失败，请稍后重试')
    }
  }
}

const handleBatchDisable = async () => {
  if (selectedServices.value.length === 0) {
    ElMessage.warning('请选择要禁用的服务')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要禁用选中的 ${selectedServices.value.length} 项服务吗？`, '批量禁用', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const ids = selectedServices.value.map(s => s.id)
    await batchUpdateServiceStatus(ids, 'disabled')
    ElMessage.success('批量禁用成功')
    fetchServices()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量禁用失败:', error)
      ElMessage.error('批量禁用失败，请稍后重试')
    }
  }
}

const handleBatchDelete = async () => {
  if (selectedServices.value.length === 0) {
    ElMessage.warning('请选择要删除的服务')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedServices.value.length} 项服务吗？此操作不可恢复！`, '批量删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const ids = selectedServices.value.map(s => s.id)
    await batchDeleteServices(ids)
    ElMessage.success('批量删除成功')
    fetchServices()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败，请稍后重试')
    }
  }
}

const formatDuration = (minutes: number | undefined) => {
  if (!minutes) return '0分钟'
  if (minutes >= 60) {
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
  }
  return `${minutes}分钟`
}

const formatDate = (date: string | undefined) => {
  if (!date) return '-'
  return date.split('T')[0]
}

onMounted(() => {
  fetchServices()
})
</script>

<template>
  <div class="merchant-services">
    <div class="page-header">
      <h2 class="page-title">服务管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加服务
      </el-button>
    </div>

    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="服务名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入服务名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="价格区间">
          <el-input v-model="searchForm.minPrice" placeholder="最低价" type="number" clearable style="width: 100px" />
          <span style="margin: 0 8px">-</span>
          <el-input v-model="searchForm.maxPrice" placeholder="最高价" type="number" clearable style="width: 100px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="启用" value="enabled" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="batch-actions" v-if="selectedServices.length > 0">
      <span class="selected-info">已选择 {{ selectedServices.length }} 项</span>
      <el-button type="success" size="small" @click="handleBatchEnable">
        <el-icon><Check /></el-icon>
        批量启用
      </el-button>
      <el-button type="warning" size="small" @click="handleBatchDisable">
        <el-icon><Close /></el-icon>
        批量禁用
      </el-button>
      <el-button type="danger" size="small" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </el-card>

    <el-table :data="paginatedServices()" v-loading="loading" style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column label="服务图片" width="120">
        <template #default="{ row }">
          <el-image :src="row.image || 'https://placeholder.com/100'" fit="cover" style="width: 80px; height: 80px; border-radius: 8px;" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="服务名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="price" label="价格（元）" width="100">
        <template #default="{ row }">
          ¥{{ row.price?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="时长" width="120">
        <template #default="{ row }">
          {{ formatDuration(row.duration) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'enabled' ? 'success' : 'info'" size="small">
            {{ row.status === 'enabled' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="120">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button type="danger" link @click="handleDelete(row)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无服务数据" />
      </template>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="serviceForm" :rules="formRules" label-width="100px">
        <el-form-item label="服务名称" prop="name">
          <el-input v-model="serviceForm.name" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="服务价格" prop="price">
          <el-input v-model.number="serviceForm.price" type="number" placeholder="请输入服务价格" />
        </el-form-item>
        <el-form-item label="服务时长" prop="duration">
          <el-input v-model.number="serviceForm.duration" type="number" placeholder="请输入服务时长（分钟）" />
        </el-form-item>
        <el-form-item label="服务类型" prop="category">
          <el-select v-model="serviceForm.category" placeholder="请选择服务类型">
            <el-option v-for="type in serviceTypes" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="serviceForm.description" type="textarea" :rows="3" placeholder="请输入服务描述" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="serviceForm.image" placeholder="请输入图片URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.merchant-services {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.search-card {
  margin-bottom: 16px;
}

.batch-actions {
  margin-bottom: 16px;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.selected-info {
  font-size: 14px;
  color: #606266;
  margin-right: 8px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
