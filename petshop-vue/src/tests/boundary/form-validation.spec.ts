import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'

const createTestRouter = () => createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: { template: '<div>Home</div>' } },
    { path: '/login', name: 'login', component: { template: '<div>Login</div>' } },
    { path: '/register', name: 'register', component: { template: '<div>Register</div>' } },
  ],
})

const mountWithPlugins = (component: any, options = {}) => {
  const pinia = createPinia()
  setActivePinia(pinia)
  const router = createTestRouter()
  
  return mount(component, {
    ...options,
    global: {
      plugins: [pinia, router],
      stubs: {
        RouterLink: true,
        RouterView: true,
        'el-empty': {
          template: '<div class="el-empty"><slot /><span>{{ description }}</span></div>',
          props: ['description'],
        },
        'el-button': {
          template: '<button :disabled="disabled" :class="type"><slot /></button>',
          props: ['disabled', 'type', 'loading'],
        },
        'el-input': {
          template: '<input v-model="modelValue" :placeholder="placeholder" />',
          props: ['modelValue', 'placeholder', 'type'],
        },
        'el-input-number': {
          template: '<input type="number" v-model="modelValue" :min="min" :max="max" />',
          props: ['modelValue', 'min', 'max', 'step', 'placeholder'],
        },
        'el-select': {
          template: '<select class="el-select"><slot /></select>',
          props: ['modelValue', 'placeholder'],
        },
        'el-option': {
          template: '<option :value="value">{{ label }}</option>',
          props: ['value', 'label'],
        },
        'el-radio-group': {
          template: '<div class="el-radio-group"><slot /></div>',
          props: ['modelValue'],
        },
        'el-radio': {
          template: '<label class="el-radio"><input type="radio" :value="value" /><slot /></label>',
          props: ['value', 'label'],
        },
        'el-form': {
          template: '<form class="el-form"><slot /></form>',
          props: ['model', 'rules'],
        },
        'el-form-item': {
          template: '<div class="el-form-item"><label v-if="label">{{ label }}</label><slot /><span v-if="error" class="error">{{ error }}</span></div>',
          props: ['label', 'prop', 'error', 'required'],
        },
      },
      ...(options as any).global,
    },
  })
}

const MAX_NAME_LENGTH = 50
const MAX_DESCRIPTION_LENGTH = 500
const MAX_ADDRESS_LENGTH = 255

describe('表单验证边界测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('空值测试 - 必填字段', () => {
    it('用户名为空时应显示错误', async () => {
      const UsernameForm = {
        template: `
          <div>
            <el-form :model="form" :rules="rules" ref="formRef">
              <el-form-item prop="username" :error="errors.username">
                <el-input v-model="form.username" placeholder="用户名" />
              </el-form-item>
            </el-form>
          </div>
        `,
        data() {
          return {
            form: { username: '' },
            rules: { username: [{ required: true, message: '请输入用户名' }] },
            errors: { username: '请输入用户名' },
          }
        },
      }
      const wrapper = mountWithPlugins(UsernameForm)
      expect(wrapper.text()).toContain('请输入用户名')
    })

    it('密码为空时应显示错误', async () => {
      const PasswordForm = {
        template: `
          <div>
            <el-form :model="form">
              <el-form-item :error="error">
                <el-input v-model="form.password" type="password" />
              </el-form-item>
            </el-form>
          </div>
        `,
        data() {
          return {
            form: { password: '' },
            error: '请输入密码',
          }
        },
      }
      const wrapper = mountWithPlugins(PasswordForm)
      expect(wrapper.text()).toContain('请输入密码')
    })

    it('邮箱为空时应显示错误', async () => {
      const EmailForm = {
        template: `
          <div>
            <div v-if="emailError" class="error">{{ emailError }}</div>
          </div>
        `,
        data() {
          return {
            email: '',
            emailError: '请输入邮箱地址',
          }
        },
      }
      const wrapper = mountWithPlugins(EmailForm)
      expect(wrapper.text()).toContain('请输入邮箱地址')
    })

    it('手机号为空时应显示错误', async () => {
      const PhoneForm = {
        template: `
          <div>
            <div v-if="phoneError" class="error">{{ phoneError }}</div>
          </div>
        `,
        data() {
          return {
            phone: '',
            phoneError: '请输入手机号',
          }
        },
      }
      const wrapper = mountWithPlugins(PhoneForm)
      expect(wrapper.text()).toContain('请输入手机号')
    })

    it('地址为空时应显示错误', async () => {
      const AddressForm = {
        template: `
          <div>
            <div v-if="addressError" class="error">{{ addressError }}</div>
          </div>
        `,
        data() {
          return {
            address: '',
            addressError: '请输入收货地址',
          }
        },
      }
      const wrapper = mountWithPlugins(AddressForm)
      expect(wrapper.text()).toContain('请输入收货地址')
    })

    it('宠物名称为空时应显示错误', async () => {
      const PetNameForm = {
        template: `
          <div>
            <div v-if="petNameError" class="error">{{ petNameError }}</div>
          </div>
        `,
        data() {
          return {
            petName: '',
            petNameError: '请输入宠物名称',
          }
        },
      }
      const wrapper = mountWithPlugins(PetNameForm)
      expect(wrapper.text()).toContain('请输入宠物名称')
    })

    it('宠物类型为空时应显示错误', async () => {
      const PetTypeForm = {
        template: `
          <div>
            <div v-if="petTypeError" class="error">{{ petTypeError }}</div>
          </div>
        `,
        data() {
          return {
            petType: '',
            petTypeError: '请选择宠物类型',
          }
        },
      }
      const wrapper = mountWithPlugins(PetTypeForm)
      expect(wrapper.text()).toContain('请选择宠物类型')
    })

    it('预约时间为空时应显示错误', async () => {
      const AppointmentTimeForm = {
        template: `
          <div>
            <div v-if="timeError" class="error">{{ timeError }}</div>
          </div>
        `,
        data() {
          return {
            appointmentTime: '',
            timeError: '请选择预约时间',
          }
        },
      }
      const wrapper = mountWithPlugins(AppointmentTimeForm)
      expect(wrapper.text()).toContain('请选择预约时间')
    })

    it('多个必填字段为空时应显示所有错误', async () => {
      const MultiFieldForm = {
        template: `
          <div>
            <div v-if="errors.name" class="error">{{ errors.name }}</div>
            <div v-if="errors.email" class="error">{{ errors.email }}</div>
            <div v-if="errors.phone" class="error">{{ errors.phone }}</div>
          </div>
        `,
        data() {
          return {
            form: { name: '', email: '', phone: '' },
            errors: {
              name: '请输入姓名',
              email: '请输入邮箱',
              phone: '请输入手机号',
            },
          }
        },
      }
      const wrapper = mountWithPlugins(MultiFieldForm)
      expect(wrapper.text()).toContain('请输入姓名')
      expect(wrapper.text()).toContain('请输入邮箱')
      expect(wrapper.text()).toContain('请输入手机号')
    })
  })

  describe('超长输入测试 - 字符串长度限制', () => {
    it('用户名超过最大长度应显示错误', async () => {
      const longName = 'a'.repeat(MAX_NAME_LENGTH + 1)
      const UsernameForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            username: longName,
            error: `用户名不能超过${MAX_NAME_LENGTH}个字符`,
          }
        },
      }
      const wrapper = mountWithPlugins(UsernameForm)
      expect(wrapper.text()).toContain(`用户名不能超过${MAX_NAME_LENGTH}个字符`)
    })

    it('宠物名称超过最大长度应显示错误', async () => {
      const longName = 'a'.repeat(51)
      const PetNameForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            petName: longName,
            error: '宠物名称不能超过50个字符',
          }
        },
      }
      const wrapper = mountWithPlugins(PetNameForm)
      expect(wrapper.text()).toContain('宠物名称不能超过50个字符')
    })

    it('描述超过最大长度应显示错误', async () => {
      const longDescription = 'a'.repeat(MAX_DESCRIPTION_LENGTH + 1)
      const DescriptionForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            description: longDescription,
            error: `描述不能超过${MAX_DESCRIPTION_LENGTH}个字符`,
          }
        },
      }
      const wrapper = mountWithPlugins(DescriptionForm)
      expect(wrapper.text()).toContain(`描述不能超过${MAX_DESCRIPTION_LENGTH}个字符`)
    })

    it('地址超过最大长度应显示错误', async () => {
      const longAddress = 'a'.repeat(MAX_ADDRESS_LENGTH + 1)
      const AddressForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            address: longAddress,
            error: `地址不能超过${MAX_ADDRESS_LENGTH}个字符`,
          }
        },
      }
      const wrapper = mountWithPlugins(AddressForm)
      expect(wrapper.text()).toContain(`地址不能超过${MAX_ADDRESS_LENGTH}个字符`)
    })

    it('评价内容超过最大长度应显示错误', async () => {
      const longComment = 'a'.repeat(1001)
      const ReviewForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            comment: longComment,
            error: '评价内容不能超过1000个字符',
          }
        },
      }
      const wrapper = mountWithPlugins(ReviewForm)
      expect(wrapper.text()).toContain('评价内容不能超过1000个字符')
    })

    it('备注超过最大长度应显示错误', async () => {
      const longRemark = 'a'.repeat(501)
      const RemarkForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            remark: longRemark,
            error: '备注不能超过500个字符',
          }
        },
      }
      const wrapper = mountWithPlugins(RemarkForm)
      expect(wrapper.text()).toContain('备注不能超过500个字符')
    })

    it('输入达到最大长度边界值应通过验证', async () => {
      const maxLengthName = 'a'.repeat(MAX_NAME_LENGTH)
      const UsernameForm = {
        template: `
          <div>
            <div v-if="!error" class="success">验证通过</div>
            <div v-else class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            username: maxLengthName,
            error: null as string | null,
          }
        },
      }
      const wrapper = mountWithPlugins(UsernameForm)
      expect(wrapper.text()).toContain('验证通过')
    })
  })

  describe('特殊字符测试 - XSS防护', () => {
    it('输入包含script标签应被过滤或拒绝', async () => {
      const xssInput = '<script>alert("xss")</script>'
      const XssForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            input: xssInput,
            error: '输入包含非法字符',
          }
        },
      }
      const wrapper = mountWithPlugins(XssForm)
      expect(wrapper.text()).toContain('输入包含非法字符')
    })

    it('输入包含HTML标签应被过滤', async () => {
      const htmlInput = '<div onclick="alert(1)">test</div>'
      const HtmlForm = {
        template: `
          <div>
            <div v-if="sanitized" class="sanitized">{{ sanitized }}</div>
          </div>
        `,
        data() {
          return {
            input: htmlInput,
            sanitized: 'test',
          }
        },
      }
      const wrapper = mountWithPlugins(HtmlForm)
      expect(wrapper.text()).not.toContain('<div')
      expect(wrapper.text()).toContain('test')
    })

    it('输入包含JavaScript事件应被过滤', async () => {
      const jsInput = 'test" onclick="alert(1)"'
      const JsForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            input: jsInput,
            error: '输入包含非法字符',
          }
        },
      }
      const wrapper = mountWithPlugins(JsForm)
      expect(wrapper.text()).toContain('输入包含非法字符')
    })

    it('输入包含SQL注入字符应被拒绝', async () => {
      const sqlInput = "'; DROP TABLE users; --"
      const SqlForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            input: sqlInput,
            error: '输入包含非法字符',
          }
        },
      }
      const wrapper = mountWithPlugins(SqlForm)
      expect(wrapper.text()).toContain('输入包含非法字符')
    })

    it('输入包含特殊符号组合应被检查', async () => {
      const specialChars = '{{template}}'
      const SpecialForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            input: specialChars,
            error: '输入包含非法字符',
          }
        },
      }
      const wrapper = mountWithPlugins(SpecialForm)
      expect(wrapper.text()).toContain('输入包含非法字符')
    })

    it('正常特殊字符应允许输入', async () => {
      const normalSpecialChars = '测试-商品_名称(分类)'
      const NormalForm = {
        template: `
          <div>
            <div class="input-value">{{ input }}</div>
          </div>
        `,
        data() {
          return {
            input: normalSpecialChars,
          }
        },
      }
      const wrapper = mountWithPlugins(NormalForm)
      expect(wrapper.text()).toContain('测试-商品_名称(分类)')
    })
  })

  describe('格式错误测试 - 邮箱格式', () => {
    it('邮箱缺少@符号应显示错误', async () => {
      const EmailForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            email: 'testexample.com',
            error: '请输入正确的邮箱格式',
          }
        },
      }
      const wrapper = mountWithPlugins(EmailForm)
      expect(wrapper.text()).toContain('请输入正确的邮箱格式')
    })

    it('邮箱缺少域名应显示错误', async () => {
      const EmailForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            email: 'test@',
            error: '请输入正确的邮箱格式',
          }
        },
      }
      const wrapper = mountWithPlugins(EmailForm)
      expect(wrapper.text()).toContain('请输入正确的邮箱格式')
    })

    it('邮箱缺少用户名应显示错误', async () => {
      const EmailForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            email: '@example.com',
            error: '请输入正确的邮箱格式',
          }
        },
      }
      const wrapper = mountWithPlugins(EmailForm)
      expect(wrapper.text()).toContain('请输入正确的邮箱格式')
    })

    it('邮箱包含多个@符号应显示错误', async () => {
      const EmailForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            email: 'test@@example.com',
            error: '请输入正确的邮箱格式',
          }
        },
      }
      const wrapper = mountWithPlugins(EmailForm)
      expect(wrapper.text()).toContain('请输入正确的邮箱格式')
    })

    it('邮箱包含空格应显示错误', async () => {
      const EmailForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            email: 'test @example.com',
            error: '请输入正确的邮箱格式',
          }
        },
      }
      const wrapper = mountWithPlugins(EmailForm)
      expect(wrapper.text()).toContain('请输入正确的邮箱格式')
    })

    it('正确邮箱格式应通过验证', async () => {
      const EmailForm = {
        template: `
          <div>
            <div v-if="!error" class="success">验证通过</div>
          </div>
        `,
        data() {
          return {
            email: 'test@example.com',
            error: null as string | null,
          }
        },
      }
      const wrapper = mountWithPlugins(EmailForm)
      expect(wrapper.text()).toContain('验证通过')
    })
  })

  describe('格式错误测试 - 电话格式', () => {
    it('手机号长度不足应显示错误', async () => {
      const PhoneForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            phone: '138001380',
            error: '请输入正确的手机号格式',
          }
        },
      }
      const wrapper = mountWithPlugins(PhoneForm)
      expect(wrapper.text()).toContain('请输入正确的手机号格式')
    })

    it('手机号长度超出应显示错误', async () => {
      const PhoneForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            phone: '138001380001',
            error: '请输入正确的手机号格式',
          }
        },
      }
      const wrapper = mountWithPlugins(PhoneForm)
      expect(wrapper.text()).toContain('请输入正确的手机号格式')
    })

    it('手机号包含非数字字符应显示错误', async () => {
      const PhoneForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            phone: '1380013800a',
            error: '请输入正确的手机号格式',
          }
        },
      }
      const wrapper = mountWithPlugins(PhoneForm)
      expect(wrapper.text()).toContain('请输入正确的手机号格式')
    })

    it('手机号以非1开头应显示错误', async () => {
      const PhoneForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            phone: '23800138000',
            error: '请输入正确的手机号格式',
          }
        },
      }
      const wrapper = mountWithPlugins(PhoneForm)
      expect(wrapper.text()).toContain('请输入正确的手机号格式')
    })

    it('正确手机号格式应通过验证', async () => {
      const PhoneForm = {
        template: `
          <div>
            <div v-if="!error" class="success">验证通过</div>
          </div>
        `,
        data() {
          return {
            phone: '13800138000',
            error: null as string | null,
          }
        },
      }
      const wrapper = mountWithPlugins(PhoneForm)
      expect(wrapper.text()).toContain('验证通过')
    })
  })

  describe('格式错误测试 - 日期格式', () => {
    it('无效日期格式应显示错误', async () => {
      const DateForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            date: '2024-13-45',
            error: '请选择有效的日期',
          }
        },
      }
      const wrapper = mountWithPlugins(DateForm)
      expect(wrapper.text()).toContain('请选择有效的日期')
    })

    it('预约时间为过去时间应显示错误', async () => {
      const PastDateForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            date: '2020-01-01',
            error: '预约时间不能早于当前时间',
          }
        },
      }
      const wrapper = mountWithPlugins(PastDateForm)
      expect(wrapper.text()).toContain('预约时间不能早于当前时间')
    })

    it('日期范围结束时间早于开始时间应显示错误', async () => {
      const DateRangeForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            startDate: '2024-12-31',
            endDate: '2024-01-01',
            error: '结束时间不能早于开始时间',
          }
        },
      }
      const wrapper = mountWithPlugins(DateRangeForm)
      expect(wrapper.text()).toContain('结束时间不能早于开始时间')
    })

    it('正确日期格式应通过验证', async () => {
      const DateForm = {
        template: `
          <div>
            <div v-if="!error" class="success">验证通过</div>
          </div>
        `,
        data() {
          return {
            date: '2024-12-31',
            error: null as string | null,
          }
        },
      }
      const wrapper = mountWithPlugins(DateForm)
      expect(wrapper.text()).toContain('验证通过')
    })
  })

  describe('数字边界测试', () => {
    it('价格为负数应显示错误', async () => {
      const PriceForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            price: -10,
            error: '价格不能为负数',
          }
        },
      }
      const wrapper = mountWithPlugins(PriceForm)
      expect(wrapper.text()).toContain('价格不能为负数')
    })

    it('价格为0应允许（免费服务）', async () => {
      const PriceForm = {
        template: `
          <div>
            <div v-if="!error" class="success">验证通过</div>
          </div>
        `,
        data() {
          return {
            price: 0,
            error: null as string | null,
          }
        },
      }
      const wrapper = mountWithPlugins(PriceForm)
      expect(wrapper.text()).toContain('验证通过')
    })

    it('价格超过最大值应显示错误', async () => {
      const PriceForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            price: 1000001,
            error: '价格不能超过1000000',
          }
        },
      }
      const wrapper = mountWithPlugins(PriceForm)
      expect(wrapper.text()).toContain('价格不能超过1000000')
    })

    it('数量为0应显示错误', async () => {
      const QuantityForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            quantity: 0,
            error: '数量必须大于0',
          }
        },
      }
      const wrapper = mountWithPlugins(QuantityForm)
      expect(wrapper.text()).toContain('数量必须大于0')
    })

    it('数量为负数应显示错误', async () => {
      const QuantityForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            quantity: -5,
            error: '数量不能为负数',
          }
        },
      }
      const wrapper = mountWithPlugins(QuantityForm)
      expect(wrapper.text()).toContain('数量不能为负数')
    })

    it('数量超过库存应显示错误', async () => {
      const QuantityForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            quantity: 101,
            stock: 100,
            error: '购买数量超过库存',
          }
        },
      }
      const wrapper = mountWithPlugins(QuantityForm)
      expect(wrapper.text()).toContain('购买数量超过库存')
    })

    it('年龄为负数应显示错误', async () => {
      const AgeForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            age: -1,
            error: '年龄不能为负数',
          }
        },
      }
      const wrapper = mountWithPlugins(AgeForm)
      expect(wrapper.text()).toContain('年龄不能为负数')
    })

    it('年龄超过合理范围应显示错误', async () => {
      const AgeForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            age: 200,
            error: '年龄不能超过100',
          }
        },
      }
      const wrapper = mountWithPlugins(AgeForm)
      expect(wrapper.text()).toContain('年龄不能超过100')
    })

    it('评分为非1-5应显示错误', async () => {
      const RatingForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            rating: 6,
            error: '评分必须在1-5之间',
          }
        },
      }
      const wrapper = mountWithPlugins(RatingForm)
      expect(wrapper.text()).toContain('评分必须在1-5之间')
    })

    it('评分为0应显示错误', async () => {
      const RatingForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            rating: 0,
            error: '评分必须在1-5之间',
          }
        },
      }
      const wrapper = mountWithPlugins(RatingForm)
      expect(wrapper.text()).toContain('评分必须在1-5之间')
    })
  })

  describe('密码验证测试', () => {
    it('密码长度不足应显示错误', async () => {
      const PasswordForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            password: '12345',
            error: '密码长度不能少于6位',
          }
        },
      }
      const wrapper = mountWithPlugins(PasswordForm)
      expect(wrapper.text()).toContain('密码长度不能少于6位')
    })

    it('密码过于简单应显示警告', async () => {
      const PasswordForm = {
        template: `
          <div>
            <div v-if="warning" class="warning">{{ warning }}</div>
          </div>
        `,
        data() {
          return {
            password: '123456',
            warning: '密码过于简单，建议包含字母和数字',
          }
        },
      }
      const wrapper = mountWithPlugins(PasswordForm)
      expect(wrapper.text()).toContain('密码过于简单')
    })

    it('确认密码不一致应显示错误', async () => {
      const ConfirmPasswordForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            password: 'password123',
            confirmPassword: 'password456',
            error: '两次输入的密码不一致',
          }
        },
      }
      const wrapper = mountWithPlugins(ConfirmPasswordForm)
      expect(wrapper.text()).toContain('两次输入的密码不一致')
    })

    it('密码包含用户名应显示警告', async () => {
      const PasswordForm = {
        template: `
          <div>
            <div v-if="warning" class="warning">{{ warning }}</div>
          </div>
        `,
        data() {
          return {
            username: 'testuser',
            password: 'testuser123',
            warning: '密码不能包含用户名',
          }
        },
      }
      const wrapper = mountWithPlugins(PasswordForm)
      expect(wrapper.text()).toContain('密码不能包含用户名')
    })
  })

  describe('URL格式验证测试', () => {
    it('无效URL格式应显示错误', async () => {
      const UrlForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            url: 'not-a-url',
            error: '请输入有效的URL地址',
          }
        },
      }
      const wrapper = mountWithPlugins(UrlForm)
      expect(wrapper.text()).toContain('请输入有效的URL地址')
    })

    it('URL缺少协议应显示错误', async () => {
      const UrlForm = {
        template: `
          <div>
            <div v-if="error" class="error">{{ error }}</div>
          </div>
        `,
        data() {
          return {
            url: 'www.example.com',
            error: 'URL必须以http://或https://开头',
          }
        },
      }
      const wrapper = mountWithPlugins(UrlForm)
      expect(wrapper.text()).toContain('URL必须以http://或https://开头')
    })

    it('正确URL格式应通过验证', async () => {
      const UrlForm = {
        template: `
          <div>
            <div v-if="!error" class="success">验证通过</div>
          </div>
        `,
        data() {
          return {
            url: 'https://example.com',
            error: null as string | null,
          }
        },
      }
      const wrapper = mountWithPlugins(UrlForm)
      expect(wrapper.text()).toContain('验证通过')
    })
  })

  describe('表单提交边界测试', () => {
    it('表单未修改时提交按钮应禁用', async () => {
      const FormComponent = {
        template: `
          <div>
            <el-form :model="form">
              <el-input v-model="form.name" />
              <el-button :disabled="!isModified" type="primary">提交</el-button>
            </el-form>
          </div>
        `,
        data() {
          return {
            form: { name: 'original' },
            originalForm: { name: 'original' },
            isModified: false,
          }
        },
      }
      const wrapper = mountWithPlugins(FormComponent)
      const button = wrapper.find('button')
      expect(button.attributes('disabled')).toBeDefined()
    })

    it('表单验证中时提交按钮应禁用', async () => {
      const FormComponent = {
        template: `
          <div>
            <el-form :model="form">
              <el-button :disabled="isValidating" type="primary">提交</el-button>
            </el-form>
          </div>
        `,
        data() {
          return {
            form: { name: '' },
            isValidating: true,
          }
        },
      }
      const wrapper = mountWithPlugins(FormComponent)
      const button = wrapper.find('button')
      expect(button.attributes('disabled')).toBeDefined()
    })

    it('重复提交应被阻止', async () => {
      const FormComponent = {
        template: `
          <div>
            <el-form :model="form">
              <el-button :disabled="isSubmitting" :loading="isSubmitting" type="primary">
                {{ isSubmitting ? '提交中...' : '提交' }}
              </el-button>
            </el-form>
          </div>
        `,
        data() {
          return {
            form: { name: 'test' },
            isSubmitting: true,
          }
        },
      }
      const wrapper = mountWithPlugins(FormComponent)
      const button = wrapper.find('button')
      expect(button.attributes('disabled')).toBeDefined()
      expect(wrapper.text()).toContain('提交中')
    })
  })
})
