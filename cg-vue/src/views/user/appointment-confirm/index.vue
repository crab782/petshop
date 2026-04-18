<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Clock, Money, User, Phone, Location, Calendar } from '@element-plus/icons-vue'
import { getUserPets, createAppointment, getMerchantInfo, type Pet as PetType, type Service, type MerchantInfo } from '@/api/user'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const service = ref<Service | null>(null)
const merchant = ref<MerchantInfo | null>(null)
const pets = ref<PetType[]>([])
const formRef = ref<FormInstance>()

const selectedPetId = ref<number | null>(null)
const appointmentTime = ref<string>('')
const remark = ref('')

const formData = computed(() => ({
  petId: selectedPetId.value,
  appointmentTime: appointmentTime.value,
  remark: remark.value
}))

const formRules: FormRules = {
  petId: [
    { required: true, message: '请选择预约宠物', trigger: 'change' }
  ],
  appointmentTime: [
    { required: true, message: '请选择预约时间', trigger: 'change' }
  ]
}

const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7
}

const disabledHours = () => {
  const hours: number[] = []
  for (let i = 0; i < 8; i++) {
    hours.push(i)
  }
  for (let i = 22; i < 24; i++) {
    hours.push(i)
  }
  return hours
}

const fetchServiceInfo = async () => {
  const serviceData = route.query.service
  if (serviceData) {
    try {
      service.value = JSON.parse(serviceData as string)
      if (service.value?.merchantId) {
        await fetchMerchantInfo(service.value.merchantId)
      }
    } catch {
      ElMessage.error('服务信息解析失败')
      router.push('/user/services')
    }
  } else {
    ElMessage.error('缺少服务参数')
    router.push('/user/services')
  }
}

const fetchMerchantInfo = async (merchantId: number) => {
  try {
    const res = await getMerchantInfo(merchantId)
    merchant.value = res.data || null
  } catch {
    ElMessage.error('获取商家信息失败')
  }
}

const fetchPets = async () => {
  try {
    const res = await getUserPets()
    pets.value = res.data || []
    if (pets.value.length > 0) {
      selectedPetId.value = pets.value[0].id
    }
  } catch {
    ElMessage.error('获取宠物列表失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    if (!selectedPetId.value) {
      ElMessage.warning('请选择预约宠物')
      return
    }
    if (!appointmentTime.value) {
      ElMessage.warning('请选择预约时间')
      return
    }

    submitting.value = true
    try {
      await createAppointment({
        serviceId: service.value!.id,
        petId: selectedPetId.value,
        appointmentTime: appointmentTime.value,
        remark: remark.value
      })
      ElMessage.success('预约成功')
      router.push('/user/appointments')
    } catch {
      ElMessage.error('预约失败，请稍后重试')
    } finally {
      submitting.value = false
    }
  })
}

const handleCancel = () => {
  router.back()
}

const formatDuration = (minutes: number | undefined) => {
  if (!minutes) return '60分钟'
  if (minutes >= 60) {
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
  }
  return `${minutes}分钟`
}

onMounted(() => {
  loading.value = true
  Promise.all([fetchServiceInfo(), fetchPets()]).finally(() => {
    loading.value = false
  })
})
</script>

<template>
  <div class="confirm-page">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="title">确认预约</span>
        </div>
      </template>

      <div v-if="service" class="confirm-content">
        <div class="service-info-card">
          <h3 class="section-title">服务信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="服务名称">
              <el-tag type="primary" size="large">{{ service.name }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="服务价格">
              <span class="price">¥{{ service.price }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="服务时长">
              <el-icon><Clock /></el-icon>
              {{ formatDuration(service.duration) }}
            </el-descriptions-item>
            <el-descriptions-item label="服务类型">
              {{ service.category || '宠物服务' }}
            </el-descriptions-item>
            <el-descriptions-item label="服务描述" :span="2">
              {{ service.description || '暂无描述' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-if="merchant" class="merchant-info-card">
          <h3 class="section-title">商家信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="商家名称">
              <div class="merchant-name-cell">
                <el-avatar
                  v-if="merchant.logo"
                  :src="merchant.logo"
                  :size="32"
                  class="merchant-logo"
                />
                <span>{{ merchant.name }}</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="商家评分">
              <el-rate
                :model-value="merchant.rating || 5"
                disabled
                show-score
                text-color="#ff9900"
              />
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              <el-icon><Phone /></el-icon>
              {{ merchant.phone || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="商家地址">
              <el-icon><Location /></el-icon>
              {{ merchant.address || '暂无' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <el-divider />

        <div class="booking-form">
          <h3 class="section-title">预约信息</h3>

          <el-form
            ref="formRef"
            :model="formData"
            :rules="formRules"
            label-width="100px"
            class="form"
          >
            <el-form-item label="选择宠物" prop="petId">
              <el-select
                v-model="selectedPetId"
                placeholder="请选择预约宠物"
                class="full-width"
              >
                <el-option
                  v-for="pet in pets"
                  :key="pet.id"
                  :label="`${pet.name} (${pet.type}${pet.breed ? ' - ' + pet.breed : ''})`"
                  :value="pet.id"
                >
                  <div class="pet-option">
                    <el-avatar
                      v-if="pet.avatar"
                      :src="pet.avatar"
                      :size="28"
                    />
                    <el-icon v-else><User /></el-icon>
                    <span class="pet-name">{{ pet.name }}</span>
                    <span class="pet-type">{{ pet.type }}</span>
                    <span v-if="pet.breed" class="pet-breed">{{ pet.breed }}</span>
                  </div>
                </el-option>
              </el-select>
              <div v-if="pets.length === 0" class="no-pet-tip">
                <el-text type="info">暂无宠物，请先添加宠物</el-text>
                <el-button type="primary" link @click="router.push('/user/pets')">
                  去添加
                </el-button>
              </div>
            </el-form-item>

            <el-form-item label="预约时间" prop="appointmentTime">
              <el-date-picker
                v-model="appointmentTime"
                type="datetime"
                placeholder="请选择预约时间"
                :disabled-date="disabledDate"
                :disabled-hours="disabledHours"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                class="full-width"
              >
                <template #default>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-date-picker>
              <div class="time-tip">
                <el-text type="info" size="small">营业时间：08:00 - 22:00</el-text>
              </div>
            </el-form-item>

            <el-form-item label="备注信息">
              <el-input
                v-model="remark"
                type="textarea"
                :rows="3"
                placeholder="如有特殊要求，请在此说明（如宠物性格、注意事项等）..."
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-form>
        </div>

        <el-divider />

        <div class="fee-info">
          <h3 class="section-title">费用说明</h3>
          <el-card shadow="never" class="fee-card">
            <div class="fee-detail">
              <div class="fee-row">
                <span class="fee-label">
                  <el-icon><Money /></el-icon>
                  服务费用：
                </span>
                <span class="fee-value">¥{{ service.price }}</span>
              </div>
              <div class="fee-row">
                <span class="fee-label">
                  <el-icon><Clock /></el-icon>
                  服务时长：
                </span>
                <span class="fee-value">{{ formatDuration(service.duration) }}</span>
              </div>
              <div class="fee-row">
                <span class="fee-label">预约方式：</span>
                <span class="fee-value">到店支付</span>
              </div>
              <el-divider style="margin: 16px 0" />
              <div class="fee-row total">
                <span class="fee-label">合计费用：</span>
                <span class="fee-value total-price">¥{{ service.price }}</span>
              </div>
              <p class="fee-tip">* 费用将于服务完成后支付，如有额外费用以实际为准</p>
            </div>
          </el-card>
        </div>

        <div class="submit-section">
          <el-button size="large" @click="handleCancel">
            返回
          </el-button>
          <el-button
            type="primary"
            size="large"
            :loading="submitting"
            :disabled="pets.length === 0"
            @click="handleSubmit"
          >
            确认预约
          </el-button>
        </div>
      </div>

      <el-empty v-else description="加载服务信息中..." />
    </el-card>
  </div>
</template>

<style scoped>
.confirm-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.confirm-content {
  padding: 10px 0;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.service-info-card {
  margin-bottom: 20px;
}

.merchant-info-card {
  margin-top: 20px;
  margin-bottom: 10px;
}

.merchant-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.merchant-logo {
  flex-shrink: 0;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.booking-form {
  margin: 20px 0;
}

.form {
  max-width: 500px;
}

.full-width {
  width: 100%;
}

.pet-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pet-name {
  font-weight: 500;
}

.pet-type {
  color: #909399;
  font-size: 12px;
}

.pet-breed {
  color: #c0c4cc;
  font-size: 12px;
}

.no-pet-tip {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-tip {
  margin-top: 8px;
}

.fee-info {
  margin: 20px 0;
}

.fee-card {
  background-color: #fafafa;
}

.fee-detail {
  padding: 5px 0;
}

.fee-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.fee-label {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
}

.fee-value {
  font-weight: 500;
  color: #303133;
}

.fee-row.total {
  font-weight: bold;
}

.total-price {
  color: #f56c6c;
  font-size: 24px;
}

.fee-tip {
  font-size: 12px;
  color: #909399;
  margin: 12px 0 0 0;
}

.submit-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.submit-section .el-button {
  min-width: 120px;
}

@media (max-width: 768px) {
  .confirm-page {
    padding: 10px;
  }

  .submit-section {
    flex-direction: column;
  }

  .submit-section .el-button {
    width: 100%;
  }
}
</style>
