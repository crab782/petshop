<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminCard from '@/components/AdminCard.vue'

const currentDate = ref('')

const stats = [
  {
    title: '总用户数',
    value: '2,580',
    subtitle: '位注册用户',
    icon: 'fa fa-users',
    iconColor: 'primary'
  },
  {
    title: '总商家数',
    value: '128',
    subtitle: '家合作商家',
    icon: 'fa fa-building',
    iconColor: 'secondary'
  },
  {
    title: '本月营收',
    value: '¥ 156,800',
    subtitle: '本月收入',
    icon: 'fa fa-rmb',
    iconColor: 'green-500'
  },
  {
    title: '今日订单',
    value: '156',
    subtitle: '笔订单',
    icon: 'fa fa-shopping-cart',
    iconColor: 'blue-500'
  }
]

const recentUsers = [
  {
    id: 2580,
    username: '张先生',
    phone: '138****1234',
    email: 'zhangsan@email.com',
    avatar: '',
    registerTime: '2026-04-18 10:30'
  },
  {
    id: 2579,
    username: '李女士',
    phone: '139****5678',
    email: 'lisi@email.com',
    avatar: '',
    registerTime: '2026-04-18 09:15'
  },
  {
    id: 2578,
    username: '王先生',
    phone: '137****9012',
    email: 'wangwu@email.com',
    avatar: '',
    registerTime: '2026-04-17 16:45'
  },
  {
    id: 2577,
    username: '赵女士',
    phone: '136****3456',
    email: 'zhaoliu@email.com',
    avatar: '',
    registerTime: '2026-04-17 14:20'
  },
  {
    id: 2576,
    username: '钱先生',
    phone: '135****7890',
    email: 'qianqi@email.com',
    avatar: '',
    registerTime: '2026-04-17 11:00'
  }
]

const pendingMerchants = [
  {
    id: 128,
    name: '宠物乐园',
    contact: '陈老板',
    phone: '138****1234',
    email: 'petpark@email.com',
    address: '北京市朝阳区建国路88号',
    registerTime: '2026-04-17 15:30'
  },
  {
    id: 127,
    name: '快乐宠物',
    contact: '周经理',
    phone: '139****5678',
    email: 'happy@email.com',
    address: '上海市浦东新区世纪大道100号',
    registerTime: '2026-04-16 10:00'
  },
  {
    id: 126,
    name: '萌宠之家',
    contact: '吴女士',
    phone: '137****9012',
    email: 'meng@email.com',
    address: '广州市天河区天河路123号',
    registerTime: '2026-04-15 14:30'
  }
]

const announcements = [
  {
    id: 1,
    title: '平台服务升级通知',
    content: '平台将于本周日凌晨2:00-6:00进行系统升级，届时部分功能将暂停使用，请提前做好准备。',
    publishTime: '2026-04-18 08:00',
    publisher: '系统管理员'
  },
  {
    id: 2,
    title: '新功能上线公告',
    content: '平台新增商品分类管理和批量操作功能，方便商家更高效地管理商品。',
    publishTime: '2026-04-15 10:00',
    publisher: '系统管理员'
  },
  {
    id: 3,
    title: '五一劳动节放假安排',
    content: '五一劳动节期间，平台客服服务时间调整为9:00-18:00。',
    publishTime: '2026-04-10 09:00',
    publisher: '系统管理员'
  }
]

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

onMounted(() => {
  currentDate.value = new Date().toLocaleDateString('zh-CN')
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

    <div class="stats-section">
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
              <el-table :data="recentUsers" style="width: 100%">
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
              <el-table :data="pendingMerchants" style="width: 100%">
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
            <div class="announcements-list">
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
