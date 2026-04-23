<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Sort } from '@element-plus/icons-vue'
import { getServices, getServicesByKeyword, type Service } from '@/api/user'
import { ElMessage } from 'element-plus'



const router = useRouter()

const searchKeyword = ref('')
const selectedCategory = ref('all')
const priceRange = ref<[number, number]>([0, 1000])
const durationRange = ref<[number, number]>([0, 240])
const ratingFilter = ref(0)
const sortBy = ref('default')
const serviceList = ref<Service[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

const showFilterPanel = ref(false)

const categories = [
  { label: '全部', value: 'all' },
  { label: '宠物美容', value: 'beauty' },
  { label: '宠物寄养', value: 'boarding' },
  { label: '健康体检', value: 'health' },
  { label: '宠物饮食', value: 'food' },
  { label: '宠物训练', value: 'training' }
]

const sortOptions = [
  { label: '默认排序', value: 'default' },
  { label: '价格从低到高', value: 'price_asc' },
  { label: '价格从高到低', value: 'price_desc' },
  { label: '评分从高到低', value: 'rating_desc' },
  { label: '时长从短到长', value: 'duration_asc' },
  { label: '时长从长到短', value: 'duration_desc' }
]

const filteredAndSortedList = computed(() => {
  let list = [...serviceList.value]

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(s =>
      s.name.toLowerCase().includes(keyword) ||
      (s.description && s.description.toLowerCase().includes(keyword)) ||
      (s.merchantName && s.merchantName.toLowerCase().includes(keyword))
    )
  }

  if (selectedCategory.value !== 'all') {
    list = list.filter(s => s.category === selectedCategory.value)
  }

  list = list.filter(s => {
    const price = s.price || 0
    return price >= priceRange.value[0] && price <= priceRange.value[1]
  })

  list = list.filter(s => {
    const duration = s.duration || 60
    return duration >= durationRange.value[0] && duration <= durationRange.value[1]
  })

  if (ratingFilter.value > 0) {
    list = list.filter(s => {
      const rating = (s as Service & { rating?: number }).rating || 4.5
      return rating >= ratingFilter.value
    })
  }

  switch (sortBy.value) {
    case 'price_asc':
      list.sort((a, b) => (a.price || 0) - (b.price || 0))
      break
    case 'price_desc':
      list.sort((a, b) => (b.price || 0) - (a.price || 0))
      break
    case 'rating_desc':
      list.sort((a, b) => {
        const ratingA = (a as Service & { rating?: number }).rating || 4.5
        const ratingB = (b as Service & { rating?: number }).rating || 4.5
        return ratingB - ratingA
      })
      break
    case 'duration_asc':
      list.sort((a, b) => (a.duration || 60) - (b.duration || 60))
      break
    case 'duration_desc':
      list.sort((a, b) => (b.duration || 60) - (a.duration || 60))
      break
    default:
      break
  }

  return list
})

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  total.value = filteredAndSortedList.value.length
  return filteredAndSortedList.value.slice(start, end)
})

const fetchServices = async () => {
  loading.value = true
  try {
    // 使用真实API
    let res
    if (searchKeyword.value) {
      res = await getServicesByKeyword(searchKeyword.value)
    } else {
      const type = selectedCategory.value === 'all' ? undefined : selectedCategory.value
      res = await getServices({ type })
    }
    serviceList.value = (res.data || res || []).map((s: Service) => ({
      ...s,
      rating: (s as Service & { rating?: number }).rating || 4.5 + Math.random() * 0.5
    }))
  } catch {
    ElMessage.error('获取服务列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchServices()
}

const handleTabChange = () => {
  currentPage.value = 1
  fetchServices()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
}

const handleBook = (service: Service) => {
  router.push(`/user/appointments/book?serviceId=${service.id}`)
}

const handleViewMerchant = (merchantId: number) => {
  router.push(`/user/shop/${merchantId}`)
}

const resetFilters = () => {
  searchKeyword.value = ''
  selectedCategory.value = 'all'
  priceRange.value = [0, 1000]
  durationRange.value = [0, 240]
  ratingFilter.value = 0
  sortBy.value = 'default'
  currentPage.value = 1
}

const getServiceImage = (service: Service) => {
  const images: Record<string, string> = {
    beauty: '🛁',
    health: '🏥',
    boarding: '🏠',
    food: '🍖',
    training: '🎓'
  }
  return images[service.category || ''] || '🐾'
}

const formatDuration = (minutes: number) => {
  if (minutes >= 60) {
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
  }
  return `${minutes}分钟`
}

watch([selectedCategory, priceRange, durationRange, ratingFilter, sortBy], () => {
  currentPage.value = 1
})

onMounted(() => {
  fetchServices()
})
</script>

<template>
  <div class="service-list-page">
    <div class="page-header">
      <h1 class="page-title">服务项目</h1>
      <el-button
        :type="showFilterPanel ? 'primary' : 'default'"
        :icon="Search"
        @click="showFilterPanel = !showFilterPanel"
      >
        {{ showFilterPanel ? '收起筛选' : '高级筛选' }}
      </el-button>
    </div>

    <el-tabs v-model="selectedCategory" @tab-change="handleTabChange" class="category-tabs">
      <el-tab-pane
        v-for="cat in categories"
        :key="cat.value"
        :label="cat.label"
        :name="cat.value"
      />
    </el-tabs>

    <el-card v-if="showFilterPanel" class="filter-card">
      <el-form label-width="80px" class="filter-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="关键词">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索服务名称"
                clearable
                @keyup.enter="handleSearch"
              >
                <template #append>
                  <el-button :icon="Search" @click="handleSearch" />
                </template>
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="价格区间">
              <el-slider
                v-model="priceRange"
                range
                :min="0"
                :max="1000"
                :step="10"
                :format-tooltip="(val: number) => `¥${val}`"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="时长区间">
              <el-slider
                v-model="durationRange"
                range
                :min="0"
                :max="240"
                :step="15"
                :format-tooltip="(val: number) => `${val}分钟`"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="最低评分">
              <el-rate
                v-model="ratingFilter"
                :allow-half="true"
                show-score
                :score-template="ratingFilter > 0 ? `${ratingFilter}分以上` : '不限'"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="排序方式">
              <el-select v-model="sortBy" placeholder="请选择排序方式" style="width: 100%">
                <el-option
                  v-for="opt in sortOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label=" ">
              <el-button @click="resetFilters">重置筛选</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <div class="result-info">
      <span>共找到 <strong>{{ total }}</strong> 个服务</span>
      <el-select v-model="sortBy" placeholder="排序" style="width: 150px" size="small">
        <el-option
          v-for="opt in sortOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
    </div>

    <div v-loading="loading" class="services-content">
      <el-empty v-if="paginatedList.length === 0 && !loading" description="暂无服务数据" />

      <template v-else>
        <el-row :gutter="20">
          <el-col
            v-for="service in paginatedList"
            :key="service.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
          >
            <el-card class="service-card" shadow="hover">
              <div class="service-image">
                <div class="image-placeholder">{{ getServiceImage(service) }}</div>
              </div>

              <div class="service-info">
                <h3 class="service-name">{{ service.name }}</h3>
                <p
                  class="merchant-name"
                  @click.stop="handleViewMerchant(service.merchantId)"
                >
                  {{ service.merchantName }}
                </p>

                <div class="service-meta">
                  <span class="service-price">¥{{ service.price }}</span>
                  <span class="service-duration">{{ formatDuration(service.duration || 60) }}</span>
                </div>

                <div class="service-rating">
                  <el-rate
                    :model-value="(service as Service & { rating?: number }).rating || 4.5"
                    disabled
                    show-score
                    score-template="{value}"
                    size="small"
                  />
                </div>

                <el-button
                  type="primary"
                  class="book-button"
                  @click="handleBook(service)"
                >
                  立即预约
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[8, 16, 24, 32]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handlePageChange"
            @size-change="currentPage = 1"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.service-list-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.category-tabs {
  margin-bottom: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  padding: 10px 0;
}

.result-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;
}

.result-info strong {
  color: #409eff;
}

.services-content {
  min-height: 400px;
}

.service-card {
  margin-bottom: 20px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.service-image {
  height: 140px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
}

.service-info {
  padding: 0 4px;
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

.merchant-name {
  font-size: 13px;
  color: #409eff;
  margin: 0 0 12px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

.merchant-name:hover {
  text-decoration: underline;
}

.service-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.service-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.service-duration {
  font-size: 13px;
  color: #606266;
}

.service-rating {
  margin-bottom: 12px;
}

.book-button {
  width: 100%;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .result-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
