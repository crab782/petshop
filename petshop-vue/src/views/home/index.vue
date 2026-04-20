<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getServices, getProducts, type Service, type Product } from '@/api/user'
import {
  Scissor,
  HomeFilled,
  DocumentChecked,
  Food,
  Train,
  Van,
  Search,
  StarFilled,
  ShoppingCart,
  User,
  ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()

const searchKeyword = ref('')

const quickServices = [
  { title: '美容', icon: Scissor, color: '#409eff', route: '/user/services', query: { category: 'beauty' } },
  { title: '寄养', icon: HomeFilled, color: '#67c23a', route: '/user/services', query: { category: 'boarding' } },
  { title: '体检', icon: DocumentChecked, color: '#e6a23c', route: '/user/services', query: { category: 'health' } },
  { title: '饮食', icon: Food, color: '#f56c6c', route: '/user/services', query: { category: 'food' } },
  { title: '训练', icon: Train, color: '#909399', route: '/user/services', query: { category: 'training' } },
  { title: '接送', icon: Van, color: '#66b1ff', route: '/user/services', query: { category: 'transport' } }
]

const bannerImages = [
  { src: 'https://picsum.photos/1200/400?random=1', title: '专业宠物美容' },
  { src: 'https://picsum.photos/1200/400?random=2', title: '温馨寄养服务' },
  { src: 'https://picsum.photos/1200/400?random=3', title: '全面健康体检' },
  { src: 'https://picsum.photos/1200/400?random=4', title: '优质宠物食品' },
  { src: 'https://picsum.photos/1200/400?random=5', title: '专业训练课程' }
]

const recommendedServices = ref<Service[]>([])
const recommendedProducts = ref<Product[]>([])
const loadingServices = ref(false)
const loadingProducts = ref(false)

const fetchServices = async () => {
  loadingServices.value = true
  try {
    const res = await getServices()
    recommendedServices.value = (res.data || []).slice(0, 4)
  } catch (error) {
    console.error('获取服务失败:', error)
  } finally {
    loadingServices.value = false
  }
}

const fetchProducts = async () => {
  loadingProducts.value = true
  try {
    const res = await getProducts({ pageSize: 8 })
    recommendedProducts.value = (res.data || []).slice(0, 4)
  } catch (error) {
    console.error('获取商品失败:', error)
  } finally {
    loadingProducts.value = false
  }
}

const goToQuickService = (service: typeof quickServices[0]) => {
  router.push({ path: service.route, query: service.query })
}

const goToServiceDetail = (service: Service) => {
  router.push(`/user/services?id=${service.id}`)
}

const goToProductDetail = (product: Product) => {
  router.push(`/user/shop?id=${product.id}`)
}

const goToSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/user/services', query: { keyword: searchKeyword.value } })
  }
}

const goToLogin = () => {
  router.push('/login')
}

onMounted(() => {
  fetchServices()
  fetchProducts()
})
</script>

<template>
  <div class="home-page">
    <header class="header">
      <div class="header-content">
        <div class="logo-area">
          <el-icon :size="32" color="#409eff"><User /></el-icon>
          <span class="logo-text">宠物服务平台</span>
        </div>

        <nav class="nav-links">
          <el-link href="/" class="nav-link">首页</el-link>
          <el-link href="/user/services" class="nav-link">服务</el-link>
          <el-link href="/user/shop" class="nav-link">宠物商店</el-link>
          <el-link href="/about" class="nav-link">关于我们</el-link>
        </nav>

        <div class="search-area">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索服务或商品"
            class="search-input"
            @keyup.enter="goToSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="goToSearch" />
            </template>
          </el-input>
        </div>

        <div class="user-area">
          <el-button type="primary" @click="goToLogin">
            <el-icon><User /></el-icon>
            登录
          </el-button>
        </div>
      </div>
    </header>

    <section class="banner-section">
      <el-carousel :interval="4000" type="card" height="320px" indicator-position="outside">
        <el-carousel-item v-for="(banner, index) in bannerImages" :key="index">
          <div class="banner-item">
            <img :src="banner.src" :alt="banner.title" class="banner-image" />
            <div class="banner-overlay">
              <h3 class="banner-title">{{ banner.title }}</h3>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <section class="quick-services-section">
      <h2 class="section-title">快捷服务</h2>
      <el-row :gutter="24" class="quick-services">
        <el-col v-for="service in quickServices" :key="service.title" :span="4">
          <div class="service-item" @click="goToQuickService(service)">
            <div class="service-icon" :style="{ backgroundColor: service.color }">
              <el-icon :size="28"><component :is="service.icon" /></el-icon>
            </div>
            <span class="service-title">{{ service.title }}</span>
          </div>
        </el-col>
      </el-row>
    </section>

    <section class="recommended-section">
      <div class="section-header">
        <h2 class="section-title">推荐服务</h2>
        <el-link href="/user/services" class="more-link">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </el-link>
      </div>
      <el-row :gutter="16" v-loading="loadingServices">
        <el-col v-for="service in recommendedServices" :key="service.id" :span="6">
          <el-card class="service-card" shadow="hover" @click="goToServiceDetail(service)">
            <div class="card-header">
              <el-icon :size="24" color="#409eff"><Scissor /></el-icon>
            </div>
            <h3 class="service-name">{{ service.name }}</h3>
            <p class="service-merchant">{{ service.merchantName }}</p>
            <div class="service-footer">
              <span class="service-price">¥{{ service.price }}</span>
              <span class="service-duration" v-if="service.duration">{{ service.duration }}分钟</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </section>

    <section class="recommended-section">
      <div class="section-header">
        <h2 class="section-title">热门商品</h2>
        <el-link href="/user/shop" class="more-link">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </el-link>
      </div>
      <el-row :gutter="16" v-loading="loadingProducts">
        <el-col v-for="product in recommendedProducts" :key="product.id" :span="6">
          <el-card class="product-card" shadow="hover" @click="goToProductDetail(product)">
            <div class="product-image">
              <img v-if="product.image" :src="product.image" :alt="product.name" />
              <el-icon v-else :size="48" color="#909399"><ShoppingCart /></el-icon>
            </div>
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-desc" v-if="product.description">{{ product.description }}</p>
            <div class="product-footer">
              <span class="product-price">¥{{ product.price }}</span>
              <span class="product-stock" v-if="product.stock > 0">库存: {{ product.stock }}</span>
              <span class="product-stock out-of-stock" v-else>缺货</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </section>

    <footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h4 class="footer-title">关于我们</h4>
          <p class="footer-text">宠物服务平台致力于为您的爱宠提供专业、贴心的服务</p>
        </div>
        <div class="footer-section">
          <h4 class="footer-title">联系方式</h4>
          <p class="footer-text">电话: 400-123-4567</p>
          <p class="footer-text">邮箱: info@petplatform.com</p>
        </div>
        <div class="footer-section">
          <h4 class="footer-title">服务时间</h4>
          <p class="footer-text">周一至周日: 9:00 - 21:00</p>
        </div>
        <div class="footer-section">
          <h4 class="footer-title">关注我们</h4>
          <p class="footer-text">微信公众号: 宠物服务平台</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© 2024 宠物服务平台 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.nav-links {
  display: flex;
  gap: 24px;
  flex-shrink: 0;
}

.nav-link {
  font-size: 15px;
  color: #606266;
  text-decoration: none;
}

.nav-link:hover {
  color: #409eff;
}

.search-area {
  flex: 1;
  max-width: 320px;
}

.search-input {
  width: 100%;
}

.user-area {
  flex-shrink: 0;
}

.banner-section {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 24px;
}

.banner-item {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
}

.banner-title {
  color: #fff;
  font-size: 20px;
  margin: 0;
}

.quick-services-section {
  max-width: 1200px;
  margin: 0 auto 32px;
  padding: 0 24px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 20px 0;
}

.quick-services {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.service-item:hover {
  transform: translateY(-4px);
}

.service-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.service-title {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.recommended-section {
  max-width: 1200px;
  margin: 0 auto 32px;
  padding: 0 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header .section-title {
  margin: 0;
}

.more-link {
  color: #909399;
  font-size: 14px;
}

.service-card,
.product-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-bottom: 16px;
}

.service-card:hover,
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: center;
  padding: 16px 0;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 12px;
}

.service-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 8px 0;
  text-align: center;
}

.service-merchant {
  font-size: 12px;
  color: #909399;
  margin: 0 0 12px 0;
  text-align: center;
}

.service-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.service-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.service-duration {
  font-size: 12px;
  color: #909399;
}

.product-image {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: #909399;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}

.product-stock {
  font-size: 12px;
  color: #67c23a;
}

.product-stock.out-of-stock {
  color: #f56c6c;
}

.footer {
  background-color: #304156;
  color: #bfcbd9;
  margin-top: 48px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 24px 32px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-title {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  margin: 0 0 8px 0;
}

.footer-text {
  font-size: 14px;
  margin: 0;
  line-height: 1.6;
}

.footer-bottom {
  text-align: center;
  padding: 16px 24px;
  border-top: 1px solid #3d4a5c;
  font-size: 12px;
}

.footer-bottom p {
  margin: 0;
}
</style>
