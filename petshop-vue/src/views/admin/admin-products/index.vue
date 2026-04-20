<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllProducts, updateProductStatus, deleteProduct, batchUpdateProductStatus, batchDeleteProducts, getAllMerchants, type Merchant, type Product } from '@/api/admin'
import { Search, Refresh, Check, Close, Edit, Delete, View } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const productList = ref<Product[]>([])
const merchantList = ref<Merchant[]>([])
const selectedProducts = ref<Product[]>([])

const searchForm = ref({
  keyword: '',
  merchantId: null as number | null,
  minPrice: '',
  maxPrice: '',
  stockStatus: '',
  status: ''
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const filteredProducts = ref<Product[]>([])

const loadMerchants = async () => {
  try {
    const response = await fetch('/api/admin/merchants')
    if (!response.ok) throw new Error('加载商家列表失败')
    const data = await response.json()
    merchantList.value = data.map((merchant: any) => ({
      id: merchant.id,
      name: merchant.name
    }))
  } catch (error) {
    console.error('加载商家列表失败:', error)
    ElMessage.error('加载商家列表失败')
  }
}

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/admin/products')
    if (!response.ok) throw new Error('加载商品列表失败')
    const data = await response.json()
    productList.value = data.map((product: any) => ({
      id: product.id,
      name: product.name,
      merchantId: product.merchantId,
      merchantName: '',
      price: product.price,
      stock: product.stock,
      sales: product.sales || 0,
      status: product.status,
      createTime: product.createdAt ? new Date(product.createdAt).toLocaleString('zh-CN') : ''
    }))
    // 为商品添加商家名称
    productList.value.forEach(product => {
      const merchant = merchantList.value.find(m => m.id === product.merchantId)
      if (merchant) {
        product.merchantName = merchant.name
      }
    })
    filterData()
  } catch (error) {
    ElMessage.error('加载商品列表失败')
    console.error('Error loading products:', error)
  } finally {
    loading.value = false
  }
}

const filterData = () => {
  let result = [...productList.value]

  if (searchForm.value.keyword) {
    const query = searchForm.value.keyword.toLowerCase()
    result = result.filter(product =>
      product.name.toLowerCase().includes(query) ||
      product.merchantName.toLowerCase().includes(query)
    )
  }

  if (searchForm.value.merchantId) {
    result = result.filter(product => product.merchantId === searchForm.value.merchantId)
  }

  if (searchForm.value.minPrice) {
    result = result.filter(product => product.price >= Number(searchForm.value.minPrice))
  }

  if (searchForm.value.maxPrice) {
    result = result.filter(product => product.price <= Number(searchForm.value.maxPrice))
  }

  if (searchForm.value.stockStatus) {
    if (searchForm.value.stockStatus === 'in_stock') {
      result = result.filter(product => product.stock > 0)
    } else if (searchForm.value.stockStatus === 'out_of_stock') {
      result = result.filter(product => product.stock <= 0)
    }
  }

  if (searchForm.value.status) {
    result = result.filter(product => product.status === searchForm.value.status)
  }

  pagination.value.total = result.length
  const start = (pagination.value.current - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  filteredProducts.value = result.slice(start, end)
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
    stockStatus: '',
    status: ''
  }
  pagination.value.current = 1
  filterData()
}

const handleSelectionChange = (selection: Product[]) => {
  selectedProducts.value = selection
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

const handleStatusChange = async (product: Product, newStatus: boolean) => {
  const status = newStatus ? 'active' : 'inactive'
  try {
    const response = await fetch(`/api/admin/products/${product.id}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ status })
    })
    if (!response.ok) throw new Error('更新状态失败')
    product.status = status as 'active' | 'inactive'
    ElMessage.success(`商品已${status === 'active' ? '上架' : '下架'}`)
  } catch (error) {
    ElMessage.error('更新状态失败')
    console.error('Error updating product status:', error)
    filterData()
  }
}

const handleEdit = (product: Product) => {
  router.push({ path: '/admin/product-edit', query: { id: product.id } })
}

const handleDelete = (product: Product) => {
  ElMessageBox.confirm(
    `确定要删除商品"${product.name}"吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await fetch(`/api/admin/products/${product.id}`, {
        method: 'DELETE'
      })
      if (!response.ok) throw new Error('删除失败')
      ElMessage.success('删除成功')
      loadProducts()
    } catch (error) {
      ElMessage.error('删除失败，请重试')
      console.error('Error deleting product:', error)
    }
  }).catch(() => {})
}

const handleViewDetail = (product: Product) => {
  router.push({ path: '/admin/product-manage', query: { id: product.id } })
}

const handleBatchEnable = async () => {
  if (selectedProducts.value.length === 0) {
    ElMessage.warning('请选择要上架的商品')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要上架选中的 ${selectedProducts.value.length} 项商品吗？`,
      '批量上架',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const ids = selectedProducts.value.map(p => p.id)
    const response = await fetch('/api/admin/products/batch/status', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ ids, status: 'active' })
    })
    if (!response.ok) throw new Error('批量上架失败')
    ElMessage.success('批量上架成功')
    loadProducts()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量上架失败')
      console.error('Error batch enabling products:', error)
    }
  }
}

const handleBatchDisable = async () => {
  if (selectedProducts.value.length === 0) {
    ElMessage.warning('请选择要下架的商品')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要下架选中的 ${selectedProducts.value.length} 项商品吗？`,
      '批量下架',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const ids = selectedProducts.value.map(p => p.id)
    const response = await fetch('/api/admin/products/batch/status', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ ids, status: 'inactive' })
    })
    if (!response.ok) throw new Error('批量下架失败')
    ElMessage.success('批量下架成功')
    loadProducts()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量下架失败')
      console.error('Error batch disabling products:', error)
    }
  }
}

const handleBatchDelete = async () => {
  if (selectedProducts.value.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedProducts.value.length} 项商品吗？此操作不可恢复！`,
      '批量删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const ids = selectedProducts.value.map(p => p.id)
    const response = await fetch('/api/admin/products/batch', {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ ids })
    })
    if (!response.ok) throw new Error('批量删除失败')
    ElMessage.success('批量删除成功')
    loadProducts()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
      console.error('Error batch deleting products:', error)
    }
  }
}

onMounted(() => {
  loadProducts()
  loadMerchants()
})
</script>

<template>
  <div class="product-management">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>商品管理</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="filter-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词搜索">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入商品名称或商家名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
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
        <el-form-item label="库存状态">
          <el-select v-model="searchForm.stockStatus" placeholder="请选择" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="有货" value="in_stock" />
            <el-option label="缺货" value="out_of_stock" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="上架" value="active" />
            <el-option label="下架" value="inactive" />
          </el-select>
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

    <el-card v-if="selectedProducts.length > 0" shadow="hover" class="batch-actions">
      <span class="selected-info">已选择 {{ selectedProducts.length }} 项</span>
      <el-button type="success" size="small" @click="handleBatchEnable">
        <el-icon><Check /></el-icon>
        批量上架
      </el-button>
      <el-button type="warning" size="small" @click="handleBatchDisable">
        <el-icon><Close /></el-icon>
        批量下架
      </el-button>
      <el-button type="danger" size="small" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </el-card>

    <el-card shadow="hover" class="table-card">
      <el-table
        :data="filteredProducts"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="商品ID" width="80" />
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column prop="merchantName" label="商家名称" min-width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.price.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80">
          <template #default="{ row }">
            <span :class="{ 'low-stock': row.stock <= 0, 'stock-warning': row.stock > 0 && row.stock <= 10 }">
              {{ row.stock }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
              {{ row.status === 'active' ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
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
.product-management {
  padding: 0;
}

.filter-card {
  margin-top: 20px;
}

.batch-actions {
  margin-top: 16px;
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
  margin-top: 16px;
}

.price {
  color: #f56c6c;
  font-weight: 500;
}

.low-stock {
  color: #f56c6c;
  font-weight: 500;
}

.stock-warning {
  color: #e6a23c;
  font-weight: 500;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
