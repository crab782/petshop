<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'
import { getUserReviews, updateReview, deleteReview } from '@/api/user'

interface Review {
  id: number
  appointmentId: number
  merchantId: number
  merchantName: string
  serviceId: number
  serviceName: string
  productId?: number
  productName?: string
  type: 'service' | 'product'
  rating: number
  comment: string
  createTime: string
  replyStatus: '已回复' | '待回复'
  reply?: string
}

const loading = ref(false)
const reviews = ref<Review[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filterType = ref('')
const dateRange = ref<[string, string] | null>(null)

const editDialogVisible = ref(false)
const editForm = ref({
  id: 0,
  rating: 5,
  comment: ''
})

const paginatedReviews = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return reviews.value.slice(start, end)
})

const handlePageChange = (page: number) => {
  currentPage.value = page
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const replyTagType = (status: string) => {
  return status === '已回复' ? 'success' : 'warning'
}

const fetchReviews = async () => {
  loading.value = true
  try {
    const params: any = {}
    if (filterType.value) {
      params.type = filterType.value
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getUserReviews(params)
    reviews.value = (res.data || []).map((item: Review) => ({
      ...item,
      type: item.type || 'service',
      replyStatus: item.reply ? '已回复' : '待回复'
    }))
    total.value = reviews.value.length
  } catch {
    ElMessage.error('获取评价列表失败')
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  currentPage.value = 1
  fetchReviews()
}

const handleReset = () => {
  filterType.value = ''
  dateRange.value = null
  currentPage.value = 1
  fetchReviews()
}

const handleEdit = (row: Review) => {
  editForm.value = {
    id: row.id,
    rating: row.rating,
    comment: row.comment
  }
  editDialogVisible.value = true
}

const handleEditSubmit = async () => {
  try {
    await updateReview(editForm.value.id, {
      rating: editForm.value.rating,
      comment: editForm.value.comment
    })
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    fetchReviews()
  } catch {
    ElMessage.error('修改失败')
  }
}

const handleDelete = async (row: Review) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评价吗？删除后无法恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteReview(row.id)
    ElMessage.success('删除成功')
    fetchReviews()
  } catch {
  }
}

onMounted(() => {
  fetchReviews()
})
</script>

<template>
  <div class="my-reviews">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">我的评价</span>
        </div>
      </template>

      <div class="filter-section">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="8" :md="6">
            <el-select v-model="filterType" placeholder="评价类型" clearable style="width: 100%">
              <el-option label="全部类型" value="" />
              <el-option label="服务评价" value="service" />
              <el-option label="商品评价" value="product" />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-col>
          <el-col :xs="24" :sm="4" :md="4">
            <el-button type="primary" @click="handleFilter">筛选</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <el-table
        v-loading="loading"
        :data="paginatedReviews"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="评价编号" width="100" align="center" />
        <el-table-column label="评价类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 'service' ? 'primary' : 'success'" size="small">
              {{ row.type === 'service' ? '服务' : '商品' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="服务/商品名称" min-width="150">
          <template #default="{ row }">
            {{ row.type === 'service' ? row.serviceName : row.productName }}
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
            <el-tooltip :content="row.comment" placement="top" :disabled="!row.comment || row.comment.length < 30">
              <span class="comment-text">{{ row.comment || '暂无评价内容' }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="180" align="center" />
        <el-table-column label="回复状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="replyTagType(row.replyStatus)" size="small">
              {{ row.replyStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无评价记录" />
        </template>
      </el-table>

      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="editDialogVisible" title="编辑评价" width="500px">
      <el-form label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="editForm.rating" show-text :texts="['极差', '失望', '一般', '满意', '惊喜']" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="editForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入评价内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.my-reviews {
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
}

.filter-section .el-col {
  margin-bottom: 10px;
}

.comment-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
