<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

const loginForm = reactive({
  username: '',
  password: '',
  role: 'user'
})

const loginFormRef = ref()

const roleOptions = [
  { label: '用户', value: 'user' },
  { label: '商家', value: 'merchant' },
  { label: '管理员', value: 'admin' }
]

const loginRules = {
  username: [
    { required: true, message: '请输入邮箱/用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const loginLoading = ref(false)

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    loginLoading.value = true

    try {
      const response = await axios.post('/api/auth/login', {
        username: loginForm.username,
        password: loginForm.password,
        role: loginForm.role
      })

      if (response.data.code === 200 || response.data.code === 0) {
        ElMessage.success('登录成功')

        const roleRoutes: Record<string, string> = {
          user: '/user/home',
          merchant: '/merchant/home',
          admin: '/admin/dashboard'
        }

        router.push(roleRoutes[loginForm.role] || '/user/home')
      } else {
        ElMessage.error(response.data.message || '登录失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '登录失败，请检查账号密码')
    } finally {
      loginLoading.value = false
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1 class="login-title">宠物服务平台</h1>
        <p class="login-subtitle">欢迎回来</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        label-position="top"
      >
        <el-form-item label="选择角色" prop="role">
          <el-radio-group v-model="loginForm.role" class="role-group">
            <el-radio-button
              v-for="option in roleOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item prop="username">
          <template #label>
            <span class="form-label">邮箱 / 用户名</span>
          </template>
          <el-input
            v-model="loginForm.username"
            placeholder="请输入邮箱或用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <template #label>
            <span class="form-label">密码</span>
          </template>
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loginLoading"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span class="footer-text">还没有账号？</span>
          <el-link type="primary" @click="goToRegister">立即注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  padding: 40px;
  overflow: hidden;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-title {
  font-size: 24px;
  font-weight: bold;
  color: #333333;
  margin: 0 0 8px 0;
}

.login-subtitle {
  font-size: 14px;
  color: #666666;
  margin: 0;
}

.login-form {
  margin-top: 0;
}

.form-label {
  font-weight: 500;
  color: #666666;
  font-size: 14px;
  margin-bottom: 8px;
  display: block;
}

.role-group {
  width: 100%;
  display: flex;
  margin-bottom: 24px;
}

.role-group :deep(.el-radio-button__inner) {
  width: 100%;
  border-radius: 4px;
  margin: 0 4px;
}

.role-group :deep(.el-radio-button:first-child .el-radio-button__inner) {
  margin-left: 0;
  border-top-left-radius: 4px;
  border-bottom-left-radius: 4px;
}

.role-group :deep(.el-radio-button:last-child .el-radio-button__inner) {
  margin-right: 0;
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
}

.role-group :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: #4CAF50;
  border-color: #4CAF50;
  color: white;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 16px;
  background-color: #4CAF50;
  border-color: #4CAF50;
}

.login-button:hover {
  background-color: #45a049;
  border-color: #45a049;
}

.login-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
}

.footer-text {
  color: #666666;
  font-size: 14px;
}

:deep(.el-input__wrapper) {
  border-radius: 4px;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #4CAF50 inset;
}

:deep(.el-button--primary) {
  background-color: #4CAF50;
  border-color: #4CAF50;
}

:deep(.el-button--primary:hover) {
  background-color: #45a049;
  border-color: #45a049;
}

:deep(.el-link--primary) {
  color: #4CAF50;
}

:deep(.el-link--primary:hover) {
  color: #45a049;
}
</style>
