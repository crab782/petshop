<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Plus, Edit, Delete, Star } from '@element-plus/icons-vue'
import { getAddresses, addAddress, updateAddress, deleteAddress, setDefaultAddress, type Address } from '@/api/user'

const loading = ref(false)
const addressList = ref<Address[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加地址')
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const searchKeyword = ref('')

const addressForm = reactive({
  contactName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

const addressRules: FormRules = {
  contactName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请选择省份', trigger: 'change' }
  ],
  city: [
    { required: true, message: '请选择城市', trigger: 'change' }
  ],
  district: [
    { required: true, message: '请选择区县', trigger: 'change' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 100, message: '详细地址长度在 5 到 100 个字符', trigger: 'blur' }
  ]
}

const provinceOptions = [
  { value: '北京市', label: '北京市' },
  { value: '上海市', label: '上海市' },
  { value: '广东省', label: '广东省' },
  { value: '浙江省', label: '浙江省' },
  { value: '江苏省', label: '江苏省' },
  { value: '四川省', label: '四川省' },
  { value: '湖北省', label: '湖北省' },
  { value: '湖南省', label: '湖南省' },
  { value: '河南省', label: '河南省' },
  { value: '山东省', label: '山东省' }
]

const cityOptions: Record<string, { value: string; label: string }[]> = {
  '北京市': [{ value: '北京市', label: '北京市' }],
  '上海市': [{ value: '上海市', label: '上海市' }],
  '广东省': [
    { value: '广州市', label: '广州市' },
    { value: '深圳市', label: '深圳市' },
    { value: '佛山市', label: '佛山市' },
    { value: '东莞市', label: '东莞市' }
  ],
  '浙江省': [
    { value: '杭州市', label: '杭州市' },
    { value: '宁波市', label: '宁波市' },
    { value: '温州市', label: '温州市' },
    { value: '嘉兴市', label: '嘉兴市' }
  ],
  '江苏省': [
    { value: '南京市', label: '南京市' },
    { value: '苏州市', label: '苏州市' },
    { value: '无锡市', label: '无锡市' }
  ],
  '四川省': [
    { value: '成都市', label: '成都市' },
    { value: '绵阳市', label: '绵阳市' }
  ],
  '湖北省': [{ value: '武汉市', label: '武汉市' }],
  '湖南省': [{ value: '长沙市', label: '长沙市' }],
  '河南省': [{ value: '郑州市', label: '郑州市' }],
  '山东省': [
    { value: '济南市', label: '济南市' },
    { value: '青岛市', label: '青岛市' }
  ]
}

const districtOptions: Record<string, { value: string; label: string }[]> = {
  '广州市': [
    { value: '天河区', label: '天河区' },
    { value: '白云区', label: '白云区' },
    { value: '越秀区', label: '越秀区' },
    { value: '海珠区', label: '海珠区' },
    { value: '番禺区', label: '番禺区' }
  ],
  '深圳市': [
    { value: '福田区', label: '福田区' },
    { value: '南山区', label: '南山区' },
    { value: '宝安区', label: '宝安区' },
    { value: '龙岗区', label: '龙岗区' }
  ],
  '杭州市': [
    { value: '西湖区', label: '西湖区' },
    { value: '上城区', label: '上城区' },
    { value: '拱墅区', label: '拱墅区' },
    { value: '滨江区', label: '滨江区' }
  ],
  '成都市': [
    { value: '锦江区', label: '锦江区' },
    { value: '青羊区', label: '青羊区' },
    { value: '武侯区', label: '武侯区' },
    { value: '高新区', label: '高新区' }
  ],
  '武汉市': [
    { value: '江汉区', label: '江汉区' },
    { value: '武昌区', label: '武昌区' },
    { value: '洪山区', label: '洪山区' }
  ],
  '南京市': [
    { value: '玄武区', label: '玄武区' },
    { value: '秦淮区', label: '秦淮区' },
    { value: '鼓楼区', label: '鼓楼区' }
  ],
  '苏州市': [
    { value: '姑苏区', label: '姑苏区' },
    { value: '工业园区', label: '工业园区' },
    { value: '高新区', label: '高新区' }
  ],
  '长沙市': [
    { value: '芙蓉区', label: '芙蓉区' },
    { value: '天心区', label: '天心区' },
    { value: '岳麓区', label: '岳麓区' }
  ],
  '郑州市': [
    { value: '金水区', label: '金水区' },
    { value: '二七区', label: '二七区' },
    { value: '中原区', label: '中原区' }
  ],
  '济南市': [
    { value: '历下区', label: '历下区' },
    { value: '市中区', label: '市中区' },
    { value: '槐荫区', label: '槐荫区' }
  ],
  '青岛市': [
    { value: '市南区', label: '市南区' },
    { value: '市北区', label: '市北区' },
    { value: '崂山区', label: '崂山区' }
  ],
  '北京市': [{ value: '东城区', label: '东城区' }, { value: '西城区', label: '西城区' }, { value: '朝阳区', label: '朝阳区' }],
  '上海市': [{ value: '黄浦区', label: '黄浦区' }, { value: '徐汇区', label: '徐汇区' }, { value: '静安区', label: '静安区' }],
  '佛山市': [{ value: '禅城区', label: '禅城区' }, { value: '南海区', label: '南海区' }],
  '东莞市': [{ value: '莞城区', label: '莞城区' }, { value: '南城区', label: '南城区' }],
  '宁波市': [{ value: '海曙区', label: '海曙区' }, { value: '江北区', label: '江北区' }],
  '温州市': [{ value: '鹿城区', label: '鹿城区' }, { value: '龙湾区', label: '龙湾区' }],
  '嘉兴市': [{ value: '南湖区', label: '南湖区' }, { value: '秀洲区', label: '秀洲区' }],
  '无锡市': [{ value: '梁溪区', label: '梁溪区' }, { value: '新吴区', label: '新吴区' }],
  '绵阳市': [{ value: '涪城区', label: '涪城区' }, { value: '游仙区', label: '游仙区' }]
}

const selectedProvince = ref('')
const selectedCity = ref('')

const currentCityOptions = ref<{ value: string; label: string }[]>([])
const currentDistrictOptions = ref<{ value: string; label: string }[]>([])

const filteredAddressList = computed(() => {
  if (!searchKeyword.value) {
    return addressList.value
  }
  const keyword = searchKeyword.value.toLowerCase()
  return addressList.value.filter(item =>
    item.contactName.toLowerCase().includes(keyword) ||
    item.phone.includes(keyword) ||
    getFullAddress(item).toLowerCase().includes(keyword)
  )
})

const handleProvinceChange = (value: string) => {
  selectedProvince.value = value
  selectedCity.value = ''
  addressForm.city = ''
  addressForm.district = ''
  currentCityOptions.value = cityOptions[value] || []
  currentDistrictOptions.value = []
}

const handleCityChange = (value: string) => {
  selectedCity.value = value
  addressForm.district = ''
  currentDistrictOptions.value = districtOptions[value] || []
}

const fetchAddresses = async () => {
  loading.value = true
  try {
    const res = await getAddresses()
    addressList.value = res.data || []
  } catch {
    ElMessage.error('获取地址列表失败')
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  dialogTitle.value = '添加地址'
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (row: Address) => {
  dialogTitle.value = '编辑地址'
  editingId.value = row.id ?? null
  addressForm.contactName = row.contactName
  addressForm.phone = row.phone
  addressForm.province = row.province
  addressForm.city = row.city
  addressForm.district = row.district
  addressForm.detailAddress = row.detailAddress
  addressForm.isDefault = row.isDefault
  selectedProvince.value = row.province
  selectedCity.value = row.city
  currentCityOptions.value = cityOptions[row.province] || []
  currentDistrictOptions.value = districtOptions[row.city] || []
  dialogVisible.value = true
}

const resetForm = () => {
  addressForm.contactName = ''
  addressForm.phone = ''
  addressForm.province = ''
  addressForm.city = ''
  addressForm.district = ''
  addressForm.detailAddress = ''
  addressForm.isDefault = false
  selectedProvince.value = ''
  selectedCity.value = ''
  currentCityOptions.value = []
  currentDistrictOptions.value = []
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    try {
      loading.value = true
      if (editingId.value) {
        await updateAddress(editingId.value, addressForm)
        ElMessage.success('修改地址成功')
      } else {
        await addAddress(addressForm as Omit<Address, 'id'>)
        ElMessage.success('添加地址成功')
      }
      dialogVisible.value = false
      fetchAddresses()
    } catch {
      ElMessage.error(editingId.value ? '修改地址失败' : '添加地址失败')
    } finally {
      loading.value = false
    }
  })
}

const handleDelete = (row: Address) => {
  ElMessageBox.confirm('确定要删除该地址吗？删除后无法恢复', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAddress(row.id!)
      ElMessage.success('删除成功')
      fetchAddresses()
    } catch {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSetDefault = async (row: Address) => {
  if (row.isDefault) {
    ElMessage.info('该地址已是默认地址')
    return
  }
  try {
    await setDefaultAddress(row.id!)
    ElMessage.success('已设为默认地址')
    fetchAddresses()
  } catch {
    ElMessage.error('设置失败')
  }
}

const getFullAddress = (row: Address) => {
  return `${row.province}${row.city}${row.district}${row.detailAddress}`
}

const handleSearch = () => {
}

onMounted(() => {
  fetchAddresses()
})
</script>

<template>
  <div class="addresses-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">收货地址管理</span>
          <el-button type="primary" :icon="Plus" @click="openAddDialog">
            添加新地址
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索收货人、电话或地址"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <span class="address-count">
          共 {{ filteredAddressList.length }} 个地址
        </span>
      </div>

      <div v-loading="loading" class="address-list">
        <el-empty v-if="filteredAddressList.length === 0 && !loading" description="暂无收货地址">
          <el-button type="primary" @click="openAddDialog">添加地址</el-button>
        </el-empty>

        <div v-else class="address-grid">
          <el-card
            v-for="item in filteredAddressList"
            :key="item.id"
            class="address-card"
            :class="{ 'is-default': item.isDefault }"
            shadow="hover"
          >
            <div class="address-content">
              <div class="address-info">
                <div class="contact-row">
                  <span class="contact-name">{{ item.contactName }}</span>
                  <span class="phone">{{ item.phone }}</span>
                  <el-tag v-if="item.isDefault" type="success" size="small">
                    <el-icon><Star /></el-icon>
                    默认
                  </el-tag>
                </div>
                <div class="detail-address">
                  <el-icon><i-ep-location /></el-icon>
                  {{ getFullAddress(item) }}
                </div>
              </div>
              <div class="address-actions">
                <el-button
                  type="primary"
                  text
                  size="small"
                  :icon="Edit"
                  @click="openEditDialog(item)"
                >
                  编辑
                </el-button>
                <el-button
                  type="danger"
                  text
                  size="small"
                  :icon="Delete"
                  @click="handleDelete(item)"
                >
                  删除
                </el-button>
                <el-button
                  v-if="!item.isDefault"
                  type="warning"
                  text
                  size="small"
                  :icon="Star"
                  @click="handleSetDefault(item)"
                >
                  设为默认
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="550px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="100px"
      >
        <el-form-item label="收货人" prop="contactName">
          <el-input
            v-model="addressForm.contactName"
            placeholder="请输入收货人姓名"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input
            v-model="addressForm.phone"
            placeholder="请输入联系电话"
            maxlength="11"
          />
        </el-form-item>

        <el-form-item label="省份" prop="province">
          <el-select
            v-model="addressForm.province"
            placeholder="请选择省份"
            style="width: 100%"
            @change="handleProvinceChange"
          >
            <el-option
              v-for="item in provinceOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="城市" prop="city">
          <el-select
            v-model="addressForm.city"
            placeholder="请选择城市"
            style="width: 100%"
            :disabled="!addressForm.province"
            @change="handleCityChange"
          >
            <el-option
              v-for="item in currentCityOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="区县" prop="district">
          <el-select
            v-model="addressForm.district"
            placeholder="请选择区县"
            style="width: 100%"
            :disabled="!addressForm.city"
          >
            <el-option
              v-for="item in currentDistrictOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="详细地址" prop="detailAddress">
          <el-input
            v-model="addressForm.detailAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址（如街道、楼栋、门牌号等）"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">
            设为默认地址
          </el-checkbox>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.addresses-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.address-count {
  color: #909399;
  font-size: 14px;
}

.address-list {
  min-height: 200px;
}

.address-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.address-card {
  position: relative;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.address-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.address-card.is-default {
  border-color: #67c23a;
  background: linear-gradient(to right, #f6ffed, white);
}

.address-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.address-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.contact-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.contact-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.phone {
  font-size: 14px;
  color: #606266;
}

.detail-address {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  display: flex;
  align-items: flex-start;
  gap: 4px;
}

.detail-address .el-icon {
  margin-top: 2px;
  color: #909399;
}

.address-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-bar .el-input {
    width: 100% !important;
  }

  .address-count {
    text-align: center;
  }

  .address-grid {
    grid-template-columns: 1fr;
  }
}
</style>
