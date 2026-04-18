<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone, Shop, Upload } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  contact_person: '',
  name: '',
  logo: '',
  address: '',
  agreeToTerms: false
})

const logoInputMethod = ref<'url' | 'upload'>('url')
const logoUrl = ref('')

const registerFormRef = ref()

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validatePhone = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请输入联系电话'))
  } else if (!/^1[3-9]\d{9}$/.test(value) && value.length > 20) {
    callback(new Error('请输入正确的手机号格式'))
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
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
    { max: 100, message: '邮箱最多100个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' },
    { max: 20, message: '联系电话最多20个字符', trigger: 'blur' }
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
}

const registerLoading = ref(false)

const handleLogoUrlInput = (value: string) => {
  logoUrl.value = value
  registerForm.logo = value
}

const handleLogoUploadChange = (uploadFile: { raw?: File; url?: string }) => {
  if (uploadFile.raw) {
    const url = URL.createObjectURL(uploadFile.raw)
    logoUrl.value = url
    registerForm.logo = url
  } else if (uploadFile.url) {
    logoUrl.value = uploadFile.url
    registerForm.logo = uploadFile.url
  }
  return false
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  if (!registerForm.agreeToTerms) {
    ElMessage.warning('请阅读并同意注册协议')
    return
  }

  await registerFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    registerLoading.value = true

    try {
      const response = await axios.post('/api/auth/merchant/register', {
        username: registerForm.username,
        email: registerForm.email,
        password: registerForm.password,
        phone: registerForm.phone,
        contact_person: registerForm.contact_person,
        name: registerForm.name,
        logo: registerForm.logo,
        address: registerForm.address
      })

      if (response.data.code === 200 || response.data.code === 0) {
        ElMessage.success('注册成功，请等待审核')
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

const goToUserRegister = () => {
  router.push('/register')
}

const termsDialogVisible = ref(false)

const showTermsDialog = () => {
  termsDialogVisible.value = true
}
</script>

<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h1 class="register-title">商家注册</h1>
        <p class="register-subtitle">创建您的店铺账号</p>
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
            <span class="form-label">联系电话</span>
          </template>
          <el-input
            v-model="registerForm.phone"
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
            v-model="registerForm.contact_person"
            placeholder="请输入联系人姓名"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="name">
          <template #label>
            <span class="form-label">商家名称</span>
          </template>
          <el-input
            v-model="registerForm.name"
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
                  v-model="registerForm.logo"
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
            v-model="registerForm.address"
            placeholder="请输入地址"
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

        <el-form-item prop="agreeToTerms">
          <div class="terms-checkbox">
            <el-checkbox v-model="registerForm.agreeToTerms">
              我已阅读并同意
              <el-link type="primary" @click.prevent="showTermsDialog">《商家注册协议》</el-link>
            </el-checkbox>
          </div>
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

        <div class="register-divider">
          <span>或</span>
        </div>

        <div class="register-footer">
          <el-link type="primary" @click="goToUserRegister">用户注册</el-link>
        </div>
      </el-form>
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
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.register-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  padding: 40px;
  overflow: hidden;
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-title {
  font-size: 24px;
  font-weight: bold;
  color: #333333;
  margin: 0 0 8px 0;
}

.register-subtitle {
  font-size: 14px;
  color: #666666;
  margin: 0;
}

.register-form {
  margin-top: 0;
}

.form-label {
  font-weight: 500;
  color: #666666;
  font-size: 14px;
  margin-bottom: 8px;
  display: block;
}

.register-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 16px;
  background-color: #4CAF50;
  border-color: #4CAF50;
}

.register-button:hover {
  background-color: #45a049;
  border-color: #45a049;
}

.register-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
}

.footer-text {
  color: #666666;
  font-size: 14px;
}

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
  color: #999999;
  font-size: 12px;
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

.terms-checkbox {
  display: flex;
  align-items: center;
}

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
</style>
