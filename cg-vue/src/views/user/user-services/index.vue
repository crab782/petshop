<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Calendar, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getUserPurchasedServices, type UserPurchasedService } from '@/api/user'

const router = useRouter()

const searchKeyword = ref('')
const selectedStatus = ref('')
const myServices = ref<UserPurchasedService[]>([])

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待使用', value: 'active' },
  { label: '已使用', value: 'used' },
  { label: '已过期', value: 'expired' }
]

const filteredServices = ref<UserPurchasedService[]>([])

const loadServices = async () => {
  loading.value = true
  try {
    const response = await getUserPurchasedServices({
      keyword: searchKeyword.value,
      status: selectedStatus.value,
      page: currentPage.value - 1,
      pageSize: pageSize.value
    })

    const data = response.data || response
    if (data) {
      myServices.value = data.data || data || []
      total.value = data.total || 0
      filteredServices.value = data.data || data || []
    }
  } catch (error) {
    console.error('加载服务列表失败:', error)
    ElMessage.error('加载服务列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadServices()
}

const handleStatusChange = () => {
  currentPage.value = 1
  loadServices()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  loadServices()
}

const handleDetail = (service: UserPurchasedService) => {
  // 跳转到服务详情页
  router.push(`/user/services/detail/${service.serviceId}`)
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    active: '待使用',
    used: '已使用',
    expired: '已过期'
  }
  return labels[status] || status
}

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    active: 'success',
    used: 'info',
    expired: 'danger'
  }
  return types[status] || 'default'
}

onMounted(() => {
  loadServices()
})
</script>

<template>
  <div class="services-page">
    <div class="page-header">
      <h1 class="page-title">我的服务</h1>
      <p class="page-description">管理您已购买的宠物服务</p>
    </div>

    <div class="search-section">
      <div class="container">
        <el-row :gutter="20" align="middle" class="search-row">
          <el-col :span="8">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索服务名称"
              clearable
              @keyup.enter="handleSearch"
              class="search-input"
            >
              <template #append>
                <el-button :icon="Search" @click="handleSearch" />
              </template>
            </el-input>
          </el-col>
          <el-col :span="6">
            <el-select
              v-model="selectedStatus"
              placeholder="服务状态"
              clearable
              @change="handleStatusChange"
              class="status-select"
            >
              <el-option
                v-for="option in statusOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-col>
        </el-row>
      </div>
    </div>

    <div v-loading="loading" class="services-content">
      <el-empty v-if="filteredServices.length === 0 && !loading" description="暂无已购买的服务" />

      <template v-else>
        <div class="container">
          <el-table :data="filteredServices" style="width: 100%" class="services-table">
            <el-table-column prop="name" label="服务名称" min-width="180" />
            <el-table-column prop="merchant" label="商家" min-width="120" />
            <el-table-column prop="price" label="价格" width="100">
              <template #default="scope">
                ¥{{ scope.row.price }}
              </template>
            </el-table-column>
            <el-table-column prop="purchaseDate" label="购买日期" width="150" />
            <el-table-column prop="expiryDate" label="有效期至" width="150" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusLabel(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button type="primary" size="small" @click="handleDetail(scope.row)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next, total"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.services-page {
  min-height: 100vh;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  padding-top: 20px;
}

.page-title {
  font-size: 32px;
  font-weight: bold;
  color: #333333;
  margin: 0 0 12px 0;
}

.page-description {
  font-size: 16px;
  color: #666666;
  margin: 0;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.search-section {
  background-color: white;
  padding: 20px 0;
  margin-bottom: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-row {
  max-width: 1200px;
  margin: 0 auto;
}

.search-input,
.category-select {
  width: 100%;
}

.services-content {
  min-height: 400px;
  padding-bottom: 40px;
}

.services-table {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.status-select {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }

  .services-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .search-row {
    flex-direction: column;
  }

  .el-col {
    width: 100%;
    margin-bottom: 12px;
  }
}
</style>
