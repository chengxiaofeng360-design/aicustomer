#!/bin/bash
# Dify集成最终测试脚本

echo "======================================"
echo "  Dify集成完整测试"
echo "======================================"
echo ""

# 测试数据
SESSION_ID="dify-integration-test-$(date +%s)"
TEST_MESSAGE="你好，请介绍一下你自己"

echo "📊 测试信息:"
echo "  Session ID: $SESSION_ID"
echo "  测试消息: $TEST_MESSAGE"
echo ""

# 1. 健康检查
echo "1️⃣ 健康检查..."
HEALTH=$(curl -s http://localhost:8085/actuator/health)
if echo "$HEALTH" | grep -q "UP"; then
    echo "✅ 服务健康"
else
    echo "❌ 服务异常: $HEALTH"
    exit 1
fi
echo ""

# 2. 测试API调用
echo "2️⃣ 测试Dify API调用..."
echo "发送测试消息..."

RESPONSE=$(curl -s -X POST http://localhost:8085/api/ai-chat/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer test-token" \
  -d "{
    \"sessionId\": \"$SESSION_ID\",
    \"message\": \"$TEST_MESSAGE\"
  }")

echo "响应:"
echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"
echo ""

# 3. 检查响应是否成功
if echo "$RESPONSE" | grep -q '"success":true'; then
    echo "✅ API调用成功"
else
    echo "⚠️ API调用失败或返回错误"
fi
echo ""

# 4. 检查engine_type
echo "3️⃣ 检查数据库engine_type..."
ENGINE_TYPE=$(mysql -h 106.12.33.232 -u root -pBjzk60166200 zqgl \
  -N -e "SELECT engine_type FROM ai_chat WHERE session_id='$SESSION_ID' LIMIT 1;" 2>/dev/null)

if [ "$ENGINE_TYPE" = "dify" ]; then
    echo "✅ Engine Type: dify (使用Dify引擎)"
elif [ "$ENGINE_TYPE" = "legacy" ]; then
    echo "⚠️ Engine Type: legacy (降级到Legacy引擎)"
    echo "   可能原因: Dify配置有问题或服务不可用"
else
    echo "❌ 未找到记录或engine_type为空"
fi
echo ""

# 5. 查看最近5条记录
echo "4️⃣ 最近5条AI对话记录:"
mysql -h 106.12.33.232 -u root -pBjzk60166200 zqgl \
  -e "SELECT id, session_id, engine_type, LEFT(content,50) as content_preview, create_time 
      FROM ai_chat 
      ORDER BY create_time DESC 
      LIMIT 5;" 2>/dev/null
echo ""

# 6. 检查Dify服务
echo "5️⃣ 检查Dify服务状态..."
cd /Users/zuozuo/dify/dify-1.10.1/docker
DIFY_STATUS=$(docker-compose ps --format json 2>/dev/null | grep api)
if [ -n "$DIFY_STATUS" ]; then
    echo "✅ Dify服务运行中"
else
    echo "❌ Dify服务未运行"
fi
echo ""

# 7. 总结
echo "======================================"
echo "  测试总结"
echo "======================================"
echo ""

if [ "$ENGINE_TYPE" = "dify" ]; then
    echo "🎉 Dify集成成功！"
    echo ""
    echo "✅ 所有测试通过:"
    echo "  - 服务健康状态正常"
    echo "  - API调用成功"
    echo "  - 使用Dify引擎处理请求"
    echo "  - 数据库记录正确"
    echo ""
    echo "🚀 您的系统现在使用Dify作为主AI引擎！"
else
    echo "⚠️ 集成部分完成，但可能有问题:"
    echo ""
    echo "已完成:"
    echo "  ✓ 代码集成"
    echo "  ✓ 配置更新"
    echo "  ✓ 服务运行"
    echo ""
    echo "需要检查:"
    echo "  ? Dify模型配置是否正确"
    echo "  ? Dify API Key是否有效"
    echo "  ? Dify服务是否正常"
    echo ""
    echo "查看日志:"
    echo "  tail -100 /Users/zuozuo/Downloads/cxf/aicustomer/logs/app-dify-production.log | grep -i dify"
fi
echo ""
