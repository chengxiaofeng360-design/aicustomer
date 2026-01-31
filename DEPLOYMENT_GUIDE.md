# Dify集成最终部署指南

## 🎯 当前状态

✅ **代码开发**: 100%完成  
✅ **Maven构建**: 成功  
✅ **JAR包生成**: `target/ai-customer-management-1.0.0.jar`  
⏳ **数据库迁移**: 待执行  
⏳ **服务部署**: 待执行  

---

## 🚀 快速部署（推荐）

### 一键部署脚本

```bash
cd /Users/zuozuo/Downloads/cxf/aicustomer
./deploy-dify.sh
```

**脚本功能**:
1. ✅ 自动执行数据库迁移
2. ✅ 自动停止现有服务
3. ✅ 自动启动新服务（Legacy模式）
4. ✅ 自动健康检查
5. ✅ 验证部署结果

---

## 📝 手动部署（备选）

### 步骤1：数据库迁移

**选项A：使用MySQL命令行**
```bash
mysql -h 106.12.33.232 -u root -pBjzk60166200 zqgl < \
  src/main/resources/sql/migrations/20260131_add_engine_type.sql
```

**选项B：手动执行SQL**
```sql
-- 1. 添加字段
ALTER TABLE ai_chat 
ADD COLUMN engine_type VARCHAR(20) DEFAULT 'legacy' 
COMMENT 'AI引擎类型: legacy/dify';

-- 2. 更新历史数据
UPDATE ai_chat 
SET engine_type = 'legacy' 
WHERE engine_type IS NULL;

-- 3. 创建索引
CREATE INDEX idx_session_engine 
ON ai_chat(session_id, engine_type);

-- 4. 验证
SHOW COLUMNS FROM ai_chat LIKE 'engine_type';
SELECT COUNT(*) FROM ai_chat WHERE engine_type = 'legacy';
```

### 步骤2：停止现有服务

```bash
# 查找运行中的服务
ps aux | grep ai-customer-management

# 停止服务（替换<PID>为实际进程ID）
kill <PID>
```

### 步骤3：启动新服务

**默认Legacy模式（推荐）**:
```bash
cd /Users/zuozuo/Downloads/cxf/aicustomer

nohup java -jar target/ai-customer-management-1.0.0.jar \
  --server.port=8085 \
  --ai.engine=legacy \
  --dify.enabled=false \
  > logs/app.log 2>&1 &
```

**Dify测试模式**:
```bash
export DIFY_API_KEY="app-xxxxxxxxxxxxx"

nohup java -jar target/ai-customer-management-1.0.0.jar \
  --server.port=8085 \
  --ai.engine=dify \
  --dify.enabled=true \
  --dify.api.url=http://your-dify-server/v1 \
  > logs/app.log 2>&1 &
```

### 步骤4：验证部署

```bash
# 健康检查
curl http://localhost:8085/actuator/health

# 查看日志
tail -f logs/app.log

# 测试AI聊天（需要登录token）
curl -X POST http://localhost:8085/api/ai-chat/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"sessionId":"test","message":"你好"}'
```

---

## 🔧 配置说明

### 当前默认配置（零影响）

```yaml
# application.yml
ai:
  engine: legacy  # 使用现有方案

dify:
  enabled: false  # Dify完全关闭
  api:
    url: http://localhost/v1
    key: ${DIFY_API_KEY:}
  timeout: 30000
  max-retries: 2
  health-check-interval: 60000
  traffic-percentage: 0  # 流量百分比：0%
```

### 启用Dify的配置

**方式1：修改application.yml**
```yaml
ai:
  engine: dify  # 切换到Dify

dify:
  enabled: true  # 启用Dify
  api:
    url: http://your-dify-server/v1
    key: app-xxxxxxxxxxxxx
  traffic-percentage: 100  # 100%流量使用Dify
```

**方式2：环境变量**
```bash
export AI_ENGINE=dify
export DIFY_ENABLED=true
export DIFY_API_URL=http://your-dify-server/v1
export DIFY_API_KEY=app-xxxxxxxxxxxxx
```

**方式3：启动参数**
```bash
java -jar ai-customer-management-1.0.0.jar \
  --ai.engine=dify \
  --dify.enabled=true \
  --dify.api.url=http://your-dify-server/v1 \
  --dify.api.key=app-xxxxxxxxxxxxx
```

---

## 📊 灰度发布策略

### 第1天：10%流量
```yaml
dify:
  enabled: true
  traffic-percentage: 10  # 10%用户使用Dify
```

### 第3天：20%流量
```yaml
dify:
  traffic-percentage: 20
```

### 第5天：50%流量
```yaml
dify:
  traffic-percentage: 50
```

### 第7天：100%流量
```yaml
ai:
  engine: dify  # 全量切换
dify:
  traffic-percentage: 100
```

---

## ⚠️ 回滚方案（1分钟回滚）

### 紧急回滚到Legacy

**方式1：重启切换引擎**
```bash
# 停止服务
kill <PID>

# 重启为Legacy模式
java -jar target/ai-customer-management-1.0.0.jar \
  --ai.engine=legacy \
  --dify.enabled=false &
```

**方式2：热配置修改**
```yaml
# 修改application.yml
ai:
  engine: legacy

dify:
  enabled: false
```
然后重启服务。

**方式3：降级流量**
```yaml
# 先降低Dify流量
dify:
  traffic-percentage: 0  # 0%流量，全部使用Legacy
```

---

## 📈 监控指标

### 关键日志

**Legacy引擎日志**:
```
🔧 使用Legacy引擎处理请求
```

**Dify引擎日志**:
```
✨ 使用Dify引擎处理请求
【Dify适配器】处理请求: xxx
✅ Dify响应成功
```

**降级日志**:
```
⚠️ Dify不可用，降级到Legacy引擎
```

**熔断器日志**:
```
⚡ Dify熔断器状态变化: CLOSED -> OPEN
❌ Dify调用失败（已重试2次）
```

### 数据库验证

```sql
-- 查看引擎使用分布
SELECT 
    engine_type,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM ai_chat), 2) as percentage
FROM ai_chat
GROUP BY engine_type;

-- 最近10条消息的引擎
SELECT 
    session_id,
    engine_type,
    message_type,
    LEFT(content, 50) as content_preview,
    create_time
FROM ai_chat
ORDER BY create_time DESC
LIMIT 10;
```

---

## 🛠️ 故障排查

### 问题1：服务启动失败

**检查**:
```bash
# 查看日志
tail -100 logs/app.log

# 检查端口占用
lsof -i:8085

# 检查Java进程
ps aux | grep java
```

### 问题2：Dify调用失败

**检查**:
```bash
# 查看Dify配置
grep -A 10 "dify:" src/main/resources/application.yml

# 测试Dify连接
curl -X POST http://your-dify-server/v1/chat-messages \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"query":"hello","user":"test"}'
```

### 问题3：数据库字段缺失

**检查**:
```sql
-- 查看ai_chat表结构
DESCRIBE ai_chat;

-- 手动添加字段
ALTER TABLE ai_chat 
ADD COLUMN engine_type VARCHAR(20) DEFAULT 'legacy';
```

---

## ✅ 部署检查清单

部署前:
- [ ] 确认JAR包已构建: `target/ai-customer-management-1.0.0.jar`
- [ ] 确认数据库连接正常
- [ ] 备份当前数据库（可选）
- [ ] 记录当前运行的服务PID

部署中:
- [ ] 执行数据库迁移脚本
- [ ] 验证engine_type字段已添加
- [ ] 停止现有服务
- [ ] 启动新服务（Legacy模式）
- [ ] 等待10秒启动

部署后:
- [ ] 健康检查通过
- [ ] 查看日志无ERROR
- [ ] 测试发送AI消息
- [ ] 确认日志显示"Legacy引擎"
- [ ] 检查数据库engine_type正确写入

---

## 🎯 下一步计划

### 当前（已完成）:
✅ 代码开发100%完成  
✅ Maven构建成功  
✅ 部署脚本ready  

### 即将执行:
⏭️ 运行`./deploy-dify.sh`自动部署  
⏭️ 验证Legacy引擎正常工作  

### 未来（可选）:
🔜 部署Dify平台  
🔜 创建Dify Agent应用  
🔜 启用Dify引擎测试  
🔜 灰度发布（10% → 100%）  

---

## 📞 支持

如有问题，请检查:
1. **日志文件**: `logs/app.log`
2. **配置文件**: `src/main/resources/application.yml`
3. **数据库连接**: 确保能连接到106.12.33.232
4. **端口占用**: 确保8085端口未被占用

**紧急回滚**: 只需将`ai.engine`改回`legacy`并重启！
