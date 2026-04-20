<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, View, Delete, Star } from '@element-plus/icons-vue'
import {
  getUserAppointments,
  addReview,
  getUserReviewsList,
  deleteReview,
  type Appointment,
  type UserReview
} from '@/api/user'

const router = useRouter()

const loading = ref(false)
const appointments = ref<Appointment[]>([])
const reviewList = ref<UserReview[]>([])
const reviewDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentAppointment = ref<Appointment | null>(null)
const currentReview = ref<UserReview | null>(null)
const reviewForm = ref({
  rating: 5,
  comment: ''
})
const reviewLoading = ref(false)

const ratingFilter = ref<number | null>(null)
const dateRange = ref<[Date, Date] | null>(null)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

const ratingOptions = [
  { label: '全部评分', value: null },
  { label: '5星', value: 5 },
  { label: '4星', value: 4 },
  { label: '3星', value: 3 },
  { label: '2星', value: 2 },
  { label: '1星', value: 1 }
]

// 硬编码测试数据 - 仅在开发环境使用
const mockAppointments: Appointment[] = [
  {
    id: 1,
    userId: 1,
    merchantId: 1,
    serviceId: 1,
    petId: 1,
    appointmentTime: '2024-01-20 14:00:00',
    status: 'completed',
    totalPrice: 88,
    notes: '需要给狗狗剪指甲',
    serviceName: '宠物洗澡美容套餐',
    merchantName: '爱心宠物美容会所',
    petName: '小白',
    createdAt: '2024-01-18 10:00:00',
    updatedAt: '2024-01-20 15:00:00'
  },
  {
    id: 2,
    userId: 1,
    merchantId: 2,
    serviceId: 2,
    petId: 2,
    appointmentTime: '2024-01-19 10:30:00',
    status: 'completed',
    totalPrice: 150,
    notes: '年度体检',
    serviceName: '宠物健康体检',
    merchantName: '宠物健康医院',
    petName: '小黑',
    createdAt: '2024-01-17 09:00:00',
    updatedAt: '2024-01-19 11:30:00'
  },
  {
    id: 3,
    userId: 1,
    merchantId: 3,
    serviceId: 3,
    petId: 1,
    appointmentTime: '2024-01-15 09:00:00',
    status: 'completed',
    totalPrice: 50,
    notes: '寄养3天',
    serviceName: '宠物寄养服务',
    merchantName: '快乐宠物寄养中心',
    petName: '小白',
    createdAt: '2024-01-10 16:00:00',
    updatedAt: '2024-01-18 10:00:00'
  },
  {
    id: 4,
    userId: 1,
    merchantId: 1,
    serviceId: 1,
    petId: 2,
    appointmentTime: '2024-01-25 15:00:00',
    status: 'confirmed',
    totalPrice: 88,
    notes: '需要给猫咪洗澡',
    serviceName: '宠物洗澡美容套餐',
    merchantName: '爱心宠物美容会所',
    petName: '小黑',
    createdAt: '2024-01-22 11:00:00',
    updatedAt: '2024-01-22 11:30:00'
  }
]

const mockReviews: UserReview[] = [
  {
    id: 1,
    appointmentId: 5,
    serviceId: 1,
    merchantId: 1,
    serviceName: '宠物洗澡美容套餐',
    merchantName: '爱心宠物美容会所',
    rating: 5,
    comment: '服务非常好，狗狗洗得很干净，美容师很专业，态度也很好，下次还会再来！',
    createdAt: '2024-01-10 16:30:00'
  },
  {
    id: 2,
    appointmentId: 6,
    serviceId: 2,
    merchantId: 2,
    serviceName: '宠物健康体检',
    merchantName: '宠物健康医院',
    rating: 4,
    comment: '医生很专业，检查很仔细，就是等待时间有点长，总体来说很满意。',
    createdAt: '2024-01-05 11:20:00'
  },
  {
    id: 3,
    appointmentId: 7,
    serviceId: 3,
    merchantId: 3,
    serviceName: '宠物寄养服务',
    merchantName: '快乐宠物寄养中心',
    rating: 5,
    comment: '寄养环境很好，工作人员很负责，每天都会发照片和视频，非常放心。',
    createdAt: '2023-12-30 14:45:00'
  },
  {
    id: 4,
    appointmentId: 8,
    serviceId: 1,
    merchantId: 1,
    serviceName: '宠物洗澡美容套餐',
    merchantName: '爱心宠物美容会所',
    rating: 3,
    comment: '服务一般，洗得不是很干净，价格有点贵，可能不会再来了。',
    createdAt: '2023-12-25 10:15:00'
  }
]

const completedAppointments = computed(() => {
  const reviewedIds = new Set(reviewList.value.map(r => r.appointmentId))
  return appointments.value.filter(item => item.status === 'completed' && !reviewedIds.has(item.id))
})

const filteredReviews = computed(() => {
  let list = [...reviewList.value]

  if (ratingFilter.value !== null) {
    list = list.filter(item => item.rating === ratingFilter.value)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(item =>
      item.serviceName?.toLowerCase().includes(keyword) ||
      item.merchantName?.toLowerCase().includes(keyword) ||
      item.comment?.toLowerCase().includes(keyword)
    )
  }

  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0])
    const endDate = new Date(dateRange.value[1])
    endDate.setHours(23, 59, 59, 999)
    list = list.filter(item => {
      const reviewDate = new Date(item.createdAt)
      return reviewDate >= startDate && reviewDate <= endDate
    })
  }

  return list
})

const statusTagType = (status: string) => {
  const map: Record<string, string> = {
    'pending': 'warning',
    'confirmed': 'primary',
    'completed': 'success',
    'cancelled': 'info'
  }
  return map[status] || 'info'
}

const statusText = (status: string) => {
  const map: Record<string, string> = {
    'pending': '待确认',
    'confirmed': '已确认',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return map[status] || status
}

const fetchAppointments = async () => {
  loading.value = true
  try {
    const res = await getUserAppointments()
    appointments.value = res.data || res || []
  } catch (error) {
    console.error('获取预约列表失败:', error)
    ElMessage.error('获取预约列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const fetchReviews = async () => {
  try {
    const res = await getUserReviewsList()
    const data = res.data || res
    reviewList.value = data.data || data || []
  } catch (error) {
    console.error('获取评价列表失败:', error)
    ElMessage.error('获取评价列表失败，请稍后重试')
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleReset = () => {
  ratingFilter.value = null
  dateRange.value = null
  searchKeyword.value = ''
  currentPage.value = 1
}

const openReviewDialog = (row: Appointment) => {
  currentAppointment.value = row
  reviewForm.value = {
    rating: 5,
    comment: ''
  }
  reviewDialogVisible.value = true
}

const submitReview = async () => {
  if (!currentAppointment.value) return

  if (!reviewForm.value.comment.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }

  reviewLoading.value = true
  try {
    const data = {
      appointmentId: currentAppointment.value.id,
      merchantId: currentAppointment.value.merchantId,
      serviceId: currentAppointment.value.serviceId,
      rating: reviewForm.value.rating,
      comment: reviewForm.value.comment
    }
    await addReview(data)
    ElMessage.success('评价提交成功')
    reviewDialogVisible.value = false

    // 重新获取评价列表
    await fetchReviews()
  } catch (error) {
    console.error('评价提交失败:', error)
    ElMessage.error('评价提交失败，请稍后重试')
  } finally {
    reviewLoading.value = false
  }
}

const handleViewDetail = (review: UserReview) => {
  currentReview.value = review
  detailDialogVisible.value = true
}

const handleDeleteReview = async (review: UserReview) => {
  try {
    await ElMessageBox.confirm('确定要删除该评价吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteReview(review.id)
    ElMessage.success('评价已删除')
    reviewList.value = reviewList.value.filter(item => item.id !== review.id)
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除评价失败:', error)
      ElMessage.error('删除评价失败，请稍后重试')
    }
  }
}

const handleViewMerchant = (merchantId: number) => {
  router.push(`/user/merchant/${merchantId}`)
  detailDialogVisible.value = false
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchAppointments()
  fetchReviews()
})
</script>

<template>
  <div class="user-reviews">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">我的评价</span>
        </div>
      </template>

      <el-tabs>
        <el-tab-pane label="待评价">
          <el-table
            v-loading="loading"
            :data="completedAppointments"
            style="width: 100%"
            stripe
          >
            <el-table-column prop="id" label="订单编号" width="100" align="center" />
            <el-table-column label="服务名称" min-width="150">
              <template #default="{ row }">
                {{ row.serviceName }}
              </template>
            </el-table-column>
            <el-table-column label="商家名称" min-width="120">
              <template #default="{ row }">
                {{ row.merchantName }}
              </template>
            </el-table-column>
            <el-table-column label="预约时间" width="160" align="center">
              <template #default="{ row }">
                {{ formatDate(row.appointmentTime) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.status)" size="small">
                  {{ statusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  size="small"
                  :icon="Star"
                  @click="openReviewDialog(row)"
                >
                  评价
                </el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无待评价订单" />
            </template>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="已评价">
          <div class="filter-section">
            <el-form inline>
              <el-form-item label="关键词">
                <el-input
                  v-model="searchKeyword"
                  placeholder="搜索服务/商家/评价"
                  clearable
                  style="width: 200px"
                  @keyup.enter="handleSearch"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item label="评分">
                <el-select v-model="ratingFilter" placeholder="全部评分" clearable style="width: 120px">
                  <el-option
                    v-for="item in ratingOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="日期范围">
                <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  style="width: 260px"
                />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table
            v-loading="loading"
            :data="filteredReviews"
            style="width: 100%"
            stripe
          >
            <el-table-column label="服务名称" min-width="150">
              <template #default="{ row }">
                {{ row.serviceName }}
              </template>
            </el-table-column>
            <el-table-column label="商家名称" min-width="120">
              <template #default="{ row }">
                {{ row.merchantName }}
              </template>
            </el-table-column>
            <el-table-column label="评分" width="150" align="center">
              <template #default="{ row }">
                <el-rate v-model="row.rating" disabled text-color="#ff9900" />
              </template>
            </el-table-column>
            <el-table-column label="评价内容" min-width="200">
              <template #default="{ row }">
                <div class="comment-cell">
                  {{ row.comment || '暂无评价内容' }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="评价时间" width="160" align="center">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" plain :icon="View" @click="handleViewDetail(row)">
                  详情
                </el-button>
                <el-button type="danger" size="small" plain :icon="Delete" @click="handleDeleteReview(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无已评价记录" />
            </template>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog
      v-model="reviewDialogVisible"
      title="评价服务"
      width="500px"
    >
      <div v-if="currentAppointment" class="review-form">
        <div class="form-item">
          <span class="label">服务名称：</span>
          <span>{{ currentAppointment.serviceName }}</span>
        </div>
        <div class="form-item">
          <span class="label">商家名称：</span>
          <span>{{ currentAppointment.merchantName }}</span>
        </div>
        <div class="form-item">
          <span class="label">预约时间：</span>
          <span>{{ formatDate(currentAppointment.appointmentTime) }}</span>
        </div>
        <el-divider />
        <el-form label-width="80px">
          <el-form-item label="评分" required>
            <el-rate v-model="reviewForm.rating" show-text :texts="['极差', '失望', '一般', '满意', '非常满意']" />
          </el-form-item>
          <el-form-item label="评价内容" required>
            <el-input
              v-model="reviewForm.comment"
              type="textarea"
              :rows="4"
              placeholder="请输入您的评价..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="reviewLoading" @click="submitReview">
          提交评价
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailDialogVisible"
      title="评价详情"
      width="500px"
    >
      <div v-if="currentReview" class="review-detail">
        <div class="detail-item">
          <span class="detail-label">服务名称：</span>
          <span class="detail-value">{{ currentReview.serviceName }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">商家名称：</span>
          <span class="detail-value link" @click="handleViewMerchant(currentReview.merchantId)">
            {{ currentReview.merchantName }}
          </span>
        </div>
        <div class="detail-item">
          <span class="detail-label">评分：</span>
          <el-rate v-model="currentReview.rating" disabled text-color="#ff9900" />
        </div>
        <div class="detail-item">
          <span class="detail-label">评价内容：</span>
          <div class="detail-content">{{ currentReview.comment || '暂无评价内容' }}</div>
        </div>
        <div class="detail-item">
          <span class="detail-label">评价时间：</span>
          <span class="detail-value">{{ formatDate(currentReview.createdAt) }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-reviews {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.filter-section {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.filter-section :deep(.el-form-item) {
  margin-bottom: 0;
}

.comment-cell {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.5;
}

.review-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-item {
  display: flex;
  align-items: flex-start;
}

.form-item .label {
  width: 80px;
  color: #606266;
  flex-shrink: 0;
}

.review-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
}

.detail-label {
  width: 80px;
  color: #909399;
  flex-shrink: 0;
}

.detail-value {
  color: #303133;
}

.detail-value.link {
  color: #409eff;
  cursor: pointer;
}

.detail-value.link:hover {
  text-decoration: underline;
}

.detail-content {
  flex: 1;
  color: #303133;
  line-height: 1.6;
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
}

@media (max-width: 768px) {
  .user-reviews {
    padding: 10px;
  }

  .filter-section :deep(.el-form-item) {
    width: 100%;
    margin-right: 0;
    margin-bottom: 12px;
  }

  .filter-section :deep(.el-input),
  .filter-section :deep(.el-select),
  .filter-section :deep(.el-date-editor) {
    width: 100% !important;
  }
}
</style>
