<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useAsync, useForm } from '@/composables'
import { getMerchantInfo, updateMerchantInfo, type MerchantInfo } from '@/api/merchant'
import { Upload, Shop } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const logoUrl = ref('')
const logoInputMethod = ref<'url' | 'upload'>('url')

interface ShopFormData {
  name: string
  logo: string
  contact_person: string
  phone: string
  email: string
  address: string
}

const initialFormData: ShopFormData = {
  name: '',
  logo: '',
  contact_person: '',
  phone: '',
  email: '',
  address: ''
}

const rules: FormRules = {
  name: [
    { required: true, message: '请输入店铺名称', trigger: 'blur' },
    { min: 2, max: 100, message: '店铺名称长度为2-100个字符', trigger: 'blur' }
  ],
  contact_person: [
    { required: true, message: '请输入联系人', trigger: 'blur' },
    { max: 50, message: '联系人姓名最多50个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { max: 20, message: '联系电话最多20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
    { max: 100, message: '邮箱最多100个字符', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入店铺地址', trigger: 'blur' },
    { max: 255, message: '店铺地址最多255个字符', trigger: 'blur' }
  ]
}

const { formData, isSubmitting, reset: resetForm } = useForm<ShopFormData>(initialFormData)

const {
  loading: fetchLoading,
  error: fetchError,
  execute: executeFetch
} = useAsync<MerchantInfo>(async () => {
  try {
    const result = await getMerchantInfo()
    return result
  } catch (error) {
    ElMessage.error('获取店铺信息失败')
    return null
  }
})

const {
  loading: submitLoading,
  error: submitError,
  execute: executeSubmit
} = useAsync<MerchantInfo>(async (data: Partial<MerchantInfo>) => {
  try {
    const result = await updateMerchantInfo(data)
    return result
  } catch (error) {
    ElMessage.error('更新店铺信息失败')
    return null
  }
})

const loading = computed(() => fetchLoading.value || submitLoading.value)

const fetchShopInfo = async () => {
  const result = await executeFetch()
  if (result) {
    resetForm({
      name: result.name || '',
      logo: result.logo || '',
      contact_person: result.contact_person || '',
      phone: result.phone || '',
      email: result.email || '',
      address: result.address || ''
    })
    logoUrl.value = result.logo || ''
  } else if (fetchError.value) {
    ElMessage.error(fetchError.value.message || '获取店铺信息失败')
  }
}

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

const handleSave = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return
  } catch {
    return
  }

  const data: Partial<MerchantInfo> = {
    name: formData.name,
    logo: formData.logo,
    contact_person: formData.contact_person,
    phone: formData.phone,
    email: formData.email,
    address: formData.address
  }

  const result = await executeSubmit(data)

  if (result) {
    ElMessage.success('保存成功')
  } else if (submitError.value) {
    ElMessage.error(submitError.value.message || '保存失败')
  }
}

const handleBack = () => {
  router.push('/merchant/shop-settings')
}

onMounted(() => {
  fetchShopInfo()
})
</script>

<template>
  <div class="shop-edit">
    <h1 class="page-title">店铺信息</h1>

    <el-card class="form-card" v-loading="loading">
      <template #header>
        <span>编辑店铺信息</span>
      </template>

      <div v-if="fetchLoading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>

      <el-form
        v-else
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        class="shop-form"
      >
        <el-form-item label="店铺名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入店铺名称"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="店铺Logo">
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
                  :disabled="loading"
                />
              </div>

              <div v-else class="logo-upload">
                <el-upload
                  :show-file-list="false"
                  :on-change="handleLogoUploadChange"
                  accept="image/*"
                  :disabled="loading"
                >
                  <el-button type="primary" :icon="Upload" :disabled="loading">
                    选择图片
                  </el-button>
                </el-upload>
              </div>

              <div class="logo-preview">
                <el-avatar :size="80" :src="logoUrl" :icon="Shop" />
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="联系人" prop="contact_person">
          <el-input
            v-model="formData.contact_person"
            placeholder="请输入联系人姓名"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input
            v-model="formData.phone"
            placeholder="请输入联系电话"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="formData.email"
            placeholder="请输入邮箱"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="店铺地址" prop="address">
          <el-input
            v-model="formData.address"
            type="textarea"
            :rows="2"
            placeholder="请输入店铺地址"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="submitLoading"
            :disabled="loading"
            @click="handleSave"
          >
            保存
          </el-button>
          <el-button
            size="large"
            :disabled="loading"
            @click="handleBack"
          >
            返回设置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.shop-edit {
  max-width: 700px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 24px 0;
}

.form-card {
  border-radius: 12px;
}

.shop-form {
  max-width: 500px;
  padding: 20px 0;
}

.logo-section {
  width: 100%;
}

.logo-method {
  margin-bottom: 16px;
}

.logo-content {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.logo-url-input {
  flex: 1;
}

.logo-upload {
  margin-bottom: 16px;
}

.logo-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.logo-preview span {
  font-size: 12px;
  color: #909399;
}

.loading-container {
  padding: 20px 0;
}
</style>
