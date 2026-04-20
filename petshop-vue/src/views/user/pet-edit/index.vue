<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import { addPet, updatePet, getPetById } from '@/api/user'

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)

const petId = computed(() => route.params.id as string | undefined)
const isEdit = computed(() => !!petId.value)
const pageTitle = computed(() => isEdit.value ? '编辑宠物' : '添加宠物')

const form = reactive({
  name: '',
  type: '',
  breed: '',
  age: undefined as number | undefined,
  gender: '',
  avatar: '',
  description: '',
  weight: undefined as number | undefined,
  bodyType: '',
  furColor: '',
  personality: ''
})

const petTypes = [
  { label: '狗', value: '狗' },
  { label: '猫', value: '猫' },
  { label: '鸟类', value: '鸟类' },
  { label: '兔子', value: '兔子' },
  { label: '仓鼠', value: '仓鼠' },
  { label: '其他', value: '其他' }
]

const genderOptions = [
  { label: '公', value: '公' },
  { label: '母', value: '母' },
  { label: '未知', value: '未知' }
]

const bodyTypeOptions = [
  { label: '偏瘦', value: '偏瘦' },
  { label: '正常', value: '正常' },
  { label: '偏胖', value: '偏胖' },
  { label: '肥胖', value: '肥胖' }
]

const personalityOptions = [
  { label: '活泼好动', value: '活泼好动' },
  { label: '温顺安静', value: '温顺安静' },
  { label: '粘人', value: '粘人' },
  { label: '独立', value: '独立' },
  { label: '胆小', value: '胆小' },
  { label: '凶猛', value: '凶猛' }
]

const rules: FormRules = {
  name: [
    { required: true, message: '请输入宠物名称', trigger: 'blur' },
    { min: 1, max: 50, message: '宠物名称长度为1-50个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择宠物类型', trigger: 'change' }
  ],
  age: [
    { type: 'number', min: 0, max: 50, message: '年龄必须在0-50之间', trigger: 'blur' }
  ],
  weight: [
    { type: 'number', min: 0, max: 500, message: '体重必须在0-500kg之间', trigger: 'blur' }
  ]
}

const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  if (response && response.url) {
    form.avatar = response.url
    ElMessage.success('头像上传成功')
  }
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/')
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const fetchPetData = async () => {
  if (!petId.value) return

  loading.value = true
  try {
    const res = await getPetById(Number(petId.value))
    const pet = (res as any).data || res
    form.name = pet.name || ''
    form.type = pet.type || ''
    form.breed = pet.breed || ''
    form.age = pet.age
    form.gender = pet.gender || ''
    form.avatar = pet.avatar || ''
    form.description = pet.description || ''
    form.weight = pet.weight
    form.bodyType = pet.bodyType || ''
    form.furColor = pet.furColor || ''
    form.personality = pet.personality || ''
  } catch {
    ElMessage.error('获取宠物信息失败')
    router.push('/user/pets')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    submitting.value = true
    try {
      const data: any = {
        name: form.name,
        type: form.type
      }

      if (form.breed) data.breed = form.breed
      if (form.age !== undefined) data.age = form.age
      if (form.gender) data.gender = form.gender
      if (form.avatar) data.avatar = form.avatar
      if (form.description) data.description = form.description
      if (form.weight !== undefined) data.weight = form.weight
      if (form.bodyType) data.bodyType = form.bodyType
      if (form.furColor) data.furColor = form.furColor
      if (form.personality) data.personality = form.personality

      if (isEdit.value && petId.value) {
        await updatePet(Number(petId.value), data)
        ElMessage.success('更新成功')
      } else {
        await addPet(data)
        ElMessage.success('添加成功')
      }

      router.push('/user/pets')
    } catch {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleCancel = () => {
  router.push('/user/pets')
}

onMounted(() => {
  if (isEdit.value) {
    fetchPetData()
  }
})
</script>

<template>
  <div class="pet-edit">
    <el-card v-loading="loading">
      <template #header>
        <div class="header">
          <h2>{{ pageTitle }}</h2>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="pet-form"
      >
        <el-divider content-position="left">基本信息</el-divider>

        <el-form-item label="宠物头像" prop="avatar">
          <div class="avatar-upload">
            <el-upload
              class="avatar-uploader"
              action="/api/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <el-avatar v-if="form.avatar" :size="100" :src="form.avatar" />
              <div v-else class="avatar-placeholder">
                <el-icon :size="40"><Plus /></el-icon>
                <span>上传头像</span>
              </div>
            </el-upload>
            <div class="avatar-tips">
              <p>支持 jpg、png 格式</p>
              <p>大小不超过 2MB</p>
            </div>
          </div>
          <el-input 
            v-model="form.avatar" 
            placeholder="或直接输入头像URL" 
            style="margin-top: 10px;"
          />
        </el-form-item>

        <el-form-item label="宠物名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入宠物名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="宠物类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择宠物类型" style="width: 100%">
            <el-option
              v-for="item in petTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="品种" prop="breed">
          <el-input v-model="form.breed" placeholder="请输入品种" maxlength="50" />
        </el-form-item>

        <el-form-item label="年龄" prop="age">
          <el-input-number
            v-model="form.age"
            :min="0"
            :max="50"
            :precision="0"
            placeholder="请输入年龄"
            style="width: 100%"
          />
          <span class="unit">岁</span>
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio
              v-for="item in genderOptions"
              :key="item.value"
              :value="item.value"
            >
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-divider content-position="left">身体特征</el-divider>

        <el-form-item label="体重" prop="weight">
          <el-input-number
            v-model="form.weight"
            :min="0"
            :max="500"
            :precision="2"
            :step="0.1"
            placeholder="请输入体重"
            style="width: 100%"
          />
          <span class="unit">kg</span>
        </el-form-item>

        <el-form-item label="体型" prop="bodyType">
          <el-select v-model="form.bodyType" placeholder="请选择体型" style="width: 100%" clearable>
            <el-option
              v-for="item in bodyTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="毛色" prop="furColor">
          <el-input v-model="form.furColor" placeholder="请输入毛色，如：白色、黑色、棕色等" maxlength="50" />
        </el-form-item>

        <el-divider content-position="left">性格特点</el-divider>

        <el-form-item label="性格" prop="personality">
          <el-select v-model="form.personality" placeholder="请选择性格特点" style="width: 100%" clearable>
            <el-option
              v-for="item in personalityOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入宠物描述或备注信息"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ isEdit ? '保存' : '添加' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.pet-edit {
  max-width: 700px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.pet-form {
  max-width: 550px;
  margin: 0 auto;
}

.avatar-upload {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
  background-color: #f5f7fa;
}

.avatar-placeholder span {
  font-size: 12px;
  margin-top: 8px;
}

.avatar-tips {
  color: #909399;
  font-size: 12px;
  line-height: 1.8;
}

.avatar-tips p {
  margin: 0;
}

.unit {
  margin-left: 10px;
  color: #909399;
}

:deep(.el-divider__text) {
  font-weight: bold;
  color: #303133;
}

@media (max-width: 768px) {
  .avatar-upload {
    flex-direction: column;
    align-items: center;
  }
  
  .avatar-tips {
    text-align: center;
  }
}
</style>
