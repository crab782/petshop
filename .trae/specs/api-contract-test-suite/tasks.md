# Tasks

- [x] Task 1: 扫描前端API调用并建立档案
  - [x] SubTask 1.1: 扫描 `petshop-vue/src/api/` 下所有TS文件，提取API路径、方法、参数、响应类型
  - [x] SubTask 1.2: 扫描前端页面中的直接axios调用（如Login.vue、Register.vue）
  - [x] SubTask 1.3: 生成前端API调用档案文档

- [x] Task 2: 扫描后端API定义并建立档案
  - [x] SubTask 2.1: 扫描 `AuthApiController.java`，提取所有接口定义
  - [x] SubTask 2.2: 扫描 `UserApiController.java`，提取所有接口定义
  - [x] SubTask 2.3: 扫描 `MerchantController.java` 和 `PublicApiController.java`，提取所有接口定义
  - [x] SubTask 2.4: 扫描 `AdminApiController.java`，提取所有接口定义
  - [x] SubTask 2.5: 生成后端API定义档案文档

- [x] Task 3: 前后端API比对分析
  - [x] SubTask 3.1: 比对认证相关API（登录、注册、密码重置）
  - [x] SubTask 3.2: 比对用户端API（宠物、预约、订单、购物车等）
  - [x] SubTask 3.3: 比对商家端API（服务管理、商品管理、订单处理等）
  - [x] SubTask 3.4: 比对平台端API（用户管理、商家审核、系统设置等）
  - [x] SubTask 3.5: 生成比对报告，标记所有不匹配项

- [x] Task 4: 修复发现的API不匹配问题
  - [x] SubTask 4.1: 修复路径不匹配的接口
  - [x] SubTask 4.2: 修复参数不匹配的接口
  - [x] SubTask 4.3: 修复响应结构不匹配的接口

- [x] Task 5: 开发自动化接口测试套件
  - [x] SubTask 5.1: 创建测试基础设施（BaseTestClass、测试配置、JWT工具）
  - [x] SubTask 5.2: 编写认证API测试（AuthApiControllerTest）
  - [x] SubTask 5.3: 编写用户端API测试（UserApiControllerTest）
  - [x] SubTask 5.4: 编写商家端API测试（MerchantControllerTest）
  - [x] SubTask 5.5: 编写平台端API测试（AdminApiControllerTest）

- [x] Task 6: 生成API接口文档
  - [x] SubTask 6.1: 生成完整的API接口Markdown文档
  - [x] SubTask 6.2: 生成比对报告文档

# Task Dependencies
- Task 3 依赖 Task 1 和 Task 2（需要前后端档案才能比对）
- Task 4 依赖 Task 3（需要比对结果才能修复）
- Task 5 依赖 Task 4（修复后再编写测试）
- Task 6 依赖 Task 3 和 Task 5（需要比对结果和测试结果）
