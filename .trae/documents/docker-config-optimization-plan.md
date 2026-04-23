# Docker配置优化计划

## 项目分析

### Redis使用情况
后端项目**确实使用了Redis**：
- `RedisLockUtil.java` - 分布式锁工具类
- `ProductServiceImpl.java` - 使用Redis锁处理库存扣减
- `RedisLockController.java` - Redis锁控制器

**结论**：Redis是必需的中间件，不能移除。

### 当前问题
1. Windows本地MySQL已监听3306端口，与Docker容器冲突
2. 需要移除MySQL和Redis的端口映射，仅保留容器间通信
3. 需要验证服务启动后是否正常

## 实施计划

### 1. 修改docker-compose.yml
- **移除MySQL端口映射**：删除 `ports: - "3306:3306"`
- **移除Redis端口映射**：删除 `ports: - "6379:6379"`
- **保留volume持久化**：mysql-data、redis-data
- **保留network通信**：petshop-network
- **仅暴露应用端口**：8080

### 2. 启动容器
- 设置代理环境变量
- 执行 `docker-compose up -d --build`

### 3. 验证服务
- 使用 `docker logs petshop-app` 查看应用日志
- 使用 `curl http://localhost:8080/swagger-ui/index.html` 验证Swagger UI
- 检查容器状态 `docker ps`

## 修改后的docker-compose.yml结构

```yaml
services:
  mysql:
    # 移除 ports 映射
    volumes:
      - mysql-data:/var/lib/mysql
      - ./petshop_423.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - petshop-network

  redis:
    # 移除 ports 映射
    volumes:
      - redis-data:/data
    networks:
      - petshop-network

  app:
    ports:
      - "8080:8080"  # 仅暴露应用端口
    networks:
      - petshop-network
    depends_on:
      - mysql
      - redis
```

## 预期效果

- MySQL和Redis仅在Docker内部网络通信
- 应用通过容器名称访问MySQL和Redis
- 外部仅能访问8080端口的应用服务
- 数据持久化到volume
- 服务正常运行

## 验证步骤

1. `docker ps` - 查看容器状态
2. `docker logs petshop-app` - 查看应用日志
3. `curl http://localhost:8080/swagger-ui/index.html` - 验证API文档
4. `docker exec petshop-app curl http://localhost:8080/actuator/health` - 健康检查
