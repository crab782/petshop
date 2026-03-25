<template>
  <div class="bg-light font-sans">
    <!-- 顶部导航栏 -->
    <nav class="bg-white shadow-md fixed w-full z-50">
      <div class="container mx-auto px-4 py-3 flex justify-between items-center">
        <div class="flex items-center">
          <i class="fa fa-paw text-primary text-3xl mr-2"></i>
          <span class="text-2xl font-bold text-dark">宠物家园</span>
        </div>
        <div class="flex items-center space-x-4">
          <div class="relative">
            <button class="flex items-center space-x-2">
              <i class="fa fa-bell text-gray-500"></i>
              <span class="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full text-white text-xs flex items-center justify-center">8</span>
            </button>
          </div>
          <div class="flex items-center space-x-2">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20admin%20avatar%2C%20serious%20expression&image_size=square" alt="管理员头像" class="w-8 h-8 rounded-full">
            <span class="text-gray-700">管理员</span>
            <i class="fa fa-caret-down text-gray-500"></i>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主内容区域 -->
    <div class="flex pt-16 min-h-screen">
      <!-- 左侧导航栏 -->
      <aside class="w-64 bg-white shadow-md fixed h-screen pt-16">
        <nav class="p-4">
          <ul class="space-y-2">
            <li>
              <a href="#dashboard" @click="setActive('dashboard')" :class="activeNav === 'dashboard' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-home text-xl"></i>
                <span class="font-medium">后台首页</span>
              </a>
            </li>
            <li>
              <a href="#user" @click="setActive('user')" :class="activeNav === 'user' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-users text-xl text-gray-600"></i>
                <span>用户管理</span>
              </a>
            </li>
            <li>
              <a href="#shop" @click="setActive('shop')" :class="activeNav === 'shop' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-shopping-bag text-xl text-gray-600"></i>
                <span>店铺信息管理</span>
              </a>
            </li>
            <li>
              <a href="#pet" @click="setActive('pet')" :class="activeNav === 'pet' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-paw text-xl text-gray-600"></i>
                <span>宠物信息管理</span>
              </a>
            </li>
            <li>
              <a href="#forum" @click="setActive('forum')" :class="activeNav === 'forum' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-comments text-xl text-gray-600"></i>
                <span>论坛管理</span>
              </a>
            </li>
            <li>
              <a href="#announcement" @click="setActive('announcement')" :class="activeNav === 'announcement' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-bullhorn text-xl text-gray-600"></i>
                <span>公告管理</span>
              </a>
            </li>
            <li>
              <a href="#system" @click="setActive('system')" :class="activeNav === 'system' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-cog text-xl text-gray-600"></i>
                <span>系统管理</span>
              </a>
            </li>
            <li>
              <a href="#merchant" @click="setActive('merchant')" :class="activeNav === 'merchant' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-building text-xl text-gray-600"></i>
                <span>商店管理</span>
              </a>
            </li>
            <li>
              <a href="#review" @click="setActive('review')" :class="activeNav === 'review' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-star text-xl text-gray-600"></i>
                <span>服务评价管理</span>
              </a>
            </li>
            <li class="mt-8">
              <router-link to="/login" class="flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition">
                <i class="fa fa-sign-out text-xl text-gray-600"></i>
                <span>退出登录</span>
              </router-link>
            </li>
          </ul>
        </nav>
      </aside>

      <!-- 右侧内容区域 -->
      <main class="flex-1 ml-64 p-8">
        <!-- 欢迎区域 -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-8">
          <h1 class="text-2xl font-bold text-dark mb-2">欢迎回来，管理员！</h1>
          <p class="text-gray-600">今天是 <span>{{ currentDate }}</span>，祝您工作顺利！</p>
        </div>

        <!-- 统计卡片 -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div class="bg-white rounded-lg shadow-md p-6 card-hover">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-700">总用户数</h3>
              <div class="w-12 h-12 bg-primary/20 rounded-full flex items-center justify-center">
                <i class="fa fa-users text-primary text-2xl"></i>
              </div>
            </div>
            <p class="text-3xl font-bold text-dark">{{ totalUsers }}</p>
            <p class="text-gray-600 mt-2">位用户</p>
          </div>
          <div class="bg-white rounded-lg shadow-md p-6 card-hover">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-700">商家数量</h3>
              <div class="w-12 h-12 bg-secondary/20 rounded-full flex items-center justify-center">
                <i class="fa fa-building text-secondary text-2xl"></i>
              </div>
            </div>
            <p class="text-3xl font-bold text-dark">{{ totalMerchants }}</p>
            <p class="text-gray-600 mt-2">家商家</p>
          </div>
          <div class="bg-white rounded-lg shadow-md p-6 card-hover">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-700">宠物数量</h3>
              <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                <i class="fa fa-paw text-green-500 text-2xl"></i>
              </div>
            </div>
            <p class="text-3xl font-bold text-dark">{{ totalPets }}</p>
            <p class="text-gray-600 mt-2">只宠物</p>
          </div>
          <div class="bg-white rounded-lg shadow-md p-6 card-hover">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-700">总订单数</h3>
              <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                <i class="fa fa-shopping-cart text-blue-500 text-2xl"></i>
              </div>
            </div>
            <p class="text-3xl font-bold text-dark">{{ totalOrders }}</p>
            <p class="text-gray-600 mt-2">笔订单</p>
          </div>
        </div>

        <!-- 最近用户 -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-8">
          <h2 class="text-xl font-bold text-dark mb-4">最近注册用户</h2>
          <div class="overflow-x-auto">
            <table class="min-w-full">
              <thead>
                <tr class="border-b border-gray-200">
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">用户ID</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">用户名</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">邮箱</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">注册时间</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">状态</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr class="border-b border-gray-200" v-for="(user, index) in users" :key="index">
                  <td class="py-3 px-4">{{ user.id }}</td>
                  <td class="py-3 px-4">
                    <div class="flex items-center">
                      <img :src="user.avatar" alt="用户头像" class="w-8 h-8 rounded-full mr-3">
                      <span>{{ user.name }}</span>
                    </div>
                  </td>
                  <td class="py-3 px-4">{{ user.email }}</td>
                  <td class="py-3 px-4">{{ user.registerTime }}</td>
                  <td class="py-3 px-4">
                    <span :class="getUserStatusClass(user.status)">{{ user.statusText }}</span>
                  </td>
                  <td class="py-3 px-4">
                    <button class="text-primary hover:underline mr-3">编辑</button>
                    <button class="text-red-500 hover:underline">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 最近商家 -->
        <div class="bg-white rounded-lg shadow-md p-6">
          <h2 class="text-xl font-bold text-dark mb-4">最近注册商家</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="border border-gray-200 rounded-lg p-4 card-hover" v-for="(merchant, index) in merchants" :key="index">
              <div class="flex items-start justify-between mb-3">
                <div class="flex items-center">
                  <img :src="merchant.logo" alt="商家 logo" class="w-12 h-12 rounded-lg mr-3">
                  <div>
                    <h4 class="font-semibold text-dark">{{ merchant.name }}</h4>
                    <p class="text-gray-600 text-sm">{{ merchant.address }}</p>
                  </div>
                </div>
                <span :class="getMerchantStatusClass(merchant.status)">{{ merchant.statusText }}</span>
              </div>
              <div class="flex items-center text-sm text-gray-600 mb-3">
                <i class="fa fa-phone mr-2"></i>
                <span>{{ merchant.phone }}</span>
              </div>
              <div class="flex items-center text-sm text-gray-600 mb-3">
                <i class="fa fa-envelope mr-2"></i>
                <span>{{ merchant.email }}</span>
              </div>
              <div class="flex space-x-2">
                <button class="flex-1 bg-primary text-white py-2 rounded-lg hover:bg-opacity-90 transition">编辑</button>
                <button class="flex-1 border border-gray-300 text-gray-700 py-2 rounded-lg hover:bg-gray-100 transition">删除</button>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminDashboard',
  data() {
    return {
      currentDate: '',
      activeNav: 'dashboard',
      totalUsers: 1258,
      totalMerchants: 32,
      totalPets: 2845,
      totalOrders: 5678,
      users: [
        {
          id: 'U1001',
          name: '张三',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=young%20user%20avatar%2C%20friendly&image_size=square',
          email: 'zhangsan@example.com',
          registerTime: '2026-03-25 10:30',
          status: 'active',
          statusText: '正常'
        },
        {
          id: 'U1002',
          name: '李四',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20user%20avatar%2C%20smiling&image_size=square',
          email: 'lisi@example.com',
          registerTime: '2026-03-25 09:15',
          status: 'active',
          statusText: '正常'
        },
        {
          id: 'U1003',
          name: '王五',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=young%20man%20avatar%2C%20casual&image_size=square',
          email: 'wangwu@example.com',
          registerTime: '2026-03-24 16:45',
          status: 'active',
          statusText: '正常'
        },
        {
          id: 'U1004',
          name: '赵六',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=woman%20avatar%2C%20professional&image_size=square',
          email: 'zhaoliu@example.com',
          registerTime: '2026-03-24 14:20',
          status: 'pending',
          statusText: '待审核'
        }
      ],
      merchants: [
        {
          name: '快乐宠物美容店',
          logo: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20shop%20logo%2C%20professional&image_size=square',
          address: '北京市朝阳区',
          phone: '010-12345678',
          email: 'happy@petshop.com',
          status: 'approved',
          statusText: '已审核'
        },
        {
          name: '爱心宠物医院',
          logo: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20shop%20logo%2C%20modern&image_size=square',
          address: '北京市海淀区',
          phone: '010-87654321',
          email: 'love@pethospital.com',
          status: 'pending',
          statusText: '待审核'
        }
      ]
    }
  },
  methods: {
    setActive(nav) {
      this.activeNav = nav;
    },
    getUserStatusClass(status) {
      if (status === 'active') {
        return 'inline-block px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs';
      } else if (status === 'pending') {
        return 'inline-block px-2 py-1 bg-yellow-100 text-yellow-800 rounded-full text-xs';
      } else if (status === 'inactive') {
        return 'inline-block px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs';
      }
      return '';
    },
    getMerchantStatusClass(status) {
      if (status === 'approved') {
        return 'inline-block px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs';
      } else if (status === 'pending') {
        return 'inline-block px-2 py-1 bg-yellow-100 text-yellow-800 rounded-full text-xs';
      } else if (status === 'rejected') {
        return 'inline-block px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs';
      }
      return '';
    }
  },
  mounted() {
    // 设置当前日期
    this.currentDate = new Date().toLocaleDateString('zh-CN');
  }
}
</script>

<style scoped>
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