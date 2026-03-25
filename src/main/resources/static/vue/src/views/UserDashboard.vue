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
              <span class="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full text-white text-xs flex items-center justify-center">3</span>
            </button>
          </div>
          <div class="flex items-center space-x-2">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=friendly%20asian%20user%20avatar%2C%20professional%20headshot&image_size=square" alt="用户头像" class="w-8 h-8 rounded-full">
            <span class="text-gray-700">{{ username }}</span>
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
              <a href="#forum" @click="setActive('forum')" :class="activeNav === 'forum' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-comments text-xl text-gray-600"></i>
                <span>交流论坛</span>
              </a>
            </li>
            <li>
              <a href="#announcement" @click="setActive('announcement')" :class="activeNav === 'announcement' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-bullhorn text-xl text-gray-600"></i>
                <span>公告通知</span>
              </a>
            </li>
            <li>
              <a href="#appointment" @click="setActive('appointment')" :class="activeNav === 'appointment' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-calendar text-xl text-gray-600"></i>
                <span>预约服务</span>
              </a>
            </li>
            <li>
              <a href="#shops" @click="setActive('shops')" :class="activeNav === 'shops' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-shopping-bag text-xl text-gray-600"></i>
                <span>店铺信息</span>
              </a>
            </li>
            <li>
              <a href="#profile" @click="setActive('profile')" :class="activeNav === 'profile' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-user text-xl text-gray-600"></i>
                <span>个人中心</span>
              </a>
            </li>
            <li>
              <a href="#pets" @click="setActive('pets')" :class="activeNav === 'pets' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-paw text-xl text-gray-600"></i>
                <span>我的宠物</span>
              </a>
            </li>
            <li>
              <a href="#favorites" @click="setActive('favorites')" :class="activeNav === 'favorites' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-heart text-xl text-gray-600"></i>
                <span>收藏评价</span>
              </a>
            </li>
            <li>
              <a href="#reviews" @click="setActive('reviews')" :class="activeNav === 'reviews' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-star text-xl text-gray-600"></i>
                <span>服务评价</span>
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
          <h1 class="text-2xl font-bold text-dark mb-2">欢迎回来，{{ username }}！</h1>
          <p class="text-gray-600">今天是 <span>{{ currentDate }}</span>，祝您有愉快的一天！</p>
        </div>

        <!-- 统计卡片 -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div class="bg-white rounded-lg shadow-md p-6 card-hover">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-700">我的宠物</h3>
              <div class="w-12 h-12 bg-primary/20 rounded-full flex items-center justify-center">
                <i class="fa fa-paw text-primary text-2xl"></i>
              </div>
            </div>
            <p class="text-3xl font-bold text-dark">{{ petCount }}</p>
            <p class="text-gray-600 mt-2">只可爱的宠物</p>
          </div>
          <div class="bg-white rounded-lg shadow-md p-6 card-hover">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-700">待处理预约</h3>
              <div class="w-12 h-12 bg-secondary/20 rounded-full flex items-center justify-center">
                <i class="fa fa-calendar-check-o text-secondary text-2xl"></i>
              </div>
            </div>
            <p class="text-3xl font-bold text-dark">{{ appointmentCount }}</p>
            <p class="text-gray-600 mt-2">个待处理预约</p>
          </div>
          <div class="bg-white rounded-lg shadow-md p-6 card-hover">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-700">服务评价</h3>
              <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                <i class="fa fa-star text-blue-500 text-2xl"></i>
              </div>
            </div>
            <p class="text-3xl font-bold text-dark">{{ reviewCount }}</p>
            <p class="text-gray-600 mt-2">条服务评价</p>
          </div>
        </div>

        <!-- 最近活动 -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-8">
          <h2 class="text-xl font-bold text-dark mb-4">最近活动</h2>
          <div class="overflow-x-auto">
            <table class="min-w-full">
              <thead>
                <tr class="border-b border-gray-200">
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">活动类型</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">详情</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">时间</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">状态</th>
                </tr>
              </thead>
              <tbody>
                <tr class="border-b border-gray-200" v-for="(activity, index) in activities" :key="index">
                  <td class="py-3 px-4">
                    <span :class="getActivityTypeClass(activity.type)">{{ activity.typeText }}</span>
                  </td>
                  <td class="py-3 px-4">{{ activity.detail }}</td>
                  <td class="py-3 px-4">{{ activity.time }}</td>
                  <td class="py-3 px-4">
                    <span :class="getActivityStatusClass(activity.status)">{{ activity.statusText }}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 推荐服务 -->
        <div class="bg-white rounded-lg shadow-md p-6">
          <h2 class="text-xl font-bold text-dark mb-4">推荐服务</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div class="border border-gray-200 rounded-lg p-4 card-hover" v-for="(service, index) in services" :key="index">
              <div class="w-full h-40 bg-gray-200 rounded-lg mb-4 overflow-hidden">
                <img :src="service.image" :alt="service.name" class="w-full h-full object-cover">
              </div>
              <h3 class="font-semibold text-dark mb-2">{{ service.name }}</h3>
              <p class="text-gray-600 text-sm mb-3">{{ service.description }}</p>
              <div class="flex justify-between items-center">
                <span class="text-primary font-bold">{{ service.price }}</span>
                <button class="bg-primary text-white px-4 py-2 rounded-lg hover:bg-opacity-90 transition">立即预约</button>
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
  name: 'UserDashboard',
  data() {
    return {
      username: '用户',
      currentDate: '',
      activeNav: 'dashboard',
      petCount: 3,
      appointmentCount: 2,
      reviewCount: 5,
      activities: [
        {
          type: 'appointment',
          typeText: '预约',
          detail: '宠物美容服务',
          time: '2026-03-20 14:30',
          status: 'completed',
          statusText: '已完成'
        },
        {
          type: 'review',
          typeText: '评价',
          detail: '对宠物美容服务的评价',
          time: '2026-03-20 16:45',
          status: 'completed',
          statusText: '已完成'
        },
        {
          type: 'appointment',
          typeText: '预约',
          detail: '宠物寄养服务',
          time: '2026-03-25 09:00',
          status: 'pending',
          statusText: '待处理'
        }
      ],
      services: [
        {
          name: '宠物美容套餐',
          description: '专业的宠物美容服务，包括洗澡、剪毛、指甲修剪等',
          price: '¥128',
          image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20pet%20grooming%20service%2C%20clean%20modern%20salon&image_size=square'
        },
        {
          name: '宠物寄养服务',
          description: '安全舒适的寄养环境，24小时专人照顾',
          price: '¥80/天',
          image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20boarding%20facility%2C%20comfortable%20cages%2C%20play%20area&image_size=square'
        },
        {
          name: '健康体检套餐',
          description: '全面的宠物健康检查，及早发现潜在问题',
          price: '¥198',
          image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=veterinarian%20examining%20pet%2C%20professional%20clinic&image_size=square'
        }
      ]
    }
  },
  methods: {
    setActive(nav) {
      this.activeNav = nav;
    },
    getActivityTypeClass(type) {
      if (type === 'appointment') {
        return 'inline-block px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs';
      } else if (type === 'review') {
        return 'inline-block px-2 py-1 bg-yellow-100 text-yellow-800 rounded-full text-xs';
      }
      return '';
    },
    getActivityStatusClass(status) {
      if (status === 'completed') {
        return 'inline-block px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs';
      } else if (status === 'pending') {
        return 'inline-block px-2 py-1 bg-orange-100 text-orange-800 rounded-full text-xs';
      } else if (status === 'processing') {
        return 'inline-block px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs';
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