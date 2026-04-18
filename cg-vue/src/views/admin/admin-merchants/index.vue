<script setup lang="ts">
defineOptions({
  name: 'AdminMerchants'
})

import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllMerchants, updateMerchantStatus, deleteMerchant, auditMerchant, type Merchant } from '@/api/admin'
import { Search, Refresh, Check, Close, View, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const merchants = ref<Merchant[]>([])
const dialogVisible = ref(false)
const rejectDialogVisible = ref(false)
const selectedMerchant = ref<Merchant | null>(null)
const rejectReason = ref('')
const selectedIds = ref<number[]>([])

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const filteredMerchants = ref<Merchant[]>([])

const statusMap: Record<string, { label: string; type: string }> = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已拒绝', type: 'danger' }
}

const fetchMerchants = async () => {
  loading.value = true
  try {
    const res = await getAllMerchants()
    merchants.value = res.data || []
    handleSearch()
  } catch {
    ElMessage.error('获取商家列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  let result = merchants.value
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(m =>
      m.name.toLowerCase().includes(keyword) ||
      (m.phone && m.phone.includes(searchKeyword.value)) ||
      (m.contactPerson && m.contactPerson.toLowerCase().includes(keyword))
    )
  }
  if (statusFilter.value) {
    result = result.filter(m => m.status === statusFilter.value)
  }
  filteredMerchants.value = result
  pagination.value.total = result.length
  pagination.value.current = 1
}

const handleReset = () => {
  searchKeyword.value = ''
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

const handleSelectionChange = (selection: Merchant[]) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleViewDetail = (row: Merchant) => {
  selectedMerchant.value = row
  dialogVisible.value = true
}

const handleApprove = async (row: Merchant) => {
  try {
    await ElMessageBox.confirm('确认通过该商家审核?', '审核确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    })
    await auditMerchant(row.id, 'approved')
    ElMessage.success('商家审核已通过')
    fetchMerchants()
    dialogVisible.value = false
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleReject = (row: Merchant) => {
  selectedMerchant.value = row
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写拒绝原因')
    return
  }
  try {
    await auditMerchant(selectedMerchant.value!.id, 'rejected', rejectReason.value)
    ElMessage.success('已拒绝该商家')
    fetchMerchants()
    rejectDialogVisible.value = false
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row: Merchant) => {
  try {
    await ElMessageBox.confirm(`确定要删除商家 "${row.name}" 吗？`, '删除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteMerchant(row.id)
    ElMessage.success('删除成功')
    fetchMerchants()
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleBatchAudit = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要审核的商家')
    return
  }
  const pendingSelected = merchants.value.filter(m => selectedIds.value.includes(m.id) && m.status === 'pending')
  if (pendingSelected.length === 0) {
    ElMessage.warning('选中的商家中没有待审核的')
    return
  }
  try {
    await ElMessageBox.confirm(`确认通过选中的 ${pendingSelected.length} 个商家审核?`, '批量审核', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    })
    for (const merchant of pendingSelected) {
      await auditMerchant(merchant.id, 'approved')
    }
    ElMessage.success('批量审核成功')
    selectedIds.value = []
    fetchMerchants()
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleBatchEnable = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要启用的商家')
    return
  }
  try {
    await ElMessageBox.confirm(`确认启用选中的 ${selectedIds.value.length} 个商家?`, '批量启用', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    })
    for (const id of selectedIds.value) {
      await updateMerchantStatus(id, 'approved')
    }
    ElMessage.success('批量启用成功')
    selectedIds.value = []
    fetchMerchants()
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleBatchDisable = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要禁用的商家')
    return
  }
  try {
    await ElMessageBox.confirm(`确认禁用选中的 ${selectedIds.value.length} 个商家?`, '批量禁用', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    for (const id of selectedIds.value) {
      await updateMerchantStatus(id, 'disabled')
    }
    ElMessage.success('批量禁用成功')
    selectedIds.value = []
    fetchMerchants()
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要删除的商家')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个商家吗?此操作不可恢复。`, '批量删除', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    for (const id of selectedIds.value) {
      await deleteMerchant(id)
    }
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    fetchMerchants()
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  fetchMerchants()
})
</script>

<template>
  <div class="merchants-page">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>商家管理</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" class="search-form">
        <el-form-item label="关键词搜索">
          <el-input
            v-model="searchKeyword"
            placeholder="请输入商家名称/联系人/手机号"
            clearable
            style="width: 250px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="状态筛选">
          <el-select v-model="statusFilter" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
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
      <div class="batch-actions">
        <el-button type="success" size="small" @click="handleBatchAudit" :disabled="selectedIds.length === 0">
          <el-icon><Check /></el-icon> 批量审核
        </el-button>
        <el-button type="primary" size="small" @click="handleBatchEnable" :disabled="selectedIds.length === 0">
          <el-icon><Check /></el-icon> 批量启用
        </el-button>
        <el-button type="warning" size="small" @click="handleBatchDisable" :disabled="selectedIds.length === 0">
          <el-icon><Close /></el-icon> 批量禁用
        </el-button>
        <el-button type="danger" size="small" @click="handleBatchDelete" :disabled="selectedIds.length === 0">
          <el-icon><Delete /></el-icon> 批量删除
        </el-button>
      </div>

      <el-table
        :data="filteredMerchants.slice((pagination.current - 1) * pagination.pageSize, pagination.current * pagination.pageSize)"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="商家ID" width="80" />
        <el-table-column prop="name" label="商家名称" min-width="150" />
        <el-table-column prop="contactPerson" label="联系人" width="120">
          <template #default="{ row }">
            {{ row.contactPerson || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="address" label="地址" min-width="180">
          <template #default="{ row }">
            {{ row.address || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'" size="small">
              {{ statusMap[row.status]?.label || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              size="small"
              :icon="Check"
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              size="small"
              :icon="Close"
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button
              type="primary"
              size="small"
              :icon="View"
              @click="handleViewDetail(row)"
            >
              详情
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

    <el-dialog v-model="dialogVisible" title="商家详情" width="500px">
      <div v-if="selectedMerchant" class="merchant-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="商家ID">{{ selectedMerchant.id }}</el-descriptions-item>
          <el-descriptions-item label="商家名称">{{ selectedMerchant.name }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ selectedMerchant.contactPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ selectedMerchant.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">{{ selectedMerchant.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ selectedMerchant.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusMap[selectedMerchant.status]?.type || 'info'" size="small">
              {{ statusMap[selectedMerchant.status]?.label || selectedMerchant.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评分">{{ selectedMerchant.rating || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ selectedMerchant.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button
            v-if="selectedMerchant?.status === 'pending'"
            type="success"
            @click="handleApprove(selectedMerchant!)"
          >
            审核通过
          </el-button>
          <el-button
            v-if="selectedMerchant?.status === 'pending'"
            type="danger"
            @click="handleReject(selectedMerchant!)"
          >
            审核拒绝
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="400px">
      <el-form label-position="top">
        <el-form-item label="请填写拒绝原因">
          <el-input
            v-model="rejectReason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.merchants-page {
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
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.merchant-detail {
  padding: 10px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
