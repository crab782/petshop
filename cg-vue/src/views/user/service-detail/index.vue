<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, Clock, User, Location, Phone, Calendar, Plus, StarFilled, ChatDotRound } from '@element-plus/icons-vue'
import {
  getServiceById,
  getMerchantInfo,
  getMerchantReviews,
  getUserPets,
  createAppointment,
  addFavorite,
  removeFavorite,
  type Service,
  type MerchantInfo,
  type MerchantReview,
  type Pet
} from '@/api/user'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const service = ref<Service | null>(null)
const merchant = ref<MerchantInfo | null>(null)
const reviews = ref<MerchantReview[]>([])
const pets = ref<Pet[]>([])
const activeTab = ref('description')

const selectedPetId = ref<number | null>(null)
const appointmentTime = ref<string>('')
const remark = ref('')
const submitting = ref(false)
const bookDialogVisible = ref(false)

const isFavorited = ref(false)
const favoriteLoading = ref(false)

const averageRating = computed(() => {
  if (reviews.value.length === 0) return 5
  const sum = reviews.value.reduce((acc, r) => acc + r.rating, 0)
  return (sum / reviews.value.length).toFixed(1)
})

const fetchService = async () => {
  const id = Number(route.params.id)
  if (isNaN(id)) {
    ElMessage.error('无效的服务ID')
    return
  }
  loading.value = true
  try {
    const res = await getServiceById(id)
    service.value = res.data || res
    if (service.value?.merchantId) {
      await fetchMerchant(service.value.merchantId)
      await fetchReviews(service.value.merchantId)
    }
  } catch {
    ElMessage.error('获取服务详情失败')
  } finally {
    loading.value = false
  }
}

const fetchMerchant = async (merchantId: number) => {
  try {
    const res = await getMerchantInfo(merchantId)
    merchant.value = res.data || res
    isFavorited.value = merchant.value?.isFavorited || false
  } catch {
    merchant.value = null
  }
}

const fetchReviews = async (merchantId: number) => {
  try {
    const res = await getMerchantReviews(merchantId)
    reviews.value = res.data || []
  } catch {
    reviews.value = []
  }
}

const fetchPets = async () => {
  try {
    const res = await getUserPets()
    pets.value = res.data || []
    if (pets.value.length > 0) {
      selectedPetId.value = pets.value[0].id
    }
  } catch {
    pets.value = []
  }
}

const handleBook = () => {
  if (pets.value.length === 0) {
    ElMessage.warning('请先添加宠物信息')
    router.push('/user/pets')
    return
  }
  bookDialogVisible.value = true
}

const handleSubmitAppointment = async () => {
  if (!selectedPetId.value) {
    ElMessage.warning('请选择预约宠物')
    return
  }
  if (!appointmentTime.value) {
    ElMessage.warning('请选择预约时间')
    return
  }
  if (!service.value) return

  submitting.value = true
  try {
    await createAppointment({
      serviceId: service.value.id,
      petId: selectedPetId.value,
      appointmentTime: appointmentTime.value,
      remark: remark.value
    })
    ElMessage.success('预约成功')
    bookDialogVisible.value = false
    router.push('/user/book')
  } catch {
    ElMessage.error('预约失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const handleToggleFavorite = async () => {
  if (!merchant.value) return
  favoriteLoading.value = true
  try {
    if (isFavorited.value) {
      await removeFavorite(merchant.value.id)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(merchant.value.id)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch {
    ElMessage.error('操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7
}

const getStarArray = (rating: number) => {
  return Array.from({ length: 5 }, (_, i) => i < rating)
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchService()
  fetchPets()
})
</script>

<template>
  <div class="service-detail">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/user/services' }">服务项目</el-breadcrumb-item>
      <el-breadcrumb-item>服务详情</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card v-loading="loading" class="service-card">
      <div v-if="service" class="service-content">
        <el-row :gutter="40">
          <el-col :xs="24" :sm="24" :md="10">
            <div class="service-image-container">
              <div class="image-placeholder">
                <span class="service-emoji">🐾</span>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="14">
            <div class="service-info">
              <h2 class="service-name">{{ service.name }}</h2>

              <div class="service-rating">
                <el-rate :model-value="Number(averageRating)" disabled show-score size="small" />
                <span class="review-count">({{ reviews.length }}条评价)</span>
              </div>

              <div class="service-price-row">
                <span class="service-price">¥{{ service.price }}</span>
                <span class="service-duration">
                  <el-icon><Clock /></el-icon>
                  {{ service.duration || 60 }}分钟
                </span>
              </div>

              <p class="service-description">{{ service.description || '暂无描述' }}</p>

              <div class="action-buttons">
                <el-button
                  type="primary"
                  size="large"
                  @click="handleBook"
                >
                  <el-icon><Calendar /></el-icon>
                  立即预约
                </el-button>
                <el-button
                  size="large"
                  :type="isFavorited ? 'warning' : 'default'"
                  :loading="favoriteLoading"
                  @click="handleToggleFavorite"
                >
                  <el-icon v-if="isFavorited"><StarFilled /></el-icon>
                  <el-icon v-else><Star /></el-icon>
                  {{ isFavorited ? '已收藏' : '收藏' }}
                </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-empty v-else description="服务不存在" />
    </el-card>

    <el-card class="merchant-card" v-if="merchant">
      <template #header>
        <div class="card-header">
          <span class="card-title">商家信息</span>
          <el-button type="primary" text @click="router.push(`/user/shop?id=${merchant.id}`)">
            查看店铺
          </el-button>
        </div>
      </template>
      <div class="merchant-info">
        <div class="merchant-logo">
          <img v-if="merchant.logo" :src="merchant.logo" :alt="merchant.name" />
          <el-icon v-else :size="40" color="#909399"><User /></el-icon>
        </div>
        <div class="merchant-detail">
          <h3 class="merchant-name">{{ merchant.name }}</h3>
          <div class="merchant-meta">
            <div class="meta-item">
              <el-icon><Star /></el-icon>
              <span>评分: {{ merchant.rating || averageRating }}</span>
            </div>
            <div class="meta-item" v-if="merchant.address">
              <el-icon><Location /></el-icon>
              <span>{{ merchant.address }}</span>
            </div>
            <div class="meta-item" v-if="merchant.phone">
              <el-icon><Phone /></el-icon>
              <span>{{ merchant.phone }}</span>
            </div>
          </div>
          <p class="merchant-desc" v-if="merchant.description">
            {{ merchant.description }}
          </p>
        </div>
      </div>
    </el-card>

    <el-card class="detail-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="服务介绍" name="description">
          <div class="description-content">
            <p>{{ service?.description || '暂无服务介绍' }}</p>
          </div>
        </el-tab-pane>
        <el-tab-pane name="reviews">
          <template #label>
            <span class="tab-label">
              <el-icon><ChatDotRound /></el-icon>
              评价列表 ({{ reviews.length }})
            </span>
          </template>
          <div v-if="reviews.length > 0" class="reviews-list">
            <div v-for="review in reviews" :key="review.id" class="review-item">
              <div class="review-header">
                <div class="review-user-info">
                  <el-avatar :size="40" class="user-avatar">
                    {{ review.userName?.charAt(0) || 'U' }}
                  </el-avatar>
                  <div class="user-detail">
                    <span class="review-user">{{ review.userName }}</span>
                    <div class="review-rating">
                      <el-icon
                        v-for="(isFilled, index) in getStarArray(review.rating)"
                        :key="index"
                        :color="isFilled ? '#f56c6c' : '#dcdfe6'"
                      >
                        <Star />
                      </el-icon>
                    </div>
                  </div>
                </div>
                <span class="review-time">{{ formatDate(review.createTime) }}</span>
              </div>
              <p class="review-content">{{ review.content }}</p>
            </div>
          </div>
          <el-empty v-else description="暂无评价" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog
      v-model="bookDialogVisible"
      title="预约服务"
      width="500px"
      destroy-on-close
    >
      <div class="book-dialog-content" v-if="service">
        <div class="service-summary">
          <div class="summary-item">
            <span class="summary-label">服务名称：</span>
            <span class="summary-value">{{ service.name }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">服务价格：</span>
            <span class="summary-value price">¥{{ service.price }}</span>
          </div>
        </div>

        <el-divider />

        <el-form label-width="100px" class="book-form">
          <el-form-item label="选择宠物" required>
            <el-select
              v-model="selectedPetId"
              placeholder="请选择预约宠物"
              class="full-width"
            >
              <el-option
                v-for="pet in pets"
                :key="pet.id"
                :label="`${pet.name} (${pet.type})`"
                :value="pet.id"
              >
                <div class="pet-option">
                  <span>{{ pet.name }}</span>
                  <span class="pet-type">{{ pet.type }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="预约时间" required>
            <el-date-picker
              v-model="appointmentTime"
              type="datetime"
              placeholder="请选择预约时间"
              :disabled-date="disabledDate"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm:ss"
              class="full-width"
            />
          </el-form-item>

          <el-form-item label="备注信息">
            <el-input
              v-model="remark"
              type="textarea"
              :rows="3"
              placeholder="如有特殊要求，请在此说明..."
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="bookDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submitting"
          @click="handleSubmitAppointment"
        >
          确认预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.service-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.service-card {
  margin-top: 20px;
}

.service-content {
  padding: 20px;
}

.service-image-container {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f5f7fa;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.service-emoji {
  font-size: 120px;
}

.service-info {
  padding: 10px 0;
}

.service-name {
  margin: 0 0 16px 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.service-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.review-count {
  font-size: 14px;
  color: #909399;
}

.service-price-row {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
}

.service-price {
  color: #f56c6c;
  font-size: 28px;
  font-weight: bold;
}

.service-duration {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 14px;
}

.service-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 24px;
}

.action-buttons {
  display: flex;
  gap: 16px;
}

.merchant-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.merchant-info {
  display: flex;
  gap: 20px;
}

.merchant-logo {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.merchant-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.merchant-detail {
  flex: 1;
}

.merchant-name {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 12px 0;
}

.merchant-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.merchant-desc {
  font-size: 14px;
  color: #909399;
  line-height: 1.6;
  margin: 0;
}

.detail-tabs {
  margin-top: 20px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 4px;
}

.description-content {
  padding: 20px;
  color: #606266;
  line-height: 1.8;
}

.reviews-list {
  padding: 10px 0;
}

.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #ebeef5;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.review-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background-color: #409eff;
  color: #fff;
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.review-user {
  font-weight: 500;
  color: #303133;
}

.review-rating {
  display: flex;
  gap: 2px;
}

.review-content {
  color: #606266;
  font-size: 14px;
  margin: 0;
  line-height: 1.6;
  padding-left: 52px;
}

.review-time {
  color: #909399;
  font-size: 12px;
}

.book-dialog-content {
  padding: 10px 0;
}

.service-summary {
  background-color: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.summary-label {
  color: #909399;
}

.summary-value {
  font-weight: 500;
  color: #303133;
}

.summary-value.price {
  color: #f56c6c;
  font-size: 18px;
}

.book-form {
  margin-top: 16px;
}

.full-width {
  width: 100%;
}

.pet-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pet-type {
  color: #909399;
  font-size: 12px;
}

@media (max-width: 768px) {
  .service-image-container {
    height: 250px;
  }

  .service-name {
    font-size: 20px;
  }

  .service-price {
    font-size: 24px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }

  .merchant-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .merchant-meta {
    justify-content: center;
  }

  .review-content {
    padding-left: 0;
  }
}
</style>
