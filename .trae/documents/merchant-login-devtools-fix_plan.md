# 商家端登录页面Vue DevTools元素隐藏计划

## 问题分析

**问题现象**：访问 http://localhost:5174/merchant/login 时，页面底部出现了一个Vue DevTools的悬浮面板元素。

**根本原因**：
1. 这不是Vue欢迎页的残留元素
2. 这是浏览器安装的Vue DevTools扩展添加的悬浮面板
3. 扩展会在所有Vue应用页面上添加这个元素

**元素特征**：
- 类名：`vue-devtools__panel`
- 包含Vue DevTools的图标和功能按钮
- 由浏览器扩展动态添加到页面

## 解决方案

### 目标
- 隐藏Vue DevTools扩展添加的悬浮面板元素
- 确保商家端登录页面干净整洁
- 不影响其他页面的Vue DevTools功能

### 实施步骤

#### 步骤1：在商家登录页面添加CSS隐藏规则
- 在 `src/views/merchant/Login.vue` 的样式部分添加CSS规则
- 针对 `vue-devtools__panel` 类添加隐藏样式
- 使用 `display: none !important` 确保优先级

#### 步骤2：验证隐藏效果
- 访问 http://localhost:5174/merchant/login
- 验证Vue DevTools面板元素已隐藏
- 验证登录页面其他功能正常

#### 步骤3：测试其他页面
- 确保其他页面的Vue DevTools功能不受影响
- 验证只在商家登录页面隐藏该元素

## 技术实现

### CSS隐藏规则
```css
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
```

### 实现位置
在 `src/views/merchant/Login.vue` 文件的 `<style>` 标签中添加上述CSS规则。

## 预期结果

- ✅ 商家端登录页面不再显示Vue DevTools悬浮面板
- ✅ 登录页面其他功能正常
- ✅ 其他页面的Vue DevTools功能不受影响
- ✅ 页面干净整洁，无多余元素
