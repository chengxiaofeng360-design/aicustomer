#!/bin/bash
# Dify集成快速启动脚本
# 使用说明：
# 1. 在Dify中获取API Key
# 2. 执行: ./quick-start-dify.sh YOUR_API_KEY

set -e

API_KEY=$1

if [ -z "$API_KEY" ]; then
    echo "❌ 错误: 请提供Dify API Key"
    echo "用法: ./quick-start-dify.sh app-xxxxxxxxxxxxx"
    exit 1
fi

echo "🚀 开始Dify集成..."
echo ""

# 1. 备份当前配置
echo "📦 备份配置文件..."
cp src/main/resources/application.yml src/main/resources/application.yml.backup.$(date +%Y%m%d_%H%M%S)
echo "✓ 备份完成"

# 2. 更新配置
echo ""
echo "⚙️  更新配置..."

# 使用sed更新API Key
sed -i.tmp "s/YOUR_DIFY_API_KEY/$API_KEY/g" application.yml.dify-template
cp application.yml.dify-template src/main/resources/application.yml
rm application.yml.dify-template.tmp

echo "✓ 配置已更新"

# 3. 停止现有服务
echo ""
echo "🛑 停止现有服务..."
if lsof -ti:8085 > /dev/null 2>&1; then
    kill $(lsof -ti:8085)
    echo "✓ 服务已停止"
else
    echo "ℹ️  服务未运行"
fi

# 4. 重新构建（可选）
echo ""
read -p "是否重新构建？(y/N): " rebuild
if [[ $rebuild =~ ^[Yy]$ ]]; then
    echo "🔨 重新构建..."
    mvn clean package -DskipTests
    echo "✓ 构建完成"
fi

# 5. 启动服务
echo ""
echo "🚀 启动服务..."
nohup java -jar target/ai-customer-management-1.0.0.jar \
  --server.port=8085 \
  > logs/app-dify.log 2>&1 &

echo "✓ 服务启动中..."

# 6. 等待启动
echo ""
echo "⏳ 等待服务启动（30秒）..."
sleep 30

# 7. 健康检查
echo ""
echo "🏥 健康检查..."
if curl -s http://localhost:8085/actuator/health | grep -q "UP"; then
    echo "✅ 服务健康！"
else
    echo "⚠️  服务可能未完全启动，请查看日志: tail -f logs/app-dify.log"
fi

# 8. 测试Dify集成
echo ""
echo "🧪 测试Dify集成..."
echo "查看日志确认Dify初始化..."
grep -i "dify" logs/app-dify.log | tail -5

echo ""
echo "🎉 Dify集成完成！"
echo ""
echo "📊 下一步:"
echo "1. 查看日志: tail -f logs/app-dify.log"
echo "2. 测试API: curl -X POST http://localhost:8085/api/ai-chat/send ..."
echo "3. 检查数据库: engine_type应为'dify'"
echo ""
echo "💡 回滚命令:"
echo "   cp src/main/resources/application.yml.backup.* src/main/resources/application.yml"
echo "   kill \$(lsof -ti:8085) && java -jar target/ai-customer-management-1.0.0.jar --server.port=8085 &"
