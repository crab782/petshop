<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, Phone, Shop } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

const loginForm = reactive({
  phone: '',
  password: ''
})

const loginFormRef = ref()

const loginRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号格式', trigger: 'blur' }
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
        phone: loginForm.phone,
        password: loginForm.password,
        role: 'user'
      })

      if (response.data.code === 200 || response.data.code === 0) {
        const result = response.data.data
        if (result.token) {
          localStorage.setItem('token', result.token)
        }
        if (result.user) {
          localStorage.setItem('userInfo', JSON.stringify(result.user))
        }
        ElMessage.success('登录成功')
        router.push('/user/home')
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
  router.push('/register')
}

const goToMerchantLogin = () => {
  router.push('/merchant/login')
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <!-- 登录头部 -->
      <div class="login-header">
        <div class="brand-logo">
          <div class="logo-icon">🐶</div>
          <h1 class="brand-title">宠物服务平台</h1>
        </div>
        <h2 class="login-title">用户登录</h2>
        <p class="login-subtitle">欢迎回来</p>
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
            placeholder="请输入11位手机号"
            :prefix-icon="Phone"
            size="large"
            class="form-input"
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
            class="form-input"
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

        <div class="merchant-login-entry" @click="goToMerchantLogin">
          <el-icon class="merchant-icon"><Shop /></el-icon>
          <span class="merchant-text">商家登录</span>
        </div>
      </el-form>

      <!-- 用户端特色 -->
      <div class="user-features">
        <div class="feature-item">
          <span class="feature-icon">🐾</span>
          <span class="feature-text">宠物服务</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">📅</span>
          <span class="feature-text">在线预约</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">🛒</span>
          <span class="feature-text">宠物商城</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 登录容器 */
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 登录卡片 */
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

/* 卡片装饰 */
.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #4CAF50, #45a049);
}

/* 登录头部 */
.login-header {
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

/* 登录标题 */
.login-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
  margin-top: 0;
}

.login-subtitle {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
  margin-top: 0;
}

/* 登录表单 */
.login-form {
  margin-bottom: 20px;
  margin-top: 0;
}

/* 表单组 */
.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #555;
  margin-bottom: 8px;
  display: block;
}

/* 商家登录入口 */
.merchant-login-entry {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  padding: 12px;
  border-radius: 8px;
  border: 1px dashed #ff9800;
  background-color: #fff8e1;
  cursor: pointer;
  transition: all 0.3s ease;
}

.merchant-login-entry:hover {
  background-color: #ffecb3;
  border-color: #f57c00;
  transform: translateY(-1px);
}

.merchant-icon {
  font-size: 18px;
  color: #ff9800;
}

.merchant-text {
  font-size: 14px;
  color: #f57c00;
  font-weight: 500;
}

/* 表单输入 */
.form-input {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

/* 登录按钮 */
.login-button {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 10px;
  background: linear-gradient(90deg, #4CAF50, #45a049);
  border-color: #4CAF50;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-button:hover:not(:disabled) {
  background: linear-gradient(90deg, #45a049, #3d8b40);
  border-color: #45a049;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

:deep(.el-button--primary) {
  background: linear-gradient(90deg, #4CAF50, #45a049);
  border-color: #4CAF50;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(90deg, #45a049, #3d8b40);
  border-color: #45a049;
}

/* 底部链接 */
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
  color: #4CAF50;
  font-weight: 500;
  transition: color 0.3s ease;
}

:deep(.el-link--primary:hover) {
  color: #45a049;
  text-decoration: underline;
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

/* 响应式设计 */
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
  
  .login-button {
    padding: 12px;
    font-size: 14px;
  }
}
</style>
