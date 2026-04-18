<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getMerchantInfo, updateMerchantInfo, type MerchantInfo } from '@/api/merchant'
import { Upload, Shop } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const logoUrl = ref('')

interface ShopInfo {
  id: number
  name: string
  logo?: string
  contact_person?: string
  phone?: string
  email?: string
  address?: string
}

const shopForm = reactive<ShopInfo>({
  id: 0,
  name: '',
  logo: '',
  contact_person: '',
  phone: '',
  email: '',
  address: ''
})

const logoInputMethod = ref<'url' | 'upload'>('url')

const shopRules: FormRules = {
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

const fetchShopInfo = async () => {
  try {
    loading.value = true
    const res = await getMerchantInfo()
    if (res) {
      shopForm.id = res.id
      shopForm.name = res.name || ''
      shopForm.logo = res.logo || ''
      shopForm.contact_person = (res as MerchantInfo).contact_person || ''
      shopForm.phone = res.phone || ''
      shopForm.email = (res as MerchantInfo).email || ''
      shopForm.address = res.address || ''
      logoUrl.value = res.logo || ''
    }
  } catch (error: unknown) {
    const err = error as Error
    ElMessage.error(err.message || '获取店铺信息失败')
  } finally {
    loading.value = false
  }
}

const handleLogoUrlInput = (value: string) => {
  logoUrl.value = value
  shopForm.logo = value
}

const handleLogoUploadChange = (uploadFile: { raw?: File; url?: string }) => {
  if (uploadFile.raw) {
    const url = URL.createObjectURL(uploadFile.raw)
    logoUrl.value = url
    shopForm.logo = url
  } else if (uploadFile.url) {
    logoUrl.value = uploadFile.url
    shopForm.logo = uploadFile.url
  }
  return false
}

const handleSave = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    try {
      loading.value = true
      await updateMerchantInfo({
        name: shopForm.name,
        logo: shopForm.logo,
        contact_person: shopForm.contact_person,
        phone: shopForm.phone,
        email: shopForm.email,
        address: shopForm.address
      })
      ElMessage.success('保存成功')
    } catch (error: unknown) {
      const err = error as Error
      ElMessage.error(err.message || '保存失败')
    } finally {
      loading.value = false
    }
  })
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

      <el-form
        ref="formRef"
        :model="shopForm"
        :rules="shopRules"
        label-width="100px"
        class="shop-form"
      >
        <el-form-item label="店铺名称" prop="name">
          <el-input v-model="shopForm.name" placeholder="请输入店铺名称" />
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
                  v-model="shopForm.logo"
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
                <el-avatar :size="80" :src="logoUrl" icon="Shop" />
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="联系人" prop="contact_person">
          <el-input v-model="shopForm.contact_person" placeholder="请输入联系人姓名" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="shopForm.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="shopForm.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="店铺地址" prop="address">
          <el-input
            v-model="shopForm.address"
            type="textarea"
            :rows="2"
            placeholder="请输入店铺地址"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleSave"
          >
            保存
          </el-button>
          <el-button
            size="large"
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
</style>
