<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAnnouncements, getAnnouncementById } from '@/api/announcement'



interface Announcement {
  id: number
  title: string
  content: string
  publishTime: string
  category?: string
  summary?: string
}

const loading = ref(false)
const announcementList = ref<Announcement[]>([])
const searchKeyword = ref('')
const selectedCategory = ref('all')
const dialogVisible = ref(false)
const currentAnnouncement = reactive<Announcement>({
  id: 0,
  title: '',
  content: '',
  publishTime: ''
})

const readAnnouncements = ref<Set<number>>(new Set())

const categories = [
  { label: '全部', value: 'all' },
  { label: '系统公告', value: 'system' },
  { label: '活动通知', value: 'activity' },
  { label: '服务更新', value: 'update' },
  { label: '其他', value: 'other' }
]

const filteredList = computed(() => {
  let list = announcementList.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(item =>
      item.title.toLowerCase().includes(keyword) ||
      (item.content && item.content.toLowerCase().includes(keyword))
    )
  }

  if (selectedCategory.value !== 'all') {
    list = list.filter(item => item.category === selectedCategory.value)
  }

  return list
})

const fetchAnnouncements = async () => {
  try {
    loading.value = true
    // 使用真实API
    const res = await getAnnouncements()
    const data = res.data || res
    announcementList.value = (data || []).map(item => ({
      ...item,
      summary: item.content ? item.content.substring(0, 100) + (item.content.length > 100 ? '...' : '') : '',
      category: item.category || 'other'
    }))
    loadReadStatus()
  } catch (error: unknown) {
    const err = error as Error
    ElMessage.error(err.message || '获取公告列表失败')
  } finally {
    loading.value = false
  }
}

const loadReadStatus = () => {
  const stored = localStorage.getItem('readAnnouncements')
  if (stored) {
    try {
      const parsed = JSON.parse(stored)
      readAnnouncements.value = new Set(parsed)
    } catch {
      readAnnouncements.value = new Set()
    }
  }
}

const saveReadStatus = () => {
  localStorage.setItem('readAnnouncements', JSON.stringify([...readAnnouncements.value]))
}

const handleViewDetail = async (row: Announcement) => {
  try {
    const detailRes = await getAnnouncementById(row.id)
    const detail = detailRes.data || detailRes
    currentAnnouncement.id = detail.id
    currentAnnouncement.title = detail.title
    currentAnnouncement.content = detail.content
    currentAnnouncement.publishTime = detail.publishTime
    currentAnnouncement.category = detail.category

    readAnnouncements.value.add(row.id)
    saveReadStatus()

    dialogVisible.value = true
  } catch {
    currentAnnouncement.id = row.id
    currentAnnouncement.title = row.title
    currentAnnouncement.content = row.content
    currentAnnouncement.publishTime = row.publishTime
    currentAnnouncement.category = row.category

    readAnnouncements.value.add(row.id)
    saveReadStatus()

    dialogVisible.value = true
  }
}

const handleSearch = () => {
}

const handleCategoryChange = () => {
}

const isRead = (id: number) => {
  return readAnnouncements.value.has(id)
}

const getCategoryLabel = (value: string) => {
  const cat = categories.find(c => c.value === value)
  return cat ? cat.label : '其他'
}

const getCategoryType = (value: string) => {
  const typeMap: Record<string, string> = {
    system: 'danger',
    activity: 'warning',
    update: 'success',
    other: 'info'
  }
  return typeMap[value] || 'info'
}

onMounted(() => {
  fetchAnnouncements()
})
</script>

<template>
  <div class="user-announcements">
    <h1 class="page-title">平台公告</h1>

    <el-card class="filter-card">
      <div class="filter-row">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索公告标题或内容"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="selectedCategory"
          placeholder="公告分类"
          style="width: 150px; margin-left: 16px"
          @change="handleCategoryChange"
        >
          <el-option
            v-for="cat in categories"
            :key="cat.value"
            :label="cat.label"
            :value="cat.value"
          />
        </el-select>
      </div>
    </el-card>

    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>公告列表</span>
          <span class="total-count">共 {{ filteredList.length }} 条公告</span>
        </div>
      </template>

      <div v-loading="loading" class="announcement-list">
        <el-empty
          v-if="!loading && filteredList.length === 0"
          description="暂无公告"
        />

        <div
          v-for="item in filteredList"
          :key="item.id"
          class="announcement-item"
          :class="{ 'is-read': isRead(item.id) }"
          @click="handleViewDetail(item)"
        >
          <div class="announcement-main">
            <div class="announcement-header">
              <span class="announcement-title">
                <el-badge v-if="!isRead(item.id)" is-dot class="unread-dot" />
                {{ item.title }}
              </span>
              <el-tag
                :type="getCategoryType(item.category || 'other')"
                size="small"
              >
                {{ getCategoryLabel(item.category || 'other') }}
              </el-tag>
            </div>
            <div class="announcement-summary">{{ item.summary }}</div>
            <div class="announcement-footer">
              <span class="publish-time">
                <el-icon><i-ep-clock /></el-icon>
                {{ item.publishTime }}
              </span>
              <span v-if="isRead(item.id)" class="read-status">
                <el-icon><i-ep-check /></el-icon>
                已读
              </span>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="公告详情"
      width="700px"
      class="announcement-dialog"
    >
      <div class="announcement-detail">
        <div class="detail-header">
          <el-tag
            :type="getCategoryType(currentAnnouncement.category || 'other')"
            size="small"
          >
            {{ getCategoryLabel(currentAnnouncement.category || 'other') }}
          </el-tag>
          <h2 class="detail-title">{{ currentAnnouncement.title }}</h2>
          <div class="detail-time">
            <el-icon><i-ep-clock /></el-icon>
            发布时间：{{ currentAnnouncement.publishTime }}
          </div>
        </div>
        <el-divider />
        <div class="detail-content">{{ currentAnnouncement.content }}</div>
      </div>
      <template #footer>
        <el-button type="primary" @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-announcements {
  max-width: 1000px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 24px 0;
}

.filter-card {
  margin-bottom: 16px;
}

.filter-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.list-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-count {
  font-size: 14px;
  color: #909399;
}

.announcement-list {
  min-height: 200px;
}

.announcement-item {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-item:hover {
  background-color: #f5f7fa;
}

.announcement-item.is-read {
  opacity: 0.7;
}

.announcement-main {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.announcement-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.unread-dot {
  margin-right: 4px;
}

.announcement-summary {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.announcement-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.publish-time,
.read-status {
  display: flex;
  align-items: center;
  gap: 4px;
}

.read-status {
  color: #67c23a;
}

.announcement-detail {
  padding: 10px 0;
}

.detail-header {
  text-align: center;
  margin-bottom: 16px;
}

.detail-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 12px 0;
}

.detail-time {
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.detail-content {
  font-size: 15px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
  padding: 0 16px;
}

@media (max-width: 768px) {
  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-row .el-input,
  .filter-row .el-select {
    width: 100% !important;
    margin-left: 0 !important;
  }

  .announcement-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
