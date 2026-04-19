<script setup lang="ts">
import { ref, computed, onMounted, watch, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAsync, useForm } from '@/composables'
import { addService, updateService, getServiceById } from '@/api/merchant'
import type { MerchantService } from '@/api/merchant'
import type { FormInstance, FormRules } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const serviceId = computed(() => {
  const id = route.params.id || route.query.id
  return id ? Number(id) : null
})
const isEdit = computed(() => !!serviceId.value)
const pageTitle = computed(() => isEdit.value ? '编辑服务' : '添加服务')

const formRef = ref<FormInstance>()

const initialFormData = {
  name: '',
  description: '',
  price: 0,
  duration: 0,
  image: ''
}

const rules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入服务名称', trigger: 'blur' },
    { max: 100, message: '服务名称最多100个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格最小为0', trigger: 'blur' }
  ],
  duration: [
    { required: true, message: '请输入时长', trigger: 'blur' },
    { type: 'number', min: 1, message: '时长最少1分钟', trigger: 'blur' }
  ]
})

const { formData, isSubmitting, reset: resetForm } = useForm(initialFormData)

const {
  loading: fetchLoading,
  error: fetchError,
  execute: executeFetch
} = useAsync<MerchantService>(async () => {
  if (!serviceId.value) return null
  return await getServiceById(serviceId.value)
})

const {
  loading: submitLoading,
  error: submitError,
  execute: executeSubmit
} = useAsync<MerchantService>(async (data: Partial<MerchantService>) => {
  if (isEdit.value && serviceId.value) {
    return await updateService(serviceId.value, data)
  }
  return await addService(data as Omit<MerchantService, 'id'>)
})

const loading = computed(() => fetchLoading.value || submitLoading.value)

const fetchServiceData = async () => {
  if (!serviceId.value) return

  const result = await executeFetch()
  if (result) {
    resetForm({
      name: result.name || '',
      description: result.description || '',
      price: result.price || 0,
      duration: result.duration || 0,
      image: result.image || ''
    })
  } else if (fetchError.value) {
    ElMessage.error('获取服务信息失败')
    router.push('/merchant/services')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return
  } catch {
    return
  }

  const data: Partial<MerchantService> = {
    name: formData.name,
    description: formData.description,
    price: formData.price,
    duration: formData.duration,
    image: formData.image
  }

  const result = await executeSubmit(data)

  if (result) {
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    router.push('/merchant/services')
  } else if (submitError.value) {
    ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
  }
}

const handleCancel = () => {
  router.push('/merchant/services')
}

watch(serviceId, (newId) => {
  if (newId) {
    fetchServiceData()
  } else {
    resetForm()
  }
}, { immediate: true })

onMounted(() => {
  if (isEdit.value) {
    fetchServiceData()
  }
})
</script>

<template>
  <div class="service-edit">
    <div class="bg-white rounded-lg shadow-md p-6">
      <h1 class="text-2xl font-bold text-dark mb-6">{{ pageTitle }}</h1>

      <div v-if="fetchLoading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>

      <el-form
        v-else
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
        class="service-form"
      >
        <el-form-item label="服务名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入服务名称"
            maxlength="100"
            show-word-limit
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="服务描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入服务描述（选填）"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="formData.price"
            :precision="2"
            :min="0"
            :step="0.01"
            placeholder="请输入价格"
            class="price-input"
            :disabled="loading"
          />
          <span class="input-suffix">元</span>
        </el-form-item>

        <el-form-item label="时长" prop="duration">
          <el-input-number
            v-model="formData.duration"
            :min="1"
            :step="1"
            placeholder="请输入时长"
            class="duration-input"
            :disabled="loading"
          />
          <span class="input-suffix">分钟</span>
        </el-form-item>

        <el-form-item label="服务图片" prop="image">
          <el-input
            v-model="formData.image"
            placeholder="请输入图片URL"
            :disabled="loading"
          />
          <div v-if="formData.image" class="image-preview">
            <el-image
              :src="formData.image"
              fit="cover"
              class="preview-image"
              :preview-src-list="[formData.image]"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>图片加载失败</span>
                </div>
              </template>
            </el-image>
          </div>
        </el-form-item>

        <el-form-item class="form-actions">
          <el-button @click="handleCancel" :disabled="loading">
            取消
          </el-button>
          <el-button
            type="primary"
            :loading="submitLoading"
            :disabled="loading"
            @click="handleSubmit"
          >
            {{ isEdit ? '更新' : '保存' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.service-edit {
  font-family: Arial, sans-serif;
}

.service-form {
  max-width: 600px;
}

.input-suffix {
  margin-left: 8px;
  color: #909399;
}

.price-input,
.duration-input {
  width: 200px;
}

.image-preview {
  margin-top: 12px;
}

.preview-image {
  width: 200px;
  height: 150px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
}

.image-error .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.form-actions {
  margin-top: 32px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.loading-container {
  padding: 20px 0;
}

.text-2xl {
  font-size: 1.5rem;
  font-weight: 700;
}

.text-dark {
  color: #333333;
}

.mb-6 {
  margin-bottom: 1.5rem;
}

.bg-white {
  background-color: #ffffff;
}

.rounded-lg {
  border-radius: 0.5rem;
}

.shadow-md {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.p-6 {
  padding: 1.5rem;
}
</style>
