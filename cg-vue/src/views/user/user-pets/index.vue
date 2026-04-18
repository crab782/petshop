<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPets, addPet, updatePet, deletePet, type Pet } from '@/api/user'
import Card from '@/components/Card.vue'

const loading = ref(false)
const pets = ref<Pet[]>([])
const allPets = ref<Pet[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加宠物')
const isEdit = ref(false)
const currentPetId = ref<number | null>(null)

const searchName = ref('')
const filterType = ref('')

const petForm = ref({
  name: '',
  type: '',
  breed: '',
  age: undefined as number | undefined,
  gender: '',
  weight: undefined as number | undefined,
  size: '',
  color: '',
  personality: '',
  avatar: '',
  description: ''
})

const petTypes = ['狗', '猫', '鸟类', '小宠物']
const genderOptions = [
  { label: '公', value: '公' },
  { label: '母', value: '母' }
]

const filteredPets = computed(() => {
  return allPets.value.filter(pet => {
    const matchName = !searchName.value || pet.name.includes(searchName.value)
    const matchType = !filterType.value || pet.type === filterType.value
    return matchName && matchType
  })
})

const resetForm = () => {
  petForm.value = {
    name: '',
    type: '',
    breed: '',
    age: undefined,
    gender: '',
    weight: undefined,
    size: '',
    color: '',
    personality: '',
    avatar: '',
    description: ''
  }
}

const fetchPets = async () => {
  loading.value = true
  try {
    const res = await getUserPets()
    allPets.value = res.data || []
    pets.value = res.data || []
  } catch {
    ElMessage.error('获取宠物列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加宠物'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: Pet) => {
  isEdit.value = true
  dialogTitle.value = '编辑宠物'
  currentPetId.value = row.id
  petForm.value = {
    name: row.name,
    type: row.type,
    breed: row.breed || '',
    age: row.age,
    gender: (row as any).gender || '',
    weight: (row as any).weight,
    size: (row as any).size || '',
    color: (row as any).color || '',
    personality: (row as any).personality || '',
    avatar: row.avatar || '',
    description: (row as any).description || ''
  }
  dialogVisible.value = true
}

const handleDelete = async (row: Pet) => {
  try {
    await ElMessageBox.confirm('确定要删除该宠物吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePet(row.id)
    ElMessage.success('删除成功')
    fetchPets()
  } catch (err: any) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!petForm.value.name || !petForm.value.type) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    const data: any = {
      name: petForm.value.name,
      type: petForm.value.type,
      breed: petForm.value.breed,
      age: petForm.value.age
    }
    if (petForm.value.avatar) {
      data.avatar = petForm.value.avatar
    }
    if (petForm.value.gender) {
      data.gender = petForm.value.gender
    }
    if (petForm.value.weight) {
      data.weight = petForm.value.weight
    }
    if (petForm.value.size) {
      data.size = petForm.value.size
    }
    if (petForm.value.color) {
      data.color = petForm.value.color
    }
    if (petForm.value.personality) {
      data.personality = petForm.value.personality
    }
    if (petForm.value.description) {
      data.description = petForm.value.description
    }

    if (isEdit.value && currentPetId.value) {
      await updatePet(currentPetId.value, data)
      ElMessage.success('更新成功')
    } else {
      await addPet(data)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchPets()
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
  }
}

const getPetImage = (pet: Pet) => {
  if (pet.avatar) {
    return pet.avatar
  }
  // 默认宠物图片
  const defaultImages: Record<string, string> = {
    '狗': 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20dog%20portrait%2C%20professional%20pet%20photography&image_size=square',
    '猫': 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cat%20portrait%2C%20professional%20pet%20photography&image_size=square',
    '鸟类': 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=colorful%20bird%20portrait%2C%20professional%20pet%20photography&image_size=square',
    '小宠物': 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20small%20pet%20portrait%2C%20professional%20pet%20photography&image_size=square'
  }
  return defaultImages[pet.type] || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20pet%20portrait%2C%20professional%20pet%20photography&image_size=square'
}

onMounted(() => {
  fetchPets()
})
</script>

<template>
  <div class="user-pets">
    <div class="page-header">
      <h1 class="page-title">我的宠物</h1>
      <el-button type="primary" round @click="handleAdd">添加宠物</el-button>
    </div>

    <div class="filter-bar">
      <el-input
        v-model="searchName"
        placeholder="搜索宠物名称"
        style="width: 200px"
        clearable
      />
      <el-select
        v-model="filterType"
        placeholder="选择类型"
        style="width: 150px"
        clearable
      >
        <el-option v-for="t in petTypes" :key="t" :label="t" :value="t" />
      </el-select>
    </div>

    <div v-loading="loading" class="pets-content">
      <el-empty v-if="filteredPets.length === 0 && !loading" description="暂无宠物数据" />

      <template v-else>
        <div class="container">
          <div class="pets-grid">
            <div
              v-for="pet in filteredPets"
              :key="pet.id"
              class="pet-item card-hover"
            >
              <el-card shadow="hover" class="pet-card">
                <div class="pet-image">
                  <img :src="getPetImage(pet)" alt="宠物头像" class="pet-img">
                </div>

                <div class="pet-info">
                  <h3 class="pet-name">{{ pet.name }}</h3>
                  <p class="pet-details">{{ pet.type }} | {{ pet.breed || '未知品种' }} | {{ pet.age ? `${pet.age}岁` : '年龄未知' }}</p>
                  <p class="pet-details" v-if="(pet as any).weight || (pet as any).size || (pet as any).color">
                    {{ (pet as any).weight ? `${(pet as any).weight}kg` : '' }}
                    {{ (pet as any).size ? `| ${(pet as any).size}` : '' }}
                    {{ (pet as any).color ? `| ${(pet as any).color}` : '' }}
                  </p>
                  <p class="pet-personality" v-if="(pet as any).personality">
                    <strong>性格：</strong>{{ (pet as any).personality }}
                  </p>

                  <div class="pet-actions">
                    <el-button type="primary" size="small" @click="handleEdit(pet)">编辑</el-button>
                    <el-button type="danger" size="small" @click="handleDelete(pet)">删除</el-button>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
        </div>
      </template>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @closed="resetForm">
      <el-form :model="petForm" label-width="80px">
        <el-form-item label="宠物名称" required>
          <el-input v-model="petForm.name" placeholder="请输入宠物名称" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-select v-model="petForm.type" placeholder="请选择类型" style="width: 100%">
            <el-option v-for="t in petTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="品种">
          <el-input v-model="petForm.breed" placeholder="请输入品种" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="petForm.age" :min="0" :max="30" placeholder="请输入年龄" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="petForm.gender">
            <el-radio v-for="g in genderOptions" :key="g.value" :value="g.value">
              {{ g.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="petForm.weight" :min="0" :max="100" :step="0.1" placeholder="请输入体重" />
        </el-form-item>
        <el-form-item label="体型">
          <el-select v-model="petForm.size" placeholder="请选择体型" style="width: 100%">
            <el-option label="小型" value="小型" />
            <el-option label="中型" value="中型" />
            <el-option label="大型" value="大型" />
          </el-select>
        </el-form-item>
        <el-form-item label="毛色">
          <el-input v-model="petForm.color" placeholder="请输入毛色" />
        </el-form-item>
        <el-form-item label="性格">
          <el-input v-model="petForm.personality" placeholder="请输入性格" />
        </el-form-item>
        <el-form-item label="头像">
          <el-input v-model="petForm.avatar" placeholder="请输入头像URL" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="petForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-pets {
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 0 20px;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding: 0 20px;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #333333;
  margin: 0;
}

.pets-content {
  min-height: 400px;
  padding-bottom: 40px;
}

.pets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.pet-item {
  transition: all 0.3s ease;
}

.pet-item:hover {
  transform: translateY(-5px);
}

.pet-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.pet-image {
  height: 180px;
  overflow: hidden;
  background-color: #f5f5f5;
}

.pet-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.pet-card:hover .pet-img {
  transform: scale(1.05);
}

.pet-info {
  padding: 16px;
}

.pet-name {
  font-size: 18px;
  font-weight: bold;
  color: #333333;
  margin: 0 0 8px 0;
}

.pet-details {
  font-size: 14px;
  color: #666666;
  margin: 0 0 8px 0;
}

.pet-personality {
  font-size: 14px;
  color: #666666;
  margin: 0 0 16px 0;
}

.pet-actions {
  display: flex;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .pets-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .page-title {
    font-size: 24px;
  }
}
</style>
