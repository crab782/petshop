<script setup lang="ts">
defineOptions({
  name: 'MerchantAudit'
})

import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPendingMerchants, auditMerchant, type Merchant } from '@/api/admin'
import { Check, Close, View, Search } from '@element-plus/icons-vue'

const loading = ref(false)
const merchants = ref<Merchant[]>([])
const detailDialogVisible = ref(false)
const auditDialogVisible = ref(false)
const selectedMerchant = ref<Merchant | null>(null)
const rejectReason = ref('')
const auditLoading = ref(false)
const searchKeyword = ref('')
const selectedRows = ref<Merchant[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchPendingMerchants = async () => {
  loading.value = true
  try {
    const url = new URL('/api/admin/merchants/pending', window.location.origin)
    url.searchParams.append('page', currentPage.value.toString())
    url.searchParams.append('pageSize', pageSize.value.toString())
    if (searchKeyword.value) {
      url.searchParams.append('keyword', searchKeyword.value)
    }
    const response = await fetch(url)
    if (!response.ok) throw new Error('获取待审核商家列表失败')
    const data = await response.json()
    merchants.value = data.map((merchant: any) => ({
      id: merchant.id,
      name: merchant.name,
      contactPerson: merchant.contactPerson,
      phone: merchant.phone,
      address: merchant.address,
      createTime: merchant.createdAt ? new Date(merchant.createdAt).toLocaleString('zh-CN') : ''
    }))
    total.value = merchants.value.length
  } catch (error) {
    ElMessage.error('获取待审核商家列表失败')
    console.error('Error fetching pending merchants:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchPendingMerchants()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchPendingMerchants()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  fetchPendingMerchants()
}

const handleSelectionChange = (rows: Merchant[]) => {
  selectedRows.value = rows
}

const handleBatchApprove = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要操作的商家')
    return
  }
  try {
    await ElMessageBox.confirm(`确认通过选中的 ${selectedRows.value.length} 家商家审核?`, '批量审核确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    })
    auditLoading.value = true
    for (const row of selectedRows.value) {
      const response = await fetch(`/api/admin/merchants/${row.id}/audit`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ status: 'approved' })
      })
      if (!response.ok) throw new Error('操作失败')
    }
    ElMessage.success('批量审核已通过')
    fetchPendingMerchants()
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
      console.error('Error batch approving merchants:', err)
    }
  } finally {
    auditLoading.value = false
  }
}

const handleBatchReject = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要操作的商家')
    return
  }
  try {
    await ElMessageBox.confirm(`确认拒绝选中的 ${selectedRows.value.length} 家商家?`, '批量审核确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    auditLoading.value = true
    for (const row of selectedRows.value) {
      const response = await fetch(`/api/admin/merchants/${row.id}/audit`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ status: 'rejected' })
      })
      if (!response.ok) throw new Error('操作失败')
    }
    ElMessage.success('批量已拒绝')
    fetchPendingMerchants()
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
      console.error('Error batch rejecting merchants:', err)
    }
  } finally {
    auditLoading.value = false
  }
}

const handleViewDetail = (row: Merchant) => {
  selectedMerchant.value = row
  detailDialogVisible.value = true
}

const handleAudit = (row: Merchant) => {
  selectedMerchant.value = row
  rejectReason.value = ''
  auditDialogVisible.value = true
}

const handleApprove = async () => {
  if (!selectedMerchant.value) return
  try {
    await ElMessageBox.confirm('确认通过该商家审核?', '审核确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    })
    auditLoading.value = true
    const response = await fetch(`/api/admin/merchants/${selectedMerchant.value.id}/audit`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ status: 'approved' })
    })
    if (!response.ok) throw new Error('操作失败')
    ElMessage.success('商家审核已通过')
    fetchPendingMerchants()
    auditDialogVisible.value = false
    detailDialogVisible.value = false
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
      console.error('Error approving merchant:', err)
    }
  } finally {
    auditLoading.value = false
  }
}

const handleReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写拒绝原因')
    return
  }
  if (!selectedMerchant.value) return
  try {
    auditLoading.value = true
    const response = await fetch(`/api/admin/merchants/${selectedMerchant.value.id}/audit`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ 
        status: 'rejected',
        reason: rejectReason.value 
      })
    })
    if (!response.ok) throw new Error('操作失败')
    ElMessage.success('已拒绝该商家')
    fetchPendingMerchants()
    auditDialogVisible.value = false
    detailDialogVisible.value = false
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('Error rejecting merchant:', error)
  } finally {
    auditLoading.value = false
  }
}

onMounted(() => {
  fetchPendingMerchants()
})
</script>

<template>
  <div class="audit-page">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>商家审核</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="search-card">
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商家名称"
          :prefix-icon="Search"
          clearable
          style="max-width: 300px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <div class="batch-actions">
          <el-button
            type="success"
            :disabled="selectedRows.length === 0"
            :loading="auditLoading"
            @click="handleBatchApprove"
          >
            批量通过
          </el-button>
          <el-button
            type="danger"
            :disabled="selectedRows.length === 0"
            :loading="auditLoading"
            @click="handleBatchReject"
          >
            批量拒绝
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="title">待审核商家列表</span>
          <el-button type="primary" @click="fetchPendingMerchants" :loading="loading">
            刷新列表
          </el-button>
        </div>
      </template>

      <el-table
        :data="merchants"
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
        <el-table-column prop="phone" label="电话" width="130">
          <template #default="{ row }">
            {{ row.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              type="success"
              size="small"
              :icon="Check"
              @click="handleAudit(row)"
            >
              审核通过
            </el-button>
            <el-button
              type="danger"
              size="small"
              :icon="Close"
              @click="handleAudit(row)"
            >
              审核拒绝
            </el-button>
            <el-button
              type="primary"
              size="small"
              :icon="View"
              @click="handleViewDetail(row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: flex-end;"
      />

      <el-empty v-if="!loading && merchants.length === 0" description="暂无待审核商家" />
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="商家详情" width="600px">
      <div v-if="selectedMerchant" class="merchant-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="商家ID">{{ selectedMerchant.id }}</el-descriptions-item>
          <el-descriptions-item label="商家名称">{{ selectedMerchant.name }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ selectedMerchant.contactPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ selectedMerchant.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">{{ selectedMerchant.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ selectedMerchant.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间" :span="2">{{ selectedMerchant.createTime }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="selectedMerchant.licenseImage" class="license-section">
          <h4>营业执照</h4>
          <el-image
            :src="selectedMerchant.licenseImage"
            :preview-src-list="[selectedMerchant.licenseImage]"
            fit="contain"
            style="max-width: 100%; max-height: 300px;"
          />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button
            type="success"
            :loading="auditLoading"
            @click="handleApprove"
          >
            审核通过
          </el-button>
          <el-button
            type="danger"
            :loading="auditLoading"
            @click="handleAudit(selectedMerchant!)"
          >
            审核拒绝
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="auditDialogVisible" title="商家审核" width="600px">
      <div v-if="selectedMerchant" class="audit-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="商家ID">{{ selectedMerchant.id }}</el-descriptions-item>
          <el-descriptions-item label="商家名称">{{ selectedMerchant.name }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ selectedMerchant.contactPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ selectedMerchant.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">{{ selectedMerchant.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ selectedMerchant.description || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="selectedMerchant.licenseImage" class="license-section">
          <h4>营业执照</h4>
          <el-image
            :src="selectedMerchant.licenseImage"
            :preview-src-list="[selectedMerchant.licenseImage]"
            fit="contain"
            style="max-width: 100%; max-height: 300px;"
          />
        </div>

        <el-form label-position="top" class="reject-form">
          <el-form-item label="拒绝原因">
            <el-input
              v-model="rejectReason"
              type="textarea"
              :rows="3"
              placeholder="请输入拒绝原因（审核拒绝时必填）"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button
            type="success"
            :loading="auditLoading"
            @click="handleApprove"
          >
            通过
          </el-button>
          <el-button
            type="danger"
            :loading="auditLoading"
            @click="handleReject"
          >
            拒绝
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.audit-page {
  padding: 0;
}

.search-card {
  margin-top: 20px;
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.batch-actions {
  margin-left: auto;
  display: flex;
  gap: 10px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: 600;
}

.merchant-detail {
  padding: 10px 0;
}

.license-section {
  margin-top: 20px;
}

.license-section h4 {
  margin-bottom: 10px;
  font-size: 14px;
  color: #606266;
}

.audit-content {
  padding: 10px 0;
}

.reject-form {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
