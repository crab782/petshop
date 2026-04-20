# 修复 Page 类型混用问题 - 验证清单

## Service 层修复

### ServiceService
- [ ] `findAll(Pageable pageable)` 方法返回类型改为 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
- [ ] 方法体内无 Spring Data Page 类型转换代码
- [ ] 方法实现正确，使用 MyBatis-Plus 的 Page
- [ ] 分页功能正常工作

### MerchantService
- [ ] `findByStatus(String status, Pageable pageable)` 方法返回类型改为 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
- [ ] 方法体内无 Spring Data Page 类型转换代码
- [ ] 方法实现正确，使用 MyBatis-Plus 的 Page
- [ ] 分页功能正常工作

### UserService
- [ ] `findAll(Pageable pageable)` 方法返回类型改为 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
- [ ] 方法体内无 Spring Data Page 类型转换代码
- [ ] 方法实现正确，使用 MyBatis-Plus 的 Page
- [ ] 分页功能正常工作

### ReviewService
- [ ] `findByUserIdWithPaging` 方法返回类型改为 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
- [ ] `findByUserIdWithPagingAndRating` 方法返回类型改为 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`
- [ ] 方法体内无 Spring Data Page 类型转换代码
- [ ] 方法实现正确，使用 MyBatis-Plus 的 Page
- [ ] 分页功能正常工作

## Controller 层修复

### 查找并修改调用的 Controller
- [ ] 查找所有调用修改后 Service 方法的 Controller
- [ ] 修改 Controller 代码，适应新的返回类型
- [ ] 确保 Controller 正确处理 MyBatis-Plus Page 类型
- [ ] 测试 Controller 端点，确保分页数据正确返回

## 功能测试

### 分页功能测试
- [ ] 测试不同页码的情况
- [ ] 测试不同每页大小的情况
- [ ] 测试总数计算是否正确
- [ ] 测试排序功能是否正常
- [ ] 测试系统无异常抛出

## 代码质量

### 代码审查
- [ ] 代码风格一致
- [ ] 无冗余代码
- [ ] 所有修改点都已处理
- [ ] 注释清晰完整

## 验收标准确认
- [ ] AC-1: Service 层返回类型统一
- [ ] AC-2: 消除类型转换代码
- [ ] AC-3: Controller 调用适配
- [ ] AC-4: 分页功能正常
