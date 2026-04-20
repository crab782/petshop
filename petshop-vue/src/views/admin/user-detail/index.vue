<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserDetailById, updateUserStatus, type UserDetail } from '@/api/admin'
import { ArrowLeft, Check, Close } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const userDetail = ref<UserDetail | null>(null)
const activeTab = ref('orders')

const userId = () => Number(route.params.id)

const fetchUserDetail = async () => {
  if (!userId()) return
  loading.value = true
  try {
    const response = await fetch(`/api/admin/users/${userId()}`)
    if (!response.ok) throw new Error('获取用户详情失败')
    const user = await response.json()
    userDetail.value = {
      id: user.id,
      username: user.username,
      email: user.email,
      phone: user.phone,
      status: user.status === 'active' ? '正常' : '禁用',
      createTime: user.createdAt ? new Date(user.createdAt).toLocaleString('zh-CN') : '',
      recentOrders: [],
      recentAppointments: [],
      reviews: []
    }
  } catch (error) {
    ElMessage.error('获取用户详情失败')
    console.error('Error fetching user detail:', error)
  } finally {
    loading.value = false
  }
}

const handleDisableAccount = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要禁用用户 "${userDetail.value?.username}" 吗？`,
      '禁用确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const response = await fetch(`/api/admin/users/${userId()}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ status: 'disabled' })
    })
    if (!response.ok) throw new Error('操作失败')
    ElMessage.success('账户已禁用')
    fetchUserDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
      console.error('Error disabling account:', error)
    }
  }
}

const handleEnableAccount = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要启用用户 "${userDetail.value?.username}" 吗？`,
      '启用确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    const response = await fetch(`/api/admin/users/${userId()}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ status: 'active' })
    })
    if (!response.ok) throw new Error('操作失败')
    ElMessage.success('账户已启用')
    fetchUserDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
      console.error('Error enabling account:', error)
    }
  }
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  fetchUserDetail()
})
</script>

<template>
  <div class="user-detail">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/users' }">用户管理</el-breadcrumb-item>
      <el-breadcrumb-item>用户详情</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="back-card">
      <el-button :icon="ArrowLeft" @click="handleBack">返回</el-button>
    </el-card>

    <el-card v-loading="loading" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">用户详情</span>
          <div class="action-buttons">
            <el-button
              v-if="userDetail?.status === '正常'"
              type="danger"
              :icon="Close"
              @click="handleDisableAccount"
            >
              禁用账户
            </el-button>
            <el-button
              v-else
              type="success"
              :icon="Check"
              @click="handleEnableAccount"
            >
              启用账户
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="userDetail">
        <el-card class="info-card" shadow="never">
          <template #header>
            <span class="section-title">基本信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户ID">{{ userDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ userDetail.username }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userDetail.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userDetail.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ userDetail.createTime }}</el-descriptions-item>
            <el-descriptions-item label="账户状态">
              <el-tag v-if="userDetail.status === '正常'" type="success" size="small">
                <el-icon><Check /></el-icon> 正常
              </el-tag>
              <el-tag v-else type="danger" size="small">
                <el-icon><Close /></el-icon> 禁用
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card class="info-card" shadow="never">
          <template #header>
            <span class="section-title">用户行为记录</span>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="最近订单" name="orders">
              <el-table
                :data="userDetail.recentOrders"
                stripe
                style="width: 100%"
                max-height="300"
              >
                <el-table-column prop="orderNo" label="订单编号" width="180" />
                <el-table-column prop="serviceName" label="服务/商品" min-width="120" />
                <el-table-column prop="totalPrice" label="金额" width="100">
                  <template #default="{ row }">
                    ¥{{ row.totalPrice }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag
                      :type="row.status === '已完成' ? 'success' : row.status === '已取消' ? 'danger' : 'warning'"
                      size="small"
                    >
                      {{ row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="下单时间" width="180" />
              </el-table>
              <el-empty v-if="!userDetail.recentOrders?.length" description="暂无订单记录" />
            </el-tab-pane>

            <el-tab-pane label="最近预约" name="appointments">
              <el-table
                :data="userDetail.recentAppointments"
                stripe
                style="width: 100%"
                max-height="300"
              >
                <el-table-column prop="serviceName" label="服务名称" min-width="120" />
                <el-table-column prop="merchantName" label="商家" min-width="120" />
                <el-table-column prop="appointmentTime" label="预约时间" width="180" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag
                      :type="row.status === '已完成' ? 'success' : row.status === '已取消' ? 'danger' : 'warning'"
                      size="small"
                    >
                      {{ row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!userDetail.recentAppointments?.length" description="暂无预约记录" />
            </el-tab-pane>

            <el-tab-pane label="评价记录" name="reviews">
              <el-table
                :data="userDetail.reviews"
                stripe
                style="width: 100%"
                max-height="300"
              >
                <el-table-column prop="serviceName" label="服务/商品" min-width="120" />
                <el-table-column prop="merchantName" label="商家" min-width="120" />
                <el-table-column prop="rating" label="评分" width="100">
                  <template #default="{ row }">
                    <el-rate v-model="row.rating" disabled text-color="#ff9900" />
                  </template>
                </el-table-column>
                <el-table-column prop="comment" label="评价内容" min-width="200" show-overflow-tooltip />
                <el-table-column prop="createdAt" label="评价时间" width="180" />
              </el-table>
              <el-empty v-if="!userDetail.reviews?.length" description="暂无评价记录" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>

      <el-empty v-else description="用户不存在" />
    </el-card>
  </div>
</template>

<style scoped>
.user-detail {
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
</style>
