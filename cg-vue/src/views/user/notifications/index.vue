<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Message, ShoppingCart, Calendar, ChatDotRound, Check, Delete } from '@element-plus/icons-vue'
import {
  getNotifications,
  markAsRead,
  markAllAsRead,
  markBatchAsRead,
  deleteNotification,
  deleteBatchNotifications,
  type Notification
} from '@/api/notification'

const loading = ref(false)
const activeTab = ref('all')
const readFilter = ref<string>('all')
const notificationList = ref<Notification[]>([])
const detailDialogVisible = ref(false)
const currentNotification = reactive<Notification>({
  id: 0,
  type: 'system',
  title: '',
  summary: '',
  content: '',
  isRead: false,
  createTime: ''
})

const selectedIds = ref<number[]>([])

const filteredList = computed(() => {
  let list = notificationList.value

  if (activeTab.value !== 'all') {
    list = list.filter(item => item.type === activeTab.value)
  }

  if (readFilter.value === 'unread') {
    list = list.filter(item => !item.isRead)
  } else if (readFilter.value === 'read') {
    list = list.filter(item => item.isRead)
  }

  return list
})

const unreadCount = computed(() => {
  return notificationList.value.filter(item => !item.isRead).length
})

const notificationTypes = [
  { value: 'system', label: '系统通知', icon: Message, color: '#409eff' },
  { value: 'order', label: '订单通知', icon: ShoppingCart, color: '#67c23a' },
  { value: 'appointment', label: '预约通知', icon: Calendar, color: '#e6a23c' },
  { value: 'review', label: '评价通知', icon: ChatDotRound, color: '#f56c6c' }
]

const getNotificationTypeInfo = (type: string) => {
  return notificationTypes.find(t => t.value === type) || notificationTypes[0]
}

const getNotificationIcon = (type: string) => {
  return getNotificationTypeInfo(type).icon
}

const getNotificationIconColor = (type: string) => {
  return getNotificationTypeInfo(type).color
}

const getTypeLabel = (type: string) => {
  return getNotificationTypeInfo(type).label
}

const fetchNotifications = async () => {
  try {
    loading.value = true
    const res = await getNotifications()
    notificationList.value = res.data || res || []
  } catch {
    ElMessage.error('获取通知列表失败')
  } finally {
    loading.value = false
  }
}

const handleViewDetail = async (row: Notification) => {
  if (!row.isRead) {
    try {
      await markAsRead(row.id)
      row.isRead = true
    } catch {
      ElMessage.error('标记已读失败')
    }
  }
  currentNotification.id = row.id
  currentNotification.type = row.type
  currentNotification.title = row.title
  currentNotification.summary = row.summary
  currentNotification.content = row.content
  currentNotification.isRead = row.isRead
  currentNotification.createTime = row.createTime
  detailDialogVisible.value = true
}

const handleMarkAsRead = async (row: Notification) => {
  try {
    await markAsRead(row.id)
    row.isRead = true
    ElMessage.success('已标记为已读')
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row: Notification) => {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteNotification(row.id)
    notificationList.value = notificationList.value.filter(item => item.id !== row.id)
    ElMessage.success('删除成功')
  } catch {
  }
}

const handleMarkAllAsRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将所有通知标为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await markAllAsRead()
    notificationList.value.forEach(item => {
      item.isRead = true
    })
    ElMessage.success('已全部标为已读')
  } catch {
  }
}

const handleSelectionChange = (selection: Notification[]) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleBatchMarkRead = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要标记的通知')
    return
  }
  try {
    await markBatchAsRead(selectedIds.value)
    notificationList.value.forEach(item => {
      if (selectedIds.value.includes(item.id)) {
        item.isRead = true
      }
    })
    selectedIds.value = []
    ElMessage.success('批量标记成功')
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的通知')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条通知吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteBatchNotifications(selectedIds.value)
    notificationList.value = notificationList.value.filter(item => !selectedIds.value.includes(item.id))
    selectedIds.value = []
    ElMessage.success('批量删除成功')
  } catch {
  }
}

onMounted(() => {
  fetchNotifications()
})
</script>

<template>
  <div class="notifications">
    <h1 class="page-title">消息通知</h1>

    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-tabs v-model="activeTab" class="notification-tabs">
              <el-tab-pane label="全部" name="all" />
              <el-tab-pane label="系统通知" name="system" />
              <el-tab-pane label="订单通知" name="order" />
              <el-tab-pane label="预约通知" name="appointment" />
              <el-tab-pane label="评价通知" name="review" />
            </el-tabs>
            <el-select v-model="readFilter" placeholder="阅读状态" style="width: 120px; margin-left: 16px;">
              <el-option label="全部" value="all" />
              <el-option label="未读" value="unread" />
              <el-option label="已读" value="read" />
            </el-select>
          </div>
          <div class="header-right">
            <el-button
              v-if="unreadCount > 0"
              type="primary"
              plain
              size="small"
              @click="handleMarkAllAsRead"
            >
              全部标为已读
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="selectedIds.length > 0" class="batch-actions">
        <span class="selected-count">已选择 {{ selectedIds.length }} 条</span>
        <el-button type="primary" size="small" :icon="Check" @click="handleBatchMarkRead">
          批量标记已读
        </el-button>
        <el-button type="danger" size="small" :icon="Delete" @click="handleBatchDelete">
          批量删除
        </el-button>
      </div>

      <div v-loading="loading" class="notification-list">
        <el-table
          :data="filteredList"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column label="类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.type === 'system' ? 'primary' : row.type === 'order' ? 'success' : row.type === 'appointment' ? 'warning' : 'danger'" size="small">
                {{ getTypeLabel(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="通知内容" min-width="300">
            <template #default="{ row }">
              <div
                class="notification-item-row"
                :class="{ unread: !row.isRead }"
                @click="handleViewDetail(row)"
              >
                <div class="notification-icon-wrapper">
                  <div
                    class="notification-icon"
                    :style="{ backgroundColor: getNotificationIconColor(row.type) + '20', color: getNotificationIconColor(row.type) }"
                  >
                    <el-icon :size="20">
                      <component :is="getNotificationIcon(row.type)" />
                    </el-icon>
                  </div>
                </div>
                <div class="notification-content-wrapper">
                  <div class="notification-header">
                    <span class="notification-title">{{ row.title }}</span>
                    <el-tag v-if="!row.isRead" type="danger" size="small" effect="plain">未读</el-tag>
                  </div>
                  <div class="notification-summary">{{ row.summary }}</div>
                  <div class="notification-time">{{ row.createTime }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.isRead ? 'info' : 'danger'" size="small">
                {{ row.isRead ? '已读' : '未读' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleViewDetail(row)">查看</el-button>
              <el-button
                v-if="!row.isRead"
                type="success"
                link
                size="small"
                @click="handleMarkAsRead(row)"
              >
                标记已读
              </el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无通知" />
          </template>
        </el-table>
      </div>
    </el-card>

    <el-dialog
      v-model="detailDialogVisible"
      title="通知详情"
      width="600px"
    >
      <div class="notification-detail">
        <div class="detail-header">
          <div
            class="detail-icon"
            :style="{ backgroundColor: getNotificationIconColor(currentNotification.type) + '20', color: getNotificationIconColor(currentNotification.type) }"
          >
            <el-icon :size="32">
              <component :is="getNotificationIcon(currentNotification.type)" />
            </el-icon>
          </div>
          <div class="detail-title-wrapper">
            <h2 class="detail-title">{{ currentNotification.title }}</h2>
            <div class="detail-meta">
              <el-tag :type="currentNotification.type === 'system' ? 'primary' : currentNotification.type === 'order' ? 'success' : currentNotification.type === 'appointment' ? 'warning' : 'danger'" size="small">
                {{ getTypeLabel(currentNotification.type) }}
              </el-tag>
              <span class="detail-time">{{ currentNotification.createTime }}</span>
            </div>
          </div>
        </div>
        <el-divider />
        <div class="detail-content">{{ currentNotification.content }}</div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.notifications {
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 24px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.notification-tabs {
  flex: 1;
}

.notification-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.batch-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 16px;
}

.selected-count {
  color: #606266;
  font-size: 14px;
}

.notification-list {
  min-height: 200px;
}

.notification-item-row {
  display: flex;
  align-items: flex-start;
  padding: 12px;
  cursor: pointer;
  transition: background-color 0.3s;
  border-radius: 4px;
}

.notification-item-row:hover {
  background-color: #f5f7fa;
}

.notification-item-row.unread {
  background-color: #ecf5ff;
}

.notification-item-row.unread:hover {
  background-color: #e6f0ff;
}

.notification-icon-wrapper {
  margin-right: 12px;
}

.notification-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  flex-shrink: 0;
}

.notification-content-wrapper {
  flex: 1;
  min-width: 0;
}

.notification-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.notification-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.notification-summary {
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.notification-detail {
  padding: 10px 0;
}

.detail-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.detail-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 12px;
  flex-shrink: 0;
}

.detail-title-wrapper {
  flex: 1;
}

.detail-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 8px 0;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-time {
  font-size: 14px;
  color: #909399;
}

.detail-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-left {
    width: 100%;
  }

  .notification-tabs {
    width: 100%;
  }

  .header-right {
    width: 100%;
  }

  .header-right .el-button {
    width: 100%;
  }
}
</style>
