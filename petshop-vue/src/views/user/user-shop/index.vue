<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, Phone, Location, Goods, Service, ChatLineSquare, ShoppingCart } from '@element-plus/icons-vue'
import {
  getMerchantInfo,
  getMerchantServices,
  getMerchantProducts,
  getMerchantReviews,
  addFavorite,
  removeFavorite,
  getFavorites,
  addToCart,
  type Service as ServiceType,
  type MerchantReview,
  type MerchantInfo,
  type Product
} from '@/api/user'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const error = ref(false)
const merchantInfo = ref<MerchantInfo | null>(null)
const services = ref<ServiceType[]>([])
const products = ref<Product[]>([])
const reviews = ref<MerchantReview[]>([])
const isFavorited = ref(false)

const activeTab = ref('services')

const merchantId = computed(() => {
  return Number(route.params.id)
})

const avgRating = computed(() => {
  if (reviews.value.length === 0) return merchantInfo.value?.rating || 0
  const sum = reviews.value.reduce((acc, r) => acc + r.rating, 0)
  return (sum / reviews.value.length).toFixed(1)
})

const fetchMerchantInfo = async () => {
  if (!merchantId.value) {
    ElMessage.error('店铺ID不存在')
    error.value = true
    return
  }

  loading.value = true
  error.value = false
  try {
    const [infoRes, servicesRes, productsRes, reviewsRes, favoritesRes] = await Promise.all([
      getMerchantInfo(merchantId.value),
      getMerchantServices(merchantId.value),
      getMerchantProducts(merchantId.value),
      getMerchantReviews(merchantId.value),
      getFavorites()
    ])

    merchantInfo.value = infoRes as unknown as MerchantInfo
    services.value = (servicesRes as unknown as ServiceType[]) || []
    products.value = (productsRes as unknown as Product[]) || []
    reviews.value = (reviewsRes as unknown as MerchantReview[]) || []

    const favoriteList = (favoritesRes as unknown as { merchantId: number }[]) || []
    isFavorited.value = favoriteList.some(f => f.merchantId === merchantId.value)
  } catch {
    error.value = true
    ElMessage.error('获取店铺信息失败')
  } finally {
    loading.value = false
  }
}

const handleToggleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await removeFavorite(merchantId.value)
      ElMessage.success('已取消收藏')
      isFavorited.value = false
    } else {
      await addFavorite(merchantId.value)
      ElMessage.success('收藏成功')
      isFavorited.value = true
    }
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleContact = () => {
  if (merchantInfo.value?.phone) {
    ElMessage.success(`商家电话：${merchantInfo.value.phone}`)
  }
}

const handleBookService = (service: ServiceType) => {
  router.push(`/user/appointments/book?serviceId=${service.id}&merchantId=${merchantId.value}`)
}

const handleAddToCart = async (product: Product) => {
  try {
    await addToCart({ productId: product.id, quantity: 1 })
    ElMessage.success('已加入购物车')
  } catch {
    ElMessage.error('加入购物车失败')
  }
}

const handleBuyNow = (product: Product) => {
  router.push(`/user/product/detail/${product.id}`)
}

const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}

const formatDate = (date: string) => {
  if (!date) return ''
  const d = new Date(date)
  return isNaN(d.getTime()) ? '' : d.toLocaleDateString('zh-CN')
}

const handleRetry = () => {
  fetchMerchantInfo()
}

onMounted(() => {
  fetchMerchantInfo()
})
</script>

<template>
  <div class="user-shop">
    <div v-loading="loading">
      <el-empty v-if="!loading && error" description="获取店铺信息失败">
        <el-button type="primary" @click="handleRetry">重新加载</el-button>
      </el-empty>

      <el-empty v-else-if="!loading && !merchantInfo" description="店铺不存在" />

      <template v-else-if="merchantInfo">
        <el-card class="shop-header-card">
          <div class="shop-header">
            <div class="shop-avatar">
              <el-avatar
                :size="100"
                :src="merchantInfo.logo || 'https://via.placeholder.com/100'"
              >
                {{ merchantInfo.name?.charAt(0) }}
              </el-avatar>
            </div>
            <div class="shop-info">
              <h1 class="shop-name">{{ merchantInfo.name }}</h1>
              <div class="shop-meta">
                <div class="meta-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ merchantInfo.address || '暂无地址' }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Phone /></el-icon>
                  <span>{{ merchantInfo.phone || '暂无电话' }}</span>
                </div>
              </div>
              <div class="shop-rating">
                <el-rate
                  :model-value="Number(avgRating) || 5"
                  disabled
                  show-score
                  score-template="{value} 分"
                />
                <span class="review-count">({{ reviews.length }} 条评价)</span>
              </div>
              <div v-if="merchantInfo.description" class="shop-desc">
                {{ merchantInfo.description }}
              </div>
            </div>
            <div class="shop-actions">
              <el-button
                :type="isFavorited ? 'warning' : 'primary'"
                :icon="Star"
                @click="handleToggleFavorite"
              >
                {{ isFavorited ? '已收藏' : '收藏店铺' }}
              </el-button>
              <el-button
                type="success"
                :icon="Phone"
                @click="handleContact"
              >
                联系商家
              </el-button>
            </div>
          </div>
        </el-card>

        <el-card class="shop-content-card">
          <el-tabs v-model="activeTab">
            <el-tab-pane name="services">
              <template #label>
                <span class="tab-label">
                  <el-icon><Service /></el-icon>
                  服务列表
                </span>
              </template>

              <div class="content-section">
                <el-empty v-if="services.length === 0" description="暂无服务" />
                <el-row v-else :gutter="20">
                  <el-col
                    v-for="service in services"
                    :key="service.id"
                    :xs="24"
                    :sm="12"
                    :md="8"
                    :lg="6"
                  >
                    <el-card class="service-card" shadow="hover">
                      <div class="service-image">
                        <el-icon :size="48"><Service /></el-icon>
                      </div>
                      <div class="service-info">
                        <h3 class="service-name">{{ service.name }}</h3>
                        <p class="service-desc">{{ service.description || '暂无描述' }}</p>
                        <div class="service-meta">
                          <span class="service-price">{{ formatPrice(service.price) }}</span>
                          <span class="service-duration">{{ service.duration || 60 }}分钟</span>
                        </div>
                        <el-button
                          type="primary"
                          size="small"
                          class="book-btn"
                          @click="handleBookService(service)"
                        >
                          立即预约
                        </el-button>
                      </div>
                    </el-card>
                  </el-col>
                </el-row>
              </div>
            </el-tab-pane>

            <el-tab-pane name="products">
              <template #label>
                <span class="tab-label">
                  <el-icon><Goods /></el-icon>
                  商品列表
                </span>
              </template>

              <div class="content-section">
                <el-empty v-if="products.length === 0" description="暂无商品" />
                <el-row v-else :gutter="20">
                  <el-col
                    v-for="product in products"
                    :key="product.id"
                    :xs="24"
                    :sm="12"
                    :md="8"
                    :lg="6"
                  >
                    <el-card class="product-card" shadow="hover">
                      <el-image
                        :src="product.image || 'https://via.placeholder.com/200x200'"
                        fit="cover"
                        class="product-image"
                      />
                      <div class="product-info">
                        <h3 class="product-name">{{ product.name }}</h3>
                        <p class="product-desc">{{ product.description || '暂无描述' }}</p>
                        <div class="product-meta">
                          <span class="product-price">{{ formatPrice(product.price) }}</span>
                          <span class="product-stock">库存: {{ product.stock }}</span>
                        </div>
                        <div class="product-actions">
                          <el-button
                            type="warning"
                            size="small"
                            :icon="ShoppingCart"
                            :disabled="product.stock === 0"
                            @click="handleAddToCart(product)"
                          >
                            加入购物车
                          </el-button>
                          <el-button
                            type="primary"
                            size="small"
                            :disabled="product.stock === 0"
                            @click="handleBuyNow(product)"
                          >
                            {{ product.stock === 0 ? '缺货' : '立即购买' }}
                          </el-button>
                        </div>
                      </div>
                    </el-card>
                  </el-col>
                </el-row>
              </div>
            </el-tab-pane>

            <el-tab-pane name="reviews">
              <template #label>
                <span class="tab-label">
                  <el-icon><ChatLineSquare /></el-icon>
                  用户评价
                </span>
              </template>

              <div class="content-section">
                <div class="rating-summary">
                  <div class="rating-score">{{ avgRating }}</div>
                  <div class="rating-info">
                    <el-rate :model-value="Number(avgRating)" disabled />
                    <span class="total-reviews">{{ reviews.length }} 条评价</span>
                  </div>
                </div>

                <el-empty v-if="reviews.length === 0" description="暂无评价" />
                <div v-else class="review-list">
                  <div
                    v-for="review in reviews"
                    :key="review.id"
                    class="review-item"
                  >
                    <div class="review-header">
                      <el-avatar :size="40" class="review-avatar">
                        {{ review.userName?.charAt(0) }}
                      </el-avatar>
                      <div class="review-user-info">
                        <span class="review-username">{{ review.userName }}</span>
                        <el-rate
                          :model-value="review.rating"
                          disabled
                          size="small"
                        />
                      </div>
                      <span class="review-time">{{ formatDate(review.createTime) }}</span>
                    </div>
                    <div class="review-content">{{ review.content }}</div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </template>
    </div>
  </div>
</template>

<style scoped>
.user-shop {
  max-width: 1200px;
  margin: 0 auto;
}

.shop-header-card {
  margin-bottom: 20px;
}

.shop-header {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  flex-wrap: wrap;
}

.shop-avatar {
  flex-shrink: 0;
}

.shop-info {
  flex: 1;
  min-width: 200px;
}

.shop-name {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 12px 0;
}

.shop-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.shop-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.review-count {
  color: #909399;
  font-size: 14px;
}

.shop-desc {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.shop-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex-shrink: 0;
}

.shop-content-card {
  margin-bottom: 20px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 4px;
}

.content-section {
  padding: 20px 0;
  min-height: 300px;
}

.service-card,
.product-card {
  margin-bottom: 20px;
  transition: transform 0.2s;
}

.service-card:hover,
.product-card:hover {
  transform: translateY(-4px);
}

.service-image {
  height: 120px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 12px;
}

.service-info,
.product-info {
  padding: 0 4px;
}

.service-name,
.product-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.service-desc,
.product-desc {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.service-meta,
.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.service-price,
.product-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.service-duration,
.product-stock {
  font-size: 13px;
  color: #909399;
}

.book-btn {
  width: 100%;
}

.product-actions {
  display: flex;
  gap: 8px;
}

.product-actions .el-button {
  flex: 1;
}

.product-image {
  width: 100%;
  height: 140px;
  border-radius: 8px;
}

.rating-summary {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.rating-score {
  font-size: 48px;
  font-weight: bold;
  color: #ff9900;
}

.rating-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.total-reviews {
  color: #909399;
  font-size: 14px;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.review-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.review-user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.review-username {
  font-weight: 500;
  color: #303133;
}

.review-time {
  color: #909399;
  font-size: 13px;
}

.review-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .shop-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .shop-meta {
    align-items: center;
  }

  .shop-actions {
    flex-direction: row;
    width: 100%;
    justify-content: center;
  }

  .rating-summary {
    flex-direction: column;
    text-align: center;
  }

  .product-actions {
    flex-direction: column;
  }
}
</style>
