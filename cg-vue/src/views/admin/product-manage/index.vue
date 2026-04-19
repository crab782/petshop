<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElButton, ElTable, ElTableColumn, ElImage, ElDialog, ElMessage, ElMessageBox, ElSwitch, ElInput, ElCard, ElForm, ElFormItem, ElSelect, ElOption } from 'element-plus'
import { Search, Refresh, Edit, Delete, View } from '@element-plus/icons-vue'
import { getAllProductsForAdmin, updateProductStatus, deleteProduct, getAllMerchants, type Merchant, type Product } from '@/api/admin'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const productList = ref<Product[]>([])
const merchantList = ref<Merchant[]>([])
const selectedProducts = ref<Product[]>([])
const detailDialogVisible = ref(false)
const currentProduct = ref<Product | null>(null)
const editDialogVisible = ref(false)
const editForm = ref({
  name: '',
  price: 0,
  stock: 0,
  description: ''
})

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

const filteredProducts = computed(() => {
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
  return result.slice(start, end)
})

const loadMerchants = async () => {
  try {
    const data = await getAllMerchants()
    merchantList.value = data || []
  } catch (error) {
    console.error('加载商家列表失败:', error)
  }
}

const loadProducts = async () => {
  loading.value = true
  try {
    const data = await getAllProductsForAdmin()
    productList.value = data
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
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
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
}

const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size
  pagination.value.current = 1
}

const handleStatusChange = async (product: Product, newStatus: boolean) => {
  const status = newStatus ? 'active' : 'inactive'
  try {
    await updateProductStatus(product.id, status)
    product.status = status as 'active' | 'inactive'
    ElMessage.success(`商品已${status === 'active' ? '上架' : '下架'}`)
  } catch (error) {
    ElMessage.error('更新状态失败')
    loadProducts()
  }
}

const handleView = (product: Product) => {
  currentProduct.value = product
  detailDialogVisible.value = true
}

const handleEdit = (product: Product) => {
  editForm.value = {
    name: product.name,
    price: product.price,
    stock: product.stock,
    description: product.description || ''
  }
  currentProduct.value = product
  editDialogVisible.value = true
}

const handleDelete = async (product: Product) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？此操作不可恢复！', '提示', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteProduct(product.id)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadProducts()
  loadMerchants()
})
</script>

<template>
  <div class="product-manage">
    <div class="page-header">
      <h2 class="page-title">商品详情</h2>
    </div>

    <el-card shadow="hover" class="filter-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词搜索">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索商品名称或商家名称"
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

    <el-card shadow="hover" class="table-card">
      <el-table
        :data="filteredProducts"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="商品ID" width="100" />
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
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 'active'"
              active-text="上架"
              inactive-text="下架"
              inline-prompt
              @change="handleStatusChange(row, $event)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog
      v-model="detailDialogVisible"
      title="商品详情"
      width="600px"
      destroy-on-close
    >
      <el-descriptions :column="2" border v-if="currentProduct">
        <el-descriptions-item label="商品ID">{{ currentProduct.id }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentProduct.name }}</el-descriptions-item>
        <el-descriptions-item label="商家ID">{{ currentProduct.merchantId }}</el-descriptions-item>
        <el-descriptions-item label="商家名称">{{ currentProduct.merchantName }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ currentProduct.price.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="库存">
          <span :class="{ 'low-stock': currentProduct.stock <= 0 }">
            {{ currentProduct.stock }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentProduct.status === 'active' ? 'success' : 'info'">
            {{ currentProduct.status === 'active' ? '上架' : '下架' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="销量">{{ currentProduct.sales }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentProduct.createTime }}</el-descriptions-item>
        <el-descriptions-item label="商品描述" :span="2">
          {{ currentProduct.description || '暂无描述' }}
        </el-descriptions-item>
        <el-descriptions-item label="商品图片" :span="2">
          <el-image
            v-if="currentProduct.image"
            :src="currentProduct.image"
            fit="contain"
            style="width: 200px; height: 200px"
          />
          <span v-else>暂无图片</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="editDialogVisible"
      title="编辑商品"
      width="500px"
      destroy-on-close
    >
      <el-form :model="editForm" label-width="100px" v-if="currentProduct">
        <el-form-item label="商品名称">
          <el-input v-model="editForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商家">
          <el-select
            v-model="currentProduct.merchantId"
            disabled
            style="width: 100%"
          >
            <el-option
              :label="currentProduct.merchantName"
              :value="currentProduct.merchantId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="editForm.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="editForm.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="editForm.description" type="textarea" :rows="4" placeholder="请输入商品描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="editDialogVisible = false">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.product-manage {
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

.table-card {
  margin-bottom: 20px;
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
