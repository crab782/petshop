<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addProduct, updateProduct, getProductById, type Product } from '@/api/merchant'
import { useAsync } from '@/composables/useAsync'
import { useForm } from '@/composables/useForm'

const route = useRoute()
const router = useRouter()

// 从路由参数检测是否为编辑模式
const productId = computed(() => {
  const id = route.params.id || route.query.id
  return id ? Number(id) : null
})
const isEdit = computed(() => !!productId.value)
const pageTitle = computed(() => isEdit.value ? '编辑商品' : '添加商品')

// 表单初始数据
const initialFormData = {
  name: '',
  category: '',
  price: 0,
  stock: 0,
  lowStockThreshold: 10,
  image: '',
  description: ''
}

// 使用 useForm 管理表单
const {
  formData,
  errors,
  isSubmitting,
  validate,
  handleSubmit,
  reset
} = useForm(initialFormData)

// 使用 useAsync 获取商品详情
const {
  loading: fetchLoading,
  execute: fetchProduct
} = useAsync(async (id: number) => {
  const res = await getProductById(id)
  return res
})

// 分类选项
const categoryOptions = [
  { label: '狗粮', value: '狗粮' },
  { label: '猫粮', value: '猫粮' },
  { label: '宠物零食', value: '宠物零食' },
  { label: '宠物玩具', value: '宠物玩具' },
  { label: '宠物用品', value: '宠物用品' },
  { label: '宠物美容', value: '宠物美容' },
  { label: '其他', value: '其他' }
]

// 表单验证规则
const validationRules = {
  name: (value: string) => {
    if (!value || !value.trim()) return '请输入商品名称'
    if (value.length > 100) return '商品名称长度不能超过100个字符'
    return null
  },
  category: (value: string) => {
    if (!value) return '请选择商品分类'
    return null
  },
  price: (value: number) => {
    if (value === undefined || value === null) return '请输入价格'
    if (value < 0) return '价格必须大于等于0'
    return null
  },
  stock: (value: number) => {
    if (value === undefined || value === null) return '请输入库存'
    if (value < 0) return '库存必须大于等于0'
    return null
  }
}

// 加载商品数据
const loadProductData = async () => {
  if (!productId.value) return

  const product = await fetchProduct(productId.value)
  if (product) {
    reset({
      name: product.name || '',
      category: product.category || '',
      price: product.price || 0,
      stock: product.stock || 0,
      lowStockThreshold: product.lowStockThreshold || 10,
      image: product.image || '',
      description: product.description || ''
    })
  } else {
    ElMessage.error('获取商品信息失败')
    router.push('/merchant/products')
  }
}

// 提交表单
const onSubmit = async (data: typeof formData) => {
  if (!validate(validationRules)) {
    return
  }

  try {
    const submitData: Partial<Product> = {
      name: data.name,
      category: data.category,
      price: data.price,
      stock: data.stock,
      lowStockThreshold: data.lowStockThreshold
    }

    if (data.image) submitData.image = data.image
    if (data.description) submitData.description = data.description

    if (isEdit.value && productId.value) {
      await updateProduct(productId.value, submitData)
      ElMessage.success('更新成功')
    } else {
      await addProduct(submitData as Omit<Product, 'id'>)
      ElMessage.success('添加成功')
    }

    router.push('/merchant/products')
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
  }
}

// 取消操作
const handleCancel = () => {
  router.push('/merchant/products')
}

// 组件挂载时加载数据
onMounted(() => {
  if (isEdit.value) {
    loadProductData()
  }
})
</script>

<template>
  <div class="product-edit">
    <el-card v-loading="fetchLoading">
      <template #header>
        <div class="header">
          <h2>{{ pageTitle }}</h2>
        </div>
      </template>

      <el-form
        :model="formData"
        label-width="100px"
        class="product-form"
      >
        <el-form-item label="商品名称" required>
          <el-input
            v-model="formData.name"
            placeholder="请输入商品名称"
            maxlength="100"
            show-word-limit
            :class="{ 'is-error': errors.name }"
          />
          <div v-if="errors.name" class="error-message">{{ errors.name }}</div>
        </el-form-item>

        <el-form-item label="商品分类" required>
          <el-select v-model="formData.category" placeholder="请选择商品分类" style="width: 100%">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div v-if="errors.category" class="error-message">{{ errors.category }}</div>
        </el-form-item>

        <el-form-item label="价格（元）" required>
          <el-input-number
            v-model="formData.price"
            :min="0"
            :precision="2"
            placeholder="请输入价格"
            style="width: 100%"
          />
          <div v-if="errors.price" class="error-message">{{ errors.price }}</div>
        </el-form-item>

        <el-form-item label="库存" required>
          <el-input-number
            v-model="formData.stock"
            :min="0"
            :precision="0"
            placeholder="请输入库存"
            style="width: 100%"
          />
          <div v-if="errors.stock" class="error-message">{{ errors.stock }}</div>
        </el-form-item>

        <el-form-item label="库存预警">
          <el-input-number
            v-model="formData.lowStockThreshold"
            :min="0"
            :precision="0"
            placeholder="库存低于此值时提醒"
            style="width: 100%"
          />
          <div class="form-tip">当库存低于此值时，系统将提醒您及时补货</div>
        </el-form-item>

        <el-form-item label="商品图片">
          <el-input v-model="formData.image" placeholder="请输入图片URL" />
          <div v-if="formData.image" class="image-preview">
            <el-image
              :src="formData.image"
              fit="cover"
              style="width: 120px; height: 120px; border-radius: 8px;"
            />
          </div>
        </el-form-item>

        <el-form-item label="商品描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入商品描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="isSubmitting"
            @click="handleSubmit(onSubmit)"
          >
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

.error-message {
  margin-top: 4px;
  font-size: 12px;
  color: #f56c6c;
  line-height: 1;
}

.is-error :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #f56c6c inset;
}
</style>
