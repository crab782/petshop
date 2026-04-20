<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getMerchantReviews, 
  replyReview, 
  deleteReview,
  type Review,
  type ReviewQuery,
  type ReviewListResponse
} from '@/api/merchant'
import { useAsync } from '@/composables/useAsync'
import { usePagination } from '@/composables/usePagination'
import { useSearch } from '@/composables/useSearch'

// 使用组合式函数
const { data: reviewsData, loading, error, execute: fetchReviews } = useAsync<ReviewListResponse>()
const { page, pageSize, total, setTotal, setPage, setPageSize } = usePagination(1, 10)
const { keyword, filters, debouncedKeyword, setKeyword, setFilter, clearFilters } = useSearch(300)

// 评价列表数据
const reviews = ref<Review[]>([])
const ratingDistribution = ref({
  five: 0,
  four: 0,
  three: 0,
  two: 0,
  one: 0
})

// 回复对话框
const replyDialogVisible = ref(false)
const currentReview = ref<Review | null>(null)
const replyContent = ref('')

// 日期范围筛选
const dateRange = ref<[string, string] | null>(null)

// 计算平均评分
const averageRating = computed(() => {
  const total = ratingDistribution.value.five + ratingDistribution.value.four + 
                ratingDistribution.value.three + ratingDistribution.value.two + 
                ratingDistribution.value.one
  if (total === 0) return 0
  const sum = ratingDistribution.value.five * 5 + ratingDistribution.value.four * 4 +
              ratingDistribution.value.three * 3 + ratingDistribution.value.two * 2 +
              ratingDistribution.value.one * 1
  return (sum / total).toFixed(1)
})

// 加载评价数据
const loadReviews = async () => {
  const query: ReviewQuery = {
    page: page.value,
    pageSize: pageSize.value,
    keyword: debouncedKeyword.value || undefined,
    rating: filters.value.rating || undefined,
    startDate: dateRange.value?.[0] || undefined,
    endDate: dateRange.value?.[1] || undefined
  }
  
  const result = await fetchReviews(() => getMerchantReviews(query))
  
  if (result) {
    reviews.value = result.list
    setTotal(result.total)
    ratingDistribution.value = result.ratingDistribution
  }
}

// 监听筛选条件变化
watch([page, pageSize, debouncedKeyword, filters, dateRange], () => {
  loadReviews()
}, { deep: true })

// 评分筛选
const handleRatingFilter = (rating: number) => {
  if (rating === 0) {
    setFilter('rating', undefined)
  } else {
    setFilter('rating', rating)
  }
  page.value = 1
}

// 打开回复对话框
const openReplyDialog = (review: Review) => {
  currentReview.value = review
  replyContent.value = review.replyContent || ''
  replyDialogVisible.value = true
}

// 提交回复
const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  if (!currentReview.value) return
  
  try {
    await replyReview(currentReview.value.id, replyContent.value)
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    loadReviews()
  } catch (err) {
    ElMessage.error('回复失败，请重试')
  }
}

// 删除评价
const handleDelete = async (review: Review) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条评价吗？删除后将无法恢复。',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteReview(review.id)
    ElMessage.success('删除成功')
    loadReviews()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败，请重试')
    }
  }
}

// 格式化日期
const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  setPageSize(size)
}

// 页码改变
const handleCurrentChange = (newPage: number) => {
  setPage(newPage)
}

// 日期范围改变
const handleDateRangeChange = (value: [string, string] | null) => {
  dateRange.value = value
  page.value = 1
}

// 组件挂载时加载数据
onMounted(() => {
  loadReviews()
})
</script>

<template>
  <div class="merchant-reviews">
    <div class="header">
      <h2>服务评价列表</h2>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-label">平均评分</div>
        <div class="stat-value">
          <span class="rating-number">{{ averageRating }}</span>
          <el-rate 
            :model-value="Number(averageRating)" 
            disabled 
            show-score 
            text-color="#ff9900"
          />
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-label">总评价数</div>
        <div class="stat-value">{{ total }}</div>
      </div>
    </div>

    <!-- 评分分布 -->
    <div class="rating-distribution">
      <div class="distribution-title">评分分布</div>
      <div class="distribution-bars">
        <div 
          v-for="star in [5, 4, 3, 2, 1]" 
          :key="star"
          class="distribution-item"
          @click="handleRatingFilter(star)"
        >
          <span class="star-label">{{ star }}星</span>
          <div class="bar-container">
            <div 
              class="bar-fill" 
              :style="{ 
                width: `${total > 0 ? (ratingDistribution[['one','two','three','four','five'][star-1] as keyof typeof ratingDistribution] / total * 100) : 0}%` 
              }"
            ></div>
          </div>
          <span class="count">{{ ratingDistribution[['one','two','three','four','five'][star-1] as keyof typeof ratingDistribution] }}</span>
        </div>
      </div>
    </div>

    <!-- 筛选器 -->
    <div class="filters">
      <div class="filter-row">
        <div class="filter-item">
          <span class="filter-label">评分筛选：</span>
          <div class="rating-buttons">
            <el-button 
              :type="!filters.rating ? 'primary' : 'default'"
              size="small"
              @click="handleRatingFilter(0)"
            >
              全部
            </el-button>
            <el-button 
              v-for="rating in [5, 4, 3, 2, 1]"
              :key="rating"
              :type="filters.rating === rating ? 'primary' : 'default'"
              size="small"
              @click="handleRatingFilter(rating)"
            >
              {{ rating }}星及以上
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="filter-row">
        <div class="filter-item">
          <span class="filter-label">日期范围：</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
        </div>
        
        <div class="filter-item">
          <span class="filter-label">关键字搜索：</span>
          <el-input
            v-model="keyword"
            placeholder="搜索评价内容、用户名"
            clearable
            style="width: 300px"
          />
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="40">
        <i class="el-icon-loading"></i>
      </el-icon>
      <p>加载中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <el-icon :size="40" color="#f56c6c">
        <i class="el-icon-warning"></i>
      </el-icon>
      <p>{{ error.message }}</p>
      <el-button type="primary" @click="loadReviews">重试</el-button>
    </div>

    <!-- 评价列表 -->
    <div v-else class="reviews-list">
      <div v-if="reviews.length === 0" class="empty-state">
        <el-empty description="暂无评价数据" />
      </div>
      
      <div v-else class="review-cards">
        <div 
          v-for="review in reviews" 
          :key="review.id" 
          class="review-card"
        >
          <div class="review-header">
            <div class="user-info">
              <el-avatar :size="48" :src="review.userAvatar">
                {{ review.userName.charAt(0) }}
              </el-avatar>
              <div class="user-details">
                <div class="user-name">{{ review.userName }}</div>
                <div class="review-meta">
                  <el-rate :model-value="review.rating" disabled />
                  <span class="service-name">{{ review.serviceName }}</span>
                  <span class="review-time">{{ formatDate(review.reviewTime) }}</span>
                </div>
              </div>
            </div>
            <div class="review-actions">
              <el-button 
                type="primary" 
                size="small"
                @click="openReplyDialog(review)"
              >
                回复
              </el-button>
              <el-button 
                type="danger" 
                size="small"
                @click="handleDelete(review)"
              >
                删除
              </el-button>
            </div>
          </div>
          
          <div class="review-content">
            {{ review.content }}
          </div>
          
          <!-- 商家回复 -->
          <div v-if="review.replyContent" class="merchant-reply">
            <div class="reply-header">
              <el-icon><i class="el-icon-chat-dot-round"></i></el-icon>
              <span>商家回复</span>
              <span class="reply-time">{{ formatDate(review.replyTime!) }}</span>
            </div>
            <div class="reply-content">{{ review.replyContent }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 回复对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      title="回复评价"
      width="500px"
    >
      <div class="reply-dialog-content">
        <div v-if="currentReview" class="original-review">
          <div class="review-user">{{ currentReview.userName }}</div>
          <el-rate :model-value="currentReview.rating" disabled size="small" />
          <div class="review-text">{{ currentReview.content }}</div>
        </div>
        
        <el-input
          v-model="replyContent"
          type="textarea"
          :rows="5"
          placeholder="请输入回复内容..."
          maxlength="500"
          show-word-limit
        />
      </div>
      
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.merchant-reviews {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header {
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.rating-number {
  font-size: 32px;
  color: #ff9900;
}

.rating-distribution {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.distribution-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
}

.distribution-bars {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.distribution-item {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.distribution-item:hover {
  background-color: #f5f7fa;
}

.star-label {
  width: 40px;
  font-size: 14px;
  color: #606266;
}

.bar-container {
  flex: 1;
  height: 8px;
  background-color: #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #ff9900, #ffb84d);
  border-radius: 4px;
  transition: width 0.3s;
}

.count {
  width: 40px;
  text-align: right;
  font-size: 14px;
  color: #909399;
}

.filters {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 15px;
}

.filter-row:last-child {
  margin-bottom: 0;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.rating-buttons {
  display: flex;
  gap: 8px;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
}

.loading-container p,
.error-container p {
  margin-top: 20px;
  font-size: 16px;
  color: #909399;
}

.reviews-list {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.empty-state {
  padding: 60px 20px;
}

.review-cards {
  padding: 20px;
}

.review-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  transition: box-shadow 0.3s;
}

.review-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.15);
}

.review-card:last-child {
  margin-bottom: 0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  gap: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.user-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.service-name {
  font-size: 14px;
  color: #909399;
}

.review-time {
  font-size: 14px;
  color: #c0c4cc;
}

.review-actions {
  display: flex;
  gap: 8px;
}

.review-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 15px;
}

.merchant-reply {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  border-left: 3px solid #409eff;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: bold;
  color: #409eff;
}

.reply-time {
  font-size: 12px;
  color: #c0c4cc;
  font-weight: normal;
  margin-left: auto;
}

.reply-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

.reply-dialog-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.original-review {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
}

.review-user {
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.review-text {
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .filter-row {
    flex-direction: column;
  }
  
  .rating-buttons {
    flex-wrap: wrap;
  }
  
  .review-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .review-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
