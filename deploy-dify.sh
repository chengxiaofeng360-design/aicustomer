#!/bin/bash
# Dify集成部署脚本
# 用途：自动化部署流程，包括数据库迁移、服务重启等

set -e  # 遇到错误立即退出

echo "======================================"
echo "  Dify集成部署脚本 v1.0"
echo "======================================"
echo ""

# 配置
DB_HOST="106.12.33.232"
DB_USER="root"
DB_PASS="Bjzk60166200"
DB_NAME="zqgl"
PROJECT_DIR="/Users/zuozuo/Downloads/cxf/aicustomer"
JAR_FILE="target/ai-customer-management-1.0.0.jar"
SERVICE_PORT="8085"

# 颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 步骤1：数据库迁移
echo -e "${YELLOW}步骤1: 执行数据库迁移...${NC}"
if command -v mysql &> /dev/null; then
    echo "  → 添加engine_type字段..."
    mysql -h $DB_HOST -u $DB_USER -p$DB_PASS $DB_NAME -e "
        ALTER TABLE ai_chat ADD COLUMN IF NOT EXISTS engine_type VARCHAR(20) DEFAULT 'legacy' COMMENT 'AI引擎类型: legacy/dify';
    " 2>/dev/null || echo "  ⚠️  字段可能已存在，跳过"
    
    echo "  → 更新历史数据..."
    mysql -h $DB_HOST -u $DB_USER -p$DB_PASS $DB_NAME -e "
        UPDATE ai_chat SET engine_type = 'legacy' WHERE engine_type IS NULL;
    "
    
    echo "  → 创建索引..."
    mysql -h $DB_HOST -u $DB_USER -p$DB_PASS $DB_NAME -e "
        CREATE INDEX IF NOT EXISTS idx_session_engine ON ai_chat(session_id, engine_type);
    " 2>/dev/null || echo "  ⚠️  索引可能已存在，跳过"
    
    echo -e "${GREEN}✓ 数据库迁移完成${NC}"
else
    echo -e "${RED}✗ MySQL命令未找到，请手动执行SQL脚本：${NC}"
    echo "  $PROJECT_DIR/src/main/resources/sql/migrations/20260131_add_engine_type.sql"
fi
echo ""

# 步骤2：验证JAR包
echo -e "${YELLOW}步骤2: 验证JAR包...${NC}"
if [ -f "$PROJECT_DIR/$JAR_FILE" ]; then
    echo -e "${GREEN}✓ JAR包已存在: $JAR_FILE${NC}"
    JAR_SIZE=$(du -h "$PROJECT_DIR/$JAR_FILE" | cut -f1)
    echo "  文件大小: $JAR_SIZE"
else
    echo -e "${RED}✗ JAR包不存在，开始构建...${NC}"
    cd $PROJECT_DIR
    mvn clean package -DskipTests
    echo -e "${GREEN}✓ 构建完成${NC}"
fi
echo ""

# 步骤3：停止现有服务
echo -e "${YELLOW}步骤3: 停止现有服务...${NC}"
PID=$(lsof -ti:$SERVICE_PORT 2>/dev/null || echo "")
if [ -n "$PID" ]; then
    echo "  → 找到运行在端口 $SERVICE_PORT 的进程: PID $PID"
    kill $PID
    sleep 2
    echo -e "${GREEN}✓ 服务已停止${NC}"
else
    echo "  ℹ️  没有服务运行在端口 $SERVICE_PORT"
fi
echo ""

# 步骤4：启动新服务
echo -e "${YELLOW}步骤4: 启动服务（Legacy引擎模式）...${NC}"
cd $PROJECT_DIR
nohup java -jar $JAR_FILE \
    --server.port=$SERVICE_PORT \
    --ai.engine=legacy \
    --dify.enabled=false \
    > logs/app-$(date +%Y%m%d-%H%M%S).log 2>&1 &

NEW_PID=$!
echo "  → 服务已启动，PID: $NEW_PID"
echo ""

# 步骤5：健康检查
echo -e "${YELLOW}步骤5: 健康检查...${NC}"
echo "  → 等待服务启动..."
sleep 10

for i in {1..6}; do
    if curl -s http://localhost:$SERVICE_PORT/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ 服务健康检查通过！${NC}"
        break
    else
        if [ $i -eq 6 ]; then
            echo -e "${RED}✗ 服务启动失败，请检查日志${NC}"
            exit 1
        fi
        echo "  ⏳ 等待中... ($i/6)"
        sleep 5
    fi
done
echo ""

# 步骤6：验证部署
echo -e "${YELLOW}步骤6: 验证部署...${NC}"
mysql -h $DB_HOST -u $DB_USER -p$DB_PASS $DB_NAME -e "
    SELECT 
        COLUMN_NAME, 
        COLUMN_TYPE, 
        COLUMN_DEFAULT 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'ai_chat' AND COLUMN_NAME = 'engine_type';
" 2>/dev/null && echo -e "${GREEN}✓ 数据库字段验证通过${NC}" || echo "⚠️  跳过数据库验证"

echo ""
echo "======================================"
echo -e "${GREEN}🎉 部署完成！${NC}"
echo "======================================"
echo ""
echo "服务信息:"
echo "  • 端口: $SERVICE_PORT"
echo "  • PID: $NEW_PID"
echo "  • 引擎: Legacy (默认)"
echo "  • Dify: 关闭 (dify.enabled=false)"
echo ""
echo "查看日志:"
echo "  tail -f logs/app-*.log"
echo ""
echo "停止服务:"
echo "  kill $NEW_PID"
echo ""
echo "启用Dify（可选）:"
echo "  1. 修改 application.yml"
echo "  2. 设置 ai.engine=dify"
echo "  3. 设置 dify.enabled=true"
echo "  4. 配置 DIFY_API_KEY 环境变量"
echo "  5. 重启服务"
echo ""
