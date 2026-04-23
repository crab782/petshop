<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Star, Location, Phone, Service } from '@element-plus/icons-vue'
import {
  getMerchantList,
  addFavorite,
  removeFavorite,
  type MerchantListItem
} from '@/api/user'

const router = useRouter()

const loading = ref(false)
const merchantList = ref<MerchantListItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)

const searchForm = reactive({
  keyword: '',
  rating: null as number | null,
  sortBy: 'default'
})

const sortOptions = [
  { label: '默认排序', value: 'default' },
  { label: '评分从高到低', value: 'rating_desc' },
  { label: '评分从低到高', value: 'rating_asc' },
  { label: '服务数量从多到少', value: 'service_desc' }
]

const ratingOptions = [
  { label: '全部评分', value: null },
  { label: '4.5分以上', value: 4.5 },
  { label: '4分以上', value: 4 },
  { label: '3.5分以上', value: 3.5 },
  { label: '3分以上', value: 3 }
]

const filteredMerchants = computed(() => {
  const list = [...merchantList.value]

  if (searchForm.sortBy === 'rating_desc') {
    list.sort((a, b) => (b.rating || 0) - (a.rating || 0))
  } else if (searchForm.sortBy === 'rating_asc') {
    list.sort((a, b) => (a.rating || 0) - (b.rating || 0))
  } else if (searchForm.sortBy === 'service_desc') {
    list.sort((a, b) => b.serviceCount - a.serviceCount)
  }

  return list
})

const fetchMerchantList = async () => {
  loading.value = true
  try {
    const res = await getMerchantList({
      keyword: searchForm.keyword || undefined,
      rating: searchForm.rating || undefined,
      sortBy: searchForm.sortBy,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    merchantList.value = res.data?.data || []
    total.value = res.data?.total || 0
  } catch (error: unknown) {
    const err = error as Error
    ElMessage.error(err.message || '获取商家列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchMerchantList()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.rating = null
  searchForm.sortBy = 'default'
  currentPage.value = 1
  fetchMerchantList()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchMerchantList()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchMerchantList()
}

const handleViewDetail = (merchantId: number) => {
  router.push(`/user/shop?id=${merchantId}`)
}

const handleToggleFavorite = async (merchant: MerchantListItem, event: Event) => {
  event.stopPropagation()
  try {
    if (merchant.isFavorited) {
      await removeFavorite(merchant.id)
      merchant.isFavorited = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(merchant.id)
      merchant.isFavorited = true
      ElMessage.success('收藏成功')
    }
  } catch (error: unknown) {
    const err = error as Error
    ElMessage.error(err.message || '操作失败')
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

onMounted(() => {
  fetchMerchantList()
})
</script>

<template>
  <div class="merchant-list">
    <h1 class="page-title">商家列表</h1>

    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="商家名称">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入商家名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="评分筛选">
          <el-select
            v-model="searchForm.rating"
            placeholder="全部评分"
            clearable
            style="width: 140px"
          >
            <el-option
              v-for="item in ratingOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="排序方式">
          <el-select
            v-model="searchForm.sortBy"
            placeholder="默认排序"
            style="width: 160px"
          >
            <el-option
              v-for="item in sortOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div v-loading="loading" class="merchant-content">
      <el-empty v-if="filteredMerchants.length === 0 && !loading" description="暂无商家数据" />

      <el-row v-else :gutter="20">
        <el-col
          v-for="merchant in filteredMerchants"
          :key="merchant.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card class="merchant-card" shadow="hover" @click="handleViewDetail(merchant.id)">
            <div class="merchant-header">
              <el-avatar :size="64" class="merchant-logo">
                <img
                  v-if="merchant.logo"
                  :src="merchant.logo"
                  :alt="merchant.name"
                />
                <span v-else class="logo-placeholder">
                  {{ getDefaultLogo(merchant.name) }}
                </span>
              </el-avatar>
              <div class="merchant-basic">
                <h3 class="merchant-name">{{ merchant.name }}</h3>
                <div class="merchant-rating" v-if="merchant.rating">
                  <el-rate
                    :model-value="merchant.rating"
                    disabled
                    show-score
                    score-template="{value}"
                    size="small"
                  />
                </div>
              </div>
              <el-button
                :type="merchant.isFavorited ? 'warning' : 'default'"
                :icon="Star"
                circle
                size="small"
                class="favorite-btn"
                @click="handleToggleFavorite(merchant, $event)"
              />
            </div>

            <div class="merchant-info">
              <div class="info-item" v-if="merchant.address">
                <el-icon><Location /></el-icon>
                <span class="info-value">{{ merchant.address }}</span>
              </div>
              <div class="info-item" v-if="merchant.phone">
                <el-icon><Phone /></el-icon>
                <span class="info-value">{{ merchant.phone }}</span>
              </div>
              <div class="info-item">
                <el-icon><Service /></el-icon>
                <span class="info-value">服务数量：{{ merchant.serviceCount }} 项</span>
              </div>
            </div>

            <div class="card-footer">
              <el-button type="primary" size="small" @click.stop="handleViewDetail(merchant.id)">
                查看详情
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-pagination
        v-if="total > 0"
        class="pagination"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[12, 24, 36, 48]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.merchant-list {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 24px 0;
}

.search-card {
  margin-bottom: 24px;
}

.search-card :deep(.el-form-item) {
  margin-bottom: 12px;
}

.merchant-content {
  min-height: 400px;
}

.merchant-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.merchant-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.merchant-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
  position: relative;
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
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.merchant-rating {
  display: flex;
  align-items: center;
}

.favorite-btn {
  position: absolute;
  top: 0;
  right: 0;
}

.merchant-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.info-item .el-icon {
  flex-shrink: 0;
  color: #909399;
  margin-top: 2px;
}

.info-value {
  flex: 1;
  word-break: break-all;
  line-height: 1.4;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .search-card :deep(.el-form-item) {
    width: 100%;
    margin-right: 0;
  }

  .search-card :deep(.el-input),
  .search-card :deep(.el-select) {
    width: 100% !important;
  }
}
</style>
