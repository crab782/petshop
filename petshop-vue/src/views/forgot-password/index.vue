<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Key, Check } from '@element-plus/icons-vue'
import { sendVerifyCode, resetPassword } from '@/api/auth'

const router = useRouter()

const currentStep = ref(0)

const stepItems = [
  { title: '验证邮箱', icon: Message },
  { title: '重置密码', icon: Lock },
  { title: '完成', icon: Check }
]

const emailForm = reactive({
  email: ''
})

const emailFormRef = ref()

const emailRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const verifyCodeForm = reactive({
  email: '',
  verifyCode: '',
  password: '',
  confirmPassword: ''
})

const verifyCodeFormRef = ref()

const verifyCodeRules = {
  verifyCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: Function) => {
        if (value !== verifyCodeForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const sendLoading = ref(false)
const resetLoading = ref(false)
const countdown = ref(0)
let countdownTimer: ReturnType<typeof setInterval> | null = null

const handleSendCode = async () => {
  if (!emailFormRef.value) return

  await emailFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    sendLoading.value = true

    try {
      await sendVerifyCode(emailForm.email)
      ElMessage.success('验证码已发送到邮箱')
      verifyCodeForm.email = emailForm.email
      currentStep.value = 1
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '发送验证码失败')
    } finally {
      sendLoading.value = false
    }
  })
}

const startCountdown = () => {
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      if (countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }
  }, 1000)
}

const handleResetPassword = async () => {
  if (!verifyCodeFormRef.value) return

  await verifyCodeFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    resetLoading.value = true

    try {
      await resetPassword({
        email: verifyCodeForm.email,
        verifyCode: verifyCodeForm.verifyCode,
        password: verifyCodeForm.password
      })
      ElMessage.success('密码重置成功')
      currentStep.value = 2
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '重置密码失败')
    } finally {
      resetLoading.value = false
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}

const goBack = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}
</script>

<template>
  <div class="forgot-container">
    <div class="forgot-card">
      <div class="forgot-header">
        <h1 class="forgot-title">忘记密码</h1>
        <p class="forgot-subtitle">通过邮箱验证重置密码</p>
      </div>

      <el-steps :active="currentStep" class="step-indicator" finish-status="success">
        <el-step
          v-for="(item, index) in stepItems"
          :key="index"
          :title="item.title"
          :icon="item.icon"
        />
      </el-steps>

      <div class="step-content">
        <el-form
          v-show="currentStep === 0"
          ref="emailFormRef"
          :model="emailForm"
          :rules="emailRules"
          class="forgot-form"
          label-position="top"
        >
          <el-form-item prop="email">
            <template #label>
              <span class="form-label">邮箱地址</span>
            </template>
            <el-input
              v-model="emailForm.email"
              placeholder="请输入注册时的邮箱"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="sendLoading"
              class="action-button"
              @click="handleSendCode"
            >
              {{ sendLoading ? '发送中...' : '发送验证码' }}
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <el-link type="primary" @click="goToLogin">返回登录</el-link>
          </div>
        </el-form>

        <el-form
          v-show="currentStep === 1"
          ref="verifyCodeFormRef"
          :model="verifyCodeForm"
          :rules="verifyCodeRules"
          class="forgot-form"
          label-position="top"
        >
          <el-form-item prop="verifyCode">
            <template #label>
              <span class="form-label">验证码</span>
            </template>
            <el-input
              v-model="verifyCodeForm.verifyCode"
              placeholder="请输入邮箱收到的验证码"
              :prefix-icon="Key"
              maxlength="6"
              size="large"
            />
          </el-form-item>

          <el-form-item prop="password">
            <template #label>
              <span class="form-label">新密码</span>
            </template>
            <el-input
              v-model="verifyCodeForm.password"
              type="password"
              placeholder="请输入新密码"
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
              v-model="verifyCodeForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              :prefix-icon="Lock"
              show-password
              size="large"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="resetLoading"
              class="action-button"
              @click="handleResetPassword"
            >
              {{ resetLoading ? '重置中...' : '重置密码' }}
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <el-link type="primary" @click="goBack">返回上一步</el-link>
          </div>
        </el-form>

        <div v-show="currentStep === 2" class="success-content">
          <div class="success-icon">
            <el-icon :size="64" color="#67c23a"><Check /></el-icon>
          </div>
          <h2 class="success-title">密码重置成功</h2>
          <p class="success-text">您的密码已成功重置，请使用新密码登录</p>
          <el-button type="primary" size="large" class="action-button" @click="goToLogin">
            返回登录
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.forgot-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.forgot-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 48px 40px;
}

.forgot-header {
  text-align: center;
  margin-bottom: 32px;
}

.forgot-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 12px 0;
}

.forgot-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.step-indicator {
  margin-bottom: 32px;
  padding: 0 20px;
}

.step-content {
  margin-top: 0;
}

.forgot-form {
  margin-top: 0;
}

.form-label {
  font-weight: 500;
  color: #606266;
}

.action-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  margin-top: 8px;
}

.form-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 16px;
}

.success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #f0f9eb;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
}

.success-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 12px 0;
}

.success-text {
  font-size: 14px;
  color: #909399;
  margin: 0 0 32px 0;
}
</style>
