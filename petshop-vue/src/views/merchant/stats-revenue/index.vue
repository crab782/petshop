<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElCard, ElButton, ElButtonGroup, ElDatePicker, ElTable, ElTableColumn, ElMessage, ElRow, ElCol, ElProgress, ElTag, ElEmpty } from 'element-plus'
import { Download, TrendCharts, Money, ShoppingCart, Goods, ArrowUp, ArrowDown, Top, Star } from '@element-plus/icons-vue'
import { getMerchantRevenueStats, exportRevenueStats, type RevenueStats, type RevenueQuery, type RevenueOrderItem, type TopServiceItem, type TopProductItem } from '@/api/merchant'
import { useAsync } from '@/composables'
import dayjs from 'dayjs'

type TimeType = 'today' | 'week' | 'month' | 'year' | 'custom'

const timeType = ref<TimeType>('month')
const customDateRange = ref<[Date, Date] | null>(null)
const exportLoading = ref(false)

const timeButtons = [
  { label: '今日', value: 'today' },
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '本年', value: 'year' },
  { label: '自定义', value: 'custom' }
]

const query = computed<RevenueQuery>(() => {
  if (timeType.value === 'custom' && customDateRange.value) {
    return {
      type: 'custom',
      startDate: dayjs(customDateRange.value[0]).format('YYYY-MM-DD'),
      endDate: dayjs(customDateRange.value[1]).format('YYYY-MM-DD')
    }
  }
  return { type: timeType.value }
})

const { data: statsData, loading, error, execute: fetchStats } = useAsync<RevenueStats>(
  () => getMerchantRevenueStats(query.value)
)

watch(error, (newError) => {
  if (newError) {
    ElMessage.error('获取营业额统计数据失败')
  }
})

const handleTimeTypeChange = (val: TimeType) => {
  timeType.value = val
  if (val !== 'custom') {
    fetchStats()
  }
}

const handleCustomDateChange = () => {
  if (timeType.value === 'custom' && customDateRange.value) {
    fetchStats()
  }
}

const trendItems = computed(() => {
  if (!statsData.value?.orderList) return []
  return statsData.value.orderList.slice(-7).map((item: RevenueOrderItem) => ({
    label: dayjs(item.date).format('MM-DD'),
    serviceValue: item.serviceAmount,
    productValue: item.productAmount,
    totalValue: item.totalAmount
  }))
})

const maxTrendValue = computed(() => {
  if (!trendItems.value.length) return 1
  return Math.max(...trendItems.value.map(item => item.totalValue), 1)
})

const compositionData = computed(() => {
  if (!statsData.value) return []
  const data = []
  if (statsData.value.serviceRevenue > 0) {
    data.push({
      name: '服务收入',
      value: statsData.value.serviceRevenue,
      color: '#409eff'
    })
  }
  if (statsData.value.productRevenue > 0) {
    data.push({
      name: '商品收入',
      value: statsData.value.productRevenue,
      color: '#67c23a'
    })
  }
  return data
})

const compositionPercentages = computed(() => {
  if (!statsData.value || statsData.value.totalRevenue === 0) return { service: 0, product: 0 }
  return {
    service: Math.round((statsData.value.serviceRevenue / statsData.value.totalRevenue) * 100),
    product: Math.round((statsData.value.productRevenue / statsData.value.totalRevenue) * 100)
  }
})

const growthRateDisplay = computed(() => {
  if (!statsData.value) return { value: 0, isPositive: true }
  const rate = statsData.value.growthRate || 0
  return {
    value: Math.abs(rate),
    isPositive: rate >= 0
  }
})

const handleExport = async () => {
  if (!statsData.value?.orderList?.length) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  exportLoading.value = true
  try {
    const blob = await exportRevenueStats(query.value)
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.download = `营收统计_${dayjs().format('YYYY-MM-DD')}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败，请稍后重试')
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<template>
  <div class="stats-revenue">
    <el-card shadow="hover" class="title-card">
      <template #header>
        <div class="card-header">
          <span class="page-title">营业额统计</span>
          <el-button type="success" :icon="Download" :loading="exportLoading" @click="handleExport">
            导出Excel
          </el-button>
        </div>
      </template>
    </el-card>

    <el-card shadow="hover" class="filter-card">
      <div class="filter-row">
        <el-button-group>
          <el-button
            v-for="btn in timeButtons"
            :key="btn.value"
            :type="timeType === btn.value ? 'primary' : 'default'"
            @click="handleTimeTypeChange(btn.value as TimeType)"
          >
            {{ btn.label }}
          </el-button>
        </el-button-group>
        <el-date-picker
          v-if="timeType === 'custom'"
          v-model="customDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleCustomDateChange"
        />
      </div>
    </el-card>

    <el-row :gutter="16" class="stats-cards" v-loading="loading">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-wrapper revenue-icon">
            <el-icon class="stat-icon"><Money /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">本月总收入</div>
            <div class="stat-value">¥{{ (statsData?.totalRevenue || 0).toFixed(2) }}</div>
            <div class="stat-compare" v-if="statsData?.lastPeriodRevenue">
              <span :class="growthRateDisplay.isPositive ? 'positive' : 'negative'">
                <el-icon v-if="growthRateDisplay.isPositive"><ArrowUp /></el-icon>
                <el-icon v-else><ArrowDown /></el-icon>
                {{ growthRateDisplay.value }}%
              </span>
              <span class="compare-text">较上期</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-wrapper order-icon">
            <el-icon class="stat-icon"><ShoppingCart /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">本月订单数</div>
            <div class="stat-value">{{ statsData?.orderCount || 0 }}</div>
            <div class="stat-sub">
              <span>服务订单 {{ statsData?.serviceOrderCount || 0 }}</span>
              <span>商品订单 {{ statsData?.productOrderCount || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-wrapper avg-icon">
            <el-icon class="stat-icon"><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">平均客单价</div>
            <div class="stat-value">¥{{ (statsData?.avgOrderValue || 0).toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-wrapper growth-icon">
            <el-icon class="stat-icon"><Top /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">环比增长率</div>
            <div class="stat-value" :class="growthRateDisplay.isPositive ? 'positive' : 'negative'">
              {{ growthRateDisplay.isPositive ? '+' : '-' }}{{ growthRateDisplay.value }}%
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :span="14">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">
              <el-icon class="title-icon"><TrendCharts /></el-icon>
              营收趋势
            </span>
          </template>
          <div class="trend-area">
            <div v-if="trendItems.length > 0" class="trend-chart">
              <div class="chart-labels">
                <span class="label-item service-label">服务收入</span>
                <span class="label-item product-label">商品收入</span>
              </div>
              <div class="trend-bars">
                <div v-for="item in trendItems" :key="item.label" class="trend-bar-item">
                  <div class="bar-stacks">
                    <div
                      class="bar service-bar"
                      :style="{ height: `${(item.serviceValue / maxTrendValue) * 120}px` }"
                    />
                    <div
                      class="bar product-bar"
                      :style="{ height: `${(item.productValue / maxTrendValue) * 120}px` }"
                    />
                  </div>
                  <div class="bar-label">{{ item.label }}</div>
                  <div class="bar-value">¥{{ item.totalValue.toFixed(0) }}</div>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无趋势数据" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">
              <el-icon class="title-icon"><Money /></el-icon>
              营收构成
            </span>
          </template>
          <div class="composition-area" v-if="compositionData.length > 0">
            <div class="composition-chart">
              <div class="pie-container">
                <div
                  v-for="(item, index) in compositionData"
                  :key="item.name"
                  class="pie-segment"
                  :class="`segment-${index}`"
                  :style="{
                    '--percentage': compositionPercentages[item.name === '服务收入' ? 'service' : 'product'] + '%',
                    '--color': item.color
                  }"
                >
                  <div class="pie-label">{{ item.name }}</div>
                  <div class="pie-value">¥{{ item.value.toFixed(2) }}</div>
                </div>
              </div>
              <div class="pie-legend">
                <div
                  v-for="item in compositionData"
                  :key="item.name"
                  class="legend-item"
                >
                  <span class="legend-dot" :style="{ backgroundColor: item.color }"></span>
                  <span class="legend-name">{{ item.name }}</span>
                  <span class="legend-percent">
                    {{ compositionPercentages[item.name === '服务收入' ? 'service' : 'product'] }}%
                  </span>
                </div>
              </div>
            </div>
            <div class="composition-summary">
              <div class="summary-item">
                <span class="summary-label">服务收入</span>
                <el-progress
                  :percentage="compositionPercentages.service"
                  :stroke-width="8"
                  :show-text="false"
                  color="#409eff"
                />
                <span class="summary-value">¥{{ (statsData?.serviceRevenue || 0).toFixed(2) }}</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">商品收入</span>
                <el-progress
                  :percentage="compositionPercentages.product"
                  :stroke-width="8"
                  :show-text="false"
                  color="#67c23a"
                />
                <span class="summary-value">¥{{ (statsData?.productRevenue || 0).toFixed(2) }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无营收构成数据" />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" class="table-card">
      <template #header>
        <span class="card-title">
          <el-icon class="title-icon"><ShoppingCart /></el-icon>
          营收统计明细
        </span>
      </template>
      <el-table
        :data="statsData?.orderList || []"
        v-loading="loading"
        style="width: 100%"
        :show-header="true"
      >
        <el-table-column prop="date" label="日期" width="150">
          <template #default="{ row }">
            {{ dayjs(row.date).format('YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column label="服务订单数" width="120">
          <template #default="{ row }">
            {{ (row as RevenueOrderItem).serviceAmount > 0 ? '是' : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="服务收入" width="150">
          <template #default="{ row }">
            <span class="amount service-amount">
              ¥{{ (row as RevenueOrderItem).serviceAmount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="商品订单数" width="120">
          <template #default="{ row }">
            {{ (row as RevenueOrderItem).productAmount > 0 ? '是' : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="商品收入" width="150">
          <template #default="{ row }">
            <span class="amount product-amount">
              ¥{{ (row as RevenueOrderItem).productAmount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="总收入" min-width="150">
          <template #default="{ row }">
            <span class="amount total-amount">
              ¥{{ (row as RevenueOrderItem).totalAmount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-row :gutter="16" class="rank-row">
      <el-col :span="12">
        <el-card shadow="hover" class="rank-card">
          <template #header>
            <span class="card-title">
              <el-icon class="title-icon"><Star /></el-icon>
              Top服务排行
            </span>
          </template>
          <el-table
            :data="statsData?.topServices || []"
            v-loading="loading"
            style="width: 100%"
            :show-header="true"
          >
            <el-table-column type="index" label="排名" width="80">
              <template #default="{ $index }">
                <el-tag :type="$index < 3 ? 'danger' : 'info'" size="small">
                  {{ $index + 1 }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="服务名称" min-width="150" />
            <el-table-column prop="orderCount" label="订单数" width="100" />
            <el-table-column label="收入" width="120">
              <template #default="{ row }">
                <span class="amount">¥{{ (row as TopServiceItem).revenue.toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!statsData?.topServices?.length" description="暂无服务数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="rank-card">
          <template #header>
            <span class="card-title">
              <el-icon class="title-icon"><Goods /></el-icon>
              Top商品排行
            </span>
          </template>
          <el-table
            :data="statsData?.topProducts || []"
            v-loading="loading"
            style="width: 100%"
            :show-header="true"
          >
            <el-table-column type="index" label="排名" width="80">
              <template #default="{ $index }">
                <el-tag :type="$index < 3 ? 'danger' : 'info'" size="small">
                  {{ $index + 1 }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="商品名称" min-width="150" />
            <el-table-column prop="orderCount" label="订单数" width="100" />
            <el-table-column label="收入" width="120">
              <template #default="{ row }">
                <span class="amount">¥{{ (row as TopProductItem).revenue.toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!statsData?.topProducts?.length" description="暂无商品数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.stats-revenue {
  padding: 0;
}

.title-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.filter-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.stats-cards {
  margin-bottom: 16px;
}

.stat-card {
  border-radius: 12px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 8px;
}

.stat-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon {
  font-size: 28px;
  color: #fff;
}

.revenue-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.order-icon {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.avg-icon {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.growth-icon {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
}

.stat-value.positive {
  color: #67c23a;
}

.stat-value.negative {
  color: #f56c6c;
}

.stat-compare {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  font-size: 12px;
}

.stat-compare .positive {
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 2px;
}

.stat-compare .negative {
  color: #f56c6c;
  display: flex;
  align-items: center;
  gap: 2px;
}

.compare-text {
  color: #909399;
}

.stat-sub {
  display: flex;
  gap: 12px;
  margin-top: 6px;
  font-size: 12px;
  color: #606266;
}

.content-row {
  margin-bottom: 16px;
}

.chart-card {
  border-radius: 12px;
  height: 100%;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  color: #409eff;
}

.trend-area {
  min-height: 200px;
}

.trend-chart {
  padding: 10px 0;
}

.chart-labels {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  justify-content: center;
}

.label-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
}

.label-item::before {
  content: '';
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.service-label::before {
  background: linear-gradient(180deg, #409eff 0%, #66b1ff 100%);
}

.product-label::before {
  background: linear-gradient(180deg, #67c23a 0%, #85ce61 100%);
}

.trend-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 160px;
  padding-top: 20px;
}

.trend-bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  max-width: 80px;
}

.bar-stacks {
  display: flex;
  gap: 4px;
  align-items: flex-end;
}

.bar {
  width: 20px;
  border-radius: 4px 4px 0 0;
  transition: height 0.3s ease;
  min-height: 4px;
}

.service-bar {
  background: linear-gradient(180deg, #409eff 0%, #66b1ff 100%);
}

.product-bar {
  background: linear-gradient(180deg, #67c23a 0%, #85ce61 100%);
}

.bar-label {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.bar-value {
  font-size: 11px;
  color: #606266;
  margin-top: 4px;
  text-align: center;
}

.composition-area {
  min-height: 240px;
}

.composition-chart {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 20px;
}

.pie-container {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: conic-gradient(
    #409eff 0deg,
    #409eff var(--percentage, 0deg),
    #67c23a var(--percentage, 0deg),
    #67c23a 360deg
  );
  display: flex;
  align-items: center;
  justify-content: center;
}

.pie-container::before {
  content: '';
  position: absolute;
  width: 80px;
  height: 80px;
  background: #fff;
  border-radius: 50%;
}

.pie-segment {
  position: relative;
  z-index: 1;
  text-align: center;
}

.pie-label {
  font-size: 12px;
  color: #606266;
}

.pie-value {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.pie-legend {
  flex: 1;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.legend-name {
  flex: 1;
  font-size: 14px;
  color: #606266;
}

.legend-percent {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.composition-summary {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.summary-label {
  width: 70px;
  font-size: 13px;
  color: #606266;
}

.summary-item :deep(.el-progress) {
  flex: 1;
}

.summary-value {
  width: 100px;
  text-align: right;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.table-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.amount {
  font-weight: 600;
}

.service-amount {
  color: #409eff;
}

.product-amount {
  color: #67c23a;
}

.total-amount {
  color: #303133;
}

.rank-row {
  margin-bottom: 16px;
}

.rank-card {
  border-radius: 12px;
  min-height: 280px;
}

.rank-card :deep(.el-card__body) {
  padding-bottom: 10px;
}
</style>