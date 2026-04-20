# 分库分表方案设计

## 1. 数据模型分析

### 1.1 核心实体
- **User**: 用户信息表
- **Merchant**: 商家信息表
- **Product**: 产品信息表
- **ProductOrder**: 订单信息表
- **Appointment**: 预约信息表
- **Address**: 地址表
- **Cart**: 购物车表
- **Favorite**: 收藏表
- **Review**: 评价表

### 1.2 数据增长趋势
- **高频增长表**: ProductOrder、Appointment、Cart、Review
- **中频增长表**: User、Merchant、Product、Address
- **低频增长表**: Category、SystemConfig、SystemSettings

### 1.3 访问模式
- **用户维度**: 基于user_id的查询（订单、预约、购物车、收藏、评价）
- **商家维度**: 基于merchant_id的查询（产品、订单、预约、评价）
- **时间维度**: 基于创建时间的查询（订单、预约、活动）

## 2. 分库分表策略

### 2.1 分库策略
- **用户库**: 存储用户相关数据（User、Address、Cart、Favorite）
- **商家库**: 存储商家相关数据（Merchant、Product、MerchantSettings）
- **订单库**: 存储订单和预约相关数据（ProductOrder、ProductOrderItem、Appointment）
- **公共库**: 存储公共数据（Category、SystemConfig、SystemSettings、Announcement）

### 2.2 分表策略

#### 2.2.1 订单表（ProductOrder）
- **分片键**: user_id
- **分片规则**: user_id % 8（8个分片）
- **表名**: product_order_0 ~ product_order_7

#### 2.2.2 预约表（Appointment）
- **分片键**: user_id
- **分片规则**: user_id % 8（8个分片）
- **表名**: appointment_0 ~ appointment_7

#### 2.2.3 产品表（Product）
- **分片键**: merchant_id
- **分片规则**: merchant_id % 4（4个分片）
- **表名**: product_0 ~ product_3

#### 2.2.4 购物车表（Cart）
- **分片键**: user_id
- **分片规则**: user_id % 8（8个分片）
- **表名**: cart_0 ~ cart_7

#### 2.2.5 评价表（Review）
- **分片键**: merchant_id
- **分片规则**: merchant_id % 4（4个分片）
- **表名**: review_0 ~ review_3

## 3. 路由规则

### 3.1 单库单表路由
- **用户表**: 基于user_id路由到用户库
- **商家表**: 基于merchant_id路由到商家库
- **公共表**: 直接访问公共库

### 3.2 跨库查询处理
- **用户订单查询**: 根据user_id路由到订单库对应分片
- **商家产品查询**: 根据merchant_id路由到商家库对应分片
- **商家订单查询**: 需要跨库查询，通过merchant_id在订单库的所有分片上执行查询

### 3.3 联合查询优化
- **使用二级索引**: 在分片键上建立二级索引
- **减少跨库联合查询**: 通过应用层缓存和数据冗余减少跨库查询
- **异步处理**: 对于复杂查询，使用异步处理机制

## 4. 扩容机制

### 4.1 水平扩容
- **分片数量扩展**: 当单个分片数据量达到阈值时，扩展分片数量
- **扩容策略**: 
  1. 备份现有数据
  2. 创建新的分片表
  3. 数据迁移（按分片键重新分配）
  4. 更新路由规则
  5. 验证数据一致性

### 4.2 垂直扩容
- **数据库服务器升级**: 增加内存、CPU等硬件资源
- **存储优化**: 使用SSD存储，优化数据库配置

### 4.3 监控与预警
- **数据量监控**: 定期监控各分片数据量
- **性能监控**: 监控查询性能，及时发现瓶颈
- **自动预警**: 当数据量达到阈值时自动预警

## 5. 技术实现

### 5.1 分片中间件选择
- **MyBatis-Plus 分片插件**: 与现有MyBatis-Plus集成
- **ShardingSphere**: 提供完整的分库分表解决方案

### 5.2 实现步骤
1. **配置分片规则**
2. **修改数据访问层代码**
3. **测试分片功能**
4. **数据迁移**
5. **线上部署**

### 5.3 代码示例
```java
// 分片规则配置
@Configuration
public class ShardingConfig {
    @Bean
    public ShardingRuleConfiguration shardingRuleConfiguration() {
        // 配置分片规则
        // ...
        return shardingRuleConfig;
    }
}

// Mapper接口
@Mapper
public interface ProductOrderMapper extends BaseMapper<ProductOrder> {
    // 自定义分片查询
    @Select("SELECT * FROM product_order WHERE user_id = #{userId}")
    List<ProductOrder> selectByUserId(@Param("userId") Integer userId);
}
```

## 6. 风险评估与应对策略

### 6.1 风险评估
- **数据一致性风险**: 跨库事务可能导致数据不一致
- **性能风险**: 跨库查询可能影响性能
- **维护风险**: 分库分表增加了系统复杂度

### 6.2 应对策略
- **使用分布式事务**: 保证跨库事务一致性
- **缓存策略**: 缓存热点数据，减少数据库访问
- **监控与告警**: 实时监控系统状态，及时发现问题
- **备份与恢复**: 定期备份数据，确保数据安全

## 7. 总结

本方案基于项目的实际数据模型和访问模式，设计了合理的分库分表策略，包括分片键选择、路由规则和扩容机制。通过实施本方案，可以有效解决数据量增长带来的性能问题，提高系统的可扩展性和可靠性。

同时，本方案考虑了系统的实际情况，避免了过度设计，确保了方案的可行性和可维护性。在实施过程中，需要根据实际情况进行调整和优化，以达到最佳效果。