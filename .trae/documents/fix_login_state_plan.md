# 修复多端登录状态冲突问题计划

## 问题分析

通过分析代码，发现三个端（用户端、商家端、平台端）在同一浏览器中登录状态互相覆盖的原因：

### 1. 根本原因
- **API请求拦截器**：`src/api/request.ts` 中token获取逻辑存在问题
- **响应拦截器**：401错误处理时只清除部分token
- **路径判断**：没有根据当前页面路径选择正确的token

### 2. 具体问题

**请求拦截器问题**：
```javascript
const token = localStorage.getItem('token') || sessionStorage.getItem('token') || localStorage.getItem('merchant_token') || sessionStorage.getItem('merchant_token')
```
- 只考虑了 `token` 和 `merchant_token`，没有考虑 `admin_token`
- 使用优先级顺序，导致不同端的token互相覆盖

**响应拦截器问题**：
- 只清除 `token` 和 `merchant_token`，没有清除 `admin_token`
- 跳转逻辑只考虑用户和商家，没有考虑管理员

## 解决方案

### 1. 修改API请求拦截器
- 根据当前页面路径选择正确的token
- 为每个端设置独立的token获取逻辑

### 2. 修改API响应拦截器
- 清除所有端的token
- 根据当前路径跳转到对应的登录页

### 3. 确保状态管理的独立性
- 验证三个端的状态管理文件（user.ts、merchant.ts、admin.ts）已使用不同的localStorage键

## 具体修改步骤

### 1. 修改 `src/api/request.ts`

**请求拦截器**：
```javascript
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    let token = ''
    const path = window.location.pathname
    
    if (path.startsWith('/merchant/')) {
      // 商家端
      token = localStorage.getItem('merchant_token') || sessionStorage.getItem('merchant_token')
    } else if (path.startsWith('/admin/')) {
      // 平台端
      token = localStorage.getItem('admin_token') || sessionStorage.getItem('admin_token')
    } else {
      // 用户端
      token = localStorage.getItem('user_token') || sessionStorage.getItem('user_token')
    }
    
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)
```

**响应拦截器**：
```javascript
request.interceptors.response.use(
  // 成功响应处理保持不变
  (response: AxiosResponse) => {
    // 现有代码
  },
  (error: AxiosError) => {
    if (error.response) {
      const status = error.response.status
      switch (status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          // 清除所有token
          localStorage.removeItem('user_token')
          localStorage.removeItem('merchant_token')
          localStorage.removeItem('admin_token')
          sessionStorage.removeItem('user_token')
          sessionStorage.removeItem('merchant_token')
          sessionStorage.removeItem('admin_token')
          // 根据当前路径跳转到对应的登录页
          const path = window.location.pathname
          if (path.startsWith('/merchant/')) {
            window.location.href = '/merchant/login'
          } else if (path.startsWith('/admin/')) {
            window.location.href = '/admin/login'
          } else {
            window.location.href = '/login'
          }
          break
        // 其他错误处理保持不变
      }
    }
    // 其他错误处理保持不变
  }
)
```

### 2. 验证状态管理文件

确保三个状态管理文件使用不同的localStorage键：
- **user.ts**：`user_token`
- **merchant.ts**：`merchant_token`
- **admin.ts**：`admin_token`

## 风险评估

### 低风险
- 修改仅涉及API请求/响应拦截器
- 不影响后端API实现
- 保持了三个端的独立性

### 注意事项
- 确保路径判断逻辑正确
- 确保所有token都被正确清除
- 确保跳转逻辑正确

## 预期结果

1. **用户端**：登录后使用 `user_token`，不会影响其他端
2. **商家端**：登录后使用 `merchant_token`，不会影响其他端
3. **平台端**：登录后使用 `admin_token`，不会影响其他端
4. **同一浏览器**：三个端可以同时登录，互不影响
5. **退出登录**：只清除对应端的token，不影响其他端
6. **401错误**：跳转到对应端的登录页，不影响其他端