<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAnnouncements,
  addAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
  publishAnnouncement,
  unpublishAnnouncement
} from '@/api/announcement'
import type { Announcement } from '@/api/announcement'
import { Plus, Edit, Delete, Search, Refresh, View } from '@element-plus/icons-vue'

const loading = ref(false)
const announcementList = ref<Announcement[]>([])
const searchQuery = ref('')
const statusFilter = ref('')
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const selectedRows = ref<Announcement[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加公告')
const isEdit = ref(false)
const currentId = ref<number | null>(null)
const formData = ref({
  title: '',
  content: ''
})

const statusMap: Record<string, { label: string; type: string }> = {
  published: { label: '已发布', type: 'success' },
  draft: { label: '草稿', type: 'info' }
}

const filteredAnnouncements = computed(() => {
  let result = announcementList.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(item =>
      item.title.toLowerCase().includes(query)
    )
  }

  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value)
  }

  return result
})

const paginatedAnnouncements = computed(() => {
  const start = (pagination.value.current - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  return filteredAnnouncements.value.slice(start, end)
})

const loadAnnouncements = async () => {
  loading.value = true
  try {
    const data = await getAnnouncements()
    announcementList.value = data
    pagination.value.total = filteredAnnouncements.value.length
    pagination.value.current = 1
  } catch (error) {
    ElMessage.error('加载公告列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.total = filteredAnnouncements.value.length
  pagination.value.current = 1
}

const handleReset = () => {
  searchQuery.value = ''
  statusFilter.value = ''
  handleSearch()
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
}

const handlePageSizeChange = (size: number) => {
  pagination.value.pageSize = size
  pagination.value.current = 1
}

const handleSelectionChange = (rows: Announcement[]) => {
  selectedRows.value = rows
}

const handleAdd = () => {
  dialogTitle.value = '添加公告'
  isEdit.value = false
  currentId.value = null
  formData.value = { title: '', content: '' }
  dialogVisible.value = true
}

const handleEdit = (row: Announcement) => {
  dialogTitle.value = '编辑公告'
  isEdit.value = true
  currentId.value = row.id
  formData.value = { title: row.title, content: row.content }
  dialogVisible.value = true
}

const handleDelete = async (row: Announcement) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除公告 "${row.title}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteAnnouncement(row.id)
    ElMessage.success('删除成功')
    loadAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handlePublish = async (row: Announcement) => {
  try {
    await ElMessageBox.confirm(
      `确定要发布公告 "${row.title}" 吗？`,
      '发布确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    await publishAnnouncement(row.id)
    ElMessage.success('发布成功')
    loadAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

const handleUnpublish = async (row: Announcement) => {
  try {
    await ElMessageBox.confirm(
      `确定要下架公告 "${row.title}" 吗？`,
      '下架确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await unpublishAnnouncement(row.id)
    ElMessage.success('下架成功')
    loadAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('下架失败')
    }
  }
}

const handleBatchPublish = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要发布的公告')
    return
  }

  const drafts = selectedRows.value.filter(row => row.status === 'draft')
  if (drafts.length === 0) {
    ElMessage.info('所选公告都已发布')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要发布选中的 ${drafts.length} 篇公告吗？`,
      '批量发布确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    for (const row of drafts) {
      await publishAnnouncement(row.id)
    }
    ElMessage.success(`成功发布 ${drafts.length} 篇公告`)
    selectedRows.value = []
    loadAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量发布失败')
    }
  }
}

const handleBatchUnpublish = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要下架的公告')
    return
  }

  const published = selectedRows.value.filter(row => row.status === 'published')
  if (published.length === 0) {
    ElMessage.info('所选公告都已下架')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要下架选中的 ${published.length} 篇公告吗？`,
      '批量下架确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    for (const row of published) {
      await unpublishAnnouncement(row.id)
    }
    ElMessage.success(`成功下架 ${published.length} 篇公告`)
    selectedRows.value = []
    loadAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量下架失败')
    }
  }
}

const handleSave = async () => {
  if (!formData.value.title.trim()) {
    ElMessage.warning('请输入公告标题')
    return
  }
  if (!formData.value.content.trim()) {
    ElMessage.warning('请输入公告内容')
    return
  }

  try {
    if (isEdit.value && currentId.value !== null) {
      await updateAnnouncement(currentId.value, formData.value)
      ElMessage.success('更新成功')
    } else {
      await addAnnouncement(formData.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadAnnouncements()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const getContentSummary = (content: string, maxLength = 50) => {
  if (content.length <= maxLength) return content
  return content.substring(0, maxLength) + '...'
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<template>
  <div class="announcement-management">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>公告管理</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" class="search-form">
        <el-form-item label="关键词搜索">
          <el-input
            v-model="searchQuery"
            placeholder="请输入公告标题"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="状态筛选">
          <el-select
            v-model="statusFilter"
            placeholder="全部状态"
            clearable
            style="width: 150px"
          >
            <el-option label="全部状态" value="" />
            <el-option label="已发布" value="published" />
            <el-option label="草稿" value="draft" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button
              type="primary"
              @click="handleAdd"
              :disabled="selectedRows.length > 0"
            >
              <el-icon><Plus /></el-icon> 添加公告
            </el-button>
            <el-button
              type="success"
              @click="handleBatchPublish"
              :disabled="selectedRows.length === 0"
            >
              批量发布
            </el-button>
            <el-button
              type="warning"
              @click="handleBatchUnpublish"
              :disabled="selectedRows.length === 0"
            >
              批量下架
            </el-button>
          </div>
          <div class="header-right" v-if="selectedRows.length > 0">
            <span class="selected-count">已选择 {{ selectedRows.length }} 项</span>
          </div>
        </div>
      </template>

      <el-table
        :data="paginatedAnnouncements"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="公告ID" width="100" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column label="内容摘要" min-width="250">
          <template #default="{ row }">
            {{ getContentSummary(row.content) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="statusMap[row.status]?.type || 'info'"
              size="small"
            >
              {{ statusMap[row.status]?.label || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'draft'"
              type="success"
              size="small"
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button
              v-else
              type="warning"
              size="small"
              @click="handleUnpublish(row)"
            >
              下架
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" label-width="80px">
        <el-form-item label="标题">
          <el-input
            v-model="formData.title"
            placeholder="请输入公告标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="formData.content"
            type="textarea"
            placeholder="请输入公告内容"
            :rows="6"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.announcement-management {
  padding: 0;
}

.search-card {
  margin-top: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  gap: 10px;
}

.header-right {
  display: flex;
  align-items: center;
}

.selected-count {
  color: #409eff;
  font-size: 14px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
