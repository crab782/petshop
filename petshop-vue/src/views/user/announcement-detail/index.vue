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

// 硬编码测试数据 - 仅在开发环境使用
const mockAnnouncement: Announcement = {
  id: 1,
  title: '平台服务升级通知',
  content: '尊敬的用户：\n\n为了提供更好的服务体验，我们将于2024年1月20日凌晨2:00-4:00进行系统升级维护。期间平台可能会出现短暂的访问延迟或无法访问的情况，请您提前做好安排。\n\n升级完成后，我们将推出以下新功能：\n1. 全新的宠物预约系统，支持更多预约时间选择\n2. 优化后的商家评价系统，提供更详细的评价维度\n3. 增加宠物健康档案功能，方便您记录宠物的健康状况\n\n感谢您的理解与支持！\n\n宠物服务平台团队\n2024年1月15日',
  publishTime: '2024-01-15T09:00:00',
  publisher: '系统管理员'
}

const mockAnnouncements: Announcement[] = [
  {
    id: 1,
    title: '平台服务升级通知',
    content: '尊敬的用户：\n\n为了提供更好的服务体验，我们将于2024年1月20日凌晨2:00-4:00进行系统升级维护。',
    publishTime: '2024-01-15T09:00:00',
    publisher: '系统管理员'
  },
  {
    id: 2,
    title: '春节期间服务安排',
    content: '春节期间（2月10日-2月17日），平台服务时间调整为10:00-18:00，请您合理安排预约时间。',
    publishTime: '2024-01-20T10:30:00',
    publisher: '客服中心'
  },
  {
    id: 3,
    title: '新商家入驻公告',
    content: '欢迎"快乐宠物乐园"入驻平台，为大家提供专业的宠物寄养和训练服务。',
    publishTime: '2024-01-10T14:00:00',
    publisher: '运营团队'
  },
  {
    id: 4,
    title: '宠物健康知识讲座',
    content: '我们将于1月25日举办线上宠物健康知识讲座，欢迎大家参与。',
    publishTime: '2024-01-18T11:00:00',
    publisher: '市场部'
  },
  {
    id: 5,
    title: '平台优惠券活动',
    content: '即日起至2月29日，新用户注册即可领取50元服务优惠券，老用户可领取30元优惠券。',
    publishTime: '2024-01-22T09:30:00',
    publisher: '营销部'
  }
]

const relatedAnnouncements = computed(() => {
  if (!announcement.value) return []
  return allAnnouncements.value
    .filter(item => item.id !== announcement.value?.id)
    .slice(0, 5)
})

const fetchAnnouncement = async () => {
  const id = Number(route.params.id)
  try {
    // 开发环境使用模拟数据
    if (import.meta.env.DEV) {
      loading.value = true
      // 模拟加载延迟
      await new Promise(resolve => setTimeout(resolve, 300))
      announcement.value = mockAnnouncement
      loading.value = false
      return
    }
    
    if (isNaN(id)) {
      ElMessage.error('无效的公告ID')
      router.back()
      return
    }

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
    // 开发环境使用模拟数据
    if (import.meta.env.DEV) {
      allAnnouncements.value = mockAnnouncements
      return
    }
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
