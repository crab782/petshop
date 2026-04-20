<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllReviews, deleteReview, type Review } from '@/api/admin'
import { Search, Refresh, View, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const reviewList = ref<Review[]>([])
const searchQuery = ref('')

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const detailDialogVisible = ref(false)
const currentReview = ref<Review | null>(null)

const filteredList = computed(() => {
  if (!searchQuery.value) return reviewList.value
  const query = searchQuery.value.toLowerCase()
  return reviewList.value.filter(item =>
    item.serviceName.toLowerCase().includes(query) ||
    item.merchantName.toLowerCase().includes(query) ||
    item.userName.toLowerCase().includes(query)
  )
})

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredList.value.slice(start, end)
})

const stats = computed(() => {
  const totalReviews = reviewList.value.length
  const avgRating = totalReviews > 0
    ? (reviewList.value.reduce((sum, item) => sum + item.rating, 0) / totalReviews).toFixed(1)
    : '0.0'
  const goodRatingCount = reviewList.value.filter(item => item.rating >= 4).length
  const goodRatingRate = totalReviews > 0
    ? ((goodRatingCount / totalReviews) * 100).toFixed(1)
    : '0.0'
  return {
    totalReviews,
    avgRating,
    goodRatingRate
  }
})

const loadReviews = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/admin/reviews')
    if (!response.ok) throw new Error('加载评价列表失败')
    const data = await response.json()
    reviewList.value = data.map((review: any) => ({
      id: review.id,
      serviceName: review.service?.name || '',
      merchantName: review.merchant?.name || '',
      userName: review.user?.username || '',
      rating: review.rating,
      comment: review.comment,
      createdAt: review.createdAt ? new Date(review.createdAt).toLocaleString('zh-CN') : '',
      orderNo: review.appointment?.id || ''
    }))
    total.value = filteredList.value.length
  } catch (error) {
    ElMessage.error('加载评价列表失败')
    console.error('Error loading reviews:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  total.value = filteredList.value.length
}

const handleReset = () => {
  searchQuery.value = ''
  handleSearch()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleView = (row: Review) => {
  currentReview.value = row
  detailDialogVisible.value = true
}

const handleDelete = (row: Review) => {
  ElMessageBox.confirm(
    `确定要删除该评价吗？删除后将无法恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await fetch(`/api/admin/reviews/${row.id}`, {
        method: 'DELETE'
      })
      if (!response.ok) throw new Error('删除失败')
      ElMessage.success('删除成功')
      loadReviews()
    } catch (error) {
      ElMessage.error('删除失败，请重试')
      console.error('Error deleting review:', error)
    }
  }).catch(() => {})
}

const getRatingType = (rating: number) => {
  if (rating >= 4) return 'success'
  if (rating >= 3) return 'warning'
  return 'danger'
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  return dateStr
}

onMounted(() => {
  loadReviews()
})
</script>

<template>
  <div class="reviews-container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>评价管理</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="stats-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalReviews }}</div>
            <div class="stat-label">总评价数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value">{{ stats.avgRating }}</div>
            <div class="stat-label">平均评分</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value">{{ stats.goodRatingRate }}%</div>
            <div class="stat-label">好评率</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="hover" class="filter-card">
      <el-form inline>
        <el-form-item label="关键词搜索">
          <el-input
            v-model="searchQuery"
            placeholder="服务名称/商家名称/用户名称"
            clearable
            style="width: 280px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
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
        :data="paginatedList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="评价ID" width="80" />
        <el-table-column prop="serviceName" label="服务名称" min-width="150" />
        <el-table-column prop="merchantName" label="商家名称" min-width="150" />
        <el-table-column prop="userName" label="用户名称" min-width="120" />
        <el-table-column label="评分" width="150">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="评价内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="评价时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon> 查看
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="detailDialogVisible"
      title="评价详情"
      width="600px"
    >
      <div v-if="currentReview" class="review-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="评价ID">{{ currentReview.id }}</el-descriptions-item>
          <el-descriptions-item label="评分">
            <el-rate v-model="currentReview.rating" disabled text-color="#ff9900" />
          </el-descriptions-item>
          <el-descriptions-item label="服务名称" :span="2">{{ currentReview.serviceName }}</el-descriptions-item>
          <el-descriptions-item label="商家名称" :span="2">{{ currentReview.merchantName }}</el-descriptions-item>
          <el-descriptions-item label="用户名称" :span="2">{{ currentReview.userName }}</el-descriptions-item>
          <el-descriptions-item label="评价时间" :span="2">{{ formatDate(currentReview.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="订单编号" :span="2">
            {{ currentReview.orderNo || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="评价内容" :span="2">
            <div class="comment-content">{{ currentReview.comment || '暂无评价内容' }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.reviews-container {
  padding: 0;
}

.stats-card {
  margin-top: 20px;
}

.stat-item {
  text-align: center;
  padding: 10px 0;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.filter-card {
  margin-top: 20px;
}

.table-card {
  margin-top: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.review-detail {
  padding: 10px 0;
}

.comment-content {
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
