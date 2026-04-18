<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantDetailById, updateMerchantStatus, type MerchantDetail } from '@/api/admin'
import { ArrowLeft, Check, Close, View, Goods, Ticket } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const merchantDetail = ref<MerchantDetail | null>(null)

const merchantId = () => Number(route.params.id)

const fetchMerchantDetail = async () => {
  if (!merchantId()) return
  loading.value = true
  try {
    const res = await getMerchantDetailById(merchantId())
    merchantDetail.value = res.data
  } catch {
    ElMessage.error('获取店铺详情失败')
  } finally {
    loading.value = false
  }
}

const handleDisableMerchant = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要禁用店铺 "${merchantDetail.value?.name}" 吗？`,
      '禁用确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await updateMerchantStatus(merchantId(), 'disabled')
    ElMessage.success('店铺已禁用')
    fetchMerchantDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleEnableMerchant = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要启用店铺 "${merchantDetail.value?.name}" 吗？`,
      '启用确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    await updateMerchantStatus(merchantId(), 'approved')
    ElMessage.success('店铺已启用')
    fetchMerchantDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleViewServices = () => {
  router.push({ path: '/admin/services', query: { merchantId: String(merchantId()) } })
}

const handleViewProducts = () => {
  router.push({ path: '/admin/products', query: { merchantId: String(merchantId()) } })
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  fetchMerchantDetail()
})
</script>

<template>
  <div class="merchant-detail">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/merchants' }">商家管理</el-breadcrumb-item>
      <el-breadcrumb-item>店铺详情</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="back-card">
      <el-button :icon="ArrowLeft" @click="handleBack">返回</el-button>
    </el-card>

    <el-card v-loading="loading" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">店铺详情</span>
          <div class="action-buttons">
            <el-button
              v-if="merchantDetail?.status === 'approved'"
              type="danger"
              :icon="Close"
              @click="handleDisableMerchant"
            >
              禁用店铺
            </el-button>
            <el-button
              v-else-if="merchantDetail?.status === 'disabled'"
              type="success"
              :icon="Check"
              @click="handleEnableMerchant"
            >
              启用店铺
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="merchantDetail">
        <el-card class="info-card" shadow="never">
          <template #header>
            <span class="section-title">基本信息</span>
          </template>
          <div class="merchant-header">
            <el-avatar :size="80" :src="merchantDetail.avatar || '/placeholder-merchant.png'" />
            <div class="merchant-header-info">
              <h2>{{ merchantDetail.name }}</h2>
              <el-tag
                :type="merchantDetail.status === 'approved' ? 'success' : merchantDetail.status === 'pending' ? 'warning' : 'danger'"
                size="small"
              >
                {{ merchantDetail.status === 'approved' ? '营业中' : merchantDetail.status === 'pending' ? '待审核' : '已禁用' }}
              </el-tag>
            </div>
          </div>
          <el-descriptions :column="2" border style="margin-top: 20px">
            <el-descriptions-item label="店铺ID">{{ merchantDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="店铺名称">{{ merchantDetail.name }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ merchantDetail.contactPerson || '-' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ merchantDetail.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="店铺地址" :span="2">{{ merchantDetail.address || '-' }}</el-descriptions-item>
            <el-descriptions-item label="营业时间">{{ merchantDetail.businessHours || '-' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ merchantDetail.createTime }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card class="info-card" shadow="never">
          <template #header>
            <span class="section-title">店铺统计</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <el-icon class="stat-icon" color="#409EFF"><Ticket /></el-icon>
                <div class="stat-content">
                  <div class="stat-value">{{ merchantDetail.serviceCount || 0 }}</div>
                  <div class="stat-label">服务数量</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <el-icon class="stat-icon" color="#67C23A"><Goods /></el-icon>
                <div class="stat-content">
                  <div class="stat-value">{{ merchantDetail.productCount || 0 }}</div>
                  <div class="stat-label">商品数量</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <el-icon class="stat-icon" color="#E6A23C"><View /></el-icon>
                <div class="stat-content">
                  <div class="stat-value">{{ merchantDetail.orderCount || 0 }}</div>
                  <div class="stat-label">订单数量</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <el-icon class="stat-icon" color="#F56C6C"><Check /></el-icon>
                <div class="stat-content">
                  <div class="stat-value">{{ merchantDetail.rating || '-' }}</div>
                  <div class="stat-label">评分</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <el-card class="info-card" shadow="never">
          <template #header>
            <span class="section-title">快捷操作</span>
          </template>
          <div class="action-grid">
            <el-button type="primary" :icon="Ticket" @click="handleViewServices">
              查看服务列表
            </el-button>
            <el-button type="success" :icon="Goods" @click="handleViewProducts">
              查看商品列表
            </el-button>
          </div>
        </el-card>
      </div>

      <el-empty v-else description="店铺不存在" />
    </el-card>
  </div>
</template>

<style scoped>
.merchant-detail {
  padding: 0;
}

.back-card {
  margin-top: 20px;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.info-card {
  margin-bottom: 20px;
}

.section-title {
  font-weight: bold;
  font-size: 16px;
}

.merchant-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.merchant-header-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.merchant-header-info h2 {
  margin: 0;
  font-size: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-icon {
  font-size: 32px;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.action-grid {
  display: flex;
  gap: 15px;
}
</style>
