#!/bin/bash

echo "🚀 启动 PaddleOCR 服务..."

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 未运行，请先启动 Docker"
    exit 1
fi

# 构建并启动容器
docker-compose up -d --build

# 等待服务就绪
echo "⏳ 等待服务启动..."
sleep 5

# 检查服务状态
if curl -s http://localhost:5000/health > /dev/null; then
    echo "✅ PaddleOCR 服务启动成功！"
    echo "📍 服务地址: http://localhost:5000"
    echo ""
    echo "查看日志: docker-compose logs -f paddleocr"
    echo "停止服务: ./stop_ocr.sh"
else
    echo "⚠️  服务可能未完全启动，请稍后或查看日志"
    echo "查看日志: docker-compose logs paddleocr"
fi
