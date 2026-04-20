<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReviewsForAudit, approveReview, markReviewViolation, deleteReview, type Review } from '@/api/admin'
import { Search, Check, Close, Delete } from '@element-plus/icons-vue'

defineOptions({
  name: 'ReviewAudit'
})

const loading = ref(false)
const reviewList = ref<Review[]>([])
const searchQuery = ref('')
const statusFilter = ref<string>('all')

const violationDialogVisible = ref(false)
const selectedReview = ref<Review | null>(null)
const violationReason = ref('')
const violationRemark = ref('')

const violationReasons = [
  '含有敏感词汇',
  '存在广告内容',
  '恶意评价',
  '内容不实',
  '涉及隐私信息',
  '其他违规原因'
]

const statusMap: Record<string, { label: string; type: string }> = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '通过', type: 'success' },
  violation: { label: '违规', type: 'danger' }
}

const filteredList = computed(() => {
  let list = reviewList.value

  if (statusFilter.value !== 'all') {
    list = list.filter(item => item.status === statusFilter.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    list = list.filter(item =>
      item.serviceName.toLowerCase().includes(query) ||
      item.merchantName.toLowerCase().includes(query) ||
      item.userName.toLowerCase().includes(query) ||
      item.comment.toLowerCase().includes(query)
    )
  }

  return list
})

const loadReviews = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/admin/reviews/pending')
    if (!response.ok) throw new Error('加载评价列表失败')
    const data = await response.json()
    reviewList.value = data.map((review: any) => ({
      id: review.id,
      userName: review.user?.username || '',
      serviceName: review.service?.name || '',
      merchantName: review.merchant?.name || '',
      rating: review.rating,
      comment: review.comment,
      status: review.status || 'pending'
    }))
  } catch (error) {
    ElMessage.error('加载评价列表失败')
    console.error('Error loading reviews for audit:', error)
  } finally {
    loading.value = false
  }
}

const handleStatusFilter = (status: string) => {
  statusFilter.value = status
}

const handleApprove = async (row: Review) => {
  try {
    await ElMessageBox.confirm('确认通过该评价?', '审核确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    })
    const response = await fetch(`/api/admin/reviews/${row.id}/audit`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ status: 'approved' })
    })
    if (!response.ok) throw new Error('操作失败')
    ElMessage.success('评价已通过')
    loadReviews()
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
      console.error('Error approving review:', err)
    }
  }
}

const handleViolation = (row: Review) => {
  selectedReview.value = row
  violationReason.value = ''
  violationRemark.value = ''
  violationDialogVisible.value = true
}

const confirmViolation = async () => {
  if (!violationReason.value) {
    ElMessage.warning('请选择违规原因')
    return
  }
  try {
    const response = await fetch(`/api/admin/reviews/${selectedReview.value!.id}/audit`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ 
        status: 'violation',
        reason: violationReason.value,
        remark: violationRemark.value 
      })
    })
    if (!response.ok) throw new Error('操作失败')
    ElMessage.success('已标记为违规')
    loadReviews()
    violationDialogVisible.value = false
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('Error marking review as violation:', error)
  }
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

onMounted(() => {
  loadReviews()
})
</script>

<template>
  <div class="review-audit-container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>评价审核</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="filter-card">
      <div class="filter-row">
        <el-input
          v-model="searchQuery"
          placeholder="搜索关键词"
          clearable
          style="width: 280px"
          :prefix-icon="Search"
        />
        <div class="status-tabs">
          <el-radio-group v-model="statusFilter" @change="handleStatusFilter">
            <el-radio-button value="all">全部</el-radio-button>
            <el-radio-button value="pending">待审核</el-radio-button>
            <el-radio-button value="approved">通过</el-radio-button>
            <el-radio-button value="violation">违规</el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </el-card>

    <el-card shadow="hover" class="table-card">
      <el-table
        :data="filteredList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="评价ID" width="80" />
        <el-table-column prop="userName" label="用户名称" width="120" />
        <el-table-column prop="serviceName" label="服务/商品" min-width="150" />
        <el-table-column prop="merchantName" label="商家名称" min-width="150" />
        <el-table-column label="评分" width="120">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'" size="small">
              {{ statusMap[row.status]?.label || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              size="small"
              :icon="Check"
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              size="small"
              :icon="Close"
              @click="handleViolation(row)"
            >
              违规
            </el-button>
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="violationDialogVisible"
      title="违规处理"
      width="500px"
    >
      <el-form label-position="top">
        <el-form-item label="选择违规原因">
          <el-select v-model="violationReason" placeholder="请选择违规原因" style="width: 100%">
            <el-option
              v-for="reason in violationReasons"
              :key="reason"
              :label="reason"
              :value="reason"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="添加备注">
          <el-input
            v-model="violationRemark"
            type="textarea"
            :rows="4"
            placeholder="请输入备注信息（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="violationDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmViolation">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.review-audit-container {
  padding: 0;
}

.filter-card {
  margin-top: 20px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.status-tabs {
  flex: 1;
}

.table-card {
  margin-top: 20px;
}
</style>
