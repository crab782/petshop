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
          <button class="bg-primary text-white px-4 py-2 rounded-full hover:bg-opacity-90 transition">登录</button>
          <button class="md:hidden text-dark text-2xl">
            <i class="fa fa-bars"></i>
          </button>
        </div>
      </div>
    </nav>

    <!-- 登录区域 -->
    <section class="pt-32 pb-16">
      <div class="container mx-auto px-4">
        <div class="max-w-md mx-auto bg-white rounded-lg shadow-xl overflow-hidden">
          <div class="p-8">
            <div class="text-center mb-8">
              <div class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mx-auto mb-4">
                <i class="fa fa-user text-primary text-2xl"></i>
              </div>
              <h2 class="text-2xl font-bold text-dark mb-2">欢迎回来</h2>
              <p class="text-gray-600">请登录您的账户</p>
            </div>
            <div class="flex border-b mb-6">
              <button 
                @click="switchTab('user')" 
                :class="activeTab === 'user' ? 'flex-1 py-2 text-center text-primary border-b-2 border-primary font-medium' : 'flex-1 py-2 text-center text-gray-500 font-medium'"
              >
                用户登录
              </button>
              <button 
                @click="switchTab('admin')" 
                :class="activeTab === 'admin' ? 'flex-1 py-2 text-center text-primary border-b-2 border-primary font-medium' : 'flex-1 py-2 text-center text-gray-500 font-medium'"
              >
                管理员登录
              </button>
            </div>
            <div v-if="activeTab === 'user'">
              <form @submit.prevent="handleLogin('user')" class="space-y-4">
                <input type="hidden" name="type" value="user">
                <div>
                  <label class="block text-gray-700 mb-2">用户名/邮箱</label>
                  <div class="relative">
                    <i class="fa fa-user absolute left-3 top-3 text-gray-400"></i>
                    <input type="text" v-model="userForm.email" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
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
                <div class="flex items-center justify-between">
                  <div class="flex items-center">
                    <input type="checkbox" id="remember" v-model="userForm.remember" class="mr-2">
                    <label for="remember" class="text-gray-600 text-sm">记住我</label>
                  </div>
                  <a href="#" class="text-primary hover:underline text-sm">忘记密码？</a>
                </div>
                <button type="submit" class="w-full bg-primary text-white py-3 rounded-lg font-medium hover:bg-opacity-90 transition">登录</button>
              </form>
            </div>
            <div v-else>
              <form @submit.prevent="handleLogin('admin')" class="space-y-4">
                <input type="hidden" name="type" value="admin">
                <div>
                  <label class="block text-gray-700 mb-2">管理员账号</label>
                  <div class="relative">
                    <i class="fa fa-user-circle absolute left-3 top-3 text-gray-400"></i>
                    <input type="text" v-model="adminForm.email" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                  </div>
                </div>
                <div>
                  <label class="block text-gray-700 mb-2">密码</label>
                  <div class="relative">
                    <i class="fa fa-lock absolute left-3 top-3 text-gray-400"></i>
                    <input :type="adminForm.showPassword ? 'text' : 'password'" v-model="adminForm.password" class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                    <button type="button" @click="adminForm.showPassword = !adminForm.showPassword" class="absolute right-3 top-3 text-gray-400 hover:text-gray-600">
                      <i :class="adminForm.showPassword ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
                    </button>
                  </div>
                </div>
                <div class="flex items-center justify-between">
                  <div class="flex items-center">
                    <input type="checkbox" id="adminRemember" v-model="adminForm.remember" class="mr-2">
                    <label for="adminRemember" class="text-gray-600 text-sm">记住我</label>
                  </div>
                </div>
                <button type="submit" class="w-full bg-primary text-white py-3 rounded-lg font-medium hover:bg-opacity-90 transition">登录</button>
              </form>
            </div>
            <div class="mt-6 text-center">
              <p class="text-gray-600">还没有账户？ <router-link to="/register" class="text-primary hover:underline font-medium">立即注册</router-link></p>
            </div>
            <div class="mt-8">
              <div class="flex items-center justify-center">
                <div class="h-px bg-gray-300 flex-grow"></div>
                <span class="px-4 text-gray-500 text-sm">其他登录方式</span>
                <div class="h-px bg-gray-300 flex-grow"></div>
              </div>
              <div class="flex justify-center space-x-4 mt-4">
                <a href="#" class="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center hover:bg-gray-200 transition">
                  <i class="fa fa-weixin text-green-600"></i>
                </a>
                <a href="#" class="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center hover:bg-gray-200 transition">
                  <i class="fa fa-qq text-blue-500"></i>
                </a>
                <a href="#" class="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center hover:bg-gray-200 transition">
                  <i class="fa fa-weibo text-red-500"></i>
                </a>
              </div>
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
  name: 'Login',
  data() {
    return {
      activeTab: 'user',
      userForm: {
        email: '',
        password: '',
        remember: false,
        showPassword: false
      },
      adminForm: {
        email: '',
        password: '',
        remember: false,
        showPassword: false
      }
    }
  },
  methods: {
    switchTab(tab) {
      this.activeTab = tab;
    },
    handleLogin(type) {
      // 这里可以添加登录逻辑，调用API进行登录
      console.log('Login type:', type);
      if (type === 'user') {
        console.log('User login data:', this.userForm);
        // 登录成功后跳转到用户 dashboard
        this.$router.push('/user/dashboard');
      } else {
        console.log('Admin login data:', this.adminForm);
        // 登录成功后跳转到管理员 dashboard
        this.$router.push('/admin/dashboard');
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