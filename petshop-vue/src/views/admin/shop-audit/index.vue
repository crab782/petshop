<script setup lang="ts">
defineOptions({
  name: 'ShopAudit'
})

import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPendingShops, auditShop, type Shop } from '@/api/admin'
import { Check, Close, View, Search } from '@element-plus/icons-vue'

const loading = ref(false)
const shops = ref<Shop[]>([])
const detailDialogVisible = ref(false)
const auditDialogVisible = ref(false)
const selectedShop = ref<Shop | null>(null)
const rejectReason = ref('')
const auditLoading = ref(false)
const searchKeyword = ref('')
const selectedRows = ref<Shop[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchPendingShops = async () => {
  loading.value = true
  try {
    const res = await getPendingShops({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    })
    shops.value = res.data || []
    total.value = res.total || shops.value.length
  } catch {
    ElMessage.error('获取待审核店铺列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchPendingShops()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchPendingShops()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  fetchPendingShops()
}

const handleSelectionChange = (rows: Shop[]) => {
  selectedRows.value = rows
}

const handleBatchApprove = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要操作的店铺')
    return
  }
  try {
    await ElMessageBox.confirm(`确认通过选中的 ${selectedRows.value.length} 家店铺审核?`, '批量审核确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    })
    auditLoading.value = true
    for (const row of selectedRows.value) {
      await auditShop(row.id, 'approved')
    }
    ElMessage.success('批量审核已通过')
    fetchPendingShops()
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  } finally {
    auditLoading.value = false
  }
}

const handleBatchReject = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要操作的店铺')
    return
  }
  try {
    await ElMessageBox.confirm(`确认拒绝选中的 ${selectedRows.value.length} 家店铺?`, '批量审核确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    auditLoading.value = true
    for (const row of selectedRows.value) {
      await auditShop(row.id, 'rejected')
    }
    ElMessage.success('批量已拒绝')
    fetchPendingShops()
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  } finally {
    auditLoading.value = false
  }
}

const handleViewDetail = (row: Shop) => {
  selectedShop.value = row
  detailDialogVisible.value = true
}

const handleAudit = (row: Shop) => {
  selectedShop.value = row
  rejectReason.value = ''
  auditDialogVisible.value = true
}

const handleApprove = async () => {
  if (!selectedShop.value) return
  try {
    await ElMessageBox.confirm('确认通过该店铺审核?', '审核确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    })
    auditLoading.value = true
    await auditShop(selectedShop.value.id, 'approved')
    ElMessage.success('店铺审核已通过')
    fetchPendingShops()
    auditDialogVisible.value = false
    detailDialogVisible.value = false
  } catch (err: unknown) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
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
  if (!selectedShop.value) return
  try {
    auditLoading.value = true
    await auditShop(selectedShop.value.id, 'rejected', rejectReason.value)
    ElMessage.success('已拒绝该店铺')
    fetchPendingShops()
    auditDialogVisible.value = false
    detailDialogVisible.value = false
  } catch {
    ElMessage.error('操作失败')
  } finally {
    auditLoading.value = false
  }
}

onMounted(() => {
  fetchPendingShops()
})
</script>

<template>
  <div class="audit-page">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>店铺审核</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="search-card">
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索店铺名称"
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
          <span class="title">店铺审核</span>
          <el-button type="primary" @click="fetchPendingShops" :loading="loading">
            刷新列表
          </el-button>
        </div>
      </template>

      <el-table
        :data="shops"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="店铺ID" width="80" />
        <el-table-column prop="name" label="店铺名称" min-width="150" />
        <el-table-column prop="applicant" label="申请人" width="120" />
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

      <el-empty v-if="!loading && shops.length === 0" description="暂无待审核店铺" />
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="店铺详情" width="600px">
      <div v-if="selectedShop" class="shop-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="店铺ID">{{ selectedShop.id }}</el-descriptions-item>
          <el-descriptions-item label="店铺名称">{{ selectedShop.name }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ selectedShop.applicant }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ selectedShop.applicantPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">{{ selectedShop.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ selectedShop.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间" :span="2">{{ selectedShop.createTime }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="selectedShop.licenseImage" class="license-section">
          <h4>营业执照</h4>
          <el-image
            :src="selectedShop.licenseImage"
            :preview-src-list="[selectedShop.licenseImage]"
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
            @click="handleAudit(selectedShop!)"
          >
            审核拒绝
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="auditDialogVisible" title="店铺审核" width="600px">
      <div v-if="selectedShop" class="audit-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="店铺ID">{{ selectedShop.id }}</el-descriptions-item>
          <el-descriptions-item label="店铺名称">{{ selectedShop.name }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ selectedShop.applicant }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ selectedShop.applicantPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">{{ selectedShop.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ selectedShop.description || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="selectedShop.licenseImage" class="license-section">
          <h4>营业执照</h4>
          <el-image
            :src="selectedShop.licenseImage"
            :preview-src-list="[selectedShop.licenseImage]"
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

.shop-detail {
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
