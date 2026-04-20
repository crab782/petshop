<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AdminCard from '@/components/AdminCard.vue'

const currentDate = ref('')
const loading = ref(false)

const stats = ref([
  {
    title: '总用户数',
    value: '0',
    subtitle: '位注册用户',
    icon: 'fa fa-users',
    iconColor: 'primary'
  },
  {
    title: '总商家数',
    value: '0',
    subtitle: '家合作商家',
    icon: 'fa fa-building',
    iconColor: 'secondary'
  },
  {
    title: '本月营收',
    value: '¥ 0',
    subtitle: '本月收入',
    icon: 'fa fa-rmb',
    iconColor: 'green-500'
  },
  {
    title: '今日订单',
    value: '0',
    subtitle: '笔订单',
    icon: 'fa fa-shopping-cart',
    iconColor: 'blue-500'
  }
])

const recentUsers = ref<any[]>([])
const pendingMerchants = ref<any[]>([])
const announcements = ref<any[]>([])

const quickActions = [
  {
    title: '用户管理',
    icon: 'fa fa-users',
    iconColor: 'primary',
    path: '/admin/users',
    description: '管理注册用户'
  },
  {
    title: '商家审核',
    icon: 'fa fa-building',
    iconColor: 'secondary',
    path: '/admin/merchants/audit',
    description: '审核商家入驻'
  },
  {
    title: '发布公告',
    icon: 'fa fa-bullhorn',
    iconColor: 'orange-500',
    path: '/admin/announcements/edit',
    description: '发布系统公告'
  },
  {
    title: '系统设置',
    icon: 'fa fa-cog',
    iconColor: 'gray-500',
    path: '/admin/system',
    description: '系统参数配置'
  }
]

const fetchDashboardStats = async () => {
  try {
    const response = await fetch('/api/admin/dashboard')
    if (!response.ok) throw new Error('获取仪表盘数据失败')
    const data = await response.json()
    if (data.code === 200 && data.data) {
      stats.value = [
        {
          title: '总用户数',
          value: data.data.userCount.toString(),
          subtitle: '位注册用户',
          icon: 'fa fa-users',
          iconColor: 'primary'
        },
        {
          title: '总商家数',
          value: data.data.merchantCount.toString(),
          subtitle: '家合作商家',
          icon: 'fa fa-building',
          iconColor: 'secondary'
        },
        {
          title: '本月营收',
          value: '¥ 0',
          subtitle: '本月收入',
          icon: 'fa fa-rmb',
          iconColor: 'green-500'
        },
        {
          title: '今日订单',
          value: data.data.orderCount.toString(),
          subtitle: '笔订单',
          icon: 'fa fa-shopping-cart',
          iconColor: 'blue-500'
        }
      ]
    }
  } catch (error) {
    ElMessage.error('获取仪表盘数据失败')
    console.error('Error fetching dashboard stats:', error)
  }
}

const fetchRecentUsers = async () => {
  try {
    const response = await fetch('/api/admin/dashboard/recent-users?page=0&pageSize=5')
    if (!response.ok) throw new Error('获取最近用户数据失败')
    const data = await response.json()
    if (data.code === 200 && data.data && data.data.data) {
      recentUsers.value = data.data.data.map((user: any) => ({
        id: user.id,
        username: user.username,
        phone: user.phone ? user.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '-',
        email: user.email || '-',
        avatar: user.avatar || '',
        registerTime: user.createdAt ? new Date(user.createdAt).toLocaleString('zh-CN') : ''
      }))
    }
  } catch (error) {
    ElMessage.error('获取最近用户数据失败')
    console.error('Error fetching recent users:', error)
  }
}

const fetchPendingMerchants = async () => {
  try {
    const response = await fetch('/api/admin/dashboard/pending-merchants?page=0&pageSize=3')
    if (!response.ok) throw new Error('获取待审核商家数据失败')
    const data = await response.json()
    if (data.code === 200 && data.data && data.data.data) {
      pendingMerchants.value = data.data.data.map((merchant: any) => ({
        id: merchant.id,
        name: merchant.name,
        contact: merchant.contactPerson || '-',
        phone: merchant.phone ? merchant.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '-',
        email: merchant.email || '-',
        address: merchant.address || '-',
        registerTime: merchant.createdAt ? new Date(merchant.createdAt).toLocaleString('zh-CN') : ''
      }))
    }
  } catch (error) {
    ElMessage.error('获取待审核商家数据失败')
    console.error('Error fetching pending merchants:', error)
  }
}

const fetchAnnouncements = async () => {
  try {
    const response = await fetch('/api/admin/dashboard/announcements?page=0&pageSize=3')
    if (!response.ok) throw new Error('获取公告数据失败')
    const data = await response.json()
    if (data.code === 200 && data.data && data.data.data) {
      announcements.value = data.data.data.map((announcement: any) => ({
        id: announcement.id,
        title: announcement.title,
        content: announcement.content || '',
        publishTime: announcement.createdAt ? new Date(announcement.createdAt).toLocaleString('zh-CN') : '',
        publisher: '系统管理员'
      }))
    }
  } catch (error) {
    ElMessage.error('获取公告数据失败')
    console.error('Error fetching announcements:', error)
  }
}

const handleUserClick = (user: any) => {
  console.log('查看用户:', user)
}

const handleMerchantClick = (merchant: any) => {
  console.log('审核商家:', merchant)
}

const handleAnnouncementClick = (announcement: any) => {
  console.log('查看公告:', announcement)
}

const handleQuickAction = (action: any) => {
  console.log('快捷操作:', action)
}

const fetchAllData = async () => {
  loading.value = true
  try {
    await Promise.all([
      fetchDashboardStats(),
      fetchRecentUsers(),
      fetchPendingMerchants(),
      fetchAnnouncements()
    ])
  } catch (error) {
    console.error('Error fetching data:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  currentDate.value = new Date().toLocaleDateString('zh-CN')
  fetchAllData()
})
</script>

<template>
  <div class="admin-dashboard">
    <div class="welcome-section">
      <el-card shadow="hover" class="welcome-card">
        <h1 class="welcome-title">欢迎回来，管理员！</h1>
        <p class="welcome-text">今天是 {{ currentDate }}，祝您工作顺利！</p>
      </el-card>
    </div>

    <div class="stats-section" v-loading="loading">
      <div class="stats-grid">
        <div v-for="(stat, index) in stats" :key="index" class="stat-item">
          <AdminCard
            :title="stat.title"
            :value="stat.value"
            :subtitle="stat.subtitle"
            :icon="stat.icon"
            :icon-color="stat.iconColor"
          />
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="left-content">
        <div class="users-section">
          <el-card shadow="hover" class="users-card">
            <template #header>
              <div class="section-header">
                <h2 class="section-title">最近注册用户</h2>
                <el-button type="primary" link @click="handleQuickAction({ path: '/admin/users' })">
                  查看全部
                </el-button>
              </div>
            </template>
            <div class="users-table">
              <el-table :data="recentUsers" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="用户ID" width="80" />
                <el-table-column prop="username" label="用户名" width="100" />
                <el-table-column prop="phone" label="手机号" width="130" />
                <el-table-column prop="email" label="邮箱" width="180" />
                <el-table-column prop="registerTime" label="注册时间" width="160" />
                <el-table-column label="操作" width="80">
                  <template #default="{ row }">
                    <el-button type="primary" link @click="handleUserClick(row)">
                      详情
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loading && recentUsers.length === 0" description="暂无最近注册用户" />
            </div>
          </el-card>
        </div>

        <div class="merchants-section">
          <el-card shadow="hover" class="merchants-card">
            <template #header>
              <div class="section-header">
                <h2 class="section-title">待审核商家</h2>
                <el-button type="primary" link @click="handleQuickAction({ path: '/admin/merchants/audit' })">
                  查看全部
                </el-button>
              </div>
            </template>
            <div class="merchants-table">
              <el-table :data="pendingMerchants" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="商家ID" width="80" />
                <el-table-column prop="name" label="商家名称" width="120" />
                <el-table-column prop="contact" label="联系人" width="100" />
                <el-table-column prop="phone" label="联系电话" width="130" />
                <el-table-column prop="address" label="地址" min-width="200" />
                <el-table-column prop="registerTime" label="申请时间" width="160" />
                <el-table-column label="操作" width="120">
                  <template #default="{ row }">
                    <el-button type="success" link size="small" @click="handleMerchantClick(row)">
                      通过
                    </el-button>
                    <el-button type="danger" link size="small" @click="handleMerchantClick(row)">
                      拒绝
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loading && pendingMerchants.length === 0" description="暂无待审核商家" />
            </div>
          </el-card>
        </div>
      </div>

      <div class="right-content">
        <div class="quick-actions-section">
          <el-card shadow="hover" class="quick-card">
            <template #header>
              <h2 class="section-title">快捷操作</h2>
            </template>
            <div class="quick-actions">
              <div
                v-for="(action, index) in quickActions"
                :key="index"
                class="quick-action-item"
                @click="handleQuickAction(action)"
              >
                <div class="action-icon" :class="`bg-${action.iconColor}/20`">
                  <i :class="`${action.icon} text-${action.iconColor} text-xl`"></i>
                </div>
                <div class="action-info">
                  <h3 class="action-title">{{ action.title }}</h3>
                  <p class="action-desc">{{ action.description }}</p>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <div class="announcements-section">
          <el-card shadow="hover" class="announcements-card">
            <template #header>
              <div class="section-header">
                <h2 class="section-title">系统公告</h2>
                <el-button type="primary" link @click="handleQuickAction({ path: '/admin/announcements' })">
                  查看全部
                </el-button>
              </div>
            </template>
            <div class="announcements-list" v-loading="loading">
              <div
                v-for="(announcement, index) in announcements"
                :key="index"
                class="announcement-item"
                @click="handleAnnouncementClick(announcement)"
              >
                <div class="announcement-header">
                  <span class="announcement-title">{{ announcement.title }}</span>
                  <span class="announcement-time">{{ announcement.publishTime }}</span>
                </div>
                <p class="announcement-content">{{ announcement.content }}</p>
                <div class="announcement-footer">
                  <span class="announcement-publisher">发布人：{{ announcement.publisher }}</span>
                </div>
              </div>
              <el-empty v-if="!loading && announcements.length === 0" description="暂无系统公告" />
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-dashboard {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.welcome-section {
  margin-bottom: 8px;
}

.welcome-card {
  border-radius: 8px;
  overflow: hidden;
}

.welcome-title {
  font-size: 24px;
  font-weight: bold;
  color: #333333;
  margin: 0 0 8px 0;
}

.welcome-text {
  font-size: 16px;
  color: #666666;
  margin: 0;
}

.stats-section {
  margin-bottom: 8px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

.left-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.right-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333333;
  margin: 0;
}

.users-card,
.merchants-card {
  border-radius: 8px;
  overflow: hidden;
}

.users-table,
.merchants-table {
  margin-top: 16px;
}

.quick-card,
.announcements-card {
  border-radius: 8px;
  overflow: hidden;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-top: 16px;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quick-action-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: var(--el-color-primary);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}

.action-info {
  text-align: center;
}

.action-title {
  font-size: 14px;
  font-weight: semibold;
  color: #333333;
  margin: 0 0 4px 0;
}

.action-desc {
  font-size: 12px;
  color: #999999;
  margin: 0;
}

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.announcement-item {
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.announcement-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: var(--el-color-primary);
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.announcement-title {
  font-size: 14px;
  font-weight: semibold;
  color: #333333;
}

.announcement-time {
  font-size: 12px;
  color: #999999;
}

.announcement-content {
  font-size: 13px;
  color: #666666;
  margin: 0 0 8px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.announcement-footer {
  display: flex;
  justify-content: flex-end;
}

.announcement-publisher {
  font-size: 12px;
  color: #999999;
}

@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }

  .right-content {
    flex-direction: row;
  }

  .quick-actions-section,
  .announcements-section {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .right-content {
    flex-direction: column;
  }

  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
