<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElCard, ElRow, ElCol, ElStatistic, ElTable, ElTableColumn, ElButton, ElTag, ElRate, ElDialog, ElInput, ElMessage, ElPagination, ElDatePicker, ElSelect, ElOption, ElPopconfirm, ElEmpty, ElProgress } from 'element-plus'
import { Star, ChatDotRound, User, Document, Delete, Search, Refresh } from '@element-plus/icons-vue'
import { getMerchantReviews, replyReview, deleteReview, type Review, type ReviewQuery, type ReviewListResponse } from '@/api/merchant'
import dayjs from 'dayjs'

const reviews = ref<Review[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const currentReview = ref<Review | null>(null)
const replyContent = ref('')
const submitting = ref(false)

const queryParams = ref<ReviewQuery>({
  page: 1,
  pageSize: 10,
  rating: undefined,
  startDate: '',
  endDate: '',
  keyword: ''
})

const total = ref(0)
const ratingDistribution = ref({
  five: 0,
  four: 0,
  three: 0,
  two: 0,
  one: 0
})

const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0
  const sum = reviews.value.reduce((acc, r) => acc + r.rating, 0)
  return Number((sum / reviews.value.length).toFixed(1))
})

const totalReviews = computed(() => total.value)

const positiveRate = computed(() => {
  if (total.value === 0) return 0
  const positive = ratingDistribution.value.five + ratingDistribution.value.four
  return Number(((positive / total.value) * 100).toFixed(1))
})

const fetchReviews = async () => {
  loading.value = true
  try {
    const res = await getMerchantReviews(queryParams.value)
    const data = res.data as ReviewListResponse
    reviews.value = data.list
    total.value = data.total
    ratingDistribution.value = data.ratingDistribution
  } catch (error) {
    console.error('获取评价列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.value.page = 1
  fetchReviews()
}

const handleReset = () => {
  queryParams.value = {
    page: 1,
    pageSize: 10,
    rating: undefined,
    startDate: '',
    endDate: '',
    keyword: ''
  }
  fetchReviews()
}

const handlePageChange = (page: number) => {
  queryParams.value.page = page
  fetchReviews()
}

const handleSizeChange = (size: number) => {
  queryParams.value.pageSize = size
  queryParams.value.page = 1
  fetchReviews()
}

const openReplyDialog = (review: Review) => {
  currentReview.value = review
  replyContent.value = ''
  dialogVisible.value = true
}

const handleReply = async () => {
  if (!currentReview.value || !replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  submitting.value = true
  try {
    await replyReview(currentReview.value.id, replyContent.value)
    ElMessage.success('回复成功')
    dialogVisible.value = false
    fetchReviews()
  } catch (error) {
    ElMessage.error('回复失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await deleteReview(id)
    ElMessage.success('删除成功')
    fetchReviews()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const getRatingTagType = (rating: number) => {
  if (rating >= 4) return 'success'
  if (rating >= 3) return 'warning'
  return 'danger'
}

onMounted(() => {
  fetchReviews()
})
</script>

<template>
  <div class="merchant-reviews">
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic :value="averageRating" :precision="1">
            <template #title>
              <span class="stat-title">平均评分</span>
            </template>
            <template #prefix>
              <el-icon class="stat-icon rating-icon"><Star /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="总评价数" :value="totalReviews">
            <template #prefix>
              <el-icon class="stat-icon reviews-icon"><ChatDotRound /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic :value="positiveRate" suffix="%">
            <template #title>
              <span class="stat-title">好评率</span>
            </template>
            <template #prefix>
              <el-icon class="stat-icon positive-icon"><Star /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card distribution-card">
          <template #header>
            <span class="stat-title">评分分布</span>
          </template>
          <div class="rating-distribution">
            <div class="distribution-item">
              <span class="star-label"><el-icon class="star-icon"><Star /></el-icon> 5星</span>
              <el-progress :percentage="total > 0 ? (ratingDistribution.five / total) * 100 : 0" :stroke-width="8" :show-text="false" color="#f6c23e" />
              <span class="distribution-count">{{ ratingDistribution.five }}</span>
            </div>
            <div class="distribution-item">
              <span class="star-label"><el-icon class="star-icon"><Star /></el-icon> 4星</span>
              <el-progress :percentage="total > 0 ? (ratingDistribution.four / total) * 100 : 0" :stroke-width="8" :show-text="false" color="#f6c23e" />
              <span class="distribution-count">{{ ratingDistribution.four }}</span>
            </div>
            <div class="distribution-item">
              <span class="star-label"><el-icon class="star-icon"><Star /></el-icon> 3星</span>
              <el-progress :percentage="total > 0 ? (ratingDistribution.three / total) * 100 : 0" :stroke-width="8" :show-text="false" color="#f6c23e" />
              <span class="distribution-count">{{ ratingDistribution.three }}</span>
            </div>
            <div class="distribution-item">
              <span class="star-label"><el-icon class="star-icon"><Star /></el-icon> 2星</span>
              <el-progress :percentage="total > 0 ? (ratingDistribution.two / total) * 100 : 0" :stroke-width="8" :show-text="false" color="#f6c23e" />
              <span class="distribution-count">{{ ratingDistribution.two }}</span>
            </div>
            <div class="distribution-item">
              <span class="star-label"><el-icon class="star-icon"><Star /></el-icon> 1星</span>
              <el-progress :percentage="total > 0 ? (ratingDistribution.one / total) * 100 : 0" :stroke-width="8" :show-text="false" color="#f6c23e" />
              <span class="distribution-count">{{ ratingDistribution.one }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">评价列表</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Refresh" @click="fetchReviews">刷新</el-button>
          </div>
        </div>
      </template>

      <div class="search-form">
        <el-row :gutter="16" align="middle">
          <el-col :span="5">
            <el-input
              v-model="queryParams.keyword"
              placeholder="搜索评价内容"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="4">
            <el-select v-model="queryParams.rating" placeholder="选择评分" clearable>
              <el-option :value="5" label="5星" />
              <el-option :value="4" label="4星" />
              <el-option :value="3" label="3星" />
              <el-option :value="2" label="2星" />
              <el-option :value="1" label="1星" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-date-picker
              v-model="queryParams.startDate"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              @change="(val: [string, string] | null) => {
                if (val) {
                  queryParams.startDate = val[0]
                  queryParams.endDate = val[1]
                } else {
                  queryParams.startDate = ''
                  queryParams.endDate = ''
                }
              }"
            />
          </el-col>
          <el-col :span="9">
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <el-table :data="reviews" v-loading="loading" style="width: 100%" :empty-text="'暂无评价数据'">
        <el-table-column prop="id" label="评价ID" width="90" />
        <el-table-column label="用户信息" width="150">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :src="row.userAvatar" :size="32">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name">{{ row.userName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="serviceName" label="服务名称" width="150" show-overflow-tooltip />
        <el-table-column prop="orderId" label="订单编号" width="120">
          <template #default="{ row }">
            <div class="order-cell">
              <el-icon><Document /></el-icon>
              <span>#{{ row.orderId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="150">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="reviewTime" label="评价时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.reviewTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="replyStatus" label="回复状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.replyStatus === 'replied' ? 'success' : 'warning'" size="small">
              {{ row.replyStatus === 'replied' ? '已回复' : '待回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="openReplyDialog(row)">
              {{ row.replyStatus === 'replied' ? '查看回复' : '回复评价' }}
            </el-button>
            <el-popconfirm title="确定要删除这条评价吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" size="small" link :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="回复评价" width="600px" destroy-on-close>
      <div class="reply-dialog-content" v-if="currentReview">
        <div class="original-review">
          <h4>原始评价</h4>
          <div class="review-header">
            <div class="user-info">
              <el-avatar :src="currentReview.userAvatar" :size="40">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name">{{ currentReview.userName }}</span>
            </div>
            <el-rate v-model="currentReview.rating" disabled />
          </div>
          <div class="review-info">
            <span class="review-time">{{ formatDate(currentReview.reviewTime) }}</span>
          </div>
          <p class="review-content-text">{{ currentReview.content }}</p>
          <div class="service-info">
            <span>服务：{{ currentReview.serviceName }}</span>
            <span>订单号：#{{ currentReview.orderId }}</span>
          </div>
        </div>

        <div class="reply-section" v-if="currentReview.replyStatus === 'replied' && currentReview.replyContent">
          <h4>商家回复</h4>
          <p class="reply-content-text">{{ currentReview.replyContent }}</p>
          <span class="reply-time" v-if="currentReview.replyTime">{{ formatDate(currentReview.replyTime) }}</span>
        </div>

        <div class="reply-form" v-else>
          <h4>回复内容</h4>
          <el-input
            v-model="replyContent"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容..."
            maxlength="200"
            show-word-limit
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReply" :loading="submitting" v-if="currentReview?.replyStatus === 'pending'">
          提交回复
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.merchant-reviews {
  padding: 0;
}

.statistics-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 24px;
  margin-right: 8px;
}

.rating-icon {
  color: #ff9900;
}

.reviews-icon {
  color: #409eff;
}

.positive-icon {
  color: #67c23a;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.distribution-card {
  height: 100%;
}

.rating-distribution {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.distribution-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.star-label {
  display: flex;
  align-items: center;
  gap: 2px;
  width: 50px;
  font-size: 12px;
  color: #606266;
}

.star-icon {
  color: #f6c23e;
}

.distribution-count {
  width: 30px;
  text-align: right;
  font-size: 12px;
  color: #909399;
}

.table-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.search-form {
  margin-bottom: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 13px;
  color: #303133;
}

.order-cell {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.reply-dialog-content {
  padding: 0 8px;
}

.original-review {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.original-review h4,
.reply-section h4,
.reply-form h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #303133;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.review-info {
  margin-bottom: 12px;
}

.review-time {
  font-size: 12px;
  color: #909399;
}

.review-content-text {
  margin: 0 0 12px 0;
  color: #606266;
  line-height: 1.6;
}

.service-info {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #909399;
}

.reply-section {
  background: #f0f9eb;
  padding: 16px;
  border-radius: 8px;
  border-left: 3px solid #67c23a;
}

.reply-content-text {
  margin: 0 0 8px 0;
  color: #606266;
  line-height: 1.6;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}

.reply-form {
  margin-top: 20px;
}
</style>
