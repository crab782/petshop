<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getUserInfo as getUserInfoApi, updateUserInfo } from '@/api/auth'
import { Upload } from '@element-plus/icons-vue'

interface UserProfile {
  id: number
  username: string
  nickname: string
  gender: number
  phone: string
  email: string
  avatar: string
  birthday: string
}

const router = useRouter()
const profileFormRef = ref<FormInstance>()
const loading = ref(false)
const imageUrl = ref('')

const profileForm = reactive<UserProfile>({
  id: 0,
  username: '',
  nickname: '',
  gender: 0,
  phone: '',
  email: '',
  avatar: '',
  birthday: ''
})

const profileRules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度为2-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const genderOptions = [
  { value: 0, label: '保密' },
  { value: 1, label: '男' },
  { value: 2, label: '女' }
]

const fetchUserInfo = async () => {
  try {
    loading.value = true
    const res = await getUserInfoApi()
    if (res) {
      profileForm.id = res.id
      profileForm.username = res.username || ''
      profileForm.nickname = (res as any).nickname || res.username || ''
      profileForm.gender = (res as any).gender || 0
      profileForm.phone = res.phone || ''
      profileForm.email = res.email || ''
      profileForm.avatar = res.avatar || ''
      profileForm.birthday = (res as any).birthday || ''
      imageUrl.value = res.avatar || ''
    }
  } catch (error: unknown) {
    const err = error as Error
    ElMessage.error(err.message || '获取用户信息失败')
  } finally {
    loading.value = false
  }
}

const handleAvatarChange = (uploadFile: { raw: File }) => {
  const url = URL.createObjectURL(uploadFile.raw)
  imageUrl.value = url
  profileForm.avatar = url
  return false
}

const handleSave = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    try {
      loading.value = true
      await updateUserInfo({
        username: profileForm.nickname,
        email: profileForm.email,
        phone: profileForm.phone,
        avatar: profileForm.avatar
      })
      const updateData: Record<string, unknown> = {
        nickname: profileForm.nickname,
        gender: profileForm.gender,
        phone: profileForm.phone,
        email: profileForm.email,
        avatar: profileForm.avatar
      }
      if (profileForm.birthday) {
        updateData.birthday = profileForm.birthday
      }
      await fetch('/api/auth/userinfo', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updateData)
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

const handleCancel = () => {
  router.push('/user/profile')
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<template>
  <div class="profile-edit">
    <h1 class="page-title">编辑资料</h1>

    <el-card class="avatar-card">
      <template #header>
        <span>头像上传</span>
      </template>

      <div class="avatar-content">
        <el-avatar :size="120" :src="imageUrl" icon="UserFilled" />

        <el-upload
          class="avatar-upload"
          :show-file-list="false"
          :on-change="handleAvatarChange"
          accept="image/*"
        >
          <el-button type="primary" :icon="Upload">
            选择图片
          </el-button>
        </el-upload>
        <p class="avatar-tip">支持 jpg、png 格式，建议尺寸 200x200 像素</p>
      </div>
    </el-card>

    <el-card class="form-card">
      <template #header>
        <span>个人资料</span>
      </template>

      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="80px"
        class="profile-form"
      >
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="profileForm.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="profileForm.gender">
            <el-radio
              v-for="item in genderOptions"
              :key="item.value"
              :value="item.value"
            >
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="生日" prop="birthday">
          <el-date-picker
            v-model="profileForm.birthday"
            type="date"
            placeholder="请选择生日"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱" maxlength="100" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSave">
            保存
          </el-button>
          <el-button @click="handleCancel">
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.profile-edit {
  max-width: 600px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 24px 0;
}

.avatar-card {
  margin-bottom: 24px;
}

.avatar-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px 0;
}

.avatar-upload {
  display: flex;
  justify-content: center;
}

.avatar-tip {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

.profile-form {
  max-width: 400px;
}
</style>
