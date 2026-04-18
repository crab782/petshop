<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { getAnnouncementById, addAnnouncement, updateAnnouncement } from '@/api/announcement'
import { ArrowLeft, Send } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const isEdit = computed(() => !!route.query.id)
const pageTitle = computed(() => isEdit.value ? '编辑公告' : '发布公告')

const formData = ref({
  title: '',
  content: '',
  publishTime: ''
})

const rules: FormRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 5, max: 2000, message: '内容长度在 5 到 2000 个字符', trigger: 'blur' }
  ],
  publishTime: [
    { required: true, message: '请选择发布时间', trigger: 'change' }
  ]
}

const loadAnnouncement = async (id: number) => {
  loading.value = true
  try {
    const data = await getAnnouncementById(id)
    formData.value = {
      title: data.title,
      content: data.content,
      publishTime: data.publishTime
    }
  } catch (error) {
    ElMessage.error('加载公告详情失败')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return

  await formEl.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      if (isEdit.value && route.query.id) {
        await updateAnnouncement(Number(route.query.id), {
          title: formData.value.title,
          content: formData.value.content
        })
        ElMessage.success('更新成功')
      } else {
        await addAnnouncement({
          title: formData.value.title,
          content: formData.value.content
        })
        ElMessage.success('发布成功')
      }
      router.push('/admin/announcements')
    } catch (error) {
      ElMessage.error('操作失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  if (isEdit.value && route.query.id) {
    loadAnnouncement(Number(route.query.id))
  } else {
    formData.value.publishTime = new Date().toISOString().slice(0, 16).replace('T', ' ')
  }
})
</script>

<template>
  <div class="announcement-edit">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/admin/announcements' }">公告管理</el-breadcrumb-item>
      <el-breadcrumb-item>{{ pageTitle }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="hover" class="form-card">
      <template #header>
        <div class="card-header">
          <el-button @click="handleBack" text>
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <span>{{ pageTitle }}</span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        v-loading="loading"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input
            v-model="formData.title"
            placeholder="请输入公告标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            placeholder="请输入公告内容"
            :rows="10"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker
            v-model="formData.publishTime"
            type="datetime"
            placeholder="选择发布时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled="isEdit"
          />
        </el-form-item>

        <el-form-item>
          <el-button @click="handleBack">取消</el-button>
          <el-button type="primary" @click="handleSubmit(formRef)" :loading="submitting">
            <el-icon v-if="!submitting"><Send /></el-icon>
            {{ isEdit ? '保存' : '发布' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.announcement-edit {
  padding: 0;
}

.form-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.card-header span {
  font-weight: 500;
  font-size: 16px;
}
</style>
