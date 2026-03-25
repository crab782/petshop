<template>
  <div class="bg-light font-sans">
    <!-- 导航栏 -->
    <nav class="bg-white shadow-md fixed w-full z-50">
      <div class="container mx-auto px-4 py-3 flex justify-between items-center">
        <div class="flex items-center">
          <i class="fa fa-paw text-primary text-3xl mr-2"></i>
          <span class="text-2xl font-bold text-dark">宠物家园</span>
        </div>
        <div class="hidden md:flex space-x-8">
          <router-link to="/" class="text-dark hover:text-primary font-medium">首页</router-link>
          <router-link to="/" class="text-dark hover:text-primary font-medium">服务</router-link>
          <router-link to="/" class="text-dark hover:text-primary font-medium">宠物展示</router-link>
          <router-link to="/" class="text-dark hover:text-primary font-medium">关于我们</router-link>
          <router-link to="/" class="text-dark hover:text-primary font-medium">联系我们</router-link>
        </div>
        <div class="flex items-center space-x-4">
          <router-link to="/login" class="bg-primary text-white px-4 py-2 rounded-full hover:bg-opacity-90 transition">登录</router-link>
          <button class="md:hidden text-dark text-2xl">
            <i class="fa fa-bars"></i>
          </button>
        </div>
      </div>
    </nav>

    <!-- 注册区域 -->
    <section class="pt-32 pb-16">
      <div class="container mx-auto px-4">
        <div class="max-w-md mx-auto bg-white rounded-lg shadow-xl overflow-hidden">
          <div class="p-8">
            <div class="text-center mb-8">
              <div class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mx-auto mb-4">
                <i class="fa fa-user-plus text-primary text-2xl"></i>
              </div>
              <h2 class="text-2xl font-bold text-dark mb-2">创建账户</h2>
              <p class="text-gray-600">请填写以下信息完成注册</p>
            </div>
            <div class="flex border-b mb-6">
              <button 
                @click="switchTab('user')" 
                :class="activeTab === 'user' ? 'flex-1 py-2 text-center text-primary border-b-2 border-primary font-medium' : 'flex-1 py-2 text-center text-gray-500 font-medium'"
              >
                用户注册
              </button>
              <button 
                @click="switchTab('merchant')" 
                :class="activeTab === 'merchant' ? 'flex-1 py-2 text-center text-primary border-b-2 border-primary font-medium' : 'flex-1 py-2 text-center text-gray-500 font-medium'"
              >
                商家注册
              </button>
            </div>
            <div v-if="activeTab === 'user'">
              <form @submit.prevent="handleRegister('user')" class="space-y-4">
                <div>
                  <label class="block text-gray-700 mb-2">用户名</label>
                  <div class="relative">
                    <i class="fa fa-user absolute left-3 top-3 text-gray-400"></i>
                    <input type="text" v-model="userForm.username" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                  </div>
                </div>
                <div>
                  <label class="block text-gray-700 mb-2">邮箱</label>
                  <div class="relative">
                    <i class="fa fa-envelope absolute left-3 top-3 text-gray-400"></i>
                    <input type="email" v-model="userForm.email" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                  </div>
                </div>
                <div>
                  <label class="block text-gray-700 mb-2">密码</label>
                  <div class="relative">
                    <i class="fa fa-lock absolute left-3 top-3 text-gray-400"></i>
                    <input :type="userForm.showPassword ? 'text' : 'password'" v-model="userForm.password" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                    <button type="button" @click="userForm.showPassword = !userForm.showPassword" class="absolute right-3 top-3 text-gray-400 hover:text-gray-600">
                      <i :class="userForm.showPassword ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
                    </button>
                  </div>
                </div>
                <div>
                  <label class="block text-gray-700 mb-2">确认密码</label>
                  <div class="relative">
                    <i class="fa fa-lock absolute left-3 top-3 text-gray-400"></i>
                    <input :type="userForm.showConfirmPassword ? 'text' : 'password'" v-model="userForm.confirmPassword" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                    <button type="button" @click="userForm.showConfirmPassword = !userForm.showConfirmPassword" class="absolute right-3 top-3 text-gray-400 hover:text-gray-600">
                      <i :class="userForm.showConfirmPassword ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
                    </button>
                  </div>
                </div>
                <div>
                  <label class="block text-gray-700 mb-2">手机号码</label>
                  <div class="relative">
                    <i class="fa fa-phone absolute left-3 top-3 text-gray-400"></i>
                    <input type="tel" v-model="userForm.phone" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                  </div>
                </div>
                <div class="flex items-center">
                  <input type="checkbox" id="agree" v-model="userForm.agree" class="mr-2">
                  <label for="agree" class="text-gray-600 text-sm">我已阅读并同意 <a href="#" class="text-primary hover:underline">用户协议</a> 和 <a href="#" class="text-primary hover:underline">隐私政策</a></label>
                </div>
                <button type="submit" class="w-full bg-primary text-white py-3 rounded-lg font-medium hover:bg-opacity-90 transition">注册</button>
              </form>
            </div>
            <div v-else>
              <form @submit.prevent="handleRegister('merchant')" class="space-y-4">
                <div>
                  <label class="block text-gray-700 mb-2">商家名称</label>
                  <div class="relative">
                    <i class="fa fa-building absolute left-3 top-3 text-gray-400"></i>
                    <input type="text" v-model="merchantForm.name" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                  </div>
                </div>
                <div>
                  <label class="block text-gray-700 mb-2">联系人</label>
                  <div class="relative">
                    <i class="fa fa-user absolute left-3 top-3 text-gray-400"></i>
                    <input type="text" v-model="merchantForm.contactPerson" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                  </div>
                </div>
                <div>
                  <label class="block text-gray-700 mb-2">联系电话</label>
                  <div class="relative">
                    <i class="fa fa-phone absolute left-3 top-3 text-gray-400"></i>
                    <input type="tel" v-model="merchantForm.phone" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                  </div>
                </div>
                <div>
                  <label class="block text-gray-700 mb-2">邮箱</label>
                  <div class="relative">
                    <i class="fa fa-envelope absolute left-3 top-3 text-gray-400"></i>
                    <input type="email" v-model="merchantForm.email" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                  </div>
                </div>
                <div>
                  <label class="block text-gray-700 mb-2">密码</label>
                  <div class="relative">
                    <i class="fa fa-lock absolute left-3 top-3 text-gray-400"></i>
                    <input :type="merchantForm.showPassword ? 'text' : 'password'" v-model="merchantForm.password" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                    <button type="button" @click="merchantForm.showPassword = !merchantForm.showPassword" class="absolute right-3 top-3 text-gray-400 hover:text-gray-600">
                      <i :class="merchantForm.showPassword ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
                    </button>
                  </div>
                </div>
                <div>
                  <label class="block text-gray-700 mb-2">确认密码</label>
                  <div class="relative">
                    <i class="fa fa-lock absolute left-3 top-3 text-gray-400"></i>
                    <input :type="merchantForm.showConfirmPassword ? 'text' : 'password'" v-model="merchantForm.confirmPassword" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                    <button type="button" @click="merchantForm.showConfirmPassword = !merchantForm.showConfirmPassword" class="absolute right-3 top-3 text-gray-400 hover:text-gray-600">
                      <i :class="merchantForm.showConfirmPassword ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
                    </button>
                  </div>
                </div>
                <div>
                  <label class="block text-gray-700 mb-2">地址</label>
                  <div class="relative">
                    <i class="fa fa-map-marker absolute left-3 top-3 text-gray-400"></i>
                    <input type="text" v-model="merchantForm.address" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                  </div>
                </div>
                <div class="flex items-center">
                  <input type="checkbox" id="merchantAgree" v-model="merchantForm.agree" class="mr-2">
                  <label for="merchantAgree" class="text-gray-600 text-sm">我已阅读并同意 <a href="#" class="text-primary hover:underline">商家协议</a> 和 <a href="#" class="text-primary hover:underline">隐私政策</a></label>
                </div>
                <button type="submit" class="w-full bg-primary text-white py-3 rounded-lg font-medium hover:bg-opacity-90 transition">注册</button>
              </form>
            </div>
            <div class="mt-6 text-center">
              <p class="text-gray-600">已有账户？ <router-link to="/login" class="text-primary hover:underline font-medium">立即登录</router-link></p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部区域 -->
    <footer class="bg-dark text-white py-12">
      <div class="container mx-auto px-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <div class="flex items-center mb-4">
              <i class="fa fa-paw text-primary text-2xl mr-2"></i>
              <span class="text-xl font-bold">宠物家园</span>
            </div>
            <p class="text-gray-400 mb-4">专业的宠物服务平台，为您的爱宠提供全方位的呵护与关怀</p>
            <div class="flex space-x-4">
              <a href="#" class="text-gray-400 hover:text-primary transition">
                <i class="fa fa-weixin"></i>
              </a>
              <a href="#" class="text-gray-400 hover:text-primary transition">
                <i class="fa fa-weibo"></i>
              </a>
              <a href="#" class="text-gray-400 hover:text-primary transition">
                <i class="fa fa-instagram"></i>
              </a>
              <a href="#" class="text-gray-400 hover:text-primary transition">
                <i class="fa fa-facebook"></i>
              </a>
            </div>
          </div>
          <div>
            <h4 class="text-lg font-semibold mb-4">服务</h4>
            <ul class="space-y-2">
              <li><a href="#" class="text-gray-400 hover:text-primary transition">宠物美容</a></li>
              <li><a href="#" class="text-gray-400 hover:text-primary transition">健康体检</a></li>
              <li><a href="#" class="text-gray-400 hover:text-primary transition">宠物寄养</a></li>
              <li><a href="#" class="text-gray-400 hover:text-primary transition">宠物饮食</a></li>
              <li><a href="#" class="text-gray-400 hover:text-primary transition">宠物训练</a></li>
            </ul>
          </div>
          <div>
            <h4 class="text-lg font-semibold mb-4">快速链接</h4>
            <ul class="space-y-2">
              <li><router-link to="/" class="text-gray-400 hover:text-primary transition">首页</router-link></li>
              <li><router-link to="/" class="text-gray-400 hover:text-primary transition">服务</router-link></li>
              <li><router-link to="/" class="text-gray-400 hover:text-primary transition">宠物展示</router-link></li>
              <li><router-link to="/" class="text-gray-400 hover:text-primary transition">关于我们</router-link></li>
              <li><router-link to="/" class="text-gray-400 hover:text-primary transition">联系我们</router-link></li>
            </ul>
          </div>
          <div>
            <h4 class="text-lg font-semibold mb-4">联系我们</h4>
            <ul class="space-y-2">
              <li class="flex items-start">
                <i class="fa fa-map-marker text-primary mt-1 mr-2"></i>
                <span class="text-gray-400">北京市朝阳区建国路88号</span>
              </li>
              <li class="flex items-start">
                <i class="fa fa-phone text-primary mt-1 mr-2"></i>
                <span class="text-gray-400">010-12345678</span>
              </li>
              <li class="flex items-start">
                <i class="fa fa-envelope text-primary mt-1 mr-2"></i>
                <span class="text-gray-400">info@pet-home.com</span>
              </li>
            </ul>
          </div>
        </div>
        <div class="border-t border-gray-800 mt-8 pt-8 text-center text-gray-400">
          <p>&copy; 2026 宠物家园. 保留所有权利.</p>
        </div>
      </div>
    </footer>

    <!-- 返回顶部按钮 -->
    <button id="backToTop" class="fixed bottom-8 right-8 bg-primary text-white w-12 h-12 rounded-full flex items-center justify-center shadow-lg opacity-0 invisible transition-all duration-300">
      <i class="fa fa-arrow-up"></i>
    </button>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    return {
      activeTab: 'user',
      userForm: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        phone: '',
        agree: false,
        showPassword: false,
        showConfirmPassword: false
      },
      merchantForm: {
        name: '',
        contactPerson: '',
        phone: '',
        email: '',
        password: '',
        confirmPassword: '',
        address: '',
        agree: false,
        showPassword: false,
        showConfirmPassword: false
      }
    }
  },
  methods: {
    switchTab(tab) {
      this.activeTab = tab;
    },
    handleRegister(type) {
      // 这里可以添加注册逻辑，调用API进行注册
      console.log('Register type:', type);
      if (type === 'user') {
        console.log('User register data:', this.userForm);
        // 注册成功后跳转到登录页面
        this.$router.push('/login');
      } else {
        console.log('Merchant register data:', this.merchantForm);
        // 注册成功后跳转到登录页面
        this.$router.push('/login');
      }
    }
  },
  mounted() {
    // 返回顶部按钮功能
    const backToTopBtn = document.getElementById('backToTop');
    
    window.addEventListener('scroll', function() {
      if (window.pageYOffset > 300) {
        backToTopBtn.classList.remove('opacity-0', 'invisible');
        backToTopBtn.classList.add('opacity-100', 'visible');
      } else {
        backToTopBtn.classList.remove('opacity-100', 'visible');
        backToTopBtn.classList.add('opacity-0', 'invisible');
      }
    });
    
    backToTopBtn.addEventListener('click', function() {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }
}
</script>

<style>
/* Tailwind CSS 已经在HTML中通过CDN引入，这里只添加自定义样式 */
.text-shadow {
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.card-hover {
  transition: all 0.3s ease;
}

.card-hover:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}
</style>

<!-- 引入Tailwind CSS和Font Awesome -->
<link href="https://cdn.tailwindcss.com" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/font-awesome@4.7.0/css/font-awesome.min.css" rel="stylesheet">
<script>
  tailwind.config = {
    theme: {
      extend: {
        colors: {
          primary: '#4CAF50',
          secondary: '#FF9800',
          dark: '#333333',
          light: '#F5F5F5'
        },
        fontFamily: {
          sans: ['Arial', 'sans-serif']
        }
      }
    }
  }
</script>