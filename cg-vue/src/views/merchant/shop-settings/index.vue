<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElCard, ElForm, ElFormItem, ElCheckboxGroup, ElCheckbox, ElTimeSelect, ElSwitch, ElDatePicker, ElInputNumber, ElButton, ElMessage, ElTag, ElInput, ElDivider, ElTabs, ElTabPane, ElRow, ElCol, ElAvatar, ElEmpty, ElIcon, ElDescriptions, ElDescriptionsItem } from 'element-plus'
import { User, Shop } from '@element-plus/icons-vue'
import { getMerchantSettings, updateMerchantSettings, getMerchantInfo, changePassword, bindPhone, bindEmail, sendVerifyCode, type MerchantSettings, type MerchantInfo } from '@/api/merchant'

const loading = ref(false)
const saving = ref(false)
const infoLoading = ref(false)
const passwordLoading = ref(false)
const bindLoading = ref(false)

const merchantInfo = ref<MerchantInfo | null>(null)

const weekDays = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 0 }
]

const timeOptions = ref<{ label: string, value: string }[]>([])

for (let h = 0; h < 24; h++) {
  for (let m = 0; m < 60; m += 30) {
    const hour = h.toString().padStart(2, '0')
    const minute = m.toString().padStart(2, '0')
    const value = `${hour}:${minute}`
    const label = `${hour}:${minute}`
    timeOptions.value.push({ label, value })
  }
}

const settingsForm = ref<MerchantSettings>({
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
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const phoneForm = reactive({
  phone: '',
  verifyCode: ''
})

const emailForm = reactive({
  email: '',
  verifyCode: ''
})

const verifyCodeSending = ref(false)
const verifyCodeText = ref('发送验证码')
let verifyCodeTimer: ReturnType<typeof setInterval> | null = null

const fetchMerchantInfo = async () => {
  infoLoading.value = true
  try {
    const res = await getMerchantInfo()
    if (res.data) {
      merchantInfo.value = res.data
    }
  } catch (error) {
    console.error('获取店铺信息失败', error)
  } finally {
    infoLoading.value = false
  }
}

const fetchSettings = async () => {
  loading.value = true
  try {
    const res = await getMerchantSettings()
    if (res.data) {
      settingsForm.value = {
        ...settingsForm.value,
        ...res.data,
        notificationSettings: {
          ...settingsForm.value.notificationSettings,
          ...res.data.notificationSettings
        }
      }
    }
  } catch (error) {
    ElMessage.error('获取店铺设置失败')
  } finally {
    loading.value = false
  }
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

const handleChangePassword = async () => {
  if (!passwordForm.oldPassword) {
    ElMessage.warning('请输入原密码')
    return
  }
  if (!passwordForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  if (passwordForm.newPassword.length < 6) {
    ElMessage.warning('新密码长度不能少于6位')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  passwordLoading.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    ElMessage.error('密码修改失败')
  } finally {
    passwordLoading.value = false
  }
}

const handleSendVerifyCode = async (type: 'phone' | 'email') => {
  const form = type === 'phone' ? phoneForm : emailForm
  const value = type === 'phone' ? form.phone : form.email

  if (!value) {
    ElMessage.warning(`请输入${type === 'phone' ? '手机号' : '邮箱'}`)
    return
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (type === 'email' && !emailRegex.test(value)) {
    ElMessage.warning('请输入正确的邮箱格式')
    return
  }

  const phoneRegex = /^1[3-9]\d{9}$/
  if (type === 'phone' && !phoneRegex.test(value)) {
    ElMessage.warning('请输入正确的手机号格式')
    return
  }

  verifyCodeSending.value = true
  try {
    await sendVerifyCode(type, value)
    ElMessage.success('验证码发送成功')

    let countdown = 60
    verifyCodeText.value = `${countdown}秒后重新发送`
    if (verifyCodeTimer) clearInterval(verifyCodeTimer)

    verifyCodeTimer = setInterval(() => {
      countdown--
      if (countdown <= 0) {
        if (verifyCodeTimer) clearInterval(verifyCodeTimer)
        verifyCodeText.value = '发送验证码'
        verifyCodeSending.value = false
      } else {
        verifyCodeText.value = `${countdown}秒后重新发送`
      }
    }, 1000)
  } catch (error) {
    ElMessage.error('验证码发送失败')
    verifyCodeSending.value = false
  }
}

const handleBindPhone = async () => {
  if (!phoneForm.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  if (!phoneForm.verifyCode) {
    ElMessage.warning('请输入验证码')
    return
  }
  bindLoading.value = true
  try {
    await bindPhone({
      phone: phoneForm.phone,
      verifyCode: phoneForm.verifyCode
    })
    ElMessage.success('手机号绑定成功')
    phoneForm.phone = ''
    phoneForm.verifyCode = ''
    fetchMerchantInfo()
  } catch (error) {
    ElMessage.error('手机号绑定失败')
  } finally {
    bindLoading.value = false
  }
}

const handleBindEmail = async () => {
  if (!emailForm.email) {
    ElMessage.warning('请输入邮箱')
    return
  }
  if (!emailForm.verifyCode) {
    ElMessage.warning('请输入验证码')
    return
  }
  bindLoading.value = true
  try {
    await bindEmail({
      email: emailForm.email,
      verifyCode: emailForm.verifyCode
    })
    ElMessage.success('邮箱绑定成功')
    emailForm.email = ''
    emailForm.verifyCode = ''
    fetchMerchantInfo()
  } catch (error) {
    ElMessage.error('邮箱绑定失败')
  } finally {
    bindLoading.value = false
  }
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
</script>

<template>
  <div class="shop-settings">
    <div class="page-header">
      <h2 class="page-title">店铺设置</h2>
    </div>

    <el-tabs>
      <el-tab-pane label="店铺信息">
        <el-card v-loading="infoLoading" class="info-card">
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
                  {{ merchantInfo.contactPerson || '-' }}
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
                  <el-tag :type="getStatusType(merchantInfo.status)">
                    {{ getStatusText(merchantInfo.status) }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="注册时间">
                  {{ formatDate(merchantInfo.createdAt) }}
                </el-descriptions-item>
              </el-descriptions>
            </el-col>
          </el-row>
          <el-empty v-else description="暂无店铺信息" />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="营业设置">
        <el-card v-loading="loading" class="settings-card">
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
        <el-card v-loading="loading" class="settings-card">
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
          <el-form label-width="100px" label-position="left" style="max-width: 400px;">
            <el-form-item label="原密码">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码（至少6位）"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>

          <el-divider content-position="left">绑定手机号</el-divider>
          <el-form label-width="100px" label-position="left" style="max-width: 400px;">
            <el-form-item label="当前手机">
              <span>{{ merchantInfo?.phone || '未绑定' }}</span>
            </el-form-item>
            <el-form-item label="新手机号">
              <el-input
                v-model="phoneForm.phone"
                placeholder="请输入手机号"
                style="width: 200px;"
              />
              <el-button
                @click="handleSendVerifyCode('phone')"
                :disabled="verifyCodeSending"
                style="margin-left: 10px;"
              >
                {{ verifyCodeText }}
              </el-button>
            </el-form-item>
            <el-form-item label="验证码">
              <el-input
                v-model="phoneForm.verifyCode"
                placeholder="请输入验证码"
                style="width: 200px;"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="bindLoading" @click="handleBindPhone">
                绑定手机号
              </el-button>
            </el-form-item>
          </el-form>

          <el-divider content-position="left">绑定邮箱</el-divider>
          <el-form label-width="100px" label-position="left" style="max-width: 400px;">
            <el-form-item label="当前邮箱">
              <span>{{ merchantInfo?.email || '未绑定' }}</span>
            </el-form-item>
            <el-form-item label="新邮箱">
              <el-input
                v-model="emailForm.email"
                placeholder="请输入邮箱"
                style="width: 200px;"
              />
              <el-button
                @click="handleSendVerifyCode('email')"
                :disabled="verifyCodeSending"
                style="margin-left: 10px;"
              >
                {{ verifyCodeText }}
              </el-button>
            </el-form-item>
            <el-form-item label="验证码">
              <el-input
                v-model="emailForm.verifyCode"
                placeholder="请输入验证码"
                style="width: 200px;"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="bindLoading" @click="handleBindEmail">
                绑定邮箱
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
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
