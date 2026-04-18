<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllServices, deleteService, updateServiceStatus, batchUpdateServiceStatus, batchDeleteServices, getAllMerchants, Merchant, type AdminService } from '@/api/admin'
import { Search, Refresh, Check, Close, Edit, Delete, Plus } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const serviceList = ref<AdminService[]>([])
const merchantList = ref<Merchant[]>([])
const selectedServices = ref<AdminService[]>([])

const searchForm = ref({
  keyword: '',
  merchantId: null as number | null,
  minPrice: '',
  maxPrice: '',
  status: ''
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const filteredList = ref<AdminService[]>([])

const loadMerchants = async () => {
  try {
    const data = await getAllMerchants()
    merchantList.value = data || []
  } catch (error) {
    console.error('加载商家列表失败:', error)
  }
}

const loadServices = async () => {
  loading.value = true
  try {
    const data = await getAllServices()
    serviceList.value = data || []
    filterData()
  } catch (error) {
    ElMessage.error('加载服务列表失败')
  } finally {
    loading.value = false
  }
}

const filterData = () => {
  let result = [...serviceList.value]

  if (searchForm.value.keyword) {
    result = result.filter(item =>
      item.name.toLowerCase().includes(searchForm.value.keyword.toLowerCase())
    )
  }

  if (searchForm.value.merchantId) {
    result = result.filter(item => item.merchantId === searchForm.value.merchantId)
  }

  if (searchForm.value.minPrice) {
    result = result.filter(item => item.price >= Number(searchForm.value.minPrice))
  }

  if (searchForm.value.maxPrice) {
    result = result.filter(item => item.price <= Number(searchForm.value.maxPrice))
  }

  if (searchForm.value.status) {
    result = result.filter(item => item.status === searchForm.value.status)
  }

  pagination.value.total = result.length
  const start = (pagination.value.current - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  filteredList.value = result.slice(start, end)
}

const handleSearch = () => {
  pagination.value.current = 1
  filterData()
}

const handleReset = () => {
  searchForm.value = {
    keyword: '',
    merchantId: null,
    minPrice: '',
    maxPrice: '',
    status: ''
  }
  pagination.value.current = 1
  filterData()
}

const handleSelectionChange = (selection: AdminService[]) => {
  selectedServices.value = selection
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  filterData()
}

const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size
  pagination.value.current = 1
  filterData()
}

const handleStatusChange = async (row: AdminService, newStatus: boolean) => {
  const status = newStatus ? 'enabled' : 'disabled'
  try {
    await updateServiceStatus(row.id, status)
    row.status = status as 'enabled' | 'disabled'
    ElMessage.success(`服务已${status === 'enabled' ? '启用' : '禁用'}`)
  } catch (error) {
    ElMessage.error('状态更新失败')
    filterData()
  }
}

const handleEdit = (row: AdminService) => {
  router.push({ path: '/admin/service-edit', query: { id: row.id } })
}

const handleDelete = (row: AdminService) => {
  ElMessageBox.confirm(
    `确定要删除服务"${row.name}"吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteService(row.id)
      ElMessage.success('删除成功')
      loadServices()
    } catch (error) {
      ElMessage.error('删除失败，请重试')
    }
  }).catch(() => {})
}

const handleBatchEnable = async () => {
  if (selectedServices.value.length === 0) {
    ElMessage.warning('请选择要启用的服务')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要启用选中的 ${selectedServices.value.length} 项服务吗？`,
      '批量启用',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const ids = selectedServices.value.map(s => s.id)
    await batchUpdateServiceStatus(ids, 'enabled')
    ElMessage.success('批量启用成功')
    loadServices()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量启用失败')
    }
  }
}

const handleBatchDisable = async () => {
  if (selectedServices.value.length === 0) {
    ElMessage.warning('请选择要禁用的服务')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要禁用选中的 ${selectedServices.value.length} 项服务吗？`,
      '批量禁用',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const ids = selectedServices.value.map(s => s.id)
    await batchUpdateServiceStatus(ids, 'disabled')
    ElMessage.success('批量禁用成功')
    loadServices()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量禁用失败')
    }
  }
}

const handleBatchDelete = async () => {
  if (selectedServices.value.length === 0) {
    ElMessage.warning('请选择要删除的服务')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedServices.value.length} 项服务吗？此操作不可恢复！`,
      '批量删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const ids = selectedServices.value.map(s => s.id)
    await batchDeleteServices(ids)
    ElMessage.success('批量删除成功')
    loadServices()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}

const formatDuration = (minutes: number | undefined) => {
  if (!minutes) return '-'
  if (minutes >= 60) {
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
  }
  return `${minutes}分钟`
}

onMounted(() => {
  loadServices()
  loadMerchants()
})
</script>

<template>
  <div class="services-container">
    <div class="page-header">
      <h2 class="page-title">服务管理</h2>
    </div>

    <el-card shadow="hover" class="filter-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="服务名称">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入服务名称"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="商家名称">
          <el-select
            v-model="searchForm.merchantId"
            placeholder="请选择商家"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="merchant in merchantList"
              :key="merchant.id"
              :label="merchant.name"
              :value="merchant.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格区间">
          <el-input
            v-model="searchForm.minPrice"
            placeholder="最低价"
            type="number"
            clearable
            style="width: 100px"
          />
          <span style="margin: 0 8px">-</span>
          <el-input
            v-model="searchForm.maxPrice"
            placeholder="最高价"
            type="number"
            clearable
            style="width: 100px"
          />
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

    <el-card v-if="selectedServices.length > 0" shadow="hover" class="batch-actions">
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

    <el-card shadow="hover" class="table-card">
      <el-table
        :data="filteredList"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="服务ID" width="100" />
        <el-table-column prop="name" label="服务名称" min-width="150" />
        <el-table-column prop="merchantName" label="商家名称" min-width="150" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">
            <span class="price">{{ formatPrice(row.price) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="120">
          <template #default="{ row }">
            {{ formatDuration(row.duration) }}
          </template>
        </el-table-column>
        <el-table-column prop="category" label="服务类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.category" size="small">{{ row.category }}</el-tag>
            <span v-else class="no-data">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 'enabled'"
              active-text="启用"
              inactive-text="禁用"
              inline-prompt
              @change="handleStatusChange(row, $event)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="120">
          <template #default="{ row }">
            {{ row.createdAt ? row.createdAt.split('T')[0] : '-' }}
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
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.services-container {
  padding: 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.filter-card {
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

.table-card {
  background: #fff;
}

.price {
  color: #f56c6c;
  font-weight: 500;
}

.no-data {
  color: #909399;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
