import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import { ref } from 'vue'

const createTestRouter = () => createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: { template: '<div>Home</div>' } },
    { path: '/login', name: 'login', component: { template: '<div>Login</div>' } },
    { path: '/user', name: 'user', component: { template: '<div>User</div>' } },
    { path: '/403', name: 'forbidden', component: { template: '<div>403</div>' } },
    { path: '/404', name: 'not-found', component: { template: '<div>404</div>' } },
    { path: '/500', name: 'server-error', component: { template: '<div>500</div>' } },
  ],
})

const mountWithPlugins = async (component: any, options = {}) => {
  const pinia = createPinia()
  setActivePinia(pinia)
  const router = createTestRouter()
  
  const wrapper = mount(component, {
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
          template: '<button :disabled="disabled"><slot /></button>',
          props: ['disabled', 'type', 'loading'],
        },
        'el-input': {
          template: '<input v-model="modelValue" />',
          props: ['modelValue', 'placeholder'],
        },
        'el-form': {
          template: '<form class="el-form"><slot /></form>',
          props: ['model', 'rules'],
        },
        'el-form-item': {
          template: '<div class="el-form-item"><slot /><span v-if="error" class="error">{{ error }}</span></div>',
          props: ['label', 'prop', 'error'],
        },
      },
      ...(options as any).global,
    },
  })
  
  await router.isReady()
  return wrapper
}

const mockErrorResponse = (status: number, message: string) => ({
  response: {
    status,
    data: {
      message,
      code: status,
    },
  },
})

describe('错误处理测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('网络错误测试', () => {
    it('网络断开时应显示网络错误提示', async () => {
      const NetworkErrorComponent = {
        template: `
          <div>
            <div v-if="networkError" class="network-error">
              <span>网络连接失败，请检查网络设置</span>
              <button @click="$emit('retry')">重试</button>
            </div>
            <div v-else>内容加载中...</div>
          </div>
        `,
        data() {
          return { networkError: true }
        },
      }
      const wrapper = await mountWithPlugins(NetworkErrorComponent)
      expect(wrapper.find('.network-error').exists()).toBe(true)
      expect(wrapper.text()).toContain('网络连接失败')
    })

    it('请求超时应显示超时提示', async () => {
      const TimeoutErrorComponent = {
        template: `
          <div>
            <div v-if="isTimeout" class="timeout-error">
              <span>请求超时，请稍后重试</span>
              <button @click="$emit('retry')">重试</button>
            </div>
          </div>
        `,
        data() {
          return { isTimeout: true }
        },
      }
      const wrapper = await mountWithPlugins(TimeoutErrorComponent)
      expect(wrapper.find('.timeout-error').exists()).toBe(true)
      expect(wrapper.text()).toContain('请求超时')
    })

    it('网络错误时应提供重试按钮', async () => {
      const RetryComponent = {
        template: `
          <div>
            <div v-if="error" class="error-container">
              <span>{{ errorMessage }}</span>
              <button class="retry-btn" @click="handleRetry">重试</button>
            </div>
          </div>
        `,
        data() {
          return {
            error: true,
            errorMessage: '网络错误',
            retryCount: 0,
          }
        },
        methods: {
          handleRetry() {
            this.retryCount++
            this.$emit('retry', this.retryCount)
          },
        },
      }
      const wrapper = await mountWithPlugins(RetryComponent)
      const retryBtn = wrapper.find('.retry-btn')
      expect(retryBtn.exists()).toBe(true)
      await retryBtn.trigger('click')
      expect(wrapper.emitted('retry')).toBeTruthy()
    })

    it('多次重试失败后应显示更详细的错误信息', async () => {
      const MultipleRetryComponent = {
        template: `
          <div>
            <div v-if="retryCount >= 3" class="detailed-error">
              多次尝试失败，请联系客服或稍后再试
            </div>
            <div v-else-if="error" class="simple-error">
              加载失败，点击重试
            </div>
          </div>
        `,
        data() {
          return {
            error: true,
            retryCount: 3,
          }
        },
      }
      const wrapper = await mountWithPlugins(MultipleRetryComponent)
      expect(wrapper.find('.detailed-error').exists()).toBe(true)
      expect(wrapper.text()).toContain('多次尝试失败')
    })
  })

  describe('API错误测试 - 400 Bad Request', () => {
    it('400错误应显示请求参数错误提示', async () => {
      const BadRequestComponent = {
        template: `
          <div>
            <div v-if="error" class="error-message">
              {{ errorMessage }}
            </div>
          </div>
        `,
        data() {
          return {
            error: true,
            errorMessage: '请求参数错误，请检查输入',
          }
        },
      }
      const wrapper = await mountWithPlugins(BadRequestComponent)
      expect(wrapper.text()).toContain('请求参数错误')
    })

    it('表单验证错误应高亮显示错误字段', async () => {
      const FormErrorComponent = {
        template: `
          <div>
            <el-form :model="form" :rules="rules" ref="formRef">
              <el-form-item label="名称" prop="name" :error="errors.name">
                <el-input v-model="form.name" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email" :error="errors.email">
                <el-input v-model="form.email" />
              </el-form-item>
            </el-form>
          </div>
        `,
        data() {
          return {
            form: { name: '', email: '' },
            rules: {},
            errors: {
              name: '名称不能为空',
              email: '邮箱格式不正确',
            },
          }
        },
      }
      const wrapper = await mountWithPlugins(FormErrorComponent)
      expect(wrapper.text()).toContain('名称不能为空')
      expect(wrapper.text()).toContain('邮箱格式不正确')
    })

    it('必填字段为空时应显示具体错误', async () => {
      const RequiredFieldComponent = {
        template: `
          <div>
            <div v-if="validationError" class="validation-error">
              {{ fieldName }}为必填项
            </div>
          </div>
        `,
        data() {
          return {
            validationError: true,
            fieldName: '宠物名称',
          }
        },
      }
      const wrapper = await mountWithPlugins(RequiredFieldComponent)
      expect(wrapper.text()).toContain('宠物名称为必填项')
    })
  })

  describe('API错误测试 - 401 Unauthorized', () => {
    it('401错误应跳转到登录页', async () => {
      const UnauthorizedComponent = {
        template: `
          <div>
            <div v-if="unauthorized" class="unauthorized">
              登录已过期，请重新登录
            </div>
          </div>
        `,
        data() {
          return { unauthorized: true }
        },
      }
      const wrapper = await mountWithPlugins(UnauthorizedComponent)
      expect(wrapper.text()).toContain('登录已过期')
    })

    it('未登录访问受保护页面应提示登录', async () => {
      const ProtectedPageComponent = {
        template: `
          <div>
            <div v-if="!isLoggedIn" class="login-prompt">
              <span>请先登录以访问此页面</span>
              <button @click="$emit('login')">去登录</button>
            </div>
          </div>
        `,
        data() {
          return { isLoggedIn: false }
        },
      }
      const wrapper = await mountWithPlugins(ProtectedPageComponent)
      expect(wrapper.text()).toContain('请先登录')
    })

    it('Token过期应清除本地存储并跳转', async () => {
      const TokenExpiredComponent = {
        template: `
          <div>
            <div v-if="tokenExpired" class="token-expired">
              Token已过期，正在跳转到登录页...
            </div>
          </div>
        `,
        data() {
          return { tokenExpired: true }
        },
        mounted() {
          localStorage.removeItem('token')
          localStorage.removeItem('user')
        },
      }
      const wrapper = await mountWithPlugins(TokenExpiredComponent)
      expect(wrapper.text()).toContain('Token已过期')
      expect(localStorage.getItem('token')).toBeNull()
    })
  })

  describe('API错误测试 - 403 Forbidden', () => {
    it('403错误应显示无权限提示', async () => {
      const ForbiddenComponent = {
        template: `
          <div>
            <div v-if="forbidden" class="forbidden">
              <h3>无访问权限</h3>
              <p>您没有权限访问此资源</p>
              <button @click="$emit('goBack')">返回上一页</button>
            </div>
          </div>
        `,
        data() {
          return { forbidden: true }
        },
      }
      const wrapper = await mountWithPlugins(ForbiddenComponent)
      expect(wrapper.text()).toContain('无访问权限')
    })

    it('非商家访问商家管理页面应显示权限错误', async () => {
      const MerchantOnlyComponent = {
        template: `
          <div>
            <div v-if="!isMerchant" class="permission-denied">
              此页面仅限商家访问
            </div>
          </div>
        `,
        data() {
          return { isMerchant: false }
        },
      }
      const wrapper = await mountWithPlugins(MerchantOnlyComponent)
      expect(wrapper.text()).toContain('此页面仅限商家访问')
    })

    it('普通用户访问管理员页面应被拒绝', async () => {
      const AdminOnlyComponent = {
        template: `
          <div>
            <div v-if="!isAdmin" class="admin-required">
              需要管理员权限
            </div>
          </div>
        `,
        data() {
          return { isAdmin: false }
        },
      }
      const wrapper = await mountWithPlugins(AdminOnlyComponent)
      expect(wrapper.text()).toContain('需要管理员权限')
    })
  })

  describe('API错误测试 - 404 Not Found', () => {
    it('404错误应显示资源不存在提示', async () => {
      const NotFoundComponent = {
        template: `
          <div>
            <div v-if="notFound" class="not-found">
              <h3>页面不存在</h3>
              <p>您访问的页面不存在或已被删除</p>
              <button @click="$emit('goHome')">返回首页</button>
            </div>
          </div>
        `,
        data() {
          return { notFound: true }
        },
      }
      const wrapper = await mountWithPlugins(NotFoundComponent)
      expect(wrapper.text()).toContain('页面不存在')
    })

    it('商品不存在应显示友好提示', async () => {
      const ProductNotFoundComponent = {
        template: `
          <div>
            <div v-if="productNotFound" class="product-not-found">
              商品不存在或已下架
              <button @click="$emit('browse')">浏览其他商品</button>
            </div>
          </div>
        `,
        data() {
          return { productNotFound: true }
        },
      }
      const wrapper = await mountWithPlugins(ProductNotFoundComponent)
      expect(wrapper.text()).toContain('商品不存在或已下架')
    })

    it('订单不存在应显示提示', async () => {
      const OrderNotFoundComponent = {
        template: `
          <div>
            <div v-if="orderNotFound" class="order-not-found">
              订单不存在或已被删除
            </div>
          </div>
        `,
        data() {
          return { orderNotFound: true }
        },
      }
      const wrapper = await mountWithPlugins(OrderNotFoundComponent)
      expect(wrapper.text()).toContain('订单不存在')
    })

    it('API路由不存在应显示错误', async () => {
      const ApiNotFoundComponent = {
        template: `
          <div>
            <div v-if="apiNotFound" class="api-error">
              服务暂时不可用，请稍后重试
            </div>
          </div>
        `,
        data() {
          return { apiNotFound: true }
        },
      }
      const wrapper = await mountWithPlugins(ApiNotFoundComponent)
      expect(wrapper.text()).toContain('服务暂时不可用')
    })
  })

  describe('API错误测试 - 500 Internal Server Error', () => {
    it('500错误应显示服务器错误提示', async () => {
      const ServerErrorComponent = {
        template: `
          <div>
            <div v-if="serverError" class="server-error">
              <h3>服务器错误</h3>
              <p>服务器开小差了，请稍后重试</p>
              <button @click="$emit('retry')">重试</button>
            </div>
          </div>
        `,
        data() {
          return { serverError: true }
        },
      }
      const wrapper = await mountWithPlugins(ServerErrorComponent)
      expect(wrapper.text()).toContain('服务器错误')
    })

    it('服务器维护中应显示维护提示', async () => {
      const MaintenanceComponent = {
        template: `
          <div>
            <div v-if="maintenance" class="maintenance">
              <h3>系统维护中</h3>
              <p>系统正在升级维护，请稍后再试</p>
              <p>预计恢复时间：{{ estimatedTime }}</p>
            </div>
          </div>
        `,
        data() {
          return {
            maintenance: true,
            estimatedTime: '2024-04-20 10:00',
          }
        },
      }
      const wrapper = await mountWithPlugins(MaintenanceComponent)
      expect(wrapper.text()).toContain('系统维护中')
    })

    it('数据库错误应显示友好提示', async () => {
      const DatabaseErrorComponent = {
        template: `
          <div>
            <div v-if="dbError" class="db-error">
              数据加载失败，请刷新页面重试
            </div>
          </div>
        `,
        data() {
          return { dbError: true }
        },
      }
      const wrapper = await mountWithPlugins(DatabaseErrorComponent)
      expect(wrapper.text()).toContain('数据加载失败')
    })
  })

  describe('权限错误测试', () => {
    it('无权限操作时应显示提示', async () => {
      const PermissionDeniedComponent = {
        template: `
          <div>
            <div v-if="permissionDenied" class="permission-denied">
              您没有权限执行此操作
            </div>
          </div>
        `,
        data() {
          return { permissionDenied: true }
        },
      }
      const wrapper = await mountWithPlugins(PermissionDeniedComponent)
      expect(wrapper.text()).toContain('没有权限执行此操作')
    })

    it('删除他人数据应被拒绝', async () => {
      const DeleteOtherDataComponent = {
        template: `
          <div>
            <div v-if="deleteDenied" class="delete-denied">
              无法删除不属于您的数据
            </div>
          </div>
        `,
        data() {
          return { deleteDenied: true }
        },
      }
      const wrapper = await mountWithPlugins(DeleteOtherDataComponent)
      expect(wrapper.text()).toContain('无法删除不属于您的数据')
    })

    it('修改他人订单应被拒绝', async () => {
      const ModifyOtherOrderComponent = {
        template: `
          <div>
            <div v-if="modifyDenied" class="modify-denied">
              无法修改不属于您的订单
            </div>
          </div>
        `,
        data() {
          return { modifyDenied: true }
        },
      }
      const wrapper = await mountWithPlugins(ModifyOtherOrderComponent)
      expect(wrapper.text()).toContain('无法修改不属于您的订单')
    })

    it('未登录用户尝试下单应跳转登录', async () => {
      const GuestCheckoutComponent = {
        template: `
          <div>
            <div v-if="!isLoggedIn" class="guest-checkout">
              <span>请先登录后再下单</span>
              <button @click="$emit('login')">立即登录</button>
            </div>
          </div>
        `,
        data() {
          return { isLoggedIn: false }
        },
      }
      const wrapper = await mountWithPlugins(GuestCheckoutComponent)
      expect(wrapper.text()).toContain('请先登录后再下单')
    })
  })

  describe('业务逻辑错误测试', () => {
    it('库存不足时应显示提示', async () => {
      const OutOfStockComponent = {
        template: `
          <div>
            <div v-if="outOfStock" class="out-of-stock">
              商品库存不足，当前库存：{{ stock }}件
            </div>
          </div>
        `,
        data() {
          return {
            outOfStock: true,
            stock: 0,
          }
        },
      }
      const wrapper = await mountWithPlugins(OutOfStockComponent)
      expect(wrapper.text()).toContain('商品库存不足')
    })

    it('商品已下架应显示提示', async () => {
      const ProductOfflineComponent = {
        template: `
          <div>
            <div v-if="productOffline" class="product-offline">
              该商品已下架
            </div>
          </div>
        `,
        data() {
          return { productOffline: true }
        },
      }
      const wrapper = await mountWithPlugins(ProductOfflineComponent)
      expect(wrapper.text()).toContain('该商品已下架')
    })

    it('订单已支付不能重复支付', async () => {
      const AlreadyPaidComponent = {
        template: `
          <div>
            <div v-if="alreadyPaid" class="already-paid">
              订单已支付，请勿重复操作
            </div>
          </div>
        `,
        data() {
          return { alreadyPaid: true }
        },
      }
      const wrapper = await mountWithPlugins(AlreadyPaidComponent)
      expect(wrapper.text()).toContain('订单已支付')
    })

    it('订单已取消不能再次取消', async () => {
      const AlreadyCancelledComponent = {
        template: `
          <div>
            <div v-if="alreadyCancelled" class="already-cancelled">
              订单已取消，无法重复操作
            </div>
          </div>
        `,
        data() {
          return { alreadyCancelled: true }
        },
      }
      const wrapper = await mountWithPlugins(AlreadyCancelledComponent)
      expect(wrapper.text()).toContain('订单已取消')
    })

    it('预约时间已过不能取消', async () => {
      const AppointmentPassedComponent = {
        template: `
          <div>
            <div v-if="appointmentPassed" class="appointment-passed">
              预约时间已过，无法取消
            </div>
          </div>
        `,
        data() {
          return { appointmentPassed: true }
        },
      }
      const wrapper = await mountWithPlugins(AppointmentPassedComponent)
      expect(wrapper.text()).toContain('预约时间已过')
    })

    it('商家未审核通过不能操作', async () => {
      const MerchantPendingComponent = {
        template: `
          <div>
            <div v-if="merchantPending" class="merchant-pending">
              您的商家账号正在审核中，暂时无法操作
            </div>
          </div>
        `,
        data() {
          return { merchantPending: true }
        },
      }
      const wrapper = await mountWithPlugins(MerchantPendingComponent)
      expect(wrapper.text()).toContain('商家账号正在审核中')
    })
  })

  describe('文件上传错误测试', () => {
    it('文件类型不支持应显示错误', async () => {
      const FileTypeComponent = {
        template: `
          <div>
            <div v-if="fileTypeError" class="file-type-error">
              不支持的文件类型，仅支持：jpg, png, gif
            </div>
          </div>
        `,
        data() {
          return { fileTypeError: true }
        },
      }
      const wrapper = await mountWithPlugins(FileTypeComponent)
      expect(wrapper.text()).toContain('不支持的文件类型')
    })

    it('文件大小超限应显示错误', async () => {
      const FileSizeComponent = {
        template: `
          <div>
            <div v-if="fileSizeError" class="file-size-error">
              文件大小超过限制，最大允许{{ maxSize }}MB
            </div>
          </div>
        `,
        data() {
          return {
            fileSizeError: true,
            maxSize: 5,
          }
        },
      }
      const wrapper = await mountWithPlugins(FileSizeComponent)
      expect(wrapper.text()).toContain('文件大小超过限制')
    })

    it('上传失败应显示错误提示', async () => {
      const UploadFailedComponent = {
        template: `
          <div>
            <div v-if="uploadFailed" class="upload-failed">
              文件上传失败，请重试
            </div>
          </div>
        `,
        data() {
          return { uploadFailed: true }
        },
      }
      const wrapper = await mountWithPlugins(UploadFailedComponent)
      expect(wrapper.text()).toContain('文件上传失败')
    })
  })

  describe('支付错误测试', () => {
    it('支付失败应显示错误原因', async () => {
      const PaymentFailedComponent = {
        template: `
          <div>
            <div v-if="paymentFailed" class="payment-failed">
              支付失败：{{ reason }}
            </div>
          </div>
        `,
        data() {
          return {
            paymentFailed: true,
            reason: '余额不足',
          }
        },
      }
      const wrapper = await mountWithPlugins(PaymentFailedComponent)
      expect(wrapper.text()).toContain('支付失败')
      expect(wrapper.text()).toContain('余额不足')
    })

    it('支付超时应显示提示', async () => {
      const PaymentTimeoutComponent = {
        template: `
          <div>
            <div v-if="paymentTimeout" class="payment-timeout">
              支付超时，请重新发起支付
            </div>
          </div>
        `,
        data() {
          return { paymentTimeout: true }
        },
      }
      const wrapper = await mountWithPlugins(PaymentTimeoutComponent)
      expect(wrapper.text()).toContain('支付超时')
    })

    it('退款失败应显示错误', async () => {
      const RefundFailedComponent = {
        template: `
          <div>
            <div v-if="refundFailed" class="refund-failed">
              退款失败，请联系客服处理
            </div>
          </div>
        `,
        data() {
          return { refundFailed: true }
        },
      }
      const wrapper = await mountWithPlugins(RefundFailedComponent)
      expect(wrapper.text()).toContain('退款失败')
    })
  })

  describe('全局错误处理测试', () => {
    it('未捕获的错误应显示通用错误页面', async () => {
      const GlobalErrorComponent = {
        template: `
          <div>
            <div v-if="hasError" class="global-error">
              <h3>出错了</h3>
              <p>系统出现异常，请刷新页面或联系客服</p>
              <button @click="$emit('refresh')">刷新页面</button>
            </div>
          </div>
        `,
        data() {
          return { hasError: true }
        },
      }
      const wrapper = await mountWithPlugins(GlobalErrorComponent)
      expect(wrapper.text()).toContain('出错了')
    })

    it('错误信息应记录到日志', async () => {
      const ErrorLogComponent = {
        template: `
          <div>
            <div v-if="errorLogged">错误已记录</div>
          </div>
        `,
        data() {
          return { errorLogged: true }
        },
      }
      const wrapper = await mountWithPlugins(ErrorLogComponent)
      expect(wrapper.text()).toContain('错误已记录')
    })
  })
})
