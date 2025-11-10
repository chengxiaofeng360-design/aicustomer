#!/bin/bash
# 客户档案管理增删改查测试脚本

BASE_URL="http://localhost:8080/api/customer"

echo "=========================================="
echo "客户档案管理增删改查测试"
echo "=========================================="
echo ""

# 1. 插入测试数据
echo "1. 测试新增功能 - 插入测试数据"
echo "----------------------------------------"
INSERT_RESPONSE=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "测试客户公司",
    "customerType": 2,
    "phone": "13800138000",
    "email": "test@example.com",
    "address": "测试地址123号",
    "customerLevel": 2,
    "status": 1,
    "source": 1,
    "remark": "这是一条测试数据"
  }')

echo "$INSERT_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$INSERT_RESPONSE"
echo ""

# 提取客户ID（如果插入成功）
CUSTOMER_ID=$(echo "$INSERT_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d: -f2)
CUSTOMER_CODE="CUST$(date +%s)"

if [ -z "$CUSTOMER_ID" ]; then
    echo "⚠️  插入失败，无法继续测试。请检查："
    echo "   1. 数据库是否已创建（ai_customer_db）"
    echo "   2. 表结构是否已初始化（执行schema.sql）"
    echo "   3. 应用日志中的错误信息"
    exit 1
fi

echo "✅ 插入成功，客户ID: $CUSTOMER_ID"
echo ""

# 2. 查询测试
echo "2. 测试查询功能"
echo "----------------------------------------"

# 2.1 分页查询
echo "2.1 分页查询"
PAGE_RESPONSE=$(curl -s "$BASE_URL/page?pageNum=1&pageSize=10")
echo "$PAGE_RESPONSE" | python3 -m json.tool 2>/dev/null | head -20 || echo "$PAGE_RESPONSE"
echo ""

# 2.2 根据ID查询
echo "2.2 根据ID查询 (ID: $CUSTOMER_ID)"
GET_RESPONSE=$(curl -s "$BASE_URL/$CUSTOMER_ID")
echo "$GET_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$GET_RESPONSE"
echo ""

# 3. 更新测试
echo "3. 测试更新功能"
echo "----------------------------------------"
UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d "{
    \"id\": $CUSTOMER_ID,
    \"customerName\": \"更新后的测试客户公司\",
    \"phone\": \"13900139000\",
    \"remark\": \"这是更新后的测试数据\"
  }")
echo "$UPDATE_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$UPDATE_RESPONSE"
echo ""

# 验证更新
echo "3.1 验证更新结果"
VERIFY_RESPONSE=$(curl -s "$BASE_URL/$CUSTOMER_ID")
echo "$VERIFY_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$VERIFY_RESPONSE"
echo ""

# 4. 删除测试
echo "4. 测试删除功能（逻辑删除）"
echo "----------------------------------------"
DELETE_RESPONSE=$(curl -s -X DELETE "$BASE_URL/$CUSTOMER_ID")
echo "$DELETE_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$DELETE_RESPONSE"
echo ""

# 验证删除（应该查询不到）
echo "4.1 验证删除结果（应该查询不到）"
AFTER_DELETE=$(curl -s "$BASE_URL/$CUSTOMER_ID")
echo "$AFTER_DELETE" | python3 -m json.tool 2>/dev/null || echo "$AFTER_DELETE"
echo ""

echo "=========================================="
echo "测试完成"
echo "=========================================="

