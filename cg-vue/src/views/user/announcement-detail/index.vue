<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Calendar, User } from '@element-plus/icons-vue'
import { getAnnouncementById, getAnnouncements } from '@/api/announcement'

interface Announcement {
  id: number
  title: string
  content: string
  publishTime: string
  publisher?: string
  status?: string
}

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const announcement = ref<Announcement | null>(null)
const allAnnouncements = ref<Announcement[]>([])

const relatedAnnouncements = computed(() => {
  if (!announcement.value) return []
  return allAnnouncements.value
    .filter(item => item.id !== announcement.value?.id)
    .slice(0, 5)
})

const fetchAnnouncement = async () => {
  const id = Number(route.params.id)
  if (isNaN(id)) {
    ElMessage.error('无效的公告ID')
    router.back()
    return
  }

  try {
    loading.value = true
    const data = await getAnnouncementById(id)
    announcement.value = data
  } catch (error: unknown) {
    const err = error as Error
    ElMessage.error(err.message || '获取公告详情失败')
  } finally {
    loading.value = false
  }
}

const fetchAllAnnouncements = async () => {
  try {
    const res = await getAnnouncements()
    allAnnouncements.value = res || []
  } catch {
    allAnnouncements.value = []
  }
}

const handleBack = () => {
  router.push('/user/announcements')
}

const handleViewRelated = (id: number) => {
  router.push(`/user/announcements/${id}`)
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

onMounted(() => {
  fetchAnnouncement()
  fetchAllAnnouncements()
})
</script>

<template>
  <div class="announcement-detail">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/user/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/user/announcements' }">公告列表</el-breadcrumb-item>
      <el-breadcrumb-item>公告详情</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card class="detail-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="header-title">公告详情</span>
          <el-button type="primary" text @click="handleBack">
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
        </div>
      </template>

      <div v-if="announcement" class="content">
        <h1 class="title">{{ announcement.title }}</h1>
        <div class="meta">
          <span class="meta-item">
            <el-icon><Calendar /></el-icon>
            发布时间：{{ formatDate(announcement.publishTime) }}
          </span>
          <span class="meta-item" v-if="announcement.publisher">
            <el-icon><User /></el-icon>
            发布人：{{ announcement.publisher }}
          </span>
        </div>
        <el-divider />
        <div class="body">{{ announcement.content }}</div>
      </div>

      <el-empty v-if="!loading && !announcement" description="公告不存在" />
    </el-card>

    <el-card class="related-card" v-if="relatedAnnouncements.length > 0">
      <template #header>
        <div class="related-header">
          <span class="related-title">相关公告</span>
        </div>
      </template>

      <div class="related-list">
        <div
          v-for="item in relatedAnnouncements"
          :key="item.id"
          class="related-item"
          @click="handleViewRelated(item.id)"
        >
          <div class="related-item-title">{{ item.title }}</div>
          <div class="related-item-time">{{ formatDate(item.publishTime) }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.announcement-detail {
  max-width: 900px;
  margin: 0 auto;
}

.detail-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.content {
  padding: 20px 0;
}

.title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 20px 0;
  text-align: center;
  line-height: 1.4;
}

.meta {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 24px;
  font-size: 14px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.body {
  font-size: 16px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
  min-height: 200px;
  padding: 0 20px;
}

.related-card {
  margin-top: 24px;
}

.related-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.related-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.related-item:hover {
  background-color: #ecf5ff;
}

.related-item-title {
  font-size: 14px;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.related-item-time {
  font-size: 12px;
  color: #909399;
  margin-left: 16px;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .title {
    font-size: 22px;
  }

  .meta {
    flex-direction: column;
    gap: 8px;
    align-items: center;
  }

  .body {
    padding: 0;
    font-size: 15px;
  }

  .related-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .related-item-time {
    margin-left: 0;
  }
}
</style>
