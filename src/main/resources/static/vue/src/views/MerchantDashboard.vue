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
              <span class="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full text-white text-xs flex items-center justify-center">5</span>
            </button>
          </div>
          <div class="flex items-center space-x-2">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20merchant%20avatar%2C%20business%20person&image_size=square" alt="商家头像" class="w-8 h-8 rounded-full">
            <span class="text-gray-700">{{ merchantName }}</span>
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
              <a href="#shop" @click="setActive('shop')" :class="activeNav === 'shop' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-shopping-bag text-xl text-gray-600"></i>
                <span>店铺管理</span>
              </a>
            </li>
            <li>
              <a href="#appointment" @click="setActive('appointment')" :class="activeNav === 'appointment' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-calendar text-xl text-gray-600"></i>
                <span>预约订单列表</span>
              </a>
            </li>
            <li>
              <a href="#product" @click="setActive('product')" :class="activeNav === 'product' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-cubes text-xl text-gray-600"></i>
                <span>商品订单列表</span>
              </a>
            </li>
            <li>
              <a href="#review" @click="setActive('review')" :class="activeNav === 'review' ? 'flex items-center space-x-3 p-3 bg-primary/10 text-primary rounded-lg' : 'flex items-center space-x-3 p-3 hover:bg-gray-100 rounded-lg transition'">
                <i class="fa fa-star text-xl text-gray-600"></i>
                <span>服务评价列表</span>
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
          <h1 class="text-2xl font-bold text-dark mb-2">欢迎回来，{{ merchantName }}！</h1>
          <p class="text-gray-600">今天是 <span>{{ currentDate }}</span>，祝您生意兴隆！</p>
        </div>

        <!-- 统计卡片 -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div class="bg-white rounded-lg shadow-md p-6 card-hover">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-700">今日订单</h3>
              <div class="w-12 h-12 bg-primary/20 rounded-full flex items-center justify-center">
                <i class="fa fa-shopping-cart text-primary text-2xl"></i>
              </div>
            </div>
            <p class="text-3xl font-bold text-dark">{{ todayOrders }}</p>
            <p class="text-gray-600 mt-2">笔订单</p>
          </div>
          <div class="bg-white rounded-lg shadow-md p-6 card-hover">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-700">待处理预约</h3>
              <div class="w-12 h-12 bg-secondary/20 rounded-full flex items-center justify-center">
                <i class="fa fa-calendar-check-o text-secondary text-2xl"></i>
              </div>
            </div>
            <p class="text-3xl font-bold text-dark">{{ pendingAppointments }}</p>
            <p class="text-gray-600 mt-2">个待处理</p>
          </div>
          <div class="bg-white rounded-lg shadow-md p-6 card-hover">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-700">今日收入</h3>
              <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                <i class="fa fa-money text-green-500 text-2xl"></i>
              </div>
            </div>
            <p class="text-3xl font-bold text-dark">{{ todayIncome }}</p>
            <p class="text-gray-600 mt-2">今日收入</p>
          </div>
          <div class="bg-white rounded-lg shadow-md p-6 card-hover">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-700">服务评价</h3>
              <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                <i class="fa fa-star text-blue-500 text-2xl"></i>
              </div>
            </div>
            <p class="text-3xl font-bold text-dark">{{ averageRating }}</p>
            <p class="text-gray-600 mt-2">平均评分</p>
          </div>
        </div>

        <!-- 最近订单 -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-8">
          <h2 class="text-xl font-bold text-dark mb-4">最近订单</h2>
          <div class="overflow-x-auto">
            <table class="min-w-full">
              <thead>
                <tr class="border-b border-gray-200">
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">订单号</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">订单类型</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">客户</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">金额</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">时间</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">状态</th>
                  <th class="py-3 px-4 text-left text-gray-600 font-medium">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr class="border-b border-gray-200" v-for="(order, index) in orders" :key="index">
                  <td class="py-3 px-4">{{ order.orderId }}</td>
                  <td class="py-3 px-4">
                    <span :class="getOrderTypeClass(order.type)">{{ order.typeText }}</span>
                  </td>
                  <td class="py-3 px-4">{{ order.customer }}</td>
                  <td class="py-3 px-4">{{ order.amount }}</td>
                  <td class="py-3 px-4">{{ order.time }}</td>
                  <td class="py-3 px-4">
                    <span :class="getOrderStatusClass(order.status)">{{ order.statusText }}</span>
                  </td>
                  <td class="py-3 px-4">
                    <button class="text-primary hover:underline">查看</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 服务评价 -->
        <div class="bg-white rounded-lg shadow-md p-6">
          <h2 class="text-xl font-bold text-dark mb-4">最新评价</h2>
          <div class="space-y-4">
            <div class="border border-gray-200 rounded-lg p-4" v-for="(review, index) in reviews" :key="index">
              <div class="flex items-start justify-between mb-3">
                <div class="flex items-center">
                  <img :src="review.avatar" alt="客户头像" class="w-10 h-10 rounded-full mr-3">
                  <div>
                    <h4 class="font-semibold text-dark">{{ review.customer }}</h4>
                    <div class="text-yellow-400">
                      <i v-for="n in 5" :key="n" :class="n <= review.rating ? 'fa fa-star' : 'fa fa-star-o'"></i>
                    </div>
                  </div>
                </div>
                <span class="text-gray-500 text-sm">{{ review.date }}</span>
              </div>
              <p class="text-gray-600 mb-3">{{ review.content }}</p>
              <div class="text-sm text-gray-500">
                服务类型：{{ review.serviceType }}
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
  name: 'MerchantDashboard',
  data() {
    return {
      merchantName: '商家名称',
      currentDate: '',
      activeNav: 'dashboard',
      todayOrders: 12,
      pendingAppointments: 8,
      todayIncome: '¥2,580',
      averageRating: 4.8,
      orders: [
        {
          orderId: 'ORD20260325001',
          type: 'appointment',
          typeText: '预约',
          customer: '张先生',
          amount: '¥128',
          time: '2026-03-25 10:30',
          status: 'pending',
          statusText: '待处理'
        },
        {
          orderId: 'ORD20260325002',
          type: 'product',
          typeText: '商品',
          customer: '李女士',
          amount: '¥298',
          time: '2026-03-25 09:15',
          status: 'completed',
          statusText: '已完成'
        },
        {
          orderId: 'ORD20260324001',
          type: 'appointment',
          typeText: '预约',
          customer: '王女士',
          amount: '¥80',
          time: '2026-03-24 16:45',
          status: 'processing',
          statusText: '处理中'
        },
        {
          orderId: 'ORD20260324002',
          type: 'product',
          typeText: '商品',
          customer: '赵先生',
          amount: '¥156',
          time: '2026-03-24 14:20',
          status: 'completed',
          statusText: '已完成'
        }
      ],
      reviews: [
        {
          customer: '张先生',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=friendly%20customer%20avatar%2C%20happy%20expression&image_size=square',
          rating: 5,
          date: '2026-03-24',
          content: '服务非常专业，我的狗狗做完美容后变得特别精神，工作人员也很有耐心，会继续选择这里的服务。',
          serviceType: '宠物美容'
        },
        {
          customer: '李女士',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=friendly%20customer%20avatar%2C%20female&image_size=square',
          rating: 4.5,
          date: '2026-03-23',
          content: '寄养服务非常贴心，我出差期间把猫咪放在这里，工作人员每天都会给我发猫咪的照片和视频，让我很放心。',
          serviceType: '宠物寄养'
        },
        {
          customer: '王先生',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=friendly%20customer%20avatar%2C%20young%20man&image_size=square',
          rating: 5,
          date: '2026-03-22',
          content: '宠物训练课程效果很好，我的狗狗现在变得很听话，训练师很专业，会根据狗狗的情况制定个性化的训练计划。',
          serviceType: '宠物训练'
        }
      ]
    }
  },
  methods: {
    setActive(nav) {
      this.activeNav = nav;
    },
    getOrderTypeClass(type) {
      if (type === 'appointment') {
        return 'inline-block px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs';
      } else if (type === 'product') {
        return 'inline-block px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs';
      }
      return '';
    },
    getOrderStatusClass(status) {
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