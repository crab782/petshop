<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElButton, ElTable, ElTableColumn, ElImage, ElDialog, ElForm, ElFormItem, ElInput, ElMessage, ElMessageBox, ElSwitch, ElPagination, ElSelect, ElOption, ElInputNumber, ElTag } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh } from '@element-plus/icons-vue'
import { getMerchantServices, deleteService, batchUpdateServiceStatus, type MerchantService } from '@/api/merchant'
import { useAsync } from '@/composables/useAsync'
import { usePagination } from '@/composables/usePagination'
import { useSearch } from '@/composables/useSearch'

const router = useRouter()

// 服务列表数据
const services = ref<MerchantService[]>([])
const loading = ref(false)
const selectedRows = ref<MerchantService[]>([])

// 编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('编辑服务')
const formData = ref({
  id: 0,
  name: '',
  description: '',
  price: 0,
  duration: 0,
  image: '',
  status: 'enabled' as 'enabled' | 'disabled'
})
const formRef = ref()

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入时长', trigger: 'blur' }]
}

// 搜索和筛选参数
const { keyword, debouncedKeyword, filters, setKeyword, setFilter, clearFilters } = useSearch(300)

// 价格筛选
const minPrice = ref<number | undefined>(undefined)
const maxPrice = ref<number | undefined>(undefined)

// 分页
const { page, pageSize, total, setTotal, setPage, setPageSize } = usePagination(1, 10)

// 筛选后的服务列表
const filteredServices = computed(() => {
  let result = [...services.value]

  // 关键字搜索
  if (debouncedKeyword.value) {
    const searchLower = debouncedKeyword.value.toLowerCase()
    result = result.filter(s => 
      s.name.toLowerCase().includes(searchLower) ||
      (s.description && s.description.toLowerCase().includes(searchLower))
    )
  }

  // 价格区间筛选
  if (minPrice.value !== undefined) {
    result = result.filter(s => s.price >= minPrice.value)
  }
  if (maxPrice.value !== undefined) {
    result = result.filter(s => s.price <= maxPrice.value)
  }

  // 状态筛选
  if (filters.value.status && filters.value.status !== 'all') {
    result = result.filter(s => s.status === filters.value.status)
  }

  return result
})

// 分页后的服务列表
const paginatedServices = computed(() => {
  const start = (page.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredServices.value.slice(start, end)
})

// 监听筛选结果变化，更新总数
watch(filteredServices, (newVal) => {
  setTotal(newVal.length)
})

// 获取服务列表
const fetchServices = async () => {
  loading.value = true
  try {
    const res = await getMerchantServices()
    services.value = res.data || []
    setTotal(services.value.length)
  } catch (error) {
    ElMessage.error('获取服务列表失败')
    console.error('获取服务列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到添加服务页面
const handleAdd = () => {
  router.push('/merchant/service-edit')
}

// 跳转到编辑服务页面
const handleEdit = (row: MerchantService) => {
  router.push(`/merchant/service-edit?id=${row.id}`)
}

// 删除服务
const handleDelete = async (row: MerchantService) => {
  try {
    await ElMessageBox.confirm(`确定要删除服务"${row.name}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteService(row.id)
    ElMessage.success('删除成功')
    fetchServices()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }
}

// 切换服务状态
const handleStatusChange = async (row: MerchantService) => {
  try {
    await batchUpdateServiceStatus([row.id], row.status as 'enabled' | 'disabled')
    ElMessage.success(`${row.status === 'enabled' ? '启用' : '禁用'}成功`)
  } catch (error) {
    ElMessage.error('状态更新失败')
    // 恢复原状态
    row.status = row.status === 'enabled' ? 'disabled' : 'enabled'
    console.error('状态更新失败:', error)
  }
}

// 批量操作
const handleSelectionChange = (rows: MerchantService[]) => {
  selectedRows.value = rows
}

// 批量启用
const handleBatchEnable = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择服务')
    return
  }
  try {
    const ids = selectedRows.value.map(row => row.id)
    await batchUpdateServiceStatus(ids, 'enabled')
    ElMessage.success('批量启用成功')
    fetchServices()
  } catch (error) {
    ElMessage.error('批量启用失败')
    console.error('批量启用失败:', error)
  }
}

// 批量禁用
const handleBatchDisable = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择服务')
    return
  }
  try {
    const ids = selectedRows.value.map(row => row.id)
    await batchUpdateServiceStatus(ids, 'disabled')
    ElMessage.success('批量禁用成功')
    fetchServices()
  } catch (error) {
    ElMessage.error('批量禁用失败')
    console.error('批量禁用失败:', error)
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择服务')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个服务吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 逐个删除
    for (const row of selectedRows.value) {
      await deleteService(row.id)
    }
    ElMessage.success('批量删除成功')
    fetchServices()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
      console.error('批量删除失败:', error)
    }
  }
}

// 搜索
const handleSearch = () => {
  setPage(1)
}

// 重置筛选
const handleReset = () => {
  setKeyword('')
  minPrice.value = undefined
  maxPrice.value = undefined
  clearFilters()
  setPage(1)
}

// 分页变化
const handlePageChange = (newPage: number) => {
  setPage(newPage)
}

const handleSizeChange = (newSize: number) => {
  setPageSize(newSize)
}

// 格式化价格
const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}

// 格式化时长
const formatDuration = (duration: number | undefined) => {
  if (!duration) return '-'
  if (duration >= 60) {
    const hours = Math.floor(duration / 60)
    const minutes = duration % 60
    return minutes > 0 ? `${hours}小时${minutes}分钟` : `${hours}小时`
  }
  return `${duration}分钟`
}

// 格式化日期
const formatDate = (date: string | undefined) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 初始化
onMounted(() => {
  fetchServices()
})
</script>

<template>
  <div class="merchant-services">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">服务管理</h2>
      <div class="header-actions">
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加服务</el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-form">
      <el-form :inline="true">
        <el-form-item label="服务名称">
          <el-input 
            v-model="keyword" 
            placeholder="请输入服务名称或描述" 
            clearable 
            style="width: 200px" 
          />
        </el-form-item>
        <el-form-item label="价格区间">
          <el-input-number 
            v-model="minPrice" 
            placeholder="最低价" 
            :min="0" 
            :precision="2" 
            style="width: 120px" 
          />
          <span style="margin: 0 8px">-</span>
          <el-input-number 
            v-model="maxPrice" 
            placeholder="最高价" 
            :min="0" 
            :precision="2" 
            style="width: 120px" 
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            :model-value="filters.status || 'all'" 
            style="width: 120px"
            @change="(val: string) => setFilter('status', val)"
          >
            <el-option label="全部" value="all" />
            <el-option label="启用" value="enabled" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions">
      <el-button :disabled="selectedRows.length === 0" @click="handleBatchEnable">批量启用</el-button>
      <el-button :disabled="selectedRows.length === 0" @click="handleBatchDisable">批量禁用</el-button>
      <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">批量删除</el-button>
      <span v-if="selectedRows.length > 0" class="selected-count">已选择 {{ selectedRows.length }} 项</span>
    </div>

    <!-- 服务列表表格 -->
    <el-table 
      :data="paginatedServices" 
      v-loading="loading" 
      style="width: 100%" 
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="服务图片" width="120">
        <template #default="{ row }">
          <el-image
            v-if="row.image"
            :src="row.image"
            fit="cover"
            style="width: 80px; height: 80px; border-radius: 8px;"
          />
          <div v-else class="image-placeholder">暂无图片</div>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="服务名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column label="价格" width="120">
        <template #default="{ row }">
          <span class="price-text">{{ formatPrice(row.price) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="时长" width="120">
        <template #default="{ row }">
          {{ formatDuration(row.duration) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            active-value="enabled"
            inactive-value="disabled"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="120">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
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
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-form {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.batch-actions {
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
  align-items: center;
}

.selected-count {
  margin-left: 16px;
  color: #909399;
  font-size: 14px;
}

.image-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 12px;
}

.price-text {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
