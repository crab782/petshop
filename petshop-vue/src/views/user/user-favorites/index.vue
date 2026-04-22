<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFavorites, removeFavorite, type FavoriteMerchant, getServiceFavorites, removeServiceFavorite, type Service } from '@/api/user'

interface FavoriteService {
  id: number
  serviceId: number
  serviceName: string
  serviceImage: string
  servicePrice: number
  serviceDuration: number
  merchantId: number
  merchantName: string
  createdAt: string
}

const router = useRouter()

const activeTab = ref<'merchant' | 'service'>('merchant')
const merchantFavorites = ref<FavoriteMerchant[]>([])
const serviceFavorites = ref<FavoriteService[]>([])
const loading = ref(false)



const currentFavorites = computed(() => {
  return activeTab.value === 'merchant' ? merchantFavorites.value : serviceFavorites.value
})

const fetchMerchantFavorites = async () => {
  try {
    const res = await getFavorites()
    merchantFavorites.value = res.data || res || []
  } catch (error) {
    console.error('获取商家收藏列表失败:', error)
    ElMessage.error('获取商家收藏列表失败，请稍后重试')
  }
}

const fetchServiceFavorites = async () => {
  try {
    const res = await getServiceFavorites()
    serviceFavorites.value = res.data || res || []
  } catch (error) {
    console.error('获取服务收藏列表失败:', error)
    ElMessage.error('获取服务收藏列表失败，请稍后重试')
  }
}

const fetchAllFavorites = async () => {
  loading.value = true
  try {
    await Promise.all([fetchMerchantFavorites(), fetchServiceFavorites()])
  } finally {
    loading.value = false
  }
}

const handleViewMerchantDetail = (merchantId: number) => {
  router.push(`/user/merchant/${merchantId}`)
}

const handleViewServiceDetail = (serviceId: number) => {
  router.push(`/user/service/${serviceId}`)
}

const handleRemoveMerchantFavorite = async (merchant: FavoriteMerchant) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消收藏商家"${merchant.merchantName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await removeFavorite(merchant.merchantId)
    ElMessage.success('已取消收藏')
    fetchMerchantFavorites()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败，请稍后重试')
    }
  }
}

const handleRemoveServiceFavorite = async (service: FavoriteService) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消收藏服务"${service.serviceName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await removeServiceFavorite(service.serviceId)
    ElMessage.success('已取消收藏')
    fetchServiceFavorites()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败，请稍后重试')
    }
  }
}

const getDefaultLogo = (name: string) => {
  const emojis: Record<string, string> = {
    '美容': '💇',
    '宠物': '🐾',
    '医院': '🏥',
    '商店': '🏪',
    '训练': '🎓',
    '寄养': '🏠'
  }
  for (const key of Object.keys(emojis)) {
    if (name.includes(key)) {
      return emojis[key]
    }
  }
  return '🏪'
}

const handleTabChange = () => {
}

onMounted(() => {
  fetchAllFavorites()
})
</script>

<template>
  <div class="favorites-page">
    <h1 class="page-title">我的收藏</h1>

    <el-tabs v-model="activeTab" class="favorites-tabs" @tab-change="handleTabChange">
      <el-tab-pane label="商家收藏" name="merchant">
        <div v-loading="loading" class="favorites-content">
          <el-empty
            v-if="merchantFavorites.length === 0 && !loading"
            description="暂无收藏商家"
          />

          <el-row v-else :gutter="20">
            <el-col
              v-for="item in merchantFavorites"
              :key="item.id"
              :xs="24"
              :sm="12"
              :md="8"
              :lg="6"
            >
              <el-card class="favorite-card" shadow="hover">
                <div class="merchant-header">
                  <el-avatar :size="60" class="merchant-logo">
                    <img
                      v-if="item.merchantLogo"
                      :src="item.merchantLogo"
                      :alt="item.merchantName"
                    />
                    <span v-else class="logo-placeholder">
                      {{ getDefaultLogo(item.merchantName) }}
                    </span>
                  </el-avatar>
                  <div class="merchant-basic">
                    <h3 class="merchant-name">{{ item.merchantName }}</h3>
                    <p class="merchant-add-time">
                      收藏于 {{ item.createdAt }}
                    </p>
                  </div>
                </div>

                <div class="merchant-info">
                  <div class="info-item">
                    <span class="info-label">地址：</span>
                    <span class="info-value">{{ item.merchantAddress || '暂无' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">电话：</span>
                    <span class="info-value">{{ item.merchantPhone || '暂无' }}</span>
                  </div>
                </div>

                <div class="card-actions">
                  <el-button
                    type="primary"
                    @click="handleViewMerchantDetail(item.merchantId)"
                  >
                    查看详情
                  </el-button>
                  <el-button
                    type="danger"
                    plain
                    @click="handleRemoveMerchantFavorite(item)"
                  >
                    取消收藏
                  </el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <el-tab-pane label="服务收藏" name="service">
        <div v-loading="loading" class="favorites-content">
          <el-empty
            v-if="serviceFavorites.length === 0 && !loading"
            description="暂无收藏服务"
          />

          <el-row v-else :gutter="20">
            <el-col
              v-for="item in serviceFavorites"
              :key="item.id"
              :xs="24"
              :sm="12"
              :md="8"
              :lg="6"
            >
              <el-card class="favorite-card service-card" shadow="hover">
                <div class="service-image">
                  <el-image
                    v-if="item.serviceImage"
                    :src="item.serviceImage"
                    fit="cover"
                    class="service-img"
                  >
                    <template #error>
                      <div class="image-placeholder">
                        <span>🐾</span>
                      </div>
                    </template>
                  </el-image>
                  <div v-else class="image-placeholder">
                    <span>🐾</span>
                  </div>
                </div>

                <div class="service-info">
                  <h3 class="service-name">{{ item.serviceName }}</h3>
                  <p class="service-merchant">{{ item.merchantName }}</p>
                  <div class="service-meta">
                    <span class="service-price">¥{{ item.servicePrice }}</span>
                    <span class="service-duration">{{ item.serviceDuration || 60 }}分钟</span>
                  </div>
                  <p class="service-add-time">收藏于 {{ item.createdAt }}</p>
                </div>

                <div class="card-actions">
                  <el-button
                    type="primary"
                    @click="handleViewServiceDetail(item.serviceId)"
                  >
                    查看详情
                  </el-button>
                  <el-button
                    type="danger"
                    plain
                    @click="handleRemoveServiceFavorite(item)"
                  >
                    取消收藏
                  </el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.favorites-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 24px 0;
}

.favorites-tabs {
  margin-bottom: 20px;
}

.favorites-content {
  min-height: 400px;
}

.favorite-card {
  margin-bottom: 20px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.favorite-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.merchant-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.merchant-logo {
  flex-shrink: 0;
  background-color: #f5f7fa;
  font-size: 28px;
}

.logo-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.merchant-basic {
  flex: 1;
  min-width: 0;
}

.merchant-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.merchant-add-time {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

.merchant-info {
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 13px;
  color: #909399;
  flex-shrink: 0;
}

.info-value {
  font-size: 13px;
  color: #606266;
  word-break: break-all;
}

.card-actions {
  display: flex;
  gap: 12px;
}

.card-actions .el-button {
  flex: 1;
}

.service-card {
  padding: 0;
}

.service-image {
  width: 100%;
  height: 150px;
  overflow: hidden;
}

.service-img {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  font-size: 48px;
}

.service-info {
  padding: 16px;
}

.service-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.service-merchant {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px 0;
}

.service-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.service-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.service-duration {
  font-size: 13px;
  color: #909399;
}

.service-add-time {
  font-size: 12px;
  color: #c0c4cc;
  margin: 0;
}

.service-card .card-actions {
  padding: 0 16px 16px;
}
</style>
