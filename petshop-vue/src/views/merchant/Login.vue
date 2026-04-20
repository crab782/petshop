<script>
// 商家端登录页面 - 使用纯JavaScript实现

// DOM元素
let loginForm, usernameInput, passwordInput, loginButton, buttonText, loadingText, errorMessage;

// 页面加载完成后初始化
window.addEventListener('DOMContentLoaded', function() {
  // 获取DOM元素
  loginForm = document.getElementById('loginForm');
  usernameInput = document.getElementById('username');
  passwordInput = document.getElementById('password');
  loginButton = document.getElementById('loginButton');
  buttonText = document.getElementById('buttonText');
  loadingText = document.getElementById('loadingText');
  errorMessage = document.getElementById('errorMessage');
  
  // 添加表单提交事件
  if (loginForm) {
    loginForm.addEventListener('submit', handleLogin);
  }
  
  // 添加回车键提交
  passwordInput.addEventListener('keyup', function(event) {
    if (event.key === 'Enter') {
      handleLogin(event);
    }
  });
});

// 登录处理
function handleLogin(event) {
  event.preventDefault();
  
  // 表单验证
  if (!validateForm()) {
    return;
  }
  
  // 显示加载状态
  showLoading(true);
  clearError();
  
  // 获取表单数据
  const username = usernameInput.value.trim();
  const password = passwordInput.value;
  
  // 模拟登录请求（实际项目中应使用真实的API调用）
  setTimeout(function() {
    // 模拟登录成功
    if (username && password) {
      // 登录成功，跳转到商家端首页
      window.location.href = '/merchant/home';
    } else {
      // 登录失败
      showError('请输入用户名和密码');
    }
    showLoading(false);
  }, 1000);
}

// 表单验证
function validateForm() {
  const username = usernameInput.value.trim();
  const password = passwordInput.value;
  
  if (!username) {
    showError('请输入用户名或邮箱');
    usernameInput.focus();
    return false;
  }
  
  if (!password) {
    showError('请输入密码');
    passwordInput.focus();
    return false;
  }
  
  if (password.length < 6) {
    showError('密码长度至少6位');
    passwordInput.focus();
    return false;
  }
  
  return true;
}

// 显示加载状态
function showLoading(show) {
  if (show) {
    buttonText.style.display = 'none';
    loadingText.style.display = 'inline';
    loginButton.disabled = true;
    loginButton.classList.add('loading');
  } else {
    buttonText.style.display = 'inline';
    loadingText.style.display = 'none';
    loginButton.disabled = false;
    loginButton.classList.remove('loading');
  }
}

// 显示错误信息
function showError(message) {
  errorMessage.textContent = message;
  errorMessage.style.display = 'block';
  
  // 3秒后自动清除错误
  setTimeout(clearError, 3000);
}

// 清除错误信息
function clearError() {
  errorMessage.textContent = '';
  errorMessage.style.display = 'none';
}
</script>

<template>
  <!-- 商家端登录页面 -->
  <div class="merchant-login-container">
    <div class="login-card">
      <!-- 登录头部 -->
      <div class="login-header">
        <div class="brand-logo">
          <div class="logo-icon">🐶</div>
          <h1 class="brand-title">宠物服务平台</h1>
        </div>
        <h2 class="login-title">商家端登录</h2>
        <p class="login-subtitle">欢迎回来，商家朋友</p>
      </div>
      
      <!-- 登录表单 -->
      <form id="loginForm" class="login-form">
        <!-- 用户名/邮箱输入 -->
        <div class="form-group">
          <label for="username" class="form-label">邮箱 / 用户名</label>
          <div class="input-container">
            <span class="input-icon">📧</span>
            <input 
              type="text" 
              id="username" 
              class="form-input"
              placeholder="请输入邮箱或用户名"
              autocomplete="username"
            >
          </div>
        </div>
        
        <!-- 密码输入 -->
        <div class="form-group">
          <label for="password" class="form-label">密码</label>
          <div class="input-container">
            <span class="input-icon">🔒</span>
            <input 
              type="password" 
              id="password" 
              class="form-input"
              placeholder="请输入密码"
              autocomplete="current-password"
            >
          </div>
        </div>
        
        <!-- 登录按钮 -->
        <button type="submit" id="loginButton" class="login-button">
          <span id="buttonText">登录</span>
          <span id="loadingText" style="display: none;">登录中...</span>
        </button>
        
        <!-- 底部链接 -->
        <div class="login-footer">
          <span class="footer-text">还没有账号？</span>
          <a href="/merchant/register" class="register-link">立即注册</a>
        </div>
      </form>
      
      <!-- 错误提示 -->
      <div id="errorMessage" class="error-message"></div>
      
      <!-- 商家端特色 -->
      <div class="merchant-features">
        <div class="feature-item">
          <span class="feature-icon">📊</span>
          <span class="feature-text">数据分析</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">🛠️</span>
          <span class="feature-text">服务管理</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">💳</span>
          <span class="feature-text">订单处理</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
  color: #333;
  line-height: 1.6;
}

/* 登录容器 */
.merchant-login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

/* 登录卡片 */
.login-card {
  width: 100%;
  max-width: 450px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  position: relative;
  overflow: hidden;
}

/* 卡片装饰 */
.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #4CAF50, #45a049);
}

/* 登录头部 */
.login-header {
  text-align: center;
  margin-bottom: 30px;
}

/* 品牌标识 */
.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.logo-icon {
  font-size: 36px;
  margin-right: 10px;
}

.brand-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

/* 登录标题 */
.login-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

/* 登录表单 */
.login-form {
  margin-bottom: 20px;
}

/* 表单组 */
.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #555;
  margin-bottom: 8px;
}

/* 输入容器 */
.input-container {
  position: relative;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.input-container:focus-within {
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

/* 输入图标 */
.input-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  font-size: 16px;
}

/* 表单输入 */
.form-input {
  width: 100%;
  padding: 12px 15px 12px 45px;
  border: none;
  outline: none;
  font-size: 14px;
  color: #333;
  background: transparent;
}

.form-input::placeholder {
  color: #999;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(90deg, #4CAF50, #45a049);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
  position: relative;
  overflow: hidden;
}

.login-button:hover:not(:disabled) {
  background: linear-gradient(90deg, #45a049, #3d8b40);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.login-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.login-button.loading {
  background: #4CAF50;
  opacity: 0.8;
}

/* 底部链接 */
.login-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  gap: 8px;
}

.footer-text {
  font-size: 14px;
  color: #666;
}

.register-link {
  font-size: 14px;
  color: #4CAF50;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.register-link:hover {
  color: #45a049;
  text-decoration: underline;
}

/* 错误提示 */
.error-message {
  background: #fee;
  border: 1px solid #fcc;
  color: #c33;
  padding: 10px 15px;
  border-radius: 6px;
  margin-top: 15px;
  font-size: 14px;
  display: none;
  animation: fadeIn 0.3s ease;
}

/* 商家端特色 */
.merchant-features {
  display: flex;
  justify-content: space-around;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.feature-icon {
  font-size: 20px;
}

.feature-text {
  font-size: 12px;
  color: #666;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
  }
  
  .login-title {
    font-size: 20px;
  }
  
  .brand-title {
    font-size: 18px;
  }
  
  .form-input {
    padding: 10px 12px 10px 40px;
  }
  
  .login-button {
    padding: 12px;
    font-size: 14px;
  }
}

/* 加载动画 */
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.login-button.loading::after {
  content: '';
  position: absolute;
  right: 15px;
  top: 50%;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  transform: translateY(-50%);
}

/* 隐藏Vue DevTools面板 */
.vue-devtools__panel {
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
  pointer-events: none !important;
  z-index: -1 !important;
}

/* 隐藏Vue DevTools锚点按钮 */
.vue-devtools__anchor-btn {
  display: none !important;
}

/* 隐藏Vue DevTools检查器容器 */
#inspector-container {
  display: none !important;
}

/* 隐藏Vue DevTools相关元素 */
.vue-devtools__inspector-button {
  display: none !important;
}

/* 隐藏Vue DevTools面板内容 */
.vue-devtools__panel-content {
  display: none !important;
}
</style>