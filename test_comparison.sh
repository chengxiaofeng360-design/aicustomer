#!/bin/bash
# Dify vs Legacy 快速对比测试脚本

echo "======================================"
echo "  Dify vs Legacy 效果对比测试"
echo "======================================"
echo ""

# 配置
API_URL="http://localhost:8085/api/ai-chat/send"
TOKEN="YOUR_AUTH_TOKEN"  # 替换为实际Token

# 测试问题
TEST_QUESTIONS=(
    "你好，介绍一下你自己"
    "查询最近一周的VIP客户"
    "如何提高客户满意度？"
    "分析我们的客户流失原因"
)

echo "📝 测试问题列表:"
for i in "${!TEST_QUESTIONS[@]}"; do
    echo "  $((i+1)). ${TEST_QUESTIONS[$i]}"
done
echo ""

# 测试Legacy引擎
echo "======================================"
echo "  测试1: Legacy引擎"
echo "======================================"
echo ""

for i in "${!TEST_QUESTIONS[@]}"; do
    question="${TEST_QUESTIONS[$i]}"
    echo "问题 $((i+1)): $question"
    echo "引擎: Legacy (DeepSeek)"
    echo ""
    
    start_time=$(date +%s%3N)
    
    response=$(curl -s -X POST "$API_URL" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d "{\"sessionId\":\"test-legacy-$i\",\"message\":\"$question\"}")
    
    end_time=$(date +%s%3N)
    duration=$((end_time - start_time))
    
    echo "响应时间: ${duration}ms"
    echo "回答: $response"
    echo ""
    echo "----------------------------------------"
    echo ""
done

echo ""
echo "======================================"
echo "  测试完成！"
echo "======================================"
echo ""
echo "下一步："
echo "1. 访问 http://localhost 创建Dify应用"
echo "2. 获取API Key并配置到application.yml"
echo "3. 修改 ai.engine=dify 并重启服务"
echo "4. 再次运行此脚本，对比Dify的效果"
echo ""
echo "评估维度："
echo "- 响应时间（越快越好）"
echo "- 回答准确性（是否符合预期）"
echo "- 知识库召回（是否找到相关信息）"
echo "- 工具调用（是否正确触发查询）"
echo ""
