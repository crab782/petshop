<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElCard, ElRow, ElCol, ElStatistic, ElSelect, ElOption, ElTable, ElTableColumn, ElTag, ElProgress, ElMessage, ElButton, ElButtonGroup, ElDatePicker } from 'element-plus'
import { Calendar, Clock, Check, Close, TrendCharts, PieChart, Star, Download, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { getMerchantAppointmentStats, type AppointmentStats } from '@/api/merchant'

type TimeType = 'today' | 'week' | 'month' | 'year' | 'custom'

const timeType = ref<TimeType>('today')
const customDateRange = ref<[Date, Date] | null>(null)
const loading = ref(false)
const statsData = ref<AppointmentStats | null>(null)

const timeButtons = [
  { label: '今日', value: 'today' },
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '本年', value: 'year' },
  { label: '自定义', value: 'custom' }
]

const sourceColors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399']

const fetchStats = async () => {
  loading.value = true
  try {
    const params: { timeRange: string; startDate?: string; endDate?: string } = { timeRange: timeType.value }
    if (timeType.value === 'custom' && customDateRange.value) {
      params.startDate = customDateRange.value[0].toISOString().split('T')[0]
      params.endDate = customDateRange.value[1].toISOString().split('T')[0]
    }
    statsData.value = await getMerchantAppointmentStats(params.timeRange, params.startDate, params.endDate)
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

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

const totalAppointments = computed(() => {
  if (!statsData.value) return 0
  return statsData.value.totalCount
})

const pendingAppointments = computed(() => {
  if (!statsData.value) return 0
  return statsData.value.pendingCount
})

const completedAppointments = computed(() => {
  if (!statsData.value) return 0
  return statsData.value.completedCount
})

const cancelledAppointments = computed(() => {
  if (!statsData.value) return 0
  return statsData.value.cancelledCount
})

const completionRate = computed(() => {
  if (!statsData.value || statsData.value.totalCount === 0) return 0
  return Math.round((statsData.value.completedCount / statsData.value.totalCount) * 100)
})

const cancellationRate = computed(() => {
  if (!statsData.value || statsData.value.totalCount === 0) return 0
  return Math.round((statsData.value.cancelledCount / statsData.value.totalCount) * 100)
})

const growthRate = computed(() => {
  if (!statsData.value) return 0
  return statsData.value.growthRate || 0
})

const hourDistribution = computed(() => {
  if (!statsData.value?.hourData) return []
  return statsData.value.hourData.map(item => ({
    hour: item.hour,
    count: item.count,
    percentage: statsData.value!.totalCount > 0
      ? Math.round((item.count / statsData.value!.totalCount) * 100)
      : 0
  }))
})

const peakHour = computed(() => {
  if (!hourDistribution.value.length) return null
  return hourDistribution.value.reduce((max, item) => item.count > max.count ? item : max)
})

const getSourceColor = (index: number) => {
  return sourceColors[index % sourceColors.length]
}

const getSourcePercent = (value: number) => {
  if (!statsData.value || statsData.value.totalCount === 0) return 0
  return Math.round((value / statsData.value.totalCount) * 100)
}

const getBarHeight = (count: number) => {
  if (!hourDistribution.value.length) return 0
  const maxCount = Math.max(...hourDistribution.value.map(h => h.count))
  return maxCount > 0 ? Math.round((count / maxCount) * 100) : 0
}

const exportToCSV = () => {
  if (!statsData.value) {
    ElMessage.warning('暂无数据可导出')
    return
  }
  const headers = ['日期', '预约数量', '完成数量', '取消数量', '完成率', '总金额']
  const rows = statsData.value.trendData.map(item => {
    const dayData = statsData.value!.dailyStats?.[item.date] || {}
    return [
      item.date,
      item.count,
      dayData.completedCount || 0,
      dayData.cancelledCount || 0,
      item.count > 0 ? Math.round(((dayData.completedCount || 0) / item.count) * 100) + '%' : '0%',
      '¥' + ((dayData.totalRevenue || 0).toFixed(2))
    ]
  })

  const csvContent = [headers.join(','), ...rows.map(row => row.join(','))].join('\n')
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `预约统计_${new Date().toISOString().split('T')[0]}.csv`
  link.click()
  ElMessage.success('导出成功')
}

onMounted(() => {
  fetchStats()
})
</script>

<template>
  <div class="stats-appointments">
    <el-card shadow="hover" class="filter-card">
      <div class="filter-bar">
        <h2 class="page-title">预约统计</h2>
        <div class="filter-controls">
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
          <el-button type="success" :icon="Download" @click="exportToCSV">导出数据</el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="statistics-row" v-loading="loading">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card total-card">
          <el-statistic title="总预约数" :value="totalAppointments">
            <template #prefix>
              <el-icon class="stat-icon"><Calendar /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card pending-card">
          <el-statistic title="待处理数" :value="pendingAppointments">
            <template #prefix>
              <el-icon class="stat-icon"><Clock /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card completed-card">
          <el-statistic title="已完成数" :value="completedAppointments">
            <template #prefix>
              <el-icon class="stat-icon"><Check /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card cancelled-card">
          <el-statistic title="取消数" :value="cancelledAppointments">
            <template #prefix>
              <el-icon class="stat-icon"><Close /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="完成率" :value="completionRate" suffix="%">
            <template #prefix>
              <el-icon class="stat-icon" style="color: #67c23a"><Check /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="取消率" :value="cancellationRate" suffix="%">
            <template #prefix>
              <el-icon class="stat-icon" style="color: #f56c6c"><Close /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="statistics-row" v-loading="loading">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card growth-card">
          <el-statistic title="环比增长率" :value="growthRate" suffix="%">
            <template #prefix>
              <el-icon class="stat-icon" :style="{ color: growthRate >= 0 ? '#67c23a' : '#f56c6c' }">
                <ArrowUp v-if="growthRate >= 0" />
                <ArrowDown v-else />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="14">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">预约趋势</span>
              <el-icon class="header-icon"><TrendCharts /></el-icon>
            </div>
          </template>
          <el-table
            :data="statsData?.trendData || []"
            style="width: 100%"
            :show-header="true"
            v-loading="loading"
          >
            <el-table-column prop="date" label="日期" width="150" />
            <el-table-column prop="count" label="预约数量" />
            <el-table-column label="完成数" width="100">
              <template #default="{ row }">
                {{ statsData?.dailyStats?.[row.date]?.completedCount || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="取消数" width="100">
              <template #default="{ row }">
                {{ statsData?.dailyStats?.[row.date]?.cancelledCount || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="完成率" width="120">
              <template #default="{ row }">
                <el-progress
                  :percentage="row.count > 0 ? Math.round(((statsData?.dailyStats?.[row.date]?.completedCount || 0) / row.count) * 100) : 0"
                  :stroke-width="10"
                  :show-text="true"
                />
              </template>
            </el-table-column>
            <el-table-column label="总金额" width="120">
              <template #default="{ row }">
                ¥{{ (statsData?.dailyStats?.[row.date]?.totalRevenue || 0).toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">预约来源分析</span>
              <el-icon class="header-icon"><PieChart /></el-icon>
            </div>
          </template>
          <div class="source-analysis" v-loading="loading">
            <div
              v-for="(item, index) in statsData?.sourceData || []"
              :key="item.name"
              class="source-item"
            >
              <div class="source-label">
                <span class="source-dot" :style="{ backgroundColor: getSourceColor(index) }"></span>
                <span class="source-name">{{ item.name }}</span>
              </div>
              <div class="source-value">
                <span class="source-count">{{ item.value }}</span>
                <span class="source-percent">{{ getSourcePercent(item.value) }}%</span>
              </div>
            </div>
            <div v-if="!statsData?.sourceData?.length" class="empty-text">
              暂无数据
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">时段统计（预约高峰分析）</span>
              <el-icon class="header-icon"><Clock /></el-icon>
            </div>
          </template>
          <div class="hour-stats" v-loading="loading">
            <div v-if="peakHour" class="peak-hour-tip">
              <span class="peak-label">预约高峰时段：</span>
              <span class="peak-value">{{ peakHour.hour }}:00 - {{ peakHour.hour + 1 }}:00</span>
              <span class="peak-count">（{{ peakHour.count }}笔预约）</span>
            </div>
            <div class="hour-bars">
              <div v-for="item in hourDistribution" :key="item.hour" class="hour-bar-item">
                <div class="hour-bar-container">
                  <div
                    class="hour-bar"
                    :style="{ height: `${getBarHeight(item.count)}%` }"
                  />
                </div>
                <div class="hour-label">{{ item.hour }}时</div>
                <div class="hour-count">{{ item.count }}</div>
              </div>
            </div>
            <div v-if="!hourDistribution.length" class="empty-text">
              暂无时段数据
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">服务类型统计</span>
              <el-icon class="header-icon"><Star /></el-icon>
            </div>
          </template>
          <div class="service-stats" v-loading="loading">
            <div
              v-for="(item, index) in statsData?.sourceData || []"
              :key="item.name"
              class="service-item"
            >
              <div class="service-info">
                <span class="service-dot" :style="{ backgroundColor: getSourceColor(index) }"></span>
                <span class="service-name">{{ item.name }}</span>
              </div>
              <div class="service-bar-container">
                <div
                  class="service-bar"
                  :style="{
                    width: `${getSourcePercent(item.value)}%`,
                    backgroundColor: getSourceColor(index)
                  }"
                />
              </div>
              <div class="service-count">{{ item.value }}次</div>
            </div>
            <div v-if="!statsData?.sourceData?.length" class="empty-text">
              暂无服务数据
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" class="hot-services-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="card-title">热门服务统计</span>
          <el-icon class="header-icon"><Star /></el-icon>
        </div>
      </template>
      <el-table
        :data="statsData?.hotServices || []"
        style="width: 100%"
        :show-header="true"
      >
        <el-table-column prop="name" label="服务名称" min-width="200" />
        <el-table-column prop="count" label="预约次数" width="150" />
        <el-table-column label="占比" width="300">
          <template #default="{ row }">
            <el-progress
              :percentage="Math.min((row.count / (statsData?.totalCount || 1)) * 100, 100)"
              :stroke-width="10"
              :show-text="true"
              :color="sourceColors[Math.floor(Math.random() * sourceColors.length)]"
            />
          </template>
        </el-table-column>
        <el-table-column label="热度" width="120">
          <template #default="{ row }">
            <el-tag
              :type="row.count > 50 ? 'danger' : row.count > 20 ? 'warning' : 'info'"
              size="small"
            >
              {{ row.count > 50 ? '热门' : row.count > 20 ? '一般' : '冷门' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-row :gutter="20" class="summary-row">
      <el-col :span="24">
        <el-card shadow="hover" class="summary-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">数据摘要</span>
            </div>
          </template>
          <div class="summary-content">
            <div class="summary-item">
              <span class="summary-label">完成率</span>
              <el-progress
                type="circle"
                :percentage="completionRate"
                :width="100"
                :stroke-width="12"
              />
            </div>
            <div class="summary-item">
              <span class="summary-label">本周趋势</span>
              <div class="trend-indicator">
                <span class="trend-value">{{ statsData?.trendData?.length || 0 }} 天有预约</span>
              </div>
            </div>
            <div class="summary-item">
              <span class="summary-label">最热服务</span>
              <span class="hot-service-name">
                {{ statsData?.hotServices?.[0]?.name || '暂无' }}
              </span>
            </div>
            <div class="summary-item">
              <span class="summary-label">高峰时段</span>
              <span class="peak-time" v-if="peakHour">
                {{ peakHour.hour }}:00 - {{ peakHour.hour + 1 }}:00
              </span>
              <span class="peak-time" v-else>暂无</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.stats-appointments {
  padding: 0;
}

.filter-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.statistics-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 28px;
  margin-right: 8px;
}

.total-card .stat-icon {
  color: #409eff;
}

.pending-card .stat-icon {
  color: #e6a23c;
}

.completed-card .stat-icon {
  color: #67c23a;
}

.cancelled-card .stat-icon {
  color: #909399;
}

.content-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-icon {
  font-size: 20px;
  color: #409eff;
}

.source-analysis {
  padding: 10px 0;
}

.source-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.source-item:last-child {
  border-bottom: none;
}

.source-label {
  display: flex;
  align-items: center;
  gap: 10px;
}

.source-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.source-name {
  font-size: 14px;
  color: #606266;
}

.source-value {
  display: flex;
  align-items: center;
  gap: 12px;
}

.source-count {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.source-percent {
  font-size: 14px;
  color: #909399;
  min-width: 40px;
  text-align: right;
}

.empty-text {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

.hot-services-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.summary-row {
  margin-bottom: 20px;
}

.summary-card {
  border-radius: 12px;
}

.summary-content {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 20px 0;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.summary-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.trend-indicator {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
}

.hot-service-name {
  font-size: 20px;
  font-weight: 600;
  color: #f56c6c;
}

.filter-controls {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.hour-stats {
  padding: 10px 0;
}

.peak-hour-tip {
  text-align: center;
  padding: 12px;
  background: #f0f9eb;
  border-radius: 8px;
  margin-bottom: 16px;
}

.peak-label {
  font-size: 14px;
  color: #606266;
}

.peak-value {
  font-size: 16px;
  font-weight: 600;
  color: #67c23a;
  margin: 0 8px;
}

.peak-count {
  font-size: 14px;
  color: #909399;
}

.hour-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 160px;
  padding: 10px 0;
}

.hour-bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.hour-bar-container {
  width: 24px;
  height: 120px;
  background: #f0f2f5;
  border-radius: 4px;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
}

.hour-bar {
  width: 100%;
  background: linear-gradient(180deg, #409eff 0%, #66b1ff 100%);
  border-radius: 4px;
  transition: height 0.3s ease;
  min-height: 4px;
}

.hour-label {
  margin-top: 8px;
  font-size: 11px;
  color: #909399;
}

.hour-count {
  font-size: 12px;
  color: #606266;
  font-weight: 500;
}

.service-stats {
  padding: 10px 0;
}

.service-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  gap: 12px;
}

.service-info {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 120px;
}

.service-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.service-name {
  font-size: 14px;
  color: #303133;
}

.service-bar-container {
  flex: 1;
  height: 16px;
  background: #f0f2f5;
  border-radius: 8px;
  overflow: hidden;
}

.service-bar {
  height: 100%;
  border-radius: 8px;
  transition: width 0.3s ease;
  min-width: 4px;
}

.service-count {
  font-size: 13px;
  color: #606266;
  min-width: 60px;
  text-align: right;
}

.growth-card .stat-icon {
  color: #409eff;
}

.peak-time {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
}
</style>
