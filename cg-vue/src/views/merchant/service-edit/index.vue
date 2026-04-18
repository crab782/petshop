<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { updateService, addService } from '@/api/merchant'
import request from '@/api/request'
import type { FormInstance, FormRules } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const imagePreviewVisible = ref(false)
const imagePreviewUrl = ref('')

const serviceId = computed(() => route.query.id as string | undefined)
const isEdit = computed(() => !!serviceId.value)
const pageTitle = computed(() => isEdit.value ? '编辑服务' : '添加服务')

const formRef = ref<FormInstance>()
const form = reactive({
  name: '',
  description: '',
  price: 0,
  duration: 0,
  image: ''
})

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

const getServiceById = (id: number) => {
  return request.get<any>(`/api/merchant/services/${id}`)
}

const fetchServiceData = async () => {
  if (!serviceId.value) return

  loading.value = true
  try {
    const res = await getServiceById(Number(serviceId.value))
    const service = (res as any).data || res
    form.name = service.name || ''
    form.description = service.description || ''
    form.price = service.price || 0
    form.duration = service.duration || 0
    form.image = service.image || ''
  } catch {
    ElMessage.error('获取服务信息失败')
    router.push('/merchant/services')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const data = {
        name: form.name,
        description: form.description,
        price: form.price,
        duration: form.duration,
        image: form.image
      }

      if (isEdit.value && serviceId.value) {
        await updateService(Number(serviceId.value), data)
        ElMessage.success('更新成功')
      } else {
        await addService(data as any)
        ElMessage.success('添加成功')
      }

      router.push('/merchant/services')
    } catch {
      ElMessage.error(isEdit.value ? '更新失败' : '保存失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleImagePreview = () => {
  if (form.image) {
    imagePreviewUrl.value = form.image
    imagePreviewVisible.value = true
  }
}

const handleImageChange = (url: string) => {
  form.image = url
}

const handleCancel = () => {
  router.push('/merchant/services')
}

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
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="service-form"
      >
        <el-form-item label="服务名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入服务名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="服务描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入服务描述（选填）"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="form.price"
            :precision="2"
            :min="0"
            :step="0.01"
            placeholder="请输入价格"
            class="price-input"
          />
          <span class="input-suffix">元</span>
        </el-form-item>
        <el-form-item label="时长" prop="duration">
          <el-input-number
            v-model="form.duration"
            :min="1"
            :step="1"
            placeholder="请输入时长"
            class="duration-input"
          />
          <span class="input-suffix">分钟</span>
        </el-form-item>
        <el-form-item label="服务图片" prop="image">
          <div class="image-input-group">
            <el-input
              v-model="form.image"
              placeholder="请输入图片URL或上传图片"
              class="image-url-input"
            />
            <el-button @click="handleImagePreview" :disabled="!form.image" type="primary">
              预览
            </el-button>
          </div>
          <div v-if="form.image" class="image-preview">
            <el-image
              :src="form.image"
              fit="cover"
              class="preview-image"
              :preview-src-list="[form.image]"
            />
          </div>
        </el-form-item>
        <el-form-item class="form-actions">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ isEdit ? '更新' : '保存' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-dialog v-model="imagePreviewVisible" title="图片预览" width="600px">
      <el-image :src="imagePreviewUrl" fit="contain" style="width: 100%; height: 400px;" />
    </el-dialog>
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

.image-input-group {
  display: flex;
  gap: 8px;
}

.image-url-input {
  flex: 1;
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

.form-actions {
  margin-top: 32px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
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
