<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllUsers, updateUserStatus, deleteUser, batchUpdateUserStatus, batchDeleteUsers } from '@/api/admin'
import type { User } from '@/api/admin'
import { Search, Refresh, Check, Close, View } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const userList = ref<User[]>([])
const searchQuery = ref('')
const selectedUsers = ref<User[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const filteredUsers = ref<User[]>([])

const handleSearch = () => {
  let result = userList.value
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = userList.value.filter(user =>
      user.username.toLowerCase().includes(query) ||
      (user.email && user.email.toLowerCase().includes(query))
    )
  }
  filteredUsers.value = result
  pagination.value.total = result.length
  pagination.value.current = 1
}

const handleReset = () => {
  searchQuery.value = ''
  handleSearch()
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
}

const handlePageSizeChange = (size: number) => {
  pagination.value.pageSize = size
  pagination.value.current = 1
}

const handleSelectionChange = (selection: User[]) => {
  selectedUsers.value = selection
}

const handleDelete = async (user: User) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.username}" 吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deleteUser(user.id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleBatchEnable = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要启用的用户')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要启用选中的 ${selectedUsers.value.length} 个用户吗？`,
      '批量启用',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const ids = selectedUsers.value.map(u => u.id)
    await batchUpdateUserStatus(ids, '正常')
    ElMessage.success('批量启用成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量启用失败')
    }
  }
}

const handleBatchDisable = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要禁用的用户')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要禁用选中的 ${selectedUsers.value.length} 个用户吗？`,
      '批量禁用',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const ids = selectedUsers.value.map(u => u.id)
    await batchUpdateUserStatus(ids, '禁用')
    ElMessage.success('批量禁用成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量禁用失败')
    }
  }
}

const handleBatchDelete = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要删除的用户')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？此操作不可恢复！`,
      '批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const ids = selectedUsers.value.map(u => u.id)
    await batchDeleteUsers(ids)
    ElMessage.success('批量删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const handleStatusChange = async (user: User) => {
  const action = user.status === '正常' ? '禁用' : '启用'
  const newStatus = user.status === '正常' ? '禁用' : '正常'

  try {
    await ElMessageBox.confirm(
      `确定要${action}用户 "${user.username}" 吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await updateUserStatus(user.id, newStatus)
    ElMessage.success(`${action}成功`)

    const targetUser = userList.value.find(u => u.id === user.id)
    if (targetUser) {
      targetUser.status = newStatus
    }
    handleSearch()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const loadUsers = async () => {
  loading.value = true
  try {
    const data = await getAllUsers()
    userList.value = data
    handleSearch()
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<template>
  <div class="user-management">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" class="search-form">
        <el-form-item label="关键词搜索">
          <el-input
            v-model="searchQuery"
            placeholder="请输入用户名或邮箱"
            clearable
            style="width: 250px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
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
      <div class="batch-actions">
        <span class="selected-info">已选择 {{ selectedUsers.length }} 项</span>
        <el-button
          type="success"
          size="small"
          :disabled="selectedUsers.length === 0"
          @click="handleBatchEnable"
        >
          批量启用
        </el-button>
        <el-button
          type="warning"
          size="small"
          :disabled="selectedUsers.length === 0"
          @click="handleBatchDisable"
        >
          批量禁用
        </el-button>
        <el-button
          type="danger"
          size="small"
          :disabled="selectedUsers.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </div>

      <el-table
        :data="filteredUsers.slice((pagination.current - 1) * pagination.pageSize, pagination.current * pagination.pageSize)"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180">
          <template #default="{ row }">
            {{ row.email || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130">
          <template #default="{ row }">
            {{ row.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === '正常'" type="success" size="small">
              <el-icon><Check /></el-icon> 正常
            </el-tag>
            <el-tag v-else type="danger" size="small">
              <el-icon><Close /></el-icon> 禁用
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :icon="View"
              @click="router.push({ name: 'admin-user-detail', params: { id: row.id } })"
            >
              详情
            </el-button>
            <el-button
              v-if="row.status === '正常'"
              type="danger"
              size="small"
              @click="handleStatusChange(row)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="handleStatusChange(row)"
            >
              启用
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
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
  </div>
</template>

<style scoped>
.user-management {
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

.batch-actions {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  gap: 12px;
}

.selected-info {
  color: #909399;
  font-size: 14px;
  margin-right: 8px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
