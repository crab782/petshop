# 商家端登录页面修复计划

## 问题分析

**问题现象**：访问 http://localhost:5174/merchant/login 时显示Vue欢迎页的残留元素，而不是商家端登录页面。

**根本原因**：
1. `src/router/index.ts` 中不存在 `/merchant/login` 路由
2. 访问不存在的路由时可能显示默认的Vue欢迎页
3. 当前只有通用登录页面 `/login`，没有商家端专门的登录页面

## 解决方案

### 目标
- 移除Vue欢迎页的残留元素
- 创建专门的商家端登录页面
- 直接通过HTML/CSS/JavaScript实现，不使用视图组件渲染
- 确保页面加载后立即展示正确的登录界面

### 实施步骤

#### 步骤1：添加商家端登录路由
- 在 `src/router/index.ts` 中添加 `/merchant/login` 路由
- 指向新创建的商家端登录页面

#### 步骤2：创建商家端登录页面
- 在 `src/views/merchant/` 目录下创建 `Login.vue` 文件
- 使用HTML/CSS/JavaScript直接实现，不使用组件渲染
- 包含：
  - 商家端品牌标识
  - 登录表单（邮箱/用户名、密码）
  - 登录按钮
  - 注册链接
  - 加载状态
  - 错误提示

#### 步骤3：移除Vue欢迎页元素
- 确保新页面不包含任何Vue默认欢迎页的元素
- 使用纯HTML/CSS/JavaScript实现
- 确保页面加载后立即显示登录界面

#### 步骤4：测试验证
- 访问 http://localhost:5174/merchant/login
- 验证页面显示正确的商家端登录界面
- 验证登录功能正常
- 验证无Vue欢迎页残留元素

## 技术实现

### 页面结构
```html
<div class="merchant-login-container">
  <div class="login-card">
    <div class="login-header">
      <h1>商家端登录</h1>
      <p>欢迎回来，商家朋友</p>
    </div>
    <form id="loginForm">
      <div class="form-group">
        <label>邮箱 / 用户名</label>
        <input type="text" id="username" required>
      </div>
      <div class="form-group">
        <label>密码</label>
        <input type="password" id="password" required>
      </div>
      <button type="submit" id="loginButton">
        <span id="buttonText">登录</span>
        <span id="loadingText" style="display: none;">登录中...</span>
      </button>
      <div class="login-footer">
        <a href="/merchant/register">立即注册</a>
      </div>
    </form>
    <div id="errorMessage" class="error-message"></div>
  </div>
</div>
```

### 样式
- 使用CSS实现响应式布局
- 商家端品牌风格（绿色主题）
- 现代、简洁的UI设计

### JavaScript
- 表单验证
- 登录请求处理
- 加载状态管理
- 错误提示
- 成功后跳转

## 预期结果

- ✅ 访问 http://localhost:5174/merchant/login 显示商家端登录页面
- ✅ 页面无Vue欢迎页残留元素
- ✅ 登录功能正常
- ✅ 响应式设计，适配不同设备
- ✅ 加载状态和错误提示正常
