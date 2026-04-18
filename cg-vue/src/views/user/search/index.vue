<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchProducts, searchServices, getMerchantInfo, type Product, type Service, type MerchantInfo } from '@/api/user'
import { Search, ShoppingCart, Goods, Shop, Delete, TrendCharts, Clock } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const activeTab = ref('products')
const products = ref<Product[]>([])
const services = ref<Service[]>([])
const merchants = ref<MerchantInfo[]>([])
const loadingProducts = ref(false)
const loadingServices = ref(false)
const loadingMerchants = ref(false)
const hasSearched = ref(false)

const searchHistory = ref<string[]>([])
const hotKeywords = ref<string[]>(['宠物美容', '宠物寄养', '宠物医疗', '宠物训练', '猫粮', '狗粮', '宠物玩具'])

const sortField = ref('default')
const sortOrder = ref('desc')

const priceRange = ref<[number, number] | null>(null)
const minPrice = ref<number | undefined>()
const maxPrice = ref<number | undefined>()

const loadSearchHistory = () => {
  const history = localStorage.getItem('searchHistory')
  if (history) {
    searchHistory.value = JSON.parse(history)
  }
}

const saveSearchHistory = (kw: string) => {
  if (!kw.trim()) return
  const history = searchHistory.value.filter(item => item !== kw)
  history.unshift(kw)
  if (history.length > 10) {
    history.pop()
  }
  searchHistory.value = history
  localStorage.setItem('searchHistory', JSON.stringify(history))
}

const clearSearchHistory = () => {
  searchHistory.value = []
  localStorage.removeItem('searchHistory')
}

const handleHistoryClick = (kw: string) => {
  keyword.value = kw
  handleSearch()
}

const handleHotClick = (kw: string) => {
  keyword.value = kw
  handleSearch()
}

const handleSearch = async () => {
  if (!keyword.value.trim()) return

  hasSearched.value = true
  saveSearchHistory(keyword.value.trim())
  activeTab.value = 'products'

  await Promise.all([
    fetchProducts(),
    fetchServices(),
    fetchMerchants()
  ])
}

const fetchProducts = async () => {
  loadingProducts.value = true
  try {
    const res = await searchProducts(keyword.value)
    products.value = res.data || []
  } catch (error) {
    console.error('搜索商品失败:', error)
    products.value = []
  } finally {
    loadingProducts.value = false
  }
}

const fetchServices = async () => {
  loadingServices.value = true
  try {
    const res = await searchServices(keyword.value)
    services.value = res.data || []
  } catch (error) {
    console.error('搜索服务失败:', error)
    services.value = []
  } finally {
    loadingServices.value = false
  }
}

const fetchMerchants = async () => {
  loadingMerchants.value = true
  try {
    const res = await searchMerchants(keyword.value)
    merchants.value = res.data || []
  } catch (error) {
    console.error('搜索商家失败:', error)
    merchants.value = []
  } finally {
    loadingMerchants.value = false
  }
}

const searchMerchants = async (kw: string) => {
  const { default: request } = await import('@/api/request')
  return request.get('/api/merchants/search', { params: { keyword: kw } })
}

const handleTabChange = async (tab: string) => {
  activeTab.value = tab
}

const sortProducts = (list: Product[]) => {
  if (sortField.value === 'default') return list
  return [...list].sort((a, b) => {
    const aValue = sortField.value === 'price' ? a.price : 0
    const bValue = sortField.value === 'price' ? b.price : 0
    return sortOrder.value === 'asc' ? aValue - bValue : bValue - aValue
  })
}

const sortServices = (list: Service[]) => {
  if (sortField.value === 'default') return list
  return [...list].sort((a, b) => {
    const aValue = sortField.value === 'price' ? a.price : (a.duration || 0)
    const bValue = sortField.value === 'price' ? b.price : (b.duration || 0)
    return sortOrder.value === 'asc' ? aValue - bValue : bValue - aValue
  })
}

const filterByPrice = <T extends { price: number }>(list: T[]) => {
  if (minPrice.value === undefined && maxPrice.value === undefined) return list
  return list.filter(item => {
    if (minPrice.value !== undefined && item.price < minPrice.value) return false
    if (maxPrice.value !== undefined && item.price > maxPrice.value) return false
    return true
  })
}

const filteredProducts = computed(() => {
  return sortProducts(filterByPrice(products.value))
})

const filteredServices = computed(() => {
  return sortServices(filterByPrice(services.value))
})

const goToProductDetail = (product: Product) => {
  router.push(`/user/product/${product.id}`)
}

const goToServiceDetail = (service: Service) => {
  router.push(`/user/service/${service.id}`)
}

const goToMerchantDetail = (merchant: MerchantInfo) => {
  router.push(`/user/shop?id=${merchant.id}`)
}

onMounted(() => {
  loadSearchHistory()
  const queryKeyword = route.query.keyword as string
  if (queryKeyword) {
    keyword.value = queryKeyword
    handleSearch()
  }
})
</script>

<template>
  <div class="search-page">
    <div class="search-header">
      <h1 class="page-title">搜索</h1>
    </div>

    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="请输入关键词搜索商品、服务和商家"
        size="large"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <div class="search-suggestions" v-if="!hasSearched">
      <div class="history-section" v-if="searchHistory.length > 0">
        <div class="section-header">
          <span class="section-title">
            <el-icon><Clock /></el-icon>
            搜索历史
          </span>
          <el-button type="danger" text size="small" @click="clearSearchHistory">
            <el-icon><Delete /></el-icon>
            清空
          </el-button>
        </div>
        <div class="tag-list">
          <el-tag
            v-for="(item, index) in searchHistory"
            :key="index"
            class="history-tag"
            @click="handleHistoryClick(item)"
          >
            {{ item }}
          </el-tag>
        </div>
      </div>

      <div class="hot-section">
        <div class="section-header">
          <span class="section-title">
            <el-icon><TrendCharts /></el-icon>
            热门搜索
          </span>
        </div>
        <div class="tag-list">
          <el-tag
            v-for="(item, index) in hotKeywords"
            :key="index"
            type="warning"
            class="hot-tag"
            @click="handleHotClick(item)"
          >
            {{ item }}
          </el-tag>
        </div>
      </div>
    </div>

    <div class="search-results" v-if="hasSearched">
      <div class="filter-bar">
        <div class="filter-left">
          <span class="filter-label">价格筛选：</span>
          <el-input-number
            v-model="minPrice"
            :min="0"
            :precision="0"
            placeholder="最低价"
            size="small"
            style="width: 100px"
          />
          <span class="price-separator">-</span>
          <el-input-number
            v-model="maxPrice"
            :min="0"
            :precision="0"
            placeholder="最高价"
            size="small"
            style="width: 100px"
          />
        </div>
        <div class="filter-right">
          <span class="filter-label">排序：</span>
          <el-select v-model="sortField" size="small" style="width: 100px">
            <el-option label="默认" value="default" />
            <el-option label="价格" value="price" />
          </el-select>
          <el-select v-model="sortOrder" size="small" style="width: 80px" v-if="sortField !== 'default'">
            <el-option label="升序" value="asc" />
            <el-option label="降序" value="desc" />
          </el-select>
        </div>
      </div>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane name="products">
          <template #label>
            <span class="tab-label">
              <el-icon><ShoppingCart /></el-icon>
              商品 ({{ filteredProducts.length }})
            </span>
          </template>
          <div v-loading="loadingProducts" class="results-content">
            <el-row :gutter="16" v-if="filteredProducts.length > 0">
              <el-col
                v-for="product in filteredProducts"
                :key="product.id"
                :xs="12" :sm="8" :md="6"
              >
                <el-card
                  class="product-card"
                  shadow="hover"
                  @click="goToProductDetail(product)"
                >
                  <div class="product-image">
                    <img v-if="product.image" :src="product.image" :alt="product.name" />
                    <el-icon v-else :size="48" color="#909399"><ShoppingCart /></el-icon>
                  </div>
                  <h3 class="product-name">{{ product.name }}</h3>
                  <p class="product-desc" v-if="product.description">
                    {{ product.description }}
                  </p>
                  <div class="product-footer">
                    <span class="product-price">¥{{ product.price }}</span>
                    <span class="product-stock" v-if="product.stock !== undefined">
                      库存: {{ product.stock }}
                    </span>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-empty v-else description="暂无商品结果" />
          </div>
        </el-tab-pane>

        <el-tab-pane name="services">
          <template #label>
            <span class="tab-label">
              <el-icon><Goods /></el-icon>
              服务 ({{ filteredServices.length }})
            </span>
          </template>
          <div v-loading="loadingServices" class="results-content">
            <el-row :gutter="16" v-if="filteredServices.length > 0">
              <el-col
                v-for="service in filteredServices"
                :key="service.id"
                :xs="12" :sm="8" :md="6"
              >
                <el-card
                  class="service-card"
                  shadow="hover"
                  @click="goToServiceDetail(service)"
                >
                  <div class="service-image">
                    <el-icon :size="48" color="#409eff"><Goods /></el-icon>
                  </div>
                  <h3 class="service-name">{{ service.name }}</h3>
                  <p class="service-merchant" v-if="service.merchantName">
                    {{ service.merchantName }}
                  </p>
                  <div class="service-footer">
                    <span class="service-price">¥{{ service.price }}</span>
                    <span class="service-duration" v-if="service.duration">
                      {{ service.duration }}分钟
                    </span>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-empty v-else description="暂无服务结果" />
          </div>
        </el-tab-pane>

        <el-tab-pane name="merchants">
          <template #label>
            <span class="tab-label">
              <el-icon><Shop /></el-icon>
              商家 ({{ merchants.length }})
            </span>
          </template>
          <div v-loading="loadingMerchants" class="results-content">
            <el-row :gutter="16" v-if="merchants.length > 0">
              <el-col
                v-for="merchant in merchants"
                :key="merchant.id"
                :xs="24" :sm="12" :md="8"
              >
                <el-card
                  class="merchant-card"
                  shadow="hover"
                  @click="goToMerchantDetail(merchant)"
                >
                  <div class="merchant-info">
                    <div class="merchant-logo">
                      <img v-if="merchant.logo" :src="merchant.logo" :alt="merchant.name" />
                      <el-icon v-else :size="40" color="#909399"><Shop /></el-icon>
                    </div>
                    <div class="merchant-detail">
                      <h3 class="merchant-name">{{ merchant.name }}</h3>
                      <p class="merchant-address" v-if="merchant.address">
                        {{ merchant.address }}
                      </p>
                      <div class="merchant-meta">
                        <el-rate
                          :model-value="merchant.rating || 5"
                          disabled
                          size="small"
                        />
                        <span class="merchant-phone" v-if="merchant.phone">
                          {{ merchant.phone }}
                        </span>
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-empty v-else description="暂无商家结果" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <div class="search-empty" v-if="!hasSearched && searchHistory.length === 0">
      <el-empty description="请输入关键词进行搜索">
        <el-button type="primary" @click="handleSearch" :disabled="!keyword.trim()">
          开始搜索
        </el-button>
      </el-empty>
    </div>
  </div>
</template>

<style scoped>
.search-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.search-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.search-bar {
  margin-bottom: 24px;
}

.search-suggestions {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
}

.history-section,
.hot-section {
  margin-bottom: 20px;
}

.hot-section {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.history-tag,
.hot-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.history-tag:hover,
.hot-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-results {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

.filter-left,
.filter-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
}

.price-separator {
  color: #909399;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 4px;
}

.results-content {
  min-height: 200px;
}

.product-card,
.service-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-bottom: 16px;
}

.product-card:hover,
.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.product-image,
.service-image {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-name,
.service-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: #909399;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-footer,
.service-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price,
.service-price {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}

.product-stock {
  font-size: 12px;
  color: #909399;
}

.service-merchant {
  font-size: 12px;
  color: #909399;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.service-duration {
  font-size: 12px;
  color: #909399;
}

.merchant-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-bottom: 16px;
}

.merchant-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.merchant-info {
  display: flex;
  gap: 16px;
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
  min-width: 0;
}

.merchant-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 8px 0;
}

.merchant-address {
  font-size: 12px;
  color: #909399;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.merchant-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.merchant-phone {
  font-size: 12px;
  color: #606266;
}

.search-empty {
  background-color: #fff;
  border-radius: 8px;
  padding: 48px;
  text-align: center;
}

@media (max-width: 768px) {
  .search-page {
    padding: 16px;
  }

  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-left,
  .filter-right {
    width: 100%;
  }
}
</style>
