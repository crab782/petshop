<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone, Shop, Upload } from '@element-plus/icons-vue'
import { useForm, useAsync } from '@/composables'
import { merchantRegister, type MerchantRegisterData } from '@/api/auth'

const router = useRouter()

interface FormData {
  email: string
  password: string
  confirmPassword: string
  phone: string
  contact_person: string
  name: string
  logo: string
  address: string
  agreeToTerms: boolean
}

const initialFormData: FormData = {
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  contact_person: '',
  name: '',
  logo: '',
  address: '',
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

const logoInputMethod = ref<'url' | 'upload'>('url')
const logoUrl = ref('')
const registerFormRef = ref()
const termsDialogVisible = ref(false)

const validationRules: Record<keyof FormData, (value: any) => string | null> = {
  email: (value: string) => {
    if (!value) return '请输入邮箱'
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(value)) return '请输入正确的邮箱格式'
    if (value.length > 100) return '邮箱最多100个字符'
    return null
  },
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
  contact_person: (value: string) => {
    if (!value) return '请输入联系人'
    if (value.length > 50) return '联系人姓名最多50个字符'
    return null
  },
  name: (value: string) => {
    if (!value) return '请输入商家名称'
    if (value.length < 2 || value.length > 100) return '商家名称长度为2-100个字符'
    return null
  },
  logo: () => null,
  address: (value: string) => {
    if (!value) return '请输入地址'
    if (value.length > 255) return '地址最多255个字符'
    return null
  },
  agreeToTerms: (value: boolean) => {
    if (!value) return '请阅读并同意注册协议'
    return null
  }
}

const elFormRules = computed(() => ({
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
    { max: 100, message: '邮箱最多100个字符', trigger: 'blur' }
  ],
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
  contact_person: [
    { required: true, message: '请输入联系人', trigger: 'blur' },
    { max: 50, message: '联系人姓名最多50个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入商家名称', trigger: 'blur' },
    { min: 2, max: 100, message: '商家名称长度为2-100个字符', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' },
    { max: 255, message: '地址最多255个字符', trigger: 'blur' }
  ]
}))

const {
  loading: registerLoading,
  error: registerError,
  execute: executeRegister
} = useAsync(async (data: MerchantRegisterData) => {
  return await merchantRegister(data)
})

const handleLogoUrlInput = (value: string) => {
  logoUrl.value = value
  formData.logo = value
}

const handleLogoUploadChange = (uploadFile: { raw?: File; url?: string }) => {
  if (uploadFile.raw) {
    const url = URL.createObjectURL(uploadFile.raw)
    logoUrl.value = url
    formData.logo = url
  } else if (uploadFile.url) {
    logoUrl.value = uploadFile.url
    formData.logo = uploadFile.url
  }
  return false
}

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

    const registerData: MerchantRegisterData = {
      email: formData.email,
      password: formData.password,
      phone: formData.phone,
      contact_person: formData.contact_person,
      name: formData.name,
      logo: formData.logo || undefined,
      address: formData.address
    }

    try {
      const result = await executeRegister(registerData)

      if (result) {
        ElMessage.success('注册成功，请等待审核')
        router.push('/login')
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || error.message || '注册失败，请稍后重试'
      ElMessage.error(errorMessage)
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}

const goToUserRegister = () => {
  router.push('/register')
}

const showTermsDialog = () => {
  termsDialogVisible.value = true
}
</script>

<template>
  <div class="merchant-register-container">
    <div class="register-card">
      <!-- 注册头部 -->
      <div class="register-header">
        <div class="brand-logo">
          <div class="logo-icon">🐶</div>
          <h1 class="brand-title">宠物服务平台</h1>
        </div>
        <h2 class="register-title">商家注册</h2>
        <p class="register-subtitle">创建您的店铺账号，开始您的宠物服务之旅</p>
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

        <el-form-item prop="contact_person">
          <template #label>
            <span class="form-label">联系人</span>
          </template>
          <el-input
            v-model="formData.contact_person"
            placeholder="请输入联系人姓名"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="name">
          <template #label>
            <span class="form-label">商家名称</span>
          </template>
          <el-input
            v-model="formData.name"
            placeholder="请输入商家名称"
            :prefix-icon="Shop"
            size="large"
          />
        </el-form-item>

        <el-form-item label="商家Logo">
          <div class="logo-section">
            <el-radio-group v-model="logoInputMethod" class="logo-method">
              <el-radio value="url">URL输入</el-radio>
              <el-radio value="upload">本地上传</el-radio>
            </el-radio-group>

            <div class="logo-content">
              <div v-if="logoInputMethod === 'url'" class="logo-url-input">
                <el-input
                  v-model="formData.logo"
                  placeholder="请输入Logo图片URL"
                  @input="handleLogoUrlInput"
                />
              </div>

              <div v-else class="logo-upload">
                <el-upload
                  :show-file-list="false"
                  :on-change="handleLogoUploadChange"
                  accept="image/*"
                >
                  <el-button type="primary" :icon="Upload">
                    选择图片
                  </el-button>
                </el-upload>
              </div>

              <div class="logo-preview">
                <el-avatar :size="60" :src="logoUrl" icon="Shop" />
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item prop="address">
          <template #label>
            <span class="form-label">地址</span>
          </template>
          <el-input
            v-model="formData.address"
            placeholder="请输入地址"
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
              <el-link type="primary" @click.prevent="showTermsDialog">《商家注册协议》</el-link>
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
      
      <!-- 商家端特色 -->
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

  <el-dialog
    v-model="termsDialogVisible"
    title="商家注册协议"
    width="600px"
  >
    <div class="terms-content">
      <h3>第一章 总则</h3>
      <p>本协议是您与本平台之间就商家入驻相关事宜所订立的契约，请您仔细阅读本注册协议，您点击"同意并继续"按钮后，本协议即构成对双方有约束力的法律文件。</p>

      <h3>第二章 商家入驻</h3>
      <p>第一条 商家入驻条件：</p>
      <ul>
        <li>具有合法经营资格的商户</li>
        <li>提供真实有效的商家信息</li>
        <li>遵守平台各项规则和管理制度</li>
      </ul>

      <h3>第三章 权利与义务</h3>
      <p>第二条 商家权利：</p>
      <ul>
        <li>在平台开展经营活动</li>
        <li>获取平台提供的相关服务</li>
        <li>对平台管理提出建议和意见</li>
      </ul>

      <p>第三条 商家义务：</p>
      <ul>
        <li>保证商品和服务的质量</li>
        <li>遵守国家法律法规</li>
        <li>保护消费者合法权益</li>
        <li>按时缴纳相关费用</li>
      </ul>

      <h3>第四章 审核与违规处理</h3>
      <p>第四条 平台有权对商家提交的资料进行审核，审核通过后方可开展经营活动。</p>
      <p>第五条 商家如有违规行为，平台有权根据情节轻重进行处罚，包括但不限于：警告、限制功能、终止合作等。</p>

      <h3>第五章 附则</h3>
      <p>第六条 本协议自商家注册成功之日起生效。</p>
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
.merchant-register-container {
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

/* Logo部分 */
.logo-section {
  width: 100%;
}

.logo-method {
  margin-bottom: 12px;
}

.logo-content {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.logo-url-input {
  flex: 1;
}

.logo-upload {
  margin-bottom: 8px;
}

.logo-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.logo-preview span {
  font-size: 12px;
  color: #909399;
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

/* 商家端特色 */
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
