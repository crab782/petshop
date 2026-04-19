<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted } from 'vue'
import {
  ElCard,
  ElForm,
  ElFormItem,
  ElCheckboxGroup,
  ElCheckbox,
  ElTimeSelect,
  ElSwitch,
  ElDatePicker,
  ElInputNumber,
  ElButton,
  ElMessage,
  ElTag,
  ElInput,
  ElDivider,
  ElTabs,
  ElTabPane,
  ElRow,
  ElCol,
  ElAvatar,
  ElEmpty,
  ElIcon,
  ElDescriptions,
  ElDescriptionsItem,
  ElDialog
} from 'element-plus'
import { User, Shop } from '@element-plus/icons-vue'
import {
  getMerchantSettings,
  updateMerchantSettings,
  getMerchantInfo,
  changePassword,
  bindPhone,
  bindEmail,
  sendVerifyCode,
  type MerchantSettings,
  type MerchantInfo
} from '@/api/merchant'
import { useAsync, useForm } from '@/composables'

const weekDays = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 0 }
]

const defaultSettings: MerchantSettings = {
  businessDays: [1, 2, 3, 4, 5],
  startTime: '09:00',
  endTime: '18:00',
  legalHolidayRest: false,
  customRestDays: [],
  maxAppointments: 10,
  advanceBookingHours: 24,
  isOpen: true,
  notificationSettings: {
    appointmentReminder: true,
    orderReminder: true,
    reviewReminder: true,
    notifyViaSms: false,
    notifyViaEmail: true
  }
}

const settingsAsync = useAsync(getMerchantSettings, defaultSettings)
const merchantInfoAsync = useAsync(getMerchantInfo)

const settingsForm = computed(() => settingsAsync.data.value || defaultSettings)
const merchantInfo = computed(() => merchantInfoAsync.data.value)

const saving = ref(false)

const passwordDialogVisible = ref(false)
const phoneDialogVisible = ref(false)
const emailDialogVisible = ref(false)

const passwordForm = useForm({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const phoneForm = useForm({
  phone: '',
  verifyCode: ''
})

const emailForm = useForm({
  email: '',
  verifyCode: ''
})

const passwordLoading = ref(false)
const bindLoading = ref(false)
const verifyCodeSending = ref(false)
const verifyCodeCountdown = ref(0)
let verifyCodeTimer: ReturnType<typeof setInterval> | null = null

const verifyCodeText = computed(() => {
  if (verifyCodeCountdown.value > 0) {
    return `${verifyCodeCountdown.value}秒后重新发送`
  }
  return '发送验证码'
})

const passwordRules = {
  oldPassword: (value: string) => {
    if (!value) return '请输入原密码'
    return null
  },
  newPassword: (value: string) => {
    if (!value) return '请输入新密码'
    if (value.length < 6) return '新密码长度不能少于6位'
    return null
  },
  confirmPassword: (value: string) => {
    if (!value) return '请再次输入新密码'
    if (value !== passwordForm.formData.newPassword) return '两次输入的密码不一致'
    return null
  }
}

const phoneRules = {
  phone: (value: string) => {
    if (!value) return '请输入手机号'
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(value)) return '请输入正确的手机号格式'
    return null
  },
  verifyCode: (value: string) => {
    if (!value) return '请输入验证码'
    if (!/^\d{6}$/.test(value)) return '验证码为6位数字'
    return null
  }
}

const emailRules = {
  email: (value: string) => {
    if (!value) return '请输入邮箱'
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(value)) return '请输入正确的邮箱格式'
    return null
  },
  verifyCode: (value: string) => {
    if (!value) return '请输入验证码'
    if (!/^\d{6}$/.test(value)) return '验证码为6位数字'
    return null
  }
}

const fetchSettings = async () => {
  await settingsAsync.execute()
}

const fetchMerchantInfo = async () => {
  await merchantInfoAsync.execute()
}

const handleSave = async () => {
  saving.value = true
  try {
    await updateMerchantSettings(settingsForm.value)
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const openPasswordDialog = () => {
  passwordForm.reset()
  passwordDialogVisible.value = true
}

const handleChangePassword = async () => {
  if (!passwordForm.validate(passwordRules)) {
    return
  }

  await passwordForm.handleSubmit(async (data) => {
    passwordLoading.value = true
    try {
      await changePassword({
        oldPassword: data.oldPassword,
        newPassword: data.newPassword
      })
      ElMessage.success('密码修改成功')
      passwordDialogVisible.value = false
      passwordForm.reset()
    } catch (error) {
      ElMessage.error('密码修改失败')
    } finally {
      passwordLoading.value = false
    }
  })
}

const openPhoneDialog = () => {
  phoneForm.reset()
  phoneDialogVisible.value = true
}

const handleSendVerifyCode = async (type: 'phone' | 'email') => {
  const form = type === 'phone' ? phoneForm : emailForm
  const fieldName = type === 'phone' ? 'phone' : 'email'
  const value = form.formData[fieldName]

  if (!value) {
    ElMessage.warning(`请输入${type === 'phone' ? '手机号' : '邮箱'}`)
    return
  }

  if (type === 'email') {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(value)) {
      ElMessage.warning('请输入正确的邮箱格式')
      return
    }
  }

  if (type === 'phone') {
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(value)) {
      ElMessage.warning('请输入正确的手机号格式')
      return
    }
  }

  verifyCodeSending.value = true
  try {
    await sendVerifyCode(type, value)
    ElMessage.success('验证码发送成功')

    verifyCodeCountdown.value = 60
    if (verifyCodeTimer) clearInterval(verifyCodeTimer)

    verifyCodeTimer = setInterval(() => {
      verifyCodeCountdown.value--
      if (verifyCodeCountdown.value <= 0) {
        if (verifyCodeTimer) clearInterval(verifyCodeTimer)
        verifyCodeTimer = null
      }
    }, 1000)
  } catch (error) {
    ElMessage.error('验证码发送失败')
  } finally {
    verifyCodeSending.value = false
  }
}

const handleBindPhone = async () => {
  if (!phoneForm.validate(phoneRules)) {
    return
  }

  await phoneForm.handleSubmit(async (data) => {
    bindLoading.value = true
    try {
      await bindPhone({
        phone: data.phone,
        verifyCode: data.verifyCode
      })
      ElMessage.success('手机号绑定成功')
      phoneDialogVisible.value = false
      phoneForm.reset()
      fetchMerchantInfo()
    } catch (error) {
      ElMessage.error('手机号绑定失败')
    } finally {
      bindLoading.value = false
    }
  })
}

const openEmailDialog = () => {
  emailForm.reset()
  emailDialogVisible.value = true
}

const handleBindEmail = async () => {
  if (!emailForm.validate(emailRules)) {
    return
  }

  await emailForm.handleSubmit(async (data) => {
    bindLoading.value = true
    try {
      await bindEmail({
        email: data.email,
        verifyCode: data.verifyCode
      })
      ElMessage.success('邮箱绑定成功')
      emailDialogVisible.value = false
      emailForm.reset()
      fetchMerchantInfo()
    } catch (error) {
      ElMessage.error('邮箱绑定失败')
    } finally {
      bindLoading.value = false
    }
  })
}

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return statusMap[status] || status
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchMerchantInfo()
  fetchSettings()
})

onUnmounted(() => {
  if (verifyCodeTimer) {
    clearInterval(verifyCodeTimer)
    verifyCodeTimer = null
  }
})
</script>

<template>
  <div class="shop-settings">
    <div class="page-header">
      <h2 class="page-title">店铺设置</h2>
    </div>

    <el-tabs>
      <el-tab-pane label="店铺信息">
        <el-card v-loading="merchantInfoAsync.loading.value" class="info-card">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
            </div>
          </template>
          <el-row :gutter="20" v-if="merchantInfo">
            <el-col :span="6">
              <div class="logo-section">
                <el-avatar :size="100" :src="merchantInfo.logo" v-if="merchantInfo.logo">
                  <el-icon :size="50"><User /></el-icon>
                </el-avatar>
                <el-avatar :size="100" v-else>
                  <el-icon :size="50"><Shop /></el-icon>
                </el-avatar>
              </div>
            </el-col>
            <el-col :span="18">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="商家名称">
                  {{ merchantInfo.name }}
                </el-descriptions-item>
                <el-descriptions-item label="联系人">
                  {{ merchantInfo.contact_person || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="联系电话">
                  {{ merchantInfo.phone || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="邮箱">
                  {{ merchantInfo.email || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="地址">
                  {{ merchantInfo.address || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="审核状态">
                  <el-tag :type="getStatusType(merchantInfo.status || '')">
                    {{ getStatusText(merchantInfo.status || '') }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="注册时间">
                  {{ formatDate(merchantInfo.created_at || '') }}
                </el-descriptions-item>
              </el-descriptions>
            </el-col>
          </el-row>
          <el-empty v-else description="暂无店铺信息" />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="营业设置">
        <el-card v-loading="settingsAsync.loading.value" class="settings-card">
          <el-form label-width="140px" label-position="left">
            <el-form-item label="店铺状态">
              <el-switch
                v-model="settingsForm.isOpen"
                active-text="营业中"
                inactive-text="休息中"
                :width="60"
              />
              <span class="status-tip" v-if="settingsForm.isOpen">店铺正在营业中</span>
              <span class="status-tip closed" v-else>店铺已关闭，暂时不接受预约</span>
            </el-form-item>

            <el-divider />

            <el-form-item label="营业日设置">
              <el-checkbox-group v-model="settingsForm.businessDays">
                <el-checkbox v-for="day in weekDays" :key="day.value" :value="day.value">
                  {{ day.label }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="每日营业时间">
              <el-time-select
                v-model="settingsForm.startTime"
                placeholder="开始时间"
                start="00:00"
                step="00:30"
                end="23:30"
                style="margin-right: 12px;"
              />
              <span style="margin: 0 8px; color: #909399;">至</span>
              <el-time-select
                v-model="settingsForm.endTime"
                placeholder="结束时间"
                start="00:00"
                step="00:30"
                end="23:30"
              />
            </el-form-item>

            <el-divider />

            <el-form-item label="法定节假日休息">
              <el-switch v-model="settingsForm.legalHolidayRest" />
            </el-form-item>

            <el-form-item label="自定义休息日">
              <el-date-picker
                v-model="settingsForm.customRestDays"
                type="dates"
                placeholder="选择日期"
                style="width: 100%;"
                :clearable="true"
              />
            </el-form-item>

            <el-divider />

            <el-form-item label="最大预约人数">
              <el-input-number
                v-model="settingsForm.maxAppointments"
                :min="1"
                :max="100"
                style="width: 200px;"
              />
            </el-form-item>

            <el-form-item label="预约提前时间">
              <el-input-number
                v-model="settingsForm.advanceBookingHours"
                :min="1"
                :max="168"
                style="width: 200px;"
              />
              <span style="margin-left: 8px; color: #909399;">小时</span>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="通知设置">
        <el-card v-loading="settingsAsync.loading.value" class="settings-card">
          <el-form label-width="140px" label-position="left">
            <el-form-item label="预约提醒">
              <el-switch v-model="settingsForm.notificationSettings.appointmentReminder" />
              <span class="setting-tip">收到新预约时通知</span>
            </el-form-item>

            <el-form-item label="订单提醒">
              <el-switch v-model="settingsForm.notificationSettings.orderReminder" />
              <span class="setting-tip">收到新订单时通知</span>
            </el-form-item>

            <el-form-item label="评价提醒">
              <el-switch v-model="settingsForm.notificationSettings.reviewReminder" />
              <span class="setting-tip">收到新评价时通知</span>
            </el-form-item>

            <el-divider />

            <el-form-item label="通知方式">
              <div class="notify-channels">
                <el-checkbox v-model="settingsForm.notificationSettings.notifyViaSms">
                  短信通知
                </el-checkbox>
                <el-checkbox v-model="settingsForm.notificationSettings.notifyViaEmail">
                  邮件通知
                </el-checkbox>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="账户安全">
        <el-card class="settings-card">
          <el-divider content-position="left">修改密码</el-divider>
          <div class="security-item">
            <div class="security-info">
              <span class="security-label">登录密码</span>
              <span class="security-desc">定期修改密码可以提高账户安全性</span>
            </div>
            <el-button type="primary" plain @click="openPasswordDialog">
              修改密码
            </el-button>
          </div>

          <el-divider content-position="left">绑定手机号</el-divider>
          <div class="security-item">
            <div class="security-info">
              <span class="security-label">当前手机</span>
              <span class="security-desc">{{ merchantInfo?.phone || '未绑定' }}</span>
            </div>
            <el-button type="primary" plain @click="openPhoneDialog">
              {{ merchantInfo?.phone ? '更换手机号' : '绑定手机号' }}
            </el-button>
          </div>

          <el-divider content-position="left">绑定邮箱</el-divider>
          <div class="security-item">
            <div class="security-info">
              <span class="security-label">当前邮箱</span>
              <span class="security-desc">{{ merchantInfo?.email || '未绑定' }}</span>
            </div>
            <el-button type="primary" plain @click="openEmailDialog">
              {{ merchantInfo?.email ? '更换邮箱' : '绑定邮箱' }}
            </el-button>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form
        label-width="100px"
        label-position="left"
        @submit.prevent="handleChangePassword"
      >
        <el-form-item label="原密码" :error="passwordForm.errors.oldPassword">
          <el-input
            v-model="passwordForm.formData.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" :error="passwordForm.errors.newPassword">
          <el-input
            v-model="passwordForm.formData.newPassword"
            type="password"
            placeholder="请输入新密码（至少6位）"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" :error="passwordForm.errors.confirmPassword">
          <el-input
            v-model="passwordForm.formData.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">
          确认修改
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="phoneDialogVisible"
      title="绑定手机号"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form
        label-width="100px"
        label-position="left"
        @submit.prevent="handleBindPhone"
      >
        <el-form-item label="新手机号" :error="phoneForm.errors.phone">
          <el-input
            v-model="phoneForm.formData.phone"
            placeholder="请输入手机号"
          />
        </el-form-item>
        <el-form-item label="验证码" :error="phoneForm.errors.verifyCode">
          <div class="verify-code-input">
            <el-input
              v-model="phoneForm.formData.verifyCode"
              placeholder="请输入验证码"
              maxlength="6"
            />
            <el-button
              @click="handleSendVerifyCode('phone')"
              :disabled="verifyCodeSending || verifyCodeCountdown > 0"
            >
              {{ verifyCodeText }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="phoneDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="bindLoading" @click="handleBindPhone">
          确认绑定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="emailDialogVisible"
      title="绑定邮箱"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form
        label-width="100px"
        label-position="left"
        @submit.prevent="handleBindEmail"
      >
        <el-form-item label="新邮箱" :error="emailForm.errors.email">
          <el-input
            v-model="emailForm.formData.email"
            placeholder="请输入邮箱"
          />
        </el-form-item>
        <el-form-item label="验证码" :error="emailForm.errors.verifyCode">
          <div class="verify-code-input">
            <el-input
              v-model="emailForm.formData.verifyCode"
              placeholder="请输入验证码"
              maxlength="6"
            />
            <el-button
              @click="handleSendVerifyCode('email')"
              :disabled="verifyCodeSending || verifyCodeCountdown > 0"
            >
              {{ verifyCodeText }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="emailDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="bindLoading" @click="handleBindEmail">
          确认绑定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.shop-settings {
  padding: 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.settings-card {
  max-width: 800px;
}

.info-card {
  max-width: 900px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.status-tip {
  margin-left: 12px;
  color: #67c23a;
  font-size: 13px;
}

.status-tip.closed {
  color: #909399;
}

.setting-tip {
  margin-left: 12px;
  color: #909399;
  font-size: 13px;
}

.notify-channels {
  display: flex;
  gap: 20px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
}

.security-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.security-label {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.security-desc {
  font-size: 13px;
  color: #909399;
}

.verify-code-input {
  display: flex;
  gap: 10px;
}

.verify-code-input .el-input {
  flex: 1;
}

:deep(.el-tabs__nav-wrap) {
  background: #fff;
  padding: 0 20px;
  border-radius: 8px 8px 0 0;
}

:deep(.el-tabs__content) {
  background: #f5f7fa;
  padding: 20px;
}

:deep(.el-tabs__active-bar) {
  height: 3px;
}

:deep(.el-card) {
  border-radius: 8px;
}

:deep(.el-divider--horizontal) {
  margin: 24px 0;
}
</style>
