<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElButton, ElTable, ElTableColumn, ElImage, ElDialog, ElForm, ElFormItem, ElInput, ElMessage, ElMessageBox, ElSwitch, ElPagination, ElSelect, ElOption, ElInputNumber, ElTag } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh, Download } from '@element-plus/icons-vue'
import { getMerchantProducts, updateProduct, deleteProduct, updateProductStatus, batchUpdateProductStatus, batchDeleteProducts, type Product } from '@/api/merchant'

const STORAGE_WARNING_THRESHOLD = 10

const products = ref<Product[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加商品')
const isEdit = ref(false)
const selectedRows = ref<Product[]>([])

const formData = ref({
  id: 0,
  name: '',
  description: '',
  price: 0,
  stock: 0,
  image: '',
  status: 'enabled' as 'enabled' | 'disabled'
})

const formRef = ref()

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const queryParams = ref({
  name: '',
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined,
  stockStatus: 'all' as 'all' | 'in_stock' | 'out_of_stock',
  status: 'all' as 'all' | 'enabled' | 'disabled'
})

const pagination = ref({
  page: 1,
  pageSize: 10,
  total: 0
})

const filteredProducts = computed(() => {
  let result = [...products.value]

  if (queryParams.value.name) {
    result = result.filter(p => p.name.toLowerCase().includes(queryParams.value.name.toLowerCase()))
  }

  if (queryParams.value.minPrice !== undefined) {
    result = result.filter(p => p.price >= (queryParams.value.minPrice as number))
  }

  if (queryParams.value.maxPrice !== undefined) {
    result = result.filter(p => p.price <= (queryParams.value.maxPrice as number))
  }

  if (queryParams.value.stockStatus === 'in_stock') {
    result = result.filter(p => p.stock > 0)
  } else if (queryParams.value.stockStatus === 'out_of_stock') {
    result = result.filter(p => p.stock <= 0)
  }

  if (queryParams.value.status !== 'all') {
    result = result.filter(p => p.status === queryParams.value.status)
  }

  return result
})

const paginatedProducts = computed(() => {
  const start = (pagination.value.page - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  pagination.value.total = filteredProducts.value.length
  return filteredProducts.value.slice(start, end)
})

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await getMerchantProducts()
    products.value = res.data || []
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加商品'
  formData.value = {
    id: 0,
    name: '',
    description: '',
    price: 0,
    stock: 0,
    image: '',
    status: 'enabled'
  }
  dialogVisible.value = true
}

const handleEdit = (row: Product) => {
  isEdit.value = true
  dialogTitle.value = '编辑商品'
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: Product) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    fetchProducts()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleStatusChange = async (row: Product) => {
  try {
    await updateProductStatus(row.id, row.status)
    ElMessage.success(`${row.status === 'enabled' ? '启用' : '禁用'}成功`)
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.status = row.status === 'enabled' ? 'disabled' : 'enabled'
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const data = {
          name: formData.value.name,
          description: formData.value.description,
          price: formData.value.price,
          stock: formData.value.stock,
          image: formData.value.image,
          status: formData.value.status,
          merchantId: 1
        }
        if (isEdit.value) {
          await updateProduct(formData.value.id, data)
          ElMessage.success('更新成功')
        } else {
          ElMessage.error('添加功能请使用商品编辑页面')
        }
        dialogVisible.value = false
        fetchProducts()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
      }
    }
  })
}

const handleSelectionChange = (rows: Product[]) => {
  selectedRows.value = rows
}

const handleBatchEnable = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }
  try {
    const ids = selectedRows.value.map(row => row.id)
    await batchUpdateProductStatus(ids, 'enabled')
    ElMessage.success('批量启用成功')
    fetchProducts()
  } catch (error) {
    ElMessage.error('批量启用失败')
  }
}

const handleBatchDisable = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }
  try {
    const ids = selectedRows.value.map(row => row.id)
    await batchUpdateProductStatus(ids, 'disabled')
    ElMessage.success('批量禁用成功')
    fetchProducts()
  } catch (error) {
    ElMessage.error('批量禁用失败')
  }
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const ids = selectedRows.value.map(row => row.id)
    await batchDeleteProducts(ids)
    ElMessage.success('批量删除成功')
    fetchProducts()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const handleSearch = () => {
  pagination.value.page = 1
}

const handleReset = () => {
  queryParams.value = {
    name: '',
    minPrice: undefined,
    maxPrice: undefined,
    stockStatus: 'all',
    status: 'all'
  }
  pagination.value.page = 1
}

const handlePageChange = (page: number) => {
  pagination.value.page = page
}

const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size
  pagination.value.page = 1
}

const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}

const formatDate = (date: string | undefined) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const isStockLow = (stock: number) => {
  return stock > 0 && stock <= STORAGE_WARNING_THRESHOLD
}

const isStockOut = (stock: number) => {
  return stock <= 0
}

const goToProductEdit = () => {
  window.location.href = '/merchant/product-edit'
}

onMounted(() => {
  fetchProducts()
})
</script>

<template>
  <div class="merchant-products">
    <div class="page-header">
      <h2 class="page-title">商品管理</h2>
      <div class="header-actions">
        <el-button type="primary" :icon="Plus" @click="goToProductEdit">添加商品</el-button>
      </div>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="商品名称">
          <el-input v-model="queryParams.name" placeholder="请输入商品名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="价格区间">
          <el-input-number v-model="queryParams.minPrice" placeholder="最低价" :min="0" :precision="2" style="width: 120px" />
          <span style="margin: 0 8px">-</span>
          <el-input-number v-model="queryParams.maxPrice" placeholder="最高价" :min="0" :precision="2" style="width: 120px" />
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select v-model="queryParams.stockStatus" style="width: 120px">
            <el-option label="全部" value="all" />
            <el-option label="有货" value="in_stock" />
            <el-option label="缺货" value="out_of_stock" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" style="width: 120px">
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
      <el-button :disabled="selectedRows.length === 0" @click="handleBatchEnable">批量启用</el-button>
      <el-button :disabled="selectedRows.length === 0" @click="handleBatchDisable">批量禁用</el-button>
      <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">批量删除</el-button>
      <span v-if="selectedRows.length > 0" class="selected-count">已选择 {{ selectedRows.length }} 项</span>
    </div>

    <el-table :data="paginatedProducts" v-loading="loading" style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="商品图片" width="120">
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
      <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column label="价格" width="120">
        <template #default="{ row }">
          <span class="price-text">{{ formatPrice(row.price) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="库存" width="120">
        <template #default="{ row }">
          <span v-if="isStockOut(row.stock)" class="stock-out">缺货</span>
          <span v-else-if="isStockLow(row.stock)" class="stock-low">{{ row.stock }}</span>
          <span v-else>{{ row.stock }}</span>
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

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="formData.stock" :min="0" :precision="0" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="formData.image" placeholder="请输入图片URL" />
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

.stock-out {
  color: #f56c6c;
  font-weight: 600;
}

.stock-low {
  color: #e6a23c;
  font-weight: 600;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
