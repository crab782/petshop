<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

const registerFormRef = ref()

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
}

const registerLoading = ref(false)

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    registerLoading.value = true

    try {
      const response = await axios.post('/api/auth/register', {
        username: registerForm.username,
        email: registerForm.email,
        password: registerForm.password,
        phone: registerForm.phone
      })

      if (response.data.code === 200 || response.data.code === 0) {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } else {
        ElMessage.error(response.data.message || '注册失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '注册失败，请稍后重试')
    } finally {
      registerLoading.value = false
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="register-container">
    <div class="register-card">
      <!-- 卡片装饰 -->
      <!-- 登录头部 -->
      <div class="register-header">
        <div class="brand-logo">
          <div class="logo-icon">🐶</div>
          <h1 class="brand-title">宠物服务平台</h1>
        </div>
        <h2 class="register-title">用户注册</h2>
        <p class="register-subtitle">创建您的账号</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-position="top"
      >
        <el-form-item prop="username">
          <template #label>
            <span class="form-label">用户名</span>
          </template>
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="email">
          <template #label>
            <span class="form-label">邮箱</span>
          </template>
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            :prefix-icon="Message"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="phone">
          <template #label>
            <span class="form-label">手机号</span>
          </template>
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            :prefix-icon="Phone"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <template #label>
            <span class="form-label">密码</span>
          </template>
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            size="large"
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <template #label>
            <span class="form-label">确认密码</span>
          </template>
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            show-password
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="registerLoading"
            class="register-button"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <span class="footer-text">已有账号？</span>
          <el-link type="primary" @click="goToLogin">立即登录</el-link>
        </div>
      </el-form>
      
      <!-- 用户端特色 -->
      <div class="user-features">
        <div class="feature-item">
          <span class="feature-icon">🐾</span>
          <span class="feature-text">宠物管理</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">📅</span>
          <span class="feature-text">在线预约</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">🛍️</span>
          <span class="feature-text">宠物商城</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.register-card {
  width: 100%;
  max-width: 450px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  position: relative;
  overflow: hidden;
}

/* 卡片装饰 */
.register-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #4CAF50, #45a049);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

/* 品牌标识 */
.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.logo-icon {
  font-size: 36px;
  margin-right: 10px;
}

.brand-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.register-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
}

.register-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.register-form {
  margin-top: 0;
  margin-bottom: 20px;
}

.form-label {
  font-weight: 500;
  color: #555;
  display: block;
  font-size: 14px;
  margin-bottom: 8px;
}

/* 表单输入 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
  border-color: #4CAF50;
}

:deep(.el-input__prefix-inner) {
  color: #999;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 10px;
  background: linear-gradient(90deg, #4CAF50, #45a049);
  border: none;
  transition: all 0.3s ease;
}

.register-button:hover:not(:disabled) {
  background: linear-gradient(90deg, #45a049, #3d8b40);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.register-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.register-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
}

.footer-text {
  color: #666;
  font-size: 14px;
}

:deep(.el-link) {
  color: #4CAF50;
  font-weight: 500;
  transition: color 0.3s ease;
}

:deep(.el-link:hover) {
  color: #45a049;
}

/* 用户端特色 */
.user-features {
  display: flex;
  justify-content: space-around;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.feature-icon {
  font-size: 20px;
}

.feature-text {
  font-size: 12px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-card {
    padding: 30px 20px;
  }
  
  .register-title {
    font-size: 20px;
  }
  
  .brand-title {
    font-size: 18px;
  }
  
  :deep(.el-input__wrapper) {
    height: 40px;
  }
  
  .register-button {
    height: 44px;
    font-size: 14px;
  }
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 加载动画 */
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
