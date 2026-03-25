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
          <a href="#home" class="text-dark hover:text-primary font-medium">首页</a>
          <a href="#services" class="text-dark hover:text-primary font-medium">服务</a>
          <a href="#pets" class="text-dark hover:text-primary font-medium">宠物展示</a>
          <a href="#about" class="text-dark hover:text-primary font-medium">关于我们</a>
          <a href="#contact" class="text-dark hover:text-primary font-medium">联系我们</a>
        </div>
        <div class="flex items-center space-x-4">
          <router-link to="/login" class="bg-primary text-white px-4 py-2 rounded-full hover:bg-opacity-90 transition">登录</router-link>
          <button class="md:hidden text-dark text-2xl">
            <i class="fa fa-bars"></i>
          </button>
        </div>
      </div>
    </nav>

    <!-- 英雄区域 -->
    <section id="home" class="pt-24 pb-16 bg-gradient-to-r from-primary to-primary/80 text-white">
      <div class="container mx-auto px-4 flex flex-col md:flex-row items-center">
        <div class="md:w-1/2 mb-8 md:mb-0">
          <h1 class="text-4xl md:text-5xl font-bold mb-4 text-shadow">您的宠物，我们的关爱</h1>
          <p class="text-lg mb-6">专业的宠物服务平台，为您的爱宠提供全方位的呵护与关怀</p>
          <div class="flex space-x-4">
            <button class="bg-white text-primary px-6 py-3 rounded-full font-medium hover:bg-opacity-90 transition">立即预约</button>
            <button class="border-2 border-white text-white px-6 py-3 rounded-full font-medium hover:bg-white hover:text-primary transition">了解更多</button>
          </div>
        </div>
        <div class="md:w-1/2">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20happy%20dog%20with%20owner%20in%20park%2C%20professional%20photography%2C%20warm%20lighting&image_size=landscape_16_9" alt="宠物与主人" class="rounded-lg shadow-xl w-full">
        </div>
      </div>
    </section>

    <!-- 服务区域 -->
    <section id="services" class="py-16 bg-white">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-dark mb-4">我们的服务</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">为您的爱宠提供专业、贴心的全方位服务</p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <!-- 服务卡片 1 -->
          <div class="bg-light rounded-lg p-6 shadow-md card-hover">
            <div class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mb-4">
              <i class="fa fa-scissors text-primary text-2xl"></i>
            </div>
            <h3 class="text-xl font-semibold mb-3">宠物美容</h3>
            <p class="text-gray-600 mb-4">专业的宠物美容服务，让您的爱宠焕然一新，保持健康整洁的形象。</p>
            <router-link to="/service/grooming" class="inline-block bg-primary text-white px-4 py-2 rounded hover:bg-opacity-90 transition">了解详情</router-link>
          </div>
          <!-- 服务卡片 2 -->
          <div class="bg-light rounded-lg p-6 shadow-md card-hover">
            <div class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mb-4">
              <i class="fa fa-stethoscope text-primary text-2xl"></i>
            </div>
            <h3 class="text-xl font-semibold mb-3">健康体检</h3>
            <p class="text-gray-600 mb-4">定期健康检查，及时发现潜在健康问题，为您的爱宠保驾护航。</p>
            <router-link to="/service/health" class="inline-block bg-primary text-white px-4 py-2 rounded hover:bg-opacity-90 transition">了解详情</router-link>
          </div>
          <!-- 服务卡片 3 -->
          <div class="bg-light rounded-lg p-6 shadow-md card-hover">
            <div class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mb-4">
              <i class="fa fa-home text-primary text-2xl"></i>
            </div>
            <h3 class="text-xl font-semibold mb-3">宠物寄养</h3>
            <p class="text-gray-600 mb-4">安全舒适的寄养环境，让您外出时无后顾之忧，爱宠得到悉心照料。</p>
            <router-link to="/service/boarding" class="inline-block bg-primary text-white px-4 py-2 rounded hover:bg-opacity-90 transition">了解详情</router-link>
          </div>
          <!-- 服务卡片 4 -->
          <div class="bg-light rounded-lg p-6 shadow-md card-hover">
            <div class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mb-4">
              <i class="fa fa-cutlery text-primary text-2xl"></i>
            </div>
            <h3 class="text-xl font-semibold mb-3">宠物饮食</h3>
            <p class="text-gray-600 mb-4">营养均衡的宠物食品推荐与配送，满足不同年龄段宠物的饮食需求。</p>
            <router-link to="/service/food" class="inline-block bg-primary text-white px-4 py-2 rounded hover:bg-opacity-90 transition">了解详情</router-link>
          </div>
          <!-- 服务卡片 5 -->
          <div class="bg-light rounded-lg p-6 shadow-md card-hover">
            <div class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mb-4">
              <i class="fa fa-car text-primary text-2xl"></i>
            </div>
            <h3 class="text-xl font-semibold mb-3">接送服务</h3>
            <p class="text-gray-600 mb-4">便捷的宠物接送服务，为您节省时间，让爱宠安全抵达目的地。</p>
            <router-link to="/service/transport" class="inline-block bg-primary text-white px-4 py-2 rounded hover:bg-opacity-90 transition">了解详情</router-link>
          </div>
          <!-- 服务卡片 6 -->
          <div class="bg-light rounded-lg p-6 shadow-md card-hover">
            <div class="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mb-4">
              <i class="fa fa-book text-primary text-2xl"></i>
            </div>
            <h3 class="text-xl font-semibold mb-3">宠物训练</h3>
            <p class="text-gray-600 mb-4">专业的宠物训练课程，帮助您的爱宠养成良好习惯，增强互动能力。</p>
            <router-link to="/service/training" class="inline-block bg-primary text-white px-4 py-2 rounded hover:bg-opacity-90 transition">了解详情</router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- 宠物展示区域 -->
    <section id="pets" class="py-16 bg-light">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-dark mb-4">可爱宠物展示</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">这些可爱的小家伙正在寻找温暖的家</p>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <!-- 宠物卡片 1 -->
          <div class="bg-white rounded-lg overflow-hidden shadow-md card-hover">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20golden%20retriever%20puppy%2C%20friendly%20expression%2C%20professional%20pet%20photography&image_size=square" alt="金毛犬" class="w-full h-48 object-cover">
            <div class="p-4">
              <h3 class="text-lg font-semibold mb-2">小金</h3>
              <p class="text-gray-600 text-sm mb-3">金毛犬 | 3个月</p>
              <button class="w-full bg-primary text-white py-2 rounded hover:bg-opacity-90 transition">了解详情</button>
            </div>
          </div>
          <!-- 宠物卡片 2 -->
          <div class="bg-white rounded-lg overflow-hidden shadow-md card-hover">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20persian%20cat%20with%20blue%20eyes%2C%20fluffy%20fur%2C%20professional%20pet%20photography&image_size=square" alt="波斯猫" class="w-full h-48 object-cover">
            <div class="p-4">
              <h3 class="text-lg font-semibold mb-2">咪咪</h3>
              <p class="text-gray-600 text-sm mb-3">波斯猫 | 6个月</p>
              <button class="w-full bg-primary text-white py-2 rounded hover:bg-opacity-90 transition">了解详情</button>
            </div>
          </div>
          <!-- 宠物卡片 3 -->
          <div class="bg-white rounded-lg overflow-hidden shadow-md card-hover">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=small%20cute%20pomeranian%20dog%2C%20happy%20expression%2C%20professional%20pet%20photography&image_size=square" alt="博美犬" class="w-full h-48 object-cover">
            <div class="p-4">
              <h3 class="text-lg font-semibold mb-2">球球</h3>
              <p class="text-gray-600 text-sm mb-3">博美犬 | 1岁</p>
              <button class="w-full bg-primary text-white py-2 rounded hover:bg-opacity-90 transition">了解详情</button>
            </div>
          </div>
          <!-- 宠物卡片 4 -->
          <div class="bg-white rounded-lg overflow-hidden shadow-md card-hover">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20british%20shorthair%20cat%2C%20gray%20fur%2C%20professional%20pet%20photography&image_size=square" alt="英短猫" class="w-full h-48 object-cover">
            <div class="p-4">
              <h3 class="text-lg font-semibold mb-2">灰灰</h3>
              <p class="text-gray-600 text-sm mb-3">英短猫 | 8个月</p>
              <button class="w-full bg-primary text-white py-2 rounded hover:bg-opacity-90 transition">了解详情</button>
            </div>
          </div>
        </div>
        <div class="text-center mt-8">
          <button class="bg-white border-2 border-primary text-primary px-6 py-3 rounded-full font-medium hover:bg-primary hover:text-white transition">查看更多宠物</button>
        </div>
      </div>
    </section>

    <!-- 关于我们区域 -->
    <section id="about" class="py-16 bg-white">
      <div class="container mx-auto px-4 flex flex-col md:flex-row items-center">
        <div class="md:w-1/2 mb-8 md:mb-0">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20pet%20care%20center%2C%20clean%20modern%20facility%2C%20staff%20caring%20for%20pets&image_size=landscape_16_9" alt="宠物护理中心" class="rounded-lg shadow-xl w-full">
        </div>
        <div class="md:w-1/2 md:pl-12">
          <h2 class="text-3xl font-bold text-dark mb-4">关于我们</h2>
          <p class="text-gray-600 mb-4">宠物家园成立于2018年，是一家专业的宠物服务平台，致力于为宠物和宠物主人提供全方位的服务。</p>
          <p class="text-gray-600 mb-6">我们拥有一支经验丰富、爱心满满的专业团队，包括宠物美容师、兽医、训练师等，为您的爱宠提供最优质的服务。</p>
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div class="flex items-center">
              <i class="fa fa-check-circle text-primary mr-2"></i>
              <span>专业团队</span>
            </div>
            <div class="flex items-center">
              <i class="fa fa-check-circle text-primary mr-2"></i>
              <span>优质服务</span>
            </div>
            <div class="flex items-center">
              <i class="fa fa-check-circle text-primary mr-2"></i>
              <span>舒适环境</span>
            </div>
            <div class="flex items-center">
              <i class="fa fa-check-circle text-primary mr-2"></i>
              <span>贴心关怀</span>
            </div>
          </div>
          <button class="bg-primary text-white px-6 py-3 rounded-full font-medium hover:bg-opacity-90 transition">了解更多</button>
        </div>
      </div>
    </section>

    <!-- 客户评价区域 -->
    <section class="py-16 bg-light">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-dark mb-4">客户评价</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">听听我们的客户怎么说</p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <!-- 评价卡片 1 -->
          <div class="bg-white rounded-lg p-6 shadow-md">
            <div class="flex items-center mb-4">
              <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=friendly%20asian%20woman%20portrait%2C%20professional%20headshot&image_size=square" alt="客户头像" class="w-12 h-12 rounded-full mr-4">
              <div>
                <h4 class="font-semibold">李女士</h4>
                <div class="text-yellow-400">
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star"></i>
                </div>
              </div>
            </div>
            <p class="text-gray-600">"宠物家园的服务非常专业，我的金毛犬在这里做美容后变得特别精神，工作人员也很有耐心，会继续选择这里的服务。"</p>
          </div>
          <!-- 评价卡片 2 -->
          <div class="bg-white rounded-lg p-6 shadow-md">
            <div class="flex items-center mb-4">
              <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=friendly%20asian%20man%20portrait%2C%20professional%20headshot&image_size=square" alt="客户头像" class="w-12 h-12 rounded-full mr-4">
              <div>
                <h4 class="font-semibold">张先生</h4>
                <div class="text-yellow-400">
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star-half-o"></i>
                </div>
              </div>
            </div>
            <p class="text-gray-600">"寄养服务非常贴心，我出差期间把猫咪放在这里，工作人员每天都会给我发猫咪的照片和视频，让我很放心。"</p>
          </div>
          <!-- 评价卡片 3 -->
          <div class="bg-white rounded-lg p-6 shadow-md">
            <div class="flex items-center mb-4">
              <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=friendly%20young%20woman%20portrait%2C%20professional%20headshot&image_size=square" alt="客户头像" class="w-12 h-12 rounded-full mr-4">
              <div>
                <h4 class="font-semibold">王女士</h4>
                <div class="text-yellow-400">
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star"></i>
                  <i class="fa fa-star"></i>
                </div>
              </div>
            </div>
            <p class="text-gray-600">"宠物训练课程效果很好，我的狗狗现在变得很听话，训练师很专业，会根据狗狗的情况制定个性化的训练计划。"</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 联系我们区域 -->
    <section id="contact" class="py-16 bg-white">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-dark mb-4">联系我们</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">如有任何问题或需求，欢迎随时联系我们</p>
        </div>
        <div class="flex flex-col md:flex-row">
          <div class="md:w-1/2 mb-8 md:mb-0 md:pr-8">
            <form class="space-y-4">
              <div>
                <label class="block text-gray-700 mb-2">姓名</label>
                <input type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
              </div>
              <div>
                <label class="block text-gray-700 mb-2">电话</label>
                <input type="tel" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
              </div>
              <div>
                <label class="block text-gray-700 mb-2">邮箱</label>
                <input type="email" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary">
              </div>
              <div>
                <label class="block text-gray-700 mb-2">留言</label>
                <textarea class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" rows="4"></textarea>
              </div>
              <button type="submit" class="w-full bg-primary text-white py-3 rounded-lg font-medium hover:bg-opacity-90 transition">提交</button>
            </form>
          </div>
          <div class="md:w-1/2">
            <div class="bg-light rounded-lg p-6 h-full">
              <h3 class="text-xl font-semibold mb-6">联系方式</h3>
              <div class="space-y-4">
                <div class="flex items-start">
                  <i class="fa fa-map-marker text-primary text-xl mt-1 mr-4"></i>
                  <div>
                    <h4 class="font-medium mb-1">地址</h4>
                    <p class="text-gray-600">北京市朝阳区建国路88号宠物家园</p>
                  </div>
                </div>
                <div class="flex items-start">
                  <i class="fa fa-phone text-primary text-xl mt-1 mr-4"></i>
                  <div>
                    <h4 class="font-medium mb-1">电话</h4>
                    <p class="text-gray-600">010-12345678</p>
                  </div>
                </div>
                <div class="flex items-start">
                  <i class="fa fa-envelope text-primary text-xl mt-1 mr-4"></i>
                  <div>
                    <h4 class="font-medium mb-1">邮箱</h4>
                    <p class="text-gray-600">info@pet-home.com</p>
                  </div>
                </div>
                <div class="flex items-start">
                  <i class="fa fa-clock-o text-primary text-xl mt-1 mr-4"></i>
                  <div>
                    <h4 class="font-medium mb-1">营业时间</h4>
                    <p class="text-gray-600">周一至周日 9:00-20:00</p>
                  </div>
                </div>
              </div>
              <div class="mt-8">
                <h4 class="font-medium mb-4">关注我们</h4>
                <div class="flex space-x-4">
                  <a href="#" class="w-10 h-10 bg-primary text-white rounded-full flex items-center justify-center hover:bg-opacity-90 transition">
                    <i class="fa fa-weixin"></i>
                  </a>
                  <a href="#" class="w-10 h-10 bg-primary text-white rounded-full flex items-center justify-center hover:bg-opacity-90 transition">
                    <i class="fa fa-weibo"></i>
                  </a>
                  <a href="#" class="w-10 h-10 bg-primary text-white rounded-full flex items-center justify-center hover:bg-opacity-90 transition">
                    <i class="fa fa-instagram"></i>
                  </a>
                  <a href="#" class="w-10 h-10 bg-primary text-white rounded-full flex items-center justify-center hover:bg-opacity-90 transition">
                    <i class="fa fa-facebook"></i>
                  </a>
                </div>
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
              <li><a href="#home" class="text-gray-400 hover:text-primary transition">首页</a></li>
              <li><a href="#services" class="text-gray-400 hover:text-primary transition">服务</a></li>
              <li><a href="#pets" class="text-gray-400 hover:text-primary transition">宠物展示</a></li>
              <li><a href="#about" class="text-gray-400 hover:text-primary transition">关于我们</a></li>
              <li><a href="#contact" class="text-gray-400 hover:text-primary transition">联系我们</a></li>
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
  name: 'Home',
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
    
    // 平滑滚动
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
      anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
          target.scrollIntoView({ behavior: 'smooth' });
        }
      });
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