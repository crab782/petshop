<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElButton, ElTable, ElTableColumn, ElImage, ElForm, ElFormItem, ElInput, ElMessage, ElMessageBox, ElSwitch, ElPagination, ElSelect, ElOption, ElInputNumber, ElTag, ElEmpty } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh } from '@element-plus/icons-vue'
import { getMerchantProductsPaged, deleteProduct, updateProductStatus, batchUpdateProductStatus, batchDeleteProducts, type Product, type ProductQuery } from '@/api/merchant'
import { useAsync } from '@/composables/useAsync'
import { usePagination } from '@/composables/usePagination'
import { useSearch } from '@/composables/useSearch'

const router = useRouter()

const STORAGE_WARNING_THRESHOLD = 10

const products = ref<Product[]>([])
const selectedRows = ref<Product[]>([])

const { loading, error, execute } = useAsync(getMerchantProductsPaged)
const pagination = usePagination(1, 10)
const search = useSearch(300)

const queryParams = ref<ProductQuery>({
  page: 1,
  pageSize: 10,
  name: '',
  minPrice: undefined,
  maxPrice: undefined,
  stockStatus: 'all',
  status: 'all'
})

const fetchProducts = async () => {
  const params = {
    page: pagination.page.value - 1, // 后端使用0-based分页
    size: pagination.pageSize.value,
    name: queryParams.value.name || undefined,
    status: queryParams.value.status === 'all' ? undefined : queryParams.value.status
  }

  const result = await execute(params)
  if (result) {
    products.value = result.content || []
    pagination.setTotal(result.totalElements || 0)
  }

  if (error.value) {
    ElMessage.error('获取商品列表失败')
  }
}

const handleSearch = () => {
  pagination.firstPage()
  fetchProducts()
}

const handleReset = () => {
  queryParams.value = {
    page: 1,
    pageSize: 10,
    name: '',
    minPrice: undefined,
    maxPrice: undefined,
    stockStatus: 'all',
    status: 'all'
  }
  pagination.reset()
  fetchProducts()
}

const handlePageChange = (page: number) => {
  pagination.setPage(page)
  fetchProducts()
}

const handleSizeChange = (size: number) => {
  pagination.setPageSize(size)
  fetchProducts()
}

const handleSelectionChange = (rows: Product[]) => {
  selectedRows.value = rows
}

const handleDelete = async (row: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品「${row.name}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteProduct(row.id)
    ElMessage.success('删除成功')

    if (products.value.length === 1 && pagination.page.value > 1) {
      pagination.prevPage()
    }
    fetchProducts()
  } catch (err: any) {
    if (err !== 'cancel') {
      ElMessage.error(err.message || '删除失败')
    }
  }
}

const handleStatusChange = async (row: Product) => {
  const newStatus = row.status
  const oldStatus = newStatus === 'enabled' ? 'disabled' : 'enabled'

  try {
    await updateProductStatus(row.id, newStatus)
    ElMessage.success(`${newStatus === 'enabled' ? '启用' : '禁用'}成功`)
  } catch (err: any) {
    ElMessage.error(err.message || '状态更新失败')
    row.status = oldStatus
  }
}

const handleBatchEnable = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要启用选中的 ${selectedRows.value.length} 个商品吗？`,
      '批量启用确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    const ids = selectedRows.value.map(row => row.id)
    await batchUpdateProductStatus(ids, 'enabled')
    ElMessage.success('批量启用成功')
    fetchProducts()
  } catch (err: any) {
    if (err !== 'cancel') {
      ElMessage.error(err.message || '批量启用失败')
    }
  }
}

const handleBatchDisable = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要禁用选中的 ${selectedRows.value.length} 个商品吗？`,
      '批量禁用确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const ids = selectedRows.value.map(row => row.id)
    await batchUpdateProductStatus(ids, 'disabled')
    ElMessage.success('批量禁用成功')
    fetchProducts()
  } catch (err: any) {
    if (err !== 'cancel') {
      ElMessage.error(err.message || '批量禁用失败')
    }
  }
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个商品吗？此操作不可恢复。`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )

    const ids = selectedRows.value.map(row => row.id)
    await batchDeleteProducts(ids)
    ElMessage.success('批量删除成功')

    if (products.value.length === selectedRows.value.length && pagination.page.value > 1) {
      pagination.prevPage()
    }
    fetchProducts()
  } catch (err: any) {
    if (err !== 'cancel') {
      ElMessage.error(err.message || '批量删除失败')
    }
  }
}

const handleEdit = (row: Product) => {
  router.push({ path: '/merchant/product-edit', query: { id: row.id } })
}

const handleAdd = () => {
  router.push('/merchant/product-edit')
}

const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}

const formatDate = (date: string | undefined) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const getStockStatus = (stock: number) => {
  if (stock <= 0) return { type: 'danger' as const, text: '缺货' }
  if (stock <= STORAGE_WARNING_THRESHOLD) return { type: 'warning' as const, text: '库存不足' }
  return { type: 'success' as const, text: '有货' }
}

const isStockLow = (stock: number) => {
  return stock > 0 && stock <= STORAGE_WARNING_THRESHOLD
}

const isStockOut = (stock: number) => {
  return stock <= 0
}

watch(
  () => search.debouncedKeyword.value,
  (newKeyword) => {
    queryParams.value.name = newKeyword
    pagination.firstPage()
    fetchProducts()
  }
)

onMounted(() => {
  fetchProducts()
})
</script>

<template>
  <div class="merchant-products">
    <div class="page-header">
      <h2 class="page-title">商品管理</h2>
      <div class="header-actions">
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加商品</el-button>
      </div>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="商品名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入商品名称"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" style="width: 120px" clearable>
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

    <div class="batch-actions">
      <el-button :disabled="selectedRows.length === 0" @click="handleBatchEnable">
        批量启用
      </el-button>
      <el-button :disabled="selectedRows.length === 0" @click="handleBatchDisable">
        批量禁用
      </el-button>
      <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">
        批量删除
      </el-button>
      <span v-if="selectedRows.length > 0" class="selected-count">
        已选择 {{ selectedRows.length }} 项
      </span>
    </div>

    <el-table
      :data="products"
      v-loading="loading"
      style="width: 100%"
      @selection-change="handleSelectionChange"
      :empty-text="error ? '加载失败，请重试' : '暂无数据'"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="商品图片" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.image"
            :src="row.image"
            fit="cover"
            style="width: 60px; height: 60px; border-radius: 4px;"
            :preview-src-list="[row.image]"
          />
          <div v-else class="image-placeholder">暂无图片</div>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.description || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="价格" width="100">
        <template #default="{ row }">
          <span class="price-text">{{ formatPrice(row.price) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="库存" width="100">
        <template #default="{ row }">
          <div class="stock-cell">
            <el-tag :type="getStockStatus(row.stock).type" size="small">
              {{ getStockStatus(row.stock).text }}
            </el-tag>
            <span v-if="row.stock > 0" class="stock-count">{{ row.stock }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            active-value="enabled"
            inactive-value="disabled"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="110">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page.value"
        v-model:page-size="pagination.pageSize.value"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total.value"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<style scoped>
.merchant-products {
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
  width: 60px;
  height: 60px;
  border-radius: 4px;
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

.stock-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stock-count {
  font-size: 12px;
  color: #606266;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
