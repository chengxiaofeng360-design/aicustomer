#!/bin/bash

# 使用Spring Boot应用的REST API批量导入FAQ数据

echo "开始导入FAQ数据..."

curl -X POST http://localhost:8085/api/faq/batch \
  -H "Content-Type: application/json" \
  -d @faq_data.json \
  -v

echo ""
echo "导入完成！"
