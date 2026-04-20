<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElButton, ElTable, ElTableColumn, ElDialog, ElForm, ElFormItem, ElInput, ElMessage, ElMessageBox, ElSwitch, ElPagination, ElInputNumber, ElImage, ElSelect, ElOption } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh } from '@element-plus/icons-vue'
import { useAsync } from '@/composables'
import {
  getCategories,
  addCategory,
  updateCategory,
  deleteCategory,
  updateCategoryStatus,
  batchUpdateCategoryStatus,
  batchDeleteCategories,
  type Category
} from '@/api/merchant'

// 分类列表数据
const categories = ref<Category[]>([])
const selectedRows = ref<Category[]>([])

// 分页配置
const pagination = ref({
  page: 1,
  pageSize: 10,
  total: 0
})

// 查询参数
const queryParams = ref({
  name: '',
  status: 'all' as 'all' | 'enabled' | 'disabled'
})

// 对话框配置
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const formData = ref({
  id: 0,
  name: '',
  icon: '',
  description: '',
  sort: 0,
  status: 'enabled' as 'enabled' | 'disabled'
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

// 使用 useAsync 封装 API 调用
const fetchCategoriesAsync = useAsync(getCategories)
const addCategoryAsync = useAsync(addCategory)
const updateCategoryAsync = useAsync(updateCategory)
const deleteCategoryAsync = useAsync(deleteCategory)
const updateStatusAsync = useAsync(updateCategoryStatus)
const batchUpdateStatusAsync = useAsync(batchUpdateCategoryStatus)
const batchDeleteAsync = useAsync(batchDeleteCategories)

// 计算属性：过滤后的分类列表
const filteredCategories = computed(() => {
  let result = [...categories.value]

  if (queryParams.value.name) {
    result = result.filter(c => c.name.toLowerCase().includes(queryParams.value.name.toLowerCase()))
  }

  if (queryParams.value.status !== 'all') {
    result = result.filter(c => c.status === queryParams.value.status)
  }

  result.sort((a, b) => b.sort - a.sort)

  return result
})

// 计算属性：分页后的分类列表
const paginatedCategories = computed(() => {
  const start = (pagination.value.page - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  return filteredCategories.value.slice(start, end)
})

// 更新分页总数
const updatePaginationTotal = () => {
  pagination.value.total = filteredCategories.value.length
}

// 获取分类列表
const fetchCategories = async () => {
  const result = await fetchCategoriesAsync.execute()
  if (result) {
    categories.value = result.data || []
    updatePaginationTotal()
  } else if (fetchCategoriesAsync.error.value) {
    ElMessage.error('获取分类列表失败')
  }
}

// 打开添加对话框
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加分类'
  formData.value = {
    id: 0,
    name: '',
    icon: '',
    description: '',
    sort: 0,
    status: 'enabled'
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const handleEdit = (row: Category) => {
  isEdit.value = true
  dialogTitle.value = '编辑分类'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 删除分类
const handleDelete = async (row: Category) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？删除后不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await deleteCategoryAsync.execute(row.id)
    if (result) {
      ElMessage.success('删除成功')
      fetchCategories()
    } else if (deleteCategoryAsync.error.value) {
      ElMessage.error('删除失败')
    }
  } catch (error: unknown) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 状态切换
const handleStatusChange = async (row: Category) => {
  const result = await updateStatusAsync.execute(row.id, row.status)
  if (result) {
    ElMessage.success(`${row.status === 'enabled' ? '启用' : '禁用'}成功`)
  } else if (updateStatusAsync.error.value) {
    ElMessage.error('状态更新失败')
    row.status = row.status === 'enabled' ? 'disabled' : 'enabled'
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      const data = {
        name: formData.value.name,
        icon: formData.value.icon,
        description: formData.value.description,
        sort: formData.value.sort,
        status: formData.value.status
      }
      
      if (isEdit.value) {
        const result = await updateCategoryAsync.execute(formData.value.id, data)
        if (result) {
          ElMessage.success('更新成功')
          dialogVisible.value = false
          fetchCategories()
        } else if (updateCategoryAsync.error.value) {
          ElMessage.error('更新失败')
        }
      } else {
        const result = await addCategoryAsync.execute(data)
        if (result) {
          ElMessage.success('添加成功')
          dialogVisible.value = false
          fetchCategories()
        } else if (addCategoryAsync.error.value) {
          ElMessage.error('添加失败')
        }
      }
    }
  })
}

// 选择变化
const handleSelectionChange = (rows: Category[]) => {
  selectedRows.value = rows
}

// 批量启用
const handleBatchEnable = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择分类')
    return
  }
  
  const ids = selectedRows.value.map(row => row.id)
  const result = await batchUpdateStatusAsync.execute(ids, 'enabled')
  if (result) {
    ElMessage.success('批量启用成功')
    fetchCategories()
  } else if (batchUpdateStatusAsync.error.value) {
    ElMessage.error('批量启用失败')
  }
}

// 批量禁用
const handleBatchDisable = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择分类')
    return
  }
  
  const ids = selectedRows.value.map(row => row.id)
  const result = await batchUpdateStatusAsync.execute(ids, 'disabled')
  if (result) {
    ElMessage.success('批量禁用成功')
    fetchCategories()
  } else if (batchUpdateStatusAsync.error.value) {
    ElMessage.error('批量禁用失败')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择分类')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个分类吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const ids = selectedRows.value.map(row => row.id)
    const result = await batchDeleteAsync.execute(ids)
    if (result) {
      ElMessage.success('批量删除成功')
      fetchCategories()
    } else if (batchDeleteAsync.error.value) {
      ElMessage.error('批量删除失败')
    }
  } catch (error: unknown) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.page = 1
  updatePaginationTotal()
}

// 重置
const handleReset = () => {
  queryParams.value = {
    name: '',
    status: 'all'
  }
  pagination.value.page = 1
  updatePaginationTotal()
}

// 页码变化
const handlePageChange = (page: number) => {
  pagination.value.page = page
}

// 每页条数变化
const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size
  pagination.value.page = 1
}

// 格式化日期
const formatDate = (date: string | undefined) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 排序调整
const handleSortChange = async (row: Category, direction: number) => {
  const newSort = row.sort + direction
  const result = await updateCategoryAsync.execute(row.id, { sort: newSort })
  if (result) {
    ElMessage.success('排序更新成功')
    fetchCategories()
  } else if (updateCategoryAsync.error.value) {
    ElMessage.error('排序更新失败')
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <div class="merchant-categories">
    <div class="page-header">
      <h2 class="page-title">商品分类</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">添加分类</el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="分类名称">
          <el-input v-model="queryParams.name" placeholder="请输入分类名称" clearable style="width: 180px" />
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

    <el-table
      :data="paginatedCategories"
      v-loading="fetchCategoriesAsync.loading.value"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="分类ID" width="100" />
      <el-table-column label="分类图标" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.icon"
            :src="row.icon"
            fit="cover"
            style="width: 40px; height: 40px; border-radius: 4px;"
          />
          <div v-else class="icon-placeholder">
            <el-icon><Plus /></el-icon>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="分类名称" min-width="150" />
      <el-table-column prop="description" label="分类描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="productCount" label="商品数量" width="120">
        <template #default="{ row }">
          <span :class="{ 'low-count': row.productCount === 0 }">{{ row.productCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" width="120">
        <template #default="{ row }">
          <div class="sort-cell">
            <el-button text size="small" @click="handleSortChange(row, -1)">-</el-button>
            <span>{{ row.sort }}</span>
            <el-button text size="small" @click="handleSortChange(row, 1)">+</el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            active-value="enabled"
            inactive-value="disabled"
            :loading="updateStatusAsync.loading.value"
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
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="分类图标">
          <el-input v-model="formData.icon" placeholder="请输入图标URL" />
        </el-form-item>
        <el-form-item label="分类描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入分类描述" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sort" :min="0" :precision="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="formData.status"
            active-value="enabled"
            inactive-value="disabled"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="addCategoryAsync.loading.value || updateCategoryAsync.loading.value" @click="submitForm">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.merchant-categories {
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

.icon-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.sort-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-cell span {
  min-width: 30px;
  text-align: center;
}

.low-count {
  color: #909399;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
