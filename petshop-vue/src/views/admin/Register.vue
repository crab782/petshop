<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone, Upload } from '@element-plus/icons-vue'
import { useForm, useAsync } from '@/composables'
import { adminRegister, type AdminRegisterData } from '@/api/auth'

const router = useRouter()

interface FormData {
  email: string
  password: string
  confirmPassword: string
  phone: string
  username: string
  agreeToTerms: boolean
}

const initialFormData: FormData = {
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  username: '',
  agreeToTerms: false
}

const {
  formData,
  errors,
  isSubmitting,
  isValid,
  setFieldError,
  clearErrors,
  validate,
  handleSubmit
} = useForm<FormData>(initialFormData)

const registerFormRef = ref()
const termsDialogVisible = ref(false)

const validationRules: Record<keyof FormData, (value: any) => string | null> = {
  email: () => null,
  password: (value: string) => {
    if (!value) return '请输入密码'
    if (value.length < 6) return '密码至少6个字符'
    return null
  },
  confirmPassword: (value: string) => {
    if (!value) return '请确认密码'
    if (value !== formData.password) return '两次输入的密码不一致'
    return null
  },
  phone: (value: string) => {
    if (!value) return '请输入联系电话'
    if (value.length > 20) return '联系电话最多20个字符'
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(value)) return '请输入正确的手机号格式'
    return null
  },
  username: () => null,
  agreeToTerms: (value: boolean) => {
    if (!value) return '请阅读并同意注册协议'
    return null
  }
}

const elFormRules = computed(() => ({
  email: [],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: (error?: Error) => void) => {
        if (value !== formData.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: (error?: Error) => void) => {
        if (!value) {
          callback(new Error('请输入联系电话'))
        } else if (value.length > 20) {
          callback(new Error('联系电话最多20个字符'))
        } else if (!/^1[3-9]\d{9}$/.test(value)) {
          callback(new Error('请输入正确的手机号格式'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  username: []
}))

const {
  loading: registerLoading,
  error: registerError,
  execute: executeRegister
} = useAsync(async (data: AdminRegisterData) => {
  return await adminRegister(data)
})

const handleRegister = async () => {
  if (!registerFormRef.value) return

  if (!formData.agreeToTerms) {
    ElMessage.warning('请阅读并同意注册协议')
    return
  }

  await registerFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    if (!validate(validationRules)) {
      return
    }

    const registerData: AdminRegisterData = {
      email: formData.email,
      password: formData.password,
      phone: formData.phone,
      username: formData.username
    }

    try {
      const result = await executeRegister(registerData)

      if (result) {
        ElMessage.success('注册成功，请等待审核')
        router.push('/admin/login')
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || error.message || '注册失败，请稍后重试'
      ElMessage.error(errorMessage)
    }
  })
}

const goToLogin = () => {
  router.push('/admin/login')
}

const goToUserRegister = () => {
  router.push('/register')
}

const showTermsDialog = () => {
  termsDialogVisible.value = true
}
</script>

<template>
  <div class="admin-register-container">
    <div class="register-card">
      <!-- 注册头部 -->
      <div class="register-header">
        <div class="brand-logo">
          <div class="logo-icon">🐶</div>
          <h1 class="brand-title">宠物服务平台</h1>
        </div>
        <h2 class="register-title">平台注册</h2>
        <p class="register-subtitle">创建您的管理员账号，开始管理平台</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="formData"
        :rules="elFormRules"
        class="register-form"
        label-position="top"
      >
        <el-form-item prop="email">
          <template #label>
            <span class="form-label">邮箱</span>
          </template>
          <el-input
            v-model="formData.email"
            placeholder="请输入邮箱"
            :prefix-icon="Message"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="phone">
          <template #label>
            <span class="form-label">联系电话</span>
          </template>
          <el-input
            v-model="formData.phone"
            placeholder="请输入联系电话"
            :prefix-icon="Phone"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="username">
          <template #label>
            <span class="form-label">用户名</span>
          </template>
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <template #label>
            <span class="form-label">密码</span>
          </template>
          <el-input
            v-model="formData.password"
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
            v-model="formData.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            show-password
            size="large"
          />
        </el-form-item>

        <el-form-item prop="agreeToTerms">
          <div class="terms-checkbox">
            <el-checkbox v-model="formData.agreeToTerms">
              我已阅读并同意
              <el-link type="primary" @click.prevent="showTermsDialog">《平台管理员注册协议》</el-link>
            </el-checkbox>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="registerLoading || isSubmitting"
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

        <div class="register-divider">
          <span>或</span>
        </div>

        <div class="register-footer">
          <el-link type="primary" @click="goToUserRegister">用户注册</el-link>
        </div>
      </el-form>
      
      <!-- 平台端特色 -->
      <div class="admin-features">
        <div class="feature-item">
          <span class="feature-icon">👥</span>
          <span class="feature-text">用户管理</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">🏪</span>
          <span class="feature-text">商家管理</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">⚙️</span>
          <span class="feature-text">系统设置</span>
        </div>
      </div>
    </div>
  </div>

  <el-dialog
    v-model="termsDialogVisible"
    title="平台管理员注册协议"
    width="600px"
  >
    <div class="terms-content">
      <h3>第一章 总则</h3>
      <p>本协议是您与本平台之间就平台管理员注册相关事宜所订立的契约，请您仔细阅读本注册协议，您点击"同意并继续"按钮后，本协议即构成对双方有约束力的法律文件。</p>

      <h3>第二章 管理员注册</h3>
      <p>第一条 管理员注册条件：</p>
      <ul>
        <li>具有完全民事行为能力的自然人</li>
        <li>提供真实有效的个人信息</li>
        <li>遵守平台各项规则和管理制度</li>
        <li>具备一定的管理能力和责任心</li>
      </ul>

      <h3>第三章 权利与义务</h3>
      <p>第二条 管理员权利：</p>
      <ul>
        <li>管理平台用户和商家</li>
        <li>管理平台服务和商品</li>
        <li>管理平台系统设置</li>
        <li>获取平台运营数据</li>
      </ul>

      <p>第三条 管理员义务：</p>
      <ul>
        <li>保证管理行为的合法性</li>
        <li>保护用户和商家的合法权益</li>
        <li>维护平台的正常运行</li>
        <li>保守平台商业秘密</li>
      </ul>

      <h3>第四章 审核与违规处理</h3>
      <p>第四条 平台有权对管理员提交的资料进行审核，审核通过后方可获得管理员权限。</p>
      <p>第五条 管理员如有违规行为，平台有权根据情节轻重进行处罚，包括但不限于：警告、限制权限、终止合作等。</p>

      <h3>第五章 附则</h3>
      <p>第六条 本协议自管理员注册成功之日起生效。</p>
      <p>第七条 本协议的解释权归平台所有。</p>
    </div>
    <template #footer>
      <el-button type="primary" @click="termsDialogVisible = false">我已阅读</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 注册容器 */
.admin-register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 注册卡片 */
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

/* 注册头部 */
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
}

/* 注册标题 */
.register-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.register-subtitle {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

/* 注册表单 */
.register-form {
  margin-bottom: 20px;
}

/* 表单组 */
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

/* 输入容器 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

/* 注册按钮 */
.register-button {
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
  position: relative;
  overflow: hidden;
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

.register-button.loading {
  background: #4CAF50;
  opacity: 0.8;
}

/* 底部链接 */
.register-footer {
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
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

:deep(.el-link--primary:hover) {
  color: #45a049;
  text-decoration: underline;
}

/* 分隔线 */
.register-divider {
  display: flex;
  align-items: center;
  margin: 20px 0;
}

.register-divider::before,
.register-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e0e0e0;
}

.register-divider span {
  padding: 0 12px;
  color: #999;
  font-size: 12px;
}

/* 协议复选框 */
.terms-checkbox {
  display: flex;
  align-items: center;
}

/* 协议内容 */
.terms-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 0 10px;
}

.terms-content h3 {
  font-size: 16px;
  color: #303133;
  margin: 16px 0 8px 0;
}

.terms-content p {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 8px 0;
}

.terms-content ul {
  margin: 8px 0;
  padding-left: 20px;
}

.terms-content li {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

/* 平台端特色 */
.admin-features {
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
    padding: 8px 12px;
  }
  
  .register-button {
    padding: 12px;
    font-size: 14px;
  }
}

/* 加载动画 */
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.register-button:disabled::after {
  content: '';
  position: absolute;
  right: 15px;
  top: 50%;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  transform: translateY(-50%);
}
</style>