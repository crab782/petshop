<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Phone, Lock } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

const loginForm = ref({
  phone: '',
  password: ''
})
const loginLoading = ref(false)
const loginFormRef = ref()

const loginRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    loginLoading.value = true

    try {
      const response = await axios.post('/api/auth/login', {
        phone: loginForm.value.phone,
        password: loginForm.value.password,
        role: 'merchant'
      })

      if (response.data.code === 200 || response.data.code === 0) {
        const result = response.data.data
        if (result.token) {
          localStorage.setItem('merchant_token', result.token)
        }
        if (result.user) {
          localStorage.setItem('merchantInfo', JSON.stringify(result.user))
        }
        ElMessage.success('登录成功')
        router.push('/merchant/home')
      } else {
        ElMessage.error(response.data.message || '登录失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '登录失败，请检查手机号和密码')
    } finally {
      loginLoading.value = false
    }
  })
}

const goToRegister = () => {
  router.push('/merchant/register')
}

const goToUserLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="merchant-login-container">
    <div class="login-card">
      <div class="login-card-inner">
        <div class="login-header">
          <div class="brand-logo">
            <div class="logo-icon">🐶</div>
            <h1 class="brand-title">宠物服务平台</h1>
          </div>
          <h2 class="login-title">商家端登录</h2>
          <p class="login-subtitle">欢迎回来，商家朋友</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          label-position="top"
        >
          <el-form-item prop="phone">
            <template #label>
              <span class="form-label">手机号</span>
            </template>
            <el-input
              v-model="loginForm.phone"
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
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              size="large"
              @keyup.enter="handleLogin"
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

          <div class="login-divider">
            <span>或</span>
          </div>

          <div class="login-footer">
            <el-link type="primary" @click="goToUserLogin">用户登录</el-link>
          </div>
        </el-form>
      </div>

      <div class="merchant-features">
        <div class="feature-item">
          <span class="feature-icon">📊</span>
          <span class="feature-text">数据分析</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">🛠️</span>
          <span class="feature-text">服务管理</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">💳</span>
          <span class="feature-text">订单处理</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.merchant-login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.login-card {
  width: 100%;
  max-width: 450px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  position: relative;
  overflow: hidden;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #4CAF50, #45a049);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

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
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.login-form {
  margin-bottom: 20px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #555;
  margin-bottom: 8px;
  display: block;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

.login-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(90deg, #4CAF50, #45a049);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
}

.login-button:hover:not(:disabled) {
  background: linear-gradient(90deg, #45a049, #3d8b40);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.login-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  gap: 8px;
}

.footer-text {
  font-size: 14px;
  color: #666;
}

:deep(.el-link--primary) {
  font-size: 14px;
  color: #4CAF50;
  font-weight: 500;
}

.login-divider {
  display: flex;
  align-items: center;
  margin: 20px 0;
}

.login-divider::before,
.login-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e0e0e0;
}

.login-divider span {
  padding: 0 12px;
  color: #999;
  font-size: 12px;
}

.merchant-features {
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

@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
  }

  .login-title {
    font-size: 20px;
  }

  .brand-title {
    font-size: 18px;
  }

  :deep(.el-input__wrapper) {
    height: 40px;
  }

  .login-button {
    height: 44px;
    font-size: 14px;
  }
}
</style>
