# 移除前端登录验证计划

## 任务目标
移除前端所有页面的登录验证，方便直接通过链接进入检查页面效果。

## 修改文件
- `d:\j\cg\cg\cg-vue\src\router\index.ts`

## 修改内容

### 第367-393行的路由守卫代码修改

**当前代码：**
```typescript
// 路由守卫
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const role = to.matched[to.matched.length - 1]?.meta.role;

  // 模拟登录状态和用户角色
  // 实际项目中，应该从localStorage或其他存储中获取
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
  const userRole = localStorage.getItem('userRole') || 'user';

  if (requiresAuth && !isLoggedIn) {
    // 未登录用户重定向到登录页面
    next('/login');
  } else if (requiresAuth && role && role !== userRole) {
    // 角色不匹配，重定向到对应角色的首页
    if (userRole === 'admin') {
      next('/admin/dashboard');
    } else if (userRole === 'merchant') {
      next('/merchant/home');
    } else {
      next('/user/home');
    }
  } else {
    // 正常访问
    next();
  }
});
```

**修改后代码：**
```typescript
// 路由守卫（已禁用登录验证，仅用于调试）
router.beforeEach((to, from, next) => {
  // 临时禁用所有登录验证，方便直接访问页面进行测试
  next();
});
```

## 修改步骤
1. 打开 `d:\j\cg\cg\cg-vue\src\router\index.ts`
2. 找到第367-393行的路由守卫代码
3. 使用 SearchReplace 工具将整个路由守卫替换为简化版本
4. 保存文件

## 验证方法
- 重启开发服务器（或热更新生效）
- 直接访问任意URL，如 http://localhost:5174/user/home
- 不再跳转到登录页面

## 回滚方法（如需恢复）
将简化版路由守卫恢复为原始完整版本即可。