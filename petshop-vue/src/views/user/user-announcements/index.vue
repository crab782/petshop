<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAnnouncements, getAnnouncementById } from '@/api/announcement'

// 硬编码测试数据 - 仅在开发环境使用
const mockAnnouncements = [
  {
    id: 1,
    title: '平台系统升级通知',
    content: '尊敬的用户，为了提供更好的服务体验，我们将于2024年1月20日凌晨进行系统升级维护，预计维护时间为2小时。维护期间可能会出现短暂的服务中断，给您带来的不便敬请谅解。\n\n升级内容：\n1. 优化预约流程，提高预约成功率\n2. 新增宠物健康档案功能\n3. 修复部分已知问题\n\n感谢您的支持与理解！',
    publishTime: '2024-01-15 10:00:00',
    category: 'system'
  },
  {
    id: 2,
    title: '新年优惠活动开始啦',
    content: '亲爱的用户，值此新年之际，我们推出了一系列优惠活动：\n\n1. 新用户注册送100元服务券\n2. 预约服务享8折优惠\n3. 会员充值满1000送200\n\n活动时间：2024年1月1日 - 2024年1月31日\n\n详情请查看活动页面或咨询客服。',
    publishTime: '2024-01-01 00:00:00',
    category: 'activity'
  },
  {
    id: 3,
    title: '服务内容更新',
    content: '为了满足广大用户的需求，我们新增了以下服务：\n\n1. 宠物寄养服务\n2. 宠物训练课程\n3. 宠物健康咨询\n\n所有服务均由专业人员提供，欢迎预约体验！',
    publishTime: '2023-12-20 15:30:00',
    category: 'update'
  },
  {
    id: 4,
    title: '关于平台使用的温馨提示',
    content: '尊敬的用户，为了确保您的使用体验，请注意以下事项：\n\n1. 请确保预约信息填写准确，特别是宠物信息和预约时间\n2. 如需取消预约，请提前24小时操作\n3. 如有任何问题，请及时联系客服\n\n感谢您的配合！',
    publishTime: '2023-12-10 09:00:00',
    category: 'other'
  },
  {
    id: 5,
    title: '平台安全提示',
    content: '为了保障您的账户安全，请务必注意以下几点：\n\n1. 不要将账户密码告诉他人\n2. 定期修改密码\n3. 如遇可疑操作，请及时联系客服\n\n我们将持续加强平台安全措施，为您提供更安全的服务环境。',
    publishTime: '2023-11-30 14:00:00',
    category: 'system'
  }
]

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
    // 在开发环境下使用硬编码测试数据
    if (import.meta.env.DEV) {
      // 模拟API延迟
      await new Promise(resolve => setTimeout(resolve, 300))
      announcementList.value = mockAnnouncements.map(item => ({
        ...item,
        summary: item.content ? item.content.substring(0, 100) + (item.content.length > 100 ? '...' : '') : '',
        category: item.category || 'other'
      }))
    } else {
      // 在生产环境下使用真实API
      const res = await getAnnouncements()
      const data = res.data || res
      announcementList.value = (data || []).map(item => ({
        ...item,
        summary: item.content ? item.content.substring(0, 100) + (item.content.length > 100 ? '...' : '') : '',
        category: item.category || 'other'
      }))
    }
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
