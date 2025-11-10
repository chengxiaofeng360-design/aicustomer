#!/bin/bash
# 插入测试数据脚本

BASE_URL="http://localhost:8080/api/customer"

echo "=========================================="
echo "插入测试客户数据"
echo "=========================================="
echo ""

# 测试数据1
echo "1. 插入测试客户1..."
RESPONSE1=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "测试客户公司A",
    "customerType": 2,
    "phone": "13800138001",
    "email": "testa@example.com",
    "address": "北京市朝阳区测试路123号",
    "customerLevel": 2,
    "status": 1,
    "source": 1,
    "remark": "这是一条测试数据A"
  }')

echo "$RESPONSE1" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE1"
echo ""

# 测试数据2
echo "2. 插入测试客户2..."
RESPONSE2=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "测试客户公司B",
    "customerType": 2,
    "phone": "13800138002",
    "email": "testb@example.com",
    "address": "上海市浦东新区测试街456号",
    "customerLevel": 3,
    "status": 1,
    "source": 2,
    "remark": "这是一条测试数据B"
  }')

echo "$RESPONSE2" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE2"
echo ""

# 测试数据3
echo "3. 插入测试客户3（个人客户）..."
RESPONSE3=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "张三",
    "customerType": 1,
    "phone": "13800138003",
    "email": "zhangsan@example.com",
    "address": "深圳市南山区测试小区789号",
    "customerLevel": 1,
    "status": 1,
    "source": 1,
    "remark": "个人客户测试数据"
  }')

echo "$RESPONSE3" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE3"
echo ""

# 查询验证
echo "4. 查询插入的数据..."
QUERY_RESPONSE=$(curl -s "$BASE_URL/page?pageNum=1&pageSize=10")
echo "$QUERY_RESPONSE" | python3 -m json.tool 2>/dev/null | head -50 || echo "$QUERY_RESPONSE"
echo ""

echo "=========================================="
echo "数据插入完成"
echo "=========================================="
echo ""
echo "请访问 http://localhost:8080/customers.html 查看客户列表"

