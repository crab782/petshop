<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { Save, Upload } from '@element-plus/icons-vue'

defineOptions({
  name: 'AdminSystem'
})

const loading = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const basicForm = reactive({
  siteName: '宠物服务平台',
  siteDescription: '专业的宠物服务预约平台',
  siteLogo: '',
  contactEmail: 'admin@example.com',
  contactPhone: '400-888-8888',
  icpNumber: '京ICP备XXXXXXXX号',
  copyright: '© 2024 宠物服务平台 版权所有'
})

const emailForm = reactive({
  smtpHost: 'smtp.example.com',
  smtpPort: 465,
  smtpUsername: 'noreply@example.com',
  smtpPassword: '',
  fromEmail: 'noreply@example.com',
  fromName: '宠物服务平台',
  encryption: 'ssl',
  isEnabled: false
})

const smsForm = reactive({
  provider: 'aliyun',
  accessKeyId: '',
  accessKeySecret: '',
  signName: '宠物服务平台',
  isEnabled: false
})

const paymentForm = reactive({
  alipayEnabled: false,
  alipayAppId: '',
  alipayPrivateKey: '',
  alipayPublicKey: '',
  wechatEnabled: false,
  wechatMchId: '',
  wechatApiKey: '',
  wechatCertPath: '',
  wechatKeyPath: ''
})

const rules: FormRules = {
  siteName: [
    { required: true, message: '请输入网站名称', trigger: 'blur' },
    { max: 100, message: '网站名称不能超过100个字符', trigger: 'blur' }
  ],
  contactEmail: [
    { required: true, message: '请输入联系邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ],
  smtpHost: [
    { required: true, message: '请输入SMTP服务器地址', trigger: 'blur' }
  ],
  smtpPort: [
    { required: true, message: '请输入SMTP端口', trigger: 'blur' },
    { type: 'number', message: '端口必须是数字', trigger: 'blur' }
  ],
  smtpUsername: [
    { required: true, message: '请输入SMTP用户名', trigger: 'blur' }
  ],
  accessKeyId: [
    { required: true, message: '请输入AccessKey ID', trigger: 'blur' }
  ],
  accessKeySecret: [
    { required: true, message: '请输入AccessKey Secret', trigger: 'blur' }
  ],
  alipayAppId: [
    { required: true, message: '请输入支付宝AppID', trigger: 'blur' }
  ],
  wechatMchId: [
    { required: true, message: '请输入微信商户号', trigger: 'blur' }
  ]
}

const activeTab = ref('basic')

const handleSubmit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return

  await formEl.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 500))
      ElMessage.success('保存成功')
    } catch {
      ElMessage.error('保存失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleLogoUpload = () => {
  ElMessage.info('Logo上传功能开发中')
}
</script>

<template>
  <div class="admin-system">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统设置</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="settings-card">
      <template #header>
        <div class="card-header">
          <span class="title">系统设置</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="settings-tabs">
        <el-tab-pane label="基本设置" name="basic">
          <el-form
            ref="formRef"
            :model="basicForm"
            :rules="rules"
            label-width="120px"
            class="settings-form"
          >
            <el-form-item label="网站名称" prop="siteName">
              <el-input
                v-model="basicForm.siteName"
                placeholder="请输入网站名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="网站描述" prop="siteDescription">
              <el-input
                v-model="basicForm.siteDescription"
                type="textarea"
                placeholder="请输入网站描述"
                :rows="3"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="网站Logo" prop="siteLogo">
              <div class="logo-upload">
                <el-input
                  v-model="basicForm.siteLogo"
                  placeholder="请输入Logo URL或点击上传"
                  style="flex: 1;"
                />
                <el-button type="primary" @click="handleLogoUpload" :icon="Upload">
                  上传
                </el-button>
              </div>
              <div v-if="basicForm.siteLogo" class="logo-preview">
                <el-image :src="basicForm.siteLogo" fit="contain" style="max-width: 200px; max-height: 80px;" />
              </div>
            </el-form-item>

            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input
                v-model="basicForm.contactEmail"
                placeholder="请输入联系邮箱"
                maxlength="100"
              />
            </el-form-item>

            <el-form-item label="联系电话" prop="contactPhone">
              <el-input
                v-model="basicForm.contactPhone"
                placeholder="请输入联系电话"
                maxlength="20"
              />
            </el-form-item>

            <el-form-item label="ICP备案号" prop="icpNumber">
              <el-input
                v-model="basicForm.icpNumber"
                placeholder="请输入ICP备案号"
                maxlength="50"
              />
            </el-form-item>

            <el-form-item label="版权信息" prop="copyright">
              <el-input
                v-model="basicForm.copyright"
                placeholder="请输入版权信息"
                maxlength="200"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="邮件设置" name="email">
          <el-form
            ref="formRef"
            :model="emailForm"
            :rules="rules"
            label-width="140px"
            class="settings-form"
          >
            <el-form-item label="启用邮件服务">
              <el-switch v-model="emailForm.isEnabled" />
            </el-form-item>

            <el-form-item label="SMTP服务器" prop="smtpHost">
              <el-input
                v-model="emailForm.smtpHost"
                placeholder="如: smtp.gmail.com"
                :disabled="!emailForm.isEnabled"
              />
            </el-form-item>

            <el-form-item label="SMTP端口" prop="smtpPort">
              <el-input-number
                v-model="emailForm.smtpPort"
                :min="1"
                :max="65535"
                :disabled="!emailForm.isEnabled"
              />
            </el-form-item>

            <el-form-item label="加密方式">
              <el-radio-group v-model="emailForm.encryption" :disabled="!emailForm.isEnabled">
                <el-radio label="ssl">SSL</el-radio>
                <el-radio label="tls">TLS</el-radio>
                <el-radio label="none">无</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="SMTP用户名" prop="smtpUsername">
              <el-input
                v-model="emailForm.smtpUsername"
                placeholder="请输入SMTP用户名"
                :disabled="!emailForm.isEnabled"
              />
            </el-form-item>

            <el-form-item label="SMTP密码" prop="smtpPassword">
              <el-input
                v-model="emailForm.smtpPassword"
                type="password"
                placeholder="请输入SMTP密码（留空则不修改）"
                :disabled="!emailForm.isEnabled"
                show-password
              />
            </el-form-item>

            <el-form-item label="发件人邮箱" prop="fromEmail">
              <el-input
                v-model="emailForm.fromEmail"
                placeholder="请输入发件人邮箱"
                :disabled="!emailForm.isEnabled"
              />
            </el-form-item>

            <el-form-item label="发件人名称" prop="fromName">
              <el-input
                v-model="emailForm.fromName"
                placeholder="请输入发件人名称"
                :disabled="!emailForm.isEnabled"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="短信设置" name="sms">
          <el-form
            ref="formRef"
            :model="smsForm"
            :rules="rules"
            label-width="140px"
            class="settings-form"
          >
            <el-form-item label="启用短信服务">
              <el-switch v-model="smsForm.isEnabled" />
            </el-form-item>

            <el-form-item label="短信平台">
              <el-radio-group v-model="smsForm.provider" :disabled="!smsForm.isEnabled">
                <el-radio label="aliyun">阿里云</el-radio>
                <el-radio label="tencent">腾讯云</el-radio>
                <el-radio label="huawei">华为云</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="AccessKey ID" prop="accessKeyId">
              <el-input
                v-model="smsForm.accessKeyId"
                placeholder="请输入AccessKey ID"
                :disabled="!smsForm.isEnabled"
              />
            </el-form-item>

            <el-form-item label="AccessKey Secret" prop="accessKeySecret">
              <el-input
                v-model="smsForm.accessKeySecret"
                type="password"
                placeholder="请输入AccessKey Secret"
                :disabled="!smsForm.isEnabled"
                show-password
              />
            </el-form-item>

            <el-form-item label="短信签名" prop="signName">
              <el-input
                v-model="smsForm.signName"
                placeholder="请输入短信签名"
                :disabled="!smsForm.isEnabled"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="支付设置" name="payment">
          <div class="payment-section">
            <h3 class="section-title">支付宝配置</h3>
            <el-form
              ref="formRef"
              :model="paymentForm"
              :rules="rules"
              label-width="140px"
              class="settings-form"
            >
              <el-form-item label="启用支付宝">
                <el-switch v-model="paymentForm.alipayEnabled" />
              </el-form-item>

              <el-form-item label="支付宝AppID" prop="alipayAppId">
                <el-input
                  v-model="paymentForm.alipayAppId"
                  placeholder="请输入支付宝AppID"
                  :disabled="!paymentForm.alipayEnabled"
                />
              </el-form-item>

              <el-form-item label="应用私钥" prop="alipayPrivateKey">
                <el-input
                  v-model="paymentForm.alipayPrivateKey"
                  type="textarea"
                  placeholder="请输入应用私钥"
                  :rows="4"
                  :disabled="!paymentForm.alipayEnabled"
                />
              </el-form-item>

              <el-form-item label="支付宝公钥" prop="alipayPublicKey">
                <el-input
                  v-model="paymentForm.alipayPublicKey"
                  type="textarea"
                  placeholder="请输入支付宝公钥"
                  :rows="4"
                  :disabled="!paymentForm.alipayEnabled"
                />
              </el-form-item>
            </el-form>
          </div>

          <el-divider />

          <div class="payment-section">
            <h3 class="section-title">微信支付配置</h3>
            <el-form
              ref="formRef"
              :model="paymentForm"
              :rules="rules"
              label-width="140px"
              class="settings-form"
            >
              <el-form-item label="启用微信支付">
                <el-switch v-model="paymentForm.wechatEnabled" />
              </el-form-item>

              <el-form-item label="商户号" prop="wechatMchId">
                <el-input
                  v-model="paymentForm.wechatMchId"
                  placeholder="请输入微信商户号"
                  :disabled="!paymentForm.wechatEnabled"
                />
              </el-form-item>

              <el-form-item label="API密钥" prop="wechatApiKey">
                <el-input
                  v-model="paymentForm.wechatApiKey"
                  type="password"
                  placeholder="请输入API密钥"
                  :disabled="!paymentForm.wechatEnabled"
                  show-password
                />
              </el-form-item>

              <el-form-item label="证书路径">
                <el-input
                  v-model="paymentForm.wechatCertPath"
                  placeholder="请输入apiclient_cert.pem路径"
                  :disabled="!paymentForm.wechatEnabled"
                />
              </el-form-item>

              <el-form-item label="密钥路径">
                <el-input
                  v-model="paymentForm.wechatKeyPath"
                  placeholder="请输入apiclient_key.pem路径"
                  :disabled="!paymentForm.wechatEnabled"
                />
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>

      <div class="form-actions">
        <el-button type="primary" @click="handleSubmit(formRef)" :loading="submitting">
          <el-icon v-if="!submitting"><Save /></el-icon>
          保存设置
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.admin-system {
  padding: 0;
}

.settings-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: 600;
}

.settings-tabs {
  margin-top: 10px;
}

.settings-form {
  max-width: 700px;
  margin: 20px 0;
}

.logo-upload {
  display: flex;
  gap: 10px;
  align-items: center;
}

.logo-preview {
  margin-top: 10px;
  padding: 10px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  display: inline-block;
}

.payment-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}

.form-actions {
  padding: 20px 0;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
}
</style>