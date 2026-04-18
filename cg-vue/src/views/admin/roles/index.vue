<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoles, addRole, updateRole, deleteRole, getPermissions } from '@/api/admin'
import type { Role, Permission } from '@/api/admin'
import { Plus, Edit, Delete, Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const roleList = ref<Role[]>([])
const permissionList = ref<Permission[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加角色')
const isEdit = ref(false)
const currentRoleId = ref<number | null>(null)
const searchQuery = ref('')
const selectedRows = ref<Role[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const formData = ref({
  name: '',
  description: '',
  permissions: [] as number[]
})

const formRef = ref()

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

const filteredRoles = computed(() => {
  let result = roleList.value
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = roleList.value.filter(role =>
      role.name.toLowerCase().includes(query) ||
      (role.description && role.description.toLowerCase().includes(query))
    )
  }
  return result
})

const paginatedRoles = computed(() => {
  const start = (pagination.value.current - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  pagination.value.total = filteredRoles.value.length
  return filteredRoles.value.slice(start, end)
})

const loadRoles = async () => {
  loading.value = true
  try {
    const data = await getRoles()
    roleList.value = data
    pagination.value.total = data.length
  } catch (error) {
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

const loadPermissions = async () => {
  try {
    const data = await getPermissions()
    permissionList.value = data
  } catch (error) {
    ElMessage.error('加载权限列表失败')
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  pagination.value.total = filteredRoles.value.length
}

const handleReset = () => {
  searchQuery.value = ''
  pagination.value.current = 1
  pagination.value.total = roleList.value.length
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
}

const handlePageSizeChange = (size: number) => {
  pagination.value.pageSize = size
  pagination.value.current = 1
}

const handleAdd = () => {
  dialogTitle.value = '添加角色'
  isEdit.value = false
  currentRoleId.value = null
  formData.value = {
    name: '',
    description: '',
    permissions: []
  }
  dialogVisible.value = true
}

const handleEdit = (row: Role) => {
  dialogTitle.value = '编辑角色'
  isEdit.value = true
  currentRoleId.value = row.id
  formData.value = {
    name: row.name,
    description: row.description || '',
    permissions: row.permissions || []
  }
  dialogVisible.value = true
}

const handleDelete = async (row: Role) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色 "${row.name}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadRoles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSelectionChange = (selection: Role[]) => {
  selectedRows.value = selection
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的角色')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个角色吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    for (const row of selectedRows.value) {
      await deleteRole(row.id)
    }
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    loadRoles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    try {
      if (isEdit.value && currentRoleId.value) {
        await updateRole(currentRoleId.value, {
          name: formData.value.name,
          description: formData.value.description,
          permissions: formData.value.permissions
        })
        ElMessage.success('更新成功')
      } else {
        await addRole({
          name: formData.value.name,
          description: formData.value.description,
          permissions: formData.value.permissions
        })
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadRoles()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    }
  })
}

const handlePermissionsChange = (selectedKeys: number[]) => {
  formData.value.permissions = selectedKeys
}

onMounted(() => {
  loadRoles()
  loadPermissions()
})
</script>

<template>
  <div class="role-management">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>角色权限管理</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" class="search-form">
        <el-form-item label="角色搜索">
          <el-input
            v-model="searchQuery"
            placeholder="请输入角色名称"
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
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          添加角色
        </el-button>
        <el-button
          type="danger"
          :icon="Delete"
          :disabled="selectedRows.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </div>

      <el-table
        :data="paginatedRoles"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="角色ID" width="100" />
        <el-table-column prop="name" label="角色名称" min-width="150" />
        <el-table-column prop="description" label="角色描述" min-width="200">
          <template #default="{ row }">
            {{ row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="permissionCount" label="权限数量" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入角色名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            placeholder="请输入角色描述"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="权限配置">
          <el-tree
            :data="permissionList"
            :props="{
              label: 'name',
              children: 'children'
            }"
            node-key="id"
            :default-checked-keys="formData.permissions"
            show-checkbox
            check-strictly
            @check-change="(node: Permission, checked: boolean) => {
              if (checked) {
                if (!formData.permissions.includes(node.id)) {
                  formData.permissions.push(node.id)
                }
              } else {
                const idx = formData.permissions.indexOf(node.id)
                if (idx > -1) formData.permissions.splice(idx, 1)
              }
            }"
            style="max-height: 300px; overflow-y: auto; border: 1px solid #dcdfe6; border-radius: 4px; padding: 10px;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.role-management {
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

.toolbar {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
