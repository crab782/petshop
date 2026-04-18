<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getUserInfo as getUserInfoApi, updateUserInfo } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { Upload, Edit, Pets, Ticket, Star, Shield, Calendar } from '@element-plus/icons-vue'

interface UserProfile {
  id: number
  username: string
  email: string
  phone: string
  avatar: string
  createdAt: string
}

const router = useRouter()
const userStore = useUserStore()

const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const isEditing = ref(false)
const loading = ref(false)

const profileForm = reactive<UserProfile>({
  id: 0,
  username: '',
  email: '',
  phone: '',
  avatar: '',
  createdAt: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为2-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const quickActions = [
  { title: '编辑个人资料', icon: Edit, route: '/user/profile/edit' },
  { title: '管理宠物', icon: Pets, route: '/user/pets' },
  { title: '查看订单', icon: Ticket, route: '/user/orders' },
  { title: '查看评价', icon: Star, route: '/user/reviews' }
]

const securitySettings = [
  { title: '修改密码', description: '定期修改密码，保障账户安全' },
  { title: '绑定手机号', description: '绑定手机号，增强账户安全性' },
  { title: '绑定邮箱', description: '绑定邮箱，用于找回密码' }
]

const fetchUserInfo = async () => {
  try {
    loading.value = true
    const res = await getUserInfoApi()
    if (res) {
      profileForm.id = res.id
      profileForm.username = res.username || ''
      profileForm.email = res.email || ''
      profileForm.phone = res.phone || ''
      profileForm.avatar = res.avatar || ''
      profileForm.createdAt = res.created_at || ''
      userStore.setUserInfo(res)
    }
  } catch (error: unknown) {
    const err = error as Error
    ElMessage.error(err.message || '获取用户信息失败')
  } finally {
    loading.value = false
  }
}

const handleEdit = () => {
  isEditing.value = true
}

const handleSave = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    try {
      loading.value = true
      await updateUserInfo({
        username: profileForm.username,
        email: profileForm.email,
        phone: profileForm.phone,
        avatar: profileForm.avatar
      })
      ElMessage.success('保存成功')
      isEditing.value = false
      fetchUserInfo()
    } catch (error: unknown) {
      const err = error as Error
      ElMessage.error(err.message || '保存失败')
    } finally {
      loading.value = false
    }
  })
}

const handleCancel = () => {
  isEditing.value = false
  fetchUserInfo()
}

const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    try {
      loading.value = true
      ElMessage.success('密码修改成功')
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    } catch (error: unknown) {
      const err = error as Error
      ElMessage.error(err.message || '密码修改失败')
    } finally {
      loading.value = false
    }
  })
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const handleAvatarChange = (uploadFile: { raw: File }) => {
  const url = URL.createObjectURL(uploadFile.raw)
  profileForm.avatar = url
  return false
}

const goToAction = (route: string) => {
  router.push(route)
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<template>
  <div class="user-profile">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
      <p class="page-description">管理您的个人信息和设置</p>
    </div>

    <div class="container">
      <div class="profile-grid">
        <div class="profile-main">
          <el-card class="profile-card card-hover">
            <template #header>
              <div class="card-header">
                <h3 class="card-title">个人资料</h3>
                <el-button
                  v-if="!isEditing"
                  type="primary"
                  round
                  @click="handleEdit"
                  class="edit-button"
                >
                  编辑
                </el-button>
                <div v-else class="edit-actions">
                  <el-button size="small" round @click="handleCancel">取消</el-button>
                  <el-button size="small" type="primary" round @click="handleSave">保存</el-button>
                </div>
              </div>
            </template>

            <el-form
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              :disabled="!isEditing"
              label-width="100px"
              class="profile-form"
            >
              <el-form-item label="用户名" prop="username">
                <el-input v-model="profileForm.username" placeholder="请输入用户名" class="form-input" />
              </el-form-item>

              <el-form-item label="邮箱" prop="email">
                <el-input v-model="profileForm.email" placeholder="请输入邮箱" class="form-input" />
              </el-form-item>

              <el-form-item label="手机号" prop="phone">
                <el-input v-model="profileForm.phone" placeholder="请输入手机号" class="form-input" />
              </el-form-item>

              <el-form-item label="注册时间" v-if="!isEditing">
                <el-input v-model="profileForm.createdAt" disabled class="form-input" />
              </el-form-item>
            </el-form>
          </el-card>

          <el-card class="quick-actions-card card-hover">
            <template #header>
              <h3 class="card-title">快捷操作</h3>
            </template>

            <div class="quick-actions-grid">
              <div
                v-for="action in quickActions"
                :key="action.title"
                class="quick-action-item"
                @click="goToAction(action.route)"
              >
                <div class="action-icon">
                  <el-icon :size="24"><component :is="action.icon" /></el-icon>
                </div>
                <div class="action-title">{{ action.title }}</div>
              </div>
            </div>
          </el-card>

          <el-card class="security-card card-hover">
            <template #header>
              <h3 class="card-title">安全设置</h3>
            </template>

            <div class="security-settings">
              <div
                v-for="(setting, index) in securitySettings"
                :key="setting.title"
                class="security-setting-item"
              >
                <div class="setting-info">
                  <h4 class="setting-title">{{ setting.title }}</h4>
                  <p class="setting-description">{{ setting.description }}</p>
                </div>
                <el-button type="primary" size="small" round>
                  前往设置
                </el-button>
              </div>
            </div>
          </el-card>

          <el-card class="password-card card-hover">
            <template #header>
              <h3 class="card-title">修改密码</h3>
            </template>

            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="120px"
              class="password-form"
            >
              <el-form-item label="原密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入原密码"
                  show-password
                  class="form-input"
                />
              </el-form-item>

              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                  class="form-input"
                />
              </el-form-item>

              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                  class="form-input"
                />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" round @click="handlePasswordSubmit" class="submit-button">
                  确认修改
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>

        <div class="profile-sidebar">
          <el-card class="avatar-card card-hover">
            <template #header>
              <h3 class="card-title">头像设置</h3>
            </template>

            <div class="avatar-content">
              <div class="avatar-container">
                <el-avatar :size="140" :src="profileForm.avatar" icon="UserFilled" class="user-avatar" />
              </div>

              <el-upload
                class="avatar-upload"
                :show-file-list="false"
                :on-change="handleAvatarChange"
                accept="image/*"
              >
                <el-button type="primary" :icon="Upload" round class="upload-button">
                  上传头像
                </el-button>
              </el-upload>
            </div>
          </el-card>

          <el-card class="logout-card card-hover">
            <el-button
              type="danger"
              size="large"
              round
              class="logout-button"
              @click="handleLogout"
            >
              退出登录
            </el-button>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-profile {
  min-height: 100vh;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  padding-top: 20px;
}

.page-title {
  font-size: 32px;
  font-weight: bold;
  color: #333333;
  margin: 0 0 12px 0;
}

.page-description {
  font-size: 16px;
  color: #666666;
  margin: 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-grid {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
}

.profile-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card,
.password-card,
.avatar-card,
.logout-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-hover:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #f9f9f9;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  color: #333333;
  margin: 0;
}

.edit-button {
  transition: all 0.3s ease;
}

.edit-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(76, 175, 80, 0.3);
}

.edit-actions {
  display: flex;
  gap: 12px;
}

.profile-form,
.password-form {
  padding: 24px;
}

.form-input {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.form-input:focus {
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.submit-button {
  transition: all 0.3s ease;
}

.submit-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(76, 175, 80, 0.3);
}

.avatar-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  padding: 30px 0;
}

.avatar-container {
  position: relative;
  width: 140px;
  height: 140px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.avatar-container:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.user-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-button {
  transition: all 0.3s ease;
}

.upload-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(76, 175, 80, 0.3);
}

.logout-button {
  width: 100%;
  transition: all 0.3s ease;
}

.logout-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(245, 108, 108, 0.3);
}

.quick-actions-card,
.security-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  padding: 24px;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quick-action-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background-color: #f0f9ff;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #4CAF50;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: all 0.3s ease;
}

.quick-action-item:hover .action-icon {
  transform: scale(1.1);
}

.action-title {
  font-size: 14px;
  font-weight: 500;
  color: #333333;
  text-align: center;
}

.security-settings {
  padding: 24px;
}

.security-setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #e0e0e0;
}

.security-setting-item:last-child {
  border-bottom: none;
}

.setting-info {
  flex: 1;
}

.setting-title {
  font-size: 16px;
  font-weight: 500;
  color: #333333;
  margin: 0 0 4px 0;
}

.setting-description {
  font-size: 14px;
  color: #666666;
  margin: 0;
}


/* 响应式设计 */
@media (max-width: 768px) {
  .user-profile {
    padding: 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .profile-grid {
    grid-template-columns: 1fr;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .edit-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .profile-form,
  .password-form {
    padding: 16px;
  }

  .el-form-item {
    margin-bottom: 16px;
  }
}
</style>
