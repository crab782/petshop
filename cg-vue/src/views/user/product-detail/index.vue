<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Star, StarFilled, User, Phone, Location } from '@element-plus/icons-vue'
import {
  getProductById,
  addToCart,
  createOrder,
  getAddresses,
  getMerchantInfo,
  getProductReviews,
  addProductFavorite,
  removeProductFavorite,
  checkProductFavorite,
  type Product,
  type Review
} from '@/api/user'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const product = ref<Product | null>(null)
const merchant = ref<any>(null)
const quantity = ref(1)
const activeTab = ref('description')
const addresses = ref<any[]>([])
const selectedAddressId = ref<number | null>(null)
const buyDialogVisible = ref(false)
const reviews = ref<Review[]>([])
const isFavorited = ref(false)
const favoriteLoading = ref(false)

const selectedSpec = ref('')
const specs = ref([
  { label: '默认规格', value: 'default' },
  { label: '大号', value: 'large' },
  { label: '小号', value: 'small' }
])

const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0
  const sum = reviews.value.reduce((acc, r) => acc + (r.rating || 0), 0)
  return (sum / reviews.value.length).toFixed(1)
})

const fetchProduct = async () => {
  const id = Number(route.params.id)
  if (isNaN(id)) {
    ElMessage.error('无效的商品ID')
    return
  }
  loading.value = true
  try {
    const res = await getProductById(id)
    product.value = res.data || res
    if (product.value.stock > 0) {
      quantity.value = 1
    }
    if (product.value.merchantId) {
      fetchMerchantInfo(product.value.merchantId)
    }
    fetchProductReviews(id)
    checkFavoriteStatus(id)
  } catch {
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

const fetchMerchantInfo = async (merchantId: number) => {
  try {
    const res = await getMerchantInfo(merchantId)
    merchant.value = res.data || res
  } catch {
    merchant.value = null
  }
}

const fetchProductReviews = async (productId: number) => {
  try {
    const res = await getProductReviews(productId)
    reviews.value = res.data || []
  } catch {
    reviews.value = []
  }
}

const checkFavoriteStatus = async (productId: number) => {
  try {
    const res = await checkProductFavorite(productId)
    isFavorited.value = res.data?.isFavorited || false
  } catch {
    isFavorited.value = false
  }
}

const fetchAddresses = async () => {
  try {
    const res = await getAddresses()
    addresses.value = res.data || []
    if (addresses.value.length > 0) {
      selectedAddressId.value = addresses.value[0].id
    }
  } catch {
    addresses.value = []
  }
}

const handleAddToCart = async () => {
  if (!product.value) return
  try {
    await addToCart({ productId: product.value.id, quantity: quantity.value })
    ElMessage.success('已加入购物车')
  } catch {
    ElMessage.error('加入购物车失败')
  }
}

const handleBuyNow = () => {
  fetchAddresses()
  buyDialogVisible.value = true
}

const handleCreateOrder = async () => {
  if (!product.value || !selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  try {
    await createOrder({
      productId: product.value.id,
      addressId: selectedAddressId.value,
      quantity: quantity.value
    })
    ElMessage.success('订单创建成功')
    buyDialogVisible.value = false
    router.push('/user/orders')
  } catch {
    ElMessage.error('创建订单失败')
  }
}

const handleToggleFavorite = async () => {
  if (!product.value) return
  favoriteLoading.value = true
  try {
    if (isFavorited.value) {
      await removeProductFavorite(product.value.id)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addProductFavorite(product.value.id)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch {
    ElMessage.error('操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

const getStarArray = (rating: number) => {
  return Array.from({ length: 5 }, (_, i) => i < rating)
}

onMounted(() => {
  fetchProduct()
})
</script>

<template>
  <div class="product-detail">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/user/shop' }">宠物商店</el-breadcrumb-item>
      <el-breadcrumb-item>商品详情</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card v-loading="loading" class="product-card">
      <div v-if="product" class="product-content">
        <el-row :gutter="40">
          <el-col :xs="24" :sm="24" :md="10">
            <el-image
              :src="product.image || 'https://placeholder.com/400x400'"
              fit="cover"
              class="product-image"
              :preview-src-list="[product.image || 'https://placeholder.com/400x400']"
            />
          </el-col>
          <el-col :xs="24" :sm="24" :md="14">
            <div class="product-info">
              <div class="product-header">
                <h2 class="product-name">{{ product.name }}</h2>
                <el-button
                  :type="isFavorited ? 'danger' : 'default'"
                  :icon="isFavorited ? StarFilled : Star"
                  :loading="favoriteLoading"
                  @click="handleToggleFavorite"
                >
                  {{ isFavorited ? '已收藏' : '收藏' }}
                </el-button>
              </div>

              <div class="product-price-row">
                <span class="product-price">¥{{ product.price }}</span>
                <span class="product-stock" :class="{ 'low-stock': product.stock < 10 }">
                  库存: {{ product.stock }}
                </span>
              </div>

              <div class="product-rating" v-if="reviews.length > 0">
                <el-rate :model-value="Number(averageRating)" disabled show-score text-color="#ff9900" />
                <span class="review-count">({{ reviews.length }}条评价)</span>
              </div>

              <div class="product-merchant" v-if="merchant">
                <div class="merchant-header">
                  <el-icon><User /></el-icon>
                  <span class="merchant-name">{{ merchant.name || '未知商家' }}</span>
                </div>
                <div class="merchant-info">
                  <div class="info-item">
                    <el-icon><Star /></el-icon>
                    <span>评分: {{ merchant.rating || '暂无' }}</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Location /></el-icon>
                    <span>{{ merchant.address || '暂无地址' }}</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Phone /></el-icon>
                    <span>{{ merchant.phone || '暂无联系方式' }}</span>
                  </div>
                </div>
              </div>

              <p class="product-description">{{ product.description || '暂无描述' }}</p>

              <div class="spec-row">
                <span class="spec-label">规格:</span>
                <el-radio-group v-model="selectedSpec" size="default">
                  <el-radio-button
                    v-for="spec in specs"
                    :key="spec.value"
                    :value="spec.value"
                  >
                    {{ spec.label }}
                  </el-radio-button>
                </el-radio-group>
              </div>

              <div class="quantity-row">
                <span class="quantity-label">数量:</span>
                <el-input-number
                  v-model="quantity"
                  :min="1"
                  :max="product.stock"
                  :disabled="product.stock === 0"
                />
              </div>

              <div class="action-buttons">
                <el-button
                  type="primary"
                  size="large"
                  :disabled="product.stock === 0"
                  @click="handleBuyNow"
                >
                  立即购买
                </el-button>
                <el-button
                  size="large"
                  :disabled="product.stock === 0"
                  @click="handleAddToCart"
                >
                  <el-icon style="margin-right: 4px"><ShoppingCart /></el-icon>
                  加入购物车
                </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-empty v-else description="商品不存在" />
    </el-card>

    <el-card class="detail-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="商品介绍" name="description">
          <div class="description-content">
            <p>{{ product?.description || '暂无商品介绍' }}</p>
          </div>
        </el-tab-pane>
        <el-tab-pane label="评价列表" name="reviews">
          <div v-if="reviews.length > 0" class="reviews-list">
            <div class="rating-summary">
              <div class="rating-score">{{ averageRating }}</div>
              <div class="rating-info">
                <el-rate :model-value="Number(averageRating)" disabled text-color="#ff9900" />
                <span>{{ reviews.length }}条评价</span>
              </div>
            </div>
            <el-divider />
            <div v-for="review in reviews" :key="review.id" class="review-item">
              <div class="review-header">
                <div class="review-user-info">
                  <el-avatar :size="40">{{ review.userName?.charAt(0) || 'U' }}</el-avatar>
                  <div class="user-detail">
                    <span class="review-user">{{ review.userName || '匿名用户' }}</span>
                    <div class="review-rating">
                      <el-icon
                        v-for="(isFilled, index) in getStarArray(review.rating || 0)"
                        :key="index"
                        :color="isFilled ? '#f56c6c' : '#dcdfe6'"
                      >
                        <Star />
                      </el-icon>
                    </div>
                  </div>
                </div>
                <span class="review-time">{{ review.createTime }}</span>
              </div>
              <p class="review-content">{{ review.comment || '暂无评价内容' }}</p>
            </div>
          </div>
          <el-empty v-else description="暂无评价" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="buyDialogVisible" title="确认订单" width="500px">
      <el-form label-width="80px">
        <el-form-item label="商品">
          <span>{{ product?.name }}</span>
        </el-form-item>
        <el-form-item label="规格">
          <span>{{ specs.find(s => s.value === selectedSpec)?.label || '默认规格' }}</span>
        </el-form-item>
        <el-form-item label="购买数量">
          <span>{{ quantity }}</span>
        </el-form-item>
        <el-form-item label="单价">
          <span class="price">¥{{ product?.price }}</span>
        </el-form-item>
        <el-form-item label="总价">
          <span class="total-price">¥{{ ((product?.price || 0) * quantity).toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="收货地址" required>
          <el-select v-model="selectedAddressId" placeholder="请选择收货地址" style="width: 100%">
            <el-option
              v-for="addr in addresses"
              :key="addr.id"
              :label="`${addr.contactName} ${addr.phone} ${addr.province}${addr.city}${addr.district}${addr.detailAddress}`"
              :value="addr.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="buyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateOrder">确认下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.product-card {
  margin-top: 20px;
}

.product-content {
  padding: 20px;
}

.product-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
}

.product-info {
  padding: 10px 0;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.product-name {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  flex: 1;
  margin-right: 16px;
}

.product-price-row {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
}

.product-price {
  color: #f56c6c;
  font-size: 28px;
  font-weight: bold;
}

.product-stock {
  color: #909399;
  font-size: 14px;
}

.product-stock.low-stock {
  color: #e6a23c;
  font-weight: bold;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.review-count {
  color: #909399;
  font-size: 14px;
}

.product-merchant {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.merchant-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.merchant-name {
  font-weight: bold;
  color: #303133;
}

.merchant-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
}

.product-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 24px;
}

.spec-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.spec-label {
  color: #606266;
  font-size: 14px;
}

.quantity-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.quantity-label {
  color: #606266;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 16px;
}

.detail-tabs {
  margin-top: 20px;
}

.description-content {
  padding: 20px;
  color: #606266;
  line-height: 1.8;
}

.reviews-list {
  padding: 10px 0;
}

.rating-summary {
  display: flex;
  align-items: center;
  gap: 16px;
}

.rating-score {
  font-size: 48px;
  font-weight: bold;
  color: #f56c6c;
}

.rating-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #909399;
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

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.review-user {
  font-weight: bold;
  color: #303133;
}

.review-rating {
  display: flex;
  gap: 2px;
}

.review-content {
  color: #606266;
  font-size: 14px;
  margin: 8px 0;
  line-height: 1.6;
}

.review-time {
  color: #909399;
  font-size: 12px;
}

.price {
  color: #606266;
}

.total-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

@media (max-width: 768px) {
  .product-header {
    flex-direction: column;
    gap: 12px;
  }

  .product-name {
    margin-right: 0;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>
