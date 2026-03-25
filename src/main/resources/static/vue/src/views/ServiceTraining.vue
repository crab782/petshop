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

    <!-- 服务详情头部 -->
    <section class="pt-24 pb-16 bg-gradient-to-r from-primary to-primary/80 text-white">
      <div class="container mx-auto px-4">
        <div class="flex flex-col md:flex-row items-center">
          <div class="md:w-1/2 mb-8 md:mb-0">
            <h1 class="text-4xl md:text-5xl font-bold mb-4 text-shadow">宠物训练服务</h1>
            <p class="text-lg mb-6">专业的宠物训练课程，帮助您的爱宠养成良好习惯，增强互动能力。</p>
            <div class="flex space-x-4">
              <a href="#booking" class="bg-white text-primary px-6 py-3 rounded-full font-medium hover:bg-opacity-90 transition">立即预约</a>
              <router-link to="/" class="border-2 border-white text-white px-6 py-3 rounded-full font-medium hover:bg-white hover:text-primary transition">返回服务列表</router-link>
            </div>
          </div>
          <div class="md:w-1/2">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20dog%20training%20session%2C%20trainer%20teaching%20dog%20obedience%2C%20positive%20reinforcement%2C%20outdoor%20setting&image_size=landscape_16_9" alt="宠物训练" class="rounded-lg shadow-xl w-full">
          </div>
        </div>
      </div>
    </section>

    <!-- 服务详情 -->
    <section class="py-16 bg-white">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-dark mb-4">服务详情</h2>
          <p class="text-gray-600 max-w-3xl mx-auto">我们提供专业的宠物训练课程，由经验丰富的训练师指导，帮助您的爱宠养成良好习惯，增强与您的互动能力。</p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-12">
          <div class="bg-light rounded-lg p-6 shadow-md">
            <h3 class="text-xl font-semibold mb-4 flex items-center">
              <i class="fa fa-book text-primary mr-2"></i>
              服务内容
            </h3>
            <ul class="space-y-3">
              <li class="flex items-start">
                <i class="fa fa-check-circle text-primary mt-1 mr-3"></i>
                <span>基础 obedience训练（坐下、握手、趴下等）</span>
              </li>
              <li class="flex items-start">
                <i class="fa fa-check-circle text-primary mt-1 mr-3"></i>
                <span>行为纠正（如咬东西、乱叫、分离焦虑等）</span>
              </li>
              <li class="flex items-start">
                <i class="fa fa-check-circle text-primary mt-1 mr-3"></i>
                <span>社交化训练（与其他宠物和人相处）</span>
              </li>
              <li class="flex items-start">
                <i class="fa fa-check-circle text-primary mt-1 mr-3"></i>
                <span> leash训练和散步礼仪</span>
              </li>
              <li class="flex items-start">
                <i class="fa fa-check-circle text-primary mt-1 mr-3"></i>
                <span>高级技能训练（如接飞盘、打滚等）</span>
              </li>
              <li class="flex items-start">
                <i class="fa fa-check-circle text-primary mt-1 mr-3"></i>
                <span>个性化训练方案制定</span>
              </li>
            </ul>
          </div>
          
          <div class="bg-light rounded-lg p-6 shadow-md">
            <h3 class="text-xl font-semibold mb-4 flex items-center">
              <i class="fa fa-clock-o text-primary mr-2"></i>
              服务价格
            </h3>
            <ul class="space-y-3">
              <li class="flex justify-between">
                <span>基础训练课程（4课时）</span>
                <span class="font-medium">¥400</span>
              </li>
              <li class="flex justify-between">
                <span>高级训练课程（6课时）</span>
                <span class="font-medium">¥600</span>
              </li>
              <li class="flex justify-between">
                <span>行为纠正课程（8课时）</span>
                <span class="font-medium">¥800</span>
              </li>
              <li class="flex justify-between">
                <span>一对一私人训练（每课时）</span>
                <span class="font-medium">¥200</span>
              </li>
              <li class="flex justify-between">
                <span>团体训练课程（每课时）</span>
                <span class="font-medium">¥100</span>
              </li>
              <li class="flex justify-between">
                <span>上门训练服务（每课时）</span>
                <span class="font-medium">¥250</span>
              </li>
            </ul>
          </div>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
          <div class="bg-light rounded-lg p-6 shadow-md text-center card-hover">
            <div class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mb-4 mx-auto">
              <i class="fa fa-user text-primary text-2xl"></i>
            </div>
            <h3 class="text-lg font-semibold mb-2">专业训练师</h3>
            <p class="text-gray-600">经验丰富的专业训练师，使用正向强化方法</p>
          </div>
          <div class="bg-light rounded-lg p-6 shadow-md text-center card-hover">
            <div class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mb-4 mx-auto">
              <i class="fa fa-calendar-check-o text-primary text-2xl"></i>
            </div>
            <h3 class="text-lg font-semibold mb-2">灵活时间</h3>
            <p class="text-gray-600">根据您的时间安排，提供灵活的训练课程</p>
          </div>
          <div class="bg-light rounded-lg p-6 shadow-md text-center card-hover">
            <div class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mb-4 mx-auto">
              <i class="fa fa-line-chart text-primary text-2xl"></i>
            </div>
            <h3 class="text-lg font-semibold mb-2">效果显著</h3>
            <p class="text-gray-600">科学的训练方法，确保训练效果显著持久</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 预约表单 -->
    <section id="booking" class="py-16 bg-light">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-dark mb-4">在线预约</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">填写以下表单预约宠物训练服务，我们的工作人员会尽快与您联系确认。</p>
        </div>
        
        <div class="max-w-3xl mx-auto bg-white rounded-lg shadow-lg p-8">
          <form class="space-y-6" @submit.prevent="submitForm">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-gray-700 mb-2">宠物主人姓名</label>
                <input type="text" v-model="form.ownerName" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
              </div>
              <div>
                <label class="block text-gray-700 mb-2">联系电话</label>
                <input type="tel" v-model="form.phone" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
              </div>
            </div>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-gray-700 mb-2">宠物名称</label>
                <input type="text" v-model="form.petName" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
              </div>
              <div>
                <label class="block text-gray-700 mb-2">宠物品种</label>
                <input type="text" v-model="form.petBreed" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
              </div>
            </div>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-gray-700 mb-2">宠物年龄</label>
                <input type="text" v-model="form.petAge" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" placeholder="如：2岁">
              </div>
              <div>
                <label class="block text-gray-700 mb-2">宠物类型</label>
                <select v-model="form.petType" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                  <option value="">请选择宠物类型</option>
                  <option value="small-dog">小型犬</option>
                  <option value="medium-dog">中型犬</option>
                  <option value="large-dog">大型犬</option>
                  <option value="cat">猫咪</option>
                  <option value="other">其他</option>
                </select>
              </div>
            </div>
            
            <div>
              <label class="block text-gray-700 mb-2">训练类型</label>
              <select v-model="form.trainingType" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                <option value="">请选择训练类型</option>
                <option value="basic">基础训练</option>
                <option value="advanced">高级训练</option>
                <option value="behavior">行为纠正</option>
                <option value="socialization">社交化训练</option>
                <option value="private">一对一私人训练</option>
                <option value="group">团体训练</option>
              </select>
            </div>
            
            <div>
              <label class="block text-gray-700 mb-2">训练需求</label>
              <textarea v-model="form.trainingNeeds" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" rows="4" placeholder="请描述您的宠物训练需求，如具体行为问题、期望达到的效果等"></textarea>
            </div>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-gray-700 mb-2">期望开始日期</label>
                <input type="date" v-model="form.startDate" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
              </div>
              <div>
                <label class="block text-gray-700 mb-2">期望训练时间</label>
                <select v-model="form.trainingTime" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
                  <option value="">请选择训练时间</option>
                  <option value="morning">上午</option>
                  <option value="afternoon">下午</option>
                  <option value="evening">晚上</option>
                </select>
              </div>
            </div>
            
            <button type="submit" class="w-full bg-primary text-white py-3 rounded-lg font-medium hover:bg-opacity-90 transition">提交预约</button>
          </form>
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
              <li><router-link to="/service-grooming" class="text-gray-400 hover:text-primary transition">宠物美容</router-link></li>
              <li><router-link to="/service-health" class="text-gray-400 hover:text-primary transition">健康体检</router-link></li>
              <li><router-link to="/service-boarding" class="text-gray-400 hover:text-primary transition">宠物寄养</router-link></li>
              <li><router-link to="/service-food" class="text-gray-400 hover:text-primary transition">宠物饮食</router-link></li>
              <li><router-link to="/service-transport" class="text-gray-400 hover:text-primary transition">接送服务</router-link></li>
              <li><router-link to="/service-training" class="text-gray-400 hover:text-primary transition">宠物训练</router-link></li>
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
  name: 'ServiceTraining',
  data() {
    return {
      form: {
        ownerName: '',
        phone: '',
        petName: '',
        petBreed: '',
        petAge: '',
        petType: '',
        trainingType: '',
        trainingNeeds: '',
        startDate: '',
        trainingTime: ''
      }
    }
  },
  methods: {
    submitForm() {
      // 处理表单提交
      console.log('表单提交:', this.form);
      // 这里可以添加API调用逻辑
      alert('预约提交成功！我们的工作人员会尽快与您联系。');
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