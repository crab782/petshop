<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { addProduct, updateProduct, getProductById } from '@/api/merchant'

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)

const productId = computed(() => route.query.id as string | undefined)
const isEdit = computed(() => !!productId.value)
const pageTitle = computed(() => isEdit.value ? '编辑商品' : '添加商品')

const form = reactive({
  name: '',
  category: '',
  price: 0,
  stock: 0,
  lowStockThreshold: 10,
  image: '',
  description: ''
})

const categoryOptions = [
  { label: '狗粮', value: '狗粮' },
  { label: '猫粮', value: '猫粮' },
  { label: '宠物零食', value: '宠物零食' },
  { label: '宠物玩具', value: '宠物玩具' },
  { label: '宠物用品', value: '宠物用品' },
  { label: '宠物美容', value: '宠物美容' },
  { label: '其他', value: '其他' }
]

const rules: FormRules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 1, max: 100, message: '商品名称长度为1-100个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格必须大于等于0', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存必须大于等于0', trigger: 'blur' }
  ]
}

const fetchProductData = async () => {
  if (!productId.value) return

  loading.value = true
  try {
    const res = await getProductById(Number(productId.value))
    const product = (res as any).data || res
    form.name = product.name || ''
    form.category = product.category || ''
    form.price = product.price || 0
    form.stock = product.stock || 0
    form.lowStockThreshold = product.lowStockThreshold || 10
    form.image = product.image || ''
    form.description = product.description || ''
  } catch {
    ElMessage.error('获取商品信息失败')
    router.push('/merchant/products')
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
        category: form.category,
        price: form.price,
        stock: form.stock,
        lowStockThreshold: form.lowStockThreshold
      }

      if (form.image) data.image = form.image
      if (form.description) data.description = form.description

      if (isEdit.value && productId.value) {
        await updateProduct(Number(productId.value), data)
        ElMessage.success('更新成功')
      } else {
        await addProduct(data)
        ElMessage.success('添加成功')
      }

      router.push('/merchant/products')
    } catch {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleCancel = () => {
  router.push('/merchant/products')
}

onMounted(() => {
  if (isEdit.value) {
    fetchProductData()
  }
})
</script>

<template>
  <div class="product-edit">
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
        class="product-form"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入商品名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="商品分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择商品分类" style="width: 100%">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="价格（元）" prop="price">
          <el-input-number
            v-model="form.price"
            :min="0"
            :precision="2"
            placeholder="请输入价格"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="库存" prop="stock">
          <el-input-number
            v-model="form.stock"
            :min="0"
            :precision="0"
            placeholder="请输入库存"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="库存预警" prop="lowStockThreshold">
          <el-input-number
            v-model="form.lowStockThreshold"
            :min="0"
            :precision="0"
            placeholder="库存低于此值时提醒"
            style="width: 100%"
          />
          <div class="form-tip">当库存低于此值时，系统将提醒您及时补货</div>
        </el-form-item>

        <el-form-item label="商品图片" prop="image">
          <el-input v-model="form.image" placeholder="请输入图片URL" />
          <div v-if="form.image" class="image-preview">
            <el-image
              :src="form.image"
              fit="cover"
              style="width: 120px; height: 120px; border-radius: 8px;"
            />
          </div>
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入商品描述"
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
.product-edit {
  max-width: 600px;
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

.product-form {
  max-width: 500px;
  margin: 0 auto;
}

.image-preview {
  margin-top: 12px;
}

.form-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}
</style>
