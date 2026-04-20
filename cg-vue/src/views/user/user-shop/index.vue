<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, Phone, Location, ChatDotRound, Goods, Service, ChatLineSquare } from '@element-plus/icons-vue'
import {
  getMerchantInfo,
  getMerchantServices,
  getMerchantReviews,
  addFavorite,
  removeFavorite,
  getFavorites,
  getProducts,
  type Service as ServiceType,
  type MerchantReview,
  type MerchantInfo,
  type Product
} from '@/api/user'

// 硬编码测试数据 - 仅在开发环境使用
const mockMerchant: MerchantInfo = {
  id: 1,
  name: '爱心宠物会所',
  logo: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20shop%20logo%2C%20professional%20design&image_size=square',
  phone: '13800138000',
  address: '北京市朝阳区建国路88号',
  description: '专业的宠物服务机构，提供宠物美容、寄养、训练等全方位服务，拥有专业的团队和设施，为您的宠物提供最优质的护理。',
  rating: 4.8
}

const mockServices: ServiceType[] = [
  {
    id: 1,
    name: '宠物洗澡美容套餐',
    description: '包含洗澡、剪毛、修指甲等全套美容服务',
    price: 88,
    duration: 90,
    merchantId: 1,
    merchantName: '爱心宠物会所',
    category: 'beauty'
  },
  {
    id: 2,
    name: '宠物寄养服务',
    description: '提供舒适的寄养环境，专业人员照顾',
    price: 150,
    duration: 1440,
    merchantId: 1,
    merchantName: '爱心宠物会所',
    category: 'boarding'
  },
  {
    id: 3,
    name: '宠物spa护理',
    description: '深层清洁、精油按摩、毛发护理等高端服务',
    price: 288,
    duration: 120,
    merchantId: 1,
    merchantName: '爱心宠物会所',
    category: 'beauty'
  }
]

const mockProducts: Product[] = [
  {
    id: 1,
    name: '宠物天然粮',
    description: '天然成分，营养均衡，适合各种宠物',
    price: 128,
    stock: 50,
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20food%20package%2C%20professional%20product%20photography&image_size=square',
    merchantId: 1
  },
  {
    id: 2,
    name: '宠物玩具套装',
    description: '包含多种玩具，适合不同年龄段的宠物',
    price: 88,
    stock: 30,
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20toys%20set%2C%20professional%20product%20photography&image_size=square',
    merchantId: 1
  },
  {
    id: 3,
    name: '宠物牵引绳',
    description: '舒适耐用，适合各种体型的宠物',
    price: 45,
    stock: 40,
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20leash%2C%20professional%20product%20photography&image_size=square',
    merchantId: 1
  }
]

const mockReviews: MerchantReview[] = [
  {
    id: 1,
    userId: 1,
    userName: '张三',
    merchantId: 1,
    rating: 5,
    content: '服务非常好，宠物美容后非常漂亮，店员也很专业，环境干净整洁，下次还会再来！',
    createTime: '2024-01-15 10:00:00'
  },
  {
    id: 2,
    userId: 2,
    userName: '李四',
    merchantId: 1,
    rating: 4,
    content: '寄养服务不错，宠物回来后状态很好，就是价格稍微贵了一点。',
    createTime: '2024-01-10 14:30:00'
  },
  {
    id: 3,
    userId: 3,
    userName: '王五',
    merchantId: 1,
    rating: 5,
    content: 'SPA服务非常棒，宠物很享受，店员态度也很好，强烈推荐！',
    createTime: '2024-01-05 09:00:00'
  }
]

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const merchantInfo = ref<MerchantInfo | null>(null)
const services = ref<ServiceType[]>([])
const products = ref<Product[]>([])
const reviews = ref<MerchantReview[]>([])
const isFavorited = ref(false)

const activeTab = ref('services')

const merchantId = computed(() => {
  return Number(route.params.id) || Number(route.query.merchantId)
})

const avgRating = computed(() => {
  if (reviews.value.length === 0) return 0
  const sum = reviews.value.reduce((acc, r) => acc + r.rating, 0)
  return (sum / reviews.value.length).toFixed(1)
})

const fetchMerchantInfo = async () => {
  if (!merchantId.value) {
    ElMessage.error('店铺ID不存在')
    return
  }

  loading.value = true
  try {
    // 在开发环境下使用硬编码测试数据
    if (import.meta.env.DEV) {
      // 模拟API延迟
      await new Promise(resolve => setTimeout(resolve, 300))

      merchantInfo.value = mockMerchant
      services.value = mockServices
      products.value = mockProducts
      reviews.value = mockReviews
      isFavorited.value = false
    } else {
      // 在生产环境下使用真实API
      const [infoRes, servicesRes, productsRes, reviewsRes, favoritesRes] = await Promise.all([
        getMerchantInfo(merchantId.value),
        getMerchantServices(merchantId.value),
        getProducts({}),
        getMerchantReviews(merchantId.value),
        getFavorites()
      ])

      merchantInfo.value = infoRes.data || infoRes || null
      services.value = servicesRes.data || servicesRes || []
      const productsData = productsRes.data || productsRes || []
      products.value = productsData.filter((p: Product) => p.merchantId === merchantId.value)
      reviews.value = reviewsRes.data || reviewsRes || []

      const favoriteList = favoritesRes.data || favoritesRes || []
      isFavorited.value = favoriteList.some((f: { merchantId: number }) => f.merchantId === merchantId.value)
    }
  } catch {
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

const handleViewProduct = (product: Product) => {
  router.push(`/user/products/${product.id}`)
}

const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchMerchantInfo()
})
</script>

<template>
  <div class="user-shop">
    <div v-loading="loading">
      <el-empty v-if="!loading && !merchantInfo" description="店铺不存在" />

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
                        <el-button
                          type="primary"
                          size="small"
                          class="view-btn"
                          :disabled="product.stock === 0"
                          @click="handleViewProduct(product)"
                        >
                          {{ product.stock === 0 ? '缺货' : '查看详情' }}
                        </el-button>
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

.book-btn,
.view-btn {
  width: 100%;
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
}
</style>
